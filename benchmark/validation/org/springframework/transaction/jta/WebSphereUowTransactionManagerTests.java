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
package org.springframework.transaction.jta;


import Status.STATUS_ACTIVE;
import Status.STATUS_NO_TRANSACTION;
import TransactionDefinition.PROPAGATION_MANDATORY;
import TransactionDefinition.PROPAGATION_NESTED;
import TransactionDefinition.PROPAGATION_NEVER;
import TransactionDefinition.PROPAGATION_NOT_SUPPORTED;
import TransactionDefinition.PROPAGATION_REQUIRED;
import TransactionDefinition.PROPAGATION_REQUIRES_NEW;
import TransactionDefinition.PROPAGATION_SUPPORTS;
import UOWManager.UOW_STATUS_ACTIVE;
import UOWManager.UOW_TYPE_GLOBAL_TRANSACTION;
import UOWManager.UOW_TYPE_LOCAL_TRANSACTION;
import WebSphereUowTransactionManager.DEFAULT_UOW_MANAGER_NAME;
import WebSphereUowTransactionManager.DEFAULT_USER_TRANSACTION_NAME;
import WebSphereUowTransactionManager.SYNCHRONIZATION_ALWAYS;
import WebSphereUowTransactionManager.SYNCHRONIZATION_NEVER;
import WebSphereUowTransactionManager.SYNCHRONIZATION_ON_ACTUAL_TRANSACTION;
import com.ibm.wsspi.uow.UOWAction;
import com.ibm.wsspi.uow.UOWException;
import javax.transaction.RollbackException;
import javax.transaction.UserTransaction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.tests.mock.jndi.ExpectedLookupTemplate;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static WebSphereUowTransactionManager.DEFAULT_UOW_MANAGER_NAME;


/**
 *
 *
 * @author Juergen Hoeller
 */
public class WebSphereUowTransactionManagerTests {
    @Test
    public void uowManagerFoundInJndi() {
        MockUOWManager manager = new MockUOWManager();
        ExpectedLookupTemplate jndiTemplate = new ExpectedLookupTemplate(DEFAULT_UOW_MANAGER_NAME, manager);
        WebSphereUowTransactionManager ptm = new WebSphereUowTransactionManager();
        ptm.setJndiTemplate(jndiTemplate);
        ptm.afterPropertiesSet();
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        Assert.assertEquals("result", ptm.execute(definition, new org.springframework.transaction.support.TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                return "result";
            }
        }));
        Assert.assertEquals(UOW_TYPE_GLOBAL_TRANSACTION, manager.getUOWType());
        Assert.assertFalse(manager.getJoined());
        Assert.assertFalse(manager.getRollbackOnly());
    }

    @Test
    public void uowManagerAndUserTransactionFoundInJndi() throws Exception {
        UserTransaction ut = Mockito.mock(UserTransaction.class);
        BDDMockito.given(ut.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        MockUOWManager manager = new MockUOWManager();
        ExpectedLookupTemplate jndiTemplate = new ExpectedLookupTemplate();
        jndiTemplate.addObject(DEFAULT_USER_TRANSACTION_NAME, ut);
        jndiTemplate.addObject(DEFAULT_UOW_MANAGER_NAME, manager);
        WebSphereUowTransactionManager ptm = new WebSphereUowTransactionManager();
        ptm.setJndiTemplate(jndiTemplate);
        ptm.afterPropertiesSet();
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus ts = ptm.getTransaction(definition);
        ptm.commit(ts);
        Assert.assertEquals("result", ptm.execute(definition, new org.springframework.transaction.support.TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                return "result";
            }
        }));
        Assert.assertEquals(UOW_TYPE_GLOBAL_TRANSACTION, manager.getUOWType());
        Assert.assertFalse(manager.getJoined());
        Assert.assertFalse(manager.getRollbackOnly());
        Mockito.verify(ut).begin();
        Mockito.verify(ut).commit();
    }

    @Test
    public void propagationMandatoryFailsInCaseOfNoExistingTransaction() {
        MockUOWManager manager = new MockUOWManager();
        WebSphereUowTransactionManager ptm = new WebSphereUowTransactionManager(manager);
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(PROPAGATION_MANDATORY);
        try {
            ptm.execute(definition, new org.springframework.transaction.support.TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    return "result";
                }
            });
            Assert.fail("Should have thrown IllegalTransactionStateException");
        } catch (IllegalTransactionStateException ex) {
            // expected
        }
    }

    @Test
    public void newTransactionSynchronizationUsingPropagationSupports() {
        doTestNewTransactionSynchronization(PROPAGATION_SUPPORTS, SYNCHRONIZATION_ALWAYS);
    }

    @Test
    public void newTransactionSynchronizationUsingPropagationNotSupported() {
        doTestNewTransactionSynchronization(PROPAGATION_NOT_SUPPORTED, SYNCHRONIZATION_ALWAYS);
    }

    @Test
    public void newTransactionSynchronizationUsingPropagationNever() {
        doTestNewTransactionSynchronization(PROPAGATION_NEVER, SYNCHRONIZATION_ALWAYS);
    }

    @Test
    public void newTransactionSynchronizationUsingPropagationSupportsAndSynchOnActual() {
        doTestNewTransactionSynchronization(PROPAGATION_SUPPORTS, SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
    }

    @Test
    public void newTransactionSynchronizationUsingPropagationNotSupportedAndSynchOnActual() {
        doTestNewTransactionSynchronization(PROPAGATION_NOT_SUPPORTED, SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
    }

    @Test
    public void newTransactionSynchronizationUsingPropagationNeverAndSynchOnActual() {
        doTestNewTransactionSynchronization(PROPAGATION_NEVER, SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
    }

    @Test
    public void newTransactionSynchronizationUsingPropagationSupportsAndSynchNever() {
        doTestNewTransactionSynchronization(PROPAGATION_SUPPORTS, SYNCHRONIZATION_NEVER);
    }

    @Test
    public void newTransactionSynchronizationUsingPropagationNotSupportedAndSynchNever() {
        doTestNewTransactionSynchronization(PROPAGATION_NOT_SUPPORTED, SYNCHRONIZATION_NEVER);
    }

    @Test
    public void newTransactionSynchronizationUsingPropagationNeverAndSynchNever() {
        doTestNewTransactionSynchronization(PROPAGATION_NEVER, SYNCHRONIZATION_NEVER);
    }

    @Test
    public void newTransactionWithCommitUsingPropagationRequired() {
        doTestNewTransactionWithCommit(PROPAGATION_REQUIRED, SYNCHRONIZATION_ALWAYS);
    }

    @Test
    public void newTransactionWithCommitUsingPropagationRequiresNew() {
        doTestNewTransactionWithCommit(PROPAGATION_REQUIRES_NEW, SYNCHRONIZATION_ALWAYS);
    }

    @Test
    public void newTransactionWithCommitUsingPropagationNested() {
        doTestNewTransactionWithCommit(PROPAGATION_NESTED, SYNCHRONIZATION_ALWAYS);
    }

    @Test
    public void newTransactionWithCommitUsingPropagationRequiredAndSynchOnActual() {
        doTestNewTransactionWithCommit(PROPAGATION_REQUIRED, SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
    }

    @Test
    public void newTransactionWithCommitUsingPropagationRequiresNewAndSynchOnActual() {
        doTestNewTransactionWithCommit(PROPAGATION_REQUIRES_NEW, SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
    }

    @Test
    public void newTransactionWithCommitUsingPropagationNestedAndSynchOnActual() {
        doTestNewTransactionWithCommit(PROPAGATION_NESTED, SYNCHRONIZATION_ON_ACTUAL_TRANSACTION);
    }

    @Test
    public void newTransactionWithCommitUsingPropagationRequiredAndSynchNever() {
        doTestNewTransactionWithCommit(PROPAGATION_REQUIRED, SYNCHRONIZATION_NEVER);
    }

    @Test
    public void newTransactionWithCommitUsingPropagationRequiresNewAndSynchNever() {
        doTestNewTransactionWithCommit(PROPAGATION_REQUIRES_NEW, SYNCHRONIZATION_NEVER);
    }

    @Test
    public void newTransactionWithCommitUsingPropagationNestedAndSynchNever() {
        doTestNewTransactionWithCommit(PROPAGATION_NESTED, SYNCHRONIZATION_NEVER);
    }

    @Test
    public void newTransactionWithCommitAndTimeout() {
        MockUOWManager manager = new MockUOWManager();
        WebSphereUowTransactionManager ptm = new WebSphereUowTransactionManager(manager);
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setTimeout(10);
        definition.setReadOnly(true);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Assert.assertEquals("result", ptm.execute(definition, new org.springframework.transaction.support.TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                Assert.assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                return "result";
            }
        }));
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Assert.assertEquals(10, manager.getUOWTimeout());
        Assert.assertEquals(UOW_TYPE_GLOBAL_TRANSACTION, manager.getUOWType());
        Assert.assertFalse(manager.getJoined());
        Assert.assertFalse(manager.getRollbackOnly());
    }

    @Test
    public void newTransactionWithCommitException() {
        final RollbackException rex = new RollbackException();
        MockUOWManager manager = new MockUOWManager() {
            @Override
            public void runUnderUOW(int type, boolean join, UOWAction action) throws UOWException {
                throw new UOWException(rex);
            }
        };
        WebSphereUowTransactionManager ptm = new WebSphereUowTransactionManager(manager);
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        try {
            ptm.execute(definition, new org.springframework.transaction.support.TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                    Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                    Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                    return "result";
                }
            });
            Assert.fail("Should have thrown TransactionSystemException");
        } catch (TransactionSystemException ex) {
            // expected
            Assert.assertTrue(((ex.getCause()) instanceof UOWException));
            Assert.assertSame(rex, ex.getRootCause());
            Assert.assertSame(rex, ex.getMostSpecificCause());
        }
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Assert.assertEquals(0, manager.getUOWTimeout());
    }

    @Test
    public void newTransactionWithRollback() {
        MockUOWManager manager = new MockUOWManager();
        WebSphereUowTransactionManager ptm = new WebSphereUowTransactionManager(manager);
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        try {
            ptm.execute(definition, new org.springframework.transaction.support.TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                    Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                    Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                    throw new OptimisticLockingFailureException("");
                }
            });
            Assert.fail("Should have thrown OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException ex) {
            // expected
        }
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Assert.assertEquals(0, manager.getUOWTimeout());
        Assert.assertEquals(UOW_TYPE_GLOBAL_TRANSACTION, manager.getUOWType());
        Assert.assertFalse(manager.getJoined());
        Assert.assertTrue(manager.getRollbackOnly());
    }

    @Test
    public void newTransactionWithRollbackOnly() {
        MockUOWManager manager = new MockUOWManager();
        WebSphereUowTransactionManager ptm = new WebSphereUowTransactionManager(manager);
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Assert.assertEquals("result", ptm.execute(definition, new org.springframework.transaction.support.TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                status.setRollbackOnly();
                return "result";
            }
        }));
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Assert.assertEquals(0, manager.getUOWTimeout());
        Assert.assertEquals(UOW_TYPE_GLOBAL_TRANSACTION, manager.getUOWType());
        Assert.assertFalse(manager.getJoined());
        Assert.assertTrue(manager.getRollbackOnly());
    }

    @Test
    public void existingNonSpringTransaction() {
        MockUOWManager manager = new MockUOWManager();
        manager.setUOWStatus(UOW_STATUS_ACTIVE);
        WebSphereUowTransactionManager ptm = new WebSphereUowTransactionManager(manager);
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Assert.assertEquals("result", ptm.execute(definition, new org.springframework.transaction.support.TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                return "result";
            }
        }));
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Assert.assertEquals(0, manager.getUOWTimeout());
        Assert.assertEquals(UOW_TYPE_GLOBAL_TRANSACTION, manager.getUOWType());
        Assert.assertTrue(manager.getJoined());
        Assert.assertFalse(manager.getRollbackOnly());
    }

    @Test
    public void propagationNeverFailsInCaseOfExistingTransaction() {
        MockUOWManager manager = new MockUOWManager();
        manager.setUOWStatus(UOW_STATUS_ACTIVE);
        WebSphereUowTransactionManager ptm = new WebSphereUowTransactionManager(manager);
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(PROPAGATION_NEVER);
        try {
            ptm.execute(definition, new org.springframework.transaction.support.TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    return "result";
                }
            });
            Assert.fail("Should have thrown IllegalTransactionStateException");
        } catch (IllegalTransactionStateException ex) {
            // expected
        }
    }

    @Test
    public void propagationNestedFailsInCaseOfExistingTransaction() {
        MockUOWManager manager = new MockUOWManager();
        manager.setUOWStatus(UOW_STATUS_ACTIVE);
        WebSphereUowTransactionManager ptm = new WebSphereUowTransactionManager(manager);
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(PROPAGATION_NESTED);
        try {
            ptm.execute(definition, new org.springframework.transaction.support.TransactionCallback<String>() {
                @Override
                public String doInTransaction(TransactionStatus status) {
                    return "result";
                }
            });
            Assert.fail("Should have thrown NestedTransactionNotSupportedException");
        } catch (NestedTransactionNotSupportedException ex) {
            // expected
        }
    }

    @Test
    public void existingTransactionWithParticipationUsingPropagationRequired() {
        doTestExistingTransactionWithParticipation(PROPAGATION_REQUIRED);
    }

    @Test
    public void existingTransactionWithParticipationUsingPropagationSupports() {
        doTestExistingTransactionWithParticipation(PROPAGATION_SUPPORTS);
    }

    @Test
    public void existingTransactionWithParticipationUsingPropagationMandatory() {
        doTestExistingTransactionWithParticipation(PROPAGATION_MANDATORY);
    }

    @Test
    public void existingTransactionWithSuspensionUsingPropagationRequiresNew() {
        doTestExistingTransactionWithSuspension(PROPAGATION_REQUIRES_NEW);
    }

    @Test
    public void existingTransactionWithSuspensionUsingPropagationNotSupported() {
        doTestExistingTransactionWithSuspension(PROPAGATION_NOT_SUPPORTED);
    }

    @Test
    public void existingTransactionUsingPropagationNotSupported() {
        MockUOWManager manager = new MockUOWManager();
        final WebSphereUowTransactionManager ptm = new WebSphereUowTransactionManager(manager);
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        final DefaultTransactionDefinition definition2 = new DefaultTransactionDefinition();
        definition2.setPropagationBehavior(PROPAGATION_NOT_SUPPORTED);
        definition2.setReadOnly(true);
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Assert.assertEquals("result", ptm.execute(definition, new org.springframework.transaction.support.TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertEquals("result2", ptm.execute(definition2, new org.springframework.transaction.support.TransactionCallback<String>() {
                    @Override
                    public String doInTransaction(TransactionStatus status) {
                        Assert.assertTrue(TransactionSynchronizationManager.isSynchronizationActive());
                        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
                        Assert.assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                        return "result2";
                    }
                }));
                return "result";
            }
        }));
        Assert.assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
        Assert.assertEquals(0, manager.getUOWTimeout());
        Assert.assertEquals(UOW_TYPE_LOCAL_TRANSACTION, manager.getUOWType());
        Assert.assertFalse(manager.getJoined());
        Assert.assertFalse(manager.getRollbackOnly());
    }
}

