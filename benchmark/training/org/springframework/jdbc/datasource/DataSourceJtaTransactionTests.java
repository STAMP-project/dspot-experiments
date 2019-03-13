/**
 * Copyright 2002-2016 the original author or authors.
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


import Status.STATUS_ACTIVE;
import Status.STATUS_NO_TRANSACTION;
import TransactionDefinition.ISOLATION_REPEATABLE_READ;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.jta.JtaTransactionObject;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 17.10.2005
 */
public class DataSourceJtaTransactionTests {
    private Connection connection;

    private DataSource dataSource;

    private UserTransaction userTransaction;

    private TransactionManager transactionManager;

    private Transaction transaction;

    @Test
    public void testJtaTransactionCommit() throws Exception {
        doTestJtaTransaction(false);
    }

    @Test
    public void testJtaTransactionRollback() throws Exception {
        doTestJtaTransaction(true);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNew() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNew(false, false, false, false);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithAccessAfterResume() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNew(false, false, true, false);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithOpenOuterConnection() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNew(false, true, false, false);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithOpenOuterConnectionAccessed() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNew(false, true, true, false);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithTransactionAwareDataSource() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNew(false, false, true, true);
    }

    @Test
    public void testJtaTransactionRollbackWithPropagationRequiresNew() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNew(true, false, false, false);
    }

    @Test
    public void testJtaTransactionRollbackWithPropagationRequiresNewWithAccessAfterResume() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNew(true, false, true, false);
    }

    @Test
    public void testJtaTransactionRollbackWithPropagationRequiresNewWithOpenOuterConnection() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNew(true, true, false, false);
    }

    @Test
    public void testJtaTransactionRollbackWithPropagationRequiresNewWithOpenOuterConnectionAccessed() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNew(true, true, true, false);
    }

    @Test
    public void testJtaTransactionRollbackWithPropagationRequiresNewWithTransactionAwareDataSource() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNew(true, false, true, true);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiredWithinSupports() throws Exception {
        doTestJtaTransactionCommitWithNewTransactionWithinEmptyTransaction(false, false);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiredWithinNotSupported() throws Exception {
        doTestJtaTransactionCommitWithNewTransactionWithinEmptyTransaction(false, true);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithinSupports() throws Exception {
        doTestJtaTransactionCommitWithNewTransactionWithinEmptyTransaction(true, false);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithinNotSupported() throws Exception {
        doTestJtaTransactionCommitWithNewTransactionWithinEmptyTransaction(true, true);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewAndSuspendException() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNewAndBeginException(true, false, false);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithOpenOuterConnectionAndSuspendException() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNewAndBeginException(true, true, false);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithTransactionAwareDataSourceAndSuspendException() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNewAndBeginException(true, false, true);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithOpenOuterConnectionAndTransactionAwareDataSourceAndSuspendException() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNewAndBeginException(true, true, true);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewAndBeginException() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNewAndBeginException(false, false, false);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithOpenOuterConnectionAndBeginException() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNewAndBeginException(false, true, false);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithOpenOuterConnectionAndTransactionAwareDataSourceAndBeginException() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNewAndBeginException(false, true, true);
    }

    @Test
    public void testJtaTransactionCommitWithPropagationRequiresNewWithTransactionAwareDataSourceAndBeginException() throws Exception {
        doTestJtaTransactionWithPropagationRequiresNewAndBeginException(false, false, true);
    }

    @Test
    public void testJtaTransactionWithConnectionHolderStillBound() throws Exception {
        @SuppressWarnings("serial")
        JtaTransactionManager ptm = new JtaTransactionManager(userTransaction) {
            @Override
            protected void doRegisterAfterCompletionWithJtaTransaction(JtaTransactionObject txObject, final List<TransactionSynchronization> synchronizations) throws RollbackException, SystemException {
                Thread async = new Thread() {
                    @Override
                    public void run() {
                        invokeAfterCompletion(synchronizations, TransactionSynchronization.STATUS_COMMITTED);
                    }
                };
                async.start();
                try {
                    async.join();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        };
        TransactionTemplate tt = new TransactionTemplate(ptm);
        Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(dataSource))));
        Assert.assertTrue("JTA synchronizations not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        BDDMockito.given(userTransaction.getStatus()).willReturn(STATUS_ACTIVE);
        for (int i = 0; i < 3; i++) {
            final boolean releaseCon = i != 1;
            tt.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                    Assert.assertTrue("JTA synchronizations active", TransactionSynchronizationManager.isSynchronizationActive());
                    Assert.assertTrue("Is existing transaction", (!(status.isNewTransaction())));
                    Connection c = DataSourceUtils.getConnection(dataSource);
                    Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(dataSource));
                    DataSourceUtils.releaseConnection(c, dataSource);
                    c = DataSourceUtils.getConnection(dataSource);
                    Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(dataSource));
                    if (releaseCon) {
                        DataSourceUtils.releaseConnection(c, dataSource);
                    }
                }
            });
            if (!releaseCon) {
                Assert.assertTrue("Still has connection holder", TransactionSynchronizationManager.hasResource(dataSource));
            } else {
                Assert.assertTrue("Hasn't thread connection", (!(TransactionSynchronizationManager.hasResource(dataSource))));
            }
            Assert.assertTrue("JTA synchronizations not active", (!(TransactionSynchronizationManager.isSynchronizationActive())));
        }
        Mockito.verify(connection, Mockito.times(3)).close();
    }

    @Test
    public void testJtaTransactionWithIsolationLevelDataSourceAdapter() throws Exception {
        BDDMockito.given(userTransaction.getStatus()).willReturn(STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE, STATUS_NO_TRANSACTION, STATUS_ACTIVE, STATUS_ACTIVE);
        final IsolationLevelDataSourceAdapter dsToUse = new IsolationLevelDataSourceAdapter();
        dsToUse.setTargetDataSource(dataSource);
        dsToUse.afterPropertiesSet();
        JtaTransactionManager ptm = new JtaTransactionManager(userTransaction);
        ptm.setAllowCustomIsolationLevels(true);
        TransactionTemplate tt = new TransactionTemplate(ptm);
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Connection c = DataSourceUtils.getConnection(dsToUse);
                Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(dsToUse));
                Assert.assertSame(connection, c);
                DataSourceUtils.releaseConnection(c, dsToUse);
            }
        });
        tt.setIsolationLevel(ISOLATION_REPEATABLE_READ);
        tt.setReadOnly(true);
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) throws RuntimeException {
                Connection c = DataSourceUtils.getConnection(dsToUse);
                Assert.assertTrue("Has thread connection", TransactionSynchronizationManager.hasResource(dsToUse));
                Assert.assertSame(connection, c);
                DataSourceUtils.releaseConnection(c, dsToUse);
            }
        });
        Mockito.verify(userTransaction, Mockito.times(2)).begin();
        Mockito.verify(userTransaction, Mockito.times(2)).commit();
        Mockito.verify(connection).setReadOnly(true);
        Mockito.verify(connection).setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        Mockito.verify(connection, Mockito.times(2)).close();
    }

    @Test
    public void testJtaTransactionWithIsolationLevelDataSourceRouter() throws Exception {
        doTestJtaTransactionWithIsolationLevelDataSourceRouter(false);
    }

    @Test
    public void testJtaTransactionWithIsolationLevelDataSourceRouterWithDataSourceLookup() throws Exception {
        doTestJtaTransactionWithIsolationLevelDataSourceRouter(true);
    }
}

