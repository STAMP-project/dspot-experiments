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
package org.springframework.web.socket.messaging;


import CloseStatus.BAD_DATA;
import MimeTypeUtils.APPLICATION_OCTET_STREAM;
import SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER;
import SimpMessageHeaderAccessor.DISCONNECT_MESSAGE_HEADER;
import SimpMessageHeaderAccessor.HEART_BEAT_HEADER;
import SimpMessageType.CONNECT_ACK;
import SimpMessageType.DISCONNECT_ACK;
import SimpMessageType.HEARTBEAT;
import StompCommand.CONNECT;
import StompCommand.CONNECTED;
import StompCommand.DISCONNECT;
import StompCommand.MESSAGE;
import StompCommand.SUBSCRIBE;
import StompCommand.UNSUBSCRIBE;
import StompHeaderAccessor.ORIGINAL_DESTINATION;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpAttributes;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.TestPrincipal;
import org.springframework.messaging.simp.stomp.StompEncoder;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.DestinationUserNameProvider;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.messaging.support.ImmutableMessageChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.handler.TestWebSocketSession;
import org.springframework.web.socket.sockjs.transport.SockJsSession;


/**
 * Test fixture for {@link StompSubProtocolHandler} tests.
 *
 * @author Rossen Stoyanchev
 */
public class StompSubProtocolHandlerTests {
    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    private StompSubProtocolHandler protocolHandler;

    private TestWebSocketSession session;

    private MessageChannel channel;

    @SuppressWarnings("rawtypes")
    private ArgumentCaptor<Message> messageCaptor;

    @Test
    public void handleMessageToClientWithConnectedFrame() {
        StompHeaderAccessor headers = StompHeaderAccessor.create(CONNECTED);
        Message<byte[]> message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, headers.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, message);
        Assert.assertEquals(1, this.session.getSentMessages().size());
        WebSocketMessage<?> textMessage = this.session.getSentMessages().get(0);
        Assert.assertEquals(("CONNECTED\n" + (("user-name:joe\n" + "\n") + "\u0000")), textMessage.getPayload());
    }

    @Test
    public void handleMessageToClientWithDestinationUserNameProvider() {
        this.session.setPrincipal(new StompSubProtocolHandlerTests.UniqueUser("joe"));
        StompHeaderAccessor headers = StompHeaderAccessor.create(CONNECTED);
        Message<byte[]> message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, headers.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, message);
        Assert.assertEquals(1, this.session.getSentMessages().size());
        WebSocketMessage<?> textMessage = this.session.getSentMessages().get(0);
        Assert.assertEquals(("CONNECTED\n" + (("user-name:joe\n" + "\n") + "\u0000")), textMessage.getPayload());
    }

    @Test
    public void handleMessageToClientWithSimpConnectAck() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(CONNECT);
        accessor.setHeartbeat(10000, 10000);
        accessor.setAcceptVersion("1.0,1.1,1.2");
        Message<?> connectMessage = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, accessor.getMessageHeaders());
        SimpMessageHeaderAccessor ackAccessor = SimpMessageHeaderAccessor.create(CONNECT_ACK);
        ackAccessor.setHeader(CONNECT_MESSAGE_HEADER, connectMessage);
        ackAccessor.setHeader(HEART_BEAT_HEADER, new long[]{ 15000, 15000 });
        Message<byte[]> ackMessage = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, ackAccessor.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, ackMessage);
        Assert.assertEquals(1, this.session.getSentMessages().size());
        TextMessage actual = ((TextMessage) (this.session.getSentMessages().get(0)));
        Assert.assertEquals(("CONNECTED\n" + (((("version:1.2\n" + "heart-beat:15000,15000\n") + "user-name:joe\n") + "\n") + "\u0000")), actual.getPayload());
    }

    @Test
    public void handleMessageToClientWithSimpConnectAckDefaultHeartBeat() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(CONNECT);
        accessor.setHeartbeat(10000, 10000);
        accessor.setAcceptVersion("1.0");
        Message<?> connectMessage = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, accessor.getMessageHeaders());
        SimpMessageHeaderAccessor ackAccessor = SimpMessageHeaderAccessor.create(CONNECT_ACK);
        ackAccessor.setHeader(CONNECT_MESSAGE_HEADER, connectMessage);
        Message<byte[]> ackMessage = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, ackAccessor.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, ackMessage);
        Assert.assertEquals(1, this.session.getSentMessages().size());
        TextMessage actual = ((TextMessage) (this.session.getSentMessages().get(0)));
        Assert.assertEquals(("CONNECTED\n" + (((("version:1.0\n" + "heart-beat:0,0\n") + "user-name:joe\n") + "\n") + "\u0000")), actual.getPayload());
    }

    @Test
    public void handleMessageToClientWithSimpDisconnectAck() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(DISCONNECT);
        Message<?> connectMessage = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, accessor.getMessageHeaders());
        SimpMessageHeaderAccessor ackAccessor = SimpMessageHeaderAccessor.create(DISCONNECT_ACK);
        ackAccessor.setHeader(DISCONNECT_MESSAGE_HEADER, connectMessage);
        Message<byte[]> ackMessage = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, ackAccessor.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, ackMessage);
        Assert.assertEquals(1, this.session.getSentMessages().size());
        TextMessage actual = ((TextMessage) (this.session.getSentMessages().get(0)));
        Assert.assertEquals(("ERROR\n" + (("message:Session closed.\n" + "content-length:0\n") + "\n\u0000")), actual.getPayload());
    }

    @Test
    public void handleMessageToClientWithSimpDisconnectAckAndReceipt() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(DISCONNECT);
        accessor.setReceipt("message-123");
        Message<?> connectMessage = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, accessor.getMessageHeaders());
        SimpMessageHeaderAccessor ackAccessor = SimpMessageHeaderAccessor.create(DISCONNECT_ACK);
        ackAccessor.setHeader(DISCONNECT_MESSAGE_HEADER, connectMessage);
        Message<byte[]> ackMessage = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, ackAccessor.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, ackMessage);
        Assert.assertEquals(1, this.session.getSentMessages().size());
        TextMessage actual = ((TextMessage) (this.session.getSentMessages().get(0)));
        Assert.assertEquals(("RECEIPT\n" + ("receipt-id:message-123\n" + "\n\u0000")), actual.getPayload());
    }

    @Test
    public void handleMessageToClientWithSimpHeartbeat() {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(HEARTBEAT);
        accessor.setSessionId("s1");
        accessor.setUser(new TestPrincipal("joe"));
        Message<byte[]> ackMessage = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, accessor.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, ackMessage);
        Assert.assertEquals(1, this.session.getSentMessages().size());
        TextMessage actual = ((TextMessage) (this.session.getSentMessages().get(0)));
        Assert.assertEquals("\n", actual.getPayload());
    }

    @Test
    public void handleMessageToClientWithHeartbeatSuppressingSockJsHeartbeat() throws IOException {
        SockJsSession sockJsSession = Mockito.mock(SockJsSession.class);
        Mockito.when(sockJsSession.getId()).thenReturn("s1");
        StompHeaderAccessor accessor = StompHeaderAccessor.create(CONNECTED);
        accessor.setHeartbeat(0, 10);
        Message<byte[]> message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, accessor.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(sockJsSession, message);
        Mockito.verify(sockJsSession).getId();
        Mockito.verify(sockJsSession).getPrincipal();
        Mockito.verify(sockJsSession).disableHeartbeat();
        Mockito.verify(sockJsSession).sendMessage(ArgumentMatchers.any(WebSocketMessage.class));
        Mockito.verifyNoMoreInteractions(sockJsSession);
        sockJsSession = Mockito.mock(SockJsSession.class);
        Mockito.when(sockJsSession.getId()).thenReturn("s1");
        accessor = StompHeaderAccessor.create(CONNECTED);
        accessor.setHeartbeat(0, 0);
        message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, accessor.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(sockJsSession, message);
        Mockito.verify(sockJsSession).getId();
        Mockito.verify(sockJsSession).getPrincipal();
        Mockito.verify(sockJsSession).sendMessage(ArgumentMatchers.any(WebSocketMessage.class));
        Mockito.verifyNoMoreInteractions(sockJsSession);
    }

    @Test
    public void handleMessageToClientWithUserDestination() {
        StompHeaderAccessor headers = StompHeaderAccessor.create(MESSAGE);
        headers.setMessageId("mess0");
        headers.setSubscriptionId("sub0");
        headers.setDestination("/queue/foo-user123");
        headers.setNativeHeader(ORIGINAL_DESTINATION, "/user/queue/foo");
        Message<byte[]> message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, headers.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, message);
        Assert.assertEquals(1, this.session.getSentMessages().size());
        WebSocketMessage<?> textMessage = this.session.getSentMessages().get(0);
        Assert.assertTrue(((String) (textMessage.getPayload())).contains("destination:/user/queue/foo\n"));
        Assert.assertFalse(((String) (textMessage.getPayload())).contains(SimpMessageHeaderAccessor.ORIGINAL_DESTINATION));
    }

    // SPR-12475
    @Test
    public void handleMessageToClientWithBinaryWebSocketMessage() {
        StompHeaderAccessor headers = StompHeaderAccessor.create(MESSAGE);
        headers.setMessageId("mess0");
        headers.setSubscriptionId("sub0");
        headers.setContentType(APPLICATION_OCTET_STREAM);
        headers.setDestination("/queue/foo");
        // Non-empty payload
        byte[] payload = new byte[1];
        Message<byte[]> message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, message);
        Assert.assertEquals(1, this.session.getSentMessages().size());
        WebSocketMessage<?> webSocketMessage = this.session.getSentMessages().get(0);
        Assert.assertTrue((webSocketMessage instanceof BinaryMessage));
        // Empty payload
        payload = StompSubProtocolHandlerTests.EMPTY_PAYLOAD;
        message = MessageBuilder.createMessage(payload, headers.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, message);
        Assert.assertEquals(2, this.session.getSentMessages().size());
        webSocketMessage = this.session.getSentMessages().get(1);
        Assert.assertTrue((webSocketMessage instanceof TextMessage));
    }

    @Test
    public void handleMessageFromClient() {
        TextMessage textMessage = StompTextMessageBuilder.create(CONNECT).headers("login:guest", "passcode:guest", "accept-version:1.1,1.0", "heart-beat:10000,10000").build();
        this.protocolHandler.afterSessionStarted(this.session, this.channel);
        this.protocolHandler.handleMessageFromClient(this.session, textMessage, this.channel);
        Mockito.verify(this.channel).send(this.messageCaptor.capture());
        Message<?> actual = this.messageCaptor.getValue();
        Assert.assertNotNull(actual);
        Assert.assertEquals("s1", SimpMessageHeaderAccessor.getSessionId(actual.getHeaders()));
        Assert.assertNotNull(SimpMessageHeaderAccessor.getSessionAttributes(actual.getHeaders()));
        Assert.assertNotNull(SimpMessageHeaderAccessor.getUser(actual.getHeaders()));
        Assert.assertEquals("joe", SimpMessageHeaderAccessor.getUser(actual.getHeaders()).getName());
        Assert.assertNotNull(SimpMessageHeaderAccessor.getHeartbeat(actual.getHeaders()));
        Assert.assertArrayEquals(new long[]{ 10000, 10000 }, SimpMessageHeaderAccessor.getHeartbeat(actual.getHeaders()));
        StompHeaderAccessor stompAccessor = StompHeaderAccessor.wrap(actual);
        Assert.assertEquals(CONNECT, stompAccessor.getCommand());
        Assert.assertEquals("guest", stompAccessor.getLogin());
        Assert.assertEquals("guest", stompAccessor.getPasscode());
        Assert.assertArrayEquals(new long[]{ 10000, 10000 }, stompAccessor.getHeartbeat());
        Assert.assertEquals(new HashSet(Arrays.asList("1.1", "1.0")), stompAccessor.getAcceptVersion());
        Assert.assertEquals(0, this.session.getSentMessages().size());
    }

    @Test
    public void handleMessageFromClientWithImmutableMessageInterceptor() {
        AtomicReference<Boolean> mutable = new AtomicReference<>();
        ExecutorSubscribableChannel channel = new ExecutorSubscribableChannel();
        channel.addInterceptor(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                mutable.set(MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class).isMutable());
                return message;
            }
        });
        channel.addInterceptor(new ImmutableMessageChannelInterceptor());
        StompSubProtocolHandler handler = new StompSubProtocolHandler();
        handler.afterSessionStarted(this.session, channel);
        TextMessage message = StompTextMessageBuilder.create(CONNECT).build();
        handler.handleMessageFromClient(this.session, message, channel);
        Assert.assertNotNull(mutable.get());
        Assert.assertTrue(mutable.get());
    }

    @Test
    public void handleMessageFromClientWithoutImmutableMessageInterceptor() {
        AtomicReference<Boolean> mutable = new AtomicReference<>();
        ExecutorSubscribableChannel channel = new ExecutorSubscribableChannel();
        channel.addInterceptor(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                mutable.set(MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class).isMutable());
                return message;
            }
        });
        StompSubProtocolHandler handler = new StompSubProtocolHandler();
        handler.afterSessionStarted(this.session, channel);
        TextMessage message = StompTextMessageBuilder.create(CONNECT).build();
        handler.handleMessageFromClient(this.session, message, channel);
        Assert.assertNotNull(mutable.get());
        Assert.assertFalse(mutable.get());
    }

    // SPR-14690
    @Test
    public void handleMessageFromClientWithTokenAuthentication() {
        ExecutorSubscribableChannel channel = new ExecutorSubscribableChannel();
        channel.addInterceptor(new StompSubProtocolHandlerTests.AuthenticationInterceptor("__pete__@gmail.com"));
        channel.addInterceptor(new ImmutableMessageChannelInterceptor());
        StompSubProtocolHandlerTests.TestMessageHandler messageHandler = new StompSubProtocolHandlerTests.TestMessageHandler();
        channel.subscribe(messageHandler);
        StompSubProtocolHandler handler = new StompSubProtocolHandler();
        handler.afterSessionStarted(this.session, channel);
        TextMessage wsMessage = StompTextMessageBuilder.create(CONNECT).build();
        handler.handleMessageFromClient(this.session, wsMessage, channel);
        Assert.assertEquals(1, messageHandler.getMessages().size());
        Message<?> message = messageHandler.getMessages().get(0);
        Principal user = SimpMessageHeaderAccessor.getUser(message.getHeaders());
        Assert.assertNotNull(user);
        Assert.assertEquals("__pete__@gmail.com", user.getName());
    }

    @Test
    public void handleMessageFromClientWithInvalidStompCommand() {
        TextMessage textMessage = new TextMessage("FOO\n\n\u0000");
        this.protocolHandler.afterSessionStarted(this.session, this.channel);
        this.protocolHandler.handleMessageFromClient(this.session, textMessage, this.channel);
        Mockito.verifyZeroInteractions(this.channel);
        Assert.assertEquals(1, this.session.getSentMessages().size());
        TextMessage actual = ((TextMessage) (this.session.getSentMessages().get(0)));
        Assert.assertTrue(actual.getPayload().startsWith("ERROR"));
    }

    @Test
    public void eventPublication() {
        StompSubProtocolHandlerTests.TestPublisher publisher = new StompSubProtocolHandlerTests.TestPublisher();
        this.protocolHandler.setApplicationEventPublisher(publisher);
        this.protocolHandler.afterSessionStarted(this.session, this.channel);
        StompHeaderAccessor headers = StompHeaderAccessor.create(CONNECT);
        Message<byte[]> message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, headers.getMessageHeaders());
        TextMessage textMessage = new TextMessage(new StompEncoder().encode(message));
        this.protocolHandler.handleMessageFromClient(this.session, textMessage, this.channel);
        headers = StompHeaderAccessor.create(CONNECTED);
        message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, headers.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, message);
        headers = StompHeaderAccessor.create(SUBSCRIBE);
        message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, headers.getMessageHeaders());
        textMessage = new TextMessage(new StompEncoder().encode(message));
        this.protocolHandler.handleMessageFromClient(this.session, textMessage, this.channel);
        headers = StompHeaderAccessor.create(UNSUBSCRIBE);
        message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, headers.getMessageHeaders());
        textMessage = new TextMessage(new StompEncoder().encode(message));
        this.protocolHandler.handleMessageFromClient(this.session, textMessage, this.channel);
        this.protocolHandler.afterSessionEnded(this.session, BAD_DATA, this.channel);
        Assert.assertEquals(("Unexpected events " + (publisher.events)), 5, publisher.events.size());
        Assert.assertEquals(SessionConnectEvent.class, publisher.events.get(0).getClass());
        Assert.assertEquals(SessionConnectedEvent.class, publisher.events.get(1).getClass());
        Assert.assertEquals(SessionSubscribeEvent.class, publisher.events.get(2).getClass());
        Assert.assertEquals(SessionUnsubscribeEvent.class, publisher.events.get(3).getClass());
        Assert.assertEquals(SessionDisconnectEvent.class, publisher.events.get(4).getClass());
    }

    @Test
    public void eventPublicationWithExceptions() {
        ApplicationEventPublisher publisher = Mockito.mock(ApplicationEventPublisher.class);
        this.protocolHandler.setApplicationEventPublisher(publisher);
        this.protocolHandler.afterSessionStarted(this.session, this.channel);
        StompHeaderAccessor headers = StompHeaderAccessor.create(CONNECT);
        Message<byte[]> message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, headers.getMessageHeaders());
        TextMessage textMessage = new TextMessage(new StompEncoder().encode(message));
        this.protocolHandler.handleMessageFromClient(this.session, textMessage, this.channel);
        Mockito.verify(this.channel).send(this.messageCaptor.capture());
        Message<?> actual = this.messageCaptor.getValue();
        Assert.assertNotNull(actual);
        Assert.assertEquals(CONNECT, StompHeaderAccessor.wrap(actual).getCommand());
        Mockito.reset(this.channel);
        headers = StompHeaderAccessor.create(CONNECTED);
        message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, headers.getMessageHeaders());
        this.protocolHandler.handleMessageToClient(this.session, message);
        Assert.assertEquals(1, this.session.getSentMessages().size());
        textMessage = ((TextMessage) (this.session.getSentMessages().get(0)));
        Assert.assertEquals(("CONNECTED\n" + (("user-name:joe\n" + "\n") + "\u0000")), textMessage.getPayload());
        this.protocolHandler.afterSessionEnded(this.session, BAD_DATA, this.channel);
        Mockito.verify(this.channel).send(this.messageCaptor.capture());
        actual = this.messageCaptor.getValue();
        Assert.assertNotNull(actual);
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(actual);
        Assert.assertEquals(DISCONNECT, accessor.getCommand());
        Assert.assertEquals("s1", accessor.getSessionId());
        Assert.assertEquals("joe", accessor.getUser().getName());
    }

    @Test
    public void webSocketScope() {
        Runnable runnable = Mockito.mock(Runnable.class);
        SimpAttributes simpAttributes = new SimpAttributes(this.session.getId(), this.session.getAttributes());
        simpAttributes.setAttribute("name", "value");
        simpAttributes.registerDestructionCallback("name", runnable);
        MessageChannel testChannel = new MessageChannel() {
            @Override
            public boolean send(Message<?> message) {
                SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
                Assert.assertThat(simpAttributes.getAttribute("name"), is("value"));
                return true;
            }

            @Override
            public boolean send(Message<?> message, long timeout) {
                return false;
            }
        };
        this.protocolHandler.afterSessionStarted(this.session, this.channel);
        StompHeaderAccessor headers = StompHeaderAccessor.create(CONNECT);
        Message<byte[]> message = MessageBuilder.createMessage(StompSubProtocolHandlerTests.EMPTY_PAYLOAD, headers.getMessageHeaders());
        TextMessage textMessage = new TextMessage(new StompEncoder().encode(message));
        this.protocolHandler.handleMessageFromClient(this.session, textMessage, testChannel);
        Assert.assertEquals(Collections.<WebSocketMessage<?>>emptyList(), session.getSentMessages());
        this.protocolHandler.afterSessionEnded(this.session, BAD_DATA, testChannel);
        Assert.assertEquals(Collections.<WebSocketMessage<?>>emptyList(), this.session.getSentMessages());
        Mockito.verify(runnable, Mockito.times(1)).run();
    }

    private static class UniqueUser extends TestPrincipal implements DestinationUserNameProvider {
        private UniqueUser(String name) {
            super(name);
        }

        @Override
        public String getDestinationUserName() {
            return "Me myself and I";
        }
    }

    private static class TestPublisher implements ApplicationEventPublisher {
        private final List<ApplicationEvent> events = new ArrayList<>();

        @Override
        public void publishEvent(ApplicationEvent event) {
            events.add(event);
        }

        @Override
        public void publishEvent(Object event) {
            publishEvent(new org.springframework.context.PayloadApplicationEvent(this, event));
        }
    }

    private static class TestMessageHandler implements MessageHandler {
        private final List<Message<?>> messages = new ArrayList<>();

        public List<Message<?>> getMessages() {
            return this.messages;
        }

        @Override
        public void handleMessage(Message<?> message) throws MessagingException {
            this.messages.add(message);
        }
    }

    private static class AuthenticationInterceptor implements ChannelInterceptor {
        private final String name;

        public AuthenticationInterceptor(String name) {
            this.name = name;
        }

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            TestPrincipal user = new TestPrincipal(name);
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class).setUser(user);
            return message;
        }
    }
}

