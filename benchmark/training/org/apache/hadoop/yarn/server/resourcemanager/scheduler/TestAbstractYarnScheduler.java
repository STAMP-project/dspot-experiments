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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler;


import ContainerState.COMPLETE;
import ContainerState.RUNNING;
import ExecutionType.GUARANTEED;
import RMContainerState.ALLOCATED;
import RMContainerState.KILLED;
import YarnConfiguration.RECOVERY_ENABLED;
import YarnConfiguration.RM_PLACEMENT_CONSTRAINTS_HANDLER;
import YarnConfiguration.RM_SCHEDULER_MAXIMUM_ALLOCATION_MB;
import YarnConfiguration.RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES;
import YarnConfiguration.RM_STORE;
import YarnConfiguration.RM_WORK_PRESERVING_RECOVERY_ENABLED;
import YarnConfiguration.RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNodes;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.ParameterizedSchedulerTestBase;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceTrackerService;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.MemoryRMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.MockRMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import org.apache.hadoop.yarn.server.scheduler.SchedulerRequestKey;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.apache.hadoop.yarn.server.resourcemanager.ParameterizedSchedulerTestBase.SchedulerType.CAPACITY;
import static org.apache.hadoop.yarn.server.resourcemanager.ParameterizedSchedulerTestBase.SchedulerType.FAIR;


@SuppressWarnings("unchecked")
public class TestAbstractYarnScheduler extends ParameterizedSchedulerTestBase {
    public TestAbstractYarnScheduler(ParameterizedSchedulerTestBase.SchedulerType type) throws IOException {
        super(type);
    }

    @Test
    public void testMaximimumAllocationMemory() throws Exception {
        final int node1MaxMemory = 15 * 1024;
        final int node2MaxMemory = 5 * 1024;
        final int node3MaxMemory = 6 * 1024;
        final int configuredMaxMemory = 10 * 1024;
        YarnConfiguration conf = getConf();
        conf.setInt(RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, configuredMaxMemory);
        conf.setLong(RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS, (1000 * 1000));
        MockRM rm = new MockRM(conf);
        try {
            start();
            testMaximumAllocationMemoryHelper(getResourceScheduler(), node1MaxMemory, node2MaxMemory, node3MaxMemory, configuredMaxMemory, configuredMaxMemory, configuredMaxMemory, configuredMaxMemory, configuredMaxMemory, configuredMaxMemory);
        } finally {
            stop();
        }
        conf.setLong(RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS, 0);
        rm = new MockRM(conf);
        try {
            start();
            testMaximumAllocationMemoryHelper(getResourceScheduler(), node1MaxMemory, node2MaxMemory, node3MaxMemory, configuredMaxMemory, configuredMaxMemory, configuredMaxMemory, node2MaxMemory, node3MaxMemory, node2MaxMemory);
        } finally {
            stop();
        }
    }

    @Test
    public void testMaximimumAllocationVCores() throws Exception {
        final int node1MaxVCores = 15;
        final int node2MaxVCores = 5;
        final int node3MaxVCores = 6;
        final int configuredMaxVCores = 10;
        YarnConfiguration conf = getConf();
        conf.setInt(RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, configuredMaxVCores);
        conf.setLong(RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS, (1000 * 1000));
        MockRM rm = new MockRM(conf);
        try {
            start();
            testMaximumAllocationVCoresHelper(getResourceScheduler(), node1MaxVCores, node2MaxVCores, node3MaxVCores, configuredMaxVCores, configuredMaxVCores, configuredMaxVCores, configuredMaxVCores, configuredMaxVCores, configuredMaxVCores);
        } finally {
            stop();
        }
        conf.setLong(RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS, 0);
        rm = new MockRM(conf);
        try {
            start();
            testMaximumAllocationVCoresHelper(getResourceScheduler(), node1MaxVCores, node2MaxVCores, node3MaxVCores, configuredMaxVCores, configuredMaxVCores, configuredMaxVCores, node2MaxVCores, node3MaxVCores, node2MaxVCores);
        } finally {
            stop();
        }
    }

    @Test
    public void testUpdateMaxAllocationUsesTotal() throws IOException {
        final int configuredMaxVCores = 20;
        final int configuredMaxMemory = 10 * 1024;
        Resource configuredMaximumResource = Resource.newInstance(configuredMaxMemory, configuredMaxVCores);
        YarnConfiguration conf = getConf();
        conf.setInt(RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, configuredMaxVCores);
        conf.setInt(RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, configuredMaxMemory);
        conf.setLong(RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS, 0);
        MockRM rm = new MockRM(conf);
        try {
            start();
            AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
            Resource emptyResource = Resource.newInstance(0, 0);
            Resource fullResource1 = Resource.newInstance(1024, 5);
            Resource fullResource2 = Resource.newInstance(2048, 10);
            SchedulerNode mockNode1 = Mockito.mock(SchedulerNode.class);
            Mockito.when(mockNode1.getNodeID()).thenReturn(NodeId.newInstance("foo", 8080));
            Mockito.when(mockNode1.getUnallocatedResource()).thenReturn(emptyResource);
            Mockito.when(mockNode1.getTotalResource()).thenReturn(fullResource1);
            SchedulerNode mockNode2 = Mockito.mock(SchedulerNode.class);
            Mockito.when(mockNode1.getNodeID()).thenReturn(NodeId.newInstance("bar", 8081));
            Mockito.when(mockNode2.getUnallocatedResource()).thenReturn(emptyResource);
            Mockito.when(mockNode2.getTotalResource()).thenReturn(fullResource2);
            verifyMaximumResourceCapability(configuredMaximumResource, scheduler);
            scheduler.nodeTracker.addNode(mockNode1);
            verifyMaximumResourceCapability(fullResource1, scheduler);
            scheduler.nodeTracker.addNode(mockNode2);
            verifyMaximumResourceCapability(fullResource2, scheduler);
            scheduler.nodeTracker.removeNode(mockNode2.getNodeID());
            verifyMaximumResourceCapability(fullResource1, scheduler);
            scheduler.nodeTracker.removeNode(mockNode1.getNodeID());
            verifyMaximumResourceCapability(configuredMaximumResource, scheduler);
        } finally {
            stop();
        }
    }

    @Test
    public void testMaxAllocationAfterUpdateNodeResource() throws IOException {
        final int configuredMaxVCores = 20;
        final int configuredMaxMemory = 10 * 1024;
        Resource configuredMaximumResource = Resource.newInstance(configuredMaxMemory, configuredMaxVCores);
        YarnConfiguration conf = getConf();
        conf.setInt(RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES, configuredMaxVCores);
        conf.setInt(RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, configuredMaxMemory);
        conf.setLong(RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS, 0);
        MockRM rm = new MockRM(conf);
        try {
            start();
            AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
            verifyMaximumResourceCapability(configuredMaximumResource, scheduler);
            Resource resource1 = Resource.newInstance(2048, 5);
            Resource resource2 = Resource.newInstance(4096, 10);
            Resource resource3 = Resource.newInstance(512, 1);
            Resource resource4 = Resource.newInstance(1024, 2);
            RMNode node1 = MockNodes.newNodeInfo(0, resource1, 1, "127.0.0.2");
            scheduler.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node1));
            RMNode node2 = MockNodes.newNodeInfo(0, resource3, 2, "127.0.0.3");
            scheduler.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node2));
            verifyMaximumResourceCapability(resource1, scheduler);
            // increase node1 resource
            scheduler.updateNodeResource(node1, ResourceOption.newInstance(resource2, 0));
            verifyMaximumResourceCapability(resource2, scheduler);
            // decrease node1 resource
            scheduler.updateNodeResource(node1, ResourceOption.newInstance(resource1, 0));
            verifyMaximumResourceCapability(resource1, scheduler);
            // increase node2 resource
            scheduler.updateNodeResource(node2, ResourceOption.newInstance(resource4, 0));
            verifyMaximumResourceCapability(resource1, scheduler);
            // decrease node2 resource
            scheduler.updateNodeResource(node2, ResourceOption.newInstance(resource3, 0));
            verifyMaximumResourceCapability(resource1, scheduler);
        } finally {
            stop();
        }
    }

    /* This test case is to test the pending containers are cleared from the
    attempt even if one of the application in the list have current attempt as
    null (no attempt).
     */
    @SuppressWarnings({ "rawtypes" })
    @Test(timeout = 10000)
    public void testReleasedContainerIfAppAttemptisNull() throws Exception {
        YarnConfiguration conf = getConf();
        MockRM rm1 = new MockRM(conf);
        try {
            start();
            MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
            nm1.registerNode();
            AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
            // Mock App without attempt
            RMApp mockAPp = new MockRMApp(125, System.currentTimeMillis(), RMAppState.NEW);
            SchedulerApplication<FiCaSchedulerApp> application = new SchedulerApplication<FiCaSchedulerApp>(null, mockAPp.getUser());
            // Second app with one app attempt
            RMApp app = rm1.submitApp(200);
            MockAM am1 = MockRM.launchAndRegisterAM(app, rm1, nm1);
            final ContainerId runningContainer = ContainerId.newContainerId(am1.getApplicationAttemptId(), 2);
            am1.allocate(null, Arrays.asList(runningContainer));
            Map schedulerApplications = scheduler.getSchedulerApplications();
            SchedulerApplication schedulerApp = ((SchedulerApplication) (scheduler.getSchedulerApplications().get(app.getApplicationId())));
            schedulerApplications.put(mockAPp.getApplicationId(), application);
            scheduler.clearPendingContainerCache();
            Assert.assertEquals(("Pending containers are not released " + "when one of the application attempt is null !"), schedulerApp.getCurrentAppAttempt().getPendingRelease().size(), 0);
        } finally {
            if (rm1 != null) {
                stop();
            }
        }
    }

    @Test(timeout = 30000L)
    public void testContainerReleaseWithAllocationTags() throws Exception {
        // Currently only can be tested against capacity scheduler.
        if (getSchedulerType().equals(CAPACITY)) {
            final String testTag1 = "some-tag";
            final String testTag2 = "some-other-tag";
            YarnConfiguration conf = getConf();
            conf.set(RM_PLACEMENT_CONSTRAINTS_HANDLER, "scheduler");
            MockRM rm1 = new MockRM(conf);
            start();
            MockNM nm1 = new MockNM("127.0.0.1:1234", 10240, getResourceTrackerService());
            nm1.registerNode();
            RMApp app1 = rm1.submitApp(200, "name", "user", new HashMap<>(), false, "default", (-1), null, "Test", false, true);
            MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
            // allocate 1 container with tag1
            SchedulingRequest sr = SchedulingRequest.newInstance(1L, Priority.newInstance(1), ExecutionTypeRequest.newInstance(GUARANTEED), Sets.newHashSet(testTag1), ResourceSizing.newInstance(1, Resource.newInstance(1024, 1)), null);
            // allocate 3 containers with tag2
            SchedulingRequest sr1 = SchedulingRequest.newInstance(2L, Priority.newInstance(1), ExecutionTypeRequest.newInstance(GUARANTEED), Sets.newHashSet(testTag2), ResourceSizing.newInstance(3, Resource.newInstance(1024, 1)), null);
            AllocateRequest ar = AllocateRequest.newBuilder().schedulingRequests(Lists.newArrayList(sr, sr1)).build();
            am1.allocate(ar);
            nm1.nodeHeartbeat(true);
            List<Container> allocated = new ArrayList<>();
            while ((allocated.size()) < 4) {
                AllocateResponse rsp = am1.allocate(new ArrayList<>(), new ArrayList<>());
                allocated.addAll(rsp.getAllocatedContainers());
                nm1.nodeHeartbeat(true);
                Thread.sleep(1000);
            } 
            Assert.assertEquals(4, allocated.size());
            Set<Container> containers = allocated.stream().filter(( container) -> (container.getAllocationRequestId()) == 1L).collect(Collectors.toSet());
            Assert.assertNotNull(containers);
            Assert.assertEquals(1, containers.size());
            ContainerId cid = containers.iterator().next().getId();
            // mock container start
            getRMContext().getScheduler().getSchedulerNode(nm1.getNodeId()).containerStarted(cid);
            // verifies the allocation is made with correct number of tags
            Map<String, Long> nodeTags = getRMContext().getAllocationTagsManager().getAllocationTagsWithCount(nm1.getNodeId());
            Assert.assertNotNull(nodeTags.get(testTag1));
            Assert.assertEquals(1, nodeTags.get(testTag1).intValue());
            // release a container
            am1.allocate(new ArrayList(), Lists.newArrayList(cid));
            // before NM confirms, the tag should still exist
            nodeTags = getRMContext().getAllocationTagsManager().getAllocationTagsWithCount(nm1.getNodeId());
            Assert.assertNotNull(nodeTags);
            Assert.assertNotNull(nodeTags.get(testTag1));
            Assert.assertEquals(1, nodeTags.get(testTag1).intValue());
            // NM reports back that container is released
            // RM should cleanup the tag
            ContainerStatus cs = ContainerStatus.newInstance(cid, COMPLETE, "", 0);
            nm1.nodeHeartbeat(Lists.newArrayList(cs), true);
            // Wait on condition
            // 1) tag1 doesn't exist anymore
            // 2) num of tag2 is still 3
            GenericTestUtils.waitFor(() -> {
                Map<String, Long> tags = rm1.getRMContext().getAllocationTagsManager().getAllocationTagsWithCount(nm1.getNodeId());
                return ((tags.get(testTag1)) == null) && ((tags.get(testTag2).intValue()) == 3);
            }, 500, 3000);
        }
    }

    @Test(timeout = 60000)
    public void testContainerReleasedByNode() throws Exception {
        System.out.println("Starting testContainerReleasedByNode");
        YarnConfiguration conf = getConf();
        MockRM rm1 = new MockRM(conf);
        try {
            start();
            RMApp app1 = rm1.submitApp(200, "name", "user", new HashMap<ApplicationAccessType, String>(), false, "default", (-1), null, "Test", false, true);
            MockNM nm1 = new MockNM("127.0.0.1:1234", 10240, getResourceTrackerService());
            nm1.registerNode();
            MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
            // allocate a container that fills more than half the node
            am1.allocate("127.0.0.1", 8192, 1, new ArrayList<ContainerId>());
            nm1.nodeHeartbeat(true);
            // wait for containers to be allocated.
            List<Container> containers = am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
            while (containers.isEmpty()) {
                Thread.sleep(10);
                nm1.nodeHeartbeat(true);
                containers = am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
            } 
            // release the container from the AM
            ContainerId cid = containers.get(0).getId();
            List<ContainerId> releasedContainers = new ArrayList<>(1);
            releasedContainers.add(cid);
            List<ContainerStatus> completedContainers = am1.allocate(new ArrayList<ResourceRequest>(), releasedContainers).getCompletedContainersStatuses();
            while (completedContainers.isEmpty()) {
                Thread.sleep(10);
                completedContainers = am1.allocate(new ArrayList<ResourceRequest>(), releasedContainers).getCompletedContainersStatuses();
            } 
            // verify new container can be allocated immediately because container
            // never launched on the node
            containers = am1.allocate("127.0.0.1", 8192, 1, new ArrayList<ContainerId>()).getAllocatedContainers();
            nm1.nodeHeartbeat(true);
            while (containers.isEmpty()) {
                Thread.sleep(10);
                nm1.nodeHeartbeat(true);
                containers = am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
            } 
            // launch the container on the node
            cid = containers.get(0).getId();
            nm1.nodeHeartbeat(cid.getApplicationAttemptId(), cid.getContainerId(), RUNNING);
            rm1.waitForState(nm1, cid, RMContainerState.RUNNING);
            // release the container from the AM
            releasedContainers.clear();
            releasedContainers.add(cid);
            completedContainers = am1.allocate(new ArrayList<ResourceRequest>(), releasedContainers).getCompletedContainersStatuses();
            while (completedContainers.isEmpty()) {
                Thread.sleep(10);
                completedContainers = am1.allocate(new ArrayList<ResourceRequest>(), releasedContainers).getCompletedContainersStatuses();
            } 
            // verify new container cannot be allocated immediately because container
            // has not been released by the node
            containers = am1.allocate("127.0.0.1", 8192, 1, new ArrayList<ContainerId>()).getAllocatedContainers();
            nm1.nodeHeartbeat(true);
            Assert.assertTrue("new container allocated before node freed old", containers.isEmpty());
            for (int i = 0; i < 10; ++i) {
                Thread.sleep(10);
                containers = am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
                nm1.nodeHeartbeat(true);
                Assert.assertTrue("new container allocated before node freed old", containers.isEmpty());
            }
            // free the old container from the node
            nm1.nodeHeartbeat(cid.getApplicationAttemptId(), cid.getContainerId(), COMPLETE);
            // verify new container is now allocated
            containers = am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
            while (containers.isEmpty()) {
                Thread.sleep(10);
                nm1.nodeHeartbeat(true);
                containers = am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
            } 
        } finally {
            stop();
            System.out.println("Stopping testContainerReleasedByNode");
        }
    }

    @Test(timeout = 60000)
    public void testResourceRequestRestoreWhenRMContainerIsAtAllocated() throws Exception {
        YarnConfiguration conf = getConf();
        MockRM rm1 = new MockRM(conf);
        try {
            start();
            RMApp app1 = rm1.submitApp(200, "name", "user", new HashMap<ApplicationAccessType, String>(), false, "default", (-1), null, "Test", false, true);
            MockNM nm1 = new MockNM("127.0.0.1:1234", 10240, getResourceTrackerService());
            nm1.registerNode();
            MockNM nm2 = new MockNM("127.0.0.1:2351", 10240, getResourceTrackerService());
            nm2.registerNode();
            MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
            int NUM_CONTAINERS = 1;
            // allocate NUM_CONTAINERS containers
            am1.allocate("127.0.0.1", 1024, NUM_CONTAINERS, new ArrayList<ContainerId>());
            nm1.nodeHeartbeat(true);
            // wait for containers to be allocated.
            List<Container> containers = am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
            while ((containers.size()) != NUM_CONTAINERS) {
                nm1.nodeHeartbeat(true);
                containers.addAll(am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers());
                Thread.sleep(200);
            } 
            // launch the 2nd container, for testing running container transferred.
            nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 2, RUNNING);
            ContainerId containerId2 = ContainerId.newContainerId(am1.getApplicationAttemptId(), 2);
            rm1.waitForState(nm1, containerId2, RMContainerState.RUNNING);
            // 3rd container is in Allocated state.
            am1.allocate("127.0.0.1", 1024, NUM_CONTAINERS, new ArrayList<ContainerId>());
            nm2.nodeHeartbeat(true);
            ContainerId containerId3 = ContainerId.newContainerId(am1.getApplicationAttemptId(), 3);
            rm1.waitForState(nm2, containerId3, ALLOCATED);
            // NodeManager restart
            nm2.registerNode();
            // NM restart kills all allocated and running containers.
            rm1.waitForState(nm2, containerId3, KILLED);
            // The killed RMContainer request should be restored. In successive
            // nodeHeartBeats AM should be able to get container allocated.
            containers = am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
            while ((containers.size()) != NUM_CONTAINERS) {
                nm2.nodeHeartbeat(true);
                containers.addAll(am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers());
                Thread.sleep(200);
            } 
            nm2.nodeHeartbeat(am1.getApplicationAttemptId(), 4, RUNNING);
            ContainerId containerId4 = ContainerId.newContainerId(am1.getApplicationAttemptId(), 4);
            rm1.waitForState(nm2, containerId4, RMContainerState.RUNNING);
        } finally {
            stop();
        }
    }

    /**
     * Test to verify that ResourceRequests recovery back to the right app-attempt
     * after a container gets killed at ACQUIRED state: YARN-4502.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testResourceRequestRecoveryToTheRightAppAttempt() throws Exception {
        YarnConfiguration conf = getConf();
        MockRM rm = new MockRM(conf);
        try {
            start();
            RMApp rmApp = rm.submitApp(200, "name", "user", new HashMap<ApplicationAccessType, String>(), false, "default", (-1), null, "Test", false, true);
            MockNM node = new MockNM("127.0.0.1:1234", 10240, getResourceTrackerService());
            node.registerNode();
            MockAM am1 = MockRM.launchAndRegisterAM(rmApp, rm, node);
            ApplicationAttemptId applicationAttemptOneID = am1.getApplicationAttemptId();
            ContainerId am1ContainerID = ContainerId.newContainerId(applicationAttemptOneID, 1);
            // allocate NUM_CONTAINERS containers
            am1.allocate("127.0.0.1", 1024, 1, new ArrayList<ContainerId>());
            node.nodeHeartbeat(true);
            // wait for containers to be allocated.
            List<Container> containers = am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
            while ((containers.size()) != 1) {
                node.nodeHeartbeat(true);
                containers.addAll(am1.allocate(new ArrayList<ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers());
                Thread.sleep(200);
            } 
            // launch a 2nd container, for testing running-containers transfer.
            node.nodeHeartbeat(applicationAttemptOneID, 2, RUNNING);
            ContainerId runningContainerID = ContainerId.newContainerId(applicationAttemptOneID, 2);
            rm.waitForState(node, runningContainerID, RMContainerState.RUNNING);
            // 3rd container is in Allocated state.
            int ALLOCATED_CONTAINER_PRIORITY = 1047;
            am1.allocate("127.0.0.1", 1024, 1, ALLOCATED_CONTAINER_PRIORITY, new ArrayList<ContainerId>(), null);
            node.nodeHeartbeat(true);
            ContainerId allocatedContainerID = ContainerId.newContainerId(applicationAttemptOneID, 3);
            rm.waitForState(node, allocatedContainerID, ALLOCATED);
            RMContainer allocatedContainer = getResourceScheduler().getRMContainer(allocatedContainerID);
            // Capture scheduler app-attempt before AM crash.
            SchedulerApplicationAttempt firstSchedulerAppAttempt = ((AbstractYarnScheduler<SchedulerApplicationAttempt, SchedulerNode>) (getResourceScheduler())).getApplicationAttempt(applicationAttemptOneID);
            // AM crashes, and a new app-attempt gets created
            node.nodeHeartbeat(applicationAttemptOneID, 1, COMPLETE);
            rm.drainEvents();
            RMAppAttempt rmAppAttempt2 = MockRM.waitForAttemptScheduled(rmApp, rm);
            ApplicationAttemptId applicationAttemptTwoID = rmAppAttempt2.getAppAttemptId();
            Assert.assertEquals(2, applicationAttemptTwoID.getAttemptId());
            // All outstanding allocated containers will be killed (irrespective of
            // keep-alive of container across app-attempts)
            Assert.assertEquals(KILLED, allocatedContainer.getState());
            // The core part of this test
            // The killed containers' ResourceRequests are recovered back to the
            // original app-attempt, not the new one
            for (SchedulerRequestKey key : firstSchedulerAppAttempt.getSchedulerKeys()) {
                if ((key.getPriority().getPriority()) == 0) {
                    Assert.assertEquals(0, firstSchedulerAppAttempt.getOutstandingAsksCount(key));
                } else
                    if ((key.getPriority().getPriority()) == ALLOCATED_CONTAINER_PRIORITY) {
                        Assert.assertEquals(1, firstSchedulerAppAttempt.getOutstandingAsksCount(key));
                    }

            }
            // Also, only one running container should be transferred after AM
            // launches
            MockRM.launchAM(rmApp, rm, node);
            List<Container> transferredContainers = getResourceScheduler().getTransferredContainers(applicationAttemptTwoID);
            Assert.assertEquals(1, transferredContainers.size());
            Assert.assertEquals(runningContainerID, transferredContainers.get(0).getId());
        } finally {
            stop();
        }
    }

    private class SleepHandler implements EventHandler<SchedulerEvent> {
        boolean sleepFlag = false;

        int sleepTime = 20;

        @Override
        public void handle(SchedulerEvent event) {
            try {
                if (sleepFlag) {
                    Thread.sleep(sleepTime);
                }
            } catch (InterruptedException ie) {
            }
        }
    }

    /**
     * Test the behavior of the scheduler when a node reconnects
     * with changed capabilities. This test is to catch any race conditions
     * that might occur due to the use of the RMNode object.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testNodemanagerReconnect() throws Exception {
        Configuration conf = getConf();
        MockRM rm = new MockRM(conf);
        try {
            start();
            DrainDispatcher privateDispatcher = new DrainDispatcher();
            privateDispatcher.disableExitOnDispatchException();
            TestAbstractYarnScheduler.SleepHandler sleepHandler = new TestAbstractYarnScheduler.SleepHandler();
            ResourceTrackerService privateResourceTrackerService = getPrivateResourceTrackerService(privateDispatcher, rm, sleepHandler);
            // Register node1
            String hostname1 = "localhost1";
            Resource capability = BuilderUtils.newResource(4096, 4);
            RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
            RegisterNodeManagerRequest request1 = recordFactory.newRecordInstance(RegisterNodeManagerRequest.class);
            NodeId nodeId1 = NodeId.newInstance(hostname1, 0);
            request1.setNodeId(nodeId1);
            request1.setHttpPort(0);
            request1.setResource(capability);
            privateResourceTrackerService.registerNodeManager(request1);
            privateDispatcher.await();
            Resource clusterResource = getResourceScheduler().getClusterResource();
            Assert.assertEquals("Initial cluster resources don't match", capability, clusterResource);
            Resource newCapability = BuilderUtils.newResource(1024, 1);
            RegisterNodeManagerRequest request2 = recordFactory.newRecordInstance(RegisterNodeManagerRequest.class);
            request2.setNodeId(nodeId1);
            request2.setHttpPort(0);
            request2.setResource(newCapability);
            // hold up the disaptcher and register the same node with lower capability
            sleepHandler.sleepFlag = true;
            privateResourceTrackerService.registerNodeManager(request2);
            privateDispatcher.await();
            Assert.assertEquals("Cluster resources don't match", newCapability, getResourceScheduler().getClusterResource());
            privateResourceTrackerService.stop();
        } finally {
            stop();
        }
    }

    @Test(timeout = 10000)
    public void testUpdateThreadLifeCycle() throws Exception {
        MockRM rm = new MockRM(getConf());
        try {
            start();
            AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
            if (getSchedulerType().equals(FAIR)) {
                Thread updateThread = scheduler.updateThread;
                Assert.assertTrue(updateThread.isAlive());
                scheduler.stop();
                int numRetries = 100;
                while (((numRetries--) > 0) && (updateThread.isAlive())) {
                    Thread.sleep(50);
                } 
                Assert.assertNotEquals("The Update thread is still alive", 0, numRetries);
            } else
                if (getSchedulerType().equals(CAPACITY)) {
                    Assert.assertNull("updateThread shouldn't have been created", scheduler.updateThread);
                } else {
                    Assert.fail((("Unhandled SchedulerType, " + (getSchedulerType())) + ", please update this unit test."));
                }

        } finally {
            stop();
        }
    }

    @Test(timeout = 60000)
    public void testContainerRecoveredByNode() throws Exception {
        System.out.println("Starting testContainerRecoveredByNode");
        final int maxMemory = 10 * 1024;
        YarnConfiguration conf = getConf();
        conf.setBoolean(RECOVERY_ENABLED, true);
        conf.setBoolean(RM_WORK_PRESERVING_RECOVERY_ENABLED, true);
        conf.set(RM_STORE, MemoryRMStateStore.class.getName());
        MockRM rm1 = new MockRM(conf);
        try {
            start();
            RMApp app1 = rm1.submitApp(200, "name", "user", new HashMap<ApplicationAccessType, String>(), false, "default", (-1), null, "Test", false, true);
            MockNM nm1 = new MockNM("127.0.0.1:1234", 10240, getResourceTrackerService());
            nm1.registerNode();
            MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
            am1.allocate("127.0.0.1", 8192, 1, new ArrayList<ContainerId>());
            YarnScheduler scheduler = rm1.getResourceScheduler();
            RMNode node1 = MockNodes.newNodeInfo(0, Resources.createResource(maxMemory), 1, "127.0.0.2");
            ContainerId containerId = ContainerId.newContainerId(app1.getCurrentAppAttempt().getAppAttemptId(), 2);
            NMContainerStatus containerReport = NMContainerStatus.newInstance(containerId, 0, RUNNING, Resource.newInstance(1024, 1), "recover container", 0, Priority.newInstance(0), 0);
            List<NMContainerStatus> containerReports = new ArrayList<>();
            containerReports.add(containerReport);
            scheduler.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent(node1, containerReports));
            RMContainer rmContainer = scheduler.getRMContainer(containerId);
            // verify queue name when rmContainer is recovered
            Assert.assertEquals(app1.getQueue(), rmContainer.getQueueName());
        } finally {
            stop();
            System.out.println("Stopping testContainerRecoveredByNode");
        }
    }
}

