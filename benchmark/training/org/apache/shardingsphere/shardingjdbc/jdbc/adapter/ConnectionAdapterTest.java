/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.shardingjdbc.jdbc.adapter;


import TransactionOperationType.BEGIN;
import TransactionOperationType.COMMIT;
import TransactionOperationType.ROLLBACK;
import TransactionType.BASE;
import TransactionType.XA;
import com.google.common.collect.Multimap;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.shardingsphere.shardingjdbc.common.base.AbstractShardingJDBCDatabaseAndTableTest;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ConnectionAdapterTest extends AbstractShardingJDBCDatabaseAndTableTest {
    private final String sql = "SELECT 1";

    @Test
    public void assertSetAutoCommit() throws SQLException {
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            Assert.assertTrue(actual.getAutoCommit());
            actual.setAutoCommit(false);
            actual.createStatement().executeQuery(sql);
            Assert.assertFalse(actual.getAutoCommit());
            Multimap<String, Connection> cachedConnections = getCachedConnections(actual);
            Assert.assertThat(cachedConnections.size(), CoreMatchers.is(1));
            for (Connection each : cachedConnections.values()) {
                Assert.assertFalse(each.getAutoCommit());
            }
        }
    }

    @Test
    public void assertIgnoreAutoCommitForXA() throws SQLException {
        TransactionTypeHolder.set(XA);
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            actual.setAutoCommit(true);
            Assert.assertFalse(getInvocations().contains(BEGIN));
        }
    }

    @Test
    public void assertIgnoreAutoCommitForBASE() throws SQLException {
        TransactionTypeHolder.set(BASE);
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            actual.setAutoCommit(true);
            Assert.assertFalse(getInvocations().contains(BEGIN));
        }
    }

    @Test
    public void assertCommit() throws SQLException {
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            actual.setAutoCommit(false);
            actual.createStatement().executeQuery(sql);
            actual.commit();
        }
    }

    @Test
    public void assertXACommit() throws SQLException {
        TransactionTypeHolder.set(XA);
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            actual.commit();
            Assert.assertTrue(getInvocations().contains(COMMIT));
        }
    }

    @Test
    public void assertRollback() throws SQLException {
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            actual.setAutoCommit(false);
            actual.createStatement().executeQuery(sql);
            actual.rollback();
        }
    }

    @Test
    public void assertXARollback() throws SQLException {
        TransactionTypeHolder.set(XA);
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            actual.rollback();
            Assert.assertTrue(getInvocations().contains(ROLLBACK));
        }
    }

    @Test
    public void assertClose() throws SQLException {
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            actual.createStatement().executeQuery(sql);
            actual.close();
            assertClose(actual);
        }
    }

    @Test
    public void assertSetReadOnly() throws SQLException {
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            Assert.assertTrue(actual.isReadOnly());
            actual.setReadOnly(false);
            actual.createStatement().executeQuery(sql);
            assertReadOnly(actual, false);
            actual.setReadOnly(true);
            assertReadOnly(actual, true);
        }
    }

    @Test
    public void assertGetTransactionIsolation() throws SQLException {
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            actual.createStatement().executeQuery(sql);
            Assert.assertThat(actual.getTransactionIsolation(), CoreMatchers.is(Connection.TRANSACTION_READ_COMMITTED));
        }
    }

    @Test
    public void assertSetTransactionIsolation() throws SQLException {
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            Assert.assertThat(actual.getTransactionIsolation(), CoreMatchers.is(Connection.TRANSACTION_READ_UNCOMMITTED));
            actual.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            actual.createStatement().executeQuery(sql);
            assertTransactionIsolation(actual, Connection.TRANSACTION_SERIALIZABLE);
            actual.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            assertTransactionIsolation(actual, Connection.TRANSACTION_READ_COMMITTED);
        }
    }

    @Test
    public void assertGetWarnings() throws SQLException {
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            Assert.assertNull(actual.getWarnings());
        }
    }

    @Test
    public void assertClearWarnings() throws SQLException {
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            actual.clearWarnings();
        }
    }

    @Test
    public void assertGetHoldability() throws SQLException {
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            Assert.assertThat(actual.getHoldability(), CoreMatchers.is(ResultSet.CLOSE_CURSORS_AT_COMMIT));
        }
    }

    @Test
    public void assertSetHoldability() throws SQLException {
        try (ShardingConnection actual = getShardingDataSource().getConnection()) {
            actual.setHoldability(ResultSet.CONCUR_READ_ONLY);
            Assert.assertThat(actual.getHoldability(), CoreMatchers.is(ResultSet.CLOSE_CURSORS_AT_COMMIT));
        }
    }
}

