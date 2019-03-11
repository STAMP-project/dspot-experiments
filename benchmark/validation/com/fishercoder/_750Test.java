package com.fishercoder;


import _750.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _750Test {
    private static Solution1 solution1;

    private static int[][] grid;

    @Test
    public void test1() {
        _750Test.grid = new int[][]{ new int[]{ 1, 0, 0, 1, 0 }, new int[]{ 0, 0, 1, 0, 1 }, new int[]{ 0, 0, 0, 1, 0 }, new int[]{ 1, 0, 1, 0, 1 } };
        Assert.assertEquals(1, _750Test.solution1.countCornerRectangles(_750Test.grid));
    }

    @Test
    public void test2() {
        _750Test.grid = new int[][]{ new int[]{ 1, 1, 1 }, new int[]{ 1, 1, 1 }, new int[]{ 1, 1, 1 } };
        Assert.assertEquals(9, _750Test.solution1.countCornerRectangles(_750Test.grid));
    }

    @Test
    public void test3() {
        _750Test.grid = new int[][]{ new int[]{ 1, 1, 1, 1 } };
        Assert.assertEquals(0, _750Test.solution1.countCornerRectangles(_750Test.grid));
    }
}

