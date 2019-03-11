/**
 * Copyright 2017 The Netty Project
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
package io.netty.handler.codec.http2;


import ByteBufAllocator.DEFAULT;
import CharsetUtil.UTF_8;
import HttpMethod.GET;
import HttpResponseStatus.CONTINUE;
import HttpResponseStatus.OK;
import HttpScheme.HTTP;
import HttpVersion.HTTP_1_1;
import SslProvider.JDK;
import Unpooled.EMPTY_BUFFER;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class Http2StreamFrameToHttpObjectCodecTest {
    @Test
    public void testUpgradeEmptyFullResponse() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        Assert.assertTrue(ch.writeOutbound(new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)));
        Http2HeadersFrame headersFrame = ch.readOutbound();
        Assert.assertThat(headersFrame.headers().status().toString(), CoreMatchers.is("200"));
        Assert.assertTrue(headersFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void encode100ContinueAsHttp2HeadersFrameThatIsNotEndStream() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        Assert.assertTrue(ch.writeOutbound(new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE)));
        Http2HeadersFrame headersFrame = ch.readOutbound();
        Assert.assertThat(headersFrame.headers().status().toString(), CoreMatchers.is("100"));
        Assert.assertFalse(headersFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test(expected = EncoderException.class)
    public void encodeNonFullHttpResponse100ContinueIsRejected() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        try {
            ch.writeOutbound(new io.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        } finally {
            ch.finishAndReleaseAll();
        }
    }

    @Test
    public void testUpgradeNonEmptyFullResponse() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        Assert.assertTrue(ch.writeOutbound(new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, hello)));
        Http2HeadersFrame headersFrame = ch.readOutbound();
        Assert.assertThat(headersFrame.headers().status().toString(), CoreMatchers.is("200"));
        Assert.assertFalse(headersFrame.isEndStream());
        Http2DataFrame dataFrame = ch.readOutbound();
        try {
            Assert.assertThat(dataFrame.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertTrue(dataFrame.isEndStream());
        } finally {
            dataFrame.release();
        }
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testUpgradeEmptyFullResponseWithTrailers() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        FullHttpResponse response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpHeaders trailers = response.trailingHeaders();
        trailers.set("key", "value");
        Assert.assertTrue(ch.writeOutbound(response));
        Http2HeadersFrame headersFrame = ch.readOutbound();
        Assert.assertThat(headersFrame.headers().status().toString(), CoreMatchers.is("200"));
        Assert.assertFalse(headersFrame.isEndStream());
        Http2HeadersFrame trailersFrame = ch.readOutbound();
        Assert.assertThat(trailersFrame.headers().get("key").toString(), CoreMatchers.is("value"));
        Assert.assertTrue(trailersFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testUpgradeNonEmptyFullResponseWithTrailers() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        FullHttpResponse response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, hello);
        HttpHeaders trailers = response.trailingHeaders();
        trailers.set("key", "value");
        Assert.assertTrue(ch.writeOutbound(response));
        Http2HeadersFrame headersFrame = ch.readOutbound();
        Assert.assertThat(headersFrame.headers().status().toString(), CoreMatchers.is("200"));
        Assert.assertFalse(headersFrame.isEndStream());
        Http2DataFrame dataFrame = ch.readOutbound();
        try {
            Assert.assertThat(dataFrame.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertFalse(dataFrame.isEndStream());
        } finally {
            dataFrame.release();
        }
        Http2HeadersFrame trailersFrame = ch.readOutbound();
        Assert.assertThat(trailersFrame.headers().get("key").toString(), CoreMatchers.is("value"));
        Assert.assertTrue(trailersFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testUpgradeHeaders() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        HttpResponse response = new io.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        Assert.assertTrue(ch.writeOutbound(response));
        Http2HeadersFrame headersFrame = ch.readOutbound();
        Assert.assertThat(headersFrame.headers().status().toString(), CoreMatchers.is("200"));
        Assert.assertFalse(headersFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testUpgradeChunk() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        HttpContent content = new io.netty.handler.codec.http.DefaultHttpContent(hello);
        Assert.assertTrue(ch.writeOutbound(content));
        Http2DataFrame dataFrame = ch.readOutbound();
        try {
            Assert.assertThat(dataFrame.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertFalse(dataFrame.isEndStream());
        } finally {
            dataFrame.release();
        }
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testUpgradeEmptyEnd() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        LastHttpContent end = LastHttpContent.EMPTY_LAST_CONTENT;
        Assert.assertTrue(ch.writeOutbound(end));
        Http2DataFrame emptyFrame = ch.readOutbound();
        try {
            Assert.assertThat(emptyFrame.content().readableBytes(), CoreMatchers.is(0));
            Assert.assertTrue(emptyFrame.isEndStream());
        } finally {
            emptyFrame.release();
        }
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testUpgradeDataEnd() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        LastHttpContent end = new io.netty.handler.codec.http.DefaultLastHttpContent(hello, true);
        Assert.assertTrue(ch.writeOutbound(end));
        Http2DataFrame dataFrame = ch.readOutbound();
        try {
            Assert.assertThat(dataFrame.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertTrue(dataFrame.isEndStream());
        } finally {
            dataFrame.release();
        }
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testUpgradeTrailers() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        LastHttpContent trailers = new io.netty.handler.codec.http.DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, true);
        HttpHeaders headers = trailers.trailingHeaders();
        headers.set("key", "value");
        Assert.assertTrue(ch.writeOutbound(trailers));
        Http2HeadersFrame headerFrame = ch.readOutbound();
        Assert.assertThat(headerFrame.headers().get("key").toString(), CoreMatchers.is("value"));
        Assert.assertTrue(headerFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testUpgradeDataEndWithTrailers() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        LastHttpContent trailers = new io.netty.handler.codec.http.DefaultLastHttpContent(hello, true);
        HttpHeaders headers = trailers.trailingHeaders();
        headers.set("key", "value");
        Assert.assertTrue(ch.writeOutbound(trailers));
        Http2DataFrame dataFrame = ch.readOutbound();
        try {
            Assert.assertThat(dataFrame.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertFalse(dataFrame.isEndStream());
        } finally {
            dataFrame.release();
        }
        Http2HeadersFrame headerFrame = ch.readOutbound();
        Assert.assertThat(headerFrame.headers().get("key").toString(), CoreMatchers.is("value"));
        Assert.assertTrue(headerFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDowngradeHeaders() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        Http2Headers headers = new DefaultHttp2Headers();
        headers.path("/");
        headers.method("GET");
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2HeadersFrame(headers)));
        HttpRequest request = ch.readInbound();
        Assert.assertThat(request.uri(), CoreMatchers.is("/"));
        Assert.assertThat(request.method(), CoreMatchers.is(GET));
        Assert.assertThat(request.protocolVersion(), CoreMatchers.is(HTTP_1_1));
        Assert.assertFalse((request instanceof FullHttpRequest));
        Assert.assertTrue(HttpUtil.isTransferEncodingChunked(request));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDowngradeHeadersWithContentLength() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        Http2Headers headers = new DefaultHttp2Headers();
        headers.path("/");
        headers.method("GET");
        headers.setInt("content-length", 0);
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2HeadersFrame(headers)));
        HttpRequest request = ch.readInbound();
        Assert.assertThat(request.uri(), CoreMatchers.is("/"));
        Assert.assertThat(request.method(), CoreMatchers.is(GET));
        Assert.assertThat(request.protocolVersion(), CoreMatchers.is(HTTP_1_1));
        Assert.assertFalse((request instanceof FullHttpRequest));
        Assert.assertFalse(HttpUtil.isTransferEncodingChunked(request));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDowngradeFullHeaders() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        Http2Headers headers = new DefaultHttp2Headers();
        headers.path("/");
        headers.method("GET");
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2HeadersFrame(headers, true)));
        FullHttpRequest request = ch.readInbound();
        try {
            Assert.assertThat(request.uri(), CoreMatchers.is("/"));
            Assert.assertThat(request.method(), CoreMatchers.is(GET));
            Assert.assertThat(request.protocolVersion(), CoreMatchers.is(HTTP_1_1));
            Assert.assertThat(request.content().readableBytes(), CoreMatchers.is(0));
            Assert.assertTrue(request.trailingHeaders().isEmpty());
            Assert.assertFalse(HttpUtil.isTransferEncodingChunked(request));
        } finally {
            request.release();
        }
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDowngradeTrailers() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        Http2Headers headers = new DefaultHttp2Headers();
        headers.set("key", "value");
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2HeadersFrame(headers, true)));
        LastHttpContent trailers = ch.readInbound();
        try {
            Assert.assertThat(trailers.content().readableBytes(), CoreMatchers.is(0));
            Assert.assertThat(trailers.trailingHeaders().get("key"), CoreMatchers.is("value"));
            Assert.assertFalse((trailers instanceof FullHttpRequest));
        } finally {
            trailers.release();
        }
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDowngradeData() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2DataFrame(hello)));
        HttpContent content = ch.readInbound();
        try {
            Assert.assertThat(content.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertFalse((content instanceof LastHttpContent));
        } finally {
            content.release();
        }
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDowngradeEndData() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2DataFrame(hello, true)));
        LastHttpContent content = ch.readInbound();
        try {
            Assert.assertThat(content.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertTrue(content.trailingHeaders().isEmpty());
        } finally {
            content.release();
        }
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testPassThroughOther() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(true));
        Http2ResetFrame reset = new DefaultHttp2ResetFrame(0);
        Http2GoAwayFrame goaway = new DefaultHttp2GoAwayFrame(0);
        Assert.assertTrue(ch.writeInbound(reset));
        Assert.assertTrue(ch.writeInbound(goaway.retain()));
        Assert.assertEquals(reset, ch.readInbound());
        Http2GoAwayFrame frame = ch.readInbound();
        try {
            Assert.assertEquals(goaway, frame);
            Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
            Assert.assertFalse(ch.finish());
        } finally {
            goaway.release();
            frame.release();
        }
    }

    // client-specific tests
    @Test
    public void testEncodeEmptyFullRequest() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        Assert.assertTrue(ch.writeOutbound(new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/hello/world")));
        Http2HeadersFrame headersFrame = ch.readOutbound();
        Http2Headers headers = headersFrame.headers();
        Assert.assertThat(headers.scheme().toString(), CoreMatchers.is("http"));
        Assert.assertThat(headers.method().toString(), CoreMatchers.is("GET"));
        Assert.assertThat(headers.path().toString(), CoreMatchers.is("/hello/world"));
        Assert.assertTrue(headersFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testEncodeHttpsSchemeWhenSslHandlerExists() throws Exception {
        final Queue<Http2StreamFrame> frames = new ConcurrentLinkedQueue<Http2StreamFrame>();
        final SslContext ctx = SslContextBuilder.forClient().sslProvider(JDK).build();
        EmbeddedChannel ch = new EmbeddedChannel(ctx.newHandler(DEFAULT), new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                if (msg instanceof Http2StreamFrame) {
                    frames.add(((Http2StreamFrame) (msg)));
                    ctx.write(EMPTY_BUFFER, promise);
                } else {
                    ctx.write(msg, promise);
                }
            }
        }, new Http2StreamFrameToHttpObjectCodec(false));
        try {
            FullHttpRequest req = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/hello/world");
            Assert.assertTrue(ch.writeOutbound(req));
            ch.finishAndReleaseAll();
            Http2HeadersFrame headersFrame = ((Http2HeadersFrame) (frames.poll()));
            Http2Headers headers = headersFrame.headers();
            Assert.assertThat(headers.scheme().toString(), CoreMatchers.is("https"));
            Assert.assertThat(headers.method().toString(), CoreMatchers.is("GET"));
            Assert.assertThat(headers.path().toString(), CoreMatchers.is("/hello/world"));
            Assert.assertTrue(headersFrame.isEndStream());
            Assert.assertNull(frames.poll());
        } finally {
            ch.finishAndReleaseAll();
        }
    }

    @Test
    public void testEncodeNonEmptyFullRequest() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        Assert.assertTrue(ch.writeOutbound(new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, "/hello/world", hello)));
        Http2HeadersFrame headersFrame = ch.readOutbound();
        Http2Headers headers = headersFrame.headers();
        Assert.assertThat(headers.scheme().toString(), CoreMatchers.is("http"));
        Assert.assertThat(headers.method().toString(), CoreMatchers.is("PUT"));
        Assert.assertThat(headers.path().toString(), CoreMatchers.is("/hello/world"));
        Assert.assertFalse(headersFrame.isEndStream());
        Http2DataFrame dataFrame = ch.readOutbound();
        try {
            Assert.assertThat(dataFrame.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertTrue(dataFrame.isEndStream());
        } finally {
            dataFrame.release();
        }
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testEncodeEmptyFullRequestWithTrailers() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, "/hello/world");
        HttpHeaders trailers = request.trailingHeaders();
        trailers.set("key", "value");
        Assert.assertTrue(ch.writeOutbound(request));
        Http2HeadersFrame headersFrame = ch.readOutbound();
        Http2Headers headers = headersFrame.headers();
        Assert.assertThat(headers.scheme().toString(), CoreMatchers.is("http"));
        Assert.assertThat(headers.method().toString(), CoreMatchers.is("PUT"));
        Assert.assertThat(headers.path().toString(), CoreMatchers.is("/hello/world"));
        Assert.assertFalse(headersFrame.isEndStream());
        Http2HeadersFrame trailersFrame = ch.readOutbound();
        Assert.assertThat(trailersFrame.headers().get("key").toString(), CoreMatchers.is("value"));
        Assert.assertTrue(trailersFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testEncodeNonEmptyFullRequestWithTrailers() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, "/hello/world", hello);
        HttpHeaders trailers = request.trailingHeaders();
        trailers.set("key", "value");
        Assert.assertTrue(ch.writeOutbound(request));
        Http2HeadersFrame headersFrame = ch.readOutbound();
        Http2Headers headers = headersFrame.headers();
        Assert.assertThat(headers.scheme().toString(), CoreMatchers.is("http"));
        Assert.assertThat(headers.method().toString(), CoreMatchers.is("PUT"));
        Assert.assertThat(headers.path().toString(), CoreMatchers.is("/hello/world"));
        Assert.assertFalse(headersFrame.isEndStream());
        Http2DataFrame dataFrame = ch.readOutbound();
        try {
            Assert.assertThat(dataFrame.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertFalse(dataFrame.isEndStream());
        } finally {
            dataFrame.release();
        }
        Http2HeadersFrame trailersFrame = ch.readOutbound();
        Assert.assertThat(trailersFrame.headers().get("key").toString(), CoreMatchers.is("value"));
        Assert.assertTrue(trailersFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testEncodeRequestHeaders() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        HttpRequest request = new io.netty.handler.codec.http.DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/hello/world");
        Assert.assertTrue(ch.writeOutbound(request));
        Http2HeadersFrame headersFrame = ch.readOutbound();
        Http2Headers headers = headersFrame.headers();
        Assert.assertThat(headers.scheme().toString(), CoreMatchers.is("http"));
        Assert.assertThat(headers.method().toString(), CoreMatchers.is("GET"));
        Assert.assertThat(headers.path().toString(), CoreMatchers.is("/hello/world"));
        Assert.assertFalse(headersFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testEncodeChunkAsClient() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        HttpContent content = new io.netty.handler.codec.http.DefaultHttpContent(hello);
        Assert.assertTrue(ch.writeOutbound(content));
        Http2DataFrame dataFrame = ch.readOutbound();
        try {
            Assert.assertThat(dataFrame.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertFalse(dataFrame.isEndStream());
        } finally {
            dataFrame.release();
        }
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testEncodeEmptyEndAsClient() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        LastHttpContent end = LastHttpContent.EMPTY_LAST_CONTENT;
        Assert.assertTrue(ch.writeOutbound(end));
        Http2DataFrame emptyFrame = ch.readOutbound();
        try {
            Assert.assertThat(emptyFrame.content().readableBytes(), CoreMatchers.is(0));
            Assert.assertTrue(emptyFrame.isEndStream());
        } finally {
            emptyFrame.release();
        }
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testEncodeDataEndAsClient() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        LastHttpContent end = new io.netty.handler.codec.http.DefaultLastHttpContent(hello, true);
        Assert.assertTrue(ch.writeOutbound(end));
        Http2DataFrame dataFrame = ch.readOutbound();
        try {
            Assert.assertThat(dataFrame.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertTrue(dataFrame.isEndStream());
        } finally {
            dataFrame.release();
        }
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testEncodeTrailersAsClient() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        LastHttpContent trailers = new io.netty.handler.codec.http.DefaultLastHttpContent(Unpooled.EMPTY_BUFFER, true);
        HttpHeaders headers = trailers.trailingHeaders();
        headers.set("key", "value");
        Assert.assertTrue(ch.writeOutbound(trailers));
        Http2HeadersFrame headerFrame = ch.readOutbound();
        Assert.assertThat(headerFrame.headers().get("key").toString(), CoreMatchers.is("value"));
        Assert.assertTrue(headerFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testEncodeDataEndWithTrailersAsClient() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        LastHttpContent trailers = new io.netty.handler.codec.http.DefaultLastHttpContent(hello, true);
        HttpHeaders headers = trailers.trailingHeaders();
        headers.set("key", "value");
        Assert.assertTrue(ch.writeOutbound(trailers));
        Http2DataFrame dataFrame = ch.readOutbound();
        try {
            Assert.assertThat(dataFrame.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertFalse(dataFrame.isEndStream());
        } finally {
            dataFrame.release();
        }
        Http2HeadersFrame headerFrame = ch.readOutbound();
        Assert.assertThat(headerFrame.headers().get("key").toString(), CoreMatchers.is("value"));
        Assert.assertTrue(headerFrame.isEndStream());
        Assert.assertThat(ch.readOutbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void decode100ContinueHttp2HeadersAsFullHttpResponse() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        Http2Headers headers = new DefaultHttp2Headers();
        headers.scheme(HTTP.name());
        headers.status(CONTINUE.codeAsText());
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2HeadersFrame(headers, false)));
        final FullHttpResponse response = ch.readInbound();
        try {
            Assert.assertThat(response.status(), CoreMatchers.is(CONTINUE));
            Assert.assertThat(response.protocolVersion(), CoreMatchers.is(HTTP_1_1));
        } finally {
            response.release();
        }
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDecodeResponseHeaders() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        Http2Headers headers = new DefaultHttp2Headers();
        headers.scheme(HTTP.name());
        headers.status(OK.codeAsText());
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2HeadersFrame(headers)));
        HttpResponse response = ch.readInbound();
        Assert.assertThat(response.status(), CoreMatchers.is(OK));
        Assert.assertThat(response.protocolVersion(), CoreMatchers.is(HTTP_1_1));
        Assert.assertFalse((response instanceof FullHttpResponse));
        Assert.assertTrue(HttpUtil.isTransferEncodingChunked(response));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDecodeResponseHeadersWithContentLength() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        Http2Headers headers = new DefaultHttp2Headers();
        headers.scheme(HTTP.name());
        headers.status(OK.codeAsText());
        headers.setInt("content-length", 0);
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2HeadersFrame(headers)));
        HttpResponse response = ch.readInbound();
        Assert.assertThat(response.status(), CoreMatchers.is(OK));
        Assert.assertThat(response.protocolVersion(), CoreMatchers.is(HTTP_1_1));
        Assert.assertFalse((response instanceof FullHttpResponse));
        Assert.assertFalse(HttpUtil.isTransferEncodingChunked(response));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDecodeFullResponseHeaders() throws Exception {
        testDecodeFullResponseHeaders(false);
    }

    @Test
    public void testDecodeFullResponseHeadersWithStreamID() throws Exception {
        testDecodeFullResponseHeaders(true);
    }

    @Test
    public void testDecodeResponseTrailersAsClient() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        Http2Headers headers = new DefaultHttp2Headers();
        headers.set("key", "value");
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2HeadersFrame(headers, true)));
        LastHttpContent trailers = ch.readInbound();
        try {
            Assert.assertThat(trailers.content().readableBytes(), CoreMatchers.is(0));
            Assert.assertThat(trailers.trailingHeaders().get("key"), CoreMatchers.is("value"));
            Assert.assertFalse((trailers instanceof FullHttpRequest));
        } finally {
            trailers.release();
        }
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDecodeDataAsClient() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2DataFrame(hello)));
        HttpContent content = ch.readInbound();
        try {
            Assert.assertThat(content.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertFalse((content instanceof LastHttpContent));
        } finally {
            content.release();
        }
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testDecodeEndDataAsClient() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        ByteBuf hello = Unpooled.copiedBuffer("hello world", UTF_8);
        Assert.assertTrue(ch.writeInbound(new DefaultHttp2DataFrame(hello, true)));
        LastHttpContent content = ch.readInbound();
        try {
            Assert.assertThat(content.content().toString(UTF_8), CoreMatchers.is("hello world"));
            Assert.assertTrue(content.trailingHeaders().isEmpty());
        } finally {
            content.release();
        }
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertFalse(ch.finish());
    }

    @Test
    public void testPassThroughOtherAsClient() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new Http2StreamFrameToHttpObjectCodec(false));
        Http2ResetFrame reset = new DefaultHttp2ResetFrame(0);
        Http2GoAwayFrame goaway = new DefaultHttp2GoAwayFrame(0);
        Assert.assertTrue(ch.writeInbound(reset));
        Assert.assertTrue(ch.writeInbound(goaway.retain()));
        Assert.assertEquals(reset, ch.readInbound());
        Http2GoAwayFrame frame = ch.readInbound();
        try {
            Assert.assertEquals(goaway, frame);
            Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
            Assert.assertFalse(ch.finish());
        } finally {
            goaway.release();
            frame.release();
        }
    }

    @Test
    public void testIsSharableBetweenChannels() throws Exception {
        final Queue<Http2StreamFrame> frames = new ConcurrentLinkedQueue<Http2StreamFrame>();
        final ChannelHandler sharedHandler = new Http2StreamFrameToHttpObjectCodec(false);
        final SslContext ctx = SslContextBuilder.forClient().sslProvider(JDK).build();
        EmbeddedChannel tlsCh = new EmbeddedChannel(ctx.newHandler(DEFAULT), new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                if (msg instanceof Http2StreamFrame) {
                    frames.add(((Http2StreamFrame) (msg)));
                    promise.setSuccess();
                } else {
                    ctx.write(msg, promise);
                }
            }
        }, sharedHandler);
        EmbeddedChannel plaintextCh = new EmbeddedChannel(new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                if (msg instanceof Http2StreamFrame) {
                    frames.add(((Http2StreamFrame) (msg)));
                    promise.setSuccess();
                } else {
                    ctx.write(msg, promise);
                }
            }
        }, sharedHandler);
        FullHttpRequest req = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/hello/world");
        Assert.assertTrue(tlsCh.writeOutbound(req));
        Assert.assertTrue(tlsCh.finishAndReleaseAll());
        Http2HeadersFrame headersFrame = ((Http2HeadersFrame) (frames.poll()));
        Http2Headers headers = headersFrame.headers();
        Assert.assertThat(headers.scheme().toString(), CoreMatchers.is("https"));
        Assert.assertThat(headers.method().toString(), CoreMatchers.is("GET"));
        Assert.assertThat(headers.path().toString(), CoreMatchers.is("/hello/world"));
        Assert.assertTrue(headersFrame.isEndStream());
        Assert.assertNull(frames.poll());
        // Run the plaintext channel
        req = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/hello/world");
        Assert.assertFalse(plaintextCh.writeOutbound(req));
        Assert.assertFalse(plaintextCh.finishAndReleaseAll());
        headersFrame = ((Http2HeadersFrame) (frames.poll()));
        headers = headersFrame.headers();
        Assert.assertThat(headers.scheme().toString(), CoreMatchers.is("http"));
        Assert.assertThat(headers.method().toString(), CoreMatchers.is("GET"));
        Assert.assertThat(headers.path().toString(), CoreMatchers.is("/hello/world"));
        Assert.assertTrue(headersFrame.isEndStream());
        Assert.assertNull(frames.poll());
    }
}

