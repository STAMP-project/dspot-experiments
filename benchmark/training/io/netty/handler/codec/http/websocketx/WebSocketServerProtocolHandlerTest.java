/**
 * Copyright 2012 The Netty Project
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


import HttpHeaderValues.WEBSOCKET;
import HttpMethod.GET;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.util.ReferenceCountUtil;
import java.util.ArrayDeque;
import java.util.Queue;
import org.junit.Assert;
import org.junit.Test;


public class WebSocketServerProtocolHandlerTest {
    private final Queue<FullHttpResponse> responses = new ArrayDeque<FullHttpResponse>();

    @Test
    public void testHttpUpgradeRequest() throws Exception {
        EmbeddedChannel ch = createChannel(new WebSocketServerProtocolHandlerTest.MockOutboundHandler());
        ChannelHandlerContext handshakerCtx = ch.pipeline().context(WebSocketServerProtocolHandshakeHandler.class);
        WebSocketServerProtocolHandlerTest.writeUpgradeRequest(ch);
        FullHttpResponse response = responses.remove();
        Assert.assertEquals(SWITCHING_PROTOCOLS, response.status());
        response.release();
        Assert.assertNotNull(WebSocketServerProtocolHandler.getHandshaker(handshakerCtx.channel()));
    }

    @Test
    public void testSubsequentHttpRequestsAfterUpgradeShouldReturn403() throws Exception {
        EmbeddedChannel ch = createChannel();
        WebSocketServerProtocolHandlerTest.writeUpgradeRequest(ch);
        FullHttpResponse response = responses.remove();
        Assert.assertEquals(SWITCHING_PROTOCOLS, response.status());
        response.release();
        ch.writeInbound(new io.netty.handler.codec.http.DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/test"));
        response = responses.remove();
        Assert.assertEquals(FORBIDDEN, response.status());
        response.release();
    }

    @Test
    public void testHttpUpgradeRequestInvalidUpgradeHeader() {
        EmbeddedChannel ch = createChannel();
        FullHttpRequest httpRequestWithEntity = new WebSocketRequestBuilder().httpVersion(HTTP_1_1).method(GET).uri("/test").connection("Upgrade").version00().upgrade("BogusSocket").build();
        ch.writeInbound(httpRequestWithEntity);
        FullHttpResponse response = responses.remove();
        Assert.assertEquals(BAD_REQUEST, response.status());
        Assert.assertEquals("not a WebSocket handshake request: missing upgrade", WebSocketServerProtocolHandlerTest.getResponseMessage(response));
        response.release();
    }

    @Test
    public void testHttpUpgradeRequestMissingWSKeyHeader() {
        EmbeddedChannel ch = createChannel();
        HttpRequest httpRequest = new WebSocketRequestBuilder().httpVersion(HTTP_1_1).method(GET).uri("/test").key(null).connection("Upgrade").upgrade(WEBSOCKET).version13().build();
        ch.writeInbound(httpRequest);
        FullHttpResponse response = responses.remove();
        Assert.assertEquals(BAD_REQUEST, response.status());
        Assert.assertEquals("not a WebSocket request: missing key", WebSocketServerProtocolHandlerTest.getResponseMessage(response));
        response.release();
    }

    @Test
    public void testHandleTextFrame() {
        WebSocketServerProtocolHandlerTest.CustomTextFrameHandler customTextFrameHandler = new WebSocketServerProtocolHandlerTest.CustomTextFrameHandler();
        EmbeddedChannel ch = createChannel(customTextFrameHandler);
        WebSocketServerProtocolHandlerTest.writeUpgradeRequest(ch);
        if ((ch.pipeline().context(HttpRequestDecoder.class)) != null) {
            // Removing the HttpRequestDecoder because we are writing a TextWebSocketFrame and thus
            // decoding is not necessary.
            ch.pipeline().remove(HttpRequestDecoder.class);
        }
        ch.writeInbound(new TextWebSocketFrame("payload"));
        Assert.assertEquals("processed: payload", customTextFrameHandler.getContent());
    }

    private class MockOutboundHandler extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            responses.add(((FullHttpResponse) (msg)));
            promise.setSuccess();
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
        }
    }

    private static class CustomTextFrameHandler extends ChannelInboundHandlerAdapter {
        private String content;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Assert.assertNull(content);
            content = "processed: " + (text());
            ReferenceCountUtil.release(msg);
        }

        String getContent() {
            return content;
        }
    }
}

