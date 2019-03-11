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


import SqlTypeName.BIGINT;
import SqlTypeName.OTHER;
import SqlTypeName.TIMESTAMP;
import SqlTypeName.VARCHAR;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.schema.Table;
import org.apache.druid.client.ImmutableDruidServer;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.QueryRunnerFactoryConglomerate;
import org.apache.druid.server.coordination.DruidServerMetadata;
import org.apache.druid.sql.calcite.planner.PlannerConfig;
import org.apache.druid.sql.calcite.table.DruidTable;
import org.apache.druid.sql.calcite.util.CalciteTestBase;
import org.apache.druid.sql.calcite.util.CalciteTests;
import org.apache.druid.sql.calcite.util.SpecificSegmentsQuerySegmentWalker;
import org.apache.druid.timeline.DataSegment;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class DruidSchemaTest extends CalciteTestBase {
    private static final PlannerConfig PLANNER_CONFIG_DEFAULT = new PlannerConfig();

    private static final List<InputRow> ROWS1 = ImmutableList.of(CalciteTests.createRow(ImmutableMap.of("t", "2000-01-01", "m1", "1.0", "dim1", "")), CalciteTests.createRow(ImmutableMap.of("t", "2000-01-02", "m1", "2.0", "dim1", "10.1")), CalciteTests.createRow(ImmutableMap.of("t", "2000-01-03", "m1", "3.0", "dim1", "2")));

    private static final List<InputRow> ROWS2 = ImmutableList.of(CalciteTests.createRow(ImmutableMap.of("t", "2001-01-01", "m1", "4.0", "dim2", ImmutableList.of("a"))), CalciteTests.createRow(ImmutableMap.of("t", "2001-01-02", "m1", "5.0", "dim2", ImmutableList.of("abc"))), CalciteTests.createRow(ImmutableMap.of("t", "2001-01-03", "m1", "6.0")));

    private static QueryRunnerFactoryConglomerate conglomerate;

    private static Closer resourceCloser;

    private List<ImmutableDruidServer> druidServers;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private SpecificSegmentsQuerySegmentWalker walker = null;

    private DruidSchema schema = null;

    @Test
    public void testGetTableMap() {
        Assert.assertEquals(ImmutableSet.of("foo", "foo2"), schema.getTableNames());
        final Map<String, Table> tableMap = schema.getTableMap();
        Assert.assertEquals(ImmutableSet.of("foo", "foo2"), tableMap.keySet());
    }

    @Test
    public void testGetTableMapFoo() {
        final DruidTable fooTable = ((DruidTable) (schema.getTableMap().get("foo")));
        final RelDataType rowType = fooTable.getRowType(new JavaTypeFactoryImpl());
        final List<RelDataTypeField> fields = rowType.getFieldList();
        Assert.assertEquals(6, fields.size());
        Assert.assertEquals("__time", fields.get(0).getName());
        Assert.assertEquals(TIMESTAMP, fields.get(0).getType().getSqlTypeName());
        Assert.assertEquals("cnt", fields.get(1).getName());
        Assert.assertEquals(BIGINT, fields.get(1).getType().getSqlTypeName());
        Assert.assertEquals("dim1", fields.get(2).getName());
        Assert.assertEquals(VARCHAR, fields.get(2).getType().getSqlTypeName());
        Assert.assertEquals("dim2", fields.get(3).getName());
        Assert.assertEquals(VARCHAR, fields.get(3).getType().getSqlTypeName());
        Assert.assertEquals("m1", fields.get(4).getName());
        Assert.assertEquals(BIGINT, fields.get(4).getType().getSqlTypeName());
        Assert.assertEquals("unique_dim1", fields.get(5).getName());
        Assert.assertEquals(OTHER, fields.get(5).getType().getSqlTypeName());
    }

    @Test
    public void testGetTableMapFoo2() {
        final DruidTable fooTable = ((DruidTable) (schema.getTableMap().get("foo2")));
        final RelDataType rowType = fooTable.getRowType(new JavaTypeFactoryImpl());
        final List<RelDataTypeField> fields = rowType.getFieldList();
        Assert.assertEquals(3, fields.size());
        Assert.assertEquals("__time", fields.get(0).getName());
        Assert.assertEquals(TIMESTAMP, fields.get(0).getType().getSqlTypeName());
        Assert.assertEquals("dim2", fields.get(1).getName());
        Assert.assertEquals(VARCHAR, fields.get(1).getType().getSqlTypeName());
        Assert.assertEquals("m1", fields.get(2).getName());
        Assert.assertEquals(BIGINT, fields.get(2).getType().getSqlTypeName());
    }

    /**
     * This tests that {@link SegmentMetadataHolder#getNumRows()} is correct in case
     * of multiple replicas i.e. when {@link DruidSchema#addSegment(DruidServerMetadata, DataSegment)}
     * is called more than once for same segment
     */
    @Test
    public void testSegmentMetadataHolderNumRows() {
        Map<DataSegment, SegmentMetadataHolder> segmentsMetadata = schema.getSegmentMetadata();
        final Set<DataSegment> segments = segmentsMetadata.keySet();
        Assert.assertEquals(3, segments.size());
        // find the only segment with datasource "foo2"
        final DataSegment existingSegment = segments.stream().filter(( segment) -> segment.getDataSource().equals("foo2")).findFirst().orElse(null);
        Assert.assertNotNull(existingSegment);
        final SegmentMetadataHolder existingHolder = segmentsMetadata.get(existingSegment);
        // update SegmentMetadataHolder of existingSegment with numRows=5
        SegmentMetadataHolder updatedHolder = SegmentMetadataHolder.from(existingHolder).withNumRows(5).build();
        schema.setSegmentMetadataHolder(existingSegment, updatedHolder);
        // find a druidServer holding existingSegment
        final Pair<ImmutableDruidServer, DataSegment> pair = druidServers.stream().flatMap(( druidServer) -> druidServer.getSegments().stream().filter(( segment) -> segment.equals(existingSegment)).map(( segment) -> Pair.of(druidServer, segment))).findAny().orElse(null);
        Assert.assertNotNull(pair);
        final ImmutableDruidServer server = pair.lhs;
        Assert.assertNotNull(server);
        final DruidServerMetadata druidServerMetadata = server.getMetadata();
        // invoke DruidSchema#addSegment on existingSegment
        schema.addSegment(druidServerMetadata, existingSegment);
        segmentsMetadata = schema.getSegmentMetadata();
        // get the only segment with datasource "foo2"
        final DataSegment currentSegment = segments.stream().filter(( segment) -> segment.getDataSource().equals("foo2")).findFirst().orElse(null);
        final SegmentMetadataHolder currentHolder = segmentsMetadata.get(currentSegment);
        Assert.assertEquals(updatedHolder.getSegmentId(), currentHolder.getSegmentId());
        Assert.assertEquals(updatedHolder.getNumRows(), currentHolder.getNumRows());
        // numreplicas do not change here since we addSegment with the same server which was serving existingSegment before
        Assert.assertEquals(updatedHolder.getNumReplicas(), currentHolder.getNumReplicas());
        Assert.assertEquals(updatedHolder.isAvailable(), currentHolder.isAvailable());
        Assert.assertEquals(updatedHolder.isPublished(), currentHolder.isPublished());
    }

    @Test
    public void testNullDatasource() throws IOException {
        Map<DataSegment, SegmentMetadataHolder> segmentMetadatas = schema.getSegmentMetadata();
        Set<DataSegment> segments = segmentMetadatas.keySet();
        Assert.assertEquals(segments.size(), 3);
        // segments contains two segments with datasource "foo" and one with datasource "foo2"
        // let's remove the only segment with datasource "foo2"
        final DataSegment segmentToRemove = segments.stream().filter(( segment) -> segment.getDataSource().equals("foo2")).findFirst().orElse(null);
        Assert.assertFalse((segmentToRemove == null));
        schema.removeSegment(segmentToRemove);
        schema.refreshSegments(segments);// can cause NPE without dataSourceSegments null check in DruidSchema#refreshSegmentsForDataSource

        segmentMetadatas = schema.getSegmentMetadata();
        segments = segmentMetadatas.keySet();
        Assert.assertEquals(segments.size(), 2);
    }

    @Test
    public void testNullSegmentMetadataHolder() throws IOException {
        Map<DataSegment, SegmentMetadataHolder> segmentMetadatas = schema.getSegmentMetadata();
        Set<DataSegment> segments = segmentMetadatas.keySet();
        Assert.assertEquals(segments.size(), 3);
        // remove one of the segments with datasource "foo"
        final DataSegment segmentToRemove = segments.stream().filter(( segment) -> segment.getDataSource().equals("foo")).findFirst().orElse(null);
        Assert.assertFalse((segmentToRemove == null));
        schema.removeSegment(segmentToRemove);
        schema.refreshSegments(segments);// can cause NPE without holder null check in SegmentMetadataHolder#from

        segmentMetadatas = schema.getSegmentMetadata();
        segments = segmentMetadatas.keySet();
        Assert.assertEquals(segments.size(), 2);
    }
}

