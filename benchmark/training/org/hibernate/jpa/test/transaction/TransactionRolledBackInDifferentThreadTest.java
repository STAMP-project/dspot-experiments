/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.transaction;


import TestingJtaPlatformImpl.INSTANCE;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import org.hibernate.HibernateException;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Recreate test failure that occurs when three threads share the same entity manager and
 * one of them calls set rollback only on its transaction.
 *
 * @author Scott Marlow
 */
public class TransactionRolledBackInDifferentThreadTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testTransactionRolledBackInDifferentThreadFailure() throws Exception {
        /**
         * The three test threads share the same entity manager.
         * The main test thread creates an EntityManager, joins it to the transaction and ends the transaction.
         * Test thread 1 joins the EntityManager to its transaction, sets rollbackonly and ends the transaction.
         * Test thread 2 attempts to join the EntityManager to its transaction but will fail with a
         *   HibernateException("Transaction was rolled back in a different thread!")
         */
        // main test thread
        INSTANCE.getTransactionManager().begin();
        final EntityManager em = entityManagerFactory().createEntityManager();
        em.joinTransaction();
        INSTANCE.getTransactionManager().commit();
        // will be set to the failing exception
        final HibernateException[] transactionRolledBackInDifferentThreadException = new HibernateException[2];
        transactionRolledBackInDifferentThreadException[0] = transactionRolledBackInDifferentThreadException[1] = null;
        // background test thread 1
        final Runnable run1 = new Runnable() {
            @Override
            public void run() {
                try {
                    INSTANCE.getTransactionManager().begin();
                    em.joinTransaction();
                    INSTANCE.getTransactionManager().setRollbackOnly();
                    INSTANCE.getTransactionManager().commit();
                } catch (javax.persistence e) {
                    if (((e.getCause()) instanceof HibernateException) && (e.getCause().getMessage().equals("Transaction was rolled back in a different thread!"))) {
                        /**
                         * Save the exception for the main test thread to fail
                         */
                        e.printStackTrace();// show the error first

                        transactionRolledBackInDifferentThreadException[0] = ((HibernateException) (e.getCause()));
                    }
                } catch (RollbackException ignored) {
                    // expected to see RollbackException: ARJUNA016053: Could not commit transaction.
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                } finally {
                    try {
                        if ((INSTANCE.getTransactionManager().getStatus()) != (Status.STATUS_NO_TRANSACTION)) {
                            INSTANCE.getTransactionManager().rollback();
                        }
                    } catch (SystemException ignore) {
                    }
                }
            }
        };
        // test thread 2
        final Runnable run2 = new Runnable() {
            @Override
            public void run() {
                try {
                    INSTANCE.getTransactionManager().begin();
                    /**
                     * the following call to em.joinTransaction() will throw:
                     *   org.hibernate.HibernateException: Transaction was rolled back in a different thread!
                     */
                    em.joinTransaction();
                    INSTANCE.getTransactionManager().commit();
                } catch (javax.persistence e) {
                    if (((e.getCause()) instanceof HibernateException) && (e.getCause().getMessage().equals("Transaction was rolled back in a different thread!"))) {
                        /**
                         * Save the exception for the main test thread to fail
                         */
                        e.printStackTrace();// show the error first

                        transactionRolledBackInDifferentThreadException[1] = ((HibernateException) (e.getCause()));
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                } finally {
                    try {
                        if ((INSTANCE.getTransactionManager().getStatus()) != (Status.STATUS_NO_TRANSACTION)) {
                            INSTANCE.getTransactionManager().rollback();
                        }
                    } catch (SystemException ignore) {
                    }
                }
            }
        };
        Thread thread = new Thread(run1, "test thread1");
        thread.start();
        thread.join();
        Thread thread2 = new Thread(run2, "test thread2");
        thread2.start();
        thread2.join();
        // show failure for exception caught in run2.run()
        if (((transactionRolledBackInDifferentThreadException[0]) != null) || ((transactionRolledBackInDifferentThreadException[1]) != null)) {
            Assert.fail(((("failure in test thread 1 = " + ((transactionRolledBackInDifferentThreadException[0]) != null ? transactionRolledBackInDifferentThreadException[0].getMessage() : "(none)")) + ", failure in test thread 2 = ") + ((transactionRolledBackInDifferentThreadException[1]) != null ? transactionRolledBackInDifferentThreadException[1].getMessage() : "(none)")));
        }
        em.close();
    }
}

