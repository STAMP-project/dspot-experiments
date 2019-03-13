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
package io.netty.channel.kqueue;


import ChannelOption.SO_LINGER;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import java.net.InetSocketAddress;
import org.junit.Assert;
import org.junit.Test;


public class KQueueChannelConfigTest {
    @Test
    public void testOptionGetThrowsChannelException() throws Exception {
        KQueueSocketChannel channel = new KQueueSocketChannel();
        channel.config().getSoLinger();
        channel.fd().close();
        try {
            channel.config().getSoLinger();
            Assert.fail();
        } catch (ChannelException e) {
            // expected
        }
    }

    @Test
    public void testOptionSetThrowsChannelException() throws Exception {
        KQueueSocketChannel channel = new KQueueSocketChannel();
        channel.config().setKeepAlive(true);
        channel.fd().close();
        try {
            channel.config().setKeepAlive(true);
            Assert.fail();
        } catch (ChannelException e) {
            // expected
        }
    }

    // See https://github.com/netty/netty/issues/7159
    @Test
    public void testSoLingerNoAssertError() throws Exception {
        EventLoopGroup group = new KQueueEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            KQueueSocketChannel ch = ((KQueueSocketChannel) (bootstrap.group(group).channel(KQueueSocketChannel.class).option(SO_LINGER, 10).handler(new ChannelInboundHandlerAdapter()).bind(new InetSocketAddress(0)).syncUninterruptibly().channel()));
            ch.close().syncUninterruptibly();
        } finally {
            group.shutdownGracefully();
        }
    }
}

