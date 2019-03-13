/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.connections;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.context.internal.ThreadLocalSessionContext;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@RequiresDialect(H2Dialect.class)
public class ThreadLocalCurrentSessionTest extends ConnectionManagementTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11067")
    public void testEqualityChecking() {
        Session session1 = sessionFactory().getCurrentSession();
        Session session2 = sessionFactory().getCurrentSession();
        Assert.assertSame("== check", session1, session2);
        Assert.assertEquals("#equals check", session1, session2);
    }

    @Test
    public void testTransactionProtection() {
        Session session = sessionFactory().getCurrentSession();
        try {
            session.createQuery("from Silly");
            Assert.fail("method other than beginTransaction() allowed");
        } catch (HibernateException e) {
            // ok
        }
    }

    @Test
    public void testContextCleanup() {
        Session session = sessionFactory().getCurrentSession();
        session.beginTransaction();
        session.getTransaction().commit();
        Assert.assertFalse("session open after txn completion", session.isOpen());
        Assert.assertFalse("session still bound after txn completion", ThreadLocalCurrentSessionTest.TestableThreadLocalContext.isSessionBound(session));
        Session session2 = sessionFactory().getCurrentSession();
        Assert.assertFalse("same session returned after txn completion", (session == session2));
        session2.close();
        Assert.assertFalse("session open after closing", session2.isOpen());
        Assert.assertFalse("session still bound after closing", ThreadLocalCurrentSessionTest.TestableThreadLocalContext.isSessionBound(session2));
    }

    public static class TestableThreadLocalContext extends ThreadLocalSessionContext {
        private static ThreadLocalCurrentSessionTest.TestableThreadLocalContext me;

        public TestableThreadLocalContext(SessionFactoryImplementor factory) {
            super(factory);
            ThreadLocalCurrentSessionTest.TestableThreadLocalContext.me = this;
        }

        public static boolean isSessionBound(Session session) {
            return (((sessionMap()) != null) && (sessionMap().containsKey(factory()))) && ((sessionMap().get(factory())) == session);
        }

        public static boolean hasBind() {
            return ((sessionMap()) != null) && (sessionMap().containsKey(factory()));
        }
    }
}

