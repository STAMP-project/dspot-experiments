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
package org.apache.shardingsphere.core.rule;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.core.exception.ShardingConfigurationException;
import org.apache.shardingsphere.core.fixture.PreciseShardingAlgorithmFixture;
import org.apache.shardingsphere.core.keygen.fixture.IncrementShardingKeyGenerator;
import org.apache.shardingsphere.core.keygen.impl.SnowflakeShardingKeyGenerator;
import org.apache.shardingsphere.core.parsing.parser.context.condition.Column;
import org.apache.shardingsphere.core.routing.strategy.inline.InlineShardingStrategy;
import org.apache.shardingsphere.core.routing.strategy.none.NoneShardingStrategy;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class ShardingRuleTest {
    @Test(expected = IllegalArgumentException.class)
    public void assertNewShardingRuleWithEmptyDataSourceNames() {
        new ShardingRule(new ShardingRuleConfiguration(), Collections.<String>emptyList());
    }

    @Test
    public void assertNewShardingRuleWithMaximumConfiguration() {
        ShardingRule actual = createMaximumShardingRule();
        Assert.assertThat(actual.getTableRules().size(), CoreMatchers.is(2));
        Assert.assertThat(actual.getBindingTableRules().size(), CoreMatchers.is(1));
        Assert.assertThat(actual.getBindingTableRules().iterator().next().getTableRules().size(), CoreMatchers.is(2));
        Assert.assertThat(actual.getBroadcastTables(), CoreMatchers.<Collection<String>>is(Collections.singletonList("BROADCAST_TABLE")));
        Assert.assertThat(actual.getDefaultDatabaseShardingStrategy(), CoreMatchers.instanceOf(InlineShardingStrategy.class));
        Assert.assertThat(actual.getDefaultTableShardingStrategy(), CoreMatchers.instanceOf(InlineShardingStrategy.class));
        Assert.assertThat(actual.getDefaultShardingKeyGenerator(), CoreMatchers.instanceOf(IncrementShardingKeyGenerator.class));
    }

    @Test
    public void assertNewShardingRuleWithMinimumConfiguration() {
        ShardingRule actual = createMinimumShardingRule();
        Assert.assertThat(actual.getTableRules().size(), CoreMatchers.is(1));
        Assert.assertTrue(actual.getBindingTableRules().isEmpty());
        Assert.assertTrue(actual.getBroadcastTables().isEmpty());
        Assert.assertThat(actual.getDefaultDatabaseShardingStrategy(), CoreMatchers.instanceOf(NoneShardingStrategy.class));
        Assert.assertThat(actual.getDefaultTableShardingStrategy(), CoreMatchers.instanceOf(NoneShardingStrategy.class));
        Assert.assertThat(actual.getDefaultShardingKeyGenerator(), CoreMatchers.instanceOf(SnowflakeShardingKeyGenerator.class));
    }

    @Test
    public void assertNewShardingRuleWithMasterSlaveConfiguration() {
        ShardingRule actual = createMasterSlaveShardingRule();
        Assert.assertThat(actual.getMasterSlaveRules().size(), CoreMatchers.is(2));
    }

    @Test
    public void assertFindTableRule() {
        Assert.assertTrue(createMaximumShardingRule().findTableRule("logic_Table").isPresent());
    }

    @Test
    public void assertNotFindTableRule() {
        Assert.assertFalse(createMaximumShardingRule().findTableRule("other_Table").isPresent());
    }

    @Test
    public void assertFindTableRuleByActualTable() {
        Assert.assertTrue(createMaximumShardingRule().findTableRuleByActualTable("table_0").isPresent());
    }

    @Test
    public void assertNotFindTableRuleByActualTable() {
        Assert.assertFalse(createMaximumShardingRule().findTableRuleByActualTable("table_3").isPresent());
    }

    @Test
    public void assertGetTableRuleWithShardingTable() {
        TableRule actual = createMaximumShardingRule().getTableRule("Logic_Table");
        Assert.assertThat(actual.getLogicTable(), CoreMatchers.is("logic_table"));
    }

    @Test
    public void assertGetTableRuleWithBroadcastTable() {
        TableRule actual = createMaximumShardingRule().getTableRule("Broadcast_Table");
        Assert.assertThat(actual.getLogicTable(), CoreMatchers.is("broadcast_table"));
    }

    @Test
    public void assertGetTableRuleWithDefaultDataSource() {
        ShardingRule shardingRule = createMaximumShardingRule();
        shardingRule.getBroadcastTables().clear();
        Assert.assertThat(shardingRule.getTableRule("Default_Table").getLogicTable(), CoreMatchers.is("default_table"));
    }

    @Test(expected = ShardingConfigurationException.class)
    public void assertGetTableRuleFailure() {
        createMinimumShardingRule().getTableRule("New_Table");
    }

    @Test
    public void assertGetDatabaseShardingStrategyFromTableRule() {
        TableRule tableRule = Mockito.mock(TableRule.class);
        Mockito.when(tableRule.getDatabaseShardingStrategy()).thenReturn(new NoneShardingStrategy());
        Assert.assertThat(createMaximumShardingRule().getDatabaseShardingStrategy(tableRule), CoreMatchers.instanceOf(NoneShardingStrategy.class));
    }

    @Test
    public void assertGetDatabaseShardingStrategyFromDefault() {
        Assert.assertThat(createMaximumShardingRule().getDatabaseShardingStrategy(Mockito.mock(TableRule.class)), CoreMatchers.instanceOf(InlineShardingStrategy.class));
    }

    @Test
    public void assertGetTableShardingStrategyFromTableRule() {
        TableRule tableRule = Mockito.mock(TableRule.class);
        Mockito.when(tableRule.getTableShardingStrategy()).thenReturn(new NoneShardingStrategy());
        Assert.assertThat(createMaximumShardingRule().getTableShardingStrategy(tableRule), CoreMatchers.instanceOf(NoneShardingStrategy.class));
    }

    @Test
    public void assertGetTableShardingStrategyFromDefault() {
        Assert.assertThat(createMaximumShardingRule().getTableShardingStrategy(Mockito.mock(TableRule.class)), CoreMatchers.instanceOf(InlineShardingStrategy.class));
    }

    @Test
    public void assertIsAllBindingTableWhenLogicTablesIsEmpty() {
        Assert.assertFalse(createMaximumShardingRule().isAllBindingTables(Collections.<String>emptyList()));
    }

    @Test
    public void assertIsNotAllBindingTable() {
        Assert.assertFalse(createMaximumShardingRule().isAllBindingTables(Collections.singletonList("new_Table")));
        Assert.assertFalse(createMaximumShardingRule().isAllBindingTables(Arrays.asList("logic_Table", "new_Table")));
    }

    @Test
    public void assertIsAllBindingTable() {
        Assert.assertTrue(createMaximumShardingRule().isAllBindingTables(Collections.singletonList("logic_Table")));
        Assert.assertTrue(createMaximumShardingRule().isAllBindingTables(Collections.singletonList("logic_table")));
        Assert.assertTrue(createMaximumShardingRule().isAllBindingTables(Collections.singletonList("sub_Logic_Table")));
        Assert.assertTrue(createMaximumShardingRule().isAllBindingTables(Collections.singletonList("sub_logic_table")));
        Assert.assertTrue(createMaximumShardingRule().isAllBindingTables(Arrays.asList("logic_Table", "sub_Logic_Table")));
        Assert.assertTrue(createMaximumShardingRule().isAllBindingTables(Arrays.asList("logic_table", "sub_logic_Table")));
        Assert.assertFalse(createMaximumShardingRule().isAllBindingTables(Arrays.asList("logic_table", "sub_logic_Table", "new_table")));
        Assert.assertFalse(createMaximumShardingRule().isAllBindingTables(Collections.<String>emptyList()));
        Assert.assertFalse(createMaximumShardingRule().isAllBindingTables(Collections.singletonList("new_Table")));
    }

    @Test
    public void assertGetBindingTableRuleForNotConfig() {
        Assert.assertFalse(createMinimumShardingRule().findBindingTableRule("logic_Table").isPresent());
    }

    @Test
    public void assertGetBindingTableRuleForNotFound() {
        Assert.assertFalse(createMaximumShardingRule().findBindingTableRule("new_Table").isPresent());
    }

    @Test
    public void assertGetBindingTableRuleForFound() {
        ShardingRule actual = createMaximumShardingRule();
        Assert.assertTrue(actual.findBindingTableRule("logic_Table").isPresent());
        Assert.assertThat(actual.findBindingTableRule("logic_Table").get().getTableRules().size(), CoreMatchers.is(2));
    }

    @Test
    public void assertIsAllBroadcastTableWhenLogicTablesIsEmpty() {
        Assert.assertFalse(createMaximumShardingRule().isAllBroadcastTables(Collections.<String>emptyList()));
    }

    @Test
    public void assertIsAllBroadcastTable() {
        Assert.assertTrue(createMaximumShardingRule().isAllBroadcastTables(Collections.singletonList("Broadcast_Table")));
    }

    @Test
    public void assertIsNotAllBroadcastTable() {
        Assert.assertFalse(createMaximumShardingRule().isAllBroadcastTables(Arrays.asList("broadcast_table", "other_table")));
    }

    @Test
    public void assertIsBroadcastTable() {
        Assert.assertTrue(createMaximumShardingRule().isBroadcastTable("Broadcast_Table"));
    }

    @Test
    public void assertIsNotBroadcastTable() {
        Assert.assertFalse(createMaximumShardingRule().isBroadcastTable("other_table"));
    }

    @Test
    public void assertIsAllInDefaultDataSource() {
        Assert.assertTrue(createMaximumShardingRule().isAllInDefaultDataSource(Collections.singletonList("table_0")));
    }

    @Test
    public void assertIsNotAllInDefaultDataSourceWithShardingTable() {
        Assert.assertFalse(createMaximumShardingRule().isAllInDefaultDataSource(Arrays.asList("table_0", "logic_table")));
    }

    @Test
    public void assertIsNotAllInDefaultDataSourceWithBroadcastTable() {
        Assert.assertFalse(createMaximumShardingRule().isAllInDefaultDataSource(Arrays.asList("table_0", "broadcast_table")));
    }

    @Test
    public void assertIsShardingColumnForDefaultDatabaseShardingStrategy() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(createTableRuleConfigWithAllStrategies());
        shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration("column", new PreciseShardingAlgorithmFixture()));
        Assert.assertTrue(new ShardingRule(shardingRuleConfig, createDataSourceNames()).isShardingColumn(new Column("column", "LOGIC_TABLE")));
    }

    @Test
    public void assertIsShardingColumnForDefaultTableShardingStrategy() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(createTableRuleConfigWithAllStrategies());
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("column", new PreciseShardingAlgorithmFixture()));
        Assert.assertTrue(new ShardingRule(shardingRuleConfig, createDataSourceNames()).isShardingColumn(new Column("column", "LOGIC_TABLE")));
    }

    @Test
    public void assertIsShardingColumnForDatabaseShardingStrategy() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(createTableRuleConfigWithAllStrategies());
        Assert.assertTrue(new ShardingRule(shardingRuleConfig, createDataSourceNames()).isShardingColumn(new Column("column", "logic_Table")));
    }

    @Test
    public void assertIsShardingColumnForTableShardingStrategy() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(createTableRuleConfigWithTableStrategies());
        Assert.assertTrue(new ShardingRule(shardingRuleConfig, createDataSourceNames()).isShardingColumn(new Column("column", "logic_Table")));
    }

    @Test
    public void assertIsNotShardingColumn() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(createTableRuleConfigWithAllStrategies());
        Assert.assertFalse(new ShardingRule(shardingRuleConfig, createDataSourceNames()).isShardingColumn(new Column("column", "other_Table")));
    }

    @Test
    public void assertFindGenerateKeyColumn() {
        Assert.assertTrue(createMaximumShardingRule().findGenerateKeyColumn("logic_table").isPresent());
    }

    @Test
    public void assertNotFindGenerateKeyColumn() {
        Assert.assertFalse(createMinimumShardingRule().findGenerateKeyColumn("sub_logic_table").isPresent());
    }

    @Test(expected = ShardingConfigurationException.class)
    public void assertGenerateKeyFailure() {
        createMaximumShardingRule().generateKey("table_0");
    }

    @Test
    public void assertGenerateKeyWithDefaultKeyGenerator() {
        Assert.assertThat(createMinimumShardingRule().generateKey("logic_table"), CoreMatchers.instanceOf(Long.class));
    }

    @Test
    public void assertGenerateKeyWithKeyGenerator() {
        Assert.assertThat(createMaximumShardingRule().generateKey("logic_table"), CoreMatchers.instanceOf(Integer.class));
    }

    @Test
    public void assertDataSourceNameFromDefaultDataSourceName() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.setDefaultDataSourceName("ds_3");
        ShardingRule actual = new ShardingRule(shardingRuleConfig, createDataSourceNames());
        Assert.assertThat(actual.getShardingDataSourceNames().getDefaultDataSourceName(), CoreMatchers.is("ds_3"));
    }

    @Test
    public void assertDataSourceNameFromDataSourceNames() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.setDefaultDataSourceName("ds_3");
        Assert.assertThat(getShardingDataSourceNames().getDefaultDataSourceName(), CoreMatchers.is("ds_0"));
    }

    @Test
    public void assertGetLogicTableNameSuccess() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        TableRuleConfiguration tableRuleConfig = createTableRuleConfigWithLogicIndex();
        shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfig);
        Assert.assertThat(getLogicTableName("index_table"), CoreMatchers.is("logic_table"));
    }

    @Test(expected = ShardingConfigurationException.class)
    public void assertGetLogicTableNameFailure() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        TableRuleConfiguration tableRuleConfig = createTableRuleConfigWithLogicIndex();
        shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfig);
        getLogicTableName("");
    }

    @Test
    public void assertGetDataNodeByLogicTable() {
        Assert.assertThat(createMaximumShardingRule().getDataNode("logic_table"), CoreMatchers.is(new DataNode("ds_0.table_0")));
    }

    @Test
    public void assertGetDataNodeByDataSourceAndLogicTable() {
        Assert.assertThat(createMaximumShardingRule().getDataNode("ds_1", "logic_table"), CoreMatchers.is(new DataNode("ds_1.table_0")));
    }

    @Test(expected = ShardingConfigurationException.class)
    public void assertGetDataNodeByLogicTableFailureWithDataSourceName() {
        createMaximumShardingRule().getDataNode("ds_3", "logic_table");
    }

    @Test
    public void assertContainsWithTableRule() {
        Assert.assertTrue(createMaximumShardingRule().contains("LOGIC_TABLE"));
    }

    @Test
    public void assertContainsWithBindingTableRule() {
        Assert.assertTrue(createMaximumShardingRule().contains("SUB_LOGIC_TABLE"));
    }

    @Test
    public void assertContainsWithBroadcastTableRule() {
        Assert.assertTrue(createMaximumShardingRule().contains("BROADCAST_TABLE"));
    }

    @Test
    public void assertNotContains() {
        Assert.assertFalse(createMaximumShardingRule().contains("NEW_TABLE"));
    }

    @Test
    public void assertGetShardingLogicTableNames() {
        ShardingRule actual = createMaximumShardingRule();
        Assert.assertThat(actual.getShardingLogicTableNames(Arrays.asList("LOGIC_TABLE", "BROADCAST_TABLE")), CoreMatchers.<Collection<String>>is(Collections.singletonList("LOGIC_TABLE")));
    }
}

