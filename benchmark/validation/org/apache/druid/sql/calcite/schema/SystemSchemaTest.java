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
package org.apache.druid.sql.calcite.schema;


import DataSegment.PruneLoadSpecHolder;
import HttpMethod.GET;
import SqlTypeName.VARCHAR;
import SystemSchema.SEGMENTS_SIGNATURE;
import SystemSchema.SERVERS_SIGNATURE;
import SystemSchema.SERVER_SEGMENTS_SIGNATURE;
import SystemSchema.SegmentsTable;
import SystemSchema.ServerSegmentsTable;
import SystemSchema.ServersTable;
import SystemSchema.TASKS_SIGNATURE;
import SystemSchema.TasksTable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.SettableFuture;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletResponse;
import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.schema.Table;
import org.apache.druid.client.DruidServer;
import org.apache.druid.client.ImmutableDruidServer;
import org.apache.druid.client.TimelineServerView;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.discovery.DruidLeaderClient;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.java.util.http.client.Request;
import org.apache.druid.java.util.http.client.io.AppendableByteArrayInputStream;
import org.apache.druid.java.util.http.client.response.FullResponseHolder;
import org.apache.druid.query.QueryRunnerFactoryConglomerate;
import org.apache.druid.server.coordination.ServerType;
import org.apache.druid.server.coordinator.BytesAccumulatingResponseHandler;
import org.apache.druid.server.security.AuthorizerMapper;
import org.apache.druid.sql.calcite.planner.PlannerConfig;
import org.apache.druid.sql.calcite.util.CalciteTestBase;
import org.apache.druid.sql.calcite.util.CalciteTests;
import org.apache.druid.sql.calcite.util.SpecificSegmentsQuerySegmentWalker;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.NumberedShardSpec;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class SystemSchemaTest extends CalciteTestBase {
    private static final PlannerConfig PLANNER_CONFIG_DEFAULT = new PlannerConfig();

    private static final List<InputRow> ROWS1 = ImmutableList.of(CalciteTests.createRow(ImmutableMap.of("t", "2000-01-01", "m1", "1.0", "dim1", "")), CalciteTests.createRow(ImmutableMap.of("t", "2000-01-02", "m1", "2.0", "dim1", "10.1")), CalciteTests.createRow(ImmutableMap.of("t", "2000-01-03", "m1", "3.0", "dim1", "2")));

    private static final List<InputRow> ROWS2 = ImmutableList.of(CalciteTests.createRow(ImmutableMap.of("t", "2001-01-01", "m1", "4.0", "dim2", ImmutableList.of("a"))), CalciteTests.createRow(ImmutableMap.of("t", "2001-01-02", "m1", "5.0", "dim2", ImmutableList.of("abc"))), CalciteTests.createRow(ImmutableMap.of("t", "2001-01-03", "m1", "6.0")));

    private static final List<InputRow> ROWS3 = ImmutableList.of(CalciteTests.createRow(ImmutableMap.of("t", "2001-01-01", "m1", "7.0", "dim3", ImmutableList.of("x"))), CalciteTests.createRow(ImmutableMap.of("t", "2001-01-02", "m1", "8.0", "dim3", ImmutableList.of("xyz"))));

    private SystemSchema schema;

    private SpecificSegmentsQuerySegmentWalker walker;

    private DruidLeaderClient client;

    private TimelineServerView serverView;

    private ObjectMapper mapper;

    private FullResponseHolder responseHolder;

    private BytesAccumulatingResponseHandler responseHandler;

    private Request request;

    private DruidSchema druidSchema;

    private AuthorizerMapper authMapper;

    private static QueryRunnerFactoryConglomerate conglomerate;

    private static Closer resourceCloser;

    private MetadataSegmentView metadataView;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final DataSegment publishedSegment1 = new DataSegment("wikipedia1", Intervals.of("2007/2008"), "version1", null, ImmutableList.of("dim1", "dim2"), ImmutableList.of("met1", "met2"), null, 1, 53000L, PruneLoadSpecHolder.DEFAULT);

    private final DataSegment publishedSegment2 = new DataSegment("wikipedia2", Intervals.of("2008/2009"), "version2", null, ImmutableList.of("dim1", "dim2"), ImmutableList.of("met1", "met2"), null, 1, 83000L, PruneLoadSpecHolder.DEFAULT);

    private final DataSegment publishedSegment3 = new DataSegment("wikipedia3", Intervals.of("2009/2010"), "version3", null, ImmutableList.of("dim1", "dim2"), ImmutableList.of("met1", "met2"), null, 1, 47000L, PruneLoadSpecHolder.DEFAULT);

    private final DataSegment segment1 = new DataSegment("test1", Intervals.of("2010/2011"), "version1", null, ImmutableList.of("dim1", "dim2"), ImmutableList.of("met1", "met2"), null, 1, 100L, PruneLoadSpecHolder.DEFAULT);

    private final DataSegment segment2 = new DataSegment("test2", Intervals.of("2011/2012"), "version2", null, ImmutableList.of("dim1", "dim2"), ImmutableList.of("met1", "met2"), null, 1, 100L, PruneLoadSpecHolder.DEFAULT);

    private final DataSegment segment3 = new DataSegment("test3", Intervals.of("2012/2013"), "version3", null, ImmutableList.of("dim1", "dim2"), ImmutableList.of("met1", "met2"), new NumberedShardSpec(2, 3), 1, 100L, PruneLoadSpecHolder.DEFAULT);

    private final DataSegment segment4 = new DataSegment("test4", Intervals.of("2014/2015"), "version4", null, ImmutableList.of("dim1", "dim2"), ImmutableList.of("met1", "met2"), null, 1, 100L, PruneLoadSpecHolder.DEFAULT);

    private final DataSegment segment5 = new DataSegment("test5", Intervals.of("2015/2016"), "version5", null, ImmutableList.of("dim1", "dim2"), ImmutableList.of("met1", "met2"), null, 1, 100L, PruneLoadSpecHolder.DEFAULT);

    final List<DataSegment> realtimeSegments = ImmutableList.of(segment2, segment4, segment5);

    private final ImmutableDruidServer druidServer1 = new ImmutableDruidServer(new org.apache.druid.server.coordination.DruidServerMetadata("server1", "localhost:0000", null, 5L, ServerType.REALTIME, DruidServer.DEFAULT_TIER, 0), 1L, ImmutableMap.of("dummy", new org.apache.druid.client.ImmutableDruidDataSource("dummy", Collections.emptyMap(), Arrays.asList(segment1, segment2))), 2);

    private final ImmutableDruidServer druidServer2 = new ImmutableDruidServer(new org.apache.druid.server.coordination.DruidServerMetadata("server2", "server2:1234", null, 5L, ServerType.HISTORICAL, DruidServer.DEFAULT_TIER, 0), 1L, ImmutableMap.of("dummy", new org.apache.druid.client.ImmutableDruidDataSource("dummy", Collections.emptyMap(), Arrays.asList(segment3, segment4, segment5))), 3);

    private final List<ImmutableDruidServer> immutableDruidServers = ImmutableList.of(druidServer1, druidServer2);

    @Test
    public void testGetTableMap() {
        Assert.assertEquals(ImmutableSet.of("segments", "servers", "server_segments", "tasks"), schema.getTableNames());
        final Map<String, Table> tableMap = schema.getTableMap();
        Assert.assertEquals(ImmutableSet.of("segments", "servers", "server_segments", "tasks"), tableMap.keySet());
        final SystemSchema.SegmentsTable segmentsTable = ((SystemSchema.SegmentsTable) (schema.getTableMap().get("segments")));
        final RelDataType rowType = segmentsTable.getRowType(new JavaTypeFactoryImpl());
        final List<RelDataTypeField> fields = rowType.getFieldList();
        Assert.assertEquals(13, fields.size());
        final SystemSchema.TasksTable tasksTable = ((SystemSchema.TasksTable) (schema.getTableMap().get("tasks")));
        final RelDataType sysRowType = tasksTable.getRowType(new JavaTypeFactoryImpl());
        final List<RelDataTypeField> sysFields = sysRowType.getFieldList();
        Assert.assertEquals(13, sysFields.size());
        Assert.assertEquals("task_id", sysFields.get(0).getName());
        Assert.assertEquals(VARCHAR, sysFields.get(0).getType().getSqlTypeName());
        final SystemSchema.ServersTable serversTable = ((SystemSchema.ServersTable) (schema.getTableMap().get("servers")));
        final RelDataType serverRowType = serversTable.getRowType(new JavaTypeFactoryImpl());
        final List<RelDataTypeField> serverFields = serverRowType.getFieldList();
        Assert.assertEquals(8, serverFields.size());
        Assert.assertEquals("server", serverFields.get(0).getName());
        Assert.assertEquals(VARCHAR, serverFields.get(0).getType().getSqlTypeName());
    }

    @Test
    public void testSegmentsTable() {
        final SystemSchema.SegmentsTable segmentsTable = EasyMock.createMockBuilder(SegmentsTable.class).withConstructor(druidSchema, metadataView, mapper, authMapper).createMock();
        EasyMock.replay(segmentsTable);
        final Set<DataSegment> publishedSegments = Stream.of(publishedSegment1, publishedSegment2, publishedSegment3, segment1, segment2).collect(Collectors.toSet());
        EasyMock.expect(metadataView.getPublishedSegments()).andReturn(publishedSegments.iterator()).once();
        EasyMock.replay(client, request, responseHolder, responseHandler, metadataView);
        DataContext dataContext = new DataContext() {
            @Override
            public SchemaPlus getRootSchema() {
                return null;
            }

            @Override
            public JavaTypeFactory getTypeFactory() {
                return null;
            }

            @Override
            public QueryProvider getQueryProvider() {
                return null;
            }

            @Override
            public Object get(String name) {
                return CalciteTests.SUPER_USER_AUTH_RESULT;
            }
        };
        final List<Object[]> rows = segmentsTable.scan(dataContext).toList();
        rows.sort((Object[] row1,Object[] row2) -> ((Comparable) (row1[0])).compareTo(row2[0]));
        // total segments = 8
        // segments test1, test2  are published and available
        // segment test3 is served by historical but unpublished or unused
        // segments test4, test5 are not published but available (realtime segments)
        // segment test2 is both published and served by a realtime server.
        Assert.assertEquals(8, rows.size());
        // partition_num
        // num_replicas
        // numRows
        // is_published
        // is_available
        // is_realtime
        verifyRow(rows.get(0), "test1_2010-01-01T00:00:00.000Z_2011-01-01T00:00:00.000Z_version1", 100L, 0L, 1L, 3L, 1L, 1L, 0L);
        // partition_num
        // x?segment test2 is served by historical and realtime servers
        // numRows
        // is_published
        // is_available
        // is_realtime
        verifyRow(rows.get(1), "test2_2011-01-01T00:00:00.000Z_2012-01-01T00:00:00.000Z_version2", 100L, 0L, 2L, 3L, 1L, 1L, 0L);
        // segment test3 is unpublished and has a NumberedShardSpec with partitionNum = 2
        // partition_num
        // num_replicas
        // numRows
        // is_published
        // is_available
        // is_realtime
        verifyRow(rows.get(2), "test3_2012-01-01T00:00:00.000Z_2013-01-01T00:00:00.000Z_version3_2", 100L, 2L, 1L, 2L, 0L, 1L, 0L);
        // partition_num
        // num_replicas
        // numRows
        // is_published
        // is_available
        // is_realtime
        verifyRow(rows.get(3), "test4_2014-01-01T00:00:00.000Z_2015-01-01T00:00:00.000Z_version4", 100L, 0L, 1L, 0L, 0L, 1L, 1L);
        // partition_num
        // num_replicas
        // numRows
        // is_published
        // is_available
        // is_realtime
        verifyRow(rows.get(4), "test5_2015-01-01T00:00:00.000Z_2016-01-01T00:00:00.000Z_version5", 100L, 0L, 1L, 0L, 0L, 1L, 1L);
        // wikipedia segments are published and unavailable, num_replicas is 0
        // partition_num
        // num_replicas
        // numRows
        // is_published
        // is_available
        // is_realtime
        verifyRow(rows.get(5), "wikipedia1_2007-01-01T00:00:00.000Z_2008-01-01T00:00:00.000Z_version1", 53000L, 0L, 0L, 0L, 1L, 0L, 0L);
        // partition_num
        // num_replicas
        // numRows
        // is_published
        // is_available
        // is_realtime
        verifyRow(rows.get(6), "wikipedia2_2008-01-01T00:00:00.000Z_2009-01-01T00:00:00.000Z_version2", 83000L, 0L, 0L, 0L, 1L, 0L, 0L);
        // partition_num
        // num_replicas
        // numRows
        // is_published
        // is_available
        // is_realtime
        verifyRow(rows.get(7), "wikipedia3_2009-01-01T00:00:00.000Z_2010-01-01T00:00:00.000Z_version3", 47000L, 0L, 0L, 0L, 1L, 0L, 0L);
        // Verify value types.
        SystemSchemaTest.verifyTypes(rows, SEGMENTS_SIGNATURE);
    }

    @Test
    public void testServersTable() {
        SystemSchema.ServersTable serversTable = EasyMock.createMockBuilder(ServersTable.class).withConstructor(serverView, authMapper).createMock();
        EasyMock.replay(serversTable);
        EasyMock.expect(serverView.getDruidServers()).andReturn(immutableDruidServers).once();
        EasyMock.replay(serverView);
        DataContext dataContext = new DataContext() {
            @Override
            public SchemaPlus getRootSchema() {
                return null;
            }

            @Override
            public JavaTypeFactory getTypeFactory() {
                return null;
            }

            @Override
            public QueryProvider getQueryProvider() {
                return null;
            }

            @Override
            public Object get(String name) {
                return CalciteTests.SUPER_USER_AUTH_RESULT;
            }
        };
        final List<Object[]> rows = serversTable.scan(dataContext).toList();
        Assert.assertEquals(2, rows.size());
        Object[] row1 = rows.get(0);
        Assert.assertEquals("localhost:0000", row1[0]);
        Assert.assertEquals("realtime", row1[4].toString());
        Object[] row2 = rows.get(1);
        Assert.assertEquals("server2:1234", row2[0]);
        Assert.assertEquals("historical", row2[4].toString());
        // Verify value types.
        SystemSchemaTest.verifyTypes(rows, SERVERS_SIGNATURE);
    }

    @Test
    public void testServerSegmentsTable() {
        SystemSchema.ServerSegmentsTable serverSegmentsTable = EasyMock.createMockBuilder(ServerSegmentsTable.class).withConstructor(serverView, authMapper).createMock();
        EasyMock.replay(serverSegmentsTable);
        EasyMock.expect(serverView.getDruidServers()).andReturn(immutableDruidServers).once();
        EasyMock.replay(serverView);
        DataContext dataContext = new DataContext() {
            @Override
            public SchemaPlus getRootSchema() {
                return null;
            }

            @Override
            public JavaTypeFactory getTypeFactory() {
                return null;
            }

            @Override
            public QueryProvider getQueryProvider() {
                return null;
            }

            @Override
            public Object get(String name) {
                return CalciteTests.SUPER_USER_AUTH_RESULT;
            }
        };
        // server_segments table is the join of servers and segments table
        // it will have 5 rows as follows
        // localhost:0000 |  test1_2010-01-01T00:00:00.000Z_2011-01-01T00:00:00.000Z_version1(segment1)
        // localhost:0000 |  test2_2011-01-01T00:00:00.000Z_2012-01-01T00:00:00.000Z_version2(segment2)
        // server2:1234   |  test3_2012-01-01T00:00:00.000Z_2013-01-01T00:00:00.000Z_version3(segment3)
        // server2:1234   |  test4_2017-01-01T00:00:00.000Z_2018-01-01T00:00:00.000Z_version4(segment4)
        // server2:1234   |  test5_2017-01-01T00:00:00.000Z_2018-01-01T00:00:00.000Z_version5(segment5)
        final List<Object[]> rows = serverSegmentsTable.scan(dataContext).toList();
        Assert.assertEquals(5, rows.size());
        Object[] row0 = rows.get(0);
        Assert.assertEquals("localhost:0000", row0[0]);
        Assert.assertEquals("test1_2010-01-01T00:00:00.000Z_2011-01-01T00:00:00.000Z_version1", row0[1].toString());
        Object[] row1 = rows.get(1);
        Assert.assertEquals("localhost:0000", row1[0]);
        Assert.assertEquals("test2_2011-01-01T00:00:00.000Z_2012-01-01T00:00:00.000Z_version2", row1[1].toString());
        Object[] row2 = rows.get(2);
        Assert.assertEquals("server2:1234", row2[0]);
        Assert.assertEquals("test3_2012-01-01T00:00:00.000Z_2013-01-01T00:00:00.000Z_version3_2", row2[1].toString());
        Object[] row3 = rows.get(3);
        Assert.assertEquals("server2:1234", row3[0]);
        Assert.assertEquals("test4_2014-01-01T00:00:00.000Z_2015-01-01T00:00:00.000Z_version4", row3[1].toString());
        Object[] row4 = rows.get(4);
        Assert.assertEquals("server2:1234", row4[0]);
        Assert.assertEquals("test5_2015-01-01T00:00:00.000Z_2016-01-01T00:00:00.000Z_version5", row4[1].toString());
        // Verify value types.
        SystemSchemaTest.verifyTypes(rows, SERVER_SEGMENTS_SIGNATURE);
    }

    @Test
    public void testTasksTable() throws Exception {
        SystemSchema.TasksTable tasksTable = EasyMock.createMockBuilder(TasksTable.class).withConstructor(client, mapper, responseHandler, authMapper).createMock();
        EasyMock.replay(tasksTable);
        EasyMock.expect(client.makeRequest(GET, "/druid/indexer/v1/tasks", false)).andReturn(request).anyTimes();
        SettableFuture<InputStream> future = SettableFuture.create();
        EasyMock.expect(client.goAsync(request, responseHandler)).andReturn(future).once();
        final int ok = HttpServletResponse.SC_OK;
        EasyMock.expect(responseHandler.getStatus()).andReturn(ok).anyTimes();
        EasyMock.expect(request.getUrl()).andReturn(new URL("http://test-host:1234/druid/indexer/v1/tasks")).anyTimes();
        AppendableByteArrayInputStream in = new AppendableByteArrayInputStream();
        String json = "[{\n" + ((((((((((((((((((((((((((((("\t\"id\": \"index_wikipedia_2018-09-20T22:33:44.911Z\",\n" + "\t\"type\": \"index\",\n") + "\t\"createdTime\": \"2018-09-20T22:33:44.922Z\",\n") + "\t\"queueInsertionTime\": \"1970-01-01T00:00:00.000Z\",\n") + "\t\"statusCode\": \"FAILED\",\n") + "\t\"runnerStatusCode\": \"NONE\",\n") + "\t\"duration\": -1,\n") + "\t\"location\": {\n") + "\t\t\"host\": \"testHost\",\n") + "\t\t\"port\": 1234,\n") + "\t\t\"tlsPort\": -1\n") + "\t},\n") + "\t\"dataSource\": \"wikipedia\",\n") + "\t\"errorMsg\": null\n") + "}, {\n") + "\t\"id\": \"index_wikipedia_2018-09-21T18:38:47.773Z\",\n") + "\t\"type\": \"index\",\n") + "\t\"createdTime\": \"2018-09-21T18:38:47.873Z\",\n") + "\t\"queueInsertionTime\": \"2018-09-21T18:38:47.910Z\",\n") + "\t\"statusCode\": \"RUNNING\",\n") + "\t\"runnerStatusCode\": \"RUNNING\",\n") + "\t\"duration\": null,\n") + "\t\"location\": {\n") + "\t\t\"host\": \"192.168.1.6\",\n") + "\t\t\"port\": 8100,\n") + "\t\t\"tlsPort\": -1\n") + "\t},\n") + "\t\"dataSource\": \"wikipedia\",\n") + "\t\"errorMsg\": null\n") + "}]");
        byte[] bytesToWrite = json.getBytes(StandardCharsets.UTF_8);
        in.add(bytesToWrite);
        in.done();
        future.set(in);
        EasyMock.replay(client, request, responseHandler);
        DataContext dataContext = new DataContext() {
            @Override
            public SchemaPlus getRootSchema() {
                return null;
            }

            @Override
            public JavaTypeFactory getTypeFactory() {
                return null;
            }

            @Override
            public QueryProvider getQueryProvider() {
                return null;
            }

            @Override
            public Object get(String name) {
                return CalciteTests.SUPER_USER_AUTH_RESULT;
            }
        };
        final List<Object[]> rows = tasksTable.scan(dataContext).toList();
        Object[] row0 = rows.get(0);
        Assert.assertEquals("index_wikipedia_2018-09-20T22:33:44.911Z", row0[0].toString());
        Assert.assertEquals("FAILED", row0[5].toString());
        Assert.assertEquals("NONE", row0[6].toString());
        Assert.assertEquals((-1L), row0[7]);
        Assert.assertEquals("testHost:1234", row0[8]);
        Object[] row1 = rows.get(1);
        Assert.assertEquals("index_wikipedia_2018-09-21T18:38:47.773Z", row1[0].toString());
        Assert.assertEquals("RUNNING", row1[5].toString());
        Assert.assertEquals("RUNNING", row1[6].toString());
        Assert.assertEquals(0L, row1[7]);
        Assert.assertEquals("192.168.1.6:8100", row1[8]);
        // Verify value types.
        SystemSchemaTest.verifyTypes(rows, TASKS_SIGNATURE);
    }
}

