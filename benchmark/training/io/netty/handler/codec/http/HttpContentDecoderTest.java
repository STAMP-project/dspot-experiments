/**
 * Copyright 2015 The Netty Project
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
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderNames.TRANSFER_ENCODING;
import HttpHeaderValues.CHUNKED;
import HttpStatusClass.CLIENT_ERROR;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static HttpMethod.GET;
import static HttpVersion.HTTP_1_1;


public class HttpContentDecoderTest {
    private static final String HELLO_WORLD = "hello, world";

    private static final byte[] GZ_HELLO_WORLD = new byte[]{ 31, -117, 8, 8, 12, 3, -74, 84, 0, 3, 50, 0, -53, 72, -51, -55, -55, -41, 81, 40, -49, 47, -54, 73, 1, 0, 58, 114, -85, -1, 12, 0, 0, 0 };

    @Test
    public void testBinaryDecompression() throws Exception {
        // baseline test: zlib library and test helpers work correctly.
        byte[] helloWorld = HttpContentDecoderTest.gzDecompress(HttpContentDecoderTest.GZ_HELLO_WORLD);
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD.length(), helloWorld.length);
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD, new String(helloWorld, CharsetUtil.US_ASCII));
        String fullCycleTest = "full cycle test";
        byte[] compressed = HttpContentDecoderTest.gzCompress(fullCycleTest.getBytes(US_ASCII));
        byte[] decompressed = HttpContentDecoderTest.gzDecompress(compressed);
        Assert.assertEquals(decompressed.length, fullCycleTest.length());
        Assert.assertEquals(fullCycleTest, new String(decompressed, CharsetUtil.US_ASCII));
    }

    @Test
    public void testRequestDecompression() {
        // baseline test: request decoder, content decompressor && request aggregator work as expected
        HttpRequestDecoder decoder = new HttpRequestDecoder();
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        HttpObjectAggregator aggregator = new HttpObjectAggregator(1024);
        EmbeddedChannel channel = new EmbeddedChannel(decoder, decompressor, aggregator);
        String headers = (((("POST / HTTP/1.1\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Content-Encoding: gzip\r\n") + "\r\n";
        ByteBuf buf = Unpooled.copiedBuffer(headers.getBytes(US_ASCII), HttpContentDecoderTest.GZ_HELLO_WORLD);
        Assert.assertTrue(channel.writeInbound(buf));
        Object o = channel.readInbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpRequest.class)));
        FullHttpRequest req = ((FullHttpRequest) (o));
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD.length(), req.headers().getInt(CONTENT_LENGTH).intValue());
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD, req.content().toString(US_ASCII));
        req.release();
        HttpContentDecoderTest.assertHasInboundMessages(channel, false);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());// assert that no messages are left in channel

    }

    @Test
    public void testChunkedRequestDecompression() {
        HttpResponseDecoder decoder = new HttpResponseDecoder();
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        EmbeddedChannel channel = new EmbeddedChannel(decoder, decompressor, null);
        String headers = "HTTP/1.1 200 OK\r\n" + (("Transfer-Encoding: chunked\r\n" + "Trailer: My-Trailer\r\n") + "Content-Encoding: gzip\r\n\r\n");
        channel.writeInbound(Unpooled.copiedBuffer(headers.getBytes(US_ASCII)));
        String chunkLength = Integer.toHexString(HttpContentDecoderTest.GZ_HELLO_WORLD.length);
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer((chunkLength + "\r\n"), US_ASCII)));
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer(HttpContentDecoderTest.GZ_HELLO_WORLD)));
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer("\r\n".getBytes(US_ASCII))));
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer("0\r\n", US_ASCII)));
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer("My-Trailer: 42\r\n\r\n\r\n", US_ASCII)));
        Object ob1 = channel.readInbound();
        Assert.assertThat(ob1, CoreMatchers.is(CoreMatchers.instanceOf(DefaultHttpResponse.class)));
        Object ob2 = channel.readInbound();
        Assert.assertThat(ob1, CoreMatchers.is(CoreMatchers.instanceOf(DefaultHttpResponse.class)));
        HttpContent content = ((HttpContent) (ob2));
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD, content.content().toString(US_ASCII));
        content.release();
        Object ob3 = channel.readInbound();
        Assert.assertThat(ob1, CoreMatchers.is(CoreMatchers.instanceOf(DefaultHttpResponse.class)));
        LastHttpContent lastContent = ((LastHttpContent) (ob3));
        Assert.assertNotNull(lastContent.decoderResult());
        Assert.assertTrue(lastContent.decoderResult().isSuccess());
        Assert.assertFalse(lastContent.trailingHeaders().isEmpty());
        Assert.assertEquals("42", lastContent.trailingHeaders().get("My-Trailer"));
        HttpContentDecoderTest.assertHasInboundMessages(channel, false);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testResponseDecompression() {
        // baseline test: response decoder, content decompressor && request aggregator work as expected
        HttpResponseDecoder decoder = new HttpResponseDecoder();
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        HttpObjectAggregator aggregator = new HttpObjectAggregator(1024);
        EmbeddedChannel channel = new EmbeddedChannel(decoder, decompressor, aggregator);
        String headers = (((("HTTP/1.1 200 OK\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Content-Encoding: gzip\r\n") + "\r\n";
        ByteBuf buf = Unpooled.copiedBuffer(headers.getBytes(US_ASCII), HttpContentDecoderTest.GZ_HELLO_WORLD);
        Assert.assertTrue(channel.writeInbound(buf));
        Object o = channel.readInbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        FullHttpResponse resp = ((FullHttpResponse) (o));
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD.length(), resp.headers().getInt(CONTENT_LENGTH).intValue());
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD, resp.content().toString(US_ASCII));
        resp.release();
        HttpContentDecoderTest.assertHasInboundMessages(channel, false);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());// assert that no messages are left in channel

    }

    @Test
    public void testExpectContinueResponse1() {
        // request with header "Expect: 100-continue" must be replied with one "100 Continue" response
        // case 1: no ContentDecoder in chain at all (baseline test)
        HttpRequestDecoder decoder = new HttpRequestDecoder();
        HttpObjectAggregator aggregator = new HttpObjectAggregator(1024);
        EmbeddedChannel channel = new EmbeddedChannel(decoder, aggregator);
        String req = (((("POST / HTTP/1.1\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Expect: 100-continue\r\n") + "\r\n";
        // note: the following writeInbound() returns false as there is no message is inbound buffer
        // until HttpObjectAggregator caches composes a complete message.
        // however, http response "100 continue" must be sent as soon as headers are received
        Assert.assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(req.getBytes())));
        Object o = channel.readOutbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        FullHttpResponse r = ((FullHttpResponse) (o));
        Assert.assertEquals(100, r.status().code());
        Assert.assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(HttpContentDecoderTest.GZ_HELLO_WORLD)));
        r.release();
        HttpContentDecoderTest.assertHasInboundMessages(channel, true);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testExpectContinueResponse2() {
        // request with header "Expect: 100-continue" must be replied with one "100 Continue" response
        // case 2: contentDecoder is in chain, but the content is not encoded, should be no-op
        HttpRequestDecoder decoder = new HttpRequestDecoder();
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        HttpObjectAggregator aggregator = new HttpObjectAggregator(1024);
        EmbeddedChannel channel = new EmbeddedChannel(decoder, decompressor, aggregator);
        String req = (((("POST / HTTP/1.1\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Expect: 100-continue\r\n") + "\r\n";
        Assert.assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(req.getBytes())));
        Object o = channel.readOutbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        FullHttpResponse r = ((FullHttpResponse) (o));
        Assert.assertEquals(100, r.status().code());
        r.release();
        Assert.assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(HttpContentDecoderTest.GZ_HELLO_WORLD)));
        HttpContentDecoderTest.assertHasInboundMessages(channel, true);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testExpectContinueResponse3() {
        // request with header "Expect: 100-continue" must be replied with one "100 Continue" response
        // case 3: ContentDecoder is in chain and content is encoded
        HttpRequestDecoder decoder = new HttpRequestDecoder();
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        HttpObjectAggregator aggregator = new HttpObjectAggregator(1024);
        EmbeddedChannel channel = new EmbeddedChannel(decoder, decompressor, aggregator);
        String req = ((((("POST / HTTP/1.1\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Expect: 100-continue\r\n") + "Content-Encoding: gzip\r\n") + "\r\n";
        Assert.assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(req.getBytes())));
        Object o = channel.readOutbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        FullHttpResponse r = ((FullHttpResponse) (o));
        Assert.assertEquals(100, r.status().code());
        r.release();
        Assert.assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(HttpContentDecoderTest.GZ_HELLO_WORLD)));
        HttpContentDecoderTest.assertHasInboundMessages(channel, true);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testExpectContinueResponse4() {
        // request with header "Expect: 100-continue" must be replied with one "100 Continue" response
        // case 4: ObjectAggregator is up in chain
        HttpRequestDecoder decoder = new HttpRequestDecoder();
        HttpObjectAggregator aggregator = new HttpObjectAggregator(1024);
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        EmbeddedChannel channel = new EmbeddedChannel(decoder, aggregator, decompressor);
        String req = ((((("POST / HTTP/1.1\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Expect: 100-continue\r\n") + "Content-Encoding: gzip\r\n") + "\r\n";
        Assert.assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(req.getBytes())));
        Object o = channel.readOutbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        FullHttpResponse r = ((FullHttpResponse) (o));
        Assert.assertEquals(100, r.status().code());
        r.release();
        Assert.assertTrue(channel.writeInbound(Unpooled.wrappedBuffer(HttpContentDecoderTest.GZ_HELLO_WORLD)));
        HttpContentDecoderTest.assertHasInboundMessages(channel, true);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testExpectContinueResetHttpObjectDecoder() {
        // request with header "Expect: 100-continue" must be replied with one "100 Continue" response
        // case 5: Test that HttpObjectDecoder correctly resets its internal state after a failed expectation.
        HttpRequestDecoder decoder = new HttpRequestDecoder();
        final int maxBytes = 10;
        HttpObjectAggregator aggregator = new HttpObjectAggregator(maxBytes);
        final AtomicReference<FullHttpRequest> secondRequestRef = new AtomicReference<FullHttpRequest>();
        EmbeddedChannel channel = new EmbeddedChannel(decoder, aggregator, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof FullHttpRequest) {
                    if (!(secondRequestRef.compareAndSet(null, ((FullHttpRequest) (msg))))) {
                        release();
                    }
                } else {
                    ReferenceCountUtil.release(msg);
                }
            }
        });
        String req1 = (((("POST /1 HTTP/1.1\r\n" + "Content-Length: ") + (maxBytes + 1)) + "\r\n") + "Expect: 100-continue\r\n") + "\r\n";
        Assert.assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(req1.getBytes(US_ASCII))));
        FullHttpResponse resp = channel.readOutbound();
        Assert.assertEquals(CLIENT_ERROR, resp.status().codeClass());
        resp.release();
        String req2 = (((("POST /2 HTTP/1.1\r\n" + "Content-Length: ") + maxBytes) + "\r\n") + "Expect: 100-continue\r\n") + "\r\n";
        Assert.assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(req2.getBytes(US_ASCII))));
        resp = channel.readOutbound();
        Assert.assertEquals(100, resp.status().code());
        resp.release();
        byte[] content = new byte[maxBytes];
        Assert.assertFalse(channel.writeInbound(Unpooled.wrappedBuffer(content)));
        FullHttpRequest req = secondRequestRef.get();
        Assert.assertNotNull(req);
        Assert.assertEquals("/2", req.uri());
        Assert.assertEquals(10, req.content().readableBytes());
        req.release();
        HttpContentDecoderTest.assertHasInboundMessages(channel, false);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testRequestContentLength1() {
        // case 1: test that ContentDecompressor either sets the correct Content-Length header
        // or removes it completely (handlers down the chain must rely on LastHttpContent object)
        // force content to be in more than one chunk (5 bytes/chunk)
        HttpRequestDecoder decoder = new HttpRequestDecoder(4096, 4096, 5);
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        EmbeddedChannel channel = new EmbeddedChannel(decoder, decompressor);
        String headers = (((("POST / HTTP/1.1\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Content-Encoding: gzip\r\n") + "\r\n";
        ByteBuf buf = Unpooled.copiedBuffer(headers.getBytes(US_ASCII), HttpContentDecoderTest.GZ_HELLO_WORLD);
        Assert.assertTrue(channel.writeInbound(buf));
        Queue<Object> req = channel.inboundMessages();
        Assert.assertTrue(((req.size()) >= 1));
        Object o = req.peek();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(HttpRequest.class)));
        HttpRequest r = ((HttpRequest) (o));
        String v = r.headers().get(CONTENT_LENGTH);
        Long value = (v == null) ? null : Long.parseLong(v);
        Assert.assertTrue(((value == null) || ((value.longValue()) == (HttpContentDecoderTest.HELLO_WORLD.length()))));
        HttpContentDecoderTest.assertHasInboundMessages(channel, true);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testRequestContentLength2() {
        // case 2: if HttpObjectAggregator is down the chain, then correct Content-Length header must be set
        // force content to be in more than one chunk (5 bytes/chunk)
        HttpRequestDecoder decoder = new HttpRequestDecoder(4096, 4096, 5);
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        HttpObjectAggregator aggregator = new HttpObjectAggregator(1024);
        EmbeddedChannel channel = new EmbeddedChannel(decoder, decompressor, aggregator);
        String headers = (((("POST / HTTP/1.1\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Content-Encoding: gzip\r\n") + "\r\n";
        ByteBuf buf = Unpooled.copiedBuffer(headers.getBytes(US_ASCII), HttpContentDecoderTest.GZ_HELLO_WORLD);
        Assert.assertTrue(channel.writeInbound(buf));
        Object o = channel.readInbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpRequest.class)));
        FullHttpRequest r = ((FullHttpRequest) (o));
        String v = r.headers().get(CONTENT_LENGTH);
        Long value = (v == null) ? null : Long.parseLong(v);
        r.release();
        Assert.assertNotNull(value);
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD.length(), value.longValue());
        HttpContentDecoderTest.assertHasInboundMessages(channel, false);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testResponseContentLength1() {
        // case 1: test that ContentDecompressor either sets the correct Content-Length header
        // or removes it completely (handlers down the chain must rely on LastHttpContent object)
        // force content to be in more than one chunk (5 bytes/chunk)
        HttpResponseDecoder decoder = new HttpResponseDecoder(4096, 4096, 5);
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        EmbeddedChannel channel = new EmbeddedChannel(decoder, decompressor);
        String headers = (((("HTTP/1.1 200 OK\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Content-Encoding: gzip\r\n") + "\r\n";
        ByteBuf buf = Unpooled.copiedBuffer(headers.getBytes(US_ASCII), HttpContentDecoderTest.GZ_HELLO_WORLD);
        Assert.assertTrue(channel.writeInbound(buf));
        Queue<Object> resp = channel.inboundMessages();
        Assert.assertTrue(((resp.size()) >= 1));
        Object o = resp.peek();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(HttpResponse.class)));
        HttpResponse r = ((HttpResponse) (o));
        Assert.assertFalse("Content-Length header not removed.", r.headers().contains(CONTENT_LENGTH));
        String transferEncoding = r.headers().get(TRANSFER_ENCODING);
        Assert.assertNotNull("Content-length as well as transfer-encoding not set.", transferEncoding);
        Assert.assertEquals("Unexpected transfer-encoding value.", CHUNKED.toString(), transferEncoding);
        HttpContentDecoderTest.assertHasInboundMessages(channel, true);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testResponseContentLength2() {
        // case 2: if HttpObjectAggregator is down the chain, then correct Content-Length header must be set
        // force content to be in more than one chunk (5 bytes/chunk)
        HttpResponseDecoder decoder = new HttpResponseDecoder(4096, 4096, 5);
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        HttpObjectAggregator aggregator = new HttpObjectAggregator(1024);
        EmbeddedChannel channel = new EmbeddedChannel(decoder, decompressor, aggregator);
        String headers = (((("HTTP/1.1 200 OK\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Content-Encoding: gzip\r\n") + "\r\n";
        ByteBuf buf = Unpooled.copiedBuffer(headers.getBytes(US_ASCII), HttpContentDecoderTest.GZ_HELLO_WORLD);
        Assert.assertTrue(channel.writeInbound(buf));
        Object o = channel.readInbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        FullHttpResponse r = ((FullHttpResponse) (o));
        String v = r.headers().get(CONTENT_LENGTH);
        Long value = (v == null) ? null : Long.parseLong(v);
        Assert.assertNotNull(value);
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD.length(), value.longValue());
        r.release();
        HttpContentDecoderTest.assertHasInboundMessages(channel, false);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testFullHttpRequest() {
        // test that ContentDecoder can be used after the ObjectAggregator
        HttpRequestDecoder decoder = new HttpRequestDecoder(4096, 4096, 5);
        HttpObjectAggregator aggregator = new HttpObjectAggregator(1024);
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        EmbeddedChannel channel = new EmbeddedChannel(decoder, aggregator, decompressor);
        String headers = (((("POST / HTTP/1.1\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Content-Encoding: gzip\r\n") + "\r\n";
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer(headers.getBytes(), HttpContentDecoderTest.GZ_HELLO_WORLD)));
        Queue<Object> req = channel.inboundMessages();
        Assert.assertTrue(((req.size()) > 1));
        int contentLength = 0;
        contentLength = HttpContentDecoderTest.calculateContentLength(req, contentLength);
        byte[] receivedContent = HttpContentDecoderTest.readContent(req, contentLength, true);
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD, new String(receivedContent, CharsetUtil.US_ASCII));
        HttpContentDecoderTest.assertHasInboundMessages(channel, true);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testFullHttpResponse() {
        // test that ContentDecoder can be used after the ObjectAggregator
        HttpResponseDecoder decoder = new HttpResponseDecoder(4096, 4096, 5);
        HttpObjectAggregator aggregator = new HttpObjectAggregator(1024);
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        EmbeddedChannel channel = new EmbeddedChannel(decoder, aggregator, decompressor);
        String headers = (((("HTTP/1.1 200 OK\r\n" + "Content-Length: ") + (HttpContentDecoderTest.GZ_HELLO_WORLD.length)) + "\r\n") + "Content-Encoding: gzip\r\n") + "\r\n";
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer(headers.getBytes(), HttpContentDecoderTest.GZ_HELLO_WORLD)));
        Queue<Object> resp = channel.inboundMessages();
        Assert.assertTrue(((resp.size()) > 1));
        int contentLength = 0;
        contentLength = HttpContentDecoderTest.calculateContentLength(resp, contentLength);
        byte[] receivedContent = HttpContentDecoderTest.readContent(resp, contentLength, true);
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD, new String(receivedContent, CharsetUtil.US_ASCII));
        HttpContentDecoderTest.assertHasInboundMessages(channel, true);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    // See https://github.com/netty/netty/issues/5892
    @Test
    public void testFullHttpResponseEOF() {
        // test that ContentDecoder can be used after the ObjectAggregator
        HttpResponseDecoder decoder = new HttpResponseDecoder(4096, 4096, 5);
        HttpContentDecoder decompressor = new HttpContentDecompressor();
        EmbeddedChannel channel = new EmbeddedChannel(decoder, decompressor);
        String headers = "HTTP/1.1 200 OK\r\n" + ("Content-Encoding: gzip\r\n" + "\r\n");
        Assert.assertTrue(channel.writeInbound(Unpooled.copiedBuffer(headers.getBytes(), HttpContentDecoderTest.GZ_HELLO_WORLD)));
        // This should terminate it.
        Assert.assertTrue(channel.finish());
        Queue<Object> resp = channel.inboundMessages();
        Assert.assertTrue(((resp.size()) > 1));
        int contentLength = 0;
        contentLength = HttpContentDecoderTest.calculateContentLength(resp, contentLength);
        byte[] receivedContent = HttpContentDecoderTest.readContent(resp, contentLength, false);
        Assert.assertEquals(HttpContentDecoderTest.HELLO_WORLD, new String(receivedContent, CharsetUtil.US_ASCII));
        HttpContentDecoderTest.assertHasInboundMessages(channel, true);
        HttpContentDecoderTest.assertHasOutboundMessages(channel, false);
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testCleanupThrows() {
        HttpContentDecoder decoder = new HttpContentDecoder() {
            @Override
            protected EmbeddedChannel newContentDecoder(String contentEncoding) throws Exception {
                return new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        ctx.fireExceptionCaught(new DecoderException());
                        ctx.fireChannelInactive();
                    }
                });
            }
        };
        final AtomicBoolean channelInactiveCalled = new AtomicBoolean();
        EmbeddedChannel channel = new EmbeddedChannel(decoder, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                Assert.assertTrue(channelInactiveCalled.compareAndSet(false, true));
                super.channelInactive(ctx);
            }
        });
        Assert.assertTrue(channel.writeInbound(new DefaultHttpRequest(HTTP_1_1, GET, "/")));
        HttpContent content = new DefaultHttpContent(Unpooled.buffer().writeZero(10));
        Assert.assertTrue(channel.writeInbound(content));
        Assert.assertEquals(1, content.refCnt());
        try {
            channel.finishAndReleaseAll();
            Assert.fail();
        } catch (CodecException expected) {
            // expected
        }
        Assert.assertTrue(channelInactiveCalled.get());
        Assert.assertEquals(0, content.refCnt());
    }
}

