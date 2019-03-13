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


import ExecutionState.CREATED;
import ExecutionState.DEPLOYING;
import ExecutionState.FAILED;
import ExecutionState.RUNNING;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.clusterframework.types.AllocationID;
import org.apache.flink.runtime.deployment.ResultPartitionDeploymentDescriptor;
import org.apache.flink.runtime.deployment.TaskDeploymentDescriptor;
import org.apache.flink.runtime.execution.ExecutionState;
import org.apache.flink.runtime.executiongraph.utils.SimpleAckingTaskManagerGateway;
import org.apache.flink.runtime.io.network.partition.ResultPartitionType;
import org.apache.flink.runtime.jobgraph.IntermediateDataSetID;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.ScheduleMode;
import org.apache.flink.runtime.jobmaster.LogicalSlot;
import org.apache.flink.runtime.jobmaster.SlotContext;
import org.apache.flink.runtime.jobmaster.TestingLogicalSlot;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.testutils.DirectScheduledExecutorService;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ExecutionVertexDeploymentTest extends TestLogger {
    @Test
    public void testDeployCall() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid);
            final LogicalSlot slot = new TestingLogicalSlot();
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            Assert.assertEquals(CREATED, vertex.getExecutionState());
            vertex.deployToSlot(slot);
            Assert.assertEquals(DEPLOYING, vertex.getExecutionState());
            // no repeated scheduling
            try {
                vertex.deployToSlot(slot);
                Assert.fail("Scheduled from wrong state");
            } catch (IllegalStateException e) {
                // as expected
            }
            Assert.assertNull(vertex.getFailureCause());
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(DEPLOYING)) > 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDeployWithSynchronousAnswer() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid, new DirectScheduledExecutorService());
            final LogicalSlot slot = new TestingLogicalSlot();
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            Assert.assertEquals(CREATED, vertex.getExecutionState());
            vertex.deployToSlot(slot);
            Assert.assertEquals(DEPLOYING, vertex.getExecutionState());
            // no repeated scheduling
            try {
                vertex.deployToSlot(slot);
                Assert.fail("Scheduled from wrong state");
            } catch (IllegalStateException e) {
                // as expected
            }
            Assert.assertNull(vertex.getFailureCause());
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(DEPLOYING)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(RUNNING)) == 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDeployWithAsynchronousAnswer() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid);
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            final LogicalSlot slot = new TestingLogicalSlot();
            Assert.assertEquals(CREATED, vertex.getExecutionState());
            vertex.deployToSlot(slot);
            // no repeated scheduling
            try {
                vertex.deployToSlot(slot);
                Assert.fail("Scheduled from wrong state");
            } catch (IllegalStateException e) {
                // as expected
            }
            Assert.assertEquals(DEPLOYING, vertex.getExecutionState());
            // no repeated scheduling
            try {
                vertex.deployToSlot(slot);
                Assert.fail("Scheduled from wrong state");
            } catch (IllegalStateException e) {
                // as expected
            }
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(DEPLOYING)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(RUNNING)) == 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDeployFailedSynchronous() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid, new DirectScheduledExecutorService());
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            final LogicalSlot slot = new TestingLogicalSlot(new ExecutionVertexDeploymentTest.SubmitFailingSimpleAckingTaskManagerGateway());
            Assert.assertEquals(CREATED, vertex.getExecutionState());
            vertex.deployToSlot(slot);
            Assert.assertEquals(FAILED, vertex.getExecutionState());
            Assert.assertNotNull(vertex.getFailureCause());
            Assert.assertTrue(vertex.getFailureCause().getMessage().contains(ExecutionGraphTestUtils.ERROR_MESSAGE));
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(DEPLOYING)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(FAILED)) > 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDeployFailedAsynchronously() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid);
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            final LogicalSlot slot = new TestingLogicalSlot(new ExecutionVertexDeploymentTest.SubmitFailingSimpleAckingTaskManagerGateway());
            Assert.assertEquals(CREATED, vertex.getExecutionState());
            vertex.deployToSlot(slot);
            // wait until the state transition must be done
            for (int i = 0; i < 100; i++) {
                if (((vertex.getExecutionState()) == (ExecutionState.FAILED)) && ((vertex.getFailureCause()) != null)) {
                    break;
                } else {
                    Thread.sleep(10);
                }
            }
            Assert.assertEquals(FAILED, vertex.getExecutionState());
            Assert.assertNotNull(vertex.getFailureCause());
            Assert.assertTrue(vertex.getFailureCause().getMessage().contains(ExecutionGraphTestUtils.ERROR_MESSAGE));
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(DEPLOYING)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(FAILED)) > 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testFailExternallyDuringDeploy() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid, new DirectScheduledExecutorService());
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            TestingLogicalSlot testingLogicalSlot = new TestingLogicalSlot(new ExecutionVertexDeploymentTest.SubmitBlockingSimpleAckingTaskManagerGateway());
            Assert.assertEquals(CREATED, vertex.getExecutionState());
            vertex.deployToSlot(testingLogicalSlot);
            Assert.assertEquals(DEPLOYING, vertex.getExecutionState());
            Exception testError = new Exception("test error");
            vertex.fail(testError);
            Assert.assertEquals(FAILED, vertex.getExecutionState());
            Assert.assertEquals(testError, vertex.getFailureCause());
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(DEPLOYING)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(FAILED)) > 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    private static class SubmitFailingSimpleAckingTaskManagerGateway extends SimpleAckingTaskManagerGateway {
        @Override
        public CompletableFuture<Acknowledge> submitTask(TaskDeploymentDescriptor tdd, Time timeout) {
            CompletableFuture<Acknowledge> future = new CompletableFuture<>();
            future.completeExceptionally(new Exception(ExecutionGraphTestUtils.ERROR_MESSAGE));
            return future;
        }
    }

    private static class SubmitBlockingSimpleAckingTaskManagerGateway extends SimpleAckingTaskManagerGateway {
        @Override
        public CompletableFuture<Acknowledge> submitTask(TaskDeploymentDescriptor tdd, Time timeout) {
            return new CompletableFuture<>();
        }
    }

    /**
     * Tests that the lazy scheduling flag is correctly forwarded to the produced partition descriptors.
     */
    @Test
    public void testTddProducedPartitionsLazyScheduling() throws Exception {
        ExecutionJobVertex jobVertex = ExecutionGraphTestUtils.getExecutionVertex(new JobVertexID(), new DirectScheduledExecutorService());
        IntermediateResult result = new IntermediateResult(new IntermediateDataSetID(), jobVertex, 1, ResultPartitionType.PIPELINED);
        ExecutionVertex vertex = new ExecutionVertex(jobVertex, 0, new IntermediateResult[]{ result }, Time.minutes(1));
        ExecutionEdge mockEdge = createMockExecutionEdge(1);
        result.getPartitions()[0].addConsumerGroup();
        result.getPartitions()[0].addConsumer(mockEdge, 0);
        SlotContext slotContext = Mockito.mock(SlotContext.class);
        Mockito.when(slotContext.getAllocationId()).thenReturn(new AllocationID());
        LogicalSlot slot = Mockito.mock(LogicalSlot.class);
        Mockito.when(slot.getAllocationId()).thenReturn(new AllocationID());
        for (ScheduleMode mode : ScheduleMode.values()) {
            vertex.getExecutionGraph().setScheduleMode(mode);
            TaskDeploymentDescriptor tdd = vertex.createDeploymentDescriptor(new ExecutionAttemptID(), slot, null, 1);
            Collection<ResultPartitionDeploymentDescriptor> producedPartitions = tdd.getProducedPartitions();
            Assert.assertEquals(1, producedPartitions.size());
            ResultPartitionDeploymentDescriptor desc = producedPartitions.iterator().next();
            Assert.assertEquals(mode.allowLazyDeployment(), desc.sendScheduleOrUpdateConsumersMessage());
        }
    }
}

