/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventsourcing;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.utils.StubDomainEvent;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Allard Buijze
 */
public class EventSourcingRepositoryIntegrationTest implements Thread.UncaughtExceptionHandler {
    private static final int CONCURRENT_MODIFIERS = 10;

    private EventSourcingRepository<EventSourcingRepositoryIntegrationTest.SimpleAggregateRoot> repository;

    private String aggregateIdentifier;

    private EventStore eventStore;

    private List<Throwable> uncaughtExceptions = new CopyOnWriteArrayList<>();

    private List<Thread> startedThreads = new ArrayList<>();

    @Test(timeout = 60000)
    public void testPessimisticLocking() throws Throwable {
        initializeRepository();
        long lastSequenceNumber = executeConcurrentModifications(EventSourcingRepositoryIntegrationTest.CONCURRENT_MODIFIERS);
        // with pessimistic locking, all modifications are guaranteed successful
        // note: sequence number 20 means there are 21 events. This includes the one from the setup
        Assert.assertEquals((2 * (EventSourcingRepositoryIntegrationTest.CONCURRENT_MODIFIERS)), lastSequenceNumber);
        Assert.assertEquals(EventSourcingRepositoryIntegrationTest.CONCURRENT_MODIFIERS, getSuccessfulModifications());
    }

    private static class SimpleAggregateRoot {
        @AggregateIdentifier
        private String identifier;

        private SimpleAggregateRoot() {
            identifier = UUID.randomUUID().toString();
            AggregateLifecycle.apply(new StubDomainEvent());
        }

        private SimpleAggregateRoot(String identifier) {
            this.identifier = identifier;
        }

        private void doOperation() {
            AggregateLifecycle.apply(new StubDomainEvent());
        }

        @EventSourcingHandler
        protected void handle(EventMessage event) {
            identifier = ((DomainEventMessage<?>) (event)).getAggregateIdentifier();
        }

        public String getIdentifier() {
            return identifier;
        }
    }

    private static class SimpleAggregateFactory extends AbstractAggregateFactory<EventSourcingRepositoryIntegrationTest.SimpleAggregateRoot> {
        public SimpleAggregateFactory() {
            super(EventSourcingRepositoryIntegrationTest.SimpleAggregateRoot.class);
        }

        @Override
        public EventSourcingRepositoryIntegrationTest.SimpleAggregateRoot doCreateAggregate(String aggregateIdentifier, DomainEventMessage firstEvent) {
            return new EventSourcingRepositoryIntegrationTest.SimpleAggregateRoot(aggregateIdentifier);
        }
    }
}

