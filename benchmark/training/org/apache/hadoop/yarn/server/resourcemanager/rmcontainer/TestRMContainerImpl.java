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
package org.apache.hadoop.yarn.server.resourcemanager.rmcontainer;


import ContainerExitStatus.ABORTED;
import ContainerState.COMPLETE;
import RMAppAttemptEventType.CONTAINER_FINISHED;
import RMContainerState.ACQUIRED;
import RMContainerState.ALLOCATED;
import RMContainerState.COMPLETED;
import RMContainerState.NEW;
import RMContainerState.RELEASED;
import RMContainerState.RUNNING;
import SchedulerUtils.EXPIRED_CONTAINER;
import SchedulerUtils.RELEASED_CONTAINER;
import YarnConfiguration.APPLICATION_HISTORY_SAVE_NON_AM_CONTAINER_META_INFO;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.ahs.RMApplicationHistoryWriter;
import org.apache.hadoop.yarn.server.resourcemanager.metrics.SystemMetricsPublisher;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.TestUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint.AllocationTags;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint.AllocationTagsManager;
import org.apache.hadoop.yarn.server.scheduler.SchedulerRequestKey;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static RMContainerEventType.ACQUIRED;
import static RMContainerEventType.EXPIRE;
import static RMContainerEventType.FINISHED;
import static RMContainerEventType.KILL;
import static RMContainerEventType.LAUNCHED;
import static RMContainerEventType.RELEASED;
import static RMContainerEventType.START;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class TestRMContainerImpl {
    @Test
    public void testReleaseWhileRunning() {
        DrainDispatcher drainDispatcher = new DrainDispatcher();
        EventHandler<RMAppAttemptEvent> appAttemptEventHandler = Mockito.mock(EventHandler.class);
        EventHandler generic = Mockito.mock(EventHandler.class);
        drainDispatcher.register(RMAppAttemptEventType.class, appAttemptEventHandler);
        drainDispatcher.register(RMNodeEventType.class, generic);
        drainDispatcher.init(new YarnConfiguration());
        drainDispatcher.start();
        NodeId nodeId = BuilderUtils.newNodeId("host", 3425);
        ApplicationId appId = BuilderUtils.newApplicationId(1, 1);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(appId, 1);
        ContainerId containerId = BuilderUtils.newContainerId(appAttemptId, 1);
        ContainerAllocationExpirer expirer = Mockito.mock(ContainerAllocationExpirer.class);
        Resource resource = BuilderUtils.newResource(512, 1);
        Priority priority = BuilderUtils.newPriority(5);
        Container container = BuilderUtils.newContainer(containerId, nodeId, "host:3465", resource, priority, null);
        ConcurrentMap<ApplicationId, RMApp> rmApps = Mockito.spy(new ConcurrentHashMap<ApplicationId, RMApp>());
        RMApp rmApp = Mockito.mock(RMApp.class);
        Mockito.when(rmApp.getRMAppAttempt(ArgumentMatchers.any())).thenReturn(null);
        Mockito.doReturn(rmApp).when(rmApps).get(ArgumentMatchers.<ApplicationId>any());
        RMApplicationHistoryWriter writer = Mockito.mock(RMApplicationHistoryWriter.class);
        SystemMetricsPublisher publisher = Mockito.mock(SystemMetricsPublisher.class);
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getDispatcher()).thenReturn(drainDispatcher);
        Mockito.when(rmContext.getContainerAllocationExpirer()).thenReturn(expirer);
        Mockito.when(rmContext.getRMApplicationHistoryWriter()).thenReturn(writer);
        Mockito.when(rmContext.getRMApps()).thenReturn(rmApps);
        Mockito.when(rmContext.getSystemMetricsPublisher()).thenReturn(publisher);
        AllocationTagsManager ptm = Mockito.mock(AllocationTagsManager.class);
        Mockito.when(rmContext.getAllocationTagsManager()).thenReturn(ptm);
        YarnConfiguration conf = new YarnConfiguration();
        conf.setBoolean(APPLICATION_HISTORY_SAVE_NON_AM_CONTAINER_META_INFO, true);
        Mockito.when(rmContext.getYarnConfiguration()).thenReturn(conf);
        RMContainer rmContainer = new RMContainerImpl(container, SchedulerRequestKey.extractFrom(container), appAttemptId, nodeId, "user", rmContext);
        Assert.assertEquals(NEW, rmContainer.getState());
        Assert.assertEquals(resource, rmContainer.getAllocatedResource());
        Assert.assertEquals(nodeId, rmContainer.getAllocatedNode());
        Assert.assertEquals(priority, rmContainer.getAllocatedSchedulerKey().getPriority());
        Mockito.verify(writer).containerStarted(ArgumentMatchers.any(RMContainer.class));
        rmContainer.handle(new RMContainerEvent(containerId, START));
        drainDispatcher.await();
        Assert.assertEquals(ALLOCATED, rmContainer.getState());
        rmContainer.handle(new RMContainerEvent(containerId, ACQUIRED));
        drainDispatcher.await();
        Assert.assertEquals(ACQUIRED, rmContainer.getState());
        rmContainer.handle(new RMContainerEvent(containerId, LAUNCHED));
        drainDispatcher.await();
        Assert.assertEquals(RUNNING, rmContainer.getState());
        Mockito.verify(publisher, Mockito.times(2)).containerCreated(ArgumentMatchers.any(RMContainer.class), ArgumentMatchers.anyLong());
        Assert.assertEquals("http://host:3465/node/containerlogs/container_1_0001_01_000001/user", rmContainer.getLogURL());
        // In RUNNING state. Verify RELEASED and associated actions.
        Mockito.reset(appAttemptEventHandler);
        ContainerStatus containerStatus = SchedulerUtils.createAbnormalContainerStatus(containerId, RELEASED_CONTAINER);
        rmContainer.handle(new RMContainerFinishedEvent(containerId, containerStatus, RELEASED));
        drainDispatcher.await();
        Assert.assertEquals(RELEASED, rmContainer.getState());
        Assert.assertEquals(RELEASED_CONTAINER, rmContainer.getDiagnosticsInfo());
        Assert.assertEquals(ABORTED, rmContainer.getContainerExitStatus());
        Assert.assertEquals(COMPLETE, rmContainer.getContainerState());
        Mockito.verify(writer).containerFinished(ArgumentMatchers.any(RMContainer.class));
        Mockito.verify(publisher).containerFinished(ArgumentMatchers.any(RMContainer.class), ArgumentMatchers.anyLong());
        ArgumentCaptor<RMAppAttemptContainerFinishedEvent> captor = ArgumentCaptor.forClass(RMAppAttemptContainerFinishedEvent.class);
        Mockito.verify(appAttemptEventHandler).handle(captor.capture());
        RMAppAttemptContainerFinishedEvent cfEvent = captor.getValue();
        Assert.assertEquals(appAttemptId, cfEvent.getApplicationAttemptId());
        Assert.assertEquals(containerStatus, cfEvent.getContainerStatus());
        Assert.assertEquals(CONTAINER_FINISHED, cfEvent.getType());
        // In RELEASED state. A FINISHED event may come in.
        rmContainer.handle(new RMContainerFinishedEvent(containerId, SchedulerUtils.createAbnormalContainerStatus(containerId, "FinishedContainer"), FINISHED));
        Assert.assertEquals(RELEASED, rmContainer.getState());
    }

    @Test
    public void testExpireWhileRunning() {
        DrainDispatcher drainDispatcher = new DrainDispatcher();
        EventHandler<RMAppAttemptEvent> appAttemptEventHandler = Mockito.mock(EventHandler.class);
        EventHandler generic = Mockito.mock(EventHandler.class);
        drainDispatcher.register(RMAppAttemptEventType.class, appAttemptEventHandler);
        drainDispatcher.register(RMNodeEventType.class, generic);
        drainDispatcher.init(new YarnConfiguration());
        drainDispatcher.start();
        NodeId nodeId = BuilderUtils.newNodeId("host", 3425);
        ApplicationId appId = BuilderUtils.newApplicationId(1, 1);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(appId, 1);
        ContainerId containerId = BuilderUtils.newContainerId(appAttemptId, 1);
        ContainerAllocationExpirer expirer = Mockito.mock(ContainerAllocationExpirer.class);
        Resource resource = BuilderUtils.newResource(512, 1);
        Priority priority = BuilderUtils.newPriority(5);
        Container container = BuilderUtils.newContainer(containerId, nodeId, "host:3465", resource, priority, null);
        ConcurrentMap<ApplicationId, RMApp> appMap = new ConcurrentHashMap<>();
        RMApp rmApp = Mockito.mock(RMApp.class);
        appMap.putIfAbsent(appId, rmApp);
        RMApplicationHistoryWriter writer = Mockito.mock(RMApplicationHistoryWriter.class);
        SystemMetricsPublisher publisher = Mockito.mock(SystemMetricsPublisher.class);
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getDispatcher()).thenReturn(drainDispatcher);
        Mockito.when(rmContext.getContainerAllocationExpirer()).thenReturn(expirer);
        Mockito.when(rmContext.getRMApplicationHistoryWriter()).thenReturn(writer);
        Mockito.when(rmContext.getSystemMetricsPublisher()).thenReturn(publisher);
        AllocationTagsManager ptm = Mockito.mock(AllocationTagsManager.class);
        Mockito.when(rmContext.getAllocationTagsManager()).thenReturn(ptm);
        YarnConfiguration conf = new YarnConfiguration();
        conf.setBoolean(APPLICATION_HISTORY_SAVE_NON_AM_CONTAINER_META_INFO, true);
        Mockito.when(rmContext.getYarnConfiguration()).thenReturn(conf);
        Mockito.when(rmContext.getRMApps()).thenReturn(appMap);
        RMContainer rmContainer = new RMContainerImpl(container, SchedulerRequestKey.extractFrom(container), appAttemptId, nodeId, "user", rmContext);
        Assert.assertEquals(NEW, rmContainer.getState());
        Assert.assertEquals(resource, rmContainer.getAllocatedResource());
        Assert.assertEquals(nodeId, rmContainer.getAllocatedNode());
        Assert.assertEquals(priority, rmContainer.getAllocatedSchedulerKey().getPriority());
        Mockito.verify(writer).containerStarted(ArgumentMatchers.any(RMContainer.class));
        rmContainer.handle(new RMContainerEvent(containerId, START));
        drainDispatcher.await();
        Assert.assertEquals(ALLOCATED, rmContainer.getState());
        Mockito.verify(publisher).containerCreated(ArgumentMatchers.any(RMContainer.class), ArgumentMatchers.anyLong());
        rmContainer.handle(new RMContainerEvent(containerId, ACQUIRED));
        drainDispatcher.await();
        Assert.assertEquals(ACQUIRED, rmContainer.getState());
        Mockito.verify(publisher, Mockito.times(2)).containerCreated(ArgumentMatchers.any(RMContainer.class), ArgumentMatchers.anyLong());
        rmContainer.handle(new RMContainerEvent(containerId, LAUNCHED));
        drainDispatcher.await();
        Assert.assertEquals(RUNNING, rmContainer.getState());
        Assert.assertEquals("http://host:3465/node/containerlogs/container_1_0001_01_000001/user", rmContainer.getLogURL());
        // In RUNNING state. Verify EXPIRE and associated actions.
        Mockito.reset(appAttemptEventHandler);
        ContainerStatus containerStatus = SchedulerUtils.createAbnormalContainerStatus(containerId, EXPIRED_CONTAINER);
        rmContainer.handle(new RMContainerFinishedEvent(containerId, containerStatus, EXPIRE));
        drainDispatcher.await();
        Assert.assertEquals(RUNNING, rmContainer.getState());
        Mockito.verify(writer, Mockito.never()).containerFinished(ArgumentMatchers.any(RMContainer.class));
        Mockito.verify(publisher, Mockito.never()).containerFinished(ArgumentMatchers.any(RMContainer.class), ArgumentMatchers.anyLong());
    }

    @Test
    public void testExistenceOfResourceRequestInRMContainer() throws Exception {
        Configuration conf = new Configuration();
        MockRM rm1 = new MockRM(conf);
        start();
        MockNM nm1 = rm1.registerNode("unknownhost:1234", 8000);
        RMApp app1 = rm1.submitApp(1024);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        ResourceScheduler scheduler = getResourceScheduler();
        // request a container.
        am1.allocate("127.0.0.1", 1024, 1, new ArrayList<ContainerId>());
        ContainerId containerId2 = ContainerId.newContainerId(am1.getApplicationAttemptId(), 2);
        rm1.waitForState(nm1, containerId2, ALLOCATED);
        // Verify whether list of ResourceRequest is present in RMContainer
        // while moving to ALLOCATED state
        Assert.assertNotNull(scheduler.getRMContainer(containerId2).getContainerRequest());
        // Allocate container
        am1.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
        rm1.waitForState(nm1, containerId2, ACQUIRED);
        // After RMContainer moving to ACQUIRED state, list of ResourceRequest will
        // be empty
        Assert.assertNull(scheduler.getRMContainer(containerId2).getContainerRequest());
    }

    @Test(timeout = 180000)
    public void testStoreAllContainerMetrics() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(RM_AM_MAX_ATTEMPTS, 1);
        conf.setBoolean(APPLICATION_HISTORY_SAVE_NON_AM_CONTAINER_META_INFO, true);
        MockRM rm1 = new MockRM(conf);
        SystemMetricsPublisher publisher = Mockito.mock(SystemMetricsPublisher.class);
        getRMContext().setSystemMetricsPublisher(publisher);
        start();
        MockNM nm1 = rm1.registerNode("unknownhost:1234", 8000);
        RMApp app1 = rm1.submitApp(1024);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 1, ContainerState.RUNNING);
        // request a container.
        am1.allocate("127.0.0.1", 1024, 1, new ArrayList<ContainerId>());
        ContainerId containerId2 = ContainerId.newContainerId(am1.getApplicationAttemptId(), 2);
        rm1.waitForState(nm1, containerId2, ALLOCATED);
        am1.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
        rm1.waitForState(nm1, containerId2, ACQUIRED);
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 2, ContainerState.RUNNING);
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 2, COMPLETE);
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 1, COMPLETE);
        rm1.waitForState(nm1, containerId2, COMPLETED);
        stop();
        // RMContainer should be publishing system metrics for all containers.
        // Since there is 1 AM container and 1 non-AM container, there should be 2
        // container created events and 2 container finished events.
        Mockito.verify(publisher, Mockito.times(4)).containerCreated(ArgumentMatchers.any(RMContainer.class), ArgumentMatchers.anyLong());
        Mockito.verify(publisher, Mockito.times(2)).containerFinished(ArgumentMatchers.any(RMContainer.class), ArgumentMatchers.anyLong());
    }

    @Test(timeout = 180000)
    public void testStoreOnlyAMContainerMetrics() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(RM_AM_MAX_ATTEMPTS, 1);
        conf.setBoolean(APPLICATION_HISTORY_SAVE_NON_AM_CONTAINER_META_INFO, false);
        MockRM rm1 = new MockRM(conf);
        SystemMetricsPublisher publisher = Mockito.mock(SystemMetricsPublisher.class);
        getRMContext().setSystemMetricsPublisher(publisher);
        start();
        MockNM nm1 = rm1.registerNode("unknownhost:1234", 8000);
        RMApp app1 = rm1.submitApp(1024);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 1, ContainerState.RUNNING);
        // request a container.
        am1.allocate("127.0.0.1", 1024, 1, new ArrayList<ContainerId>());
        ContainerId containerId2 = ContainerId.newContainerId(am1.getApplicationAttemptId(), 2);
        rm1.waitForState(nm1, containerId2, ALLOCATED);
        am1.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
        rm1.waitForState(nm1, containerId2, ACQUIRED);
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 2, ContainerState.RUNNING);
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 2, COMPLETE);
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 1, COMPLETE);
        rm1.waitForState(nm1, containerId2, COMPLETED);
        stop();
        // RMContainer should be publishing system metrics only for AM container.
        Mockito.verify(publisher, Mockito.times(1)).containerCreated(ArgumentMatchers.any(RMContainer.class), ArgumentMatchers.anyLong());
        Mockito.verify(publisher, Mockito.times(1)).containerFinished(ArgumentMatchers.any(RMContainer.class), ArgumentMatchers.anyLong());
    }

    @Test
    public void testContainerTransitionNotifyAllocationTagsManager() throws Exception {
        DrainDispatcher drainDispatcher = new DrainDispatcher();
        EventHandler<RMAppAttemptEvent> appAttemptEventHandler = Mockito.mock(EventHandler.class);
        EventHandler generic = Mockito.mock(EventHandler.class);
        drainDispatcher.register(RMAppAttemptEventType.class, appAttemptEventHandler);
        drainDispatcher.register(RMNodeEventType.class, generic);
        drainDispatcher.init(new YarnConfiguration());
        drainDispatcher.start();
        NodeId nodeId = BuilderUtils.newNodeId("host", 3425);
        ApplicationId appId = BuilderUtils.newApplicationId(1, 1);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(appId, 1);
        ContainerId containerId = BuilderUtils.newContainerId(appAttemptId, 1);
        ContainerAllocationExpirer expirer = Mockito.mock(ContainerAllocationExpirer.class);
        Resource resource = BuilderUtils.newResource(512, 1);
        Priority priority = BuilderUtils.newPriority(5);
        Container container = BuilderUtils.newContainer(containerId, nodeId, "host:3465", resource, priority, null);
        container.setAllocationTags(ImmutableSet.of("mapper"));
        ConcurrentMap<ApplicationId, RMApp> rmApps = Mockito.spy(new ConcurrentHashMap<ApplicationId, RMApp>());
        RMApp rmApp = Mockito.mock(RMApp.class);
        Mockito.when(rmApp.getRMAppAttempt(ArgumentMatchers.any())).thenReturn(null);
        Mockito.doReturn(rmApp).when(rmApps).get(ArgumentMatchers.<ApplicationId>any());
        RMApplicationHistoryWriter writer = Mockito.mock(RMApplicationHistoryWriter.class);
        SystemMetricsPublisher publisher = Mockito.mock(SystemMetricsPublisher.class);
        RMContext rmContext = Mockito.mock(RMContext.class);
        AllocationTagsManager tagsManager = new AllocationTagsManager(rmContext);
        Mockito.when(rmContext.getDispatcher()).thenReturn(drainDispatcher);
        Mockito.when(rmContext.getContainerAllocationExpirer()).thenReturn(expirer);
        Mockito.when(rmContext.getRMApplicationHistoryWriter()).thenReturn(writer);
        Mockito.when(rmContext.getRMApps()).thenReturn(rmApps);
        Mockito.when(rmContext.getSystemMetricsPublisher()).thenReturn(publisher);
        Mockito.when(rmContext.getAllocationTagsManager()).thenReturn(tagsManager);
        YarnConfiguration conf = new YarnConfiguration();
        conf.setBoolean(APPLICATION_HISTORY_SAVE_NON_AM_CONTAINER_META_INFO, true);
        Mockito.when(rmContext.getYarnConfiguration()).thenReturn(conf);
        RMNode rmNode = new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeImpl(nodeId, rmContext, "localhost", 0, 0, null, Resource.newInstance(10240, 10), null);
        SchedulerNode schedulerNode = new org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode(rmNode, false);
        /* First container: ALLOCATED -> KILLED */
        RMContainerImpl rmContainer = new RMContainerImpl(container, SchedulerRequestKey.extractFrom(container), appAttemptId, nodeId, "user", rmContext);
        Assert.assertEquals(0, tagsManager.getNodeCardinalityByOp(nodeId, AllocationTags.createSingleAppAllocationTags(TestUtils.getMockApplicationId(1), null), Long::max));
        rmContainer.handle(new RMContainerEvent(containerId, START));
        schedulerNode.allocateContainer(rmContainer);
        Assert.assertEquals(1, tagsManager.getNodeCardinalityByOp(nodeId, AllocationTags.createSingleAppAllocationTags(appId, null), Long::max));
        rmContainer.handle(new RMContainerFinishedEvent(containerId, ContainerStatus.newInstance(containerId, COMPLETE, "", 0), KILL));
        schedulerNode.releaseContainer(container.getId(), true);
        Assert.assertEquals(0, tagsManager.getNodeCardinalityByOp(nodeId, AllocationTags.createSingleAppAllocationTags(appId, null), Long::max));
        /* Second container: ACQUIRED -> FINISHED */
        rmContainer = new RMContainerImpl(container, SchedulerRequestKey.extractFrom(container), appAttemptId, nodeId, "user", rmContext);
        Assert.assertEquals(0, tagsManager.getNodeCardinalityByOp(nodeId, AllocationTags.createSingleAppAllocationTags(appId, null), Long::max));
        rmContainer.setAllocationTags(ImmutableSet.of("mapper"));
        rmContainer.handle(new RMContainerEvent(containerId, START));
        schedulerNode.allocateContainer(rmContainer);
        Assert.assertEquals(1, tagsManager.getNodeCardinalityByOp(nodeId, AllocationTags.createSingleAppAllocationTags(appId, null), Long::max));
        rmContainer.handle(new RMContainerEvent(containerId, ACQUIRED));
        rmContainer.handle(new RMContainerFinishedEvent(containerId, ContainerStatus.newInstance(containerId, COMPLETE, "", 0), FINISHED));
        schedulerNode.releaseContainer(container.getId(), true);
        Assert.assertEquals(0, tagsManager.getNodeCardinalityByOp(nodeId, AllocationTags.createSingleAppAllocationTags(appId, null), Long::max));
        /* Third container: RUNNING -> FINISHED */
        rmContainer = new RMContainerImpl(container, SchedulerRequestKey.extractFrom(container), appAttemptId, nodeId, "user", rmContext);
        rmContainer.setAllocationTags(ImmutableSet.of("mapper"));
        Assert.assertEquals(0, tagsManager.getNodeCardinalityByOp(nodeId, AllocationTags.createSingleAppAllocationTags(appId, null), Long::max));
        rmContainer.handle(new RMContainerEvent(containerId, START));
        schedulerNode.allocateContainer(rmContainer);
        Assert.assertEquals(1, tagsManager.getNodeCardinalityByOp(nodeId, AllocationTags.createSingleAppAllocationTags(appId, null), Long::max));
        rmContainer.handle(new RMContainerEvent(containerId, ACQUIRED));
        rmContainer.handle(new RMContainerEvent(containerId, LAUNCHED));
        rmContainer.handle(new RMContainerFinishedEvent(containerId, ContainerStatus.newInstance(containerId, COMPLETE, "", 0), FINISHED));
        schedulerNode.releaseContainer(container.getId(), true);
        Assert.assertEquals(0, tagsManager.getNodeCardinalityByOp(nodeId, AllocationTags.createSingleAppAllocationTags(appId, null), Long::max));
        /* Fourth container: NEW -> RECOVERED */
        rmContainer = new RMContainerImpl(container, SchedulerRequestKey.extractFrom(container), appAttemptId, nodeId, "user", rmContext);
        rmContainer.setAllocationTags(ImmutableSet.of("mapper"));
        Assert.assertEquals(0, tagsManager.getNodeCardinalityByOp(nodeId, AllocationTags.createSingleAppAllocationTags(appId, null), Long::max));
        NMContainerStatus containerStatus = NMContainerStatus.newInstance(containerId, 0, ContainerState.NEW, Resource.newInstance(1024, 1), "recover container", 0, Priority.newInstance(0), 0);
        containerStatus.setAllocationTags(ImmutableSet.of("mapper"));
        rmContainer.handle(new RMContainerRecoverEvent(containerId, containerStatus));
        Assert.assertEquals(1, tagsManager.getNodeCardinalityByOp(nodeId, AllocationTags.createSingleAppAllocationTags(appId, null), Long::max));
    }

    @Test(timeout = 30000)
    public void testContainerAcquiredAtKilled() {
        DrainDispatcher drainDispatcher = new DrainDispatcher();
        EventHandler<RMAppAttemptEvent> appAttemptEventHandler = Mockito.mock(EventHandler.class);
        EventHandler generic = Mockito.mock(EventHandler.class);
        drainDispatcher.register(RMAppAttemptEventType.class, appAttemptEventHandler);
        drainDispatcher.register(RMNodeEventType.class, generic);
        drainDispatcher.init(new YarnConfiguration());
        drainDispatcher.start();
        NodeId nodeId = BuilderUtils.newNodeId("host", 3425);
        ApplicationId appId = BuilderUtils.newApplicationId(1, 1);
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(appId, 1);
        ContainerId containerId = BuilderUtils.newContainerId(appAttemptId, 1);
        ContainerAllocationExpirer expirer = Mockito.mock(ContainerAllocationExpirer.class);
        Resource resource = BuilderUtils.newResource(512, 1);
        Priority priority = BuilderUtils.newPriority(5);
        Container container = BuilderUtils.newContainer(containerId, nodeId, "host:3465", resource, priority, null);
        ConcurrentMap<ApplicationId, RMApp> appMap = new ConcurrentHashMap<>();
        RMApp rmApp = Mockito.mock(RMApp.class);
        appMap.putIfAbsent(appId, rmApp);
        RMApplicationHistoryWriter writer = Mockito.mock(RMApplicationHistoryWriter.class);
        SystemMetricsPublisher publisher = Mockito.mock(SystemMetricsPublisher.class);
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getDispatcher()).thenReturn(drainDispatcher);
        Mockito.when(rmContext.getContainerAllocationExpirer()).thenReturn(expirer);
        Mockito.when(rmContext.getRMApplicationHistoryWriter()).thenReturn(writer);
        Mockito.when(rmContext.getSystemMetricsPublisher()).thenReturn(publisher);
        AllocationTagsManager ptm = Mockito.mock(AllocationTagsManager.class);
        Mockito.when(rmContext.getAllocationTagsManager()).thenReturn(ptm);
        YarnConfiguration conf = new YarnConfiguration();
        conf.setBoolean(APPLICATION_HISTORY_SAVE_NON_AM_CONTAINER_META_INFO, true);
        Mockito.when(rmContext.getYarnConfiguration()).thenReturn(conf);
        Mockito.when(rmContext.getRMApps()).thenReturn(appMap);
        RMContainer rmContainer = new RMContainerImpl(container, SchedulerRequestKey.extractFrom(container), appAttemptId, nodeId, "user", rmContext) {
            @Override
            protected void onInvalidStateTransition(RMContainerEventType rmContainerEventType, RMContainerState state) {
                Assert.fail(((("RMContainerImpl: can't handle " + rmContainerEventType) + " at state ") + state));
            }
        };
        rmContainer.handle(new RMContainerEvent(containerId, KILL));
        rmContainer.handle(new RMContainerEvent(containerId, ACQUIRED));
    }
}

