/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.queryhint;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Brett Meyer
 */
@RequiresDialect(SQLServer2012Dialect.class)
public class QueryHintSQLServer2012Test extends BaseCoreFunctionalTestCase {
    @Test
    public void testQueryHint() {
        QueryHintSQLServer2012Test.Department department = new QueryHintSQLServer2012Test.Department();
        department.name = "Sales";
        QueryHintSQLServer2012Test.Employee employee1 = new QueryHintSQLServer2012Test.Employee();
        employee1.department = department;
        QueryHintSQLServer2012Test.Employee employee2 = new QueryHintSQLServer2012Test.Employee();
        employee2.department = department;
        Session s = openSession();
        s.getTransaction().begin();
        s.persist(department);
        s.persist(employee1);
        s.persist(employee2);
        s.getTransaction().commit();
        s.clear();
        // test Query w/ a simple SQLServer2012 optimizer hint
        s.getTransaction().begin();
        Query query = s.createQuery("FROM QueryHintSQLServer2012Test$Employee e WHERE e.department.name = :departmentName").addQueryHint("MAXDOP 2").setParameter("departmentName", "Sales");
        List results = query.list();
        s.getTransaction().commit();
        s.clear();
        Assert.assertEquals(results.size(), 2);
        Assert.assertTrue(QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.getProcessedSql().contains("OPTION (MAXDOP 2)"));
        QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.resetProcessedSql();
        // test multiple hints
        s.getTransaction().begin();
        query = s.createQuery("FROM QueryHintSQLServer2012Test$Employee e WHERE e.department.name = :departmentName").addQueryHint("MAXDOP 2").addQueryHint("CONCAT UNION").setParameter("departmentName", "Sales");
        results = query.list();
        s.getTransaction().commit();
        s.clear();
        Assert.assertEquals(results.size(), 2);
        Assert.assertTrue(QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.getProcessedSql().contains("MAXDOP 2"));
        Assert.assertTrue(QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.getProcessedSql().contains("CONCAT UNION"));
        QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.resetProcessedSql();
        // ensure the insertion logic can handle a comment appended to the front
        s.getTransaction().begin();
        query = s.createQuery("FROM QueryHintSQLServer2012Test$Employee e WHERE e.department.name = :departmentName").setComment("this is a test").addQueryHint("MAXDOP 2").setParameter("departmentName", "Sales");
        results = query.list();
        s.getTransaction().commit();
        s.clear();
        Assert.assertEquals(results.size(), 2);
        Assert.assertTrue(QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.getProcessedSql().contains("OPTION (MAXDOP 2)"));
        QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.resetProcessedSql();
        // test Criteria
        s.getTransaction().begin();
        Criteria criteria = s.createCriteria(QueryHintSQLServer2012Test.Employee.class).addQueryHint("MAXDOP 2").createCriteria("department").add(Restrictions.eq("name", "Sales"));
        results = criteria.list();
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(results.size(), 2);
        Assert.assertTrue(QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.getProcessedSql().contains("OPTION (MAXDOP 2)"));
    }

    /**
     * Since the query hint is added to the SQL during Loader's executeQueryStatement -> preprocessSQL, rather than
     * early on during the QueryTranslator or QueryLoader initialization, there's not an easy way to check the full SQL
     * after completely processing it. Instead, use this ridiculous hack to ensure Loader actually calls Dialect. TODO:
     * This is terrible. Better ideas?
     */
    public static class QueryHintTestSQLServer2012Dialect extends SQLServer2012Dialect {
        private static String processedSql;

        @Override
        public String getQueryHintString(String sql, List<String> hints) {
            QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.processedSql = super.getQueryHintString(sql, hints);
            return QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.processedSql;
        }

        public static String getProcessedSql() {
            return QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.processedSql;
        }

        public static void resetProcessedSql() {
            QueryHintSQLServer2012Test.QueryHintTestSQLServer2012Dialect.processedSql = "";
        }
    }

    @Entity
    public static class Employee {
        @Id
        @GeneratedValue
        public long id;

        @ManyToOne
        public QueryHintSQLServer2012Test.Department department;
    }

    @Entity
    public static class Department {
        @Id
        @GeneratedValue
        public long id;

        public String name;
    }
}

