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


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.shardingsphere.core.constant.DatabaseType;
import org.apache.shardingsphere.shardingjdbc.common.base.AbstractShardingJDBCDatabaseAndTableTest;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ResultSetAdapterTest extends AbstractShardingJDBCDatabaseAndTableTest {
    private final List<ShardingConnection> shardingConnections = new ArrayList<>();

    private final List<Statement> statements = new ArrayList<>();

    private final Map<DatabaseType, ResultSet> resultSets = new HashMap<>();

    @Test
    public void assertClose() throws SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            each.getValue().close();
            assertClose(((AbstractResultSetAdapter) (each.getValue())), each.getKey());
        }
    }

    @Test
    public void assertWasNull() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertFalse(each.wasNull());
        }
    }

    @Test
    public void assertSetFetchDirection() throws SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            Assert.assertThat(each.getValue().getFetchDirection(), CoreMatchers.is(ResultSet.FETCH_FORWARD));
            try {
                each.getValue().setFetchDirection(ResultSet.FETCH_REVERSE);
            } catch (final SQLException ignored) {
            }
            if (((each.getKey()) == (DatabaseType.MySQL)) || ((each.getKey()) == (DatabaseType.PostgreSQL))) {
                assertFetchDirection(((AbstractResultSetAdapter) (each.getValue())), ResultSet.FETCH_REVERSE, each.getKey());
            }
        }
    }

    @Test
    public void assertSetFetchSize() throws SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            if (((DatabaseType.MySQL) == (each.getKey())) || ((DatabaseType.PostgreSQL) == (each.getKey()))) {
                Assert.assertThat(each.getValue().getFetchSize(), CoreMatchers.is(0));
            }
            each.getValue().setFetchSize(100);
            assertFetchSize(((AbstractResultSetAdapter) (each.getValue())), each.getKey());
        }
    }

    @Test
    public void assertGetType() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getType(), CoreMatchers.is(ResultSet.TYPE_FORWARD_ONLY));
        }
    }

    @Test
    public void assertGetConcurrency() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertThat(each.getConcurrency(), CoreMatchers.is(ResultSet.CONCUR_READ_ONLY));
        }
    }

    @Test
    public void assertGetStatement() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertNotNull(each.getStatement());
        }
    }

    @Test
    public void assertClearWarnings() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertNull(each.getWarnings());
            each.clearWarnings();
            Assert.assertNull(each.getWarnings());
        }
    }

    @Test
    public void assertGetMetaData() throws SQLException {
        for (ResultSet each : resultSets.values()) {
            Assert.assertNotNull(each.getMetaData());
        }
    }

    @Test
    public void assertFindColumn() throws SQLException {
        for (Map.Entry<DatabaseType, ResultSet> each : resultSets.entrySet()) {
            Assert.assertThat(each.getValue().findColumn("user_id"), CoreMatchers.is(1));
        }
    }
}

