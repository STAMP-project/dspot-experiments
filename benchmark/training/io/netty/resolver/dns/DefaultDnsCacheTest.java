/**
 * Copyright 2018 The Netty Project
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
package io.netty.resolver.dns;


import NetUtil.LOCALHOST;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class DefaultDnsCacheTest {
    @Test
    public void testExpire() throws Throwable {
        InetAddress addr1 = InetAddress.getByAddress(new byte[]{ 10, 0, 0, 1 });
        InetAddress addr2 = InetAddress.getByAddress(new byte[]{ 10, 0, 0, 2 });
        EventLoopGroup group = new DefaultEventLoopGroup(1);
        try {
            EventLoop loop = group.next();
            final DefaultDnsCache cache = new DefaultDnsCache();
            cache.cache("netty.io", null, addr1, 1, loop);
            cache.cache("netty.io", null, addr2, 10000, loop);
            Throwable error = loop.schedule(new Callable<Throwable>() {
                @Override
                public Throwable call() {
                    try {
                        Assert.assertNull(cache.get("netty.io", null));
                        return null;
                    } catch (Throwable cause) {
                        return cause;
                    }
                }
            }, 1, TimeUnit.SECONDS).get();
            if (error != null) {
                throw error;
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    @Test
    public void testExpireWithDifferentTTLs() {
        DefaultDnsCacheTest.testExpireWithTTL0(1);
        DefaultDnsCacheTest.testExpireWithTTL0(1000);
        DefaultDnsCacheTest.testExpireWithTTL0(1000000);
    }

    @Test
    public void testExpireWithToBigMinTTL() {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            EventLoop loop = group.next();
            final DefaultDnsCache cache = new DefaultDnsCache(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
            Assert.assertNotNull(cache.cache("netty.io", null, LOCALHOST, 100, loop));
        } finally {
            group.shutdownGracefully();
        }
    }

    @Test
    public void testAddMultipleAddressesForSameHostname() throws Exception {
        InetAddress addr1 = InetAddress.getByAddress(new byte[]{ 10, 0, 0, 1 });
        InetAddress addr2 = InetAddress.getByAddress(new byte[]{ 10, 0, 0, 2 });
        EventLoopGroup group = new DefaultEventLoopGroup(1);
        try {
            EventLoop loop = group.next();
            final DefaultDnsCache cache = new DefaultDnsCache();
            cache.cache("netty.io", null, addr1, 1, loop);
            cache.cache("netty.io", null, addr2, 10000, loop);
            List<? extends DnsCacheEntry> entries = cache.get("netty.io", null);
            Assert.assertEquals(2, entries.size());
            DefaultDnsCacheTest.assertEntry(entries.get(0), addr1);
            DefaultDnsCacheTest.assertEntry(entries.get(1), addr2);
        } finally {
            group.shutdownGracefully();
        }
    }

    @Test
    public void testAddSameAddressForSameHostname() throws Exception {
        InetAddress addr1 = InetAddress.getByAddress(new byte[]{ 10, 0, 0, 1 });
        EventLoopGroup group = new DefaultEventLoopGroup(1);
        try {
            EventLoop loop = group.next();
            final DefaultDnsCache cache = new DefaultDnsCache();
            cache.cache("netty.io", null, addr1, 1, loop);
            cache.cache("netty.io", null, addr1, 10000, loop);
            List<? extends DnsCacheEntry> entries = cache.get("netty.io", null);
            Assert.assertEquals(1, entries.size());
            DefaultDnsCacheTest.assertEntry(entries.get(0), addr1);
        } finally {
            group.shutdownGracefully();
        }
    }

    @Test
    public void testCacheFailed() throws Exception {
        InetAddress addr1 = InetAddress.getByAddress(new byte[]{ 10, 0, 0, 1 });
        InetAddress addr2 = InetAddress.getByAddress(new byte[]{ 10, 0, 0, 2 });
        EventLoopGroup group = new DefaultEventLoopGroup(1);
        try {
            EventLoop loop = group.next();
            final DefaultDnsCache cache = new DefaultDnsCache(1, 100, 100);
            cache.cache("netty.io", null, addr1, 10000, loop);
            cache.cache("netty.io", null, addr2, 10000, loop);
            List<? extends DnsCacheEntry> entries = cache.get("netty.io", null);
            Assert.assertEquals(2, entries.size());
            DefaultDnsCacheTest.assertEntry(entries.get(0), addr1);
            DefaultDnsCacheTest.assertEntry(entries.get(1), addr2);
            Exception exception = new Exception();
            cache.cache("netty.io", null, exception, loop);
            entries = cache.get("netty.io", null);
            DnsCacheEntry entry = entries.get(0);
            Assert.assertEquals(1, entries.size());
            Assert.assertSame(exception, entry.cause());
            Assert.assertNull(entry.address());
        } finally {
            group.shutdownGracefully();
        }
    }

    @Test
    public void testDotHandling() throws Exception {
        InetAddress addr1 = InetAddress.getByAddress(new byte[]{ 10, 0, 0, 1 });
        InetAddress addr2 = InetAddress.getByAddress(new byte[]{ 10, 0, 0, 2 });
        EventLoopGroup group = new DefaultEventLoopGroup(1);
        try {
            EventLoop loop = group.next();
            final DefaultDnsCache cache = new DefaultDnsCache(1, 100, 100);
            cache.cache("netty.io", null, addr1, 10000, loop);
            cache.cache("netty.io.", null, addr2, 10000, loop);
            List<? extends DnsCacheEntry> entries = cache.get("netty.io", null);
            Assert.assertEquals(2, entries.size());
            DefaultDnsCacheTest.assertEntry(entries.get(0), addr1);
            DefaultDnsCacheTest.assertEntry(entries.get(1), addr2);
            List<? extends DnsCacheEntry> entries2 = cache.get("netty.io.", null);
            Assert.assertEquals(2, entries2.size());
            DefaultDnsCacheTest.assertEntry(entries2.get(0), addr1);
            DefaultDnsCacheTest.assertEntry(entries2.get(1), addr2);
        } finally {
            group.shutdownGracefully();
        }
    }
}

