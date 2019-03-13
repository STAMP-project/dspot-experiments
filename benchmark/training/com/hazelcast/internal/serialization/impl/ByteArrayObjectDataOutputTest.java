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
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


/**
 * ByteArrayObjectDataOutput Tester.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ByteArrayObjectDataOutputTest {
    private InternalSerializationService mockSerializationService;

    private ByteArrayObjectDataOutput out;

    private static final byte[] TEST_DATA = new byte[]{ 1, 2, 3 };

    @Test
    public void testWriteForPositionB() {
        out.write(1, 5);
        Assert.assertEquals(5, out.buffer[1]);
    }

    @Test
    public void testWriteForBOffLen() {
        byte[] zeroBytes = new byte[20];
        out.write(zeroBytes, 0, 20);
        byte[] bytes = Arrays.copyOfRange(out.buffer, 0, 20);
        Assert.assertArrayEquals(zeroBytes, bytes);
        Assert.assertEquals(20, out.pos);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testWriteForBOffLen_negativeOff() {
        out.write(ByteArrayObjectDataOutputTest.TEST_DATA, (-1), 3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testWriteForBOffLen_negativeLen() {
        out.write(ByteArrayObjectDataOutputTest.TEST_DATA, 0, (-3));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testWriteForBOffLen_OffLenHigherThenSize() {
        out.write(ByteArrayObjectDataOutputTest.TEST_DATA, 0, (-3));
    }

    @Test(expected = NullPointerException.class)
    public void testWrite_whenBufferIsNull() {
        out.write(null, 0, 0);
    }

    @Test
    public void testWriteBooleanForPositionV() throws Exception {
        out.writeBoolean(0, true);
        out.writeBoolean(1, false);
        Assert.assertEquals(1, out.buffer[0]);
        Assert.assertEquals(0, out.buffer[1]);
    }

    @Test
    public void testWriteByteForPositionV() throws Exception {
        out.writeByte(0, 10);
        Assert.assertEquals(10, out.buffer[0]);
    }

    @Test
    public void testWriteDoubleForPositionV() throws Exception {
        double v = 1.1;
        out.writeDouble(1, v);
        long theLong = Double.doubleToLongBits(v);
        long readLongB = Bits.readLongB(out.buffer, 1);
        Assert.assertEquals(theLong, readLongB);
    }

    @Test
    public void testWriteDoubleForVByteOrder() throws Exception {
        double v = 1.1;
        out.writeDouble(v, ByteOrder.LITTLE_ENDIAN);
        long theLong = Double.doubleToLongBits(v);
        long readLongB = Bits.readLongL(out.buffer, 0);
        Assert.assertEquals(theLong, readLongB);
    }

    @Test
    public void testWriteDoubleForPositionVByteOrder() throws Exception {
        double v = 1.1;
        out.writeDouble(1, v, ByteOrder.LITTLE_ENDIAN);
        long theLong = Double.doubleToLongBits(v);
        long readLongB = Bits.readLongL(out.buffer, 1);
        Assert.assertEquals(theLong, readLongB);
    }

    @Test
    public void testWriteFloatV() throws Exception {
        float v = 1.1F;
        out.writeFloat(v);
        int expected = Float.floatToIntBits(v);
        int actual = Bits.readIntB(out.buffer, 0);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteFloatForPositionV() throws Exception {
        float v = 1.1F;
        out.writeFloat(1, v);
        int expected = Float.floatToIntBits(v);
        int actual = Bits.readIntB(out.buffer, 1);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteFloatForVByteOrder() throws Exception {
        float v = 1.1F;
        out.writeFloat(v, ByteOrder.LITTLE_ENDIAN);
        int expected = Float.floatToIntBits(v);
        int actual = Bits.readIntL(out.buffer, 0);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteFloatForPositionVByteOrder() throws Exception {
        float v = 1.1F;
        out.writeFloat(1, v, ByteOrder.LITTLE_ENDIAN);
        int expected = Float.floatToIntBits(v);
        int actual = Bits.readIntL(out.buffer, 1);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteIntV() throws Exception {
        int expected = 100;
        out.writeInt(expected);
        int actual = Bits.readIntB(out.buffer, 0);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteIntForPositionV() throws Exception {
        int expected = 100;
        out.writeInt(1, expected);
        int actual = Bits.readIntB(out.buffer, 1);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteIntForVByteOrder() throws Exception {
        int expected = 100;
        out.writeInt(expected, ByteOrder.LITTLE_ENDIAN);
        int actual = Bits.readIntL(out.buffer, 0);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteIntForPositionVByteOrder() throws Exception {
        int expected = 100;
        out.writeInt(2, expected, ByteOrder.LITTLE_ENDIAN);
        int actual = Bits.readIntL(out.buffer, 2);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteLongV() throws Exception {
        long expected = 100;
        out.writeLong(expected);
        long actual = Bits.readLongB(out.buffer, 0);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteLongForPositionV() throws Exception {
        long expected = 100;
        out.writeLong(2, expected);
        long actual = Bits.readLongB(out.buffer, 2);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteLongForVByteOrder() throws Exception {
        long expected = 100;
        out.writeLong(2, expected, ByteOrder.LITTLE_ENDIAN);
        long actual = Bits.readLongL(out.buffer, 2);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteLongForPositionVByteOrder() throws Exception {
        long expected = 100;
        out.writeLong(2, expected, ByteOrder.LITTLE_ENDIAN);
        long actual = Bits.readLongL(out.buffer, 2);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteShortV() throws Exception {
        short expected = 100;
        out.writeShort(expected);
        short actual = Bits.readShortB(out.buffer, 0);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteShortForPositionV() throws Exception {
        short expected = 100;
        out.writeShort(2, expected);
        short actual = Bits.readShortB(out.buffer, 2);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteShortForVByteOrder() throws Exception {
        short expected = 100;
        out.writeShort(2, expected, ByteOrder.LITTLE_ENDIAN);
        short actual = Bits.readShortL(out.buffer, 2);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteShortForPositionVByteOrder() throws Exception {
        short expected = 100;
        out.writeShort(2, expected, ByteOrder.LITTLE_ENDIAN);
        short actual = Bits.readShortL(out.buffer, 2);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testWriteShortForPositionVAndByteOrder() throws IOException {
        short expected = 42;
        out.pos = 2;
        out.writeShort(42, ByteOrder.LITTLE_ENDIAN);
        short actual = Bits.readShortL(out.buffer, 2);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testEnsureAvailable() {
        out.buffer = null;
        out.ensureAvailable(5);
        Assert.assertEquals(10, out.buffer.length);
    }

    @Test
    public void testEnsureAvailable_smallLen() {
        out.buffer = null;
        out.ensureAvailable(1);
        Assert.assertEquals(10, out.buffer.length);
    }

    @Test
    public void testWriteObject() throws Exception {
        out.writeObject("TEST");
        Mockito.verify(mockSerializationService).writeObject(out, "TEST");
    }

    @Test
    public void testPosition() {
        out.pos = 21;
        Assert.assertEquals(21, out.position());
    }

    @Test
    public void testPositionNewPos() {
        out.position(1);
        Assert.assertEquals(1, out.pos);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionNewPos_negativePos() {
        out.position((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionNewPos_highPos() {
        out.position(((out.buffer.length) + 1));
    }

    @Test
    public void testAvailable() {
        int available = out.available();
        out.buffer = null;
        int availableWhenBufferNull = out.available();
        Assert.assertEquals(10, available);
        Assert.assertEquals(0, availableWhenBufferNull);
    }

    @Test
    public void testToByteArray() {
        byte[] arrayWhenPosZero = out.toByteArray();
        out.buffer = null;
        byte[] arrayWhenBufferNull = out.toByteArray();
        Assert.assertArrayEquals(new byte[0], arrayWhenPosZero);
        Assert.assertArrayEquals(new byte[0], arrayWhenBufferNull);
    }

    @Test
    public void testClear() {
        out.clear();
        Assert.assertEquals(0, out.position());
        Assert.assertEquals(10, out.available());
    }

    @Test
    public void testClear_bufferNull() {
        out.buffer = null;
        out.clear();
        Assert.assertNull(out.buffer);
    }

    @Test
    public void testClear_bufferLen_lt_initX8() {
        out.ensureAvailable((10 * 10));
        out.clear();
        Assert.assertEquals((10 * 8), out.available());
    }

    @Test
    public void testClose() {
        out.close();
        Assert.assertEquals(0, out.position());
        Assert.assertNull(out.buffer);
    }

    @Test
    public void testGetByteOrder() {
        ByteArrayObjectDataOutput outLE = new ByteArrayObjectDataOutput(10, mockSerializationService, ByteOrder.LITTLE_ENDIAN);
        ByteArrayObjectDataOutput outBE = new ByteArrayObjectDataOutput(10, mockSerializationService, ByteOrder.BIG_ENDIAN);
        Assert.assertEquals(ByteOrder.LITTLE_ENDIAN, outLE.getByteOrder());
        Assert.assertEquals(ByteOrder.BIG_ENDIAN, outBE.getByteOrder());
    }

    @Test
    public void testToString() {
        Assert.assertNotNull(out.toString());
    }
}

