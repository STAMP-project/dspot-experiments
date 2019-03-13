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


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.clusterframework.types.AllocationID;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.clusterframework.types.ResourceProfile;
import org.apache.flink.runtime.clusterframework.types.SlotProfile;
import org.apache.flink.runtime.concurrent.ComponentMainThreadExecutor;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.executiongraph.TestingComponentMainThreadExecutorServiceAdapter;
import org.apache.flink.runtime.executiongraph.utils.SimpleAckingTaskManagerGateway;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobmanager.scheduler.DummyScheduledUnit;
import org.apache.flink.runtime.jobmanager.scheduler.ScheduledUnit;
import org.apache.flink.runtime.jobmaster.LogicalSlot;
import org.apache.flink.runtime.jobmaster.SlotRequestId;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.resourcemanager.SlotRequest;
import org.apache.flink.runtime.resourcemanager.utils.TestingResourceManagerGateway;
import org.apache.flink.runtime.taskexecutor.slot.SlotOffer;
import org.apache.flink.runtime.taskmanager.LocalTaskManagerLocation;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.util.clock.ManualClock;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class SlotPoolTest extends TestLogger {
    private final Time timeout = Time.seconds(10L);

    private JobID jobId;

    private TaskManagerLocation taskManagerLocation;

    private SimpleAckingTaskManagerGateway taskManagerGateway;

    private TestingResourceManagerGateway resourceManagerGateway;

    private ComponentMainThreadExecutor mainThreadExecutor = TestingComponentMainThreadExecutorServiceAdapter.forMainThread();

    @Test
    public void testAllocateSimpleSlot() throws Exception {
        CompletableFuture<SlotRequest> slotRequestFuture = new CompletableFuture<>();
        resourceManagerGateway.setRequestSlotConsumer(slotRequestFuture::complete);
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId)) {
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            Scheduler scheduler = SlotPoolTest.setupScheduler(slotPool, mainThreadExecutor);
            slotPool.registerTaskManager(taskManagerLocation.getResourceID());
            SlotRequestId requestId = new SlotRequestId();
            CompletableFuture<LogicalSlot> future = scheduler.allocateSlot(requestId, new DummyScheduledUnit(), SlotProfile.noLocality(AvailableSlotsTest.DEFAULT_TESTING_PROFILE), true, timeout);
            Assert.assertFalse(future.isDone());
            final SlotRequest slotRequest = slotRequestFuture.get(timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            final SlotOffer slotOffer = new SlotOffer(slotRequest.getAllocationId(), 0, AvailableSlotsTest.DEFAULT_TESTING_PROFILE);
            Assert.assertTrue(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, slotOffer));
            LogicalSlot slot = future.get(1, TimeUnit.SECONDS);
            Assert.assertTrue(future.isDone());
            Assert.assertTrue(slot.isAlive());
            Assert.assertEquals(taskManagerLocation, slot.getTaskManagerLocation());
        }
    }

    @Test
    public void testAllocationFulfilledByReturnedSlot() throws Exception {
        final ArrayBlockingQueue<SlotRequest> slotRequestQueue = new ArrayBlockingQueue<>(2);
        resourceManagerGateway.setRequestSlotConsumer(( slotRequest) -> {
            while (!(slotRequestQueue.offer(slotRequest))) {
                // noop
            } 
        });
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId)) {
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            Scheduler scheduler = SlotPoolTest.setupScheduler(slotPool, mainThreadExecutor);
            slotPool.registerTaskManager(taskManagerLocation.getResourceID());
            CompletableFuture<LogicalSlot> future1 = scheduler.allocateSlot(new SlotRequestId(), new DummyScheduledUnit(), SlotProfile.noLocality(AvailableSlotsTest.DEFAULT_TESTING_PROFILE), true, timeout);
            CompletableFuture<LogicalSlot> future2 = scheduler.allocateSlot(new SlotRequestId(), new DummyScheduledUnit(), SlotProfile.noLocality(AvailableSlotsTest.DEFAULT_TESTING_PROFILE), true, timeout);
            Assert.assertFalse(future1.isDone());
            Assert.assertFalse(future2.isDone());
            final List<SlotRequest> slotRequests = new ArrayList<>(2);
            for (int i = 0; i < 2; i++) {
                slotRequests.add(slotRequestQueue.poll(timeout.toMilliseconds(), TimeUnit.MILLISECONDS));
            }
            final SlotOffer slotOffer = new SlotOffer(slotRequests.get(0).getAllocationId(), 0, AvailableSlotsTest.DEFAULT_TESTING_PROFILE);
            Assert.assertTrue(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, slotOffer));
            LogicalSlot slot1 = future1.get(1, TimeUnit.SECONDS);
            Assert.assertTrue(future1.isDone());
            Assert.assertFalse(future2.isDone());
            // return this slot to pool
            slot1.releaseSlot();
            // second allocation fulfilled by previous slot returning
            LogicalSlot slot2 = future2.get(1, TimeUnit.SECONDS);
            Assert.assertTrue(future2.isDone());
            Assert.assertNotEquals(slot1, slot2);
            Assert.assertFalse(slot1.isAlive());
            Assert.assertTrue(slot2.isAlive());
            Assert.assertEquals(slot1.getTaskManagerLocation(), slot2.getTaskManagerLocation());
            Assert.assertEquals(slot1.getPhysicalSlotNumber(), slot2.getPhysicalSlotNumber());
            Assert.assertEquals(slot1.getAllocationId(), slot2.getAllocationId());
        }
    }

    @Test
    public void testAllocateWithFreeSlot() throws Exception {
        final CompletableFuture<SlotRequest> slotRequestFuture = new CompletableFuture<>();
        resourceManagerGateway.setRequestSlotConsumer(slotRequestFuture::complete);
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId)) {
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            Scheduler scheduler = SlotPoolTest.setupScheduler(slotPool, mainThreadExecutor);
            slotPool.registerTaskManager(taskManagerLocation.getResourceID());
            CompletableFuture<LogicalSlot> future1 = scheduler.allocateSlot(new SlotRequestId(), new DummyScheduledUnit(), SlotProfile.noLocality(AvailableSlotsTest.DEFAULT_TESTING_PROFILE), true, timeout);
            Assert.assertFalse(future1.isDone());
            final SlotRequest slotRequest = slotRequestFuture.get(timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            final SlotOffer slotOffer = new SlotOffer(slotRequest.getAllocationId(), 0, AvailableSlotsTest.DEFAULT_TESTING_PROFILE);
            Assert.assertTrue(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, slotOffer));
            LogicalSlot slot1 = future1.get(1, TimeUnit.SECONDS);
            Assert.assertTrue(future1.isDone());
            // return this slot to pool
            slot1.releaseSlot();
            CompletableFuture<LogicalSlot> future2 = scheduler.allocateSlot(new SlotRequestId(), new DummyScheduledUnit(), SlotProfile.noLocality(AvailableSlotsTest.DEFAULT_TESTING_PROFILE), true, timeout);
            // second allocation fulfilled by previous slot returning
            LogicalSlot slot2 = future2.get(1, TimeUnit.SECONDS);
            Assert.assertTrue(future2.isDone());
            Assert.assertNotEquals(slot1, slot2);
            Assert.assertFalse(slot1.isAlive());
            Assert.assertTrue(slot2.isAlive());
            Assert.assertEquals(slot1.getTaskManagerLocation(), slot2.getTaskManagerLocation());
            Assert.assertEquals(slot1.getPhysicalSlotNumber(), slot2.getPhysicalSlotNumber());
        }
    }

    @Test
    public void testOfferSlot() throws Exception {
        final CompletableFuture<SlotRequest> slotRequestFuture = new CompletableFuture<>();
        resourceManagerGateway.setRequestSlotConsumer(slotRequestFuture::complete);
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId)) {
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            Scheduler scheduler = SlotPoolTest.setupScheduler(slotPool, mainThreadExecutor);
            slotPool.registerTaskManager(taskManagerLocation.getResourceID());
            CompletableFuture<LogicalSlot> future = scheduler.allocateSlot(new SlotRequestId(), new DummyScheduledUnit(), SlotProfile.noLocality(AvailableSlotsTest.DEFAULT_TESTING_PROFILE), true, timeout);
            Assert.assertFalse(future.isDone());
            final SlotRequest slotRequest = slotRequestFuture.get(timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            final SlotOffer slotOffer = new SlotOffer(slotRequest.getAllocationId(), 0, AvailableSlotsTest.DEFAULT_TESTING_PROFILE);
            final TaskManagerLocation invalidTaskManagerLocation = new LocalTaskManagerLocation();
            // slot from unregistered resource
            Assert.assertFalse(slotPool.offerSlot(invalidTaskManagerLocation, taskManagerGateway, slotOffer));
            final SlotOffer nonRequestedSlotOffer = new SlotOffer(new AllocationID(), 0, AvailableSlotsTest.DEFAULT_TESTING_PROFILE);
            // we'll also accept non requested slots
            Assert.assertTrue(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, nonRequestedSlotOffer));
            // accepted slot
            Assert.assertTrue(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, slotOffer));
            LogicalSlot slot = future.get(timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            Assert.assertTrue(slot.isAlive());
            // duplicated offer with using slot
            Assert.assertTrue(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, slotOffer));
            Assert.assertTrue(slot.isAlive());
            final SlotOffer anotherSlotOfferWithSameAllocationId = new SlotOffer(slotRequest.getAllocationId(), 1, AvailableSlotsTest.DEFAULT_TESTING_PROFILE);
            Assert.assertFalse(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, anotherSlotOfferWithSameAllocationId));
            TaskManagerLocation anotherTaskManagerLocation = new LocalTaskManagerLocation();
            Assert.assertFalse(slotPool.offerSlot(anotherTaskManagerLocation, taskManagerGateway, slotOffer));
            // duplicated offer with free slot
            slot.releaseSlot();
            Assert.assertTrue(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, slotOffer));
            Assert.assertFalse(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, anotherSlotOfferWithSameAllocationId));
            Assert.assertFalse(slotPool.offerSlot(anotherTaskManagerLocation, taskManagerGateway, slotOffer));
        }
    }

    @Test
    public void testReleaseResource() throws Exception {
        final CompletableFuture<SlotRequest> slotRequestFuture = new CompletableFuture<>();
        resourceManagerGateway.setRequestSlotConsumer(slotRequestFuture::complete);
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId)) {
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            Scheduler scheduler = SlotPoolTest.setupScheduler(slotPool, mainThreadExecutor);
            slotPool.registerTaskManager(taskManagerLocation.getResourceID());
            CompletableFuture<LogicalSlot> future1 = scheduler.allocateSlot(new SlotRequestId(), new DummyScheduledUnit(), SlotProfile.noLocality(AvailableSlotsTest.DEFAULT_TESTING_PROFILE), true, timeout);
            final SlotRequest slotRequest = slotRequestFuture.get(timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            CompletableFuture<LogicalSlot> future2 = scheduler.allocateSlot(new SlotRequestId(), new DummyScheduledUnit(), SlotProfile.noLocality(AvailableSlotsTest.DEFAULT_TESTING_PROFILE), true, timeout);
            final SlotOffer slotOffer = new SlotOffer(slotRequest.getAllocationId(), 0, AvailableSlotsTest.DEFAULT_TESTING_PROFILE);
            Assert.assertTrue(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, slotOffer));
            LogicalSlot slot1 = future1.get(1, TimeUnit.SECONDS);
            Assert.assertTrue(future1.isDone());
            Assert.assertFalse(future2.isDone());
            final CompletableFuture<?> releaseFuture = new CompletableFuture<>();
            final DummyPayload dummyPayload = new DummyPayload(releaseFuture);
            slot1.tryAssignPayload(dummyPayload);
            slotPool.releaseTaskManager(taskManagerLocation.getResourceID(), null);
            releaseFuture.get();
            Assert.assertFalse(slot1.isAlive());
            // slot released and not usable, second allocation still not fulfilled
            Thread.sleep(10);
            Assert.assertFalse(future2.isDone());
        }
    }

    /**
     * Tests that a slot request is cancelled if it failed with an exception (e.g. TimeoutException).
     *
     * <p>See FLINK-7870
     */
    @Test
    public void testSlotRequestCancellationUponFailingRequest() throws Exception {
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId)) {
            final CompletableFuture<Acknowledge> requestSlotFuture = new CompletableFuture<>();
            final CompletableFuture<AllocationID> cancelSlotFuture = new CompletableFuture<>();
            final CompletableFuture<AllocationID> requestSlotFutureAllocationId = new CompletableFuture<>();
            resourceManagerGateway.setRequestSlotFuture(requestSlotFuture);
            resourceManagerGateway.setRequestSlotConsumer(( slotRequest) -> requestSlotFutureAllocationId.complete(slotRequest.getAllocationId()));
            resourceManagerGateway.setCancelSlotConsumer(cancelSlotFuture::complete);
            final ScheduledUnit scheduledUnit = new ScheduledUnit(new JobVertexID(), null, null);
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            Scheduler scheduler = SlotPoolTest.setupScheduler(slotPool, mainThreadExecutor);
            SlotProfile slotProfile = new SlotProfile(ResourceProfile.UNKNOWN, Collections.emptyList(), Collections.emptySet());
            CompletableFuture<LogicalSlot> slotFuture = scheduler.allocateSlot(new SlotRequestId(), scheduledUnit, slotProfile, true, timeout);
            requestSlotFuture.completeExceptionally(new FlinkException("Testing exception."));
            try {
                slotFuture.get();
                Assert.fail("The slot future should not have been completed properly.");
            } catch (Exception ignored) {
                // expected
            }
            // check that a failure triggered the slot request cancellation
            // with the correct allocation id
            Assert.assertEquals(requestSlotFutureAllocationId.get(), cancelSlotFuture.get());
        }
    }

    /**
     * Tests that unused offered slots are directly used to fulfill pending slot
     * requests.
     *
     * Moreover it tests that the old slot request is canceled
     *
     * <p>See FLINK-8089, FLINK-8934
     */
    @Test
    public void testFulfillingSlotRequestsWithUnusedOfferedSlots() throws Exception {
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId)) {
            final ArrayBlockingQueue<AllocationID> allocationIds = new ArrayBlockingQueue<>(2);
            resourceManagerGateway.setRequestSlotConsumer((SlotRequest slotRequest) -> allocationIds.offer(slotRequest.getAllocationId()));
            final ArrayBlockingQueue<AllocationID> canceledAllocations = new ArrayBlockingQueue<>(2);
            resourceManagerGateway.setCancelSlotConsumer(canceledAllocations::offer);
            final SlotRequestId slotRequestId1 = new SlotRequestId();
            final SlotRequestId slotRequestId2 = new SlotRequestId();
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            final Scheduler scheduler = SlotPoolTest.setupScheduler(slotPool, mainThreadExecutor);
            final ScheduledUnit scheduledUnit = new ScheduledUnit(new JobVertexID(), null, null);
            CompletableFuture<LogicalSlot> slotFuture1 = scheduler.allocateSlot(slotRequestId1, scheduledUnit, SlotProfile.noRequirements(), true, timeout);
            // wait for the first slot request
            final AllocationID allocationId1 = allocationIds.take();
            CompletableFuture<LogicalSlot> slotFuture2 = scheduler.allocateSlot(slotRequestId2, scheduledUnit, SlotProfile.noRequirements(), true, timeout);
            // wait for the second slot request
            final AllocationID allocationId2 = allocationIds.take();
            slotPool.releaseSlot(slotRequestId1, null);
            try {
                // this should fail with a CancellationException
                slotFuture1.get();
                Assert.fail("The first slot future should have failed because it was cancelled.");
            } catch (ExecutionException ee) {
                // expected
                Assert.assertTrue(((ExceptionUtils.stripExecutionException(ee)) instanceof FlinkException));
            }
            Assert.assertEquals(allocationId1, canceledAllocations.take());
            final SlotOffer slotOffer = new SlotOffer(allocationId1, 0, ResourceProfile.UNKNOWN);
            slotPool.registerTaskManager(taskManagerLocation.getResourceID());
            Assert.assertTrue(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, slotOffer));
            // the slot offer should fulfill the second slot request
            Assert.assertEquals(allocationId1, slotFuture2.get().getAllocationId());
            // check that the second slot allocation has been canceled
            Assert.assertEquals(allocationId2, canceledAllocations.take());
        }
    }

    /**
     * Tests that a SlotPoolImpl shutdown releases all registered slots
     */
    @Test
    public void testShutdownReleasesAllSlots() throws Exception {
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId)) {
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            slotPool.registerTaskManager(taskManagerLocation.getResourceID());
            final int numSlotOffers = 2;
            final Collection<SlotOffer> slotOffers = new ArrayList<>(numSlotOffers);
            for (int i = 0; i < numSlotOffers; i++) {
                slotOffers.add(new SlotOffer(new AllocationID(), i, ResourceProfile.UNKNOWN));
            }
            final ArrayBlockingQueue<AllocationID> freedSlotQueue = new ArrayBlockingQueue<>(numSlotOffers);
            taskManagerGateway.setFreeSlotFunction((AllocationID allocationID,Throwable cause) -> {
                try {
                    freedSlotQueue.put(allocationID);
                    return CompletableFuture.completedFuture(Acknowledge.get());
                } catch (InterruptedException e) {
                    return FutureUtils.completedExceptionally(e);
                }
            });
            final Collection<SlotOffer> acceptedSlotOffers = slotPool.offerSlots(taskManagerLocation, taskManagerGateway, slotOffers);
            MatcherAssert.assertThat(acceptedSlotOffers, Matchers.equalTo(slotOffers));
            // shut down the slot pool
            slotPool.close();
            // the shut down operation should have freed all registered slots
            ArrayList<AllocationID> freedSlots = new ArrayList<>(numSlotOffers);
            while ((freedSlots.size()) < numSlotOffers) {
                freedSlotQueue.drainTo(freedSlots);
            } 
            MatcherAssert.assertThat(freedSlots, Matchers.containsInAnyOrder(slotOffers.stream().map(SlotOffer::getAllocationId).toArray()));
        }
    }

    @Test
    public void testCheckIdleSlot() throws Exception {
        final ManualClock clock = new ManualClock();
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId, clock, TestingUtils.infiniteTime(), timeout)) {
            final BlockingQueue<AllocationID> freedSlots = new ArrayBlockingQueue<>(1);
            taskManagerGateway.setFreeSlotFunction((AllocationID allocationId,Throwable cause) -> {
                try {
                    freedSlots.put(allocationId);
                    return CompletableFuture.completedFuture(Acknowledge.get());
                } catch (InterruptedException e) {
                    return FutureUtils.completedExceptionally(e);
                }
            });
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            final AllocationID expiredSlotID = new AllocationID();
            final AllocationID freshSlotID = new AllocationID();
            final SlotOffer slotToExpire = new SlotOffer(expiredSlotID, 0, ResourceProfile.UNKNOWN);
            final SlotOffer slotToNotExpire = new SlotOffer(freshSlotID, 1, ResourceProfile.UNKNOWN);
            MatcherAssert.assertThat(slotPool.registerTaskManager(taskManagerLocation.getResourceID()), Matchers.is(true));
            MatcherAssert.assertThat(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, slotToExpire), Matchers.is(true));
            clock.advanceTime(((timeout.toMilliseconds()) - 1L), TimeUnit.MILLISECONDS);
            MatcherAssert.assertThat(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, slotToNotExpire), Matchers.is(true));
            clock.advanceTime(1L, TimeUnit.MILLISECONDS);
            slotPool.triggerCheckIdleSlot();
            final AllocationID freedSlot = freedSlots.poll(timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            MatcherAssert.assertThat(freedSlot, Matchers.is(expiredSlotID));
            MatcherAssert.assertThat(freedSlots.isEmpty(), Matchers.is(true));
        }
    }

    /**
     * Tests that idle slots which cannot be released are only recycled if the owning {@link TaskExecutor}
     * is still registered at the {@link SlotPoolImpl}. See FLINK-9047.
     */
    @Test
    public void testReleasingIdleSlotFailed() throws Exception {
        final ManualClock clock = new ManualClock();
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId, clock, TestingUtils.infiniteTime(), timeout)) {
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            Scheduler scheduler = SlotPoolTest.setupScheduler(slotPool, mainThreadExecutor);
            final AllocationID expiredAllocationId = new AllocationID();
            final SlotOffer slotToExpire = new SlotOffer(expiredAllocationId, 0, ResourceProfile.UNKNOWN);
            final ArrayDeque<CompletableFuture<Acknowledge>> responseQueue = new ArrayDeque<>(2);
            taskManagerGateway.setFreeSlotFunction((AllocationID allocationId,Throwable cause) -> {
                if (responseQueue.isEmpty()) {
                    return CompletableFuture.completedFuture(Acknowledge.get());
                } else {
                    return responseQueue.pop();
                }
            });
            responseQueue.add(FutureUtils.completedExceptionally(new FlinkException("Test failure")));
            final CompletableFuture<Acknowledge> responseFuture = new CompletableFuture<>();
            responseQueue.add(responseFuture);
            MatcherAssert.assertThat(slotPool.registerTaskManager(taskManagerLocation.getResourceID()), Matchers.is(true));
            MatcherAssert.assertThat(slotPool.offerSlot(taskManagerLocation, taskManagerGateway, slotToExpire), Matchers.is(true));
            clock.advanceTime(timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            slotPool.triggerCheckIdleSlot();
            CompletableFuture<LogicalSlot> allocatedSlotFuture = allocateSlot(scheduler, new SlotRequestId());
            // wait until the slot has been fulfilled with the previously idling slot
            final LogicalSlot logicalSlot = allocatedSlotFuture.get();
            MatcherAssert.assertThat(logicalSlot.getAllocationId(), Matchers.is(expiredAllocationId));
            // return the slot
            scheduler.returnLogicalSlot(logicalSlot);
            // advance the time so that the returned slot is now idling
            clock.advanceTime(timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            slotPool.triggerCheckIdleSlot();
            // request a new slot after the idling slot has been released
            allocatedSlotFuture = allocateSlot(scheduler, new SlotRequestId());
            // release the TaskExecutor before we get a response from the slot releasing
            slotPool.releaseTaskManager(taskManagerLocation.getResourceID(), null);
            // let the slot releasing fail --> since the owning TaskExecutor is no longer registered
            // the slot should be discarded
            responseFuture.completeExceptionally(new FlinkException("Second test exception"));
            try {
                // since the slot must have been discarded, we cannot fulfill the slot request
                allocatedSlotFuture.get(10L, TimeUnit.MILLISECONDS);
                Assert.fail("Expected to fail with a timeout.");
            } catch (TimeoutException ignored) {
                // expected
            }
        }
    }

    /**
     * Tests that failed slots are freed on the {@link TaskExecutor}.
     */
    @Test
    public void testFreeFailedSlots() throws Exception {
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId)) {
            final int parallelism = 5;
            final ArrayBlockingQueue<AllocationID> allocationIds = new ArrayBlockingQueue<>(parallelism);
            resourceManagerGateway.setRequestSlotConsumer(( slotRequest) -> allocationIds.offer(slotRequest.getAllocationId()));
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            Scheduler scheduler = SlotPoolTest.setupScheduler(slotPool, mainThreadExecutor);
            final Map<SlotRequestId, CompletableFuture<LogicalSlot>> slotRequestFutures = new HashMap<>(parallelism);
            for (int i = 0; i < parallelism; i++) {
                final SlotRequestId slotRequestId = new SlotRequestId();
                slotRequestFutures.put(slotRequestId, allocateSlot(scheduler, slotRequestId));
            }
            final List<SlotOffer> slotOffers = new ArrayList<>(parallelism);
            for (int i = 0; i < parallelism; i++) {
                slotOffers.add(new SlotOffer(allocationIds.take(), i, ResourceProfile.UNKNOWN));
            }
            slotPool.registerTaskManager(taskManagerLocation.getResourceID());
            slotPool.offerSlots(taskManagerLocation, taskManagerGateway, slotOffers);
            // wait for the completion of both slot futures
            FutureUtils.waitForAll(slotRequestFutures.values()).get();
            final ArrayBlockingQueue<AllocationID> freedSlots = new ArrayBlockingQueue<>(1);
            taskManagerGateway.setFreeSlotFunction(( allocationID, throwable) -> {
                freedSlots.offer(allocationID);
                return CompletableFuture.completedFuture(Acknowledge.get());
            });
            final FlinkException failException = new FlinkException("Test fail exception");
            // fail allocations one by one
            for (int i = 0; i < (parallelism - 1); i++) {
                final SlotOffer slotOffer = slotOffers.get(i);
                Optional<ResourceID> emptyTaskExecutorFuture = slotPool.failAllocation(slotOffer.getAllocationId(), failException);
                MatcherAssert.assertThat(emptyTaskExecutorFuture.isPresent(), Matchers.is(false));
                MatcherAssert.assertThat(freedSlots.take(), Matchers.is(Matchers.equalTo(slotOffer.getAllocationId())));
            }
            final SlotOffer slotOffer = slotOffers.get((parallelism - 1));
            final Optional<ResourceID> emptyTaskExecutorFuture = slotPool.failAllocation(slotOffer.getAllocationId(), failException);
            MatcherAssert.assertThat(emptyTaskExecutorFuture.get(), Matchers.is(Matchers.equalTo(taskManagerLocation.getResourceID())));
            MatcherAssert.assertThat(freedSlots.take(), Matchers.is(Matchers.equalTo(slotOffer.getAllocationId())));
        }
    }

    /**
     * Tests that failing an allocation fails the pending slot request
     */
    @Test
    public void testFailingAllocationFailsPendingSlotRequests() throws Exception {
        try (SlotPoolImpl slotPool = new SlotPoolImpl(jobId)) {
            final CompletableFuture<AllocationID> allocationIdFuture = new CompletableFuture<>();
            resourceManagerGateway.setRequestSlotConsumer(( slotRequest) -> allocationIdFuture.complete(slotRequest.getAllocationId()));
            SlotPoolTest.setupSlotPool(slotPool, resourceManagerGateway, mainThreadExecutor);
            Scheduler scheduler = SlotPoolTest.setupScheduler(slotPool, mainThreadExecutor);
            final CompletableFuture<LogicalSlot> slotFuture = allocateSlot(scheduler, new SlotRequestId());
            final AllocationID allocationId = allocationIdFuture.get();
            MatcherAssert.assertThat(slotFuture.isDone(), Matchers.is(false));
            final FlinkException cause = new FlinkException("Fail pending slot request failure.");
            final Optional<ResourceID> responseFuture = slotPool.failAllocation(allocationId, cause);
            MatcherAssert.assertThat(responseFuture.isPresent(), Matchers.is(false));
            try {
                slotFuture.get();
                Assert.fail("Expected a slot allocation failure.");
            } catch (ExecutionException ee) {
                MatcherAssert.assertThat(ExceptionUtils.stripExecutionException(ee), Matchers.equalTo(cause));
            }
        }
    }
}

