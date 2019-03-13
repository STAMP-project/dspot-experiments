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
package org.axonframework.integrationtests.modelling.command;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.axonframework.common.Registration;
import org.axonframework.eventhandling.AbstractEventBus;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.TrackingEventStream;
import org.axonframework.eventhandling.TrackingToken;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.integrationtests.utils.StubAggregate;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.junit.Assert;
import org.junit.Test;

import static AbstractEventBus.Builder.<init>;


public class CommandHandlingTest {
    private EventSourcingRepository<StubAggregate> repository;

    private String aggregateIdentifier;

    private CommandHandlingTest.StubEventStore stubEventStore;

    @Test
    public void testCommandHandlerLoadsSameAggregateTwice() throws Exception {
        DefaultUnitOfWork.startAndGet(null);
        repository.newInstance(() -> new StubAggregate(aggregateIdentifier)).execute(StubAggregate::doSomething);
        CurrentUnitOfWork.commit();
        DefaultUnitOfWork.startAndGet(null);
        repository.load(aggregateIdentifier).execute(StubAggregate::doSomething);
        repository.load(aggregateIdentifier).execute(StubAggregate::doSomething);
        CurrentUnitOfWork.commit();
        Iterator<? extends DomainEventMessage<?>> es = stubEventStore.readEvents(aggregateIdentifier);
        Assert.assertTrue(es.hasNext());
        Assert.assertEquals(((Object) (0L)), getSequenceNumber());
        Assert.assertTrue(es.hasNext());
        Assert.assertEquals(((Object) (1L)), getSequenceNumber());
        Assert.assertTrue(es.hasNext());
        Assert.assertEquals(((Object) (2L)), getSequenceNumber());
        Assert.assertFalse(es.hasNext());
    }

    private static class StubEventStore extends AbstractEventBus implements EventStore {
        private List<DomainEventMessage<?>> storedEvents = new LinkedList<>();

        private StubEventStore(CommandHandlingTest.StubEventStore.Builder builder) {
            super(builder);
        }

        private static CommandHandlingTest.StubEventStore.Builder builder() {
            return new CommandHandlingTest.StubEventStore.Builder();
        }

        @Override
        public DomainEventStream readEvents(String identifier) {
            return DomainEventStream.of(new java.util.ArrayList(storedEvents));
        }

        @Override
        protected void commit(List<? extends EventMessage<?>> events) {
            storedEvents.addAll(events.stream().map(EventUtils::asDomainEventMessage).collect(Collectors.toList()));
        }

        @Override
        public void storeSnapshot(DomainEventMessage<?> snapshot) {
        }

        @Override
        public TrackingEventStream openStream(TrackingToken trackingToken) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Registration subscribe(Consumer<List<? extends EventMessage<?>>> eventProcessor) {
            throw new UnsupportedOperationException();
        }

        private static class Builder extends AbstractEventBus.Builder {
            private CommandHandlingTest.StubEventStore build() {
                return new CommandHandlingTest.StubEventStore(this);
            }
        }
    }
}

