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
import com.hazelcast.internal.util.collection.HsaHeapMemoryManager;
import com.hazelcast.internal.util.hashslot.HashSlotArray8byteKey;
import com.hazelcast.internal.util.hashslot.HashSlotCursor8byteKey;
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
public class HashSlotArray8byteKeyImplTest {
    private static final int VALUE_LENGTH = 32;

    private final Random random = new Random();

    private HsaHeapMemoryManager memMgr;

    private MemoryAccessor mem;

    private HashSlotArray8byteKey hsa;

    @Test
    public void testPut() throws Exception {
        final long key = random.nextLong();
        SlotAssignmentResult slot = insert(key);
        final long valueAddress = slot.address();
        Assert.assertTrue(slot.isNew());
        slot = hsa.ensure(key);
        Assert.assertFalse(slot.isNew());
        Assert.assertEquals(valueAddress, slot.address());
    }

    @Test
    public void testGet() throws Exception {
        final long key = random.nextLong();
        final long valueAddress = insert(key).address();
        final long valueAddress2 = hsa.get(key);
        Assert.assertEquals(valueAddress, valueAddress2);
    }

    @Test
    public void testRemove() throws Exception {
        final long key = random.nextLong();
        insert(key);
        Assert.assertTrue(hsa.remove(key));
        Assert.assertFalse(hsa.remove(key));
    }

    @Test
    public void testSize() throws Exception {
        final long key = random.nextLong();
        insert(key);
        Assert.assertEquals(1, hsa.size());
        Assert.assertTrue(hsa.remove(key));
        Assert.assertEquals(0, hsa.size());
    }

    @Test
    public void testClear() throws Exception {
        final long key = random.nextLong();
        insert(key);
        hsa.clear();
        Assert.assertEquals(MemoryAllocator.NULL_ADDRESS, hsa.get(key));
        Assert.assertEquals(0, hsa.size());
    }

    @Test
    public void testPutGetMany() {
        final int k = 1000;
        for (int i = 1; i <= k; i++) {
            long key = ((long) (i));
            insert(key);
        }
        for (int i = 1; i <= k; i++) {
            long key = ((long) (i));
            long valueAddress = hsa.get(key);
            verifyValue(key, valueAddress);
        }
    }

    @Test
    public void testPutRemoveGetMany() {
        final int k = 5000;
        final int mod = 100;
        for (int i = 1; i <= k; i++) {
            long key = ((long) (i));
            insert(key);
        }
        for (int i = mod; i <= k; i += mod) {
            long key = ((long) (i));
            Assert.assertTrue(hsa.remove(key));
        }
        for (int i = 1; i <= k; i++) {
            long key = ((long) (i));
            long valueAddress = hsa.get(key);
            if ((i % mod) == 0) {
                Assert.assertEquals(MemoryAllocator.NULL_ADDRESS, valueAddress);
            } else {
                verifyValue(key, valueAddress);
            }
        }
    }

    @Test
    public void testMemoryNotLeaking() {
        final long k = 2000;
        for (long i = 1; i <= k; i++) {
            insert(i);
        }
        hsa.dispose();
        Assert.assertEquals("Memory leak: used memory not zero after dispose", 0, memMgr.getUsedMemory());
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testPut_whenDisposed() throws Exception {
        hsa.dispose();
        hsa.ensure(1);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testGet_whenDisposed() throws Exception {
        hsa.dispose();
        hsa.get(1);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testRemove_whenDisposed() throws Exception {
        hsa.dispose();
        hsa.remove(1);
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testClear_whenDisposed() throws Exception {
        hsa.dispose();
        hsa.clear();
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testCursor_key_withoutAdvance() {
        HashSlotCursor8byteKey cursor = hsa.cursor();
        cursor.key();
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testCursor_valueAddress_withoutAdvance() {
        HashSlotCursor8byteKey cursor = hsa.cursor();
        cursor.valueAddress();
    }

    @Test
    public void testCursor_advance_whenEmpty() {
        HashSlotCursor8byteKey cursor = hsa.cursor();
        Assert.assertFalse(cursor.advance());
    }

    @Test
    public void testCursor_advance() {
        insert(random.nextLong());
        HashSlotCursor8byteKey cursor = hsa.cursor();
        Assert.assertTrue(cursor.advance());
        Assert.assertFalse(cursor.advance());
    }

    @Test
    @RequireAssertEnabled
    public void testCursor_advance_afterAdvanceReturnsFalse() {
        insert(random.nextLong());
        HashSlotCursor8byteKey cursor = hsa.cursor();
        cursor.advance();
        cursor.advance();
        try {
            cursor.advance();
            Assert.fail("cursor.advance() returned false, but subsequent call did not throw AssertionError");
        } catch (AssertionError ignored) {
        }
    }

    @Test
    public void testCursor_key() {
        final long key = random.nextLong();
        insert(key);
        HashSlotCursor8byteKey cursor = hsa.cursor();
        cursor.advance();
        Assert.assertEquals(key, cursor.key());
    }

    @Test
    public void testCursor_valueAddress() {
        final SlotAssignmentResult slot = insert(random.nextLong());
        HashSlotCursor8byteKey cursor = hsa.cursor();
        cursor.advance();
        Assert.assertEquals(slot.address(), cursor.valueAddress());
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testCursor_advance_whenDisposed() {
        HashSlotCursor8byteKey cursor = hsa.cursor();
        hsa.dispose();
        cursor.advance();
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testCursor_key_whenDisposed() {
        HashSlotCursor8byteKey cursor = hsa.cursor();
        hsa.dispose();
        cursor.key();
    }

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testCursor_valueAddress_whenDisposed() {
        HashSlotCursor8byteKey cursor = hsa.cursor();
        hsa.dispose();
        cursor.valueAddress();
    }

    @Test
    public void testCursor_withManyValues() {
        final int k = 1000;
        for (int i = 1; i <= k; i++) {
            long key = ((long) (i));
            insert(key);
        }
        boolean[] verifiedKeys = new boolean[k];
        HashSlotCursor8byteKey cursor = hsa.cursor();
        while (cursor.advance()) {
            long key = cursor.key();
            long valueAddress = cursor.valueAddress();
            verifyValue(key, valueAddress);
            verifiedKeys[(((int) (key)) - 1)] = true;
        } 
        for (int i = 0; i < k; i++) {
            Assert.assertTrue(("Failed to encounter key " + i), verifiedKeys[i]);
        }
    }
}

