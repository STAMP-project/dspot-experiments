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
package io.netty.channel.epoll;


import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class EpollServerSocketChannelConfigTest {
    private static EventLoopGroup group;

    private static EpollServerSocketChannel ch;

    @Test
    public void testTcpDeferAccept() {
        EpollServerSocketChannelConfigTest.ch.config().setTcpDeferAccept(0);
        Assert.assertEquals(0, EpollServerSocketChannelConfigTest.ch.config().getTcpDeferAccept());
        EpollServerSocketChannelConfigTest.ch.config().setTcpDeferAccept(10);
        // The returned value may be bigger then what we set.
        // See http://www.spinics.net/lists/netdev/msg117330.html
        Assert.assertTrue((10 <= (EpollServerSocketChannelConfigTest.ch.config().getTcpDeferAccept())));
    }

    @Test
    public void testReusePort() {
        EpollServerSocketChannelConfigTest.ch.config().setReusePort(false);
        Assert.assertFalse(EpollServerSocketChannelConfigTest.ch.config().isReusePort());
        EpollServerSocketChannelConfigTest.ch.config().setReusePort(true);
        Assert.assertTrue(EpollServerSocketChannelConfigTest.ch.config().isReusePort());
    }

    @Test
    public void testFreeBind() {
        EpollServerSocketChannelConfigTest.ch.config().setFreeBind(false);
        Assert.assertFalse(EpollServerSocketChannelConfigTest.ch.config().isFreeBind());
        EpollServerSocketChannelConfigTest.ch.config().setFreeBind(true);
        Assert.assertTrue(EpollServerSocketChannelConfigTest.ch.config().isFreeBind());
    }

    @Test
    public void getGetOptions() {
        Map<ChannelOption<?>, Object> map = EpollServerSocketChannelConfigTest.ch.config().getOptions();
        Assert.assertFalse(map.isEmpty());
    }
}

