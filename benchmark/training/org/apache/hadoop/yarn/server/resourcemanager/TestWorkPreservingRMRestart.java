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


import CapacitySchedulerConfiguration.ENABLE_USER_METRICS;
import CapacitySchedulerConfiguration.RESOURCE_CALCULATOR_CLASS;
import CapacitySchedulerConfiguration.ROOT;
import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION;
import ContainerState.COMPLETE;
import ContainerState.RUNNING;
import FinalApplicationStatus.SUCCEEDED;
import RMAppAttemptState.FAILED;
import RMAppAttemptState.LAUNCHED;
import RMAppState.ACCEPTED;
import RMAppState.FINISHED;
import RMAppState.KILLED;
import YarnConfiguration.DEFAULT_RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_AM_MAX_ATTEMPTS;
import YarnConfiguration.RM_NM_EXPIRY_INTERVAL_MS;
import YarnConfiguration.RM_SCHEDULER_MAXIMUM_ALLOCATION_MB;
import YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB;
import YarnConfiguration.RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS;
import YarnConfiguration.YARN_ACL_ENABLE;
import YarnConfiguration.YARN_ADMIN_ACL;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import org.apache.hadoop.yarn.server.resourcemanager.placement.ApplicationPlacementContext;
import org.apache.hadoop.yarn.server.resourcemanager.placement.PlacementManager;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore.RMState;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationAttemptStateData;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.records.ApplicationStateData;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystemTestUtil;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.AbstractYarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplication;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.LeafQueue;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.ParentQueue;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.TestCapacitySchedulerAutoCreatedQueueBase;
import org.apache.hadoop.yarn.server.resourcemanager.security.DelegationTokenRenewer;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.util.resource.DominantResourceCalculator;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static CapacitySchedulerConfiguration.PREFIX;
import static org.apache.hadoop.yarn.server.resourcemanager.ParameterizedSchedulerTestBase.SchedulerType.CAPACITY;
import static org.apache.hadoop.yarn.server.resourcemanager.ParameterizedSchedulerTestBase.SchedulerType.FAIR;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class TestWorkPreservingRMRestart extends ParameterizedSchedulerTestBase {
    private YarnConfiguration conf;

    MockRM rm1 = null;

    MockRM rm2 = null;

    public TestWorkPreservingRMRestart(ParameterizedSchedulerTestBase.SchedulerType type) throws IOException {
        super(type);
    }

    // Test common scheduler state including SchedulerAttempt, SchedulerNode,
    // AppSchedulingInfo can be reconstructed via the container recovery reports
    // on NM re-registration.
    // Also test scheduler specific changes: i.e. Queue recovery-
    // CSQueue/FSQueue/FifoQueue recovery respectively.
    // Test Strategy: send 3 container recovery reports(AMContainer, running
    // container, completed container) on NM re-registration, check the states of
    // SchedulerAttempt, SchedulerNode etc. are updated accordingly.
    @Test(timeout = 20000)
    public void testSchedulerRecovery() throws Exception {
        conf.setBoolean(ENABLE_USER_METRICS, true);
        conf.set(RESOURCE_CALCULATOR_CLASS, DominantResourceCalculator.class.getName());
        int containerMemory = 1024;
        Resource containerResource = Resource.newInstance(containerMemory, 1);
        rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        RMApp app1 = rm1.submitApp(200);
        Resource amResources = app1.getAMResourceRequests().get(0).getCapability();
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        // clear queue metrics
        rm1.clearQueueMetrics(app1);
        // Re-start RM
        rm2 = new MockRM(conf, rm1.getRMStateStore());
        start();
        nm1.setResourceTrackerService(getResourceTrackerService());
        // recover app
        RMApp recoveredApp1 = getRMContext().getRMApps().get(app1.getApplicationId());
        RMAppAttempt loadedAttempt1 = recoveredApp1.getCurrentAppAttempt();
        NMContainerStatus amContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 1, RUNNING);
        NMContainerStatus runningContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 2, RUNNING);
        NMContainerStatus completedContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 3, COMPLETE);
        nm1.registerNode(Arrays.asList(amContainer, runningContainer, completedContainer), null);
        // Wait for RM to settle down on recovering containers;
        TestWorkPreservingRMRestart.waitForNumContainersToRecover(2, rm2, am1.getApplicationAttemptId());
        Set<ContainerId> launchedContainers = getLaunchedContainers();
        Assert.assertTrue(launchedContainers.contains(amContainer.getContainerId()));
        Assert.assertTrue(launchedContainers.contains(runningContainer.getContainerId()));
        // check RMContainers are re-recreated and the container state is correct.
        rm2.waitForState(nm1, amContainer.getContainerId(), RMContainerState.RUNNING);
        rm2.waitForState(nm1, runningContainer.getContainerId(), RMContainerState.RUNNING);
        rm2.waitForContainerToComplete(loadedAttempt1, completedContainer);
        AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
        SchedulerNode schedulerNode1 = scheduler.getSchedulerNode(nm1.getNodeId());
        Assert.assertTrue("SchedulerNode#toString is not in expected format", schedulerNode1.toString().contains(schedulerNode1.getUnallocatedResource().toString()));
        Assert.assertTrue("SchedulerNode#toString is not in expected format", schedulerNode1.toString().contains(schedulerNode1.getAllocatedResource().toString()));
        // ********* check scheduler node state.*******
        // 2 running containers.
        Resource usedResources = Resources.multiply(containerResource, 2);
        Resource nmResource = Resource.newInstance(nm1.getMemory(), nm1.getvCores());
        Assert.assertTrue(schedulerNode1.isValidContainer(amContainer.getContainerId()));
        Assert.assertTrue(schedulerNode1.isValidContainer(runningContainer.getContainerId()));
        Assert.assertFalse(schedulerNode1.isValidContainer(completedContainer.getContainerId()));
        // 2 launched containers, 1 completed container
        Assert.assertEquals(2, schedulerNode1.getNumContainers());
        Assert.assertEquals(Resources.subtract(nmResource, usedResources), schedulerNode1.getUnallocatedResource());
        Assert.assertEquals(usedResources, schedulerNode1.getAllocatedResource());
        Resource availableResources = Resources.subtract(nmResource, usedResources);
        // ***** check queue state based on the underlying scheduler ********
        Map<ApplicationId, SchedulerApplication> schedulerApps = getSchedulerApplications();
        SchedulerApplication schedulerApp = schedulerApps.get(recoveredApp1.getApplicationId());
        if ((getSchedulerType()) == (CAPACITY)) {
            checkCSQueue(rm2, schedulerApp, nmResource, nmResource, usedResources, 2);
        } else {
            checkFSQueue(rm2, schedulerApp, usedResources, availableResources, amResources);
        }
        // *********** check scheduler attempt state.********
        SchedulerApplicationAttempt schedulerAttempt = schedulerApp.getCurrentAppAttempt();
        Assert.assertTrue(schedulerAttempt.getLiveContainers().contains(scheduler.getRMContainer(amContainer.getContainerId())));
        Assert.assertTrue(schedulerAttempt.getLiveContainers().contains(scheduler.getRMContainer(runningContainer.getContainerId())));
        Assert.assertEquals(schedulerAttempt.getCurrentConsumption(), usedResources);
        // *********** check appSchedulingInfo state ***********
        Assert.assertEquals(((1L << 40) + 1L), schedulerAttempt.getNewContainerId());
    }

    // Test work preserving recovery of apps running under reservation.
    // This involves:
    // 1. Setting up a dynamic reservable queue,
    // 2. Submitting an app to it,
    // 3. Failing over RM,
    // 4. Validating that the app is recovered post failover,
    // 5. Check if all running containers are recovered,
    // 6. Verify the scheduler state like attempt info,
    // 7. Verify the queue/user metrics for the dynamic reservable queue.
    @Test(timeout = 30000)
    public void testDynamicQueueRecovery() throws Exception {
        conf.setBoolean(ENABLE_USER_METRICS, true);
        conf.set(RESOURCE_CALCULATOR_CLASS, DominantResourceCalculator.class.getName());
        // 1. Set up dynamic reservable queue.
        Configuration schedulerConf = getSchedulerDynamicConfiguration();
        int containerMemory = 1024;
        Resource containerResource = Resource.newInstance(containerMemory, 1);
        rm1 = new MockRM(schedulerConf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        // 2. Run plan follower to update the added node & then submit app to
        // dynamic queue.
        getRMContext().getReservationSystem().synchronizePlan(ReservationSystemTestUtil.reservationQ, true);
        RMApp app1 = rm1.submitApp(200, "dynamicQApp", UserGroupInformation.getCurrentUser().getShortUserName(), null, ReservationSystemTestUtil.getReservationQueueName());
        Resource amResources = app1.getAMResourceRequests().get(0).getCapability();
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        // clear queue metrics
        rm1.clearQueueMetrics(app1);
        // 3. Fail over (restart) RM.
        rm2 = new MockRM(schedulerConf, rm1.getRMStateStore());
        start();
        nm1.setResourceTrackerService(getResourceTrackerService());
        // 4. Validate app is recovered post failover.
        RMApp recoveredApp1 = getRMContext().getRMApps().get(app1.getApplicationId());
        RMAppAttempt loadedAttempt1 = recoveredApp1.getCurrentAppAttempt();
        NMContainerStatus amContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 1, RUNNING);
        NMContainerStatus runningContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 2, RUNNING);
        NMContainerStatus completedContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 3, COMPLETE);
        nm1.registerNode(Arrays.asList(amContainer, runningContainer, completedContainer), null);
        // Wait for RM to settle down on recovering containers.
        TestWorkPreservingRMRestart.waitForNumContainersToRecover(2, rm2, am1.getApplicationAttemptId());
        Set<ContainerId> launchedContainers = getLaunchedContainers();
        Assert.assertTrue(launchedContainers.contains(amContainer.getContainerId()));
        Assert.assertTrue(launchedContainers.contains(runningContainer.getContainerId()));
        // 5. Check RMContainers are re-recreated and the container state is
        // correct.
        rm2.waitForState(nm1, amContainer.getContainerId(), RMContainerState.RUNNING);
        rm2.waitForState(nm1, runningContainer.getContainerId(), RMContainerState.RUNNING);
        rm2.waitForContainerToComplete(loadedAttempt1, completedContainer);
        AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
        SchedulerNode schedulerNode1 = scheduler.getSchedulerNode(nm1.getNodeId());
        // ********* check scheduler node state.*******
        // 2 running containers.
        Resource usedResources = Resources.multiply(containerResource, 2);
        Resource nmResource = Resource.newInstance(nm1.getMemory(), nm1.getvCores());
        Assert.assertTrue(schedulerNode1.isValidContainer(amContainer.getContainerId()));
        Assert.assertTrue(schedulerNode1.isValidContainer(runningContainer.getContainerId()));
        Assert.assertFalse(schedulerNode1.isValidContainer(completedContainer.getContainerId()));
        // 2 launched containers, 1 completed container
        Assert.assertEquals(2, schedulerNode1.getNumContainers());
        Assert.assertEquals(Resources.subtract(nmResource, usedResources), schedulerNode1.getUnallocatedResource());
        Assert.assertEquals(usedResources, schedulerNode1.getAllocatedResource());
        Resource availableResources = Resources.subtract(nmResource, usedResources);
        // 6. Verify the scheduler state like attempt info.
        Map<ApplicationId, SchedulerApplication<SchedulerApplicationAttempt>> sa = getSchedulerApplications();
        SchedulerApplication<SchedulerApplicationAttempt> schedulerApp = sa.get(recoveredApp1.getApplicationId());
        // 7. Verify the queue/user metrics for the dynamic reservable queue.
        if ((getSchedulerType()) == (CAPACITY)) {
            checkCSQueue(rm2, schedulerApp, nmResource, nmResource, usedResources, 2);
        } else {
            checkFSQueue(rm2, schedulerApp, usedResources, availableResources, amResources);
        }
        // *********** check scheduler attempt state.********
        SchedulerApplicationAttempt schedulerAttempt = schedulerApp.getCurrentAppAttempt();
        Assert.assertTrue(schedulerAttempt.getLiveContainers().contains(scheduler.getRMContainer(amContainer.getContainerId())));
        Assert.assertTrue(schedulerAttempt.getLiveContainers().contains(scheduler.getRMContainer(runningContainer.getContainerId())));
        Assert.assertEquals(schedulerAttempt.getCurrentConsumption(), usedResources);
        // *********** check appSchedulingInfo state ***********
        Assert.assertEquals(((1L << 40) + 1L), schedulerAttempt.getNewContainerId());
    }

    private static final String R = "Default";

    private static final String A = "QueueA";

    private static final String B = "QueueB";

    private static final String B1 = "QueueB1";

    private static final String B2 = "QueueB2";

    // don't ever create the below queue ;-)
    private static final String QUEUE_DOESNT_EXIST = "NoSuchQueue";

    private static final String USER_1 = "user1";

    private static final String USER_2 = "user2";

    // 1. submit an app to default queue and let it finish
    // 2. restart rm with no default queue
    // 3. getApplicationReport call should succeed (with no NPE)
    @Test(timeout = 30000)
    public void testRMRestartWithRemovedQueue() throws Exception {
        conf.setBoolean(YARN_ACL_ENABLE, true);
        conf.set(YARN_ADMIN_ACL, "");
        rm1 = new MockRM(conf);
        start();
        MockMemoryRMStateStore memStore = ((MockMemoryRMStateStore) (rm1.getRMStateStore()));
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        final RMApp app1 = rm1.submitApp(1024, "app1", TestWorkPreservingRMRestart.USER_1, null);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        MockRM.finishAMAndVerifyAppState(app1, rm1, nm1, am1);
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        csConf.setQueues(ROOT, new String[]{ TestWorkPreservingRMRestart.QUEUE_DOESNT_EXIST });
        final String noQueue = ((CapacitySchedulerConfiguration.ROOT) + ".") + (TestWorkPreservingRMRestart.QUEUE_DOESNT_EXIST);
        csConf.setCapacity(noQueue, 100);
        rm2 = new MockRM(csConf, memStore);
        start();
        UserGroupInformation user2 = UserGroupInformation.createRemoteUser("user2");
        ApplicationReport report = user2.doAs(new PrivilegedExceptionAction<ApplicationReport>() {
            @Override
            public ApplicationReport run() throws Exception {
                return rm2.getApplicationReport(app1.getApplicationId());
            }
        });
        Assert.assertNotNull(report);
    }

    // Test CS recovery with multi-level queues and multi-users:
    // 1. setup 2 NMs each with 8GB memory;
    // 2. setup 2 level queues: Default -> (QueueA, QueueB)
    // 3. User1 submits 2 apps on QueueA
    // 4. User2 submits 1 app  on QueueB
    // 5. AM and each container has 1GB memory
    // 6. Restart RM.
    // 7. nm1 re-syncs back containers belong to user1
    // 8. nm2 re-syncs back containers belong to user2.
    // 9. Assert the parent queue and 2 leaf queues state and the metrics.
    // 10. Assert each user's consumption inside the queue.
    @Test(timeout = 30000)
    public void testCapacitySchedulerRecovery() throws Exception {
        if ((getSchedulerType()) != (CAPACITY)) {
            return;
        }
        conf.setBoolean(ENABLE_USER_METRICS, true);
        conf.set(RESOURCE_CALCULATOR_CLASS, DominantResourceCalculator.class.getName());
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfiguration(csConf);
        rm1 = new MockRM(csConf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        MockNM nm2 = new MockNM("127.1.1.1:4321", 8192, getResourceTrackerService());
        nm1.registerNode();
        nm2.registerNode();
        RMApp app1_1 = rm1.submitApp(1024, "app1_1", TestWorkPreservingRMRestart.USER_1, null, TestWorkPreservingRMRestart.A);
        MockAM am1_1 = MockRM.launchAndRegisterAM(app1_1, rm1, nm1);
        RMApp app1_2 = rm1.submitApp(1024, "app1_2", TestWorkPreservingRMRestart.USER_1, null, TestWorkPreservingRMRestart.A);
        MockAM am1_2 = MockRM.launchAndRegisterAM(app1_2, rm1, nm2);
        RMApp app2 = rm1.submitApp(1024, "app2", TestWorkPreservingRMRestart.USER_2, null, TestWorkPreservingRMRestart.B);
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm1, nm2);
        // clear queue metrics
        rm1.clearQueueMetrics(app1_1);
        rm1.clearQueueMetrics(app1_2);
        rm1.clearQueueMetrics(app2);
        csConf.set(((PREFIX) + "root.Default.QueueB.state"), "STOPPED");
        // Re-start RM
        rm2 = new MockRM(csConf, rm1.getRMStateStore());
        start();
        nm1.setResourceTrackerService(getResourceTrackerService());
        nm2.setResourceTrackerService(getResourceTrackerService());
        List<NMContainerStatus> am1_1Containers = TestWorkPreservingRMRestart.createNMContainerStatusForApp(am1_1);
        List<NMContainerStatus> am1_2Containers = TestWorkPreservingRMRestart.createNMContainerStatusForApp(am1_2);
        am1_1Containers.addAll(am1_2Containers);
        nm1.registerNode(am1_1Containers, null);
        List<NMContainerStatus> am2Containers = TestWorkPreservingRMRestart.createNMContainerStatusForApp(am2);
        nm2.registerNode(am2Containers, null);
        // Wait for RM to settle down on recovering containers;
        TestWorkPreservingRMRestart.waitForNumContainersToRecover(2, rm2, am1_1.getApplicationAttemptId());
        TestWorkPreservingRMRestart.waitForNumContainersToRecover(2, rm2, am1_2.getApplicationAttemptId());
        TestWorkPreservingRMRestart.waitForNumContainersToRecover(2, rm2, am2.getApplicationAttemptId());
        // Calculate each queue's resource usage.
        Resource containerResource = Resource.newInstance(1024, 1);
        Resource nmResource = Resource.newInstance(nm1.getMemory(), nm1.getvCores());
        Resource clusterResource = Resources.multiply(nmResource, 2);
        Resource q1Resource = Resources.multiply(clusterResource, 0.5);
        Resource q2Resource = Resources.multiply(clusterResource, 0.5);
        Resource q1UsedResource = Resources.multiply(containerResource, 4);
        Resource q2UsedResource = Resources.multiply(containerResource, 2);
        Resource totalUsedResource = Resources.add(q1UsedResource, q2UsedResource);
        Resource q1availableResources = Resources.subtract(q1Resource, q1UsedResource);
        Resource q2availableResources = Resources.subtract(q2Resource, q2UsedResource);
        Resource totalAvailableResource = Resources.add(q1availableResources, q2availableResources);
        Map<ApplicationId, SchedulerApplication> schedulerApps = getSchedulerApplications();
        SchedulerApplication schedulerApp1_1 = schedulerApps.get(app1_1.getApplicationId());
        // assert queue A state.
        checkCSLeafQueue(rm2, schedulerApp1_1, clusterResource, q1Resource, q1UsedResource, 4);
        QueueMetrics queue1Metrics = schedulerApp1_1.getQueue().getMetrics();
        assertMetrics(queue1Metrics, 2, 0, 2, 0, 4, q1availableResources.getMemorySize(), q1availableResources.getVirtualCores(), q1UsedResource.getMemorySize(), q1UsedResource.getVirtualCores());
        // assert queue B state.
        SchedulerApplication schedulerApp2 = schedulerApps.get(app2.getApplicationId());
        checkCSLeafQueue(rm2, schedulerApp2, clusterResource, q2Resource, q2UsedResource, 2);
        QueueMetrics queue2Metrics = schedulerApp2.getQueue().getMetrics();
        assertMetrics(queue2Metrics, 1, 0, 1, 0, 2, q2availableResources.getMemorySize(), q2availableResources.getVirtualCores(), q2UsedResource.getMemorySize(), q2UsedResource.getVirtualCores());
        // assert parent queue state.
        LeafQueue leafQueue = ((LeafQueue) (schedulerApp2.getQueue()));
        ParentQueue parentQueue = ((ParentQueue) (leafQueue.getParent()));
        checkParentQueue(parentQueue, 6, totalUsedResource, (((float) (6)) / 16), (((float) (6)) / 16));
        assertMetrics(parentQueue.getMetrics(), 3, 0, 3, 0, 6, totalAvailableResource.getMemorySize(), totalAvailableResource.getVirtualCores(), totalUsedResource.getMemorySize(), totalUsedResource.getVirtualCores());
    }

    // Test behavior of an app if queue is changed from leaf to parent during
    // recovery. Test case does following:
    // 1. Add an app to QueueB and start the attempt.
    // 2. Add 2 subqueues(QueueB1 and QueueB2) to QueueB, restart the RM, once with
    // fail fast config as false and once with fail fast as true.
    // 3. Verify that app was killed if fail fast is false.
    // 4. Verify that QueueException was thrown if fail fast is true.
    @Test(timeout = 30000)
    public void testCapacityLeafQueueBecomesParentOnRecovery() throws Exception {
        if ((getSchedulerType()) != (CAPACITY)) {
            return;
        }
        conf.setBoolean(ENABLE_USER_METRICS, true);
        conf.set(RESOURCE_CALCULATOR_CLASS, DominantResourceCalculator.class.getName());
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfiguration(csConf);
        rm1 = new MockRM(csConf);
        start();
        MockMemoryRMStateStore memStore = ((MockMemoryRMStateStore) (rm1.getRMStateStore()));
        MockNM nm = new MockNM("127.1.1.1:4321", 8192, getResourceTrackerService());
        nm.registerNode();
        // Submit an app to QueueB.
        RMApp app = rm1.submitApp(1024, "app", TestWorkPreservingRMRestart.USER_2, null, TestWorkPreservingRMRestart.B);
        MockRM.launchAndRegisterAM(app, rm1, nm);
        Assert.assertEquals(rm1.getApplicationReport(app.getApplicationId()).getYarnApplicationState(), YarnApplicationState.RUNNING);
        // Take a copy of state store so that it can be reset to this state.
        RMState state = rm1.getRMStateStore().loadState();
        // Change scheduler config with child queues added to QueueB.
        csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfigurationChildOfB(csConf);
        String diags = "Application killed on recovery as it was submitted to " + "queue QueueB which is no longer a leaf queue after restart.";
        verifyAppRecoveryWithWrongQueueConfig(csConf, app, diags, memStore, state);
    }

    // Test behavior of an app if queue is removed during recovery. Test case does
    // following:
    // 1. Add some apps to two queues, attempt to add an app to a non-existant
    // queue to verify that the new logic is not in effect during normal app
    // submission
    // 2. Remove one of the queues, restart the RM, once with fail fast config as
    // false and once with fail fast as true.
    // 3. Verify that app was killed if fail fast is false.
    // 4. Verify that QueueException was thrown if fail fast is true.
    @Test(timeout = 30000)
    public void testCapacitySchedulerQueueRemovedRecovery() throws Exception {
        if ((getSchedulerType()) != (CAPACITY)) {
            return;
        }
        conf.setBoolean(ENABLE_USER_METRICS, true);
        conf.set(RESOURCE_CALCULATOR_CLASS, DominantResourceCalculator.class.getName());
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfiguration(csConf);
        rm1 = new MockRM(csConf);
        start();
        MockMemoryRMStateStore memStore = ((MockMemoryRMStateStore) (rm1.getRMStateStore()));
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        MockNM nm2 = new MockNM("127.1.1.1:4321", 8192, getResourceTrackerService());
        nm1.registerNode();
        nm2.registerNode();
        RMApp app1_1 = rm1.submitApp(1024, "app1_1", TestWorkPreservingRMRestart.USER_1, null, TestWorkPreservingRMRestart.A);
        MockAM am1_1 = MockRM.launchAndRegisterAM(app1_1, rm1, nm1);
        RMApp app1_2 = rm1.submitApp(1024, "app1_2", TestWorkPreservingRMRestart.USER_1, null, TestWorkPreservingRMRestart.A);
        MockAM am1_2 = MockRM.launchAndRegisterAM(app1_2, rm1, nm2);
        RMApp app2 = rm1.submitApp(1024, "app2", TestWorkPreservingRMRestart.USER_2, null, TestWorkPreservingRMRestart.B);
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm1, nm2);
        Assert.assertEquals(rm1.getApplicationReport(app2.getApplicationId()).getYarnApplicationState(), YarnApplicationState.RUNNING);
        // Submit an app with a non existant queue to make sure it does not
        // cause a fatal failure in the non-recovery case
        RMApp appNA = rm1.submitApp(1024, "app1_2", TestWorkPreservingRMRestart.USER_1, null, TestWorkPreservingRMRestart.QUEUE_DOESNT_EXIST, false);
        // clear queue metrics
        rm1.clearQueueMetrics(app1_1);
        rm1.clearQueueMetrics(app1_2);
        rm1.clearQueueMetrics(app2);
        // Take a copy of state store so that it can be reset to this state.
        RMState state = memStore.loadState();
        // Set new configuration with QueueB removed.
        csConf = new CapacitySchedulerConfiguration(conf);
        setupQueueConfigurationOnlyA(csConf);
        String diags = "Application killed on recovery as it was submitted to " + "queue QueueB which no longer exists after restart.";
        verifyAppRecoveryWithWrongQueueConfig(csConf, app2, diags, memStore, state);
    }

    // Test RM shuts down, in the meanwhile, AM fails. Restarted RM scheduler
    // should not recover the containers that belong to the failed AM.
    @Test(timeout = 20000)
    public void testAMfailedBetweenRMRestart() throws Exception {
        conf.setLong(RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS, 0);
        rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        RMApp app1 = rm1.submitApp(200);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        rm2 = new MockRM(conf, rm1.getRMStateStore());
        start();
        nm1.setResourceTrackerService(getResourceTrackerService());
        NMContainerStatus amContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 1, COMPLETE);
        NMContainerStatus runningContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 2, RUNNING);
        NMContainerStatus completedContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 3, COMPLETE);
        nm1.registerNode(Arrays.asList(amContainer, runningContainer, completedContainer), null);
        rm2.waitForState(am1.getApplicationAttemptId(), FAILED);
        // Wait for RM to settle down on recovering containers;
        Thread.sleep(3000);
        YarnScheduler scheduler = rm2.getResourceScheduler();
        // Previous AM failed, The failed AM should once again release the
        // just-recovered containers.
        Assert.assertNull(scheduler.getRMContainer(runningContainer.getContainerId()));
        Assert.assertNull(scheduler.getRMContainer(completedContainer.getContainerId()));
        rm2.waitForNewAMToLaunchAndRegister(app1.getApplicationId(), 2, nm1);
        MockNM nm2 = new MockNM("127.1.1.1:4321", 8192, getResourceTrackerService());
        NMContainerStatus previousAttemptContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 4, RUNNING);
        nm2.registerNode(Arrays.asList(previousAttemptContainer), null);
        // Wait for RM to settle down on recovering containers;
        Thread.sleep(3000);
        // check containers from previous failed attempt should not be recovered.
        Assert.assertNull(scheduler.getRMContainer(previousAttemptContainer.getContainerId()));
    }

    // Apps already completed before RM restart. Restarted RM scheduler should not
    // recover containers for completed apps.
    @Test(timeout = 20000)
    public void testContainersNotRecoveredForCompletedApps() throws Exception {
        rm1 = new MockRM(conf);
        start();
        MockMemoryRMStateStore memStore = ((MockMemoryRMStateStore) (rm1.getRMStateStore()));
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        RMApp app1 = rm1.submitApp(200);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        MockRM.finishAMAndVerifyAppState(app1, rm1, nm1, am1);
        rm2 = new MockRM(conf, memStore);
        start();
        nm1.setResourceTrackerService(getResourceTrackerService());
        NMContainerStatus runningContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 2, RUNNING);
        NMContainerStatus completedContainer = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 3, COMPLETE);
        nm1.registerNode(Arrays.asList(runningContainer, completedContainer), null);
        RMApp recoveredApp1 = getRMContext().getRMApps().get(app1.getApplicationId());
        Assert.assertEquals(FINISHED, recoveredApp1.getState());
        // Wait for RM to settle down on recovering containers;
        Thread.sleep(3000);
        YarnScheduler scheduler = rm2.getResourceScheduler();
        // scheduler should not recover containers for finished apps.
        Assert.assertNull(scheduler.getRMContainer(runningContainer.getContainerId()));
        Assert.assertNull(scheduler.getRMContainer(completedContainer.getContainerId()));
    }

    @Test(timeout = 600000)
    public void testAppReregisterOnRMWorkPreservingRestart() throws Exception {
        conf.setInt(RM_AM_MAX_ATTEMPTS, 1);
        // start RM
        rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // create app and launch the AM
        RMApp app0 = rm1.submitApp(200);
        MockAM am0 = MockRM.launchAM(app0, rm1, nm1);
        // Issuing registerAppAttempt() before and after RM restart to confirm
        // registerApplicationMaster() is idempotent.
        am0.registerAppAttempt();
        // start new RM
        rm2 = new MockRM(conf, rm1.getRMStateStore());
        start();
        rm2.waitForState(app0.getApplicationId(), ACCEPTED);
        rm2.waitForState(am0.getApplicationAttemptId(), LAUNCHED);
        am0.setAMRMProtocol(getApplicationMasterService(), getRMContext());
        // retry registerApplicationMaster() after RM restart.
        am0.registerAppAttempt(true);
        rm2.waitForState(app0.getApplicationId(), RMAppState.RUNNING);
        rm2.waitForState(am0.getApplicationAttemptId(), RMAppAttemptState.RUNNING);
    }

    @Test(timeout = 30000)
    public void testAMContainerStatusWithRMRestart() throws Exception {
        rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        RMApp app1_1 = rm1.submitApp(1024);
        MockAM am1_1 = MockRM.launchAndRegisterAM(app1_1, rm1, nm1);
        RMAppAttempt attempt0 = app1_1.getCurrentAppAttempt();
        YarnScheduler scheduler = rm1.getResourceScheduler();
        Assert.assertTrue(scheduler.getRMContainer(attempt0.getMasterContainer().getId()).isAMContainer());
        // Re-start RM
        rm2 = new MockRM(conf, rm1.getRMStateStore());
        start();
        nm1.setResourceTrackerService(getResourceTrackerService());
        List<NMContainerStatus> am1_1Containers = TestWorkPreservingRMRestart.createNMContainerStatusForApp(am1_1);
        nm1.registerNode(am1_1Containers, null);
        // Wait for RM to settle down on recovering containers;
        TestWorkPreservingRMRestart.waitForNumContainersToRecover(2, rm2, am1_1.getApplicationAttemptId());
        scheduler = getResourceScheduler();
        Assert.assertTrue(scheduler.getRMContainer(attempt0.getMasterContainer().getId()).isAMContainer());
    }

    @Test(timeout = 20000)
    public void testRecoverSchedulerAppAndAttemptSynchronously() throws Exception {
        // start RM
        rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // create app and launch the AM
        RMApp app0 = rm1.submitApp(200);
        MockAM am0 = MockRM.launchAndRegisterAM(app0, rm1, nm1);
        rm2 = new MockRM(conf, rm1.getRMStateStore());
        start();
        nm1.setResourceTrackerService(getResourceTrackerService());
        // scheduler app/attempt is immediately available after RM is re-started.
        Assert.assertNotNull(getResourceScheduler().getSchedulerAppInfo(am0.getApplicationAttemptId()));
        // getTransferredContainers should not throw NPE.
        getResourceScheduler().getTransferredContainers(am0.getApplicationAttemptId());
        List<NMContainerStatus> containers = TestWorkPreservingRMRestart.createNMContainerStatusForApp(am0);
        nm1.registerNode(containers, null);
        TestWorkPreservingRMRestart.waitForNumContainersToRecover(2, rm2, am0.getApplicationAttemptId());
    }

    // Test if RM on recovery receives the container release request from AM
    // before it receives the container status reported by NM for recovery. this
    // container should not be recovered.
    @Test(timeout = 50000)
    public void testReleasedContainerNotRecovered() throws Exception {
        rm1 = new MockRM(conf);
        MockNM nm1 = new MockNM("h1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        start();
        RMApp app1 = rm1.submitApp(1024);
        final MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        // Re-start RM
        conf.setInt(RM_NM_EXPIRY_INTERVAL_MS, 8000);
        rm2 = new MockRM(conf, rm1.getRMStateStore());
        start();
        nm1.setResourceTrackerService(getResourceTrackerService());
        rm2.waitForState(app1.getApplicationId(), ACCEPTED);
        am1.setAMRMProtocol(getApplicationMasterService(), getRMContext());
        am1.registerAppAttempt(true);
        // try to release a container before the container is actually recovered.
        final ContainerId runningContainer = ContainerId.newContainerId(am1.getApplicationAttemptId(), 2);
        am1.allocate(null, Arrays.asList(runningContainer));
        // send container statuses to recover the containers
        List<NMContainerStatus> containerStatuses = TestWorkPreservingRMRestart.createNMContainerStatusForApp(am1);
        nm1.registerNode(containerStatuses, null);
        // only the am container should be recovered.
        TestWorkPreservingRMRestart.waitForNumContainersToRecover(1, rm2, am1.getApplicationAttemptId());
        final AbstractYarnScheduler scheduler = ((AbstractYarnScheduler) (getResourceScheduler()));
        // cached release request is cleaned.
        // assertFalse(scheduler.getPendingRelease().contains(runningContainer));
        AllocateResponse response = am1.allocate(null, null);
        // AM gets notified of the completed container.
        boolean receivedCompletedContainer = false;
        for (ContainerStatus status : response.getCompletedContainersStatuses()) {
            if (status.getContainerId().equals(runningContainer)) {
                receivedCompletedContainer = true;
            }
        }
        Assert.assertTrue(receivedCompletedContainer);
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            public Boolean get() {
                // release cache is cleaned up and previous running container is not
                // recovered
                return (scheduler.getApplicationAttempt(am1.getApplicationAttemptId()).getPendingRelease().isEmpty()) && ((scheduler.getRMContainer(runningContainer)) == null);
            }
        }, 1000, 20000);
    }

    @Test(timeout = 20000)
    public void testNewContainersNotAllocatedDuringSchedulerRecovery() throws Exception {
        conf.setLong(RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS, 4000);
        rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        RMApp app1 = rm1.submitApp(200);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        // Restart RM
        rm2 = new MockRM(conf, rm1.getRMStateStore());
        start();
        nm1.setResourceTrackerService(getResourceTrackerService());
        nm1.registerNode();
        ControlledClock clock = new ControlledClock();
        long startTime = System.currentTimeMillis();
        ((RMContextImpl) (getRMContext())).setSystemClock(clock);
        am1.setAMRMProtocol(getApplicationMasterService(), getRMContext());
        am1.registerAppAttempt(true);
        rm2.waitForState(app1.getApplicationId(), RMAppState.RUNNING);
        // AM request for new containers
        am1.allocate("127.0.0.1", 1000, 1, new ArrayList<ContainerId>());
        List<Container> containers = new ArrayList<Container>();
        clock.setTime((startTime + 2000));
        nm1.nodeHeartbeat(true);
        // sleep some time as allocation happens asynchronously.
        Thread.sleep(3000);
        containers.addAll(am1.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers());
        // container is not allocated during scheduling recovery.
        Assert.assertTrue(containers.isEmpty());
        clock.setTime((startTime + 8000));
        nm1.nodeHeartbeat(true);
        // Container is created after recovery is done.
        while (containers.isEmpty()) {
            containers.addAll(am1.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers());
            Thread.sleep(500);
        } 
    }

    /**
     * Testing to confirm that retried finishApplicationMaster() doesn't throw
     * InvalidApplicationMasterRequest before and after RM restart.
     */
    @Test(timeout = 20000)
    public void testRetriedFinishApplicationMasterRequest() throws Exception {
        conf.setInt(RM_AM_MAX_ATTEMPTS, 1);
        // start RM
        rm1 = new MockRM(conf);
        start();
        MockMemoryRMStateStore memStore = ((MockMemoryRMStateStore) (rm1.getRMStateStore()));
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // create app and launch the AM
        RMApp app0 = rm1.submitApp(200);
        MockAM am0 = MockRM.launchAM(app0, rm1, nm1);
        am0.registerAppAttempt();
        // Emulating following a scenario:
        // RM1 saves the app in RMStateStore and then crashes,
        // FinishApplicationMasterResponse#isRegistered still return false,
        // so AM still retry the 2nd RM
        MockRM.finishAMAndVerifyAppState(app0, rm1, nm1, am0);
        // start new RM
        rm2 = new MockRM(conf, memStore);
        start();
        am0.setAMRMProtocol(getApplicationMasterService(), getRMContext());
        am0.unregisterAppAttempt(false);
    }

    @Test(timeout = 30000)
    public void testAppFailedToRenewTokenOnRecovery() throws Exception {
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        conf.setInt(RM_AM_MAX_ATTEMPTS, 1);
        UserGroupInformation.setConfiguration(conf);
        MockRM rm1 = new TestRMRestart.TestSecurityMockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        RMApp app1 = rm1.submitApp(200);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        MockRM rm2 = new TestRMRestart.TestSecurityMockRM(conf, rm1.getRMStateStore()) {
            protected DelegationTokenRenewer createDelegationTokenRenewer() {
                return new DelegationTokenRenewer() {
                    @Override
                    public void addApplicationSync(ApplicationId applicationId, Credentials ts, boolean shouldCancelAtEnd, String user) throws IOException {
                        throw new IOException("Token renew failed !!");
                    }
                };
            }
        };
        nm1.setResourceTrackerService(getResourceTrackerService());
        start();
        NMContainerStatus containerStatus = TestRMRestart.createNMContainerStatus(am1.getApplicationAttemptId(), 1, RUNNING);
        nm1.registerNode(Arrays.asList(containerStatus), null);
        // am re-register
        rm2.waitForState(app1.getApplicationId(), ACCEPTED);
        am1.setAMRMProtocol(getApplicationMasterService(), getRMContext());
        am1.registerAppAttempt(true);
        rm2.waitForState(app1.getApplicationId(), RMAppState.RUNNING);
        // Because the token expired, am could crash.
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 1, COMPLETE);
        rm2.waitForState(am1.getApplicationAttemptId(), FAILED);
        rm2.waitForState(app1.getApplicationId(), RMAppState.FAILED);
    }

    /**
     * Test validateAndCreateResourceRequest fails on recovery, app should ignore
     * this Exception and continue
     */
    @Test(timeout = 30000)
    public void testAppFailToValidateResourceRequestOnRecovery() throws Exception {
        rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        RMApp app1 = rm1.submitApp(200);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        // Change the config so that validateAndCreateResourceRequest throws
        // exception on recovery
        conf.setInt(RM_SCHEDULER_MINIMUM_ALLOCATION_MB, 50);
        conf.setInt(RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, 100);
        rm2 = new MockRM(conf, rm1.getRMStateStore());
        nm1.setResourceTrackerService(getResourceTrackerService());
        start();
    }

    @Test(timeout = 20000)
    public void testContainerCompleteMsgNotLostAfterAMFailedAndRMRestart() throws Exception {
        rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        // submit app with keepContainersAcrossApplicationAttempts true
        Resource resource = Records.newRecord(Resource.class);
        resource.setMemorySize(200);
        RMApp app0 = rm1.submitApp(resource, "", UserGroupInformation.getCurrentUser().getShortUserName(), null, false, null, DEFAULT_RM_AM_MAX_ATTEMPTS, null, null, true, true, false, null, 0, null, true, null);
        MockAM am0 = MockRM.launchAndRegisterAM(app0, rm1, nm1);
        am0.allocate("127.0.0.1", 1000, 2, new ArrayList<ContainerId>());
        nm1.nodeHeartbeat(true);
        List<Container> conts = am0.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
        while ((conts.size()) < 2) {
            nm1.nodeHeartbeat(true);
            conts.addAll(am0.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers());
            Thread.sleep(100);
        } 
        // am failed,and relaunch it
        nm1.nodeHeartbeat(am0.getApplicationAttemptId(), 1, COMPLETE);
        rm1.waitForState(app0.getApplicationId(), ACCEPTED);
        MockAM am1 = MockRM.launchAndRegisterAM(app0, rm1, nm1);
        // rm failover
        rm2 = new MockRM(conf, rm1.getRMStateStore());
        start();
        nm1.setResourceTrackerService(getResourceTrackerService());
        // container launched by first am completed
        NMContainerStatus amContainer = TestRMRestart.createNMContainerStatus(am0.getApplicationAttemptId(), 1, RUNNING);
        NMContainerStatus completedContainer = TestRMRestart.createNMContainerStatus(am0.getApplicationAttemptId(), 2, COMPLETE);
        NMContainerStatus runningContainer = TestRMRestart.createNMContainerStatus(am0.getApplicationAttemptId(), 3, RUNNING);
        nm1.registerNode(Arrays.asList(amContainer, runningContainer, completedContainer), null);
        Thread.sleep(200);
        // check whether current am could get containerCompleteMsg
        RMApp recoveredApp0 = getRMContext().getRMApps().get(app0.getApplicationId());
        RMAppAttempt loadedAttempt1 = recoveredApp0.getCurrentAppAttempt();
        Assert.assertEquals(1, loadedAttempt1.getJustFinishedContainers().size());
    }

    // Test that if application state was saved, but attempt state was not saved.
    // RM should start correctly.
    @Test(timeout = 20000)
    public void testAppStateSavedButAttemptStateNotSaved() throws Exception {
        MockMemoryRMStateStore memStore = new MockMemoryRMStateStore() {
            @Override
            public synchronized void updateApplicationAttemptStateInternal(ApplicationAttemptId appAttemptId, ApplicationAttemptStateData attemptState) {
                // do nothing;
                // simulate the failure that attempt final state is not saved.
            }
        };
        memStore.init(conf);
        rm1 = new MockRM(conf, memStore);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        RMApp app1 = rm1.submitApp(200);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        MockRM.finishAMAndVerifyAppState(app1, rm1, nm1, am1);
        ApplicationStateData appSavedState = getState().getApplicationState().get(app1.getApplicationId());
        // check that app state is  saved.
        Assert.assertEquals(FINISHED, appSavedState.getState());
        // check that attempt state is not saved.
        Assert.assertNull(getState());
        rm2 = new MockRM(conf, memStore);
        start();
        RMApp recoveredApp1 = getRMContext().getRMApps().get(app1.getApplicationId());
        Assert.assertEquals(FINISHED, recoveredApp1.getState());
        // check that attempt state is recovered correctly.
        Assert.assertEquals(RMAppAttemptState.FINISHED, getState());
    }

    @Test(timeout = 600000)
    public void testUAMRecoveryOnRMWorkPreservingRestart() throws Exception {
        conf.setInt(RM_AM_MAX_ATTEMPTS, 1);
        // start RM
        rm1 = new MockRM(conf);
        start();
        MockMemoryRMStateStore memStore = ((MockMemoryRMStateStore) (rm1.getRMStateStore()));
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // create app and launch the UAM
        RMApp app0 = rm1.submitApp(200, true);
        MockAM am0 = MockRM.launchUAM(app0, rm1, nm1);
        am0.registerAppAttempt();
        // Allocate containers to UAM
        int numContainers = 2;
        am0.allocate("127.0.0.1", 1000, numContainers, new ArrayList<ContainerId>());
        nm1.nodeHeartbeat(true);
        List<Container> conts = am0.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers();
        while ((conts.size()) < 2) {
            nm1.nodeHeartbeat(true);
            conts.addAll(am0.allocate(new ArrayList<org.apache.hadoop.yarn.api.records.ResourceRequest>(), new ArrayList<ContainerId>()).getAllocatedContainers());
            Thread.sleep(100);
        } 
        // start new RM
        rm2 = new MockRM(conf, memStore);
        start();
        MockMemoryRMStateStore memStore2 = ((MockMemoryRMStateStore) (rm2.getRMStateStore()));
        rm2.waitForState(app0.getApplicationId(), ACCEPTED);
        rm2.waitForState(am0.getApplicationAttemptId(), LAUNCHED);
        // recover app
        nm1.setResourceTrackerService(getResourceTrackerService());
        RMApp recoveredApp = getRMContext().getRMApps().get(app0.getApplicationId());
        NMContainerStatus container1 = TestRMRestart.createNMContainerStatus(am0.getApplicationAttemptId(), 1, RUNNING);
        NMContainerStatus container2 = TestRMRestart.createNMContainerStatus(am0.getApplicationAttemptId(), 2, RUNNING);
        nm1.registerNode(Arrays.asList(container1, container2), null);
        // Wait for RM to settle down on recovering containers;
        TestWorkPreservingRMRestart.waitForNumContainersToRecover(2, rm2, am0.getApplicationAttemptId());
        // retry registerApplicationMaster() after RM restart.
        am0.setAMRMProtocol(getApplicationMasterService(), getRMContext());
        am0.registerAppAttempt(true);
        // Check if UAM is correctly recovered on restart
        rm2.waitForState(app0.getApplicationId(), RMAppState.RUNNING);
        rm2.waitForState(am0.getApplicationAttemptId(), RMAppAttemptState.RUNNING);
        // Check if containers allocated to UAM are recovered
        Map<ApplicationId, SchedulerApplication> schedulerApps = getSchedulerApplications();
        SchedulerApplication schedulerApp = schedulerApps.get(recoveredApp.getApplicationId());
        SchedulerApplicationAttempt schedulerAttempt = schedulerApp.getCurrentAppAttempt();
        Assert.assertEquals(numContainers, schedulerAttempt.getLiveContainers().size());
        // Check if UAM is able to heart beat
        Assert.assertNotNull(am0.doHeartbeat());
        // Complete the UAM
        am0.unregisterAppAttempt(false);
        rm2.waitForState(am0.getApplicationAttemptId(), RMAppAttemptState.FINISHED);
        rm2.waitForState(app0.getApplicationId(), FINISHED);
        Assert.assertEquals(SUCCEEDED, recoveredApp.getFinalApplicationStatus());
        // Restart RM once more to check UAM is not re-run
        MockRM rm3 = new MockRM(conf, memStore2);
        start();
        recoveredApp = getRMContext().getRMApps().get(app0.getApplicationId());
        Assert.assertEquals(FINISHED, recoveredApp.getState());
    }

    @Test(timeout = 30000)
    public void testUnknownUserOnRecovery() throws Exception {
        MockRM rm1 = new MockRM(conf);
        start();
        MockMemoryRMStateStore memStore = ((MockMemoryRMStateStore) (rm1.getRMStateStore()));
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        nm1.registerNode();
        // create app and launch the UAM
        RMApp app0 = rm1.submitApp(200, true);
        MockAM am0 = MockRM.launchUAM(app0, rm1, nm1);
        am0.registerAppAttempt();
        rm1.killApp(app0.getApplicationId());
        PlacementManager placementMgr = Mockito.mock(PlacementManager.class);
        Mockito.doThrow(new YarnException("No groups for user")).when(placementMgr).placeApplication(ArgumentMatchers.any(ApplicationSubmissionContext.class), ArgumentMatchers.any(String.class));
        MockRM rm2 = new MockRM(conf, memStore) {
            @Override
            protected RMAppManager createRMAppManager() {
                return new RMAppManager(this.rmContext, this.scheduler, this.masterService, this.applicationACLsManager, conf) {
                    @Override
                    ApplicationPlacementContext placeApplication(PlacementManager placementManager, ApplicationSubmissionContext context, String user, boolean isRecovery) throws YarnException {
                        return super.placeApplication(placementMgr, context, user, isRecovery);
                    }
                };
            }
        };
        start();
        RMApp recoveredApp = getRMContext().getRMApps().get(app0.getApplicationId());
        Assert.assertEquals(KILLED, recoveredApp.getState());
    }

    @Test(timeout = 30000)
    public void testDynamicAutoCreatedQueueRecoveryWithDefaultQueue() throws Exception {
        // if queue name is not specified, it should submit to 'default' queue
        testDynamicAutoCreatedQueueRecovery(TestCapacitySchedulerAutoCreatedQueueBase.USER1, null);
    }

    @Test(timeout = 30000)
    public void testDynamicAutoCreatedQueueRecoveryWithOverrideQueueMappingFlag() throws Exception {
        testDynamicAutoCreatedQueueRecovery(TestCapacitySchedulerAutoCreatedQueueBase.USER1, TestCapacitySchedulerAutoCreatedQueueBase.USER1);
    }

    // Apps already completed before RM restart. Make sure we restore the queue
    // correctly
    @Test(timeout = 20000)
    public void testFairSchedulerCompletedAppsQueue() throws Exception {
        if ((getSchedulerType()) != (FAIR)) {
            return;
        }
        rm1 = new MockRM(conf);
        start();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 8192, getResourceTrackerService());
        nm1.registerNode();
        RMApp app = rm1.submitApp(200);
        MockAM am1 = MockRM.launchAndRegisterAM(app, rm1, nm1);
        MockRM.finishAMAndVerifyAppState(app, rm1, nm1, am1);
        String fsQueueContext = app.getApplicationSubmissionContext().getQueue();
        String fsQueueApp = app.getQueue();
        Assert.assertEquals("Queue in app not equal to submission context", fsQueueApp, fsQueueContext);
        RMAppAttempt rmAttempt = app.getCurrentAppAttempt();
        Assert.assertNotNull("No AppAttempt found", rmAttempt);
        rm2 = new MockRM(conf, rm1.getRMStateStore());
        start();
        RMApp recoveredApp = getRMContext().getRMApps().get(app.getApplicationId());
        RMAppAttempt rmAttemptRecovered = recoveredApp.getCurrentAppAttempt();
        Assert.assertNotNull("No AppAttempt found after recovery", rmAttemptRecovered);
        String fsQueueContextRecovered = recoveredApp.getApplicationSubmissionContext().getQueue();
        String fsQueueAppRecovered = recoveredApp.getQueue();
        Assert.assertEquals(FINISHED, recoveredApp.getState());
        Assert.assertEquals("Recovered app queue is not the same as context queue", fsQueueAppRecovered, fsQueueContextRecovered);
    }
}

