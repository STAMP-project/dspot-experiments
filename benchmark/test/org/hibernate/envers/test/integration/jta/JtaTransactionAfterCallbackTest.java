/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.jta;


import TestingJtaPlatformImpl.INSTANCE;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Transaction;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.IntTestEntity;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jta.TestingJtaPlatformImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 * An envers specific quest that verifies the {@link AuditProcessManager} gets flushed.
 *
 * There is a similar test called {@link org.hibernate.test.tm.AfterCompletionTest}
 * in hibernate-core which verifies that the callbacks fires.
 *
 * The premise behind this test is to verify that when a JTA transaction is aborted by
 * Arjuna's reaper thread, the original thread will still invoke the after-completion
 * callbacks making sure that the Envers {@link AuditProcessManager} gets flushed to
 * avoid memory leaks.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-12448")
public class JtaTransactionAfterCallbackTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    @Priority(10)
    public void testAuditProcessManagerFlushedOnTransactionTimeout() throws Exception {
        // We will set the timeout to 5 seconds to allow the transaction reaper to kick in for us.
        INSTANCE.getTransactionManager().setTransactionTimeout(5);
        // Begin the transaction and do some extensive 10s long work
        INSTANCE.getTransactionManager().begin();
        EntityManager entityManager = null;
        try {
            entityManager = getEntityManager();
            IntTestEntity ite = new IntTestEntity(10);
            entityManager.persist(ite);
            // Register before completion callback
            // The before causes this thread to wait until the Reaper thread aborts our transaction
            final SessionImplementor session = entityManager.unwrap(SessionImplementor.class);
            session.getActionQueue().registerProcess(new JtaTransactionAfterCallbackTest.BeforeCallbackCompletionHandler());
            TestingJtaPlatformImpl.transactionManager().commit();
        } catch (Exception e) {
            // This is expected
            assertTyping(RollbackException.class, e);
        } finally {
            try {
                if (entityManager != null) {
                    entityManager.close();
                }
            } catch (PersistenceException e) {
                // we expect this
                Assert.assertTrue(e.getMessage().contains("Transaction was rolled back in a different thread!"));
            }
            // test the audit process manager was flushed
            assertAuditProcessManagerEmpty();
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
}

