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


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import java.net.ConnectException;
import org.junit.Assert;
import org.junit.Test;


public class AbstractChannelPoolMapTest {
    private static final String LOCAL_ADDR_ID = "test.id";

    @Test(expected = ConnectException.class)
    public void testMap() throws Exception {
        EventLoopGroup group = new LocalEventLoopGroup();
        LocalAddress addr = new LocalAddress(AbstractChannelPoolMapTest.LOCAL_ADDR_ID);
        final Bootstrap cb = new Bootstrap();
        cb.remoteAddress(addr);
        cb.group(group).channel(LocalChannel.class);
        AbstractChannelPoolMap<EventLoop, SimpleChannelPool> poolMap = new AbstractChannelPoolMap<EventLoop, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(EventLoop key) {
                return new SimpleChannelPool(cb.clone(key), new AbstractChannelPoolMapTest.TestChannelPoolHandler());
            }
        };
        EventLoop loop = group.next();
        Assert.assertFalse(poolMap.iterator().hasNext());
        Assert.assertEquals(0, poolMap.size());
        SimpleChannelPool pool = poolMap.get(loop);
        Assert.assertEquals(1, poolMap.size());
        Assert.assertTrue(poolMap.iterator().hasNext());
        Assert.assertSame(pool, poolMap.get(loop));
        Assert.assertTrue(poolMap.remove(loop));
        Assert.assertFalse(poolMap.remove(loop));
        Assert.assertFalse(poolMap.iterator().hasNext());
        Assert.assertEquals(0, poolMap.size());
        pool.acquire().syncUninterruptibly();
    }

    private static final class TestChannelPoolHandler extends AbstractChannelPoolHandler {
        @Override
        public void channelCreated(Channel ch) throws Exception {
            // NOOP
        }
    }
}

