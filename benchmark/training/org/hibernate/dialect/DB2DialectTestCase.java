/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect;


import Column.DEFAULT_PRECISION;
import Column.DEFAULT_SCALE;
import java.sql.Types;
import org.hibernate.engine.spi.RowSelection;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * DB2 dialect related test cases
 *
 * @author Hardy Ferentschik
 */
public class DB2DialectTestCase extends BaseUnitTestCase {
    private final DB2Dialect dialect = new DB2Dialect();

    @Test
    @TestForIssue(jiraKey = "HHH-6866")
    public void testGetDefaultBinaryTypeName() {
        String actual = dialect.getTypeName(Types.BINARY);
        Assert.assertEquals("The default column length is 255, but char length on DB2 is limited to 254", "varchar($l) for bit data", actual);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-6866")
    public void testGetExplicitBinaryTypeName() {
        // lower bound
        String actual = dialect.getTypeName(Types.BINARY, 1, DEFAULT_PRECISION, DEFAULT_SCALE);
        Assert.assertEquals("Wrong binary type", "char(1) for bit data", actual);
        // upper bound
        actual = dialect.getTypeName(Types.BINARY, 254, DEFAULT_PRECISION, DEFAULT_SCALE);
        Assert.assertEquals("Wrong binary type. 254 is the max length in DB2", "char(254) for bit data", actual);
        // exceeding upper bound
        actual = dialect.getTypeName(Types.BINARY, 255, DEFAULT_PRECISION, DEFAULT_SCALE);
        Assert.assertEquals("Wrong binary type. Should be varchar for length > 254", "varchar(255) for bit data", actual);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12369")
    public void testIntegerOverflowForMaxResults() {
        RowSelection rowSelection = new RowSelection();
        rowSelection.setFirstRow(1);
        rowSelection.setMaxRows(Integer.MAX_VALUE);
        String sql = dialect.getLimitHandler().processSql("select a.id from tbl_a a order by a.id", rowSelection);
        Assert.assertTrue(("Integer overflow for max rows in: " + sql), sql.contains("fetch first 2147483647 rows only"));
    }
}

