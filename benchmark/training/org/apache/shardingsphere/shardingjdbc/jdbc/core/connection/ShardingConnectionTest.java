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
package org.apache.shardingsphere.shardingjdbc.jdbc.core.connection;


import TransactionOperationType.BEGIN;
import TransactionOperationType.COMMIT;
import TransactionOperationType.ROLLBACK;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.ShardingContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;
import org.apache.shardingsphere.transaction.ShardingTransactionManagerEngine;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingConnectionTest {
    private static MasterSlaveDataSource masterSlaveDataSource;

    private static final String DS_NAME = "default";

    private ShardingConnection connection;

    private ShardingContext shardingContext;

    private Map<String, DataSource> dataSourceMap;

    private ShardingTransactionManagerEngine shardingTransactionManagerEngine = new ShardingTransactionManagerEngine();

    @Test
    public void assertGetConnectionFromCache() throws SQLException {
        Assert.assertThat(connection.getConnection(ShardingConnectionTest.DS_NAME), CoreMatchers.is(connection.getConnection(ShardingConnectionTest.DS_NAME)));
    }

    @Test(expected = IllegalStateException.class)
    public void assertGetConnectionFailure() throws SQLException {
        connection.getConnection("not_exist");
    }

    @Test
    public void assertXATransactionOperation() throws SQLException {
        connection = new ShardingConnection(dataSourceMap, shardingContext, shardingTransactionManagerEngine, TransactionType.XA);
        connection.setAutoCommit(false);
        Assert.assertTrue(getInvocations().contains(BEGIN));
        connection.commit();
        Assert.assertTrue(getInvocations().contains(COMMIT));
        connection.rollback();
        Assert.assertTrue(getInvocations().contains(ROLLBACK));
    }

    @Test
    public void assertBASETransactionOperation() throws SQLException {
        connection = new ShardingConnection(dataSourceMap, shardingContext, shardingTransactionManagerEngine, TransactionType.BASE);
        connection.setAutoCommit(false);
        Assert.assertTrue(getInvocations().contains(BEGIN));
        connection.commit();
        Assert.assertTrue(getInvocations().contains(COMMIT));
        connection.rollback();
        Assert.assertTrue(getInvocations().contains(ROLLBACK));
    }
}

