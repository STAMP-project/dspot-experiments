/**
 * Copyright 2016 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc.grpclb;


import BackoffPolicy.Provider;
import ClientStreamTracer.StreamInfo;
import FakeClock.ScheduledTask;
import FakeClock.TaskFilter;
import GrpcAttributes.ATTR_LB_PROVIDED_BACKEND;
import GrpclbConstants.TOKEN_METADATA_KEY;
import GrpclbState.FALLBACK_TIMEOUT_MS;
import GrpclbState.FallbackModeTask;
import GrpclbState.LbRpcRetryTask;
import GrpclbState.LoadReportingTask;
import LoadBalancer.ATTR_LOAD_BALANCING_CONFIG;
import LoadBalancerGrpc.LoadBalancerImplBase;
import Mode.PICK_FIRST;
import Mode.ROUND_ROBIN;
import Status.CANCELLED;
import Status.DEADLINE_EXCEEDED;
import Status.NOT_FOUND;
import Status.OK;
import Status.UNAVAILABLE;
import com.google.common.collect.Iterables;
import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.ChannelLogger;
import io.grpc.ChannelLogger.ChannelLogLevel;
import io.grpc.ClientStreamTracer;
import io.grpc.ConnectivityState;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer.Helper;
import io.grpc.LoadBalancer.PickResult;
import io.grpc.LoadBalancer.PickSubchannelArgs;
import io.grpc.LoadBalancer.Subchannel;
import io.grpc.LoadBalancer.SubchannelPicker;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.SynchronizationContext;
import io.grpc.grpclb.GrpclbState.BackendEntry;
import io.grpc.grpclb.GrpclbState.DropEntry;
import io.grpc.grpclb.GrpclbState.IdleSubchannelEntry;
import io.grpc.grpclb.GrpclbState.Mode;
import io.grpc.grpclb.GrpclbState.RoundRobinEntry;
import io.grpc.grpclb.GrpclbState.RoundRobinPicker;
import io.grpc.internal.BackoffPolicy;
import io.grpc.internal.FakeClock;
import io.grpc.lb.v1.ClientStats;
import io.grpc.lb.v1.ClientStatsPerToken;
import io.grpc.lb.v1.InitialLoadBalanceRequest;
import io.grpc.lb.v1.LoadBalanceRequest;
import io.grpc.lb.v1.LoadBalanceResponse;
import io.grpc.lb.v1.Server;
import io.grpc.stub.StreamObserver;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link GrpclbLoadBalancer}.
 */
@RunWith(JUnit4.class)
public class GrpclbLoadBalancerTest {
    private static final String SERVICE_AUTHORITY = "api.google.com";

    // The tasks are wrapped by SynchronizationContext, so we can't compare the types
    // directly.
    private static final TaskFilter LOAD_REPORTING_TASK_FILTER = new FakeClock.TaskFilter() {
        @Override
        public boolean shouldAccept(Runnable command) {
            return command.toString().contains(LoadReportingTask.class.getSimpleName());
        }
    };

    private static final TaskFilter FALLBACK_MODE_TASK_FILTER = new FakeClock.TaskFilter() {
        @Override
        public boolean shouldAccept(Runnable command) {
            return command.toString().contains(FallbackModeTask.class.getSimpleName());
        }
    };

    private static final TaskFilter LB_RPC_RETRY_TASK_FILTER = new FakeClock.TaskFilter() {
        @Override
        public boolean shouldAccept(Runnable command) {
            return command.toString().contains(LbRpcRetryTask.class.getSimpleName());
        }
    };

    private static final Attributes LB_BACKEND_ATTRS = Attributes.newBuilder().set(ATTR_LB_PROVIDED_BACKEND, true).build();

    @Mock
    private Helper helper;

    @Mock
    private SubchannelPool subchannelPool;

    private final ArrayList<String> logs = new ArrayList<>();

    private final ChannelLogger channelLogger = new ChannelLogger() {
        @Override
        public void log(ChannelLogLevel level, String msg) {
            logs.add(((level + ": ") + msg));
        }

        @Override
        public void log(ChannelLogLevel level, String template, Object... args) {
            log(level, MessageFormat.format(template, args));
        }
    };

    private SubchannelPicker currentPicker;

    private LoadBalancerImplBase mockLbService;

    @Captor
    private ArgumentCaptor<StreamObserver<LoadBalanceResponse>> lbResponseObserverCaptor;

    private final FakeClock fakeClock = new FakeClock();

    private final LinkedList<StreamObserver<LoadBalanceRequest>> lbRequestObservers = new LinkedList<>();

    private final LinkedList<Subchannel> mockSubchannels = new LinkedList<>();

    private final LinkedList<ManagedChannel> fakeOobChannels = new LinkedList<>();

    private final ArrayList<Subchannel> pooledSubchannelTracker = new ArrayList<>();

    private final ArrayList<Subchannel> unpooledSubchannelTracker = new ArrayList<>();

    private final ArrayList<ManagedChannel> oobChannelTracker = new ArrayList<>();

    private final ArrayList<String> failingLbAuthorities = new ArrayList<>();

    private final SynchronizationContext syncContext = new SynchronizationContext(new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            throw new AssertionError(e);
        }
    });

    private static final StreamInfo STREAM_INFO = new ClientStreamTracer.StreamInfo() {
        @Override
        public Attributes getTransportAttrs() {
            return Attributes.EMPTY;
        }

        @Override
        public CallOptions getCallOptions() {
            return CallOptions.DEFAULT;
        }
    };

    private Server fakeLbServer;

    @Captor
    private ArgumentCaptor<SubchannelPicker> pickerCaptor;

    @Mock
    private Provider backoffPolicyProvider;

    @Mock
    private BackoffPolicy backoffPolicy1;

    @Mock
    private BackoffPolicy backoffPolicy2;

    private GrpclbLoadBalancer balancer;

    @Test
    public void roundRobinPickerNoDrop() {
        GrpclbClientLoadRecorder loadRecorder = new GrpclbClientLoadRecorder(fakeClock.getTimeProvider());
        Subchannel subchannel = Mockito.mock(Subchannel.class);
        BackendEntry b1 = new BackendEntry(subchannel, loadRecorder, "LBTOKEN0001");
        BackendEntry b2 = new BackendEntry(subchannel, loadRecorder, "LBTOKEN0002");
        List<BackendEntry> pickList = Arrays.asList(b1, b2);
        RoundRobinPicker picker = new RoundRobinPicker(Collections.<DropEntry>emptyList(), pickList);
        PickSubchannelArgs args1 = Mockito.mock(PickSubchannelArgs.class);
        Metadata headers1 = new Metadata();
        // The existing token on the headers will be replaced
        headers1.put(TOKEN_METADATA_KEY, "LBTOKEN__OLD");
        Mockito.when(args1.getHeaders()).thenReturn(headers1);
        Assert.assertSame(b1.result, picker.pickSubchannel(args1));
        Mockito.verify(args1).getHeaders();
        assertThat(headers1.getAll(TOKEN_METADATA_KEY)).containsExactly("LBTOKEN0001");
        PickSubchannelArgs args2 = Mockito.mock(PickSubchannelArgs.class);
        Metadata headers2 = new Metadata();
        Mockito.when(args2.getHeaders()).thenReturn(headers2);
        Assert.assertSame(b2.result, picker.pickSubchannel(args2));
        Mockito.verify(args2).getHeaders();
        assertThat(headers2.getAll(TOKEN_METADATA_KEY)).containsExactly("LBTOKEN0002");
        PickSubchannelArgs args3 = Mockito.mock(PickSubchannelArgs.class);
        Metadata headers3 = new Metadata();
        Mockito.when(args3.getHeaders()).thenReturn(headers3);
        Assert.assertSame(b1.result, picker.pickSubchannel(args3));
        Mockito.verify(args3).getHeaders();
        assertThat(headers3.getAll(TOKEN_METADATA_KEY)).containsExactly("LBTOKEN0001");
        Mockito.verify(subchannel, Mockito.never()).getAttributes();
    }

    @Test
    public void roundRobinPickerWithDrop() {
        Assert.assertTrue(GrpclbState.DROP_PICK_RESULT.isDrop());
        GrpclbClientLoadRecorder loadRecorder = new GrpclbClientLoadRecorder(fakeClock.getTimeProvider());
        Subchannel subchannel = Mockito.mock(Subchannel.class);
        // 1 out of 2 requests are to be dropped
        DropEntry d = new DropEntry(loadRecorder, "LBTOKEN0003");
        List<DropEntry> dropList = Arrays.asList(null, d);
        BackendEntry b1 = new BackendEntry(subchannel, loadRecorder, "LBTOKEN0001");
        BackendEntry b2 = new BackendEntry(subchannel, loadRecorder, "LBTOKEN0002");
        List<BackendEntry> pickList = Arrays.asList(b1, b2);
        RoundRobinPicker picker = new RoundRobinPicker(dropList, pickList);
        // dropList[0], pickList[0]
        PickSubchannelArgs args1 = Mockito.mock(PickSubchannelArgs.class);
        Metadata headers1 = new Metadata();
        headers1.put(TOKEN_METADATA_KEY, "LBTOKEN__OLD");
        Mockito.when(args1.getHeaders()).thenReturn(headers1);
        Assert.assertSame(b1.result, picker.pickSubchannel(args1));
        Mockito.verify(args1).getHeaders();
        assertThat(headers1.getAll(TOKEN_METADATA_KEY)).containsExactly("LBTOKEN0001");
        // dropList[1]: drop
        PickSubchannelArgs args2 = Mockito.mock(PickSubchannelArgs.class);
        Metadata headers2 = new Metadata();
        Mockito.when(args2.getHeaders()).thenReturn(headers2);
        Assert.assertSame(GrpclbState.DROP_PICK_RESULT, picker.pickSubchannel(args2));
        Mockito.verify(args2, Mockito.never()).getHeaders();
        // dropList[0], pickList[1]
        PickSubchannelArgs args3 = Mockito.mock(PickSubchannelArgs.class);
        Metadata headers3 = new Metadata();
        Mockito.when(args3.getHeaders()).thenReturn(headers3);
        Assert.assertSame(b2.result, picker.pickSubchannel(args3));
        Mockito.verify(args3).getHeaders();
        assertThat(headers3.getAll(TOKEN_METADATA_KEY)).containsExactly("LBTOKEN0002");
        // dropList[1]: drop
        PickSubchannelArgs args4 = Mockito.mock(PickSubchannelArgs.class);
        Metadata headers4 = new Metadata();
        Mockito.when(args4.getHeaders()).thenReturn(headers4);
        Assert.assertSame(GrpclbState.DROP_PICK_RESULT, picker.pickSubchannel(args4));
        Mockito.verify(args4, Mockito.never()).getHeaders();
        // dropList[0], pickList[0]
        PickSubchannelArgs args5 = Mockito.mock(PickSubchannelArgs.class);
        Metadata headers5 = new Metadata();
        Mockito.when(args5.getHeaders()).thenReturn(headers5);
        Assert.assertSame(b1.result, picker.pickSubchannel(args5));
        Mockito.verify(args5).getHeaders();
        assertThat(headers5.getAll(TOKEN_METADATA_KEY)).containsExactly("LBTOKEN0001");
        Mockito.verify(subchannel, Mockito.never()).getAttributes();
    }

    @Test
    public void roundRobinPickerWithIdleEntry_noDrop() {
        Subchannel subchannel = Mockito.mock(Subchannel.class);
        IdleSubchannelEntry entry = new IdleSubchannelEntry(subchannel);
        RoundRobinPicker picker = new RoundRobinPicker(Collections.<DropEntry>emptyList(), Collections.singletonList(entry));
        PickSubchannelArgs args = Mockito.mock(PickSubchannelArgs.class);
        Mockito.verify(subchannel, Mockito.never()).requestConnection();
        assertThat(picker.pickSubchannel(args)).isSameAs(PickResult.withNoResult());
        Mockito.verify(subchannel).requestConnection();
    }

    @Test
    public void roundRobinPickerWithIdleEntry_andDrop() {
        GrpclbClientLoadRecorder loadRecorder = new GrpclbClientLoadRecorder(fakeClock.getTimeProvider());
        // 1 out of 2 requests are to be dropped
        DropEntry d = new DropEntry(loadRecorder, "LBTOKEN0003");
        List<DropEntry> dropList = Arrays.asList(null, d);
        Subchannel subchannel = Mockito.mock(Subchannel.class);
        IdleSubchannelEntry entry = new IdleSubchannelEntry(subchannel);
        RoundRobinPicker picker = new RoundRobinPicker(dropList, Collections.singletonList(entry));
        PickSubchannelArgs args = Mockito.mock(PickSubchannelArgs.class);
        Mockito.verify(subchannel, Mockito.never()).requestConnection();
        assertThat(picker.pickSubchannel(args)).isSameAs(PickResult.withNoResult());
        Mockito.verify(subchannel).requestConnection();
        assertThat(picker.pickSubchannel(args)).isSameAs(GrpclbState.DROP_PICK_RESULT);
        Mockito.verify(subchannel).requestConnection();
        assertThat(picker.pickSubchannel(args)).isSameAs(PickResult.withNoResult());
        Mockito.verify(subchannel, Mockito.times(2)).requestConnection();
    }

    @Test
    public void roundRobinPicker_requestConnection() {
        // requestConnection() on RoundRobinPicker is only passed to IdleSubchannelEntry
        Subchannel subchannel1 = Mockito.mock(Subchannel.class);
        Subchannel subchannel2 = Mockito.mock(Subchannel.class);
        RoundRobinPicker picker = new RoundRobinPicker(Collections.<DropEntry>emptyList(), Arrays.<RoundRobinEntry>asList(new BackendEntry(subchannel1), new IdleSubchannelEntry(subchannel2), new io.grpc.grpclb.GrpclbState.ErrorEntry(Status.UNAVAILABLE)));
        Mockito.verify(subchannel2, Mockito.never()).requestConnection();
        picker.requestConnection();
        Mockito.verify(subchannel2).requestConnection();
        Mockito.verify(subchannel1, Mockito.never()).requestConnection();
    }

    @Test
    public void loadReporting() {
        Metadata headers = new Metadata();
        PickSubchannelArgs args = Mockito.mock(PickSubchannelArgs.class);
        Mockito.when(args.getHeaders()).thenReturn(headers);
        long loadReportIntervalMillis = 1983;
        List<EquivalentAddressGroup> grpclbResolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(true);
        Attributes grpclbResolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        // Fallback timer is started as soon as address is resolved.
        Assert.assertEquals(1, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.FALLBACK_MODE_TASK_FILTER));
        Assert.assertEquals(1, fakeOobChannels.size());
        Mockito.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        StreamObserver<LoadBalanceResponse> lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        StreamObserver<LoadBalanceRequest> lbRequestObserver = lbRequestObservers.poll();
        InOrder inOrder = Mockito.inOrder(lbRequestObserver);
        InOrder helperInOrder = Mockito.inOrder(helper, subchannelPool);
        inOrder.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(LoadBalanceRequest.newBuilder().setInitialRequest(InitialLoadBalanceRequest.newBuilder().setName(GrpclbLoadBalancerTest.SERVICE_AUTHORITY).build()).build()));
        // Simulate receiving LB response
        Assert.assertEquals(0, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER));
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse(loadReportIntervalMillis));
        // Load reporting task is scheduled
        Assert.assertEquals(1, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER));
        Assert.assertEquals(0, fakeClock.runDueTasks());
        List<GrpclbLoadBalancerTest.ServerEntry> backends = // drop
        Arrays.asList(new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2000, "token0001"), new GrpclbLoadBalancerTest.ServerEntry("token0001"), new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2010, "token0002"), new GrpclbLoadBalancerTest.ServerEntry("token0003"));// drop

        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildLbResponse(backends));
        Assert.assertEquals(2, mockSubchannels.size());
        Subchannel subchannel1 = mockSubchannels.poll();
        Subchannel subchannel2 = mockSubchannels.poll();
        deliverSubchannelState(subchannel1, ConnectivityStateInfo.forNonError(ConnectivityState.CONNECTING));
        deliverSubchannelState(subchannel2, ConnectivityStateInfo.forNonError(ConnectivityState.CONNECTING));
        deliverSubchannelState(subchannel1, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        deliverSubchannelState(subchannel2, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        helperInOrder.verify(helper, Mockito.atLeast(1)).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        RoundRobinPicker picker = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker.dropList).containsExactly(null, new DropEntry(getLoadRecorder(), "token0001"), null, new DropEntry(getLoadRecorder(), "token0003")).inOrder();
        assertThat(picker.pickList).containsExactly(new BackendEntry(subchannel1, getLoadRecorder(), "token0001"), new BackendEntry(subchannel2, getLoadRecorder(), "token0002")).inOrder();
        // Report, no data
        assertNextReport(inOrder, lbRequestObserver, loadReportIntervalMillis, ClientStats.newBuilder().build());
        PickResult pick1 = picker.pickSubchannel(args);
        Assert.assertSame(subchannel1, pick1.getSubchannel());
        Assert.assertSame(getLoadRecorder(), pick1.getStreamTracerFactory());
        // Merely the pick will not be recorded as upstart.
        assertNextReport(inOrder, lbRequestObserver, loadReportIntervalMillis, ClientStats.newBuilder().build());
        ClientStreamTracer tracer1 = pick1.getStreamTracerFactory().newClientStreamTracer(GrpclbLoadBalancerTest.STREAM_INFO, new Metadata());
        PickResult pick2 = picker.pickSubchannel(args);
        Assert.assertNull(pick2.getSubchannel());
        Assert.assertSame(GrpclbState.DROP_PICK_RESULT, pick2);
        // Report includes upstart of pick1 and the drop of pick2
        assertNextReport(inOrder, lbRequestObserver, loadReportIntervalMillis, // pick2
        ClientStats.newBuilder().setNumCallsStarted(2).setNumCallsFinished(1).addCallsFinishedWithDrop(// pick2
        ClientStatsPerToken.newBuilder().setLoadBalanceToken("token0001").setNumCalls(1).build()).build());
        PickResult pick3 = picker.pickSubchannel(args);
        Assert.assertSame(subchannel2, pick3.getSubchannel());
        Assert.assertSame(getLoadRecorder(), pick3.getStreamTracerFactory());
        ClientStreamTracer tracer3 = pick3.getStreamTracerFactory().newClientStreamTracer(GrpclbLoadBalancerTest.STREAM_INFO, new Metadata());
        // pick3 has sent out headers
        tracer3.outboundHeaders();
        // 3rd report includes pick3's upstart
        assertNextReport(inOrder, lbRequestObserver, loadReportIntervalMillis, ClientStats.newBuilder().setNumCallsStarted(1).build());
        PickResult pick4 = picker.pickSubchannel(args);
        Assert.assertNull(pick4.getSubchannel());
        Assert.assertSame(GrpclbState.DROP_PICK_RESULT, pick4);
        // pick1 ended without sending anything
        tracer1.streamClosed(CANCELLED);
        // 4th report includes end of pick1 and drop of pick4
        assertNextReport(inOrder, lbRequestObserver, loadReportIntervalMillis, // pick1
        // pick4
        ClientStats.newBuilder().setNumCallsStarted(1).setNumCallsFinished(2).setNumCallsFinishedWithClientFailedToSend(1).addCallsFinishedWithDrop(// pick4
        ClientStatsPerToken.newBuilder().setLoadBalanceToken("token0003").setNumCalls(1).build()).build());
        PickResult pick5 = picker.pickSubchannel(args);
        Assert.assertSame(subchannel1, pick1.getSubchannel());
        Assert.assertSame(getLoadRecorder(), pick5.getStreamTracerFactory());
        ClientStreamTracer tracer5 = pick5.getStreamTracerFactory().newClientStreamTracer(GrpclbLoadBalancerTest.STREAM_INFO, new Metadata());
        // pick3 ended without receiving response headers
        tracer3.streamClosed(DEADLINE_EXCEEDED);
        // pick5 sent and received headers
        tracer5.outboundHeaders();
        tracer5.inboundHeaders();
        // 5th report includes pick3's end and pick5's upstart
        assertNextReport(inOrder, lbRequestObserver, loadReportIntervalMillis, // pick3
        // pick5
        ClientStats.newBuilder().setNumCallsStarted(1).setNumCallsFinished(1).build());
        // pick5 ends
        tracer5.streamClosed(OK);
        // 6th report includes pick5's end
        assertNextReport(inOrder, lbRequestObserver, loadReportIntervalMillis, ClientStats.newBuilder().setNumCallsFinished(1).setNumCallsFinishedKnownReceived(1).build());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        // Balancer closes the stream, scheduled reporting task cancelled
        lbResponseObserver.onError(UNAVAILABLE.asException());
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        // New stream created
        Mockito.verify(mockLbService, Mockito.times(2)).balanceLoad(lbResponseObserverCaptor.capture());
        lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        lbRequestObserver = lbRequestObservers.poll();
        inOrder = Mockito.inOrder(lbRequestObserver);
        inOrder.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(LoadBalanceRequest.newBuilder().setInitialRequest(InitialLoadBalanceRequest.newBuilder().setName(GrpclbLoadBalancerTest.SERVICE_AUTHORITY).build()).build()));
        // Load reporting is also requested
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse(loadReportIntervalMillis));
        // No picker created because balancer is still using the results from the last stream
        helperInOrder.verify(helper, Mockito.never()).updateBalancingState(ArgumentMatchers.any(ConnectivityState.class), ArgumentMatchers.any(SubchannelPicker.class));
        // Make a new pick on that picker.  It will not show up on the report of the new stream, because
        // that picker is associated with the previous stream.
        PickResult pick6 = picker.pickSubchannel(args);
        Assert.assertNull(pick6.getSubchannel());
        Assert.assertSame(GrpclbState.DROP_PICK_RESULT, pick6);
        assertNextReport(inOrder, lbRequestObserver, loadReportIntervalMillis, ClientStats.newBuilder().build());
        // New stream got the list update
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildLbResponse(backends));
        // Same backends, thus no new subchannels
        helperInOrder.verify(subchannelPool, Mockito.never()).takeOrCreateSubchannel(ArgumentMatchers.any(EquivalentAddressGroup.class), ArgumentMatchers.any(Attributes.class));
        // But the new RoundRobinEntries have a new loadRecorder, thus considered different from
        // the previous list, thus a new picker is created
        helperInOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        picker = ((RoundRobinPicker) (pickerCaptor.getValue()));
        PickResult pick1p = picker.pickSubchannel(args);
        Assert.assertSame(subchannel1, pick1p.getSubchannel());
        Assert.assertSame(getLoadRecorder(), pick1p.getStreamTracerFactory());
        pick1p.getStreamTracerFactory().newClientStreamTracer(GrpclbLoadBalancerTest.STREAM_INFO, new Metadata());
        // The pick from the new stream will be included in the report
        assertNextReport(inOrder, lbRequestObserver, loadReportIntervalMillis, ClientStats.newBuilder().setNumCallsStarted(1).build());
        Mockito.verify(args, Mockito.atLeast(0)).getHeaders();
        Mockito.verifyNoMoreInteractions(args);
    }

    @Test
    public void abundantInitialResponse() {
        Metadata headers = new Metadata();
        PickSubchannelArgs args = Mockito.mock(PickSubchannelArgs.class);
        Mockito.when(args.getHeaders()).thenReturn(headers);
        List<EquivalentAddressGroup> grpclbResolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(true);
        Attributes grpclbResolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        Assert.assertEquals(1, fakeOobChannels.size());
        Mockito.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        StreamObserver<LoadBalanceResponse> lbResponseObserver = lbResponseObserverCaptor.getValue();
        // Simulate LB initial response
        Assert.assertEquals(0, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER));
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse(1983));
        // Load reporting task is scheduled
        Assert.assertEquals(1, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER));
        FakeClock.ScheduledTask scheduledTask = Iterables.getOnlyElement(fakeClock.getPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER));
        Assert.assertEquals(1983, scheduledTask.getDelay(TimeUnit.MILLISECONDS));
        logs.clear();
        // Simulate an abundant LB initial response, with a different report interval
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse(9097));
        // This incident is logged
        assertThat(logs).containsExactly(("DEBUG: Got an LB response: " + (GrpclbLoadBalancerTest.buildInitialResponse(9097))), "WARNING: Ignoring unexpected response type: INITIAL_RESPONSE").inOrder();
        // It doesn't affect load-reporting at all
        assertThat(fakeClock.getPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER)).containsExactly(scheduledTask);
        Assert.assertEquals(1983, scheduledTask.getDelay(TimeUnit.MILLISECONDS));
    }

    @Test
    public void raceBetweenLoadReportingAndLbStreamClosure() {
        Metadata headers = new Metadata();
        PickSubchannelArgs args = Mockito.mock(PickSubchannelArgs.class);
        Mockito.when(args.getHeaders()).thenReturn(headers);
        List<EquivalentAddressGroup> grpclbResolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(true);
        Attributes grpclbResolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        Assert.assertEquals(1, fakeOobChannels.size());
        Mockito.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        StreamObserver<LoadBalanceResponse> lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        StreamObserver<LoadBalanceRequest> lbRequestObserver = lbRequestObservers.poll();
        InOrder inOrder = Mockito.inOrder(lbRequestObserver);
        inOrder.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(LoadBalanceRequest.newBuilder().setInitialRequest(InitialLoadBalanceRequest.newBuilder().setName(GrpclbLoadBalancerTest.SERVICE_AUTHORITY).build()).build()));
        // Simulate receiving LB response
        Assert.assertEquals(0, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER));
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse(1983));
        // Load reporting task is scheduled
        Assert.assertEquals(1, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER));
        FakeClock.ScheduledTask scheduledTask = Iterables.getOnlyElement(fakeClock.getPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER));
        Assert.assertEquals(1983, scheduledTask.getDelay(TimeUnit.MILLISECONDS));
        // Close lbStream
        lbResponseObserver.onCompleted();
        // Reporting task cancelled
        Assert.assertEquals(0, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER));
        // Simulate a race condition where the task has just started when its cancelled
        scheduledTask.command.run();
        // No report sent. No new task scheduled
        inOrder.verify(lbRequestObserver, Mockito.never()).onNext(ArgumentMatchers.any(LoadBalanceRequest.class));
        Assert.assertEquals(0, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER));
    }

    @Test
    public void nameResolutionFailsThenRecover() {
        Status error = NOT_FOUND.withDescription("www.google.com not found");
        deliverNameResolutionError(error);
        Mockito.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.TRANSIENT_FAILURE), pickerCaptor.capture());
        assertThat(logs).containsExactly(("DEBUG: Error: " + error), ("INFO: TRANSIENT_FAILURE: picks=" + ("[Status{code=NOT_FOUND, description=www.google.com not found, cause=null}]," + " drops=[]"))).inOrder();
        logs.clear();
        RoundRobinPicker picker = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker.dropList).isEmpty();
        assertThat(picker.pickList).containsExactly(new io.grpc.grpclb.GrpclbState.ErrorEntry(error));
        // Recover with a subsequent success
        List<EquivalentAddressGroup> resolvedServers = GrpclbLoadBalancerTest.createResolvedServerAddresses(true);
        EquivalentAddressGroup eag = resolvedServers.get(0);
        Attributes resolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(resolvedServers, resolutionAttrs);
        Mockito.verify(helper).createOobChannel(ArgumentMatchers.eq(eag), ArgumentMatchers.eq(GrpclbLoadBalancerTest.lbAuthority(0)));
        Mockito.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
    }

    @Test
    public void grpclbThenNameResolutionFails() {
        InOrder inOrder = Mockito.inOrder(helper, subchannelPool);
        // Go to GRPCLB first
        List<EquivalentAddressGroup> grpclbResolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(true);
        Attributes grpclbResolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        Mockito.verify(helper).createOobChannel(ArgumentMatchers.eq(grpclbResolutionList.get(0)), ArgumentMatchers.eq(GrpclbLoadBalancerTest.lbAuthority(0)));
        Assert.assertEquals(1, fakeOobChannels.size());
        ManagedChannel oobChannel = fakeOobChannels.poll();
        Mockito.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        StreamObserver<LoadBalanceResponse> lbResponseObserver = lbResponseObserverCaptor.getValue();
        // Let name resolution fail before round-robin list is ready
        Status error = NOT_FOUND.withDescription("www.google.com not found");
        deliverNameResolutionError(error);
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.TRANSIENT_FAILURE), pickerCaptor.capture());
        RoundRobinPicker picker = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker.dropList).isEmpty();
        assertThat(picker.pickList).containsExactly(new io.grpc.grpclb.GrpclbState.ErrorEntry(error));
        Assert.assertFalse(oobChannel.isShutdown());
        // Simulate receiving LB response
        List<GrpclbLoadBalancerTest.ServerEntry> backends = Arrays.asList(new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2000, "TOKEN1"), new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2010, "TOKEN2"));
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse());
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildLbResponse(backends));
        inOrder.verify(subchannelPool).takeOrCreateSubchannel(ArgumentMatchers.eq(new EquivalentAddressGroup(backends.get(0).addr, GrpclbLoadBalancerTest.LB_BACKEND_ATTRS)), ArgumentMatchers.any(Attributes.class));
        inOrder.verify(subchannelPool).takeOrCreateSubchannel(ArgumentMatchers.eq(new EquivalentAddressGroup(backends.get(1).addr, GrpclbLoadBalancerTest.LB_BACKEND_ATTRS)), ArgumentMatchers.any(Attributes.class));
    }

    @Test
    public void grpclbUpdatedAddresses_avoidsReconnect() {
        List<EquivalentAddressGroup> grpclbResolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(true, false);
        Attributes grpclbResolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        Mockito.verify(helper).createOobChannel(ArgumentMatchers.eq(grpclbResolutionList.get(0)), ArgumentMatchers.eq(GrpclbLoadBalancerTest.lbAuthority(0)));
        ManagedChannel oobChannel = fakeOobChannels.poll();
        Assert.assertEquals(1, lbRequestObservers.size());
        List<EquivalentAddressGroup> grpclbResolutionList2 = GrpclbLoadBalancerTest.createResolvedServerAddresses(true, false, true);
        EquivalentAddressGroup combinedEag = new EquivalentAddressGroup(Arrays.asList(grpclbResolutionList2.get(0).getAddresses().get(0), grpclbResolutionList2.get(2).getAddresses().get(0)), GrpclbLoadBalancerTest.lbAttributes(GrpclbLoadBalancerTest.lbAuthority(0)));
        deliverResolvedAddresses(grpclbResolutionList2, grpclbResolutionAttrs);
        Mockito.verify(helper).updateOobChannelAddresses(ArgumentMatchers.eq(oobChannel), ArgumentMatchers.eq(combinedEag));
        Assert.assertEquals(1, lbRequestObservers.size());// No additional RPC

    }

    @Test
    public void grpclbUpdatedAddresses_reconnectOnAuthorityChange() {
        List<EquivalentAddressGroup> grpclbResolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(true, false);
        Attributes grpclbResolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        Mockito.verify(helper).createOobChannel(ArgumentMatchers.eq(grpclbResolutionList.get(0)), ArgumentMatchers.eq(GrpclbLoadBalancerTest.lbAuthority(0)));
        ManagedChannel oobChannel = fakeOobChannels.poll();
        Assert.assertEquals(1, lbRequestObservers.size());
        final String newAuthority = "some-new-authority";
        List<EquivalentAddressGroup> grpclbResolutionList2 = GrpclbLoadBalancerTest.createResolvedServerAddresses(false);
        grpclbResolutionList2.add(new EquivalentAddressGroup(new FakeSocketAddress("somethingNew"), GrpclbLoadBalancerTest.lbAttributes(newAuthority)));
        deliverResolvedAddresses(grpclbResolutionList2, grpclbResolutionAttrs);
        Assert.assertTrue(oobChannel.isTerminated());
        Mockito.verify(helper).createOobChannel(ArgumentMatchers.eq(grpclbResolutionList2.get(1)), ArgumentMatchers.eq(newAuthority));
        Assert.assertEquals(2, lbRequestObservers.size());// An additional RPC

    }

    @Test
    public void grpclbWorking() {
        InOrder inOrder = Mockito.inOrder(helper, subchannelPool);
        List<EquivalentAddressGroup> grpclbResolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(true);
        Attributes grpclbResolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        // Fallback timer is started as soon as the addresses are resolved.
        Assert.assertEquals(1, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.FALLBACK_MODE_TASK_FILTER));
        Mockito.verify(helper).createOobChannel(ArgumentMatchers.eq(grpclbResolutionList.get(0)), ArgumentMatchers.eq(GrpclbLoadBalancerTest.lbAuthority(0)));
        Assert.assertEquals(1, fakeOobChannels.size());
        ManagedChannel oobChannel = fakeOobChannels.poll();
        Mockito.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        StreamObserver<LoadBalanceResponse> lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        StreamObserver<LoadBalanceRequest> lbRequestObserver = lbRequestObservers.poll();
        Mockito.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(LoadBalanceRequest.newBuilder().setInitialRequest(InitialLoadBalanceRequest.newBuilder().setName(GrpclbLoadBalancerTest.SERVICE_AUTHORITY).build()).build()));
        // Simulate receiving LB response
        List<GrpclbLoadBalancerTest.ServerEntry> backends1 = Arrays.asList(new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2000, "token0001"), new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2010, "token0002"));
        inOrder.verify(helper, Mockito.never()).updateBalancingState(ArgumentMatchers.any(ConnectivityState.class), ArgumentMatchers.any(SubchannelPicker.class));
        logs.clear();
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse());
        assertThat(logs).containsExactly(("DEBUG: Got an LB response: " + (GrpclbLoadBalancerTest.buildInitialResponse())));
        logs.clear();
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildLbResponse(backends1));
        inOrder.verify(subchannelPool).takeOrCreateSubchannel(ArgumentMatchers.eq(new EquivalentAddressGroup(backends1.get(0).addr, GrpclbLoadBalancerTest.LB_BACKEND_ATTRS)), ArgumentMatchers.any(Attributes.class));
        inOrder.verify(subchannelPool).takeOrCreateSubchannel(ArgumentMatchers.eq(new EquivalentAddressGroup(backends1.get(1).addr, GrpclbLoadBalancerTest.LB_BACKEND_ATTRS)), ArgumentMatchers.any(Attributes.class));
        Assert.assertEquals(2, mockSubchannels.size());
        Subchannel subchannel1 = mockSubchannels.poll();
        Subchannel subchannel2 = mockSubchannels.poll();
        Mockito.verify(subchannel1).requestConnection();
        Mockito.verify(subchannel2).requestConnection();
        Assert.assertEquals(new EquivalentAddressGroup(backends1.get(0).addr, GrpclbLoadBalancerTest.LB_BACKEND_ATTRS), subchannel1.getAddresses());
        Assert.assertEquals(new EquivalentAddressGroup(backends1.get(1).addr, GrpclbLoadBalancerTest.LB_BACKEND_ATTRS), subchannel2.getAddresses());
        deliverSubchannelState(subchannel1, ConnectivityStateInfo.forNonError(ConnectivityState.CONNECTING));
        deliverSubchannelState(subchannel2, ConnectivityStateInfo.forNonError(ConnectivityState.CONNECTING));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), pickerCaptor.capture());
        RoundRobinPicker picker0 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker0.dropList).containsExactly(null, null);
        assertThat(picker0.pickList).containsExactly(GrpclbState.BUFFER_ENTRY);
        inOrder.verifyNoMoreInteractions();
        assertThat(logs).containsExactly(("DEBUG: Got an LB response: " + (GrpclbLoadBalancerTest.buildLbResponse(backends1))), ("INFO: Using RR list=" + (("[[[/127.0.0.1:2000]/{io.grpc.grpclb.lbProvidedBackend=true}](token0001)," + " [[/127.0.0.1:2010]/{io.grpc.grpclb.lbProvidedBackend=true}](token0002)],") + " drop=[null, null]")), "INFO: CONNECTING: picks=[BUFFER_ENTRY], drops=[null, null]").inOrder();
        logs.clear();
        // Let subchannels be connected
        deliverSubchannelState(subchannel2, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        assertThat(logs).containsExactly(("INFO: READY: picks=" + ("[[[[[/127.0.0.1:2010]/{io.grpc.grpclb.lbProvidedBackend=true}]](token0002)]]," + " drops=[null, null]")));
        logs.clear();
        RoundRobinPicker picker1 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker1.dropList).containsExactly(null, null);
        assertThat(picker1.pickList).containsExactly(new BackendEntry(subchannel2, getLoadRecorder(), "token0002"));
        deliverSubchannelState(subchannel1, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        assertThat(logs).containsExactly(("INFO: READY: picks=" + (("[[[[[/127.0.0.1:2000]/{io.grpc.grpclb.lbProvidedBackend=true}]](token0001)]," + " [[[[/127.0.0.1:2010]/{io.grpc.grpclb.lbProvidedBackend=true}]](token0002)]],") + " drops=[null, null]")));
        logs.clear();
        RoundRobinPicker picker2 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker2.dropList).containsExactly(null, null);
        assertThat(picker2.pickList).containsExactly(new BackendEntry(subchannel1, getLoadRecorder(), "token0001"), new BackendEntry(subchannel2, getLoadRecorder(), "token0002")).inOrder();
        // Disconnected subchannels
        Mockito.verify(subchannel1).requestConnection();
        deliverSubchannelState(subchannel1, ConnectivityStateInfo.forNonError(ConnectivityState.IDLE));
        Mockito.verify(subchannel1, Mockito.times(2)).requestConnection();
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        assertThat(logs).containsExactly(("INFO: READY: picks=" + ("[[[[[/127.0.0.1:2010]/{io.grpc.grpclb.lbProvidedBackend=true}]](token0002)]]," + " drops=[null, null]")));
        logs.clear();
        RoundRobinPicker picker3 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker3.dropList).containsExactly(null, null);
        assertThat(picker3.pickList).containsExactly(new BackendEntry(subchannel2, getLoadRecorder(), "token0002"));
        deliverSubchannelState(subchannel1, ConnectivityStateInfo.forNonError(ConnectivityState.CONNECTING));
        inOrder.verifyNoMoreInteractions();
        // As long as there is at least one READY subchannel, round robin will work.
        ConnectivityStateInfo errorState1 = ConnectivityStateInfo.forTransientFailure(UNAVAILABLE.withDescription("error1"));
        deliverSubchannelState(subchannel1, errorState1);
        inOrder.verifyNoMoreInteractions();
        // If no subchannel is READY, some with error and the others are IDLE, will report CONNECTING
        Mockito.verify(subchannel2).requestConnection();
        deliverSubchannelState(subchannel2, ConnectivityStateInfo.forNonError(ConnectivityState.IDLE));
        Mockito.verify(subchannel2, Mockito.times(2)).requestConnection();
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), pickerCaptor.capture());
        assertThat(logs).containsExactly("INFO: CONNECTING: picks=[BUFFER_ENTRY], drops=[null, null]");
        logs.clear();
        RoundRobinPicker picker4 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker4.dropList).containsExactly(null, null);
        assertThat(picker4.pickList).containsExactly(GrpclbState.BUFFER_ENTRY);
        // Update backends, with a drop entry
        List<GrpclbLoadBalancerTest.ServerEntry> backends2 = // New address
        // drop
        // Existing address with token changed
        // New address appearing second time
        Arrays.asList(new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2030, "token0003"), new GrpclbLoadBalancerTest.ServerEntry("token0003"), new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2010, "token0004"), new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2030, "token0005"), new GrpclbLoadBalancerTest.ServerEntry("token0006"));// drop

        Mockito.verify(subchannelPool, Mockito.never()).returnSubchannel(ArgumentMatchers.same(subchannel1), ArgumentMatchers.any(ConnectivityStateInfo.class));
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildLbResponse(backends2));
        assertThat(logs).containsExactly(("DEBUG: Got an LB response: " + (GrpclbLoadBalancerTest.buildLbResponse(backends2))), ("INFO: Using RR list=" + ((("[[[/127.0.0.1:2030]/{io.grpc.grpclb.lbProvidedBackend=true}](token0003)," + " [[/127.0.0.1:2010]/{io.grpc.grpclb.lbProvidedBackend=true}](token0004),") + " [[/127.0.0.1:2030]/{io.grpc.grpclb.lbProvidedBackend=true}](token0005)],") + " drop=[null, drop(token0003), null, null, drop(token0006)]")), ("INFO: CONNECTING: picks=[BUFFER_ENTRY]," + " drops=[null, drop(token0003), null, null, drop(token0006)]")).inOrder();
        logs.clear();
        // not in backends2, closed
        Mockito.verify(subchannelPool).returnSubchannel(ArgumentMatchers.same(subchannel1), ArgumentMatchers.same(errorState1));
        // backends2[2], will be kept
        Mockito.verify(subchannelPool, Mockito.never()).returnSubchannel(ArgumentMatchers.same(subchannel2), ArgumentMatchers.any(ConnectivityStateInfo.class));
        inOrder.verify(subchannelPool, Mockito.never()).takeOrCreateSubchannel(ArgumentMatchers.eq(new EquivalentAddressGroup(backends2.get(2).addr, GrpclbLoadBalancerTest.LB_BACKEND_ATTRS)), ArgumentMatchers.any(Attributes.class));
        inOrder.verify(subchannelPool).takeOrCreateSubchannel(ArgumentMatchers.eq(new EquivalentAddressGroup(backends2.get(0).addr, GrpclbLoadBalancerTest.LB_BACKEND_ATTRS)), ArgumentMatchers.any(Attributes.class));
        ConnectivityStateInfo errorOnCachedSubchannel1 = ConnectivityStateInfo.forTransientFailure(UNAVAILABLE.withDescription("You can get this error even if you are cached"));
        deliverSubchannelState(subchannel1, errorOnCachedSubchannel1);
        Mockito.verify(subchannelPool).handleSubchannelState(ArgumentMatchers.same(subchannel1), ArgumentMatchers.same(errorOnCachedSubchannel1));
        Assert.assertEquals(1, mockSubchannels.size());
        Subchannel subchannel3 = mockSubchannels.poll();
        Mockito.verify(subchannel3).requestConnection();
        Assert.assertEquals(new EquivalentAddressGroup(backends2.get(0).addr, GrpclbLoadBalancerTest.LB_BACKEND_ATTRS), subchannel3.getAddresses());
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), pickerCaptor.capture());
        RoundRobinPicker picker7 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker7.dropList).containsExactly(null, new DropEntry(getLoadRecorder(), "token0003"), null, null, new DropEntry(getLoadRecorder(), "token0006")).inOrder();
        assertThat(picker7.pickList).containsExactly(GrpclbState.BUFFER_ENTRY);
        // State updates on obsolete subchannel1 will only be passed to the pool
        deliverSubchannelState(subchannel1, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        deliverSubchannelState(subchannel1, ConnectivityStateInfo.forTransientFailure(UNAVAILABLE));
        deliverSubchannelState(subchannel1, ConnectivityStateInfo.forNonError(ConnectivityState.SHUTDOWN));
        inOrder.verify(subchannelPool).handleSubchannelState(ArgumentMatchers.same(subchannel1), ArgumentMatchers.eq(ConnectivityStateInfo.forNonError(ConnectivityState.READY)));
        inOrder.verify(subchannelPool).handleSubchannelState(ArgumentMatchers.same(subchannel1), ArgumentMatchers.eq(ConnectivityStateInfo.forTransientFailure(UNAVAILABLE)));
        inOrder.verifyNoMoreInteractions();
        deliverSubchannelState(subchannel3, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        RoundRobinPicker picker8 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker8.dropList).containsExactly(null, new DropEntry(getLoadRecorder(), "token0003"), null, null, new DropEntry(getLoadRecorder(), "token0006")).inOrder();
        // subchannel2 is still IDLE, thus not in the active list
        assertThat(picker8.pickList).containsExactly(new BackendEntry(subchannel3, getLoadRecorder(), "token0003"), new BackendEntry(subchannel3, getLoadRecorder(), "token0005")).inOrder();
        // subchannel2 becomes READY and makes it into the list
        deliverSubchannelState(subchannel2, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        RoundRobinPicker picker9 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker9.dropList).containsExactly(null, new DropEntry(getLoadRecorder(), "token0003"), null, null, new DropEntry(getLoadRecorder(), "token0006")).inOrder();
        assertThat(picker9.pickList).containsExactly(new BackendEntry(subchannel3, getLoadRecorder(), "token0003"), new BackendEntry(subchannel2, getLoadRecorder(), "token0004"), new BackendEntry(subchannel3, getLoadRecorder(), "token0005")).inOrder();
        Mockito.verify(subchannelPool, Mockito.never()).returnSubchannel(ArgumentMatchers.same(subchannel3), ArgumentMatchers.any(ConnectivityStateInfo.class));
        // Update backends, with no entry
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildLbResponse(Collections.<GrpclbLoadBalancerTest.ServerEntry>emptyList()));
        Mockito.verify(subchannelPool).returnSubchannel(ArgumentMatchers.same(subchannel2), ArgumentMatchers.eq(ConnectivityStateInfo.forNonError(ConnectivityState.READY)));
        Mockito.verify(subchannelPool).returnSubchannel(ArgumentMatchers.same(subchannel3), ArgumentMatchers.eq(ConnectivityStateInfo.forNonError(ConnectivityState.READY)));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), pickerCaptor.capture());
        RoundRobinPicker picker10 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker10.dropList).isEmpty();
        assertThat(picker10.pickList).containsExactly(GrpclbState.BUFFER_ENTRY);
        Assert.assertFalse(oobChannel.isShutdown());
        Assert.assertEquals(0, lbRequestObservers.size());
        Mockito.verify(lbRequestObserver, Mockito.never()).onCompleted();
        Mockito.verify(lbRequestObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        // Load reporting was not requested, thus never scheduled
        Assert.assertEquals(0, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LOAD_REPORTING_TASK_FILTER));
        Mockito.verify(subchannelPool, Mockito.never()).clear();
        balancer.shutdown();
        Mockito.verify(subchannelPool).clear();
    }

    @Test
    public void grpclbFallback_initialTimeout_serverListReceivedBeforeTimerExpires() {
        subtestGrpclbFallbackInitialTimeout(false);
    }

    @Test
    public void grpclbFallback_initialTimeout_timerExpires() {
        subtestGrpclbFallbackInitialTimeout(true);
    }

    @Test
    public void grpclbFallback_breakLbStreamBeforeFallbackTimerExpires() {
        long loadReportIntervalMillis = 1983;
        InOrder inOrder = Mockito.inOrder(helper, subchannelPool);
        // Create a resolution list with a mixture of balancer and backend addresses
        List<EquivalentAddressGroup> resolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(false, true, false);
        Attributes resolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(resolutionList, resolutionAttrs);
        inOrder.verify(helper).createOobChannel(ArgumentMatchers.eq(resolutionList.get(1)), ArgumentMatchers.eq(GrpclbLoadBalancerTest.lbAuthority(0)));
        // Attempted to connect to balancer
        assertThat(fakeOobChannels).hasSize(1);
        Mockito.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        StreamObserver<LoadBalanceResponse> lbResponseObserver = lbResponseObserverCaptor.getValue();
        assertThat(lbRequestObservers).hasSize(1);
        StreamObserver<LoadBalanceRequest> lbRequestObserver = lbRequestObservers.poll();
        Mockito.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(LoadBalanceRequest.newBuilder().setInitialRequest(InitialLoadBalanceRequest.newBuilder().setName(GrpclbLoadBalancerTest.SERVICE_AUTHORITY).build()).build()));
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse(loadReportIntervalMillis));
        // We don't care if these methods have been run.
        inOrder.verify(helper, Mockito.atLeast(0)).getSynchronizationContext();
        inOrder.verify(helper, Mockito.atLeast(0)).getScheduledExecutorService();
        inOrder.verifyNoMoreInteractions();
        Assert.assertEquals(1, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.FALLBACK_MODE_TASK_FILTER));
        // ///////////////////////////////////////////
        // Break the LB stream before timer expires
        // ///////////////////////////////////////////
        Status streamError = UNAVAILABLE.withDescription("OOB stream broken");
        lbResponseObserver.onError(streamError.asException());
        // Fall back to the backends from resolver
        fallbackTestVerifyUseOfFallbackBackendLists(inOrder, Arrays.asList(resolutionList.get(0), resolutionList.get(2)));
        // A new stream is created
        Mockito.verify(mockLbService, Mockito.times(2)).balanceLoad(lbResponseObserverCaptor.capture());
        assertThat(lbRequestObservers).hasSize(1);
        lbRequestObserver = lbRequestObservers.poll();
        Mockito.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(LoadBalanceRequest.newBuilder().setInitialRequest(InitialLoadBalanceRequest.newBuilder().setName(GrpclbLoadBalancerTest.SERVICE_AUTHORITY).build()).build()));
    }

    @Test
    public void grpclbFallback_noBalancerAddress() {
        InOrder inOrder = Mockito.inOrder(helper, subchannelPool);
        // Create a resolution list with just backend addresses
        List<EquivalentAddressGroup> resolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(false, false);
        Attributes resolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(resolutionList, resolutionAttrs);
        assertThat(logs).containsExactly("INFO: Using fallback backends", ("INFO: Using RR list=[[[FakeSocketAddress-fake-address-0]/{}], " + "[[FakeSocketAddress-fake-address-1]/{}]], drop=[null, null]"), "INFO: CONNECTING: picks=[BUFFER_ENTRY], drops=[null, null]").inOrder();
        // Fall back to the backends from resolver
        fallbackTestVerifyUseOfFallbackBackendLists(inOrder, resolutionList);
        // No fallback timeout timer scheduled.
        Assert.assertEquals(0, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.FALLBACK_MODE_TASK_FILTER));
        Mockito.verify(helper, Mockito.never()).createOobChannel(ArgumentMatchers.any(EquivalentAddressGroup.class), ArgumentMatchers.anyString());
    }

    @Test
    public void grpclbFallback_balancerLost() {
        subtestGrpclbFallbackConnectionLost(true, false);
    }

    @Test
    public void grpclbFallback_subchannelsLost() {
        subtestGrpclbFallbackConnectionLost(false, true);
    }

    @Test
    public void grpclbFallback_allLost() {
        subtestGrpclbFallbackConnectionLost(true, true);
    }

    @Test
    public void grpclbMultipleAuthorities() throws Exception {
        List<EquivalentAddressGroup> grpclbResolutionList = Arrays.asList(new EquivalentAddressGroup(new FakeSocketAddress("fake-address-1"), GrpclbLoadBalancerTest.lbAttributes("fake-authority-1")), new EquivalentAddressGroup(new FakeSocketAddress("fake-address-2"), GrpclbLoadBalancerTest.lbAttributes("fake-authority-2")), new EquivalentAddressGroup(new FakeSocketAddress("not-a-lb-address")), new EquivalentAddressGroup(new FakeSocketAddress("fake-address-3"), GrpclbLoadBalancerTest.lbAttributes("fake-authority-1")));
        final EquivalentAddressGroup goldenOobChannelEag = new EquivalentAddressGroup(Arrays.<SocketAddress>asList(new FakeSocketAddress("fake-address-1"), new FakeSocketAddress("fake-address-3")), GrpclbLoadBalancerTest.lbAttributes("fake-authority-1"));// Supporting multiple authorities would be good, one day

        Attributes grpclbResolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        Mockito.verify(helper).createOobChannel(goldenOobChannelEag, "fake-authority-1");
    }

    @Test
    public void grpclbBalancerStreamClosedAndRetried() throws Exception {
        LoadBalanceRequest expectedInitialRequest = LoadBalanceRequest.newBuilder().setInitialRequest(InitialLoadBalanceRequest.newBuilder().setName(GrpclbLoadBalancerTest.SERVICE_AUTHORITY).build()).build();
        InOrder inOrder = Mockito.inOrder(mockLbService, backoffPolicyProvider, backoffPolicy1, backoffPolicy2, helper);
        List<EquivalentAddressGroup> grpclbResolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(true);
        Attributes grpclbResolutionAttrs = Attributes.EMPTY;
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        Assert.assertEquals(1, fakeOobChannels.size());
        @SuppressWarnings("unused")
        ManagedChannel oobChannel = fakeOobChannels.poll();
        // First balancer RPC
        inOrder.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        StreamObserver<LoadBalanceResponse> lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        StreamObserver<LoadBalanceRequest> lbRequestObserver = lbRequestObservers.poll();
        Mockito.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(expectedInitialRequest));
        Assert.assertEquals(0, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LB_RPC_RETRY_TASK_FILTER));
        // Balancer closes it immediately (erroneously)
        lbResponseObserver.onCompleted();
        // Will start backoff sequence 1 (10ns)
        inOrder.verify(backoffPolicyProvider).get();
        inOrder.verify(backoffPolicy1).nextBackoffNanos();
        Assert.assertEquals(1, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LB_RPC_RETRY_TASK_FILTER));
        inOrder.verify(helper).refreshNameResolution();
        // Fast-forward to a moment before the retry
        fakeClock.forwardNanos(9);
        Mockito.verifyNoMoreInteractions(mockLbService);
        // Then time for retry
        fakeClock.forwardNanos(1);
        inOrder.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        lbRequestObserver = lbRequestObservers.poll();
        Mockito.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(expectedInitialRequest));
        Assert.assertEquals(0, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LB_RPC_RETRY_TASK_FILTER));
        // Balancer closes it with an error.
        lbResponseObserver.onError(UNAVAILABLE.asException());
        // Will continue the backoff sequence 1 (100ns)
        Mockito.verifyNoMoreInteractions(backoffPolicyProvider);
        inOrder.verify(backoffPolicy1).nextBackoffNanos();
        Assert.assertEquals(1, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LB_RPC_RETRY_TASK_FILTER));
        inOrder.verify(helper).refreshNameResolution();
        // Fast-forward to a moment before the retry
        fakeClock.forwardNanos((100 - 1));
        Mockito.verifyNoMoreInteractions(mockLbService);
        // Then time for retry
        fakeClock.forwardNanos(1);
        inOrder.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        lbRequestObserver = lbRequestObservers.poll();
        Mockito.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(expectedInitialRequest));
        Assert.assertEquals(0, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LB_RPC_RETRY_TASK_FILTER));
        // Balancer sends initial response.
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse());
        // Then breaks the RPC
        lbResponseObserver.onError(UNAVAILABLE.asException());
        // Will reset the retry sequence and retry immediately, because balancer has responded.
        inOrder.verify(backoffPolicyProvider).get();
        inOrder.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        lbRequestObserver = lbRequestObservers.poll();
        Mockito.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(expectedInitialRequest));
        inOrder.verify(helper).refreshNameResolution();
        // Fail the retry after spending 4ns
        fakeClock.forwardNanos(4);
        lbResponseObserver.onError(UNAVAILABLE.asException());
        // Will be on the first retry (10ns) of backoff sequence 2.
        inOrder.verify(backoffPolicy2).nextBackoffNanos();
        Assert.assertEquals(1, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LB_RPC_RETRY_TASK_FILTER));
        inOrder.verify(helper).refreshNameResolution();
        // Fast-forward to a moment before the retry, the time spent in the last try is deducted.
        fakeClock.forwardNanos(((10 - 4) - 1));
        Mockito.verifyNoMoreInteractions(mockLbService);
        // Then time for retry
        fakeClock.forwardNanos(1);
        inOrder.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        Assert.assertEquals(1, lbRequestObservers.size());
        lbRequestObserver = lbRequestObservers.poll();
        Mockito.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(expectedInitialRequest));
        Assert.assertEquals(0, fakeClock.numPendingTasks(GrpclbLoadBalancerTest.LB_RPC_RETRY_TASK_FILTER));
        // Wrapping up
        Mockito.verify(backoffPolicyProvider, Mockito.times(2)).get();
        Mockito.verify(backoffPolicy1, Mockito.times(2)).nextBackoffNanos();
        Mockito.verify(backoffPolicy2, Mockito.times(1)).nextBackoffNanos();
        Mockito.verify(helper, Mockito.times(4)).refreshNameResolution();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void grpclbWorking_pickFirstMode() throws Exception {
        InOrder inOrder = Mockito.inOrder(helper);
        String lbConfig = "{\"childPolicy\" : [ {\"pick_first\" : {}} ]}";
        List<EquivalentAddressGroup> grpclbResolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(true);
        Attributes grpclbResolutionAttrs = Attributes.newBuilder().set(ATTR_LOAD_BALANCING_CONFIG, GrpclbLoadBalancerTest.parseJsonObject(lbConfig)).build();
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        Assert.assertEquals(1, fakeOobChannels.size());
        Mockito.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        StreamObserver<LoadBalanceResponse> lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        StreamObserver<LoadBalanceRequest> lbRequestObserver = lbRequestObservers.poll();
        Mockito.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(LoadBalanceRequest.newBuilder().setInitialRequest(InitialLoadBalanceRequest.newBuilder().setName(GrpclbLoadBalancerTest.SERVICE_AUTHORITY).build()).build()));
        // Simulate receiving LB response
        List<GrpclbLoadBalancerTest.ServerEntry> backends1 = Arrays.asList(new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2000, "token0001"), new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2010, "token0002"));
        inOrder.verify(helper, Mockito.never()).updateBalancingState(ArgumentMatchers.any(ConnectivityState.class), ArgumentMatchers.any(SubchannelPicker.class));
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse());
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildLbResponse(backends1));
        inOrder.verify(helper).createSubchannel(ArgumentMatchers.eq(Arrays.asList(new EquivalentAddressGroup(backends1.get(0).addr, GrpclbLoadBalancerTest.eagAttrsWithToken("token0001")), new EquivalentAddressGroup(backends1.get(1).addr, GrpclbLoadBalancerTest.eagAttrsWithToken("token0002")))), ArgumentMatchers.any(Attributes.class));
        // Initially IDLE
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.IDLE), pickerCaptor.capture());
        RoundRobinPicker picker0 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        // Only one subchannel is created
        assertThat(mockSubchannels).hasSize(1);
        Subchannel subchannel = mockSubchannels.poll();
        assertThat(picker0.dropList).containsExactly(null, null);
        assertThat(picker0.pickList).containsExactly(new IdleSubchannelEntry(subchannel));
        // PICK_FIRST doesn't eagerly connect
        Mockito.verify(subchannel, Mockito.never()).requestConnection();
        // CONNECTING
        deliverSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.CONNECTING));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), pickerCaptor.capture());
        RoundRobinPicker picker1 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker1.dropList).containsExactly(null, null);
        assertThat(picker1.pickList).containsExactly(GrpclbState.BUFFER_ENTRY);
        // TRANSIENT_FAILURE
        Status error = UNAVAILABLE.withDescription("Simulated connection error");
        deliverSubchannelState(subchannel, ConnectivityStateInfo.forTransientFailure(error));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.TRANSIENT_FAILURE), pickerCaptor.capture());
        RoundRobinPicker picker2 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker2.dropList).containsExactly(null, null);
        assertThat(picker2.pickList).containsExactly(new io.grpc.grpclb.GrpclbState.ErrorEntry(error));
        // READY
        deliverSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        RoundRobinPicker picker3 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker3.dropList).containsExactly(null, null);
        assertThat(picker3.pickList).containsExactly(new BackendEntry(subchannel, new TokenAttachingTracerFactory(getLoadRecorder())));
        // New server list with drops
        List<GrpclbLoadBalancerTest.ServerEntry> backends2 = // drop
        Arrays.asList(new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2000, "token0001"), new GrpclbLoadBalancerTest.ServerEntry("token0003"), new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2020, "token0004"));
        inOrder.verify(helper, Mockito.never()).updateBalancingState(ArgumentMatchers.any(ConnectivityState.class), ArgumentMatchers.any(SubchannelPicker.class));
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildLbResponse(backends2));
        // new addresses will be updated to the existing subchannel
        // createSubchannel() has ever been called only once
        Mockito.verify(helper, Mockito.times(1)).createSubchannel(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Attributes.class));
        assertThat(mockSubchannels).isEmpty();
        inOrder.verify(helper).updateSubchannelAddresses(ArgumentMatchers.same(subchannel), ArgumentMatchers.eq(Arrays.asList(new EquivalentAddressGroup(backends2.get(0).addr, GrpclbLoadBalancerTest.eagAttrsWithToken("token0001")), new EquivalentAddressGroup(backends2.get(2).addr, GrpclbLoadBalancerTest.eagAttrsWithToken("token0004")))));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        RoundRobinPicker picker4 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker4.dropList).containsExactly(null, new DropEntry(getLoadRecorder(), "token0003"), null);
        assertThat(picker4.pickList).containsExactly(new BackendEntry(subchannel, new TokenAttachingTracerFactory(getLoadRecorder())));
        // Subchannel goes IDLE, but PICK_FIRST will not try to reconnect
        deliverSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.IDLE));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.IDLE), pickerCaptor.capture());
        RoundRobinPicker picker5 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        Mockito.verify(subchannel, Mockito.never()).requestConnection();
        // ... until it's selected
        PickSubchannelArgs args = Mockito.mock(PickSubchannelArgs.class);
        PickResult pick = picker5.pickSubchannel(args);
        assertThat(pick).isSameAs(PickResult.withNoResult());
        Mockito.verify(subchannel).requestConnection();
        // ... or requested by application
        picker5.requestConnection();
        Mockito.verify(subchannel, Mockito.times(2)).requestConnection();
        // PICK_FIRST doesn't use subchannelPool
        Mockito.verify(subchannelPool, Mockito.never()).takeOrCreateSubchannel(ArgumentMatchers.any(EquivalentAddressGroup.class), ArgumentMatchers.any(Attributes.class));
        Mockito.verify(subchannelPool, Mockito.never()).returnSubchannel(ArgumentMatchers.any(Subchannel.class), ArgumentMatchers.any(ConnectivityStateInfo.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void pickFirstMode_fallback() throws Exception {
        InOrder inOrder = Mockito.inOrder(helper);
        String lbConfig = "{\"childPolicy\" : [ {\"pick_first\" : {}} ]}";
        // Name resolver returns a mix of balancer and backend addresses
        List<EquivalentAddressGroup> grpclbResolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(false, true, false);
        Attributes grpclbResolutionAttrs = Attributes.newBuilder().set(ATTR_LOAD_BALANCING_CONFIG, GrpclbLoadBalancerTest.parseJsonObject(lbConfig)).build();
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        // Attempted to connect to balancer
        Assert.assertEquals(1, fakeOobChannels.size());
        Mockito.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        StreamObserver<LoadBalanceResponse> lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        // Fallback timer expires with no response
        fakeClock.forwardTime(FALLBACK_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        // Entering fallback mode
        inOrder.verify(helper).createSubchannel(ArgumentMatchers.eq(Arrays.asList(grpclbResolutionList.get(0), grpclbResolutionList.get(2))), ArgumentMatchers.any(Attributes.class));
        assertThat(mockSubchannels).hasSize(1);
        Subchannel subchannel = mockSubchannels.poll();
        // Initially IDLE
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.IDLE), pickerCaptor.capture());
        RoundRobinPicker picker0 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        // READY
        deliverSubchannelState(subchannel, ConnectivityStateInfo.forNonError(ConnectivityState.READY));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        RoundRobinPicker picker1 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker1.dropList).containsExactly(null, null);
        assertThat(picker1.pickList).containsExactly(new BackendEntry(subchannel, new TokenAttachingTracerFactory(null)));
        assertThat(picker0.dropList).containsExactly(null, null);
        assertThat(picker0.pickList).containsExactly(new IdleSubchannelEntry(subchannel));
        // Finally, an LB response, which brings us out of fallback
        List<GrpclbLoadBalancerTest.ServerEntry> backends1 = Arrays.asList(new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2000, "token0001"), new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2010, "token0002"));
        inOrder.verify(helper, Mockito.never()).updateBalancingState(ArgumentMatchers.any(ConnectivityState.class), ArgumentMatchers.any(SubchannelPicker.class));
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse());
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildLbResponse(backends1));
        // new addresses will be updated to the existing subchannel
        // createSubchannel() has ever been called only once
        Mockito.verify(helper, Mockito.times(1)).createSubchannel(ArgumentMatchers.any(List.class), ArgumentMatchers.any(Attributes.class));
        assertThat(mockSubchannels).isEmpty();
        inOrder.verify(helper).updateSubchannelAddresses(ArgumentMatchers.same(subchannel), ArgumentMatchers.eq(Arrays.asList(new EquivalentAddressGroup(backends1.get(0).addr, GrpclbLoadBalancerTest.eagAttrsWithToken("token0001")), new EquivalentAddressGroup(backends1.get(1).addr, GrpclbLoadBalancerTest.eagAttrsWithToken("token0002")))));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.READY), pickerCaptor.capture());
        RoundRobinPicker picker2 = ((RoundRobinPicker) (pickerCaptor.getValue()));
        assertThat(picker2.dropList).containsExactly(null, null);
        assertThat(picker2.pickList).containsExactly(new BackendEntry(subchannel, new TokenAttachingTracerFactory(getLoadRecorder())));
        // PICK_FIRST doesn't use subchannelPool
        Mockito.verify(subchannelPool, Mockito.never()).takeOrCreateSubchannel(ArgumentMatchers.any(EquivalentAddressGroup.class), ArgumentMatchers.any(Attributes.class));
        Mockito.verify(subchannelPool, Mockito.never()).returnSubchannel(ArgumentMatchers.any(Subchannel.class), ArgumentMatchers.any(ConnectivityStateInfo.class));
    }

    @Test
    public void switchMode() throws Exception {
        InOrder inOrder = Mockito.inOrder(helper);
        String lbConfig = "{\"childPolicy\" : [ {\"round_robin\" : {}} ]}";
        List<EquivalentAddressGroup> grpclbResolutionList = GrpclbLoadBalancerTest.createResolvedServerAddresses(true);
        Attributes grpclbResolutionAttrs = Attributes.newBuilder().set(ATTR_LOAD_BALANCING_CONFIG, GrpclbLoadBalancerTest.parseJsonObject(lbConfig)).build();
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        Assert.assertEquals(1, fakeOobChannels.size());
        ManagedChannel oobChannel = fakeOobChannels.poll();
        Mockito.verify(mockLbService).balanceLoad(lbResponseObserverCaptor.capture());
        StreamObserver<LoadBalanceResponse> lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        StreamObserver<LoadBalanceRequest> lbRequestObserver = lbRequestObservers.poll();
        Mockito.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(LoadBalanceRequest.newBuilder().setInitialRequest(InitialLoadBalanceRequest.newBuilder().setName(GrpclbLoadBalancerTest.SERVICE_AUTHORITY).build()).build()));
        // Simulate receiving LB response
        List<GrpclbLoadBalancerTest.ServerEntry> backends1 = Arrays.asList(new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2000, "token0001"), new GrpclbLoadBalancerTest.ServerEntry("127.0.0.1", 2010, "token0002"));
        inOrder.verify(helper, Mockito.never()).updateBalancingState(ArgumentMatchers.any(ConnectivityState.class), ArgumentMatchers.any(SubchannelPicker.class));
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse());
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildLbResponse(backends1));
        // ROUND_ROBIN: create one subchannel per server
        Mockito.verify(subchannelPool).takeOrCreateSubchannel(ArgumentMatchers.eq(new EquivalentAddressGroup(backends1.get(0).addr, GrpclbLoadBalancerTest.LB_BACKEND_ATTRS)), ArgumentMatchers.any(Attributes.class));
        Mockito.verify(subchannelPool).takeOrCreateSubchannel(ArgumentMatchers.eq(new EquivalentAddressGroup(backends1.get(1).addr, GrpclbLoadBalancerTest.LB_BACKEND_ATTRS)), ArgumentMatchers.any(Attributes.class));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.CONNECTING), ArgumentMatchers.any(SubchannelPicker.class));
        Assert.assertEquals(2, mockSubchannels.size());
        Subchannel subchannel1 = mockSubchannels.poll();
        Subchannel subchannel2 = mockSubchannels.poll();
        Mockito.verify(subchannelPool, Mockito.never()).returnSubchannel(ArgumentMatchers.any(Subchannel.class), ArgumentMatchers.any(ConnectivityStateInfo.class));
        // Switch to PICK_FIRST
        lbConfig = "{\"childPolicy\" : [ {\"pick_first\" : {}} ]}";
        grpclbResolutionAttrs = Attributes.newBuilder().set(ATTR_LOAD_BALANCING_CONFIG, GrpclbLoadBalancerTest.parseJsonObject(lbConfig)).build();
        deliverResolvedAddresses(grpclbResolutionList, grpclbResolutionAttrs);
        // GrpclbState will be shutdown, and a new one will be created
        assertThat(oobChannel.isShutdown()).isTrue();
        Mockito.verify(subchannelPool).returnSubchannel(ArgumentMatchers.same(subchannel1), ArgumentMatchers.eq(ConnectivityStateInfo.forNonError(ConnectivityState.IDLE)));
        Mockito.verify(subchannelPool).returnSubchannel(ArgumentMatchers.same(subchannel2), ArgumentMatchers.eq(ConnectivityStateInfo.forNonError(ConnectivityState.IDLE)));
        // A new LB stream is created
        Assert.assertEquals(1, fakeOobChannels.size());
        Mockito.verify(mockLbService, Mockito.times(2)).balanceLoad(lbResponseObserverCaptor.capture());
        lbResponseObserver = lbResponseObserverCaptor.getValue();
        Assert.assertEquals(1, lbRequestObservers.size());
        lbRequestObserver = lbRequestObservers.poll();
        Mockito.verify(lbRequestObserver).onNext(ArgumentMatchers.eq(LoadBalanceRequest.newBuilder().setInitialRequest(InitialLoadBalanceRequest.newBuilder().setName(GrpclbLoadBalancerTest.SERVICE_AUTHORITY).build()).build()));
        // Simulate receiving LB response
        inOrder.verify(helper, Mockito.never()).updateBalancingState(ArgumentMatchers.any(ConnectivityState.class), ArgumentMatchers.any(SubchannelPicker.class));
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildInitialResponse());
        lbResponseObserver.onNext(GrpclbLoadBalancerTest.buildLbResponse(backends1));
        // PICK_FIRST Subchannel
        inOrder.verify(helper).createSubchannel(ArgumentMatchers.eq(Arrays.asList(new EquivalentAddressGroup(backends1.get(0).addr, GrpclbLoadBalancerTest.eagAttrsWithToken("token0001")), new EquivalentAddressGroup(backends1.get(1).addr, GrpclbLoadBalancerTest.eagAttrsWithToken("token0002")))), ArgumentMatchers.any(Attributes.class));
        inOrder.verify(helper).updateBalancingState(ArgumentMatchers.eq(ConnectivityState.IDLE), ArgumentMatchers.any(SubchannelPicker.class));
    }

    @Test
    public void retrieveModeFromLbConfig_pickFirst() throws Exception {
        String lbConfig = "{\"childPolicy\" : [{\"pick_first\" : {}}, {\"round_robin\" : {}}]}";
        Mode mode = GrpclbLoadBalancer.retrieveModeFromLbConfig(GrpclbLoadBalancerTest.parseJsonObject(lbConfig), channelLogger);
        assertThat(logs).isEmpty();
        assertThat(mode).isEqualTo(PICK_FIRST);
    }

    @Test
    public void retrieveModeFromLbConfig_roundRobin() throws Exception {
        String lbConfig = "{\"childPolicy\" : [{\"round_robin\" : {}}, {\"pick_first\" : {}}]}";
        Mode mode = GrpclbLoadBalancer.retrieveModeFromLbConfig(GrpclbLoadBalancerTest.parseJsonObject(lbConfig), channelLogger);
        assertThat(logs).isEmpty();
        assertThat(mode).isEqualTo(ROUND_ROBIN);
    }

    @Test
    public void retrieveModeFromLbConfig_nullConfigUseRoundRobin() throws Exception {
        Mode mode = GrpclbLoadBalancer.retrieveModeFromLbConfig(null, channelLogger);
        assertThat(logs).isEmpty();
        assertThat(mode).isEqualTo(ROUND_ROBIN);
    }

    @Test
    public void retrieveModeFromLbConfig_emptyConfigUseRoundRobin() throws Exception {
        String lbConfig = "{}";
        Mode mode = GrpclbLoadBalancer.retrieveModeFromLbConfig(GrpclbLoadBalancerTest.parseJsonObject(lbConfig), channelLogger);
        assertThat(logs).isEmpty();
        assertThat(mode).isEqualTo(ROUND_ROBIN);
    }

    @Test
    public void retrieveModeFromLbConfig_emptyChildPolicyUseRoundRobin() throws Exception {
        String lbConfig = "{\"childPolicy\" : []}";
        Mode mode = GrpclbLoadBalancer.retrieveModeFromLbConfig(GrpclbLoadBalancerTest.parseJsonObject(lbConfig), channelLogger);
        assertThat(logs).isEmpty();
        assertThat(mode).isEqualTo(ROUND_ROBIN);
    }

    @Test
    public void retrieveModeFromLbConfig_unsupportedChildPolicyUseRoundRobin() throws Exception {
        String lbConfig = "{\"childPolicy\" : [ {\"nonono\" : {}} ]}";
        Mode mode = GrpclbLoadBalancer.retrieveModeFromLbConfig(GrpclbLoadBalancerTest.parseJsonObject(lbConfig), channelLogger);
        assertThat(logs).containsExactly("DEBUG: grpclb ignoring unsupported child policy nonono");
        assertThat(mode).isEqualTo(ROUND_ROBIN);
    }

    @Test
    public void retrieveModeFromLbConfig_skipUnsupportedChildPolicy() throws Exception {
        String lbConfig = "{\"childPolicy\" : [ {\"nono\" : {}}, {\"pick_first\" : {} } ]}";
        Mode mode = GrpclbLoadBalancer.retrieveModeFromLbConfig(GrpclbLoadBalancerTest.parseJsonObject(lbConfig), channelLogger);
        assertThat(logs).containsExactly("DEBUG: grpclb ignoring unsupported child policy nono");
        assertThat(mode).isEqualTo(PICK_FIRST);
    }

    @Test
    public void retrieveModeFromLbConfig_badConfigDefaultToRoundRobin() throws Exception {
        String lbConfig = "{\"childPolicy\" : {}}";
        Mode mode = GrpclbLoadBalancer.retrieveModeFromLbConfig(GrpclbLoadBalancerTest.parseJsonObject(lbConfig), channelLogger);
        assertThat(logs).containsExactly("WARNING: Bad grpclb config, using ROUND_ROBIN");
        assertThat(mode).isEqualTo(ROUND_ROBIN);
    }

    private static class ServerEntry {
        final InetSocketAddress addr;

        final String token;

        ServerEntry(String host, int port, String token) {
            this.addr = new InetSocketAddress(host, port);
            this.token = token;
        }

        // Drop entry
        ServerEntry(String token) {
            this.addr = null;
            this.token = token;
        }
    }
}

