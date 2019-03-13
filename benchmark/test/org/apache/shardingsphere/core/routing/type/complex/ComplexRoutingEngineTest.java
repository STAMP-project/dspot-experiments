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
package org.apache.shardingsphere.core.routing.type.complex;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.shardingsphere.core.optimizer.condition.ShardingCondition;
import org.apache.shardingsphere.core.parsing.parser.sql.SQLStatement;
import org.apache.shardingsphere.core.routing.type.RoutingResult;
import org.apache.shardingsphere.core.routing.type.TableUnit;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.core.strategy.route.value.RouteValue;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public final class ComplexRoutingEngineTest {
    private ShardingRule shardingRule;

    @Test
    public void assertRoutingForBindingTables() {
        List<ShardingCondition> shardingConditions = new ArrayList<>();
        RouteValue shardingValue1 = new org.apache.shardingsphere.core.strategy.route.value.ListRouteValue("user_id", "t_order", Collections.singleton(1L));
        RouteValue shardingValue2 = new org.apache.shardingsphere.core.strategy.route.value.ListRouteValue("order_id", "t_order", Collections.singleton(1L));
        ShardingCondition shardingCondition = new ShardingCondition();
        shardingCondition.getShardingValues().add(shardingValue1);
        shardingCondition.getShardingValues().add(shardingValue2);
        shardingConditions.add(shardingCondition);
        ComplexRoutingEngine complexRoutingEngine = new ComplexRoutingEngine(Mockito.mock(SQLStatement.class), shardingRule, Arrays.asList("t_order", "t_order_item"), new org.apache.shardingsphere.core.optimizer.condition.ShardingConditions(shardingConditions));
        RoutingResult routingResult = complexRoutingEngine.route();
        List<TableUnit> tableUnitList = new ArrayList(routingResult.getTableUnits().getTableUnits());
        Assert.assertThat(routingResult, CoreMatchers.instanceOf(RoutingResult.class));
        Assert.assertThat(routingResult.getTableUnits().getTableUnits().size(), CoreMatchers.is(1));
        Assert.assertThat(tableUnitList.get(0).getDataSourceName(), CoreMatchers.is("ds1"));
        Assert.assertThat(tableUnitList.get(0).getRoutingTables().size(), CoreMatchers.is(1));
        Assert.assertThat(tableUnitList.get(0).getRoutingTables().get(0).getActualTableName(), CoreMatchers.is("t_order_1"));
        Assert.assertThat(tableUnitList.get(0).getRoutingTables().get(0).getLogicTableName(), CoreMatchers.is("t_order"));
    }

    @Test
    public void assertRoutingForShardingTableJoinBroadcastTable() {
        List<ShardingCondition> shardingConditions = new ArrayList<>();
        RouteValue shardingValue1 = new org.apache.shardingsphere.core.strategy.route.value.ListRouteValue("user_id", "t_order", Collections.singleton(1L));
        RouteValue shardingValue2 = new org.apache.shardingsphere.core.strategy.route.value.ListRouteValue("order_id", "t_order", Collections.singleton(1L));
        ShardingCondition shardingCondition = new ShardingCondition();
        shardingCondition.getShardingValues().add(shardingValue1);
        shardingCondition.getShardingValues().add(shardingValue2);
        shardingConditions.add(shardingCondition);
        ComplexRoutingEngine complexRoutingEngine = new ComplexRoutingEngine(Mockito.mock(SQLStatement.class), shardingRule, Arrays.asList("t_order", "t_config"), new org.apache.shardingsphere.core.optimizer.condition.ShardingConditions(shardingConditions));
        RoutingResult routingResult = complexRoutingEngine.route();
        List<TableUnit> tableUnitList = new ArrayList(routingResult.getTableUnits().getTableUnits());
        Assert.assertThat(routingResult, CoreMatchers.instanceOf(RoutingResult.class));
        Assert.assertThat(routingResult.getTableUnits().getTableUnits().size(), CoreMatchers.is(1));
        Assert.assertThat(tableUnitList.get(0).getDataSourceName(), CoreMatchers.is("ds1"));
        Assert.assertThat(tableUnitList.get(0).getRoutingTables().size(), CoreMatchers.is(1));
        Assert.assertThat(tableUnitList.get(0).getRoutingTables().get(0).getActualTableName(), CoreMatchers.is("t_order_1"));
        Assert.assertThat(tableUnitList.get(0).getRoutingTables().get(0).getLogicTableName(), CoreMatchers.is("t_order"));
    }
}

