package com.baeldung.algorithms.insertionsort;


import org.junit.Assert;
import org.junit.Test;


public class InsertionSortUnitTest {
    @Test
    public void givenUnsortedArray_whenInsertionSortImperative_thenSortedAsc() {
        int[] input = new int[]{ 6, 2, 3, 4, 5, 1 };
        InsertionSort.insertionSortImperative(input);
        int[] expected = new int[]{ 1, 2, 3, 4, 5, 6 };
        Assert.assertArrayEquals("the two arrays are not equal", expected, input);
    }

    @Test
    public void givenUnsortedArray_whenInsertionSortRecursive_thenSortedAsc() {
        int[] input = new int[]{ 6, 4, 5, 2, 3, 1 };
        InsertionSort.insertionSortRecursive(input);
        int[] expected = new int[]{ 1, 2, 3, 4, 5, 6 };
        Assert.assertArrayEquals("the two arrays are not equal", expected, input);
    }
}

