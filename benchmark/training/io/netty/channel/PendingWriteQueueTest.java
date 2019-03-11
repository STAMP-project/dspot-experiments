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
package io.netty.channel;


import CharsetUtil.US_ASCII;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class PendingWriteQueueTest {
    @Test
    public void testRemoveAndWrite() {
        PendingWriteQueueTest.assertWrite(new PendingWriteQueueTest.TestHandler() {
            @Override
            public void flush(ChannelHandlerContext ctx) throws Exception {
                Assert.assertFalse("Should not be writable anymore", ctx.channel().isWritable());
                ChannelFuture future = queue.removeAndWrite();
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        PendingWriteQueueTest.assertQueueEmpty(queue);
                    }
                });
                super.flush(ctx);
            }
        }, 1);
    }

    @Test
    public void testRemoveAndWriteAll() {
        PendingWriteQueueTest.assertWrite(new PendingWriteQueueTest.TestHandler() {
            @Override
            public void flush(ChannelHandlerContext ctx) throws Exception {
                Assert.assertFalse("Should not be writable anymore", ctx.channel().isWritable());
                ChannelFuture future = queue.removeAndWriteAll();
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        PendingWriteQueueTest.assertQueueEmpty(queue);
                    }
                });
                super.flush(ctx);
            }
        }, 3);
    }

    @Test
    public void testRemoveAndFail() {
        PendingWriteQueueTest.assertWriteFails(new PendingWriteQueueTest.TestHandler() {
            @Override
            public void flush(ChannelHandlerContext ctx) throws Exception {
                queue.removeAndFail(new PendingWriteQueueTest.TestException());
                super.flush(ctx);
            }
        }, 1);
    }

    @Test
    public void testRemoveAndFailAll() {
        PendingWriteQueueTest.assertWriteFails(new PendingWriteQueueTest.TestHandler() {
            @Override
            public void flush(ChannelHandlerContext ctx) throws Exception {
                queue.removeAndFailAll(new PendingWriteQueueTest.TestException());
                super.flush(ctx);
            }
        }, 3);
    }

    @Test
    public void shouldFireChannelWritabilityChangedAfterRemoval() {
        final AtomicReference<ChannelHandlerContext> ctxRef = new AtomicReference<ChannelHandlerContext>();
        final AtomicReference<PendingWriteQueue> queueRef = new AtomicReference<PendingWriteQueue>();
        final ByteBuf msg = Unpooled.copiedBuffer("test", US_ASCII);
        final EmbeddedChannel channel = new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                ctxRef.set(ctx);
                queueRef.set(new PendingWriteQueue(ctx));
            }

            @Override
            public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
                final PendingWriteQueue queue = queueRef.get();
                final ByteBuf msg = ((ByteBuf) (queue.current()));
                if (msg == null) {
                    return;
                }
                Assert.assertThat(msg.refCnt(), is(1));
                // This call will trigger another channelWritabilityChanged() event because the number of
                // pending bytes will go below the low watermark.
                // 
                // If PendingWriteQueue.remove() did not remove the current entry before triggering
                // channelWritabilityChanged() event, we will end up with attempting to remove the same
                // element twice, resulting in the double release.
                queue.remove();
                Assert.assertThat(msg.refCnt(), is(0));
            }
        });
        channel.config().setWriteBufferLowWaterMark(1);
        channel.config().setWriteBufferHighWaterMark(3);
        final PendingWriteQueue queue = queueRef.get();
        // Trigger channelWritabilityChanged() by adding a message that's larger than the high watermark.
        queue.add(msg, channel.newPromise());
        channel.finish();
        Assert.assertThat(msg.refCnt(), is(0));
    }

    @Test
    public void testRemoveAndFailAllReentrantFailAll() {
        EmbeddedChannel channel = PendingWriteQueueTest.newChannel();
        final PendingWriteQueue queue = new PendingWriteQueue(channel.pipeline().firstContext());
        ChannelPromise promise = channel.newPromise();
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                queue.removeAndFailAll(new IllegalStateException());
            }
        });
        queue.add(1L, promise);
        ChannelPromise promise2 = channel.newPromise();
        queue.add(2L, promise2);
        queue.removeAndFailAll(new Exception());
        Assert.assertTrue(promise.isDone());
        Assert.assertFalse(promise.isSuccess());
        Assert.assertTrue(promise2.isDone());
        Assert.assertFalse(promise2.isSuccess());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testRemoveAndWriteAllReentrantWrite() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                // Convert to writeAndFlush(...) so the promise will be notified by the transport.
                ctx.writeAndFlush(msg, promise);
            }
        }, new ChannelOutboundHandlerAdapter());
        final PendingWriteQueue queue = new PendingWriteQueue(channel.pipeline().lastContext());
        ChannelPromise promise = channel.newPromise();
        final ChannelPromise promise3 = channel.newPromise();
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                queue.add(3L, promise3);
            }
        });
        queue.add(1L, promise);
        ChannelPromise promise2 = channel.newPromise();
        queue.add(2L, promise2);
        queue.removeAndWriteAll();
        Assert.assertTrue(promise.isDone());
        Assert.assertTrue(promise.isSuccess());
        Assert.assertTrue(promise2.isDone());
        Assert.assertTrue(promise2.isSuccess());
        Assert.assertTrue(promise3.isDone());
        Assert.assertTrue(promise3.isSuccess());
        Assert.assertTrue(channel.finish());
        Assert.assertEquals(1L, channel.readOutbound());
        Assert.assertEquals(2L, channel.readOutbound());
        Assert.assertEquals(3L, channel.readOutbound());
    }

    @Test
    public void testRemoveAndWriteAllWithVoidPromise() {
        EmbeddedChannel channel = new EmbeddedChannel(new ChannelOutboundHandlerAdapter() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                // Convert to writeAndFlush(...) so the promise will be notified by the transport.
                ctx.writeAndFlush(msg, promise);
            }
        }, new ChannelOutboundHandlerAdapter());
        final PendingWriteQueue queue = new PendingWriteQueue(channel.pipeline().lastContext());
        ChannelPromise promise = channel.newPromise();
        queue.add(1L, promise);
        queue.add(2L, channel.voidPromise());
        queue.removeAndWriteAll();
        Assert.assertTrue(channel.finish());
        Assert.assertTrue(promise.isDone());
        Assert.assertTrue(promise.isSuccess());
        Assert.assertEquals(1L, channel.readOutbound());
        Assert.assertEquals(2L, channel.readOutbound());
    }

    @Test
    public void testRemoveAndFailAllReentrantWrite() {
        final List<Integer> failOrder = Collections.synchronizedList(new ArrayList<Integer>());
        EmbeddedChannel channel = PendingWriteQueueTest.newChannel();
        final PendingWriteQueue queue = new PendingWriteQueue(channel.pipeline().firstContext());
        ChannelPromise promise = channel.newPromise();
        final ChannelPromise promise3 = channel.newPromise();
        promise3.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                failOrder.add(3);
            }
        });
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                failOrder.add(1);
                queue.add(3L, promise3);
            }
        });
        queue.add(1L, promise);
        ChannelPromise promise2 = channel.newPromise();
        promise2.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                failOrder.add(2);
            }
        });
        queue.add(2L, promise2);
        queue.removeAndFailAll(new Exception());
        Assert.assertTrue(promise.isDone());
        Assert.assertFalse(promise.isSuccess());
        Assert.assertTrue(promise2.isDone());
        Assert.assertFalse(promise2.isSuccess());
        Assert.assertTrue(promise3.isDone());
        Assert.assertFalse(promise3.isSuccess());
        Assert.assertFalse(channel.finish());
        Assert.assertEquals(1, ((int) (failOrder.get(0))));
        Assert.assertEquals(2, ((int) (failOrder.get(1))));
        Assert.assertEquals(3, ((int) (failOrder.get(2))));
    }

    @Test
    public void testRemoveAndWriteAllReentrance() {
        EmbeddedChannel channel = PendingWriteQueueTest.newChannel();
        final PendingWriteQueue queue = new PendingWriteQueue(channel.pipeline().firstContext());
        ChannelPromise promise = channel.newPromise();
        promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                queue.removeAndWriteAll();
            }
        });
        queue.add(1L, promise);
        ChannelPromise promise2 = channel.newPromise();
        queue.add(2L, promise2);
        queue.removeAndWriteAll();
        channel.flush();
        Assert.assertTrue(promise.isSuccess());
        Assert.assertTrue(promise2.isSuccess());
        Assert.assertTrue(channel.finish());
        Assert.assertEquals(1L, channel.readOutbound());
        Assert.assertEquals(2L, channel.readOutbound());
        Assert.assertNull(channel.readOutbound());
        Assert.assertNull(channel.readInbound());
    }

    // See https://github.com/netty/netty/issues/3967
    @Test
    public void testCloseChannelOnCreation() {
        EmbeddedChannel channel = PendingWriteQueueTest.newChannel();
        ChannelHandlerContext context = channel.pipeline().firstContext();
        channel.close().syncUninterruptibly();
        final PendingWriteQueue queue = new PendingWriteQueue(context);
        IllegalStateException ex = new IllegalStateException();
        ChannelPromise promise = channel.newPromise();
        queue.add(1L, promise);
        queue.removeAndFailAll(ex);
        Assert.assertSame(ex, promise.cause());
    }

    private static class TestHandler extends ChannelDuplexHandler {
        protected PendingWriteQueue queue;

        private int expectedSize;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            PendingWriteQueueTest.assertQueueEmpty(queue);
            Assert.assertTrue("Should be writable", ctx.channel().isWritable());
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            queue.add(msg, promise);
            Assert.assertFalse(queue.isEmpty());
            Assert.assertEquals((++(expectedSize)), queue.size());
            Assert.assertNotNull(queue.current());
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            queue = new PendingWriteQueue(ctx);
        }
    }

    private static final class TestException extends Exception {
        private static final long serialVersionUID = -9018570103039458401L;
    }
}

