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
package org.apache.druid.sql.calcite;


import Granularities.ALL;
import Granularities.DAY;
import Granularities.MONTH;
import Granularities.YEAR;
import JodaUtils.MAX_INSTANT;
import ScanQuery.RESULT_FORMAT_COMPACTED_LIST;
import StringComparators.LEXICOGRAPHIC;
import StringComparators.NUMERIC;
import ValueType.FLOAT;
import ValueType.LONG;
import ValueType.STRING;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.math.expr.ExprMacroTable;
import org.apache.druid.query.Druids;
import org.apache.druid.query.ResourceLimitExceededException;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleSumAggregatorFactory;
import org.apache.druid.query.aggregation.FloatMaxAggregatorFactory;
import org.apache.druid.query.aggregation.FloatMinAggregatorFactory;
import org.apache.druid.query.aggregation.LongMaxAggregatorFactory;
import org.apache.druid.query.aggregation.LongMinAggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.aggregation.hyperloglog.HyperUniqueFinalizingPostAggregator;
import org.apache.druid.query.aggregation.hyperloglog.HyperUniquesAggregatorFactory;
import org.apache.druid.query.aggregation.post.FieldAccessPostAggregator;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.extraction.RegexDimExtractionFn;
import org.apache.druid.query.extraction.SubstringDimExtractionFn;
import org.apache.druid.query.filter.DimFilter;
import org.apache.druid.query.filter.InDimFilter;
import org.apache.druid.query.filter.LikeDimFilter;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.query.groupby.GroupByQuery;
import org.apache.druid.query.groupby.orderby.OrderByColumnSpec.Direction;
import org.apache.druid.query.lookup.RegisteredLookupExtractionFn;
import org.apache.druid.query.ordering.StringComparators;
import org.apache.druid.query.select.PagingSpec;
import org.apache.druid.query.topn.InvertedTopNMetricSpec;
import org.apache.druid.query.topn.NumericTopNMetricSpec;
import org.apache.druid.query.topn.TopNQueryBuilder;
import org.apache.druid.segment.column.ValueType;
import org.apache.druid.sql.calcite.expression.DruidExpression;
import org.apache.druid.sql.calcite.filtration.Filtration;
import org.apache.druid.sql.calcite.rel.CannotBuildQueryException;
import org.apache.druid.sql.calcite.util.CalciteTests;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;


public class CalciteQueryTest extends BaseCalciteQueryTest {
    @Test
    public void testSelectConstantExpression() throws Exception {
        testQuery("SELECT 1 + 1", ImmutableList.of(), ImmutableList.of(new Object[]{ 2 }));
    }

    @Test
    public void testSelectConstantExpressionFromTable() throws Exception {
        testQuery("SELECT 1 + 1, dim1 FROM foo LIMIT 1", ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).virtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("v0", "2", LONG)).columns("dim1", "v0").resultFormat(RESULT_FORMAT_COMPACTED_LIST).limit(1).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2, "" }));
    }

    @Test
    public void testSelectCountStart() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, BaseCalciteQueryTest.QUERY_CONTEXT_DONT_SKIP_EMPTY_BUCKETS, "SELECT exp(count(*)) + 10, sum(m2)  FROM druid.foo WHERE  dim2 = 0", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.selector("dim2", "0", null)).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"), new DoubleSumAggregatorFactory("a1", "m2"))).postAggregators(BaseCalciteQueryTest.expresionPostAgg("p0", "(exp(\"a0\") + 10)")).context(BaseCalciteQueryTest.QUERY_CONTEXT_DONT_SKIP_EMPTY_BUCKETS).build()), ImmutableList.of(new Object[]{ 11.0, NullHandling.defaultDoubleValue() }));
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, BaseCalciteQueryTest.QUERY_CONTEXT_DONT_SKIP_EMPTY_BUCKETS, "SELECT exp(count(*)) + 10, sum(m2)  FROM druid.foo WHERE  __time >= TIMESTAMP '2999-01-01 00:00:00'", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2999-01-01T00:00:00.000Z/146140482-04-24T15:36:27.903Z"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"), new DoubleSumAggregatorFactory("a1", "m2"))).postAggregators(BaseCalciteQueryTest.expresionPostAgg("p0", "(exp(\"a0\") + 10)")).context(BaseCalciteQueryTest.QUERY_CONTEXT_DONT_SKIP_EMPTY_BUCKETS).build()), ImmutableList.of(new Object[]{ 11.0, NullHandling.defaultDoubleValue() }));
        testQuery("SELECT COUNT(*) FROM foo WHERE dim1 = 'nonexistent' GROUP BY FLOOR(__time TO DAY)", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.selector("dim1", "nonexistent", null)).granularity(DAY).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of());
    }

    @Test
    public void testSelectTrimFamily() throws Exception {
        // TRIM has some whacky parsing. Make sure the different forms work.
        testQuery(("SELECT\n" + (((((((((((("TRIM(BOTH \'x\' FROM \'xfoox\'),\n" + "TRIM(TRAILING \'x\' FROM \'xfoox\'),\n") + "TRIM(\' \' FROM \' foo \'),\n") + "TRIM(TRAILING FROM \' foo \'),\n") + "TRIM(\' foo \'),\n") + "BTRIM(\' foo \'),\n") + "BTRIM(\'xfoox\', \'x\'),\n") + "LTRIM(\' foo \'),\n") + "LTRIM(\'xfoox\', \'x\'),\n") + "RTRIM(\' foo \'),\n") + "RTRIM(\'xfoox\', \'x\'),\n") + "COUNT(*)\n") + "FROM foo")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).postAggregators(BaseCalciteQueryTest.expresionPostAgg("p0", "'foo'"), BaseCalciteQueryTest.expresionPostAgg("p1", "'xfoo'"), BaseCalciteQueryTest.expresionPostAgg("p2", "'foo'"), BaseCalciteQueryTest.expresionPostAgg("p3", "' foo'"), BaseCalciteQueryTest.expresionPostAgg("p4", "'foo'"), BaseCalciteQueryTest.expresionPostAgg("p5", "'foo'"), BaseCalciteQueryTest.expresionPostAgg("p6", "'foo'"), BaseCalciteQueryTest.expresionPostAgg("p7", "'foo '"), BaseCalciteQueryTest.expresionPostAgg("p8", "'foox'"), BaseCalciteQueryTest.expresionPostAgg("p9", "' foo'"), BaseCalciteQueryTest.expresionPostAgg("p10", "'xfoo'")).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "foo", "xfoo", "foo", " foo", "foo", "foo", "foo", "foo ", "foox", " foo", "xfoo", 6L }));
    }

    @Test
    public void testExplainSelectConstantExpression() throws Exception {
        testQuery("EXPLAIN PLAN FOR SELECT 1 + 1", ImmutableList.of(), ImmutableList.of(new Object[]{ "BindableValues(tuples=[[{ 2 }]])\n" }));
    }

    @Test
    public void testInformationSchemaSchemata() throws Exception {
        testQuery("SELECT DISTINCT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA", ImmutableList.of(), ImmutableList.of(new Object[]{ "druid" }, new Object[]{ "sys" }, new Object[]{ "INFORMATION_SCHEMA" }));
    }

    @Test
    public void testInformationSchemaTables() throws Exception {
        testQuery(("SELECT TABLE_SCHEMA, TABLE_NAME, TABLE_TYPE\n" + ("FROM INFORMATION_SCHEMA.TABLES\n" + "WHERE TABLE_TYPE IN ('SYSTEM_TABLE', 'TABLE', 'VIEW')")), ImmutableList.of(), ImmutableList.of(new Object[]{ "druid", CalciteTests.DATASOURCE1, "TABLE" }, new Object[]{ "druid", CalciteTests.DATASOURCE2, "TABLE" }, new Object[]{ "druid", CalciteTests.DATASOURCE3, "TABLE" }, new Object[]{ "druid", "aview", "VIEW" }, new Object[]{ "druid", "bview", "VIEW" }, new Object[]{ "INFORMATION_SCHEMA", "COLUMNS", "SYSTEM_TABLE" }, new Object[]{ "INFORMATION_SCHEMA", "SCHEMATA", "SYSTEM_TABLE" }, new Object[]{ "INFORMATION_SCHEMA", "TABLES", "SYSTEM_TABLE" }, new Object[]{ "sys", "segments", "SYSTEM_TABLE" }, new Object[]{ "sys", "server_segments", "SYSTEM_TABLE" }, new Object[]{ "sys", "servers", "SYSTEM_TABLE" }, new Object[]{ "sys", "tasks", "SYSTEM_TABLE" }));
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, ("SELECT TABLE_SCHEMA, TABLE_NAME, TABLE_TYPE\n" + ("FROM INFORMATION_SCHEMA.TABLES\n" + "WHERE TABLE_TYPE IN ('SYSTEM_TABLE', 'TABLE', 'VIEW')")), CalciteTests.SUPER_USER_AUTH_RESULT, ImmutableList.of(), ImmutableList.<Object[]>builder().add(new Object[]{ "druid", CalciteTests.DATASOURCE1, "TABLE" }).add(new Object[]{ "druid", CalciteTests.DATASOURCE2, "TABLE" }).add(new Object[]{ "druid", CalciteTests.FORBIDDEN_DATASOURCE, "TABLE" }).add(new Object[]{ "druid", CalciteTests.DATASOURCE3, "TABLE" }).add(new Object[]{ "druid", "aview", "VIEW" }).add(new Object[]{ "druid", "bview", "VIEW" }).add(new Object[]{ "INFORMATION_SCHEMA", "COLUMNS", "SYSTEM_TABLE" }).add(new Object[]{ "INFORMATION_SCHEMA", "SCHEMATA", "SYSTEM_TABLE" }).add(new Object[]{ "INFORMATION_SCHEMA", "TABLES", "SYSTEM_TABLE" }).add(new Object[]{ "sys", "segments", "SYSTEM_TABLE" }).add(new Object[]{ "sys", "server_segments", "SYSTEM_TABLE" }).add(new Object[]{ "sys", "servers", "SYSTEM_TABLE" }).add(new Object[]{ "sys", "tasks", "SYSTEM_TABLE" }).build());
    }

    @Test
    public void testInformationSchemaColumnsOnTable() throws Exception {
        testQuery(("SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE\n" + ("FROM INFORMATION_SCHEMA.COLUMNS\n" + "WHERE TABLE_SCHEMA = 'druid' AND TABLE_NAME = 'foo'")), ImmutableList.of(), ImmutableList.of(new Object[]{ "__time", "TIMESTAMP", "NO" }, new Object[]{ "cnt", "BIGINT", "NO" }, new Object[]{ "dim1", "VARCHAR", "YES" }, new Object[]{ "dim2", "VARCHAR", "YES" }, new Object[]{ "dim3", "VARCHAR", "YES" }, new Object[]{ "m1", "FLOAT", "NO" }, new Object[]{ "m2", "DOUBLE", "NO" }, new Object[]{ "unique_dim1", "OTHER", "YES" }));
    }

    @Test
    public void testInformationSchemaColumnsOnForbiddenTable() throws Exception {
        testQuery(("SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE\n" + ("FROM INFORMATION_SCHEMA.COLUMNS\n" + "WHERE TABLE_SCHEMA = 'druid' AND TABLE_NAME = 'forbiddenDatasource'")), ImmutableList.of(), ImmutableList.of());
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, ("SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE\n" + ("FROM INFORMATION_SCHEMA.COLUMNS\n" + "WHERE TABLE_SCHEMA = 'druid' AND TABLE_NAME = 'forbiddenDatasource'")), CalciteTests.SUPER_USER_AUTH_RESULT, ImmutableList.of(), ImmutableList.of(new Object[]{ "__time", "TIMESTAMP", "NO" }, new Object[]{ "cnt", "BIGINT", "NO" }, new Object[]{ "dim1", "VARCHAR", "YES" }, new Object[]{ "dim2", "VARCHAR", "YES" }, new Object[]{ "m1", "FLOAT", "NO" }, new Object[]{ "m2", "DOUBLE", "NO" }, new Object[]{ "unique_dim1", "OTHER", "YES" }));
    }

    @Test
    public void testInformationSchemaColumnsOnView() throws Exception {
        testQuery(("SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE\n" + ("FROM INFORMATION_SCHEMA.COLUMNS\n" + "WHERE TABLE_SCHEMA = 'druid' AND TABLE_NAME = 'aview'")), ImmutableList.of(), ImmutableList.of(new Object[]{ "dim1_firstchar", "VARCHAR", "YES" }));
    }

    @Test
    public void testExplainInformationSchemaColumns() throws Exception {
        final String explanation = "BindableProject(COLUMN_NAME=[$3], DATA_TYPE=[$7])\n" + ("  BindableFilter(condition=[AND(=($1, \'druid\'), =($2, \'foo\'))])\n" + "    BindableTableScan(table=[[INFORMATION_SCHEMA, COLUMNS]])\n");
        testQuery(("EXPLAIN PLAN FOR\n" + (("SELECT COLUMN_NAME, DATA_TYPE\n" + "FROM INFORMATION_SCHEMA.COLUMNS\n") + "WHERE TABLE_SCHEMA = 'druid' AND TABLE_NAME = 'foo'")), ImmutableList.of(), ImmutableList.of(new Object[]{ explanation }));
    }

    @Test
    public void testAggregatorsOnInformationSchemaColumns() throws Exception {
        // Not including COUNT DISTINCT, since it isn't supported by BindableAggregate, and so it can't work.
        testQuery(("SELECT\n" + (((((("  COUNT(JDBC_TYPE),\n" + "  SUM(JDBC_TYPE),\n") + "  AVG(JDBC_TYPE),\n") + "  MIN(JDBC_TYPE),\n") + "  MAX(JDBC_TYPE)\n") + "FROM INFORMATION_SCHEMA.COLUMNS\n") + "WHERE TABLE_SCHEMA = 'druid' AND TABLE_NAME = 'foo'")), ImmutableList.of(), ImmutableList.of(new Object[]{ 8L, 1249L, 156L, -5L, 1111L }));
    }

    @Test
    public void testSelectStar() throws Exception {
        String hyperLogLogCollectorClassName = BaseCalciteQueryTest.HLLC_STRING;
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT_NO_COMPLEX_SERDE, BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT, "SELECT * FROM druid.foo", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).columns("__time", "cnt", "dim1", "dim2", "dim3", "m1", "m2", "unique_dim1").resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000-01-01"), 1L, "", "a", "[\"a\",\"b\"]", 1.0F, 1.0, hyperLogLogCollectorClassName }, new Object[]{ BaseCalciteQueryTest.t("2000-01-02"), 1L, "10.1", BaseCalciteQueryTest.NULL_VALUE, "[\"b\",\"c\"]", 2.0F, 2.0, hyperLogLogCollectorClassName }, new Object[]{ BaseCalciteQueryTest.t("2000-01-03"), 1L, "2", "", "d", 3.0F, 3.0, hyperLogLogCollectorClassName }, new Object[]{ BaseCalciteQueryTest.t("2001-01-01"), 1L, "1", "a", "", 4.0F, 4.0, hyperLogLogCollectorClassName }, new Object[]{ BaseCalciteQueryTest.t("2001-01-02"), 1L, "def", "abc", BaseCalciteQueryTest.NULL_VALUE, 5.0F, 5.0, hyperLogLogCollectorClassName }, new Object[]{ BaseCalciteQueryTest.t("2001-01-03"), 1L, "abc", BaseCalciteQueryTest.NULL_VALUE, BaseCalciteQueryTest.NULL_VALUE, 6.0F, 6.0, hyperLogLogCollectorClassName }));
    }

    @Test
    public void testSelectStarOnForbiddenTable() throws Exception {
        assertQueryIsForbidden("SELECT * FROM druid.forbiddenDatasource", CalciteTests.REGULAR_USER_AUTH_RESULT);
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, "SELECT * FROM druid.forbiddenDatasource", CalciteTests.SUPER_USER_AUTH_RESULT, ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.FORBIDDEN_DATASOURCE).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).columns("__time", "cnt", "dim1", "dim2", "m1", "m2", "unique_dim1").resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000-01-01"), 1L, "forbidden", "abcd", 9999.0F, NullHandling.defaultDoubleValue(), "\"AQAAAQAAAALFBA==\"" }));
    }

    @Test
    public void testUnqualifiedTableName() throws Exception {
        testQuery("SELECT COUNT(*) FROM foo", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }));
    }

    @Test
    public void testExplainSelectStar() throws Exception {
        testQuery("EXPLAIN PLAN FOR SELECT * FROM druid.foo", ImmutableList.of(), ImmutableList.of(new Object[]{ "DruidQueryRel(query=[{\"queryType\":\"scan\",\"dataSource\":{\"type\":\"table\",\"name\":\"foo\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"virtualColumns\":[],\"resultFormat\":\"compactedList\",\"batchSize\":20480,\"limit\":9223372036854775807,\"filter\":null,\"columns\":[\"__time\",\"cnt\",\"dim1\",\"dim2\",\"dim3\",\"m1\",\"m2\",\"unique_dim1\"],\"legacy\":false,\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"},\"descending\":false,\"granularity\":{\"type\":\"all\"}}], signature=[{__time:LONG, cnt:LONG, dim1:STRING, dim2:STRING, dim3:STRING, m1:FLOAT, m2:DOUBLE, unique_dim1:COMPLEX}])\n" }));
    }

    @Test
    public void testSelectStarWithLimit() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT_NO_COMPLEX_SERDE, BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT, "SELECT * FROM druid.foo LIMIT 2", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).columns("__time", "cnt", "dim1", "dim2", "dim3", "m1", "m2", "unique_dim1").limit(2).resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000-01-01"), 1L, "", "a", "[\"a\",\"b\"]", 1.0F, 1.0, BaseCalciteQueryTest.HLLC_STRING }, new Object[]{ BaseCalciteQueryTest.t("2000-01-02"), 1L, "10.1", BaseCalciteQueryTest.NULL_VALUE, "[\"b\",\"c\"]", 2.0F, 2.0, BaseCalciteQueryTest.HLLC_STRING }));
    }

    @Test
    public void testSelectWithProjection() throws Exception {
        testQuery("SELECT SUBSTRING(dim2, 1, 1) FROM druid.foo LIMIT 2", ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).virtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("v0", "substring(\"dim2\", 0, 1)", STRING)).columns("v0").limit(2).resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "a" }, new Object[]{ BaseCalciteQueryTest.NULL_VALUE }));
    }

    @Test
    public void testSelectStarWithLimitTimeDescending() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT_NO_COMPLEX_SERDE, BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT, "SELECT * FROM druid.foo ORDER BY __time DESC LIMIT 2", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newSelectQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimensions(ImmutableList.of("dummy")).metrics(ImmutableList.of("__time", "cnt", "dim1", "dim2", "dim3", "m1", "m2", "unique_dim1")).descending(true).pagingSpec(BaseCalciteQueryTest.FIRST_PAGING_SPEC).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2001-01-03"), 1L, "abc", BaseCalciteQueryTest.NULL_VALUE, BaseCalciteQueryTest.NULL_VALUE, 6.0F, 6.0, BaseCalciteQueryTest.HLLC_STRING }, new Object[]{ BaseCalciteQueryTest.t("2001-01-02"), 1L, "def", "abc", BaseCalciteQueryTest.NULL_VALUE, 5.0F, 5.0, BaseCalciteQueryTest.HLLC_STRING }));
    }

    @Test
    public void testSelectStarWithoutLimitTimeAscending() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT_NO_COMPLEX_SERDE, BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT, "SELECT * FROM druid.foo ORDER BY __time", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newSelectQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimensions(ImmutableList.of("dummy")).metrics(ImmutableList.of("__time", "cnt", "dim1", "dim2", "dim3", "m1", "m2", "unique_dim1")).descending(false).pagingSpec(BaseCalciteQueryTest.FIRST_PAGING_SPEC).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build(), Druids.newSelectQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimensions(ImmutableList.of("dummy")).metrics(ImmutableList.of("__time", "cnt", "dim1", "dim2", "dim3", "m1", "m2", "unique_dim1")).descending(false).pagingSpec(new PagingSpec(ImmutableMap.of("foo_1970-01-01T00:00:00.000Z_2001-01-03T00:00:00.001Z_1", 5), 1000, true)).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000-01-01"), 1L, "", "a", "[\"a\",\"b\"]", 1.0F, 1.0, BaseCalciteQueryTest.HLLC_STRING }, new Object[]{ BaseCalciteQueryTest.t("2000-01-02"), 1L, "10.1", BaseCalciteQueryTest.NULL_VALUE, "[\"b\",\"c\"]", 2.0F, 2.0, BaseCalciteQueryTest.HLLC_STRING }, new Object[]{ BaseCalciteQueryTest.t("2000-01-03"), 1L, "2", "", "d", 3.0F, 3.0, BaseCalciteQueryTest.HLLC_STRING }, new Object[]{ BaseCalciteQueryTest.t("2001-01-01"), 1L, "1", "a", "", 4.0F, 4.0, BaseCalciteQueryTest.HLLC_STRING }, new Object[]{ BaseCalciteQueryTest.t("2001-01-02"), 1L, "def", "abc", BaseCalciteQueryTest.NULL_VALUE, 5.0F, 5.0, BaseCalciteQueryTest.HLLC_STRING }, new Object[]{ BaseCalciteQueryTest.t("2001-01-03"), 1L, "abc", BaseCalciteQueryTest.NULL_VALUE, BaseCalciteQueryTest.NULL_VALUE, 6.0F, 6.0, BaseCalciteQueryTest.HLLC_STRING }));
    }

    @Test
    public void testSelectSingleColumnTwice() throws Exception {
        testQuery("SELECT dim2 x, dim2 y FROM druid.foo LIMIT 2", ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).columns("dim2").limit(2).resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "a", "a" }, new Object[]{ BaseCalciteQueryTest.NULL_VALUE, BaseCalciteQueryTest.NULL_VALUE }));
    }

    @Test
    public void testSelectSingleColumnWithLimitDescending() throws Exception {
        testQuery("SELECT dim1 FROM druid.foo ORDER BY __time DESC LIMIT 2", ImmutableList.of(Druids.newSelectQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).dimensionSpecs(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d1"))).granularity(ALL).descending(true).dimensions(ImmutableList.of("dummy")).metrics(ImmutableList.of("__time", "dim1")).pagingSpec(BaseCalciteQueryTest.FIRST_PAGING_SPEC).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "abc" }, new Object[]{ "def" }));
    }

    @Test
    public void testGroupBySingleColumnDescendingNoTopN() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, "SELECT dim1 FROM druid.foo GROUP BY dim1 ORDER BY dim1 DESC", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(new GroupByQuery.Builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setGranularity(ALL).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.DESCENDING, StringComparators.LEXICOGRAPHIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "def" }, new Object[]{ "abc" }, new Object[]{ "2" }, new Object[]{ "10.1" }, new Object[]{ "1" }, new Object[]{ "" }));
    }

    @Test
    public void testSelfJoinWithFallback() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_FALLBACK, ("SELECT x.dim1, y.dim1, y.dim2\n" + ((("FROM\n" + "  druid.foo x INNER JOIN druid.foo y ON x.dim1 = y.dim2\n") + "WHERE\n") + "  x.dim1 <> ''")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).columns("dim1").filters(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "", null))).resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build(), BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).columns("dim1", "dim2").resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "abc", "def", "abc" }));
    }

    @Test
    public void testExplainSelfJoinWithFallback() throws Exception {
        String emptyStringEq = (NullHandling.replaceWithDefault()) ? null : "\"\"";
        final String explanation = ((("BindableJoin(condition=[=($0, $2)], joinType=[inner])\n" + "  DruidQueryRel(query=[{\"queryType\":\"scan\",\"dataSource\":{\"type\":\"table\",\"name\":\"foo\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"virtualColumns\":[],\"resultFormat\":\"compactedList\",\"batchSize\":20480,\"limit\":9223372036854775807,\"filter\":{\"type\":\"not\",\"field\":{\"type\":\"selector\",\"dimension\":\"dim1\",\"value\":") + emptyStringEq) + ",\"extractionFn\":null}},\"columns\":[\"dim1\"],\"legacy\":false,\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"},\"descending\":false,\"granularity\":{\"type\":\"all\"}}], signature=[{dim1:STRING}])\n") + "  DruidQueryRel(query=[{\"queryType\":\"scan\",\"dataSource\":{\"type\":\"table\",\"name\":\"foo\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"virtualColumns\":[],\"resultFormat\":\"compactedList\",\"batchSize\":20480,\"limit\":9223372036854775807,\"filter\":null,\"columns\":[\"dim1\",\"dim2\"],\"legacy\":false,\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"},\"descending\":false,\"granularity\":{\"type\":\"all\"}}], signature=[{dim1:STRING, dim2:STRING}])\n";
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_FALLBACK, ("EXPLAIN PLAN FOR\n" + (((("SELECT x.dim1, y.dim1, y.dim2\n" + "FROM\n") + "  druid.foo x INNER JOIN druid.foo y ON x.dim1 = y.dim2\n") + "WHERE\n") + "  x.dim1 <> ''")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(), ImmutableList.of(new Object[]{ explanation }));
    }

    @Test
    public void testGroupByLong() throws Exception {
        testQuery("SELECT cnt, COUNT(*) FROM druid.foo GROUP BY cnt", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("cnt", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, 6L }));
    }

    @Test
    public void testGroupByOrdinal() throws Exception {
        testQuery("SELECT cnt, COUNT(*) FROM druid.foo GROUP BY 1", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("cnt", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, 6L }));
    }

    @Test
    public void testGroupByExpressionAliasedAsOriginalColumnName() throws Exception {
        testQuery(("SELECT\n" + ((("FLOOR(__time TO MONTH) AS __time,\n" + "COUNT(*)\n") + "FROM druid.foo\n") + "GROUP BY FLOOR(__time TO MONTH)")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(MONTH).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000-01-01"), 3L }, new Object[]{ BaseCalciteQueryTest.t("2001-01-01"), 3L }));
    }

    @Test
    public void testGroupByAndOrderByOrdinalOfAlias() throws Exception {
        testQuery("SELECT cnt as theCnt, COUNT(*) FROM druid.foo GROUP BY 1 ORDER BY 1 ASC", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("cnt", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.ASCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, 6L }));
    }

    @Test
    public void testGroupByFloat() throws Exception {
        testQuery("SELECT m1, COUNT(*) FROM druid.foo GROUP BY m1", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("m1", "d0", ValueType.FLOAT))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1.0F, 1L }, new Object[]{ 2.0F, 1L }, new Object[]{ 3.0F, 1L }, new Object[]{ 4.0F, 1L }, new Object[]{ 5.0F, 1L }, new Object[]{ 6.0F, 1L }));
    }

    @Test
    public void testGroupByDouble() throws Exception {
        testQuery("SELECT m2, COUNT(*) FROM druid.foo GROUP BY m2", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("m2", "d0", ValueType.DOUBLE))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1.0, 1L }, new Object[]{ 2.0, 1L }, new Object[]{ 3.0, 1L }, new Object[]{ 4.0, 1L }, new Object[]{ 5.0, 1L }, new Object[]{ 6.0, 1L }));
    }

    @Test
    public void testFilterOnFloat() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE m1 = 1.0", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).filters(BaseCalciteQueryTest.selector("m1", "1.0", null)).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testFilterOnDouble() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE m2 = 1.0", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).filters(BaseCalciteQueryTest.selector("m2", "1.0", null)).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testHavingOnGrandTotal() throws Exception {
        testQuery("SELECT SUM(m1) AS m1_sum FROM foo HAVING m1_sum = 21", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new DoubleSumAggregatorFactory("a0", "m1"))).setHavingSpec(BaseCalciteQueryTest.having(BaseCalciteQueryTest.numeric_Selector("a0", "21", null))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 21.0 }));
    }

    @Test
    public void testHavingOnDoubleSum() throws Exception {
        testQuery("SELECT dim1, SUM(m1) AS m1_sum FROM druid.foo GROUP BY dim1 HAVING SUM(m1) > 1", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new DoubleSumAggregatorFactory("a0", "m1"))).setHavingSpec(BaseCalciteQueryTest.having(new org.apache.druid.query.filter.BoundDimFilter("a0", "1", null, true, false, false, null, StringComparators.NUMERIC))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "1", 4.0 }, new Object[]{ "10.1", 2.0 }, new Object[]{ "2", 3.0 }, new Object[]{ "abc", 6.0 }, new Object[]{ "def", 5.0 }));
    }

    @Test
    public void testHavingOnApproximateCountDistinct() throws Exception {
        testQuery("SELECT dim2, COUNT(DISTINCT m1) FROM druid.foo GROUP BY dim2 HAVING COUNT(DISTINCT m1) > 1", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a0", null, ImmutableList.of(new DefaultDimensionSpec("m1", "m1", ValueType.FLOAT)), false, true))).setHavingSpec(BaseCalciteQueryTest.having(BaseCalciteQueryTest.bound("a0", "1", null, true, false, null, NUMERIC))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "", 3L }, new Object[]{ "a", 2L }) : ImmutableList.of(new Object[]{ null, 2L }, new Object[]{ "a", 2L })));
    }

    @Test
    public void testHavingOnExactCountDistinct() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_NO_HLL, "SELECT dim2, COUNT(DISTINCT m1) FROM druid.foo GROUP BY dim2 HAVING COUNT(DISTINCT m1) > 1", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0", ValueType.STRING), new DefaultDimensionSpec("m1", "d1", ValueType.FLOAT))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0", "_d0", ValueType.STRING))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setHavingSpec(BaseCalciteQueryTest.having(BaseCalciteQueryTest.bound("a0", "1", null, true, false, null, NUMERIC))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "", 3L }, new Object[]{ "a", 2L }) : ImmutableList.of(new Object[]{ null, 2L }, new Object[]{ "a", 2L })));
    }

    @Test
    public void testHavingOnFloatSum() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_FALLBACK, "SELECT dim1, CASt(SUM(m1) AS FLOAT) AS m1_sum FROM druid.foo GROUP BY dim1 HAVING CAST(SUM(m1) AS FLOAT) > 1", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new DoubleSumAggregatorFactory("a0", "m1"))).setHavingSpec(BaseCalciteQueryTest.having(new org.apache.druid.query.filter.BoundDimFilter("a0", "1", null, true, false, false, null, StringComparators.NUMERIC))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "1", 4.0F }, new Object[]{ "10.1", 2.0F }, new Object[]{ "2", 3.0F }, new Object[]{ "abc", 6.0F }, new Object[]{ "def", 5.0F }));
    }

    @Test
    public void testColumnComparison() throws Exception {
        testQuery("SELECT dim1, m1, COUNT(*) FROM druid.foo WHERE m1 - 1 = dim1 GROUP BY dim1, m1", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.expressionFilter("((\"m1\" - 1) == \"dim1\")")).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"), new DefaultDimensionSpec("m1", "d1", ValueType.FLOAT))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "", 1.0F, 1L }, new Object[]{ "2", 3.0F, 1L }) : ImmutableList.of(new Object[]{ "2", 3.0F, 1L })));
    }

    @Test
    public void testHavingOnRatio() throws Exception {
        // Test for https://github.com/apache/incubator-druid/issues/4264
        testQuery(("SELECT\n" + (((("  dim1,\n" + "  COUNT(*) FILTER(WHERE dim2 <> \'a\')/COUNT(*) as ratio\n") + "FROM druid.foo\n") + "GROUP BY dim1\n") + "HAVING COUNT(*) FILTER(WHERE dim2 <> 'a')/COUNT(*) = 1")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a0"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", "a", null))), new CountAggregatorFactory("a1"))).setPostAggregatorSpecs(ImmutableList.of(BaseCalciteQueryTest.expresionPostAgg("p0", "(\"a0\" / \"a1\")"))).setHavingSpec(BaseCalciteQueryTest.having(BaseCalciteQueryTest.expressionFilter("((\"a0\" / \"a1\") == 1)"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "10.1", 1L }, new Object[]{ "2", 1L }, new Object[]{ "abc", 1L }, new Object[]{ "def", 1L }));
    }

    @Test
    public void testGroupByWithSelectProjections() throws Exception {
        testQuery(("SELECT\n" + ((("  dim1," + "  SUBSTRING(dim1, 2)\n") + "FROM druid.foo\n") + "GROUP BY dim1\n")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setPostAggregatorSpecs(ImmutableList.of(BaseCalciteQueryTest.expresionPostAgg("p0", "substring(\"d0\", 1, -1)"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "", BaseCalciteQueryTest.NULL_VALUE }, new Object[]{ "1", BaseCalciteQueryTest.NULL_VALUE }, new Object[]{ "10.1", "0.1" }, new Object[]{ "2", BaseCalciteQueryTest.NULL_VALUE }, new Object[]{ "abc", "bc" }, new Object[]{ "def", "ef" }));
    }

    @Test
    public void testGroupByWithSelectAndOrderByProjections() throws Exception {
        testQuery(("SELECT\n" + (((("  dim1," + "  SUBSTRING(dim1, 2)\n") + "FROM druid.foo\n") + "GROUP BY dim1\n") + "ORDER BY CHARACTER_LENGTH(dim1) DESC, dim1")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setPostAggregatorSpecs(ImmutableList.of(BaseCalciteQueryTest.expresionPostAgg("p0", "substring(\"d0\", 1, -1)"), BaseCalciteQueryTest.expresionPostAgg("p1", "strlen(\"d0\")"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("p1", Direction.DESCENDING, StringComparators.NUMERIC), new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.ASCENDING, StringComparators.LEXICOGRAPHIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "10.1", "0.1" }, new Object[]{ "abc", "bc" }, new Object[]{ "def", "ef" }, new Object[]{ "1", BaseCalciteQueryTest.NULL_VALUE }, new Object[]{ "2", BaseCalciteQueryTest.NULL_VALUE }, new Object[]{ "", BaseCalciteQueryTest.NULL_VALUE }));
    }

    @Test
    public void testTopNWithSelectProjections() throws Exception {
        testQuery(("SELECT\n" + (((("  dim1," + "  SUBSTRING(dim1, 2)\n") + "FROM druid.foo\n") + "GROUP BY dim1\n") + "LIMIT 10")), ImmutableList.of(new TopNQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimension(new DefaultDimensionSpec("dim1", "d0")).postAggregators(ImmutableList.of(BaseCalciteQueryTest.expresionPostAgg("p0", "substring(\"d0\", 1, -1)"))).metric(new org.apache.druid.query.topn.DimensionTopNMetricSpec(null, StringComparators.LEXICOGRAPHIC)).threshold(10).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "", BaseCalciteQueryTest.NULL_VALUE }, new Object[]{ "1", BaseCalciteQueryTest.NULL_VALUE }, new Object[]{ "10.1", "0.1" }, new Object[]{ "2", BaseCalciteQueryTest.NULL_VALUE }, new Object[]{ "abc", "bc" }, new Object[]{ "def", "ef" }));
    }

    @Test
    public void testTopNWithSelectAndOrderByProjections() throws Exception {
        testQuery(("SELECT\n" + ((((("  dim1," + "  SUBSTRING(dim1, 2)\n") + "FROM druid.foo\n") + "GROUP BY dim1\n") + "ORDER BY CHARACTER_LENGTH(dim1) DESC\n") + "LIMIT 10")), ImmutableList.of(new TopNQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimension(new DefaultDimensionSpec("dim1", "d0")).postAggregators(ImmutableList.of(BaseCalciteQueryTest.expresionPostAgg("p0", "substring(\"d0\", 1, -1)"), BaseCalciteQueryTest.expresionPostAgg("p1", "strlen(\"d0\")"))).metric(new NumericTopNMetricSpec("p1")).threshold(10).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "10.1", "0.1" }, new Object[]{ "abc", "bc" }, new Object[]{ "def", "ef" }, new Object[]{ "1", BaseCalciteQueryTest.NULL_VALUE }, new Object[]{ "2", BaseCalciteQueryTest.NULL_VALUE }, new Object[]{ "", BaseCalciteQueryTest.NULL_VALUE }));
    }

    @Test
    public void testUnionAll() throws Exception {
        testQuery("SELECT COUNT(*) FROM foo UNION ALL SELECT SUM(cnt) FROM foo UNION ALL SELECT COUNT(*) FROM foo", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build(), Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build(), Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }, new Object[]{ 6L }, new Object[]{ 6L }));
    }

    @Test
    public void testUnionAllWithLimit() throws Exception {
        testQuery(("SELECT * FROM (" + ("SELECT COUNT(*) FROM foo UNION ALL SELECT SUM(cnt) FROM foo UNION ALL SELECT COUNT(*) FROM foo" + ") LIMIT 2")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build(), Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }, new Object[]{ 6L }));
    }

    @Test
    public void testPruneDeadAggregators() throws Exception {
        // Test for ProjectAggregatePruneUnusedCallRule.
        testQuery(("SELECT\n" + ((((("  CASE \'foo\'\n" + "  WHEN \'bar\' THEN SUM(cnt)\n") + "  WHEN \'foo\' THEN SUM(m1)\n") + "  WHEN \'baz\' THEN SUM(m2)\n") + "  END\n") + "FROM foo")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new DoubleSumAggregatorFactory("a0", "m1"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 21.0 }));
    }

    @Test
    public void testPruneDeadAggregatorsThroughPostProjection() throws Exception {
        // Test for ProjectAggregatePruneUnusedCallRule.
        testQuery(("SELECT\n" + ((((("  CASE \'foo\'\n" + "  WHEN \'bar\' THEN SUM(cnt) / 10\n") + "  WHEN \'foo\' THEN SUM(m1) / 10\n") + "  WHEN \'baz\' THEN SUM(m2) / 10\n") + "  END\n") + "FROM foo")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new DoubleSumAggregatorFactory("a0", "m1"))).postAggregators(ImmutableList.of(BaseCalciteQueryTest.expresionPostAgg("p0", "(\"a0\" / 10)"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2.1 }));
    }

    @Test
    public void testPruneDeadAggregatorsThroughHaving() throws Exception {
        // Test for ProjectAggregatePruneUnusedCallRule.
        testQuery(("SELECT\n" + (((((("  CASE \'foo\'\n" + "  WHEN \'bar\' THEN SUM(cnt)\n") + "  WHEN \'foo\' THEN SUM(m1)\n") + "  WHEN \'baz\' THEN SUM(m2)\n") + "  END AS theCase\n") + "FROM foo\n") + "HAVING theCase = 21")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new DoubleSumAggregatorFactory("a0", "m1"))).setHavingSpec(BaseCalciteQueryTest.having(BaseCalciteQueryTest.numeric_Selector("a0", "21", null))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 21.0 }));
    }

    @Test
    public void testGroupByCaseWhen() throws Exception {
        testQuery(("SELECT\n" + (((((((((((("  CASE EXTRACT(DAY FROM __time)\n" + "    WHEN m1 THEN \'match-m1\'\n") + "    WHEN cnt THEN \'match-cnt\'\n") + "    WHEN 0 THEN 'zero'") + "    END,") + "  COUNT(*)\n") + "FROM druid.foo\n") + "GROUP BY") + "  CASE EXTRACT(DAY FROM __time)\n") + "    WHEN m1 THEN \'match-m1\'\n") + "    WHEN cnt THEN \'match-cnt\'\n") + "    WHEN 0 THEN 'zero'") + "    END")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", ((("case_searched(" + ((((("(CAST(timestamp_extract(\"__time\",\'DAY\',\'UTC\'), \'DOUBLE\') == \"m1\")," + "'match-m1',") + "(timestamp_extract(\"__time\",\'DAY\',\'UTC\') == \"cnt\"),") + "'match-cnt',") + "(timestamp_extract(\"__time\",\'DAY\',\'UTC\') == 0),") + "'zero',")) + (DruidExpression.nullLiteral())) + ")"), STRING)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ NullHandling.defaultStringValue(), 2L }, new Object[]{ "match-cnt", 1L }, new Object[]{ "match-m1", 3L }));
    }

    @Test
    public void testGroupByCaseWhenOfTripleAnd() throws Exception {
        testQuery(("SELECT\n" + ((("  CASE WHEN m1 > 1 AND m1 < 5 AND cnt = 1 THEN 'x' ELSE NULL END," + "  COUNT(*)\n") + "FROM druid.foo\n") + "GROUP BY 1")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "case_searched(((\"m1\" > 1) && (\"m1\" < 5) && (\"cnt\" == 1)),\'x\',null)", STRING)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ NullHandling.defaultStringValue(), 3L }, new Object[]{ "x", 3L }));
    }

    @Test
    public void testNullEmptyStringEquality() throws Exception {
        testQuery(("SELECT COUNT(*)\n" + ("FROM druid.foo\n" + "WHERE NULLIF(dim2, 'a') IS NULL")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.expressionFilter("case_searched((\"dim2\" == \'a\'),1,isnull(\"dim2\"))")).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of((NullHandling.replaceWithDefault() ? // Matches everything but "abc"
        new Object[]{ 5L } : // match only null values
        new Object[]{ 4L })));
    }

    @Test
    public void testEmptyStringEquality() throws Exception {
        testQuery(("SELECT COUNT(*)\n" + ("FROM druid.foo\n" + "WHERE NULLIF(dim2, 'a') = ''")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.expressionFilter((("case_searched((\"dim2\" == \'a\')," + (NullHandling.replaceWithDefault() ? "1" : "0")) + ",(\"dim2\" == \'\'))"))).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of((NullHandling.replaceWithDefault() ? // Matches everything but "abc"
        new Object[]{ 5L } : // match only empty string
        new Object[]{ 1L })));
    }

    @Test
    public void testNullStringEquality() throws Exception {
        testQuery(("SELECT COUNT(*)\n" + ("FROM druid.foo\n" + "WHERE NULLIF(dim2, 'a') = null")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.expressionFilter((("case_searched((\"dim2\" == \'a\')," + (NullHandling.replaceWithDefault() ? "1" : "0")) + ",(\"dim2\" == null))"))).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? // Matches everything but "abc"
        ImmutableList.of(new Object[]{ 5L }) : // null is not eqaual to null or any other value
        ImmutableList.of()));
    }

    @Test
    public void testCoalesceColumns() throws Exception {
        // Doesn't conform to the SQL standard, but it's how we do it.
        // This example is used in the sql.md doc.
        testQuery("SELECT COALESCE(dim2, dim1), COUNT(*) FROM druid.foo GROUP BY COALESCE(dim2, dim1)\n", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "case_searched(notnull(\"dim2\"),\"dim2\",\"dim1\")", STRING)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.STRING))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "10.1", 1L }, new Object[]{ "2", 1L }, new Object[]{ "a", 2L }, new Object[]{ "abc", 2L }) : ImmutableList.of(new Object[]{ "", 1L }, new Object[]{ "10.1", 1L }, new Object[]{ "a", 2L }, new Object[]{ "abc", 2L })));
    }

    @Test
    public void testColumnIsNull() throws Exception {
        // Doesn't conform to the SQL standard, but it's how we do it.
        // This example is used in the sql.md doc.
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE dim2 IS NULL\n", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.selector("dim2", null, null)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ NullHandling.replaceWithDefault() ? 3L : 2L }));
    }

    @Test
    public void testUnplannableQueries() {
        // All of these queries are unplannable because they rely on features Druid doesn't support.
        // This test is here to confirm that we don't fall back to Calcite's interpreter or enumerable implementation.
        // It's also here so when we do support these features, we can have "real" tests for these queries.
        final List<String> queries = // SELECT query with order by
        // Self-join
        // DISTINCT with OFFSET
        // NOT IN subquery
        // AND of two IN subqueries
        ImmutableList.of("SELECT dim1 FROM druid.foo ORDER BY dim1", "SELECT COUNT(*) FROM druid.foo x, druid.foo y", "SELECT DISTINCT dim2 FROM druid.foo ORDER BY dim2 LIMIT 2 OFFSET 5", "SELECT COUNT(*) FROM foo WHERE dim1 NOT IN (SELECT dim1 FROM foo WHERE dim2 = 'a')", ("EXPLAIN PLAN FOR SELECT COUNT(*) FROM foo WHERE dim1 IN (SELECT dim1 FROM foo WHERE dim2 = \'a\')\n" + "AND dim1 IN (SELECT dim1 FROM foo WHERE m2 > 2)"));
        for (final String query : queries) {
            assertQueryIsUnplannable(query);
        }
    }

    @Test
    public void testUnplannableExactCountDistinctQueries() {
        // All of these queries are unplannable in exact COUNT DISTINCT mode.
        final List<String> queries = // two COUNT DISTINCTs, same query
        // two COUNT DISTINCTs
        // COUNT DISTINCT on sketch cannot be exact
        ImmutableList.of("SELECT COUNT(distinct dim1), COUNT(distinct dim2) FROM druid.foo", "SELECT dim1, COUNT(distinct dim1), COUNT(distinct dim2) FROM druid.foo GROUP BY dim1", "SELECT COUNT(distinct unique_dim1) FROM druid.foo");
        for (final String query : queries) {
            assertQueryIsUnplannable(BaseCalciteQueryTest.PLANNER_CONFIG_NO_HLL, query);
        }
    }

    @Test
    public void testSelectStarWithDimFilter() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT_NO_COMPLEX_SERDE, BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT, "SELECT * FROM druid.foo WHERE dim1 > 'd' OR dim2 = 'a'", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.or(BaseCalciteQueryTest.bound("dim1", "d", null, true, false, null, LEXICOGRAPHIC), BaseCalciteQueryTest.selector("dim2", "a", null))).columns("__time", "cnt", "dim1", "dim2", "dim3", "m1", "m2", "unique_dim1").resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000-01-01"), 1L, "", "a", "[\"a\",\"b\"]", 1.0F, 1.0, BaseCalciteQueryTest.HLLC_STRING }, new Object[]{ BaseCalciteQueryTest.t("2001-01-01"), 1L, "1", "a", "", 4.0F, 4.0, BaseCalciteQueryTest.HLLC_STRING }, new Object[]{ BaseCalciteQueryTest.t("2001-01-02"), 1L, "def", "abc", BaseCalciteQueryTest.NULL_VALUE, 5.0F, 5.0, BaseCalciteQueryTest.HLLC_STRING }));
    }

    @Test
    public void testGroupByNothingWithLiterallyFalseFilter() throws Exception {
        testQuery("SELECT COUNT(*), MAX(cnt) FROM druid.foo WHERE 1 = 0", ImmutableList.of(), ImmutableList.of(new Object[]{ 0L, null }));
    }

    @Test
    public void testGroupByOneColumnWithLiterallyFalseFilter() throws Exception {
        testQuery("SELECT COUNT(*), MAX(cnt) FROM druid.foo WHERE 1 = 0 GROUP BY dim1", ImmutableList.of(), ImmutableList.of());
    }

    @Test
    public void testGroupByWithFilterMatchingNothing() throws Exception {
        // This query should actually return [0, null] rather than an empty result set, but it doesn't.
        // This test just "documents" the current behavior.
        testQuery("SELECT COUNT(*), MAX(cnt) FROM druid.foo WHERE dim1 = 'foobar'", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.selector("dim1", "foobar", null)).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"), new LongMaxAggregatorFactory("a1", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of());
    }

    @Test
    public void testGroupByWithFilterMatchingNothingWithGroupByLiteral() throws Exception {
        testQuery("SELECT COUNT(*), MAX(cnt) FROM druid.foo WHERE dim1 = 'foobar' GROUP BY 'dummy'", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.selector("dim1", "foobar", null)).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"), new LongMaxAggregatorFactory("a1", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of());
    }

    @Test
    public void testCountNonNullColumn() throws Exception {
        testQuery("SELECT COUNT(cnt) FROM druid.foo", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }));
    }

    @Test
    public void testCountNullableColumn() throws Exception {
        testQuery("SELECT COUNT(dim2) FROM druid.foo", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a0"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", null, null))))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 3L }) : ImmutableList.of(new Object[]{ 4L })));
    }

    @Test
    public void testCountNullableExpression() throws Exception {
        testQuery("SELECT COUNT(CASE WHEN dim2 = 'abc' THEN 'yes' WHEN dim2 = 'def' THEN 'yes' END) FROM druid.foo", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a0"), BaseCalciteQueryTest.expressionFilter((("notnull(case_searched((\"dim2\" == \'abc\'),\'yes\',(\"dim2\" == \'def\'),\'yes\'," + (DruidExpression.nullLiteral())) + "))"))))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testCountStar() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }));
    }

    @Test
    public void testCountStarOnCommonTableExpression() throws Exception {
        testQuery(("WITH beep (dim1_firstchar) AS (SELECT SUBSTRING(dim1, 1, 1) FROM foo WHERE dim2 = \'a\')\n" + "SELECT COUNT(*) FROM beep WHERE dim1_firstchar <> 'z'"), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.and(BaseCalciteQueryTest.selector("dim2", "a", null), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "z", new SubstringDimExtractionFn(0, 1))))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2L }));
    }

    @Test
    public void testCountStarOnView() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.aview WHERE dim1_firstchar <> 'z'", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.and(BaseCalciteQueryTest.selector("dim2", "a", null), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "z", new SubstringDimExtractionFn(0, 1))))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2L }));
    }

    @Test
    public void testExplainCountStarOnView() throws Exception {
        final String explanation = "DruidQueryRel(query=[{" + ((((((((((("\"queryType\":\"timeseries\"," + "\"dataSource\":{\"type\":\"table\",\"name\":\"foo\"},") + "\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},") + "\"descending\":false,") + "\"virtualColumns\":[],") + "\"filter\":{\"type\":\"and\",\"fields\":[{\"type\":\"selector\",\"dimension\":\"dim2\",\"value\":\"a\",\"extractionFn\":null},{\"type\":\"not\",\"field\":{\"type\":\"selector\",\"dimension\":\"dim1\",\"value\":\"z\",\"extractionFn\":{\"type\":\"substring\",\"index\":0,\"length\":1}}}]},") + "\"granularity\":{\"type\":\"all\"},") + "\"aggregations\":[{\"type\":\"count\",\"name\":\"a0\"}],") + "\"postAggregations\":[],") + "\"limit\":2147483647,") + "\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"skipEmptyBuckets\":true,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"}}]") + ", signature=[{a0:LONG}])\n");
        testQuery("EXPLAIN PLAN FOR SELECT COUNT(*) FROM aview WHERE dim1_firstchar <> 'z'", ImmutableList.of(), ImmutableList.of(new Object[]{ explanation }));
    }

    @Test
    public void testCountStarWithLikeFilter() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE dim1 like 'a%' OR dim2 like '%xb%' escape 'x'", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.or(new LikeDimFilter("dim1", "a%", null, null), new LikeDimFilter("dim2", "%xb%", "x", null))).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2L }));
    }

    @Test
    public void testCountStarWithLongColumnFilters() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE cnt >= 3 OR cnt = 1", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.or(BaseCalciteQueryTest.bound("cnt", "3", null, false, false, null, NUMERIC), BaseCalciteQueryTest.selector("cnt", "1", null))).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }));
    }

    @Test
    public void testCountStarWithLongColumnFiltersOnFloatLiterals() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE cnt > 1.1 and cnt < 100000001.0", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.bound("cnt", "1.1", "100000001.0", true, true, null, NUMERIC)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of());
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE cnt = 1.0", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.selector("cnt", "1.0", null)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }));
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE cnt = 100000001.0", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.selector("cnt", "100000001.0", null)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of());
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE cnt = 1.0 or cnt = 100000001.0", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.in("cnt", ImmutableList.of("1.0", "100000001.0"), null)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }));
    }

    @Test
    public void testCountStarWithLongColumnFiltersOnTwoPoints() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE cnt = 1 OR cnt = 2", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.in("cnt", ImmutableList.of("1", "2"), null)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }));
    }

    @Test
    public void testFilterOnStringAsNumber() throws Exception {
        testQuery(("SELECT distinct dim1 FROM druid.foo WHERE " + ("dim1 = 10 OR " + "(floor(CAST(dim1 AS float)) = 10.00 and CAST(dim1 AS float) > 9 and CAST(dim1 AS float) <= 10.5)")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setDimFilter(BaseCalciteQueryTest.or(BaseCalciteQueryTest.selector("dim1", "10", null), BaseCalciteQueryTest.and(BaseCalciteQueryTest.expressionFilter("(floor(CAST(\"dim1\", \'DOUBLE\')) == 10.00)"), BaseCalciteQueryTest.bound("dim1", "9", "10.5", true, false, null, NUMERIC)))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "10.1" }));
    }

    @Test
    public void testSimpleAggregations() throws Exception {
        testQuery("SELECT COUNT(*), COUNT(cnt), COUNT(dim1), AVG(cnt), SUM(cnt), SUM(cnt) + MIN(cnt) + MAX(cnt), COUNT(dim2) FROM druid.foo", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a1"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", null, null))), new LongSumAggregatorFactory("a2:sum", "cnt"), new CountAggregatorFactory("a2:count"), new LongSumAggregatorFactory("a3", "cnt"), new LongMinAggregatorFactory("a4", "cnt"), new LongMaxAggregatorFactory("a5", "cnt"), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a6"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", null, null))))).postAggregators(new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("a2", "quotient", ImmutableList.of(new FieldAccessPostAggregator(null, "a2:sum"), new FieldAccessPostAggregator(null, "a2:count"))), BaseCalciteQueryTest.expresionPostAgg("p0", "((\"a3\" + \"a4\") + \"a5\")")).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 6L, 6L, 5L, 1L, 6L, 8L, 3L }) : ImmutableList.of(new Object[]{ 6L, 6L, 6L, 1L, 6L, 8L, 4L })));
    }

    @Test
    public void testGroupByWithSortOnPostAggregationDefault() throws Exception {
        // By default this query uses topN.
        testQuery("SELECT dim1, MIN(m1) + MAX(m1) AS x FROM druid.foo GROUP BY dim1 ORDER BY x LIMIT 3", ImmutableList.of(new TopNQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimension(new DefaultDimensionSpec("dim1", "d0")).metric(new InvertedTopNMetricSpec(new NumericTopNMetricSpec("p0"))).aggregators(BaseCalciteQueryTest.aggregators(new FloatMinAggregatorFactory("a0", "m1"), new FloatMaxAggregatorFactory("a1", "m1"))).postAggregators(ImmutableList.of(BaseCalciteQueryTest.expresionPostAgg("p0", "(\"a0\" + \"a1\")"))).threshold(3).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "", 2.0F }, new Object[]{ "10.1", 4.0F }, new Object[]{ "2", 6.0F }));
    }

    @Test
    public void testGroupByWithSortOnPostAggregationNoTopNConfig() throws Exception {
        // Use PlannerConfig to disable topN, so this query becomes a groupBy.
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_NO_TOPN, "SELECT dim1, MIN(m1) + MAX(m1) AS x FROM druid.foo GROUP BY dim1 ORDER BY x LIMIT 3", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setAggregatorSpecs(new FloatMinAggregatorFactory("a0", "m1"), new FloatMaxAggregatorFactory("a1", "m1")).setPostAggregatorSpecs(ImmutableList.of(BaseCalciteQueryTest.expresionPostAgg("p0", "(\"a0\" + \"a1\")"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("p0", Direction.ASCENDING, StringComparators.NUMERIC)), 3)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "", 2.0F }, new Object[]{ "10.1", 4.0F }, new Object[]{ "2", 6.0F }));
    }

    @Test
    public void testGroupByWithSortOnPostAggregationNoTopNContext() throws Exception {
        // Use context to disable topN, so this query becomes a groupBy.
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, BaseCalciteQueryTest.QUERY_CONTEXT_NO_TOPN, "SELECT dim1, MIN(m1) + MAX(m1) AS x FROM druid.foo GROUP BY dim1 ORDER BY x LIMIT 3", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setAggregatorSpecs(new FloatMinAggregatorFactory("a0", "m1"), new FloatMaxAggregatorFactory("a1", "m1")).setPostAggregatorSpecs(ImmutableList.of(BaseCalciteQueryTest.expresionPostAgg("p0", "(\"a0\" + \"a1\")"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("p0", Direction.ASCENDING, StringComparators.NUMERIC)), 3)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_NO_TOPN).build()), ImmutableList.of(new Object[]{ "", 2.0F }, new Object[]{ "10.1", 4.0F }, new Object[]{ "2", 6.0F }));
    }

    @Test
    public void testFilteredAggregations() throws Exception {
        testQuery(("SELECT " + (((((((((((("SUM(case dim1 when 'abc' then cnt end), " + "SUM(case dim1 when 'abc' then null else cnt end), ") + "SUM(case substring(dim1, 1, 1) when 'a' then cnt end), ") + "COUNT(dim2) filter(WHERE dim1 <> '1'), ") + "COUNT(CASE WHEN dim1 <> '1' THEN 'dummy' END), ") + "SUM(CASE WHEN dim1 <> '1' THEN 1 ELSE 0 END), ") + "SUM(cnt) filter(WHERE dim2 = 'a'), ") + "SUM(case when dim1 <> '1' then cnt end) filter(WHERE dim2 = 'a'), ") + "SUM(CASE WHEN dim1 <> '1' THEN cnt ELSE 0 END), ") + "MAX(CASE WHEN dim1 <> '1' THEN cnt END), ") + "COUNT(DISTINCT CASE WHEN dim1 <> '1' THEN m1 END), ") + "SUM(cnt) filter(WHERE dim2 = 'a' AND dim1 = 'b') ") + "FROM druid.foo")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new LongSumAggregatorFactory("a0", "cnt"), BaseCalciteQueryTest.selector("dim1", "abc", null)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new LongSumAggregatorFactory("a1", "cnt"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "abc", null))), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new LongSumAggregatorFactory("a2", "cnt"), BaseCalciteQueryTest.selector("dim1", "a", new SubstringDimExtractionFn(0, 1))), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a3"), BaseCalciteQueryTest.and(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", null, null)), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "1", null)))), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a4"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "1", null))), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a5"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "1", null))), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new LongSumAggregatorFactory("a6", "cnt"), BaseCalciteQueryTest.selector("dim2", "a", null)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new LongSumAggregatorFactory("a7", "cnt"), BaseCalciteQueryTest.and(BaseCalciteQueryTest.selector("dim2", "a", null), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "1", null)))), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new LongSumAggregatorFactory("a8", "cnt"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "1", null))), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new LongMaxAggregatorFactory("a9", "cnt"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "1", null))), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a10", null, BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("m1", "m1", ValueType.FLOAT)), false, true), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "1", null))), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new LongSumAggregatorFactory("a11", "cnt"), BaseCalciteQueryTest.and(BaseCalciteQueryTest.selector("dim2", "a", null), BaseCalciteQueryTest.selector("dim1", "b", null))))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 1L, 5L, 1L, 2L, 5L, 5L, 2L, 1L, 5L, 1L, 5L, 0L }) : ImmutableList.of(new Object[]{ 1L, 5L, 1L, 3L, 5L, 5L, 2L, 1L, 5L, 1L, 5L, null })));
    }

    @Test
    public void testCaseFilteredAggregationWithGroupBy() throws Exception {
        testQuery(("SELECT\n" + ((("  cnt,\n" + "  SUM(CASE WHEN dim1 <> \'1\' THEN 1 ELSE 0 END) + SUM(cnt)\n") + "FROM druid.foo\n") + "GROUP BY cnt")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("cnt", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a0"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "1", null))), new LongSumAggregatorFactory("a1", "cnt"))).setPostAggregatorSpecs(ImmutableList.of(BaseCalciteQueryTest.expresionPostAgg("p0", "(\"a0\" + \"a1\")"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, 11L }));
    }

    @Test
    public void testFilteredAggregationWithNotIn() throws Exception {
        testQuery(("SELECT\n" + (("COUNT(*) filter(WHERE dim1 NOT IN (\'1\')),\n" + "COUNT(dim2) filter(WHERE dim1 NOT IN (\'1\'))\n") + "FROM druid.foo")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a0"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "1", null))), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a1"), BaseCalciteQueryTest.and(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", null, null)), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "1", null)))))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 5L, 2L }) : ImmutableList.of(new Object[]{ 5L, 3L })));
    }

    @Test
    public void testExpressionAggregations() throws Exception {
        final ExprMacroTable macroTable = CalciteTests.createExprMacroTable();
        testQuery(("SELECT\n" + ((((("  SUM(cnt * 3),\n" + "  LN(SUM(cnt) + SUM(m1)),\n") + "  MOD(SUM(cnt), 4),\n") + "  SUM(CHARACTER_LENGTH(CAST(cnt * 10 AS VARCHAR))),\n") + "  MAX(CHARACTER_LENGTH(dim2) + LN(m1))\n") + "FROM druid.foo")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", null, "(\"cnt\" * 3)", macroTable), new LongSumAggregatorFactory("a1", "cnt"), new DoubleSumAggregatorFactory("a2", "m1"), new LongSumAggregatorFactory("a3", null, "strlen(CAST((\"cnt\" * 10), \'STRING\'))", macroTable), new org.apache.druid.query.aggregation.DoubleMaxAggregatorFactory("a4", null, "(strlen(\"dim2\") + log(\"m1\"))", macroTable))).postAggregators(BaseCalciteQueryTest.expresionPostAgg("p0", "log((\"a1\" + \"a2\"))"), BaseCalciteQueryTest.expresionPostAgg("p1", "(\"a1\" % 4)")).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 18L, 3.295836866004329, 2, 12L, 3.0F + (Math.log(5.0)) }));
    }

    @Test
    public void testExpressionFilteringAndGrouping() throws Exception {
        testQuery(("SELECT\n" + ((((("  FLOOR(m1 / 2) * 2,\n" + "  COUNT(*)\n") + "FROM druid.foo\n") + "WHERE FLOOR(m1 / 2) * 2 > -1\n") + "GROUP BY FLOOR(m1 / 2) * 2\n") + "ORDER BY 1 DESC")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "(floor((\"m1\" / 2)) * 2)", FLOAT)).setDimFilter(BaseCalciteQueryTest.expressionFilter("((floor((\"m1\" / 2)) * 2) > -1)")).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.FLOAT))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.DESCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6.0F, 1L }, new Object[]{ 4.0F, 2L }, new Object[]{ 2.0F, 2L }, new Object[]{ 0.0F, 1L }));
    }

    @Test
    public void testExpressionFilteringAndGroupingUsingCastToLong() throws Exception {
        testQuery(("SELECT\n" + ((((("  CAST(m1 AS BIGINT) / 2 * 2,\n" + "  COUNT(*)\n") + "FROM druid.foo\n") + "WHERE CAST(m1 AS BIGINT) / 2 * 2 > -1\n") + "GROUP BY CAST(m1 AS BIGINT) / 2 * 2\n") + "ORDER BY 1 DESC")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "((CAST(\"m1\", \'LONG\') / 2) * 2)", LONG)).setDimFilter(BaseCalciteQueryTest.expressionFilter("(((CAST(\"m1\", \'LONG\') / 2) * 2) > -1)")).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.DESCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L, 1L }, new Object[]{ 4L, 2L }, new Object[]{ 2L, 2L }, new Object[]{ 0L, 1L }));
    }

    @Test
    public void testExpressionFilteringAndGroupingOnStringCastToNumber() throws Exception {
        testQuery(("SELECT\n" + ((((("  FLOOR(CAST(dim1 AS FLOAT) / 2) * 2,\n" + "  COUNT(*)\n") + "FROM druid.foo\n") + "WHERE FLOOR(CAST(dim1 AS FLOAT) / 2) * 2 > -1\n") + "GROUP BY FLOOR(CAST(dim1 AS FLOAT) / 2) * 2\n") + "ORDER BY 1 DESC")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "(floor((CAST(\"dim1\", \'DOUBLE\') / 2)) * 2)", FLOAT)).setDimFilter(BaseCalciteQueryTest.expressionFilter("((floor((CAST(\"dim1\", \'DOUBLE\') / 2)) * 2) > -1)")).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.FLOAT))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.DESCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 10.0F, 1L }, new Object[]{ 2.0F, 1L }, new Object[]{ 0.0F, 4L }) : ImmutableList.of(new Object[]{ 10.0F, 1L }, new Object[]{ 2.0F, 1L }, new Object[]{ 0.0F, 1L })));
    }

    @Test
    public void testInFilter() throws Exception {
        testQuery("SELECT dim1, COUNT(*) FROM druid.foo WHERE dim1 IN ('abc', 'def', 'ghi') GROUP BY dim1", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setDimFilter(new InDimFilter("dim1", ImmutableList.of("abc", "def", "ghi"), null)).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "abc", 1L }, new Object[]{ "def", 1L }));
    }

    @Test
    public void testInFilterWith23Elements() throws Exception {
        // Regression test for https://github.com/apache/incubator-druid/issues/4203.
        final List<String> elements = new ArrayList<>();
        elements.add("abc");
        elements.add("def");
        elements.add("ghi");
        for (int i = 0; i < 20; i++) {
            elements.add(("dummy" + i));
        }
        final String elementsString = Joiner.on(",").join(elements.stream().map(( s) -> ("'" + s) + "'").iterator());
        testQuery((("SELECT dim1, COUNT(*) FROM druid.foo WHERE dim1 IN (" + elementsString) + ") GROUP BY dim1"), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setDimFilter(new InDimFilter("dim1", elements, null)).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "abc", 1L }, new Object[]{ "def", 1L }));
    }

    @Test
    public void testCountStarWithDegenerateFilter() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE dim2 = 'a' and (dim1 > 'a' OR dim1 < 'b')", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.and(BaseCalciteQueryTest.selector("dim2", "a", null), BaseCalciteQueryTest.or(BaseCalciteQueryTest.bound("dim1", "a", null, true, false, null, LEXICOGRAPHIC), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", null, null))))).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ NullHandling.sqlCompatible() ? 2L : 1L }));
    }

    @Test
    public void testCountStarWithNotOfDegenerateFilter() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE dim2 = 'a' and not (dim1 > 'a' OR dim1 < 'b')", ImmutableList.of(), ImmutableList.of(new Object[]{ 0L }));
    }

    @Test
    public void testCountStarWithBoundFilterSimplifyOnMetric() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE 2.5 < m1 AND m1 < 3.5", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.bound("m1", "2.5", "3.5", true, true, null, NUMERIC)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testCountStarWithBoundFilterSimplifyOr() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE (dim1 >= 'a' and dim1 < 'b') OR dim1 = 'ab'", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.bound("dim1", "a", "b", false, true, null, LEXICOGRAPHIC)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testCountStarWithBoundFilterSimplifyAnd() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE (dim1 >= 'a' and dim1 < 'b') and dim1 = 'abc'", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.selector("dim1", "abc", null)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testCountStarWithFilterOnCastedString() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE CAST(dim1 AS bigint) = 2", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.numeric_Selector("dim1", "2", null)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testCountStarWithTimeFilter() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo " + "WHERE __time >= TIMESTAMP '2000-01-01 00:00:00' AND __time < TIMESTAMP '2001-01-01 00:00:00'"), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-01/2001-01-01"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L }));
    }

    @Test
    public void testRemoveUselessCaseWhen() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo\n" + ((((((("WHERE\n" + "  CASE\n") + "    WHEN __time >= TIME_PARSE(\'2000-01-01 00:00:00\', \'yyyy-MM-dd HH:mm:ss\') AND __time < TIMESTAMP \'2001-01-01 00:00:00\'\n") + "    THEN true\n") + "    ELSE false\n") + "  END\n") + "OR\n") + "  __time >= TIMESTAMP '2010-01-01 00:00:00' AND __time < TIMESTAMP '2011-01-01 00:00:00'")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000/2001"), Intervals.of("2010/2011"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L }));
    }

    @Test
    public void testCountStarWithTimeMillisecondFilters() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo\n" + ("WHERE __time = TIMESTAMP \'2000-01-01 00:00:00.111\'\n" + "OR (__time >= TIMESTAMP '2000-01-01 00:00:00.888' AND __time < TIMESTAMP '2000-01-02 00:00:00.222')")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-01T00:00:00.111/2000-01-01T00:00:00.112"), Intervals.of("2000-01-01T00:00:00.888/2000-01-02T00:00:00.222"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testCountStarWithTimeFilterUsingStringLiterals() throws Exception {
        // Strings are implicitly cast to timestamps. Test a few different forms.
        testQuery(("SELECT COUNT(*) FROM druid.foo\n" + (("WHERE __time >= \'2000-01-01 00:00:00\' AND __time < \'2001-01-01T00:00:00\'\n" + "OR __time >= \'2001-02-01\' AND __time < \'2001-02-02\'\n") + "OR __time BETWEEN '2001-03-01' AND '2001-03-02'")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-01/2001-01-01"), Intervals.of("2001-02-01/2001-02-02"), Intervals.of("2001-03-01/2001-03-02T00:00:00.001"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L }));
    }

    @Test
    public void testCountStarWithTimeFilterUsingStringLiteralsInvalid() throws Exception {
        // Strings are implicitly cast to timestamps. Test an invalid string.
        // This error message isn't ideal but it is at least better than silently ignoring the problem.
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Error while applying rule ReduceExpressionsRule");
        expectedException.expectCause(ThrowableMessageMatcher.hasMessage(CoreMatchers.containsString("Illegal TIMESTAMP constant")));
        testQuery(("SELECT COUNT(*) FROM druid.foo\n" + "WHERE __time >= \'z2000-01-01 00:00:00\' AND __time < \'2001-01-01 00:00:00\'\n"), ImmutableList.of(), ImmutableList.of());
    }

    @Test
    public void testCountStarWithSinglePointInTime() throws Exception {
        testQuery("SELECT COUNT(*) FROM druid.foo WHERE __time = TIMESTAMP '2000-01-01 00:00:00'", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-01/2000-01-01T00:00:00.001"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testCountStarWithTwoPointsInTime() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo WHERE " + "__time = TIMESTAMP '2000-01-01 00:00:00' OR __time = TIMESTAMP '2000-01-01 00:00:00' + INTERVAL '1' DAY"), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-01/2000-01-01T00:00:00.001"), Intervals.of("2000-01-02/2000-01-02T00:00:00.001"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2L }));
    }

    @Test
    public void testCountStarWithComplexDisjointTimeFilter() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo " + ((((((("WHERE dim2 = 'a' and (" + "  (__time >= TIMESTAMP '2000-01-01 00:00:00' AND __time < TIMESTAMP '2001-01-01 00:00:00')") + "  OR (") + "    (__time >= TIMESTAMP '2002-01-01 00:00:00' AND __time < TIMESTAMP '2003-05-01 00:00:00')") + "    and (__time >= TIMESTAMP '2002-05-01 00:00:00' AND __time < TIMESTAMP '2004-01-01 00:00:00')") + "    and dim1 = 'abc'") + "  )") + ")")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000/2001"), Intervals.of("2002-05-01/2003-05-01"))).granularity(ALL).filters(BaseCalciteQueryTest.and(BaseCalciteQueryTest.selector("dim2", "a", null), BaseCalciteQueryTest.or(BaseCalciteQueryTest.timeBound("2000/2001"), BaseCalciteQueryTest.and(BaseCalciteQueryTest.selector("dim1", "abc", null), BaseCalciteQueryTest.timeBound("2002-05-01/2003-05-01"))))).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testCountStarWithNotOfComplexDisjointTimeFilter() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo " + (((((((("WHERE not (dim2 = 'a' and (" + "    (__time >= TIMESTAMP '2000-01-01 00:00:00' AND __time < TIMESTAMP '2001-01-01 00:00:00')") + "    OR (") + "      (__time >= TIMESTAMP '2002-01-01 00:00:00' AND __time < TIMESTAMP '2004-01-01 00:00:00')") + "      and (__time >= TIMESTAMP '2002-05-01 00:00:00' AND __time < TIMESTAMP '2003-05-01 00:00:00')") + "      and dim1 = 'abc'") + "    )") + "  )") + ")")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.or(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", "a", null)), BaseCalciteQueryTest.and(BaseCalciteQueryTest.not(BaseCalciteQueryTest.timeBound("2000/2001")), BaseCalciteQueryTest.not(BaseCalciteQueryTest.and(BaseCalciteQueryTest.selector("dim1", "abc", null), BaseCalciteQueryTest.timeBound("2002-05-01/2003-05-01")))))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 5L }));
    }

    @Test
    public void testCountStarWithNotTimeFilter() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo " + (("WHERE dim1 <> 'xxx' and not (" + "    (__time >= TIMESTAMP '2000-01-01 00:00:00' AND __time < TIMESTAMP '2001-01-01 00:00:00')") + "    OR (__time >= TIMESTAMP '2003-01-01 00:00:00' AND __time < TIMESTAMP '2004-01-01 00:00:00'))")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(new org.joda.time.Interval(DateTimes.MIN, DateTimes.of("2000")), Intervals.of("2001/2003"), new org.joda.time.Interval(DateTimes.of("2004"), DateTimes.MAX))).filters(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "xxx", null))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L }));
    }

    @Test
    public void testCountStarWithTimeAndDimFilter() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo " + ("WHERE dim2 <> 'a' " + "and __time BETWEEN TIMESTAMP '2000-01-01 00:00:00' AND TIMESTAMP '2000-12-31 23:59:59.999'")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-01/2001-01-01"))).filters(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", "a", null))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2L }));
    }

    @Test
    public void testCountStarWithTimeOrDimFilter() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo " + ("WHERE dim2 <> 'a' " + "or __time BETWEEN TIMESTAMP '2000-01-01 00:00:00' AND TIMESTAMP '2000-12-31 23:59:59.999'")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.or(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", "a", null)), BaseCalciteQueryTest.bound("__time", String.valueOf(BaseCalciteQueryTest.t("2000-01-01")), String.valueOf(BaseCalciteQueryTest.t("2000-12-31T23:59:59.999")), false, false, null, NUMERIC))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 5L }));
    }

    @Test
    public void testCountStarWithTimeFilterOnLongColumnUsingExtractEpoch() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo WHERE " + ("cnt >= EXTRACT(EPOCH FROM TIMESTAMP '1970-01-01 00:00:00') * 1000 " + "AND cnt < EXTRACT(EPOCH FROM TIMESTAMP '1970-01-02 00:00:00') * 1000")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.bound("cnt", String.valueOf(DateTimes.of("1970-01-01").getMillis()), String.valueOf(DateTimes.of("1970-01-02").getMillis()), false, true, null, NUMERIC)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }));
    }

    @Test
    public void testCountStarWithTimeFilterOnLongColumnUsingExtractEpochFromDate() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo WHERE " + ("cnt >= EXTRACT(EPOCH FROM DATE '1970-01-01') * 1000 " + "AND cnt < EXTRACT(EPOCH FROM DATE '1970-01-02') * 1000")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.bound("cnt", String.valueOf(DateTimes.of("1970-01-01").getMillis()), String.valueOf(DateTimes.of("1970-01-02").getMillis()), false, true, null, NUMERIC)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }));
    }

    @Test
    public void testCountStarWithTimeFilterOnLongColumnUsingTimestampToMillis() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo WHERE " + ("cnt >= TIMESTAMP_TO_MILLIS(TIMESTAMP '1970-01-01 00:00:00') " + "AND cnt < TIMESTAMP_TO_MILLIS(TIMESTAMP '1970-01-02 00:00:00')")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(BaseCalciteQueryTest.bound("cnt", String.valueOf(DateTimes.of("1970-01-01").getMillis()), String.valueOf(DateTimes.of("1970-01-02").getMillis()), false, true, null, NUMERIC)).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L }));
    }

    @Test
    public void testSumOfString() throws Exception {
        testQuery("SELECT SUM(CAST(dim1 AS INTEGER)) FROM druid.foo", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", null, "CAST(\"dim1\", \'LONG\')", CalciteTests.createExprMacroTable()))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 13L }));
    }

    @Test
    public void testSumOfExtractionFn() throws Exception {
        testQuery("SELECT SUM(CAST(SUBSTRING(dim1, 1, 10) AS INTEGER)) FROM druid.foo", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", null, "CAST(substring(\"dim1\", 0, 10), \'LONG\')", CalciteTests.createExprMacroTable()))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 13L }));
    }

    @Test
    public void testTimeseriesWithTimeFilterOnLongColumnUsingMillisToTimestamp() throws Exception {
        testQuery(("SELECT\n" + (((((((("  FLOOR(MILLIS_TO_TIMESTAMP(cnt) TO YEAR),\n" + "  COUNT(*)\n") + "FROM\n") + "  druid.foo\n") + "WHERE\n") + "  MILLIS_TO_TIMESTAMP(cnt) >= TIMESTAMP \'1970-01-01 00:00:00\'\n") + "  AND MILLIS_TO_TIMESTAMP(cnt) < TIMESTAMP \'1970-01-02 00:00:00\'\n") + "GROUP BY\n") + "  FLOOR(MILLIS_TO_TIMESTAMP(cnt) TO YEAR)")), ImmutableList.of(new GroupByQuery.Builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "timestamp_floor(\"cnt\",\'P1Y\',null,\'UTC\')", LONG)).setDimFilter(BaseCalciteQueryTest.bound("cnt", String.valueOf(DateTimes.of("1970-01-01").getMillis()), String.valueOf(DateTimes.of("1970-01-02").getMillis()), false, true, null, NUMERIC)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("1970-01-01"), 6L }));
    }

    @Test
    public void testSelectDistinctWithCascadeExtractionFilter() throws Exception {
        testQuery("SELECT distinct dim1 FROM druid.foo WHERE substring(substring(dim1, 2), 1, 1) = 'e' OR dim2 = 'a'", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setDimFilter(BaseCalciteQueryTest.or(BaseCalciteQueryTest.selector("dim1", "e", BaseCalciteQueryTest.cascade(new SubstringDimExtractionFn(1, null), new SubstringDimExtractionFn(0, 1))), BaseCalciteQueryTest.selector("dim2", "a", null))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "" }, new Object[]{ "1" }, new Object[]{ "def" }));
    }

    @Test
    public void testSelectDistinctWithStrlenFilter() throws Exception {
        testQuery(("SELECT distinct dim1 FROM druid.foo " + "WHERE CHARACTER_LENGTH(dim1) = 3 OR CAST(CHARACTER_LENGTH(dim1) AS varchar) = 3"), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setDimFilter(BaseCalciteQueryTest.or(BaseCalciteQueryTest.expressionFilter("(strlen(\"dim1\") == 3)"), BaseCalciteQueryTest.expressionFilter("(CAST(strlen(\"dim1\"), \'STRING\') == 3)"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "abc" }, new Object[]{ "def" }));
    }

    @Test
    public void testSelectDistinctWithLimit() throws Exception {
        // Should use topN even if approximate topNs are off, because this query is exact.
        testQuery("SELECT DISTINCT dim2 FROM druid.foo LIMIT 10", ImmutableList.of(new TopNQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimension(new DefaultDimensionSpec("dim2", "d0")).metric(new org.apache.druid.query.topn.DimensionTopNMetricSpec(null, StringComparators.LEXICOGRAPHIC)).threshold(10).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "" }, new Object[]{ "a" }, new Object[]{ "abc" }) : ImmutableList.of(new Object[]{ null }, new Object[]{ "" }, new Object[]{ "a" }, new Object[]{ "abc" })));
    }

    @Test
    public void testSelectDistinctWithSortAsOuterQuery() throws Exception {
        testQuery("SELECT * FROM (SELECT DISTINCT dim2 FROM druid.foo ORDER BY dim2) LIMIT 10", ImmutableList.of(new TopNQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimension(new DefaultDimensionSpec("dim2", "d0")).metric(new org.apache.druid.query.topn.DimensionTopNMetricSpec(null, StringComparators.LEXICOGRAPHIC)).threshold(10).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "" }, new Object[]{ "a" }, new Object[]{ "abc" }) : ImmutableList.of(new Object[]{ null }, new Object[]{ "" }, new Object[]{ "a" }, new Object[]{ "abc" })));
    }

    @Test
    public void testSelectDistinctWithSortAsOuterQuery2() throws Exception {
        testQuery("SELECT * FROM (SELECT DISTINCT dim2 FROM druid.foo ORDER BY dim2 LIMIT 5) LIMIT 10", ImmutableList.of(new TopNQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimension(new DefaultDimensionSpec("dim2", "d0")).metric(new org.apache.druid.query.topn.DimensionTopNMetricSpec(null, StringComparators.LEXICOGRAPHIC)).threshold(5).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "" }, new Object[]{ "a" }, new Object[]{ "abc" }) : ImmutableList.of(new Object[]{ null }, new Object[]{ "" }, new Object[]{ "a" }, new Object[]{ "abc" })));
    }

    @Test
    public void testSelectDistinctWithSortAsOuterQuery3() throws Exception {
        // Query reduces to LIMIT 0.
        testQuery("SELECT * FROM (SELECT DISTINCT dim2 FROM druid.foo ORDER BY dim2 LIMIT 2 OFFSET 5) OFFSET 2", ImmutableList.of(), ImmutableList.of());
    }

    @Test
    public void testSelectDistinctWithSortAsOuterQuery4() throws Exception {
        testQuery("SELECT * FROM (SELECT DISTINCT dim2 FROM druid.foo ORDER BY dim2 DESC LIMIT 5) LIMIT 10", ImmutableList.of(new TopNQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimension(new DefaultDimensionSpec("dim2", "d0")).metric(new InvertedTopNMetricSpec(new org.apache.druid.query.topn.DimensionTopNMetricSpec(null, StringComparators.LEXICOGRAPHIC))).threshold(5).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "" }, new Object[]{ "abc" }, new Object[]{ "a" }) : ImmutableList.of(new Object[]{ null }, new Object[]{ "abc" }, new Object[]{ "a" }, new Object[]{ "" })));
    }

    @Test
    public void testCountDistinct() throws Exception {
        testQuery("SELECT SUM(cnt), COUNT(distinct dim2), COUNT(distinct unique_dim1) FROM druid.foo", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"), new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a1", null, BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", null)), false, true), new HyperUniquesAggregatorFactory("a2", "unique_dim1", false, true))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L, 3L, 6L }));
    }

    @Test
    public void testCountDistinctOfCaseWhen() throws Exception {
        testQuery(("SELECT\n" + ((("COUNT(DISTINCT CASE WHEN m1 >= 4 THEN m1 END),\n" + "COUNT(DISTINCT CASE WHEN m1 >= 4 THEN dim1 END),\n") + "COUNT(DISTINCT CASE WHEN m1 >= 4 THEN unique_dim1 END)\n") + "FROM druid.foo")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a0", null, ImmutableList.of(new DefaultDimensionSpec("m1", "m1", ValueType.FLOAT)), false, true), BaseCalciteQueryTest.bound("m1", "4", null, false, false, null, NUMERIC)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a1", null, ImmutableList.of(new DefaultDimensionSpec("dim1", "dim1", ValueType.STRING)), false, true), BaseCalciteQueryTest.bound("m1", "4", null, false, false, null, NUMERIC)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new HyperUniquesAggregatorFactory("a2", "unique_dim1", false, true), BaseCalciteQueryTest.bound("m1", "4", null, false, false, null, NUMERIC)))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L, 3L, 3L }));
    }

    @Test
    public void testExactCountDistinct() throws Exception {
        // When HLL is disabled, do exact count distinct through a nested query.
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_NO_HLL, "SELECT COUNT(distinct dim2) FROM druid.foo", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("a0"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("d0", null, null))))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ NullHandling.replaceWithDefault() ? 2L : 3L }));
    }

    @Test
    public void testApproxCountDistinctWhenHllDisabled() throws Exception {
        // When HLL is disabled, APPROX_COUNT_DISTINCT is still approximate.
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_NO_HLL, "SELECT APPROX_COUNT_DISTINCT(dim2) FROM druid.foo", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a0", null, BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", null)), false, true))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L }));
    }

    @Test
    public void testExactCountDistinctWithGroupingAndOtherAggregators() throws Exception {
        // When HLL is disabled, do exact count distinct through a nested query.
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_NO_HLL, "SELECT dim2, SUM(cnt), COUNT(distinct dim1) FROM druid.foo GROUP BY dim2", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"), new DefaultDimensionSpec("dim1", "d1"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0", "_d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("_a0", "a0"), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("_a1"), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("d1", null, null))))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "", 3L, 3L }, new Object[]{ "a", 2L, 1L }, new Object[]{ "abc", 1L, 1L }) : ImmutableList.of(new Object[]{ null, 2L, 2L }, new Object[]{ "", 1L, 1L }, new Object[]{ "a", 2L, 2L }, new Object[]{ "abc", 1L, 1L })));
    }

    @Test
    public void testApproxCountDistinct() throws Exception {
        testQuery(("SELECT\n" + (((((("  SUM(cnt),\n" + "  APPROX_COUNT_DISTINCT(dim2),\n")// uppercase
         + "  approx_count_distinct(dim2) FILTER(WHERE dim2 <> \'\'),\n")// lowercase; also, filtered
         + "  APPROX_COUNT_DISTINCT(SUBSTRING(dim2, 1, 1)),\n")// on extractionFn
         + "  APPROX_COUNT_DISTINCT(SUBSTRING(dim2, 1, 1) || \'x\'),\n")// on expression
         + "  approx_count_distinct(unique_dim1)\n")// on native hyperUnique column
         + "FROM druid.foo")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).virtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("a4:v", "concat(substring(\"dim2\", 0, 1),\'x\')", STRING)).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"), new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a1", null, BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "dim2")), false, true), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a2", null, BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "dim2")), false, true), BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", "", null))), new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a3", null, BaseCalciteQueryTest.dimensionSpec(new org.apache.druid.query.dimension.ExtractionDimensionSpec("dim2", "dim2", ValueType.STRING, new SubstringDimExtractionFn(0, 1))), false, true), new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a4", null, BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("a4:v", "a4:v", ValueType.STRING)), false, true), new HyperUniquesAggregatorFactory("a5", "unique_dim1", false, true))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 6L, 3L, 2L, 2L, 2L, 6L }) : ImmutableList.of(new Object[]{ 6L, 3L, 2L, 1L, 1L, 6L })));
    }

    @Test
    public void testNestedGroupBy() throws Exception {
        testQuery(("SELECT\n" + ((((((((((((("    FLOOR(__time to hour) AS __time,\n" + "    dim1,\n") + "    COUNT(m2)\n") + "FROM (\n") + "    SELECT\n") + "        MAX(__time) AS __time,\n") + "        m2,\n") + "        dim1\n") + "    FROM druid.foo\n") + "    WHERE 1=1\n") + "        AND m1 = \'5.0\'\n") + "    GROUP BY m2, dim1\n") + ")\n") + "GROUP BY FLOOR(__time to hour), dim1")), ImmutableList.of(GroupByQuery.builder().setDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("m2", "d0", ValueType.DOUBLE), new DefaultDimensionSpec("dim1", "d1"))).setDimFilter(new SelectorDimFilter("m1", "5.0", null)).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongMaxAggregatorFactory("a0", "__time"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("_d0:v", "timestamp_floor(\"a0\",\'PT1H\',null,\'UTC\')", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("_d0:v", "_d0", ValueType.LONG), new DefaultDimensionSpec("d1", "_d1", ValueType.STRING))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("_a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 978393600000L, "def", 1L }));
    }

    @Test
    public void testDoubleNestedGroupBy() throws Exception {
        testQuery(("SELECT SUM(cnt), COUNT(*) FROM (\n" + ((((((((("  SELECT dim2, SUM(t1.cnt) cnt FROM (\n" + "    SELECT\n") + "      dim1,\n") + "      dim2,\n") + "      COUNT(*) cnt\n") + "    FROM druid.foo\n") + "    GROUP BY dim1, dim2\n") + "  ) t1\n") + "  GROUP BY dim2\n") + ") t2")), ImmutableList.of(GroupByQuery.builder().setDataSource(GroupByQuery.builder().setDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"), new DefaultDimensionSpec("dim2", "d1"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d1", "_d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("_a0", "a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "_a0"), new CountAggregatorFactory("a1"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 6L, 3L }) : ImmutableList.of(new Object[]{ 6L, 4L })));
    }

    @Test
    public void testExplainDoubleNestedGroupBy() throws Exception {
        final String explanation = "DruidOuterQueryRel(query=[{\"queryType\":\"timeseries\",\"dataSource\":{\"type\":\"table\",\"name\":\"__subquery__\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"descending\":false,\"virtualColumns\":[],\"filter\":null,\"granularity\":{\"type\":\"all\"},\"aggregations\":[{\"type\":\"longSum\",\"name\":\"a0\",\"fieldName\":\"cnt\",\"expression\":null},{\"type\":\"count\",\"name\":\"a1\"}],\"postAggregations\":[],\"limit\":2147483647,\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"skipEmptyBuckets\":true,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"}}], signature=[{a0:LONG, a1:LONG}])\n" + ("  DruidOuterQueryRel(query=[{\"queryType\":\"groupBy\",\"dataSource\":{\"type\":\"table\",\"name\":\"__subquery__\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"virtualColumns\":[],\"filter\":null,\"granularity\":{\"type\":\"all\"},\"dimensions\":[{\"type\":\"default\",\"dimension\":\"dim2\",\"outputName\":\"d0\",\"outputType\":\"STRING\"}],\"aggregations\":[{\"type\":\"longSum\",\"name\":\"a0\",\"fieldName\":\"cnt\",\"expression\":null}],\"postAggregations\":[],\"having\":null,\"limitSpec\":{\"type\":\"NoopLimitSpec\"},\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"},\"descending\":false}], signature=[{d0:STRING, a0:LONG}])\n" + "    DruidQueryRel(query=[{\"queryType\":\"groupBy\",\"dataSource\":{\"type\":\"table\",\"name\":\"foo\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"virtualColumns\":[],\"filter\":null,\"granularity\":{\"type\":\"all\"},\"dimensions\":[{\"type\":\"default\",\"dimension\":\"dim1\",\"outputName\":\"d0\",\"outputType\":\"STRING\"},{\"type\":\"default\",\"dimension\":\"dim2\",\"outputName\":\"d1\",\"outputType\":\"STRING\"}],\"aggregations\":[{\"type\":\"count\",\"name\":\"a0\"}],\"postAggregations\":[],\"having\":null,\"limitSpec\":{\"type\":\"NoopLimitSpec\"},\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"},\"descending\":false}], signature=[{d0:STRING, d1:STRING, a0:LONG}])\n");
        testQuery(("EXPLAIN PLAN FOR SELECT SUM(cnt), COUNT(*) FROM (\n" + ((((((((("  SELECT dim2, SUM(t1.cnt) cnt FROM (\n" + "    SELECT\n") + "      dim1,\n") + "      dim2,\n") + "      COUNT(*) cnt\n") + "    FROM druid.foo\n") + "    GROUP BY dim1, dim2\n") + "  ) t1\n") + "  GROUP BY dim2\n") + ") t2")), ImmutableList.of(), ImmutableList.of(new Object[]{ explanation }));
    }

    @Test
    public void testExactCountDistinctUsingSubquery() throws Exception {
        // Sanity check; this query should work with a single level of nesting.
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_SINGLE_NESTING_ONLY, ("SELECT\n" + (("  SUM(cnt),\n" + "  COUNT(*)\n") + "FROM (SELECT dim2, SUM(cnt) AS cnt FROM druid.foo GROUP BY dim2)")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("_a0", "a0"), new CountAggregatorFactory("_a1"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 6L, 3L }) : ImmutableList.of(new Object[]{ 6L, 4L })));
    }

    @Test
    public void testMinMaxAvgDailyCountWithLimit() throws Exception {
        testQuery(("SELECT * FROM (" + ((((("  SELECT max(cnt), min(cnt), avg(cnt), TIME_EXTRACT(max(t), \'EPOCH\') last_time, count(1) num_days FROM (\n" + "      SELECT TIME_FLOOR(__time, \'P1D\') AS t, count(1) cnt\n") + "      FROM \"foo\"\n") + "      GROUP BY 1\n") + "  )") + ") LIMIT 1\n")), ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "timestamp_floor(\"__time\",\'P1D\',null,\'UTC\')", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongMaxAggregatorFactory("_a0", "a0"), new LongMinAggregatorFactory("_a1", "a0"), new LongSumAggregatorFactory("_a2:sum", "a0"), new CountAggregatorFactory("_a2:count"), new LongMaxAggregatorFactory("_a3", "d0"), new CountAggregatorFactory("_a4"))).setPostAggregatorSpecs(ImmutableList.of(new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("_a2", "quotient", ImmutableList.of(new FieldAccessPostAggregator(null, "_a2:sum"), new FieldAccessPostAggregator(null, "_a2:count"))), BaseCalciteQueryTest.expresionPostAgg("s0", "timestamp_extract(\"_a3\",\'EPOCH\',\'UTC\')"))).setLimit(1).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, 1L, 1L, 978480000L, 6L }));
    }

    @Test
    public void testAvgDailyCountDistinct() throws Exception {
        testQuery(("SELECT\n" + ("  AVG(u)\n" + "FROM (SELECT FLOOR(__time TO DAY), APPROX_COUNT_DISTINCT(cnt) AS u FROM druid.foo GROUP BY 1)")), ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "timestamp_floor(\"__time\",\'P1D\',null,\'UTC\')", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a0:a", null, BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("cnt", "cnt", ValueType.LONG)), false, true))).setPostAggregatorSpecs(ImmutableList.of(new HyperUniqueFinalizingPostAggregator("a0", "a0:a"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("_a0:sum", "a0"), new CountAggregatorFactory("_a0:count"))).setPostAggregatorSpecs(ImmutableList.of(new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("_a0", "quotient", ImmutableList.of(new FieldAccessPostAggregator(null, "_a0:sum"), new FieldAccessPostAggregator(null, "_a0:count"))))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testTopNFilterJoin() throws Exception {
        DimFilter filter = (NullHandling.replaceWithDefault()) ? BaseCalciteQueryTest.in("dim2", Arrays.asList(null, "a"), null) : BaseCalciteQueryTest.selector("dim2", "a", null);
        // Filters on top N values of some dimension by using an inner join.
        testQuery(("SELECT t1.dim1, SUM(t1.cnt)\n" + ((((((((((("FROM druid.foo t1\n" + "  INNER JOIN (\n") + "  SELECT\n") + "    SUM(cnt) AS sum_cnt,\n") + "    dim2\n") + "  FROM druid.foo\n") + "  GROUP BY dim2\n") + "  ORDER BY 1 DESC\n") + "  LIMIT 2\n") + ") t2 ON (t1.dim2 = t2.dim2)\n") + "GROUP BY t1.dim1\n") + "ORDER BY 1\n")), ImmutableList.of(new TopNQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimension(new DefaultDimensionSpec("dim2", "d0")).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).metric(new NumericTopNMetricSpec("a0")).threshold(2).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build(), GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(filter).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.ASCENDING, StringComparators.LEXICOGRAPHIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "", 1L }, new Object[]{ "1", 1L }, new Object[]{ "10.1", 1L }, new Object[]{ "2", 1L }, new Object[]{ "abc", 1L }) : ImmutableList.of(new Object[]{ "", 1L }, new Object[]{ "1", 1L })));
    }

    @Test
    public void testRemovableLeftJoin() throws Exception {
        // LEFT JOIN where the right-hand side can be ignored.
        testQuery(("SELECT t1.dim1, SUM(t1.cnt)\n" + ((((((((((("FROM druid.foo t1\n" + "  LEFT JOIN (\n") + "  SELECT\n") + "    SUM(cnt) AS sum_cnt,\n") + "    dim2\n") + "  FROM druid.foo\n") + "  GROUP BY dim2\n") + "  ORDER BY 1 DESC\n") + "  LIMIT 2\n") + ") t2 ON (t1.dim2 = t2.dim2)\n") + "GROUP BY t1.dim1\n") + "ORDER BY 1\n")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.ASCENDING, StringComparators.LEXICOGRAPHIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "", 1L }, new Object[]{ "1", 1L }, new Object[]{ "10.1", 1L }, new Object[]{ "2", 1L }, new Object[]{ "abc", 1L }, new Object[]{ "def", 1L }));
    }

    @Test
    public void testExactCountDistinctOfSemiJoinResult() throws Exception {
        testQuery(("SELECT COUNT(*)\n" + (((((("FROM (\n" + "  SELECT DISTINCT dim2\n") + "  FROM druid.foo\n") + "  WHERE SUBSTRING(dim2, 1, 1) IN (\n") + "    SELECT SUBSTRING(dim1, 1, 1) FROM druid.foo WHERE dim1 <> \'\'\n") + "  ) AND __time >= \'2000-01-01\' AND __time < \'2002-01-01\'\n") + ")")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "", null))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new org.apache.druid.query.dimension.ExtractionDimensionSpec("dim1", "d0", new SubstringDimExtractionFn(0, 1)))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build(), GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-01/2002-01-01"))).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.in("dim2", ImmutableList.of("1", "2", "a", "d"), new SubstringDimExtractionFn(0, 1))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2L }));
    }

    @Test
    public void testMaxSemiJoinRowsInMemory() throws Exception {
        expectedException.expect(ResourceLimitExceededException.class);
        expectedException.expectMessage("maxSemiJoinRowsInMemory[2] exceeded");
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_SEMI_JOIN_ROWS_LIMIT, ("SELECT COUNT(*)\n" + ((("FROM druid.foo\n" + "WHERE SUBSTRING(dim2, 1, 1) IN (\n") + "  SELECT SUBSTRING(dim1, 1, 1) FROM druid.foo WHERE dim1 <> \'\'\n") + ")\n")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(), ImmutableList.of());
    }

    @Test
    public void testExplainExactCountDistinctOfSemiJoinResult() throws Exception {
        final String explanation = "DruidOuterQueryRel(query=[{\"queryType\":\"timeseries\",\"dataSource\":{\"type\":\"table\",\"name\":\"__subquery__\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"descending\":false,\"virtualColumns\":[],\"filter\":null,\"granularity\":{\"type\":\"all\"},\"aggregations\":[{\"type\":\"count\",\"name\":\"a0\"}],\"postAggregations\":[],\"limit\":2147483647,\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"skipEmptyBuckets\":true,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"}}], signature=[{a0:LONG}])\n" + ("  DruidSemiJoin(query=[{\"queryType\":\"groupBy\",\"dataSource\":{\"type\":\"table\",\"name\":\"foo\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"virtualColumns\":[],\"filter\":null,\"granularity\":{\"type\":\"all\"},\"dimensions\":[{\"type\":\"default\",\"dimension\":\"dim2\",\"outputName\":\"d0\",\"outputType\":\"STRING\"}],\"aggregations\":[],\"postAggregations\":[],\"having\":null,\"limitSpec\":{\"type\":\"NoopLimitSpec\"},\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"},\"descending\":false}], leftExpressions=[[SUBSTRING($3, 1, 1)]], rightKeys=[[0]])\n" + "    DruidQueryRel(query=[{\"queryType\":\"groupBy\",\"dataSource\":{\"type\":\"table\",\"name\":\"foo\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"virtualColumns\":[],\"filter\":{\"type\":\"not\",\"field\":{\"type\":\"selector\",\"dimension\":\"dim1\",\"value\":null,\"extractionFn\":null}},\"granularity\":{\"type\":\"all\"},\"dimensions\":[{\"type\":\"extraction\",\"dimension\":\"dim1\",\"outputName\":\"d0\",\"outputType\":\"STRING\",\"extractionFn\":{\"type\":\"substring\",\"index\":0,\"length\":1}}],\"aggregations\":[],\"postAggregations\":[],\"having\":null,\"limitSpec\":{\"type\":\"NoopLimitSpec\"},\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"},\"descending\":false}], signature=[{d0:STRING}])\n");
        testQuery(("EXPLAIN PLAN FOR SELECT COUNT(*)\n" + (((((("FROM (\n" + "  SELECT DISTINCT dim2\n") + "  FROM druid.foo\n") + "  WHERE SUBSTRING(dim2, 1, 1) IN (\n") + "    SELECT SUBSTRING(dim1, 1, 1) FROM druid.foo WHERE dim1 IS NOT NULL\n") + "  )\n") + ")")), ImmutableList.of(), ImmutableList.of(new Object[]{ explanation }));
    }

    @Test
    public void testExactCountDistinctUsingSubqueryWithWherePushDown() throws Exception {
        testQuery(("SELECT\n" + ((("  SUM(cnt),\n" + "  COUNT(*)\n") + "FROM (SELECT dim2, SUM(cnt) AS cnt FROM druid.foo GROUP BY dim2)\n") + "WHERE dim2 <> ''")), ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setDimFilter(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", "", null))).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("_a0", "a0"), new CountAggregatorFactory("_a1"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 3L, 2L }) : ImmutableList.of(new Object[]{ 5L, 3L })));
        testQuery(("SELECT\n" + ((("  SUM(cnt),\n" + "  COUNT(*)\n") + "FROM (SELECT dim2, SUM(cnt) AS cnt FROM druid.foo GROUP BY dim2)\n") + "WHERE dim2 IS NOT NULL")), ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setDimFilter(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim2", null, null))).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("_a0", "a0"), new CountAggregatorFactory("_a1"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 3L, 2L }) : ImmutableList.of(new Object[]{ 4L, 3L })));
    }

    @Test
    public void testExactCountDistinctUsingSubqueryWithWhereToOuterFilter() throws Exception {
        testQuery(("SELECT\n" + ((("  SUM(cnt),\n" + "  COUNT(*)\n") + "FROM (SELECT dim2, SUM(cnt) AS cnt FROM druid.foo GROUP BY dim2 LIMIT 1)") + "WHERE cnt > 0")), ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setLimit(1).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setDimFilter(BaseCalciteQueryTest.bound("a0", "0", null, true, false, null, NUMERIC)).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("_a0", "a0"), new CountAggregatorFactory("_a1"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 3L, 1L }) : ImmutableList.of(new Object[]{ 2L, 1L })));
    }

    @Test
    public void testCompareExactAndApproximateCountDistinctUsingSubquery() throws Exception {
        testQuery(("SELECT\n" + ((("  COUNT(*) AS exact_count,\n" + "  COUNT(DISTINCT dim1) AS approx_count,\n") + "  (CAST(1 AS FLOAT) - COUNT(DISTINCT dim1) / COUNT(*)) * 100 AS error_pct\n") + "FROM (SELECT DISTINCT dim1 FROM druid.foo WHERE dim1 <> '')")), ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "", null))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"), new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a1", null, BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0", null)), false, true))).setPostAggregatorSpecs(ImmutableList.of(BaseCalciteQueryTest.expresionPostAgg("p0", "((1 - (\"a1\" / \"a0\")) * 100)"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 5L, 5L, 0.0F }));
    }

    @Test
    public void testHistogramUsingSubquery() throws Exception {
        testQuery(("SELECT\n" + ((("  CAST(thecnt AS VARCHAR),\n" + "  COUNT(*)\n") + "FROM (SELECT dim2, SUM(cnt) AS thecnt FROM druid.foo GROUP BY dim2)\n") + "GROUP BY CAST(thecnt AS VARCHAR)")), ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("a0", "_d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("_a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "1", 1L }, new Object[]{ "2", 1L }, new Object[]{ "3", 1L }) : ImmutableList.of(new Object[]{ "1", 2L }, new Object[]{ "2", 2L })));
    }

    @Test
    public void testHistogramUsingSubqueryWithSort() throws Exception {
        testQuery(("SELECT\n" + ((("  CAST(thecnt AS VARCHAR),\n" + "  COUNT(*)\n") + "FROM (SELECT dim2, SUM(cnt) AS thecnt FROM druid.foo GROUP BY dim2)\n") + "GROUP BY CAST(thecnt AS VARCHAR) ORDER BY CAST(thecnt AS VARCHAR) LIMIT 2")), ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("a0", "_d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("_a0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("_d0", Direction.ASCENDING, StringComparators.LEXICOGRAPHIC)), 2)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "1", 1L }, new Object[]{ "2", 1L }) : ImmutableList.of(new Object[]{ "1", 2L }, new Object[]{ "2", 2L })));
    }

    @Test
    public void testCountDistinctArithmetic() throws Exception {
        testQuery(("SELECT\n" + (((((("  SUM(cnt),\n" + "  COUNT(DISTINCT dim2),\n") + "  CAST(COUNT(DISTINCT dim2) AS FLOAT),\n") + "  SUM(cnt) / COUNT(DISTINCT dim2),\n") + "  SUM(cnt) / COUNT(DISTINCT dim2) + 3,\n") + "  CAST(SUM(cnt) AS FLOAT) / CAST(COUNT(DISTINCT dim2) AS FLOAT) + 3\n") + "FROM druid.foo")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"), new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a1", null, BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", null)), false, true))).postAggregators(BaseCalciteQueryTest.expresionPostAgg("p0", "CAST(\"a1\", \'DOUBLE\')"), BaseCalciteQueryTest.expresionPostAgg("p1", "(\"a0\" / \"a1\")"), BaseCalciteQueryTest.expresionPostAgg("p2", "((\"a0\" / \"a1\") + 3)"), BaseCalciteQueryTest.expresionPostAgg("p3", "((CAST(\"a0\", \'DOUBLE\') / CAST(\"a1\", \'DOUBLE\')) + 3)")).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 6L, 3L, 3.0F, 2L, 5L, 5.0F }));
    }

    @Test
    public void testCountDistinctOfSubstring() throws Exception {
        testQuery("SELECT COUNT(DISTINCT SUBSTRING(dim1, 1, 1)) FROM druid.foo WHERE dim1 <> ''", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "", null))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a0", null, BaseCalciteQueryTest.dimensionSpec(new org.apache.druid.query.dimension.ExtractionDimensionSpec("dim1", null, new SubstringDimExtractionFn(0, 1))), false, true))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 4L }));
    }

    @Test
    public void testCountDistinctOfTrim() throws Exception {
        // Test a couple different syntax variants of TRIM.
        testQuery("SELECT COUNT(DISTINCT TRIM(BOTH ' ' FROM dim1)) FROM druid.foo WHERE TRIM(dim1) <> ''", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "", null))).granularity(ALL).virtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("a0:v", "trim(\"dim1\",\' \')", STRING)).filters(BaseCalciteQueryTest.expressionFilter("(trim(\"dim1\",\' \') != \'\')")).aggregators(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a0", null, BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("a0:v", "a0:v", ValueType.STRING)), false, true))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 5L }));
    }

    @Test
    public void testSillyQuarters() throws Exception {
        // Like FLOOR(__time TO QUARTER) but silly.
        testQuery(("SELECT CAST((EXTRACT(MONTH FROM __time) - 1 ) / 3 + 1 AS INTEGER) AS quarter, COUNT(*)\n" + ("FROM foo\n" + "GROUP BY CAST((EXTRACT(MONTH FROM __time) - 1 ) / 3 + 1 AS INTEGER)")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "(((timestamp_extract(\"__time\",\'MONTH\',\'UTC\') - 1) / 3) + 1)", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1, 6L }));
    }

    @Test
    public void testRegexpExtract() throws Exception {
        String nullValue = (NullHandling.replaceWithDefault()) ? "" : null;
        testQuery(("SELECT DISTINCT\n" + ((("  REGEXP_EXTRACT(dim1, \'^.\'),\n" + "  REGEXP_EXTRACT(dim1, \'^(.)\', 1)\n") + "FROM foo\n") + "WHERE REGEXP_EXTRACT(dim1, '^(.)', 1) <> 'x'")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "x", new RegexDimExtractionFn("^(.)", 1, true, null)))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new org.apache.druid.query.dimension.ExtractionDimensionSpec("dim1", "d0", new RegexDimExtractionFn("^.", 0, true, null)), new org.apache.druid.query.dimension.ExtractionDimensionSpec("dim1", "d1", new RegexDimExtractionFn("^(.)", 1, true, null)))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ nullValue, nullValue }, new Object[]{ "1", "1" }, new Object[]{ "2", "2" }, new Object[]{ "a", "a" }, new Object[]{ "d", "d" }));
    }

    @Test
    public void testGroupBySortPushDown() throws Exception {
        String nullValue = (NullHandling.replaceWithDefault()) ? "" : null;
        testQuery("SELECT dim2, dim1, SUM(cnt) FROM druid.foo GROUP BY dim2, dim1 ORDER BY dim1 LIMIT 4", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"), new DefaultDimensionSpec("dim1", "d1"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d1", Direction.ASCENDING)), 4)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "a", "", 1L }, new Object[]{ "a", "1", 1L }, new Object[]{ nullValue, "10.1", 1L }, new Object[]{ "", "2", 1L }));
    }

    @Test
    public void testGroupByLimitPushDownWithHavingOnLong() throws Exception {
        String nullValue = (NullHandling.replaceWithDefault()) ? "" : null;
        testQuery(("SELECT dim1, dim2, SUM(cnt) AS thecnt " + (((("FROM druid.foo " + "group by dim1, dim2 ") + "having SUM(cnt) = 1 ") + "order by dim2 ") + "limit 4")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"), new DefaultDimensionSpec("dim2", "d1"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d1", Direction.ASCENDING)), 4)).setHavingSpec(BaseCalciteQueryTest.having(BaseCalciteQueryTest.numeric_Selector("a0", "1", null))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "10.1", "", 1L }, new Object[]{ "2", "", 1L }, new Object[]{ "abc", "", 1L }, new Object[]{ "", "a", 1L }) : ImmutableList.of(new Object[]{ "10.1", null, 1L }, new Object[]{ "abc", null, 1L }, new Object[]{ "2", "", 1L }, new Object[]{ "", "a", 1L })));
    }

    @Test
    public void testFilterOnTimeFloor() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo\n" + (("WHERE\n" + "FLOOR(__time TO MONTH) = TIMESTAMP \'2000-01-01 00:00:00\'\n") + "OR FLOOR(__time TO MONTH) = TIMESTAMP '2000-02-01 00:00:00'")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000/P2M"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L }));
    }

    @Test
    public void testFilterOnCurrentTimestampWithIntervalArithmetic() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo\n" + (("WHERE\n" + "  __time >= CURRENT_TIMESTAMP + INTERVAL \'01:02\' HOUR TO MINUTE\n") + "  AND __time < TIMESTAMP '2003-02-02 01:00:00' - INTERVAL '1 1' DAY TO HOUR - INTERVAL '1-1' YEAR TO MONTH")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-01T01:02/2002"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 5L }));
    }

    @Test
    public void testSelectCurrentTimeAndDateLosAngeles() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, BaseCalciteQueryTest.QUERY_CONTEXT_LOS_ANGELES, "SELECT CURRENT_TIMESTAMP, CURRENT_DATE, CURRENT_DATE + INTERVAL '1' DAY", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000-01-01T00Z", BaseCalciteQueryTest.LOS_ANGELES), BaseCalciteQueryTest.d("1999-12-31"), BaseCalciteQueryTest.d("2000-01-01") }));
    }

    @Test
    public void testFilterOnCurrentTimestampLosAngeles() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, BaseCalciteQueryTest.QUERY_CONTEXT_LOS_ANGELES, ("SELECT COUNT(*) FROM druid.foo\n" + "WHERE __time >= CURRENT_TIMESTAMP + INTERVAL '1' DAY AND __time < TIMESTAMP '2002-01-01 00:00:00'"), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-02T00Z/2002-01-01T08Z"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_LOS_ANGELES).build()), ImmutableList.of(new Object[]{ 5L }));
    }

    @Test
    public void testFilterOnCurrentTimestampOnView() throws Exception {
        testQuery("SELECT * FROM bview", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-02/2002"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 5L }));
    }

    @Test
    public void testFilterOnCurrentTimestampLosAngelesOnView() throws Exception {
        // Tests that query context still applies to view SQL; note the result is different from
        // "testFilterOnCurrentTimestampOnView" above.
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, BaseCalciteQueryTest.QUERY_CONTEXT_LOS_ANGELES, "SELECT * FROM bview", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-02T00Z/2002-01-01T08Z"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_LOS_ANGELES).build()), ImmutableList.of(new Object[]{ 5L }));
    }

    @Test
    public void testFilterOnNotTimeFloor() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo\n" + ("WHERE\n" + "FLOOR(__time TO MONTH) <> TIMESTAMP '2001-01-01 00:00:00'")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(new org.joda.time.Interval(DateTimes.MIN, DateTimes.of("2001-01-01")), new org.joda.time.Interval(DateTimes.of("2001-02-01"), DateTimes.MAX))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L }));
    }

    @Test
    public void testFilterOnTimeFloorComparison() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo\n" + ("WHERE\n" + "FLOOR(__time TO MONTH) < TIMESTAMP '2000-02-01 00:00:00'")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(new org.joda.time.Interval(DateTimes.MIN, DateTimes.of("2000-02-01")))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L }));
    }

    @Test
    public void testFilterOnTimeFloorComparisonMisaligned() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo\n" + ("WHERE\n" + "FLOOR(__time TO MONTH) < TIMESTAMP '2000-02-01 00:00:01'")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(new org.joda.time.Interval(DateTimes.MIN, DateTimes.of("2000-03-01")))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L }));
    }

    @Test
    public void testFilterOnTimeExtract() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo\n" + ("WHERE EXTRACT(YEAR FROM __time) = 2000\n" + "AND EXTRACT(MONTH FROM __time) = 1")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).filters(BaseCalciteQueryTest.and(BaseCalciteQueryTest.expressionFilter("(timestamp_extract(\"__time\",\'YEAR\',\'UTC\') == 2000)"), BaseCalciteQueryTest.expressionFilter("(timestamp_extract(\"__time\",\'MONTH\',\'UTC\') == 1)"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L }));
    }

    @Test
    public void testFilterOnTimeExtractWithMultipleDays() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo\n" + ("WHERE EXTRACT(YEAR FROM __time) = 2000\n" + "AND EXTRACT(DAY FROM __time) IN (2, 3, 5)")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).filters(BaseCalciteQueryTest.and(BaseCalciteQueryTest.expressionFilter("(timestamp_extract(\"__time\",\'YEAR\',\'UTC\') == 2000)"), BaseCalciteQueryTest.or(BaseCalciteQueryTest.expressionFilter("(timestamp_extract(\"__time\",\'DAY\',\'UTC\') == 2)"), BaseCalciteQueryTest.expressionFilter("(timestamp_extract(\"__time\",\'DAY\',\'UTC\') == 3)"), BaseCalciteQueryTest.expressionFilter("(timestamp_extract(\"__time\",\'DAY\',\'UTC\') == 5)")))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2L }));
    }

    @Test
    public void testFilterOnTimeFloorMisaligned() throws Exception {
        testQuery(("SELECT COUNT(*) FROM druid.foo " + "WHERE floor(__time TO month) = TIMESTAMP '2000-01-01 00:00:01'"), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec()).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of());
    }

    @Test
    public void testGroupByFloor() throws Exception {
        // Sanity check; this simple query should work with subqueries disabled.
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_NO_SUBQUERIES, "SELECT floor(CAST(dim1 AS float)), COUNT(*) FROM druid.foo GROUP BY floor(CAST(dim1 AS float))", CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "floor(CAST(\"dim1\", \'DOUBLE\'))", FLOAT)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.FLOAT))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ NullHandling.defaultFloatValue(), 3L }, new Object[]{ 1.0F, 1L }, new Object[]{ 2.0F, 1L }, new Object[]{ 10.0F, 1L }));
    }

    @Test
    public void testGroupByFloorWithOrderBy() throws Exception {
        testQuery("SELECT floor(CAST(dim1 AS float)) AS fl, COUNT(*) FROM druid.foo GROUP BY floor(CAST(dim1 AS float)) ORDER BY fl DESC", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "floor(CAST(\"dim1\", \'DOUBLE\'))", FLOAT)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.FLOAT))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.DESCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 10.0F, 1L }, new Object[]{ 2.0F, 1L }, new Object[]{ 1.0F, 1L }, new Object[]{ NullHandling.defaultFloatValue(), 3L }));
    }

    @Test
    public void testGroupByFloorTimeAndOneOtherDimensionWithOrderBy() throws Exception {
        testQuery(("SELECT floor(__time TO year), dim2, COUNT(*)" + ((" FROM druid.foo" + " GROUP BY floor(__time TO year), dim2") + " ORDER BY floor(__time TO year), dim2, COUNT(*) DESC")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "timestamp_floor(\"__time\",\'P1Y\',null,\'UTC\')", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG), new DefaultDimensionSpec("dim2", "d1"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.ASCENDING, StringComparators.NUMERIC), new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d1", Direction.ASCENDING, StringComparators.LEXICOGRAPHIC), new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("a0", Direction.DESCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000"), "", 2L }, new Object[]{ BaseCalciteQueryTest.t("2000"), "a", 1L }, new Object[]{ BaseCalciteQueryTest.t("2001"), "", 1L }, new Object[]{ BaseCalciteQueryTest.t("2001"), "a", 1L }, new Object[]{ BaseCalciteQueryTest.t("2001"), "abc", 1L }) : ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000"), null, 1L }, new Object[]{ BaseCalciteQueryTest.t("2000"), "", 1L }, new Object[]{ BaseCalciteQueryTest.t("2000"), "a", 1L }, new Object[]{ BaseCalciteQueryTest.t("2001"), null, 1L }, new Object[]{ BaseCalciteQueryTest.t("2001"), "a", 1L }, new Object[]{ BaseCalciteQueryTest.t("2001"), "abc", 1L })));
    }

    @Test
    public void testGroupByStringLength() throws Exception {
        testQuery("SELECT CHARACTER_LENGTH(dim1), COUNT(*) FROM druid.foo GROUP BY CHARACTER_LENGTH(dim1)", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "strlen(\"dim1\")", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 0, 1L }, new Object[]{ 1, 2L }, new Object[]{ 3, 2L }, new Object[]{ 4, 1L }));
    }

    @Test
    public void testFilterAndGroupByLookup() throws Exception {
        String nullValue = (NullHandling.replaceWithDefault()) ? "" : null;
        final RegisteredLookupExtractionFn extractionFn = new RegisteredLookupExtractionFn(null, "lookyloo", false, null, false, true);
        testQuery(("SELECT LOOKUP(dim1, \'lookyloo\'), COUNT(*) FROM foo\n" + ("WHERE LOOKUP(dim1, \'lookyloo\') <> \'xxx\'\n" + "GROUP BY LOOKUP(dim1, 'lookyloo')")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "xxx", extractionFn))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new org.apache.druid.query.dimension.ExtractionDimensionSpec("dim1", "d0", ValueType.STRING, extractionFn))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ nullValue, 5L }, new Object[]{ "xabc", 1L }));
    }

    @Test
    public void testCountDistinctOfLookup() throws Exception {
        final RegisteredLookupExtractionFn extractionFn = new RegisteredLookupExtractionFn(null, "lookyloo", false, null, false, true);
        testQuery("SELECT COUNT(DISTINCT LOOKUP(dim1, 'lookyloo')) FROM foo", ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a0", null, ImmutableList.of(new org.apache.druid.query.dimension.ExtractionDimensionSpec("dim1", null, extractionFn)), false, true))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ NullHandling.replaceWithDefault() ? 2L : 1L }));
    }

    @Test
    public void testTimeseries() throws Exception {
        testQuery(("SELECT SUM(cnt), gran FROM (\n" + (((("  SELECT floor(__time TO month) AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(MONTH).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L, BaseCalciteQueryTest.t("2000-01-01") }, new Object[]{ 3L, BaseCalciteQueryTest.t("2001-01-01") }));
    }

    @Test
    public void testFilteredTimeAggregators() throws Exception {
        testQuery(("SELECT\n" + ((((((("  SUM(cnt) FILTER(WHERE __time >= TIMESTAMP \'2000-01-01 00:00:00\'\n" + "                    AND __time <  TIMESTAMP \'2000-02-01 00:00:00\'),\n") + "  SUM(cnt) FILTER(WHERE __time >= TIMESTAMP \'2001-01-01 00:00:00\'\n") + "                    AND __time <  TIMESTAMP \'2001-02-01 00:00:00\')\n") + "FROM foo\n") + "WHERE\n") + "  __time >= TIMESTAMP \'2000-01-01 00:00:00\'\n") + "  AND __time < TIMESTAMP '2001-02-01 00:00:00'")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-01/2001-02-01"))).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new LongSumAggregatorFactory("a0", "cnt"), BaseCalciteQueryTest.bound("__time", String.valueOf(BaseCalciteQueryTest.t("2000-01-01")), String.valueOf(BaseCalciteQueryTest.t("2000-02-01")), false, true, null, NUMERIC)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new LongSumAggregatorFactory("a1", "cnt"), BaseCalciteQueryTest.bound("__time", String.valueOf(BaseCalciteQueryTest.t("2001-01-01")), String.valueOf(BaseCalciteQueryTest.t("2001-02-01")), false, true, null, NUMERIC)))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L, 3L }));
    }

    @Test
    public void testTimeseriesLosAngelesViaQueryContext() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, BaseCalciteQueryTest.QUERY_CONTEXT_LOS_ANGELES, ("SELECT SUM(cnt), gran FROM (\n" + (((("  SELECT FLOOR(__time TO MONTH) AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.months(1), null, DateTimes.inferTzFromString(BaseCalciteQueryTest.LOS_ANGELES))).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_LOS_ANGELES).build()), ImmutableList.of(new Object[]{ 1L, BaseCalciteQueryTest.t("1999-12-01", BaseCalciteQueryTest.LOS_ANGELES) }, new Object[]{ 2L, BaseCalciteQueryTest.t("2000-01-01", BaseCalciteQueryTest.LOS_ANGELES) }, new Object[]{ 1L, BaseCalciteQueryTest.t("2000-12-01", BaseCalciteQueryTest.LOS_ANGELES) }, new Object[]{ 2L, BaseCalciteQueryTest.t("2001-01-01", BaseCalciteQueryTest.LOS_ANGELES) }));
    }

    @Test
    public void testTimeseriesLosAngelesViaPlannerConfig() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_LOS_ANGELES, BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT, ("SELECT SUM(cnt), gran FROM (\n" + (((("  SELECT FLOOR(__time TO MONTH) AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.months(1), null, DateTimes.inferTzFromString(BaseCalciteQueryTest.LOS_ANGELES))).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, BaseCalciteQueryTest.t("1999-12-01", BaseCalciteQueryTest.LOS_ANGELES) }, new Object[]{ 2L, BaseCalciteQueryTest.t("2000-01-01", BaseCalciteQueryTest.LOS_ANGELES) }, new Object[]{ 1L, BaseCalciteQueryTest.t("2000-12-01", BaseCalciteQueryTest.LOS_ANGELES) }, new Object[]{ 2L, BaseCalciteQueryTest.t("2001-01-01", BaseCalciteQueryTest.LOS_ANGELES) }));
    }

    @Test
    public void testTimeseriesUsingTimeFloor() throws Exception {
        testQuery(("SELECT SUM(cnt), gran FROM (\n" + (((("  SELECT TIME_FLOOR(__time, \'P1M\') AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(MONTH).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L, BaseCalciteQueryTest.t("2000-01-01") }, new Object[]{ 3L, BaseCalciteQueryTest.t("2001-01-01") }));
    }

    @Test
    public void testTimeseriesUsingTimeFloorWithTimeShift() throws Exception {
        testQuery(("SELECT SUM(cnt), gran FROM (\n" + (((("  SELECT TIME_FLOOR(TIME_SHIFT(__time, \'P1D\', -1), \'P1M\') AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "timestamp_floor(timestamp_shift(\"__time\",\'P1D\',-1),\'P1M\',null,\'UTC\')", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.ASCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, BaseCalciteQueryTest.t("1999-12-01") }, new Object[]{ 2L, BaseCalciteQueryTest.t("2000-01-01") }, new Object[]{ 1L, BaseCalciteQueryTest.t("2000-12-01") }, new Object[]{ 2L, BaseCalciteQueryTest.t("2001-01-01") }));
    }

    @Test
    public void testTimeseriesUsingTimeFloorWithTimestampAdd() throws Exception {
        testQuery(("SELECT SUM(cnt), gran FROM (\n" + (((("  SELECT TIME_FLOOR(TIMESTAMPADD(DAY, -1, __time), \'P1M\') AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "timestamp_floor((\"__time\" + -86400000),\'P1M\',null,\'UTC\')", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.ASCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, BaseCalciteQueryTest.t("1999-12-01") }, new Object[]{ 2L, BaseCalciteQueryTest.t("2000-01-01") }, new Object[]{ 1L, BaseCalciteQueryTest.t("2000-12-01") }, new Object[]{ 2L, BaseCalciteQueryTest.t("2001-01-01") }));
    }

    @Test
    public void testTimeseriesUsingTimeFloorWithOrigin() throws Exception {
        testQuery(("SELECT SUM(cnt), gran FROM (\n" + (((("  SELECT TIME_FLOOR(__time, \'P1M\', TIMESTAMP \'1970-01-01 01:02:03\') AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.months(1), DateTimes.of("1970-01-01T01:02:03"), DateTimeZone.UTC)).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, BaseCalciteQueryTest.t("1999-12-01T01:02:03") }, new Object[]{ 2L, BaseCalciteQueryTest.t("2000-01-01T01:02:03") }, new Object[]{ 1L, BaseCalciteQueryTest.t("2000-12-01T01:02:03") }, new Object[]{ 2L, BaseCalciteQueryTest.t("2001-01-01T01:02:03") }));
    }

    @Test
    public void testTimeseriesLosAngelesUsingTimeFloorConnectionUtc() throws Exception {
        testQuery(("SELECT SUM(cnt), gran FROM (\n" + (((("  SELECT TIME_FLOOR(__time, \'P1M\', CAST(NULL AS TIMESTAMP), \'America/Los_Angeles\') AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.months(1), null, DateTimes.inferTzFromString(BaseCalciteQueryTest.LOS_ANGELES))).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, BaseCalciteQueryTest.t("1999-12-01T08") }, new Object[]{ 2L, BaseCalciteQueryTest.t("2000-01-01T08") }, new Object[]{ 1L, BaseCalciteQueryTest.t("2000-12-01T08") }, new Object[]{ 2L, BaseCalciteQueryTest.t("2001-01-01T08") }));
    }

    @Test
    public void testTimeseriesLosAngelesUsingTimeFloorConnectionLosAngeles() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, BaseCalciteQueryTest.QUERY_CONTEXT_LOS_ANGELES, ("SELECT SUM(cnt), gran FROM (\n" + (((("  SELECT TIME_FLOOR(__time, \'P1M\') AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.months(1), null, DateTimes.inferTzFromString(BaseCalciteQueryTest.LOS_ANGELES))).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_LOS_ANGELES).build()), ImmutableList.of(new Object[]{ 1L, BaseCalciteQueryTest.t("1999-12-01", BaseCalciteQueryTest.LOS_ANGELES) }, new Object[]{ 2L, BaseCalciteQueryTest.t("2000-01-01", BaseCalciteQueryTest.LOS_ANGELES) }, new Object[]{ 1L, BaseCalciteQueryTest.t("2000-12-01", BaseCalciteQueryTest.LOS_ANGELES) }, new Object[]{ 2L, BaseCalciteQueryTest.t("2001-01-01", BaseCalciteQueryTest.LOS_ANGELES) }));
    }

    @Test
    public void testTimeseriesDontSkipEmptyBuckets() throws Exception {
        // Tests that query context parameters are passed through to the underlying query engine.
        Long defaultVal = (NullHandling.replaceWithDefault()) ? 0L : null;
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, BaseCalciteQueryTest.QUERY_CONTEXT_DONT_SKIP_EMPTY_BUCKETS, ("SELECT SUM(cnt), gran FROM (\n" + (((("  SELECT floor(__time TO HOUR) AS gran, cnt FROM druid.foo\n" + "  WHERE __time >= TIMESTAMP \'2000-01-01 00:00:00\' AND __time < TIMESTAMP \'2000-01-02 00:00:00\'\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000/2000-01-02"))).granularity(new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.hours(1), null, DateTimeZone.UTC)).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.QUERY_CONTEXT_DONT_SKIP_EMPTY_BUCKETS).build()), ImmutableList.<Object[]>builder().add(new Object[]{ 1L, BaseCalciteQueryTest.t("2000-01-01") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T01") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T02") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T03") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T04") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T05") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T06") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T07") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T08") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T09") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T10") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T11") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T12") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T13") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T14") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T15") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T16") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T17") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T18") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T19") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T20") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T21") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T22") }).add(new Object[]{ defaultVal, BaseCalciteQueryTest.t("2000-01-01T23") }).build());
    }

    @Test
    public void testTimeseriesUsingCastAsDate() throws Exception {
        testQuery(("SELECT SUM(cnt), dt FROM (\n" + (((("  SELECT CAST(__time AS DATE) AS dt,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY dt\n") + "ORDER BY dt")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.days(1), null, DateTimeZone.UTC)).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, BaseCalciteQueryTest.d("2000-01-01") }, new Object[]{ 1L, BaseCalciteQueryTest.d("2000-01-02") }, new Object[]{ 1L, BaseCalciteQueryTest.d("2000-01-03") }, new Object[]{ 1L, BaseCalciteQueryTest.d("2001-01-01") }, new Object[]{ 1L, BaseCalciteQueryTest.d("2001-01-02") }, new Object[]{ 1L, BaseCalciteQueryTest.d("2001-01-03") }));
    }

    @Test
    public void testTimeseriesUsingFloorPlusCastAsDate() throws Exception {
        testQuery(("SELECT SUM(cnt), dt FROM (\n" + (((("  SELECT CASt(FLOOR(__time TO QUARTER) AS DATE) AS dt,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY dt\n") + "ORDER BY dt")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.months(3), null, DateTimeZone.UTC)).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L, BaseCalciteQueryTest.d("2000-01-01") }, new Object[]{ 3L, BaseCalciteQueryTest.d("2001-01-01") }));
    }

    @Test
    public void testTimeseriesDescending() throws Exception {
        testQuery(("SELECT gran, SUM(cnt) FROM (\n" + (((("  SELECT floor(__time TO month) AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran DESC")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(MONTH).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).descending(true).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2001-01-01"), 3L }, new Object[]{ BaseCalciteQueryTest.t("2000-01-01"), 3L }));
    }

    @Test
    public void testGroupByExtractYear() throws Exception {
        testQuery(("SELECT\n" + (((("  EXTRACT(YEAR FROM __time) AS \"year\",\n" + "  SUM(cnt)\n") + "FROM druid.foo\n") + "GROUP BY EXTRACT(YEAR FROM __time)\n") + "ORDER BY 1")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "timestamp_extract(\"__time\",\'YEAR\',\'UTC\')", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.ASCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2000L, 3L }, new Object[]{ 2001L, 3L }));
    }

    @Test
    public void testGroupByFormatYearAndMonth() throws Exception {
        testQuery(("SELECT\n" + (((("  TIME_FORMAT(__time, \'yyyy MM\') AS \"year\",\n" + "  SUM(cnt)\n") + "FROM druid.foo\n") + "GROUP BY TIME_FORMAT(__time, \'yyyy MM\')\n") + "ORDER BY 1")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "timestamp_format(\"__time\",\'yyyy MM\',\'UTC\')", STRING)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.STRING))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.ASCENDING, StringComparators.LEXICOGRAPHIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "2000 01", 3L }, new Object[]{ "2001 01", 3L }));
    }

    @Test
    public void testGroupByExtractFloorTime() throws Exception {
        testQuery(("SELECT\n" + (("EXTRACT(YEAR FROM FLOOR(__time TO YEAR)) AS \"year\", SUM(cnt)\n" + "FROM druid.foo\n") + "GROUP BY EXTRACT(YEAR FROM FLOOR(__time TO YEAR))")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "timestamp_extract(timestamp_floor(\"__time\",\'P1Y\',null,\'UTC\'),\'YEAR\',\'UTC\')", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2000L, 3L }, new Object[]{ 2001L, 3L }));
    }

    @Test
    public void testGroupByExtractFloorTimeLosAngeles() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, BaseCalciteQueryTest.QUERY_CONTEXT_LOS_ANGELES, ("SELECT\n" + (("EXTRACT(YEAR FROM FLOOR(__time TO YEAR)) AS \"year\", SUM(cnt)\n" + "FROM druid.foo\n") + "GROUP BY EXTRACT(YEAR FROM FLOOR(__time TO YEAR))")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "timestamp_extract(timestamp_floor(\"__time\",\'P1Y\',null,\'America/Los_Angeles\'),\'YEAR\',\'America/Los_Angeles\')", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_LOS_ANGELES).build()), ImmutableList.of(new Object[]{ 1999L, 1L }, new Object[]{ 2000L, 3L }, new Object[]{ 2001L, 2L }));
    }

    @Test
    public void testTimeseriesWithLimitNoTopN() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_NO_TOPN, ("SELECT gran, SUM(cnt)\n" + (((((("FROM (\n" + "  SELECT floor(__time TO month) AS gran, cnt\n") + "  FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran\n") + "LIMIT 1")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(MONTH).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).limit(1).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000-01-01"), 3L }));
    }

    @Test
    public void testTimeseriesWithLimit() throws Exception {
        testQuery(("SELECT gran, SUM(cnt)\n" + ((((("FROM (\n" + "  SELECT floor(__time TO month) AS gran, cnt\n") + "  FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "LIMIT 1")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(MONTH).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).limit(1).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000-01-01"), 3L }));
    }

    @Test
    public void testTimeseriesWithOrderByAndLimit() throws Exception {
        testQuery(("SELECT gran, SUM(cnt)\n" + (((((("FROM (\n" + "  SELECT floor(__time TO month) AS gran, cnt\n") + "  FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran\n") + "LIMIT 1")), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(MONTH).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).limit(1).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2000-01-01"), 3L }));
    }

    @Test
    public void testGroupByTimeAndOtherDimension() throws Exception {
        testQuery(("SELECT dim2, gran, SUM(cnt)\n" + (("FROM (SELECT FLOOR(__time TO MONTH) AS gran, dim2, cnt FROM druid.foo) AS x\n" + "GROUP BY dim2, gran\n") + "ORDER BY dim2, gran")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d1:v", "timestamp_floor(\"__time\",\'P1M\',null,\'UTC\')", LONG)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"), new DefaultDimensionSpec("d1:v", "d1", ValueType.LONG))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.ASCENDING), new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d1", Direction.ASCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "", BaseCalciteQueryTest.t("2000-01-01"), 2L }, new Object[]{ "", BaseCalciteQueryTest.t("2001-01-01"), 1L }, new Object[]{ "a", BaseCalciteQueryTest.t("2000-01-01"), 1L }, new Object[]{ "a", BaseCalciteQueryTest.t("2001-01-01"), 1L }, new Object[]{ "abc", BaseCalciteQueryTest.t("2001-01-01"), 1L }) : ImmutableList.of(new Object[]{ null, BaseCalciteQueryTest.t("2000-01-01"), 1L }, new Object[]{ null, BaseCalciteQueryTest.t("2001-01-01"), 1L }, new Object[]{ "", BaseCalciteQueryTest.t("2000-01-01"), 1L }, new Object[]{ "a", BaseCalciteQueryTest.t("2000-01-01"), 1L }, new Object[]{ "a", BaseCalciteQueryTest.t("2001-01-01"), 1L }, new Object[]{ "abc", BaseCalciteQueryTest.t("2001-01-01"), 1L })));
    }

    @Test
    public void testUsingSubqueryAsPartOfAndFilter() throws Exception {
        // Sanity check; this query should work with a single level of nesting.
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_SINGLE_NESTING_ONLY, ("SELECT dim1, dim2, COUNT(*) FROM druid.foo\n" + (("WHERE dim2 IN (SELECT dim1 FROM druid.foo WHERE dim1 <> \'\')\n" + "AND dim1 <> \'xxx\'\n") + "group by dim1, dim2 ORDER BY dim2")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "", null))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build(), GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.and(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "xxx", null)), BaseCalciteQueryTest.in("dim2", ImmutableList.of("1", "10.1", "2", "abc", "def"), null))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"), new DefaultDimensionSpec("dim2", "d1"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d1", Direction.ASCENDING)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "def", "abc", 1L }));
    }

    @Test
    public void testUsingSubqueryAsPartOfOrFilter() throws Exception {
        // This query should ideally be plannable without fallback, but it's not. The "OR" means it isn't really
        // a semiJoin and so the filter condition doesn't get converted.
        final String explanation = "BindableSort(sort0=[$1], dir0=[ASC])\n" + (((((("  BindableAggregate(group=[{0, 1}], EXPR$2=[COUNT()])\n" + "    BindableFilter(condition=[OR(=($0, \'xxx\'), CAST(AND(IS NOT NULL($4), <>($2, 0), IS NOT NULL($1))):BOOLEAN)])\n") + "      BindableJoin(condition=[=($1, $3)], joinType=[left])\n") + "        BindableJoin(condition=[true], joinType=[inner])\n") + "          DruidQueryRel(query=[{\"queryType\":\"scan\",\"dataSource\":{\"type\":\"table\",\"name\":\"foo\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"virtualColumns\":[],\"resultFormat\":\"compactedList\",\"batchSize\":20480,\"limit\":9223372036854775807,\"filter\":null,\"columns\":[\"dim1\",\"dim2\"],\"legacy\":false,\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"},\"descending\":false,\"granularity\":{\"type\":\"all\"}}], signature=[{dim1:STRING, dim2:STRING}])\n") + "          DruidQueryRel(query=[{\"queryType\":\"timeseries\",\"dataSource\":{\"type\":\"table\",\"name\":\"foo\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"descending\":false,\"virtualColumns\":[],\"filter\":{\"type\":\"like\",\"dimension\":\"dim1\",\"pattern\":\"%bc\",\"escape\":null,\"extractionFn\":null},\"granularity\":{\"type\":\"all\"},\"aggregations\":[{\"type\":\"count\",\"name\":\"a0\"}],\"postAggregations\":[],\"limit\":2147483647,\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"skipEmptyBuckets\":true,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"}}], signature=[{a0:LONG}])\n") + "        DruidQueryRel(query=[{\"queryType\":\"groupBy\",\"dataSource\":{\"type\":\"table\",\"name\":\"foo\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"virtualColumns\":[{\"type\":\"expression\",\"name\":\"d1:v\",\"expression\":\"1\",\"outputType\":\"LONG\"}],\"filter\":{\"type\":\"like\",\"dimension\":\"dim1\",\"pattern\":\"%bc\",\"escape\":null,\"extractionFn\":null},\"granularity\":{\"type\":\"all\"},\"dimensions\":[{\"type\":\"default\",\"dimension\":\"dim1\",\"outputName\":\"d0\",\"outputType\":\"STRING\"},{\"type\":\"default\",\"dimension\":\"d1:v\",\"outputName\":\"d1\",\"outputType\":\"LONG\"}],\"aggregations\":[],\"postAggregations\":[],\"having\":null,\"limitSpec\":{\"type\":\"NoopLimitSpec\"},\"context\":{\"defaultTimeout\":300000,\"maxScatterGatherBytes\":9223372036854775807,\"sqlCurrentTimestamp\":\"2000-01-01T00:00:00Z\",\"sqlQueryId\":\"dummy\"},\"descending\":false}], signature=[{d0:STRING, d1:LONG}])\n");
        final String theQuery = "SELECT dim1, dim2, COUNT(*) FROM druid.foo\n" + ("WHERE dim1 = \'xxx\' OR dim2 IN (SELECT dim1 FROM druid.foo WHERE dim1 LIKE \'%bc\')\n" + "group by dim1, dim2 ORDER BY dim2");
        assertQueryIsUnplannable(theQuery);
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_FALLBACK, ("EXPLAIN PLAN FOR " + theQuery), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(), ImmutableList.of(new Object[]{ explanation }));
    }

    @Test
    public void testUsingSubqueryAsFilterForbiddenByConfig() {
        assertQueryIsUnplannable(BaseCalciteQueryTest.PLANNER_CONFIG_NO_SUBQUERIES, ("SELECT dim1, dim2, COUNT(*) FROM druid.foo " + (("WHERE dim2 IN (SELECT dim1 FROM druid.foo WHERE dim1 <> '')" + "AND dim1 <> 'xxx'") + "group by dim1, dim2 ORDER BY dim2")));
    }

    @Test
    public void testUsingSubqueryAsFilterOnTwoColumns() throws Exception {
        testQuery(("SELECT __time, cnt, dim1, dim2 FROM druid.foo " + ((((((((" WHERE (dim1, dim2) IN (" + "   SELECT dim1, dim2 FROM (") + "     SELECT dim1, dim2, COUNT(*)") + "     FROM druid.foo") + "     WHERE dim2 = 'abc'") + "     GROUP BY dim1, dim2") + "     HAVING COUNT(*) = 1") + "   )") + " )")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.selector("dim2", "abc", null)).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"), new DefaultDimensionSpec("dim2", "d1"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setHavingSpec(BaseCalciteQueryTest.having(BaseCalciteQueryTest.numeric_Selector("a0", "1", null))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build(), BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.or(BaseCalciteQueryTest.selector("dim1", "def", null), BaseCalciteQueryTest.and(BaseCalciteQueryTest.selector("dim1", "def", null), BaseCalciteQueryTest.selector("dim2", "abc", null)))).columns("__time", "cnt", "dim1", "dim2").resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ BaseCalciteQueryTest.t("2001-01-02"), 1L, "def", "abc" }));
    }

    @Test
    public void testUsingSubqueryAsFilterWithInnerSort() throws Exception {
        String nullValue = (NullHandling.replaceWithDefault()) ? "" : null;
        // Regression test for https://github.com/apache/incubator-druid/issues/4208
        testQuery(("SELECT dim1, dim2 FROM druid.foo\n" + (((((" WHERE dim2 IN (\n" + "   SELECT dim2\n") + "   FROM druid.foo\n") + "   GROUP BY dim2\n") + "   ORDER BY dim2 DESC\n") + " )")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.DESCENDING, StringComparators.LEXICOGRAPHIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build(), BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.in("dim2", ImmutableList.of("", "a", "abc"), null)).columns("dim1", "dim2").context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ "", "a" }, new Object[]{ "10.1", nullValue }, new Object[]{ "2", "" }, new Object[]{ "1", "a" }, new Object[]{ "def", "abc" }, new Object[]{ "abc", nullValue }) : ImmutableList.of(new Object[]{ "", "a" }, new Object[]{ "2", "" }, new Object[]{ "1", "a" }, new Object[]{ "def", "abc" })));
    }

    @Test
    public void testSemiJoinWithOuterTimeExtractScan() throws Exception {
        testQuery(("SELECT dim1, EXTRACT(MONTH FROM __time) FROM druid.foo\n" + ((((" WHERE dim2 IN (\n" + "   SELECT dim2\n") + "   FROM druid.foo\n") + "   WHERE dim1 = \'def\'\n") + " ) AND dim1 <> ''")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setDimFilter(BaseCalciteQueryTest.selector("dim1", "def", null)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build(), BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).virtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("v0", "timestamp_extract(\"__time\",\'MONTH\',\'UTC\')", LONG)).filters(BaseCalciteQueryTest.and(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "", null)), BaseCalciteQueryTest.selector("dim2", "abc", null))).columns("dim1", "v0").context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "def", 1L }));
    }

    @Test
    public void testSemiJoinWithOuterTimeExtractAggregateWithOrderBy() throws Exception {
        testQuery(("SELECT COUNT(DISTINCT dim1), EXTRACT(MONTH FROM __time) FROM druid.foo\n" + ((((((" WHERE dim2 IN (\n" + "   SELECT dim2\n") + "   FROM druid.foo\n") + "   WHERE dim1 = \'def\'\n") + " ) AND dim1 <> ''") + "GROUP BY EXTRACT(MONTH FROM __time)\n") + "ORDER BY EXTRACT(MONTH FROM __time)")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setDimFilter(BaseCalciteQueryTest.selector("dim1", "def", null)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build(), GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setVirtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("d0:v", "timestamp_extract(\"__time\",\'MONTH\',\'UTC\')", LONG)).setDimFilter(BaseCalciteQueryTest.and(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "", null)), BaseCalciteQueryTest.selector("dim2", "abc", null))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG))).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorFactory("a0", null, ImmutableList.of(new DefaultDimensionSpec("dim1", "dim1", ValueType.STRING)), false, true))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(ImmutableList.of(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.ASCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L, 1L }));
    }

    @Test
    public void testUsingSubqueryWithExtractionFns() throws Exception {
        testQuery(("SELECT dim2, COUNT(*) FROM druid.foo " + ("WHERE substring(dim2, 1, 1) IN (SELECT substring(dim1, 1, 1) FROM druid.foo WHERE dim1 <> '')" + "group by dim2")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "", null))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new org.apache.druid.query.dimension.ExtractionDimensionSpec("dim1", "d0", new SubstringDimExtractionFn(0, 1)))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build(), GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.in("dim2", ImmutableList.of("1", "2", "a", "d"), new SubstringDimExtractionFn(0, 1))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "a", 2L }, new Object[]{ "abc", 1L }));
    }

    @Test
    public void testUnicodeFilterAndGroupBy() throws Exception {
        testQuery(("SELECT\n" + ((((((("  dim1,\n" + "  dim2,\n") + "  COUNT(*)\n") + "FROM foo2\n") + "WHERE\n") + "  dim1 LIKE U&\'\u05d3\\05E8%\'\n")// First char is actually in the string; second is a SQL U& escape
         + "  OR dim1 = \'\u0434\u0440\u0443\u0438\u0434\'\n") + "GROUP BY dim1, dim2")), ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE2).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.or(new LikeDimFilter("dim1", "??%", null, null), new SelectorDimFilter("dim1", "?????", null))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"), new DefaultDimensionSpec("dim2", "d1"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "?????", "ru", 1L }, new Object[]{ "??????", "he", 1L }));
    }

    @Test
    public void testProjectAfterSort() throws Exception {
        testQuery("select dim1 from (select dim1, dim2, count(*) cnt from druid.foo group by dim1, dim2 order by cnt)", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"), new DefaultDimensionSpec("dim2", "d1"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(Collections.singletonList(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("a0", Direction.ASCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "" }, new Object[]{ "1" }, new Object[]{ "10.1" }, new Object[]{ "2" }, new Object[]{ "abc" }, new Object[]{ "def" }));
    }

    @Test
    public void testProjectAfterSort2() throws Exception {
        testQuery("select s / cnt, dim1, dim2, s from (select dim1, dim2, count(*) cnt, sum(m2) s from druid.foo group by dim1, dim2 order by cnt)", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"), new DefaultDimensionSpec("dim2", "d1"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"), new DoubleSumAggregatorFactory("a1", "m2"))).setPostAggregatorSpecs(Collections.singletonList(BaseCalciteQueryTest.expresionPostAgg("s0", "(\"a1\" / \"a0\")"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(Collections.singletonList(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("a0", Direction.ASCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1.0, "", "a", 1.0 }, new Object[]{ 4.0, "1", "a", 4.0 }, new Object[]{ 2.0, "10.1", NullHandling.defaultStringValue(), 2.0 }, new Object[]{ 3.0, "2", "", 3.0 }, new Object[]{ 6.0, "abc", NullHandling.defaultStringValue(), 6.0 }, new Object[]{ 5.0, "def", "abc", 5.0 }));
    }

    @Test
    public void testProjectAfterSort3() throws Exception {
        testQuery("select dim1 from (select dim1, dim1, count(*) cnt from druid.foo group by dim1, dim1 order by cnt)", ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim1", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(Collections.singletonList(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("a0", Direction.ASCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "" }, new Object[]{ "1" }, new Object[]{ "10.1" }, new Object[]{ "2" }, new Object[]{ "abc" }, new Object[]{ "def" }));
    }

    @Test
    public void testSortProjectAfterNestedGroupBy() throws Exception {
        testQuery(("SELECT " + (((((((((((((((("  cnt " + "FROM (") + "  SELECT ") + "    __time, ") + "    dim1, ") + "    COUNT(m2) AS cnt ") + "  FROM (") + "    SELECT ") + "        __time, ") + "        m2, ") + "        dim1 ") + "    FROM druid.foo ") + "    GROUP BY __time, m2, dim1 ") + "  ) ") + "  GROUP BY __time, dim1 ") + "  ORDER BY cnt") + ")")), ImmutableList.of(GroupByQuery.builder().setDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("__time", "d0", ValueType.LONG), new DefaultDimensionSpec("m2", "d1", ValueType.DOUBLE), new DefaultDimensionSpec("dim1", "d2"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("d0", "_d0", ValueType.LONG), new DefaultDimensionSpec("d2", "_d1", ValueType.STRING))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(Collections.singletonList(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("a0", Direction.ASCENDING, StringComparators.NUMERIC)), Integer.MAX_VALUE)).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }, new Object[]{ 1L }, new Object[]{ 1L }, new Object[]{ 1L }, new Object[]{ 1L }, new Object[]{ 1L }));
    }

    @Test
    public void testPostAggWithTimeseries() throws Exception {
        testQuery(("SELECT " + (((((((("  FLOOR(__time TO YEAR), " + "  SUM(m1), ") + "  SUM(m1) + SUM(m2) ") + "FROM ") + "  druid.foo ") + "WHERE ") + "  dim2 = 'a' ") + "GROUP BY FLOOR(__time TO YEAR) ") + "ORDER BY FLOOR(__time TO YEAR) desc")), Collections.singletonList(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.selector("dim2", "a", null)).granularity(YEAR).aggregators(BaseCalciteQueryTest.aggregators(new DoubleSumAggregatorFactory("a0", "m1"), new DoubleSumAggregatorFactory("a1", "m2"))).postAggregators(BaseCalciteQueryTest.expresionPostAgg("p0", "(\"a0\" + \"a1\")")).descending(true).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 978307200000L, 4.0, 8.0 }, new Object[]{ 946684800000L, 1.0, 2.0 }));
    }

    @Test
    public void testPostAggWithTopN() throws Exception {
        testQuery(("SELECT " + (((((((("  AVG(m2), " + "  SUM(m1) + SUM(m2) ") + "FROM ") + "  druid.foo ") + "WHERE ") + "  dim2 = 'a' ") + "GROUP BY m1 ") + "ORDER BY m1 ") + "LIMIT 5")), Collections.singletonList(new TopNQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).granularity(ALL).dimension(new DefaultDimensionSpec("m1", "d0", ValueType.FLOAT)).filters("dim2", "a").aggregators(BaseCalciteQueryTest.aggregators(new DoubleSumAggregatorFactory("a0:sum", "m2"), new CountAggregatorFactory("a0:count"), new DoubleSumAggregatorFactory("a1", "m1"), new DoubleSumAggregatorFactory("a2", "m2"))).postAggregators(ImmutableList.of(new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("a0", "quotient", ImmutableList.of(new FieldAccessPostAggregator(null, "a0:sum"), new FieldAccessPostAggregator(null, "a0:count"))), BaseCalciteQueryTest.expresionPostAgg("p0", "(\"a1\" + \"a2\")"))).metric(new org.apache.druid.query.topn.DimensionTopNMetricSpec(null, StringComparators.NUMERIC)).threshold(5).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1.0, 2.0 }, new Object[]{ 4.0, 8.0 }));
    }

    @Test
    public void testConcat() throws Exception {
        testQuery("SELECT CONCAT(dim1, '-', dim1, '_', dim1) as dimX FROM foo", ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).virtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("v0", "concat(\"dim1\",\'-\',\"dim1\",\'_\',\"dim1\")", STRING)).columns("v0").resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "-_" }, new Object[]{ "10.1-10.1_10.1" }, new Object[]{ "2-2_2" }, new Object[]{ "1-1_1" }, new Object[]{ "def-def_def" }, new Object[]{ "abc-abc_abc" }));
        testQuery("SELECT CONCAT(dim1, CONCAT(dim2,'x'), m2, 9999, dim1) as dimX FROM foo", ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).virtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("v0", "concat(\"dim1\",concat(\"dim2\",\'x\'),\"m2\",9999,\"dim1\")", STRING)).columns("v0").resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), // dim2 is null
        // dim2 is null
        ImmutableList.of(new Object[]{ "ax1.09999" }, new Object[]{ NullHandling.sqlCompatible() ? null : "10.1x2.0999910.1" }, new Object[]{ "2x3.099992" }, new Object[]{ "1ax4.099991" }, new Object[]{ "defabcx5.09999def" }, new Object[]{ NullHandling.sqlCompatible() ? null : "abcx6.09999abc" }));
    }

    @Test
    public void testTextcat() throws Exception {
        testQuery("SELECT textcat(dim1, dim1) as dimX FROM foo", ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).virtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("v0", "concat(\"dim1\",\"dim1\")", STRING)).columns("v0").resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "" }, new Object[]{ "10.110.1" }, new Object[]{ "22" }, new Object[]{ "11" }, new Object[]{ "defdef" }, new Object[]{ "abcabc" }));
        testQuery("SELECT textcat(dim1, CAST(m2 as VARCHAR)) as dimX FROM foo", ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).virtualColumns(BaseCalciteQueryTest.expression_Virtual_Column("v0", "concat(\"dim1\",CAST(\"m2\", \'STRING\'))", STRING)).columns("v0").resultFormat(RESULT_FORMAT_COMPACTED_LIST).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "1.0" }, new Object[]{ "10.12.0" }, new Object[]{ "23.0" }, new Object[]{ "14.0" }, new Object[]{ "def5.0" }, new Object[]{ "abc6.0" }));
    }

    @Test
    public void testRequireTimeConditionPositive() throws Exception {
        // simple timeseries
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_REQUIRE_TIME_CONDITION, ("SELECT SUM(cnt), gran FROM (\n" + ((((("  SELECT __time as t, floor(__time TO month) AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "WHERE t >= '2000-01-01' and t < '2002-01-01'") + "GROUP BY gran\n") + "ORDER BY gran")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.of("2000-01-01/2002-01-01"))).granularity(MONTH).aggregators(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L, BaseCalciteQueryTest.t("2000-01-01") }, new Object[]{ 3L, BaseCalciteQueryTest.t("2001-01-01") }));
        // nested groupby only requires time condition for inner most query
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_REQUIRE_TIME_CONDITION, ("SELECT\n" + (("  SUM(cnt),\n" + "  COUNT(*)\n") + "FROM (SELECT dim2, SUM(cnt) AS cnt FROM druid.foo WHERE __time >= '2000-01-01' GROUP BY dim2)")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Intervals.utc(DateTimes.of("2000-01-01").getMillis(), MAX_INSTANT))).setGranularity(ALL).setDimensions(BaseCalciteQueryTest.dimensionSpec(new DefaultDimensionSpec("dim2", "d0"))).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("a0", "cnt"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build())).setInterval(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).setGranularity(ALL).setAggregatorSpecs(BaseCalciteQueryTest.aggregators(new LongSumAggregatorFactory("_a0", "a0"), new CountAggregatorFactory("_a1"))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), (NullHandling.replaceWithDefault() ? ImmutableList.of(new Object[]{ 6L, 3L }) : ImmutableList.of(new Object[]{ 6L, 4L })));
        // semi-join requires time condition on both left and right query
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_REQUIRE_TIME_CONDITION, ("SELECT COUNT(*) FROM druid.foo\n" + ((("WHERE __time >= \'2000-01-01\' AND SUBSTRING(dim2, 1, 1) IN (\n" + "  SELECT SUBSTRING(dim1, 1, 1) FROM druid.foo\n") + "  WHERE dim1 <> \'\' AND __time >= \'2000-01-01\'\n") + ")")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(GroupByQuery.builder().setDataSource(CalciteTests.DATASOURCE1).setInterval(BaseCalciteQueryTest.querySegmentSpec(Intervals.utc(DateTimes.of("2000-01-01").getMillis(), MAX_INSTANT))).setGranularity(ALL).setDimFilter(BaseCalciteQueryTest.not(BaseCalciteQueryTest.selector("dim1", "", null))).setDimensions(BaseCalciteQueryTest.dimensionSpec(new org.apache.druid.query.dimension.ExtractionDimensionSpec("dim1", "d0", new SubstringDimExtractionFn(0, 1)))).setContext(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build(), Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Intervals.utc(DateTimes.of("2000-01-01").getMillis(), MAX_INSTANT))).granularity(ALL).filters(BaseCalciteQueryTest.in("dim2", ImmutableList.of("1", "2", "a", "d"), new SubstringDimExtractionFn(0, 1))).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).context(BaseCalciteQueryTest.TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 3L }));
    }

    @Test
    public void testRequireTimeConditionSimpleQueryNegative() throws Exception {
        expectedException.expect(CannotBuildQueryException.class);
        expectedException.expectMessage("__time column");
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_REQUIRE_TIME_CONDITION, ("SELECT SUM(cnt), gran FROM (\n" + (((("  SELECT __time as t, floor(__time TO month) AS gran,\n" + "  cnt FROM druid.foo\n") + ") AS x\n") + "GROUP BY gran\n") + "ORDER BY gran")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(), ImmutableList.of());
    }

    @Test
    public void testRequireTimeConditionSubQueryNegative() throws Exception {
        expectedException.expect(CannotBuildQueryException.class);
        expectedException.expectMessage("__time column");
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_REQUIRE_TIME_CONDITION, ("SELECT\n" + (("  SUM(cnt),\n" + "  COUNT(*)\n") + "FROM (SELECT dim2, SUM(cnt) AS cnt FROM druid.foo GROUP BY dim2)")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(), ImmutableList.of());
    }

    @Test
    public void testRequireTimeConditionSemiJoinNegative() throws Exception {
        expectedException.expect(CannotBuildQueryException.class);
        expectedException.expectMessage("__time column");
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_REQUIRE_TIME_CONDITION, ("SELECT COUNT(*) FROM druid.foo\n" + ((("WHERE SUBSTRING(dim2, 1, 1) IN (\n" + "  SELECT SUBSTRING(dim1, 1, 1) FROM druid.foo\n") + "  WHERE dim1 <> \'\' AND __time >= \'2000-01-01\'\n") + ")")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(), ImmutableList.of());
    }

    @Test
    public void testFilterFloatDimension() throws Exception {
        testQuery("SELECT dim1 FROM numfoo WHERE f1 = 0.1 LIMIT 1", ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE3).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).columns("dim1").filters(BaseCalciteQueryTest.selector("f1", "0.1", null)).resultFormat(RESULT_FORMAT_COMPACTED_LIST).limit(1).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "10.1" }));
    }

    @Test
    public void testFilterDoubleDimension() throws Exception {
        testQuery("SELECT dim1 FROM numfoo WHERE d1 = 1.7 LIMIT 1", ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE3).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).columns("dim1").filters(BaseCalciteQueryTest.selector("d1", "1.7", null)).resultFormat(RESULT_FORMAT_COMPACTED_LIST).limit(1).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "10.1" }));
    }

    @Test
    public void testFilterLongDimension() throws Exception {
        testQuery("SELECT dim1 FROM numfoo WHERE l1 = 7 LIMIT 1", ImmutableList.of(BaseCalciteQueryTest.newScanQueryBuilder().dataSource(CalciteTests.DATASOURCE3).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).columns("dim1").filters(BaseCalciteQueryTest.selector("l1", "7", null)).resultFormat(RESULT_FORMAT_COMPACTED_LIST).limit(1).context(BaseCalciteQueryTest.QUERY_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ "" }));
    }

    @Test
    public void testTrigonometricFunction() throws Exception {
        testQuery(BaseCalciteQueryTest.PLANNER_CONFIG_DEFAULT, BaseCalciteQueryTest.QUERY_CONTEXT_DONT_SKIP_EMPTY_BUCKETS, ("SELECT exp(count(*)) + 10, sin(pi / 6), cos(pi / 6), tan(pi / 6), cot(pi / 6)," + ("asin(exp(count(*)) / 2), acos(exp(count(*)) / 2), atan(exp(count(*)) / 2), atan2(exp(count(*)), 1) " + "FROM druid.foo WHERE  dim2 = 0")), CalciteTests.REGULAR_USER_AUTH_RESULT, ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(CalciteTests.DATASOURCE1).intervals(BaseCalciteQueryTest.querySegmentSpec(Filtration.eternity())).filters(BaseCalciteQueryTest.selector("dim2", "0", null)).granularity(ALL).aggregators(BaseCalciteQueryTest.aggregators(new CountAggregatorFactory("a0"))).postAggregators(BaseCalciteQueryTest.expresionPostAgg("p0", "(exp(\"a0\") + 10)"), BaseCalciteQueryTest.expresionPostAgg("p1", "sin((pi() / 6))"), BaseCalciteQueryTest.expresionPostAgg("p2", "cos((pi() / 6))"), BaseCalciteQueryTest.expresionPostAgg("p3", "tan((pi() / 6))"), BaseCalciteQueryTest.expresionPostAgg("p4", "cot((pi() / 6))"), BaseCalciteQueryTest.expresionPostAgg("p5", "asin((exp(\"a0\") / 2))"), BaseCalciteQueryTest.expresionPostAgg("p6", "acos((exp(\"a0\") / 2))"), BaseCalciteQueryTest.expresionPostAgg("p7", "atan((exp(\"a0\") / 2))"), BaseCalciteQueryTest.expresionPostAgg("p8", "atan2(exp(\"a0\"),1)")).context(BaseCalciteQueryTest.QUERY_CONTEXT_DONT_SKIP_EMPTY_BUCKETS).build()), ImmutableList.of(new Object[]{ 11.0, Math.sin(((Math.PI) / 6)), Math.cos(((Math.PI) / 6)), Math.tan(((Math.PI) / 6)), (Math.cos(((Math.PI) / 6))) / (Math.sin(((Math.PI) / 6))), Math.asin(0.5), Math.acos(0.5), Math.atan(0.5), Math.atan2(1, 1) }));
    }
}

