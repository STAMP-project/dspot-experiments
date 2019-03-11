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
package org.apache.flink.runtime.jobmaster.slotpool;


import Locality.LOCAL;
import Locality.UNCONSTRAINED;
import LocationPreferenceSlotSelectionStrategy.INSTANCE;
import ResourceProfile.UNKNOWN;
import SlotSelectionStrategy.SlotInfoAndLocality;
import SlotSharingManager.MultiTaskSlot;
import SlotSharingManager.SingleTaskSlot;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import org.apache.flink.runtime.clusterframework.types.AllocationID;
import org.apache.flink.runtime.clusterframework.types.SlotProfile;
import org.apache.flink.runtime.executiongraph.utils.SimpleAckingTaskManagerGateway;
import org.apache.flink.runtime.instance.SlotSharingGroupId;
import org.apache.flink.runtime.jobmanager.scheduler.Locality;
import org.apache.flink.runtime.jobmanager.slots.DummySlotOwner;
import org.apache.flink.runtime.jobmaster.LogicalSlot;
import org.apache.flink.runtime.jobmaster.SlotContext;
import org.apache.flink.runtime.jobmaster.SlotInfo;
import org.apache.flink.runtime.jobmaster.SlotRequestId;
import org.apache.flink.runtime.taskmanager.LocalTaskManagerLocation;
import org.apache.flink.util.AbstractID;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for the {@link SlotSharingManager}.
 */
public class SlotSharingManagerTest extends TestLogger {
    private static final SlotSharingGroupId SLOT_SHARING_GROUP_ID = new SlotSharingGroupId();

    private static final DummySlotOwner SLOT_OWNER = new DummySlotOwner();

    @Test
    public void testRootSlotCreation() {
        final TestingAllocatedSlotActions allocatedSlotActions = new TestingAllocatedSlotActions();
        final SlotSharingManager slotSharingManager = new SlotSharingManager(SlotSharingManagerTest.SLOT_SHARING_GROUP_ID, allocatedSlotActions, SlotSharingManagerTest.SLOT_OWNER);
        SlotRequestId slotRequestId = new SlotRequestId();
        SlotRequestId allocatedSlotRequestId = new SlotRequestId();
        final SlotSharingManager.MultiTaskSlot multiTaskSlot = slotSharingManager.createRootSlot(slotRequestId, new CompletableFuture(), allocatedSlotRequestId);
        Assert.assertEquals(slotRequestId, multiTaskSlot.getSlotRequestId());
        Assert.assertNotNull(slotSharingManager.getTaskSlot(slotRequestId));
    }

    @Test
    public void testRootSlotRelease() throws InterruptedException, ExecutionException {
        final CompletableFuture<SlotRequestId> slotReleasedFuture = new CompletableFuture<>();
        final TestingAllocatedSlotActions allocatedSlotActions = new TestingAllocatedSlotActions();
        allocatedSlotActions.setReleaseSlotConsumer(( tuple3) -> slotReleasedFuture.complete(tuple3.f0));
        final SlotSharingManager slotSharingManager = new SlotSharingManager(SlotSharingManagerTest.SLOT_SHARING_GROUP_ID, allocatedSlotActions, SlotSharingManagerTest.SLOT_OWNER);
        SlotRequestId slotRequestId = new SlotRequestId();
        SlotRequestId allocatedSlotRequestId = new SlotRequestId();
        CompletableFuture<SlotContext> slotContextFuture = new CompletableFuture<>();
        SlotSharingManager.MultiTaskSlot rootSlot = slotSharingManager.createRootSlot(slotRequestId, slotContextFuture, allocatedSlotRequestId);
        Assert.assertTrue(slotSharingManager.contains(slotRequestId));
        rootSlot.release(new FlinkException("Test exception"));
        // check that we return the allocated slot
        Assert.assertEquals(allocatedSlotRequestId, slotReleasedFuture.get());
        Assert.assertFalse(slotSharingManager.contains(slotRequestId));
    }

    /**
     * Tests that we can create nested slots.
     */
    @Test
    public void testNestedSlotCreation() {
        final TestingAllocatedSlotActions allocatedSlotActions = new TestingAllocatedSlotActions();
        final SlotSharingManager slotSharingManager = new SlotSharingManager(SlotSharingManagerTest.SLOT_SHARING_GROUP_ID, allocatedSlotActions, SlotSharingManagerTest.SLOT_OWNER);
        SlotSharingManager.MultiTaskSlot rootSlot = slotSharingManager.createRootSlot(new SlotRequestId(), new CompletableFuture(), new SlotRequestId());
        AbstractID singleTaskSlotGroupId = new AbstractID();
        SlotRequestId singleTaskSlotRequestId = new SlotRequestId();
        SlotSharingManager.SingleTaskSlot singleTaskSlot = rootSlot.allocateSingleTaskSlot(singleTaskSlotRequestId, singleTaskSlotGroupId, LOCAL);
        AbstractID multiTaskSlotGroupId = new AbstractID();
        SlotRequestId multiTaskSlotRequestId = new SlotRequestId();
        SlotSharingManager.MultiTaskSlot multiTaskSlot = rootSlot.allocateMultiTaskSlot(multiTaskSlotRequestId, multiTaskSlotGroupId);
        Assert.assertTrue(Objects.equals(singleTaskSlotRequestId, singleTaskSlot.getSlotRequestId()));
        Assert.assertTrue(Objects.equals(multiTaskSlotRequestId, multiTaskSlot.getSlotRequestId()));
        Assert.assertTrue(rootSlot.contains(singleTaskSlotGroupId));
        Assert.assertTrue(rootSlot.contains(multiTaskSlotGroupId));
        Assert.assertTrue(slotSharingManager.contains(singleTaskSlotRequestId));
        Assert.assertTrue(slotSharingManager.contains(multiTaskSlotRequestId));
    }

    /**
     * Tests that we can release nested slots from the leaves onwards
     */
    @Test
    public void testNestedSlotRelease() throws Exception {
        TestingAllocatedSlotActions testingAllocatedSlotActions = new TestingAllocatedSlotActions();
        final CompletableFuture<SlotRequestId> releasedSlotFuture = new CompletableFuture<>();
        testingAllocatedSlotActions.setReleaseSlotConsumer(( tuple3) -> releasedSlotFuture.complete(tuple3.f0));
        final SlotSharingManager slotSharingManager = new SlotSharingManager(SlotSharingManagerTest.SLOT_SHARING_GROUP_ID, testingAllocatedSlotActions, SlotSharingManagerTest.SLOT_OWNER);
        SlotRequestId rootSlotRequestId = new SlotRequestId();
        SlotRequestId allocatedSlotRequestId = new SlotRequestId();
        SlotSharingManager.MultiTaskSlot rootSlot = slotSharingManager.createRootSlot(rootSlotRequestId, new CompletableFuture(), allocatedSlotRequestId);
        SlotRequestId singleTaskSlotRequestId = new SlotRequestId();
        SlotSharingManager.SingleTaskSlot singleTaskSlot = rootSlot.allocateSingleTaskSlot(singleTaskSlotRequestId, new AbstractID(), LOCAL);
        SlotRequestId multiTaskSlotRequestId = new SlotRequestId();
        SlotSharingManager.MultiTaskSlot multiTaskSlot = rootSlot.allocateMultiTaskSlot(multiTaskSlotRequestId, new AbstractID());
        CompletableFuture<LogicalSlot> singleTaskSlotFuture = singleTaskSlot.getLogicalSlotFuture();
        Assert.assertTrue(slotSharingManager.contains(rootSlotRequestId));
        Assert.assertTrue(slotSharingManager.contains(singleTaskSlotRequestId));
        Assert.assertFalse(singleTaskSlotFuture.isDone());
        FlinkException testException = new FlinkException("Test exception");
        singleTaskSlot.release(testException);
        // check that we fail the single task slot future
        Assert.assertTrue(singleTaskSlotFuture.isCompletedExceptionally());
        Assert.assertFalse(slotSharingManager.contains(singleTaskSlotRequestId));
        // the root slot has still one child
        Assert.assertTrue(slotSharingManager.contains(rootSlotRequestId));
        multiTaskSlot.release(testException);
        Assert.assertEquals(allocatedSlotRequestId, releasedSlotFuture.get());
        Assert.assertFalse(slotSharingManager.contains(rootSlotRequestId));
        Assert.assertFalse(slotSharingManager.contains(multiTaskSlotRequestId));
        Assert.assertTrue(slotSharingManager.isEmpty());
    }

    /**
     * Tests that we can release inner slots and that this triggers the slot release for all
     * its children.
     */
    @Test
    public void testInnerSlotRelease() {
        final TestingAllocatedSlotActions allocatedSlotActions = new TestingAllocatedSlotActions();
        final SlotSharingManager slotSharingManager = new SlotSharingManager(SlotSharingManagerTest.SLOT_SHARING_GROUP_ID, allocatedSlotActions, SlotSharingManagerTest.SLOT_OWNER);
        SlotSharingManager.MultiTaskSlot rootSlot = slotSharingManager.createRootSlot(new SlotRequestId(), new CompletableFuture(), new SlotRequestId());
        SlotSharingManager.MultiTaskSlot multiTaskSlot = rootSlot.allocateMultiTaskSlot(new SlotRequestId(), new AbstractID());
        SlotSharingManager.SingleTaskSlot singleTaskSlot1 = multiTaskSlot.allocateSingleTaskSlot(new SlotRequestId(), new AbstractID(), LOCAL);
        SlotSharingManager.MultiTaskSlot multiTaskSlot1 = multiTaskSlot.allocateMultiTaskSlot(new SlotRequestId(), new AbstractID());
        Assert.assertTrue(slotSharingManager.contains(multiTaskSlot1.getSlotRequestId()));
        Assert.assertTrue(slotSharingManager.contains(singleTaskSlot1.getSlotRequestId()));
        Assert.assertTrue(slotSharingManager.contains(multiTaskSlot.getSlotRequestId()));
        multiTaskSlot.release(new FlinkException("Test exception"));
        Assert.assertFalse(slotSharingManager.contains(multiTaskSlot1.getSlotRequestId()));
        Assert.assertFalse(slotSharingManager.contains(singleTaskSlot1.getSlotRequestId()));
        Assert.assertFalse(slotSharingManager.contains(multiTaskSlot.getSlotRequestId()));
        Assert.assertTrue(singleTaskSlot1.getLogicalSlotFuture().isCompletedExceptionally());
    }

    /**
     * Tests that the logical task slot futures are completed once the slot context
     * future is completed.
     */
    @Test
    public void testSlotContextFutureCompletion() throws Exception {
        final TestingAllocatedSlotActions allocatedSlotActions = new TestingAllocatedSlotActions();
        final SlotSharingManager slotSharingManager = new SlotSharingManager(SlotSharingManagerTest.SLOT_SHARING_GROUP_ID, allocatedSlotActions, SlotSharingManagerTest.SLOT_OWNER);
        final SlotContext slotContext = new org.apache.flink.runtime.instance.SimpleSlotContext(new AllocationID(), new LocalTaskManagerLocation(), 0, new SimpleAckingTaskManagerGateway());
        CompletableFuture<SlotContext> slotContextFuture = new CompletableFuture<>();
        SlotSharingManager.MultiTaskSlot rootSlot = slotSharingManager.createRootSlot(new SlotRequestId(), slotContextFuture, new SlotRequestId());
        Locality locality1 = Locality.LOCAL;
        SlotSharingManager.SingleTaskSlot singleTaskSlot1 = rootSlot.allocateSingleTaskSlot(new SlotRequestId(), new AbstractID(), locality1);
        Locality locality2 = Locality.HOST_LOCAL;
        SlotSharingManager.SingleTaskSlot singleTaskSlot2 = rootSlot.allocateSingleTaskSlot(new SlotRequestId(), new AbstractID(), locality2);
        CompletableFuture<LogicalSlot> logicalSlotFuture1 = singleTaskSlot1.getLogicalSlotFuture();
        CompletableFuture<LogicalSlot> logicalSlotFuture2 = singleTaskSlot2.getLogicalSlotFuture();
        Assert.assertFalse(logicalSlotFuture1.isDone());
        Assert.assertFalse(logicalSlotFuture2.isDone());
        slotContextFuture.complete(slotContext);
        Assert.assertTrue(logicalSlotFuture1.isDone());
        Assert.assertTrue(logicalSlotFuture2.isDone());
        final LogicalSlot logicalSlot1 = logicalSlotFuture1.get();
        final LogicalSlot logicalSlot2 = logicalSlotFuture2.get();
        Assert.assertEquals(logicalSlot1.getAllocationId(), slotContext.getAllocationId());
        Assert.assertEquals(logicalSlot2.getAllocationId(), slotContext.getAllocationId());
        Assert.assertEquals(locality1, logicalSlot1.getLocality());
        Assert.assertEquals(locality2, logicalSlot2.getLocality());
        Locality locality3 = Locality.NON_LOCAL;
        SlotSharingManager.SingleTaskSlot singleTaskSlot3 = rootSlot.allocateSingleTaskSlot(new SlotRequestId(), new AbstractID(), locality3);
        CompletableFuture<LogicalSlot> logicalSlotFuture3 = singleTaskSlot3.getLogicalSlotFuture();
        Assert.assertTrue(logicalSlotFuture3.isDone());
        LogicalSlot logicalSlot3 = logicalSlotFuture3.get();
        Assert.assertEquals(locality3, logicalSlot3.getLocality());
        Assert.assertEquals(slotContext.getAllocationId(), logicalSlot3.getAllocationId());
    }

    /**
     * Tests that slot context future failures will release the root slot
     */
    @Test
    public void testSlotContextFutureFailure() {
        final TestingAllocatedSlotActions allocatedSlotActions = new TestingAllocatedSlotActions();
        SlotSharingManager slotSharingManager = new SlotSharingManager(SlotSharingManagerTest.SLOT_SHARING_GROUP_ID, allocatedSlotActions, SlotSharingManagerTest.SLOT_OWNER);
        CompletableFuture<SlotContext> slotContextFuture = new CompletableFuture<>();
        Assert.assertTrue(slotSharingManager.isEmpty());
        SlotSharingManager.MultiTaskSlot rootSlot = slotSharingManager.createRootSlot(new SlotRequestId(), slotContextFuture, new SlotRequestId());
        SlotSharingManager.SingleTaskSlot singleTaskSlot = rootSlot.allocateSingleTaskSlot(new SlotRequestId(), new AbstractID(), LOCAL);
        slotContextFuture.completeExceptionally(new FlinkException("Test exception"));
        Assert.assertTrue(singleTaskSlot.getLogicalSlotFuture().isCompletedExceptionally());
        Assert.assertTrue(slotSharingManager.isEmpty());
        Assert.assertTrue(slotSharingManager.getResolvedRootSlots().isEmpty());
        Assert.assertTrue(slotSharingManager.getUnresolvedRootSlots().isEmpty());
    }

    /**
     * Tests that the root slot are moved from unresolved to resolved once the
     * slot context future is successfully completed
     */
    @Test
    public void testRootSlotTransition() {
        final TestingAllocatedSlotActions allocatedSlotActions = new TestingAllocatedSlotActions();
        SlotSharingManager slotSharingManager = new SlotSharingManager(SlotSharingManagerTest.SLOT_SHARING_GROUP_ID, allocatedSlotActions, SlotSharingManagerTest.SLOT_OWNER);
        CompletableFuture<SlotContext> slotContextFuture = new CompletableFuture<>();
        SlotSharingManager.MultiTaskSlot rootSlot = slotSharingManager.createRootSlot(new SlotRequestId(), slotContextFuture, new SlotRequestId());
        Assert.assertTrue(slotSharingManager.getUnresolvedRootSlots().contains(rootSlot));
        Assert.assertFalse(slotSharingManager.getResolvedRootSlots().contains(rootSlot));
        // now complete the slotContextFuture
        slotContextFuture.complete(new org.apache.flink.runtime.instance.SimpleSlotContext(new AllocationID(), new LocalTaskManagerLocation(), 0, new SimpleAckingTaskManagerGateway()));
        Assert.assertFalse(slotSharingManager.getUnresolvedRootSlots().contains(rootSlot));
        Assert.assertTrue(slotSharingManager.getResolvedRootSlots().contains(rootSlot));
    }

    /**
     * Tests that we can correctly retrieve resolved slots.
     */
    @Test
    public void testGetResolvedSlot() {
        final TestingAllocatedSlotActions allocatedSlotActions = new TestingAllocatedSlotActions();
        SlotSharingManager slotSharingManager = new SlotSharingManager(SlotSharingManagerTest.SLOT_SHARING_GROUP_ID, allocatedSlotActions, SlotSharingManagerTest.SLOT_OWNER);
        SlotSharingManager.MultiTaskSlot rootSlot = slotSharingManager.createRootSlot(new SlotRequestId(), CompletableFuture.completedFuture(new org.apache.flink.runtime.instance.SimpleSlotContext(new AllocationID(), new LocalTaskManagerLocation(), 0, new SimpleAckingTaskManagerGateway())), new SlotRequestId());
        AbstractID groupId = new AbstractID();
        Collection<SlotInfo> slotInfos = slotSharingManager.listResolvedRootSlotInfo(groupId);
        Assert.assertEquals(1, slotInfos.size());
        SlotInfo slotInfo = slotInfos.iterator().next();
        SlotSharingManager.MultiTaskSlot resolvedMultiTaskSlot = slotSharingManager.getResolvedRootSlot(slotInfo);
        SlotSelectionStrategy.SlotInfoAndLocality slotInfoAndLocality = INSTANCE.selectBestSlotForProfile(slotInfos, SlotProfile.noRequirements()).get();
        Assert.assertNotNull(resolvedMultiTaskSlot);
        Assert.assertEquals(UNCONSTRAINED, slotInfoAndLocality.getLocality());
        Assert.assertEquals(rootSlot.getSlotRequestId(), resolvedMultiTaskSlot.getSlotRequestId());
        // occupy the resolved root slot
        resolvedMultiTaskSlot.allocateSingleTaskSlot(new SlotRequestId(), groupId, UNCONSTRAINED);
        slotInfos = slotSharingManager.listResolvedRootSlotInfo(groupId);
        Assert.assertTrue(slotInfos.isEmpty());
    }

    /**
     * Tests that the location preferences are honoured when looking for a resolved slot.
     */
    @Test
    public void testGetResolvedSlotWithLocationPreferences() {
        final TestingAllocatedSlotActions allocatedSlotActions = new TestingAllocatedSlotActions();
        SlotSharingManager slotSharingManager = new SlotSharingManager(SlotSharingManagerTest.SLOT_SHARING_GROUP_ID, allocatedSlotActions, SlotSharingManagerTest.SLOT_OWNER);
        SlotSharingManager.MultiTaskSlot rootSlot1 = slotSharingManager.createRootSlot(new SlotRequestId(), CompletableFuture.completedFuture(new org.apache.flink.runtime.instance.SimpleSlotContext(new AllocationID(), new LocalTaskManagerLocation(), 0, new SimpleAckingTaskManagerGateway())), new SlotRequestId());
        LocalTaskManagerLocation taskManagerLocation = new LocalTaskManagerLocation();
        SlotSharingManager.MultiTaskSlot rootSlot2 = slotSharingManager.createRootSlot(new SlotRequestId(), CompletableFuture.completedFuture(new org.apache.flink.runtime.instance.SimpleSlotContext(new AllocationID(), taskManagerLocation, 0, new SimpleAckingTaskManagerGateway())), new SlotRequestId());
        AbstractID groupId = new AbstractID();
        SlotProfile slotProfile = SlotProfile.preferredLocality(UNKNOWN, Collections.singleton(taskManagerLocation));
        Collection<SlotInfo> slotInfos = slotSharingManager.listResolvedRootSlotInfo(groupId);
        SlotSelectionStrategy.SlotInfoAndLocality slotInfoAndLocality = INSTANCE.selectBestSlotForProfile(slotInfos, slotProfile).get();
        SlotSharingManager.MultiTaskSlot resolvedRootSlot = slotSharingManager.getResolvedRootSlot(slotInfoAndLocality.getSlotInfo());
        Assert.assertNotNull(resolvedRootSlot);
        Assert.assertEquals(LOCAL, slotInfoAndLocality.getLocality());
        Assert.assertEquals(rootSlot2.getSlotRequestId(), resolvedRootSlot.getSlotRequestId());
        // occupy the slot
        resolvedRootSlot.allocateSingleTaskSlot(new SlotRequestId(), groupId, slotInfoAndLocality.getLocality());
        slotInfos = slotSharingManager.listResolvedRootSlotInfo(groupId);
        slotInfoAndLocality = INSTANCE.selectBestSlotForProfile(slotInfos, slotProfile).get();
        resolvedRootSlot = slotSharingManager.getResolvedRootSlot(slotInfoAndLocality.getSlotInfo());
        Assert.assertNotNull(resolvedRootSlot);
        Assert.assertNotSame(LOCAL, slotInfoAndLocality.getLocality());
        Assert.assertEquals(rootSlot1.getSlotRequestId(), resolvedRootSlot.getSlotRequestId());
    }

    @Test
    public void testGetUnresolvedSlot() {
        final TestingAllocatedSlotActions allocatedSlotActions = new TestingAllocatedSlotActions();
        SlotSharingManager slotSharingManager = new SlotSharingManager(SlotSharingManagerTest.SLOT_SHARING_GROUP_ID, allocatedSlotActions, SlotSharingManagerTest.SLOT_OWNER);
        SlotSharingManager.MultiTaskSlot rootSlot1 = slotSharingManager.createRootSlot(new SlotRequestId(), new CompletableFuture(), new SlotRequestId());
        final AbstractID groupId = new AbstractID();
        SlotSharingManager.MultiTaskSlot unresolvedRootSlot = slotSharingManager.getUnresolvedRootSlot(groupId);
        Assert.assertNotNull(unresolvedRootSlot);
        Assert.assertEquals(rootSlot1.getSlotRequestId(), unresolvedRootSlot.getSlotRequestId());
        // occupy the unresolved slot
        unresolvedRootSlot.allocateSingleTaskSlot(new SlotRequestId(), groupId, Locality.UNKNOWN);
        SlotSharingManager.MultiTaskSlot unresolvedRootSlot1 = slotSharingManager.getUnresolvedRootSlot(groupId);
        // we should no longer have a free unresolved root slot
        Assert.assertNull(unresolvedRootSlot1);
    }
}

