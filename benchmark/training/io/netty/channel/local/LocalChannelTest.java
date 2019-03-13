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
package io.netty.channel.local;


import LocalAddress.ANY;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class LocalChannelTest {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(LocalChannelTest.class);

    private static final LocalAddress TEST_ADDRESS = new LocalAddress("test.id");

    private static EventLoopGroup group1;

    private static EventLoopGroup group2;

    private static EventLoopGroup sharedGroup;

    @Test
    public void testLocalAddressReuse() throws Exception {
        for (int i = 0; i < 2; i++) {
            Bootstrap cb = new Bootstrap();
            ServerBootstrap sb = new ServerBootstrap();
            cb.group(LocalChannelTest.group1).channel(LocalChannel.class).handler(new LocalChannelTest.TestHandler());
            sb.group(LocalChannelTest.group2).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
                @Override
                public void initChannel(LocalChannel ch) throws Exception {
                    ch.pipeline().addLast(new LocalChannelTest.TestHandler());
                }
            });
            Channel sc = null;
            Channel cc = null;
            try {
                // Start server
                sc = sb.bind(LocalChannelTest.TEST_ADDRESS).sync().channel();
                final CountDownLatch latch = new CountDownLatch(1);
                // Connect to the server
                cc = cb.connect(sc.localAddress()).sync().channel();
                final Channel ccCpy = cc;
                cc.eventLoop().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Send a message event up the pipeline.
                        ccCpy.pipeline().fireChannelRead("Hello, World");
                        latch.countDown();
                    }
                });
                Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
                // Close the channel
                LocalChannelTest.closeChannel(cc);
                LocalChannelTest.closeChannel(sc);
                sc.closeFuture().sync();
                Assert.assertNull(String.format("Expected null, got channel '%s' for local address '%s'", LocalChannelRegistry.get(LocalChannelTest.TEST_ADDRESS), LocalChannelTest.TEST_ADDRESS), LocalChannelRegistry.get(LocalChannelTest.TEST_ADDRESS));
            } finally {
                LocalChannelTest.closeChannel(cc);
                LocalChannelTest.closeChannel(sc);
            }
        }
    }

    @Test
    public void testWriteFailsFastOnClosedChannel() throws Exception {
        Bootstrap cb = new Bootstrap();
        ServerBootstrap sb = new ServerBootstrap();
        cb.group(LocalChannelTest.group1).channel(LocalChannel.class).handler(new LocalChannelTest.TestHandler());
        sb.group(LocalChannelTest.group2).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new LocalChannelTest.TestHandler());
            }
        });
        Channel sc = null;
        Channel cc = null;
        try {
            // Start server
            sc = sb.bind(LocalChannelTest.TEST_ADDRESS).sync().channel();
            // Connect to the server
            cc = cb.connect(sc.localAddress()).sync().channel();
            // Close the channel and write something.
            cc.close().sync();
            try {
                cc.writeAndFlush(new Object()).sync();
                Assert.fail("must raise a ClosedChannelException");
            } catch (Exception e) {
                Assert.assertThat(e, CoreMatchers.is(CoreMatchers.instanceOf(ClosedChannelException.class)));
                // Ensure that the actual write attempt on a closed channel was never made by asserting that
                // the ClosedChannelException has been created by AbstractUnsafe rather than transport implementations.
                if ((e.getStackTrace().length) > 0) {
                    Assert.assertThat(e.getStackTrace()[0].getClassName(), CoreMatchers.is(((AbstractChannel.class.getName()) + "$AbstractUnsafe")));
                    e.printStackTrace();
                }
            }
        } finally {
            LocalChannelTest.closeChannel(cc);
            LocalChannelTest.closeChannel(sc);
        }
    }

    @Test
    public void testServerCloseChannelSameEventLoop() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ServerBootstrap sb = new ServerBootstrap().group(LocalChannelTest.group2).channel(LocalServerChannel.class).childHandler(new io.netty.channel.SimpleChannelInboundHandler<Object>() {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                ctx.close();
                latch.countDown();
            }
        });
        Channel sc = null;
        Channel cc = null;
        try {
            sc = sb.bind(LocalChannelTest.TEST_ADDRESS).sync().channel();
            Bootstrap b = new Bootstrap().group(LocalChannelTest.group2).channel(LocalChannel.class).handler(new io.netty.channel.SimpleChannelInboundHandler<Object>() {
                @Override
                protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                    // discard
                }
            });
            cc = b.connect(sc.localAddress()).sync().channel();
            cc.writeAndFlush(new Object());
            Assert.assertTrue(latch.await(5, TimeUnit.SECONDS));
        } finally {
            LocalChannelTest.closeChannel(cc);
            LocalChannelTest.closeChannel(sc);
        }
    }

    @Test
    public void localChannelRaceCondition() throws Exception {
        final CountDownLatch closeLatch = new CountDownLatch(1);
        final EventLoopGroup clientGroup = new DefaultEventLoopGroup(1) {
            @Override
            protected EventLoop newChild(Executor threadFactory, Object... args) throws Exception {
                return new SingleThreadEventLoop(this, threadFactory, true) {
                    @Override
                    protected void run() {
                        for (; ;) {
                            Runnable task = takeTask();
                            if (task != null) {
                                /* Only slow down the anonymous class in LocalChannel#doRegister() */
                                if ((task.getClass().getEnclosingClass()) == (LocalChannel.class)) {
                                    try {
                                        closeLatch.await();
                                    } catch (InterruptedException e) {
                                        throw new Error(e);
                                    }
                                }
                                task.run();
                                updateLastExecutionTime();
                            }
                            if (confirmShutdown()) {
                                break;
                            }
                        }
                    }
                };
            }
        };
        Channel sc = null;
        Channel cc = null;
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sc = sb.group(LocalChannelTest.group2).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.close();
                    closeLatch.countDown();
                }
            }).bind(LocalChannelTest.TEST_ADDRESS).sync().channel();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientGroup).channel(LocalChannel.class).handler(new io.netty.channel.ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {
                    /* Do nothing */
                }
            });
            ChannelFuture future = bootstrap.connect(sc.localAddress());
            Assert.assertTrue("Connection should finish, not time out", future.await(200));
            cc = future.channel();
        } finally {
            LocalChannelTest.closeChannel(cc);
            LocalChannelTest.closeChannel(sc);
            clientGroup.shutdownGracefully(0, 0, TimeUnit.SECONDS).await();
        }
    }

    @Test
    public void testReRegister() {
        Bootstrap cb = new Bootstrap();
        ServerBootstrap sb = new ServerBootstrap();
        cb.group(LocalChannelTest.group1).channel(LocalChannel.class).handler(new LocalChannelTest.TestHandler());
        sb.group(LocalChannelTest.group2).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new LocalChannelTest.TestHandler());
            }
        });
        Channel sc = null;
        Channel cc = null;
        try {
            // Start server
            sc = sb.bind(LocalChannelTest.TEST_ADDRESS).syncUninterruptibly().channel();
            // Connect to the server
            cc = cb.connect(sc.localAddress()).syncUninterruptibly().channel();
            cc.deregister().syncUninterruptibly();
        } finally {
            LocalChannelTest.closeChannel(cc);
            LocalChannelTest.closeChannel(sc);
        }
    }

    @Test
    public void testCloseInWritePromiseCompletePreservesOrder() throws InterruptedException {
        Bootstrap cb = new Bootstrap();
        ServerBootstrap sb = new ServerBootstrap();
        final CountDownLatch messageLatch = new CountDownLatch(2);
        final ByteBuf data = Unpooled.wrappedBuffer(new byte[1024]);
        try {
            cb.group(LocalChannelTest.group1).channel(LocalChannel.class).handler(new LocalChannelTest.TestHandler());
            sb.group(LocalChannelTest.group2).channel(LocalServerChannel.class).childHandler(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if (msg.equals(data)) {
                        ReferenceCountUtil.safeRelease(msg);
                        messageLatch.countDown();
                    } else {
                        super.channelRead(ctx, msg);
                    }
                }

                @Override
                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                    messageLatch.countDown();
                    super.channelInactive(ctx);
                }
            });
            Channel sc = null;
            Channel cc = null;
            try {
                // Start server
                sc = sb.bind(LocalChannelTest.TEST_ADDRESS).syncUninterruptibly().channel();
                // Connect to the server
                cc = cb.connect(sc.localAddress()).syncUninterruptibly().channel();
                final Channel ccCpy = cc;
                // Make sure a write operation is executed in the eventloop
                cc.pipeline().lastContext().executor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ChannelPromise promise = ccCpy.newPromise();
                        promise.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                ccCpy.pipeline().lastContext().close();
                            }
                        });
                        ccCpy.writeAndFlush(data.retainedDuplicate(), promise);
                    }
                });
                Assert.assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
                Assert.assertFalse(cc.isOpen());
            } finally {
                LocalChannelTest.closeChannel(cc);
                LocalChannelTest.closeChannel(sc);
            }
        } finally {
            data.release();
        }
    }

    @Test
    public void testCloseAfterWriteInSameEventLoopPreservesOrder() throws InterruptedException {
        Bootstrap cb = new Bootstrap();
        ServerBootstrap sb = new ServerBootstrap();
        final CountDownLatch messageLatch = new CountDownLatch(3);
        final ByteBuf data = Unpooled.wrappedBuffer(new byte[1024]);
        try {
            cb.group(LocalChannelTest.sharedGroup).channel(LocalChannel.class).handler(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    ctx.writeAndFlush(data.retainedDuplicate());
                }

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if (data.equals(msg)) {
                        ReferenceCountUtil.safeRelease(msg);
                        messageLatch.countDown();
                    } else {
                        super.channelRead(ctx, msg);
                    }
                }
            });
            sb.group(LocalChannelTest.sharedGroup).channel(LocalServerChannel.class).childHandler(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if (data.equals(msg)) {
                        messageLatch.countDown();
                        ctx.writeAndFlush(data);
                        ctx.close();
                    } else {
                        super.channelRead(ctx, msg);
                    }
                }

                @Override
                public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                    messageLatch.countDown();
                    super.channelInactive(ctx);
                }
            });
            Channel sc = null;
            Channel cc = null;
            try {
                // Start server
                sc = sb.bind(LocalChannelTest.TEST_ADDRESS).syncUninterruptibly().channel();
                // Connect to the server
                cc = cb.connect(sc.localAddress()).syncUninterruptibly().channel();
                Assert.assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
                Assert.assertFalse(cc.isOpen());
            } finally {
                LocalChannelTest.closeChannel(cc);
                LocalChannelTest.closeChannel(sc);
            }
        } finally {
            data.release();
        }
    }

    @Test
    public void testWriteInWritePromiseCompletePreservesOrder() throws InterruptedException {
        Bootstrap cb = new Bootstrap();
        ServerBootstrap sb = new ServerBootstrap();
        final CountDownLatch messageLatch = new CountDownLatch(2);
        final ByteBuf data = Unpooled.wrappedBuffer(new byte[1024]);
        final ByteBuf data2 = Unpooled.wrappedBuffer(new byte[512]);
        try {
            cb.group(LocalChannelTest.group1).channel(LocalChannel.class).handler(new LocalChannelTest.TestHandler());
            sb.group(LocalChannelTest.group2).channel(LocalServerChannel.class).childHandler(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    final long count = messageLatch.getCount();
                    if (((data.equals(msg)) && (count == 2)) || ((data2.equals(msg)) && (count == 1))) {
                        ReferenceCountUtil.safeRelease(msg);
                        messageLatch.countDown();
                    } else {
                        super.channelRead(ctx, msg);
                    }
                }
            });
            Channel sc = null;
            Channel cc = null;
            try {
                // Start server
                sc = sb.bind(LocalChannelTest.TEST_ADDRESS).syncUninterruptibly().channel();
                // Connect to the server
                cc = cb.connect(sc.localAddress()).syncUninterruptibly().channel();
                final Channel ccCpy = cc;
                // Make sure a write operation is executed in the eventloop
                cc.pipeline().lastContext().executor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ChannelPromise promise = ccCpy.newPromise();
                        promise.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                ccCpy.writeAndFlush(data2.retainedDuplicate(), ccCpy.newPromise());
                            }
                        });
                        ccCpy.writeAndFlush(data.retainedDuplicate(), promise);
                    }
                });
                Assert.assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
            } finally {
                LocalChannelTest.closeChannel(cc);
                LocalChannelTest.closeChannel(sc);
            }
        } finally {
            data.release();
            data2.release();
        }
    }

    @Test
    public void testPeerWriteInWritePromiseCompleteDifferentEventLoopPreservesOrder() throws InterruptedException {
        Bootstrap cb = new Bootstrap();
        ServerBootstrap sb = new ServerBootstrap();
        final CountDownLatch messageLatch = new CountDownLatch(2);
        final ByteBuf data = Unpooled.wrappedBuffer(new byte[1024]);
        final ByteBuf data2 = Unpooled.wrappedBuffer(new byte[512]);
        final CountDownLatch serverChannelLatch = new CountDownLatch(1);
        final AtomicReference<Channel> serverChannelRef = new AtomicReference<Channel>();
        cb.group(LocalChannelTest.group1).channel(LocalChannel.class).handler(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (data2.equals(msg)) {
                    ReferenceCountUtil.safeRelease(msg);
                    messageLatch.countDown();
                } else {
                    super.channelRead(ctx, msg);
                }
            }
        });
        sb.group(LocalChannelTest.group2).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        if (data.equals(msg)) {
                            ReferenceCountUtil.safeRelease(msg);
                            messageLatch.countDown();
                        } else {
                            super.channelRead(ctx, msg);
                        }
                    }
                });
                serverChannelRef.set(ch);
                serverChannelLatch.countDown();
            }
        });
        Channel sc = null;
        Channel cc = null;
        try {
            // Start server
            sc = sb.bind(LocalChannelTest.TEST_ADDRESS).syncUninterruptibly().channel();
            // Connect to the server
            cc = cb.connect(sc.localAddress()).syncUninterruptibly().channel();
            Assert.assertTrue(serverChannelLatch.await(5, TimeUnit.SECONDS));
            final Channel ccCpy = cc;
            // Make sure a write operation is executed in the eventloop
            cc.pipeline().lastContext().executor().execute(new Runnable() {
                @Override
                public void run() {
                    ChannelPromise promise = ccCpy.newPromise();
                    promise.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            Channel serverChannelCpy = serverChannelRef.get();
                            serverChannelCpy.writeAndFlush(data2.retainedDuplicate(), serverChannelCpy.newPromise());
                        }
                    });
                    ccCpy.writeAndFlush(data.retainedDuplicate(), promise);
                }
            });
            Assert.assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
        } finally {
            LocalChannelTest.closeChannel(cc);
            LocalChannelTest.closeChannel(sc);
            data.release();
            data2.release();
        }
    }

    @Test
    public void testPeerWriteInWritePromiseCompleteSameEventLoopPreservesOrder() throws InterruptedException {
        Bootstrap cb = new Bootstrap();
        ServerBootstrap sb = new ServerBootstrap();
        final CountDownLatch messageLatch = new CountDownLatch(2);
        final ByteBuf data = Unpooled.wrappedBuffer(new byte[1024]);
        final ByteBuf data2 = Unpooled.wrappedBuffer(new byte[512]);
        final CountDownLatch serverChannelLatch = new CountDownLatch(1);
        final AtomicReference<Channel> serverChannelRef = new AtomicReference<Channel>();
        try {
            cb.group(LocalChannelTest.sharedGroup).channel(LocalChannel.class).handler(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    if ((data2.equals(msg)) && ((messageLatch.getCount()) == 1)) {
                        ReferenceCountUtil.safeRelease(msg);
                        messageLatch.countDown();
                    } else {
                        super.channelRead(ctx, msg);
                    }
                }
            });
            sb.group(LocalChannelTest.sharedGroup).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
                @Override
                public void initChannel(LocalChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            if ((data.equals(msg)) && ((messageLatch.getCount()) == 2)) {
                                ReferenceCountUtil.safeRelease(msg);
                                messageLatch.countDown();
                            } else {
                                super.channelRead(ctx, msg);
                            }
                        }
                    });
                    serverChannelRef.set(ch);
                    serverChannelLatch.countDown();
                }
            });
            Channel sc = null;
            Channel cc = null;
            try {
                // Start server
                sc = sb.bind(LocalChannelTest.TEST_ADDRESS).syncUninterruptibly().channel();
                // Connect to the server
                cc = cb.connect(sc.localAddress()).syncUninterruptibly().channel();
                Assert.assertTrue(serverChannelLatch.await(5, TimeUnit.SECONDS));
                final Channel ccCpy = cc;
                // Make sure a write operation is executed in the eventloop
                cc.pipeline().lastContext().executor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ChannelPromise promise = ccCpy.newPromise();
                        promise.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                Channel serverChannelCpy = serverChannelRef.get();
                                serverChannelCpy.writeAndFlush(data2.retainedDuplicate(), serverChannelCpy.newPromise());
                            }
                        });
                        ccCpy.writeAndFlush(data.retainedDuplicate(), promise);
                    }
                });
                Assert.assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
            } finally {
                LocalChannelTest.closeChannel(cc);
                LocalChannelTest.closeChannel(sc);
            }
        } finally {
            data.release();
            data2.release();
        }
    }

    @Test
    public void testWriteWhilePeerIsClosedReleaseObjectAndFailPromise() throws InterruptedException {
        Bootstrap cb = new Bootstrap();
        ServerBootstrap sb = new ServerBootstrap();
        final CountDownLatch serverMessageLatch = new CountDownLatch(1);
        final LocalChannelTest.LatchChannelFutureListener serverChannelCloseLatch = new LocalChannelTest.LatchChannelFutureListener(1);
        final LocalChannelTest.LatchChannelFutureListener clientChannelCloseLatch = new LocalChannelTest.LatchChannelFutureListener(1);
        final CountDownLatch writeFailLatch = new CountDownLatch(1);
        final ByteBuf data = Unpooled.wrappedBuffer(new byte[1024]);
        final ByteBuf data2 = Unpooled.wrappedBuffer(new byte[512]);
        final CountDownLatch serverChannelLatch = new CountDownLatch(1);
        final AtomicReference<Channel> serverChannelRef = new AtomicReference<Channel>();
        try {
            cb.group(LocalChannelTest.group1).channel(LocalChannel.class).handler(new LocalChannelTest.TestHandler());
            sb.group(LocalChannelTest.group2).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
                @Override
                public void initChannel(LocalChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            if (data.equals(msg)) {
                                ReferenceCountUtil.safeRelease(msg);
                                serverMessageLatch.countDown();
                            } else {
                                super.channelRead(ctx, msg);
                            }
                        }
                    });
                    serverChannelRef.set(ch);
                    serverChannelLatch.countDown();
                }
            });
            Channel sc = null;
            Channel cc = null;
            try {
                // Start server
                sc = sb.bind(LocalChannelTest.TEST_ADDRESS).syncUninterruptibly().channel();
                // Connect to the server
                cc = cb.connect(sc.localAddress()).syncUninterruptibly().channel();
                Assert.assertTrue(serverChannelLatch.await(5, TimeUnit.SECONDS));
                final Channel ccCpy = cc;
                final Channel serverChannelCpy = serverChannelRef.get();
                serverChannelCpy.closeFuture().addListener(serverChannelCloseLatch);
                ccCpy.closeFuture().addListener(clientChannelCloseLatch);
                // Make sure a write operation is executed in the eventloop
                cc.pipeline().lastContext().executor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ccCpy.writeAndFlush(data.retainedDuplicate(), ccCpy.newPromise()).addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                serverChannelCpy.eventLoop().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        // The point of this test is to write while the peer is closed, so we should
                                        // ensure the peer is actually closed before we write.
                                        int waitCount = 0;
                                        while (ccCpy.isOpen()) {
                                            try {
                                                Thread.sleep(50);
                                            } catch (InterruptedException ignored) {
                                                // ignored
                                            }
                                            if ((++waitCount) > 5) {
                                                Assert.fail();
                                            }
                                        } 
                                        serverChannelCpy.writeAndFlush(data2.retainedDuplicate(), serverChannelCpy.newPromise()).addListener(new ChannelFutureListener() {
                                            @Override
                                            public void operationComplete(ChannelFuture future) throws Exception {
                                                if ((!(future.isSuccess())) && ((future.cause()) instanceof ClosedChannelException)) {
                                                    writeFailLatch.countDown();
                                                }
                                            }
                                        });
                                    }
                                });
                                ccCpy.close();
                            }
                        });
                    }
                });
                Assert.assertTrue(serverMessageLatch.await(5, TimeUnit.SECONDS));
                Assert.assertTrue(writeFailLatch.await(5, TimeUnit.SECONDS));
                Assert.assertTrue(serverChannelCloseLatch.await(5, TimeUnit.SECONDS));
                Assert.assertTrue(clientChannelCloseLatch.await(5, TimeUnit.SECONDS));
                Assert.assertFalse(ccCpy.isOpen());
                Assert.assertFalse(serverChannelCpy.isOpen());
            } finally {
                LocalChannelTest.closeChannel(cc);
                LocalChannelTest.closeChannel(sc);
            }
        } finally {
            data.release();
            data2.release();
        }
    }

    @Test(timeout = 3000)
    public void testConnectFutureBeforeChannelActive() throws Exception {
        Bootstrap cb = new Bootstrap();
        ServerBootstrap sb = new ServerBootstrap();
        cb.group(LocalChannelTest.group1).channel(LocalChannel.class).handler(new ChannelInboundHandlerAdapter());
        sb.group(LocalChannelTest.group2).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new LocalChannelTest.TestHandler());
            }
        });
        Channel sc = null;
        Channel cc = null;
        try {
            // Start server
            sc = sb.bind(LocalChannelTest.TEST_ADDRESS).sync().channel();
            cc = cb.register().sync().channel();
            final ChannelPromise promise = cc.newPromise();
            final Promise<Void> assertPromise = cc.eventLoop().newPromise();
            cc.pipeline().addLast(new LocalChannelTest.TestHandler() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    // Ensure the promise was done before the handler method is triggered.
                    if (promise.isDone()) {
                        assertPromise.setSuccess(null);
                    } else {
                        assertPromise.setFailure(new AssertionError("connect promise should be done"));
                    }
                }
            });
            // Connect to the server
            cc.connect(sc.localAddress(), promise).sync();
            assertPromise.syncUninterruptibly();
            Assert.assertTrue(promise.isSuccess());
        } finally {
            LocalChannelTest.closeChannel(cc);
            LocalChannelTest.closeChannel(sc);
        }
    }

    @Test(expected = ConnectException.class)
    public void testConnectionRefused() {
        Bootstrap sb = new Bootstrap();
        sb.group(LocalChannelTest.group1).channel(LocalChannel.class).handler(new LocalChannelTest.TestHandler()).connect(ANY).syncUninterruptibly();
    }

    private static final class LatchChannelFutureListener extends CountDownLatch implements ChannelFutureListener {
        private LatchChannelFutureListener(int count) {
            super(count);
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            countDown();
        }
    }

    static class TestHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            LocalChannelTest.logger.info(String.format("Received message: %s", msg));
            ReferenceCountUtil.safeRelease(msg);
        }
    }

    @Test
    public void testNotLeakBuffersWhenCloseByRemotePeer() throws Exception {
        Bootstrap cb = new Bootstrap();
        ServerBootstrap sb = new ServerBootstrap();
        cb.group(LocalChannelTest.sharedGroup).channel(LocalChannel.class).handler(new io.netty.channel.SimpleChannelInboundHandler<ByteBuf>() {
            @Override
            public void channelActive(final ChannelHandlerContext ctx) throws Exception {
                ctx.writeAndFlush(ctx.alloc().buffer().writeZero(100));
            }

            @Override
            public void channelRead0(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
                // Just drop the buffer
            }
        });
        sb.group(LocalChannelTest.sharedGroup).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new io.netty.channel.SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    public void channelRead0(ChannelHandlerContext ctx, ByteBuf buffer) throws Exception {
                        while (buffer.isReadable()) {
                            // Fill the ChannelOutboundBuffer with multiple buffers
                            ctx.write(buffer.readRetainedSlice(1));
                        } 
                        // Flush and so transfer the written buffers to the inboundBuffer of the remote peer.
                        // After this point the remote peer is responsible to release all the buffers.
                        ctx.flush();
                        // This close call will trigger the remote peer close as well.
                        ctx.close();
                    }
                });
            }
        });
        Channel sc = null;
        LocalChannel cc = null;
        try {
            // Start server
            sc = sb.bind(LocalChannelTest.TEST_ADDRESS).sync().channel();
            // Connect to the server
            cc = ((LocalChannel) (cb.connect(sc.localAddress()).sync().channel()));
            // Close the channel
            LocalChannelTest.closeChannel(cc);
            Assert.assertTrue(cc.inboundBuffer.isEmpty());
            LocalChannelTest.closeChannel(sc);
        } finally {
            LocalChannelTest.closeChannel(cc);
            LocalChannelTest.closeChannel(sc);
        }
    }

    @Test(timeout = 5000)
    public void testAutoReadDisabledSharedGroup() throws Exception {
        LocalChannelTest.testAutoReadDisabled(LocalChannelTest.sharedGroup, LocalChannelTest.sharedGroup);
    }

    @Test(timeout = 5000)
    public void testAutoReadDisabledDifferentGroup() throws Exception {
        LocalChannelTest.testAutoReadDisabled(LocalChannelTest.group1, LocalChannelTest.group2);
    }

    @Test(timeout = 5000)
    public void testMaxMessagesPerReadRespectedWithAutoReadSharedGroup() throws Exception {
        LocalChannelTest.testMaxMessagesPerReadRespected(LocalChannelTest.sharedGroup, LocalChannelTest.sharedGroup, true);
    }

    @Test(timeout = 5000)
    public void testMaxMessagesPerReadRespectedWithoutAutoReadSharedGroup() throws Exception {
        LocalChannelTest.testMaxMessagesPerReadRespected(LocalChannelTest.sharedGroup, LocalChannelTest.sharedGroup, false);
    }

    @Test(timeout = 5000)
    public void testMaxMessagesPerReadRespectedWithAutoReadDifferentGroup() throws Exception {
        LocalChannelTest.testMaxMessagesPerReadRespected(LocalChannelTest.group1, LocalChannelTest.group2, true);
    }

    @Test(timeout = 5000)
    public void testMaxMessagesPerReadRespectedWithoutAutoReadDifferentGroup() throws Exception {
        LocalChannelTest.testMaxMessagesPerReadRespected(LocalChannelTest.group1, LocalChannelTest.group2, false);
    }

    @Test(timeout = 5000)
    public void testServerMaxMessagesPerReadRespectedWithAutoReadSharedGroup() throws Exception {
        testServerMaxMessagesPerReadRespected(LocalChannelTest.sharedGroup, LocalChannelTest.sharedGroup, true);
    }

    @Test(timeout = 5000)
    public void testServerMaxMessagesPerReadRespectedWithoutAutoReadSharedGroup() throws Exception {
        testServerMaxMessagesPerReadRespected(LocalChannelTest.sharedGroup, LocalChannelTest.sharedGroup, false);
    }

    @Test(timeout = 5000)
    public void testServerMaxMessagesPerReadRespectedWithAutoReadDifferentGroup() throws Exception {
        testServerMaxMessagesPerReadRespected(LocalChannelTest.group1, LocalChannelTest.group2, true);
    }

    @Test(timeout = 5000)
    public void testServerMaxMessagesPerReadRespectedWithoutAutoReadDifferentGroup() throws Exception {
        testServerMaxMessagesPerReadRespected(LocalChannelTest.group1, LocalChannelTest.group2, false);
    }

    private static final class ChannelReadHandler extends ChannelInboundHandlerAdapter {
        private final CountDownLatch latch;

        private final boolean autoRead;

        private int read;

        ChannelReadHandler(CountDownLatch latch, boolean autoRead) {
            this.latch = latch;
            this.autoRead = autoRead;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            if (!(autoRead)) {
                ctx.read();
            }
            ctx.fireChannelActive();
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, Object msg) {
            Assert.assertEquals(0, read);
            (read)++;
            ctx.fireChannelRead(msg);
        }

        @Override
        public void channelReadComplete(final ChannelHandlerContext ctx) {
            Assert.assertEquals(1, read);
            latch.countDown();
            if ((latch.getCount()) > 0) {
                if (!(autoRead)) {
                    // The read will be scheduled 100ms in the future to ensure we not receive any
                    // channelRead calls in the meantime.
                    ctx.executor().schedule(new Runnable() {
                        @Override
                        public void run() {
                            read = 0;
                            ctx.read();
                        }
                    }, 100, TimeUnit.MILLISECONDS);
                } else {
                    read = 0;
                }
            } else {
                read = 0;
            }
            ctx.fireChannelReadComplete();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.fireExceptionCaught(cause);
            ctx.close();
        }
    }
}

