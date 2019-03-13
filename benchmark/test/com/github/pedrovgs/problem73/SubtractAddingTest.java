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
package com.github.pedrovgs.problem73;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class SubtractAddingTest {
    private SubtractAdding subtractAdding;

    @Test
    public void shouldReturnZeroIfInputsAreZero() {
        int inputA = 0;
        int inputB = 0;
        int result = subtractAdding.subtract(inputA, inputB);
        Assert.assertEquals(0, result);
    }

    @Test
    public void shouldReturnZeroIfInputsAreEquals() {
        int inputA = 30;
        int inputB = 30;
        int result = subtractAdding.subtract(inputA, inputB);
        Assert.assertEquals(0, result);
    }

    @Test
    public void shouldReturnAPositiveResultIfFirstInputIsGreaterThanTheSecondOne() {
        int inputA = 33;
        int inputB = 30;
        int result = subtractAdding.subtract(inputA, inputB);
        Assert.assertEquals(3, result);
    }

    @Test
    public void shouldReturnANegativeResultIfFirstInputIsLessThanTheSecondOne() {
        int inputA = 30;
        int inputB = 35;
        int result = subtractAdding.subtract(inputA, inputB);
        Assert.assertEquals((-5), result);
    }

    @Test
    public void shouldCalculateTheCorrectSubtractionIfOneOfTheIntegersIsNegative() {
        int inputA = 30;
        int inputB = -35;
        int result = subtractAdding.subtract(inputA, inputB);
        Assert.assertEquals(65, result);
    }

    @Test
    public void shouldCalculateTheCorrectSubttractionIfBothIntegersAreNegative() {
        int inputA = -30;
        int inputB = -35;
        int result = subtractAdding.subtract(inputA, inputB);
        Assert.assertEquals(5, result);
    }
}

