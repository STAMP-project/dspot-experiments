/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.queryhint;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hamcrest.CoreMatchers;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jonathan Bregler
 */
@RequiresDialect(AbstractHANADialect.class)
public class QueryHintHANATest extends BaseNonConfigCoreFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void testQueryHint() {
        sqlStatementInterceptor.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Query<org.hibernate.test.queryhint.Employee> query = s.createQuery("FROM QueryHintHANATest$Employee e WHERE e.department.name = :departmentName", .class).addQueryHint("NO_CS_JOIN").setParameter("departmentName", "Sales");
            List<org.hibernate.test.queryhint.Employee> results = query.list();
            assertEquals(results.size(), 2);
        });
        sqlStatementInterceptor.assertExecutedCount(1);
        Assert.assertThat(sqlStatementInterceptor.getSqlQueries().get(0), CoreMatchers.containsString(" with hint (NO_CS_JOIN)"));
        sqlStatementInterceptor.clear();
        // test multiple hints
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Query<org.hibernate.test.queryhint.Employee> query = s.createQuery("FROM QueryHintHANATest$Employee e WHERE e.department.name = :departmentName", .class).addQueryHint("NO_CS_JOIN").addQueryHint("OPTIMIZE_METAMODEL").setParameter("departmentName", "Sales");
            List<org.hibernate.test.queryhint.Employee> results = query.list();
            assertEquals(results.size(), 2);
        });
        sqlStatementInterceptor.assertExecutedCount(1);
        Assert.assertThat(sqlStatementInterceptor.getSqlQueries().get(0), CoreMatchers.containsString(" with hint (NO_CS_JOIN,OPTIMIZE_METAMODEL)"));
        sqlStatementInterceptor.clear();
        // ensure the insertion logic can handle a comment appended to the front
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Query<org.hibernate.test.queryhint.Employee> query = s.createQuery("FROM QueryHintHANATest$Employee e WHERE e.department.name = :departmentName", .class).setComment("this is a test").addQueryHint("NO_CS_JOIN").setParameter("departmentName", "Sales");
            List<org.hibernate.test.queryhint.Employee> results = query.list();
            assertEquals(results.size(), 2);
        });
        sqlStatementInterceptor.assertExecutedCount(1);
        Assert.assertThat(sqlStatementInterceptor.getSqlQueries().get(0), CoreMatchers.containsString(" with hint (NO_CS_JOIN)"));
        sqlStatementInterceptor.clear();
        // test Criteria
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Criteria criteria = s.createCriteria(.class).addQueryHint("NO_CS_JOIN").createCriteria("department").add(Restrictions.eq("name", "Sales"));
            List<?> results = criteria.list();
            assertEquals(results.size(), 2);
        });
        sqlStatementInterceptor.assertExecutedCount(1);
        Assert.assertThat(sqlStatementInterceptor.getSqlQueries().get(0), CoreMatchers.containsString(" with hint (NO_CS_JOIN)"));
        sqlStatementInterceptor.clear();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12362")
    public void testQueryHintAndComment() {
        sqlStatementInterceptor.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Query<org.hibernate.test.queryhint.Employee> query = s.createQuery("FROM QueryHintHANATest$Employee e WHERE e.department.name = :departmentName", .class).addQueryHint("NO_CS_JOIN").setComment("My_Query").setParameter("departmentName", "Sales");
            List<org.hibernate.test.queryhint.Employee> results = query.list();
            assertEquals(results.size(), 2);
        });
        sqlStatementInterceptor.assertExecutedCount(1);
        Assert.assertThat(sqlStatementInterceptor.getSqlQueries().get(0), CoreMatchers.containsString(" with hint (NO_CS_JOIN)"));
        Assert.assertThat(sqlStatementInterceptor.getSqlQueries().get(0), CoreMatchers.containsString("/* My_Query */ select"));
        sqlStatementInterceptor.clear();
    }

    @Entity
    public static class Employee {
        @Id
        @GeneratedValue
        public long id;

        @ManyToOne(fetch = FetchType.LAZY)
        public QueryHintHANATest.Department department;
    }

    @Entity
    public static class Department {
        @Id
        @GeneratedValue
        public long id;

        public String name;
    }
}

