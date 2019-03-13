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


import TransformSpec.NONE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.druid.data.input.Firehose;
import org.apache.druid.data.input.InputRow;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.InputRowParser;
import org.apache.druid.data.input.impl.MapInputRowParser;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.indexing.common.TestUtils;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.IndexMergerV9;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.SegmentId;
import org.apache.druid.timeline.partition.LinearShardSpec;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class IngestSegmentFirehoseFactoryTimelineTest {
    private static final String DATA_SOURCE = "foo";

    private static final String TIME_COLUMN = "t";

    private static final String[] DIMENSIONS = new String[]{ "d1" };

    private static final String[] METRICS = new String[]{ "m1" };

    // Must decorate the parser, since IngestSegmentFirehoseFactory will undecorate it.
    private static final InputRowParser<Map<String, Object>> ROW_PARSER = NONE.decorate(new MapInputRowParser(new org.apache.druid.data.input.impl.JSONParseSpec(new TimestampSpec(IngestSegmentFirehoseFactoryTimelineTest.TIME_COLUMN, "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList(IngestSegmentFirehoseFactoryTimelineTest.DIMENSIONS)), null, null), null, null)));

    private final IngestSegmentFirehoseFactory factory;

    private final File tmpDir;

    private final int expectedCount;

    private final long expectedSum;

    private static final ObjectMapper MAPPER;

    private static final IndexIO INDEX_IO;

    private static final IndexMergerV9 INDEX_MERGER_V9;

    static {
        TestUtils testUtils = new TestUtils();
        MAPPER = IngestSegmentFirehoseFactoryTest.setupInjectablesInObjectMapper(testUtils.getTestObjectMapper());
        INDEX_IO = testUtils.getTestIndexIO();
        INDEX_MERGER_V9 = testUtils.getTestIndexMergerV9();
    }

    public IngestSegmentFirehoseFactoryTimelineTest(String name, IngestSegmentFirehoseFactory factory, File tmpDir, int expectedCount, long expectedSum) {
        this.factory = factory;
        this.tmpDir = tmpDir;
        this.expectedCount = expectedCount;
        this.expectedSum = expectedSum;
    }

    @Test
    public void testSimple() throws Exception {
        int count = 0;
        long sum = 0;
        try (final Firehose firehose = factory.connect(IngestSegmentFirehoseFactoryTimelineTest.ROW_PARSER, null)) {
            while (firehose.hasMore()) {
                final InputRow row = firehose.nextRow();
                count++;
                sum += row.getMetric(IngestSegmentFirehoseFactoryTimelineTest.METRICS[0]).longValue();
            } 
        }
        Assert.assertEquals("count", expectedCount, count);
        Assert.assertEquals("sum", expectedSum, sum);
    }

    private static class TestCase {
        final File tmpDir;

        final Interval interval;

        final int expectedCount;

        final long expectedSum;

        final Set<DataSegment> segments;

        public TestCase(File tmpDir, Interval interval, int expectedCount, long expectedSum, Set<DataSegment> segments) {
            this.tmpDir = tmpDir;
            this.interval = interval;
            this.expectedCount = expectedCount;
            this.expectedSum = expectedSum;
            this.segments = segments;
        }

        @Override
        public String toString() {
            final List<SegmentId> segmentIds = new ArrayList<>();
            for (DataSegment segment : segments) {
                segmentIds.add(segment.getId());
            }
            return (((((((("TestCase{" + "interval=") + (interval)) + ", expectedCount=") + (expectedCount)) + ", expectedSum=") + (expectedSum)) + ", segments=") + segmentIds) + '}';
        }
    }

    private static class DataSegmentMaker {
        final Interval interval;

        final String version;

        final int partitionNum;

        final List<InputRow> rows;

        public DataSegmentMaker(Interval interval, String version, int partitionNum, List<InputRow> rows) {
            this.interval = interval;
            this.version = version;
            this.partitionNum = partitionNum;
            this.rows = rows;
        }

        public DataSegment make(File tmpDir) {
            final Map<String, Object> loadSpec = IngestSegmentFirehoseFactoryTimelineTest.persist(tmpDir, Iterables.toArray(rows, InputRow.class));
            return new DataSegment(IngestSegmentFirehoseFactoryTimelineTest.DATA_SOURCE, interval, version, loadSpec, Arrays.asList(IngestSegmentFirehoseFactoryTimelineTest.DIMENSIONS), Arrays.asList(IngestSegmentFirehoseFactoryTimelineTest.METRICS), new LinearShardSpec(partitionNum), (-1), 0L);
        }
    }
}

