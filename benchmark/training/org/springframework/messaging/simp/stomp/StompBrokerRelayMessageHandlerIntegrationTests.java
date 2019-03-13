/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.messaging.simp.stomp;


import SimpMessageType.MESSAGE;
import StompCommand.CONNECT;
import StompCommand.CONNECTED;
import StompCommand.DISCONNECT;
import StompCommand.RECEIPT;
import StompCommand.SEND;
import StompCommand.SUBSCRIBE;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.broker.BrokerAvailabilityEvent;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;

import static StompCommand.ERROR;


/**
 * Integration tests for {@link StompBrokerRelayMessageHandler} running against ActiveMQ.
 *
 * @author Rossen Stoyanchev
 */
public class StompBrokerRelayMessageHandlerIntegrationTests {
    @Rule
    public final TestName testName = new TestName();

    private static final Log logger = LogFactory.getLog(StompBrokerRelayMessageHandlerIntegrationTests.class);

    private StompBrokerRelayMessageHandler relay;

    private BrokerService activeMQBroker;

    private ExecutorSubscribableChannel responseChannel;

    private StompBrokerRelayMessageHandlerIntegrationTests.TestMessageHandler responseHandler;

    private StompBrokerRelayMessageHandlerIntegrationTests.TestEventPublisher eventPublisher;

    private int port;

    @Test
    public void publishSubscribe() throws Exception {
        StompBrokerRelayMessageHandlerIntegrationTests.logger.debug("Starting test publishSubscribe()");
        String sess1 = "sess1";
        String sess2 = "sess2";
        String subs1 = "subs1";
        String destination = "/topic/test";
        StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange conn1 = StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder.connect(sess1).build();
        StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange conn2 = StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder.connect(sess2).build();
        this.relay.handleMessage(conn1.message);
        this.relay.handleMessage(conn2.message);
        this.responseHandler.expectMessages(conn1, conn2);
        StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange subscribe = StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder.subscribeWithReceipt(sess1, subs1, destination, "r1").build();
        this.relay.handleMessage(subscribe.message);
        this.responseHandler.expectMessages(subscribe);
        StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange send = StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder.send(destination, "foo").andExpectMessage(sess1, subs1).build();
        this.relay.handleMessage(send.message);
        this.responseHandler.expectMessages(send);
    }

    @Test(expected = MessageDeliveryException.class)
    public void messageDeliveryExceptionIfSystemSessionForwardFails() throws Exception {
        StompBrokerRelayMessageHandlerIntegrationTests.logger.debug("Starting test messageDeliveryExceptionIfSystemSessionForwardFails()");
        stopActiveMqBrokerAndAwait();
        this.eventPublisher.expectBrokerAvailabilityEvent(false);
        StompHeaderAccessor headers = StompHeaderAccessor.create(SEND);
        this.relay.handleMessage(MessageBuilder.createMessage("test".getBytes(), headers.getMessageHeaders()));
    }

    @Test
    public void brokerBecomingUnavailableTriggersErrorFrame() throws Exception {
        StompBrokerRelayMessageHandlerIntegrationTests.logger.debug("Starting test brokerBecomingUnavailableTriggersErrorFrame()");
        String sess1 = "sess1";
        StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange connect = StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder.connect(sess1).build();
        this.relay.handleMessage(connect.message);
        this.responseHandler.expectMessages(connect);
        StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange error = StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder.error(sess1).build();
        stopActiveMqBrokerAndAwait();
        this.eventPublisher.expectBrokerAvailabilityEvent(false);
        this.responseHandler.expectMessages(error);
    }

    @Test
    public void brokerAvailabilityEventWhenStopped() throws Exception {
        StompBrokerRelayMessageHandlerIntegrationTests.logger.debug("Starting test brokerAvailabilityEventWhenStopped()");
        stopActiveMqBrokerAndAwait();
        this.eventPublisher.expectBrokerAvailabilityEvent(false);
    }

    @Test
    public void relayReconnectsIfBrokerComesBackUp() throws Exception {
        StompBrokerRelayMessageHandlerIntegrationTests.logger.debug("Starting test relayReconnectsIfBrokerComesBackUp()");
        String sess1 = "sess1";
        StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange conn1 = StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder.connect(sess1).build();
        this.relay.handleMessage(conn1.message);
        this.responseHandler.expectMessages(conn1);
        String subs1 = "subs1";
        String destination = "/topic/test";
        StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange subscribe = StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder.subscribeWithReceipt(sess1, subs1, destination, "r1").build();
        this.relay.handleMessage(subscribe.message);
        this.responseHandler.expectMessages(subscribe);
        StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange error = StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder.error(sess1).build();
        stopActiveMqBrokerAndAwait();
        this.responseHandler.expectMessages(error);
        this.eventPublisher.expectBrokerAvailabilityEvent(false);
        startActiveMqBroker();
        this.eventPublisher.expectBrokerAvailabilityEvent(true);
    }

    @Test
    public void disconnectWithReceipt() throws Exception {
        StompBrokerRelayMessageHandlerIntegrationTests.logger.debug("Starting test disconnectWithReceipt()");
        StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange connect = StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder.connect("sess1").build();
        this.relay.handleMessage(connect.message);
        this.responseHandler.expectMessages(connect);
        StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange disconnect = StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder.disconnectWithReceipt("sess1", "r123").build();
        this.relay.handleMessage(disconnect.message);
        this.responseHandler.expectMessages(disconnect);
    }

    private static class TestEventPublisher implements ApplicationEventPublisher {
        private final BlockingQueue<BrokerAvailabilityEvent> eventQueue = new LinkedBlockingQueue<>();

        @Override
        public void publishEvent(ApplicationEvent event) {
            publishEvent(((Object) (event)));
        }

        @Override
        public void publishEvent(Object event) {
            StompBrokerRelayMessageHandlerIntegrationTests.logger.debug(("Processing ApplicationEvent " + event));
            if (event instanceof BrokerAvailabilityEvent) {
                this.eventQueue.add(((BrokerAvailabilityEvent) (event)));
            }
        }

        public void expectBrokerAvailabilityEvent(boolean isBrokerAvailable) throws InterruptedException {
            BrokerAvailabilityEvent event = this.eventQueue.poll(20000, TimeUnit.MILLISECONDS);
            Assert.assertNotNull((("Times out waiting for BrokerAvailabilityEvent[" + isBrokerAvailable) + "]"), event);
            Assert.assertEquals(isBrokerAvailable, event.isBrokerAvailable());
        }
    }

    private static class TestMessageHandler implements MessageHandler {
        private final BlockingQueue<Message<?>> queue = new LinkedBlockingQueue<>();

        @Override
        public void handleMessage(Message<?> message) throws MessagingException {
            if ((SimpMessageType.HEARTBEAT) == (SimpMessageHeaderAccessor.getMessageType(message.getHeaders()))) {
                return;
            }
            this.queue.add(message);
        }

        public void expectMessages(StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange... messageExchanges) throws InterruptedException {
            List<StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange> expectedMessages = new ArrayList<>(Arrays.<StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange>asList(messageExchanges));
            while ((expectedMessages.size()) > 0) {
                Message<?> message = this.queue.poll(10000, TimeUnit.MILLISECONDS);
                Assert.assertNotNull((("Timed out waiting for messages, expected [" + expectedMessages) + "]"), message);
                StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange match = findMatch(expectedMessages, message);
                Assert.assertNotNull((((("Unexpected message=" + message) + ", expected [") + expectedMessages) + "]"), match);
                expectedMessages.remove(match);
            } 
        }

        private StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange findMatch(List<StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange> expectedMessages, Message<?> message) {
            for (StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange exchange : expectedMessages) {
                if (exchange.matchMessage(message)) {
                    return exchange;
                }
            }
            return null;
        }
    }

    /**
     * Holds a message as well as expected and actual messages matched against expectations.
     */
    private static class MessageExchange {
        private final Message<?> message;

        private final StompBrokerRelayMessageHandlerIntegrationTests.MessageMatcher[] expected;

        private final Message<?>[] actual;

        public MessageExchange(Message<?> message, StompBrokerRelayMessageHandlerIntegrationTests.MessageMatcher... expected) {
            this.message = message;
            this.expected = expected;
            this.actual = new Message<?>[expected.length];
        }

        public boolean matchMessage(Message<?> message) {
            for (int i = 0; i < (this.expected.length); i++) {
                if (this.expected[i].match(message)) {
                    this.actual[i] = message;
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return ((((((("Forwarded message:\n" + (this.message)) + "\n") + "Should receive back:\n") + (Arrays.toString(this.expected))) + "\n") + "Actually received:\n") + (Arrays.toString(this.actual))) + "\n";
        }
    }

    private static class MessageExchangeBuilder {
        private final Message<?> message;

        private final StompHeaderAccessor headers;

        private final List<StompBrokerRelayMessageHandlerIntegrationTests.MessageMatcher> expected = new ArrayList<>();

        public MessageExchangeBuilder(Message<?> message) {
            this.message = message;
            this.headers = StompHeaderAccessor.wrap(message);
        }

        public static StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder error(String sessionId) {
            return new StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder(null).andExpectError(sessionId);
        }

        public static StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder connect(String sessionId) {
            StompHeaderAccessor headers = StompHeaderAccessor.create(CONNECT);
            headers.setSessionId(sessionId);
            headers.setAcceptVersion("1.1,1.2");
            headers.setHeartbeat(0, 0);
            Message<?> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
            StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder builder = new StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder(message);
            builder.expected.add(new StompBrokerRelayMessageHandlerIntegrationTests.StompConnectedFrameMessageMatcher(sessionId));
            return builder;
        }

        // TODO Determine why connectWithError() is unused.
        @SuppressWarnings("unused")
        public static StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder connectWithError(String sessionId) {
            StompHeaderAccessor headers = StompHeaderAccessor.create(CONNECT);
            headers.setSessionId(sessionId);
            headers.setAcceptVersion("1.1,1.2");
            Message<?> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
            StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder builder = new StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder(message);
            return builder.andExpectError();
        }

        public static StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder subscribeWithReceipt(String sessionId, String subscriptionId, String destination, String receiptId) {
            StompHeaderAccessor headers = StompHeaderAccessor.create(SUBSCRIBE);
            headers.setSessionId(sessionId);
            headers.setSubscriptionId(subscriptionId);
            headers.setDestination(destination);
            headers.setReceipt(receiptId);
            Message<?> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
            StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder builder = new StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder(message);
            builder.expected.add(new StompBrokerRelayMessageHandlerIntegrationTests.StompReceiptFrameMessageMatcher(sessionId, receiptId));
            return builder;
        }

        public static StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder send(String destination, String payload) {
            SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(MESSAGE);
            headers.setDestination(destination);
            Message<?> message = MessageBuilder.createMessage(payload.getBytes(StandardCharsets.UTF_8), headers.getMessageHeaders());
            return new StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder(message);
        }

        public static StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder disconnectWithReceipt(String sessionId, String receiptId) {
            StompHeaderAccessor headers = StompHeaderAccessor.create(DISCONNECT);
            headers.setSessionId(sessionId);
            headers.setReceipt(receiptId);
            Message<?> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
            StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder builder = new StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder(message);
            builder.expected.add(new StompBrokerRelayMessageHandlerIntegrationTests.StompReceiptFrameMessageMatcher(sessionId, receiptId));
            return builder;
        }

        public StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder andExpectMessage(String sessionId, String subscriptionId) {
            org.springframework.util.Assert.state(MESSAGE.equals(this.headers.getMessageType()), "MESSAGE type expected");
            String destination = this.headers.getDestination();
            Object payload = this.message.getPayload();
            this.expected.add(new StompBrokerRelayMessageHandlerIntegrationTests.StompMessageFrameMessageMatcher(sessionId, subscriptionId, destination, payload));
            return this;
        }

        public StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder andExpectError() {
            String sessionId = this.headers.getSessionId();
            org.springframework.util.Assert.state((sessionId != null), "No sessionId to match the ERROR frame to");
            return andExpectError(sessionId);
        }

        public StompBrokerRelayMessageHandlerIntegrationTests.MessageExchangeBuilder andExpectError(String sessionId) {
            this.expected.add(new StompBrokerRelayMessageHandlerIntegrationTests.StompFrameMessageMatcher(ERROR, sessionId));
            return this;
        }

        public StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange build() {
            return new StompBrokerRelayMessageHandlerIntegrationTests.MessageExchange(this.message, this.expected.toArray(new StompBrokerRelayMessageHandlerIntegrationTests.MessageMatcher[this.expected.size()]));
        }
    }

    private interface MessageMatcher {
        boolean match(Message<?> message);
    }

    private static class StompFrameMessageMatcher implements StompBrokerRelayMessageHandlerIntegrationTests.MessageMatcher {
        private final StompCommand command;

        private final String sessionId;

        public StompFrameMessageMatcher(StompCommand command, String sessionId) {
            this.command = command;
            this.sessionId = sessionId;
        }

        @Override
        public final boolean match(Message<?> message) {
            StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
            if ((!(this.command.equals(headers.getCommand()))) || ((this.sessionId) != (headers.getSessionId()))) {
                return false;
            }
            return matchInternal(headers, message.getPayload());
        }

        protected boolean matchInternal(StompHeaderAccessor headers, Object payload) {
            return true;
        }

        @Override
        public String toString() {
            return ((("command=" + (this.command)) + ", session=\"") + (this.sessionId)) + "\"";
        }
    }

    private static class StompReceiptFrameMessageMatcher extends StompBrokerRelayMessageHandlerIntegrationTests.StompFrameMessageMatcher {
        private final String receiptId;

        public StompReceiptFrameMessageMatcher(String sessionId, String receipt) {
            super(RECEIPT, sessionId);
            this.receiptId = receipt;
        }

        @Override
        protected boolean matchInternal(StompHeaderAccessor headers, Object payload) {
            return this.receiptId.equals(headers.getReceiptId());
        }

        @Override
        public String toString() {
            return (((super.toString()) + ", receiptId=\"") + (this.receiptId)) + "\"";
        }
    }

    private static class StompMessageFrameMessageMatcher extends StompBrokerRelayMessageHandlerIntegrationTests.StompFrameMessageMatcher {
        private final String subscriptionId;

        private final String destination;

        private final Object payload;

        public StompMessageFrameMessageMatcher(String sessionId, String subscriptionId, String destination, Object payload) {
            super(StompCommand.MESSAGE, sessionId);
            this.subscriptionId = subscriptionId;
            this.destination = destination;
            this.payload = payload;
        }

        @Override
        protected boolean matchInternal(StompHeaderAccessor headers, Object payload) {
            if ((!(this.subscriptionId.equals(headers.getSubscriptionId()))) || (!(this.destination.equals(headers.getDestination())))) {
                return false;
            }
            if ((payload instanceof byte[]) && ((this.payload) instanceof byte[])) {
                return Arrays.equals(((byte[]) (payload)), ((byte[]) (this.payload)));
            } else {
                return this.payload.equals(payload);
            }
        }

        @Override
        public String toString() {
            return (((((((super.toString()) + ", subscriptionId=\"") + (this.subscriptionId)) + "\", destination=\"") + (this.destination)) + "\", payload=\"") + (getPayloadAsText())) + "\"";
        }

        protected String getPayloadAsText() {
            return (this.payload) instanceof byte[] ? new String(((byte[]) (this.payload)), StandardCharsets.UTF_8) : this.payload.toString();
        }
    }

    private static class StompConnectedFrameMessageMatcher extends StompBrokerRelayMessageHandlerIntegrationTests.StompFrameMessageMatcher {
        public StompConnectedFrameMessageMatcher(String sessionId) {
            super(CONNECTED, sessionId);
        }
    }
}

