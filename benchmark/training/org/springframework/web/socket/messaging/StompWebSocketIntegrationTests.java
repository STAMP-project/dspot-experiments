/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.socket.messaging;


import ComponentScan.Filter;
import StompCommand.CONNECT;
import StompCommand.SEND;
import StompCommand.SUBSCRIBE;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.AbstractWebSocketIntegrationTests;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.DelegatingWebSocketMessageBrokerConfiguration;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;


/**
 * Integration tests with annotated message-handling methods.
 *
 * @author Rossen Stoyanchev
 */
@RunWith(Parameterized.class)
public class StompWebSocketIntegrationTests extends AbstractWebSocketIntegrationTests {
    private static final long TIMEOUT = 10;

    @Test
    public void sendMessageToController() throws Exception {
        TextMessage message = StompTextMessageBuilder.create(SEND).headers("destination:/app/simple").build();
        WebSocketSession session = doHandshake(new StompWebSocketIntegrationTests.TestClientWebSocketHandler(0, message), "/ws").get();
        StompWebSocketIntegrationTests.SimpleController controller = this.wac.getBean(StompWebSocketIntegrationTests.SimpleController.class);
        try {
            Assert.assertTrue(controller.latch.await(StompWebSocketIntegrationTests.TIMEOUT, TimeUnit.SECONDS));
        } finally {
            session.close();
        }
    }

    @Test
    public void sendMessageToControllerAndReceiveReplyViaTopic() throws Exception {
        TextMessage m0 = StompTextMessageBuilder.create(CONNECT).headers("accept-version:1.1").build();
        TextMessage m1 = StompTextMessageBuilder.create(SUBSCRIBE).headers("id:subs1", "destination:/topic/increment").build();
        TextMessage m2 = StompTextMessageBuilder.create(SEND).headers("destination:/app/increment").body("5").build();
        StompWebSocketIntegrationTests.TestClientWebSocketHandler clientHandler = new StompWebSocketIntegrationTests.TestClientWebSocketHandler(2, m0, m1, m2);
        WebSocketSession session = doHandshake(clientHandler, "/ws").get();
        try {
            Assert.assertTrue(clientHandler.latch.await(StompWebSocketIntegrationTests.TIMEOUT, TimeUnit.SECONDS));
        } finally {
            session.close();
        }
    }

    // SPR-10930
    @Test
    public void sendMessageToBrokerAndReceiveReplyViaTopic() throws Exception {
        TextMessage m0 = StompTextMessageBuilder.create(CONNECT).headers("accept-version:1.1").build();
        TextMessage m1 = StompTextMessageBuilder.create(SUBSCRIBE).headers("id:subs1", "destination:/topic/foo").build();
        TextMessage m2 = StompTextMessageBuilder.create(SEND).headers("destination:/topic/foo").body("5").build();
        StompWebSocketIntegrationTests.TestClientWebSocketHandler clientHandler = new StompWebSocketIntegrationTests.TestClientWebSocketHandler(2, m0, m1, m2);
        WebSocketSession session = doHandshake(clientHandler, "/ws").get();
        try {
            Assert.assertTrue(clientHandler.latch.await(StompWebSocketIntegrationTests.TIMEOUT, TimeUnit.SECONDS));
            String payload = clientHandler.actual.get(1).getPayload();
            Assert.assertTrue(("Expected STOMP MESSAGE, got " + payload), payload.startsWith("MESSAGE\n"));
        } finally {
            session.close();
        }
    }

    // SPR-11648
    @Test
    public void sendSubscribeToControllerAndReceiveReply() throws Exception {
        TextMessage m0 = StompTextMessageBuilder.create(CONNECT).headers("accept-version:1.1").build();
        String destHeader = "destination:/app/number";
        TextMessage m1 = StompTextMessageBuilder.create(SUBSCRIBE).headers("id:subs1", destHeader).build();
        StompWebSocketIntegrationTests.TestClientWebSocketHandler clientHandler = new StompWebSocketIntegrationTests.TestClientWebSocketHandler(2, m0, m1);
        WebSocketSession session = doHandshake(clientHandler, "/ws").get();
        try {
            Assert.assertTrue(clientHandler.latch.await(StompWebSocketIntegrationTests.TIMEOUT, TimeUnit.SECONDS));
            String payload = clientHandler.actual.get(1).getPayload();
            Assert.assertTrue(("Expected STOMP destination=/app/number, got " + payload), payload.contains(destHeader));
            Assert.assertTrue(("Expected STOMP Payload=42, got " + payload), payload.contains("42"));
        } finally {
            session.close();
        }
    }

    @Test
    public void handleExceptionAndSendToUser() throws Exception {
        String destHeader = "destination:/user/queue/error";
        TextMessage m0 = StompTextMessageBuilder.create(CONNECT).headers("accept-version:1.1").build();
        TextMessage m1 = StompTextMessageBuilder.create(SUBSCRIBE).headers("id:subs1", destHeader).build();
        TextMessage m2 = StompTextMessageBuilder.create(SEND).headers("destination:/app/exception").build();
        StompWebSocketIntegrationTests.TestClientWebSocketHandler clientHandler = new StompWebSocketIntegrationTests.TestClientWebSocketHandler(2, m0, m1, m2);
        WebSocketSession session = doHandshake(clientHandler, "/ws").get();
        try {
            Assert.assertTrue(clientHandler.latch.await(StompWebSocketIntegrationTests.TIMEOUT, TimeUnit.SECONDS));
            String payload = clientHandler.actual.get(1).getPayload();
            Assert.assertTrue(payload.startsWith("MESSAGE\n"));
            Assert.assertTrue(payload.contains("destination:/user/queue/error\n"));
            Assert.assertTrue(payload.endsWith("Got error: Bad input\u0000"));
        } finally {
            session.close();
        }
    }

    @Test
    public void webSocketScope() throws Exception {
        TextMessage m0 = StompTextMessageBuilder.create(CONNECT).headers("accept-version:1.1").build();
        TextMessage m1 = StompTextMessageBuilder.create(SUBSCRIBE).headers("id:subs1", "destination:/topic/scopedBeanValue").build();
        TextMessage m2 = StompTextMessageBuilder.create(SEND).headers("destination:/app/scopedBeanValue").build();
        StompWebSocketIntegrationTests.TestClientWebSocketHandler clientHandler = new StompWebSocketIntegrationTests.TestClientWebSocketHandler(2, m0, m1, m2);
        WebSocketSession session = doHandshake(clientHandler, "/ws").get();
        try {
            Assert.assertTrue(clientHandler.latch.await(StompWebSocketIntegrationTests.TIMEOUT, TimeUnit.SECONDS));
            String payload = clientHandler.actual.get(1).getPayload();
            Assert.assertTrue(payload.startsWith("MESSAGE\n"));
            Assert.assertTrue(payload.contains("destination:/topic/scopedBeanValue\n"));
            Assert.assertTrue(payload.endsWith("55\u0000"));
        } finally {
            session.close();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Controller
    private @interface IntegrationTestController {}

    @StompWebSocketIntegrationTests.IntegrationTestController
    static class SimpleController {
        private CountDownLatch latch = new CountDownLatch(1);

        @MessageMapping("/simple")
        public void handle() {
            this.latch.countDown();
        }

        @MessageMapping("/exception")
        public void handleWithError() {
            throw new IllegalArgumentException("Bad input");
        }

        @MessageExceptionHandler
        @SendToUser("/queue/error")
        public String handleException(IllegalArgumentException ex) {
            return "Got error: " + (ex.getMessage());
        }
    }

    @StompWebSocketIntegrationTests.IntegrationTestController
    static class IncrementController {
        @MessageMapping("/increment")
        public int handle(int i) {
            return i + 1;
        }

        @SubscribeMapping("/number")
        public int number() {
            return 42;
        }
    }

    @StompWebSocketIntegrationTests.IntegrationTestController
    static class ScopedBeanController {
        private final StompWebSocketIntegrationTests.ScopedBean scopedBean;

        @Autowired
        public ScopedBeanController(StompWebSocketIntegrationTests.ScopedBean scopedBean) {
            this.scopedBean = scopedBean;
        }

        @MessageMapping("/scopedBeanValue")
        public String getValue() {
            return this.scopedBean.getValue();
        }
    }

    interface ScopedBean {
        String getValue();
    }

    static class ScopedBeanImpl implements StompWebSocketIntegrationTests.ScopedBean {
        private final String value;

        public ScopedBeanImpl(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return this.value;
        }
    }

    private static class TestClientWebSocketHandler extends TextWebSocketHandler {
        private final TextMessage[] messagesToSend;

        private final int expected;

        private final List<TextMessage> actual = new CopyOnWriteArrayList<>();

        private final CountDownLatch latch;

        public TestClientWebSocketHandler(int expectedNumberOfMessages, TextMessage... messagesToSend) {
            this.messagesToSend = messagesToSend;
            this.expected = expectedNumberOfMessages;
            this.latch = new CountDownLatch(this.expected);
        }

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            for (TextMessage message : this.messagesToSend) {
                session.sendMessage(message);
            }
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            this.actual.add(message);
            this.latch.countDown();
        }
    }

    @Configuration
    @ComponentScan(basePackageClasses = StompWebSocketIntegrationTests.class, useDefaultFilters = false, includeFilters = @ComponentScan.Filter(StompWebSocketIntegrationTests.IntegrationTestController.class))
    static class TestMessageBrokerConfigurer implements WebSocketMessageBrokerConfigurer {
        @Autowired
        private HandshakeHandler handshakeHandler;// can't rely on classpath for server detection


        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/ws").setHandshakeHandler(this.handshakeHandler);
        }

        @Override
        public void configureMessageBroker(MessageBrokerRegistry configurer) {
            configurer.setApplicationDestinationPrefixes("/app");
            configurer.enableSimpleBroker("/topic", "/queue");
        }

        @Bean
        @Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.INTERFACES)
        public StompWebSocketIntegrationTests.ScopedBean scopedBean() {
            return new StompWebSocketIntegrationTests.ScopedBeanImpl("55");
        }
    }

    @Configuration
    static class TestMessageBrokerConfiguration extends DelegatingWebSocketMessageBrokerConfiguration {
        @Override
        @Bean
        public AbstractSubscribableChannel clientInboundChannel() {
            return new ExecutorSubscribableChannel();// synchronous

        }

        @Override
        @Bean
        public AbstractSubscribableChannel clientOutboundChannel() {
            return new ExecutorSubscribableChannel();// synchronous

        }
    }
}

