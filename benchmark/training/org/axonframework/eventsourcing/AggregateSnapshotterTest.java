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


import java.util.UUID;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.utils.StubDomainEvent;
import org.axonframework.messaging.MetaData;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class AggregateSnapshotterTest {
    private AggregateSnapshotter testSubject;

    private AggregateFactory mockAggregateFactory;

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testCreateSnapshot() {
        String aggregateIdentifier = UUID.randomUUID().toString();
        DomainEventMessage firstEvent = new org.axonframework.eventhandling.GenericDomainEventMessage("type", aggregateIdentifier, ((long) (0)), "Mock contents", MetaData.emptyInstance());
        DomainEventStream eventStream = DomainEventStream.of(firstEvent);
        AggregateSnapshotterTest.StubAggregate aggregate = new AggregateSnapshotterTest.StubAggregate(aggregateIdentifier);
        Mockito.when(mockAggregateFactory.createAggregateRoot(aggregateIdentifier, firstEvent)).thenReturn(aggregate);
        DomainEventMessage snapshot = testSubject.createSnapshot(AggregateSnapshotterTest.StubAggregate.class, aggregateIdentifier, eventStream);
        Mockito.verify(mockAggregateFactory).createAggregateRoot(aggregateIdentifier, firstEvent);
        Assert.assertSame(aggregate, snapshot.getPayload());
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testCreateSnapshot_FirstEventLoadedIsSnapshotEvent() {
        UUID aggregateIdentifier = UUID.randomUUID();
        AggregateSnapshotterTest.StubAggregate aggregate = new AggregateSnapshotterTest.StubAggregate(aggregateIdentifier);
        DomainEventMessage<AggregateSnapshotterTest.StubAggregate> first = new org.axonframework.eventhandling.GenericDomainEventMessage("type", aggregate.getIdentifier(), 0, aggregate);
        DomainEventMessage second = new org.axonframework.eventhandling.GenericDomainEventMessage("type", aggregateIdentifier.toString(), 0, "Mock contents", MetaData.emptyInstance());
        DomainEventStream eventStream = DomainEventStream.of(first, second);
        Mockito.when(mockAggregateFactory.createAggregateRoot(ArgumentMatchers.any(), ArgumentMatchers.any(DomainEventMessage.class))).thenAnswer(( invocation) -> ((DomainEventMessage) (invocation.getArguments()[1])).getPayload());
        DomainEventMessage snapshot = testSubject.createSnapshot(AggregateSnapshotterTest.StubAggregate.class, aggregateIdentifier.toString(), eventStream);
        Assert.assertSame("Snapshotter did not recognize the aggregate snapshot", aggregate, snapshot.getPayload());
        Mockito.verify(mockAggregateFactory).createAggregateRoot(ArgumentMatchers.any(), ArgumentMatchers.any(DomainEventMessage.class));
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testCreateSnapshot_AggregateMarkedDeletedWillNotGenerateSnapshot() {
        String aggregateIdentifier = UUID.randomUUID().toString();
        DomainEventMessage firstEvent = new org.axonframework.eventhandling.GenericDomainEventMessage("type", aggregateIdentifier, ((long) (0)), "Mock contents", MetaData.emptyInstance());
        DomainEventMessage secondEvent = new org.axonframework.eventhandling.GenericDomainEventMessage("type", aggregateIdentifier, ((long) (0)), "deleted", MetaData.emptyInstance());
        DomainEventStream eventStream = DomainEventStream.of(firstEvent, secondEvent);
        AggregateSnapshotterTest.StubAggregate aggregate = new AggregateSnapshotterTest.StubAggregate(aggregateIdentifier);
        Mockito.when(mockAggregateFactory.createAggregateRoot(aggregateIdentifier, firstEvent)).thenReturn(aggregate);
        DomainEventMessage snapshot = testSubject.createSnapshot(AggregateSnapshotterTest.StubAggregate.class, aggregateIdentifier, eventStream);
        Mockito.verify(mockAggregateFactory).createAggregateRoot(aggregateIdentifier, firstEvent);
        Assert.assertNull("Snapshotter shouldn't have created snapshot of deleted aggregate", snapshot);
    }

    public static class StubAggregate {
        @AggregateIdentifier
        private String identifier;

        public StubAggregate() {
            identifier = UUID.randomUUID().toString();
        }

        public StubAggregate(Object identifier) {
            this.identifier = identifier.toString();
        }

        public void doSomething() {
            apply(new StubDomainEvent());
        }

        public String getIdentifier() {
            return identifier;
        }

        @EventSourcingHandler
        protected void handle(EventMessage event) {
            identifier = getAggregateIdentifier();
            // See Issue #
            if ("Mock contents".equals(event.getPayload().toString())) {
                apply("Another");
            }
            if ("deleted".equals(event.getPayload().toString())) {
                markDeleted();
            }
        }
    }
}

