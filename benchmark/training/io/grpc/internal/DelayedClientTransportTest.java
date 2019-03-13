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


import CallOptions.DEFAULT;
import CallOptions.Key;
import ManagedClientTransport.Listener;
import MethodType.UNKNOWN;
import Status.CANCELLED;
import Status.RESOURCE_EXHAUSTED;
import Status.UNAVAILABLE;
import io.grpc.CallOptions;
import io.grpc.IntegerMarshaller;
import io.grpc.LoadBalancer.PickResult;
import io.grpc.LoadBalancer.PickSubchannelArgs;
import io.grpc.LoadBalancer.SubchannelPicker;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.StringMarshaller;
import io.grpc.SynchronizationContext;
import io.grpc.internal.ClientStreamListener.RpcProgress;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;


/**
 * Unit tests for {@link DelayedClientTransport}.
 */
@RunWith(JUnit4.class)
public class DelayedClientTransportTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Mock
    private Listener transportListener;

    @Mock
    private SubchannelPicker mockPicker;

    @Mock
    private AbstractSubchannel mockSubchannel;

    @Mock
    private ClientTransport mockRealTransport;

    @Mock
    private ClientTransport mockRealTransport2;

    @Mock
    private ClientStream mockRealStream;

    @Mock
    private ClientStream mockRealStream2;

    @Mock
    private ClientStreamListener streamListener;

    @Mock
    private Executor mockExecutor;

    @Captor
    private ArgumentCaptor<Status> statusCaptor;

    @Captor
    private ArgumentCaptor<ClientStreamListener> listenerCaptor;

    private static final CallOptions.Key<Integer> SHARD_ID = Key.createWithDefault("shard-id", (-1));

    private static final Status SHUTDOWN_STATUS = UNAVAILABLE.withDescription("shutdown called");

    private final MethodDescriptor<String, Integer> method = MethodDescriptor.<String, Integer>newBuilder().setType(UNKNOWN).setFullMethodName("service/method").setRequestMarshaller(new StringMarshaller()).setResponseMarshaller(new IntegerMarshaller()).build();

    private final MethodDescriptor<String, Integer> method2 = method.toBuilder().setFullMethodName("service/method").build();

    private final Metadata headers = new Metadata();

    private final Metadata headers2 = new Metadata();

    private final CallOptions callOptions = DEFAULT.withAuthority("dummy_value");

    private final CallOptions callOptions2 = DEFAULT.withAuthority("dummy_value2");

    private final FakeClock fakeExecutor = new FakeClock();

    private final DelayedClientTransport delayedTransport = new DelayedClientTransport(fakeExecutor.getScheduledExecutorService(), new SynchronizationContext(new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            throw new AssertionError(e);
        }
    }));

    @Test
    public void streamStartThenAssignTransport() {
        Assert.assertFalse(delayedTransport.hasPendingStreams());
        ClientStream stream = delayedTransport.newStream(method, headers, callOptions);
        stream.start(streamListener);
        Assert.assertEquals(1, delayedTransport.getPendingStreamsCount());
        Assert.assertTrue(delayedTransport.hasPendingStreams());
        Assert.assertTrue((stream instanceof DelayedStream));
        Assert.assertEquals(0, fakeExecutor.numPendingTasks());
        delayedTransport.reprocess(mockPicker);
        Assert.assertEquals(0, delayedTransport.getPendingStreamsCount());
        Assert.assertFalse(delayedTransport.hasPendingStreams());
        Assert.assertEquals(1, fakeExecutor.runDueTasks());
        Mockito.verify(mockRealTransport).newStream(ArgumentMatchers.same(method), ArgumentMatchers.same(headers), ArgumentMatchers.same(callOptions));
        Mockito.verify(mockRealStream).start(listenerCaptor.capture());
        Mockito.verifyNoMoreInteractions(streamListener);
        listenerCaptor.getValue().onReady();
        Mockito.verify(streamListener).onReady();
        Mockito.verifyNoMoreInteractions(streamListener);
    }

    @Test
    public void newStreamThenAssignTransportThenShutdown() {
        ClientStream stream = delayedTransport.newStream(method, headers, callOptions);
        Assert.assertEquals(1, delayedTransport.getPendingStreamsCount());
        Assert.assertTrue((stream instanceof DelayedStream));
        delayedTransport.reprocess(mockPicker);
        Assert.assertEquals(0, delayedTransport.getPendingStreamsCount());
        delayedTransport.shutdown(DelayedClientTransportTest.SHUTDOWN_STATUS);
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.same(DelayedClientTransportTest.SHUTDOWN_STATUS));
        Mockito.verify(transportListener).transportTerminated();
        Assert.assertEquals(1, fakeExecutor.runDueTasks());
        Mockito.verify(mockRealTransport).newStream(ArgumentMatchers.same(method), ArgumentMatchers.same(headers), ArgumentMatchers.same(callOptions));
        stream.start(streamListener);
        Mockito.verify(mockRealStream).start(ArgumentMatchers.same(streamListener));
    }

    @Test
    public void transportTerminatedThenAssignTransport() {
        delayedTransport.shutdown(DelayedClientTransportTest.SHUTDOWN_STATUS);
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.same(DelayedClientTransportTest.SHUTDOWN_STATUS));
        Mockito.verify(transportListener).transportTerminated();
        delayedTransport.reprocess(mockPicker);
        Mockito.verifyNoMoreInteractions(transportListener);
    }

    @Test
    public void assignTransportThenShutdownThenNewStream() {
        delayedTransport.reprocess(mockPicker);
        delayedTransport.shutdown(DelayedClientTransportTest.SHUTDOWN_STATUS);
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.same(DelayedClientTransportTest.SHUTDOWN_STATUS));
        Mockito.verify(transportListener).transportTerminated();
        ClientStream stream = delayedTransport.newStream(method, headers, callOptions);
        Assert.assertEquals(0, delayedTransport.getPendingStreamsCount());
        Assert.assertTrue((stream instanceof FailingClientStream));
        Mockito.verify(mockRealTransport, Mockito.never()).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
    }

    @Test
    public void assignTransportThenShutdownNowThenNewStream() {
        delayedTransport.reprocess(mockPicker);
        delayedTransport.shutdownNow(UNAVAILABLE);
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.any(Status.class));
        Mockito.verify(transportListener).transportTerminated();
        ClientStream stream = delayedTransport.newStream(method, headers, callOptions);
        Assert.assertEquals(0, delayedTransport.getPendingStreamsCount());
        Assert.assertTrue((stream instanceof FailingClientStream));
        Mockito.verify(mockRealTransport, Mockito.never()).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
    }

    @Test
    public void cancelStreamWithoutSetTransport() {
        ClientStream stream = delayedTransport.newStream(method, new Metadata(), DEFAULT);
        Assert.assertEquals(1, delayedTransport.getPendingStreamsCount());
        stream.cancel(CANCELLED);
        Assert.assertEquals(0, delayedTransport.getPendingStreamsCount());
        Mockito.verifyNoMoreInteractions(mockRealTransport);
        Mockito.verifyNoMoreInteractions(mockRealStream);
    }

    @Test
    public void startThenCancelStreamWithoutSetTransport() {
        ClientStream stream = delayedTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(streamListener);
        Assert.assertEquals(1, delayedTransport.getPendingStreamsCount());
        stream.cancel(CANCELLED);
        Assert.assertEquals(0, delayedTransport.getPendingStreamsCount());
        Mockito.verify(streamListener).closed(ArgumentMatchers.same(CANCELLED), ArgumentMatchers.any(Metadata.class));
        Mockito.verifyNoMoreInteractions(mockRealTransport);
        Mockito.verifyNoMoreInteractions(mockRealStream);
    }

    @Test
    public void newStreamThenShutdownTransportThenAssignTransport() {
        ClientStream stream = delayedTransport.newStream(method, headers, callOptions);
        stream.start(streamListener);
        delayedTransport.shutdown(DelayedClientTransportTest.SHUTDOWN_STATUS);
        // Stream is still buffered
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.same(DelayedClientTransportTest.SHUTDOWN_STATUS));
        Mockito.verify(transportListener, Mockito.times(0)).transportTerminated();
        Assert.assertEquals(1, delayedTransport.getPendingStreamsCount());
        // ... and will proceed if a real transport is available
        delayedTransport.reprocess(mockPicker);
        fakeExecutor.runDueTasks();
        Mockito.verify(mockRealTransport).newStream(method, headers, callOptions);
        Mockito.verify(mockRealStream).start(ArgumentMatchers.any(ClientStreamListener.class));
        // Since no more streams are pending, delayed transport is now terminated
        Assert.assertEquals(0, delayedTransport.getPendingStreamsCount());
        Mockito.verify(transportListener).transportTerminated();
        // Further newStream() will return a failing stream
        stream = delayedTransport.newStream(method, new Metadata(), DEFAULT);
        Mockito.verify(streamListener, Mockito.never()).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
        stream.start(streamListener);
        Mockito.verify(streamListener).closed(statusCaptor.capture(), ArgumentMatchers.any(RpcProgress.class), ArgumentMatchers.any(Metadata.class));
        Assert.assertEquals(Status.Code.UNAVAILABLE, statusCaptor.getValue().getCode());
        Assert.assertEquals(0, delayedTransport.getPendingStreamsCount());
        Mockito.verifyNoMoreInteractions(mockRealTransport);
        Mockito.verifyNoMoreInteractions(mockRealStream);
    }

    @Test
    public void newStreamThenShutdownTransportThenCancelStream() {
        ClientStream stream = delayedTransport.newStream(method, new Metadata(), DEFAULT);
        delayedTransport.shutdown(DelayedClientTransportTest.SHUTDOWN_STATUS);
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.same(DelayedClientTransportTest.SHUTDOWN_STATUS));
        Mockito.verify(transportListener, Mockito.times(0)).transportTerminated();
        Assert.assertEquals(1, delayedTransport.getPendingStreamsCount());
        stream.cancel(CANCELLED);
        Mockito.verify(transportListener).transportTerminated();
        Assert.assertEquals(0, delayedTransport.getPendingStreamsCount());
        Mockito.verifyNoMoreInteractions(mockRealTransport);
        Mockito.verifyNoMoreInteractions(mockRealStream);
    }

    @Test
    public void shutdownThenNewStream() {
        delayedTransport.shutdown(DelayedClientTransportTest.SHUTDOWN_STATUS);
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.same(DelayedClientTransportTest.SHUTDOWN_STATUS));
        Mockito.verify(transportListener).transportTerminated();
        ClientStream stream = delayedTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(streamListener);
        Mockito.verify(streamListener).closed(statusCaptor.capture(), ArgumentMatchers.any(RpcProgress.class), ArgumentMatchers.any(Metadata.class));
        Assert.assertEquals(Status.Code.UNAVAILABLE, statusCaptor.getValue().getCode());
    }

    @Test
    public void startStreamThenShutdownNow() {
        ClientStream stream = delayedTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(streamListener);
        delayedTransport.shutdownNow(UNAVAILABLE);
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.any(Status.class));
        Mockito.verify(transportListener).transportTerminated();
        Mockito.verify(streamListener).closed(statusCaptor.capture(), ArgumentMatchers.any(Metadata.class));
        Assert.assertEquals(Status.Code.UNAVAILABLE, statusCaptor.getValue().getCode());
    }

    @Test
    public void shutdownNowThenNewStream() {
        delayedTransport.shutdownNow(UNAVAILABLE);
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.any(Status.class));
        Mockito.verify(transportListener).transportTerminated();
        ClientStream stream = delayedTransport.newStream(method, new Metadata(), DEFAULT);
        stream.start(streamListener);
        Mockito.verify(streamListener).closed(statusCaptor.capture(), ArgumentMatchers.any(RpcProgress.class), ArgumentMatchers.any(Metadata.class));
        Assert.assertEquals(Status.Code.UNAVAILABLE, statusCaptor.getValue().getCode());
    }

    @Test
    public void reprocessSemantics() {
        CallOptions failFastCallOptions = DEFAULT.withOption(io.grpc.internal.SHARD_ID, 1);
        CallOptions waitForReadyCallOptions = DEFAULT.withOption(io.grpc.internal.SHARD_ID, 2).withWaitForReady();
        AbstractSubchannel subchannel1 = Mockito.mock(AbstractSubchannel.class);
        AbstractSubchannel subchannel2 = Mockito.mock(AbstractSubchannel.class);
        AbstractSubchannel subchannel3 = Mockito.mock(AbstractSubchannel.class);
        Mockito.when(mockRealTransport.newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class))).thenReturn(mockRealStream);
        Mockito.when(mockRealTransport2.newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class))).thenReturn(mockRealStream2);
        Mockito.when(subchannel1.obtainActiveTransport()).thenReturn(mockRealTransport);
        Mockito.when(subchannel2.obtainActiveTransport()).thenReturn(mockRealTransport2);
        Mockito.when(subchannel3.obtainActiveTransport()).thenReturn(null);
        // Fail-fast streams
        DelayedStream ff1 = ((DelayedStream) (delayedTransport.newStream(method, headers, failFastCallOptions)));
        PickSubchannelArgsImpl ff1args = new PickSubchannelArgsImpl(method, headers, failFastCallOptions);
        Mockito.verify(transportListener).transportInUse(true);
        DelayedStream ff2 = ((DelayedStream) (delayedTransport.newStream(method2, headers2, failFastCallOptions)));
        PickSubchannelArgsImpl ff2args = new PickSubchannelArgsImpl(method2, headers2, failFastCallOptions);
        DelayedStream ff3 = ((DelayedStream) (delayedTransport.newStream(method, headers, failFastCallOptions)));
        PickSubchannelArgsImpl ff3args = new PickSubchannelArgsImpl(method, headers, failFastCallOptions);
        DelayedStream ff4 = ((DelayedStream) (delayedTransport.newStream(method2, headers2, failFastCallOptions)));
        PickSubchannelArgsImpl ff4args = new PickSubchannelArgsImpl(method2, headers2, failFastCallOptions);
        // Wait-for-ready streams
        FakeClock wfr3Executor = new FakeClock();
        DelayedStream wfr1 = ((DelayedStream) (delayedTransport.newStream(method, headers, waitForReadyCallOptions)));
        PickSubchannelArgsImpl wfr1args = new PickSubchannelArgsImpl(method, headers, waitForReadyCallOptions);
        DelayedStream wfr2 = ((DelayedStream) (delayedTransport.newStream(method2, headers2, waitForReadyCallOptions)));
        PickSubchannelArgsImpl wfr2args = new PickSubchannelArgsImpl(method2, headers2, waitForReadyCallOptions);
        CallOptions wfr3callOptions = waitForReadyCallOptions.withExecutor(wfr3Executor.getScheduledExecutorService());
        DelayedStream wfr3 = ((DelayedStream) (delayedTransport.newStream(method, headers, wfr3callOptions)));
        PickSubchannelArgsImpl wfr3args = new PickSubchannelArgsImpl(method, headers, wfr3callOptions);
        DelayedStream wfr4 = ((DelayedStream) (delayedTransport.newStream(method2, headers2, waitForReadyCallOptions)));
        PickSubchannelArgsImpl wfr4args = new PickSubchannelArgsImpl(method2, headers2, waitForReadyCallOptions);
        Assert.assertEquals(8, delayedTransport.getPendingStreamsCount());
        // First reprocess(). Some will proceed, some will fail and the rest will stay buffered.
        SubchannelPicker picker = Mockito.mock(SubchannelPicker.class);
        // For the fail-fast streams
        // ff1: proceed
        // ff2: fail
        // ff3: stay
        // ff4: stay
        // For the wait-for-ready streams
        // wfr1: proceed
        // wfr2: stay
        Mockito.when(picker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel1), PickResult.withError(UNAVAILABLE), PickResult.withSubchannel(subchannel3), PickResult.withNoResult(), PickResult.withSubchannel(subchannel2), PickResult.withError(RESOURCE_EXHAUSTED), PickResult.withSubchannel(subchannel3));
        // wfr3: stay
        InOrder inOrder = Mockito.inOrder(picker);
        delayedTransport.reprocess(picker);
        Assert.assertEquals(5, delayedTransport.getPendingStreamsCount());
        inOrder.verify(picker).pickSubchannel(ff1args);
        inOrder.verify(picker).pickSubchannel(ff2args);
        inOrder.verify(picker).pickSubchannel(ff3args);
        inOrder.verify(picker).pickSubchannel(ff4args);
        inOrder.verify(picker).pickSubchannel(wfr1args);
        inOrder.verify(picker).pickSubchannel(wfr2args);
        inOrder.verify(picker).pickSubchannel(wfr3args);
        inOrder.verify(picker).pickSubchannel(wfr4args);
        inOrder.verifyNoMoreInteractions();
        // Make sure that real transport creates streams in the executor
        Mockito.verify(mockRealTransport, Mockito.never()).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        Mockito.verify(mockRealTransport2, Mockito.never()).newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class));
        fakeExecutor.runDueTasks();
        Assert.assertEquals(0, fakeExecutor.numPendingTasks());
        // ff1 and wfr1 went through
        Mockito.verify(mockRealTransport).newStream(method, headers, failFastCallOptions);
        Mockito.verify(mockRealTransport2).newStream(method, headers, waitForReadyCallOptions);
        Assert.assertSame(mockRealStream, ff1.getRealStream());
        Assert.assertSame(mockRealStream2, wfr1.getRealStream());
        // The ff2 has failed due to picker returning an error
        Assert.assertSame(UNAVAILABLE, getError());
        // Other streams are still buffered
        Assert.assertNull(ff3.getRealStream());
        Assert.assertNull(ff4.getRealStream());
        Assert.assertNull(wfr2.getRealStream());
        Assert.assertNull(wfr3.getRealStream());
        Assert.assertNull(wfr4.getRealStream());
        // Second reprocess(). All existing streams will proceed.
        picker = Mockito.mock(SubchannelPicker.class);
        // ff3
        // ff4
        // wfr2
        // wfr3
        // wfr4
        Mockito.when(picker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel1), PickResult.withSubchannel(subchannel2), PickResult.withSubchannel(subchannel2), PickResult.withSubchannel(subchannel1), PickResult.withSubchannel(subchannel2), PickResult.withNoResult());
        // wfr5 (not yet created)
        inOrder = Mockito.inOrder(picker);
        Assert.assertEquals(0, wfr3Executor.numPendingTasks());
        Mockito.verify(transportListener, Mockito.never()).transportInUse(false);
        delayedTransport.reprocess(picker);
        Assert.assertEquals(0, delayedTransport.getPendingStreamsCount());
        Mockito.verify(transportListener).transportInUse(false);
        inOrder.verify(picker).pickSubchannel(ff3args);// ff3

        inOrder.verify(picker).pickSubchannel(ff4args);// ff4

        inOrder.verify(picker).pickSubchannel(wfr2args);// wfr2

        inOrder.verify(picker).pickSubchannel(wfr3args);// wfr3

        inOrder.verify(picker).pickSubchannel(wfr4args);// wfr4

        inOrder.verifyNoMoreInteractions();
        fakeExecutor.runDueTasks();
        Assert.assertEquals(0, fakeExecutor.numPendingTasks());
        Assert.assertSame(mockRealStream, ff3.getRealStream());
        Assert.assertSame(mockRealStream2, ff4.getRealStream());
        Assert.assertSame(mockRealStream2, wfr2.getRealStream());
        Assert.assertSame(mockRealStream2, wfr4.getRealStream());
        // If there is an executor in the CallOptions, it will be used to create the real stream.
        Assert.assertNull(wfr3.getRealStream());
        wfr3Executor.runDueTasks();
        Assert.assertSame(mockRealStream, wfr3.getRealStream());
        // New streams will use the last picker
        DelayedStream wfr5 = ((DelayedStream) (delayedTransport.newStream(method, headers, waitForReadyCallOptions)));
        Assert.assertNull(wfr5.getRealStream());
        inOrder.verify(picker).pickSubchannel(new PickSubchannelArgsImpl(method, headers, waitForReadyCallOptions));
        inOrder.verifyNoMoreInteractions();
        Assert.assertEquals(1, delayedTransport.getPendingStreamsCount());
        // wfr5 will stop delayed transport from terminating
        delayedTransport.shutdown(DelayedClientTransportTest.SHUTDOWN_STATUS);
        Mockito.verify(transportListener).transportShutdown(ArgumentMatchers.same(DelayedClientTransportTest.SHUTDOWN_STATUS));
        Mockito.verify(transportListener, Mockito.never()).transportTerminated();
        // ... until it's gone
        picker = Mockito.mock(SubchannelPicker.class);
        Mockito.when(picker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel1));
        delayedTransport.reprocess(picker);
        Mockito.verify(picker).pickSubchannel(new PickSubchannelArgsImpl(method, headers, waitForReadyCallOptions));
        fakeExecutor.runDueTasks();
        Assert.assertSame(mockRealStream, wfr5.getRealStream());
        Assert.assertEquals(0, delayedTransport.getPendingStreamsCount());
        Mockito.verify(transportListener).transportTerminated();
    }

    @Test
    public void reprocess_NoPendingStream() {
        SubchannelPicker picker = Mockito.mock(SubchannelPicker.class);
        AbstractSubchannel subchannel = Mockito.mock(AbstractSubchannel.class);
        Mockito.when(subchannel.obtainActiveTransport()).thenReturn(mockRealTransport);
        Mockito.when(picker.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withSubchannel(subchannel));
        Mockito.when(mockRealTransport.newStream(ArgumentMatchers.any(MethodDescriptor.class), ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(CallOptions.class))).thenReturn(mockRealStream);
        delayedTransport.reprocess(picker);
        Mockito.verifyNoMoreInteractions(picker);
        Mockito.verifyNoMoreInteractions(transportListener);
        // Though picker was not originally used, it will be saved and serve future streams.
        ClientStream stream = delayedTransport.newStream(method, headers, DEFAULT);
        Mockito.verify(picker).pickSubchannel(new PickSubchannelArgsImpl(method, headers, CallOptions.DEFAULT));
        Mockito.verify(subchannel).obtainActiveTransport();
        Assert.assertSame(mockRealStream, stream);
    }

    @Test
    public void reprocess_newStreamRacesWithReprocess() throws Exception {
        final CyclicBarrier barrier = new CyclicBarrier(2);
        // In both phases, we only expect the first pickSubchannel() call to block on the barrier.
        final AtomicBoolean nextPickShouldWait = new AtomicBoolean(true);
        // /////// Phase 1: reprocess() twice with the same picker
        SubchannelPicker picker = Mockito.mock(SubchannelPicker.class);
        Mockito.doAnswer(new Answer<PickResult>() {
            @Override
            @SuppressWarnings("CatchAndPrintStackTrace")
            public PickResult answer(InvocationOnMock invocation) throws Throwable {
                if (nextPickShouldWait.compareAndSet(true, false)) {
                    try {
                        barrier.await();
                        return PickResult.withNoResult();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return PickResult.withNoResult();
            }
        }).when(picker).pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class));
        // Because there is no pending stream yet, it will do nothing but save the picker.
        delayedTransport.reprocess(picker);
        Mockito.verify(picker, Mockito.never()).pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class));
        Thread sideThread = new Thread("sideThread") {
            @Override
            public void run() {
                // Will call pickSubchannel and wait on barrier
                delayedTransport.newStream(method, headers, callOptions);
            }
        };
        sideThread.start();
        PickSubchannelArgsImpl args = new PickSubchannelArgsImpl(method, headers, callOptions);
        PickSubchannelArgsImpl args2 = new PickSubchannelArgsImpl(method, headers2, callOptions);
        // Is called from sideThread
        Mockito.verify(picker, Mockito.timeout(5000)).pickSubchannel(args);
        // Because stream has not been buffered (it's still stuck in newStream()), this will do nothing,
        // but incrementing the picker version.
        delayedTransport.reprocess(picker);
        Mockito.verify(picker).pickSubchannel(args);
        // Now let the stuck newStream() through
        barrier.await(5, TimeUnit.SECONDS);
        sideThread.join(5000);
        Assert.assertFalse("sideThread should've exited", sideThread.isAlive());
        // newStream() detects that there has been a new picker while it's stuck, thus will pick again.
        Mockito.verify(picker, Mockito.times(2)).pickSubchannel(args);
        barrier.reset();
        nextPickShouldWait.set(true);
        // //////// Phase 2: reprocess() with a different picker
        // Create the second stream
        Thread sideThread2 = new Thread("sideThread2") {
            @Override
            public void run() {
                // Will call pickSubchannel and wait on barrier
                delayedTransport.newStream(method, headers2, callOptions);
            }
        };
        sideThread2.start();
        // The second stream will see the first picker
        Mockito.verify(picker, Mockito.timeout(5000)).pickSubchannel(args2);
        // While the first stream won't use the first picker any more.
        Mockito.verify(picker, Mockito.times(2)).pickSubchannel(args);
        // Now use a different picker
        SubchannelPicker picker2 = Mockito.mock(SubchannelPicker.class);
        Mockito.when(picker2.pickSubchannel(ArgumentMatchers.any(PickSubchannelArgs.class))).thenReturn(PickResult.withNoResult());
        delayedTransport.reprocess(picker2);
        // The pending first stream uses the new picker
        Mockito.verify(picker2).pickSubchannel(args);
        // The second stream is still pending in creation, doesn't use the new picker.
        Mockito.verify(picker2, Mockito.never()).pickSubchannel(args2);
        // Now let the second stream finish creation
        barrier.await(5, TimeUnit.SECONDS);
        sideThread2.join(5000);
        Assert.assertFalse("sideThread2 should've exited", sideThread2.isAlive());
        // The second stream should see the new picker
        Mockito.verify(picker2, Mockito.timeout(5000)).pickSubchannel(args2);
        // Wrapping up
        Mockito.verify(picker, Mockito.times(2)).pickSubchannel(args);
        Mockito.verify(picker).pickSubchannel(args2);
        Mockito.verify(picker2).pickSubchannel(args);
        Mockito.verify(picker2).pickSubchannel(args);
    }
}

