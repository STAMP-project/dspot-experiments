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
import lombok.SneakyThrows;
import org.apache.shardingsphere.core.constant.ShardingConstant;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class OrchestrationShardingDataSourceTest {
    private static OrchestrationShardingDataSource shardingDataSource;

    @Test
    @SneakyThrows
    public void assertInitializeOrchestrationShardingDataSource() {
        OrchestrationShardingDataSource shardingDataSource = new OrchestrationShardingDataSource(OrchestrationShardingDataSourceTest.getOrchestrationConfiguration());
        Assert.assertThat(shardingDataSource.getConnection(), CoreMatchers.instanceOf(Connection.class));
    }

    @Test
    public void assertRenewRule() {
        OrchestrationShardingDataSourceTest.shardingDataSource.renew(new org.apache.shardingsphere.orchestration.internal.registry.config.event.ShardingRuleChangedEvent(ShardingConstant.LOGIC_SCHEMA_NAME, getShardingRuleConfig()));
        Assert.assertThat(OrchestrationShardingDataSourceTest.shardingDataSource.getDataSource().getShardingContext().getShardingRule().getTableRules().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertRenewDataSource() {
        OrchestrationShardingDataSourceTest.shardingDataSource.renew(new org.apache.shardingsphere.orchestration.internal.registry.config.event.DataSourceChangedEvent(ShardingConstant.LOGIC_SCHEMA_NAME, getDataSourceConfigurations()));
        Assert.assertThat(OrchestrationShardingDataSourceTest.shardingDataSource.getDataSource().getDataSourceMap().size(), CoreMatchers.is(3));
    }

    @Test
    public void assertRenewProperties() {
        OrchestrationShardingDataSourceTest.shardingDataSource.renew(getPropertiesChangedEvent());
        Assert.assertThat(OrchestrationShardingDataSourceTest.shardingDataSource.getDataSource().getShardingContext().getShardingProperties().getProps().getProperty("sql.show"), CoreMatchers.is("true"));
    }

    @Test
    public void assertRenewDisabledState() {
        OrchestrationShardingDataSourceTest.shardingDataSource.renew(getDisabledStateChangedEvent());
        Assert.assertThat(OrchestrationShardingDataSourceTest.shardingDataSource.getDataSource().getShardingContext().getShardingRule().getMasterSlaveRules().iterator().next().getSlaveDataSourceNames().size(), CoreMatchers.is(0));
    }
}

