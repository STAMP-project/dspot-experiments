/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.transaction;


import Book_.name;
import LockModeType.PESSIMISTIC_WRITE;
import SynchronizationType.UNSYNCHRONIZED;
import TestingJtaPlatformImpl.INSTANCE;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests of the JPA 2.1 added {@link SynchronizationType} handling.  {@link SynchronizationType#SYNCHRONIZED} is
 * the same as 2.0 behavior, so we do not explicitly test for that ({@link TransactionJoiningTest} handles it).
 * Tests here specifically test the {@link SynchronizationType#UNSYNCHRONIZED} behavior
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-7451")
public class SynchronizationTypeTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testUnSynchronizedExplicitJoinHandling() throws Exception {
        // JPA 2.1 adds this notion allowing to open an EM using a specified "SynchronizationType".
        Assert.assertFalse(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        EntityManager entityManager = entityManagerFactory().createEntityManager(UNSYNCHRONIZED, null);
        TransactionJoinHandlingChecker.validateExplicitJoiningHandling(entityManager);
    }

    @Test
    public void testImplicitJoining() throws Exception {
        // here the transaction is started before the EM is opened.  Because the SynchronizationType is UNSYNCHRONIZED
        // though, it should not auto join the transaction
        Assert.assertFalse("setup problem", JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        INSTANCE.getTransactionManager().begin();
        Assert.assertTrue("setup problem", JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        EntityManager entityManager = entityManagerFactory().createEntityManager(UNSYNCHRONIZED, null);
        SharedSessionContractImplementor session = entityManager.unwrap(SharedSessionContractImplementor.class);
        ExtraAssertions.assertTyping(JtaTransactionCoordinatorImpl.class, session.getTransactionCoordinator());
        JtaTransactionCoordinatorImpl transactionCoordinator = ((JtaTransactionCoordinatorImpl) (session.getTransactionCoordinator()));
        Assert.assertFalse("EM was auto joined on creation", transactionCoordinator.isSynchronizationRegistered());
        Assert.assertTrue("EM was auto joined on creation", transactionCoordinator.isActive());
        Assert.assertFalse("EM was auto joined on creation", transactionCoordinator.isJoined());
        session.getFlushMode();
        Assert.assertFalse(transactionCoordinator.isSynchronizationRegistered());
        Assert.assertTrue(transactionCoordinator.isActive());
        Assert.assertFalse(transactionCoordinator.isJoined());
        entityManager.joinTransaction();
        Assert.assertTrue(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        Assert.assertTrue(transactionCoordinator.isActive());
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
    public void testDisallowedOperations() throws Exception {
        // test calling operations that are disallowed while a UNSYNCHRONIZED persistence context is not
        // yet joined/enlisted
        Assert.assertFalse("setup problem", JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        INSTANCE.getTransactionManager().begin();
        Assert.assertTrue("setup problem", JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        EntityManager entityManager = entityManagerFactory().createEntityManager(UNSYNCHRONIZED, null);
        // explicit flushing
        try {
            entityManager.flush();
            Assert.fail("Expecting flush() call to fail");
        } catch (TransactionRequiredException expected) {
        }
        // bulk operations
        try {
            entityManager.createQuery("delete Book").executeUpdate();
            Assert.fail("Expecting executeUpdate() call to fail");
        } catch (TransactionRequiredException expected) {
        }
        try {
            entityManager.createQuery("update Book set name = null").executeUpdate();
            Assert.fail("Expecting executeUpdate() call to fail");
        } catch (TransactionRequiredException expected) {
        }
        try {
            CriteriaDelete<Book> deleteCriteria = entityManager.getCriteriaBuilder().createCriteriaDelete(Book.class);
            deleteCriteria.from(Book.class);
            entityManager.createQuery(deleteCriteria).executeUpdate();
            Assert.fail("Expecting executeUpdate() call to fail");
        } catch (TransactionRequiredException expected) {
        }
        try {
            CriteriaUpdate<Book> updateCriteria = entityManager.getCriteriaBuilder().createCriteriaUpdate(Book.class);
            updateCriteria.from(Book.class);
            updateCriteria.set(name, ((String) (null)));
            entityManager.createQuery(updateCriteria).executeUpdate();
            Assert.fail("Expecting executeUpdate() call to fail");
        } catch (TransactionRequiredException expected) {
        }
        try {
            entityManager.createQuery("select b from Book b").setLockMode(PESSIMISTIC_WRITE).getResultList();
            Assert.fail("Expecting attempted pessimistic lock query to fail");
        } catch (TransactionRequiredException expected) {
        }
        entityManager.close();
        INSTANCE.getTransactionManager().rollback();
    }
}

