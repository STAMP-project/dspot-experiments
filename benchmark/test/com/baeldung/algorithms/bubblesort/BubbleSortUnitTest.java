package com.baeldung.algorithms.bubblesort;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BubbleSortUnitTest {
    @Test
    public void givenIntegerArray_whenSortedWithBubbleSort_thenGetSortedArray() {
        Integer[] array = new Integer[]{ 2, 1, 4, 6, 3, 5 };
        Integer[] sortedArray = new Integer[]{ 1, 2, 3, 4, 5, 6 };
        BubbleSort bubbleSort = new BubbleSort();
        bubbleSort.bubbleSort(array);
        Assertions.assertArrayEquals(array, sortedArray);
    }

    @Test
    public void givenIntegerArray_whenSortedWithOptimizedBubbleSort_thenGetSortedArray() {
        Integer[] array = new Integer[]{ 2, 1, 4, 6, 3, 5 };
        Integer[] sortedArray = new Integer[]{ 1, 2, 3, 4, 5, 6 };
        BubbleSort bubbleSort = new BubbleSort();
        bubbleSort.optimizedBubbleSort(array);
        Assertions.assertArrayEquals(array, sortedArray);
    }
}

