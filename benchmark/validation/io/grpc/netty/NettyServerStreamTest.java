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


import Status.CANCELLED;
import Status.DEADLINE_EXCEEDED;
import Status.INTERNAL;
import Status.OK;
import Utils.CONTENT_TYPE_GRPC;
import Utils.CONTENT_TYPE_HEADER;
import Utils.STATUS_OK;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.internal.ServerStreamListener;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.util.AsciiString;
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
 * Unit tests for {@link NettyServerStream}.
 */
@RunWith(JUnit4.class)
public class NettyServerStreamTest extends NettyStreamTestBase<NettyServerStream> {
    @Mock
    protected ServerStreamListener serverListener;

    @Mock
    private NettyServerHandler handler;

    private Metadata trailers = new Metadata();

    private final Queue<InputStream> listenerMessageQueue = new LinkedList<>();

    @Test
    public void writeMessageShouldSendResponse() throws Exception {
        ListMultimap<CharSequence, CharSequence> expectedHeaders = ImmutableListMultimap.copyOf(new DefaultHttp2Headers().status(STATUS_OK).set(CONTENT_TYPE_HEADER, CONTENT_TYPE_GRPC));
        stream.writeHeaders(new Metadata());
        ArgumentCaptor<SendResponseHeadersCommand> sendHeadersCap = ArgumentCaptor.forClass(SendResponseHeadersCommand.class);
        Mockito.verify(writeQueue).enqueue(sendHeadersCap.capture(), ArgumentMatchers.eq(true));
        SendResponseHeadersCommand sendHeaders = sendHeadersCap.getValue();
        assertThat(sendHeaders.stream()).isSameAs(stream.transportState());
        assertThat(ImmutableListMultimap.copyOf(sendHeaders.headers())).containsExactlyEntriesIn(expectedHeaders);
        assertThat(sendHeaders.endOfStream()).isFalse();
        byte[] msg = smallMessage();
        stream.writeMessage(new ByteArrayInputStream(msg));
        stream.flush();
        Mockito.verify(writeQueue).enqueue(ArgumentMatchers.eq(new SendGrpcFrameCommand(stream.transportState(), NettyTestUtil.messageFrame(NettyStreamTestBase.MESSAGE), false)), ArgumentMatchers.eq(true));
    }

    @Test
    public void writeHeadersShouldSendHeaders() throws Exception {
        Metadata headers = new Metadata();
        ListMultimap<CharSequence, CharSequence> expectedHeaders = ImmutableListMultimap.copyOf(Utils.convertServerHeaders(headers));
        stream().writeHeaders(headers);
        ArgumentCaptor<SendResponseHeadersCommand> sendHeadersCap = ArgumentCaptor.forClass(SendResponseHeadersCommand.class);
        Mockito.verify(writeQueue).enqueue(sendHeadersCap.capture(), ArgumentMatchers.eq(true));
        SendResponseHeadersCommand sendHeaders = sendHeadersCap.getValue();
        assertThat(sendHeaders.stream()).isSameAs(stream.transportState());
        assertThat(ImmutableListMultimap.copyOf(sendHeaders.headers())).containsExactlyEntriesIn(expectedHeaders);
        assertThat(sendHeaders.endOfStream()).isFalse();
    }

    @Test
    public void closeBeforeClientHalfCloseShouldSucceed() throws Exception {
        ListMultimap<CharSequence, CharSequence> expectedHeaders = ImmutableListMultimap.copyOf(new DefaultHttp2Headers().status(new AsciiString("200")).set(new AsciiString("content-type"), new AsciiString("application/grpc")).set(new AsciiString("grpc-status"), new AsciiString("0")));
        stream().close(OK, new Metadata());
        ArgumentCaptor<SendResponseHeadersCommand> sendHeadersCap = ArgumentCaptor.forClass(SendResponseHeadersCommand.class);
        Mockito.verify(writeQueue).enqueue(sendHeadersCap.capture(), ArgumentMatchers.eq(true));
        SendResponseHeadersCommand sendHeaders = sendHeadersCap.getValue();
        assertThat(sendHeaders.stream()).isSameAs(stream.transportState());
        assertThat(ImmutableListMultimap.copyOf(sendHeaders.headers())).containsExactlyEntriesIn(expectedHeaders);
        assertThat(sendHeaders.endOfStream()).isTrue();
        Mockito.verifyZeroInteractions(serverListener);
        // Sending complete. Listener gets closed()
        stream().transportState().complete();
        Mockito.verify(serverListener).closed(OK);
        Assert.assertNull("no message expected", listenerMessageQueue.poll());
    }

    @Test
    public void closeWithErrorBeforeClientHalfCloseShouldSucceed() throws Exception {
        ListMultimap<CharSequence, CharSequence> expectedHeaders = ImmutableListMultimap.copyOf(new DefaultHttp2Headers().status(new AsciiString("200")).set(new AsciiString("content-type"), new AsciiString("application/grpc")).set(new AsciiString("grpc-status"), new AsciiString("1")));
        // Error is sent on wire and ends the stream
        stream().close(CANCELLED, trailers);
        ArgumentCaptor<SendResponseHeadersCommand> sendHeadersCap = ArgumentCaptor.forClass(SendResponseHeadersCommand.class);
        Mockito.verify(writeQueue).enqueue(sendHeadersCap.capture(), ArgumentMatchers.eq(true));
        SendResponseHeadersCommand sendHeaders = sendHeadersCap.getValue();
        assertThat(sendHeaders.stream()).isSameAs(stream.transportState());
        assertThat(ImmutableListMultimap.copyOf(sendHeaders.headers())).containsExactlyEntriesIn(expectedHeaders);
        assertThat(sendHeaders.endOfStream()).isTrue();
        Mockito.verifyZeroInteractions(serverListener);
        // Sending complete. Listener gets closed()
        stream().transportState().complete();
        Mockito.verify(serverListener).closed(OK);
        Assert.assertNull("no message expected", listenerMessageQueue.poll());
    }

    @Test
    public void closeAfterClientHalfCloseShouldSucceed() throws Exception {
        ListMultimap<CharSequence, CharSequence> expectedHeaders = ImmutableListMultimap.copyOf(new DefaultHttp2Headers().status(new AsciiString("200")).set(new AsciiString("content-type"), new AsciiString("application/grpc")).set(new AsciiString("grpc-status"), new AsciiString("0")));
        // Client half-closes. Listener gets halfClosed()
        stream().transportState().inboundDataReceived(new io.netty.buffer.EmptyByteBuf(UnpooledByteBufAllocator.DEFAULT), true);
        Mockito.verify(serverListener).halfClosed();
        // Server closes. Status sent
        stream().close(OK, trailers);
        Assert.assertNull("no message expected", listenerMessageQueue.poll());
        ArgumentCaptor<SendResponseHeadersCommand> cmdCap = ArgumentCaptor.forClass(SendResponseHeadersCommand.class);
        Mockito.verify(writeQueue).enqueue(cmdCap.capture(), ArgumentMatchers.eq(true));
        SendResponseHeadersCommand cmd = cmdCap.getValue();
        assertThat(cmd.stream()).isSameAs(stream.transportState());
        assertThat(ImmutableListMultimap.copyOf(cmd.headers())).containsExactlyEntriesIn(expectedHeaders);
        assertThat(cmd.endOfStream()).isTrue();
        // Sending and receiving complete. Listener gets closed()
        stream().transportState().complete();
        Mockito.verify(serverListener).closed(OK);
        Assert.assertNull("no message expected", listenerMessageQueue.poll());
    }

    @Test
    public void abortStreamAndNotSendStatus() throws Exception {
        Status status = INTERNAL.withCause(new Throwable());
        stream().transportState().transportReportStatus(status);
        Mockito.verify(serverListener).closed(ArgumentMatchers.same(status));
        Mockito.verify(channel, Mockito.never()).writeAndFlush(ArgumentMatchers.any(SendResponseHeadersCommand.class));
        Mockito.verify(channel, Mockito.never()).writeAndFlush(ArgumentMatchers.any(SendGrpcFrameCommand.class));
        Assert.assertNull("no message expected", listenerMessageQueue.poll());
    }

    @Test
    public void abortStreamAfterClientHalfCloseShouldCallClose() {
        Status status = INTERNAL.withCause(new Throwable());
        // Client half-closes. Listener gets halfClosed()
        stream().transportState().inboundDataReceived(new io.netty.buffer.EmptyByteBuf(UnpooledByteBufAllocator.DEFAULT), true);
        Mockito.verify(serverListener).halfClosed();
        // Abort from the transport layer
        stream().transportState().transportReportStatus(status);
        Mockito.verify(serverListener).closed(ArgumentMatchers.same(status));
        Assert.assertNull("no message expected", listenerMessageQueue.poll());
    }

    @Test
    public void emptyFramerShouldSendNoPayload() {
        ListMultimap<CharSequence, CharSequence> expectedHeaders = ImmutableListMultimap.copyOf(new DefaultHttp2Headers().status(new AsciiString("200")).set(new AsciiString("content-type"), new AsciiString("application/grpc")).set(new AsciiString("grpc-status"), new AsciiString("0")));
        ArgumentCaptor<SendResponseHeadersCommand> cmdCap = ArgumentCaptor.forClass(SendResponseHeadersCommand.class);
        stream().close(OK, new Metadata());
        Mockito.verify(writeQueue).enqueue(cmdCap.capture(), ArgumentMatchers.eq(true));
        SendResponseHeadersCommand cmd = cmdCap.getValue();
        assertThat(cmd.stream()).isSameAs(stream.transportState());
        assertThat(ImmutableListMultimap.copyOf(cmd.headers())).containsExactlyEntriesIn(expectedHeaders);
        assertThat(cmd.endOfStream()).isTrue();
    }

    @Test
    public void cancelStreamShouldSucceed() {
        stream().cancel(DEADLINE_EXCEEDED);
        Mockito.verify(writeQueue).enqueue(new CancelServerStreamCommand(stream().transportState(), Status.DEADLINE_EXCEEDED), true);
    }
}

