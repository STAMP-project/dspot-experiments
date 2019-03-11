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


import ExecutionState.DEPLOYING;
import InputDependencyConstraint.ALL;
import InputDependencyConstraint.ANY;
import java.util.List;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the inputs constraint for {@link ExecutionVertex}.
 */
public class ExecutionVertexInputConstraintTest extends TestLogger {
    private TestingComponentMainThreadExecutorServiceAdapter mainThreadExecutor = TestingComponentMainThreadExecutorServiceAdapter.forMainThread();

    @Test
    public void testInputConsumable() throws Exception {
        List<JobVertex> vertices = ExecutionVertexInputConstraintTest.createOrderedVertices();
        ExecutionGraph eg = ExecutionVertexInputConstraintTest.createExecutionGraph(vertices, ALL);
        ExecutionVertex ev11 = eg.getJobVertex(vertices.get(0).getID()).getTaskVertices()[0];
        ExecutionVertex ev21 = eg.getJobVertex(vertices.get(1).getID()).getTaskVertices()[0];
        ExecutionVertex ev22 = eg.getJobVertex(vertices.get(1).getID()).getTaskVertices()[1];
        ExecutionVertex ev31 = eg.getJobVertex(vertices.get(2).getID()).getTaskVertices()[0];
        ExecutionVertex ev32 = eg.getJobVertex(vertices.get(2).getID()).getTaskVertices()[1];
        eg.start(mainThreadExecutor);
        eg.scheduleForExecution();
        // Inputs not consumable on init
        Assert.assertFalse(ev31.isInputConsumable(0));
        Assert.assertFalse(ev31.isInputConsumable(1));
        // One pipelined input consumable on data produced
        IntermediateResultPartition partition11 = ev11.getProducedPartitions().values().iterator().next();
        ev11.scheduleOrUpdateConsumers(new org.apache.flink.runtime.io.network.partition.ResultPartitionID(partition11.getPartitionId(), ev11.getCurrentExecutionAttempt().getAttemptId()));
        Assert.assertTrue(ev31.isInputConsumable(0));
        // Input0 of ev32 is not consumable. It consumes the same PIPELINED result with ev31 but not the same partition
        Assert.assertFalse(ev32.isInputConsumable(0));
        // The blocking input not consumable if only one partition is FINISHED
        ev21.getCurrentExecutionAttempt().markFinished();
        Assert.assertFalse(ev31.isInputConsumable(1));
        // The blocking input consumable if all partitions are FINISHED
        ev22.getCurrentExecutionAttempt().markFinished();
        Assert.assertTrue(ev31.isInputConsumable(1));
        // Inputs not consumable after failover
        ev11.fail(new Exception());
        waitUntilJobRestarted(eg);
        Assert.assertFalse(ev31.isInputConsumable(0));
        Assert.assertFalse(ev31.isInputConsumable(1));
    }

    @Test
    public void testInputConstraintANY() throws Exception {
        List<JobVertex> vertices = ExecutionVertexInputConstraintTest.createOrderedVertices();
        ExecutionGraph eg = ExecutionVertexInputConstraintTest.createExecutionGraph(vertices, ANY);
        ExecutionVertex ev11 = eg.getJobVertex(vertices.get(0).getID()).getTaskVertices()[0];
        ExecutionVertex ev21 = eg.getJobVertex(vertices.get(1).getID()).getTaskVertices()[0];
        ExecutionVertex ev22 = eg.getJobVertex(vertices.get(1).getID()).getTaskVertices()[1];
        ExecutionVertex ev31 = eg.getJobVertex(vertices.get(2).getID()).getTaskVertices()[0];
        eg.start(mainThreadExecutor);
        eg.scheduleForExecution();
        // Inputs constraint not satisfied on init
        Assert.assertFalse(ev31.checkInputDependencyConstraints());
        // Input1 consumable satisfies the constraint
        IntermediateResultPartition partition11 = ev11.getProducedPartitions().values().iterator().next();
        ev11.scheduleOrUpdateConsumers(new org.apache.flink.runtime.io.network.partition.ResultPartitionID(partition11.getPartitionId(), ev11.getCurrentExecutionAttempt().getAttemptId()));
        Assert.assertTrue(ev31.checkInputDependencyConstraints());
        // Inputs constraint not satisfied after failover
        ev11.fail(new Exception());
        waitUntilJobRestarted(eg);
        Assert.assertFalse(ev31.checkInputDependencyConstraints());
        // Input2 consumable satisfies the constraint
        ExecutionGraphTestUtils.waitUntilExecutionVertexState(ev21, DEPLOYING, 2000L);
        ExecutionGraphTestUtils.waitUntilExecutionVertexState(ev22, DEPLOYING, 2000L);
        ev21.getCurrentExecutionAttempt().markFinished();
        ev22.getCurrentExecutionAttempt().markFinished();
        Assert.assertTrue(ev31.checkInputDependencyConstraints());
    }

    @Test
    public void testInputConstraintALL() throws Exception {
        List<JobVertex> vertices = ExecutionVertexInputConstraintTest.createOrderedVertices();
        ExecutionGraph eg = ExecutionVertexInputConstraintTest.createExecutionGraph(vertices, ALL);
        ExecutionVertex ev11 = eg.getJobVertex(vertices.get(0).getID()).getTaskVertices()[0];
        ExecutionVertex ev21 = eg.getJobVertex(vertices.get(1).getID()).getTaskVertices()[0];
        ExecutionVertex ev22 = eg.getJobVertex(vertices.get(1).getID()).getTaskVertices()[1];
        ExecutionVertex ev31 = eg.getJobVertex(vertices.get(2).getID()).getTaskVertices()[0];
        eg.start(mainThreadExecutor);
        eg.scheduleForExecution();
        // Inputs constraint not satisfied on init
        Assert.assertFalse(ev31.checkInputDependencyConstraints());
        // Input1 consumable does not satisfy the constraint
        IntermediateResultPartition partition11 = ev11.getProducedPartitions().values().iterator().next();
        ev11.scheduleOrUpdateConsumers(new org.apache.flink.runtime.io.network.partition.ResultPartitionID(partition11.getPartitionId(), ev11.getCurrentExecutionAttempt().getAttemptId()));
        Assert.assertFalse(ev31.checkInputDependencyConstraints());
        // Input2 consumable satisfies the constraint
        ev21.getCurrentExecutionAttempt().markFinished();
        ev22.getCurrentExecutionAttempt().markFinished();
        Assert.assertTrue(ev31.checkInputDependencyConstraints());
        // Inputs constraint not satisfied after failover
        ev11.fail(new Exception());
        waitUntilJobRestarted(eg);
        Assert.assertFalse(ev31.checkInputDependencyConstraints());
    }
}

