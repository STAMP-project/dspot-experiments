/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.orm.jpa.support;


import TransactionDefinition.PROPAGATION_SUPPORTS;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.SynchronizationType;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.transaction.support.TransactionTemplate;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 4.1.2
 */
public class PersistenceContextTransactionTests {
    private EntityManagerFactory factory;

    private EntityManager manager;

    private EntityTransaction tx;

    private TransactionTemplate tt;

    private PersistenceContextTransactionTests.EntityManagerHoldingBean bean;

    @Test
    public void testTransactionCommitWithSharedEntityManager() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        tt.execute(( status) -> {
            bean.sharedEntityManager.flush();
            return null;
        });
        Mockito.verify(tx).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionCommitWithSharedEntityManagerAndPropagationSupports() {
        BDDMockito.given(manager.isOpen()).willReturn(true);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        tt.execute(( status) -> {
            bean.sharedEntityManager.clear();
            return null;
        });
        Mockito.verify(manager).clear();
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionCommitWithExtendedEntityManager() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        tt.execute(( status) -> {
            bean.extendedEntityManager.flush();
            return null;
        });
        Mockito.verify(tx, Mockito.times(2)).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionCommitWithExtendedEntityManagerAndPropagationSupports() {
        BDDMockito.given(manager.isOpen()).willReturn(true);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        tt.execute(( status) -> {
            bean.extendedEntityManager.flush();
            return null;
        });
        Mockito.verify(manager).flush();
    }

    @Test
    public void testTransactionCommitWithSharedEntityManagerUnsynchronized() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        tt.execute(( status) -> {
            bean.sharedEntityManagerUnsynchronized.flush();
            return null;
        });
        Mockito.verify(tx).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager, Mockito.times(2)).close();
    }

    @Test
    public void testTransactionCommitWithSharedEntityManagerUnsynchronizedAndPropagationSupports() {
        BDDMockito.given(manager.isOpen()).willReturn(true);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        tt.execute(( status) -> {
            bean.sharedEntityManagerUnsynchronized.clear();
            return null;
        });
        Mockito.verify(manager).clear();
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionCommitWithExtendedEntityManagerUnsynchronized() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        tt.execute(( status) -> {
            bean.extendedEntityManagerUnsynchronized.flush();
            return null;
        });
        Mockito.verify(tx).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionCommitWithExtendedEntityManagerUnsynchronizedAndPropagationSupports() {
        BDDMockito.given(manager.isOpen()).willReturn(true);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        tt.execute(( status) -> {
            bean.extendedEntityManagerUnsynchronized.flush();
            return null;
        });
        Mockito.verify(manager).flush();
    }

    @Test
    public void testTransactionCommitWithSharedEntityManagerUnsynchronizedJoined() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        tt.execute(( status) -> {
            bean.sharedEntityManagerUnsynchronized.joinTransaction();
            bean.sharedEntityManagerUnsynchronized.flush();
            return null;
        });
        Mockito.verify(tx).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager, Mockito.times(2)).close();
    }

    @Test
    public void testTransactionCommitWithExtendedEntityManagerUnsynchronizedJoined() {
        BDDMockito.given(manager.getTransaction()).willReturn(tx);
        tt.execute(( status) -> {
            bean.extendedEntityManagerUnsynchronized.joinTransaction();
            bean.extendedEntityManagerUnsynchronized.flush();
            return null;
        });
        Mockito.verify(tx, Mockito.times(2)).commit();
        Mockito.verify(manager).flush();
        Mockito.verify(manager).close();
    }

    @Test
    public void testTransactionCommitWithExtendedEntityManagerUnsynchronizedJoinedAndPropagationSupports() {
        BDDMockito.given(manager.isOpen()).willReturn(true);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        tt.execute(( status) -> {
            bean.extendedEntityManagerUnsynchronized.joinTransaction();
            bean.extendedEntityManagerUnsynchronized.flush();
            return null;
        });
        Mockito.verify(manager).flush();
    }

    public static class EntityManagerHoldingBean {
        @PersistenceContext
        public EntityManager sharedEntityManager;

        @PersistenceContext(type = PersistenceContextType.EXTENDED)
        public EntityManager extendedEntityManager;

        @PersistenceContext(synchronization = SynchronizationType.UNSYNCHRONIZED)
        public EntityManager sharedEntityManagerUnsynchronized;

        @PersistenceContext(type = PersistenceContextType.EXTENDED, synchronization = SynchronizationType.UNSYNCHRONIZED)
        public EntityManager extendedEntityManagerUnsynchronized;
    }
}

