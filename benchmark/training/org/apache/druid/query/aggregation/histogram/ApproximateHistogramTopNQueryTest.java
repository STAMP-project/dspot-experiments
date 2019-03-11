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
package org.apache.druid.query.aggregation.histogram;


import QueryRunnerTestHelper.UNIQUES_2;
import QueryRunnerTestHelper.UNIQUES_9;
import QueryRunnerTestHelper.addRowsIndexConstant;
import QueryRunnerTestHelper.allGran;
import QueryRunnerTestHelper.commonDoubleAggregators;
import QueryRunnerTestHelper.dataSource;
import QueryRunnerTestHelper.dependentPostAgg;
import QueryRunnerTestHelper.dependentPostAggMetric;
import QueryRunnerTestHelper.fullOnIntervalSpec;
import QueryRunnerTestHelper.marketDimension;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.Result;
import org.apache.druid.query.aggregation.DoubleMaxAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleMinAggregatorFactory;
import org.apache.druid.query.topn.TopNQuery;
import org.apache.druid.query.topn.TopNQueryBuilder;
import org.apache.druid.query.topn.TopNResultValue;
import org.apache.druid.segment.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ApproximateHistogramTopNQueryTest {
    private static final Closer resourceCloser = Closer.create();

    private final QueryRunner runner;

    public ApproximateHistogramTopNQueryTest(QueryRunner runner) {
        this.runner = runner;
    }

    @Test
    public void testTopNWithApproximateHistogramAgg() {
        ApproximateHistogramAggregatorFactory factory = new ApproximateHistogramAggregatorFactory("apphisto", "index", 10, 5, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        TopNQuery query = new TopNQueryBuilder().dataSource(dataSource).granularity(allGran).dimension(marketDimension).metric(dependentPostAggMetric).threshold(4).intervals(fullOnIntervalSpec).aggregators(Lists.newArrayList(Iterables.concat(commonDoubleAggregators, Lists.newArrayList(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index"), factory)))).postAggregators(Arrays.asList(addRowsIndexConstant, dependentPostAgg, new QuantilePostAggregator("quantile", "apphisto", 0.5F))).build();
        List<Result<TopNResultValue>> expectedResults = Collections.singletonList(new Result<TopNResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Arrays.<Map<String, Object>>asList(ImmutableMap.<String, Object>builder().put(marketDimension, "total_market").put("rows", 186L).put("index", 215679.82879638672).put("addRowsIndexConstant", 215866.82879638672).put(dependentPostAggMetric, 216053.82879638672).put("uniques", UNIQUES_2).put("maxIndex", 1743.9217529296875).put("minIndex", 792.3260498046875).put("quantile", 1085.6775F).put("apphisto", new Histogram(new float[]{ 554.4271F, 792.32605F, 1030.225F, 1268.1239F, 1506.0228F, 1743.9218F }, new double[]{ 0.0, 39.42073059082031, 103.29110717773438, 34.93659591674805, 8.351564407348633 })).build(), ImmutableMap.<String, Object>builder().put(marketDimension, "upfront").put("rows", 186L).put("index", 192046.1060180664).put("addRowsIndexConstant", 192233.1060180664).put(dependentPostAggMetric, 192420.1060180664).put("uniques", UNIQUES_2).put("maxIndex", 1870.06103515625).put("minIndex", 545.9906005859375).put("quantile", 880.9881F).put("apphisto", new Histogram(new float[]{ 214.97299F, 545.9906F, 877.0082F, 1208.0258F, 1539.0433F, 1870.061F }, new double[]{ 0.0, 67.53287506103516, 72.22068786621094, 31.984678268432617, 14.261756896972656 })).build(), ImmutableMap.<String, Object>builder().put(marketDimension, "spot").put("rows", 837L).put("index", 95606.57232284546).put("addRowsIndexConstant", 96444.57232284546).put(dependentPostAggMetric, 97282.57232284546).put("uniques", UNIQUES_9).put("maxIndex", 277.2735290527344).put("minIndex", 59.02102279663086).put("quantile", 101.78856F).put("apphisto", new Histogram(new float[]{ 4.457897F, 59.021023F, 113.58415F, 168.14728F, 222.7104F, 277.27353F }, new double[]{ 0.0, 462.4309997558594, 357.5404968261719, 15.022850036621094, 2.0056631565093994 })).build()))));
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(expectedResults, runner.run(QueryPlus.wrap(query), context));
    }
}

