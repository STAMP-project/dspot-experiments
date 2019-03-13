/**
 * Copyright 2016 The Netty Project
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
package io.netty.handler.proxy;


import HttpHeaderNames.CONTENT_LENGTH;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.proxy.HttpProxyHandler.HttpProxyConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class HttpProxyHandlerTest {
    @Test
    public void testHostname() throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 8080);
        HttpProxyHandlerTest.testInitialMessage(socketAddress, "localhost:8080", "localhost:8080", null, true);
    }

    @Test
    public void testHostnameUnresolved() throws Exception {
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("localhost", 8080);
        HttpProxyHandlerTest.testInitialMessage(socketAddress, "localhost:8080", "localhost:8080", null, true);
    }

    @Test
    public void testHostHeaderWithHttpDefaultPort() throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 80);
        HttpProxyHandlerTest.testInitialMessage(socketAddress, "localhost:80", "localhost:80", null, false);
    }

    @Test
    public void testHostHeaderWithHttpDefaultPortIgnored() throws Exception {
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("localhost", 80);
        HttpProxyHandlerTest.testInitialMessage(socketAddress, "localhost:80", "localhost", null, true);
    }

    @Test
    public void testHostHeaderWithHttpsDefaultPort() throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 443);
        HttpProxyHandlerTest.testInitialMessage(socketAddress, "localhost:443", "localhost:443", null, false);
    }

    @Test
    public void testHostHeaderWithHttpsDefaultPortIgnored() throws Exception {
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("localhost", 443);
        HttpProxyHandlerTest.testInitialMessage(socketAddress, "localhost:443", "localhost", null, true);
    }

    @Test
    public void testIpv6() throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("::1"), 8080);
        HttpProxyHandlerTest.testInitialMessage(socketAddress, "[::1]:8080", "[::1]:8080", null, true);
    }

    @Test
    public void testIpv6Unresolved() throws Exception {
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("::1", 8080);
        HttpProxyHandlerTest.testInitialMessage(socketAddress, "[::1]:8080", "[::1]:8080", null, true);
    }

    @Test
    public void testIpv4() throws Exception {
        InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName("10.0.0.1"), 8080);
        HttpProxyHandlerTest.testInitialMessage(socketAddress, "10.0.0.1:8080", "10.0.0.1:8080", null, true);
    }

    @Test
    public void testIpv4Unresolved() throws Exception {
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("10.0.0.1", 8080);
        HttpProxyHandlerTest.testInitialMessage(socketAddress, "10.0.0.1:8080", "10.0.0.1:8080", null, true);
    }

    @Test
    public void testCustomHeaders() throws Exception {
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("10.0.0.1", 8080);
        HttpProxyHandlerTest.testInitialMessage(socketAddress, "10.0.0.1:8080", "10.0.0.1:8080", new DefaultHttpHeaders().add("CUSTOM_HEADER", "CUSTOM_VALUE1").add("CUSTOM_HEADER", "CUSTOM_VALUE2"), true);
    }

    @Test
    public void testExceptionDuringConnect() throws Exception {
        EventLoopGroup group = null;
        Channel serverChannel = null;
        Channel clientChannel = null;
        try {
            group = new DefaultEventLoopGroup(1);
            final LocalAddress addr = new LocalAddress("a");
            final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();
            ChannelFuture sf = new ServerBootstrap().channel(LocalServerChannel.class).group(group).childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addFirst(new HttpResponseEncoder());
                    DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_GATEWAY);
                    response.headers().add("name", "value");
                    response.headers().add(CONTENT_LENGTH, "0");
                    ch.writeAndFlush(response);
                }
            }).bind(addr);
            serverChannel = sf.sync().channel();
            ChannelFuture cf = new Bootstrap().channel(LocalChannel.class).group(group).handler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) {
                    ch.pipeline().addFirst(new HttpProxyHandler(addr));
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                            exception.set(cause);
                        }
                    });
                }
            }).connect(new InetSocketAddress("localhost", 1234));
            clientChannel = cf.sync().channel();
            clientChannel.close().sync();
            Assert.assertTrue(((exception.get()) instanceof HttpProxyConnectException));
            HttpProxyConnectException actual = ((HttpProxyConnectException) (exception.get()));
            Assert.assertNotNull(actual.headers());
            Assert.assertEquals("value", actual.headers().get("name"));
        } finally {
            if (clientChannel != null) {
                clientChannel.close();
            }
            if (serverChannel != null) {
                serverChannel.close();
            }
            if (group != null) {
                group.shutdownGracefully();
            }
        }
    }
}

