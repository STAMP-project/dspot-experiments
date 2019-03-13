/**
 * Copyright 2012 The Netty Project
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


import ChannelOption.ALLOW_HALF_CLOSURE;
import CharsetUtil.ISO_8859_1;
import HttpHeaderNames.CONNECTION;
import HttpHeaderValues.UPGRADE;
import HttpMethod.CONNECT;
import HttpMethod.GET;
import HttpResponseStatus.SWITCHING_PROTOCOLS;
import HttpVersion.HTTP_1_1;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.util.NetUtil;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static HttpMethod.GET;
import static HttpVersion.HTTP_1_1;


public class HttpClientCodecTest {
    private static final String EMPTY_RESPONSE = "HTTP/1.0 200 OK\r\nContent-Length: 0\r\n\r\n";

    private static final String RESPONSE = "HTTP/1.0 200 OK\r\n" + (((("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n" + "Content-Type: text/html\r\n") + "Content-Length: 28\r\n") + "\r\n") + "<html><body></body></html>\r\n");

    private static final String INCOMPLETE_CHUNKED_RESPONSE = "HTTP/1.1 200 OK\r\n" + ((((((("Content-Type: text/plain\r\n" + "Transfer-Encoding: chunked\r\n") + "\r\n") + "5\r\n") + "first\r\n") + "6\r\n") + "second\r\n") + "0\r\n");

    private static final String CHUNKED_RESPONSE = (HttpClientCodecTest.INCOMPLETE_CHUNKED_RESPONSE) + "\r\n";

    @Test
    public void testConnectWithResponseContent() {
        HttpClientCodec codec = new HttpClientCodec(4096, 8192, 8192, true);
        EmbeddedChannel ch = new EmbeddedChannel(codec);
        HttpClientCodecTest.sendRequestAndReadResponse(ch, CONNECT, HttpClientCodecTest.RESPONSE);
        ch.finish();
    }

    @Test
    public void testFailsNotOnRequestResponseChunked() {
        HttpClientCodec codec = new HttpClientCodec(4096, 8192, 8192, true);
        EmbeddedChannel ch = new EmbeddedChannel(codec);
        HttpClientCodecTest.sendRequestAndReadResponse(ch, GET, HttpClientCodecTest.CHUNKED_RESPONSE);
        ch.finish();
    }

    @Test
    public void testFailsOnMissingResponse() {
        HttpClientCodec codec = new HttpClientCodec(4096, 8192, 8192, true);
        EmbeddedChannel ch = new EmbeddedChannel(codec);
        Assert.assertTrue(ch.writeOutbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "http://localhost/")));
        ByteBuf buffer = ch.readOutbound();
        Assert.assertNotNull(buffer);
        buffer.release();
        try {
            ch.finish();
            Assert.fail();
        } catch (CodecException e) {
            Assert.assertTrue((e instanceof PrematureChannelClosureException));
        }
    }

    @Test
    public void testFailsOnIncompleteChunkedResponse() {
        HttpClientCodec codec = new HttpClientCodec(4096, 8192, 8192, true);
        EmbeddedChannel ch = new EmbeddedChannel(codec);
        ch.writeOutbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "http://localhost/"));
        ByteBuf buffer = ch.readOutbound();
        Assert.assertNotNull(buffer);
        buffer.release();
        Assert.assertNull(ch.readInbound());
        ch.writeInbound(Unpooled.copiedBuffer(HttpClientCodecTest.INCOMPLETE_CHUNKED_RESPONSE, ISO_8859_1));
        Assert.assertThat(ch.readInbound(), CoreMatchers.instanceOf(HttpResponse.class));
        release();// Chunk 'first'

        release();// Chunk 'second'

        Assert.assertNull(ch.readInbound());
        try {
            ch.finish();
            Assert.fail();
        } catch (CodecException e) {
            Assert.assertTrue((e instanceof PrematureChannelClosureException));
        }
    }

    @Test
    public void testServerCloseSocketInputProvidesData() throws InterruptedException {
        ServerBootstrap sb = new ServerBootstrap();
        Bootstrap cb = new Bootstrap();
        final CountDownLatch serverChannelLatch = new CountDownLatch(1);
        final CountDownLatch responseReceivedLatch = new CountDownLatch(1);
        try {
            sb.group(new NioEventLoopGroup(2));
            sb.channel(NioServerSocketChannel.class);
            sb.childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    // Don't use the HttpServerCodec, because we don't want to have content-length or anything added.
                    ch.pipeline().addLast(new HttpRequestDecoder(4096, 8192, 8192, true));
                    ch.pipeline().addLast(new HttpObjectAggregator(4096));
                    ch.pipeline().addLast(new io.netty.channel.SimpleChannelInboundHandler<FullHttpRequest>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
                            // This is just a simple demo...don't block in IO
                            Assert.assertTrue(((ctx.channel()) instanceof SocketChannel));
                            final SocketChannel sChannel = ((SocketChannel) (ctx.channel()));
                            /**
                             * The point of this test is to not add any content-length or content-encoding headers
                             * and the client should still handle this.
                             * See <a href="https://tools.ietf.org/html/rfc7230#section-3.3.3">RFC 7230, 3.3.3</a>.
                             */
                            sChannel.writeAndFlush(Unpooled.wrappedBuffer(("HTTP/1.0 200 OK\r\n" + ("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n" + "Content-Type: text/html\r\n\r\n")).getBytes(ISO_8859_1))).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    Assert.assertTrue(future.isSuccess());
                                    sChannel.writeAndFlush(Unpooled.wrappedBuffer("<html><body>hello half closed!</body></html>\r\n".getBytes(ISO_8859_1))).addListener(new ChannelFutureListener() {
                                        @Override
                                        public void operationComplete(ChannelFuture future) throws Exception {
                                            Assert.assertTrue(future.isSuccess());
                                            sChannel.shutdownOutput();
                                        }
                                    });
                                }
                            });
                        }
                    });
                    serverChannelLatch.countDown();
                }
            });
            cb.group(new NioEventLoopGroup(1));
            cb.channel(NioSocketChannel.class);
            cb.option(ALLOW_HALF_CLOSURE, true);
            cb.handler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new HttpClientCodec(4096, 8192, 8192, true, true));
                    ch.pipeline().addLast(new HttpObjectAggregator(4096));
                    ch.pipeline().addLast(new io.netty.channel.SimpleChannelInboundHandler<FullHttpResponse>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
                            responseReceivedLatch.countDown();
                        }
                    });
                }
            });
            Channel serverChannel = sb.bind(new InetSocketAddress(0)).sync().channel();
            int port = ((InetSocketAddress) (serverChannel.localAddress())).getPort();
            ChannelFuture ccf = cb.connect(new InetSocketAddress(NetUtil.LOCALHOST, port));
            Assert.assertTrue(ccf.awaitUninterruptibly().isSuccess());
            Channel clientChannel = ccf.channel();
            Assert.assertTrue(serverChannelLatch.await(5, TimeUnit.SECONDS));
            clientChannel.writeAndFlush(new DefaultHttpRequest(HTTP_1_1, GET, "/"));
            Assert.assertTrue(responseReceivedLatch.await(5, TimeUnit.SECONDS));
        } finally {
            sb.config().group().shutdownGracefully();
            sb.config().childGroup().shutdownGracefully();
            cb.config().group().shutdownGracefully();
        }
    }

    @Test
    public void testContinueParsingAfterConnect() throws Exception {
        HttpClientCodecTest.testAfterConnect(true);
    }

    @Test
    public void testPassThroughAfterConnect() throws Exception {
        HttpClientCodecTest.testAfterConnect(false);
    }

    private static class Consumer {
        private int receivedCount;

        final void onResponse(Object object) {
            (receivedCount)++;
            accept(object);
        }

        void accept(Object object) {
            // Default noop.
        }

        int getReceivedCount() {
            return receivedCount;
        }
    }

    @Test
    public void testDecodesFinalResponseAfterSwitchingProtocols() {
        String SWITCHING_PROTOCOLS_RESPONSE = "HTTP/1.1 101 Switching Protocols\r\n" + ("Connection: Upgrade\r\n" + "Upgrade: TLS/1.2, HTTP/1.1\r\n\r\n");
        HttpClientCodec codec = new HttpClientCodec(4096, 8192, 8192, true);
        EmbeddedChannel ch = new EmbeddedChannel(codec, new HttpObjectAggregator(1024));
        HttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, GET, "http://localhost/");
        request.headers().set(CONNECTION, UPGRADE);
        request.headers().set(HttpHeaderNames.UPGRADE, "TLS/1.2");
        Assert.assertTrue("Channel outbound write failed.", ch.writeOutbound(request));
        Assert.assertTrue("Channel inbound write failed.", ch.writeInbound(Unpooled.copiedBuffer(SWITCHING_PROTOCOLS_RESPONSE, ISO_8859_1)));
        Object switchingProtocolsResponse = ch.readInbound();
        Assert.assertNotNull("No response received", switchingProtocolsResponse);
        Assert.assertThat("Response was not decoded", switchingProtocolsResponse, CoreMatchers.instanceOf(FullHttpResponse.class));
        release();
        Assert.assertTrue("Channel inbound write failed", ch.writeInbound(Unpooled.copiedBuffer(HttpClientCodecTest.RESPONSE, ISO_8859_1)));
        Object finalResponse = ch.readInbound();
        Assert.assertNotNull("No response received", finalResponse);
        Assert.assertThat("Response was not decoded", finalResponse, CoreMatchers.instanceOf(FullHttpResponse.class));
        release();
        Assert.assertTrue("Channel finish failed", ch.finishAndReleaseAll());
    }

    @Test
    public void testWebSocket00Response() {
        byte[] data = ("HTTP/1.1 101 WebSocket Protocol Handshake\r\n" + ((((("Upgrade: WebSocket\r\n" + "Connection: Upgrade\r\n") + "Sec-WebSocket-Origin: http://localhost:8080\r\n") + "Sec-WebSocket-Location: ws://localhost/some/path\r\n") + "\r\n") + "1234567812345678")).getBytes();
        EmbeddedChannel ch = new EmbeddedChannel(new HttpClientCodec());
        Assert.assertTrue(ch.writeInbound(Unpooled.wrappedBuffer(data)));
        HttpResponse res = ch.readInbound();
        Assert.assertThat(res.protocolVersion(), CoreMatchers.sameInstance(HTTP_1_1));
        Assert.assertThat(res.status(), CoreMatchers.is(SWITCHING_PROTOCOLS));
        HttpContent content = ch.readInbound();
        Assert.assertThat(content.content().readableBytes(), CoreMatchers.is(16));
        content.release();
        Assert.assertThat(ch.finish(), CoreMatchers.is(false));
        Assert.assertThat(ch.readInbound(), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

