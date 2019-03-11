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


import NodeType.OFF_SWITCH;
import ResourceRequest.ANY;
import SchedulingMode.IGNORE_PARTITION_EXCLUSIVITY;
import SchedulingMode.RESPECT_PARTITION_EXCLUSIVITY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.TestUtils;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fifo.FifoScheduler;
import org.apache.hadoop.yarn.server.scheduler.SchedulerRequestKey;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestSchedulerApplicationAttempt {
    private static final NodeId nodeId = NodeId.newInstance("somehost", 5);

    private Configuration conf = new Configuration();

    @Test
    public void testActiveUsersWhenMove() {
        final String user = "user1";
        Queue parentQueue = createQueue("parent", null);
        Queue queue1 = createQueue("queue1", parentQueue);
        Queue queue2 = createQueue("queue2", parentQueue);
        Queue queue3 = createQueue("queue3", parentQueue);
        ApplicationAttemptId appAttId = createAppAttemptId(0, 0);
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getEpoch()).thenReturn(3L);
        SchedulerApplicationAttempt app = new SchedulerApplicationAttempt(appAttId, user, queue1, queue1.getAbstractUsersManager(), rmContext);
        // Resource request
        Resource requestedResource = Resource.newInstance(1536, 2);
        Priority requestedPriority = Priority.newInstance(2);
        ResourceRequest request = ResourceRequest.newInstance(requestedPriority, ANY, requestedResource, 1);
        app.updateResourceRequests(Arrays.asList(request));
        Assert.assertEquals(1, queue1.getAbstractUsersManager().getNumActiveUsers());
        // move app from queue1 to queue2
        app.move(queue2);
        // Active user count has to decrease from queue1
        Assert.assertEquals(0, queue1.getAbstractUsersManager().getNumActiveUsers());
        // Increase the active user count in queue2 if the moved app has pending requests
        Assert.assertEquals(1, queue2.getAbstractUsersManager().getNumActiveUsers());
        // Allocated container
        RMContainer container1 = createRMContainer(appAttId, 1, requestedResource);
        app.liveContainers.put(container1.getContainerId(), container1);
        SchedulerNode node = createNode();
        app.appSchedulingInfo.allocate(OFF_SWITCH, node, TestUtils.toSchedulerKey(requestedPriority), container1.getContainer());
        // Active user count has to decrease from queue2 due to app has NO pending requests
        Assert.assertEquals(0, queue2.getAbstractUsersManager().getNumActiveUsers());
        // move app from queue2 to queue3
        app.move(queue3);
        // Active user count in queue3 stays same if the moved app has NO pending requests
        Assert.assertEquals(0, queue3.getAbstractUsersManager().getNumActiveUsers());
    }

    @Test
    public void testMove() {
        final String user = "user1";
        Queue parentQueue = createQueue("parent", null);
        Queue oldQueue = createQueue("old", parentQueue);
        Queue newQueue = createQueue("new", parentQueue);
        QueueMetrics parentMetrics = parentQueue.getMetrics();
        QueueMetrics oldMetrics = oldQueue.getMetrics();
        QueueMetrics newMetrics = newQueue.getMetrics();
        ApplicationAttemptId appAttId = createAppAttemptId(0, 0);
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getEpoch()).thenReturn(3L);
        SchedulerApplicationAttempt app = new SchedulerApplicationAttempt(appAttId, user, oldQueue, oldQueue.getAbstractUsersManager(), rmContext);
        oldMetrics.submitApp(user);
        // confirm that containerId is calculated based on epoch.
        Assert.assertEquals(3298534883329L, app.getNewContainerId());
        // Resource request
        Resource requestedResource = Resource.newInstance(1536, 2);
        Priority requestedPriority = Priority.newInstance(2);
        ResourceRequest request = ResourceRequest.newInstance(requestedPriority, ANY, requestedResource, 3);
        app.updateResourceRequests(Arrays.asList(request));
        // Allocated container
        RMContainer container1 = createRMContainer(appAttId, 1, requestedResource);
        app.liveContainers.put(container1.getContainerId(), container1);
        SchedulerNode node = createNode();
        app.appSchedulingInfo.allocate(OFF_SWITCH, node, TestUtils.toSchedulerKey(requestedPriority), container1.getContainer());
        // Reserved container
        Priority prio1 = Priority.newInstance(1);
        Resource reservedResource = Resource.newInstance(2048, 3);
        RMContainer container2 = createReservedRMContainer(appAttId, 1, reservedResource, node.getNodeID(), prio1);
        Map<NodeId, RMContainer> reservations = new HashMap<NodeId, RMContainer>();
        reservations.put(node.getNodeID(), container2);
        app.reservedContainers.put(TestUtils.toSchedulerKey(prio1), reservations);
        oldMetrics.reserveResource(container2.getNodeLabelExpression(), user, reservedResource);
        checkQueueMetrics(oldMetrics, 1, 1, 1536, 2, 2048, 3, 3072, 4);
        checkQueueMetrics(newMetrics, 0, 0, 0, 0, 0, 0, 0, 0);
        checkQueueMetrics(parentMetrics, 1, 1, 1536, 2, 2048, 3, 3072, 4);
        app.move(newQueue);
        checkQueueMetrics(oldMetrics, 0, 0, 0, 0, 0, 0, 0, 0);
        checkQueueMetrics(newMetrics, 1, 1, 1536, 2, 2048, 3, 3072, 4);
        checkQueueMetrics(parentMetrics, 1, 1, 1536, 2, 2048, 3, 3072, 4);
    }

    @Test
    public void testAppPercentages() throws Exception {
        FifoScheduler scheduler = Mockito.mock(FifoScheduler.class);
        Mockito.when(scheduler.getClusterResource()).thenReturn(Resource.newInstance((10 * 1024), 10));
        Mockito.when(scheduler.getResourceCalculator()).thenReturn(new DefaultResourceCalculator());
        ApplicationAttemptId appAttId = createAppAttemptId(0, 0);
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getEpoch()).thenReturn(3L);
        Mockito.when(rmContext.getScheduler()).thenReturn(scheduler);
        final String user = "user1";
        Queue queue = createQueue("test", null);
        SchedulerApplicationAttempt app = new SchedulerApplicationAttempt(appAttId, user, queue, queue.getAbstractUsersManager(), rmContext);
        // Resource request
        Resource requestedResource = Resource.newInstance(1536, 2);
        app.attemptResourceUsage.incUsed(requestedResource);
        Assert.assertEquals(15.0F, app.getResourceUsageReport().getQueueUsagePercentage(), 0.01F);
        Assert.assertEquals(15.0F, app.getResourceUsageReport().getClusterUsagePercentage(), 0.01F);
        queue = createQueue("test2", null, 0.5F);
        app = new SchedulerApplicationAttempt(appAttId, user, queue, queue.getAbstractUsersManager(), rmContext);
        app.attemptResourceUsage.incUsed(requestedResource);
        Assert.assertEquals(30.0F, app.getResourceUsageReport().getQueueUsagePercentage(), 0.01F);
        Assert.assertEquals(15.0F, app.getResourceUsageReport().getClusterUsagePercentage(), 0.01F);
        app.attemptResourceUsage.incUsed(requestedResource);
        app.attemptResourceUsage.incUsed(requestedResource);
        app.attemptResourceUsage.incUsed(requestedResource);
        Assert.assertEquals(120.0F, app.getResourceUsageReport().getQueueUsagePercentage(), 0.01F);
        Assert.assertEquals(60.0F, app.getResourceUsageReport().getClusterUsagePercentage(), 0.01F);
        queue = createQueue("test3", null, 0.0F);
        app = new SchedulerApplicationAttempt(appAttId, user, queue, queue.getAbstractUsersManager(), rmContext);
        // Resource request
        app.attemptResourceUsage.incUsed(requestedResource);
        Assert.assertEquals(0.0F, app.getResourceUsageReport().getQueueUsagePercentage(), 0.01F);
        Assert.assertEquals(15.0F, app.getResourceUsageReport().getClusterUsagePercentage(), 0.01F);
    }

    @Test
    public void testAppPercentagesOnswitch() throws Exception {
        FifoScheduler scheduler = Mockito.mock(FifoScheduler.class);
        Mockito.when(scheduler.getClusterResource()).thenReturn(Resource.newInstance(0, 0));
        Mockito.when(scheduler.getResourceCalculator()).thenReturn(new DefaultResourceCalculator());
        ApplicationAttemptId appAttId = createAppAttemptId(0, 0);
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getEpoch()).thenReturn(3L);
        Mockito.when(rmContext.getScheduler()).thenReturn(scheduler);
        final String user = "user1";
        Queue queue = createQueue("test", null);
        SchedulerApplicationAttempt app = new SchedulerApplicationAttempt(appAttId, user, queue, queue.getAbstractUsersManager(), rmContext);
        // Resource request
        Resource requestedResource = Resource.newInstance(1536, 2);
        app.attemptResourceUsage.incUsed(requestedResource);
        Assert.assertEquals(0.0F, app.getResourceUsageReport().getQueueUsagePercentage(), 0.0F);
        Assert.assertEquals(0.0F, app.getResourceUsageReport().getClusterUsagePercentage(), 0.0F);
    }

    @Test
    public void testSchedulingOpportunityOverflow() throws Exception {
        ApplicationAttemptId attemptId = createAppAttemptId(0, 0);
        Queue queue = createQueue("test", null);
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getEpoch()).thenReturn(3L);
        SchedulerApplicationAttempt app = new SchedulerApplicationAttempt(attemptId, "user", queue, queue.getAbstractUsersManager(), rmContext);
        Priority priority = Priority.newInstance(1);
        SchedulerRequestKey schedulerKey = TestUtils.toSchedulerKey(priority);
        Assert.assertEquals(0, app.getSchedulingOpportunities(schedulerKey));
        app.addSchedulingOpportunity(schedulerKey);
        Assert.assertEquals(1, app.getSchedulingOpportunities(schedulerKey));
        // verify the count is capped at MAX_VALUE and does not overflow
        app.setSchedulingOpportunities(schedulerKey, ((Integer.MAX_VALUE) - 1));
        Assert.assertEquals(((Integer.MAX_VALUE) - 1), app.getSchedulingOpportunities(schedulerKey));
        app.addSchedulingOpportunity(schedulerKey);
        Assert.assertEquals(Integer.MAX_VALUE, app.getSchedulingOpportunities(schedulerKey));
        app.addSchedulingOpportunity(schedulerKey);
        Assert.assertEquals(Integer.MAX_VALUE, app.getSchedulingOpportunities(schedulerKey));
    }

    @Test
    public void testHasPendingResourceRequest() throws Exception {
        ApplicationAttemptId attemptId = createAppAttemptId(0, 0);
        Queue queue = createQueue("test", null);
        RMContext rmContext = Mockito.mock(RMContext.class);
        Mockito.when(rmContext.getEpoch()).thenReturn(3L);
        SchedulerApplicationAttempt app = new SchedulerApplicationAttempt(attemptId, "user", queue, queue.getAbstractUsersManager(), rmContext);
        Priority priority = Priority.newInstance(1);
        List<ResourceRequest> requests = new ArrayList<>(2);
        Resource unit = Resource.newInstance(1L, 1);
        // Add a request for a container with a node label
        requests.add(ResourceRequest.newInstance(priority, ANY, unit, 1, false, "label1"));
        // Add a request for a container without a node label
        requests.add(ResourceRequest.newInstance(priority, ANY, unit, 1, false, ""));
        // Add unique allocation IDs so that the requests aren't considered
        // duplicates
        requests.get(0).setAllocationRequestId(0L);
        requests.get(1).setAllocationRequestId(1L);
        app.updateResourceRequests(requests);
        Assert.assertTrue(("Reported no pending resource requests for no label when " + "resource requests for no label are pending (exclusive partitions)"), app.hasPendingResourceRequest("", RESPECT_PARTITION_EXCLUSIVITY));
        Assert.assertTrue(("Reported no pending resource requests for label with pending " + "resource requests (exclusive partitions)"), app.hasPendingResourceRequest("label1", RESPECT_PARTITION_EXCLUSIVITY));
        Assert.assertFalse(("Reported pending resource requests for label with no pending " + "resource requests (exclusive partitions)"), app.hasPendingResourceRequest("label2", RESPECT_PARTITION_EXCLUSIVITY));
        Assert.assertTrue(("Reported no pending resource requests for no label when " + "resource requests for no label are pending (relaxed partitions)"), app.hasPendingResourceRequest("", IGNORE_PARTITION_EXCLUSIVITY));
        Assert.assertTrue(("Reported no pending resource requests for label with pending " + "resource requests (relaxed partitions)"), app.hasPendingResourceRequest("label1", IGNORE_PARTITION_EXCLUSIVITY));
        Assert.assertTrue(("Reported no pending resource requests for label with no " + "pending resource requests (relaxed partitions)"), app.hasPendingResourceRequest("label2", IGNORE_PARTITION_EXCLUSIVITY));
    }
}

