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
package org.apache.hadoop.yarn.server.resourcemanager;


import ContainerState.COMPLETE;
import ContainerState.RUNNING;
import ContainerUpdateType.DECREASE_RESOURCE;
import ContainerUpdateType.DEMOTE_EXECUTION_TYPE;
import ContainerUpdateType.INCREASE_RESOURCE;
import ContainerUpdateType.PROMOTE_EXECUTION_TYPE;
import ExecutionType.GUARANTEED;
import ExecutionType.OPPORTUNISTIC;
import Priority.UNDEFINED;
import RMContainerState.ACQUIRED;
import YarnConfiguration.DIST_SCHEDULING_ENABLED;
import YarnConfiguration.IPC_RPC_IMPL;
import YarnConfiguration.RM_SCHEDULER_ADDRESS;
import com.google.common.base.Supplier;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocolPB;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.impl.pb.AllocateRequestPBImpl;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.ExecutionTypeRequest;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.api.records.UpdateContainerRequest;
import org.apache.hadoop.yarn.api.records.UpdatedContainer;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.ipc.HadoopYarnProtoRPC;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.yarn.server.api.DistributedSchedulingAMProtocolPB;
import org.apache.hadoop.yarn.server.api.protocolrecords.DistributedSchedulingAllocateRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.DistributedSchedulingAllocateResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterDistributedSchedulingAMResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.DistributedSchedulingAllocateRequestPBImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.AMLivelinessMonitor;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeImpl;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplication;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.TestUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager;
import org.apache.hadoop.yarn.server.scheduler.OpportunisticContainerContext;
import org.apache.hadoop.yarn.server.scheduler.SchedulerRequestKey;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for {@link OpportunisticContainerAllocatorAMService}.
 */
public class TestOpportunisticContainerAllocatorAMService {
    private static final int GB = 1024;

    private MockRM rm;

    private DrainDispatcher dispatcher;

    @Test(timeout = 600000)
    public void testContainerPromoteAndDemoteBeforeContainerStart() throws Exception {
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h1:4321", 4096, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        MockNM nm3 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nodes.put(nm3.getNodeId(), nm3);
        MockNM nm4 = new MockNM("h2:4321", 4096, getResourceTrackerService());
        nodes.put(nm4.getNodeId(), nm4);
        nm1.registerNode();
        nm2.registerNode();
        nm3.registerNode();
        nm4.registerNode();
        OpportunisticContainerAllocatorAMService amservice = ((OpportunisticContainerAllocatorAMService) (getApplicationMasterService()));
        RMApp app1 = rm.submitApp((1 * (TestOpportunisticContainerAllocatorAMService.GB)), "app", "user", null, "default");
        ApplicationAttemptId attemptId = app1.getCurrentAppAttempt().getAppAttemptId();
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2);
        ResourceScheduler scheduler = getResourceScheduler();
        RMNode rmNode1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        RMNode rmNode2 = getRMContext().getRMNodes().get(nm2.getNodeId());
        RMNode rmNode3 = getRMContext().getRMNodes().get(nm3.getNodeId());
        RMNode rmNode4 = getRMContext().getRMNodes().get(nm4.getNodeId());
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(true);
        nm3.nodeHeartbeat(true);
        nm4.nodeHeartbeat(true);
        // Send add and update node events to AM Service.
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode1));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode2));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode3));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode4));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode3));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode4));
        // All nodes 1 - 4 will be applicable for scheduling.
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(true);
        nm3.nodeHeartbeat(true);
        nm4.nodeHeartbeat(true);
        Thread.sleep(1000);
        QueueMetrics metrics = getRootQueue().getMetrics();
        // Verify Metrics
        verifyMetrics(metrics, 15360, 15, 1024, 1, 1);
        AllocateResponse allocateResponse = am1.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((1 * (TestOpportunisticContainerAllocatorAMService.GB))), 2, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true))), null);
        List<Container> allocatedContainers = allocateResponse.getAllocatedContainers();
        Assert.assertEquals(2, allocatedContainers.size());
        Container container = allocatedContainers.get(0);
        MockNM allocNode = nodes.get(container.getNodeId());
        MockNM sameHostDiffNode = null;
        for (NodeId n : nodes.keySet()) {
            if ((n.getHost().equals(allocNode.getNodeId().getHost())) && ((n.getPort()) != (allocNode.getNodeId().getPort()))) {
                sameHostDiffNode = nodes.get(n);
            }
        }
        // Verify Metrics After OPP allocation (Nothing should change)
        verifyMetrics(metrics, 15360, 15, 1024, 1, 1);
        am1.sendContainerUpdateRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, container.getId(), PROMOTE_EXECUTION_TYPE, null, GUARANTEED)));
        // Node on same host should not result in allocation
        sameHostDiffNode.nodeHeartbeat(true);
        rm.drainEvents();
        allocateResponse = am1.allocate(new ArrayList<>(), new ArrayList<>());
        Assert.assertEquals(0, allocateResponse.getUpdatedContainers().size());
        // Wait for scheduler to process all events
        dispatcher.waitForEventThreadToWait();
        rm.drainEvents();
        // Verify Metrics After OPP allocation (Nothing should change again)
        verifyMetrics(metrics, 15360, 15, 1024, 1, 1);
        // Send Promotion req again... this should result in update error
        allocateResponse = am1.sendContainerUpdateRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, container.getId(), PROMOTE_EXECUTION_TYPE, null, GUARANTEED)));
        Assert.assertEquals(0, allocateResponse.getUpdatedContainers().size());
        Assert.assertEquals(1, allocateResponse.getUpdateErrors().size());
        Assert.assertEquals("UPDATE_OUTSTANDING_ERROR", allocateResponse.getUpdateErrors().get(0).getReason());
        Assert.assertEquals(container.getId(), allocateResponse.getUpdateErrors().get(0).getUpdateContainerRequest().getContainerId());
        // Send Promotion req again with incorrect version...
        // this should also result in update error
        allocateResponse = am1.sendContainerUpdateRequest(Arrays.asList(UpdateContainerRequest.newInstance(1, container.getId(), PROMOTE_EXECUTION_TYPE, null, GUARANTEED)));
        Assert.assertEquals(0, allocateResponse.getUpdatedContainers().size());
        Assert.assertEquals(1, allocateResponse.getUpdateErrors().size());
        Assert.assertEquals("INCORRECT_CONTAINER_VERSION_ERROR", allocateResponse.getUpdateErrors().get(0).getReason());
        Assert.assertEquals(0, allocateResponse.getUpdateErrors().get(0).getCurrentContainerVersion());
        Assert.assertEquals(container.getId(), allocateResponse.getUpdateErrors().get(0).getUpdateContainerRequest().getContainerId());
        // Ensure after correct node heartbeats, we should get the allocation
        allocNode.nodeHeartbeat(true);
        rm.drainEvents();
        allocateResponse = am1.allocate(new ArrayList<>(), new ArrayList<>());
        Assert.assertEquals(1, allocateResponse.getUpdatedContainers().size());
        Container uc = allocateResponse.getUpdatedContainers().get(0).getContainer();
        Assert.assertEquals(GUARANTEED, uc.getExecutionType());
        Assert.assertEquals(uc.getId(), container.getId());
        Assert.assertEquals(uc.getVersion(), ((container.getVersion()) + 1));
        // Verify Metrics After OPP allocation :
        // Allocated cores+mem should have increased, available should decrease
        verifyMetrics(metrics, 14336, 14, 2048, 2, 2);
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(true);
        nm3.nodeHeartbeat(true);
        nm4.nodeHeartbeat(true);
        rm.drainEvents();
        // Verify that the container is still in ACQUIRED state wrt the RM.
        RMContainer rmContainer = ((CapacityScheduler) (scheduler)).getApplicationAttempt(uc.getId().getApplicationAttemptId()).getRMContainer(uc.getId());
        Assert.assertEquals(ACQUIRED, rmContainer.getState());
        // Now demote the container back..
        allocateResponse = am1.sendContainerUpdateRequest(Arrays.asList(UpdateContainerRequest.newInstance(uc.getVersion(), uc.getId(), DEMOTE_EXECUTION_TYPE, null, OPPORTUNISTIC)));
        // This should happen in the same heartbeat..
        Assert.assertEquals(1, allocateResponse.getUpdatedContainers().size());
        uc = allocateResponse.getUpdatedContainers().get(0).getContainer();
        Assert.assertEquals(OPPORTUNISTIC, uc.getExecutionType());
        Assert.assertEquals(uc.getId(), container.getId());
        Assert.assertEquals(uc.getVersion(), ((container.getVersion()) + 2));
        // Wait for scheduler to finish processing events
        dispatcher.waitForEventThreadToWait();
        rm.drainEvents();
        // Verify Metrics After OPP allocation :
        // Everything should have reverted to what it was
        verifyMetrics(metrics, 15360, 15, 1024, 1, 1);
    }

    @Test(timeout = 60000)
    public void testContainerPromoteAfterContainerStart() throws Exception {
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        nm1.registerNode();
        nm2.registerNode();
        OpportunisticContainerAllocatorAMService amservice = ((OpportunisticContainerAllocatorAMService) (getApplicationMasterService()));
        RMApp app1 = rm.submitApp((1 * (TestOpportunisticContainerAllocatorAMService.GB)), "app", "user", null, "default");
        ApplicationAttemptId attemptId = app1.getCurrentAppAttempt().getAppAttemptId();
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2);
        ResourceScheduler scheduler = getResourceScheduler();
        RMNode rmNode1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        RMNode rmNode2 = getRMContext().getRMNodes().get(nm2.getNodeId());
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(true);
        ((RMNodeImpl) (rmNode1)).setOpportunisticContainersStatus(getOppurtunisticStatus((-1), 100));
        ((RMNodeImpl) (rmNode2)).setOpportunisticContainersStatus(getOppurtunisticStatus((-1), 100));
        OpportunisticContainerContext ctxt = ((CapacityScheduler) (scheduler)).getApplicationAttempt(attemptId).getOpportunisticContainerContext();
        // Send add and update node events to AM Service.
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode1));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode2));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
        // All nodes 1 to 2 will be applicable for scheduling.
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(true);
        Thread.sleep(1000);
        QueueMetrics metrics = getRootQueue().getMetrics();
        // Verify Metrics
        verifyMetrics(metrics, 7168, 7, 1024, 1, 1);
        AllocateResponse allocateResponse = am1.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((1 * (TestOpportunisticContainerAllocatorAMService.GB))), 2, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true))), null);
        List<Container> allocatedContainers = allocateResponse.getAllocatedContainers();
        Assert.assertEquals(2, allocatedContainers.size());
        Container container = allocatedContainers.get(0);
        MockNM allocNode = nodes.get(container.getNodeId());
        // Start Container in NM
        allocNode.nodeHeartbeat(Arrays.asList(ContainerStatus.newInstance(container.getId(), OPPORTUNISTIC, RUNNING, "", 0)), true);
        rm.drainEvents();
        // Verify that container is actually running wrt the RM..
        RMContainer rmContainer = ((CapacityScheduler) (scheduler)).getApplicationAttempt(container.getId().getApplicationAttemptId()).getRMContainer(container.getId());
        Assert.assertEquals(RMContainerState.RUNNING, rmContainer.getState());
        // Verify Metrics After OPP allocation (Nothing should change)
        verifyMetrics(metrics, 7168, 7, 1024, 1, 1);
        am1.sendContainerUpdateRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, container.getId(), PROMOTE_EXECUTION_TYPE, null, GUARANTEED)));
        // Verify Metrics After OPP allocation (Nothing should change again)
        verifyMetrics(metrics, 7168, 7, 1024, 1, 1);
        // Send Promotion req again... this should result in update error
        allocateResponse = am1.sendContainerUpdateRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, container.getId(), PROMOTE_EXECUTION_TYPE, null, GUARANTEED)));
        Assert.assertEquals(0, allocateResponse.getUpdatedContainers().size());
        Assert.assertEquals(1, allocateResponse.getUpdateErrors().size());
        Assert.assertEquals("UPDATE_OUTSTANDING_ERROR", allocateResponse.getUpdateErrors().get(0).getReason());
        Assert.assertEquals(container.getId(), allocateResponse.getUpdateErrors().get(0).getUpdateContainerRequest().getContainerId());
        // Start Container in NM
        allocNode.nodeHeartbeat(Arrays.asList(ContainerStatus.newInstance(container.getId(), OPPORTUNISTIC, RUNNING, "", 0)), true);
        rm.drainEvents();
        allocateResponse = am1.allocate(new ArrayList<>(), new ArrayList<>());
        Assert.assertEquals(1, allocateResponse.getUpdatedContainers().size());
        Container uc = allocateResponse.getUpdatedContainers().get(0).getContainer();
        Assert.assertEquals(GUARANTEED, uc.getExecutionType());
        Assert.assertEquals(uc.getId(), container.getId());
        Assert.assertEquals(uc.getVersion(), ((container.getVersion()) + 1));
        // Verify that the Container is still in RUNNING state wrt RM..
        rmContainer = ((CapacityScheduler) (scheduler)).getApplicationAttempt(uc.getId().getApplicationAttemptId()).getRMContainer(uc.getId());
        Assert.assertEquals(RMContainerState.RUNNING, rmContainer.getState());
        // Verify Metrics After OPP allocation :
        // Allocated cores+mem should have increased, available should decrease
        verifyMetrics(metrics, 6144, 6, 2048, 2, 2);
    }

    @Test(timeout = 600000)
    public void testContainerPromoteAfterContainerComplete() throws Exception {
        HashMap<NodeId, MockNM> nodes = new HashMap<>();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nodes.put(nm1.getNodeId(), nm1);
        MockNM nm2 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nodes.put(nm2.getNodeId(), nm2);
        nm1.registerNode();
        nm2.registerNode();
        OpportunisticContainerAllocatorAMService amservice = ((OpportunisticContainerAllocatorAMService) (getApplicationMasterService()));
        RMApp app1 = rm.submitApp((1 * (TestOpportunisticContainerAllocatorAMService.GB)), "app", "user", null, "default");
        ApplicationAttemptId attemptId = app1.getCurrentAppAttempt().getAppAttemptId();
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2);
        ResourceScheduler scheduler = getResourceScheduler();
        RMNode rmNode1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        RMNode rmNode2 = getRMContext().getRMNodes().get(nm2.getNodeId());
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(true);
        ((RMNodeImpl) (rmNode1)).setOpportunisticContainersStatus(getOppurtunisticStatus((-1), 100));
        ((RMNodeImpl) (rmNode2)).setOpportunisticContainersStatus(getOppurtunisticStatus((-1), 100));
        OpportunisticContainerContext ctxt = ((CapacityScheduler) (scheduler)).getApplicationAttempt(attemptId).getOpportunisticContainerContext();
        // Send add and update node events to AM Service.
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode1));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode2));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
        // All nodes 1 to 2 will be applicable for scheduling.
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(true);
        Thread.sleep(1000);
        QueueMetrics metrics = getRootQueue().getMetrics();
        // Verify Metrics
        verifyMetrics(metrics, 7168, 7, 1024, 1, 1);
        AllocateResponse allocateResponse = am1.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((1 * (TestOpportunisticContainerAllocatorAMService.GB))), 2, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true))), null);
        List<Container> allocatedContainers = allocateResponse.getAllocatedContainers();
        Assert.assertEquals(2, allocatedContainers.size());
        Container container = allocatedContainers.get(0);
        MockNM allocNode = nodes.get(container.getNodeId());
        // Start Container in NM
        allocNode.nodeHeartbeat(Arrays.asList(ContainerStatus.newInstance(container.getId(), OPPORTUNISTIC, RUNNING, "", 0)), true);
        rm.drainEvents();
        // Verify that container is actually running wrt the RM..
        RMContainer rmContainer = ((CapacityScheduler) (scheduler)).getApplicationAttempt(container.getId().getApplicationAttemptId()).getRMContainer(container.getId());
        Assert.assertEquals(RMContainerState.RUNNING, rmContainer.getState());
        // Container Completed in the NM
        allocNode.nodeHeartbeat(Arrays.asList(ContainerStatus.newInstance(container.getId(), OPPORTUNISTIC, COMPLETE, "", 0)), true);
        rm.drainEvents();
        // Verify that container has been removed..
        rmContainer = ((CapacityScheduler) (scheduler)).getApplicationAttempt(container.getId().getApplicationAttemptId()).getRMContainer(container.getId());
        Assert.assertNull(rmContainer);
        // Verify Metrics After OPP allocation (Nothing should change)
        verifyMetrics(metrics, 7168, 7, 1024, 1, 1);
        // Send Promotion req... this should result in update error
        // Since the container doesn't exist anymore..
        allocateResponse = am1.sendContainerUpdateRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, container.getId(), PROMOTE_EXECUTION_TYPE, null, GUARANTEED)));
        Assert.assertEquals(1, allocateResponse.getCompletedContainersStatuses().size());
        Assert.assertEquals(container.getId(), allocateResponse.getCompletedContainersStatuses().get(0).getContainerId());
        Assert.assertEquals(0, allocateResponse.getUpdatedContainers().size());
        Assert.assertEquals(1, allocateResponse.getUpdateErrors().size());
        Assert.assertEquals("INVALID_CONTAINER_ID", allocateResponse.getUpdateErrors().get(0).getReason());
        Assert.assertEquals(container.getId(), allocateResponse.getUpdateErrors().get(0).getUpdateContainerRequest().getContainerId());
        // Verify Metrics After OPP allocation (Nothing should change again)
        verifyMetrics(metrics, 7168, 7, 1024, 1, 1);
    }

    @Test(timeout = 600000)
    public void testContainerAutoUpdateContainer() throws Exception {
        stop();
        createAndStartRMWithAutoUpdateContainer();
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        nm1.registerNode();
        OpportunisticContainerAllocatorAMService amservice = ((OpportunisticContainerAllocatorAMService) (getApplicationMasterService()));
        RMApp app1 = rm.submitApp((1 * (TestOpportunisticContainerAllocatorAMService.GB)), "app", "user", null, "default");
        ApplicationAttemptId attemptId = app1.getCurrentAppAttempt().getAppAttemptId();
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm1);
        ResourceScheduler scheduler = getResourceScheduler();
        RMNode rmNode1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        nm1.nodeHeartbeat(true);
        ((RMNodeImpl) (rmNode1)).setOpportunisticContainersStatus(getOppurtunisticStatus((-1), 100));
        OpportunisticContainerContext ctxt = ((CapacityScheduler) (scheduler)).getApplicationAttempt(attemptId).getOpportunisticContainerContext();
        // Send add and update node events to AM Service.
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode1));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
        nm1.nodeHeartbeat(true);
        Thread.sleep(1000);
        AllocateResponse allocateResponse = am1.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((1 * (TestOpportunisticContainerAllocatorAMService.GB))), 2, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true))), null);
        List<Container> allocatedContainers = allocateResponse.getAllocatedContainers();
        allocatedContainers.addAll(am1.allocate(null, null).getAllocatedContainers());
        Assert.assertEquals(2, allocatedContainers.size());
        Container container = allocatedContainers.get(0);
        // Start Container in NM
        nm1.nodeHeartbeat(Arrays.asList(ContainerStatus.newInstance(container.getId(), OPPORTUNISTIC, RUNNING, "", 0)), true);
        rm.drainEvents();
        // Verify that container is actually running wrt the RM..
        RMContainer rmContainer = ((CapacityScheduler) (scheduler)).getApplicationAttempt(container.getId().getApplicationAttemptId()).getRMContainer(container.getId());
        Assert.assertEquals(RMContainerState.RUNNING, rmContainer.getState());
        // Send Promotion req... this should result in update error
        // Since the container doesn't exist anymore..
        allocateResponse = am1.sendContainerUpdateRequest(Arrays.asList(UpdateContainerRequest.newInstance(0, container.getId(), PROMOTE_EXECUTION_TYPE, null, GUARANTEED)));
        nm1.nodeHeartbeat(Arrays.asList(ContainerStatus.newInstance(container.getId(), OPPORTUNISTIC, RUNNING, "", 0)), true);
        rm.drainEvents();
        // Get the update response on next allocate
        allocateResponse = am1.allocate(new ArrayList<>(), new ArrayList<>());
        // Check the update response from YARNRM
        Assert.assertEquals(1, allocateResponse.getUpdatedContainers().size());
        UpdatedContainer uc = allocateResponse.getUpdatedContainers().get(0);
        Assert.assertEquals(container.getId(), uc.getContainer().getId());
        Assert.assertEquals(GUARANTEED, uc.getContainer().getExecutionType());
        // Check that the container is updated in NM through NM heartbeat response
        NodeHeartbeatResponse response = nm1.nodeHeartbeat(true);
        Assert.assertEquals(1, response.getContainersToUpdate().size());
        Container containersFromNM = response.getContainersToUpdate().get(0);
        Assert.assertEquals(container.getId(), containersFromNM.getId());
        Assert.assertEquals(GUARANTEED, containersFromNM.getExecutionType());
        // Increase resources
        allocateResponse = am1.sendContainerUpdateRequest(Arrays.asList(UpdateContainerRequest.newInstance(1, container.getId(), INCREASE_RESOURCE, Resources.createResource((2 * (TestOpportunisticContainerAllocatorAMService.GB)), 1), null)));
        response = nm1.nodeHeartbeat(Arrays.asList(ContainerStatus.newInstance(container.getId(), GUARANTEED, RUNNING, "", 0)), true);
        rm.drainEvents();
        if ((allocateResponse.getUpdatedContainers().size()) == 0) {
            allocateResponse = am1.allocate(new ArrayList<>(), new ArrayList<>());
        }
        Assert.assertEquals(1, allocateResponse.getUpdatedContainers().size());
        uc = allocateResponse.getUpdatedContainers().get(0);
        Assert.assertEquals(container.getId(), uc.getContainer().getId());
        Assert.assertEquals(Resource.newInstance((2 * (TestOpportunisticContainerAllocatorAMService.GB)), 1), uc.getContainer().getResource());
        rm.drainEvents();
        // Check that the container resources are increased in
        // NM through NM heartbeat response
        if ((response.getContainersToUpdate().size()) == 0) {
            response = nm1.nodeHeartbeat(true);
        }
        Assert.assertEquals(1, response.getContainersToUpdate().size());
        Assert.assertEquals(Resource.newInstance((2 * (TestOpportunisticContainerAllocatorAMService.GB)), 1), response.getContainersToUpdate().get(0).getResource());
        // Decrease resources
        allocateResponse = am1.sendContainerUpdateRequest(Arrays.asList(UpdateContainerRequest.newInstance(2, container.getId(), DECREASE_RESOURCE, Resources.createResource((1 * (TestOpportunisticContainerAllocatorAMService.GB)), 1), null)));
        Assert.assertEquals(1, allocateResponse.getUpdatedContainers().size());
        rm.drainEvents();
        // Check that the container resources are decreased
        // in NM through NM heartbeat response
        response = nm1.nodeHeartbeat(true);
        Assert.assertEquals(1, response.getContainersToUpdate().size());
        Assert.assertEquals(Resource.newInstance((1 * (TestOpportunisticContainerAllocatorAMService.GB)), 1), response.getContainersToUpdate().get(0).getResource());
        nm1.nodeHeartbeat(true);
        // DEMOTE the container
        allocateResponse = am1.sendContainerUpdateRequest(Arrays.asList(UpdateContainerRequest.newInstance(3, container.getId(), DEMOTE_EXECUTION_TYPE, null, OPPORTUNISTIC)));
        response = nm1.nodeHeartbeat(Arrays.asList(ContainerStatus.newInstance(container.getId(), GUARANTEED, RUNNING, "", 0)), true);
        rm.drainEvents();
        if ((allocateResponse.getUpdatedContainers().size()) == 0) {
            // Get the update response on next allocate
            allocateResponse = am1.allocate(new ArrayList<>(), new ArrayList<>());
        }
        // Check the update response from YARNRM
        Assert.assertEquals(1, allocateResponse.getUpdatedContainers().size());
        uc = allocateResponse.getUpdatedContainers().get(0);
        Assert.assertEquals(OPPORTUNISTIC, uc.getContainer().getExecutionType());
        // Check that the container is updated in NM through NM heartbeat response
        if ((response.getContainersToUpdate().size()) == 0) {
            response = nm1.nodeHeartbeat(true);
        }
        Assert.assertEquals(1, response.getContainersToUpdate().size());
        Assert.assertEquals(OPPORTUNISTIC, response.getContainersToUpdate().get(0).getExecutionType());
    }

    @Test(timeout = 60000)
    public void testAMCrashDuringAllocate() throws Exception {
        MockNM nm = new MockNM("h:1234", 4096, getResourceTrackerService());
        nm.registerNode();
        RMApp app = rm.submitApp((1 * (TestOpportunisticContainerAllocatorAMService.GB)), "app", "user", null, "default");
        ApplicationAttemptId attemptId0 = app.getCurrentAppAttempt().getAppAttemptId();
        MockAM am = MockRM.launchAndRegisterAM(app, rm, nm);
        // simulate AM crash by replacing the current attempt
        // Do not use rm.failApplicationAttempt, the bug will skip due to
        // ApplicationDoesNotExistInCacheException
        CapacityScheduler scheduler = ((CapacityScheduler) (getRMContext().getScheduler()));
        SchedulerApplication<FiCaSchedulerApp> schApp = ((SchedulerApplication<FiCaSchedulerApp>) (scheduler.getSchedulerApplications().get(attemptId0.getApplicationId())));
        final ApplicationAttemptId appAttemptId1 = TestUtils.getMockApplicationAttemptId(1, 1);
        schApp.setCurrentAppAttempt(new FiCaSchedulerApp(appAttemptId1, null, scheduler.getQueue("default"), null, getRMContext()));
        // start to allocate
        am.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((1 * (TestOpportunisticContainerAllocatorAMService.GB))), 2)), null);
    }

    @Test(timeout = 60000)
    public void testNodeRemovalDuringAllocate() throws Exception {
        MockNM nm1 = new MockNM("h1:1234", 4096, getResourceTrackerService());
        MockNM nm2 = new MockNM("h2:1234", 4096, getResourceTrackerService());
        nm1.registerNode();
        nm2.registerNode();
        OpportunisticContainerAllocatorAMService amservice = ((OpportunisticContainerAllocatorAMService) (getApplicationMasterService()));
        RMApp app1 = rm.submitApp((1 * (TestOpportunisticContainerAllocatorAMService.GB)), "app", "user", null, "default");
        ApplicationAttemptId attemptId = app1.getCurrentAppAttempt().getAppAttemptId();
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm, nm2);
        ResourceScheduler scheduler = getResourceScheduler();
        RMNode rmNode1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        RMNode rmNode2 = getRMContext().getRMNodes().get(nm2.getNodeId());
        nm1.nodeHeartbeat(true);
        nm2.nodeHeartbeat(true);
        ((RMNodeImpl) (rmNode1)).setOpportunisticContainersStatus(getOppurtunisticStatus((-1), 100));
        ((RMNodeImpl) (rmNode2)).setOpportunisticContainersStatus(getOppurtunisticStatus((-1), 100));
        OpportunisticContainerContext ctxt = ((CapacityScheduler) (scheduler)).getApplicationAttempt(attemptId).getOpportunisticContainerContext();
        // Send add and update node events to AM Service.
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode1));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode2));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
        // Both node 1 and node 2 will be applicable for scheduling.
        for (int i = 0; i < 10; i++) {
            am1.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((1 * (TestOpportunisticContainerAllocatorAMService.GB))), 2)), null);
            if ((ctxt.getNodeMap().size()) == 2) {
                break;
            }
            Thread.sleep(50);
        }
        Assert.assertEquals(2, ctxt.getNodeMap().size());
        // Remove node from scheduler but not from AM Service.
        scheduler.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent(rmNode1));
        // After removal of node 1, only 1 node will be applicable for scheduling.
        for (int i = 0; i < 10; i++) {
            try {
                am1.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((1 * (TestOpportunisticContainerAllocatorAMService.GB))), 2)), null);
            } catch (Exception e) {
                Assert.fail("Allocate request should be handled on node removal");
            }
            if ((ctxt.getNodeMap().size()) == 1) {
                break;
            }
            Thread.sleep(50);
        }
        Assert.assertEquals(1, ctxt.getNodeMap().size());
    }

    @Test(timeout = 60000)
    public void testAppAttemptRemovalAfterNodeRemoval() throws Exception {
        MockNM nm = new MockNM("h:1234", 4096, getResourceTrackerService());
        nm.registerNode();
        OpportunisticContainerAllocatorAMService amservice = ((OpportunisticContainerAllocatorAMService) (getApplicationMasterService()));
        RMApp app = rm.submitApp((1 * (TestOpportunisticContainerAllocatorAMService.GB)), "app", "user", null, "default");
        ApplicationAttemptId attemptId = app.getCurrentAppAttempt().getAppAttemptId();
        MockAM am = MockRM.launchAndRegisterAM(app, rm, nm);
        ResourceScheduler scheduler = getResourceScheduler();
        SchedulerApplicationAttempt schedulerAttempt = ((CapacityScheduler) (scheduler)).getApplicationAttempt(attemptId);
        RMNode rmNode1 = getRMContext().getRMNodes().get(nm.getNodeId());
        nm.nodeHeartbeat(true);
        ((RMNodeImpl) (rmNode1)).setOpportunisticContainersStatus(getOppurtunisticStatus((-1), 100));
        // Send add and update node events to AM Service.
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(rmNode1));
        amservice.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
        try {
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return (scheduler.getNumClusterNodes()) == 1;
                }
            }, 10, (200 * 100));
        } catch (TimeoutException e) {
            Assert.fail("timed out while waiting for NM to add.");
        }
        AllocateResponse allocateResponse = am.allocate(Arrays.asList(ResourceRequest.newInstance(Priority.newInstance(1), "*", Resources.createResource((1 * (TestOpportunisticContainerAllocatorAMService.GB))), 2, true, null, ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true))), null);
        List<Container> allocatedContainers = allocateResponse.getAllocatedContainers();
        Container container = allocatedContainers.get(0);
        scheduler.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent(rmNode1));
        try {
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return (scheduler.getNumClusterNodes()) == 0;
                }
            }, 10, (200 * 100));
        } catch (TimeoutException e) {
            Assert.fail("timed out while waiting for NM to remove.");
        }
        // test YARN-9165
        RMContainer rmContainer = null;
        rmContainer = SchedulerUtils.createOpportunisticRmContainer(getRMContext(), container, true);
        if (rmContainer == null) {
            rmContainer = new org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl(container, SchedulerRequestKey.extractFrom(container), schedulerAttempt.getApplicationAttemptId(), container.getNodeId(), schedulerAttempt.getUser(), getRMContext(), true);
        }
        assert rmContainer != null;
        // test YARN-9164
        schedulerAttempt.addRMContainer(container.getId(), rmContainer);
        scheduler.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptRemovedSchedulerEvent(attemptId, RMAppAttemptState.FAILED, false));
    }

    // Test if the OpportunisticContainerAllocatorAMService can handle both
    // DSProtocol as well as AMProtocol clients
    @Test
    public void testRPCWrapping() throws Exception {
        Configuration conf = new Configuration();
        conf.set(IPC_RPC_IMPL, HadoopYarnProtoRPC.class.getName());
        YarnRPC rpc = YarnRPC.create(conf);
        String bindAddr = "localhost:0";
        InetSocketAddress addr = NetUtils.createSocketAddr(bindAddr);
        conf.setSocketAddr(RM_SCHEDULER_ADDRESS, addr);
        final RecordFactory factory = RecordFactoryProvider.getRecordFactory(null);
        final RMContext rmContext = new RMContextImpl() {
            @Override
            public AMLivelinessMonitor getAMLivelinessMonitor() {
                return null;
            }

            @Override
            public Configuration getYarnConfiguration() {
                return new YarnConfiguration();
            }

            @Override
            public RMContainerTokenSecretManager getContainerTokenSecretManager() {
                return new RMContainerTokenSecretManager(conf);
            }

            @Override
            public ResourceScheduler getScheduler() {
                return new FifoScheduler();
            }
        };
        Container c = factory.newRecordInstance(Container.class);
        c.setExecutionType(OPPORTUNISTIC);
        c.setId(ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(12345, 1), 2), 3));
        AllocateRequest allReq = ((AllocateRequestPBImpl) (factory.newRecordInstance(AllocateRequest.class)));
        allReq.setAskList(Arrays.asList(ResourceRequest.newInstance(UNDEFINED, "a", Resource.newInstance(1, 2), 1, true, "exp", ExecutionTypeRequest.newInstance(OPPORTUNISTIC, true))));
        OpportunisticContainerAllocatorAMService service = createService(factory, rmContext, c);
        conf.setBoolean(DIST_SCHEDULING_ENABLED, true);
        Server server = service.getServer(rpc, conf, addr, null);
        server.start();
        // Verify that the OpportunisticContainerAllocatorAMSercvice can handle
        // vanilla ApplicationMasterProtocol clients
        RPC.setProtocolEngine(conf, ApplicationMasterProtocolPB.class, ProtobufRpcEngine.class);
        ApplicationMasterProtocolPB ampProxy = RPC.getProxy(ApplicationMasterProtocolPB.class, 1, NetUtils.getConnectAddress(server), conf);
        RegisterApplicationMasterResponse regResp = new org.apache.hadoop.yarn.api.protocolrecords.impl.pb.RegisterApplicationMasterResponsePBImpl(ampProxy.registerApplicationMaster(null, getProto()));
        Assert.assertEquals("dummyQueue", regResp.getQueue());
        FinishApplicationMasterResponse finishResp = new org.apache.hadoop.yarn.api.protocolrecords.impl.pb.FinishApplicationMasterResponsePBImpl(ampProxy.finishApplicationMaster(null, getProto()));
        Assert.assertEquals(false, finishResp.getIsUnregistered());
        AllocateResponse allocResp = new org.apache.hadoop.yarn.api.protocolrecords.impl.pb.AllocateResponsePBImpl(ampProxy.allocate(null, getProto()));
        List<Container> allocatedContainers = allocResp.getAllocatedContainers();
        Assert.assertEquals(1, allocatedContainers.size());
        Assert.assertEquals(OPPORTUNISTIC, allocatedContainers.get(0).getExecutionType());
        Assert.assertEquals(12345, allocResp.getNumClusterNodes());
        // Verify that the DistrubutedSchedulingService can handle the
        // DistributedSchedulingAMProtocol clients as well
        RPC.setProtocolEngine(conf, DistributedSchedulingAMProtocolPB.class, ProtobufRpcEngine.class);
        DistributedSchedulingAMProtocolPB dsProxy = RPC.getProxy(DistributedSchedulingAMProtocolPB.class, 1, NetUtils.getConnectAddress(server), conf);
        RegisterDistributedSchedulingAMResponse dsRegResp = new org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RegisterDistributedSchedulingAMResponsePBImpl(dsProxy.registerApplicationMasterForDistributedScheduling(null, getProto()));
        Assert.assertEquals(54321L, dsRegResp.getContainerIdStart());
        Assert.assertEquals(4, dsRegResp.getMaxContainerResource().getVirtualCores());
        Assert.assertEquals(1024, dsRegResp.getMinContainerResource().getMemorySize());
        Assert.assertEquals(2, dsRegResp.getIncrContainerResource().getVirtualCores());
        DistributedSchedulingAllocateRequestPBImpl distAllReq = ((DistributedSchedulingAllocateRequestPBImpl) (factory.newRecordInstance(DistributedSchedulingAllocateRequest.class)));
        distAllReq.setAllocateRequest(allReq);
        distAllReq.setAllocatedContainers(Arrays.asList(c));
        DistributedSchedulingAllocateResponse dsAllocResp = new org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.DistributedSchedulingAllocateResponsePBImpl(dsProxy.allocateForDistributedScheduling(null, distAllReq.getProto()));
        Assert.assertEquals("h1", dsAllocResp.getNodesForScheduling().get(0).getNodeId().getHost());
        Assert.assertEquals("l1", dsAllocResp.getNodesForScheduling().get(1).getNodePartition());
        FinishApplicationMasterResponse dsfinishResp = new org.apache.hadoop.yarn.api.protocolrecords.impl.pb.FinishApplicationMasterResponsePBImpl(dsProxy.finishApplicationMaster(null, getProto()));
        Assert.assertEquals(false, dsfinishResp.getIsUnregistered());
    }
}

