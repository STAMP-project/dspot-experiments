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
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.internal.SocketUtils;
import java.net.InetSocketAddress;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DatagramPacketDecoderTest {
    private EmbeddedChannel channel;

    @Test
    public void testDecode() {
        InetSocketAddress recipient = SocketUtils.socketAddress("127.0.0.1", 10000);
        InetSocketAddress sender = SocketUtils.socketAddress("127.0.0.1", 20000);
        ByteBuf content = Unpooled.wrappedBuffer("netty".getBytes(UTF_8));
        Assert.assertTrue(channel.writeInbound(new io.netty.channel.socket.DatagramPacket(content, recipient, sender)));
        Assert.assertEquals("netty", channel.readInbound());
    }

    @Test
    public void testIsNotSharable() {
        DatagramPacketDecoderTest.testIsSharable(false);
    }

    @Test
    public void testIsSharable() {
        DatagramPacketDecoderTest.testIsSharable(true);
    }

    private static final class TestMessageToMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
        private final boolean sharable;

        TestMessageToMessageDecoder(boolean sharable) {
            this.sharable = sharable;
        }

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
            // NOOP
        }

        @Override
        public boolean isSharable() {
            return sharable;
        }
    }
}

