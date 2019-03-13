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


import AbstractServerStream.Sink;
import InternalStatus.CODE_KEY;
import InternalStatus.MESSAGE_KEY;
import StatsTraceContext.NOOP;
import Status.CANCELLED;
import Status.INTERNAL;
import Status.OK;
import com.google.common.util.concurrent.SettableFuture;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.AbstractServerStream.TransportState;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;


/**
 * Tests for {@link AbstractServerStream}.
 */
@RunWith(JUnit4.class)
public class AbstractServerStreamTest {
    private static final int TIMEOUT_MS = 1000;

    private static final int MAX_MESSAGE_SIZE = 100;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final WritableBufferAllocator allocator = new WritableBufferAllocator() {
        @Override
        public WritableBuffer allocate(int capacityHint) {
            return new MessageFramerTest.ByteWritableBuffer(capacityHint);
        }
    };

    private Sink sink = Mockito.mock(Sink.class);

    private TransportTracer transportTracer;

    private AbstractServerStreamTest.AbstractServerStreamBase stream;

    private final ArgumentCaptor<Metadata> metadataCaptor = ArgumentCaptor.forClass(Metadata.class);

    /**
     * Test for issue https://github.com/grpc/grpc-java/issues/1795
     */
    @Test
    public void frameShouldBeIgnoredAfterDeframerClosed() {
        final Queue<InputStream> streamListenerMessageQueue = new LinkedList<>();
        stream.transportState().setListener(new AbstractServerStreamTest.ServerStreamListenerBase() {
            @Override
            public void messagesAvailable(MessageProducer producer) {
                InputStream message;
                while ((message = producer.next()) != null) {
                    streamListenerMessageQueue.add(message);
                } 
            }
        });
        ReadableBuffer buffer = Mockito.mock(ReadableBuffer.class);
        // Close the deframer
        stream.close(OK, new Metadata());
        stream.transportState().complete();
        // Frame received after deframer closed, should be ignored and not trigger an exception
        stream.transportState().inboundDataReceived(buffer, true);
        Mockito.verify(buffer).close();
        Assert.assertNull("no message expected", streamListenerMessageQueue.poll());
    }

    @Test
    public void queuedBytesInDeframerShouldNotBlockComplete() throws Exception {
        final SettableFuture<Status> closedFuture = SettableFuture.create();
        stream.transportState().setListener(new AbstractServerStreamTest.ServerStreamListenerBase() {
            @Override
            public void closed(Status status) {
                closedFuture.set(status);
            }
        });
        // Queue bytes in deframer
        stream.transportState().inboundDataReceived(ReadableBuffers.wrap(new byte[]{ 1 }), false);
        stream.close(OK, new Metadata());
        stream.transportState().complete();
        Assert.assertEquals(OK, closedFuture.get(AbstractServerStreamTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
    }

    @Test
    public void queuedBytesInDeframerShouldNotBlockTransportReportStatus() throws Exception {
        final SettableFuture<Status> closedFuture = SettableFuture.create();
        stream.transportState().setListener(new AbstractServerStreamTest.ServerStreamListenerBase() {
            @Override
            public void closed(Status status) {
                closedFuture.set(status);
            }
        });
        // Queue bytes in deframer
        stream.transportState().inboundDataReceived(ReadableBuffers.wrap(new byte[]{ 1 }), false);
        stream.transportState().transportReportStatus(CANCELLED);
        Assert.assertEquals(CANCELLED, closedFuture.get(AbstractServerStreamTest.TIMEOUT_MS, TimeUnit.MILLISECONDS));
    }

    @Test
    public void partialMessageAtEndOfStreamShouldFail() throws Exception {
        final SettableFuture<Status> closedFuture = SettableFuture.create();
        stream.transportState().setListener(new AbstractServerStreamTest.ServerStreamListenerBase() {
            @Override
            public void closed(Status status) {
                closedFuture.set(status);
            }
        });
        // Queue a partial message in the deframer
        stream.transportState().inboundDataReceived(ReadableBuffers.wrap(new byte[]{ 1 }), true);
        stream.transportState().requestMessagesFromDeframer(1);
        Status status = closedFuture.get(AbstractServerStreamTest.TIMEOUT_MS, TimeUnit.MILLISECONDS);
        Assert.assertEquals(INTERNAL.getCode(), status.getCode());
        Assert.assertEquals("Encountered end-of-stream mid-frame", status.getDescription());
    }

    /**
     * Test for issue https://github.com/grpc/grpc-java/issues/615
     */
    @Test
    public void completeWithoutClose() {
        stream.transportState().setListener(new AbstractServerStreamTest.ServerStreamListenerBase());
        // Test that it doesn't throw an exception
        stream.close(OK, new Metadata());
        stream.transportState().complete();
    }

    @Test
    public void setListener_setOnlyOnce() {
        TransportState state = stream.transportState();
        state.setListener(new AbstractServerStreamTest.ServerStreamListenerBase());
        thrown.expect(IllegalStateException.class);
        state.setListener(new AbstractServerStreamTest.ServerStreamListenerBase());
    }

    @Test
    public void listenerReady_onlyOnce() {
        stream.transportState().setListener(new AbstractServerStreamTest.ServerStreamListenerBase());
        stream.transportState().onStreamAllocated();
        TransportState state = stream.transportState();
        thrown.expect(IllegalStateException.class);
        state.onStreamAllocated();
    }

    @Test
    public void listenerReady_readyCalled() {
        ServerStreamListener streamListener = Mockito.mock(ServerStreamListener.class);
        stream.transportState().setListener(streamListener);
        stream.transportState().onStreamAllocated();
        Mockito.verify(streamListener).onReady();
    }

    @Test
    public void setListener_failsOnNull() {
        TransportState state = stream.transportState();
        thrown.expect(NullPointerException.class);
        state.setListener(null);
    }

    // TODO(ericgribkoff) This test is only valid if deframeInTransportThread=true, as otherwise the
    // message is queued.
    /* @Test
    public void messageRead_listenerCalled() {
    final Queue<InputStream> streamListenerMessageQueue = new LinkedList<InputStream>();
    stream.transportState().setListener(new ServerStreamListenerBase() {
    @Override
    public void messagesAvailable(MessageProducer producer) {
    InputStream message;
    while ((message = producer.next()) != null) {
    streamListenerMessageQueue.add(message);
    }
    }
    });

    // Normally called by a deframe event.
    stream.transportState().messageRead(new ByteArrayInputStream(new byte[]{}));

    assertNotNull(streamListenerMessageQueue.poll());
    }
     */
    @Test
    public void writeHeaders_failsOnNullHeaders() {
        thrown.expect(NullPointerException.class);
        stream.writeHeaders(null);
    }

    @Test
    public void writeHeaders() {
        Metadata headers = new Metadata();
        stream.writeHeaders(headers);
        Mockito.verify(sink).writeHeaders(same(headers));
    }

    @Test
    public void writeMessage_dontWriteDuplicateHeaders() {
        stream.writeHeaders(new Metadata());
        stream.writeMessage(new ByteArrayInputStream(new byte[]{  }));
        // Make sure it wasn't called twice
        Mockito.verify(sink).writeHeaders(any(Metadata.class));
    }

    @Test
    public void writeMessage_ignoreIfFramerClosed() {
        stream.writeHeaders(new Metadata());
        endOfMessages();
        Mockito.reset(sink);
        stream.writeMessage(new ByteArrayInputStream(new byte[]{  }));
        Mockito.verify(sink, Mockito.never()).writeFrame(ArgumentMatchers.any(WritableBuffer.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt());
    }

    @Test
    public void writeMessage() {
        stream.writeHeaders(new Metadata());
        stream.writeMessage(new ByteArrayInputStream(new byte[]{  }));
        flush();
        Mockito.verify(sink).writeFrame(ArgumentMatchers.any(WritableBuffer.class), ArgumentMatchers.eq(true), ArgumentMatchers.eq(1));
    }

    @Test
    public void writeMessage_closesStream() throws Exception {
        stream.writeHeaders(new Metadata());
        InputStream input = Mockito.mock(InputStream.class, AdditionalAnswers.delegatesTo(new ByteArrayInputStream(new byte[1])));
        stream.writeMessage(input);
        Mockito.verify(input).close();
    }

    @Test
    public void close_failsOnNullStatus() {
        thrown.expect(NullPointerException.class);
        stream.close(null, new Metadata());
    }

    @Test
    public void close_failsOnNullMetadata() {
        thrown.expect(NullPointerException.class);
        stream.close(INTERNAL, null);
    }

    @Test
    public void close_sendsTrailers() {
        Metadata trailers = new Metadata();
        stream.close(INTERNAL, trailers);
        Mockito.verify(sink).writeTrailers(any(Metadata.class), ArgumentMatchers.eq(false), ArgumentMatchers.eq(INTERNAL));
    }

    @Test
    public void close_sendTrailersClearsReservedFields() {
        // stream actually mutates trailers, so we can't check that the fields here are the same as
        // the captured ones.
        Metadata trailers = new Metadata();
        trailers.put(CODE_KEY, OK);
        trailers.put(MESSAGE_KEY, "Everything's super.");
        Status closeStatus = INTERNAL.withDescription("bad");
        stream.close(closeStatus, trailers);
        Mockito.verify(sink).writeTrailers(metadataCaptor.capture(), ArgumentMatchers.eq(false), ArgumentMatchers.eq(closeStatus));
        Assert.assertEquals(Status.Code.INTERNAL, metadataCaptor.getValue().get(CODE_KEY).getCode());
        Assert.assertEquals("bad", metadataCaptor.getValue().get(MESSAGE_KEY));
    }

    private static class ServerStreamListenerBase implements ServerStreamListener {
        @Override
        public void messagesAvailable(MessageProducer producer) {
            InputStream message;
            while ((message = producer.next()) != null) {
                try {
                    message.close();
                } catch (IOException e) {
                    // Continue to close other messages
                }
            } 
        }

        @Override
        public void onReady() {
        }

        @Override
        public void halfClosed() {
        }

        @Override
        public void closed(Status status) {
        }
    }

    private static class AbstractServerStreamBase extends AbstractServerStream {
        private final Sink sink;

        private final io.grpc.internal.AbstractServerStream.TransportState state;

        protected AbstractServerStreamBase(WritableBufferAllocator bufferAllocator, Sink sink, AbstractServerStream.TransportState state) {
            super(bufferAllocator, NOOP);
            this.sink = sink;
            this.state = state;
        }

        @Override
        protected Sink abstractServerStreamSink() {
            return sink;
        }

        @Override
        protected io.grpc.internal.AbstractServerStream.TransportState transportState() {
            return state;
        }

        static class TransportState extends AbstractServerStream.TransportState {
            protected TransportState(int maxMessageSize, TransportTracer transportTracer) {
                super(maxMessageSize, NOOP, transportTracer);
            }

            @Override
            public void deframeFailed(Throwable cause) {
                Status status = Status.fromThrowable(cause);
                transportReportStatus(status);
            }

            @Override
            public void bytesRead(int processedBytes) {
            }

            @Override
            public void runOnTransportThread(Runnable r) {
                r.run();
            }
        }
    }
}

