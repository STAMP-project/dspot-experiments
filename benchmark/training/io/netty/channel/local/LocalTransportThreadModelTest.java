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


import Unpooled.EMPTY_BUFFER;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutorGroup;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class LocalTransportThreadModelTest {
    private static EventLoopGroup group;

    private static LocalAddress localAddr;

    @Test(timeout = 5000)
    public void testStagedExecution() throws Throwable {
        EventLoopGroup l = new io.netty.channel.DefaultEventLoopGroup(4, new DefaultThreadFactory("l"));
        EventExecutorGroup e1 = new io.netty.util.concurrent.DefaultEventExecutorGroup(4, new DefaultThreadFactory("e1"));
        EventExecutorGroup e2 = new io.netty.util.concurrent.DefaultEventExecutorGroup(4, new DefaultThreadFactory("e2"));
        LocalTransportThreadModelTest.ThreadNameAuditor h1 = new LocalTransportThreadModelTest.ThreadNameAuditor();
        LocalTransportThreadModelTest.ThreadNameAuditor h2 = new LocalTransportThreadModelTest.ThreadNameAuditor();
        LocalTransportThreadModelTest.ThreadNameAuditor h3 = new LocalTransportThreadModelTest.ThreadNameAuditor(true);
        Channel ch = new LocalChannel();
        // With no EventExecutor specified, h1 will be always invoked by EventLoop 'l'.
        ch.pipeline().addLast(h1);
        // h2 will be always invoked by EventExecutor 'e1'.
        ch.pipeline().addLast(e1, h2);
        // h3 will be always invoked by EventExecutor 'e2'.
        ch.pipeline().addLast(e2, h3);
        l.register(ch).sync().channel().connect(LocalTransportThreadModelTest.localAddr).sync();
        // Fire inbound events from all possible starting points.
        ch.pipeline().fireChannelRead("1");
        ch.pipeline().context(h1).fireChannelRead("2");
        ch.pipeline().context(h2).fireChannelRead("3");
        ch.pipeline().context(h3).fireChannelRead("4");
        // Fire outbound events from all possible starting points.
        ch.pipeline().write("5");
        ch.pipeline().context(h3).write("6");
        ch.pipeline().context(h2).write("7");
        ch.pipeline().context(h1).writeAndFlush("8").sync();
        ch.close().sync();
        // Wait until all events are handled completely.
        while ((((h1.outboundThreadNames.size()) < 3) || ((h3.inboundThreadNames.size()) < 3)) || ((h1.removalThreadNames.size()) < 1)) {
            if ((h1.exception.get()) != null) {
                throw h1.exception.get();
            }
            if ((h2.exception.get()) != null) {
                throw h2.exception.get();
            }
            if ((h3.exception.get()) != null) {
                throw h3.exception.get();
            }
            Thread.sleep(10);
        } 
        String currentName = Thread.currentThread().getName();
        try {
            // Events should never be handled from the current thread.
            Assert.assertFalse(h1.inboundThreadNames.contains(currentName));
            Assert.assertFalse(h2.inboundThreadNames.contains(currentName));
            Assert.assertFalse(h3.inboundThreadNames.contains(currentName));
            Assert.assertFalse(h1.outboundThreadNames.contains(currentName));
            Assert.assertFalse(h2.outboundThreadNames.contains(currentName));
            Assert.assertFalse(h3.outboundThreadNames.contains(currentName));
            Assert.assertFalse(h1.removalThreadNames.contains(currentName));
            Assert.assertFalse(h2.removalThreadNames.contains(currentName));
            Assert.assertFalse(h3.removalThreadNames.contains(currentName));
            // Assert that events were handled by the correct executor.
            for (String name : h1.inboundThreadNames) {
                Assert.assertTrue(name.startsWith("l-"));
            }
            for (String name : h2.inboundThreadNames) {
                Assert.assertTrue(name.startsWith("e1-"));
            }
            for (String name : h3.inboundThreadNames) {
                Assert.assertTrue(name.startsWith("e2-"));
            }
            for (String name : h1.outboundThreadNames) {
                Assert.assertTrue(name.startsWith("l-"));
            }
            for (String name : h2.outboundThreadNames) {
                Assert.assertTrue(name.startsWith("e1-"));
            }
            for (String name : h3.outboundThreadNames) {
                Assert.assertTrue(name.startsWith("e2-"));
            }
            for (String name : h1.removalThreadNames) {
                Assert.assertTrue(name.startsWith("l-"));
            }
            for (String name : h2.removalThreadNames) {
                Assert.assertTrue(name.startsWith("e1-"));
            }
            for (String name : h3.removalThreadNames) {
                Assert.assertTrue(name.startsWith("e2-"));
            }
            // Assert that the events for the same handler were handled by the same thread.
            Set<String> names = new HashSet<String>();
            names.addAll(h1.inboundThreadNames);
            names.addAll(h1.outboundThreadNames);
            names.addAll(h1.removalThreadNames);
            Assert.assertEquals(1, names.size());
            names.clear();
            names.addAll(h2.inboundThreadNames);
            names.addAll(h2.outboundThreadNames);
            names.addAll(h2.removalThreadNames);
            Assert.assertEquals(1, names.size());
            names.clear();
            names.addAll(h3.inboundThreadNames);
            names.addAll(h3.outboundThreadNames);
            names.addAll(h3.removalThreadNames);
            Assert.assertEquals(1, names.size());
            // Count the number of events
            Assert.assertEquals(1, h1.inboundThreadNames.size());
            Assert.assertEquals(2, h2.inboundThreadNames.size());
            Assert.assertEquals(3, h3.inboundThreadNames.size());
            Assert.assertEquals(3, h1.outboundThreadNames.size());
            Assert.assertEquals(2, h2.outboundThreadNames.size());
            Assert.assertEquals(1, h3.outboundThreadNames.size());
            Assert.assertEquals(1, h1.removalThreadNames.size());
            Assert.assertEquals(1, h2.removalThreadNames.size());
            Assert.assertEquals(1, h3.removalThreadNames.size());
        } catch (AssertionError e) {
            System.out.println(("H1I: " + (h1.inboundThreadNames)));
            System.out.println(("H2I: " + (h2.inboundThreadNames)));
            System.out.println(("H3I: " + (h3.inboundThreadNames)));
            System.out.println(("H1O: " + (h1.outboundThreadNames)));
            System.out.println(("H2O: " + (h2.outboundThreadNames)));
            System.out.println(("H3O: " + (h3.outboundThreadNames)));
            System.out.println(("H1R: " + (h1.removalThreadNames)));
            System.out.println(("H2R: " + (h2.removalThreadNames)));
            System.out.println(("H3R: " + (h3.removalThreadNames)));
            throw e;
        } finally {
            l.shutdownGracefully();
            e1.shutdownGracefully();
            e2.shutdownGracefully();
            l.terminationFuture().sync();
            e1.terminationFuture().sync();
            e2.terminationFuture().sync();
        }
    }

    private static class ThreadNameAuditor extends ChannelDuplexHandler {
        private final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();

        private final Queue<String> inboundThreadNames = new ConcurrentLinkedQueue<String>();

        private final Queue<String> outboundThreadNames = new ConcurrentLinkedQueue<String>();

        private final Queue<String> removalThreadNames = new ConcurrentLinkedQueue<String>();

        private final boolean discard;

        ThreadNameAuditor() {
            this(false);
        }

        ThreadNameAuditor(boolean discard) {
            this.discard = discard;
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            removalThreadNames.add(Thread.currentThread().getName());
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            inboundThreadNames.add(Thread.currentThread().getName());
            if (!(discard)) {
                ctx.fireChannelRead(msg);
            }
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            outboundThreadNames.add(Thread.currentThread().getName());
            ctx.write(msg, promise);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            exception.compareAndSet(null, cause);
            System.err.print((('[' + (Thread.currentThread().getName())) + "] "));
            cause.printStackTrace();
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * Converts integers into a binary stream.
     */
    private static class MessageForwarder1 extends ChannelDuplexHandler {
        private final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();

        private volatile int inCnt;

        private volatile int outCnt;

        private volatile Thread t;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Thread t = this.t;
            if (t == null) {
                this.t = Thread.currentThread();
            } else {
                Assert.assertSame(t, Thread.currentThread());
            }
            ByteBuf out = ctx.alloc().buffer(4);
            int m = ((Integer) (msg)).intValue();
            int expected = (inCnt)++;
            Assert.assertEquals(expected, m);
            out.writeInt(m);
            ctx.fireChannelRead(out);
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            Assert.assertSame(t, Thread.currentThread());
            // Don't let the write request go to the server-side channel - just swallow.
            boolean swallow = (this) == (ctx.pipeline().first());
            ByteBuf m = ((ByteBuf) (msg));
            int count = (m.readableBytes()) / 4;
            for (int j = 0; j < count; j++) {
                int actual = m.readInt();
                int expected = (outCnt)++;
                Assert.assertEquals(expected, actual);
                if (!swallow) {
                    ctx.write(actual);
                }
            }
            ctx.writeAndFlush(EMPTY_BUFFER, promise);
            m.release();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            exception.compareAndSet(null, cause);
            // System.err.print("[" + Thread.currentThread().getName() + "] ");
            // cause.printStackTrace();
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * Converts a binary stream into integers.
     */
    private static class MessageForwarder2 extends ChannelDuplexHandler {
        private final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();

        private volatile int inCnt;

        private volatile int outCnt;

        private volatile Thread t;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Thread t = this.t;
            if (t == null) {
                this.t = Thread.currentThread();
            } else {
                Assert.assertSame(t, Thread.currentThread());
            }
            ByteBuf m = ((ByteBuf) (msg));
            int count = (m.readableBytes()) / 4;
            for (int j = 0; j < count; j++) {
                int actual = m.readInt();
                int expected = (inCnt)++;
                Assert.assertEquals(expected, actual);
                ctx.fireChannelRead(actual);
            }
            m.release();
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            Assert.assertSame(t, Thread.currentThread());
            ByteBuf out = ctx.alloc().buffer(4);
            int m = ((Integer) (msg));
            int expected = (outCnt)++;
            Assert.assertEquals(expected, m);
            out.writeInt(m);
            ctx.write(out, promise);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            exception.compareAndSet(null, cause);
            // System.err.print("[" + Thread.currentThread().getName() + "] ");
            // cause.printStackTrace();
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * Simply forwards the received object to the next handler.
     */
    private static class MessageForwarder3 extends ChannelDuplexHandler {
        private final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();

        private volatile int inCnt;

        private volatile int outCnt;

        private volatile Thread t;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Thread t = this.t;
            if (t == null) {
                this.t = Thread.currentThread();
            } else {
                Assert.assertSame(t, Thread.currentThread());
            }
            int actual = ((Integer) (msg));
            int expected = (inCnt)++;
            Assert.assertEquals(expected, actual);
            ctx.fireChannelRead(msg);
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            Assert.assertSame(t, Thread.currentThread());
            int actual = ((Integer) (msg));
            int expected = (outCnt)++;
            Assert.assertEquals(expected, actual);
            ctx.write(msg, promise);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            exception.compareAndSet(null, cause);
            System.err.print((('[' + (Thread.currentThread().getName())) + "] "));
            cause.printStackTrace();
            super.exceptionCaught(ctx, cause);
        }
    }

    /**
     * Discards all received messages.
     */
    private static class MessageDiscarder extends ChannelDuplexHandler {
        private final AtomicReference<Throwable> exception = new AtomicReference<Throwable>();

        private volatile int inCnt;

        private volatile int outCnt;

        private volatile Thread t;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Thread t = this.t;
            if (t == null) {
                this.t = Thread.currentThread();
            } else {
                Assert.assertSame(t, Thread.currentThread());
            }
            int actual = ((Integer) (msg));
            int expected = (inCnt)++;
            Assert.assertEquals(expected, actual);
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            Assert.assertSame(t, Thread.currentThread());
            int actual = ((Integer) (msg));
            int expected = (outCnt)++;
            Assert.assertEquals(expected, actual);
            ctx.write(msg, promise);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            exception.compareAndSet(null, cause);
            // System.err.print("[" + Thread.currentThread().getName() + "] ");
            // cause.printStackTrace();
            super.exceptionCaught(ctx, cause);
        }
    }
}

