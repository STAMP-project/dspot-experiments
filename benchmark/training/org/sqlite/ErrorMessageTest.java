package org.sqlite;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.junit.rules.ExpectedException;

import static SQLiteErrorCode.SQLITE_READONLY;
import static SQLiteErrorCode.SQLITE_READONLY_DBMOVED;


public class ErrorMessageTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    static class VendorCodeMatcher extends BaseMatcher<Object> {
        final SQLiteErrorCode expected;

        VendorCodeMatcher(SQLiteErrorCode expected) {
            this.expected = expected;
        }

        public boolean matches(Object o) {
            if (!(o instanceof SQLException)) {
                return false;
            }
            SQLException e = ((SQLException) (o));
            SQLiteErrorCode ec = SQLiteErrorCode.getErrorCode(e.getErrorCode());
            return ec == (expected);
        }

        public void describeTo(Description description) {
            description.appendText("SQLException with error code ").appendText(expected.name()).appendText(" (").appendValue(expected.code).appendText(")");
        }
    }

    static class ResultCodeMatcher extends BaseMatcher<Object> {
        final SQLiteErrorCode expected;

        ResultCodeMatcher(SQLiteErrorCode expected) {
            this.expected = expected;
        }

        public boolean matches(Object o) {
            if (!(o instanceof SQLiteException)) {
                return false;
            }
            SQLiteException e = ((SQLiteException) (o));
            return (e.getResultCode()) == (expected);
        }

        public void describeTo(Description description) {
            description.appendText("SQLiteException with error code ").appendText(expected.name()).appendText(" (").appendValue(expected.code).appendText(")");
        }
    }

    @Test
    public void moved() throws IOException, SQLException {
        File from = File.createTempFile("error-message-test-moved-from", ".sqlite");
        from.deleteOnExit();
        Connection conn = DriverManager.getConnection(("jdbc:sqlite:" + (from.getAbsolutePath())));
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("create table sample(id, name)");
        stmt.executeUpdate("insert into sample values(1, \"foo\")");
        File to = File.createTempFile("error-message-test-moved-from", ".sqlite");
        Assume.assumeTrue(to.delete());
        Assume.assumeTrue(from.renameTo(to));
        thrown.expectMessage(JUnitMatchers.containsString("[SQLITE_READONLY_DBMOVED]"));
        stmt.executeUpdate("insert into sample values(2, \"bar\")");
        stmt.close();
        conn.close();
    }

    @Test
    public void writeProtected() throws IOException, SQLException {
        File file = File.createTempFile("error-message-test-write-protected", ".sqlite");
        file.deleteOnExit();
        Connection conn = DriverManager.getConnection(("jdbc:sqlite:" + (file.getAbsolutePath())));
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("create table sample(id, name)");
        stmt.executeUpdate("insert into sample values(1, \"foo\")");
        stmt.close();
        conn.close();
        Assume.assumeTrue(file.setReadOnly());
        conn = DriverManager.getConnection(("jdbc:sqlite:" + (file.getAbsolutePath())));
        stmt = conn.createStatement();
        thrown.expectMessage(JUnitMatchers.containsString("[SQLITE_READONLY]"));
        stmt.executeUpdate("insert into sample values(2, \"bar\")");
        stmt.close();
        conn.close();
    }

    @Test
    public void shouldUsePlainErrorCodeAsVendorCodeAndExtendedAsResultCode() throws IOException, SQLException {
        File from = File.createTempFile("error-message-test-plain-1", ".sqlite");
        from.deleteOnExit();
        Connection conn = DriverManager.getConnection(("jdbc:sqlite:" + (from.getAbsolutePath())));
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("create table sample(id, name)");
        stmt.executeUpdate("insert into sample values(1, \"foo\")");
        File to = File.createTempFile("error-message-test-plain-2", ".sqlite");
        Assume.assumeTrue(to.delete());
        Assume.assumeTrue(from.renameTo(to));
        thrown.expectMessage(JUnitMatchers.containsString("[SQLITE_READONLY_DBMOVED]"));
        thrown.expect(new ErrorMessageTest.VendorCodeMatcher(SQLITE_READONLY));
        thrown.expect(new ErrorMessageTest.ResultCodeMatcher(SQLITE_READONLY_DBMOVED));
        stmt.executeUpdate("insert into sample values(2, \"bar\")");
        stmt.close();
        conn.close();
    }
}

