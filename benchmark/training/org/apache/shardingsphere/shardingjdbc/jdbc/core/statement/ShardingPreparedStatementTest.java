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
package org.apache.shardingsphere.shardingjdbc.jdbc.core.statement;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.shardingsphere.shardingjdbc.common.base.AbstractShardingJDBCDatabaseAndTableTest;
import org.apache.shardingsphere.shardingjdbc.jdbc.JDBCTestSQL;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingPreparedStatementTest extends AbstractShardingJDBCDatabaseAndTableTest {
    @Test
    public void assertAddBatch() throws SQLException {
        try (Connection connection = getShardingDataSource().getConnection();PreparedStatement preparedStatement = connection.prepareStatement(JDBCTestSQL.INSERT_ORDER_ITEM_WITH_ALL_PLACEHOLDERS_SQL)) {
            preparedStatement.setInt(1, 3101);
            preparedStatement.setInt(2, 11);
            preparedStatement.setInt(3, 11);
            preparedStatement.setString(4, "BATCH");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 3102);
            preparedStatement.setInt(2, 12);
            preparedStatement.setInt(3, 12);
            preparedStatement.setString(4, "BATCH");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 3111);
            preparedStatement.setInt(2, 21);
            preparedStatement.setInt(3, 21);
            preparedStatement.setString(4, "BATCH");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 3112);
            preparedStatement.setInt(2, 22);
            preparedStatement.setInt(3, 22);
            preparedStatement.setString(4, "BATCH");
            preparedStatement.addBatch();
            int[] result = preparedStatement.executeBatch();
            for (int rs : result) {
                Assert.assertThat(rs, CoreMatchers.is(1));
            }
        }
    }

    @Test
    public void assertAddBatchWithoutGenerateKeyColumn() throws SQLException {
        String sql = "INSERT INTO t_order_item (order_id, user_id, status) VALUES (?, ?, ?)";
        try (Connection connection = getShardingDataSource().getConnection();PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);Statement queryStatement = connection.createStatement()) {
            preparedStatement.setInt(1, 11);
            preparedStatement.setInt(2, 11);
            preparedStatement.setString(3, "BATCH");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 12);
            preparedStatement.setInt(2, 12);
            preparedStatement.setString(3, "BATCH");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 21);
            preparedStatement.setInt(2, 21);
            preparedStatement.setString(3, "BATCH");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 22);
            preparedStatement.setInt(2, 22);
            preparedStatement.setString(3, "BATCH");
            preparedStatement.addBatch();
            int[] result = preparedStatement.executeBatch();
            for (int rs : result) {
                Assert.assertThat(rs, CoreMatchers.is(1));
            }
            ResultSet generateKeyResultSet = preparedStatement.getGeneratedKeys();
            Assert.assertTrue(generateKeyResultSet.next());
            Assert.assertThat(generateKeyResultSet.getLong(1), CoreMatchers.is(1L));
            Assert.assertTrue(generateKeyResultSet.next());
            Assert.assertThat(generateKeyResultSet.getLong(1), CoreMatchers.is(2L));
            Assert.assertTrue(generateKeyResultSet.next());
            Assert.assertThat(generateKeyResultSet.getLong(1), CoreMatchers.is(3L));
            Assert.assertTrue(generateKeyResultSet.next());
            Assert.assertThat(generateKeyResultSet.getLong(1), CoreMatchers.is(4L));
            Assert.assertFalse(generateKeyResultSet.next());
            try (ResultSet rs = queryStatement.executeQuery(String.format(JDBCTestSQL.SELECT_WITH_AUTO_INCREMENT_COLUMN_SQL, 11, 11))) {
                Assert.assertTrue(rs.next());
                Assert.assertThat(rs.getInt(1), CoreMatchers.is(1));
            }
            try (ResultSet rs = queryStatement.executeQuery(String.format(JDBCTestSQL.SELECT_WITH_AUTO_INCREMENT_COLUMN_SQL, 12, 12))) {
                Assert.assertTrue(rs.next());
                Assert.assertThat(rs.getInt(1), CoreMatchers.is(2));
            }
            try (ResultSet rs = queryStatement.executeQuery(String.format(JDBCTestSQL.SELECT_WITH_AUTO_INCREMENT_COLUMN_SQL, 21, 21))) {
                Assert.assertTrue(rs.next());
                Assert.assertThat(rs.getInt(1), CoreMatchers.is(3));
            }
            try (ResultSet rs = queryStatement.executeQuery(String.format(JDBCTestSQL.SELECT_WITH_AUTO_INCREMENT_COLUMN_SQL, 22, 22))) {
                Assert.assertTrue(rs.next());
                Assert.assertThat(rs.getInt(1), CoreMatchers.is(4));
            }
        }
    }

    @Test
    public void assertAddBatchWithGenerateKeyColumn() throws SQLException {
        try (Connection connection = getShardingDataSource().getConnection();PreparedStatement preparedStatement = connection.prepareStatement(JDBCTestSQL.INSERT_ORDER_ITEM_WITH_ALL_PLACEHOLDERS_SQL, Statement.RETURN_GENERATED_KEYS);Statement queryStatement = connection.createStatement()) {
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, 11);
            preparedStatement.setInt(3, 11);
            preparedStatement.setString(4, "BATCH");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 2);
            preparedStatement.setInt(2, 12);
            preparedStatement.setInt(3, 12);
            preparedStatement.setString(4, "BATCH");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 3);
            preparedStatement.setInt(2, 21);
            preparedStatement.setInt(3, 21);
            preparedStatement.setString(4, "BATCH");
            preparedStatement.addBatch();
            preparedStatement.setInt(1, 4);
            preparedStatement.setInt(2, 22);
            preparedStatement.setInt(3, 22);
            preparedStatement.setString(4, "BATCH");
            preparedStatement.addBatch();
            int[] result = preparedStatement.executeBatch();
            for (int rs : result) {
                Assert.assertThat(rs, CoreMatchers.is(1));
            }
            ResultSet generateKeyResultSet = preparedStatement.getGeneratedKeys();
            Assert.assertTrue(generateKeyResultSet.next());
            Assert.assertThat(generateKeyResultSet.getLong(1), CoreMatchers.is(1L));
            Assert.assertTrue(generateKeyResultSet.next());
            Assert.assertThat(generateKeyResultSet.getLong(1), CoreMatchers.is(2L));
            Assert.assertTrue(generateKeyResultSet.next());
            Assert.assertThat(generateKeyResultSet.getLong(1), CoreMatchers.is(3L));
            Assert.assertTrue(generateKeyResultSet.next());
            Assert.assertThat(generateKeyResultSet.getLong(1), CoreMatchers.is(4L));
            Assert.assertFalse(generateKeyResultSet.next());
            try (ResultSet rs = queryStatement.executeQuery(String.format(JDBCTestSQL.SELECT_WITH_AUTO_INCREMENT_COLUMN_SQL, 11, 11))) {
                Assert.assertTrue(rs.next());
                Assert.assertThat(rs.getInt(1), CoreMatchers.is(1));
            }
            try (ResultSet rs = queryStatement.executeQuery(String.format(JDBCTestSQL.SELECT_WITH_AUTO_INCREMENT_COLUMN_SQL, 12, 12))) {
                Assert.assertTrue(rs.next());
                Assert.assertThat(rs.getInt(1), CoreMatchers.is(2));
            }
            try (ResultSet rs = queryStatement.executeQuery(String.format(JDBCTestSQL.SELECT_WITH_AUTO_INCREMENT_COLUMN_SQL, 21, 21))) {
                Assert.assertTrue(rs.next());
                Assert.assertThat(rs.getInt(1), CoreMatchers.is(3));
            }
            try (ResultSet rs = queryStatement.executeQuery(String.format(JDBCTestSQL.SELECT_WITH_AUTO_INCREMENT_COLUMN_SQL, 22, 22))) {
                Assert.assertTrue(rs.next());
                Assert.assertThat(rs.getInt(1), CoreMatchers.is(4));
            }
        }
    }

    @Test
    public void assertUpdateBatch() throws SQLException {
        String sql = "UPDATE t_order SET status=? WHERE status=?";
        try (Connection connection = getShardingDataSource().getConnection();PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, "batch");
            preparedStatement.setString(2, "init");
            preparedStatement.addBatch();
            preparedStatement.setString(1, "batch");
            preparedStatement.setString(2, "init");
            preparedStatement.addBatch();
            preparedStatement.setString(1, "init");
            preparedStatement.setString(2, "batch");
            preparedStatement.addBatch();
            int[] result = preparedStatement.executeBatch();
            Assert.assertThat(result.length, CoreMatchers.is(3));
            Assert.assertThat(result[0], CoreMatchers.is(4));
            Assert.assertThat(result[1], CoreMatchers.is(0));
            Assert.assertThat(result[2], CoreMatchers.is(4));
        }
    }

    @Test
    public void assertClearBatch() throws SQLException {
        try (Connection connection = getShardingDataSource().getConnection();PreparedStatement preparedStatement = connection.prepareStatement(JDBCTestSQL.INSERT_ORDER_ITEM_WITH_ALL_PLACEHOLDERS_SQL)) {
            preparedStatement.setInt(1, 3101);
            preparedStatement.setInt(2, 11);
            preparedStatement.setInt(3, 11);
            preparedStatement.setString(4, "BATCH");
            preparedStatement.addBatch();
            preparedStatement.clearBatch();
            int[] result = preparedStatement.executeBatch();
            Assert.assertThat(result.length, CoreMatchers.is(0));
        }
    }
}

