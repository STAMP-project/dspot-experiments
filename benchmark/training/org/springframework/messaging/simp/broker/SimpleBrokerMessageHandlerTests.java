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
package org.springframework.messaging.simp.broker;


import SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER;
import SimpMessageHeaderAccessor.DISCONNECT_MESSAGE_HEADER;
import SimpMessageHeaderAccessor.MESSAGE_TYPE_HEADER;
import SimpMessageHeaderAccessor.SESSION_ID_HEADER;
import SimpMessageHeaderAccessor.USER_HEADER;
import SimpMessageType.CONNECT_ACK;
import SimpMessageType.DISCONNECT;
import SimpMessageType.DISCONNECT_ACK;
import SimpMessageType.HEARTBEAT;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.TestPrincipal;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.TaskScheduler;


/**
 * Unit tests for {@link SimpleBrokerMessageHandler}.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
@SuppressWarnings("unchecked")
public class SimpleBrokerMessageHandlerTests {
    private SimpleBrokerMessageHandler messageHandler;

    @Mock
    private SubscribableChannel clientInChannel;

    @Mock
    private MessageChannel clientOutChannel;

    @Mock
    private SubscribableChannel brokerChannel;

    @Mock
    private TaskScheduler taskScheduler;

    @Captor
    ArgumentCaptor<Message<?>> messageCaptor;

    @Test
    public void subscribePublish() {
        startSession("sess1");
        startSession("sess2");
        this.messageHandler.handleMessage(createSubscriptionMessage("sess1", "sub1", "/foo"));
        this.messageHandler.handleMessage(createSubscriptionMessage("sess1", "sub2", "/foo"));
        this.messageHandler.handleMessage(createSubscriptionMessage("sess1", "sub3", "/bar"));
        this.messageHandler.handleMessage(createSubscriptionMessage("sess2", "sub1", "/foo"));
        this.messageHandler.handleMessage(createSubscriptionMessage("sess2", "sub2", "/foo"));
        this.messageHandler.handleMessage(createSubscriptionMessage("sess2", "sub3", "/bar"));
        this.messageHandler.handleMessage(createMessage("/foo", "message1"));
        this.messageHandler.handleMessage(createMessage("/bar", "message2"));
        Mockito.verify(this.clientOutChannel, Mockito.times(6)).send(this.messageCaptor.capture());
        Assert.assertTrue(messageCaptured("sess1", "sub1", "/foo"));
        Assert.assertTrue(messageCaptured("sess1", "sub2", "/foo"));
        Assert.assertTrue(messageCaptured("sess2", "sub1", "/foo"));
        Assert.assertTrue(messageCaptured("sess2", "sub2", "/foo"));
        Assert.assertTrue(messageCaptured("sess1", "sub3", "/bar"));
        Assert.assertTrue(messageCaptured("sess2", "sub3", "/bar"));
    }

    @Test
    public void subscribeDisconnectPublish() {
        String sess1 = "sess1";
        String sess2 = "sess2";
        startSession(sess1);
        startSession(sess2);
        this.messageHandler.handleMessage(createSubscriptionMessage(sess1, "sub1", "/foo"));
        this.messageHandler.handleMessage(createSubscriptionMessage(sess1, "sub2", "/foo"));
        this.messageHandler.handleMessage(createSubscriptionMessage(sess1, "sub3", "/bar"));
        this.messageHandler.handleMessage(createSubscriptionMessage(sess2, "sub1", "/foo"));
        this.messageHandler.handleMessage(createSubscriptionMessage(sess2, "sub2", "/foo"));
        this.messageHandler.handleMessage(createSubscriptionMessage(sess2, "sub3", "/bar"));
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.create(DISCONNECT);
        headers.setSessionId(sess1);
        headers.setUser(new TestPrincipal("joe"));
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headers.getMessageHeaders());
        this.messageHandler.handleMessage(message);
        this.messageHandler.handleMessage(createMessage("/foo", "message1"));
        this.messageHandler.handleMessage(createMessage("/bar", "message2"));
        Mockito.verify(this.clientOutChannel, Mockito.times(4)).send(this.messageCaptor.capture());
        Message<?> captured = this.messageCaptor.getAllValues().get(2);
        Assert.assertEquals(DISCONNECT_ACK, SimpMessageHeaderAccessor.getMessageType(captured.getHeaders()));
        Assert.assertSame(message, captured.getHeaders().get(DISCONNECT_MESSAGE_HEADER));
        Assert.assertEquals(sess1, SimpMessageHeaderAccessor.getSessionId(captured.getHeaders()));
        Assert.assertEquals("joe", SimpMessageHeaderAccessor.getUser(captured.getHeaders()).getName());
        Assert.assertTrue(messageCaptured(sess2, "sub1", "/foo"));
        Assert.assertTrue(messageCaptured(sess2, "sub2", "/foo"));
        Assert.assertTrue(messageCaptured(sess2, "sub3", "/bar"));
    }

    @Test
    public void connect() {
        String id = "sess1";
        Message<String> connectMessage = startSession(id);
        Message<?> connectAckMessage = this.messageCaptor.getValue();
        SimpMessageHeaderAccessor connectAckHeaders = SimpMessageHeaderAccessor.wrap(connectAckMessage);
        Assert.assertEquals(connectMessage, connectAckHeaders.getHeader(CONNECT_MESSAGE_HEADER));
        Assert.assertEquals(id, connectAckHeaders.getSessionId());
        Assert.assertEquals("joe", connectAckHeaders.getUser().getName());
        Assert.assertArrayEquals(new long[]{ 10000, 10000 }, SimpMessageHeaderAccessor.getHeartbeat(connectAckHeaders.getMessageHeaders()));
    }

    @Test
    public void heartbeatValueWithAndWithoutTaskScheduler() {
        Assert.assertNull(this.messageHandler.getHeartbeatValue());
        this.messageHandler.setTaskScheduler(this.taskScheduler);
        Assert.assertNotNull(this.messageHandler.getHeartbeatValue());
        Assert.assertArrayEquals(new long[]{ 10000, 10000 }, this.messageHandler.getHeartbeatValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void startWithHeartbeatValueWithoutTaskScheduler() {
        this.messageHandler.setHeartbeatValue(new long[]{ 10000, 10000 });
        this.messageHandler.start();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void startAndStopWithHeartbeatValue() {
        ScheduledFuture future = Mockito.mock(ScheduledFuture.class);
        Mockito.when(this.taskScheduler.scheduleWithFixedDelay(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(15000L))).thenReturn(future);
        this.messageHandler.setTaskScheduler(this.taskScheduler);
        this.messageHandler.setHeartbeatValue(new long[]{ 15000, 16000 });
        this.messageHandler.start();
        Mockito.verify(this.taskScheduler).scheduleWithFixedDelay(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(15000L));
        Mockito.verifyNoMoreInteractions(this.taskScheduler, future);
        this.messageHandler.stop();
        Mockito.verify(future).cancel(true);
        Mockito.verifyNoMoreInteractions(future);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void startWithOneZeroHeartbeatValue() {
        this.messageHandler.setTaskScheduler(this.taskScheduler);
        this.messageHandler.setHeartbeatValue(new long[]{ 0, 10000 });
        this.messageHandler.start();
        Mockito.verify(this.taskScheduler).scheduleWithFixedDelay(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(10000L));
    }

    @Test
    public void readInactivity() throws Exception {
        this.messageHandler.setHeartbeatValue(new long[]{ 0, 1 });
        this.messageHandler.setTaskScheduler(this.taskScheduler);
        this.messageHandler.start();
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(this.taskScheduler).scheduleWithFixedDelay(taskCaptor.capture(), ArgumentMatchers.eq(1L));
        Runnable heartbeatTask = taskCaptor.getValue();
        Assert.assertNotNull(heartbeatTask);
        String id = "sess1";
        TestPrincipal user = new TestPrincipal("joe");
        Message<String> connectMessage = createConnectMessage(id, user, new long[]{ 1, 0 });
        this.messageHandler.handleMessage(connectMessage);
        Thread.sleep(10);
        heartbeatTask.run();
        Mockito.verify(this.clientOutChannel, Mockito.atLeast(2)).send(this.messageCaptor.capture());
        List<Message<?>> messages = this.messageCaptor.getAllValues();
        Assert.assertEquals(2, messages.size());
        MessageHeaders headers = messages.get(0).getHeaders();
        Assert.assertEquals(CONNECT_ACK, headers.get(MESSAGE_TYPE_HEADER));
        headers = messages.get(1).getHeaders();
        Assert.assertEquals(DISCONNECT_ACK, headers.get(MESSAGE_TYPE_HEADER));
        Assert.assertEquals(id, headers.get(SESSION_ID_HEADER));
        Assert.assertEquals(user, headers.get(USER_HEADER));
    }

    @Test
    public void writeInactivity() throws Exception {
        this.messageHandler.setHeartbeatValue(new long[]{ 1, 0 });
        this.messageHandler.setTaskScheduler(this.taskScheduler);
        this.messageHandler.start();
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(this.taskScheduler).scheduleWithFixedDelay(taskCaptor.capture(), ArgumentMatchers.eq(1L));
        Runnable heartbeatTask = taskCaptor.getValue();
        Assert.assertNotNull(heartbeatTask);
        String id = "sess1";
        TestPrincipal user = new TestPrincipal("joe");
        Message<String> connectMessage = createConnectMessage(id, user, new long[]{ 0, 1 });
        this.messageHandler.handleMessage(connectMessage);
        Thread.sleep(10);
        heartbeatTask.run();
        Mockito.verify(this.clientOutChannel, Mockito.times(2)).send(this.messageCaptor.capture());
        List<Message<?>> messages = this.messageCaptor.getAllValues();
        Assert.assertEquals(2, messages.size());
        MessageHeaders headers = messages.get(0).getHeaders();
        Assert.assertEquals(CONNECT_ACK, headers.get(MESSAGE_TYPE_HEADER));
        headers = messages.get(1).getHeaders();
        Assert.assertEquals(HEARTBEAT, headers.get(MESSAGE_TYPE_HEADER));
        Assert.assertEquals(id, headers.get(SESSION_ID_HEADER));
        Assert.assertEquals(user, headers.get(USER_HEADER));
    }

    @Test
    public void readWriteIntervalCalculation() throws Exception {
        this.messageHandler.setHeartbeatValue(new long[]{ 1, 1 });
        this.messageHandler.setTaskScheduler(this.taskScheduler);
        this.messageHandler.start();
        ArgumentCaptor<Runnable> taskCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(this.taskScheduler).scheduleWithFixedDelay(taskCaptor.capture(), ArgumentMatchers.eq(1L));
        Runnable heartbeatTask = taskCaptor.getValue();
        Assert.assertNotNull(heartbeatTask);
        String id = "sess1";
        TestPrincipal user = new TestPrincipal("joe");
        Message<String> connectMessage = createConnectMessage(id, user, new long[]{ 10000, 10000 });
        this.messageHandler.handleMessage(connectMessage);
        Thread.sleep(10);
        heartbeatTask.run();
        Mockito.verify(this.clientOutChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        List<Message<?>> messages = this.messageCaptor.getAllValues();
        Assert.assertEquals(1, messages.size());
        Assert.assertEquals(CONNECT_ACK, messages.get(0).getHeaders().get(MESSAGE_TYPE_HEADER));
    }
}

