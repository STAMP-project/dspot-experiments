/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.transaction;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.RollbackException;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class TransactionCommitFailureTest extends BaseUnitTestCase {
    public static final String COMMIT_FAILURE = "Error while committing the transaction";

    private static AtomicBoolean transactionFailureTrigger;

    private static AtomicBoolean connectionIsOpen;

    private EntityManagerFactory emf;

    @Test
    public void assertConnectionIsReleasedIfCommitFails() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            TransactionCommitFailureTest.transactionFailureTrigger.set(true);
            em.getTransaction().commit();
        } catch (RollbackException e) {
            Assert.assertEquals(TransactionCommitFailureTest.COMMIT_FAILURE, e.getLocalizedMessage());
        } finally {
            if (((em.getTransaction()) != null) && (em.getTransaction().isActive())) {
                em.getTransaction().rollback();
            }
            em.close();
        }
        Assert.assertEquals("The connection was not released", false, TransactionCommitFailureTest.connectionIsOpen.get());
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12285")
    public void assertConnectionIsReleasedIfRollbackFails() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Assert.assertEquals(true, TransactionCommitFailureTest.connectionIsOpen.get());
            TransactionCommitFailureTest.transactionFailureTrigger.set(true);
            em.getTransaction().rollback();
            Assert.fail("Rollback failure, Exception expected");
        } catch (Exception pe) {
            // expected
        } finally {
            em.close();
        }
        Assert.assertEquals("The connection was not released", false, TransactionCommitFailureTest.connectionIsOpen.get());
    }

    public static class ProxyConnectionProvider extends DriverManagerConnectionProviderImpl {
        @Override
        public Connection getConnection() throws SQLException {
            Connection delegate = super.getConnection();
            TransactionCommitFailureTest.connectionIsOpen.set(true);
            return ((Connection) (Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{ Connection.class }, new TransactionCommitFailureTest.ConnectionInvocationHandler(delegate))));
        }

        @Override
        public void closeConnection(Connection conn) throws SQLException {
            super.closeConnection(conn);
            TransactionCommitFailureTest.connectionIsOpen.set(false);
        }
    }

    private static class ConnectionInvocationHandler implements InvocationHandler {
        private final Connection delegate;

        public ConnectionInvocationHandler(Connection delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("commit".equals(method.getName())) {
                if (TransactionCommitFailureTest.transactionFailureTrigger.get()) {
                    throw new SQLException(TransactionCommitFailureTest.COMMIT_FAILURE);
                }
            } else
                if ("rollback".equals(method.getName())) {
                    if (TransactionCommitFailureTest.transactionFailureTrigger.get()) {
                        TransactionCommitFailureTest.transactionFailureTrigger.set(false);
                        throw new SQLException("Rollback failed!");
                    }
                }

            return method.invoke(delegate, args);
        }
    }
}

