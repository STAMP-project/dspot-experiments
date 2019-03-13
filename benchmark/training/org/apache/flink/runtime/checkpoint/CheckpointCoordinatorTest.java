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


import CheckpointDeclineReason.PERIODIC_SCHEDULER_SHUTDOWN;
import CheckpointRetentionPolicy.NEVER_RETAIN_AFTER_TERMINATION;
import CheckpointRetentionPolicy.RETAIN_ON_FAILURE;
import ExecutionState.RUNNING;
import JobStatus.FINISHED;
import JobStatus.SUSPENDED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.concurrent.Executors;
import org.apache.flink.runtime.execution.ExecutionState;
import org.apache.flink.runtime.executiongraph.Execution;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.executiongraph.ExecutionJobVertex;
import org.apache.flink.runtime.executiongraph.ExecutionVertex;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.messages.checkpoint.AcknowledgeCheckpoint;
import org.apache.flink.runtime.state.IncrementalRemoteKeyedStateHandle;
import org.apache.flink.runtime.state.KeyGroupRange;
import org.apache.flink.runtime.state.KeyGroupsStateHandle;
import org.apache.flink.runtime.state.KeyedStateHandle;
import org.apache.flink.runtime.state.PlaceholderStreamStateHandle;
import org.apache.flink.runtime.state.SharedStateRegistry;
import org.apache.flink.runtime.state.StateHandleID;
import org.apache.flink.runtime.state.StreamStateHandle;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.runtime.state.testutils.TestCompletedCheckpointStorageLocation;
import org.apache.flink.runtime.testutils.RecoverableCompletedCheckpointStore;
import org.apache.flink.shaded.guava18.com.google.common.collect.Iterables;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;

import static CheckpointRetentionPolicy.NEVER_RETAIN_AFTER_TERMINATION;
import static CheckpointRetentionPolicy.RETAIN_ON_FAILURE;


/**
 * Tests for the checkpoint coordinator.
 */
public class CheckpointCoordinatorTest extends TestLogger {
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    @Test
    public void testCheckpointAbortsIfTriggerTasksAreNotExecuted() {
        try {
            final JobID jid = new JobID();
            final long timestamp = System.currentTimeMillis();
            // create some mock Execution vertices that receive the checkpoint trigger messages
            ExecutionVertex triggerVertex1 = Mockito.mock(ExecutionVertex.class);
            ExecutionVertex triggerVertex2 = Mockito.mock(ExecutionVertex.class);
            // create some mock Execution vertices that need to ack the checkpoint
            final ExecutionAttemptID ackAttemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID2 = new ExecutionAttemptID();
            ExecutionVertex ackVertex1 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID1);
            ExecutionVertex ackVertex2 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID2);
            // set up the coordinator and validate the initial state
            CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ triggerVertex1, triggerVertex2 }, new ExecutionVertex[]{ ackVertex1, ackVertex2 }, new ExecutionVertex[]{  }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            // nothing should be happening
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            // trigger the first checkpoint. this should not succeed
            Assert.assertFalse(coord.triggerCheckpoint(timestamp, false));
            // still, nothing should be happening
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCheckpointAbortsIfTriggerTasksAreFinished() {
        try {
            final JobID jid = new JobID();
            final long timestamp = System.currentTimeMillis();
            // create some mock Execution vertices that receive the checkpoint trigger messages
            final ExecutionAttemptID triggerAttemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID triggerAttemptID2 = new ExecutionAttemptID();
            ExecutionVertex triggerVertex1 = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID1);
            JobVertexID jobVertexID2 = new JobVertexID();
            ExecutionVertex triggerVertex2 = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID2, jobVertexID2, Collections.singletonList(OperatorID.fromJobVertexID(jobVertexID2)), 1, 1, ExecutionState.FINISHED);
            // create some mock Execution vertices that need to ack the checkpoint
            final ExecutionAttemptID ackAttemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID2 = new ExecutionAttemptID();
            ExecutionVertex ackVertex1 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID1);
            ExecutionVertex ackVertex2 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID2);
            // set up the coordinator and validate the initial state
            CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ triggerVertex1, triggerVertex2 }, new ExecutionVertex[]{ ackVertex1, ackVertex2 }, new ExecutionVertex[]{  }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            // nothing should be happening
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            // trigger the first checkpoint. this should not succeed
            Assert.assertFalse(coord.triggerCheckpoint(timestamp, false));
            // still, nothing should be happening
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCheckpointAbortsIfAckTasksAreNotExecuted() {
        try {
            final JobID jid = new JobID();
            final long timestamp = System.currentTimeMillis();
            // create some mock Execution vertices that need to ack the checkpoint
            final ExecutionAttemptID triggerAttemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID triggerAttemptID2 = new ExecutionAttemptID();
            ExecutionVertex triggerVertex1 = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID1);
            ExecutionVertex triggerVertex2 = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID2);
            // create some mock Execution vertices that receive the checkpoint trigger messages
            ExecutionVertex ackVertex1 = Mockito.mock(ExecutionVertex.class);
            ExecutionVertex ackVertex2 = Mockito.mock(ExecutionVertex.class);
            // set up the coordinator and validate the initial state
            CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ triggerVertex1, triggerVertex2 }, new ExecutionVertex[]{ ackVertex1, ackVertex2 }, new ExecutionVertex[]{  }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            // nothing should be happening
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            // trigger the first checkpoint. this should not succeed
            Assert.assertFalse(coord.triggerCheckpoint(timestamp, false));
            // still, nothing should be happening
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * This test triggers a checkpoint and then sends a decline checkpoint message from
     * one of the tasks. The expected behaviour is that said checkpoint is discarded and a new
     * checkpoint is triggered.
     */
    @Test
    public void testTriggerAndDeclineCheckpointSimple() {
        try {
            final JobID jid = new JobID();
            final long timestamp = System.currentTimeMillis();
            // create some mock Execution vertices that receive the checkpoint trigger messages
            final ExecutionAttemptID attemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID attemptID2 = new ExecutionAttemptID();
            ExecutionVertex vertex1 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID1);
            ExecutionVertex vertex2 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID2);
            // set up the coordinator and validate the initial state
            CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex1, vertex2 }, new ExecutionVertex[]{ vertex1, vertex2 }, new ExecutionVertex[]{ vertex1, vertex2 }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            // trigger the first checkpoint. this should succeed
            Assert.assertTrue(coord.triggerCheckpoint(timestamp, false));
            // validate that we have a pending checkpoint
            Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            // we have one task scheduled that will cancel after timeout
            Assert.assertEquals(1, coord.getNumScheduledTasks());
            long checkpointId = coord.getPendingCheckpoints().entrySet().iterator().next().getKey();
            PendingCheckpoint checkpoint = coord.getPendingCheckpoints().get(checkpointId);
            Assert.assertNotNull(checkpoint);
            Assert.assertEquals(checkpointId, checkpoint.getCheckpointId());
            Assert.assertEquals(timestamp, checkpoint.getCheckpointTimestamp());
            Assert.assertEquals(jid, checkpoint.getJobId());
            Assert.assertEquals(2, checkpoint.getNumberOfNonAcknowledgedTasks());
            Assert.assertEquals(0, checkpoint.getNumberOfAcknowledgedTasks());
            Assert.assertEquals(0, checkpoint.getOperatorStates().size());
            Assert.assertFalse(checkpoint.isDiscarded());
            Assert.assertFalse(checkpoint.isFullyAcknowledged());
            // check that the vertices received the trigger checkpoint message
            Mockito.verify(vertex1.getCurrentExecutionAttempt()).triggerCheckpoint(checkpointId, timestamp, CheckpointOptions.forCheckpointWithDefaultLocation());
            Mockito.verify(vertex2.getCurrentExecutionAttempt()).triggerCheckpoint(checkpointId, timestamp, CheckpointOptions.forCheckpointWithDefaultLocation());
            // acknowledge from one of the tasks
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID2, checkpointId));
            Assert.assertEquals(1, checkpoint.getNumberOfAcknowledgedTasks());
            Assert.assertEquals(1, checkpoint.getNumberOfNonAcknowledgedTasks());
            Assert.assertFalse(checkpoint.isDiscarded());
            Assert.assertFalse(checkpoint.isFullyAcknowledged());
            // acknowledge the same task again (should not matter)
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID2, checkpointId));
            Assert.assertFalse(checkpoint.isDiscarded());
            Assert.assertFalse(checkpoint.isFullyAcknowledged());
            // decline checkpoint from the other task, this should cancel the checkpoint
            // and trigger a new one
            coord.receiveDeclineMessage(new org.apache.flink.runtime.messages.checkpoint.DeclineCheckpoint(jid, attemptID1, checkpointId));
            Assert.assertTrue(checkpoint.isDiscarded());
            // the canceler is also removed
            Assert.assertEquals(0, coord.getNumScheduledTasks());
            // validate that we have no new pending checkpoint
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            // decline again, nothing should happen
            // decline from the other task, nothing should happen
            coord.receiveDeclineMessage(new org.apache.flink.runtime.messages.checkpoint.DeclineCheckpoint(jid, attemptID1, checkpointId));
            coord.receiveDeclineMessage(new org.apache.flink.runtime.messages.checkpoint.DeclineCheckpoint(jid, attemptID2, checkpointId));
            Assert.assertTrue(checkpoint.isDiscarded());
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * This test triggers two checkpoints and then sends a decline message from one of the tasks
     * for the first checkpoint. This should discard the first checkpoint while not triggering
     * a new checkpoint because a later checkpoint is already in progress.
     */
    @Test
    public void testTriggerAndDeclineCheckpointComplex() {
        try {
            final JobID jid = new JobID();
            final long timestamp = System.currentTimeMillis();
            // create some mock Execution vertices that receive the checkpoint trigger messages
            final ExecutionAttemptID attemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID attemptID2 = new ExecutionAttemptID();
            ExecutionVertex vertex1 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID1);
            ExecutionVertex vertex2 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID2);
            // set up the coordinator and validate the initial state
            CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex1, vertex2 }, new ExecutionVertex[]{ vertex1, vertex2 }, new ExecutionVertex[]{ vertex1, vertex2 }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            Assert.assertEquals(0, coord.getNumScheduledTasks());
            // trigger the first checkpoint. this should succeed
            Assert.assertTrue(coord.triggerCheckpoint(timestamp, false));
            // trigger second checkpoint, should also succeed
            Assert.assertTrue(coord.triggerCheckpoint((timestamp + 2), false));
            // validate that we have a pending checkpoint
            Assert.assertEquals(2, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            Assert.assertEquals(2, coord.getNumScheduledTasks());
            Iterator<Map.Entry<Long, PendingCheckpoint>> it = coord.getPendingCheckpoints().entrySet().iterator();
            long checkpoint1Id = it.next().getKey();
            long checkpoint2Id = it.next().getKey();
            PendingCheckpoint checkpoint1 = coord.getPendingCheckpoints().get(checkpoint1Id);
            PendingCheckpoint checkpoint2 = coord.getPendingCheckpoints().get(checkpoint2Id);
            Assert.assertNotNull(checkpoint1);
            Assert.assertEquals(checkpoint1Id, checkpoint1.getCheckpointId());
            Assert.assertEquals(timestamp, checkpoint1.getCheckpointTimestamp());
            Assert.assertEquals(jid, checkpoint1.getJobId());
            Assert.assertEquals(2, checkpoint1.getNumberOfNonAcknowledgedTasks());
            Assert.assertEquals(0, checkpoint1.getNumberOfAcknowledgedTasks());
            Assert.assertEquals(0, checkpoint1.getOperatorStates().size());
            Assert.assertFalse(checkpoint1.isDiscarded());
            Assert.assertFalse(checkpoint1.isFullyAcknowledged());
            Assert.assertNotNull(checkpoint2);
            Assert.assertEquals(checkpoint2Id, checkpoint2.getCheckpointId());
            Assert.assertEquals((timestamp + 2), checkpoint2.getCheckpointTimestamp());
            Assert.assertEquals(jid, checkpoint2.getJobId());
            Assert.assertEquals(2, checkpoint2.getNumberOfNonAcknowledgedTasks());
            Assert.assertEquals(0, checkpoint2.getNumberOfAcknowledgedTasks());
            Assert.assertEquals(0, checkpoint2.getOperatorStates().size());
            Assert.assertFalse(checkpoint2.isDiscarded());
            Assert.assertFalse(checkpoint2.isFullyAcknowledged());
            // check that the vertices received the trigger checkpoint message
            {
                Mockito.verify(vertex1.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpoint1Id), ArgumentMatchers.eq(timestamp), ArgumentMatchers.any(CheckpointOptions.class));
                Mockito.verify(vertex2.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpoint1Id), ArgumentMatchers.eq(timestamp), ArgumentMatchers.any(CheckpointOptions.class));
            }
            // check that the vertices received the trigger checkpoint message for the second checkpoint
            {
                Mockito.verify(vertex1.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpoint2Id), ArgumentMatchers.eq((timestamp + 2)), ArgumentMatchers.any(CheckpointOptions.class));
                Mockito.verify(vertex2.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpoint2Id), ArgumentMatchers.eq((timestamp + 2)), ArgumentMatchers.any(CheckpointOptions.class));
            }
            // decline checkpoint from one of the tasks, this should cancel the checkpoint
            coord.receiveDeclineMessage(new org.apache.flink.runtime.messages.checkpoint.DeclineCheckpoint(jid, attemptID1, checkpoint1Id));
            Assert.assertTrue(checkpoint1.isDiscarded());
            // validate that we have only one pending checkpoint left
            Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            Assert.assertEquals(1, coord.getNumScheduledTasks());
            // validate that it is the same second checkpoint from earlier
            long checkpointIdNew = coord.getPendingCheckpoints().entrySet().iterator().next().getKey();
            PendingCheckpoint checkpointNew = coord.getPendingCheckpoints().get(checkpointIdNew);
            Assert.assertEquals(checkpoint2Id, checkpointIdNew);
            Assert.assertNotNull(checkpointNew);
            Assert.assertEquals(checkpointIdNew, checkpointNew.getCheckpointId());
            Assert.assertEquals(jid, checkpointNew.getJobId());
            Assert.assertEquals(2, checkpointNew.getNumberOfNonAcknowledgedTasks());
            Assert.assertEquals(0, checkpointNew.getNumberOfAcknowledgedTasks());
            Assert.assertEquals(0, checkpointNew.getOperatorStates().size());
            Assert.assertFalse(checkpointNew.isDiscarded());
            Assert.assertFalse(checkpointNew.isFullyAcknowledged());
            Assert.assertNotEquals(checkpoint1.getCheckpointId(), checkpointNew.getCheckpointId());
            // decline again, nothing should happen
            // decline from the other task, nothing should happen
            coord.receiveDeclineMessage(new org.apache.flink.runtime.messages.checkpoint.DeclineCheckpoint(jid, attemptID1, checkpoint1Id));
            coord.receiveDeclineMessage(new org.apache.flink.runtime.messages.checkpoint.DeclineCheckpoint(jid, attemptID2, checkpoint1Id));
            Assert.assertTrue(checkpoint1.isDiscarded());
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTriggerAndConfirmSimpleCheckpoint() {
        try {
            final JobID jid = new JobID();
            final long timestamp = System.currentTimeMillis();
            // create some mock Execution vertices that receive the checkpoint trigger messages
            final ExecutionAttemptID attemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID attemptID2 = new ExecutionAttemptID();
            ExecutionVertex vertex1 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID1);
            ExecutionVertex vertex2 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID2);
            // set up the coordinator and validate the initial state
            CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex1, vertex2 }, new ExecutionVertex[]{ vertex1, vertex2 }, new ExecutionVertex[]{ vertex1, vertex2 }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            Assert.assertEquals(0, coord.getNumScheduledTasks());
            // trigger the first checkpoint. this should succeed
            Assert.assertTrue(coord.triggerCheckpoint(timestamp, false));
            // validate that we have a pending checkpoint
            Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            Assert.assertEquals(1, coord.getNumScheduledTasks());
            long checkpointId = coord.getPendingCheckpoints().entrySet().iterator().next().getKey();
            PendingCheckpoint checkpoint = coord.getPendingCheckpoints().get(checkpointId);
            Assert.assertNotNull(checkpoint);
            Assert.assertEquals(checkpointId, checkpoint.getCheckpointId());
            Assert.assertEquals(timestamp, checkpoint.getCheckpointTimestamp());
            Assert.assertEquals(jid, checkpoint.getJobId());
            Assert.assertEquals(2, checkpoint.getNumberOfNonAcknowledgedTasks());
            Assert.assertEquals(0, checkpoint.getNumberOfAcknowledgedTasks());
            Assert.assertEquals(0, checkpoint.getOperatorStates().size());
            Assert.assertFalse(checkpoint.isDiscarded());
            Assert.assertFalse(checkpoint.isFullyAcknowledged());
            // check that the vertices received the trigger checkpoint message
            {
                Mockito.verify(vertex1.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId), ArgumentMatchers.eq(timestamp), ArgumentMatchers.any(CheckpointOptions.class));
                Mockito.verify(vertex2.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId), ArgumentMatchers.eq(timestamp), ArgumentMatchers.any(CheckpointOptions.class));
            }
            OperatorID opID1 = OperatorID.fromJobVertexID(vertex1.getJobvertexId());
            OperatorID opID2 = OperatorID.fromJobVertexID(vertex2.getJobvertexId());
            TaskStateSnapshot taskOperatorSubtaskStates1 = Mockito.mock(TaskStateSnapshot.class);
            TaskStateSnapshot taskOperatorSubtaskStates2 = Mockito.mock(TaskStateSnapshot.class);
            OperatorSubtaskState subtaskState1 = Mockito.mock(OperatorSubtaskState.class);
            OperatorSubtaskState subtaskState2 = Mockito.mock(OperatorSubtaskState.class);
            Mockito.when(taskOperatorSubtaskStates1.getSubtaskStateByOperatorID(opID1)).thenReturn(subtaskState1);
            Mockito.when(taskOperatorSubtaskStates2.getSubtaskStateByOperatorID(opID2)).thenReturn(subtaskState2);
            // acknowledge from one of the tasks
            AcknowledgeCheckpoint acknowledgeCheckpoint1 = new AcknowledgeCheckpoint(jid, attemptID2, checkpointId, new CheckpointMetrics(), taskOperatorSubtaskStates2);
            coord.receiveAcknowledgeMessage(acknowledgeCheckpoint1);
            Assert.assertEquals(1, checkpoint.getNumberOfAcknowledgedTasks());
            Assert.assertEquals(1, checkpoint.getNumberOfNonAcknowledgedTasks());
            Assert.assertFalse(checkpoint.isDiscarded());
            Assert.assertFalse(checkpoint.isFullyAcknowledged());
            Mockito.verify(taskOperatorSubtaskStates2, Mockito.never()).registerSharedStates(ArgumentMatchers.any(SharedStateRegistry.class));
            // acknowledge the same task again (should not matter)
            coord.receiveAcknowledgeMessage(acknowledgeCheckpoint1);
            Assert.assertFalse(checkpoint.isDiscarded());
            Assert.assertFalse(checkpoint.isFullyAcknowledged());
            Mockito.verify(subtaskState2, Mockito.never()).registerSharedStates(ArgumentMatchers.any(SharedStateRegistry.class));
            // acknowledge the other task.
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID1, checkpointId, new CheckpointMetrics(), taskOperatorSubtaskStates1));
            // the checkpoint is internally converted to a successful checkpoint and the
            // pending checkpoint object is disposed
            Assert.assertTrue(checkpoint.isDiscarded());
            // the now we should have a completed checkpoint
            Assert.assertEquals(1, coord.getNumberOfRetainedSuccessfulCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            // the canceler should be removed now
            Assert.assertEquals(0, coord.getNumScheduledTasks());
            // validate that the subtasks states have registered their shared states.
            {
                Mockito.verify(subtaskState1, Mockito.times(1)).registerSharedStates(ArgumentMatchers.any(SharedStateRegistry.class));
                Mockito.verify(subtaskState2, Mockito.times(1)).registerSharedStates(ArgumentMatchers.any(SharedStateRegistry.class));
            }
            // validate that the relevant tasks got a confirmation message
            {
                Mockito.verify(vertex1.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId), ArgumentMatchers.eq(timestamp), ArgumentMatchers.any(CheckpointOptions.class));
                Mockito.verify(vertex2.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId), ArgumentMatchers.eq(timestamp), ArgumentMatchers.any(CheckpointOptions.class));
            }
            CompletedCheckpoint success = coord.getSuccessfulCheckpoints().get(0);
            Assert.assertEquals(jid, success.getJobId());
            Assert.assertEquals(timestamp, success.getTimestamp());
            Assert.assertEquals(checkpoint.getCheckpointId(), success.getCheckpointID());
            Assert.assertEquals(2, success.getOperatorStates().size());
            // ---------------
            // trigger another checkpoint and see that this one replaces the other checkpoint
            // ---------------
            final long timestampNew = timestamp + 7;
            coord.triggerCheckpoint(timestampNew, false);
            long checkpointIdNew = coord.getPendingCheckpoints().entrySet().iterator().next().getKey();
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID1, checkpointIdNew));
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID2, checkpointIdNew));
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(1, coord.getNumberOfRetainedSuccessfulCheckpoints());
            Assert.assertEquals(0, coord.getNumScheduledTasks());
            CompletedCheckpoint successNew = coord.getSuccessfulCheckpoints().get(0);
            Assert.assertEquals(jid, successNew.getJobId());
            Assert.assertEquals(timestampNew, successNew.getTimestamp());
            Assert.assertEquals(checkpointIdNew, successNew.getCheckpointID());
            Assert.assertTrue(successNew.getOperatorStates().isEmpty());
            // validate that the relevant tasks got a confirmation message
            {
                Mockito.verify(vertex1.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointIdNew), ArgumentMatchers.eq(timestampNew), ArgumentMatchers.any(CheckpointOptions.class));
                Mockito.verify(vertex2.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointIdNew), ArgumentMatchers.eq(timestampNew), ArgumentMatchers.any(CheckpointOptions.class));
                Mockito.verify(vertex1.getCurrentExecutionAttempt(), Mockito.times(1)).notifyCheckpointComplete(ArgumentMatchers.eq(checkpointIdNew), ArgumentMatchers.eq(timestampNew));
                Mockito.verify(vertex2.getCurrentExecutionAttempt(), Mockito.times(1)).notifyCheckpointComplete(ArgumentMatchers.eq(checkpointIdNew), ArgumentMatchers.eq(timestampNew));
            }
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMultipleConcurrentCheckpoints() {
        try {
            final JobID jid = new JobID();
            final long timestamp1 = System.currentTimeMillis();
            final long timestamp2 = timestamp1 + 8617;
            // create some mock execution vertices
            final ExecutionAttemptID triggerAttemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID triggerAttemptID2 = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID2 = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID3 = new ExecutionAttemptID();
            final ExecutionAttemptID commitAttemptID = new ExecutionAttemptID();
            ExecutionVertex triggerVertex1 = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID1);
            ExecutionVertex triggerVertex2 = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID2);
            ExecutionVertex ackVertex1 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID1);
            ExecutionVertex ackVertex2 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID2);
            ExecutionVertex ackVertex3 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID3);
            ExecutionVertex commitVertex = CheckpointCoordinatorTest.mockExecutionVertex(commitAttemptID);
            // set up the coordinator and validate the initial state
            CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ triggerVertex1, triggerVertex2 }, new ExecutionVertex[]{ ackVertex1, ackVertex2, ackVertex3 }, new ExecutionVertex[]{ commitVertex }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(2), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            // trigger the first checkpoint. this should succeed
            Assert.assertTrue(coord.triggerCheckpoint(timestamp1, false));
            Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            PendingCheckpoint pending1 = coord.getPendingCheckpoints().values().iterator().next();
            long checkpointId1 = pending1.getCheckpointId();
            // trigger messages should have been sent
            Mockito.verify(triggerVertex1.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId1), ArgumentMatchers.eq(timestamp1), ArgumentMatchers.any(CheckpointOptions.class));
            Mockito.verify(triggerVertex2.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId1), ArgumentMatchers.eq(timestamp1), ArgumentMatchers.any(CheckpointOptions.class));
            // acknowledge one of the three tasks
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID2, checkpointId1));
            // start the second checkpoint
            // trigger the first checkpoint. this should succeed
            Assert.assertTrue(coord.triggerCheckpoint(timestamp2, false));
            Assert.assertEquals(2, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            PendingCheckpoint pending2;
            {
                Iterator<PendingCheckpoint> all = coord.getPendingCheckpoints().values().iterator();
                PendingCheckpoint cc1 = all.next();
                PendingCheckpoint cc2 = all.next();
                pending2 = (pending1 == cc1) ? cc2 : cc1;
            }
            long checkpointId2 = pending2.getCheckpointId();
            // trigger messages should have been sent
            Mockito.verify(triggerVertex1.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId2), ArgumentMatchers.eq(timestamp2), ArgumentMatchers.any(CheckpointOptions.class));
            Mockito.verify(triggerVertex2.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId2), ArgumentMatchers.eq(timestamp2), ArgumentMatchers.any(CheckpointOptions.class));
            // we acknowledge the remaining two tasks from the first
            // checkpoint and two tasks from the second checkpoint
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID3, checkpointId1));
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID1, checkpointId2));
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID1, checkpointId1));
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID2, checkpointId2));
            // now, the first checkpoint should be confirmed
            Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(1, coord.getNumberOfRetainedSuccessfulCheckpoints());
            Assert.assertTrue(pending1.isDiscarded());
            // the first confirm message should be out
            Mockito.verify(commitVertex.getCurrentExecutionAttempt(), Mockito.times(1)).notifyCheckpointComplete(ArgumentMatchers.eq(checkpointId1), ArgumentMatchers.eq(timestamp1));
            // send the last remaining ack for the second checkpoint
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID3, checkpointId2));
            // now, the second checkpoint should be confirmed
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(2, coord.getNumberOfRetainedSuccessfulCheckpoints());
            Assert.assertTrue(pending2.isDiscarded());
            // the second commit message should be out
            Mockito.verify(commitVertex.getCurrentExecutionAttempt(), Mockito.times(1)).notifyCheckpointComplete(ArgumentMatchers.eq(checkpointId2), ArgumentMatchers.eq(timestamp2));
            // validate the committed checkpoints
            List<CompletedCheckpoint> scs = coord.getSuccessfulCheckpoints();
            CompletedCheckpoint sc1 = scs.get(0);
            Assert.assertEquals(checkpointId1, sc1.getCheckpointID());
            Assert.assertEquals(timestamp1, sc1.getTimestamp());
            Assert.assertEquals(jid, sc1.getJobId());
            Assert.assertTrue(sc1.getOperatorStates().isEmpty());
            CompletedCheckpoint sc2 = scs.get(1);
            Assert.assertEquals(checkpointId2, sc2.getCheckpointID());
            Assert.assertEquals(timestamp2, sc2.getTimestamp());
            Assert.assertEquals(jid, sc2.getJobId());
            Assert.assertTrue(sc2.getOperatorStates().isEmpty());
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSuccessfulCheckpointSubsumesUnsuccessful() {
        try {
            final JobID jid = new JobID();
            final long timestamp1 = System.currentTimeMillis();
            final long timestamp2 = timestamp1 + 1552;
            // create some mock execution vertices
            final ExecutionAttemptID triggerAttemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID triggerAttemptID2 = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID2 = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID3 = new ExecutionAttemptID();
            final ExecutionAttemptID commitAttemptID = new ExecutionAttemptID();
            ExecutionVertex triggerVertex1 = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID1);
            ExecutionVertex triggerVertex2 = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID2);
            ExecutionVertex ackVertex1 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID1);
            ExecutionVertex ackVertex2 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID2);
            ExecutionVertex ackVertex3 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID3);
            ExecutionVertex commitVertex = CheckpointCoordinatorTest.mockExecutionVertex(commitAttemptID);
            // set up the coordinator and validate the initial state
            CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ triggerVertex1, triggerVertex2 }, new ExecutionVertex[]{ ackVertex1, ackVertex2, ackVertex3 }, new ExecutionVertex[]{ commitVertex }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(10), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            // trigger the first checkpoint. this should succeed
            Assert.assertTrue(coord.triggerCheckpoint(timestamp1, false));
            Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            PendingCheckpoint pending1 = coord.getPendingCheckpoints().values().iterator().next();
            long checkpointId1 = pending1.getCheckpointId();
            // trigger messages should have been sent
            Mockito.verify(triggerVertex1.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId1), ArgumentMatchers.eq(timestamp1), ArgumentMatchers.any(CheckpointOptions.class));
            Mockito.verify(triggerVertex2.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId1), ArgumentMatchers.eq(timestamp1), ArgumentMatchers.any(CheckpointOptions.class));
            OperatorID opID1 = OperatorID.fromJobVertexID(ackVertex1.getJobvertexId());
            OperatorID opID2 = OperatorID.fromJobVertexID(ackVertex2.getJobvertexId());
            OperatorID opID3 = OperatorID.fromJobVertexID(ackVertex3.getJobvertexId());
            TaskStateSnapshot taskOperatorSubtaskStates1_1 = Mockito.spy(new TaskStateSnapshot());
            TaskStateSnapshot taskOperatorSubtaskStates1_2 = Mockito.spy(new TaskStateSnapshot());
            TaskStateSnapshot taskOperatorSubtaskStates1_3 = Mockito.spy(new TaskStateSnapshot());
            OperatorSubtaskState subtaskState1_1 = Mockito.mock(OperatorSubtaskState.class);
            OperatorSubtaskState subtaskState1_2 = Mockito.mock(OperatorSubtaskState.class);
            OperatorSubtaskState subtaskState1_3 = Mockito.mock(OperatorSubtaskState.class);
            taskOperatorSubtaskStates1_1.putSubtaskStateByOperatorID(opID1, subtaskState1_1);
            taskOperatorSubtaskStates1_2.putSubtaskStateByOperatorID(opID2, subtaskState1_2);
            taskOperatorSubtaskStates1_3.putSubtaskStateByOperatorID(opID3, subtaskState1_3);
            // acknowledge one of the three tasks
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID2, checkpointId1, new CheckpointMetrics(), taskOperatorSubtaskStates1_2));
            // start the second checkpoint
            // trigger the first checkpoint. this should succeed
            Assert.assertTrue(coord.triggerCheckpoint(timestamp2, false));
            Assert.assertEquals(2, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            PendingCheckpoint pending2;
            {
                Iterator<PendingCheckpoint> all = coord.getPendingCheckpoints().values().iterator();
                PendingCheckpoint cc1 = all.next();
                PendingCheckpoint cc2 = all.next();
                pending2 = (pending1 == cc1) ? cc2 : cc1;
            }
            long checkpointId2 = pending2.getCheckpointId();
            TaskStateSnapshot taskOperatorSubtaskStates2_1 = Mockito.spy(new TaskStateSnapshot());
            TaskStateSnapshot taskOperatorSubtaskStates2_2 = Mockito.spy(new TaskStateSnapshot());
            TaskStateSnapshot taskOperatorSubtaskStates2_3 = Mockito.spy(new TaskStateSnapshot());
            OperatorSubtaskState subtaskState2_1 = Mockito.mock(OperatorSubtaskState.class);
            OperatorSubtaskState subtaskState2_2 = Mockito.mock(OperatorSubtaskState.class);
            OperatorSubtaskState subtaskState2_3 = Mockito.mock(OperatorSubtaskState.class);
            taskOperatorSubtaskStates2_1.putSubtaskStateByOperatorID(opID1, subtaskState2_1);
            taskOperatorSubtaskStates2_2.putSubtaskStateByOperatorID(opID2, subtaskState2_2);
            taskOperatorSubtaskStates2_3.putSubtaskStateByOperatorID(opID3, subtaskState2_3);
            // trigger messages should have been sent
            Mockito.verify(triggerVertex1.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId2), ArgumentMatchers.eq(timestamp2), ArgumentMatchers.any(CheckpointOptions.class));
            Mockito.verify(triggerVertex2.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointId2), ArgumentMatchers.eq(timestamp2), ArgumentMatchers.any(CheckpointOptions.class));
            // we acknowledge one more task from the first checkpoint and the second
            // checkpoint completely. The second checkpoint should then subsume the first checkpoint
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID3, checkpointId2, new CheckpointMetrics(), taskOperatorSubtaskStates2_3));
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID1, checkpointId2, new CheckpointMetrics(), taskOperatorSubtaskStates2_1));
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID1, checkpointId1, new CheckpointMetrics(), taskOperatorSubtaskStates1_1));
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID2, checkpointId2, new CheckpointMetrics(), taskOperatorSubtaskStates2_2));
            // now, the second checkpoint should be confirmed, and the first discarded
            // actually both pending checkpoints are discarded, and the second has been transformed
            // into a successful checkpoint
            Assert.assertTrue(pending1.isDiscarded());
            Assert.assertTrue(pending2.isDiscarded());
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(1, coord.getNumberOfRetainedSuccessfulCheckpoints());
            // validate that all received subtask states in the first checkpoint have been discarded
            Mockito.verify(subtaskState1_1, Mockito.times(1)).discardState();
            Mockito.verify(subtaskState1_2, Mockito.times(1)).discardState();
            // validate that all subtask states in the second checkpoint are not discarded
            Mockito.verify(subtaskState2_1, Mockito.never()).discardState();
            Mockito.verify(subtaskState2_2, Mockito.never()).discardState();
            Mockito.verify(subtaskState2_3, Mockito.never()).discardState();
            // validate the committed checkpoints
            List<CompletedCheckpoint> scs = coord.getSuccessfulCheckpoints();
            CompletedCheckpoint success = scs.get(0);
            Assert.assertEquals(checkpointId2, success.getCheckpointID());
            Assert.assertEquals(timestamp2, success.getTimestamp());
            Assert.assertEquals(jid, success.getJobId());
            Assert.assertEquals(3, success.getOperatorStates().size());
            // the first confirm message should be out
            Mockito.verify(commitVertex.getCurrentExecutionAttempt(), Mockito.times(1)).notifyCheckpointComplete(ArgumentMatchers.eq(checkpointId2), ArgumentMatchers.eq(timestamp2));
            // send the last remaining ack for the first checkpoint. This should not do anything
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID3, checkpointId1, new CheckpointMetrics(), taskOperatorSubtaskStates1_3));
            Mockito.verify(subtaskState1_3, Mockito.times(1)).discardState();
            coord.shutdown(FINISHED);
            // validate that the states in the second checkpoint have been discarded
            Mockito.verify(subtaskState2_1, Mockito.times(1)).discardState();
            Mockito.verify(subtaskState2_2, Mockito.times(1)).discardState();
            Mockito.verify(subtaskState2_3, Mockito.times(1)).discardState();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCheckpointTimeoutIsolated() {
        try {
            final JobID jid = new JobID();
            final long timestamp = System.currentTimeMillis();
            // create some mock execution vertices
            final ExecutionAttemptID triggerAttemptID = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID2 = new ExecutionAttemptID();
            final ExecutionAttemptID commitAttemptID = new ExecutionAttemptID();
            ExecutionVertex triggerVertex = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID);
            ExecutionVertex ackVertex1 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID1);
            ExecutionVertex ackVertex2 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID2);
            ExecutionVertex commitVertex = CheckpointCoordinatorTest.mockExecutionVertex(commitAttemptID);
            // set up the coordinator
            // the timeout for the checkpoint is a 200 milliseconds
            CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 200, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ triggerVertex }, new ExecutionVertex[]{ ackVertex1, ackVertex2 }, new ExecutionVertex[]{ commitVertex }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(2), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            // trigger a checkpoint, partially acknowledged
            Assert.assertTrue(coord.triggerCheckpoint(timestamp, false));
            Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
            PendingCheckpoint checkpoint = coord.getPendingCheckpoints().values().iterator().next();
            Assert.assertFalse(checkpoint.isDiscarded());
            OperatorID opID1 = OperatorID.fromJobVertexID(ackVertex1.getJobvertexId());
            TaskStateSnapshot taskOperatorSubtaskStates1 = Mockito.spy(new TaskStateSnapshot());
            OperatorSubtaskState subtaskState1 = Mockito.mock(OperatorSubtaskState.class);
            taskOperatorSubtaskStates1.putSubtaskStateByOperatorID(opID1, subtaskState1);
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID1, checkpoint.getCheckpointId(), new CheckpointMetrics(), taskOperatorSubtaskStates1));
            // wait until the checkpoint must have expired.
            // we check every 250 msecs conservatively for 5 seconds
            // to give even slow build servers a very good chance of completing this
            long deadline = (System.currentTimeMillis()) + 5000;
            do {
                Thread.sleep(250);
            } while (((!(checkpoint.isDiscarded())) && ((coord.getNumberOfPendingCheckpoints()) > 0)) && ((System.currentTimeMillis()) < deadline) );
            Assert.assertTrue("Checkpoint was not canceled by the timeout", checkpoint.isDiscarded());
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
            // validate that the received states have been discarded
            Mockito.verify(subtaskState1, Mockito.times(1)).discardState();
            // no confirm message must have been sent
            Mockito.verify(commitVertex.getCurrentExecutionAttempt(), Mockito.times(0)).notifyCheckpointComplete(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testHandleMessagesForNonExistingCheckpoints() {
        try {
            final JobID jid = new JobID();
            final long timestamp = System.currentTimeMillis();
            // create some mock execution vertices and trigger some checkpoint
            final ExecutionAttemptID triggerAttemptID = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID1 = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID2 = new ExecutionAttemptID();
            final ExecutionAttemptID commitAttemptID = new ExecutionAttemptID();
            ExecutionVertex triggerVertex = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID);
            ExecutionVertex ackVertex1 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID1);
            ExecutionVertex ackVertex2 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID2);
            ExecutionVertex commitVertex = CheckpointCoordinatorTest.mockExecutionVertex(commitAttemptID);
            CheckpointCoordinator coord = new CheckpointCoordinator(jid, 200000, 200000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ triggerVertex }, new ExecutionVertex[]{ ackVertex1, ackVertex2 }, new ExecutionVertex[]{ commitVertex }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(2), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            Assert.assertTrue(coord.triggerCheckpoint(timestamp, false));
            long checkpointId = coord.getPendingCheckpoints().keySet().iterator().next();
            // send some messages that do not belong to either the job or the any
            // of the vertices that need to be acknowledged.
            // non of the messages should throw an exception
            // wrong job id
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(new JobID(), ackAttemptID1, checkpointId));
            // unknown checkpoint
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID1, 1L));
            // unknown ack vertex
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, new ExecutionAttemptID(), checkpointId));
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests that late acknowledge checkpoint messages are properly cleaned up. Furthermore it tests
     * that unknown checkpoint messages for the same job a are cleaned up as well. In contrast
     * checkpointing messages from other jobs should not be touched. A late acknowledge
     * message is an acknowledge message which arrives after the checkpoint has been declined.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStateCleanupForLateOrUnknownMessages() throws Exception {
        final JobID jobId = new JobID();
        final ExecutionAttemptID triggerAttemptId = new ExecutionAttemptID();
        final ExecutionVertex triggerVertex = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptId);
        final ExecutionAttemptID ackAttemptId1 = new ExecutionAttemptID();
        final ExecutionVertex ackVertex1 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptId1);
        final ExecutionAttemptID ackAttemptId2 = new ExecutionAttemptID();
        final ExecutionVertex ackVertex2 = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptId2);
        final long timestamp = 1L;
        CheckpointCoordinator coord = new CheckpointCoordinator(jobId, 20000L, 20000L, 0L, 1, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ triggerVertex }, new ExecutionVertex[]{ triggerVertex, ackVertex1, ackVertex2 }, new ExecutionVertex[0], new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        Assert.assertTrue(coord.triggerCheckpoint(timestamp, false));
        Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
        PendingCheckpoint pendingCheckpoint = coord.getPendingCheckpoints().values().iterator().next();
        long checkpointId = pendingCheckpoint.getCheckpointId();
        OperatorID opIDtrigger = OperatorID.fromJobVertexID(triggerVertex.getJobvertexId());
        TaskStateSnapshot taskOperatorSubtaskStatesTrigger = Mockito.spy(new TaskStateSnapshot());
        OperatorSubtaskState subtaskStateTrigger = Mockito.mock(OperatorSubtaskState.class);
        taskOperatorSubtaskStatesTrigger.putSubtaskStateByOperatorID(opIDtrigger, subtaskStateTrigger);
        // acknowledge the first trigger vertex
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jobId, triggerAttemptId, checkpointId, new CheckpointMetrics(), taskOperatorSubtaskStatesTrigger));
        // verify that the subtask state has not been discarded
        Mockito.verify(subtaskStateTrigger, Mockito.never()).discardState();
        TaskStateSnapshot unknownSubtaskState = Mockito.mock(TaskStateSnapshot.class);
        // receive an acknowledge message for an unknown vertex
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jobId, new ExecutionAttemptID(), checkpointId, new CheckpointMetrics(), unknownSubtaskState));
        // we should discard acknowledge messages from an unknown vertex belonging to our job
        Mockito.verify(unknownSubtaskState, Mockito.times(1)).discardState();
        TaskStateSnapshot differentJobSubtaskState = Mockito.mock(TaskStateSnapshot.class);
        // receive an acknowledge message from an unknown job
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(new JobID(), new ExecutionAttemptID(), checkpointId, new CheckpointMetrics(), differentJobSubtaskState));
        // we should not interfere with different jobs
        Mockito.verify(differentJobSubtaskState, Mockito.never()).discardState();
        // duplicate acknowledge message for the trigger vertex
        TaskStateSnapshot triggerSubtaskState = Mockito.mock(TaskStateSnapshot.class);
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jobId, triggerAttemptId, checkpointId, new CheckpointMetrics(), triggerSubtaskState));
        // duplicate acknowledge messages for a known vertex should not trigger discarding the state
        Mockito.verify(triggerSubtaskState, Mockito.never()).discardState();
        // let the checkpoint fail at the first ack vertex
        Mockito.reset(subtaskStateTrigger);
        coord.receiveDeclineMessage(new org.apache.flink.runtime.messages.checkpoint.DeclineCheckpoint(jobId, ackAttemptId1, checkpointId));
        Assert.assertTrue(pendingCheckpoint.isDiscarded());
        // check that we've cleaned up the already acknowledged state
        Mockito.verify(subtaskStateTrigger, Mockito.times(1)).discardState();
        TaskStateSnapshot ackSubtaskState = Mockito.mock(TaskStateSnapshot.class);
        // late acknowledge message from the second ack vertex
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jobId, ackAttemptId2, checkpointId, new CheckpointMetrics(), ackSubtaskState));
        // check that we also cleaned up this state
        Mockito.verify(ackSubtaskState, Mockito.times(1)).discardState();
        // receive an acknowledge message from an unknown job
        Mockito.reset(differentJobSubtaskState);
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(new JobID(), new ExecutionAttemptID(), checkpointId, new CheckpointMetrics(), differentJobSubtaskState));
        // we should not interfere with different jobs
        Mockito.verify(differentJobSubtaskState, Mockito.never()).discardState();
        TaskStateSnapshot unknownSubtaskState2 = Mockito.mock(TaskStateSnapshot.class);
        // receive an acknowledge message for an unknown vertex
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jobId, new ExecutionAttemptID(), checkpointId, new CheckpointMetrics(), unknownSubtaskState2));
        // we should discard acknowledge messages from an unknown vertex belonging to our job
        Mockito.verify(unknownSubtaskState2, Mockito.times(1)).discardState();
    }

    @Test
    public void testPeriodicTriggering() {
        try {
            final JobID jid = new JobID();
            final long start = System.currentTimeMillis();
            // create some mock execution vertices and trigger some checkpoint
            final ExecutionAttemptID triggerAttemptID = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID = new ExecutionAttemptID();
            final ExecutionAttemptID commitAttemptID = new ExecutionAttemptID();
            ExecutionVertex triggerVertex = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID);
            ExecutionVertex ackVertex = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID);
            ExecutionVertex commitVertex = CheckpointCoordinatorTest.mockExecutionVertex(commitAttemptID);
            final AtomicInteger numCalls = new AtomicInteger();
            final Execution execution = triggerVertex.getCurrentExecutionAttempt();
            Mockito.doAnswer(new Answer<Void>() {
                private long lastId = -1;

                private long lastTs = -1;

                @Override
                public Void answer(InvocationOnMock invocation) throws Throwable {
                    long id = ((Long) (invocation.getArguments()[0]));
                    long ts = ((Long) (invocation.getArguments()[1]));
                    Assert.assertTrue((id > (lastId)));
                    Assert.assertTrue((ts >= (lastTs)));
                    Assert.assertTrue((ts >= start));
                    lastId = id;
                    lastTs = ts;
                    numCalls.incrementAndGet();
                    return null;
                }
            }).when(execution).triggerCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(CheckpointOptions.class));
            CheckpointCoordinator coord = // periodic interval is 10 ms
            // timeout is very long (200 s)
            new CheckpointCoordinator(jid, 10, 200000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ triggerVertex }, new ExecutionVertex[]{ ackVertex }, new ExecutionVertex[]{ commitVertex }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(2), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            coord.startCheckpointScheduler();
            long timeout = (System.currentTimeMillis()) + 60000;
            do {
                Thread.sleep(20);
            } while ((timeout > (System.currentTimeMillis())) && ((numCalls.get()) < 5) );
            Assert.assertTrue(((numCalls.get()) >= 5));
            coord.stopCheckpointScheduler();
            // for 400 ms, no further calls may come.
            // there may be the case that one trigger was fired and about to
            // acquire the lock, such that after cancelling it will still do
            // the remainder of its work
            int numCallsSoFar = numCalls.get();
            Thread.sleep(400);
            Assert.assertTrue(((numCallsSoFar == (numCalls.get())) || ((numCallsSoFar + 1) == (numCalls.get()))));
            // start another sequence of periodic scheduling
            numCalls.set(0);
            coord.startCheckpointScheduler();
            timeout = (System.currentTimeMillis()) + 60000;
            do {
                Thread.sleep(20);
            } while ((timeout > (System.currentTimeMillis())) && ((numCalls.get()) < 5) );
            Assert.assertTrue(((numCalls.get()) >= 5));
            coord.stopCheckpointScheduler();
            // for 400 ms, no further calls may come
            // there may be the case that one trigger was fired and about to
            // acquire the lock, such that after cancelling it will still do
            // the remainder of its work
            numCallsSoFar = numCalls.get();
            Thread.sleep(400);
            Assert.assertTrue(((numCallsSoFar == (numCalls.get())) || ((numCallsSoFar + 1) == (numCalls.get()))));
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * This test verified that after a completed checkpoint a certain time has passed before
     * another is triggered.
     */
    @Test
    public void testMinTimeBetweenCheckpointsInterval() throws Exception {
        final JobID jid = new JobID();
        // create some mock execution vertices and trigger some checkpoint
        final ExecutionAttemptID attemptID = new ExecutionAttemptID();
        final ExecutionVertex vertex = CheckpointCoordinatorTest.mockExecutionVertex(attemptID);
        final Execution executionAttempt = vertex.getCurrentExecutionAttempt();
        final BlockingQueue<Long> triggerCalls = new LinkedBlockingQueue<>();
        Mockito.doAnswer(( invocation) -> {
            triggerCalls.add(((Long) (invocation.getArguments()[0])));
            return null;
        }).when(executionAttempt).triggerCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(CheckpointOptions.class));
        final long delay = 50;
        final CheckpointCoordinator coord = // periodic interval is 2 ms
        // timeout is very long (200 s)
        // 50 ms delay between checkpoints
        new CheckpointCoordinator(jid, 2, 200000, delay, 1, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex }, new ExecutionVertex[]{ vertex }, new ExecutionVertex[]{ vertex }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(2), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        try {
            coord.startCheckpointScheduler();
            // wait until the first checkpoint was triggered
            Long firstCallId = triggerCalls.take();
            Assert.assertEquals(1L, firstCallId.longValue());
            AcknowledgeCheckpoint ackMsg = new AcknowledgeCheckpoint(jid, attemptID, 1L);
            // tell the coordinator that the checkpoint is done
            final long ackTime = System.nanoTime();
            coord.receiveAcknowledgeMessage(ackMsg);
            // wait until the next checkpoint is triggered
            Long nextCallId = triggerCalls.take();
            final long nextCheckpointTime = System.nanoTime();
            Assert.assertEquals(2L, nextCallId.longValue());
            final long delayMillis = (nextCheckpointTime - ackTime) / 1000000;
            // we need to add one ms here to account for rounding errors
            if ((delayMillis + 1) < delay) {
                Assert.fail(((("checkpoint came too early: delay was " + delayMillis) + " but should have been at least ") + delay));
            }
        } finally {
            coord.stopCheckpointScheduler();
            coord.shutdown(FINISHED);
        }
    }

    @Test
    public void testMaxConcurrentAttempts1() {
        testMaxConcurrentAttempts(1);
    }

    @Test
    public void testMaxConcurrentAttempts2() {
        testMaxConcurrentAttempts(2);
    }

    @Test
    public void testMaxConcurrentAttempts5() {
        testMaxConcurrentAttempts(5);
    }

    @Test
    public void testTriggerAndConfirmSimpleSavepoint() throws Exception {
        final JobID jid = new JobID();
        final long timestamp = System.currentTimeMillis();
        // create some mock Execution vertices that receive the checkpoint trigger messages
        final ExecutionAttemptID attemptID1 = new ExecutionAttemptID();
        final ExecutionAttemptID attemptID2 = new ExecutionAttemptID();
        ExecutionVertex vertex1 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID1);
        ExecutionVertex vertex2 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID2);
        // set up the coordinator and validate the initial state
        CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex1, vertex2 }, new ExecutionVertex[]{ vertex1, vertex2 }, new ExecutionVertex[]{ vertex1, vertex2 }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
        Assert.assertEquals(0, coord.getNumberOfRetainedSuccessfulCheckpoints());
        // trigger the first checkpoint. this should succeed
        String savepointDir = tmpFolder.newFolder().getAbsolutePath();
        CompletableFuture<CompletedCheckpoint> savepointFuture = coord.triggerSavepoint(timestamp, savepointDir);
        Assert.assertFalse(savepointFuture.isDone());
        // validate that we have a pending savepoint
        Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
        long checkpointId = coord.getPendingCheckpoints().entrySet().iterator().next().getKey();
        PendingCheckpoint pending = coord.getPendingCheckpoints().get(checkpointId);
        Assert.assertNotNull(pending);
        Assert.assertEquals(checkpointId, pending.getCheckpointId());
        Assert.assertEquals(timestamp, pending.getCheckpointTimestamp());
        Assert.assertEquals(jid, pending.getJobId());
        Assert.assertEquals(2, pending.getNumberOfNonAcknowledgedTasks());
        Assert.assertEquals(0, pending.getNumberOfAcknowledgedTasks());
        Assert.assertEquals(0, pending.getOperatorStates().size());
        Assert.assertFalse(pending.isDiscarded());
        Assert.assertFalse(pending.isFullyAcknowledged());
        Assert.assertFalse(pending.canBeSubsumed());
        OperatorID opID1 = OperatorID.fromJobVertexID(vertex1.getJobvertexId());
        OperatorID opID2 = OperatorID.fromJobVertexID(vertex2.getJobvertexId());
        TaskStateSnapshot taskOperatorSubtaskStates1 = Mockito.mock(TaskStateSnapshot.class);
        TaskStateSnapshot taskOperatorSubtaskStates2 = Mockito.mock(TaskStateSnapshot.class);
        OperatorSubtaskState subtaskState1 = Mockito.mock(OperatorSubtaskState.class);
        OperatorSubtaskState subtaskState2 = Mockito.mock(OperatorSubtaskState.class);
        Mockito.when(taskOperatorSubtaskStates1.getSubtaskStateByOperatorID(opID1)).thenReturn(subtaskState1);
        Mockito.when(taskOperatorSubtaskStates2.getSubtaskStateByOperatorID(opID2)).thenReturn(subtaskState2);
        // acknowledge from one of the tasks
        AcknowledgeCheckpoint acknowledgeCheckpoint2 = new AcknowledgeCheckpoint(jid, attemptID2, checkpointId, new CheckpointMetrics(), taskOperatorSubtaskStates2);
        coord.receiveAcknowledgeMessage(acknowledgeCheckpoint2);
        Assert.assertEquals(1, pending.getNumberOfAcknowledgedTasks());
        Assert.assertEquals(1, pending.getNumberOfNonAcknowledgedTasks());
        Assert.assertFalse(pending.isDiscarded());
        Assert.assertFalse(pending.isFullyAcknowledged());
        Assert.assertFalse(savepointFuture.isDone());
        // acknowledge the same task again (should not matter)
        coord.receiveAcknowledgeMessage(acknowledgeCheckpoint2);
        Assert.assertFalse(pending.isDiscarded());
        Assert.assertFalse(pending.isFullyAcknowledged());
        Assert.assertFalse(savepointFuture.isDone());
        // acknowledge the other task.
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID1, checkpointId, new CheckpointMetrics(), taskOperatorSubtaskStates1));
        // the checkpoint is internally converted to a successful checkpoint and the
        // pending checkpoint object is disposed
        Assert.assertTrue(pending.isDiscarded());
        Assert.assertTrue(savepointFuture.isDone());
        // the now we should have a completed checkpoint
        Assert.assertEquals(1, coord.getNumberOfRetainedSuccessfulCheckpoints());
        Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
        // validate that the relevant tasks got a confirmation message
        {
            Mockito.verify(vertex1.getCurrentExecutionAttempt(), Mockito.times(1)).notifyCheckpointComplete(ArgumentMatchers.eq(checkpointId), ArgumentMatchers.eq(timestamp));
            Mockito.verify(vertex2.getCurrentExecutionAttempt(), Mockito.times(1)).notifyCheckpointComplete(ArgumentMatchers.eq(checkpointId), ArgumentMatchers.eq(timestamp));
        }
        // validate that the shared states are registered
        {
            Mockito.verify(subtaskState1, Mockito.times(1)).registerSharedStates(ArgumentMatchers.any(SharedStateRegistry.class));
            Mockito.verify(subtaskState2, Mockito.times(1)).registerSharedStates(ArgumentMatchers.any(SharedStateRegistry.class));
        }
        CompletedCheckpoint success = coord.getSuccessfulCheckpoints().get(0);
        Assert.assertEquals(jid, success.getJobId());
        Assert.assertEquals(timestamp, success.getTimestamp());
        Assert.assertEquals(pending.getCheckpointId(), success.getCheckpointID());
        Assert.assertEquals(2, success.getOperatorStates().size());
        // ---------------
        // trigger another checkpoint and see that this one replaces the other checkpoint
        // ---------------
        final long timestampNew = timestamp + 7;
        savepointFuture = coord.triggerSavepoint(timestampNew, savepointDir);
        Assert.assertFalse(savepointFuture.isDone());
        long checkpointIdNew = coord.getPendingCheckpoints().entrySet().iterator().next().getKey();
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID1, checkpointIdNew));
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID2, checkpointIdNew));
        Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
        Assert.assertEquals(1, coord.getNumberOfRetainedSuccessfulCheckpoints());
        CompletedCheckpoint successNew = coord.getSuccessfulCheckpoints().get(0);
        Assert.assertEquals(jid, successNew.getJobId());
        Assert.assertEquals(timestampNew, successNew.getTimestamp());
        Assert.assertEquals(checkpointIdNew, successNew.getCheckpointID());
        Assert.assertTrue(successNew.getOperatorStates().isEmpty());
        Assert.assertTrue(savepointFuture.isDone());
        // validate that the first savepoint does not discard its private states.
        Mockito.verify(subtaskState1, Mockito.never()).discardState();
        Mockito.verify(subtaskState2, Mockito.never()).discardState();
        // validate that the relevant tasks got a confirmation message
        {
            Mockito.verify(vertex1.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointIdNew), ArgumentMatchers.eq(timestampNew), ArgumentMatchers.any(CheckpointOptions.class));
            Mockito.verify(vertex2.getCurrentExecutionAttempt(), Mockito.times(1)).triggerCheckpoint(ArgumentMatchers.eq(checkpointIdNew), ArgumentMatchers.eq(timestampNew), ArgumentMatchers.any(CheckpointOptions.class));
            Mockito.verify(vertex1.getCurrentExecutionAttempt(), Mockito.times(1)).notifyCheckpointComplete(ArgumentMatchers.eq(checkpointIdNew), ArgumentMatchers.eq(timestampNew));
            Mockito.verify(vertex2.getCurrentExecutionAttempt(), Mockito.times(1)).notifyCheckpointComplete(ArgumentMatchers.eq(checkpointIdNew), ArgumentMatchers.eq(timestampNew));
        }
        coord.shutdown(FINISHED);
    }

    /**
     * Triggers a savepoint and two checkpoints. The second checkpoint completes
     * and subsumes the first checkpoint, but not the first savepoint. Then we
     * trigger another checkpoint and savepoint. The 2nd savepoint completes and
     * subsumes the last checkpoint, but not the first savepoint.
     */
    @Test
    public void testSavepointsAreNotSubsumed() throws Exception {
        final JobID jid = new JobID();
        final long timestamp = System.currentTimeMillis();
        // create some mock Execution vertices that receive the checkpoint trigger messages
        final ExecutionAttemptID attemptID1 = new ExecutionAttemptID();
        final ExecutionAttemptID attemptID2 = new ExecutionAttemptID();
        ExecutionVertex vertex1 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID1);
        ExecutionVertex vertex2 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID2);
        StandaloneCheckpointIDCounter counter = new StandaloneCheckpointIDCounter();
        // set up the coordinator and validate the initial state
        CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex1, vertex2 }, new ExecutionVertex[]{ vertex1, vertex2 }, new ExecutionVertex[]{ vertex1, vertex2 }, counter, new StandaloneCompletedCheckpointStore(10), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        String savepointDir = tmpFolder.newFolder().getAbsolutePath();
        // Trigger savepoint and checkpoint
        CompletableFuture<CompletedCheckpoint> savepointFuture1 = coord.triggerSavepoint(timestamp, savepointDir);
        long savepointId1 = counter.getLast();
        Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
        Assert.assertTrue(coord.triggerCheckpoint((timestamp + 1), false));
        Assert.assertEquals(2, coord.getNumberOfPendingCheckpoints());
        Assert.assertTrue(coord.triggerCheckpoint((timestamp + 2), false));
        long checkpointId2 = counter.getLast();
        Assert.assertEquals(3, coord.getNumberOfPendingCheckpoints());
        // 2nd checkpoint should subsume the 1st checkpoint, but not the savepoint
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID1, checkpointId2));
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID2, checkpointId2));
        Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
        Assert.assertEquals(1, coord.getNumberOfRetainedSuccessfulCheckpoints());
        Assert.assertFalse(coord.getPendingCheckpoints().get(savepointId1).isDiscarded());
        Assert.assertFalse(savepointFuture1.isDone());
        Assert.assertTrue(coord.triggerCheckpoint((timestamp + 3), false));
        Assert.assertEquals(2, coord.getNumberOfPendingCheckpoints());
        CompletableFuture<CompletedCheckpoint> savepointFuture2 = coord.triggerSavepoint((timestamp + 4), savepointDir);
        long savepointId2 = counter.getLast();
        Assert.assertEquals(3, coord.getNumberOfPendingCheckpoints());
        // 2nd savepoint should subsume the last checkpoint, but not the 1st savepoint
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID1, savepointId2));
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID2, savepointId2));
        Assert.assertEquals(1, coord.getNumberOfPendingCheckpoints());
        Assert.assertEquals(2, coord.getNumberOfRetainedSuccessfulCheckpoints());
        Assert.assertFalse(coord.getPendingCheckpoints().get(savepointId1).isDiscarded());
        Assert.assertFalse(savepointFuture1.isDone());
        Assert.assertTrue(savepointFuture2.isDone());
        // Ack first savepoint
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID1, savepointId1));
        coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, attemptID2, savepointId1));
        Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
        Assert.assertEquals(3, coord.getNumberOfRetainedSuccessfulCheckpoints());
        Assert.assertTrue(savepointFuture1.isDone());
    }

    @Test
    public void testMaxConcurrentAttempsWithSubsumption() {
        try {
            final int maxConcurrentAttempts = 2;
            final JobID jid = new JobID();
            // create some mock execution vertices and trigger some checkpoint
            final ExecutionAttemptID triggerAttemptID = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID = new ExecutionAttemptID();
            final ExecutionAttemptID commitAttemptID = new ExecutionAttemptID();
            ExecutionVertex triggerVertex = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID);
            ExecutionVertex ackVertex = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID);
            ExecutionVertex commitVertex = CheckpointCoordinatorTest.mockExecutionVertex(commitAttemptID);
            CheckpointCoordinator coord = // periodic interval is 10 ms
            // timeout is very long (200 s)
            // no extra delay
            // max two concurrent checkpoints
            new CheckpointCoordinator(jid, 10, 200000, 0L, maxConcurrentAttempts, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ triggerVertex }, new ExecutionVertex[]{ ackVertex }, new ExecutionVertex[]{ commitVertex }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(2), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            coord.startCheckpointScheduler();
            // after a while, there should be exactly as many checkpoints
            // as concurrently permitted
            long now = System.currentTimeMillis();
            long timeout = now + 60000;
            long minDuration = now + 100;
            do {
                Thread.sleep(20);
            } while (((now = System.currentTimeMillis()) < minDuration) || (((coord.getNumberOfPendingCheckpoints()) < maxConcurrentAttempts) && (now < timeout)) );
            // validate that the pending checkpoints are there
            Assert.assertEquals(maxConcurrentAttempts, coord.getNumberOfPendingCheckpoints());
            Assert.assertNotNull(coord.getPendingCheckpoints().get(1L));
            Assert.assertNotNull(coord.getPendingCheckpoints().get(2L));
            // now we acknowledge the second checkpoint, which should subsume the first checkpoint
            // and allow two more checkpoints to be triggered
            // now, once we acknowledge one checkpoint, it should trigger the next one
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jid, ackAttemptID, 2L));
            // after a while, there should be the new checkpoints
            final long newTimeout = (System.currentTimeMillis()) + 60000;
            do {
                Thread.sleep(20);
            } while (((coord.getPendingCheckpoints().get(4L)) == null) && ((System.currentTimeMillis()) < newTimeout) );
            // do the final check
            Assert.assertEquals(maxConcurrentAttempts, coord.getNumberOfPendingCheckpoints());
            Assert.assertNotNull(coord.getPendingCheckpoints().get(3L));
            Assert.assertNotNull(coord.getPendingCheckpoints().get(4L));
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPeriodicSchedulingWithInactiveTasks() {
        try {
            final JobID jid = new JobID();
            // create some mock execution vertices and trigger some checkpoint
            final ExecutionAttemptID triggerAttemptID = new ExecutionAttemptID();
            final ExecutionAttemptID ackAttemptID = new ExecutionAttemptID();
            final ExecutionAttemptID commitAttemptID = new ExecutionAttemptID();
            ExecutionVertex triggerVertex = CheckpointCoordinatorTest.mockExecutionVertex(triggerAttemptID);
            ExecutionVertex ackVertex = CheckpointCoordinatorTest.mockExecutionVertex(ackAttemptID);
            ExecutionVertex commitVertex = CheckpointCoordinatorTest.mockExecutionVertex(commitAttemptID);
            final AtomicReference<ExecutionState> currentState = new AtomicReference(ExecutionState.CREATED);
            Mockito.when(triggerVertex.getCurrentExecutionAttempt().getState()).thenAnswer(( invocation) -> currentState.get());
            CheckpointCoordinator coord = // periodic interval is 10 ms
            // timeout is very long (200 s)
            // no extra delay
            // max two concurrent checkpoints
            new CheckpointCoordinator(jid, 10, 200000, 0L, 2, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ triggerVertex }, new ExecutionVertex[]{ ackVertex }, new ExecutionVertex[]{ commitVertex }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(2), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            coord.startCheckpointScheduler();
            // no checkpoint should have started so far
            Thread.sleep(200);
            Assert.assertEquals(0, coord.getNumberOfPendingCheckpoints());
            // now move the state to RUNNING
            currentState.set(RUNNING);
            // the coordinator should start checkpointing now
            final long timeout = (System.currentTimeMillis()) + 10000;
            do {
                Thread.sleep(20);
            } while (((System.currentTimeMillis()) < timeout) && ((coord.getNumberOfPendingCheckpoints()) == 0) );
            Assert.assertTrue(((coord.getNumberOfPendingCheckpoints()) > 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests that the savepoints can be triggered concurrently.
     */
    @Test
    public void testConcurrentSavepoints() throws Exception {
        JobID jobId = new JobID();
        final ExecutionAttemptID attemptID1 = new ExecutionAttemptID();
        ExecutionVertex vertex1 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID1);
        StandaloneCheckpointIDCounter checkpointIDCounter = new StandaloneCheckpointIDCounter();
        CheckpointCoordinator coord = // max one checkpoint at a time => should not affect savepoints
        new CheckpointCoordinator(jobId, 100000, 200000, 0L, 1, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, checkpointIDCounter, new StandaloneCompletedCheckpointStore(2), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        List<CompletableFuture<CompletedCheckpoint>> savepointFutures = new ArrayList<>();
        int numSavepoints = 5;
        String savepointDir = tmpFolder.newFolder().getAbsolutePath();
        // Trigger savepoints
        for (int i = 0; i < numSavepoints; i++) {
            savepointFutures.add(coord.triggerSavepoint(i, savepointDir));
        }
        // After triggering multiple savepoints, all should in progress
        for (CompletableFuture<CompletedCheckpoint> savepointFuture : savepointFutures) {
            Assert.assertFalse(savepointFuture.isDone());
        }
        // ACK all savepoints
        long checkpointId = checkpointIDCounter.getLast();
        for (int i = 0; i < numSavepoints; i++ , checkpointId--) {
            coord.receiveAcknowledgeMessage(new AcknowledgeCheckpoint(jobId, attemptID1, checkpointId));
        }
        // After ACKs, all should be completed
        for (CompletableFuture<CompletedCheckpoint> savepointFuture : savepointFutures) {
            Assert.assertTrue(savepointFuture.isDone());
        }
    }

    /**
     * Tests that no minimum delay between savepoints is enforced.
     */
    @Test
    public void testMinDelayBetweenSavepoints() throws Exception {
        JobID jobId = new JobID();
        final ExecutionAttemptID attemptID1 = new ExecutionAttemptID();
        ExecutionVertex vertex1 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID1);
        CheckpointCoordinator coord = // very long min delay => should not affect savepoints
        new CheckpointCoordinator(jobId, 100000, 200000, 100000000L, 1, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(2), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        String savepointDir = tmpFolder.newFolder().getAbsolutePath();
        CompletableFuture<CompletedCheckpoint> savepoint0 = coord.triggerSavepoint(0, savepointDir);
        Assert.assertFalse("Did not trigger savepoint", savepoint0.isDone());
        CompletableFuture<CompletedCheckpoint> savepoint1 = coord.triggerSavepoint(1, savepointDir);
        Assert.assertFalse("Did not trigger savepoint", savepoint1.isDone());
    }

    /**
     * Tests that the checkpointed partitioned and non-partitioned state is assigned properly to
     * the {@link Execution} upon recovery.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRestoreLatestCheckpointedState() throws Exception {
        final JobID jid = new JobID();
        final long timestamp = System.currentTimeMillis();
        final JobVertexID jobVertexID1 = new JobVertexID();
        final JobVertexID jobVertexID2 = new JobVertexID();
        int parallelism1 = 3;
        int parallelism2 = 2;
        int maxParallelism1 = 42;
        int maxParallelism2 = 13;
        final ExecutionJobVertex jobVertex1 = CheckpointCoordinatorTest.mockExecutionJobVertex(jobVertexID1, parallelism1, maxParallelism1);
        final ExecutionJobVertex jobVertex2 = CheckpointCoordinatorTest.mockExecutionJobVertex(jobVertexID2, parallelism2, maxParallelism2);
        List<ExecutionVertex> allExecutionVertices = new ArrayList<>((parallelism1 + parallelism2));
        allExecutionVertices.addAll(Arrays.asList(jobVertex1.getTaskVertices()));
        allExecutionVertices.addAll(Arrays.asList(jobVertex2.getTaskVertices()));
        ExecutionVertex[] arrayExecutionVertices = allExecutionVertices.toArray(new ExecutionVertex[allExecutionVertices.size()]);
        CompletedCheckpointStore store = new RecoverableCompletedCheckpointStore();
        // set up the coordinator and validate the initial state
        CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, arrayExecutionVertices, arrayExecutionVertices, arrayExecutionVertices, new StandaloneCheckpointIDCounter(), store, new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        // trigger the checkpoint
        coord.triggerCheckpoint(timestamp, false);
        Assert.assertTrue(((coord.getPendingCheckpoints().keySet().size()) == 1));
        long checkpointId = Iterables.getOnlyElement(coord.getPendingCheckpoints().keySet());
        List<KeyGroupRange> keyGroupPartitions1 = StateAssignmentOperation.createKeyGroupPartitions(maxParallelism1, parallelism1);
        List<KeyGroupRange> keyGroupPartitions2 = StateAssignmentOperation.createKeyGroupPartitions(maxParallelism2, parallelism2);
        for (int index = 0; index < (jobVertex1.getParallelism()); index++) {
            TaskStateSnapshot subtaskState = CheckpointCoordinatorTest.mockSubtaskState(jobVertexID1, index, keyGroupPartitions1.get(index));
            AcknowledgeCheckpoint acknowledgeCheckpoint = new AcknowledgeCheckpoint(jid, jobVertex1.getTaskVertices()[index].getCurrentExecutionAttempt().getAttemptId(), checkpointId, new CheckpointMetrics(), subtaskState);
            coord.receiveAcknowledgeMessage(acknowledgeCheckpoint);
        }
        for (int index = 0; index < (jobVertex2.getParallelism()); index++) {
            TaskStateSnapshot subtaskState = CheckpointCoordinatorTest.mockSubtaskState(jobVertexID2, index, keyGroupPartitions2.get(index));
            AcknowledgeCheckpoint acknowledgeCheckpoint = new AcknowledgeCheckpoint(jid, jobVertex2.getTaskVertices()[index].getCurrentExecutionAttempt().getAttemptId(), checkpointId, new CheckpointMetrics(), subtaskState);
            coord.receiveAcknowledgeMessage(acknowledgeCheckpoint);
        }
        List<CompletedCheckpoint> completedCheckpoints = coord.getSuccessfulCheckpoints();
        Assert.assertEquals(1, completedCheckpoints.size());
        // shutdown the store
        store.shutdown(SUSPENDED);
        // restore the store
        Map<JobVertexID, ExecutionJobVertex> tasks = new HashMap<>();
        tasks.put(jobVertexID1, jobVertex1);
        tasks.put(jobVertexID2, jobVertex2);
        coord.restoreLatestCheckpointedState(tasks, true, false);
        // validate that all shared states are registered again after the recovery.
        for (CompletedCheckpoint completedCheckpoint : completedCheckpoints) {
            for (OperatorState taskState : completedCheckpoint.getOperatorStates().values()) {
                for (OperatorSubtaskState subtaskState : taskState.getStates()) {
                    Mockito.verify(subtaskState, Mockito.times(2)).registerSharedStates(ArgumentMatchers.any(SharedStateRegistry.class));
                }
            }
        }
        // verify the restored state
        CheckpointCoordinatorTest.verifyStateRestore(jobVertexID1, jobVertex1, keyGroupPartitions1);
        CheckpointCoordinatorTest.verifyStateRestore(jobVertexID2, jobVertex2, keyGroupPartitions2);
    }

    /**
     * Tests that the checkpoint restoration fails if the max parallelism of the job vertices has
     * changed.
     *
     * @throws Exception
     * 		
     */
    @Test(expected = IllegalStateException.class)
    public void testRestoreLatestCheckpointFailureWhenMaxParallelismChanges() throws Exception {
        final JobID jid = new JobID();
        final long timestamp = System.currentTimeMillis();
        final JobVertexID jobVertexID1 = new JobVertexID();
        final JobVertexID jobVertexID2 = new JobVertexID();
        int parallelism1 = 3;
        int parallelism2 = 2;
        int maxParallelism1 = 42;
        int maxParallelism2 = 13;
        final ExecutionJobVertex jobVertex1 = CheckpointCoordinatorTest.mockExecutionJobVertex(jobVertexID1, parallelism1, maxParallelism1);
        final ExecutionJobVertex jobVertex2 = CheckpointCoordinatorTest.mockExecutionJobVertex(jobVertexID2, parallelism2, maxParallelism2);
        List<ExecutionVertex> allExecutionVertices = new ArrayList<>((parallelism1 + parallelism2));
        allExecutionVertices.addAll(Arrays.asList(jobVertex1.getTaskVertices()));
        allExecutionVertices.addAll(Arrays.asList(jobVertex2.getTaskVertices()));
        ExecutionVertex[] arrayExecutionVertices = allExecutionVertices.toArray(new ExecutionVertex[allExecutionVertices.size()]);
        // set up the coordinator and validate the initial state
        CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, arrayExecutionVertices, arrayExecutionVertices, arrayExecutionVertices, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        // trigger the checkpoint
        coord.triggerCheckpoint(timestamp, false);
        Assert.assertTrue(((coord.getPendingCheckpoints().keySet().size()) == 1));
        long checkpointId = Iterables.getOnlyElement(coord.getPendingCheckpoints().keySet());
        List<KeyGroupRange> keyGroupPartitions1 = StateAssignmentOperation.createKeyGroupPartitions(maxParallelism1, parallelism1);
        List<KeyGroupRange> keyGroupPartitions2 = StateAssignmentOperation.createKeyGroupPartitions(maxParallelism2, parallelism2);
        for (int index = 0; index < (jobVertex1.getParallelism()); index++) {
            KeyGroupsStateHandle keyGroupState = CheckpointCoordinatorTest.generateKeyGroupState(jobVertexID1, keyGroupPartitions1.get(index), false);
            OperatorSubtaskState operatorSubtaskState = new OperatorSubtaskState(null, null, keyGroupState, null);
            TaskStateSnapshot taskOperatorSubtaskStates = new TaskStateSnapshot();
            taskOperatorSubtaskStates.putSubtaskStateByOperatorID(OperatorID.fromJobVertexID(jobVertexID1), operatorSubtaskState);
            AcknowledgeCheckpoint acknowledgeCheckpoint = new AcknowledgeCheckpoint(jid, jobVertex1.getTaskVertices()[index].getCurrentExecutionAttempt().getAttemptId(), checkpointId, new CheckpointMetrics(), taskOperatorSubtaskStates);
            coord.receiveAcknowledgeMessage(acknowledgeCheckpoint);
        }
        for (int index = 0; index < (jobVertex2.getParallelism()); index++) {
            KeyGroupsStateHandle keyGroupState = CheckpointCoordinatorTest.generateKeyGroupState(jobVertexID2, keyGroupPartitions2.get(index), false);
            OperatorSubtaskState operatorSubtaskState = new OperatorSubtaskState(null, null, keyGroupState, null);
            TaskStateSnapshot taskOperatorSubtaskStates = new TaskStateSnapshot();
            taskOperatorSubtaskStates.putSubtaskStateByOperatorID(OperatorID.fromJobVertexID(jobVertexID2), operatorSubtaskState);
            AcknowledgeCheckpoint acknowledgeCheckpoint = new AcknowledgeCheckpoint(jid, jobVertex2.getTaskVertices()[index].getCurrentExecutionAttempt().getAttemptId(), checkpointId, new CheckpointMetrics(), taskOperatorSubtaskStates);
            coord.receiveAcknowledgeMessage(acknowledgeCheckpoint);
        }
        List<CompletedCheckpoint> completedCheckpoints = coord.getSuccessfulCheckpoints();
        Assert.assertEquals(1, completedCheckpoints.size());
        Map<JobVertexID, ExecutionJobVertex> tasks = new HashMap<>();
        int newMaxParallelism1 = 20;
        int newMaxParallelism2 = 42;
        final ExecutionJobVertex newJobVertex1 = CheckpointCoordinatorTest.mockExecutionJobVertex(jobVertexID1, parallelism1, newMaxParallelism1);
        final ExecutionJobVertex newJobVertex2 = CheckpointCoordinatorTest.mockExecutionJobVertex(jobVertexID2, parallelism2, newMaxParallelism2);
        tasks.put(jobVertexID1, newJobVertex1);
        tasks.put(jobVertexID2, newJobVertex2);
        coord.restoreLatestCheckpointedState(tasks, true, false);
        Assert.fail("The restoration should have failed because the max parallelism changed.");
    }

    @Test
    public void testRestoreLatestCheckpointedStateScaleIn() throws Exception {
        testRestoreLatestCheckpointedStateWithChangingParallelism(false);
    }

    @Test
    public void testRestoreLatestCheckpointedStateScaleOut() throws Exception {
        testRestoreLatestCheckpointedStateWithChangingParallelism(true);
    }

    @Test
    public void testStateRecoveryWhenTopologyChangeOut() throws Exception {
        testStateRecoveryWithTopologyChange(0);
    }

    @Test
    public void testStateRecoveryWhenTopologyChangeIn() throws Exception {
        testStateRecoveryWithTopologyChange(1);
    }

    @Test
    public void testStateRecoveryWhenTopologyChange() throws Exception {
        testStateRecoveryWithTopologyChange(2);
    }

    /**
     * Tests that the externalized checkpoint configuration is respected.
     */
    @Test
    public void testExternalizedCheckpoints() throws Exception {
        try {
            final JobID jid = new JobID();
            final long timestamp = System.currentTimeMillis();
            // create some mock Execution vertices that receive the checkpoint trigger messages
            final ExecutionAttemptID attemptID1 = new ExecutionAttemptID();
            ExecutionVertex vertex1 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID1);
            // set up the coordinator and validate the initial state
            CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, RETAIN_ON_FAILURE, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
            Assert.assertTrue(coord.triggerCheckpoint(timestamp, false));
            for (PendingCheckpoint checkpoint : coord.getPendingCheckpoints().values()) {
                CheckpointProperties props = checkpoint.getProps();
                CheckpointProperties expected = CheckpointProperties.forCheckpoint(RETAIN_ON_FAILURE);
                Assert.assertEquals(expected, props);
            }
            // the now we should have a completed checkpoint
            coord.shutdown(FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCreateKeyGroupPartitions() {
        testCreateKeyGroupPartitions(1, 1);
        testCreateKeyGroupPartitions(13, 1);
        testCreateKeyGroupPartitions(13, 2);
        testCreateKeyGroupPartitions(Short.MAX_VALUE, 1);
        testCreateKeyGroupPartitions(Short.MAX_VALUE, 13);
        testCreateKeyGroupPartitions(Short.MAX_VALUE, Short.MAX_VALUE);
        Random r = new Random(1234);
        for (int k = 0; k < 1000; ++k) {
            int maxParallelism = 1 + (r.nextInt(((Short.MAX_VALUE) - 1)));
            int parallelism = 1 + (r.nextInt(maxParallelism));
            testCreateKeyGroupPartitions(maxParallelism, parallelism);
        }
    }

    @Test
    public void testStopPeriodicScheduler() throws Exception {
        // create some mock Execution vertices that receive the checkpoint trigger messages
        final ExecutionAttemptID attemptID1 = new ExecutionAttemptID();
        ExecutionVertex vertex1 = CheckpointCoordinatorTest.mockExecutionVertex(attemptID1);
        // set up the coordinator and validate the initial state
        CheckpointCoordinator coord = new CheckpointCoordinator(new JobID(), 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        // Periodic
        CheckpointTriggerResult triggerResult = coord.triggerCheckpoint(System.currentTimeMillis(), CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION), null, true);
        Assert.assertTrue(triggerResult.isFailure());
        Assert.assertEquals(PERIODIC_SCHEDULER_SHUTDOWN, triggerResult.getFailureReason());
        // Not periodic
        triggerResult = coord.triggerCheckpoint(System.currentTimeMillis(), CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION), null, false);
        Assert.assertFalse(triggerResult.isFailure());
    }

    @Test
    public void testPartitionableStateRepartitioning() {
        Random r = new Random(42);
        for (int run = 0; run < 10000; ++run) {
            int oldParallelism = 1 + (r.nextInt(9));
            int newParallelism = 1 + (r.nextInt(9));
            int numNamedStates = 1 + (r.nextInt(9));
            int maxPartitionsPerState = 1 + (r.nextInt(9));
            doTestPartitionableStateRepartitioning(r, oldParallelism, newParallelism, numNamedStates, maxPartitionsPerState);
        }
    }

    /**
     * Tests that the pending checkpoint stats callbacks are created.
     */
    @Test
    public void testCheckpointStatsTrackerPendingCheckpointCallback() {
        final long timestamp = System.currentTimeMillis();
        ExecutionVertex vertex1 = CheckpointCoordinatorTest.mockExecutionVertex(new ExecutionAttemptID());
        // set up the coordinator and validate the initial state
        CheckpointCoordinator coord = new CheckpointCoordinator(new JobID(), 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, new StandaloneCheckpointIDCounter(), new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        CheckpointStatsTracker tracker = Mockito.mock(CheckpointStatsTracker.class);
        coord.setCheckpointStatsTracker(tracker);
        Mockito.when(tracker.reportPendingCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(CheckpointProperties.class))).thenReturn(Mockito.mock(PendingCheckpointStats.class));
        // Trigger a checkpoint and verify callback
        Assert.assertTrue(coord.triggerCheckpoint(timestamp, false));
        Mockito.verify(tracker, Mockito.times(1)).reportPendingCheckpoint(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(timestamp), ArgumentMatchers.eq(CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION)));
    }

    /**
     * Tests that the restore callbacks are called if registered.
     */
    @Test
    public void testCheckpointStatsTrackerRestoreCallback() throws Exception {
        ExecutionVertex vertex1 = CheckpointCoordinatorTest.mockExecutionVertex(new ExecutionAttemptID());
        StandaloneCompletedCheckpointStore store = new StandaloneCompletedCheckpointStore(1);
        // set up the coordinator and validate the initial state
        CheckpointCoordinator coord = new CheckpointCoordinator(new JobID(), 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, new ExecutionVertex[]{ vertex1 }, new StandaloneCheckpointIDCounter(), store, new MemoryStateBackend(), Executors.directExecutor(), SharedStateRegistry.DEFAULT_FACTORY);
        store.addCheckpoint(new CompletedCheckpoint(new JobID(), 0, 0, 0, Collections.<OperatorID, OperatorState>emptyMap(), Collections.<MasterState>emptyList(), CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION), new TestCompletedCheckpointStorageLocation()));
        CheckpointStatsTracker tracker = Mockito.mock(CheckpointStatsTracker.class);
        coord.setCheckpointStatsTracker(tracker);
        Assert.assertTrue(coord.restoreLatestCheckpointedState(Collections.<JobVertexID, ExecutionJobVertex>emptyMap(), false, true));
        Mockito.verify(tracker, Mockito.times(1)).reportRestoredCheckpoint(ArgumentMatchers.any(RestoredCheckpointStats.class));
    }

    @Test
    public void testSharedStateRegistrationOnRestore() throws Exception {
        final JobID jid = new JobID();
        final long timestamp = System.currentTimeMillis();
        final JobVertexID jobVertexID1 = new JobVertexID();
        int parallelism1 = 2;
        int maxParallelism1 = 4;
        final ExecutionJobVertex jobVertex1 = CheckpointCoordinatorTest.mockExecutionJobVertex(jobVertexID1, parallelism1, maxParallelism1);
        List<ExecutionVertex> allExecutionVertices = new ArrayList<>(parallelism1);
        allExecutionVertices.addAll(Arrays.asList(jobVertex1.getTaskVertices()));
        ExecutionVertex[] arrayExecutionVertices = allExecutionVertices.toArray(new ExecutionVertex[allExecutionVertices.size()]);
        RecoverableCompletedCheckpointStore store = new RecoverableCompletedCheckpointStore(10);
        final List<SharedStateRegistry> createdSharedStateRegistries = new ArrayList<>(2);
        // set up the coordinator and validate the initial state
        CheckpointCoordinator coord = new CheckpointCoordinator(jid, 600000, 600000, 0, Integer.MAX_VALUE, NEVER_RETAIN_AFTER_TERMINATION, arrayExecutionVertices, arrayExecutionVertices, arrayExecutionVertices, new StandaloneCheckpointIDCounter(), store, new MemoryStateBackend(), Executors.directExecutor(), ( deleteExecutor) -> {
            SharedStateRegistry instance = new SharedStateRegistry(deleteExecutor);
            createdSharedStateRegistries.add(instance);
            return instance;
        });
        final int numCheckpoints = 3;
        List<KeyGroupRange> keyGroupPartitions1 = StateAssignmentOperation.createKeyGroupPartitions(maxParallelism1, parallelism1);
        for (int i = 0; i < numCheckpoints; ++i) {
            performIncrementalCheckpoint(jid, coord, jobVertex1, keyGroupPartitions1, (timestamp + i), i);
        }
        List<CompletedCheckpoint> completedCheckpoints = coord.getSuccessfulCheckpoints();
        Assert.assertEquals(numCheckpoints, completedCheckpoints.size());
        int sharedHandleCount = 0;
        List<Map<StateHandleID, StreamStateHandle>> sharedHandlesByCheckpoint = new ArrayList<>(numCheckpoints);
        for (int i = 0; i < numCheckpoints; ++i) {
            sharedHandlesByCheckpoint.add(new HashMap(2));
        }
        int cp = 0;
        for (CompletedCheckpoint completedCheckpoint : completedCheckpoints) {
            for (OperatorState taskState : completedCheckpoint.getOperatorStates().values()) {
                for (OperatorSubtaskState subtaskState : taskState.getStates()) {
                    for (KeyedStateHandle keyedStateHandle : subtaskState.getManagedKeyedState()) {
                        // test we are once registered with the current registry
                        Mockito.verify(keyedStateHandle, Mockito.times(1)).registerSharedStates(createdSharedStateRegistries.get(0));
                        IncrementalRemoteKeyedStateHandle incrementalKeyedStateHandle = ((IncrementalRemoteKeyedStateHandle) (keyedStateHandle));
                        sharedHandlesByCheckpoint.get(cp).putAll(incrementalKeyedStateHandle.getSharedState());
                        for (StreamStateHandle streamStateHandle : incrementalKeyedStateHandle.getSharedState().values()) {
                            Assert.assertTrue((!(streamStateHandle instanceof PlaceholderStreamStateHandle)));
                            Mockito.verify(streamStateHandle, Mockito.never()).discardState();
                            ++sharedHandleCount;
                        }
                        for (StreamStateHandle streamStateHandle : incrementalKeyedStateHandle.getPrivateState().values()) {
                            Mockito.verify(streamStateHandle, Mockito.never()).discardState();
                        }
                        Mockito.verify(incrementalKeyedStateHandle.getMetaStateHandle(), Mockito.never()).discardState();
                    }
                    Mockito.verify(subtaskState, Mockito.never()).discardState();
                }
            }
            ++cp;
        }
        // 2 (parallelism) x (1 (CP0) + 2 (CP1) + 2 (CP2)) = 10
        Assert.assertEquals(10, sharedHandleCount);
        // discard CP0
        store.removeOldestCheckpoint();
        // we expect no shared state was discarded because the state of CP0 is still referenced by CP1
        for (Map<StateHandleID, StreamStateHandle> cpList : sharedHandlesByCheckpoint) {
            for (StreamStateHandle streamStateHandle : cpList.values()) {
                Mockito.verify(streamStateHandle, Mockito.never()).discardState();
            }
        }
        // shutdown the store
        store.shutdown(SUSPENDED);
        // restore the store
        Map<JobVertexID, ExecutionJobVertex> tasks = new HashMap<>();
        tasks.put(jobVertexID1, jobVertex1);
        coord.restoreLatestCheckpointedState(tasks, true, false);
        // validate that all shared states are registered again after the recovery.
        cp = 0;
        for (CompletedCheckpoint completedCheckpoint : completedCheckpoints) {
            for (OperatorState taskState : completedCheckpoint.getOperatorStates().values()) {
                for (OperatorSubtaskState subtaskState : taskState.getStates()) {
                    for (KeyedStateHandle keyedStateHandle : subtaskState.getManagedKeyedState()) {
                        VerificationMode verificationMode;
                        // test we are once registered with the new registry
                        if (cp > 0) {
                            verificationMode = Mockito.times(1);
                        } else {
                            verificationMode = Mockito.never();
                        }
                        // check that all are registered with the new registry
                        Mockito.verify(keyedStateHandle, verificationMode).registerSharedStates(createdSharedStateRegistries.get(1));
                    }
                }
            }
            ++cp;
        }
        // discard CP1
        store.removeOldestCheckpoint();
        // we expect that all shared state from CP0 is no longer referenced and discarded. CP2 is still live and also
        // references the state from CP1, so we expect they are not discarded.
        for (Map<StateHandleID, StreamStateHandle> cpList : sharedHandlesByCheckpoint) {
            for (Map.Entry<StateHandleID, StreamStateHandle> entry : cpList.entrySet()) {
                String key = entry.getKey().getKeyString();
                int belongToCP = Integer.parseInt(String.valueOf(key.charAt(((key.length()) - 1))));
                if (belongToCP == 0) {
                    Mockito.verify(entry.getValue(), Mockito.times(1)).discardState();
                } else {
                    Mockito.verify(entry.getValue(), Mockito.never()).discardState();
                }
            }
        }
        // discard CP2
        store.removeOldestCheckpoint();
        // we expect all shared state was discarded now, because all CPs are
        for (Map<StateHandleID, StreamStateHandle> cpList : sharedHandlesByCheckpoint) {
            for (StreamStateHandle streamStateHandle : cpList.values()) {
                Mockito.verify(streamStateHandle, Mockito.times(1)).discardState();
            }
        }
    }
}

