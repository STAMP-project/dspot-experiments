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
package org.springframework.messaging.simp.stomp;


import MimeTypeUtils.APPLICATION_JSON;
import MimeTypeUtils.TEXT_PLAIN;
import StompCommand.ACK;
import StompCommand.CONNECT;
import StompCommand.CONNECTED;
import StompCommand.ERROR;
import StompCommand.MESSAGE;
import StompCommand.NACK;
import StompCommand.RECEIPT;
import StompCommand.SEND;
import StompCommand.SUBSCRIBE;
import StompCommand.UNSUBSCRIBE;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.simp.stomp.StompSession.Receiptable;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.tcp.TcpConnection;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.MimeType;
import org.springframework.util.concurrent.SettableListenableFuture;


/**
 * Unit tests for {@link DefaultStompSession}.
 *
 * @author Rossen Stoyanchev
 */
public class DefaultStompSessionTests {
    private DefaultStompSession session;

    @Mock
    private StompSessionHandler sessionHandler;

    private StompHeaders connectHeaders;

    @Mock
    private TcpConnection<byte[]> connection;

    @Captor
    private ArgumentCaptor<Message<byte[]>> messageCaptor;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void afterConnected() {
        Assert.assertFalse(this.session.isConnected());
        this.connectHeaders.setHost("my-host");
        this.connectHeaders.setHeartbeat(new long[]{ 11, 12 });
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        Message<byte[]> message = this.messageCaptor.getValue();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals(CONNECT, accessor.getCommand());
        Assert.assertEquals("my-host", accessor.getHost());
        Assert.assertThat(accessor.getAcceptVersion(), containsInAnyOrder("1.1", "1.2"));
        Assert.assertArrayEquals(new long[]{ 11, 12 }, accessor.getHeartbeat());
    }

    // SPR-16844
    @Test
    public void afterConnectedWithSpecificVersion() {
        Assert.assertFalse(this.session.isConnected());
        this.connectHeaders.setAcceptVersion(new String[]{ "1.1" });
        this.session.afterConnected(this.connection);
        Message<byte[]> message = this.messageCaptor.getValue();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals(CONNECT, accessor.getCommand());
        Assert.assertThat(accessor.getAcceptVersion(), containsInAnyOrder("1.1"));
    }

    @Test
    public void afterConnectFailure() {
        IllegalStateException exception = new IllegalStateException("simulated exception");
        this.session.afterConnectFailure(exception);
        Mockito.verify(this.sessionHandler).handleTransportError(this.session, exception);
        Mockito.verifyNoMoreInteractions(this.sessionHandler);
    }

    @Test
    public void handleConnectedFrame() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        this.connectHeaders.setHeartbeat(new long[]{ 10000, 10000 });
        StompHeaderAccessor accessor = StompHeaderAccessor.create(CONNECTED);
        accessor.setVersion("1.2");
        accessor.setHeartbeat(10000, 10000);
        accessor.setLeaveMutable(true);
        this.session.handleMessage(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()));
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Mockito.verify(this.sessionHandler).afterConnected(this.session, stompHeaders);
        Mockito.verifyNoMoreInteractions(this.sessionHandler);
    }

    @Test
    public void heartbeatValues() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        this.connectHeaders.setHeartbeat(new long[]{ 10000, 10000 });
        StompHeaderAccessor accessor = StompHeaderAccessor.create(CONNECTED);
        accessor.setVersion("1.2");
        accessor.setHeartbeat(20000, 20000);
        accessor.setLeaveMutable(true);
        this.session.handleMessage(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()));
        ArgumentCaptor<Long> writeInterval = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(this.connection).onWriteInactivity(ArgumentMatchers.any(Runnable.class), writeInterval.capture());
        Assert.assertEquals(20000, ((long) (writeInterval.getValue())));
        ArgumentCaptor<Long> readInterval = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(this.connection).onReadInactivity(ArgumentMatchers.any(Runnable.class), readInterval.capture());
        Assert.assertEquals(60000, ((long) (readInterval.getValue())));
    }

    @Test
    public void heartbeatNotSupportedByServer() {
        this.session.afterConnected(this.connection);
        Mockito.verify(this.connection).send(ArgumentMatchers.any());
        this.connectHeaders.setHeartbeat(new long[]{ 10000, 10000 });
        StompHeaderAccessor accessor = StompHeaderAccessor.create(CONNECTED);
        accessor.setVersion("1.2");
        accessor.setHeartbeat(0, 0);
        accessor.setLeaveMutable(true);
        this.session.handleMessage(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()));
        Mockito.verifyNoMoreInteractions(this.connection);
    }

    @Test
    public void heartbeatTasks() {
        this.session.afterConnected(this.connection);
        Mockito.verify(this.connection).send(ArgumentMatchers.any());
        this.connectHeaders.setHeartbeat(new long[]{ 10000, 10000 });
        StompHeaderAccessor connected = StompHeaderAccessor.create(CONNECTED);
        connected.setVersion("1.2");
        connected.setHeartbeat(10000, 10000);
        connected.setLeaveMutable(true);
        this.session.handleMessage(MessageBuilder.createMessage(new byte[0], connected.getMessageHeaders()));
        ArgumentCaptor<Runnable> writeTaskCaptor = ArgumentCaptor.forClass(Runnable.class);
        ArgumentCaptor<Runnable> readTaskCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(this.connection).onWriteInactivity(writeTaskCaptor.capture(), ArgumentMatchers.any(Long.class));
        Mockito.verify(this.connection).onReadInactivity(readTaskCaptor.capture(), ArgumentMatchers.any(Long.class));
        Runnable writeTask = writeTaskCaptor.getValue();
        Runnable readTask = readTaskCaptor.getValue();
        Assert.assertNotNull(writeTask);
        Assert.assertNotNull(readTask);
        writeTask.run();
        StompHeaderAccessor accessor = StompHeaderAccessor.createForHeartbeat();
        Message<byte[]> message = MessageBuilder.createMessage(new byte[]{ '\n' }, accessor.getMessageHeaders());
        Mockito.verify(this.connection).send(ArgumentMatchers.eq(message));
        Mockito.verifyNoMoreInteractions(this.connection);
        Mockito.reset(this.sessionHandler);
        readTask.run();
        Mockito.verify(this.sessionHandler).handleTransportError(ArgumentMatchers.same(this.session), ArgumentMatchers.any(IllegalStateException.class));
        Mockito.verifyNoMoreInteractions(this.sessionHandler);
    }

    @Test
    public void handleErrorFrame() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(ERROR);
        accessor.setContentType(new MimeType("text", "plain", StandardCharsets.UTF_8));
        accessor.addNativeHeader("foo", "bar");
        accessor.setLeaveMutable(true);
        String payload = "Oops";
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Mockito.when(this.sessionHandler.getPayloadType(stompHeaders)).thenReturn(String.class);
        this.session.handleMessage(MessageBuilder.createMessage(payload.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders()));
        Mockito.verify(this.sessionHandler).getPayloadType(stompHeaders);
        Mockito.verify(this.sessionHandler).handleFrame(stompHeaders, payload);
        Mockito.verifyNoMoreInteractions(this.sessionHandler);
    }

    @Test
    public void handleErrorFrameWithEmptyPayload() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(ERROR);
        accessor.addNativeHeader("foo", "bar");
        accessor.setLeaveMutable(true);
        this.session.handleMessage(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()));
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Mockito.verify(this.sessionHandler).handleFrame(stompHeaders, null);
        Mockito.verifyNoMoreInteractions(this.sessionHandler);
    }

    @Test
    public void handleErrorFrameWithConversionException() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(ERROR);
        accessor.setContentType(APPLICATION_JSON);
        accessor.addNativeHeader("foo", "bar");
        accessor.setLeaveMutable(true);
        byte[] payload = "{'foo':'bar'}".getBytes(StandardCharsets.UTF_8);
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Mockito.when(this.sessionHandler.getPayloadType(stompHeaders)).thenReturn(Map.class);
        this.session.handleMessage(MessageBuilder.createMessage(payload, accessor.getMessageHeaders()));
        Mockito.verify(this.sessionHandler).getPayloadType(stompHeaders);
        Mockito.verify(this.sessionHandler).handleException(ArgumentMatchers.same(this.session), ArgumentMatchers.same(ERROR), ArgumentMatchers.eq(stompHeaders), ArgumentMatchers.same(payload), ArgumentMatchers.any(MessageConversionException.class));
        Mockito.verifyNoMoreInteractions(this.sessionHandler);
    }

    @Test
    public void handleMessageFrame() {
        this.session.afterConnected(this.connection);
        StompFrameHandler frameHandler = Mockito.mock(StompFrameHandler.class);
        String destination = "/topic/foo";
        Subscription subscription = this.session.subscribe(destination, frameHandler);
        StompHeaderAccessor accessor = StompHeaderAccessor.create(MESSAGE);
        accessor.setDestination(destination);
        accessor.setSubscriptionId(subscription.getSubscriptionId());
        accessor.setContentType(TEXT_PLAIN);
        accessor.setMessageId("1");
        accessor.setLeaveMutable(true);
        String payload = "sample payload";
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Mockito.when(frameHandler.getPayloadType(stompHeaders)).thenReturn(String.class);
        this.session.handleMessage(MessageBuilder.createMessage(payload.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders()));
        Mockito.verify(frameHandler).getPayloadType(stompHeaders);
        Mockito.verify(frameHandler).handleFrame(stompHeaders, payload);
        Mockito.verifyNoMoreInteractions(frameHandler);
    }

    @Test
    public void handleMessageFrameWithConversionException() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        StompFrameHandler frameHandler = Mockito.mock(StompFrameHandler.class);
        String destination = "/topic/foo";
        Subscription subscription = this.session.subscribe(destination, frameHandler);
        StompHeaderAccessor accessor = StompHeaderAccessor.create(MESSAGE);
        accessor.setDestination(destination);
        accessor.setSubscriptionId(subscription.getSubscriptionId());
        accessor.setContentType(APPLICATION_JSON);
        accessor.setMessageId("1");
        accessor.setLeaveMutable(true);
        byte[] payload = "{'foo':'bar'}".getBytes(StandardCharsets.UTF_8);
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Mockito.when(frameHandler.getPayloadType(stompHeaders)).thenReturn(Map.class);
        this.session.handleMessage(MessageBuilder.createMessage(payload, accessor.getMessageHeaders()));
        Mockito.verify(frameHandler).getPayloadType(stompHeaders);
        Mockito.verifyNoMoreInteractions(frameHandler);
        Mockito.verify(this.sessionHandler).handleException(ArgumentMatchers.same(this.session), ArgumentMatchers.same(MESSAGE), ArgumentMatchers.eq(stompHeaders), ArgumentMatchers.same(payload), ArgumentMatchers.any(MessageConversionException.class));
        Mockito.verifyNoMoreInteractions(this.sessionHandler);
    }

    @Test
    public void handleFailure() {
        IllegalStateException exception = new IllegalStateException("simulated exception");
        this.session.handleFailure(exception);
        Mockito.verify(this.sessionHandler).handleTransportError(this.session, exception);
        Mockito.verifyNoMoreInteractions(this.sessionHandler);
    }

    @Test
    public void afterConnectionClosed() {
        this.session.afterConnectionClosed();
        Mockito.verify(this.sessionHandler).handleTransportError(ArgumentMatchers.same(this.session), ArgumentMatchers.any(ConnectionLostException.class));
        Mockito.verifyNoMoreInteractions(this.sessionHandler);
    }

    @Test
    public void send() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        String destination = "/topic/foo";
        String payload = "sample payload";
        this.session.send(destination, payload);
        Message<byte[]> message = this.messageCaptor.getValue();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals(SEND, accessor.getCommand());
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Assert.assertEquals(stompHeaders.toString(), 2, stompHeaders.size());
        Assert.assertEquals(destination, stompHeaders.getDestination());
        Assert.assertEquals(new MimeType("text", "plain", StandardCharsets.UTF_8), stompHeaders.getContentType());
        Assert.assertEquals((-1), stompHeaders.getContentLength());// StompEncoder isn't involved

        Assert.assertEquals(payload, new String(message.getPayload(), StandardCharsets.UTF_8));
    }

    @Test
    public void sendWithReceipt() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        this.session.setTaskScheduler(Mockito.mock(TaskScheduler.class));
        this.session.setAutoReceipt(true);
        this.session.send("/topic/foo", "sample payload");
        Message<byte[]> message = this.messageCaptor.getValue();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertNotNull(accessor.getReceipt());
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/topic/foo");
        stompHeaders.setReceipt("my-receipt");
        this.session.send(stompHeaders, "sample payload");
        message = this.messageCaptor.getValue();
        accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals("my-receipt", accessor.getReceipt());
    }

    @Test
    public void sendWithConversionException() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setDestination("/topic/foo");
        stompHeaders.setContentType(APPLICATION_JSON);
        String payload = "{'foo':'bar'}";
        this.expected.expect(MessageConversionException.class);
        this.session.send(stompHeaders, payload);
        Mockito.verifyNoMoreInteractions(this.connection);
    }

    @Test
    public void sendWithExecutionException() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        IllegalStateException exception = new IllegalStateException("simulated exception");
        SettableListenableFuture<Void> future = new SettableListenableFuture();
        future.setException(exception);
        Mockito.when(this.connection.send(ArgumentMatchers.any())).thenReturn(future);
        this.expected.expect(MessageDeliveryException.class);
        this.expected.expectCause(Matchers.sameInstance(exception));
        this.session.send("/topic/foo", "sample payload".getBytes(StandardCharsets.UTF_8));
        Mockito.verifyNoMoreInteractions(this.connection);
    }

    @Test
    public void subscribe() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        String destination = "/topic/foo";
        StompFrameHandler frameHandler = Mockito.mock(StompFrameHandler.class);
        Subscription subscription = this.session.subscribe(destination, frameHandler);
        Message<byte[]> message = this.messageCaptor.getValue();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals(SUBSCRIBE, accessor.getCommand());
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Assert.assertEquals(stompHeaders.toString(), 2, stompHeaders.size());
        Assert.assertEquals(destination, stompHeaders.getDestination());
        Assert.assertEquals(subscription.getSubscriptionId(), stompHeaders.getId());
    }

    @Test
    public void subscribeWithHeaders() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        String subscriptionId = "123";
        String destination = "/topic/foo";
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.setId(subscriptionId);
        stompHeaders.setDestination(destination);
        StompFrameHandler frameHandler = Mockito.mock(StompFrameHandler.class);
        Subscription subscription = this.session.subscribe(stompHeaders, frameHandler);
        Assert.assertEquals(subscriptionId, subscription.getSubscriptionId());
        Message<byte[]> message = this.messageCaptor.getValue();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals(SUBSCRIBE, accessor.getCommand());
        stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Assert.assertEquals(stompHeaders.toString(), 2, stompHeaders.size());
        Assert.assertEquals(destination, stompHeaders.getDestination());
        Assert.assertEquals(subscriptionId, stompHeaders.getId());
    }

    @Test
    public void unsubscribe() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        String destination = "/topic/foo";
        StompFrameHandler frameHandler = Mockito.mock(StompFrameHandler.class);
        Subscription subscription = this.session.subscribe(destination, frameHandler);
        subscription.unsubscribe();
        Message<byte[]> message = this.messageCaptor.getValue();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals(UNSUBSCRIBE, accessor.getCommand());
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Assert.assertEquals(stompHeaders.toString(), 1, stompHeaders.size());
        Assert.assertEquals(subscription.getSubscriptionId(), stompHeaders.getId());
    }

    // SPR-15131
    @Test
    public void unsubscribeWithCustomHeader() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        String headerName = "durable-subscription-name";
        String headerValue = "123";
        StompHeaders subscribeHeaders = new StompHeaders();
        subscribeHeaders.setDestination("/topic/foo");
        subscribeHeaders.set(headerName, headerValue);
        StompFrameHandler frameHandler = Mockito.mock(StompFrameHandler.class);
        Subscription subscription = this.session.subscribe(subscribeHeaders, frameHandler);
        StompHeaders unsubscribeHeaders = new StompHeaders();
        unsubscribeHeaders.set(headerName, subscription.getSubscriptionHeaders().getFirst(headerName));
        subscription.unsubscribe(unsubscribeHeaders);
        Message<byte[]> message = this.messageCaptor.getValue();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals(UNSUBSCRIBE, accessor.getCommand());
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Assert.assertEquals(stompHeaders.toString(), 2, stompHeaders.size());
        Assert.assertEquals(subscription.getSubscriptionId(), stompHeaders.getId());
        Assert.assertEquals(headerValue, stompHeaders.getFirst(headerName));
    }

    @Test
    public void ack() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        String messageId = "123";
        this.session.acknowledge(messageId, true);
        Message<byte[]> message = this.messageCaptor.getValue();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals(ACK, accessor.getCommand());
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Assert.assertEquals(stompHeaders.toString(), 1, stompHeaders.size());
        Assert.assertEquals(messageId, stompHeaders.getId());
    }

    @Test
    public void nack() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        String messageId = "123";
        this.session.acknowledge(messageId, false);
        Message<byte[]> message = this.messageCaptor.getValue();
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        Assert.assertEquals(NACK, accessor.getCommand());
        StompHeaders stompHeaders = StompHeaders.readOnlyStompHeaders(accessor.getNativeHeaders());
        Assert.assertEquals(stompHeaders.toString(), 1, stompHeaders.size());
        Assert.assertEquals(messageId, stompHeaders.getId());
    }

    @Test
    public void receiptReceived() {
        this.session.afterConnected(this.connection);
        this.session.setTaskScheduler(Mockito.mock(TaskScheduler.class));
        AtomicReference<Boolean> received = new AtomicReference<>();
        StompHeaders headers = new StompHeaders();
        headers.setDestination("/topic/foo");
        headers.setReceipt("my-receipt");
        Subscription subscription = this.session.subscribe(headers, Mockito.mock(StompFrameHandler.class));
        subscription.addReceiptTask(() -> received.set(true));
        Assert.assertNull(received.get());
        StompHeaderAccessor accessor = StompHeaderAccessor.create(RECEIPT);
        accessor.setReceiptId("my-receipt");
        accessor.setLeaveMutable(true);
        this.session.handleMessage(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()));
        Assert.assertNotNull(received.get());
        Assert.assertTrue(received.get());
    }

    @Test
    public void receiptReceivedBeforeTaskAdded() {
        this.session.afterConnected(this.connection);
        this.session.setTaskScheduler(Mockito.mock(TaskScheduler.class));
        AtomicReference<Boolean> received = new AtomicReference<>();
        StompHeaders headers = new StompHeaders();
        headers.setDestination("/topic/foo");
        headers.setReceipt("my-receipt");
        Subscription subscription = this.session.subscribe(headers, Mockito.mock(StompFrameHandler.class));
        StompHeaderAccessor accessor = StompHeaderAccessor.create(RECEIPT);
        accessor.setReceiptId("my-receipt");
        accessor.setLeaveMutable(true);
        this.session.handleMessage(MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()));
        subscription.addReceiptTask(() -> received.set(true));
        Assert.assertNotNull(received.get());
        Assert.assertTrue(received.get());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void receiptNotReceived() {
        TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);
        this.session.afterConnected(this.connection);
        this.session.setTaskScheduler(taskScheduler);
        AtomicReference<Boolean> notReceived = new AtomicReference<>();
        ScheduledFuture future = Mockito.mock(ScheduledFuture.class);
        Mockito.when(taskScheduler.schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class))).thenReturn(future);
        StompHeaders headers = new StompHeaders();
        headers.setDestination("/topic/foo");
        headers.setReceipt("my-receipt");
        Receiptable receiptable = this.session.send(headers, "payload");
        receiptable.addReceiptLostTask(() -> notReceived.set(true));
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(taskScheduler).schedule(taskCaptor.capture(), ((Date) (ArgumentMatchers.notNull())));
        Runnable scheduledTask = taskCaptor.getValue();
        Assert.assertNotNull(scheduledTask);
        Assert.assertNull(notReceived.get());
        scheduledTask.run();
        Assert.assertTrue(notReceived.get());
        Mockito.verify(future).cancel(true);
        Mockito.verifyNoMoreInteractions(future);
    }

    @Test
    public void disconnect() {
        this.session.afterConnected(this.connection);
        Assert.assertTrue(this.session.isConnected());
        this.session.disconnect();
        Assert.assertFalse(this.session.isConnected());
        Mockito.verifyNoMoreInteractions(this.sessionHandler);
    }
}

