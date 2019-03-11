/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect;


import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import junit.framework.TestCase;
import org.hamcrest.core.Is;
import org.hibernate.JDBCException;
import org.hibernate.PessimisticLockException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Testing of patched support for PostgreSQL Lock error detection. HHH-7251
 *
 * @author Bryan Varner
 */
public class PostgreSQL81DialectTestCase extends BaseUnitTestCase {
    @Test
    public void testDeadlockException() {
        PostgreSQL81Dialect dialect = new PostgreSQL81Dialect();
        SQLExceptionConversionDelegate delegate = dialect.buildSQLExceptionConversionDelegate();
        Assert.assertNotNull(delegate);
        JDBCException exception = delegate.convert(new SQLException("Deadlock Detected", "40P01"), "", "");
        Assert.assertTrue((exception instanceof LockAcquisitionException));
    }

    @Test
    public void testTimeoutException() {
        PostgreSQL81Dialect dialect = new PostgreSQL81Dialect();
        SQLExceptionConversionDelegate delegate = dialect.buildSQLExceptionConversionDelegate();
        Assert.assertNotNull(delegate);
        JDBCException exception = delegate.convert(new SQLException("Lock Not Available", "55P03"), "", "");
        Assert.assertTrue((exception instanceof PessimisticLockException));
    }

    @Test
    public void testExtractConstraintName() {
        PostgreSQL81Dialect dialect = new PostgreSQL81Dialect();
        SQLException psqlException = new SQLException("ERROR: duplicate key value violates unique constraint \"uk_4bm1x2ultdmq63y3h5r3eg0ej\" Detail: Key (username, server_config)=(user, 1) already exists.", "23505");
        BatchUpdateException batchUpdateException = new BatchUpdateException("Concurrent Error", "23505", null);
        batchUpdateException.setNextException(psqlException);
        String constraintName = dialect.getViolatedConstraintNameExtracter().extractConstraintName(batchUpdateException);
        Assert.assertThat(constraintName, Is.is("uk_4bm1x2ultdmq63y3h5r3eg0ej"));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8687")
    public void testMessageException() throws SQLException {
        PostgreSQL81Dialect dialect = new PostgreSQL81Dialect();
        try {
            dialect.getResultSet(Mockito.mock(CallableStatement.class), "abc");
            Assert.fail("Expected UnsupportedOperationException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof UnsupportedOperationException));
            TestCase.assertEquals("PostgreSQL only supports accessing REF_CURSOR parameters by position", e.getMessage());
        }
    }
}

