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
package com.hazelcast.util;


import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Unit tests for {@link QuickMath} class.
 */
@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class QuickMathTest {
    @Test
    public void testIsPowerOfTwo() {
        Assert.assertTrue(QuickMath.isPowerOfTwo(1));
        Assert.assertTrue(QuickMath.isPowerOfTwo(2));
        Assert.assertFalse(QuickMath.isPowerOfTwo(3));
        Assert.assertTrue(QuickMath.isPowerOfTwo(1024));
        Assert.assertFalse(QuickMath.isPowerOfTwo(1023));
        Assert.assertFalse(QuickMath.isPowerOfTwo(Integer.MAX_VALUE));
    }

    @Test
    public void testNextPowerOfTwo() {
        Assert.assertEquals(1, QuickMath.nextPowerOfTwo((-9999999)));
        Assert.assertEquals(1, QuickMath.nextPowerOfTwo((-1)));
        Assert.assertEquals(1, QuickMath.nextPowerOfTwo(0));
        Assert.assertEquals(1, QuickMath.nextPowerOfTwo(1));
        Assert.assertEquals(2, QuickMath.nextPowerOfTwo(2));
        Assert.assertEquals(1024, QuickMath.nextPowerOfTwo(999));
        Assert.assertEquals((1 << 23), QuickMath.nextPowerOfTwo(((1 << 23) - 1)));
        Assert.assertEquals((1 << 23), QuickMath.nextPowerOfTwo((1 << 23)));
        Assert.assertEquals(2048L, QuickMath.nextPowerOfTwo(2000L));
        Assert.assertEquals((1L << 33), QuickMath.nextPowerOfTwo(((1L << 33) - 3)));
        Assert.assertEquals((1L << 43), QuickMath.nextPowerOfTwo((1L << 43)));
    }

    @Test
    public void testNextLongPowerOfTwo() {
        Assert.assertEquals(1L, QuickMath.nextPowerOfTwo((-9999999L)));
        Assert.assertEquals(1L, QuickMath.nextPowerOfTwo((-1L)));
        Assert.assertEquals(1L, QuickMath.nextPowerOfTwo(0L));
        Assert.assertEquals(1L, QuickMath.nextPowerOfTwo(1L));
        Assert.assertEquals(2L, QuickMath.nextPowerOfTwo(2L));
        Assert.assertEquals(4L, QuickMath.nextPowerOfTwo(3L));
        Assert.assertEquals((1L << 62), QuickMath.nextPowerOfTwo(((1L << 61) + 1)));
    }

    @Test
    public void testModPowerOfTwo() {
        int[] aParams = new int[]{ 0, 1, (Integer.MAX_VALUE) / 2, Integer.MAX_VALUE };
        int[] bParams = new int[]{ 1, 2, 1024, powerOfTwo(10), powerOfTwo(20) };
        for (int a : aParams) {
            for (int b : bParams) {
                Assert.assertEquals((a % b), QuickMath.modPowerOfTwo(a, b));
            }
        }
    }
}

