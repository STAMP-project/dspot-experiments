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
package org.axonframework.spring.eventsourcing;


import java.util.Collections;
import java.util.concurrent.Executor;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.command.RepositoryProvider;
import org.axonframework.spring.config.annotation.StubAggregate;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.SimpleTransactionStatus;


/**
 *
 *
 * @author Allard Buijze
 * @author Nakul Mishra
 */
public class SpringAggregateSnapshotterFactoryBeanTest {
    private SpringAggregateSnapshotterFactoryBean testSubject;

    private PlatformTransactionManager mockTransactionManager;

    private String aggregateIdentifier;

    private EventStore mockEventStore;

    private RepositoryProvider mockRepositoryProvider;

    private ApplicationContext mockApplicationContext;

    private Executor executor;

    @Test
    public void testSnapshotCreatedNoTransaction() {
        SpringAggregateSnapshotter snapshotter = testSubject.getObject();
        snapshotter.scheduleSnapshot(StubAggregate.class, aggregateIdentifier);
        Mockito.verify(mockEventStore).storeSnapshot(eventSequence(1L));
        Mockito.verify(executor).execute(ArgumentMatchers.any());
    }

    @Test
    public void testRetrieveAggregateFactoryFromRepositoryIfNotExplicitlyAvailable() {
        testSubject.setEventStore(null);
        Mockito.reset(mockApplicationContext);
        Mockito.when(mockApplicationContext.getBean(EventStore.class)).thenReturn(mockEventStore);
        Mockito.when(mockApplicationContext.getBeansOfType(EventSourcingRepository.class)).thenReturn(Collections.singletonMap("myRepository", EventSourcingRepository.builder(StubAggregate.class).eventStore(mockEventStore).repositoryProvider(mockRepositoryProvider).build()));
        testSnapshotCreatedNoTransaction();
    }

    @Test
    public void testSnapshotCreatedNewlyCreatedTransactionCommitted() {
        testSubject.setTransactionManager(mockTransactionManager);
        SpringAggregateSnapshotter snapshotter = testSubject.getObject();
        SimpleTransactionStatus newlyCreatedTransaction = new SimpleTransactionStatus(true);
        Mockito.when(mockTransactionManager.getTransaction(ArgumentMatchers.isA(TransactionDefinition.class))).thenReturn(newlyCreatedTransaction);
        snapshotter.scheduleSnapshot(StubAggregate.class, aggregateIdentifier);
        Mockito.verify(mockEventStore).storeSnapshot(eventSequence(1L));
        Mockito.verify(mockTransactionManager).commit(newlyCreatedTransaction);
    }

    @Test
    public void testSnapshotCreatedExistingTransactionNotCommitted() {
        testSubject.setTransactionManager(mockTransactionManager);
        SpringAggregateSnapshotter snapshotter = testSubject.getObject();
        SimpleTransactionStatus existingTransaction = new SimpleTransactionStatus(false);
        Mockito.when(mockTransactionManager.getTransaction(ArgumentMatchers.isA(TransactionDefinition.class))).thenReturn(existingTransaction);
        snapshotter.scheduleSnapshot(StubAggregate.class, aggregateIdentifier);
        Mockito.verify(mockEventStore).storeSnapshot(eventSequence(1L));
        Mockito.verify(mockTransactionManager, Mockito.never()).commit(existingTransaction);
    }

    @Test
    public void testSnapshotCreatedExistingTransactionNotRolledBack() {
        testSubject.setTransactionManager(mockTransactionManager);
        SpringAggregateSnapshotter snapshotter = testSubject.getObject();
        SimpleTransactionStatus existingTransaction = new SimpleTransactionStatus(false);
        Mockito.when(mockTransactionManager.getTransaction(ArgumentMatchers.isA(TransactionDefinition.class))).thenReturn(existingTransaction);
        Mockito.doThrow(new RuntimeException("Stub")).when(mockEventStore).storeSnapshot(ArgumentMatchers.isA(DomainEventMessage.class));
        snapshotter.scheduleSnapshot(StubAggregate.class, aggregateIdentifier);
        Mockito.verify(mockEventStore).storeSnapshot(eventSequence(1L));
        Mockito.verify(mockTransactionManager, Mockito.never()).commit(existingTransaction);
        Mockito.verify(mockTransactionManager, Mockito.never()).rollback(existingTransaction);
    }

    @Test
    public void testSnapshotCreatedNewTransactionRolledBack() {
        testSubject.setTransactionManager(mockTransactionManager);
        SpringAggregateSnapshotter snapshotter = testSubject.getObject();
        SimpleTransactionStatus existingTransaction = new SimpleTransactionStatus(true);
        Mockito.when(mockTransactionManager.getTransaction(ArgumentMatchers.any())).thenReturn(existingTransaction);
        Mockito.doThrow(new RuntimeException("Stub")).when(mockEventStore).storeSnapshot(ArgumentMatchers.isA(DomainEventMessage.class));
        snapshotter.scheduleSnapshot(StubAggregate.class, aggregateIdentifier);
        Mockito.verify(mockEventStore).storeSnapshot(eventSequence(1L));
        Mockito.verify(mockTransactionManager, Mockito.never()).commit(existingTransaction);
        Mockito.verify(mockTransactionManager).rollback(existingTransaction);
    }

    public static class MockExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}

