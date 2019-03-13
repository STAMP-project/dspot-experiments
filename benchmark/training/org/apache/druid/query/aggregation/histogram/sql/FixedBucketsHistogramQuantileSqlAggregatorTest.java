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
package org.apache.druid.query.aggregation.histogram.sql;


import CalciteTests.DATASOURCE1;
import FixedBucketsHistogram.OutlierHandlingMode;
import Granularities.ALL;
import PlannerContext.CTX_SQL_QUERY_ID;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Map;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.Druids;
import org.apache.druid.query.Query;
import org.apache.druid.query.QueryRunnerFactoryConglomerate;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleSumAggregatorFactory;
import org.apache.druid.query.aggregation.FilteredAggregatorFactory;
import org.apache.druid.query.aggregation.histogram.QuantilePostAggregator;
import org.apache.druid.query.aggregation.post.FieldAccessPostAggregator;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.expression.TestExprMacroTable;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.query.groupby.GroupByQuery;
import org.apache.druid.segment.column.ValueType;
import org.apache.druid.server.security.AuthenticationResult;
import org.apache.druid.sql.SqlLifecycle;
import org.apache.druid.sql.SqlLifecycleFactory;
import org.apache.druid.sql.calcite.filtration.Filtration;
import org.apache.druid.sql.calcite.util.CalciteTestBase;
import org.apache.druid.sql.calcite.util.CalciteTests;
import org.apache.druid.sql.calcite.util.QueryLogHook;
import org.apache.druid.sql.calcite.util.SpecificSegmentsQuerySegmentWalker;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FixedBucketsHistogramQuantileSqlAggregatorTest extends CalciteTestBase {
    private static final String DATA_SOURCE = "foo";

    private static QueryRunnerFactoryConglomerate conglomerate;

    private static Closer resourceCloser;

    private static AuthenticationResult authenticationResult = CalciteTests.REGULAR_USER_AUTH_RESULT;

    private static final Map<String, Object> QUERY_CONTEXT_DEFAULT = ImmutableMap.of(CTX_SQL_QUERY_ID, "dummy");

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public QueryLogHook queryLogHook = QueryLogHook.create();

    private SpecificSegmentsQuerySegmentWalker walker;

    private SqlLifecycleFactory sqlLifecycleFactory;

    @Test
    public void testQuantileOnFloatAndLongs() throws Exception {
        SqlLifecycle sqlLifecycle = sqlLifecycleFactory.factorize();
        final String sql = "SELECT\n" + ((((((((("APPROX_QUANTILE_FIXED_BUCKETS(m1, 0.01, 20, 0.0, 10.0),\n" + "APPROX_QUANTILE_FIXED_BUCKETS(m1, 0.5, 20, 0.0, 10.0),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(m1, 0.98, 20, 0.0, 10.0),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(m1, 0.99, 20, 0.0, 10.0),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(m1 * 2, 0.97, 40, 0.0, 20.0),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(m1, 0.99, 20, 0.0, 10.0) FILTER(WHERE dim1 = \'abc\'),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(m1, 0.999, 20, 0.0, 10.0) FILTER(WHERE dim1 <> \'abc\'),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(m1, 0.999, 20, 0.0, 10.0) FILTER(WHERE dim1 = \'abc\'),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(cnt, 0.5, 20, 0.0, 10.0)\n") + "FROM foo");
        // Verify results
        final List<Object[]> results = sqlLifecycle.runSimple(sql, FixedBucketsHistogramQuantileSqlAggregatorTest.QUERY_CONTEXT_DEFAULT, FixedBucketsHistogramQuantileSqlAggregatorTest.authenticationResult).toList();
        final List<Object[]> expectedResults = ImmutableList.of(new Object[]{ 1.0299999713897705, 3.5, 6.440000057220459, 6.470000267028809, 12.40999984741211, 6.494999885559082, 5.497499942779541, 6.499499797821045, 1.25 });
        Assert.assertEquals(expectedResults.size(), results.size());
        for (int i = 0; i < (expectedResults.size()); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), results.get(i));
        }
        Query actual = Iterables.getOnlyElement(queryLogHook.getRecordedQueries());
        Query expected = Druids.newTimeseriesQueryBuilder().dataSource(DATASOURCE1).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).granularity(ALL).virtualColumns(new org.apache.druid.segment.virtual.ExpressionVirtualColumn("a4:v", "(\"m1\" * 2)", ValueType.FLOAT, TestExprMacroTable.INSTANCE)).aggregators(ImmutableList.of(new org.apache.druid.query.aggregation.histogram.FixedBucketsHistogramAggregatorFactory("a0:agg", "m1", 20, 0.0, 10.0, OutlierHandlingMode.IGNORE), new org.apache.druid.query.aggregation.histogram.FixedBucketsHistogramAggregatorFactory("a4:agg", "a4:v", 40, 0.0, 20.0, OutlierHandlingMode.IGNORE), new FilteredAggregatorFactory(new org.apache.druid.query.aggregation.histogram.FixedBucketsHistogramAggregatorFactory("a5:agg", "m1", 20, 0.0, 10.0, OutlierHandlingMode.IGNORE), new SelectorDimFilter("dim1", "abc", null)), new FilteredAggregatorFactory(new org.apache.druid.query.aggregation.histogram.FixedBucketsHistogramAggregatorFactory("a6:agg", "m1", 20, 0.0, 10.0, OutlierHandlingMode.IGNORE), new org.apache.druid.query.filter.NotDimFilter(new SelectorDimFilter("dim1", "abc", null))), new org.apache.druid.query.aggregation.histogram.FixedBucketsHistogramAggregatorFactory("a8:agg", "cnt", 20, 0.0, 10.0, OutlierHandlingMode.IGNORE))).postAggregators(new QuantilePostAggregator("a0", "a0:agg", 0.01F), new QuantilePostAggregator("a1", "a0:agg", 0.5F), new QuantilePostAggregator("a2", "a0:agg", 0.98F), new QuantilePostAggregator("a3", "a0:agg", 0.99F), new QuantilePostAggregator("a4", "a4:agg", 0.97F), new QuantilePostAggregator("a5", "a5:agg", 0.99F), new QuantilePostAggregator("a6", "a6:agg", 0.999F), new QuantilePostAggregator("a7", "a5:agg", 0.999F), new QuantilePostAggregator("a8", "a8:agg", 0.5F)).context(ImmutableMap.of("skipEmptyBuckets", true, CTX_SQL_QUERY_ID, "dummy")).build();
        // Verify query
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testQuantileOnComplexColumn() throws Exception {
        SqlLifecycle lifecycle = sqlLifecycleFactory.factorize();
        final String sql = "SELECT\n" + ((((((("APPROX_QUANTILE_FIXED_BUCKETS(fbhist_m1, 0.01, 20, 0.0, 10.0),\n" + "APPROX_QUANTILE_FIXED_BUCKETS(fbhist_m1, 0.5, 20, 0.0, 10.0),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(fbhist_m1, 0.98, 30, 0.0, 10.0),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(fbhist_m1, 0.99, 20, 0.0, 10.0),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(fbhist_m1, 0.99, 20, 0.0, 10.0) FILTER(WHERE dim1 = \'abc\'),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(fbhist_m1, 0.999, 20, 0.0, 10.0) FILTER(WHERE dim1 <> \'abc\'),\n") + "APPROX_QUANTILE_FIXED_BUCKETS(fbhist_m1, 0.999, 20, 0.0, 10.0) FILTER(WHERE dim1 = \'abc\')\n") + "FROM foo");
        // Verify results
        final List<Object[]> results = lifecycle.runSimple(sql, FixedBucketsHistogramQuantileSqlAggregatorTest.QUERY_CONTEXT_DEFAULT, FixedBucketsHistogramQuantileSqlAggregatorTest.authenticationResult).toList();
        final List<Object[]> expectedResults = ImmutableList.of(new Object[]{ 1.0299999713897705, 3.5, 6.293333530426025, 6.470000267028809, 6.494999885559082, 5.497499942779541, 6.499499797821045 });
        Assert.assertEquals(expectedResults.size(), results.size());
        for (int i = 0; i < (expectedResults.size()); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), results.get(i));
        }
        Query actual = Iterables.getOnlyElement(queryLogHook.getRecordedQueries());
        Query expected = Druids.newTimeseriesQueryBuilder().dataSource(DATASOURCE1).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).granularity(ALL).aggregators(ImmutableList.of(new org.apache.druid.query.aggregation.histogram.FixedBucketsHistogramAggregatorFactory("a0:agg", "fbhist_m1", 20, 0.0, 10.0, OutlierHandlingMode.IGNORE), new org.apache.druid.query.aggregation.histogram.FixedBucketsHistogramAggregatorFactory("a2:agg", "fbhist_m1", 30, 0.0, 10.0, OutlierHandlingMode.IGNORE), new FilteredAggregatorFactory(new org.apache.druid.query.aggregation.histogram.FixedBucketsHistogramAggregatorFactory("a4:agg", "fbhist_m1", 20, 0.0, 10.0, OutlierHandlingMode.IGNORE), new SelectorDimFilter("dim1", "abc", null)), new FilteredAggregatorFactory(new org.apache.druid.query.aggregation.histogram.FixedBucketsHistogramAggregatorFactory("a5:agg", "fbhist_m1", 20, 0.0, 10.0, OutlierHandlingMode.IGNORE), new org.apache.druid.query.filter.NotDimFilter(new SelectorDimFilter("dim1", "abc", null))))).postAggregators(new QuantilePostAggregator("a0", "a0:agg", 0.01F), new QuantilePostAggregator("a1", "a0:agg", 0.5F), new QuantilePostAggregator("a2", "a2:agg", 0.98F), new QuantilePostAggregator("a3", "a0:agg", 0.99F), new QuantilePostAggregator("a4", "a4:agg", 0.99F), new QuantilePostAggregator("a5", "a5:agg", 0.999F), new QuantilePostAggregator("a6", "a4:agg", 0.999F)).context(ImmutableMap.of("skipEmptyBuckets", true, CTX_SQL_QUERY_ID, "dummy")).build();
        // Verify query
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testQuantileOnInnerQuery() throws Exception {
        SqlLifecycle sqlLifecycle = sqlLifecycleFactory.factorize();
        final String sql = "SELECT AVG(x), APPROX_QUANTILE_FIXED_BUCKETS(x, 0.98, 100, 0.0, 100.0)\n" + "FROM (SELECT dim2, SUM(m1) AS x FROM foo GROUP BY dim2)";
        // Verify results
        final List<Object[]> results = sqlLifecycle.runSimple(sql, FixedBucketsHistogramQuantileSqlAggregatorTest.QUERY_CONTEXT_DEFAULT, FixedBucketsHistogramQuantileSqlAggregatorTest.authenticationResult).toList();
        final List<Object[]> expectedResults;
        if (NullHandling.replaceWithDefault()) {
            expectedResults = ImmutableList.of(new Object[]{ 7.0, 11.940000534057617 });
        } else {
            expectedResults = ImmutableList.of(new Object[]{ 5.25, 8.920000076293945 });
        }
        Assert.assertEquals(expectedResults.size(), results.size());
        for (int i = 0; i < (expectedResults.size()); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), results.get(i));
        }
        Query actual = Iterables.getOnlyElement(queryLogHook.getRecordedQueries());
        Query expected = GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(DATASOURCE1).setInterval(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).setGranularity(ALL).setDimensions(new DefaultDimensionSpec("dim2", "d0")).setAggregatorSpecs(ImmutableList.of(new DoubleSumAggregatorFactory("a0", "m1"))).setContext(ImmutableMap.of(CTX_SQL_QUERY_ID, "dummy")).build())).setInterval(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).setGranularity(ALL).setAggregatorSpecs(new DoubleSumAggregatorFactory("_a0:sum", "a0"), new CountAggregatorFactory("_a0:count"), new org.apache.druid.query.aggregation.histogram.FixedBucketsHistogramAggregatorFactory("_a1:agg", "a0", 100, 0, 100.0, OutlierHandlingMode.IGNORE)).setPostAggregatorSpecs(ImmutableList.of(new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("_a0", "quotient", ImmutableList.of(new FieldAccessPostAggregator(null, "_a0:sum"), new FieldAccessPostAggregator(null, "_a0:count"))), new QuantilePostAggregator("_a1", "_a1:agg", 0.98F))).setContext(ImmutableMap.of(CTX_SQL_QUERY_ID, "dummy")).build();
        // Verify query
        Assert.assertEquals(expected, actual);
    }
}

