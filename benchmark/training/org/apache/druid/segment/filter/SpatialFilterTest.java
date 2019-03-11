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
package org.apache.druid.segment.filter;


import Granularities.ALL;
import Granularities.DAY;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.druid.collections.spatial.search.RadiusBound;
import org.apache.druid.collections.spatial.search.RectangularBound;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.query.Druids;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.Result;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.timeseries.TimeseriesQuery;
import org.apache.druid.query.timeseries.TimeseriesQueryEngine;
import org.apache.druid.query.timeseries.TimeseriesQueryRunnerFactory;
import org.apache.druid.query.timeseries.TimeseriesResultValue;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.IndexMerger;
import org.apache.druid.segment.Segment;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.segment.writeout.OffHeapMemorySegmentWriteOutMediumFactory;
import org.joda.time.Interval;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class SpatialFilterTest {
    private static IndexMerger INDEX_MERGER = TestHelper.getTestIndexMergerV9(OffHeapMemorySegmentWriteOutMediumFactory.instance());

    private static IndexIO INDEX_IO = TestHelper.getTestIndexIO();

    public static final int NUM_POINTS = 5000;

    private static Interval DATA_INTERVAL = Intervals.of("2013-01-01/2013-01-07");

    private static AggregatorFactory[] METRIC_AGGS = new AggregatorFactory[]{ new CountAggregatorFactory("rows"), new LongSumAggregatorFactory("val", "val") };

    private static List<String> DIMS = Lists.newArrayList("dim", "lat", "long", "lat2", "long2");

    private final Segment segment;

    public SpatialFilterTest(Segment segment) {
        this.segment = segment;
    }

    @Test
    public void testSpatialQuery() {
        TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource("test").granularity(ALL).intervals(Collections.singletonList(Intervals.of("2013-01-01/2013-01-07"))).filters(new org.apache.druid.query.filter.SpatialDimFilter("dim.geo", new RadiusBound(new float[]{ 0.0F, 0.0F }, 5))).aggregators(Arrays.asList(new CountAggregatorFactory("rows"), new LongSumAggregatorFactory("val", "val"))).build();
        List<Result<TimeseriesResultValue>> expectedResults = Collections.singletonList(new Result<TimeseriesResultValue>(DateTimes.of("2013-01-01T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 3L).put("val", 59L).build())));
        try {
            TimeseriesQueryRunnerFactory factory = new TimeseriesQueryRunnerFactory(new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()), new TimeseriesQueryEngine(), QueryRunnerTestHelper.NOOP_QUERYWATCHER);
            QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(factory.createRunner(segment), factory.getToolchest());
            TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), new HashMap()));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Test
    public void testSpatialQueryWithOtherSpatialDim() {
        TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource("test").granularity(ALL).intervals(Collections.singletonList(Intervals.of("2013-01-01/2013-01-07"))).filters(new org.apache.druid.query.filter.SpatialDimFilter("spatialIsRad", new RadiusBound(new float[]{ 0.0F, 0.0F }, 5))).aggregators(Arrays.asList(new CountAggregatorFactory("rows"), new LongSumAggregatorFactory("val", "val"))).build();
        List<Result<TimeseriesResultValue>> expectedResults = Collections.singletonList(new Result<TimeseriesResultValue>(DateTimes.of("2013-01-01T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("val", 13L).build())));
        try {
            TimeseriesQueryRunnerFactory factory = new TimeseriesQueryRunnerFactory(new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()), new TimeseriesQueryEngine(), QueryRunnerTestHelper.NOOP_QUERYWATCHER);
            QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(factory.createRunner(segment), factory.getToolchest());
            TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), new HashMap()));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Test
    public void testSpatialQueryMorePoints() {
        TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource("test").granularity(DAY).intervals(Collections.singletonList(Intervals.of("2013-01-01/2013-01-07"))).filters(new org.apache.druid.query.filter.SpatialDimFilter("dim.geo", new RectangularBound(new float[]{ 0.0F, 0.0F }, new float[]{ 9.0F, 9.0F }))).aggregators(Arrays.asList(new CountAggregatorFactory("rows"), new LongSumAggregatorFactory("val", "val"))).build();
        List<Result<TimeseriesResultValue>> expectedResults = Arrays.asList(new Result<TimeseriesResultValue>(DateTimes.of("2013-01-01T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("val", 17L).build())), new Result<TimeseriesResultValue>(DateTimes.of("2013-01-02T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("val", 29L).build())), new Result<TimeseriesResultValue>(DateTimes.of("2013-01-03T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("val", 13L).build())), new Result<TimeseriesResultValue>(DateTimes.of("2013-01-04T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("val", 91L).build())), new Result<TimeseriesResultValue>(DateTimes.of("2013-01-05T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("val", 47L).build())));
        try {
            TimeseriesQueryRunnerFactory factory = new TimeseriesQueryRunnerFactory(new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()), new TimeseriesQueryEngine(), QueryRunnerTestHelper.NOOP_QUERYWATCHER);
            QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(factory.createRunner(segment), factory.getToolchest());
            TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), new HashMap()));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}

