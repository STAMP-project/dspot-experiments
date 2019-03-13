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


import java.util.concurrent.Executor;
import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.utils.EventStoreTestUtils;
import org.axonframework.messaging.MetaData;
import org.axonframework.modelling.command.ConcurrencyException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static AbstractSnapshotter.Builder.<init>;


/**
 *
 *
 * @author Allard Buijze
 * @author Nakul Mishra
 */
public class AbstractSnapshotterTest {
    private AbstractSnapshotter testSubject;

    private EventStore mockEventStore;

    private Logger logger;

    private Logger originalLogger;

    @Test
    public void testScheduleSnapshot() {
        String aggregateIdentifier = "aggregateIdentifier";
        Mockito.when(mockEventStore.readEvents(aggregateIdentifier)).thenReturn(DomainEventStream.of(EventStoreTestUtils.createEvents(2)));
        testSubject.scheduleSnapshot(Object.class, aggregateIdentifier);
        Mockito.verify(mockEventStore).storeSnapshot(ArgumentMatchers.argThat(event(aggregateIdentifier, 1)));
    }

    @Test
    public void testScheduleSnapshot_ConcurrencyExceptionIsSilenced() {
        final String aggregateIdentifier = "aggregateIdentifier";
        Mockito.doNothing().doThrow(new ConcurrencyException("Mock")).when(mockEventStore).storeSnapshot(ArgumentMatchers.isA(DomainEventMessage.class));
        Mockito.when(mockEventStore.readEvents(aggregateIdentifier)).thenAnswer(( invocationOnMock) -> DomainEventStream.of(createEvents(2)));
        testSubject.scheduleSnapshot(Object.class, aggregateIdentifier);
        testSubject.scheduleSnapshot(Object.class, aggregateIdentifier);
        Mockito.verify(mockEventStore, Mockito.times(2)).storeSnapshot(ArgumentMatchers.argThat(event(aggregateIdentifier, 1)));
        Mockito.verify(logger, Mockito.never()).warn(ArgumentMatchers.anyString());
        Mockito.verify(logger, Mockito.never()).error(ArgumentMatchers.anyString());
    }

    @Test
    public void testScheduleSnapshot_SnapshotIsNull() {
        String aggregateIdentifier = "aggregateIdentifier";
        Mockito.when(mockEventStore.readEvents(aggregateIdentifier)).thenReturn(DomainEventStream.of(EventStoreTestUtils.createEvent()));
        testSubject.scheduleSnapshot(Object.class, aggregateIdentifier);
        Mockito.verify(mockEventStore, Mockito.never()).storeSnapshot(ArgumentMatchers.any(DomainEventMessage.class));
    }

    @Test
    public void testScheduleSnapshot_SnapshotReplacesOneEvent() {
        String aggregateIdentifier = "aggregateIdentifier";
        Mockito.when(mockEventStore.readEvents(aggregateIdentifier)).thenReturn(DomainEventStream.of(EventStoreTestUtils.createEvent(2)));
        testSubject.scheduleSnapshot(Object.class, aggregateIdentifier);
        Mockito.verify(mockEventStore, Mockito.never()).storeSnapshot(ArgumentMatchers.any(DomainEventMessage.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testScheduleSnapshot_WithTransaction() {
        Transaction mockTransaction = Mockito.mock(Transaction.class);
        TransactionManager txManager = Mockito.spy(new AbstractSnapshotterTest.StubTransactionManager(mockTransaction));
        Mockito.when(txManager.startTransaction()).thenReturn(mockTransaction);
        testSubject = AbstractSnapshotterTest.TestSnapshotter.builder().eventStore(mockEventStore).transactionManager(txManager).build();
        testScheduleSnapshot();
        InOrder inOrder = Mockito.inOrder(mockEventStore, txManager, mockTransaction);
        inOrder.verify(txManager).startTransaction();
        inOrder.verify(mockEventStore).readEvents(ArgumentMatchers.anyString());
        inOrder.verify(mockEventStore).storeSnapshot(ArgumentMatchers.isA(DomainEventMessage.class));
        inOrder.verify(mockTransaction).commit();
    }

    private static class TestSnapshotter extends AbstractSnapshotter {
        private TestSnapshotter(AbstractSnapshotterTest.TestSnapshotter.Builder builder) {
            super(builder);
        }

        private static AbstractSnapshotterTest.TestSnapshotter.Builder builder() {
            return new AbstractSnapshotterTest.TestSnapshotter.Builder();
        }

        @Override
        protected DomainEventMessage createSnapshot(Class<?> aggregateType, String aggregateIdentifier, DomainEventStream eventStream) {
            long lastIdentifier = getLastIdentifierFrom(eventStream);
            if (lastIdentifier <= 0) {
                return null;
            }
            return new org.axonframework.eventhandling.GenericDomainEventMessage("test", aggregateIdentifier, lastIdentifier, "Mock contents", MetaData.emptyInstance());
        }

        private long getLastIdentifierFrom(DomainEventStream eventStream) {
            long lastSequenceNumber = -1;
            while (eventStream.hasNext()) {
                lastSequenceNumber = eventStream.next().getSequenceNumber();
            } 
            return lastSequenceNumber;
        }

        private static class Builder extends AbstractSnapshotter.Builder {
            @Override
            public AbstractSnapshotterTest.TestSnapshotter.Builder eventStore(EventStore eventStore) {
                super.eventStore(eventStore);
                return this;
            }

            @Override
            public AbstractSnapshotterTest.TestSnapshotter.Builder executor(Executor executor) {
                super.executor(executor);
                return this;
            }

            @Override
            public AbstractSnapshotterTest.TestSnapshotter.Builder transactionManager(TransactionManager transactionManager) {
                super.transactionManager(transactionManager);
                return this;
            }

            private AbstractSnapshotterTest.TestSnapshotter build() {
                return new AbstractSnapshotterTest.TestSnapshotter(this);
            }
        }
    }

    private static class StubTransactionManager implements TransactionManager {
        private final Transaction transaction;

        private StubTransactionManager(Transaction transaction) {
            this.transaction = transaction;
        }

        @Override
        public Transaction startTransaction() {
            return transaction;
        }
    }
}

