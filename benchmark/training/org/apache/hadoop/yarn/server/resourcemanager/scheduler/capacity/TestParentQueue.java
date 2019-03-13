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


import CapacitySchedulerConfiguration.ROOT;
import NodeType.NODE_LOCAL;
import NodeType.OFF_SWITCH;
import NodeType.RACK_LOCAL;
import QueueACL.ADMINISTER_QUEUE;
import QueueACL.SUBMIT_APPLICATIONS;
import SchedulingMode.RESPECT_PARTITION_EXCLUSIVITY;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.QueueUserACLInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.security.YarnAuthorizationProvider;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.placement.CandidateNodeSet;
import org.apache.hadoop.yarn.server.resourcemanager.security.AppPriorityACLsManager;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CapacitySchedulerConfiguration.ROOT;


public class TestParentQueue {
    private static final Resource QUEUE_B_RESOURCE = Resource.newInstance((14 * 1024), 22);

    private static final Resource QUEUE_A_RESOURCE = Resource.newInstance((6 * 1024), 10);

    private static final Logger LOG = LoggerFactory.getLogger(TestParentQueue.class);

    RMContext rmContext;

    YarnConfiguration conf;

    CapacitySchedulerConfiguration csConf;

    CapacitySchedulerContext csContext;

    static final int GB = 1024;

    static final String DEFAULT_RACK = "/default";

    private final ResourceCalculator resourceComparator = new DefaultResourceCalculator();

    private static final String A = "a";

    private static final String B = "b";

    static final float DELTA = 1.0E-4F;

    @Test
    public void testSingleLevelQueues() throws Exception {
        // Setup queue configs
        setupSingleLevelQueues(csConf);
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        CSQueue root = CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
        // Setup some nodes
        final int memoryPerNode = 10;
        final int coresPerNode = 16;
        final int numNodes = 2;
        FiCaSchedulerNode node_0 = TestUtils.getMockNode("host_0", TestParentQueue.DEFAULT_RACK, 0, (memoryPerNode * (TestParentQueue.GB)));
        FiCaSchedulerNode node_1 = TestUtils.getMockNode("host_1", TestParentQueue.DEFAULT_RACK, 0, (memoryPerNode * (TestParentQueue.GB)));
        final Resource clusterResource = Resources.createResource((numNodes * (memoryPerNode * (TestParentQueue.GB))), (numNodes * coresPerNode));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        // Start testing
        LeafQueue a = ((LeafQueue) (queues.get(TestParentQueue.A)));
        LeafQueue b = ((LeafQueue) (queues.get(TestParentQueue.B)));
        a.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        b.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        queues.get(ROOT).getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        // Simulate B returning a container on node_0
        stubQueueAllocation(a, clusterResource, node_0, (0 * (TestParentQueue.GB)));
        stubQueueAllocation(b, clusterResource, node_0, (1 * (TestParentQueue.GB)));
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        verifyQueueMetrics(a, (0 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (1 * (TestParentQueue.GB)), clusterResource);
        // Now, A should get the scheduling opportunity since A=0G/6G, B=1G/14G
        stubQueueAllocation(a, clusterResource, node_1, (2 * (TestParentQueue.GB)));
        stubQueueAllocation(b, clusterResource, node_1, (1 * (TestParentQueue.GB)));
        root.assignContainers(clusterResource, node_1, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        InOrder allocationOrder = Mockito.inOrder(a, b);
        allocationOrder.verify(a).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        root.assignContainers(clusterResource, node_1, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder.verify(b).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        verifyQueueMetrics(a, (2 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (2 * (TestParentQueue.GB)), clusterResource);
        // Now, B should get the scheduling opportunity
        // since A has 2/6G while B has 2/14G
        stubQueueAllocation(a, clusterResource, node_0, (1 * (TestParentQueue.GB)));
        stubQueueAllocation(b, clusterResource, node_0, (2 * (TestParentQueue.GB)));
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder = Mockito.inOrder(b, a);
        allocationOrder.verify(b).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        allocationOrder.verify(a).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        verifyQueueMetrics(a, (3 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (4 * (TestParentQueue.GB)), clusterResource);
        // Now, B should still get the scheduling opportunity
        // since A has 3/6G while B has 4/14G
        stubQueueAllocation(a, clusterResource, node_0, (0 * (TestParentQueue.GB)));
        stubQueueAllocation(b, clusterResource, node_0, (4 * (TestParentQueue.GB)));
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder = Mockito.inOrder(b, a);
        allocationOrder.verify(b).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        allocationOrder.verify(a).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        verifyQueueMetrics(a, (3 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (8 * (TestParentQueue.GB)), clusterResource);
        // Now, A should get the scheduling opportunity
        // since A has 3/6G while B has 8/14G
        stubQueueAllocation(a, clusterResource, node_1, (1 * (TestParentQueue.GB)));
        stubQueueAllocation(b, clusterResource, node_1, (1 * (TestParentQueue.GB)));
        root.assignContainers(clusterResource, node_1, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        root.assignContainers(clusterResource, node_1, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder = Mockito.inOrder(a, b);
        allocationOrder.verify(b).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        allocationOrder.verify(a).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        verifyQueueMetrics(a, (4 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (9 * (TestParentQueue.GB)), clusterResource);
    }

    @Test
    public void testSingleLevelQueuesPrecision() throws Exception {
        // Setup queue configs
        setupSingleLevelQueues(csConf);
        final String Q_A = ((ROOT) + ".") + "a";
        csConf.setCapacity(Q_A, 30);
        final String Q_B = ((ROOT) + ".") + "b";
        csConf.setCapacity(Q_B, 70.5F);
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        boolean exceptionOccurred = false;
        try {
            CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
        } catch (IllegalArgumentException ie) {
            exceptionOccurred = true;
        }
        if (!exceptionOccurred) {
            Assert.fail("Capacity is more then 100% so should be failed.");
        }
        csConf.setCapacity(Q_A, 30);
        csConf.setCapacity(Q_B, 70);
        exceptionOccurred = false;
        queues.clear();
        try {
            CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
        } catch (IllegalArgumentException ie) {
            exceptionOccurred = true;
        }
        if (exceptionOccurred) {
            Assert.fail("Capacity is 100% so should not be failed.");
        }
        csConf.setCapacity(Q_A, 30);
        csConf.setCapacity(Q_B, 70.005F);
        exceptionOccurred = false;
        queues.clear();
        try {
            CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
        } catch (IllegalArgumentException ie) {
            exceptionOccurred = true;
        }
        if (exceptionOccurred) {
            Assert.fail("Capacity is under PRECISION which is .05% so should not be failed.");
        }
    }

    private static final String C = "c";

    private static final String C1 = "c1";

    private static final String C11 = "c11";

    private static final String C111 = "c111";

    private static final String C1111 = "c1111";

    private static final String D = "d";

    private static final String A1 = "a1";

    private static final String A2 = "a2";

    private static final String B1 = "b1";

    private static final String B2 = "b2";

    private static final String B3 = "b3";

    @Test
    public void testMultiLevelQueues() throws Exception {
        /* Structure of queue:
                   Root
                  ____________
                 /    |   \   \
                A     B    C   D
              / |   / | \   \
             A1 A2 B1 B2 B3  C1
                               \
                                C11
                                  \
                                  C111
                                    \
                                     C1111
         */
        // Setup queue configs
        setupMultiLevelQueues(csConf);
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        CSQueue root = CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
        // Setup some nodes
        final int memoryPerNode = 10;
        final int coresPerNode = 16;
        final int numNodes = 3;
        FiCaSchedulerNode node_0 = TestUtils.getMockNode("host_0", TestParentQueue.DEFAULT_RACK, 0, (memoryPerNode * (TestParentQueue.GB)));
        FiCaSchedulerNode node_1 = TestUtils.getMockNode("host_1", TestParentQueue.DEFAULT_RACK, 0, (memoryPerNode * (TestParentQueue.GB)));
        FiCaSchedulerNode node_2 = TestUtils.getMockNode("host_2", TestParentQueue.DEFAULT_RACK, 0, (memoryPerNode * (TestParentQueue.GB)));
        final Resource clusterResource = Resources.createResource((numNodes * (memoryPerNode * (TestParentQueue.GB))), (numNodes * coresPerNode));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        // Start testing
        CSQueue a = queues.get(TestParentQueue.A);
        a.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        CSQueue b = queues.get(TestParentQueue.B);
        b.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        CSQueue c = queues.get(TestParentQueue.C);
        c.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        CSQueue d = queues.get(TestParentQueue.D);
        d.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        CSQueue a1 = queues.get(TestParentQueue.A1);
        a1.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        CSQueue a2 = queues.get(TestParentQueue.A2);
        a2.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        CSQueue b1 = queues.get(TestParentQueue.B1);
        b1.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        CSQueue b2 = queues.get(TestParentQueue.B2);
        b2.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        CSQueue b3 = queues.get(TestParentQueue.B3);
        b3.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        queues.get(ROOT).getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        // Simulate C returning a container on node_0
        stubQueueAllocation(a, clusterResource, node_0, (0 * (TestParentQueue.GB)));
        stubQueueAllocation(b, clusterResource, node_0, (0 * (TestParentQueue.GB)));
        stubQueueAllocation(c, clusterResource, node_0, (1 * (TestParentQueue.GB)));
        stubQueueAllocation(d, clusterResource, node_0, (0 * (TestParentQueue.GB)));
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        verifyQueueMetrics(a, (0 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (0 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(c, (1 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(d, (0 * (TestParentQueue.GB)), clusterResource);
        Mockito.reset(a);
        Mockito.reset(b);
        Mockito.reset(c);
        // Now get B2 to allocate
        // A = 0/3, B = 0/15, C = 1/6, D=0/6
        stubQueueAllocation(a, clusterResource, node_1, (0 * (TestParentQueue.GB)));
        stubQueueAllocation(b2, clusterResource, node_1, (4 * (TestParentQueue.GB)));
        stubQueueAllocation(c, clusterResource, node_1, (0 * (TestParentQueue.GB)));
        root.assignContainers(clusterResource, node_1, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        applyAllocationToQueue(clusterResource, (4 * (TestParentQueue.GB)), b);
        verifyQueueMetrics(a, (0 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (4 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(c, (1 * (TestParentQueue.GB)), clusterResource);
        Mockito.reset(a);
        Mockito.reset(b);
        Mockito.reset(c);
        // Now get both A1, C & B3 to allocate in right order
        // A = 0/3, B = 4/15, C = 1/6, D=0/6
        stubQueueAllocation(a1, clusterResource, node_0, (1 * (TestParentQueue.GB)));
        stubQueueAllocation(b3, clusterResource, node_0, (2 * (TestParentQueue.GB)));
        stubQueueAllocation(c, clusterResource, node_0, (2 * (TestParentQueue.GB)));
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        InOrder allocationOrder = Mockito.inOrder(a, c, b);
        allocationOrder.verify(a).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        applyAllocationToQueue(clusterResource, (1 * (TestParentQueue.GB)), a);
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder.verify(c).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        applyAllocationToQueue(clusterResource, (2 * (TestParentQueue.GB)), root);
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder.verify(b).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        applyAllocationToQueue(clusterResource, (2 * (TestParentQueue.GB)), b);
        verifyQueueMetrics(a, (1 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (6 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(c, (3 * (TestParentQueue.GB)), clusterResource);
        Mockito.reset(a);
        Mockito.reset(b);
        Mockito.reset(c);
        // Now verify max-capacity
        // A = 1/3, B = 6/15, C = 3/6, D=0/6
        // Ensure a1 won't alloc above max-cap although it should get
        // scheduling opportunity now, right after a2
        TestParentQueue.LOG.info("here");
        setMaxCapacity(0.1F);// a should be capped at 3/30

        stubQueueAllocation(a1, clusterResource, node_2, (1 * (TestParentQueue.GB)));// shouldn't be

        // allocated due
        // to max-cap
        stubQueueAllocation(a2, clusterResource, node_2, (2 * (TestParentQueue.GB)));
        stubQueueAllocation(b3, clusterResource, node_2, (1 * (TestParentQueue.GB)));
        stubQueueAllocation(b1, clusterResource, node_2, (1 * (TestParentQueue.GB)));
        stubQueueAllocation(c, clusterResource, node_2, (1 * (TestParentQueue.GB)));
        root.assignContainers(clusterResource, node_2, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder = Mockito.inOrder(a, a2, a1, b, c);
        allocationOrder.verify(a).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        allocationOrder.verify(a2).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        applyAllocationToQueue(clusterResource, (2 * (TestParentQueue.GB)), a);
        root.assignContainers(clusterResource, node_2, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder.verify(b).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        applyAllocationToQueue(clusterResource, (2 * (TestParentQueue.GB)), b);
        root.assignContainers(clusterResource, node_2, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder.verify(c).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        verifyQueueMetrics(a, (3 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (8 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(c, (4 * (TestParentQueue.GB)), clusterResource);
        Mockito.reset(a);
        Mockito.reset(b);
        Mockito.reset(c);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQueueCapacitySettingChildZero() throws Exception {
        // Setup queue configs
        setupMultiLevelQueues(csConf);
        // set child queues capacity to 0 when parents not 0
        final String Q_B = ((ROOT) + ".") + (TestParentQueue.B);
        csConf.setCapacity(((Q_B + ".") + (TestParentQueue.B1)), 0);
        csConf.setCapacity(((Q_B + ".") + (TestParentQueue.B2)), 0);
        csConf.setCapacity(((Q_B + ".") + (TestParentQueue.B3)), 0);
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQueueCapacitySettingParentZero() throws Exception {
        // Setup queue configs
        setupMultiLevelQueues(csConf);
        // set parent capacity to 0 when child not 0
        final String Q_B = ((ROOT) + ".") + (TestParentQueue.B);
        csConf.setCapacity(Q_B, 0);
        final String Q_A = ((ROOT) + ".") + (TestParentQueue.A);
        csConf.setCapacity(Q_A, 60);
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
    }

    @Test
    public void testQueueCapacityZero() throws Exception {
        // Setup queue configs
        setupMultiLevelQueues(csConf);
        // set parent and child capacity to 0
        final String Q_B = ((ROOT) + ".") + (TestParentQueue.B);
        csConf.setCapacity(Q_B, 0);
        csConf.setCapacity(((Q_B + ".") + (TestParentQueue.B1)), 0);
        csConf.setCapacity(((Q_B + ".") + (TestParentQueue.B2)), 0);
        csConf.setCapacity(((Q_B + ".") + (TestParentQueue.B3)), 0);
        final String Q_A = ((ROOT) + ".") + (TestParentQueue.A);
        csConf.setCapacity(Q_A, 60);
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        try {
            CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
        } catch (IllegalArgumentException e) {
            Assert.fail(("Failed to create queues with 0 capacity: " + e));
        }
        Assert.assertTrue("Failed to create queues with 0 capacity", true);
    }

    @Test
    public void testOffSwitchScheduling() throws Exception {
        // Setup queue configs
        setupSingleLevelQueues(csConf);
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        CSQueue root = CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
        // Setup some nodes
        final int memoryPerNode = 10;
        final int coresPerNode = 16;
        final int numNodes = 2;
        FiCaSchedulerNode node_0 = TestUtils.getMockNode("host_0", TestParentQueue.DEFAULT_RACK, 0, (memoryPerNode * (TestParentQueue.GB)));
        FiCaSchedulerNode node_1 = TestUtils.getMockNode("host_1", TestParentQueue.DEFAULT_RACK, 0, (memoryPerNode * (TestParentQueue.GB)));
        final Resource clusterResource = Resources.createResource((numNodes * (memoryPerNode * (TestParentQueue.GB))), (numNodes * coresPerNode));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        // Start testing
        LeafQueue a = ((LeafQueue) (queues.get(TestParentQueue.A)));
        LeafQueue b = ((LeafQueue) (queues.get(TestParentQueue.B)));
        a.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        b.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        queues.get(ROOT).getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        // Simulate B returning a container on node_0
        stubQueueAllocation(a, clusterResource, node_0, (0 * (TestParentQueue.GB)), OFF_SWITCH);
        stubQueueAllocation(b, clusterResource, node_0, (1 * (TestParentQueue.GB)), OFF_SWITCH);
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        verifyQueueMetrics(a, (0 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (1 * (TestParentQueue.GB)), clusterResource);
        // Now, A should get the scheduling opportunity since A=0G/6G, B=1G/14G
        // also, B gets a scheduling opportunity since A allocates RACK_LOCAL
        stubQueueAllocation(a, clusterResource, node_1, (2 * (TestParentQueue.GB)), RACK_LOCAL);
        stubQueueAllocation(b, clusterResource, node_1, (1 * (TestParentQueue.GB)), OFF_SWITCH);
        root.assignContainers(clusterResource, node_1, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        InOrder allocationOrder = Mockito.inOrder(a);
        allocationOrder.verify(a).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        root.assignContainers(clusterResource, node_1, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder = Mockito.inOrder(b);
        allocationOrder.verify(b).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        verifyQueueMetrics(a, (2 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (2 * (TestParentQueue.GB)), clusterResource);
        // Now, B should get the scheduling opportunity
        // since A has 2/6G while B has 2/14G,
        // However, since B returns off-switch, A won't get an opportunity
        stubQueueAllocation(a, clusterResource, node_0, (1 * (TestParentQueue.GB)), NODE_LOCAL);
        stubQueueAllocation(b, clusterResource, node_0, (2 * (TestParentQueue.GB)), OFF_SWITCH);
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder = Mockito.inOrder(b, a);
        allocationOrder.verify(b).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        allocationOrder.verify(a).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        verifyQueueMetrics(a, (2 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b, (4 * (TestParentQueue.GB)), clusterResource);
    }

    @Test
    public void testOffSwitchSchedulingMultiLevelQueues() throws Exception {
        // Setup queue configs
        setupMultiLevelQueues(csConf);
        // B3
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        CSQueue root = CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
        // Setup some nodes
        final int memoryPerNode = 10;
        final int coresPerNode = 10;
        final int numNodes = 2;
        FiCaSchedulerNode node_0 = TestUtils.getMockNode("host_0", TestParentQueue.DEFAULT_RACK, 0, (memoryPerNode * (TestParentQueue.GB)));
        FiCaSchedulerNode node_1 = TestUtils.getMockNode("host_1", TestParentQueue.DEFAULT_RACK, 0, (memoryPerNode * (TestParentQueue.GB)));
        final Resource clusterResource = Resources.createResource((numNodes * (memoryPerNode * (TestParentQueue.GB))), (numNodes * coresPerNode));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        // Start testing
        LeafQueue b3 = ((LeafQueue) (queues.get(TestParentQueue.B3)));
        LeafQueue b2 = ((LeafQueue) (queues.get(TestParentQueue.B2)));
        b2.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        b3.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        queues.get(ROOT).getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        CSQueue b = queues.get(TestParentQueue.B);
        b.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestParentQueue.GB))));
        // Simulate B3 returning a container on node_0
        stubQueueAllocation(b2, clusterResource, node_0, (0 * (TestParentQueue.GB)), OFF_SWITCH);
        stubQueueAllocation(b3, clusterResource, node_0, (1 * (TestParentQueue.GB)), OFF_SWITCH);
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        verifyQueueMetrics(b2, (0 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b3, (1 * (TestParentQueue.GB)), clusterResource);
        // Now, B2 should get the scheduling opportunity since B2=0G/2G, B3=1G/7G
        // also, B3 gets a scheduling opportunity since B2 allocates RACK_LOCAL
        stubQueueAllocation(b2, clusterResource, node_1, (1 * (TestParentQueue.GB)), RACK_LOCAL);
        stubQueueAllocation(b3, clusterResource, node_1, (1 * (TestParentQueue.GB)), OFF_SWITCH);
        root.assignContainers(clusterResource, node_1, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        root.assignContainers(clusterResource, node_1, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        InOrder allocationOrder = Mockito.inOrder(b2, b3);
        allocationOrder.verify(b2).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        allocationOrder.verify(b3).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        verifyQueueMetrics(b2, (1 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b3, (2 * (TestParentQueue.GB)), clusterResource);
        // Now, B3 should get the scheduling opportunity
        // since B2 has 1/2G while B3 has 2/7G,
        // However, since B3 returns off-switch, B2 won't get an opportunity
        stubQueueAllocation(b2, clusterResource, node_0, (1 * (TestParentQueue.GB)), NODE_LOCAL);
        stubQueueAllocation(b3, clusterResource, node_0, (1 * (TestParentQueue.GB)), OFF_SWITCH);
        root.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        allocationOrder = Mockito.inOrder(b3, b2);
        allocationOrder.verify(b3).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        allocationOrder.verify(b2).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), anyResourceLimits(), ArgumentMatchers.any(SchedulingMode.class));
        verifyQueueMetrics(b2, (1 * (TestParentQueue.GB)), clusterResource);
        verifyQueueMetrics(b3, (3 * (TestParentQueue.GB)), clusterResource);
    }

    @Test
    public void testQueueAcl() throws Exception {
        setupMultiLevelQueues(csConf);
        csConf.setAcl(ROOT, SUBMIT_APPLICATIONS, " ");
        csConf.setAcl(ROOT, ADMINISTER_QUEUE, " ");
        final String Q_C = ((ROOT) + ".") + (TestParentQueue.C);
        csConf.setAcl(Q_C, ADMINISTER_QUEUE, "*");
        final String Q_C11 = (((Q_C + ".") + (TestParentQueue.C1)) + ".") + (TestParentQueue.C11);
        csConf.setAcl(Q_C11, SUBMIT_APPLICATIONS, "*");
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        CSQueue root = CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
        YarnAuthorizationProvider authorizer = YarnAuthorizationProvider.getInstance(conf);
        AppPriorityACLsManager appPriorityACLManager = new AppPriorityACLsManager(conf);
        CapacitySchedulerQueueManager.setQueueAcls(authorizer, appPriorityACLManager, queues);
        UserGroupInformation user = UserGroupInformation.getCurrentUser();
        // Setup queue configs
        ParentQueue c = ((ParentQueue) (queues.get(TestParentQueue.C)));
        ParentQueue c1 = ((ParentQueue) (queues.get(TestParentQueue.C1)));
        ParentQueue c11 = ((ParentQueue) (queues.get(TestParentQueue.C11)));
        ParentQueue c111 = ((ParentQueue) (queues.get(TestParentQueue.C111)));
        Assert.assertFalse(root.hasAccess(ADMINISTER_QUEUE, user));
        List<QueueUserACLInfo> aclInfos = root.getQueueUserAclInfo(user);
        Assert.assertFalse(hasQueueACL(aclInfos, ADMINISTER_QUEUE, "root"));
        Assert.assertFalse(root.hasAccess(SUBMIT_APPLICATIONS, user));
        Assert.assertFalse(hasQueueACL(aclInfos, SUBMIT_APPLICATIONS, "root"));
        // c has no SA, but QA
        Assert.assertTrue(c.hasAccess(ADMINISTER_QUEUE, user));
        Assert.assertTrue(hasQueueACL(aclInfos, ADMINISTER_QUEUE, "c"));
        Assert.assertFalse(c.hasAccess(SUBMIT_APPLICATIONS, user));
        Assert.assertFalse(hasQueueACL(aclInfos, SUBMIT_APPLICATIONS, "c"));
        // Queue c1 has QA, no SA (gotten perm from parent)
        Assert.assertTrue(c1.hasAccess(ADMINISTER_QUEUE, user));
        Assert.assertTrue(hasQueueACL(aclInfos, ADMINISTER_QUEUE, "c1"));
        Assert.assertFalse(c1.hasAccess(SUBMIT_APPLICATIONS, user));
        Assert.assertFalse(hasQueueACL(aclInfos, SUBMIT_APPLICATIONS, "c1"));
        // Queue c11 has permissions from parent queue and SA
        Assert.assertTrue(c11.hasAccess(ADMINISTER_QUEUE, user));
        Assert.assertTrue(hasQueueACL(aclInfos, ADMINISTER_QUEUE, "c11"));
        Assert.assertTrue(c11.hasAccess(SUBMIT_APPLICATIONS, user));
        Assert.assertTrue(hasQueueACL(aclInfos, SUBMIT_APPLICATIONS, "c11"));
        // Queue c111 has SA and AQ, both from parent
        Assert.assertTrue(c111.hasAccess(ADMINISTER_QUEUE, user));
        Assert.assertTrue(hasQueueACL(aclInfos, ADMINISTER_QUEUE, "c111"));
        Assert.assertTrue(c111.hasAccess(SUBMIT_APPLICATIONS, user));
        Assert.assertTrue(hasQueueACL(aclInfos, SUBMIT_APPLICATIONS, "c111"));
        Mockito.reset(c);
    }

    @Test
    public void testAbsoluteResourceWithChangeInClusterResource() throws Exception {
        // Setup queue configs
        setupSingleLevelQueuesWithAbsoluteResource(csConf);
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        CSQueue root = CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
        // Setup some nodes
        final int memoryPerNode = 10;
        int coresPerNode = 16;
        int numNodes = 2;
        Resource clusterResource = Resources.createResource((numNodes * (memoryPerNode * (TestParentQueue.GB))), (numNodes * coresPerNode));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        // Start testing
        LeafQueue a = ((LeafQueue) (queues.get(TestParentQueue.A)));
        LeafQueue b = ((LeafQueue) (queues.get(TestParentQueue.B)));
        Assert.assertEquals(a.getQueueResourceQuotas().getConfiguredMinResource(), TestParentQueue.QUEUE_A_RESOURCE);
        Assert.assertEquals(b.getQueueResourceQuotas().getConfiguredMinResource(), TestParentQueue.QUEUE_B_RESOURCE);
        Assert.assertEquals(a.getQueueResourceQuotas().getEffectiveMinResource(), TestParentQueue.QUEUE_A_RESOURCE);
        Assert.assertEquals(b.getQueueResourceQuotas().getEffectiveMinResource(), TestParentQueue.QUEUE_B_RESOURCE);
        numNodes = 1;
        clusterResource = Resources.createResource((numNodes * (memoryPerNode * (TestParentQueue.GB))), (numNodes * coresPerNode));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        Resource QUEUE_B_RESOURCE_HALF = Resource.newInstance((7 * 1024), 11);
        Resource QUEUE_A_RESOURCE_HALF = Resource.newInstance((3 * 1024), 5);
        Assert.assertEquals(a.getQueueResourceQuotas().getConfiguredMinResource(), TestParentQueue.QUEUE_A_RESOURCE);
        Assert.assertEquals(b.getQueueResourceQuotas().getConfiguredMinResource(), TestParentQueue.QUEUE_B_RESOURCE);
        Assert.assertEquals(a.getQueueResourceQuotas().getEffectiveMinResource(), QUEUE_A_RESOURCE_HALF);
        Assert.assertEquals(b.getQueueResourceQuotas().getEffectiveMinResource(), QUEUE_B_RESOURCE_HALF);
        coresPerNode = 40;
        clusterResource = Resources.createResource((numNodes * (memoryPerNode * (TestParentQueue.GB))), (numNodes * coresPerNode));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        Resource QUEUE_B_RESOURCE_70PERC = Resource.newInstance((7 * 1024), 27);
        Resource QUEUE_A_RESOURCE_30PERC = Resource.newInstance((3 * 1024), 12);
        Assert.assertEquals(a.getQueueResourceQuotas().getConfiguredMinResource(), TestParentQueue.QUEUE_A_RESOURCE);
        Assert.assertEquals(b.getQueueResourceQuotas().getConfiguredMinResource(), TestParentQueue.QUEUE_B_RESOURCE);
        Assert.assertEquals(a.getQueueResourceQuotas().getEffectiveMinResource(), QUEUE_A_RESOURCE_30PERC);
        Assert.assertEquals(b.getQueueResourceQuotas().getEffectiveMinResource(), QUEUE_B_RESOURCE_70PERC);
    }
}

