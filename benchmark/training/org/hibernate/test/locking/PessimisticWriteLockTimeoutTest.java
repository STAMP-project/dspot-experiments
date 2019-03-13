package org.hibernate.test.locking;


import LockOptions.NO_WAIT;
import LockOptions.SKIP_LOCKED;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.dialect.SQLServer2005Dialect;
import org.hibernate.query.Query;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class PessimisticWriteLockTimeoutTest extends BaseNonConfigCoreFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    private A entity;

    @Test
    @RequiresDialect({ Oracle8iDialect.class, PostgreSQL81Dialect.class, SQLServer2005Dialect.class })
    public void testNoWait() throws IllegalAccessException, NoSuchFieldException {
        Session session = sessionFactory().openSession();
        session.beginTransaction();
        try {
            session.createQuery("select a from A a", A.class).unwrap(Query.class).setLockOptions(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE).setTimeOut(NO_WAIT)).list();
            String lockingQuery = sqlStatementInterceptor.getSqlQueries().getLast();
            Assert.assertTrue(lockingQuery.toLowerCase().contains("nowait"));
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }

    @Test
    @RequiresDialect({ Oracle8iDialect.class, PostgreSQL95Dialect.class })
    public void testSkipLocked() throws IllegalAccessException, NoSuchFieldException {
        Session session = sessionFactory().openSession();
        session.beginTransaction();
        try {
            session.createQuery("select a from A a", A.class).unwrap(Query.class).setLockOptions(new org.hibernate.LockOptions(LockMode.PESSIMISTIC_WRITE).setTimeOut(SKIP_LOCKED)).list();
            String lockingQuery = sqlStatementInterceptor.getSqlQueries().getLast();
            Assert.assertTrue(lockingQuery.toLowerCase().contains("skip locked"));
        } finally {
            session.getTransaction().commit();
            session.close();
        }
    }
}

