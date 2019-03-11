/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;


import CapacitySchedulerConfiguration.ADDITIONAL_RESOURCE_BALANCE_BASED_ON_RESERVED_CONTAINERS;
import CapacitySchedulerConfiguration.MAX_WAIT_BEFORE_KILL_FOR_QUEUE_BALANCE_PREEMPTION;
import CapacitySchedulerConfiguration.PREEMPTION_TO_BALANCE_QUEUES_BEYOND_GUARANTEED;
import CapacitySchedulerConfiguration.QUEUE_PRIORITY_UTILIZATION_ORDERING_POLICY;
import CapacitySchedulerConfiguration.ROOT;
import YarnConfiguration.RM_SCHEDULER_MAXIMUM_ALLOCATION_MB;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.monitor.SchedulingMonitor;
import org.apache.hadoop.yarn.server.resourcemanager.monitor.SchedulingMonitorManager;
import org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity.ProportionalCapacityPreemptionPolicy;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import static CapacitySchedulerConfiguration.ROOT;


public class TestCapacitySchedulerSurgicalPreemption extends CapacitySchedulerPreemptionTestBase {
    private static final int NUM_NM = 5;

    @Test(timeout = 60000)
    public void testSimpleSurgicalPreemption() throws Exception {
        /**
         * Test case: Submit two application (app1/app2) to different queues, queue
         * structure:
         *
         * <pre>
         *             Root
         *            /  |  \
         *           a   b   c
         *          10   20  70
         * </pre>
         *
         * 1) Two nodes (n1/n2) in the cluster, each of them has 20G.
         *
         * 2) app1 submit to queue-a first, it asked 32 * 1G containers
         * We will allocate 16 on n1 and 16 on n2.
         *
         * 3) app2 submit to queue-c, ask for one 1G container (for AM)
         *
         * 4) app2 asks for another 6G container, it will be reserved on n1
         *
         * Now: we have:
         * n1: 17 from app1, 1 from app2, and 1 reserved from app2
         * n2: 16 from app1.
         *
         * After preemption, we should expect:
         * Preempt 4 containers from app1 on n1.
         */
        testSimpleSurgicalPreemption("a", "c", "user", "user");
    }

    @Test(timeout = 60000)
    public void testSurgicalPreemptionWithAvailableResource() throws Exception {
        /**
         * Test case: Submit two application (app1/app2) to different queues, queue
         * structure:
         *
         * <pre>
         *             Root
         *            /  |  \
         *           a   b   c
         *          10   20  70
         * </pre>
         *
         * 1) Two nodes (n1/n2) in the cluster, each of them has 20G.
         *
         * 2) app1 submit to queue-b, asks for 1G * 5
         *
         * 3) app2 submit to queue-c, ask for one 4G container (for AM)
         *
         * After preemption, we should expect:
         * Preempt 3 containers from app1 and AM of app2 successfully allocated.
         */
        MockRM rm1 = new MockRM(conf);
        getRMContext().setNodeLabelManager(mgr);
        start();
        MockNM nm1 = rm1.registerNode("h1:1234", (20 * (GB)));
        MockNM nm2 = rm1.registerNode("h2:1234", (20 * (GB)));
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        RMNode rmNode1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        RMNode rmNode2 = getRMContext().getRMNodes().get(nm2.getNodeId());
        // launch an app to queue, AM container should be launched in nm1
        RMApp app1 = rm1.submitApp((1 * (GB)), "app", "user", null, "a");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        am1.allocate("*", (1 * (GB)), 38, new ArrayList<org.apache.hadoop.yarn.api.records.ContainerId>());
        // Do allocation for node1/node2
        for (int i = 0; i < 38; i++) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
        }
        // App1 should have 31 containers now
        FiCaSchedulerApp schedulerApp1 = cs.getApplicationAttempt(am1.getApplicationAttemptId());
        Assert.assertEquals(39, schedulerApp1.getLiveContainers().size());
        // 17 from n1 and 16 from n2
        waitNumberOfLiveContainersOnNodeFromApp(cs.getNode(rmNode1.getNodeID()), am1.getApplicationAttemptId(), 20);
        waitNumberOfLiveContainersOnNodeFromApp(cs.getNode(rmNode2.getNodeID()), am1.getApplicationAttemptId(), 19);
        // Submit app2 to queue-c and asks for a 4G container for AM
        RMApp app2 = rm1.submitApp((4 * (GB)), "app", "user", null, "c");
        FiCaSchedulerApp schedulerApp2 = cs.getApplicationAttempt(ApplicationAttemptId.newInstance(app2.getApplicationId(), 1));
        // Call editSchedule: containers are selected to be preemption candidate
        SchedulingMonitorManager smm = getSchedulingMonitorManager();
        SchedulingMonitor smon = smm.getAvailableSchedulingMonitor();
        ProportionalCapacityPreemptionPolicy editPolicy = ((ProportionalCapacityPreemptionPolicy) (smon.getSchedulingEditPolicy()));
        editPolicy.editSchedule();
        Assert.assertEquals(3, editPolicy.getToPreemptContainers().size());
        // Call editSchedule again: selected containers are killed
        editPolicy.editSchedule();
        waitNumberOfLiveContainersFromApp(schedulerApp1, 36);
        // Call allocation, containers are reserved
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
        waitNumberOfReservedContainersFromApp(schedulerApp2, 1);
        // Call editSchedule twice and allocation once, container should get allocated
        editPolicy.editSchedule();
        editPolicy.editSchedule();
        int tick = 0;
        while (((schedulerApp2.getLiveContainers().size()) != 1) && (tick < 10)) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
            tick++;
            Thread.sleep(100);
        } 
        waitNumberOfReservedContainersFromApp(schedulerApp2, 0);
        close();
    }

    @Test(timeout = 60000)
    public void testPriorityPreemptionWhenAllQueuesAreBelowGuaranteedCapacities() throws Exception {
        /**
         * Test case: Submit two application (app1/app2) to different queues, queue
         * structure:
         *
         * <pre>
         *             Root
         *            /  |  \
         *           a   b   c
         *          10   20  70
         * </pre>
         *
         * 1) Two nodes (n1/n2) in the cluster, each of them has 20G.
         *
         * 2) app1 submit to queue-b first, it asked 6 * 1G containers
         * We will allocate 4 on n1 (including AM) and 3 on n2.
         *
         * 3) app2 submit to queue-c, ask for one 18G container (for AM)
         *
         * After preemption, we should expect:
         * Preempt 3 containers from app1 and AM of app2 successfully allocated.
         */
        conf.setPUOrderingPolicyUnderUtilizedPreemptionEnabled(true);
        conf.setPUOrderingPolicyUnderUtilizedPreemptionDelay(1000);
        conf.setQueueOrderingPolicy(ROOT, QUEUE_PRIORITY_UTILIZATION_ORDERING_POLICY);
        // Queue c has higher priority than a/b
        conf.setQueuePriority(((ROOT) + ".c"), 1);
        MockRM rm1 = new MockRM(conf);
        getRMContext().setNodeLabelManager(mgr);
        start();
        MockNM nm1 = rm1.registerNode("h1:1234", (20 * (GB)));
        MockNM nm2 = rm1.registerNode("h2:1234", (20 * (GB)));
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        RMNode rmNode1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        RMNode rmNode2 = getRMContext().getRMNodes().get(nm2.getNodeId());
        // launch an app to queue, AM container should be launched in nm1
        RMApp app1 = rm1.submitApp((1 * (GB)), "app", "user", null, "b");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        am1.allocate("*", (1 * (GB)), 6, new ArrayList<>());
        // Do allocation for node1/node2
        for (int i = 0; i < 3; i++) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
        }
        // App1 should have 7 containers now, so the abs-used-cap of b is
        // 7 / 40 = 17.5% < 20% (guaranteed)
        FiCaSchedulerApp schedulerApp1 = cs.getApplicationAttempt(am1.getApplicationAttemptId());
        Assert.assertEquals(7, schedulerApp1.getLiveContainers().size());
        // 4 from n1 and 3 from n2
        waitNumberOfLiveContainersOnNodeFromApp(cs.getNode(rmNode1.getNodeID()), am1.getApplicationAttemptId(), 4);
        waitNumberOfLiveContainersOnNodeFromApp(cs.getNode(rmNode2.getNodeID()), am1.getApplicationAttemptId(), 3);
        // Submit app2 to queue-c and asks for a 1G container for AM
        RMApp app2 = rm1.submitApp((18 * (GB)), "app", "user", null, "c");
        FiCaSchedulerApp schedulerApp2 = cs.getApplicationAttempt(ApplicationAttemptId.newInstance(app2.getApplicationId(), 1));
        while ((cs.getNode(rmNode1.getNodeID()).getReservedContainer()) == null) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
            Thread.sleep(10);
        } 
        // Call editSchedule immediately: containers are not selected
        SchedulingMonitorManager smm = getSchedulingMonitorManager();
        SchedulingMonitor smon = smm.getAvailableSchedulingMonitor();
        ProportionalCapacityPreemptionPolicy editPolicy = ((ProportionalCapacityPreemptionPolicy) (smon.getSchedulingEditPolicy()));
        editPolicy.editSchedule();
        Assert.assertEquals(0, editPolicy.getToPreemptContainers().size());
        // Sleep the timeout interval, we should be able to see containers selected
        Thread.sleep(1000);
        editPolicy.editSchedule();
        Assert.assertEquals(2, editPolicy.getToPreemptContainers().size());
        // Call editSchedule again: selected containers are killed, and new AM
        // container launched
        editPolicy.editSchedule();
        // Do allocation till reserved container allocated
        while ((cs.getNode(rmNode1.getNodeID()).getReservedContainer()) != null) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
            Thread.sleep(10);
        } 
        waitNumberOfLiveContainersFromApp(schedulerApp2, 1);
        close();
    }

    @Test(timeout = 300000)
    public void testPriorityPreemptionRequiresMoveReservation() throws Exception {
        /**
         * Test case: Submit two application (app1/app2) to different queues, queue
         * structure:
         *
         * <pre>
         *             Root
         *            /  |  \
         *           a   b   c
         *          10   20  70
         * </pre>
         *
         * 1) 3 nodes in the cluster, 10G for each
         *
         * 2) app1 submit to queue-b first, it asked 2G each,
         *    it can get 2G on n1 (AM), 2 * 2G on n2
         *
         * 3) app2 submit to queue-c, with 2G AM container (allocated on n3)
         *    app2 requires 9G resource, which will be reserved on n3
         *
         * We should expect container unreserved from n3 and allocated on n1/n2
         */
        conf.setPUOrderingPolicyUnderUtilizedPreemptionEnabled(true);
        conf.setPUOrderingPolicyUnderUtilizedPreemptionDelay(1000);
        conf.setQueueOrderingPolicy(ROOT, QUEUE_PRIORITY_UTILIZATION_ORDERING_POLICY);
        conf.setPUOrderingPolicyUnderUtilizedPreemptionMoveReservation(true);
        // Queue c has higher priority than a/b
        conf.setQueuePriority(((ROOT) + ".c"), 1);
        MockRM rm1 = new MockRM(conf);
        getRMContext().setNodeLabelManager(mgr);
        start();
        MockNM nm1 = rm1.registerNode("h1:1234", (10 * (GB)));
        MockNM nm2 = rm1.registerNode("h2:1234", (10 * (GB)));
        MockNM nm3 = rm1.registerNode("h3:1234", (10 * (GB)));
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        RMNode rmNode1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        RMNode rmNode2 = getRMContext().getRMNodes().get(nm2.getNodeId());
        RMNode rmNode3 = getRMContext().getRMNodes().get(nm3.getNodeId());
        // launch an app to queue, AM container should be launched in nm1
        RMApp app1 = rm1.submitApp((2 * (GB)), "app", "user", null, "b");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        am1.allocate("*", (2 * (GB)), 2, new ArrayList<>());
        // Do allocation for node2 twice
        for (int i = 0; i < 2; i++) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
        }
        FiCaSchedulerApp schedulerApp1 = cs.getApplicationAttempt(am1.getApplicationAttemptId());
        Assert.assertEquals(3, schedulerApp1.getLiveContainers().size());
        // 1 from n1 and 2 from n2
        waitNumberOfLiveContainersOnNodeFromApp(cs.getNode(rmNode1.getNodeID()), am1.getApplicationAttemptId(), 1);
        waitNumberOfLiveContainersOnNodeFromApp(cs.getNode(rmNode2.getNodeID()), am1.getApplicationAttemptId(), 2);
        // Submit app2 to queue-c and asks for a 2G container for AM, on n3
        RMApp app2 = rm1.submitApp((2 * (GB)), "app", "user", null, "c");
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm1, nm3);
        FiCaSchedulerApp schedulerApp2 = cs.getApplicationAttempt(ApplicationAttemptId.newInstance(app2.getApplicationId(), 1));
        // Asks 1 * 9G container
        am2.allocate("*", (9 * (GB)), 1, new ArrayList<>());
        // Do allocation for node3 once
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode3));
        // Make sure container reserved on node3
        Assert.assertNotNull(cs.getNode(rmNode3.getNodeID()).getReservedContainer());
        // Call editSchedule immediately: nothing happens
        SchedulingMonitorManager smm = getSchedulingMonitorManager();
        SchedulingMonitor smon = smm.getAvailableSchedulingMonitor();
        ProportionalCapacityPreemptionPolicy editPolicy = ((ProportionalCapacityPreemptionPolicy) (smon.getSchedulingEditPolicy()));
        editPolicy.editSchedule();
        Assert.assertNotNull(cs.getNode(rmNode3.getNodeID()).getReservedContainer());
        // Sleep the timeout interval, we should be able to see reserved container
        // moved to n2 (n1 occupied by AM)
        Thread.sleep(1000);
        editPolicy.editSchedule();
        Assert.assertNull(cs.getNode(rmNode3.getNodeID()).getReservedContainer());
        Assert.assertNotNull(cs.getNode(rmNode2.getNodeID()).getReservedContainer());
        Assert.assertEquals(am2.getApplicationAttemptId(), cs.getNode(rmNode2.getNodeID()).getReservedContainer().getApplicationAttemptId());
        // Do it again, we should see containers marked to be preempt
        editPolicy.editSchedule();
        Assert.assertEquals(2, editPolicy.getToPreemptContainers().size());
        // Call editSchedule again: selected containers are killed
        editPolicy.editSchedule();
        // Do allocation till reserved container allocated
        while ((schedulerApp2.getLiveContainers().size()) < 2) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
            Thread.sleep(200);
        } 
        waitNumberOfLiveContainersFromApp(schedulerApp1, 1);
        close();
    }

    @Test(timeout = 60000)
    public void testPriorityPreemptionOnlyTriggeredWhenDemandingQueueUnsatisfied() throws Exception {
        /**
         * Test case: Submit two application (app1/app2) to different queues, queue
         * structure:
         *
         * <pre>
         *             Root
         *            /  |  \
         *           a   b   c
         *          10   20  70
         * </pre>
         *
         * 1) 10 nodes (n0-n9) in the cluster, each of them has 10G.
         *
         * 2) app1 submit to queue-b first, it asked 8 * 1G containers
         * We will allocate 1 container on each of n0-n10
         *
         * 3) app2 submit to queue-c, ask for 10 * 10G containers (including AM)
         *
         * After preemption, we should expect:
         * Preempt 7 containers from app1 and usage of app2 is 70%
         */
        conf.setPUOrderingPolicyUnderUtilizedPreemptionEnabled(true);
        conf.setPUOrderingPolicyUnderUtilizedPreemptionDelay(1000);
        conf.setQueueOrderingPolicy(ROOT, QUEUE_PRIORITY_UTILIZATION_ORDERING_POLICY);
        // Queue c has higher priority than a/b
        conf.setQueuePriority(((ROOT) + ".c"), 1);
        MockRM rm1 = new MockRM(conf);
        getRMContext().setNodeLabelManager(mgr);
        start();
        MockNM[] mockNMs = new MockNM[10];
        for (int i = 0; i < 10; i++) {
            mockNMs[i] = rm1.registerNode((("h" + i) + ":1234"), (10 * (GB)));
        }
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        RMNode[] rmNodes = new RMNode[10];
        for (int i = 0; i < 10; i++) {
            rmNodes[i] = getRMContext().getRMNodes().get(mockNMs[i].getNodeId());
        }
        // launch an app to queue, AM container should be launched in nm1
        RMApp app1 = rm1.submitApp((1 * (GB)), "app", "user", null, "b");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, mockNMs[0]);
        am1.allocate("*", (1 * (GB)), 8, new ArrayList<>());
        // Do allocation for nm1-nm8
        for (int i = 1; i < 9; i++) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNodes[i]));
        }
        // App1 should have 9 containers now, so the abs-used-cap of b is 9%
        FiCaSchedulerApp schedulerApp1 = cs.getApplicationAttempt(am1.getApplicationAttemptId());
        Assert.assertEquals(9, schedulerApp1.getLiveContainers().size());
        for (int i = 0; i < 9; i++) {
            waitNumberOfLiveContainersOnNodeFromApp(cs.getNode(rmNodes[i].getNodeID()), am1.getApplicationAttemptId(), 1);
        }
        // Submit app2 to queue-c and asks for a 10G container for AM
        // Launch AM in NM9
        RMApp app2 = rm1.submitApp((10 * (GB)), "app", "user", null, "c");
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm1, mockNMs[9]);
        FiCaSchedulerApp schedulerApp2 = cs.getApplicationAttempt(ApplicationAttemptId.newInstance(app2.getApplicationId(), 1));
        // Ask 10 * 10GB containers
        am2.allocate("*", (10 * (GB)), 10, new ArrayList<>());
        // Do allocation for all nms
        for (int i = 1; i < 10; i++) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNodes[i]));
        }
        // Check am2 reserved resource from nm1-nm9
        for (int i = 1; i < 9; i++) {
            Assert.assertNotNull(("Should reserve on nm-" + i), cs.getNode(rmNodes[i].getNodeID()).getReservedContainer());
        }
        // Sleep the timeout interval, we should be able to see 6 containers selected
        // 6 (selected) + 1 (allocated) which makes target capacity to 70%
        Thread.sleep(1000);
        SchedulingMonitorManager smm = getSchedulingMonitorManager();
        SchedulingMonitor smon = smm.getAvailableSchedulingMonitor();
        ProportionalCapacityPreemptionPolicy editPolicy = ((ProportionalCapacityPreemptionPolicy) (smon.getSchedulingEditPolicy()));
        editPolicy.editSchedule();
        checkNumberOfPreemptionCandidateFromApp(editPolicy, 6, am1.getApplicationAttemptId());
        // Call editSchedule again: selected containers are killed
        editPolicy.editSchedule();
        waitNumberOfLiveContainersFromApp(schedulerApp1, 3);
        // Do allocation for all nms
        for (int i = 1; i < 10; i++) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNodes[i]));
        }
        waitNumberOfLiveContainersFromApp(schedulerApp2, 7);
        waitNumberOfLiveContainersFromApp(schedulerApp1, 3);
        close();
    }

    @Test(timeout = 600000)
    public void testPriorityPreemptionFromHighestPriorityQueueAndOldestContainer() throws Exception {
        /**
         * Test case: Submit two application (app1/app2) to different queues, queue
         * structure:
         *
         * <pre>
         *             Root
         *            /  |  \
         *           a   b   c
         *          45  45  10
         * </pre>
         *
         * Priority of queue_a = 1
         * Priority of queue_b = 2
         *
         * 1) 5 nodes (n0-n4) in the cluster, each of them has 4G.
         *
         * 2) app1 submit to queue-c first (AM=1G), it asked 4 * 1G containers
         *    We will allocate 1 container on each of n0-n4. AM on n4.
         *
         * 3) app2 submit to queue-a, AM container=0.5G, allocated on n0
         *    Ask for 2 * 3.5G containers. (Reserved on n0/n1)
         *
         * 4) app2 submit to queue-b, AM container=0.5G, allocated on n2
         *    Ask for 2 * 3.5G containers. (Reserved on n2/n3)
         *
         * First we will preempt container on n2 since it is the oldest container of
         * Highest priority queue (b)
         */
        // A/B has higher priority
        conf.setQueuePriority(((ROOT) + ".a"), 1);
        conf.setQueuePriority(((ROOT) + ".b"), 2);
        conf.setCapacity(((ROOT) + ".a"), 45.0F);
        conf.setCapacity(((ROOT) + ".b"), 45.0F);
        conf.setCapacity(((ROOT) + ".c"), 10.0F);
        testPriorityPreemptionFromHighestPriorityQueueAndOldestContainer(new String[]{ "a", "b", "c" }, new String[]{ "user", "user", "user" });
    }

    @Test
    public void testPriorityPreemptionWithNodeLabels() throws Exception {
        // set up queue priority and capacity
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        initializeConfProperties(conf);
        MockRM rm1 = new MockRM(conf) {
            protected RMNodeLabelsManager createNodeLabelManager() {
                return mgr;
            }
        };
        start();
        MockNM[] mockNMs = new MockNM[TestCapacitySchedulerSurgicalPreemption.NUM_NM];
        for (int i = 0; i < (TestCapacitySchedulerSurgicalPreemption.NUM_NM); i++) {
            mockNMs[i] = rm1.registerNode((("h" + i) + ":1234"), 6144);
        }
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        mgr.addToCluserNodeLabels(Arrays.asList(NodeLabel.newInstance("x")));
        RMNode[] rmNodes = new RMNode[5];
        for (int i = 0; i < (TestCapacitySchedulerSurgicalPreemption.NUM_NM); i++) {
            rmNodes[i] = getRMContext().getRMNodes().get(mockNMs[i].getNodeId());
            mgr.replaceLabelsOnNode(ImmutableMap.of(rmNodes[i].getNodeID(), ImmutableSet.of("x")));
        }
        // launch an app to queue B, AM container launched in nm4
        RMApp app1 = rm1.submitApp(4096, "app", "user", null, "B");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, mockNMs[4]);
        am1.allocate("*", 4096, ((TestCapacitySchedulerSurgicalPreemption.NUM_NM) - 1), new ArrayList<>());
        // Do allocation for nm0-nm3
        for (int i = 0; i < ((TestCapacitySchedulerSurgicalPreemption.NUM_NM) - 1); i++) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNodes[i]));
        }
        // App1 should have 5 containers now, one for each node
        FiCaSchedulerApp schedulerApp1 = cs.getApplicationAttempt(am1.getApplicationAttemptId());
        Assert.assertEquals(TestCapacitySchedulerSurgicalPreemption.NUM_NM, schedulerApp1.getLiveContainers().size());
        for (int i = 0; i < (TestCapacitySchedulerSurgicalPreemption.NUM_NM); i++) {
            waitNumberOfLiveContainersOnNodeFromApp(cs.getNode(rmNodes[i].getNodeID()), am1.getApplicationAttemptId(), 1);
        }
        // Submit app2 to queue A and asks for a 750MB container for AM (on n0)
        RMApp app2 = rm1.submitApp(1024, "app", "user", null, "A");
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm1, mockNMs[0]);
        FiCaSchedulerApp schedulerApp2 = cs.getApplicationAttempt(ApplicationAttemptId.newInstance(app2.getApplicationId(), 1));
        // Ask NUM_NM-1 * 1500MB containers
        am2.allocate("*", 2048, ((TestCapacitySchedulerSurgicalPreemption.NUM_NM) - 1), new ArrayList<>());
        // Do allocation for n1-n4
        for (int i = 1; i < (TestCapacitySchedulerSurgicalPreemption.NUM_NM); i++) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNodes[i]));
        }
        // kill app1
        rm1.killApp(app1.getApplicationId());
        // Submit app3 to queue B and asks for a 5000MB container for AM (on n2)
        RMApp app3 = rm1.submitApp(1024, "app", "user", null, "B");
        MockAM am3 = MockRM.launchAndRegisterAM(app3, rm1, mockNMs[2]);
        FiCaSchedulerApp schedulerApp3 = cs.getApplicationAttempt(ApplicationAttemptId.newInstance(app3.getApplicationId(), 1));
        // Ask NUM_NM * 5000MB containers
        am3.allocate("*", 5120, TestCapacitySchedulerSurgicalPreemption.NUM_NM, new ArrayList<>());
        // Do allocation for n0-n4
        for (int i = 0; i < (TestCapacitySchedulerSurgicalPreemption.NUM_NM); i++) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNodes[i]));
        }
        // Sleep the timeout interval, we should see 2 containers selected
        Thread.sleep(1000);
        SchedulingMonitorManager smm = getSchedulingMonitorManager();
        SchedulingMonitor smon = smm.getAvailableSchedulingMonitor();
        ProportionalCapacityPreemptionPolicy editPolicy = ((ProportionalCapacityPreemptionPolicy) (smon.getSchedulingEditPolicy()));
        editPolicy.editSchedule();
        // We should only allow to preempt 2 containers, on node1 and node2
        Set<RMContainer> selectedToPreempt = editPolicy.getToPreemptContainers().keySet();
        Assert.assertEquals(2, selectedToPreempt.size());
        List<NodeId> selectedToPreemptNodeIds = new ArrayList<>();
        for (RMContainer rmc : selectedToPreempt) {
            selectedToPreemptNodeIds.add(rmc.getAllocatedNode());
        }
        MatcherAssert.assertThat(selectedToPreemptNodeIds, CoreMatchers.hasItems(mockNMs[1].getNodeId(), mockNMs[2].getNodeId()));
        close();
    }

    @Test(timeout = 60000)
    public void testPreemptionForFragmentatedCluster() throws Exception {
        // Set additional_balance_queue_based_on_reserved_res to true to get
        // additional preemptions.
        conf.setBoolean(ADDITIONAL_RESOURCE_BALANCE_BASED_ON_RESERVED_CONTAINERS, true);
        /**
         * Two queues, a/b, each of them are 50/50
         * 5 nodes in the cluster, each of them is 30G.
         *
         * Submit first app, AM = 3G, and 4 * 21G containers.
         * Submit second app, AM = 3G, and 4 * 21G containers,
         *
         * We can get one container preempted from 1st app.
         */
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration(this.conf);
        conf.setLong(RM_SCHEDULER_MAXIMUM_ALLOCATION_MB, (1024 * 21));
        conf.setQueues("root", new String[]{ "a", "b" });
        conf.setCapacity("root.a", 50);
        conf.setUserLimitFactor("root.a", 100);
        conf.setCapacity("root.b", 50);
        conf.setUserLimitFactor("root.b", 100);
        MockRM rm1 = new MockRM(conf);
        getRMContext().setNodeLabelManager(mgr);
        start();
        List<MockNM> nms = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            nms.add(rm1.registerNode((("h" + i) + ":1234"), (30 * (GB))));
        }
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        // launch an app to queue, AM container should be launched in nm1
        RMApp app1 = rm1.submitApp((3 * (GB)), "app", "user", null, "a");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nms.get(0));
        am1.allocate("*", (21 * (GB)), 4, new ArrayList<org.apache.hadoop.yarn.api.records.ContainerId>());
        // Do allocation for all nodes
        for (int i = 0; i < 10; i++) {
            MockNM mockNM = nms.get((i % (nms.size())));
            RMNode rmNode = cs.getRMContext().getRMNodes().get(mockNM.getNodeId());
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode));
        }
        // App1 should have 5 containers now
        FiCaSchedulerApp schedulerApp1 = cs.getApplicationAttempt(am1.getApplicationAttemptId());
        Assert.assertEquals(5, schedulerApp1.getLiveContainers().size());
        // launch an app to queue, AM container should be launched in nm1
        RMApp app2 = rm1.submitApp((3 * (GB)), "app", "user", null, "b");
        MockAM am2 = MockRM.launchAndRegisterAM(app2, rm1, nms.get(2));
        am2.allocate("*", (21 * (GB)), 4, new ArrayList<org.apache.hadoop.yarn.api.records.ContainerId>());
        // Do allocation for all nodes
        for (int i = 0; i < 10; i++) {
            MockNM mockNM = nms.get((i % (nms.size())));
            RMNode rmNode = cs.getRMContext().getRMNodes().get(mockNM.getNodeId());
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode));
        }
        // App2 should have 2 containers now
        FiCaSchedulerApp schedulerApp2 = cs.getApplicationAttempt(am2.getApplicationAttemptId());
        Assert.assertEquals(2, schedulerApp2.getLiveContainers().size());
        waitNumberOfReservedContainersFromApp(schedulerApp2, 1);
        // Call editSchedule twice and allocation once, container should get allocated
        SchedulingMonitorManager smm = getSchedulingMonitorManager();
        SchedulingMonitor smon = smm.getAvailableSchedulingMonitor();
        ProportionalCapacityPreemptionPolicy editPolicy = ((ProportionalCapacityPreemptionPolicy) (smon.getSchedulingEditPolicy()));
        editPolicy.editSchedule();
        editPolicy.editSchedule();
        int tick = 0;
        while (((schedulerApp2.getLiveContainers().size()) != 4) && (tick < 10)) {
            // Do allocation for all nodes
            for (int i = 0; i < 10; i++) {
                MockNM mockNM = nms.get((i % (nms.size())));
                RMNode rmNode = cs.getRMContext().getRMNodes().get(mockNM.getNodeId());
                cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode));
            }
            tick++;
            Thread.sleep(100);
        } 
        Assert.assertEquals(3, schedulerApp2.getLiveContainers().size());
        close();
    }

    @Test(timeout = 600000)
    public void testPreemptionToBalanceWithCustomTimeout() throws Exception {
        /**
         * Test case: Submit two application (app1/app2) to different queues, queue
         * structure:
         *
         * <pre>
         *             Root
         *            /  |  \
         *           a   b   c
         *          10   20  70
         * </pre>
         *
         * 1) Two nodes (n1/n2) in the cluster, each of them has 20G.
         *
         * 2) app1 submit to queue-b, asks for 1G * 5
         *
         * 3) app2 submit to queue-c, ask for one 4G container (for AM)
         *
         * After preemption, we should expect:
         * 1. Preempt 4 containers from app1
         * 2. the selected containers will be killed after configured timeout.
         * 3. AM of app2 successfully allocated.
         */
        conf.setBoolean(PREEMPTION_TO_BALANCE_QUEUES_BEYOND_GUARANTEED, true);
        conf.setLong(MAX_WAIT_BEFORE_KILL_FOR_QUEUE_BALANCE_PREEMPTION, (20 * 1000));
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration(this.conf);
        MockRM rm1 = new MockRM(conf);
        getRMContext().setNodeLabelManager(mgr);
        start();
        MockNM nm1 = rm1.registerNode("h1:1234", (20 * (GB)));
        MockNM nm2 = rm1.registerNode("h2:1234", (20 * (GB)));
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        RMNode rmNode1 = getRMContext().getRMNodes().get(nm1.getNodeId());
        RMNode rmNode2 = getRMContext().getRMNodes().get(nm2.getNodeId());
        // launch an app to queue, AM container should be launched in nm1
        RMApp app1 = rm1.submitApp((1 * (GB)), "app", "user", null, "a");
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);
        am1.allocate("*", (1 * (GB)), 38, new ArrayList<org.apache.hadoop.yarn.api.records.ContainerId>());
        // Do allocation for node1/node2
        for (int i = 0; i < 38; i++) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
        }
        // App1 should have 39 containers now
        FiCaSchedulerApp schedulerApp1 = cs.getApplicationAttempt(am1.getApplicationAttemptId());
        Assert.assertEquals(39, schedulerApp1.getLiveContainers().size());
        // 20 from n1 and 19 from n2
        waitNumberOfLiveContainersOnNodeFromApp(cs.getNode(rmNode1.getNodeID()), am1.getApplicationAttemptId(), 20);
        waitNumberOfLiveContainersOnNodeFromApp(cs.getNode(rmNode2.getNodeID()), am1.getApplicationAttemptId(), 19);
        // Submit app2 to queue-c and asks for a 4G container for AM
        RMApp app2 = rm1.submitApp((4 * (GB)), "app", "user", null, "c");
        FiCaSchedulerApp schedulerApp2 = cs.getApplicationAttempt(ApplicationAttemptId.newInstance(app2.getApplicationId(), 1));
        // Call editSchedule: containers are selected to be preemption candidate
        SchedulingMonitorManager smm = getSchedulingMonitorManager();
        SchedulingMonitor smon = smm.getAvailableSchedulingMonitor();
        ProportionalCapacityPreemptionPolicy editPolicy = ((ProportionalCapacityPreemptionPolicy) (smon.getSchedulingEditPolicy()));
        editPolicy.editSchedule();
        Assert.assertEquals(4, editPolicy.getToPreemptContainers().size());
        // check live containers immediately, nothing happen
        Assert.assertEquals(39, schedulerApp1.getLiveContainers().size());
        Thread.sleep((20 * 1000));
        // Call editSchedule again: selected containers are killed
        editPolicy.editSchedule();
        waitNumberOfLiveContainersFromApp(schedulerApp1, 35);
        // Call allocation, containers are reserved
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
        cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
        waitNumberOfReservedContainersFromApp(schedulerApp2, 1);
        // Call editSchedule twice and allocation once, container should get allocated
        editPolicy.editSchedule();
        editPolicy.editSchedule();
        int tick = 0;
        while (((schedulerApp2.getLiveContainers().size()) != 1) && (tick < 10)) {
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode1));
            cs.handle(new org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent(rmNode2));
            tick++;
            Thread.sleep(100);
        } 
        waitNumberOfReservedContainersFromApp(schedulerApp2, 0);
        close();
    }
}

