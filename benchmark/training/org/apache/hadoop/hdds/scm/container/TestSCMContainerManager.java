/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.hdds.scm.container;


import ContainerReplicaProto.State.CLOSED;
import LifeCycleEvent.CLOSE;
import LifeCycleEvent.FINALIZE;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.XceiverClientManager;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.hdds.scm.pipeline.PipelineManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for Container ContainerManager.
 */
public class TestSCMContainerManager {
    private static SCMContainerManager containerManager;

    private static MockNodeManager nodeManager;

    private static PipelineManager pipelineManager;

    private static File testDir;

    private static XceiverClientManager xceiverClientManager;

    private static String containerOwner = "OZONE";

    private static Random random;

    private static final long TIMEOUT = 10000;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testallocateContainer() throws Exception {
        ContainerInfo containerInfo = TestSCMContainerManager.containerManager.allocateContainer(TestSCMContainerManager.xceiverClientManager.getType(), TestSCMContainerManager.xceiverClientManager.getFactor(), TestSCMContainerManager.containerOwner);
        Assert.assertNotNull(containerInfo);
    }

    @Test
    public void testallocateContainerDistributesAllocation() throws Exception {
        /* This is a lame test, we should really be testing something like
        z-score or make sure that we don't have 3sigma kind of events. Too lazy
        to write all that code. This test very lamely tests if we have more than
        5 separate nodes  from the list of 10 datanodes that got allocated a
        container.
         */
        Set<UUID> pipelineList = new TreeSet<>();
        for (int x = 0; x < 30; x++) {
            ContainerInfo containerInfo = TestSCMContainerManager.containerManager.allocateContainer(TestSCMContainerManager.xceiverClientManager.getType(), TestSCMContainerManager.xceiverClientManager.getFactor(), TestSCMContainerManager.containerOwner);
            Assert.assertNotNull(containerInfo);
            Assert.assertNotNull(containerInfo.getPipelineID());
            pipelineList.add(TestSCMContainerManager.pipelineManager.getPipeline(containerInfo.getPipelineID()).getFirstNode().getUuid());
        }
        Assert.assertTrue(((pipelineList.size()) > 5));
    }

    @Test
    public void testGetContainer() throws IOException {
        ContainerInfo containerInfo = TestSCMContainerManager.containerManager.allocateContainer(TestSCMContainerManager.xceiverClientManager.getType(), TestSCMContainerManager.xceiverClientManager.getFactor(), TestSCMContainerManager.containerOwner);
        Assert.assertNotNull(containerInfo);
        Pipeline pipeline = TestSCMContainerManager.pipelineManager.getPipeline(containerInfo.getPipelineID());
        Assert.assertNotNull(pipeline);
        Assert.assertEquals(containerInfo, TestSCMContainerManager.containerManager.getContainer(containerInfo.containerID()));
    }

    @Test
    public void testGetContainerWithPipeline() throws Exception {
        ContainerInfo contInfo = TestSCMContainerManager.containerManager.allocateContainer(TestSCMContainerManager.xceiverClientManager.getType(), TestSCMContainerManager.xceiverClientManager.getFactor(), TestSCMContainerManager.containerOwner);
        // Add dummy replicas for container.
        Iterator<DatanodeDetails> nodes = TestSCMContainerManager.pipelineManager.getPipeline(contInfo.getPipelineID()).getNodes().iterator();
        DatanodeDetails dn1 = nodes.next();
        TestSCMContainerManager.containerManager.updateContainerState(contInfo.containerID(), FINALIZE);
        TestSCMContainerManager.containerManager.updateContainerState(contInfo.containerID(), CLOSE);
        ContainerInfo finalContInfo = contInfo;
        Assert.assertEquals(0, TestSCMContainerManager.containerManager.getContainerReplicas(finalContInfo.containerID()).size());
        TestSCMContainerManager.containerManager.updateContainerReplica(contInfo.containerID(), ContainerReplica.newBuilder().setContainerID(contInfo.containerID()).setContainerState(CLOSED).setDatanodeDetails(dn1).build());
        Assert.assertEquals(1, TestSCMContainerManager.containerManager.getContainerReplicas(finalContInfo.containerID()).size());
        contInfo = TestSCMContainerManager.containerManager.getContainer(contInfo.containerID());
        Assert.assertEquals(contInfo.getState(), LifeCycleState.CLOSED);
        // After closing the container, we should get the replica and construct
        // standalone pipeline. No more ratis pipeline.
        Set<DatanodeDetails> replicaNodes = TestSCMContainerManager.containerManager.getContainerReplicas(contInfo.containerID()).stream().map(ContainerReplica::getDatanodeDetails).collect(Collectors.toSet());
        Assert.assertTrue(replicaNodes.contains(dn1));
    }

    @Test
    public void testgetNoneExistentContainer() {
        try {
            TestSCMContainerManager.containerManager.getContainer(ContainerID.valueof(((TestSCMContainerManager.random.nextInt()) & (Integer.MAX_VALUE))));
            Assert.fail();
        } catch (ContainerNotFoundException ex) {
            // Success!
        }
    }

    @Test
    public void testCloseContainer() throws IOException {
        ContainerID id = createContainer().containerID();
        TestSCMContainerManager.containerManager.updateContainerState(id, HddsProtos.LifeCycleEvent.FINALIZE);
        TestSCMContainerManager.containerManager.updateContainerState(id, HddsProtos.LifeCycleEvent.CLOSE);
        ContainerInfo closedContainer = TestSCMContainerManager.containerManager.getContainer(id);
        Assert.assertEquals(LifeCycleState.CLOSED, closedContainer.getState());
    }
}

