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
package io.netty.handler.codec.http.websocketx;


import CharsetUtil.UTF_8;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests common, abstract class functionality in {@link WebSocketClientProtocolHandler}.
 */
public class WebSocketProtocolHandlerTest {
    @Test
    public void testPingFrame() {
        ByteBuf pingData = Unpooled.copiedBuffer("Hello, world", UTF_8);
        EmbeddedChannel channel = new EmbeddedChannel(new WebSocketProtocolHandler() {});
        PingWebSocketFrame inputMessage = new PingWebSocketFrame(pingData);
        Assert.assertFalse(channel.writeInbound(inputMessage));// the message was not propagated inbound

        // a Pong frame was written to the channel
        PongWebSocketFrame response = channel.readOutbound();
        Assert.assertEquals(pingData, response.content());
        pingData.release();
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testPongFrameDropFrameFalse() {
        EmbeddedChannel channel = new EmbeddedChannel(new WebSocketProtocolHandler(false) {});
        PongWebSocketFrame pingResponse = new PongWebSocketFrame();
        Assert.assertTrue(channel.writeInbound(pingResponse));
        WebSocketProtocolHandlerTest.assertPropagatedInbound(pingResponse, channel);
        pingResponse.release();
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testPongFrameDropFrameTrue() {
        EmbeddedChannel channel = new EmbeddedChannel(new WebSocketProtocolHandler(true) {});
        PongWebSocketFrame pingResponse = new PongWebSocketFrame();
        Assert.assertFalse(channel.writeInbound(pingResponse));// message was not propagated inbound

    }

    @Test
    public void testTextFrame() {
        EmbeddedChannel channel = new EmbeddedChannel(new WebSocketProtocolHandler() {});
        TextWebSocketFrame textFrame = new TextWebSocketFrame();
        Assert.assertTrue(channel.writeInbound(textFrame));
        WebSocketProtocolHandlerTest.assertPropagatedInbound(textFrame, channel);
        textFrame.release();
        Assert.assertFalse(channel.finish());
    }
}

