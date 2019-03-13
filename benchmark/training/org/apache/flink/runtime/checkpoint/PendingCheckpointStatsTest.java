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
import CheckpointStatsStatus.COMPLETED;
import CheckpointStatsStatus.FAILED;
import CheckpointStatsStatus.IN_PROGRESS;
import CheckpointStatsTracker.PendingCheckpointStatsCallback;
import CompletedCheckpointStats.DiscardCallback;
import java.util.HashMap;
import junit.framework.TestCase;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class PendingCheckpointStatsTest {
    /**
     * Tests reporting of subtask stats.
     */
    @Test
    public void testReportSubtaskStats() throws Exception {
        long checkpointId = (Integer.MAX_VALUE) + 1222L;
        long triggerTimestamp = (Integer.MAX_VALUE) - 1239L;
        CheckpointProperties props = CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION);
        TaskStateStats task1 = new TaskStateStats(new JobVertexID(), 3);
        TaskStateStats task2 = new TaskStateStats(new JobVertexID(), 4);
        int totalSubtaskCount = (task1.getNumberOfSubtasks()) + (task2.getNumberOfSubtasks());
        HashMap<JobVertexID, TaskStateStats> taskStats = new HashMap<>();
        taskStats.put(task1.getJobVertexId(), task1);
        taskStats.put(task2.getJobVertexId(), task2);
        CheckpointStatsTracker.PendingCheckpointStatsCallback callback = Mockito.mock(PendingCheckpointStatsCallback.class);
        PendingCheckpointStats pending = new PendingCheckpointStats(checkpointId, triggerTimestamp, props, totalSubtaskCount, taskStats, callback);
        // Check initial state
        Assert.assertEquals(checkpointId, pending.getCheckpointId());
        Assert.assertEquals(triggerTimestamp, pending.getTriggerTimestamp());
        Assert.assertEquals(props, pending.getProperties());
        Assert.assertEquals(IN_PROGRESS, pending.getStatus());
        Assert.assertEquals(0, pending.getNumberOfAcknowledgedSubtasks());
        Assert.assertEquals(0, pending.getStateSize());
        Assert.assertEquals(totalSubtaskCount, pending.getNumberOfSubtasks());
        Assert.assertNull(pending.getLatestAcknowledgedSubtaskStats());
        Assert.assertEquals((-1), pending.getLatestAckTimestamp());
        Assert.assertEquals((-1), pending.getEndToEndDuration());
        Assert.assertEquals(task1, pending.getTaskStateStats(task1.getJobVertexId()));
        Assert.assertEquals(task2, pending.getTaskStateStats(task2.getJobVertexId()));
        Assert.assertNull(pending.getTaskStateStats(new JobVertexID()));
        // Report subtasks and check getters
        TestCase.assertFalse(pending.reportSubtaskStats(new JobVertexID(), createSubtaskStats(0)));
        long stateSize = 0;
        long alignmentBuffered = 0;
        // Report 1st task
        for (int i = 0; i < (task1.getNumberOfSubtasks()); i++) {
            SubtaskStateStats subtask = createSubtaskStats(i);
            stateSize += subtask.getStateSize();
            alignmentBuffered += subtask.getAlignmentBuffered();
            pending.reportSubtaskStats(task1.getJobVertexId(), subtask);
            Assert.assertEquals(subtask, pending.getLatestAcknowledgedSubtaskStats());
            Assert.assertEquals(subtask.getAckTimestamp(), pending.getLatestAckTimestamp());
            Assert.assertEquals(((subtask.getAckTimestamp()) - triggerTimestamp), pending.getEndToEndDuration());
            Assert.assertEquals(stateSize, pending.getStateSize());
            Assert.assertEquals(alignmentBuffered, pending.getAlignmentBuffered());
        }
        // Don't allow overwrite
        TestCase.assertFalse(pending.reportSubtaskStats(task1.getJobVertexId(), task1.getSubtaskStats()[0]));
        // Report 2nd task
        for (int i = 0; i < (task2.getNumberOfSubtasks()); i++) {
            SubtaskStateStats subtask = createSubtaskStats(i);
            stateSize += subtask.getStateSize();
            alignmentBuffered += subtask.getAlignmentBuffered();
            pending.reportSubtaskStats(task2.getJobVertexId(), subtask);
            Assert.assertEquals(subtask, pending.getLatestAcknowledgedSubtaskStats());
            Assert.assertEquals(subtask.getAckTimestamp(), pending.getLatestAckTimestamp());
            Assert.assertEquals(((subtask.getAckTimestamp()) - triggerTimestamp), pending.getEndToEndDuration());
            Assert.assertEquals(stateSize, pending.getStateSize());
            Assert.assertEquals(alignmentBuffered, pending.getAlignmentBuffered());
        }
        Assert.assertEquals(task1.getNumberOfSubtasks(), task1.getNumberOfAcknowledgedSubtasks());
        Assert.assertEquals(task2.getNumberOfSubtasks(), task2.getNumberOfAcknowledgedSubtasks());
    }

    /**
     * Test reporting of a completed checkpoint.
     */
    @Test
    public void testReportCompletedCheckpoint() throws Exception {
        TaskStateStats task1 = new TaskStateStats(new JobVertexID(), 3);
        TaskStateStats task2 = new TaskStateStats(new JobVertexID(), 4);
        HashMap<JobVertexID, TaskStateStats> taskStats = new HashMap<>();
        taskStats.put(task1.getJobVertexId(), task1);
        taskStats.put(task2.getJobVertexId(), task2);
        CheckpointStatsTracker.PendingCheckpointStatsCallback callback = Mockito.mock(PendingCheckpointStatsCallback.class);
        PendingCheckpointStats pending = new PendingCheckpointStats(0, 1, CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION), ((task1.getNumberOfSubtasks()) + (task2.getNumberOfSubtasks())), taskStats, callback);
        // Report subtasks
        for (int i = 0; i < (task1.getNumberOfSubtasks()); i++) {
            pending.reportSubtaskStats(task1.getJobVertexId(), createSubtaskStats(i));
        }
        for (int i = 0; i < (task2.getNumberOfSubtasks()); i++) {
            pending.reportSubtaskStats(task2.getJobVertexId(), createSubtaskStats(i));
        }
        // Report completed
        String externalPath = "asdjkasdjkasd";
        CompletedCheckpointStats.DiscardCallback discardCallback = pending.reportCompletedCheckpoint(externalPath);
        ArgumentCaptor<CompletedCheckpointStats> args = ArgumentCaptor.forClass(CompletedCheckpointStats.class);
        Mockito.verify(callback).reportCompletedCheckpoint(args.capture());
        CompletedCheckpointStats completed = args.getValue();
        Assert.assertNotNull(completed);
        Assert.assertEquals(COMPLETED, completed.getStatus());
        TestCase.assertFalse(completed.isDiscarded());
        discardCallback.notifyDiscardedCheckpoint();
        Assert.assertTrue(completed.isDiscarded());
        Assert.assertEquals(externalPath, completed.getExternalPath());
        Assert.assertEquals(pending.getCheckpointId(), completed.getCheckpointId());
        Assert.assertEquals(pending.getNumberOfAcknowledgedSubtasks(), completed.getNumberOfAcknowledgedSubtasks());
        Assert.assertEquals(pending.getLatestAcknowledgedSubtaskStats(), completed.getLatestAcknowledgedSubtaskStats());
        Assert.assertEquals(pending.getLatestAckTimestamp(), completed.getLatestAckTimestamp());
        Assert.assertEquals(pending.getEndToEndDuration(), completed.getEndToEndDuration());
        Assert.assertEquals(pending.getStateSize(), completed.getStateSize());
        Assert.assertEquals(pending.getAlignmentBuffered(), completed.getAlignmentBuffered());
        Assert.assertEquals(task1, completed.getTaskStateStats(task1.getJobVertexId()));
        Assert.assertEquals(task2, completed.getTaskStateStats(task2.getJobVertexId()));
    }

    /**
     * Test reporting of a failed checkpoint.
     */
    @Test
    public void testReportFailedCheckpoint() throws Exception {
        TaskStateStats task1 = new TaskStateStats(new JobVertexID(), 3);
        TaskStateStats task2 = new TaskStateStats(new JobVertexID(), 4);
        HashMap<JobVertexID, TaskStateStats> taskStats = new HashMap<>();
        taskStats.put(task1.getJobVertexId(), task1);
        taskStats.put(task2.getJobVertexId(), task2);
        CheckpointStatsTracker.PendingCheckpointStatsCallback callback = Mockito.mock(PendingCheckpointStatsCallback.class);
        long triggerTimestamp = 123123;
        PendingCheckpointStats pending = new PendingCheckpointStats(0, triggerTimestamp, CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION), ((task1.getNumberOfSubtasks()) + (task2.getNumberOfSubtasks())), taskStats, callback);
        // Report subtasks
        for (int i = 0; i < (task1.getNumberOfSubtasks()); i++) {
            pending.reportSubtaskStats(task1.getJobVertexId(), createSubtaskStats(i));
        }
        for (int i = 0; i < (task2.getNumberOfSubtasks()); i++) {
            pending.reportSubtaskStats(task2.getJobVertexId(), createSubtaskStats(i));
        }
        // Report failed
        Exception cause = new Exception("test exception");
        long failureTimestamp = 112211137;
        pending.reportFailedCheckpoint(failureTimestamp, cause);
        ArgumentCaptor<FailedCheckpointStats> args = ArgumentCaptor.forClass(FailedCheckpointStats.class);
        Mockito.verify(callback).reportFailedCheckpoint(args.capture());
        FailedCheckpointStats failed = args.getValue();
        Assert.assertNotNull(failed);
        Assert.assertEquals(FAILED, failed.getStatus());
        Assert.assertEquals(failureTimestamp, failed.getFailureTimestamp());
        Assert.assertEquals(cause.getMessage(), failed.getFailureMessage());
        Assert.assertEquals(pending.getCheckpointId(), failed.getCheckpointId());
        Assert.assertEquals(pending.getNumberOfAcknowledgedSubtasks(), failed.getNumberOfAcknowledgedSubtasks());
        Assert.assertEquals(pending.getLatestAcknowledgedSubtaskStats(), failed.getLatestAcknowledgedSubtaskStats());
        Assert.assertEquals(pending.getLatestAckTimestamp(), failed.getLatestAckTimestamp());
        Assert.assertEquals((failureTimestamp - triggerTimestamp), failed.getEndToEndDuration());
        Assert.assertEquals(pending.getStateSize(), failed.getStateSize());
        Assert.assertEquals(pending.getAlignmentBuffered(), failed.getAlignmentBuffered());
        Assert.assertEquals(task1, failed.getTaskStateStats(task1.getJobVertexId()));
        Assert.assertEquals(task2, failed.getTaskStateStats(task2.getJobVertexId()));
    }

    @Test
    public void testIsJavaSerializable() throws Exception {
        TaskStateStats task1 = new TaskStateStats(new JobVertexID(), 3);
        TaskStateStats task2 = new TaskStateStats(new JobVertexID(), 4);
        HashMap<JobVertexID, TaskStateStats> taskStats = new HashMap<>();
        taskStats.put(task1.getJobVertexId(), task1);
        taskStats.put(task2.getJobVertexId(), task2);
        PendingCheckpointStats pending = new PendingCheckpointStats(123123123L, 10123L, CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION), 1337, taskStats, Mockito.mock(PendingCheckpointStatsCallback.class));
        PendingCheckpointStats copy = CommonTestUtils.createCopySerializable(pending);
        Assert.assertEquals(pending.getCheckpointId(), copy.getCheckpointId());
        Assert.assertEquals(pending.getTriggerTimestamp(), copy.getTriggerTimestamp());
        Assert.assertEquals(pending.getProperties(), copy.getProperties());
        Assert.assertEquals(pending.getNumberOfSubtasks(), copy.getNumberOfSubtasks());
        Assert.assertEquals(pending.getNumberOfAcknowledgedSubtasks(), copy.getNumberOfAcknowledgedSubtasks());
        Assert.assertEquals(pending.getEndToEndDuration(), copy.getEndToEndDuration());
        Assert.assertEquals(pending.getStateSize(), copy.getStateSize());
        Assert.assertEquals(pending.getLatestAcknowledgedSubtaskStats(), copy.getLatestAcknowledgedSubtaskStats());
        Assert.assertEquals(pending.getStatus(), copy.getStatus());
    }
}

