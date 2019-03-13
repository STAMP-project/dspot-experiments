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
package org.apache.flink.util;


import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link MathUtils}.
 */
public class MathUtilTest {
    @Test
    public void testLog2Computation() {
        Assert.assertEquals(0, MathUtils.log2floor(1));
        Assert.assertEquals(1, MathUtils.log2floor(2));
        Assert.assertEquals(1, MathUtils.log2floor(3));
        Assert.assertEquals(2, MathUtils.log2floor(4));
        Assert.assertEquals(2, MathUtils.log2floor(5));
        Assert.assertEquals(2, MathUtils.log2floor(7));
        Assert.assertEquals(3, MathUtils.log2floor(8));
        Assert.assertEquals(3, MathUtils.log2floor(9));
        Assert.assertEquals(4, MathUtils.log2floor(16));
        Assert.assertEquals(4, MathUtils.log2floor(17));
        Assert.assertEquals(13, MathUtils.log2floor(((1 << 13) + 1)));
        Assert.assertEquals(30, MathUtils.log2floor(Integer.MAX_VALUE));
        Assert.assertEquals(31, MathUtils.log2floor((-1)));
        try {
            MathUtils.log2floor(0);
            Assert.fail();
        } catch (ArithmeticException ignored) {
        }
    }

    @Test
    public void testRoundDownToPowerOf2() {
        Assert.assertEquals(0, MathUtils.roundDownToPowerOf2(0));
        Assert.assertEquals(1, MathUtils.roundDownToPowerOf2(1));
        Assert.assertEquals(2, MathUtils.roundDownToPowerOf2(2));
        Assert.assertEquals(2, MathUtils.roundDownToPowerOf2(3));
        Assert.assertEquals(4, MathUtils.roundDownToPowerOf2(4));
        Assert.assertEquals(4, MathUtils.roundDownToPowerOf2(5));
        Assert.assertEquals(4, MathUtils.roundDownToPowerOf2(6));
        Assert.assertEquals(4, MathUtils.roundDownToPowerOf2(7));
        Assert.assertEquals(8, MathUtils.roundDownToPowerOf2(8));
        Assert.assertEquals(8, MathUtils.roundDownToPowerOf2(9));
        Assert.assertEquals(8, MathUtils.roundDownToPowerOf2(15));
        Assert.assertEquals(16, MathUtils.roundDownToPowerOf2(16));
        Assert.assertEquals(16, MathUtils.roundDownToPowerOf2(17));
        Assert.assertEquals(16, MathUtils.roundDownToPowerOf2(31));
        Assert.assertEquals(32, MathUtils.roundDownToPowerOf2(32));
        Assert.assertEquals(32, MathUtils.roundDownToPowerOf2(33));
        Assert.assertEquals(32, MathUtils.roundDownToPowerOf2(42));
        Assert.assertEquals(32, MathUtils.roundDownToPowerOf2(63));
        Assert.assertEquals(64, MathUtils.roundDownToPowerOf2(64));
        Assert.assertEquals(64, MathUtils.roundDownToPowerOf2(125));
        Assert.assertEquals(16384, MathUtils.roundDownToPowerOf2(25654));
        Assert.assertEquals(33554432, MathUtils.roundDownToPowerOf2(34366363));
        Assert.assertEquals(33554432, MathUtils.roundDownToPowerOf2(63463463));
        Assert.assertEquals(1073741824, MathUtils.roundDownToPowerOf2(1852987883));
        Assert.assertEquals(1073741824, MathUtils.roundDownToPowerOf2(Integer.MAX_VALUE));
    }

    @Test
    public void testRoundUpToPowerOf2() {
        Assert.assertEquals(0, MathUtils.roundUpToPowerOfTwo(0));
        Assert.assertEquals(1, MathUtils.roundUpToPowerOfTwo(1));
        Assert.assertEquals(2, MathUtils.roundUpToPowerOfTwo(2));
        Assert.assertEquals(4, MathUtils.roundUpToPowerOfTwo(3));
        Assert.assertEquals(4, MathUtils.roundUpToPowerOfTwo(4));
        Assert.assertEquals(8, MathUtils.roundUpToPowerOfTwo(5));
        Assert.assertEquals(8, MathUtils.roundUpToPowerOfTwo(6));
        Assert.assertEquals(8, MathUtils.roundUpToPowerOfTwo(7));
        Assert.assertEquals(8, MathUtils.roundUpToPowerOfTwo(8));
        Assert.assertEquals(16, MathUtils.roundUpToPowerOfTwo(9));
        Assert.assertEquals(16, MathUtils.roundUpToPowerOfTwo(15));
        Assert.assertEquals(16, MathUtils.roundUpToPowerOfTwo(16));
        Assert.assertEquals(32, MathUtils.roundUpToPowerOfTwo(17));
        Assert.assertEquals(32, MathUtils.roundUpToPowerOfTwo(31));
        Assert.assertEquals(32, MathUtils.roundUpToPowerOfTwo(32));
        Assert.assertEquals(64, MathUtils.roundUpToPowerOfTwo(33));
        Assert.assertEquals(64, MathUtils.roundUpToPowerOfTwo(42));
        Assert.assertEquals(64, MathUtils.roundUpToPowerOfTwo(63));
        Assert.assertEquals(64, MathUtils.roundUpToPowerOfTwo(64));
        Assert.assertEquals(128, MathUtils.roundUpToPowerOfTwo(125));
        Assert.assertEquals(32768, MathUtils.roundUpToPowerOfTwo(25654));
        Assert.assertEquals(67108864, MathUtils.roundUpToPowerOfTwo(34366363));
        Assert.assertEquals(67108864, MathUtils.roundUpToPowerOfTwo(67108863));
        Assert.assertEquals(67108864, MathUtils.roundUpToPowerOfTwo(67108864));
        Assert.assertEquals(1073741824, MathUtils.roundUpToPowerOfTwo(1073741822));
        Assert.assertEquals(1073741824, MathUtils.roundUpToPowerOfTwo(1073741823));
        Assert.assertEquals(1073741824, MathUtils.roundUpToPowerOfTwo(1073741824));
    }

    @Test
    public void testPowerOfTwo() {
        Assert.assertTrue(MathUtils.isPowerOf2(1));
        Assert.assertTrue(MathUtils.isPowerOf2(2));
        Assert.assertTrue(MathUtils.isPowerOf2(4));
        Assert.assertTrue(MathUtils.isPowerOf2(8));
        Assert.assertTrue(MathUtils.isPowerOf2(32768));
        Assert.assertTrue(MathUtils.isPowerOf2(65536));
        Assert.assertTrue(MathUtils.isPowerOf2((1 << 30)));
        Assert.assertTrue(MathUtils.isPowerOf2((1L + (Integer.MAX_VALUE))));
        Assert.assertTrue(MathUtils.isPowerOf2((1L << 41)));
        Assert.assertTrue(MathUtils.isPowerOf2((1L << 62)));
        Assert.assertFalse(MathUtils.isPowerOf2(3));
        Assert.assertFalse(MathUtils.isPowerOf2(5));
        Assert.assertFalse(MathUtils.isPowerOf2(567923));
        Assert.assertFalse(MathUtils.isPowerOf2(Integer.MAX_VALUE));
        Assert.assertFalse(MathUtils.isPowerOf2(Long.MAX_VALUE));
    }

    @Test
    public void testFlipSignBit() {
        Assert.assertEquals(0L, MathUtils.flipSignBit(Long.MIN_VALUE));
        Assert.assertEquals(Long.MIN_VALUE, MathUtils.flipSignBit(0L));
        Assert.assertEquals((-1L), MathUtils.flipSignBit(Long.MAX_VALUE));
        Assert.assertEquals(Long.MAX_VALUE, MathUtils.flipSignBit((-1L)));
        Assert.assertEquals((42L | (Long.MIN_VALUE)), MathUtils.flipSignBit(42L));
        Assert.assertEquals(((-42L) & (Long.MAX_VALUE)), MathUtils.flipSignBit((-42L)));
    }
}

