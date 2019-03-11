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
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.java.util.common.granularity.Granularity;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.Result;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleSumAggregatorFactory;
import org.apache.druid.query.aggregation.hyperloglog.HyperUniquesAggregatorFactory;
import org.apache.druid.query.aggregation.post.ArithmeticPostAggregator;
import org.apache.druid.query.aggregation.post.ConstantPostAggregator;
import org.apache.druid.query.aggregation.post.FieldAccessPostAggregator;
import org.apache.druid.query.search.SearchHit;
import org.apache.druid.query.search.SearchResultValue;
import org.apache.druid.query.spec.QuerySegmentSpec;
import org.apache.druid.query.timeboundary.TimeBoundaryResultValue;
import org.apache.druid.query.timeseries.TimeseriesResultValue;
import org.apache.druid.query.topn.TopNResultValue;
import org.apache.druid.segment.writeout.SegmentWriteOutMediumFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class SchemalessTestFullTest {
    final double UNIQUES_2 = 2.000977198748901;

    final double UNIQUES_1 = 1.0002442201269182;

    final SchemalessIndexTest schemalessIndexTest;

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

    public SchemalessTestFullTest(SegmentWriteOutMediumFactory segmentWriteOutMediumFactory) {
        schemalessIndexTest = new SchemalessIndexTest(segmentWriteOutMediumFactory);
    }

    @Test
    public void testCompleteIntersectingSchemas() {
        List<Result<TimeseriesResultValue>> expectedTimeSeriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 2L).put("index", 200.0).put("addRowsIndexConstant", 203.0).put("uniques", UNIQUES_2).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        List<Result<TimeseriesResultValue>> expectedFilteredTimeSeriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        List<Result<TopNResultValue>> expectedTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.<Map<String, Object>>asList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build(), ImmutableMap.<String, Object>builder().put("market", "total_market").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<TopNResultValue>> expectedFilteredTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Collections.<Map<String, Object>>singletonList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<SearchResultValue>> expectedSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(qualityDimension, "automotive"), new SearchHit(placementDimension, "mezzanine"), new SearchHit(marketDimension, "total_market")))));
        List<Result<SearchResultValue>> expectedFilteredSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(qualityDimension, "automotive")))));
        List<Result<TimeBoundaryResultValue>> expectedTimeBoundaryResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-13T00:00:00.000Z")))));
        testAll(1, 5, expectedTimeSeriesResults, expectedFilteredTimeSeriesResults, expectedTopNResults, expectedFilteredTopNResults, expectedSearchResults, expectedFilteredSearchResults, expectedTimeBoundaryResults);
    }

    @Test
    public void testEmptyStrings() {
        List<Result<TimeseriesResultValue>> expectedTimeSeriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 2L).put("index", 200.0).put("addRowsIndexConstant", 203.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        List<Result<TimeseriesResultValue>> expectedFilteredTimeSeriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        List<Result<TopNResultValue>> expectedTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.<Map<String, Object>>asList(ImmutableMap.<String, Object>builder().put("market", "").put("rows", 2L).put("index", 200.0).put("addRowsIndexConstant", 203.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build(), ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<TopNResultValue>> expectedFilteredTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.<Map<String, Object>>asList(ImmutableMap.<String, Object>builder().put("market", "").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build(), ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<SearchResultValue>> expectedSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Collections.emptyList())));
        List<Result<SearchResultValue>> expectedFilteredSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Collections.emptyList())));
        List<Result<TimeBoundaryResultValue>> expectedTimeBoundaryResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-12T00:00:00.000Z")))));
        testAll(9, 10, expectedTimeSeriesResults, expectedFilteredTimeSeriesResults, expectedTopNResults, expectedFilteredTopNResults, expectedSearchResults, expectedFilteredSearchResults, expectedTimeBoundaryResults);
    }

    @Test
    public void testNonIntersectingSchemas() {
        List<Result<TimeseriesResultValue>> expectedTimeseriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 2L).put("index", 100.0).put("addRowsIndexConstant", 103.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", (NullHandling.replaceWithDefault() ? 0.0 : 100.0)).build())));
        List<Result<TimeseriesResultValue>> expectedFilteredTimeSeriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        List<Result<TopNResultValue>> expectedTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.asList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build(), QueryRunnerTestHelper.orderedMap("market", null, "rows", 1L, "index", 0.0, "addRowsIndexConstant", 2.0, "uniques", 0.0, "maxIndex", 0.0, "minIndex", 0.0)))));
        List<Result<TopNResultValue>> expectedFilteredTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Collections.<Map<String, Object>>singletonList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<SearchResultValue>> expectedSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(qualityDimension, "automotive")))));
        List<Result<SearchResultValue>> expectedFilteredSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Collections.singletonList(new SearchHit(qualityDimension, "automotive")))));
        List<Result<TimeBoundaryResultValue>> expectedTimeBoundaryResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-12T00:00:00.000Z")))));
        testAll(2, 3, expectedTimeseriesResults, expectedFilteredTimeSeriesResults, expectedTopNResults, expectedFilteredTopNResults, expectedSearchResults, expectedFilteredSearchResults, expectedTimeBoundaryResults);
    }

    @Test
    public void testPartialIntersectingSchemas() {
        List<Result<TimeseriesResultValue>> expectedTimeseriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 2L).put("index", 200.0).put("addRowsIndexConstant", 203.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        List<Result<TimeseriesResultValue>> expectedFilteredTimeSeriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        List<Result<TopNResultValue>> expectedTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.<Map<String, Object>>asList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build(), ImmutableMap.<String, Object>builder().put("market", "total_market").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<TopNResultValue>> expectedFilteredTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Collections.<Map<String, Object>>singletonList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<SearchResultValue>> expectedSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(qualityDimension, "automotive"), new SearchHit(marketDimension, "total_market")))));
        List<Result<SearchResultValue>> expectedFilteredSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Collections.singletonList(new SearchHit(qualityDimension, "automotive")))));
        List<Result<TimeBoundaryResultValue>> expectedTimeBoundaryResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-13T00:00:00.000Z")))));
        testAll(2, 4, expectedTimeseriesResults, expectedFilteredTimeSeriesResults, expectedTopNResults, expectedFilteredTopNResults, expectedSearchResults, expectedFilteredSearchResults, expectedTimeBoundaryResults);
    }

    @Test
    public void testSupersetSchemas() {
        List<Result<TimeseriesResultValue>> expectedTimeseriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 2L).put("index", 200.0).put("addRowsIndexConstant", 203.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        List<Result<TimeseriesResultValue>> expectedFilteredTimeSeriesResults = expectedTimeseriesResults;
        List<Result<TopNResultValue>> expectedTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Collections.<Map<String, Object>>singletonList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 2L).put("index", 200.0).put("addRowsIndexConstant", 203.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<TopNResultValue>> expectedFilteredTopNResults = expectedTopNResults;
        List<Result<SearchResultValue>> expectedSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(qualityDimension, "automotive")))));
        List<Result<SearchResultValue>> expectedFilteredSearchResults = expectedSearchResults;
        List<Result<TimeBoundaryResultValue>> expectedTimeBoundaryResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-12T00:00:00.000Z")))));
        testAll(1, 2, expectedTimeseriesResults, expectedFilteredTimeSeriesResults, expectedTopNResults, expectedFilteredTopNResults, expectedSearchResults, expectedFilteredSearchResults, expectedTimeBoundaryResults);
    }

    @Test
    public void testValueAndEmptySchemas() {
        List<Result<TimeseriesResultValue>> expectedTimeseriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 2L).put("index", 100.0).put("addRowsIndexConstant", 103.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", (NullHandling.replaceWithDefault() ? 0.0 : 100.0)).build())));
        List<Result<TimeseriesResultValue>> expectedFilteredTimeSeriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        List<Result<TopNResultValue>> expectedTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.asList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build(), QueryRunnerTestHelper.orderedMap("market", null, "rows", 1L, "index", 0.0, "addRowsIndexConstant", 2.0, "uniques", 0.0, "maxIndex", 0.0, "minIndex", 0.0)))));
        List<Result<TopNResultValue>> expectedFilteredTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Collections.<Map<String, Object>>singletonList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<SearchResultValue>> expectedSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(qualityDimension, "automotive")))));
        List<Result<SearchResultValue>> expectedFilteredSearchResults = expectedSearchResults;
        List<Result<TimeBoundaryResultValue>> expectedTimeBoundaryResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-12T00:00:00.000Z")))));
        testAll(0, 1, expectedTimeseriesResults, expectedFilteredTimeSeriesResults, expectedTopNResults, expectedFilteredTopNResults, expectedSearchResults, expectedFilteredSearchResults, expectedTimeBoundaryResults);
    }

    @Test
    public void testEmptySchemas() {
        List<Result<TimeseriesResultValue>> expectedTimeseriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(TestHelper.createExpectedMap("rows", 1L, "index", (NullHandling.replaceWithDefault() ? 0.0 : null), "addRowsIndexConstant", (NullHandling.replaceWithDefault() ? 2.0 : null), "uniques", 0.0, "maxIndex", (NullHandling.replaceWithDefault() ? 0.0 : null), "minIndex", (NullHandling.replaceWithDefault() ? 0.0 : null)))));
        List<Result<TimeseriesResultValue>> expectedFilteredTimeSeriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(TestHelper.createExpectedMap("rows", 0L, "index", (NullHandling.replaceWithDefault() ? 0.0 : null), "addRowsIndexConstant", (NullHandling.replaceWithDefault() ? 1.0 : null), "uniques", 0.0, "maxIndex", (NullHandling.replaceWithDefault() ? Double.NEGATIVE_INFINITY : null), "minIndex", (NullHandling.replaceWithDefault() ? Double.POSITIVE_INFINITY : null)))));
        List<Result<TopNResultValue>> expectedTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Collections.singletonList(QueryRunnerTestHelper.orderedMap("market", null, "rows", 1L, "index", 0.0, "addRowsIndexConstant", 2.0, "uniques", 0.0, "maxIndex", 0.0, "minIndex", 0.0)))));
        List<Result<TopNResultValue>> expectedFilteredTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Collections.<SearchHit>emptyList())));
        List<Result<SearchResultValue>> expectedSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Collections.emptyList())));
        List<Result<SearchResultValue>> expectedFilteredSearchResults = expectedSearchResults;
        List<Result<TimeBoundaryResultValue>> expectedTimeBoundaryResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-12T00:00:00.000Z")))));
        runTests(new QueryableIndexSegment(schemalessIndexTest.getMergedIncrementalIndex(0, 0), null), expectedTimeseriesResults, expectedFilteredTimeSeriesResults, expectedTopNResults, expectedFilteredTopNResults, expectedSearchResults, expectedFilteredSearchResults, expectedTimeBoundaryResults, "Failed: MII[0, 0]");
    }

    @Test
    public void testExactSameSchemas() {
        List<Result<TimeseriesResultValue>> expectedTimeseriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("index", 200.0).put("addRowsIndexConstant", 202.0).put("uniques", UNIQUES_1).put("maxIndex", 200.0).put("minIndex", 200.0).build())));
        List<Result<TimeseriesResultValue>> expectedFilteredTimeSeriesResults = expectedTimeseriesResults;
        List<Result<TopNResultValue>> expectedTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Collections.<Map<String, Object>>singletonList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 200.0).put("addRowsIndexConstant", 202.0).put("uniques", UNIQUES_1).put("maxIndex", 200.0).put("minIndex", 200.0).build()))));
        List<Result<TopNResultValue>> expectedFilteredTopNResults = expectedTopNResults;
        List<Result<SearchResultValue>> expectedSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(qualityDimension, "automotive")))));
        List<Result<SearchResultValue>> expectedFilteredSearchResults = expectedSearchResults;
        List<Result<TimeBoundaryResultValue>> expectedTimeBoundaryResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-12T00:00:00.000Z")))));
        runTests(new QueryableIndexSegment(schemalessIndexTest.getMergedIncrementalIndex(1, 1), null), expectedTimeseriesResults, expectedFilteredTimeSeriesResults, expectedTopNResults, expectedFilteredTopNResults, expectedSearchResults, expectedFilteredSearchResults, expectedTimeBoundaryResults, "Failed: MII[1, 1]");
    }

    @Test
    public void testMultiDimensionalValues() {
        List<Result<TimeseriesResultValue>> expectedTimeseriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 3L).put("index", 300.0).put("addRowsIndexConstant", 304.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        List<Result<TimeseriesResultValue>> expectedFilteredTimeSeriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        List<Result<TopNResultValue>> expectedTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.asList(QueryRunnerTestHelper.orderedMap("market", null, "rows", 2L, "index", 200.0, "addRowsIndexConstant", 203.0, "uniques", 0.0, "maxIndex", 100.0, "minIndex", 100.0), ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<TopNResultValue>> expectedFilteredTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Collections.<Map<String, Object>>singletonList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<SearchResultValue>> expectedSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Collections.singletonList(new SearchHit(placementDimension, "mezzanine")))));
        List<Result<SearchResultValue>> expectedFilteredSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Collections.emptyList())));
        List<Result<TimeBoundaryResultValue>> expectedTimeBoundaryResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-12T00:00:00.000Z")))));
        runTests(new QueryableIndexSegment(schemalessIndexTest.getMergedIncrementalIndex(new int[]{ 6, 7, 8 }), null), expectedTimeseriesResults, expectedFilteredTimeSeriesResults, expectedTopNResults, expectedFilteredTopNResults, expectedSearchResults, expectedFilteredSearchResults, expectedTimeBoundaryResults, "Failed: MII[6, 7]");
    }

    @Test
    public void testDifferentMetrics() {
        List<Result<TimeseriesResultValue>> expectedTimeseriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", (NullHandling.sqlCompatible() ? 11L : 10L)).put("index", 900.0).put("addRowsIndexConstant", (NullHandling.sqlCompatible() ? 912.0 : 911.0)).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", (NullHandling.replaceWithDefault() ? 0.0 : 100.0)).build())));
        List<Result<TimeseriesResultValue>> expectedFilteredTimeSeriesResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", 4L).put("index", 400.0).put("addRowsIndexConstant", 405.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build())));
        /* Uncomment when Druid support for nulls/empty strings is actually consistent
        List<Result<TopNResultValue>> expectedTopNResults = Arrays.asList(
        new Result<TopNResultValue>(
        DateTimes.of("2011-01-12T00:00:00.000Z"),
        new TopNResultValue(
        Arrays.<Map<String, Object>>asList(
        ImmutableMap.<String, Object>builder()
        .put("market", "")
        .put("rows", 6L)
        .put("index", 400.0D)
        .put("addRowsIndexConstant", 407.0D)
        .put("uniques", 0.0)
        .put("maxIndex", 100.0)
        .put("minIndex", 0.0)
        .build(),
        ImmutableMap.<String, Object>builder()
        .put("market", "spot")
        .put("rows", 4L)
        .put("index", 400.0D)
        .put("addRowsIndexConstant", 405.0D)
        .put("uniques", 0.0D)
        .put("maxIndex", 100.0)
        .put("minIndex", 100.0)
        .build(),
        ImmutableMap.<String, Object>builder()
        .put("market", "total_market")
        .put("rows", 2L)
        .put("index", 200.0D)
        .put("addRowsIndexConstant", 203.0D)
        .put("uniques", UNIQUES_1)
        .put("maxIndex", 100.0)
        .put("minIndex", 100.0)
        .build()
        )
        )
        )
        );
         */
        List<Result<TopNResultValue>> expectedTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.<Map<String, Object>>asList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 4L).put("index", 400.0).put("addRowsIndexConstant", 405.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build(), ImmutableMap.<String, Object>builder().put("market", "").put("rows", 3L).put("index", 200.0).put("addRowsIndexConstant", 204.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 0.0).build(), ImmutableMap.<String, Object>builder().put("market", "total_market").put("rows", 2L).put("index", 200.0).put("addRowsIndexConstant", 203.0).put("uniques", UNIQUES_1).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<TopNResultValue>> expectedFilteredTopNResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.<Map<String, Object>>asList(ImmutableMap.<String, Object>builder().put("market", "spot").put("rows", 4L).put("index", 400.0).put("addRowsIndexConstant", 405.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build(), ImmutableMap.<String, Object>builder().put("market", "").put("rows", 1L).put("index", 100.0).put("addRowsIndexConstant", 102.0).put("uniques", 0.0).put("maxIndex", 100.0).put("minIndex", 100.0).build()))));
        List<Result<SearchResultValue>> expectedSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(qualityDimension, "automotive"), new SearchHit(placementDimension, "mezzanine"), new SearchHit(marketDimension, "total_market")))));
        List<Result<SearchResultValue>> expectedFilteredSearchResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(qualityDimension, "automotive")))));
        List<Result<TimeBoundaryResultValue>> expectedTimeBoundaryResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-13T00:00:00.000Z")))));
        runTests(new QueryableIndexSegment(schemalessIndexTest.getMergedIncrementalIndexDiffMetrics(), null), expectedTimeseriesResults, expectedFilteredTimeSeriesResults, expectedTopNResults, expectedFilteredTopNResults, expectedSearchResults, expectedFilteredSearchResults, expectedTimeBoundaryResults, "Failed: MIIDM");
    }
}

