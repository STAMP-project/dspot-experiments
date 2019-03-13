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
package org.springframework.messaging.support;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;


/**
 * Test fixture for the use of {@link ChannelInterceptor}s.
 *
 * @author Rossen Stoyanchev
 */
public class ChannelInterceptorTests {
    private ExecutorSubscribableChannel channel;

    private ChannelInterceptorTests.TestMessageHandler messageHandler;

    @Test
    public void preSendInterceptorReturningModifiedMessage() {
        Message<?> expected = Mockito.mock(Message.class);
        ChannelInterceptorTests.PreSendInterceptor interceptor = new ChannelInterceptorTests.PreSendInterceptor();
        interceptor.setMessageToReturn(expected);
        this.channel.addInterceptor(interceptor);
        this.channel.send(MessageBuilder.withPayload("test").build());
        Assert.assertEquals(1, this.messageHandler.getMessages().size());
        Message<?> result = this.messageHandler.getMessages().get(0);
        Assert.assertNotNull(result);
        Assert.assertSame(expected, result);
        Assert.assertTrue(interceptor.wasAfterCompletionInvoked());
    }

    @Test
    public void preSendInterceptorReturningNull() {
        ChannelInterceptorTests.PreSendInterceptor interceptor1 = new ChannelInterceptorTests.PreSendInterceptor();
        ChannelInterceptorTests.NullReturningPreSendInterceptor interceptor2 = new ChannelInterceptorTests.NullReturningPreSendInterceptor();
        this.channel.addInterceptor(interceptor1);
        this.channel.addInterceptor(interceptor2);
        Message<?> message = MessageBuilder.withPayload("test").build();
        this.channel.send(message);
        Assert.assertEquals(1, interceptor1.getCounter().get());
        Assert.assertEquals(1, interceptor2.getCounter().get());
        Assert.assertEquals(0, this.messageHandler.getMessages().size());
        Assert.assertTrue(interceptor1.wasAfterCompletionInvoked());
        Assert.assertFalse(interceptor2.wasAfterCompletionInvoked());
    }

    @Test
    public void postSendInterceptorMessageWasSent() {
        final AtomicBoolean preSendInvoked = new AtomicBoolean(false);
        final AtomicBoolean completionInvoked = new AtomicBoolean(false);
        this.channel.addInterceptor(new ChannelInterceptor() {
            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                assertInput(message, channel, sent);
                preSendInvoked.set(true);
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
                assertInput(message, channel, sent);
                completionInvoked.set(true);
            }

            private void assertInput(Message<?> message, MessageChannel channel, boolean sent) {
                Assert.assertNotNull(message);
                Assert.assertNotNull(channel);
                Assert.assertSame(ChannelInterceptorTests.this.channel, channel);
                Assert.assertTrue(sent);
            }
        });
        this.channel.send(MessageBuilder.withPayload("test").build());
        Assert.assertTrue(preSendInvoked.get());
        Assert.assertTrue(completionInvoked.get());
    }

    @Test
    public void postSendInterceptorMessageWasNotSent() {
        final AbstractMessageChannel testChannel = new AbstractMessageChannel() {
            @Override
            protected boolean sendInternal(Message<?> message, long timeout) {
                return false;
            }
        };
        final AtomicBoolean preSendInvoked = new AtomicBoolean(false);
        final AtomicBoolean completionInvoked = new AtomicBoolean(false);
        testChannel.addInterceptor(new ChannelInterceptor() {
            @Override
            public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
                assertInput(message, channel, sent);
                preSendInvoked.set(true);
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
                assertInput(message, channel, sent);
                completionInvoked.set(true);
            }

            private void assertInput(Message<?> message, MessageChannel channel, boolean sent) {
                Assert.assertNotNull(message);
                Assert.assertNotNull(channel);
                Assert.assertSame(testChannel, channel);
                Assert.assertFalse(sent);
            }
        });
        testChannel.send(MessageBuilder.withPayload("test").build());
        Assert.assertTrue(preSendInvoked.get());
        Assert.assertTrue(completionInvoked.get());
    }

    @Test
    public void afterCompletionWithSendException() {
        final AbstractMessageChannel testChannel = new AbstractMessageChannel() {
            @Override
            protected boolean sendInternal(Message<?> message, long timeout) {
                throw new RuntimeException("Simulated exception");
            }
        };
        ChannelInterceptorTests.PreSendInterceptor interceptor1 = new ChannelInterceptorTests.PreSendInterceptor();
        ChannelInterceptorTests.PreSendInterceptor interceptor2 = new ChannelInterceptorTests.PreSendInterceptor();
        testChannel.addInterceptor(interceptor1);
        testChannel.addInterceptor(interceptor2);
        try {
            testChannel.send(MessageBuilder.withPayload("test").build());
        } catch (Exception ex) {
            Assert.assertEquals("Simulated exception", ex.getCause().getMessage());
        }
        Assert.assertTrue(interceptor1.wasAfterCompletionInvoked());
        Assert.assertTrue(interceptor2.wasAfterCompletionInvoked());
    }

    @Test
    public void afterCompletionWithPreSendException() {
        ChannelInterceptorTests.PreSendInterceptor interceptor1 = new ChannelInterceptorTests.PreSendInterceptor();
        ChannelInterceptorTests.PreSendInterceptor interceptor2 = new ChannelInterceptorTests.PreSendInterceptor();
        interceptor2.setExceptionToRaise(new RuntimeException("Simulated exception"));
        this.channel.addInterceptor(interceptor1);
        this.channel.addInterceptor(interceptor2);
        try {
            this.channel.send(MessageBuilder.withPayload("test").build());
        } catch (Exception ex) {
            Assert.assertEquals("Simulated exception", ex.getCause().getMessage());
        }
        Assert.assertTrue(interceptor1.wasAfterCompletionInvoked());
        Assert.assertFalse(interceptor2.wasAfterCompletionInvoked());
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

    private abstract static class AbstractTestInterceptor implements ChannelInterceptor {
        private AtomicInteger counter = new AtomicInteger();

        private volatile boolean afterCompletionInvoked;

        public AtomicInteger getCounter() {
            return this.counter;
        }

        public boolean wasAfterCompletionInvoked() {
            return this.afterCompletionInvoked;
        }

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            Assert.assertNotNull(message);
            counter.incrementAndGet();
            return message;
        }

        @Override
        public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
            this.afterCompletionInvoked = true;
        }
    }

    private static class PreSendInterceptor extends ChannelInterceptorTests.AbstractTestInterceptor {
        private Message<?> messageToReturn;

        private RuntimeException exceptionToRaise;

        public void setMessageToReturn(Message<?> messageToReturn) {
            this.messageToReturn = messageToReturn;
        }

        public void setExceptionToRaise(RuntimeException exception) {
            this.exceptionToRaise = exception;
        }

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            super.preSend(message, channel);
            if ((this.exceptionToRaise) != null) {
                throw this.exceptionToRaise;
            }
            return (this.messageToReturn) != null ? this.messageToReturn : message;
        }
    }

    private static class NullReturningPreSendInterceptor extends ChannelInterceptorTests.AbstractTestInterceptor {
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            super.preSend(message, channel);
            return null;
        }
    }
}

