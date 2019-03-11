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


import ShardingPropertiesConstant.EXECUTOR_SIZE;
import ShardingPropertiesConstant.SQL_SHOW;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.core.constant.properties.ShardingProperties;
import org.apache.shardingsphere.core.constant.properties.ShardingPropertiesConstant;
import org.apache.shardingsphere.core.rule.BindingTableRule;
import org.apache.shardingsphere.core.rule.DataNode;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.rule.TableRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.ShardingContext;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.shardingjdbc.orchestration.spring.datasource.OrchestrationSpringShardingDataSource;
import org.apache.shardingsphere.shardingjdbc.orchestration.spring.fixture.IncrementKeyGenerator;
import org.apache.shardingsphere.shardingjdbc.orchestration.spring.util.FieldValueUtil;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;


@ContextConfiguration(locations = "classpath:META-INF/rdb/shardingOrchestration.xml")
public class OrchestrationShardingNamespaceTest extends AbstractJUnit4SpringContextTests {
    @Test
    public void assertSimpleShardingDataSource() {
        Map<String, DataSource> dataSourceMap = getDataSourceMap("simpleShardingOrchestration");
        ShardingRule shardingRule = getShardingRule("simpleShardingOrchestration");
        Assert.assertNotNull(dataSourceMap.get("dbtbl_0"));
        Assert.assertThat(shardingRule.getTableRules().size(), CoreMatchers.is(1));
        Assert.assertThat(shardingRule.getTableRules().iterator().next().getLogicTable(), CoreMatchers.is("t_order"));
    }

    @Test
    public void assertShardingRuleWithAttributesDataSource() {
        Map<String, DataSource> dataSourceMap = getDataSourceMap("shardingRuleWithAttributesDataSourceOrchestration");
        ShardingRule shardingRule = getShardingRule("shardingRuleWithAttributesDataSourceOrchestration");
        Assert.assertNotNull(dataSourceMap.get("dbtbl_0"));
        Assert.assertNotNull(dataSourceMap.get("dbtbl_1"));
        Assert.assertThat(shardingRule.getShardingDataSourceNames().getDefaultDataSourceName(), CoreMatchers.is("dbtbl_0"));
        Assert.assertTrue(Arrays.equals(shardingRule.getDefaultDatabaseShardingStrategy().getShardingColumns().toArray(new String[]{  }), new String[]{ applicationContext.getBean("standardStrategy", StandardShardingStrategyConfiguration.class).getShardingColumn() }));
        Assert.assertTrue(Arrays.equals(shardingRule.getDefaultTableShardingStrategy().getShardingColumns().toArray(new String[]{  }), new String[]{ applicationContext.getBean("inlineStrategy", InlineShardingStrategyConfiguration.class).getShardingColumn() }));
        Assert.assertThat(shardingRule.getDefaultShardingKeyGenerator().getClass().getName(), CoreMatchers.is(IncrementKeyGenerator.class.getCanonicalName()));
    }

    @Test
    public void assertTableRuleWithAttributesDataSource() {
        ShardingRule shardingRule = getShardingRule("tableRuleWithAttributesDataSourceOrchestration");
        Assert.assertThat(shardingRule.getTableRules().size(), CoreMatchers.is(1));
        TableRule tableRule = shardingRule.getTableRules().iterator().next();
        Assert.assertThat(tableRule.getLogicTable(), CoreMatchers.is("t_order"));
        Assert.assertThat(tableRule.getActualDataNodes().size(), CoreMatchers.is(8));
        Assert.assertTrue(tableRule.getActualDataNodes().contains(new DataNode("dbtbl_0", "t_order_0")));
        Assert.assertTrue(tableRule.getActualDataNodes().contains(new DataNode("dbtbl_0", "t_order_1")));
        Assert.assertTrue(tableRule.getActualDataNodes().contains(new DataNode("dbtbl_0", "t_order_2")));
        Assert.assertTrue(tableRule.getActualDataNodes().contains(new DataNode("dbtbl_0", "t_order_3")));
        Assert.assertTrue(tableRule.getActualDataNodes().contains(new DataNode("dbtbl_1", "t_order_0")));
        Assert.assertTrue(tableRule.getActualDataNodes().contains(new DataNode("dbtbl_1", "t_order_1")));
        Assert.assertTrue(tableRule.getActualDataNodes().contains(new DataNode("dbtbl_1", "t_order_2")));
        Assert.assertTrue(tableRule.getActualDataNodes().contains(new DataNode("dbtbl_1", "t_order_3")));
        Assert.assertTrue(Arrays.equals(tableRule.getDatabaseShardingStrategy().getShardingColumns().toArray(new String[]{  }), new String[]{ applicationContext.getBean("standardStrategy", StandardShardingStrategyConfiguration.class).getShardingColumn() }));
        Assert.assertTrue(Arrays.equals(tableRule.getTableShardingStrategy().getShardingColumns().toArray(new String[]{  }), new String[]{ applicationContext.getBean("inlineStrategy", InlineShardingStrategyConfiguration.class).getShardingColumn() }));
        Assert.assertThat(tableRule.getGenerateKeyColumn(), CoreMatchers.is("order_id"));
        Assert.assertThat(tableRule.getShardingKeyGenerator().getClass().getName(), CoreMatchers.is(IncrementKeyGenerator.class.getCanonicalName()));
    }

    @Test
    public void assertMultiTableRulesDataSource() {
        ShardingRule shardingRule = getShardingRule("multiTableRulesDataSourceOrchestration");
        Assert.assertThat(shardingRule.getTableRules().size(), CoreMatchers.is(2));
        Iterator<TableRule> tableRules = shardingRule.getTableRules().iterator();
        Assert.assertThat(tableRules.next().getLogicTable(), CoreMatchers.is("t_order"));
        Assert.assertThat(tableRules.next().getLogicTable(), CoreMatchers.is("t_order_item"));
    }

    @Test
    public void assertBindingTableRuleDatasource() {
        ShardingRule shardingRule = getShardingRule("bindingTableRuleDatasourceOrchestration");
        Assert.assertThat(shardingRule.getBindingTableRules().size(), CoreMatchers.is(1));
        BindingTableRule bindingTableRule = shardingRule.getBindingTableRules().iterator().next();
        Assert.assertThat(bindingTableRule.getBindingActualTable("dbtbl_0", "t_order", "t_order_item"), CoreMatchers.is("t_order"));
        Assert.assertThat(bindingTableRule.getBindingActualTable("dbtbl_1", "t_order", "t_order_item"), CoreMatchers.is("t_order"));
    }

    @Test
    public void assertMultiBindingTableRulesDatasource() {
        ShardingRule shardingRule = getShardingRule("multiBindingTableRulesDatasourceOrchestration");
        Assert.assertThat(shardingRule.getBindingTableRules().size(), CoreMatchers.is(2));
        Iterator<BindingTableRule> bindingTableRules = shardingRule.getBindingTableRules().iterator();
        BindingTableRule orderRule = bindingTableRules.next();
        Assert.assertThat(orderRule.getBindingActualTable("dbtbl_0", "t_order", "t_order_item"), CoreMatchers.is("t_order"));
        Assert.assertThat(orderRule.getBindingActualTable("dbtbl_1", "t_order", "t_order_item"), CoreMatchers.is("t_order"));
        BindingTableRule userRule = bindingTableRules.next();
        Assert.assertThat(userRule.getBindingActualTable("dbtbl_0", "t_user", "t_user_detail"), CoreMatchers.is("t_user"));
        Assert.assertThat(userRule.getBindingActualTable("dbtbl_1", "t_user", "t_user_detail"), CoreMatchers.is("t_user"));
    }

    @Test
    public void assertBroadcastTableRuleDatasource() {
        ShardingRule shardingRule = getShardingRule("broadcastTableRuleDatasourceOrchestration");
        Assert.assertThat(shardingRule.getBroadcastTables().size(), CoreMatchers.is(1));
        Assert.assertThat(shardingRule.getBroadcastTables().iterator().next(), CoreMatchers.is("t_config"));
    }

    @Test
    public void assertMultiBroadcastTableRulesDatasource() {
        ShardingRule shardingRule = getShardingRule("multiBroadcastTableRulesDatasourceOrchestration");
        Assert.assertThat(shardingRule.getBroadcastTables().size(), CoreMatchers.is(2));
        Assert.assertThat(((LinkedList<String>) (shardingRule.getBroadcastTables())).get(0), CoreMatchers.is("t_config1"));
        Assert.assertThat(((LinkedList<String>) (shardingRule.getBroadcastTables())).get(1), CoreMatchers.is("t_config2"));
    }

    @Test
    public void assertPropsDataSource() {
        OrchestrationSpringShardingDataSource shardingDataSource = applicationContext.getBean("propsDataSourceOrchestration", OrchestrationSpringShardingDataSource.class);
        ShardingDataSource dataSource = ((ShardingDataSource) (FieldValueUtil.getFieldValue(shardingDataSource, "dataSource", true)));
        ShardingContext shardingContext = ((ShardingContext) (FieldValueUtil.getFieldValue(dataSource, "shardingContext", false)));
        Assert.assertTrue(shardingContext.getShardingProperties().<Boolean>getValue(SQL_SHOW));
        ShardingProperties shardingProperties = shardingContext.getShardingProperties();
        boolean showSql = shardingProperties.getValue(SQL_SHOW);
        Assert.assertTrue(showSql);
        int executorSize = shardingProperties.getValue(EXECUTOR_SIZE);
        Assert.assertThat(executorSize, CoreMatchers.is(10));
        Assert.assertNull(ShardingPropertiesConstant.findByKey("foo"));
    }

    @Test
    public void assertShardingDataSourceType() {
        Assert.assertTrue(((applicationContext.getBean("simpleShardingOrchestration")) instanceof OrchestrationSpringShardingDataSource));
    }

    @Test
    public void assertDefaultActualDataNodes() {
        OrchestrationSpringShardingDataSource multiTableRulesDataSource = applicationContext.getBean("multiTableRulesDataSourceOrchestration", OrchestrationSpringShardingDataSource.class);
        ShardingDataSource dataSource = ((ShardingDataSource) (FieldValueUtil.getFieldValue(multiTableRulesDataSource, "dataSource", true)));
        Object shardingContext = FieldValueUtil.getFieldValue(dataSource, "shardingContext", false);
        ShardingRule shardingRule = ((ShardingRule) (FieldValueUtil.getFieldValue(shardingContext, "shardingRule")));
        Assert.assertThat(shardingRule.getTableRules().size(), CoreMatchers.is(2));
        Iterator<TableRule> tableRules = shardingRule.getTableRules().iterator();
        TableRule orderRule = tableRules.next();
        Assert.assertThat(orderRule.getActualDataNodes().size(), CoreMatchers.is(2));
        Assert.assertTrue(orderRule.getActualDataNodes().contains(new DataNode("dbtbl_0", "t_order")));
        Assert.assertTrue(orderRule.getActualDataNodes().contains(new DataNode("dbtbl_1", "t_order")));
        TableRule orderItemRule = tableRules.next();
        Assert.assertThat(orderItemRule.getActualDataNodes().size(), CoreMatchers.is(2));
        Assert.assertTrue(orderItemRule.getActualDataNodes().contains(new DataNode("dbtbl_0", "t_order_item")));
        Assert.assertTrue(orderItemRule.getActualDataNodes().contains(new DataNode("dbtbl_1", "t_order_item")));
    }
}

