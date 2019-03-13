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
package org.apache.drill.jdbc;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import org.apache.drill.categories.JdbcTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test for Drill's implementation of Connection's methods (other than
 * main transaction-related methods in {@link ConnectionTransactionMethodsTest}).
 * TODO: When here will be more tests, they should be sorted according to the {@link Connection} methods order
 */
@Category(JdbcTest.class)
public class ConnectionTest extends JdbcTestBase {
    private static Connection connection;

    private static ExecutorService executor;

    // //////////////////////////////////////
    // Basic tests of statement creation methods (not necessarily executing
    // statements):
    // ////////
    // Simplest cases of createStatement, prepareStatement, prepareCall:
    @Test
    public void testCreateStatementBasicCaseWorks() throws SQLException {
        Statement stmt = ConnectionTest.connection.createStatement();
        ResultSet rs = stmt.executeQuery("VALUES 1");
        Assert.assertTrue(rs.next());
    }

    @Test
    public void testPrepareStatementBasicCaseWorks() throws SQLException {
        PreparedStatement stmt = ConnectionTest.connection.prepareStatement("VALUES 1");
        ResultSet rs = stmt.executeQuery();
        Assert.assertTrue(rs.next());
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testPrepareCallThrows() throws SQLException {
        try {
            ConnectionTest.connection.prepareCall("VALUES 1");
        } catch (UnsupportedOperationException e) {
            // TODO(DRILL-2769):  Purge this mapping when right exception is thrown.
            ConnectionTest.emitSupportExceptionWarning();
            throw new SQLFeatureNotSupportedException("Note: Still throwing UnsupportedOperationException ", e);
        }
    }

    // ////////
    // createStatement(int, int):
    @Test
    public void testCreateStatement_overload2_supportedCase_returns() throws SQLException {
        ConnectionTest.connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    // ////////
    // prepareStatement(String, int, int, int):
    @Test
    public void testPrepareStatement_overload2_supportedCase_returns() throws SQLException {
        ConnectionTest.connection.prepareStatement("VALUES 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    // ////////
    // prepareCall(String, int, int, int):
    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testCreateCall_overload3_throws() throws SQLException {
        try {
            ConnectionTest.connection.prepareCall("VALUES 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
        } catch (UnsupportedOperationException e) {
            // TODO(DRILL-2769):  Purge this mapping when right exception is thrown.
            ConnectionTest.emitSupportExceptionWarning();
            throw new SQLFeatureNotSupportedException("Note: Still throwing UnsupportedOperationException ", e);
        }
    }

    // ////////
    // remaining prepareStatement(...):
    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testPrepareStatement_overload4_throws() throws SQLException {
        try {
            ConnectionTest.connection.prepareStatement("VALUES 1", Statement.RETURN_GENERATED_KEYS);
        } catch (UnsupportedOperationException e) {
            // TODO(DRILL-2769):  Purge this mapping when right exception is thrown.
            ConnectionTest.emitSupportExceptionWarning();
            throw new SQLFeatureNotSupportedException("Note: Still throwing UnsupportedOperationException ", e);
        }
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testPrepareStatement_overload5_throws() throws SQLException {
        try {
            ConnectionTest.connection.prepareStatement("VALUES 1", new int[]{ 1 });
        } catch (UnsupportedOperationException e) {
            // TODO(DRILL-2769):  Purge this mapping when right exception is thrown.
            ConnectionTest.emitSupportExceptionWarning();
            throw new SQLFeatureNotSupportedException("Note: Still throwing UnsupportedOperationException ", e);
        }
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testPrepareStatement_overload6_throws() throws SQLException {
        try {
            ConnectionTest.connection.prepareStatement("VALUES 1 AS colA", new String[]{ "colA" });
        } catch (UnsupportedOperationException e) {
            // TODO(DRILL-2769):  Purge this mapping when right exception is thrown.
            ConnectionTest.emitSupportExceptionWarning();
            throw new SQLFeatureNotSupportedException("Note: Still throwing UnsupportedOperationException ", e);
        }
    }

    // //////////////////////////////////////
    // Network timeout methods:
    // ////////
    // getNetworkTimeout():
    /**
     * Tests that getNetworkTimeout() indicates no timeout set.
     */
    @Test
    public void testGetNetworkTimeoutSaysNoTimeout() throws SQLException {
        Assert.assertThat(ConnectionTest.connection.getNetworkTimeout(), CoreMatchers.equalTo(0));
    }

    // ////////
    // setNetworkTimeout(...):
    /**
     * Tests that setNetworkTimeout(...) accepts (redundantly) setting to
     *  no-timeout mode.
     */
    @Test
    public void testSetNetworkTimeoutAcceptsNotimeoutRequest() throws SQLException {
        ConnectionTest.connection.setNetworkTimeout(ConnectionTest.executor, 0);
    }

    /**
     * Tests that setNetworkTimeout(...) rejects setting a timeout.
     */
    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetNetworkTimeoutRejectsTimeoutRequest() throws SQLException {
        try {
            ConnectionTest.connection.setNetworkTimeout(ConnectionTest.executor, 1000);
        } catch (SQLFeatureNotSupportedException e) {
            // Check exception for some mention of network timeout:
            Assert.assertThat(e.getMessage(), CoreMatchers.anyOf(CoreMatchers.containsString("Timeout"), CoreMatchers.containsString("timeout")));
            throw e;
        }
    }

    /**
     * Tests that setNetworkTimeout(...) rejects setting a timeout (different
     *  value).
     */
    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetNetworkTimeoutRejectsTimeoutRequest2() throws SQLException {
        ConnectionTest.connection.setNetworkTimeout(ConnectionTest.executor, Integer.MAX_VALUE);
    }

    @Test(expected = InvalidParameterSqlException.class)
    public void testSetNetworkTimeoutRejectsBadTimeoutValue() throws SQLException {
        try {
            ConnectionTest.connection.setNetworkTimeout(ConnectionTest.executor, (-1));
        } catch (InvalidParameterSqlException e) {
            // Check exception for some mention of parameter name or semantics:
            Assert.assertThat(e.getMessage(), CoreMatchers.anyOf(CoreMatchers.containsString("milliseconds"), CoreMatchers.containsString("timeout"), CoreMatchers.containsString("Timeout")));
            throw e;
        }
    }

    @Test(expected = InvalidParameterSqlException.class)
    public void testSetNetworkTimeoutRejectsBadExecutorValue() throws SQLException {
        try {
            ConnectionTest.connection.setNetworkTimeout(null, 1);
        } catch (InvalidParameterSqlException e) {
            // Check exception for some mention of parameter name or semantics:
            Assert.assertThat(e.getMessage(), CoreMatchers.anyOf(CoreMatchers.containsString("executor"), CoreMatchers.containsString("Executor")));
            throw e;
        }
    }

    @Test
    public void testIsReadOnly() throws Exception {
        Assert.assertFalse(ConnectionTest.connection.isReadOnly());
    }
}

