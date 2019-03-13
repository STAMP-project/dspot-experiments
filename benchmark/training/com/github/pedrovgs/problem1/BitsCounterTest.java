/**
 * Copyright (C) 2014 Pedro Vicente G?mez S?nchez.
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
package com.github.pedrovgs.problem1;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class BitsCounterTest {
    private BitsCounter bitsCounter;

    @Test
    public void numberOfBitsInOneIsOne() {
        Assert.assertEquals(1, bitsCounter.countBitsToOneBasedOnString(1));
    }

    @Test
    public void numberOfBitsInTwoIsOne() {
        Assert.assertEquals(1, bitsCounter.countBitsToOneBasedOnString(2));
    }

    @Test
    public void numberOfBitsInThreeIsTwo() {
        Assert.assertEquals(2, bitsCounter.countBitsToOneBasedOnString(3));
    }

    @Test
    public void numberOfBitsInSevenIsThree() {
        Assert.assertEquals(3, bitsCounter.countBitsToOneBasedOnBinaryOperators(7));
    }

    @Test
    public void numberOfBitsIn1990IsSeven() {
        Assert.assertEquals(7, bitsCounter.countBitsToOneBasedOnBinaryOperators(1990));
    }

    @Test
    public void numberOfBitsInOneIsOneBasedOnBinaryOperator() {
        Assert.assertEquals(1, bitsCounter.countBitsToOneBasedOnBinaryOperators(1));
    }

    @Test
    public void numberOfBitsInTwoIsOneBasedOnBinaryOperator() {
        Assert.assertEquals(1, bitsCounter.countBitsToOneBasedOnBinaryOperators(2));
    }

    @Test
    public void numberOfBitsInThreeIsTwoBasedOnBinaryOperator() {
        Assert.assertEquals(2, bitsCounter.countBitsToOneBasedOnBinaryOperators(3));
    }

    @Test
    public void numberOfBitsInSevenIsThreeBasedOnBinaryOperator() {
        Assert.assertEquals(3, bitsCounter.countBitsToOneBasedOnBinaryOperators(7));
    }

    /**
     * A negative number is represented by calculating its complement and adding 1 to
     * the result (Two's complement).
     * eg. ~7 + 1
     */
    @Test
    public void numberOfBitsInNegativeSevenIsThreeBasedOnBinaryOperator() {
        Assert.assertEquals(30, bitsCounter.countBitsToOneBasedOnBinaryOperators((-7)));
    }

    @Test
    public void numberOfBitsInZero() {
        Assert.assertEquals(0, bitsCounter.countBitsToOneBasedOnBinaryOperators(0));
    }

    @Test
    public void numberOfBitsInZeroKernighanMethod() {
        Assert.assertEquals(0, bitsCounter.countNumberOfBitsLogN(0));
    }

    @Test
    public void numberOfBitsInNegativeIntegerKernighanMethod() {
        Assert.assertEquals(30, bitsCounter.countNumberOfBitsLogN((-7)));
    }

    @Test
    public void numberOfBitsKernighanMethod() {
        Assert.assertEquals(3, bitsCounter.countBitsToOneBasedOnBinaryOperators(7));
    }
}

