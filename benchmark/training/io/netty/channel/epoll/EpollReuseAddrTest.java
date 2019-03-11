/**
 * Copyright 2014 The Netty Project
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
package io.netty.channel.epoll;


import ChannelHandler.Sharable;
import EpollChannelOption.SO_REUSEPORT;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static Native.KERNEL_VERSION;


public class EpollReuseAddrTest {
    private static final int MAJOR;

    private static final int MINOR;

    private static final int BUGFIX;

    static {
        String kernelVersion = KERNEL_VERSION;
        int index = kernelVersion.indexOf('-');
        if (index > (-1)) {
            kernelVersion = kernelVersion.substring(0, index);
        }
        String[] versionParts = kernelVersion.split("\\.");
        if ((versionParts.length) <= 3) {
            MAJOR = Integer.parseInt(versionParts[0]);
            MINOR = Integer.parseInt(versionParts[1]);
            if ((versionParts.length) == 3) {
                BUGFIX = Integer.parseInt(versionParts[2]);
            } else {
                BUGFIX = 0;
            }
        } else {
            throw new IllegalStateException(("Can not parse kernel version " + kernelVersion));
        }
    }

    @Test
    public void testMultipleBindSocketChannelWithoutReusePortFails() {
        Assume.assumeTrue(EpollReuseAddrTest.versionEqOrGt(3, 9, 0));
        EpollReuseAddrTest.testMultipleBindDatagramChannelWithoutReusePortFails0(EpollReuseAddrTest.createServerBootstrap());
    }

    @Test
    public void testMultipleBindDatagramChannelWithoutReusePortFails() {
        Assume.assumeTrue(EpollReuseAddrTest.versionEqOrGt(3, 9, 0));
        EpollReuseAddrTest.testMultipleBindDatagramChannelWithoutReusePortFails0(EpollReuseAddrTest.createBootstrap());
    }

    @Test(timeout = 10000)
    public void testMultipleBindSocketChannel() throws Exception {
        Assume.assumeTrue(EpollReuseAddrTest.versionEqOrGt(3, 9, 0));
        ServerBootstrap bootstrap = EpollReuseAddrTest.createServerBootstrap();
        bootstrap.option(SO_REUSEPORT, true);
        final AtomicBoolean accepted1 = new AtomicBoolean();
        bootstrap.childHandler(new EpollReuseAddrTest.ServerSocketTestHandler(accepted1));
        ChannelFuture future = bootstrap.bind().syncUninterruptibly();
        InetSocketAddress address1 = ((InetSocketAddress) (future.channel().localAddress()));
        final AtomicBoolean accepted2 = new AtomicBoolean();
        bootstrap.childHandler(new EpollReuseAddrTest.ServerSocketTestHandler(accepted2));
        ChannelFuture future2 = bootstrap.bind(address1).syncUninterruptibly();
        InetSocketAddress address2 = ((InetSocketAddress) (future2.channel().localAddress()));
        Assert.assertEquals(address1, address2);
        while ((!(accepted1.get())) || (!(accepted2.get()))) {
            Socket socket = new Socket(address1.getAddress(), address1.getPort());
            socket.setReuseAddress(true);
            socket.close();
        } 
        future.channel().close().syncUninterruptibly();
        future2.channel().close().syncUninterruptibly();
    }

    @ChannelHandler.Sharable
    private static class ServerSocketTestHandler extends ChannelInboundHandlerAdapter {
        private final AtomicBoolean accepted;

        ServerSocketTestHandler(AtomicBoolean accepted) {
            this.accepted = accepted;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            accepted.set(true);
            ctx.close();
        }
    }

    @ChannelHandler.Sharable
    private static class DatagramSocketTestHandler extends ChannelInboundHandlerAdapter {
        private final AtomicBoolean received;

        DatagramSocketTestHandler(AtomicBoolean received) {
            this.received = received;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ReferenceCountUtil.release(msg);
            received.set(true);
        }
    }

    @ChannelHandler.Sharable
    private static final class DummyHandler extends ChannelHandlerAdapter {}
}

