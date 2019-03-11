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
package io.netty.handler.codec.http.websocketx;


import CharsetUtil.US_ASCII;
import HttpHeaderNames.CONNECTION;
import HttpHeaderNames.HOST;
import HttpHeaderNames.SEC_WEBSOCKET_KEY1;
import HttpHeaderNames.SEC_WEBSOCKET_PROTOCOL;
import HttpHeaderNames.UPGRADE;
import HttpHeaderValues.WEBSOCKET;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpVersion;
import org.junit.Assert;
import org.junit.Test;


public class WebSocketServerHandshaker00Test {
    @Test
    public void testPerformOpeningHandshake() {
        WebSocketServerHandshaker00Test.testPerformOpeningHandshake0(true);
    }

    @Test
    public void testPerformOpeningHandshakeSubProtocolNotSupported() {
        WebSocketServerHandshaker00Test.testPerformOpeningHandshake0(false);
    }

    @Test
    public void testPerformHandshakeWithoutOriginHeader() {
        EmbeddedChannel ch = new EmbeddedChannel(new HttpObjectAggregator(42), new HttpRequestDecoder(), new HttpResponseEncoder());
        FullHttpRequest req = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/chat", Unpooled.copiedBuffer("^n:ds[4U", US_ASCII));
        req.headers().set(HOST, "server.example.com");
        req.headers().set(UPGRADE, WEBSOCKET);
        req.headers().set(CONNECTION, "Upgrade");
        req.headers().set(SEC_WEBSOCKET_KEY1, "4 @1  46546xW%0l 1 5");
        req.headers().set(SEC_WEBSOCKET_PROTOCOL, "chat, superchat");
        WebSocketServerHandshaker00 handshaker00 = new WebSocketServerHandshaker00("ws://example.com/chat", "chat", Integer.MAX_VALUE);
        try {
            handshaker00.handshake(ch, req);
            Assert.fail("Expecting WebSocketHandshakeException");
        } catch (WebSocketHandshakeException e) {
            Assert.assertEquals(("Missing origin header, got only " + "[host, upgrade, connection, sec-websocket-key1, sec-websocket-protocol]"), e.getMessage());
        } finally {
            req.release();
        }
    }
}

