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
package org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity;


import CapacitySchedulerConfiguration.DEFAULT_PREEMPTION_MONITORING_INTERVAL;
import CapacitySchedulerConfiguration.DEFAULT_PREEMPTION_OBSERVE_ONLY;
import CapacitySchedulerConfiguration.PREEMPTION_MAX_IGNORED_OVER_CAPACITY;
import CapacitySchedulerConfiguration.PREEMPTION_MONITORING_INTERVAL;
import CapacitySchedulerConfiguration.PREEMPTION_NATURAL_TERMINATION_FACTOR;
import CapacitySchedulerConfiguration.PREEMPTION_OBSERVE_ONLY;
import CapacitySchedulerConfiguration.PREEMPTION_WAIT_TIME_BEFORE_KILL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.monitor.SchedulingMonitor;
import org.apache.hadoop.yarn.server.resourcemanager.monitor.SchedulingMonitorManager;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.ContainerPreemptEvent;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.resource.DefaultResourceCalculator;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestProportionalCapacityPreemptionPolicy {
    static final long TS = 3141592653L;

    int appAlloc = 0;

    boolean setAMContainer = false;

    boolean setLabeledContainer = false;

    float setAMResourcePercent = 0.0F;

    Random rand = null;

    Clock mClock = null;

    CapacitySchedulerConfiguration conf = null;

    CapacityScheduler mCS = null;

    RMContext rmContext = null;

    RMNodeLabelsManager lm = null;

    EventHandler<Event> mDisp = null;

    ResourceCalculator rc = new DefaultResourceCalculator();

    Resource clusterResources = null;

    final ApplicationAttemptId appA = ApplicationAttemptId.newInstance(ApplicationId.newInstance(TestProportionalCapacityPreemptionPolicy.TS, 0), 0);

    final ApplicationAttemptId appB = ApplicationAttemptId.newInstance(ApplicationId.newInstance(TestProportionalCapacityPreemptionPolicy.TS, 1), 0);

    final ApplicationAttemptId appC = ApplicationAttemptId.newInstance(ApplicationId.newInstance(TestProportionalCapacityPreemptionPolicy.TS, 2), 0);

    final ApplicationAttemptId appD = ApplicationAttemptId.newInstance(ApplicationId.newInstance(TestProportionalCapacityPreemptionPolicy.TS, 3), 0);

    final ApplicationAttemptId appE = ApplicationAttemptId.newInstance(ApplicationId.newInstance(TestProportionalCapacityPreemptionPolicy.TS, 4), 0);

    final ApplicationAttemptId appF = ApplicationAttemptId.newInstance(ApplicationId.newInstance(TestProportionalCapacityPreemptionPolicy.TS, 4), 0);

    final ArgumentCaptor<ContainerPreemptEvent> evtCaptor = ArgumentCaptor.forClass(ContainerPreemptEvent.class);

    public enum priority {

        AMCONTAINER(0),
        CONTAINER(1),
        LABELEDCONTAINER(2);
        int value;

        priority(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    @Rule
    public TestName name = new TestName();

    private static final int[][] Q_DATA_FOR_IGNORE = new int[][]{ // /   A   B   C
    new int[]{ 100, 40, 40, 20 }// abs
    // abs
    // abs
    , new int[]{ 100, 100, 100, 100 }// maxCap
    // maxCap
    // maxCap
    , new int[]{ 100, 0, 60, 40 }// used
    // used
    // used
    , new int[]{ 0, 0, 0, 0 }// pending
    // pending
    // pending
    , new int[]{ 0, 0, 0, 0 }// reserved
    // reserved
    // reserved
    , new int[]{ 3, 1, 1, 1 }// apps
    // apps
    // apps
    , new int[]{ -1, 1, 1, 1 }// req granularity
    // req granularity
    // req granularity
    , new int[]{ 3, 0, 0, 0 }// subqueues
    // subqueues
    // subqueues
     };

    @Test
    public void testIgnore() {
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(TestProportionalCapacityPreemptionPolicy.Q_DATA_FOR_IGNORE);
        policy.editSchedule();
        // don't correct imbalances without demand
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.isA(ContainerPreemptEvent.class));
    }

    @Test
    public void testProportionalPreemption() {
        int[][] qData = new int[][]{ // /   A   B   C  D
        new int[]{ 100, 10, 40, 20, 30 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100, 100, 100 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 100, 30, 60, 10, 0 }// used
        // used
        // used
        , new int[]{ 45, 20, 5, 20, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 3, 1, 1, 1, 0 }// apps
        // apps
        // apps
        , new int[]{ -1, 1, 1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 4, 0, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // A will preempt guaranteed-allocated.
        Mockito.verify(mDisp, Mockito.times(10)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
    }

    @Test
    public void testMaxCap() {
        int[][] qData = new int[][]{ // /   A   B   C
        new int[]{ 100, 40, 40, 20 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 45, 100 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 100, 55, 45, 0 }// used
        // used
        // used
        , new int[]{ 20, 10, 10, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 2, 1, 1, 0 }// apps
        // apps
        // apps
        , new int[]{ -1, 1, 1, 0 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // despite the imbalance, since B is at maxCap, do not correct
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
    }

    @Test
    public void testPreemptCycle() {
        int[][] qData = new int[][]{ // /   A   B   C
        new int[]{ 100, 40, 40, 20 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100, 100 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 100, 0, 60, 40 }// used
        // used
        // used
        , new int[]{ 10, 10, 0, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 3, 1, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, 1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // ensure all pending rsrc from A get preempted from other queues
        Mockito.verify(mDisp, Mockito.times(10)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
    }

    @Test
    public void testExpireKill() {
        final long killTime = 10000L;
        int[][] qData = new int[][]{ // /   A   B   C
        new int[]{ 100, 40, 40, 20 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100, 100 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 100, 0, 60, 40 }// used
        // used
        // used
        , new int[]{ 10, 10, 0, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 3, 1, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, 1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        conf.setLong(PREEMPTION_WAIT_TIME_BEFORE_KILL, killTime);
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        // ensure all pending rsrc from A get preempted from other queues
        Mockito.when(mClock.getTime()).thenReturn(0L);
        policy.editSchedule();
        Mockito.verify(mDisp, Mockito.times(10)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
        // requests reiterated
        Mockito.when(mClock.getTime()).thenReturn((killTime / 2));
        policy.editSchedule();
        Mockito.verify(mDisp, Mockito.times(10)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
        // kill req sent
        Mockito.when(mClock.getTime()).thenReturn((killTime + 1));
        policy.editSchedule();
        Mockito.verify(mDisp, Mockito.times(20)).handle(evtCaptor.capture());
        List<ContainerPreemptEvent> events = evtCaptor.getAllValues();
        for (ContainerPreemptEvent e : events.subList(20, 20)) {
            Assert.assertEquals(appC, e.getAppId());
            Assert.assertEquals(SchedulerEventType.MARK_CONTAINER_FOR_KILLABLE, e.getType());
        }
    }

    @Test
    public void testDeadzone() {
        int[][] qData = new int[][]{ // /   A   B   C
        new int[]{ 100, 40, 40, 20 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100, 100 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 100, 39, 43, 21 }// used
        // used
        // used
        , new int[]{ 10, 10, 0, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 3, 1, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, 1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        conf.setFloat(PREEMPTION_MAX_IGNORED_OVER_CAPACITY, ((float) (0.1)));
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // ignore 10% overcapacity to avoid jitter
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.isA(ContainerPreemptEvent.class));
    }

    @Test
    public void testPerQueueDisablePreemption() {
        int[][] qData = new int[][]{ // /    A    B    C
        new int[]{ 100, 55, 25, 20 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100, 100 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 100, 0, 54, 46 }// used
        // used
        // used
        , new int[]{ 10, 10, 0, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , // appA appB appC
        new int[]{ 3, 1, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, 1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        conf.setPreemptionDisabled("root.queueB", true);
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // Since queueB is not preemptable, get resources from queueC
        Mockito.verify(mDisp, Mockito.times(10)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appB)));
        // Since queueB is preemptable, resources will be preempted
        // from both queueB and queueC. Test must be reset so that the mDisp
        // event handler will count only events from the following test and not the
        // previous one.
        setup();
        conf.setPreemptionDisabled("root.queueB", false);
        ProportionalCapacityPreemptionPolicy policy2 = buildPolicy(qData);
        policy2.editSchedule();
        Mockito.verify(mDisp, Mockito.times(4)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appB)));
        Mockito.verify(mDisp, Mockito.times(6)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
    }

    @Test
    public void testPerQueueDisablePreemptionHierarchical() {
        int[][] qData = new int[][]{ // /    A              D
        // B    C         E    F
        new int[]{ 200, 100, 50, 50, 100, 10, 90 }// abs
        // abs
        // abs
        , new int[]{ 200, 200, 200, 200, 200, 200, 200 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 200, 110, 60, 50, 90, 90, 0 }// used
        // used
        // used
        , new int[]{ 10, 0, 0, 0, 10, 0, 10 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , // appA appB      appC appD
        new int[]{ 4, 2, 1, 1, 2, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, 1, -1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 2, 2, 0, 0, 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // verify capacity taken from queueB (appA), not queueE (appC) despite
        // queueE being far over its absolute capacity because queueA (queueB's
        // parent) is over capacity and queueD (queueE's parent) is not.
        ApplicationAttemptId expectedAttemptOnQueueB = ApplicationAttemptId.newInstance(appA.getApplicationId(), appA.getAttemptId());
        Assert.assertTrue("appA should be running on queueB", mCS.getAppsInQueue("queueB").contains(expectedAttemptOnQueueB));
        Mockito.verify(mDisp, Mockito.times(10)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
        // Need to call setup() again to reset mDisp
        setup();
        // Turn off preemption for queueB and it's children
        conf.setPreemptionDisabled("root.queueA.queueB", true);
        ProportionalCapacityPreemptionPolicy policy2 = buildPolicy(qData);
        policy2.editSchedule();
        ApplicationAttemptId expectedAttemptOnQueueC = ApplicationAttemptId.newInstance(appB.getApplicationId(), appB.getAttemptId());
        ApplicationAttemptId expectedAttemptOnQueueE = ApplicationAttemptId.newInstance(appC.getApplicationId(), appC.getAttemptId());
        // Now, all of queueB's (appA) over capacity is not preemptable, so neither
        // is queueA's. Verify that capacity is taken from queueE (appC).
        Assert.assertTrue("appB should be running on queueC", mCS.getAppsInQueue("queueC").contains(expectedAttemptOnQueueC));
        Assert.assertTrue("appC should be running on queueE", mCS.getAppsInQueue("queueE").contains(expectedAttemptOnQueueE));
        // Resources should have come from queueE (appC) and neither of queueA's
        // children.
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
        Mockito.verify(mDisp, Mockito.times(10)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
    }

    @Test
    public void testPerQueueDisablePreemptionBroadHierarchical() {
        int[][] qData = new int[][]{ // /    A              D              G
        // B    C         E    F         H    I
        new int[]{ 1000, 350, 150, 200, 400, 200, 200, 250, 100, 150 }// abs
        // abs
        // abs
        , new int[]{ 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 1000, 400, 200, 200, 400, 250, 150, 200, 150, 50 }// used
        // used
        // used
        , new int[]{ 50, 0, 0, 0, 50, 0, 50, 0, 0, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , // appA appB      appC appD      appE appF
        new int[]{ 6, 2, 1, 1, 2, 1, 1, 2, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, 1, -1, 1, 1, -1, 1, 1 }// req granulrity
        // req granulrity
        // req granulrity
        , new int[]{ 3, 2, 0, 0, 2, 0, 0, 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // queueF(appD) wants resources, Verify that resources come from queueE(appC)
        // because it's a sibling and queueB(appA) because queueA is over capacity.
        Mockito.verify(mDisp, Mockito.times(27)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
        Mockito.verify(mDisp, Mockito.times(23)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
        // Need to call setup() again to reset mDisp
        setup();
        // Turn off preemption for queueB(appA)
        conf.setPreemptionDisabled("root.queueA.queueB", true);
        ProportionalCapacityPreemptionPolicy policy2 = buildPolicy(qData);
        policy2.editSchedule();
        // Now that queueB(appA) is not preemptable, verify that resources come
        // from queueE(appC)
        Mockito.verify(mDisp, Mockito.times(50)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
        setup();
        // Turn off preemption for two of the 3 queues with over-capacity.
        conf.setPreemptionDisabled("root.queueD.queueE", true);
        conf.setPreemptionDisabled("root.queueA.queueB", true);
        ProportionalCapacityPreemptionPolicy policy3 = buildPolicy(qData);
        policy3.editSchedule();
        // Verify that the request was starved out even though queueH(appE) is
        // over capacity. This is because queueG (queueH's parent) is NOT
        // overcapacity.
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));// queueB

        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appB)));// queueC

        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));// queueE

        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appE)));// queueH

        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appF)));// queueI

    }

    @Test
    public void testPerQueueDisablePreemptionInheritParent() {
        int[][] qData = new int[][]{ // /    A                   E
        // B    C    D         F    G    H
        new int[]{ 1000, 500, 200, 200, 100, 500, 200, 200, 100 }// abs (guar)
        // abs (guar)
        // abs (guar)
        , new int[]{ 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 1000, 700, 0, 350, 350, 300, 0, 200, 100 }// used
        // used
        // used
        , new int[]{ 200, 0, 0, 0, 0, 200, 200, 0, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , // appA appB      appC appD appE
        new int[]{ 5, 2, 0, 1, 1, 3, 1, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, 1, 1, -1, 1, 1, 1 }// req granulrity
        // req granulrity
        // req granulrity
        , new int[]{ 2, 3, 0, 0, 0, 3, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // With all queues preemptable, resources should be taken from queueC(appA)
        // and queueD(appB). Resources taken more from queueD(appB) than
        // queueC(appA) because it's over its capacity by a larger percentage.
        Mockito.verify(mDisp, Mockito.times(17)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
        Mockito.verify(mDisp, Mockito.times(183)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appB)));
        // Turn off preemption for queueA and it's children. queueF(appC)'s request
        // should starve.
        setup();// Call setup() to reset mDisp

        conf.setPreemptionDisabled("root.queueA", true);
        ProportionalCapacityPreemptionPolicy policy2 = buildPolicy(qData);
        policy2.editSchedule();
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));// queueC

        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appB)));// queueD

        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appD)));// queueG

        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appE)));// queueH

    }

    @Test
    public void testPerQueuePreemptionNotAllUntouchable() {
        int[][] qData = new int[][]{ // /      A                       E
        // B     C     D           F     G     H
        new int[]{ 2000, 1000, 800, 100, 100, 1000, 500, 300, 200 }// abs
        // abs
        // abs
        , new int[]{ 2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000, 2000 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 2000, 1300, 300, 800, 200, 700, 500, 0, 200 }// used
        // used
        // used
        , new int[]{ 300, 0, 0, 0, 0, 300, 0, 300, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , // appA  appB  appC        appD  appE  appF
        new int[]{ 6, 3, 1, 1, 1, 3, 1, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, 1, 1, -1, 1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 2, 3, 0, 0, 0, 3, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        conf.setPreemptionDisabled("root.queueA.queueC", true);
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // Although queueC(appB) is way over capacity and is untouchable,
        // queueD(appC) is preemptable. Request should be filled from queueD(appC).
        Mockito.verify(mDisp, Mockito.times(100)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
    }

    @Test
    public void testPerQueueDisablePreemptionRootDisablesAll() {
        int[][] qData = new int[][]{ // /    A              D              G
        // B    C         E    F         H    I
        new int[]{ 1000, 500, 250, 250, 250, 100, 150, 250, 100, 150 }// abs
        // abs
        // abs
        , new int[]{ 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 1000, 20, 0, 20, 490, 240, 250, 490, 240, 250 }// used
        // used
        // used
        , new int[]{ 200, 200, 200, 0, 0, 0, 0, 0, 0, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , // appA appB      appC appD      appE appF
        new int[]{ 6, 2, 1, 1, 2, 1, 1, 2, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, 1, -1, 1, 1, -1, 1, 1 }// req granulrity
        // req granulrity
        // req granulrity
        , new int[]{ 3, 2, 0, 0, 2, 0, 0, 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        conf.setPreemptionDisabled("root", true);
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // All queues should be non-preemptable, so request should starve.
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appB)));// queueC

        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));// queueE

        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appD)));// queueB

        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appE)));// queueH

        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appF)));// queueI

    }

    @Test
    public void testPerQueueDisablePreemptionOverAbsMaxCapacity() {
        int[][] qData = new int[][]{ // /    A              D
        // B    C         E    F
        new int[]{ 1000, 725, 360, 365, 275, 17, 258 }// absCap
        // absCap
        // absCap
        , new int[]{ 1000, 1000, 1000, 1000, 550, 109, 1000 }// absMaxCap
        // absMaxCap
        // absMaxCap
        , new int[]{ 1000, 741, 396, 345, 259, 110, 149 }// used
        // used
        // used
        , new int[]{ 40, 20, 0, 20, 20, 20, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , // appA appB     appC appD
        new int[]{ 4, 2, 1, 1, 2, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, 1, -1, 1, 1 }// req granulrity
        // req granulrity
        // req granulrity
        , new int[]{ 2, 2, 0, 0, 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        // QueueE inherits non-preemption from QueueD
        conf.setPreemptionDisabled("root.queueD", true);
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // appC is running on QueueE. QueueE is over absMaxCap, but is not
        // preemptable. Therefore, appC resources should not be preempted.
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
    }

    @Test
    public void testOverCapacityImbalance() {
        int[][] qData = new int[][]{ // /   A   B   C
        new int[]{ 100, 40, 40, 20 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100, 100 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 100, 55, 45, 0 }// used
        // used
        // used
        , new int[]{ 20, 10, 10, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 2, 1, 1, 0 }// apps
        // apps
        // apps
        , new int[]{ -1, 1, 1, 0 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // Will not preempt for over capacity queues
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
    }

    @Test
    public void testNaturalTermination() {
        int[][] qData = new int[][]{ // /   A   B   C
        new int[]{ 100, 40, 40, 20 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100, 100 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 100, 55, 45, 0 }// used
        // used
        // used
        , new int[]{ 20, 10, 10, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 2, 1, 1, 0 }// apps
        // apps
        // apps
        , new int[]{ -1, 1, 1, 0 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        conf.setFloat(PREEMPTION_NATURAL_TERMINATION_FACTOR, ((float) (0.1)));
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // ignore 10% imbalance between over-capacity queues
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.isA(ContainerPreemptEvent.class));
    }

    @Test
    public void testObserveOnly() {
        int[][] qData = new int[][]{ // /   A   B   C
        new int[]{ 100, 40, 40, 20 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100, 100 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 100, 90, 10, 0 }// used
        // used
        // used
        , new int[]{ 80, 10, 20, 50 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 2, 1, 1, 0 }// apps
        // apps
        // apps
        , new int[]{ -1, 1, 1, 0 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        conf.setBoolean(PREEMPTION_OBSERVE_ONLY, true);
        Mockito.when(mCS.getConfiguration()).thenReturn(new CapacitySchedulerConfiguration(conf));
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // verify even severe imbalance not affected
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.isA(ContainerPreemptEvent.class));
    }

    @Test
    public void testHierarchical() {
        int[][] qData = new int[][]{ // /    A   B   C    D   E   F
        new int[]{ 200, 100, 50, 50, 100, 10, 90 }// abs
        // abs
        // abs
        , new int[]{ 200, 200, 200, 200, 200, 200, 200 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 200, 110, 60, 50, 90, 90, 0 }// used
        // used
        // used
        , new int[]{ 10, 0, 0, 0, 10, 0, 10 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 4, 2, 1, 1, 2, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, 1, -1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 2, 2, 0, 0, 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // verify capacity taken from A1, not B1 despite B1 being far over
        // its absolute guaranteed capacity
        Mockito.verify(mDisp, Mockito.times(10)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
    }

    @Test
    public void testHierarchicalWithReserved() {
        int[][] qData = new int[][]{ // /    A   B   C    D   E   F
        new int[]{ 200, 100, 50, 50, 100, 10, 90 }// abs
        // abs
        // abs
        , new int[]{ 200, 200, 200, 200, 200, 200, 200 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 200, 110, 60, 50, 90, 90, 0 }// used
        // used
        // used
        , new int[]{ 10, 0, 0, 0, 10, 0, 10 }// pending
        // pending
        // pending
        , new int[]{ 40, 25, 15, 10, 15, 15, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 4, 2, 1, 1, 2, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, 1, -1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 2, 2, 0, 0, 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // verify capacity taken from A1, not B1 despite B1 being far over
        // its absolute guaranteed capacity
        Mockito.verify(mDisp, Mockito.times(10)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
    }

    @Test
    public void testZeroGuar() {
        int[][] qData = new int[][]{ // /    A   B   C    D   E   F
        new int[]{ 200, 100, 0, 99, 100, 10, 90 }// abs
        // abs
        // abs
        , new int[]{ 200, 200, 200, 200, 200, 200, 200 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 170, 80, 60, 20, 90, 90, 0 }// used
        // used
        // used
        , new int[]{ 10, 0, 0, 0, 10, 0, 10 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 4, 2, 1, 1, 2, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, 1, -1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 2, 2, 0, 0, 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // verify capacity taken from A1, not B1 despite B1 being far over
        // its absolute guaranteed capacity
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
    }

    @Test
    public void testZeroGuarOverCap() {
        int[][] qData = new int[][]{ // /    A   B   C    D   E   F
        new int[]{ 200, 100, 0, 100, 0, 100, 100 }// abs
        // abs
        // abs
        , new int[]{ 200, 200, 200, 200, 200, 200, 200 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 170, 170, 60, 20, 90, 0, 0 }// used
        // used
        // used
        , new int[]{ 85, 50, 30, 10, 10, 20, 20 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 4, 3, 1, 1, 1, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, 1, 1, -1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 2, 3, 0, 0, 0, 1, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // No preemption should happen because zero guaranteed queues should be
        // treated as always satisfied, they should not preempt from each other.
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appB)));
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appD)));
    }

    @Test
    public void testHierarchicalLarge() {
        int[][] qData = new int[][]{ // /    A              D              G
        // B    C         E    F         H    I
        new int[]{ 400, 200, 60, 140, 100, 70, 30, 100, 10, 90 }// abs
        // abs
        // abs
        , new int[]{ 400, 400, 400, 400, 400, 400, 400, 400, 400, 400 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 400, 210, 70, 140, 100, 50, 50, 90, 90, 0 }// used
        // used
        // used
        , new int[]{ 15, 0, 0, 0, 0, 0, 0, 0, 0, 15 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , // appA appB      appC appD      appE appF
        new int[]{ 6, 2, 1, 1, 2, 1, 1, 2, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, 1, -1, 1, 1, -1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 2, 0, 0, 2, 0, 0, 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // verify capacity taken from A1, not H1 despite H1 being far over
        // its absolute guaranteed capacity
        // XXX note: compensating for rounding error in Resources.multiplyTo
        // which is likely triggered since we use small numbers for readability
        Mockito.verify(mDisp, Mockito.times(9)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
        Mockito.verify(mDisp, Mockito.times(6)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appE)));
    }

    @Test
    public void testContainerOrdering() {
        List<RMContainer> containers = new ArrayList<RMContainer>();
        ApplicationAttemptId appAttId = ApplicationAttemptId.newInstance(ApplicationId.newInstance(TestProportionalCapacityPreemptionPolicy.TS, 10), 0);
        // create a set of containers
        RMContainer rm1 = mockContainer(appAttId, 5, Mockito.mock(Resource.class), 3);
        RMContainer rm2 = mockContainer(appAttId, 3, Mockito.mock(Resource.class), 3);
        RMContainer rm3 = mockContainer(appAttId, 2, Mockito.mock(Resource.class), 2);
        RMContainer rm4 = mockContainer(appAttId, 1, Mockito.mock(Resource.class), 2);
        RMContainer rm5 = mockContainer(appAttId, 4, Mockito.mock(Resource.class), 1);
        // insert them in non-sorted order
        containers.add(rm3);
        containers.add(rm2);
        containers.add(rm1);
        containers.add(rm5);
        containers.add(rm4);
        // sort them
        FifoCandidatesSelector.sortContainers(containers);
        // verify the "priority"-first, "reverse container-id"-second
        // ordering is enforced correctly
        assert containers.get(0).equals(rm1);
        assert containers.get(1).equals(rm2);
        assert containers.get(2).equals(rm3);
        assert containers.get(3).equals(rm4);
        assert containers.get(4).equals(rm5);
    }

    @Test
    public void testPolicyInitializeAfterSchedulerInitialized() {
        @SuppressWarnings("resource")
        MockRM rm = new MockRM(conf);
        rm.init(conf);
        // ProportionalCapacityPreemptionPolicy should be initialized after
        // CapacityScheduler initialized. We will
        // 1) find SchedulingMonitor from RMActiveService's service list,
        // 2) check if ResourceCalculator in policy is null or not.
        // If it's not null, we can come to a conclusion that policy initialized
        // after scheduler got initialized
        // Get SchedulingMonitor from SchedulingMonitorManager instead
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        SchedulingMonitorManager smm = cs.getSchedulingMonitorManager();
        Service service = smm.getAvailableSchedulingMonitor();
        if (service instanceof SchedulingMonitor) {
            ProportionalCapacityPreemptionPolicy policy = ((ProportionalCapacityPreemptionPolicy) (getSchedulingEditPolicy()));
            Assert.assertNotNull(policy.getResourceCalculator());
            return;
        }
        Assert.fail("Failed to find SchedulingMonitor service, please check what happened");
    }

    @Test
    public void testSkipAMContainer() {
        int[][] qData = new int[][]{ // /   A   B
        new int[]{ 100, 50, 50 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100 }// maxcap
        // maxcap
        // maxcap
        , new int[]{ 100, 100, 0 }// used
        // used
        // used
        , new int[]{ 70, 20, 50 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 5, 4, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        setAMContainer = true;
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // By skipping AM Container, all other 24 containers of appD will be
        // preempted
        Mockito.verify(mDisp, Mockito.times(24)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appD)));
        // By skipping AM Container, all other 24 containers of appC will be
        // preempted
        Mockito.verify(mDisp, Mockito.times(24)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
        // Since AM containers of appC and appD are saved, 2 containers from appB
        // has to be preempted.
        Mockito.verify(mDisp, Mockito.times(2)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appB)));
        setAMContainer = false;
    }

    @Test
    public void testPreemptSkippedAMContainers() {
        int[][] qData = new int[][]{ // /   A   B
        new int[]{ 100, 10, 90 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100 }// maxcap
        // maxcap
        // maxcap
        , new int[]{ 100, 100, 0 }// used
        // used
        // used
        , new int[]{ 70, 20, 90 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 5, 4, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, 5, 5 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        setAMContainer = true;
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // All 5 containers of appD will be preempted including AM container.
        Mockito.verify(mDisp, Mockito.times(5)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appD)));
        // All 5 containers of appC will be preempted including AM container.
        Mockito.verify(mDisp, Mockito.times(5)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
        // By skipping AM Container, all other 4 containers of appB will be
        // preempted
        Mockito.verify(mDisp, Mockito.times(4)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appB)));
        // By skipping AM Container, all other 4 containers of appA will be
        // preempted
        Mockito.verify(mDisp, Mockito.times(4)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
        setAMContainer = false;
    }

    @Test
    public void testAMResourcePercentForSkippedAMContainers() {
        int[][] qData = new int[][]{ // /   A   B
        new int[]{ 100, 10, 90 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100 }// maxcap
        // maxcap
        // maxcap
        , new int[]{ 100, 100, 0 }// used
        // used
        // used
        , new int[]{ 70, 20, 90 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 5, 4, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, 5, 5 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        setAMContainer = true;
        setAMResourcePercent = 0.5F;
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // AMResoucePercent is 50% of cluster and maxAMCapacity will be 5Gb.
        // Total used AM container size is 20GB, hence 2 AM container has
        // to be preempted as Queue Capacity is 10Gb.
        Mockito.verify(mDisp, Mockito.times(5)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appD)));
        // Including AM Container, all other 4 containers of appC will be
        // preempted
        Mockito.verify(mDisp, Mockito.times(5)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
        // By skipping AM Container, all other 4 containers of appB will be
        // preempted
        Mockito.verify(mDisp, Mockito.times(4)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appB)));
        // By skipping AM Container, all other 4 containers of appA will be
        // preempted
        Mockito.verify(mDisp, Mockito.times(4)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
        setAMContainer = false;
    }

    @Test
    public void testPreemptionWithVCoreResource() {
        int[][] qData = new int[][]{ // / A B
        new int[]{ 100, 100, 100 }// maxcap
        // maxcap
        // maxcap
        , new int[]{ 5, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        // Resources can be set like memory:vcores
        String[][] resData = new String[][]{ // / A B
        new String[]{ "100:100", "50:50", "50:50" }// abs
        // abs
        // abs
        , new String[]{ "10:100", "10:100", "0" }// used
        // used
        // used
        , new String[]{ "70:20", "70:20", "10:100" }// pending
        // pending
        // pending
        , new String[]{ "0", "0", "0" }// reserved
        // reserved
        // reserved
        , new String[]{ "-1", "1:10", "1:10" }// req granularity
        // req granularity
        // req granularity
         };
        // Passing last param as TRUE to use DominantResourceCalculator
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData, resData, true);
        policy.editSchedule();
        // 5 containers will be preempted here
        Mockito.verify(mDisp, Mockito.times(5)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
    }

    @Test
    public void testHierarchicalLarge3Levels() {
        int[][] qData = new int[][]{ // /    A                      F               I
        // B    C                  G    H          J    K
        // D    E
        new int[]{ 400, 200, 60, 140, 100, 40, 100, 70, 30, 100, 10, 90 }// abs
        // abs
        // abs
        , new int[]{ 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 400, 210, 60, 150, 100, 50, 100, 50, 50, 90, 10, 80 }// used
        // used
        // used
        , new int[]{ 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10 }// pending
        // pending
        // pending
        , new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }// reserved
        // reserved
        // reserved
        , // appA     appB appC   appD appE      appF appG
        new int[]{ 7, 3, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, -1, 1, 1, -1, 1, 1, -1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 2, 0, 2, 0, 0, 2, 0, 0, 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // XXX note: compensating for rounding error in Resources.multiplyTo
        // which is likely triggered since we use small numbers for readability
        // run with Logger.getRootLogger().setLevel(Level.DEBUG);
        Mockito.verify(mDisp, Mockito.times(9)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
        Assert.assertEquals(10, policy.getQueuePartitions().get("queueE").get("").preemptableExtra.getMemorySize());
        // 2nd level child(E) preempts 10, but parent A has only 9 extra
        // check the parent can prempt only the extra from > 2 level child
        TempQueuePerPartition tempQueueAPartition = policy.getQueuePartitions().get("queueA").get("");
        Assert.assertEquals(0, tempQueueAPartition.untouchableExtra.getMemorySize());
        long extraForQueueA = (tempQueueAPartition.getUsed().getMemorySize()) - (tempQueueAPartition.getGuaranteed().getMemorySize());
        Assert.assertEquals(extraForQueueA, tempQueueAPartition.preemptableExtra.getMemorySize());
    }

    @Test
    public void testHierarchicalLarge3LevelsWithReserved() {
        int[][] qData = new int[][]{ // /    A                      F               I
        // B    C                  G    H          J    K
        // D    E
        new int[]{ 400, 200, 60, 140, 100, 40, 100, 70, 30, 100, 10, 90 }// abs
        // abs
        // abs
        , new int[]{ 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400, 400 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 400, 210, 60, 150, 100, 50, 100, 50, 50, 90, 10, 80 }// used
        // used
        // used
        , new int[]{ 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10 }// pending
        // pending
        // pending
        , new int[]{ 50, 30, 20, 10, 5, 5, 0, 0, 0, 10, 10, 0 }// reserved
        // reserved
        // reserved
        , // appA     appB appC   appD appE      appF appG
        new int[]{ 7, 3, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1 }// apps
        // apps
        // apps
        , new int[]{ -1, -1, 1, -1, 1, 1, -1, 1, 1, -1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 2, 0, 2, 0, 0, 2, 0, 0, 2, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        Mockito.verify(mDisp, Mockito.times(9)).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appC)));
        Assert.assertEquals(10, policy.getQueuePartitions().get("queueE").get("").preemptableExtra.getMemorySize());
        // 2nd level child(E) preempts 10, but parent A has only 9 extra
        // check the parent can prempt only the extra from > 2 level child
        TempQueuePerPartition tempQueueAPartition = policy.getQueuePartitions().get("queueA").get("");
        Assert.assertEquals(0, tempQueueAPartition.untouchableExtra.getMemorySize());
        long extraForQueueA = (tempQueueAPartition.getUsed().getMemorySize()) - (tempQueueAPartition.getGuaranteed().getMemorySize());
        Assert.assertEquals(extraForQueueA, tempQueueAPartition.preemptableExtra.getMemorySize());
    }

    @Test
    public void testPreemptionNotHappenForSingleReservedQueue() {
        /* Test case to make sure, when reserved > pending, preemption will not
        happen if there's only one demanding queue.
         */
        int[][] qData = new int[][]{ // /   A   B   C
        new int[]{ 100, 40, 40, 20 }// abs
        // abs
        // abs
        , new int[]{ 100, 100, 100, 100 }// maxCap
        // maxCap
        // maxCap
        , new int[]{ 100, 70, 0, 0 }// used
        // used
        // used
        , new int[]{ 10, 30, 0, 0 }// pending
        // pending
        // pending
        , new int[]{ 0, 50, 0, 0 }// reserved
        // reserved
        // reserved
        , new int[]{ 1, 1, 0, 0 }// apps
        // apps
        // apps
        , new int[]{ -1, 1, 1, 1 }// req granularity
        // req granularity
        // req granularity
        , new int[]{ 3, 0, 0, 0 }// subqueues
        // subqueues
        // subqueues
         };
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(qData);
        policy.editSchedule();
        // No preemption happens
        Mockito.verify(mDisp, Mockito.never()).handle(ArgumentMatchers.argThat(new TestProportionalCapacityPreemptionPolicy.IsPreemptionRequestFor(appA)));
    }

    @Test
    public void testRefreshPreemptionProperties() throws Exception {
        ProportionalCapacityPreemptionPolicy policy = buildPolicy(TestProportionalCapacityPreemptionPolicy.Q_DATA_FOR_IGNORE);
        Assert.assertEquals(DEFAULT_PREEMPTION_MONITORING_INTERVAL, policy.getMonitoringInterval());
        Assert.assertEquals(DEFAULT_PREEMPTION_OBSERVE_ONLY, policy.isObserveOnly());
        CapacitySchedulerConfiguration newConf = new CapacitySchedulerConfiguration(conf);
        long newMonitoringInterval = 5000;
        boolean newObserveOnly = true;
        newConf.setLong(PREEMPTION_MONITORING_INTERVAL, newMonitoringInterval);
        newConf.setBoolean(PREEMPTION_OBSERVE_ONLY, newObserveOnly);
        Mockito.when(mCS.getConfiguration()).thenReturn(newConf);
        policy.editSchedule();
        Assert.assertEquals(newMonitoringInterval, policy.getMonitoringInterval());
        Assert.assertEquals(newObserveOnly, policy.isObserveOnly());
    }

    static class IsPreemptionRequestFor implements ArgumentMatcher<ContainerPreemptEvent> {
        private final ApplicationAttemptId appAttId;

        private final org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEventType type;

        IsPreemptionRequestFor(ApplicationAttemptId appAttId) {
            this(appAttId, SchedulerEventType.MARK_CONTAINER_FOR_PREEMPTION);
        }

        IsPreemptionRequestFor(ApplicationAttemptId appAttId, org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEventType type) {
            this.appAttId = appAttId;
            this.type = type;
        }

        @Override
        public boolean matches(ContainerPreemptEvent evt) {
            return (appAttId.equals(evt.getAppId())) && (type.equals(evt.getType()));
        }

        @Override
        public String toString() {
            return appAttId.toString();
        }
    }
}

