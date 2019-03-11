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


import SlotPoolImpl.AvailableSlots;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.clusterframework.types.ResourceProfile;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


public class AvailableSlotsTest extends TestLogger {
    static final ResourceProfile DEFAULT_TESTING_PROFILE = new ResourceProfile(1.0, 512);

    @Test
    public void testAddAndRemove() {
        SlotPoolImpl.AvailableSlots availableSlots = new SlotPoolImpl.AvailableSlots();
        final ResourceID resource1 = new ResourceID("resource1");
        final ResourceID resource2 = new ResourceID("resource2");
        final AllocatedSlot slot1 = AvailableSlotsTest.createAllocatedSlot(resource1);
        final AllocatedSlot slot2 = AvailableSlotsTest.createAllocatedSlot(resource1);
        final AllocatedSlot slot3 = AvailableSlotsTest.createAllocatedSlot(resource2);
        availableSlots.add(slot1, 1L);
        availableSlots.add(slot2, 2L);
        availableSlots.add(slot3, 3L);
        Assert.assertEquals(3, availableSlots.size());
        Assert.assertTrue(availableSlots.contains(slot1.getAllocationId()));
        Assert.assertTrue(availableSlots.contains(slot2.getAllocationId()));
        Assert.assertTrue(availableSlots.contains(slot3.getAllocationId()));
        Assert.assertTrue(availableSlots.containsTaskManager(resource1));
        Assert.assertTrue(availableSlots.containsTaskManager(resource2));
        availableSlots.removeAllForTaskManager(resource1);
        Assert.assertEquals(1, availableSlots.size());
        Assert.assertFalse(availableSlots.contains(slot1.getAllocationId()));
        Assert.assertFalse(availableSlots.contains(slot2.getAllocationId()));
        Assert.assertTrue(availableSlots.contains(slot3.getAllocationId()));
        Assert.assertFalse(availableSlots.containsTaskManager(resource1));
        Assert.assertTrue(availableSlots.containsTaskManager(resource2));
        availableSlots.removeAllForTaskManager(resource2);
        Assert.assertEquals(0, availableSlots.size());
        Assert.assertFalse(availableSlots.contains(slot1.getAllocationId()));
        Assert.assertFalse(availableSlots.contains(slot2.getAllocationId()));
        Assert.assertFalse(availableSlots.contains(slot3.getAllocationId()));
        Assert.assertFalse(availableSlots.containsTaskManager(resource1));
        Assert.assertFalse(availableSlots.containsTaskManager(resource2));
    }
}

