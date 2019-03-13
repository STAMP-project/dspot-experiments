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
package org.apache.flink.core.memory;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;
import java.util.Arrays;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the access and transfer methods of the HeapMemorySegment.
 */
public abstract class MemorySegmentTestBase {
    private final Random random = new Random();

    private final int pageSize;

    MemorySegmentTestBase(int pageSize) {
        this.pageSize = pageSize;
    }

    // ------------------------------------------------------------------------
    // Access to primitives
    // ------------------------------------------------------------------------
    @Test
    public void testByteAccess() {
        final MemorySegment segment = createSegment(pageSize);
        // test exceptions
        try {
            segment.put((-1), ((byte) (0)));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(pageSize, ((byte) (0)));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(Integer.MAX_VALUE, ((byte) (0)));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(Integer.MIN_VALUE, ((byte) (0)));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get((-1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(pageSize);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(Integer.MAX_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(Integer.MIN_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // test expected correct behavior, sequential access
        long seed = random.nextLong();
        random.setSeed(seed);
        for (int i = 0; i < (pageSize); i++) {
            segment.put(i, ((byte) (random.nextInt())));
        }
        random.setSeed(seed);
        for (int i = 0; i < (pageSize); i++) {
            Assert.assertEquals(((byte) (random.nextInt())), segment.get(i));
        }
        // test expected correct behavior, random access
        random.setSeed(seed);
        boolean[] occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(pageSize);
            if (occupied[pos]) {
                continue;
            } else {
                occupied[pos] = true;
            }
            segment.put(pos, ((byte) (random.nextInt())));
        }
        random.setSeed(seed);
        occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(pageSize);
            if (occupied[pos]) {
                continue;
            } else {
                occupied[pos] = true;
            }
            Assert.assertEquals(((byte) (random.nextInt())), segment.get(pos));
        }
    }

    @Test
    public void testBooleanAccess() {
        final MemorySegment segment = createSegment(pageSize);
        // test exceptions
        try {
            segment.putBoolean((-1), false);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putBoolean(pageSize, false);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putBoolean(Integer.MAX_VALUE, false);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putBoolean(Integer.MIN_VALUE, false);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getBoolean((-1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getBoolean(pageSize);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getBoolean(Integer.MAX_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getBoolean(Integer.MIN_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // test expected correct behavior, sequential access
        long seed = random.nextLong();
        random.setSeed(seed);
        for (int i = 0; i < (pageSize); i++) {
            segment.putBoolean(i, random.nextBoolean());
        }
        random.setSeed(seed);
        for (int i = 0; i < (pageSize); i++) {
            Assert.assertEquals(random.nextBoolean(), segment.getBoolean(i));
        }
        // test expected correct behavior, random access
        random.setSeed(seed);
        boolean[] occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(pageSize);
            if (occupied[pos]) {
                continue;
            } else {
                occupied[pos] = true;
            }
            segment.putBoolean(pos, random.nextBoolean());
        }
        random.setSeed(seed);
        occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(pageSize);
            if (occupied[pos]) {
                continue;
            } else {
                occupied[pos] = true;
            }
            Assert.assertEquals(random.nextBoolean(), segment.getBoolean(pos));
        }
    }

    @Test
    public void testCopyUnsafeIndexOutOfBounds() {
        byte[] bytes = new byte[pageSize];
        MemorySegment segment = createSegment(pageSize);
        try {
            segment.copyToUnsafe(1, bytes, 0, pageSize);
            Assert.fail("should fail with an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ignored) {
        }
        try {
            segment.copyFromUnsafe(1, bytes, 0, pageSize);
            Assert.fail("should fail with an IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    @Test
    public void testEqualTo() {
        MemorySegment seg1 = createSegment(pageSize);
        MemorySegment seg2 = createSegment(pageSize);
        int i = new Random().nextInt(((pageSize) - 8));
        seg1.put(i, ((byte) (10)));
        Assert.assertFalse(seg1.equalTo(seg2, i, i, 9));
        seg1.put(i, ((byte) (0)));
        Assert.assertTrue(seg1.equalTo(seg2, i, i, 9));
        seg1.put((i + 8), ((byte) (10)));
        Assert.assertFalse(seg1.equalTo(seg2, i, i, 9));
    }

    @Test
    public void testCharAccess() {
        final MemorySegment segment = createSegment(pageSize);
        // test exceptions
        try {
            segment.putChar((-1), 'a');
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putChar(pageSize, 'a');
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putChar(Integer.MIN_VALUE, 'a');
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putChar(Integer.MAX_VALUE, 'a');
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putChar(((Integer.MAX_VALUE) - 1), 'a');
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getChar((-1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getChar(pageSize);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getChar(Integer.MIN_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getChar(Integer.MAX_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getChar(((Integer.MAX_VALUE) - 1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // test expected correct behavior, sequential access
        long seed = random.nextLong();
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 2); i += 2) {
            segment.putChar(i, ((char) (random.nextInt(Character.MAX_VALUE))));
        }
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 2); i += 2) {
            Assert.assertEquals(((char) (random.nextInt(Character.MAX_VALUE))), segment.getChar(i));
        }
        // test expected correct behavior, random access
        random.setSeed(seed);
        boolean[] occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 1));
            if ((occupied[pos]) || (occupied[(pos + 1)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
            }
            segment.putChar(pos, ((char) (random.nextInt(Character.MAX_VALUE))));
        }
        random.setSeed(seed);
        occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 1));
            if ((occupied[pos]) || (occupied[(pos + 1)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
            }
            Assert.assertEquals(((char) (random.nextInt(Character.MAX_VALUE))), segment.getChar(pos));
        }
    }

    @Test
    public void testShortAccess() {
        final MemorySegment segment = createSegment(pageSize);
        // test exceptions
        try {
            segment.putShort((-1), ((short) (0)));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putShort(pageSize, ((short) (0)));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putShort(Integer.MIN_VALUE, ((short) (0)));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putShort(Integer.MAX_VALUE, ((short) (0)));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putShort(((Integer.MAX_VALUE) - 1), ((short) (0)));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getShort((-1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getShort(pageSize);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getShort(Integer.MIN_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getShort(Integer.MAX_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getShort(((Integer.MAX_VALUE) - 1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // test expected correct behavior, sequential access
        long seed = random.nextLong();
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 2); i += 2) {
            segment.putShort(i, ((short) (random.nextInt())));
        }
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 2); i += 2) {
            Assert.assertEquals(((short) (random.nextInt())), segment.getShort(i));
        }
        // test expected correct behavior, random access
        random.setSeed(seed);
        boolean[] occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 1));
            if ((occupied[pos]) || (occupied[(pos + 1)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
            }
            segment.putShort(pos, ((short) (random.nextInt())));
        }
        random.setSeed(seed);
        occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 1));
            if ((occupied[pos]) || (occupied[(pos + 1)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
            }
            Assert.assertEquals(((short) (random.nextInt())), segment.getShort(pos));
        }
    }

    @Test
    public void testIntAccess() {
        final MemorySegment segment = createSegment(pageSize);
        // test exceptions
        try {
            segment.putInt((-1), 0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putInt(pageSize, 0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putInt(((pageSize) - 3), 0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putInt(Integer.MIN_VALUE, 0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putInt(Integer.MAX_VALUE, 0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putInt(((Integer.MAX_VALUE) - 3), 0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getInt((-1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getInt(pageSize);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getInt(((pageSize) - 3));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getInt(Integer.MIN_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getInt(Integer.MAX_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getInt(((Integer.MAX_VALUE) - 3));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // test expected correct behavior, sequential access
        long seed = random.nextLong();
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 4); i += 4) {
            segment.putInt(i, random.nextInt());
        }
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 4); i += 4) {
            Assert.assertEquals(random.nextInt(), segment.getInt(i));
        }
        // test expected correct behavior, random access
        random.setSeed(seed);
        boolean[] occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 3));
            if ((((occupied[pos]) || (occupied[(pos + 1)])) || (occupied[(pos + 2)])) || (occupied[(pos + 3)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
                occupied[(pos + 2)] = true;
                occupied[(pos + 3)] = true;
            }
            segment.putInt(pos, random.nextInt());
        }
        random.setSeed(seed);
        occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 3));
            if ((((occupied[pos]) || (occupied[(pos + 1)])) || (occupied[(pos + 2)])) || (occupied[(pos + 3)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
                occupied[(pos + 2)] = true;
                occupied[(pos + 3)] = true;
            }
            Assert.assertEquals(random.nextInt(), segment.getInt(pos));
        }
    }

    @Test
    public void testLongAccess() {
        final MemorySegment segment = createSegment(pageSize);
        // test exceptions
        try {
            segment.putLong((-1), 0L);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putLong(pageSize, 0L);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putLong(((pageSize) - 7), 0L);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putLong(Integer.MIN_VALUE, 0L);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putLong(Integer.MAX_VALUE, 0L);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putLong(((Integer.MAX_VALUE) - 7), 0L);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getLong((-1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getLong(pageSize);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getLong(((pageSize) - 7));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getLong(Integer.MIN_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getLong(Integer.MAX_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getLong(((Integer.MAX_VALUE) - 7));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // test expected correct behavior, sequential access
        long seed = random.nextLong();
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 8); i += 8) {
            segment.putLong(i, random.nextLong());
        }
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 8); i += 8) {
            Assert.assertEquals(random.nextLong(), segment.getLong(i));
        }
        // test expected correct behavior, random access
        random.setSeed(seed);
        boolean[] occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 7));
            if ((((((((occupied[pos]) || (occupied[(pos + 1)])) || (occupied[(pos + 2)])) || (occupied[(pos + 3)])) || (occupied[(pos + 4)])) || (occupied[(pos + 5)])) || (occupied[(pos + 6)])) || (occupied[(pos + 7)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
                occupied[(pos + 2)] = true;
                occupied[(pos + 3)] = true;
                occupied[(pos + 4)] = true;
                occupied[(pos + 5)] = true;
                occupied[(pos + 6)] = true;
                occupied[(pos + 7)] = true;
            }
            segment.putLong(pos, random.nextLong());
        }
        random.setSeed(seed);
        occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 7));
            if ((((((((occupied[pos]) || (occupied[(pos + 1)])) || (occupied[(pos + 2)])) || (occupied[(pos + 3)])) || (occupied[(pos + 4)])) || (occupied[(pos + 5)])) || (occupied[(pos + 6)])) || (occupied[(pos + 7)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
                occupied[(pos + 2)] = true;
                occupied[(pos + 3)] = true;
                occupied[(pos + 4)] = true;
                occupied[(pos + 5)] = true;
                occupied[(pos + 6)] = true;
                occupied[(pos + 7)] = true;
            }
            Assert.assertEquals(random.nextLong(), segment.getLong(pos));
        }
    }

    @Test
    public void testFloatAccess() {
        final MemorySegment segment = createSegment(pageSize);
        // test exceptions
        try {
            segment.putFloat((-1), 0.0F);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putFloat(pageSize, 0.0F);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putFloat(((pageSize) - 3), 0.0F);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putFloat(Integer.MIN_VALUE, 0.0F);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putFloat(Integer.MAX_VALUE, 0.0F);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putFloat(((Integer.MAX_VALUE) - 3), 0.0F);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getFloat((-1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getFloat(pageSize);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getFloat(((pageSize) - 3));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getFloat(Integer.MIN_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getFloat(Integer.MAX_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getFloat(((Integer.MAX_VALUE) - 3));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // test expected correct behavior, sequential access
        long seed = random.nextLong();
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 4); i += 4) {
            segment.putFloat(i, random.nextFloat());
        }
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 4); i += 4) {
            Assert.assertEquals(random.nextFloat(), segment.getFloat(i), 0.0);
        }
        // test expected correct behavior, random access
        random.setSeed(seed);
        boolean[] occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 3));
            if ((((occupied[pos]) || (occupied[(pos + 1)])) || (occupied[(pos + 2)])) || (occupied[(pos + 3)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
                occupied[(pos + 2)] = true;
                occupied[(pos + 3)] = true;
            }
            segment.putFloat(pos, random.nextFloat());
        }
        random.setSeed(seed);
        occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 3));
            if ((((occupied[pos]) || (occupied[(pos + 1)])) || (occupied[(pos + 2)])) || (occupied[(pos + 3)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
                occupied[(pos + 2)] = true;
                occupied[(pos + 3)] = true;
            }
            Assert.assertEquals(random.nextFloat(), segment.getFloat(pos), 0.0);
        }
    }

    @Test
    public void testDoubleAccess() {
        final MemorySegment segment = createSegment(pageSize);
        // test exceptions
        try {
            segment.putDouble((-1), 0.0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putDouble(pageSize, 0.0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putDouble(((pageSize) - 7), 0.0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putDouble(Integer.MIN_VALUE, 0.0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putDouble(Integer.MAX_VALUE, 0.0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.putDouble(((Integer.MAX_VALUE) - 7), 0.0);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getDouble((-1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getDouble(pageSize);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getDouble(((pageSize) - 7));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getDouble(Integer.MIN_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getDouble(Integer.MAX_VALUE);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.getDouble(((Integer.MAX_VALUE) - 7));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // test expected correct behavior, sequential access
        long seed = random.nextLong();
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 8); i += 8) {
            segment.putDouble(i, random.nextDouble());
        }
        random.setSeed(seed);
        for (int i = 0; i <= ((pageSize) - 8); i += 8) {
            Assert.assertEquals(random.nextDouble(), segment.getDouble(i), 0.0);
        }
        // test expected correct behavior, random access
        random.setSeed(seed);
        boolean[] occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 7));
            if ((((((((occupied[pos]) || (occupied[(pos + 1)])) || (occupied[(pos + 2)])) || (occupied[(pos + 3)])) || (occupied[(pos + 4)])) || (occupied[(pos + 5)])) || (occupied[(pos + 6)])) || (occupied[(pos + 7)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
                occupied[(pos + 2)] = true;
                occupied[(pos + 3)] = true;
                occupied[(pos + 4)] = true;
                occupied[(pos + 5)] = true;
                occupied[(pos + 6)] = true;
                occupied[(pos + 7)] = true;
            }
            segment.putDouble(pos, random.nextDouble());
        }
        random.setSeed(seed);
        occupied = new boolean[pageSize];
        for (int i = 0; i < 1000; i++) {
            int pos = random.nextInt(((pageSize) - 7));
            if ((((((((occupied[pos]) || (occupied[(pos + 1)])) || (occupied[(pos + 2)])) || (occupied[(pos + 3)])) || (occupied[(pos + 4)])) || (occupied[(pos + 5)])) || (occupied[(pos + 6)])) || (occupied[(pos + 7)])) {
                continue;
            } else {
                occupied[pos] = true;
                occupied[(pos + 1)] = true;
                occupied[(pos + 2)] = true;
                occupied[(pos + 3)] = true;
                occupied[(pos + 4)] = true;
                occupied[(pos + 5)] = true;
                occupied[(pos + 6)] = true;
                occupied[(pos + 7)] = true;
            }
            Assert.assertEquals(random.nextDouble(), segment.getDouble(pos), 0.0);
        }
    }

    // ------------------------------------------------------------------------
    // Bulk Byte Movements
    // ------------------------------------------------------------------------
    @Test
    public void testBulkBytePutExceptions() {
        final MemorySegment segment = createSegment(pageSize);
        byte[] bytes = new byte[((pageSize) / 4) + ((pageSize) % 4)];
        random.nextBytes(bytes);
        // wrong positions into memory segment
        try {
            segment.put((-1), bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put((-1), bytes, 4, 5);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(Integer.MIN_VALUE, bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(Integer.MIN_VALUE, bytes, 4, 5);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(pageSize, bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(pageSize, bytes, 6, 44);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put((((pageSize) - (bytes.length)) + 1), bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(((pageSize) - 5), bytes, 3, 6);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(Integer.MAX_VALUE, bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(Integer.MAX_VALUE, bytes, 10, 20);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put((((Integer.MAX_VALUE) - (bytes.length)) + 1), bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(((Integer.MAX_VALUE) - 11), bytes, 11, 11);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(((3 * ((pageSize) / 4)) + 1), bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(((3 * ((pageSize) / 4)) + 2), bytes, 0, ((bytes.length) - 1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(((7 * ((pageSize) / 8)) + 1), bytes, 0, ((bytes.length) / 2));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // wrong source array positions / lengths
        try {
            segment.put(0, bytes, (-1), 1);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(0, bytes, (-1), ((bytes.length) + 1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(0, bytes, Integer.MIN_VALUE, bytes.length);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(0, bytes, Integer.MAX_VALUE, bytes.length);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.put(0, bytes, (((Integer.MAX_VALUE) - (bytes.length)) + 1), bytes.length);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // case where negative offset and negative index compensate each other
        try {
            segment.put((-2), bytes, (-1), ((bytes.length) / 2));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
    }

    @Test
    public void testBulkByteGetExceptions() {
        final MemorySegment segment = createSegment(pageSize);
        byte[] bytes = new byte[(pageSize) / 4];
        // wrong positions into memory segment
        try {
            segment.get((-1), bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get((-1), bytes, 4, 5);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(Integer.MIN_VALUE, bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(Integer.MIN_VALUE, bytes, 4, 5);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(pageSize, bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(pageSize, bytes, 6, 44);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get((((pageSize) - (bytes.length)) + 1), bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(((pageSize) - 5), bytes, 3, 6);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(Integer.MAX_VALUE, bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(Integer.MAX_VALUE, bytes, 10, 20);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get((((Integer.MAX_VALUE) - (bytes.length)) + 1), bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(((Integer.MAX_VALUE) - 11), bytes, 11, 11);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(((3 * ((pageSize) / 4)) + 1), bytes);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(((3 * ((pageSize) / 4)) + 2), bytes, 0, ((bytes.length) - 1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(((7 * ((pageSize) / 8)) + 1), bytes, 0, ((bytes.length) / 2));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // wrong source array positions / lengths
        try {
            segment.get(0, bytes, (-1), 1);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(0, bytes, (-1), ((bytes.length) + 1));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(0, bytes, Integer.MIN_VALUE, bytes.length);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(0, bytes, Integer.MAX_VALUE, bytes.length);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        try {
            segment.get(0, bytes, (((Integer.MAX_VALUE) - (bytes.length)) + 1), bytes.length);
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
        // case where negative offset and negative index compensate each other
        try {
            segment.get((-2), bytes, (-1), ((bytes.length) / 2));
            Assert.fail("IndexOutOfBoundsException expected");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IndexOutOfBoundsException));
        }
    }

    @Test
    public void testBulkByteAccess() {
        // test expected correct behavior with default offset / length
        {
            final MemorySegment segment = createSegment(pageSize);
            long seed = random.nextLong();
            random.setSeed(seed);
            byte[] src = new byte[(pageSize) / 8];
            for (int i = 0; i < 8; i++) {
                random.nextBytes(src);
                segment.put((i * ((pageSize) / 8)), src);
            }
            random.setSeed(seed);
            byte[] expected = new byte[(pageSize) / 8];
            byte[] actual = new byte[(pageSize) / 8];
            for (int i = 0; i < 8; i++) {
                random.nextBytes(expected);
                segment.get((i * ((pageSize) / 8)), actual);
                Assert.assertArrayEquals(expected, actual);
            }
        }
        // test expected correct behavior with specific offset / length
        {
            final MemorySegment segment = createSegment(pageSize);
            byte[] expected = new byte[pageSize];
            random.nextBytes(expected);
            for (int i = 0; i < 16; i++) {
                segment.put((i * ((pageSize) / 16)), expected, (i * ((pageSize) / 16)), ((pageSize) / 16));
            }
            byte[] actual = new byte[pageSize];
            for (int i = 0; i < 16; i++) {
                segment.get((i * ((pageSize) / 16)), actual, (i * ((pageSize) / 16)), ((pageSize) / 16));
            }
            Assert.assertArrayEquals(expected, actual);
        }
        // put segments of various lengths to various positions
        {
            final MemorySegment segment = createSegment(pageSize);
            byte[] expected = new byte[pageSize];
            for (int i = 0; i < 200; i++) {
                int numBytes = (random.nextInt(((pageSize) - 10))) + 1;
                int pos = random.nextInt((((pageSize) - numBytes) + 1));
                byte[] data = new byte[((random.nextInt(3)) + 1) * numBytes];
                int dataStartPos = random.nextInt((((data.length) - numBytes) + 1));
                random.nextBytes(data);
                // copy to the expected
                System.arraycopy(data, dataStartPos, expected, pos, numBytes);
                // put to the memory segment
                segment.put(pos, data, dataStartPos, numBytes);
            }
            byte[] validation = new byte[pageSize];
            segment.get(0, validation);
            Assert.assertArrayEquals(expected, validation);
        }
        // get segments with various contents
        {
            final MemorySegment segment = createSegment(pageSize);
            byte[] contents = new byte[pageSize];
            random.nextBytes(contents);
            segment.put(0, contents);
            for (int i = 0; i < 200; i++) {
                int numBytes = (random.nextInt(((pageSize) / 8))) + 1;
                int pos = random.nextInt((((pageSize) - numBytes) + 1));
                byte[] data = new byte[((random.nextInt(3)) + 1) * numBytes];
                int dataStartPos = random.nextInt((((data.length) - numBytes) + 1));
                segment.get(pos, data, dataStartPos, numBytes);
                byte[] expected = Arrays.copyOfRange(contents, pos, (pos + numBytes));
                byte[] validation = Arrays.copyOfRange(data, dataStartPos, (dataStartPos + numBytes));
                Assert.assertArrayEquals(expected, validation);
            }
        }
    }

    // ------------------------------------------------------------------------
    // Writing / Reading to/from DataInput / DataOutput
    // ------------------------------------------------------------------------
    @Test
    public void testDataInputOutput() throws IOException {
        MemorySegment seg = createSegment(pageSize);
        byte[] contents = new byte[pageSize];
        random.nextBytes(contents);
        seg.put(0, contents);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(pageSize);
        DataOutputStream out = new DataOutputStream(buffer);
        // write the segment in chunks into the stream
        int pos = 0;
        while (pos < (pageSize)) {
            int len = random.nextInt(200);
            len = Math.min(len, ((pageSize) - pos));
            seg.get(out, pos, len);
            pos += len;
        } 
        // verify that we wrote the same bytes
        byte[] result = buffer.toByteArray();
        Assert.assertArrayEquals(contents, result);
        // re-read the bytes into a new memory segment
        MemorySegment reader = createSegment(pageSize);
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(result));
        pos = 0;
        while (pos < (pageSize)) {
            int len = random.nextInt(200);
            len = Math.min(len, ((pageSize) - pos));
            reader.put(in, pos, len);
            pos += len;
        } 
        byte[] targetBuffer = new byte[pageSize];
        reader.get(0, targetBuffer);
        Assert.assertArrayEquals(contents, targetBuffer);
    }

    @Test
    public void testDataInputOutputOutOfBounds() {
        final int segmentSize = 52;
        // segment with random contents
        MemorySegment seg = createSegment(segmentSize);
        byte[] bytes = new byte[segmentSize];
        random.nextBytes(bytes);
        seg.put(0, bytes);
        // out of bounds when writing
        {
            DataOutputStream out = new DataOutputStream(new ByteArrayOutputStream());
            try {
                seg.get(out, (-1), (segmentSize / 2));
                Assert.fail("IndexOutOfBoundsException expected");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IndexOutOfBoundsException));
            }
            try {
                seg.get(out, segmentSize, (segmentSize / 2));
                Assert.fail("IndexOutOfBoundsException expected");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IndexOutOfBoundsException));
            }
            try {
                seg.get(out, (-segmentSize), (segmentSize / 2));
                Assert.fail("IndexOutOfBoundsException expected");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IndexOutOfBoundsException));
            }
            try {
                seg.get(out, Integer.MIN_VALUE, (segmentSize / 2));
                Assert.fail("IndexOutOfBoundsException expected");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IndexOutOfBoundsException));
            }
            try {
                seg.get(out, Integer.MAX_VALUE, (segmentSize / 2));
                Assert.fail("IndexOutOfBoundsException expected");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IndexOutOfBoundsException));
            }
        }
        // out of bounds when reading
        {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(new byte[segmentSize]));
            try {
                seg.put(in, (-1), (segmentSize / 2));
                Assert.fail("IndexOutOfBoundsException expected");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IndexOutOfBoundsException));
            }
            try {
                seg.put(in, segmentSize, (segmentSize / 2));
                Assert.fail("IndexOutOfBoundsException expected");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IndexOutOfBoundsException));
            }
            try {
                seg.put(in, (-segmentSize), (segmentSize / 2));
                Assert.fail("IndexOutOfBoundsException expected");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IndexOutOfBoundsException));
            }
            try {
                seg.put(in, Integer.MIN_VALUE, (segmentSize / 2));
                Assert.fail("IndexOutOfBoundsException expected");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IndexOutOfBoundsException));
            }
            try {
                seg.put(in, Integer.MAX_VALUE, (segmentSize / 2));
                Assert.fail("IndexOutOfBoundsException expected");
            } catch (Exception e) {
                Assert.assertTrue((e instanceof IndexOutOfBoundsException));
            }
        }
    }

    @Test
    public void testDataInputOutputStreamUnderflowOverflow() throws IOException {
        final int segmentSize = 1337;
        // segment with random contents
        MemorySegment seg = createSegment(segmentSize);
        byte[] bytes = new byte[segmentSize];
        random.nextBytes(bytes);
        seg.put(0, bytes);
        // a stream that we cannot fully write to
        DataOutputStream out = new DataOutputStream(new OutputStream() {
            int bytesSoFar = 0;

            @Override
            public void write(int b) throws IOException {
                (bytesSoFar)++;
                if ((bytesSoFar) > (segmentSize / 2)) {
                    throw new IOException("overflow");
                }
            }
        });
        // write the segment in chunks into the stream
        try {
            int pos = 0;
            while (pos < (pageSize)) {
                int len = random.nextInt((segmentSize / 10));
                len = Math.min(len, ((pageSize) - pos));
                seg.get(out, pos, len);
                pos += len;
            } 
            Assert.fail("Should fail with an IOException");
        } catch (IOException e) {
            // expected
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(new byte[segmentSize / 2]));
        try {
            int pos = 0;
            while (pos < (pageSize)) {
                int len = random.nextInt((segmentSize / 10));
                len = Math.min(len, ((pageSize) - pos));
                seg.put(in, pos, len);
                pos += len;
            } 
            Assert.fail("Should fail with an EOFException");
        } catch (EOFException e) {
            // expected
        }
    }

    // ------------------------------------------------------------------------
    // ByteBuffer Ops
    // ------------------------------------------------------------------------
    @Test
    public void testByteBufferGet() {
        testByteBufferGet(false);
        testByteBufferGet(true);
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testHeapByteBufferGetReadOnly() {
        testByteBufferGetReadOnly(false);
    }

    @Test(expected = ReadOnlyBufferException.class)
    public void testOffHeapByteBufferGetReadOnly() {
        testByteBufferGetReadOnly(true);
    }

    @Test
    public void testByteBufferPut() {
        testByteBufferPut(false);
        testByteBufferPut(true);
    }

    // ------------------------------------------------------------------------
    // ByteBuffer Ops on sliced byte buffers
    // ------------------------------------------------------------------------
    @Test
    public void testSlicedByteBufferGet() {
        testSlicedByteBufferGet(false);
        testSlicedByteBufferGet(true);
    }

    @Test
    public void testSlicedByteBufferPut() {
        testSlicedByteBufferPut(false);
        testSlicedByteBufferPut(true);
    }

    // ------------------------------------------------------------------------
    // ByteBuffer overflow / underflow and out of bounds
    // ------------------------------------------------------------------------
    @Test
    public void testByteBufferOutOfBounds() {
        final int bbCapacity = (pageSize) / 10;
        final int[] validOffsets = new int[]{ 0, 1, ((pageSize) / 10) * 9 };
        final int[] invalidOffsets = new int[]{ -1, (pageSize) + 1, -(pageSize), Integer.MAX_VALUE, Integer.MIN_VALUE };
        final int[] validLengths = new int[]{ 0, 1, bbCapacity, pageSize };
        final int[] invalidLengths = new int[]{ -1, -(pageSize), Integer.MAX_VALUE, Integer.MIN_VALUE };
        final MemorySegment seg = createSegment(pageSize);
        for (ByteBuffer bb : new ByteBuffer[]{ ByteBuffer.allocate(bbCapacity), ByteBuffer.allocateDirect(bbCapacity) }) {
            for (int off : validOffsets) {
                for (int len : invalidLengths) {
                    try {
                        seg.put(off, bb, len);
                        Assert.fail("should fail with an IndexOutOfBoundsException");
                    } catch (IndexOutOfBoundsException | BufferUnderflowException ignored) {
                    }
                    try {
                        seg.get(off, bb, len);
                        Assert.fail("should fail with an IndexOutOfBoundsException");
                    } catch (IndexOutOfBoundsException | BufferOverflowException ignored) {
                    }
                    // position/limit may not have changed
                    Assert.assertEquals(0, bb.position());
                    Assert.assertEquals(bb.capacity(), bb.limit());
                }
            }
            for (int off : invalidOffsets) {
                for (int len : validLengths) {
                    try {
                        seg.put(off, bb, len);
                        Assert.fail("should fail with an IndexOutOfBoundsException");
                    } catch (IndexOutOfBoundsException | BufferUnderflowException ignored) {
                    }
                    try {
                        seg.get(off, bb, len);
                        Assert.fail("should fail with an IndexOutOfBoundsException");
                    } catch (IndexOutOfBoundsException | BufferOverflowException ignored) {
                    }
                    // position/limit may not have changed
                    Assert.assertEquals(0, bb.position());
                    Assert.assertEquals(bb.capacity(), bb.limit());
                }
            }
            for (int off : validOffsets) {
                for (int len : validLengths) {
                    if ((off + len) > (pageSize)) {
                        try {
                            seg.put(off, bb, len);
                            Assert.fail("should fail with an IndexOutOfBoundsException");
                        } catch (IndexOutOfBoundsException | BufferUnderflowException ignored) {
                        }
                        try {
                            seg.get(off, bb, len);
                            Assert.fail("should fail with an IndexOutOfBoundsException");
                        } catch (IndexOutOfBoundsException | BufferOverflowException ignored) {
                        }
                        // position/limit may not have changed
                        Assert.assertEquals(0, bb.position());
                        Assert.assertEquals(bb.capacity(), bb.limit());
                    }
                }
            }
        }
    }

    @Test
    public void testByteBufferOverflowUnderflow() {
        final int bbCapacity = (pageSize) / 10;
        ByteBuffer bb = ByteBuffer.allocate(bbCapacity);
        MemorySegment seg = createSegment(pageSize);
        try {
            seg.get(((pageSize) / 5), bb, (((pageSize) / 10) + 2));
            Assert.fail("should fail with an exception");
        } catch (BufferOverflowException ignored) {
        }
        // position / limit should not have been modified
        Assert.assertEquals(0, bb.position());
        Assert.assertEquals(bb.capacity(), bb.limit());
        try {
            seg.put(((pageSize) / 5), bb, (((pageSize) / 10) + 2));
            Assert.fail("should fail with an exception");
        } catch (BufferUnderflowException ignored) {
        }
        // position / limit should not have been modified
        Assert.assertEquals(0, bb.position());
        Assert.assertEquals(bb.capacity(), bb.limit());
        int pos = (bb.capacity()) / 3;
        int limit = (2 * (bb.capacity())) / 3;
        bb.limit(limit);
        bb.position(pos);
        try {
            seg.get(20, bb, (((bb.capacity()) / 3) + 3));
            Assert.fail("should fail with an exception");
        } catch (BufferOverflowException ignored) {
        }
        // position / limit should not have been modified
        Assert.assertEquals(pos, bb.position());
        Assert.assertEquals(limit, bb.limit());
        try {
            seg.put(20, bb, (((bb.capacity()) / 3) + 3));
            Assert.fail("should fail with an exception");
        } catch (BufferUnderflowException ignored) {
        }
        // position / limit should not have been modified
        Assert.assertEquals(pos, bb.position());
        Assert.assertEquals(limit, bb.limit());
    }

    // ------------------------------------------------------------------------
    // Comparing and swapping
    // ------------------------------------------------------------------------
    @Test
    public void testCompareBytes() {
        final byte[] bytes1 = new byte[pageSize];
        final byte[] bytes2 = new byte[pageSize];
        final int stride = (pageSize) / 255;
        final int shift = 16666;
        for (int i = 0; i < (pageSize); i++) {
            byte val = ((byte) ((i / stride) & 255));
            bytes1[i] = val;
            if ((i + shift) < (bytes2.length)) {
                bytes2[(i + shift)] = val;
            }
        }
        MemorySegment seg1 = createSegment(pageSize);
        MemorySegment seg2 = createSegment(pageSize);
        seg1.put(0, bytes1);
        seg2.put(0, bytes2);
        for (int i = 0; i < 1000; i++) {
            int pos1 = random.nextInt(bytes1.length);
            int pos2 = random.nextInt(bytes2.length);
            int len = Math.min(Math.min(((bytes1.length) - pos1), ((bytes2.length) - pos2)), random.nextInt(((pageSize) / 50)));
            int cmp = seg1.compare(seg2, pos1, pos2, len);
            if (pos1 < (pos2 - shift)) {
                Assert.assertTrue((cmp <= 0));
            } else {
                Assert.assertTrue((cmp >= 0));
            }
        }
    }

    @Test
    public void testSwapBytes() {
        final int halfPageSize = (pageSize) / 2;
        final byte[] bytes1 = new byte[pageSize];
        final byte[] bytes2 = new byte[halfPageSize];
        Arrays.fill(bytes2, ((byte) (1)));
        MemorySegment seg1 = createSegment(pageSize);
        MemorySegment seg2 = createSegment(halfPageSize);
        seg1.put(0, bytes1);
        seg2.put(0, bytes2);
        // wap the second half of the first segment with the second segment
        int pos = 0;
        while (pos < halfPageSize) {
            int len = random.nextInt(((pageSize) / 40));
            len = Math.min(len, (halfPageSize - pos));
            seg1.swapBytes(new byte[len], seg2, (pos + halfPageSize), pos, len);
            pos += len;
        } 
        // the second segment should now be all zeros, the first segment should have one in its second half
        for (int i = 0; i < halfPageSize; i++) {
            Assert.assertEquals(((byte) (0)), seg1.get(i));
            Assert.assertEquals(((byte) (0)), seg2.get(i));
            Assert.assertEquals(((byte) (1)), seg1.get((i + halfPageSize)));
        }
    }

    @Test
    public void testCheckAgainstOverflowUnderflowOnRelease() {
        MemorySegment seg = createSegment(512);
        seg.free();
        // --- bytes (smallest type) ---
        try {
            seg.get(0);
            Assert.fail("Expecting an IllegalStateException");
        } catch (Exception e) {
            Assert.assertTrue(((e instanceof IllegalStateException) || (e instanceof NullPointerException)));
        }
        try {
            seg.get(Integer.MAX_VALUE);
            Assert.fail("Expecting an IllegalStateException");
        } catch (Exception e) {
            Assert.assertTrue(((e instanceof IllegalStateException) || (e instanceof NullPointerException)));
        }
        try {
            seg.get(Integer.MIN_VALUE);
            Assert.fail("Expecting an IllegalStateException");
        } catch (Exception e) {
            Assert.assertTrue(((e instanceof IllegalStateException) || (e instanceof NullPointerException)));
        }
        // --- longs (largest type) ---
        try {
            seg.getLong(0);
            Assert.fail("Expecting an IllegalStateException");
        } catch (Exception e) {
            Assert.assertTrue(((e instanceof IllegalStateException) || (e instanceof NullPointerException)));
        }
        try {
            seg.getLong(Integer.MAX_VALUE);
            Assert.fail("Expecting an IllegalStateException");
        } catch (Exception e) {
            Assert.assertTrue(((e instanceof IllegalStateException) || (e instanceof NullPointerException)));
        }
        try {
            seg.getLong(Integer.MIN_VALUE);
            Assert.fail("Expecting an IllegalStateException");
        } catch (Exception e) {
            Assert.assertTrue(((e instanceof IllegalStateException) || (e instanceof NullPointerException)));
        }
    }

    // ------------------------------------------------------------------------
    // Miscellaneous
    // ------------------------------------------------------------------------
    @Test
    public void testByteBufferWrapping() {
        MemorySegment seg = createSegment(1024);
        ByteBuffer buf1 = seg.wrap(13, 47);
        Assert.assertEquals(13, buf1.position());
        Assert.assertEquals(60, buf1.limit());
        Assert.assertEquals(47, buf1.remaining());
        ByteBuffer buf2 = seg.wrap(500, 267);
        Assert.assertEquals(500, buf2.position());
        Assert.assertEquals(767, buf2.limit());
        Assert.assertEquals(267, buf2.remaining());
        ByteBuffer buf3 = seg.wrap(0, 1024);
        Assert.assertEquals(0, buf3.position());
        Assert.assertEquals(1024, buf3.limit());
        Assert.assertEquals(1024, buf3.remaining());
        // verify that operations on the byte buffer are correctly reflected
        // in the memory segment
        buf3.order(ByteOrder.LITTLE_ENDIAN);
        buf3.putInt(112, 651797651);
        Assert.assertEquals(651797651, seg.getIntLittleEndian(112));
        buf3.order(ByteOrder.BIG_ENDIAN);
        buf3.putInt(187, 992288337);
        Assert.assertEquals(992288337, seg.getIntBigEndian(187));
        try {
            seg.wrap((-1), 20);
            Assert.fail("should throw an exception");
        } catch (IndexOutOfBoundsException | IllegalArgumentException ignored) {
        }
        try {
            seg.wrap(10, (-20));
            Assert.fail("should throw an exception");
        } catch (IndexOutOfBoundsException | IllegalArgumentException ignored) {
        }
        try {
            seg.wrap(10, 1024);
            Assert.fail("should throw an exception");
        } catch (IndexOutOfBoundsException | IllegalArgumentException ignored) {
        }
        // after freeing, no wrapping should be possible any more.
        seg.free();
        try {
            seg.wrap(13, 47);
            Assert.fail("should fail with an exception");
        } catch (IllegalStateException e) {
            // expected
        }
        // existing wraps should stay valid after freeing
        buf3.order(ByteOrder.LITTLE_ENDIAN);
        buf3.putInt(112, 651797651);
        Assert.assertEquals(651797651, buf3.getInt(112));
        buf3.order(ByteOrder.BIG_ENDIAN);
        buf3.putInt(187, 992288337);
        Assert.assertEquals(992288337, buf3.getInt(187));
    }

    @Test
    public void testOwner() {
        // a segment without an owner has a null owner
        Assert.assertNull(createSegment(64).getOwner());
        Object theOwner = new Object();
        MemorySegment seg = createSegment(64, theOwner);
        Assert.assertEquals(theOwner, seg.getOwner());
        // freeing must release the owner, to prevent leaks that prevent class unloading!
        seg.free();
        Assert.assertNotNull(seg.getOwner());
    }

    @Test
    public void testSizeAndFreeing() {
        // a segment without an owner has a null owner
        final int segmentSize = 651;
        MemorySegment seg = createSegment(segmentSize);
        Assert.assertEquals(segmentSize, seg.size());
        Assert.assertFalse(seg.isFreed());
        seg.free();
        Assert.assertTrue(seg.isFreed());
        Assert.assertEquals(segmentSize, seg.size());
    }
}

