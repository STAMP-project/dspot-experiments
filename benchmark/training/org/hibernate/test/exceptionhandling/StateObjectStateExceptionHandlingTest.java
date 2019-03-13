/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.exceptionhandling;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import org.hibernate.Session;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12666")
@RequiresDialect(H2Dialect.class)
public class StateObjectStateExceptionHandlingTest extends BaseExceptionHandlingTest {
    public StateObjectStateExceptionHandlingTest(BaseJpaOrNativeBootstrapFunctionalTestCase.BootstrapMethod bootstrapMethod, BaseExceptionHandlingTest.ExceptionHandlingSetting exceptionHandlingSetting, ExceptionExpectations exceptionExpectations) {
        super(bootstrapMethod, exceptionHandlingSetting, exceptionExpectations);
    }

    @Test
    public void testStaleObjectMerged() {
        Session s = openSession();
        s.beginTransaction();
        StateObjectStateExceptionHandlingTest.A a = new StateObjectStateExceptionHandlingTest.A();
        a.id = 1;
        s.persist(a);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        StateObjectStateExceptionHandlingTest.A aGet = s.get(StateObjectStateExceptionHandlingTest.A.class, a.id);
        aGet.name = "A. Name";
        s.getTransaction().commit();
        s.close();
        a.name = "Another Name";
        s = openSession();
        s.beginTransaction();
        try {
            s.merge(a);
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onStaleObjectMergeAndUpdateFlush(expected);
        } finally {
            s.getTransaction().rollback();
            s.close();
        }
    }

    @Test
    public void testStaleObjectUpdatedAndFlushed() {
        Session s = openSession();
        s.beginTransaction();
        StateObjectStateExceptionHandlingTest.A a = new StateObjectStateExceptionHandlingTest.A();
        a.id = 2;
        s.persist(a);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        StateObjectStateExceptionHandlingTest.A aGet = s.get(StateObjectStateExceptionHandlingTest.A.class, a.id);
        aGet.name = "A. Name";
        s.getTransaction().commit();
        s.close();
        a.name = "Another Name";
        s = openSession();
        s.beginTransaction();
        try {
            s.update(a);
            s.flush();
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onStaleObjectMergeAndUpdateFlush(expected);
        } finally {
            s.getTransaction().rollback();
            s.close();
        }
    }

    @Entity(name = "A")
    public static class A {
        @Id
        private long id;

        private String name;

        @Version
        @Column(name = "ver")
        private int version;
    }
}

