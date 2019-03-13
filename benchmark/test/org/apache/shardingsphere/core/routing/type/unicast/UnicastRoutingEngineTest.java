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
package org.apache.shardingsphere.core.routing.type.unicast;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.shardingsphere.core.exception.ShardingConfigurationException;
import org.apache.shardingsphere.core.routing.type.RoutingResult;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class UnicastRoutingEngineTest {
    private ShardingRule shardingRule;

    @Test
    public void assertRoutingForShardingTable() {
        UnicastRoutingEngine unicastRoutingEngine = new UnicastRoutingEngine(shardingRule, Collections.singleton("t_order"));
        RoutingResult routingResult = unicastRoutingEngine.route();
        Assert.assertThat(routingResult, CoreMatchers.instanceOf(RoutingResult.class));
        Assert.assertThat(routingResult.getTableUnits().getTableUnits().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertRoutingForBroadcastTable() {
        UnicastRoutingEngine unicastRoutingEngine = new UnicastRoutingEngine(shardingRule, Collections.singleton("t_config"));
        RoutingResult routingResult = unicastRoutingEngine.route();
        Assert.assertThat(routingResult, CoreMatchers.instanceOf(RoutingResult.class));
        Assert.assertThat(routingResult.getTableUnits().getTableUnits().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertRoutingForNoTable() {
        UnicastRoutingEngine unicastRoutingEngine = new UnicastRoutingEngine(shardingRule, Collections.<String>emptyList());
        RoutingResult routingResult = unicastRoutingEngine.route();
        Assert.assertThat(routingResult, CoreMatchers.instanceOf(RoutingResult.class));
        Assert.assertThat(routingResult.getTableUnits().getTableUnits().size(), CoreMatchers.is(1));
    }

    @Test
    public void assertRoutingForShardingTableAndBroadcastTable() {
        Set<String> sets = new HashSet<>();
        sets.add("t_order");
        sets.add("t_config");
        UnicastRoutingEngine unicastRoutingEngine = new UnicastRoutingEngine(shardingRule, sets);
        RoutingResult routingResult = unicastRoutingEngine.route();
        Assert.assertThat(routingResult, CoreMatchers.instanceOf(RoutingResult.class));
        Assert.assertThat(routingResult.getTableUnits().getTableUnits().size(), CoreMatchers.is(1));
    }

    @Test(expected = ShardingConfigurationException.class)
    public void assertRouteForWithNoIntersection() {
        Set<String> sets = new HashSet<>();
        sets.add("t_order");
        sets.add("t_config");
        sets.add("t_product");
        UnicastRoutingEngine unicastRoutingEngine = new UnicastRoutingEngine(shardingRule, sets);
        unicastRoutingEngine.route();
    }
}

