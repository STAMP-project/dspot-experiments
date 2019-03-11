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
package org.apache.shardingsphere.shardingjdbc.orchestration.internal.datasource;


import java.sql.Connection;
import java.sql.SQLException;
import org.apache.shardingsphere.core.constant.ShardingConstant;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.apache.shardingsphere.orchestration.internal.registry.state.event.CircuitStateChangedEvent;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;
import org.apache.shardingsphere.shardingjdbc.orchestration.internal.circuit.connection.CircuitBreakerConnection;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class OrchestrationMasterSlaveDataSourceTest {
    private static OrchestrationMasterSlaveDataSource masterSlaveDataSource;

    @Test
    public void assertInitializeOrchestrationMasterSlaveDataSource() throws SQLException {
        OrchestrationMasterSlaveDataSource masterSlaveDataSource = new OrchestrationMasterSlaveDataSource(OrchestrationMasterSlaveDataSourceTest.getOrchestrationConfiguration());
        Assert.assertThat(masterSlaveDataSource.getConnection(), CoreMatchers.instanceOf(Connection.class));
    }

    @Test
    public void assertRenewRule() {
        OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.renew(getMasterSlaveRuleChangedEvent());
        Assert.assertThat(OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.getDataSource().getMasterSlaveRule().getName(), CoreMatchers.is("new_ms"));
    }

    @Test
    public void assertRenewDataSource() {
        OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.renew(new org.apache.shardingsphere.orchestration.internal.registry.config.event.DataSourceChangedEvent(ShardingConstant.LOGIC_SCHEMA_NAME, getDataSourceConfigurations()));
        Assert.assertThat(OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.getDataSource().getDataSourceMap().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertRenewProperties() {
        OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.renew(getPropertiesChangedEvent());
        Assert.assertThat(OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.getDataSource().getShardingProperties().getProps().getProperty("sql.show"), CoreMatchers.is("true"));
    }

    @Test
    public void assertRenewDisabledState() {
        OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.renew(getDisabledStateChangedEvent());
        Assert.assertThat(OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.getDataSource().getMasterSlaveRule().getSlaveDataSourceNames().size(), CoreMatchers.is(0));
    }

    @Test
    public void assertRenewCircuitState() throws SQLException {
        OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.renew(new CircuitStateChangedEvent(true));
        Assert.assertThat(OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.getConnection(), CoreMatchers.instanceOf(CircuitBreakerConnection.class));
    }

    @Test
    public void assertGetDataSource() {
        Assert.assertThat(OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.getDataSource(), CoreMatchers.instanceOf(MasterSlaveDataSource.class));
    }

    @Test
    public void assertGetConnection() {
        Assert.assertThat(OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.getConnection("root", "root"), CoreMatchers.instanceOf(Connection.class));
    }

    @Test(expected = ShardingException.class)
    public void assertClose() throws Exception {
        OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.close();
        try {
            OrchestrationMasterSlaveDataSourceTest.masterSlaveDataSource.getDataSource().getDataSourceMap().values().iterator().next().getConnection();
        } catch (final SQLException ex) {
            throw new ShardingException(ex.getMessage());
        }
    }
}

