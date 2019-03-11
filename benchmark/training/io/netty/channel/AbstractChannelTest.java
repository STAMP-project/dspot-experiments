/**
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;


import io.netty.util.NetUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class AbstractChannelTest {
    @Test
    public void ensureInitialRegistrationFiresActive() throws Throwable {
        EventLoop eventLoop = Mockito.mock(EventLoop.class);
        // This allows us to have a single-threaded test
        Mockito.when(eventLoop.inEventLoop()).thenReturn(true);
        AbstractChannelTest.TestChannel channel = new AbstractChannelTest.TestChannel();
        ChannelInboundHandler handler = Mockito.mock(ChannelInboundHandler.class);
        pipeline().addLast(handler);
        AbstractChannelTest.registerChannel(eventLoop, channel);
        Mockito.verify(handler).handlerAdded(ArgumentMatchers.any(ChannelHandlerContext.class));
        Mockito.verify(handler).channelRegistered(ArgumentMatchers.any(ChannelHandlerContext.class));
        Mockito.verify(handler).channelActive(ArgumentMatchers.any(ChannelHandlerContext.class));
    }

    @Test
    public void ensureSubsequentRegistrationDoesNotFireActive() throws Throwable {
        final EventLoop eventLoop = Mockito.mock(EventLoop.class);
        // This allows us to have a single-threaded test
        Mockito.when(eventLoop.inEventLoop()).thenReturn(true);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ((Runnable) (invocationOnMock.getArgument(0))).run();
                return null;
            }
        }).when(eventLoop).execute(ArgumentMatchers.any(Runnable.class));
        final AbstractChannelTest.TestChannel channel = new AbstractChannelTest.TestChannel();
        ChannelInboundHandler handler = Mockito.mock(ChannelInboundHandler.class);
        pipeline().addLast(handler);
        AbstractChannelTest.registerChannel(eventLoop, channel);
        unsafe().deregister(new DefaultChannelPromise(channel));
        AbstractChannelTest.registerChannel(eventLoop, channel);
        Mockito.verify(handler).handlerAdded(ArgumentMatchers.any(ChannelHandlerContext.class));
        // Should register twice
        Mockito.verify(handler, Mockito.times(2)).channelRegistered(ArgumentMatchers.any(ChannelHandlerContext.class));
        Mockito.verify(handler).channelActive(ArgumentMatchers.any(ChannelHandlerContext.class));
        Mockito.verify(handler).channelUnregistered(ArgumentMatchers.any(ChannelHandlerContext.class));
    }

    @Test
    public void ensureDefaultChannelId() {
        AbstractChannelTest.TestChannel channel = new AbstractChannelTest.TestChannel();
        final ChannelId channelId = id();
        Assert.assertTrue((channelId instanceof DefaultChannelId));
    }

    @Test
    public void testClosedChannelExceptionCarryIOException() throws Exception {
        final IOException ioException = new IOException();
        final Channel channel = new AbstractChannelTest.TestChannel() {
            private boolean open = true;

            private boolean active;

            @Override
            protected AbstractUnsafe newUnsafe() {
                return new AbstractUnsafe() {
                    @Override
                    public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
                        active = true;
                        promise.setSuccess();
                    }
                };
            }

            @Override
            protected void doClose() {
                active = false;
                open = false;
            }

            @Override
            protected void doWrite(ChannelOutboundBuffer in) throws Exception {
                throw ioException;
            }

            @Override
            public boolean isOpen() {
                return open;
            }

            @Override
            public boolean isActive() {
                return active;
            }
        };
        EventLoop loop = new DefaultEventLoop();
        try {
            AbstractChannelTest.registerChannel(loop, channel);
            channel.connect(new InetSocketAddress(NetUtil.LOCALHOST, 8888)).sync();
            Assert.assertSame(ioException, channel.writeAndFlush("").await().cause());
            AbstractChannelTest.assertClosedChannelException(channel.writeAndFlush(""), ioException);
            AbstractChannelTest.assertClosedChannelException(channel.write(""), ioException);
            AbstractChannelTest.assertClosedChannelException(channel.bind(new InetSocketAddress(NetUtil.LOCALHOST, 8888)), ioException);
        } finally {
            channel.close();
            loop.shutdownGracefully();
        }
    }

    private static class TestChannel extends AbstractChannel {
        private static final ChannelMetadata TEST_METADATA = new ChannelMetadata(false);

        private final ChannelConfig config = new DefaultChannelConfig(this);

        TestChannel() {
            super(null);
        }

        @Override
        public ChannelConfig config() {
            return config;
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public ChannelMetadata metadata() {
            return AbstractChannelTest.TestChannel.TEST_METADATA;
        }

        @Override
        protected AbstractUnsafe newUnsafe() {
            return new AbstractUnsafe() {
                @Override
                public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
                    promise.setFailure(new UnsupportedOperationException());
                }
            };
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
        protected void doBind(SocketAddress localAddress) {
        }

        @Override
        protected void doDisconnect() {
        }

        @Override
        protected void doClose() {
        }

        @Override
        protected void doBeginRead() {
        }

        @Override
        protected void doWrite(ChannelOutboundBuffer in) throws Exception {
        }
    }
}

