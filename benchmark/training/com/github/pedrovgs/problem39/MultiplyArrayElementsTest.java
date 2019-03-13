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
package com.github.pedrovgs.problem39;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class MultiplyArrayElementsTest {
    private MultiplyArrayElements multiplyArrayElements;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArrays() {
        multiplyArrayElements.multiply(null);
    }

    @Test
    public void shouldSupportEmptyArrays() {
        int[] array = new int[0];
        int[] result = multiplyArrayElements.multiply(array);
        Assert.assertEquals(0, result.length);
    }

    @Test
    public void shouldDoNothingIfTheArrayWithJustOneElement() {
        int[] array = new int[]{ 1 };
        int[] result = multiplyArrayElements.multiply(array);
        int[] expected = new int[]{ 1 };
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void shouldMultiplyEveryElementByTheRest() {
        int[] array = new int[]{ 1, 2, 3 };
        int[] result = multiplyArrayElements.multiply(array);
        int[] expected = new int[]{ 6, 3, 2 };
        Assert.assertArrayEquals(expected, result);
    }
}

