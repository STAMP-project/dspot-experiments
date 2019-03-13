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
package org.jboss.netty.channel.socket.nio;


import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;


public class UnsupportedMessageTest {
    // Test for https://github.com/netty/netty/issues/734
    @Test(timeout = 10000)
    public void testUnsupported() throws Throwable {
        ServerBootstrap sb = new ServerBootstrap(new NioServerSocketChannelFactory());
        ClientBootstrap cb = new ClientBootstrap(new NioClientSocketChannelFactory());
        UnsupportedMessageTest.TestHandler sh = new UnsupportedMessageTest.TestHandler(true);
        UnsupportedMessageTest.TestHandler ch = new UnsupportedMessageTest.TestHandler(false);
        sb.getPipeline().addLast("handler", sh);
        cb.getPipeline().addLast("handler", ch);
        Channel sc = sb.bind(new InetSocketAddress(0));
        int port = ((InetSocketAddress) (sc.getLocalAddress())).getPort();
        ChannelFuture ccf = cb.connect(new InetSocketAddress(TestUtil.getLocalHost(), port));
        ccf.awaitUninterruptibly();
        if (!(ccf.isSuccess())) {
            sc.close().awaitUninterruptibly();
        }
        Assert.assertTrue(ccf.isSuccess());
        while ((sh.channel) == null) {
            Thread.sleep(10);
        } 
        sh.channel.close().awaitUninterruptibly();
        ch.channel.close().awaitUninterruptibly();
        sc.close().awaitUninterruptibly();
        Assert.assertTrue(sh.await());
    }

    private static class TestHandler extends SimpleChannelUpstreamHandler {
        volatile Channel channel;

        private final boolean server;

        private final CountDownLatch latch;

        private final CountDownLatch exceptionLatch = new CountDownLatch(1);

        TestHandler(boolean server) {
            this.server = server;
            if (server) {
                latch = new CountDownLatch(1);
            } else {
                latch = null;
            }
        }

        @Override
        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            channel = e.getChannel();
            if (server) {
                channel.write(new Object()).addListener(new ChannelFutureListener() {
                    public void operationComplete(ChannelFuture future) throws Exception {
                        latch.countDown();
                    }
                });
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            exceptionLatch.countDown();
            e.getChannel().close();
        }

        public boolean await() {
            if ((latch) == null) {
                return true;
            }
            try {
                return (latch.await(5, TimeUnit.SECONDS)) && (exceptionLatch.await(5, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }
}

