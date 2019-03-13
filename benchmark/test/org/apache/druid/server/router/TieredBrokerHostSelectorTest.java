/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.router;


import DruidServer.DEFAULT_TIER;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.druid.client.selector.Server;
import org.apache.druid.discovery.DiscoveryDruidNode;
import org.apache.druid.discovery.DruidNodeDiscovery;
import org.apache.druid.discovery.DruidNodeDiscoveryProvider;
import org.apache.druid.guice.annotations.Json;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.query.Druids;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.timeseries.TimeseriesQuery;
import org.apache.druid.server.coordinator.rules.Rule;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class TieredBrokerHostSelectorTest {
    private DruidNodeDiscoveryProvider druidNodeDiscoveryProvider;

    private DruidNodeDiscovery druidNodeDiscovery;

    private TieredBrokerHostSelector brokerSelector;

    private DiscoveryDruidNode node1;

    private DiscoveryDruidNode node2;

    private DiscoveryDruidNode node3;

    @Test
    public void testBasicSelect() {
        TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource("test").granularity("all").aggregators(Collections.singletonList(new CountAggregatorFactory("rows"))).intervals(Collections.singletonList(Intervals.of("2011-08-31/2011-09-01"))).build();
        Pair<String, Server> p = brokerSelector.select(query);
        Assert.assertEquals("coldBroker", p.lhs);
        Assert.assertEquals("coldHost1:8080", p.rhs.getHost());
        p = brokerSelector.select(query);
        Assert.assertEquals("coldBroker", p.lhs);
        Assert.assertEquals("coldHost2:8080", p.rhs.getHost());
        p = brokerSelector.select(query);
        Assert.assertEquals("coldBroker", p.lhs);
        Assert.assertEquals("coldHost1:8080", p.rhs.getHost());
    }

    @Test
    public void testBasicSelect2() {
        Pair<String, Server> p = brokerSelector.select(Druids.newTimeseriesQueryBuilder().dataSource("test").granularity("all").aggregators(Collections.singletonList(new CountAggregatorFactory("rows"))).intervals(Collections.singletonList(Intervals.of("2013-08-31/2013-09-01"))).build());
        Assert.assertEquals("hotBroker", p.lhs);
        Assert.assertEquals("hotHost:8080", p.rhs.getHost());
    }

    @Test
    public void testSelectMatchesNothing() {
        String brokerName = ((String) (brokerSelector.select(Druids.newTimeseriesQueryBuilder().dataSource("test").granularity("all").aggregators(Collections.singletonList(new CountAggregatorFactory("rows"))).intervals(Collections.singletonList(Intervals.of("2010-08-31/2010-09-01"))).build()).lhs));
        Assert.assertEquals("hotBroker", brokerName);
    }

    @Test
    public void testSelectMultiInterval() {
        String brokerName = ((String) (brokerSelector.select(Druids.newTimeseriesQueryBuilder().dataSource("test").aggregators(Collections.singletonList(new CountAggregatorFactory("count"))).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Arrays.asList(Intervals.of("2013-08-31/2013-09-01"), Intervals.of("2012-08-31/2012-09-01"), Intervals.of("2011-08-31/2011-09-01")))).build()).lhs));
        Assert.assertEquals("coldBroker", brokerName);
    }

    @Test
    public void testSelectMultiInterval2() {
        String brokerName = ((String) (brokerSelector.select(Druids.newTimeseriesQueryBuilder().dataSource("test").aggregators(Collections.singletonList(new CountAggregatorFactory("count"))).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Arrays.asList(Intervals.of("2011-08-31/2011-09-01"), Intervals.of("2012-08-31/2012-09-01"), Intervals.of("2013-08-31/2013-09-01")))).build()).lhs));
        Assert.assertEquals("coldBroker", brokerName);
    }

    @Test
    public void testPrioritySelect() {
        String brokerName = ((String) (brokerSelector.select(Druids.newTimeseriesQueryBuilder().dataSource("test").aggregators(Collections.singletonList(new CountAggregatorFactory("count"))).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Arrays.asList(Intervals.of("2011-08-31/2011-09-01"), Intervals.of("2012-08-31/2012-09-01"), Intervals.of("2013-08-31/2013-09-01")))).context(ImmutableMap.of("priority", (-1))).build()).lhs));
        Assert.assertEquals("hotBroker", brokerName);
    }

    @Test
    public void testPrioritySelect2() {
        String brokerName = ((String) (brokerSelector.select(Druids.newTimeseriesQueryBuilder().dataSource("test").aggregators(Collections.singletonList(new CountAggregatorFactory("count"))).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Arrays.asList(Intervals.of("2011-08-31/2011-09-01"), Intervals.of("2012-08-31/2012-09-01"), Intervals.of("2013-08-31/2013-09-01")))).context(ImmutableMap.of("priority", 5)).build()).lhs));
        Assert.assertEquals("hotBroker", brokerName);
    }

    @Test
    public void testGetAllBrokers() {
        Assert.assertEquals(ImmutableMap.of("mediumBroker", ImmutableList.of(), "coldBroker", ImmutableList.of("coldHost1:8080", "coldHost2:8080"), "hotBroker", ImmutableList.of("hotHost:8080")), Maps.transformValues(brokerSelector.getAllBrokers(), new Function<List<Server>, List<String>>() {
            @Override
            public List<String> apply(@Nullable
            List<Server> servers) {
                return Lists.transform(servers, ( server) -> server.getHost());
            }
        }));
    }

    private static class TestRuleManager extends CoordinatorRuleManager {
        public TestRuleManager(@Json
        ObjectMapper jsonMapper, Supplier<TieredBrokerConfig> config) {
            super(jsonMapper, config, null);
        }

        @Override
        public boolean isStarted() {
            return true;
        }

        @Override
        public List<Rule> getRulesWithDefault(String dataSource) {
            return Arrays.asList(new org.apache.druid.server.coordinator.rules.IntervalLoadRule(Intervals.of("2013/2014"), ImmutableMap.of("hot", 1)), new org.apache.druid.server.coordinator.rules.IntervalLoadRule(Intervals.of("2012/2013"), ImmutableMap.of("medium", 1)), new org.apache.druid.server.coordinator.rules.IntervalLoadRule(Intervals.of("2011/2012"), ImmutableMap.of(DEFAULT_TIER, 1)));
        }
    }
}

