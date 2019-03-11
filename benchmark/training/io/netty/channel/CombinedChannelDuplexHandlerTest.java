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


import io.netty.channel.embedded.EmbeddedChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;


public class CombinedChannelDuplexHandlerTest {
    private static final Object MSG = new Object();

    private static final SocketAddress ADDRESS = new InetSocketAddress(0);

    private enum Event {

        REGISTERED,
        UNREGISTERED,
        ACTIVE,
        INACTIVE,
        CHANNEL_READ,
        CHANNEL_READ_COMPLETE,
        EXCEPTION_CAUGHT,
        USER_EVENT_TRIGGERED,
        CHANNEL_WRITABILITY_CHANGED,
        HANDLER_ADDED,
        HANDLER_REMOVED,
        BIND,
        CONNECT,
        WRITE,
        FLUSH,
        READ,
        REGISTER,
        DEREGISTER,
        CLOSE,
        DISCONNECT;}

    @Test(expected = IllegalStateException.class)
    public void testInboundRemoveBeforeAdded() {
        CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler> handler = new CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler>(new ChannelInboundHandlerAdapter(), new ChannelOutboundHandlerAdapter());
        handler.removeInboundHandler();
    }

    @Test(expected = IllegalStateException.class)
    public void testOutboundRemoveBeforeAdded() {
        CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler> handler = new CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler>(new ChannelInboundHandlerAdapter(), new ChannelOutboundHandlerAdapter());
        handler.removeOutboundHandler();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInboundHandlerImplementsOutboundHandler() {
        new CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler>(new ChannelDuplexHandler(), new ChannelOutboundHandlerAdapter());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOutboundHandlerImplementsInboundHandler() {
        new CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler>(new ChannelInboundHandlerAdapter(), new ChannelDuplexHandler());
    }

    @Test(expected = IllegalStateException.class)
    public void testInitNotCalledBeforeAdded() throws Exception {
        CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler> handler = new CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler>() {};
        handler.handlerAdded(null);
    }

    @Test
    public void testExceptionCaughtBothCombinedHandlers() {
        final Exception exception = new Exception();
        final Queue<ChannelHandler> queue = new ArrayDeque<ChannelHandler>();
        ChannelInboundHandler inboundHandler = new ChannelInboundHandlerAdapter() {
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                Assert.assertSame(exception, cause);
                queue.add(this);
                ctx.fireExceptionCaught(cause);
            }
        };
        ChannelOutboundHandler outboundHandler = new ChannelOutboundHandlerAdapter() {
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                Assert.assertSame(exception, cause);
                queue.add(this);
                ctx.fireExceptionCaught(cause);
            }
        };
        ChannelInboundHandler lastHandler = new ChannelInboundHandlerAdapter() {
            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                Assert.assertSame(exception, cause);
                queue.add(this);
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(new CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler>(inboundHandler, outboundHandler), lastHandler);
        channel.pipeline().fireExceptionCaught(exception);
        Assert.assertFalse(channel.finish());
        Assert.assertSame(inboundHandler, queue.poll());
        Assert.assertSame(outboundHandler, queue.poll());
        Assert.assertSame(lastHandler, queue.poll());
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void testInboundEvents() {
        final Queue<CombinedChannelDuplexHandlerTest.Event> queue = new ArrayDeque<CombinedChannelDuplexHandlerTest.Event>();
        ChannelInboundHandler inboundHandler = new ChannelInboundHandlerAdapter() {
            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.HANDLER_ADDED);
            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.HANDLER_REMOVED);
            }

            @Override
            public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.REGISTERED);
            }

            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.UNREGISTERED);
            }

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.ACTIVE);
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.INACTIVE);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.CHANNEL_READ);
            }

            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.CHANNEL_READ_COMPLETE);
            }

            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.USER_EVENT_TRIGGERED);
            }

            @Override
            public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.CHANNEL_WRITABILITY_CHANGED);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.EXCEPTION_CAUGHT);
            }
        };
        CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler> handler = new CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler>(inboundHandler, new ChannelOutboundHandlerAdapter());
        EmbeddedChannel channel = new EmbeddedChannel(handler);
        channel.pipeline().fireChannelWritabilityChanged();
        channel.pipeline().fireUserEventTriggered(CombinedChannelDuplexHandlerTest.MSG);
        channel.pipeline().fireChannelRead(CombinedChannelDuplexHandlerTest.MSG);
        channel.pipeline().fireChannelReadComplete();
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.HANDLER_ADDED, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.REGISTERED, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.ACTIVE, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.CHANNEL_WRITABILITY_CHANGED, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.USER_EVENT_TRIGGERED, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.CHANNEL_READ, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.CHANNEL_READ_COMPLETE, queue.poll());
        handler.removeInboundHandler();
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.HANDLER_REMOVED, queue.poll());
        // These should not be handled by the inboundHandler anymore as it was removed before
        channel.pipeline().fireChannelWritabilityChanged();
        channel.pipeline().fireUserEventTriggered(CombinedChannelDuplexHandlerTest.MSG);
        channel.pipeline().fireChannelRead(CombinedChannelDuplexHandlerTest.MSG);
        channel.pipeline().fireChannelReadComplete();
        // Should have not received any more events as it was removed before via removeInboundHandler()
        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(channel.finish());
        Assert.assertTrue(queue.isEmpty());
    }

    @Test
    public void testOutboundEvents() {
        final Queue<CombinedChannelDuplexHandlerTest.Event> queue = new ArrayDeque<CombinedChannelDuplexHandlerTest.Event>();
        ChannelInboundHandler inboundHandler = new ChannelInboundHandlerAdapter();
        ChannelOutboundHandler outboundHandler = new ChannelOutboundHandlerAdapter() {
            @Override
            public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.HANDLER_ADDED);
            }

            @Override
            public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.HANDLER_REMOVED);
            }

            @Override
            public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.BIND);
            }

            @Override
            public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.CONNECT);
            }

            @Override
            public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.DISCONNECT);
            }

            @Override
            public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.CLOSE);
            }

            @Override
            public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.DEREGISTER);
            }

            @Override
            public void read(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.READ);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.WRITE);
            }

            @Override
            public void flush(ChannelHandlerContext ctx) throws Exception {
                queue.add(CombinedChannelDuplexHandlerTest.Event.FLUSH);
            }
        };
        CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler> handler = new CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler>(inboundHandler, outboundHandler);
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addFirst(handler);
        CombinedChannelDuplexHandlerTest.doOutboundOperations(channel);
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.HANDLER_ADDED, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.BIND, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.CONNECT, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.WRITE, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.FLUSH, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.READ, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.CLOSE, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.CLOSE, queue.poll());
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.DEREGISTER, queue.poll());
        handler.removeOutboundHandler();
        Assert.assertEquals(CombinedChannelDuplexHandlerTest.Event.HANDLER_REMOVED, queue.poll());
        // These should not be handled by the inboundHandler anymore as it was removed before
        CombinedChannelDuplexHandlerTest.doOutboundOperations(channel);
        // Should have not received any more events as it was removed before via removeInboundHandler()
        Assert.assertTrue(queue.isEmpty());
        Assert.assertTrue(channel.finish());
        Assert.assertTrue(queue.isEmpty());
    }

    @Test(timeout = 3000)
    public void testPromisesPassed() {
        ChannelOutboundHandler outboundHandler = new ChannelOutboundHandlerAdapter() {
            @Override
            public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
                promise.setSuccess();
            }

            @Override
            public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
                promise.setSuccess();
            }

            @Override
            public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                promise.setSuccess();
            }

            @Override
            public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                promise.setSuccess();
            }

            @Override
            public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
                promise.setSuccess();
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                promise.setSuccess();
            }
        };
        EmbeddedChannel ch = new EmbeddedChannel(outboundHandler, new CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler>(new ChannelInboundHandlerAdapter(), new ChannelOutboundHandlerAdapter()));
        ChannelPipeline pipeline = ch.pipeline();
        ChannelPromise promise = ch.newPromise();
        pipeline.connect(new InetSocketAddress(0), null, promise);
        promise.syncUninterruptibly();
        promise = ch.newPromise();
        pipeline.bind(new InetSocketAddress(0), promise);
        promise.syncUninterruptibly();
        promise = ch.newPromise();
        pipeline.close(promise);
        promise.syncUninterruptibly();
        promise = ch.newPromise();
        pipeline.disconnect(promise);
        promise.syncUninterruptibly();
        promise = ch.newPromise();
        pipeline.write("test", promise);
        promise.syncUninterruptibly();
        promise = ch.newPromise();
        pipeline.deregister(promise);
        promise.syncUninterruptibly();
        ch.finish();
    }

    @Test(expected = IllegalStateException.class)
    public void testNotSharable() {
        new CombinedChannelDuplexHandler<ChannelInboundHandler, ChannelOutboundHandler>() {
            @Override
            public boolean isSharable() {
                return true;
            }
        };
    }
}

