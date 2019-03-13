/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.util.hashslot.impl;


import com.hazelcast.internal.memory.MemoryAccessor;
import com.hazelcast.internal.memory.MemoryAllocator;
import com.hazelcast.internal.memory.impl.HeapMemoryManager;
import com.hazelcast.internal.util.hashslot.HashSlotCursor16byteKey;
import com.hazelcast.internal.util.hashslot.SlotAssignmentResult;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.RequireAssertEnabled;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class HashSlotArray16byteKeyImplTest {
    // Value length must be at least 16 bytes, as required by the test's logic
    private static final int VALUE_LENGTH = 16;

    private final Random random = new Random();

    private HeapMemoryManager memMgr;

    private MemoryAccessor mem;

    private HashSlotArray16byteKeyImpl hsa;

    @Test
    public void testPut() throws Exception {
        final long key1 = randomKey();
        final long key2 = randomKey();
        SlotAssignmentResult slot = insert(key1, key2);
        Assert.assertTrue(slot.isNew());
        final long valueAddress = slot.address();
        slot = hsa.ensure(key1, key2);
        Assert.assertFalse(slot.isNew());
        Assert.assertEquals(valueAddress, slot.address());
    }

    @Test
    public void testGet() throws Exception {
        final long key1 = randomKey();
        final long key2 = randomKey();
        final SlotAssignmentResult slot = insert(key1, key2);
        final long valueAddress2 = hsa.get(key1, key2);
        Assert.assertEquals(slot.address(), valueAddress2);
    }

    @Test
    public void testRemove() throws Exception {
        final long key1 = randomKey();
        final long key2 = randomKey();
        insert(key1, key2);
        Assert.assertTrue(hsa.remove(key1, key2));
        Assert.assertFalse(hsa.remove(key1, key2));
    }

    @Test
    public void testSize() throws Exception {
        final long key1 = randomKey();
        final long key2 = randomKey();
        insert(key1, key2);
        Assert.assertEquals(1, hsa.size());
        Assert.assertTrue(hsa.remove(key1, key2));
        Assert.assertEquals(0, hsa.size());
    }

    @Test
    public void testClear() throws Exception {
        final long key1 = randomKey();
        final long key2 = randomKey();
        insert(key1, key2);
        hsa.clear();
        Assert.assertEquals(MemoryAllocator.NULL_ADDRESS, hsa.get(key1, key2));
        Assert.assertEquals(0, hsa.size());
    }

    @Test
    public void testPutGetMany() {
        final long factor = 123456;
        final int k = 1000;
        for (int i = 1; i <= k; i++) {
            long key1 = ((long) (i));
            long key2 = key1 * factor;
            insert(key1, key2);
        }
        for (int i = 1; i <= k; i++) {
            long key1 = ((long) (i));
            long key2 = key1 * factor;
            long valueAddress = hsa.get(key1, key2);
            Assert.assertEquals(key1, mem.getLong(valueAddress));
            Assert.assertEquals(key2, mem.getLong((valueAddress + 8L)));
        }
    }

    @Test
    public void testPutRemoveGetMany() {
        final long factor = 123456;
        final int k = 5000;
        final int mod = 100;
        for (int i = 1; i <= k; i++) {
            long key1 = ((long) (i));
            long key2 = key1 * factor;
            insert(key1, key2);
        }
        for (int i = mod; i <= k; i += mod) {
            long key1 = ((long) (i));
            long key2 = key1 * factor;
            Assert.assertTrue(hsa.remove(key1, key2));
        }
        for (int i = 1; i <= k; i++) {
            long key1 = ((long) (i));
            long key2 = key1 * factor;
            long valueAddress = hsa.get(key1, key2);
            if ((i % mod) == 0) {
                Assert.assertEquals(MemoryAllocator.NULL_ADDRESS, valueAddress);
            } else {
                verifyValue(key1, key2, valueAddress);
            }
        }
    }

    @Test
    public void testMemoryNotLeaking() {
        final long k = 2000;
        final long factor = 123456;
        for (long i = 1; i <= k; i++) {
            insert(i, (factor * i));
        }
        hsa.dispose();
        Assert.assertEquals("Memory leak: used memory not zero after dispose", 0, memMgr.getUsedMemory());
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testPut_whenDisposed() throws Exception {
        hsa.dispose();
        hsa.ensure(1, 1);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testGet_whenDisposed() throws Exception {
        hsa.dispose();
        hsa.get(1, 1);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testRemove_whenDisposed() throws Exception {
        hsa.dispose();
        hsa.remove(1, 1);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testClear_whenDisposed() throws Exception {
        hsa.dispose();
        hsa.clear();
    }

    @Test
    public void testMigrateTo() {
        final SlotAssignmentResult slot = insert(1, 2);
        mem.putLong(slot.address(), 3);
        final HeapMemoryManager mgr2 = new HeapMemoryManager(memMgr);
        hsa.migrateTo(mgr2.getAllocator());
        Assert.assertEquals(0, memMgr.getUsedMemory());
        final long newValueAddr = hsa.get(1, 2);
        Assert.assertEquals(3, mem.getLong(newValueAddr));
    }

    @Test
    public void testAuxAllocator() {
        final HeapMemoryManager mgr2 = new HeapMemoryManager(memMgr);
        final int initialCapacity = 4;
        final int factor = 13;
        hsa = new HashSlotArray16byteKeyImpl(0, memMgr, mgr2.getAllocator(), HashSlotArray16byteKeyImplTest.VALUE_LENGTH, initialCapacity, DEFAULT_LOAD_FACTOR);
        hsa.gotoNew();
        for (int i = 1; i <= (2 * initialCapacity); i++) {
            insert(i, (factor * i));
        }
        for (int i = 1; i <= (2 * initialCapacity); i++) {
            verifyValue(i, (factor * i), hsa.get(i, (factor * i)));
        }
        Assert.assertEquals(0, mgr2.getUsedMemory());
    }

    @Test
    public void testGotoNew() {
        hsa.dispose();
        hsa.gotoNew();
        final SlotAssignmentResult slot = insert(1, 2);
        final long gotValueAddr = hsa.get(1, 2);
        Assert.assertEquals(slot.address(), gotValueAddr);
    }

    @Test
    public void testGotoAddress() {
        final long addr1 = hsa.address();
        final SlotAssignmentResult slot = insert(1, 2);
        hsa.gotoNew();
        Assert.assertEquals(MemoryAllocator.NULL_ADDRESS, hsa.get(1, 2));
        hsa.gotoAddress(addr1);
        Assert.assertEquals(slot.address(), hsa.get(1, 2));
    }

    // Cursor tests
    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testCursor_key1_withoutAdvance() {
        HashSlotCursor16byteKey cursor = hsa.cursor();
        cursor.key1();
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testCursor_key2_withoutAdvance() {
        HashSlotCursor16byteKey cursor = hsa.cursor();
        cursor.key2();
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testCursor_valueAddress_withoutAdvance() {
        HashSlotCursor16byteKey cursor = hsa.cursor();
        cursor.valueAddress();
    }

    @Test
    public void testCursor_advance_whenEmpty() {
        HashSlotCursor16byteKey cursor = hsa.cursor();
        Assert.assertFalse(cursor.advance());
    }

    @Test
    public void testCursor_advance() {
        insert(randomKey(), randomKey());
        HashSlotCursor16byteKey cursor = hsa.cursor();
        Assert.assertTrue(cursor.advance());
        Assert.assertFalse(cursor.advance());
    }

    @Test
    @RequireAssertEnabled
    public void testCursor_advance_afterAdvanceReturnsFalse() {
        insert(randomKey(), randomKey());
        HashSlotCursor16byteKey cursor = hsa.cursor();
        cursor.advance();
        cursor.advance();
        try {
            cursor.advance();
            Assert.fail("cursor.advance() returned false, but subsequent call did not throw AssertionError");
        } catch (AssertionError ignored) {
        }
    }

    @Test
    public void testCursor_key1() {
        final long key1 = randomKey();
        final long key2 = randomKey();
        insert(key1, key2);
        HashSlotCursor16byteKey cursor = hsa.cursor();
        cursor.advance();
        Assert.assertEquals(key1, cursor.key1());
    }

    @Test
    public void testCursor_key2() {
        final long key1 = randomKey();
        final long key2 = randomKey();
        insert(key1, key2);
        HashSlotCursor16byteKey cursor = hsa.cursor();
        cursor.advance();
        Assert.assertEquals(key2, cursor.key2());
    }

    @Test
    public void testCursor_valueAddress() {
        final SlotAssignmentResult slot = insert(randomKey(), randomKey());
        HashSlotCursor16byteKey cursor = hsa.cursor();
        cursor.advance();
        Assert.assertEquals(slot.address(), cursor.valueAddress());
    }

    @Test(expected = AssertionError.class)
    public void testCursor_advance_whenDisposed() {
        HashSlotCursor16byteKey cursor = hsa.cursor();
        hsa.dispose();
        cursor.advance();
    }

    @Test(expected = AssertionError.class)
    public void testCursor_key1_whenDisposed() {
        HashSlotCursor16byteKey cursor = hsa.cursor();
        hsa.dispose();
        cursor.key1();
    }

    @Test(expected = AssertionError.class)
    public void testCursor_key2_whenDisposed() {
        HashSlotCursor16byteKey cursor = hsa.cursor();
        hsa.dispose();
        cursor.key2();
    }

    @Test(expected = AssertionError.class)
    public void testCursor_valueAddress_whenDisposed() {
        HashSlotCursor16byteKey cursor = hsa.cursor();
        hsa.dispose();
        cursor.valueAddress();
    }

    @Test
    public void testCursor_withManyValues() {
        final long factor = 123456;
        final int k = 1000;
        for (int i = 1; i <= k; i++) {
            long key1 = ((long) (i));
            long key2 = key1 * factor;
            insert(key1, key2);
        }
        boolean[] verifiedKeys = new boolean[k];
        HashSlotCursor16byteKey cursor = hsa.cursor();
        while (cursor.advance()) {
            long key1 = cursor.key1();
            long key2 = cursor.key2();
            long valueAddress = cursor.valueAddress();
            Assert.assertEquals((key1 * factor), key2);
            verifyValue(key1, key2, valueAddress);
            verifiedKeys[(((int) (key1)) - 1)] = true;
        } 
        for (int i = 0; i < k; i++) {
            Assert.assertTrue(("Failed to encounter key " + i), verifiedKeys[i]);
        }
    }
}

