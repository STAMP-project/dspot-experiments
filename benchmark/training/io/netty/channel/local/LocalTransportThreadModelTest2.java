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
package io.netty.channel.local;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.util.ReferenceCountUtil;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class LocalTransportThreadModelTest2 {
    private static final String LOCAL_CHANNEL = LocalTransportThreadModelTest2.class.getName();

    static final int messageCountPerRun = 4;

    @Test(timeout = 15000)
    public void testSocketReuse() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        LocalTransportThreadModelTest2.LocalHandler serverHandler = new LocalTransportThreadModelTest2.LocalHandler("SERVER");
        serverBootstrap.group(new DefaultEventLoopGroup(), new DefaultEventLoopGroup()).channel(LocalServerChannel.class).childHandler(serverHandler);
        Bootstrap clientBootstrap = new Bootstrap();
        LocalTransportThreadModelTest2.LocalHandler clientHandler = new LocalTransportThreadModelTest2.LocalHandler("CLIENT");
        clientBootstrap.group(new DefaultEventLoopGroup()).channel(LocalChannel.class).remoteAddress(new LocalAddress(LocalTransportThreadModelTest2.LOCAL_CHANNEL)).handler(clientHandler);
        serverBootstrap.bind(new LocalAddress(LocalTransportThreadModelTest2.LOCAL_CHANNEL)).sync();
        int count = 100;
        for (int i = 1; i < (count + 1); i++) {
            Channel ch = clientBootstrap.connect().sync().channel();
            // SPIN until we get what we are looking for.
            int target = i * (LocalTransportThreadModelTest2.messageCountPerRun);
            while (((serverHandler.count.get()) != target) || ((clientHandler.count.get()) != target)) {
                Thread.sleep(50);
            } 
            close(ch, clientHandler);
        }
        Assert.assertEquals(((count * 2) * (LocalTransportThreadModelTest2.messageCountPerRun)), ((serverHandler.count.get()) + (clientHandler.count.get())));
    }

    @Sharable
    static class LocalHandler extends ChannelInboundHandlerAdapter {
        private final String name;

        public volatile ChannelFuture lastWriteFuture;

        public final AtomicInteger count = new AtomicInteger(0);

        LocalHandler(String name) {
            this.name = name;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            for (int i = 0; i < (LocalTransportThreadModelTest2.messageCountPerRun); i++) {
                lastWriteFuture = ctx.channel().write((((name) + ' ') + i));
            }
            ctx.channel().flush();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            count.incrementAndGet();
            ReferenceCountUtil.release(msg);
        }
    }
}

