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
package org.jboss.netty.handler.codec.http.websocketx;


import HttpMethod.GET;
import java.util.LinkedList;
import java.util.Queue;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.junit.Assert;
import org.junit.Test;


public class WebSocketServerProtocolHandlerTest {
    @Test
    public void testHttpUpgradeRequest() {
        DecoderEmbedder<Object> embedder = WebSocketServerProtocolHandlerTest.decoderEmbedder();
        ChannelHandlerContext ctx = embedder.getPipeline().getContext(WebSocketServerProtocolHandshakeHandler.class);
        WebSocketServerProtocolHandlerTest.HttpResponseInterceptor responseInterceptor = WebSocketServerProtocolHandlerTest.addHttpResponseInterceptor(embedder);
        embedder.offer(WebSocketRequestBuilder.sucessful());
        HttpResponse response = responseInterceptor.getHttpResponse();
        Assert.assertEquals(SWITCHING_PROTOCOLS, response.getStatus());
        Assert.assertNotNull(WebSocketServerProtocolHandler.getHandshaker(ctx));
    }

    @Test
    public void testSubsequentHttpRequestsAfterUpgradeShouldReturn403() throws Exception {
        DecoderEmbedder<Object> embedder = WebSocketServerProtocolHandlerTest.decoderEmbedder();
        WebSocketServerProtocolHandlerTest.HttpResponseInterceptor responseInterceptor = WebSocketServerProtocolHandlerTest.addHttpResponseInterceptor(embedder);
        embedder.offer(WebSocketRequestBuilder.sucessful());
        embedder.offer(new org.jboss.netty.handler.codec.http.DefaultHttpRequest(HTTP_1_1, HttpMethod.GET, "path"));
        Assert.assertEquals(SWITCHING_PROTOCOLS, responseInterceptor.getHttpResponse().getStatus());
        Assert.assertEquals(FORBIDDEN, responseInterceptor.getHttpResponse().getStatus());
    }

    @Test
    public void testHttpUpgradeRequestInvalidUpgradeHeader() {
        DecoderEmbedder<Object> embedder = WebSocketServerProtocolHandlerTest.decoderEmbedder();
        HttpRequest invalidUpgradeRequest = new WebSocketRequestBuilder().httpVersion(HTTP_1_1).method(GET).uri("/test").connection("Upgrade").version00().upgrade("BogusSocket").build();
        try {
            embedder.offer(invalidUpgradeRequest);
        } catch (Exception e) {
            WebSocketServerProtocolHandlerTest.assertWebSocketHandshakeException(e);
        }
    }

    @Test
    public void testHttpUpgradeRequestMissingWSKeyHeader() {
        DecoderEmbedder<Object> embedder = WebSocketServerProtocolHandlerTest.decoderEmbedder();
        HttpRequest missingWSKeyRequest = new WebSocketRequestBuilder().httpVersion(HTTP_1_1).method(GET).uri("/test").key(null).connection("Upgrade").upgrade(WEBSOCKET.toLowerCase()).version13().build();
        try {
            embedder.offer(missingWSKeyRequest);
        } catch (Exception e) {
            WebSocketServerProtocolHandlerTest.assertWebSocketHandshakeException(e);
        }
    }

    @Test
    public void testHandleTextFrame() {
        WebSocketServerProtocolHandlerTest.CustomTextFrameHandler customTextFrameHandler = new WebSocketServerProtocolHandlerTest.CustomTextFrameHandler();
        DecoderEmbedder<Object> embedder = WebSocketServerProtocolHandlerTest.decoderEmbedder(customTextFrameHandler);
        embedder.offer(WebSocketRequestBuilder.sucessful());
        embedder.offer(new TextWebSocketFrame("payload"));
        Assert.assertEquals("processed: payload", customTextFrameHandler.getContent());
    }

    private static class CustomTextFrameHandler extends SimpleChannelHandler {
        private String content;

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            if ((e.getMessage()) instanceof TextWebSocketFrame) {
                TextWebSocketFrame frame = ((TextWebSocketFrame) (e.getMessage()));
                content = "processed: " + (frame.getText());
            }
        }

        public String getContent() {
            return content;
        }
    }

    private static class HttpResponseInterceptor extends HttpResponseEncoder {
        private final Queue<HttpResponse> responses = new LinkedList<HttpResponse>();

        @Override
        protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
            responses.add(((HttpResponse) (msg)));
            return super.encode(ctx, channel, msg);
        }

        public HttpResponse getHttpResponse() {
            return responses.poll();
        }
    }
}

