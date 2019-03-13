/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.exceptionhandling;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12666")
@RequiresDialect(H2Dialect.class)
public class TransactionExceptionHandlingTest extends BaseExceptionHandlingTest {
    public TransactionExceptionHandlingTest(BaseJpaOrNativeBootstrapFunctionalTestCase.BootstrapMethod bootstrapMethod, BaseExceptionHandlingTest.ExceptionHandlingSetting exceptionHandlingSetting, ExceptionExpectations exceptionExpectations) {
        super(bootstrapMethod, exceptionHandlingSetting, exceptionExpectations);
    }

    @Test
    public void testPersistWithGeneratedValue() throws Exception {
        Session s = openSession();
        // Get the transaction and set the timeout BEFORE calling begin()
        Transaction t = s.getTransaction();
        t.setTimeout(1);
        Assert.assertEquals((-1), getJdbcCoordinator().determineRemainingTransactionTimeOutPeriod());
        t.begin();
        Thread.sleep(1000);
        try {
            s.persist(new TransactionExceptionHandlingTest.A());
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onTransactionExceptionOnPersistAndMergeAndFlush(expected);
        } finally {
            t.rollback();
            s.close();
        }
    }

    @Test
    public void testMergeWithGeneratedValue() throws Exception {
        Session s = openSession();
        // Get the transaction and set the timeout BEFORE calling begin()
        Transaction t = s.getTransaction();
        t.setTimeout(1);
        Assert.assertEquals((-1), getJdbcCoordinator().determineRemainingTransactionTimeOutPeriod());
        t.begin();
        Thread.sleep(1000);
        try {
            s.merge(new TransactionExceptionHandlingTest.A());
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onTransactionExceptionOnPersistAndMergeAndFlush(expected);
        } finally {
            t.rollback();
            s.close();
        }
    }

    @Test
    public void testSaveWithGeneratedValue() throws Exception {
        Session s = openSession();
        // Get the transaction and set the timeout BEFORE calling begin()
        Transaction t = s.getTransaction();
        t.setTimeout(1);
        Assert.assertEquals((-1), getJdbcCoordinator().determineRemainingTransactionTimeOutPeriod());
        t.begin();
        Thread.sleep(1000);
        try {
            s.save(new TransactionExceptionHandlingTest.A());
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onTransactionExceptionOnSaveAndSaveOrUpdate(expected);
        } finally {
            t.rollback();
            s.close();
        }
    }

    @Test
    public void testSaveOrUpdateWithGeneratedValue() throws Exception {
        Session s = openSession();
        // Get the transaction and set the timeout BEFORE calling begin()
        Transaction t = s.getTransaction();
        t.setTimeout(1);
        Assert.assertEquals((-1), getJdbcCoordinator().determineRemainingTransactionTimeOutPeriod());
        t.begin();
        Thread.sleep(1000);
        try {
            s.saveOrUpdate(new TransactionExceptionHandlingTest.A());
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onTransactionExceptionOnSaveAndSaveOrUpdate(expected);
        } finally {
            t.rollback();
            s.close();
        }
    }

    @Test
    public void testFlushWithAssignedValue() throws Exception {
        Session s = openSession();
        // Get the transaction and set the timeout BEFORE calling begin()
        Transaction t = s.getTransaction();
        t.setTimeout(1);
        Assert.assertEquals((-1), getJdbcCoordinator().determineRemainingTransactionTimeOutPeriod());
        t.begin();
        Thread.sleep(1000);
        try {
            s.persist(new TransactionExceptionHandlingTest.B(1));
            s.flush();
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onTransactionExceptionOnPersistAndMergeAndFlush(expected);
        } finally {
            t.rollback();
            s.close();
        }
    }

    @Test
    public void testCommitWithAssignedValue() throws Exception {
        Session s = openSession();
        // Get the transaction and set the timeout BEFORE calling begin()
        Transaction t = s.getTransaction();
        t.setTimeout(1);
        Assert.assertEquals((-1), getJdbcCoordinator().determineRemainingTransactionTimeOutPeriod());
        t.begin();
        Thread.sleep(1000);
        try {
            s.persist(new TransactionExceptionHandlingTest.B(1));
            s.getTransaction().commit();
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onTransactionExceptionOnCommit(expected);
        } finally {
            t.rollback();
            s.close();
        }
    }

    @Entity(name = "A")
    public static class A {
        @Id
        @GeneratedValue
        private long id;
    }

    @Entity(name = "B")
    public static class B {
        @Id
        private long id;

        public B() {
        }

        public B(long id) {
            this.id = id;
        }
    }
}

