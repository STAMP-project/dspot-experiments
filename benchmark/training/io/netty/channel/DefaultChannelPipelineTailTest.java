/**
 * Copyright 2017 The Netty Project
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


import io.netty.bootstrap.Bootstrap;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class DefaultChannelPipelineTailTest {
    private static EventLoopGroup GROUP;

    @Test
    public void testOnUnhandledInboundChannelActive() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        DefaultChannelPipelineTailTest.MyChannel myChannel = new DefaultChannelPipelineTailTest.MyChannel() {
            @Override
            protected void onUnhandledInboundChannelActive() {
                latch.countDown();
            }
        };
        Bootstrap bootstrap = new Bootstrap().channelFactory(new DefaultChannelPipelineTailTest.MyChannelFactory(myChannel)).group(DefaultChannelPipelineTailTest.GROUP).handler(new ChannelInboundHandlerAdapter()).remoteAddress(new InetSocketAddress(0));
        Channel channel = bootstrap.connect().sync().channel();
        try {
            Assert.assertTrue(latch.await(1L, TimeUnit.SECONDS));
        } finally {
            channel.close();
        }
    }

    @Test
    public void testOnUnhandledInboundChannelInactive() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        DefaultChannelPipelineTailTest.MyChannel myChannel = new DefaultChannelPipelineTailTest.MyChannel() {
            @Override
            protected void onUnhandledInboundChannelInactive() {
                latch.countDown();
            }
        };
        Bootstrap bootstrap = new Bootstrap().channelFactory(new DefaultChannelPipelineTailTest.MyChannelFactory(myChannel)).group(DefaultChannelPipelineTailTest.GROUP).handler(new ChannelInboundHandlerAdapter()).remoteAddress(new InetSocketAddress(0));
        Channel channel = bootstrap.connect().sync().channel();
        channel.close().syncUninterruptibly();
        Assert.assertTrue(latch.await(1L, TimeUnit.SECONDS));
    }

    @Test
    public void testOnUnhandledInboundException() throws Exception {
        final AtomicReference<Throwable> causeRef = new AtomicReference<Throwable>();
        final CountDownLatch latch = new CountDownLatch(1);
        DefaultChannelPipelineTailTest.MyChannel myChannel = new DefaultChannelPipelineTailTest.MyChannel() {
            @Override
            protected void onUnhandledInboundException(Throwable cause) {
                causeRef.set(cause);
                latch.countDown();
            }
        };
        Bootstrap bootstrap = new Bootstrap().channelFactory(new DefaultChannelPipelineTailTest.MyChannelFactory(myChannel)).group(DefaultChannelPipelineTailTest.GROUP).handler(new ChannelInboundHandlerAdapter()).remoteAddress(new InetSocketAddress(0));
        Channel channel = bootstrap.connect().sync().channel();
        try {
            IOException ex = new IOException("testOnUnhandledInboundException");
            channel.pipeline().fireExceptionCaught(ex);
            Assert.assertTrue(latch.await(1L, TimeUnit.SECONDS));
            Assert.assertSame(ex, causeRef.get());
        } finally {
            channel.close();
        }
    }

    @Test
    public void testOnUnhandledInboundMessage() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        DefaultChannelPipelineTailTest.MyChannel myChannel = new DefaultChannelPipelineTailTest.MyChannel() {
            @Override
            protected void onUnhandledInboundMessage(Object msg) {
                latch.countDown();
            }
        };
        Bootstrap bootstrap = new Bootstrap().channelFactory(new DefaultChannelPipelineTailTest.MyChannelFactory(myChannel)).group(DefaultChannelPipelineTailTest.GROUP).handler(new ChannelInboundHandlerAdapter()).remoteAddress(new InetSocketAddress(0));
        Channel channel = bootstrap.connect().sync().channel();
        try {
            channel.pipeline().fireChannelRead("testOnUnhandledInboundMessage");
            Assert.assertTrue(latch.await(1L, TimeUnit.SECONDS));
        } finally {
            channel.close();
        }
    }

    @Test
    public void testOnUnhandledInboundReadComplete() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        DefaultChannelPipelineTailTest.MyChannel myChannel = new DefaultChannelPipelineTailTest.MyChannel() {
            @Override
            protected void onUnhandledInboundReadComplete() {
                latch.countDown();
            }
        };
        Bootstrap bootstrap = new Bootstrap().channelFactory(new DefaultChannelPipelineTailTest.MyChannelFactory(myChannel)).group(DefaultChannelPipelineTailTest.GROUP).handler(new ChannelInboundHandlerAdapter()).remoteAddress(new InetSocketAddress(0));
        Channel channel = bootstrap.connect().sync().channel();
        try {
            channel.pipeline().fireChannelReadComplete();
            Assert.assertTrue(latch.await(1L, TimeUnit.SECONDS));
        } finally {
            channel.close();
        }
    }

    @Test
    public void testOnUnhandledInboundUserEventTriggered() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        DefaultChannelPipelineTailTest.MyChannel myChannel = new DefaultChannelPipelineTailTest.MyChannel() {
            @Override
            protected void onUnhandledInboundUserEventTriggered(Object evt) {
                latch.countDown();
            }
        };
        Bootstrap bootstrap = new Bootstrap().channelFactory(new DefaultChannelPipelineTailTest.MyChannelFactory(myChannel)).group(DefaultChannelPipelineTailTest.GROUP).handler(new ChannelInboundHandlerAdapter()).remoteAddress(new InetSocketAddress(0));
        Channel channel = bootstrap.connect().sync().channel();
        try {
            channel.pipeline().fireUserEventTriggered("testOnUnhandledInboundUserEventTriggered");
            Assert.assertTrue(latch.await(1L, TimeUnit.SECONDS));
        } finally {
            channel.close();
        }
    }

    @Test
    public void testOnUnhandledInboundWritabilityChanged() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        DefaultChannelPipelineTailTest.MyChannel myChannel = new DefaultChannelPipelineTailTest.MyChannel() {
            @Override
            protected void onUnhandledInboundWritabilityChanged() {
                latch.countDown();
            }
        };
        Bootstrap bootstrap = new Bootstrap().channelFactory(new DefaultChannelPipelineTailTest.MyChannelFactory(myChannel)).group(DefaultChannelPipelineTailTest.GROUP).handler(new ChannelInboundHandlerAdapter()).remoteAddress(new InetSocketAddress(0));
        Channel channel = bootstrap.connect().sync().channel();
        try {
            channel.pipeline().fireChannelWritabilityChanged();
            Assert.assertTrue(latch.await(1L, TimeUnit.SECONDS));
        } finally {
            channel.close();
        }
    }

    private static class MyChannelFactory implements ChannelFactory<DefaultChannelPipelineTailTest.MyChannel> {
        private final DefaultChannelPipelineTailTest.MyChannel channel;

        MyChannelFactory(DefaultChannelPipelineTailTest.MyChannel channel) {
            this.channel = channel;
        }

        @Override
        public DefaultChannelPipelineTailTest.MyChannel newChannel() {
            return channel;
        }
    }

    private abstract static class MyChannel extends AbstractChannel {
        private static final ChannelMetadata METADATA = new ChannelMetadata(false);

        private final ChannelConfig config = new DefaultChannelConfig(this);

        private boolean active;

        private boolean closed;

        protected MyChannel() {
            super(null);
        }

        @Override
        protected DefaultChannelPipeline newChannelPipeline() {
            return new DefaultChannelPipelineTailTest.MyChannel.MyChannelPipeline(this);
        }

        @Override
        public ChannelConfig config() {
            return config;
        }

        @Override
        public boolean isOpen() {
            return !(closed);
        }

        @Override
        public boolean isActive() {
            return (isOpen()) && (active);
        }

        @Override
        public ChannelMetadata metadata() {
            return DefaultChannelPipelineTailTest.MyChannel.METADATA;
        }

        @Override
        protected AbstractUnsafe newUnsafe() {
            return new DefaultChannelPipelineTailTest.MyChannel.MyUnsafe();
        }

        @Override
        protected boolean isCompatible(EventLoop loop) {
            return true;
        }

        @Override
        protected SocketAddress localAddress0() {
            return null;
        }

        @Override
        protected SocketAddress remoteAddress0() {
            return null;
        }

        @Override
        protected void doBind(SocketAddress localAddress) throws Exception {
        }

        @Override
        protected void doDisconnect() throws Exception {
        }

        @Override
        protected void doClose() throws Exception {
            closed = true;
        }

        @Override
        protected void doBeginRead() throws Exception {
        }

        @Override
        protected void doWrite(ChannelOutboundBuffer in) throws Exception {
            throw new IOException();
        }

        protected void onUnhandledInboundChannelActive() {
        }

        protected void onUnhandledInboundChannelInactive() {
        }

        protected void onUnhandledInboundException(Throwable cause) {
        }

        protected void onUnhandledInboundMessage(Object msg) {
        }

        protected void onUnhandledInboundReadComplete() {
        }

        protected void onUnhandledInboundUserEventTriggered(Object evt) {
        }

        protected void onUnhandledInboundWritabilityChanged() {
        }

        private class MyUnsafe extends AbstractUnsafe {
            @Override
            public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
                if (!(ensureOpen(promise))) {
                    return;
                }
                if (!(active)) {
                    active = true;
                    pipeline().fireChannelActive();
                }
                promise.setSuccess();
            }
        }

        private class MyChannelPipeline extends DefaultChannelPipeline {
            MyChannelPipeline(Channel channel) {
                super(channel);
            }

            @Override
            protected void onUnhandledInboundChannelActive() {
                DefaultChannelPipelineTailTest.MyChannel.this.onUnhandledInboundChannelActive();
            }

            @Override
            protected void onUnhandledInboundChannelInactive() {
                DefaultChannelPipelineTailTest.MyChannel.this.onUnhandledInboundChannelInactive();
            }

            @Override
            protected void onUnhandledInboundException(Throwable cause) {
                DefaultChannelPipelineTailTest.MyChannel.this.onUnhandledInboundException(cause);
            }

            @Override
            protected void onUnhandledInboundMessage(Object msg) {
                DefaultChannelPipelineTailTest.MyChannel.this.onUnhandledInboundMessage(msg);
            }

            @Override
            protected void onUnhandledInboundChannelReadComplete() {
                DefaultChannelPipelineTailTest.MyChannel.this.onUnhandledInboundReadComplete();
            }

            @Override
            protected void onUnhandledInboundUserEventTriggered(Object evt) {
                DefaultChannelPipelineTailTest.MyChannel.this.onUnhandledInboundUserEventTriggered(evt);
            }

            @Override
            protected void onUnhandledChannelWritabilityChanged() {
                DefaultChannelPipelineTailTest.MyChannel.this.onUnhandledInboundWritabilityChanged();
            }
        }
    }
}

