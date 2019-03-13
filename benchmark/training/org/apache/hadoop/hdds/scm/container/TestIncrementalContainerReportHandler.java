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
package org.apache.hadoop.hdds.scm.container;


import LifeCycleEvent.CLOSE;
import LifeCycleEvent.FORCE_CLOSE;
import LifeCycleEvent.QUASI_CLOSE;
import LifeCycleState.CLOSED;
import LifeCycleState.CLOSING;
import LifeCycleState.QUASI_CLOSED;
import java.io.IOException;
import java.util.Set;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.IncrementalContainerReportProto;
import org.apache.hadoop.hdds.scm.TestUtils;
import org.apache.hadoop.hdds.scm.pipeline.PipelineManager;
import org.apache.hadoop.hdds.scm.server.SCMDatanodeHeartbeatDispatcher.IncrementalContainerReportFromDatanode;
import org.apache.hadoop.hdds.server.events.EventPublisher;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test cases to verify the functionality of IncrementalContainerReportHandler.
 */
public class TestIncrementalContainerReportHandler {
    @Test
    public void testClosingToClosed() throws IOException {
        final ContainerManager containerManager = Mockito.mock(ContainerManager.class);
        final PipelineManager pipelineManager = Mockito.mock(PipelineManager.class);
        final IncrementalContainerReportHandler reportHandler = new IncrementalContainerReportHandler(pipelineManager, containerManager);
        final ContainerInfo container = TestContainerReportHelper.getContainer(CLOSING);
        final DatanodeDetails datanodeOne = TestUtils.randomDatanodeDetails();
        final DatanodeDetails datanodeTwo = TestUtils.randomDatanodeDetails();
        final DatanodeDetails datanodeThree = TestUtils.randomDatanodeDetails();
        final Set<ContainerReplica> containerReplicas = TestContainerReportHelper.getReplicas(container.containerID(), ContainerReplicaProto.State.CLOSING, datanodeOne, datanodeTwo, datanodeThree);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, container, containerReplicas);
        TestContainerReportHelper.mockUpdateContainerReplica(containerManager, container, containerReplicas);
        TestContainerReportHelper.mockUpdateContainerState(containerManager, container, CLOSE, CLOSED);
        final IncrementalContainerReportProto containerReport = TestIncrementalContainerReportHandler.getIncrementalContainerReportProto(container.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne.getUuidString());
        final EventPublisher publisher = Mockito.mock(EventPublisher.class);
        final IncrementalContainerReportFromDatanode icrFromDatanode = new IncrementalContainerReportFromDatanode(datanodeOne, containerReport);
        reportHandler.onMessage(icrFromDatanode, publisher);
        Assert.assertEquals(CLOSED, container.getState());
    }

    @Test
    public void testClosingToQuasiClosed() throws IOException {
        final ContainerManager containerManager = Mockito.mock(ContainerManager.class);
        final PipelineManager pipelineManager = Mockito.mock(PipelineManager.class);
        final IncrementalContainerReportHandler reportHandler = new IncrementalContainerReportHandler(pipelineManager, containerManager);
        final ContainerInfo container = TestContainerReportHelper.getContainer(CLOSING);
        final DatanodeDetails datanodeOne = TestUtils.randomDatanodeDetails();
        final DatanodeDetails datanodeTwo = TestUtils.randomDatanodeDetails();
        final DatanodeDetails datanodeThree = TestUtils.randomDatanodeDetails();
        final Set<ContainerReplica> containerReplicas = TestContainerReportHelper.getReplicas(container.containerID(), ContainerReplicaProto.State.CLOSING, datanodeOne, datanodeTwo, datanodeThree);
        TestContainerReportHelper.addContainerToContainerManager(containerManager, container, containerReplicas);
        TestContainerReportHelper.mockUpdateContainerReplica(containerManager, container, containerReplicas);
        TestContainerReportHelper.mockUpdateContainerState(containerManager, container, QUASI_CLOSE, QUASI_CLOSED);
        final IncrementalContainerReportProto containerReport = TestIncrementalContainerReportHandler.getIncrementalContainerReportProto(container.containerID(), ContainerReplicaProto.State.QUASI_CLOSED, datanodeOne.getUuidString());
        final EventPublisher publisher = Mockito.mock(EventPublisher.class);
        final IncrementalContainerReportFromDatanode icrFromDatanode = new IncrementalContainerReportFromDatanode(datanodeOne, containerReport);
        reportHandler.onMessage(icrFromDatanode, publisher);
        Assert.assertEquals(QUASI_CLOSED, container.getState());
    }

    @Test
    public void testQuasiClosedToClosed() throws IOException {
        final ContainerManager containerManager = Mockito.mock(ContainerManager.class);
        final PipelineManager pipelineManager = Mockito.mock(PipelineManager.class);
        final IncrementalContainerReportHandler reportHandler = new IncrementalContainerReportHandler(pipelineManager, containerManager);
        final ContainerInfo container = TestContainerReportHelper.getContainer(QUASI_CLOSED);
        final DatanodeDetails datanodeOne = TestUtils.randomDatanodeDetails();
        final DatanodeDetails datanodeTwo = TestUtils.randomDatanodeDetails();
        final DatanodeDetails datanodeThree = TestUtils.randomDatanodeDetails();
        final Set<ContainerReplica> containerReplicas = TestContainerReportHelper.getReplicas(container.containerID(), ContainerReplicaProto.State.CLOSING, datanodeOne, datanodeTwo);
        containerReplicas.addAll(TestContainerReportHelper.getReplicas(container.containerID(), ContainerReplicaProto.State.QUASI_CLOSED, datanodeThree));
        TestContainerReportHelper.addContainerToContainerManager(containerManager, container, containerReplicas);
        TestContainerReportHelper.mockUpdateContainerReplica(containerManager, container, containerReplicas);
        TestContainerReportHelper.mockUpdateContainerState(containerManager, container, FORCE_CLOSE, CLOSED);
        final IncrementalContainerReportProto containerReport = TestIncrementalContainerReportHandler.getIncrementalContainerReportProto(container.containerID(), ContainerReplicaProto.State.QUASI_CLOSED, datanodeOne.getUuidString(), 999999L);
        final EventPublisher publisher = Mockito.mock(EventPublisher.class);
        final IncrementalContainerReportFromDatanode icrFromDatanode = new IncrementalContainerReportFromDatanode(datanodeOne, containerReport);
        reportHandler.onMessage(icrFromDatanode, publisher);
        // SCM should issue force close.
        Mockito.verify(publisher, Mockito.times(1)).fireEvent(Mockito.any(), Mockito.any());
        final IncrementalContainerReportProto containerReportTwo = TestIncrementalContainerReportHandler.getIncrementalContainerReportProto(container.containerID(), ContainerReplicaProto.State.CLOSED, datanodeOne.getUuidString(), 999999L);
        final IncrementalContainerReportFromDatanode icrTwoFromDatanode = new IncrementalContainerReportFromDatanode(datanodeOne, containerReportTwo);
        reportHandler.onMessage(icrTwoFromDatanode, publisher);
        Assert.assertEquals(CLOSED, container.getState());
    }
}

