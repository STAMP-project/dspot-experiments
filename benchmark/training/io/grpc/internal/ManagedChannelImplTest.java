/**
 * Copyright 2015 The gRPC Authors
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
package io.grpc.internal;


import Attributes.EMPTY;
import Attributes.Key;
import CallCredentials.MetadataApplier;
import CallOptions.DEFAULT;
import ChannelTrace.Event.Severity.CT_INFO;
import ChannelTrace.Event.Severity.CT_WARNING;
import ClientStreamTracer.Factory;
import ConnectivityState.CONNECTING;
import GrpcAttributes.NAME_RESOLVER_SERVICE_CONFIG;
import GrpcUtil.CALL_OPTIONS_RPC_OWNED_BY_BALANCER;
import LoadBalancer.ATTR_LOAD_BALANCING_CONFIG;
import ManagedChannelImpl.DelayedNameResolverRefresh;
import ManagedChannelImpl.SHUTDOWN_STATUS;
import ManagedChannelImpl.SUBCHANNEL_SHUTDOWN_DELAY_SECONDS;
import ManagedChannelImpl.SUBCHANNEL_SHUTDOWN_STATUS;
import ManagedChannelImpl.URI_PATTERN;
import ManagedClientTransport.Listener;
import MethodType.UNKNOWN;
import NameResolver.Factory.PARAMS_DEFAULT_PORT;
import NameResolver.Factory.PARAMS_PROXY_DETECTOR;
import SecurityLevel.NONE;
import Status.CANCELLED;
import Status.DEADLINE_EXCEEDED;
import Status.UNAVAILABLE;
import SubchannelChannel.NOT_READY_ERROR;
import SubchannelChannel.WAIT_FOR_READY_ERROR;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.truth.Truth;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.grpc.Attributes;
import io.grpc.BinaryLog;
import io.grpc.CallCredentials;
import io.grpc.CallCredentials.RequestInfo;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ChannelLogger;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ClientStreamTracer;
import io.grpc.ConnectivityState;
import io.grpc.ConnectivityStateInfo;
import io.grpc.Context;
import io.grpc.EquivalentAddressGroup;
import io.grpc.IntegerMarshaller;
import io.grpc.InternalChannelz;
import io.grpc.InternalChannelz.ChannelTrace;
import io.grpc.LoadBalancer;
import io.grpc.LoadBalancer.Helper;
import io.grpc.LoadBalancer.PickResult;
import io.grpc.LoadBalancer.PickSubchannelArgs;
import io.grpc.LoadBalancer.Subchannel;
import io.grpc.LoadBalancer.SubchannelPicker;
import io.grpc.LoadBalancerProvider;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.NameResolver;
import io.grpc.NameResolver.Helper.ConfigOrError;
import io.grpc.ProxiedSocketAddress;
import io.grpc.ProxyDetector;
import io.grpc.ServerMethodDefinition;
import io.grpc.Status;
import io.grpc.StringMarshaller;
import io.grpc.SynchronizationContext;
import io.grpc.internal.ClientTransportFactory.ClientTransportOptions;
import io.grpc.internal.InternalSubchannel.TransportLogger;
import io.grpc.internal.ManagedChannelImpl.NrHelper;
import io.grpc.stub.ClientCalls;
import io.grpc.testing.TestMethodDescriptors;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import static ManagedChannelImpl.SUBCHANNEL_SHUTDOWN_DELAY_SECONDS;
import static NameResolver.Factory.<init>;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;


/**
 * Unit tests for {@link ManagedChannelImpl}.
 */
@RunWith(JUnit4.class)
public class ManagedChannelImplTest {
    private static final int DEFAULT_PORT = 447;

    private static final MethodDescriptor<String, Integer> method = MethodDescriptor.<String, Integer>newBuilder().setType(UNKNOWN).setFullMethodName("service/method").setRequestMarshaller(new StringMarshaller()).setResponseMarshaller(new IntegerMarshaller()).build();

    private static final Attributes.Key<String> SUBCHANNEL_ATTR_KEY = Key.create("subchannel-attr-key");

    private static final long RECONNECT_BACKOFF_INTERVAL_NANOS = 10;

    private static final String SERVICE_NAME = "fake.example.com";

    private static final String AUTHORITY = ManagedChannelImplTest.SERVICE_NAME;

    private static final String USER_AGENT = "userAgent";

    private static final ClientTransportOptions clientTransportOptions = new ClientTransportOptions().setAuthority(ManagedChannelImplTest.AUTHORITY).setUserAgent(ManagedChannelImplTest.USER_AGENT);

    private static final String TARGET = "fake://" + (ManagedChannelImplTest.SERVICE_NAME);

    private static final String MOCK_POLICY_NAME = "mock_lb";

    private URI expectedUri;

    private final SocketAddress socketAddress = new SocketAddress() {
        @Override
        public String toString() {
            return "test-addr";
        }
    };

    private final EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(socketAddress);

    private final FakeClock timer = new FakeClock();

    private final FakeClock executor = new FakeClock();

    private final FakeClock balancerRpcExecutor = new FakeClock();

    private static final FakeClock.TaskFilter NAME_RESOLVER_REFRESH_TASK_FILTER = new FakeClock.TaskFilter() {
        @Override
        public boolean shouldAccept(Runnable command) {
            return command.toString().contains(DelayedNameResolverRefresh.class.getName());
        }
    };

    private final InternalChannelz channelz = new InternalChannelz();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    private ManagedChannelImpl channel;

    private Helper helper;

    @Captor
    private ArgumentCaptor<Status> statusCaptor;

    @Captor
    private ArgumentCaptor<CallOptions> callOptionsCaptor;

    @Mock
    private LoadBalancer mockLoadBalancer;

    private final LoadBalancerProvider mockLoadBalancerProvider = Mockito.mock(LoadBalancerProvider.class, AdditionalAnswers.delegatesTo(new LoadBalancerProvider() {
        @Override
        public LoadBalancer newLoadBalancer(Helper helper) {
            return mockLoadBalancer;
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public int getPriority() {
            return 999;
        }

        @Override
        public String getPolicyName() {
            return ManagedChannelImplTest.MOCK_POLICY_NAME;
        }
    }));

    @Captor
    private ArgumentCaptor<ConnectivityStateInfo> stateInfoCaptor;

    @Mock
    private SubchannelPicker mockPicker;

    @Mock
    private ClientTransportFactory mockTransportFactory;

    @Mock
    private ClientCall.Listener<Integer> mockCallListener;

    @Mock
    private ClientCall.Listener<Integer> mockCallListener2;

    @Mock
    private ClientCall.Listener<Integer> mockCallListener3;

    @Mock
    private ClientCall.Listener<Integer> mockCallListener4;

    @Mock
    private ClientCall.Listener<Integer> mockCallListener5;

    @Mock
    private ObjectPool<Executor> executorPool;

    @Mock
    private ObjectPool<Executor> balancerRpcExecutorPool;

    @Mock
    private CallCredentials creds;

    private ManagedChannelImplTest.ChannelBuilder channelBuilder;

    private boolean requestConnection = true;

    private BlockingQueue<TestUtils.MockClientTransportInfo> transports;

    private ArgumentCaptor<ClientStreamListener> streamListenerCaptor = ArgumentCaptor.forClass(ClientStreamListener.class);

    @Test
    public void createSubchannelOutsideSynchronizationContextShouldLogWarning() {
        createChannel();
        final AtomicReference<LogRecord> logRef = new AtomicReference<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                logRef.set(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        Logger logger = Logger.getLogger(ManagedChannelImpl.class.getName());
        try {
            logger.addHandler(handler);
            helper.createSubchannel(addressGroup, EMPTY);
            LogRecord record = logRef.get();
            assertThat(record.getLevel()).isEqualTo(Level.WARNING);
            assertThat(record.getMessage()).contains("We sugguest you call createSubchannel() from SynchronizationContext");
            assertThat(record.getThrown()).isInstanceOf(IllegalStateException.class);
        } finally {
            logger.removeHandler(handler);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void idleModeDisabled() {
        channelBuilder.nameResolverFactory(new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(socketAddress))).build());
        createChannel();
        // In this test suite, the channel is always created with idle mode disabled.
        // No task is scheduled to enter idle mode
        Assert.assertEquals(0, timer.numPendingTasks());
        Assert.assertEquals(0, executor.numPendingTasks());
    }

    @Test
    public void immediateDeadlineExceeded() {
        createChannel();
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT.withDeadlineAfter(0, TimeUnit.NANOSECONDS));
        call.start(mockCallListener, new Metadata());
        Assert.assertEquals(1, executor.runDueTasks());
        Mockito.verify(mockCallListener).onClose(statusCaptor.capture(), ArgumentMatchers.any(Metadata.class));
        Status status = statusCaptor.getValue();
        Assert.assertSame(DEADLINE_EXCEEDED.getCode(), status.getCode());
    }

    @Test
    public void shutdownWithNoTransportsEverCreated() {
        channelBuilder.nameResolverFactory(new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(socketAddress))).build());
        createChannel();
        Mockito.verify(executorPool).getObject();
        Mockito.verify(executorPool, Mockito.never()).returnObject(ArgumentMatchers.anyObject());
        channel.shutdown();
        Assert.assertTrue(channel.isShutdown());
        Assert.assertTrue(channel.isTerminated());
        Mockito.verify(executorPool).returnObject(executor.getScheduledExecutorService());
    }

    @Test
    public void channelzMembership() throws Exception {
        createChannel();
        Assert.assertNotNull(channelz.getRootChannel(channel.getLogId().getId()));
        Assert.assertFalse(channelz.containsSubchannel(channel.getLogId()));
        channel.shutdownNow();
        channel.awaitTermination(5, TimeUnit.SECONDS);
        Assert.assertNull(channelz.getRootChannel(channel.getLogId().getId()));
        Assert.assertFalse(channelz.containsSubchannel(channel.getLogId()));
    }

    @Test
    public void channelzMembership_subchannel() throws Exception {
        createChannel();
        Assert.assertNotNull(channelz.getRootChannel(channel.getLogId().getId()));
        AbstractSubchannel subchannel = ((AbstractSubchannel) (ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY)));
        // subchannels are not root channels
        Assert.assertNull(channelz.getRootChannel(subchannel.getInternalSubchannel().getLogId().getId()));
        Assert.assertTrue(channelz.containsSubchannel(subchannel.getInternalSubchannel().getLogId()));
        assertThat(ManagedChannelImplTest.getStats(channel).subchannels).containsExactly(subchannel.getInternalSubchannel());
        subchannel.requestConnection();
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        Assert.assertNotNull(transportInfo);
        Assert.assertTrue(channelz.containsClientSocket(transportInfo.transport.getLogId()));
        // terminate transport
        transportInfo.listener.transportTerminated();
        Assert.assertFalse(channelz.containsClientSocket(transportInfo.transport.getLogId()));
        // terminate subchannel
        Assert.assertTrue(channelz.containsSubchannel(subchannel.getInternalSubchannel().getLogId()));
        subchannel.shutdown();
        timer.forwardTime(SUBCHANNEL_SHUTDOWN_DELAY_SECONDS, TimeUnit.SECONDS);
        timer.runDueTasks();
        Assert.assertFalse(channelz.containsSubchannel(subchannel.getInternalSubchannel().getLogId()));
        assertThat(ManagedChannelImplTest.getStats(channel).subchannels).isEmpty();
        // channel still appears
        Assert.assertNotNull(channelz.getRootChannel(channel.getLogId().getId()));
    }

    @Test
    public void channelzMembership_oob() throws Exception {
        createChannel();
        OobChannel oob = ((OobChannel) (helper.createOobChannel(addressGroup, ManagedChannelImplTest.AUTHORITY)));
        // oob channels are not root channels
        Assert.assertNull(channelz.getRootChannel(oob.getLogId().getId()));
        Assert.assertTrue(channelz.containsSubchannel(oob.getLogId()));
        assertThat(ManagedChannelImplTest.getStats(channel).subchannels).containsExactly(oob);
        Assert.assertTrue(channelz.containsSubchannel(oob.getLogId()));
        AbstractSubchannel subchannel = ((AbstractSubchannel) (oob.getSubchannel()));
        Assert.assertTrue(channelz.containsSubchannel(subchannel.getInternalSubchannel().getLogId()));
        assertThat(ManagedChannelImplTest.getStats(oob).subchannels).containsExactly(subchannel.getInternalSubchannel());
        Assert.assertTrue(channelz.containsSubchannel(subchannel.getInternalSubchannel().getLogId()));
        oob.getSubchannel().requestConnection();
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        Assert.assertNotNull(transportInfo);
        Assert.assertTrue(channelz.containsClientSocket(transportInfo.transport.getLogId()));
        // terminate transport
        transportInfo.listener.transportTerminated();
        Assert.assertFalse(channelz.containsClientSocket(transportInfo.transport.getLogId()));
        // terminate oobchannel
        oob.shutdown();
        Assert.assertFalse(channelz.containsSubchannel(oob.getLogId()));
        assertThat(ManagedChannelImplTest.getStats(channel).subchannels).isEmpty();
        Assert.assertFalse(channelz.containsSubchannel(subchannel.getInternalSubchannel().getLogId()));
        // channel still appears
        Assert.assertNotNull(channelz.getRootChannel(channel.getLogId().getId()));
    }

    @Test
    public void callsAndShutdown() {
        subtestCallsAndShutdown(false, false);
    }

    @Test
    public void callsAndShutdownNow() {
        subtestCallsAndShutdown(true, false);
    }

    /**
     * Make sure shutdownNow() after shutdown() has an effect.
     */
    @Test
    public void callsAndShutdownAndShutdownNow() {
        subtestCallsAndShutdown(false, true);
    }

    @Test
    public void noMoreCallbackAfterLoadBalancerShutdown() {
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(socketAddress))).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        Status resolutionError = UNAVAILABLE.withDescription("Resolution failed");
        createChannel();
        ManagedChannelImplTest.FakeNameResolverFactory.FakeNameResolver resolver = nameResolverFactory.resolvers.get(0);
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(ArgumentMatchers.any(Helper.class));
        Mockito.verify(mockLoadBalancer).handleResolvedAddressGroups(ArgumentMatchers.eq(Arrays.asList(addressGroup)), ArgumentMatchers.eq(EMPTY));
        Subchannel subchannel1 = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Subchannel subchannel2 = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        subchannel1.requestConnection();
        subchannel2.requestConnection();
        Mockito.verify(mockTransportFactory, Mockito.times(2)).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        TestUtils.MockClientTransportInfo transportInfo1 = transports.poll();
        TestUtils.MockClientTransportInfo transportInfo2 = transports.poll();
        // LoadBalancer receives all sorts of callbacks
        transportInfo1.listener.transportReady();
        Mockito.verify(mockLoadBalancer, Mockito.times(2)).handleSubchannelState(ArgumentMatchers.same(subchannel1), stateInfoCaptor.capture());
        Assert.assertSame(ConnectivityState.CONNECTING, stateInfoCaptor.getAllValues().get(0).getState());
        Assert.assertSame(ConnectivityState.READY, stateInfoCaptor.getAllValues().get(1).getState());
        Mockito.verify(mockLoadBalancer).handleSubchannelState(ArgumentMatchers.same(subchannel2), stateInfoCaptor.capture());
        Assert.assertSame(ConnectivityState.CONNECTING, stateInfoCaptor.getValue().getState());
        resolver.listener.onError(resolutionError);
        Mockito.verify(mockLoadBalancer).handleNameResolutionError(resolutionError);
        Mockito.verifyNoMoreInteractions(mockLoadBalancer);
        channel.shutdown();
        Mockito.verify(mockLoadBalancer).shutdown();
        // No more callback should be delivered to LoadBalancer after it's shut down
        transportInfo2.listener.transportReady();
        resolver.listener.onError(resolutionError);
        resolver.resolved();
        Mockito.verifyNoMoreInteractions(mockLoadBalancer);
    }

    @Test
    public void interceptor() throws Exception {
        final AtomicLong atomic = new AtomicLong();
        ClientInterceptor interceptor = new ClientInterceptor() {
            @Override
            public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> interceptCall(MethodDescriptor<RequestT, ResponseT> method, CallOptions callOptions, Channel next) {
                atomic.set(1);
                return next.newCall(method, callOptions);
            }
        };
        createChannel(interceptor);
        Assert.assertNotNull(channel.newCall(ManagedChannelImplTest.method, DEFAULT));
        Assert.assertEquals(1, atomic.get());
    }

    @Test
    public void callOptionsExecutor() {
        Metadata headers = new Metadata();
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        FakeClock callExecutor = new FakeClock();
        createChannel();
        // Start a call with a call executor
        CallOptions options = DEFAULT.withExecutor(callExecutor.getScheduledExecutorService());
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, options);
        call.start(mockCallListener, headers);
        // Make the transport available
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Mockito.verify(mockTransportFactory, Mockito.never()).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        subchannel.requestConnection();
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        ConnectionClientTransport mockTransport = transportInfo.transport;
        ManagedClientTransport.Listener transportListener = transportInfo.listener;
        Mockito.when(mockTransport.newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.same(headers), ArgumentMatchers.any(CallOptions.class))).thenReturn(mockStream);
        transportListener.transportReady();
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel));
        Assert.assertEquals(0, callExecutor.numPendingTasks());
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        // Real streams are started in the call executor if they were previously buffered.
        Assert.assertEquals(1, callExecutor.runDueTasks());
        Mockito.verify(mockTransport).newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.same(headers), ArgumentMatchers.same(options));
        Mockito.verify(mockStream).start(streamListenerCaptor.capture());
        // Call listener callbacks are also run in the call executor
        ClientStreamListener streamListener = streamListenerCaptor.getValue();
        Metadata trailers = new Metadata();
        Assert.assertEquals(0, callExecutor.numPendingTasks());
        streamListener.closed(CANCELLED, trailers);
        Mockito.verify(mockCallListener, Mockito.never()).onClose(ArgumentMatchers.same(CANCELLED), ArgumentMatchers.same(trailers));
        Assert.assertEquals(1, callExecutor.runDueTasks());
        Mockito.verify(mockCallListener).onClose(ArgumentMatchers.same(CANCELLED), ArgumentMatchers.same(trailers));
        transportListener.transportShutdown(UNAVAILABLE);
        transportListener.transportTerminated();
        // Clean up as much as possible to allow the channel to terminate.
        subchannel.shutdown();
        timer.forwardNanos(TimeUnit.SECONDS.toNanos(SUBCHANNEL_SHUTDOWN_DELAY_SECONDS));
    }

    @Test
    public void nameResolutionFailed() {
        Status error = UNAVAILABLE.withCause(new Throwable("fake name resolution error"));
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(socketAddress))).setError(error).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        // Name resolution is started as soon as channel is created.
        createChannel();
        ManagedChannelImplTest.FakeNameResolverFactory.FakeNameResolver resolver = nameResolverFactory.resolvers.get(0);
        Mockito.verify(mockLoadBalancer).handleNameResolutionError(ArgumentMatchers.same(error));
        Assert.assertEquals(1, timer.numPendingTasks(ManagedChannelImplTest.NAME_RESOLVER_REFRESH_TASK_FILTER));
        timer.forwardNanos(((ManagedChannelImplTest.RECONNECT_BACKOFF_INTERVAL_NANOS) - 1));
        Assert.assertEquals(0, resolver.refreshCalled);
        timer.forwardNanos(1);
        Assert.assertEquals(1, resolver.refreshCalled);
        Mockito.verify(mockLoadBalancer, Mockito.times(2)).handleNameResolutionError(ArgumentMatchers.same(error));
        // Verify an additional name resolution failure does not schedule another timer
        resolver.refresh();
        Mockito.verify(mockLoadBalancer, Mockito.times(3)).handleNameResolutionError(ArgumentMatchers.same(error));
        Assert.assertEquals(1, timer.numPendingTasks(ManagedChannelImplTest.NAME_RESOLVER_REFRESH_TASK_FILTER));
        // Allow the next refresh attempt to succeed
        resolver.error = null;
        // For the second attempt, the backoff should occur at RECONNECT_BACKOFF_INTERVAL_NANOS * 2
        timer.forwardNanos((((ManagedChannelImplTest.RECONNECT_BACKOFF_INTERVAL_NANOS) * 2) - 1));
        Assert.assertEquals(2, resolver.refreshCalled);
        timer.forwardNanos(1);
        Assert.assertEquals(3, resolver.refreshCalled);
        Assert.assertEquals(0, timer.numPendingTasks());
        // Verify that the successful resolution reset the backoff policy
        resolver.listener.onError(error);
        timer.forwardNanos(((ManagedChannelImplTest.RECONNECT_BACKOFF_INTERVAL_NANOS) - 1));
        Assert.assertEquals(3, resolver.refreshCalled);
        timer.forwardNanos(1);
        Assert.assertEquals(4, resolver.refreshCalled);
        Assert.assertEquals(0, timer.numPendingTasks());
    }

    @Test
    public void nameResolutionFailed_delayedTransportShutdownCancelsBackoff() {
        Status error = UNAVAILABLE.withCause(new Throwable("fake name resolution error"));
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setError(error).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        // Name resolution is started as soon as channel is created.
        createChannel();
        Mockito.verify(mockLoadBalancer).handleNameResolutionError(ArgumentMatchers.same(error));
        FakeClock.ScheduledTask nameResolverBackoff = getNameResolverRefresh();
        Assert.assertNotNull(nameResolverBackoff);
        Assert.assertFalse(nameResolverBackoff.isCancelled());
        // Add a pending call to the delayed transport
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        Metadata headers = new Metadata();
        call.start(mockCallListener, headers);
        // The pending call on the delayed transport stops the name resolver backoff from cancelling
        channel.shutdown();
        Assert.assertFalse(nameResolverBackoff.isCancelled());
        // Notify that a subchannel is ready, which drains the delayed transport
        SubchannelPicker picker = Mockito.mock(SubchannelPicker.class);
        Status status = UNAVAILABLE.withDescription("for test");
        Mockito.when(picker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withDrop(status));
        helper.updateBalancingState(ConnectivityState.READY, picker);
        executor.runDueTasks();
        Mockito.verify(mockCallListener).onClose(ArgumentMatchers.same(status), ArgumentMatchers.any(Metadata.class));
        Assert.assertTrue(nameResolverBackoff.isCancelled());
    }

    @Test
    public void nameResolverReturnsEmptySubLists_becomeErrorByDefault() throws Exception {
        String errorDescription = "Name resolver returned no usable address";
        // Pass a FakeNameResolverFactory with an empty list and LB config
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).build();
        Map<String, Object> serviceConfig = ManagedChannelImplTest.parseConfig("{\"loadBalancingConfig\": [ {\"mock_lb\": { \"setting1\": \"high\" } } ] }");
        Attributes serviceConfigAttrs = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        nameResolverFactory.nextResolvedAttributes.set(serviceConfigAttrs);
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        // LoadBalancer received the error
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(ArgumentMatchers.any(Helper.class));
        Mockito.verify(mockLoadBalancer).handleNameResolutionError(statusCaptor.capture());
        Status status = statusCaptor.getValue();
        Assert.assertSame(Status.Code.UNAVAILABLE, status.getCode());
        Truth.assertThat(status.getDescription()).startsWith(errorDescription);
    }

    @Test
    public void nameResolverReturnsEmptySubLists_optionallyAllowed() throws Exception {
        Mockito.when(mockLoadBalancer.canHandleEmptyAddressListFromNameResolution()).thenReturn(true);
        // Pass a FakeNameResolverFactory with an empty list and LB config
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).build();
        Map<String, Object> serviceConfig = ManagedChannelImplTest.parseConfig("{\"loadBalancingConfig\": [ {\"mock_lb\": { \"setting1\": \"high\" } } ] }");
        Attributes serviceConfigAttrs = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        nameResolverFactory.nextResolvedAttributes.set(serviceConfigAttrs);
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        // LoadBalancer received the empty list and the LB config
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(ArgumentMatchers.any(Helper.class));
        ArgumentCaptor<Attributes> attrsCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockLoadBalancer).handleResolvedAddressGroups(ArgumentMatchers.eq(ImmutableList.<EquivalentAddressGroup>of()), attrsCaptor.capture());
        Map<String, ?> lbConfig = attrsCaptor.getValue().get(ATTR_LOAD_BALANCING_CONFIG);
        Assert.assertEquals(ImmutableMap.<String, String>of("setting1", "high"), lbConfig);
        Assert.assertSame(serviceConfig, attrsCaptor.getValue().get(NAME_RESOLVER_SERVICE_CONFIG));
    }

    @Test
    @Deprecated
    public void nameResolverReturnsEmptySubLists_becomeErrorByDefault_lbFactorySetDirectly() throws Exception {
        String errorDescription = "returned an empty list";
        // Pass a FakeNameResolverFactory with an empty list and LB config
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).build();
        Map<String, Object> serviceConfig = ManagedChannelImplTest.parseConfig("{\"loadBalancingConfig\": [ {\"mock_lb\": { \"setting1\": \"high\" } } ] }");
        Attributes serviceConfigAttrs = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        nameResolverFactory.nextResolvedAttributes.set(serviceConfigAttrs);
        channelBuilder.nameResolverFactory(nameResolverFactory);
        // Pass a LoadBalancerFactory directly to the builder, bypassing
        // AutoConfiguredLoadBalancerFactory.  The empty-list check is done in ManagedChannelImpl rather
        // than AutoConfiguredLoadBalancerFactory
        channelBuilder.loadBalancerFactory(mockLoadBalancerProvider);
        createChannel();
        // LoadBalancer received the error
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(ArgumentMatchers.any(Helper.class));
        Mockito.verify(mockLoadBalancer).handleNameResolutionError(statusCaptor.capture());
        Status status = statusCaptor.getValue();
        Assert.assertSame(Status.Code.UNAVAILABLE, status.getCode());
        Truth.assertThat(status.getDescription()).contains(errorDescription);
    }

    @Test
    @Deprecated
    public void nameResolverReturnsEmptySubLists_optionallyAllowed_lbFactorySetDirectly() throws Exception {
        Mockito.when(mockLoadBalancer.canHandleEmptyAddressListFromNameResolution()).thenReturn(true);
        // Pass a FakeNameResolverFactory with an empty list and LB config
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).build();
        Map<String, Object> serviceConfig = ManagedChannelImplTest.parseConfig("{\"loadBalancingConfig\": [ {\"mock_lb\": { \"setting1\": \"high\" } } ] }");
        Attributes serviceConfigAttrs = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        nameResolverFactory.nextResolvedAttributes.set(serviceConfigAttrs);
        channelBuilder.nameResolverFactory(nameResolverFactory);
        // Pass a LoadBalancerFactory directly to the builder, bypassing
        // AutoConfiguredLoadBalancerFactory.  The empty-list check is done in ManagedChannelImpl rather
        // than AutoConfiguredLoadBalancerFactory
        channelBuilder.loadBalancerFactory(mockLoadBalancerProvider);
        createChannel();
        // LoadBalancer received the empty list and the LB config
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(ArgumentMatchers.any(Helper.class));
        Mockito.verify(mockLoadBalancer).handleResolvedAddressGroups(ArgumentMatchers.eq(ImmutableList.<EquivalentAddressGroup>of()), ArgumentMatchers.same(serviceConfigAttrs));
    }

    @Test
    public void loadBalancerThrowsInHandleResolvedAddresses() {
        RuntimeException ex = new RuntimeException("simulated");
        // Delay the success of name resolution until allResolved() is called
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setResolvedAtStart(false).setServers(Collections.singletonList(new EquivalentAddressGroup(socketAddress))).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(ArgumentMatchers.any(Helper.class));
        Mockito.doThrow(ex).when(mockLoadBalancer).handleResolvedAddressGroups(Matchers.<List<EquivalentAddressGroup>>anyObject(), ArgumentMatchers.any(Attributes.class));
        // NameResolver returns addresses.
        nameResolverFactory.allResolved();
        // Exception thrown from balancer is caught by ChannelExecutor, making channel enter panic mode.
        verifyPanicMode(ex);
    }

    @Test
    public void nameResolvedAfterChannelShutdown() {
        // Delay the success of name resolution until allResolved() is called.
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setResolvedAtStart(false).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        channel.shutdown();
        Assert.assertTrue(channel.isShutdown());
        Assert.assertTrue(channel.isTerminated());
        Mockito.verify(mockLoadBalancer).shutdown();
        // Name resolved after the channel is shut down, which is possible if the name resolution takes
        // time and is not cancellable. The resolved address will be dropped.
        nameResolverFactory.allResolved();
        Mockito.verifyNoMoreInteractions(mockLoadBalancer);
    }

    /**
     * Verify that if the first resolved address points to a server that cannot be connected, the call
     * will end up with the second address which works.
     */
    @Test
    public void firstResolvedServerFailedToConnect() throws Exception {
        final SocketAddress goodAddress = new SocketAddress() {
            @Override
            public String toString() {
                return "goodAddress";
            }
        };
        final SocketAddress badAddress = new SocketAddress() {
            @Override
            public String toString() {
                return "badAddress";
            }
        };
        InOrder inOrder = Mockito.inOrder(mockLoadBalancer);
        List<SocketAddress> resolvedAddrs = Arrays.asList(badAddress, goodAddress);
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(resolvedAddrs))).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        // Start the call
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        Metadata headers = new Metadata();
        call.start(mockCallListener, headers);
        executor.runDueTasks();
        // Simulate name resolution results
        EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(resolvedAddrs);
        inOrder.verify(mockLoadBalancer).handleResolvedAddressGroups(ArgumentMatchers.eq(Arrays.asList(addressGroup)), ArgumentMatchers.eq(EMPTY));
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel));
        subchannel.requestConnection();
        inOrder.verify(mockLoadBalancer).handleSubchannelState(ArgumentMatchers.same(subchannel), stateInfoCaptor.capture());
        Assert.assertEquals(ConnectivityState.CONNECTING, stateInfoCaptor.getValue().getState());
        // The channel will starts with the first address (badAddress)
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.same(badAddress), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        Mockito.verify(mockTransportFactory, Mockito.times(0)).newClientTransport(ArgumentMatchers.same(goodAddress), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        TestUtils.MockClientTransportInfo badTransportInfo = transports.poll();
        // Which failed to connect
        badTransportInfo.listener.transportShutdown(UNAVAILABLE);
        inOrder.verifyNoMoreInteractions();
        // The channel then try the second address (goodAddress)
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.same(goodAddress), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        TestUtils.MockClientTransportInfo goodTransportInfo = transports.poll();
        Mockito.when(goodTransportInfo.transport.newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class))).thenReturn(Mockito.mock(ClientStream.class));
        goodTransportInfo.listener.transportReady();
        inOrder.verify(mockLoadBalancer).handleSubchannelState(ArgumentMatchers.same(subchannel), stateInfoCaptor.capture());
        Assert.assertEquals(ConnectivityState.READY, stateInfoCaptor.getValue().getState());
        // A typical LoadBalancer will call this once the subchannel becomes READY
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        // Delayed transport uses the app executor to create real streams.
        executor.runDueTasks();
        Mockito.verify(goodTransportInfo.transport).newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.same(headers), ArgumentMatchers.same(DEFAULT));
        // The bad transport was never used.
        Mockito.verify(badTransportInfo.transport, Mockito.times(0)).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
    }

    @Test
    public void failFastRpcFailFromErrorFromBalancer() {
        subtestFailRpcFromBalancer(false, false, true);
    }

    @Test
    public void failFastRpcFailFromDropFromBalancer() {
        subtestFailRpcFromBalancer(false, true, true);
    }

    @Test
    public void waitForReadyRpcImmuneFromErrorFromBalancer() {
        subtestFailRpcFromBalancer(true, false, false);
    }

    @Test
    public void waitForReadyRpcFailFromDropFromBalancer() {
        subtestFailRpcFromBalancer(true, true, true);
    }

    /**
     * Verify that if all resolved addresses failed to connect, a fail-fast call will fail, while a
     * wait-for-ready call will still be buffered.
     */
    @Test
    public void allServersFailedToConnect() throws Exception {
        final SocketAddress addr1 = new SocketAddress() {
            @Override
            public String toString() {
                return "addr1";
            }
        };
        final SocketAddress addr2 = new SocketAddress() {
            @Override
            public String toString() {
                return "addr2";
            }
        };
        InOrder inOrder = Mockito.inOrder(mockLoadBalancer);
        List<SocketAddress> resolvedAddrs = Arrays.asList(addr1, addr2);
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(resolvedAddrs))).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        // Start a wait-for-ready call
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT.withWaitForReady());
        Metadata headers = new Metadata();
        call.start(mockCallListener, headers);
        // ... and a fail-fast call
        ClientCall<String, Integer> call2 = channel.newCall(ManagedChannelImplTest.method, DEFAULT.withoutWaitForReady());
        call2.start(mockCallListener2, headers);
        executor.runDueTasks();
        // Simulate name resolution results
        EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(resolvedAddrs);
        inOrder.verify(mockLoadBalancer).handleResolvedAddressGroups(ArgumentMatchers.eq(Arrays.asList(addressGroup)), ArgumentMatchers.eq(EMPTY));
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel));
        subchannel.requestConnection();
        inOrder.verify(mockLoadBalancer).handleSubchannelState(ArgumentMatchers.same(subchannel), stateInfoCaptor.capture());
        Assert.assertEquals(ConnectivityState.CONNECTING, stateInfoCaptor.getValue().getState());
        // Connecting to server1, which will fail
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.same(addr1), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        Mockito.verify(mockTransportFactory, Mockito.times(0)).newClientTransport(ArgumentMatchers.same(addr2), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        TestUtils.MockClientTransportInfo transportInfo1 = transports.poll();
        transportInfo1.listener.transportShutdown(UNAVAILABLE);
        // Connecting to server2, which will fail too
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.same(addr2), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        TestUtils.MockClientTransportInfo transportInfo2 = transports.poll();
        Status server2Error = UNAVAILABLE.withDescription("Server2 failed to connect");
        transportInfo2.listener.transportShutdown(server2Error);
        // ... which makes the subchannel enter TRANSIENT_FAILURE. The last error Status is propagated
        // to LoadBalancer.
        inOrder.verify(mockLoadBalancer).handleSubchannelState(ArgumentMatchers.same(subchannel), stateInfoCaptor.capture());
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, stateInfoCaptor.getValue().getState());
        Assert.assertSame(server2Error, stateInfoCaptor.getValue().getStatus());
        // A typical LoadBalancer would create a picker with error
        SubchannelPicker picker2 = Mockito.mock(SubchannelPicker.class);
        Mockito.when(picker2.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withError(server2Error));
        helper.updateBalancingState(ConnectivityState.TRANSIENT_FAILURE, picker2);
        executor.runDueTasks();
        // ... which fails the fail-fast call
        Mockito.verify(mockCallListener2).onClose(ArgumentMatchers.same(server2Error), ArgumentMatchers.any(Metadata.class));
        // ... while the wait-for-ready call stays
        Mockito.verifyNoMoreInteractions(mockCallListener);
        // No real stream was ever created
        Mockito.verify(transportInfo1.transport, Mockito.times(0)).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        Mockito.verify(transportInfo2.transport, Mockito.times(0)).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
    }

    @Test
    public void subchannels() {
        createChannel();
        // createSubchannel() always return a new Subchannel
        Attributes attrs1 = Attributes.newBuilder().set(io.grpc.internal.SUBCHANNEL_ATTR_KEY, "attr1").build();
        Attributes attrs2 = Attributes.newBuilder().set(io.grpc.internal.SUBCHANNEL_ATTR_KEY, "attr2").build();
        Subchannel sub1 = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, attrs1);
        Subchannel sub2 = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, attrs2);
        TestCase.assertNotSame(sub1, sub2);
        TestCase.assertNotSame(attrs1, attrs2);
        Assert.assertSame(attrs1, sub1.getAttributes());
        Assert.assertSame(attrs2, sub2.getAttributes());
        Assert.assertSame(addressGroup, sub1.getAddresses());
        Assert.assertSame(addressGroup, sub2.getAddresses());
        // requestConnection()
        Mockito.verify(mockTransportFactory, Mockito.never()).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(TransportLogger.class));
        sub1.requestConnection();
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(socketAddress), ArgumentMatchers.eq(ManagedChannelImplTest.clientTransportOptions), ArgumentMatchers.isA(TransportLogger.class));
        TestUtils.MockClientTransportInfo transportInfo1 = transports.poll();
        Assert.assertNotNull(transportInfo1);
        sub2.requestConnection();
        Mockito.verify(mockTransportFactory, Mockito.times(2)).newClientTransport(ArgumentMatchers.eq(socketAddress), ArgumentMatchers.eq(ManagedChannelImplTest.clientTransportOptions), ArgumentMatchers.isA(TransportLogger.class));
        TestUtils.MockClientTransportInfo transportInfo2 = transports.poll();
        Assert.assertNotNull(transportInfo2);
        sub1.requestConnection();
        sub2.requestConnection();
        // The subchannel doesn't matter since this isn't called
        Mockito.verify(mockTransportFactory, Mockito.times(2)).newClientTransport(ArgumentMatchers.eq(socketAddress), ArgumentMatchers.eq(ManagedChannelImplTest.clientTransportOptions), ArgumentMatchers.isA(TransportLogger.class));
        // shutdown() has a delay
        sub1.shutdown();
        timer.forwardTime(((SUBCHANNEL_SHUTDOWN_DELAY_SECONDS) - 1), TimeUnit.SECONDS);
        sub1.shutdown();
        Mockito.verify(transportInfo1.transport, Mockito.never()).shutdown(ArgumentMatchers.any(Status.class));
        timer.forwardTime(1, TimeUnit.SECONDS);
        Mockito.verify(transportInfo1.transport).shutdown(ArgumentMatchers.same(SUBCHANNEL_SHUTDOWN_STATUS));
        // ... but not after Channel is terminating
        Mockito.verify(mockLoadBalancer, Mockito.never()).shutdown();
        channel.shutdown();
        Mockito.verify(mockLoadBalancer).shutdown();
        Mockito.verify(transportInfo2.transport, Mockito.never()).shutdown(ArgumentMatchers.any(Status.class));
        sub2.shutdown();
        Mockito.verify(transportInfo2.transport).shutdown(ArgumentMatchers.same(SHUTDOWN_STATUS));
        // Cleanup
        transportInfo1.listener.transportShutdown(UNAVAILABLE);
        transportInfo1.listener.transportTerminated();
        transportInfo2.listener.transportShutdown(UNAVAILABLE);
        transportInfo2.listener.transportTerminated();
        timer.forwardTime(SUBCHANNEL_SHUTDOWN_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    @Test
    public void subchannelsWhenChannelShutdownNow() {
        createChannel();
        Subchannel sub1 = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Subchannel sub2 = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        sub1.requestConnection();
        sub2.requestConnection();
        assertThat(transports).hasSize(2);
        TestUtils.MockClientTransportInfo ti1 = transports.poll();
        TestUtils.MockClientTransportInfo ti2 = transports.poll();
        ti1.listener.transportReady();
        ti2.listener.transportReady();
        channel.shutdownNow();
        Mockito.verify(ti1.transport).shutdownNow(ArgumentMatchers.any(Status.class));
        Mockito.verify(ti2.transport).shutdownNow(ArgumentMatchers.any(Status.class));
        ti1.listener.transportShutdown(UNAVAILABLE.withDescription("shutdown now"));
        ti2.listener.transportShutdown(UNAVAILABLE.withDescription("shutdown now"));
        ti1.listener.transportTerminated();
        Assert.assertFalse(channel.isTerminated());
        ti2.listener.transportTerminated();
        Assert.assertTrue(channel.isTerminated());
    }

    @Test
    public void subchannelsNoConnectionShutdown() {
        createChannel();
        Subchannel sub1 = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Subchannel sub2 = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        channel.shutdown();
        Mockito.verify(mockLoadBalancer).shutdown();
        sub1.shutdown();
        Assert.assertFalse(channel.isTerminated());
        sub2.shutdown();
        Assert.assertTrue(channel.isTerminated());
        Mockito.verify(mockTransportFactory, Mockito.never()).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
    }

    @Test
    public void subchannelsNoConnectionShutdownNow() {
        createChannel();
        ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        channel.shutdownNow();
        Mockito.verify(mockLoadBalancer).shutdown();
        // Channel's shutdownNow() will call shutdownNow() on all subchannels and oobchannels.
        // Therefore, channel is terminated without relying on LoadBalancer to shutdown subchannels.
        Assert.assertTrue(channel.isTerminated());
        Mockito.verify(mockTransportFactory, Mockito.never()).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
    }

    @Test
    public void oobchannels() {
        createChannel();
        ManagedChannel oob1 = helper.createOobChannel(addressGroup, "oob1authority");
        ManagedChannel oob2 = helper.createOobChannel(addressGroup, "oob2authority");
        Mockito.verify(balancerRpcExecutorPool, Mockito.times(2)).getObject();
        Assert.assertEquals("oob1authority", oob1.authority());
        Assert.assertEquals("oob2authority", oob2.authority());
        // OOB channels create connections lazily.  A new call will initiate the connection.
        Metadata headers = new Metadata();
        ClientCall<String, Integer> call = oob1.newCall(ManagedChannelImplTest.method, DEFAULT);
        call.start(mockCallListener, headers);
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(socketAddress), ArgumentMatchers.eq(new ClientTransportOptions().setAuthority("oob1authority").setUserAgent(ManagedChannelImplTest.USER_AGENT)), ArgumentMatchers.isA(ChannelLogger.class));
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        Assert.assertNotNull(transportInfo);
        Assert.assertEquals(0, balancerRpcExecutor.numPendingTasks());
        transportInfo.listener.transportReady();
        Assert.assertEquals(1, balancerRpcExecutor.runDueTasks());
        Mockito.verify(transportInfo.transport).newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.same(headers), ArgumentMatchers.same(DEFAULT));
        // The transport goes away
        transportInfo.listener.transportShutdown(UNAVAILABLE);
        transportInfo.listener.transportTerminated();
        // A new call will trigger a new transport
        ClientCall<String, Integer> call2 = oob1.newCall(ManagedChannelImplTest.method, DEFAULT);
        call2.start(mockCallListener2, headers);
        ClientCall<String, Integer> call3 = oob1.newCall(ManagedChannelImplTest.method, DEFAULT.withWaitForReady());
        call3.start(mockCallListener3, headers);
        Mockito.verify(mockTransportFactory, Mockito.times(2)).newClientTransport(ArgumentMatchers.eq(socketAddress), ArgumentMatchers.eq(new ClientTransportOptions().setAuthority("oob1authority").setUserAgent(ManagedChannelImplTest.USER_AGENT)), ArgumentMatchers.isA(ChannelLogger.class));
        transportInfo = transports.poll();
        Assert.assertNotNull(transportInfo);
        // This transport fails
        Status transportError = UNAVAILABLE.withDescription("Connection refused");
        Assert.assertEquals(0, balancerRpcExecutor.numPendingTasks());
        transportInfo.listener.transportShutdown(transportError);
        Assert.assertTrue(((balancerRpcExecutor.runDueTasks()) > 0));
        // Fail-fast RPC will fail, while wait-for-ready RPC will still be pending
        Mockito.verify(mockCallListener2).onClose(ArgumentMatchers.same(transportError), ArgumentMatchers.any(Metadata.class));
        Mockito.verify(mockCallListener3, Mockito.never()).onClose(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
        // Shutdown
        Assert.assertFalse(oob1.isShutdown());
        Assert.assertFalse(oob2.isShutdown());
        oob1.shutdown();
        oob2.shutdownNow();
        Assert.assertTrue(oob1.isShutdown());
        Assert.assertTrue(oob2.isShutdown());
        Assert.assertTrue(oob2.isTerminated());
        Mockito.verify(balancerRpcExecutorPool).returnObject(balancerRpcExecutor.getScheduledExecutorService());
        // New RPCs will be rejected.
        Assert.assertEquals(0, balancerRpcExecutor.numPendingTasks());
        ClientCall<String, Integer> call4 = oob1.newCall(ManagedChannelImplTest.method, DEFAULT);
        ClientCall<String, Integer> call5 = oob2.newCall(ManagedChannelImplTest.method, DEFAULT);
        call4.start(mockCallListener4, headers);
        call5.start(mockCallListener5, headers);
        Assert.assertTrue(((balancerRpcExecutor.runDueTasks()) > 0));
        Mockito.verify(mockCallListener4).onClose(statusCaptor.capture(), ArgumentMatchers.any(Metadata.class));
        Status status4 = statusCaptor.getValue();
        Assert.assertEquals(Status.Code.UNAVAILABLE, status4.getCode());
        Mockito.verify(mockCallListener5).onClose(statusCaptor.capture(), ArgumentMatchers.any(Metadata.class));
        Status status5 = statusCaptor.getValue();
        Assert.assertEquals(Status.Code.UNAVAILABLE, status5.getCode());
        // The pending RPC will still be pending
        Mockito.verify(mockCallListener3, Mockito.never()).onClose(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
        // This will shutdownNow() the delayed transport, terminating the pending RPC
        Assert.assertEquals(0, balancerRpcExecutor.numPendingTasks());
        oob1.shutdownNow();
        Assert.assertTrue(((balancerRpcExecutor.runDueTasks()) > 0));
        Mockito.verify(mockCallListener3).onClose(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
        // Shut down the channel, and it will not terminated because OOB channel has not.
        channel.shutdown();
        Assert.assertFalse(channel.isTerminated());
        // Delayed transport has already terminated.  Terminating the transport terminates the
        // subchannel, which in turn terimates the OOB channel, which terminates the channel.
        Assert.assertFalse(oob1.isTerminated());
        Mockito.verify(balancerRpcExecutorPool).returnObject(balancerRpcExecutor.getScheduledExecutorService());
        transportInfo.listener.transportTerminated();
        Assert.assertTrue(oob1.isTerminated());
        Assert.assertTrue(channel.isTerminated());
        Mockito.verify(balancerRpcExecutorPool, Mockito.times(2)).returnObject(balancerRpcExecutor.getScheduledExecutorService());
    }

    @Test
    public void oobChannelsWhenChannelShutdownNow() {
        createChannel();
        ManagedChannel oob1 = helper.createOobChannel(addressGroup, "oob1Authority");
        ManagedChannel oob2 = helper.createOobChannel(addressGroup, "oob2Authority");
        oob1.newCall(ManagedChannelImplTest.method, DEFAULT).start(mockCallListener, new Metadata());
        oob2.newCall(ManagedChannelImplTest.method, DEFAULT).start(mockCallListener2, new Metadata());
        assertThat(transports).hasSize(2);
        TestUtils.MockClientTransportInfo ti1 = transports.poll();
        TestUtils.MockClientTransportInfo ti2 = transports.poll();
        ti1.listener.transportReady();
        ti2.listener.transportReady();
        channel.shutdownNow();
        Mockito.verify(ti1.transport).shutdownNow(ArgumentMatchers.any(Status.class));
        Mockito.verify(ti2.transport).shutdownNow(ArgumentMatchers.any(Status.class));
        ti1.listener.transportShutdown(UNAVAILABLE.withDescription("shutdown now"));
        ti2.listener.transportShutdown(UNAVAILABLE.withDescription("shutdown now"));
        ti1.listener.transportTerminated();
        Assert.assertFalse(channel.isTerminated());
        ti2.listener.transportTerminated();
        Assert.assertTrue(channel.isTerminated());
    }

    @Test
    public void oobChannelsNoConnectionShutdown() {
        createChannel();
        ManagedChannel oob1 = helper.createOobChannel(addressGroup, "oob1Authority");
        ManagedChannel oob2 = helper.createOobChannel(addressGroup, "oob2Authority");
        channel.shutdown();
        Mockito.verify(mockLoadBalancer).shutdown();
        oob1.shutdown();
        Assert.assertTrue(oob1.isTerminated());
        Assert.assertFalse(channel.isTerminated());
        oob2.shutdown();
        Assert.assertTrue(oob2.isTerminated());
        Assert.assertTrue(channel.isTerminated());
        Mockito.verify(mockTransportFactory, Mockito.never()).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
    }

    @Test
    public void oobChannelsNoConnectionShutdownNow() {
        createChannel();
        helper.createOobChannel(addressGroup, "oob1Authority");
        helper.createOobChannel(addressGroup, "oob2Authority");
        channel.shutdownNow();
        Mockito.verify(mockLoadBalancer).shutdown();
        Assert.assertTrue(channel.isTerminated());
        // Channel's shutdownNow() will call shutdownNow() on all subchannels and oobchannels.
        // Therefore, channel is terminated without relying on LoadBalancer to shutdown oobchannels.
        Mockito.verify(mockTransportFactory, Mockito.never()).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
    }

    @Test
    public void subchannelChannel_normalUsage() {
        createChannel();
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Mockito.verify(balancerRpcExecutorPool, Mockito.never()).getObject();
        Channel sChannel = subchannel.asChannel();
        Mockito.verify(balancerRpcExecutorPool).getObject();
        Metadata headers = new Metadata();
        CallOptions callOptions = DEFAULT.withDeadlineAfter(5, TimeUnit.SECONDS);
        // Subchannel must be READY when creating the RPC.
        subchannel.requestConnection();
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        ConnectionClientTransport mockTransport = transportInfo.transport;
        ManagedClientTransport.Listener transportListener = transportInfo.listener;
        transportListener.transportReady();
        ClientCall<String, Integer> call = sChannel.newCall(ManagedChannelImplTest.method, callOptions);
        call.start(mockCallListener, headers);
        Mockito.verify(mockTransport).newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.same(headers), callOptionsCaptor.capture());
        CallOptions capturedCallOption = callOptionsCaptor.getValue();
        assertThat(capturedCallOption.getDeadline()).isSameAs(callOptions.getDeadline());
        assertThat(capturedCallOption.getOption(CALL_OPTIONS_RPC_OWNED_BY_BALANCER)).isTrue();
    }

    @Test
    public void subchannelChannel_failWhenNotReady() {
        createChannel();
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Channel sChannel = subchannel.asChannel();
        Metadata headers = new Metadata();
        subchannel.requestConnection();
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        ConnectionClientTransport mockTransport = transportInfo.transport;
        Assert.assertEquals(0, balancerRpcExecutor.numPendingTasks());
        // Subchannel is still CONNECTING, but not READY yet
        ClientCall<String, Integer> call = sChannel.newCall(ManagedChannelImplTest.method, DEFAULT);
        call.start(mockCallListener, headers);
        Mockito.verify(mockTransport, Mockito.never()).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        Mockito.verifyZeroInteractions(mockCallListener);
        Assert.assertEquals(1, balancerRpcExecutor.runDueTasks());
        Mockito.verify(mockCallListener).onClose(ArgumentMatchers.same(NOT_READY_ERROR), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void subchannelChannel_failWaitForReady() {
        createChannel();
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Channel sChannel = subchannel.asChannel();
        Metadata headers = new Metadata();
        // Subchannel must be READY when creating the RPC.
        subchannel.requestConnection();
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.any(ClientTransportOptions.class), ArgumentMatchers.any(ChannelLogger.class));
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        ConnectionClientTransport mockTransport = transportInfo.transport;
        ManagedClientTransport.Listener transportListener = transportInfo.listener;
        transportListener.transportReady();
        Assert.assertEquals(0, balancerRpcExecutor.numPendingTasks());
        // Wait-for-ready RPC is not allowed
        ClientCall<String, Integer> call = sChannel.newCall(ManagedChannelImplTest.method, DEFAULT.withWaitForReady());
        call.start(mockCallListener, headers);
        Mockito.verify(mockTransport, Mockito.never()).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        Mockito.verifyZeroInteractions(mockCallListener);
        Assert.assertEquals(1, balancerRpcExecutor.runDueTasks());
        Mockito.verify(mockCallListener).onClose(ArgumentMatchers.same(WAIT_FOR_READY_ERROR), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void lbHelper_getScheduledExecutorService() {
        createChannel();
        ScheduledExecutorService ses = helper.getScheduledExecutorService();
        Runnable task = Mockito.mock(Runnable.class);
        helper.getSynchronizationContext().schedule(task, 110, TimeUnit.NANOSECONDS, ses);
        timer.forwardNanos(109);
        Mockito.verify(task, Mockito.never()).run();
        timer.forwardNanos(1);
        Mockito.verify(task).run();
        try {
            ses.shutdown();
            Assert.fail("Should throw");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            ses.shutdownNow();
            Assert.fail("Should throw");
        } catch (UnsupportedOperationException e) {
            // exepcted
        }
    }

    @Test
    public void refreshNameResolution_whenSubchannelConnectionFailed_notIdle() {
        subtestNameResolutionRefreshWhenConnectionFailed(false, false);
    }

    @Test
    public void refreshNameResolution_whenOobChannelConnectionFailed_notIdle() {
        subtestNameResolutionRefreshWhenConnectionFailed(true, false);
    }

    @Test
    public void notRefreshNameResolution_whenSubchannelConnectionFailed_idle() {
        subtestNameResolutionRefreshWhenConnectionFailed(false, true);
    }

    @Test
    public void notRefreshNameResolution_whenOobChannelConnectionFailed_idle() {
        subtestNameResolutionRefreshWhenConnectionFailed(true, true);
    }

    @Test
    public void uriPattern() {
        Assert.assertTrue(URI_PATTERN.matcher("a:/").matches());
        Assert.assertTrue(URI_PATTERN.matcher("Z019+-.:/!@ #~ ").matches());
        Assert.assertFalse(URI_PATTERN.matcher("a/:").matches());// "/:" not matched

        Assert.assertFalse(URI_PATTERN.matcher("0a:/").matches());// '0' not matched

        Assert.assertFalse(URI_PATTERN.matcher("a,:/").matches());// ',' not matched

        Assert.assertFalse(URI_PATTERN.matcher(" a:/").matches());// space not matched

    }

    /**
     * Test that information such as the Call's context, MethodDescriptor, authority, executor are
     * propagated to newStream() and applyRequestMetadata().
     */
    @Test
    public void informationPropagatedToNewStreamAndCallCredentials() {
        createChannel();
        CallOptions callOptions = DEFAULT.withCallCredentials(creds);
        final Context.Key<String> testKey = Context.key("testing");
        Context ctx = Context.current().withValue(testKey, "testValue");
        final LinkedList<Context> credsApplyContexts = new LinkedList<>();
        final LinkedList<Context> newStreamContexts = new LinkedList<>();
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock in) throws Throwable {
                credsApplyContexts.add(Context.current());
                return null;
            }
        }).when(creds).applyRequestMetadata(ArgumentMatchers.any(RequestInfo.class), ArgumentMatchers.any(Executor.class), ArgumentMatchers.any(MetadataApplier.class));
        // First call will be on delayed transport.  Only newCall() is run within the expected context,
        // so that we can verify that the context is explicitly attached before calling newStream() and
        // applyRequestMetadata(), which happens after we detach the context from the thread.
        Context origCtx = ctx.attach();
        Assert.assertEquals("testValue", testKey.get());
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, callOptions);
        ctx.detach(origCtx);
        Assert.assertNull(testKey.get());
        call.start(mockCallListener, new Metadata());
        // Simulate name resolution results
        EquivalentAddressGroup addressGroup = new EquivalentAddressGroup(socketAddress);
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        subchannel.requestConnection();
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.same(socketAddress), ArgumentMatchers.eq(ManagedChannelImplTest.clientTransportOptions), ArgumentMatchers.any(ChannelLogger.class));
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        final ConnectionClientTransport transport = transportInfo.transport;
        Mockito.when(transport.getAttributes()).thenReturn(EMPTY);
        Mockito.doAnswer(new Answer<ClientStream>() {
            @Override
            public ClientStream answer(InvocationOnMock in) throws Throwable {
                newStreamContexts.add(Context.current());
                return Mockito.mock(ClientStream.class);
            }
        }).when(transport).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        Mockito.verify(creds, Mockito.never()).applyRequestMetadata(ArgumentMatchers.any(RequestInfo.class), ArgumentMatchers.any(Executor.class), ArgumentMatchers.any(MetadataApplier.class));
        // applyRequestMetadata() is called after the transport becomes ready.
        transportInfo.listener.transportReady();
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel));
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        executor.runDueTasks();
        ArgumentCaptor<RequestInfo> infoCaptor = ArgumentCaptor.forClass(null);
        ArgumentCaptor<CallCredentials.MetadataApplier> applierCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(creds).applyRequestMetadata(infoCaptor.capture(), ArgumentMatchers.same(executor.getScheduledExecutorService()), applierCaptor.capture());
        Assert.assertEquals("testValue", testKey.get(credsApplyContexts.poll()));
        Assert.assertEquals(ManagedChannelImplTest.AUTHORITY, infoCaptor.getValue().getAuthority());
        Assert.assertEquals(NONE, infoCaptor.getValue().getSecurityLevel());
        Mockito.verify(transport, Mockito.never()).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        // newStream() is called after apply() is called
        applierCaptor.getValue().apply(new Metadata());
        Mockito.verify(transport).newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.same(callOptions));
        Assert.assertEquals("testValue", testKey.get(newStreamContexts.poll()));
        // The context should not live beyond the scope of newStream() and applyRequestMetadata()
        Assert.assertNull(testKey.get());
        // Second call will not be on delayed transport
        origCtx = ctx.attach();
        call = channel.newCall(ManagedChannelImplTest.method, callOptions);
        ctx.detach(origCtx);
        call.start(mockCallListener, new Metadata());
        Mockito.verify(creds, Mockito.times(2)).applyRequestMetadata(infoCaptor.capture(), ArgumentMatchers.same(executor.getScheduledExecutorService()), applierCaptor.capture());
        Assert.assertEquals("testValue", testKey.get(credsApplyContexts.poll()));
        Assert.assertEquals(ManagedChannelImplTest.AUTHORITY, infoCaptor.getValue().getAuthority());
        Assert.assertEquals(NONE, infoCaptor.getValue().getSecurityLevel());
        // This is from the first call
        Mockito.verify(transport).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        // Still, newStream() is called after apply() is called
        applierCaptor.getValue().apply(new Metadata());
        Mockito.verify(transport, Mockito.times(2)).newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.same(callOptions));
        Assert.assertEquals("testValue", testKey.get(newStreamContexts.poll()));
        Assert.assertNull(testKey.get());
    }

    @Test
    public void pickerReturnsStreamTracer_noDelay() {
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        ClientStreamTracer.Factory factory1 = Mockito.mock(Factory.class);
        ClientStreamTracer.Factory factory2 = Mockito.mock(Factory.class);
        createChannel();
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        subchannel.requestConnection();
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        transportInfo.listener.transportReady();
        ClientTransport mockTransport = transportInfo.transport;
        Mockito.when(mockTransport.newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class))).thenReturn(mockStream);
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel, factory2));
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        CallOptions callOptions = DEFAULT.withStreamTracerFactory(factory1);
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, callOptions);
        call.start(mockCallListener, new Metadata());
        Mockito.verify(mockPicker).pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class));
        Mockito.verify(mockTransport).newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), callOptionsCaptor.capture());
        Assert.assertEquals(Arrays.asList(factory1, factory2), callOptionsCaptor.getValue().getStreamTracerFactories());
        // The factories are safely not stubbed because we do not expect any usage of them.
        Mockito.verifyZeroInteractions(factory1);
        Mockito.verifyZeroInteractions(factory2);
    }

    @Test
    public void pickerReturnsStreamTracer_delayed() {
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        ClientStreamTracer.Factory factory1 = Mockito.mock(Factory.class);
        ClientStreamTracer.Factory factory2 = Mockito.mock(Factory.class);
        createChannel();
        CallOptions callOptions = DEFAULT.withStreamTracerFactory(factory1);
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, callOptions);
        call.start(mockCallListener, new Metadata());
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        subchannel.requestConnection();
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        transportInfo.listener.transportReady();
        ClientTransport mockTransport = transportInfo.transport;
        Mockito.when(mockTransport.newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class))).thenReturn(mockStream);
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel, factory2));
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        Assert.assertEquals(1, executor.runDueTasks());
        Mockito.verify(mockPicker).pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class));
        Mockito.verify(mockTransport).newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), callOptionsCaptor.capture());
        Assert.assertEquals(Arrays.asList(factory1, factory2), callOptionsCaptor.getValue().getStreamTracerFactories());
        // The factories are safely not stubbed because we do not expect any usage of them.
        Mockito.verifyZeroInteractions(factory1);
        Mockito.verifyZeroInteractions(factory2);
    }

    @Test
    public void getState_loadBalancerSupportsChannelState() {
        channelBuilder.nameResolverFactory(new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setResolvedAtStart(false).build());
        createChannel();
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        helper.updateBalancingState(ConnectivityState.TRANSIENT_FAILURE, mockPicker);
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, channel.getState(false));
    }

    @Test
    public void getState_withRequestConnect() {
        channelBuilder.nameResolverFactory(new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setResolvedAtStart(false).build());
        requestConnection = false;
        createChannel();
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        Mockito.verify(mockLoadBalancerProvider, Mockito.never()).newLoadBalancer(ArgumentMatchers.any(Helper.class));
        // call getState() with requestConnection = true
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(true));
        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(helperCaptor.capture());
        helper = helperCaptor.getValue();
        helper.updateBalancingState(ConnectivityState.CONNECTING, mockPicker);
        Assert.assertEquals(ConnectivityState.CONNECTING, channel.getState(false));
        Assert.assertEquals(ConnectivityState.CONNECTING, channel.getState(true));
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(ArgumentMatchers.any(Helper.class));
    }

    @Test
    public void getState_withRequestConnect_IdleWithLbRunning() {
        channelBuilder.nameResolverFactory(new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setResolvedAtStart(false).build());
        createChannel();
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(ArgumentMatchers.any(Helper.class));
        helper.updateBalancingState(ConnectivityState.IDLE, mockPicker);
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(true));
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(ArgumentMatchers.any(Helper.class));
        Mockito.verify(mockPicker).requestConnection();
    }

    @Test
    public void notifyWhenStateChanged() {
        final AtomicBoolean stateChanged = new AtomicBoolean();
        Runnable onStateChanged = new Runnable() {
            @Override
            public void run() {
                stateChanged.set(true);
            }
        };
        channelBuilder.nameResolverFactory(new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setResolvedAtStart(false).build());
        createChannel();
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        channel.notifyWhenStateChanged(ConnectivityState.IDLE, onStateChanged);
        executor.runDueTasks();
        Assert.assertFalse(stateChanged.get());
        // state change from IDLE to CONNECTING
        helper.updateBalancingState(ConnectivityState.CONNECTING, mockPicker);
        // onStateChanged callback should run
        executor.runDueTasks();
        Assert.assertTrue(stateChanged.get());
        // clear and test form CONNECTING
        stateChanged.set(false);
        channel.notifyWhenStateChanged(ConnectivityState.IDLE, onStateChanged);
        // onStateChanged callback should run immediately
        executor.runDueTasks();
        Assert.assertTrue(stateChanged.get());
    }

    @Test
    public void channelStateWhenChannelShutdown() {
        final AtomicBoolean stateChanged = new AtomicBoolean();
        Runnable onStateChanged = new Runnable() {
            @Override
            public void run() {
                stateChanged.set(true);
            }
        };
        channelBuilder.nameResolverFactory(new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setResolvedAtStart(false).build());
        createChannel();
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        channel.notifyWhenStateChanged(ConnectivityState.IDLE, onStateChanged);
        executor.runDueTasks();
        Assert.assertFalse(stateChanged.get());
        channel.shutdown();
        Assert.assertEquals(ConnectivityState.SHUTDOWN, channel.getState(false));
        executor.runDueTasks();
        Assert.assertTrue(stateChanged.get());
        stateChanged.set(false);
        channel.notifyWhenStateChanged(ConnectivityState.SHUTDOWN, onStateChanged);
        helper.updateBalancingState(ConnectivityState.CONNECTING, mockPicker);
        Assert.assertEquals(ConnectivityState.SHUTDOWN, channel.getState(false));
        executor.runDueTasks();
        Assert.assertFalse(stateChanged.get());
    }

    @Test
    public void stateIsIdleOnIdleTimeout() {
        long idleTimeoutMillis = 2000L;
        idleTimeout(idleTimeoutMillis, TimeUnit.MILLISECONDS);
        createChannel();
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        helper.updateBalancingState(ConnectivityState.CONNECTING, mockPicker);
        Assert.assertEquals(ConnectivityState.CONNECTING, channel.getState(false));
        timer.forwardNanos(TimeUnit.MILLISECONDS.toNanos(idleTimeoutMillis));
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
    }

    @Test
    public void panic_whenIdle() {
        subtestPanic(ConnectivityState.IDLE);
    }

    @Test
    public void panic_whenConnecting() {
        subtestPanic(ConnectivityState.CONNECTING);
    }

    @Test
    public void panic_whenTransientFailure() {
        subtestPanic(ConnectivityState.TRANSIENT_FAILURE);
    }

    @Test
    public void panic_whenReady() {
        subtestPanic(ConnectivityState.READY);
    }

    @Test
    public void panic_bufferedCallsWillFail() {
        createChannel();
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withNoResult());
        helper.updateBalancingState(ConnectivityState.CONNECTING, mockPicker);
        // Start RPCs that will be buffered in delayedTransport
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT.withoutWaitForReady());
        call.start(mockCallListener, new Metadata());
        ClientCall<String, Integer> call2 = channel.newCall(ManagedChannelImplTest.method, DEFAULT.withWaitForReady());
        call2.start(mockCallListener2, new Metadata());
        executor.runDueTasks();
        Mockito.verifyZeroInteractions(mockCallListener, mockCallListener2);
        // Enter panic
        final Throwable panicReason = new Exception("Simulated uncaught exception");
        channel.syncContext.execute(new Runnable() {
            @Override
            public void run() {
                channel.panic(panicReason);
            }
        });
        // Buffered RPCs fail immediately
        executor.runDueTasks();
        verifyCallListenerClosed(mockCallListener, Status.Code.INTERNAL, panicReason);
        verifyCallListenerClosed(mockCallListener2, Status.Code.INTERNAL, panicReason);
    }

    @Test
    public void idleTimeoutAndReconnect() {
        long idleTimeoutMillis = 2000L;
        idleTimeout(idleTimeoutMillis, TimeUnit.MILLISECONDS);
        createChannel();
        timer.forwardNanos(TimeUnit.MILLISECONDS.toNanos(idleTimeoutMillis));
        Assert.assertEquals(ConnectivityState.IDLE, /* request connection */
        channel.getState(true));
        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(Helper.class);
        // Two times of requesting connection will create loadBalancer twice.
        Mockito.verify(mockLoadBalancerProvider, Mockito.times(2)).newLoadBalancer(helperCaptor.capture());
        Helper helper2 = helperCaptor.getValue();
        // Updating on the old helper (whose balancer has been shutdown) does not change the channel
        // state.
        helper.updateBalancingState(ConnectivityState.CONNECTING, mockPicker);
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        helper2.updateBalancingState(ConnectivityState.CONNECTING, mockPicker);
        Assert.assertEquals(ConnectivityState.CONNECTING, channel.getState(false));
    }

    @Test
    public void idleMode_resetsDelayedTransportPicker() {
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        Status pickError = UNAVAILABLE.withDescription("pick result error");
        long idleTimeoutMillis = 1000L;
        idleTimeout(idleTimeoutMillis, TimeUnit.MILLISECONDS);
        channelBuilder.nameResolverFactory(new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(socketAddress))).build());
        createChannel();
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        // This call will be buffered in delayedTransport
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        // Move channel into TRANSIENT_FAILURE, which will fail the pending call
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withError(pickError));
        helper.updateBalancingState(ConnectivityState.TRANSIENT_FAILURE, mockPicker);
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, channel.getState(false));
        executor.runDueTasks();
        Mockito.verify(mockCallListener).onClose(ArgumentMatchers.same(pickError), ArgumentMatchers.any(Metadata.class));
        // Move channel to idle
        timer.forwardNanos(TimeUnit.MILLISECONDS.toNanos(idleTimeoutMillis));
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        // This call should be buffered, but will move the channel out of idle
        ClientCall<String, Integer> call2 = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        call2.start(mockCallListener2, new Metadata());
        executor.runDueTasks();
        Mockito.verifyNoMoreInteractions(mockCallListener2);
        // Get the helper created on exiting idle
        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(Helper.class);
        Mockito.verify(mockLoadBalancerProvider, Mockito.times(2)).newLoadBalancer(helperCaptor.capture());
        Helper helper2 = helperCaptor.getValue();
        // Establish a connection
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper2, addressGroup, EMPTY);
        subchannel.requestConnection();
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        ConnectionClientTransport mockTransport = transportInfo.transport;
        ManagedClientTransport.Listener transportListener = transportInfo.listener;
        Mockito.when(mockTransport.newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class))).thenReturn(mockStream);
        transportListener.transportReady();
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel));
        helper2.updateBalancingState(ConnectivityState.READY, mockPicker);
        Assert.assertEquals(ConnectivityState.READY, channel.getState(false));
        executor.runDueTasks();
        // Verify the buffered call was drained
        Mockito.verify(mockTransport).newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        Mockito.verify(mockStream).start(ArgumentMatchers.any(ClientStreamListener.class));
    }

    @Test
    public void enterIdleEntersIdle() {
        createChannel();
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        Assert.assertEquals(ConnectivityState.READY, channel.getState(false));
        channel.enterIdle();
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
    }

    @Test
    public void enterIdleAfterIdleTimerIsNoOp() {
        long idleTimeoutMillis = 2000L;
        idleTimeout(idleTimeoutMillis, TimeUnit.MILLISECONDS);
        createChannel();
        timer.forwardNanos(TimeUnit.MILLISECONDS.toNanos(idleTimeoutMillis));
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        channel.enterIdle();
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
    }

    @Test
    public void enterIdle_exitsIdleIfDelayedStreamPending() {
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(socketAddress))).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        // Start a call that will be buffered in delayedTransport
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        // enterIdle() will shut down the name resolver and lb policy used to get a pick for the delayed
        // call
        channel.enterIdle();
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        // enterIdle() will restart the delayed call by exiting idle. This creates a new helper.
        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(Helper.class);
        Mockito.verify(mockLoadBalancerProvider, Mockito.times(2)).newLoadBalancer(helperCaptor.capture());
        Helper helper2 = helperCaptor.getValue();
        // Establish a connection
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper2, addressGroup, EMPTY);
        subchannel.requestConnection();
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        ConnectionClientTransport mockTransport = transportInfo.transport;
        ManagedClientTransport.Listener transportListener = transportInfo.listener;
        Mockito.when(mockTransport.newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class))).thenReturn(mockStream);
        transportListener.transportReady();
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel));
        helper2.updateBalancingState(ConnectivityState.READY, mockPicker);
        Assert.assertEquals(ConnectivityState.READY, channel.getState(false));
        // Verify the original call was drained
        executor.runDueTasks();
        Mockito.verify(mockTransport).newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        Mockito.verify(mockStream).start(ArgumentMatchers.any(ClientStreamListener.class));
    }

    @Test
    public void updateBalancingStateDoesUpdatePicker() {
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        createChannel();
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        // Make the transport available with subchannel2
        Subchannel subchannel1 = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Subchannel subchannel2 = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        subchannel2.requestConnection();
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        ConnectionClientTransport mockTransport = transportInfo.transport;
        ManagedClientTransport.Listener transportListener = transportInfo.listener;
        Mockito.when(mockTransport.newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class))).thenReturn(mockStream);
        transportListener.transportReady();
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel1));
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        executor.runDueTasks();
        Mockito.verify(mockTransport, Mockito.never()).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        Mockito.verify(mockStream, Mockito.never()).start(ArgumentMatchers.any(ClientStreamListener.class));
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel2));
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        executor.runDueTasks();
        Mockito.verify(mockTransport).newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        Mockito.verify(mockStream).start(ArgumentMatchers.any(ClientStreamListener.class));
    }

    @Test
    public void updateBalancingStateWithShutdownShouldBeIgnored() {
        channelBuilder.nameResolverFactory(new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setResolvedAtStart(false).build());
        createChannel();
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        Runnable onStateChanged = Mockito.mock(Runnable.class);
        channel.notifyWhenStateChanged(ConnectivityState.IDLE, onStateChanged);
        helper.updateBalancingState(ConnectivityState.SHUTDOWN, mockPicker);
        Assert.assertEquals(ConnectivityState.IDLE, channel.getState(false));
        executor.runDueTasks();
        Mockito.verify(onStateChanged, Mockito.never()).run();
    }

    @Test
    public void balancerRefreshNameResolution() {
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        ManagedChannelImplTest.FakeNameResolverFactory.FakeNameResolver resolver = nameResolverFactory.resolvers.get(0);
        int initialRefreshCount = resolver.refreshCalled;
        helper.refreshNameResolution();
        Assert.assertEquals((initialRefreshCount + 1), resolver.refreshCalled);
    }

    @Test
    public void resetConnectBackoff() {
        // Start with a name resolution failure to trigger backoff attempts
        Status error = UNAVAILABLE.withCause(new Throwable("fake name resolution error"));
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setError(error).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        // Name resolution is started as soon as channel is created.
        createChannel();
        ManagedChannelImplTest.FakeNameResolverFactory.FakeNameResolver resolver = nameResolverFactory.resolvers.get(0);
        Mockito.verify(mockLoadBalancer).handleNameResolutionError(ArgumentMatchers.same(error));
        FakeClock.ScheduledTask nameResolverBackoff = getNameResolverRefresh();
        Assert.assertNotNull("There should be a name resolver backoff task", nameResolverBackoff);
        Assert.assertEquals(0, resolver.refreshCalled);
        // Verify resetConnectBackoff() calls refresh and cancels the scheduled backoff
        channel.resetConnectBackoff();
        Assert.assertEquals(1, resolver.refreshCalled);
        Assert.assertTrue(nameResolverBackoff.isCancelled());
        // Simulate a race between cancel and the task scheduler. Should be a no-op.
        nameResolverBackoff.command.run();
        Assert.assertEquals(1, resolver.refreshCalled);
        // Verify that the reconnect policy was recreated and the backoff multiplier reset to 1
        timer.forwardNanos(ManagedChannelImplTest.RECONNECT_BACKOFF_INTERVAL_NANOS);
        Assert.assertEquals(2, resolver.refreshCalled);
    }

    @Test
    public void resetConnectBackoff_noOpWithoutPendingResolverBackoff() {
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(socketAddress))).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        ManagedChannelImplTest.FakeNameResolverFactory.FakeNameResolver nameResolver = nameResolverFactory.resolvers.get(0);
        Assert.assertEquals(0, nameResolver.refreshCalled);
        channel.resetConnectBackoff();
        Assert.assertEquals(0, nameResolver.refreshCalled);
    }

    @Test
    public void resetConnectBackoff_noOpWhenChannelShutdown() {
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        channel.shutdown();
        Assert.assertTrue(channel.isShutdown());
        channel.resetConnectBackoff();
        ManagedChannelImplTest.FakeNameResolverFactory.FakeNameResolver nameResolver = nameResolverFactory.resolvers.get(0);
        Assert.assertEquals(0, nameResolver.refreshCalled);
    }

    @Test
    public void resetConnectBackoff_noOpWhenNameResolverNotStarted() {
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        requestConnection = false;
        createChannel();
        channel.resetConnectBackoff();
        ManagedChannelImplTest.FakeNameResolverFactory.FakeNameResolver nameResolver = nameResolverFactory.resolvers.get(0);
        Assert.assertEquals(0, nameResolver.refreshCalled);
    }

    @Test
    public void channelsAndSubchannels_instrumented_name() throws Exception {
        createChannel();
        Assert.assertEquals(ManagedChannelImplTest.TARGET, ManagedChannelImplTest.getStats(channel).target);
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Assert.assertEquals(Collections.singletonList(addressGroup).toString(), ManagedChannelImplTest.getStats(((AbstractSubchannel) (subchannel))).target);
    }

    @Test
    public void channelTracing_channelCreationEvent() throws Exception {
        timer.forwardNanos(1234);
        maxTraceEvents(10);
        createChannel();
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).contains(new ChannelTrace.Event.Builder().setDescription("Channel for 'fake://fake.example.com' created").setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).build());
    }

    @Test
    public void channelTracing_subchannelCreationEvents() throws Exception {
        maxTraceEvents(10);
        createChannel();
        timer.forwardNanos(1234);
        AbstractSubchannel subchannel = ((AbstractSubchannel) (ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY)));
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).contains(new ChannelTrace.Event.Builder().setDescription("Child Subchannel created").setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).setSubchannelRef(subchannel.getInternalSubchannel()).build());
        assertThat(ManagedChannelImplTest.getStats(subchannel).channelTrace.events).contains(new ChannelTrace.Event.Builder().setDescription("Subchannel for [[[test-addr]/{}]] created").setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).build());
    }

    @Test
    public void channelTracing_nameResolvingErrorEvent() throws Exception {
        timer.forwardNanos(1234);
        maxTraceEvents(10);
        Status error = UNAVAILABLE.withDescription("simulated error");
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setError(error).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).contains(new ChannelTrace.Event.Builder().setDescription(("Failed to resolve name: " + error)).setSeverity(CT_WARNING).setTimestampNanos(timer.getTicker().read()).build());
    }

    @Test
    public void channelTracing_nameResolvedEvent() throws Exception {
        timer.forwardNanos(1234);
        maxTraceEvents(10);
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(socketAddress))).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).contains(new ChannelTrace.Event.Builder().setDescription(("Address resolved: " + (Collections.singletonList(new EquivalentAddressGroup(socketAddress))))).setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).build());
    }

    @Test
    public void channelTracing_nameResolvedEvent_zeorAndNonzeroBackends() throws Exception {
        timer.forwardNanos(1234);
        maxTraceEvents(10);
        List<EquivalentAddressGroup> servers = new ArrayList<>();
        servers.add(new EquivalentAddressGroup(socketAddress));
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(servers).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        int prevSize = ManagedChannelImplTest.getStats(channel).channelTrace.events.size();
        nameResolverFactory.resolvers.get(0).listener.onAddresses(Collections.singletonList(new EquivalentAddressGroup(Arrays.asList(new SocketAddress() {}, new SocketAddress() {}))), EMPTY);
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).hasSize(prevSize);
        prevSize = ManagedChannelImplTest.getStats(channel).channelTrace.events.size();
        nameResolverFactory.resolvers.get(0).listener.onError(Status.INTERNAL);
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).hasSize((prevSize + 1));
        prevSize = ManagedChannelImplTest.getStats(channel).channelTrace.events.size();
        nameResolverFactory.resolvers.get(0).listener.onError(Status.INTERNAL);
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).hasSize(prevSize);
        prevSize = ManagedChannelImplTest.getStats(channel).channelTrace.events.size();
        nameResolverFactory.resolvers.get(0).listener.onAddresses(Collections.singletonList(new EquivalentAddressGroup(Arrays.asList(new SocketAddress() {}, new SocketAddress() {}))), EMPTY);
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).hasSize((prevSize + 1));
    }

    @Test
    public void channelTracing_serviceConfigChange() throws Exception {
        timer.forwardNanos(1234);
        maxTraceEvents(10);
        List<EquivalentAddressGroup> servers = new ArrayList<>();
        servers.add(new EquivalentAddressGroup(socketAddress));
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(servers).build();
        channelBuilder.nameResolverFactory(nameResolverFactory);
        createChannel();
        int prevSize = ManagedChannelImplTest.getStats(channel).channelTrace.events.size();
        Attributes attributes = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, new HashMap<String, Object>()).build();
        nameResolverFactory.resolvers.get(0).listener.onAddresses(Collections.singletonList(new EquivalentAddressGroup(Arrays.asList(new SocketAddress() {}, new SocketAddress() {}))), attributes);
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).hasSize((prevSize + 1));
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events.get(prevSize)).isEqualTo(new ChannelTrace.Event.Builder().setDescription("Service config changed").setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).build());
        prevSize = ManagedChannelImplTest.getStats(channel).channelTrace.events.size();
        nameResolverFactory.resolvers.get(0).listener.onAddresses(Collections.singletonList(new EquivalentAddressGroup(Arrays.asList(new SocketAddress() {}, new SocketAddress() {}))), attributes);
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).hasSize(prevSize);
        prevSize = ManagedChannelImplTest.getStats(channel).channelTrace.events.size();
        Map<String, Object> serviceConfig = new HashMap<>();
        serviceConfig.put("methodConfig", new HashMap<String, Object>());
        attributes = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        timer.forwardNanos(1234);
        nameResolverFactory.resolvers.get(0).listener.onAddresses(Collections.singletonList(new EquivalentAddressGroup(Arrays.asList(new SocketAddress() {}, new SocketAddress() {}))), attributes);
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).hasSize((prevSize + 1));
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events.get(prevSize)).isEqualTo(new ChannelTrace.Event.Builder().setDescription("Service config changed").setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).build());
    }

    @Test
    public void channelTracing_stateChangeEvent() throws Exception {
        maxTraceEvents(10);
        createChannel();
        timer.forwardNanos(1234);
        helper.updateBalancingState(ConnectivityState.CONNECTING, mockPicker);
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).contains(new ChannelTrace.Event.Builder().setDescription("Entering CONNECTING state").setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).build());
    }

    @Test
    public void channelTracing_subchannelStateChangeEvent() throws Exception {
        maxTraceEvents(10);
        createChannel();
        AbstractSubchannel subchannel = ((AbstractSubchannel) (ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY)));
        timer.forwardNanos(1234);
        subchannel.obtainActiveTransport();
        assertThat(ManagedChannelImplTest.getStats(subchannel).channelTrace.events).contains(new ChannelTrace.Event.Builder().setDescription("CONNECTING as requested").setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).build());
    }

    @Test
    public void channelTracing_oobChannelStateChangeEvent() throws Exception {
        maxTraceEvents(10);
        createChannel();
        OobChannel oobChannel = ((OobChannel) (helper.createOobChannel(addressGroup, "authority")));
        timer.forwardNanos(1234);
        oobChannel.handleSubchannelStateChange(ConnectivityStateInfo.forNonError(CONNECTING));
        assertThat(ManagedChannelImplTest.getStats(oobChannel).channelTrace.events).contains(new ChannelTrace.Event.Builder().setDescription("Entering CONNECTING state").setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).build());
    }

    @Test
    public void channelTracing_oobChannelCreationEvents() throws Exception {
        maxTraceEvents(10);
        createChannel();
        timer.forwardNanos(1234);
        OobChannel oobChannel = ((OobChannel) (helper.createOobChannel(addressGroup, "authority")));
        assertThat(ManagedChannelImplTest.getStats(channel).channelTrace.events).contains(new ChannelTrace.Event.Builder().setDescription("Child OobChannel created").setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).setChannelRef(oobChannel).build());
        assertThat(ManagedChannelImplTest.getStats(oobChannel).channelTrace.events).contains(new ChannelTrace.Event.Builder().setDescription("OobChannel for [[test-addr]/{}] created").setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).build());
        assertThat(ManagedChannelImplTest.getStats(oobChannel.getInternalSubchannel()).channelTrace.events).contains(new ChannelTrace.Event.Builder().setDescription("Subchannel for [[test-addr]/{}] created").setSeverity(CT_INFO).setTimestampNanos(timer.getTicker().read()).build());
    }

    @Test
    public void channelsAndSubchannels_instrumented_state() throws Exception {
        createChannel();
        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(helperCaptor.capture());
        helper = helperCaptor.getValue();
        Assert.assertEquals(ConnectivityState.IDLE, ManagedChannelImplTest.getStats(channel).state);
        helper.updateBalancingState(ConnectivityState.CONNECTING, mockPicker);
        Assert.assertEquals(ConnectivityState.CONNECTING, ManagedChannelImplTest.getStats(channel).state);
        AbstractSubchannel subchannel = ((AbstractSubchannel) (ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY)));
        Assert.assertEquals(ConnectivityState.IDLE, ManagedChannelImplTest.getStats(subchannel).state);
        subchannel.requestConnection();
        Assert.assertEquals(ConnectivityState.CONNECTING, ManagedChannelImplTest.getStats(subchannel).state);
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        Assert.assertEquals(ConnectivityState.CONNECTING, ManagedChannelImplTest.getStats(subchannel).state);
        transportInfo.listener.transportReady();
        Assert.assertEquals(ConnectivityState.READY, ManagedChannelImplTest.getStats(subchannel).state);
        Assert.assertEquals(ConnectivityState.CONNECTING, ManagedChannelImplTest.getStats(channel).state);
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        Assert.assertEquals(ConnectivityState.READY, ManagedChannelImplTest.getStats(channel).state);
        channel.shutdownNow();
        Assert.assertEquals(ConnectivityState.SHUTDOWN, ManagedChannelImplTest.getStats(channel).state);
        Assert.assertEquals(ConnectivityState.SHUTDOWN, ManagedChannelImplTest.getStats(subchannel).state);
    }

    @Test
    public void channelStat_callStarted() throws Exception {
        createChannel();
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        Assert.assertEquals(0, ManagedChannelImplTest.getStats(channel).callsStarted);
        call.start(mockCallListener, new Metadata());
        Assert.assertEquals(1, ManagedChannelImplTest.getStats(channel).callsStarted);
        Assert.assertEquals(executor.getTicker().read(), ManagedChannelImplTest.getStats(channel).lastCallStartedNanos);
    }

    @Test
    public void channelsAndSubChannels_instrumented_success() throws Exception {
        channelsAndSubchannels_instrumented0(true);
    }

    @Test
    public void channelsAndSubChannels_instrumented_fail() throws Exception {
        channelsAndSubchannels_instrumented0(false);
    }

    @Test
    public void channelsAndSubchannels_oob_instrumented_success() throws Exception {
        channelsAndSubchannels_oob_instrumented0(true);
    }

    @Test
    public void channelsAndSubchannels_oob_instrumented_fail() throws Exception {
        channelsAndSubchannels_oob_instrumented0(false);
    }

    @Test
    public void channelsAndSubchannels_oob_instrumented_name() throws Exception {
        createChannel();
        String authority = "oobauthority";
        OobChannel oobChannel = ((OobChannel) (helper.createOobChannel(addressGroup, authority)));
        Assert.assertEquals(authority, ManagedChannelImplTest.getStats(oobChannel).target);
    }

    @Test
    public void channelsAndSubchannels_oob_instrumented_state() throws Exception {
        createChannel();
        OobChannel oobChannel = ((OobChannel) (helper.createOobChannel(addressGroup, "oobauthority")));
        Assert.assertEquals(ConnectivityState.IDLE, ManagedChannelImplTest.getStats(oobChannel).state);
        oobChannel.getSubchannel().requestConnection();
        Assert.assertEquals(ConnectivityState.CONNECTING, ManagedChannelImplTest.getStats(oobChannel).state);
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        ManagedClientTransport.Listener transportListener = transportInfo.listener;
        transportListener.transportReady();
        Assert.assertEquals(ConnectivityState.READY, ManagedChannelImplTest.getStats(oobChannel).state);
        // oobchannel state is separate from the ManagedChannel
        Assert.assertEquals(ConnectivityState.IDLE, ManagedChannelImplTest.getStats(channel).state);
        channel.shutdownNow();
        Assert.assertEquals(ConnectivityState.SHUTDOWN, ManagedChannelImplTest.getStats(channel).state);
        Assert.assertEquals(ConnectivityState.SHUTDOWN, ManagedChannelImplTest.getStats(oobChannel).state);
    }

    @Test
    public void binaryLogInstalled() throws Exception {
        final SettableFuture<Boolean> intercepted = SettableFuture.create();
        channelBuilder.binlog = new BinaryLog() {
            @Override
            public void close() throws IOException {
                // noop
            }

            @Override
            public <ReqT, RespT> ServerMethodDefinition<?, ?> wrapMethodDefinition(ServerMethodDefinition<ReqT, RespT> oMethodDef) {
                return oMethodDef;
            }

            @Override
            public Channel wrapChannel(Channel channel) {
                return ClientInterceptors.intercept(channel, new ClientInterceptor() {
                    @Override
                    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
                        intercepted.set(true);
                        return next.newCall(method, callOptions);
                    }
                });
            }
        };
        createChannel();
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        Assert.assertTrue(intercepted.get());
    }

    @Test
    public void retryBackoffThenChannelShutdown_retryShouldStillHappen_newCallShouldFail() {
        Map<String, Object> retryPolicy = new HashMap<>();
        retryPolicy.put("maxAttempts", 3.0);
        retryPolicy.put("initialBackoff", "10s");
        retryPolicy.put("maxBackoff", "30s");
        retryPolicy.put("backoffMultiplier", 2.0);
        retryPolicy.put("retryableStatusCodes", Arrays.<Object>asList("UNAVAILABLE"));
        Map<String, Object> methodConfig = new HashMap<>();
        Map<String, Object> name = new HashMap<>();
        name.put("service", "service");
        methodConfig.put("name", Arrays.<Object>asList(name));
        methodConfig.put("retryPolicy", retryPolicy);
        Map<String, Object> serviceConfig = new HashMap<>();
        serviceConfig.put("methodConfig", Arrays.<Object>asList(methodConfig));
        Attributes attributesWithRetryPolicy = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(socketAddress))).build();
        nameResolverFactory.nextResolvedAttributes.set(attributesWithRetryPolicy);
        channelBuilder.nameResolverFactory(nameResolverFactory);
        executor(directExecutor());
        enableRetry();
        // not random
        RetriableStream.setRandom(new Random() {
            @Override
            public double nextDouble() {
                return 1.0;// fake random

            }
        });
        requestConnection = false;
        createChannel();
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(Helper.class);
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(helperCaptor.capture());
        helper = helperCaptor.getValue();
        Mockito.verify(mockLoadBalancer).handleResolvedAddressGroups(ArgumentMatchers.eq(nameResolverFactory.servers), ArgumentMatchers.same(attributesWithRetryPolicy));
        // simulating request connection and then transport ready after resolved address
        Subchannel subchannel = ManagedChannelImplTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel));
        subchannel.requestConnection();
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        ConnectionClientTransport mockTransport = transportInfo.transport;
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        Mockito.when(mockTransport.newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class))).thenReturn(mockStream).thenReturn(mockStream2);
        transportInfo.listener.transportReady();
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        ArgumentCaptor<ClientStreamListener> streamListenerCaptor = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream).start(streamListenerCaptor.capture());
        assertThat(timer.getPendingTasks()).isEmpty();
        // trigger retry
        streamListenerCaptor.getValue().closed(UNAVAILABLE, new Metadata());
        // in backoff
        timer.forwardTime(5, TimeUnit.SECONDS);
        assertThat(timer.getPendingTasks()).hasSize(1);
        Mockito.verify(mockStream2, Mockito.never()).start(ArgumentMatchers.any(ClientStreamListener.class));
        // shutdown during backoff period
        channel.shutdown();
        assertThat(timer.getPendingTasks()).hasSize(1);
        Mockito.verify(mockCallListener, Mockito.never()).onClose(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
        ClientCall<String, Integer> call2 = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        call2.start(mockCallListener2, new Metadata());
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockCallListener2).onClose(statusCaptor.capture(), ArgumentMatchers.any(Metadata.class));
        Assert.assertSame(Status.Code.UNAVAILABLE, statusCaptor.getValue().getCode());
        Assert.assertEquals("Channel shutdown invoked", statusCaptor.getValue().getDescription());
        // backoff ends
        timer.forwardTime(5, TimeUnit.SECONDS);
        assertThat(timer.getPendingTasks()).isEmpty();
        Mockito.verify(mockStream2).start(streamListenerCaptor.capture());
        Mockito.verify(mockLoadBalancer, Mockito.never()).shutdown();
        Assert.assertFalse("channel.isTerminated() is expected to be false but was true", channel.isTerminated());
        streamListenerCaptor.getValue().closed(Status.INTERNAL, new Metadata());
        Mockito.verify(mockLoadBalancer).shutdown();
        // simulating the shutdown of load balancer triggers the shutdown of subchannel
        subchannel.shutdown();
        transportInfo.listener.transportTerminated();// simulating transport terminated

        Assert.assertTrue("channel.isTerminated() is expected to be true but was false", channel.isTerminated());
    }

    @Test
    public void hedgingScheduledThenChannelShutdown_hedgeShouldStillHappen_newCallShouldFail() {
        Map<String, Object> hedgingPolicy = new HashMap<>();
        hedgingPolicy.put("maxAttempts", 3.0);
        hedgingPolicy.put("hedgingDelay", "10s");
        hedgingPolicy.put("nonFatalStatusCodes", Arrays.<Object>asList("UNAVAILABLE"));
        Map<String, Object> methodConfig = new HashMap<>();
        Map<String, Object> name = new HashMap<>();
        name.put("service", "service");
        methodConfig.put("name", Arrays.<Object>asList(name));
        methodConfig.put("hedgingPolicy", hedgingPolicy);
        Map<String, Object> serviceConfig = new HashMap<>();
        serviceConfig.put("methodConfig", Arrays.<Object>asList(methodConfig));
        Attributes attributesWithRetryPolicy = Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, serviceConfig).build();
        ManagedChannelImplTest.FakeNameResolverFactory nameResolverFactory = new ManagedChannelImplTest.FakeNameResolverFactory.Builder(expectedUri).setServers(Collections.singletonList(new EquivalentAddressGroup(socketAddress))).build();
        nameResolverFactory.nextResolvedAttributes.set(attributesWithRetryPolicy);
        channelBuilder.nameResolverFactory(nameResolverFactory);
        executor(directExecutor());
        enableRetry();
        requestConnection = false;
        createChannel();
        ClientCall<String, Integer> call = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(Helper.class);
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(helperCaptor.capture());
        helper = helperCaptor.getValue();
        Mockito.verify(mockLoadBalancer).handleResolvedAddressGroups(nameResolverFactory.servers, attributesWithRetryPolicy);
        // simulating request connection and then transport ready after resolved address
        Subchannel subchannel = helper.createSubchannel(addressGroup, EMPTY);
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel));
        subchannel.requestConnection();
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        ConnectionClientTransport mockTransport = transportInfo.transport;
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        Mockito.when(mockTransport.newStream(ArgumentMatchers.same(ManagedChannelImplTest.method), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class))).thenReturn(mockStream).thenReturn(mockStream2);
        transportInfo.listener.transportReady();
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        ArgumentCaptor<ClientStreamListener> streamListenerCaptor = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream).start(streamListenerCaptor.capture());
        // in hedging delay backoff
        timer.forwardTime(5, TimeUnit.SECONDS);
        assertThat(timer.numPendingTasks()).isEqualTo(1);
        // first hedge fails
        streamListenerCaptor.getValue().closed(UNAVAILABLE, new Metadata());
        Mockito.verify(mockStream2, Mockito.never()).start(ArgumentMatchers.any(ClientStreamListener.class));
        // shutdown during backoff period
        channel.shutdown();
        assertThat(timer.numPendingTasks()).isEqualTo(1);
        Mockito.verify(mockCallListener, Mockito.never()).onClose(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
        ClientCall<String, Integer> call2 = channel.newCall(ManagedChannelImplTest.method, DEFAULT);
        call2.start(mockCallListener2, new Metadata());
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockCallListener2).onClose(statusCaptor.capture(), ArgumentMatchers.any(Metadata.class));
        Assert.assertSame(Status.Code.UNAVAILABLE, statusCaptor.getValue().getCode());
        Assert.assertEquals("Channel shutdown invoked", statusCaptor.getValue().getDescription());
        // backoff ends
        timer.forwardTime(5, TimeUnit.SECONDS);
        assertThat(timer.numPendingTasks()).isEqualTo(1);
        Mockito.verify(mockStream2).start(streamListenerCaptor.capture());
        Mockito.verify(mockLoadBalancer, Mockito.never()).shutdown();
        Assert.assertFalse("channel.isTerminated() is expected to be false but was true", channel.isTerminated());
        streamListenerCaptor.getValue().closed(Status.INTERNAL, new Metadata());
        assertThat(timer.numPendingTasks()).isEqualTo(0);
        Mockito.verify(mockLoadBalancer).shutdown();
        // simulating the shutdown of load balancer triggers the shutdown of subchannel
        subchannel.shutdown();
        transportInfo.listener.transportTerminated();// simulating transport terminated

        Assert.assertTrue("channel.isTerminated() is expected to be true but was false", channel.isTerminated());
    }

    @Test
    public void badServiceConfigIsRecoverable() throws Exception {
        final List<EquivalentAddressGroup> addresses = ImmutableList.of(new EquivalentAddressGroup(new SocketAddress() {}));
        final class FakeNameResolver extends NameResolver {
            Listener listener;

            @Override
            public String getServiceAuthority() {
                return "also fake";
            }

            @Override
            public void start(Listener listener) {
                this.listener = listener;
                listener.onAddresses(addresses, Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, ImmutableMap.<String, Object>of("loadBalancingPolicy", "kaboom")).build());
            }

            @Override
            public void shutdown() {
            }
        }
        final class FakeNameResolverFactory extends NameResolver.Factory {
            FakeNameResolver resolver;

            @Nullable
            @Override
            public NameResolver newNameResolver(URI targetUri, NameResolver.Helper helper) {
                return resolver = new FakeNameResolver();
            }

            @Override
            public String getDefaultScheme() {
                return "fake";
            }
        }
        FakeNameResolverFactory factory = new FakeNameResolverFactory();
        final class CustomBuilder extends AbstractManagedChannelImplBuilder<CustomBuilder> {
            CustomBuilder() {
                super(ManagedChannelImplTest.TARGET);
                this.executorPool = ManagedChannelImplTest.this.executorPool;
                this.channelz = ManagedChannelImplTest.this.channelz;
            }

            @Override
            protected ClientTransportFactory buildTransportFactory() {
                return mockTransportFactory;
            }
        }
        ManagedChannel mychannel = nameResolverFactory(factory).build();
        ClientCall<Void, Void> call1 = mychannel.newCall(TestMethodDescriptors.voidMethod(), DEFAULT);
        ListenableFuture<Void> future1 = ClientCalls.futureUnaryCall(call1, null);
        executor.runDueTasks();
        try {
            future1.get();
            Assert.fail();
        } catch (ExecutionException e) {
            assertThat(Throwables.getStackTraceAsString(e.getCause())).contains("kaboom");
        }
        // ok the service config is bad, let's fix it.
        factory.resolver.listener.onAddresses(addresses, Attributes.newBuilder().set(NAME_RESOLVER_SERVICE_CONFIG, ImmutableMap.<String, Object>of("loadBalancingPolicy", "round_robin")).build());
        ClientCall<Void, Void> call2 = mychannel.newCall(TestMethodDescriptors.voidMethod(), DEFAULT.withDeadlineAfter(5, TimeUnit.SECONDS));
        ListenableFuture<Void> future2 = ClientCalls.futureUnaryCall(call2, null);
        timer.forwardTime(1234, TimeUnit.SECONDS);
        executor.runDueTasks();
        try {
            future2.get();
            Assert.fail();
        } catch (ExecutionException e) {
            assertThat(Throwables.getStackTraceAsString(e.getCause())).contains("deadline");
        }
        mychannel.shutdownNow();
    }

    @Test
    public void nameResolverHelperPropagation() {
        final AtomicReference<NameResolver.Helper> capturedHelper = new AtomicReference<>();
        final NameResolver noopResolver = new NameResolver() {
            @Override
            public String getServiceAuthority() {
                return "fake-authority";
            }

            @Override
            public void start(Listener listener) {
            }

            @Override
            public void shutdown() {
            }
        };
        ProxyDetector neverProxy = new ProxyDetector() {
            @Override
            public ProxiedSocketAddress proxyFor(SocketAddress targetAddress) {
                return null;
            }
        };
        NameResolver.Factory oldApiFactory = new NameResolver.Factory() {
            @Override
            public NameResolver newNameResolver(URI targetUri, NameResolver.Helper helper) {
                capturedHelper.set(helper);
                return noopResolver;
            }

            @Override
            public String getDefaultScheme() {
                return "fakescheme";
            }
        };
        channelBuilder.nameResolverFactory(oldApiFactory).proxyDetector(neverProxy);
        createChannel();
        NameResolver.Helper helper = capturedHelper.get();
        assertThat(helper).isNotNull();
        assertThat(helper.getDefaultPort()).isEqualTo(ManagedChannelImplTest.DEFAULT_PORT);
        assertThat(helper.getProxyDetector()).isSameAs(neverProxy);
    }

    @Test
    @Deprecated
    public void nameResolverParams_oldApi() {
        final AtomicReference<Attributes> capturedParams = new AtomicReference<>();
        final NameResolver noopResolver = new NameResolver() {
            @Override
            public String getServiceAuthority() {
                return "fake-authority";
            }

            @Override
            public void start(Listener listener) {
            }

            @Override
            public void shutdown() {
            }
        };
        ProxyDetector neverProxy = new ProxyDetector() {
            @Override
            public ProxiedSocketAddress proxyFor(SocketAddress targetAddress) {
                return null;
            }
        };
        NameResolver.Factory oldApiFactory = new NameResolver.Factory() {
            @Override
            public NameResolver newNameResolver(URI targetUri, Attributes params) {
                capturedParams.set(params);
                return noopResolver;
            }

            @Override
            public String getDefaultScheme() {
                return "fakescheme";
            }
        };
        channelBuilder.nameResolverFactory(oldApiFactory).proxyDetector(neverProxy);
        createChannel();
        Attributes attrs = capturedParams.get();
        assertThat(attrs).isNotNull();
        assertThat(attrs.get(PARAMS_DEFAULT_PORT)).isEqualTo(ManagedChannelImplTest.DEFAULT_PORT);
        assertThat(attrs.get(PARAMS_PROXY_DETECTOR)).isSameAs(neverProxy);
    }

    @Test
    public void getAuthorityAfterShutdown() throws Exception {
        createChannel();
        Assert.assertEquals(ManagedChannelImplTest.SERVICE_NAME, channel.authority());
        channel.shutdownNow().awaitTermination(1, TimeUnit.SECONDS);
        Assert.assertEquals(ManagedChannelImplTest.SERVICE_NAME, channel.authority());
    }

    @Test
    public void nameResolverHelper_emptyConfigSucceeds() {
        int defaultPort = 1;
        ProxyDetector proxyDetector = GrpcUtil.getDefaultProxyDetector();
        SynchronizationContext syncCtx = new SynchronizationContext(Thread.currentThread().getUncaughtExceptionHandler());
        boolean retryEnabled = false;
        int maxRetryAttemptsLimit = 2;
        int maxHedgedAttemptsLimit = 3;
        NrHelper nrh = new NrHelper(defaultPort, proxyDetector, syncCtx, retryEnabled, maxRetryAttemptsLimit, maxHedgedAttemptsLimit);
        ConfigOrError<ManagedChannelServiceConfig> coe = nrh.parseServiceConfig(ImmutableMap.<String, Object>of());
        assertThat(coe.getConfig()).isNotNull();
        assertThat(coe.getConfig().getServiceMap()).isEmpty();
        assertThat(coe.getConfig().getServiceMethodMap()).isEmpty();
    }

    @Test
    public void nameResolverHelper_badConfigFails() {
        int defaultPort = 1;
        ProxyDetector proxyDetector = GrpcUtil.getDefaultProxyDetector();
        SynchronizationContext syncCtx = new SynchronizationContext(Thread.currentThread().getUncaughtExceptionHandler());
        boolean retryEnabled = false;
        int maxRetryAttemptsLimit = 2;
        int maxHedgedAttemptsLimit = 3;
        NrHelper nrh = new NrHelper(defaultPort, proxyDetector, syncCtx, retryEnabled, maxRetryAttemptsLimit, maxHedgedAttemptsLimit);
        ConfigOrError<ManagedChannelServiceConfig> coe = nrh.parseServiceConfig(ImmutableMap.<String, Object>of("methodConfig", "bogus"));
        assertThat(coe.getError()).isNotNull();
        assertThat(coe.getError().getCode()).isEqualTo(Code.UNKNOWN);
        assertThat(coe.getError().getDescription()).contains("failed to parse service config");
        assertThat(coe.getError().getCause()).isInstanceOf(ClassCastException.class);
    }

    private static final class ChannelBuilder extends AbstractManagedChannelImplBuilder<ManagedChannelImplTest.ChannelBuilder> {
        ChannelBuilder() {
            super(ManagedChannelImplTest.TARGET);
        }

        @Override
        protected ClientTransportFactory buildTransportFactory() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected int getDefaultPort() {
            return ManagedChannelImplTest.DEFAULT_PORT;
        }
    }

    private static final class FakeBackoffPolicyProvider implements BackoffPolicy.Provider {
        @Override
        public BackoffPolicy get() {
            return new BackoffPolicy() {
                int multiplier = 1;

                @Override
                public long nextBackoffNanos() {
                    return (ManagedChannelImplTest.RECONNECT_BACKOFF_INTERVAL_NANOS) * ((multiplier)++);
                }
            };
        }
    }

    private static final class FakeNameResolverFactory extends NameResolver.Factory {
        final URI expectedUri;

        final List<EquivalentAddressGroup> servers;

        final boolean resolvedAtStart;

        final Status error;

        final ArrayList<ManagedChannelImplTest.FakeNameResolverFactory.FakeNameResolver> resolvers = new ArrayList<>();

        // The Attributes argument of the next invocation of listener.onAddresses(servers, attrs)
        final AtomicReference<Attributes> nextResolvedAttributes = new AtomicReference(Attributes.EMPTY);

        FakeNameResolverFactory(URI expectedUri, List<EquivalentAddressGroup> servers, boolean resolvedAtStart, Status error) {
            this.expectedUri = expectedUri;
            this.servers = servers;
            this.resolvedAtStart = resolvedAtStart;
            this.error = error;
        }

        @Override
        public NameResolver newNameResolver(final URI targetUri, NameResolver.Helper helper) {
            if (!(expectedUri.equals(targetUri))) {
                return null;
            }
            Assert.assertEquals(ManagedChannelImplTest.DEFAULT_PORT, helper.getDefaultPort());
            ManagedChannelImplTest.FakeNameResolverFactory.FakeNameResolver resolver = new ManagedChannelImplTest.FakeNameResolverFactory.FakeNameResolver(error);
            resolvers.add(resolver);
            return resolver;
        }

        @Override
        public String getDefaultScheme() {
            return "fake";
        }

        void allResolved() {
            for (ManagedChannelImplTest.FakeNameResolverFactory.FakeNameResolver resolver : resolvers) {
                resolver.resolved();
            }
        }

        final class FakeNameResolver extends NameResolver {
            Listener listener;

            boolean shutdown;

            int refreshCalled;

            Status error;

            FakeNameResolver(Status error) {
                this.error = error;
            }

            @Override
            public String getServiceAuthority() {
                return expectedUri.getAuthority();
            }

            @Override
            public void start(final Listener listener) {
                this.listener = listener;
                if (resolvedAtStart) {
                    resolved();
                }
            }

            @Override
            public void refresh() {
                (refreshCalled)++;
                resolved();
            }

            void resolved() {
                if ((error) != null) {
                    listener.onError(error);
                    return;
                }
                listener.onAddresses(servers, nextResolvedAttributes.get());
            }

            @Override
            public void shutdown() {
                shutdown = true;
            }

            @Override
            public String toString() {
                return "FakeNameResolver";
            }
        }

        static final class Builder {
            final URI expectedUri;

            List<EquivalentAddressGroup> servers = ImmutableList.of();

            boolean resolvedAtStart = true;

            Status error = null;

            Builder(URI expectedUri) {
                this.expectedUri = expectedUri;
            }

            ManagedChannelImplTest.FakeNameResolverFactory.Builder setServers(List<EquivalentAddressGroup> servers) {
                this.servers = servers;
                return this;
            }

            ManagedChannelImplTest.FakeNameResolverFactory.Builder setResolvedAtStart(boolean resolvedAtStart) {
                this.resolvedAtStart = resolvedAtStart;
                return this;
            }

            ManagedChannelImplTest.FakeNameResolverFactory.Builder setError(Status error) {
                this.error = error;
                return this;
            }

            ManagedChannelImplTest.FakeNameResolverFactory build() {
                return new ManagedChannelImplTest.FakeNameResolverFactory(expectedUri, servers, resolvedAtStart, error);
            }
        }
    }
}

