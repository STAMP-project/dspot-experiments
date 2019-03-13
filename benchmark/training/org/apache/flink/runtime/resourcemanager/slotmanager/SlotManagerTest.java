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
package org.apache.flink.runtime.resourcemanager.slotmanager;


import TaskManagerSlot.State;
import TaskManagerSlot.State.ALLOCATED;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.runtime.clusterframework.types.AllocationID;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.clusterframework.types.ResourceProfile;
import org.apache.flink.runtime.clusterframework.types.SlotID;
import org.apache.flink.runtime.clusterframework.types.TaskManagerSlot;
import org.apache.flink.runtime.concurrent.Executors;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.concurrent.ScheduledExecutor;
import org.apache.flink.runtime.instance.InstanceID;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.resourcemanager.ResourceManagerId;
import org.apache.flink.runtime.resourcemanager.SlotRequest;
import org.apache.flink.runtime.resourcemanager.exceptions.ResourceManagerException;
import org.apache.flink.runtime.resourcemanager.registration.TaskExecutorConnection;
import org.apache.flink.runtime.taskexecutor.SlotReport;
import org.apache.flink.runtime.taskexecutor.SlotStatus;
import org.apache.flink.runtime.taskexecutor.TaskExecutorGateway;
import org.apache.flink.runtime.taskexecutor.TestingTaskExecutorGateway;
import org.apache.flink.runtime.taskexecutor.TestingTaskExecutorGatewayBuilder;
import org.apache.flink.runtime.taskexecutor.exceptions.SlotAllocationException;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for the {@link SlotManager}.
 */
public class SlotManagerTest extends TestLogger {
    /**
     * Tests that we can register task manager and their slots at the slot manager.
     */
    @Test
    public void testTaskManagerRegistration() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().build();
        final TaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().createTestingTaskExecutorGateway();
        final ResourceID resourceId = ResourceID.generate();
        final TaskExecutorConnection taskManagerConnection = new TaskExecutorConnection(resourceId, taskExecutorGateway);
        final SlotID slotId1 = new SlotID(resourceId, 0);
        final SlotID slotId2 = new SlotID(resourceId, 1);
        final ResourceProfile resourceProfile = new ResourceProfile(42.0, 1337);
        final SlotStatus slotStatus1 = new SlotStatus(slotId1, resourceProfile);
        final SlotStatus slotStatus2 = new SlotStatus(slotId2, resourceProfile);
        final SlotReport slotReport = new SlotReport(Arrays.asList(slotStatus1, slotStatus2));
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            slotManager.registerTaskManager(taskManagerConnection, slotReport);
            Assert.assertTrue("The number registered slots does not equal the expected number.", (2 == (slotManager.getNumberRegisteredSlots())));
            Assert.assertNotNull(slotManager.getSlot(slotId1));
            Assert.assertNotNull(slotManager.getSlot(slotId2));
        }
    }

    /**
     * Tests that un-registration of task managers will free and remove all registered slots.
     */
    @Test
    public void testTaskManagerUnregistration() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceActions resourceManagerActions = Mockito.mock(ResourceActions.class);
        final JobID jobId = new JobID();
        final TaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setRequestSlotFunction(( tuple5) -> {
            Assert.assertThat(tuple5.f4, Matchers.is(Matchers.equalTo(resourceManagerId)));
            return new CompletableFuture<>();
        }).createTestingTaskExecutorGateway();
        final ResourceID resourceId = ResourceID.generate();
        final TaskExecutorConnection taskManagerConnection = new TaskExecutorConnection(resourceId, taskExecutorGateway);
        final SlotID slotId1 = new SlotID(resourceId, 0);
        final SlotID slotId2 = new SlotID(resourceId, 1);
        final AllocationID allocationId1 = new AllocationID();
        final AllocationID allocationId2 = new AllocationID();
        final ResourceProfile resourceProfile = new ResourceProfile(42.0, 1337);
        final SlotStatus slotStatus1 = new SlotStatus(slotId1, resourceProfile, jobId, allocationId1);
        final SlotStatus slotStatus2 = new SlotStatus(slotId2, resourceProfile);
        final SlotReport slotReport = new SlotReport(Arrays.asList(slotStatus1, slotStatus2));
        final SlotRequest slotRequest = new SlotRequest(new JobID(), allocationId2, resourceProfile, "foobar");
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            slotManager.registerTaskManager(taskManagerConnection, slotReport);
            Assert.assertTrue("The number registered slots does not equal the expected number.", (2 == (slotManager.getNumberRegisteredSlots())));
            TaskManagerSlot slot1 = slotManager.getSlot(slotId1);
            TaskManagerSlot slot2 = slotManager.getSlot(slotId2);
            Assert.assertTrue(((slot1.getState()) == (State.ALLOCATED)));
            Assert.assertTrue(((slot2.getState()) == (State.FREE)));
            Assert.assertTrue(slotManager.registerSlotRequest(slotRequest));
            Assert.assertFalse(((slot2.getState()) == (State.FREE)));
            Assert.assertTrue(((slot2.getState()) == (State.PENDING)));
            PendingSlotRequest pendingSlotRequest = slotManager.getSlotRequest(allocationId2);
            Assert.assertTrue("The pending slot request should have been assigned to slot 2", pendingSlotRequest.isAssigned());
            slotManager.unregisterTaskManager(taskManagerConnection.getInstanceID());
            Assert.assertTrue((0 == (slotManager.getNumberRegisteredSlots())));
            Assert.assertFalse(pendingSlotRequest.isAssigned());
        }
    }

    /**
     * Tests that a slot request with no free slots will trigger the resource allocation.
     */
    @Test
    public void testSlotRequestWithoutFreeSlots() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceProfile resourceProfile = new ResourceProfile(42.0, 1337);
        final SlotRequest slotRequest = new SlotRequest(new JobID(), new AllocationID(), resourceProfile, "localhost");
        CompletableFuture<ResourceProfile> allocateResourceFuture = new CompletableFuture<>();
        ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().setAllocateResourceConsumer(allocateResourceFuture::complete).build();
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            slotManager.registerSlotRequest(slotRequest);
            Assert.assertThat(allocateResourceFuture.get(), Matchers.is(Matchers.equalTo(resourceProfile)));
        }
    }

    /**
     * Tests that the slot request fails if we cannot allocate more resources.
     */
    @Test
    public void testSlotRequestWithResourceAllocationFailure() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceProfile resourceProfile = new ResourceProfile(42.0, 1337);
        final SlotRequest slotRequest = new SlotRequest(new JobID(), new AllocationID(), resourceProfile, "localhost");
        ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().setAllocateResourceFunction(( value) -> {
            throw new ResourceManagerException("Test exception");
        }).build();
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            slotManager.registerSlotRequest(slotRequest);
            Assert.fail("The slot request should have failed with a ResourceManagerException.");
        } catch (ResourceManagerException e) {
            // expected exception
        }
    }

    /**
     * Tests that a slot request which can be fulfilled will trigger a slot allocation.
     */
    @Test
    public void testSlotRequestWithFreeSlot() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceID resourceID = ResourceID.generate();
        final JobID jobId = new JobID();
        final SlotID slotId = new SlotID(resourceID, 0);
        final String targetAddress = "localhost";
        final AllocationID allocationId = new AllocationID();
        final ResourceProfile resourceProfile = new ResourceProfile(42.0, 1337);
        final SlotRequest slotRequest = new SlotRequest(jobId, allocationId, resourceProfile, targetAddress);
        ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().build();
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            final CompletableFuture<Tuple5<SlotID, JobID, AllocationID, String, ResourceManagerId>> requestFuture = new CompletableFuture<>();
            // accept an incoming slot request
            final TaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setRequestSlotFunction(( tuple5) -> {
                requestFuture.complete(Tuple5.of(tuple5.f0, tuple5.f1, tuple5.f2, tuple5.f3, tuple5.f4));
                return CompletableFuture.completedFuture(Acknowledge.get());
            }).createTestingTaskExecutorGateway();
            final TaskExecutorConnection taskExecutorConnection = new TaskExecutorConnection(resourceID, taskExecutorGateway);
            final SlotStatus slotStatus = new SlotStatus(slotId, resourceProfile);
            final SlotReport slotReport = new SlotReport(slotStatus);
            slotManager.registerTaskManager(taskExecutorConnection, slotReport);
            Assert.assertTrue("The slot request should be accepted", slotManager.registerSlotRequest(slotRequest));
            Assert.assertThat(requestFuture.get(), Matchers.is(Matchers.equalTo(Tuple5.of(slotId, jobId, allocationId, targetAddress, resourceManagerId))));
            TaskManagerSlot slot = slotManager.getSlot(slotId);
            Assert.assertEquals("The slot has not been allocated to the expected allocation id.", allocationId, slot.getAllocationId());
        }
    }

    /**
     * Checks that un-registering a pending slot request will cancel it, removing it from all
     * assigned task manager slots and then remove it from the slot manager.
     */
    @Test
    public void testUnregisterPendingSlotRequest() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceActions resourceManagerActions = Mockito.mock(ResourceActions.class);
        final ResourceID resourceID = ResourceID.generate();
        final SlotID slotId = new SlotID(resourceID, 0);
        final AllocationID allocationId = new AllocationID();
        final TaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setRequestSlotFunction(( slotIDJobIDAllocationIDStringResourceManagerIdTuple5) -> new CompletableFuture<>()).createTestingTaskExecutorGateway();
        final ResourceProfile resourceProfile = new ResourceProfile(1.0, 1);
        final SlotStatus slotStatus = new SlotStatus(slotId, resourceProfile);
        final SlotReport slotReport = new SlotReport(slotStatus);
        final SlotRequest slotRequest = new SlotRequest(new JobID(), allocationId, resourceProfile, "foobar");
        final TaskExecutorConnection taskManagerConnection = new TaskExecutorConnection(resourceID, taskExecutorGateway);
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            slotManager.registerTaskManager(taskManagerConnection, slotReport);
            TaskManagerSlot slot = slotManager.getSlot(slotId);
            slotManager.registerSlotRequest(slotRequest);
            Assert.assertNotNull(slotManager.getSlotRequest(allocationId));
            Assert.assertTrue(((slot.getState()) == (State.PENDING)));
            slotManager.unregisterSlotRequest(allocationId);
            Assert.assertNull(slotManager.getSlotRequest(allocationId));
            slot = slotManager.getSlot(slotId);
            Assert.assertTrue(((slot.getState()) == (State.FREE)));
        }
    }

    /**
     * Tests that pending slot requests are tried to be fulfilled upon new slot registrations.
     */
    @Test
    public void testFulfillingPendingSlotRequest() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceID resourceID = ResourceID.generate();
        final JobID jobId = new JobID();
        final SlotID slotId = new SlotID(resourceID, 0);
        final String targetAddress = "localhost";
        final AllocationID allocationId = new AllocationID();
        final ResourceProfile resourceProfile = new ResourceProfile(42.0, 1337);
        final SlotRequest slotRequest = new SlotRequest(jobId, allocationId, resourceProfile, targetAddress);
        final AtomicInteger numberAllocateResourceCalls = new AtomicInteger(0);
        ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().setAllocateResourceConsumer(( ignored) -> numberAllocateResourceCalls.incrementAndGet()).build();
        final CompletableFuture<Tuple5<SlotID, JobID, AllocationID, String, ResourceManagerId>> requestFuture = new CompletableFuture<>();
        // accept an incoming slot request
        final TaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setRequestSlotFunction(( tuple5) -> {
            requestFuture.complete(Tuple5.of(tuple5.f0, tuple5.f1, tuple5.f2, tuple5.f3, tuple5.f4));
            return CompletableFuture.completedFuture(Acknowledge.get());
        }).createTestingTaskExecutorGateway();
        final TaskExecutorConnection taskExecutorConnection = new TaskExecutorConnection(resourceID, taskExecutorGateway);
        final SlotStatus slotStatus = new SlotStatus(slotId, resourceProfile);
        final SlotReport slotReport = new SlotReport(slotStatus);
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            Assert.assertTrue("The slot request should be accepted", slotManager.registerSlotRequest(slotRequest));
            Assert.assertThat(numberAllocateResourceCalls.get(), Matchers.is(1));
            slotManager.registerTaskManager(taskExecutorConnection, slotReport);
            Assert.assertThat(requestFuture.get(), Matchers.is(Matchers.equalTo(Tuple5.of(slotId, jobId, allocationId, targetAddress, resourceManagerId))));
            TaskManagerSlot slot = slotManager.getSlot(slotId);
            Assert.assertEquals("The slot has not been allocated to the expected allocation id.", allocationId, slot.getAllocationId());
        }
    }

    /**
     * Tests that freeing a slot will correctly reset the slot and mark it as a free slot.
     */
    @Test
    public void testFreeSlot() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceID resourceID = ResourceID.generate();
        final JobID jobId = new JobID();
        final SlotID slotId = new SlotID(resourceID, 0);
        final AllocationID allocationId = new AllocationID();
        final ResourceProfile resourceProfile = new ResourceProfile(42.0, 1337);
        ResourceActions resourceManagerActions = Mockito.mock(ResourceActions.class);
        // accept an incoming slot request
        final TaskExecutorGateway taskExecutorGateway = Mockito.mock(TaskExecutorGateway.class);
        final TaskExecutorConnection taskExecutorConnection = new TaskExecutorConnection(resourceID, taskExecutorGateway);
        final SlotStatus slotStatus = new SlotStatus(slotId, resourceProfile, jobId, allocationId);
        final SlotReport slotReport = new SlotReport(slotStatus);
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            slotManager.registerTaskManager(taskExecutorConnection, slotReport);
            TaskManagerSlot slot = slotManager.getSlot(slotId);
            Assert.assertEquals("The slot has not been allocated to the expected allocation id.", allocationId, slot.getAllocationId());
            // this should be ignored since the allocation id does not match
            slotManager.freeSlot(slotId, new AllocationID());
            Assert.assertTrue(((slot.getState()) == (State.ALLOCATED)));
            Assert.assertEquals("The slot has not been allocated to the expected allocation id.", allocationId, slot.getAllocationId());
            slotManager.freeSlot(slotId, allocationId);
            Assert.assertTrue(((slot.getState()) == (State.FREE)));
            Assert.assertNull(slot.getAllocationId());
        }
    }

    /**
     * Tests that a second pending slot request is detected as a duplicate if the allocation ids are
     * the same.
     */
    @Test
    public void testDuplicatePendingSlotRequest() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final AtomicInteger numberAllocateResourceFunctionCalls = new AtomicInteger(0);
        final ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().setAllocateResourceConsumer(( resourceProfile) -> numberAllocateResourceFunctionCalls.incrementAndGet()).build();
        final AllocationID allocationId = new AllocationID();
        final ResourceProfile resourceProfile1 = new ResourceProfile(1.0, 2);
        final ResourceProfile resourceProfile2 = new ResourceProfile(2.0, 1);
        final SlotRequest slotRequest1 = new SlotRequest(new JobID(), allocationId, resourceProfile1, "foobar");
        final SlotRequest slotRequest2 = new SlotRequest(new JobID(), allocationId, resourceProfile2, "barfoo");
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            Assert.assertTrue(slotManager.registerSlotRequest(slotRequest1));
            Assert.assertFalse(slotManager.registerSlotRequest(slotRequest2));
        }
        // check that we have only called the resource allocation only for the first slot request,
        // since the second request is a duplicate
        Assert.assertThat(numberAllocateResourceFunctionCalls.get(), Matchers.is(1));
    }

    /**
     * Tests that if we have received a slot report with some allocated slots, then we don't accept
     * slot requests with allocated allocation ids.
     */
    @Test
    public void testDuplicatePendingSlotRequestAfterSlotReport() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceActions resourceManagerActions = Mockito.mock(ResourceActions.class);
        final JobID jobId = new JobID();
        final AllocationID allocationId = new AllocationID();
        final ResourceProfile resourceProfile = new ResourceProfile(1.0, 1);
        final ResourceID resourceID = ResourceID.generate();
        final SlotID slotId = new SlotID(resourceID, 0);
        final TaskExecutorGateway taskExecutorGateway = Mockito.mock(TaskExecutorGateway.class);
        final TaskExecutorConnection taskManagerConnection = new TaskExecutorConnection(resourceID, taskExecutorGateway);
        final SlotStatus slotStatus = new SlotStatus(slotId, resourceProfile, jobId, allocationId);
        final SlotReport slotReport = new SlotReport(slotStatus);
        final SlotRequest slotRequest = new SlotRequest(jobId, allocationId, resourceProfile, "foobar");
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            slotManager.registerTaskManager(taskManagerConnection, slotReport);
            Assert.assertFalse(slotManager.registerSlotRequest(slotRequest));
        }
    }

    /**
     * Tests that duplicate slot requests (requests with an already registered allocation id) are
     * also detected after a pending slot request has been fulfilled but not yet freed.
     */
    @Test
    public void testDuplicatePendingSlotRequestAfterSuccessfulAllocation() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final AtomicInteger allocateResourceCalls = new AtomicInteger(0);
        final ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().setAllocateResourceConsumer(( resourceProfile) -> allocateResourceCalls.incrementAndGet()).build();
        final AllocationID allocationId = new AllocationID();
        final ResourceProfile resourceProfile1 = new ResourceProfile(1.0, 2);
        final ResourceProfile resourceProfile2 = new ResourceProfile(2.0, 1);
        final SlotRequest slotRequest1 = new SlotRequest(new JobID(), allocationId, resourceProfile1, "foobar");
        final SlotRequest slotRequest2 = new SlotRequest(new JobID(), allocationId, resourceProfile2, "barfoo");
        final TaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().createTestingTaskExecutorGateway();
        final ResourceID resourceID = ResourceID.generate();
        final TaskExecutorConnection taskManagerConnection = new TaskExecutorConnection(resourceID, taskExecutorGateway);
        final SlotID slotId = new SlotID(resourceID, 0);
        final SlotStatus slotStatus = new SlotStatus(slotId, resourceProfile1);
        final SlotReport slotReport = new SlotReport(slotStatus);
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            slotManager.registerTaskManager(taskManagerConnection, slotReport);
            Assert.assertTrue(slotManager.registerSlotRequest(slotRequest1));
            TaskManagerSlot slot = slotManager.getSlot(slotId);
            Assert.assertEquals("The slot has not been allocated to the expected allocation id.", allocationId, slot.getAllocationId());
            Assert.assertFalse(slotManager.registerSlotRequest(slotRequest2));
        }
        // check that we have only called the resource allocation only for the first slot request,
        // since the second request is a duplicate
        Assert.assertThat(allocateResourceCalls.get(), Matchers.is(0));
    }

    /**
     * Tests that an already registered allocation id can be reused after the initial slot request
     * has been freed.
     */
    @Test
    public void testAcceptingDuplicateSlotRequestAfterAllocationRelease() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final AtomicInteger allocateResourceCalls = new AtomicInteger(0);
        final ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().setAllocateResourceConsumer(( resourceProfile) -> allocateResourceCalls.incrementAndGet()).build();
        final AllocationID allocationId = new AllocationID();
        final ResourceProfile resourceProfile1 = new ResourceProfile(1.0, 2);
        final ResourceProfile resourceProfile2 = new ResourceProfile(2.0, 1);
        final SlotRequest slotRequest1 = new SlotRequest(new JobID(), allocationId, resourceProfile1, "foobar");
        final SlotRequest slotRequest2 = new SlotRequest(new JobID(), allocationId, resourceProfile2, "barfoo");
        final TaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().createTestingTaskExecutorGateway();
        final ResourceID resourceID = ResourceID.generate();
        final TaskExecutorConnection taskManagerConnection = new TaskExecutorConnection(resourceID, taskExecutorGateway);
        final SlotID slotId = new SlotID(resourceID, 0);
        final SlotStatus slotStatus = new SlotStatus(slotId, new ResourceProfile(2.0, 2));
        final SlotReport slotReport = new SlotReport(slotStatus);
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            slotManager.registerTaskManager(taskManagerConnection, slotReport);
            Assert.assertTrue(slotManager.registerSlotRequest(slotRequest1));
            TaskManagerSlot slot = slotManager.getSlot(slotId);
            Assert.assertEquals("The slot has not been allocated to the expected allocation id.", allocationId, slot.getAllocationId());
            slotManager.freeSlot(slotId, allocationId);
            // check that the slot has been freed
            Assert.assertTrue(((slot.getState()) == (State.FREE)));
            Assert.assertNull(slot.getAllocationId());
            Assert.assertTrue(slotManager.registerSlotRequest(slotRequest2));
            Assert.assertEquals("The slot has not been allocated to the expected allocation id.", allocationId, slot.getAllocationId());
        }
        // check that we have only called the resource allocation only for the first slot request,
        // since the second request is a duplicate
        Assert.assertThat(allocateResourceCalls.get(), Matchers.is(0));
    }

    /**
     * Tests that the slot manager ignores slot reports of unknown origin (not registered
     * task managers).
     */
    @Test
    public void testReceivingUnknownSlotReport() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceActions resourceManagerActions = Mockito.mock(ResourceActions.class);
        final InstanceID unknownInstanceID = new InstanceID();
        final SlotID unknownSlotId = new SlotID(ResourceID.generate(), 0);
        final ResourceProfile unknownResourceProfile = new ResourceProfile(1.0, 1);
        final SlotStatus unknownSlotStatus = new SlotStatus(unknownSlotId, unknownResourceProfile);
        final SlotReport unknownSlotReport = new SlotReport(unknownSlotStatus);
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            // check that we don't have any slots registered
            Assert.assertTrue((0 == (slotManager.getNumberRegisteredSlots())));
            // this should not update anything since the instance id is not known to the slot manager
            Assert.assertFalse(slotManager.reportSlotStatus(unknownInstanceID, unknownSlotReport));
            Assert.assertTrue((0 == (slotManager.getNumberRegisteredSlots())));
        }
    }

    /**
     * Tests that slots are updated with respect to the latest incoming slot report. This means that
     * slots for which a report was received are updated accordingly.
     */
    @Test
    public void testUpdateSlotReport() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().build();
        final JobID jobId = new JobID();
        final AllocationID allocationId = new AllocationID();
        final ResourceID resourceId = ResourceID.generate();
        final SlotID slotId1 = new SlotID(resourceId, 0);
        final SlotID slotId2 = new SlotID(resourceId, 1);
        final ResourceProfile resourceProfile = new ResourceProfile(1.0, 1);
        final SlotStatus slotStatus1 = new SlotStatus(slotId1, resourceProfile);
        final SlotStatus slotStatus2 = new SlotStatus(slotId2, resourceProfile);
        final SlotStatus newSlotStatus2 = new SlotStatus(slotId2, resourceProfile, jobId, allocationId);
        final SlotReport slotReport1 = new SlotReport(Arrays.asList(slotStatus1, slotStatus2));
        final SlotReport slotReport2 = new SlotReport(Arrays.asList(newSlotStatus2, slotStatus1));
        final TaskExecutorGateway taskExecutorGateway = Mockito.mock(TaskExecutorGateway.class);
        final TaskExecutorConnection taskManagerConnection = new TaskExecutorConnection(resourceId, taskExecutorGateway);
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            // check that we don't have any slots registered
            Assert.assertTrue((0 == (slotManager.getNumberRegisteredSlots())));
            slotManager.registerTaskManager(taskManagerConnection, slotReport1);
            TaskManagerSlot slot1 = slotManager.getSlot(slotId1);
            TaskManagerSlot slot2 = slotManager.getSlot(slotId2);
            Assert.assertTrue((2 == (slotManager.getNumberRegisteredSlots())));
            Assert.assertTrue(((slot1.getState()) == (State.FREE)));
            Assert.assertTrue(((slot2.getState()) == (State.FREE)));
            Assert.assertTrue(slotManager.reportSlotStatus(taskManagerConnection.getInstanceID(), slotReport2));
            Assert.assertTrue((2 == (slotManager.getNumberRegisteredSlots())));
            Assert.assertNotNull(slotManager.getSlot(slotId1));
            Assert.assertNotNull(slotManager.getSlot(slotId2));
            // slotId2 should have been allocated for allocationId
            Assert.assertEquals(allocationId, slotManager.getSlot(slotId2).getAllocationId());
        }
    }

    /**
     * Tests that idle task managers time out after the configured timeout. A timed out task manager
     * will be removed from the slot manager and the resource manager will be notified about the
     * timeout.
     */
    @Test
    public void testTaskManagerTimeout() throws Exception {
        final long tmTimeout = 10L;
        final CompletableFuture<InstanceID> releaseFuture = new CompletableFuture<>();
        final ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().setReleaseResourceConsumer(( instanceID, e) -> releaseFuture.complete(instanceID)).build();
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceID resourceID = ResourceID.generate();
        final TaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().createTestingTaskExecutorGateway();
        final TaskExecutorConnection taskManagerConnection = new TaskExecutorConnection(resourceID, taskExecutorGateway);
        final SlotID slotId = new SlotID(resourceID, 0);
        final ResourceProfile resourceProfile = new ResourceProfile(1.0, 1);
        final SlotStatus slotStatus = new SlotStatus(slotId, resourceProfile);
        final SlotReport slotReport = new SlotReport(slotStatus);
        final Executor mainThreadExecutor = TestingUtils.defaultExecutor();
        try (SlotManager slotManager = new SlotManager(TestingUtils.defaultScheduledExecutor(), TestingUtils.infiniteTime(), TestingUtils.infiniteTime(), Time.milliseconds(tmTimeout))) {
            slotManager.start(resourceManagerId, mainThreadExecutor, resourceManagerActions);
            mainThreadExecutor.execute(() -> slotManager.registerTaskManager(taskManagerConnection, slotReport));
            Assert.assertThat(releaseFuture.get(), Matchers.is(Matchers.equalTo(taskManagerConnection.getInstanceID())));
        }
    }

    /**
     * Tests that slot requests time out after the specified request timeout. If a slot request
     * times out, then the request is cancelled, removed from the slot manager and the resource
     * manager is notified about the failed allocation.
     */
    @Test
    public void testSlotRequestTimeout() throws Exception {
        final long allocationTimeout = 50L;
        final CompletableFuture<Tuple2<JobID, AllocationID>> failedAllocationFuture = new CompletableFuture<>();
        final ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().setNotifyAllocationFailureConsumer(( tuple3) -> failedAllocationFuture.complete(Tuple2.of(tuple3.f0, tuple3.f1))).build();
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final JobID jobId = new JobID();
        final AllocationID allocationId = new AllocationID();
        final ResourceProfile resourceProfile = new ResourceProfile(1.0, 1);
        final SlotRequest slotRequest = new SlotRequest(jobId, allocationId, resourceProfile, "foobar");
        final Executor mainThreadExecutor = TestingUtils.defaultExecutor();
        try (SlotManager slotManager = new SlotManager(TestingUtils.defaultScheduledExecutor(), TestingUtils.infiniteTime(), Time.milliseconds(allocationTimeout), TestingUtils.infiniteTime())) {
            slotManager.start(resourceManagerId, mainThreadExecutor, resourceManagerActions);
            final AtomicReference<Exception> atomicException = new AtomicReference<>(null);
            mainThreadExecutor.execute(() -> {
                try {
                    Assert.assertTrue(slotManager.registerSlotRequest(slotRequest));
                } catch (Exception e) {
                    atomicException.compareAndSet(null, e);
                }
            });
            Assert.assertThat(failedAllocationFuture.get(), Matchers.is(Matchers.equalTo(Tuple2.of(jobId, allocationId))));
            if ((atomicException.get()) != null) {
                throw atomicException.get();
            }
        }
    }

    /**
     * Tests that a slot request is retried if it times out on the task manager side.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testTaskManagerSlotRequestTimeoutHandling() throws Exception {
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceActions resourceManagerActions = Mockito.mock(ResourceActions.class);
        final JobID jobId = new JobID();
        final AllocationID allocationId = new AllocationID();
        final ResourceProfile resourceProfile = new ResourceProfile(42.0, 1337);
        final SlotRequest slotRequest = new SlotRequest(jobId, allocationId, resourceProfile, "foobar");
        final CompletableFuture<Acknowledge> slotRequestFuture1 = new CompletableFuture<>();
        final CompletableFuture<Acknowledge> slotRequestFuture2 = new CompletableFuture<>();
        final TaskExecutorGateway taskExecutorGateway = Mockito.mock(TaskExecutorGateway.class);
        Mockito.when(taskExecutorGateway.requestSlot(ArgumentMatchers.any(SlotID.class), ArgumentMatchers.any(JobID.class), ArgumentMatchers.eq(allocationId), ArgumentMatchers.anyString(), ArgumentMatchers.any(ResourceManagerId.class), ArgumentMatchers.any(Time.class))).thenReturn(slotRequestFuture1, slotRequestFuture2);
        final ResourceID resourceId = ResourceID.generate();
        final TaskExecutorConnection taskManagerConnection = new TaskExecutorConnection(resourceId, taskExecutorGateway);
        final SlotID slotId1 = new SlotID(resourceId, 0);
        final SlotID slotId2 = new SlotID(resourceId, 1);
        final SlotStatus slotStatus1 = new SlotStatus(slotId1, resourceProfile);
        final SlotStatus slotStatus2 = new SlotStatus(slotId2, resourceProfile);
        final SlotReport slotReport = new SlotReport(Arrays.asList(slotStatus1, slotStatus2));
        try (SlotManager slotManager = createSlotManager(resourceManagerId, resourceManagerActions)) {
            slotManager.registerTaskManager(taskManagerConnection, slotReport);
            slotManager.registerSlotRequest(slotRequest);
            ArgumentCaptor<SlotID> slotIdCaptor = ArgumentCaptor.forClass(SlotID.class);
            Mockito.verify(taskExecutorGateway, Mockito.times(1)).requestSlot(slotIdCaptor.capture(), ArgumentMatchers.eq(jobId), ArgumentMatchers.eq(allocationId), ArgumentMatchers.anyString(), ArgumentMatchers.eq(resourceManagerId), ArgumentMatchers.any(Time.class));
            TaskManagerSlot failedSlot = slotManager.getSlot(slotIdCaptor.getValue());
            // let the first attempt fail --> this should trigger a second attempt
            slotRequestFuture1.completeExceptionally(new SlotAllocationException("Test exception."));
            Mockito.verify(taskExecutorGateway, Mockito.times(2)).requestSlot(slotIdCaptor.capture(), ArgumentMatchers.eq(jobId), ArgumentMatchers.eq(allocationId), ArgumentMatchers.anyString(), ArgumentMatchers.eq(resourceManagerId), ArgumentMatchers.any(Time.class));
            // the second attempt succeeds
            slotRequestFuture2.complete(Acknowledge.get());
            TaskManagerSlot slot = slotManager.getSlot(slotIdCaptor.getValue());
            Assert.assertTrue(((slot.getState()) == (State.ALLOCATED)));
            Assert.assertEquals(allocationId, slot.getAllocationId());
            if (!(failedSlot.getSlotId().equals(slot.getSlotId()))) {
                Assert.assertTrue(((failedSlot.getState()) == (State.FREE)));
            }
        }
    }

    /**
     * Tests that pending slot requests are rejected if a slot report with a different allocation
     * is received.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testSlotReportWhileActiveSlotRequest() throws Exception {
        final long verifyTimeout = 10000L;
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().build();
        final JobID jobId = new JobID();
        final AllocationID allocationId = new AllocationID();
        final ResourceProfile resourceProfile = new ResourceProfile(42.0, 1337);
        final SlotRequest slotRequest = new SlotRequest(jobId, allocationId, resourceProfile, "foobar");
        final CompletableFuture<Acknowledge> slotRequestFuture1 = new CompletableFuture<>();
        final TaskExecutorGateway taskExecutorGateway = Mockito.mock(TaskExecutorGateway.class);
        Mockito.when(taskExecutorGateway.requestSlot(ArgumentMatchers.any(SlotID.class), ArgumentMatchers.any(JobID.class), ArgumentMatchers.eq(allocationId), ArgumentMatchers.anyString(), ArgumentMatchers.any(ResourceManagerId.class), ArgumentMatchers.any(Time.class))).thenReturn(slotRequestFuture1, CompletableFuture.completedFuture(Acknowledge.get()));
        final ResourceID resourceId = ResourceID.generate();
        final TaskExecutorConnection taskManagerConnection = new TaskExecutorConnection(resourceId, taskExecutorGateway);
        final SlotID slotId1 = new SlotID(resourceId, 0);
        final SlotID slotId2 = new SlotID(resourceId, 1);
        final SlotStatus slotStatus1 = new SlotStatus(slotId1, resourceProfile);
        final SlotStatus slotStatus2 = new SlotStatus(slotId2, resourceProfile);
        final SlotReport slotReport = new SlotReport(Arrays.asList(slotStatus1, slotStatus2));
        final Executor mainThreadExecutor = TestingUtils.defaultExecutor();
        try (final SlotManager slotManager = new SlotManager(TestingUtils.defaultScheduledExecutor(), TestingUtils.infiniteTime(), TestingUtils.infiniteTime(), TestingUtils.infiniteTime())) {
            slotManager.start(resourceManagerId, mainThreadExecutor, resourceManagerActions);
            CompletableFuture<Void> registrationFuture = CompletableFuture.supplyAsync(() -> {
                slotManager.registerTaskManager(taskManagerConnection, slotReport);
                return null;
            }, mainThreadExecutor).thenAccept((Object value) -> {
                try {
                    slotManager.registerSlotRequest(slotRequest);
                } catch (SlotManagerException e) {
                    throw new RuntimeException("Could not register slots.", e);
                }
            });
            // check that no exception has been thrown
            registrationFuture.get();
            ArgumentCaptor<SlotID> slotIdCaptor = ArgumentCaptor.forClass(SlotID.class);
            Mockito.verify(taskExecutorGateway, Mockito.times(1)).requestSlot(slotIdCaptor.capture(), ArgumentMatchers.eq(jobId), ArgumentMatchers.eq(allocationId), ArgumentMatchers.anyString(), ArgumentMatchers.eq(resourceManagerId), ArgumentMatchers.any(Time.class));
            final SlotID requestedSlotId = slotIdCaptor.getValue();
            final SlotID freeSlotId = (requestedSlotId.equals(slotId1)) ? slotId2 : slotId1;
            CompletableFuture<Boolean> freeSlotFuture = CompletableFuture.supplyAsync(() -> (slotManager.getSlot(freeSlotId).getState()) == (State.FREE), mainThreadExecutor);
            Assert.assertTrue(freeSlotFuture.get());
            final SlotStatus newSlotStatus1 = new SlotStatus(slotIdCaptor.getValue(), resourceProfile, new JobID(), new AllocationID());
            final SlotStatus newSlotStatus2 = new SlotStatus(freeSlotId, resourceProfile);
            final SlotReport newSlotReport = new SlotReport(Arrays.asList(newSlotStatus1, newSlotStatus2));
            CompletableFuture<Boolean> reportSlotStatusFuture = // this should update the slot with the pending slot request triggering the reassignment of it
            CompletableFuture.supplyAsync(() -> slotManager.reportSlotStatus(taskManagerConnection.getInstanceID(), newSlotReport), mainThreadExecutor);
            Assert.assertTrue(reportSlotStatusFuture.get());
            Mockito.verify(taskExecutorGateway, Mockito.timeout(verifyTimeout).times(2)).requestSlot(slotIdCaptor.capture(), ArgumentMatchers.eq(jobId), ArgumentMatchers.eq(allocationId), ArgumentMatchers.anyString(), ArgumentMatchers.eq(resourceManagerId), ArgumentMatchers.any(Time.class));
            final SlotID requestedSlotId2 = slotIdCaptor.getValue();
            Assert.assertEquals(slotId2, requestedSlotId2);
            CompletableFuture<TaskManagerSlot> requestedSlotFuture = CompletableFuture.supplyAsync(() -> slotManager.getSlot(requestedSlotId2), mainThreadExecutor);
            TaskManagerSlot slot = requestedSlotFuture.get();
            Assert.assertTrue(((slot.getState()) == (State.ALLOCATED)));
            Assert.assertEquals(allocationId, slot.getAllocationId());
        }
    }

    /**
     * Tests that formerly used task managers can again timeout after all of their slots have
     * been freed.
     */
    @Test
    public void testTimeoutForUnusedTaskManager() throws Exception {
        final long taskManagerTimeout = 50L;
        final CompletableFuture<InstanceID> releasedResourceFuture = new CompletableFuture<>();
        final ResourceActions resourceManagerActions = new TestingResourceActionsBuilder().setReleaseResourceConsumer(( instanceID, e) -> releasedResourceFuture.complete(instanceID)).build();
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ScheduledExecutor scheduledExecutor = TestingUtils.defaultScheduledExecutor();
        final ResourceID resourceId = ResourceID.generate();
        final JobID jobId = new JobID();
        final AllocationID allocationId = new AllocationID();
        final ResourceProfile resourceProfile = new ResourceProfile(1.0, 1);
        final SlotRequest slotRequest = new SlotRequest(jobId, allocationId, resourceProfile, "foobar");
        final CompletableFuture<SlotID> requestedSlotFuture = new CompletableFuture<>();
        final TaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setRequestSlotFunction(( tuple5) -> {
            requestedSlotFuture.complete(tuple5.f0);
            return CompletableFuture.completedFuture(Acknowledge.get());
        }).createTestingTaskExecutorGateway();
        final TaskExecutorConnection taskManagerConnection = new TaskExecutorConnection(resourceId, taskExecutorGateway);
        final SlotID slotId1 = new SlotID(resourceId, 0);
        final SlotID slotId2 = new SlotID(resourceId, 1);
        final SlotStatus slotStatus1 = new SlotStatus(slotId1, resourceProfile);
        final SlotStatus slotStatus2 = new SlotStatus(slotId2, resourceProfile);
        final SlotReport initialSlotReport = new SlotReport(Arrays.asList(slotStatus1, slotStatus2));
        final Executor mainThreadExecutor = TestingUtils.defaultExecutor();
        try (final SlotManager slotManager = new SlotManager(scheduledExecutor, TestingUtils.infiniteTime(), TestingUtils.infiniteTime(), Time.of(taskManagerTimeout, TimeUnit.MILLISECONDS))) {
            slotManager.start(resourceManagerId, mainThreadExecutor, resourceManagerActions);
            CompletableFuture.supplyAsync(() -> {
                try {
                    return slotManager.registerSlotRequest(slotRequest);
                } catch (SlotManagerException e) {
                    throw new CompletionException(e);
                }
            }, mainThreadExecutor).thenRun(() -> slotManager.registerTaskManager(taskManagerConnection, initialSlotReport));
            final SlotID slotId = requestedSlotFuture.get();
            CompletableFuture<Boolean> idleFuture = CompletableFuture.supplyAsync(() -> slotManager.isTaskManagerIdle(taskManagerConnection.getInstanceID()), mainThreadExecutor);
            // check that the TaskManager is not idle
            Assert.assertFalse(idleFuture.get());
            CompletableFuture<TaskManagerSlot> slotFuture = CompletableFuture.supplyAsync(() -> slotManager.getSlot(slotId), mainThreadExecutor);
            TaskManagerSlot slot = slotFuture.get();
            Assert.assertTrue(((slot.getState()) == (State.ALLOCATED)));
            Assert.assertEquals(allocationId, slot.getAllocationId());
            CompletableFuture<Boolean> idleFuture2 = CompletableFuture.runAsync(() -> slotManager.freeSlot(slotId, allocationId), mainThreadExecutor).thenApply((Object value) -> slotManager.isTaskManagerIdle(taskManagerConnection.getInstanceID()));
            Assert.assertTrue(idleFuture2.get());
            Assert.assertThat(releasedResourceFuture.get(), Matchers.is(Matchers.equalTo(taskManagerConnection.getInstanceID())));
        }
    }

    /**
     * Tests that a task manager timeout does not remove the slots from the SlotManager.
     * A timeout should only trigger the {@link ResourceActions#releaseResource(InstanceID, Exception)}
     * callback. The receiver of the callback can then decide what to do with the TaskManager.
     *
     * <p>See FLINK-7793
     */
    @Test
    public void testTaskManagerTimeoutDoesNotRemoveSlots() throws Exception {
        final Time taskManagerTimeout = Time.milliseconds(10L);
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceID resourceID = ResourceID.generate();
        final ResourceActions resourceActions = Mockito.mock(ResourceActions.class);
        final TaskExecutorGateway taskExecutorGateway = Mockito.mock(TaskExecutorGateway.class);
        final TaskExecutorConnection taskExecutorConnection = new TaskExecutorConnection(resourceID, taskExecutorGateway);
        final SlotStatus slotStatus = new SlotStatus(new SlotID(resourceID, 0), new ResourceProfile(1.0, 1));
        final SlotReport initialSlotReport = new SlotReport(slotStatus);
        try (final SlotManager slotManager = new SlotManager(TestingUtils.defaultScheduledExecutor(), TestingUtils.infiniteTime(), TestingUtils.infiniteTime(), taskManagerTimeout)) {
            slotManager.start(resourceManagerId, Executors.directExecutor(), resourceActions);
            slotManager.registerTaskManager(taskExecutorConnection, initialSlotReport);
            Assert.assertEquals(1, slotManager.getNumberRegisteredSlots());
            // wait for the timeout call to happen
            Mockito.verify(resourceActions, Mockito.timeout(((taskManagerTimeout.toMilliseconds()) * 20L)).atLeast(1)).releaseResource(ArgumentMatchers.eq(taskExecutorConnection.getInstanceID()), ArgumentMatchers.any(Exception.class));
            Assert.assertEquals(1, slotManager.getNumberRegisteredSlots());
            slotManager.unregisterTaskManager(taskExecutorConnection.getInstanceID());
            Assert.assertEquals(0, slotManager.getNumberRegisteredSlots());
        }
    }

    /**
     * Tests that free slots which are reported as allocated won't be considered for fulfilling
     * other pending slot requests.
     *
     * <p>See: FLINK-8505
     */
    @Test
    public void testReportAllocatedSlot() throws Exception {
        final ResourceID taskManagerId = ResourceID.generate();
        final ResourceActions resourceActions = new TestingResourceActionsBuilder().build();
        final TestingTaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().createTestingTaskExecutorGateway();
        final TaskExecutorConnection taskExecutorConnection = new TaskExecutorConnection(taskManagerId, taskExecutorGateway);
        try (final SlotManager slotManager = new SlotManager(TestingUtils.defaultScheduledExecutor(), TestingUtils.infiniteTime(), TestingUtils.infiniteTime(), TestingUtils.infiniteTime())) {
            slotManager.start(ResourceManagerId.generate(), Executors.directExecutor(), resourceActions);
            // initially report a single slot as free
            final SlotID slotId = new SlotID(taskManagerId, 0);
            final SlotStatus initialSlotStatus = new SlotStatus(slotId, ResourceProfile.UNKNOWN);
            final SlotReport initialSlotReport = new SlotReport(initialSlotStatus);
            slotManager.registerTaskManager(taskExecutorConnection, initialSlotReport);
            Assert.assertThat(slotManager.getNumberRegisteredSlots(), Matchers.is(Matchers.equalTo(1)));
            // Now report this slot as allocated
            final SlotStatus slotStatus = new SlotStatus(slotId, ResourceProfile.UNKNOWN, new JobID(), new AllocationID());
            final SlotReport slotReport = new SlotReport(slotStatus);
            slotManager.reportSlotStatus(taskExecutorConnection.getInstanceID(), slotReport);
            // this slot request should not be fulfilled
            final AllocationID allocationId = new AllocationID();
            final SlotRequest slotRequest = new SlotRequest(new JobID(), allocationId, ResourceProfile.UNKNOWN, "foobar");
            // This triggered an IllegalStateException before
            slotManager.registerSlotRequest(slotRequest);
            Assert.assertThat(slotManager.getSlotRequest(allocationId).isAssigned(), Matchers.is(false));
        }
    }

    /**
     * Tests that the SlotManager retries allocating a slot if the TaskExecutor#requestSlot call
     * fails.
     */
    @Test
    public void testSlotRequestFailure() throws Exception {
        try (final SlotManager slotManager = createSlotManager(ResourceManagerId.generate(), new TestingResourceActionsBuilder().build())) {
            final SlotRequest slotRequest = new SlotRequest(new JobID(), new AllocationID(), ResourceProfile.UNKNOWN, "foobar");
            slotManager.registerSlotRequest(slotRequest);
            final BlockingQueue<Tuple5<SlotID, JobID, AllocationID, String, ResourceManagerId>> requestSlotQueue = new ArrayBlockingQueue<>(1);
            final BlockingQueue<CompletableFuture<Acknowledge>> responseQueue = new ArrayBlockingQueue<>(1);
            final TestingTaskExecutorGateway testingTaskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setRequestSlotFunction(( slotIDJobIDAllocationIDStringResourceManagerIdTuple5) -> {
                requestSlotQueue.offer(slotIDJobIDAllocationIDStringResourceManagerIdTuple5);
                try {
                    return responseQueue.take();
                } catch (InterruptedException ignored) {
                    return FutureUtils.completedExceptionally(new FlinkException("Response queue was interrupted."));
                }
            }).createTestingTaskExecutorGateway();
            final ResourceID taskExecutorResourceId = ResourceID.generate();
            final TaskExecutorConnection taskExecutionConnection = new TaskExecutorConnection(taskExecutorResourceId, testingTaskExecutorGateway);
            final SlotReport slotReport = new SlotReport(new SlotStatus(new SlotID(taskExecutorResourceId, 0), ResourceProfile.UNKNOWN));
            final CompletableFuture<Acknowledge> firstManualSlotRequestResponse = new CompletableFuture<>();
            responseQueue.offer(firstManualSlotRequestResponse);
            slotManager.registerTaskManager(taskExecutionConnection, slotReport);
            final Tuple5<SlotID, JobID, AllocationID, String, ResourceManagerId> firstRequest = requestSlotQueue.take();
            final CompletableFuture<Acknowledge> secondManualSlotRequestResponse = new CompletableFuture<>();
            responseQueue.offer(secondManualSlotRequestResponse);
            // fail first request
            firstManualSlotRequestResponse.completeExceptionally(new SlotAllocationException("Test exception"));
            final Tuple5<SlotID, JobID, AllocationID, String, ResourceManagerId> secondRequest = requestSlotQueue.take();
            Assert.assertThat(secondRequest.f2, Matchers.equalTo(firstRequest.f2));
            Assert.assertThat(secondRequest.f0, Matchers.equalTo(firstRequest.f0));
            secondManualSlotRequestResponse.complete(Acknowledge.get());
            final TaskManagerSlot slot = slotManager.getSlot(secondRequest.f0);
            Assert.assertThat(slot.getState(), Matchers.equalTo(ALLOCATED));
            Assert.assertThat(slot.getAllocationId(), Matchers.equalTo(secondRequest.f2));
        }
    }

    /**
     * Tests that pending request is removed if task executor reports a slot with its allocation id.
     */
    @Test
    public void testSlotRequestRemovedIfTMReportAllocation() throws Exception {
        try (final SlotManager slotManager = createSlotManager(ResourceManagerId.generate(), new TestingResourceActionsBuilder().build())) {
            final JobID jobID = new JobID();
            final SlotRequest slotRequest1 = new SlotRequest(jobID, new AllocationID(), ResourceProfile.UNKNOWN, "foobar");
            slotManager.registerSlotRequest(slotRequest1);
            final BlockingQueue<Tuple5<SlotID, JobID, AllocationID, String, ResourceManagerId>> requestSlotQueue = new ArrayBlockingQueue<>(1);
            final BlockingQueue<CompletableFuture<Acknowledge>> responseQueue = new ArrayBlockingQueue<>(1);
            final TestingTaskExecutorGateway testingTaskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setRequestSlotFunction(( slotIDJobIDAllocationIDStringResourceManagerIdTuple5) -> {
                requestSlotQueue.offer(slotIDJobIDAllocationIDStringResourceManagerIdTuple5);
                try {
                    return responseQueue.take();
                } catch (InterruptedException ignored) {
                    return FutureUtils.completedExceptionally(new FlinkException("Response queue was interrupted."));
                }
            }).createTestingTaskExecutorGateway();
            final ResourceID taskExecutorResourceId = ResourceID.generate();
            final TaskExecutorConnection taskExecutionConnection = new TaskExecutorConnection(taskExecutorResourceId, testingTaskExecutorGateway);
            final SlotReport slotReport = new SlotReport(new SlotStatus(new SlotID(taskExecutorResourceId, 0), ResourceProfile.UNKNOWN));
            final CompletableFuture<Acknowledge> firstManualSlotRequestResponse = new CompletableFuture<>();
            responseQueue.offer(firstManualSlotRequestResponse);
            slotManager.registerTaskManager(taskExecutionConnection, slotReport);
            final Tuple5<SlotID, JobID, AllocationID, String, ResourceManagerId> firstRequest = requestSlotQueue.take();
            final CompletableFuture<Acknowledge> secondManualSlotRequestResponse = new CompletableFuture<>();
            responseQueue.offer(secondManualSlotRequestResponse);
            final SlotRequest slotRequest2 = new SlotRequest(jobID, new AllocationID(), ResourceProfile.UNKNOWN, "foobar");
            slotManager.registerSlotRequest(slotRequest2);
            // fail first request
            firstManualSlotRequestResponse.completeExceptionally(new TimeoutException("Test exception to fail first allocation"));
            final Tuple5<SlotID, JobID, AllocationID, String, ResourceManagerId> secondRequest = requestSlotQueue.take();
            // fail second request
            secondManualSlotRequestResponse.completeExceptionally(new org.apache.flink.runtime.taskexecutor.exceptions.SlotOccupiedException("Test exception", slotRequest1.getAllocationId(), jobID));
            Assert.assertThat(firstRequest.f2, Matchers.equalTo(slotRequest1.getAllocationId()));
            Assert.assertThat(secondRequest.f2, Matchers.equalTo(slotRequest2.getAllocationId()));
            Assert.assertThat(secondRequest.f0, Matchers.equalTo(firstRequest.f0));
            secondManualSlotRequestResponse.complete(Acknowledge.get());
            final TaskManagerSlot slot = slotManager.getSlot(secondRequest.f0);
            Assert.assertThat(slot.getState(), Matchers.equalTo(ALLOCATED));
            Assert.assertThat(slot.getAllocationId(), Matchers.equalTo(firstRequest.f2));
            Assert.assertThat(slotManager.getNumberRegisteredSlots(), Matchers.is(1));
        }
    }

    /**
     * Tests notify the job manager of the allocations when the task manager is failed/killed.
     */
    @Test
    public void testNotifyFailedAllocationWhenTaskManagerTerminated() throws Exception {
        final Queue<Tuple2<JobID, AllocationID>> allocationFailures = new ArrayDeque<>(5);
        final TestingResourceActions resourceManagerActions = new TestingResourceActionsBuilder().setNotifyAllocationFailureConsumer((Tuple3<JobID, AllocationID, Exception> failureMessage) -> allocationFailures.offer(Tuple2.of(failureMessage.f0, failureMessage.f1))).build();
        try (final SlotManager slotManager = createSlotManager(ResourceManagerId.generate(), resourceManagerActions)) {
            // register slot request for job1.
            JobID jobId1 = new JobID();
            final SlotRequest slotRequest11 = createSlotRequest(jobId1);
            final SlotRequest slotRequest12 = createSlotRequest(jobId1);
            slotManager.registerSlotRequest(slotRequest11);
            slotManager.registerSlotRequest(slotRequest12);
            // create task-manager-1 with 2 slots.
            final ResourceID taskExecutorResourceId1 = ResourceID.generate();
            final TestingTaskExecutorGateway testingTaskExecutorGateway1 = new TestingTaskExecutorGatewayBuilder().createTestingTaskExecutorGateway();
            final TaskExecutorConnection taskExecutionConnection1 = new TaskExecutorConnection(taskExecutorResourceId1, testingTaskExecutorGateway1);
            final SlotReport slotReport1 = createSlotReport(taskExecutorResourceId1, 2);
            // register the task-manager-1 to the slot manager, this will trigger the slot allocation for job1.
            slotManager.registerTaskManager(taskExecutionConnection1, slotReport1);
            // register slot request for job2.
            JobID jobId2 = new JobID();
            final SlotRequest slotRequest21 = createSlotRequest(jobId2);
            final SlotRequest slotRequest22 = createSlotRequest(jobId2);
            slotManager.registerSlotRequest(slotRequest21);
            slotManager.registerSlotRequest(slotRequest22);
            // register slot request for job3.
            JobID jobId3 = new JobID();
            final SlotRequest slotRequest31 = createSlotRequest(jobId3);
            slotManager.registerSlotRequest(slotRequest31);
            // create task-manager-2 with 3 slots.
            final ResourceID taskExecutorResourceId2 = ResourceID.generate();
            final TestingTaskExecutorGateway testingTaskExecutorGateway2 = new TestingTaskExecutorGatewayBuilder().createTestingTaskExecutorGateway();
            final TaskExecutorConnection taskExecutionConnection2 = new TaskExecutorConnection(taskExecutorResourceId2, testingTaskExecutorGateway2);
            final SlotReport slotReport2 = createSlotReport(taskExecutorResourceId2, 3);
            // register the task-manager-2 to the slot manager, this will trigger the slot allocation for job2 and job3.
            slotManager.registerTaskManager(taskExecutionConnection2, slotReport2);
            // validate for job1.
            slotManager.unregisterTaskManager(taskExecutionConnection1.getInstanceID());
            Assert.assertThat(allocationFailures, Matchers.hasSize(2));
            Tuple2<JobID, AllocationID> allocationFailure;
            final Set<AllocationID> failedAllocations = new HashSet<>(2);
            while ((allocationFailure = allocationFailures.poll()) != null) {
                Assert.assertThat(allocationFailure.f0, Matchers.equalTo(jobId1));
                failedAllocations.add(allocationFailure.f1);
            } 
            Assert.assertThat(failedAllocations, Matchers.containsInAnyOrder(slotRequest11.getAllocationId(), slotRequest12.getAllocationId()));
            // validate the result for job2 and job3.
            slotManager.unregisterTaskManager(taskExecutionConnection2.getInstanceID());
            Assert.assertThat(allocationFailures, Matchers.hasSize(3));
            Map<JobID, List<Tuple2<JobID, AllocationID>>> job2AndJob3FailedAllocationInfo = allocationFailures.stream().collect(Collectors.groupingBy(( tuple) -> tuple.f0));
            Assert.assertThat(job2AndJob3FailedAllocationInfo.entrySet(), Matchers.hasSize(2));
            final Set<AllocationID> job2FailedAllocations = extractFailedAllocationsForJob(jobId2, job2AndJob3FailedAllocationInfo);
            final Set<AllocationID> job3FailedAllocations = extractFailedAllocationsForJob(jobId3, job2AndJob3FailedAllocationInfo);
            Assert.assertThat(job2FailedAllocations, Matchers.containsInAnyOrder(slotRequest21.getAllocationId(), slotRequest22.getAllocationId()));
            Assert.assertThat(job3FailedAllocations, Matchers.containsInAnyOrder(slotRequest31.getAllocationId()));
        }
    }

    /**
     * Tests that we only request new resources/containers once we have assigned
     * all pending task manager slots.
     */
    @Test
    public void testRequestNewResources() throws Exception {
        final int numberSlots = 2;
        final AtomicInteger resourceRequests = new AtomicInteger(0);
        final TestingResourceActions testingResourceActions = new TestingResourceActionsBuilder().setAllocateResourceFunction(SlotManagerTest.convert(( ignored) -> {
            resourceRequests.incrementAndGet();
            return numberSlots;
        })).build();
        try (final SlotManager slotManager = createSlotManager(ResourceManagerId.generate(), testingResourceActions)) {
            final JobID jobId = new JobID();
            Assert.assertThat(slotManager.registerSlotRequest(createSlotRequest(jobId)), Matchers.is(true));
            Assert.assertThat(resourceRequests.get(), Matchers.is(1));
            // the second slot request should not try to allocate a new resource because the
            // previous resource was started with 2 slots.
            Assert.assertThat(slotManager.registerSlotRequest(createSlotRequest(jobId)), Matchers.is(true));
            Assert.assertThat(resourceRequests.get(), Matchers.is(1));
            Assert.assertThat(slotManager.getNumberAssignedPendingTaskManagerSlots(), Matchers.is(2));
            Assert.assertThat(slotManager.registerSlotRequest(createSlotRequest(jobId)), Matchers.is(true));
            Assert.assertThat(resourceRequests.get(), Matchers.is(2));
        }
    }

    /**
     * Tests that a failing allocation/slot request will return the pending task manager slot.
     */
    @Test
    public void testFailingAllocationReturnsPendingTaskManagerSlot() throws Exception {
        final int numberSlots = 2;
        final TestingResourceActions resourceActions = new TestingResourceActionsBuilder().setAllocateResourceFunction(SlotManagerTest.convert(( value) -> numberSlots)).build();
        try (final SlotManager slotManager = createSlotManager(ResourceManagerId.generate(), resourceActions)) {
            final JobID jobId = new JobID();
            final SlotRequest slotRequest = createSlotRequest(jobId);
            Assert.assertThat(slotManager.registerSlotRequest(slotRequest), Matchers.is(true));
            Assert.assertThat(slotManager.getNumberPendingTaskManagerSlots(), Matchers.is(numberSlots));
            Assert.assertThat(slotManager.getNumberAssignedPendingTaskManagerSlots(), Matchers.is(1));
            slotManager.unregisterSlotRequest(slotRequest.getAllocationId());
            Assert.assertThat(slotManager.getNumberPendingTaskManagerSlots(), Matchers.is(numberSlots));
            Assert.assertThat(slotManager.getNumberAssignedPendingTaskManagerSlots(), Matchers.is(0));
        }
    }

    /**
     * Tests the completion of pending task manager slots by registering a TaskExecutor.
     */
    @Test
    public void testPendingTaskManagerSlotCompletion() throws Exception {
        final int numberSlots = 3;
        final TestingResourceActions resourceActions = new TestingResourceActionsBuilder().setAllocateResourceFunction(SlotManagerTest.convert(( value) -> numberSlots)).build();
        try (final SlotManager slotManager = createSlotManager(ResourceManagerId.generate(), resourceActions)) {
            final JobID jobId = new JobID();
            Assert.assertThat(slotManager.registerSlotRequest(createSlotRequest(jobId)), Matchers.is(true));
            Assert.assertThat(slotManager.getNumberPendingTaskManagerSlots(), Matchers.is(numberSlots));
            Assert.assertThat(slotManager.getNumberAssignedPendingTaskManagerSlots(), Matchers.is(1));
            Assert.assertThat(slotManager.getNumberRegisteredSlots(), Matchers.is(0));
            final TaskExecutorConnection taskExecutorConnection = createTaskExecutorConnection();
            final SlotReport slotReport = createSlotReport(taskExecutorConnection.getResourceID(), (numberSlots - 1));
            slotManager.registerTaskManager(taskExecutorConnection, slotReport);
            Assert.assertThat(slotManager.getNumberRegisteredSlots(), Matchers.is((numberSlots - 1)));
            Assert.assertThat(slotManager.getNumberPendingTaskManagerSlots(), Matchers.is(1));
        }
    }

    /**
     * Tests that a different slot can fulfill a pending slot request. If the
     * pending slot request has a pending task manager slot assigned, it should
     * be freed.
     */
    @Test
    public void testRegistrationOfDifferentSlot() throws Exception {
        final int numberSlots = 1;
        final TestingResourceActions resourceActions = new TestingResourceActionsBuilder().setAllocateResourceFunction(SlotManagerTest.convert(( value) -> numberSlots)).build();
        try (final SlotManager slotManager = createSlotManager(ResourceManagerId.generate(), resourceActions)) {
            final JobID jobId = new JobID();
            final ResourceProfile requestedSlotProfile = new ResourceProfile(1.0, 1);
            Assert.assertThat(slotManager.registerSlotRequest(createSlotRequest(jobId, requestedSlotProfile)), Matchers.is(true));
            Assert.assertThat(slotManager.getNumberPendingTaskManagerSlots(), Matchers.is(numberSlots));
            final int numberOfferedSlots = 1;
            final TaskExecutorConnection taskExecutorConnection = createTaskExecutorConnection();
            final ResourceProfile offeredSlotProfile = new ResourceProfile(2.0, 2);
            final SlotReport slotReport = createSlotReport(taskExecutorConnection.getResourceID(), numberOfferedSlots, offeredSlotProfile);
            slotManager.registerTaskManager(taskExecutorConnection, slotReport);
            Assert.assertThat(slotManager.getNumberRegisteredSlots(), Matchers.is(numberOfferedSlots));
            Assert.assertThat(slotManager.getNumberPendingTaskManagerSlots(), Matchers.is(numberSlots));
            Assert.assertThat(slotManager.getNumberAssignedPendingTaskManagerSlots(), Matchers.is(0));
        }
    }

    /**
     * Tests that only free slots can fulfill/complete a pending task manager slot.
     */
    @Test
    public void testOnlyFreeSlotsCanFulfillPendingTaskManagerSlot() throws Exception {
        final int numberSlots = 1;
        final TestingResourceActions resourceActions = new TestingResourceActionsBuilder().setAllocateResourceFunction(SlotManagerTest.convert(( value) -> numberSlots)).build();
        try (final SlotManager slotManager = createSlotManager(ResourceManagerId.generate(), resourceActions)) {
            final JobID jobId = new JobID();
            Assert.assertThat(slotManager.registerSlotRequest(createSlotRequest(jobId)), Matchers.is(true));
            final TaskExecutorConnection taskExecutorConnection = createTaskExecutorConnection();
            final SlotID slotId = new SlotID(taskExecutorConnection.getResourceID(), 0);
            final SlotStatus slotStatus = new SlotStatus(slotId, ResourceProfile.UNKNOWN, jobId, new AllocationID());
            final SlotReport slotReport = new SlotReport(slotStatus);
            slotManager.registerTaskManager(taskExecutorConnection, slotReport);
            Assert.assertThat(slotManager.getNumberRegisteredSlots(), Matchers.is(1));
            Assert.assertThat(slotManager.getNumberPendingTaskManagerSlots(), Matchers.is(numberSlots));
            Assert.assertThat(slotManager.getNumberAssignedPendingTaskManagerSlots(), Matchers.is(1));
        }
    }
}

