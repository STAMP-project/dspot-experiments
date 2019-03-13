/**
 * Copyright 2014 The gRPC Authors
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
package io.grpc.netty;


import GrpcUtil.USER_AGENT_KEY;
import Metadata.ASCII_STRING_MARSHALLER;
import Metadata.Key;
import MethodDescriptor.Marshaller;
import MethodDescriptor.MethodType.UNARY;
import StatsTraceContext.NOOP;
import Status.CANCELLED;
import Status.DEADLINE_EXCEEDED;
import Status.INTERNAL;
import Status.OK;
import Status.UNKNOWN;
import Utils.HTTP_GET_METHOD;
import Utils.USER_AGENT;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.io.BaseEncoding;
import io.grpc.CallOptions;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.internal.ClientStreamListener;
import io.grpc.internal.StatsTraceContext;
import io.grpc.internal.TransportTracer;
import io.grpc.netty.WriteQueue.QueuedCommand;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link NettyClientStream}.
 */
@RunWith(JUnit4.class)
public class NettyClientStreamTest extends NettyStreamTestBase<NettyClientStream> {
    @Mock
    protected ClientStreamListener listener;

    @Mock
    protected NettyClientHandler handler;

    @SuppressWarnings("unchecked")
    private MethodDescriptor.Marshaller<Void> marshaller = Mockito.mock(Marshaller.class);

    private final Queue<InputStream> listenerMessageQueue = new LinkedList<>();

    // Must be initialized before @Before, because it is used by createStream()
    private MethodDescriptor<?, ?> methodDescriptor = MethodDescriptor.<Void, Void>newBuilder().setType(UNARY).setFullMethodName("testService/test").setRequestMarshaller(marshaller).setResponseMarshaller(marshaller).build();

    private final TransportTracer transportTracer = new TransportTracer();

    @Test
    public void closeShouldSucceed() {
        // Force stream creation.
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        stream().halfClose();
        Mockito.verifyNoMoreInteractions(listener);
    }

    @Test
    public void cancelShouldSendCommand() {
        // Set stream id to indicate it has been created
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        stream().cancel(CANCELLED);
        ArgumentCaptor<CancelClientStreamCommand> commandCaptor = ArgumentCaptor.forClass(CancelClientStreamCommand.class);
        Mockito.verify(writeQueue).enqueue(commandCaptor.capture(), ArgumentMatchers.eq(true));
        Assert.assertEquals(commandCaptor.getValue().reason(), CANCELLED);
    }

    @Test
    public void deadlineExceededCancelShouldSendCommand() {
        // Set stream id to indicate it has been created
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        stream().cancel(DEADLINE_EXCEEDED);
        ArgumentCaptor<CancelClientStreamCommand> commandCaptor = ArgumentCaptor.forClass(CancelClientStreamCommand.class);
        Mockito.verify(writeQueue).enqueue(commandCaptor.capture(), ArgumentMatchers.eq(true));
        Assert.assertEquals(commandCaptor.getValue().reason(), DEADLINE_EXCEEDED);
    }

    @Test
    public void cancelShouldStillSendCommandIfStreamNotCreatedToCancelCreation() {
        stream().cancel(CANCELLED);
        Mockito.verify(writeQueue).enqueue(ArgumentMatchers.isA(CancelClientStreamCommand.class), ArgumentMatchers.eq(true));
    }

    @Test
    public void writeMessageShouldSendRequest() throws Exception {
        // Force stream creation.
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        byte[] msg = smallMessage();
        stream.writeMessage(new ByteArrayInputStream(msg));
        stream.flush();
        Mockito.verify(writeQueue).enqueue(ArgumentMatchers.eq(new SendGrpcFrameCommand(stream.transportState(), NettyTestUtil.messageFrame(NettyStreamTestBase.MESSAGE), false)), ArgumentMatchers.eq(true));
    }

    @Test
    public void writeMessageShouldSendRequestUnknownLength() throws Exception {
        // Force stream creation.
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        byte[] msg = smallMessage();
        stream.writeMessage(new BufferedInputStream(new ByteArrayInputStream(msg)));
        stream.flush();
        // Two writes occur, one for the GRPC frame header and the second with the payload
        // The framer reports the message count when the payload is completely written
        Mockito.verify(writeQueue).enqueue(ArgumentMatchers.eq(new SendGrpcFrameCommand(stream.transportState(), NettyTestUtil.messageFrame(NettyStreamTestBase.MESSAGE).slice(0, 5), false)), ArgumentMatchers.eq(false));
        Mockito.verify(writeQueue).enqueue(ArgumentMatchers.eq(new SendGrpcFrameCommand(stream.transportState(), NettyTestUtil.messageFrame(NettyStreamTestBase.MESSAGE).slice(5, 11), false)), ArgumentMatchers.eq(true));
    }

    @Test
    public void setStatusWithOkShouldCloseStream() {
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        stream().transportState().transportReportStatus(OK, true, new Metadata());
        Mockito.verify(listener).closed(ArgumentMatchers.same(OK), ArgumentMatchers.same(PROCESSED), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void setStatusWithErrorShouldCloseStream() {
        Status errorStatus = Status.INTERNAL;
        stream().transportState().transportReportStatus(errorStatus, true, new Metadata());
        Mockito.verify(listener).closed(ArgumentMatchers.eq(errorStatus), ArgumentMatchers.same(PROCESSED), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void setStatusWithOkShouldNotOverrideError() {
        Status errorStatus = Status.INTERNAL;
        stream().transportState().transportReportStatus(errorStatus, true, new Metadata());
        stream().transportState().transportReportStatus(OK, true, new Metadata());
        Mockito.verify(listener).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.same(PROCESSED), ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void setStatusWithErrorShouldNotOverridePreviousError() {
        Status errorStatus = Status.INTERNAL;
        stream().transportState().transportReportStatus(errorStatus, true, new Metadata());
        stream().transportState().transportReportStatus(Status.fromThrowable(new RuntimeException("fake")), true, new Metadata());
        Mockito.verify(listener).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.same(PROCESSED), ArgumentMatchers.any(Metadata.class));
    }

    @Override
    @Test
    public void inboundMessageShouldCallListener() throws Exception {
        // Receive headers first so that it's a valid GRPC response.
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        stream().transportState().transportHeadersReceived(grpcResponseHeaders(), false);
        super.inboundMessageShouldCallListener();
    }

    @Test
    public void inboundHeadersShouldCallListenerHeadersRead() throws Exception {
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        Http2Headers headers = grpcResponseHeaders();
        stream().transportState().transportHeadersReceived(headers, false);
        Mockito.verify(listener).headersRead(ArgumentMatchers.any(Metadata.class));
    }

    @Test
    public void inboundTrailersClosesCall() throws Exception {
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        stream().transportState().transportHeadersReceived(grpcResponseHeaders(), false);
        super.inboundMessageShouldCallListener();
        stream().transportState().transportHeadersReceived(grpcResponseTrailers(OK), true);
    }

    @Test
    public void inboundTrailersBeforeHalfCloseSendsRstStream() {
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        stream().transportState().transportHeadersReceived(grpcResponseHeaders(), false);
        stream().transportState().transportHeadersReceived(grpcResponseTrailers(OK), true);
        // Verify a cancel stream with reason=null is sent to the handler.
        ArgumentCaptor<CancelClientStreamCommand> captor = ArgumentCaptor.forClass(CancelClientStreamCommand.class);
        Mockito.verify(writeQueue).enqueue(captor.capture(), ArgumentMatchers.eq(true));
        Assert.assertNull(captor.getValue().reason());
    }

    @Test
    public void inboundTrailersAfterHalfCloseDoesNotSendRstStream() {
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        stream().transportState().transportHeadersReceived(grpcResponseHeaders(), false);
        stream.halfClose();
        stream().transportState().transportHeadersReceived(grpcResponseTrailers(OK), true);
        Mockito.verify(writeQueue, Mockito.never()).enqueue(ArgumentMatchers.isA(CancelClientStreamCommand.class), ArgumentMatchers.eq(true));
    }

    @Test
    public void inboundStatusShouldSetStatus() throws Exception {
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        // Receive headers first so that it's a valid GRPC response.
        stream().transportState().transportHeadersReceived(grpcResponseHeaders(), false);
        stream().transportState().transportHeadersReceived(grpcResponseTrailers(INTERNAL), true);
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(listener).closed(captor.capture(), ArgumentMatchers.same(PROCESSED), ArgumentMatchers.any(Metadata.class));
        Assert.assertEquals(INTERNAL.getCode(), captor.getValue().getCode());
    }

    @Test
    public void invalidInboundHeadersCancelStream() throws Exception {
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        Http2Headers headers = grpcResponseHeaders();
        headers.set("random", "4");
        headers.remove(Utils.CONTENT_TYPE_HEADER);
        // Remove once b/16290036 is fixed.
        headers.status(new AsciiString("500"));
        stream().transportState().transportHeadersReceived(headers, false);
        Mockito.verify(listener, Mockito.never()).closed(ArgumentMatchers.any(Status.class), ArgumentMatchers.any(Metadata.class));
        // We are now waiting for 100 bytes of error context on the stream, cancel has not yet been
        // sent
        Mockito.verify(channel, Mockito.never()).writeAndFlush(ArgumentMatchers.any(CancelClientStreamCommand.class));
        stream().transportState().transportDataReceived(Unpooled.buffer(100).writeZero(100), false);
        Mockito.verify(channel, Mockito.never()).writeAndFlush(ArgumentMatchers.any(CancelClientStreamCommand.class));
        stream().transportState().transportDataReceived(Unpooled.buffer(1000).writeZero(1000), false);
        // Now verify that cancel is sent and an error is reported to the listener
        Mockito.verify(writeQueue).enqueue(ArgumentMatchers.isA(CancelClientStreamCommand.class), ArgumentMatchers.eq(true));
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        ArgumentCaptor<Metadata> metadataCaptor = ArgumentCaptor.forClass(Metadata.class);
        Mockito.verify(listener).closed(captor.capture(), ArgumentMatchers.same(PROCESSED), metadataCaptor.capture());
        Assert.assertEquals(UNKNOWN.getCode(), captor.getValue().getCode());
        Assert.assertEquals("4", metadataCaptor.getValue().get(Key.of("random", ASCII_STRING_MARSHALLER)));
    }

    @Test
    public void invalidInboundContentTypeShouldCancelStream() {
        // Set stream id to indicate it has been created
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        Http2Headers headers = new DefaultHttp2Headers().status(Utils.STATUS_OK).set(Utils.CONTENT_TYPE_HEADER, new AsciiString("application/bad", CharsetUtil.UTF_8));
        stream().transportState().transportHeadersReceived(headers, false);
        Http2Headers trailers = new DefaultHttp2Headers().set(new AsciiString("grpc-status", CharsetUtil.UTF_8), new AsciiString("0", CharsetUtil.UTF_8));
        stream().transportState().transportHeadersReceived(trailers, true);
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        ArgumentCaptor<Metadata> metadataCaptor = ArgumentCaptor.forClass(Metadata.class);
        Mockito.verify(listener).closed(captor.capture(), ArgumentMatchers.same(PROCESSED), metadataCaptor.capture());
        Status status = captor.getValue();
        Assert.assertEquals(Status.Code.UNKNOWN, status.getCode());
        Assert.assertTrue(status.getDescription().contains("content-type"));
        Assert.assertEquals("application/bad", metadataCaptor.getValue().get(Key.of("Content-Type", ASCII_STRING_MARSHALLER)));
    }

    @Test
    public void nonGrpcResponseShouldSetStatus() throws Exception {
        stream().transportState().transportDataReceived(Unpooled.copiedBuffer(NettyStreamTestBase.MESSAGE, CharsetUtil.UTF_8), true);
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(listener).closed(captor.capture(), ArgumentMatchers.same(PROCESSED), ArgumentMatchers.any(Metadata.class));
        Assert.assertEquals(Status.Code.INTERNAL, captor.getValue().getCode());
    }

    @Test
    public void deframedDataAfterCancelShouldBeIgnored() throws Exception {
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        // Receive headers first so that it's a valid GRPC response.
        stream().transportState().transportHeadersReceived(grpcResponseHeaders(), false);
        // Receive 2 consecutive empty frames. Only one is delivered at a time to the listener.
        stream().transportState().transportDataReceived(simpleGrpcFrame(), false);
        stream().transportState().transportDataReceived(simpleGrpcFrame(), false);
        // Only allow the first to be delivered.
        stream().request(1);
        // Receive error trailers. The server status will not be processed until after all of the
        // data frames have been processed. Since cancellation will interrupt message delivery,
        // this status will never be processed and the listener will instead only see the
        // cancellation.
        stream().transportState().transportHeadersReceived(grpcResponseTrailers(INTERNAL), true);
        // Verify that the first was delivered.
        Assert.assertNotNull("message expected", listenerMessageQueue.poll());
        Assert.assertNull("no additional message expected", listenerMessageQueue.poll());
        // Now set the error status.
        Metadata trailers = Utils.convertTrailers(grpcResponseTrailers(CANCELLED));
        stream().transportState().transportReportStatus(CANCELLED, true, trailers);
        // Now allow the delivery of the second.
        stream().request(1);
        // Verify that the listener was only notified of the first message, not the second.
        Assert.assertNull("no additional message expected", listenerMessageQueue.poll());
        Mockito.verify(listener).closed(ArgumentMatchers.eq(CANCELLED), ArgumentMatchers.same(PROCESSED), ArgumentMatchers.eq(trailers));
    }

    @Test
    public void dataFrameWithEosShouldDeframeAndThenFail() {
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        stream().request(1);
        // Receive headers first so that it's a valid GRPC response.
        stream().transportState().transportHeadersReceived(grpcResponseHeaders(), false);
        // Receive a DATA frame with EOS set.
        stream().transportState().transportDataReceived(simpleGrpcFrame(), true);
        // Verify that the message was delivered.
        Assert.assertNotNull("message expected", listenerMessageQueue.poll());
        Assert.assertNull("no additional message expected", listenerMessageQueue.poll());
        ArgumentCaptor<Status> captor = ArgumentCaptor.forClass(Status.class);
        Mockito.verify(listener).closed(captor.capture(), ArgumentMatchers.same(PROCESSED), ArgumentMatchers.any(Metadata.class));
        Assert.assertEquals(Status.Code.INTERNAL, captor.getValue().getCode());
    }

    @Test
    public void setHttp2StreamShouldNotifyReady() {
        listener = Mockito.mock(ClientStreamListener.class);
        stream = new NettyClientStream(new NettyClientStreamTest.TransportStateImpl(handler, DEFAULT_MAX_MESSAGE_SIZE), methodDescriptor, new Metadata(), channel, AsciiString.of("localhost"), AsciiString.of("http"), AsciiString.of("agent"), StatsTraceContext.NOOP, transportTracer, CallOptions.DEFAULT);
        stream.start(listener);
        stream().transportState().setId(NettyStreamTestBase.STREAM_ID);
        Mockito.verify(listener, Mockito.never()).onReady();
        Assert.assertFalse(stream.isReady());
        stream().transportState().setHttp2Stream(http2Stream);
        Mockito.verify(listener).onReady();
        Assert.assertTrue(stream.isReady());
    }

    @Test
    public void removeUserAgentFromApplicationHeaders() {
        Metadata metadata = new Metadata();
        metadata.put(USER_AGENT_KEY, "bad agent");
        listener = Mockito.mock(ClientStreamListener.class);
        Mockito.reset(writeQueue);
        ChannelPromise completedPromise = setSuccess();
        Mockito.when(writeQueue.enqueue(ArgumentMatchers.any(QueuedCommand.class), ArgumentMatchers.anyBoolean())).thenReturn(completedPromise);
        stream = new NettyClientStream(new NettyClientStreamTest.TransportStateImpl(handler, DEFAULT_MAX_MESSAGE_SIZE), methodDescriptor, new Metadata(), channel, AsciiString.of("localhost"), AsciiString.of("http"), AsciiString.of("good agent"), StatsTraceContext.NOOP, transportTracer, CallOptions.DEFAULT);
        stream.start(listener);
        ArgumentCaptor<CreateStreamCommand> cmdCap = ArgumentCaptor.forClass(CreateStreamCommand.class);
        Mockito.verify(writeQueue).enqueue(cmdCap.capture(), ArgumentMatchers.eq(false));
        assertThat(ImmutableListMultimap.copyOf(cmdCap.getValue().headers())).containsEntry(USER_AGENT, AsciiString.of("good agent"));
    }

    @Test
    public void getRequestSentThroughHeader() {
        // Creating a GET method
        MethodDescriptor<?, ?> descriptor = MethodDescriptor.<Void, Void>newBuilder().setType(UNARY).setFullMethodName("testService/test").setRequestMarshaller(marshaller).setResponseMarshaller(marshaller).setIdempotent(true).setSafe(true).build();
        NettyClientStream stream = new NettyClientStream(new NettyClientStreamTest.TransportStateImpl(handler, DEFAULT_MAX_MESSAGE_SIZE), descriptor, new Metadata(), channel, AsciiString.of("localhost"), AsciiString.of("http"), AsciiString.of("agent"), StatsTraceContext.NOOP, transportTracer, CallOptions.DEFAULT);
        stream.start(listener);
        stream.transportState().setId(NettyStreamTestBase.STREAM_ID);
        stream.transportState().setHttp2Stream(http2Stream);
        byte[] msg = smallMessage();
        stream.writeMessage(new ByteArrayInputStream(msg));
        stream.flush();
        stream.halfClose();
        ArgumentCaptor<CreateStreamCommand> cmdCap = ArgumentCaptor.forClass(CreateStreamCommand.class);
        Mockito.verify(writeQueue).enqueue(cmdCap.capture(), ArgumentMatchers.eq(true));
        ImmutableListMultimap<CharSequence, CharSequence> headers = ImmutableListMultimap.copyOf(cmdCap.getValue().headers());
        assertThat(headers).containsEntry(AsciiString.of(":method"), HTTP_GET_METHOD);
        assertThat(headers).containsEntry(AsciiString.of(":path"), AsciiString.of(("/testService/test?" + (BaseEncoding.base64().encode(msg)))));
    }

    private class TransportStateImpl extends NettyClientStream.TransportState {
        public TransportStateImpl(NettyClientHandler handler, int maxMessageSize) {
            super(handler, channel.eventLoop(), maxMessageSize, NOOP, transportTracer);
        }

        @Override
        protected Status statusFromFailedFuture(ChannelFuture f) {
            return Utils.statusFromThrowable(f.cause());
        }
    }
}

