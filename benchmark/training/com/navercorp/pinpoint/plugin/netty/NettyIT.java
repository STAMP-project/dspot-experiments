/**
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.netty;


import com.navercorp.pinpoint.bootstrap.plugin.test.Expectations;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.plugin.AgentPath;
import com.navercorp.pinpoint.plugin.WebServer;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.JvmVersion;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointConfig;
import com.navercorp.pinpoint.test.plugin.PinpointPluginTestSuite;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Taejin Koo
 */
@RunWith(PinpointPluginTestSuite.class)
@PinpointAgent(AgentPath.PATH)
@JvmVersion(7)
@Dependency({ "io.netty:netty-all:[4.1.0.Final,4.1.max]", "org.nanohttpd:nanohttpd:2.3.1" })
@PinpointConfig("pinpoint-netty-plugin-test.config")
public class NettyIT {
    private static WebServer webServer;

    @Test
    public void listenerTest() throws Exception {
        final CountDownLatch awaitLatch = new CountDownLatch(1);
        Bootstrap bootstrap = client();
        Channel channel = bootstrap.connect(getHostname(), getListeningPort()).sync().channel();
        channel.pipeline().addLast(new io.netty.channel.SimpleChannelInboundHandler<FullHttpResponse>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
                awaitLatch.countDown();
            }
        });
        HttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
        channel.writeAndFlush(request);
        boolean await = awaitLatch.await(3000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(await);
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        verifier.verifyTrace(Expectations.event("NETTY", Bootstrap.class.getMethod("connect", SocketAddress.class), Expectations.annotation("netty.address", NettyIT.webServer.getHostAndPort())));
        verifier.verifyTrace(Expectations.event("NETTY", "io.netty.channel.DefaultChannelPipeline.writeAndFlush(java.lang.Object)"));
        verifier.verifyTrace(Expectations.event("ASYNC", "Asynchronous Invocation"));
        verifier.verifyTrace(Expectations.event("NETTY_HTTP", "io.netty.handler.codec.http.HttpObjectEncoder.encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, java.util.List)", Expectations.annotation("http.url", "/")));
    }

    @Test
    public void writeTest() throws Exception {
        final CountDownLatch awaitLatch = new CountDownLatch(1);
        Bootstrap bootstrap = client();
        bootstrap.connect(getHostname(), getListeningPort()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    Channel channel = future.channel();
                    channel.pipeline().addLast(new io.netty.channel.SimpleChannelInboundHandler() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                            awaitLatch.countDown();
                        }
                    });
                    HttpRequest request = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
                    future.channel().writeAndFlush(request);
                }
            }
        });
        boolean await = awaitLatch.await(3000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(await);
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();
        verifier.verifyTrace(Expectations.event("NETTY", Bootstrap.class.getMethod("connect", SocketAddress.class), Expectations.annotation("netty.address", NettyIT.webServer.getHostAndPort())));
        verifier.verifyTrace(Expectations.event("NETTY", "io.netty.channel.DefaultChannelPromise.addListener(io.netty.util.concurrent.GenericFutureListener)"));
        verifier.verifyTrace(Expectations.event("ASYNC", "Asynchronous Invocation"));
        verifier.verifyTrace(Expectations.event("NETTY_INTERNAL", "io.netty.util.concurrent.DefaultPromise.notifyListenersNow()"));
        verifier.verifyTrace(Expectations.event("NETTY_INTERNAL", "io.netty.util.concurrent.DefaultPromise.notifyListener0(io.netty.util.concurrent.Future, io.netty.util.concurrent.GenericFutureListener)"));
        verifier.verifyTrace(Expectations.event("NETTY", "io.netty.channel.DefaultChannelPipeline.writeAndFlush(java.lang.Object)"));
        verifier.verifyTrace(Expectations.event("NETTY_HTTP", "io.netty.handler.codec.http.HttpObjectEncoder.encode(io.netty.channel.ChannelHandlerContext, java.lang.Object, java.util.List)", Expectations.annotation("http.url", "/")));
    }
}

