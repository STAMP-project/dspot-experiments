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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;


/**
 * Unit tests for {@link org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class BrokerMessageHandlerTests {
    private BrokerMessageHandlerTests.TestBrokerMessageHandler handler;

    @Test
    public void startShouldUpdateIsRunning() {
        Assert.assertFalse(isRunning());
        start();
        Assert.assertTrue(isRunning());
    }

    @Test
    public void stopShouldUpdateIsRunning() {
        start();
        Assert.assertTrue(isRunning());
        stop();
        Assert.assertFalse(isRunning());
    }

    @Test
    public void startAndStopShouldNotPublishBrokerAvailabilityEvents() {
        start();
        stop();
        Assert.assertEquals(Collections.emptyList(), this.handler.availabilityEvents);
    }

    @Test
    public void handleMessageWhenBrokerNotRunning() {
        this.handler.handleMessage(new org.springframework.messaging.support.GenericMessage<Object>("payload"));
        Assert.assertEquals(Collections.emptyList(), this.handler.messages);
    }

    @Test
    public void publishBrokerAvailableEvent() {
        Assert.assertFalse(isBrokerAvailable());
        Assert.assertEquals(Collections.emptyList(), this.handler.availabilityEvents);
        this.handler.publishBrokerAvailableEvent();
        Assert.assertTrue(isBrokerAvailable());
        Assert.assertEquals(Arrays.asList(true), this.handler.availabilityEvents);
    }

    @Test
    public void publishBrokerAvailableEventWhenAlreadyAvailable() {
        this.handler.publishBrokerAvailableEvent();
        this.handler.publishBrokerAvailableEvent();
        Assert.assertEquals(Arrays.asList(true), this.handler.availabilityEvents);
    }

    @Test
    public void publishBrokerUnavailableEvent() {
        this.handler.publishBrokerAvailableEvent();
        Assert.assertTrue(isBrokerAvailable());
        this.handler.publishBrokerUnavailableEvent();
        Assert.assertFalse(isBrokerAvailable());
        Assert.assertEquals(Arrays.asList(true, false), this.handler.availabilityEvents);
    }

    @Test
    public void publishBrokerUnavailableEventWhenAlreadyUnavailable() {
        this.handler.publishBrokerAvailableEvent();
        this.handler.publishBrokerUnavailableEvent();
        this.handler.publishBrokerUnavailableEvent();
        Assert.assertEquals(Arrays.asList(true, false), this.handler.availabilityEvents);
    }

    private static class TestBrokerMessageHandler extends AbstractBrokerMessageHandler implements ApplicationEventPublisher {
        private final List<Message<?>> messages = new ArrayList<>();

        private final List<Boolean> availabilityEvents = new ArrayList<>();

        private TestBrokerMessageHandler() {
            super(Mockito.mock(SubscribableChannel.class), Mockito.mock(MessageChannel.class), Mockito.mock(SubscribableChannel.class));
            setApplicationEventPublisher(this);
        }

        @Override
        protected void handleMessageInternal(Message<?> message) {
            this.messages.add(message);
        }

        @Override
        public void publishEvent(ApplicationEvent event) {
            publishEvent(((Object) (event)));
        }

        @Override
        public void publishEvent(Object event) {
            if (event instanceof BrokerAvailabilityEvent) {
                this.availabilityEvents.add(isBrokerAvailable());
            }
        }
    }
}

