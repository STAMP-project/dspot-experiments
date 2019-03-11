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
package org.apache.flink.runtime.executiongraph;


import ExecutionState.CANCELING;
import ExecutionState.DEPLOYING;
import ExecutionState.FAILED;
import JobStatus.CANCELED;
import JobStatus.CANCELLING;
import JobStatus.FAILING;
import JobStatus.RUNNING;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.checkpoint.CheckpointCoordinator;
import org.apache.flink.runtime.checkpoint.CheckpointOptions;
import org.apache.flink.runtime.checkpoint.CheckpointRetentionPolicy;
import org.apache.flink.runtime.checkpoint.PendingCheckpoint;
import org.apache.flink.runtime.checkpoint.StandaloneCheckpointIDCounter;
import org.apache.flink.runtime.checkpoint.StandaloneCompletedCheckpointStore;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.deployment.TaskDeploymentDescriptor;
import org.apache.flink.runtime.execution.SuppressRestartsException;
import org.apache.flink.runtime.executiongraph.failover.FailoverRegion;
import org.apache.flink.runtime.executiongraph.failover.RestartIndividualStrategy;
import org.apache.flink.runtime.executiongraph.failover.RestartPipelinedRegionStrategy;
import org.apache.flink.runtime.executiongraph.utils.SimpleSlotProvider;
import org.apache.flink.runtime.jobgraph.tasks.CheckpointCoordinatorConfiguration;
import org.apache.flink.runtime.jobmanager.slots.TaskManagerGateway;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.metrics.groups.UnregisteredMetricGroups;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * These tests make sure that global failover (restart all) always takes precedence over
 * local recovery strategies.
 *
 * <p>This test must be in the package it resides in, because it uses package-private methods
 * from the ExecutionGraph classes.
 */
public class ConcurrentFailoverStrategyExecutionGraphTest extends TestLogger {
    private final TestingComponentMainThreadExecutorServiceAdapter mainThreadExecutor = TestingComponentMainThreadExecutorServiceAdapter.forMainThread();

    /**
     * Tests that a cancellation concurrent to a local failover leads to a properly
     * cancelled state.
     */
    @Test
    public void testCancelWhileInLocalFailover() throws Exception {
        // the logic in this test is as follows:
        // - start a job
        // - cause a task failure and delay the local recovery action via the manual executor
        // - cancel the job to go into cancelling
        // - resume in local recovery action
        // - validate that this does in fact not start a new task, because the graph as a
        // whole should now be cancelled already
        final JobID jid = new JobID();
        final int parallelism = 2;
        final SimpleSlotProvider slotProvider = new SimpleSlotProvider(jid, parallelism);
        final ExecutionGraph graph = createSampleGraph(jid, ConcurrentFailoverStrategyExecutionGraphTest.TestRestartPipelinedRegionStrategy::new, TestRestartStrategy.directExecuting(), slotProvider, parallelism);
        graph.start(mainThreadExecutor);
        ConcurrentFailoverStrategyExecutionGraphTest.TestRestartPipelinedRegionStrategy strategy = ((ConcurrentFailoverStrategyExecutionGraphTest.TestRestartPipelinedRegionStrategy) (graph.getFailoverStrategy()));
        // This future is used to block the failover strategy execution until we complete it
        final CompletableFuture<?> blocker = new CompletableFuture<>();
        strategy.setBlockerFuture(blocker);
        final ExecutionJobVertex ejv = graph.getVerticesTopologically().iterator().next();
        final ExecutionVertex vertex1 = ejv.getTaskVertices()[0];
        final ExecutionVertex vertex2 = ejv.getTaskVertices()[1];
        graph.scheduleForExecution();
        Assert.assertEquals(RUNNING, graph.getState());
        // let one of the vertices fail - that triggers a local recovery action
        vertex1.getCurrentExecutionAttempt().fail(new Exception("test failure"));
        Assert.assertEquals(FAILED, vertex1.getCurrentExecutionAttempt().getState());
        // graph should still be running and the failover recovery action should be queued
        Assert.assertEquals(RUNNING, graph.getState());
        // now cancel the job
        graph.cancel();
        Assert.assertEquals(CANCELLING, graph.getState());
        Assert.assertEquals(FAILED, vertex1.getCurrentExecutionAttempt().getState());
        Assert.assertEquals(CANCELING, vertex2.getCurrentExecutionAttempt().getState());
        // let the recovery action continue
        blocker.complete(null);
        // now report that cancelling is complete for the other vertex
        vertex2.getCurrentExecutionAttempt().completeCancelling();
        Assert.assertEquals(CANCELED, graph.getTerminationFuture().get());
        Assert.assertTrue(vertex1.getCurrentExecutionAttempt().getState().isTerminal());
        Assert.assertTrue(vertex2.getCurrentExecutionAttempt().getState().isTerminal());
        // make sure all slots are recycled
        Assert.assertEquals(parallelism, slotProvider.getNumberOfAvailableSlots());
    }

    /**
     * Tests that a terminal global failure concurrent to a local failover
     * leads to a properly failed state.
     */
    @Test
    public void testGlobalFailureConcurrentToLocalFailover() throws Exception {
        // the logic in this test is as follows:
        // - start a job
        // - cause a task failure and delay the local recovery action via the manual executor
        // - cause a global failure
        // - resume in local recovery action
        // - validate that this does in fact not start a new task, because the graph as a
        // whole should now be terminally failed already
        final JobID jid = new JobID();
        final int parallelism = 2;
        final SimpleSlotProvider slotProvider = new SimpleSlotProvider(jid, parallelism);
        final ExecutionGraph graph = createSampleGraph(jid, ConcurrentFailoverStrategyExecutionGraphTest.TestRestartPipelinedRegionStrategy::new, TestRestartStrategy.directExecuting(), slotProvider, parallelism);
        graph.start(mainThreadExecutor);
        ConcurrentFailoverStrategyExecutionGraphTest.TestRestartPipelinedRegionStrategy strategy = ((ConcurrentFailoverStrategyExecutionGraphTest.TestRestartPipelinedRegionStrategy) (graph.getFailoverStrategy()));
        // This future is used to block the failover strategy execution until we complete it
        final CompletableFuture<?> blocker = new CompletableFuture<>();
        strategy.setBlockerFuture(blocker);
        final ExecutionJobVertex ejv = graph.getVerticesTopologically().iterator().next();
        final ExecutionVertex vertex1 = ejv.getTaskVertices()[0];
        final ExecutionVertex vertex2 = ejv.getTaskVertices()[1];
        graph.scheduleForExecution();
        Assert.assertEquals(RUNNING, graph.getState());
        // let one of the vertices fail - that triggers a local recovery action
        vertex1.getCurrentExecutionAttempt().fail(new Exception("test failure"));
        Assert.assertEquals(FAILED, vertex1.getCurrentExecutionAttempt().getState());
        // graph should still be running and the failover recovery action should be queued
        Assert.assertEquals(RUNNING, graph.getState());
        // now cancel the job
        graph.failGlobal(new SuppressRestartsException(new Exception("test exception")));
        Assert.assertEquals(FAILING, graph.getState());
        Assert.assertEquals(FAILED, vertex1.getCurrentExecutionAttempt().getState());
        Assert.assertEquals(CANCELING, vertex2.getCurrentExecutionAttempt().getState());
        // let the recovery action continue
        blocker.complete(null);
        // now report that cancelling is complete for the other vertex
        vertex2.getCurrentExecutionAttempt().completeCancelling();
        Assert.assertEquals(JobStatus.FAILED, graph.getState());
        Assert.assertTrue(vertex1.getCurrentExecutionAttempt().getState().isTerminal());
        Assert.assertTrue(vertex2.getCurrentExecutionAttempt().getState().isTerminal());
        // make sure all slots are recycled
        Assert.assertEquals(parallelism, slotProvider.getNumberOfAvailableSlots());
    }

    /**
     * Tests that a local failover does not try to trump a global failover.
     */
    @Test
    public void testGlobalRecoveryConcurrentToLocalRecovery() throws Exception {
        // the logic in this test is as follows:
        // - start a job
        // - cause a task failure and delay the local recovery action via the manual executor
        // - cause a global failure that is recovering immediately
        // - resume in local recovery action
        // - validate that this does in fact not cause another task restart, because the global
        // recovery should already have restarted the task graph
        final JobID jid = new JobID();
        final int parallelism = 2;
        final SimpleSlotProvider slotProvider = new SimpleSlotProvider(jid, parallelism);
        final ExecutionGraph graph = // twice restart, no delay
        createSampleGraph(jid, ConcurrentFailoverStrategyExecutionGraphTest.TestRestartPipelinedRegionStrategy::new, new TestRestartStrategy(2, false), slotProvider, parallelism);
        ConcurrentFailoverStrategyExecutionGraphTest.TestRestartPipelinedRegionStrategy strategy = ((ConcurrentFailoverStrategyExecutionGraphTest.TestRestartPipelinedRegionStrategy) (graph.getFailoverStrategy()));
        // This future is used to block the failover strategy execution until we complete it
        CompletableFuture<?> blocker = new CompletableFuture<>();
        strategy.setBlockerFuture(blocker);
        graph.start(mainThreadExecutor);
        final ExecutionJobVertex ejv = graph.getVerticesTopologically().iterator().next();
        final ExecutionVertex vertex1 = ejv.getTaskVertices()[0];
        final ExecutionVertex vertex2 = ejv.getTaskVertices()[1];
        graph.scheduleForExecution();
        Assert.assertEquals(RUNNING, graph.getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(vertex1).getState());
        // let one of the vertices fail - that triggers a local recovery action
        vertex2.getCurrentExecutionAttempt().fail(new Exception("test failure"));
        Assert.assertEquals(FAILED, vertex2.getCurrentExecutionAttempt().getState());
        Assert.assertEquals(CANCELLING, strategy.getFailoverRegion(vertex2).getState());
        // graph should still be running and the failover recovery action should be queued
        Assert.assertEquals(RUNNING, graph.getState());
        // now cancel the job
        graph.failGlobal(new Exception("test exception"));
        Assert.assertEquals(FAILING, graph.getState());
        Assert.assertEquals(FAILED, vertex2.getCurrentExecutionAttempt().getState());
        Assert.assertEquals(CANCELING, vertex1.getCurrentExecutionAttempt().getState());
        // now report that cancelling is complete for the other vertex
        vertex1.getCurrentExecutionAttempt().completeCancelling();
        ExecutionGraphTestUtils.waitUntilJobStatus(graph, RUNNING, 1000);
        Assert.assertEquals(RUNNING, graph.getState());
        ExecutionGraphTestUtils.waitUntilExecutionState(vertex1.getCurrentExecutionAttempt(), DEPLOYING, 1000);
        ExecutionGraphTestUtils.waitUntilExecutionState(vertex2.getCurrentExecutionAttempt(), DEPLOYING, 1000);
        vertex1.getCurrentExecutionAttempt().switchToRunning();
        vertex2.getCurrentExecutionAttempt().switchToRunning();
        Assert.assertEquals(ExecutionState.RUNNING, vertex1.getCurrentExecutionAttempt().getState());
        Assert.assertEquals(ExecutionState.RUNNING, vertex2.getCurrentExecutionAttempt().getState());
        // let the recovery action continue - this should do nothing any more
        blocker.complete(null);
        // validate that the graph is still peachy
        Assert.assertEquals(RUNNING, graph.getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(vertex1).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(vertex2).getState());
        Assert.assertEquals(ExecutionState.RUNNING, vertex1.getCurrentExecutionAttempt().getState());
        Assert.assertEquals(ExecutionState.RUNNING, vertex2.getCurrentExecutionAttempt().getState());
        Assert.assertEquals(1, vertex1.getCurrentExecutionAttempt().getAttemptNumber());
        Assert.assertEquals(1, vertex2.getCurrentExecutionAttempt().getAttemptNumber());
        Assert.assertEquals(1, vertex1.getCopyOfPriorExecutionsList().size());
        Assert.assertEquals(1, vertex2.getCopyOfPriorExecutionsList().size());
        // make sure all slots are in use
        Assert.assertEquals(0, slotProvider.getNumberOfAvailableSlots());
        blocker = new CompletableFuture<>();
        strategy.setBlockerFuture(blocker);
        // validate that a task failure then can be handled by the local recovery
        vertex2.getCurrentExecutionAttempt().fail(new Exception("test failure"));
        // let the local recovery action continue - this should recover the vertex2
        blocker.complete(null);
        ExecutionGraphTestUtils.waitUntilExecutionState(vertex2.getCurrentExecutionAttempt(), DEPLOYING, 1000);
        vertex2.getCurrentExecutionAttempt().switchToRunning();
        // validate that the local recovery result
        Assert.assertEquals(RUNNING, graph.getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(vertex1).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(vertex2).getState());
        Assert.assertEquals(ExecutionState.RUNNING, vertex1.getCurrentExecutionAttempt().getState());
        Assert.assertEquals(ExecutionState.RUNNING, vertex2.getCurrentExecutionAttempt().getState());
        Assert.assertEquals(1, vertex1.getCurrentExecutionAttempt().getAttemptNumber());
        Assert.assertEquals(2, vertex2.getCurrentExecutionAttempt().getAttemptNumber());
        Assert.assertEquals(1, vertex1.getCopyOfPriorExecutionsList().size());
        Assert.assertEquals(2, vertex2.getCopyOfPriorExecutionsList().size());
        // make sure all slots are in use
        Assert.assertEquals(0, slotProvider.getNumberOfAvailableSlots());
    }

    /**
     * Tests that a local failure fails all pending checkpoints which have not been acknowledged by the failing
     * task.
     */
    @Test
    public void testLocalFailureFailsPendingCheckpoints() throws Exception {
        final JobID jid = new JobID();
        final int parallelism = 2;
        final long verifyTimeout = 5000L;
        final TaskManagerGateway taskManagerGateway = Mockito.mock(TaskManagerGateway.class);
        Mockito.when(taskManagerGateway.submitTask(ArgumentMatchers.any(TaskDeploymentDescriptor.class), ArgumentMatchers.any(Time.class))).thenReturn(CompletableFuture.completedFuture(Acknowledge.get()));
        Mockito.when(taskManagerGateway.cancelTask(ArgumentMatchers.any(ExecutionAttemptID.class), ArgumentMatchers.any(Time.class))).thenReturn(CompletableFuture.completedFuture(Acknowledge.get()));
        final SimpleSlotProvider slotProvider = new SimpleSlotProvider(jid, parallelism, taskManagerGateway);
        final CheckpointCoordinatorConfiguration checkpointCoordinatorConfiguration = new CheckpointCoordinatorConfiguration(10L, 100000L, 1L, 3, CheckpointRetentionPolicy.NEVER_RETAIN_AFTER_TERMINATION, true);
        final ExecutionGraph graph = // twice restart, no delay
        createSampleGraph(jid, ( eg) -> new RestartIndividualStrategy(eg) {
            @Override
            protected void performExecutionVertexRestart(ExecutionVertex vertexToRecover, long globalModVersion) {
            }
        }, new TestRestartStrategy(2, false), slotProvider, parallelism);
        graph.start(mainThreadExecutor);
        final List<ExecutionJobVertex> allVertices = new ArrayList(graph.getAllVertices().values());
        final StandaloneCheckpointIDCounter standaloneCheckpointIDCounter = new StandaloneCheckpointIDCounter();
        graph.enableCheckpointing(checkpointCoordinatorConfiguration.getCheckpointInterval(), checkpointCoordinatorConfiguration.getCheckpointTimeout(), checkpointCoordinatorConfiguration.getMinPauseBetweenCheckpoints(), checkpointCoordinatorConfiguration.getMaxConcurrentCheckpoints(), checkpointCoordinatorConfiguration.getCheckpointRetentionPolicy(), allVertices, allVertices, allVertices, Collections.emptyList(), standaloneCheckpointIDCounter, new StandaloneCompletedCheckpointStore(1), new MemoryStateBackend(), new org.apache.flink.runtime.checkpoint.CheckpointStatsTracker(1, allVertices, checkpointCoordinatorConfiguration, UnregisteredMetricGroups.createUnregisteredTaskMetricGroup()));
        final CheckpointCoordinator checkpointCoordinator = graph.getCheckpointCoordinator();
        final ExecutionJobVertex ejv = graph.getVerticesTopologically().iterator().next();
        final ExecutionVertex vertex1 = ejv.getTaskVertices()[0];
        final ExecutionVertex vertex2 = ejv.getTaskVertices()[1];
        graph.scheduleForExecution();
        Assert.assertEquals(RUNNING, graph.getState());
        Mockito.verify(taskManagerGateway, Mockito.timeout(verifyTimeout).times(parallelism)).submitTask(ArgumentMatchers.any(TaskDeploymentDescriptor.class), ArgumentMatchers.any(Time.class));
        // switch all executions to running
        for (ExecutionVertex executionVertex : graph.getAllExecutionVertices()) {
            executionVertex.getCurrentExecutionAttempt().switchToRunning();
        }
        // wait for a first checkpoint to be triggered
        Mockito.verify(taskManagerGateway, Mockito.timeout(verifyTimeout).times(3)).triggerCheckpoint(ArgumentMatchers.eq(vertex1.getCurrentExecutionAttempt().getAttemptId()), ArgumentMatchers.any(JobID.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(CheckpointOptions.class));
        Mockito.verify(taskManagerGateway, Mockito.timeout(verifyTimeout).times(3)).triggerCheckpoint(ArgumentMatchers.eq(vertex2.getCurrentExecutionAttempt().getAttemptId()), ArgumentMatchers.any(JobID.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(CheckpointOptions.class));
        Assert.assertEquals(3, checkpointCoordinator.getNumberOfPendingCheckpoints());
        long checkpointToAcknowledge = standaloneCheckpointIDCounter.getLast();
        checkpointCoordinator.receiveAcknowledgeMessage(new org.apache.flink.runtime.messages.checkpoint.AcknowledgeCheckpoint(graph.getJobID(), vertex1.getCurrentExecutionAttempt().getAttemptId(), checkpointToAcknowledge));
        Map<Long, PendingCheckpoint> oldPendingCheckpoints = new HashMap<>(3);
        for (PendingCheckpoint pendingCheckpoint : checkpointCoordinator.getPendingCheckpoints().values()) {
            Assert.assertFalse(pendingCheckpoint.isDiscarded());
            oldPendingCheckpoints.put(pendingCheckpoint.getCheckpointId(), pendingCheckpoint);
        }
        // let one of the vertices fail - this should trigger the failing of not acknowledged pending checkpoints
        vertex1.getCurrentExecutionAttempt().fail(new Exception("test failure"));
        for (PendingCheckpoint pendingCheckpoint : oldPendingCheckpoints.values()) {
            if ((pendingCheckpoint.getCheckpointId()) == checkpointToAcknowledge) {
                Assert.assertFalse(pendingCheckpoint.isDiscarded());
            } else {
                Assert.assertTrue(pendingCheckpoint.isDiscarded());
            }
        }
    }

    /**
     * Test implementation of the {@link RestartPipelinedRegionStrategy} that makes it possible to control when the
     * failover action is performed via {@link CompletableFuture}.
     */
    static class TestRestartPipelinedRegionStrategy extends RestartPipelinedRegionStrategy {
        @Nonnull
        CompletableFuture<?> blockerFuture;

        public TestRestartPipelinedRegionStrategy(ExecutionGraph executionGraph) {
            super(executionGraph);
            this.blockerFuture = CompletableFuture.completedFuture(null);
        }

        public void setBlockerFuture(@Nonnull
        CompletableFuture<?> blockerFuture) {
            this.blockerFuture = blockerFuture;
        }

        @Override
        protected FailoverRegion createFailoverRegion(ExecutionGraph eg, List<ExecutionVertex> connectedExecutions) {
            return new FailoverRegion(eg, connectedExecutions) {
                @Override
                protected CompletableFuture<Void> createTerminationFutureOverAllConnectedVertexes() {
                    ArrayList<CompletableFuture<?>> terminationAndBlocker = new ArrayList<>(2);
                    terminationAndBlocker.add(super.createTerminationFutureOverAllConnectedVertexes());
                    terminationAndBlocker.add(blockerFuture);
                    return FutureUtils.waitForAll(terminationAndBlocker);
                }
            };
        }
    }
}

