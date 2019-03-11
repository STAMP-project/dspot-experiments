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
package org.apache.druid.indexing.firehose;


import Intervals.ETERNITY;
import TransformSpec.NONE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.druid.data.input.Firehose;
import org.apache.druid.data.input.FirehoseFactory;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.InputRowParser;
import org.apache.druid.data.input.impl.MapInputRowParser;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.indexing.common.TestUtils;
import org.apache.druid.indexing.common.config.TaskStorageConfig;
import org.apache.druid.indexing.common.task.NoopTask;
import org.apache.druid.indexing.common.task.Task;
import org.apache.druid.indexing.overlord.HeapMemoryTaskStorage;
import org.apache.druid.indexing.overlord.TaskLockbox;
import org.apache.druid.indexing.overlord.TaskStorage;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.math.expr.ExprMacroTable;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.IndexMergerV9;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.segment.column.ColumnHolder;
import org.apache.druid.segment.realtime.firehose.CombiningFirehoseFactory;
import org.apache.druid.segment.transform.TransformSpec;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.TimelineObjectHolder;
import org.apache.druid.timeline.partition.NumberedShardSpec;
import org.apache.druid.timeline.partition.PartitionChunk;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class IngestSegmentFirehoseFactoryTest {
    private static final ObjectMapper MAPPER;

    private static final IndexMergerV9 INDEX_MERGER_V9;

    private static final IndexIO INDEX_IO;

    private static final TaskStorage TASK_STORAGE;

    private static final TaskLockbox TASK_LOCKBOX;

    private static final Task TASK;

    static {
        TestUtils testUtils = new TestUtils();
        MAPPER = IngestSegmentFirehoseFactoryTest.setupInjectablesInObjectMapper(TestHelper.makeJsonMapper());
        INDEX_MERGER_V9 = testUtils.getTestIndexMergerV9();
        INDEX_IO = testUtils.getTestIndexIO();
        TASK_STORAGE = new HeapMemoryTaskStorage(new TaskStorageConfig(null) {});
        TASK_LOCKBOX = new TaskLockbox(IngestSegmentFirehoseFactoryTest.TASK_STORAGE);
        TASK = NoopTask.create();
        IngestSegmentFirehoseFactoryTest.TASK_LOCKBOX.add(IngestSegmentFirehoseFactoryTest.TASK);
    }

    public IngestSegmentFirehoseFactoryTest(String testName, FirehoseFactory factory, InputRowParser rowParser) {
        this.factory = factory;
        // Must decorate the parser, since IngestSegmentFirehoseFactory will undecorate it.
        this.rowParser = NONE.decorate(rowParser);
    }

    private static final Logger log = new Logger(IngestSegmentFirehoseFactoryTest.class);

    private static final String DATA_SOURCE_NAME = "testDataSource";

    private static final String DATA_SOURCE_VERSION = "version";

    private static final Integer BINARY_VERSION = -1;

    private static final String DIM_NAME = "testDimName";

    private static final String DIM_VALUE = "testDimValue";

    private static final String DIM_LONG_NAME = "testDimLongName";

    private static final String DIM_FLOAT_NAME = "testDimFloatName";

    private static final String METRIC_LONG_NAME = "testLongMetric";

    private static final String METRIC_FLOAT_NAME = "testFloatMetric";

    private static final Long METRIC_LONG_VALUE = 1L;

    private static final Float METRIC_FLOAT_VALUE = 1.0F;

    private static final String TIME_COLUMN = "ts";

    private static final Integer MAX_SHARD_NUMBER = 10;

    private static final Integer MAX_ROWS = 10;

    private static final File tmpDir = Files.createTempDir();

    private static final File persistDir = Paths.get(IngestSegmentFirehoseFactoryTest.tmpDir.getAbsolutePath(), "indexTestMerger").toFile();

    private static final List<DataSegment> segmentSet = new ArrayList<>(IngestSegmentFirehoseFactoryTest.MAX_SHARD_NUMBER);

    private final FirehoseFactory<InputRowParser> factory;

    private final InputRowParser rowParser;

    private static final InputRowParser<Map<String, Object>> ROW_PARSER = new MapInputRowParser(new org.apache.druid.data.input.impl.TimeAndDimsParseSpec(new TimestampSpec(IngestSegmentFirehoseFactoryTest.TIME_COLUMN, "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(ImmutableList.of(IngestSegmentFirehoseFactoryTest.DIM_NAME)), ImmutableList.of(IngestSegmentFirehoseFactoryTest.DIM_FLOAT_NAME, IngestSegmentFirehoseFactoryTest.DIM_LONG_NAME), ImmutableList.of())));

    @Test
    public void sanityTest() {
        if ((factory) instanceof CombiningFirehoseFactory) {
            // This method tests IngestSegmentFirehoseFactory-specific methods.
            return;
        }
        final IngestSegmentFirehoseFactory isfFactory = ((IngestSegmentFirehoseFactory) (factory));
        Assert.assertEquals(IngestSegmentFirehoseFactoryTest.TASK.getDataSource(), isfFactory.getDataSource());
        if ((isfFactory.getDimensions()) != null) {
            Assert.assertArrayEquals(new String[]{ IngestSegmentFirehoseFactoryTest.DIM_NAME }, isfFactory.getDimensions().toArray());
        }
        Assert.assertEquals(ETERNITY, isfFactory.getInterval());
        if ((isfFactory.getMetrics()) != null) {
            Assert.assertEquals(ImmutableSet.of(IngestSegmentFirehoseFactoryTest.METRIC_LONG_NAME, IngestSegmentFirehoseFactoryTest.METRIC_FLOAT_NAME), ImmutableSet.copyOf(isfFactory.getMetrics()));
        }
    }

    @Test
    public void simpleFirehoseReadingTest() throws IOException {
        Assert.assertEquals(IngestSegmentFirehoseFactoryTest.MAX_SHARD_NUMBER.longValue(), IngestSegmentFirehoseFactoryTest.segmentSet.size());
        Integer rowcount = 0;
        try (final Firehose firehose = factory.connect(rowParser, null)) {
            while (firehose.hasMore()) {
                InputRow row = firehose.nextRow();
                Assert.assertArrayEquals(new String[]{ IngestSegmentFirehoseFactoryTest.DIM_NAME }, row.getDimensions().toArray());
                Assert.assertArrayEquals(new String[]{ IngestSegmentFirehoseFactoryTest.DIM_VALUE }, row.getDimension(IngestSegmentFirehoseFactoryTest.DIM_NAME).toArray());
                Assert.assertEquals(IngestSegmentFirehoseFactoryTest.METRIC_LONG_VALUE.longValue(), row.getMetric(IngestSegmentFirehoseFactoryTest.METRIC_LONG_NAME));
                Assert.assertEquals(IngestSegmentFirehoseFactoryTest.METRIC_FLOAT_VALUE, row.getMetric(IngestSegmentFirehoseFactoryTest.METRIC_FLOAT_NAME).floatValue(), ((IngestSegmentFirehoseFactoryTest.METRIC_FLOAT_VALUE) * 1.0E-4));
                ++rowcount;
            } 
        }
        Assert.assertEquals((((int) (IngestSegmentFirehoseFactoryTest.MAX_SHARD_NUMBER)) * (IngestSegmentFirehoseFactoryTest.MAX_ROWS)), ((int) (rowcount)));
    }

    @Test
    public void testTransformSpec() throws IOException {
        Assert.assertEquals(IngestSegmentFirehoseFactoryTest.MAX_SHARD_NUMBER.longValue(), IngestSegmentFirehoseFactoryTest.segmentSet.size());
        Integer rowcount = 0;
        final TransformSpec transformSpec = new TransformSpec(new org.apache.druid.query.filter.SelectorDimFilter(ColumnHolder.TIME_COLUMN_NAME, "1", null), ImmutableList.of(new org.apache.druid.segment.transform.ExpressionTransform(IngestSegmentFirehoseFactoryTest.METRIC_FLOAT_NAME, ((IngestSegmentFirehoseFactoryTest.METRIC_FLOAT_NAME) + " * 10"), ExprMacroTable.nil())));
        int skipped = 0;
        try (final Firehose firehose = factory.connect(transformSpec.decorate(rowParser), null)) {
            while (firehose.hasMore()) {
                InputRow row = firehose.nextRow();
                if (row == null) {
                    skipped++;
                    continue;
                }
                Assert.assertArrayEquals(new String[]{ IngestSegmentFirehoseFactoryTest.DIM_NAME }, row.getDimensions().toArray());
                Assert.assertArrayEquals(new String[]{ IngestSegmentFirehoseFactoryTest.DIM_VALUE }, row.getDimension(IngestSegmentFirehoseFactoryTest.DIM_NAME).toArray());
                Assert.assertEquals(IngestSegmentFirehoseFactoryTest.METRIC_LONG_VALUE.longValue(), row.getMetric(IngestSegmentFirehoseFactoryTest.METRIC_LONG_NAME).longValue());
                Assert.assertEquals(((IngestSegmentFirehoseFactoryTest.METRIC_FLOAT_VALUE) * 10), row.getMetric(IngestSegmentFirehoseFactoryTest.METRIC_FLOAT_NAME).floatValue(), ((IngestSegmentFirehoseFactoryTest.METRIC_FLOAT_VALUE) * 1.0E-4));
                ++rowcount;
            } 
        }
        Assert.assertEquals(90, skipped);
        Assert.assertEquals(((int) (IngestSegmentFirehoseFactoryTest.MAX_ROWS)), ((int) (rowcount)));
    }

    @Test
    public void testGetUniqueDimensionsAndMetrics() {
        final int numSegmentsPerPartitionChunk = 5;
        final int numPartitionChunksPerTimelineObject = 10;
        final int numSegments = numSegmentsPerPartitionChunk * numPartitionChunksPerTimelineObject;
        final Interval interval = Intervals.of("2017-01-01/2017-01-02");
        final String version = "1";
        final List<TimelineObjectHolder<String, DataSegment>> timelineSegments = new ArrayList<>();
        for (int i = 0; i < numPartitionChunksPerTimelineObject; i++) {
            final List<PartitionChunk<DataSegment>> chunks = new ArrayList<>();
            for (int j = 0; j < numSegmentsPerPartitionChunk; j++) {
                final List<String> dims = IntStream.range(i, (i + numSegmentsPerPartitionChunk)).mapToObj(( suffix) -> "dim" + suffix).collect(Collectors.toList());
                final List<String> metrics = IntStream.range(i, (i + numSegmentsPerPartitionChunk)).mapToObj(( suffix) -> "met" + suffix).collect(Collectors.toList());
                final DataSegment segment = new DataSegment("ds", interval, version, ImmutableMap.of(), dims, metrics, new NumberedShardSpec(numPartitionChunksPerTimelineObject, i), 1, 1);
                final PartitionChunk<DataSegment> partitionChunk = new org.apache.druid.timeline.partition.NumberedPartitionChunk(i, numPartitionChunksPerTimelineObject, segment);
                chunks.add(partitionChunk);
            }
            final TimelineObjectHolder<String, DataSegment> timelineHolder = new TimelineObjectHolder(interval, version, new org.apache.druid.timeline.partition.PartitionHolder(chunks));
            timelineSegments.add(timelineHolder);
        }
        final String[] expectedDims = new String[]{ "dim9", "dim10", "dim11", "dim12", "dim13", "dim8", "dim7", "dim6", "dim5", "dim4", "dim3", "dim2", "dim1", "dim0" };
        final String[] expectedMetrics = new String[]{ "met9", "met10", "met11", "met12", "met13", "met8", "met7", "met6", "met5", "met4", "met3", "met2", "met1", "met0" };
        Assert.assertEquals(Arrays.asList(expectedDims), IngestSegmentFirehoseFactory.getUniqueDimensions(timelineSegments, null));
        Assert.assertEquals(Arrays.asList(expectedMetrics), IngestSegmentFirehoseFactory.getUniqueMetrics(timelineSegments));
    }
}

