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
import CharsetUtil.UTF_8;
import DecoderResult.SUCCESS;
import HttpHeaderNames.ACCEPT_ENCODING;
import HttpHeaderNames.CONTENT_ENCODING;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderNames.TRANSFER_ENCODING;
import HttpHeaderValues.CHUNKED;
import HttpHeaderValues.GZIP;
import HttpHeaderValues.ZERO;
import LastHttpContent.EMPTY_LAST_CONTENT;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.EncoderException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static HttpMethod.CONNECT;
import static HttpMethod.GET;
import static HttpMethod.HEAD;
import static HttpResponseStatus.METHOD_NOT_ALLOWED;
import static HttpResponseStatus.NOT_MODIFIED;
import static HttpResponseStatus.OK;
import static HttpVersion.HTTP_1_0;
import static HttpVersion.HTTP_1_1;


public class HttpContentEncoderTest {
    private static final class TestEncoder extends HttpContentEncoder {
        @Override
        protected Result beginEncode(HttpResponse headers, String acceptEncoding) {
            return new Result("test", new EmbeddedChannel(new io.netty.handler.codec.MessageToByteEncoder<ByteBuf>() {
                @Override
                protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
                    out.writeBytes(String.valueOf(in.readableBytes()).getBytes(US_ASCII));
                    in.skipBytes(in.readableBytes());
                }
            }));
        }
    }

    @Test
    public void testSplitContent() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        ch.writeInbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "/"));
        ch.writeOutbound(new DefaultHttpResponse(HTTP_1_1, OK));
        ch.writeOutbound(new DefaultHttpContent(Unpooled.wrappedBuffer(new byte[3])));
        ch.writeOutbound(new DefaultHttpContent(Unpooled.wrappedBuffer(new byte[2])));
        ch.writeOutbound(new DefaultLastHttpContent(Unpooled.wrappedBuffer(new byte[1])));
        HttpContentEncoderTest.assertEncodedResponse(ch);
        HttpContent chunk;
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("3"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("2"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("1"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().isReadable(), CoreMatchers.is(false));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(LastHttpContent.class)));
        chunk.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testChunkedContent() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        ch.writeInbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "/"));
        HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
        res.headers().set(TRANSFER_ENCODING, CHUNKED);
        ch.writeOutbound(res);
        HttpContentEncoderTest.assertEncodedResponse(ch);
        ch.writeOutbound(new DefaultHttpContent(Unpooled.wrappedBuffer(new byte[3])));
        ch.writeOutbound(new DefaultHttpContent(Unpooled.wrappedBuffer(new byte[2])));
        ch.writeOutbound(new DefaultLastHttpContent(Unpooled.wrappedBuffer(new byte[1])));
        HttpContent chunk;
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("3"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("2"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("1"));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(HttpContent.class)));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().isReadable(), CoreMatchers.is(false));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(LastHttpContent.class)));
        chunk.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testChunkedContentWithTrailingHeader() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        ch.writeInbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "/"));
        HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
        res.headers().set(TRANSFER_ENCODING, CHUNKED);
        ch.writeOutbound(res);
        HttpContentEncoderTest.assertEncodedResponse(ch);
        ch.writeOutbound(new DefaultHttpContent(Unpooled.wrappedBuffer(new byte[3])));
        ch.writeOutbound(new DefaultHttpContent(Unpooled.wrappedBuffer(new byte[2])));
        LastHttpContent content = new DefaultLastHttpContent(Unpooled.wrappedBuffer(new byte[1]));
        content.trailingHeaders().set(HttpHeadersTestUtils.of("X-Test"), HttpHeadersTestUtils.of("Netty"));
        ch.writeOutbound(content);
        HttpContent chunk;
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("3"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("2"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("1"));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(HttpContent.class)));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().isReadable(), CoreMatchers.is(false));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(LastHttpContent.class)));
        Assert.assertEquals("Netty", trailingHeaders().get(HttpHeadersTestUtils.of("X-Test")));
        Assert.assertEquals(SUCCESS, res.decoderResult());
        chunk.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testFullContentWithContentLength() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        ch.writeInbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "/"));
        FullHttpResponse fullRes = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(new byte[42]));
        fullRes.headers().set(CONTENT_LENGTH, 42);
        ch.writeOutbound(fullRes);
        HttpResponse res = ch.readOutbound();
        Assert.assertThat(res, CoreMatchers.is(CoreMatchers.not(CoreMatchers.instanceOf(HttpContent.class))));
        Assert.assertThat(res.headers().get(TRANSFER_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(res.headers().get(CONTENT_LENGTH), CoreMatchers.is("2"));
        Assert.assertThat(res.headers().get(CONTENT_ENCODING), CoreMatchers.is("test"));
        HttpContent c = ch.readOutbound();
        Assert.assertThat(c.content().readableBytes(), CoreMatchers.is(2));
        Assert.assertThat(c.content().toString(US_ASCII), CoreMatchers.is("42"));
        c.release();
        LastHttpContent last = ch.readOutbound();
        Assert.assertThat(last.content().readableBytes(), CoreMatchers.is(0));
        last.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testFullContent() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        ch.writeInbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "/"));
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(new byte[42]));
        ch.writeOutbound(res);
        HttpContentEncoderTest.assertEncodedResponse(ch);
        HttpContent c = ch.readOutbound();
        Assert.assertThat(c.content().readableBytes(), CoreMatchers.is(2));
        Assert.assertThat(c.content().toString(US_ASCII), CoreMatchers.is("42"));
        c.release();
        LastHttpContent last = ch.readOutbound();
        Assert.assertThat(last.content().readableBytes(), CoreMatchers.is(0));
        last.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    /**
     * If the length of the content is unknown, {@link HttpContentEncoder} should not skip encoding the content
     * even if the actual length is turned out to be 0.
     */
    @Test
    public void testEmptySplitContent() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        ch.writeInbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "/"));
        ch.writeOutbound(new DefaultHttpResponse(HTTP_1_1, OK));
        HttpContentEncoderTest.assertEncodedResponse(ch);
        ch.writeOutbound(EMPTY_LAST_CONTENT);
        HttpContent chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("0"));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(HttpContent.class)));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().isReadable(), CoreMatchers.is(false));
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(LastHttpContent.class)));
        chunk.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    /**
     * If the length of the content is 0 for sure, {@link HttpContentEncoder} should skip encoding.
     */
    @Test
    public void testEmptyFullContent() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        ch.writeInbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "/"));
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.EMPTY_BUFFER);
        ch.writeOutbound(res);
        Object o = ch.readOutbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        res = ((FullHttpResponse) (o));
        Assert.assertThat(res.headers().get(TRANSFER_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        // Content encoding shouldn't be modified.
        Assert.assertThat(res.headers().get(CONTENT_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(res.content().readableBytes(), CoreMatchers.is(0));
        Assert.assertThat(res.content().toString(US_ASCII), CoreMatchers.is(""));
        res.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testEmptyFullContentWithTrailer() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        ch.writeInbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "/"));
        FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.EMPTY_BUFFER);
        res.trailingHeaders().set(HttpHeadersTestUtils.of("X-Test"), HttpHeadersTestUtils.of("Netty"));
        ch.writeOutbound(res);
        Object o = ch.readOutbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(FullHttpResponse.class)));
        res = ((FullHttpResponse) (o));
        Assert.assertThat(res.headers().get(TRANSFER_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        // Content encoding shouldn't be modified.
        Assert.assertThat(res.headers().get(CONTENT_ENCODING), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(res.content().readableBytes(), CoreMatchers.is(0));
        Assert.assertThat(res.content().toString(US_ASCII), CoreMatchers.is(""));
        Assert.assertEquals("Netty", res.trailingHeaders().get(HttpHeadersTestUtils.of("X-Test")));
        Assert.assertEquals(SUCCESS, res.decoderResult());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testEmptyHeadResponse() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        HttpRequest req = new DefaultFullHttpRequest(HTTP_1_1, HEAD, "/");
        ch.writeInbound(req);
        HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
        res.headers().set(TRANSFER_ENCODING, CHUNKED);
        ch.writeOutbound(res);
        ch.writeOutbound(EMPTY_LAST_CONTENT);
        HttpContentEncoderTest.assertEmptyResponse(ch);
    }

    @Test
    public void testHttp304Response() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        HttpRequest req = new DefaultFullHttpRequest(HTTP_1_1, GET, "/");
        req.headers().set(ACCEPT_ENCODING, GZIP);
        ch.writeInbound(req);
        HttpResponse res = new DefaultHttpResponse(HTTP_1_1, NOT_MODIFIED);
        res.headers().set(TRANSFER_ENCODING, CHUNKED);
        ch.writeOutbound(res);
        ch.writeOutbound(EMPTY_LAST_CONTENT);
        HttpContentEncoderTest.assertEmptyResponse(ch);
    }

    @Test
    public void testConnect200Response() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        HttpRequest req = new DefaultFullHttpRequest(HTTP_1_1, CONNECT, "google.com:80");
        ch.writeInbound(req);
        HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
        res.headers().set(TRANSFER_ENCODING, CHUNKED);
        ch.writeOutbound(res);
        ch.writeOutbound(EMPTY_LAST_CONTENT);
        HttpContentEncoderTest.assertEmptyResponse(ch);
    }

    @Test
    public void testConnectFailureResponse() throws Exception {
        String content = "Not allowed by configuration";
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        HttpRequest req = new DefaultFullHttpRequest(HTTP_1_1, CONNECT, "google.com:80");
        ch.writeInbound(req);
        HttpResponse res = new DefaultHttpResponse(HTTP_1_1, METHOD_NOT_ALLOWED);
        res.headers().set(TRANSFER_ENCODING, CHUNKED);
        ch.writeOutbound(res);
        ch.writeOutbound(new DefaultHttpContent(Unpooled.wrappedBuffer(content.getBytes(UTF_8))));
        ch.writeOutbound(EMPTY_LAST_CONTENT);
        HttpContentEncoderTest.assertEncodedResponse(ch);
        Object o = ch.readOutbound();
        Assert.assertThat(o, CoreMatchers.is(CoreMatchers.instanceOf(HttpContent.class)));
        HttpContent chunk = ((HttpContent) (o));
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("28"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk.content().isReadable(), CoreMatchers.is(true));
        Assert.assertThat(chunk.content().toString(US_ASCII), CoreMatchers.is("0"));
        chunk.release();
        chunk = ch.readOutbound();
        Assert.assertThat(chunk, CoreMatchers.is(CoreMatchers.instanceOf(LastHttpContent.class)));
        chunk.release();
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testHttp1_0() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpContentEncoderTest.TestEncoder());
        FullHttpRequest req = new DefaultFullHttpRequest(HTTP_1_0, GET, "/");
        Assert.assertTrue(ch.writeInbound(req));
        HttpResponse res = new DefaultHttpResponse(HTTP_1_0, OK);
        res.headers().set(CONTENT_LENGTH, ZERO);
        Assert.assertTrue(ch.writeOutbound(res));
        Assert.assertTrue(ch.writeOutbound(EMPTY_LAST_CONTENT));
        Assert.assertTrue(ch.finish());
        FullHttpRequest request = ch.readInbound();
        Assert.assertTrue(request.release());
        Assert.assertNull(ch.readInbound());
        HttpResponse response = ch.readOutbound();
        Assert.assertSame(res, response);
        LastHttpContent content = ch.readOutbound();
        Assert.assertSame(EMPTY_LAST_CONTENT, content);
        content.release();
        Assert.assertNull(ch.readOutbound());
    }

    @Test
    public void testCleanupThrows() {
        HttpContentEncoder encoder = new HttpContentEncoder() {
            @Override
            protected Result beginEncode(HttpResponse headers, String acceptEncoding) throws Exception {
                return new Result("myencoding", new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        ctx.fireExceptionCaught(new EncoderException());
                        ctx.fireChannelInactive();
                    }
                }));
            }
        };
        final AtomicBoolean channelInactiveCalled = new AtomicBoolean();
        EmbeddedChannel channel = new EmbeddedChannel(encoder, new ChannelInboundHandlerAdapter() {
            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                Assert.assertTrue(channelInactiveCalled.compareAndSet(false, true));
                super.channelInactive(ctx);
            }
        });
        Assert.assertTrue(channel.writeInbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "/")));
        Assert.assertTrue(channel.writeOutbound(new DefaultHttpResponse(HTTP_1_1, OK)));
        HttpContent content = new DefaultHttpContent(Unpooled.buffer().writeZero(10));
        Assert.assertTrue(channel.writeOutbound(content));
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

