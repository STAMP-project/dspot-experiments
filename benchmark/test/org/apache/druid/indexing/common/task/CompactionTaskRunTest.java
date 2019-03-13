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


import Granularities.DAY;
import Granularities.HOUR;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.apache.druid.client.coordinator.CoordinatorClient;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.ParseSpec;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.indexer.TaskStatus;
import org.apache.druid.indexing.common.RetryPolicyConfig;
import org.apache.druid.indexing.common.RetryPolicyFactory;
import org.apache.druid.indexing.common.SegmentLoaderFactory;
import org.apache.druid.indexing.common.TestUtils;
import org.apache.druid.indexing.common.stats.RowIngestionMetersFactory;
import org.apache.druid.indexing.common.task.CompactionTask.Builder;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.server.security.AuthTestUtils;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.NumberedShardSpec;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class CompactionTaskRunTest extends IngestionTestBase {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final ParseSpec DEFAULT_PARSE_SPEC = new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("ts", "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList("ts", "dim")), Collections.emptyList(), Collections.emptyList()), null, Arrays.asList("ts", "dim", "val"), false, 0);

    private RowIngestionMetersFactory rowIngestionMetersFactory;

    private CoordinatorClient coordinatorClient;

    private SegmentLoaderFactory segmentLoaderFactory;

    private ExecutorService exec;

    private static RetryPolicyFactory retryPolicyFactory = new RetryPolicyFactory(new RetryPolicyConfig());

    public CompactionTaskRunTest() {
        TestUtils testUtils = new TestUtils();
        rowIngestionMetersFactory = testUtils.getRowIngestionMetersFactory();
        coordinatorClient = new CoordinatorClient(null, null) {
            @Override
            public List<DataSegment> getDatabaseSegmentDataSourceSegments(String dataSource, List<Interval> intervals) {
                return getStorageCoordinator().getUsedSegmentsForIntervals(dataSource, intervals);
            }
        };
        segmentLoaderFactory = new SegmentLoaderFactory(getIndexIO(), getObjectMapper());
    }

    @Test
    public void testRun() throws Exception {
        runIndexTask();
        final Builder builder = new Builder(IngestionTestBase.DATA_SOURCE, getObjectMapper(), AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory, coordinatorClient, segmentLoaderFactory, CompactionTaskRunTest.retryPolicyFactory);
        final CompactionTask compactionTask = builder.interval(Intervals.of("2014-01-01/2014-01-02")).build();
        final Pair<TaskStatus, List<DataSegment>> resultPair = runTask(compactionTask);
        Assert.assertTrue(resultPair.lhs.isSuccess());
        final List<DataSegment> segments = resultPair.rhs;
        Assert.assertEquals(3, segments.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(Intervals.of("2014-01-01T0%d:00:00/2014-01-01T0%d:00:00", i, (i + 1)), segments.get(i).getInterval());
            Assert.assertEquals(new NumberedShardSpec(0, 0), segments.get(i).getShardSpec());
        }
    }

    @Test
    public void testRunCompactionTwiceWithoutKeepSegmentGranularity() throws Exception {
        runIndexTask();
        final Builder builder = new Builder(IngestionTestBase.DATA_SOURCE, getObjectMapper(), AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory, coordinatorClient, segmentLoaderFactory, CompactionTaskRunTest.retryPolicyFactory);
        final CompactionTask compactionTask1 = builder.interval(Intervals.of("2014-01-01/2014-01-02")).keepSegmentGranularity(false).build();
        Pair<TaskStatus, List<DataSegment>> resultPair = runTask(compactionTask1);
        Assert.assertTrue(resultPair.lhs.isSuccess());
        List<DataSegment> segments = resultPair.rhs;
        Assert.assertEquals(1, segments.size());
        Assert.assertEquals(Intervals.of("2014-01-01/2014-01-02"), segments.get(0).getInterval());
        Assert.assertEquals(new NumberedShardSpec(0, 0), segments.get(0).getShardSpec());
        final CompactionTask compactionTask2 = builder.interval(Intervals.of("2014-01-01/2014-01-02")).keepSegmentGranularity(false).build();
        resultPair = runTask(compactionTask2);
        Assert.assertTrue(resultPair.lhs.isSuccess());
        segments = resultPair.rhs;
        Assert.assertEquals(1, segments.size());
        Assert.assertEquals(Intervals.of("2014-01-01/2014-01-02"), segments.get(0).getInterval());
        Assert.assertEquals(new NumberedShardSpec(0, 0), segments.get(0).getShardSpec());
    }

    @Test
    public void testRunCompactionTwiceWithKeepSegmentGranularity() throws Exception {
        runIndexTask();
        final Builder builder = new Builder(IngestionTestBase.DATA_SOURCE, getObjectMapper(), AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory, coordinatorClient, segmentLoaderFactory, CompactionTaskRunTest.retryPolicyFactory);
        final CompactionTask compactionTask1 = builder.interval(Intervals.of("2014-01-01/2014-01-02")).keepSegmentGranularity(true).build();
        Pair<TaskStatus, List<DataSegment>> resultPair = runTask(compactionTask1);
        Assert.assertTrue(resultPair.lhs.isSuccess());
        List<DataSegment> segments = resultPair.rhs;
        Assert.assertEquals(3, segments.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(Intervals.of("2014-01-01T0%d:00:00/2014-01-01T0%d:00:00", i, (i + 1)), segments.get(i).getInterval());
            Assert.assertEquals(new NumberedShardSpec(0, 0), segments.get(i).getShardSpec());
        }
        final CompactionTask compactionTask2 = builder.interval(Intervals.of("2014-01-01/2014-01-02")).keepSegmentGranularity(true).build();
        resultPair = runTask(compactionTask2);
        Assert.assertTrue(resultPair.lhs.isSuccess());
        segments = resultPair.rhs;
        Assert.assertEquals(3, segments.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(Intervals.of("2014-01-01T0%d:00:00/2014-01-01T0%d:00:00", i, (i + 1)), segments.get(i).getInterval());
            Assert.assertEquals(new NumberedShardSpec(0, 0), segments.get(i).getShardSpec());
        }
    }

    @Test
    public void testWithSegmentGranularity() throws Exception {
        runIndexTask();
        final Builder builder = new Builder(IngestionTestBase.DATA_SOURCE, getObjectMapper(), AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, rowIngestionMetersFactory, coordinatorClient, segmentLoaderFactory, CompactionTaskRunTest.retryPolicyFactory);
        // day segmentGranularity
        final CompactionTask compactionTask1 = builder.interval(Intervals.of("2014-01-01/2014-01-02")).segmentGranularity(DAY).build();
        Pair<TaskStatus, List<DataSegment>> resultPair = runTask(compactionTask1);
        Assert.assertTrue(resultPair.lhs.isSuccess());
        List<DataSegment> segments = resultPair.rhs;
        Assert.assertEquals(1, segments.size());
        Assert.assertEquals(Intervals.of("2014-01-01/2014-01-02"), segments.get(0).getInterval());
        Assert.assertEquals(new NumberedShardSpec(0, 0), segments.get(0).getShardSpec());
        // hour segmentGranularity
        final CompactionTask compactionTask2 = builder.interval(Intervals.of("2014-01-01/2014-01-02")).segmentGranularity(HOUR).build();
        resultPair = runTask(compactionTask2);
        Assert.assertTrue(resultPair.lhs.isSuccess());
        segments = resultPair.rhs;
        Assert.assertEquals(3, segments.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(Intervals.of("2014-01-01T0%d:00:00/2014-01-01T0%d:00:00", i, (i + 1)), segments.get(i).getInterval());
            Assert.assertEquals(new NumberedShardSpec(0, 0), segments.get(i).getShardSpec());
        }
    }
}

