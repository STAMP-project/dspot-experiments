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


import org.apache.flink.runtime.jobmaster.TestingPayload;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


public class SimpleSlotTest extends TestLogger {
    @Test
    public void testStateTransitions() {
        try {
            // release immediately
            {
                SimpleSlot slot = SimpleSlotTest.getSlot();
                Assert.assertTrue(slot.isAlive());
                slot.releaseSlot();
                Assert.assertFalse(slot.isAlive());
                Assert.assertTrue(slot.isCanceled());
                Assert.assertTrue(slot.isReleased());
            }
            // state transitions manually
            {
                SimpleSlot slot = SimpleSlotTest.getSlot();
                Assert.assertTrue(slot.isAlive());
                slot.markCancelled();
                Assert.assertFalse(slot.isAlive());
                Assert.assertTrue(slot.isCanceled());
                Assert.assertFalse(slot.isReleased());
                slot.markReleased();
                Assert.assertFalse(slot.isAlive());
                Assert.assertTrue(slot.isCanceled());
                Assert.assertTrue(slot.isReleased());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSetExecutionVertex() {
        try {
            TestingPayload payload1 = new TestingPayload();
            TestingPayload payload2 = new TestingPayload();
            // assign to alive slot
            {
                SimpleSlot slot = SimpleSlotTest.getSlot();
                Assert.assertTrue(slot.tryAssignPayload(payload1));
                Assert.assertEquals(payload1, slot.getPayload());
                // try to add another one
                Assert.assertFalse(slot.tryAssignPayload(payload2));
                Assert.assertEquals(payload1, slot.getPayload());
            }
            // assign to canceled slot
            {
                SimpleSlot slot = SimpleSlotTest.getSlot();
                Assert.assertTrue(slot.markCancelled());
                Assert.assertFalse(slot.tryAssignPayload(payload1));
                Assert.assertNull(slot.getPayload());
            }
            // assign to released marked slot
            {
                SimpleSlot slot = SimpleSlotTest.getSlot();
                Assert.assertTrue(slot.markCancelled());
                Assert.assertTrue(slot.markReleased());
                Assert.assertFalse(slot.tryAssignPayload(payload1));
                Assert.assertNull(slot.getPayload());
            }
            // assign to released
            {
                SimpleSlot slot = SimpleSlotTest.getSlot();
                slot.releaseSlot();
                Assert.assertFalse(slot.tryAssignPayload(payload1));
                Assert.assertNull(slot.getPayload());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

