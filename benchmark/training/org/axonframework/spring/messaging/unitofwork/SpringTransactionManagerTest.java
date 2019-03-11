/**
 * Copyright (c) 2010-2016. Axon Framework
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.spring.messaging.unitofwork;


import org.axonframework.common.transaction.Transaction;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


/**
 *
 *
 * @author Allard Buijze
 */
public class SpringTransactionManagerTest {
    private SpringTransactionManager testSubject;

    private PlatformTransactionManager transactionManager;

    private TransactionStatus underlyingTransactionStatus;

    @Test
    public void testManageTransaction_CustomTransactionStatus() {
        testSubject = new SpringTransactionManager(transactionManager, Mockito.mock(TransactionDefinition.class));
        testSubject.startTransaction().commit();
        Mockito.verify(transactionManager).getTransaction(ArgumentMatchers.any());
        Mockito.verify(transactionManager).commit(underlyingTransactionStatus);
    }

    @Test
    public void testManageTransaction_DefaultTransactionStatus() {
        testSubject.startTransaction().commit();
        Mockito.verify(transactionManager).getTransaction(ArgumentMatchers.isA(DefaultTransactionDefinition.class));
        Mockito.verify(transactionManager).commit(underlyingTransactionStatus);
    }

    @Test
    public void testCommitTransaction_NoCommitOnInactiveTransaction() {
        Transaction transaction = testSubject.startTransaction();
        Mockito.when(underlyingTransactionStatus.isCompleted()).thenReturn(true);
        transaction.commit();
        Mockito.verify(transactionManager, Mockito.never()).commit(underlyingTransactionStatus);
    }

    @Test
    public void testCommitTransaction_NoRollbackOnInactiveTransaction() {
        Transaction transaction = testSubject.startTransaction();
        Mockito.when(underlyingTransactionStatus.isCompleted()).thenReturn(true);
        transaction.rollback();
        Mockito.verify(transactionManager, Mockito.never()).rollback(underlyingTransactionStatus);
    }

    @Test
    public void testCommitTransaction_NoCommitOnNestedTransaction() {
        Transaction transaction = testSubject.startTransaction();
        Mockito.when(underlyingTransactionStatus.isNewTransaction()).thenReturn(false);
        transaction.commit();
        Mockito.verify(transactionManager, Mockito.never()).commit(underlyingTransactionStatus);
    }

    @Test
    public void testCommitTransaction_NoRollbackOnNestedTransaction() {
        Transaction transaction = testSubject.startTransaction();
        Mockito.when(underlyingTransactionStatus.isNewTransaction()).thenReturn(false);
        transaction.rollback();
        Mockito.verify(transactionManager, Mockito.never()).rollback(underlyingTransactionStatus);
    }
}

