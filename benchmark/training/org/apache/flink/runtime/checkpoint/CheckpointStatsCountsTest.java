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


import org.junit.Assert;
import org.junit.Test;


/**
 * Test checkpoint statistics counters.
 */
public class CheckpointStatsCountsTest {
    /**
     * Tests that counts are reported correctly.
     */
    @Test
    public void testCounts() {
        CheckpointStatsCounts counts = new CheckpointStatsCounts();
        Assert.assertEquals(0, counts.getNumberOfRestoredCheckpoints());
        Assert.assertEquals(0, counts.getTotalNumberOfCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfInProgressCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfCompletedCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfFailedCheckpoints());
        counts.incrementRestoredCheckpoints();
        Assert.assertEquals(1, counts.getNumberOfRestoredCheckpoints());
        Assert.assertEquals(0, counts.getTotalNumberOfCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfInProgressCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfCompletedCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfFailedCheckpoints());
        // 1st checkpoint
        counts.incrementInProgressCheckpoints();
        Assert.assertEquals(1, counts.getNumberOfRestoredCheckpoints());
        Assert.assertEquals(1, counts.getTotalNumberOfCheckpoints());
        Assert.assertEquals(1, counts.getNumberOfInProgressCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfCompletedCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfFailedCheckpoints());
        counts.incrementCompletedCheckpoints();
        Assert.assertEquals(1, counts.getNumberOfRestoredCheckpoints());
        Assert.assertEquals(1, counts.getTotalNumberOfCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfInProgressCheckpoints());
        Assert.assertEquals(1, counts.getNumberOfCompletedCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfFailedCheckpoints());
        // 2nd checkpoint
        counts.incrementInProgressCheckpoints();
        Assert.assertEquals(1, counts.getNumberOfRestoredCheckpoints());
        Assert.assertEquals(2, counts.getTotalNumberOfCheckpoints());
        Assert.assertEquals(1, counts.getNumberOfInProgressCheckpoints());
        Assert.assertEquals(1, counts.getNumberOfCompletedCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfFailedCheckpoints());
        counts.incrementFailedCheckpoints();
        Assert.assertEquals(1, counts.getNumberOfRestoredCheckpoints());
        Assert.assertEquals(2, counts.getTotalNumberOfCheckpoints());
        Assert.assertEquals(0, counts.getNumberOfInProgressCheckpoints());
        Assert.assertEquals(1, counts.getNumberOfCompletedCheckpoints());
        Assert.assertEquals(1, counts.getNumberOfFailedCheckpoints());
    }

    /**
     * Tests that increment the completed or failed number of checkpoints without
     * incrementing the in progress checkpoints before throws an Exception.
     */
    @Test
    public void testCompleteOrFailWithoutInProgressCheckpoint() {
        CheckpointStatsCounts counts = new CheckpointStatsCounts();
        counts.incrementCompletedCheckpoints();
        Assert.assertTrue("Number of checkpoints in progress should never be negative", ((counts.getNumberOfInProgressCheckpoints()) >= 0));
        counts.incrementFailedCheckpoints();
        Assert.assertTrue("Number of checkpoints in progress should never be negative", ((counts.getNumberOfInProgressCheckpoints()) >= 0));
    }

    /**
     * Tests that that taking snapshots of the state are independent from the
     * parent.
     */
    @Test
    public void testCreateSnapshot() {
        CheckpointStatsCounts counts = new CheckpointStatsCounts();
        counts.incrementRestoredCheckpoints();
        counts.incrementRestoredCheckpoints();
        counts.incrementRestoredCheckpoints();
        counts.incrementInProgressCheckpoints();
        counts.incrementCompletedCheckpoints();
        counts.incrementInProgressCheckpoints();
        counts.incrementCompletedCheckpoints();
        counts.incrementInProgressCheckpoints();
        counts.incrementCompletedCheckpoints();
        counts.incrementInProgressCheckpoints();
        counts.incrementCompletedCheckpoints();
        counts.incrementInProgressCheckpoints();
        counts.incrementFailedCheckpoints();
        long restored = counts.getNumberOfRestoredCheckpoints();
        long total = counts.getTotalNumberOfCheckpoints();
        long inProgress = counts.getNumberOfInProgressCheckpoints();
        long completed = counts.getNumberOfCompletedCheckpoints();
        long failed = counts.getNumberOfFailedCheckpoints();
        CheckpointStatsCounts snapshot = counts.createSnapshot();
        Assert.assertEquals(restored, snapshot.getNumberOfRestoredCheckpoints());
        Assert.assertEquals(total, snapshot.getTotalNumberOfCheckpoints());
        Assert.assertEquals(inProgress, snapshot.getNumberOfInProgressCheckpoints());
        Assert.assertEquals(completed, snapshot.getNumberOfCompletedCheckpoints());
        Assert.assertEquals(failed, snapshot.getNumberOfFailedCheckpoints());
        // Update the original
        counts.incrementRestoredCheckpoints();
        counts.incrementRestoredCheckpoints();
        counts.incrementInProgressCheckpoints();
        counts.incrementCompletedCheckpoints();
        counts.incrementInProgressCheckpoints();
        counts.incrementFailedCheckpoints();
        Assert.assertEquals(restored, snapshot.getNumberOfRestoredCheckpoints());
        Assert.assertEquals(total, snapshot.getTotalNumberOfCheckpoints());
        Assert.assertEquals(inProgress, snapshot.getNumberOfInProgressCheckpoints());
        Assert.assertEquals(completed, snapshot.getNumberOfCompletedCheckpoints());
        Assert.assertEquals(failed, snapshot.getNumberOfFailedCheckpoints());
    }
}

