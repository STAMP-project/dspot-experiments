/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.resource.transaction.jta;


import Status.STATUS_ACTIVE;
import Status.STATUS_NO_TRANSACTION;
import TransactionStatus.ACTIVE;
import TransactionStatus.MARKED_ROLLBACK;
import TransactionStatus.NOT_ACTIVE;
import javax.transaction.TransactionManager;
import org.hibernate.TransactionException;
import org.hibernate.resource.transaction.TransactionRequiredForJoinException;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorBuilderImpl;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.SynchronizationCallbackCoordinatorTrackingImpl;
import org.hibernate.test.resource.common.SynchronizationCollectorImpl;
import org.hibernate.test.resource.common.SynchronizationErrorImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public abstract class AbstractBasicJtaTestScenarios {
    private final TransactionCoordinatorOwnerTestingImpl owner = new TransactionCoordinatorOwnerTestingImpl();

    private JtaTransactionCoordinatorBuilderImpl transactionCoordinatorBuilder = new JtaTransactionCoordinatorBuilderImpl();

    @Test
    public void basicBmtUsageTest() throws Exception {
        final JtaTransactionCoordinatorImpl transactionCoordinator = buildTransactionCoordinator(true);
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        // begin the transaction
        transactionCoordinator.getTransactionDriverControl().begin();
        Assert.assertEquals(STATUS_ACTIVE, tm.getStatus());
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        // create and add a local Synchronization
        SynchronizationCollectorImpl localSync = new SynchronizationCollectorImpl();
        transactionCoordinator.getLocalSynchronizations().registerSynchronization(localSync);
        // commit the transaction
        transactionCoordinator.getTransactionDriverControl().commit();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertEquals(1, localSync.getBeforeCompletionCount());
        Assert.assertEquals(1, localSync.getSuccessfulCompletionCount());
        Assert.assertEquals(0, localSync.getFailedCompletionCount());
    }

    @Test
    public void rollbackBmtUsageTest() throws Exception {
        final JtaTransactionCoordinatorImpl transactionCoordinator = buildTransactionCoordinator(true);
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        // begin the transaction
        transactionCoordinator.getTransactionDriverControl().begin();
        Assert.assertEquals(STATUS_ACTIVE, tm.getStatus());
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        // create and add a local Synchronization
        SynchronizationCollectorImpl localSync = new SynchronizationCollectorImpl();
        transactionCoordinator.getLocalSynchronizations().registerSynchronization(localSync);
        // rollback the transaction
        transactionCoordinator.getTransactionDriverControl().rollback();
        // post conditions
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertEquals(0, localSync.getBeforeCompletionCount());
        Assert.assertEquals(0, localSync.getSuccessfulCompletionCount());
        Assert.assertEquals(1, localSync.getFailedCompletionCount());
    }

    @Test
    public void basicCmtUsageTest() throws Exception {
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        // begin the transaction
        tm.begin();
        final JtaTransactionCoordinatorImpl transactionCoordinator = buildTransactionCoordinator(true);
        Assert.assertEquals(STATUS_ACTIVE, tm.getStatus());
        // NOTE : because of auto-join
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        // create and add a local Synchronization
        SynchronizationCollectorImpl localSync = new SynchronizationCollectorImpl();
        transactionCoordinator.getLocalSynchronizations().registerSynchronization(localSync);
        // commit the transaction
        tm.commit();
        // post conditions
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertEquals(1, localSync.getBeforeCompletionCount());
        Assert.assertEquals(1, localSync.getSuccessfulCompletionCount());
        Assert.assertEquals(0, localSync.getFailedCompletionCount());
    }

    @Test
    public void basicCmtUsageWithPulseTest() throws Exception {
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        final JtaTransactionCoordinatorImpl transactionCoordinator = buildTransactionCoordinator(true);
        // begin the transaction
        tm.begin();
        Assert.assertEquals(STATUS_ACTIVE, tm.getStatus());
        transactionCoordinator.pulse();
        // NOTE : because of auto-join
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        transactionCoordinator.pulse();
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        // create and add a local Synchronization
        SynchronizationCollectorImpl localSync = new SynchronizationCollectorImpl();
        transactionCoordinator.getLocalSynchronizations().registerSynchronization(localSync);
        // commit the transaction
        tm.commit();
        // post conditions
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertEquals(1, localSync.getBeforeCompletionCount());
        Assert.assertEquals(1, localSync.getSuccessfulCompletionCount());
        Assert.assertEquals(0, localSync.getFailedCompletionCount());
    }

    @Test
    public void rollbackCmtUsageTest() throws Exception {
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        // begin the transaction
        tm.begin();
        Assert.assertEquals(STATUS_ACTIVE, tm.getStatus());
        final JtaTransactionCoordinatorImpl transactionCoordinator = buildTransactionCoordinator(true);
        // NOTE : because of auto-join
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        // create and add a local Synchronization
        SynchronizationCollectorImpl localSync = new SynchronizationCollectorImpl();
        transactionCoordinator.getLocalSynchronizations().registerSynchronization(localSync);
        // rollback the transaction
        tm.rollback();
        // post conditions
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertEquals(0, localSync.getBeforeCompletionCount());
        Assert.assertEquals(0, localSync.getSuccessfulCompletionCount());
        Assert.assertEquals(1, localSync.getFailedCompletionCount());
    }

    @Test
    public void explicitJoiningTest() throws Exception {
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        final JtaTransactionCoordinatorImpl transactionCoordinator = buildTransactionCoordinator(false);
        // begin the transaction
        tm.begin();
        Assert.assertEquals(STATUS_ACTIVE, tm.getStatus());
        // no auto-join now
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        transactionCoordinator.explicitJoin();
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        // create and add a local Synchronization
        SynchronizationCollectorImpl localSync = new SynchronizationCollectorImpl();
        transactionCoordinator.getLocalSynchronizations().registerSynchronization(localSync);
        // commit the transaction
        tm.commit();
        // post conditions
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertEquals(1, localSync.getBeforeCompletionCount());
        Assert.assertEquals(1, localSync.getSuccessfulCompletionCount());
        Assert.assertEquals(0, localSync.getFailedCompletionCount());
    }

    @Test
    public void jpaExplicitJoiningTest() throws Exception {
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        // begin the transaction
        tm.begin();
        Assert.assertEquals(STATUS_ACTIVE, tm.getStatus());
        final JtaTransactionCoordinatorImpl transactionCoordinator = buildTransactionCoordinator(false);
        // no auto-join now
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        transactionCoordinator.explicitJoin();
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        // create and add a local Synchronization
        SynchronizationCollectorImpl localSync = new SynchronizationCollectorImpl();
        transactionCoordinator.getLocalSynchronizations().registerSynchronization(localSync);
        // commit the transaction
        tm.commit();
        // post conditions
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertEquals(1, localSync.getBeforeCompletionCount());
        Assert.assertEquals(1, localSync.getSuccessfulCompletionCount());
        Assert.assertEquals(0, localSync.getFailedCompletionCount());
    }

    @Test
    public void assureMultipleJoinCallsNoOp() throws Exception {
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        // begin the transaction
        tm.begin();
        Assert.assertEquals(STATUS_ACTIVE, tm.getStatus());
        final JtaTransactionCoordinatorImpl transactionCoordinator = buildTransactionCoordinator(false);
        // no auto-join now
        Assert.assertFalse(transactionCoordinator.isJoined());
        transactionCoordinator.explicitJoin();
        Assert.assertTrue(transactionCoordinator.isJoined());
        transactionCoordinator.explicitJoin();
        transactionCoordinator.explicitJoin();
        transactionCoordinator.explicitJoin();
        transactionCoordinator.explicitJoin();
        Assert.assertTrue(transactionCoordinator.isJoined());
        // create and add a local Synchronization
        SynchronizationCollectorImpl localSync = new SynchronizationCollectorImpl();
        transactionCoordinator.getLocalSynchronizations().registerSynchronization(localSync);
        // commit the transaction
        tm.commit();
        // post conditions
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertEquals(1, localSync.getBeforeCompletionCount());
        Assert.assertEquals(1, localSync.getSuccessfulCompletionCount());
        Assert.assertEquals(0, localSync.getFailedCompletionCount());
    }

    @Test
    @SuppressWarnings("EmptyCatchBlock")
    public void explicitJoinOutsideTxnTest() throws Exception {
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        final JtaTransactionCoordinatorImpl transactionCoordinator = buildTransactionCoordinator(false);
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        // try to force a join, should fail...
        try {
            transactionCoordinator.explicitJoin();
            Assert.fail("Expecting explicitJoin() call outside of transaction to fail");
        } catch (TransactionRequiredForJoinException expected) {
        }
    }

    @Test
    public void basicThreadCheckingUsage() throws Exception {
        JtaTransactionCoordinatorImpl transactionCoordinator = new JtaTransactionCoordinatorImpl(transactionCoordinatorBuilder, owner, true, JtaPlatformStandardTestingImpl.INSTANCE, preferUserTransactions(), true);
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        // begin the transaction
        tm.begin();
        transactionCoordinator.explicitJoin();
        Assert.assertEquals(SynchronizationCallbackCoordinatorTrackingImpl.class, transactionCoordinator.getSynchronizationCallbackCoordinator().getClass());
        tm.commit();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertFalse(transactionCoordinator.isJoined());
        tm.begin();
        transactionCoordinator.explicitJoin();
        Assert.assertEquals(SynchronizationCallbackCoordinatorTrackingImpl.class, transactionCoordinator.getSynchronizationCallbackCoordinator().getClass());
        tm.rollback();
    }

    @Test
    @SuppressWarnings("EmptyCatchBlock")
    public void testMarkRollbackOnly() throws Exception {
        JtaTransactionCoordinatorImpl transactionCoordinator = new JtaTransactionCoordinatorImpl(transactionCoordinatorBuilder, owner, true, JtaPlatformStandardTestingImpl.INSTANCE, preferUserTransactions(), true);
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertEquals(NOT_ACTIVE, transactionCoordinator.getTransactionDriverControl().getStatus());
        transactionCoordinator.getTransactionDriverControl().begin();
        Assert.assertEquals(ACTIVE, transactionCoordinator.getTransactionDriverControl().getStatus());
        transactionCoordinator.getTransactionDriverControl().markRollbackOnly();
        Assert.assertEquals(MARKED_ROLLBACK, transactionCoordinator.getTransactionDriverControl().getStatus());
        try {
            transactionCoordinator.getTransactionDriverControl().commit();
        } catch (TransactionException expected) {
        } finally {
            Assert.assertEquals(NOT_ACTIVE, transactionCoordinator.getTransactionDriverControl().getStatus());
        }
    }

    @Test
    @SuppressWarnings("EmptyCatchBlock")
    public void testSynchronizationFailure() throws Exception {
        JtaTransactionCoordinatorImpl transactionCoordinator = new JtaTransactionCoordinatorImpl(transactionCoordinatorBuilder, owner, true, JtaPlatformStandardTestingImpl.INSTANCE, preferUserTransactions(), true);
        // pre conditions
        final TransactionManager tm = JtaPlatformStandardTestingImpl.INSTANCE.transactionManager();
        Assert.assertEquals(STATUS_NO_TRANSACTION, tm.getStatus());
        Assert.assertEquals(NOT_ACTIVE, transactionCoordinator.getTransactionDriverControl().getStatus());
        transactionCoordinator.getLocalSynchronizations().registerSynchronization(SynchronizationErrorImpl.forBefore());
        transactionCoordinator.getTransactionDriverControl().begin();
        Assert.assertEquals(ACTIVE, transactionCoordinator.getTransactionDriverControl().getStatus());
        try {
            transactionCoordinator.getTransactionDriverControl().commit();
        } catch (Exception expected) {
        } finally {
            Assert.assertEquals(NOT_ACTIVE, transactionCoordinator.getTransactionDriverControl().getStatus());
        }
    }
}

