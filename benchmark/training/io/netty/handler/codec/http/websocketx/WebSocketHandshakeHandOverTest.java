/**
 * Copyright 2014 The Netty Project
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
package io.netty.handler.codec.http.websocketx;


import WebSocketServerProtocolHandler.HandshakeComplete;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler.ClientHandshakeStateEvent;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.ServerHandshakeStateEvent;
import org.junit.Assert;
import org.junit.Test;


public class WebSocketHandshakeHandOverTest {
    private boolean serverReceivedHandshake;

    private HandshakeComplete serverHandshakeComplete;

    private boolean clientReceivedHandshake;

    private boolean clientReceivedMessage;

    @Test
    public void testHandover() throws Exception {
        EmbeddedChannel serverChannel = WebSocketHandshakeHandOverTest.createServerChannel(new io.netty.channel.SimpleChannelInboundHandler<Object>() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                if (evt == (ServerHandshakeStateEvent.HANDSHAKE_COMPLETE)) {
                    serverReceivedHandshake = true;
                    // immediately send a message to the client on connect
                    ctx.writeAndFlush(new TextWebSocketFrame("abc"));
                } else
                    if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
                        serverHandshakeComplete = ((WebSocketServerProtocolHandler.HandshakeComplete) (evt));
                    }

            }

            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            }
        });
        EmbeddedChannel clientChannel = WebSocketHandshakeHandOverTest.createClientChannel(new io.netty.channel.SimpleChannelInboundHandler<Object>() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
                if (evt == (ClientHandshakeStateEvent.HANDSHAKE_COMPLETE)) {
                    clientReceivedHandshake = true;
                }
            }

            @Override
            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof TextWebSocketFrame) {
                    clientReceivedMessage = true;
                }
            }
        });
        // Transfer the handshake from the client to the server
        WebSocketHandshakeHandOverTest.transferAllDataWithMerge(clientChannel, serverChannel);
        Assert.assertTrue(serverReceivedHandshake);
        Assert.assertNotNull(serverHandshakeComplete);
        Assert.assertEquals("/test", serverHandshakeComplete.requestUri());
        Assert.assertEquals(8, serverHandshakeComplete.requestHeaders().size());
        Assert.assertEquals("test-proto-2", serverHandshakeComplete.selectedSubprotocol());
        // Transfer the handshake response and the websocket message to the client
        WebSocketHandshakeHandOverTest.transferAllDataWithMerge(serverChannel, clientChannel);
        Assert.assertTrue(clientReceivedHandshake);
        Assert.assertTrue(clientReceivedMessage);
    }
}

