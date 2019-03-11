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
import RMContainerEventType.KILL;
import SchedulingMode.RESPECT_PARTITION_EXCLUSIVITY;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.ahs.RMApplicationHistoryWriter;
import org.apache.hadoop.yarn.server.resourcemanager.metrics.SystemMetricsPublisher;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.ContainerAllocationExpirer;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.placement.CandidateNodeSet;
import org.apache.hadoop.yarn.server.scheduler.SchedulerRequestKey;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestChildQueueOrder {
    private static final Logger LOG = LoggerFactory.getLogger(TestChildQueueOrder.class);

    RMContext rmContext;

    YarnConfiguration conf;

    CapacitySchedulerConfiguration csConf;

    CapacitySchedulerContext csContext;

    static final int GB = 1024;

    static final String DEFAULT_RACK = "/default";

    private final ResourceCalculator resourceComparator = new DefaultResourceCalculator();

    static final float DELTA = 1.0E-4F;

    private static final String A = "a";

    private static final String B = "b";

    private static final String C = "c";

    private static final String D = "d";

    @Test
    @SuppressWarnings("unchecked")
    public void testSortedQueues() throws Exception {
        // Setup queue configs
        setupSortedQueues(csConf);
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        CSQueue root = CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, ROOT, queues, queues, TestUtils.spyHook);
        // Setup some nodes
        final int memoryPerNode = 10;
        final int coresPerNode = 16;
        final int numNodes = 1;
        FiCaSchedulerNode node_0 = TestUtils.getMockNode("host_0", TestChildQueueOrder.DEFAULT_RACK, 0, (memoryPerNode * (TestChildQueueOrder.GB)));
        Mockito.doNothing().when(node_0).releaseContainer(ArgumentMatchers.any(ContainerId.class), ArgumentMatchers.anyBoolean());
        final Resource clusterResource = Resources.createResource((numNodes * (memoryPerNode * (TestChildQueueOrder.GB))), (numNodes * coresPerNode));
        Mockito.when(csContext.getNumClusterNodes()).thenReturn(numNodes);
        root.updateClusterResource(clusterResource, new ResourceLimits(clusterResource));
        // Start testing
        CSQueue a = queues.get(TestChildQueueOrder.A);
        CSQueue b = queues.get(TestChildQueueOrder.B);
        CSQueue c = queues.get(TestChildQueueOrder.C);
        CSQueue d = queues.get(TestChildQueueOrder.D);
        // Make a/b/c/d has >0 pending resource, so that allocation will continue.
        queues.get(ROOT).getQueueResourceUsage().incPending(Resources.createResource((1 * (TestChildQueueOrder.GB))));
        a.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestChildQueueOrder.GB))));
        b.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestChildQueueOrder.GB))));
        c.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestChildQueueOrder.GB))));
        d.getQueueResourceUsage().incPending(Resources.createResource((1 * (TestChildQueueOrder.GB))));
        final String user_0 = "user_0";
        // Stub an App and its containerCompleted
        FiCaSchedulerApp app_0 = getMockApplication(0, user_0);
        Mockito.doReturn(true).when(app_0).containerCompleted(ArgumentMatchers.any(RMContainer.class), ArgumentMatchers.any(), ArgumentMatchers.any(RMContainerEventType.class), ArgumentMatchers.any(String.class));
        Priority priority = TestUtils.createMockPriority(1);
        ContainerAllocationExpirer expirer = Mockito.mock(ContainerAllocationExpirer.class);
        DrainDispatcher drainDispatcher = new DrainDispatcher();
        RMApplicationHistoryWriter writer = Mockito.mock(RMApplicationHistoryWriter.class);
        SystemMetricsPublisher publisher = Mockito.mock(SystemMetricsPublisher.class);
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getContainerAllocationExpirer()).thenReturn(expirer);
        Mockito.when(rmContext.getDispatcher()).thenReturn(drainDispatcher);
        Mockito.when(rmContext.getRMApplicationHistoryWriter()).thenReturn(writer);
        Mockito.when(rmContext.getSystemMetricsPublisher()).thenReturn(publisher);
        Mockito.when(rmContext.getYarnConfiguration()).thenReturn(new YarnConfiguration());
        ApplicationAttemptId appAttemptId = BuilderUtils.newApplicationAttemptId(app_0.getApplicationId(), 1);
        ContainerId containerId = BuilderUtils.newContainerId(appAttemptId, 1);
        Container container = TestUtils.getMockContainer(containerId, node_0.getNodeID(), Resources.createResource((1 * (TestChildQueueOrder.GB))), priority);
        RMContainer rmContainer = new org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainerImpl(container, SchedulerRequestKey.extractFrom(container), appAttemptId, node_0.getNodeID(), "user", rmContext);
        // Assign {1,2,3,4} 1GB containers respectively to queues
        stubQueueAllocation(a, clusterResource, node_0, (1 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(b, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(c, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(d, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
        root.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        for (int i = 0; i < 2; i++) {
            stubQueueAllocation(a, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(b, clusterResource, node_0, (1 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(c, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(d, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            root.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        }
        for (int i = 0; i < 3; i++) {
            stubQueueAllocation(a, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(b, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(c, clusterResource, node_0, (1 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(d, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            root.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        }
        for (int i = 0; i < 4; i++) {
            stubQueueAllocation(a, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(b, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(c, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(d, clusterResource, node_0, (1 * (TestChildQueueOrder.GB)));
            root.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        }
        verifyQueueMetrics(a, (1 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(b, (2 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(c, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(d, (4 * (TestChildQueueOrder.GB)), clusterResource);
        TestChildQueueOrder.LOG.info(("status child-queues: " + (getChildQueuesToPrint())));
        // Release 3 x 1GB containers from D
        for (int i = 0; i < 3; i++) {
            d.completedContainer(clusterResource, app_0, node_0, rmContainer, null, KILL, null, true);
        }
        verifyQueueMetrics(a, (1 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(b, (2 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(c, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(d, (1 * (TestChildQueueOrder.GB)), clusterResource);
        // reset manually resources on node
        node_0 = TestUtils.getMockNode("host_0", TestChildQueueOrder.DEFAULT_RACK, 0, (((((memoryPerNode - 1) - 2) - 3) - 1) * (TestChildQueueOrder.GB)));
        TestChildQueueOrder.LOG.info(("status child-queues: " + (getChildQueuesToPrint())));
        // Assign 2 x 1GB Containers to A
        for (int i = 0; i < 2; i++) {
            stubQueueAllocation(a, clusterResource, node_0, (1 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(b, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(c, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            stubQueueAllocation(d, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
            root.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        }
        verifyQueueMetrics(a, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(b, (2 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(c, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(d, (1 * (TestChildQueueOrder.GB)), clusterResource);
        TestChildQueueOrder.LOG.info(("status child-queues: " + (getChildQueuesToPrint())));
        // Release 1GB Container from A
        a.completedContainer(clusterResource, app_0, node_0, rmContainer, null, KILL, null, true);
        verifyQueueMetrics(a, (2 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(b, (2 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(c, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(d, (1 * (TestChildQueueOrder.GB)), clusterResource);
        // reset manually resources on node
        node_0 = TestUtils.getMockNode("host_0", TestChildQueueOrder.DEFAULT_RACK, 0, (((((memoryPerNode - 2) - 2) - 3) - 1) * (TestChildQueueOrder.GB)));
        TestChildQueueOrder.LOG.info(("status child-queues: " + (getChildQueuesToPrint())));
        // Assign 1GB container to B
        stubQueueAllocation(a, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(b, clusterResource, node_0, (1 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(c, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(d, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
        root.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        verifyQueueMetrics(a, (2 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(b, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(c, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(d, (1 * (TestChildQueueOrder.GB)), clusterResource);
        TestChildQueueOrder.LOG.info(("status child-queues: " + (getChildQueuesToPrint())));
        // Release 1GB container resources from B
        b.completedContainer(clusterResource, app_0, node_0, rmContainer, null, KILL, null, true);
        verifyQueueMetrics(a, (2 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(b, (2 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(c, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(d, (1 * (TestChildQueueOrder.GB)), clusterResource);
        // reset manually resources on node
        node_0 = TestUtils.getMockNode("host_0", TestChildQueueOrder.DEFAULT_RACK, 0, (((((memoryPerNode - 2) - 2) - 3) - 1) * (TestChildQueueOrder.GB)));
        TestChildQueueOrder.LOG.info(("status child-queues: " + (getChildQueuesToPrint())));
        // Assign 1GB container to A
        stubQueueAllocation(a, clusterResource, node_0, (1 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(b, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(c, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(d, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
        root.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        verifyQueueMetrics(a, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(b, (2 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(c, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(d, (1 * (TestChildQueueOrder.GB)), clusterResource);
        TestChildQueueOrder.LOG.info(("status child-queues: " + (getChildQueuesToPrint())));
        // Now do the real test, where B and D request a 1GB container
        // D should should get the next container if the order is correct
        stubQueueAllocation(a, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(b, clusterResource, node_0, (1 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(c, clusterResource, node_0, (0 * (TestChildQueueOrder.GB)));
        stubQueueAllocation(d, clusterResource, node_0, (1 * (TestChildQueueOrder.GB)));
        root.assignContainers(clusterResource, node_0, new ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        InOrder allocationOrder = Mockito.inOrder(d, b);
        allocationOrder.verify(d).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), ArgumentMatchers.any(ResourceLimits.class), ArgumentMatchers.any(SchedulingMode.class));
        allocationOrder.verify(b).assignContainers(ArgumentMatchers.eq(clusterResource), ArgumentMatchers.any(CandidateNodeSet.class), ArgumentMatchers.any(ResourceLimits.class), ArgumentMatchers.any(SchedulingMode.class));
        verifyQueueMetrics(a, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(b, (2 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(c, (3 * (TestChildQueueOrder.GB)), clusterResource);
        verifyQueueMetrics(d, (2 * (TestChildQueueOrder.GB)), clusterResource);// D got the container

        TestChildQueueOrder.LOG.info(("status child-queues: " + (getChildQueuesToPrint())));
    }
}

