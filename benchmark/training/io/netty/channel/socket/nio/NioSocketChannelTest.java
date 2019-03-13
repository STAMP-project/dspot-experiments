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
package io.netty.channel.socket.nio;


import ChannelFutureListener.CLOSE;
import ChannelOption.SO_SNDBUF;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class NioSocketChannelTest extends AbstractNioChannelTest<NioSocketChannel> {
    /**
     * Reproduces the issue #1600
     */
    @Test
    public void testFlushCloseReentrance() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            final Queue<ChannelFuture> futures = new LinkedBlockingQueue<ChannelFuture>();
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(group).channel(NioServerSocketChannel.class);
            sb.childOption(SO_SNDBUF, 1024);
            sb.childHandler(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    // Write a large enough data so that it is split into two loops.
                    futures.add(ctx.write(ctx.alloc().buffer().writeZero(1048576)).addListener(CLOSE));
                    futures.add(ctx.write(ctx.alloc().buffer().writeZero(1048576)));
                    ctx.flush();
                    futures.add(ctx.write(ctx.alloc().buffer().writeZero(1048576)));
                    ctx.flush();
                }
            });
            SocketAddress address = sb.bind(0).sync().channel().localAddress();
            Socket s = new Socket(NetUtil.LOCALHOST, ((InetSocketAddress) (address)).getPort());
            InputStream in = s.getInputStream();
            byte[] buf = new byte[8192];
            for (; ;) {
                if ((in.read(buf)) == (-1)) {
                    break;
                }
                // Wait a little bit so that the write attempts are split into multiple flush attempts.
                Thread.sleep(10);
            }
            s.close();
            Assert.assertThat(futures.size(), CoreMatchers.is(3));
            ChannelFuture f1 = futures.poll();
            ChannelFuture f2 = futures.poll();
            ChannelFuture f3 = futures.poll();
            Assert.assertThat(f1.isSuccess(), CoreMatchers.is(true));
            Assert.assertThat(f2.isDone(), CoreMatchers.is(true));
            Assert.assertThat(f2.isSuccess(), CoreMatchers.is(false));
            Assert.assertThat(f2.cause(), CoreMatchers.is(CoreMatchers.instanceOf(ClosedChannelException.class)));
            Assert.assertThat(f3.isDone(), CoreMatchers.is(true));
            Assert.assertThat(f3.isSuccess(), CoreMatchers.is(false));
            Assert.assertThat(f3.cause(), CoreMatchers.is(CoreMatchers.instanceOf(ClosedChannelException.class)));
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    /**
     * Reproduces the issue #1679
     */
    @Test
    public void testFlushAfterGatheredFlush() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.group(group).channel(NioServerSocketChannel.class);
            sb.childHandler(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelActive(final ChannelHandlerContext ctx) throws Exception {
                    // Trigger a gathering write by writing two buffers.
                    ctx.write(Unpooled.wrappedBuffer(new byte[]{ 'a' }));
                    ChannelFuture f = ctx.write(Unpooled.wrappedBuffer(new byte[]{ 'b' }));
                    f.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            // This message must be flushed
                            ctx.writeAndFlush(Unpooled.wrappedBuffer(new byte[]{ 'c' }));
                        }
                    });
                    ctx.flush();
                }
            });
            SocketAddress address = sb.bind(0).sync().channel().localAddress();
            Socket s = new Socket(NetUtil.LOCALHOST, ((InetSocketAddress) (address)).getPort());
            DataInput in = new DataInputStream(s.getInputStream());
            byte[] buf = new byte[3];
            in.readFully(buf);
            Assert.assertThat(new String(buf, CharsetUtil.US_ASCII), CoreMatchers.is("abc"));
            s.close();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    // Test for https://github.com/netty/netty/issues/4805
    @Test(timeout = 3000)
    public void testChannelReRegisterReadSameEventLoop() throws Exception {
        NioSocketChannelTest.testChannelReRegisterRead(true);
    }

    @Test(timeout = 3000)
    public void testChannelReRegisterReadDifferentEventLoop() throws Exception {
        NioSocketChannelTest.testChannelReRegisterRead(false);
    }

    @Test(timeout = 3000)
    public void testShutdownOutputAndClose() throws IOException {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        ServerSocket socket = new ServerSocket();
        socket.bind(new InetSocketAddress(0));
        Socket accepted = null;
        try {
            Bootstrap sb = new Bootstrap();
            sb.group(group).channel(NioSocketChannel.class);
            sb.handler(new ChannelInboundHandlerAdapter());
            SocketChannel channel = ((SocketChannel) (sb.connect(socket.getLocalSocketAddress()).syncUninterruptibly().channel()));
            accepted = socket.accept();
            channel.shutdownOutput().syncUninterruptibly();
            channel.close().syncUninterruptibly();
        } finally {
            if (accepted != null) {
                try {
                    accepted.close();
                } catch (IOException ignore) {
                    // ignore
                }
            }
            try {
                socket.close();
            } catch (IOException ignore) {
                // ignore
            }
            group.shutdownGracefully();
        }
    }
}

