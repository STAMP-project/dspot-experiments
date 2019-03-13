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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;


import NodeType.NODE_LOCAL;
import NodeType.OFF_SWITCH;
import NodeType.RACK_LOCAL;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.MockNodes;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.TestUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies.DominantResourceFairnessPolicy;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies.FairSharePolicy;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.policies.FifoPolicy;
import org.apache.hadoop.yarn.server.scheduler.SchedulerRequestKey;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestFSAppAttempt extends FairSchedulerTestBase {
    @Test
    public void testDelayScheduling() {
        FSLeafQueue queue = Mockito.mock(FSLeafQueue.class);
        Priority pri = Mockito.mock(Priority.class);
        SchedulerRequestKey prio = TestUtils.toSchedulerKey(pri);
        Mockito.when(pri.getPriority()).thenReturn(1);
        double nodeLocalityThreshold = 0.5;
        double rackLocalityThreshold = 0.6;
        ApplicationAttemptId applicationAttemptId = createAppAttemptId(1, 1);
        RMContext rmContext = resourceManager.getRMContext();
        FSAppAttempt schedulerApp = new FSAppAttempt(scheduler, applicationAttemptId, "user1", queue, null, rmContext);
        // Default level should be node-local
        Assert.assertEquals(NODE_LOCAL, schedulerApp.getAllowedLocalityLevel(prio, 10, nodeLocalityThreshold, rackLocalityThreshold));
        // First five scheduling opportunities should remain node local
        for (int i = 0; i < 5; i++) {
            schedulerApp.addSchedulingOpportunity(prio);
            Assert.assertEquals(NODE_LOCAL, schedulerApp.getAllowedLocalityLevel(prio, 10, nodeLocalityThreshold, rackLocalityThreshold));
        }
        // After five it should switch to rack local
        schedulerApp.addSchedulingOpportunity(prio);
        Assert.assertEquals(RACK_LOCAL, schedulerApp.getAllowedLocalityLevel(prio, 10, nodeLocalityThreshold, rackLocalityThreshold));
        // Manually set back to node local
        schedulerApp.resetAllowedLocalityLevel(prio, NODE_LOCAL);
        schedulerApp.resetSchedulingOpportunities(prio);
        Assert.assertEquals(NODE_LOCAL, schedulerApp.getAllowedLocalityLevel(prio, 10, nodeLocalityThreshold, rackLocalityThreshold));
        // Now escalate again to rack-local, then to off-switch
        for (int i = 0; i < 5; i++) {
            schedulerApp.addSchedulingOpportunity(prio);
            Assert.assertEquals(NODE_LOCAL, schedulerApp.getAllowedLocalityLevel(prio, 10, nodeLocalityThreshold, rackLocalityThreshold));
        }
        schedulerApp.addSchedulingOpportunity(prio);
        Assert.assertEquals(RACK_LOCAL, schedulerApp.getAllowedLocalityLevel(prio, 10, nodeLocalityThreshold, rackLocalityThreshold));
        for (int i = 0; i < 6; i++) {
            schedulerApp.addSchedulingOpportunity(prio);
            Assert.assertEquals(RACK_LOCAL, schedulerApp.getAllowedLocalityLevel(prio, 10, nodeLocalityThreshold, rackLocalityThreshold));
        }
        schedulerApp.addSchedulingOpportunity(prio);
        Assert.assertEquals(OFF_SWITCH, schedulerApp.getAllowedLocalityLevel(prio, 10, nodeLocalityThreshold, rackLocalityThreshold));
    }

    @Test
    public void testDelaySchedulingForContinuousScheduling() throws InterruptedException {
        FSLeafQueue queue = scheduler.getQueueManager().getLeafQueue("queue", true);
        Priority pri = Mockito.mock(Priority.class);
        SchedulerRequestKey prio = TestUtils.toSchedulerKey(pri);
        Mockito.when(pri.getPriority()).thenReturn(1);
        ControlledClock clock = new ControlledClock();
        scheduler.setClock(clock);
        long nodeLocalityDelayMs = 5 * 1000L;// 5 seconds

        long rackLocalityDelayMs = 6 * 1000L;// 6 seconds

        RMContext rmContext = resourceManager.getRMContext();
        ApplicationAttemptId applicationAttemptId = createAppAttemptId(1, 1);
        FSAppAttempt schedulerApp = new FSAppAttempt(scheduler, applicationAttemptId, "user1", queue, null, rmContext);
        // Default level should be node-local
        Assert.assertEquals(NODE_LOCAL, schedulerApp.getAllowedLocalityLevelByTime(prio, nodeLocalityDelayMs, rackLocalityDelayMs, clock.getTime()));
        // after 4 seconds should remain node local
        clock.tickSec(4);
        Assert.assertEquals(NODE_LOCAL, schedulerApp.getAllowedLocalityLevelByTime(prio, nodeLocalityDelayMs, rackLocalityDelayMs, clock.getTime()));
        // after 6 seconds should switch to rack local
        clock.tickSec(2);
        Assert.assertEquals(RACK_LOCAL, schedulerApp.getAllowedLocalityLevelByTime(prio, nodeLocalityDelayMs, rackLocalityDelayMs, clock.getTime()));
        // manually set back to node local
        schedulerApp.resetAllowedLocalityLevel(prio, NODE_LOCAL);
        schedulerApp.resetSchedulingOpportunities(prio, clock.getTime());
        Assert.assertEquals(NODE_LOCAL, schedulerApp.getAllowedLocalityLevelByTime(prio, nodeLocalityDelayMs, rackLocalityDelayMs, clock.getTime()));
        // Now escalate again to rack-local, then to off-switch
        clock.tickSec(6);
        Assert.assertEquals(RACK_LOCAL, schedulerApp.getAllowedLocalityLevelByTime(prio, nodeLocalityDelayMs, rackLocalityDelayMs, clock.getTime()));
        clock.tickSec(7);
        Assert.assertEquals(OFF_SWITCH, schedulerApp.getAllowedLocalityLevelByTime(prio, nodeLocalityDelayMs, rackLocalityDelayMs, clock.getTime()));
    }

    /**
     * Ensure that when negative paramaters are given (signaling delay scheduling
     * no tin use), the least restrictive locality level is returned.
     */
    @Test
    public void testLocalityLevelWithoutDelays() {
        FSLeafQueue queue = Mockito.mock(FSLeafQueue.class);
        Priority pri = Mockito.mock(Priority.class);
        SchedulerRequestKey prio = TestUtils.toSchedulerKey(pri);
        Mockito.when(pri.getPriority()).thenReturn(1);
        RMContext rmContext = resourceManager.getRMContext();
        ApplicationAttemptId applicationAttemptId = createAppAttemptId(1, 1);
        FSAppAttempt schedulerApp = new FSAppAttempt(scheduler, applicationAttemptId, "user1", queue, null, rmContext);
        Assert.assertEquals(OFF_SWITCH, schedulerApp.getAllowedLocalityLevel(prio, 10, (-1.0), (-1.0)));
    }

    @Test
    public void testHeadroom() {
        final FairScheduler mockScheduler = Mockito.mock(FairScheduler.class);
        Mockito.when(mockScheduler.getClock()).thenReturn(scheduler.getClock());
        final FSLeafQueue mockQueue = Mockito.mock(FSLeafQueue.class);
        final Resource queueMaxResources = Resource.newInstance((5 * 1024), 3);
        final Resource queueFairShare = Resources.createResource(4096, 2);
        final Resource queueUsage = Resource.newInstance(2048, 2);
        final Resource queueStarvation = Resources.subtract(queueFairShare, queueUsage);
        final Resource queueMaxResourcesAvailable = Resources.subtract(queueMaxResources, queueUsage);
        final Resource clusterResource = Resources.createResource(8192, 8);
        final Resource clusterUsage = Resources.createResource(2048, 2);
        final Resource clusterAvailable = Resources.subtract(clusterResource, clusterUsage);
        final QueueMetrics fakeRootQueueMetrics = Mockito.mock(QueueMetrics.class);
        Mockito.when(mockQueue.getMaxShare()).thenReturn(queueMaxResources);
        Mockito.when(mockQueue.getFairShare()).thenReturn(queueFairShare);
        Mockito.when(mockQueue.getResourceUsage()).thenReturn(queueUsage);
        Mockito.when(mockScheduler.getClusterResource()).thenReturn(clusterResource);
        Mockito.when(fakeRootQueueMetrics.getAllocatedResources()).thenReturn(clusterUsage);
        Mockito.when(mockScheduler.getRootQueueMetrics()).thenReturn(fakeRootQueueMetrics);
        ApplicationAttemptId applicationAttemptId = createAppAttemptId(1, 1);
        RMContext rmContext = resourceManager.getRMContext();
        FSAppAttempt schedulerApp = new FSAppAttempt(mockScheduler, applicationAttemptId, "user1", mockQueue, null, rmContext);
        // Min of Memory and CPU across cluster and queue is used in
        // DominantResourceFairnessPolicy
        Mockito.when(mockQueue.getPolicy()).thenReturn(SchedulingPolicy.getInstance(DominantResourceFairnessPolicy.class));
        verifyHeadroom(schedulerApp, TestFSAppAttempt.min(queueStarvation.getMemorySize(), clusterAvailable.getMemorySize(), queueMaxResourcesAvailable.getMemorySize()), TestFSAppAttempt.min(queueStarvation.getVirtualCores(), clusterAvailable.getVirtualCores(), queueMaxResourcesAvailable.getVirtualCores()));
        // Fair and Fifo ignore CPU of queue, so use cluster available CPU
        Mockito.when(mockQueue.getPolicy()).thenReturn(SchedulingPolicy.getInstance(FairSharePolicy.class));
        verifyHeadroom(schedulerApp, TestFSAppAttempt.min(queueStarvation.getMemorySize(), clusterAvailable.getMemorySize(), queueMaxResourcesAvailable.getMemorySize()), Math.min(clusterAvailable.getVirtualCores(), queueMaxResourcesAvailable.getVirtualCores()));
        Mockito.when(mockQueue.getPolicy()).thenReturn(SchedulingPolicy.getInstance(FifoPolicy.class));
        verifyHeadroom(schedulerApp, TestFSAppAttempt.min(queueStarvation.getMemorySize(), clusterAvailable.getMemorySize(), queueMaxResourcesAvailable.getMemorySize()), Math.min(clusterAvailable.getVirtualCores(), queueMaxResourcesAvailable.getVirtualCores()));
    }

    @Test
    public void testHeadroomWithBlackListedNodes() {
        // Add two nodes
        RMNode node1 = MockNodes.newNodeInfo(1, Resources.createResource((8 * 1024), 8), 1, "127.0.0.1");
        NodeAddedSchedulerEvent nodeEvent1 = new NodeAddedSchedulerEvent(node1);
        scheduler.handle(nodeEvent1);
        RMNode node2 = MockNodes.newNodeInfo(1, Resources.createResource((4 * 1024), 4), 2, "127.0.0.2");
        NodeAddedSchedulerEvent nodeEvent2 = new NodeAddedSchedulerEvent(node2);
        scheduler.handle(nodeEvent2);
        Assert.assertEquals("We should have two alive nodes.", 2, scheduler.getNumClusterNodes());
        Resource clusterResource = scheduler.getClusterResource();
        Resource clusterUsage = scheduler.getRootQueueMetrics().getAllocatedResources();
        Assert.assertEquals((12 * 1024), clusterResource.getMemorySize());
        Assert.assertEquals(12, clusterResource.getVirtualCores());
        Assert.assertEquals(0, clusterUsage.getMemorySize());
        Assert.assertEquals(0, clusterUsage.getVirtualCores());
        ApplicationAttemptId id11 = createAppAttemptId(1, 1);
        createMockRMApp(id11);
        scheduler.addApplication(id11.getApplicationId(), "default", "user1", false);
        scheduler.addApplicationAttempt(id11, false, false);
        Assert.assertNotNull(scheduler.getSchedulerApplications().get(id11.getApplicationId()));
        FSAppAttempt app = scheduler.getSchedulerApp(id11);
        Assert.assertNotNull(app);
        Resource queueUsage = app.getQueue().getResourceUsage();
        Assert.assertEquals(0, queueUsage.getMemorySize());
        Assert.assertEquals(0, queueUsage.getVirtualCores());
        SchedulerNode n1 = scheduler.getSchedulerNode(node1.getNodeID());
        SchedulerNode n2 = scheduler.getSchedulerNode(node2.getNodeID());
        Assert.assertNotNull(n1);
        Assert.assertNotNull(n2);
        List<String> blacklistAdditions = new ArrayList<String>(1);
        List<String> blacklistRemovals = new ArrayList<String>(1);
        blacklistAdditions.add(n1.getNodeName());
        FSAppAttempt spyApp = Mockito.spy(app);
        Mockito.doReturn(false).when(spyApp).isWaitingForAMContainer();
        spyApp.updateBlacklist(blacklistAdditions, blacklistRemovals);
        spyApp.getQueue().setFairShare(clusterResource);
        Assert.assertTrue(spyApp.isPlaceBlacklisted(n1.getNodeName()));
        Assert.assertFalse(spyApp.isPlaceBlacklisted(n2.getNodeName()));
        Assert.assertEquals(n2.getUnallocatedResource(), spyApp.getHeadroom());
        blacklistAdditions.clear();
        blacklistAdditions.add(n2.getNodeName());
        blacklistRemovals.add(n1.getNodeName());
        spyApp.updateBlacklist(blacklistAdditions, blacklistRemovals);
        Assert.assertFalse(spyApp.isPlaceBlacklisted(n1.getNodeName()));
        Assert.assertTrue(spyApp.isPlaceBlacklisted(n2.getNodeName()));
        Assert.assertEquals(n1.getUnallocatedResource(), spyApp.getHeadroom());
        blacklistAdditions.clear();
        blacklistRemovals.clear();
        blacklistRemovals.add(n2.getNodeName());
        spyApp.updateBlacklist(blacklistAdditions, blacklistRemovals);
        Assert.assertFalse(spyApp.isPlaceBlacklisted(n1.getNodeName()));
        Assert.assertFalse(spyApp.isPlaceBlacklisted(n2.getNodeName()));
        Assert.assertEquals(clusterResource, spyApp.getHeadroom());
    }
}

