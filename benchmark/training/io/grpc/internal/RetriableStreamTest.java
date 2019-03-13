/**
 * Copyright 2017 The gRPC Authors
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


import ClientStreamTracer.Factory;
import ClientStreamTracer.StreamInfo;
import Codec.Identity;
import MethodType.BIDI_STREAMING;
import RetriableStream.GRPC_RETRY_PUSHBACK_MS;
import Status.CANCELLED;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.ClientStreamTracer;
import io.grpc.Compressor;
import io.grpc.DecompressorRegistry;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StringMarshaller;
import io.grpc.internal.RetriableStream.ChannelBufferMeter;
import io.grpc.internal.RetriableStream.Throttle;
import io.grpc.internal.StreamListener.MessageProducer;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static RetryPolicy.DEFAULT;


/**
 * Unit tests for {@link RetriableStream}.
 */
@RunWith(JUnit4.class)
public class RetriableStreamTest {
    private static final String CANCELLED_BECAUSE_COMMITTED = "Stream thrown away because RetriableStream committed";

    private static final String AUTHORITY = "fakeAuthority";

    private static final Compressor COMPRESSOR = Identity.NONE;

    private static final DecompressorRegistry DECOMPRESSOR_REGISTRY = DecompressorRegistry.getDefaultInstance();

    private static final int MAX_INBOUND_MESSAGE_SIZE = 1234;

    private static final int MAX_OUTBOUND_MESSAGE_SIZE = 5678;

    private static final long PER_RPC_BUFFER_LIMIT = 1000;

    private static final long CHANNEL_BUFFER_LIMIT = 2000;

    private static final int MAX_ATTEMPTS = 6;

    private static final long INITIAL_BACKOFF_IN_SECONDS = 100;

    private static final long HEDGING_DELAY_IN_SECONDS = 100;

    private static final long MAX_BACKOFF_IN_SECONDS = 700;

    private static final double BACKOFF_MULTIPLIER = 2.0;

    private static final double FAKE_RANDOM = 0.5;

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

    static {
        // not random
        RetriableStream.setRandom(new Random() {
            @Override
            public double nextDouble() {
                return RetriableStreamTest.FAKE_RANDOM;
            }
        });
    }

    private static final Code RETRIABLE_STATUS_CODE_1 = Code.UNAVAILABLE;

    private static final Code RETRIABLE_STATUS_CODE_2 = Code.DATA_LOSS;

    private static final Code NON_RETRIABLE_STATUS_CODE = Code.INTERNAL;

    private static final Code NON_FATAL_STATUS_CODE_1 = Code.UNAVAILABLE;

    private static final Code NON_FATAL_STATUS_CODE_2 = Code.DATA_LOSS;

    private static final Code FATAL_STATUS_CODE = Code.INTERNAL;

    private static final RetryPolicy RETRY_POLICY = new RetryPolicy(RetriableStreamTest.MAX_ATTEMPTS, TimeUnit.SECONDS.toNanos(RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS), TimeUnit.SECONDS.toNanos(RetriableStreamTest.MAX_BACKOFF_IN_SECONDS), RetriableStreamTest.BACKOFF_MULTIPLIER, ImmutableSet.of(RetriableStreamTest.RETRIABLE_STATUS_CODE_1, RetriableStreamTest.RETRIABLE_STATUS_CODE_2));

    private static final HedgingPolicy HEDGING_POLICY = new HedgingPolicy(RetriableStreamTest.MAX_ATTEMPTS, TimeUnit.SECONDS.toNanos(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS), ImmutableSet.of(RetriableStreamTest.NON_FATAL_STATUS_CODE_1, RetriableStreamTest.NON_FATAL_STATUS_CODE_2));

    private final RetriableStreamTest.RetriableStreamRecorder retriableStreamRecorder = Mockito.mock(RetriableStreamTest.RetriableStreamRecorder.class);

    private final ClientStreamListener masterListener = Mockito.mock(ClientStreamListener.class);

    private final MethodDescriptor<String, String> method = MethodDescriptor.<String, String>newBuilder().setType(BIDI_STREAMING).setFullMethodName(MethodDescriptor.generateFullMethodName("service_foo", "method_bar")).setRequestMarshaller(new StringMarshaller()).setResponseMarshaller(new StringMarshaller()).build();

    private final ChannelBufferMeter channelBufferUsed = new ChannelBufferMeter();

    private final FakeClock fakeClock = new FakeClock();

    private final class RecordedRetriableStream extends RetriableStream<String> {
        RecordedRetriableStream(MethodDescriptor<String, ?> method, Metadata headers, ChannelBufferMeter channelBufferUsed, long perRpcBufferLimit, long channelBufferLimit, Executor callExecutor, ScheduledExecutorService scheduledExecutorService, final RetryPolicy retryPolicy, final HedgingPolicy hedgingPolicy, @Nullable
        Throttle throttle) {
            super(method, headers, channelBufferUsed, perRpcBufferLimit, channelBufferLimit, callExecutor, scheduledExecutorService, new RetryPolicy.Provider() {
                @Override
                public RetryPolicy get() {
                    return retryPolicy;
                }
            }, new HedgingPolicy.Provider() {
                @Override
                public HedgingPolicy get() {
                    return hedgingPolicy;
                }
            }, throttle);
        }

        @Override
        void postCommit() {
            retriableStreamRecorder.postCommit();
        }

        @Override
        ClientStream newSubstream(ClientStreamTracer.Factory tracerFactory, Metadata metadata) {
            bufferSizeTracer = tracerFactory.newClientStreamTracer(RetriableStreamTest.STREAM_INFO, new Metadata());
            int actualPreviousRpcAttemptsInHeader = ((metadata.get(RetriableStream.GRPC_PREVIOUS_RPC_ATTEMPTS)) == null) ? 0 : Integer.valueOf(metadata.get(RetriableStream.GRPC_PREVIOUS_RPC_ATTEMPTS));
            return retriableStreamRecorder.newSubstream(actualPreviousRpcAttemptsInHeader);
        }

        @Override
        Status prestart() {
            return retriableStreamRecorder.prestart();
        }
    }

    private final RetriableStream<String> retriableStream = /* throttle */
    newThrottledRetriableStream(null);

    private final RetriableStream<String> hedgingStream = /* throttle */
    newThrottledHedgingStream(null);

    private ClientStreamTracer bufferSizeTracer;

    @Test
    public void retry_everythingDrained() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder, masterListener, mockStream1, mockStream2, mockStream3);
        // stream settings before start
        retriableStream.setAuthority(RetriableStreamTest.AUTHORITY);
        retriableStream.setCompressor(RetriableStreamTest.COMPRESSOR);
        retriableStream.setDecompressorRegistry(RetriableStreamTest.DECOMPRESSOR_REGISTRY);
        retriableStream.setFullStreamDecompression(false);
        retriableStream.setFullStreamDecompression(true);
        retriableStream.setMaxInboundMessageSize(RetriableStreamTest.MAX_INBOUND_MESSAGE_SIZE);
        retriableStream.setMessageCompression(true);
        retriableStream.setMaxOutboundMessageSize(RetriableStreamTest.MAX_OUTBOUND_MESSAGE_SIZE);
        retriableStream.setMessageCompression(false);
        inOrder.verifyNoMoreInteractions();
        // start
        retriableStream.start(masterListener);
        inOrder.verify(retriableStreamRecorder).prestart();
        inOrder.verify(retriableStreamRecorder).newSubstream(0);
        inOrder.verify(mockStream1).setAuthority(RetriableStreamTest.AUTHORITY);
        inOrder.verify(mockStream1).setCompressor(RetriableStreamTest.COMPRESSOR);
        inOrder.verify(mockStream1).setDecompressorRegistry(RetriableStreamTest.DECOMPRESSOR_REGISTRY);
        inOrder.verify(mockStream1).setFullStreamDecompression(false);
        inOrder.verify(mockStream1).setFullStreamDecompression(true);
        inOrder.verify(mockStream1).setMaxInboundMessageSize(RetriableStreamTest.MAX_INBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream1).setMessageCompression(true);
        inOrder.verify(mockStream1).setMaxOutboundMessageSize(RetriableStreamTest.MAX_OUTBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream1).setMessageCompression(false);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        retriableStream.sendMessage("msg1");
        retriableStream.sendMessage("msg2");
        retriableStream.request(345);
        retriableStream.flush();
        retriableStream.flush();
        retriableStream.sendMessage("msg3");
        retriableStream.request(456);
        inOrder.verify(mockStream1, Mockito.times(2)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream1).request(345);
        inOrder.verify(mockStream1, Mockito.times(2)).flush();
        inOrder.verify(mockStream1).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream1).request(456);
        inOrder.verifyNoMoreInteractions();
        // retry1
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_2), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        // send more messages during backoff
        retriableStream.sendMessage("msg1 during backoff1");
        retriableStream.sendMessage("msg2 during backoff1");
        fakeClock.forwardTime((((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))) - 1L), TimeUnit.SECONDS);
        inOrder.verifyNoMoreInteractions();
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        inOrder.verify(retriableStreamRecorder).newSubstream(1);
        inOrder.verify(mockStream2).setAuthority(RetriableStreamTest.AUTHORITY);
        inOrder.verify(mockStream2).setCompressor(RetriableStreamTest.COMPRESSOR);
        inOrder.verify(mockStream2).setDecompressorRegistry(RetriableStreamTest.DECOMPRESSOR_REGISTRY);
        inOrder.verify(mockStream2).setFullStreamDecompression(false);
        inOrder.verify(mockStream2).setFullStreamDecompression(true);
        inOrder.verify(mockStream2).setMaxInboundMessageSize(RetriableStreamTest.MAX_INBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream2).setMessageCompression(true);
        inOrder.verify(mockStream2).setMaxOutboundMessageSize(RetriableStreamTest.MAX_OUTBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream2).setMessageCompression(false);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verify(mockStream2, Mockito.times(2)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream2).request(345);
        inOrder.verify(mockStream2, Mockito.times(2)).flush();
        inOrder.verify(mockStream2).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream2).request(456);
        inOrder.verify(mockStream2, Mockito.times(2)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verifyNoMoreInteractions();
        // send more messages
        retriableStream.sendMessage("msg1 after retry1");
        retriableStream.sendMessage("msg2 after retry1");
        // mockStream1 is closed so it is not in the drainedSubstreams
        Mockito.verifyNoMoreInteractions(mockStream1);
        inOrder.verify(mockStream2, Mockito.times(2)).writeMessage(ArgumentMatchers.any(InputStream.class));
        // retry2
        Mockito.doReturn(mockStream3).when(retriableStreamRecorder).newSubstream(2);
        sublistenerCaptor2.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        // send more messages during backoff
        retriableStream.sendMessage("msg1 during backoff2");
        retriableStream.sendMessage("msg2 during backoff2");
        retriableStream.sendMessage("msg3 during backoff2");
        fakeClock.forwardTime((((long) (((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.BACKOFF_MULTIPLIER)) * (RetriableStreamTest.FAKE_RANDOM))) - 1L), TimeUnit.SECONDS);
        inOrder.verifyNoMoreInteractions();
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        inOrder.verify(retriableStreamRecorder).newSubstream(2);
        inOrder.verify(mockStream3).setAuthority(RetriableStreamTest.AUTHORITY);
        inOrder.verify(mockStream3).setCompressor(RetriableStreamTest.COMPRESSOR);
        inOrder.verify(mockStream3).setDecompressorRegistry(RetriableStreamTest.DECOMPRESSOR_REGISTRY);
        inOrder.verify(mockStream3).setFullStreamDecompression(false);
        inOrder.verify(mockStream3).setFullStreamDecompression(true);
        inOrder.verify(mockStream3).setMaxInboundMessageSize(RetriableStreamTest.MAX_INBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream3).setMessageCompression(true);
        inOrder.verify(mockStream3).setMaxOutboundMessageSize(RetriableStreamTest.MAX_OUTBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream3).setMessageCompression(false);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream3).start(sublistenerCaptor3.capture());
        inOrder.verify(mockStream3, Mockito.times(2)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream3).request(345);
        inOrder.verify(mockStream3, Mockito.times(2)).flush();
        inOrder.verify(mockStream3).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream3).request(456);
        inOrder.verify(mockStream3, Mockito.times(7)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verifyNoMoreInteractions();
        // no more retry
        sublistenerCaptor3.getValue().closed(Status.fromCode(RetriableStreamTest.NON_RETRIABLE_STATUS_CODE), new Metadata());
        inOrder.verify(retriableStreamRecorder).postCommit();
        inOrder.verify(masterListener).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void headersRead_cancel() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder);
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        sublistenerCaptor1.getValue().headersRead(new Metadata());
        inOrder.verify(retriableStreamRecorder).postCommit();
        retriableStream.cancel(CANCELLED);
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
    }

    @Test
    public void retry_headersRead_cancel() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder);
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        // retry
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        // headersRead
        sublistenerCaptor2.getValue().headersRead(new Metadata());
        inOrder.verify(retriableStreamRecorder).postCommit();
        // cancel
        retriableStream.cancel(CANCELLED);
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
    }

    @Test
    public void headersRead_closed() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder);
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        sublistenerCaptor1.getValue().headersRead(new Metadata());
        inOrder.verify(retriableStreamRecorder).postCommit();
        Status status = Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1);
        Metadata metadata = new Metadata();
        sublistenerCaptor1.getValue().closed(status, metadata);
        Mockito.verify(masterListener).closed(status, metadata);
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
    }

    @Test
    public void retry_headersRead_closed() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder);
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        // retry
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        fakeClock.forwardTime(((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        // headersRead
        sublistenerCaptor2.getValue().headersRead(new Metadata());
        inOrder.verify(retriableStreamRecorder).postCommit();
        // closed even with retriable status
        Status status = Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1);
        Metadata metadata = new Metadata();
        sublistenerCaptor2.getValue().closed(status, metadata);
        Mockito.verify(masterListener).closed(status, metadata);
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
    }

    @Test
    public void cancel_closed() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder);
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        // cancel
        retriableStream.cancel(CANCELLED);
        inOrder.verify(retriableStreamRecorder).postCommit();
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockStream1).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        // closed even with retriable status
        Status status = Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1);
        Metadata metadata = new Metadata();
        sublistenerCaptor1.getValue().closed(status, metadata);
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
    }

    @Test
    public void retry_cancel_closed() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder);
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        // retry
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        fakeClock.forwardTime(((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        // cancel
        retriableStream.cancel(CANCELLED);
        inOrder.verify(retriableStreamRecorder).postCommit();
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockStream2).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        // closed
        Status status = Status.fromCode(RetriableStreamTest.NON_RETRIABLE_STATUS_CODE);
        Metadata metadata = new Metadata();
        sublistenerCaptor2.getValue().closed(status, metadata);
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
    }

    @Test
    public void unretriableClosed_cancel() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder);
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        // closed
        Status status = Status.fromCode(RetriableStreamTest.NON_RETRIABLE_STATUS_CODE);
        Metadata metadata = new Metadata();
        sublistenerCaptor1.getValue().closed(status, metadata);
        inOrder.verify(retriableStreamRecorder).postCommit();
        Mockito.verify(masterListener).closed(status, metadata);
        // cancel
        retriableStream.cancel(CANCELLED);
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
    }

    @Test
    public void retry_unretriableClosed_cancel() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder);
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        // retry
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        fakeClock.forwardTime(((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        // closed
        Status status = Status.fromCode(RetriableStreamTest.NON_RETRIABLE_STATUS_CODE);
        Metadata metadata = new Metadata();
        sublistenerCaptor2.getValue().closed(status, metadata);
        inOrder.verify(retriableStreamRecorder).postCommit();
        Mockito.verify(masterListener).closed(status, metadata);
        // cancel
        retriableStream.cancel(CANCELLED);
        inOrder.verify(retriableStreamRecorder, Mockito.never()).postCommit();
    }

    @Test
    public void retry_cancelWhileBackoff() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        // retry
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        // cancel while backoff
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        retriableStream.cancel(CANCELLED);
        Mockito.verify(retriableStreamRecorder).postCommit();
        Mockito.verifyNoMoreInteractions(mockStream1);
        Mockito.verifyNoMoreInteractions(mockStream2);
    }

    @Test
    public void operationsWhileDraining() {
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        final AtomicReference<ClientStreamListener> sublistenerCaptor2 = new AtomicReference<>();
        final Status cancelStatus = CANCELLED.withDescription("c");
        ClientStream mockStream1 = Mockito.mock(ClientStream.class, AdditionalAnswers.delegatesTo(new NoopClientStream() {
            @Override
            public void request(int numMessages) {
                retriableStream.sendMessage(("substream1 request " + numMessages));
                if (numMessages > 1) {
                    retriableStream.request((--numMessages));
                }
            }
        }));
        final ClientStream mockStream2 = Mockito.mock(ClientStream.class, AdditionalAnswers.delegatesTo(new NoopClientStream() {
            @Override
            public void start(ClientStreamListener listener) {
                sublistenerCaptor2.set(listener);
            }

            @Override
            public void request(int numMessages) {
                retriableStream.sendMessage(("substream2 request " + numMessages));
                if (numMessages == 3) {
                    sublistenerCaptor2.get().headersRead(new Metadata());
                }
                if (numMessages == 2) {
                    retriableStream.request(100);
                }
                if (numMessages == 100) {
                    retriableStream.cancel(cancelStatus);
                }
            }
        }));
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder, mockStream1, mockStream2);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        retriableStream.request(3);
        inOrder.verify(mockStream1).request(3);
        inOrder.verify(mockStream1).writeMessage(ArgumentMatchers.any(InputStream.class));// msg "substream1 request 3"

        inOrder.verify(mockStream1).request(2);
        inOrder.verify(mockStream1).writeMessage(ArgumentMatchers.any(InputStream.class));// msg "substream1 request 2"

        inOrder.verify(mockStream1).request(1);
        inOrder.verify(mockStream1).writeMessage(ArgumentMatchers.any(InputStream.class));// msg "substream1 request 1"

        // retry
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        // send more requests during backoff
        retriableStream.request(789);
        fakeClock.forwardTime(((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.get());
        inOrder.verify(mockStream2).request(3);
        inOrder.verify(retriableStreamRecorder).postCommit();
        inOrder.verify(mockStream2).writeMessage(ArgumentMatchers.any(InputStream.class));// msg "substream1 request 3"

        inOrder.verify(mockStream2).request(2);
        inOrder.verify(mockStream2).writeMessage(ArgumentMatchers.any(InputStream.class));// msg "substream1 request 2"

        inOrder.verify(mockStream2).request(1);
        // msg "substream1 request 1"
        inOrder.verify(mockStream2).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream2).request(789);
        // msg "substream2 request 3"
        // msg "substream2 request 2"
        inOrder.verify(mockStream2, Mockito.times(2)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream2).request(100);
        Mockito.verify(mockStream2).cancel(cancelStatus);
        // "substream2 request 1" will never be sent
        inOrder.verify(mockStream2, Mockito.never()).writeMessage(ArgumentMatchers.any(InputStream.class));
    }

    @Test
    public void operationsAfterImmediateCommit() {
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder, mockStream1);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        // drained
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        // commit
        sublistenerCaptor1.getValue().headersRead(new Metadata());
        retriableStream.request(3);
        inOrder.verify(mockStream1).request(3);
        retriableStream.sendMessage("msg 1");
        inOrder.verify(mockStream1).writeMessage(ArgumentMatchers.any(InputStream.class));
    }

    @Test
    public void isReady_whenDrained() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        Assert.assertFalse(retriableStream.isReady());
        Mockito.doReturn(true).when(mockStream1).isReady();
        Assert.assertTrue(retriableStream.isReady());
    }

    @Test
    public void isReady_whileDraining() {
        final AtomicReference<ClientStreamListener> sublistenerCaptor1 = new AtomicReference<>();
        final List<Boolean> readiness = new ArrayList<>();
        ClientStream mockStream1 = Mockito.mock(ClientStream.class, AdditionalAnswers.delegatesTo(new NoopClientStream() {
            @Override
            public void start(ClientStreamListener listener) {
                sublistenerCaptor1.set(listener);
                readiness.add(retriableStream.isReady());// expected false b/c in draining

            }

            @Override
            public boolean isReady() {
                return true;
            }
        }));
        final ClientStream mockStream2 = Mockito.mock(ClientStream.class, AdditionalAnswers.delegatesTo(new NoopClientStream() {
            @Override
            public void start(ClientStreamListener listener) {
                readiness.add(retriableStream.isReady());// expected false b/c in draining

            }

            @Override
            public boolean isReady() {
                return true;
            }
        }));
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.get());
        readiness.add(retriableStream.isReady());// expected true

        // retry
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        Mockito.doReturn(false).when(mockStream1).isReady();// mockStream1 closed, so isReady false

        sublistenerCaptor1.get().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        // send more requests during backoff
        retriableStream.request(789);
        readiness.add(retriableStream.isReady());// expected false b/c in backoff

        fakeClock.forwardTime(((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        Mockito.verify(mockStream2).start(ArgumentMatchers.any(ClientStreamListener.class));
        readiness.add(retriableStream.isReady());// expected true

        assertThat(readiness).containsExactly(false, true, false, false, true).inOrder();
    }

    @Test
    public void messageAvailable() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        ClientStreamListener listener = sublistenerCaptor1.getValue();
        listener.headersRead(new Metadata());
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        listener.messagesAvailable(messageProducer);
        Mockito.verify(masterListener).messagesAvailable(messageProducer);
    }

    @Test
    public void closedWhileDraining() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        final ClientStream mockStream2 = Mockito.mock(ClientStream.class, AdditionalAnswers.delegatesTo(new NoopClientStream() {
            @Override
            public void start(ClientStreamListener listener) {
                // closed while draning
                listener.closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
            }
        }));
        final ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        Mockito.when(retriableStreamRecorder.newSubstream(ArgumentMatchers.anyInt())).thenReturn(mockStream1, mockStream2, mockStream3);
        retriableStream.start(masterListener);
        retriableStream.sendMessage("msg1");
        retriableStream.sendMessage("msg2");
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        ClientStreamListener listener1 = sublistenerCaptor1.getValue();
        // retry
        listener1.closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        // send requests during backoff
        retriableStream.request(3);
        fakeClock.forwardTime(((long) (((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.BACKOFF_MULTIPLIER)) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        retriableStream.request(1);
        Mockito.verify(mockStream1, Mockito.never()).request(ArgumentMatchers.anyInt());
        Mockito.verify(mockStream2, Mockito.never()).request(ArgumentMatchers.anyInt());
        Mockito.verify(mockStream3).request(3);
        Mockito.verify(mockStream3).request(1);
    }

    @Test
    public void perRpcBufferLimitExceeded() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        bufferSizeTracer.outboundWireSize(RetriableStreamTest.PER_RPC_BUFFER_LIMIT);
        Assert.assertEquals(RetriableStreamTest.PER_RPC_BUFFER_LIMIT, channelBufferUsed.addAndGet(0));
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        bufferSizeTracer.outboundWireSize(2);
        Mockito.verify(retriableStreamRecorder).postCommit();
        // verify channel buffer is adjusted
        Assert.assertEquals(0, channelBufferUsed.addAndGet(0));
    }

    @Test
    public void perRpcBufferLimitExceededDuringBackoff() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        bufferSizeTracer.outboundWireSize(((RetriableStreamTest.PER_RPC_BUFFER_LIMIT) - 1));
        // retry
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        // bufferSizeTracer.outboundWireSize() quits immediately while backoff b/c substream1 is closed
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        bufferSizeTracer.outboundWireSize(2);
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        fakeClock.forwardTime(((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        Mockito.verify(mockStream2).start(ArgumentMatchers.any(ClientStreamListener.class));
        // bufferLimitExceeded
        bufferSizeTracer.outboundWireSize(((RetriableStreamTest.PER_RPC_BUFFER_LIMIT) - 1));
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        bufferSizeTracer.outboundWireSize(2);
        Mockito.verify(retriableStreamRecorder).postCommit();
        Mockito.verifyNoMoreInteractions(mockStream1);
        Mockito.verifyNoMoreInteractions(mockStream2);
    }

    @Test
    public void channelBufferLimitExceeded() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        bufferSizeTracer.outboundWireSize(100);
        Assert.assertEquals(100, channelBufferUsed.addAndGet(0));
        channelBufferUsed.addAndGet(((RetriableStreamTest.CHANNEL_BUFFER_LIMIT) - 200));
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        bufferSizeTracer.outboundWireSize((100 + 1));
        Mockito.verify(retriableStreamRecorder).postCommit();
        // verify channel buffer is adjusted
        Assert.assertEquals(((RetriableStreamTest.CHANNEL_BUFFER_LIMIT) - 200), channelBufferUsed.addAndGet(0));
    }

    @Test
    public void updateHeaders() {
        Metadata originalHeaders = new Metadata();
        Metadata headers = retriableStream.updateHeaders(originalHeaders, 0);
        Assert.assertNotSame(originalHeaders, headers);
        Assert.assertNull(headers.get(RetriableStream.GRPC_PREVIOUS_RPC_ATTEMPTS));
        headers = retriableStream.updateHeaders(originalHeaders, 345);
        Assert.assertEquals("345", headers.get(RetriableStream.GRPC_PREVIOUS_RPC_ATTEMPTS));
        Assert.assertNull(originalHeaders.get(RetriableStream.GRPC_PREVIOUS_RPC_ATTEMPTS));
    }

    @Test
    public void expBackoff_maxBackoff_maxRetryAttempts() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        ClientStream mockStream4 = Mockito.mock(ClientStream.class);
        ClientStream mockStream5 = Mockito.mock(ClientStream.class);
        ClientStream mockStream6 = Mockito.mock(ClientStream.class);
        ClientStream mockStream7 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(mockStream1, mockStream2, mockStream3, mockStream4, mockStream5, mockStream6, mockStream7);
        Mockito.when(retriableStreamRecorder.newSubstream(ArgumentMatchers.anyInt())).thenReturn(mockStream1, mockStream2, mockStream3, mockStream4, mockStream5, mockStream6, mockStream7);
        retriableStream.start(masterListener);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(0);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        // retry1
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime((((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))) - 1L), TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(1);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verifyNoMoreInteractions();
        // retry2
        sublistenerCaptor2.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_2), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime((((long) (((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.BACKOFF_MULTIPLIER)) * (RetriableStreamTest.FAKE_RANDOM))) - 1L), TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(2);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream3).start(sublistenerCaptor3.capture());
        inOrder.verifyNoMoreInteractions();
        // retry3
        sublistenerCaptor3.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime((((long) ((((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.BACKOFF_MULTIPLIER)) * (RetriableStreamTest.BACKOFF_MULTIPLIER)) * (RetriableStreamTest.FAKE_RANDOM))) - 1L), TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(3);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor4 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream4).start(sublistenerCaptor4.capture());
        inOrder.verifyNoMoreInteractions();
        // retry4
        sublistenerCaptor4.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_2), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime((((long) ((RetriableStreamTest.MAX_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))) - 1L), TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(4);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor5 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream5).start(sublistenerCaptor5.capture());
        inOrder.verifyNoMoreInteractions();
        // retry5
        sublistenerCaptor5.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_2), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime((((long) ((RetriableStreamTest.MAX_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))) - 1L), TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(5);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor6 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream6).start(sublistenerCaptor6.capture());
        inOrder.verifyNoMoreInteractions();
        // can not retry any more
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        sublistenerCaptor6.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        Mockito.verify(retriableStreamRecorder).postCommit();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void pushback() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        ClientStream mockStream4 = Mockito.mock(ClientStream.class);
        ClientStream mockStream5 = Mockito.mock(ClientStream.class);
        ClientStream mockStream6 = Mockito.mock(ClientStream.class);
        ClientStream mockStream7 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(mockStream1, mockStream2, mockStream3, mockStream4, mockStream5, mockStream6, mockStream7);
        Mockito.when(retriableStreamRecorder.newSubstream(ArgumentMatchers.anyInt())).thenReturn(mockStream1, mockStream2, mockStream3, mockStream4, mockStream5, mockStream6, mockStream7);
        retriableStream.start(masterListener);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(0);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        // retry1
        int pushbackInMillis = 123;
        Metadata headers = new Metadata();
        headers.put(GRPC_RETRY_PUSHBACK_MS, ("" + pushbackInMillis));
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), headers);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime((pushbackInMillis - 1), TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.MILLISECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(1);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verifyNoMoreInteractions();
        // retry2
        pushbackInMillis = 4567 * 1000;
        headers = new Metadata();
        headers.put(GRPC_RETRY_PUSHBACK_MS, ("" + pushbackInMillis));
        sublistenerCaptor2.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_2), headers);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime((pushbackInMillis - 1), TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.MILLISECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(2);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream3).start(sublistenerCaptor3.capture());
        inOrder.verifyNoMoreInteractions();
        // retry3
        sublistenerCaptor3.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime((((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))) - 1L), TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(3);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor4 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream4).start(sublistenerCaptor4.capture());
        inOrder.verifyNoMoreInteractions();
        // retry4
        sublistenerCaptor4.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_2), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime((((long) (((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.BACKOFF_MULTIPLIER)) * (RetriableStreamTest.FAKE_RANDOM))) - 1L), TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(4);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor5 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream5).start(sublistenerCaptor5.capture());
        inOrder.verifyNoMoreInteractions();
        // retry5
        sublistenerCaptor5.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_2), new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime((((long) ((((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.BACKOFF_MULTIPLIER)) * (RetriableStreamTest.BACKOFF_MULTIPLIER)) * (RetriableStreamTest.FAKE_RANDOM))) - 1L), TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(1L, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(5);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor6 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream6).start(sublistenerCaptor6.capture());
        inOrder.verifyNoMoreInteractions();
        // can not retry any more even pushback is positive
        pushbackInMillis = 4567 * 1000;
        headers = new Metadata();
        headers.put(GRPC_RETRY_PUSHBACK_MS, ("" + pushbackInMillis));
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        sublistenerCaptor6.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), headers);
        Mockito.verify(retriableStreamRecorder).postCommit();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void pushback_noRetry() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(ArgumentMatchers.anyInt());
        retriableStream.start(masterListener);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        Mockito.verify(retriableStreamRecorder).newSubstream(0);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        // pushback no retry
        Metadata headers = new Metadata();
        headers.put(GRPC_RETRY_PUSHBACK_MS, "");
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), headers);
        Mockito.verify(retriableStreamRecorder, Mockito.never()).newSubstream(1);
        Mockito.verify(retriableStreamRecorder).postCommit();
    }

    @Test
    public void throttle() {
        Throttle throttle = new Throttle(4.0F, 0.8F);
        Assert.assertTrue(throttle.isAboveThreshold());
        Assert.assertTrue(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// token = 3

        Assert.assertTrue(throttle.isAboveThreshold());
        Assert.assertFalse(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// token = 2

        Assert.assertFalse(throttle.isAboveThreshold());
        Assert.assertFalse(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// token = 1

        Assert.assertFalse(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// token = 0

        Assert.assertFalse(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// token = 0

        Assert.assertFalse(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// token = 0

        Assert.assertFalse(throttle.isAboveThreshold());
        throttle.onSuccess();// token = 0.8

        Assert.assertFalse(throttle.isAboveThreshold());
        throttle.onSuccess();// token = 1.6

        Assert.assertFalse(throttle.isAboveThreshold());
        throttle.onSuccess();// token = 3.2

        Assert.assertTrue(throttle.isAboveThreshold());
        throttle.onSuccess();// token = 4

        Assert.assertTrue(throttle.isAboveThreshold());
        throttle.onSuccess();// token = 4

        Assert.assertTrue(throttle.isAboveThreshold());
        throttle.onSuccess();// token = 4

        Assert.assertTrue(throttle.isAboveThreshold());
        Assert.assertTrue(throttle.isAboveThreshold());
        Assert.assertTrue(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// token = 3

        Assert.assertTrue(throttle.isAboveThreshold());
        Assert.assertFalse(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// token = 2

        Assert.assertFalse(throttle.isAboveThreshold());
    }

    @Test
    public void throttledStream_FailWithRetriableStatusCode_WithoutPushback() {
        Throttle throttle = new Throttle(4.0F, 0.8F);
        RetriableStream<String> retriableStream = newThrottledRetriableStream(throttle);
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream).when(retriableStreamRecorder).newSubstream(ArgumentMatchers.anyInt());
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream).start(sublistenerCaptor.capture());
        // mimic some other call in the channel triggers a throttle countdown
        Assert.assertTrue(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 3

        sublistenerCaptor.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), new Metadata());
        Mockito.verify(retriableStreamRecorder).postCommit();
        Assert.assertFalse(throttle.isAboveThreshold());// count = 2

    }

    @Test
    public void throttledStream_FailWithNonRetriableStatusCode_WithoutPushback() {
        Throttle throttle = new Throttle(4.0F, 0.8F);
        RetriableStream<String> retriableStream = newThrottledRetriableStream(throttle);
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream).when(retriableStreamRecorder).newSubstream(ArgumentMatchers.anyInt());
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream).start(sublistenerCaptor.capture());
        // mimic some other call in the channel triggers a throttle countdown
        Assert.assertTrue(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 3

        sublistenerCaptor.getValue().closed(Status.fromCode(RetriableStreamTest.NON_RETRIABLE_STATUS_CODE), new Metadata());
        Mockito.verify(retriableStreamRecorder).postCommit();
        Assert.assertTrue(throttle.isAboveThreshold());// count = 3

        Assert.assertFalse(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 2

    }

    @Test
    public void throttledStream_FailWithRetriableStatusCode_WithRetriablePushback() {
        Throttle throttle = new Throttle(4.0F, 0.8F);
        RetriableStream<String> retriableStream = newThrottledRetriableStream(throttle);
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream).when(retriableStreamRecorder).newSubstream(ArgumentMatchers.anyInt());
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream).start(sublistenerCaptor.capture());
        // mimic some other call in the channel triggers a throttle countdown
        Assert.assertTrue(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 3

        int pushbackInMillis = 123;
        Metadata headers = new Metadata();
        headers.put(GRPC_RETRY_PUSHBACK_MS, ("" + pushbackInMillis));
        sublistenerCaptor.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), headers);
        Mockito.verify(retriableStreamRecorder).postCommit();
        Assert.assertFalse(throttle.isAboveThreshold());// count = 2

    }

    @Test
    public void throttledStream_FailWithNonRetriableStatusCode_WithRetriablePushback() {
        Throttle throttle = new Throttle(4.0F, 0.8F);
        RetriableStream<String> retriableStream = newThrottledRetriableStream(throttle);
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream).when(retriableStreamRecorder).newSubstream(ArgumentMatchers.anyInt());
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream).start(sublistenerCaptor.capture());
        // mimic some other call in the channel triggers a throttle countdown
        Assert.assertTrue(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 3

        int pushbackInMillis = 123;
        Metadata headers = new Metadata();
        headers.put(GRPC_RETRY_PUSHBACK_MS, ("" + pushbackInMillis));
        sublistenerCaptor.getValue().closed(Status.fromCode(RetriableStreamTest.NON_RETRIABLE_STATUS_CODE), headers);
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        Assert.assertTrue(throttle.isAboveThreshold());// count = 3

        // drain pending retry
        fakeClock.forwardTime(pushbackInMillis, TimeUnit.MILLISECONDS);
        Assert.assertTrue(throttle.isAboveThreshold());// count = 3

        Assert.assertFalse(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 2

    }

    @Test
    public void throttledStream_FailWithRetriableStatusCode_WithNonRetriablePushback() {
        Throttle throttle = new Throttle(4.0F, 0.8F);
        RetriableStream<String> retriableStream = newThrottledRetriableStream(throttle);
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream).when(retriableStreamRecorder).newSubstream(ArgumentMatchers.anyInt());
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream).start(sublistenerCaptor.capture());
        // mimic some other call in the channel triggers a throttle countdown
        Assert.assertTrue(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 3

        Metadata headers = new Metadata();
        headers.put(GRPC_RETRY_PUSHBACK_MS, "");
        sublistenerCaptor.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), headers);
        Mockito.verify(retriableStreamRecorder).postCommit();
        Assert.assertFalse(throttle.isAboveThreshold());// count = 2

    }

    @Test
    public void throttledStream_FailWithNonRetriableStatusCode_WithNonRetriablePushback() {
        Throttle throttle = new Throttle(4.0F, 0.8F);
        RetriableStream<String> retriableStream = newThrottledRetriableStream(throttle);
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream).when(retriableStreamRecorder).newSubstream(ArgumentMatchers.anyInt());
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream).start(sublistenerCaptor.capture());
        // mimic some other call in the channel triggers a throttle countdown
        Assert.assertTrue(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 3

        Metadata headers = new Metadata();
        headers.put(GRPC_RETRY_PUSHBACK_MS, "");
        sublistenerCaptor.getValue().closed(Status.fromCode(RetriableStreamTest.NON_RETRIABLE_STATUS_CODE), headers);
        Mockito.verify(retriableStreamRecorder).postCommit();
        Assert.assertFalse(throttle.isAboveThreshold());// count = 2

    }

    @Test
    public void throttleStream_Succeed() {
        Throttle throttle = new Throttle(4.0F, 0.8F);
        RetriableStream<String> retriableStream = newThrottledRetriableStream(throttle);
        ClientStream mockStream = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream).when(retriableStreamRecorder).newSubstream(ArgumentMatchers.anyInt());
        retriableStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream).start(sublistenerCaptor.capture());
        // mimic some other calls in the channel trigger throttle countdowns
        Assert.assertTrue(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 3

        Assert.assertFalse(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 2

        Assert.assertFalse(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 1

        sublistenerCaptor.getValue().headersRead(new Metadata());
        Mockito.verify(retriableStreamRecorder).postCommit();
        Assert.assertFalse(throttle.isAboveThreshold());// count = 1.8

        // mimic some other call in the channel triggers a success
        throttle.onSuccess();
        Assert.assertTrue(throttle.isAboveThreshold());// count = 2.6

    }

    @Test
    public void transparentRetry() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder, mockStream1, mockStream2, mockStream3);
        // start
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        inOrder.verify(retriableStreamRecorder).newSubstream(0);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        // transparent retry
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(0);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.NON_RETRIABLE_STATUS_CODE), RpcProgress.REFUSED, new Metadata());
        inOrder.verify(retriableStreamRecorder).newSubstream(0);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        // no more transparent retry
        Mockito.doReturn(mockStream3).when(retriableStreamRecorder).newSubstream(1);
        sublistenerCaptor2.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), RpcProgress.REFUSED, new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        inOrder.verify(retriableStreamRecorder).newSubstream(1);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream3).start(sublistenerCaptor3.capture());
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        Assert.assertEquals(0, fakeClock.numPendingTasks());
    }

    @Test
    public void normalRetry_thenNoTransparentRetry_butNormalRetry() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder, mockStream1, mockStream2, mockStream3);
        // start
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        inOrder.verify(retriableStreamRecorder).newSubstream(0);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        // normal retry
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), RpcProgress.PROCESSED, new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        inOrder.verify(retriableStreamRecorder).newSubstream(1);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        // no more transparent retry
        Mockito.doReturn(mockStream3).when(retriableStreamRecorder).newSubstream(2);
        sublistenerCaptor2.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), RpcProgress.REFUSED, new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(((long) (((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.BACKOFF_MULTIPLIER)) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        inOrder.verify(retriableStreamRecorder).newSubstream(2);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream3).start(sublistenerCaptor3.capture());
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
    }

    @Test
    public void normalRetry_thenNoTransparentRetry_andNoMoreRetry() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder, mockStream1, mockStream2, mockStream3);
        // start
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        retriableStream.start(masterListener);
        inOrder.verify(retriableStreamRecorder).newSubstream(0);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        // normal retry
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1), RpcProgress.PROCESSED, new Metadata());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        fakeClock.forwardTime(((long) ((RetriableStreamTest.INITIAL_BACKOFF_IN_SECONDS) * (RetriableStreamTest.FAKE_RANDOM))), TimeUnit.SECONDS);
        inOrder.verify(retriableStreamRecorder).newSubstream(1);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        // no more transparent retry
        Mockito.doReturn(mockStream3).when(retriableStreamRecorder).newSubstream(2);
        sublistenerCaptor2.getValue().closed(Status.fromCode(RetriableStreamTest.NON_RETRIABLE_STATUS_CODE), RpcProgress.REFUSED, new Metadata());
        Mockito.verify(retriableStreamRecorder).postCommit();
    }

    @Test
    public void noRetry_transparentRetry_earlyCommit() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder, mockStream1, mockStream2);
        RetriableStream<String> unretriableStream = new RetriableStreamTest.RecordedRetriableStream(method, new Metadata(), channelBufferUsed, RetriableStreamTest.PER_RPC_BUFFER_LIMIT, RetriableStreamTest.CHANNEL_BUFFER_LIMIT, MoreExecutors.directExecutor(), fakeClock.getScheduledExecutorService(), DEFAULT, HedgingPolicy.DEFAULT, null);
        // start
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        unretriableStream.start(masterListener);
        inOrder.verify(retriableStreamRecorder).newSubstream(0);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        // transparent retry
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(0);
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.NON_RETRIABLE_STATUS_CODE), RpcProgress.REFUSED, new Metadata());
        inOrder.verify(retriableStreamRecorder).newSubstream(0);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(retriableStreamRecorder).postCommit();
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verifyNoMoreInteractions();
        Assert.assertEquals(0, fakeClock.numPendingTasks());
    }

    @Test
    public void droppedShouldNeverRetry() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        // start
        retriableStream.start(masterListener);
        Mockito.verify(retriableStreamRecorder).newSubstream(0);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        // drop and verify no retry
        Status status = Status.fromCode(RetriableStreamTest.RETRIABLE_STATUS_CODE_1);
        sublistenerCaptor1.getValue().closed(status, RpcProgress.DROPPED, new Metadata());
        Mockito.verifyNoMoreInteractions(mockStream1, mockStream2);
        Mockito.verify(retriableStreamRecorder).postCommit();
        Mockito.verify(masterListener).closed(ArgumentMatchers.same(status), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void hedging_everythingDrained_oneHedgeReceivesNonFatal_oneHedgeReceivesFatalStatus() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        ClientStream mockStream4 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        Mockito.doReturn(mockStream3).when(retriableStreamRecorder).newSubstream(2);
        Mockito.doReturn(mockStream4).when(retriableStreamRecorder).newSubstream(3);
        InOrder inOrder = Mockito.inOrder(retriableStreamRecorder, masterListener, mockStream1, mockStream2, mockStream3, mockStream4);
        // stream settings before start
        hedgingStream.setAuthority(RetriableStreamTest.AUTHORITY);
        hedgingStream.setCompressor(RetriableStreamTest.COMPRESSOR);
        hedgingStream.setDecompressorRegistry(RetriableStreamTest.DECOMPRESSOR_REGISTRY);
        hedgingStream.setFullStreamDecompression(false);
        hedgingStream.setFullStreamDecompression(true);
        hedgingStream.setMaxInboundMessageSize(RetriableStreamTest.MAX_INBOUND_MESSAGE_SIZE);
        hedgingStream.setMessageCompression(true);
        hedgingStream.setMaxOutboundMessageSize(RetriableStreamTest.MAX_OUTBOUND_MESSAGE_SIZE);
        hedgingStream.setMessageCompression(false);
        inOrder.verifyNoMoreInteractions();
        // start
        hedgingStream.start(masterListener);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        inOrder.verify(retriableStreamRecorder).prestart();
        inOrder.verify(retriableStreamRecorder).newSubstream(0);
        inOrder.verify(mockStream1).setAuthority(RetriableStreamTest.AUTHORITY);
        inOrder.verify(mockStream1).setCompressor(RetriableStreamTest.COMPRESSOR);
        inOrder.verify(mockStream1).setDecompressorRegistry(RetriableStreamTest.DECOMPRESSOR_REGISTRY);
        inOrder.verify(mockStream1).setFullStreamDecompression(false);
        inOrder.verify(mockStream1).setFullStreamDecompression(true);
        inOrder.verify(mockStream1).setMaxInboundMessageSize(RetriableStreamTest.MAX_INBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream1).setMessageCompression(true);
        inOrder.verify(mockStream1).setMaxOutboundMessageSize(RetriableStreamTest.MAX_OUTBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream1).setMessageCompression(false);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        hedgingStream.sendMessage("msg1");
        hedgingStream.sendMessage("msg2");
        hedgingStream.request(345);
        hedgingStream.flush();
        hedgingStream.flush();
        hedgingStream.sendMessage("msg3");
        hedgingStream.request(456);
        inOrder.verify(mockStream1, Mockito.times(2)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream1).request(345);
        inOrder.verify(mockStream1, Mockito.times(2)).flush();
        inOrder.verify(mockStream1).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream1).request(456);
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(((RetriableStreamTest.HEDGING_DELAY_IN_SECONDS) - 1), TimeUnit.SECONDS);
        inOrder.verifyNoMoreInteractions();
        // hedge2 starts
        fakeClock.forwardTime(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        inOrder.verify(retriableStreamRecorder).newSubstream(1);
        inOrder.verify(mockStream2).setAuthority(RetriableStreamTest.AUTHORITY);
        inOrder.verify(mockStream2).setCompressor(RetriableStreamTest.COMPRESSOR);
        inOrder.verify(mockStream2).setDecompressorRegistry(RetriableStreamTest.DECOMPRESSOR_REGISTRY);
        inOrder.verify(mockStream2).setFullStreamDecompression(false);
        inOrder.verify(mockStream2).setFullStreamDecompression(true);
        inOrder.verify(mockStream2).setMaxInboundMessageSize(RetriableStreamTest.MAX_INBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream2).setMessageCompression(true);
        inOrder.verify(mockStream2).setMaxOutboundMessageSize(RetriableStreamTest.MAX_OUTBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream2).setMessageCompression(false);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verify(mockStream2, Mockito.times(2)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream2).request(345);
        inOrder.verify(mockStream2, Mockito.times(2)).flush();
        inOrder.verify(mockStream2).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream2).request(456);
        inOrder.verifyNoMoreInteractions();
        // send more messages
        hedgingStream.sendMessage("msg1 after hedge2 starts");
        hedgingStream.sendMessage("msg2 after hedge2 starts");
        inOrder.verify(mockStream1).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream2).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream1).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream2).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verifyNoMoreInteractions();
        // hedge3 starts
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        inOrder.verify(retriableStreamRecorder).newSubstream(2);
        inOrder.verify(mockStream3).setAuthority(RetriableStreamTest.AUTHORITY);
        inOrder.verify(mockStream3).setCompressor(RetriableStreamTest.COMPRESSOR);
        inOrder.verify(mockStream3).setDecompressorRegistry(RetriableStreamTest.DECOMPRESSOR_REGISTRY);
        inOrder.verify(mockStream3).setFullStreamDecompression(false);
        inOrder.verify(mockStream3).setFullStreamDecompression(true);
        inOrder.verify(mockStream3).setMaxInboundMessageSize(RetriableStreamTest.MAX_INBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream3).setMessageCompression(true);
        inOrder.verify(mockStream3).setMaxOutboundMessageSize(RetriableStreamTest.MAX_OUTBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream3).setMessageCompression(false);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream3).start(sublistenerCaptor3.capture());
        inOrder.verify(mockStream3, Mockito.times(2)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream3).request(345);
        inOrder.verify(mockStream3, Mockito.times(2)).flush();
        inOrder.verify(mockStream3).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream3).request(456);
        inOrder.verify(mockStream3, Mockito.times(2)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verifyNoMoreInteractions();
        // send one more message
        hedgingStream.sendMessage("msg1 after hedge3 starts");
        inOrder.verify(mockStream1).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream2).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream3).writeMessage(ArgumentMatchers.any(InputStream.class));
        // hedge3 receives nonFatalStatus
        sublistenerCaptor3.getValue().closed(RetriableStreamTest.NON_FATAL_STATUS_CODE_1.toStatus(), new Metadata());
        inOrder.verifyNoMoreInteractions();
        // send one more message
        hedgingStream.sendMessage("msg1 after hedge3 fails");
        inOrder.verify(mockStream1).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream2).writeMessage(ArgumentMatchers.any(InputStream.class));
        // the hedge mockStream4 starts
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        inOrder.verify(retriableStreamRecorder).newSubstream(3);
        inOrder.verify(mockStream4).setAuthority(RetriableStreamTest.AUTHORITY);
        inOrder.verify(mockStream4).setCompressor(RetriableStreamTest.COMPRESSOR);
        inOrder.verify(mockStream4).setDecompressorRegistry(RetriableStreamTest.DECOMPRESSOR_REGISTRY);
        inOrder.verify(mockStream4).setFullStreamDecompression(false);
        inOrder.verify(mockStream4).setFullStreamDecompression(true);
        inOrder.verify(mockStream4).setMaxInboundMessageSize(RetriableStreamTest.MAX_INBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream4).setMessageCompression(true);
        inOrder.verify(mockStream4).setMaxOutboundMessageSize(RetriableStreamTest.MAX_OUTBOUND_MESSAGE_SIZE);
        inOrder.verify(mockStream4).setMessageCompression(false);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor4 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream4).start(sublistenerCaptor4.capture());
        inOrder.verify(mockStream4, Mockito.times(2)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream4).request(345);
        inOrder.verify(mockStream4, Mockito.times(2)).flush();
        inOrder.verify(mockStream4).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verify(mockStream4).request(456);
        inOrder.verify(mockStream4, Mockito.times(4)).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verifyNoMoreInteractions();
        // commit
        sublistenerCaptor2.getValue().closed(Status.fromCode(RetriableStreamTest.FATAL_STATUS_CODE), new Metadata());
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        inOrder.verify(mockStream1).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        inOrder.verify(mockStream4).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        inOrder.verify(retriableStreamRecorder).postCommit();
        inOrder.verify(masterListener).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void hedging_maxAttempts() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        ClientStream mockStream4 = Mockito.mock(ClientStream.class);
        ClientStream mockStream5 = Mockito.mock(ClientStream.class);
        ClientStream mockStream6 = Mockito.mock(ClientStream.class);
        ClientStream mockStream7 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(mockStream1, mockStream2, mockStream3, mockStream4, mockStream5, mockStream6, mockStream7, retriableStreamRecorder, masterListener);
        Mockito.when(retriableStreamRecorder.newSubstream(ArgumentMatchers.anyInt())).thenReturn(mockStream1, mockStream2, mockStream3, mockStream4, mockStream5, mockStream6, mockStream7);
        hedgingStream.start(masterListener);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream3).start(sublistenerCaptor3.capture());
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor4 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream4).start(sublistenerCaptor4.capture());
        inOrder.verifyNoMoreInteractions();
        // a random one of the hedges fails
        sublistenerCaptor2.getValue().closed(RetriableStreamTest.NON_FATAL_STATUS_CODE_1.toStatus(), new Metadata());
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor5 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream5).start(sublistenerCaptor5.capture());
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor6 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream6).start(sublistenerCaptor6.capture());
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        inOrder.verifyNoMoreInteractions();
        // all but one of the hedges fail
        sublistenerCaptor1.getValue().closed(RetriableStreamTest.NON_FATAL_STATUS_CODE_2.toStatus(), new Metadata());
        sublistenerCaptor4.getValue().closed(RetriableStreamTest.NON_FATAL_STATUS_CODE_2.toStatus(), new Metadata());
        sublistenerCaptor5.getValue().closed(RetriableStreamTest.NON_FATAL_STATUS_CODE_2.toStatus(), new Metadata());
        inOrder.verifyNoMoreInteractions();
        sublistenerCaptor6.getValue().closed(RetriableStreamTest.NON_FATAL_STATUS_CODE_1.toStatus(), new Metadata());
        inOrder.verifyNoMoreInteractions();
        hedgingStream.sendMessage("msg1 after commit");
        inOrder.verify(mockStream3).writeMessage(ArgumentMatchers.any(InputStream.class));
        inOrder.verifyNoMoreInteractions();
        Metadata heders = new Metadata();
        sublistenerCaptor3.getValue().headersRead(heders);
        inOrder.verify(retriableStreamRecorder).postCommit();
        inOrder.verify(masterListener).headersRead(heders);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void hedging_receiveHeaders() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(mockStream1, mockStream2, mockStream3, retriableStreamRecorder, masterListener);
        Mockito.when(retriableStreamRecorder.newSubstream(ArgumentMatchers.anyInt())).thenReturn(mockStream1, mockStream2, mockStream3);
        hedgingStream.start(masterListener);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream3).start(sublistenerCaptor3.capture());
        inOrder.verifyNoMoreInteractions();
        // a random one of the hedges receives headers
        Metadata headers = new Metadata();
        sublistenerCaptor2.getValue().headersRead(headers);
        // all but one of the hedges get cancelled
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        inOrder.verify(mockStream1).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        inOrder.verify(mockStream3).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        inOrder.verify(retriableStreamRecorder).postCommit();
        inOrder.verify(masterListener).headersRead(headers);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void hedging_pushback_negative() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        ClientStream mockStream4 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(mockStream1, mockStream2, mockStream3, mockStream4, retriableStreamRecorder, masterListener);
        Mockito.when(retriableStreamRecorder.newSubstream(ArgumentMatchers.anyInt())).thenReturn(mockStream1, mockStream2, mockStream3, mockStream4);
        hedgingStream.start(masterListener);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream3).start(sublistenerCaptor3.capture());
        inOrder.verifyNoMoreInteractions();
        // a random one of the hedges receives a negative pushback
        Metadata headers = new Metadata();
        headers.put(GRPC_RETRY_PUSHBACK_MS, "-1");
        sublistenerCaptor2.getValue().closed(Status.fromCode(RetriableStreamTest.NON_FATAL_STATUS_CODE_1), headers);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(0, fakeClock.numPendingTasks());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void hedging_pushback_positive() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        ClientStream mockStream4 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(mockStream1, mockStream2, mockStream3, mockStream4, retriableStreamRecorder, masterListener);
        Mockito.when(retriableStreamRecorder.newSubstream(ArgumentMatchers.anyInt())).thenReturn(mockStream1, mockStream2, mockStream3, mockStream4);
        hedgingStream.start(masterListener);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verifyNoMoreInteractions();
        // hedge1 receives a pushback for HEDGING_DELAY_IN_SECONDS + 1 second
        Metadata headers = new Metadata();
        headers.put(GRPC_RETRY_PUSHBACK_MS, "101000");
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.NON_FATAL_STATUS_CODE_1), headers);
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(1, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream3).start(sublistenerCaptor3.capture());
        inOrder.verifyNoMoreInteractions();
        // hedge2 receives a pushback for HEDGING_DELAY_IN_SECONDS - 1 second
        headers = new Metadata();
        headers.put(GRPC_RETRY_PUSHBACK_MS, "99000");
        sublistenerCaptor2.getValue().closed(Status.fromCode(RetriableStreamTest.NON_FATAL_STATUS_CODE_1), headers);
        fakeClock.forwardTime(((RetriableStreamTest.HEDGING_DELAY_IN_SECONDS) - 1), TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor4 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream4).start(sublistenerCaptor4.capture());
        inOrder.verifyNoMoreInteractions();
        // commit
        Status fatal = RetriableStreamTest.FATAL_STATUS_CODE.toStatus();
        Metadata metadata = new Metadata();
        sublistenerCaptor4.getValue().closed(fatal, metadata);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        inOrder.verify(mockStream3).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        inOrder.verify(retriableStreamRecorder).postCommit();
        inOrder.verify(masterListener).closed(fatal, metadata);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void hedging_cancelled() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        InOrder inOrder = Mockito.inOrder(mockStream1, mockStream2, retriableStreamRecorder, masterListener);
        Mockito.when(retriableStreamRecorder.newSubstream(ArgumentMatchers.anyInt())).thenReturn(mockStream1, mockStream2);
        hedgingStream.start(masterListener);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream1).start(sublistenerCaptor1.capture());
        inOrder.verifyNoMoreInteractions();
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        inOrder.verify(mockStream2).start(sublistenerCaptor2.capture());
        inOrder.verifyNoMoreInteractions();
        Status status = CANCELLED.withDescription("cancelled");
        hedgingStream.cancel(status);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        inOrder.verify(mockStream1).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        inOrder.verify(mockStream2).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        inOrder.verify(retriableStreamRecorder).postCommit();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void hedging_perRpcBufferLimitExceeded() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        hedgingStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        ClientStreamTracer bufferSizeTracer1 = bufferSizeTracer;
        bufferSizeTracer1.outboundWireSize(((RetriableStreamTest.PER_RPC_BUFFER_LIMIT) - 1));
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream2).start(sublistenerCaptor2.capture());
        ClientStreamTracer bufferSizeTracer2 = bufferSizeTracer;
        bufferSizeTracer2.outboundWireSize(((RetriableStreamTest.PER_RPC_BUFFER_LIMIT) - 1));
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        // bufferLimitExceeded
        bufferSizeTracer2.outboundWireSize(2);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockStream1).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        Mockito.verify(retriableStreamRecorder).postCommit();
        Mockito.verifyNoMoreInteractions(mockStream1);
        Mockito.verifyNoMoreInteractions(mockStream2);
    }

    @Test
    public void hedging_channelBufferLimitExceeded() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        Mockito.doReturn(mockStream1).when(retriableStreamRecorder).newSubstream(0);
        Mockito.doReturn(mockStream2).when(retriableStreamRecorder).newSubstream(1);
        hedgingStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        ClientStreamTracer bufferSizeTracer1 = bufferSizeTracer;
        bufferSizeTracer1.outboundWireSize(100);
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream2).start(sublistenerCaptor2.capture());
        ClientStreamTracer bufferSizeTracer2 = bufferSizeTracer;
        bufferSizeTracer2.outboundWireSize(100);
        Mockito.verify(retriableStreamRecorder, Mockito.never()).postCommit();
        // channel bufferLimitExceeded
        channelBufferUsed.addAndGet(((RetriableStreamTest.CHANNEL_BUFFER_LIMIT) - 200));
        bufferSizeTracer2.outboundWireSize(101);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockStream1).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        Mockito.verify(retriableStreamRecorder).postCommit();
        Mockito.verifyNoMoreInteractions(mockStream1);
        Mockito.verifyNoMoreInteractions(mockStream2);
        // verify channel buffer is adjusted
        Assert.assertEquals(((RetriableStreamTest.CHANNEL_BUFFER_LIMIT) - 200), channelBufferUsed.addAndGet(0));
    }

    @Test
    public void hedging_transparentRetry() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        ClientStream mockStream4 = Mockito.mock(ClientStream.class);
        Mockito.when(retriableStreamRecorder.newSubstream(ArgumentMatchers.anyInt())).thenReturn(mockStream1, mockStream2, mockStream3, mockStream4);
        hedgingStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream2).start(sublistenerCaptor2.capture());
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream3).start(sublistenerCaptor3.capture());
        // transparent retry for hedge2
        sublistenerCaptor2.getValue().closed(Status.fromCode(RetriableStreamTest.FATAL_STATUS_CODE), RpcProgress.REFUSED, new Metadata());
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor4 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream4).start(sublistenerCaptor4.capture());
        Assert.assertEquals(1, fakeClock.numPendingTasks());
        // no more transparent retry
        Status status = Status.fromCode(RetriableStreamTest.FATAL_STATUS_CODE);
        Metadata metadata = new Metadata();
        sublistenerCaptor3.getValue().closed(status, RpcProgress.REFUSED, metadata);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockStream1).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        Mockito.verify(mockStream4).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        Mockito.verify(retriableStreamRecorder).postCommit();
        Mockito.verify(masterListener).closed(status, metadata);
    }

    @Test
    public void hedging_transparentRetryNotAllowed() {
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        Mockito.when(retriableStreamRecorder.newSubstream(ArgumentMatchers.anyInt())).thenReturn(mockStream1, mockStream2, mockStream3);
        hedgingStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor2 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream2).start(sublistenerCaptor2.capture());
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor3 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream3).start(sublistenerCaptor3.capture());
        sublistenerCaptor2.getValue().closed(Status.fromCode(RetriableStreamTest.NON_FATAL_STATUS_CODE_1), new Metadata());
        // no more transparent retry
        Status status = Status.fromCode(RetriableStreamTest.FATAL_STATUS_CODE);
        Metadata metadata = new Metadata();
        sublistenerCaptor3.getValue().closed(status, RpcProgress.REFUSED, metadata);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockStream1).cancel(statusCaptor.capture());
        Assert.assertEquals(CANCELLED.getCode(), statusCaptor.getValue().getCode());
        Assert.assertEquals(RetriableStreamTest.CANCELLED_BECAUSE_COMMITTED, statusCaptor.getValue().getDescription());
        Mockito.verify(retriableStreamRecorder).postCommit();
        Mockito.verify(masterListener).closed(status, metadata);
    }

    @Test
    public void hedging_throttled() {
        Throttle throttle = new Throttle(4.0F, 0.8F);
        RetriableStream<String> hedgingStream = newThrottledHedgingStream(throttle);
        ClientStream mockStream1 = Mockito.mock(ClientStream.class);
        ClientStream mockStream2 = Mockito.mock(ClientStream.class);
        ClientStream mockStream3 = Mockito.mock(ClientStream.class);
        Mockito.when(retriableStreamRecorder.newSubstream(ArgumentMatchers.anyInt())).thenReturn(mockStream1, mockStream2, mockStream3);
        hedgingStream.start(masterListener);
        ArgumentCaptor<ClientStreamListener> sublistenerCaptor1 = ArgumentCaptor.forClass(ClientStreamListener.class);
        Mockito.verify(mockStream1).start(sublistenerCaptor1.capture());
        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Mockito.verify(mockStream2).start(ArgumentMatchers.any(ClientStreamListener.class));
        sublistenerCaptor1.getValue().closed(Status.fromCode(RetriableStreamTest.NON_FATAL_STATUS_CODE_1), new Metadata());
        Assert.assertTrue(throttle.isAboveThreshold());// count = 3

        // mimic some other call in the channel triggers a throttle countdown
        Assert.assertFalse(throttle.onQualifiedFailureThenCheckIsAboveThreshold());// count = 2

        fakeClock.forwardTime(RetriableStreamTest.HEDGING_DELAY_IN_SECONDS, TimeUnit.SECONDS);
        Mockito.verify(mockStream3).start(ArgumentMatchers.any(ClientStreamListener.class));
        Assert.assertEquals(0, fakeClock.numPendingTasks());
    }

    /**
     * Used to stub a retriable stream as well as to record methods of the retriable stream being
     * called.
     */
    private interface RetriableStreamRecorder {
        void postCommit();

        ClientStream newSubstream(int previousAttempts);

        Status prestart();
    }
}

