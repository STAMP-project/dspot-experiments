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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleSumAggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.aggregation.hyperloglog.HyperUniquesAggregatorFactory;
import org.apache.druid.query.expression.TestExprMacroTable;
import org.apache.druid.query.ordering.StringComparators;
import org.apache.druid.query.timeseries.TimeseriesQuery;
import org.apache.druid.query.timeseries.TimeseriesQueryRunnerFactory;
import org.apache.druid.segment.QueryableIndex;
import org.apache.druid.segment.TestHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests designed to exercise changing column types, adding columns, removing columns, etc.
 */
public class SchemaEvolutionTest {
    private static final String DATA_SOURCE = "foo";

    private static final String TIMESTAMP_COLUMN = "t";

    private static final double THIRTY_ONE_POINT_ONE = 31.1;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    // Index1: c1 is a string, c2 nonexistent, "uniques" nonexistent
    private QueryableIndex index1 = null;

    // Index2: c1 is a long, c2 is a string, "uniques" is uniques on c2
    private QueryableIndex index2 = null;

    // Index3: c1 is a float, c2 is a string, "uniques" is uniques on c2
    private QueryableIndex index3 = null;

    // Index4: c1 is nonexistent, c2 is uniques on c2
    private QueryableIndex index4 = null;

    @Test
    public void testHyperUniqueEvolutionTimeseries() {
        final TimeseriesQueryRunnerFactory factory = QueryRunnerTestHelper.newTimeseriesQueryRunnerFactory();
        final TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource(SchemaEvolutionTest.DATA_SOURCE).intervals("1000/3000").aggregators(ImmutableList.of(new HyperUniquesAggregatorFactory("uniques", "uniques"))).build();
        // index1 has no "uniques" column
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(ImmutableMap.of("uniques", 0)), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index1)));
        // index1 (no uniques) + index2 and index3 (yes uniques); we should be able to combine
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(ImmutableMap.of("uniques", 4.003911343725148)), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index1, index2, index3)));
    }

    @Test
    public void testNumericEvolutionTimeseriesAggregation() {
        final TimeseriesQueryRunnerFactory factory = QueryRunnerTestHelper.newTimeseriesQueryRunnerFactory();
        // "c1" changes from string(1) -> long(2) -> float(3) -> nonexistent(4)
        // test behavior of longSum/doubleSum with/without expressions
        final TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource(SchemaEvolutionTest.DATA_SOURCE).intervals("1000/3000").aggregators(ImmutableList.of(new LongSumAggregatorFactory("a", "c1"), new DoubleSumAggregatorFactory("b", "c1"), new LongSumAggregatorFactory("c", null, "c1 * 1", TestExprMacroTable.INSTANCE), new DoubleSumAggregatorFactory("d", null, "c1 * 1", TestExprMacroTable.INSTANCE))).build();
        // Only string(1)
        // Note: Expressions implicitly cast strings to numbers, leading to the a/b vs c/d difference.
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(ImmutableMap.of("a", 0L, "b", 0.0, "c", 31L, "d", SchemaEvolutionTest.THIRTY_ONE_POINT_ONE)), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index1)));
        // Only long(2)
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(ImmutableMap.of("a", 31L, "b", 31.0, "c", 31L, "d", 31.0)), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index2)));
        // Only float(3)
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(ImmutableMap.of("a", 31L, "b", SchemaEvolutionTest.THIRTY_ONE_POINT_ONE, "c", 31L, "d", SchemaEvolutionTest.THIRTY_ONE_POINT_ONE)), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index3)));
        // Only nonexistent(4)
        Map<String, Object> result = new HashMap<>();
        result.put("a", NullHandling.defaultLongValue());
        result.put("b", NullHandling.defaultDoubleValue());
        result.put("c", NullHandling.defaultLongValue());
        result.put("d", NullHandling.defaultDoubleValue());
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(result), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index4)));
        // string(1) + long(2) + float(3) + nonexistent(4)
        // Note: Expressions implicitly cast strings to numbers, leading to the a/b vs c/d difference.
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(ImmutableMap.of("a", (31L * 2), "b", ((SchemaEvolutionTest.THIRTY_ONE_POINT_ONE) + 31), "c", (31L * 3), "d", (((SchemaEvolutionTest.THIRTY_ONE_POINT_ONE) * 2) + 31))), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index1, index2, index3, index4)));
        // long(2) + float(3) + nonexistent(4)
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(ImmutableMap.of("a", (31L * 2), "b", ((SchemaEvolutionTest.THIRTY_ONE_POINT_ONE) + 31), "c", (31L * 2), "d", ((SchemaEvolutionTest.THIRTY_ONE_POINT_ONE) + 31))), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index2, index3, index4)));
    }

    @Test
    public void testNumericEvolutionFiltering() {
        final TimeseriesQueryRunnerFactory factory = QueryRunnerTestHelper.newTimeseriesQueryRunnerFactory();
        // "c1" changes from string(1) -> long(2) -> float(3) -> nonexistent(4)
        // test behavior of filtering
        final TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource(SchemaEvolutionTest.DATA_SOURCE).intervals("1000/3000").filters(new org.apache.druid.query.filter.BoundDimFilter("c1", "9", "11", false, false, null, null, StringComparators.NUMERIC)).aggregators(ImmutableList.of(new LongSumAggregatorFactory("a", "c1"), new DoubleSumAggregatorFactory("b", "c1"), new CountAggregatorFactory("c"))).build();
        // Only string(1) -- which we can filter but not aggregate
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(ImmutableMap.of("a", 0L, "b", 0.0, "c", 2L)), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index1)));
        // Only long(2) -- which we can filter and aggregate
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(ImmutableMap.of("a", 19L, "b", 19.0, "c", 2L)), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index2)));
        // Only float(3) -- which we can't filter, but can aggregate
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(ImmutableMap.of("a", 19L, "b", 19.1, "c", 2L)), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index3)));
        // Only nonexistent(4)
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(TestHelper.createExpectedMap("a", NullHandling.defaultLongValue(), "b", NullHandling.defaultDoubleValue(), "c", 0L)), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index4)));
        // string(1) + long(2) + float(3) + nonexistent(4)
        Assert.assertEquals(SchemaEvolutionTest.timeseriesResult(ImmutableMap.of("a", 38L, "b", 38.1, "c", 6L)), SchemaEvolutionTest.runQuery(query, factory, ImmutableList.of(index1, index2, index3, index4)));
    }
}

