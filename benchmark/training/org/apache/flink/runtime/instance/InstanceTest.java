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


import java.lang.reflect.Method;
import java.net.InetAddress;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.jobmanager.slots.ActorTaskManagerGateway;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link Instance} class.
 */
public class InstanceTest {
    @Test
    public void testAllocatingAndCancellingSlots() {
        try {
            ResourceID resourceID = ResourceID.generate();
            HardwareDescription hardwareDescription = new HardwareDescription(4, (((2L * 1024) * 1024) * 1024), ((1024 * 1024) * 1024), ((512 * 1024) * 1024));
            InetAddress address = InetAddress.getByName("127.0.0.1");
            TaskManagerLocation connection = new TaskManagerLocation(resourceID, address, 10001);
            Instance instance = new Instance(new ActorTaskManagerGateway(DummyActorGateway.INSTANCE), connection, new InstanceID(), hardwareDescription, 4);
            Assert.assertEquals(4, instance.getTotalNumberOfSlots());
            Assert.assertEquals(4, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(0, instance.getNumberOfAllocatedSlots());
            SimpleSlot slot1 = instance.allocateSimpleSlot();
            SimpleSlot slot2 = instance.allocateSimpleSlot();
            SimpleSlot slot3 = instance.allocateSimpleSlot();
            SimpleSlot slot4 = instance.allocateSimpleSlot();
            Assert.assertNotNull(slot1);
            Assert.assertNotNull(slot2);
            Assert.assertNotNull(slot3);
            Assert.assertNotNull(slot4);
            Assert.assertEquals(0, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(4, instance.getNumberOfAllocatedSlots());
            Assert.assertEquals(6, ((((slot1.getSlotNumber()) + (slot2.getSlotNumber())) + (slot3.getSlotNumber())) + (slot4.getSlotNumber())));
            // no more slots
            Assert.assertNull(instance.allocateSimpleSlot());
            try {
                instance.returnLogicalSlot(slot2);
                Assert.fail("instance accepted a non-cancelled slot.");
            } catch (IllegalArgumentException e) {
                // good
            }
            // release the slots. this returns them to the instance
            slot1.releaseSlot();
            slot2.releaseSlot();
            slot3.releaseSlot();
            slot4.releaseSlot();
            Assert.assertEquals(4, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(0, instance.getNumberOfAllocatedSlots());
            instance.returnLogicalSlot(slot1);
            instance.returnLogicalSlot(slot2);
            instance.returnLogicalSlot(slot3);
            instance.returnLogicalSlot(slot4);
            Assert.assertEquals(4, instance.getNumberOfAvailableSlots());
            Assert.assertEquals(0, instance.getNumberOfAllocatedSlots());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testInstanceDies() {
        try {
            ResourceID resourceID = ResourceID.generate();
            HardwareDescription hardwareDescription = new HardwareDescription(4, (((2L * 1024) * 1024) * 1024), ((1024 * 1024) * 1024), ((512 * 1024) * 1024));
            InetAddress address = InetAddress.getByName("127.0.0.1");
            TaskManagerLocation connection = new TaskManagerLocation(resourceID, address, 10001);
            Instance instance = new Instance(new ActorTaskManagerGateway(DummyActorGateway.INSTANCE), connection, new InstanceID(), hardwareDescription, 3);
            Assert.assertEquals(3, instance.getNumberOfAvailableSlots());
            SimpleSlot slot1 = instance.allocateSimpleSlot();
            SimpleSlot slot2 = instance.allocateSimpleSlot();
            SimpleSlot slot3 = instance.allocateSimpleSlot();
            instance.markDead();
            Assert.assertEquals(0, instance.getNumberOfAllocatedSlots());
            Assert.assertEquals(0, instance.getNumberOfAvailableSlots());
            Assert.assertTrue(slot1.isCanceled());
            Assert.assertTrue(slot2.isCanceled());
            Assert.assertTrue(slot3.isCanceled());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCancelAllSlots() {
        try {
            ResourceID resourceID = ResourceID.generate();
            HardwareDescription hardwareDescription = new HardwareDescription(4, (((2L * 1024) * 1024) * 1024), ((1024 * 1024) * 1024), ((512 * 1024) * 1024));
            InetAddress address = InetAddress.getByName("127.0.0.1");
            TaskManagerLocation connection = new TaskManagerLocation(resourceID, address, 10001);
            Instance instance = new Instance(new ActorTaskManagerGateway(DummyActorGateway.INSTANCE), connection, new InstanceID(), hardwareDescription, 3);
            Assert.assertEquals(3, instance.getNumberOfAvailableSlots());
            SimpleSlot slot1 = instance.allocateSimpleSlot();
            SimpleSlot slot2 = instance.allocateSimpleSlot();
            SimpleSlot slot3 = instance.allocateSimpleSlot();
            instance.cancelAndReleaseAllSlots();
            Assert.assertEquals(3, instance.getNumberOfAvailableSlots());
            Assert.assertTrue(slot1.isCanceled());
            Assert.assertTrue(slot2.isCanceled());
            Assert.assertTrue(slot3.isCanceled());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    /**
     * It is crucial for some portions of the code that instance objects do not override equals and
     * are only considered equal, if the references are equal.
     */
    @Test
    public void testInstancesReferenceEqual() {
        try {
            Method m = Instance.class.getMethod("equals", Object.class);
            Assert.assertTrue(((m.getDeclaringClass()) == (Object.class)));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

