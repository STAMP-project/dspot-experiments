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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.clusterframework.types.SlotProfile;
import org.apache.flink.runtime.instance.Instance;
import org.apache.flink.runtime.jobmaster.LogicalSlot;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for scheduling individual tasks.
 */
public class SchedulerIsolatedTasksTest extends SchedulerTestBase {
    @Test
    public void testScheduleImmediately() throws Exception {
        Assert.assertEquals(0, testingSlotProvider.getNumberOfAvailableSlots());
        testingSlotProvider.addTaskManager(2);
        testingSlotProvider.addTaskManager(1);
        testingSlotProvider.addTaskManager(2);
        Assert.assertEquals(5, testingSlotProvider.getNumberOfAvailableSlots());
        // schedule something into all slots
        LogicalSlot s1 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s2 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s3 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s4 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s5 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        // the slots should all be different
        Assert.assertTrue(SchedulerTestUtils.areAllDistinct(s1, s2, s3, s4, s5));
        try {
            testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
            Assert.fail("Scheduler accepted scheduling request without available resource.");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof NoResourceAvailableException));
        }
        // release some slots again
        s3.releaseSlot();
        s4.releaseSlot();
        Assert.assertEquals(2, testingSlotProvider.getNumberOfAvailableSlots());
        // now we can schedule some more slots
        LogicalSlot s6 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        LogicalSlot s7 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        Assert.assertTrue(SchedulerTestUtils.areAllDistinct(s1, s2, s3, s4, s5, s6, s7));
        // release all
        s1.releaseSlot();
        s2.releaseSlot();
        s5.releaseSlot();
        s6.releaseSlot();
        s7.releaseSlot();
        Assert.assertEquals(5, testingSlotProvider.getNumberOfAvailableSlots());
        // check that slots that are released twice (accidentally) do not mess things up
        s1.releaseSlot();
        s2.releaseSlot();
        s5.releaseSlot();
        s6.releaseSlot();
        s7.releaseSlot();
        Assert.assertEquals(5, testingSlotProvider.getNumberOfAvailableSlots());
    }

    @Test
    public void testScheduleQueueing() throws Exception {
        final int NUM_INSTANCES = 50;
        final int NUM_SLOTS_PER_INSTANCE = 3;
        final int NUM_TASKS_TO_SCHEDULE = 2000;
        for (int i = 0; i < NUM_INSTANCES; i++) {
            testingSlotProvider.addTaskManager((((int) ((Math.random()) * NUM_SLOTS_PER_INSTANCE)) + 1));
        }
        final int totalSlots = testingSlotProvider.getNumberOfAvailableSlots();
        // all slots we ever got.
        List<CompletableFuture<LogicalSlot>> allAllocatedSlots = new ArrayList<>();
        // slots that need to be released
        final Set<LogicalSlot> toRelease = new HashSet<>();
        // flag to track errors in the concurrent thread
        final AtomicBoolean errored = new AtomicBoolean(false);
        for (int i = 0; i < NUM_TASKS_TO_SCHEDULE; i++) {
            CompletableFuture<LogicalSlot> future = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), true, SlotProfile.noRequirements(), TestingUtils.infiniteTime());
            future.thenAcceptAsync((LogicalSlot slot) -> {
                synchronized(toRelease) {
                    toRelease.add(slot);
                    toRelease.notifyAll();
                }
            }, TestingUtils.defaultExecutionContext());
            allAllocatedSlots.add(future);
        }
        try {
            int recycled = 0;
            while (recycled < NUM_TASKS_TO_SCHEDULE) {
                synchronized(toRelease) {
                    while (toRelease.isEmpty()) {
                        toRelease.wait();
                    } 
                    Iterator<LogicalSlot> iter = toRelease.iterator();
                    LogicalSlot next = iter.next();
                    iter.remove();
                    next.releaseSlot();
                    recycled++;
                }
            } 
        } catch (Throwable t) {
            errored.set(true);
        }
        Assert.assertFalse("The slot releasing thread caused an error.", errored.get());
        List<LogicalSlot> slotsAfter = new ArrayList<>();
        for (CompletableFuture<LogicalSlot> future : allAllocatedSlots) {
            slotsAfter.add(future.get());
        }
        // the slots should all be different
        Assert.assertTrue(SchedulerTestUtils.areAllDistinct(slotsAfter.toArray()));
        Assert.assertEquals("All slots should be available.", totalSlots, testingSlotProvider.getNumberOfAvailableSlots());
    }

    @Test
    public void testScheduleWithDyingInstances() throws Exception {
        final TaskManagerLocation taskManagerLocation1 = testingSlotProvider.addTaskManager(2);
        final TaskManagerLocation taskManagerLocation2 = testingSlotProvider.addTaskManager(2);
        final TaskManagerLocation taskManagerLocation3 = testingSlotProvider.addTaskManager(1);
        List<LogicalSlot> slots = new ArrayList<>();
        slots.add(testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get());
        slots.add(testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get());
        slots.add(testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get());
        slots.add(testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get());
        slots.add(testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get());
        testingSlotProvider.releaseTaskManager(taskManagerLocation2.getResourceID());
        for (LogicalSlot slot : slots) {
            if (slot.getTaskManagerLocation().getResourceID().equals(taskManagerLocation2.getResourceID())) {
                Assert.assertFalse(slot.isAlive());
            } else {
                Assert.assertTrue(slot.isAlive());
            }
            slot.releaseSlot();
        }
        Assert.assertEquals(3, testingSlotProvider.getNumberOfAvailableSlots());
        testingSlotProvider.releaseTaskManager(taskManagerLocation1.getResourceID());
        testingSlotProvider.releaseTaskManager(taskManagerLocation3.getResourceID());
        // cannot get another slot, since all instances are dead
        try {
            testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getDummyTask()), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
            Assert.fail("Scheduler served a slot from a dead instance");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof NoResourceAvailableException));
        } catch (Exception e) {
            Assert.fail("Wrong exception type.");
        }
        // now the latest, the scheduler should have noticed (through the lazy mechanisms)
        // that all instances have vanished
        Assert.assertEquals(0, testingSlotProvider.getNumberOfAvailableSlots());
    }

    @Test
    public void testSchedulingLocation() throws Exception {
        final TaskManagerLocation taskManagerLocation1 = testingSlotProvider.addTaskManager(2);
        final TaskManagerLocation taskManagerLocation2 = testingSlotProvider.addTaskManager(2);
        final TaskManagerLocation taskManagerLocation3 = testingSlotProvider.addTaskManager(2);
        // schedule something on an arbitrary instance
        LogicalSlot s1 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(new Instance[0])), false, SlotProfile.noRequirements(), TestingUtils.infiniteTime()).get();
        // figure out how we use the location hints
        ResourceID firstResourceId = s1.getTaskManagerLocation().getResourceID();
        List<TaskManagerLocation> taskManagerLocations = Arrays.asList(taskManagerLocation1, taskManagerLocation2, taskManagerLocation3);
        int index = 0;
        for (; index < (taskManagerLocations.size()); index++) {
            if (Objects.equals(taskManagerLocations.get(index).getResourceID(), firstResourceId)) {
                break;
            }
        }
        TaskManagerLocation first = taskManagerLocations.get(index);
        TaskManagerLocation second = taskManagerLocations.get(((index + 1) % (taskManagerLocations.size())));
        TaskManagerLocation third = taskManagerLocations.get(((index + 2) % (taskManagerLocations.size())));
        // something that needs to go to the first instance again
        LogicalSlot s2 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(s1.getTaskManagerLocation())), false, SchedulerIsolatedTasksTest.slotProfileForLocation(s1.getTaskManagerLocation()), TestingUtils.infiniteTime()).get();
        Assert.assertEquals(first.getResourceID(), s2.getTaskManagerLocation().getResourceID());
        // first or second --> second, because first is full
        LogicalSlot s3 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(first, second)), false, SchedulerIsolatedTasksTest.slotProfileForLocation(first, second), TestingUtils.infiniteTime()).get();
        Assert.assertEquals(second.getResourceID(), s3.getTaskManagerLocation().getResourceID());
        // first or third --> third (because first is full)
        LogicalSlot s4 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(first, third)), false, SchedulerIsolatedTasksTest.slotProfileForLocation(first, third), TestingUtils.infiniteTime()).get();
        LogicalSlot s5 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(first, third)), false, SchedulerIsolatedTasksTest.slotProfileForLocation(first, third), TestingUtils.infiniteTime()).get();
        Assert.assertEquals(third.getResourceID(), s4.getTaskManagerLocation().getResourceID());
        Assert.assertEquals(third.getResourceID(), s5.getTaskManagerLocation().getResourceID());
        // first or third --> second, because all others are full
        LogicalSlot s6 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(first, third)), false, SchedulerIsolatedTasksTest.slotProfileForLocation(first, third), TestingUtils.infiniteTime()).get();
        Assert.assertEquals(second.getResourceID(), s6.getTaskManagerLocation().getResourceID());
        // release something on the first and second instance
        s2.releaseSlot();
        s6.releaseSlot();
        LogicalSlot s7 = testingSlotProvider.allocateSlot(new ScheduledUnit(SchedulerTestUtils.getTestVertex(first, third)), false, SchedulerIsolatedTasksTest.slotProfileForLocation(first, third), TestingUtils.infiniteTime()).get();
        Assert.assertEquals(first.getResourceID(), s7.getTaskManagerLocation().getResourceID());
        Assert.assertEquals(1, testingSlotProvider.getNumberOfUnconstrainedAssignments());
        Assert.assertTrue(((1 == (testingSlotProvider.getNumberOfNonLocalizedAssignments())) || (1 == (testingSlotProvider.getNumberOfHostLocalizedAssignments()))));
        Assert.assertEquals(5, testingSlotProvider.getNumberOfLocalizedAssignments());
    }
}

