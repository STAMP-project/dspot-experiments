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


import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.NoneShardingStrategyConfiguration;
import org.apache.shardingsphere.core.strategy.keygen.fixture.IncrementShardingKeyGenerator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class TableRuleTest {
    @Test
    public void assertCreateMinTableRule() {
        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration("LOGIC_TABLE");
        TableRule actual = new TableRule(tableRuleConfig, createShardingDataSourceNames(), null);
        Assert.assertThat(actual.getLogicTable(), CoreMatchers.is("logic_table"));
        Assert.assertThat(actual.getActualDataNodes().size(), CoreMatchers.is(2));
        Assert.assertTrue(actual.getActualDataNodes().contains(new DataNode("ds0", "LOGIC_TABLE")));
        Assert.assertTrue(actual.getActualDataNodes().contains(new DataNode("ds1", "LOGIC_TABLE")));
        Assert.assertNull(actual.getDatabaseShardingStrategy());
        Assert.assertNull(actual.getTableShardingStrategy());
        Assert.assertNull(actual.getLogicIndex());
    }

    @Test
    public void assertCreateFullTableRule() {
        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration("LOGIC_TABLE", "ds${0..1}.table_${0..2}");
        tableRuleConfig.setDatabaseShardingStrategyConfig(new NoneShardingStrategyConfiguration());
        tableRuleConfig.setTableShardingStrategyConfig(new NoneShardingStrategyConfiguration());
        tableRuleConfig.setKeyGeneratorConfig(new KeyGeneratorConfiguration("INCREMENT", "col_1", new Properties()));
        tableRuleConfig.setLogicIndex("LOGIC_INDEX");
        TableRule actual = new TableRule(tableRuleConfig, createShardingDataSourceNames(), null);
        Assert.assertThat(actual.getLogicTable(), CoreMatchers.is("logic_table"));
        Assert.assertThat(actual.getActualDataNodes().size(), CoreMatchers.is(6));
        Assert.assertTrue(actual.getActualDataNodes().contains(new DataNode("ds0", "table_0")));
        Assert.assertTrue(actual.getActualDataNodes().contains(new DataNode("ds0", "table_1")));
        Assert.assertTrue(actual.getActualDataNodes().contains(new DataNode("ds0", "table_2")));
        Assert.assertTrue(actual.getActualDataNodes().contains(new DataNode("ds1", "table_0")));
        Assert.assertTrue(actual.getActualDataNodes().contains(new DataNode("ds1", "table_1")));
        Assert.assertTrue(actual.getActualDataNodes().contains(new DataNode("ds1", "table_2")));
        Assert.assertNotNull(actual.getDatabaseShardingStrategy());
        Assert.assertNotNull(actual.getTableShardingStrategy());
        Assert.assertThat(actual.getGenerateKeyColumn(), CoreMatchers.is("col_1"));
        Assert.assertThat(actual.getShardingKeyGenerator(), CoreMatchers.instanceOf(IncrementShardingKeyGenerator.class));
        Assert.assertThat(actual.getLogicIndex(), CoreMatchers.is("logic_index"));
    }

    @Test
    public void assertGetActualDatasourceNames() {
        TableRule actual = new TableRule(new TableRuleConfiguration("LOGIC_TABLE", "ds${0..1}.table_${0..2}"), createShardingDataSourceNames(), null);
        Assert.assertThat(actual.getActualDatasourceNames(), CoreMatchers.is(((Collection<String>) (Sets.newLinkedHashSet(Arrays.asList("ds0", "ds1"))))));
    }

    @Test
    public void assertGetActualTableNames() {
        TableRule actual = new TableRule(new TableRuleConfiguration("LOGIC_TABLE", "ds${0..1}.table_${0..2}"), createShardingDataSourceNames(), null);
        Assert.assertThat(actual.getActualTableNames("ds0"), CoreMatchers.is(((Collection<String>) (Sets.newLinkedHashSet(Arrays.asList("table_0", "table_1", "table_2"))))));
        Assert.assertThat(actual.getActualTableNames("ds1"), CoreMatchers.is(((Collection<String>) (Sets.newLinkedHashSet(Arrays.asList("table_0", "table_1", "table_2"))))));
        Assert.assertThat(actual.getActualTableNames("ds2"), CoreMatchers.is(((Collection<String>) (Collections.<String>emptySet()))));
    }

    @Test
    public void assertFindActualTableIndex() {
        TableRule actual = new TableRule(new TableRuleConfiguration("LOGIC_TABLE", "ds${0..1}.table_${0..2}"), createShardingDataSourceNames(), null);
        Assert.assertThat(actual.findActualTableIndex("ds1", "table_1"), CoreMatchers.is(4));
    }

    @Test
    public void assertNotFindActualTableIndex() {
        TableRule actual = new TableRule(new TableRuleConfiguration("LOGIC_TABLE", "ds${0..1}.table_${0..2}"), createShardingDataSourceNames(), null);
        Assert.assertThat(actual.findActualTableIndex("ds2", "table_2"), CoreMatchers.is((-1)));
    }

    @Test
    public void assertActualTableNameExisted() {
        TableRule actual = new TableRule(new TableRuleConfiguration("LOGIC_TABLE", "ds${0..1}.table_${0..2}"), createShardingDataSourceNames(), null);
        Assert.assertTrue(actual.isExisted("table_2"));
    }

    @Test
    public void assertActualTableNameNotExisted() {
        TableRule actual = new TableRule(new TableRuleConfiguration("LOGIC_TABLE", "ds${0..1}.table_${0..2}"), createShardingDataSourceNames(), null);
        Assert.assertFalse(actual.isExisted("table_3"));
    }

    @Test
    public void assertToString() {
        TableRule actual = new TableRule(new TableRuleConfiguration("LOGIC_TABLE", "ds${0..1}.table_${0..2}"), createShardingDataSourceNames(), null);
        String actualString = "TableRule(logicTable=logic_table, actualDataNodes=[DataNode(dataSourceName=ds0, tableName=table_0), DataNode(dataSourceName=ds0, tableName=table_1), " + (("DataNode(dataSourceName=ds0, tableName=table_2), DataNode(dataSourceName=ds1, tableName=table_0), DataNode(dataSourceName=ds1, tableName=table_1), " + "DataNode(dataSourceName=ds1, tableName=table_2)], databaseShardingStrategy=null, tableShardingStrategy=null, generateKeyColumn=null, shardingKeyGenerator=null, ") + "shardingEncryptorStrategy=null, logicIndex=null)");
        Assert.assertThat(actual.toString(), CoreMatchers.is(actualString));
    }
}

