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
package com.github.pedrovgs.problem6;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class MergeSortedArraysTest {
    private MergeSortedArrays mergeSortedArrays;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArrays() {
        mergeSortedArrays.merge(null, null);
    }

    @Test
    public void shouldConcatenateArraysIfThereArentArrayCollisions() {
        int[] a1 = new int[]{ 1, 2, 3, 4, 5 };
        int[] a2 = new int[]{ 7, 8, 9 };
        int[] result = mergeSortedArrays.merge(a1, a2);
        int[] expectedArray = new int[]{ 1, 2, 3, 4, 5, 7, 8, 9 };
        Assert.assertArrayEquals(expectedArray, result);
    }

    @Test
    public void shouldMergeArraysWithCollisions() {
        int[] a1 = new int[]{ 1, 2, 3, 4, 9, 10, 11 };
        int[] a2 = new int[]{ 5, 6, 7 };
        int[] result = mergeSortedArrays.merge(a1, a2);
        int[] expectedArray = new int[]{ 1, 2, 3, 4, 5, 6, 7, 9, 10, 11 };
        Assert.assertArrayEquals(expectedArray, result);
    }
}

