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
package org.apache.druid.client;


import Druids.TimeseriesQueryBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.druid.client.cache.Cache;
import org.apache.druid.client.selector.ServerSelector;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.Druids;
import org.apache.druid.query.QueryToolChestWarehouse;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.select.SelectQueryConfig;
import org.apache.druid.timeline.VersionedIntervalTimeline;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class CachingClusteredClientFunctionalityTest {
    private static final ObjectMapper OBJECT_MAPPER = CachingClusteredClientTestUtils.createObjectMapper();

    private static final Supplier<SelectQueryConfig> SELECT_CONFIG_SUPPLIER = Suppliers.ofInstance(new SelectQueryConfig(true));

    private static final Pair<QueryToolChestWarehouse, Closer> WAREHOUSE_AND_CLOSER = CachingClusteredClientTestUtils.createWarehouse(CachingClusteredClientFunctionalityTest.OBJECT_MAPPER, CachingClusteredClientFunctionalityTest.SELECT_CONFIG_SUPPLIER);

    private static final QueryToolChestWarehouse WAREHOUSE = CachingClusteredClientFunctionalityTest.WAREHOUSE_AND_CLOSER.lhs;

    private static final Closer RESOURCE_CLOSER = CachingClusteredClientFunctionalityTest.WAREHOUSE_AND_CLOSER.rhs;

    private CachingClusteredClient client;

    private VersionedIntervalTimeline<String, ServerSelector> timeline;

    private TimelineServerView serverView;

    private Cache cache;

    @Test
    public void testUncoveredInterval() {
        addToTimeline(Intervals.of("2015-01-02/2015-01-03"), "1");
        addToTimeline(Intervals.of("2015-01-04/2015-01-05"), "1");
        addToTimeline(Intervals.of("2015-02-04/2015-02-05"), "1");
        final Druids.TimeseriesQueryBuilder builder = Druids.newTimeseriesQueryBuilder().dataSource("test").intervals("2015-01-02/2015-01-03").granularity("day").aggregators(Collections.singletonList(new CountAggregatorFactory("rows"))).context(ImmutableMap.of("uncoveredIntervalsLimit", 3));
        Map<String, Object> responseContext = new HashMap<>();
        CachingClusteredClientFunctionalityTest.runQuery(client, builder.build(), responseContext);
        Assert.assertNull(responseContext.get("uncoveredIntervals"));
        builder.intervals("2015-01-01/2015-01-03");
        responseContext = new HashMap<>();
        CachingClusteredClientFunctionalityTest.runQuery(client, builder.build(), responseContext);
        assertUncovered(responseContext, false, "2015-01-01/2015-01-02");
        builder.intervals("2015-01-01/2015-01-04");
        responseContext = new HashMap<>();
        CachingClusteredClientFunctionalityTest.runQuery(client, builder.build(), responseContext);
        assertUncovered(responseContext, false, "2015-01-01/2015-01-02", "2015-01-03/2015-01-04");
        builder.intervals("2015-01-02/2015-01-04");
        responseContext = new HashMap<>();
        CachingClusteredClientFunctionalityTest.runQuery(client, builder.build(), responseContext);
        assertUncovered(responseContext, false, "2015-01-03/2015-01-04");
        builder.intervals("2015-01-01/2015-01-30");
        responseContext = new HashMap<>();
        CachingClusteredClientFunctionalityTest.runQuery(client, builder.build(), responseContext);
        assertUncovered(responseContext, false, "2015-01-01/2015-01-02", "2015-01-03/2015-01-04", "2015-01-05/2015-01-30");
        builder.intervals("2015-01-02/2015-01-30");
        responseContext = new HashMap<>();
        CachingClusteredClientFunctionalityTest.runQuery(client, builder.build(), responseContext);
        assertUncovered(responseContext, false, "2015-01-03/2015-01-04", "2015-01-05/2015-01-30");
        builder.intervals("2015-01-04/2015-01-30");
        responseContext = new HashMap<>();
        CachingClusteredClientFunctionalityTest.runQuery(client, builder.build(), responseContext);
        assertUncovered(responseContext, false, "2015-01-05/2015-01-30");
        builder.intervals("2015-01-10/2015-01-30");
        responseContext = new HashMap<>();
        CachingClusteredClientFunctionalityTest.runQuery(client, builder.build(), responseContext);
        assertUncovered(responseContext, false, "2015-01-10/2015-01-30");
        builder.intervals("2015-01-01/2015-02-25");
        responseContext = new HashMap<>();
        CachingClusteredClientFunctionalityTest.runQuery(client, builder.build(), responseContext);
        assertUncovered(responseContext, true, "2015-01-01/2015-01-02", "2015-01-03/2015-01-04", "2015-01-05/2015-02-04");
    }
}

