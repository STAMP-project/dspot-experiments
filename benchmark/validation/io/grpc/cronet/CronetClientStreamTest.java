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
package io.grpc.cronet;


import BidirectionalStream.Callback;
import CallOptions.DEFAULT;
import CronetCallOptions.CRONET_ANNOTATION_KEY;
import ExperimentalBidirectionalStream.Builder;
import GrpcUtil.CONTENT_TYPE_GRPC;
import GrpcUtil.CONTENT_TYPE_KEY;
import GrpcUtil.TE_HEADER;
import GrpcUtil.TE_TRAILERS;
import GrpcUtil.USER_AGENT_KEY;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.Key;
import MethodDescriptor.MethodType.UNARY;
import Status.DEADLINE_EXCEEDED;
import Status.OK;
import Status.PERMISSION_DENIED;
import Status.UNAUTHENTICATED;
import Status.UNAVAILABLE;
import com.google.common.io.BaseEncoding;
import io.grpc.CallOptions;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.cronet.CronetChannelBuilder.StreamBuilderFactory;
import io.grpc.internal.ClientStreamListener;
import io.grpc.internal.ClientStreamListener.RpcProgress;
import io.grpc.internal.StatsTraceContext;
import io.grpc.internal.StreamListener.MessageProducer;
import io.grpc.internal.TransportTracer;
import io.grpc.internal.WritableBuffer;
import io.grpc.testing.TestMethodDescriptors;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import org.chromium.net.BidirectionalStream;
import org.chromium.net.CronetException;
import org.chromium.net.ExperimentalBidirectionalStream;
import org.chromium.net.UrlResponseInfo;
import org.chromium.net.impl.UrlResponseInfoImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

import static java.nio.charset.Charset.forName;


@RunWith(RobolectricTestRunner.class)
public final class CronetClientStreamTest {
    @Mock
    private CronetClientTransport transport;

    private Metadata metadata = new Metadata();

    @Mock
    private StreamBuilderFactory factory;

    @Mock
    private ExperimentalBidirectionalStream cronetStream;

    @Mock
    private Executor executor;

    @Mock
    private ClientStreamListener clientListener;

    @Mock
    private Builder builder;

    private final Object lock = new Object();

    private final TransportTracer transportTracer = TransportTracer.getDefaultFactory().create();

    CronetClientStream clientStream;

    private MethodDescriptor.Marshaller<Void> marshaller = TestMethodDescriptors.voidMarshaller();

    private MethodDescriptor<?, ?> method = TestMethodDescriptors.voidMethod();

    private static class SetStreamFactoryRunnable implements Runnable {
        private final StreamBuilderFactory factory;

        private CronetClientStream stream;

        SetStreamFactoryRunnable(StreamBuilderFactory factory) {
            this.factory = factory;
        }

        void setStream(CronetClientStream stream) {
            this.stream = stream;
        }

        @Override
        public void run() {
            Assert.assertTrue(((stream) != null));
            stream.transportState().start(factory);
        }
    }

    @Test
    public void startStream() {
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.eq("https://www.google.com:443"), ArgumentMatchers.isA(Callback.class), ArgumentMatchers.eq(executor));
        Mockito.verify(builder).build();
        // At least content type and trailer headers are set.
        Mockito.verify(builder, Mockito.atLeast(2)).addHeader(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(String.class));
        // addRequestAnnotation should only be called when we explicitly add the CRONET_ANNOTATION_KEY
        // to CallOptions.
        Mockito.verify(builder, Mockito.times(0)).addRequestAnnotation(ArgumentMatchers.isA(Object.class));
        Mockito.verify(builder, Mockito.times(0)).setHttpMethod(ArgumentMatchers.any(String.class));
        Mockito.verify(cronetStream).start();
    }

    @Test
    public void write() {
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), callbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        BidirectionalStream.Callback callback = callbackCaptor.getValue();
        // Create 5 frames to send.
        CronetWritableBufferAllocator allocator = new CronetWritableBufferAllocator();
        String[] requests = new String[5];
        WritableBuffer[] buffers = new WritableBuffer[5];
        for (int i = 0; i < 5; ++i) {
            requests[i] = new String(("request" + (String.valueOf(i))));
            buffers[i] = allocator.allocate(requests[i].length());
            buffers[i].write(requests[i].getBytes(forName("UTF-8")), 0, requests[i].length());
            // The 3rd and 5th writeFrame calls have flush=true.
            clientStream.abstractClientStreamSink().writeFrame(buffers[i], false, ((i == 2) || (i == 4)), 1);
        }
        // BidirectionalStream.write is not called because stream is not ready yet.
        Mockito.verify(cronetStream, Mockito.times(0)).write(ArgumentMatchers.isA(ByteBuffer.class), ArgumentMatchers.isA(Boolean.class));
        // Stream is ready.
        callback.onStreamReady(cronetStream);
        // 5 writes are called.
        Mockito.verify(cronetStream, Mockito.times(5)).write(ArgumentMatchers.isA(ByteBuffer.class), ArgumentMatchers.eq(false));
        ByteBuffer fakeBuffer = ByteBuffer.allocateDirect(8);
        fakeBuffer.position(8);
        Mockito.verify(cronetStream, Mockito.times(2)).flush();
        // 5 onWriteCompleted callbacks for previous writes.
        callback.onWriteCompleted(cronetStream, null, fakeBuffer, false);
        callback.onWriteCompleted(cronetStream, null, fakeBuffer, false);
        callback.onWriteCompleted(cronetStream, null, fakeBuffer, false);
        callback.onWriteCompleted(cronetStream, null, fakeBuffer, false);
        callback.onWriteCompleted(cronetStream, null, fakeBuffer, false);
        // All pending data has been sent. onWriteCompleted callback will not trigger any additional
        // write call.
        Mockito.verify(cronetStream, Mockito.times(5)).write(ArgumentMatchers.isA(ByteBuffer.class), ArgumentMatchers.eq(false));
        // Send end of stream. write will be immediately called since stream is ready.
        clientStream.abstractClientStreamSink().writeFrame(null, true, true, 1);
        Mockito.verify(cronetStream, Mockito.times(1)).write(ArgumentMatchers.isA(ByteBuffer.class), ArgumentMatchers.eq(true));
        Mockito.verify(cronetStream, Mockito.times(3)).flush();
    }

    @Test
    public void read() {
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), callbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        BidirectionalStream.Callback callback = callbackCaptor.getValue();
        // Read is not called until we receive the response header.
        Mockito.verify(cronetStream, Mockito.times(0)).read(ArgumentMatchers.isA(ByteBuffer.class));
        UrlResponseInfo info = new UrlResponseInfoImpl(new ArrayList<String>(), 200, "", CronetClientStreamTest.responseHeader("200"), false, "", "");
        callback.onResponseHeadersReceived(cronetStream, info);
        Mockito.verify(cronetStream, Mockito.times(1)).read(ArgumentMatchers.isA(ByteBuffer.class));
        ArgumentCaptor<Metadata> metadataCaptor = ArgumentCaptor.forClass(Metadata.class);
        Mockito.verify(clientListener).headersRead(metadataCaptor.capture());
        // Verify recevied headers.
        Metadata metadata = metadataCaptor.getValue();
        Assert.assertEquals("application/grpc", metadata.get(Key.of("content-type", ASCII_STRING_MARSHALLER)));
        Assert.assertEquals("test-value", metadata.get(Key.of("test-key", ASCII_STRING_MARSHALLER)));
        callback.onReadCompleted(cronetStream, info, ((ByteBuffer) (CronetClientStreamTest.createMessageFrame(new String("response1").getBytes(forName("UTF-8"))))), false);
        // Haven't request any message, so no callback is called here.
        Mockito.verify(clientListener, Mockito.times(0)).messagesAvailable(ArgumentMatchers.isA(MessageProducer.class));
        Mockito.verify(cronetStream, Mockito.times(1)).read(ArgumentMatchers.isA(ByteBuffer.class));
        // Request one message
        clientStream.request(1);
        Mockito.verify(clientListener, Mockito.times(1)).messagesAvailable(ArgumentMatchers.isA(MessageProducer.class));
        Mockito.verify(cronetStream, Mockito.times(2)).read(ArgumentMatchers.isA(ByteBuffer.class));
        // BidirectionalStream.read will not be called again after receiving endOfStream(empty buffer).
        clientStream.request(1);
        callback.onReadCompleted(cronetStream, info, ByteBuffer.allocate(0), true);
        Mockito.verify(clientListener, Mockito.times(1)).messagesAvailable(ArgumentMatchers.isA(MessageProducer.class));
        Mockito.verify(cronetStream, Mockito.times(2)).read(ArgumentMatchers.isA(ByteBuffer.class));
    }

    @Test
    public void streamSucceeded() {
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), callbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        BidirectionalStream.Callback callback = callbackCaptor.getValue();
        callback.onStreamReady(cronetStream);
        Mockito.verify(cronetStream, Mockito.times(0)).write(ArgumentMatchers.isA(ByteBuffer.class), ArgumentMatchers.isA(Boolean.class));
        // Send the first data frame.
        CronetWritableBufferAllocator allocator = new CronetWritableBufferAllocator();
        String request = new String("request");
        WritableBuffer writableBuffer = allocator.allocate(request.length());
        writableBuffer.write(request.getBytes(forName("UTF-8")), 0, request.length());
        clientStream.abstractClientStreamSink().writeFrame(writableBuffer, false, true, 1);
        ArgumentCaptor<ByteBuffer> bufferCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.verify(cronetStream, Mockito.times(1)).write(bufferCaptor.capture(), ArgumentMatchers.isA(Boolean.class));
        ByteBuffer buffer = bufferCaptor.getValue();
        buffer.position(request.length());
        Mockito.verify(cronetStream, Mockito.times(1)).flush();
        // Receive response header
        clientStream.request(2);
        UrlResponseInfo info = new UrlResponseInfoImpl(new ArrayList<String>(), 200, "", CronetClientStreamTest.responseHeader("200"), false, "", "");
        callback.onResponseHeadersReceived(cronetStream, info);
        Mockito.verify(cronetStream, Mockito.times(1)).read(ArgumentMatchers.isA(ByteBuffer.class));
        // Receive one message
        callback.onReadCompleted(cronetStream, info, ((ByteBuffer) (CronetClientStreamTest.createMessageFrame(new String("response").getBytes(forName("UTF-8"))))), false);
        Mockito.verify(clientListener, Mockito.times(1)).messagesAvailable(ArgumentMatchers.isA(MessageProducer.class));
        Mockito.verify(cronetStream, Mockito.times(2)).read(ArgumentMatchers.isA(ByteBuffer.class));
        // Send endOfStream
        callback.onWriteCompleted(cronetStream, null, buffer, false);
        clientStream.abstractClientStreamSink().writeFrame(null, true, true, 1);
        Mockito.verify(cronetStream, Mockito.times(2)).write(ArgumentMatchers.isA(ByteBuffer.class), ArgumentMatchers.isA(Boolean.class));
        Mockito.verify(cronetStream, Mockito.times(2)).flush();
        // Receive trailer
        processTrailers(CronetClientStreamTest.trailers(0));
        callback.onSucceeded(cronetStream, info);
        // Verify trailer
        ArgumentCaptor<Metadata> trailerCaptor = ArgumentCaptor.forClass(Metadata.class);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(clientListener).closed(statusCaptor.capture(), ArgumentMatchers.isA(RpcProgress.class), trailerCaptor.capture());
        // Verify recevied headers.
        Metadata trailers = trailerCaptor.getValue();
        Status status = statusCaptor.getValue();
        Assert.assertEquals("test-trailer-value", trailers.get(Key.of("test-trailer-key", ASCII_STRING_MARSHALLER)));
        Assert.assertEquals("application/grpc", trailers.get(Key.of("content-type", ASCII_STRING_MARSHALLER)));
        Assert.assertTrue(status.isOk());
    }

    @Test
    public void streamSucceededWithGrpcError() {
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), callbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        BidirectionalStream.Callback callback = callbackCaptor.getValue();
        callback.onStreamReady(cronetStream);
        Mockito.verify(cronetStream, Mockito.times(0)).write(ArgumentMatchers.isA(ByteBuffer.class), ArgumentMatchers.isA(Boolean.class));
        clientStream.abstractClientStreamSink().writeFrame(null, true, true, 1);
        Mockito.verify(cronetStream, Mockito.times(1)).write(ArgumentMatchers.isA(ByteBuffer.class), ArgumentMatchers.isA(Boolean.class));
        Mockito.verify(cronetStream, Mockito.times(1)).flush();
        // Receive response header
        clientStream.request(2);
        UrlResponseInfo info = new UrlResponseInfoImpl(new ArrayList<String>(), 200, "", CronetClientStreamTest.responseHeader("200"), false, "", "");
        callback.onResponseHeadersReceived(cronetStream, info);
        Mockito.verify(cronetStream, Mockito.times(1)).read(ArgumentMatchers.isA(ByteBuffer.class));
        // Receive trailer
        callback.onReadCompleted(cronetStream, null, ByteBuffer.allocate(0), true);
        ((CronetClientStream.BidirectionalStreamCallback) (callback)).processTrailers(CronetClientStreamTest.trailers(PERMISSION_DENIED.getCode().value()));
        callback.onSucceeded(cronetStream, info);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(clientListener).closed(statusCaptor.capture(), ArgumentMatchers.isA(RpcProgress.class), ArgumentMatchers.isA(Metadata.class));
        // Verify error status.
        Status status = statusCaptor.getValue();
        Assert.assertFalse(status.isOk());
        Assert.assertEquals(PERMISSION_DENIED.getCode(), status.getCode());
    }

    @Test
    public void streamFailed() {
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), callbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        BidirectionalStream.Callback callback = callbackCaptor.getValue();
        // Nothing happens and stream fails
        CronetException exception = Mockito.mock(CronetException.class);
        callback.onFailed(cronetStream, null, exception);
        Mockito.verify(transport).finishStream(ArgumentMatchers.eq(clientStream), ArgumentMatchers.isA(Status.class));
        // finishStream calls transportReportStatus.
        clientStream.transportState().transportReportStatus(UNAVAILABLE, false, new Metadata());
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(clientListener).closed(statusCaptor.capture(), ArgumentMatchers.isA(RpcProgress.class), ArgumentMatchers.isA(Metadata.class));
        Status status = statusCaptor.getValue();
        Assert.assertEquals(UNAVAILABLE.getCode(), status.getCode());
    }

    @Test
    public void streamFailedAfterResponseHeaderReceived() {
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), callbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        BidirectionalStream.Callback callback = callbackCaptor.getValue();
        // Receive response header
        UrlResponseInfo info = new UrlResponseInfoImpl(new ArrayList<String>(), 200, "", CronetClientStreamTest.responseHeader("200"), false, "", "");
        callback.onResponseHeadersReceived(cronetStream, info);
        CronetException exception = Mockito.mock(CronetException.class);
        callback.onFailed(cronetStream, info, exception);
        Mockito.verify(transport).finishStream(ArgumentMatchers.eq(clientStream), ArgumentMatchers.isA(Status.class));
        // finishStream calls transportReportStatus.
        clientStream.transportState().transportReportStatus(UNAVAILABLE, false, new Metadata());
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(clientListener).closed(statusCaptor.capture(), ArgumentMatchers.isA(RpcProgress.class), ArgumentMatchers.isA(Metadata.class));
        Status status = statusCaptor.getValue();
        Assert.assertEquals(UNAVAILABLE.getCode(), status.getCode());
    }

    @Test
    public void streamFailedAfterTrailerReceived() {
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), callbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        BidirectionalStream.Callback callback = callbackCaptor.getValue();
        // Receive response header
        UrlResponseInfo info = new UrlResponseInfoImpl(new ArrayList<String>(), 200, "", CronetClientStreamTest.responseHeader("200"), false, "", "");
        callback.onResponseHeadersReceived(cronetStream, info);
        // Report trailer but not endOfStream.
        processTrailers(CronetClientStreamTest.trailers(0));
        CronetException exception = Mockito.mock(CronetException.class);
        callback.onFailed(cronetStream, info, exception);
        Mockito.verify(transport).finishStream(ArgumentMatchers.eq(clientStream), ArgumentMatchers.isA(Status.class));
        // finishStream calls transportReportStatus.
        clientStream.transportState().transportReportStatus(UNAVAILABLE, false, new Metadata());
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(clientListener).closed(statusCaptor.capture(), ArgumentMatchers.isA(RpcProgress.class), ArgumentMatchers.isA(Metadata.class));
        Status status = statusCaptor.getValue();
        // Stream has already finished so OK status should be reported.
        Assert.assertEquals(UNAVAILABLE.getCode(), status.getCode());
    }

    @Test
    public void streamFailedAfterTrailerAndEndOfStreamReceived() {
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), callbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        BidirectionalStream.Callback callback = callbackCaptor.getValue();
        // Receive response header
        UrlResponseInfo info = new UrlResponseInfoImpl(new ArrayList<String>(), 200, "", CronetClientStreamTest.responseHeader("200"), false, "", "");
        callback.onResponseHeadersReceived(cronetStream, info);
        // Report trailer and endOfStream
        callback.onReadCompleted(cronetStream, null, ByteBuffer.allocate(0), true);
        processTrailers(CronetClientStreamTest.trailers(0));
        CronetException exception = Mockito.mock(CronetException.class);
        callback.onFailed(cronetStream, info, exception);
        Mockito.verify(transport).finishStream(ArgumentMatchers.eq(clientStream), ArgumentMatchers.isA(Status.class));
        // finishStream calls transportReportStatus.
        clientStream.transportState().transportReportStatus(UNAVAILABLE, false, new Metadata());
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(clientListener).closed(statusCaptor.capture(), ArgumentMatchers.isA(RpcProgress.class), ArgumentMatchers.isA(Metadata.class));
        Status status = statusCaptor.getValue();
        // Stream has already finished so OK status should be reported.
        Assert.assertEquals(OK.getCode(), status.getCode());
    }

    @Test
    public void cancelStream() {
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), callbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        BidirectionalStream.Callback callback = callbackCaptor.getValue();
        // Cancel the stream
        clientStream.cancel(DEADLINE_EXCEEDED);
        Mockito.verify(transport, Mockito.times(0)).finishStream(ArgumentMatchers.eq(clientStream), ArgumentMatchers.isA(Status.class));
        callback.onCanceled(cronetStream, null);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(transport, Mockito.times(1)).finishStream(ArgumentMatchers.eq(clientStream), statusCaptor.capture());
        Status status = statusCaptor.getValue();
        Assert.assertEquals(DEADLINE_EXCEEDED.getCode(), status.getCode());
    }

    @Test
    public void reportTrailersWhenTrailersReceivedBeforeReadClosed() {
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), callbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        BidirectionalStream.Callback callback = callbackCaptor.getValue();
        callback.onStreamReady(cronetStream);
        UrlResponseInfo info = new UrlResponseInfoImpl(new ArrayList<String>(), 200, "", CronetClientStreamTest.responseHeader("200"), false, "", "");
        callback.onResponseHeadersReceived(cronetStream, info);
        // Receive trailer first
        ((CronetClientStream.BidirectionalStreamCallback) (callback)).processTrailers(CronetClientStreamTest.trailers(UNAUTHENTICATED.getCode().value()));
        Mockito.verify(clientListener, Mockito.times(0)).closed(ArgumentMatchers.isA(Status.class), ArgumentMatchers.isA(RpcProgress.class), ArgumentMatchers.isA(Metadata.class));
        // Receive cronet's endOfStream
        callback.onReadCompleted(cronetStream, null, ByteBuffer.allocate(0), true);
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(clientListener, Mockito.times(1)).closed(statusCaptor.capture(), ArgumentMatchers.isA(RpcProgress.class), ArgumentMatchers.isA(Metadata.class));
        Status status = statusCaptor.getValue();
        Assert.assertEquals(UNAUTHENTICATED.getCode(), status.getCode());
    }

    @Test
    public void reportTrailersWhenTrailersReceivedAfterReadClosed() {
        ArgumentCaptor<BidirectionalStream.Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        Mockito.verify(factory).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), callbackCaptor.capture(), ArgumentMatchers.isA(Executor.class));
        BidirectionalStream.Callback callback = callbackCaptor.getValue();
        callback.onStreamReady(cronetStream);
        UrlResponseInfo info = new UrlResponseInfoImpl(new ArrayList<String>(), 200, "", CronetClientStreamTest.responseHeader("200"), false, "", "");
        callback.onResponseHeadersReceived(cronetStream, info);
        // Receive cronet's endOfStream
        callback.onReadCompleted(cronetStream, null, ByteBuffer.allocate(0), true);
        Mockito.verify(clientListener, Mockito.times(0)).closed(ArgumentMatchers.isA(Status.class), ArgumentMatchers.isA(RpcProgress.class), ArgumentMatchers.isA(Metadata.class));
        // Receive trailer
        ((CronetClientStream.BidirectionalStreamCallback) (callback)).processTrailers(CronetClientStreamTest.trailers(UNAUTHENTICATED.getCode().value()));
        ArgumentCaptor<Status> statusCaptor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(clientListener, Mockito.times(1)).closed(statusCaptor.capture(), ArgumentMatchers.isA(RpcProgress.class), ArgumentMatchers.isA(Metadata.class));
        Status status = statusCaptor.getValue();
        Assert.assertEquals(UNAUTHENTICATED.getCode(), status.getCode());
    }

    @Test
    public void addCronetRequestAnnotation_deprecated() {
        Object annotation = new Object();
        CronetClientStreamTest.SetStreamFactoryRunnable callback = new CronetClientStreamTest.SetStreamFactoryRunnable(factory);
        CronetClientStream stream = /* alwaysUsePut */
        new CronetClientStream("https://www.google.com:443", "cronet", executor, metadata, transport, callback, lock, 100, false, method, StatsTraceContext.NOOP, DEFAULT.withOption(CRONET_ANNOTATION_KEY, annotation), transportTracer);
        callback.setStream(stream);
        Mockito.when(factory.newBidirectionalStreamBuilder(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Callback.class), ArgumentMatchers.any(Executor.class))).thenReturn(builder);
        stream.start(clientListener);
        // addRequestAnnotation should be called since we add the option CRONET_ANNOTATION_KEY above.
        Mockito.verify(builder).addRequestAnnotation(annotation);
    }

    @Test
    public void withAnnotation() {
        Object annotation1 = new Object();
        Object annotation2 = new Object();
        CallOptions callOptions = CronetCallOptions.withAnnotation(DEFAULT, annotation1);
        callOptions = CronetCallOptions.withAnnotation(callOptions, annotation2);
        CronetClientStreamTest.SetStreamFactoryRunnable callback = new CronetClientStreamTest.SetStreamFactoryRunnable(factory);
        CronetClientStream stream = /* alwaysUsePut */
        new CronetClientStream("https://www.google.com:443", "cronet", executor, metadata, transport, callback, lock, 100, false, method, StatsTraceContext.NOOP, callOptions, transportTracer);
        callback.setStream(stream);
        Mockito.when(factory.newBidirectionalStreamBuilder(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Callback.class), ArgumentMatchers.any(Executor.class))).thenReturn(builder);
        stream.start(clientListener);
        Mockito.verify(builder).addRequestAnnotation(annotation1);
        Mockito.verify(builder).addRequestAnnotation(annotation2);
    }

    @Test
    public void getUnaryRequest() {
        StreamBuilderFactory getFactory = Mockito.mock(StreamBuilderFactory.class);
        MethodDescriptor<?, ?> getMethod = MethodDescriptor.<Void, Void>newBuilder().setType(UNARY).setFullMethodName("/service/method").setIdempotent(true).setSafe(true).setRequestMarshaller(marshaller).setResponseMarshaller(marshaller).build();
        CronetClientStreamTest.SetStreamFactoryRunnable callback = new CronetClientStreamTest.SetStreamFactoryRunnable(getFactory);
        CronetClientStream stream = /* alwaysUsePut */
        new CronetClientStream("https://www.google.com/service/method", "cronet", executor, metadata, transport, callback, lock, 100, false, getMethod, StatsTraceContext.NOOP, CallOptions.DEFAULT, transportTracer);
        callback.setStream(stream);
        ExperimentalBidirectionalStream.Builder getBuilder = Mockito.mock(Builder.class);
        Mockito.when(getFactory.newBidirectionalStreamBuilder(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Callback.class), ArgumentMatchers.any(Executor.class))).thenReturn(getBuilder);
        Mockito.when(getBuilder.build()).thenReturn(cronetStream);
        stream.start(clientListener);
        // We will not create BidirectionalStream until we have the full request.
        Mockito.verify(getFactory, Mockito.times(0)).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(Callback.class), ArgumentMatchers.isA(Executor.class));
        byte[] msg = "request".getBytes(forName("UTF-8"));
        stream.writeMessage(new ByteArrayInputStream(msg));
        // We still haven't built the stream or sent anything.
        Mockito.verify(cronetStream, Mockito.times(0)).write(ArgumentMatchers.isA(ByteBuffer.class), ArgumentMatchers.isA(Boolean.class));
        Mockito.verify(getFactory, Mockito.times(0)).newBidirectionalStreamBuilder(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(Callback.class), ArgumentMatchers.isA(Executor.class));
        // halfClose will trigger sending.
        stream.halfClose();
        // Stream should be built with request payload in the header.
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(getFactory).newBidirectionalStreamBuilder(urlCaptor.capture(), ArgumentMatchers.isA(Callback.class), ArgumentMatchers.isA(Executor.class));
        Mockito.verify(getBuilder).setHttpMethod("GET");
        Assert.assertEquals(("https://www.google.com/service/method?" + (BaseEncoding.base64().encode(msg))), urlCaptor.getValue());
    }

    @Test
    public void idempotentMethod_usesHttpPut() {
        CronetClientStreamTest.SetStreamFactoryRunnable callback = new CronetClientStreamTest.SetStreamFactoryRunnable(factory);
        MethodDescriptor<?, ?> idempotentMethod = method.toBuilder().setIdempotent(true).build();
        CronetClientStream stream = /* alwaysUsePut */
        new CronetClientStream("https://www.google.com:443", "cronet", executor, metadata, transport, callback, lock, 100, false, idempotentMethod, StatsTraceContext.NOOP, CallOptions.DEFAULT, transportTracer);
        callback.setStream(stream);
        ExperimentalBidirectionalStream.Builder builder = Mockito.mock(Builder.class);
        Mockito.when(factory.newBidirectionalStreamBuilder(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Callback.class), ArgumentMatchers.any(Executor.class))).thenReturn(builder);
        Mockito.when(builder.build()).thenReturn(cronetStream);
        stream.start(clientListener);
        Mockito.verify(builder).setHttpMethod("PUT");
    }

    @Test
    public void alwaysUsePutOption_usesHttpPut() {
        CronetClientStreamTest.SetStreamFactoryRunnable callback = new CronetClientStreamTest.SetStreamFactoryRunnable(factory);
        CronetClientStream stream = /* alwaysUsePut */
        new CronetClientStream("https://www.google.com:443", "cronet", executor, metadata, transport, callback, lock, 100, true, method, StatsTraceContext.NOOP, CallOptions.DEFAULT, transportTracer);
        callback.setStream(stream);
        ExperimentalBidirectionalStream.Builder builder = Mockito.mock(Builder.class);
        Mockito.when(factory.newBidirectionalStreamBuilder(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Callback.class), ArgumentMatchers.any(Executor.class))).thenReturn(builder);
        Mockito.when(builder.build()).thenReturn(cronetStream);
        stream.start(clientListener);
        Mockito.verify(builder).setHttpMethod("PUT");
    }

    @Test
    public void reservedHeadersStripped() {
        String userAgent = "cronet";
        Metadata headers = new Metadata();
        Metadata.Key<String> userKey = Key.of("user-key", ASCII_STRING_MARSHALLER);
        headers.put(CONTENT_TYPE_KEY, "to-be-removed");
        headers.put(USER_AGENT_KEY, "to-be-removed");
        headers.put(TE_HEADER, "to-be-removed");
        headers.put(userKey, "user-value");
        CronetClientStreamTest.SetStreamFactoryRunnable callback = new CronetClientStreamTest.SetStreamFactoryRunnable(factory);
        CronetClientStream stream = /* alwaysUsePut */
        new CronetClientStream("https://www.google.com:443", userAgent, executor, headers, transport, callback, lock, 100, false, method, StatsTraceContext.NOOP, CallOptions.DEFAULT, transportTracer);
        callback.setStream(stream);
        ExperimentalBidirectionalStream.Builder builder = Mockito.mock(Builder.class);
        Mockito.when(factory.newBidirectionalStreamBuilder(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Callback.class), ArgumentMatchers.any(Executor.class))).thenReturn(builder);
        Mockito.when(builder.build()).thenReturn(cronetStream);
        stream.start(clientListener);
        Mockito.verify(builder, Mockito.times(4)).addHeader(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));
        Mockito.verify(builder).addHeader(USER_AGENT_KEY.name(), userAgent);
        Mockito.verify(builder).addHeader(CONTENT_TYPE_KEY.name(), CONTENT_TYPE_GRPC);
        Mockito.verify(builder).addHeader("te", TE_TRAILERS);
        Mockito.verify(builder).addHeader(userKey.name(), "user-value");
    }
}

