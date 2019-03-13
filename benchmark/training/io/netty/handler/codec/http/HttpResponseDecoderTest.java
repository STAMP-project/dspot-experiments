/**
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http;


import CharsetUtil.US_ASCII;
import HttpHeaderNames.TRAILER;
import HttpHeaderNames.TRANSFER_ENCODING;
import HttpResponseStatus.OK;
import HttpResponseStatus.RESET_CONTENT;
import HttpResponseStatus.SWITCHING_PROTOCOLS;
import HttpVersion.HTTP_1_0;
import HttpVersion.HTTP_1_1;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.TooLongFrameException;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class HttpResponseDecoderTest {
    /**
     * The size of headers should be calculated correctly even if a single header is split into multiple fragments.
     *
     * @see <a href="https://github.com/netty/netty/issues/3445">#3445</a>
     */
    @Test
    public void testMaxHeaderSize1() {
        final int maxHeaderSize = 8192;
        final EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder(4096, maxHeaderSize, 8192));
        final char[] bytes = new char[(maxHeaderSize / 2) - 2];
        Arrays.fill(bytes, 'a');
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.1 200 OK\r\n", US_ASCII));
        // Write two 4096-byte headers (= 8192 bytes)
        ch.writeInbound(Unpooled.copiedBuffer("A:", US_ASCII));
        ch.writeInbound(Unpooled.copiedBuffer(bytes, US_ASCII));
        ch.writeInbound(Unpooled.copiedBuffer("\r\n", US_ASCII));
        Assert.assertNull(ch.readInbound());
        ch.writeInbound(Unpooled.copiedBuffer("B:", US_ASCII));
        ch.writeInbound(Unpooled.copiedBuffer(bytes, US_ASCII));
        ch.writeInbound(Unpooled.copiedBuffer("\r\n", US_ASCII));
        ch.writeInbound(Unpooled.copiedBuffer("\r\n", US_ASCII));
        HttpResponse res = ch.readInbound();
        Assert.assertNull(res.decoderResult().cause());
        Assert.assertTrue(res.decoderResult().isSuccess());
        Assert.assertNull(ch.readInbound());
        Assert.assertTrue(ch.finish());
        Assert.assertThat(ch.readInbound(), CoreMatchers.instanceOf(LastHttpContent.class));
    }

    /**
     * Complementary test case of {@link #testMaxHeaderSize1()} When it actually exceeds the maximum, it should fail.
     */
    @Test
    public void testMaxHeaderSize2() {
        final int maxHeaderSize = 8192;
        final EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder(4096, maxHeaderSize, 8192));
        final char[] bytes = new char[(maxHeaderSize / 2) - 2];
        Arrays.fill(bytes, 'a');
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.1 200 OK\r\n", US_ASCII));
        // Write a 4096-byte header and a 4097-byte header to test an off-by-one case (= 8193 bytes)
        ch.writeInbound(Unpooled.copiedBuffer("A:", US_ASCII));
        ch.writeInbound(Unpooled.copiedBuffer(bytes, US_ASCII));
        ch.writeInbound(Unpooled.copiedBuffer("\r\n", US_ASCII));
        Assert.assertNull(ch.readInbound());
        ch.writeInbound(Unpooled.copiedBuffer("B: ", US_ASCII));// Note an extra space.

        ch.writeInbound(Unpooled.copiedBuffer(bytes, US_ASCII));
        ch.writeInbound(Unpooled.copiedBuffer("\r\n", US_ASCII));
        ch.writeInbound(Unpooled.copiedBuffer("\r\n", US_ASCII));
        HttpResponse res = ch.readInbound();
        Assert.assertTrue(((res.decoderResult().cause()) instanceof TooLongFrameException));
        Assert.assertFalse(ch.finish());
        Assert.assertNull(ch.readInbound());
    }

    @Test
    public void testResponseChunked() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.1 200 OK\r\nTransfer-Encoding: chunked\r\n\r\n", US_ASCII));
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(OK));
        byte[] data = new byte[64];
        for (int i = 0; i < (data.length); i++) {
            data[i] = ((byte) (i));
        }
        for (int i = 0; i < 10; i++) {
            Assert.assertFalse(ch.writeInbound(Unpooled.copiedBuffer(((Integer.toHexString(data.length)) + "\r\n"), US_ASCII)));
            Assert.assertTrue(ch.writeInbound(Unpooled.wrappedBuffer(data)));
            HttpContent content = ch.readInbound();
            Assert.assertEquals(data.length, content.content().readableBytes());
            byte[] decodedData = new byte[data.length];
            content.content().readBytes(decodedData);
            Assert.assertArrayEquals(data, decodedData);
            content.release();
            Assert.assertFalse(ch.writeInbound(Unpooled.copiedBuffer("\r\n", US_ASCII)));
        }
        // Write the last chunk.
        ch.writeInbound(Unpooled.copiedBuffer("0\r\n\r\n", US_ASCII));
        // Ensure the last chunk was decoded.
        LastHttpContent content = ch.readInbound();
        Assert.assertFalse(content.content().isReadable());
        content.release();
        ch.finish();
        Assert.assertNull(ch.readInbound());
    }

    @Test
    public void testResponseChunkedExceedMaxChunkSize() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder(4096, 8192, 32));
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.1 200 OK\r\nTransfer-Encoding: chunked\r\n\r\n", US_ASCII));
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(OK));
        byte[] data = new byte[64];
        for (int i = 0; i < (data.length); i++) {
            data[i] = ((byte) (i));
        }
        for (int i = 0; i < 10; i++) {
            Assert.assertFalse(ch.writeInbound(Unpooled.copiedBuffer(((Integer.toHexString(data.length)) + "\r\n"), US_ASCII)));
            Assert.assertTrue(ch.writeInbound(Unpooled.wrappedBuffer(data)));
            byte[] decodedData = new byte[data.length];
            HttpContent content = ch.readInbound();
            Assert.assertEquals(32, content.content().readableBytes());
            content.content().readBytes(decodedData, 0, 32);
            content.release();
            content = ch.readInbound();
            Assert.assertEquals(32, content.content().readableBytes());
            content.content().readBytes(decodedData, 32, 32);
            Assert.assertArrayEquals(data, decodedData);
            content.release();
            Assert.assertFalse(ch.writeInbound(Unpooled.copiedBuffer("\r\n", US_ASCII)));
        }
        // Write the last chunk.
        ch.writeInbound(Unpooled.copiedBuffer("0\r\n\r\n", US_ASCII));
        // Ensure the last chunk was decoded.
        LastHttpContent content = ch.readInbound();
        Assert.assertFalse(content.content().isReadable());
        content.release();
        ch.finish();
        Assert.assertNull(ch.readInbound());
    }

    @Test
    public void testClosureWithoutContentLength1() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.1 200 OK\r\n\r\n", US_ASCII));
        // Read the response headers.
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(OK));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        // Close the connection without sending anything.
        Assert.assertTrue(ch.finish());
        // The decoder should still produce the last content.
        LastHttpContent content = ch.readInbound();
        Assert.assertThat(content.content().isReadable(), CoreMatchers.is(false));
        content.release();
        // But nothing more.
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testClosureWithoutContentLength2() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        // Write the partial response.
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.1 200 OK\r\n\r\n12345678", US_ASCII));
        // Read the response headers.
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(OK));
        // Read the partial content.
        HttpContent content = ch.readInbound();
        Assert.assertThat(content.content().toString(US_ASCII), CoreMatchers.is("12345678"));
        Assert.assertThat(content, CoreMatchers.is(CoreMatchers.not(CoreMatchers.instanceOf(LastHttpContent.class))));
        content.release();
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        // Close the connection.
        Assert.assertTrue(ch.finish());
        // The decoder should still produce the last content.
        LastHttpContent lastContent = ch.readInbound();
        Assert.assertThat(lastContent.content().isReadable(), CoreMatchers.is(false));
        lastContent.release();
        // But nothing more.
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testPrematureClosureWithChunkedEncoding1() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.1 200 OK\r\nTransfer-Encoding: chunked\r\n\r\n", US_ASCII));
        // Read the response headers.
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(OK));
        Assert.assertThat(res.headers().get(TRANSFER_ENCODING), CoreMatchers.is("chunked"));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        // Close the connection without sending anything.
        ch.finish();
        // The decoder should not generate the last chunk because it's closed prematurely.
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testPrematureClosureWithChunkedEncoding2() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        // Write the partial response.
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.1 200 OK\r\nTransfer-Encoding: chunked\r\n\r\n8\r\n12345678", US_ASCII));
        // Read the response headers.
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(OK));
        Assert.assertThat(res.headers().get(TRANSFER_ENCODING), CoreMatchers.is("chunked"));
        // Read the partial content.
        HttpContent content = ch.readInbound();
        Assert.assertThat(content.content().toString(US_ASCII), CoreMatchers.is("12345678"));
        Assert.assertThat(content, CoreMatchers.is(CoreMatchers.not(CoreMatchers.instanceOf(LastHttpContent.class))));
        content.release();
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        // Close the connection.
        ch.finish();
        // The decoder should not generate the last chunk because it's closed prematurely.
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testLastResponseWithEmptyHeaderAndEmptyContent() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.1 200 OK\r\n\r\n", US_ASCII));
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(OK));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(ch.finish(), CoreMatchers.is(true));
        LastHttpContent content = ch.readInbound();
        Assert.assertThat(content.content().isReadable(), CoreMatchers.is(false));
        content.release();
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testLastResponseWithoutContentLengthHeader() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.1 200 OK\r\n\r\n", US_ASCII));
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(OK));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[1024]));
        HttpContent content = ch.readInbound();
        Assert.assertThat(content.content().readableBytes(), CoreMatchers.is(1024));
        content.release();
        Assert.assertThat(ch.finish(), CoreMatchers.is(true));
        LastHttpContent lastContent = ch.readInbound();
        Assert.assertThat(lastContent.content().isReadable(), CoreMatchers.is(false));
        lastContent.release();
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testLastResponseWithHeaderRemoveTrailingSpaces() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.copiedBuffer("HTTP/1.1 200 OK\r\nX-Header: h2=h2v2; Expires=Wed, 09-Jun-2021 10:18:14 GMT       \r\n\r\n", US_ASCII));
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(OK));
        Assert.assertThat(res.headers().get(HttpHeadersTestUtils.of("X-Header")), CoreMatchers.is("h2=h2v2; Expires=Wed, 09-Jun-2021 10:18:14 GMT"));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        ch.writeInbound(Unpooled.wrappedBuffer(new byte[1024]));
        HttpContent content = ch.readInbound();
        Assert.assertThat(content.content().readableBytes(), CoreMatchers.is(1024));
        content.release();
        Assert.assertThat(ch.finish(), CoreMatchers.is(true));
        LastHttpContent lastContent = ch.readInbound();
        Assert.assertThat(lastContent.content().isReadable(), CoreMatchers.is(false));
        lastContent.release();
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testResetContentResponseWithTransferEncoding() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        Assert.assertTrue(ch.writeInbound(Unpooled.copiedBuffer(("HTTP/1.1 205 Reset Content\r\n" + ((("Transfer-Encoding: chunked\r\n" + "\r\n") + "0\r\n") + "\r\n")), US_ASCII)));
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(RESET_CONTENT));
        LastHttpContent lastContent = ch.readInbound();
        Assert.assertThat(lastContent.content().isReadable(), CoreMatchers.is(false));
        lastContent.release();
        Assert.assertThat(ch.finish(), CoreMatchers.is(false));
    }

    @Test
    public void testLastResponseWithTrailingHeader() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.copiedBuffer(("HTTP/1.1 200 OK\r\n" + ((((("Transfer-Encoding: chunked\r\n" + "\r\n") + "0\r\n") + "Set-Cookie: t1=t1v1\r\n") + "Set-Cookie: t2=t2v2; Expires=Wed, 09-Jun-2021 10:18:14 GMT\r\n") + "\r\n")), US_ASCII));
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(OK));
        LastHttpContent lastContent = ch.readInbound();
        Assert.assertThat(lastContent.content().isReadable(), CoreMatchers.is(false));
        HttpHeaders headers = lastContent.trailingHeaders();
        Assert.assertEquals(1, headers.names().size());
        List<String> values = headers.getAll(HttpHeadersTestUtils.of("Set-Cookie"));
        Assert.assertEquals(2, values.size());
        Assert.assertTrue(values.contains("t1=t1v1"));
        Assert.assertTrue(values.contains("t2=t2v2; Expires=Wed, 09-Jun-2021 10:18:14 GMT"));
        lastContent.release();
        Assert.assertThat(ch.finish(), CoreMatchers.is(false));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testLastResponseWithTrailingHeaderFragmented() {
        byte[] data = ("HTTP/1.1 200 OK\r\n" + ((((("Transfer-Encoding: chunked\r\n" + "\r\n") + "0\r\n") + "Set-Cookie: t1=t1v1\r\n") + "Set-Cookie: t2=t2v2; Expires=Wed, 09-Jun-2021 10:18:14 GMT\r\n") + "\r\n")).getBytes(US_ASCII);
        for (int i = 1; i < (data.length); i++) {
            HttpResponseDecoderTest.testLastResponseWithTrailingHeaderFragmented(data, i);
        }
    }

    @Test
    public void testResponseWithContentLength() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.copiedBuffer(("HTTP/1.1 200 OK\r\n" + ("Content-Length: 10\r\n" + "\r\n")), US_ASCII));
        byte[] data = new byte[10];
        for (int i = 0; i < (data.length); i++) {
            data[i] = ((byte) (i));
        }
        ch.writeInbound(Unpooled.wrappedBuffer(data, 0, ((data.length) / 2)));
        ch.writeInbound(Unpooled.wrappedBuffer(data, 5, ((data.length) / 2)));
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(OK));
        HttpContent firstContent = ch.readInbound();
        Assert.assertThat(firstContent.content().readableBytes(), CoreMatchers.is(5));
        Assert.assertEquals(Unpooled.wrappedBuffer(data, 0, 5), firstContent.content());
        firstContent.release();
        LastHttpContent lastContent = ch.readInbound();
        Assert.assertEquals(5, lastContent.content().readableBytes());
        Assert.assertEquals(Unpooled.wrappedBuffer(data, 5, 5), lastContent.content());
        lastContent.release();
        Assert.assertThat(ch.finish(), CoreMatchers.is(false));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testResponseWithContentLengthFragmented() {
        byte[] data = ("HTTP/1.1 200 OK\r\n" + ("Content-Length: 10\r\n" + "\r\n")).getBytes(US_ASCII);
        for (int i = 1; i < (data.length); i++) {
            HttpResponseDecoderTest.testResponseWithContentLengthFragmented(data, i);
        }
    }

    @Test
    public void testWebSocketResponse() {
        byte[] data = ("HTTP/1.1 101 WebSocket Protocol Handshake\r\n" + ((((("Upgrade: WebSocket\r\n" + "Connection: Upgrade\r\n") + "Sec-WebSocket-Origin: http://localhost:8080\r\n") + "Sec-WebSocket-Location: ws://localhost/some/path\r\n") + "\r\n") + "1234567812345678")).getBytes();
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.wrappedBuffer(data));
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(SWITCHING_PROTOCOLS));
        HttpContent content = ch.readInbound();
        Assert.assertThat(content.content().readableBytes(), CoreMatchers.is(16));
        content.release();
        Assert.assertThat(ch.finish(), CoreMatchers.is(false));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    // See https://github.com/netty/netty/issues/2173
    @Test
    public void testWebSocketResponseWithDataFollowing() {
        byte[] data = ("HTTP/1.1 101 WebSocket Protocol Handshake\r\n" + ((((("Upgrade: WebSocket\r\n" + "Connection: Upgrade\r\n") + "Sec-WebSocket-Origin: http://localhost:8080\r\n") + "Sec-WebSocket-Location: ws://localhost/some/path\r\n") + "\r\n") + "1234567812345678")).getBytes();
        byte[] otherData = new byte[]{ 1, 2, 3, 4 };
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.wrappedBuffer(data, otherData));
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(SWITCHING_PROTOCOLS));
        HttpContent content = ch.readInbound();
        Assert.assertThat(content.content().readableBytes(), CoreMatchers.is(16));
        content.release();
        Assert.assertThat(ch.finish(), CoreMatchers.is(true));
        ByteBuf expected = Unpooled.wrappedBuffer(otherData);
        ByteBuf buffer = ch.readInbound();
        try {
            Assert.assertEquals(expected, buffer);
        } finally {
            expected.release();
            if (buffer != null) {
                buffer.release();
            }
        }
    }

    @Test
    public void testGarbageHeaders() {
        // A response without headers - from https://github.com/netty/netty/issues/2103
        byte[] data = ("<html>\r\n" + ((((("<head><title>400 Bad Request</title></head>\r\n" + "<body bgcolor=\"white\">\r\n") + "<center><h1>400 Bad Request</h1></center>\r\n") + "<hr><center>nginx/1.1.19</center>\r\n") + "</body>\r\n") + "</html>\r\n")).getBytes();
        EmbeddedChannel ch = new EmbeddedChannel(new HttpResponseDecoder());
        ch.writeInbound(Unpooled.wrappedBuffer(data));
        // Garbage input should generate the 999 Unknown response.
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_0));
        Assert.assertThat(res.status().code(), CoreMatchers.is(999));
        Assert.assertThat(res.decoderResult().isFailure(), CoreMatchers.is(true));
        Assert.assertThat(res.decoderResult().isFinished(), CoreMatchers.is(true));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        // More garbage should not generate anything (i.e. the decoder discards anything beyond this point.)
        ch.writeInbound(Unpooled.wrappedBuffer(data));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        // Closing the connection should not generate anything since the protocol has been violated.
        ch.finish();
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    /**
     * Tests if the decoder produces one and only {@link LastHttpContent} when an invalid chunk is received and
     * the connection is closed.
     */
    @Test
    public void testGarbageChunk() {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseDecoder());
        String responseWithIllegalChunk = "HTTP/1.1 200 OK\r\n" + ("Transfer-Encoding: chunked\r\n\r\n" + "NOT_A_CHUNK_LENGTH\r\n");
        channel.writeInbound(Unpooled.copiedBuffer(responseWithIllegalChunk, US_ASCII));
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.instanceOf(HttpResponse.class)));
        // Ensure that the decoder generates the last chunk with correct decoder result.
        LastHttpContent invalidChunk = channel.readInbound();
        Assert.assertThat(invalidChunk.decoderResult().isFailure(), CoreMatchers.is(true));
        invalidChunk.release();
        // And no more messages should be produced by the decoder.
        Assert.assertThat(channel.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        // .. even after the connection is closed.
        Assert.assertThat(channel.finish(), CoreMatchers.is(false));
    }

    @Test
    public void testConnectionClosedBeforeHeadersReceived() {
        EmbeddedChannel channel = new EmbeddedChannel(new HttpResponseDecoder());
        String responseInitialLine = "HTTP/1.1 200 OK\r\n";
        Assert.assertFalse(channel.writeInbound(Unpooled.copiedBuffer(responseInitialLine, US_ASCII)));
        Assert.assertTrue(channel.finish());
        HttpMessage message = channel.readInbound();
        Assert.assertTrue(message.decoderResult().isFailure());
        Assert.assertThat(message.decoderResult().cause(), CoreMatchers.instanceOf(PrematureChannelClosureException.class));
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testTrailerWithEmptyLineInSeparateBuffer() {
        HttpResponseDecoder decoder = new HttpResponseDecoder();
        EmbeddedChannel channel = new EmbeddedChannel(decoder);
        String headers = "HTTP/1.1 200 OK\r\n" + ("Transfer-Encoding: chunked\r\n" + "Trailer: My-Trailer\r\n");
        Assert.assertFalse(channel.writeInbound(Unpooled.copiedBuffer(headers.getBytes(US_ASCII))));
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer("\r\n".getBytes(US_ASCII))));
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer("0\r\n", US_ASCII)));
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer("My-Trailer: 42\r\n", US_ASCII)));
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer("\r\n", US_ASCII)));
        HttpResponse response = channel.readInbound();
        Assert.assertEquals(2, response.headers().size());
        Assert.assertEquals("chunked", response.headers().get(TRANSFER_ENCODING));
        Assert.assertEquals("My-Trailer", response.headers().get(TRAILER));
        LastHttpContent lastContent = channel.readInbound();
        Assert.assertEquals(1, lastContent.trailingHeaders().size());
        Assert.assertEquals("42", lastContent.trailingHeaders().get("My-Trailer"));
        Assert.assertEquals(0, lastContent.content().readableBytes());
        lastContent.release();
        Assert.assertFalse(channel.finish());
    }
}

