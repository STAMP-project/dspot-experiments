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
package org.jboss.netty.handler.ipfilter;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.junit.Assert;
import org.junit.Test;


public class IpFilterRuleTest {
    @Test
    public void testIpFilterRule() throws Exception {
        IpFilterRuleHandler h = new IpFilterRuleHandler();
        h.addAll(new IpFilterRuleList("+n:localhost, -n:*"));
        InetSocketAddress addr = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
        addr = new InetSocketAddress(InetAddress.getByName("127.0.0.2"), 8080);
        Assert.assertFalse(IpFilterRuleTest.accept(h, addr));
        addr = new InetSocketAddress(InetAddress.getByName(InetAddress.getLocalHost().getHostName()), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
        h.clear();
        h.addAll(new IpFilterRuleList((("+n:*" + (InetAddress.getLocalHost().getHostName().substring(1))) + ", -n:*")));
        addr = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
        addr = new InetSocketAddress(InetAddress.getByName("127.0.0.2"), 8080);
        Assert.assertFalse(IpFilterRuleTest.accept(h, addr));
        addr = new InetSocketAddress(InetAddress.getByName(InetAddress.getLocalHost().getHostName()), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
        h.clear();
        h.addAll(new IpFilterRuleList((("+c:" + (InetAddress.getLocalHost().getHostAddress())) + "/32, -n:*")));
        addr = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
        addr = new InetSocketAddress(InetAddress.getByName("127.0.0.2"), 8080);
        Assert.assertFalse(IpFilterRuleTest.accept(h, addr));
        addr = new InetSocketAddress(InetAddress.getByName(InetAddress.getLocalHost().getHostName()), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
        h.clear();
        h.addAll(new IpFilterRuleList(""));
        addr = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
        addr = new InetSocketAddress(InetAddress.getByName("127.0.0.2"), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
        addr = new InetSocketAddress(InetAddress.getByName(InetAddress.getLocalHost().getHostName()), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
        h.clear();
        addr = new InetSocketAddress(InetAddress.getLocalHost(), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
        addr = new InetSocketAddress(InetAddress.getByName("127.0.0.2"), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
        addr = new InetSocketAddress(InetAddress.getByName(InetAddress.getLocalHost().getHostName()), 8080);
        Assert.assertTrue(IpFilterRuleTest.accept(h, addr));
    }
}

