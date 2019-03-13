/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id;


import DialectChecks.SupportsSequences;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jdbc.SQLStatementInterceptor;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


public class SequenceGeneratorTest extends BaseNonConfigCoreFunctionalTestCase {
    private SQLStatementInterceptor sqlStatementInterceptor;

    /**
     * This seems a little trivial, but we need to guarantee that all Dialects start their sequences on a non-0 value.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-8814")
    @RequiresDialectFeature(SupportsSequences.class)
    @SkipForDialect(value = SQLServer2012Dialect.class, comment = "SQLServer2012Dialect initializes sequence to minimum value (e.g., Long.MIN_VALUE; Hibernate assumes it is uninitialized.")
    public void testStartOfSequence() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        final Person person = new Person();
        s.persist(person);
        tx.commit();
        s.close();
        Assert.assertTrue(((person.getId()) > 0));
        Assert.assertTrue(sqlStatementInterceptor.getSqlQueries().stream().filter(( sql) -> sql.contains("product_sequence")).findFirst().isPresent());
    }
}

