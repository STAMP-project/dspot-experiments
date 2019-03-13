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
package io.netty.handler.codec.stomp;


import LastStompContentSubframe.EMPTY_LAST_CONTENT;
import StompCommand.CONNECT;
import StompCommand.CONNECTED;
import StompCommand.SEND;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;


public class StompSubframeDecoderTest {
    private EmbeddedChannel channel;

    @Test
    public void testSingleFrameDecoding() {
        ByteBuf incoming = Unpooled.buffer();
        incoming.writeBytes(StompTestConstants.CONNECT_FRAME.getBytes());
        channel.writeInbound(incoming);
        StompHeadersSubframe frame = channel.readInbound();
        Assert.assertNotNull(frame);
        Assert.assertEquals(CONNECT, frame.command());
        StompContentSubframe content = channel.readInbound();
        Assert.assertSame(EMPTY_LAST_CONTENT, content);
        content.release();
        Object o = channel.readInbound();
        Assert.assertNull(o);
    }

    @Test
    public void testSingleFrameWithBodyAndContentLength() {
        ByteBuf incoming = Unpooled.buffer();
        incoming.writeBytes(StompTestConstants.SEND_FRAME_2.getBytes());
        channel.writeInbound(incoming);
        StompHeadersSubframe frame = channel.readInbound();
        Assert.assertNotNull(frame);
        Assert.assertEquals(SEND, frame.command());
        StompContentSubframe content = channel.readInbound();
        Assert.assertTrue((content instanceof LastStompContentSubframe));
        String s = content.content().toString(UTF_8);
        Assert.assertEquals("hello, queue a!!!", s);
        content.release();
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testSingleFrameWithBodyWithoutContentLength() {
        ByteBuf incoming = Unpooled.buffer();
        incoming.writeBytes(StompTestConstants.SEND_FRAME_1.getBytes());
        channel.writeInbound(incoming);
        StompHeadersSubframe frame = channel.readInbound();
        Assert.assertNotNull(frame);
        Assert.assertEquals(SEND, frame.command());
        StompContentSubframe content = channel.readInbound();
        Assert.assertTrue((content instanceof LastStompContentSubframe));
        String s = content.content().toString(UTF_8);
        Assert.assertEquals("hello, queue a!", s);
        content.release();
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testSingleFrameChunked() {
        EmbeddedChannel channel = new EmbeddedChannel(new StompSubframeDecoder(10000, 5));
        ByteBuf incoming = Unpooled.buffer();
        incoming.writeBytes(StompTestConstants.SEND_FRAME_2.getBytes());
        channel.writeInbound(incoming);
        StompHeadersSubframe frame = channel.readInbound();
        Assert.assertNotNull(frame);
        Assert.assertEquals(SEND, frame.command());
        StompContentSubframe content = channel.readInbound();
        String s = content.content().toString(UTF_8);
        Assert.assertEquals("hello", s);
        content.release();
        content = channel.readInbound();
        s = content.content().toString(UTF_8);
        Assert.assertEquals(", que", s);
        content.release();
        content = channel.readInbound();
        s = content.content().toString(UTF_8);
        Assert.assertEquals("ue a!", s);
        content.release();
        content = channel.readInbound();
        s = content.content().toString(UTF_8);
        Assert.assertEquals("!!", s);
        content.release();
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testMultipleFramesDecoding() {
        ByteBuf incoming = Unpooled.buffer();
        incoming.writeBytes(StompTestConstants.CONNECT_FRAME.getBytes());
        incoming.writeBytes(StompTestConstants.CONNECTED_FRAME.getBytes());
        channel.writeInbound(incoming);
        StompHeadersSubframe frame = channel.readInbound();
        Assert.assertNotNull(frame);
        Assert.assertEquals(CONNECT, frame.command());
        StompContentSubframe content = channel.readInbound();
        Assert.assertSame(EMPTY_LAST_CONTENT, content);
        content.release();
        StompHeadersSubframe frame2 = channel.readInbound();
        Assert.assertNotNull(frame2);
        Assert.assertEquals(CONNECTED, frame2.command());
        StompContentSubframe content2 = channel.readInbound();
        Assert.assertSame(EMPTY_LAST_CONTENT, content2);
        content2.release();
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testValidateHeadersDecodingDisabled() {
        ByteBuf invalidIncoming = Unpooled.copiedBuffer(StompTestConstants.FRAME_WITH_INVALID_HEADER.getBytes(US_ASCII));
        Assert.assertTrue(channel.writeInbound(invalidIncoming));
        StompHeadersSubframe frame = channel.readInbound();
        Assert.assertNotNull(frame);
        Assert.assertEquals(SEND, frame.command());
        Assert.assertTrue(frame.headers().contains("destination"));
        Assert.assertTrue(frame.headers().contains("content-type"));
        Assert.assertFalse(frame.headers().contains("current-time"));
        StompContentSubframe content = channel.readInbound();
        String s = content.content().toString(UTF_8);
        Assert.assertEquals("some body", s);
        content.release();
    }

    @Test
    public void testValidateHeadersDecodingEnabled() {
        channel = new EmbeddedChannel(new StompSubframeDecoder(true));
        ByteBuf invalidIncoming = Unpooled.copiedBuffer(StompTestConstants.FRAME_WITH_INVALID_HEADER.getBytes(US_ASCII));
        Assert.assertTrue(channel.writeInbound(invalidIncoming));
        StompHeadersSubframe frame = channel.readInbound();
        Assert.assertNotNull(frame);
        Assert.assertTrue(frame.decoderResult().isFailure());
        Assert.assertEquals("a header value or name contains a prohibited character ':', current-time:2000-01-01T00:00:00", frame.decoderResult().cause().getMessage());
    }
}

