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
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.executiongraph.ExecutionJobVertex;
import org.apache.flink.runtime.executiongraph.ExecutionVertex;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.state.SharedStateRegistry;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static CheckpointType.CHECKPOINT;
import static CheckpointType.SAVEPOINT;


/**
 * Tests for the {@link PendingCheckpoint}.
 */
public class PendingCheckpointTest {
    private static final Map<ExecutionAttemptID, ExecutionVertex> ACK_TASKS = new HashMap<>();

    private static final ExecutionAttemptID ATTEMPT_ID = new ExecutionAttemptID();

    static {
        ExecutionJobVertex jobVertex = Mockito.mock(ExecutionJobVertex.class);
        when(jobVertex.getOperatorIDs()).thenReturn(Collections.singletonList(new OperatorID()));
        ExecutionVertex vertex = Mockito.mock(ExecutionVertex.class);
        when(vertex.getMaxParallelism()).thenReturn(128);
        when(vertex.getTotalNumberOfParallelSubtasks()).thenReturn(1);
        when(vertex.getJobVertex()).thenReturn(jobVertex);
        PendingCheckpointTest.ACK_TASKS.put(PendingCheckpointTest.ATTEMPT_ID, vertex);
    }

    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    /**
     * Tests that pending checkpoints can be subsumed iff they are forced.
     */
    @Test
    public void testCanBeSubsumed() throws Exception {
        // Forced checkpoints cannot be subsumed
        CheckpointProperties forced = new CheckpointProperties(true, SAVEPOINT, false, false, false, false, false);
        PendingCheckpoint pending = createPendingCheckpoint(forced);
        Assert.assertFalse(pending.canBeSubsumed());
        try {
            pending.abortSubsumed();
            Assert.fail("Did not throw expected Exception");
        } catch (IllegalStateException ignored) {
            // Expected
        }
        // Non-forced checkpoints can be subsumed
        CheckpointProperties subsumed = new CheckpointProperties(false, SAVEPOINT, false, false, false, false, false);
        pending = createPendingCheckpoint(subsumed);
        Assert.assertTrue(pending.canBeSubsumed());
    }

    /**
     * Tests that the completion future is succeeded on finalize and failed on
     * abort and failures during finalize.
     */
    @Test
    public void testCompletionFuture() throws Exception {
        CheckpointProperties props = new CheckpointProperties(false, SAVEPOINT, false, false, false, false, false);
        // Abort declined
        PendingCheckpoint pending = createPendingCheckpoint(props);
        CompletableFuture<CompletedCheckpoint> future = pending.getCompletionFuture();
        Assert.assertFalse(future.isDone());
        pending.abortDeclined();
        Assert.assertTrue(future.isDone());
        // Abort expired
        pending = createPendingCheckpoint(props);
        future = pending.getCompletionFuture();
        Assert.assertFalse(future.isDone());
        pending.abortExpired();
        Assert.assertTrue(future.isDone());
        // Abort subsumed
        pending = createPendingCheckpoint(props);
        future = pending.getCompletionFuture();
        Assert.assertFalse(future.isDone());
        pending.abortSubsumed();
        Assert.assertTrue(future.isDone());
        // Finalize (all ACK'd)
        pending = createPendingCheckpoint(props);
        future = pending.getCompletionFuture();
        Assert.assertFalse(future.isDone());
        pending.acknowledgeTask(PendingCheckpointTest.ATTEMPT_ID, null, new CheckpointMetrics());
        Assert.assertTrue(pending.isFullyAcknowledged());
        pending.finalizeCheckpoint();
        Assert.assertTrue(future.isDone());
        // Finalize (missing ACKs)
        pending = createPendingCheckpoint(props);
        future = pending.getCompletionFuture();
        Assert.assertFalse(future.isDone());
        try {
            pending.finalizeCheckpoint();
            Assert.fail("Did not throw expected Exception");
        } catch (IllegalStateException ignored) {
            // Expected
        }
    }

    /**
     * Tests that abort discards state.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testAbortDiscardsState() throws Exception {
        CheckpointProperties props = new CheckpointProperties(false, SAVEPOINT, false, false, false, false, false);
        PendingCheckpointTest.QueueExecutor executor = new PendingCheckpointTest.QueueExecutor();
        OperatorState state = Mockito.mock(OperatorState.class);
        Mockito.doNothing().when(state).registerSharedStates(ArgumentMatchers.any(SharedStateRegistry.class));
        // Abort declined
        PendingCheckpoint pending = createPendingCheckpoint(props, executor);
        PendingCheckpointTest.setTaskState(pending, state);
        pending.abortDeclined();
        // execute asynchronous discard operation
        executor.runQueuedCommands();
        Mockito.verify(state, Mockito.times(1)).discardState();
        // Abort error
        Mockito.reset(state);
        pending = createPendingCheckpoint(props, executor);
        PendingCheckpointTest.setTaskState(pending, state);
        pending.abortError(new Exception("Expected Test Exception"));
        // execute asynchronous discard operation
        executor.runQueuedCommands();
        Mockito.verify(state, Mockito.times(1)).discardState();
        // Abort expired
        Mockito.reset(state);
        pending = createPendingCheckpoint(props, executor);
        PendingCheckpointTest.setTaskState(pending, state);
        pending.abortExpired();
        // execute asynchronous discard operation
        executor.runQueuedCommands();
        Mockito.verify(state, Mockito.times(1)).discardState();
        // Abort subsumed
        Mockito.reset(state);
        pending = createPendingCheckpoint(props, executor);
        PendingCheckpointTest.setTaskState(pending, state);
        pending.abortSubsumed();
        // execute asynchronous discard operation
        executor.runQueuedCommands();
        Mockito.verify(state, Mockito.times(1)).discardState();
    }

    /**
     * Tests that the stats callbacks happen if the callback is registered.
     */
    @Test
    public void testPendingCheckpointStatsCallbacks() throws Exception {
        {
            // Complete successfully
            PendingCheckpointStats callback = Mockito.mock(PendingCheckpointStats.class);
            PendingCheckpoint pending = createPendingCheckpoint(CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION));
            pending.setStatsCallback(callback);
            pending.acknowledgeTask(PendingCheckpointTest.ATTEMPT_ID, null, new CheckpointMetrics());
            Mockito.verify(callback, Mockito.times(1)).reportSubtaskStats(ArgumentMatchers.nullable(JobVertexID.class), ArgumentMatchers.any(SubtaskStateStats.class));
            pending.finalizeCheckpoint();
            Mockito.verify(callback, Mockito.times(1)).reportCompletedCheckpoint(ArgumentMatchers.any(String.class));
        }
        {
            // Fail subsumed
            PendingCheckpointStats callback = Mockito.mock(PendingCheckpointStats.class);
            PendingCheckpoint pending = createPendingCheckpoint(CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION));
            pending.setStatsCallback(callback);
            pending.abortSubsumed();
            Mockito.verify(callback, Mockito.times(1)).reportFailedCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Exception.class));
        }
        {
            // Fail subsumed
            PendingCheckpointStats callback = Mockito.mock(PendingCheckpointStats.class);
            PendingCheckpoint pending = createPendingCheckpoint(CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION));
            pending.setStatsCallback(callback);
            pending.abortDeclined();
            Mockito.verify(callback, Mockito.times(1)).reportFailedCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Exception.class));
        }
        {
            // Fail subsumed
            PendingCheckpointStats callback = Mockito.mock(PendingCheckpointStats.class);
            PendingCheckpoint pending = createPendingCheckpoint(CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION));
            pending.setStatsCallback(callback);
            pending.abortError(new Exception("Expected test error"));
            Mockito.verify(callback, Mockito.times(1)).reportFailedCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Exception.class));
        }
        {
            // Fail subsumed
            PendingCheckpointStats callback = Mockito.mock(PendingCheckpointStats.class);
            PendingCheckpoint pending = createPendingCheckpoint(CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION));
            pending.setStatsCallback(callback);
            pending.abortExpired();
            Mockito.verify(callback, Mockito.times(1)).reportFailedCheckpoint(ArgumentMatchers.anyLong(), ArgumentMatchers.any(Exception.class));
        }
    }

    /**
     * FLINK-5985.
     *
     * <p>Ensures that subtasks that acknowledge their state as 'null' are considered stateless. This means that they
     * should not appear in the task states map of the checkpoint.
     */
    @Test
    public void testNullSubtaskStateLeadsToStatelessTask() throws Exception {
        PendingCheckpoint pending = createPendingCheckpoint(CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION));
        pending.acknowledgeTask(PendingCheckpointTest.ATTEMPT_ID, null, Mockito.mock(CheckpointMetrics.class));
        Assert.assertTrue(pending.getOperatorStates().isEmpty());
    }

    /**
     * FLINK-5985.
     *
     * <p>This tests checks the inverse of {@link #testNullSubtaskStateLeadsToStatelessTask()}. We want to test that
     * for subtasks that acknowledge some state are given an entry in the task states of the checkpoint.
     */
    @Test
    public void testNonNullSubtaskStateLeadsToStatefulTask() throws Exception {
        PendingCheckpoint pending = createPendingCheckpoint(CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION));
        pending.acknowledgeTask(PendingCheckpointTest.ATTEMPT_ID, Mockito.mock(TaskStateSnapshot.class), Mockito.mock(CheckpointMetrics.class));
        Assert.assertFalse(pending.getOperatorStates().isEmpty());
    }

    @Test
    public void testSetCanceller() throws Exception {
        final CheckpointProperties props = new CheckpointProperties(false, CHECKPOINT, true, true, true, true, true);
        PendingCheckpoint aborted = createPendingCheckpoint(props);
        aborted.abortDeclined();
        Assert.assertTrue(aborted.isDiscarded());
        Assert.assertFalse(aborted.setCancellerHandle(Mockito.mock(ScheduledFuture.class)));
        PendingCheckpoint pending = createPendingCheckpoint(props);
        ScheduledFuture<?> canceller = Mockito.mock(ScheduledFuture.class);
        Assert.assertTrue(pending.setCancellerHandle(canceller));
        pending.abortDeclined();
        Mockito.verify(canceller).cancel(false);
    }

    private static final class QueueExecutor implements Executor {
        private final Queue<Runnable> queue = new ArrayDeque<>(4);

        @Override
        public void execute(Runnable command) {
            queue.add(command);
        }

        public void runQueuedCommands() {
            for (Runnable runnable : queue) {
                runnable.run();
            }
        }
    }
}

