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
package org.springframework.jdbc.datasource;


import DataSourceTransactionManager.SYNCHRONIZATION_NEVER;
import TransactionDefinition.ISOLATION_SERIALIZABLE;
import TransactionDefinition.PROPAGATION_NESTED;
import TransactionDefinition.PROPAGATION_NEVER;
import TransactionDefinition.PROPAGATION_NOT_SUPPORTED;
import TransactionDefinition.PROPAGATION_REQUIRES_NEW;
import TransactionDefinition.PROPAGATION_SUPPORTS;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 04.07.2003
 */
public class DataSourceTransactionManagerTests {
    private DataSource ds;

    private Connection con;

    private DataSourceTransactionManager tm;

    @Test
    public void testTransactionCommitWithAutoCommitTrue() throws Exception {
        doTestTransactionCommitRestoringAutoCommit(true, false, false);
    }

    @Test
    public void testTransactionCommitWithAutoCommitFalse() throws Exception {
        doTestTransactionCommitRestoringAutoCommit(false, false, false);
    }

    @Test
    public void testTransactionCommitWithAutoCommitTrueAndLazyConnection() throws Exception {
        doTestTransactionCommitRestoringAutoCommit(true, true, false);
    }

    @Test
    public void testTransactionCommitWithAutoCommitFalseAndLazyConnection() throws Exception {
        doTestTransactionCommitRestoringAutoCommit(false, true, false);
    }

    @Test
    public void testTransactionCommitWithAutoCommitTrueAndLazyConnectionAndStatementCreated() throws Exception {
        doTestTransactionCommitRestoringAutoCommit(true, true, true);
    }

    @Test
    public void testTransactionCommitWithAutoCommitFalseAndLazyConnectionAndStatementCreated() throws Exception {
        doTestTransactionCommitRestoringAutoCommit(false, true, true);
    }

    @Test
    public void testTransactionRollbackWithAutoCommitTrue() throws Exception {
        doTestTransactionRollbackRestoringAutoCommit(true, false, false);
    }

    @Test
    public void testTransactionRollbackWithAutoCommitFalse() throws Exception {
        doTestTransactionRollbackRestoringAutoCommit(false, false, false);
    }

    @Test
    public void testTransactionRollbackWithAutoCommitTrueAndLazyConnection() throws Exception {
        doTestTransactionRollbackRestoringAutoCommit(true, true, false);
    }

    @Test
    public void testTransactionRollbackWithAutoCommitFalseAndLazyConnection() throws Exception {
        doTestTransactionRollbackRestoringAutoCommit(false, true, false);
    }

    @Test
    public void testTransactionRollbackWithAutoCommitTrueAndLazyConnectionAndCreateStatement() throws Exception {
        doTestTransactionRollbackRestoringAutoCommit(true, true, true);
    }

    @Test
    public void testTransactionRollbackWithAutoCommitFalseAndLazyConnectionAndCreateStatement() throws Exception {
        doTestTransactionRollbackRestoringAutoCommit(false, true, true);
    }

    @Test
    public void testTransactionRollbackOnly() throws Exception {
        tm.setTransactionSynchronization(SYNCHRONIZATION_NEVER);
        TransactionTemplate tt = new TransactionTemplate(tm);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        ConnectionHolder conHolder = new ConnectionHolder(con);
        conHolder.setTransactionActive(true);
        TransactionSynchronizationManager.bindResource(ds, conHolder);
        final RuntimeException ex = new RuntimeException("Application exception");
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                    Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(ds));
                    Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
                    Assert.assertTrue("Is existing transaction", (!(status.isNewTransaction())));
                    throw ex;
                }
            });
            Assert.fail("Should have thrown RuntimeException");
        } catch (RuntimeException ex2) {
            // expected
            Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
            Assert.assertEquals("Correct exception thrown", ex, ex2);
        } finally {
            TransactionSynchronizationManager.unbindResource(ds);
        }
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
    }

    @Test
    public void testParticipatingTransactionWithRollbackOnly() throws Exception {
        doTestParticipatingTransactionWithRollbackOnly(false);
    }

    @Test
    public void testParticipatingTransactionWithRollbackOnlyAndFailEarly() throws Exception {
        doTestParticipatingTransactionWithRollbackOnly(true);
    }

    @Test
    public void testParticipatingTransactionWithIncompatibleIsolationLevel() throws Exception {
        tm.setValidateExistingTransaction(true);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        try {
            final TransactionTemplate tt = new TransactionTemplate(tm);
            final TransactionTemplate tt2 = new TransactionTemplate(tm);
            tt2.setIsolationLevel(ISOLATION_SERIALIZABLE);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                    Assert.assertFalse("Is not rollback-only", status.isRollbackOnly());
                    tt2.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                            status.setRollbackOnly();
                        }
                    });
                    Assert.assertTrue("Is rollback-only", status.isRollbackOnly());
                }
            });
            Assert.fail("Should have thrown IllegalTransactionStateException");
        } catch (IllegalTransactionStateException ex) {
            // expected
        }
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).rollback();
        Mockito.verify(con).close();
    }

    @Test
    public void testParticipatingTransactionWithIncompatibleReadOnly() throws Exception {
        BDDMockito.willThrow(new SQLException("read-only not supported")).given(con).setReadOnly(true);
        tm.setValidateExistingTransaction(true);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        try {
            final TransactionTemplate tt = new TransactionTemplate(tm);
            tt.setReadOnly(true);
            final TransactionTemplate tt2 = new TransactionTemplate(tm);
            tt2.setReadOnly(false);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                    Assert.assertFalse("Is not rollback-only", status.isRollbackOnly());
                    tt2.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                            status.setRollbackOnly();
                        }
                    });
                    Assert.assertTrue("Is rollback-only", status.isRollbackOnly());
                }
            });
            Assert.fail("Should have thrown IllegalTransactionStateException");
        } catch (IllegalTransactionStateException ex) {
            // expected
        }
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).rollback();
        Mockito.verify(con).close();
    }

    @Test
    public void testParticipatingTransactionWithTransactionStartedFromSynch() throws Exception {
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        final DataSourceTransactionManagerTests.TestTransactionSynchronization synch = new DataSourceTransactionManagerTests.TestTransactionSynchronization(ds, TransactionSynchronization.STATUS_COMMITTED) {
            @Override
            protected void doAfterCompletion(int status) {
                super.doAfterCompletion(status);
                tt.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                    }
                });
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {});
            }
        };
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                TransactionSynchronizationManager.registerSynchronization(synch);
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue(synch.beforeCommitCalled);
        Assert.assertTrue(synch.beforeCompletionCalled);
        Assert.assertTrue(synch.afterCommitCalled);
        Assert.assertTrue(synch.afterCompletionCalled);
        Assert.assertTrue(((synch.afterCompletionException) instanceof IllegalStateException));
        Mockito.verify(con, Mockito.times(2)).commit();
        Mockito.verify(con, Mockito.times(2)).close();
    }

    @Test
    public void testParticipatingTransactionWithDifferentConnectionObtainedFromSynch() throws Exception {
        DataSource ds2 = Mockito.mock(DataSource.class);
        final Connection con2 = Mockito.mock(Connection.class);
        BDDMockito.given(ds2.getConnection()).willReturn(con2);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        final TransactionTemplate tt = new TransactionTemplate(tm);
        final DataSourceTransactionManagerTests.TestTransactionSynchronization synch = new DataSourceTransactionManagerTests.TestTransactionSynchronization(ds, TransactionSynchronization.STATUS_COMMITTED) {
            @Override
            protected void doAfterCompletion(int status) {
                super.doAfterCompletion(status);
                Connection con = DataSourceUtils.getConnection(ds2);
                DataSourceUtils.releaseConnection(con, ds2);
            }
        };
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                TransactionSynchronizationManager.registerSynchronization(synch);
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue(synch.beforeCommitCalled);
        Assert.assertTrue(synch.beforeCompletionCalled);
        Assert.assertTrue(synch.afterCommitCalled);
        Assert.assertTrue(synch.afterCompletionCalled);
        Assert.assertNull(synch.afterCompletionException);
        Mockito.verify(con).commit();
        Mockito.verify(con).close();
        Mockito.verify(con2).close();
    }

    @Test
    public void testParticipatingTransactionWithRollbackOnlyAndInnerSynch() throws Exception {
        tm.setTransactionSynchronization(SYNCHRONIZATION_NEVER);
        DataSourceTransactionManager tm2 = new DataSourceTransactionManager(ds);
        // tm has no synch enabled (used at outer level), tm2 has synch enabled (inner level)
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
        final DataSourceTransactionManagerTests.TestTransactionSynchronization synch = new DataSourceTransactionManagerTests.TestTransactionSynchronization(ds, TransactionSynchronization.STATUS_UNKNOWN);
        try {
            Assert.assertTrue("Is new transaction", ts.isNewTransaction());
            final TransactionTemplate tt = new TransactionTemplate(tm2);
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                    Assert.assertTrue("Is existing transaction", (!(status.isNewTransaction())));
                    Assert.assertFalse("Is not rollback-only", status.isRollbackOnly());
                    tt.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                            Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(ds));
                            Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                            Assert.assertTrue("Is existing transaction", (!(status.isNewTransaction())));
                            status.setRollbackOnly();
                        }
                    });
                    Assert.assertTrue("Is existing transaction", (!(status.isNewTransaction())));
                    Assert.assertTrue("Is rollback-only", status.isRollbackOnly());
                    TransactionSynchronizationManager.registerSynchronization(synch);
                }
            });
            tm.commit(ts);
            Assert.fail("Should have thrown UnexpectedRollbackException");
        } catch (UnexpectedRollbackException ex) {
            // expected
        }
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertFalse(synch.beforeCommitCalled);
        Assert.assertTrue(synch.beforeCompletionCalled);
        Assert.assertFalse(synch.afterCommitCalled);
        Assert.assertTrue(synch.afterCompletionCalled);
        Mockito.verify(con).rollback();
        Mockito.verify(con).close();
    }

    @Test
    public void testPropagationRequiresNewWithExistingTransaction() throws Exception {
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                tt.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                        Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(ds));
                        Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                        Assert.assertTrue("Is new transaction", status.isNewTransaction());
                        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                        Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                        status.setRollbackOnly();
                    }
                });
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).rollback();
        Mockito.verify(con).commit();
        Mockito.verify(con, Mockito.times(2)).close();
    }

    @Test
    public void testPropagationRequiresNewWithExistingTransactionAndUnrelatedDataSource() throws Exception {
        Connection con2 = Mockito.mock(Connection.class);
        final DataSource ds2 = Mockito.mock(DataSource.class);
        BDDMockito.given(ds2.getConnection()).willReturn(con2);
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        PlatformTransactionManager tm2 = new DataSourceTransactionManager(ds2);
        final TransactionTemplate tt2 = new TransactionTemplate(tm2);
        tt2.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds2))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                tt2.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                        Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(ds));
                        Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                        Assert.assertTrue("Is new transaction", status.isNewTransaction());
                        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                        Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                        status.setRollbackOnly();
                    }
                });
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds2))));
        Mockito.verify(con).commit();
        Mockito.verify(con).close();
        Mockito.verify(con2).rollback();
        Mockito.verify(con2).close();
    }

    @Test
    public void testPropagationRequiresNewWithExistingTransactionAndUnrelatedFailingDataSource() throws Exception {
        final DataSource ds2 = Mockito.mock(DataSource.class);
        SQLException failure = new SQLException();
        BDDMockito.given(ds2.getConnection()).willThrow(failure);
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        DataSourceTransactionManager tm2 = new DataSourceTransactionManager(ds2);
        tm2.setTransactionSynchronization(SYNCHRONIZATION_NEVER);
        final TransactionTemplate tt2 = new TransactionTemplate(tm2);
        tt2.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds2))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                    Assert.assertTrue("Is new transaction", status.isNewTransaction());
                    Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                    Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                    Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                    tt2.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                            status.setRollbackOnly();
                        }
                    });
                }
            });
            Assert.fail("Should have thrown CannotCreateTransactionException");
        } catch (CannotCreateTransactionException ex) {
            Assert.assertSame(failure, ex.getCause());
        }
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds2))));
        Mockito.verify(con).rollback();
        Mockito.verify(con).close();
    }

    @Test
    public void testPropagationNotSupportedWithExistingTransaction() throws Exception {
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                tt.setPropagationBehavior(PROPAGATION_NOT_SUPPORTED);
                tt.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
                        Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                        Assert.assertTrue("Isn't new transaction", (!(status.isNewTransaction())));
                        Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                        Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
                        status.setRollbackOnly();
                    }
                });
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).commit();
        Mockito.verify(con).close();
    }

    @Test
    public void testPropagationNeverWithExistingTransaction() throws Exception {
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                    Assert.assertTrue("Is new transaction", status.isNewTransaction());
                    tt.setPropagationBehavior(PROPAGATION_NEVER);
                    tt.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                            Assert.fail("Should have thrown IllegalTransactionStateException");
                        }
                    });
                    Assert.fail("Should have thrown IllegalTransactionStateException");
                }
            });
        } catch (IllegalTransactionStateException ex) {
            // expected
        }
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).rollback();
        Mockito.verify(con).close();
    }

    @Test
    public void testPropagationSupportsAndRequiresNew() throws Exception {
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                TransactionTemplate tt2 = new TransactionTemplate(tm);
                tt2.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
                tt2.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                        Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(ds));
                        Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                        Assert.assertTrue("Is new transaction", status.isNewTransaction());
                        Assert.assertSame(con, DataSourceUtils.getConnection(ds));
                        Assert.assertSame(con, DataSourceUtils.getConnection(ds));
                    }
                });
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).commit();
        Mockito.verify(con).close();
    }

    @Test
    public void testPropagationSupportsAndRequiresNewWithEarlyAccess() throws Exception {
        final Connection con1 = Mockito.mock(Connection.class);
        final Connection con2 = Mockito.mock(Connection.class);
        BDDMockito.given(ds.getConnection()).willReturn(con1, con2);
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                Assert.assertSame(con1, DataSourceUtils.getConnection(ds));
                Assert.assertSame(con1, DataSourceUtils.getConnection(ds));
                TransactionTemplate tt2 = new TransactionTemplate(tm);
                tt2.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
                tt2.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                        Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(ds));
                        Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                        Assert.assertTrue("Is new transaction", status.isNewTransaction());
                        Assert.assertSame(con2, DataSourceUtils.getConnection(ds));
                        Assert.assertSame(con2, DataSourceUtils.getConnection(ds));
                    }
                });
                Assert.assertSame(con1, DataSourceUtils.getConnection(ds));
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con1).close();
        Mockito.verify(con2).commit();
        Mockito.verify(con2).close();
    }

    @Test
    public void testTransactionWithIsolationAndReadOnly() throws Exception {
        BDDMockito.given(con.getTransactionIsolation()).willReturn(Connection.TRANSACTION_READ_COMMITTED);
        BDDMockito.given(con.getAutoCommit()).willReturn(true);
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        tt.setIsolationLevel(ISOLATION_SERIALIZABLE);
        tt.setReadOnly(true);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                // something transactional
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        InOrder ordered = Mockito.inOrder(con);
        ordered.verify(con).setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        ordered.verify(con).setAutoCommit(false);
        ordered.verify(con).commit();
        ordered.verify(con).setAutoCommit(true);
        ordered.verify(con).setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        Mockito.verify(con).close();
    }

    @Test
    public void testTransactionWithEnforceReadOnly() throws Exception {
        tm.setEnforceReadOnly(true);
        BDDMockito.given(con.getAutoCommit()).willReturn(true);
        Statement stmt = Mockito.mock(Statement.class);
        BDDMockito.given(con.createStatement()).willReturn(stmt);
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
        tt.setReadOnly(true);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Assert.assertTrue(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
                // something transactional
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        InOrder ordered = Mockito.inOrder(con, stmt);
        ordered.verify(con).setAutoCommit(false);
        ordered.verify(stmt).executeUpdate("SET TRANSACTION READ ONLY");
        ordered.verify(stmt).close();
        ordered.verify(con).commit();
        ordered.verify(con).setAutoCommit(true);
        ordered.verify(con).close();
    }

    @Test
    public void testTransactionWithLongTimeout() throws Exception {
        doTestTransactionWithTimeout(10);
    }

    @Test
    public void testTransactionWithShortTimeout() throws Exception {
        doTestTransactionWithTimeout(1);
    }

    @Test
    public void testTransactionAwareDataSourceProxy() throws Exception {
        BDDMockito.given(con.getAutoCommit()).willReturn(true);
        TransactionTemplate tt = new TransactionTemplate(tm);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // something transactional
                Assert.assertEquals(con, DataSourceUtils.getConnection(ds));
                TransactionAwareDataSourceProxy dsProxy = new TransactionAwareDataSourceProxy(ds);
                try {
                    Assert.assertEquals(con, getTargetConnection());
                    // should be ignored
                    dsProxy.getConnection().close();
                } catch (SQLException ex) {
                    throw new UncategorizedSQLException("", "", ex);
                }
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        InOrder ordered = Mockito.inOrder(con);
        ordered.verify(con).setAutoCommit(false);
        ordered.verify(con).commit();
        ordered.verify(con).setAutoCommit(true);
        Mockito.verify(con).close();
    }

    @Test
    public void testTransactionAwareDataSourceProxyWithSuspension() throws Exception {
        BDDMockito.given(con.getAutoCommit()).willReturn(true);
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // something transactional
                Assert.assertEquals(con, DataSourceUtils.getConnection(ds));
                final TransactionAwareDataSourceProxy dsProxy = new TransactionAwareDataSourceProxy(ds);
                try {
                    Assert.assertEquals(con, getTargetConnection());
                    // should be ignored
                    dsProxy.getConnection().close();
                } catch (SQLException ex) {
                    throw new UncategorizedSQLException("", "", ex);
                }
                tt.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        // something transactional
                        Assert.assertEquals(con, DataSourceUtils.getConnection(ds));
                        try {
                            Assert.assertEquals(con, getTargetConnection());
                            // should be ignored
                            dsProxy.getConnection().close();
                        } catch (SQLException ex) {
                            throw new UncategorizedSQLException("", "", ex);
                        }
                    }
                });
                try {
                    Assert.assertEquals(con, getTargetConnection());
                    // should be ignored
                    dsProxy.getConnection().close();
                } catch (SQLException ex) {
                    throw new UncategorizedSQLException("", "", ex);
                }
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        InOrder ordered = Mockito.inOrder(con);
        ordered.verify(con).setAutoCommit(false);
        ordered.verify(con).commit();
        ordered.verify(con).setAutoCommit(true);
        Mockito.verify(con, Mockito.times(2)).close();
    }

    @Test
    public void testTransactionAwareDataSourceProxyWithSuspensionAndReobtaining() throws Exception {
        BDDMockito.given(con.getAutoCommit()).willReturn(true);
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRES_NEW);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // something transactional
                Assert.assertEquals(con, DataSourceUtils.getConnection(ds));
                final TransactionAwareDataSourceProxy dsProxy = new TransactionAwareDataSourceProxy(ds);
                dsProxy.setReobtainTransactionalConnections(true);
                try {
                    Assert.assertEquals(con, getTargetConnection());
                    // should be ignored
                    dsProxy.getConnection().close();
                } catch (SQLException ex) {
                    throw new UncategorizedSQLException("", "", ex);
                }
                tt.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        // something transactional
                        Assert.assertEquals(con, DataSourceUtils.getConnection(ds));
                        try {
                            Assert.assertEquals(con, getTargetConnection());
                            // should be ignored
                            dsProxy.getConnection().close();
                        } catch (SQLException ex) {
                            throw new UncategorizedSQLException("", "", ex);
                        }
                    }
                });
                try {
                    Assert.assertEquals(con, getTargetConnection());
                    // should be ignored
                    dsProxy.getConnection().close();
                } catch (SQLException ex) {
                    throw new UncategorizedSQLException("", "", ex);
                }
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        InOrder ordered = Mockito.inOrder(con);
        ordered.verify(con).setAutoCommit(false);
        ordered.verify(con).commit();
        ordered.verify(con).setAutoCommit(true);
        Mockito.verify(con, Mockito.times(2)).close();
    }

    /**
     * Test behavior if the first operation on a connection (getAutoCommit) throws SQLException.
     */
    @Test
    public void testTransactionWithExceptionOnBegin() throws Exception {
        BDDMockito.willThrow(new SQLException("Cannot begin")).given(con).getAutoCommit();
        TransactionTemplate tt = new TransactionTemplate(tm);
        try {
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
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).close();
    }

    @Test
    public void testTransactionWithExceptionOnCommit() throws Exception {
        BDDMockito.willThrow(new SQLException("Cannot commit")).given(con).commit();
        TransactionTemplate tt = new TransactionTemplate(tm);
        try {
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
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).close();
    }

    @Test
    public void testTransactionWithExceptionOnCommitAndRollbackOnCommitFailure() throws Exception {
        BDDMockito.willThrow(new SQLException("Cannot commit")).given(con).commit();
        tm.setRollbackOnCommitFailure(true);
        TransactionTemplate tt = new TransactionTemplate(tm);
        try {
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
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).rollback();
        Mockito.verify(con).close();
    }

    @Test
    public void testTransactionWithExceptionOnRollback() throws Exception {
        BDDMockito.given(con.getAutoCommit()).willReturn(true);
        BDDMockito.willThrow(new SQLException("Cannot rollback")).given(con).rollback();
        TransactionTemplate tt = new TransactionTemplate(tm);
        try {
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                    status.setRollbackOnly();
                }
            });
            Assert.fail("Should have thrown TransactionSystemException");
        } catch (TransactionSystemException ex) {
            // expected
        }
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        InOrder ordered = Mockito.inOrder(con);
        ordered.verify(con).setAutoCommit(false);
        ordered.verify(con).rollback();
        ordered.verify(con).setAutoCommit(true);
        Mockito.verify(con).close();
    }

    @Test
    public void testTransactionWithPropagationSupports() throws Exception {
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_SUPPORTS);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
                Assert.assertTrue("Is not new transaction", (!(status.isNewTransaction())));
                Assert.assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
                Assert.assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
    }

    @Test
    public void testTransactionWithPropagationNotSupported() throws Exception {
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_NOT_SUPPORTED);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
                Assert.assertTrue("Is not new transaction", (!(status.isNewTransaction())));
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
    }

    @Test
    public void testTransactionWithPropagationNever() throws Exception {
        TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_NEVER);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
                Assert.assertTrue("Is not new transaction", (!(status.isNewTransaction())));
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
    }

    @Test
    public void testExistingTransactionWithPropagationNested() throws Exception {
        doTestExistingTransactionWithPropagationNested(1);
    }

    @Test
    public void testExistingTransactionWithPropagationNestedTwice() throws Exception {
        doTestExistingTransactionWithPropagationNested(2);
    }

    @Test
    public void testExistingTransactionWithPropagationNestedAndRollback() throws Exception {
        DatabaseMetaData md = Mockito.mock(DatabaseMetaData.class);
        Savepoint sp = Mockito.mock(Savepoint.class);
        BDDMockito.given(md.supportsSavepoints()).willReturn(true);
        BDDMockito.given(con.getMetaData()).willReturn(md);
        BDDMockito.given(con.setSavepoint("SAVEPOINT_1")).willReturn(sp);
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_NESTED);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertTrue("Isn't nested transaction", (!(status.hasSavepoint())));
                tt.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                        Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(ds));
                        Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                        Assert.assertTrue("Isn't new transaction", (!(status.isNewTransaction())));
                        Assert.assertTrue("Is nested transaction", status.hasSavepoint());
                        status.setRollbackOnly();
                    }
                });
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertTrue("Isn't nested transaction", (!(status.hasSavepoint())));
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).rollback(sp);
        Mockito.verify(con).releaseSavepoint(sp);
        Mockito.verify(con).commit();
        Mockito.verify(con).isReadOnly();
        Mockito.verify(con).close();
    }

    @Test
    public void testExistingTransactionWithPropagationNestedAndRequiredRollback() throws Exception {
        DatabaseMetaData md = Mockito.mock(DatabaseMetaData.class);
        Savepoint sp = Mockito.mock(Savepoint.class);
        BDDMockito.given(md.supportsSavepoints()).willReturn(true);
        BDDMockito.given(con.getMetaData()).willReturn(md);
        BDDMockito.given(con.setSavepoint("SAVEPOINT_1")).willReturn(sp);
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_NESTED);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertTrue("Isn't nested transaction", (!(status.hasSavepoint())));
                try {
                    tt.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                            Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(ds));
                            Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                            Assert.assertTrue("Isn't new transaction", (!(status.isNewTransaction())));
                            Assert.assertTrue("Is nested transaction", status.hasSavepoint());
                            TransactionTemplate ntt = new TransactionTemplate(tm);
                            ntt.execute(new TransactionCallbackWithoutResult() {
                                @Override
                                protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                                    Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(ds));
                                    Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                                    Assert.assertTrue("Isn't new transaction", (!(status.isNewTransaction())));
                                    Assert.assertTrue("Is regular transaction", (!(status.hasSavepoint())));
                                    throw new IllegalStateException();
                                }
                            });
                        }
                    });
                    Assert.fail("Should have thrown IllegalStateException");
                } catch (IllegalStateException ex) {
                    // expected
                }
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertTrue("Isn't nested transaction", (!(status.hasSavepoint())));
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).rollback(sp);
        Mockito.verify(con).releaseSavepoint(sp);
        Mockito.verify(con).commit();
        Mockito.verify(con).isReadOnly();
        Mockito.verify(con).close();
    }

    @Test
    public void testExistingTransactionWithPropagationNestedAndRequiredRollbackOnly() throws Exception {
        DatabaseMetaData md = Mockito.mock(DatabaseMetaData.class);
        Savepoint sp = Mockito.mock(Savepoint.class);
        BDDMockito.given(md.supportsSavepoints()).willReturn(true);
        BDDMockito.given(con.getMetaData()).willReturn(md);
        BDDMockito.given(con.setSavepoint("SAVEPOINT_1")).willReturn(sp);
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_NESTED);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertTrue("Isn't nested transaction", (!(status.hasSavepoint())));
                try {
                    tt.execute(new TransactionCallbackWithoutResult() {
                        @Override
                        protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                            Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(ds));
                            Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                            Assert.assertTrue("Isn't new transaction", (!(status.isNewTransaction())));
                            Assert.assertTrue("Is nested transaction", status.hasSavepoint());
                            TransactionTemplate ntt = new TransactionTemplate(tm);
                            ntt.execute(new TransactionCallbackWithoutResult() {
                                @Override
                                protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                                    Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(ds));
                                    Assert.assertTrue("Synchronization active", TransactionSynchronizationManager.isSynchronizationActive());
                                    Assert.assertTrue("Isn't new transaction", (!(status.isNewTransaction())));
                                    Assert.assertTrue("Is regular transaction", (!(status.hasSavepoint())));
                                    status.setRollbackOnly();
                                }
                            });
                        }
                    });
                    Assert.fail("Should have thrown UnexpectedRollbackException");
                } catch (UnexpectedRollbackException ex) {
                    // expected
                }
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Assert.assertTrue("Isn't nested transaction", (!(status.hasSavepoint())));
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).rollback(sp);
        Mockito.verify(con).releaseSavepoint(sp);
        Mockito.verify(con).commit();
        Mockito.verify(con).isReadOnly();
        Mockito.verify(con).close();
    }

    @Test
    public void testExistingTransactionWithManualSavepoint() throws Exception {
        DatabaseMetaData md = Mockito.mock(DatabaseMetaData.class);
        Savepoint sp = Mockito.mock(Savepoint.class);
        BDDMockito.given(md.supportsSavepoints()).willReturn(true);
        BDDMockito.given(con.getMetaData()).willReturn(md);
        BDDMockito.given(con.setSavepoint("SAVEPOINT_1")).willReturn(sp);
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_NESTED);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Object savepoint = status.createSavepoint();
                status.releaseSavepoint(savepoint);
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).releaseSavepoint(sp);
        Mockito.verify(con).commit();
        Mockito.verify(con).close();
        Mockito.verify(ds).getConnection();
    }

    @Test
    public void testExistingTransactionWithManualSavepointAndRollback() throws Exception {
        DatabaseMetaData md = Mockito.mock(DatabaseMetaData.class);
        Savepoint sp = Mockito.mock(Savepoint.class);
        BDDMockito.given(md.supportsSavepoints()).willReturn(true);
        BDDMockito.given(con.getMetaData()).willReturn(md);
        BDDMockito.given(con.setSavepoint("SAVEPOINT_1")).willReturn(sp);
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_NESTED);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                Object savepoint = status.createSavepoint();
                status.rollbackToSavepoint(savepoint);
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).rollback(sp);
        Mockito.verify(con).commit();
        Mockito.verify(con).close();
    }

    @Test
    public void testTransactionWithPropagationNested() throws Exception {
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_NESTED);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).commit();
        Mockito.verify(con).close();
    }

    @Test
    public void testTransactionWithPropagationNestedAndRollback() throws Exception {
        final TransactionTemplate tt = new TransactionTemplate(tm);
        tt.setPropagationBehavior(PROPAGATION_NESTED);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Assert.assertTrue("Synchronization not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Assert.assertTrue("Is new transaction", status.isNewTransaction());
                status.setRollbackOnly();
            }
        });
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(ds))));
        Mockito.verify(con).rollback();
        Mockito.verify(con).close();
    }

    private static class TestTransactionSynchronization implements TransactionSynchronization {
        private DataSource dataSource;

        private int status;

        public boolean beforeCommitCalled;

        public boolean beforeCompletionCalled;

        public boolean afterCommitCalled;

        public boolean afterCompletionCalled;

        public Throwable afterCompletionException;

        public TestTransactionSynchronization(DataSource dataSource, int status) {
            this.dataSource = dataSource;
            this.status = status;
        }

        @Override
        public void suspend() {
        }

        @Override
        public void resume() {
        }

        @Override
        public void flush() {
        }

        @Override
        public void beforeCommit(boolean readOnly) {
            if ((this.status) != (TransactionSynchronization.STATUS_COMMITTED)) {
                Assert.fail("Should never be called");
            }
            Assert.assertFalse(this.beforeCommitCalled);
            this.beforeCommitCalled = true;
        }

        @Override
        public void beforeCompletion() {
            Assert.assertFalse(this.beforeCompletionCalled);
            this.beforeCompletionCalled = true;
        }

        @Override
        public void afterCommit() {
            if ((this.status) != (TransactionSynchronization.STATUS_COMMITTED)) {
                Assert.fail("Should never be called");
            }
            Assert.assertFalse(this.afterCommitCalled);
            this.afterCommitCalled = true;
        }

        @Override
        public void afterCompletion(int status) {
            try {
                doAfterCompletion(status);
            } catch (Throwable ex) {
                this.afterCompletionException = ex;
            }
        }

        protected void doAfterCompletion(int status) {
            Assert.assertFalse(this.afterCompletionCalled);
            this.afterCompletionCalled = true;
            Assert.assertTrue((status == (this.status)));
            Assert.assertTrue(TransactionSynchronizationManager.hasResource(this.dataSource));
        }
    }
}

