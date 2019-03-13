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
package com.hazelcast.internal.serialization.impl;


import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.Bits;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.nio.ByteOrder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * UnsafeObjectDataOutput Tester.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class UnsafeObjectDataOutputTest {
    private InternalSerializationService mockSerializationService;

    private UnsafeObjectDataOutput out;

    @Test
    public void testWriteCharV() throws Exception {
        char expected = 100;
        out.writeChar(expected);
        char actual = Bits.readChar(out.buffer, 0, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteCharForPositionV() throws Exception {
        char expected = 100;
        out.writeChar(2, expected);
        char actual = Bits.readChar(out.buffer, 2, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteDoubleV() throws Exception {
        double expected = 1.1;
        out.writeDouble(expected);
        long theLong = Bits.readLong(out.buffer, 0, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        double actual = Double.longBitsToDouble(theLong);
        Assert.assertEquals(actual, expected, 0);
    }

    @Test
    public void testWriteDoubleForPositionV() throws Exception {
        double expected = 1.1;
        out.writeDouble(1, expected);
        long theLong = Bits.readLong(out.buffer, 1, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        double actual = Double.longBitsToDouble(theLong);
        Assert.assertEquals(actual, expected, 0);
    }

    @Test
    public void testWriteFloatV() throws Exception {
        float expected = 1.1F;
        out.writeFloat(expected);
        int val = Bits.readInt(out.buffer, 0, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        float actual = Float.intBitsToFloat(val);
        Assert.assertEquals(actual, expected, 0);
    }

    @Test
    public void testWriteFloatForPositionV() throws Exception {
        float expected = 1.1F;
        out.writeFloat(1, expected);
        int val = Bits.readInt(out.buffer, 1, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        float actual = Float.intBitsToFloat(val);
        Assert.assertEquals(actual, expected, 0);
    }

    @Test
    public void testWriteIntV() throws Exception {
        int expected = 100;
        out.writeInt(expected);
        int actual = Bits.readInt(out.buffer, 0, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteIntForPositionV() throws Exception {
        int expected = 100;
        out.writeInt(1, expected);
        int actual = Bits.readInt(out.buffer, 1, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteIntForVByteOrder() throws Exception {
        int expected = 100;
        out.writeInt(expected, ByteOrder.LITTLE_ENDIAN);
        out.writeInt(expected, ByteOrder.BIG_ENDIAN);
        int actual1 = Bits.readInt(out.buffer, 0, false);
        int actual2 = Bits.readInt(out.buffer, 4, true);
        Assert.assertEquals(actual1, expected);
        Assert.assertEquals(actual2, expected);
    }

    @Test
    public void testWriteIntForPositionVByteOrder() throws Exception {
        int expected = 100;
        out.writeInt(10, expected, ByteOrder.LITTLE_ENDIAN);
        out.writeInt(14, expected, ByteOrder.BIG_ENDIAN);
        int actual1 = Bits.readInt(out.buffer, 10, false);
        int actual2 = Bits.readInt(out.buffer, 14, true);
        Assert.assertEquals(actual1, expected);
        Assert.assertEquals(actual2, expected);
    }

    @Test
    public void testWriteLongV() throws Exception {
        long expected = 100;
        out.writeLong(expected);
        long actual = Bits.readLong(out.buffer, 0, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteLongForPositionV() throws Exception {
        long expected = 100;
        out.writeLong(2, expected);
        long actual = Bits.readLong(out.buffer, 2, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteLongForVByteOrder() throws Exception {
        long expected = 100;
        out.writeLong(expected, ByteOrder.LITTLE_ENDIAN);
        out.writeLong(expected, ByteOrder.BIG_ENDIAN);
        long actual1 = Bits.readLong(out.buffer, 0, false);
        long actual2 = Bits.readLong(out.buffer, 8, true);
        Assert.assertEquals(actual1, expected);
        Assert.assertEquals(actual2, expected);
    }

    @Test
    public void testWriteLongForPositionVByteOrder() throws Exception {
        long expected = 100;
        out.writeLong(10, expected, ByteOrder.LITTLE_ENDIAN);
        out.writeLong(18, expected, ByteOrder.BIG_ENDIAN);
        long actual1 = Bits.readLong(out.buffer, 10, false);
        long actual2 = Bits.readLong(out.buffer, 18, true);
        Assert.assertEquals(actual1, expected);
        Assert.assertEquals(actual2, expected);
    }

    @Test
    public void testWriteShortV() throws Exception {
        short expected = 100;
        out.writeShort(expected);
        short actual = Bits.readShort(out.buffer, 0, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteShortForPositionV() throws Exception {
        short expected = 100;
        out.writeShort(1, expected);
        short actual = Bits.readShort(out.buffer, 1, ((ByteOrder.nativeOrder()) == (ByteOrder.BIG_ENDIAN)));
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteShortForVByteOrder() throws Exception {
        short expected = 100;
        out.writeShort(expected, ByteOrder.LITTLE_ENDIAN);
        out.writeShort(expected, ByteOrder.BIG_ENDIAN);
        short actual1 = Bits.readShort(out.buffer, 0, false);
        short actual2 = Bits.readShort(out.buffer, 2, true);
        Assert.assertEquals(actual1, expected);
        Assert.assertEquals(actual2, expected);
    }

    @Test
    public void testWriteShortForPositionVByteOrder() throws Exception {
        short expected = 100;
        out.writeShort(1, expected, ByteOrder.LITTLE_ENDIAN);
        out.writeShort(3, expected, ByteOrder.BIG_ENDIAN);
        short actual1 = Bits.readShort(out.buffer, 1, false);
        short actual2 = Bits.readShort(out.buffer, 3, true);
        Assert.assertEquals(actual1, expected);
        Assert.assertEquals(actual2, expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckAvailable_negativePos() throws Exception {
        out.writeInt((-1), 1);
    }

    @Test(expected = IOException.class)
    public void testCheckAvailable_noSpaceLeft() throws Exception {
        out.writeInt(out.buffer.length, 1);
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertNotNull(out.toString());
    }
}

