/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.transaction;


import Status.STATUS_ROLLEDBACK;
import SynchronizationType.UNSYNCHRONIZED;
import TestingJtaPlatformImpl.INSTANCE;
import java.util.concurrent.CountDownLatch;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
import org.hibernate.internal.SessionImpl;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Largely a copy of {@link org.hibernate.test.jpa.txn.JtaTransactionJoiningTest}
 *
 * @author Steve Ebersole
 */
public class TransactionJoiningTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testExplicitJoining() throws Exception {
        Assert.assertFalse(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        EntityManager entityManager = entityManagerFactory().createEntityManager(UNSYNCHRONIZED);
        TransactionJoinHandlingChecker.validateExplicitJoiningHandling(entityManager);
    }

    @Test
    @SuppressWarnings("EmptyCatchBlock")
    public void testExplicitJoiningTransactionRequiredException() throws Exception {
        // explicitly calling EntityManager#joinTransaction outside of an active transaction should cause
        // a TransactionRequiredException to be thrown
        EntityManager entityManager = entityManagerFactory().createEntityManager();
        Assert.assertFalse("setup problem", JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        try {
            entityManager.joinTransaction();
            Assert.fail("Expected joinTransaction() to fail since there is no active JTA transaction");
        } catch (TransactionRequiredException expected) {
        }
    }

    @Test
    public void testImplicitJoining() throws Exception {
        // here the transaction is started before the EM is opened...
        Assert.assertFalse(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        INSTANCE.getTransactionManager().begin();
        EntityManager entityManager = entityManagerFactory().createEntityManager();
        SharedSessionContractImplementor session = entityManager.unwrap(SharedSessionContractImplementor.class);
        ExtraAssertions.assertTyping(JtaTransactionCoordinatorImpl.class, session.getTransactionCoordinator());
        JtaTransactionCoordinatorImpl transactionCoordinator = ((JtaTransactionCoordinatorImpl) (session.getTransactionCoordinator()));
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertTrue(transactionCoordinator.isActive());
        Assert.assertTrue(transactionCoordinator.isJoined());
        Assert.assertTrue(entityManager.isOpen());
        Assert.assertTrue(session.isOpen());
        entityManager.close();
        Assert.assertFalse(entityManager.isOpen());
        Assert.assertFalse(session.isOpen());
        INSTANCE.getTransactionManager().commit();
        Assert.assertFalse(entityManager.isOpen());
        Assert.assertFalse(session.isOpen());
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10807")
    public void testIsJoinedAfterMarkedForRollbackImplict() throws Exception {
        Assert.assertFalse(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        INSTANCE.getTransactionManager().begin();
        EntityManager entityManager = entityManagerFactory().createEntityManager();
        SharedSessionContractImplementor session = entityManager.unwrap(SharedSessionContractImplementor.class);
        ExtraAssertions.assertTyping(JtaTransactionCoordinatorImpl.class, session.getTransactionCoordinator());
        JtaTransactionCoordinatorImpl transactionCoordinator = ((JtaTransactionCoordinatorImpl) (session.getTransactionCoordinator()));
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertTrue(transactionCoordinator.isActive());
        Assert.assertTrue(transactionCoordinator.isJoined());
        Assert.assertTrue(entityManager.isOpen());
        Assert.assertTrue(session.isOpen());
        transactionCoordinator.getTransactionDriverControl().markRollbackOnly();
        Assert.assertTrue(transactionCoordinator.isActive());
        Assert.assertTrue(transactionCoordinator.isJoined());
        Assert.assertTrue(entityManager.isJoinedToTransaction());
        INSTANCE.getTransactionManager().rollback();
        entityManager.close();
        Assert.assertFalse(entityManager.isOpen());
        Assert.assertFalse(session.isOpen());
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10807")
    public void testIsJoinedAfterMarkedForRollbackExplicit() throws Exception {
        Assert.assertFalse(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        EntityManager entityManager = entityManagerFactory().createEntityManager(UNSYNCHRONIZED);
        SharedSessionContractImplementor session = entityManager.unwrap(SharedSessionContractImplementor.class);
        Assert.assertTrue(entityManager.isOpen());
        Assert.assertTrue(session.isOpen());
        ExtraAssertions.assertTyping(JtaTransactionCoordinatorImpl.class, session.getTransactionCoordinator());
        JtaTransactionCoordinatorImpl transactionCoordinator = ((JtaTransactionCoordinatorImpl) (session.getTransactionCoordinator()));
        INSTANCE.getTransactionManager().begin();
        entityManager.joinTransaction();
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertTrue(transactionCoordinator.isActive());
        Assert.assertTrue(transactionCoordinator.isJoined());
        transactionCoordinator.getTransactionDriverControl().markRollbackOnly();
        Assert.assertTrue(transactionCoordinator.isActive());
        Assert.assertTrue(transactionCoordinator.isJoined());
        Assert.assertTrue(entityManager.isJoinedToTransaction());
        INSTANCE.getTransactionManager().rollback();
        entityManager.close();
        Assert.assertFalse(entityManager.isOpen());
        Assert.assertFalse(session.isOpen());
    }

    @Test
    public void testCloseAfterCommit() throws Exception {
        Assert.assertFalse(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        INSTANCE.getTransactionManager().begin();
        EntityManager entityManager = entityManagerFactory().createEntityManager();
        SharedSessionContractImplementor session = entityManager.unwrap(SharedSessionContractImplementor.class);
        ExtraAssertions.assertTyping(JtaTransactionCoordinatorImpl.class, session.getTransactionCoordinator());
        JtaTransactionCoordinatorImpl transactionCoordinator = ((JtaTransactionCoordinatorImpl) (session.getTransactionCoordinator()));
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertTrue(transactionCoordinator.isActive());
        Assert.assertTrue(transactionCoordinator.isJoined());
        Assert.assertTrue(entityManager.isOpen());
        Assert.assertTrue(session.isOpen());
        INSTANCE.getTransactionManager().commit();
        Assert.assertTrue(entityManager.isOpen());
        Assert.assertTrue(session.isOpen());
        entityManager.close();
        Assert.assertFalse(entityManager.isOpen());
        Assert.assertFalse(session.isOpen());
    }

    @Test
    public void testImplicitJoiningWithExtraSynchronization() throws Exception {
        Assert.assertFalse(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        INSTANCE.getTransactionManager().begin();
        EntityManager entityManager = entityManagerFactory().createEntityManager();
        SharedSessionContractImplementor session = entityManager.unwrap(SharedSessionContractImplementor.class);
        ExtraAssertions.assertTyping(JtaTransactionCoordinatorImpl.class, session.getTransactionCoordinator());
        JtaTransactionCoordinatorImpl transactionCoordinator = ((JtaTransactionCoordinatorImpl) (session.getTransactionCoordinator()));
        Assert.assertTrue(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertTrue(transactionCoordinator.isActive());
        Assert.assertTrue(transactionCoordinator.isJoined());
        entityManager.close();
        INSTANCE.getTransactionManager().commit();
    }

    /**
     * In certain JTA environments (JBossTM, etc.), a background thread (reaper)
     * can rollback a transaction if it times out.  These timeouts are rare and
     * typically come from server failures.  However, we need to handle the
     * multi-threaded nature of the transaction afterCompletion action.
     * Emulate a timeout with a simple afterCompletion call in a thread.
     * See HHH-7910
     */
    @Test
    @TestForIssue(jiraKey = "HHH-7910")
    public void testMultiThreadTransactionTimeout() throws Exception {
        INSTANCE.getTransactionManager().begin();
        EntityManager em = entityManagerFactory().createEntityManager();
        final SessionImpl sImpl = em.unwrap(SessionImpl.class);
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread() {
            public void run() {
                getSynchronizationCallbackCoordinator().afterCompletion(STATUS_ROLLEDBACK);
                latch.countDown();
            }
        };
        thread.start();
        latch.await();
        boolean caught = false;
        try {
            em.persist(new Book("The Book of Foo", 1));
        } catch (PersistenceException e) {
            caught = e.getCause().getClass().equals(HibernateException.class);
        }
        Assert.assertTrue(caught);
        // Ensure that the connection was closed by the background thread.
        caught = false;
        try {
            em.createQuery("from Book").getResultList();
        } catch (PersistenceException e) {
            // HHH-9312
            caught = true;
        } catch (Exception e) {
            caught = true;
        }
        Assert.assertTrue(caught);
        INSTANCE.getTransactionManager().rollback();
        em.close();
    }
}

