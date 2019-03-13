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
package com.github.pedrovgs.problem12;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class MoveZerosInArrayTest {
    private MoveZerosInArray moveZeros;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArraysSorting() {
        moveZeros.moveSorting(null);
    }

    @Test
    public void shouldWorkWithAnEmptyArraySorting() {
        int[] array = new int[0];
        moveZeros.moveSorting(array);
        Assert.assertArrayEquals(new int[0], array);
    }

    @Test
    public void shouldWorkWithPositiveNegativeZerosArraySorting() {
        int[] array = new int[]{ -1, 0, 2, 4, 0, -3, -5, 0, 6, -3, 0 };
        moveZeros.moveSorting(array);
        assertZerosAtRight(array);
    }

    @Test
    public void shouldOrganizeAnArrayFullOfZerosSorting() {
        int[] array = new int[]{ 0, 0, 0, 0, 0 };
        moveZeros.moveUsingTwoPointers(array);
        int[] expected = new int[]{ 0, 0, 0, 0, 0 };
        Assert.assertArrayEquals(expected, array);
    }

    @Test
    public void shouldOrganizeAnArrayFullOfNonZerosSorting() {
        int[] array = new int[]{ 1, 1, 1, 1, 1 };
        moveZeros.moveUsingTwoPointers(array);
        int[] expected = new int[]{ 1, 1, 1, 1, 1 };
        Assert.assertArrayEquals(expected, array);
    }

    @Test
    public void shouldOrganizeAnArrayWithZerosAndNonPositiveIntegersSorting() {
        int[] array = new int[]{ 1, 0, 2, 3, 0 };
        moveZeros.moveUsingTwoPointers(array);
        assertZerosAtRight(array);
    }

    @Test
    public void shouldOrganizeAnArrayWithZerosPositiveAndNegativeIntegersSorting() {
        int[] array = new int[]{ 1, 0, 2, -3, 0, 0, 0, 0, -1 };
        moveZeros.moveUsingTwoPointers(array);
        assertZerosAtRight(array);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArraysWithTwoPointers() {
        moveZeros.moveUsingTwoPointers(null);
    }

    @Test
    public void shouldWorkWithAnEmptyArrayWithTwoPointers() {
        int[] array = new int[0];
        moveZeros.moveUsingTwoPointers(array);
        Assert.assertArrayEquals(new int[0], array);
    }

    @Test
    public void shouldOrganizeAnArrayFullOfZerosWithTwoPointers() {
        int[] array = new int[]{ 0, 0, 0, 0, 0 };
        moveZeros.moveUsingTwoPointers(array);
        int[] expected = new int[]{ 0, 0, 0, 0, 0 };
        Assert.assertArrayEquals(expected, array);
    }

    @Test
    public void shouldOrganizeAnArrayFullOfNonZerosWithTwoPointers() {
        int[] array = new int[]{ 1, 1, 1, 1, 1 };
        moveZeros.moveUsingTwoPointers(array);
        int[] expected = new int[]{ 1, 1, 1, 1, 1 };
        Assert.assertArrayEquals(expected, array);
    }

    @Test
    public void shouldOrganizeAnArrayWithZerosAndNonPositiveIntegersWithTwoPointers() {
        int[] array = new int[]{ 1, 0, 2, 3, 0 };
        moveZeros.moveUsingTwoPointers(array);
        assertZerosAtRight(array);
    }

    @Test
    public void shouldOrganizeAnArrayWithZerosPositiveAndNegativeIntegersWithTwoPointers() {
        int[] array = new int[]{ 1, 0, 2, -3, 0, 0, 0, 0, -1 };
        moveZeros.moveUsingTwoPointers(array);
        assertZerosAtRight(array);
    }
}

