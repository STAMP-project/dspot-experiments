/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.exceptionhandling;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12666")
@RequiresDialect(H2Dialect.class)
public class QuerySyntaxExceptionHandlingTest extends BaseExceptionHandlingTest {
    public QuerySyntaxExceptionHandlingTest(BaseJpaOrNativeBootstrapFunctionalTestCase.BootstrapMethod bootstrapMethod, BaseExceptionHandlingTest.ExceptionHandlingSetting exceptionHandlingSetting, ExceptionExpectations exceptionExpectations) {
        super(bootstrapMethod, exceptionHandlingSetting, exceptionExpectations);
    }

    @Test
    public void testInvalidQuery() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        QuerySyntaxExceptionHandlingTest.A a = new QuerySyntaxExceptionHandlingTest.A();
        a.id = 1;
        s.persist(a);
        s.flush();
        s.clear();
        try {
            s.createQuery("from A where blahblahblah").list();
            Assert.fail("should have thrown an exception");
        } catch (RuntimeException expected) {
            exceptionExpectations.onInvalidQueryExecuted(expected);
        } finally {
            tx.rollback();
            s.close();
        }
    }

    @Entity(name = "A")
    public static class A {
        @Id
        private long id;
    }
}

