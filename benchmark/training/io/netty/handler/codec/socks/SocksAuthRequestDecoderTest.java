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
package io.netty.handler.codec.socks;


import SocksSubnegotiationVersion.AUTH_PASSWORD;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;


public class SocksAuthRequestDecoderTest {
    private static final String username = "testUserName";

    private static final String password = "testPassword";

    @Test
    public void testAuthRequestDecoder() {
        SocksAuthRequest msg = new SocksAuthRequest(SocksAuthRequestDecoderTest.username, SocksAuthRequestDecoderTest.password);
        SocksAuthRequestDecoder decoder = new SocksAuthRequestDecoder();
        EmbeddedChannel embedder = new EmbeddedChannel(decoder);
        SocksCommonTestUtils.writeMessageIntoEmbedder(embedder, msg);
        msg = embedder.readInbound();
        Assert.assertEquals(SocksAuthRequestDecoderTest.username, msg.username());
        Assert.assertEquals(SocksAuthRequestDecoderTest.password, msg.password());
        Assert.assertNull(embedder.readInbound());
    }

    @Test
    public void testAuthRequestDecoderPartialSend() {
        EmbeddedChannel ch = new EmbeddedChannel(new SocksAuthRequestDecoder());
        ByteBuf byteBuf = Unpooled.buffer(16);
        // Send username and password size
        byteBuf.writeByte(AUTH_PASSWORD.byteValue());
        byteBuf.writeByte(SocksAuthRequestDecoderTest.username.length());
        byteBuf.writeBytes(SocksAuthRequestDecoderTest.username.getBytes());
        byteBuf.writeByte(SocksAuthRequestDecoderTest.password.length());
        ch.writeInbound(byteBuf);
        // Check that channel is empty
        Assert.assertNull(ch.readInbound());
        // Send password
        ByteBuf byteBuf2 = Unpooled.buffer();
        byteBuf2.writeBytes(SocksAuthRequestDecoderTest.password.getBytes());
        ch.writeInbound(byteBuf2);
        // Read message from channel
        SocksAuthRequest msg = ch.readInbound();
        // Check message
        Assert.assertEquals(SocksAuthRequestDecoderTest.username, msg.username());
        Assert.assertEquals(SocksAuthRequestDecoderTest.password, msg.password());
        Assert.assertFalse(ch.finishAndReleaseAll());
    }
}

