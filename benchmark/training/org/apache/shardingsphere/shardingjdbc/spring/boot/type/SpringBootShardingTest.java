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
import org.apache.shardingsphere.core.strategy.encrypt.ShardingEncryptorStrategy;
import org.apache.shardingsphere.core.strategy.route.inline.InlineShardingStrategy;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.ShardingContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootShardingTest.class)
@SpringBootApplication
@ActiveProfiles("sharding")
public class SpringBootShardingTest {
    @Resource
    private DataSource dataSource;

    @Test
    public void assertWithShardingDataSource() {
        Assert.assertThat(dataSource, CoreMatchers.instanceOf(ShardingDataSource.class));
        ShardingContext shardingContext = getFieldValue("shardingContext", ShardingDataSource.class, dataSource);
        for (DataSource each : getDataSourceMap().values()) {
            Assert.assertThat(getMaxTotal(), CoreMatchers.is(100));
        }
        Assert.assertTrue(shardingContext.getShardingProperties().<Boolean>getValue(SQL_SHOW));
        ShardingProperties shardingProperties = shardingContext.getShardingProperties();
        Assert.assertTrue(((Boolean) (shardingProperties.getValue(SQL_SHOW))));
        Assert.assertThat(((Integer) (shardingProperties.getValue(EXECUTOR_SIZE))), CoreMatchers.is(100));
        ShardingEncryptorStrategy shardingEncryptorStrategy = shardingContext.getShardingRule().getTableRule("t_order").getShardingEncryptorStrategy();
        Assert.assertThat(shardingEncryptorStrategy.getColumns().size(), CoreMatchers.is(2));
        Assert.assertThat(shardingEncryptorStrategy.getAssistedQueryColumns().size(), CoreMatchers.is(0));
        Assert.assertThat(shardingEncryptorStrategy.getShardingEncryptor().getProperties().getProperty("appToken"), CoreMatchers.is("business"));
    }

    @Test
    public void assertWithShardingDataSourceNames() {
        ShardingContext shardingContext = getFieldValue("shardingContext", ShardingDataSource.class, dataSource);
        ShardingRule shardingRule = shardingContext.getShardingRule();
        Assert.assertThat(shardingRule.getShardingDataSourceNames().getDataSourceNames().size(), CoreMatchers.is(3));
        Assert.assertTrue(shardingRule.getShardingDataSourceNames().getDataSourceNames().contains("ds"));
        Assert.assertTrue(shardingRule.getShardingDataSourceNames().getDataSourceNames().contains("ds_0"));
        Assert.assertTrue(shardingRule.getShardingDataSourceNames().getDataSourceNames().contains("ds_1"));
    }

    @Test
    public void assertWithTableRules() {
        ShardingContext shardingContext = getFieldValue("shardingContext", ShardingDataSource.class, dataSource);
        ShardingRule shardingRule = shardingContext.getShardingRule();
        Assert.assertThat(shardingRule.getTableRules().size(), CoreMatchers.is(2));
        TableRule tableRule1 = shardingRule.getTableRule("t_order");
        Assert.assertThat(tableRule1.getActualDataNodes().size(), CoreMatchers.is(4));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_0", "t_order_0")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_0", "t_order_1")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_1", "t_order_0")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_1", "t_order_1")));
        Assert.assertThat(tableRule1.getTableShardingStrategy(), CoreMatchers.instanceOf(InlineShardingStrategy.class));
        Assert.assertThat(tableRule1.getTableShardingStrategy().getShardingColumns().iterator().next(), CoreMatchers.is("order_id"));
        Assert.assertThat(tableRule1.getGenerateKeyColumn(), CoreMatchers.is("order_id"));
        TableRule tableRule2 = shardingRule.getTableRule("t_order_item");
        Assert.assertThat(tableRule2.getActualDataNodes().size(), CoreMatchers.is(4));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_0", "t_order_item_0")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_0", "t_order_item_1")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_1", "t_order_item_0")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_1", "t_order_item_1")));
        Assert.assertThat(tableRule1.getTableShardingStrategy(), CoreMatchers.instanceOf(InlineShardingStrategy.class));
        Assert.assertThat(tableRule1.getTableShardingStrategy().getShardingColumns().iterator().next(), CoreMatchers.is("order_id"));
        Assert.assertThat(tableRule2.getGenerateKeyColumn(), CoreMatchers.is("order_item_id"));
    }

    @Test
    public void assertWithBindingTableRules() {
        ShardingContext shardingContext = getFieldValue("shardingContext", ShardingDataSource.class, dataSource);
        ShardingRule shardingRule = shardingContext.getShardingRule();
        Assert.assertThat(shardingRule.getBindingTableRules().size(), CoreMatchers.is(2));
        TableRule tableRule1 = shardingRule.getTableRule("t_order");
        Assert.assertThat(tableRule1.getLogicTable(), CoreMatchers.is("t_order"));
        Assert.assertThat(tableRule1.getActualDataNodes().size(), CoreMatchers.is(4));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_0", "t_order_0")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_0", "t_order_1")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_1", "t_order_0")));
        Assert.assertTrue(tableRule1.getActualDataNodes().contains(new DataNode("ds_1", "t_order_1")));
        Assert.assertThat(tableRule1.getTableShardingStrategy(), CoreMatchers.instanceOf(InlineShardingStrategy.class));
        Assert.assertThat(tableRule1.getTableShardingStrategy().getShardingColumns().iterator().next(), CoreMatchers.is("order_id"));
        Assert.assertThat(tableRule1.getGenerateKeyColumn(), CoreMatchers.is("order_id"));
        TableRule tableRule2 = shardingRule.getTableRule("t_order_item");
        Assert.assertThat(tableRule2.getLogicTable(), CoreMatchers.is("t_order_item"));
        Assert.assertThat(tableRule2.getActualDataNodes().size(), CoreMatchers.is(4));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_0", "t_order_item_0")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_0", "t_order_item_1")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_1", "t_order_item_0")));
        Assert.assertTrue(tableRule2.getActualDataNodes().contains(new DataNode("ds_1", "t_order_item_1")));
        Assert.assertThat(tableRule1.getTableShardingStrategy(), CoreMatchers.instanceOf(InlineShardingStrategy.class));
        Assert.assertThat(tableRule1.getTableShardingStrategy().getShardingColumns().iterator().next(), CoreMatchers.is("order_id"));
        Assert.assertThat(tableRule2.getGenerateKeyColumn(), CoreMatchers.is("order_item_id"));
    }

    @Test
    public void assertWithBroadcastTables() {
        ShardingContext shardingContext = getFieldValue("shardingContext", ShardingDataSource.class, dataSource);
        ShardingRule shardingRule = shardingContext.getShardingRule();
        Assert.assertThat(shardingRule.getBroadcastTables().size(), CoreMatchers.is(1));
        Assert.assertThat(shardingRule.getBroadcastTables().iterator().next(), CoreMatchers.is("t_config"));
    }
}

