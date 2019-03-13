/**
 * Copyright 2016 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.flow;


import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class FlowControlHandlerTest {
    private static EventLoopGroup GROUP;

    /**
     * This test demonstrates the default behavior if auto reading
     * is turned on from the get-go and you're trying to turn it off
     * once you've received your first message.
     *
     * NOTE: This test waits for the client to disconnect which is
     * interpreted as the signal that all {@code byte}s have been
     * transferred to the server.
     */
    @Test
    public void testAutoReadingOn() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        ChannelInboundHandlerAdapter handler = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                ReferenceCountUtil.release(msg);
                // We're turning off auto reading in the hope that no
                // new messages are being sent but that is not true.
                ctx.channel().config().setAutoRead(false);
                latch.countDown();
            }
        };
        Channel server = FlowControlHandlerTest.newServer(true, handler);
        Channel client = FlowControlHandlerTest.newClient(server.localAddress());
        try {
            client.writeAndFlush(FlowControlHandlerTest.newOneMessage()).syncUninterruptibly();
            // We received three messages even through auto reading
            // was turned off after we received the first message.
            Assert.assertTrue(latch.await(1L, TimeUnit.SECONDS));
        } finally {
            client.close();
            server.close();
        }
    }

    /**
     * This test demonstrates the default behavior if auto reading
     * is turned off from the get-go and you're calling read() in
     * the hope that only one message will be returned.
     *
     * NOTE: This test waits for the client to disconnect which is
     * interpreted as the signal that all {@code byte}s have been
     * transferred to the server.
     */
    @Test
    public void testAutoReadingOff() throws Exception {
        final Exchanger<Channel> peerRef = new Exchanger<Channel>();
        final CountDownLatch latch = new CountDownLatch(3);
        ChannelInboundHandlerAdapter handler = new ChannelInboundHandlerAdapter() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                peerRef.exchange(ctx.channel(), 1L, TimeUnit.SECONDS);
                ctx.fireChannelActive();
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                ReferenceCountUtil.release(msg);
                latch.countDown();
            }
        };
        Channel server = FlowControlHandlerTest.newServer(false, handler);
        Channel client = FlowControlHandlerTest.newClient(server.localAddress());
        try {
            // The client connection on the server side
            Channel peer = peerRef.exchange(null, 1L, TimeUnit.SECONDS);
            // Write the message
            client.writeAndFlush(FlowControlHandlerTest.newOneMessage()).syncUninterruptibly();
            // Read the message
            peer.read();
            // We received all three messages but hoped that only one
            // message was read because auto reading was off and we
            // invoked the read() method only once.
            Assert.assertTrue(latch.await(1L, TimeUnit.SECONDS));
        } finally {
            client.close();
            server.close();
        }
    }

    /**
     * The {@link FlowControlHandler} will simply pass-through all messages
     * if auto reading is on and remains on.
     */
    @Test
    public void testFlowAutoReadOn() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        ChannelInboundHandlerAdapter handler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                latch.countDown();
            }
        };
        FlowControlHandler flow = new FlowControlHandler();
        Channel server = FlowControlHandlerTest.newServer(true, flow, handler);
        Channel client = FlowControlHandlerTest.newClient(server.localAddress());
        try {
            // Write the message
            client.writeAndFlush(FlowControlHandlerTest.newOneMessage()).syncUninterruptibly();
            // We should receive 3 messages
            Assert.assertTrue(latch.await(1L, TimeUnit.SECONDS));
            Assert.assertTrue(flow.isQueueEmpty());
        } finally {
            client.close();
            server.close();
        }
    }

    /**
     * The {@link FlowControlHandler} will pass down messages one by one
     * if {@link ChannelConfig#setAutoRead(boolean)} is being toggled.
     */
    @Test
    public void testFlowToggleAutoRead() throws Exception {
        final Exchanger<Channel> peerRef = new Exchanger<Channel>();
        final CountDownLatch msgRcvLatch1 = new CountDownLatch(1);
        final CountDownLatch msgRcvLatch2 = new CountDownLatch(1);
        final CountDownLatch msgRcvLatch3 = new CountDownLatch(1);
        final CountDownLatch setAutoReadLatch1 = new CountDownLatch(1);
        final CountDownLatch setAutoReadLatch2 = new CountDownLatch(1);
        ChannelInboundHandlerAdapter handler = new ChannelInboundHandlerAdapter() {
            private int msgRcvCount;

            private int expectedMsgCount;

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                peerRef.exchange(ctx.channel(), 1L, TimeUnit.SECONDS);
                ctx.fireChannelActive();
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
                ReferenceCountUtil.release(msg);
                // Disable auto reading after each message
                ctx.channel().config().setAutoRead(false);
                if (((msgRcvCount)++) != (expectedMsgCount)) {
                    return;
                }
                switch (msgRcvCount) {
                    case 1 :
                        msgRcvLatch1.countDown();
                        if (setAutoReadLatch1.await(1L, TimeUnit.SECONDS)) {
                            ++(expectedMsgCount);
                        }
                        break;
                    case 2 :
                        msgRcvLatch2.countDown();
                        if (setAutoReadLatch2.await(1L, TimeUnit.SECONDS)) {
                            ++(expectedMsgCount);
                        }
                        break;
                    default :
                        msgRcvLatch3.countDown();
                        break;
                }
            }
        };
        FlowControlHandler flow = new FlowControlHandler();
        Channel server = FlowControlHandlerTest.newServer(true, flow, handler);
        Channel client = FlowControlHandlerTest.newClient(server.localAddress());
        try {
            // The client connection on the server side
            Channel peer = peerRef.exchange(null, 1L, TimeUnit.SECONDS);
            client.writeAndFlush(FlowControlHandlerTest.newOneMessage()).syncUninterruptibly();
            // channelRead(1)
            Assert.assertTrue(msgRcvLatch1.await(1L, TimeUnit.SECONDS));
            // channelRead(2)
            peer.config().setAutoRead(true);
            setAutoReadLatch1.countDown();
            Assert.assertTrue(msgRcvLatch1.await(1L, TimeUnit.SECONDS));
            // channelRead(3)
            peer.config().setAutoRead(true);
            setAutoReadLatch2.countDown();
            Assert.assertTrue(msgRcvLatch3.await(1L, TimeUnit.SECONDS));
            Assert.assertTrue(flow.isQueueEmpty());
        } finally {
            client.close();
            server.close();
        }
    }

    /**
     * The {@link FlowControlHandler} will pass down messages one by one
     * if auto reading is off and the user is calling {@code read()} on
     * their own.
     */
    @Test
    public void testFlowAutoReadOff() throws Exception {
        final Exchanger<Channel> peerRef = new Exchanger<Channel>();
        final CountDownLatch msgRcvLatch1 = new CountDownLatch(1);
        final CountDownLatch msgRcvLatch2 = new CountDownLatch(2);
        final CountDownLatch msgRcvLatch3 = new CountDownLatch(3);
        ChannelInboundHandlerAdapter handler = new ChannelDuplexHandler() {
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                ctx.fireChannelActive();
                peerRef.exchange(ctx.channel(), 1L, TimeUnit.SECONDS);
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                msgRcvLatch1.countDown();
                msgRcvLatch2.countDown();
                msgRcvLatch3.countDown();
            }
        };
        FlowControlHandler flow = new FlowControlHandler();
        Channel server = FlowControlHandlerTest.newServer(false, flow, handler);
        Channel client = FlowControlHandlerTest.newClient(server.localAddress());
        try {
            // The client connection on the server side
            Channel peer = peerRef.exchange(null, 1L, TimeUnit.SECONDS);
            // Write the message
            client.writeAndFlush(FlowControlHandlerTest.newOneMessage()).syncUninterruptibly();
            // channelRead(1)
            peer.read();
            Assert.assertTrue(msgRcvLatch1.await(1L, TimeUnit.SECONDS));
            // channelRead(2)
            peer.read();
            Assert.assertTrue(msgRcvLatch2.await(1L, TimeUnit.SECONDS));
            // channelRead(3)
            peer.read();
            Assert.assertTrue(msgRcvLatch3.await(1L, TimeUnit.SECONDS));
            Assert.assertTrue(flow.isQueueEmpty());
        } finally {
            client.close();
            server.close();
        }
    }

    /**
     * This is a fictional message decoder. It decodes each {@code byte}
     * into three strings.
     */
    private static final class OneByteToThreeStringsDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            for (int i = 0; i < (in.readableBytes()); i++) {
                out.add("1");
                out.add("2");
                out.add("3");
            }
            in.readerIndex(in.readableBytes());
        }
    }
}

