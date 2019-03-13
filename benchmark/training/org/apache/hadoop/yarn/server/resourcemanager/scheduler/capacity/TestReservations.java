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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;


import CapacitySchedulerConfiguration.RESERVE_CONT_LOOK_ALL_NODES;
import RMNodeLabelsManager.NO_LABEL;
import ResourceRequest.ANY;
import SchedulingMode.RESPECT_PARTITION_EXCLUSIVITY;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.ahs.RMApplicationHistoryWriter;
import org.apache.hadoop.yarn.server.resourcemanager.metrics.SystemMetricsPublisher;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.ContainerAllocationExpirer;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ActiveUsersManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt.AMState;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode;
import org.apache.hadoop.yarn.server.scheduler.SchedulerRequestKey;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


public class TestReservations {
    private static final Logger LOG = LoggerFactory.getLogger(TestReservations.class);

    private final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    RMContext rmContext;

    RMContext spyRMContext;

    CapacityScheduler cs;

    // CapacitySchedulerConfiguration csConf;
    CapacitySchedulerContext csContext;

    private final ResourceCalculator resourceCalculator = new DefaultResourceCalculator();

    CSQueue root;

    Map<String, CSQueue> queues = new HashMap<String, CSQueue>();

    Map<String, CSQueue> oldQueues = new HashMap<String, CSQueue>();

    static final int GB = 1024;

    static final String DEFAULT_RACK = "/default";

    private static final String A = "a";

    @Test
    @SuppressWarnings("unchecked")
    public void testReservation() throws Exception {
        // Test that we now unreserve and use a node that has space
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setup(csConf);
        // Manipulate queue 'a'
        LeafQueue a = TestReservations.stubLeafQueue(((LeafQueue) (queues.get(TestReservations.A))));
        // Users
        final String user_0 = "user_0";
        // Submit applications
        final ApplicationAttemptId appAttemptId_0 = TestUtils.getMockApplicationAttemptId(0, 0);
        FiCaSchedulerApp app_0 = new FiCaSchedulerApp(appAttemptId_0, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_0 = Mockito.spy(app_0);
        Mockito.doNothing().when(app_0).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        rmContext.getRMApps().put(app_0.getApplicationId(), Mockito.mock(RMApp.class));
        a.submitApplicationAttempt(app_0, user_0);
        final ApplicationAttemptId appAttemptId_1 = TestUtils.getMockApplicationAttemptId(1, 0);
        FiCaSchedulerApp app_1 = new FiCaSchedulerApp(appAttemptId_1, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_1 = Mockito.spy(app_1);
        Mockito.doNothing().when(app_1).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        a.submitApplicationAttempt(app_1, user_0);
        // Setup some nodes
        String host_0 = "host_0";
        FiCaSchedulerNode node_0 = TestUtils.getMockNode(host_0, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_1 = "host_1";
        FiCaSchedulerNode node_1 = TestUtils.getMockNode(host_1, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_2 = "host_2";
        FiCaSchedulerNode node_2 = TestUtils.getMockNode(host_2, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        Map<ApplicationAttemptId, FiCaSchedulerApp> apps = ImmutableMap.of(app_0.getApplicationAttemptId(), app_0, app_1.getApplicationAttemptId(), app_1);
        Map<NodeId, FiCaSchedulerNode> nodes = ImmutableMap.of(node_0.getNodeID(), node_0, node_1.getNodeID(), node_1, node_2.getNodeID(), node_2);
        Mockito.when(csContext.getNode(node_0.getNodeID())).thenReturn(node_0);
        Mockito.when(csContext.getNode(node_1.getNodeID())).thenReturn(node_1);
        Mockito.when(csContext.getNode(node_2.getNodeID())).thenReturn(node_2);
        cs.getNodeTracker().addNode(node_0);
        cs.getNodeTracker().addNode(node_1);
        cs.getNodeTracker().addNode(node_2);
        final int numNodes = 3;
        Resource clusterResource = Resources.createResource((numNodes * (8 * (TestReservations.GB))));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new ResourceLimits(clusterResource));
        // Setup resource-requests
        Priority priorityAM = TestUtils.createMockPriority(1);
        Priority priorityMap = TestUtils.createMockPriority(5);
        Priority priorityReduce = TestUtils.createMockPriority(10);
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (2 * (TestReservations.GB)), 1, true, priorityAM, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (5 * (TestReservations.GB)), 2, true, priorityReduce, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (3 * (TestReservations.GB)), 2, true, priorityMap, recordFactory)));
        // Start testing...
        // Only AM
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((2 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((2 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((22 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((2 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // Only 1 map - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((5 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((19 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // Only 1 map to other node - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_1, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((8 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((16 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((16 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals(null, node_0.getReservedContainer());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        Assert.assertEquals(2, app_0.getOutstandingAsksCount(TestUtils.toSchedulerKey(priorityReduce)));
        // try to assign reducer (5G on node 0 and should reserve)
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((13 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((11 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((11 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getReservedContainer().getReservedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        Assert.assertEquals(2, app_0.getOutstandingAsksCount(TestUtils.toSchedulerKey(priorityReduce)));
        // assign reducer to node 2
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_2, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((18 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((13 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((13 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((6 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((6 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getReservedContainer().getReservedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        Assert.assertEquals(1, app_0.getOutstandingAsksCount(TestUtils.toSchedulerKey(priorityReduce)));
        // node_1 heartbeat and unreserves from node_0 in order to allocate
        // on node_1
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_1, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((18 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((18 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((18 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((6 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((6 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals(null, node_0.getReservedContainer());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        Assert.assertEquals(0, app_0.getOutstandingAsksCount(TestUtils.toSchedulerKey(priorityReduce)));
    }

    // Test that hitting a reservation limit and needing to unreserve
    // does not affect assigning containers for other users
    @Test
    @SuppressWarnings("unchecked")
    public void testReservationLimitOtherUsers() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setup(csConf, true);
        // Manipulate queue 'a'
        LeafQueue a = TestReservations.stubLeafQueue(((LeafQueue) (queues.get(TestReservations.A))));
        // Users
        final String user_0 = "user_0";
        final String user_1 = "user_1";
        // Submit applications
        final ApplicationAttemptId appAttemptId_0 = TestUtils.getMockApplicationAttemptId(0, 0);
        FiCaSchedulerApp app_0 = new FiCaSchedulerApp(appAttemptId_0, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_0 = Mockito.spy(app_0);
        Mockito.doNothing().when(app_0).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        rmContext.getRMApps().put(app_0.getApplicationId(), Mockito.mock(RMApp.class));
        a.submitApplicationAttempt(app_0, user_0);
        final ApplicationAttemptId appAttemptId_1 = TestUtils.getMockApplicationAttemptId(1, 0);
        FiCaSchedulerApp app_1 = new FiCaSchedulerApp(appAttemptId_1, user_1, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_1 = Mockito.spy(app_1);
        Mockito.doNothing().when(app_1).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        rmContext.getRMApps().put(app_1.getApplicationId(), Mockito.mock(RMApp.class));
        a.submitApplicationAttempt(app_1, user_1);
        // Setup some nodes
        String host_0 = "host_0";
        FiCaSchedulerNode node_0 = TestUtils.getMockNode(host_0, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_1 = "host_1";
        FiCaSchedulerNode node_1 = TestUtils.getMockNode(host_1, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_2 = "host_2";
        FiCaSchedulerNode node_2 = TestUtils.getMockNode(host_2, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        Mockito.when(csContext.getNode(node_0.getNodeID())).thenReturn(node_0);
        Mockito.when(csContext.getNode(node_1.getNodeID())).thenReturn(node_1);
        Mockito.when(csContext.getNode(node_2.getNodeID())).thenReturn(node_2);
        Map<ApplicationAttemptId, FiCaSchedulerApp> apps = ImmutableMap.of(app_0.getApplicationAttemptId(), app_0, app_1.getApplicationAttemptId(), app_1);
        Map<NodeId, FiCaSchedulerNode> nodes = ImmutableMap.of(node_0.getNodeID(), node_0, node_1.getNodeID(), node_1, node_2.getNodeID(), node_2);
        cs.getNodeTracker().addNode(node_0);
        cs.getNodeTracker().addNode(node_1);
        cs.getNodeTracker().addNode(node_2);
        final int numNodes = 3;
        Resource clusterResource = Resources.createResource((numNodes * (8 * (TestReservations.GB))));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new ResourceLimits(clusterResource));
        // Setup resource-requests
        Priority priorityAM = TestUtils.createMockPriority(1);
        Priority priorityMap = TestUtils.createMockPriority(5);
        Priority priorityReduce = TestUtils.createMockPriority(10);
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (2 * (TestReservations.GB)), 1, true, priorityAM, recordFactory)));
        app_1.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (2 * (TestReservations.GB)), 1, true, priorityAM, recordFactory)));
        // Start testing...
        // Only AM
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((2 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), app_1.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((2 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((22 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((2 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_1, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((4 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_1.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((4 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((20 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((2 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // Add a few requests to each app
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (8 * (TestReservations.GB)), 2, true, priorityMap, recordFactory)));
        app_1.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (2 * (TestReservations.GB)), 2, true, priorityMap, recordFactory)));
        // add a reservation for app_0
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((12 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_1.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((4 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((12 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((2 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // next assignment is beyond user limit for user_0 but it should assign to
        // app_1 for user_1
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_1, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((14 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((4 * (TestReservations.GB)), app_1.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((6 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((10 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((2 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((4 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
    }

    @Test
    public void testReservationNoContinueLook() throws Exception {
        // Test that with reservations-continue-look-all-nodes feature off
        // we don't unreserve and show we could get stuck
        queues = new HashMap<String, CSQueue>();
        // test that the deadlock occurs when turned off
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        csConf.setBoolean(RESERVE_CONT_LOOK_ALL_NODES, false);
        setup(csConf);
        // Manipulate queue 'a'
        LeafQueue a = TestReservations.stubLeafQueue(((LeafQueue) (queues.get(TestReservations.A))));
        // Users
        final String user_0 = "user_0";
        // Submit applications
        final ApplicationAttemptId appAttemptId_0 = TestUtils.getMockApplicationAttemptId(0, 0);
        FiCaSchedulerApp app_0 = new FiCaSchedulerApp(appAttemptId_0, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_0 = Mockito.spy(app_0);
        Mockito.doNothing().when(app_0).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        rmContext.getRMApps().put(app_0.getApplicationId(), Mockito.mock(RMApp.class));
        a.submitApplicationAttempt(app_0, user_0);
        final ApplicationAttemptId appAttemptId_1 = TestUtils.getMockApplicationAttemptId(1, 0);
        FiCaSchedulerApp app_1 = new FiCaSchedulerApp(appAttemptId_1, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_1 = Mockito.spy(app_1);
        Mockito.doNothing().when(app_1).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        a.submitApplicationAttempt(app_1, user_0);
        // Setup some nodes
        String host_0 = "host_0";
        FiCaSchedulerNode node_0 = TestUtils.getMockNode(host_0, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_1 = "host_1";
        FiCaSchedulerNode node_1 = TestUtils.getMockNode(host_1, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_2 = "host_2";
        FiCaSchedulerNode node_2 = TestUtils.getMockNode(host_2, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        Map<ApplicationAttemptId, FiCaSchedulerApp> apps = ImmutableMap.of(app_0.getApplicationAttemptId(), app_0, app_1.getApplicationAttemptId(), app_1);
        Map<NodeId, FiCaSchedulerNode> nodes = ImmutableMap.of(node_0.getNodeID(), node_0, node_1.getNodeID(), node_1, node_2.getNodeID(), node_2);
        Mockito.when(csContext.getNode(node_0.getNodeID())).thenReturn(node_0);
        Mockito.when(csContext.getNode(node_1.getNodeID())).thenReturn(node_1);
        Mockito.when(csContext.getNode(node_2.getNodeID())).thenReturn(node_2);
        final int numNodes = 3;
        Resource clusterResource = Resources.createResource((numNodes * (8 * (TestReservations.GB))));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new ResourceLimits(clusterResource));
        // Setup resource-requests
        Priority priorityAM = TestUtils.createMockPriority(1);
        Priority priorityMap = TestUtils.createMockPriority(5);
        Priority priorityReduce = TestUtils.createMockPriority(10);
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (2 * (TestReservations.GB)), 1, true, priorityAM, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (5 * (TestReservations.GB)), 2, true, priorityReduce, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (3 * (TestReservations.GB)), 2, true, priorityMap, recordFactory)));
        // Start testing...
        // Only AM
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((2 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((2 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((22 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((2 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // Only 1 map - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((5 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((19 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // Only 1 map to other node - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_1, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((8 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((16 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((16 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals(null, node_0.getReservedContainer());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        Assert.assertEquals(2, app_0.getOutstandingAsksCount(TestUtils.toSchedulerKey(priorityReduce)));
        // try to assign reducer (5G on node 0 and should reserve)
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((13 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((11 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((11 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getReservedContainer().getReservedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        Assert.assertEquals(2, app_0.getOutstandingAsksCount(TestUtils.toSchedulerKey(priorityReduce)));
        // assign reducer to node 2
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_2, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((18 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((13 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((13 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((6 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((6 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getReservedContainer().getReservedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        Assert.assertEquals(1, app_0.getOutstandingAsksCount(TestUtils.toSchedulerKey(priorityReduce)));
        // node_1 heartbeat and won't unreserve from node_0, potentially stuck
        // if AM doesn't handle
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_1, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((18 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((13 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((13 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((6 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((6 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getReservedContainer().getReservedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        Assert.assertEquals(1, app_0.getOutstandingAsksCount(TestUtils.toSchedulerKey(priorityReduce)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAssignContainersNeedToUnreserve() throws Exception {
        // Test that we now unreserve and use a node that has space
        GenericTestUtils.setRootLogLevel(Level.DEBUG);
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setup(csConf);
        // Manipulate queue 'a'
        LeafQueue a = TestReservations.stubLeafQueue(((LeafQueue) (queues.get(TestReservations.A))));
        // Users
        final String user_0 = "user_0";
        // Submit applications
        final ApplicationAttemptId appAttemptId_0 = TestUtils.getMockApplicationAttemptId(0, 0);
        FiCaSchedulerApp app_0 = new FiCaSchedulerApp(appAttemptId_0, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_0 = Mockito.spy(app_0);
        Mockito.doNothing().when(app_0).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        rmContext.getRMApps().put(app_0.getApplicationId(), Mockito.mock(RMApp.class));
        a.submitApplicationAttempt(app_0, user_0);
        final ApplicationAttemptId appAttemptId_1 = TestUtils.getMockApplicationAttemptId(1, 0);
        FiCaSchedulerApp app_1 = new FiCaSchedulerApp(appAttemptId_1, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_1 = Mockito.spy(app_1);
        Mockito.doNothing().when(app_1).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        a.submitApplicationAttempt(app_1, user_0);
        // Setup some nodes
        String host_0 = "host_0";
        FiCaSchedulerNode node_0 = TestUtils.getMockNode(host_0, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_1 = "host_1";
        FiCaSchedulerNode node_1 = TestUtils.getMockNode(host_1, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        Map<ApplicationAttemptId, FiCaSchedulerApp> apps = ImmutableMap.of(app_0.getApplicationAttemptId(), app_0, app_1.getApplicationAttemptId(), app_1);
        Map<NodeId, FiCaSchedulerNode> nodes = ImmutableMap.of(node_0.getNodeID(), node_0, node_1.getNodeID(), node_1);
        cs.getNodeTracker().addNode(node_0);
        cs.getNodeTracker().addNode(node_1);
        Mockito.when(csContext.getNode(node_0.getNodeID())).thenReturn(node_0);
        Mockito.when(csContext.getNode(node_1.getNodeID())).thenReturn(node_1);
        final int numNodes = 2;
        Resource clusterResource = Resources.createResource((numNodes * (8 * (TestReservations.GB))));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new ResourceLimits(clusterResource));
        // Setup resource-requests
        Priority priorityAM = TestUtils.createMockPriority(1);
        Priority priorityMap = TestUtils.createMockPriority(5);
        Priority priorityReduce = TestUtils.createMockPriority(10);
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (2 * (TestReservations.GB)), 1, true, priorityAM, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (5 * (TestReservations.GB)), 2, true, priorityReduce, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (3 * (TestReservations.GB)), 2, true, priorityMap, recordFactory)));
        // Start testing...
        // Only AM
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((2 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((2 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((14 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((2 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        // Only 1 map - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((5 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((11 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        // Only 1 map to other node - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_1, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((8 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals(null, node_0.getReservedContainer());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals(2, app_0.getOutstandingAsksCount(TestUtils.toSchedulerKey(priorityReduce)));
        // try to assign reducer (5G on node 0 and should reserve)
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((13 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((3 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((3 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getReservedContainer().getReservedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals(2, app_0.getOutstandingAsksCount(TestUtils.toSchedulerKey(priorityReduce)));
        // could allocate but told need to unreserve first
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_1, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((13 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((13 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((13 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((3 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((3 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals(null, node_0.getReservedContainer());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals(1, app_0.getOutstandingAsksCount(TestUtils.toSchedulerKey(priorityReduce)));
    }

    @Test
    public void testGetAppToUnreserve() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setup(csConf);
        final String user_0 = "user_0";
        final ApplicationAttemptId appAttemptId_0 = TestUtils.getMockApplicationAttemptId(0, 0);
        LeafQueue a = TestReservations.stubLeafQueue(((LeafQueue) (queues.get(TestReservations.A))));
        FiCaSchedulerApp app_0 = new FiCaSchedulerApp(appAttemptId_0, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        String host_0 = "host_0";
        FiCaSchedulerNode node_0 = TestUtils.getMockNode(host_0, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_1 = "host_1";
        FiCaSchedulerNode node_1 = TestUtils.getMockNode(host_1, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        Resource clusterResource = Resources.createResource(((2 * 8) * (TestReservations.GB)));
        root.updateClusterResource(clusterResource, new ResourceLimits(clusterResource));
        // Setup resource-requests
        Priority p = TestUtils.createMockPriority(5);
        SchedulerRequestKey priorityMap = TestUtils.toSchedulerKey(p);
        Resource capability = Resources.createResource((2 * (TestReservations.GB)), 0);
        RMApplicationHistoryWriter writer = Mockito.mock(RMApplicationHistoryWriter.class);
        SystemMetricsPublisher publisher = Mockito.mock(SystemMetricsPublisher.class);
        RMContext rmContext = Mockito.mock(RMContext.class);
        ContainerAllocationExpirer expirer = Mockito.mock(ContainerAllocationExpirer.class);
        DrainDispatcher drainDispatcher = new DrainDispatcher();
        Mockito.when(rmContext.getContainerAllocationExpirer()).thenReturn(expirer);
        Mockito.when(rmContext.getDispatcher()).thenReturn(drainDispatcher);
        Mockito.when(rmContext.getRMApplicationHistoryWriter()).thenReturn(writer);
        Mockito.when(rmContext.getSystemMetricsPublisher()).thenReturn(publisher);
        Mockito.when(rmContext.getYarnConfiguration()).thenReturn(new YarnConfiguration());
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(app_0.getApplicationId(), 1);
        ContainerId containerId = BuilderUtils.newContainerId(appAttemptId, 1);
        Container container = TestUtils.getMockContainer(containerId, node_1.getNodeID(), Resources.createResource((2 * (TestReservations.GB))), priorityMap.getPriority());
        RMContainer rmContainer = new org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl(container, SchedulerRequestKey.extractFrom(container), appAttemptId, node_1.getNodeID(), "user", rmContext);
        Container container_1 = TestUtils.getMockContainer(containerId, node_0.getNodeID(), Resources.createResource((1 * (TestReservations.GB))), priorityMap.getPriority());
        RMContainer rmContainer_1 = new org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl(container_1, SchedulerRequestKey.extractFrom(container_1), appAttemptId, node_0.getNodeID(), "user", rmContext);
        // no reserved containers
        NodeId unreserveId = app_0.getNodeIdToUnreserve(priorityMap, capability, cs.getResourceCalculator());
        Assert.assertEquals(null, unreserveId);
        // no reserved containers - reserve then unreserve
        app_0.reserve(node_0, priorityMap, rmContainer_1, container_1);
        app_0.unreserve(priorityMap, node_0, rmContainer_1);
        unreserveId = app_0.getNodeIdToUnreserve(priorityMap, capability, cs.getResourceCalculator());
        Assert.assertEquals(null, unreserveId);
        // no container large enough is reserved
        app_0.reserve(node_0, priorityMap, rmContainer_1, container_1);
        unreserveId = app_0.getNodeIdToUnreserve(priorityMap, capability, cs.getResourceCalculator());
        Assert.assertEquals(null, unreserveId);
        // reserve one that is now large enough
        app_0.reserve(node_1, priorityMap, rmContainer, container);
        unreserveId = app_0.getNodeIdToUnreserve(priorityMap, capability, cs.getResourceCalculator());
        Assert.assertEquals(node_1.getNodeID(), unreserveId);
    }

    @Test
    public void testFindNodeToUnreserve() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setup(csConf);
        final String user_0 = "user_0";
        final ApplicationAttemptId appAttemptId_0 = TestUtils.getMockApplicationAttemptId(0, 0);
        LeafQueue a = TestReservations.stubLeafQueue(((LeafQueue) (queues.get(TestReservations.A))));
        FiCaSchedulerApp app_0 = new FiCaSchedulerApp(appAttemptId_0, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        String host_1 = "host_1";
        FiCaSchedulerNode node_1 = TestUtils.getMockNode(host_1, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        // Setup resource-requests
        Priority p = TestUtils.createMockPriority(5);
        SchedulerRequestKey priorityMap = TestUtils.toSchedulerKey(p);
        Resource capability = Resources.createResource((2 * (TestReservations.GB)), 0);
        RMApplicationHistoryWriter writer = Mockito.mock(RMApplicationHistoryWriter.class);
        SystemMetricsPublisher publisher = Mockito.mock(SystemMetricsPublisher.class);
        RMContext rmContext = Mockito.mock(RMContext.class);
        ContainerAllocationExpirer expirer = Mockito.mock(ContainerAllocationExpirer.class);
        DrainDispatcher drainDispatcher = new DrainDispatcher();
        Mockito.when(rmContext.getContainerAllocationExpirer()).thenReturn(expirer);
        Mockito.when(rmContext.getDispatcher()).thenReturn(drainDispatcher);
        Mockito.when(rmContext.getRMApplicationHistoryWriter()).thenReturn(writer);
        Mockito.when(rmContext.getSystemMetricsPublisher()).thenReturn(publisher);
        Mockito.when(rmContext.getYarnConfiguration()).thenReturn(new YarnConfiguration());
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(app_0.getApplicationId(), 1);
        ContainerId containerId = BuilderUtils.newContainerId(appAttemptId, 1);
        Container container = TestUtils.getMockContainer(containerId, node_1.getNodeID(), Resources.createResource((2 * (TestReservations.GB))), priorityMap.getPriority());
        RMContainer rmContainer = new org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl(container, SchedulerRequestKey.extractFrom(container), appAttemptId, node_1.getNodeID(), "user", rmContext);
        // nothing reserved
        RMContainer toUnreserveContainer = app_0.findNodeToUnreserve(node_1, priorityMap, capability);
        Assert.assertTrue((toUnreserveContainer == null));
        // reserved but scheduler doesn't know about that node.
        app_0.reserve(node_1, priorityMap, rmContainer, container);
        node_1.reserveResource(app_0, priorityMap, rmContainer);
        toUnreserveContainer = app_0.findNodeToUnreserve(node_1, priorityMap, capability);
        Assert.assertTrue((toUnreserveContainer == null));
    }

    @Test
    public void testAssignToQueue() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setup(csConf);
        // Manipulate queue 'a'
        LeafQueue a = TestReservations.stubLeafQueue(((LeafQueue) (queues.get(TestReservations.A))));
        // Users
        final String user_0 = "user_0";
        // Submit applications
        final ApplicationAttemptId appAttemptId_0 = TestUtils.getMockApplicationAttemptId(0, 0);
        FiCaSchedulerApp app_0 = new FiCaSchedulerApp(appAttemptId_0, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_0 = Mockito.spy(app_0);
        Mockito.doNothing().when(app_0).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        rmContext.getRMApps().put(app_0.getApplicationId(), Mockito.mock(RMApp.class));
        a.submitApplicationAttempt(app_0, user_0);
        final ApplicationAttemptId appAttemptId_1 = TestUtils.getMockApplicationAttemptId(1, 0);
        FiCaSchedulerApp app_1 = new FiCaSchedulerApp(appAttemptId_1, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_1 = Mockito.spy(app_1);
        Mockito.doNothing().when(app_1).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        a.submitApplicationAttempt(app_1, user_0);
        // Setup some nodes
        String host_0 = "host_0";
        FiCaSchedulerNode node_0 = TestUtils.getMockNode(host_0, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_1 = "host_1";
        FiCaSchedulerNode node_1 = TestUtils.getMockNode(host_1, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_2 = "host_2";
        FiCaSchedulerNode node_2 = TestUtils.getMockNode(host_2, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        Mockito.when(csContext.getNode(node_0.getNodeID())).thenReturn(node_0);
        Mockito.when(csContext.getNode(node_1.getNodeID())).thenReturn(node_1);
        Mockito.when(csContext.getNode(node_2.getNodeID())).thenReturn(node_2);
        Map<ApplicationAttemptId, FiCaSchedulerApp> apps = ImmutableMap.of(app_0.getApplicationAttemptId(), app_0, app_1.getApplicationAttemptId(), app_1);
        Map<NodeId, FiCaSchedulerNode> nodes = ImmutableMap.of(node_0.getNodeID(), node_0, node_1.getNodeID(), node_1, node_2.getNodeID(), node_2);
        final int numNodes = 2;
        Resource clusterResource = Resources.createResource((numNodes * (8 * (TestReservations.GB))));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new ResourceLimits(clusterResource));
        // Setup resource-requests
        Priority priorityAM = TestUtils.createMockPriority(1);
        Priority priorityMap = TestUtils.createMockPriority(5);
        Priority priorityReduce = TestUtils.createMockPriority(10);
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (2 * (TestReservations.GB)), 1, true, priorityAM, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (5 * (TestReservations.GB)), 2, true, priorityReduce, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (3 * (TestReservations.GB)), 2, true, priorityMap, recordFactory)));
        // Start testing...
        // Only AM
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((2 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((2 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((14 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((2 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        // Only 1 map - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((5 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((11 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        // Only 1 map to other node - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_1, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((8 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals(null, node_0.getReservedContainer());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        // now add in reservations and make sure it continues if config set
        // allocate to queue so that the potential new capacity is greater then
        // absoluteMaxCapacity
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((13 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((3 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((3 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        ResourceLimits limits = new ResourceLimits(Resources.createResource((13 * (TestReservations.GB))));
        boolean res = a.canAssignToThisQueue(Resources.createResource((13 * (TestReservations.GB))), NO_LABEL, limits, Resources.createResource((3 * (TestReservations.GB))), RESPECT_PARTITION_EXCLUSIVITY);
        Assert.assertTrue(res);
        // 16GB total, 13GB consumed (8 allocated, 5 reserved). asking for 5GB so we would have to
        // unreserve 2GB to get the total 5GB needed.
        // also note vcore checks not enabled
        Assert.assertEquals(0, limits.getHeadroom().getMemorySize());
        refreshQueuesTurnOffReservationsContLook(a, csConf);
        // should return false since reservations continue look is off.
        limits = new ResourceLimits(Resources.createResource((13 * (TestReservations.GB))));
        res = a.canAssignToThisQueue(Resources.createResource((13 * (TestReservations.GB))), NO_LABEL, limits, Resources.createResource((3 * (TestReservations.GB))), RESPECT_PARTITION_EXCLUSIVITY);
        Assert.assertFalse(res);
    }

    @Test
    public void testContinueLookingReservationsAfterQueueRefresh() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setup(csConf);
        // Manipulate queue 'e'
        LeafQueue a = TestReservations.stubLeafQueue(((LeafQueue) (queues.get(TestReservations.A))));
        refreshQueuesTurnOffReservationsContLook(a, csConf);
    }

    @Test
    public void testAssignToUser() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setup(csConf);
        // Manipulate queue 'a'
        LeafQueue a = TestReservations.stubLeafQueue(((LeafQueue) (queues.get(TestReservations.A))));
        // Users
        final String user_0 = "user_0";
        // Submit applications
        final ApplicationAttemptId appAttemptId_0 = TestUtils.getMockApplicationAttemptId(0, 0);
        FiCaSchedulerApp app_0 = new FiCaSchedulerApp(appAttemptId_0, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_0 = Mockito.spy(app_0);
        Mockito.doNothing().when(app_0).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        rmContext.getRMApps().put(app_0.getApplicationId(), Mockito.mock(RMApp.class));
        a.submitApplicationAttempt(app_0, user_0);
        final ApplicationAttemptId appAttemptId_1 = TestUtils.getMockApplicationAttemptId(1, 0);
        FiCaSchedulerApp app_1 = new FiCaSchedulerApp(appAttemptId_1, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_1 = Mockito.spy(app_1);
        Mockito.doNothing().when(app_1).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        a.submitApplicationAttempt(app_1, user_0);
        // Setup some nodes
        String host_0 = "host_0";
        FiCaSchedulerNode node_0 = TestUtils.getMockNode(host_0, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_1 = "host_1";
        FiCaSchedulerNode node_1 = TestUtils.getMockNode(host_1, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_2 = "host_2";
        FiCaSchedulerNode node_2 = TestUtils.getMockNode(host_2, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        Map<ApplicationAttemptId, FiCaSchedulerApp> apps = ImmutableMap.of(app_0.getApplicationAttemptId(), app_0, app_1.getApplicationAttemptId(), app_1);
        Map<NodeId, FiCaSchedulerNode> nodes = ImmutableMap.of(node_0.getNodeID(), node_0, node_1.getNodeID(), node_1, node_2.getNodeID(), node_2);
        Mockito.when(csContext.getNode(node_0.getNodeID())).thenReturn(node_0);
        Mockito.when(csContext.getNode(node_1.getNodeID())).thenReturn(node_1);
        Mockito.when(csContext.getNode(node_2.getNodeID())).thenReturn(node_2);
        final int numNodes = 2;
        Resource clusterResource = Resources.createResource((numNodes * (8 * (TestReservations.GB))));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new ResourceLimits(clusterResource));
        // Setup resource-requests
        Priority priorityAM = TestUtils.createMockPriority(1);
        Priority priorityMap = TestUtils.createMockPriority(5);
        Priority priorityReduce = TestUtils.createMockPriority(10);
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (2 * (TestReservations.GB)), 1, true, priorityAM, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (3 * (TestReservations.GB)), 2, true, priorityMap, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (5 * (TestReservations.GB)), 2, true, priorityReduce, recordFactory)));
        // Start testing...
        // Only AM
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((2 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((2 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((14 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((2 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        // Only 1 map - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((5 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((11 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        // Only 1 map to other node - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_1, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((8 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals(null, node_0.getReservedContainer());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        // now add in reservations and make sure it continues if config set
        // allocate to queue so that the potential new capacity is greater then
        // absoluteMaxCapacity
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((13 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), app_0.getCurrentReservation().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((3 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((3 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        // not over the limit
        Resource limit = Resources.createResource((14 * (TestReservations.GB)), 0);
        ResourceLimits userResourceLimits = new ResourceLimits(clusterResource);
        boolean res = a.canAssignToUser(clusterResource, user_0, limit, app_0, "", userResourceLimits);
        Assert.assertTrue(res);
        Assert.assertEquals(Resources.none(), userResourceLimits.getAmountNeededUnreserve());
        // set limit so it subtracts reservations and it can continue
        limit = Resources.createResource((12 * (TestReservations.GB)), 0);
        userResourceLimits = new ResourceLimits(clusterResource);
        res = a.canAssignToUser(clusterResource, user_0, limit, app_0, "", userResourceLimits);
        Assert.assertTrue(res);
        // limit set to 12GB, we are using 13GB (8 allocated,  5 reserved), to get under limit
        // we need to unreserve 1GB
        // also note vcore checks not enabled
        Assert.assertEquals(Resources.createResource((1 * (TestReservations.GB)), 4), userResourceLimits.getAmountNeededUnreserve());
        refreshQueuesTurnOffReservationsContLook(a, csConf);
        userResourceLimits = new ResourceLimits(clusterResource);
        // should now return false since feature off
        res = a.canAssignToUser(clusterResource, user_0, limit, app_0, "", userResourceLimits);
        Assert.assertFalse(res);
        Assert.assertEquals(Resources.none(), userResourceLimits.getAmountNeededUnreserve());
    }

    @Test
    public void testReservationsNoneAvailable() throws Exception {
        // Test that we now unreserve and use a node that has space
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setup(csConf);
        // Manipulate queue 'a'
        LeafQueue a = TestReservations.stubLeafQueue(((LeafQueue) (queues.get(TestReservations.A))));
        // Users
        final String user_0 = "user_0";
        // Submit applications
        final ApplicationAttemptId appAttemptId_0 = TestUtils.getMockApplicationAttemptId(0, 0);
        FiCaSchedulerApp app_0 = new FiCaSchedulerApp(appAttemptId_0, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_0 = Mockito.spy(app_0);
        Mockito.doNothing().when(app_0).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        rmContext.getRMApps().put(app_0.getApplicationId(), Mockito.mock(RMApp.class));
        a.submitApplicationAttempt(app_0, user_0);
        final ApplicationAttemptId appAttemptId_1 = TestUtils.getMockApplicationAttemptId(1, 0);
        FiCaSchedulerApp app_1 = new FiCaSchedulerApp(appAttemptId_1, user_0, a, Mockito.mock(ActiveUsersManager.class), spyRMContext);
        app_1 = Mockito.spy(app_1);
        Mockito.doNothing().when(app_1).updateAMContainerDiagnostics(ArgumentMatchers.any(AMState.class), ArgumentMatchers.any(String.class));
        a.submitApplicationAttempt(app_1, user_0);
        // Setup some nodes
        String host_0 = "host_0";
        FiCaSchedulerNode node_0 = TestUtils.getMockNode(host_0, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_1 = "host_1";
        FiCaSchedulerNode node_1 = TestUtils.getMockNode(host_1, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        String host_2 = "host_2";
        FiCaSchedulerNode node_2 = TestUtils.getMockNode(host_2, TestReservations.DEFAULT_RACK, 0, (8 * (TestReservations.GB)));
        Map<ApplicationAttemptId, FiCaSchedulerApp> apps = ImmutableMap.of(app_0.getApplicationAttemptId(), app_0, app_1.getApplicationAttemptId(), app_1);
        Map<NodeId, FiCaSchedulerNode> nodes = ImmutableMap.of(node_0.getNodeID(), node_0, node_1.getNodeID(), node_1, node_2.getNodeID(), node_2);
        Mockito.when(csContext.getNode(node_0.getNodeID())).thenReturn(node_0);
        Mockito.when(csContext.getNode(node_1.getNodeID())).thenReturn(node_1);
        Mockito.when(csContext.getNode(node_2.getNodeID())).thenReturn(node_2);
        final int numNodes = 3;
        Resource clusterResource = Resources.createResource((numNodes * (8 * (TestReservations.GB))));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new ResourceLimits(clusterResource));
        // Setup resource-requests
        Priority priorityAM = TestUtils.createMockPriority(1);
        Priority priorityMap = TestUtils.createMockPriority(5);
        Priority priorityReduce = TestUtils.createMockPriority(10);
        Priority priorityLast = TestUtils.createMockPriority(12);
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (2 * (TestReservations.GB)), 1, true, priorityAM, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (3 * (TestReservations.GB)), 2, true, priorityMap, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (5 * (TestReservations.GB)), 1, true, priorityReduce, recordFactory)));
        app_0.updateResourceRequests(Collections.singletonList(TestUtils.createResourceRequest(ANY, (8 * (TestReservations.GB)), 2, true, priorityLast, recordFactory)));
        // Start testing...
        // Only AM
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((2 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((2 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((22 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((2 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // Only 1 map - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((5 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((5 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((19 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // Only 1 map to other node - simulating reduce
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_1, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((8 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((16 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((16 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // try to assign reducer (5G on node 0), but tell it's resource limits <
        // used (8G) + required (5G). It will not reserved since it has to unreserve
        // some resource. Even with continous reservation looking, we don't allow
        // unreserve resource to reserve container.
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(Resources.createResource((10 * (TestReservations.GB)))), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((8 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((16 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        // app_0's headroom = limit (10G) - used (8G) = 2G
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // try to assign reducer (5G on node 0), but tell it's resource limits <
        // used (8G) + required (5G). It will not reserved since it has to unreserve
        // some resource. Unfortunately, there's nothing to unreserve.
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_2, new ResourceLimits(Resources.createResource((10 * (TestReservations.GB)))), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((8 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((16 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        // app_0's headroom = limit (10G) - used (8G) = 2G
        Assert.assertEquals((2 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // let it assign 5G to node_2
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_2, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((13 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((13 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((0 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((13 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((11 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((11 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // reserve 8G node_0
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((21 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((13 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((13 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((3 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((3 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
        // try to assign (8G on node 2). No room to allocate,
        // continued to try due to having reservation above,
        // but hits queue limits so can't reserve anymore.
        TestUtils.applyResourceCommitRequest(clusterResource, a.assignContainers(clusterResource, node_2, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY), nodes, apps);
        Assert.assertEquals((21 * (TestReservations.GB)), a.getUsedResources().getMemorySize());
        Assert.assertEquals((13 * (TestReservations.GB)), app_0.getCurrentConsumption().getMemorySize());
        Assert.assertEquals((8 * (TestReservations.GB)), a.getMetrics().getReservedMB());
        Assert.assertEquals((13 * (TestReservations.GB)), a.getMetrics().getAllocatedMB());
        Assert.assertEquals((3 * (TestReservations.GB)), a.getMetrics().getAvailableMB());
        Assert.assertEquals((3 * (TestReservations.GB)), app_0.getHeadroom().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_0.getAllocatedResource().getMemorySize());
        Assert.assertEquals((3 * (TestReservations.GB)), node_1.getAllocatedResource().getMemorySize());
        Assert.assertEquals((5 * (TestReservations.GB)), node_2.getAllocatedResource().getMemorySize());
    }
}

