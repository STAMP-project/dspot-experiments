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
import org.apache.druid.query.QueryRunnerFactoryConglomerate;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleSumAggregatorFactory;
import org.apache.druid.query.aggregation.histogram.ApproximateHistogramAggregatorFactory;
import org.apache.druid.query.aggregation.histogram.ApproximateHistogramFoldingAggregatorFactory;
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


public class QuantileSqlAggregatorTest extends CalciteTestBase {
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
        final String sql = "SELECT\n" + ((((((((("APPROX_QUANTILE(m1, 0.01),\n" + "APPROX_QUANTILE(m1, 0.5, 50),\n") + "APPROX_QUANTILE(m1, 0.98, 200),\n") + "APPROX_QUANTILE(m1, 0.99),\n") + "APPROX_QUANTILE(m1 * 2, 0.97),\n") + "APPROX_QUANTILE(m1, 0.99) FILTER(WHERE dim1 = \'abc\'),\n") + "APPROX_QUANTILE(m1, 0.999) FILTER(WHERE dim1 <> \'abc\'),\n") + "APPROX_QUANTILE(m1, 0.999) FILTER(WHERE dim1 = \'abc\'),\n") + "APPROX_QUANTILE(cnt, 0.5)\n") + "FROM foo");
        // Verify results
        final List<Object[]> results = sqlLifecycle.runSimple(sql, QuantileSqlAggregatorTest.QUERY_CONTEXT_DEFAULT, QuantileSqlAggregatorTest.authenticationResult).toList();
        final List<Object[]> expectedResults = ImmutableList.of(new Object[]{ 1.0, 3.0, 5.880000114440918, 5.940000057220459, 11.640000343322754, 6.0, 4.994999885559082, 6.0, 1.0 });
        Assert.assertEquals(expectedResults.size(), results.size());
        for (int i = 0; i < (expectedResults.size()); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), results.get(i));
        }
        // Verify query
        Assert.assertEquals(Druids.newTimeseriesQueryBuilder().dataSource(DATASOURCE1).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).granularity(ALL).virtualColumns(new org.apache.druid.segment.virtual.ExpressionVirtualColumn("a4:v", "(\"m1\" * 2)", ValueType.FLOAT, TestExprMacroTable.INSTANCE)).aggregators(ImmutableList.of(new ApproximateHistogramAggregatorFactory("a0:agg", "m1", null, null, null, null), new ApproximateHistogramAggregatorFactory("a2:agg", "m1", 200, null, null, null), new ApproximateHistogramAggregatorFactory("a4:agg", "a4:v", null, null, null, null), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new ApproximateHistogramAggregatorFactory("a5:agg", "m1", null, null, null, null), new SelectorDimFilter("dim1", "abc", null)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new ApproximateHistogramAggregatorFactory("a6:agg", "m1", null, null, null, null), new org.apache.druid.query.filter.NotDimFilter(new SelectorDimFilter("dim1", "abc", null))), new ApproximateHistogramAggregatorFactory("a8:agg", "cnt", null, null, null, null))).postAggregators(new QuantilePostAggregator("a0", "a0:agg", 0.01F), new QuantilePostAggregator("a1", "a0:agg", 0.5F), new QuantilePostAggregator("a2", "a2:agg", 0.98F), new QuantilePostAggregator("a3", "a0:agg", 0.99F), new QuantilePostAggregator("a4", "a4:agg", 0.97F), new QuantilePostAggregator("a5", "a5:agg", 0.99F), new QuantilePostAggregator("a6", "a6:agg", 0.999F), new QuantilePostAggregator("a7", "a5:agg", 0.999F), new QuantilePostAggregator("a8", "a8:agg", 0.5F)).context(ImmutableMap.of("skipEmptyBuckets", true, CTX_SQL_QUERY_ID, "dummy")).build(), Iterables.getOnlyElement(queryLogHook.getRecordedQueries()));
    }

    @Test
    public void testQuantileOnComplexColumn() throws Exception {
        SqlLifecycle lifecycle = sqlLifecycleFactory.factorize();
        final String sql = "SELECT\n" + ((((((("APPROX_QUANTILE(hist_m1, 0.01),\n" + "APPROX_QUANTILE(hist_m1, 0.5, 50),\n") + "APPROX_QUANTILE(hist_m1, 0.98, 200),\n") + "APPROX_QUANTILE(hist_m1, 0.99),\n") + "APPROX_QUANTILE(hist_m1, 0.99) FILTER(WHERE dim1 = \'abc\'),\n") + "APPROX_QUANTILE(hist_m1, 0.999) FILTER(WHERE dim1 <> \'abc\'),\n") + "APPROX_QUANTILE(hist_m1, 0.999) FILTER(WHERE dim1 = \'abc\')\n") + "FROM foo");
        // Verify results
        final List<Object[]> results = lifecycle.runSimple(sql, QuantileSqlAggregatorTest.QUERY_CONTEXT_DEFAULT, QuantileSqlAggregatorTest.authenticationResult).toList();
        final List<Object[]> expectedResults = ImmutableList.of(new Object[]{ 1.0, 3.0, 5.880000114440918, 5.940000057220459, 6.0, 4.994999885559082, 6.0 });
        Assert.assertEquals(expectedResults.size(), results.size());
        for (int i = 0; i < (expectedResults.size()); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), results.get(i));
        }
        // Verify query
        Assert.assertEquals(Druids.newTimeseriesQueryBuilder().dataSource(DATASOURCE1).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).granularity(ALL).aggregators(ImmutableList.of(new ApproximateHistogramFoldingAggregatorFactory("a0:agg", "hist_m1", null, null, null, null), new ApproximateHistogramFoldingAggregatorFactory("a2:agg", "hist_m1", 200, null, null, null), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new ApproximateHistogramFoldingAggregatorFactory("a4:agg", "hist_m1", null, null, null, null), new SelectorDimFilter("dim1", "abc", null)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new ApproximateHistogramFoldingAggregatorFactory("a5:agg", "hist_m1", null, null, null, null), new org.apache.druid.query.filter.NotDimFilter(new SelectorDimFilter("dim1", "abc", null))))).postAggregators(new QuantilePostAggregator("a0", "a0:agg", 0.01F), new QuantilePostAggregator("a1", "a0:agg", 0.5F), new QuantilePostAggregator("a2", "a2:agg", 0.98F), new QuantilePostAggregator("a3", "a0:agg", 0.99F), new QuantilePostAggregator("a4", "a4:agg", 0.99F), new QuantilePostAggregator("a5", "a5:agg", 0.999F), new QuantilePostAggregator("a6", "a4:agg", 0.999F)).context(ImmutableMap.of("skipEmptyBuckets", true, CTX_SQL_QUERY_ID, "dummy")).build(), Iterables.getOnlyElement(queryLogHook.getRecordedQueries()));
    }

    @Test
    public void testQuantileOnInnerQuery() throws Exception {
        SqlLifecycle sqlLifecycle = sqlLifecycleFactory.factorize();
        final String sql = "SELECT AVG(x), APPROX_QUANTILE(x, 0.98)\n" + "FROM (SELECT dim2, SUM(m1) AS x FROM foo GROUP BY dim2)";
        // Verify results
        final List<Object[]> results = sqlLifecycle.runSimple(sql, QuantileSqlAggregatorTest.QUERY_CONTEXT_DEFAULT, QuantileSqlAggregatorTest.authenticationResult).toList();
        final List<Object[]> expectedResults;
        if (NullHandling.replaceWithDefault()) {
            expectedResults = ImmutableList.of(new Object[]{ 7.0, 8.26386833190918 });
        } else {
            expectedResults = ImmutableList.of(new Object[]{ 5.25, 6.59091854095459 });
        }
        Assert.assertEquals(expectedResults.size(), results.size());
        for (int i = 0; i < (expectedResults.size()); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), results.get(i));
        }
        // Verify query
        Assert.assertEquals(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(DATASOURCE1).setInterval(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).setGranularity(ALL).setDimensions(new DefaultDimensionSpec("dim2", "d0")).setAggregatorSpecs(ImmutableList.of(new DoubleSumAggregatorFactory("a0", "m1"))).setContext(ImmutableMap.of(CTX_SQL_QUERY_ID, "dummy")).build())).setInterval(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).setGranularity(ALL).setAggregatorSpecs(new DoubleSumAggregatorFactory("_a0:sum", "a0"), new CountAggregatorFactory("_a0:count"), new ApproximateHistogramAggregatorFactory("_a1:agg", "a0", null, null, null, null)).setPostAggregatorSpecs(ImmutableList.of(new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("_a0", "quotient", ImmutableList.of(new FieldAccessPostAggregator(null, "_a0:sum"), new FieldAccessPostAggregator(null, "_a0:count"))), new QuantilePostAggregator("_a1", "_a1:agg", 0.98F))).setContext(ImmutableMap.of(CTX_SQL_QUERY_ID, "dummy")).build(), Iterables.getOnlyElement(queryLogHook.getRecordedQueries()));
    }
}

