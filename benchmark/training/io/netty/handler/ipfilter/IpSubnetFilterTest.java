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
package io.netty.handler.ipfilter;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import java.net.InetSocketAddress;
import org.junit.Assert;
import org.junit.Test;

import static IpFilterRuleType.ACCEPT;
import static IpFilterRuleType.REJECT;


public class IpSubnetFilterTest {
    @Test
    public void testIpv4DefaultRoute() {
        IpSubnetFilterRule rule = new IpSubnetFilterRule("0.0.0.0", 0, ACCEPT);
        Assert.assertTrue(rule.matches(IpSubnetFilterTest.newSockAddress("91.114.240.43")));
        Assert.assertTrue(rule.matches(IpSubnetFilterTest.newSockAddress("10.0.0.3")));
        Assert.assertTrue(rule.matches(IpSubnetFilterTest.newSockAddress("192.168.93.2")));
    }

    @Test
    public void testIpv4SubnetMaskCorrectlyHandlesIpv6() {
        IpSubnetFilterRule rule = new IpSubnetFilterRule("0.0.0.0", 0, ACCEPT);
        Assert.assertFalse(rule.matches(IpSubnetFilterTest.newSockAddress("2001:db8:abcd:0000::1")));
    }

    @Test
    public void testIpv6SubnetMaskCorrectlyHandlesIpv4() {
        IpSubnetFilterRule rule = new IpSubnetFilterRule("::", 0, ACCEPT);
        Assert.assertFalse(rule.matches(IpSubnetFilterTest.newSockAddress("91.114.240.43")));
    }

    @Test
    public void testIp4SubnetFilterRule() throws Exception {
        IpSubnetFilterRule rule = new IpSubnetFilterRule("192.168.56.1", 24, ACCEPT);
        for (int i = 0; i <= 255; i++) {
            Assert.assertTrue(rule.matches(IpSubnetFilterTest.newSockAddress(String.format("192.168.56.%d", i))));
        }
        Assert.assertFalse(rule.matches(IpSubnetFilterTest.newSockAddress("192.168.57.1")));
        rule = new IpSubnetFilterRule("91.114.240.1", 23, ACCEPT);
        Assert.assertTrue(rule.matches(IpSubnetFilterTest.newSockAddress("91.114.240.43")));
        Assert.assertTrue(rule.matches(IpSubnetFilterTest.newSockAddress("91.114.240.255")));
        Assert.assertTrue(rule.matches(IpSubnetFilterTest.newSockAddress("91.114.241.193")));
        Assert.assertTrue(rule.matches(IpSubnetFilterTest.newSockAddress("91.114.241.254")));
        Assert.assertFalse(rule.matches(IpSubnetFilterTest.newSockAddress("91.115.241.2")));
    }

    @Test
    public void testIp6SubnetFilterRule() {
        IpSubnetFilterRule rule;
        rule = new IpSubnetFilterRule("2001:db8:abcd:0000::", 52, ACCEPT);
        Assert.assertTrue(rule.matches(IpSubnetFilterTest.newSockAddress("2001:db8:abcd:0000::1")));
        Assert.assertTrue(rule.matches(IpSubnetFilterTest.newSockAddress("2001:db8:abcd:0fff:ffff:ffff:ffff:ffff")));
        Assert.assertFalse(rule.matches(IpSubnetFilterTest.newSockAddress("2001:db8:abcd:1000::")));
    }

    @Test
    public void testIpFilterRuleHandler() throws Exception {
        IpFilterRule filter0 = new IpFilterRule() {
            @Override
            public boolean matches(InetSocketAddress remoteAddress) {
                return "192.168.57.1".equals(remoteAddress.getHostName());
            }

            @Override
            public IpFilterRuleType ruleType() {
                return REJECT;
            }
        };
        RuleBasedIpFilter denyHandler = new RuleBasedIpFilter(filter0) {
            private final byte[] message = new byte[]{ 1, 2, 3, 4, 5, 6, 7 };

            @Override
            protected ChannelFuture channelRejected(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) {
                Assert.assertTrue(ctx.channel().isActive());
                Assert.assertTrue(ctx.channel().isWritable());
                Assert.assertEquals("192.168.57.1", remoteAddress.getHostName());
                return ctx.writeAndFlush(Unpooled.wrappedBuffer(message));
            }
        };
        EmbeddedChannel chDeny = IpSubnetFilterTest.newEmbeddedInetChannel("192.168.57.1", denyHandler);
        ByteBuf out = chDeny.readOutbound();
        Assert.assertEquals(7, out.readableBytes());
        for (byte i = 1; i <= 7; i++) {
            Assert.assertEquals(i, out.readByte());
        }
        Assert.assertFalse(chDeny.isActive());
        Assert.assertFalse(chDeny.isOpen());
        RuleBasedIpFilter allowHandler = new RuleBasedIpFilter(filter0) {
            @Override
            protected ChannelFuture channelRejected(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) {
                Assert.fail();
                return null;
            }
        };
        EmbeddedChannel chAllow = IpSubnetFilterTest.newEmbeddedInetChannel("192.168.57.2", allowHandler);
        Assert.assertTrue(chAllow.isActive());
        Assert.assertTrue(chAllow.isOpen());
    }

    @Test
    public void testUniqueIpFilterHandler() {
        UniqueIpFilter handler = new UniqueIpFilter();
        EmbeddedChannel ch1 = IpSubnetFilterTest.newEmbeddedInetChannel("91.92.93.1", handler);
        Assert.assertTrue(ch1.isActive());
        EmbeddedChannel ch2 = IpSubnetFilterTest.newEmbeddedInetChannel("91.92.93.2", handler);
        Assert.assertTrue(ch2.isActive());
        EmbeddedChannel ch3 = IpSubnetFilterTest.newEmbeddedInetChannel("91.92.93.1", handler);
        Assert.assertFalse(ch3.isActive());
        // false means that no data is left to read/write
        Assert.assertFalse(ch1.finish());
        EmbeddedChannel ch4 = IpSubnetFilterTest.newEmbeddedInetChannel("91.92.93.1", handler);
        Assert.assertTrue(ch4.isActive());
    }
}

