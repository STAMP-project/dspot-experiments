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
package org.springframework.web.socket.config.annotation;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.AbstractWebSocketIntegrationTests;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;


/**
 * Integration tests for WebSocket Java server-side configuration.
 *
 * @author Rossen Stoyanchev
 */
@RunWith(Parameterized.class)
public class WebSocketConfigurationTests extends AbstractWebSocketIntegrationTests {
    @Test
    public void registerWebSocketHandler() throws Exception {
        WebSocketSession session = this.webSocketClient.doHandshake(new AbstractWebSocketHandler() {}, ((getWsBaseUrl()) + "/ws")).get();
        WebSocketConfigurationTests.TestHandler serverHandler = this.wac.getBean(WebSocketConfigurationTests.TestHandler.class);
        Assert.assertTrue(serverHandler.connectLatch.await(2, TimeUnit.SECONDS));
        session.close();
    }

    @Test
    public void registerWebSocketHandlerWithSockJS() throws Exception {
        WebSocketSession session = this.webSocketClient.doHandshake(new AbstractWebSocketHandler() {}, ((getWsBaseUrl()) + "/sockjs/websocket")).get();
        WebSocketConfigurationTests.TestHandler serverHandler = this.wac.getBean(WebSocketConfigurationTests.TestHandler.class);
        Assert.assertTrue(serverHandler.connectLatch.await(2, TimeUnit.SECONDS));
        session.close();
    }

    @Configuration
    @EnableWebSocket
    static class TestConfig implements WebSocketConfigurer {
        @Autowired
        private HandshakeHandler handshakeHandler;// can't rely on classpath for server detection


        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            registry.addHandler(serverHandler(), "/ws").setHandshakeHandler(this.handshakeHandler);
            registry.addHandler(serverHandler(), "/sockjs").withSockJS().setTransportHandlerOverrides(new org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler(this.handshakeHandler));
        }

        @Bean
        public WebSocketConfigurationTests.TestHandler serverHandler() {
            return new WebSocketConfigurationTests.TestHandler();
        }
    }

    private static class TestHandler extends AbstractWebSocketHandler {
        private CountDownLatch connectLatch = new CountDownLatch(1);

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            this.connectLatch.countDown();
        }
    }
}

