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
package com.github.pedrovgs.problem4;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class SquareRootTest {
    private static final float DELTA = 0.1F;

    private SquareRoot squareRoot;

    @Test
    public void squareRootOfFourEqualsToTwo() {
        float result = squareRoot.calculateSquareRootIterative(4);
        Assert.assertEquals(2, result, SquareRootTest.DELTA);
    }

    @Test
    public void squareRootOfNineEqualsToThreeIterative() {
        float result = squareRoot.calculateSquareRootIterative(9);
        Assert.assertEquals(3, result, SquareRootTest.DELTA);
    }

    @Test
    public void shouldCalculateNonPerfectSquaresIterative() {
        float result = squareRoot.calculateSquareRootIterative(7);
        Assert.assertEquals(2.64575F, result, SquareRootTest.DELTA);
    }

    @Test
    public void shouldCalculateSquaresOfTwoDigitsIterative() {
        float result = squareRoot.calculateSquareRootIterative(12);
        Assert.assertEquals(3.4641F, result, SquareRootTest.DELTA);
    }

    @Test
    public void squareRootOfFourEqualsToTwoRecursiveIterative() {
        float result = squareRoot.calculateSquareRootRecursive(4);
        Assert.assertEquals(2, result, SquareRootTest.DELTA);
    }

    @Test
    public void squareRootOfNineEqualsToThreeRecursive() {
        float result = squareRoot.calculateSquareRootRecursive(9);
        Assert.assertEquals(3, result, SquareRootTest.DELTA);
    }

    @Test
    public void shouldCalculateNonPerfectSquaresRecursive() {
        float result = squareRoot.calculateSquareRootRecursive(7);
        Assert.assertEquals(2.64575F, result, SquareRootTest.DELTA);
    }

    @Test
    public void shouldCalculateSquaresOfTwoDigitsRecursive() {
        float result = squareRoot.calculateSquareRootRecursive(12);
        Assert.assertEquals(3.4641F, result, SquareRootTest.DELTA);
    }
}

