/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import org.hibernate.dialect.DB297Dialect;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * DB2 has 2 functions for getting a substring: "substr" and "substring"
 *
 * @author Gail Badner
 */
@RequiresDialect(DB297Dialect.class)
public class DB297SubStringFunctionsTest extends BaseCoreFunctionalTestCase {
    private static final DB297SubStringFunctionsTest.MostRecentStatementInspector mostRecentStatementInspector = new DB297SubStringFunctionsTest.MostRecentStatementInspector();

    @Test
    @TestForIssue(jiraKey = "HHH-11957")
    public void testSubstringWithStringUnits() {
        DB297SubStringFunctionsTest.mostRecentStatementInspector.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            String value = session.createQuery("select substring( e.description, 21, 11, octets ) from AnEntity e", .class).uniqueResult();
            assertEquals("description", value);
        });
        Assert.assertTrue(DB297SubStringFunctionsTest.mostRecentStatementInspector.mostRecentSql.contains("substring("));
        Assert.assertTrue(DB297SubStringFunctionsTest.mostRecentStatementInspector.mostRecentSql.contains("octets"));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11957")
    public void testSubstringWithoutStringUnits() {
        DB297SubStringFunctionsTest.mostRecentStatementInspector.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            String value = session.createQuery("select substring( e.description, 21, 11 ) from AnEntity e", .class).uniqueResult();
            assertEquals("description", value);
        });
        Assert.assertTrue(DB297SubStringFunctionsTest.mostRecentStatementInspector.mostRecentSql.contains("substr("));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11957")
    public void testSubstrWithStringUnits() {
        DB297SubStringFunctionsTest.mostRecentStatementInspector.clear();
        try {
            TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
                String value = session.createQuery("select substr( e.description, 21, 11, octets ) from AnEntity e", .class).uniqueResult();
                assertEquals("description", value);
            });
            Assert.fail("Should have failed because substr cannot be used with string units.");
        } catch (PersistenceException expected) {
            Assert.assertTrue(SQLGrammarException.class.isInstance(expected.getCause()));
        }
        Assert.assertTrue(DB297SubStringFunctionsTest.mostRecentStatementInspector.mostRecentSql.contains("substr("));
        Assert.assertTrue(DB297SubStringFunctionsTest.mostRecentStatementInspector.mostRecentSql.contains("octets"));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11957")
    public void testSubstrWithoutStringUnits() {
        DB297SubStringFunctionsTest.mostRecentStatementInspector.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            String value = session.createQuery("select substr( e.description, 21, 11 ) from AnEntity e", .class).uniqueResult();
            assertEquals("description", value);
        });
        Assert.assertTrue(DB297SubStringFunctionsTest.mostRecentStatementInspector.mostRecentSql.contains("substr("));
    }

    @Entity(name = "AnEntity")
    public static class AnEntity {
        @Id
        @GeneratedValue
        private long id;

        private String description;
    }

    private static class MostRecentStatementInspector implements StatementInspector {
        private String mostRecentSql;

        public String inspect(String sql) {
            mostRecentSql = sql;
            return sql;
        }

        private void clear() {
            mostRecentSql = null;
        }
    }
}

