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
package io.netty.channel.epoll;


import ChannelOption.SO_LINGER;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import java.net.InetSocketAddress;
import org.junit.Test;


public class EpollSocketChannelTest {
    @Test
    public void testTcpInfo() throws Exception {
        EventLoopGroup group = new EpollEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            EpollSocketChannel ch = ((EpollSocketChannel) (bootstrap.group(group).channel(EpollSocketChannel.class).handler(new ChannelInboundHandlerAdapter()).bind(new InetSocketAddress(0)).syncUninterruptibly().channel()));
            EpollTcpInfo info = ch.tcpInfo();
            EpollSocketChannelTest.assertTcpInfo0(info);
            ch.close().syncUninterruptibly();
        } finally {
            group.shutdownGracefully();
        }
    }

    @Test
    public void testTcpInfoReuse() throws Exception {
        EventLoopGroup group = new EpollEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            EpollSocketChannel ch = ((EpollSocketChannel) (bootstrap.group(group).channel(EpollSocketChannel.class).handler(new ChannelInboundHandlerAdapter()).bind(new InetSocketAddress(0)).syncUninterruptibly().channel()));
            EpollTcpInfo info = new EpollTcpInfo();
            ch.tcpInfo(info);
            EpollSocketChannelTest.assertTcpInfo0(info);
            ch.close().syncUninterruptibly();
        } finally {
            group.shutdownGracefully();
        }
    }

    // See https://github.com/netty/netty/issues/7159
    @Test
    public void testSoLingerNoAssertError() throws Exception {
        EventLoopGroup group = new EpollEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            EpollSocketChannel ch = ((EpollSocketChannel) (bootstrap.group(group).channel(EpollSocketChannel.class).option(SO_LINGER, 10).handler(new ChannelInboundHandlerAdapter()).bind(new InetSocketAddress(0)).syncUninterruptibly().channel()));
            ch.close().syncUninterruptibly();
        } finally {
            group.shutdownGracefully();
        }
    }
}

