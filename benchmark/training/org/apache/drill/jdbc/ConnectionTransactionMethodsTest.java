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
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Savepoint;
import org.apache.drill.categories.JdbcTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Test for Drill's implementation of Connection's main transaction-related
 * methods.
 */
@Category(JdbcTest.class)
public class ConnectionTransactionMethodsTest {
    private static Connection connection;

    // //////////////////////////////////////
    // Transaction mode methods:
    // ////////
    // Transaction isolation level:
    @Test
    public void testGetTransactionIsolationSaysNone() throws SQLException {
        Assert.assertThat(ConnectionTransactionMethodsTest.connection.getTransactionIsolation(), CoreMatchers.equalTo(Connection.TRANSACTION_NONE));
    }

    @Test
    public void testSetTransactionIsolationNoneExitsNormally() throws SQLException {
        ConnectionTransactionMethodsTest.connection.setTransactionIsolation(Connection.TRANSACTION_NONE);
    }

    // Test trying to set to unsupported isolation levels:
    // (Sample message:  "Can't change transaction isolation level to
    // Connection.TRANSACTION_REPEATABLE_READ (from Connection.TRANSACTION_NONE).
    // (Drill is not transactional.)" (as of 2015-04-22))
    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetTransactionIsolationReadUncommittedThrows() throws SQLException {
        try {
            ConnectionTransactionMethodsTest.connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        } catch (SQLFeatureNotSupportedException e) {
            // Check a few things in an error message:
            Assert.assertThat("Missing requested-level string", e.getMessage(), CoreMatchers.containsString("TRANSACTION_READ_UNCOMMITTED"));
            Assert.assertThat("Missing (or reworded) expected description", e.getMessage(), CoreMatchers.containsString("transaction isolation level"));
            Assert.assertThat("Missing current-level string", e.getMessage(), CoreMatchers.containsString("TRANSACTION_NONE"));
            throw e;
        }
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetTransactionIsolationReadCommittedThrows() throws SQLException {
        ConnectionTransactionMethodsTest.connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetTransactionIsolationRepeatableReadThrows() throws SQLException {
        ConnectionTransactionMethodsTest.connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetTransactionIsolationSerializableThrows() throws SQLException {
        ConnectionTransactionMethodsTest.connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    }

    @Test(expected = JdbcApiSqlException.class)
    public void testSetTransactionIsolationBadIntegerThrows() throws SQLException {
        ConnectionTransactionMethodsTest.connection.setTransactionIsolation(15);// not any TRANSACTION_* value

    }

    // ////////
    // Auto-commit mode.
    @Test
    public void testGetAutoCommitSaysAuto() throws SQLException {
        // Auto-commit should always be true.
        Assert.assertThat(ConnectionTransactionMethodsTest.connection.getAutoCommit(), CoreMatchers.equalTo(true));
    }

    @Test
    public void testSetAutoCommitTrueExitsNormally() throws SQLException {
        // Setting auto-commit true (redundantly) shouldn't throw exception.
        ConnectionTransactionMethodsTest.connection.setAutoCommit(true);
    }

    // //////////////////////////////////////
    // Transaction operation methods:
    @Test(expected = JdbcApiSqlException.class)
    public void testCommitThrows() throws SQLException {
        // Should fail saying because in auto-commit mode (or maybe because not
        // supported).
        ConnectionTransactionMethodsTest.connection.commit();
    }

    @Test(expected = JdbcApiSqlException.class)
    public void testRollbackThrows() throws SQLException {
        // Should fail saying because in auto-commit mode (or maybe because not
        // supported).
        ConnectionTransactionMethodsTest.connection.rollback();
    }

    // //////////////////////////////////////
    // Savepoint methods:
    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetSavepointUnamed() throws SQLException {
        ConnectionTransactionMethodsTest.connection.setSavepoint();
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testSetSavepointNamed() throws SQLException {
        ConnectionTransactionMethodsTest.connection.setSavepoint("savepoint name");
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testRollbackSavepoint() throws SQLException {
        ConnectionTransactionMethodsTest.connection.rollback(((Savepoint) (null)));
    }

    @Test(expected = SQLFeatureNotSupportedException.class)
    public void testReleaseSavepoint() throws SQLException {
        ConnectionTransactionMethodsTest.connection.releaseSavepoint(((Savepoint) (null)));
    }
}

