/**
 * Copyright 2013 The Netty Project
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
package io.netty.handler.codec.http.websocketx;


import CharsetUtil.UTF_8;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.ReferenceCountUtil;
import org.junit.Assert;
import org.junit.Test;


public class WebSocketFrameAggregatorTest {
    private static final byte[] content1 = "Content1".getBytes(UTF_8);

    private static final byte[] content2 = "Content2".getBytes(UTF_8);

    private static final byte[] content3 = "Content3".getBytes(UTF_8);

    private static final byte[] aggregatedContent = new byte[((WebSocketFrameAggregatorTest.content1.length) + (WebSocketFrameAggregatorTest.content2.length)) + (WebSocketFrameAggregatorTest.content3.length)];

    static {
        System.arraycopy(WebSocketFrameAggregatorTest.content1, 0, WebSocketFrameAggregatorTest.aggregatedContent, 0, WebSocketFrameAggregatorTest.content1.length);
        System.arraycopy(WebSocketFrameAggregatorTest.content2, 0, WebSocketFrameAggregatorTest.aggregatedContent, WebSocketFrameAggregatorTest.content1.length, WebSocketFrameAggregatorTest.content2.length);
        System.arraycopy(WebSocketFrameAggregatorTest.content3, 0, WebSocketFrameAggregatorTest.aggregatedContent, ((WebSocketFrameAggregatorTest.content1.length) + (WebSocketFrameAggregatorTest.content2.length)), WebSocketFrameAggregatorTest.content3.length);
    }

    @Test
    public void testAggregationBinary() {
        EmbeddedChannel channel = new EmbeddedChannel(new WebSocketFrameAggregator(Integer.MAX_VALUE));
        channel.writeInbound(new BinaryWebSocketFrame(true, 1, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        channel.writeInbound(new BinaryWebSocketFrame(false, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        channel.writeInbound(new ContinuationWebSocketFrame(false, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content2)));
        channel.writeInbound(new PingWebSocketFrame(Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        channel.writeInbound(new PongWebSocketFrame(Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        channel.writeInbound(new ContinuationWebSocketFrame(true, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content3)));
        Assert.assertTrue(channel.finish());
        BinaryWebSocketFrame frame = channel.readInbound();
        Assert.assertTrue(frame.isFinalFragment());
        Assert.assertEquals(1, frame.rsv());
        Assert.assertArrayEquals(WebSocketFrameAggregatorTest.content1, WebSocketFrameAggregatorTest.toBytes(frame.content()));
        PingWebSocketFrame frame2 = channel.readInbound();
        Assert.assertTrue(frame2.isFinalFragment());
        Assert.assertEquals(0, frame2.rsv());
        Assert.assertArrayEquals(WebSocketFrameAggregatorTest.content1, WebSocketFrameAggregatorTest.toBytes(frame2.content()));
        PongWebSocketFrame frame3 = channel.readInbound();
        Assert.assertTrue(frame3.isFinalFragment());
        Assert.assertEquals(0, frame3.rsv());
        Assert.assertArrayEquals(WebSocketFrameAggregatorTest.content1, WebSocketFrameAggregatorTest.toBytes(frame3.content()));
        BinaryWebSocketFrame frame4 = channel.readInbound();
        Assert.assertTrue(frame4.isFinalFragment());
        Assert.assertEquals(0, frame4.rsv());
        Assert.assertArrayEquals(WebSocketFrameAggregatorTest.aggregatedContent, WebSocketFrameAggregatorTest.toBytes(frame4.content()));
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void testAggregationText() {
        EmbeddedChannel channel = new EmbeddedChannel(new WebSocketFrameAggregator(Integer.MAX_VALUE));
        channel.writeInbound(new TextWebSocketFrame(true, 1, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        channel.writeInbound(new TextWebSocketFrame(false, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        channel.writeInbound(new ContinuationWebSocketFrame(false, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content2)));
        channel.writeInbound(new PingWebSocketFrame(Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        channel.writeInbound(new PongWebSocketFrame(Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        channel.writeInbound(new ContinuationWebSocketFrame(true, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content3)));
        Assert.assertTrue(channel.finish());
        TextWebSocketFrame frame = channel.readInbound();
        Assert.assertTrue(frame.isFinalFragment());
        Assert.assertEquals(1, frame.rsv());
        Assert.assertArrayEquals(WebSocketFrameAggregatorTest.content1, WebSocketFrameAggregatorTest.toBytes(frame.content()));
        PingWebSocketFrame frame2 = channel.readInbound();
        Assert.assertTrue(frame2.isFinalFragment());
        Assert.assertEquals(0, frame2.rsv());
        Assert.assertArrayEquals(WebSocketFrameAggregatorTest.content1, WebSocketFrameAggregatorTest.toBytes(frame2.content()));
        PongWebSocketFrame frame3 = channel.readInbound();
        Assert.assertTrue(frame3.isFinalFragment());
        Assert.assertEquals(0, frame3.rsv());
        Assert.assertArrayEquals(WebSocketFrameAggregatorTest.content1, WebSocketFrameAggregatorTest.toBytes(frame3.content()));
        TextWebSocketFrame frame4 = channel.readInbound();
        Assert.assertTrue(frame4.isFinalFragment());
        Assert.assertEquals(0, frame4.rsv());
        Assert.assertArrayEquals(WebSocketFrameAggregatorTest.aggregatedContent, WebSocketFrameAggregatorTest.toBytes(frame4.content()));
        Assert.assertNull(channel.readInbound());
    }

    @Test
    public void textFrameTooBig() throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(new WebSocketFrameAggregator(8));
        channel.writeInbound(new BinaryWebSocketFrame(true, 1, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        channel.writeInbound(new BinaryWebSocketFrame(false, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        try {
            channel.writeInbound(new ContinuationWebSocketFrame(false, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content2)));
            Assert.fail();
        } catch (TooLongFrameException e) {
            // expected
        }
        channel.writeInbound(new ContinuationWebSocketFrame(false, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content2)));
        channel.writeInbound(new ContinuationWebSocketFrame(true, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content2)));
        channel.writeInbound(new BinaryWebSocketFrame(true, 1, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        channel.writeInbound(new BinaryWebSocketFrame(false, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content1)));
        try {
            channel.writeInbound(new ContinuationWebSocketFrame(false, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content2)));
            Assert.fail();
        } catch (TooLongFrameException e) {
            // expected
        }
        channel.writeInbound(new ContinuationWebSocketFrame(false, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content2)));
        channel.writeInbound(new ContinuationWebSocketFrame(true, 0, Unpooled.wrappedBuffer(WebSocketFrameAggregatorTest.content2)));
        for (; ;) {
            Object msg = channel.readInbound();
            if (msg == null) {
                break;
            }
            ReferenceCountUtil.release(msg);
        }
        channel.finish();
    }
}

