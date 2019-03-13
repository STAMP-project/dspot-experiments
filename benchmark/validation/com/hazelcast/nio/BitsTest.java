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
package com.hazelcast.nio;


import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class BitsTest {
    private static Random random = new Random();

    private static byte[] readBuffer;

    @Test
    public void testReadCharBigEndian() {
        testReadChar(true);
    }

    @Test
    public void testReadCharLittleEndian() {
        testReadChar(false);
    }

    @Test
    public void testReadShortBigEndian() {
        testReadShort(true);
    }

    @Test
    public void testReadShortLittleEndian() {
        testReadShort(false);
    }

    @Test
    public void testReadIntBigEndian() {
        testReadInt(true);
    }

    @Test
    public void testReadIntLittleEndian() {
        testReadInt(false);
    }

    @Test
    public void testReadLongBigEndian() {
        testReadLong(true);
    }

    @Test
    public void testReadLongLittleEndian() {
        testReadLong(false);
    }

    @Test
    public void testWriteCharBigEndian() {
        testWriteChar(true);
    }

    @Test
    public void testWriteCharLittleEndian() {
        testWriteChar(false);
    }

    @Test
    public void testWriteShortBigEndian() {
        testWriteShort(true);
    }

    @Test
    public void testWriteShortLittleEndian() {
        testWriteShort(false);
    }

    @Test
    public void testWriteIntBigEndian() {
        testWriteInt(true);
    }

    @Test
    public void testWriteIntLittleEndian() {
        testWriteInt(false);
    }

    @Test
    public void testWriteLongBigEndian() {
        testWriteLong(true);
    }

    @Test
    public void testWriteLongLittleEndian() {
        testWriteLong(false);
    }

    @Test
    public void testWriteUtf8Char() {
        byte[] bytes = new byte[3];
        char c1 = 16;// 1 byte

        char c2 = 128;// 2 byte

        char c3 = 2048;// 3 byte

        Assert.assertEquals(1, Bits.writeUtf8Char(bytes, 0, c1));
        Assert.assertEquals(2, Bits.writeUtf8Char(bytes, 0, c2));
        Assert.assertEquals(3, Bits.writeUtf8Char(bytes, 0, c3));
    }

    @Test
    public void testReadUtf8Char() throws IOException {
        byte[] bytes = new byte[6];
        char c0 = 16;// 1 byte

        char c1 = 128;// 2 byte

        char c2 = 2048;// 3 byte

        Bits.writeUtf8Char(bytes, 0, c0);
        Bits.writeUtf8Char(bytes, 1, c1);
        Bits.writeUtf8Char(bytes, 3, c2);
        char[] chars = new char[3];
        Bits.readUtf8Char(bytes, 0, chars, 0);
        Bits.readUtf8Char(bytes, 1, chars, 1);
        Bits.readUtf8Char(bytes, 3, chars, 2);
        Assert.assertEquals(c0, chars[0]);
        Assert.assertEquals(c1, chars[1]);
        Assert.assertEquals(c2, chars[2]);
    }

    @Test(expected = UTFDataFormatException.class)
    public void testReadUtf8CharMalformedBytes() throws IOException {
        byte[] bytes = new byte[]{ ((byte) (255)) };
        char[] chars = new char[1];
        Bits.readUtf8Char(bytes, 0, chars, 0);
    }

    @Test
    public void testSetBitByte() throws Exception {
        byte b = 110;
        b = Bits.setBit(b, 0);
        Assert.assertEquals(111, b);
        b = Bits.setBit(b, 7);
        Assert.assertTrue((b < 0));
    }

    @Test
    public void testClearBitByte() throws Exception {
        byte b = 111;
        b = Bits.clearBit(b, 0);
        Assert.assertEquals(110, b);
        b = -111;
        b = Bits.clearBit(b, 7);
        Assert.assertTrue((b > 0));
    }

    @Test
    public void testInvertBitByte() throws Exception {
        byte b = -111;
        b = Bits.invertBit(b, 7);
        Assert.assertTrue((b > 0));
    }

    @Test
    public void testSetBitInteger() throws Exception {
        int b = 110;
        b = Bits.setBit(b, 0);
        Assert.assertEquals(111, b);
        b = Bits.setBit(b, 31);
        Assert.assertTrue((b < 0));
    }

    @Test
    public void testClearBitInteger() throws Exception {
        int b = 111;
        b = Bits.clearBit(b, 0);
        Assert.assertEquals(110, b);
        b = -111;
        b = Bits.clearBit(b, 31);
        Assert.assertTrue((b > 0));
    }

    @Test
    public void testInvertBitInteger() throws Exception {
        int b = -111111;
        b = Bits.invertBit(b, 31);
        Assert.assertTrue((b > 0));
    }

    @Test
    public void testIsBitSet() throws Exception {
        Assert.assertFalse(Bits.isBitSet(123, 31));
        Assert.assertTrue(Bits.isBitSet((-123), 31));
        Assert.assertFalse(Bits.isBitSet(222, 0));
        Assert.assertTrue(Bits.isBitSet(221, 0));
    }

    @Test
    public void testCombineToInt() throws Exception {
        short x = ((short) (BitsTest.random.nextInt()));
        short y = ((short) (BitsTest.random.nextInt()));
        int k = Bits.combineToInt(x, y);
        Assert.assertEquals(x, Bits.extractShort(k, false));
        Assert.assertEquals(y, Bits.extractShort(k, true));
    }

    @Test
    public void testCombineToLong() throws Exception {
        int x = BitsTest.random.nextInt();
        int y = BitsTest.random.nextInt();
        long k = Bits.combineToLong(x, y);
        Assert.assertEquals(x, Bits.extractInt(k, false));
        Assert.assertEquals(y, Bits.extractInt(k, true));
    }
}

