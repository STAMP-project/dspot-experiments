/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.transaction;


import JtaTransactionManager.SYNCHRONIZATION_ALWAYS;
import JtaTransactionManager.SYNCHRONIZATION_NEVER;
import JtaTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION;
import Status.STATUS_ACTIVE;
import Status.STATUS_NO_TRANSACTION;
import Status.STATUS_ROLLEDBACK;
import TransactionDefinition.ISOLATION_SERIALIZABLE;
import TransactionDefinition.PROPAGATION_NESTED;
import TransactionDefinition.PROPAGATION_NOT_SUPPORTED;
import TransactionDefinition.PROPAGATION_REQUIRES_NEW;
import TransactionDefinition.PROPAGATION_SUPPORTS;
import TransactionSynchronization.STATUS_COMMITTED;
import TransactionSynchronization.STATUS_ROLLED_BACK;
import TransactionSynchronization.STATUS_UNKNOWN;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.tests.transaction.MockJtaTransaction;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import static HeuristicCompletionException.STATE_MIXED;
import static HeuristicCompletionException.STATE_ROLLED_BACK;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 12.05.2003
 */
public class JtaTransactionManagerTests {
    @Test
    public void jtaTransactionManagerWithCommit() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setName("txName");
        Assert.assertEquals(SYNCHRONIZATION_ALWAYS, ptm.getTransactionSynchronization());
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertNull(TransactionSynchronizationManager.getCurrentTransactionName());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // something transactional
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                TransactionSynchronizationManager.registerSynchronization(synch);
                Assert.assertEquals("txName", TransactionSynchronizationManager.getCurrentTransactionName());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertNull(TransactionSynchronizationManager.getCurrentTransactionName());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Mockito.verify(ut).begin();
        Mockito.verify(ut).commit();
        Mockito.verify(synch).beforeCommit(false);
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCommit();
        Mockito.verify(synch).afterCompletion(STATUS_COMMITTED);
    }

    @Test
    public void jtaTransactionManagerWithCommitAndSynchronizationOnActual() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        ptm.setTransactionSynchronization(SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // something transactional
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                TransactionSynchronizationManager.registerSynchronization(synch);
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).begin();
        Mockito.verify(ut).commit();
        Mockito.verify(synch).beforeCommit(false);
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCommit();
        Mockito.verify(synch).afterCompletion(STATUS_COMMITTED);
    }

    @Test
    public void jtaTransactionManagerWithCommitAndSynchronizationNever() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        ptm.setTransactionSynchronization(SYNCHRONIZATION_NEVER);
        ptm.afterPropertiesSet();
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).begin();
        Mockito.verify(ut).commit();
    }

    @Test
    public void jtaTransactionManagerWithRollback() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setTimeout(10);
        tt.setName("txName");
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertNull(TransactionSynchronizationManager.getCurrentTransactionName());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                TransactionSynchronizationManager.registerSynchronization(synch);
                Assert.assertEquals("txName", TransactionSynchronizationManager.getCurrentTransactionName());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertNull(TransactionSynchronizationManager.getCurrentTransactionName());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Mockito.verify(ut).setTransactionTimeout(10);
        Mockito.verify(ut).begin();
        Mockito.verify(ut).rollback();
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCompletion(STATUS_ROLLED_BACK);
    }

    @Test
    public void jtaTransactionManagerWithRollbackAndSynchronizationOnActual() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        ptm.setTransactionSynchronization(SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        tt.setTimeout(10);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                TransactionSynchronizationManager.registerSynchronization(synch);
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).setTransactionTimeout(10);
        Mockito.verify(ut).begin();
        Mockito.verify(ut).rollback();
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCompletion(STATUS_ROLLED_BACK);
    }

    @Test
    public void jtaTransactionManagerWithRollbackAndSynchronizationNever() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        ptm.setTransactionSynchronizationName("SYNCHRONIZATION_NEVER");
        tt.setTimeout(10);
        ptm.afterPropertiesSet();
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).setTransactionTimeout(10);
        Mockito.verify(ut).begin();
        Mockito.verify(ut, Mockito.atLeastOnce()).getStatus();
        Mockito.verify(ut).rollback();
    }

    @Test
    public void jtaTransactionManagerWithExistingTransactionAndRollbackOnly() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                TransactionSynchronizationManager.registerSynchronization(synch);
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).setRollbackOnly();
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCompletion(STATUS_UNKNOWN);
    }

    @Test
    public void jtaTransactionManagerWithExistingTransactionAndException() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                    TransactionSynchronizationManager.registerSynchronization(synch);
                    throw new IllegalStateException("I want a rollback");
                }
            });
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).setRollbackOnly();
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCompletion(STATUS_UNKNOWN);
    }

    @Test
    public void jtaTransactionManagerWithExistingTransactionAndCommitException() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        BDDMockito.willThrow(new OptimisticLockingFailureException("")).given(synch).beforeCommit(false);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                    TransactionSynchronizationManager.registerSynchronization(synch);
                }
            });
            Assert.fail("Should have thrown OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException ex) {
            // expected
        }
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).setRollbackOnly();
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCompletion(STATUS_UNKNOWN);
    }

    @Test
    public void jtaTransactionManagerWithExistingTransactionAndRollbackOnlyAndNoGlobalRollback() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        ptm.setGlobalRollbackOnParticipationFailure(false);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                TransactionSynchronizationManager.registerSynchronization(synch);
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).setRollbackOnly();
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCompletion(STATUS_UNKNOWN);
    }

    @Test
    public void jtaTransactionManagerWithExistingTransactionAndExceptionAndNoGlobalRollback() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        ptm.setGlobalRollbackOnParticipationFailure(false);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                    TransactionSynchronizationManager.registerSynchronization(synch);
                    throw new IllegalStateException("I want a rollback");
                }
            });
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException ex) {
            // expected
        }
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCompletion(STATUS_UNKNOWN);
    }

    @Test
    public void jtaTransactionManagerWithExistingTransactionAndJtaSynchronization() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        TransactionManager tm = Mockito.mock(TransactionManager.class);
        MockJtaTransaction tx = new MockJtaTransaction();
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        BDDMockito.given(tm.getTransaction()).willReturn(tx);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut, tm);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                TransactionSynchronizationManager.registerSynchronization(synch);
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertNotNull(tx.getSynchronization());
        tx.getSynchronization().beforeCompletion();
        tx.getSynchronization().afterCompletion(STATUS_ROLLEDBACK);
        Mockito.verify(ut).setRollbackOnly();
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCompletion(STATUS_ROLLED_BACK);
    }

    @Test
    public void jtaTransactionManagerWithExistingTransactionAndSynchronizationOnActual() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        ptm.setTransactionSynchronization(SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                TransactionSynchronizationManager.registerSynchronization(synch);
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).setRollbackOnly();
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCompletion(STATUS_UNKNOWN);
    }

    @Test
    public void jtaTransactionManagerWithExistingTransactionAndSynchronizationNever() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        ptm.setTransactionSynchronization(SYNCHRONIZATION_NEVER);
        ptm.afterPropertiesSet();
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).setRollbackOnly();
    }

    @Test
    public void jtaTransactionManagerWithExistingAndPropagationSupports() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                TransactionSynchronizationManager.registerSynchronization(synch);
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).setRollbackOnly();
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCompletion(STATUS_UNKNOWN);
    }

    @Test
    public void jtaTransactionManagerWithPropagationSupports() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION);
        final TransactionSynchronization synch = Mockito.mock(TransactionSynchronization.class);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                TransactionSynchronizationManager.registerSynchronization(synch);
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(synch).beforeCompletion();
        Mockito.verify(synch).afterCompletion(STATUS_ROLLED_BACK);
    }

    @Test
    public void jtaTransactionManagerWithPropagationSupportsAndSynchronizationOnActual() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        ptm.setTransactionSynchronization(SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        ptm.afterPropertiesSet();
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
    }

    @Test
    public void jtaTransactionManagerWithPropagationSupportsAndSynchronizationNever() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        ptm.setTransactionSynchronization(SYNCHRONIZATION_NEVER);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        ptm.afterPropertiesSet();
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
    }

    @Test
    public void jtaTransactionManagerWithPropagationNotSupported() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        TransactionManager tm = Mockito.mock(TransactionManager.class);
        Transaction tx = Mockito.mock(Transaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        BDDMockito.given(tm.suspend()).willReturn(tx);
        JtaTransactionManager ptm = newJtaTransactionManager(ut, tm);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setPropagationBehavior(PROPAGATION_NOT_SUPPORTED);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                status.setRollbackOnly();
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(tm).resume(tx);
    }

    @Test
    public void jtaTransactionManagerWithPropagationRequiresNew() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        TransactionManager tm = Mockito.mock(TransactionManager.class);
        Transaction tx = Mockito.mock(Transaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE, STATUS_ACTIVE, STATUS_ACTIVE, STATUS_ACTIVE);
        BDDMockito.given(tm.suspend()).willReturn(tx);
        final JtaTransactionManager ptm = newJtaTransactionManager(ut, tm);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        tt.setName("txName");
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertEquals("txName", TransactionSynchronizationManager.getCurrentTransactionName());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                TransactionTemplate tt2 = new TransactionTemplate(ptm);
                tt2.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
                tt2.setReadOnly(true);
                tt2.setName("txName2");
                tt2.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                        Assert.assertEquals("txName2", TransactionSynchronizationManager.getCurrentTransactionName());
                        Assert.assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                    }
                });
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertEquals("txName", TransactionSynchronizationManager.getCurrentTransactionName());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut, Mockito.times(2)).begin();
        Mockito.verify(ut, Mockito.times(2)).commit();
        Mockito.verify(tm).resume(tx);
    }

    @Test
    public void jtaTransactionManagerWithPropagationRequiresNewWithinSupports() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        final JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
                TransactionTemplate tt2 = new TransactionTemplate(ptm);
                tt2.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
                tt2.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                        Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                    }
                });
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).begin();
        Mockito.verify(ut).commit();
    }

    @Test
    public void jtaTransactionManagerWithPropagationRequiresNewAndExisting() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        TransactionManager tm = Mockito.mock(TransactionManager.class);
        Transaction tx = Mockito.mock(Transaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        BDDMockito.given(tm.suspend()).willReturn(tx);
        JtaTransactionManager ptm = newJtaTransactionManager(ut, tm);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(ut).begin();
        Mockito.verify(ut).commit();
        Mockito.verify(tm).resume(tx);
    }

    @Test
    public void jtaTransactionManagerWithPropagationRequiresNewAndExistingWithSuspendException() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        TransactionManager tm = Mockito.mock(TransactionManager.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        BDDMockito.willThrow(new SystemException()).given(tm).suspend();
        JtaTransactionManager ptm = newJtaTransactionManager(ut, tm);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                }
            });
            Assert.fail("Should have thrown TransactionSystemException");
        } catch (TransactionSystemException ex) {
            // expected
        }
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
    }

    @Test
    public void jtaTransactionManagerWithPropagationRequiresNewAndExistingWithBeginException() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        TransactionManager tm = Mockito.mock(TransactionManager.class);
        Transaction tx = Mockito.mock(Transaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        BDDMockito.given(tm.suspend()).willReturn(tx);
        BDDMockito.willThrow(new SystemException()).given(ut).begin();
        JtaTransactionManager ptm = newJtaTransactionManager(ut, tm);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                }
            });
            Assert.fail("Should have thrown CannotCreateTransactionException");
        } catch (CannotCreateTransactionException ex) {
            // expected
        }
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(tm).resume(tx);
    }

    @Test
    public void jtaTransactionManagerWithPropagationRequiresNewAndAdapter() throws Exception {
        TransactionManager tm = Mockito.mock(TransactionManager.class);
        Transaction tx = Mockito.mock(Transaction.class);
        BDDMockito.given(tm.getStatus()).willReturn(STATUS_ACTIVE);
        BDDMockito.given(tm.suspend()).willReturn(tx);
        JtaTransactionManager ptm = newJtaTransactionManager(tm);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
            }
        });
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Mockito.verify(tm).begin();
        Mockito.verify(tm).commit();
        Mockito.verify(tm).resume(tx);
    }

    @Test
    public void jtaTransactionManagerWithPropagationRequiresNewAndSuspensionNotSupported() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                }
            });
            Assert.fail("Should have thrown TransactionSuspensionNotSupportedException");
        } catch (TransactionSuspensionNotSupportedException ex) {
            // expected
        }
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
    }

    @Test
    public void jtaTransactionManagerWithIsolationLevel() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION);
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.setIsolationLevel(ISOLATION_SERIALIZABLE);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    // something transactional
                }
            });
            Assert.fail("Should have thrown InvalidIsolationLevelException");
        } catch (InvalidIsolationLevelException ex) {
            // expected
        }
    }

    @Test
    public void jtaTransactionManagerWithSystemExceptionOnIsExisting() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willThrow(new SystemException("system exception"));
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    // something transactional
                }
            });
            Assert.fail("Should have thrown TransactionSystemException");
        } catch (TransactionSystemException ex) {
            // expected
        }
    }

    @Test
    public void jtaTransactionManagerWithNestedBegin() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.setPropagationBehavior(PROPAGATION_NESTED);
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // something transactional
            }
        });
        Mockito.verify(ut).begin();
        Mockito.verify(ut).commit();
    }

    @Test
    public void jtaTransactionManagerWithNotSupportedExceptionOnNestedBegin() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        BDDMockito.willThrow(new NotSupportedException("not supported")).given(ut).begin();
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.setPropagationBehavior(PROPAGATION_NESTED);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    // something transactional
                }
            });
            Assert.fail("Should have thrown NestedTransactionNotSupportedException");
        } catch (NestedTransactionNotSupportedException ex) {
            // expected
        }
    }

    @Test
    public void jtaTransactionManagerWithUnsupportedOperationExceptionOnNestedBegin() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        BDDMockito.willThrow(new UnsupportedOperationException("not supported")).given(ut).begin();
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.setPropagationBehavior(PROPAGATION_NESTED);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    // something transactional
                }
            });
            Assert.fail("Should have thrown NestedTransactionNotSupportedException");
        } catch (NestedTransactionNotSupportedException ex) {
            // expected
        }
    }

    @Test
    public void jtaTransactionManagerWithSystemExceptionOnBegin() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION);
        BDDMockito.willThrow(new SystemException("system exception")).given(ut).begin();
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    // something transactional
                }
            });
            Assert.fail("Should have thrown CannotCreateTransactionException");
        } catch (CannotCreateTransactionException ex) {
            // expected
        }
    }

    @Test
    public void jtaTransactionManagerWithRollbackExceptionOnCommit() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        BDDMockito.willThrow(new RollbackException("unexpected rollback")).given(ut).commit();
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    // something transactional
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            Assert.assertTrue("Correct completion status", (status == (TransactionSynchronization.STATUS_ROLLED_BACK)));
                        }
                    });
                }
            });
            Assert.fail("Should have thrown UnexpectedRollbackException");
        } catch (UnexpectedRollbackException ex) {
            // expected
        }
        Mockito.verify(ut).begin();
    }

    @Test
    public void jtaTransactionManagerWithNoExceptionOnGlobalRollbackOnly() throws Exception {
        doTestJtaTransactionManagerWithNoExceptionOnGlobalRollbackOnly(false);
    }

    @Test
    public void jtaTransactionManagerWithNoExceptionOnGlobalRollbackOnlyAndFailEarly() throws Exception {
        doTestJtaTransactionManagerWithNoExceptionOnGlobalRollbackOnly(true);
    }

    @Test
    public void jtaTransactionManagerWithHeuristicMixedExceptionOnCommit() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        BDDMockito.willThrow(new HeuristicMixedException("heuristic exception")).given(ut).commit();
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    // something transactional
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            Assert.assertTrue("Correct completion status", (status == (TransactionSynchronization.STATUS_UNKNOWN)));
                        }
                    });
                }
            });
            Assert.fail("Should have thrown HeuristicCompletionException");
        } catch (HeuristicCompletionException ex) {
            // expected
            Assert.assertTrue(((ex.getOutcomeState()) == (STATE_MIXED)));
        }
        Mockito.verify(ut).begin();
    }

    @Test
    public void jtaTransactionManagerWithHeuristicRollbackExceptionOnCommit() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        BDDMockito.willThrow(new HeuristicRollbackException("heuristic exception")).given(ut).commit();
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    // something transactional
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            Assert.assertTrue("Correct completion status", (status == (TransactionSynchronization.STATUS_UNKNOWN)));
                        }
                    });
                }
            });
            Assert.fail("Should have thrown HeuristicCompletionException");
        } catch (HeuristicCompletionException ex) {
            // expected
            Assert.assertTrue(((ex.getOutcomeState()) == (STATE_ROLLED_BACK)));
        }
        Mockito.verify(ut).begin();
    }

    @Test
    public void jtaTransactionManagerWithSystemExceptionOnCommit() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        BDDMockito.willThrow(new SystemException("system exception")).given(ut).commit();
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    // something transactional
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            Assert.assertTrue("Correct completion status", (status == (TransactionSynchronization.STATUS_UNKNOWN)));
                        }
                    });
                }
            });
            Assert.fail("Should have thrown TransactionSystemException");
        } catch (TransactionSystemException ex) {
            // expected
        }
        Mockito.verify(ut).begin();
    }

    @Test
    public void jtaTransactionManagerWithSystemExceptionOnRollback() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);
        BDDMockito.willThrow(new SystemException("system exception")).given(ut).rollback();
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            Assert.assertTrue("Correct completion status", (status == (TransactionSynchronization.STATUS_UNKNOWN)));
                        }
                    });
                    status.setRollbackOnly();
                }
            });
            Assert.fail("Should have thrown TransactionSystemException");
        } catch (TransactionSystemException ex) {
            // expected
        }
        Mockito.verify(ut).begin();
    }

    @Test
    public void jtaTransactionManagerWithIllegalStateExceptionOnRollbackOnly() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        BDDMockito.willThrow(new IllegalStateException("no existing transaction")).given(ut).setRollbackOnly();
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    status.setRollbackOnly();
                }
            });
            Assert.fail("Should have thrown TransactionSystemException");
        } catch (TransactionSystemException ex) {
            // expected
        }
    }

    @Test
    public void jtaTransactionManagerWithSystemExceptionOnRollbackOnly() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_ACTIVE);
        BDDMockito.willThrow(new SystemException("system exception")).given(ut).setRollbackOnly();
        try {
            JtaTransactionManager ptm = newJtaTransactionManager(ut);
            TransactionTemplate tt = new TransactionTemplate(ptm);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    status.setRollbackOnly();
                    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCompletion(int status) {
                            Assert.assertTrue("Correct completion status", (status == (TransactionSynchronization.STATUS_UNKNOWN)));
                        }
                    });
                }
            });
            Assert.fail("Should have thrown TransactionSystemException");
        } catch (TransactionSystemException ex) {
            // expected
        }
    }

    @Test
    public void jtaTransactionManagerWithDoubleCommit() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        TransactionStatus status = ptm.getTransaction(new DefaultTransactionDefinition());
        Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
        // first commit
        ptm.commit(status);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        try {
            // second commit attempt
            ptm.commit(status);
            Assert.fail("Should have thrown IllegalTransactionStateException");
        } catch (IllegalTransactionStateException ex) {
            // expected
        }
        Mockito.verify(ut).begin();
        Mockito.verify(ut).commit();
    }

    @Test
    public void jtaTransactionManagerWithDoubleRollback() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        TransactionStatus status = ptm.getTransaction(new DefaultTransactionDefinition());
        Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
        // first rollback
        ptm.rollback(status);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        try {
            // second rollback attempt
            ptm.rollback(status);
            Assert.fail("Should have thrown IllegalTransactionStateException");
        } catch (IllegalTransactionStateException ex) {
            // expected
        }
        Mockito.verify(ut).begin();
        Mockito.verify(ut).rollback();
    }

    @Test
    public void jtaTransactionManagerWithRollbackAndCommit() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE);
        JtaTransactionManager ptm = newJtaTransactionManager(ut);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        TransactionStatus status = ptm.getTransaction(new DefaultTransactionDefinition());
        Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
        // first: rollback
        ptm.rollback(status);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        try {
            // second: commit attempt
            ptm.commit(status);
            Assert.fail("Should have thrown IllegalTransactionStateException");
        } catch (IllegalTransactionStateException ex) {
            // expected
        }
        Mockito.verify(ut).begin();
        Mockito.verify(ut).rollback();
    }
}

