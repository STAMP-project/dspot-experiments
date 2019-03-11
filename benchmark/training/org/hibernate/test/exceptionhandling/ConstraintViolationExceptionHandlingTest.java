/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.exceptionhandling;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
public class ConstraintViolationExceptionHandlingTest extends BaseExceptionHandlingTest {
    public ConstraintViolationExceptionHandlingTest(BaseJpaOrNativeBootstrapFunctionalTestCase.BootstrapMethod bootstrapMethod, BaseExceptionHandlingTest.ExceptionHandlingSetting exceptionHandlingSetting, ExceptionExpectations exceptionExpectations) {
        super(bootstrapMethod, exceptionHandlingSetting, exceptionExpectations);
    }

    @Test
    public void testConstraintViolationOnSave() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        ConstraintViolationExceptionHandlingTest.AInfo aInfo = new ConstraintViolationExceptionHandlingTest.AInfo();
        aInfo.uniqueString = "unique";
        s.persist(aInfo);
        s.flush();
        s.clear();
        try {
            ConstraintViolationExceptionHandlingTest.AInfo anotherAInfo = new ConstraintViolationExceptionHandlingTest.AInfo();
            anotherAInfo.uniqueString = "unique";
            s.save(anotherAInfo);
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onConstraintViolationOnSaveAndSaveOrUpdate(expected);
        } finally {
            tx.rollback();
            s.close();
        }
    }

    @Test
    public void testConstraintViolationOnSaveOrUpdate() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        ConstraintViolationExceptionHandlingTest.AInfo aInfo = new ConstraintViolationExceptionHandlingTest.AInfo();
        aInfo.uniqueString = "unique";
        s.persist(aInfo);
        s.flush();
        s.clear();
        try {
            ConstraintViolationExceptionHandlingTest.AInfo anotherAInfo = new ConstraintViolationExceptionHandlingTest.AInfo();
            anotherAInfo.uniqueString = "unique";
            s.saveOrUpdate(anotherAInfo);
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onConstraintViolationOnSaveAndSaveOrUpdate(expected);
        } finally {
            tx.rollback();
            s.close();
        }
    }

    @Test
    public void testConstraintViolationOnPersist() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        ConstraintViolationExceptionHandlingTest.AInfo aInfo = new ConstraintViolationExceptionHandlingTest.AInfo();
        aInfo.uniqueString = "unique";
        s.persist(aInfo);
        s.flush();
        s.clear();
        try {
            ConstraintViolationExceptionHandlingTest.AInfo anotherAInfo = new ConstraintViolationExceptionHandlingTest.AInfo();
            anotherAInfo.uniqueString = "unique";
            s.persist(anotherAInfo);
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onConstraintViolationOnPersistAndMergeAndFlush(expected);
        } finally {
            tx.rollback();
            s.close();
        }
    }

    @Test
    public void testConstraintViolationOnMerge() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        ConstraintViolationExceptionHandlingTest.AInfo aInfo = new ConstraintViolationExceptionHandlingTest.AInfo();
        aInfo.uniqueString = "unique";
        s.persist(aInfo);
        s.flush();
        s.clear();
        try {
            ConstraintViolationExceptionHandlingTest.AInfo anotherAInfo = new ConstraintViolationExceptionHandlingTest.AInfo();
            anotherAInfo.uniqueString = "unique";
            s.merge(anotherAInfo);
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onConstraintViolationOnPersistAndMergeAndFlush(expected);
        } finally {
            tx.rollback();
            s.close();
        }
    }

    @Test
    public void testConstraintViolationUpdateFlush() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        ConstraintViolationExceptionHandlingTest.AInfo aInfo = new ConstraintViolationExceptionHandlingTest.AInfo();
        aInfo.uniqueString = "unique";
        s.persist(aInfo);
        ConstraintViolationExceptionHandlingTest.AInfo aInfo1 = new ConstraintViolationExceptionHandlingTest.AInfo();
        s.persist(aInfo1);
        s.flush();
        s.clear();
        try {
            aInfo1 = s.get(ConstraintViolationExceptionHandlingTest.AInfo.class, aInfo1.id);
            aInfo1.uniqueString = "unique";
            s.flush();
        } catch (RuntimeException expected) {
            exceptionExpectations.onConstraintViolationOnPersistAndMergeAndFlush(expected);
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
        private ConstraintViolationExceptionHandlingTest.AInfo aInfo;
    }

    @Entity(name = "AInfo")
    public static class AInfo {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @Column(unique = true)
        private String uniqueString;
    }
}

