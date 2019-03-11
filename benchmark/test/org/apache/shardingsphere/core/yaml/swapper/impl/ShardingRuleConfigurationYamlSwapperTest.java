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
package org.apache.shardingsphere.core.yaml.swapper.impl;


import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ShardingStrategyConfiguration;
import org.apache.shardingsphere.core.yaml.config.masterslave.YamlMasterSlaveRuleConfiguration;
import org.apache.shardingsphere.core.yaml.config.sharding.YamlKeyGeneratorConfiguration;
import org.apache.shardingsphere.core.yaml.config.sharding.YamlShardingRuleConfiguration;
import org.apache.shardingsphere.core.yaml.config.sharding.YamlShardingStrategyConfiguration;
import org.apache.shardingsphere.core.yaml.config.sharding.YamlTableRuleConfiguration;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class ShardingRuleConfigurationYamlSwapperTest {
    @Mock
    private TableRuleConfigurationYamlSwapper tableRuleConfigurationYamlSwapper;

    @Mock
    private ShardingStrategyConfigurationYamlSwapper shardingStrategyConfigurationYamlSwapper;

    @Mock
    private KeyGeneratorConfigurationYamlSwapper keyGeneratorConfigurationYamlSwapper;

    @Mock
    private MasterSlaveRuleConfigurationYamlSwapper masterSlaveRuleConfigurationYamlSwapper;

    private ShardingRuleConfigurationYamlSwapper shardingRuleConfigurationYamlSwapper = new ShardingRuleConfigurationYamlSwapper();

    @Test
    public void assertSwapToYamlWithMinProperties() {
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        shardingRuleConfiguration.getTableRuleConfigs().add(Mockito.mock(TableRuleConfiguration.class));
        YamlShardingRuleConfiguration actual = shardingRuleConfigurationYamlSwapper.swap(shardingRuleConfiguration);
        Assert.assertThat(actual.getTables().size(), CoreMatchers.is(1));
        Assert.assertTrue(actual.getBindingTables().isEmpty());
        Assert.assertTrue(actual.getBroadcastTables().isEmpty());
        Assert.assertNull(actual.getDefaultDataSourceName());
        Assert.assertNull(actual.getDefaultDatabaseStrategy());
        Assert.assertNull(actual.getDefaultTableStrategy());
        Assert.assertNull(actual.getDefaultKeyGenerator());
        Assert.assertTrue(actual.getMasterSlaveRules().isEmpty());
    }

    @Test
    public void assertSwapToYamlWithMaxProperties() {
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        shardingRuleConfiguration.getTableRuleConfigs().add(Mockito.mock(TableRuleConfiguration.class));
        shardingRuleConfiguration.getBindingTableGroups().add("tbl, sub_tbl");
        shardingRuleConfiguration.getBroadcastTables().add("dict");
        shardingRuleConfiguration.setDefaultDataSourceName("ds");
        shardingRuleConfiguration.setDefaultDatabaseShardingStrategyConfig(Mockito.mock(ShardingStrategyConfiguration.class));
        shardingRuleConfiguration.setDefaultTableShardingStrategyConfig(Mockito.mock(ShardingStrategyConfiguration.class));
        shardingRuleConfiguration.setDefaultKeyGeneratorConfig(Mockito.mock(KeyGeneratorConfiguration.class));
        shardingRuleConfiguration.getMasterSlaveRuleConfigs().add(Mockito.mock(MasterSlaveRuleConfiguration.class));
        YamlShardingRuleConfiguration actual = shardingRuleConfigurationYamlSwapper.swap(shardingRuleConfiguration);
        Assert.assertThat(actual.getTables().size(), CoreMatchers.is(1));
        Assert.assertThat(actual.getBindingTables().size(), CoreMatchers.is(1));
        Assert.assertThat(actual.getBindingTables().iterator().next(), CoreMatchers.is("tbl, sub_tbl"));
        Assert.assertThat(actual.getBroadcastTables().size(), CoreMatchers.is(1));
        Assert.assertThat(actual.getBroadcastTables().iterator().next(), CoreMatchers.is("dict"));
        Assert.assertThat(actual.getDefaultDataSourceName(), CoreMatchers.is("ds"));
        Assert.assertNotNull(actual.getDefaultDatabaseStrategy());
        Assert.assertNotNull(actual.getDefaultTableStrategy());
        Assert.assertNotNull(actual.getDefaultKeyGenerator());
        Assert.assertThat(actual.getMasterSlaveRules().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertSwapToObjectWithMinProperties() {
        YamlShardingRuleConfiguration yamlConfiguration = new YamlShardingRuleConfiguration();
        yamlConfiguration.getTables().put("tbl", Mockito.mock(YamlTableRuleConfiguration.class));
        ShardingRuleConfiguration actual = shardingRuleConfigurationYamlSwapper.swap(yamlConfiguration);
        Assert.assertThat(actual.getTableRuleConfigs().size(), CoreMatchers.is(1));
        Assert.assertTrue(actual.getBindingTableGroups().isEmpty());
        Assert.assertTrue(actual.getBroadcastTables().isEmpty());
        Assert.assertNull(actual.getDefaultDataSourceName());
        Assert.assertNull(actual.getDefaultDatabaseShardingStrategyConfig());
        Assert.assertNull(actual.getDefaultTableShardingStrategyConfig());
        Assert.assertNull(actual.getDefaultKeyGeneratorConfig());
        Assert.assertTrue(actual.getMasterSlaveRuleConfigs().isEmpty());
    }

    @Test
    public void assertSwapToObjectWithMaxProperties() {
        YamlShardingRuleConfiguration yamlConfiguration = new YamlShardingRuleConfiguration();
        yamlConfiguration.getTables().put("tbl", Mockito.mock(YamlTableRuleConfiguration.class));
        yamlConfiguration.getBindingTables().add("tbl, sub_tbl");
        yamlConfiguration.getBroadcastTables().add("dict");
        yamlConfiguration.setDefaultDataSourceName("ds");
        yamlConfiguration.setDefaultDatabaseStrategy(Mockito.mock(YamlShardingStrategyConfiguration.class));
        yamlConfiguration.setDefaultTableStrategy(Mockito.mock(YamlShardingStrategyConfiguration.class));
        yamlConfiguration.setDefaultKeyGenerator(Mockito.mock(YamlKeyGeneratorConfiguration.class));
        yamlConfiguration.getMasterSlaveRules().put("ds", Mockito.mock(YamlMasterSlaveRuleConfiguration.class));
        ShardingRuleConfiguration actual = shardingRuleConfigurationYamlSwapper.swap(yamlConfiguration);
        Assert.assertThat(actual.getTableRuleConfigs().size(), CoreMatchers.is(1));
        Assert.assertThat(actual.getBindingTableGroups().size(), CoreMatchers.is(1));
        Assert.assertThat(actual.getBindingTableGroups().iterator().next(), CoreMatchers.is("tbl, sub_tbl"));
        Assert.assertThat(actual.getBroadcastTables().size(), CoreMatchers.is(1));
        Assert.assertThat(actual.getBroadcastTables().iterator().next(), CoreMatchers.is("dict"));
        Assert.assertThat(actual.getDefaultDataSourceName(), CoreMatchers.is("ds"));
        Assert.assertNotNull(actual.getDefaultDatabaseShardingStrategyConfig());
        Assert.assertNotNull(actual.getDefaultTableShardingStrategyConfig());
        Assert.assertNotNull(actual.getDefaultKeyGeneratorConfig());
        Assert.assertThat(actual.getMasterSlaveRuleConfigs().size(), CoreMatchers.is(1));
    }
}

