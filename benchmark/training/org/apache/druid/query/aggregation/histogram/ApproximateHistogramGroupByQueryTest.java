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


import OrderByColumnSpec.Direction;
import QueryRunnerTestHelper.allGran;
import QueryRunnerTestHelper.dataSource;
import QueryRunnerTestHelper.fullOnIntervalSpec;
import QueryRunnerTestHelper.rowsCount;
import java.util.Collections;
import java.util.List;
import org.apache.druid.data.input.Row;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.groupby.GroupByQuery;
import org.apache.druid.query.groupby.GroupByQueryRunnerFactory;
import org.apache.druid.query.groupby.GroupByQueryRunnerTestHelper;
import org.apache.druid.segment.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class ApproximateHistogramGroupByQueryTest {
    private static final Closer resourceCloser = Closer.create();

    private final QueryRunner<Row> runner;

    private final GroupByQueryRunnerFactory factory;

    public ApproximateHistogramGroupByQueryTest(String testName, GroupByQueryRunnerFactory factory, QueryRunner runner) {
        this.factory = factory;
        this.runner = runner;
        ApproximateHistogramDruidModule.registerSerde();
    }

    @Test
    public void testGroupByWithApproximateHistogramAgg() {
        ApproximateHistogramAggregatorFactory aggFactory = new ApproximateHistogramAggregatorFactory("apphisto", "index", 10, 5, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        GroupByQuery query = new GroupByQuery.Builder().setDataSource(dataSource).setGranularity(allGran).setDimensions(new org.apache.druid.query.dimension.DefaultDimensionSpec(QueryRunnerTestHelper.marketDimension, "marketalias")).setInterval(fullOnIntervalSpec).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(Collections.singletonList(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("marketalias", Direction.DESCENDING)), 1)).setAggregatorSpecs(rowsCount, aggFactory).setPostAggregatorSpecs(Collections.singletonList(new QuantilePostAggregator("quantile", "apphisto", 0.5F))).build();
        List<Row> expectedResults = Collections.singletonList(GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "marketalias", "upfront", "rows", 186L, "quantile", 880.9881F, "apphisto", new Histogram(new float[]{ 214.97299F, 545.9906F, 877.0082F, 1208.0258F, 1539.0433F, 1870.061F }, new double[]{ 0.0, 67.53287506103516, 72.22068786621094, 31.984678268432617, 14.261756896972656 })));
        Iterable<Row> results = GroupByQueryRunnerTestHelper.runQuery(factory, runner, query);
        TestHelper.assertExpectedObjects(expectedResults, results, "approx-histo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGroupByWithSameNameComplexPostAgg() {
        ApproximateHistogramAggregatorFactory aggFactory = new ApproximateHistogramAggregatorFactory("quantile", "index", 10, 5, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
        GroupByQuery query = new GroupByQuery.Builder().setDataSource(dataSource).setGranularity(allGran).setDimensions(new org.apache.druid.query.dimension.DefaultDimensionSpec(QueryRunnerTestHelper.marketDimension, "marketalias")).setInterval(fullOnIntervalSpec).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(Collections.singletonList(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("marketalias", Direction.DESCENDING)), 1)).setAggregatorSpecs(rowsCount, aggFactory).setPostAggregatorSpecs(Collections.singletonList(new QuantilePostAggregator("quantile", "quantile", 0.5F))).build();
        List<Row> expectedResults = Collections.singletonList(GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "marketalias", "upfront", "rows", 186L, "quantile", 880.9881F));
        Iterable<Row> results = GroupByQueryRunnerTestHelper.runQuery(factory, runner, query);
        TestHelper.assertExpectedObjects(expectedResults, results, "approx-histo");
    }
}

