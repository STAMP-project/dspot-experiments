package com.fishercoder;


import _994.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _994Test {
    private static Solution1 solution1;

    private static int[][] grid;

    @Test
    public void test1() {
        _994Test.grid = new int[][]{ new int[]{ 2, 1, 1 }, new int[]{ 1, 1, 0 }, new int[]{ 0, 1, 1 } };
        Assert.assertEquals(4, _994Test.solution1.orangesRotting(_994Test.grid));
    }

    @Test
    public void test2() {
        _994Test.grid = new int[][]{ new int[]{ 2, 1, 1 }, new int[]{ 0, 1, 1 }, new int[]{ 1, 0, 1 } };
        Assert.assertEquals((-1), _994Test.solution1.orangesRotting(_994Test.grid));
    }

    @Test
    public void test3() {
        _994Test.grid = new int[][]{ new int[]{ 0, 2 } };
        Assert.assertEquals(0, _994Test.solution1.orangesRotting(_994Test.grid));
    }
}

