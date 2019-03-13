/**
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.zxing.qrcode.encoder;


import com.google.zxing.common.BitArray;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author satorux@google.com (Satoru Takabayashi) - creator
 * @author dswitkin@google.com (Daniel Switkin) - ported from C++
 */
public final class BitVectorTestCase extends Assert {
    @Test
    public void testAppendBit() {
        BitArray v = new BitArray();
        Assert.assertEquals(0, v.getSizeInBytes());
        // 1
        v.appendBit(true);
        Assert.assertEquals(1, v.getSize());
        Assert.assertEquals(2147483648L, BitVectorTestCase.getUnsignedInt(v, 0));
        // 10
        v.appendBit(false);
        Assert.assertEquals(2, v.getSize());
        Assert.assertEquals(2147483648L, BitVectorTestCase.getUnsignedInt(v, 0));
        // 101
        v.appendBit(true);
        Assert.assertEquals(3, v.getSize());
        Assert.assertEquals(2684354560L, BitVectorTestCase.getUnsignedInt(v, 0));
        // 1010
        v.appendBit(false);
        Assert.assertEquals(4, v.getSize());
        Assert.assertEquals(2684354560L, BitVectorTestCase.getUnsignedInt(v, 0));
        // 10101
        v.appendBit(true);
        Assert.assertEquals(5, v.getSize());
        Assert.assertEquals(2818572288L, BitVectorTestCase.getUnsignedInt(v, 0));
        // 101010
        v.appendBit(false);
        Assert.assertEquals(6, v.getSize());
        Assert.assertEquals(2818572288L, BitVectorTestCase.getUnsignedInt(v, 0));
        // 1010101
        v.appendBit(true);
        Assert.assertEquals(7, v.getSize());
        Assert.assertEquals(2852126720L, BitVectorTestCase.getUnsignedInt(v, 0));
        // 10101010
        v.appendBit(false);
        Assert.assertEquals(8, v.getSize());
        Assert.assertEquals(2852126720L, BitVectorTestCase.getUnsignedInt(v, 0));
        // 10101010 1
        v.appendBit(true);
        Assert.assertEquals(9, v.getSize());
        Assert.assertEquals(2860515328L, BitVectorTestCase.getUnsignedInt(v, 0));
        // 10101010 10
        v.appendBit(false);
        Assert.assertEquals(10, v.getSize());
        Assert.assertEquals(2860515328L, BitVectorTestCase.getUnsignedInt(v, 0));
    }

    @Test
    public void testAppendBits() {
        BitArray v = new BitArray();
        v.appendBits(1, 1);
        Assert.assertEquals(1, v.getSize());
        Assert.assertEquals(2147483648L, BitVectorTestCase.getUnsignedInt(v, 0));
        v = new BitArray();
        v.appendBits(255, 8);
        Assert.assertEquals(8, v.getSize());
        Assert.assertEquals(4278190080L, BitVectorTestCase.getUnsignedInt(v, 0));
        v = new BitArray();
        v.appendBits(4087, 12);
        Assert.assertEquals(12, v.getSize());
        Assert.assertEquals(4285530112L, BitVectorTestCase.getUnsignedInt(v, 0));
    }

    @Test
    public void testNumBytes() {
        BitArray v = new BitArray();
        Assert.assertEquals(0, v.getSizeInBytes());
        v.appendBit(false);
        // 1 bit was added in the vector, so 1 byte should be consumed.
        Assert.assertEquals(1, v.getSizeInBytes());
        v.appendBits(0, 7);
        Assert.assertEquals(1, v.getSizeInBytes());
        v.appendBits(0, 8);
        Assert.assertEquals(2, v.getSizeInBytes());
        v.appendBits(0, 1);
        // We now have 17 bits, so 3 bytes should be consumed.
        Assert.assertEquals(3, v.getSizeInBytes());
    }

    @Test
    public void testAppendBitVector() {
        BitArray v1 = new BitArray();
        v1.appendBits(190, 8);
        BitArray v2 = new BitArray();
        v2.appendBits(239, 8);
        v1.appendBitArray(v2);
        // beef = 1011 1110 1110 1111
        Assert.assertEquals(" X.XXXXX. XXX.XXXX", v1.toString());
    }

    @Test
    public void testXOR() {
        BitArray v1 = new BitArray();
        v1.appendBits(1431677610, 32);
        BitArray v2 = new BitArray();
        v2.appendBits(-1431677611, 32);
        v1.xor(v2);
        Assert.assertEquals(4294967295L, BitVectorTestCase.getUnsignedInt(v1, 0));
    }

    @Test
    public void testXOR2() {
        BitArray v1 = new BitArray();
        v1.appendBits(42, 7);// 010 1010

        BitArray v2 = new BitArray();
        v2.appendBits(85, 7);// 101 0101

        v1.xor(v2);
        Assert.assertEquals(4261412864L, BitVectorTestCase.getUnsignedInt(v1, 0));// 1111 1110

    }

    @Test
    public void testAt() {
        BitArray v = new BitArray();
        v.appendBits(57005, 16);// 1101 1110 1010 1101

        Assert.assertTrue(v.get(0));
        Assert.assertTrue(v.get(1));
        Assert.assertFalse(v.get(2));
        Assert.assertTrue(v.get(3));
        Assert.assertTrue(v.get(4));
        Assert.assertTrue(v.get(5));
        Assert.assertTrue(v.get(6));
        Assert.assertFalse(v.get(7));
        Assert.assertTrue(v.get(8));
        Assert.assertFalse(v.get(9));
        Assert.assertTrue(v.get(10));
        Assert.assertFalse(v.get(11));
        Assert.assertTrue(v.get(12));
        Assert.assertTrue(v.get(13));
        Assert.assertFalse(v.get(14));
        Assert.assertTrue(v.get(15));
    }

    @Test
    public void testToString() {
        BitArray v = new BitArray();
        v.appendBits(57005, 16);// 1101 1110 1010 1101

        Assert.assertEquals(" XX.XXXX. X.X.XX.X", v.toString());
    }
}

