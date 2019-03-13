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
import ExecutionState.SCHEDULED;
import LocationPreferenceConstraint.ALL;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobmaster.LogicalSlot;
import org.apache.flink.runtime.jobmaster.TestingLogicalSlot;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


public class ExecutionVertexSchedulingTest extends TestLogger {
    @Test
    public void testSlotReleasedWhenScheduledImmediately() {
        try {
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(new JobVertexID());
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            // a slot than cannot be deployed to
            final LogicalSlot slot = new TestingLogicalSlot();
            slot.releaseSlot(new Exception("Test Exception"));
            Assert.assertFalse(slot.isAlive());
            CompletableFuture<LogicalSlot> future = new CompletableFuture<>();
            future.complete(slot);
            Assert.assertEquals(CREATED, vertex.getExecutionState());
            // try to deploy to the slot
            vertex.scheduleForExecution(new TestingSlotProvider(( i) -> future), false, ALL, Collections.emptySet());
            // will have failed
            Assert.assertEquals(FAILED, vertex.getExecutionState());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSlotReleasedWhenScheduledQueued() {
        try {
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(new JobVertexID());
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            // a slot than cannot be deployed to
            final LogicalSlot slot = new TestingLogicalSlot();
            slot.releaseSlot(new Exception("Test Exception"));
            Assert.assertFalse(slot.isAlive());
            final CompletableFuture<LogicalSlot> future = new CompletableFuture<>();
            Assert.assertEquals(CREATED, vertex.getExecutionState());
            // try to deploy to the slot
            vertex.scheduleForExecution(new TestingSlotProvider(( ignore) -> future), true, ALL, Collections.emptySet());
            // future has not yet a slot
            Assert.assertEquals(SCHEDULED, vertex.getExecutionState());
            future.complete(slot);
            // will have failed
            Assert.assertEquals(FAILED, vertex.getExecutionState());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testScheduleToDeploying() {
        try {
            final ExecutionJobVertex ejv = ExecutionGraphTestUtils.getExecutionVertex(new JobVertexID());
            final ExecutionVertex vertex = new ExecutionVertex(ejv, 0, new IntermediateResult[0], AkkaUtils.getDefaultTimeout());
            final LogicalSlot slot = new TestingLogicalSlot();
            CompletableFuture<LogicalSlot> future = CompletableFuture.completedFuture(slot);
            Assert.assertEquals(CREATED, vertex.getExecutionState());
            // try to deploy to the slot
            vertex.scheduleForExecution(new TestingSlotProvider(( ignore) -> future), false, ALL, Collections.emptySet());
            Assert.assertEquals(DEPLOYING, vertex.getExecutionState());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

