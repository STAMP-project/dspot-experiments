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
package org.apache.druid.query;


import DruidMetrics.DATASOURCE;
import DruidMetrics.ID;
import DruidMetrics.INTERVAL;
import DruidMetrics.TYPE;
import Granularities.ALL;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.druid.java.util.emitter.service.ServiceEmitter;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.query.topn.TopNQuery;
import org.apache.druid.query.topn.TopNQueryBuilder;
import org.apache.druid.segment.TestHelper;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;


public class DefaultQueryMetricsTest {
    /**
     * Tests that passed a query {@link DefaultQueryMetrics} produces events with a certain set of dimensions, no more,
     * no less.
     */
    @Test
    public void testDefaultQueryMetricsQuery() {
        CachingEmitter cachingEmitter = new CachingEmitter();
        ServiceEmitter serviceEmitter = new ServiceEmitter("", "", cachingEmitter);
        DefaultQueryMetrics<Query<?>> queryMetrics = new DefaultQueryMetrics(TestHelper.makeJsonMapper());
        TopNQuery query = new TopNQueryBuilder().dataSource("xx").granularity(ALL).dimension(new org.apache.druid.query.dimension.ListFilteredDimensionSpec(new DefaultDimensionSpec("tags", "tags"), ImmutableSet.of("t3"), null)).metric("count").intervals(QueryRunnerTestHelper.fullOnIntervalSpec).aggregators(Collections.singletonList(new CountAggregatorFactory("count"))).threshold(5).filters(new SelectorDimFilter("tags", "t3", null)).build();
        queryMetrics.query(query);
        queryMetrics.reportQueryTime(0).emit(serviceEmitter);
        Map<String, Object> actualEvent = cachingEmitter.getLastEmittedEvent().toMap();
        Assert.assertEquals(12, actualEvent.size());
        Assert.assertTrue(actualEvent.containsKey("feed"));
        Assert.assertTrue(actualEvent.containsKey("timestamp"));
        Assert.assertEquals("", actualEvent.get("host"));
        Assert.assertEquals("", actualEvent.get("service"));
        Assert.assertEquals("xx", actualEvent.get(DATASOURCE));
        Assert.assertEquals(query.getType(), actualEvent.get(TYPE));
        List<Interval> expectedIntervals = QueryRunnerTestHelper.fullOnIntervalSpec.getIntervals();
        List<String> expectedStringIntervals = expectedIntervals.stream().map(Interval::toString).collect(Collectors.toList());
        Assert.assertEquals(expectedStringIntervals, actualEvent.get(INTERVAL));
        Assert.assertEquals("true", actualEvent.get("hasFilters"));
        Assert.assertEquals(expectedIntervals.get(0).toDuration().toString(), actualEvent.get("duration"));
        Assert.assertEquals("", actualEvent.get(ID));
        Assert.assertEquals("query/time", actualEvent.get("metric"));
        Assert.assertEquals(0L, actualEvent.get("value"));
    }

    @Test
    public void testDefaultQueryMetricsMetricNamesAndUnits() {
        CachingEmitter cachingEmitter = new CachingEmitter();
        ServiceEmitter serviceEmitter = new ServiceEmitter("", "", cachingEmitter);
        DefaultQueryMetrics<Query<?>> queryMetrics = new DefaultQueryMetrics(TestHelper.makeJsonMapper());
        DefaultQueryMetricsTest.testQueryMetricsDefaultMetricNamesAndUnits(cachingEmitter, serviceEmitter, queryMetrics);
    }
}

