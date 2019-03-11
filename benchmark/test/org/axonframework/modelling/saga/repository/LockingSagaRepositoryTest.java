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
package org.axonframework.modelling.saga.repository;


import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;
import org.axonframework.common.lock.Lock;
import org.axonframework.common.lock.LockFactory;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.modelling.saga.AssociationValue;
import org.axonframework.modelling.saga.Saga;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Rene de Waele
 */
public class LockingSagaRepositoryTest {
    private LockFactory lockFactory;

    private Lock lock;

    private LockingSagaRepository<Object> subject;

    @Test
    public void testLockReleasedOnUnitOfWorkCleanUpAfterCreate() {
        subject.createInstance("id", Object::new);
        Mockito.verify(lockFactory).obtainLock("id");
        Mockito.verify(subject).doCreateInstance(ArgumentMatchers.eq("id"), ArgumentMatchers.any());
        Mockito.verifyZeroInteractions(lock);
        CurrentUnitOfWork.commit();
        Mockito.verify(lock).release();
    }

    @Test
    public void testLockReleasedOnUnitOfWorkCleanUpAfterLoad() {
        subject.load("id");
        Mockito.verify(lockFactory).obtainLock("id");
        Mockito.verify(subject).doLoad("id");
        Mockito.verifyZeroInteractions(lock);
        CurrentUnitOfWork.commit();
        Mockito.verify(lock).release();
    }

    private static class CustomSagaRepository extends LockingSagaRepository<Object> {
        private final Saga<Object> saga;

        @SuppressWarnings("unchecked")
        private CustomSagaRepository(LockingSagaRepositoryTest.CustomSagaRepository.Builder builder) {
            super(builder);
            saga = Mockito.mock(Saga.class);
        }

        public static LockingSagaRepositoryTest.CustomSagaRepository.Builder builder() {
            return new LockingSagaRepositoryTest.CustomSagaRepository.Builder();
        }

        @Override
        public Set<String> find(AssociationValue associationValue) {
            return Collections.emptySet();
        }

        @Override
        protected Saga<Object> doLoad(String sagaIdentifier) {
            return saga;
        }

        @Override
        protected Saga<Object> doCreateInstance(String sagaIdentifier, Supplier<Object> factoryMethod) {
            return saga;
        }

        private static class Builder extends LockingSagaRepository.Builder<Object> {
            @Override
            public LockingSagaRepositoryTest.CustomSagaRepository.Builder lockFactory(LockFactory lockFactory) {
                super.lockFactory(lockFactory);
                return this;
            }

            public LockingSagaRepositoryTest.CustomSagaRepository build() {
                return new LockingSagaRepositoryTest.CustomSagaRepository(this);
            }
        }
    }
}

