/**
 * Copyright 2018 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.remote.blobstore.http;


import ChannelFutureListener.CLOSE;
import HttpHeaderNames.AUTHORIZATION;
import HttpHeaderNames.WWW_AUTHENTICATE;
import com.google.api.client.util.Preconditions;
import com.google.auth.Credentials;
import com.google.common.base.Charsets;
import com.google.devtools.build.lib.remote.util.Utils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import java.io.ByteArrayOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.function.IntFunction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import static io.netty.channel.SimpleChannelInboundHandler.<init>;


/**
 * Tests for {@link HttpBlobStore}.
 */
@RunWith(Parameterized.class)
public class HttpBlobStoreTest {
    interface TestServer {
        ServerChannel start(ChannelInboundHandler handler);

        void stop(ServerChannel serverChannel);
    }

    private static final class InetTestServer implements HttpBlobStoreTest.TestServer {
        public ServerChannel start(ChannelInboundHandler handler) {
            return HttpBlobStoreTest.createServer(NioServerSocketChannel.class, NioEventLoopGroup::new, new InetSocketAddress("localhost", 0), handler);
        }

        public void stop(ServerChannel serverChannel) {
            try {
                serverChannel.close();
                serverChannel.closeFuture().sync();
                serverChannel.eventLoop().shutdownGracefully().sync();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static final class UnixDomainServer implements HttpBlobStoreTest.TestServer {
        // Note: this odd implementation is a workaround because we're unable to shut down and restart
        // KQueue backed implementations. See https://github.com/netty/netty/issues/7047.
        private final ServerChannel serverChannel;

        private ChannelInboundHandler handler = null;

        public UnixDomainServer(Class<? extends ServerChannel> serverChannelClass, IntFunction<EventLoopGroup> newEventLoopGroup) {
            EventLoopGroup eventLoop = newEventLoopGroup.apply(1);
            ServerBootstrap sb = new ServerBootstrap().group(eventLoop).channel(serverChannelClass).childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addLast(new HttpServerCodec());
                    ch.pipeline().addLast(new HttpObjectAggregator(1000));
                    ch.pipeline().addLast(Preconditions.checkNotNull(handler));
                }
            });
            try {
                ServerChannel actual = ((ServerChannel) (sb.bind(HttpBlobStoreTest.newDomainSocketAddress()).sync().channel()));
                this.serverChannel = Mockito.mock(ServerChannel.class, AdditionalAnswers.delegatesTo(actual));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        public ServerChannel start(ChannelInboundHandler handler) {
            Mockito.reset(this.serverChannel);
            this.handler = handler;
            return this.serverChannel;
        }

        public void stop(ServerChannel serverChannel) {
            // Note: In the tests, we expect that connecting to a closed server channel results
            // in a channel connection error. Netty doesn't seem to handle closing domain socket
            // addresses very well-- often connecting to a closed domain socket will result in a
            // read timeout instead of a connection timeout.
            // 
            // This is a hack to ensure connection timeouts are "received" by the tests for this
            // dummy domain socket server. In particular, this lets the timeoutShouldWork_connect
            // test work for both inet and domain sockets.
            // 
            // This is also part of the workaround for https://github.com/netty/netty/issues/7047.
            Mockito.when(this.serverChannel.localAddress()).thenReturn(new DomainSocketAddress(""));
            this.handler = null;
        }
    }

    private final HttpBlobStoreTest.TestServer testServer;

    public HttpBlobStoreTest(HttpBlobStoreTest.TestServer testServer) {
        this.testServer = testServer;
    }

    @Test(expected = ConnectException.class, timeout = 30000)
    public void timeoutShouldWork_connect() throws Exception {
        ServerChannel server = testServer.start(new ChannelInboundHandlerAdapter() {});
        testServer.stop(server);
        Credentials credentials = newCredentials();
        HttpBlobStore blobStore = /* timeoutSeconds= */
        createHttpBlobStore(server, 1, credentials);
        Utils.getFromFuture(blobStore.get("key", new ByteArrayOutputStream()));
        Assert.fail("Exception expected");
    }

    @Test(expected = DownloadTimeoutException.class, timeout = 30000)
    public void timeoutShouldWork_read() throws Exception {
        ServerChannel server = null;
        try {
            server = testServer.start(new io.netty.channel.SimpleChannelInboundHandler<FullHttpRequest>() {
                @Override
                protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
                    // Don't respond and force a client timeout.
                }
            });
            Credentials credentials = newCredentials();
            HttpBlobStore blobStore = /* timeoutSeconds= */
            createHttpBlobStore(server, 1, credentials);
            Utils.getFromFuture(blobStore.get("key", new ByteArrayOutputStream()));
            Assert.fail("Exception expected");
        } finally {
            testServer.stop(server);
        }
    }

    @Test
    public void expiredAuthTokensShouldBeRetried_get() throws Exception {
        expiredAuthTokensShouldBeRetried_get(HttpBlobStoreTest.NotAuthorizedHandler.ErrorType.UNAUTHORIZED);
        expiredAuthTokensShouldBeRetried_get(HttpBlobStoreTest.NotAuthorizedHandler.ErrorType.INVALID_TOKEN);
    }

    @Test
    public void expiredAuthTokensShouldBeRetried_put() throws Exception {
        expiredAuthTokensShouldBeRetried_put(HttpBlobStoreTest.NotAuthorizedHandler.ErrorType.UNAUTHORIZED);
        expiredAuthTokensShouldBeRetried_put(HttpBlobStoreTest.NotAuthorizedHandler.ErrorType.INVALID_TOKEN);
    }

    @Test
    public void errorCodesThatShouldNotBeRetried_get() {
        errorCodeThatShouldNotBeRetried_get(HttpBlobStoreTest.NotAuthorizedHandler.ErrorType.INSUFFICIENT_SCOPE);
        errorCodeThatShouldNotBeRetried_get(HttpBlobStoreTest.NotAuthorizedHandler.ErrorType.INVALID_REQUEST);
    }

    @Test
    public void errorCodesThatShouldNotBeRetried_put() {
        errorCodeThatShouldNotBeRetried_put(HttpBlobStoreTest.NotAuthorizedHandler.ErrorType.INSUFFICIENT_SCOPE);
        errorCodeThatShouldNotBeRetried_put(HttpBlobStoreTest.NotAuthorizedHandler.ErrorType.INVALID_REQUEST);
    }

    /**
     * {@link ChannelHandler} that on the first request responds with a 401 UNAUTHORIZED status code,
     * which the client is expected to retry once with a new authentication token.
     */
    @Sharable
    static class NotAuthorizedHandler extends io.netty.channel.SimpleChannelInboundHandler<FullHttpRequest> {
        enum ErrorType {

            UNAUTHORIZED,
            INVALID_TOKEN,
            INSUFFICIENT_SCOPE,
            INVALID_REQUEST;}

        private final HttpBlobStoreTest.NotAuthorizedHandler.ErrorType errorType;

        private int messageCount;

        NotAuthorizedHandler(HttpBlobStoreTest.NotAuthorizedHandler.ErrorType errorType) {
            this.errorType = errorType;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
            if ((messageCount) == 0) {
                if (!("Bearer invalidToken".equals(request.headers().get(AUTHORIZATION)))) {
                    ctx.writeAndFlush(new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR)).addListener(CLOSE);
                    return;
                }
                final FullHttpResponse response;
                if ((errorType) == (HttpBlobStoreTest.NotAuthorizedHandler.ErrorType.UNAUTHORIZED)) {
                    response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
                } else {
                    response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);
                    response.headers().set(WWW_AUTHENTICATE, (((("Bearer realm=\"localhost\"," + "error=\"") + (errorType.name().toLowerCase())) + "\",") + "error_description=\"The access token expired\""));
                }
                ctx.writeAndFlush(response).addListener(CLOSE);
                (messageCount)++;
            } else
                if ((messageCount) == 1) {
                    if (!("Bearer validToken".equals(request.headers().get(AUTHORIZATION)))) {
                        ctx.writeAndFlush(new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR)).addListener(CLOSE);
                        return;
                    }
                    ByteBuf content = ctx.alloc().buffer();
                    content.writeCharSequence("File Contents", Charsets.US_ASCII);
                    FullHttpResponse response = new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
                    HttpUtil.setKeepAlive(response, true);
                    HttpUtil.setContentLength(response, content.readableBytes());
                    ctx.writeAndFlush(response);
                    (messageCount)++;
                } else {
                    // No third message expected.
                    ctx.writeAndFlush(new io.netty.handler.codec.http.DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR)).addListener(CLOSE);
                }

        }
    }
}

