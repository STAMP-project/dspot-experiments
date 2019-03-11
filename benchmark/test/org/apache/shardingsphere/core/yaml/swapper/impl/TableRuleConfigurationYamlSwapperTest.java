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


import org.apache.shardingsphere.api.config.encryptor.EncryptorConfiguration;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.core.yaml.config.encrypt.YamlEncryptorConfiguration;
import org.apache.shardingsphere.core.yaml.config.sharding.YamlKeyGeneratorConfiguration;
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
public final class TableRuleConfigurationYamlSwapperTest {
    @Mock
    private ShardingStrategyConfigurationYamlSwapper shardingStrategyConfigurationYamlSwapper;

    @Mock
    private KeyGeneratorConfigurationYamlSwapper keyGeneratorConfigurationYamlSwapper;

    @Mock
    private EncryptorConfigurationYamlSwapper encryptorConfigurationYamlSwapper;

    private TableRuleConfigurationYamlSwapper tableRuleConfigurationYamlSwapper = new TableRuleConfigurationYamlSwapper();

    @Test
    public void assertSwapToYamlWithMinProperties() {
        YamlTableRuleConfiguration actual = tableRuleConfigurationYamlSwapper.swap(new TableRuleConfiguration("tbl", "ds_$->{0..1}.tbl_$->{0..1}"));
        Assert.assertThat(actual.getLogicTable(), CoreMatchers.is("tbl"));
        Assert.assertThat(actual.getActualDataNodes(), CoreMatchers.is("ds_$->{0..1}.tbl_$->{0..1}"));
        Assert.assertNull(actual.getDatabaseStrategy());
        Assert.assertNull(actual.getTableStrategy());
        Assert.assertNull(actual.getKeyGenerator());
        Assert.assertNull(actual.getEncryptor());
        Assert.assertNull(actual.getLogicIndex());
    }

    @Test
    public void assertSwapToYamlWithMaxProperties() {
        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration("tbl", "ds_$->{0..1}.tbl_$->{0..1}");
        tableRuleConfiguration.setDatabaseShardingStrategyConfig(Mockito.mock(InlineShardingStrategyConfiguration.class));
        tableRuleConfiguration.setTableShardingStrategyConfig(Mockito.mock(InlineShardingStrategyConfiguration.class));
        tableRuleConfiguration.setKeyGeneratorConfig(Mockito.mock(KeyGeneratorConfiguration.class));
        tableRuleConfiguration.setEncryptorConfig(Mockito.mock(EncryptorConfiguration.class));
        tableRuleConfiguration.setLogicIndex("idx");
        YamlTableRuleConfiguration actual = tableRuleConfigurationYamlSwapper.swap(tableRuleConfiguration);
        Assert.assertThat(actual.getLogicTable(), CoreMatchers.is("tbl"));
        Assert.assertThat(actual.getActualDataNodes(), CoreMatchers.is("ds_$->{0..1}.tbl_$->{0..1}"));
        Assert.assertNotNull(actual.getDatabaseStrategy());
        Assert.assertNotNull(actual.getTableStrategy());
        Assert.assertNotNull(actual.getKeyGenerator());
        Assert.assertNotNull(actual.getEncryptor());
        Assert.assertThat(actual.getLogicIndex(), CoreMatchers.is("idx"));
    }

    @Test(expected = NullPointerException.class)
    public void assertSwapToObjectWithoutLogicTable() {
        new TableRuleConfigurationYamlSwapper().swap(new YamlTableRuleConfiguration());
    }

    @Test
    public void assertSwapToObjectWithMinProperties() {
        YamlTableRuleConfiguration yamlConfiguration = new YamlTableRuleConfiguration();
        yamlConfiguration.setLogicTable("tbl");
        yamlConfiguration.setActualDataNodes("ds_$->{0..1}.tbl_$->{0..1}");
        TableRuleConfiguration actual = tableRuleConfigurationYamlSwapper.swap(yamlConfiguration);
        Assert.assertThat(actual.getLogicTable(), CoreMatchers.is("tbl"));
        Assert.assertThat(actual.getActualDataNodes(), CoreMatchers.is("ds_$->{0..1}.tbl_$->{0..1}"));
        Assert.assertNull(actual.getDatabaseShardingStrategyConfig());
        Assert.assertNull(actual.getTableShardingStrategyConfig());
        Assert.assertNull(actual.getKeyGeneratorConfig());
        Assert.assertNull(actual.getEncryptorConfig());
        Assert.assertNull(actual.getLogicIndex());
    }

    @Test
    public void assertSwapToObjectWithMaxProperties() {
        YamlTableRuleConfiguration yamlConfiguration = new YamlTableRuleConfiguration();
        yamlConfiguration.setLogicTable("tbl");
        yamlConfiguration.setActualDataNodes("ds_$->{0..1}.tbl_$->{0..1}");
        yamlConfiguration.setDatabaseStrategy(Mockito.mock(YamlShardingStrategyConfiguration.class));
        yamlConfiguration.setTableStrategy(Mockito.mock(YamlShardingStrategyConfiguration.class));
        yamlConfiguration.setKeyGenerator(Mockito.mock(YamlKeyGeneratorConfiguration.class));
        yamlConfiguration.setEncryptor(Mockito.mock(YamlEncryptorConfiguration.class));
        yamlConfiguration.setLogicIndex("idx");
        TableRuleConfiguration actual = tableRuleConfigurationYamlSwapper.swap(yamlConfiguration);
        Assert.assertThat(actual.getLogicTable(), CoreMatchers.is("tbl"));
        Assert.assertThat(actual.getActualDataNodes(), CoreMatchers.is("ds_$->{0..1}.tbl_$->{0..1}"));
        Assert.assertNotNull(actual.getDatabaseShardingStrategyConfig());
        Assert.assertNotNull(actual.getTableShardingStrategyConfig());
        Assert.assertNotNull(actual.getKeyGeneratorConfig());
        Assert.assertNotNull(actual.getEncryptorConfig());
        Assert.assertThat(actual.getLogicIndex(), CoreMatchers.is("idx"));
    }
}

