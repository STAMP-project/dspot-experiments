/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.jdbc.proxy;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.apache.drill.categories.JdbcTest;
import org.apache.drill.test.DrillTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test of TracingProxyDriver other than loading of driver classes.
 */
@Category(JdbcTest.class)
public class TracingProxyDriverTest extends DrillTest {
    private static Driver proxyDriver;

    private static Connection proxyConnection;

    @Test
    public void testBasicProxying() throws SQLException {
        try (final Statement stmt = TracingProxyDriverTest.proxyConnection.createStatement()) {
            final ResultSet rs = stmt.executeQuery("SELECT * FROM INFORMATION_SCHEMA.CATALOGS");
            Assert.assertTrue(rs.next());
            Assert.assertThat(rs.getString(1), CoreMatchers.equalTo("DRILL"));
            Assert.assertThat(rs.getObject(1), CoreMatchers.equalTo(((Object) ("DRILL"))));
        }
    }

    private static class StdErrCapturer {
        private final PrintStream savedStdErr;

        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        private final PrintStream capturingStream = new PrintStream(buffer);

        private boolean redirected;

        StdErrCapturer() {
            savedStdErr = System.err;
        }

        void redirect() {
            Assert.assertFalse(redirected);
            redirected = true;
            System.setErr(capturingStream);
        }

        void unredirect() {
            Assert.assertTrue(redirected);
            redirected = false;
            System.setErr(savedStdErr);
        }

        String getOutput() {
            Assert.assertFalse(redirected);
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    @Test
    public void testBasicReturnTrace() throws SQLException {
        final TracingProxyDriverTest.StdErrCapturer nameThis = new TracingProxyDriverTest.StdErrCapturer();
        try {
            nameThis.redirect();
            TracingProxyDriverTest.proxyConnection.isClosed();
        } finally {
            nameThis.unredirect();
        }
        // Check captured System.err:
        final String output = nameThis.getOutput();
        final String[] lines = output.split("\n");
        Assert.assertThat((("Not 2 lines: \"\"\"" + output) + "\"\"\""), lines.length, CoreMatchers.equalTo(2));
        final String callLine = lines[0];
        final String returnLine = lines[1];
        // Expect something like current:
        // TRACER: CALL:   ((Connection) <id=3> ...) . isClosed()
        // TRACER: RETURN: ((Connection) <id=3> ...) . isClosed(), RESULT: (boolean) false
        Assert.assertThat(callLine, CoreMatchers.containsString(" CALL:"));
        Assert.assertThat(returnLine, CoreMatchers.containsString(" RETURN:"));
        Assert.assertThat(callLine, CoreMatchers.containsString("(Connection)"));
        Assert.assertThat(returnLine, CoreMatchers.containsString("(Connection)"));
        Assert.assertThat(callLine, CoreMatchers.containsString("isClosed()"));
        Assert.assertThat(returnLine, CoreMatchers.containsString("isClosed()"));
        Assert.assertThat(callLine, CoreMatchers.not(CoreMatchers.containsString(" (boolean) ")));
        Assert.assertThat(returnLine, CoreMatchers.containsString(" (boolean) "));
        Assert.assertThat(callLine, CoreMatchers.not(CoreMatchers.containsString("false")));
        Assert.assertThat(returnLine, CoreMatchers.containsString("false"));
    }

    @Test
    public void testBasicThrowTrace() throws SQLException {
        final TracingProxyDriverTest.StdErrCapturer stdErrCapturer = new TracingProxyDriverTest.StdErrCapturer();
        final Statement statement = TracingProxyDriverTest.proxyConnection.createStatement();
        statement.close();
        try {
            stdErrCapturer.redirect();
            statement.execute("");
        } catch (final SQLException e) {
            // "already closed" is expected
        } finally {
            stdErrCapturer.unredirect();
        }
        // Check captured System.err:
        final String output = stdErrCapturer.getOutput();
        final String[] lines = output.split("\n");
        Assert.assertThat((("Not 2 lines: \"\"\"" + output) + "\"\"\""), lines.length, CoreMatchers.equalTo(2));
        final String callLine = lines[0];
        final String returnLine = lines[1];
        // Expect something like current:
        // TRACER: CALL:   ((Statement) <id=6> ...) . execute( (String) "" )
        // TRACER: THROW:  ((Statement) <id=6> ...) . execute( (String) "" ), th\
        // rew: (org.apache.drill.jdbc.AlreadyClosedSqlException) org.apache.dri\
        // ll.jdbc.AlreadyClosedSqlException: Statement is already closed.
        Assert.assertThat(callLine, CoreMatchers.containsString(" CALL:"));
        Assert.assertThat(returnLine, CoreMatchers.containsString(" THROW:"));
        Assert.assertThat(callLine, CoreMatchers.containsString("(Statement)"));
        Assert.assertThat(returnLine, CoreMatchers.containsString("(Statement)"));
        Assert.assertThat(callLine, CoreMatchers.containsString("execute("));
        Assert.assertThat(returnLine, CoreMatchers.containsString("execute("));
        Assert.assertThat(callLine, CoreMatchers.not(CoreMatchers.containsString("threw:")));
        Assert.assertThat(returnLine, CoreMatchers.containsString("threw:"));
        Assert.assertThat(callLine, CoreMatchers.not(CoreMatchers.anyOf(CoreMatchers.containsString("exception"), CoreMatchers.containsString("Exception"))));
        Assert.assertThat(returnLine, CoreMatchers.anyOf(CoreMatchers.containsString("exception"), CoreMatchers.containsString("Exception")));
        Assert.assertThat(callLine, CoreMatchers.not(CoreMatchers.anyOf(CoreMatchers.containsString("closed"), CoreMatchers.containsString("Closed"))));
        Assert.assertThat(returnLine, CoreMatchers.anyOf(CoreMatchers.containsString("closed"), CoreMatchers.containsString("Closed")));
    }

    // TODO:  Clean up these assorted remnants; probably move into separate test
    // methods.
    @Test
    public void testUnsortedMethods() throws SQLException {
        // Exercise these, even though we don't check results.
        TracingProxyDriverTest.proxyDriver.getMajorVersion();
        TracingProxyDriverTest.proxyDriver.getMinorVersion();
        TracingProxyDriverTest.proxyDriver.jdbcCompliant();
        TracingProxyDriverTest.proxyDriver.getParentLogger();
        TracingProxyDriverTest.proxyDriver.getPropertyInfo("jdbc:proxy::jdbc:drill:zk=local", new Properties());
        final DatabaseMetaData dbMetaData = TracingProxyDriverTest.proxyConnection.getMetaData();
        Assert.assertThat(dbMetaData, CoreMatchers.instanceOf(DatabaseMetaData.class));
        Assert.assertThat(dbMetaData, CoreMatchers.notNullValue());
        Assert.assertThat(dbMetaData.getConnection(), CoreMatchers.sameInstance(TracingProxyDriverTest.proxyConnection));
        dbMetaData.allTablesAreSelectable();
        try {
            dbMetaData.ownUpdatesAreVisible(ResultSet.TYPE_FORWARD_ONLY);
            Assert.fail();
        } catch (SQLException | RuntimeException e) {
            // expected
        }
        final ResultSet catalogsResultSet = dbMetaData.getCatalogs();
        Assert.assertThat(catalogsResultSet, CoreMatchers.notNullValue());
        Assert.assertThat(catalogsResultSet, CoreMatchers.instanceOf(ResultSet.class));
        catalogsResultSet.next();
        catalogsResultSet.getString(1);
        catalogsResultSet.getObject(1);
        final ResultSetMetaData rsMetaData = catalogsResultSet.getMetaData();
        Assert.assertThat(rsMetaData, CoreMatchers.notNullValue());
        Assert.assertThat(rsMetaData, CoreMatchers.instanceOf(ResultSetMetaData.class));
        int colCount = rsMetaData.getColumnCount();
        for (int cx = 1; cx <= colCount; cx++) {
            catalogsResultSet.getObject(cx);
            catalogsResultSet.getString(cx);
            try {
                catalogsResultSet.getInt(cx);
                Assert.fail("Expected some kind of string-to-int exception.");
            } catch (SQLException e) {
                // expected;
            }
        }
        Assert.assertThat(TracingProxyDriverTest.proxyConnection.getMetaData(), CoreMatchers.sameInstance(dbMetaData));
        Assert.assertThat(catalogsResultSet.getMetaData(), CoreMatchers.sameInstance(rsMetaData));
    }
}

