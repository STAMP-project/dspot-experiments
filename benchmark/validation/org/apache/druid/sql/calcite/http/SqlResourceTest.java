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
package org.apache.druid.sql.calcite.http;


import AuthConfig.DRUID_ALLOW_UNSECURED_PATH;
import AuthConfig.DRUID_AUTHENTICATION_RESULT;
import AuthConfig.DRUID_AUTHORIZATION_CHECKED;
import PlannerContext.CTX_SQL_QUERY_ID;
import PlannerContext.CTX_SQL_TIME_ZONE;
import QueryInterruptedException.RESOURCE_LIMIT_EXCEEDED;
import QueryInterruptedException.UNKNOWN_EXCEPTION;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.calcite.tools.ValidationException;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.ISE;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.QueryInterruptedException;
import org.apache.druid.query.QueryRunnerFactoryConglomerate;
import org.apache.druid.query.ResourceLimitExceededException;
import org.apache.druid.server.log.TestRequestLogger;
import org.apache.druid.server.security.ForbiddenException;
import org.apache.druid.sql.calcite.util.CalciteTestBase;
import org.apache.druid.sql.calcite.util.CalciteTests;
import org.apache.druid.sql.calcite.util.QueryLogHook;
import org.apache.druid.sql.calcite.util.SpecificSegmentsQuerySegmentWalker;
import org.apache.druid.sql.http.ResultFormat;
import org.apache.druid.sql.http.SqlQuery;
import org.apache.druid.sql.http.SqlResource;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class SqlResourceTest extends CalciteTestBase {
    private static final ObjectMapper JSON_MAPPER = new DefaultObjectMapper();

    private static final String DUMMY_SQL_QUERY_ID = "dummy";

    private static QueryRunnerFactoryConglomerate conglomerate;

    private static Closer resourceCloser;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public QueryLogHook queryLogHook = QueryLogHook.create();

    private SpecificSegmentsQuerySegmentWalker walker = null;

    private TestRequestLogger testRequestLogger;

    private SqlResource resource;

    private HttpServletRequest req;

    @Test
    public void testUnauthorized() throws Exception {
        HttpServletRequest testRequest = EasyMock.createStrictMock(HttpServletRequest.class);
        EasyMock.expect(testRequest.getRemoteAddr()).andReturn(null).once();
        EasyMock.expect(testRequest.getAttribute(DRUID_AUTHENTICATION_RESULT)).andReturn(CalciteTests.REGULAR_USER_AUTH_RESULT).anyTimes();
        EasyMock.expect(testRequest.getAttribute(DRUID_ALLOW_UNSECURED_PATH)).andReturn(null).anyTimes();
        EasyMock.expect(testRequest.getAttribute(DRUID_AUTHORIZATION_CHECKED)).andReturn(null).anyTimes();
        EasyMock.expect(testRequest.getAttribute(DRUID_AUTHENTICATION_RESULT)).andReturn(CalciteTests.REGULAR_USER_AUTH_RESULT).anyTimes();
        testRequest.setAttribute(DRUID_AUTHORIZATION_CHECKED, false);
        EasyMock.expectLastCall().once();
        EasyMock.replay(testRequest);
        try {
            resource.doPost(new SqlQuery("select count(*) from forbiddenDatasource", null, false, null), testRequest);
            Assert.fail("doPost did not throw ForbiddenException for an unauthorized query");
        } catch (ForbiddenException e) {
            // expected
        }
        Assert.assertEquals(0, testRequestLogger.getSqlQueryLogs().size());
    }

    @Test
    public void testCountStar() throws Exception {
        final List<Map<String, Object>> rows = doPost(new SqlQuery("SELECT COUNT(*) AS cnt, 'foo' AS TheFoo FROM druid.foo", null, false, null)).rhs;
        Assert.assertEquals(ImmutableList.of(ImmutableMap.of("cnt", 6, "TheFoo", "foo")), rows);
        checkSqlRequestLog(true);
    }

    @Test
    public void testTimestampsInResponse() throws Exception {
        final List<Map<String, Object>> rows = doPost(new SqlQuery("SELECT __time, CAST(__time AS DATE) AS t2 FROM druid.foo LIMIT 1", ResultFormat.OBJECT, false, null)).rhs;
        Assert.assertEquals(ImmutableList.of(ImmutableMap.of("__time", "2000-01-01T00:00:00.000Z", "t2", "2000-01-01T00:00:00.000Z")), rows);
    }

    @Test
    public void testTimestampsInResponseLosAngelesTimeZone() throws Exception {
        final List<Map<String, Object>> rows = doPost(new SqlQuery("SELECT __time, CAST(__time AS DATE) AS t2 FROM druid.foo LIMIT 1", ResultFormat.OBJECT, false, ImmutableMap.of(CTX_SQL_TIME_ZONE, "America/Los_Angeles"))).rhs;
        Assert.assertEquals(ImmutableList.of(ImmutableMap.of("__time", "1999-12-31T16:00:00.000-08:00", "t2", "1999-12-31T00:00:00.000-08:00")), rows);
    }

    @Test
    public void testFieldAliasingSelect() throws Exception {
        final List<Map<String, Object>> rows = doPost(new SqlQuery("SELECT dim2 \"x\", dim2 \"y\" FROM druid.foo LIMIT 1", ResultFormat.OBJECT, false, null)).rhs;
        Assert.assertEquals(ImmutableList.of(ImmutableMap.of("x", "a", "y", "a")), rows);
    }

    @Test
    public void testFieldAliasingGroupBy() throws Exception {
        final List<Map<String, Object>> rows = doPost(new SqlQuery("SELECT dim2 \"x\", dim2 \"y\" FROM druid.foo GROUP BY dim2", ResultFormat.OBJECT, false, null)).rhs;
        Assert.assertEquals((NullHandling.replaceWithDefault() ? ImmutableList.of(ImmutableMap.of("x", "", "y", ""), ImmutableMap.of("x", "a", "y", "a"), ImmutableMap.of("x", "abc", "y", "abc")) : // x and y both should be null instead of empty string
        ImmutableList.of(Maps.transformValues(ImmutableMap.of("x", "", "y", ""), ( val) -> null), ImmutableMap.of("x", "", "y", ""), ImmutableMap.of("x", "a", "y", "a"), ImmutableMap.of("x", "abc", "y", "abc"))), rows);
    }

    @Test
    public void testArrayResultFormat() throws Exception {
        final String query = "SELECT *, CASE dim2 WHEN '' THEN dim2 END FROM foo LIMIT 2";
        final String nullStr = (NullHandling.replaceWithDefault()) ? "" : null;
        Assert.assertEquals(ImmutableList.of(Arrays.asList("2000-01-01T00:00:00.000Z", 1, "", "a", "[\"a\",\"b\"]", 1.0, 1.0, "org.apache.druid.hll.VersionOneHyperLogLogCollector", nullStr), Arrays.asList("2000-01-02T00:00:00.000Z", 1, "10.1", nullStr, "[\"b\",\"c\"]", 2.0, 2.0, "org.apache.druid.hll.VersionOneHyperLogLogCollector", nullStr)), doPost(new SqlQuery(query, ResultFormat.ARRAY, false, null), new TypeReference<List<List<Object>>>() {}).rhs);
    }

    @Test
    public void testArrayResultFormatWithHeader() throws Exception {
        final String query = "SELECT *, CASE dim2 WHEN '' THEN dim2 END FROM foo LIMIT 2";
        final String nullStr = (NullHandling.replaceWithDefault()) ? "" : null;
        Assert.assertEquals(ImmutableList.of(Arrays.asList("__time", "cnt", "dim1", "dim2", "dim3", "m1", "m2", "unique_dim1", "EXPR$8"), Arrays.asList("2000-01-01T00:00:00.000Z", 1, "", "a", "[\"a\",\"b\"]", 1.0, 1.0, "org.apache.druid.hll.VersionOneHyperLogLogCollector", nullStr), Arrays.asList("2000-01-02T00:00:00.000Z", 1, "10.1", nullStr, "[\"b\",\"c\"]", 2.0, 2.0, "org.apache.druid.hll.VersionOneHyperLogLogCollector", nullStr)), doPost(new SqlQuery(query, ResultFormat.ARRAY, true, null), new TypeReference<List<List<Object>>>() {}).rhs);
    }

    @Test
    public void testArrayLinesResultFormat() throws Exception {
        final String query = "SELECT *, CASE dim2 WHEN '' THEN dim2 END FROM foo LIMIT 2";
        final String response = doPostRaw(new SqlQuery(query, ResultFormat.ARRAYLINES, false, null)).rhs;
        final String nullStr = (NullHandling.replaceWithDefault()) ? "" : null;
        final List<String> lines = Splitter.on('\n').splitToList(response);
        Assert.assertEquals(4, lines.size());
        Assert.assertEquals(Arrays.asList("2000-01-01T00:00:00.000Z", 1, "", "a", "[\"a\",\"b\"]", 1.0, 1.0, "org.apache.druid.hll.VersionOneHyperLogLogCollector", nullStr), SqlResourceTest.JSON_MAPPER.readValue(lines.get(0), List.class));
        Assert.assertEquals(Arrays.asList("2000-01-02T00:00:00.000Z", 1, "10.1", nullStr, "[\"b\",\"c\"]", 2.0, 2.0, "org.apache.druid.hll.VersionOneHyperLogLogCollector", nullStr), SqlResourceTest.JSON_MAPPER.readValue(lines.get(1), List.class));
        Assert.assertEquals("", lines.get(2));
        Assert.assertEquals("", lines.get(3));
    }

    @Test
    public void testArrayLinesResultFormatWithHeader() throws Exception {
        final String query = "SELECT *, CASE dim2 WHEN '' THEN dim2 END FROM foo LIMIT 2";
        final String response = doPostRaw(new SqlQuery(query, ResultFormat.ARRAYLINES, true, null)).rhs;
        final String nullStr = (NullHandling.replaceWithDefault()) ? "" : null;
        final List<String> lines = Splitter.on('\n').splitToList(response);
        Assert.assertEquals(5, lines.size());
        Assert.assertEquals(Arrays.asList("__time", "cnt", "dim1", "dim2", "dim3", "m1", "m2", "unique_dim1", "EXPR$8"), SqlResourceTest.JSON_MAPPER.readValue(lines.get(0), List.class));
        Assert.assertEquals(Arrays.asList("2000-01-01T00:00:00.000Z", 1, "", "a", "[\"a\",\"b\"]", 1.0, 1.0, "org.apache.druid.hll.VersionOneHyperLogLogCollector", nullStr), SqlResourceTest.JSON_MAPPER.readValue(lines.get(1), List.class));
        Assert.assertEquals(Arrays.asList("2000-01-02T00:00:00.000Z", 1, "10.1", nullStr, "[\"b\",\"c\"]", 2.0, 2.0, "org.apache.druid.hll.VersionOneHyperLogLogCollector", nullStr), SqlResourceTest.JSON_MAPPER.readValue(lines.get(2), List.class));
        Assert.assertEquals("", lines.get(3));
        Assert.assertEquals("", lines.get(4));
    }

    @Test
    public void testObjectResultFormat() throws Exception {
        final String query = "SELECT *, CASE dim2 WHEN '' THEN dim2 END FROM foo  LIMIT 2";
        final String nullStr = (NullHandling.replaceWithDefault()) ? "" : null;
        final Function<Map<String, Object>, Map<String, Object>> transformer = ( m) -> {
            return Maps.transformEntries(m, ( k, v) -> ("EXPR$8".equals(k)) || (("dim2".equals(k)) && (v.toString().isEmpty())) ? nullStr : v);
        };
        Assert.assertEquals(ImmutableList.of(ImmutableMap.<String, Object>builder().put("__time", "2000-01-01T00:00:00.000Z").put("cnt", 1).put("dim1", "").put("dim2", "a").put("dim3", "[\"a\",\"b\"]").put("m1", 1.0).put("m2", 1.0).put("unique_dim1", "org.apache.druid.hll.VersionOneHyperLogLogCollector").put("EXPR$8", "").build(), ImmutableMap.<String, Object>builder().put("__time", "2000-01-02T00:00:00.000Z").put("cnt", 1).put("dim1", "10.1").put("dim2", "").put("dim3", "[\"b\",\"c\"]").put("m1", 2.0).put("m2", 2.0).put("unique_dim1", "org.apache.druid.hll.VersionOneHyperLogLogCollector").put("EXPR$8", "").build()).stream().map(transformer).collect(Collectors.toList()), doPost(new SqlQuery(query, ResultFormat.OBJECT, false, null), new TypeReference<List<Map<String, Object>>>() {}).rhs);
    }

    @Test
    public void testObjectLinesResultFormat() throws Exception {
        final String query = "SELECT *, CASE dim2 WHEN '' THEN dim2 END FROM foo LIMIT 2";
        final String response = doPostRaw(new SqlQuery(query, ResultFormat.OBJECTLINES, false, null)).rhs;
        final String nullStr = (NullHandling.replaceWithDefault()) ? "" : null;
        final Function<Map<String, Object>, Map<String, Object>> transformer = ( m) -> {
            return Maps.transformEntries(m, ( k, v) -> ("EXPR$8".equals(k)) || (("dim2".equals(k)) && (v.toString().isEmpty())) ? nullStr : v);
        };
        final List<String> lines = Splitter.on('\n').splitToList(response);
        Assert.assertEquals(4, lines.size());
        Assert.assertEquals(transformer.apply(ImmutableMap.<String, Object>builder().put("__time", "2000-01-01T00:00:00.000Z").put("cnt", 1).put("dim1", "").put("dim2", "a").put("dim3", "[\"a\",\"b\"]").put("m1", 1.0).put("m2", 1.0).put("unique_dim1", "org.apache.druid.hll.VersionOneHyperLogLogCollector").put("EXPR$8", "").build()), SqlResourceTest.JSON_MAPPER.readValue(lines.get(0), Object.class));
        Assert.assertEquals(transformer.apply(ImmutableMap.<String, Object>builder().put("__time", "2000-01-02T00:00:00.000Z").put("cnt", 1).put("dim1", "10.1").put("dim2", "").put("dim3", "[\"b\",\"c\"]").put("m1", 2.0).put("m2", 2.0).put("unique_dim1", "org.apache.druid.hll.VersionOneHyperLogLogCollector").put("EXPR$8", "").build()), SqlResourceTest.JSON_MAPPER.readValue(lines.get(1), Object.class));
        Assert.assertEquals("", lines.get(2));
        Assert.assertEquals("", lines.get(3));
    }

    @Test
    public void testCsvResultFormat() throws Exception {
        final String query = "SELECT *, CASE dim2 WHEN '' THEN dim2 END FROM foo LIMIT 2";
        final String response = doPostRaw(new SqlQuery(query, ResultFormat.CSV, false, null)).rhs;
        final List<String> lines = Splitter.on('\n').splitToList(response);
        Assert.assertEquals(ImmutableList.of("2000-01-01T00:00:00.000Z,1,,a,\"[\"\"a\"\",\"\"b\"\"]\",1.0,1.0,org.apache.druid.hll.VersionOneHyperLogLogCollector,", "2000-01-02T00:00:00.000Z,1,10.1,,\"[\"\"b\"\",\"\"c\"\"]\",2.0,2.0,org.apache.druid.hll.VersionOneHyperLogLogCollector,", "", ""), lines);
    }

    @Test
    public void testCsvResultFormatWithHeaders() throws Exception {
        final String query = "SELECT *, CASE dim2 WHEN '' THEN dim2 END FROM foo LIMIT 2";
        final String response = doPostRaw(new SqlQuery(query, ResultFormat.CSV, true, null)).rhs;
        final List<String> lines = Splitter.on('\n').splitToList(response);
        Assert.assertEquals(ImmutableList.of("__time,cnt,dim1,dim2,dim3,m1,m2,unique_dim1,EXPR$8", "2000-01-01T00:00:00.000Z,1,,a,\"[\"\"a\"\",\"\"b\"\"]\",1.0,1.0,org.apache.druid.hll.VersionOneHyperLogLogCollector,", "2000-01-02T00:00:00.000Z,1,10.1,,\"[\"\"b\"\",\"\"c\"\"]\",2.0,2.0,org.apache.druid.hll.VersionOneHyperLogLogCollector,", "", ""), lines);
    }

    @Test
    public void testExplainCountStar() throws Exception {
        Map<String, Object> queryContext = ImmutableMap.of(CTX_SQL_QUERY_ID, SqlResourceTest.DUMMY_SQL_QUERY_ID);
        final List<Map<String, Object>> rows = doPost(new SqlQuery("EXPLAIN PLAN FOR SELECT COUNT(*) AS cnt FROM druid.foo", ResultFormat.OBJECT, false, queryContext)).rhs;
        Assert.assertEquals(ImmutableList.of(ImmutableMap.<String, Object>of("PLAN", StringUtils.format("DruidQueryRel(query=[{\"queryType\":\"timeseries\",\"dataSource\":{\"type\":\"table\",\"name\":\"foo\"},\"intervals\":{\"type\":\"intervals\",\"intervals\":[\"-146136543-09-08T08:23:32.096Z/146140482-04-24T15:36:27.903Z\"]},\"descending\":false,\"virtualColumns\":[],\"filter\":null,\"granularity\":{\"type\":\"all\"},\"aggregations\":[{\"type\":\"count\",\"name\":\"a0\"}],\"postAggregations\":[],\"limit\":2147483647,\"context\":{\"skipEmptyBuckets\":true,\"sqlQueryId\":\"%s\"}}], signature=[{a0:LONG}])\n", SqlResourceTest.DUMMY_SQL_QUERY_ID))), rows);
    }

    @Test
    public void testCannotValidate() throws Exception {
        final QueryInterruptedException exception = doPost(new SqlQuery("SELECT dim4 FROM druid.foo", ResultFormat.OBJECT, false, null)).lhs;
        Assert.assertNotNull(exception);
        Assert.assertEquals(UNKNOWN_EXCEPTION, exception.getErrorCode());
        Assert.assertEquals(ValidationException.class.getName(), exception.getErrorClass());
        Assert.assertTrue(exception.getMessage().contains("Column 'dim4' not found in any table"));
        checkSqlRequestLog(false);
    }

    @Test
    public void testCannotConvert() throws Exception {
        // SELECT + ORDER unsupported
        final QueryInterruptedException exception = doPost(new SqlQuery("SELECT dim1 FROM druid.foo ORDER BY dim1", ResultFormat.OBJECT, false, null)).lhs;
        Assert.assertNotNull(exception);
        Assert.assertEquals(UNKNOWN_EXCEPTION, exception.getErrorCode());
        Assert.assertEquals(ISE.class.getName(), exception.getErrorClass());
        Assert.assertTrue(exception.getMessage().contains("Cannot build plan for query: SELECT dim1 FROM druid.foo ORDER BY dim1"));
        checkSqlRequestLog(false);
    }

    @Test
    public void testResourceLimitExceeded() throws Exception {
        final QueryInterruptedException exception = doPost(new SqlQuery("SELECT DISTINCT dim1 FROM foo", ResultFormat.OBJECT, false, ImmutableMap.of("maxMergingDictionarySize", 1))).lhs;
        Assert.assertNotNull(exception);
        Assert.assertEquals(exception.getErrorCode(), RESOURCE_LIMIT_EXCEEDED);
        Assert.assertEquals(exception.getErrorClass(), ResourceLimitExceededException.class.getName());
        checkSqlRequestLog(false);
    }
}

