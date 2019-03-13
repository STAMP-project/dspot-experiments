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
package org.apache.druid.segment;


import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import java.util.List;
import org.apache.druid.data.input.Row;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.groupby.GroupByQuery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MapVirtualColumnGroupByTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private QueryRunner<Row> runner;

    @Test
    public void testWithMapColumn() {
        final GroupByQuery query = new GroupByQuery(new org.apache.druid.query.TableDataSource(QueryRunnerTestHelper.dataSource), new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Intervals.of("2011/2012"))), VirtualColumns.create(ImmutableList.of(new MapVirtualColumn("keys", "values", "params"))), null, Granularities.ALL, ImmutableList.of(new DefaultDimensionSpec("params", "params")), ImmutableList.of(new CountAggregatorFactory("count")), null, null, null, null, null);
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Map column doesn't support getRow()");
        runner.run(QueryPlus.wrap(query), new HashMap()).toList();
    }

    @Test
    public void testWithSubColumn() {
        final GroupByQuery query = new GroupByQuery(new org.apache.druid.query.TableDataSource(QueryRunnerTestHelper.dataSource), new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Intervals.of("2011/2012"))), VirtualColumns.create(ImmutableList.of(new MapVirtualColumn("keys", "values", "params"))), null, Granularities.ALL, ImmutableList.of(new DefaultDimensionSpec("params.key3", "params.key3")), ImmutableList.of(new CountAggregatorFactory("count")), null, null, null, null, null);
        final List<Row> result = runner.run(QueryPlus.wrap(query), new HashMap()).toList();
        final List<Row> expected = ImmutableList.of(new org.apache.druid.data.input.MapBasedRow(DateTimes.of("2011-01-12T00:00:00.000Z"), MapVirtualColumnTestBase.mapOf("count", 1L, "params.key3", "value3")), new org.apache.druid.data.input.MapBasedRow(DateTimes.of("2011-01-12T00:00:00.000Z"), MapVirtualColumnTestBase.mapOf("count", 2L)));
        Assert.assertEquals(expected, result);
    }
}

