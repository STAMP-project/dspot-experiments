/**
 * Copyright 2007 ZXing authors
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
package com.google.zxing.common;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sean Owen
 */
public final class BitArrayTestCase extends Assert {
    @Test
    public void testGetSet() {
        BitArray array = new BitArray(33);
        for (int i = 0; i < 33; i++) {
            Assert.assertFalse(array.get(i));
            array.set(i);
            Assert.assertTrue(array.get(i));
        }
    }

    @Test
    public void testGetNextSet1() {
        BitArray array = new BitArray(32);
        for (int i = 0; i < (array.getSize()); i++) {
            Assert.assertEquals(String.valueOf(i), 32, array.getNextSet(i));
        }
        array = new BitArray(33);
        for (int i = 0; i < (array.getSize()); i++) {
            Assert.assertEquals(String.valueOf(i), 33, array.getNextSet(i));
        }
    }

    @Test
    public void testGetNextSet2() {
        BitArray array = new BitArray(33);
        array.set(31);
        for (int i = 0; i < (array.getSize()); i++) {
            Assert.assertEquals(String.valueOf(i), (i <= 31 ? 31 : 33), array.getNextSet(i));
        }
        array = new BitArray(33);
        array.set(32);
        for (int i = 0; i < (array.getSize()); i++) {
            Assert.assertEquals(String.valueOf(i), 32, array.getNextSet(i));
        }
    }

    @Test
    public void testGetNextSet3() {
        BitArray array = new BitArray(63);
        array.set(31);
        array.set(32);
        for (int i = 0; i < (array.getSize()); i++) {
            int expected;
            if (i <= 31) {
                expected = 31;
            } else
                if (i == 32) {
                    expected = 32;
                } else {
                    expected = 63;
                }

            Assert.assertEquals(String.valueOf(i), expected, array.getNextSet(i));
        }
    }

    @Test
    public void testGetNextSet4() {
        BitArray array = new BitArray(63);
        array.set(33);
        array.set(40);
        for (int i = 0; i < (array.getSize()); i++) {
            int expected;
            if (i <= 33) {
                expected = 33;
            } else
                if (i <= 40) {
                    expected = 40;
                } else {
                    expected = 63;
                }

            Assert.assertEquals(String.valueOf(i), expected, array.getNextSet(i));
        }
    }

    @Test
    public void testGetNextSet5() {
        Random r = new Random(-559038737);
        for (int i = 0; i < 10; i++) {
            BitArray array = new BitArray((1 + (r.nextInt(100))));
            int numSet = r.nextInt(20);
            for (int j = 0; j < numSet; j++) {
                array.set(r.nextInt(array.getSize()));
            }
            int numQueries = r.nextInt(20);
            for (int j = 0; j < numQueries; j++) {
                int query = r.nextInt(array.getSize());
                int expected = query;
                while ((expected < (array.getSize())) && (!(array.get(expected)))) {
                    expected++;
                } 
                int actual = array.getNextSet(query);
                Assert.assertEquals(expected, actual);
            }
        }
    }

    @Test
    public void testSetBulk() {
        BitArray array = new BitArray(64);
        array.setBulk(32, -65536);
        for (int i = 0; i < 48; i++) {
            Assert.assertFalse(array.get(i));
        }
        for (int i = 48; i < 64; i++) {
            Assert.assertTrue(array.get(i));
        }
    }

    @Test
    public void testSetRange() {
        BitArray array = new BitArray(64);
        array.setRange(28, 36);
        Assert.assertFalse(array.get(27));
        for (int i = 28; i < 36; i++) {
            Assert.assertTrue(array.get(i));
        }
        Assert.assertFalse(array.get(36));
    }

    @Test
    public void testClear() {
        BitArray array = new BitArray(32);
        for (int i = 0; i < 32; i++) {
            array.set(i);
        }
        array.clear();
        for (int i = 0; i < 32; i++) {
            Assert.assertFalse(array.get(i));
        }
    }

    @Test
    public void testFlip() {
        BitArray array = new BitArray(32);
        Assert.assertFalse(array.get(5));
        array.flip(5);
        Assert.assertTrue(array.get(5));
        array.flip(5);
        Assert.assertFalse(array.get(5));
    }

    @Test
    public void testGetArray() {
        BitArray array = new BitArray(64);
        array.set(0);
        array.set(63);
        int[] ints = array.getBitArray();
        Assert.assertEquals(1, ints[0]);
        Assert.assertEquals(Integer.MIN_VALUE, ints[1]);
    }

    @Test
    public void testIsRange() {
        BitArray array = new BitArray(64);
        Assert.assertTrue(array.isRange(0, 64, false));
        Assert.assertFalse(array.isRange(0, 64, true));
        array.set(32);
        Assert.assertTrue(array.isRange(32, 33, true));
        array.set(31);
        Assert.assertTrue(array.isRange(31, 33, true));
        array.set(34);
        Assert.assertFalse(array.isRange(31, 35, true));
        for (int i = 0; i < 31; i++) {
            array.set(i);
        }
        Assert.assertTrue(array.isRange(0, 33, true));
        for (int i = 33; i < 64; i++) {
            array.set(i);
        }
        Assert.assertTrue(array.isRange(0, 64, true));
        Assert.assertFalse(array.isRange(0, 64, false));
    }

    @Test
    public void reverseAlgorithmTest() {
        int[] oldBits = new int[]{ 128, 256, 512, 6453324, 50934953 };
        for (int size = 1; size < 160; size++) {
            int[] newBitsOriginal = BitArrayTestCase.reverseOriginal(oldBits.clone(), size);
            BitArray newBitArray = new BitArray(oldBits.clone(), size);
            newBitArray.reverse();
            int[] newBitsNew = newBitArray.getBitArray();
            Assert.assertTrue(BitArrayTestCase.arraysAreEqual(newBitsOriginal, newBitsNew, ((size / 32) + 1)));
        }
    }

    @Test
    public void testClone() {
        BitArray array = new BitArray(32);
        array.clone().set(0);
        Assert.assertFalse(array.get(0));
    }

    @Test
    public void testEquals() {
        BitArray a = new BitArray(32);
        BitArray b = new BitArray(32);
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        Assert.assertNotEquals(a, new BitArray(31));
        a.set(16);
        Assert.assertNotEquals(a, b);
        Assert.assertNotEquals(a.hashCode(), b.hashCode());
        b.set(16);
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
    }
}

