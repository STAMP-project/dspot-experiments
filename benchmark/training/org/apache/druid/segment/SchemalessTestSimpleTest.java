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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.java.util.common.granularity.Granularity;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class SchemalessTestSimpleTest {
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

    private final Segment segment;

    private final boolean coalesceAbsentAndEmptyDims;

    public SchemalessTestSimpleTest(Segment segment, boolean coalesceAbsentAndEmptyDims) {
        this.segment = segment;
        // Empty and empty dims are equivalent only when replaceWithDefault is true
        this.coalesceAbsentAndEmptyDims = coalesceAbsentAndEmptyDims && (NullHandling.replaceWithDefault());
    }

    @Test
    public void testFullOnTimeseries() {
        TimeseriesQuery query = org.apache.druid.query.Druids.newTimeseriesQueryBuilder().dataSource(dataSource).granularity(allGran).intervals(fullOnInterval).aggregators(Lists.newArrayList(Iterables.concat(commonAggregators, Lists.newArrayList(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index"))))).postAggregators(addRowsIndexConstant).build();
        List<Result<TimeseriesResultValue>> expectedResults = Collections.singletonList(new Result(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeseriesResultValue(ImmutableMap.<String, Object>builder().put("rows", (coalesceAbsentAndEmptyDims ? 10L : 11L)).put("index", 900.0).put("addRowsIndexConstant", (coalesceAbsentAndEmptyDims ? 911.0 : 912.0)).put("uniques", 2.000977198748901).put("maxIndex", 100.0).put("minIndex", (NullHandling.replaceWithDefault() ? 0.0 : 100.0)).build())));
        QueryRunner runner = TestQueryRunners.makeTimeSeriesQueryRunner(segment);
        HashMap<String, Object> context = new HashMap();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testFullOnSearch() {
        SearchQuery query = org.apache.druid.query.Druids.newSearchQueryBuilder().dataSource(dataSource).granularity(allGran).intervals(fullOnInterval).query("a").build();
        List<Result<SearchResultValue>> expectedResults = Collections.singletonList(new Result<SearchResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SearchResultValue(Arrays.asList(new SearchHit(placementishDimension, "a"), new SearchHit(qualityDimension, "automotive"), new SearchHit(placementDimension, "mezzanine"), new SearchHit(marketDimension, "total_market")))));
        QueryRunner runner = TestQueryRunners.makeSearchQueryRunner(segment);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testTimeBoundary() {
        TimeBoundaryQuery query = org.apache.druid.query.Druids.newTimeBoundaryQueryBuilder().dataSource("testing").build();
        List<Result<TimeBoundaryResultValue>> expectedResults = Collections.singletonList(new Result<TimeBoundaryResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TimeBoundaryResultValue(ImmutableMap.of(MIN_TIME, DateTimes.of("2011-01-12T00:00:00.000Z"), MAX_TIME, DateTimes.of("2011-01-13T00:00:00.000Z")))));
        QueryRunner runner = TestQueryRunners.makeTimeBoundaryQueryRunner(segment);
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }
}

