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
import java.util.Collection;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptRemovedSchedulerEvent;
import org.junit.Assert;
import org.junit.Test;


public class TestFairSchedulerFairShare extends FairSchedulerTestBase {
    private static final String ALLOC_FILE = new File(FairSchedulerTestBase.TEST_DIR, ((TestFairSchedulerFairShare.class.getName()) + ".xml")).getAbsolutePath();

    @Test
    public void testFairShareNoAppsRunning() throws IOException {
        int nodeCapacity = 16 * 1024;
        createClusterWithQueuesAndOneNode(nodeCapacity, "fair");
        scheduler.update();
        // No apps are running in the cluster,verify if fair share is zero
        // for all queues under parentA and parentB.
        Collection<FSLeafQueue> leafQueues = scheduler.getQueueManager().getLeafQueues();
        for (FSLeafQueue leaf : leafQueues) {
            if (leaf.getName().startsWith("root.parentA")) {
                Assert.assertEquals(0, (((double) (leaf.getFairShare().getMemorySize())) / nodeCapacity), 0);
            } else
                if (leaf.getName().startsWith("root.parentB")) {
                    Assert.assertEquals(0, (((double) (leaf.getFairShare().getMemorySize())) / nodeCapacity), 0);
                }

        }
        verifySteadyFairShareMemory(leafQueues, nodeCapacity);
    }

    @Test
    public void testFairShareOneAppRunning() throws IOException {
        int nodeCapacity = 16 * 1024;
        createClusterWithQueuesAndOneNode(nodeCapacity, "fair");
        // Run a app in a childA1. Verify whether fair share is 100% in childA1,
        // since it is the only active queue.
        // Also verify if fair share is 0 for childA2. since no app is
        // running in it.
        createSchedulingRequest((2 * 1024), "root.parentA.childA1", "user1");
        scheduler.update();
        Assert.assertEquals(100, ((((double) (scheduler.getQueueManager().getLeafQueue("root.parentA.childA1", false).getFairShare().getMemorySize())) / nodeCapacity) * 100), 0.1);
        Assert.assertEquals(0, (((double) (scheduler.getQueueManager().getLeafQueue("root.parentA.childA2", false).getFairShare().getMemorySize())) / nodeCapacity), 0.1);
        verifySteadyFairShareMemory(scheduler.getQueueManager().getLeafQueues(), nodeCapacity);
    }

    @Test
    public void testFairShareMultipleActiveQueuesUnderSameParent() throws IOException {
        int nodeCapacity = 16 * 1024;
        createClusterWithQueuesAndOneNode(nodeCapacity, "fair");
        // Run apps in childA1,childA2,childA3
        createSchedulingRequest((2 * 1024), "root.parentA.childA1", "user1");
        createSchedulingRequest((2 * 1024), "root.parentA.childA2", "user2");
        createSchedulingRequest((2 * 1024), "root.parentA.childA3", "user3");
        scheduler.update();
        // Verify if fair share is 100 / 3 = 33%
        for (int i = 1; i <= 3; i++) {
            Assert.assertEquals(33, ((((double) (scheduler.getQueueManager().getLeafQueue(("root.parentA.childA" + i), false).getFairShare().getMemorySize())) / nodeCapacity) * 100), 0.9);
        }
        verifySteadyFairShareMemory(scheduler.getQueueManager().getLeafQueues(), nodeCapacity);
    }

    @Test
    public void testFairShareMultipleActiveQueuesUnderDifferentParent() throws IOException {
        int nodeCapacity = 16 * 1024;
        createClusterWithQueuesAndOneNode(nodeCapacity, "fair");
        // Run apps in childA1,childA2 which are under parentA
        createSchedulingRequest((2 * 1024), "root.parentA.childA1", "user1");
        createSchedulingRequest((3 * 1024), "root.parentA.childA2", "user2");
        // Run app in childB1 which is under parentB
        createSchedulingRequest((1 * 1024), "root.parentB.childB1", "user3");
        // Run app in root.default queue
        createSchedulingRequest((1 * 1024), "root.default", "user4");
        scheduler.update();
        // The two active child queues under parentA would
        // get fair share of 80/2=40%
        for (int i = 1; i <= 2; i++) {
            Assert.assertEquals(40, ((((double) (scheduler.getQueueManager().getLeafQueue(("root.parentA.childA" + i), false).getFairShare().getMemorySize())) / nodeCapacity) * 100), 0.9);
        }
        // The child queue under parentB would get a fair share of 10%,
        // basically all of parentB's fair share
        Assert.assertEquals(10, ((((double) (scheduler.getQueueManager().getLeafQueue("root.parentB.childB1", false).getFairShare().getMemorySize())) / nodeCapacity) * 100), 0.9);
        verifySteadyFairShareMemory(scheduler.getQueueManager().getLeafQueues(), nodeCapacity);
    }

    @Test
    public void testFairShareResetsToZeroWhenAppsComplete() throws IOException {
        int nodeCapacity = 16 * 1024;
        createClusterWithQueuesAndOneNode(nodeCapacity, "fair");
        // Run apps in childA1,childA2 which are under parentA
        ApplicationAttemptId app1 = createSchedulingRequest((2 * 1024), "root.parentA.childA1", "user1");
        ApplicationAttemptId app2 = createSchedulingRequest((3 * 1024), "root.parentA.childA2", "user2");
        scheduler.update();
        // Verify if both the active queues under parentA get 50% fair
        // share
        for (int i = 1; i <= 2; i++) {
            Assert.assertEquals(50, ((((double) (scheduler.getQueueManager().getLeafQueue(("root.parentA.childA" + i), false).getFairShare().getMemorySize())) / nodeCapacity) * 100), 0.9);
        }
        // Let app under childA1 complete. This should cause the fair share
        // of queue childA1 to be reset to zero,since the queue has no apps running.
        // Queue childA2's fair share would increase to 100% since its the only
        // active queue.
        AppAttemptRemovedSchedulerEvent appRemovedEvent1 = new AppAttemptRemovedSchedulerEvent(app1, RMAppAttemptState.FINISHED, false);
        scheduler.handle(appRemovedEvent1);
        scheduler.update();
        Assert.assertEquals(0, ((((double) (scheduler.getQueueManager().getLeafQueue("root.parentA.childA1", false).getFairShare().getMemorySize())) / nodeCapacity) * 100), 0);
        Assert.assertEquals(100, ((((double) (scheduler.getQueueManager().getLeafQueue("root.parentA.childA2", false).getFairShare().getMemorySize())) / nodeCapacity) * 100), 0.1);
        verifySteadyFairShareMemory(scheduler.getQueueManager().getLeafQueues(), nodeCapacity);
    }

    @Test
    public void testFairShareWithDRFMultipleActiveQueuesUnderDifferentParent() throws IOException {
        int nodeMem = 16 * 1024;
        int nodeVCores = 10;
        createClusterWithQueuesAndOneNode(nodeMem, nodeVCores, "drf");
        // Run apps in childA1,childA2 which are under parentA
        createSchedulingRequest((2 * 1024), "root.parentA.childA1", "user1");
        createSchedulingRequest((3 * 1024), "root.parentA.childA2", "user2");
        // Run app in childB1 which is under parentB
        createSchedulingRequest((1 * 1024), "root.parentB.childB1", "user3");
        // Run app in root.default queue
        createSchedulingRequest((1 * 1024), "root.default", "user4");
        scheduler.update();
        // The two active child queues under parentA would
        // get 80/2=40% memory and vcores
        for (int i = 1; i <= 2; i++) {
            Assert.assertEquals(40, ((((double) (scheduler.getQueueManager().getLeafQueue(("root.parentA.childA" + i), false).getFairShare().getMemorySize())) / nodeMem) * 100), 0.9);
            Assert.assertEquals(40, ((((double) (scheduler.getQueueManager().getLeafQueue(("root.parentA.childA" + i), false).getFairShare().getVirtualCores())) / nodeVCores) * 100), 0.9);
        }
        // The only active child queue under parentB would get 10% memory and vcores
        Assert.assertEquals(10, ((((double) (scheduler.getQueueManager().getLeafQueue("root.parentB.childB1", false).getFairShare().getMemorySize())) / nodeMem) * 100), 0.9);
        Assert.assertEquals(10, ((((double) (scheduler.getQueueManager().getLeafQueue("root.parentB.childB1", false).getFairShare().getVirtualCores())) / nodeVCores) * 100), 0.9);
        Collection<FSLeafQueue> leafQueues = scheduler.getQueueManager().getLeafQueues();
        for (FSLeafQueue leaf : leafQueues) {
            if (leaf.getName().startsWith("root.parentA")) {
                Assert.assertEquals(0.2, (((double) (leaf.getSteadyFairShare().getMemorySize())) / nodeMem), 0.001);
                Assert.assertEquals(0.2, (((double) (leaf.getSteadyFairShare().getVirtualCores())) / nodeVCores), 0.001);
            } else
                if (leaf.getName().startsWith("root.parentB")) {
                    Assert.assertEquals(0.05, (((double) (leaf.getSteadyFairShare().getMemorySize())) / nodeMem), 0.001);
                    Assert.assertEquals(0.1, (((double) (leaf.getSteadyFairShare().getVirtualCores())) / nodeVCores), 0.001);
                }

        }
    }
}

