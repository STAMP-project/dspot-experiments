/**
 * Copyright (C) 2013, 2014 Brett Wooldridge
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zaxxer.hikari.pool;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.UtilityElf;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Test;


public class ConnectionStateTest {
    @Test
    public void testAutoCommit() throws SQLException {
        try (HikariDataSource ds = TestElf.newHikariDataSource()) {
            ds.setAutoCommit(true);
            ds.setMinimumIdle(1);
            ds.setMaximumPoolSize(1);
            ds.setConnectionTestQuery("VALUES 1");
            ds.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
            ds.addDataSourceProperty("user", "bar");
            ds.addDataSourceProperty("password", "secret");
            ds.addDataSourceProperty("url", "baf");
            ds.addDataSourceProperty("loginTimeout", "10");
            try (Connection connection = ds.getConnection()) {
                Connection unwrap = connection.unwrap(Connection.class);
                connection.setAutoCommit(false);
                connection.close();
                Assert.assertTrue(unwrap.getAutoCommit());
            }
        }
    }

    @Test
    public void testTransactionIsolation() throws SQLException {
        try (HikariDataSource ds = TestElf.newHikariDataSource()) {
            ds.setTransactionIsolation("TRANSACTION_READ_COMMITTED");
            ds.setMinimumIdle(1);
            ds.setMaximumPoolSize(1);
            ds.setConnectionTestQuery("VALUES 1");
            ds.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
            try (Connection connection = ds.getConnection()) {
                Connection unwrap = connection.unwrap(Connection.class);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                connection.close();
                Assert.assertEquals(Connection.TRANSACTION_READ_COMMITTED, unwrap.getTransactionIsolation());
            }
        }
    }

    @Test
    public void testIsolation() throws Exception {
        HikariConfig config = TestElf.newHikariConfig();
        config.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
        config.setTransactionIsolation("TRANSACTION_REPEATABLE_READ");
        config.validate();
        int transactionIsolation = UtilityElf.getTransactionIsolation(config.getTransactionIsolation());
        Assert.assertSame(Connection.TRANSACTION_REPEATABLE_READ, transactionIsolation);
    }

    @Test
    public void testReadOnly() throws Exception {
        try (HikariDataSource ds = TestElf.newHikariDataSource()) {
            ds.setCatalog("test");
            ds.setMinimumIdle(1);
            ds.setMaximumPoolSize(1);
            ds.setConnectionTestQuery("VALUES 1");
            ds.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
            try (Connection connection = ds.getConnection()) {
                Connection unwrap = connection.unwrap(Connection.class);
                connection.setReadOnly(true);
                connection.close();
                Assert.assertFalse(unwrap.isReadOnly());
            }
        }
    }

    @Test
    public void testCatalog() throws SQLException {
        try (HikariDataSource ds = TestElf.newHikariDataSource()) {
            ds.setCatalog("test");
            ds.setMinimumIdle(1);
            ds.setMaximumPoolSize(1);
            ds.setConnectionTestQuery("VALUES 1");
            ds.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
            try (Connection connection = ds.getConnection()) {
                Connection unwrap = connection.unwrap(Connection.class);
                connection.setCatalog("other");
                connection.close();
                Assert.assertEquals("test", unwrap.getCatalog());
            }
        }
    }

    @Test
    public void testCommitTracking() throws SQLException {
        try (HikariDataSource ds = TestElf.newHikariDataSource()) {
            ds.setAutoCommit(false);
            ds.setMinimumIdle(1);
            ds.setMaximumPoolSize(1);
            ds.setConnectionTestQuery("VALUES 1");
            ds.setDataSourceClassName("com.zaxxer.hikari.mocks.StubDataSource");
            try (Connection connection = ds.getConnection()) {
                Statement statement = connection.createStatement();
                statement.execute("SELECT something");
                Assert.assertTrue(TestElf.getConnectionCommitDirtyState(connection));
                connection.commit();
                Assert.assertFalse(TestElf.getConnectionCommitDirtyState(connection));
                statement.execute("SELECT something", Statement.NO_GENERATED_KEYS);
                Assert.assertTrue(TestElf.getConnectionCommitDirtyState(connection));
                connection.rollback();
                Assert.assertFalse(TestElf.getConnectionCommitDirtyState(connection));
                ResultSet resultSet = statement.executeQuery("SELECT something");
                Assert.assertTrue(TestElf.getConnectionCommitDirtyState(connection));
                connection.rollback(null);
                Assert.assertFalse(TestElf.getConnectionCommitDirtyState(connection));
                resultSet.updateRow();
                Assert.assertTrue(TestElf.getConnectionCommitDirtyState(connection));
            }
        }
    }
}

