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


import AbstractClientStream.Sink;
import CallOptions.DEFAULT;
import Codec.Identity.NONE;
import GrpcUtil.CONTENT_ENCODING_KEY;
import GrpcUtil.MESSAGE_ENCODING_KEY;
import GrpcUtil.TIMEOUT_KEY;
import Status.CANCELLED;
import Status.DATA_LOSS;
import Status.DEADLINE_EXCEEDED;
import Status.INTERNAL;
import Status.OK;
import io.grpc.Attributes;
import io.grpc.Codec;
import io.grpc.Deadline;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StreamTracer;
import io.grpc.internal.AbstractClientStream.TransportState;
import io.grpc.internal.testing.TestClientStreamTracer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static StatsTraceContext.NOOP;


/**
 * Test for {@link AbstractClientStream}.  This class tries to test functionality in
 * AbstractClientStream, but not in any super classes.
 */
@RunWith(JUnit4.class)
public class AbstractClientStreamTest {
    @Rule
    public final MockitoRule mocks = MockitoJUnit.rule();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final StatsTraceContext statsTraceCtx = NOOP;

    private final TransportTracer transportTracer = new TransportTracer();

    @Mock
    private ClientStreamListener mockListener;

    private final WritableBufferAllocator allocator = new WritableBufferAllocator() {
        @Override
        public WritableBuffer allocate(int capacityHint) {
            return new MessageFramerTest.ByteWritableBuffer(capacityHint);
        }
    };

    @Test
    public void cancel_doNotAcceptOk() {
        for (Code code : Code.values()) {
            ClientStreamListener listener = new NoopClientStreamListener();
            AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
            stream.start(listener);
            if (code != (Code.OK)) {
                stream.cancel(Status.fromCodeValue(code.value()));
            } else {
                try {
                    stream.cancel(Status.fromCodeValue(code.value()));
                    Assert.fail();
                } catch (IllegalArgumentException e) {
                    // ignore
                }
            }
        }
    }

    @Test
    public void cancel_failsOnNull() {
        ClientStreamListener listener = new NoopClientStreamListener();
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(listener);
        thrown.expect(NullPointerException.class);
        stream.cancel(null);
    }

    @Test
    public void cancel_notifiesOnlyOnce() {
        final AbstractClientStreamTest.BaseTransportState state = new AbstractClientStreamTest.BaseTransportState(statsTraceCtx, transportTracer);
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, state, new AbstractClientStreamTest.BaseSink() {
            @Override
            public void cancel(Status errorStatus) {
                // Cancel should eventually result in a transportReportStatus on the transport thread
                /* stop delivery */
                state.transportReportStatus(errorStatus, true, new Metadata());
            }
        }, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        stream.cancel(DEADLINE_EXCEEDED);
        stream.cancel(DEADLINE_EXCEEDED);
        Mockito.verify(mockListener).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.same(RpcProgress.PROCESSED), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void startFailsOnNullListener() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        thrown.expect(NullPointerException.class);
        stream.start(null);
    }

    @Test
    public void cantCallStartTwice() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        thrown.expect(IllegalStateException.class);
        stream.start(mockListener);
    }

    @Test
    public void inboundDataReceived_failsOnNullFrame() {
        ClientStreamListener listener = new NoopClientStreamListener();
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(listener);
        TransportState state = stream.transportState();
        thrown.expect(NullPointerException.class);
        state.inboundDataReceived(null);
    }

    @Test
    public void inboundHeadersReceived_notifiesListener() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        Metadata headers = new Metadata();
        stream.transportState().inboundHeadersReceived(headers);
        Mockito.verify(mockListener).headersRead(headers);
    }

    @Test
    public void inboundHeadersReceived_failsIfStatusReported() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        stream.transportState().transportReportStatus(CANCELLED, false, new Metadata());
        TransportState state = stream.transportState();
        thrown.expect(IllegalStateException.class);
        state.inboundHeadersReceived(new Metadata());
    }

    @Test
    public void inboundHeadersReceived_acceptsGzipContentEncoding() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        Metadata headers = new Metadata();
        headers.put(CONTENT_ENCODING_KEY, "gzip");
        stream.setFullStreamDecompression(true);
        stream.transportState().inboundHeadersReceived(headers);
        Mockito.verify(mockListener).headersRead(headers);
    }

    // https://tools.ietf.org/html/rfc7231#section-3.1.2.1
    @Test
    public void inboundHeadersReceived_contentEncodingIsCaseInsensitive() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        Metadata headers = new Metadata();
        headers.put(CONTENT_ENCODING_KEY, "gZIp");
        stream.setFullStreamDecompression(true);
        stream.transportState().inboundHeadersReceived(headers);
        Mockito.verify(mockListener).headersRead(headers);
    }

    @Test
    public void inboundHeadersReceived_failsOnUnrecognizedContentEncoding() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        Metadata headers = new Metadata();
        headers.put(CONTENT_ENCODING_KEY, "not-a-real-compression-method");
        stream.setFullStreamDecompression(true);
        stream.transportState().inboundHeadersReceived(headers);
        Mockito.verifyNoMoreInteractions(mockListener);
        Throwable t = ((AbstractClientStreamTest.BaseTransportState) (stream.transportState())).getDeframeFailedCause();
        Assert.assertEquals(INTERNAL.getCode(), Status.fromThrowable(t).getCode());
        Assert.assertTrue("unexpected deframe failed description", Status.fromThrowable(t).getDescription().startsWith("Can't find full stream decompressor for"));
    }

    @Test
    public void inboundHeadersReceived_disallowsContentAndMessageEncoding() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        Metadata headers = new Metadata();
        headers.put(CONTENT_ENCODING_KEY, "gzip");
        headers.put(MESSAGE_ENCODING_KEY, new Codec.Gzip().getMessageEncoding());
        stream.setFullStreamDecompression(true);
        stream.transportState().inboundHeadersReceived(headers);
        Mockito.verifyNoMoreInteractions(mockListener);
        Throwable t = ((AbstractClientStreamTest.BaseTransportState) (stream.transportState())).getDeframeFailedCause();
        Assert.assertEquals(INTERNAL.getCode(), Status.fromThrowable(t).getCode());
        Assert.assertTrue("unexpected deframe failed description", Status.fromThrowable(t).getDescription().equals("Full stream and gRPC message encoding cannot both be set"));
    }

    @Test
    public void inboundHeadersReceived_acceptsGzipMessageEncoding() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        Metadata headers = new Metadata();
        headers.put(MESSAGE_ENCODING_KEY, new Codec.Gzip().getMessageEncoding());
        stream.transportState().inboundHeadersReceived(headers);
        Mockito.verify(mockListener).headersRead(headers);
    }

    @Test
    public void inboundHeadersReceived_acceptsIdentityMessageEncoding() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        Metadata headers = new Metadata();
        headers.put(MESSAGE_ENCODING_KEY, NONE.getMessageEncoding());
        stream.transportState().inboundHeadersReceived(headers);
        Mockito.verify(mockListener).headersRead(headers);
    }

    @Test
    public void inboundHeadersReceived_failsOnUnrecognizedMessageEncoding() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        Metadata headers = new Metadata();
        headers.put(MESSAGE_ENCODING_KEY, "not-a-real-compression-method");
        stream.transportState().inboundHeadersReceived(headers);
        Mockito.verifyNoMoreInteractions(mockListener);
        Throwable t = ((AbstractClientStreamTest.BaseTransportState) (stream.transportState())).getDeframeFailedCause();
        Assert.assertEquals(INTERNAL.getCode(), Status.fromThrowable(t).getCode());
        Assert.assertTrue("unexpected deframe failed description", Status.fromThrowable(t).getDescription().startsWith("Can't find decompressor for"));
    }

    @Test
    public void rstStreamClosesStream() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        // The application will call request when waiting for a message, which will in turn call this
        // on the transport thread.
        stream.transportState().requestMessagesFromDeframer(1);
        // Send first byte of 2 byte message
        stream.transportState().deframe(ReadableBuffers.wrap(new byte[]{ 0, 0, 0, 0, 2, 1 }));
        Status status = INTERNAL.withDescription("rst___stream");
        // Simulate getting a reset
        /* stop delivery */
        stream.transportState().transportReportStatus(status, false, new Metadata());
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.any(io.grpc.internal.ClientStreamListener.RpcProgress.class), ArgumentMatchers.any(Metadata.class));
        Assert.assertSame(Status.Code.INTERNAL, statusCaptor.getValue().getCode());
        Assert.assertEquals("rst___stream", statusCaptor.getValue().getDescription());
    }

    @Test
    public void statusOkFollowedByRstStreamNoError() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        stream.transportState().deframe(ReadableBuffers.wrap(new byte[]{ 0, 0, 0, 0, 1, 1 }));
        stream.transportState().inboundTrailersReceived(new Metadata(), OK);
        Status status = INTERNAL.withDescription("rst___stream");
        // Simulate getting a reset
        /* stop delivery */
        stream.transportState().transportReportStatus(status, false, new Metadata());
        stream.transportState().requestMessagesFromDeframer(1);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.any(io.grpc.internal.ClientStreamListener.RpcProgress.class), ArgumentMatchers.any(Metadata.class));
        Assert.assertTrue(statusCaptor.getValue().isOk());
    }

    @Test
    public void trailerOkWithTruncatedMessage() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        stream.transportState().requestMessagesFromDeframer(1);
        stream.transportState().deframe(ReadableBuffers.wrap(new byte[]{ 0, 0, 0, 0, 2, 1 }));
        stream.transportState().inboundTrailersReceived(new Metadata(), OK);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.any(io.grpc.internal.ClientStreamListener.RpcProgress.class), ArgumentMatchers.any(Metadata.class));
        Assert.assertSame(Status.Code.INTERNAL, statusCaptor.getValue().getCode());
        Assert.assertEquals("Encountered end-of-stream mid-frame", statusCaptor.getValue().getDescription());
    }

    @Test
    public void trailerNotOkWithTruncatedMessage() {
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, statsTraceCtx, transportTracer);
        stream.start(mockListener);
        stream.transportState().requestMessagesFromDeframer(1);
        stream.transportState().deframe(ReadableBuffers.wrap(new byte[]{ 0, 0, 0, 0, 2, 1 }));
        stream.transportState().inboundTrailersReceived(new Metadata(), DATA_LOSS.withDescription("data___loss"));
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(mockListener).closed(statusCaptor.capture(), ArgumentMatchers.any(io.grpc.internal.ClientStreamListener.RpcProgress.class), ArgumentMatchers.any(Metadata.class));
        Assert.assertSame(Status.Code.DATA_LOSS, statusCaptor.getValue().getCode());
        Assert.assertEquals("data___loss", statusCaptor.getValue().getDescription());
    }

    @Test
    public void getRequest() {
        AbstractClientStream.Sink sink = Mockito.mock(Sink.class);
        final TestClientStreamTracer tracer = new TestClientStreamTracer();
        StatsTraceContext statsTraceCtx = new StatsTraceContext(new StreamTracer[]{ tracer });
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, new AbstractClientStreamTest.BaseTransportState(statsTraceCtx, transportTracer), sink, statsTraceCtx, transportTracer, true);
        stream.start(mockListener);
        stream.writeMessage(new ByteArrayInputStream(new byte[1]));
        // writeHeaders will be delayed since we're sending a GET request.
        Mockito.verify(sink, Mockito.never()).writeHeaders(ArgumentMatchers.any(Metadata.class), ArgumentMatchers.any(byte[].class));
        // halfClose will trigger writeHeaders.
        stream.halfClose();
        ArgumentCaptor<byte[]> payloadCaptor = ArgumentCaptor.forClass(byte[].class);
        Mockito.verify(sink).writeHeaders(ArgumentMatchers.any(Metadata.class), payloadCaptor.capture());
        Assert.assertTrue(((payloadCaptor.getValue()) != null));
        // GET requests don't have BODY.
        Mockito.verify(sink, Mockito.never()).writeFrame(ArgumentMatchers.any(WritableBuffer.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt());
        assertThat(tracer.nextOutboundEvent()).isEqualTo("outboundMessage(0)");
        assertThat(tracer.nextOutboundEvent()).matches("outboundMessageSent\\(0, [0-9]+, [0-9]+\\)");
        Assert.assertNull(tracer.nextOutboundEvent());
        Assert.assertNull(tracer.nextInboundEvent());
        Assert.assertEquals(1, tracer.getOutboundWireSize());
        Assert.assertEquals(1, tracer.getOutboundUncompressedSize());
    }

    @Test
    public void writeMessage_closesStream() throws IOException {
        AbstractClientStream.Sink sink = Mockito.mock(Sink.class);
        final TestClientStreamTracer tracer = new TestClientStreamTracer();
        StatsTraceContext statsTraceCtx = new StatsTraceContext(new StreamTracer[]{ tracer });
        AbstractClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, new AbstractClientStreamTest.BaseTransportState(statsTraceCtx, transportTracer), sink, statsTraceCtx, transportTracer, true);
        stream.start(mockListener);
        InputStream input = Mockito.mock(InputStream.class, AdditionalAnswers.delegatesTo(new ByteArrayInputStream(new byte[1])));
        stream.writeMessage(input);
        Mockito.verify(input).close();
    }

    @Test
    public void deadlineTimeoutPopulatedToHeaders() {
        AbstractClientStream.Sink sink = Mockito.mock(Sink.class);
        ClientStream stream = new AbstractClientStreamTest.BaseAbstractClientStream(allocator, new AbstractClientStreamTest.BaseTransportState(statsTraceCtx, transportTracer), sink, statsTraceCtx, transportTracer);
        stream.setDeadline(Deadline.after(1, TimeUnit.SECONDS));
        stream.start(mockListener);
        ArgumentCaptor<Metadata> headersCaptor = ArgumentCaptor.forClass(Metadata.class);
        Mockito.verify(sink).writeHeaders(headersCaptor.capture(), ArgumentMatchers.any(byte[].class));
        Metadata headers = headersCaptor.getValue();
        Assert.assertTrue(headers.containsKey(TIMEOUT_KEY));
        assertThat(headers.get(TIMEOUT_KEY).longValue()).isLessThan(TimeUnit.SECONDS.toNanos(1));
        assertThat(headers.get(TIMEOUT_KEY).longValue()).isGreaterThan(TimeUnit.MILLISECONDS.toNanos(600));
    }

    /**
     * No-op base class for testing.
     */
    private static class BaseAbstractClientStream extends AbstractClientStream {
        private final TransportState state;

        private final Sink sink;

        public BaseAbstractClientStream(WritableBufferAllocator allocator, StatsTraceContext statsTraceCtx, TransportTracer transportTracer) {
            this(allocator, new AbstractClientStreamTest.BaseTransportState(statsTraceCtx, transportTracer), new AbstractClientStreamTest.BaseSink(), statsTraceCtx, transportTracer);
        }

        public BaseAbstractClientStream(WritableBufferAllocator allocator, TransportState state, Sink sink, StatsTraceContext statsTraceCtx, TransportTracer transportTracer) {
            this(allocator, state, sink, statsTraceCtx, transportTracer, false);
        }

        public BaseAbstractClientStream(WritableBufferAllocator allocator, TransportState state, Sink sink, StatsTraceContext statsTraceCtx, TransportTracer transportTracer, boolean useGet) {
            super(allocator, statsTraceCtx, transportTracer, new Metadata(), DEFAULT, useGet);
            this.state = state;
            this.sink = sink;
        }

        @Override
        protected Sink abstractClientStreamSink() {
            return sink;
        }

        @Override
        public TransportState transportState() {
            return state;
        }

        @Override
        public void setAuthority(String authority) {
        }

        @Override
        public void setMaxInboundMessageSize(int maxSize) {
        }

        @Override
        public void setMaxOutboundMessageSize(int maxSize) {
        }

        @Override
        public Attributes getAttributes() {
            return Attributes.EMPTY;
        }
    }

    private static class BaseSink implements AbstractClientStream.Sink {
        @Override
        public void writeHeaders(Metadata headers, byte[] payload) {
        }

        @Override
        public void request(int numMessages) {
        }

        @Override
        public void writeFrame(WritableBuffer frame, boolean endOfStream, boolean flush, int numMessages) {
        }

        @Override
        public void cancel(Status reason) {
        }
    }

    private static class BaseTransportState extends AbstractClientStream.TransportState {
        private Throwable deframeFailedCause;

        private Throwable getDeframeFailedCause() {
            return deframeFailedCause;
        }

        public BaseTransportState(StatsTraceContext statsTraceCtx, TransportTracer transportTracer) {
            super(GrpcUtil.DEFAULT_MAX_MESSAGE_SIZE, statsTraceCtx, transportTracer);
        }

        @Override
        public void deframeFailed(Throwable cause) {
            Assert.assertNull("deframeFailed already called", deframeFailedCause);
            deframeFailedCause = cause;
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

