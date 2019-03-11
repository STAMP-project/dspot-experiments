/**
 * Copyright (c) 2010-2018. Axon Framework
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
package org.axonframework.integrationtests.loopbacktest.synchronous;


import PropagatingErrorHandler.INSTANCE;
import org.axonframework.common.lock.PessimisticLockFactory;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.Repository;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for issue #119
 *
 * @author Allard Buijze
 */
public class SynchronousLoopbackTest {
    private CommandBus commandBus;

    private String aggregateIdentifier;

    private EventStore eventStore;

    private CommandCallback<Object, Object> reportErrorCallback;

    private CommandCallback<Object, Object> expectErrorCallback;

    @Test
    public void testLoopBackKeepsProperEventOrder_PessimisticLocking() {
        initializeRepository(PessimisticLockFactory.usingDefaults());
        EventMessageHandler eventHandler = ( event) -> {
            DomainEventMessage domainEvent = ((DomainEventMessage) (event));
            if ((event.getPayload()) instanceof org.axonframework.integrationtests.loopbacktest.synchronous.CounterChangedEvent) {
                org.axonframework.integrationtests.loopbacktest.synchronous.CounterChangedEvent counterChangedEvent = ((org.axonframework.integrationtests.loopbacktest.synchronous.CounterChangedEvent) (event.getPayload()));
                if ((counterChangedEvent.getCounter()) == 1) {
                    commandBus.dispatch(asCommandMessage(new org.axonframework.integrationtests.loopbacktest.synchronous.ChangeCounterCommand(domainEvent.getAggregateIdentifier(), ((counterChangedEvent.getCounter()) + 1))), reportErrorCallback);
                    commandBus.dispatch(asCommandMessage(new org.axonframework.integrationtests.loopbacktest.synchronous.ChangeCounterCommand(domainEvent.getAggregateIdentifier(), ((counterChangedEvent.getCounter()) + 2))), reportErrorCallback);
                }
            }
            return null;
        };
        SimpleEventHandlerInvoker eventHandlerInvoker = SimpleEventHandlerInvoker.builder().eventHandlers(eventHandler).build();
        SubscribingEventProcessor.builder().name("processor").eventHandlerInvoker(eventHandlerInvoker).messageSource(eventStore).build().start();
        commandBus.dispatch(asCommandMessage(new SynchronousLoopbackTest.ChangeCounterCommand(aggregateIdentifier, 1)), reportErrorCallback);
        DomainEventStream storedEvents = eventStore.readEvents(aggregateIdentifier);
        Assert.assertTrue(storedEvents.hasNext());
        // noinspection Duplicates
        while (storedEvents.hasNext()) {
            DomainEventMessage next = storedEvents.next();
            if ((next.getPayload()) instanceof SynchronousLoopbackTest.CounterChangedEvent) {
                SynchronousLoopbackTest.CounterChangedEvent event = ((SynchronousLoopbackTest.CounterChangedEvent) (next.getPayload()));
                Assert.assertEquals(event.getCounter(), next.getSequenceNumber());
            }
        } 
        Mockito.verify(eventStore, Mockito.times(3)).publish(SynchronousLoopbackTest.anyEventList());
    }

    @Test
    public void testLoopBackKeepsProperEventOrder_PessimisticLocking_ProcessingFails() {
        initializeRepository(PessimisticLockFactory.usingDefaults());
        EventMessageHandler eventHandler = ( event) -> {
            DomainEventMessage domainEvent = ((DomainEventMessage) (event));
            if ((event.getPayload()) instanceof org.axonframework.integrationtests.loopbacktest.synchronous.CounterChangedEvent) {
                org.axonframework.integrationtests.loopbacktest.synchronous.CounterChangedEvent counterChangedEvent = ((org.axonframework.integrationtests.loopbacktest.synchronous.CounterChangedEvent) (event.getPayload()));
                if ((counterChangedEvent.getCounter()) == 1) {
                    commandBus.dispatch(asCommandMessage(new org.axonframework.integrationtests.loopbacktest.synchronous.ChangeCounterCommand(domainEvent.getAggregateIdentifier(), ((counterChangedEvent.getCounter()) + 1))), reportErrorCallback);
                    commandBus.dispatch(asCommandMessage(new org.axonframework.integrationtests.loopbacktest.synchronous.ChangeCounterCommand(domainEvent.getAggregateIdentifier(), ((counterChangedEvent.getCounter()) + 2))), reportErrorCallback);
                } else
                    if ((counterChangedEvent.getCounter()) == 2) {
                        throw new RuntimeException("Mock exception");
                    }

            }
            return null;
        };
        SimpleEventHandlerInvoker eventHandlerInvoker = SimpleEventHandlerInvoker.builder().eventHandlers(eventHandler).listenerInvocationErrorHandler(INSTANCE).build();
        SubscribingEventProcessor.builder().name("processor").eventHandlerInvoker(eventHandlerInvoker).messageSource(eventStore).build().start();
        commandBus.dispatch(asCommandMessage(new SynchronousLoopbackTest.ChangeCounterCommand(aggregateIdentifier, 1)), expectErrorCallback);
        DomainEventStream storedEvents = eventStore.readEvents(aggregateIdentifier);
        Assert.assertTrue(storedEvents.hasNext());
        while (storedEvents.hasNext()) {
            DomainEventMessage next = storedEvents.next();
            if ((next.getPayload()) instanceof SynchronousLoopbackTest.CounterChangedEvent) {
                SynchronousLoopbackTest.CounterChangedEvent event = ((SynchronousLoopbackTest.CounterChangedEvent) (next.getPayload()));
                Assert.assertEquals(event.getCounter(), next.getSequenceNumber());
            }
        } 
        Mockito.verify(eventStore, Mockito.times(3)).publish(SynchronousLoopbackTest.anyEventList());
    }

    private static class CounterCommandHandler {
        private Repository<SynchronousLoopbackTest.CountingAggregate> repository;

        private CounterCommandHandler(Repository<SynchronousLoopbackTest.CountingAggregate> repository) {
            this.repository = repository;
        }

        @CommandHandler
        @SuppressWarnings("unchecked")
        public void changeCounter(SynchronousLoopbackTest.ChangeCounterCommand command) {
            repository.load(command.getAggregateId()).execute(( r) -> r.setCounter(command.getNewValue()));
        }
    }

    private static class ChangeCounterCommand {
        private String aggregateId;

        private int newValue;

        private ChangeCounterCommand(String aggregateId, int newValue) {
            this.aggregateId = aggregateId;
            this.newValue = newValue;
        }

        public String getAggregateId() {
            return aggregateId;
        }

        public int getNewValue() {
            return newValue;
        }
    }

    private static class AggregateCreatedEvent {
        private final String aggregateIdentifier;

        private AggregateCreatedEvent(String aggregateIdentifier) {
            this.aggregateIdentifier = aggregateIdentifier;
        }

        public String getAggregateIdentifier() {
            return aggregateIdentifier;
        }
    }

    private static class CountingAggregate {
        private static final long serialVersionUID = -2927751585905120260L;

        private int counter = 0;

        @AggregateIdentifier
        private String identifier;

        private CountingAggregate(String identifier) {
            apply(new SynchronousLoopbackTest.AggregateCreatedEvent(identifier));
        }

        CountingAggregate() {
        }

        public void setCounter(int newValue) {
            apply(new SynchronousLoopbackTest.CounterChangedEvent(newValue));
        }

        @EventSourcingHandler
        private void handleCreatedEvent(SynchronousLoopbackTest.AggregateCreatedEvent event) {
            this.identifier = event.getAggregateIdentifier();
        }

        @EventSourcingHandler
        private void handleCounterIncreased(SynchronousLoopbackTest.CounterChangedEvent event) {
            this.counter = event.getCounter();
        }
    }

    private static class CounterChangedEvent {
        private final int counter;

        private CounterChangedEvent(int counter) {
            this.counter = counter;
        }

        public int getCounter() {
            return counter;
        }
    }
}

