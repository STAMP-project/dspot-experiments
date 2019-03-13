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


import Attributes.Key;
import BackoffPolicy.Provider;
import InternalSubchannel.Callback;
import Status.RESOURCE_EXHAUSTED;
import Status.UNAVAILABLE;
import com.google.common.collect.Iterables;
import io.grpc.Attributes;
import io.grpc.ConnectivityState;
import io.grpc.ConnectivityStateInfo;
import io.grpc.EquivalentAddressGroup;
import io.grpc.InternalChannelz;
import io.grpc.InternalWithLogId;
import io.grpc.Status;
import io.grpc.SynchronizationContext;
import io.grpc.internal.InternalSubchannel.Index;
import io.grpc.internal.InternalSubchannel.TransportLogger;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


/**
 * Unit tests for {@link InternalSubchannel}.
 */
@RunWith(JUnit4.class)
public class InternalSubchannelTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private static final String AUTHORITY = "fakeauthority";

    private static final String USER_AGENT = "mosaic";

    private static final ConnectivityStateInfo UNAVAILABLE_STATE = ConnectivityStateInfo.forTransientFailure(UNAVAILABLE);

    private static final ConnectivityStateInfo RESOURCE_EXHAUSTED_STATE = ConnectivityStateInfo.forTransientFailure(RESOURCE_EXHAUSTED);

    private static final Status SHUTDOWN_REASON = UNAVAILABLE.withDescription("for test");

    // For scheduled executor
    private final FakeClock fakeClock = new FakeClock();

    // For syncContext
    private final FakeClock fakeExecutor = new FakeClock();

    private final SynchronizationContext syncContext = new SynchronizationContext(new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            throw new AssertionError(e);
        }
    });

    private final InternalChannelz channelz = new InternalChannelz();

    @Mock
    private BackoffPolicy mockBackoffPolicy1;

    @Mock
    private BackoffPolicy mockBackoffPolicy2;

    @Mock
    private BackoffPolicy mockBackoffPolicy3;

    @Mock
    private Provider mockBackoffPolicyProvider;

    @Mock
    private ClientTransportFactory mockTransportFactory;

    private final LinkedList<String> callbackInvokes = new LinkedList<>();

    private final Callback mockInternalSubchannelCallback = new InternalSubchannel.Callback() {
        @Override
        protected void onTerminated(InternalSubchannel is) {
            Assert.assertSame(internalSubchannel, is);
            callbackInvokes.add("onTerminated");
        }

        @Override
        protected void onStateChange(InternalSubchannel is, ConnectivityStateInfo newState) {
            Assert.assertSame(internalSubchannel, is);
            callbackInvokes.add(("onStateChange:" + newState));
        }

        @Override
        protected void onInUse(InternalSubchannel is) {
            Assert.assertSame(internalSubchannel, is);
            callbackInvokes.add("onInUse");
        }

        @Override
        protected void onNotInUse(InternalSubchannel is) {
            Assert.assertSame(internalSubchannel, is);
            callbackInvokes.add("onNotInUse");
        }
    };

    private InternalSubchannel internalSubchannel;

    private BlockingQueue<TestUtils.MockClientTransportInfo> transports;

    @Test(expected = IllegalArgumentException.class)
    public void constructor_emptyEagList_throws() {
        createInternalSubchannel(new EquivalentAddressGroup[0]);
    }

    @Test(expected = NullPointerException.class)
    public void constructor_eagListWithNull_throws() {
        createInternalSubchannel(new EquivalentAddressGroup[]{ null });
    }

    @Test
    public void eagAttribute_propagatesToTransport() {
        SocketAddress addr = new SocketAddress() {};
        Attributes attr = Attributes.newBuilder().set(Key.create("some-key"), "1").build();
        createInternalSubchannel(new EquivalentAddressGroup(Arrays.asList(addr), attr));
        // First attempt
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions().setEagAttributes(attr)), ArgumentMatchers.isA(TransportLogger.class));
    }

    @Test
    public void singleAddressReconnect() {
        SocketAddress addr = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr);
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        // Invocation counters
        int transportsCreated = 0;
        int backoff1Consulted = 0;
        int backoff2Consulted = 0;
        int backoffReset = 0;
        // First attempt
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        assertNoCallbackInvoke();
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsCreated))).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Fail this one. Because there is only one address to try, enter TRANSIENT_FAILURE.
        assertNoCallbackInvoke();
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        assertExactCallbackInvokes(("onStateChange:" + (InternalSubchannelTest.UNAVAILABLE_STATE)));
        // Backoff reset and using first back-off value interval
        Mockito.verify(mockBackoffPolicy1, Mockito.times((++backoff1Consulted))).nextBackoffNanos();
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times((++backoffReset))).get();
        // Second attempt
        // Transport creation doesn't happen until time is due
        fakeClock.forwardNanos(9);
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Mockito.verify(mockTransportFactory, Mockito.times(transportsCreated)).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        assertNoCallbackInvoke();
        fakeClock.forwardNanos(1);
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsCreated))).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Fail this one too
        assertNoCallbackInvoke();
        // Here we use a different status from the first failure, and verify that it's passed to
        // the callback.
        transports.poll().listener.transportShutdown(RESOURCE_EXHAUSTED);
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        assertExactCallbackInvokes(("onStateChange:" + (InternalSubchannelTest.RESOURCE_EXHAUSTED_STATE)));
        // Second back-off interval
        Mockito.verify(mockBackoffPolicy1, Mockito.times((++backoff1Consulted))).nextBackoffNanos();
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times(backoffReset)).get();
        // Third attempt
        // Transport creation doesn't happen until time is due
        fakeClock.forwardNanos(99);
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Mockito.verify(mockTransportFactory, Mockito.times(transportsCreated)).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        assertNoCallbackInvoke();
        fakeClock.forwardNanos(1);
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsCreated))).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Let this one succeed, will enter READY state.
        assertNoCallbackInvoke();
        transports.peek().listener.transportReady();
        assertExactCallbackInvokes("onStateChange:READY");
        Assert.assertEquals(ConnectivityState.READY, internalSubchannel.getState());
        Assert.assertSame(transports.peek().transport, delegate());
        // Close the READY transport, will enter IDLE state.
        assertNoCallbackInvoke();
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        assertExactCallbackInvokes("onStateChange:IDLE");
        // Back-off is reset, and the next attempt will happen immediately
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times(backoffReset)).get();
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsCreated))).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Final checks for consultations on back-off policies
        Mockito.verify(mockBackoffPolicy1, Mockito.times(backoff1Consulted)).nextBackoffNanos();
        Mockito.verify(mockBackoffPolicy2, Mockito.times(backoff2Consulted)).nextBackoffNanos();
    }

    @Test
    public void twoAddressesReconnect() {
        SocketAddress addr1 = Mockito.mock(SocketAddress.class);
        SocketAddress addr2 = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr1, addr2);
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        // Invocation counters
        int transportsAddr1 = 0;
        int transportsAddr2 = 0;
        int backoff1Consulted = 0;
        int backoff2Consulted = 0;
        int backoff3Consulted = 0;
        int backoffReset = 0;
        // First attempt
        assertNoCallbackInvoke();
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsAddr1))).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Let this one fail without success
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        // Still in CONNECTING
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        assertNoCallbackInvoke();
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        // Second attempt will start immediately. Still no back-off policy.
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times(backoffReset)).get();
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsAddr2))).newClientTransport(ArgumentMatchers.eq(addr2), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        // Fail this one too
        assertNoCallbackInvoke();
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        // All addresses have failed. Delayed transport will be in back-off interval.
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        assertExactCallbackInvokes(("onStateChange:" + (InternalSubchannelTest.UNAVAILABLE_STATE)));
        // Backoff reset and first back-off interval begins
        Mockito.verify(mockBackoffPolicy1, Mockito.times((++backoff1Consulted))).nextBackoffNanos();
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times((++backoffReset))).get();
        // No reconnect during TRANSIENT_FAILURE even when requested.
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        assertNoCallbackInvoke();
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        // Third attempt is the first address, thus controlled by the first back-off interval.
        fakeClock.forwardNanos(9);
        Mockito.verify(mockTransportFactory, Mockito.times(transportsAddr1)).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        assertNoCallbackInvoke();
        fakeClock.forwardNanos(1);
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsAddr1))).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Fail this one too
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        // Forth attempt will start immediately. Keep back-off policy.
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times(backoffReset)).get();
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsAddr2))).newClientTransport(ArgumentMatchers.eq(addr2), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Fail this one too
        assertNoCallbackInvoke();
        transports.poll().listener.transportShutdown(RESOURCE_EXHAUSTED);
        // All addresses have failed again. Delayed transport will be in back-off interval.
        assertExactCallbackInvokes(("onStateChange:" + (InternalSubchannelTest.RESOURCE_EXHAUSTED_STATE)));
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        // Second back-off interval begins
        Mockito.verify(mockBackoffPolicy1, Mockito.times((++backoff1Consulted))).nextBackoffNanos();
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times(backoffReset)).get();
        // Fifth attempt for the first address, thus controlled by the second back-off interval.
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        fakeClock.forwardNanos(99);
        Mockito.verify(mockTransportFactory, Mockito.times(transportsAddr1)).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        assertNoCallbackInvoke();
        fakeClock.forwardNanos(1);
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsAddr1))).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Let it through
        assertNoCallbackInvoke();
        transports.peek().listener.transportReady();
        assertExactCallbackInvokes("onStateChange:READY");
        Assert.assertEquals(ConnectivityState.READY, internalSubchannel.getState());
        Assert.assertSame(transports.peek().transport, delegate());
        // Then close it.
        assertNoCallbackInvoke();
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        assertExactCallbackInvokes("onStateChange:IDLE");
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        // First attempt after a successful connection. Old back-off policy should be ignored, but there
        // is not yet a need for a new one. Start from the first address.
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times(backoffReset)).get();
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsAddr1))).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Fail the transport
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        // Second attempt will start immediately. Still no new back-off policy.
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times(backoffReset)).get();
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsAddr2))).newClientTransport(ArgumentMatchers.eq(addr2), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Fail this one too
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        // All addresses have failed. Enter TRANSIENT_FAILURE. Back-off in effect.
        assertExactCallbackInvokes(("onStateChange:" + (InternalSubchannelTest.UNAVAILABLE_STATE)));
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        // Back-off reset and first back-off interval begins
        Mockito.verify(mockBackoffPolicy2, Mockito.times((++backoff2Consulted))).nextBackoffNanos();
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times((++backoffReset))).get();
        // Third attempt is the first address, thus controlled by the first back-off interval.
        fakeClock.forwardNanos(9);
        Mockito.verify(mockTransportFactory, Mockito.times(transportsAddr1)).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        assertNoCallbackInvoke();
        fakeClock.forwardNanos(1);
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsAddr1))).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Final checks on invocations on back-off policies
        Mockito.verify(mockBackoffPolicy1, Mockito.times(backoff1Consulted)).nextBackoffNanos();
        Mockito.verify(mockBackoffPolicy2, Mockito.times(backoff2Consulted)).nextBackoffNanos();
        Mockito.verify(mockBackoffPolicy3, Mockito.times(backoff3Consulted)).nextBackoffNanos();
    }

    @Test
    public void updateAddresses_emptyEagList_throws() {
        SocketAddress addr = new InternalSubchannelTest.FakeSocketAddress();
        createInternalSubchannel(addr);
        thrown.expect(IllegalArgumentException.class);
        internalSubchannel.updateAddresses(Arrays.<EquivalentAddressGroup>asList());
    }

    @Test
    public void updateAddresses_eagListWithNull_throws() {
        SocketAddress addr = new InternalSubchannelTest.FakeSocketAddress();
        createInternalSubchannel(addr);
        List<EquivalentAddressGroup> eags = Arrays.asList(((EquivalentAddressGroup) (null)));
        thrown.expect(NullPointerException.class);
        internalSubchannel.updateAddresses(eags);
    }

    @Test
    public void updateAddresses_intersecting_ready() {
        SocketAddress addr1 = Mockito.mock(SocketAddress.class);
        SocketAddress addr2 = Mockito.mock(SocketAddress.class);
        SocketAddress addr3 = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr1, addr2);
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        // First address fails
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        // Second address connects
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr2), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.peek().listener.transportReady();
        assertExactCallbackInvokes("onStateChange:READY");
        Assert.assertEquals(ConnectivityState.READY, internalSubchannel.getState());
        // Update addresses
        internalSubchannel.updateAddresses(Arrays.asList(new EquivalentAddressGroup(Arrays.asList(addr2, addr3))));
        assertNoCallbackInvoke();
        Assert.assertEquals(ConnectivityState.READY, internalSubchannel.getState());
        Mockito.verify(transports.peek().transport, Mockito.never()).shutdown(ArgumentMatchers.any(Status.class));
        Mockito.verify(transports.peek().transport, Mockito.never()).shutdownNow(ArgumentMatchers.any(Status.class));
        // And new addresses chosen when re-connecting
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        assertExactCallbackInvokes("onStateChange:IDLE");
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(mockTransportFactory, Mockito.times(2)).newClientTransport(ArgumentMatchers.eq(addr2), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr3), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Mockito.verifyNoMoreInteractions(mockTransportFactory);
        fakeClock.forwardNanos(10);// Drain retry, but don't care about result

    }

    @Test
    public void updateAddresses_intersecting_connecting() {
        SocketAddress addr1 = Mockito.mock(SocketAddress.class);
        SocketAddress addr2 = Mockito.mock(SocketAddress.class);
        SocketAddress addr3 = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr1, addr2);
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        // First address fails
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        // Second address connecting
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr2), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        assertNoCallbackInvoke();
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        // Update addresses
        internalSubchannel.updateAddresses(Arrays.asList(new EquivalentAddressGroup(Arrays.asList(addr2, addr3))));
        assertNoCallbackInvoke();
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        Mockito.verify(transports.peek().transport, Mockito.never()).shutdown(ArgumentMatchers.any(Status.class));
        Mockito.verify(transports.peek().transport, Mockito.never()).shutdownNow(ArgumentMatchers.any(Status.class));
        // And new addresses chosen when re-connecting
        transports.peek().listener.transportReady();
        assertExactCallbackInvokes("onStateChange:READY");
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        assertExactCallbackInvokes("onStateChange:IDLE");
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(mockTransportFactory, Mockito.times(2)).newClientTransport(ArgumentMatchers.eq(addr2), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr3), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Mockito.verifyNoMoreInteractions(mockTransportFactory);
        fakeClock.forwardNanos(10);// Drain retry, but don't care about result

    }

    @Test
    public void updateAddresses_disjoint_idle() {
        SocketAddress addr1 = Mockito.mock(SocketAddress.class);
        SocketAddress addr2 = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr1);
        internalSubchannel.updateAddresses(Arrays.asList(new EquivalentAddressGroup(addr2)));
        // Nothing happened on address update
        Mockito.verify(mockTransportFactory, Mockito.never()).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        Mockito.verify(mockTransportFactory, Mockito.never()).newClientTransport(ArgumentMatchers.eq(addr2), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        Mockito.verifyNoMoreInteractions(mockTransportFactory);
        assertNoCallbackInvoke();
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        // But new address chosen when connecting
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr2), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // And no other addresses attempted
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        assertExactCallbackInvokes(("onStateChange:" + (InternalSubchannelTest.UNAVAILABLE_STATE)));
        Assert.assertEquals(ConnectivityState.TRANSIENT_FAILURE, internalSubchannel.getState());
        Mockito.verifyNoMoreInteractions(mockTransportFactory);
        fakeClock.forwardNanos(10);// Drain retry, but don't care about result

    }

    @Test
    public void updateAddresses_disjoint_ready() {
        SocketAddress addr1 = Mockito.mock(SocketAddress.class);
        SocketAddress addr2 = Mockito.mock(SocketAddress.class);
        SocketAddress addr3 = Mockito.mock(SocketAddress.class);
        SocketAddress addr4 = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr1, addr2);
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        // First address fails
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        // Second address connects
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr2), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.peek().listener.transportReady();
        assertExactCallbackInvokes("onStateChange:READY");
        Assert.assertEquals(ConnectivityState.READY, internalSubchannel.getState());
        // Update addresses
        internalSubchannel.updateAddresses(Arrays.asList(new EquivalentAddressGroup(Arrays.asList(addr3, addr4))));
        assertExactCallbackInvokes("onStateChange:IDLE");
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        Mockito.verify(transports.peek().transport).shutdown(ArgumentMatchers.any(Status.class));
        // And new addresses chosen when re-connecting
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        assertNoCallbackInvoke();
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr3), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr4), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Mockito.verifyNoMoreInteractions(mockTransportFactory);
        fakeClock.forwardNanos(10);// Drain retry, but don't care about result

    }

    @Test
    public void updateAddresses_disjoint_connecting() {
        SocketAddress addr1 = Mockito.mock(SocketAddress.class);
        SocketAddress addr2 = Mockito.mock(SocketAddress.class);
        SocketAddress addr3 = Mockito.mock(SocketAddress.class);
        SocketAddress addr4 = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr1, addr2);
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        // First address fails
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr1), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        // Second address connecting
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr2), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        assertNoCallbackInvoke();
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        // Update addresses
        internalSubchannel.updateAddresses(Arrays.asList(new EquivalentAddressGroup(Arrays.asList(addr3, addr4))));
        assertNoCallbackInvoke();
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        // And new addresses chosen immediately
        Mockito.verify(transports.poll().transport).shutdown(ArgumentMatchers.any(Status.class));
        assertNoCallbackInvoke();
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr3), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr4), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        Mockito.verifyNoMoreInteractions(mockTransportFactory);
        fakeClock.forwardNanos(10);// Drain retry, but don't care about result

    }

    @Test
    public void connectIsLazy() {
        SocketAddress addr = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr);
        // Invocation counters
        int transportsCreated = 0;
        // Won't connect until requested
        Mockito.verify(mockTransportFactory, Mockito.times(transportsCreated)).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // First attempt
        internalSubchannel.obtainActiveTransport();
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsCreated))).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Fail this one
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        assertExactCallbackInvokes(("onStateChange:" + (InternalSubchannelTest.UNAVAILABLE_STATE)));
        // Will always reconnect after back-off
        fakeClock.forwardNanos(10);
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsCreated))).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Make this one proceed
        transports.peek().listener.transportReady();
        assertExactCallbackInvokes("onStateChange:READY");
        // Then go-away
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        assertExactCallbackInvokes("onStateChange:IDLE");
        // No scheduled tasks that would ever try to reconnect ...
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Assert.assertEquals(0, fakeExecutor.numPendingTasks());
        // ... until it's requested.
        internalSubchannel.obtainActiveTransport();
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockTransportFactory, Mockito.times((++transportsCreated))).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
    }

    @Test
    public void shutdownWhenReady() throws Exception {
        SocketAddress addr = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr);
        internalSubchannel.obtainActiveTransport();
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        transportInfo.listener.transportReady();
        assertExactCallbackInvokes("onStateChange:CONNECTING", "onStateChange:READY");
        internalSubchannel.shutdown(InternalSubchannelTest.SHUTDOWN_REASON);
        Mockito.verify(transportInfo.transport).shutdown(ArgumentMatchers.same(InternalSubchannelTest.SHUTDOWN_REASON));
        assertExactCallbackInvokes("onStateChange:SHUTDOWN");
        transportInfo.listener.transportTerminated();
        assertExactCallbackInvokes("onTerminated");
        Mockito.verify(transportInfo.transport, Mockito.never()).shutdownNow(ArgumentMatchers.any(Status.class));
    }

    @Test
    public void shutdownBeforeTransportCreated() throws Exception {
        SocketAddress addr = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr);
        // First transport is created immediately
        internalSubchannel.obtainActiveTransport();
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        // Fail this one
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        transportInfo.listener.transportShutdown(UNAVAILABLE);
        transportInfo.listener.transportTerminated();
        // Entering TRANSIENT_FAILURE, waiting for back-off
        assertExactCallbackInvokes(("onStateChange:" + (InternalSubchannelTest.UNAVAILABLE_STATE)));
        // Save the reconnectTask before shutting down
        FakeClock.ScheduledTask reconnectTask = null;
        for (FakeClock.ScheduledTask task : fakeClock.getPendingTasks()) {
            if (task.command.toString().contains("EndOfCurrentBackoff")) {
                Assert.assertNull("There shouldn't be more than one reconnectTask", reconnectTask);
                Assert.assertFalse(task.isDone());
                reconnectTask = task;
            }
        }
        Assert.assertNotNull("There should be at least one reconnectTask", reconnectTask);
        // Shut down InternalSubchannel before the transport is created.
        internalSubchannel.shutdown(InternalSubchannelTest.SHUTDOWN_REASON);
        Assert.assertTrue(reconnectTask.isCancelled());
        // InternalSubchannel terminated promptly.
        assertExactCallbackInvokes("onStateChange:SHUTDOWN", "onTerminated");
        // Simulate a race between reconnectTask cancellation and execution -- the task runs anyway.
        // This should not lead to the creation of a new transport.
        reconnectTask.command.run();
        // Futher call to obtainActiveTransport() is no-op.
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Assert.assertEquals(ConnectivityState.SHUTDOWN, internalSubchannel.getState());
        assertNoCallbackInvoke();
        // No more transports will be created.
        fakeClock.forwardNanos(10000);
        Assert.assertEquals(ConnectivityState.SHUTDOWN, internalSubchannel.getState());
        Mockito.verifyNoMoreInteractions(mockTransportFactory);
        Assert.assertEquals(0, transports.size());
        assertNoCallbackInvoke();
    }

    @Test
    public void shutdownBeforeTransportReady() throws Exception {
        SocketAddress addr = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr);
        internalSubchannel.obtainActiveTransport();
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        TestUtils.MockClientTransportInfo transportInfo = transports.poll();
        // Shutdown the InternalSubchannel before the pending transport is ready
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        internalSubchannel.shutdown(InternalSubchannelTest.SHUTDOWN_REASON);
        assertExactCallbackInvokes("onStateChange:SHUTDOWN");
        // The transport should've been shut down even though it's not the active transport yet.
        Mockito.verify(transportInfo.transport).shutdown(ArgumentMatchers.same(InternalSubchannelTest.SHUTDOWN_REASON));
        transportInfo.listener.transportShutdown(UNAVAILABLE);
        assertNoCallbackInvoke();
        transportInfo.listener.transportTerminated();
        assertExactCallbackInvokes("onTerminated");
        Assert.assertEquals(ConnectivityState.SHUTDOWN, internalSubchannel.getState());
    }

    @Test
    public void shutdownNow() throws Exception {
        SocketAddress addr = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr);
        internalSubchannel.obtainActiveTransport();
        TestUtils.MockClientTransportInfo t1 = transports.poll();
        t1.listener.transportReady();
        assertExactCallbackInvokes("onStateChange:CONNECTING", "onStateChange:READY");
        t1.listener.transportShutdown(UNAVAILABLE);
        assertExactCallbackInvokes("onStateChange:IDLE");
        internalSubchannel.obtainActiveTransport();
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        TestUtils.MockClientTransportInfo t2 = transports.poll();
        Status status = UNAVAILABLE.withDescription("Requested");
        internalSubchannel.shutdownNow(status);
        Mockito.verify(t1.transport).shutdownNow(ArgumentMatchers.same(status));
        Mockito.verify(t2.transport).shutdownNow(ArgumentMatchers.same(status));
        assertExactCallbackInvokes("onStateChange:SHUTDOWN");
    }

    @Test
    public void obtainTransportAfterShutdown() throws Exception {
        SocketAddress addr = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr);
        internalSubchannel.shutdown(InternalSubchannelTest.SHUTDOWN_REASON);
        assertExactCallbackInvokes("onStateChange:SHUTDOWN", "onTerminated");
        Assert.assertEquals(ConnectivityState.SHUTDOWN, internalSubchannel.getState());
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        Mockito.verify(mockTransportFactory, Mockito.times(0)).newClientTransport(addr, createClientTransportOptions(), internalSubchannel.getChannelLogger());
        assertNoCallbackInvoke();
        Assert.assertEquals(ConnectivityState.SHUTDOWN, internalSubchannel.getState());
    }

    @Test
    public void logId() {
        createInternalSubchannel(Mockito.mock(SocketAddress.class));
        Assert.assertNotNull(internalSubchannel.getLogId());
    }

    @Test
    public void inUseState() {
        SocketAddress addr = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr);
        internalSubchannel.obtainActiveTransport();
        TestUtils.MockClientTransportInfo t0 = transports.poll();
        t0.listener.transportReady();
        assertExactCallbackInvokes("onStateChange:CONNECTING", "onStateChange:READY");
        t0.listener.transportInUse(true);
        assertExactCallbackInvokes("onInUse");
        t0.listener.transportInUse(false);
        assertExactCallbackInvokes("onNotInUse");
        t0.listener.transportInUse(true);
        assertExactCallbackInvokes("onInUse");
        t0.listener.transportShutdown(UNAVAILABLE);
        assertExactCallbackInvokes("onStateChange:IDLE");
        Assert.assertNull(internalSubchannel.obtainActiveTransport());
        TestUtils.MockClientTransportInfo t1 = transports.poll();
        t1.listener.transportReady();
        assertExactCallbackInvokes("onStateChange:CONNECTING", "onStateChange:READY");
        t1.listener.transportInUse(true);
        // InternalSubchannel is already in-use, thus doesn't call the callback
        assertNoCallbackInvoke();
        t1.listener.transportInUse(false);
        // t0 is still in-use
        assertNoCallbackInvoke();
        t0.listener.transportInUse(false);
        assertExactCallbackInvokes("onNotInUse");
    }

    @Test
    public void transportTerminateWithoutExitingInUse() {
        // An imperfect transport that terminates without going out of in-use. InternalSubchannel will
        // clear the in-use bit for it.
        SocketAddress addr = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr);
        internalSubchannel.obtainActiveTransport();
        TestUtils.MockClientTransportInfo t0 = transports.poll();
        t0.listener.transportReady();
        assertExactCallbackInvokes("onStateChange:CONNECTING", "onStateChange:READY");
        t0.listener.transportInUse(true);
        assertExactCallbackInvokes("onInUse");
        t0.listener.transportShutdown(UNAVAILABLE);
        assertExactCallbackInvokes("onStateChange:IDLE");
        t0.listener.transportTerminated();
        assertExactCallbackInvokes("onNotInUse");
    }

    @Test
    public void transportStartReturnsRunnable() {
        SocketAddress addr1 = Mockito.mock(SocketAddress.class);
        SocketAddress addr2 = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr1, addr2);
        final AtomicInteger runnableInvokes = new AtomicInteger(0);
        Runnable startRunnable = new Runnable() {
            @Override
            public void run() {
                runnableInvokes.incrementAndGet();
            }
        };
        transports = TestUtils.captureTransports(mockTransportFactory, startRunnable);
        Assert.assertEquals(0, runnableInvokes.get());
        internalSubchannel.obtainActiveTransport();
        Assert.assertEquals(1, runnableInvokes.get());
        internalSubchannel.obtainActiveTransport();
        Assert.assertEquals(1, runnableInvokes.get());
        TestUtils.MockClientTransportInfo t0 = transports.poll();
        t0.listener.transportShutdown(UNAVAILABLE);
        Assert.assertEquals(2, runnableInvokes.get());
        // 2nd address: reconnect immediatly
        TestUtils.MockClientTransportInfo t1 = transports.poll();
        t1.listener.transportShutdown(UNAVAILABLE);
        // Addresses exhausted, waiting for back-off.
        Assert.assertEquals(2, runnableInvokes.get());
        // Run out the back-off period
        fakeClock.forwardNanos(10);
        Assert.assertEquals(3, runnableInvokes.get());
        // This test doesn't care about scheduled InternalSubchannel callbacks.  Clear it up so that
        // noMorePendingTasks() won't fail.
        fakeExecutor.runDueTasks();
        Assert.assertEquals(3, runnableInvokes.get());
    }

    @Test
    public void resetConnectBackoff() throws Exception {
        SocketAddress addr = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr);
        // Move into TRANSIENT_FAILURE to schedule reconnect
        internalSubchannel.obtainActiveTransport();
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Mockito.verify(mockTransportFactory).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        assertExactCallbackInvokes(("onStateChange:" + (InternalSubchannelTest.UNAVAILABLE_STATE)));
        // Save the reconnectTask
        FakeClock.ScheduledTask reconnectTask = null;
        for (FakeClock.ScheduledTask task : fakeClock.getPendingTasks()) {
            if (task.command.toString().contains("EndOfCurrentBackoff")) {
                Assert.assertNull("There shouldn't be more than one reconnectTask", reconnectTask);
                Assert.assertFalse(task.isDone());
                reconnectTask = task;
            }
        }
        Assert.assertNotNull("There should be at least one reconnectTask", reconnectTask);
        internalSubchannel.resetConnectBackoff();
        Mockito.verify(mockTransportFactory, Mockito.times(2)).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Assert.assertTrue(reconnectTask.isCancelled());
        // Simulate a race between cancel and the task scheduler. Should be a no-op.
        reconnectTask.command.run();
        assertNoCallbackInvoke();
        Mockito.verify(mockTransportFactory, Mockito.times(2)).newClientTransport(ArgumentMatchers.eq(addr), ArgumentMatchers.eq(createClientTransportOptions()), ArgumentMatchers.isA(TransportLogger.class));
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times(1)).get();
        // Fail the reconnect attempt to verify that a fresh reconnect policy is generated after
        // invoking resetConnectBackoff()
        transports.poll().listener.transportShutdown(UNAVAILABLE);
        assertExactCallbackInvokes(("onStateChange:" + (InternalSubchannelTest.UNAVAILABLE_STATE)));
        Mockito.verify(mockBackoffPolicyProvider, Mockito.times(2)).get();
        fakeClock.forwardNanos(10);
        assertExactCallbackInvokes("onStateChange:CONNECTING");
        Assert.assertEquals(ConnectivityState.CONNECTING, internalSubchannel.getState());
    }

    @Test
    public void resetConnectBackoff_noopOnIdleTransport() throws Exception {
        SocketAddress addr = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr);
        Assert.assertEquals(ConnectivityState.IDLE, internalSubchannel.getState());
        internalSubchannel.resetConnectBackoff();
        assertNoCallbackInvoke();
    }

    @Test
    public void channelzMembership() throws Exception {
        SocketAddress addr1 = Mockito.mock(SocketAddress.class);
        createInternalSubchannel(addr1);
        internalSubchannel.obtainActiveTransport();
        TestUtils.MockClientTransportInfo t0 = transports.poll();
        Assert.assertTrue(channelz.containsClientSocket(t0.transport.getLogId()));
        t0.listener.transportTerminated();
        Assert.assertFalse(channelz.containsClientSocket(t0.transport.getLogId()));
    }

    @Test
    public void channelzStatContainsTransport() throws Exception {
        SocketAddress addr = new SocketAddress() {};
        assertThat(transports).isEmpty();
        createInternalSubchannel(addr);
        internalSubchannel.obtainActiveTransport();
        InternalWithLogId registeredTransport = Iterables.getOnlyElement(internalSubchannel.getStats().get().sockets);
        TestUtils.MockClientTransportInfo actualTransport = Iterables.getOnlyElement(transports);
        Assert.assertEquals(actualTransport.transport.getLogId(), registeredTransport.getLogId());
    }

    @Test
    public void index_looping() {
        Attributes.Key<String> key = Key.create("some-key");
        Attributes attr1 = Attributes.newBuilder().set(key, "1").build();
        Attributes attr2 = Attributes.newBuilder().set(key, "2").build();
        Attributes attr3 = Attributes.newBuilder().set(key, "3").build();
        SocketAddress addr1 = new InternalSubchannelTest.FakeSocketAddress();
        SocketAddress addr2 = new InternalSubchannelTest.FakeSocketAddress();
        SocketAddress addr3 = new InternalSubchannelTest.FakeSocketAddress();
        SocketAddress addr4 = new InternalSubchannelTest.FakeSocketAddress();
        SocketAddress addr5 = new InternalSubchannelTest.FakeSocketAddress();
        Index index = new Index(Arrays.asList(new EquivalentAddressGroup(Arrays.asList(addr1, addr2), attr1), new EquivalentAddressGroup(Arrays.asList(addr3), attr2), new EquivalentAddressGroup(Arrays.asList(addr4, addr5), attr3)));
        assertThat(index.getCurrentAddress()).isSameAs(addr1);
        assertThat(index.getCurrentEagAttributes()).isSameAs(attr1);
        assertThat(index.isAtBeginning()).isTrue();
        assertThat(index.isValid()).isTrue();
        index.increment();
        assertThat(index.getCurrentAddress()).isSameAs(addr2);
        assertThat(index.getCurrentEagAttributes()).isSameAs(attr1);
        assertThat(index.isAtBeginning()).isFalse();
        assertThat(index.isValid()).isTrue();
        index.increment();
        assertThat(index.getCurrentAddress()).isSameAs(addr3);
        assertThat(index.getCurrentEagAttributes()).isSameAs(attr2);
        assertThat(index.isAtBeginning()).isFalse();
        assertThat(index.isValid()).isTrue();
        index.increment();
        assertThat(index.getCurrentAddress()).isSameAs(addr4);
        assertThat(index.getCurrentEagAttributes()).isSameAs(attr3);
        assertThat(index.isAtBeginning()).isFalse();
        assertThat(index.isValid()).isTrue();
        index.increment();
        assertThat(index.getCurrentAddress()).isSameAs(addr5);
        assertThat(index.getCurrentEagAttributes()).isSameAs(attr3);
        assertThat(index.isAtBeginning()).isFalse();
        assertThat(index.isValid()).isTrue();
        index.increment();
        assertThat(index.isAtBeginning()).isFalse();
        assertThat(index.isValid()).isFalse();
        index.reset();
        assertThat(index.getCurrentAddress()).isSameAs(addr1);
        assertThat(index.getCurrentEagAttributes()).isSameAs(attr1);
        assertThat(index.isAtBeginning()).isTrue();
        assertThat(index.isValid()).isTrue();
        // We want to make sure both groupIndex and addressIndex are reset
        index.increment();
        index.increment();
        index.increment();
        index.increment();
        assertThat(index.getCurrentAddress()).isSameAs(addr5);
        assertThat(index.getCurrentEagAttributes()).isSameAs(attr3);
        index.reset();
        assertThat(index.getCurrentAddress()).isSameAs(addr1);
        assertThat(index.getCurrentEagAttributes()).isSameAs(attr1);
    }

    @Test
    public void index_updateGroups_resets() {
        SocketAddress addr1 = new InternalSubchannelTest.FakeSocketAddress();
        SocketAddress addr2 = new InternalSubchannelTest.FakeSocketAddress();
        SocketAddress addr3 = new InternalSubchannelTest.FakeSocketAddress();
        Index index = new Index(Arrays.asList(new EquivalentAddressGroup(Arrays.asList(addr1)), new EquivalentAddressGroup(Arrays.asList(addr2, addr3))));
        index.increment();
        index.increment();
        // We want to make sure both groupIndex and addressIndex are reset
        index.updateGroups(Arrays.asList(new EquivalentAddressGroup(Arrays.asList(addr1)), new EquivalentAddressGroup(Arrays.asList(addr2, addr3))));
        assertThat(index.getCurrentAddress()).isSameAs(addr1);
    }

    @Test
    public void index_seekTo() {
        SocketAddress addr1 = new InternalSubchannelTest.FakeSocketAddress();
        SocketAddress addr2 = new InternalSubchannelTest.FakeSocketAddress();
        SocketAddress addr3 = new InternalSubchannelTest.FakeSocketAddress();
        Index index = new Index(Arrays.asList(new EquivalentAddressGroup(Arrays.asList(addr1, addr2)), new EquivalentAddressGroup(Arrays.asList(addr3))));
        assertThat(index.seekTo(addr3)).isTrue();
        assertThat(index.getCurrentAddress()).isSameAs(addr3);
        assertThat(index.seekTo(addr1)).isTrue();
        assertThat(index.getCurrentAddress()).isSameAs(addr1);
        assertThat(index.seekTo(addr2)).isTrue();
        assertThat(index.getCurrentAddress()).isSameAs(addr2);
        index.seekTo(new InternalSubchannelTest.FakeSocketAddress());
        // Failed seekTo doesn't change the index
        assertThat(index.getCurrentAddress()).isSameAs(addr2);
    }

    private static class FakeSocketAddress extends SocketAddress {}
}

