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
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.utils.StubDomainEvent;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.modelling.command.Aggregate;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.ConflictingAggregateVersionException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class EventSourcingRepositoryTest {
    private EventStore mockEventStore;

    private EventSourcingRepository<EventSourcingRepositoryTest.TestAggregate> testSubject;

    private UnitOfWork<?> unitOfWork;

    private EventSourcingRepositoryTest.StubAggregateFactory stubAggregateFactory;

    private SnapshotTriggerDefinition triggerDefinition;

    private SnapshotTrigger snapshotTrigger;

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadAndSaveAggregate() {
        String identifier = UUID.randomUUID().toString();
        DomainEventMessage event1 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, ((long) (1)), "Mock contents", emptyInstance());
        DomainEventMessage event2 = new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, ((long) (2)), "Mock contents", emptyInstance());
        Mockito.when(mockEventStore.readEvents(identifier)).thenReturn(DomainEventStream.of(event1, event2));
        Aggregate<EventSourcingRepositoryTest.TestAggregate> aggregate = testSubject.load(identifier, null);
        Assert.assertEquals(2, aggregate.invoke(EventSourcingRepositoryTest.TestAggregate::getHandledEvents).size());
        Assert.assertSame(event1, aggregate.invoke(EventSourcingRepositoryTest.TestAggregate::getHandledEvents).get(0));
        Assert.assertSame(event2, aggregate.invoke(EventSourcingRepositoryTest.TestAggregate::getHandledEvents).get(1));
        Assert.assertEquals(0, aggregate.invoke(EventSourcingRepositoryTest.TestAggregate::getLiveEvents).size());
        // now the aggregate is loaded (and hopefully correctly locked)
        StubDomainEvent event3 = new StubDomainEvent();
        aggregate.execute(( r) -> r.apply(event3));
        CurrentUnitOfWork.commit();
        Mockito.verify(mockEventStore, Mockito.times(1)).publish(((EventMessage) (ArgumentMatchers.anyVararg())));
        Assert.assertEquals(1, aggregate.invoke(EventSourcingRepositoryTest.TestAggregate::getLiveEvents).size());
        Assert.assertSame(event3, aggregate.invoke(EventSourcingRepositoryTest.TestAggregate::getLiveEvents).get(0).getPayload());
    }

    @Test
    public void testLoad_FirstEventIsSnapshot() {
        String identifier = UUID.randomUUID().toString();
        EventSourcingRepositoryTest.TestAggregate aggregate = new EventSourcingRepositoryTest.TestAggregate(identifier);
        Mockito.when(mockEventStore.readEvents(identifier)).thenReturn(DomainEventStream.of(new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, 10, aggregate)));
        Assert.assertSame(aggregate, testSubject.load(identifier).getWrappedAggregate().getAggregateRoot());
    }

    @Test
    public void testLoadWithConflictingChanges() {
        String identifier = UUID.randomUUID().toString();
        Mockito.when(mockEventStore.readEvents(identifier)).thenReturn(DomainEventStream.of(new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, ((long) (1)), "Mock contents", emptyInstance()), new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, ((long) (2)), "Mock contents", emptyInstance()), new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, ((long) (3)), "Mock contents", emptyInstance())));
        testSubject.load(identifier, 1L);
        try {
            CurrentUnitOfWork.commit();
            Assert.fail("Expected ConflictingAggregateVersionException");
        } catch (ConflictingAggregateVersionException e) {
            Assert.assertEquals(identifier, e.getAggregateIdentifier());
            Assert.assertEquals(1L, e.getExpectedVersion());
            Assert.assertEquals(3L, e.getActualVersion());
        }
    }

    @Test
    public void testLoadWithConflictingChanges_NoConflictResolverSet_UsingTooHighExpectedVersion() {
        String identifier = UUID.randomUUID().toString();
        Mockito.when(mockEventStore.readEvents(identifier)).thenReturn(DomainEventStream.of(new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, ((long) (1)), "Mock contents", emptyInstance()), new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, ((long) (2)), "Mock contents", emptyInstance()), new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, ((long) (3)), "Mock contents", emptyInstance())));
        try {
            testSubject.load(identifier, 100L);
            Assert.fail("Expected ConflictingAggregateVersionException");
        } catch (ConflictingAggregateVersionException e) {
            Assert.assertEquals(identifier, e.getAggregateIdentifier());
            Assert.assertEquals(100L, e.getExpectedVersion());
            Assert.assertEquals(3L, e.getActualVersion());
        }
    }

    @Test
    public void testLoadEventsWithSnapshotter() {
        String identifier = UUID.randomUUID().toString();
        Mockito.when(mockEventStore.readEvents(identifier)).thenReturn(DomainEventStream.of(new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, ((long) (1)), "Mock contents", emptyInstance()), new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, ((long) (2)), "Mock contents", emptyInstance()), new org.axonframework.eventhandling.GenericDomainEventMessage("type", identifier, ((long) (3)), "Mock contents", emptyInstance())));
        Aggregate<EventSourcingRepositoryTest.TestAggregate> aggregate = testSubject.load(identifier);
        aggregate.execute(( r) -> r.apply(new StubDomainEvent()));
        aggregate.execute(( r) -> r.apply(new StubDomainEvent()));
        InOrder inOrder = Mockito.inOrder(triggerDefinition, snapshotTrigger);
        inOrder.verify(triggerDefinition).prepareTrigger(stubAggregateFactory.getAggregateType());
        inOrder.verify(snapshotTrigger, Mockito.times(3)).eventHandled(ArgumentMatchers.any());
        inOrder.verify(snapshotTrigger).initializationFinished();
        inOrder.verify(snapshotTrigger, Mockito.times(2)).eventHandled(ArgumentMatchers.any());
    }

    private static class StubAggregateFactory extends AbstractAggregateFactory<EventSourcingRepositoryTest.TestAggregate> {
        public StubAggregateFactory() {
            super(EventSourcingRepositoryTest.TestAggregate.class);
        }

        @Override
        public EventSourcingRepositoryTest.TestAggregate doCreateAggregate(String aggregateIdentifier, DomainEventMessage firstEvent) {
            return new EventSourcingRepositoryTest.TestAggregate(aggregateIdentifier);
        }

        @Override
        public Class<EventSourcingRepositoryTest.TestAggregate> getAggregateType() {
            return EventSourcingRepositoryTest.TestAggregate.class;
        }
    }

    private static class TestAggregate {
        private List<EventMessage<?>> handledEvents = new ArrayList<>();

        private List<EventMessage<?>> liveEvents = new ArrayList<>();

        @AggregateIdentifier
        private String identifier;

        private TestAggregate(String identifier) {
            this.identifier = identifier;
        }

        public void apply(Object eventPayload) {
            AggregateLifecycle.apply(eventPayload);
        }

        public void changeState() {
            AggregateLifecycle.apply("Test more");
        }

        @EventSourcingHandler
        protected void handle(EventMessage event) {
            identifier = ((DomainEventMessage<?>) (event)).getAggregateIdentifier();
            handledEvents.add(event);
            if (AggregateLifecycle.isLive()) {
                liveEvents.add(event);
            }
        }

        public List<EventMessage<?>> getHandledEvents() {
            return handledEvents;
        }

        public List<EventMessage<?>> getLiveEvents() {
            return liveEvents;
        }

        public String getIdentifier() {
            return identifier;
        }
    }
}

