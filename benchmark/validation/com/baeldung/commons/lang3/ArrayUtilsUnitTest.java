package com.baeldung.commons.lang3;


import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;


public class ArrayUtilsUnitTest {
    @Test
    public void givenArray_whenAddingElementAtSpecifiedPosition_thenCorrect() {
        int[] oldArray = new int[]{ 2, 3, 4, 5 };
        int[] newArray = ArrayUtils.add(oldArray, 0, 1);
        int[] expectedArray = new int[]{ 1, 2, 3, 4, 5 };
        Assert.assertArrayEquals(expectedArray, newArray);
    }

    @Test
    public void givenArray_whenAddingElementAtTheEnd_thenCorrect() {
        int[] oldArray = new int[]{ 2, 3, 4, 5 };
        int[] newArray = ArrayUtils.add(oldArray, 1);
        int[] expectedArray = new int[]{ 2, 3, 4, 5, 1 };
        Assert.assertArrayEquals(expectedArray, newArray);
    }

    @Test
    public void givenArray_whenAddingAllElementsAtTheEnd_thenCorrect() {
        int[] oldArray = new int[]{ 0, 1, 2 };
        int[] newArray = ArrayUtils.addAll(oldArray, 3, 4, 5);
        int[] expectedArray = new int[]{ 0, 1, 2, 3, 4, 5 };
        Assert.assertArrayEquals(expectedArray, newArray);
    }

    @Test
    public void givenArray_whenRemovingElementAtSpecifiedPosition_thenCorrect() {
        int[] oldArray = new int[]{ 1, 2, 3, 4, 5 };
        int[] newArray = ArrayUtils.remove(oldArray, 1);
        int[] expectedArray = new int[]{ 1, 3, 4, 5 };
        Assert.assertArrayEquals(expectedArray, newArray);
    }

    @Test
    public void givenArray_whenRemovingAllElementsAtSpecifiedPositions_thenCorrect() {
        int[] oldArray = new int[]{ 1, 2, 3, 4, 5 };
        int[] newArray = ArrayUtils.removeAll(oldArray, 1, 3);
        int[] expectedArray = new int[]{ 1, 3, 5 };
        Assert.assertArrayEquals(expectedArray, newArray);
    }

    @Test
    public void givenArray_whenRemovingAnElement_thenCorrect() {
        int[] oldArray = new int[]{ 1, 2, 3, 3, 4 };
        int[] newArray = ArrayUtils.removeElement(oldArray, 3);
        int[] expectedArray = new int[]{ 1, 2, 3, 4 };
        Assert.assertArrayEquals(expectedArray, newArray);
    }

    @Test
    public void givenArray_whenRemovingElements_thenCorrect() {
        int[] oldArray = new int[]{ 1, 2, 3, 3, 4 };
        int[] newArray = ArrayUtils.removeElements(oldArray, 2, 3, 5);
        int[] expectedArray = new int[]{ 1, 3, 4 };
        Assert.assertArrayEquals(expectedArray, newArray);
    }

    @Test
    public void givenArray_whenRemovingAllElementOccurences_thenCorrect() {
        int[] oldArray = new int[]{ 1, 2, 2, 2, 3 };
        int[] newArray = ArrayUtils.removeAllOccurences(oldArray, 2);
        int[] expectedArray = new int[]{ 1, 3 };
        Assert.assertArrayEquals(expectedArray, newArray);
    }

    @Test
    public void givenArray_whenCheckingExistingElement_thenCorrect() {
        int[] array = new int[]{ 1, 3, 5, 7, 9 };
        boolean evenContained = ArrayUtils.contains(array, 2);
        boolean oddContained = ArrayUtils.contains(array, 7);
        Assert.assertEquals(false, evenContained);
        Assert.assertEquals(true, oddContained);
    }

    @Test
    public void givenArray_whenReversingElementsWithinARange_thenCorrect() {
        int[] originalArray = new int[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.reverse(originalArray, 1, 4);
        int[] expectedArray = new int[]{ 1, 4, 3, 2, 5 };
        Assert.assertArrayEquals(expectedArray, originalArray);
    }

    @Test
    public void givenArray_whenReversingAllElements_thenCorrect() {
        int[] originalArray = new int[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.reverse(originalArray);
        int[] expectedArray = new int[]{ 5, 4, 3, 2, 1 };
        Assert.assertArrayEquals(expectedArray, originalArray);
    }

    @Test
    public void givenArray_whenShiftingElementsWithinARange_thenCorrect() {
        int[] originalArray = new int[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.shift(originalArray, 1, 4, 1);
        int[] expectedArray = new int[]{ 1, 4, 2, 3, 5 };
        Assert.assertArrayEquals(expectedArray, originalArray);
    }

    @Test
    public void givenArray_whenShiftingAllElements_thenCorrect() {
        int[] originalArray = new int[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.shift(originalArray, 1);
        int[] expectedArray = new int[]{ 5, 1, 2, 3, 4 };
        Assert.assertArrayEquals(expectedArray, originalArray);
    }

    @Test
    public void givenArray_whenExtractingElements_thenCorrect() {
        int[] oldArray = new int[]{ 1, 2, 3, 4, 5 };
        int[] newArray = ArrayUtils.subarray(oldArray, 2, 7);
        int[] expectedArray = new int[]{ 3, 4, 5 };
        Assert.assertArrayEquals(expectedArray, newArray);
    }

    @Test
    public void givenArray_whenSwapingElementsWithinARange_thenCorrect() {
        int[] originalArray = new int[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.swap(originalArray, 0, 3, 2);
        int[] expectedArray = new int[]{ 4, 5, 3, 1, 2 };
        Assert.assertArrayEquals(expectedArray, originalArray);
    }

    @Test
    public void givenArray_whenSwapingElementsAtSpecifiedPositions_thenCorrect() {
        int[] originalArray = new int[]{ 1, 2, 3, 4, 5 };
        ArrayUtils.swap(originalArray, 0, 3);
        int[] expectedArray = new int[]{ 4, 2, 3, 1, 5 };
        Assert.assertArrayEquals(expectedArray, originalArray);
    }
}

