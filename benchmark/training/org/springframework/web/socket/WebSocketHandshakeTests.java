/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.socket;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;


/**
 * Client and server-side WebSocket integration tests.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
@RunWith(Parameterized.class)
public class WebSocketHandshakeTests extends AbstractWebSocketIntegrationTests {
    @Test
    public void subProtocolNegotiation() throws Exception {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.setSecWebSocketProtocol("foo");
        URI url = new URI(((getWsBaseUrl()) + "/ws"));
        WebSocketSession session = this.webSocketClient.doHandshake(new TextWebSocketHandler(), headers, url).get();
        Assert.assertEquals("foo", session.getAcceptedProtocol());
        session.close();
    }

    // SPR-12727
    @Test
    public void unsolicitedPongWithEmptyPayload() throws Exception {
        String url = (getWsBaseUrl()) + "/ws";
        WebSocketSession session = this.webSocketClient.doHandshake(new AbstractWebSocketHandler() {}, url).get();
        WebSocketHandshakeTests.TestWebSocketHandler serverHandler = this.wac.getBean(WebSocketHandshakeTests.TestWebSocketHandler.class);
        serverHandler.setWaitMessageCount(1);
        session.sendMessage(new PongMessage());
        serverHandler.await();
        Assert.assertNull(serverHandler.getTransportError());
        Assert.assertEquals(1, serverHandler.getReceivedMessages().size());
        Assert.assertEquals(PongMessage.class, serverHandler.getReceivedMessages().get(0).getClass());
    }

    @Configuration
    @EnableWebSocket
    static class TestConfig implements WebSocketConfigurer {
        @Autowired
        private DefaultHandshakeHandler handshakeHandler;// can't rely on classpath for server detection


        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            this.handshakeHandler.setSupportedProtocols("foo", "bar", "baz");
            registry.addHandler(handler(), "/ws").setHandshakeHandler(this.handshakeHandler);
        }

        @Bean
        public WebSocketHandshakeTests.TestWebSocketHandler handler() {
            return new WebSocketHandshakeTests.TestWebSocketHandler();
        }
    }

    @SuppressWarnings("rawtypes")
    private static class TestWebSocketHandler extends AbstractWebSocketHandler {
        private List<WebSocketMessage> receivedMessages = new ArrayList<>();

        private int waitMessageCount;

        private final CountDownLatch latch = new CountDownLatch(1);

        private Throwable transportError;

        public void setWaitMessageCount(int waitMessageCount) {
            this.waitMessageCount = waitMessageCount;
        }

        public List<WebSocketMessage> getReceivedMessages() {
            return this.receivedMessages;
        }

        public Throwable getTransportError() {
            return this.transportError;
        }

        @Override
        public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
            this.receivedMessages.add(message);
            if ((this.receivedMessages.size()) >= (this.waitMessageCount)) {
                this.latch.countDown();
            }
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            this.transportError = exception;
            this.latch.countDown();
        }

        public void await() throws InterruptedException {
            this.latch.await(5, TimeUnit.SECONDS);
        }
    }
}

