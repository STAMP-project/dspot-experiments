/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.fileimport;


import java.math.BigInteger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-2403")
@RequiresDialect(value = H2Dialect.class, jiraKey = "HHH-6286", comment = "Only running the tests against H2, because the sql statements in the import file are not generic. " + "This test should actually not test directly against the db")
public class MultiLineImportFileTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testImportFile() throws Exception {
        Session s = openSession();
        final Transaction tx = s.beginTransaction();
        BigInteger count = ((BigInteger) (s.createSQLQuery("SELECT COUNT(*) FROM test_data").uniqueResult()));
        Assert.assertEquals("Incorrect row number", 3L, count.longValue());
        final String multiLineText = ((String) (s.createSQLQuery("SELECT text FROM test_data WHERE id = 2").uniqueResult()));
        // "Multi-line comment line 1\n-- line 2'\n/* line 3 */"
        final String expected = String.format("Multi-line comment line 1%n-- line 2'%n/* line 3 */");
        Assert.assertEquals("Multi-line string inserted incorrectly", expected, multiLineText);
        String empty = ((String) (s.createSQLQuery("SELECT text FROM test_data WHERE id = 3").uniqueResult()));
        Assert.assertNull("NULL value inserted incorrectly", empty);
        tx.commit();
        s.close();
    }
}

