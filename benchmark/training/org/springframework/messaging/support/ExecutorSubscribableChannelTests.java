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
package org.springframework.messaging.support;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.task.TaskExecutor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.MessageHandler;


/**
 * Unit tests for {@link ExecutorSubscribableChannel}.
 *
 * @author Phillip Webb
 */
public class ExecutorSubscribableChannelTests {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ExecutorSubscribableChannel channel = new ExecutorSubscribableChannel();

    @Mock
    private MessageHandler handler;

    private final Object payload = new Object();

    private final Message<Object> message = MessageBuilder.withPayload(this.payload).build();

    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;

    @Test
    public void messageMustNotBeNull() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Message must not be null");
        this.channel.send(null);
    }

    @Test
    public void sendWithoutExecutor() {
        ExecutorSubscribableChannelTests.BeforeHandleInterceptor interceptor = new ExecutorSubscribableChannelTests.BeforeHandleInterceptor();
        this.channel.addInterceptor(interceptor);
        this.channel.subscribe(this.handler);
        this.channel.send(this.message);
        Mockito.verify(this.handler).handleMessage(this.message);
        Assert.assertEquals(1, interceptor.getCounter().get());
        Assert.assertTrue(interceptor.wasAfterHandledInvoked());
    }

    @Test
    public void sendWithExecutor() {
        ExecutorSubscribableChannelTests.BeforeHandleInterceptor interceptor = new ExecutorSubscribableChannelTests.BeforeHandleInterceptor();
        TaskExecutor executor = Mockito.mock(TaskExecutor.class);
        ExecutorSubscribableChannel testChannel = new ExecutorSubscribableChannel(executor);
        testChannel.addInterceptor(interceptor);
        testChannel.subscribe(this.handler);
        testChannel.send(this.message);
        Mockito.verify(executor).execute(this.runnableCaptor.capture());
        Mockito.verify(this.handler, Mockito.never()).handleMessage(this.message);
        this.runnableCaptor.getValue().run();
        Mockito.verify(this.handler).handleMessage(this.message);
        Assert.assertEquals(1, interceptor.getCounter().get());
        Assert.assertTrue(interceptor.wasAfterHandledInvoked());
    }

    @Test
    public void subscribeTwice() {
        Assert.assertThat(this.channel.subscribe(this.handler), equalTo(true));
        Assert.assertThat(this.channel.subscribe(this.handler), equalTo(false));
        this.channel.send(this.message);
        Mockito.verify(this.handler, Mockito.times(1)).handleMessage(this.message);
    }

    @Test
    public void unsubscribeTwice() {
        this.channel.subscribe(this.handler);
        Assert.assertThat(this.channel.unsubscribe(this.handler), equalTo(true));
        Assert.assertThat(this.channel.unsubscribe(this.handler), equalTo(false));
        this.channel.send(this.message);
        Mockito.verify(this.handler, Mockito.never()).handleMessage(this.message);
    }

    @Test
    public void failurePropagates() {
        RuntimeException ex = new RuntimeException();
        BDDMockito.willThrow(ex).given(this.handler).handleMessage(this.message);
        MessageHandler secondHandler = Mockito.mock(MessageHandler.class);
        this.channel.subscribe(this.handler);
        this.channel.subscribe(secondHandler);
        try {
            this.channel.send(message);
        } catch (MessageDeliveryException actualException) {
            Assert.assertThat(actualException.getCause(), equalTo(ex));
        }
        Mockito.verifyZeroInteractions(secondHandler);
    }

    @Test
    public void concurrentModification() {
        this.channel.subscribe(( message1) -> channel.unsubscribe(handler));
        this.channel.subscribe(this.handler);
        this.channel.send(this.message);
        Mockito.verify(this.handler).handleMessage(this.message);
    }

    @Test
    public void interceptorWithModifiedMessage() {
        Message<?> expected = Mockito.mock(Message.class);
        ExecutorSubscribableChannelTests.BeforeHandleInterceptor interceptor = new ExecutorSubscribableChannelTests.BeforeHandleInterceptor();
        interceptor.setMessageToReturn(expected);
        this.channel.addInterceptor(interceptor);
        this.channel.subscribe(this.handler);
        this.channel.send(this.message);
        Mockito.verify(this.handler).handleMessage(expected);
        Assert.assertEquals(1, interceptor.getCounter().get());
        Assert.assertTrue(interceptor.wasAfterHandledInvoked());
    }

    @Test
    public void interceptorWithNull() {
        ExecutorSubscribableChannelTests.BeforeHandleInterceptor interceptor1 = new ExecutorSubscribableChannelTests.BeforeHandleInterceptor();
        ExecutorSubscribableChannelTests.NullReturningBeforeHandleInterceptor interceptor2 = new ExecutorSubscribableChannelTests.NullReturningBeforeHandleInterceptor();
        this.channel.addInterceptor(interceptor1);
        this.channel.addInterceptor(interceptor2);
        this.channel.subscribe(this.handler);
        this.channel.send(this.message);
        Mockito.verifyNoMoreInteractions(this.handler);
        Assert.assertEquals(1, interceptor1.getCounter().get());
        Assert.assertEquals(1, interceptor2.getCounter().get());
        Assert.assertTrue(interceptor1.wasAfterHandledInvoked());
    }

    @Test
    public void interceptorWithException() {
        IllegalStateException expected = new IllegalStateException("Fake exception");
        BDDMockito.willThrow(expected).given(this.handler).handleMessage(this.message);
        ExecutorSubscribableChannelTests.BeforeHandleInterceptor interceptor = new ExecutorSubscribableChannelTests.BeforeHandleInterceptor();
        this.channel.addInterceptor(interceptor);
        this.channel.subscribe(this.handler);
        try {
            this.channel.send(this.message);
        } catch (MessageDeliveryException actual) {
            Assert.assertSame(expected, actual.getCause());
        }
        Mockito.verify(this.handler).handleMessage(this.message);
        Assert.assertEquals(1, interceptor.getCounter().get());
        Assert.assertTrue(interceptor.wasAfterHandledInvoked());
    }

    private abstract static class AbstractTestInterceptor implements ChannelInterceptor , ExecutorChannelInterceptor {
        private AtomicInteger counter = new AtomicInteger();

        private volatile boolean afterHandledInvoked;

        public AtomicInteger getCounter() {
            return this.counter;
        }

        public boolean wasAfterHandledInvoked() {
            return this.afterHandledInvoked;
        }

        @Override
        public Message<?> beforeHandle(Message<?> message, MessageChannel channel, MessageHandler handler) {
            Assert.assertNotNull(message);
            counter.incrementAndGet();
            return message;
        }

        @Override
        public void afterMessageHandled(Message<?> message, MessageChannel channel, MessageHandler handler, Exception ex) {
            this.afterHandledInvoked = true;
        }
    }

    private static class BeforeHandleInterceptor extends ExecutorSubscribableChannelTests.AbstractTestInterceptor {
        private Message<?> messageToReturn;

        private RuntimeException exceptionToRaise;

        public void setMessageToReturn(Message<?> messageToReturn) {
            this.messageToReturn = messageToReturn;
        }

        // TODO Determine why setExceptionToRaise() is unused.
        @SuppressWarnings("unused")
        public void setExceptionToRaise(RuntimeException exception) {
            this.exceptionToRaise = exception;
        }

        @Override
        public Message<?> beforeHandle(Message<?> message, MessageChannel channel, MessageHandler handler) {
            super.beforeHandle(message, channel, handler);
            if ((this.exceptionToRaise) != null) {
                throw this.exceptionToRaise;
            }
            return (this.messageToReturn) != null ? this.messageToReturn : message;
        }
    }

    private static class NullReturningBeforeHandleInterceptor extends ExecutorSubscribableChannelTests.AbstractTestInterceptor {
        @Override
        public Message<?> beforeHandle(Message<?> message, MessageChannel channel, MessageHandler handler) {
            super.beforeHandle(message, channel, handler);
            return null;
        }
    }
}

