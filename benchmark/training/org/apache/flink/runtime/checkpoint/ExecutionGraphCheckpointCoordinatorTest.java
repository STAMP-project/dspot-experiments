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


import JobStatus.FAILED;
import JobStatus.FINISHED;
import JobStatus.SUSPENDED;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.runtime.execution.ExecutionState;
import org.apache.flink.runtime.executiongraph.Execution;
import org.apache.flink.runtime.executiongraph.ExecutionGraph;
import org.apache.flink.runtime.executiongraph.ExecutionVertex;
import org.apache.flink.runtime.jobgraph.JobStatus;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the interaction between the {@link ExecutionGraph} and the {@link CheckpointCoordinator}.
 */
public class ExecutionGraphCheckpointCoordinatorTest extends TestLogger {
    /**
     * Tests that the checkpoint coordinator is shut down if the execution graph
     * is failed.
     */
    @Test
    public void testShutdownCheckpointCoordinatorOnFailure() throws Exception {
        final CompletableFuture<JobStatus> counterShutdownFuture = new CompletableFuture<>();
        CheckpointIDCounter counter = new ExecutionGraphCheckpointCoordinatorTest.TestingCheckpointIDCounter(counterShutdownFuture);
        final CompletableFuture<JobStatus> storeShutdownFuture = new CompletableFuture<>();
        CompletedCheckpointStore store = new ExecutionGraphCheckpointCoordinatorTest.TestingCompletedCheckpointStore(storeShutdownFuture);
        ExecutionGraph graph = createExecutionGraphAndEnableCheckpointing(counter, store);
        final CheckpointCoordinator checkpointCoordinator = graph.getCheckpointCoordinator();
        Assert.assertThat(checkpointCoordinator, Matchers.notNullValue());
        Assert.assertThat(checkpointCoordinator.isShutdown(), Matchers.is(false));
        graph.failGlobal(new Exception("Test Exception"));
        Assert.assertThat(checkpointCoordinator.isShutdown(), Matchers.is(true));
        Assert.assertThat(counterShutdownFuture.get(), Matchers.is(FAILED));
        Assert.assertThat(storeShutdownFuture.get(), Matchers.is(FAILED));
    }

    /**
     * Tests that the checkpoint coordinator is shut down if the execution graph
     * is suspended.
     */
    @Test
    public void testShutdownCheckpointCoordinatorOnSuspend() throws Exception {
        final CompletableFuture<JobStatus> counterShutdownFuture = new CompletableFuture<>();
        CheckpointIDCounter counter = new ExecutionGraphCheckpointCoordinatorTest.TestingCheckpointIDCounter(counterShutdownFuture);
        final CompletableFuture<JobStatus> storeShutdownFuture = new CompletableFuture<>();
        CompletedCheckpointStore store = new ExecutionGraphCheckpointCoordinatorTest.TestingCompletedCheckpointStore(storeShutdownFuture);
        ExecutionGraph graph = createExecutionGraphAndEnableCheckpointing(counter, store);
        final CheckpointCoordinator checkpointCoordinator = graph.getCheckpointCoordinator();
        Assert.assertThat(checkpointCoordinator, Matchers.notNullValue());
        Assert.assertThat(checkpointCoordinator.isShutdown(), Matchers.is(false));
        graph.suspend(new Exception("Test Exception"));
        Assert.assertThat(checkpointCoordinator.isShutdown(), Matchers.is(true));
        Assert.assertThat(counterShutdownFuture.get(), Matchers.is(SUSPENDED));
        Assert.assertThat(storeShutdownFuture.get(), Matchers.is(SUSPENDED));
    }

    /**
     * Tests that the checkpoint coordinator is shut down if the execution graph
     * is finished.
     */
    @Test
    public void testShutdownCheckpointCoordinatorOnFinished() throws Exception {
        final CompletableFuture<JobStatus> counterShutdownFuture = new CompletableFuture<>();
        CheckpointIDCounter counter = new ExecutionGraphCheckpointCoordinatorTest.TestingCheckpointIDCounter(counterShutdownFuture);
        final CompletableFuture<JobStatus> storeShutdownFuture = new CompletableFuture<>();
        CompletedCheckpointStore store = new ExecutionGraphCheckpointCoordinatorTest.TestingCompletedCheckpointStore(storeShutdownFuture);
        ExecutionGraph graph = createExecutionGraphAndEnableCheckpointing(counter, store);
        final CheckpointCoordinator checkpointCoordinator = graph.getCheckpointCoordinator();
        Assert.assertThat(checkpointCoordinator, Matchers.notNullValue());
        Assert.assertThat(checkpointCoordinator.isShutdown(), Matchers.is(false));
        graph.scheduleForExecution();
        for (ExecutionVertex executionVertex : graph.getAllExecutionVertices()) {
            final Execution currentExecutionAttempt = executionVertex.getCurrentExecutionAttempt();
            graph.updateState(new org.apache.flink.runtime.taskmanager.TaskExecutionState(graph.getJobID(), currentExecutionAttempt.getAttemptId(), ExecutionState.FINISHED));
        }
        Assert.assertThat(graph.getTerminationFuture().get(), Matchers.is(FINISHED));
        Assert.assertThat(checkpointCoordinator.isShutdown(), Matchers.is(true));
        Assert.assertThat(counterShutdownFuture.get(), Matchers.is(FINISHED));
        Assert.assertThat(storeShutdownFuture.get(), Matchers.is(FINISHED));
    }

    private static final class TestingCheckpointIDCounter implements CheckpointIDCounter {
        private final CompletableFuture<JobStatus> shutdownStatus;

        private TestingCheckpointIDCounter(CompletableFuture<JobStatus> shutdownStatus) {
            this.shutdownStatus = shutdownStatus;
        }

        @Override
        public void start() {
        }

        @Override
        public void shutdown(JobStatus jobStatus) {
            shutdownStatus.complete(jobStatus);
        }

        @Override
        public long getAndIncrement() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public void setCount(long newId) {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }

    private static final class TestingCompletedCheckpointStore implements CompletedCheckpointStore {
        private final CompletableFuture<JobStatus> shutdownStatus;

        private TestingCompletedCheckpointStore(CompletableFuture<JobStatus> shutdownStatus) {
            this.shutdownStatus = shutdownStatus;
        }

        @Override
        public void recover() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public void addCheckpoint(CompletedCheckpoint checkpoint) {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public CompletedCheckpoint getLatestCheckpoint() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public void shutdown(JobStatus jobStatus) {
            shutdownStatus.complete(jobStatus);
        }

        @Override
        public List<CompletedCheckpoint> getAllCheckpoints() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public int getNumberOfRetainedCheckpoints() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public int getMaxNumberOfRetainedCheckpoints() {
            throw new UnsupportedOperationException("Not implemented.");
        }

        @Override
        public boolean requiresExternalizedCheckpoints() {
            throw new UnsupportedOperationException("Not implemented.");
        }
    }
}

