/**
 * Copyright 2013 MovingBlocks
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
package org.terasology.math;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class IntMathTest {
    public IntMathTest() {
    }

    @Test
    public void testCeilPowerOfTwo() {
        List<Integer> powersOfTwo = IntMathTest.generateAllPowersOfTwo();
        for (int i = 1; i < (powersOfTwo.size()); i++) {
            // test inputs on and around powers of two. Skips tests on zero
            testCeilPowerOfTwo(powersOfTwo.get((i - 1)), powersOfTwo.get(i));
        }
        int largestIntegerPowerOfTwo = powersOfTwo.get(((powersOfTwo.size()) - 1));
        // test other boundary values
        Assert.assertEquals("0", 0, TeraMath.ceilPowerOfTwo(0));
        Assert.assertEquals("-1", 0, TeraMath.ceilPowerOfTwo(0));
        Assert.assertEquals("Integer.MIN_VALUE", 0, TeraMath.ceilPowerOfTwo(Integer.MIN_VALUE));
        Assert.assertEquals("Integer.MAX_VALUE", 0, TeraMath.ceilPowerOfTwo(Integer.MAX_VALUE));
        Assert.assertEquals("Largest integer power of two + 1", 0, TeraMath.ceilPowerOfTwo((largestIntegerPowerOfTwo + 1)));
    }

    @Test
    public void testSizeOfPower() {
        Assert.assertEquals(0, TeraMath.sizeOfPower(1));
        Assert.assertEquals(1, TeraMath.sizeOfPower(2));
        Assert.assertEquals(2, TeraMath.sizeOfPower(4));
        Assert.assertEquals(3, TeraMath.sizeOfPower(8));
        Assert.assertEquals(4, TeraMath.sizeOfPower(16));
        Assert.assertEquals(5, TeraMath.sizeOfPower(32));
    }

    @Test
    public void testFloorToInt() {
        Assert.assertEquals(0, TeraMath.floorToInt(0.0F));
        Assert.assertEquals(1, TeraMath.floorToInt(1.0F));
        Assert.assertEquals(0, TeraMath.floorToInt(0.5F));
        Assert.assertEquals((-1), TeraMath.floorToInt((-0.5F)));
        Assert.assertEquals((-1), TeraMath.floorToInt((-1.0F)));
    }

    @Test
    public void testCeilToInt() {
        Assert.assertEquals(0, TeraMath.ceilToInt(0.0F));
        Assert.assertEquals(1, TeraMath.ceilToInt(1.0F));
        Assert.assertEquals(1, TeraMath.ceilToInt(0.5F));
        Assert.assertEquals(0, TeraMath.ceilToInt((-0.5F)));
        Assert.assertEquals((-1), TeraMath.ceilToInt((-1.0F)));
    }
}

