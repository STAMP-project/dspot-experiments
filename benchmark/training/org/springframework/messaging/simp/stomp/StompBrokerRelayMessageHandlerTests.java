/**
 * Copyright 2002-2015 the original author or authors.
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
import StompBrokerRelayMessageHandler.SYSTEM_SESSION_ID;
import StompCommand.CONNECT;
import StompCommand.CONNECTED;
import StompCommand.ERROR;
import StompCommand.SEND;
import StompCommand.SUBSCRIBE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.StubMessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.tcp.ReconnectStrategy;
import org.springframework.messaging.tcp.TcpConnection;
import org.springframework.messaging.tcp.TcpConnectionHandler;
import org.springframework.messaging.tcp.TcpOperations;
import org.springframework.util.concurrent.ListenableFuture;


/**
 * Unit tests for StompBrokerRelayMessageHandler.
 *
 * @author Rossen Stoyanchev
 */
public class StompBrokerRelayMessageHandlerTests {
    private StompBrokerRelayMessageHandler brokerRelay;

    private StubMessageChannel outboundChannel;

    private StompBrokerRelayMessageHandlerTests.StubTcpOperations tcpClient;

    @Test
    public void virtualHost() throws Exception {
        this.brokerRelay.setVirtualHost("ABC");
        this.brokerRelay.start();
        this.brokerRelay.handleMessage(connectMessage("sess1", "joe"));
        Assert.assertEquals(2, this.tcpClient.getSentMessages().size());
        StompHeaderAccessor headers1 = this.tcpClient.getSentHeaders(0);
        Assert.assertEquals(CONNECT, headers1.getCommand());
        Assert.assertEquals(SYSTEM_SESSION_ID, headers1.getSessionId());
        Assert.assertEquals("ABC", headers1.getHost());
        StompHeaderAccessor headers2 = this.tcpClient.getSentHeaders(1);
        Assert.assertEquals(CONNECT, headers2.getCommand());
        Assert.assertEquals("sess1", headers2.getSessionId());
        Assert.assertEquals("ABC", headers2.getHost());
    }

    @Test
    public void loginAndPasscode() throws Exception {
        this.brokerRelay.setSystemLogin("syslogin");
        this.brokerRelay.setSystemPasscode("syspasscode");
        this.brokerRelay.setClientLogin("clientlogin");
        this.brokerRelay.setClientPasscode("clientpasscode");
        this.brokerRelay.start();
        this.brokerRelay.handleMessage(connectMessage("sess1", "joe"));
        Assert.assertEquals(2, this.tcpClient.getSentMessages().size());
        StompHeaderAccessor headers1 = this.tcpClient.getSentHeaders(0);
        Assert.assertEquals(CONNECT, headers1.getCommand());
        Assert.assertEquals("syslogin", headers1.getLogin());
        Assert.assertEquals("syspasscode", headers1.getPasscode());
        StompHeaderAccessor headers2 = this.tcpClient.getSentHeaders(1);
        Assert.assertEquals(CONNECT, headers2.getCommand());
        Assert.assertEquals("clientlogin", headers2.getLogin());
        Assert.assertEquals("clientpasscode", headers2.getPasscode());
    }

    @Test
    public void destinationExcluded() throws Exception {
        this.brokerRelay.start();
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(MESSAGE);
        headers.setSessionId("sess1");
        headers.setDestination("/user/daisy/foo");
        this.brokerRelay.handleMessage(MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders()));
        Assert.assertEquals(1, this.tcpClient.getSentMessages().size());
        StompHeaderAccessor headers1 = this.tcpClient.getSentHeaders(0);
        Assert.assertEquals(CONNECT, headers1.getCommand());
        Assert.assertEquals(SYSTEM_SESSION_ID, headers1.getSessionId());
    }

    @Test
    public void messageFromBrokerIsEnriched() throws Exception {
        this.brokerRelay.start();
        this.brokerRelay.handleMessage(connectMessage("sess1", "joe"));
        Assert.assertEquals(2, this.tcpClient.getSentMessages().size());
        Assert.assertEquals(CONNECT, this.tcpClient.getSentHeaders(0).getCommand());
        Assert.assertEquals(CONNECT, this.tcpClient.getSentHeaders(1).getCommand());
        this.tcpClient.handleMessage(message(StompCommand.MESSAGE, null, null, null));
        Message<byte[]> message = this.outboundChannel.getMessages().get(0);
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals("sess1", accessor.getSessionId());
        Assert.assertEquals("joe", accessor.getUser().getName());
    }

    // SPR-12820
    @Test
    public void connectWhenBrokerNotAvailable() throws Exception {
        this.brokerRelay.start();
        this.brokerRelay.stopInternal();
        this.brokerRelay.handleMessage(connectMessage("sess1", "joe"));
        Message<byte[]> message = this.outboundChannel.getMessages().get(0);
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals(ERROR, accessor.getCommand());
        Assert.assertEquals("sess1", accessor.getSessionId());
        Assert.assertEquals("joe", accessor.getUser().getName());
        Assert.assertEquals("Broker not available.", accessor.getMessage());
    }

    @Test
    public void sendAfterBrokerUnavailable() throws Exception {
        this.brokerRelay.start();
        Assert.assertEquals(1, this.brokerRelay.getConnectionCount());
        this.brokerRelay.handleMessage(connectMessage("sess1", "joe"));
        Assert.assertEquals(2, this.brokerRelay.getConnectionCount());
        this.brokerRelay.stopInternal();
        this.brokerRelay.handleMessage(message(SEND, "sess1", "joe", "/foo"));
        Assert.assertEquals(1, this.brokerRelay.getConnectionCount());
        Message<byte[]> message = this.outboundChannel.getMessages().get(0);
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals(ERROR, accessor.getCommand());
        Assert.assertEquals("sess1", accessor.getSessionId());
        Assert.assertEquals("joe", accessor.getUser().getName());
        Assert.assertEquals("Broker not available.", accessor.getMessage());
    }

    @Test
    public void systemSubscription() throws Exception {
        MessageHandler handler = Mockito.mock(MessageHandler.class);
        this.brokerRelay.setSystemSubscriptions(Collections.singletonMap("/topic/foo", handler));
        this.brokerRelay.start();
        StompHeaderAccessor accessor = StompHeaderAccessor.create(CONNECTED);
        accessor.setLeaveMutable(true);
        MessageHeaders headers = accessor.getMessageHeaders();
        this.tcpClient.handleMessage(MessageBuilder.createMessage(new byte[0], headers));
        Assert.assertEquals(2, this.tcpClient.getSentMessages().size());
        Assert.assertEquals(CONNECT, this.tcpClient.getSentHeaders(0).getCommand());
        Assert.assertEquals(SUBSCRIBE, this.tcpClient.getSentHeaders(1).getCommand());
        Assert.assertEquals("/topic/foo", this.tcpClient.getSentHeaders(1).getDestination());
        Message<byte[]> message = message(StompCommand.MESSAGE, null, null, "/topic/foo");
        this.tcpClient.handleMessage(message);
        ArgumentCaptor<Message> captor = ArgumentCaptor.forClass(Message.class);
        Mockito.verify(handler).handleMessage(captor.capture());
        Assert.assertSame(message, captor.getValue());
    }

    private static class StubTcpOperations implements TcpOperations<byte[]> {
        private StompBrokerRelayMessageHandlerTests.StubTcpConnection connection = new StompBrokerRelayMessageHandlerTests.StubTcpConnection();

        private TcpConnectionHandler<byte[]> connectionHandler;

        public List<Message<byte[]>> getSentMessages() {
            return this.connection.getMessages();
        }

        public StompHeaderAccessor getSentHeaders(int index) {
            Assert.assertTrue(("Size: " + (getSentMessages().size())), ((getSentMessages().size()) > index));
            Message<byte[]> message = getSentMessages().get(index);
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            Assert.assertNotNull(accessor);
            return accessor;
        }

        @Override
        public ListenableFuture<Void> connect(TcpConnectionHandler<byte[]> handler) {
            this.connectionHandler = handler;
            handler.afterConnected(this.connection);
            return StompBrokerRelayMessageHandlerTests.getVoidFuture();
        }

        @Override
        public ListenableFuture<Void> connect(TcpConnectionHandler<byte[]> handler, ReconnectStrategy strategy) {
            this.connectionHandler = handler;
            handler.afterConnected(this.connection);
            return StompBrokerRelayMessageHandlerTests.getVoidFuture();
        }

        @Override
        public ListenableFuture<Void> shutdown() {
            return StompBrokerRelayMessageHandlerTests.getVoidFuture();
        }

        public void handleMessage(Message<byte[]> message) {
            this.connectionHandler.handleMessage(message);
        }
    }

    private static class StubTcpConnection implements TcpConnection<byte[]> {
        private final List<Message<byte[]>> messages = new ArrayList<>();

        public List<Message<byte[]>> getMessages() {
            return this.messages;
        }

        @Override
        public ListenableFuture<Void> send(Message<byte[]> message) {
            this.messages.add(message);
            return StompBrokerRelayMessageHandlerTests.getVoidFuture();
        }

        @Override
        public void onReadInactivity(Runnable runnable, long duration) {
        }

        @Override
        public void onWriteInactivity(Runnable runnable, long duration) {
        }

        @Override
        public void close() {
        }
    }
}

