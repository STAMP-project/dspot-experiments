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


import TaskStateStats.TaskStateStatsSummary;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.junit.Assert;
import org.junit.Test;


public class TaskStateStatsTest {
    private final ThreadLocalRandom rand = ThreadLocalRandom.current();

    /**
     * Tests that subtask stats are correctly collected.
     */
    @Test
    public void testHandInSubtasks() throws Exception {
        JobVertexID jobVertexId = new JobVertexID();
        SubtaskStateStats[] subtasks = new SubtaskStateStats[7];
        TaskStateStats taskStats = new TaskStateStats(jobVertexId, subtasks.length);
        Assert.assertEquals(jobVertexId, taskStats.getJobVertexId());
        Assert.assertEquals(subtasks.length, taskStats.getNumberOfSubtasks());
        Assert.assertEquals(0, taskStats.getNumberOfAcknowledgedSubtasks());
        Assert.assertNull(taskStats.getLatestAcknowledgedSubtaskStats());
        Assert.assertEquals((-1), taskStats.getLatestAckTimestamp());
        Assert.assertArrayEquals(subtasks, taskStats.getSubtaskStats());
        long stateSize = 0;
        long alignmentBuffered = 0;
        // Hand in some subtasks
        for (int i = 0; i < (subtasks.length); i++) {
            subtasks[i] = new SubtaskStateStats(i, rand.nextInt(128), rand.nextInt(128), rand.nextInt(128), rand.nextInt(128), rand.nextInt(128), rand.nextInt(128));
            stateSize += subtasks[i].getStateSize();
            alignmentBuffered += subtasks[i].getAlignmentBuffered();
            Assert.assertTrue(taskStats.reportSubtaskStats(subtasks[i]));
            Assert.assertEquals((i + 1), taskStats.getNumberOfAcknowledgedSubtasks());
            Assert.assertEquals(subtasks[i], taskStats.getLatestAcknowledgedSubtaskStats());
            Assert.assertEquals(subtasks[i].getAckTimestamp(), taskStats.getLatestAckTimestamp());
            int duration = rand.nextInt(128);
            Assert.assertEquals(duration, taskStats.getEndToEndDuration(((subtasks[i].getAckTimestamp()) - duration)));
            Assert.assertEquals(stateSize, taskStats.getStateSize());
            Assert.assertEquals(alignmentBuffered, taskStats.getAlignmentBuffered());
        }
        Assert.assertFalse(taskStats.reportSubtaskStats(new SubtaskStateStats(0, 0, 0, 0, 0, 0, 0)));
        // Test that all subtasks are taken into the account for the summary.
        // The correctness of the actual results is checked in the test of the
        // MinMaxAvgStats.
        TaskStateStats.TaskStateStatsSummary summary = taskStats.getSummaryStats();
        Assert.assertEquals(subtasks.length, summary.getStateSizeStats().getCount());
        Assert.assertEquals(subtasks.length, summary.getAckTimestampStats().getCount());
        Assert.assertEquals(subtasks.length, summary.getSyncCheckpointDurationStats().getCount());
        Assert.assertEquals(subtasks.length, summary.getAsyncCheckpointDurationStats().getCount());
        Assert.assertEquals(subtasks.length, summary.getAlignmentBufferedStats().getCount());
        Assert.assertEquals(subtasks.length, summary.getAlignmentDurationStats().getCount());
    }

    @Test
    public void testIsJavaSerializable() throws Exception {
        JobVertexID jobVertexId = new JobVertexID();
        SubtaskStateStats[] subtasks = new SubtaskStateStats[7];
        TaskStateStats taskStats = new TaskStateStats(jobVertexId, subtasks.length);
        long stateSize = 0;
        long alignmentBuffered = 0;
        for (int i = 0; i < (subtasks.length); i++) {
            subtasks[i] = new SubtaskStateStats(i, rand.nextInt(128), rand.nextInt(128), rand.nextInt(128), rand.nextInt(128), rand.nextInt(128), rand.nextInt(128));
            stateSize += subtasks[i].getStateSize();
            alignmentBuffered += subtasks[i].getAlignmentBuffered();
            taskStats.reportSubtaskStats(subtasks[i]);
        }
        TaskStateStats copy = CommonTestUtils.createCopySerializable(taskStats);
        Assert.assertEquals(stateSize, copy.getStateSize());
        Assert.assertEquals(alignmentBuffered, copy.getAlignmentBuffered());
        TaskStateStats.TaskStateStatsSummary summary = copy.getSummaryStats();
        Assert.assertEquals(subtasks.length, summary.getStateSizeStats().getCount());
        Assert.assertEquals(subtasks.length, summary.getAckTimestampStats().getCount());
        Assert.assertEquals(subtasks.length, summary.getSyncCheckpointDurationStats().getCount());
        Assert.assertEquals(subtasks.length, summary.getAsyncCheckpointDurationStats().getCount());
        Assert.assertEquals(subtasks.length, summary.getAlignmentBufferedStats().getCount());
        Assert.assertEquals(subtasks.length, summary.getAlignmentDurationStats().getCount());
    }
}

