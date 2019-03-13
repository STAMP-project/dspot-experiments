/**
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventhandling;


import RollbackConfigurationType.ANY_THROWABLE;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.axonframework.monitoring.MessageMonitor;
import org.axonframework.utils.EventTestUtils;
import org.junit.Test;
import org.mockito.Mockito;

import static AbstractEventProcessor.Builder.<init>;


public class AbstractEventProcessorTest {
    @Test
    public void expectCallbackForAllMessages() throws Exception {
        List<DomainEventMessage<?>> events = EventTestUtils.createEvents(2);
        Set<DomainEventMessage<?>> pending = new java.util.HashSet(events);
        MessageMonitor<EventMessage<?>> messageMonitor = ( message) -> new MessageMonitor.MonitorCallback() {
            @Override
            public void reportSuccess() {
                if (!(pending.contains(message))) {
                    fail(("Message was presented to monitor twice: " + message));
                }
                pending.remove(message);
            }

            @Override
            public void reportFailure(Throwable cause) {
                fail("Test expects 'reportSuccess' to be called");
            }

            @Override
            public void reportIgnored() {
                fail("Test expects 'reportSuccess' to be called");
            }
        };
        EventMessageHandler mockHandler = Mockito.mock(EventMessageHandler.class);
        EventHandlerInvoker eventHandlerInvoker = SimpleEventHandlerInvoker.builder().eventHandlers(mockHandler).build();
        AbstractEventProcessorTest.TestEventProcessor testSubject = AbstractEventProcessorTest.TestEventProcessor.builder().name("test").eventHandlerInvoker(eventHandlerInvoker).messageMonitor(messageMonitor).build();
        // Also test that the mechanism used to call the monitor can deal with the message in the unit of work being
        // modified during processing
        registerHandlerInterceptor(( unitOfWork, interceptorChain) -> {
            unitOfWork.transformMessage(( m) -> createEvent());
            return interceptorChain.proceed();
        });
        testSubject.processInBatchingUnitOfWork(events);
        TestCase.assertTrue("Not all events were presented to monitor", pending.isEmpty());
    }

    private static class TestEventProcessor extends AbstractEventProcessor {
        private TestEventProcessor(AbstractEventProcessorTest.TestEventProcessor.Builder builder) {
            super(builder);
        }

        private static AbstractEventProcessorTest.TestEventProcessor.Builder builder() {
            return new AbstractEventProcessorTest.TestEventProcessor.Builder();
        }

        @Override
        public void start() {
        }

        @Override
        public void shutDown() {
        }

        void processInBatchingUnitOfWork(List<? extends EventMessage<?>> eventMessages) throws Exception {
            processInUnitOfWork(eventMessages, new org.axonframework.messaging.unitofwork.BatchingUnitOfWork(eventMessages));
        }

        private static class Builder extends AbstractEventProcessor.Builder {
            public Builder() {
                super.rollbackConfiguration(ANY_THROWABLE);
            }

            @Override
            public AbstractEventProcessorTest.TestEventProcessor.Builder name(String name) {
                super.name(name);
                return this;
            }

            @Override
            public AbstractEventProcessorTest.TestEventProcessor.Builder eventHandlerInvoker(EventHandlerInvoker eventHandlerInvoker) {
                super.eventHandlerInvoker(eventHandlerInvoker);
                return this;
            }

            @Override
            public AbstractEventProcessorTest.TestEventProcessor.Builder messageMonitor(MessageMonitor<? super EventMessage<?>> messageMonitor) {
                super.messageMonitor(messageMonitor);
                return this;
            }

            private AbstractEventProcessorTest.TestEventProcessor build() {
                return new AbstractEventProcessorTest.TestEventProcessor(this);
            }
        }
    }
}

