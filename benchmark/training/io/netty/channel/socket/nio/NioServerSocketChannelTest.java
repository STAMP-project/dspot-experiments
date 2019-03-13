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
package io.netty.channel.socket.nio;


import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import org.junit.Assert;
import org.junit.Test;


public class NioServerSocketChannelTest extends AbstractNioChannelTest<NioServerSocketChannel> {
    @Test
    public void testCloseOnError() throws Exception {
        ServerSocketChannel jdkChannel = ServerSocketChannel.open();
        NioServerSocketChannel serverSocketChannel = new NioServerSocketChannel(jdkChannel);
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            group.register(serverSocketChannel).syncUninterruptibly();
            serverSocketChannel.bind(new InetSocketAddress(0)).syncUninterruptibly();
            Assert.assertFalse(serverSocketChannel.closeOnReadError(new IOException()));
            Assert.assertTrue(serverSocketChannel.closeOnReadError(new IllegalArgumentException()));
            serverSocketChannel.close().syncUninterruptibly();
        } finally {
            group.shutdownGracefully();
        }
    }
}

