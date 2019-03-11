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


import DatabaseType.H2;
import TransactionType.XA;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.shardingsphere.api.config.masterslave.LoadBalanceStrategyConfiguration;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.fixture.TestDataSource;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.MasterSlaveConnection;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class MasterSlaveDataSourceTest {
    private final DataSource masterDataSource;

    private final DataSource slaveDataSource;

    private final MasterSlaveDataSource masterSlaveDataSource;

    public MasterSlaveDataSourceTest() throws SQLException {
        masterDataSource = new TestDataSource("test_ds_master");
        slaveDataSource = new TestDataSource("test_ds_slave");
        Map<String, DataSource> dataSourceMap = new HashMap<>(2, 1);
        dataSourceMap.put("test_ds_master", masterDataSource);
        dataSourceMap.put("test_ds_slave", slaveDataSource);
        masterSlaveDataSource = new MasterSlaveDataSource(dataSourceMap, new MasterSlaveRuleConfiguration("test_ds", "test_ds_master", Collections.singletonList("test_ds_slave"), new LoadBalanceStrategyConfiguration("ROUND_ROBIN")), new Properties());
    }

    @Test(expected = IllegalStateException.class)
    public void assertGetDatabaseProductNameWhenDataBaseProductNameDifferent() throws SQLException {
        DataSource masterDataSource = Mockito.mock(DataSource.class);
        DataSource slaveDataSource = Mockito.mock(DataSource.class);
        Connection masterConnection = mockConnection("MySQL");
        final Connection slaveConnection = mockConnection("H2");
        Map<String, DataSource> dataSourceMap = new HashMap<>(2, 1);
        dataSourceMap.put("masterDataSource", masterDataSource);
        dataSourceMap.put("slaveDataSource", slaveDataSource);
        Mockito.when(masterDataSource.getConnection()).thenReturn(masterConnection);
        Mockito.when(slaveDataSource.getConnection()).thenReturn(slaveConnection);
        MasterSlaveRuleConfiguration masterSlaveRuleConfig = new MasterSlaveRuleConfiguration("ds", "masterDataSource", Collections.singletonList("slaveDataSource"), new LoadBalanceStrategyConfiguration("ROUND_ROBIN"));
        try {
            getDatabaseType();
        } finally {
            Mockito.verify(masterConnection).close();
            Mockito.verify(slaveConnection).close();
        }
    }

    @Test
    public void assertGetDatabaseProductName() throws SQLException {
        DataSource masterDataSource = Mockito.mock(DataSource.class);
        DataSource slaveDataSource1 = Mockito.mock(DataSource.class);
        DataSource slaveDataSource2 = Mockito.mock(DataSource.class);
        Connection masterConnection = mockConnection("H2");
        Connection slaveConnection1 = mockConnection("H2");
        Connection slaveConnection2 = mockConnection("H2");
        Mockito.when(masterDataSource.getConnection()).thenReturn(masterConnection);
        Mockito.when(slaveDataSource1.getConnection()).thenReturn(slaveConnection1);
        Mockito.when(slaveDataSource2.getConnection()).thenReturn(slaveConnection2);
        Map<String, DataSource> dataSourceMap = new HashMap<>(3, 1);
        dataSourceMap.put("masterDataSource", masterDataSource);
        dataSourceMap.put("slaveDataSource1", slaveDataSource1);
        dataSourceMap.put("slaveDataSource2", slaveDataSource2);
        Assert.assertThat(getDatabaseType(), CoreMatchers.is(H2));
        Mockito.verify(slaveConnection1).close();
        Mockito.verify(slaveConnection2).close();
    }

    @Test
    public void assertGetConnection() {
        Assert.assertThat(masterSlaveDataSource.getConnection(), CoreMatchers.instanceOf(MasterSlaveConnection.class));
    }

    @Test
    public void assertGetXAConnection() {
        TransactionTypeHolder.set(XA);
        MasterSlaveConnection connection = masterSlaveDataSource.getConnection();
        Assert.assertNotNull(connection.getDataSourceMap());
        Assert.assertThat(connection.getDataSourceMap().values().size(), CoreMatchers.is(2));
        Assert.assertThat(connection.getTransactionType(), CoreMatchers.is(XA));
    }
}

