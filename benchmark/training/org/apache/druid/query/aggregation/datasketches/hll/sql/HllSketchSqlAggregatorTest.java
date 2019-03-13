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
package org.apache.druid.query.aggregation.datasketches.hll.sql;


import CalciteTests.DATASOURCE1;
import Granularities.ALL;
import PlannerContext.CTX_SQL_QUERY_ID;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.Druids;
import org.apache.druid.query.Query;
import org.apache.druid.query.QueryRunnerFactoryConglomerate;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.aggregation.datasketches.hll.HllSketchBuildAggregatorFactory;
import org.apache.druid.query.aggregation.datasketches.hll.HllSketchMergeAggregatorFactory;
import org.apache.druid.query.aggregation.post.FieldAccessPostAggregator;
import org.apache.druid.query.aggregation.post.FinalizingFieldAccessPostAggregator;
import org.apache.druid.query.expression.TestExprMacroTable;
import org.apache.druid.query.groupby.GroupByQuery;
import org.apache.druid.segment.column.ValueType;
import org.apache.druid.server.security.AuthenticationResult;
import org.apache.druid.sql.SqlLifecycle;
import org.apache.druid.sql.SqlLifecycleFactory;
import org.apache.druid.sql.calcite.BaseCalciteQueryTest;
import org.apache.druid.sql.calcite.filtration.Filtration;
import org.apache.druid.sql.calcite.util.CalciteTestBase;
import org.apache.druid.sql.calcite.util.CalciteTests;
import org.apache.druid.sql.calcite.util.QueryLogHook;
import org.apache.druid.sql.calcite.util.SpecificSegmentsQuerySegmentWalker;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class HllSketchSqlAggregatorTest extends CalciteTestBase {
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
    public void testApproxCountDistinctHllSketch() throws Exception {
        SqlLifecycle sqlLifecycle = sqlLifecycleFactory.factorize();
        final String sql = "SELECT\n" + ((((((("  SUM(cnt),\n" + "  APPROX_COUNT_DISTINCT_DS_HLL(dim2),\n")// uppercase
         + "  APPROX_COUNT_DISTINCT_DS_HLL(dim2) FILTER(WHERE dim2 <> \'\'),\n")// lowercase; also, filtered
         + "  APPROX_COUNT_DISTINCT_DS_HLL(SUBSTRING(dim2, 1, 1)),\n")// on extractionFn
         + "  APPROX_COUNT_DISTINCT_DS_HLL(SUBSTRING(dim2, 1, 1) || \'x\'),\n")// on expression
         + "  APPROX_COUNT_DISTINCT_DS_HLL(hllsketch_dim1, 21, \'HLL_8\'),\n")// on native HllSketch column
         + "  APPROX_COUNT_DISTINCT_DS_HLL(hllsketch_dim1)\n")// on native HllSketch column
         + "FROM druid.foo");
        // Verify results
        final List<Object[]> results = sqlLifecycle.runSimple(sql, HllSketchSqlAggregatorTest.QUERY_CONTEXT_DEFAULT, HllSketchSqlAggregatorTest.authenticationResult).toList();
        final List<Object[]> expectedResults;
        if (NullHandling.replaceWithDefault()) {
            expectedResults = ImmutableList.of(new Object[]{ 6L, 2L, 2L, 1L, 2L, 5L, 5L });
        } else {
            expectedResults = ImmutableList.of(new Object[]{ 6L, 2L, 2L, 1L, 1L, 5L, 5L });
        }
        Assert.assertEquals(expectedResults.size(), results.size());
        for (int i = 0; i < (expectedResults.size()); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), results.get(i));
        }
        // Verify query
        Assert.assertEquals(Druids.newTimeseriesQueryBuilder().dataSource(DATASOURCE1).intervals(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).granularity(ALL).virtualColumns(new org.apache.druid.segment.virtual.ExpressionVirtualColumn("a3:v", "substring(\"dim2\", 0, 1)", ValueType.STRING, TestExprMacroTable.INSTANCE), new org.apache.druid.segment.virtual.ExpressionVirtualColumn("a4:v", "concat(substring(\"dim2\", 0, 1),\'x\')", ValueType.STRING, TestExprMacroTable.INSTANCE)).aggregators(ImmutableList.of(new LongSumAggregatorFactory("a0", "cnt"), new HllSketchBuildAggregatorFactory("a1", "dim2", null, null), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new HllSketchBuildAggregatorFactory("a2", "dim2", null, null), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", "", null))), new HllSketchBuildAggregatorFactory("a3", "a3:v", null, null), new HllSketchBuildAggregatorFactory("a4", "a4:v", null, null), new HllSketchMergeAggregatorFactory("a5", "hllsketch_dim1", 21, "HLL_8"), new HllSketchMergeAggregatorFactory("a6", "hllsketch_dim1", null, null))).context(ImmutableMap.of("skipEmptyBuckets", true, CTX_SQL_QUERY_ID, "dummy")).build(), Iterables.getOnlyElement(queryLogHook.getRecordedQueries()));
    }

    @Test
    public void testAvgDailyCountDistinctHllSketch() throws Exception {
        SqlLifecycle sqlLifecycle = sqlLifecycleFactory.factorize();
        final String sql = "SELECT\n" + ("  AVG(u)\n" + "FROM (SELECT FLOOR(__time TO DAY), APPROX_COUNT_DISTINCT_DS_HLL(cnt) AS u FROM druid.foo GROUP BY 1)");
        // Verify results
        final List<Object[]> results = sqlLifecycle.runSimple(sql, HllSketchSqlAggregatorTest.QUERY_CONTEXT_DEFAULT, HllSketchSqlAggregatorTest.authenticationResult).toList();
        final List<Object[]> expectedResults = ImmutableList.of(new Object[]{ 1L });
        Assert.assertEquals(expectedResults.size(), results.size());
        for (int i = 0; i < (expectedResults.size()); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), results.get(i));
        }
        Query expected = GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(DATASOURCE1).setInterval(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).setGranularity(ALL).setVirtualColumns(new org.apache.druid.segment.virtual.ExpressionVirtualColumn("d0:v", "timestamp_floor(\"__time\",\'P1D\',null,\'UTC\')", ValueType.LONG, TestExprMacroTable.INSTANCE)).setDimensions(Collections.singletonList(new org.apache.druid.query.dimension.DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(Collections.singletonList(new HllSketchBuildAggregatorFactory("a0:a", "cnt", null, null))).setPostAggregatorSpecs(ImmutableList.of(new FinalizingFieldAccessPostAggregator("a0", "a0:a"))).setContext(HllSketchSqlAggregatorTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(ImmutableList.of(Filtration.eternity()))).setGranularity(ALL).setAggregatorSpecs(Arrays.asList(new LongSumAggregatorFactory("_a0:sum", "a0"), new CountAggregatorFactory("_a0:count"))).setPostAggregatorSpecs(ImmutableList.of(new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("_a0", "quotient", ImmutableList.of(new FieldAccessPostAggregator(null, "_a0:sum"), new FieldAccessPostAggregator(null, "_a0:count"))))).setContext(HllSketchSqlAggregatorTest.QUERY_CONTEXT_DEFAULT).build();
        Query actual = Iterables.getOnlyElement(queryLogHook.getRecordedQueries());
        // Verify query
        Assert.assertEquals(expected, actual);
    }
}

