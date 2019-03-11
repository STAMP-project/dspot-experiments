package com.fishercoder;


import _64.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _64Test {
    private static Solution1 solution1;

    private static int[][] grid;

    @Test
    public void test1() {
        _64Test.grid = new int[][]{ new int[]{ 1, 2 }, new int[]{ 1, 1 } };
        Assert.assertEquals(3, _64Test.solution1.minPathSum(_64Test.grid));
    }

    @Test
    public void test2() {
        _64Test.grid = new int[][]{ new int[]{ 1 } };
        Assert.assertEquals(1, _64Test.solution1.minPathSum(_64Test.grid));
    }

    @Test
    public void test3() {
        _64Test.grid = new int[][]{ new int[]{ 1, 3, 1 }, new int[]{ 1, 5, 1 }, new int[]{ 4, 2, 1 } };
        Assert.assertEquals(7, _64Test.solution1.minPathSum(_64Test.grid));
    }
}

