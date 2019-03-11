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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests to verify fairshare and minshare preemption, using parameterization.
 */
@RunWith(Parameterized.class)
public class TestFairSchedulerPreemption extends FairSchedulerTestBase {
    private static final File ALLOC_FILE = new File(FairSchedulerTestBase.TEST_DIR, "test-queues");

    private static final int GB = 1024;

    // Scheduler clock
    private final ControlledClock clock = new ControlledClock();

    // Node Capacity = NODE_CAPACITY_MULTIPLE * (1 GB or 1 vcore)
    private static final int NODE_CAPACITY_MULTIPLE = 4;

    private final boolean fairsharePreemption;

    private final boolean drf;

    // App that takes up the entire cluster
    private FSAppAttempt greedyApp;

    // Starving app that is expected to instigate preemption
    private FSAppAttempt starvingApp;

    public TestFairSchedulerPreemption(String name, int mode) throws IOException {
        fairsharePreemption = mode > 1;// 2 and 3

        drf = (mode % 2) == 1;// 1 and 3

        writeAllocFile();
    }

    @Test
    public void testPreemptionWithinSameLeafQueue() throws Exception {
        String queue = "root.preemptable.child-1";
        submitApps(queue, queue);
        if (fairsharePreemption) {
            verifyPreemption(2, 4);
        } else {
            verifyNoPreemption();
        }
    }

    @Test
    public void testPreemptionBetweenTwoSiblingLeafQueues() throws Exception {
        submitApps("root.preemptable.child-1", "root.preemptable.child-2");
        verifyPreemption(2, 4);
    }

    @Test
    public void testPreemptionBetweenNonSiblingQueues() throws Exception {
        submitApps("root.preemptable.child-1", "root.nonpreemptable.child-1");
        verifyPreemption(2, 4);
    }

    @Test
    public void testNoPreemptionFromDisallowedQueue() throws Exception {
        submitApps("root.nonpreemptable.child-1", "root.preemptable.child-1");
        verifyNoPreemption();
    }

    @Test
    public void testPreemptionSelectNonAMContainer() throws Exception {
        takeAllResources("root.preemptable.child-1");
        setNumAMContainersPerNode(2);
        preemptHalfResources("root.preemptable.child-2");
        verifyPreemption(2, 4);
        ArrayList<RMContainer> containers = ((ArrayList<RMContainer>) (starvingApp.getLiveContainers()));
        String host0 = containers.get(0).getNodeId().getHost();
        String host1 = containers.get(1).getNodeId().getHost();
        // Each node provides two and only two non-AM containers to be preempted, so
        // the preemption happens on both nodes.
        Assert.assertTrue(("Preempted containers should come from two different " + "nodes."), (!(host0.equals(host1))));
    }

    @Test
    public void testAppNotPreemptedBelowFairShare() throws Exception {
        takeAllResources("root.preemptable.child-1");
        tryPreemptMoreThanFairShare("root.preemptable.child-2");
    }

    @Test
    public void testPreemptionBetweenSiblingQueuesWithParentAtFairShare() throws InterruptedException {
        // Run this test only for fairshare preemption
        if (!(fairsharePreemption)) {
            return;
        }
        // Let one of the child queues take over the entire cluster
        takeAllResources("root.preemptable.child-1");
        // Submit a job so half the resources go to parent's sibling
        preemptHalfResources("root.preemptable-sibling");
        verifyPreemption(2, 4);
        // Submit a job to the child's sibling to force preemption from the child
        preemptHalfResources("root.preemptable.child-2");
        verifyPreemption(1, 2);
    }

    /* It tests the case that there is less-AM-container solution in the
    remaining nodes.
     */
    @Test
    public void testRelaxLocalityPreemptionWithLessAMInRemainingNodes() throws Exception {
        takeAllResources("root.preemptable.child-1");
        RMNode node1 = rmNodes.get(0);
        setAllAMContainersOnNode(node1.getNodeID());
        ApplicationAttemptId greedyAppAttemptId = getGreedyAppAttemptIdOnNode(node1.getNodeID());
        updateRelaxLocalityRequestSchedule(node1, TestFairSchedulerPreemption.GB, 4);
        verifyRelaxLocalityPreemption(node1.getNodeID(), greedyAppAttemptId, 4);
    }

    /* It tests the case that there is no less-AM-container solution in the
    remaining nodes.
     */
    @Test
    public void testRelaxLocalityPreemptionWithNoLessAMInRemainingNodes() throws Exception {
        takeAllResources("root.preemptable.child-1");
        RMNode node1 = rmNodes.get(0);
        setNumAMContainersOnNode(3, node1.getNodeID());
        RMNode node2 = rmNodes.get(1);
        setAllAMContainersOnNode(node2.getNodeID());
        ApplicationAttemptId greedyAppAttemptId = getGreedyAppAttemptIdOnNode(node2.getNodeID());
        updateRelaxLocalityRequestSchedule(node1, ((TestFairSchedulerPreemption.GB) * 2), 1);
        verifyRelaxLocalityPreemption(node2.getNodeID(), greedyAppAttemptId, 6);
    }
}

