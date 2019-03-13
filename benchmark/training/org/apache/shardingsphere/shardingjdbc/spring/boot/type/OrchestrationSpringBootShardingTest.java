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
package org.apache.shardingsphere.shardingjdbc.spring.boot.type;


import ShardingPropertiesConstant.EXECUTOR_SIZE;
import ShardingPropertiesConstant.SQL_SHOW;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.shardingsphere.core.constant.properties.ShardingProperties;
import org.apache.shardingsphere.core.rule.DataNode;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.core.strategy.route.inline.InlineShardingStrategy;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.ShardingContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.shardingjdbc.orchestration.internal.datasource.OrchestrationShardingDataSource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OrchestrationSpringBootShardingTest.class)
@SpringBootApplication
@ActiveProfiles("sharding")
public class OrchestrationSpringBootShardingTest {
    @Resource
    private DataSource dataSource;

    @Test
    public void assertWithShardingDataSource() {
        Assert.assertTrue(((dataSource) instanceof OrchestrationShardingDataSource));
        ShardingDataSource shardingDataSource = getFieldValue("dataSource", OrchestrationShardingDataSource.class, dataSource);
        ShardingContext shardingContext = getFieldValue("shardingContext", ShardingDataSource.class, shardingDataSource);
        for (DataSource each : shardingDataSource.getDataSourceMap().values()) {
            Assert.assertThat(getMaxTotal(), CoreMatchers.is(16));
        }
        Assert.assertTrue(shardingContext.getShardingProperties().<Boolean>getValue(SQL_SHOW));
        ShardingProperties shardingProperties = shardingContext.getShardingProperties();
        Assert.assertTrue(((Boolean) (shardingProperties.getValue(SQL_SHOW))));
        Assert.assertThat(((Integer) (shardingProperties.getValue(EXECUTOR_SIZE))), CoreMatchers.is(100));
    }

    @Test
    public void assertWithShardingDataSourceNames() {
        ShardingDataSource shardingDataSource = getFieldValue("dataSource", OrchestrationShardingDataSource.class, dataSource);
        ShardingContext shardingContext = getFieldValue("shardingContext", ShardingDataSource.class, shardingDataSource);
        ShardingRule shardingRule = shardingContext.getShardingRule();
        Assert.assertThat(shardingRule.getShardingDataSourceNames().getDataSourceNames().size(), CoreMatchers.is(3));
        Assert.assertTrue(shardingRule.getShardingDataSourceNames().getDataSourceNames().contains("ds"));
        Assert.assertTrue(shardingRule.getShardingDataSourceNames().getDataSourceNames().contains("ds_0"));
        Assert.assertTrue(shardingRule.getShardingDataSourceNames().getDataSourceNames().contains("ds_1"));
    }

    @Test
    public void assertWithTableRules() {
        ShardingDataSource shardingDataSource = getFieldValue("dataSource", OrchestrationShardingDataSource.class, dataSource);
        ShardingContext shardingContext = getFieldValue("shardingContext", ShardingDataSource.class, shardingDataSource);
        ShardingRule shardingRule = shardingContext.getShardingRule();
        Assert.assertThat(shardingRule.getTableRules().size(), CoreMatchers.is(2));
        TableRule tableRule1 = new java.util.LinkedList(shardingRule.getTableRules()).get(0);
        Assert.assertThat(tableRule1.getLogicTable(), CoreMatchers.is("t_order_item"));
        Assert.assertThat(tableRule1.getActualDataNodes().size(), CoreMatchers.is(4));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_0", "t_order_item_0")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_0", "t_order_item_1")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_1", "t_order_item_0")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_1", "t_order_item_1")));
        Assert.assertThat(tableRule1.getTableShardingStrategy(), CoreMatchers.instanceOf(InlineShardingStrategy.class));
        Assert.assertThat(tableRule1.getTableShardingStrategy().getShardingColumns().iterator().next(), CoreMatchers.is("order_id"));
        Assert.assertThat(tableRule1.getGenerateKeyColumn(), CoreMatchers.is("order_item_id"));
        TableRule tableRule2 = new java.util.LinkedList(shardingRule.getTableRules()).get(1);
        Assert.assertThat(tableRule2.getLogicTable(), CoreMatchers.is("t_order"));
        Assert.assertThat(tableRule2.getActualDataNodes().size(), CoreMatchers.is(4));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_0", "t_order_0")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_0", "t_order_1")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_1", "t_order_0")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_1", "t_order_1")));
        Assert.assertThat(tableRule1.getTableShardingStrategy(), CoreMatchers.instanceOf(InlineShardingStrategy.class));
        Assert.assertThat(tableRule1.getTableShardingStrategy().getShardingColumns().iterator().next(), CoreMatchers.is("order_id"));
        Assert.assertThat(tableRule2.getGenerateKeyColumn(), CoreMatchers.is("order_id"));
    }

    @Test
    public void assertWithBindingTableRules() {
        ShardingDataSource shardingDataSource = getFieldValue("dataSource", OrchestrationShardingDataSource.class, dataSource);
        ShardingContext shardingContext = getFieldValue("shardingContext", ShardingDataSource.class, shardingDataSource);
        ShardingRule shardingRule = shardingContext.getShardingRule();
        Assert.assertThat(shardingRule.getBindingTableRules().size(), CoreMatchers.is(2));
        TableRule tableRule1 = new java.util.LinkedList(shardingRule.getTableRules()).get(0);
        Assert.assertThat(tableRule1.getLogicTable(), CoreMatchers.is("t_order_item"));
        Assert.assertThat(tableRule1.getActualDataNodes().size(), CoreMatchers.is(4));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_0", "t_order_item_0")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_0", "t_order_item_1")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_1", "t_order_item_0")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_1", "t_order_item_1")));
        Assert.assertThat(tableRule1.getTableShardingStrategy(), CoreMatchers.instanceOf(InlineShardingStrategy.class));
        Assert.assertThat(tableRule1.getTableShardingStrategy().getShardingColumns().iterator().next(), CoreMatchers.is("order_id"));
        Assert.assertThat(tableRule1.getGenerateKeyColumn(), CoreMatchers.is("order_item_id"));
        TableRule tableRule2 = new java.util.LinkedList(shardingRule.getTableRules()).get(1);
        Assert.assertThat(tableRule2.getLogicTable(), CoreMatchers.is("t_order"));
        Assert.assertThat(tableRule2.getActualDataNodes().size(), CoreMatchers.is(4));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_0", "t_order_0")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_0", "t_order_1")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_1", "t_order_0")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_1", "t_order_1")));
        Assert.assertThat(tableRule1.getTableShardingStrategy(), CoreMatchers.instanceOf(InlineShardingStrategy.class));
        Assert.assertThat(tableRule1.getTableShardingStrategy().getShardingColumns().iterator().next(), CoreMatchers.is("order_id"));
        Assert.assertThat(tableRule2.getGenerateKeyColumn(), CoreMatchers.is("order_id"));
    }

    @Test
    public void assertWithBroadcastTables() {
        ShardingDataSource shardingDataSource = getFieldValue("dataSource", OrchestrationShardingDataSource.class, dataSource);
        ShardingContext shardingContext = getFieldValue("shardingContext", ShardingDataSource.class, shardingDataSource);
        ShardingRule shardingRule = shardingContext.getShardingRule();
        Assert.assertThat(shardingRule.getBroadcastTables().size(), CoreMatchers.is(1));
        Assert.assertThat(shardingRule.getBroadcastTables().iterator().next(), CoreMatchers.is("t_config"));
    }
}

