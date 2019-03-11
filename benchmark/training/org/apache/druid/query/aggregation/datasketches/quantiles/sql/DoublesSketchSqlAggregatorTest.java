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
package org.apache.druid.query.aggregation.datasketches.quantiles.sql;


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
import org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchAggregatorFactory;
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


public class DoublesSketchSqlAggregatorTest extends CalciteTestBase {
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
        final String sql = "SELECT\n" + ((((((((("APPROX_QUANTILE_DS(m1, 0.01),\n" + "APPROX_QUANTILE_DS(m1, 0.5, 64),\n") + "APPROX_QUANTILE_DS(m1, 0.98, 256),\n") + "APPROX_QUANTILE_DS(m1, 0.99),\n") + "APPROX_QUANTILE_DS(m1 * 2, 0.97),\n") + "APPROX_QUANTILE_DS(m1, 0.99) FILTER(WHERE dim1 = \'abc\'),\n") + "APPROX_QUANTILE_DS(m1, 0.999) FILTER(WHERE dim1 <> \'abc\'),\n") + "APPROX_QUANTILE_DS(m1, 0.999) FILTER(WHERE dim1 = \'abc\'),\n") + "APPROX_QUANTILE_DS(cnt, 0.5)\n") + "FROM foo");
        // Verify results
        final List<Object[]> results = sqlLifecycle.runSimple(sql, DoublesSketchSqlAggregatorTest.QUERY_CONTEXT_DEFAULT, DoublesSketchSqlAggregatorTest.authenticationResult).toList();
        final List<Object[]> expectedResults = ImmutableList.of(new Object[]{ 1.0, 4.0, 6.0, 6.0, 12.0, 6.0, 5.0, 6.0, 1.0 });
        Assert.assertEquals(expectedResults.size(), results.size());
        for (int i = 0; i < (expectedResults.size()); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), results.get(i));
        }
        // Verify query
        Assert.assertEquals(Druids.newTimeseriesQueryBuilder().dataSource(DATASOURCE1).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).granularity(ALL).virtualColumns(new org.apache.druid.segment.virtual.ExpressionVirtualColumn("a4:v", "(\"m1\" * 2)", ValueType.FLOAT, TestExprMacroTable.INSTANCE)).aggregators(ImmutableList.of(new DoublesSketchAggregatorFactory("a0:agg", "m1", null), new DoublesSketchAggregatorFactory("a1:agg", "m1", 64), new DoublesSketchAggregatorFactory("a2:agg", "m1", 256), new DoublesSketchAggregatorFactory("a4:agg", "a4:v", null), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new DoublesSketchAggregatorFactory("a5:agg", "m1", null), new SelectorDimFilter("dim1", "abc", null)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new DoublesSketchAggregatorFactory("a6:agg", "m1", null), new org.apache.druid.query.filter.NotDimFilter(new SelectorDimFilter("dim1", "abc", null))), new DoublesSketchAggregatorFactory("a8:agg", "cnt", null))).postAggregators(new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a0", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a0:agg"), 0.01F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a1", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a1:agg"), 0.5F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a2", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a2:agg"), 0.98F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a3", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a0:agg"), 0.99F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a4", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a4:agg"), 0.97F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a5", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a5:agg"), 0.99F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a6", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a6:agg"), 0.999F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a7", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a5:agg"), 0.999F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a8", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a8:agg"), 0.5F)).context(ImmutableMap.of("skipEmptyBuckets", true, CTX_SQL_QUERY_ID, "dummy")).build(), Iterables.getOnlyElement(queryLogHook.getRecordedQueries()));
    }

    @Test
    public void testQuantileOnComplexColumn() throws Exception {
        SqlLifecycle lifecycle = sqlLifecycleFactory.factorize();
        final String sql = "SELECT\n" + ((((((("APPROX_QUANTILE_DS(qsketch_m1, 0.01),\n" + "APPROX_QUANTILE_DS(qsketch_m1, 0.5, 64),\n") + "APPROX_QUANTILE_DS(qsketch_m1, 0.98, 256),\n") + "APPROX_QUANTILE_DS(qsketch_m1, 0.99),\n") + "APPROX_QUANTILE_DS(qsketch_m1, 0.99) FILTER(WHERE dim1 = \'abc\'),\n") + "APPROX_QUANTILE_DS(qsketch_m1, 0.999) FILTER(WHERE dim1 <> \'abc\'),\n") + "APPROX_QUANTILE_DS(qsketch_m1, 0.999) FILTER(WHERE dim1 = \'abc\')\n") + "FROM foo");
        // Verify results
        final List<Object[]> results = lifecycle.runSimple(sql, DoublesSketchSqlAggregatorTest.QUERY_CONTEXT_DEFAULT, DoublesSketchSqlAggregatorTest.authenticationResult).toList();
        final List<Object[]> expectedResults = ImmutableList.of(new Object[]{ 1.0, 4.0, 6.0, 6.0, 6.0, 5.0, 6.0 });
        Assert.assertEquals(expectedResults.size(), results.size());
        for (int i = 0; i < (expectedResults.size()); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), results.get(i));
        }
        // Verify query
        Assert.assertEquals(Druids.newTimeseriesQueryBuilder().dataSource(DATASOURCE1).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).granularity(ALL).aggregators(ImmutableList.of(new DoublesSketchAggregatorFactory("a0:agg", "qsketch_m1", null), new DoublesSketchAggregatorFactory("a1:agg", "qsketch_m1", 64), new DoublesSketchAggregatorFactory("a2:agg", "qsketch_m1", 256), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new DoublesSketchAggregatorFactory("a4:agg", "qsketch_m1", null), new SelectorDimFilter("dim1", "abc", null)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new DoublesSketchAggregatorFactory("a5:agg", "qsketch_m1", null), new org.apache.druid.query.filter.NotDimFilter(new SelectorDimFilter("dim1", "abc", null))))).postAggregators(new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a0", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a0:agg"), 0.01F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a1", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a1:agg"), 0.5F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a2", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a2:agg"), 0.98F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a3", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a0:agg"), 0.99F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a4", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a4:agg"), 0.99F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a5", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a5:agg"), 0.999F), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("a6", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("a4:agg"), 0.999F)).context(ImmutableMap.of("skipEmptyBuckets", true, CTX_SQL_QUERY_ID, "dummy")).build(), Iterables.getOnlyElement(queryLogHook.getRecordedQueries()));
    }

    @Test
    public void testQuantileOnInnerQuery() throws Exception {
        SqlLifecycle sqlLifecycle = sqlLifecycleFactory.factorize();
        final String sql = "SELECT AVG(x), APPROX_QUANTILE_DS(x, 0.98)\n" + "FROM (SELECT dim2, SUM(m1) AS x FROM foo GROUP BY dim2)";
        // Verify results
        final List<Object[]> results = sqlLifecycle.runSimple(sql, DoublesSketchSqlAggregatorTest.QUERY_CONTEXT_DEFAULT, DoublesSketchSqlAggregatorTest.authenticationResult).toList();
        final List<Object[]> expectedResults;
        if (NullHandling.replaceWithDefault()) {
            expectedResults = ImmutableList.of(new Object[]{ 7.0, 11.0 });
        } else {
            expectedResults = ImmutableList.of(new Object[]{ 5.25, 8.0 });
        }
        Assert.assertEquals(expectedResults.size(), results.size());
        for (int i = 0; i < (expectedResults.size()); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), results.get(i));
        }
        // Verify query
        Assert.assertEquals(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(DATASOURCE1).setInterval(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).setGranularity(ALL).setDimensions(new DefaultDimensionSpec("dim2", "d0")).setAggregatorSpecs(ImmutableList.of(new DoubleSumAggregatorFactory("a0", "m1"))).setContext(ImmutableMap.of(CTX_SQL_QUERY_ID, "dummy")).build())).setInterval(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).setGranularity(ALL).setAggregatorSpecs(new DoubleSumAggregatorFactory("_a0:sum", "a0"), new CountAggregatorFactory("_a0:count"), new DoublesSketchAggregatorFactory("_a1:agg", "a0", null)).setPostAggregatorSpecs(ImmutableList.of(new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("_a0", "quotient", ImmutableList.of(new FieldAccessPostAggregator(null, "_a0:sum"), new FieldAccessPostAggregator(null, "_a0:count"))), new org.apache.druid.query.aggregation.datasketches.quantiles.DoublesSketchToQuantilePostAggregator("_a1", DoublesSketchSqlAggregatorTest.makeFieldAccessPostAgg("_a1:agg"), 0.98F))).setContext(ImmutableMap.of(CTX_SQL_QUERY_ID, "dummy")).build(), Iterables.getOnlyElement(queryLogHook.getRecordedQueries()));
    }
}

