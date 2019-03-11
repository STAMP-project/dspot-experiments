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
package io.netty.handler.codec;


import CharsetUtil.UTF_8;
import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.SocketUtils;
import java.net.InetSocketAddress;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DatagramPacketEncoderTest {
    private EmbeddedChannel channel;

    @Test
    public void testEncode() {
        InetSocketAddress recipient = SocketUtils.socketAddress("127.0.0.1", 10000);
        InetSocketAddress sender = SocketUtils.socketAddress("127.0.0.1", 20000);
        Assert.assertTrue(channel.writeOutbound(new io.netty.channel.DefaultAddressedEnvelope<String, InetSocketAddress>("netty", recipient, sender)));
        DatagramPacket packet = channel.readOutbound();
        try {
            Assert.assertEquals("netty", packet.content().toString(UTF_8));
            Assert.assertEquals(recipient, packet.recipient());
            Assert.assertEquals(sender, packet.sender());
        } finally {
            packet.release();
        }
    }

    @Test
    public void testUnmatchedMessageType() {
        InetSocketAddress recipient = SocketUtils.socketAddress("127.0.0.1", 10000);
        InetSocketAddress sender = SocketUtils.socketAddress("127.0.0.1", 20000);
        io.netty.channel.DefaultAddressedEnvelope<Long, InetSocketAddress> envelope = new io.netty.channel.DefaultAddressedEnvelope<Long, InetSocketAddress>(1L, recipient, sender);
        Assert.assertTrue(channel.writeOutbound(envelope));
        io.netty.channel.DefaultAddressedEnvelope<Long, InetSocketAddress> output = channel.readOutbound();
        try {
            Assert.assertSame(envelope, output);
        } finally {
            output.release();
        }
    }

    @Test
    public void testUnmatchedType() {
        String netty = "netty";
        Assert.assertTrue(channel.writeOutbound(netty));
        Assert.assertSame(netty, channel.readOutbound());
    }

    @Test
    public void testIsNotSharable() {
        DatagramPacketEncoderTest.testSharable(false);
    }

    @Test
    public void testIsSharable() {
        DatagramPacketEncoderTest.testSharable(true);
    }

    private static final class TestMessageToMessageEncoder extends MessageToMessageEncoder<AddressedEnvelope<ByteBuf, InetSocketAddress>> {
        private final boolean sharable;

        TestMessageToMessageEncoder(boolean sharable) {
            this.sharable = sharable;
        }

        @Override
        protected void encode(ChannelHandlerContext ctx, AddressedEnvelope<ByteBuf, InetSocketAddress> msg, List<Object> out) {
            // NOOP
        }

        @Override
        public boolean isSharable() {
            return sharable;
        }
    }
}

