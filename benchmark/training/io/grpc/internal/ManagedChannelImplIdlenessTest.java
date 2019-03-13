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
package io.grpc.internal;


import Attributes.EMPTY;
import CallOptions.DEFAULT;
import MethodType.UNKNOWN;
import NameResolver.Factory;
import Status.UNAVAILABLE;
import com.google.common.collect.Lists;
import io.grpc.ChannelLogger;
import io.grpc.ClientCall;
import io.grpc.ConnectivityState;
import io.grpc.EquivalentAddressGroup;
import io.grpc.IntegerMarshaller;
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
import io.grpc.Status;
import io.grpc.StringMarshaller;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.any;


/**
 * Unit tests for {@link ManagedChannelImpl}'s idle mode.
 */
@RunWith(JUnit4.class)
public class ManagedChannelImplIdlenessTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    private final FakeClock timer = new FakeClock();

    private final FakeClock executor = new FakeClock();

    private final FakeClock oobExecutor = new FakeClock();

    private static final String AUTHORITY = "fakeauthority";

    private static final String USER_AGENT = "fakeagent";

    private static final long IDLE_TIMEOUT_SECONDS = 30;

    private static final String MOCK_POLICY_NAME = "mock_lb";

    private ManagedChannelImpl channel;

    private final MethodDescriptor<String, Integer> method = MethodDescriptor.<String, Integer>newBuilder().setType(UNKNOWN).setFullMethodName("service/method").setRequestMarshaller(new StringMarshaller()).setResponseMarshaller(new IntegerMarshaller()).build();

    private final List<EquivalentAddressGroup> servers = Lists.newArrayList();

    private final ObjectPool<Executor> executorPool = new FixedObjectPool<Executor>(executor.getScheduledExecutorService());

    private final ObjectPool<Executor> oobExecutorPool = new FixedObjectPool<Executor>(oobExecutor.getScheduledExecutorService());

    @Mock
    private ClientTransportFactory mockTransportFactory;

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
            return ManagedChannelImplIdlenessTest.MOCK_POLICY_NAME;
        }
    }));

    @Mock
    private NameResolver mockNameResolver;

    @Mock
    private Factory mockNameResolverFactory;

    @Mock
    private ClientCall.Listener<Integer> mockCallListener;

    @Mock
    private ClientCall.Listener<Integer> mockCallListener2;

    @Captor
    private ArgumentCaptor<NameResolver.Listener> nameResolverListenerCaptor;

    private BlockingQueue<TestUtils.MockClientTransportInfo> newTransports;

    @Test
    public void newCallExitsIdleness() throws Exception {
        ClientCall<String, Integer> call = channel.newCall(method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(any(Helper.class));
        Mockito.verify(mockNameResolver).start(nameResolverListenerCaptor.capture());
        // Simulate new address resolved to make sure the LoadBalancer is correctly linked to
        // the NameResolver.
        nameResolverListenerCaptor.getValue().onAddresses(servers, EMPTY);
        Mockito.verify(mockLoadBalancer).handleResolvedAddressGroups(servers, EMPTY);
    }

    @Test
    public void newCallRefreshesIdlenessTimer() throws Exception {
        // First call to exit the initial idleness, then immediately cancel the call.
        ClientCall<String, Integer> call = channel.newCall(method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        call.cancel("For testing", null);
        // Verify that we have exited the idle mode
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(any(Helper.class));
        Assert.assertFalse(channel.inUseStateAggregator.isInUse());
        // Move closer to idleness, but not yet.
        timer.forwardTime(((ManagedChannelImplIdlenessTest.IDLE_TIMEOUT_SECONDS) - 1), TimeUnit.SECONDS);
        Mockito.verify(mockLoadBalancer, Mockito.never()).shutdown();
        Assert.assertFalse(channel.inUseStateAggregator.isInUse());
        // A new call would refresh the timer
        call = channel.newCall(method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        call.cancel("For testing", null);
        Assert.assertFalse(channel.inUseStateAggregator.isInUse());
        // ... so that passing the same length of time will not trigger idle mode
        timer.forwardTime(((ManagedChannelImplIdlenessTest.IDLE_TIMEOUT_SECONDS) - 1), TimeUnit.SECONDS);
        Mockito.verify(mockLoadBalancer, Mockito.never()).shutdown();
        Assert.assertFalse(channel.inUseStateAggregator.isInUse());
        // ... until the time since last call has reached the timeout
        timer.forwardTime(1, TimeUnit.SECONDS);
        Mockito.verify(mockLoadBalancer).shutdown();
        Assert.assertFalse(channel.inUseStateAggregator.isInUse());
        // Drain the app executor, which runs the call listeners
        Mockito.verify(mockCallListener, Mockito.never()).onClose(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
        Assert.assertEquals(2, executor.runDueTasks());
        Mockito.verify(mockCallListener, Mockito.times(2)).onClose(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void delayedTransportHoldsOffIdleness() throws Exception {
        ClientCall<String, Integer> call = channel.newCall(method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        Assert.assertTrue(channel.inUseStateAggregator.isInUse());
        // As long as the delayed transport is in-use (by the pending RPC), the channel won't go idle.
        timer.forwardTime(((ManagedChannelImplIdlenessTest.IDLE_TIMEOUT_SECONDS) * 2), TimeUnit.SECONDS);
        Assert.assertTrue(channel.inUseStateAggregator.isInUse());
        // Cancelling the only RPC will reset the in-use state.
        Assert.assertEquals(0, executor.numPendingTasks());
        call.cancel("In test", null);
        Assert.assertEquals(1, executor.runDueTasks());
        Assert.assertFalse(channel.inUseStateAggregator.isInUse());
        // And allow the channel to go idle.
        timer.forwardTime(((ManagedChannelImplIdlenessTest.IDLE_TIMEOUT_SECONDS) - 1), TimeUnit.SECONDS);
        Mockito.verify(mockLoadBalancer, Mockito.never()).shutdown();
        timer.forwardTime(1, TimeUnit.SECONDS);
        Mockito.verify(mockLoadBalancer).shutdown();
    }

    @Test
    public void realTransportsHoldsOffIdleness() throws Exception {
        final EquivalentAddressGroup addressGroup = servers.get(1);
        // Start a call, which goes to delayed transport
        ClientCall<String, Integer> call = channel.newCall(method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        // Verify that we have exited the idle mode
        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(helperCaptor.capture());
        Helper helper = helperCaptor.getValue();
        Assert.assertTrue(channel.inUseStateAggregator.isInUse());
        // Assume LoadBalancer has received an address, then create a subchannel.
        Subchannel subchannel = ManagedChannelImplIdlenessTest.createSubchannelSafely(helper, addressGroup, EMPTY);
        subchannel.requestConnection();
        TestUtils.MockClientTransportInfo t0 = newTransports.poll();
        t0.listener.transportReady();
        SubchannelPicker mockPicker = Mockito.mock(SubchannelPicker.class);
        Mockito.when(mockPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel));
        helper.updateBalancingState(ConnectivityState.READY, mockPicker);
        // Delayed transport creates real streams in the app executor
        executor.runDueTasks();
        // Delayed transport exits in-use, while real transport has not entered in-use yet.
        Assert.assertFalse(channel.inUseStateAggregator.isInUse());
        // Now it's in-use
        t0.listener.transportInUse(true);
        Assert.assertTrue(channel.inUseStateAggregator.isInUse());
        // As long as the transport is in-use, the channel won't go idle.
        timer.forwardTime(((ManagedChannelImplIdlenessTest.IDLE_TIMEOUT_SECONDS) * 2), TimeUnit.SECONDS);
        Assert.assertTrue(channel.inUseStateAggregator.isInUse());
        t0.listener.transportInUse(false);
        Assert.assertFalse(channel.inUseStateAggregator.isInUse());
        // And allow the channel to go idle.
        timer.forwardTime(((ManagedChannelImplIdlenessTest.IDLE_TIMEOUT_SECONDS) - 1), TimeUnit.SECONDS);
        Mockito.verify(mockLoadBalancer, Mockito.never()).shutdown();
        timer.forwardTime(1, TimeUnit.SECONDS);
        Mockito.verify(mockLoadBalancer).shutdown();
    }

    @Test
    public void updateSubchannelAddresses_newAddressConnects() {
        ClientCall<String, Integer> call = channel.newCall(method, DEFAULT);
        call.start(mockCallListener, new Metadata());// Create LB

        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(helperCaptor.capture());
        Helper helper = helperCaptor.getValue();
        Subchannel subchannel = ManagedChannelImplIdlenessTest.createSubchannelSafely(helper, servers.get(0), EMPTY);
        subchannel.requestConnection();
        TestUtils.MockClientTransportInfo t0 = newTransports.poll();
        t0.listener.transportReady();
        helper.updateSubchannelAddresses(subchannel, servers.get(1));
        subchannel.requestConnection();
        TestUtils.MockClientTransportInfo t1 = newTransports.poll();
        t1.listener.transportReady();
    }

    @Test
    public void updateSubchannelAddresses_existingAddressDoesNotConnect() {
        ClientCall<String, Integer> call = channel.newCall(method, DEFAULT);
        call.start(mockCallListener, new Metadata());// Create LB

        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(helperCaptor.capture());
        Helper helper = helperCaptor.getValue();
        Subchannel subchannel = ManagedChannelImplIdlenessTest.createSubchannelSafely(helper, servers.get(0), EMPTY);
        subchannel.requestConnection();
        TestUtils.MockClientTransportInfo t0 = newTransports.poll();
        t0.listener.transportReady();
        List<SocketAddress> changedList = new java.util.ArrayList(servers.get(0).getAddresses());
        changedList.add(new ManagedChannelImplIdlenessTest.FakeSocketAddress("aDifferentServer"));
        helper.updateSubchannelAddresses(subchannel, new EquivalentAddressGroup(changedList));
        subchannel.requestConnection();
        Assert.assertNull(newTransports.poll());
    }

    @Test
    public void oobTransportDoesNotAffectIdleness() {
        // Start a call, which goes to delayed transport
        ClientCall<String, Integer> call = channel.newCall(method, DEFAULT);
        call.start(mockCallListener, new Metadata());
        // Verify that we have exited the idle mode
        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(helperCaptor.capture());
        Helper helper = helperCaptor.getValue();
        // Fail the RPC
        SubchannelPicker failingPicker = Mockito.mock(SubchannelPicker.class);
        Mockito.when(failingPicker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withError(UNAVAILABLE));
        helper.updateBalancingState(ConnectivityState.TRANSIENT_FAILURE, failingPicker);
        executor.runDueTasks();
        Mockito.verify(mockCallListener).onClose(ArgumentMatchers.same(UNAVAILABLE), ArgumentMatchers.any(Metadata.class));
        // ... so that the channel resets its in-use state
        Assert.assertFalse(channel.inUseStateAggregator.isInUse());
        // Now make an RPC on an OOB channel
        ManagedChannel oob = helper.createOobChannel(servers.get(0), "oobauthority");
        Mockito.verify(mockTransportFactory, Mockito.never()).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.eq(new ClientTransportFactory.ClientTransportOptions().setAuthority("oobauthority").setUserAgent(ManagedChannelImplIdlenessTest.USER_AGENT)), ArgumentMatchers.any(ChannelLogger.class));
        ClientCall<String, Integer> oobCall = oob.newCall(method, DEFAULT);
        oobCall.start(mockCallListener2, new Metadata());
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.any(SocketAddress.class), ArgumentMatchers.eq(new ClientTransportFactory.ClientTransportOptions().setAuthority("oobauthority").setUserAgent(ManagedChannelImplIdlenessTest.USER_AGENT)), ArgumentMatchers.any(ChannelLogger.class));
        TestUtils.MockClientTransportInfo oobTransportInfo = newTransports.poll();
        Assert.assertEquals(0, newTransports.size());
        // The OOB transport reports in-use state
        oobTransportInfo.listener.transportInUse(true);
        // But it won't stop the channel from going idle
        Mockito.verify(mockLoadBalancer, Mockito.never()).shutdown();
        timer.forwardTime(ManagedChannelImplIdlenessTest.IDLE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Mockito.verify(mockLoadBalancer).shutdown();
    }

    @Test
    public void updateOobChannelAddresses_newAddressConnects() {
        ClientCall<String, Integer> call = channel.newCall(method, DEFAULT);
        call.start(mockCallListener, new Metadata());// Create LB

        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(helperCaptor.capture());
        Helper helper = helperCaptor.getValue();
        ManagedChannel oobChannel = helper.createOobChannel(servers.get(0), "localhost");
        oobChannel.newCall(method, DEFAULT).start(mockCallListener, new Metadata());
        TestUtils.MockClientTransportInfo t0 = newTransports.poll();
        t0.listener.transportReady();
        helper.updateOobChannelAddresses(oobChannel, servers.get(1));
        oobChannel.newCall(method, DEFAULT).start(mockCallListener, new Metadata());
        TestUtils.MockClientTransportInfo t1 = newTransports.poll();
        t1.listener.transportReady();
    }

    @Test
    public void updateOobChannelAddresses_existingAddressDoesNotConnect() {
        ClientCall<String, Integer> call = channel.newCall(method, DEFAULT);
        call.start(mockCallListener, new Metadata());// Create LB

        ArgumentCaptor<Helper> helperCaptor = ArgumentCaptor.forClass(null);
        Mockito.verify(mockLoadBalancerProvider).newLoadBalancer(helperCaptor.capture());
        Helper helper = helperCaptor.getValue();
        ManagedChannel oobChannel = helper.createOobChannel(servers.get(0), "localhost");
        oobChannel.newCall(method, DEFAULT).start(mockCallListener, new Metadata());
        TestUtils.MockClientTransportInfo t0 = newTransports.poll();
        t0.listener.transportReady();
        List<SocketAddress> changedList = new java.util.ArrayList(servers.get(0).getAddresses());
        changedList.add(new ManagedChannelImplIdlenessTest.FakeSocketAddress("aDifferentServer"));
        helper.updateOobChannelAddresses(oobChannel, new EquivalentAddressGroup(changedList));
        oobChannel.newCall(method, DEFAULT).start(mockCallListener, new Metadata());
        Assert.assertNull(newTransports.poll());
    }

    private static class FakeBackoffPolicyProvider implements BackoffPolicy.Provider {
        @Override
        public BackoffPolicy get() {
            return new BackoffPolicy() {
                @Override
                public long nextBackoffNanos() {
                    return 1;
                }
            };
        }
    }

    private static class FakeSocketAddress extends SocketAddress {
        final String name;

        FakeSocketAddress(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "FakeSocketAddress-" + (name);
        }
    }
}

