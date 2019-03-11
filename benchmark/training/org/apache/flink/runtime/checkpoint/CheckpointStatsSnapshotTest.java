/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.checkpoint;


import CheckpointRetentionPolicy.NEVER_RETAIN_AFTER_TERMINATION;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class CheckpointStatsSnapshotTest {
    /**
     * Tests that the snapshot is actually serializable.
     */
    @Test
    public void testIsJavaSerializable() throws Exception {
        CheckpointStatsCounts counts = new CheckpointStatsCounts();
        counts.incrementInProgressCheckpoints();
        counts.incrementInProgressCheckpoints();
        counts.incrementInProgressCheckpoints();
        counts.incrementCompletedCheckpoints();
        counts.incrementFailedCheckpoints();
        counts.incrementRestoredCheckpoints();
        CompletedCheckpointStatsSummary summary = new CompletedCheckpointStatsSummary();
        summary.updateSummary(createCompletedCheckpointsStats(12398, 9919, 12));
        summary.updateSummary(createCompletedCheckpointsStats(2221, 3333, 9122));
        CheckpointStatsHistory history = new CheckpointStatsHistory(1);
        RestoredCheckpointStats restored = new RestoredCheckpointStats(1, CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION), 99119, null);
        CheckpointStatsSnapshot snapshot = new CheckpointStatsSnapshot(counts, summary, history, restored);
        CheckpointStatsSnapshot copy = CommonTestUtils.createCopySerializable(snapshot);
        Assert.assertEquals(counts.getNumberOfCompletedCheckpoints(), copy.getCounts().getNumberOfCompletedCheckpoints());
        Assert.assertEquals(counts.getNumberOfFailedCheckpoints(), copy.getCounts().getNumberOfFailedCheckpoints());
        Assert.assertEquals(counts.getNumberOfInProgressCheckpoints(), copy.getCounts().getNumberOfInProgressCheckpoints());
        Assert.assertEquals(counts.getNumberOfRestoredCheckpoints(), copy.getCounts().getNumberOfRestoredCheckpoints());
        Assert.assertEquals(counts.getTotalNumberOfCheckpoints(), copy.getCounts().getTotalNumberOfCheckpoints());
        Assert.assertEquals(summary.getStateSizeStats().getSum(), copy.getSummaryStats().getStateSizeStats().getSum());
        Assert.assertEquals(summary.getEndToEndDurationStats().getSum(), copy.getSummaryStats().getEndToEndDurationStats().getSum());
        Assert.assertEquals(summary.getAlignmentBufferedStats().getSum(), copy.getSummaryStats().getAlignmentBufferedStats().getSum());
        Assert.assertEquals(restored.getCheckpointId(), copy.getLatestRestoredCheckpoint().getCheckpointId());
    }
}

