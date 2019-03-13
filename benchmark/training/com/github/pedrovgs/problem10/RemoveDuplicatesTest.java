package com.github.pedrovgs.problem10;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pedro Vicente G?mez S?nchez.
 */
public class RemoveDuplicatesTest {
    private RemoveDuplicates removeDuplicates;

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArraysBasedOnSet() {
        removeDuplicates.removeUsingSet(null);
    }

    @Test
    public void shouldSupportEmptyArraysBasedOnSet() {
        Integer[] array = new Integer[0];
        Integer[] result = removeDuplicates.removeUsingSet(array);
        Assert.assertArrayEquals(new Integer[0], result);
    }

    @Test
    public void shouldReturnAnArrayWithJustOneElementIfInputJustContainsOneElementBasedOnSet() {
        Integer[] array = new Integer[]{ 1 };
        Integer[] result = removeDuplicates.removeUsingSet(array);
        Integer[] expected = new Integer[]{ 1 };
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void shouldSupportArraysWithoutDuplicatedElementsBasedOnSet() {
        Integer[] array = new Integer[]{ 1, 2, 3 };
        Integer[] result = removeDuplicates.removeUsingSet(array);
        Assert.assertTrue(containsUniqueElements(result));
    }

    @Test
    public void shouldReturnAnArrayWithJustOneElementIfTheArrayIsFullOfTheSameElementBasedOnSet() {
        Integer[] array = new Integer[]{ 1, 1, 1, 1, 1 };
        Integer[] result = removeDuplicates.removeUsingSet(array);
        Assert.assertTrue(containsUniqueElements(result));
    }

    @Test
    public void shouldRemoveDuplicatesIfTheInputIsSortedBasedOnSet() {
        Integer[] array = new Integer[]{ 1, 2, 3, 3, 4, 4, 6, 6 };
        Integer[] result = removeDuplicates.removeUsingSet(array);
        Assert.assertTrue(containsUniqueElements(result));
    }

    @Test
    public void shouldRemoveDuplicatesIfTheInputIsNotSortedBasedOnSet() {
        Integer[] array = new Integer[]{ 1, 1, 5, 6, 2, 3 };
        Integer[] result = removeDuplicates.removeUsingSet(array);
        Assert.assertTrue(containsUniqueElements(result));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullArraysSorting() {
        removeDuplicates.removeUsingSorting(null);
    }

    @Test
    public void shouldSupportEmptyArraysSorting() {
        Integer[] array = new Integer[0];
        Integer[] result = removeDuplicates.removeUsingSorting(array);
        Assert.assertArrayEquals(new Integer[0], result);
    }

    @Test
    public void shouldReturnAnArrayWithJustOneElementIfInputJustContainsOneElementSorting() {
        Integer[] array = new Integer[]{ 1 };
        Integer[] result = removeDuplicates.removeUsingSorting(array);
        Integer[] expected = new Integer[]{ 1 };
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void shouldSupportArraysWithoutDuplicatedElementsSorting() {
        Integer[] array = new Integer[]{ 1, 2, 3 };
        Integer[] result = removeDuplicates.removeUsingSorting(array);
        Assert.assertTrue(containsUniqueElements(result));
    }

    @Test
    public void shouldReturnAnArrayWithJustOneElementIfTheArrayIsFullOfTheSameElementSorting() {
        Integer[] array = new Integer[]{ 1, 1, 1, 1, 1 };
        Integer[] result = removeDuplicates.removeUsingSorting(array);
        Assert.assertTrue(containsUniqueElements(result));
    }

    @Test
    public void shouldRemoveDuplicatesIfTheInputIsSortedSorting() {
        Integer[] array = new Integer[]{ 1, 2, 3, 3, 4, 4, 6, 6 };
        Integer[] result = removeDuplicates.removeUsingSorting(array);
        Assert.assertTrue(containsUniqueElements(result));
    }

    @Test
    public void shouldRemoveDuplicatesIfTheInputIsNotSortedSorting() {
        Integer[] array = new Integer[]{ 1, 1, 5, 6, 2, 3 };
        Integer[] result = removeDuplicates.removeUsingSorting(array);
        Assert.assertTrue(containsUniqueElements(result));
    }
}

