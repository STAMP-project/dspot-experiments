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
package org.springframework.messaging.core;


import GenericMessagingTemplate.DEFAULT_RECEIVE_TIMEOUT_HEADER;
import GenericMessagingTemplate.DEFAULT_SEND_TIMEOUT_HEADER;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.StubMessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


/**
 * Unit tests for {@link GenericMessagingTemplate}.
 *
 * @author Rossen Stoyanchev
 * @author Gary Russell
 */
public class GenericMessagingTemplateTests {
    private GenericMessagingTemplate template;

    private StubMessageChannel messageChannel;

    private ThreadPoolTaskExecutor executor;

    @Test
    public void sendWithTimeout() {
        SubscribableChannel channel = Mockito.mock(SubscribableChannel.class);
        final AtomicReference<Message<?>> sent = new AtomicReference<>();
        Mockito.doAnswer(( invocation) -> {
            sent.set(invocation.getArgument(0));
            return true;
        }).when(channel).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(30000L));
        Message<?> message = MessageBuilder.withPayload("request").setHeader(DEFAULT_SEND_TIMEOUT_HEADER, 30000L).setHeader(DEFAULT_RECEIVE_TIMEOUT_HEADER, 1L).build();
        this.template.send(channel, message);
        Mockito.verify(channel).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(30000L));
        Assert.assertNotNull(sent.get());
        Assert.assertFalse(sent.get().getHeaders().containsKey(DEFAULT_SEND_TIMEOUT_HEADER));
        Assert.assertFalse(sent.get().getHeaders().containsKey(DEFAULT_RECEIVE_TIMEOUT_HEADER));
    }

    @Test
    public void sendWithTimeoutMutable() {
        SubscribableChannel channel = Mockito.mock(SubscribableChannel.class);
        final AtomicReference<Message<?>> sent = new AtomicReference<>();
        Mockito.doAnswer(( invocation) -> {
            sent.set(invocation.getArgument(0));
            return true;
        }).when(channel).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(30000L));
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setLeaveMutable(true);
        Message<?> message = new org.springframework.messaging.support.GenericMessage("request", accessor.getMessageHeaders());
        accessor.setHeader(DEFAULT_SEND_TIMEOUT_HEADER, 30000L);
        this.template.send(channel, message);
        Mockito.verify(channel).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(30000L));
        Assert.assertNotNull(sent.get());
        Assert.assertFalse(sent.get().getHeaders().containsKey(DEFAULT_SEND_TIMEOUT_HEADER));
        Assert.assertFalse(sent.get().getHeaders().containsKey(DEFAULT_RECEIVE_TIMEOUT_HEADER));
    }

    @Test
    public void sendAndReceive() {
        SubscribableChannel channel = new org.springframework.messaging.support.ExecutorSubscribableChannel(this.executor);
        channel.subscribe(new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                MessageChannel replyChannel = ((MessageChannel) (message.getHeaders().getReplyChannel()));
                replyChannel.send(new org.springframework.messaging.support.GenericMessage("response"));
            }
        });
        String actual = this.template.convertSendAndReceive(channel, "request", String.class);
        Assert.assertEquals("response", actual);
    }

    @Test
    public void sendAndReceiveTimeout() throws InterruptedException {
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        final CountDownLatch latch = new CountDownLatch(1);
        this.template.setReceiveTimeout(1);
        this.template.setSendTimeout(30000L);
        this.template.setThrowExceptionOnLateReply(true);
        SubscribableChannel channel = Mockito.mock(SubscribableChannel.class);
        MessageHandler handler = createLateReplier(latch, failure);
        Mockito.doAnswer(( invocation) -> {
            this.executor.execute(() -> {
                handler.handleMessage(invocation.getArgument(0));
            });
            return true;
        }).when(channel).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.anyLong());
        Assert.assertNull(this.template.convertSendAndReceive(channel, "request", String.class));
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
        Throwable ex = failure.get();
        if (ex != null) {
            throw new AssertionError(ex);
        }
        Mockito.verify(channel).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(30000L));
    }

    @Test
    public void sendAndReceiveVariableTimeout() throws InterruptedException {
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        final CountDownLatch latch = new CountDownLatch(1);
        this.template.setSendTimeout(20000);
        this.template.setReceiveTimeout(10000);
        this.template.setThrowExceptionOnLateReply(true);
        SubscribableChannel channel = Mockito.mock(SubscribableChannel.class);
        MessageHandler handler = createLateReplier(latch, failure);
        Mockito.doAnswer(( invocation) -> {
            this.executor.execute(() -> {
                handler.handleMessage(invocation.getArgument(0));
            });
            return true;
        }).when(channel).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.anyLong());
        Message<?> message = MessageBuilder.withPayload("request").setHeader(DEFAULT_SEND_TIMEOUT_HEADER, 30000L).setHeader(DEFAULT_RECEIVE_TIMEOUT_HEADER, 1L).build();
        Assert.assertNull(this.template.sendAndReceive(channel, message));
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
        Throwable ex = failure.get();
        if (ex != null) {
            throw new AssertionError(ex);
        }
        Mockito.verify(channel).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(30000L));
    }

    @Test
    public void sendAndReceiveVariableTimeoutCustomHeaders() throws InterruptedException {
        final AtomicReference<Throwable> failure = new AtomicReference<Throwable>();
        final CountDownLatch latch = new CountDownLatch(1);
        this.template.setSendTimeout(20000);
        this.template.setReceiveTimeout(10000);
        this.template.setThrowExceptionOnLateReply(true);
        this.template.setSendTimeoutHeader("sto");
        this.template.setReceiveTimeoutHeader("rto");
        SubscribableChannel channel = Mockito.mock(SubscribableChannel.class);
        MessageHandler handler = createLateReplier(latch, failure);
        Mockito.doAnswer(( invocation) -> {
            this.executor.execute(() -> {
                handler.handleMessage(invocation.getArgument(0));
            });
            return true;
        }).when(channel).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.anyLong());
        Message<?> message = MessageBuilder.withPayload("request").setHeader("sto", 30000L).setHeader("rto", 1L).build();
        Assert.assertNull(this.template.sendAndReceive(channel, message));
        Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
        Throwable ex = failure.get();
        if (ex != null) {
            throw new AssertionError(ex);
        }
        Mockito.verify(channel).send(ArgumentMatchers.any(Message.class), ArgumentMatchers.eq(30000L));
    }

    @Test
    public void convertAndSendWithSimpMessageHeaders() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setHeader("key", "value");
        accessor.setLeaveMutable(true);
        MessageHeaders headers = accessor.getMessageHeaders();
        this.template.convertAndSend("channel", "data", headers);
        List<Message<byte[]>> messages = this.messageChannel.getMessages();
        Message<byte[]> message = messages.get(0);
        Assert.assertSame(headers, message.getHeaders());
        Assert.assertFalse(accessor.isMutable());
    }

    private class TestDestinationResolver implements DestinationResolver<MessageChannel> {
        @Override
        public MessageChannel resolveDestination(String name) throws DestinationResolutionException {
            return messageChannel;
        }
    }
}

