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
package org.apache.druid.indexing.common.task;


import Granularities.ALL;
import RowIngestionMeters.BUILD_SEGMENTS;
import RowIngestionMeters.DETERMINE_PARTITIONS;
import RowIngestionMeters.PROCESSED;
import RowIngestionMeters.PROCESSED_WITH_ERROR;
import RowIngestionMeters.THROWN_AWAY;
import RowIngestionMeters.UNPARSEABLE;
import TaskState.FAILED;
import TaskState.SUCCESS;
import VirtualColumns.EMPTY;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.FloatDimensionSchema;
import org.apache.druid.data.input.impl.LongDimensionSchema;
import org.apache.druid.data.input.impl.ParseSpec;
import org.apache.druid.data.input.impl.StringDimensionSchema;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.indexer.TaskStatus;
import org.apache.druid.indexing.common.IngestionStatsAndErrorsTaskReportData;
import org.apache.druid.indexing.common.TestUtils;
import org.apache.druid.indexing.common.stats.RowIngestionMetersFactory;
import org.apache.druid.indexing.common.task.IndexTask.IndexIngestionSpec;
import org.apache.druid.indexing.common.task.IndexTask.IndexTuningConfig;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.math.expr.ExprMacroTable;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.segment.Cursor;
import org.apache.druid.segment.DimensionSelector;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.IndexMergerV9;
import org.apache.druid.segment.IndexSpec;
import org.apache.druid.segment.loading.DataSegmentPusher;
import org.apache.druid.segment.loading.SegmentLoader;
import org.apache.druid.segment.realtime.firehose.WindowedStorageAdapter;
import org.apache.druid.server.security.AuthTestUtils;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.HashBasedNumberedShardSpec;
import org.apache.druid.timeline.partition.NoneShardSpec;
import org.apache.druid.timeline.partition.NumberedShardSpec;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class IndexTaskTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final ParseSpec DEFAULT_PARSE_SPEC = new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("ts", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList("ts", "dim")), new ArrayList(), new ArrayList()), null, Arrays.asList("ts", "dim", "val"), false, 0);

    private DataSegmentPusher pusher;

    private SegmentLoader segmentLoader;

    private List<DataSegment> segments;

    private static final IndexSpec indexSpec = new IndexSpec();

    private final ObjectMapper jsonMapper;

    private IndexMergerV9 indexMergerV9;

    private IndexIO indexIO;

    private volatile int segmentAllocatePartitionCounter;

    private File reportsFile;

    private RowIngestionMetersFactory rowIngestionMetersFactory;

    public IndexTaskTest() {
        TestUtils testUtils = new TestUtils();
        jsonMapper = testUtils.getTestObjectMapper();
        indexMergerV9 = testUtils.getTestIndexMergerV9();
        indexIO = testUtils.getTestIndexIO();
        rowIngestionMetersFactory = testUtils.getRowIngestionMetersFactory();
    }

    @Test
    public void testDeterminePartitions() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("2014-01-01T00:00:10Z,a,1\n");
            writer.write("2014-01-01T01:00:20Z,b,1\n");
            writer.write("2014-01-01T02:00:30Z,c,1\n");
        }
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, null, IndexTaskTest.createTuningConfigWithMaxRowsPerSegment(2, false, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(2, segments.size());
        Assert.assertEquals("test", segments.get(0).getDataSource());
        Assert.assertEquals(Intervals.of("2014/P1D"), segments.get(0).getInterval());
        Assert.assertEquals(HashBasedNumberedShardSpec.class, segments.get(0).getShardSpec().getClass());
        Assert.assertEquals(0, segments.get(0).getShardSpec().getPartitionNum());
        Assert.assertEquals(2, getPartitions());
        Assert.assertEquals("test", segments.get(1).getDataSource());
        Assert.assertEquals(Intervals.of("2014/P1D"), segments.get(1).getInterval());
        Assert.assertEquals(HashBasedNumberedShardSpec.class, segments.get(1).getShardSpec().getClass());
        Assert.assertEquals(1, segments.get(1).getShardSpec().getPartitionNum());
        Assert.assertEquals(2, getPartitions());
    }

    @Test
    public void testForceExtendableShardSpecs() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("2014-01-01T00:00:10Z,a,1\n");
            writer.write("2014-01-01T01:00:20Z,b,1\n");
            writer.write("2014-01-01T02:00:30Z,c,1\n");
        }
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, null, IndexTaskTest.createTuningConfigWithMaxRowsPerSegment(2, true, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        Assert.assertEquals(indexTask.getId(), indexTask.getGroupId());
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(2, segments.size());
        Assert.assertEquals("test", segments.get(0).getDataSource());
        Assert.assertEquals(Intervals.of("2014/P1D"), segments.get(0).getInterval());
        Assert.assertEquals(NumberedShardSpec.class, segments.get(0).getShardSpec().getClass());
        Assert.assertEquals(0, segments.get(0).getShardSpec().getPartitionNum());
        Assert.assertEquals("test", segments.get(1).getDataSource());
        Assert.assertEquals(Intervals.of("2014/P1D"), segments.get(1).getInterval());
        Assert.assertEquals(NumberedShardSpec.class, segments.get(1).getShardSpec().getClass());
        Assert.assertEquals(1, segments.get(1).getShardSpec().getPartitionNum());
    }

    @Test
    public void testTransformSpec() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("2014-01-01T00:00:10Z,a,1\n");
            writer.write("2014-01-01T01:00:20Z,b,1\n");
            writer.write("2014-01-01T02:00:30Z,c,1\n");
        }
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, new org.apache.druid.segment.transform.TransformSpec(new SelectorDimFilter("dim", "b", null), ImmutableList.of(new org.apache.druid.segment.transform.ExpressionTransform("dimt", "concat(dim,dim)", ExprMacroTable.nil()))), null, IndexTaskTest.createTuningConfigWithMaxRowsPerSegment(2, true, false), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        Assert.assertEquals(indexTask.getId(), indexTask.getGroupId());
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(1, segments.size());
        Assert.assertEquals("test", segments.get(0).getDataSource());
        Assert.assertEquals(Intervals.of("2014/P1D"), segments.get(0).getInterval());
        Assert.assertEquals(NumberedShardSpec.class, segments.get(0).getShardSpec().getClass());
        Assert.assertEquals(0, segments.get(0).getShardSpec().getPartitionNum());
    }

    @Test
    public void testWithArbitraryGranularity() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("2014-01-01T00:00:10Z,a,1\n");
            writer.write("2014-01-01T01:00:20Z,b,1\n");
            writer.write("2014-01-01T02:00:30Z,c,1\n");
        }
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, new org.apache.druid.segment.indexing.granularity.ArbitraryGranularitySpec(Granularities.MINUTE, Collections.singletonList(Intervals.of("2014-01-01/2014-01-02"))), IndexTaskTest.createTuningConfigWithMaxRowsPerSegment(10, false, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(1, segments.size());
    }

    @Test
    public void testIntervalBucketing() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("2014-01-01T07:59:59.977Z,a,1\n");
            writer.write("2014-01-01T08:00:00.000Z,b,1\n");
        }
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, new org.apache.druid.segment.indexing.granularity.UniformGranularitySpec(Granularities.HOUR, Granularities.HOUR, Collections.singletonList(Intervals.of("2014-01-01T08:00:00Z/2014-01-01T09:00:00Z"))), IndexTaskTest.createTuningConfigWithMaxRowsPerSegment(50, false, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(1, segments.size());
    }

    @Test
    public void testNumShardsProvided() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("2014-01-01T00:00:10Z,a,1\n");
            writer.write("2014-01-01T01:00:20Z,b,1\n");
            writer.write("2014-01-01T02:00:30Z,c,1\n");
        }
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, null, IndexTaskTest.createTuningConfigWithNumShards(1, null, false, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(1, segments.size());
        Assert.assertEquals("test", segments.get(0).getDataSource());
        Assert.assertEquals(Intervals.of("2014/P1D"), segments.get(0).getInterval());
        Assert.assertEquals(NoneShardSpec.class, segments.get(0).getShardSpec().getClass());
        Assert.assertEquals(0, segments.get(0).getShardSpec().getPartitionNum());
    }

    @Test
    public void testNumShardsAndPartitionDimensionsProvided() throws Exception {
        final File tmpDir = temporaryFolder.newFolder();
        final File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("2014-01-01T00:00:10Z,a,1\n");
            writer.write("2014-01-01T01:00:20Z,b,1\n");
            writer.write("2014-01-01T02:00:30Z,c,1\n");
        }
        final IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, null, IndexTaskTest.createTuningConfigWithNumShards(2, ImmutableList.of("dim"), false, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        runTask(indexTask);
        Assert.assertEquals(2, segments.size());
        for (DataSegment segment : segments) {
            Assert.assertEquals("test", segment.getDataSource());
            Assert.assertEquals(Intervals.of("2014/P1D"), segment.getInterval());
            Assert.assertEquals(HashBasedNumberedShardSpec.class, segment.getShardSpec().getClass());
            final File segmentFile = segmentLoader.getSegmentFiles(segment);
            final WindowedStorageAdapter adapter = new WindowedStorageAdapter(new org.apache.druid.segment.QueryableIndexStorageAdapter(indexIO.loadIndex(segmentFile)), segment.getInterval());
            final Sequence<Cursor> cursorSequence = adapter.getAdapter().makeCursors(null, segment.getInterval(), EMPTY, ALL, false, null);
            final List<Integer> hashes = cursorSequence.map(( cursor) -> {
                final DimensionSelector selector = cursor.getColumnSelectorFactory().makeDimensionSelector(new DefaultDimensionSpec("dim", "dim"));
                try {
                    final int hash = HashBasedNumberedShardSpec.hash(jsonMapper, Collections.singletonList(selector.getObject()));
                    cursor.advance();
                    return hash;
                } catch ( e) {
                    throw new <e>RuntimeException();
                }
            }).toList();
            Assert.assertTrue(hashes.stream().allMatch(( h) -> (h.intValue()) == (hashes.get(0))));
        }
    }

    @Test
    public void testAppendToExisting() throws Exception {
        segmentAllocatePartitionCounter = 0;
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("2014-01-01T00:00:10Z,a,1\n");
            writer.write("2014-01-01T01:00:20Z,b,1\n");
            writer.write("2014-01-01T02:00:30Z,c,1\n");
        }
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, null, IndexTaskTest.createTuningConfigWithMaxRowsPerSegment(2, false, false), true), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        Assert.assertEquals("index_append_test", indexTask.getGroupId());
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(2, segmentAllocatePartitionCounter);
        Assert.assertEquals(2, segments.size());
        Assert.assertEquals("test", segments.get(0).getDataSource());
        Assert.assertEquals(Intervals.of("2014/P1D"), segments.get(0).getInterval());
        Assert.assertEquals(NumberedShardSpec.class, segments.get(0).getShardSpec().getClass());
        Assert.assertEquals(0, segments.get(0).getShardSpec().getPartitionNum());
        Assert.assertEquals("test", segments.get(1).getDataSource());
        Assert.assertEquals(Intervals.of("2014/P1D"), segments.get(1).getInterval());
        Assert.assertEquals(NumberedShardSpec.class, segments.get(1).getShardSpec().getClass());
        Assert.assertEquals(1, segments.get(1).getShardSpec().getPartitionNum());
    }

    @Test
    public void testIntervalNotSpecified() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("2014-01-01T00:00:10Z,a,1\n");
            writer.write("2014-01-01T01:00:20Z,b,1\n");
            writer.write("2014-01-01T02:00:30Z,c,1\n");
        }
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, new org.apache.druid.segment.indexing.granularity.UniformGranularitySpec(Granularities.HOUR, Granularities.MINUTE, null), IndexTaskTest.createTuningConfigWithMaxRowsPerSegment(2, false, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(3, segments.size());
        Assert.assertEquals("test", segments.get(0).getDataSource());
        Assert.assertEquals(Intervals.of("2014-01-01T00/PT1H"), segments.get(0).getInterval());
        Assert.assertTrue(segments.get(0).getShardSpec().getClass().equals(NoneShardSpec.class));
        Assert.assertEquals(0, segments.get(0).getShardSpec().getPartitionNum());
        Assert.assertEquals("test", segments.get(1).getDataSource());
        Assert.assertEquals(Intervals.of("2014-01-01T01/PT1H"), segments.get(1).getInterval());
        Assert.assertTrue(segments.get(1).getShardSpec().getClass().equals(NoneShardSpec.class));
        Assert.assertEquals(0, segments.get(1).getShardSpec().getPartitionNum());
        Assert.assertEquals("test", segments.get(2).getDataSource());
        Assert.assertEquals(Intervals.of("2014-01-01T02/PT1H"), segments.get(2).getInterval());
        Assert.assertTrue(segments.get(2).getShardSpec().getClass().equals(NoneShardSpec.class));
        Assert.assertEquals(0, segments.get(2).getShardSpec().getPartitionNum());
    }

    @Test
    public void testCSVFileWithHeader() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("time,d,val\n");
            writer.write("2014-01-01T00:00:10Z,a,1\n");
        }
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("time", "auto", null), new DimensionsSpec(null, new ArrayList(), new ArrayList()), null, null, true, 0), null, IndexTaskTest.createTuningConfigWithMaxRowsPerSegment(2, false, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(1, segments.size());
        Assert.assertEquals(Collections.singletonList("d"), segments.get(0).getDimensions());
        Assert.assertEquals(Collections.singletonList("val"), segments.get(0).getMetrics());
        Assert.assertEquals(Intervals.of("2014/P1D"), segments.get(0).getInterval());
    }

    @Test
    public void testCSVFileWithHeaderColumnOverride() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("time,d,val\n");
            writer.write("2014-01-01T00:00:10Z,a,1\n");
        }
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("time", "auto", null), new DimensionsSpec(null, new ArrayList(), new ArrayList()), null, Arrays.asList("time", "dim", "val"), true, 0), null, IndexTaskTest.createTuningConfigWithMaxRowsPerSegment(2, false, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(1, segments.size());
        Assert.assertEquals(Collections.singletonList("d"), segments.get(0).getDimensions());
        Assert.assertEquals(Collections.singletonList("val"), segments.get(0).getMetrics());
        Assert.assertEquals(Intervals.of("2014/P1D"), segments.get(0).getInterval());
    }

    @Test
    public void testWithSmallMaxTotalRows() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("2014-01-01T00:00:10Z,a,1\n");
            writer.write("2014-01-01T00:00:10Z,b,2\n");
            writer.write("2014-01-01T00:00:10Z,c,3\n");
            writer.write("2014-01-01T01:00:20Z,a,1\n");
            writer.write("2014-01-01T01:00:20Z,b,2\n");
            writer.write("2014-01-01T01:00:20Z,c,3\n");
            writer.write("2014-01-01T02:00:30Z,a,1\n");
            writer.write("2014-01-01T02:00:30Z,b,2\n");
            writer.write("2014-01-01T02:00:30Z,c,3\n");
        }
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, new org.apache.druid.segment.indexing.granularity.UniformGranularitySpec(Granularities.HOUR, Granularities.MINUTE, null), IndexTaskTest.createTuningConfig(2, 2, null, 2L, null, null, false, false, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(6, segments.size());
        for (int i = 0; i < 6; i++) {
            final DataSegment segment = segments.get(i);
            final Interval expectedInterval = Intervals.of(StringUtils.format("2014-01-01T0%d/PT1H", (i / 2)));
            final int expectedPartitionNum = i % 2;
            Assert.assertEquals("test", segment.getDataSource());
            Assert.assertEquals(expectedInterval, segment.getInterval());
            Assert.assertEquals(NumberedShardSpec.class, segment.getShardSpec().getClass());
            Assert.assertEquals(expectedPartitionNum, segment.getShardSpec().getPartitionNum());
        }
    }

    @Test
    public void testPerfectRollup() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        IndexTaskTest.populateRollupTestData(tmpFile);
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, new org.apache.druid.segment.indexing.granularity.UniformGranularitySpec(Granularities.DAY, Granularities.DAY, true, null), IndexTaskTest.createTuningConfig(3, 2, null, 2L, null, null, false, true, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(3, segments.size());
        for (int i = 0; i < 3; i++) {
            final DataSegment segment = segments.get(i);
            final Interval expectedInterval = Intervals.of("2014-01-01T00:00:00.000Z/2014-01-02T00:00:00.000Z");
            Assert.assertEquals("test", segment.getDataSource());
            Assert.assertEquals(expectedInterval, segment.getInterval());
            Assert.assertTrue(segment.getShardSpec().getClass().equals(HashBasedNumberedShardSpec.class));
            Assert.assertEquals(i, segment.getShardSpec().getPartitionNum());
        }
    }

    @Test
    public void testBestEffortRollup() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        IndexTaskTest.populateRollupTestData(tmpFile);
        IndexTask indexTask = new IndexTask(null, null, createIngestionSpec(tmpDir, null, new org.apache.druid.segment.indexing.granularity.UniformGranularitySpec(Granularities.DAY, Granularities.DAY, true, null), IndexTaskTest.createTuningConfig(3, 2, null, 2L, null, null, false, false, true), false), null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(5, segments.size());
        for (int i = 0; i < 5; i++) {
            final DataSegment segment = segments.get(i);
            final Interval expectedInterval = Intervals.of("2014-01-01T00:00:00.000Z/2014-01-02T00:00:00.000Z");
            Assert.assertEquals("test", segment.getDataSource());
            Assert.assertEquals(expectedInterval, segment.getInterval());
            Assert.assertEquals(NumberedShardSpec.class, segment.getShardSpec().getClass());
            Assert.assertEquals(i, segment.getShardSpec().getPartitionNum());
        }
    }

    @Test
    public void testIgnoreParseException() throws Exception {
        final File tmpDir = temporaryFolder.newFolder();
        final File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("time,d,val\n");
            writer.write("unparseable,a,1\n");
            writer.write("2014-01-01T00:00:10Z,a,1\n");
        }
        // GranularitySpec.intervals and numShards must be null to verify reportParseException=false is respected both in
        // IndexTask.determineShardSpecs() and IndexTask.generateAndPublishSegments()
        final IndexIngestionSpec parseExceptionIgnoreSpec = // ignore parse exception,
        createIngestionSpec(tmpDir, new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("time", "auto", null), new DimensionsSpec(null, new ArrayList(), new ArrayList()), null, Arrays.asList("time", "dim", "val"), true, 0), null, IndexTaskTest.createTuningConfig(2, null, null, null, null, null, false, false, false), false);
        IndexTask indexTask = new IndexTask(null, null, parseExceptionIgnoreSpec, null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        Assert.assertEquals(Collections.singletonList("d"), segments.get(0).getDimensions());
        Assert.assertEquals(Collections.singletonList("val"), segments.get(0).getMetrics());
        Assert.assertEquals(Intervals.of("2014/P1D"), segments.get(0).getInterval());
    }

    @Test
    public void testReportParseException() throws Exception {
        final File tmpDir = temporaryFolder.newFolder();
        final File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("time,d,val\n");
            writer.write("unparseable,a,1\n");
            writer.write("2014-01-01T00:00:10Z,a,1\n");
        }
        final IndexIngestionSpec parseExceptionIgnoreSpec = // report parse exception
        createIngestionSpec(tmpDir, new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("time", "auto", null), new DimensionsSpec(null, new ArrayList(), new ArrayList()), null, Arrays.asList("time", "dim", "val"), true, 0), null, IndexTaskTest.createTuningConfig(2, null, null, null, null, null, false, false, true), false);
        IndexTask indexTask = new IndexTask(null, null, parseExceptionIgnoreSpec, null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        TaskStatus status = runTask(indexTask).lhs;
        Assert.assertEquals(FAILED, status.getStatusCode());
        IndexTaskTest.checkTaskStatusErrorMsgForParseExceptionsExceeded(status);
        Map<String, Object> expectedUnparseables = ImmutableMap.of(DETERMINE_PARTITIONS, new ArrayList(), BUILD_SEGMENTS, Collections.singletonList("Unparseable timestamp found! Event: {time=unparseable, d=a, val=1}"));
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Assert.assertEquals(expectedUnparseables, reportData.getUnparseableEvents());
    }

    @Test
    public void testMultipleParseExceptionsSuccess() throws Exception {
        final File tmpDir = temporaryFolder.newFolder();
        final File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("{\"time\":\"unparseable\",\"dim\":\"a\",\"dimLong\":2,\"dimFloat\":3.0,\"val\":1}\n");// unparseable time

            writer.write("{\"time\":\"2014-01-01T00:00:10Z\",\"dim\":\"a\",\"dimLong\":2,\"dimFloat\":3.0,\"val\":1}\n");// valid row

            writer.write("{\"time\":\"2014-01-01T00:00:10Z\",\"dim\":\"b\",\"dimLong\":\"notnumber\",\"dimFloat\":3.0,\"val\":1}\n");// row with invalid long dimension

            writer.write("{\"time\":\"2014-01-01T00:00:10Z\",\"dim\":\"b\",\"dimLong\":2,\"dimFloat\":\"notnumber\",\"val\":1}\n");// row with invalid float dimension

            writer.write("{\"time\":\"2014-01-01T00:00:10Z\",\"dim\":\"b\",\"dimLong\":2,\"dimFloat\":4.0,\"val\":\"notnumber\"}\n");// row with invalid metric

            writer.write("{\"time\":9.0x,\"dim\":\"a\",\"dimLong\":2,\"dimFloat\":3.0,\"val\":1}\n");// invalid JSON

            writer.write("{\"time\":\"3014-03-01T00:00:10Z\",\"dim\":\"outsideofinterval\",\"dimLong\":2,\"dimFloat\":3.0,\"val\":1}\n");// thrown away

            writer.write("{\"time\":\"99999999999-01-01T00:00:10Z\",\"dim\":\"b\",\"dimLong\":2,\"dimFloat\":3.0,\"val\":1}\n");// unparseable time

            writer.write("this is not JSON\n");// invalid JSON

        }
        final IndexTask.IndexTuningConfig tuningConfig = new IndexTask.IndexTuningConfig(null, 2, null, null, null, null, null, null, IndexTaskTest.indexSpec, null, true, false, true, false, null, null, null, true, 7, 7);
        final IndexIngestionSpec parseExceptionIgnoreSpec = createIngestionSpec(tmpDir, new org.apache.druid.data.input.impl.JSONParseSpec(new TimestampSpec("time", "auto", null), new DimensionsSpec(Arrays.asList(new StringDimensionSchema("dim"), new LongDimensionSchema("dimLong"), new FloatDimensionSchema("dimFloat")), new ArrayList(), new ArrayList()), null, null), null, tuningConfig, false);
        IndexTask indexTask = new IndexTask(null, null, parseExceptionIgnoreSpec, null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        TaskStatus status = runTask(indexTask).lhs;
        Assert.assertEquals(SUCCESS, status.getStatusCode());
        Assert.assertEquals(null, status.getErrorMsg());
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Map<String, Object> expectedMetrics = ImmutableMap.of(DETERMINE_PARTITIONS, ImmutableMap.of(PROCESSED_WITH_ERROR, 0, PROCESSED, 4, UNPARSEABLE, 4, THROWN_AWAY, 1), BUILD_SEGMENTS, ImmutableMap.of(PROCESSED_WITH_ERROR, 3, PROCESSED, 1, UNPARSEABLE, 4, THROWN_AWAY, 1));
        Assert.assertEquals(expectedMetrics, reportData.getRowStats());
        Map<String, Object> expectedUnparseables = ImmutableMap.of(DETERMINE_PARTITIONS, Arrays.asList("Unable to parse row [this is not JSON]", "Unparseable timestamp found! Event: {time=99999999999-01-01T00:00:10Z, dim=b, dimLong=2, dimFloat=3.0, val=1}", "Unable to parse row [{\"time\":9.0x,\"dim\":\"a\",\"dimLong\":2,\"dimFloat\":3.0,\"val\":1}]", "Unparseable timestamp found! Event: {time=unparseable, dim=a, dimLong=2, dimFloat=3.0, val=1}"), BUILD_SEGMENTS, Arrays.asList("Unable to parse row [this is not JSON]", "Unparseable timestamp found! Event: {time=99999999999-01-01T00:00:10Z, dim=b, dimLong=2, dimFloat=3.0, val=1}", "Unable to parse row [{\"time\":9.0x,\"dim\":\"a\",\"dimLong\":2,\"dimFloat\":3.0,\"val\":1}]", "Found unparseable columns in row: [MapBasedInputRow{timestamp=2014-01-01T00:00:10.000Z, event={time=2014-01-01T00:00:10Z, dim=b, dimLong=2, dimFloat=4.0, val=notnumber}, dimensions=[dim, dimLong, dimFloat]}], exceptions: [Unable to parse value[notnumber] for field[val],]", "Found unparseable columns in row: [MapBasedInputRow{timestamp=2014-01-01T00:00:10.000Z, event={time=2014-01-01T00:00:10Z, dim=b, dimLong=2, dimFloat=notnumber, val=1}, dimensions=[dim, dimLong, dimFloat]}], exceptions: [could not convert value [notnumber] to float,]", "Found unparseable columns in row: [MapBasedInputRow{timestamp=2014-01-01T00:00:10.000Z, event={time=2014-01-01T00:00:10Z, dim=b, dimLong=notnumber, dimFloat=3.0, val=1}, dimensions=[dim, dimLong, dimFloat]}], exceptions: [could not convert value [notnumber] to long,]", "Unparseable timestamp found! Event: {time=unparseable, dim=a, dimLong=2, dimFloat=3.0, val=1}"));
        Assert.assertEquals(expectedUnparseables, reportData.getUnparseableEvents());
    }

    @Test
    public void testMultipleParseExceptionsFailure() throws Exception {
        final File tmpDir = temporaryFolder.newFolder();
        final File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("time,dim,dimLong,dimFloat,val\n");
            writer.write("unparseable,a,2,3.0,1\n");// unparseable

            writer.write("2014-01-01T00:00:10Z,a,2,3.0,1\n");// valid row

            writer.write("9.0,a,2,3.0,1\n");// unparseable

            writer.write("3014-03-01T00:00:10Z,outsideofinterval,2,3.0,1\n");// thrown away

            writer.write("99999999999-01-01T00:00:10Z,b,2,3.0,1\n");// unparseable

        }
        // Allow up to 3 parse exceptions, and save up to 2 parse exceptions
        final IndexTask.IndexTuningConfig tuningConfig = new IndexTask.IndexTuningConfig(null, 2, null, null, null, null, null, null, IndexTaskTest.indexSpec, null, true, false, false, false, null, null, null, true, 2, 5);
        final IndexIngestionSpec parseExceptionIgnoreSpec = createIngestionSpec(tmpDir, new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("time", "auto", null), new DimensionsSpec(Arrays.asList(new StringDimensionSchema("dim"), new LongDimensionSchema("dimLong"), new FloatDimensionSchema("dimFloat")), new ArrayList(), new ArrayList()), null, Arrays.asList("time", "dim", "dimLong", "dimFloat", "val"), true, 0), null, tuningConfig, false);
        IndexTask indexTask = new IndexTask(null, null, parseExceptionIgnoreSpec, null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        TaskStatus status = runTask(indexTask).lhs;
        Assert.assertEquals(FAILED, status.getStatusCode());
        IndexTaskTest.checkTaskStatusErrorMsgForParseExceptionsExceeded(status);
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Map<String, Object> expectedMetrics = ImmutableMap.of(DETERMINE_PARTITIONS, ImmutableMap.of(PROCESSED_WITH_ERROR, 0, PROCESSED, 0, UNPARSEABLE, 0, THROWN_AWAY, 0), BUILD_SEGMENTS, ImmutableMap.of(PROCESSED_WITH_ERROR, 0, PROCESSED, 1, UNPARSEABLE, 3, THROWN_AWAY, 2));
        Assert.assertEquals(expectedMetrics, reportData.getRowStats());
        Map<String, Object> expectedUnparseables = ImmutableMap.of(DETERMINE_PARTITIONS, new ArrayList(), BUILD_SEGMENTS, Arrays.asList("Unparseable timestamp found! Event: {time=99999999999-01-01T00:00:10Z, dim=b, dimLong=2, dimFloat=3.0, val=1}", "Unparseable timestamp found! Event: {time=9.0, dim=a, dimLong=2, dimFloat=3.0, val=1}", "Unparseable timestamp found! Event: {time=unparseable, dim=a, dimLong=2, dimFloat=3.0, val=1}"));
        Assert.assertEquals(expectedUnparseables, reportData.getUnparseableEvents());
    }

    @Test
    public void testMultipleParseExceptionsFailureAtDeterminePartitions() throws Exception {
        final File tmpDir = temporaryFolder.newFolder();
        final File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("time,dim,dimLong,dimFloat,val\n");
            writer.write("unparseable,a,2,3.0,1\n");// unparseable

            writer.write("2014-01-01T00:00:10Z,a,2,3.0,1\n");// valid row

            writer.write("9.0,a,2,3.0,1\n");// unparseable

            writer.write("3014-03-01T00:00:10Z,outsideofinterval,2,3.0,1\n");// thrown away

            writer.write("99999999999-01-01T00:00:10Z,b,2,3.0,1\n");// unparseable

        }
        // Allow up to 3 parse exceptions, and save up to 2 parse exceptions
        final IndexTask.IndexTuningConfig tuningConfig = new IndexTask.IndexTuningConfig(null, 2, null, null, null, null, null, null, IndexTaskTest.indexSpec, null, true, false, true, false, null, null, null, true, 2, 5);
        final IndexIngestionSpec parseExceptionIgnoreSpec = createIngestionSpec(tmpDir, new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("time", "auto", null), new DimensionsSpec(Arrays.asList(new StringDimensionSchema("dim"), new LongDimensionSchema("dimLong"), new FloatDimensionSchema("dimFloat")), new ArrayList(), new ArrayList()), null, Arrays.asList("time", "dim", "dimLong", "dimFloat", "val"), true, 0), null, tuningConfig, false);
        IndexTask indexTask = new IndexTask(null, null, parseExceptionIgnoreSpec, null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        TaskStatus status = runTask(indexTask).lhs;
        Assert.assertEquals(FAILED, status.getStatusCode());
        IndexTaskTest.checkTaskStatusErrorMsgForParseExceptionsExceeded(status);
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Map<String, Object> expectedMetrics = ImmutableMap.of(DETERMINE_PARTITIONS, ImmutableMap.of(PROCESSED_WITH_ERROR, 0, PROCESSED, 1, UNPARSEABLE, 3, THROWN_AWAY, 2), BUILD_SEGMENTS, ImmutableMap.of(PROCESSED_WITH_ERROR, 0, PROCESSED, 0, UNPARSEABLE, 0, THROWN_AWAY, 0));
        Assert.assertEquals(expectedMetrics, reportData.getRowStats());
        Map<String, Object> expectedUnparseables = ImmutableMap.of(DETERMINE_PARTITIONS, Arrays.asList("Unparseable timestamp found! Event: {time=99999999999-01-01T00:00:10Z, dim=b, dimLong=2, dimFloat=3.0, val=1}", "Unparseable timestamp found! Event: {time=9.0, dim=a, dimLong=2, dimFloat=3.0, val=1}", "Unparseable timestamp found! Event: {time=unparseable, dim=a, dimLong=2, dimFloat=3.0, val=1}"), BUILD_SEGMENTS, new ArrayList());
        Assert.assertEquals(expectedUnparseables, reportData.getUnparseableEvents());
    }

    @Test
    public void testCsvWithHeaderOfEmptyColumns() throws Exception {
        final File tmpDir = temporaryFolder.newFolder();
        File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("time,,\n");
            writer.write("2014-01-01T00:00:10Z,a,1\n");
        }
        tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("time,dim,\n");
            writer.write("2014-01-01T00:00:10Z,a,1\n");
        }
        tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write("time,,val\n");
            writer.write("2014-01-01T00:00:10Z,a,1\n");
        }
        final IndexIngestionSpec parseExceptionIgnoreSpec = // report parse exception
        createIngestionSpec(tmpDir, new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("time", "auto", null), new DimensionsSpec(null, null, null), null, null, true, 0), null, IndexTaskTest.createTuningConfig(2, 1, null, null, null, null, false, true, true), false);
        IndexTask indexTask = new IndexTask(null, null, parseExceptionIgnoreSpec, null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        final List<DataSegment> segments = runTask(indexTask).rhs;
        // the order of result segments can be changed because hash shardSpec is used.
        // the below loop is to make this test deterministic.
        Assert.assertEquals(2, segments.size());
        Assert.assertNotEquals(segments.get(0), segments.get(1));
        for (DataSegment segment : segments) {
            System.out.println(segment.getDimensions());
        }
        for (int i = 0; i < 2; i++) {
            final DataSegment segment = segments.get(i);
            final Set<String> dimensions = new java.util.HashSet(segment.getDimensions());
            Assert.assertTrue(StringUtils.format("Actual dimensions: %s", dimensions), ((dimensions.equals(Sets.newHashSet("dim", "column_3"))) || (dimensions.equals(Sets.newHashSet("column_2", "column_3")))));
            Assert.assertEquals(Collections.singletonList("val"), segment.getMetrics());
            Assert.assertEquals(Intervals.of("2014/P1D"), segment.getInterval());
        }
    }

    @Test
    public void testCsvWithHeaderOfEmptyTimestamp() throws Exception {
        final File tmpDir = temporaryFolder.newFolder();
        final File tmpFile = File.createTempFile("druid", "index", tmpDir);
        try (BufferedWriter writer = Files.newWriter(tmpFile, StandardCharsets.UTF_8)) {
            writer.write(",,\n");
            writer.write("2014-01-01T00:00:10Z,a,1\n");
        }
        final IndexIngestionSpec parseExceptionIgnoreSpec = // report parse exception
        createIngestionSpec(tmpDir, new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("time", "auto", null), new DimensionsSpec(null, new ArrayList(), new ArrayList()), null, Arrays.asList("time", "", ""), true, 0), null, IndexTaskTest.createTuningConfig(2, null, null, null, null, null, false, false, true), false);
        IndexTask indexTask = new IndexTask(null, null, parseExceptionIgnoreSpec, null, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory);
        TaskStatus status = runTask(indexTask).lhs;
        Assert.assertEquals(FAILED, status.getStatusCode());
        IndexTaskTest.checkTaskStatusErrorMsgForParseExceptionsExceeded(status);
        IngestionStatsAndErrorsTaskReportData reportData = getTaskReportData();
        Map<String, Object> expectedUnparseables = ImmutableMap.of(DETERMINE_PARTITIONS, new ArrayList(), BUILD_SEGMENTS, Collections.singletonList("Unparseable timestamp found! Event: {column_1=2014-01-01T00:00:10Z, column_2=a, column_3=1}"));
        Assert.assertEquals(expectedUnparseables, reportData.getUnparseableEvents());
    }
}

