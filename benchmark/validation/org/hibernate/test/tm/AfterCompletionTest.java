/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.tm;


import TestingJtaPlatformImpl.INSTANCE;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Transaction;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jta.TestingJtaPlatformImpl;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
public class AfterCompletionTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12448")
    public void testAfterCompletionCallbackExecutedAfterTransactionTimeout() throws Exception {
        // Set timeout to 5 seconds
        // Allows the reaper thread to abort our running thread for us
        INSTANCE.getTransactionManager().setTransactionTimeout(5);
        // Begin the transaction
        INSTANCE.getTransactionManager().begin();
        Session session = null;
        try {
            session = openSession();
            AfterCompletionTest.SimpleEntity entity = new AfterCompletionTest.SimpleEntity("Hello World");
            session.save(entity);
            // Register before and after callback handlers
            // The before causes the original thread to wait until Reaper aborts the transaction
            // The after tracks whether it is invoked since this test is to guarantee it is called
            final SessionImplementor sessionImplementor = ((SessionImplementor) (session));
            sessionImplementor.getActionQueue().registerProcess(new AfterCompletionTest.AfterCallbackCompletionHandler());
            sessionImplementor.getActionQueue().registerProcess(new AfterCompletionTest.BeforeCallbackCompletionHandler());
            TestingJtaPlatformImpl.transactionManager().commit();
        } catch (Exception e) {
            // This is expected
            ExtraAssertions.assertTyping(RollbackException.class, e);
        } finally {
            try {
                if (session != null) {
                    session.close();
                }
            } catch (HibernateException e) {
                // This is expected
                Assert.assertEquals("Transaction was rolled back in a different thread!", e.getMessage());
            }
            // verify that the callback was fired.
            Assert.assertEquals(1, AfterCompletionTest.AfterCallbackCompletionHandler.invoked);
        }
    }

    public static class BeforeCallbackCompletionHandler implements BeforeTransactionCompletionProcess {
        @Override
        public void doBeforeTransactionCompletion(SessionImplementor session) {
            try {
                // Wait for the transaction to be rolled back by the Reaper thread.
                final Transaction transaction = TestingJtaPlatformImpl.transactionManager().getTransaction();
                while ((transaction.getStatus()) != (Status.STATUS_ROLLEDBACK))
                    Thread.sleep(10);

            } catch (Exception e) {
                // we aren't concerned with this.
            }
        }
    }

    public static class AfterCallbackCompletionHandler implements AfterTransactionCompletionProcess {
        static int invoked = 0;

        @Override
        public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) {
            Assert.assertTrue((!success));
            (AfterCompletionTest.AfterCallbackCompletionHandler.invoked)++;
        }
    }

    @Entity(name = "SimpleEntity")
    public static class SimpleEntity {
        @Id
        @GeneratedValue
        private Integer id;

        private String name;

        SimpleEntity() {
        }

        SimpleEntity(String name) {
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

