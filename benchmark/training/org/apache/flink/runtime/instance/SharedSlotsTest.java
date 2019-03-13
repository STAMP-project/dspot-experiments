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
package org.apache.flink.runtime.instance;


import Locality.LOCAL;
import Locality.NON_LOCAL;
import Locality.UNCONSTRAINED;
import java.util.Collections;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobmanager.scheduler.CoLocationConstraint;
import org.apache.flink.runtime.jobmanager.scheduler.CoLocationGroup;
import org.apache.flink.runtime.jobmanager.scheduler.SchedulerTestUtils;
import org.apache.flink.runtime.jobmanager.scheduler.SlotSharingGroup;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.apache.flink.util.AbstractID;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the allocation, properties, and release of shared slots.
 */
public class SharedSlotsTest extends TestLogger {
    private static final Iterable<TaskManagerLocation> NO_LOCATION = Collections.emptySet();

    @Test
    public void allocateAndReleaseEmptySlot() {
        try {
            JobVertexID vertexId = new JobVertexID();
            SlotSharingGroup sharingGroup = new SlotSharingGroup(vertexId);
            SlotSharingGroupAssignment assignment = sharingGroup.getTaskAssignment();
            Assert.assertEquals(0, assignment.getNumberOfSlots());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vertexId));
            Instance instance = SchedulerTestUtils.getRandomInstance(2);
            Assert.assertEquals(2, instance.getTotalNumberOfSlots());
            Assert.assertEquals(0, instance.getNumberOfAllocatedSlots());
            Assert.assertEquals(2, instance.getNumberOfAvailableSlots());
            // allocate a shared slot
            SharedSlot slot = instance.allocateSharedSlot(assignment);
            Assert.assertEquals(2, instance.getTotalNumberOfSlots());
            Assert.assertEquals(1, instance.getNumberOfAllocatedSlots());
            Assert.assertEquals(1, instance.getNumberOfAvailableSlots());
            // check that the new slot is fresh
            Assert.assertTrue(slot.isAlive());
            Assert.assertFalse(slot.isCanceled());
            Assert.assertFalse(slot.isReleased());
            Assert.assertEquals(0, slot.getNumberLeaves());
            Assert.assertFalse(slot.hasChildren());
            Assert.assertTrue(slot.isRootAndEmpty());
            Assert.assertNotNull(slot.toString());
            Assert.assertTrue(slot.getSubSlots().isEmpty());
            Assert.assertEquals(0, slot.getSlotNumber());
            Assert.assertEquals(0, slot.getRootSlotNumber());
            // release the slot immediately.
            slot.releaseSlot();
            Assert.assertTrue(slot.isCanceled());
            Assert.assertTrue(slot.isReleased());
            // the slot sharing group and instance should not
            Assert.assertEquals(2, instance.getTotalNumberOfSlots());
            Assert.assertEquals(0, instance.getNumberOfAllocatedSlots());
            Assert.assertEquals(2, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(0, assignment.getNumberOfSlots());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vertexId));
            // we should not be able to allocate any children from this released slot
            Assert.assertNull(slot.allocateSharedSlot(new AbstractID()));
            Assert.assertNull(slot.allocateSubSlot(new AbstractID()));
            // we cannot add this slot to the assignment group
            Assert.assertNull(assignment.addSharedSlotAndAllocateSubSlot(slot, NON_LOCAL, vertexId));
            Assert.assertEquals(0, assignment.getNumberOfSlots());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void allocateSimpleSlotsAndReleaseFromRoot() {
        try {
            JobVertexID vid1 = new JobVertexID();
            JobVertexID vid2 = new JobVertexID();
            JobVertexID vid3 = new JobVertexID();
            JobVertexID vid4 = new JobVertexID();
            SlotSharingGroup sharingGroup = new SlotSharingGroup(vid1, vid2, vid3, vid4);
            SlotSharingGroupAssignment assignment = sharingGroup.getTaskAssignment();
            Instance instance = SchedulerTestUtils.getRandomInstance(1);
            // allocate a shared slot
            SharedSlot sharedSlot = instance.allocateSharedSlot(assignment);
            // allocate a series of sub slots
            SimpleSlot sub1 = assignment.addSharedSlotAndAllocateSubSlot(sharedSlot, LOCAL, vid1);
            Assert.assertNotNull(sub1);
            Assert.assertNull(sub1.getPayload());
            Assert.assertEquals(LOCAL, sub1.getLocality());
            Assert.assertEquals(1, sub1.getNumberLeaves());
            Assert.assertEquals(vid1, sub1.getGroupID());
            Assert.assertEquals(instance.getTaskManagerID(), sub1.getTaskManagerID());
            Assert.assertEquals(sharedSlot, sub1.getParent());
            Assert.assertEquals(sharedSlot, sub1.getRoot());
            Assert.assertEquals(0, sub1.getRootSlotNumber());
            Assert.assertEquals(0, sub1.getSlotNumber());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid4));
            SimpleSlot sub2 = assignment.getSlotForTask(vid2, SharedSlotsTest.NO_LOCATION);
            Assert.assertNotNull(sub2);
            Assert.assertNull(sub2.getPayload());
            Assert.assertEquals(UNCONSTRAINED, sub2.getLocality());
            Assert.assertEquals(1, sub2.getNumberLeaves());
            Assert.assertEquals(vid2, sub2.getGroupID());
            Assert.assertEquals(instance.getTaskManagerID(), sub2.getTaskManagerID());
            Assert.assertEquals(sharedSlot, sub2.getParent());
            Assert.assertEquals(sharedSlot, sub2.getRoot());
            Assert.assertEquals(0, sub2.getRootSlotNumber());
            Assert.assertEquals(1, sub2.getSlotNumber());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid4));
            SimpleSlot sub3 = assignment.getSlotForTask(vid3, Collections.singleton(instance.getTaskManagerLocation()));
            Assert.assertNotNull(sub3);
            Assert.assertNull(sub3.getPayload());
            Assert.assertEquals(LOCAL, sub3.getLocality());
            Assert.assertEquals(1, sub3.getNumberLeaves());
            Assert.assertEquals(vid3, sub3.getGroupID());
            Assert.assertEquals(instance.getTaskManagerID(), sub3.getTaskManagerID());
            Assert.assertEquals(sharedSlot, sub3.getParent());
            Assert.assertEquals(sharedSlot, sub3.getRoot());
            Assert.assertEquals(0, sub3.getRootSlotNumber());
            Assert.assertEquals(2, sub3.getSlotNumber());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid4));
            SimpleSlot sub4 = assignment.getSlotForTask(vid4, Collections.singleton(SchedulerTestUtils.getRandomInstance(1).getTaskManagerLocation()));
            Assert.assertNotNull(sub4);
            Assert.assertNull(sub4.getPayload());
            Assert.assertEquals(NON_LOCAL, sub4.getLocality());
            Assert.assertEquals(1, sub4.getNumberLeaves());
            Assert.assertEquals(vid4, sub4.getGroupID());
            Assert.assertEquals(instance.getTaskManagerID(), sub4.getTaskManagerID());
            Assert.assertEquals(sharedSlot, sub4.getParent());
            Assert.assertEquals(sharedSlot, sub4.getRoot());
            Assert.assertEquals(0, sub4.getRootSlotNumber());
            Assert.assertEquals(3, sub4.getSlotNumber());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid4));
            // release from the root.
            sharedSlot.releaseSlot();
            Assert.assertTrue(sharedSlot.isReleased());
            Assert.assertTrue(sub1.isReleased());
            Assert.assertTrue(sub2.isReleased());
            Assert.assertTrue(sub3.isReleased());
            Assert.assertTrue(sub4.isReleased());
            Assert.assertEquals(0, sharedSlot.getNumberLeaves());
            Assert.assertFalse(sharedSlot.hasChildren());
            Assert.assertEquals(1, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(0, assignment.getNumberOfSlots());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid4));
            Assert.assertNull(sharedSlot.allocateSharedSlot(new AbstractID()));
            Assert.assertNull(sharedSlot.allocateSubSlot(new AbstractID()));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void allocateSimpleSlotsAndReleaseFromLeaves() {
        try {
            JobVertexID vid1 = new JobVertexID();
            JobVertexID vid2 = new JobVertexID();
            JobVertexID vid3 = new JobVertexID();
            SlotSharingGroup sharingGroup = new SlotSharingGroup(vid1, vid2, vid3);
            SlotSharingGroupAssignment assignment = sharingGroup.getTaskAssignment();
            Instance instance = SchedulerTestUtils.getRandomInstance(1);
            // allocate a shared slot
            SharedSlot sharedSlot = instance.allocateSharedSlot(assignment);
            // allocate a series of sub slots
            SimpleSlot sub1 = assignment.addSharedSlotAndAllocateSubSlot(sharedSlot, UNCONSTRAINED, vid1);
            SimpleSlot sub2 = assignment.getSlotForTask(vid2, SharedSlotsTest.NO_LOCATION);
            SimpleSlot sub3 = assignment.getSlotForTask(vid3, SharedSlotsTest.NO_LOCATION);
            Assert.assertNotNull(sub1);
            Assert.assertNotNull(sub2);
            Assert.assertNotNull(sub3);
            Assert.assertEquals(3, sharedSlot.getNumberLeaves());
            Assert.assertEquals(1, assignment.getNumberOfSlots());
            // release from the leaves.
            sub2.releaseSlot();
            Assert.assertTrue(sharedSlot.isAlive());
            Assert.assertTrue(sub1.isAlive());
            Assert.assertTrue(sub2.isReleased());
            Assert.assertTrue(sub3.isAlive());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(1, assignment.getNumberOfSlots());
            Assert.assertEquals(2, sharedSlot.getNumberLeaves());
            sub1.releaseSlot();
            Assert.assertTrue(sharedSlot.isAlive());
            Assert.assertTrue(sub1.isReleased());
            Assert.assertTrue(sub2.isReleased());
            Assert.assertTrue(sub3.isAlive());
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(1, assignment.getNumberOfSlots());
            Assert.assertEquals(1, sharedSlot.getNumberLeaves());
            sub3.releaseSlot();
            Assert.assertTrue(sharedSlot.isReleased());
            Assert.assertTrue(sub1.isReleased());
            Assert.assertTrue(sub2.isReleased());
            Assert.assertTrue(sub3.isReleased());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(0, assignment.getNumberOfSlots());
            Assert.assertEquals(1, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(0, assignment.getNumberOfSlots());
            Assert.assertNull(sharedSlot.allocateSharedSlot(new AbstractID()));
            Assert.assertNull(sharedSlot.allocateSubSlot(new AbstractID()));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void allocateAndReleaseInMixedOrder() {
        try {
            JobVertexID vid1 = new JobVertexID();
            JobVertexID vid2 = new JobVertexID();
            JobVertexID vid3 = new JobVertexID();
            SlotSharingGroup sharingGroup = new SlotSharingGroup(vid1, vid2, vid3);
            SlotSharingGroupAssignment assignment = sharingGroup.getTaskAssignment();
            Instance instance = SchedulerTestUtils.getRandomInstance(1);
            // allocate a shared slot
            SharedSlot sharedSlot = instance.allocateSharedSlot(assignment);
            // allocate a series of sub slots
            SimpleSlot sub1 = assignment.addSharedSlotAndAllocateSubSlot(sharedSlot, UNCONSTRAINED, vid1);
            SimpleSlot sub2 = assignment.getSlotForTask(vid2, SharedSlotsTest.NO_LOCATION);
            Assert.assertNotNull(sub1);
            Assert.assertNotNull(sub2);
            Assert.assertEquals(2, sharedSlot.getNumberLeaves());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(1, assignment.getNumberOfSlots());
            sub2.releaseSlot();
            Assert.assertEquals(1, sharedSlot.getNumberLeaves());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(1, assignment.getNumberOfSlots());
            SimpleSlot sub3 = assignment.getSlotForTask(vid3, SharedSlotsTest.NO_LOCATION);
            Assert.assertNotNull(sub3);
            Assert.assertEquals(2, sharedSlot.getNumberLeaves());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(1, assignment.getNumberOfSlots());
            sub3.releaseSlot();
            sub1.releaseSlot();
            Assert.assertTrue(sharedSlot.isReleased());
            Assert.assertEquals(0, sharedSlot.getNumberLeaves());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid1));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid2));
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(vid3));
            Assert.assertEquals(0, assignment.getNumberOfSlots());
            Assert.assertEquals(1, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(0, assignment.getNumberOfSlots());
            Assert.assertNull(sharedSlot.allocateSharedSlot(new AbstractID()));
            Assert.assertNull(sharedSlot.allocateSubSlot(new AbstractID()));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * We allocate and release the structure below, starting by allocating a simple slot in the
     * shared slot and finishing by releasing a simple slot.
     *
     * <pre>
     *     Shared(0)(root)
     *        |
     *        +-- Simple(2)(sink)
     *        |
     *        +-- Shared(1)(co-location-group)
     *        |      |
     *        |      +-- Simple(0)(tail)
     *        |      +-- Simple(1)(head)
     *        |
     *        +-- Simple(0)(source)
     * </pre>
     */
    @Test
    public void testAllocateAndReleaseTwoLevels() {
        try {
            JobVertexID sourceId = new JobVertexID();
            JobVertexID headId = new JobVertexID();
            JobVertexID tailId = new JobVertexID();
            JobVertexID sinkId = new JobVertexID();
            JobVertex headVertex = new JobVertex("head", headId);
            JobVertex tailVertex = new JobVertex("tail", tailId);
            SlotSharingGroup sharingGroup = new SlotSharingGroup(sourceId, headId, tailId, sinkId);
            SlotSharingGroupAssignment assignment = sharingGroup.getTaskAssignment();
            Assert.assertEquals(0, assignment.getNumberOfSlots());
            CoLocationGroup coLocationGroup = new CoLocationGroup(headVertex, tailVertex);
            CoLocationConstraint constraint = coLocationGroup.getLocationConstraint(0);
            Assert.assertFalse(constraint.isAssigned());
            Instance instance = SchedulerTestUtils.getRandomInstance(1);
            // allocate a shared slot
            SharedSlot sharedSlot = instance.allocateSharedSlot(assignment);
            // get the first simple slot
            SimpleSlot sourceSlot = assignment.addSharedSlotAndAllocateSubSlot(sharedSlot, LOCAL, sourceId);
            Assert.assertEquals(1, sharedSlot.getNumberLeaves());
            // get the first slot in the nested shared slot from the co-location constraint
            SimpleSlot headSlot = assignment.getSlotForTask(constraint, Collections.<TaskManagerLocation>emptySet());
            Assert.assertEquals(2, sharedSlot.getNumberLeaves());
            Assert.assertNotNull(constraint.getSharedSlot());
            Assert.assertTrue(constraint.getSharedSlot().isAlive());
            Assert.assertFalse(constraint.isAssigned());
            // we do not immediately lock the location
            headSlot.releaseSlot();
            Assert.assertEquals(1, sharedSlot.getNumberLeaves());
            Assert.assertNotNull(constraint.getSharedSlot());
            Assert.assertTrue(constraint.getSharedSlot().isReleased());
            Assert.assertFalse(constraint.isAssigned());
            // re-allocate the head slot
            headSlot = assignment.getSlotForTask(constraint, Collections.<TaskManagerLocation>emptySet());
            constraint.lockLocation();
            Assert.assertNotNull(constraint.getSharedSlot());
            Assert.assertTrue(constraint.isAssigned());
            Assert.assertTrue(constraint.isAssignedAndAlive());
            Assert.assertEquals(instance.getTaskManagerLocation(), constraint.getLocation());
            SimpleSlot tailSlot = assignment.getSlotForTask(constraint, Collections.<TaskManagerLocation>emptySet());
            Assert.assertEquals(constraint.getSharedSlot(), headSlot.getParent());
            Assert.assertEquals(constraint.getSharedSlot(), tailSlot.getParent());
            SimpleSlot sinkSlot = assignment.getSlotForTask(sinkId, Collections.<TaskManagerLocation>emptySet());
            Assert.assertEquals(4, sharedSlot.getNumberLeaves());
            // we release our co-location constraint tasks
            headSlot.releaseSlot();
            tailSlot.releaseSlot();
            Assert.assertEquals(2, sharedSlot.getNumberLeaves());
            Assert.assertTrue(headSlot.isReleased());
            Assert.assertTrue(tailSlot.isReleased());
            Assert.assertTrue(constraint.isAssigned());
            Assert.assertFalse(constraint.isAssignedAndAlive());
            Assert.assertEquals(instance.getTaskManagerLocation(), constraint.getLocation());
            // we should have resources again for the co-location constraint
            Assert.assertEquals(1, assignment.getNumberOfAvailableSlotsForGroup(constraint.getGroupId()));
            // re-allocate head and tail from the constraint
            headSlot = assignment.getSlotForTask(constraint, SharedSlotsTest.NO_LOCATION);
            tailSlot = assignment.getSlotForTask(constraint, SharedSlotsTest.NO_LOCATION);
            Assert.assertEquals(4, sharedSlot.getNumberLeaves());
            Assert.assertEquals(0, assignment.getNumberOfAvailableSlotsForGroup(constraint.getGroupId()));
            // verify some basic properties of the slots
            Assert.assertEquals(instance.getTaskManagerID(), sourceSlot.getTaskManagerID());
            Assert.assertEquals(instance.getTaskManagerID(), headSlot.getTaskManagerID());
            Assert.assertEquals(instance.getTaskManagerID(), tailSlot.getTaskManagerID());
            Assert.assertEquals(instance.getTaskManagerID(), sinkSlot.getTaskManagerID());
            Assert.assertEquals(sourceId, sourceSlot.getGroupID());
            Assert.assertEquals(sinkId, sinkSlot.getGroupID());
            Assert.assertNull(headSlot.getGroupID());
            Assert.assertNull(tailSlot.getGroupID());
            Assert.assertEquals(constraint.getGroupId(), constraint.getSharedSlot().getGroupID());
            // release all
            sourceSlot.releaseSlot();
            headSlot.releaseSlot();
            tailSlot.releaseSlot();
            sinkSlot.releaseSlot();
            Assert.assertTrue(sharedSlot.isReleased());
            Assert.assertTrue(sourceSlot.isReleased());
            Assert.assertTrue(headSlot.isReleased());
            Assert.assertTrue(tailSlot.isReleased());
            Assert.assertTrue(sinkSlot.isReleased());
            Assert.assertTrue(constraint.getSharedSlot().isReleased());
            Assert.assertTrue(constraint.isAssigned());
            Assert.assertFalse(constraint.isAssignedAndAlive());
            Assert.assertEquals(1, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(0, assignment.getNumberOfSlots());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * We allocate and the structure below and release it from the root.
     *
     * <pre>
     *     Shared(0)(root)
     *        |
     *        +-- Simple(2)(sink)
     *        |
     *        +-- Shared(1)(co-location-group)
     *        |      |
     *        |      +-- Simple(0)(tail)
     *        |      +-- Simple(1)(head)
     *        |
     *        +-- Simple(0)(source)
     * </pre>
     */
    @Test
    public void testReleaseTwoLevelsFromRoot() {
        try {
            JobVertexID sourceId = new JobVertexID();
            JobVertexID headId = new JobVertexID();
            JobVertexID tailId = new JobVertexID();
            JobVertexID sinkId = new JobVertexID();
            JobVertex headVertex = new JobVertex("head", headId);
            JobVertex tailVertex = new JobVertex("tail", tailId);
            SlotSharingGroup sharingGroup = new SlotSharingGroup(sourceId, headId, tailId, sinkId);
            SlotSharingGroupAssignment assignment = sharingGroup.getTaskAssignment();
            Assert.assertEquals(0, assignment.getNumberOfSlots());
            CoLocationGroup coLocationGroup = new CoLocationGroup(headVertex, tailVertex);
            CoLocationConstraint constraint = coLocationGroup.getLocationConstraint(0);
            Assert.assertFalse(constraint.isAssigned());
            Instance instance = SchedulerTestUtils.getRandomInstance(1);
            // allocate a shared slot
            SharedSlot sharedSlot = instance.allocateSharedSlot(assignment);
            // get the first simple slot
            SimpleSlot sourceSlot = assignment.addSharedSlotAndAllocateSubSlot(sharedSlot, LOCAL, sourceId);
            SimpleSlot headSlot = assignment.getSlotForTask(constraint, SharedSlotsTest.NO_LOCATION);
            constraint.lockLocation();
            SimpleSlot tailSlot = assignment.getSlotForTask(constraint, SharedSlotsTest.NO_LOCATION);
            SimpleSlot sinkSlot = assignment.getSlotForTask(sinkId, SharedSlotsTest.NO_LOCATION);
            Assert.assertEquals(4, sharedSlot.getNumberLeaves());
            // release all
            sourceSlot.releaseSlot();
            headSlot.releaseSlot();
            tailSlot.releaseSlot();
            sinkSlot.releaseSlot();
            Assert.assertTrue(sharedSlot.isReleased());
            Assert.assertTrue(sourceSlot.isReleased());
            Assert.assertTrue(headSlot.isReleased());
            Assert.assertTrue(tailSlot.isReleased());
            Assert.assertTrue(sinkSlot.isReleased());
            Assert.assertTrue(constraint.getSharedSlot().isReleased());
            Assert.assertTrue(constraint.isAssigned());
            Assert.assertFalse(constraint.isAssignedAndAlive());
            Assert.assertEquals(1, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(0, instance.getNumberOfAllocatedSlots());
            Assert.assertEquals(0, assignment.getNumberOfSlots());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testImmediateReleaseOneLevel() {
        try {
            JobVertexID vid = new JobVertexID();
            SlotSharingGroup sharingGroup = new SlotSharingGroup(vid);
            SlotSharingGroupAssignment assignment = sharingGroup.getTaskAssignment();
            Instance instance = SchedulerTestUtils.getRandomInstance(1);
            SharedSlot sharedSlot = instance.allocateSharedSlot(assignment);
            SimpleSlot sub = assignment.addSharedSlotAndAllocateSubSlot(sharedSlot, UNCONSTRAINED, vid);
            sub.releaseSlot();
            Assert.assertTrue(sub.isReleased());
            Assert.assertTrue(sharedSlot.isReleased());
            Assert.assertEquals(1, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(0, instance.getNumberOfAllocatedSlots());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testImmediateReleaseTwoLevel() {
        try {
            JobVertexID vid = new JobVertexID();
            JobVertex vertex = new JobVertex("vertex", vid);
            SlotSharingGroup sharingGroup = new SlotSharingGroup(vid);
            SlotSharingGroupAssignment assignment = sharingGroup.getTaskAssignment();
            CoLocationGroup coLocationGroup = new CoLocationGroup(vertex);
            CoLocationConstraint constraint = coLocationGroup.getLocationConstraint(0);
            Instance instance = SchedulerTestUtils.getRandomInstance(1);
            SharedSlot sharedSlot = instance.allocateSharedSlot(assignment);
            SimpleSlot sub = assignment.addSharedSlotAndAllocateSubSlot(sharedSlot, UNCONSTRAINED, constraint);
            Assert.assertNull(sub.getGroupID());
            Assert.assertEquals(constraint.getSharedSlot(), sub.getParent());
            sub.releaseSlot();
            Assert.assertTrue(sub.isReleased());
            Assert.assertTrue(sharedSlot.isReleased());
            Assert.assertEquals(1, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(0, instance.getNumberOfAllocatedSlots());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

