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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.junit.Assert;
import org.junit.Test;


public class TestMaxRunningAppsEnforcer {
    private QueueManager queueManager;

    private Map<String, Integer> userMaxApps;

    private MaxRunningAppsEnforcer maxAppsEnforcer;

    private int appNum;

    private ControlledClock clock;

    private RMContext rmContext;

    private FairScheduler scheduler;

    @Test
    public void testRemoveDoesNotEnableAnyApp() {
        FSParentQueue root = queueManager.getRootQueue();
        FSLeafQueue leaf1 = queueManager.getLeafQueue("root.queue1", true);
        FSLeafQueue leaf2 = queueManager.getLeafQueue("root.queue2", true);
        root.setMaxRunningApps(2);
        leaf1.setMaxRunningApps(1);
        leaf2.setMaxRunningApps(1);
        FSAppAttempt app1 = addApp(leaf1, "user");
        addApp(leaf2, "user");
        addApp(leaf2, "user");
        Assert.assertEquals(1, leaf1.getNumRunnableApps());
        Assert.assertEquals(1, leaf2.getNumRunnableApps());
        Assert.assertEquals(1, leaf2.getNumNonRunnableApps());
        removeApp(app1);
        Assert.assertEquals(0, leaf1.getNumRunnableApps());
        Assert.assertEquals(1, leaf2.getNumRunnableApps());
        Assert.assertEquals(1, leaf2.getNumNonRunnableApps());
    }

    @Test
    public void testRemoveEnablesAppOnCousinQueue() {
        FSLeafQueue leaf1 = queueManager.getLeafQueue("root.queue1.subqueue1.leaf1", true);
        FSLeafQueue leaf2 = queueManager.getLeafQueue("root.queue1.subqueue2.leaf2", true);
        FSParentQueue queue1 = queueManager.getParentQueue("root.queue1", true);
        queue1.setMaxRunningApps(2);
        FSAppAttempt app1 = addApp(leaf1, "user");
        addApp(leaf2, "user");
        addApp(leaf2, "user");
        Assert.assertEquals(1, leaf1.getNumRunnableApps());
        Assert.assertEquals(1, leaf2.getNumRunnableApps());
        Assert.assertEquals(1, leaf2.getNumNonRunnableApps());
        removeApp(app1);
        Assert.assertEquals(0, leaf1.getNumRunnableApps());
        Assert.assertEquals(2, leaf2.getNumRunnableApps());
        Assert.assertEquals(0, leaf2.getNumNonRunnableApps());
    }

    @Test
    public void testRemoveEnablesOneByQueueOneByUser() {
        FSLeafQueue leaf1 = queueManager.getLeafQueue("root.queue1.leaf1", true);
        FSLeafQueue leaf2 = queueManager.getLeafQueue("root.queue1.leaf2", true);
        leaf1.setMaxRunningApps(2);
        userMaxApps.put("user1", 1);
        FSAppAttempt app1 = addApp(leaf1, "user1");
        addApp(leaf1, "user2");
        addApp(leaf1, "user3");
        addApp(leaf2, "user1");
        Assert.assertEquals(2, leaf1.getNumRunnableApps());
        Assert.assertEquals(1, leaf1.getNumNonRunnableApps());
        Assert.assertEquals(1, leaf2.getNumNonRunnableApps());
        removeApp(app1);
        Assert.assertEquals(2, leaf1.getNumRunnableApps());
        Assert.assertEquals(1, leaf2.getNumRunnableApps());
        Assert.assertEquals(0, leaf1.getNumNonRunnableApps());
        Assert.assertEquals(0, leaf2.getNumNonRunnableApps());
    }

    @Test
    public void testRemoveEnablingOrderedByStartTime() {
        FSLeafQueue leaf1 = queueManager.getLeafQueue("root.queue1.subqueue1.leaf1", true);
        FSLeafQueue leaf2 = queueManager.getLeafQueue("root.queue1.subqueue2.leaf2", true);
        FSParentQueue queue1 = queueManager.getParentQueue("root.queue1", true);
        queue1.setMaxRunningApps(2);
        FSAppAttempt app1 = addApp(leaf1, "user");
        addApp(leaf2, "user");
        addApp(leaf2, "user");
        clock.tickSec(20);
        addApp(leaf1, "user");
        Assert.assertEquals(1, leaf1.getNumRunnableApps());
        Assert.assertEquals(1, leaf2.getNumRunnableApps());
        Assert.assertEquals(1, leaf1.getNumNonRunnableApps());
        Assert.assertEquals(1, leaf2.getNumNonRunnableApps());
        removeApp(app1);
        Assert.assertEquals(0, leaf1.getNumRunnableApps());
        Assert.assertEquals(2, leaf2.getNumRunnableApps());
        Assert.assertEquals(0, leaf2.getNumNonRunnableApps());
    }

    @Test
    public void testMultipleAppsWaitingOnCousinQueue() {
        FSLeafQueue leaf1 = queueManager.getLeafQueue("root.queue1.subqueue1.leaf1", true);
        FSLeafQueue leaf2 = queueManager.getLeafQueue("root.queue1.subqueue2.leaf2", true);
        FSParentQueue queue1 = queueManager.getParentQueue("root.queue1", true);
        queue1.setMaxRunningApps(2);
        FSAppAttempt app1 = addApp(leaf1, "user");
        addApp(leaf2, "user");
        addApp(leaf2, "user");
        addApp(leaf2, "user");
        Assert.assertEquals(1, leaf1.getNumRunnableApps());
        Assert.assertEquals(1, leaf2.getNumRunnableApps());
        Assert.assertEquals(2, leaf2.getNumNonRunnableApps());
        removeApp(app1);
        Assert.assertEquals(0, leaf1.getNumRunnableApps());
        Assert.assertEquals(2, leaf2.getNumRunnableApps());
        Assert.assertEquals(1, leaf2.getNumNonRunnableApps());
    }

    @Test
    public void testMultiListStartTimeIteratorEmptyAppLists() {
        List<List<FSAppAttempt>> lists = new ArrayList<List<FSAppAttempt>>();
        lists.add(Arrays.asList(mockAppAttempt(1)));
        lists.add(Arrays.asList(mockAppAttempt(2)));
        Iterator<FSAppAttempt> iter = new MaxRunningAppsEnforcer.MultiListStartTimeIterator(lists);
        Assert.assertEquals(1, iter.next().getStartTime());
        Assert.assertEquals(2, iter.next().getStartTime());
    }
}

