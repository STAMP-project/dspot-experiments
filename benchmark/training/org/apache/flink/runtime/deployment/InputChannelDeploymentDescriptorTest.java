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
package org.apache.flink.runtime.deployment;


import ExecutionState.CREATED;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.execution.ExecutionState;
import org.apache.flink.runtime.executiongraph.ExecutionEdge;
import org.apache.flink.runtime.executiongraph.ExecutionGraphException;
import org.apache.flink.runtime.executiongraph.ExecutionVertex;
import org.apache.flink.runtime.executiongraph.IntermediateResultPartition;
import org.apache.flink.runtime.io.network.ConnectionID;
import org.apache.flink.runtime.io.network.partition.ResultPartitionID;
import org.apache.flink.runtime.jobmaster.LogicalSlot;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for the {@link InputChannelDeploymentDescriptor}.
 */
public class InputChannelDeploymentDescriptorTest {
    /**
     * Tests the deployment descriptors for local, remote, and unknown partition
     * locations (with lazy deployment allowed and all execution states for the
     * producers).
     */
    @Test
    public void testMixedLocalRemoteUnknownDeployment() throws Exception {
        boolean allowLazyDeployment = true;
        ResourceID consumerResourceId = ResourceID.generate();
        ExecutionVertex consumer = Mockito.mock(ExecutionVertex.class);
        LogicalSlot consumerSlot = InputChannelDeploymentDescriptorTest.mockSlot(consumerResourceId);
        // Local and remote channel are only allowed for certain execution
        // states.
        for (ExecutionState state : ExecutionState.values()) {
            // Local partition
            ExecutionVertex localProducer = InputChannelDeploymentDescriptorTest.mockExecutionVertex(state, consumerResourceId);
            IntermediateResultPartition localPartition = InputChannelDeploymentDescriptorTest.mockPartition(localProducer);
            ResultPartitionID localPartitionId = new ResultPartitionID(localPartition.getPartitionId(), localProducer.getCurrentExecutionAttempt().getAttemptId());
            ExecutionEdge localEdge = new ExecutionEdge(localPartition, consumer, 0);
            // Remote partition
            ExecutionVertex remoteProducer = InputChannelDeploymentDescriptorTest.mockExecutionVertex(state, ResourceID.generate());// new resource ID

            IntermediateResultPartition remotePartition = InputChannelDeploymentDescriptorTest.mockPartition(remoteProducer);
            ResultPartitionID remotePartitionId = new ResultPartitionID(remotePartition.getPartitionId(), remoteProducer.getCurrentExecutionAttempt().getAttemptId());
            ConnectionID remoteConnectionId = new ConnectionID(remoteProducer.getCurrentAssignedResource().getTaskManagerLocation(), 0);
            ExecutionEdge remoteEdge = new ExecutionEdge(remotePartition, consumer, 1);
            // Unknown partition
            ExecutionVertex unknownProducer = InputChannelDeploymentDescriptorTest.mockExecutionVertex(state, null);// no assigned resource

            IntermediateResultPartition unknownPartition = InputChannelDeploymentDescriptorTest.mockPartition(unknownProducer);
            ResultPartitionID unknownPartitionId = new ResultPartitionID(unknownPartition.getPartitionId(), unknownProducer.getCurrentExecutionAttempt().getAttemptId());
            ExecutionEdge unknownEdge = new ExecutionEdge(unknownPartition, consumer, 2);
            InputChannelDeploymentDescriptor[] desc = InputChannelDeploymentDescriptor.fromEdges(new ExecutionEdge[]{ localEdge, remoteEdge, unknownEdge }, consumerSlot.getTaskManagerLocation().getResourceID(), allowLazyDeployment);
            Assert.assertEquals(3, desc.length);
            // These states are allowed
            if ((((state == (ExecutionState.RUNNING)) || (state == (ExecutionState.FINISHED))) || (state == (ExecutionState.SCHEDULED))) || (state == (ExecutionState.DEPLOYING))) {
                // Create local or remote channels
                Assert.assertEquals(localPartitionId, desc[0].getConsumedPartitionId());
                Assert.assertTrue(desc[0].getConsumedPartitionLocation().isLocal());
                Assert.assertNull(desc[0].getConsumedPartitionLocation().getConnectionId());
                Assert.assertEquals(remotePartitionId, desc[1].getConsumedPartitionId());
                Assert.assertTrue(desc[1].getConsumedPartitionLocation().isRemote());
                Assert.assertEquals(remoteConnectionId, desc[1].getConsumedPartitionLocation().getConnectionId());
            } else {
                // Unknown (lazy deployment allowed)
                Assert.assertEquals(localPartitionId, desc[0].getConsumedPartitionId());
                Assert.assertTrue(desc[0].getConsumedPartitionLocation().isUnknown());
                Assert.assertNull(desc[0].getConsumedPartitionLocation().getConnectionId());
                Assert.assertEquals(remotePartitionId, desc[1].getConsumedPartitionId());
                Assert.assertTrue(desc[1].getConsumedPartitionLocation().isUnknown());
                Assert.assertNull(desc[1].getConsumedPartitionLocation().getConnectionId());
            }
            Assert.assertEquals(unknownPartitionId, desc[2].getConsumedPartitionId());
            Assert.assertTrue(desc[2].getConsumedPartitionLocation().isUnknown());
            Assert.assertNull(desc[2].getConsumedPartitionLocation().getConnectionId());
        }
    }

    @Test
    public void testUnknownChannelWithoutLazyDeploymentThrows() throws Exception {
        ResourceID consumerResourceId = ResourceID.generate();
        ExecutionVertex consumer = Mockito.mock(ExecutionVertex.class);
        LogicalSlot consumerSlot = InputChannelDeploymentDescriptorTest.mockSlot(consumerResourceId);
        // Unknown partition
        ExecutionVertex unknownProducer = InputChannelDeploymentDescriptorTest.mockExecutionVertex(CREATED, null);// no assigned resource

        IntermediateResultPartition unknownPartition = InputChannelDeploymentDescriptorTest.mockPartition(unknownProducer);
        ResultPartitionID unknownPartitionId = new ResultPartitionID(unknownPartition.getPartitionId(), unknownProducer.getCurrentExecutionAttempt().getAttemptId());
        ExecutionEdge unknownEdge = new ExecutionEdge(unknownPartition, consumer, 2);
        // This should work if lazy deployment is allowed
        boolean allowLazyDeployment = true;
        InputChannelDeploymentDescriptor[] desc = InputChannelDeploymentDescriptor.fromEdges(new ExecutionEdge[]{ unknownEdge }, consumerSlot.getTaskManagerLocation().getResourceID(), allowLazyDeployment);
        Assert.assertEquals(1, desc.length);
        Assert.assertEquals(unknownPartitionId, desc[0].getConsumedPartitionId());
        Assert.assertTrue(desc[0].getConsumedPartitionLocation().isUnknown());
        Assert.assertNull(desc[0].getConsumedPartitionLocation().getConnectionId());
        try {
            // Fail if lazy deployment is *not* allowed
            allowLazyDeployment = false;
            InputChannelDeploymentDescriptor.fromEdges(new ExecutionEdge[]{ unknownEdge }, consumerSlot.getTaskManagerLocation().getResourceID(), allowLazyDeployment);
            Assert.fail("Did not throw expected ExecutionGraphException");
        } catch (ExecutionGraphException ignored) {
        }
    }
}

