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
import javax.persistence.ManyToOne;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12666")
@RequiresDialect(H2Dialect.class)
public class TransientObjectExceptionHandlingTest extends BaseExceptionHandlingTest {
    public TransientObjectExceptionHandlingTest(BaseJpaOrNativeBootstrapFunctionalTestCase.BootstrapMethod bootstrapMethod, BaseExceptionHandlingTest.ExceptionHandlingSetting exceptionHandlingSetting, ExceptionExpectations exceptionExpectations) {
        super(bootstrapMethod, exceptionHandlingSetting, exceptionExpectations);
    }

    @Test
    public void testSave() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        TransientObjectExceptionHandlingTest.A a = new TransientObjectExceptionHandlingTest.A();
        a.id = 1;
        a.aInfo = new TransientObjectExceptionHandlingTest.AInfo();
        try {
            s.save(a);
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onTransientObjectOnSaveAndSaveOrUpdate(expected);
        } finally {
            tx.rollback();
            s.close();
        }
    }

    @Test
    public void testSaveOrUpdate() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        TransientObjectExceptionHandlingTest.A a = new TransientObjectExceptionHandlingTest.A();
        a.id = 1;
        a.aInfo = new TransientObjectExceptionHandlingTest.AInfo();
        try {
            s.saveOrUpdate(a);
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onTransientObjectOnSaveAndSaveOrUpdate(expected);
        } finally {
            tx.rollback();
            s.close();
        }
    }

    @Test
    public void testPersist() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        TransientObjectExceptionHandlingTest.A a = new TransientObjectExceptionHandlingTest.A();
        a.id = 1;
        a.aInfo = new TransientObjectExceptionHandlingTest.AInfo();
        try {
            s.persist(a);
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onTransientObjectOnPersistAndMergeAndFlush(expected);
        } finally {
            tx.rollback();
            s.close();
        }
    }

    @Test
    public void testMerge() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        TransientObjectExceptionHandlingTest.A a = new TransientObjectExceptionHandlingTest.A();
        a.id = 1;
        a.aInfo = new TransientObjectExceptionHandlingTest.AInfo();
        try {
            s.merge(a);
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onTransientObjectOnPersistAndMergeAndFlush(expected);
        } finally {
            tx.rollback();
            s.close();
        }
    }

    @Test
    public void testUpdateFlush() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        TransientObjectExceptionHandlingTest.A a = new TransientObjectExceptionHandlingTest.A();
        a.id = 1;
        a.aInfo = new TransientObjectExceptionHandlingTest.AInfo();
        try {
            s.update(a);
            s.flush();
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onTransientObjectOnPersistAndMergeAndFlush(expected);
        } finally {
            tx.rollback();
            s.close();
        }
    }

    @Entity(name = "A")
    public static class A {
        @Id
        private long id;

        @ManyToOne(optional = false)
        private TransientObjectExceptionHandlingTest.AInfo aInfo;
    }

    @Entity(name = "AInfo")
    public static class AInfo {
        @Id
        @GeneratedValue
        private long id;
    }
}

