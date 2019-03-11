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
package org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource;


import TransactionType.LOCAL;
import TransactionType.XA;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.shardingsphere.api.config.masterslave.LoadBalanceStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.fixture.XAShardingTransactionManagerFixture;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingDataSourceTest {
    @Test(expected = IllegalStateException.class)
    public void assertGetDatabaseProductNameWhenDataBaseProductNameDifferent() throws SQLException {
        DataSource dataSource1 = mockDataSource("MySQL");
        DataSource dataSource2 = mockDataSource("H2");
        Map<String, DataSource> dataSourceMap = new HashMap<>(2, 1);
        dataSourceMap.put("ds1", dataSource1);
        dataSourceMap.put("ds2", dataSource2);
        assertDatabaseProductName(dataSourceMap, dataSource1.getConnection(), dataSource2.getConnection());
    }

    @Test(expected = IllegalStateException.class)
    public void assertGetDatabaseProductNameWhenDataBaseProductNameDifferentForMasterSlave() throws SQLException {
        DataSource dataSource1 = mockDataSource("MySQL");
        DataSource masterDataSource = mockDataSource("H2");
        DataSource slaveDataSource = mockDataSource("H2");
        Map<String, DataSource> masterSlaveDataSourceMap = new HashMap<>(2, 1);
        masterSlaveDataSourceMap.put("masterDataSource", masterDataSource);
        masterSlaveDataSourceMap.put("slaveDataSource", slaveDataSource);
        MasterSlaveDataSource dataSource2 = ((MasterSlaveDataSource) (MasterSlaveDataSourceFactory.createDataSource(masterSlaveDataSourceMap, new org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration("ds", "masterDataSource", Collections.singletonList("slaveDataSource"), new LoadBalanceStrategyConfiguration("ROUND_ROBIN")), new Properties())));
        Map<String, DataSource> dataSourceMap = new HashMap<>(2, 1);
        dataSourceMap.put("ds1", dataSource1);
        dataSourceMap.put("ds2", dataSource2);
        assertDatabaseProductName(dataSourceMap, dataSource1.getConnection(), masterDataSource.getConnection(), slaveDataSource.getConnection());
    }

    @Test
    public void assertGetDatabaseProductName() throws SQLException {
        DataSource dataSource1 = mockDataSource("H2");
        DataSource dataSource2 = mockDataSource("H2");
        DataSource dataSource3 = mockDataSource("H2");
        Map<String, DataSource> dataSourceMap = new HashMap<>(3, 1);
        dataSourceMap.put("ds1", dataSource1);
        dataSourceMap.put("ds2", dataSource2);
        dataSourceMap.put("ds3", dataSource3);
        assertDatabaseProductName(dataSourceMap, dataSource1.getConnection(), dataSource2.getConnection(), dataSource3.getConnection());
    }

    @Test
    public void assertGetDatabaseProductNameForMasterSlave() throws SQLException {
        DataSource dataSource1 = mockDataSource("H2");
        DataSource masterDataSource = mockDataSource("H2");
        DataSource slaveDataSource = mockDataSource("H2");
        DataSource dataSource3 = mockDataSource("H2");
        Map<String, DataSource> dataSourceMap = new HashMap<>(4, 1);
        dataSourceMap.put("ds1", dataSource1);
        dataSourceMap.put("masterDataSource", masterDataSource);
        dataSourceMap.put("slaveDataSource", slaveDataSource);
        dataSourceMap.put("ds3", dataSource3);
        assertDatabaseProductName(dataSourceMap, dataSource1.getConnection(), masterDataSource.getConnection(), slaveDataSource.getConnection());
    }

    @Test
    public void assertGetConnection() throws SQLException {
        DataSource dataSource = mockDataSource("H2");
        Map<String, DataSource> dataSourceMap = new HashMap<>(1, 1);
        dataSourceMap.put("ds", dataSource);
        Assert.assertThat(createShardingDataSource(dataSourceMap).getConnection().getConnection("ds"), CoreMatchers.is(dataSource.getConnection()));
    }

    @Test
    public void assertGetXaConnection() throws SQLException {
        DataSource dataSource = mockDataSource("MySQL");
        Map<String, DataSource> dataSourceMap = new HashMap<>(1, 1);
        dataSourceMap.put("ds", dataSource);
        TransactionTypeHolder.set(XA);
        ShardingDataSource shardingDataSource = createShardingDataSource(dataSourceMap);
        Assert.assertThat(shardingDataSource.getDataSourceMap().size(), CoreMatchers.is(1));
        ShardingConnection shardingConnection = shardingDataSource.getConnection();
        Assert.assertThat(shardingConnection.getDataSourceMap().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertGetXaConnectionThenGetLocalConnection() throws SQLException {
        DataSource dataSource = mockDataSource("MySQL");
        Map<String, DataSource> dataSourceMap = new HashMap<>(1, 1);
        dataSourceMap.put("ds", dataSource);
        TransactionTypeHolder.set(XA);
        ShardingDataSource shardingDataSource = createShardingDataSource(dataSourceMap);
        ShardingConnection shardingConnection = shardingDataSource.getConnection();
        Assert.assertThat(shardingConnection.getDataSourceMap().size(), CoreMatchers.is(1));
        Assert.assertThat(shardingConnection.getTransactionType(), CoreMatchers.is(XA));
        Assert.assertThat(shardingConnection.getShardingTransactionManager(), CoreMatchers.instanceOf(XAShardingTransactionManagerFixture.class));
        TransactionTypeHolder.set(LOCAL);
        shardingConnection = shardingDataSource.getConnection();
        Assert.assertThat(shardingConnection.getConnection("ds"), CoreMatchers.is(dataSource.getConnection()));
        Assert.assertThat(shardingConnection.getDataSourceMap(), CoreMatchers.is(dataSourceMap));
        Assert.assertThat(shardingConnection.getTransactionType(), CoreMatchers.is(LOCAL));
        Assert.assertThat(((shardingConnection.getShardingTransactionManager()) == null), CoreMatchers.is(true));
    }
}

