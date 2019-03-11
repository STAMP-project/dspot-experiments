/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec.http2;


import HttpConversionUtil.ExtensionHeaderNames.PATH;
import HttpConversionUtil.ExtensionHeaderNames.SCHEME;
import HttpConversionUtil.ExtensionHeaderNames.STREAM_ID;
import HttpHeaderNames.COOKIE;
import HttpHeaderNames.HOST;
import HttpHeaderNames.TRANSFER_ENCODING;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.AsciiString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Testing the {@link HttpToHttp2ConnectionHandler} for {@link FullHttpRequest} objects into HTTP/2 frames
 */
public class HttpToHttp2ConnectionHandlerTest {
    private static final int WAIT_TIME_SECONDS = 5;

    @Mock
    private Http2FrameListener clientListener;

    @Mock
    private Http2FrameListener serverListener;

    private ServerBootstrap sb;

    private Bootstrap cb;

    private Channel serverChannel;

    private volatile Channel serverConnectedChannel;

    private Channel clientChannel;

    private CountDownLatch requestLatch;

    private CountDownLatch serverSettingsAckLatch;

    private CountDownLatch trailersLatch;

    private Http2TestUtil.FrameCountDown serverFrameCountDown;

    @Test
    public void testHeadersOnlyRequest() throws Exception {
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, GET, "http://my-user_name@www.example.org:5555/example");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        httpHeaders.set(HOST, "my-user_name@www.example.org:5555");
        httpHeaders.set(SCHEME.text(), "http");
        httpHeaders.add(Http2TestUtil.of("foo"), Http2TestUtil.of("goo"));
        httpHeaders.add(Http2TestUtil.of("foo"), Http2TestUtil.of("goo2"));
        httpHeaders.add(Http2TestUtil.of("foo2"), Http2TestUtil.of("goo2"));
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/example")).authority(new AsciiString("www.example.org:5555")).scheme(new AsciiString("http")).add(new AsciiString("foo"), new AsciiString("goo")).add(new AsciiString("foo"), new AsciiString("goo2")).add(new AsciiString("foo2"), new AsciiString("goo2"));
        ChannelPromise writePromise = newPromise();
        verifyHeadersOnly(http2Headers, writePromise, clientChannel.writeAndFlush(request, writePromise));
    }

    @Test
    public void testMultipleCookieEntriesAreCombined() throws Exception {
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, GET, "http://my-user_name@www.example.org:5555/example");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        httpHeaders.set(HOST, "my-user_name@www.example.org:5555");
        httpHeaders.set(SCHEME.text(), "http");
        httpHeaders.set(COOKIE, "a=b; c=d; e=f");
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/example")).authority(new AsciiString("www.example.org:5555")).scheme(new AsciiString("http")).add(COOKIE, "a=b").add(COOKIE, "c=d").add(COOKIE, "e=f");
        ChannelPromise writePromise = newPromise();
        verifyHeadersOnly(http2Headers, writePromise, clientChannel.writeAndFlush(request, writePromise));
    }

    @Test
    public void testOriginFormRequestTargetHandled() throws Exception {
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, GET, "/where?q=now&f=then#section1");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        httpHeaders.set(SCHEME.text(), "http");
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/where?q=now&f=then#section1")).scheme(new AsciiString("http"));
        ChannelPromise writePromise = newPromise();
        verifyHeadersOnly(http2Headers, writePromise, clientChannel.writeAndFlush(request, writePromise));
    }

    @Test
    public void testOriginFormRequestTargetHandledFromUrlencodedUri() throws Exception {
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, GET, "/where%2B0?q=now%2B0&f=then%2B0#section1%2B0");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        httpHeaders.set(SCHEME.text(), "http");
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/where%2B0?q=now%2B0&f=then%2B0#section1%2B0")).scheme(new AsciiString("http"));
        ChannelPromise writePromise = newPromise();
        verifyHeadersOnly(http2Headers, writePromise, clientChannel.writeAndFlush(request, writePromise));
    }

    @Test
    public void testAbsoluteFormRequestTargetHandledFromHeaders() throws Exception {
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, GET, "/pub/WWW/TheProject.html");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        httpHeaders.set(HOST, "foouser@www.example.org:5555");
        httpHeaders.set(PATH.text(), "ignored_path");
        httpHeaders.set(SCHEME.text(), "https");
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/pub/WWW/TheProject.html")).authority(new AsciiString("www.example.org:5555")).scheme(new AsciiString("https"));
        ChannelPromise writePromise = newPromise();
        verifyHeadersOnly(http2Headers, writePromise, clientChannel.writeAndFlush(request, writePromise));
    }

    @Test
    public void testAbsoluteFormRequestTargetHandledFromRequestTargetUri() throws Exception {
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, GET, "http://foouser@www.example.org:5555/pub/WWW/TheProject.html");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/pub/WWW/TheProject.html")).authority(new AsciiString("www.example.org:5555")).scheme(new AsciiString("http"));
        ChannelPromise writePromise = newPromise();
        verifyHeadersOnly(http2Headers, writePromise, clientChannel.writeAndFlush(request, writePromise));
    }

    @Test
    public void testAuthorityFormRequestTargetHandled() throws Exception {
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, CONNECT, "http://www.example.com:80");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("CONNECT")).path(new AsciiString("/")).scheme(new AsciiString("http")).authority(new AsciiString("www.example.com:80"));
        ChannelPromise writePromise = newPromise();
        verifyHeadersOnly(http2Headers, writePromise, clientChannel.writeAndFlush(request, writePromise));
    }

    @Test
    public void testAsterikFormRequestTargetHandled() throws Exception {
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, OPTIONS, "*");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        httpHeaders.set(HOST, "www.example.com:80");
        httpHeaders.set(SCHEME.text(), "http");
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("OPTIONS")).path(new AsciiString("*")).scheme(new AsciiString("http")).authority(new AsciiString("www.example.com:80"));
        ChannelPromise writePromise = newPromise();
        verifyHeadersOnly(http2Headers, writePromise, clientChannel.writeAndFlush(request, writePromise));
    }

    @Test
    public void testHostIPv6FormRequestTargetHandled() throws Exception {
        // Valid according to
        // https://tools.ietf.org/html/rfc7230#section-2.7.1 -> https://tools.ietf.org/html/rfc3986#section-3.2.2
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, GET, "/");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        httpHeaders.set(HOST, "[::1]:80");
        httpHeaders.set(SCHEME.text(), "http");
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/")).scheme(new AsciiString("http")).authority(new AsciiString("[::1]:80"));
        ChannelPromise writePromise = newPromise();
        verifyHeadersOnly(http2Headers, writePromise, clientChannel.writeAndFlush(request, writePromise));
    }

    @Test
    public void testHostFormRequestTargetHandled() throws Exception {
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, GET, "/");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        httpHeaders.set(HOST, "localhost:80");
        httpHeaders.set(SCHEME.text(), "http");
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/")).scheme(new AsciiString("http")).authority(new AsciiString("localhost:80"));
        ChannelPromise writePromise = newPromise();
        verifyHeadersOnly(http2Headers, writePromise, clientChannel.writeAndFlush(request, writePromise));
    }

    @Test
    public void testHostIPv4FormRequestTargetHandled() throws Exception {
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, GET, "/");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        httpHeaders.set(HOST, "1.2.3.4:80");
        httpHeaders.set(SCHEME.text(), "http");
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("GET")).path(new AsciiString("/")).scheme(new AsciiString("http")).authority(new AsciiString("1.2.3.4:80"));
        ChannelPromise writePromise = newPromise();
        verifyHeadersOnly(http2Headers, writePromise, clientChannel.writeAndFlush(request, writePromise));
    }

    @Test
    public void testNoSchemeRequestTargetHandled() throws Exception {
        bootstrapEnv(2, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, GET, "/");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.setInt(STREAM_ID.text(), 5);
        httpHeaders.set(HOST, "localhost");
        ChannelPromise writePromise = newPromise();
        ChannelFuture writeFuture = clientChannel.writeAndFlush(request, writePromise);
        Assert.assertTrue(writePromise.awaitUninterruptibly(HttpToHttp2ConnectionHandlerTest.WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(writePromise.isDone());
        Assert.assertFalse(writePromise.isSuccess());
        Assert.assertTrue(writeFuture.isDone());
        Assert.assertFalse(writeFuture.isSuccess());
    }

    @Test
    public void testRequestWithBody() throws Exception {
        final String text = "foooooogoooo";
        final List<String> receivedBuffers = Collections.synchronizedList(new ArrayList<String>());
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock in) throws Throwable {
                receivedBuffers.add(((ByteBuf) (in.getArguments()[2])).toString(UTF_8));
                return null;
            }
        }).when(serverListener).onDataRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
        bootstrapEnv(3, 1, 0);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, POST, "http://your_user-name123@www.example.org:5555/example", Unpooled.copiedBuffer(text, UTF_8));
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.set(HOST, "www.example-origin.org:5555");
        httpHeaders.add(Http2TestUtil.of("foo"), Http2TestUtil.of("goo"));
        httpHeaders.add(Http2TestUtil.of("foo"), Http2TestUtil.of("goo2"));
        httpHeaders.add(Http2TestUtil.of("foo2"), Http2TestUtil.of("goo2"));
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("POST")).path(new AsciiString("/example")).authority(new AsciiString("www.example-origin.org:5555")).scheme(new AsciiString("http")).add(new AsciiString("foo"), new AsciiString("goo")).add(new AsciiString("foo"), new AsciiString("goo2")).add(new AsciiString("foo2"), new AsciiString("goo2"));
        ChannelPromise writePromise = newPromise();
        ChannelFuture writeFuture = clientChannel.writeAndFlush(request, writePromise);
        Assert.assertTrue(writePromise.awaitUninterruptibly(HttpToHttp2ConnectionHandlerTest.WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(writePromise.isSuccess());
        Assert.assertTrue(writeFuture.awaitUninterruptibly(HttpToHttp2ConnectionHandlerTest.WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(writeFuture.isSuccess());
        awaitRequests();
        Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(http2Headers), ArgumentMatchers.eq(0), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
        Mockito.verify(serverListener).onDataRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
        Assert.assertEquals(1, receivedBuffers.size());
        Assert.assertEquals(text, receivedBuffers.get(0));
    }

    @Test
    public void testRequestWithBodyAndTrailingHeaders() throws Exception {
        final String text = "foooooogoooo";
        final List<String> receivedBuffers = Collections.synchronizedList(new ArrayList<String>());
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock in) throws Throwable {
                receivedBuffers.add(((ByteBuf) (in.getArguments()[2])).toString(UTF_8));
                return null;
            }
        }).when(serverListener).onDataRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
        bootstrapEnv(4, 1, 1);
        final FullHttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, POST, "http://your_user-name123@www.example.org:5555/example", Unpooled.copiedBuffer(text, UTF_8));
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.set(HOST, "www.example.org:5555");
        httpHeaders.add(Http2TestUtil.of("foo"), Http2TestUtil.of("goo"));
        httpHeaders.add(Http2TestUtil.of("foo"), Http2TestUtil.of("goo2"));
        httpHeaders.add(Http2TestUtil.of("foo2"), Http2TestUtil.of("goo2"));
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("POST")).path(new AsciiString("/example")).authority(new AsciiString("www.example.org:5555")).scheme(new AsciiString("http")).add(new AsciiString("foo"), new AsciiString("goo")).add(new AsciiString("foo"), new AsciiString("goo2")).add(new AsciiString("foo2"), new AsciiString("goo2"));
        request.trailingHeaders().add(Http2TestUtil.of("trailing"), Http2TestUtil.of("bar"));
        final Http2Headers http2TrailingHeaders = new DefaultHttp2Headers().add(new AsciiString("trailing"), new AsciiString("bar"));
        ChannelPromise writePromise = newPromise();
        ChannelFuture writeFuture = clientChannel.writeAndFlush(request, writePromise);
        Assert.assertTrue(writePromise.awaitUninterruptibly(HttpToHttp2ConnectionHandlerTest.WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(writePromise.isSuccess());
        Assert.assertTrue(writeFuture.awaitUninterruptibly(HttpToHttp2ConnectionHandlerTest.WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(writeFuture.isSuccess());
        awaitRequests();
        Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(http2Headers), ArgumentMatchers.eq(0), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
        Mockito.verify(serverListener).onDataRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
        Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(http2TrailingHeaders), ArgumentMatchers.eq(0), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
        Assert.assertEquals(1, receivedBuffers.size());
        Assert.assertEquals(text, receivedBuffers.get(0));
    }

    @Test
    public void testChunkedRequestWithBodyAndTrailingHeaders() throws Exception {
        final String text = "foooooo";
        final String text2 = "goooo";
        final List<String> receivedBuffers = Collections.synchronizedList(new ArrayList<String>());
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock in) throws Throwable {
                receivedBuffers.add(((ByteBuf) (in.getArguments()[2])).toString(UTF_8));
                return null;
            }
        }).when(serverListener).onDataRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
        bootstrapEnv(4, 1, 1);
        final HttpRequest request = new io.netty.handler.codec.http.DefaultHttpRequest(HTTP_1_1, POST, "http://your_user-name123@www.example.org:5555/example");
        final HttpHeaders httpHeaders = request.headers();
        httpHeaders.set(HOST, "www.example.org:5555");
        httpHeaders.add(TRANSFER_ENCODING, "chunked");
        httpHeaders.add(Http2TestUtil.of("foo"), Http2TestUtil.of("goo"));
        httpHeaders.add(Http2TestUtil.of("foo"), Http2TestUtil.of("goo2"));
        httpHeaders.add(Http2TestUtil.of("foo2"), Http2TestUtil.of("goo2"));
        final Http2Headers http2Headers = new DefaultHttp2Headers().method(new AsciiString("POST")).path(new AsciiString("/example")).authority(new AsciiString("www.example.org:5555")).scheme(new AsciiString("http")).add(new AsciiString("foo"), new AsciiString("goo")).add(new AsciiString("foo"), new AsciiString("goo2")).add(new AsciiString("foo2"), new AsciiString("goo2"));
        final DefaultHttpContent httpContent = new DefaultHttpContent(Unpooled.copiedBuffer(text, UTF_8));
        final LastHttpContent lastHttpContent = new io.netty.handler.codec.http.DefaultLastHttpContent(Unpooled.copiedBuffer(text2, UTF_8));
        lastHttpContent.trailingHeaders().add(Http2TestUtil.of("trailing"), Http2TestUtil.of("bar"));
        final Http2Headers http2TrailingHeaders = new DefaultHttp2Headers().add(new AsciiString("trailing"), new AsciiString("bar"));
        ChannelPromise writePromise = newPromise();
        ChannelFuture writeFuture = clientChannel.write(request, writePromise);
        ChannelPromise contentPromise = newPromise();
        ChannelFuture contentFuture = clientChannel.write(httpContent, contentPromise);
        ChannelPromise lastContentPromise = newPromise();
        ChannelFuture lastContentFuture = clientChannel.write(lastHttpContent, lastContentPromise);
        clientChannel.flush();
        Assert.assertTrue(writePromise.awaitUninterruptibly(HttpToHttp2ConnectionHandlerTest.WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(writePromise.isSuccess());
        Assert.assertTrue(writeFuture.awaitUninterruptibly(HttpToHttp2ConnectionHandlerTest.WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(writeFuture.isSuccess());
        Assert.assertTrue(contentPromise.awaitUninterruptibly(HttpToHttp2ConnectionHandlerTest.WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(contentPromise.isSuccess());
        Assert.assertTrue(contentFuture.awaitUninterruptibly(HttpToHttp2ConnectionHandlerTest.WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(contentFuture.isSuccess());
        Assert.assertTrue(lastContentPromise.awaitUninterruptibly(HttpToHttp2ConnectionHandlerTest.WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(lastContentPromise.isSuccess());
        Assert.assertTrue(lastContentFuture.awaitUninterruptibly(HttpToHttp2ConnectionHandlerTest.WAIT_TIME_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(lastContentFuture.isSuccess());
        awaitRequests();
        Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(http2Headers), ArgumentMatchers.eq(0), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
        Mockito.verify(serverListener).onDataRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.any(ByteBuf.class), ArgumentMatchers.eq(0), ArgumentMatchers.eq(false));
        Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(http2TrailingHeaders), ArgumentMatchers.eq(0), ArgumentMatchers.anyShort(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
        Assert.assertEquals(1, receivedBuffers.size());
        Assert.assertEquals((text + text2), receivedBuffers.get(0));
    }
}

