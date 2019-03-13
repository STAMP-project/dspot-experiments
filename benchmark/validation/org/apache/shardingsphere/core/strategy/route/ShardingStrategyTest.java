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
package org.apache.shardingsphere.core.strategy.route;


import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import org.apache.shardingsphere.api.config.sharding.strategy.ComplexShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.core.strategy.route.complex.ComplexShardingStrategy;
import org.apache.shardingsphere.core.strategy.route.fixture.ComplexKeysShardingAlgorithmFixture;
import org.apache.shardingsphere.core.strategy.route.fixture.PreciseShardingAlgorithmFixture;
import org.apache.shardingsphere.core.strategy.route.fixture.RangeShardingAlgorithmFixture;
import org.apache.shardingsphere.core.strategy.route.none.NoneShardingStrategy;
import org.apache.shardingsphere.core.strategy.route.standard.StandardShardingStrategy;
import org.apache.shardingsphere.core.strategy.route.value.RouteValue;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class ShardingStrategyTest {
    private final Collection<String> targets = Sets.newHashSet("1", "2", "3");

    @Test
    public void assertDoShardingWithoutShardingColumns() {
        NoneShardingStrategy strategy = new NoneShardingStrategy();
        Assert.assertThat(strategy.doSharding(targets, Collections.<RouteValue>emptySet()), CoreMatchers.is(targets));
    }

    @Test
    public void assertDoShardingForBetweenSingleKey() {
        StandardShardingStrategy strategy = new StandardShardingStrategy(new StandardShardingStrategyConfiguration("column", new PreciseShardingAlgorithmFixture(), new RangeShardingAlgorithmFixture()));
        Assert.assertThat(strategy.doSharding(targets, Collections.<RouteValue>singletonList(new org.apache.shardingsphere.core.strategy.route.value.BetweenRouteValue("column", "logicTable", Range.open(1, 3)))), CoreMatchers.is(((Collection<String>) (Sets.newHashSet("1")))));
    }

    @Test
    public void assertDoShardingForMultipleKeys() {
        ComplexShardingStrategy strategy = new ComplexShardingStrategy(new ComplexShardingStrategyConfiguration("column", new ComplexKeysShardingAlgorithmFixture()));
        Assert.assertThat(strategy.doSharding(targets, Collections.<RouteValue>singletonList(new org.apache.shardingsphere.core.strategy.route.value.ListRouteValue("column", "logicTable", Collections.singletonList(1)))), CoreMatchers.is(((Collection<String>) (Sets.newHashSet("1", "2", "3")))));
    }
}

