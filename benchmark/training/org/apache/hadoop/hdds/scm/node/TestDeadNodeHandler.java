/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.scm.node;


import GenericTestUtils.LogCapturer;
import java.io.IOException;
import java.util.Set;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.StorageReportProto;
import org.apache.hadoop.hdds.scm.TestUtils;
import org.apache.hadoop.hdds.scm.container.ContainerInfo;
import org.apache.hadoop.hdds.scm.container.ContainerManager;
import org.apache.hadoop.hdds.scm.container.ContainerReplica;
import org.apache.hadoop.hdds.scm.node.states.NodeNotFoundException;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.hdds.server.events.EventPublisher;
import org.apache.hadoop.hdds.server.events.EventQueue;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.event.Level;


/**
 * Test DeadNodeHandler.
 */
public class TestDeadNodeHandler {
    private StorageContainerManager scm;

    private SCMNodeManager nodeManager;

    private ContainerManager containerManager;

    private NodeReportHandler nodeReportHandler;

    private DeadNodeHandler deadNodeHandler;

    private EventPublisher publisher;

    private EventQueue eventQueue;

    private String storageDir;

    @Test
    public void testOnMessage() throws IOException, NodeNotFoundException {
        // GIVEN
        DatanodeDetails datanode1 = TestUtils.randomDatanodeDetails();
        DatanodeDetails datanode2 = TestUtils.randomDatanodeDetails();
        DatanodeDetails datanode3 = TestUtils.randomDatanodeDetails();
        String storagePath = GenericTestUtils.getRandomizedTempPath().concat(("/" + (datanode1.getUuidString())));
        StorageReportProto storageOne = TestUtils.createStorageReport(datanode1.getUuid(), storagePath, 100, 10, 90, null);
        // Standalone pipeline now excludes the nodes which are already used,
        // is the a proper behavior. Adding 9 datanodes for now to make the
        // test case happy.
        nodeManager.register(datanode1, TestUtils.createNodeReport(storageOne), null);
        nodeManager.register(datanode2, TestUtils.createNodeReport(storageOne), null);
        nodeManager.register(datanode3, TestUtils.createNodeReport(storageOne), null);
        nodeManager.register(TestUtils.randomDatanodeDetails(), TestUtils.createNodeReport(storageOne), null);
        nodeManager.register(TestUtils.randomDatanodeDetails(), TestUtils.createNodeReport(storageOne), null);
        nodeManager.register(TestUtils.randomDatanodeDetails(), TestUtils.createNodeReport(storageOne), null);
        nodeManager.register(TestUtils.randomDatanodeDetails(), TestUtils.createNodeReport(storageOne), null);
        nodeManager.register(TestUtils.randomDatanodeDetails(), TestUtils.createNodeReport(storageOne), null);
        nodeManager.register(TestUtils.randomDatanodeDetails(), TestUtils.createNodeReport(storageOne), null);
        ContainerInfo container1 = TestUtils.allocateContainer(containerManager);
        ContainerInfo container2 = TestUtils.allocateContainer(containerManager);
        ContainerInfo container3 = TestUtils.allocateContainer(containerManager);
        ContainerInfo container4 = TestUtils.allocateContainer(containerManager);
        registerContainers(datanode1, container1, container2, container4);
        registerContainers(datanode2, container1, container2);
        registerContainers(datanode3, container3);
        registerReplicas(containerManager, container1, datanode1, datanode2);
        registerReplicas(containerManager, container2, datanode1, datanode2);
        registerReplicas(containerManager, container3, datanode3);
        registerReplicas(containerManager, container4, datanode1);
        TestUtils.closeContainer(containerManager, container1.containerID());
        TestUtils.closeContainer(containerManager, container2.containerID());
        TestUtils.quasiCloseContainer(containerManager, container3.containerID());
        GenericTestUtils.setLogLevel(DeadNodeHandler.getLogger(), Level.DEBUG);
        GenericTestUtils.LogCapturer logCapturer = LogCapturer.captureLogs(DeadNodeHandler.getLogger());
        deadNodeHandler.onMessage(datanode1, publisher);
        Set<ContainerReplica> container1Replicas = containerManager.getContainerReplicas(new org.apache.hadoop.hdds.scm.container.ContainerID(container1.getContainerID()));
        Assert.assertEquals(1, container1Replicas.size());
        Assert.assertEquals(datanode2, container1Replicas.iterator().next().getDatanodeDetails());
        Set<ContainerReplica> container2Replicas = containerManager.getContainerReplicas(new org.apache.hadoop.hdds.scm.container.ContainerID(container2.getContainerID()));
        Assert.assertEquals(1, container2Replicas.size());
        Assert.assertEquals(datanode2, container2Replicas.iterator().next().getDatanodeDetails());
        Set<ContainerReplica> container3Replicas = containerManager.getContainerReplicas(new org.apache.hadoop.hdds.scm.container.ContainerID(container3.getContainerID()));
        Assert.assertEquals(1, container3Replicas.size());
        Assert.assertEquals(datanode3, container3Replicas.iterator().next().getDatanodeDetails());
        // Replicate should be fired for container 1 and container 2 as now
        // datanode 1 is dead, these 2 will not match with expected replica count
        // and their state is one of CLOSED/QUASI_CLOSE.
        Assert.assertTrue(logCapturer.getOutput().contains(("Replicate Request fired for container " + (container1.getContainerID()))));
        Assert.assertTrue(logCapturer.getOutput().contains(("Replicate Request fired for container " + (container2.getContainerID()))));
        // as container4 is still in open state, replicate event should not have
        // fired for this.
        Assert.assertFalse(logCapturer.getOutput().contains(("Replicate Request fired for container " + (container4.getContainerID()))));
    }

    @Test
    public void testOnMessageReplicaFailure() throws Exception {
        DatanodeDetails datanode1 = TestUtils.randomDatanodeDetails();
        DatanodeDetails datanode2 = TestUtils.randomDatanodeDetails();
        DatanodeDetails datanode3 = TestUtils.randomDatanodeDetails();
        String storagePath = GenericTestUtils.getRandomizedTempPath().concat(("/" + (datanode1.getUuidString())));
        StorageReportProto storageOne = TestUtils.createStorageReport(datanode1.getUuid(), storagePath, 100, 10, 90, null);
        nodeManager.register(datanode1, TestUtils.createNodeReport(storageOne), null);
        nodeManager.register(datanode2, TestUtils.createNodeReport(storageOne), null);
        nodeManager.register(datanode3, TestUtils.createNodeReport(storageOne), null);
        DatanodeDetails dn1 = TestUtils.randomDatanodeDetails();
        GenericTestUtils.LogCapturer logCapturer = LogCapturer.captureLogs(DeadNodeHandler.getLogger());
        nodeReportHandler.onMessage(getNodeReport(dn1, storageOne), Mockito.mock(EventPublisher.class));
        ContainerInfo container1 = TestUtils.allocateContainer(containerManager);
        TestUtils.closeContainer(containerManager, container1.containerID());
        deadNodeHandler.onMessage(dn1, eventQueue);
        Assert.assertTrue(logCapturer.getOutput().contains("DeadNode event for a unregistered node"));
    }
}

