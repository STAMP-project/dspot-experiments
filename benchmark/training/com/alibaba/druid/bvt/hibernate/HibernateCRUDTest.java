package com.alibaba.druid.bvt.hibernate;


import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author yinheli [yinheli@gmail.com]
 * @unknown 2012-11-26 ??11:41
 */
public class HibernateCRUDTest extends TestCase {
    private static final Logger log = LoggerFactory.getLogger(HibernateCRUDTest.class);

    private DruidDataSource dataSource;

    private SessionFactory sessionFactory;

    /* -------- test start -------- */
    public void test_create() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            doCreate(session);
        } finally {
            if (session != null) {
                session.flush();
                session.close();
            }
        }
    }

    public void test_get() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            doCreate(session);
            doGet(session);
        } finally {
            if (session != null) {
                session.flush();
                session.close();
            }
        }
    }

    public void test_update() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            doCreate(session);
            doUpdate(session);
        } finally {
            if (session != null) {
                session.flush();
                session.close();
            }
        }
    }

    public void test_delete() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            doCreate(session);
            doDelete(session);
        } finally {
            if (session != null) {
                session.flush();
                session.close();
            }
        }
    }

    public void test_transactional_create() {
        Session session = null;
        Transaction tran = null;
        try {
            session = sessionFactory.openSession();
            tran = session.beginTransaction();
            doCreate(session);
        } finally {
            if (tran != null) {
                tran.commit();
            }
            if (session != null) {
                session.flush();
                session.close();
            }
        }
    }

    public void test_transactional_update() {
        Session session = null;
        Transaction tran = null;
        try {
            session = sessionFactory.openSession();
            tran = session.beginTransaction();
            doCreate(session);
            doUpdate(session);
        } finally {
            if (tran != null) {
                tran.commit();
            }
            if (session != null) {
                session.flush();
                session.close();
            }
        }
    }

    public void test_transactional_delete() {
        Session session = null;
        Transaction tran = null;
        try {
            session = sessionFactory.openSession();
            tran = session.beginTransaction();
            doCreate(session);
            doDelete(session);
        } finally {
            if (tran != null) {
                tran.commit();
            }
            if (session != null) {
                session.flush();
                session.close();
            }
        }
    }
}

