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


import TimeBoundaryQuery.MAX_TIME;
import TimeBoundaryQuery.MIN_TIME;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.druid.collections.CloseableStupidPool;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.java.util.common.granularity.Granularity;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.Result;
import org.apache.druid.query.TestQueryRunners;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleMaxAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleMinAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleSumAggregatorFactory;
import org.apache.druid.query.aggregation.hyperloglog.HyperUniquesAggregatorFactory;
import org.apache.druid.query.aggregation.post.ArithmeticPostAggregator;
import org.apache.druid.query.aggregation.post.ConstantPostAggregator;
import org.apache.druid.query.aggregation.post.FieldAccessPostAggregator;
import org.apache.druid.query.search.SearchHit;
import org.apache.druid.query.search.SearchQuery;
import org.apache.druid.query.search.SearchResultValue;
import org.apache.druid.query.spec.QuerySegmentSpec;
import org.apache.druid.query.timeboundary.TimeBoundaryQuery;
import org.apache.druid.query.timeboundary.TimeBoundaryResultValue;
import org.apache.druid.query.timeseries.TimeseriesQuery;
import org.apache.druid.query.timeseries.TimeseriesResultValue;
import org.apache.druid.query.topn.TopNQuery;
import org.apache.druid.query.topn.TopNResultValue;
import org.junit.Ignore;
import org.junit.Test;


/**
 *
 */
@Ignore
public class AppendTest {
    private static final AggregatorFactory[] METRIC_AGGS = new AggregatorFactory[]{ new DoubleSumAggregatorFactory("index", "index"), new CountAggregatorFactory("count"), new HyperUniquesAggregatorFactory("quality_uniques", "quality") };

    private static final AggregatorFactory[] METRIC_AGGS_NO_UNIQ = new AggregatorFactory[]{ new DoubleSumAggregatorFactory("index", "index"), new CountAggregatorFactory("count") };

    final String dataSource = "testing";

    final Granularity allGran = Granularities.ALL;

    final String marketDimension = "market";

    final String qualityDimension = "quality";

    final String placementDimension = "placement";

    final String placementishDimension = "placementish";

    final String indexMetric = "index";

    final CountAggregatorFactory rowsCount = new CountAggregatorFactory("rows");

    final DoubleSumAggregatorFactory indexDoubleSum = new DoubleSumAggregatorFactory("index", "index");

    final HyperUniquesAggregatorFactory uniques = new HyperUniquesAggregatorFactory("uniques", "quality_uniques");

    final ConstantPostAggregator constant = new ConstantPostAggregator("const", 1L);

    final FieldAccessPostAggregator rowsPostAgg = new FieldAccessPostAggregator("rows", "rows");

    final FieldAccessPostAggregator indexPostAgg = new FieldAccessPostAggregator("index", "index");

    final ArithmeticPostAggregator addRowsIndexConstant = new ArithmeticPostAggregator("addRowsIndexConstant", "+", Lists.newArrayList(constant, rowsPostAgg, indexPostAgg));

    final List<AggregatorFactory> commonAggregators = Arrays.asList(rowsCount, indexDoubleSum, uniques);

    final QuerySegmentSpec fullOnInterval = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.of("1970-01-01T00:00:00.000Z/2020-01-01T00:00:00.000Z")));

    private Segment segment;

    private Segment segment2;

    private Segment segment3;

    @Test
    public void testTimeBoundary() {
        List<Result<TimeBoundaryResultValue>> expectedResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-15T02:00:00.000Z")))));
        TimeBoundaryQuery query = org.apache.druid.query.Druids.newTimeBoundaryQueryBuilder().dataSource(dataSource).build();
        QueryRunner runner = TestQueryRunners.makeTimeBoundaryQueryRunner(segment);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testTimeBoundary2() {
        List<Result<TimeBoundaryResultValue>> expectedResults = Collections.singletonList(new Result<TimeBoundaryResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-15T00:00:00.000Z")))));
        TimeBoundaryQuery query = org.apache.druid.query.Druids.newTimeBoundaryQueryBuilder().dataSource(dataSource).build();
        QueryRunner runner = TestQueryRunners.makeTimeBoundaryQueryRunner(segment2);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testTimeSeries() {
        List<Result<TimeseriesResultValue>> expectedResults = Collections.singletonList(new Result<TimeseriesResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 8L).put("index", 700.0).put("addRowsIndexConstant", 709.0).put("uniques", 1.0002442201269182).put("maxIndex", 100.0).put("minIndex", 0.0).build())));
        TimeseriesQuery query = makeTimeseriesQuery();
        QueryRunner runner = TestQueryRunners.makeTimeSeriesQueryRunner(segment);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testTimeSeries2() {
        List<Result<TimeseriesResultValue>> expectedResults = Collections.singletonList(new Result<TimeseriesResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 7L).put("index", 500.0).put("addRowsIndexConstant", 508.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 0.0).build())));
        TimeseriesQuery query = makeTimeseriesQuery();
        QueryRunner runner = TestQueryRunners.makeTimeSeriesQueryRunner(segment2);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testFilteredTimeSeries() {
        List<Result<TimeseriesResultValue>> expectedResults = Collections.singletonList(new Result<TimeseriesResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 5L).put("index", 500.0).put("addRowsIndexConstant", 506.0).put("uniques", 1.0002442201269182).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        TimeseriesQuery query = makeFilteredTimeseriesQuery();
        QueryRunner runner = TestQueryRunners.makeTimeSeriesQueryRunner(segment);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testFilteredTimeSeries2() {
        List<Result<TimeseriesResultValue>> expectedResults = Collections.singletonList(new Result<TimeseriesResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 4L).put("index", 400.0).put("addRowsIndexConstant", 405.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        TimeseriesQuery query = makeFilteredTimeseriesQuery();
        QueryRunner runner = TestQueryRunners.makeTimeSeriesQueryRunner(segment2);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testTopNSeries() {
        List<Result<TopNResultValue>> expectedResults = Collections.singletonList(new Result<TopNResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.asList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 3L).put("index", 300.0).put("addRowsIndexConstant", 304.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build(), QueryRunnerTestHelper.orderedMap("market", null, "rows", 3L, "index", 200.0, "addRowsIndexConstant", 204.0, "uniques", 0.0, "maxIndex", 100.0, "minIndex", 0.0), ImmutableMap.<String, Object>builder().put("market", "total_market").put("rows", 2L).put("index", 200.0).put("addRowsIndexConstant", 203.0).put("uniques", 1.0002442201269182).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        TopNQuery query = makeTopNQuery();
        try (CloseableStupidPool<ByteBuffer> pool = TestQueryRunners.createDefaultNonBlockingPool()) {
            QueryRunner runner = TestQueryRunners.makeTopNQueryRunner(segment, pool);
            HashMap<String, Object> context = new HashMap<String, Object>();
            TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
        }
    }

    @Test
    public void testTopNSeries2() {
        List<Result<TopNResultValue>> expectedResults = Collections.singletonList(new Result<TopNResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.asList(ImmutableMap.<String, Object>builder().put("market", "total_market").put("rows", 3L).put("index", 300.0).put("addRowsIndexConstant", 304.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build(), QueryRunnerTestHelper.orderedMap("market", null, "rows", 3L, "index", 100.0, "addRowsIndexConstant", 104.0, "uniques", 0.0, "maxIndex", 100.0, "minIndex", 0.0), ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        TopNQuery query = makeTopNQuery();
        try (CloseableStupidPool<ByteBuffer> pool = TestQueryRunners.createDefaultNonBlockingPool()) {
            QueryRunner runner = TestQueryRunners.makeTopNQueryRunner(segment2, pool);
            HashMap<String, Object> context = new HashMap<String, Object>();
            TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
        }
    }

    @Test
    public void testFilteredTopNSeries() {
        List<Result<TopNResultValue>> expectedResults = Collections.singletonList(new Result<TopNResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Collections.<Map<String, Object>>singletonList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        TopNQuery query = makeFilteredTopNQuery();
        try (CloseableStupidPool<ByteBuffer> pool = TestQueryRunners.createDefaultNonBlockingPool()) {
            QueryRunner runner = TestQueryRunners.makeTopNQueryRunner(segment, pool);
            HashMap<String, Object> context = new HashMap<String, Object>();
            TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
        }
    }

    @Test
    public void testFilteredTopNSeries2() {
        List<Result<TopNResultValue>> expectedResults = Collections.singletonList(new Result<TopNResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(new ArrayList<Map<String, Object>>())));
        TopNQuery query = makeFilteredTopNQuery();
        try (CloseableStupidPool<ByteBuffer> pool = TestQueryRunners.createDefaultNonBlockingPool()) {
            QueryRunner runner = TestQueryRunners.makeTopNQueryRunner(segment2, pool);
            HashMap<String, Object> context = new HashMap<String, Object>();
            TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
        }
    }

    @Test
    public void testSearch() {
        List<Result<SearchResultValue>> expectedResults = Collections.singletonList(new Result<SearchResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(qualityDimension, "automotive"), new SearchHit(placementDimension, "mezzanine"), new SearchHit(marketDimension, "total_market")))));
        SearchQuery query = makeSearchQuery();
        QueryRunner runner = TestQueryRunners.makeSearchQueryRunner(segment);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testSearchWithOverlap() {
        List<Result<SearchResultValue>> expectedResults = Collections.singletonList(new Result<SearchResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(placementDimension, "mezzanine"), new SearchHit(marketDimension, "total_market")))));
        SearchQuery query = makeSearchQuery();
        QueryRunner runner = TestQueryRunners.makeSearchQueryRunner(segment2);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testFilteredSearch() {
        List<Result<SearchResultValue>> expectedResults = Collections.singletonList(new Result<SearchResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementDimension, "mezzanine"), new SearchHit(marketDimension, "total_market")))));
        SearchQuery query = makeFilteredSearchQuery();
        QueryRunner runner = TestQueryRunners.makeSearchQueryRunner(segment);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testFilteredSearch2() {
        List<Result<SearchResultValue>> expectedResults = Collections.singletonList(new Result<SearchResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(placementDimension, "mezzanine"), new SearchHit(marketDimension, "total_market")))));
        SearchQuery query = makeFilteredSearchQuery();
        QueryRunner runner = TestQueryRunners.makeSearchQueryRunner(segment2);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testRowFiltering() {
        List<Result<TimeseriesResultValue>> expectedResults = Collections.singletonList(new Result<TimeseriesResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 5L).put("index", 500.0).put("addRowsIndexConstant", 506.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        TimeseriesQuery query = org.apache.druid.query.Druids.newTimeseriesQueryBuilder().dataSource(dataSource).granularity(allGran).intervals(fullOnInterval).filters(marketDimension, "breakstuff").aggregators(Lists.newArrayList(Iterables.concat(commonAggregators, Lists.newArrayList(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index"))))).postAggregators(addRowsIndexConstant).build();
        QueryRunner runner = TestQueryRunners.makeTimeSeriesQueryRunner(segment3);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }
}

