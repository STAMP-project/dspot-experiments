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


import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class CheckpointStatsHistoryTest {
    /**
     * Tests a checkpoint history with allowed size 0.
     */
    @Test
    public void testZeroMaxSizeHistory() throws Exception {
        CheckpointStatsHistory history = new CheckpointStatsHistory(0);
        history.addInProgressCheckpoint(createPendingCheckpointStats(0));
        Assert.assertFalse(history.replacePendingCheckpointById(createCompletedCheckpointStats(0)));
        CheckpointStatsHistory snapshot = history.createSnapshot();
        int counter = 0;
        for (AbstractCheckpointStats ignored : snapshot.getCheckpoints()) {
            counter++;
        }
        Assert.assertEquals(0, counter);
        Assert.assertNotNull(snapshot.getCheckpointById(0));
    }

    /**
     * Tests a checkpoint history with allowed size 1.
     */
    @Test
    public void testSizeOneHistory() throws Exception {
        CheckpointStatsHistory history = new CheckpointStatsHistory(1);
        history.addInProgressCheckpoint(createPendingCheckpointStats(0));
        history.addInProgressCheckpoint(createPendingCheckpointStats(1));
        Assert.assertFalse(history.replacePendingCheckpointById(createCompletedCheckpointStats(0)));
        Assert.assertTrue(history.replacePendingCheckpointById(createCompletedCheckpointStats(1)));
        CheckpointStatsHistory snapshot = history.createSnapshot();
        for (AbstractCheckpointStats stats : snapshot.getCheckpoints()) {
            Assert.assertEquals(1, stats.getCheckpointId());
            Assert.assertTrue(stats.getStatus().isCompleted());
        }
    }

    /**
     * Tests the checkpoint history with multiple checkpoints.
     */
    @Test
    public void testCheckpointHistory() throws Exception {
        CheckpointStatsHistory history = new CheckpointStatsHistory(3);
        history.addInProgressCheckpoint(createPendingCheckpointStats(0));
        CheckpointStatsHistory snapshot = history.createSnapshot();
        for (AbstractCheckpointStats stats : snapshot.getCheckpoints()) {
            Assert.assertEquals(0, stats.getCheckpointId());
            Assert.assertTrue(stats.getStatus().isInProgress());
        }
        history.addInProgressCheckpoint(createPendingCheckpointStats(1));
        history.addInProgressCheckpoint(createPendingCheckpointStats(2));
        history.addInProgressCheckpoint(createPendingCheckpointStats(3));
        snapshot = history.createSnapshot();
        // Check in progress stats.
        Iterator<AbstractCheckpointStats> it = snapshot.getCheckpoints().iterator();
        for (int i = 3; i > 0; i--) {
            Assert.assertTrue(it.hasNext());
            AbstractCheckpointStats stats = it.next();
            Assert.assertEquals(i, stats.getCheckpointId());
            Assert.assertTrue(stats.getStatus().isInProgress());
        }
        Assert.assertFalse(it.hasNext());
        // Update checkpoints
        history.replacePendingCheckpointById(createFailedCheckpointStats(1));
        history.replacePendingCheckpointById(createCompletedCheckpointStats(3));
        history.replacePendingCheckpointById(createFailedCheckpointStats(2));
        snapshot = history.createSnapshot();
        it = snapshot.getCheckpoints().iterator();
        Assert.assertTrue(it.hasNext());
        AbstractCheckpointStats stats = it.next();
        Assert.assertEquals(3, stats.getCheckpointId());
        Assert.assertNotNull(snapshot.getCheckpointById(3));
        Assert.assertTrue(stats.getStatus().isCompleted());
        Assert.assertTrue(snapshot.getCheckpointById(3).getStatus().isCompleted());
        Assert.assertTrue(it.hasNext());
        stats = it.next();
        Assert.assertEquals(2, stats.getCheckpointId());
        Assert.assertNotNull(snapshot.getCheckpointById(2));
        Assert.assertTrue(stats.getStatus().isFailed());
        Assert.assertTrue(snapshot.getCheckpointById(2).getStatus().isFailed());
        Assert.assertTrue(it.hasNext());
        stats = it.next();
        Assert.assertEquals(1, stats.getCheckpointId());
        Assert.assertNotNull(snapshot.getCheckpointById(1));
        Assert.assertTrue(stats.getStatus().isFailed());
        Assert.assertTrue(snapshot.getCheckpointById(1).getStatus().isFailed());
        Assert.assertFalse(it.hasNext());
    }

    /**
     * Tests that a snapshot cannot be modified or copied.
     */
    @Test
    public void testModifySnapshot() throws Exception {
        CheckpointStatsHistory history = new CheckpointStatsHistory(3);
        history.addInProgressCheckpoint(createPendingCheckpointStats(0));
        history.addInProgressCheckpoint(createPendingCheckpointStats(1));
        history.addInProgressCheckpoint(createPendingCheckpointStats(2));
        CheckpointStatsHistory snapshot = history.createSnapshot();
        try {
            snapshot.addInProgressCheckpoint(createPendingCheckpointStats(4));
            Assert.fail("Did not throw expected Exception");
        } catch (UnsupportedOperationException ignored) {
        }
        try {
            snapshot.replacePendingCheckpointById(createCompletedCheckpointStats(2));
            Assert.fail("Did not throw expected Exception");
        } catch (UnsupportedOperationException ignored) {
        }
        try {
            snapshot.createSnapshot();
            Assert.fail("Did not throw expected Exception");
        } catch (UnsupportedOperationException ignored) {
        }
    }
}

