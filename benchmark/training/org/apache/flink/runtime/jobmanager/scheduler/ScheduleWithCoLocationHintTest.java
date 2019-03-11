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
package org.apache.flink.runtime.jobmanager.scheduler;


import java.util.concurrent.ExecutionException;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.clusterframework.types.SlotProfile;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobmaster.LogicalSlot;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.junit.Assert;
import org.junit.Test;


public class ScheduleWithCoLocationHintTest extends SchedulerTestBase {
    @Test
    public void scheduleAllSharedAndCoLocated() throws Exception {
        JobVertexID jid1 = new JobVertexID();
        JobVertexID jid2 = new JobVertexID();
        testingSlotProvider.addTaskManager(2);
        testingSlotProvider.addTaskManager(2);
        testingSlotProvider.addTaskManager(2);
        Assert.assertEquals(6, testingSlotProvider.getNumberOfAvailableSlots());
        SlotSharingGroup sharingGroup = new SlotSharingGroup();
        CoLocationGroup ccg = new CoLocationGroup();
        CoLocationConstraint c1 = new CoLocationConstraint(ccg);
        CoLocationConstraint c2 = new CoLocationConstraint(ccg);
        CoLocationConstraint c3 = new CoLocationConstraint(ccg);
        CoLocationConstraint c4 = new CoLocationConstraint(ccg);
        CoLocationConstraint c5 = new CoLocationConstraint(ccg);
        CoLocationConstraint c6 = new CoLocationConstraint(ccg);
        // schedule 4 tasks from the first vertex group
        LogicalSlot s1 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 0, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c1), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s2 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 1, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c2), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s3 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 2, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c3), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s4 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 3, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c4), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s5 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 0, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c1), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s6 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 1, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c2), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s7 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 2, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c3), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s8 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 4, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c5), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s9 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 5, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c6), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s10 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 3, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c4), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s11 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 4, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c5), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s12 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 5, 6, sharingGroup), sharingGroup.getSlotSharingGroupId(), c6), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        Assert.assertNotNull(s1);
        Assert.assertNotNull(s2);
        Assert.assertNotNull(s3);
        Assert.assertNotNull(s4);
        Assert.assertNotNull(s5);
        Assert.assertNotNull(s6);
        Assert.assertNotNull(s7);
        Assert.assertNotNull(s8);
        Assert.assertNotNull(s9);
        Assert.assertNotNull(s10);
        Assert.assertNotNull(s11);
        Assert.assertNotNull(s12);
        // check that each slot got exactly two tasks
        Assert.assertEquals(s1.getTaskManagerLocation(), s5.getTaskManagerLocation());
        Assert.assertEquals(s2.getTaskManagerLocation(), s6.getTaskManagerLocation());
        Assert.assertEquals(s3.getTaskManagerLocation(), s7.getTaskManagerLocation());
        Assert.assertEquals(s4.getTaskManagerLocation(), s10.getTaskManagerLocation());
        Assert.assertEquals(s8.getTaskManagerLocation(), s11.getTaskManagerLocation());
        Assert.assertEquals(s9.getTaskManagerLocation(), s12.getTaskManagerLocation());
        Assert.assertEquals(c1.getLocation(), s1.getTaskManagerLocation());
        Assert.assertEquals(c2.getLocation(), s2.getTaskManagerLocation());
        Assert.assertEquals(c3.getLocation(), s3.getTaskManagerLocation());
        Assert.assertEquals(c4.getLocation(), s4.getTaskManagerLocation());
        Assert.assertEquals(c5.getLocation(), s8.getTaskManagerLocation());
        Assert.assertEquals(c6.getLocation(), s9.getTaskManagerLocation());
        // check the scheduler's bookkeeping
        Assert.assertEquals(0, testingSlotProvider.getNumberOfAvailableSlots());
        // the first assignments are unconstrained, co.-scheduling is constrained
        Assert.assertEquals(6, testingSlotProvider.getNumberOfLocalizedAssignments());
        Assert.assertEquals(0, testingSlotProvider.getNumberOfNonLocalizedAssignments());
        Assert.assertEquals(6, testingSlotProvider.getNumberOfUnconstrainedAssignments());
        // release some slots, be sure that new available ones come up
        s1.releaseSlot();
        s2.releaseSlot();
        s3.releaseSlot();
        s4.releaseSlot();
        s7.releaseSlot();
        s10.releaseSlot();
        s11.releaseSlot();
        s12.releaseSlot();
        Assert.assertTrue(((testingSlotProvider.getNumberOfAvailableSlots()) >= 1));
        LogicalSlot single = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(new JobVertexID(), 0, 1, null)), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        Assert.assertNotNull(single);
        s1.releaseSlot();
        s2.releaseSlot();
        s3.releaseSlot();
        s5.releaseSlot();
        s6.releaseSlot();
        s7.releaseSlot();
        s8.releaseSlot();
        s9.releaseSlot();
        s11.releaseSlot();
        s12.releaseSlot();
        Assert.assertEquals(5, testingSlotProvider.getNumberOfAvailableSlots());
        Assert.assertEquals(6, testingSlotProvider.getNumberOfLocalizedAssignments());
        Assert.assertEquals(0, testingSlotProvider.getNumberOfNonLocalizedAssignments());
        Assert.assertEquals(7, testingSlotProvider.getNumberOfUnconstrainedAssignments());
    }

    @Test
    public void scheduleWithIntermediateRelease() throws Exception {
        JobVertexID jid1 = new JobVertexID();
        JobVertexID jid2 = new JobVertexID();
        JobVertexID jid3 = new JobVertexID();
        JobVertexID jid4 = new JobVertexID();
        testingSlotProvider.addTaskManager(1);
        testingSlotProvider.addTaskManager(1);
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        SlotSharingGroup sharingGroup = new SlotSharingGroup();
        CoLocationConstraint c1 = new CoLocationConstraint(new CoLocationGroup());
        LogicalSlot s1 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 0, 1, sharingGroup), sharingGroup.getSlotSharingGroupId(), c1), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s2 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 0, 1, sharingGroup), sharingGroup.getSlotSharingGroupId(), c1), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot sSolo = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid4, 0, 1, null)), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        ResourceID taskManager = s1.getTaskManagerLocation().getResourceID();
        s1.releaseSlot();
        s2.releaseSlot();
        sSolo.releaseSlot();
        LogicalSlot sNew = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid3, 0, 1, sharingGroup), sharingGroup.getSlotSharingGroupId(), c1), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        Assert.assertEquals(taskManager, sNew.getTaskManagerLocation().getResourceID());
        Assert.assertEquals(2, testingSlotProvider.getNumberOfLocalizedAssignments());
        Assert.assertEquals(0, testingSlotProvider.getNumberOfNonLocalizedAssignments());
        Assert.assertEquals(2, testingSlotProvider.getNumberOfUnconstrainedAssignments());
    }

    @Test
    public void scheduleWithReleaseNoResource() throws Exception {
        JobVertexID jid1 = new JobVertexID();
        JobVertexID jid2 = new JobVertexID();
        JobVertexID jid3 = new JobVertexID();
        testingSlotProvider.addTaskManager(1);
        testingSlotProvider.addTaskManager(1);
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        SlotSharingGroup sharingGroup = new SlotSharingGroup();
        CoLocationConstraint c1 = new CoLocationConstraint(new CoLocationGroup());
        LogicalSlot s1 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 0, 1, sharingGroup), sharingGroup.getSlotSharingGroupId(), c1), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        s1.releaseSlot();
        testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 0, 1, null)), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 1, 2, null)), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        try {
            testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid3, 0, 1, sharingGroup), sharingGroup.getSlotSharingGroupId(), c1), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
            Assert.fail("Scheduled even though no resource was available.");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof NoResourceAvailableException));
        }
        Assert.assertEquals(0, testingSlotProvider.getNumberOfLocalizedAssignments());
        Assert.assertEquals(0, testingSlotProvider.getNumberOfNonLocalizedAssignments());
        Assert.assertEquals(3, testingSlotProvider.getNumberOfUnconstrainedAssignments());
    }

    @Test
    public void scheduleMixedCoLocationSlotSharing() throws Exception {
        JobVertexID jid1 = new JobVertexID();
        JobVertexID jid2 = new JobVertexID();
        JobVertexID jid3 = new JobVertexID();
        JobVertexID jid4 = new JobVertexID();
        testingSlotProvider.addTaskManager(1);
        testingSlotProvider.addTaskManager(1);
        testingSlotProvider.addTaskManager(1);
        testingSlotProvider.addTaskManager(1);
        Assert.assertEquals(4, testingSlotProvider.getNumberOfAvailableSlots());
        CoLocationGroup grp = new CoLocationGroup();
        CoLocationConstraint clc1 = new CoLocationConstraint(grp);
        CoLocationConstraint clc2 = new CoLocationConstraint(grp);
        CoLocationConstraint clc3 = new CoLocationConstraint(grp);
        CoLocationConstraint clc4 = new CoLocationConstraint(grp);
        SlotSharingGroup shareGroup = new SlotSharingGroup();
        // first wave
        testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 0, 4, shareGroup), shareGroup.getSlotSharingGroupId()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 2, 4, shareGroup), shareGroup.getSlotSharingGroupId()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 1, 4, shareGroup), shareGroup.getSlotSharingGroupId()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid1, 3, 4, shareGroup), shareGroup.getSlotSharingGroupId()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        // second wave
        LogicalSlot s21 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 0, 4, shareGroup), shareGroup.getSlotSharingGroupId(), clc1), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s22 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 2, 4, shareGroup), shareGroup.getSlotSharingGroupId(), clc2), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s23 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 1, 4, shareGroup), shareGroup.getSlotSharingGroupId(), clc3), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s24 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid2, 3, 4, shareGroup), shareGroup.getSlotSharingGroupId(), clc4), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        // third wave
        LogicalSlot s31 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid3, 1, 4, shareGroup), shareGroup.getSlotSharingGroupId(), clc2), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s32 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid3, 2, 4, shareGroup), shareGroup.getSlotSharingGroupId(), clc3), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s33 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid3, 3, 4, shareGroup), shareGroup.getSlotSharingGroupId(), clc4), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s34 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid3, 0, 4, shareGroup), shareGroup.getSlotSharingGroupId(), clc1), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid4, 0, 4, shareGroup), shareGroup.getSlotSharingGroupId()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid4, 1, 4, shareGroup), shareGroup.getSlotSharingGroupId()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid4, 2, 4, shareGroup), shareGroup.getSlotSharingGroupId()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(jid4, 3, 4, shareGroup), shareGroup.getSlotSharingGroupId()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        Assert.assertEquals(s21.getTaskManagerLocation(), s34.getTaskManagerLocation());
        Assert.assertEquals(s22.getTaskManagerLocation(), s31.getTaskManagerLocation());
        Assert.assertEquals(s23.getTaskManagerLocation(), s32.getTaskManagerLocation());
        Assert.assertEquals(s24.getTaskManagerLocation(), s33.getTaskManagerLocation());
        Assert.assertEquals(4, testingSlotProvider.getNumberOfLocalizedAssignments());
        Assert.assertEquals(0, testingSlotProvider.getNumberOfNonLocalizedAssignments());
        Assert.assertEquals(12, testingSlotProvider.getNumberOfUnconstrainedAssignments());
    }

    @Test
    public void testGetsNonLocalFromSharingGroupFirst() throws Exception {
        JobVertexID jid1 = new JobVertexID();
        JobVertexID jid2 = new JobVertexID();
        JobVertexID jid3 = new JobVertexID();
        TaskManagerLocation loc1 = testingSlotProvider.addTaskManager(1);
        TaskManagerLocation loc2 = testingSlotProvider.addTaskManager(1);
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        SlotSharingGroup sharingGroup = new SlotSharingGroup();
        CoLocationGroup ccg = new CoLocationGroup();
        CoLocationConstraint cc1 = new CoLocationConstraint(ccg);
        CoLocationConstraint cc2 = new CoLocationConstraint(ccg);
        // schedule something into the shared group so that both instances are in the sharing group
        LogicalSlot s1 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid1, 0, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId()), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        LogicalSlot s2 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid1, 1, 2, sharingGroup, loc2), sharingGroup.getSlotSharingGroupId()), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc2), TestingUtils.infiniteTime()).get();
        // schedule one locally to instance 1
        LogicalSlot s3 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid2, 0, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId(), cc1), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        // schedule with co location constraint (yet unassigned) and a preference for
        // instance 1, but it can only get instance 2
        LogicalSlot s4 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid2, 1, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId(), cc2), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        // schedule something into the assigned co-location constraints and check that they override the
        // other preferences
        LogicalSlot s5 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid3, 0, 2, sharingGroup, loc2), sharingGroup.getSlotSharingGroupId(), cc1), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc2), TestingUtils.infiniteTime()).get();
        LogicalSlot s6 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid3, 1, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId(), cc2), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        // check that each slot got three
        Assert.assertEquals(s1.getTaskManagerLocation(), s3.getTaskManagerLocation());
        Assert.assertEquals(s2.getTaskManagerLocation(), s4.getTaskManagerLocation());
        Assert.assertEquals(s1.getTaskManagerLocation(), s5.getTaskManagerLocation());
        Assert.assertEquals(s2.getTaskManagerLocation(), s6.getTaskManagerLocation());
        // check the scheduler's bookkeeping
        Assert.assertEquals(0, testingSlotProvider.getNumberOfAvailableSlots());
        Assert.assertEquals(5, testingSlotProvider.getNumberOfLocalizedAssignments());
        Assert.assertTrue(((1 == (testingSlotProvider.getNumberOfNonLocalizedAssignments())) || (1 == (testingSlotProvider.getNumberOfHostLocalizedAssignments()))));
        Assert.assertEquals(0, testingSlotProvider.getNumberOfUnconstrainedAssignments());
        // release some slots, be sure that new available ones come up
        s1.releaseSlot();
        s2.releaseSlot();
        s3.releaseSlot();
        s4.releaseSlot();
        s5.releaseSlot();
        s6.releaseSlot();
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
    }

    @Test
    public void testSlotReleasedInBetween() throws Exception {
        JobVertexID jid1 = new JobVertexID();
        JobVertexID jid2 = new JobVertexID();
        TaskManagerLocation loc1 = testingSlotProvider.addTaskManager(1);
        TaskManagerLocation loc2 = testingSlotProvider.addTaskManager(1);
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        SlotSharingGroup sharingGroup = new SlotSharingGroup();
        CoLocationGroup ccg = new CoLocationGroup();
        CoLocationConstraint cc1 = new CoLocationConstraint(ccg);
        CoLocationConstraint cc2 = new CoLocationConstraint(ccg);
        LogicalSlot s1 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid1, 0, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId(), cc1), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        LogicalSlot s2 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid1, 1, 2, sharingGroup, loc2), sharingGroup.getSlotSharingGroupId(), cc2), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc2), TestingUtils.infiniteTime()).get();
        s1.releaseSlot();
        s2.releaseSlot();
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        Assert.assertEquals(0, sharingGroup.getTaskAssignment().getNumberOfSlots());
        LogicalSlot s3 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid2, 0, 2, sharingGroup, loc2), sharingGroup.getSlotSharingGroupId(), cc1), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc2), TestingUtils.infiniteTime()).get();
        LogicalSlot s4 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid2, 1, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId(), cc2), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        // still preserves the previous instance mapping)
        Assert.assertEquals(loc1, s3.getTaskManagerLocation());
        Assert.assertEquals(loc2, s4.getTaskManagerLocation());
        s3.releaseSlot();
        s4.releaseSlot();
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        Assert.assertEquals(4, testingSlotProvider.getNumberOfLocalizedAssignments());
        Assert.assertEquals(0, testingSlotProvider.getNumberOfNonLocalizedAssignments());
        Assert.assertEquals(0, testingSlotProvider.getNumberOfUnconstrainedAssignments());
    }

    @Test
    public void testSlotReleasedInBetweenAndNoNewLocal() throws Exception {
        JobVertexID jid1 = new JobVertexID();
        JobVertexID jid2 = new JobVertexID();
        JobVertexID jidx = new JobVertexID();
        TaskManagerLocation loc1 = testingSlotProvider.addTaskManager(1);
        TaskManagerLocation loc2 = testingSlotProvider.addTaskManager(1);
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        SlotSharingGroup sharingGroup = new SlotSharingGroup();
        CoLocationGroup ccg = new CoLocationGroup();
        CoLocationConstraint cc1 = new CoLocationConstraint(ccg);
        CoLocationConstraint cc2 = new CoLocationConstraint(ccg);
        LogicalSlot s1 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid1, 0, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId(), cc1), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        LogicalSlot s2 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid1, 1, 2, sharingGroup, loc2), sharingGroup.getSlotSharingGroupId(), cc2), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc2), TestingUtils.infiniteTime()).get();
        s1.releaseSlot();
        s2.releaseSlot();
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        Assert.assertEquals(0, sharingGroup.getTaskAssignment().getNumberOfSlots());
        LogicalSlot sa = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jidx, 0, 2, null)), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot sb = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jidx, 1, 2, null)), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        try {
            testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid2, 0, 2, sharingGroup, loc2), sharingGroup.getSlotSharingGroupId(), cc1), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc2), TestingUtils.infiniteTime()).get();
            Assert.fail("should not be able to find a resource");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof NoResourceAvailableException));
        } catch (Exception e) {
            Assert.fail("wrong exception");
        }
        sa.releaseSlot();
        sb.releaseSlot();
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        Assert.assertEquals(2, testingSlotProvider.getNumberOfLocalizedAssignments());
        Assert.assertEquals(0, testingSlotProvider.getNumberOfNonLocalizedAssignments());
        Assert.assertEquals(2, testingSlotProvider.getNumberOfUnconstrainedAssignments());
    }

    @Test
    public void testScheduleOutOfOrder() throws Exception {
        JobVertexID jid1 = new JobVertexID();
        JobVertexID jid2 = new JobVertexID();
        TaskManagerLocation loc1 = testingSlotProvider.addTaskManager(1);
        testingSlotProvider.addTaskManager(1);
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        SlotSharingGroup sharingGroup = new SlotSharingGroup();
        CoLocationGroup ccg = new CoLocationGroup();
        CoLocationConstraint cc1 = new CoLocationConstraint(ccg);
        CoLocationConstraint cc2 = new CoLocationConstraint(ccg);
        // schedule something from the second job vertex id before the first is filled,
        // and give locality preferences that hint at using the same shared slot for both
        // co location constraints (which we seek to prevent)
        LogicalSlot s1 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid1, 0, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId(), cc1), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        LogicalSlot s2 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid2, 0, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId(), cc2), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        LogicalSlot s3 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid2, 1, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId(), cc1), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        LogicalSlot s4 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid1, 1, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId(), cc2), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        // check that each slot got three
        Assert.assertEquals(s1.getTaskManagerLocation(), s3.getTaskManagerLocation());
        Assert.assertEquals(s2.getTaskManagerLocation(), s4.getTaskManagerLocation());
        // check the testingSlotProvider's bookkeeping
        Assert.assertEquals(0, testingSlotProvider.getNumberOfAvailableSlots());
        Assert.assertEquals(3, testingSlotProvider.getNumberOfLocalizedAssignments());
        Assert.assertTrue(((1 == (testingSlotProvider.getNumberOfNonLocalizedAssignments())) || (1 == (testingSlotProvider.getNumberOfHostLocalizedAssignments()))));
        Assert.assertEquals(0, testingSlotProvider.getNumberOfUnconstrainedAssignments());
        // release some slots, be sure that new available ones come up
        s1.releaseSlot();
        s2.releaseSlot();
        s3.releaseSlot();
        s4.releaseSlot();
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        Assert.assertEquals(0, sharingGroup.getTaskAssignment().getNumberOfSlots());
        Assert.assertEquals(0, sharingGroup.getTaskAssignment().getNumberOfAvailableSlotsForGroup(jid1));
        Assert.assertEquals(0, sharingGroup.getTaskAssignment().getNumberOfAvailableSlotsForGroup(jid2));
    }

    @Test
    public void nonColocationFollowsCoLocation() throws Exception {
        JobVertexID jid1 = new JobVertexID();
        JobVertexID jid2 = new JobVertexID();
        TaskManagerLocation loc1 = testingSlotProvider.addTaskManager(1);
        TaskManagerLocation loc2 = testingSlotProvider.addTaskManager(1);
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        SlotSharingGroup sharingGroup = new SlotSharingGroup();
        CoLocationGroup ccg = new CoLocationGroup();
        CoLocationConstraint cc1 = new CoLocationConstraint(ccg);
        CoLocationConstraint cc2 = new CoLocationConstraint(ccg);
        LogicalSlot s1 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid1, 0, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId(), cc1), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        LogicalSlot s2 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid1, 1, 2, sharingGroup, loc2), sharingGroup.getSlotSharingGroupId(), cc2), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc2), TestingUtils.infiniteTime()).get();
        LogicalSlot s3 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid2, 0, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId()), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        LogicalSlot s4 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertexWithLocation(jid2, 1, 2, sharingGroup, loc1), sharingGroup.getSlotSharingGroupId()), false, ScheduleWithCoLocationHintTest.slotProfileForLocation(loc1), TestingUtils.infiniteTime()).get();
        // check that each slot got two
        Assert.assertEquals(s1.getTaskManagerLocation(), s3.getTaskManagerLocation());
        Assert.assertEquals(s2.getTaskManagerLocation(), s4.getTaskManagerLocation());
        s1.releaseSlot();
        s2.releaseSlot();
        s3.releaseSlot();
        s4.releaseSlot();
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        Assert.assertEquals(0, sharingGroup.getTaskAssignment().getNumberOfSlots());
        Assert.assertEquals(0, sharingGroup.getTaskAssignment().getNumberOfAvailableSlotsForGroup(jid1));
        Assert.assertEquals(0, sharingGroup.getTaskAssignment().getNumberOfAvailableSlotsForGroup(jid2));
    }
}

