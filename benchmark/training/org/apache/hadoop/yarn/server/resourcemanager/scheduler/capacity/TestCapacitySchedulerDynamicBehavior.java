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


import RMAppState.KILLED;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationConstants;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerDynamicEditException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.QueueEntitlement;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CapacitySchedulerConfiguration.ROOT;


public class TestCapacitySchedulerDynamicBehavior {
    private static final Logger LOG = LoggerFactory.getLogger(TestCapacitySchedulerDynamicBehavior.class);

    private static final String A = (ROOT) + ".a";

    private static final String B = (ROOT) + ".b";

    private static final String B1 = (TestCapacitySchedulerDynamicBehavior.B) + ".b1";

    private static final String B2 = (TestCapacitySchedulerDynamicBehavior.B) + ".b2";

    private static final String B3 = (TestCapacitySchedulerDynamicBehavior.B) + ".b3";

    private static float A_CAPACITY = 10.5F;

    private static float B_CAPACITY = 89.5F;

    private static float A1_CAPACITY = 30;

    private static float A2_CAPACITY = 70;

    private static float B1_CAPACITY = 79.2F;

    private static float B2_CAPACITY = 0.8F;

    private static float B3_CAPACITY = 20;

    private final TestCapacityScheduler tcs = new TestCapacityScheduler();

    private int GB = 1024;

    private MockRM rm;

    @Test
    public void testRefreshQueuesWithReservations() throws Exception {
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        // set default queue capacity to zero
        ((ReservationQueue) (cs.getQueue(("a" + (ReservationConstants.DEFAULT_QUEUE_SUFFIX))))).setEntitlement(new QueueEntitlement(0.0F, 1.0F));
        // Test add one reservation dynamically and manually modify capacity
        ReservationQueue a1 = new ReservationQueue(cs, "a1", ((PlanQueue) (cs.getQueue("a"))));
        cs.addQueue(a1);
        a1.setEntitlement(new QueueEntitlement(((TestCapacitySchedulerDynamicBehavior.A1_CAPACITY) / 100), 1.0F));
        // Test add another reservation queue and use setEntitlement to modify
        // capacity
        ReservationQueue a2 = new ReservationQueue(cs, "a2", ((PlanQueue) (cs.getQueue("a"))));
        cs.addQueue(a2);
        cs.setEntitlement("a2", new QueueEntitlement(((TestCapacitySchedulerDynamicBehavior.A2_CAPACITY) / 100), 1.0F));
        // Verify all allocations match
        tcs.checkQueueCapacities(cs, TestCapacitySchedulerDynamicBehavior.A_CAPACITY, TestCapacitySchedulerDynamicBehavior.B_CAPACITY);
        // Reinitialize and verify all dynamic queued survived
        CapacitySchedulerConfiguration conf = cs.getConfiguration();
        conf.setCapacity(TestCapacitySchedulerDynamicBehavior.A, 80.0F);
        conf.setCapacity(TestCapacitySchedulerDynamicBehavior.B, 20.0F);
        cs.reinitialize(conf, getRMContext());
        tcs.checkQueueCapacities(cs, 80.0F, 20.0F);
    }

    @Test
    public void testAddQueueFailCases() throws Exception {
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        try {
            // Test invalid addition (adding non-zero size queue)
            ReservationQueue a1 = new ReservationQueue(cs, "a1", ((PlanQueue) (cs.getQueue("a"))));
            a1.setEntitlement(new QueueEntitlement(((TestCapacitySchedulerDynamicBehavior.A1_CAPACITY) / 100), 1.0F));
            cs.addQueue(a1);
            Assert.fail();
        } catch (Exception e) {
            // expected
        }
        // Test add one reservation dynamically and manually modify capacity
        ReservationQueue a1 = new ReservationQueue(cs, "a1", ((PlanQueue) (cs.getQueue("a"))));
        cs.addQueue(a1);
        // set default queue capacity to zero
        ((ReservationQueue) (cs.getQueue(("a" + (ReservationConstants.DEFAULT_QUEUE_SUFFIX))))).setEntitlement(new QueueEntitlement(0.0F, 1.0F));
        a1.setEntitlement(new QueueEntitlement(((TestCapacitySchedulerDynamicBehavior.A1_CAPACITY) / 100), 1.0F));
        // Test add another reservation queue and use setEntitlement to modify
        // capacity
        ReservationQueue a2 = new ReservationQueue(cs, "a2", ((PlanQueue) (cs.getQueue("a"))));
        cs.addQueue(a2);
        try {
            // Test invalid entitlement (sum of queues exceed 100%)
            cs.setEntitlement("a2", new QueueEntitlement((((TestCapacitySchedulerDynamicBehavior.A2_CAPACITY) / 100) + 0.1F), 1.0F));
            Assert.fail();
        } catch (Exception e) {
            // expected
        }
        cs.setEntitlement("a2", new QueueEntitlement(((TestCapacitySchedulerDynamicBehavior.A2_CAPACITY) / 100), 1.0F));
        // Verify all allocations match
        tcs.checkQueueCapacities(cs, TestCapacitySchedulerDynamicBehavior.A_CAPACITY, TestCapacitySchedulerDynamicBehavior.B_CAPACITY);
        cs.stop();
    }

    @Test
    public void testRemoveQueue() throws Exception {
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        // Test add one reservation dynamically and manually modify capacity
        ReservationQueue a1 = new ReservationQueue(cs, "a1", ((PlanQueue) (cs.getQueue("a"))));
        cs.addQueue(a1);
        a1.setEntitlement(new QueueEntitlement(((TestCapacitySchedulerDynamicBehavior.A1_CAPACITY) / 100), 1.0F));
        // submit an app
        RMApp app = rm.submitApp(GB, "test-move-1", "user_0", null, "a1");
        // check preconditions
        List<ApplicationAttemptId> appsInA1 = cs.getAppsInQueue("a1");
        Assert.assertEquals(1, appsInA1.size());
        try {
            cs.removeQueue("a1");
            Assert.fail();
        } catch (SchedulerDynamicEditException s) {
            // expected a1 contains applications
        }
        // clear queue by killling all apps
        cs.killAllAppsInQueue("a1");
        // wait for events of move to propagate
        rm.waitForState(app.getApplicationId(), KILLED);
        try {
            cs.removeQueue("a1");
            Assert.fail();
        } catch (SchedulerDynamicEditException s) {
            // expected a1 is not zero capacity
        }
        // set capacity to zero
        cs.setEntitlement("a1", new QueueEntitlement(0.0F, 0.0F));
        cs.removeQueue("a1");
        Assert.assertTrue(((cs.getQueue("a1")) == null));
        stop();
    }

    @Test
    public void testMoveAppToPlanQueue() throws Exception {
        CapacityScheduler scheduler = ((CapacityScheduler) (getResourceScheduler()));
        // submit an app
        RMApp app = rm.submitApp(GB, "test-move-1", "user_0", null, "b1");
        ApplicationAttemptId appAttemptId = rm.getApplicationReport(app.getApplicationId()).getCurrentApplicationAttemptId();
        // check preconditions
        List<ApplicationAttemptId> appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertEquals(1, appsInB1.size());
        List<ApplicationAttemptId> appsInB = scheduler.getAppsInQueue("b");
        Assert.assertEquals(1, appsInB.size());
        Assert.assertTrue(appsInB.contains(appAttemptId));
        List<ApplicationAttemptId> appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.isEmpty());
        String queue = scheduler.getApplicationAttempt(appsInB1.get(0)).getQueue().getQueueName();
        Assert.assertEquals("b1", queue);
        List<ApplicationAttemptId> appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        // create the default reservation queue
        String defQName = "a" + (ReservationConstants.DEFAULT_QUEUE_SUFFIX);
        ReservationQueue defQ = new ReservationQueue(scheduler, defQName, ((PlanQueue) (scheduler.getQueue("a"))));
        scheduler.addQueue(defQ);
        defQ.setEntitlement(new QueueEntitlement(1.0F, 1.0F));
        List<ApplicationAttemptId> appsInDefQ = scheduler.getAppsInQueue(defQName);
        Assert.assertTrue(appsInDefQ.isEmpty());
        // now move the app to plan queue
        scheduler.moveApplication(app.getApplicationId(), "a");
        // check postconditions
        appsInDefQ = scheduler.getAppsInQueue(defQName);
        Assert.assertEquals(1, appsInDefQ.size());
        queue = scheduler.getApplicationAttempt(appsInDefQ.get(0)).getQueue().getQueueName();
        Assert.assertTrue(queue.equals(defQName));
        appsInA = scheduler.getAppsInQueue("a");
        Assert.assertTrue(appsInA.contains(appAttemptId));
        Assert.assertEquals(1, appsInA.size());
        appsInRoot = scheduler.getAppsInQueue("root");
        Assert.assertTrue(appsInRoot.contains(appAttemptId));
        Assert.assertEquals(1, appsInRoot.size());
        appsInB1 = scheduler.getAppsInQueue("b1");
        Assert.assertTrue(appsInB1.isEmpty());
        appsInB = scheduler.getAppsInQueue("b");
        Assert.assertTrue(appsInB.isEmpty());
        stop();
    }
}

