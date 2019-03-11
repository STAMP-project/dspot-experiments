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
import ExecutionState.CREATED;
import ExecutionState.RUNNING;
import ExecutionState.SCHEDULED;
import LocationPreferenceConstraint.ALL;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.execution.ExecutionState;
import org.apache.flink.runtime.executiongraph.utils.SimpleAckingTaskManagerGateway;
import org.apache.flink.runtime.instance.DummyActorGateway;
import org.apache.flink.runtime.instance.Instance;
import org.apache.flink.runtime.instance.SimpleSlot;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobmanager.slots.ActorTaskManagerGateway;
import org.apache.flink.runtime.jobmaster.LogicalSlot;
import org.apache.flink.runtime.jobmaster.TestingLogicalSlot;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.testutils.DirectScheduledExecutorService;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("serial")
public class ExecutionVertexCancelTest extends TestLogger {
    // --------------------------------------------------------------------------------------------
    // Canceling in different states
    // --------------------------------------------------------------------------------------------
    @Test
    public void testCancelFromCreated() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid);
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            Assert.assertEquals(CREATED, vertex.getExecutionState());
            vertex.cancel();
            Assert.assertEquals(CANCELED, vertex.getExecutionState());
            Assert.assertNull(vertex.getFailureCause());
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(CANCELING)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(CANCELED)) > 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCancelFromScheduled() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid);
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            ExecutionGraphTestUtils.setVertexState(vertex, SCHEDULED);
            Assert.assertEquals(SCHEDULED, vertex.getExecutionState());
            vertex.cancel();
            Assert.assertEquals(CANCELED, vertex.getExecutionState());
            Assert.assertNull(vertex.getFailureCause());
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(CANCELING)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(CANCELED)) > 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCancelFromRunning() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid, new DirectScheduledExecutorService());
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            LogicalSlot slot = new TestingLogicalSlot(new ExecutionVertexCancelTest.CancelSequenceSimpleAckingTaskManagerGateway(1));
            ExecutionGraphTestUtils.setVertexResource(vertex, slot);
            ExecutionGraphTestUtils.setVertexState(vertex, RUNNING);
            Assert.assertEquals(RUNNING, vertex.getExecutionState());
            vertex.cancel();
            vertex.getCurrentExecutionAttempt().completeCancelling();// response by task manager once actually canceled

            Assert.assertEquals(CANCELED, vertex.getExecutionState());
            Assert.assertFalse(slot.isAlive());
            Assert.assertNull(vertex.getFailureCause());
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(CANCELING)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(CANCELED)) > 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testRepeatedCancelFromRunning() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid, new DirectScheduledExecutorService());
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            LogicalSlot slot = new TestingLogicalSlot(new ExecutionVertexCancelTest.CancelSequenceSimpleAckingTaskManagerGateway(1));
            ExecutionGraphTestUtils.setVertexResource(vertex, slot);
            ExecutionGraphTestUtils.setVertexState(vertex, RUNNING);
            Assert.assertEquals(RUNNING, vertex.getExecutionState());
            vertex.cancel();
            Assert.assertEquals(CANCELING, vertex.getExecutionState());
            vertex.cancel();
            Assert.assertEquals(CANCELING, vertex.getExecutionState());
            // callback by TaskManager after canceling completes
            vertex.getCurrentExecutionAttempt().completeCancelling();
            Assert.assertEquals(CANCELED, vertex.getExecutionState());
            Assert.assertFalse(slot.isAlive());
            Assert.assertNull(vertex.getFailureCause());
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(CANCELING)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(CANCELED)) > 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCancelFromRunningDidNotFindTask() {
        // this may happen when the task finished or failed while the call was in progress
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid, new DirectScheduledExecutorService());
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            LogicalSlot slot = new TestingLogicalSlot(new ExecutionVertexCancelTest.CancelSequenceSimpleAckingTaskManagerGateway(1));
            ExecutionGraphTestUtils.setVertexResource(vertex, slot);
            ExecutionGraphTestUtils.setVertexState(vertex, RUNNING);
            Assert.assertEquals(RUNNING, vertex.getExecutionState());
            vertex.cancel();
            Assert.assertEquals(CANCELING, vertex.getExecutionState());
            Assert.assertNull(vertex.getFailureCause());
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(CANCELING)) > 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCancelCallFails() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid, new DirectScheduledExecutorService());
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            LogicalSlot slot = new TestingLogicalSlot(new ExecutionVertexCancelTest.CancelSequenceSimpleAckingTaskManagerGateway(0));
            ExecutionGraphTestUtils.setVertexResource(vertex, slot);
            ExecutionGraphTestUtils.setVertexState(vertex, RUNNING);
            Assert.assertEquals(RUNNING, vertex.getExecutionState());
            vertex.cancel();
            // Callback fails, leading to CANCELED
            Assert.assertEquals(CANCELED, vertex.getExecutionState());
            Assert.assertFalse(slot.isAlive());
            Assert.assertTrue(((vertex.getStateTimestamp(CREATED)) > 0));
            Assert.assertTrue(((vertex.getStateTimestamp(CANCELING)) > 0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSendCancelAndReceiveFail() throws Exception {
        final ExecutionGraph graph = ExecutionGraphTestUtils.createSimpleTestGraph();
        graph.scheduleForExecution();
        ExecutionGraphTestUtils.switchAllVerticesToRunning(graph);
        Assert.assertEquals(JobStatus.RUNNING, graph.getState());
        final ExecutionVertex[] vertices = graph.getVerticesTopologically().iterator().next().getTaskVertices();
        Assert.assertEquals(vertices.length, graph.getRegisteredExecutions().size());
        final Execution exec = vertices[3].getCurrentExecutionAttempt();
        exec.cancel();
        Assert.assertEquals(CANCELING, exec.getState());
        exec.markFailed(new Exception("test"));
        Assert.assertTrue((((exec.getState()) == (ExecutionState.FAILED)) || ((exec.getState()) == (ExecutionState.CANCELED))));
        Assert.assertFalse(exec.getAssignedResource().isAlive());
        Assert.assertEquals(((vertices.length) - 1), exec.getVertex().getExecutionGraph().getRegisteredExecutions().size());
    }

    // --------------------------------------------------------------------------------------------
    // Actions after a vertex has been canceled or while canceling
    // --------------------------------------------------------------------------------------------
    @Test
    public void testScheduleOrDeployAfterCancel() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid);
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            ExecutionGraphTestUtils.setVertexState(vertex, CANCELED);
            Assert.assertEquals(CANCELED, vertex.getExecutionState());
            // 1)
            // scheduling after being canceled should be tolerated (no exception) because
            // it can occur as the result of races
            {
                vertex.scheduleForExecution(new ProgrammedSlotProvider(1), false, ALL, Collections.emptySet());
                Assert.assertEquals(CANCELED, vertex.getExecutionState());
            }
            // 2)
            // deploying after canceling from CREATED needs to raise an exception, because
            // the scheduler (or any caller) needs to know that the slot should be released
            try {
                Instance instance = ExecutionGraphTestUtils.getInstance(new ActorTaskManagerGateway(DummyActorGateway.INSTANCE));
                SimpleSlot slot = instance.allocateSimpleSlot();
                vertex.deployToSlot(slot);
                Assert.fail("Method should throw an exception");
            } catch (IllegalStateException e) {
                Assert.assertEquals(CANCELED, vertex.getExecutionState());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testActionsWhileCancelling() {
        try {
            final JobVertexID jid = new JobVertexID();
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(jid);
            // scheduling while canceling is an illegal state transition
            try {
                ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
                ExecutionGraphTestUtils.setVertexState(vertex, CANCELING);
                vertex.scheduleForExecution(new ProgrammedSlotProvider(1), false, ALL, Collections.emptySet());
            } catch (Exception e) {
                Assert.fail("should not throw an exception");
            }
            // deploying while in canceling state is illegal (should immediately go to canceled)
            try {
                ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
                ExecutionGraphTestUtils.setVertexState(vertex, CANCELING);
                Instance instance = ExecutionGraphTestUtils.getInstance(new ActorTaskManagerGateway(DummyActorGateway.INSTANCE));
                SimpleSlot slot = instance.allocateSimpleSlot();
                vertex.deployToSlot(slot);
                Assert.fail("Method should throw an exception");
            } catch (IllegalStateException e) {
                // that is what we expect
            }
            // fail while canceling
            {
                ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
                Instance instance = ExecutionGraphTestUtils.getInstance(new ActorTaskManagerGateway(DummyActorGateway.INSTANCE));
                SimpleSlot slot = instance.allocateSimpleSlot();
                ExecutionGraphTestUtils.setVertexResource(vertex, slot);
                ExecutionGraphTestUtils.setVertexState(vertex, CANCELING);
                Exception failureCause = new Exception("test exception");
                vertex.fail(failureCause);
                Assert.assertEquals(CANCELED, vertex.getExecutionState());
                Assert.assertTrue(slot.isReleased());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    public static class CancelSequenceSimpleAckingTaskManagerGateway extends SimpleAckingTaskManagerGateway {
        private final int successfulOperations;

        private int index = -1;

        public CancelSequenceSimpleAckingTaskManagerGateway(int successfulOperations) {
            super();
            this.successfulOperations = successfulOperations;
        }

        @Override
        public CompletableFuture<Acknowledge> cancelTask(ExecutionAttemptID executionAttemptID, Time timeout) {
            (index)++;
            if ((index) >= (successfulOperations)) {
                return FutureUtils.completedExceptionally(new IOException("Rpc call fails"));
            } else {
                return CompletableFuture.completedFuture(Acknowledge.get());
            }
        }
    }
}

