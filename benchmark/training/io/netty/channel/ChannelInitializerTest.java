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
package io.netty.channel;


import LocalAddress.ANY;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class ChannelInitializerTest {
    private static final int TIMEOUT_MILLIS = 1000;

    private static final LocalAddress SERVER_ADDRESS = new LocalAddress("addr");

    private EventLoopGroup group;

    private ServerBootstrap server;

    private Bootstrap client;

    private ChannelInitializerTest.InspectableHandler testHandler;

    @Test
    public void testInitChannelThrowsRegisterFirst() {
        testInitChannelThrows(true);
    }

    @Test
    public void testInitChannelThrowsRegisterAfter() {
        testInitChannelThrows(false);
    }

    @Test
    public void testChannelInitializerInInitializerCorrectOrdering() {
        final ChannelInboundHandlerAdapter handler1 = new ChannelInboundHandlerAdapter();
        final ChannelInboundHandlerAdapter handler2 = new ChannelInboundHandlerAdapter();
        final ChannelInboundHandlerAdapter handler3 = new ChannelInboundHandlerAdapter();
        final ChannelInboundHandlerAdapter handler4 = new ChannelInboundHandlerAdapter();
        client.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(handler1);
                ch.pipeline().addLast(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(handler2);
                        ch.pipeline().addLast(handler3);
                    }
                });
                ch.pipeline().addLast(handler4);
            }
        }).localAddress(ANY);
        Channel channel = client.bind().syncUninterruptibly().channel();
        try {
            // Execute some task on the EventLoop and wait until its done to be sure all handlers are added to the
            // pipeline.
            channel.eventLoop().submit(new Runnable() {
                @Override
                public void run() {
                    // NOOP
                }
            }).syncUninterruptibly();
            Iterator<Map.Entry<String, ChannelHandler>> handlers = channel.pipeline().iterator();
            Assert.assertSame(handler1, handlers.next().getValue());
            Assert.assertSame(handler2, handlers.next().getValue());
            Assert.assertSame(handler3, handlers.next().getValue());
            Assert.assertSame(handler4, handlers.next().getValue());
            Assert.assertFalse(handlers.hasNext());
        } finally {
            channel.close().syncUninterruptibly();
        }
    }

    @Test
    public void testChannelInitializerReentrance() {
        final AtomicInteger registeredCalled = new AtomicInteger(0);
        final ChannelInboundHandlerAdapter handler1 = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                registeredCalled.incrementAndGet();
            }
        };
        final AtomicInteger initChannelCalled = new AtomicInteger(0);
        client.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                initChannelCalled.incrementAndGet();
                ch.pipeline().addLast(handler1);
                ch.pipeline().fireChannelRegistered();
            }
        }).localAddress(ANY);
        Channel channel = client.bind().syncUninterruptibly().channel();
        try {
            // Execute some task on the EventLoop and wait until its done to be sure all handlers are added to the
            // pipeline.
            channel.eventLoop().submit(new Runnable() {
                @Override
                public void run() {
                    // NOOP
                }
            }).syncUninterruptibly();
            Assert.assertEquals(1, initChannelCalled.get());
            Assert.assertEquals(2, registeredCalled.get());
        } finally {
            channel.close().syncUninterruptibly();
        }
    }

    @Test(timeout = ChannelInitializerTest.TIMEOUT_MILLIS)
    public void firstHandlerInPipelineShouldReceiveChannelRegisteredEvent() {
        testChannelRegisteredEventPropagation(new ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel channel) {
                channel.pipeline().addFirst(testHandler);
            }
        });
    }

    @Test(timeout = ChannelInitializerTest.TIMEOUT_MILLIS)
    public void lastHandlerInPipelineShouldReceiveChannelRegisteredEvent() {
        testChannelRegisteredEventPropagation(new ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel channel) {
                channel.pipeline().addLast(testHandler);
            }
        });
    }

    @Test
    public void testAddFirstChannelInitializer() {
        ChannelInitializerTest.testAddChannelInitializer(true);
    }

    @Test
    public void testAddLastChannelInitializer() {
        ChannelInitializerTest.testAddChannelInitializer(false);
    }

    @SuppressWarnings("deprecation")
    @Test(timeout = 10000)
    public void testChannelInitializerEventExecutor() throws Throwable {
        final AtomicInteger invokeCount = new AtomicInteger();
        final AtomicInteger completeCount = new AtomicInteger();
        final AtomicReference<Throwable> errorRef = new AtomicReference<Throwable>();
        LocalAddress addr = new LocalAddress("test");
        final EventExecutor executor = new DefaultEventLoop() {
            private final ScheduledExecutorService execService = Executors.newSingleThreadScheduledExecutor();

            @Override
            public void shutdown() {
                execService.shutdown();
            }

            @Override
            public boolean inEventLoop(Thread thread) {
                // Always return false which will ensure we always call execute(...)
                return false;
            }

            @Override
            public boolean isShuttingDown() {
                return false;
            }

            @Override
            public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
                throw new IllegalStateException();
            }

            @Override
            public Future<?> terminationFuture() {
                throw new IllegalStateException();
            }

            @Override
            public boolean isShutdown() {
                return execService.isShutdown();
            }

            @Override
            public boolean isTerminated() {
                return execService.isTerminated();
            }

            @Override
            public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
                return execService.awaitTermination(timeout, unit);
            }

            @Override
            public void execute(Runnable command) {
                execService.execute(command);
            }
        };
        final CountDownLatch latch = new CountDownLatch(1);
        ServerBootstrap serverBootstrap = new ServerBootstrap().channel(io.netty.channel.local.LocalServerChannel.class).group(group).localAddress(addr).childHandler(new ChannelInitializer<LocalChannel>() {
            @Override
            protected void initChannel(LocalChannel ch) {
                ch.pipeline().addLast(executor, new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        invokeCount.incrementAndGet();
                        ChannelHandlerContext ctx = ch.pipeline().context(this);
                        Assert.assertNotNull(ctx);
                        ch.pipeline().addAfter(ctx.executor(), ctx.name(), null, new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                // just drop on the floor.
                            }

                            @Override
                            public void handlerRemoved(ChannelHandlerContext ctx) {
                                latch.countDown();
                            }
                        });
                        completeCount.incrementAndGet();
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                        if (cause instanceof AssertionError) {
                            errorRef.set(cause);
                        }
                    }
                });
            }
        });
        Channel server = serverBootstrap.bind().sync().channel();
        Bootstrap clientBootstrap = new Bootstrap().channel(LocalChannel.class).group(group).remoteAddress(addr).handler(new ChannelInboundHandlerAdapter());
        Channel client = clientBootstrap.connect().sync().channel();
        client.writeAndFlush("Hello World").sync();
        client.close().sync();
        server.close().sync();
        client.closeFuture().sync();
        server.closeFuture().sync();
        // Wait until the handler is removed from the pipeline and so no more events are handled by it.
        latch.await();
        Assert.assertEquals(1, invokeCount.get());
        Assert.assertEquals(invokeCount.get(), completeCount.get());
        Throwable cause = errorRef.get();
        if (cause != null) {
            throw cause;
        }
        executor.shutdown();
        Assert.assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }

    private static final class InspectableHandler extends ChannelDuplexHandler {
        final AtomicInteger channelRegisteredCount = new AtomicInteger(0);

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) {
            channelRegisteredCount.incrementAndGet();
            ctx.fireChannelRegistered();
        }
    }
}

