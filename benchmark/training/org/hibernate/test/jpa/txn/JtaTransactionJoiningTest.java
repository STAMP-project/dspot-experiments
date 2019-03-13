/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jpa.txn;


import TestingJtaPlatformImpl.INSTANCE;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl;
import org.hibernate.test.jpa.AbstractJPATest;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class JtaTransactionJoiningTest extends AbstractJPATest {
    @Test
    public void testExplicitJoining() throws Exception {
        Assert.assertFalse(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        SessionImplementor session = ((SessionImplementor) (sessionFactory().withOptions().autoJoinTransactions(false).openSession()));
        ExtraAssertions.assertTyping(JtaTransactionCoordinatorImpl.class, session.getTransactionCoordinator());
        JtaTransactionCoordinatorImpl transactionCoordinator = ((JtaTransactionCoordinatorImpl) (session.getTransactionCoordinator()));
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertFalse(transactionCoordinator.isJtaTransactionCurrentlyActive());
        Assert.assertFalse(transactionCoordinator.isJoined());
        session.getFlushMode();// causes a call to TransactionCoordinator#pulse

        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertFalse(transactionCoordinator.isJtaTransactionCurrentlyActive());
        Assert.assertFalse(transactionCoordinator.isJoined());
        INSTANCE.getTransactionManager().begin();
        Assert.assertTrue(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        Assert.assertTrue(transactionCoordinator.isJtaTransactionCurrentlyActive());
        Assert.assertFalse(transactionCoordinator.isJoined());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        session.getFlushMode();
        Assert.assertTrue(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        Assert.assertTrue(transactionCoordinator.isJtaTransactionCurrentlyActive());
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertFalse(transactionCoordinator.isJoined());
        transactionCoordinator.explicitJoin();
        session.getFlushMode();
        Assert.assertTrue(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        Assert.assertTrue(transactionCoordinator.isJtaTransactionCurrentlyActive());
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertTrue(transactionCoordinator.isJoined());
        close();
        INSTANCE.getTransactionManager().commit();
    }

    @Test
    public void testImplicitJoining() throws Exception {
        Assert.assertFalse(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        INSTANCE.getTransactionManager().begin();
        Assert.assertTrue(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        SessionImplementor session = ((SessionImplementor) (sessionFactory().withOptions().autoJoinTransactions(false).openSession()));
        session.getFlushMode();
    }

    @Test
    public void control() throws Exception {
        Assert.assertFalse(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        INSTANCE.getTransactionManager().begin();
        Assert.assertTrue(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        SessionImplementor session = ((SessionImplementor) (sessionFactory().openSession()));
        ExtraAssertions.assertTyping(JtaTransactionCoordinatorImpl.class, session.getTransactionCoordinator());
        JtaTransactionCoordinatorImpl transactionCoordinator = ((JtaTransactionCoordinatorImpl) (session.getTransactionCoordinator()));
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertTrue(transactionCoordinator.isActive());
        Assert.assertTrue(transactionCoordinator.isJoined());
        close();
        INSTANCE.getTransactionManager().commit();
    }
}

