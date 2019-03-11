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
package io.netty.channel.embedded;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class EmbeddedChannelTest {
    @Test
    public void testNotRegistered() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(false, false);
        Assert.assertFalse(channel.isRegistered());
        channel.register();
        Assert.assertTrue(channel.isRegistered());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testRegistered() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(true, false);
        Assert.assertTrue(channel.isRegistered());
        try {
            channel.register();
            Assert.fail();
        } catch (IllegalStateException expected) {
            // This is expected the channel is registered already on an EventLoop.
        }
        Assert.assertFalse(channel.finish());
    }

    @Test(timeout = 2000)
    public void promiseDoesNotInfiniteLoop() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.channel().close();
            }
        });
        channel.close().syncUninterruptibly();
    }

    @Test
    public void testConstructWithChannelInitializer() {
        final Integer first = 1;
        final Integer second = 2;
        final ChannelHandler handler = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ctx.fireChannelRead(first);
                ctx.fireChannelRead(second);
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(new io.netty.channel.ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast(handler);
            }
        });
        ChannelPipeline pipeline = channel.pipeline();
        Assert.assertSame(handler, pipeline.firstContext().handler());
        Assert.assertTrue(channel.writeInbound(3));
        Assert.assertTrue(channel.finish());
        Assert.assertSame(first, channel.readInbound());
        Assert.assertSame(second, channel.readInbound());
        Assert.assertNull(channel.readInbound());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testScheduling() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        final CountDownLatch latch = new CountDownLatch(2);
        ScheduledFuture future = ch.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        }, 1, TimeUnit.SECONDS);
        future.addListener(new FutureListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                latch.countDown();
            }
        });
        long next = ch.runScheduledPendingTasks();
        Assert.assertTrue((next > 0));
        // Sleep for the nanoseconds but also give extra 50ms as the clock my not be very precise and so fail the test
        // otherwise.
        Thread.sleep(((TimeUnit.NANOSECONDS.toMillis(next)) + 50));
        Assert.assertEquals((-1), ch.runScheduledPendingTasks());
        latch.await();
    }

    @Test
    public void testScheduledCancelled() throws Exception {
        EmbeddedChannel ch = new EmbeddedChannel(new ChannelInboundHandlerAdapter());
        ScheduledFuture<?> future = ch.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
            }
        }, 1, TimeUnit.DAYS);
        ch.finish();
        Assert.assertTrue(future.isCancelled());
    }

    @Test(timeout = 3000)
    public void testHandlerAddedExecutedInEventLoop() throws Throwable {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        final ChannelHandler handler = new ChannelHandlerAdapter() {
            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                try {
                    Assert.assertTrue(ctx.executor().inEventLoop());
                } catch (Throwable cause) {
                    error.set(cause);
                } finally {
                    latch.countDown();
                }
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        Assert.assertFalse(channel.finish());
        latch.await();
        Throwable cause = error.get();
        if (cause != null) {
            throw cause;
        }
    }

    @Test
    public void testConstructWithOutHandler() {
        EmbeddedChannel channel = new EmbeddedChannel();
        Assert.assertTrue(channel.writeInbound(1));
        Assert.assertTrue(channel.writeOutbound(2));
        Assert.assertTrue(channel.finish());
        Assert.assertSame(1, channel.readInbound());
        Assert.assertNull(channel.readInbound());
        Assert.assertSame(2, channel.readOutbound());
        Assert.assertNull(channel.readOutbound());
    }

    @Test
    public void testConstructWithChannelId() {
        ChannelId channelId = new CustomChannelId(1);
        EmbeddedChannel channel = new EmbeddedChannel(channelId);
        Assert.assertSame(channelId, channel.id());
    }

    // See https://github.com/netty/netty/issues/4316.
    @Test(timeout = 2000)
    public void testFireChannelInactiveAndUnregisteredOnClose() throws InterruptedException {
        EmbeddedChannelTest.testFireChannelInactiveAndUnregistered(new EmbeddedChannelTest.Action() {
            @Override
            public ChannelFuture doRun(Channel channel) {
                return channel.close();
            }
        });
        EmbeddedChannelTest.testFireChannelInactiveAndUnregistered(new EmbeddedChannelTest.Action() {
            @Override
            public ChannelFuture doRun(Channel channel) {
                return channel.close(channel.newPromise());
            }
        });
    }

    @Test(timeout = 2000)
    public void testFireChannelInactiveAndUnregisteredOnDisconnect() throws InterruptedException {
        EmbeddedChannelTest.testFireChannelInactiveAndUnregistered(new EmbeddedChannelTest.Action() {
            @Override
            public ChannelFuture doRun(Channel channel) {
                return channel.disconnect();
            }
        });
        EmbeddedChannelTest.testFireChannelInactiveAndUnregistered(new EmbeddedChannelTest.Action() {
            @Override
            public ChannelFuture doRun(Channel channel) {
                return channel.disconnect(channel.newPromise());
            }
        });
    }

    private interface Action {
        ChannelFuture doRun(Channel channel);
    }

    @Test
    public void testHasDisconnect() {
        EmbeddedChannelTest.EventOutboundHandler handler = new EmbeddedChannelTest.EventOutboundHandler();
        EmbeddedChannel channel = new EmbeddedChannel(true, handler);
        Assert.assertTrue(channel.disconnect().isSuccess());
        Assert.assertTrue(channel.close().isSuccess());
        Assert.assertEquals(EmbeddedChannelTest.EventOutboundHandler.DISCONNECT, handler.pollEvent());
        Assert.assertEquals(EmbeddedChannelTest.EventOutboundHandler.CLOSE, handler.pollEvent());
        Assert.assertNull(handler.pollEvent());
    }

    @Test
    public void testHasNoDisconnect() {
        EmbeddedChannelTest.EventOutboundHandler handler = new EmbeddedChannelTest.EventOutboundHandler();
        EmbeddedChannel channel = new EmbeddedChannel(false, handler);
        Assert.assertTrue(channel.disconnect().isSuccess());
        Assert.assertTrue(channel.close().isSuccess());
        Assert.assertEquals(EmbeddedChannelTest.EventOutboundHandler.CLOSE, handler.pollEvent());
        Assert.assertEquals(EmbeddedChannelTest.EventOutboundHandler.CLOSE, handler.pollEvent());
        Assert.assertNull(handler.pollEvent());
    }

    @Test
    public void testFinishAndReleaseAll() {
        ByteBuf in = Unpooled.buffer();
        ByteBuf out = Unpooled.buffer();
        try {
            EmbeddedChannel channel = new EmbeddedChannel();
            Assert.assertTrue(channel.writeInbound(in));
            Assert.assertEquals(1, in.refCnt());
            Assert.assertTrue(channel.writeOutbound(out));
            Assert.assertEquals(1, out.refCnt());
            Assert.assertTrue(channel.finishAndReleaseAll());
            Assert.assertEquals(0, in.refCnt());
            Assert.assertEquals(0, out.refCnt());
            Assert.assertNull(channel.readInbound());
            Assert.assertNull(channel.readOutbound());
        } finally {
            EmbeddedChannelTest.release(in, out);
        }
    }

    @Test
    public void testReleaseInbound() {
        ByteBuf in = Unpooled.buffer();
        ByteBuf out = Unpooled.buffer();
        try {
            EmbeddedChannel channel = new EmbeddedChannel();
            Assert.assertTrue(channel.writeInbound(in));
            Assert.assertEquals(1, in.refCnt());
            Assert.assertTrue(channel.writeOutbound(out));
            Assert.assertEquals(1, out.refCnt());
            Assert.assertTrue(channel.releaseInbound());
            Assert.assertEquals(0, in.refCnt());
            Assert.assertEquals(1, out.refCnt());
            Assert.assertTrue(channel.finish());
            Assert.assertNull(channel.readInbound());
            ByteBuf buffer = channel.readOutbound();
            Assert.assertSame(out, buffer);
            buffer.release();
            Assert.assertNull(channel.readOutbound());
        } finally {
            EmbeddedChannelTest.release(in, out);
        }
    }

    @Test
    public void testReleaseOutbound() {
        ByteBuf in = Unpooled.buffer();
        ByteBuf out = Unpooled.buffer();
        try {
            EmbeddedChannel channel = new EmbeddedChannel();
            Assert.assertTrue(channel.writeInbound(in));
            Assert.assertEquals(1, in.refCnt());
            Assert.assertTrue(channel.writeOutbound(out));
            Assert.assertEquals(1, out.refCnt());
            Assert.assertTrue(channel.releaseOutbound());
            Assert.assertEquals(1, in.refCnt());
            Assert.assertEquals(0, out.refCnt());
            Assert.assertTrue(channel.finish());
            Assert.assertNull(channel.readOutbound());
            ByteBuf buffer = channel.readInbound();
            Assert.assertSame(in, buffer);
            buffer.release();
            Assert.assertNull(channel.readInbound());
        } finally {
            EmbeddedChannelTest.release(in, out);
        }
    }

    @Test
    public void testWriteLater() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
                ctx.executor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ctx.write(msg, promise);
                    }
                });
            }
        });
        Object msg = new Object();
        Assert.assertTrue(channel.writeOutbound(msg));
        Assert.assertTrue(channel.finish());
        Assert.assertSame(msg, channel.readOutbound());
        Assert.assertNull(channel.readOutbound());
    }

    @Test
    public void testWriteScheduled() throws InterruptedException {
        final int delay = 500;
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
                ctx.executor().schedule(new Runnable() {
                    @Override
                    public void run() {
                        ctx.writeAndFlush(msg, promise);
                    }
                }, delay, TimeUnit.MILLISECONDS);
            }
        });
        Object msg = new Object();
        Assert.assertFalse(channel.writeOutbound(msg));
        Thread.sleep((delay * 2));
        Assert.assertTrue(channel.finish());
        Assert.assertSame(msg, channel.readOutbound());
        Assert.assertNull(channel.readOutbound());
    }

    @Test
    public void testFlushInbound() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                latch.countDown();
            }
        });
        channel.flushInbound();
        if (!(latch.await(1L, TimeUnit.SECONDS))) {
            Assert.fail("Nobody called #channelReadComplete() in time.");
        }
    }

    @Test
    public void testWriteOneInbound() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger flushCount = new AtomicInteger(0);
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ReferenceCountUtil.release(msg);
                latch.countDown();
            }

            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                flushCount.incrementAndGet();
            }
        });
        channel.writeOneInbound("Hello, Netty!");
        if (!(latch.await(1L, TimeUnit.SECONDS))) {
            Assert.fail("Nobody called #channelRead() in time.");
        }
        channel.close().syncUninterruptibly();
        // There was no #flushInbound() call so nobody should have called
        // #channelReadComplete()
        Assert.assertEquals(0, flushCount.get());
    }

    @Test
    public void testFlushOutbound() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelOutboundHandlerAdapter() {
            @Override
            public void flush(ChannelHandlerContext ctx) throws Exception {
                latch.countDown();
            }
        });
        channel.flushOutbound();
        if (!(latch.await(1L, TimeUnit.SECONDS))) {
            Assert.fail("Nobody called #flush() in time.");
        }
    }

    @Test
    public void testWriteOneOutbound() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger flushCount = new AtomicInteger(0);
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                ctx.write(msg, promise);
                latch.countDown();
            }

            @Override
            public void flush(ChannelHandlerContext ctx) throws Exception {
                flushCount.incrementAndGet();
            }
        });
        // This shouldn't trigger a #flush()
        channel.writeOneOutbound("Hello, Netty!");
        if (!(latch.await(1L, TimeUnit.SECONDS))) {
            Assert.fail("Nobody called #write() in time.");
        }
        channel.close().syncUninterruptibly();
        // There was no #flushOutbound() call so nobody should have called #flush()
        Assert.assertEquals(0, flushCount.get());
    }

    @Test
    public void testEnsureOpen() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.close().syncUninterruptibly();
        try {
            channel.writeOutbound("Hello, Netty!");
            Assert.fail("This should have failed with a ClosedChannelException");
        } catch (Exception expected) {
            Assert.assertTrue((expected instanceof ClosedChannelException));
        }
        try {
            channel.writeInbound("Hello, Netty!");
            Assert.fail("This should have failed with a ClosedChannelException");
        } catch (Exception expected) {
            Assert.assertTrue((expected instanceof ClosedChannelException));
        }
    }

    @Test
    public void testHandleInboundMessage() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        EmbeddedChannel channel = new EmbeddedChannel() {
            @Override
            protected void handleInboundMessage(Object msg) {
                latch.countDown();
            }
        };
        channel.writeOneInbound("Hello, Netty!");
        if (!(latch.await(1L, TimeUnit.SECONDS))) {
            Assert.fail("Nobody called #handleInboundMessage() in time.");
        }
    }

    @Test
    public void testHandleOutboundMessage() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        EmbeddedChannel channel = new EmbeddedChannel() {
            @Override
            protected void handleOutboundMessage(Object msg) {
                latch.countDown();
            }
        };
        channel.writeOneOutbound("Hello, Netty!");
        if (latch.await(50L, TimeUnit.MILLISECONDS)) {
            Assert.fail("Somebody called unexpectedly #flush()");
        }
        channel.flushOutbound();
        if (!(latch.await(1L, TimeUnit.SECONDS))) {
            Assert.fail("Nobody called #handleOutboundMessage() in time.");
        }
    }

    @Test(timeout = 5000)
    public void testChannelInactiveFired() throws InterruptedException {
        final AtomicBoolean inactive = new AtomicBoolean();
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                ctx.close();
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                inactive.set(true);
            }
        });
        channel.pipeline().fireExceptionCaught(new IllegalStateException());
        Assert.assertTrue(inactive.get());
    }

    private static final class EventOutboundHandler extends ChannelOutboundHandlerAdapter {
        static final Integer DISCONNECT = 0;

        static final Integer CLOSE = 1;

        private final Queue<Integer> queue = new ArrayDeque<Integer>();

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            queue.add(EmbeddedChannelTest.EventOutboundHandler.DISCONNECT);
            promise.setSuccess();
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            queue.add(EmbeddedChannelTest.EventOutboundHandler.CLOSE);
            promise.setSuccess();
        }

        Integer pollEvent() {
            return queue.poll();
        }
    }
}

