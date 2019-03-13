/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.queryhint;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.query.Query;
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
 * @author Brett Meyer
 */
@RequiresDialect(Oracle8iDialect.class)
public class QueryHintTest extends BaseNonConfigCoreFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    @Test
    public void testQueryHint() {
        sqlStatementInterceptor.clear();
        // test Query w/ a simple Oracle optimizer hint
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Query query = s.createQuery("FROM QueryHintTest$Employee e WHERE e.department.name = :departmentName").addQueryHint("ALL_ROWS").setParameter("departmentName", "Sales");
            List results = query.list();
            assertEquals(results.size(), 2);
        });
        sqlStatementInterceptor.assertExecutedCount(1);
        Assert.assertTrue(sqlStatementInterceptor.getSqlQueries().get(0).contains("select /*+ ALL_ROWS */"));
        sqlStatementInterceptor.clear();
        // test multiple hints
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Query query = s.createQuery("FROM QueryHintTest$Employee e WHERE e.department.name = :departmentName").addQueryHint("ALL_ROWS").addQueryHint("USE_CONCAT").setParameter("departmentName", "Sales");
            List results = query.list();
            assertEquals(results.size(), 2);
        });
        sqlStatementInterceptor.assertExecutedCount(1);
        Assert.assertTrue(sqlStatementInterceptor.getSqlQueries().get(0).contains("select /*+ ALL_ROWS, USE_CONCAT */"));
        sqlStatementInterceptor.clear();
        // ensure the insertion logic can handle a comment appended to the front
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Query query = s.createQuery("FROM QueryHintTest$Employee e WHERE e.department.name = :departmentName").setComment("this is a test").addQueryHint("ALL_ROWS").setParameter("departmentName", "Sales");
            List results = query.list();
            assertEquals(results.size(), 2);
        });
        sqlStatementInterceptor.assertExecutedCount(1);
        Assert.assertTrue(sqlStatementInterceptor.getSqlQueries().get(0).contains("select /*+ ALL_ROWS */"));
        sqlStatementInterceptor.clear();
        // test Criteria
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Criteria criteria = s.createCriteria(.class).addQueryHint("ALL_ROWS").createCriteria("department").add(Restrictions.eq("name", "Sales"));
            List results = criteria.list();
            assertEquals(results.size(), 2);
        });
        sqlStatementInterceptor.assertExecutedCount(1);
        Assert.assertTrue(sqlStatementInterceptor.getSqlQueries().get(0).contains("select /*+ ALL_ROWS */"));
        sqlStatementInterceptor.clear();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12362")
    public void testQueryHintAndComment() {
        sqlStatementInterceptor.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            Query query = s.createQuery("FROM QueryHintTest$Employee e WHERE e.department.name = :departmentName").addQueryHint("ALL_ROWS").setComment("My_Query").setParameter("departmentName", "Sales");
            List results = query.list();
            assertEquals(results.size(), 2);
        });
        sqlStatementInterceptor.assertExecutedCount(1);
        Assert.assertTrue(sqlStatementInterceptor.getSqlQueries().get(0).contains("/* My_Query */ select /*+ ALL_ROWS */"));
        sqlStatementInterceptor.clear();
    }

    @Entity
    public static class Employee {
        @Id
        @GeneratedValue
        public long id;

        @ManyToOne(fetch = FetchType.LAZY)
        public QueryHintTest.Department department;
    }

    @Entity
    public static class Department {
        @Id
        @GeneratedValue
        public long id;

        public String name;
    }
}

