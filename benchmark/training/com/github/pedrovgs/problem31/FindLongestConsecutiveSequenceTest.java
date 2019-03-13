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
package com.github.pedrovgs.problem31;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class FindLongestConsecutiveSequenceTest {
    private FindLongestConsecutiveSequence lcs;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArraysIterative() {
        lcs.findIterative(null);
    }

    @Test
    public void shouldReturnZeroIfTheArrayIsEmptyIterative() {
        int[] array = new int[]{  };
        int sequenceLength = lcs.findIterative(array);
        Assert.assertEquals(0, sequenceLength);
    }

    @Test
    public void shouldReturnZeroIfThereIsNoAnyConsecutiveSequenceIterative() {
        int[] array = new int[]{ 6, 5, 4, 3, 2, 1 };
        int sequenceLength = lcs.findIterative(array);
        Assert.assertEquals(0, sequenceLength);
    }

    @Test
    public void shouldReturnLengthArrayIfTheArrayIsOneConsecutiveSequenceIterative() {
        int[] array = new int[]{ 1, 2, 3, 4, 5, 6 };
        int sequenceLength = lcs.findIterative(array);
        Assert.assertEquals(6, sequenceLength);
    }

    @Test
    public void shouldFindLongestConsecutiveSequenceIterative() {
        int[] array = new int[]{ 1, 3, 4, 5, 64, 4, 5, 6, 7, 8, 9, 98, -1, -2 };
        int sequenceLength = lcs.findIterative(array);
        Assert.assertEquals(7, sequenceLength);
    }

    @Test
    public void shouldFindLongestConsecutiveSequenceIterativeSupportingEqualsConsecutiveNumbers() {
        int[] array = new int[]{ 1, 1, 1, 1, 3, 4, 5, 64, 4, 5, 6, 7, 8, 9, 98, -1, -2 };
        int sequenceLength = lcs.findIterative(array);
        Assert.assertEquals(7, sequenceLength);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArraysRecursive() {
        lcs.findRecursive(null);
    }

    @Test
    public void shouldReturnZeroIfTheArrayIsEmptyRecursive() {
        int[] array = new int[]{  };
        int sequenceLength = lcs.findRecursive(array);
        Assert.assertEquals(0, sequenceLength);
    }

    @Test
    public void shouldReturnZeroIfThereIsNoAnyConsecutiveSequenceRecursive() {
        int[] array = new int[]{ 6, 5, 4, 3, 2, 1 };
        int sequenceLength = lcs.findRecursive(array);
        Assert.assertEquals(0, sequenceLength);
    }

    @Test
    public void shouldReturnLengthArrayIfTheArrayIsOneConsecutiveSequenceRecursive() {
        int[] array = new int[]{ 1, 2, 3, 4, 5, 6 };
        int sequenceLength = lcs.findRecursive(array);
        Assert.assertEquals(6, sequenceLength);
    }

    @Test
    public void shouldFindLongestConsecutiveSequenceRecursive() {
        int[] array = new int[]{ 1, 3, 4, 5, 64, 4, 5, 6, 7, 8, 9, 98, -1, -2 };
        int sequenceLength = lcs.findRecursive(array);
        Assert.assertEquals(7, sequenceLength);
    }

    @Test
    public void shouldFindLongestConsecutiveSequenceRecursiveSupportingEqualsConsecutiveNumbers() {
        int[] array = new int[]{ 1, 1, 1, 1, 3, 4, 5, 64, 4, 5, 6, 7, 8, 9, 98, -1, -2 };
        int sequenceLength = lcs.findRecursive(array);
        Assert.assertEquals(7, sequenceLength);
    }
}

