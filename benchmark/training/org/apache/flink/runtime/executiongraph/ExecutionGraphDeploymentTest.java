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


import CheckpointingOptions.MAX_RETAINED_CHECKPOINTS;
import DistributionPattern.ALL_TO_ALL;
import DistributionPattern.POINTWISE;
import ExecutionState.CREATED;
import ExecutionState.DEPLOYING;
import ExecutionState.SCHEDULED;
import JobStatus.FAILED;
import ResultPartitionType.BLOCKING;
import ResultPartitionType.PIPELINED;
import ScheduleMode.EAGER;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Function;
import junit.framework.TestCase;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.accumulators.Accumulator;
import org.apache.flink.api.common.accumulators.IntCounter;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.accumulators.AccumulatorSnapshot;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.blob.BlobWriter;
import org.apache.flink.runtime.blob.PermanentBlobService;
import org.apache.flink.runtime.blob.VoidBlobWriter;
import org.apache.flink.runtime.deployment.InputGateDeploymentDescriptor;
import org.apache.flink.runtime.deployment.ResultPartitionDeploymentDescriptor;
import org.apache.flink.runtime.deployment.TaskDeploymentDescriptor;
import org.apache.flink.runtime.execution.ExecutionState;
import org.apache.flink.runtime.executiongraph.failover.RestartAllStrategy;
import org.apache.flink.runtime.executiongraph.restart.NoRestartStrategy;
import org.apache.flink.runtime.executiongraph.utils.SimpleAckingTaskManagerGateway;
import org.apache.flink.runtime.instance.SimpleSlot;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobmaster.TestingLogicalSlot;
import org.apache.flink.runtime.jobmaster.slotpool.SlotProvider;
import org.apache.flink.runtime.operators.BatchTask;
import org.apache.flink.runtime.taskmanager.LocalTaskManagerLocation;
import org.apache.flink.runtime.taskmanager.TaskExecutionState;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.testtasks.NoOpInvokable;
import org.apache.flink.runtime.testutils.DirectScheduledExecutorService;
import org.apache.flink.util.TestLogger;
import org.apache.flink.util.function.FunctionUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ExecutionGraph} deployment.
 */
public class ExecutionGraphDeploymentTest extends TestLogger {
    /**
     * BLOB server instance to use for the job graph.
     */
    protected BlobWriter blobWriter = VoidBlobWriter.getInstance();

    /**
     * Permanent BLOB cache instance to use for the actor gateway that handles the {@link TaskDeploymentDescriptor} loading (may be <tt>null</tt>).
     */
    protected PermanentBlobService blobCache = null;

    @Test
    public void testBuildDeploymentDescriptor() {
        try {
            final JobID jobId = new JobID();
            final JobVertexID jid1 = new JobVertexID();
            final JobVertexID jid2 = new JobVertexID();
            final JobVertexID jid3 = new JobVertexID();
            final JobVertexID jid4 = new JobVertexID();
            JobVertex v1 = new JobVertex("v1", jid1);
            JobVertex v2 = new JobVertex("v2", jid2);
            JobVertex v3 = new JobVertex("v3", jid3);
            JobVertex v4 = new JobVertex("v4", jid4);
            v1.setParallelism(10);
            v2.setParallelism(10);
            v3.setParallelism(10);
            v4.setParallelism(10);
            v1.setInvokableClass(BatchTask.class);
            v2.setInvokableClass(BatchTask.class);
            v3.setInvokableClass(BatchTask.class);
            v4.setInvokableClass(BatchTask.class);
            v2.connectNewDataSetAsInput(v1, ALL_TO_ALL, PIPELINED);
            v3.connectNewDataSetAsInput(v2, ALL_TO_ALL, PIPELINED);
            v4.connectNewDataSetAsInput(v2, ALL_TO_ALL, PIPELINED);
            final JobInformation expectedJobInformation = new DummyJobInformation(jobId, "some job");
            DirectScheduledExecutorService executor = new DirectScheduledExecutorService();
            ExecutionGraph eg = new ExecutionGraph(expectedJobInformation, executor, executor, AkkaUtils.getDefaultTimeout(), new NoRestartStrategy(), new RestartAllStrategy.Factory(), new TestingSlotProvider(( ignore) -> new CompletableFuture<>()), ExecutionGraph.class.getClassLoader(), blobWriter, AkkaUtils.getDefaultTimeout());
            eg.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
            checkJobOffloaded(eg);
            List<JobVertex> ordered = Arrays.asList(v1, v2, v3, v4);
            eg.attachJobGraph(ordered);
            ExecutionJobVertex ejv = eg.getAllVertices().get(jid2);
            ExecutionVertex vertex = ejv.getTaskVertices()[3];
            final SimpleAckingTaskManagerGateway taskManagerGateway = new SimpleAckingTaskManagerGateway();
            final CompletableFuture<TaskDeploymentDescriptor> tdd = new CompletableFuture<>();
            taskManagerGateway.setSubmitConsumer(FunctionUtils.uncheckedConsumer(( taskDeploymentDescriptor) -> {
                taskDeploymentDescriptor.loadBigData(blobCache);
                tdd.complete(taskDeploymentDescriptor);
            }));
            final org.apache.flink.runtime.jobmaster.LogicalSlot slot = new TestingLogicalSlot(taskManagerGateway);
            Assert.assertEquals(CREATED, vertex.getExecutionState());
            vertex.deployToSlot(slot);
            Assert.assertEquals(DEPLOYING, vertex.getExecutionState());
            checkTaskOffloaded(eg, vertex.getJobvertexId());
            TaskDeploymentDescriptor descr = tdd.get();
            Assert.assertNotNull(descr);
            JobInformation jobInformation = descr.getSerializedJobInformation().deserializeValue(getClass().getClassLoader());
            TaskInformation taskInformation = descr.getSerializedTaskInformation().deserializeValue(getClass().getClassLoader());
            Assert.assertEquals(jobId, descr.getJobId());
            Assert.assertEquals(jobId, jobInformation.getJobId());
            Assert.assertEquals(jid2, taskInformation.getJobVertexId());
            Assert.assertEquals(3, descr.getSubtaskIndex());
            Assert.assertEquals(10, taskInformation.getNumberOfSubtasks());
            Assert.assertEquals(BatchTask.class.getName(), taskInformation.getInvokableClassName());
            Assert.assertEquals("v2", taskInformation.getTaskName());
            Collection<ResultPartitionDeploymentDescriptor> producedPartitions = descr.getProducedPartitions();
            Collection<InputGateDeploymentDescriptor> consumedPartitions = descr.getInputGates();
            Assert.assertEquals(2, producedPartitions.size());
            Assert.assertEquals(1, consumedPartitions.size());
            Iterator<ResultPartitionDeploymentDescriptor> iteratorProducedPartitions = producedPartitions.iterator();
            Iterator<InputGateDeploymentDescriptor> iteratorConsumedPartitions = consumedPartitions.iterator();
            Assert.assertEquals(10, iteratorProducedPartitions.next().getNumberOfSubpartitions());
            Assert.assertEquals(10, iteratorProducedPartitions.next().getNumberOfSubpartitions());
            Assert.assertEquals(10, iteratorConsumedPartitions.next().getInputChannelDeploymentDescriptors().length);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRegistrationOfExecutionsFinishing() {
        try {
            final JobVertexID jid1 = new JobVertexID();
            final JobVertexID jid2 = new JobVertexID();
            JobVertex v1 = new JobVertex("v1", jid1);
            JobVertex v2 = new JobVertex("v2", jid2);
            Map<ExecutionAttemptID, Execution> executions = setupExecution(v1, 7650, v2, 2350).f1;
            for (Execution e : executions.values()) {
                e.markFinished();
            }
            Assert.assertEquals(0, executions.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRegistrationOfExecutionsFailing() {
        try {
            final JobVertexID jid1 = new JobVertexID();
            final JobVertexID jid2 = new JobVertexID();
            JobVertex v1 = new JobVertex("v1", jid1);
            JobVertex v2 = new JobVertex("v2", jid2);
            Map<ExecutionAttemptID, Execution> executions = setupExecution(v1, 7, v2, 6).f1;
            for (Execution e : executions.values()) {
                e.markFailed(null);
            }
            Assert.assertEquals(0, executions.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRegistrationOfExecutionsFailedExternally() {
        try {
            final JobVertexID jid1 = new JobVertexID();
            final JobVertexID jid2 = new JobVertexID();
            JobVertex v1 = new JobVertex("v1", jid1);
            JobVertex v2 = new JobVertex("v2", jid2);
            Map<ExecutionAttemptID, Execution> executions = setupExecution(v1, 7, v2, 6).f1;
            for (Execution e : executions.values()) {
                e.fail(null);
            }
            Assert.assertEquals(0, executions.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Verifies that {@link ExecutionGraph#updateState(TaskExecutionState)} updates the accumulators and metrics for an
     * execution that failed or was canceled.
     */
    @Test
    public void testAccumulatorsAndMetricsForwarding() throws Exception {
        final JobVertexID jid1 = new JobVertexID();
        final JobVertexID jid2 = new JobVertexID();
        JobVertex v1 = new JobVertex("v1", jid1);
        JobVertex v2 = new JobVertex("v2", jid2);
        Tuple2<ExecutionGraph, Map<ExecutionAttemptID, Execution>> graphAndExecutions = setupExecution(v1, 1, v2, 1);
        ExecutionGraph graph = graphAndExecutions.f0;
        // verify behavior for canceled executions
        Execution execution1 = graphAndExecutions.f1.values().iterator().next();
        IOMetrics ioMetrics = new IOMetrics(0, 0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0);
        Map<String, Accumulator<?, ?>> accumulators = new HashMap<>();
        accumulators.put("acc", new IntCounter(4));
        AccumulatorSnapshot accumulatorSnapshot = new AccumulatorSnapshot(graph.getJobID(), execution1.getAttemptId(), accumulators);
        TaskExecutionState state = new TaskExecutionState(graph.getJobID(), execution1.getAttemptId(), ExecutionState.CANCELED, null, accumulatorSnapshot, ioMetrics);
        graph.updateState(state);
        Assert.assertEquals(ioMetrics, execution1.getIOMetrics());
        Assert.assertNotNull(execution1.getUserAccumulators());
        Assert.assertEquals(4, execution1.getUserAccumulators().get("acc").getLocalValue());
        // verify behavior for failed executions
        Execution execution2 = graphAndExecutions.f1.values().iterator().next();
        IOMetrics ioMetrics2 = new IOMetrics(0, 0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0);
        Map<String, Accumulator<?, ?>> accumulators2 = new HashMap<>();
        accumulators2.put("acc", new IntCounter(8));
        AccumulatorSnapshot accumulatorSnapshot2 = new AccumulatorSnapshot(graph.getJobID(), execution2.getAttemptId(), accumulators2);
        TaskExecutionState state2 = new TaskExecutionState(graph.getJobID(), execution2.getAttemptId(), ExecutionState.FAILED, null, accumulatorSnapshot2, ioMetrics2);
        graph.updateState(state2);
        Assert.assertEquals(ioMetrics2, execution2.getIOMetrics());
        Assert.assertNotNull(execution2.getUserAccumulators());
        Assert.assertEquals(8, execution2.getUserAccumulators().get("acc").getLocalValue());
    }

    /**
     * Verifies that {@link Execution#completeCancelling(Map, IOMetrics)} and {@link Execution#markFailed(Throwable, Map, IOMetrics)}
     * store the given accumulators and metrics correctly.
     */
    @Test
    public void testAccumulatorsAndMetricsStorage() throws Exception {
        final JobVertexID jid1 = new JobVertexID();
        final JobVertexID jid2 = new JobVertexID();
        JobVertex v1 = new JobVertex("v1", jid1);
        JobVertex v2 = new JobVertex("v2", jid2);
        Map<ExecutionAttemptID, Execution> executions = setupExecution(v1, 1, v2, 1).f1;
        IOMetrics ioMetrics = new IOMetrics(0, 0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0.0);
        Map<String, Accumulator<?, ?>> accumulators = Collections.emptyMap();
        Execution execution1 = executions.values().iterator().next();
        execution1.cancel();
        execution1.completeCancelling(accumulators, ioMetrics);
        Assert.assertEquals(ioMetrics, execution1.getIOMetrics());
        Assert.assertEquals(accumulators, execution1.getUserAccumulators());
        Execution execution2 = executions.values().iterator().next();
        execution2.markFailed(new Throwable(), accumulators, ioMetrics);
        Assert.assertEquals(ioMetrics, execution2.getIOMetrics());
        Assert.assertEquals(accumulators, execution2.getUserAccumulators());
    }

    @Test
    public void testRegistrationOfExecutionsCanceled() {
        try {
            final JobVertexID jid1 = new JobVertexID();
            final JobVertexID jid2 = new JobVertexID();
            JobVertex v1 = new JobVertex("v1", jid1);
            JobVertex v2 = new JobVertex("v2", jid2);
            Map<ExecutionAttemptID, Execution> executions = setupExecution(v1, 19, v2, 37).f1;
            for (Execution e : executions.values()) {
                e.cancel();
                e.completeCancelling();
            }
            Assert.assertEquals(0, executions.size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Tests that a blocking batch job fails if there are not enough resources left to schedule the
     * succeeding tasks. This test case is related to [FLINK-4296] where finished producing tasks
     * swallow the fail exception when scheduling a consumer task.
     */
    @Test
    public void testNoResourceAvailableFailure() throws Exception {
        final JobID jobId = new JobID();
        JobVertex v1 = new JobVertex("source");
        JobVertex v2 = new JobVertex("sink");
        int dop1 = 1;
        int dop2 = 1;
        v1.setParallelism(dop1);
        v2.setParallelism(dop2);
        v1.setInvokableClass(BatchTask.class);
        v2.setInvokableClass(BatchTask.class);
        v2.connectNewDataSetAsInput(v1, POINTWISE, BLOCKING);
        final ArrayDeque<CompletableFuture<org.apache.flink.runtime.jobmaster.LogicalSlot>> slotFutures = new ArrayDeque<>();
        for (int i = 0; i < dop1; i++) {
            slotFutures.addLast(CompletableFuture.completedFuture(new TestingLogicalSlot()));
        }
        final SlotProvider slotProvider = new TestingSlotProvider(( ignore) -> slotFutures.removeFirst());
        final JobInformation jobInformation = new DummyJobInformation(jobId, "failing test job");
        DirectScheduledExecutorService directExecutor = new DirectScheduledExecutorService();
        // execution graph that executes actions synchronously
        ExecutionGraph eg = new ExecutionGraph(jobInformation, directExecutor, TestingUtils.defaultExecutor(), AkkaUtils.getDefaultTimeout(), new NoRestartStrategy(), new RestartAllStrategy.Factory(), slotProvider, ExecutionGraph.class.getClassLoader(), blobWriter, AkkaUtils.getDefaultTimeout());
        eg.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        checkJobOffloaded(eg);
        eg.setQueuedSchedulingAllowed(false);
        List<JobVertex> ordered = Arrays.asList(v1, v2);
        eg.attachJobGraph(ordered);
        // schedule, this triggers mock deployment
        eg.scheduleForExecution();
        ExecutionAttemptID attemptID = eg.getJobVertex(v1.getID()).getTaskVertices()[0].getCurrentExecutionAttempt().getAttemptId();
        eg.updateState(new TaskExecutionState(jobId, attemptID, ExecutionState.RUNNING));
        eg.updateState(new TaskExecutionState(jobId, attemptID, ExecutionState.FINISHED, null));
        Assert.assertEquals(FAILED, eg.getState());
    }

    // ------------------------------------------------------------------------
    // retained checkpoints config test
    // ------------------------------------------------------------------------
    @Test
    public void testSettingDefaultMaxNumberOfCheckpointsToRetain() throws Exception {
        final Configuration jobManagerConfig = new Configuration();
        final ExecutionGraph eg = createExecutionGraph(jobManagerConfig);
        Assert.assertEquals(MAX_RETAINED_CHECKPOINTS.defaultValue().intValue(), eg.getCheckpointCoordinator().getCheckpointStore().getMaxNumberOfRetainedCheckpoints());
    }

    @Test
    public void testSettingMaxNumberOfCheckpointsToRetain() throws Exception {
        final int maxNumberOfCheckpointsToRetain = 10;
        final Configuration jobManagerConfig = new Configuration();
        jobManagerConfig.setInteger(MAX_RETAINED_CHECKPOINTS, maxNumberOfCheckpointsToRetain);
        final ExecutionGraph eg = createExecutionGraph(jobManagerConfig);
        Assert.assertEquals(maxNumberOfCheckpointsToRetain, eg.getCheckpointCoordinator().getCheckpointStore().getMaxNumberOfRetainedCheckpoints());
    }

    @Test
    public void testSettingIllegalMaxNumberOfCheckpointsToRetain() throws Exception {
        final int negativeMaxNumberOfCheckpointsToRetain = -10;
        final Configuration jobManagerConfig = new Configuration();
        jobManagerConfig.setInteger(MAX_RETAINED_CHECKPOINTS, negativeMaxNumberOfCheckpointsToRetain);
        final ExecutionGraph eg = createExecutionGraph(jobManagerConfig);
        Assert.assertNotEquals(negativeMaxNumberOfCheckpointsToRetain, eg.getCheckpointCoordinator().getCheckpointStore().getMaxNumberOfRetainedCheckpoints());
        Assert.assertEquals(MAX_RETAINED_CHECKPOINTS.defaultValue().intValue(), eg.getCheckpointCoordinator().getCheckpointStore().getMaxNumberOfRetainedCheckpoints());
    }

    /**
     * Tests that eager scheduling will wait until all input locations have been set before
     * scheduling a task.
     */
    @Test
    public void testEagerSchedulingWaitsOnAllInputPreferredLocations() throws Exception {
        final int parallelism = 2;
        final ProgrammedSlotProvider slotProvider = new ProgrammedSlotProvider(parallelism);
        final Time timeout = Time.hours(1L);
        final JobVertexID sourceVertexId = new JobVertexID();
        final JobVertex sourceVertex = new JobVertex("Test source", sourceVertexId);
        sourceVertex.setInvokableClass(NoOpInvokable.class);
        sourceVertex.setParallelism(parallelism);
        final JobVertexID sinkVertexId = new JobVertexID();
        final JobVertex sinkVertex = new JobVertex("Test sink", sinkVertexId);
        sinkVertex.setInvokableClass(NoOpInvokable.class);
        sinkVertex.setParallelism(parallelism);
        sinkVertex.connectNewDataSetAsInput(sourceVertex, ALL_TO_ALL, PIPELINED);
        final Map<JobVertexID, CompletableFuture<org.apache.flink.runtime.jobmaster.LogicalSlot>[]> slotFutures = new HashMap<>(2);
        for (JobVertexID jobVertexID : Arrays.asList(sourceVertexId, sinkVertexId)) {
            CompletableFuture<org.apache.flink.runtime.jobmaster.LogicalSlot>[] slotFutureArray = new CompletableFuture[parallelism];
            for (int i = 0; i < parallelism; i++) {
                slotFutureArray[i] = new CompletableFuture();
            }
            slotFutures.put(jobVertexID, slotFutureArray);
            slotProvider.addSlots(jobVertexID, slotFutureArray);
        }
        final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(3);
        final ExecutionGraph executionGraph = ExecutionGraphTestUtils.createExecutionGraph(new JobID(), slotProvider, new NoRestartStrategy(), scheduledExecutorService, timeout, sourceVertex, sinkVertex);
        executionGraph.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        executionGraph.setScheduleMode(EAGER);
        executionGraph.scheduleForExecution();
        // all tasks should be in state SCHEDULED
        for (ExecutionVertex executionVertex : executionGraph.getAllExecutionVertices()) {
            Assert.assertEquals(SCHEDULED, executionVertex.getCurrentExecutionAttempt().getState());
        }
        // wait until the source vertex slots have been requested
        TestCase.assertTrue(slotProvider.getSlotRequestedFuture(sourceVertexId, 0).get());
        TestCase.assertTrue(slotProvider.getSlotRequestedFuture(sourceVertexId, 1).get());
        // check that the sinks have not requested their slots because they need the location
        // information of the sources
        Assert.assertFalse(slotProvider.getSlotRequestedFuture(sinkVertexId, 0).isDone());
        Assert.assertFalse(slotProvider.getSlotRequestedFuture(sinkVertexId, 1).isDone());
        final TaskManagerLocation localTaskManagerLocation = new LocalTaskManagerLocation();
        final SimpleSlot sourceSlot1 = createSlot(localTaskManagerLocation, 0);
        final SimpleSlot sourceSlot2 = createSlot(localTaskManagerLocation, 1);
        final SimpleSlot sinkSlot1 = createSlot(localTaskManagerLocation, 0);
        final SimpleSlot sinkSlot2 = createSlot(localTaskManagerLocation, 1);
        slotFutures.get(sourceVertexId)[0].complete(sourceSlot1);
        slotFutures.get(sourceVertexId)[1].complete(sourceSlot2);
        // wait until the sink vertex slots have been requested after we completed the source slots
        TestCase.assertTrue(slotProvider.getSlotRequestedFuture(sinkVertexId, 0).get());
        TestCase.assertTrue(slotProvider.getSlotRequestedFuture(sinkVertexId, 1).get());
        slotFutures.get(sinkVertexId)[0].complete(sinkSlot1);
        slotFutures.get(sinkVertexId)[1].complete(sinkSlot2);
        for (ExecutionVertex executionVertex : executionGraph.getAllExecutionVertices()) {
            ExecutionGraphTestUtils.waitUntilExecutionState(executionVertex.getCurrentExecutionAttempt(), DEPLOYING, 5000L);
        }
    }

    @SuppressWarnings("serial")
    public static class FailingFinalizeJobVertex extends JobVertex {
        public FailingFinalizeJobVertex(String name, JobVertexID id) {
            super(name, id);
        }

        @Override
        public void finalizeOnMaster(ClassLoader cl) throws Exception {
            throw new Exception();
        }
    }
}

