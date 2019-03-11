/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.hql.internal.QuerySplitter;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class QuerySplitterTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testQueryWithEntityNameAsStringLiteral() {
        final String qry = "select e from Employee a where e.name = ', Employee Number 1'";
        String[] results = QuerySplitter.concreteQueries(qry, sessionFactory());
        Assert.assertEquals(1, results.length);
        Assert.assertEquals("select e from org.hibernate.test.hql.QuerySplitterTest$Employee a where e.name = ', Employee Number 1'", results[0]);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7973")
    public void testQueryWithEntityNameAsStringLiteral2() {
        final String qry = "from Employee where name = 'He is the, Employee Number 1'";
        String[] results = QuerySplitter.concreteQueries(qry, sessionFactory());
        Assert.assertEquals(1, results.length);
        Assert.assertEquals("from org.hibernate.test.hql.QuerySplitterTest$Employee where name = 'He is the, Employee Number 1'", results[0]);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7973")
    public void testQueryWithEntityNameAsStringLiteralAndEscapeQuoteChar() {
        final String qry = "from Employee where name = '''He is '' the, Employee Number 1'''";
        String[] results = QuerySplitter.concreteQueries(qry, sessionFactory());
        Assert.assertEquals(1, results.length);
        Assert.assertEquals("from org.hibernate.test.hql.QuerySplitterTest$Employee where name = '''He is '' the, Employee Number 1'''", results[0]);
    }

    @Entity(name = "Employee")
    @Table(name = "tabEmployees")
    public class Employee {
        @Id
        private long id;

        private String name;

        public Employee() {
        }

        public Employee(long id, String strName) {
            this();
            this.name = strName;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String strName) {
            this.name = strName;
        }
    }
}

