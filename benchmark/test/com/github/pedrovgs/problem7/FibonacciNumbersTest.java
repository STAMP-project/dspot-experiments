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
package com.github.pedrovgs.problem7;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class FibonacciNumbersTest {
    private FibonacciNumbers fibonacciNumbers;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNegativeValuesRecursive() {
        fibonacciNumbers.getRecursive((-1));
    }

    @Test
    public void firstNumberInFibonacciSequenceIsThreeRecursive() {
        Assert.assertEquals(1, fibonacciNumbers.getRecursive(1));
    }

    @Test
    public void fourthNumberInFibonacciSequenceIsThreeRecursive() {
        Assert.assertEquals(5, fibonacciNumbers.getRecursive(5));
    }

    @Test
    public void eleventhNumberInFibonacciSequenceIsRecursive() {
        Assert.assertEquals(144, fibonacciNumbers.getRecursive(12));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNegativeValuesIterative() {
        fibonacciNumbers.getIterative((-1));
    }

    @Test
    public void firstNumberInFibonacciSequenceIsThreeIterative() {
        Assert.assertEquals(1, fibonacciNumbers.getIterative(1));
    }

    @Test
    public void fourthNumberInFibonacciSequenceIsThreeIterative() {
        Assert.assertEquals(5, fibonacciNumbers.getIterative(5));
    }

    @Test
    public void eleventhNumberInFibonacciSequenceIsIterative() {
        Assert.assertEquals(144, fibonacciNumbers.getIterative(12));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNegativeValuesRecursiveWithCatching() {
        fibonacciNumbers.getRecursiveWithCaching((-1));
    }

    @Test
    public void fourthNumberInFibonacciSequenceIsThreeRecursiveWithCatching() {
        Assert.assertEquals(5, fibonacciNumbers.getRecursiveWithCaching(5));
    }

    @Test
    public void eleventhNumberInFibonacciSequenceIsRecursiveWithCatching() {
        Assert.assertEquals(144, fibonacciNumbers.getRecursiveWithCaching(12));
    }
}

