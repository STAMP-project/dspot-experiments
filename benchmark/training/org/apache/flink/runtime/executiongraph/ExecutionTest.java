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


import ExecutionState.CANCELED;
import ExecutionState.CANCELING;
import ExecutionState.SCHEDULED;
import LocationPreferenceConstraint.ALL;
import LocationPreferenceConstraint.ANY;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.checkpoint.JobManagerTaskRestore;
import org.apache.flink.runtime.checkpoint.TaskStateSnapshot;
import org.apache.flink.runtime.executiongraph.restart.NoRestartStrategy;
import org.apache.flink.runtime.executiongraph.utils.ExecutionAttemptID;
import org.apache.flink.runtime.executiongraph.utils.SimpleAckingTaskManagerGateway;
import org.apache.flink.runtime.instance.SimpleSlot;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobmanager.scheduler.SchedulerTestUtils;
import org.apache.flink.runtime.jobmaster.LogicalSlot;
import org.apache.flink.runtime.jobmaster.SlotOwner;
import org.apache.flink.runtime.jobmaster.SlotRequestId;
import org.apache.flink.runtime.jobmaster.TestingLogicalSlot;
import org.apache.flink.runtime.jobmaster.slotpool.SingleLogicalSlot;
import org.apache.flink.runtime.taskmanager.LocalTaskManagerLocation;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.testtasks.NoOpInvokable;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Tests for the {@link Execution}.
 */
public class ExecutionTest extends TestLogger {
    @ClassRule
    public static final TestingComponentMainThreadExecutor.Resource EXECUTOR_RESOURCE = new TestingComponentMainThreadExecutor.Resource();

    private final TestingComponentMainThreadExecutor testMainThreadUtil = ExecutionTest.EXECUTOR_RESOURCE.getComponentMainThreadTestExecutor();

    /**
     * Tests that slots are released if we cannot assign the allocated resource to the
     * Execution.
     */
    @Test
    public void testSlotReleaseOnFailedResourceAssignment() throws Exception {
        final JobVertex jobVertex = createNoOpJobVertex();
        final JobVertexID jobVertexId = jobVertex.getID();
        final CompletableFuture<LogicalSlot> slotFuture = new CompletableFuture<>();
        final ProgrammedSlotProvider slotProvider = new ProgrammedSlotProvider(1);
        slotProvider.addSlot(jobVertexId, 0, slotFuture);
        ExecutionGraph executionGraph = ExecutionGraphTestUtils.createSimpleTestGraph(new JobID(), slotProvider, new NoRestartStrategy(), jobVertex);
        executionGraph.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        ExecutionJobVertex executionJobVertex = executionGraph.getJobVertex(jobVertexId);
        final Execution execution = executionJobVertex.getTaskVertices()[0].getCurrentExecutionAttempt();
        final ExecutionTest.SingleSlotTestingSlotOwner slotOwner = new ExecutionTest.SingleSlotTestingSlotOwner();
        final SimpleSlot slot = new SimpleSlot(slotOwner, new LocalTaskManagerLocation(), 0, new SimpleAckingTaskManagerGateway());
        final LogicalSlot otherSlot = new TestingLogicalSlot();
        CompletableFuture<Execution> allocationFuture = execution.allocateAndAssignSlotForExecution(slotProvider, false, ALL, Collections.emptySet(), TestingUtils.infiniteTime());
        Assert.assertFalse(allocationFuture.isDone());
        Assert.assertEquals(SCHEDULED, execution.getState());
        // assign a different resource to the execution
        Assert.assertTrue(execution.tryAssignResource(otherSlot));
        // completing now the future should cause the slot to be released
        slotFuture.complete(slot);
        Assert.assertEquals(slot, slotOwner.getReturnedSlotFuture().get());
    }

    /**
     * Tests that the slot is released in case of a execution cancellation when having
     * a slot assigned and being in state SCHEDULED.
     */
    @Test
    public void testSlotReleaseOnExecutionCancellationInScheduled() throws Exception {
        final JobVertex jobVertex = createNoOpJobVertex();
        final JobVertexID jobVertexId = jobVertex.getID();
        final ExecutionTest.SingleSlotTestingSlotOwner slotOwner = new ExecutionTest.SingleSlotTestingSlotOwner();
        final SimpleSlot slot = new SimpleSlot(slotOwner, new LocalTaskManagerLocation(), 0, new SimpleAckingTaskManagerGateway());
        final ProgrammedSlotProvider slotProvider = new ProgrammedSlotProvider(1);
        slotProvider.addSlot(jobVertexId, 0, CompletableFuture.completedFuture(slot));
        ExecutionGraph executionGraph = ExecutionGraphTestUtils.createSimpleTestGraph(new JobID(), slotProvider, new NoRestartStrategy(), jobVertex);
        executionGraph.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        ExecutionJobVertex executionJobVertex = executionGraph.getJobVertex(jobVertexId);
        final Execution execution = executionJobVertex.getTaskVertices()[0].getCurrentExecutionAttempt();
        CompletableFuture<Execution> allocationFuture = execution.allocateAndAssignSlotForExecution(slotProvider, false, ALL, Collections.emptySet(), TestingUtils.infiniteTime());
        Assert.assertTrue(allocationFuture.isDone());
        Assert.assertEquals(SCHEDULED, execution.getState());
        Assert.assertEquals(slot, execution.getAssignedResource());
        // cancelling the execution should move it into state CANCELED
        execution.cancel();
        Assert.assertEquals(CANCELED, execution.getState());
        Assert.assertEquals(slot, slotOwner.getReturnedSlotFuture().get());
    }

    /**
     * Tests that the slot is released in case of a execution cancellation when being in state
     * RUNNING.
     */
    @Test
    public void testSlotReleaseOnExecutionCancellationInRunning() throws Exception {
        final JobVertex jobVertex = createNoOpJobVertex();
        final JobVertexID jobVertexId = jobVertex.getID();
        final ExecutionTest.SingleSlotTestingSlotOwner slotOwner = new ExecutionTest.SingleSlotTestingSlotOwner();
        final SimpleSlot slot = new SimpleSlot(slotOwner, new LocalTaskManagerLocation(), 0, new SimpleAckingTaskManagerGateway());
        final ProgrammedSlotProvider slotProvider = new ProgrammedSlotProvider(1);
        slotProvider.addSlot(jobVertexId, 0, CompletableFuture.completedFuture(slot));
        ExecutionGraph executionGraph = ExecutionGraphTestUtils.createSimpleTestGraph(new JobID(), slotProvider, new NoRestartStrategy(), jobVertex);
        ExecutionJobVertex executionJobVertex = executionGraph.getJobVertex(jobVertexId);
        final Execution execution = executionJobVertex.getTaskVertices()[0].getCurrentExecutionAttempt();
        CompletableFuture<Execution> allocationFuture = execution.allocateAndAssignSlotForExecution(slotProvider, false, ALL, Collections.emptySet(), TestingUtils.infiniteTime());
        Assert.assertTrue(allocationFuture.isDone());
        Assert.assertEquals(SCHEDULED, execution.getState());
        Assert.assertEquals(slot, execution.getAssignedResource());
        execution.deploy();
        execution.switchToRunning();
        // cancelling the execution should move it into state CANCELING
        execution.cancel();
        Assert.assertEquals(CANCELING, execution.getState());
        execution.completeCancelling();
        Assert.assertEquals(slot, slotOwner.getReturnedSlotFuture().get());
    }

    /**
     * Tests that a slot allocation from a {@link SlotProvider} is cancelled if the
     * {@link Execution} is cancelled.
     */
    @Test
    public void testSlotAllocationCancellationWhenExecutionCancelled() throws Exception {
        final JobVertexID jobVertexId = new JobVertexID();
        final JobVertex jobVertex = new JobVertex("test vertex", jobVertexId);
        jobVertex.setInvokableClass(NoOpInvokable.class);
        final ProgrammedSlotProvider slotProvider = new ProgrammedSlotProvider(1);
        final CompletableFuture<LogicalSlot> slotFuture = new CompletableFuture<>();
        slotProvider.addSlot(jobVertexId, 0, slotFuture);
        final ExecutionGraph executionGraph = ExecutionGraphTestUtils.createSimpleTestGraph(new JobID(), slotProvider, new NoRestartStrategy(), jobVertex);
        executionGraph.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        final ExecutionJobVertex executionJobVertex = executionGraph.getJobVertex(jobVertexId);
        final Execution currentExecutionAttempt = executionJobVertex.getTaskVertices()[0].getCurrentExecutionAttempt();
        final CompletableFuture<Execution> allocationFuture = currentExecutionAttempt.allocateAndAssignSlotForExecution(slotProvider, false, ALL, Collections.emptySet(), TestingUtils.infiniteTime());
        Assert.assertThat(allocationFuture.isDone(), Matchers.is(false));
        Assert.assertThat(slotProvider.getSlotRequestedFuture(jobVertexId, 0).get(), Matchers.is(true));
        final Set<SlotRequestId> slotRequests = slotProvider.getSlotRequests();
        Assert.assertThat(slotRequests, Matchers.hasSize(1));
        Assert.assertThat(currentExecutionAttempt.getState(), Matchers.is(SCHEDULED));
        currentExecutionAttempt.cancel();
        Assert.assertThat(currentExecutionAttempt.getState(), Matchers.is(CANCELED));
        Assert.assertThat(allocationFuture.isCompletedExceptionally(), Matchers.is(true));
        final Set<SlotRequestId> canceledSlotRequests = slotProvider.getCanceledSlotRequests();
        Assert.assertThat(canceledSlotRequests, Matchers.equalTo(slotRequests));
    }

    /**
     * Tests that all preferred locations are calculated.
     */
    @Test
    public void testAllPreferredLocationCalculation() throws InterruptedException, ExecutionException {
        final TaskManagerLocation taskManagerLocation1 = new LocalTaskManagerLocation();
        final TaskManagerLocation taskManagerLocation2 = new LocalTaskManagerLocation();
        final TaskManagerLocation taskManagerLocation3 = new LocalTaskManagerLocation();
        final CompletableFuture<TaskManagerLocation> locationFuture1 = CompletableFuture.completedFuture(taskManagerLocation1);
        final CompletableFuture<TaskManagerLocation> locationFuture2 = new CompletableFuture<>();
        final CompletableFuture<TaskManagerLocation> locationFuture3 = new CompletableFuture<>();
        final Execution execution = SchedulerTestUtils.getTestVertex(Arrays.asList(locationFuture1, locationFuture2, locationFuture3));
        CompletableFuture<Collection<TaskManagerLocation>> preferredLocationsFuture = execution.calculatePreferredLocations(ALL);
        Assert.assertFalse(preferredLocationsFuture.isDone());
        locationFuture3.complete(taskManagerLocation3);
        Assert.assertFalse(preferredLocationsFuture.isDone());
        locationFuture2.complete(taskManagerLocation2);
        Assert.assertTrue(preferredLocationsFuture.isDone());
        final Collection<TaskManagerLocation> preferredLocations = preferredLocationsFuture.get();
        Assert.assertThat(preferredLocations, Matchers.containsInAnyOrder(taskManagerLocation1, taskManagerLocation2, taskManagerLocation3));
    }

    /**
     * Tests that any preferred locations are calculated.
     */
    @Test
    public void testAnyPreferredLocationCalculation() throws InterruptedException, ExecutionException {
        final TaskManagerLocation taskManagerLocation1 = new LocalTaskManagerLocation();
        final TaskManagerLocation taskManagerLocation3 = new LocalTaskManagerLocation();
        final CompletableFuture<TaskManagerLocation> locationFuture1 = CompletableFuture.completedFuture(taskManagerLocation1);
        final CompletableFuture<TaskManagerLocation> locationFuture2 = new CompletableFuture<>();
        final CompletableFuture<TaskManagerLocation> locationFuture3 = CompletableFuture.completedFuture(taskManagerLocation3);
        final Execution execution = SchedulerTestUtils.getTestVertex(Arrays.asList(locationFuture1, locationFuture2, locationFuture3));
        CompletableFuture<Collection<TaskManagerLocation>> preferredLocationsFuture = execution.calculatePreferredLocations(ANY);
        Assert.assertTrue(preferredLocationsFuture.isDone());
        final Collection<TaskManagerLocation> preferredLocations = preferredLocationsFuture.get();
        Assert.assertThat(preferredLocations, Matchers.containsInAnyOrder(taskManagerLocation1, taskManagerLocation3));
    }

    /**
     * Checks that the {@link Execution} termination future is only completed after the
     * assigned slot has been released.
     *
     * <p>NOTE: This test only fails spuriously without the fix of this commit. Thus, one has
     * to execute this test multiple times to see the failure.
     */
    @Test
    public void testTerminationFutureIsCompletedAfterSlotRelease() throws Exception {
        final JobVertex jobVertex = createNoOpJobVertex();
        final JobVertexID jobVertexId = jobVertex.getID();
        final ExecutionTest.SingleSlotTestingSlotOwner slotOwner = new ExecutionTest.SingleSlotTestingSlotOwner();
        final ProgrammedSlotProvider slotProvider = createProgrammedSlotProvider(1, Collections.singleton(jobVertexId), slotOwner);
        ExecutionGraph executionGraph = ExecutionGraphTestUtils.createSimpleTestGraph(new JobID(), slotProvider, new NoRestartStrategy(), jobVertex);
        executionGraph.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        ExecutionJobVertex executionJobVertex = executionGraph.getJobVertex(jobVertexId);
        ExecutionVertex executionVertex = executionJobVertex.getTaskVertices()[0];
        executionVertex.scheduleForExecution(slotProvider, false, ANY, Collections.emptySet()).get();
        Execution currentExecutionAttempt = executionVertex.getCurrentExecutionAttempt();
        CompletableFuture<LogicalSlot> returnedSlotFuture = slotOwner.getReturnedSlotFuture();
        CompletableFuture<?> terminationFuture = executionVertex.cancel();
        currentExecutionAttempt.completeCancelling();
        CompletableFuture<Boolean> restartFuture = terminationFuture.thenApply(( ignored) -> {
            Assert.assertTrue(returnedSlotFuture.isDone());
            return true;
        });
        // check if the returned slot future was completed first
        restartFuture.get();
    }

    /**
     * Tests that the task restore state is nulled after the {@link Execution} has been
     * deployed. See FLINK-9693.
     */
    @Test
    public void testTaskRestoreStateIsNulledAfterDeployment() throws Exception {
        final JobVertex jobVertex = createNoOpJobVertex();
        final JobVertexID jobVertexId = jobVertex.getID();
        final ExecutionTest.SingleSlotTestingSlotOwner slotOwner = new ExecutionTest.SingleSlotTestingSlotOwner();
        final ProgrammedSlotProvider slotProvider = createProgrammedSlotProvider(1, Collections.singleton(jobVertexId), slotOwner);
        ExecutionGraph executionGraph = ExecutionGraphTestUtils.createSimpleTestGraph(new JobID(), slotProvider, new NoRestartStrategy(), jobVertex);
        ExecutionJobVertex executionJobVertex = executionGraph.getJobVertex(jobVertexId);
        ExecutionVertex executionVertex = executionJobVertex.getTaskVertices()[0];
        final Execution execution = executionVertex.getCurrentExecutionAttempt();
        final JobManagerTaskRestore taskRestoreState = new JobManagerTaskRestore(1L, new TaskStateSnapshot());
        execution.setInitialState(taskRestoreState);
        Assert.assertThat(execution.getTaskRestore(), Matchers.is(Matchers.notNullValue()));
        // schedule the execution vertex and wait for its deployment
        executionVertex.scheduleForExecution(slotProvider, false, ANY, Collections.emptySet()).get();
        Assert.assertThat(execution.getTaskRestore(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testEagerSchedulingFailureReturnsSlot() throws Exception {
        final JobVertex jobVertex = createNoOpJobVertex();
        final JobVertexID jobVertexId = jobVertex.getID();
        final SimpleAckingTaskManagerGateway taskManagerGateway = new SimpleAckingTaskManagerGateway();
        final ExecutionTest.SingleSlotTestingSlotOwner slotOwner = new ExecutionTest.SingleSlotTestingSlotOwner();
        final CompletableFuture<SlotRequestId> slotRequestIdFuture = new CompletableFuture<>();
        final CompletableFuture<SlotRequestId> returnedSlotFuture = new CompletableFuture<>();
        final TestingSlotProvider slotProvider = new TestingSlotProvider((SlotRequestId slotRequestId) -> {
            slotRequestIdFuture.complete(slotRequestId);
            return new CompletableFuture<>();
        });
        slotProvider.setSlotCanceller(returnedSlotFuture::complete);
        slotOwner.getReturnedSlotFuture().thenAccept((LogicalSlot logicalSlot) -> returnedSlotFuture.complete(logicalSlot.getSlotRequestId()));
        ExecutionGraph executionGraph = ExecutionGraphTestUtils.createSimpleTestGraph(new JobID(), slotProvider, new NoRestartStrategy(), jobVertex);
        executionGraph.start(testMainThreadUtil.getMainThreadExecutor());
        ExecutionJobVertex executionJobVertex = executionGraph.getJobVertex(jobVertexId);
        ExecutionVertex executionVertex = executionJobVertex.getTaskVertices()[0];
        final Execution execution = executionVertex.getCurrentExecutionAttempt();
        taskManagerGateway.setCancelConsumer(( executionAttemptID) -> {
            if (execution.getAttemptId().equals(executionAttemptID)) {
                execution.completeCancelling();
            }
        });
        slotRequestIdFuture.thenAcceptAsync((SlotRequestId slotRequestId) -> {
            final SingleLogicalSlot singleLogicalSlot = ExecutionGraphSchedulingTest.createSingleLogicalSlot(slotOwner, taskManagerGateway, slotRequestId);
            slotProvider.complete(slotRequestId, singleLogicalSlot);
        }, testMainThreadUtil.getMainThreadExecutor());
        final CompletableFuture<Void> schedulingFuture = testMainThreadUtil.execute(() -> execution.scheduleForExecution(slotProvider, false, LocationPreferenceConstraint.ANY, Collections.emptySet()));
        try {
            schedulingFuture.get();
            // cancel the execution in case we could schedule the execution
            testMainThreadUtil.execute(execution::cancel);
        } catch (ExecutionException ignored) {
        }
        Assert.assertThat(returnedSlotFuture.get(), Matchers.is(Matchers.equalTo(slotRequestIdFuture.get())));
    }

    /**
     * Tests that a slot release will atomically release the assigned {@link Execution}.
     */
    @Test
    public void testSlotReleaseAtomicallyReleasesExecution() throws Exception {
        final JobVertex jobVertex = createNoOpJobVertex();
        final ExecutionTest.SingleSlotTestingSlotOwner slotOwner = new ExecutionTest.SingleSlotTestingSlotOwner();
        final SingleLogicalSlot slot = ExecutionGraphSchedulingTest.createSingleLogicalSlot(slotOwner, new SimpleAckingTaskManagerGateway(), new SlotRequestId());
        final CompletableFuture<LogicalSlot> slotFuture = CompletableFuture.completedFuture(slot);
        final CountDownLatch slotRequestLatch = new CountDownLatch(1);
        final TestingSlotProvider slotProvider = new TestingSlotProvider(( slotRequestId) -> {
            slotRequestLatch.countDown();
            return slotFuture;
        });
        final ExecutionGraph executionGraph = ExecutionGraphTestUtils.createSimpleTestGraph(new JobID(), slotProvider, new NoRestartStrategy(), jobVertex);
        final Execution execution = executionGraph.getJobVertex(jobVertex.getID()).getTaskVertices()[0].getCurrentExecutionAttempt();
        executionGraph.start(testMainThreadUtil.getMainThreadExecutor());
        testMainThreadUtil.execute(executionGraph::scheduleForExecution);
        // wait until the slot has been requested
        slotRequestLatch.await();
        testMainThreadUtil.execute(() -> {
            assertThat(execution.getAssignedResource(), is(sameInstance(slot)));
            slot.release(new FlinkException("Test exception"));
            assertThat(execution.getReleaseFuture().isDone(), is(true));
        });
    }

    /**
     * Slot owner which records the first returned slot.
     */
    private static final class SingleSlotTestingSlotOwner implements SlotOwner {
        final CompletableFuture<LogicalSlot> returnedSlot = new CompletableFuture<>();

        public CompletableFuture<LogicalSlot> getReturnedSlotFuture() {
            return returnedSlot;
        }

        @Override
        public void returnLogicalSlot(LogicalSlot logicalSlot) {
            returnedSlot.complete(logicalSlot);
        }
    }
}

