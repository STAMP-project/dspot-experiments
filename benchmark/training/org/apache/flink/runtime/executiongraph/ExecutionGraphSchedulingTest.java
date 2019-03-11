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


import DistributionPattern.ALL_TO_ALL;
import DistributionPattern.POINTWISE;
import ExecutionState.SCHEDULED;
import JobStatus.CANCELED;
import JobStatus.FAILED;
import JobStatus.RUNNING;
import ResultPartitionType.PIPELINED;
import ScheduleMode.EAGER;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.clusterframework.types.AllocationID;
import org.apache.flink.runtime.executiongraph.utils.ExecutionAttemptID;
import org.apache.flink.runtime.executiongraph.utils.SimpleAckingTaskManagerGateway;
import org.apache.flink.runtime.instance.SimpleSlot;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobmanager.slots.DummySlotOwner;
import org.apache.flink.runtime.jobmanager.slots.TestingSlotOwner;
import org.apache.flink.runtime.jobmaster.LogicalSlot;
import org.apache.flink.runtime.jobmaster.SlotRequestId;
import org.apache.flink.runtime.jobmaster.TestingLogicalSlot;
import org.apache.flink.runtime.testtasks.NoOpInvokable;
import org.apache.flink.runtime.testutils.DirectScheduledExecutorService;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the scheduling of the execution graph. This tests that
 * for example the order of deployments is correct and that bulk slot allocation
 * works properly.
 */
public class ExecutionGraphSchedulingTest extends TestLogger {
    private final ScheduledExecutorService executor = new DirectScheduledExecutorService();

    // ------------------------------------------------------------------------
    // Tests
    // ------------------------------------------------------------------------
    /**
     * Tests that with scheduling futures and pipelined deployment, the target vertex will
     * not deploy its task before the source vertex does.
     */
    @Test
    public void testScheduleSourceBeforeTarget() throws Exception {
        // [pipelined]
        // we construct a simple graph    (source) ----------------> (target)
        final int parallelism = 1;
        final JobVertex sourceVertex = new JobVertex("source");
        sourceVertex.setParallelism(parallelism);
        sourceVertex.setInvokableClass(NoOpInvokable.class);
        final JobVertex targetVertex = new JobVertex("target");
        targetVertex.setParallelism(parallelism);
        targetVertex.setInvokableClass(NoOpInvokable.class);
        targetVertex.connectNewDataSetAsInput(sourceVertex, ALL_TO_ALL, PIPELINED);
        final JobID jobId = new JobID();
        final JobGraph jobGraph = new JobGraph(jobId, "test", sourceVertex, targetVertex);
        final CompletableFuture<LogicalSlot> sourceFuture = new CompletableFuture<>();
        final CompletableFuture<LogicalSlot> targetFuture = new CompletableFuture<>();
        ProgrammedSlotProvider slotProvider = new ProgrammedSlotProvider(parallelism);
        slotProvider.addSlot(sourceVertex.getID(), 0, sourceFuture);
        slotProvider.addSlot(targetVertex.getID(), 0, targetFuture);
        final ExecutionGraph eg = createExecutionGraph(jobGraph, slotProvider);
        eg.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        // set up two TaskManager gateways and slots
        final InteractionsCountingTaskManagerGateway gatewaySource = ExecutionGraphSchedulingTest.createTaskManager();
        final InteractionsCountingTaskManagerGateway gatewayTarget = ExecutionGraphSchedulingTest.createTaskManager();
        final SimpleSlot sourceSlot = createSlot(gatewaySource, jobId);
        final SimpleSlot targetSlot = createSlot(gatewayTarget, jobId);
        eg.setScheduleMode(EAGER);
        eg.setQueuedSchedulingAllowed(true);
        eg.scheduleForExecution();
        // job should be running
        Assert.assertEquals(RUNNING, eg.getState());
        // we fulfill the target slot before the source slot
        // that should not cause a deployment or deployment related failure
        targetFuture.complete(targetSlot);
        Assert.assertThat(gatewayTarget.getSubmitTaskCount(), Matchers.is(0));
        Assert.assertEquals(RUNNING, eg.getState());
        // now supply the source slot
        sourceFuture.complete(sourceSlot);
        // by now, all deployments should have happened
        Assert.assertThat(gatewaySource.getSubmitTaskCount(), Matchers.is(1));
        Assert.assertThat(gatewayTarget.getSubmitTaskCount(), Matchers.is(1));
        Assert.assertEquals(RUNNING, eg.getState());
    }

    /**
     * This test verifies that before deploying a pipelined connected component, the
     * full set of slots is available, and that not some tasks are deployed, and later the
     * system realizes that not enough resources are available.
     */
    @Test
    public void testDeployPipelinedConnectedComponentsTogether() throws Exception {
        // [pipelined]
        // we construct a simple graph    (source) ----------------> (target)
        final int parallelism = 8;
        final JobVertex sourceVertex = new JobVertex("source");
        sourceVertex.setParallelism(parallelism);
        sourceVertex.setInvokableClass(NoOpInvokable.class);
        final JobVertex targetVertex = new JobVertex("target");
        targetVertex.setParallelism(parallelism);
        targetVertex.setInvokableClass(NoOpInvokable.class);
        targetVertex.connectNewDataSetAsInput(sourceVertex, ALL_TO_ALL, PIPELINED);
        final JobID jobId = new JobID();
        final JobGraph jobGraph = new JobGraph(jobId, "test", sourceVertex, targetVertex);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final CompletableFuture<LogicalSlot>[] sourceFutures = new CompletableFuture[parallelism];
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final CompletableFuture<LogicalSlot>[] targetFutures = new CompletableFuture[parallelism];
        // 
        // Create the slots, futures, and the slot provider
        final InteractionsCountingTaskManagerGateway[] sourceTaskManagers = new InteractionsCountingTaskManagerGateway[parallelism];
        final InteractionsCountingTaskManagerGateway[] targetTaskManagers = new InteractionsCountingTaskManagerGateway[parallelism];
        final SimpleSlot[] sourceSlots = new SimpleSlot[parallelism];
        final SimpleSlot[] targetSlots = new SimpleSlot[parallelism];
        for (int i = 0; i < parallelism; i++) {
            sourceTaskManagers[i] = ExecutionGraphSchedulingTest.createTaskManager();
            targetTaskManagers[i] = ExecutionGraphSchedulingTest.createTaskManager();
            sourceSlots[i] = createSlot(sourceTaskManagers[i], jobId);
            targetSlots[i] = createSlot(targetTaskManagers[i], jobId);
            sourceFutures[i] = new CompletableFuture();
            targetFutures[i] = new CompletableFuture();
        }
        ProgrammedSlotProvider slotProvider = new ProgrammedSlotProvider(parallelism);
        slotProvider.addSlots(sourceVertex.getID(), sourceFutures);
        slotProvider.addSlots(targetVertex.getID(), targetFutures);
        final ExecutionGraph eg = createExecutionGraph(jobGraph, slotProvider);
        // 
        // we complete some of the futures
        for (int i = 0; i < parallelism; i += 2) {
            sourceFutures[i].complete(sourceSlots[i]);
        }
        // 
        // kick off the scheduling
        eg.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        eg.setScheduleMode(EAGER);
        eg.setQueuedSchedulingAllowed(true);
        eg.scheduleForExecution();
        ExecutionGraphSchedulingTest.verifyNothingDeployed(eg, sourceTaskManagers);
        // complete the remaining sources
        for (int i = 1; i < parallelism; i += 2) {
            sourceFutures[i].complete(sourceSlots[i]);
        }
        ExecutionGraphSchedulingTest.verifyNothingDeployed(eg, sourceTaskManagers);
        // complete the targets except for one
        for (int i = 1; i < parallelism; i++) {
            targetFutures[i].complete(targetSlots[i]);
        }
        ExecutionGraphSchedulingTest.verifyNothingDeployed(eg, targetTaskManagers);
        // complete the last target slot future
        targetFutures[0].complete(targetSlots[0]);
        // 
        // verify that all deployments have happened
        for (InteractionsCountingTaskManagerGateway gateway : sourceTaskManagers) {
            Assert.assertThat(gateway.getSubmitTaskCount(), Matchers.is(1));
        }
        for (InteractionsCountingTaskManagerGateway gateway : targetTaskManagers) {
            Assert.assertThat(gateway.getSubmitTaskCount(), Matchers.is(1));
        }
    }

    /**
     * This test verifies that if one slot future fails, the deployment will be aborted.
     */
    @Test
    public void testOneSlotFailureAbortsDeploy() throws Exception {
        // [pipelined]
        // we construct a simple graph    (source) ----------------> (target)
        final int parallelism = 6;
        final JobVertex sourceVertex = new JobVertex("source");
        sourceVertex.setParallelism(parallelism);
        sourceVertex.setInvokableClass(NoOpInvokable.class);
        final JobVertex targetVertex = new JobVertex("target");
        targetVertex.setParallelism(parallelism);
        targetVertex.setInvokableClass(NoOpInvokable.class);
        targetVertex.connectNewDataSetAsInput(sourceVertex, POINTWISE, PIPELINED);
        final JobID jobId = new JobID();
        final JobGraph jobGraph = new JobGraph(jobId, "test", sourceVertex, targetVertex);
        // 
        // Create the slots, futures, and the slot provider
        final InteractionsCountingTaskManagerGateway taskManager = ExecutionGraphSchedulingTest.createTaskManager();
        final BlockingQueue<AllocationID> returnedSlots = new ArrayBlockingQueue<>(parallelism);
        final TestingSlotOwner slotOwner = new TestingSlotOwner();
        slotOwner.setReturnAllocatedSlotConsumer((LogicalSlot logicalSlot) -> returnedSlots.offer(logicalSlot.getAllocationId()));
        final SimpleSlot[] sourceSlots = new SimpleSlot[parallelism];
        final SimpleSlot[] targetSlots = new SimpleSlot[parallelism];
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final CompletableFuture<LogicalSlot>[] sourceFutures = new CompletableFuture[parallelism];
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final CompletableFuture<LogicalSlot>[] targetFutures = new CompletableFuture[parallelism];
        for (int i = 0; i < parallelism; i++) {
            sourceSlots[i] = createSlot(taskManager, jobId, slotOwner);
            targetSlots[i] = createSlot(taskManager, jobId, slotOwner);
            sourceFutures[i] = new CompletableFuture();
            targetFutures[i] = new CompletableFuture();
        }
        ProgrammedSlotProvider slotProvider = new ProgrammedSlotProvider(parallelism);
        slotProvider.addSlots(sourceVertex.getID(), sourceFutures);
        slotProvider.addSlots(targetVertex.getID(), targetFutures);
        final ExecutionGraph eg = createExecutionGraph(jobGraph, slotProvider);
        eg.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        // 
        // we complete some of the futures
        for (int i = 0; i < parallelism; i += 2) {
            sourceFutures[i].complete(sourceSlots[i]);
            targetFutures[i].complete(targetSlots[i]);
        }
        // 
        // kick off the scheduling
        eg.setScheduleMode(EAGER);
        eg.setQueuedSchedulingAllowed(true);
        eg.scheduleForExecution();
        // fail one slot
        sourceFutures[1].completeExceptionally(new ExecutionGraphSchedulingTest.TestRuntimeException());
        // wait until the job failed as a whole
        eg.getTerminationFuture().get(2000, TimeUnit.MILLISECONDS);
        // wait until all slots are back
        for (int i = 0; i < parallelism; i++) {
            returnedSlots.poll(2000L, TimeUnit.MILLISECONDS);
        }
        // no deployment calls must have happened
        Assert.assertThat(taskManager.getSubmitTaskCount(), Matchers.is(0));
        // all completed futures must have been returns
        for (int i = 0; i < parallelism; i += 2) {
            Assert.assertTrue(sourceSlots[i].isCanceled());
            Assert.assertTrue(targetSlots[i].isCanceled());
        }
    }

    /**
     * This tests makes sure that with eager scheduling no task is deployed if a single
     * slot allocation fails. Moreover we check that allocated slots will be returned.
     */
    @Test
    public void testEagerSchedulingWithSlotTimeout() throws Exception {
        // we construct a simple graph:    (task)
        final int parallelism = 3;
        final JobVertex vertex = new JobVertex("task");
        vertex.setParallelism(parallelism);
        vertex.setInvokableClass(NoOpInvokable.class);
        final JobID jobId = new JobID();
        final JobGraph jobGraph = new JobGraph(jobId, "test", vertex);
        final BlockingQueue<AllocationID> returnedSlots = new ArrayBlockingQueue<>(2);
        final TestingSlotOwner slotOwner = new TestingSlotOwner();
        slotOwner.setReturnAllocatedSlotConsumer((LogicalSlot logicalSlot) -> returnedSlots.offer(logicalSlot.getAllocationId()));
        final InteractionsCountingTaskManagerGateway taskManager = ExecutionGraphSchedulingTest.createTaskManager();
        final SimpleSlot[] slots = new SimpleSlot[parallelism];
        @SuppressWarnings({ "unchecked", "rawtypes" })
        final CompletableFuture<LogicalSlot>[] slotFutures = new CompletableFuture[parallelism];
        for (int i = 0; i < parallelism; i++) {
            slots[i] = createSlot(taskManager, jobId, slotOwner);
            slotFutures[i] = new CompletableFuture();
        }
        ProgrammedSlotProvider slotProvider = new ProgrammedSlotProvider(parallelism);
        slotProvider.addSlots(vertex.getID(), slotFutures);
        final ExecutionGraph eg = createExecutionGraph(jobGraph, slotProvider);
        // we complete one future
        slotFutures[1].complete(slots[1]);
        // kick off the scheduling
        eg.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        eg.setScheduleMode(EAGER);
        eg.setQueuedSchedulingAllowed(true);
        eg.scheduleForExecution();
        // we complete another future
        slotFutures[2].complete(slots[2]);
        // check that the ExecutionGraph is not terminated yet
        Assert.assertThat(eg.getTerminationFuture().isDone(), Matchers.is(false));
        // time out one of the slot futures
        slotFutures[0].completeExceptionally(new TimeoutException("Test time out"));
        Assert.assertThat(eg.getTerminationFuture().get(), Matchers.is(FAILED));
        // wait until all slots are back
        for (int i = 0; i < (parallelism - 1); i++) {
            returnedSlots.poll(2000, TimeUnit.MILLISECONDS);
        }
        // verify that no deployments have happened
        Assert.assertThat(taskManager.getSubmitTaskCount(), Matchers.is(0));
    }

    /**
     * Tests that an ongoing scheduling operation does not fail the {@link ExecutionGraph}
     * if it gets concurrently cancelled.
     */
    @Test
    public void testSchedulingOperationCancellationWhenCancel() throws Exception {
        final JobVertex jobVertex = new JobVertex("NoOp JobVertex");
        jobVertex.setInvokableClass(NoOpInvokable.class);
        jobVertex.setParallelism(2);
        final JobGraph jobGraph = new JobGraph(jobVertex);
        jobGraph.setScheduleMode(EAGER);
        jobGraph.setAllowQueuedScheduling(true);
        final CompletableFuture<LogicalSlot> slotFuture1 = new CompletableFuture<>();
        final CompletableFuture<LogicalSlot> slotFuture2 = new CompletableFuture<>();
        final ProgrammedSlotProvider slotProvider = new ProgrammedSlotProvider(2);
        slotProvider.addSlots(jobVertex.getID(), new CompletableFuture[]{ slotFuture1, slotFuture2 });
        final ExecutionGraph executionGraph = createExecutionGraph(jobGraph, slotProvider);
        executionGraph.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        executionGraph.scheduleForExecution();
        final CompletableFuture<?> releaseFuture = new CompletableFuture<>();
        final TestingLogicalSlot slot = ExecutionGraphSchedulingTest.createTestingSlot(releaseFuture);
        slotFuture1.complete(slot);
        // cancel should change the state of all executions to CANCELLED
        executionGraph.cancel();
        // complete the now CANCELLED execution --> this should cause a failure
        slotFuture2.complete(new TestingLogicalSlot());
        Thread.sleep(1L);
        // release the first slot to finish the cancellation
        releaseFuture.complete(null);
        // NOTE: This test will only occasionally fail without the fix since there is
        // a race between the releaseFuture and the slotFuture2
        Assert.assertThat(executionGraph.getTerminationFuture().get(), Matchers.is(CANCELED));
    }

    /**
     * Tests that a partially completed eager scheduling operation fails if a
     * completed slot is released. See FLINK-9099.
     */
    @Test
    public void testSlotReleasingFailsSchedulingOperation() throws Exception {
        final int parallelism = 2;
        final JobVertex jobVertex = new JobVertex("Testing job vertex");
        jobVertex.setInvokableClass(NoOpInvokable.class);
        jobVertex.setParallelism(parallelism);
        final JobGraph jobGraph = new JobGraph(jobVertex);
        jobGraph.setAllowQueuedScheduling(true);
        jobGraph.setScheduleMode(EAGER);
        final ProgrammedSlotProvider slotProvider = new ProgrammedSlotProvider(parallelism);
        final SimpleSlot slot = createSlot(new SimpleAckingTaskManagerGateway(), jobGraph.getJobID(), new DummySlotOwner());
        slotProvider.addSlot(jobVertex.getID(), 0, CompletableFuture.completedFuture(slot));
        final CompletableFuture<LogicalSlot> slotFuture = new CompletableFuture<>();
        slotProvider.addSlot(jobVertex.getID(), 1, slotFuture);
        final ExecutionGraph executionGraph = createExecutionGraph(jobGraph, slotProvider);
        executionGraph.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        executionGraph.scheduleForExecution();
        Assert.assertThat(executionGraph.getState(), Matchers.is(RUNNING));
        final ExecutionJobVertex executionJobVertex = executionGraph.getJobVertex(jobVertex.getID());
        final ExecutionVertex[] taskVertices = executionJobVertex.getTaskVertices();
        Assert.assertThat(taskVertices[0].getExecutionState(), Matchers.is(SCHEDULED));
        Assert.assertThat(taskVertices[1].getExecutionState(), Matchers.is(SCHEDULED));
        // fail the single allocated slot --> this should fail the scheduling operation
        slot.releaseSlot(new FlinkException("Test failure"));
        Assert.assertThat(executionGraph.getTerminationFuture().get(), Matchers.is(FAILED));
    }

    /**
     * Tests that all slots are being returned to the {@link SlotOwner} if the
     * {@link ExecutionGraph} is being cancelled. See FLINK-9908
     */
    @Test
    public void testCancellationOfIncompleteScheduling() throws Exception {
        final int parallelism = 10;
        final JobVertex jobVertex = new JobVertex("Test job vertex");
        jobVertex.setInvokableClass(NoOpInvokable.class);
        jobVertex.setParallelism(parallelism);
        final JobGraph jobGraph = new JobGraph(jobVertex);
        jobGraph.setAllowQueuedScheduling(true);
        jobGraph.setScheduleMode(EAGER);
        final TestingSlotOwner slotOwner = new TestingSlotOwner();
        final SimpleAckingTaskManagerGateway taskManagerGateway = new SimpleAckingTaskManagerGateway();
        final ConcurrentMap<SlotRequestId, Integer> slotRequestIds = new ConcurrentHashMap<>(parallelism);
        final TestingSlotProvider slotProvider = new TestingSlotProvider((SlotRequestId slotRequestId) -> {
            slotRequestIds.put(slotRequestId, 1);
            // return 50/50 fulfilled and unfulfilled requests
            return ((slotRequestIds.size()) % 2) == 0 ? CompletableFuture.completedFuture(ExecutionGraphSchedulingTest.createSingleLogicalSlot(slotOwner, taskManagerGateway, slotRequestId)) : new CompletableFuture<>();
        });
        final ExecutionGraph executionGraph = createExecutionGraph(jobGraph, slotProvider);
        executionGraph.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        final Set<SlotRequestId> slotRequestIdsToReturn = ConcurrentHashMap.newKeySet(slotRequestIds.size());
        executionGraph.scheduleForExecution();
        slotRequestIdsToReturn.addAll(slotRequestIds.keySet());
        slotOwner.setReturnAllocatedSlotConsumer(( logicalSlot) -> {
            slotRequestIdsToReturn.remove(logicalSlot.getSlotRequestId());
        });
        slotProvider.setSlotCanceller(slotRequestIdsToReturn::remove);
        // make sure that we complete cancellations of deployed tasks
        taskManagerGateway.setCancelConsumer((ExecutionAttemptID executionAttemptId) -> {
            final Execution execution = executionGraph.getRegisteredExecutions().get(executionAttemptId);
            // if the execution was cancelled in state SCHEDULING, then it might already have been removed
            if (execution != null) {
                execution.completeCancelling();
            }
        });
        executionGraph.cancel();
        Assert.assertThat(slotRequestIdsToReturn, Matchers.is(Matchers.empty()));
    }

    private static class TestRuntimeException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}

