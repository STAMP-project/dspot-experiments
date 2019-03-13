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


import FairSchedulerConfiguration.ALLOCATION_FILE;
import FairSchedulerConfiguration.ALLOW_UNDECLARED_POOLS;
import FairSchedulerConfiguration.USER_AS_DEFAULT_QUEUE;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.MockNodes;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.AppAttemptRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeUpdateSchedulerEvent;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Assert;
import org.junit.Test;


/* This class is to  test the fair scheduler functionality of
deciding the number of runnable application under various conditions.
 */
public class TestAppRunnability extends FairSchedulerTestBase {
    private static final String ALLOC_FILE = new File(FairSchedulerTestBase.TEST_DIR, "test-queues").getAbsolutePath();

    @Test
    public void testUserAsDefaultQueue() throws Exception {
        conf.set(USER_AS_DEFAULT_QUEUE, "true");
        scheduler.reinitialize(conf, resourceManager.getRMContext());
        ApplicationAttemptId appAttemptId = createAppAttemptId(1, 1);
        createApplicationWithAMResource(appAttemptId, "default", "user1", null);
        Assert.assertEquals(1, scheduler.getQueueManager().getLeafQueue("user1", true).getNumRunnableApps());
        Assert.assertEquals(0, scheduler.getQueueManager().getLeafQueue("default", true).getNumRunnableApps());
        Assert.assertEquals("root.user1", resourceManager.getRMContext().getRMApps().get(appAttemptId.getApplicationId()).getQueue());
    }

    @Test
    public void testNotUserAsDefaultQueue() throws Exception {
        // Restarting resource manager since the Conf object is changed changed.
        resourceManager.stop();
        conf.set(USER_AS_DEFAULT_QUEUE, "false");
        resourceManager = new MockRM(conf);
        resourceManager.start();
        scheduler = ((FairScheduler) (resourceManager.getResourceScheduler()));
        ApplicationAttemptId appAttemptId = createAppAttemptId(1, 1);
        createApplicationWithAMResource(appAttemptId, "default", "user2", null);
        Assert.assertEquals(0, scheduler.getQueueManager().getLeafQueue("user1", true).getNumRunnableApps());
        Assert.assertEquals(1, scheduler.getQueueManager().getLeafQueue("default", true).getNumRunnableApps());
        Assert.assertEquals(0, scheduler.getQueueManager().getLeafQueue("user2", true).getNumRunnableApps());
    }

    @Test
    public void testAppAdditionAndRemoval() throws Exception {
        ApplicationAttemptId attemptId = createAppAttemptId(1, 1);
        AppAddedSchedulerEvent appAddedEvent = new AppAddedSchedulerEvent(attemptId.getApplicationId(), "default", "user1");
        scheduler.handle(appAddedEvent);
        AppAttemptAddedSchedulerEvent attemptAddedEvent = new AppAttemptAddedSchedulerEvent(createAppAttemptId(1, 1), false);
        scheduler.handle(attemptAddedEvent);
        // Scheduler should have two queues (the default and the one created for
        // user1)
        Assert.assertEquals(2, scheduler.getQueueManager().getLeafQueues().size());
        // That queue should have one app
        Assert.assertEquals(1, scheduler.getQueueManager().getLeafQueue("user1", true).getNumRunnableApps());
        AppAttemptRemovedSchedulerEvent appRemovedEvent1 = new AppAttemptRemovedSchedulerEvent(createAppAttemptId(1, 1), RMAppAttemptState.FINISHED, false);
        // Now remove app
        scheduler.handle(appRemovedEvent1);
        // Queue should have no apps
        Assert.assertEquals(0, scheduler.getQueueManager().getLeafQueue("user1", true).getNumRunnableApps());
    }

    @Test
    public void testPreemptionVariablesForQueueCreatedRuntime() throws Exception {
        // Set preemption variables for the root queue
        FSParentQueue root = scheduler.getQueueManager().getRootQueue();
        root.setMinSharePreemptionTimeout(10000);
        root.setFairSharePreemptionTimeout(15000);
        root.setFairSharePreemptionThreshold(0.6F);
        // User1 submits one application
        ApplicationAttemptId appAttemptId = createAppAttemptId(1, 1);
        createApplicationWithAMResource(appAttemptId, "default", "user1", null);
        // The user1 queue should inherit the configurations from the root queue
        FSLeafQueue userQueue = scheduler.getQueueManager().getLeafQueue("user1", true);
        Assert.assertEquals(1, userQueue.getNumRunnableApps());
        Assert.assertEquals(10000, userQueue.getMinSharePreemptionTimeout());
        Assert.assertEquals(15000, userQueue.getFairSharePreemptionTimeout());
        Assert.assertEquals(0.6F, userQueue.getFairSharePreemptionThreshold(), 0.001);
    }

    @Test
    public void testDontAllowUndeclaredPools() throws Exception {
        conf.setBoolean(ALLOW_UNDECLARED_POOLS, false);
        conf.set(ALLOCATION_FILE, TestAppRunnability.ALLOC_FILE);
        PrintWriter out = new PrintWriter(new FileWriter(TestAppRunnability.ALLOC_FILE));
        out.println("<?xml version=\"1.0\"?>");
        out.println("<allocations>");
        out.println("<queue name=\"jerry\">");
        out.println("</queue>");
        out.println("</allocations>");
        out.close();
        // Restarting resource manager since the file location and content is
        // changed.
        resourceManager.stop();
        resourceManager = new MockRM(conf);
        resourceManager.start();
        scheduler = ((FairScheduler) (resourceManager.getResourceScheduler()));
        QueueManager queueManager = scheduler.getQueueManager();
        FSLeafQueue jerryQueue = queueManager.getLeafQueue("jerry", false);
        FSLeafQueue defaultQueue = queueManager.getLeafQueue("default", false);
        // Should get put into jerry
        createSchedulingRequest(1024, "jerry", "someuser");
        Assert.assertEquals(1, jerryQueue.getNumRunnableApps());
        // Should get forced into default
        createSchedulingRequest(1024, "newqueue", "someuser");
        Assert.assertEquals(1, jerryQueue.getNumRunnableApps());
        Assert.assertEquals(1, defaultQueue.getNumRunnableApps());
        // Would get put into someuser because of user-as-default-queue, but should
        // be forced into default
        createSchedulingRequest(1024, "default", "someuser");
        Assert.assertEquals(1, jerryQueue.getNumRunnableApps());
        Assert.assertEquals(2, defaultQueue.getNumRunnableApps());
        // Should get put into jerry because of user-as-default-queue
        createSchedulingRequest(1024, "default", "jerry");
        Assert.assertEquals(2, jerryQueue.getNumRunnableApps());
        Assert.assertEquals(2, defaultQueue.getNumRunnableApps());
    }

    @Test
    public void testMoveRunnableApp() throws Exception {
        scheduler.reinitialize(conf, resourceManager.getRMContext());
        QueueManager queueMgr = scheduler.getQueueManager();
        FSLeafQueue oldQueue = queueMgr.getLeafQueue("queue1", true);
        FSLeafQueue targetQueue = queueMgr.getLeafQueue("queue2", true);
        ApplicationAttemptId appAttId = createSchedulingRequest(1024, 1, "queue1", "user1", 3);
        ApplicationId appId = appAttId.getApplicationId();
        RMNode node = MockNodes.newNodeInfo(1, Resources.createResource(1024));
        NodeAddedSchedulerEvent nodeEvent = new NodeAddedSchedulerEvent(node);
        NodeUpdateSchedulerEvent updateEvent = new NodeUpdateSchedulerEvent(node);
        scheduler.handle(nodeEvent);
        scheduler.handle(updateEvent);
        Assert.assertEquals(Resource.newInstance(1024, 1), oldQueue.getResourceUsage());
        scheduler.update();
        Assert.assertEquals(Resource.newInstance(3072, 3), oldQueue.getDemand());
        scheduler.moveApplication(appId, "queue2");
        FSAppAttempt app = scheduler.getSchedulerApp(appAttId);
        Assert.assertSame(targetQueue, app.getQueue());
        Assert.assertFalse(oldQueue.isRunnableApp(app));
        Assert.assertTrue(targetQueue.isRunnableApp(app));
        Assert.assertEquals(Resource.newInstance(0, 0), oldQueue.getResourceUsage());
        Assert.assertEquals(Resource.newInstance(1024, 1), targetQueue.getResourceUsage());
        Assert.assertEquals(0, oldQueue.getNumRunnableApps());
        Assert.assertEquals(1, targetQueue.getNumRunnableApps());
        Assert.assertEquals(1, queueMgr.getRootQueue().getNumRunnableApps());
        scheduler.update();
        Assert.assertEquals(Resource.newInstance(0, 0), oldQueue.getDemand());
        Assert.assertEquals(Resource.newInstance(3072, 3), targetQueue.getDemand());
    }

    @Test
    public void testMoveNonRunnableApp() throws Exception {
        QueueManager queueMgr = scheduler.getQueueManager();
        FSLeafQueue oldQueue = queueMgr.getLeafQueue("queue1", true);
        FSLeafQueue targetQueue = queueMgr.getLeafQueue("queue2", true);
        oldQueue.setMaxRunningApps(0);
        targetQueue.setMaxRunningApps(0);
        ApplicationAttemptId appAttId = createSchedulingRequest(1024, 1, "queue1", "user1", 3);
        Assert.assertEquals(0, oldQueue.getNumRunnableApps());
        scheduler.moveApplication(appAttId.getApplicationId(), "queue2");
        Assert.assertEquals(0, oldQueue.getNumRunnableApps());
        Assert.assertEquals(0, targetQueue.getNumRunnableApps());
        Assert.assertEquals(0, queueMgr.getRootQueue().getNumRunnableApps());
    }

    @Test
    public void testMoveMakesAppRunnable() throws Exception {
        QueueManager queueMgr = scheduler.getQueueManager();
        FSLeafQueue oldQueue = queueMgr.getLeafQueue("queue1", true);
        FSLeafQueue targetQueue = queueMgr.getLeafQueue("queue2", true);
        oldQueue.setMaxRunningApps(0);
        ApplicationAttemptId appAttId = createSchedulingRequest(1024, 1, "queue1", "user1", 3);
        FSAppAttempt app = scheduler.getSchedulerApp(appAttId);
        Assert.assertTrue(oldQueue.isNonRunnableApp(app));
        scheduler.moveApplication(appAttId.getApplicationId(), "queue2");
        Assert.assertFalse(oldQueue.isNonRunnableApp(app));
        Assert.assertFalse(targetQueue.isNonRunnableApp(app));
        Assert.assertTrue(targetQueue.isRunnableApp(app));
        Assert.assertEquals(1, targetQueue.getNumRunnableApps());
        Assert.assertEquals(1, queueMgr.getRootQueue().getNumRunnableApps());
    }
}

