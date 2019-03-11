package com.baeldung.array;


import org.junit.Assert;
import org.junit.Test;


public class ArrayInitializerUnitTest {
    @Test
    public void whenInitializeArrayInLoop_thenCorrect() {
        Assert.assertArrayEquals(new int[]{ 2, 3, 4, 5, 6 }, ArrayInitializer.initializeArrayInLoop());
    }

    @Test
    public void whenInitializeMultiDimensionalArrayInLoop_thenCorrect() {
        Assert.assertArrayEquals(new int[][]{ new int[]{ 1, 2, 3, 4, 5 }, new int[]{ 1, 2, 3, 4, 5 } }, ArrayInitializer.initializeMultiDimensionalArrayInLoop());
    }

    @Test
    public void whenInitializeArrayAtTimeOfDeclarationMethod1_thenCorrect() {
        Assert.assertArrayEquals(new String[]{ "Toyota", "Mercedes", "BMW", "Volkswagen", "Skoda" }, ArrayInitializer.initializeArrayAtTimeOfDeclarationMethod1());
    }

    @Test
    public void whenInitializeArrayAtTimeOfDeclarationMethod2_thenCorrect() {
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4, 5 }, ArrayInitializer.initializeArrayAtTimeOfDeclarationMethod2());
    }

    @Test
    public void whenInitializeArrayAtTimeOfDeclarationMethod3_thenCorrect() {
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4, 5 }, ArrayInitializer.initializeArrayAtTimeOfDeclarationMethod3());
    }

    @Test
    public void whenInitializeArrayUsingArraysFill_thenCorrect() {
        Assert.assertArrayEquals(new long[]{ 30, 30, 30, 30, 30 }, ArrayInitializer.initializeArrayUsingArraysFill());
    }

    @Test
    public void whenInitializeArrayRangeUsingArraysFill_thenCorrect() {
        Assert.assertArrayEquals(new int[]{ -50, -50, -50, 0, 0 }, ArrayInitializer.initializeArrayRangeUsingArraysFill());
    }

    @Test
    public void whenInitializeArrayRangeUsingArraysCopy_thenCorrect() {
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4, 5 }, ArrayInitializer.initializeArrayUsingArraysCopy());
    }

    @Test
    public void whenInitializeLargerArrayRangeUsingArraysCopy_thenCorrect() {
        Assert.assertArrayEquals(new int[]{ 1, 2, 3, 4, 5, 0 }, ArrayInitializer.initializeLargerArrayUsingArraysCopy());
    }

    @Test
    public void whenInitializeLargerArrayRangeUsingArraysSetAll_thenCorrect() {
        Assert.assertArrayEquals(new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, ArrayInitializer.initializeArrayUsingArraysSetAll());
    }

    @Test
    public void whenInitializeArrayUsingArraysUtilClone_thenCorrect() {
        Assert.assertArrayEquals(new char[]{ 'a', 'b', 'c' }, ArrayInitializer.initializeArrayUsingArraysUtilClone());
    }
}

