/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.testing.transaction.TransactionUtil2;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class CaseStatementTest extends BaseCoreFunctionalTestCase {
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Integer id;

        private String name;
    }

    @Test
    public void testSimpleCaseStatementFixture() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createQuery("select case p.name when 'Steve' then 'x' else 'y' end from Person p").list();
        t.commit();
        s.close();
    }

    @Test
    public void testSimpleCaseStatementWithParamResult() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createQuery("select case p.name when 'Steve' then :opt1 else p.name end from Person p").setString("opt1", "x").list();
        t.commit();
        s.close();
    }

    @Test
    public void testSimpleCaseStatementWithParamAllResults() {
        try (final SessionImplementor s = ((SessionImplementor) (openSession()))) {
            TransactionUtil2.inTransaction(s, ( session) -> {
                try {
                    s.createQuery("select case p.name when 'Steve' then :opt1 else :opt2 end from Person p").setString("opt1", "x").setString("opt2", "y").list();
                    fail("was expecting an exception");
                } catch ( e) {
                    assertTyping(.class, e.getCause());
                } catch ( expected) {
                    // expected
                }
            });
            TransactionUtil2.inTransaction(s, ( session) -> {
                s.createQuery("select case p.name when 'Steve' then cast( :opt1 as string ) else cast( :opt2 as string) end from Person p").setString("opt1", "x").setString("opt2", "y").list();
            });
            TransactionUtil2.inTransaction(s, ( session) -> {
                try {
                    s.createQuery("select case p.name when 'Steve' then :opt1 else :opt2 end from Person p").setString("opt1", "x").setString("opt2", "y").list();
                    fail("was expecting an exception");
                } catch ( e) {
                    assertTyping(.class, e.getCause());
                } catch ( expected) {
                    // expected
                }
            });
        }
    }

    @Test
    public void testSearchedCaseStatementFixture() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createQuery("select case when p.name = 'Steve' then 'x' else 'y' end from Person p").list();
        t.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13095")
    public void testSearchedCaseStatementArithmeticExpression() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.hql.Person steve = new org.hibernate.test.hql.Person();
            steve.id = 1;
            steve.name = "Steve";
            session.persist(steve);
            org.hibernate.test.hql.Person brian = new org.hibernate.test.hql.Person();
            brian.id = 2;
            brian.name = "Brian";
            session.persist(brian);
            List<Integer> values = session.createQuery("select case when p.name = 'Steve' then (p.id * 10) else p.id end from Person p order by p.id").getResultList();
            assertEquals(10, ((int) (values.get(0))));
            assertEquals(2, ((int) (values.get(1))));
        });
    }

    @Test
    public void testSearchedCaseStatementWithParamResult() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        s.createQuery("select case when p.name = 'Steve' then :opt1 else p.name end from Person p").setString("opt1", "x").list();
        t.commit();
        s.close();
    }

    @Test
    public void testSearchedCaseStatementWithAllParamResults() {
        try (final SessionImplementor s = ((SessionImplementor) (openSession()))) {
            TransactionUtil2.inTransaction(s, ( session) -> {
                try {
                    s.createQuery("select case when p.name = 'Steve' then :opt1 else :opt2 end from Person p").setString("opt1", "x").setString("opt2", "y").list();
                    fail("was expecting an exception");
                } catch ( e) {
                    assertTyping(.class, e.getCause());
                } catch ( expected) {
                    // expected
                }
            });
            TransactionUtil2.inTransaction(s, ( session) -> {
                s.createQuery("select case when p.name = 'Steve' then cast( :opt1 as string) else :opt2 end from Person p").setString("opt1", "x").setString("opt2", "y").list();
            });
        }
    }
}

