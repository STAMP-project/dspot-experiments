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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.shardingsphere.shardingjdbc.common.base.AbstractShardingJDBCDatabaseAndTableTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingStatementTest extends AbstractShardingJDBCDatabaseAndTableTest {
    private String sql = "INSERT INTO t_order_item(order_id, user_id, status) VALUES (%d, %d, '%s')";

    @Test
    public void assertGetGeneratedKeys() throws SQLException {
        try (Connection connection = getShardingDataSource().getConnection();Statement stmt = connection.createStatement()) {
            Assert.assertFalse(stmt.execute(String.format(sql, 1, 1, "init")));
            Assert.assertFalse(stmt.execute(String.format(sql, 1, 1, "init"), Statement.NO_GENERATED_KEYS));
            Assert.assertFalse(stmt.execute(String.format(sql, 1, 1, "init"), Statement.RETURN_GENERATED_KEYS));
            ResultSet generatedKeysResultSet = stmt.getGeneratedKeys();
            Assert.assertTrue(generatedKeysResultSet.next());
            Assert.assertThat(generatedKeysResultSet.getLong(1), CoreMatchers.is(3L));
            Assert.assertFalse(stmt.execute(String.format(sql, 1, 1, "init"), new int[]{ 1 }));
            generatedKeysResultSet = stmt.getGeneratedKeys();
            Assert.assertTrue(generatedKeysResultSet.next());
            Assert.assertThat(generatedKeysResultSet.getLong(1), CoreMatchers.is(4L));
            Assert.assertFalse(stmt.execute(String.format(sql, 1, 1, "init"), new String[]{ "user_id" }));
            generatedKeysResultSet = stmt.getGeneratedKeys();
            Assert.assertTrue(generatedKeysResultSet.next());
            Assert.assertThat(generatedKeysResultSet.getLong(1), CoreMatchers.is(5L));
            Assert.assertFalse(stmt.execute(String.format(sql, 1, 1, "init"), new int[]{ 2 }));
            generatedKeysResultSet = stmt.getGeneratedKeys();
            Assert.assertTrue(generatedKeysResultSet.next());
            Assert.assertThat(generatedKeysResultSet.getLong(1), CoreMatchers.is(6L));
            Assert.assertFalse(stmt.execute(String.format(sql, 1, 1, "init"), new String[]{ "no" }));
            generatedKeysResultSet = stmt.getGeneratedKeys();
            Assert.assertTrue(generatedKeysResultSet.next());
            Assert.assertThat(generatedKeysResultSet.getLong(1), CoreMatchers.is(7L));
        }
    }
}

