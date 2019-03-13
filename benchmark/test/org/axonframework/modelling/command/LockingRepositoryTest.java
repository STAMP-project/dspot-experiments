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
package org.axonframework.modelling.command;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import junit.framework.TestCase;
import org.axonframework.common.lock.Lock;
import org.axonframework.common.lock.LockFactory;
import org.axonframework.common.lock.PessimisticLockFactory;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.modelling.command.inspection.AggregateModel;
import org.axonframework.modelling.command.inspection.AnnotatedAggregate;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class LockingRepositoryTest {
    private Repository<StubAggregate> testSubject;

    private EventBus mockEventBus;

    private LockFactory lockFactory;

    private Lock lock;

    private static final Message<?> MESSAGE = new org.axonframework.messaging.GenericMessage<Object>("test");

    @Test
    public void testStoreNewAggregate() throws Exception {
        startAndGetUnitOfWork();
        StubAggregate aggregate = new StubAggregate();
        testSubject.newInstance(() -> aggregate).execute(StubAggregate::doSomething);
        CurrentUnitOfWork.commit();
        Mockito.verify(lockFactory).obtainLock(aggregate.getIdentifier());
        Mockito.verify(mockEventBus).publish(ArgumentMatchers.isA(EventMessage.class));
    }

    @Test
    public void testLoadAndStoreAggregate() throws Exception {
        startAndGetUnitOfWork();
        StubAggregate aggregate = new StubAggregate();
        testSubject.newInstance(() -> aggregate).execute(StubAggregate::doSomething);
        Mockito.verify(lockFactory).obtainLock(aggregate.getIdentifier());
        CurrentUnitOfWork.commit();
        Mockito.verify(lock).release();
        Mockito.reset(lockFactory);
        startAndGetUnitOfWork();
        Aggregate<StubAggregate> loadedAggregate = testSubject.load(aggregate.getIdentifier(), 0L);
        Mockito.verify(lockFactory).obtainLock(aggregate.getIdentifier());
        loadedAggregate.execute(StubAggregate::doSomething);
        CurrentUnitOfWork.commit();
        Mockito.verify(mockEventBus, Mockito.times(2)).publish(ArgumentMatchers.any(EventMessage.class));
        Mockito.verify(lock).release();
    }

    @Test
    public void testLoadAndStoreAggregate_LockReleasedOnException() throws Exception {
        startAndGetUnitOfWork();
        StubAggregate aggregate = new StubAggregate();
        testSubject.newInstance(() -> aggregate).execute(StubAggregate::doSomething);
        Mockito.verify(lockFactory).obtainLock(aggregate.getIdentifier());
        CurrentUnitOfWork.commit();
        Mockito.verify(lock).release();
        Mockito.reset(lockFactory);
        startAndGetUnitOfWork();
        testSubject.load(aggregate.getIdentifier(), 0L);
        Mockito.verify(lockFactory).obtainLock(aggregate.getIdentifier());
        CurrentUnitOfWork.get().onPrepareCommit(( u) -> {
            throw new RuntimeException("Mock Exception");
        });
        try {
            CurrentUnitOfWork.commit();
            Assert.fail("Expected exception to be thrown");
        } catch (RuntimeException e) {
            TestCase.assertEquals("Mock Exception", e.getMessage());
        }
        // make sure the lock is released
        Mockito.verify(lock).release();
    }

    @Test
    public void testLoadAndStoreAggregate_PessimisticLockReleasedOnException() throws Exception {
        lockFactory = Mockito.spy(PessimisticLockFactory.usingDefaults());
        testSubject = LockingRepositoryTest.InMemoryLockingRepository.builder().lockFactory(lockFactory).eventStore(mockEventBus).build();
        testSubject = Mockito.spy(testSubject);
        // we do the same test, but with a pessimistic lock, which has a different way of "re-acquiring" a lost lock
        startAndGetUnitOfWork();
        StubAggregate aggregate = new StubAggregate();
        Mockito.when(lockFactory.obtainLock(aggregate.getIdentifier())).thenAnswer(( invocation) -> lock = spy(((Lock) (invocation.callRealMethod()))));
        testSubject.newInstance(() -> aggregate).execute(StubAggregate::doSomething);
        Mockito.verify(lockFactory).obtainLock(aggregate.getIdentifier());
        CurrentUnitOfWork.commit();
        Mockito.verify(lock).release();
        Mockito.reset(lockFactory);
        startAndGetUnitOfWork();
        testSubject.load(aggregate.getIdentifier(), 0L);
        Mockito.verify(lockFactory).obtainLock(aggregate.getIdentifier());
        CurrentUnitOfWork.get().onPrepareCommit(( u) -> {
            throw new RuntimeException("Mock Exception");
        });
        try {
            CurrentUnitOfWork.commit();
            Assert.fail("Expected exception to be thrown");
        } catch (RuntimeException e) {
            TestCase.assertEquals("Mock Exception", e.getMessage());
        }
        // make sure the lock is released
        Mockito.verify(lock).release();
    }

    private static class InMemoryLockingRepository extends LockingRepository<StubAggregate, Aggregate<StubAggregate>> {
        private final EventBus eventBus;

        private final AggregateModel<StubAggregate> aggregateModel;

        private Map<Object, Aggregate<StubAggregate>> store = new HashMap<>();

        private int saveCount;

        private InMemoryLockingRepository(LockingRepositoryTest.InMemoryLockingRepository.Builder builder) {
            super(builder);
            this.eventBus = builder.eventStore;
            this.aggregateModel = buildAggregateModel();
        }

        public static LockingRepositoryTest.InMemoryLockingRepository.Builder builder() {
            return new LockingRepositoryTest.InMemoryLockingRepository.Builder();
        }

        @Override
        protected void doSaveWithLock(Aggregate<StubAggregate> aggregate) {
            store.put(aggregate.identifierAsString(), aggregate);
            (saveCount)++;
        }

        @Override
        protected void doDeleteWithLock(Aggregate<StubAggregate> aggregate) {
            store.remove(aggregate.identifierAsString());
            (saveCount)++;
        }

        @Override
        protected Aggregate<StubAggregate> doLoadWithLock(String aggregateIdentifier, Long expectedVersion) {
            return store.get(aggregateIdentifier);
        }

        @Override
        protected Aggregate<StubAggregate> doCreateNewForLock(Callable<StubAggregate> factoryMethod) throws Exception {
            return AnnotatedAggregate.initialize(factoryMethod, aggregateModel, eventBus);
        }

        public int getSaveCount() {
            return saveCount;
        }

        public void resetSaveCount() {
            saveCount = 0;
        }

        private static class Builder extends LockingRepository.Builder<StubAggregate> {
            private EventBus eventStore;

            private Builder() {
                super(StubAggregate.class);
            }

            @Override
            public LockingRepositoryTest.InMemoryLockingRepository.Builder lockFactory(LockFactory lockFactory) {
                super.lockFactory(lockFactory);
                return this;
            }

            public LockingRepositoryTest.InMemoryLockingRepository.Builder eventStore(EventBus eventStore) {
                this.eventStore = eventStore;
                return this;
            }

            public LockingRepositoryTest.InMemoryLockingRepository build() {
                return new LockingRepositoryTest.InMemoryLockingRepository(this);
            }
        }
    }
}

