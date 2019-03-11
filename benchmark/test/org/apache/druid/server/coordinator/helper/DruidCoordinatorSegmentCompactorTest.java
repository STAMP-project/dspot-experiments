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
package org.apache.druid.server.coordinator.helper;


import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.druid.client.indexing.ClientCompactQueryTuningConfig;
import org.apache.druid.client.indexing.IndexingServiceClient;
import org.apache.druid.client.indexing.NoopIndexingServiceClient;
import org.apache.druid.indexer.TaskStatusPlus;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.VersionedIntervalTimeline;
import org.apache.druid.timeline.partition.NoneShardSpec;
import org.joda.time.Interval;
import org.junit.Test;


public class DruidCoordinatorSegmentCompactorTest {
    private static final String DATA_SOURCE_PREFIX = "dataSource_";

    private final IndexingServiceClient indexingServiceClient = new NoopIndexingServiceClient() {
        private int compactVersionSuffix = 0;

        private int idSuffix = 0;

        @Override
        public String compactSegments(List<DataSegment> segments, boolean keepSegmentGranularity, @Nullable
        Long targetCompactionSizeBytes, int compactionTaskPriority, ClientCompactQueryTuningConfig tuningConfig, Map<String, Object> context) {
            Preconditions.checkArgument(((segments.size()) > 1));
            Collections.sort(segments);
            Interval compactInterval = new Interval(segments.get(0).getInterval().getStart(), segments.get(((segments.size()) - 1)).getInterval().getEnd());
            DataSegment compactSegment = new DataSegment(segments.get(0).getDataSource(), compactInterval, ("newVersion_" + ((compactVersionSuffix)++)), null, segments.get(0).getDimensions(), segments.get(0).getMetrics(), NoneShardSpec.instance(), 1, segments.stream().mapToLong(DataSegment::getSize).sum());
            final VersionedIntervalTimeline<String, DataSegment> timeline = dataSources.get(segments.get(0).getDataSource());
            segments.forEach(( segment) -> timeline.remove(segment.getInterval(), segment.getVersion(), segment.getShardSpec().createChunk(segment)));
            timeline.add(compactInterval, compactSegment.getVersion(), compactSegment.getShardSpec().createChunk(compactSegment));
            return "task_" + ((idSuffix)++);
        }

        @Override
        public List<TaskStatusPlus> getRunningTasks() {
            return Collections.emptyList();
        }

        @Override
        public List<TaskStatusPlus> getPendingTasks() {
            return Collections.emptyList();
        }

        @Override
        public List<TaskStatusPlus> getWaitingTasks() {
            return Collections.emptyList();
        }

        @Override
        public int getTotalWorkerCapacity() {
            return 10;
        }
    };

    private Map<String, VersionedIntervalTimeline<String, DataSegment>> dataSources;

    @Test
    public void testRunWithoutKeepSegmentGranularity() {
        final boolean keepSegmentGranularity = false;
        final DruidCoordinatorSegmentCompactor compactor = new DruidCoordinatorSegmentCompactor(indexingServiceClient);
        final Supplier<String> expectedVersionSupplier = new Supplier<String>() {
            private int i = 0;

            @Override
            public String get() {
                return "newVersion_" + ((i)++);
            }
        };
        int expectedCompactTaskCount = 1;
        int expectedRemainingSegments = 180;
        // compact for 2017-01-08T12:00:00.000Z/2017-01-09T12:00:00.000Z
        assertCompactSegments(compactor, keepSegmentGranularity, Intervals.of("2017-01-%02dT12:00:00/2017-01-%02dT12:00:00", 8, 9), expectedRemainingSegments, expectedCompactTaskCount, expectedVersionSupplier);
        // compact for 2017-01-08T00:00:00.000Z/2017-01-08T12:00:00.000Z
        expectedRemainingSegments -= 20;
        assertCompactSegments(compactor, keepSegmentGranularity, Intervals.of("2017-01-%02dT00:00:00/2017-01-%02dT12:00:00", 8, 8), expectedRemainingSegments, expectedCompactTaskCount, expectedVersionSupplier);
        for (int endDay = 5; endDay > 1; endDay -= 1) {
            expectedRemainingSegments -= 40;
            assertCompactSegments(compactor, keepSegmentGranularity, Intervals.of("2017-01-%02dT00:00:00/2017-01-%02dT00:00:00", (endDay - 1), endDay), expectedRemainingSegments, expectedCompactTaskCount, expectedVersionSupplier);
        }
        assertLastSegmentNotCompacted(compactor, keepSegmentGranularity);
    }

    @Test
    public void testRunWithKeepSegmentGranularity() {
        final boolean keepSegmentGranularity = true;
        final DruidCoordinatorSegmentCompactor compactor = new DruidCoordinatorSegmentCompactor(indexingServiceClient);
        final Supplier<String> expectedVersionSupplier = new Supplier<String>() {
            private int i = 0;

            @Override
            public String get() {
                return "newVersion_" + ((i)++);
            }
        };
        int expectedCompactTaskCount = 1;
        int expectedRemainingSegments = 200;
        // compact for 2017-01-08T12:00:00.000Z/2017-01-09T12:00:00.000Z
        assertCompactSegments(compactor, keepSegmentGranularity, Intervals.of("2017-01-%02dT00:00:00/2017-01-%02dT12:00:00", 9, 9), expectedRemainingSegments, expectedCompactTaskCount, expectedVersionSupplier);
        expectedRemainingSegments -= 20;
        assertCompactSegments(compactor, keepSegmentGranularity, Intervals.of("2017-01-%02dT12:00:00/2017-01-%02dT00:00:00", 8, 9), expectedRemainingSegments, expectedCompactTaskCount, expectedVersionSupplier);
        // compact for 2017-01-07T12:00:00.000Z/2017-01-08T12:00:00.000Z
        expectedRemainingSegments -= 20;
        assertCompactSegments(compactor, keepSegmentGranularity, Intervals.of("2017-01-%02dT00:00:00/2017-01-%02dT12:00:00", 8, 8), expectedRemainingSegments, expectedCompactTaskCount, expectedVersionSupplier);
        expectedRemainingSegments -= 20;
        assertCompactSegments(compactor, keepSegmentGranularity, Intervals.of("2017-01-%02dT12:00:00/2017-01-%02dT00:00:00", 4, 5), expectedRemainingSegments, expectedCompactTaskCount, expectedVersionSupplier);
        for (int endDay = 4; endDay > 1; endDay -= 1) {
            expectedRemainingSegments -= 20;
            assertCompactSegments(compactor, keepSegmentGranularity, Intervals.of("2017-01-%02dT00:00:00/2017-01-%02dT12:00:00", endDay, endDay), expectedRemainingSegments, expectedCompactTaskCount, expectedVersionSupplier);
            expectedRemainingSegments -= 20;
            assertCompactSegments(compactor, keepSegmentGranularity, Intervals.of("2017-01-%02dT12:00:00/2017-01-%02dT00:00:00", (endDay - 1), endDay), expectedRemainingSegments, expectedCompactTaskCount, expectedVersionSupplier);
        }
        assertLastSegmentNotCompacted(compactor, keepSegmentGranularity);
    }
}

