/**
 * Copyright 2015 The Netty Project
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
package io.netty.channel.pool;


import FixedChannelPool.POOL_CLOSED_ON_RELEASE_EXCEPTION;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.pool.FixedChannelPool.AcquireTimeoutAction;
import io.netty.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;

import static ChannelHealthChecker.ACTIVE;


public class FixedChannelPoolTest {
    private static final String LOCAL_ADDR_ID = "test.id";

    private static EventLoopGroup group;

    @Test
    public void testAcquire() throws Exception {
        LocalAddress addr = new LocalAddress(FixedChannelPoolTest.LOCAL_ADDR_ID);
        Bootstrap cb = new Bootstrap();
        cb.remoteAddress(addr);
        cb.group(FixedChannelPoolTest.group).channel(LocalChannel.class);
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(FixedChannelPoolTest.group).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter());
            }
        });
        // Start server
        Channel sc = sb.bind(addr).syncUninterruptibly().channel();
        CountingChannelPoolHandler handler = new CountingChannelPoolHandler();
        ChannelPool pool = new FixedChannelPool(cb, handler, 1, Integer.MAX_VALUE);
        Channel channel = pool.acquire().syncUninterruptibly().getNow();
        Future<Channel> future = pool.acquire();
        Assert.assertFalse(future.isDone());
        pool.release(channel).syncUninterruptibly();
        Assert.assertTrue(future.await(1, TimeUnit.SECONDS));
        Channel channel2 = future.getNow();
        Assert.assertSame(channel, channel2);
        Assert.assertEquals(1, handler.channelCount());
        Assert.assertEquals(1, handler.acquiredCount());
        Assert.assertEquals(1, handler.releasedCount());
        sc.close().syncUninterruptibly();
        channel2.close().syncUninterruptibly();
    }

    @Test(expected = TimeoutException.class)
    public void testAcquireTimeout() throws Exception {
        FixedChannelPoolTest.testAcquireTimeout(500);
    }

    @Test(expected = TimeoutException.class)
    public void testAcquireWithZeroTimeout() throws Exception {
        FixedChannelPoolTest.testAcquireTimeout(0);
    }

    @Test
    public void testAcquireNewConnection() throws Exception {
        LocalAddress addr = new LocalAddress(FixedChannelPoolTest.LOCAL_ADDR_ID);
        Bootstrap cb = new Bootstrap();
        cb.remoteAddress(addr);
        cb.group(FixedChannelPoolTest.group).channel(LocalChannel.class);
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(FixedChannelPoolTest.group).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter());
            }
        });
        // Start server
        Channel sc = sb.bind(addr).syncUninterruptibly().channel();
        ChannelPoolHandler handler = new FixedChannelPoolTest.TestChannelPoolHandler();
        ChannelPool pool = new FixedChannelPool(cb, handler, ACTIVE, AcquireTimeoutAction.NEW, 500, 1, Integer.MAX_VALUE);
        Channel channel = pool.acquire().syncUninterruptibly().getNow();
        Channel channel2 = pool.acquire().syncUninterruptibly().getNow();
        Assert.assertNotSame(channel, channel2);
        sc.close().syncUninterruptibly();
        channel.close().syncUninterruptibly();
        channel2.close().syncUninterruptibly();
    }

    /**
     * Tests that the acquiredChannelCount is not added up several times for the same channel acquire request.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAcquireNewConnectionWhen() throws Exception {
        LocalAddress addr = new LocalAddress(FixedChannelPoolTest.LOCAL_ADDR_ID);
        Bootstrap cb = new Bootstrap();
        cb.remoteAddress(addr);
        cb.group(FixedChannelPoolTest.group).channel(LocalChannel.class);
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(FixedChannelPoolTest.group).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter());
            }
        });
        // Start server
        Channel sc = sb.bind(addr).syncUninterruptibly().channel();
        ChannelPoolHandler handler = new FixedChannelPoolTest.TestChannelPoolHandler();
        ChannelPool pool = new FixedChannelPool(cb, handler, 1);
        Channel channel1 = pool.acquire().syncUninterruptibly().getNow();
        channel1.close().syncUninterruptibly();
        pool.release(channel1);
        Channel channel2 = pool.acquire().syncUninterruptibly().getNow();
        Assert.assertNotSame(channel1, channel2);
        sc.close().syncUninterruptibly();
        channel2.close().syncUninterruptibly();
    }

    @Test(expected = IllegalStateException.class)
    public void testAcquireBoundQueue() throws Exception {
        LocalAddress addr = new LocalAddress(FixedChannelPoolTest.LOCAL_ADDR_ID);
        Bootstrap cb = new Bootstrap();
        cb.remoteAddress(addr);
        cb.group(FixedChannelPoolTest.group).channel(LocalChannel.class);
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(FixedChannelPoolTest.group).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter());
            }
        });
        // Start server
        Channel sc = sb.bind(addr).syncUninterruptibly().channel();
        ChannelPoolHandler handler = new FixedChannelPoolTest.TestChannelPoolHandler();
        ChannelPool pool = new FixedChannelPool(cb, handler, 1, 1);
        Channel channel = pool.acquire().syncUninterruptibly().getNow();
        Future<Channel> future = pool.acquire();
        Assert.assertFalse(future.isDone());
        try {
            pool.acquire().syncUninterruptibly();
        } finally {
            sc.close().syncUninterruptibly();
            channel.close().syncUninterruptibly();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseDifferentPool() throws Exception {
        LocalAddress addr = new LocalAddress(FixedChannelPoolTest.LOCAL_ADDR_ID);
        Bootstrap cb = new Bootstrap();
        cb.remoteAddress(addr);
        cb.group(FixedChannelPoolTest.group).channel(LocalChannel.class);
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(FixedChannelPoolTest.group).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter());
            }
        });
        // Start server
        Channel sc = sb.bind(addr).syncUninterruptibly().channel();
        ChannelPoolHandler handler = new FixedChannelPoolTest.TestChannelPoolHandler();
        ChannelPool pool = new FixedChannelPool(cb, handler, 1, 1);
        ChannelPool pool2 = new FixedChannelPool(cb, handler, 1, 1);
        Channel channel = pool.acquire().syncUninterruptibly().getNow();
        try {
            pool2.release(channel).syncUninterruptibly();
        } finally {
            sc.close().syncUninterruptibly();
            channel.close().syncUninterruptibly();
        }
    }

    @Test
    public void testReleaseAfterClosePool() throws Exception {
        LocalAddress addr = new LocalAddress(FixedChannelPoolTest.LOCAL_ADDR_ID);
        Bootstrap cb = new Bootstrap();
        cb.remoteAddress(addr);
        cb.group(FixedChannelPoolTest.group).channel(LocalChannel.class);
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(FixedChannelPoolTest.group).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter());
            }
        });
        // Start server
        Channel sc = sb.bind(addr).syncUninterruptibly().channel();
        FixedChannelPool pool = new FixedChannelPool(cb, new FixedChannelPoolTest.TestChannelPoolHandler(), 2);
        final Future<Channel> acquire = pool.acquire();
        final Channel channel = acquire.get();
        pool.close();
        FixedChannelPoolTest.group.submit(new Runnable() {
            @Override
            public void run() {
                // NOOP
            }
        }).syncUninterruptibly();
        try {
            pool.release(channel).syncUninterruptibly();
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertSame(POOL_CLOSED_ON_RELEASE_EXCEPTION, e);
        }
        // Since the pool is closed, the Channel should have been closed as well.
        channel.closeFuture().syncUninterruptibly();
        Assert.assertFalse("Unexpected open channel", channel.isOpen());
        sc.close().syncUninterruptibly();
    }

    @Test
    public void testReleaseClosed() {
        LocalAddress addr = new LocalAddress(FixedChannelPoolTest.LOCAL_ADDR_ID);
        Bootstrap cb = new Bootstrap();
        cb.remoteAddress(addr);
        cb.group(FixedChannelPoolTest.group).channel(LocalChannel.class);
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(FixedChannelPoolTest.group).channel(LocalServerChannel.class).childHandler(new io.netty.channel.ChannelInitializer<LocalChannel>() {
            @Override
            public void initChannel(LocalChannel ch) throws Exception {
                ch.pipeline().addLast(new ChannelInboundHandlerAdapter());
            }
        });
        // Start server
        Channel sc = sb.bind(addr).syncUninterruptibly().channel();
        FixedChannelPool pool = new FixedChannelPool(cb, new FixedChannelPoolTest.TestChannelPoolHandler(), 2);
        Channel channel = pool.acquire().syncUninterruptibly().getNow();
        channel.close().syncUninterruptibly();
        pool.release(channel).syncUninterruptibly();
        sc.close().syncUninterruptibly();
    }

    private static final class TestChannelPoolHandler extends AbstractChannelPoolHandler {
        @Override
        public void channelCreated(Channel ch) throws Exception {
            // NOOP
        }
    }
}

