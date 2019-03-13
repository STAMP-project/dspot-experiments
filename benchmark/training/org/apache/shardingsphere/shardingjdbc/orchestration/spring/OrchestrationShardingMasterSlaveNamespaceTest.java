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
package org.apache.shardingsphere.shardingjdbc.orchestration.spring;


import java.util.Map;
import javax.sql.DataSource;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.strategy.masterslave.RoundRobinMasterSlaveLoadBalanceAlgorithm;
import org.apache.shardingsphere.shardingjdbc.orchestration.spring.fixture.IncrementKeyGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration(locations = "classpath:META-INF/rdb/shardingMasterSlaveOrchestration.xml")
public class OrchestrationShardingMasterSlaveNamespaceTest extends AbstractJUnit4SpringContextTests {
    @Test
    public void assertMasterSlaveShardingDataSourceByUserStrategy() {
        Map<String, DataSource> dataSourceMap = getDataSourceMap("masterSlaveShardingDataSourceByUserStrategyOrchestration");
        Assert.assertNotNull(dataSourceMap.get("dbtbl_0_master"));
        Assert.assertNotNull(dataSourceMap.get("dbtbl_0_slave_0"));
        Assert.assertNotNull(dataSourceMap.get("dbtbl_0_slave_1"));
        Assert.assertNotNull(dataSourceMap.get("dbtbl_1_master"));
        Assert.assertNotNull(dataSourceMap.get("dbtbl_1_slave_0"));
        Assert.assertNotNull(dataSourceMap.get("dbtbl_1_slave_1"));
        ShardingRule shardingRule = getShardingRule("masterSlaveShardingDataSourceByUserStrategyOrchestration");
        Assert.assertThat(shardingRule.getTableRules().size(), CoreMatchers.is(1));
        Assert.assertThat(shardingRule.getTableRules().iterator().next().getLogicTable(), CoreMatchers.is("t_order"));
    }

    @Test
    public void assertMasterSlaveShardingDataSourceByDefaultStrategy() {
        Map<String, DataSource> dataSourceMap = getDataSourceMap("masterSlaveShardingDataSourceByDefaultStrategyOrchestration");
        Assert.assertNotNull(dataSourceMap.get("dbtbl_0_master"));
        Assert.assertNotNull(dataSourceMap.get("dbtbl_0_slave_0"));
        Assert.assertNotNull(dataSourceMap.get("dbtbl_1_master"));
        Assert.assertNotNull(dataSourceMap.get("dbtbl_1_slave_1"));
        ShardingRule shardingRule = getShardingRule("masterSlaveShardingDataSourceByDefaultStrategyOrchestration");
        Assert.assertThat(shardingRule.getMasterSlaveRules().iterator().next().getLoadBalanceAlgorithm(), CoreMatchers.instanceOf(RoundRobinMasterSlaveLoadBalanceAlgorithm.class));
        Assert.assertThat(shardingRule.getTableRules().size(), CoreMatchers.is(1));
        Assert.assertThat(shardingRule.getTableRules().iterator().next().getLogicTable(), CoreMatchers.is("t_order"));
        Assert.assertThat(shardingRule.getDefaultShardingKeyGenerator(), CoreMatchers.instanceOf(IncrementKeyGenerator.class));
    }
}

