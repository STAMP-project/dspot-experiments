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


import CapacitySchedulerConfiguration.DEFAULT_MAXIMUM_APPLICATIONMASTERS_RESOURCE_PERCENT;
import CapacitySchedulerConfiguration.UNDEFINED;
import RMAppState.ACCEPTED;
import RMAppState.FAILED;
import ResourceRequest.ANY;
import SchedulingMode.RESPECT_PARTITION_EXCLUSIVITY;
import YarnConfiguration.RM_SCHEDULER;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.NullRMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ActiveUsersManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceUsage;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.preemption.PreemptionManager;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerNode;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CapacitySchedulerConfiguration.DEFAULT_MAXIMUM_SYSTEM_APPLICATIIONS;
import static CapacitySchedulerConfiguration.PREFIX;
import static CapacitySchedulerConfiguration.ROOT;


public class TestApplicationLimits {
    private static final Logger LOG = LoggerFactory.getLogger(TestApplicationLimits.class);

    static final int GB = 1024;

    LeafQueue queue;

    CSQueue root;

    private final ResourceCalculator resourceCalculator = new DefaultResourceCalculator();

    RMContext rmContext = null;

    private static final String A = "a";

    private static final String B = "b";

    @Test
    public void testAMResourceLimit() throws Exception {
        final String user_0 = "user_0";
        final String user_1 = "user_1";
        // This uses the default 10% of cluster value for the max am resources
        // which are allowed, at 80GB = 8GB for AM's at the queue level.  The user
        // am limit is 4G initially (based on the queue absolute capacity)
        // when there is only 1 user, and drops to 2G (the userlimit) when there
        // is a second user
        Resource clusterResource = Resource.newInstance((80 * (TestApplicationLimits.GB)), 40);
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        queue.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        ActiveUsersManager activeUsersManager = Mockito.mock(ActiveUsersManager.class);
        Mockito.when(queue.getAbstractUsersManager()).thenReturn(activeUsersManager);
        Assert.assertEquals(Resource.newInstance((8 * (TestApplicationLimits.GB)), 1), queue.calculateAndGetAMResourceLimit());
        Assert.assertEquals(Resource.newInstance((4 * (TestApplicationLimits.GB)), 1), queue.getUserAMResourceLimit());
        // Two apps for user_0, both start
        int APPLICATION_ID = 0;
        FiCaSchedulerApp app_0 = getMockApplication((APPLICATION_ID++), user_0, Resource.newInstance((2 * (TestApplicationLimits.GB)), 1));
        queue.submitApplicationAttempt(app_0, user_0);
        Assert.assertEquals(1, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(1, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_0));
        Mockito.when(activeUsersManager.getNumActiveUsers()).thenReturn(1);
        FiCaSchedulerApp app_1 = getMockApplication((APPLICATION_ID++), user_0, Resource.newInstance((2 * (TestApplicationLimits.GB)), 1));
        queue.submitApplicationAttempt(app_1, user_0);
        Assert.assertEquals(2, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_0));
        // AMLimits unchanged
        Assert.assertEquals(Resource.newInstance((8 * (TestApplicationLimits.GB)), 1), queue.getAMResourceLimit());
        Assert.assertEquals(Resource.newInstance((4 * (TestApplicationLimits.GB)), 1), queue.getUserAMResourceLimit());
        // One app for user_1, starts
        FiCaSchedulerApp app_2 = getMockApplication((APPLICATION_ID++), user_1, Resource.newInstance((2 * (TestApplicationLimits.GB)), 1));
        queue.submitApplicationAttempt(app_2, user_1);
        Assert.assertEquals(3, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(1, queue.getNumActiveApplications(user_1));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_1));
        Mockito.when(activeUsersManager.getNumActiveUsers()).thenReturn(2);
        // Now userAMResourceLimit drops to the queue configured 50% as there is
        // another user active
        Assert.assertEquals(Resource.newInstance((8 * (TestApplicationLimits.GB)), 1), queue.getAMResourceLimit());
        Assert.assertEquals(Resource.newInstance((2 * (TestApplicationLimits.GB)), 1), queue.getUserAMResourceLimit());
        // Second user_1 app cannot start
        FiCaSchedulerApp app_3 = getMockApplication((APPLICATION_ID++), user_1, Resource.newInstance((2 * (TestApplicationLimits.GB)), 1));
        queue.submitApplicationAttempt(app_3, user_1);
        Assert.assertEquals(3, queue.getNumActiveApplications());
        Assert.assertEquals(1, queue.getNumPendingApplications());
        Assert.assertEquals(1, queue.getNumActiveApplications(user_1));
        Assert.assertEquals(1, queue.getNumPendingApplications(user_1));
        // Now finish app so another should be activated
        queue.finishApplicationAttempt(app_2, TestApplicationLimits.A);
        Assert.assertEquals(3, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(1, queue.getNumActiveApplications(user_1));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_1));
    }

    @Test
    public void testLimitsComputation() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        setupQueueConfiguration(csConf);
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerContext csContext = Mockito.mock(CapacitySchedulerContext.class);
        Mockito.when(csContext.getConfiguration()).thenReturn(csConf);
        Mockito.when(csContext.getConf()).thenReturn(conf);
        Mockito.when(csContext.getMinimumResourceCapability()).thenReturn(Resources.createResource(TestApplicationLimits.GB, 1));
        Mockito.when(csContext.getMaximumResourceCapability()).thenReturn(Resources.createResource((16 * (TestApplicationLimits.GB)), 16));
        Mockito.when(csContext.getResourceCalculator()).thenReturn(resourceCalculator);
        Mockito.when(csContext.getRMContext()).thenReturn(rmContext);
        Mockito.when(csContext.getPreemptionManager()).thenReturn(new PreemptionManager());
        // Say cluster has 100 nodes of 16G each
        Resource clusterResource = Resources.createResource(((100 * 16) * (TestApplicationLimits.GB)), (100 * 16));
        Mockito.when(csContext.getClusterResource()).thenReturn(clusterResource);
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        CSQueue root = CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, "root", queues, queues, TestUtils.spyHook);
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        LeafQueue queue = ((LeafQueue) (queues.get(TestApplicationLimits.A)));
        TestApplicationLimits.LOG.info((((("Queue 'A' -" + " aMResourceLimit=") + (queue.getAMResourceLimit())) + " UserAMResourceLimit=") + (queue.getUserAMResourceLimit())));
        Resource amResourceLimit = Resource.newInstance((160 * (TestApplicationLimits.GB)), 1);
        Assert.assertEquals(queue.calculateAndGetAMResourceLimit(), amResourceLimit);
        Assert.assertEquals(queue.getUserAMResourceLimit(), Resource.newInstance((80 * (TestApplicationLimits.GB)), 1));
        // Assert in metrics
        Assert.assertEquals(queue.getMetrics().getAMResourceLimitMB(), amResourceLimit.getMemorySize());
        Assert.assertEquals(queue.getMetrics().getAMResourceLimitVCores(), amResourceLimit.getVirtualCores());
        Assert.assertEquals(((int) ((clusterResource.getMemorySize()) * (queue.getAbsoluteCapacity()))), queue.getMetrics().getAvailableMB());
        // Add some nodes to the cluster & test new limits
        clusterResource = Resources.createResource(((120 * 16) * (TestApplicationLimits.GB)));
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        Assert.assertEquals(queue.calculateAndGetAMResourceLimit(), Resource.newInstance((192 * (TestApplicationLimits.GB)), 1));
        Assert.assertEquals(queue.getUserAMResourceLimit(), Resource.newInstance((96 * (TestApplicationLimits.GB)), 1));
        Assert.assertEquals(((int) ((clusterResource.getMemorySize()) * (queue.getAbsoluteCapacity()))), queue.getMetrics().getAvailableMB());
        // should return -1 if per queue setting not set
        Assert.assertEquals(((int) (UNDEFINED)), csConf.getMaximumApplicationsPerQueue(queue.getQueuePath()));
        int expectedMaxApps = ((int) ((DEFAULT_MAXIMUM_SYSTEM_APPLICATIIONS) * (queue.getAbsoluteCapacity())));
        Assert.assertEquals(expectedMaxApps, queue.getMaxApplications());
        int expectedMaxAppsPerUser = Math.min(expectedMaxApps, ((int) ((expectedMaxApps * ((queue.getUserLimit()) / 100.0F)) * (queue.getUserLimitFactor()))));
        Assert.assertEquals(expectedMaxAppsPerUser, queue.getMaxApplicationsPerUser());
        // should default to global setting if per queue setting not set
        Assert.assertEquals(((long) (DEFAULT_MAXIMUM_APPLICATIONMASTERS_RESOURCE_PERCENT)), ((long) (csConf.getMaximumApplicationMasterResourcePerQueuePercent(queue.getQueuePath()))));
        // Change the per-queue max AM resources percentage.
        csConf.setFloat((((PREFIX) + (queue.getQueuePath())) + ".maximum-am-resource-percent"), 0.5F);
        // Re-create queues to get new configs.
        queues = new HashMap<String, CSQueue>();
        root = CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, "root", queues, queues, TestUtils.spyHook);
        clusterResource = Resources.createResource(((100 * 16) * (TestApplicationLimits.GB)));
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        queue = ((LeafQueue) (queues.get(TestApplicationLimits.A)));
        Assert.assertEquals(((long) (0.5)), ((long) (csConf.getMaximumApplicationMasterResourcePerQueuePercent(queue.getQueuePath()))));
        Assert.assertEquals(queue.calculateAndGetAMResourceLimit(), Resource.newInstance((800 * (TestApplicationLimits.GB)), 1));
        Assert.assertEquals(queue.getUserAMResourceLimit(), Resource.newInstance((400 * (TestApplicationLimits.GB)), 1));
        // Change the per-queue max applications.
        csConf.setInt((((PREFIX) + (queue.getQueuePath())) + ".maximum-applications"), 9999);
        // Re-create queues to get new configs.
        queues = new HashMap<String, CSQueue>();
        root = CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, "root", queues, queues, TestUtils.spyHook);
        root.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        queue = ((LeafQueue) (queues.get(TestApplicationLimits.A)));
        Assert.assertEquals(9999, ((int) (csConf.getMaximumApplicationsPerQueue(queue.getQueuePath()))));
        Assert.assertEquals(9999, queue.getMaxApplications());
        expectedMaxAppsPerUser = Math.min(9999, ((int) ((9999 * ((queue.getUserLimit()) / 100.0F)) * (queue.getUserLimitFactor()))));
        Assert.assertEquals(expectedMaxAppsPerUser, queue.getMaxApplicationsPerUser());
    }

    @Test
    public void testActiveApplicationLimits() throws Exception {
        final String user_0 = "user_0";
        final String user_1 = "user_1";
        final String user_2 = "user_2";
        Assert.assertEquals(Resource.newInstance((16 * (TestApplicationLimits.GB)), 1), queue.calculateAndGetAMResourceLimit());
        Assert.assertEquals(Resource.newInstance((8 * (TestApplicationLimits.GB)), 1), queue.getUserAMResourceLimit());
        int APPLICATION_ID = 0;
        // Submit first application
        FiCaSchedulerApp app_0 = getMockApplication((APPLICATION_ID++), user_0, Resources.createResource((4 * (TestApplicationLimits.GB)), 0));
        queue.submitApplicationAttempt(app_0, user_0);
        Assert.assertEquals(1, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(1, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_0));
        // Submit second application
        FiCaSchedulerApp app_1 = getMockApplication((APPLICATION_ID++), user_0, Resources.createResource((4 * (TestApplicationLimits.GB)), 0));
        queue.submitApplicationAttempt(app_1, user_0);
        Assert.assertEquals(2, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_0));
        // Submit third application, should remain pending due to user amlimit
        FiCaSchedulerApp app_2 = getMockApplication((APPLICATION_ID++), user_0, Resources.createResource((4 * (TestApplicationLimits.GB)), 0));
        queue.submitApplicationAttempt(app_2, user_0);
        Assert.assertEquals(2, queue.getNumActiveApplications());
        Assert.assertEquals(1, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(1, queue.getNumPendingApplications(user_0));
        // Finish one application, app_2 should be activated
        queue.finishApplicationAttempt(app_0, TestApplicationLimits.A);
        Assert.assertEquals(2, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_0));
        // Submit another one for user_0
        FiCaSchedulerApp app_3 = getMockApplication((APPLICATION_ID++), user_0, Resources.createResource((4 * (TestApplicationLimits.GB)), 0));
        queue.submitApplicationAttempt(app_3, user_0);
        Assert.assertEquals(2, queue.getNumActiveApplications());
        Assert.assertEquals(1, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(1, queue.getNumPendingApplications(user_0));
        // Submit first app for user_1
        FiCaSchedulerApp app_4 = getMockApplication((APPLICATION_ID++), user_1, Resources.createResource((8 * (TestApplicationLimits.GB)), 0));
        queue.submitApplicationAttempt(app_4, user_1);
        Assert.assertEquals(3, queue.getNumActiveApplications());
        Assert.assertEquals(1, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(1, queue.getNumPendingApplications(user_0));
        Assert.assertEquals(1, queue.getNumActiveApplications(user_1));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_1));
        // Submit first app for user_2, should block due to queue amlimit
        FiCaSchedulerApp app_5 = getMockApplication((APPLICATION_ID++), user_2, Resources.createResource((8 * (TestApplicationLimits.GB)), 0));
        queue.submitApplicationAttempt(app_5, user_2);
        Assert.assertEquals(3, queue.getNumActiveApplications());
        Assert.assertEquals(2, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(1, queue.getNumPendingApplications(user_0));
        Assert.assertEquals(1, queue.getNumActiveApplications(user_1));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_1));
        Assert.assertEquals(1, queue.getNumPendingApplications(user_2));
        // Now finish one app of user_1 so app_5 should be activated
        queue.finishApplicationAttempt(app_4, TestApplicationLimits.A);
        Assert.assertEquals(3, queue.getNumActiveApplications());
        Assert.assertEquals(1, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(1, queue.getNumPendingApplications(user_0));
        Assert.assertEquals(0, queue.getNumActiveApplications(user_1));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_1));
        Assert.assertEquals(1, queue.getNumActiveApplications(user_2));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_2));
    }

    @Test
    public void testActiveLimitsWithKilledApps() throws Exception {
        final String user_0 = "user_0";
        int APPLICATION_ID = 0;
        // Submit first application
        FiCaSchedulerApp app_0 = getMockApplication((APPLICATION_ID++), user_0, Resources.createResource((4 * (TestApplicationLimits.GB)), 0));
        queue.submitApplicationAttempt(app_0, user_0);
        Assert.assertEquals(1, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(1, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_0));
        Assert.assertTrue(queue.getApplications().contains(app_0));
        // Submit second application
        FiCaSchedulerApp app_1 = getMockApplication((APPLICATION_ID++), user_0, Resources.createResource((4 * (TestApplicationLimits.GB)), 0));
        queue.submitApplicationAttempt(app_1, user_0);
        Assert.assertEquals(2, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_0));
        Assert.assertTrue(queue.getApplications().contains(app_1));
        // Submit third application, should remain pending
        FiCaSchedulerApp app_2 = getMockApplication((APPLICATION_ID++), user_0, Resources.createResource((4 * (TestApplicationLimits.GB)), 0));
        queue.submitApplicationAttempt(app_2, user_0);
        Assert.assertEquals(2, queue.getNumActiveApplications());
        Assert.assertEquals(1, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(1, queue.getNumPendingApplications(user_0));
        Assert.assertTrue(queue.getPendingApplications().contains(app_2));
        // Submit fourth application, should remain pending
        FiCaSchedulerApp app_3 = getMockApplication((APPLICATION_ID++), user_0, Resources.createResource((4 * (TestApplicationLimits.GB)), 0));
        queue.submitApplicationAttempt(app_3, user_0);
        Assert.assertEquals(2, queue.getNumActiveApplications());
        Assert.assertEquals(2, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(2, queue.getNumPendingApplications(user_0));
        Assert.assertTrue(queue.getPendingApplications().contains(app_3));
        // Kill 3rd pending application
        queue.finishApplicationAttempt(app_2, TestApplicationLimits.A);
        Assert.assertEquals(2, queue.getNumActiveApplications());
        Assert.assertEquals(1, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(1, queue.getNumPendingApplications(user_0));
        Assert.assertFalse(queue.getPendingApplications().contains(app_2));
        Assert.assertFalse(queue.getApplications().contains(app_2));
        // Finish 1st application, app_3 should become active
        queue.finishApplicationAttempt(app_0, TestApplicationLimits.A);
        Assert.assertEquals(2, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(2, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_0));
        Assert.assertTrue(queue.getApplications().contains(app_3));
        Assert.assertFalse(queue.getPendingApplications().contains(app_3));
        Assert.assertFalse(queue.getApplications().contains(app_0));
        // Finish 2nd application
        queue.finishApplicationAttempt(app_1, TestApplicationLimits.A);
        Assert.assertEquals(1, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(1, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_0));
        Assert.assertFalse(queue.getApplications().contains(app_1));
        // Finish 4th application
        queue.finishApplicationAttempt(app_3, TestApplicationLimits.A);
        Assert.assertEquals(0, queue.getNumActiveApplications());
        Assert.assertEquals(0, queue.getNumPendingApplications());
        Assert.assertEquals(0, queue.getNumActiveApplications(user_0));
        Assert.assertEquals(0, queue.getNumPendingApplications(user_0));
        Assert.assertFalse(queue.getApplications().contains(app_3));
    }

    @Test
    public void testHeadroom() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        csConf.setUserLimit((((ROOT) + ".") + (TestApplicationLimits.A)), 25);
        setupQueueConfiguration(csConf);
        YarnConfiguration conf = new YarnConfiguration();
        CapacitySchedulerContext csContext = Mockito.mock(CapacitySchedulerContext.class);
        Mockito.when(csContext.getConfiguration()).thenReturn(csConf);
        Mockito.when(csContext.getConf()).thenReturn(conf);
        Mockito.when(csContext.getMinimumResourceCapability()).thenReturn(Resources.createResource(TestApplicationLimits.GB));
        Mockito.when(csContext.getMaximumResourceCapability()).thenReturn(Resources.createResource((16 * (TestApplicationLimits.GB))));
        Mockito.when(csContext.getResourceCalculator()).thenReturn(resourceCalculator);
        Mockito.when(csContext.getRMContext()).thenReturn(rmContext);
        Mockito.when(csContext.getPreemptionManager()).thenReturn(new PreemptionManager());
        // Say cluster has 100 nodes of 16G each
        Resource clusterResource = Resources.createResource(((100 * 16) * (TestApplicationLimits.GB)));
        Mockito.when(csContext.getClusterResource()).thenReturn(clusterResource);
        Map<String, CSQueue> queues = new HashMap<String, CSQueue>();
        CSQueue rootQueue = CapacitySchedulerQueueManager.parseQueue(csContext, csConf, null, "root", queues, queues, TestUtils.spyHook);
        rootQueue.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        ResourceUsage queueCapacities = rootQueue.getQueueResourceUsage();
        Mockito.when(csContext.getClusterResourceUsage()).thenReturn(queueCapacities);
        // Manipulate queue 'a'
        LeafQueue queue = TestLeafQueue.stubLeafQueue(((LeafQueue) (queues.get(TestApplicationLimits.A))));
        queue.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        String host_0 = "host_0";
        String rack_0 = "rack_0";
        FiCaSchedulerNode node_0 = TestUtils.getMockNode(host_0, rack_0, 0, (16 * (TestApplicationLimits.GB)));
        final String user_0 = "user_0";
        final String user_1 = "user_1";
        RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);
        RMContext rmContext = TestUtils.getMockRMContext();
        RMContext spyRMContext = Mockito.spy(rmContext);
        ConcurrentMap<ApplicationId, RMApp> spyApps = Mockito.spy(new ConcurrentHashMap<ApplicationId, RMApp>());
        RMApp rmApp = Mockito.mock(RMApp.class);
        ResourceRequest amResourceRequest = Mockito.mock(ResourceRequest.class);
        Resource amResource = Resources.createResource(0, 0);
        Mockito.when(amResourceRequest.getCapability()).thenReturn(amResource);
        Mockito.when(rmApp.getAMResourceRequests()).thenReturn(Collections.singletonList(amResourceRequest));
        Mockito.doReturn(rmApp).when(spyApps).get(ArgumentMatchers.<ApplicationId>any());
        Mockito.when(spyRMContext.getRMApps()).thenReturn(spyApps);
        RMAppAttempt rmAppAttempt = Mockito.mock(RMAppAttempt.class);
        Mockito.when(rmApp.getRMAppAttempt(ArgumentMatchers.any())).thenReturn(rmAppAttempt);
        Mockito.when(rmApp.getCurrentAppAttempt()).thenReturn(rmAppAttempt);
        Mockito.doReturn(rmApp).when(spyApps).get(ArgumentMatchers.<ApplicationId>any());
        Mockito.doReturn(true).when(spyApps).containsKey(ArgumentMatchers.<ApplicationId>any());
        Priority priority_1 = TestUtils.createMockPriority(1);
        // Submit first application with some resource-requests from user_0,
        // and check headroom
        final ApplicationAttemptId appAttemptId_0_0 = TestUtils.getMockApplicationAttemptId(0, 0);
        FiCaSchedulerApp app_0_0 = new FiCaSchedulerApp(appAttemptId_0_0, user_0, queue, queue.getAbstractUsersManager(), spyRMContext);
        queue.submitApplicationAttempt(app_0_0, user_0);
        List<ResourceRequest> app_0_0_requests = new ArrayList<ResourceRequest>();
        app_0_0_requests.add(TestUtils.createResourceRequest(ANY, (1 * (TestApplicationLimits.GB)), 2, true, priority_1, recordFactory));
        app_0_0.updateResourceRequests(app_0_0_requests);
        // Schedule to compute
        queue.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);
        Resource expectedHeadroom = Resources.createResource(((5 * 16) * (TestApplicationLimits.GB)), 1);
        Assert.assertEquals(expectedHeadroom, app_0_0.getHeadroom());
        // Submit second application from user_0, check headroom
        final ApplicationAttemptId appAttemptId_0_1 = TestUtils.getMockApplicationAttemptId(1, 0);
        FiCaSchedulerApp app_0_1 = new FiCaSchedulerApp(appAttemptId_0_1, user_0, queue, queue.getAbstractUsersManager(), spyRMContext);
        queue.submitApplicationAttempt(app_0_1, user_0);
        List<ResourceRequest> app_0_1_requests = new ArrayList<ResourceRequest>();
        app_0_1_requests.add(TestUtils.createResourceRequest(ANY, (1 * (TestApplicationLimits.GB)), 2, true, priority_1, recordFactory));
        app_0_1.updateResourceRequests(app_0_1_requests);
        // Schedule to compute
        queue.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);// Schedule to compute

        Assert.assertEquals(expectedHeadroom, app_0_0.getHeadroom());
        Assert.assertEquals(expectedHeadroom, app_0_1.getHeadroom());// no change

        // Submit first application from user_1, check  for new headroom
        final ApplicationAttemptId appAttemptId_1_0 = TestUtils.getMockApplicationAttemptId(2, 0);
        FiCaSchedulerApp app_1_0 = new FiCaSchedulerApp(appAttemptId_1_0, user_1, queue, queue.getAbstractUsersManager(), spyRMContext);
        queue.submitApplicationAttempt(app_1_0, user_1);
        List<ResourceRequest> app_1_0_requests = new ArrayList<ResourceRequest>();
        app_1_0_requests.add(TestUtils.createResourceRequest(ANY, (1 * (TestApplicationLimits.GB)), 2, true, priority_1, recordFactory));
        app_1_0.updateResourceRequests(app_1_0_requests);
        // Schedule to compute
        queue.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);// Schedule to compute

        expectedHeadroom = Resources.createResource((((10 * 16) * (TestApplicationLimits.GB)) / 2), 1);// changes

        Assert.assertEquals(expectedHeadroom, app_0_0.getHeadroom());
        Assert.assertEquals(expectedHeadroom, app_0_1.getHeadroom());
        Assert.assertEquals(expectedHeadroom, app_1_0.getHeadroom());
        // Now reduce cluster size and check for the smaller headroom
        clusterResource = Resources.createResource(((90 * 16) * (TestApplicationLimits.GB)));
        rootQueue.updateClusterResource(clusterResource, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource));
        // Any change is cluster resource needs to enforce user-limit recomputation.
        // In existing code, LeafQueue#updateClusterResource handled this. However
        // here that method was not used.
        queue.getUsersManager().userLimitNeedsRecompute();
        queue.assignContainers(clusterResource, node_0, new org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceLimits(clusterResource), RESPECT_PARTITION_EXCLUSIVITY);// Schedule to compute

        expectedHeadroom = Resources.createResource((((9 * 16) * (TestApplicationLimits.GB)) / 2), 1);// changes

        Assert.assertEquals(expectedHeadroom, app_0_0.getHeadroom());
        Assert.assertEquals(expectedHeadroom, app_0_1.getHeadroom());
        Assert.assertEquals(expectedHeadroom, app_1_0.getHeadroom());
    }

    @Test(timeout = 120000)
    public void testApplicationLimitSubmit() throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        RMNodeLabelsManager mgr = new NullRMNodeLabelsManager();
        mgr.init(conf);
        // set node -> label
        mgr.addToCluserNodeLabelsWithDefaultExclusivity(ImmutableSet.of("x", "y", "z"));
        // set mapping:
        // h1 -> x
        // h2 -> y
        mgr.addLabelsToNode(ImmutableMap.of(NodeId.newInstance("h1", 0), toSet("x")));
        mgr.addLabelsToNode(ImmutableMap.of(NodeId.newInstance("h2", 0), toSet("y")));
        // inject node label manager
        MockRM rm = new MockRM(getConfigurationWithQueueLabels(conf)) {
            @Override
            public RMNodeLabelsManager createNodeLabelManager() {
                return mgr;
            }
        };
        getRMContext().setNodeLabelManager(mgr);
        start();
        MockNM nm1 = rm.registerNode("h1:1234", 4096);
        MockNM nm2 = rm.registerNode("h2:1234", 4096);
        MockNM nm3 = rm.registerNode("h3:1234", 4096);
        // Submit application to queue c where the default partition capacity is
        // zero
        RMApp app1 = rm.submitApp(TestApplicationLimits.GB, "app", "user", null, "c", false);
        rm.drainEvents();
        rm.waitForState(app1.getApplicationId(), ACCEPTED);
        Assert.assertEquals(ACCEPTED, app1.getState());
        rm.killApp(app1.getApplicationId());
        RMApp app2 = rm.submitApp(TestApplicationLimits.GB, "app", "user", null, "a1", false);
        rm.drainEvents();
        rm.waitForState(app2.getApplicationId(), ACCEPTED);
        Assert.assertEquals(ACCEPTED, app2.getState());
        // Check second application is rejected and based on queue level max
        // application app is rejected
        RMApp app3 = rm.submitApp(TestApplicationLimits.GB, "app", "user", null, "a1", false);
        rm.drainEvents();
        rm.waitForState(app3.getApplicationId(), FAILED);
        Assert.assertEquals(FAILED, app3.getState());
        Assert.assertEquals((("org.apache.hadoop.security.AccessControlException: " + ("Queue root.a.a1 already has 1 applications, cannot accept " + "submission of application: ")) + (app3.getApplicationId())), app3.getDiagnostics().toString());
        // based on Global limit of queue usert application is rejected
        RMApp app11 = rm.submitApp(TestApplicationLimits.GB, "app", "user", null, "d", false);
        rm.drainEvents();
        rm.waitForState(app11.getApplicationId(), ACCEPTED);
        Assert.assertEquals(ACCEPTED, app11.getState());
        RMApp app12 = rm.submitApp(TestApplicationLimits.GB, "app", "user", null, "d", false);
        rm.drainEvents();
        rm.waitForState(app12.getApplicationId(), ACCEPTED);
        Assert.assertEquals(ACCEPTED, app12.getState());
        RMApp app13 = rm.submitApp(TestApplicationLimits.GB, "app", "user", null, "d", false);
        rm.drainEvents();
        rm.waitForState(app13.getApplicationId(), FAILED);
        Assert.assertEquals(FAILED, app13.getState());
        Assert.assertEquals((("org.apache.hadoop.security.AccessControlException: Queue" + (" root.d already has 2 applications from user user cannot" + " accept submission of application: ")) + (app13.getApplicationId())), app13.getDiagnostics().toString());
        // based on system max limit application is rejected
        RMApp app14 = rm.submitApp(TestApplicationLimits.GB, "app", "user2", null, "a2", false);
        rm.drainEvents();
        rm.waitForState(app14.getApplicationId(), ACCEPTED);
        RMApp app15 = rm.submitApp(TestApplicationLimits.GB, "app", "user2", null, "a2", false);
        rm.drainEvents();
        rm.waitForState(app15.getApplicationId(), FAILED);
        Assert.assertEquals(FAILED, app15.getState());
        Assert.assertEquals((("Maximum system application limit reached,cannot" + " accept submission of application: ") + (app15.getApplicationId())), app15.getDiagnostics().toString());
        rm.killApp(app2.getApplicationId());
        rm.killApp(app11.getApplicationId());
        rm.killApp(app13.getApplicationId());
        rm.killApp(app14.getApplicationId());
        stop();
    }
}

