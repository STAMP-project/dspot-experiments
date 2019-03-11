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
package org.axonframework.messaging.unitofwork;


import UnitOfWork.Phase;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.messaging.GenericResultMessage;
import org.axonframework.messaging.ResultMessage;
import org.axonframework.utils.MockException;
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
public class AbstractUnitOfWorkTest {
    private List<AbstractUnitOfWorkTest.PhaseTransition> phaseTransitions;

    private UnitOfWork<?> subject;

    @Test
    public void testHandlersForCurrentPhaseAreExecuted() {
        AtomicBoolean prepareCommit = new AtomicBoolean();
        AtomicBoolean commit = new AtomicBoolean();
        AtomicBoolean afterCommit = new AtomicBoolean();
        AtomicBoolean cleanup = new AtomicBoolean();
        subject.onPrepareCommit(( u) -> subject.onPrepareCommit(( i) -> prepareCommit.set(true)));
        subject.onCommit(( u) -> subject.onCommit(( i) -> commit.set(true)));
        subject.afterCommit(( u) -> subject.afterCommit(( i) -> afterCommit.set(true)));
        subject.onCleanup(( u) -> subject.onCleanup(( i) -> cleanup.set(true)));
        subject.start();
        subject.commit();
        Assert.assertTrue(prepareCommit.get());
        Assert.assertTrue(commit.get());
        Assert.assertTrue(afterCommit.get());
        Assert.assertTrue(cleanup.get());
    }

    @Test
    public void testExecuteTask() {
        Runnable task = Mockito.mock(Runnable.class);
        Mockito.doNothing().when(task).run();
        subject.execute(task);
        InOrder inOrder = Mockito.inOrder(task, subject);
        inOrder.verify(subject).start();
        inOrder.verify(task).run();
        inOrder.verify(subject).commit();
        Assert.assertFalse(subject.isActive());
    }

    @Test
    public void testExecuteFailingTask() {
        Runnable task = Mockito.mock(Runnable.class);
        MockException mockException = new MockException();
        Mockito.doThrow(mockException).when(task).run();
        try {
            subject.execute(task);
        } catch (MockException e) {
            InOrder inOrder = Mockito.inOrder(task, subject);
            inOrder.verify(subject).start();
            inOrder.verify(task).run();
            inOrder.verify(subject).rollback(e);
            Assert.assertNotNull(subject.getExecutionResult());
            Assert.assertSame(mockException, subject.getExecutionResult().getExceptionResult());
            return;
        }
        throw new AssertionError();
    }

    @Test
    public void testExecuteTaskWithResult() throws Exception {
        Object taskResult = new Object();
        Callable<Object> task = Mockito.mock(Callable.class);
        Mockito.when(task.call()).thenReturn(taskResult);
        ResultMessage result = subject.executeWithResult(task);
        InOrder inOrder = Mockito.inOrder(task, subject);
        inOrder.verify(subject).start();
        inOrder.verify(task).call();
        inOrder.verify(subject).commit();
        Assert.assertFalse(subject.isActive());
        Assert.assertSame(taskResult, result.getPayload());
        Assert.assertNotNull(subject.getExecutionResult());
        Assert.assertSame(taskResult, subject.getExecutionResult().getResult().getPayload());
    }

    @Test
    public void testExecuteTaskReturnsResultMessage() throws Exception {
        ResultMessage<Object> resultMessage = GenericResultMessage.asResultMessage(new Object());
        Callable<ResultMessage<Object>> task = Mockito.mock(Callable.class);
        Mockito.when(task.call()).thenReturn(resultMessage);
        ResultMessage actualResultMessage = subject.executeWithResult(task);
        Assert.assertSame(resultMessage, actualResultMessage);
    }

    @Test
    public void testAttachedTransactionCommittedOnUnitOfWorkCommit() {
        TransactionManager transactionManager = Mockito.mock(TransactionManager.class);
        Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(transactionManager.startTransaction()).thenReturn(transaction);
        subject.attachTransaction(transactionManager);
        subject.start();
        Mockito.verify(transactionManager).startTransaction();
        Mockito.verify(transaction, Mockito.never()).commit();
        subject.commit();
        Mockito.verify(transaction).commit();
    }

    @Test
    public void testAttachedTransactionRolledBackOnUnitOfWorkRollBack() {
        TransactionManager transactionManager = Mockito.mock(TransactionManager.class);
        Transaction transaction = Mockito.mock(Transaction.class);
        Mockito.when(transactionManager.startTransaction()).thenReturn(transaction);
        subject.attachTransaction(transactionManager);
        subject.start();
        Mockito.verify(transactionManager).startTransaction();
        Mockito.verify(transaction, Mockito.never()).commit();
        Mockito.verify(transaction, Mockito.never()).rollback();
        subject.rollback();
        Mockito.verify(transaction).rollback();
        Mockito.verify(transaction, Mockito.never()).commit();
    }

    @Test
    public void unitOfWorkIsRolledBackWhenTransactionFailsToStart() {
        TransactionManager transactionManager = Mockito.mock(TransactionManager.class);
        Mockito.when(transactionManager.startTransaction()).thenThrow(new MockException());
        try {
            subject.attachTransaction(transactionManager);
            Assert.fail("Expected MockException to be propagated");
        } catch (Exception e) {
            // expected
        }
        Mockito.verify(subject).rollback(ArgumentMatchers.isA(MockException.class));
    }

    private static class PhaseTransition {
        private final Phase phase;

        private final UnitOfWork<?> unitOfWork;

        public PhaseTransition(UnitOfWork<?> unitOfWork, UnitOfWork.Phase phase) {
            this.unitOfWork = unitOfWork;
            this.phase = phase;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            AbstractUnitOfWorkTest.PhaseTransition that = ((AbstractUnitOfWorkTest.PhaseTransition) (o));
            return (Objects.equals(phase, that.phase)) && (Objects.equals(unitOfWork, that.unitOfWork));
        }

        @Override
        public int hashCode() {
            return Objects.hash(phase, unitOfWork);
        }

        @Override
        public String toString() {
            return ((unitOfWork) + " ") + (phase);
        }
    }
}

