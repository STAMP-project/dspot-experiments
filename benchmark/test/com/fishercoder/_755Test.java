package com.fishercoder;


import _755.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _755Test {
    private static Solution1 solution1;

    private static int[] heights;

    private static int[] expected;

    @Test
    public void test1() {
        _755Test.heights = new int[]{ 2, 1, 1, 2, 1, 2, 2 };
        _755Test.expected = new int[]{ 2, 2, 2, 3, 2, 2, 2 };
        Assert.assertArrayEquals(_755Test.expected, _755Test.solution1.pourWater(_755Test.heights, 4, 3));
    }

    @Test
    public void test2() {
        _755Test.heights = new int[]{ 1, 2, 3, 4 };
        _755Test.expected = new int[]{ 2, 3, 3, 4 };
        Assert.assertArrayEquals(_755Test.expected, _755Test.solution1.pourWater(_755Test.heights, 2, 2));
    }

    @Test
    public void test3() {
        _755Test.heights = new int[]{ 3, 1, 3 };
        _755Test.expected = new int[]{ 4, 4, 4 };
        Assert.assertArrayEquals(_755Test.expected, _755Test.solution1.pourWater(_755Test.heights, 5, 1));
    }

    @Test
    public void test4() {
        _755Test.heights = new int[]{ 1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        _755Test.expected = new int[]{ 1, 2, 3, 4, 3, 3, 2, 2, 3, 4, 3, 2, 1 };
        Assert.assertArrayEquals(_755Test.expected, _755Test.solution1.pourWater(_755Test.heights, 2, 5));
    }

    @Test
    public void test5() {
        _755Test.heights = new int[]{ 1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        _755Test.expected = new int[]{ 3, 4, 4, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        Assert.assertArrayEquals(_755Test.expected, _755Test.solution1.pourWater(_755Test.heights, 5, 2));
    }

    @Test
    public void test6() {
        _755Test.heights = new int[]{ 1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        _755Test.expected = new int[]{ 4, 4, 4, 4, 3, 3, 3, 3, 3, 4, 3, 2, 1 };
        Assert.assertArrayEquals(_755Test.expected, _755Test.solution1.pourWater(_755Test.heights, 10, 2));
    }

    @Test
    public void test7() {
        _755Test.heights = new int[]{ 1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        _755Test.expected = new int[]{ 2, 3, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        Assert.assertArrayEquals(_755Test.expected, _755Test.solution1.pourWater(_755Test.heights, 2, 2));
    }

    @Test
    public void test8() {
        _755Test.heights = new int[]{ 1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        _755Test.expected = new int[]{ 3, 3, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        Assert.assertArrayEquals(_755Test.expected, _755Test.solution1.pourWater(_755Test.heights, 3, 2));
    }

    @Test
    public void test9() {
        _755Test.heights = new int[]{ 1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        _755Test.expected = new int[]{ 3, 3, 4, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        Assert.assertArrayEquals(_755Test.expected, _755Test.solution1.pourWater(_755Test.heights, 4, 2));
    }

    @Test
    public void test10() {
        _755Test.heights = new int[]{ 1, 2, 3, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        _755Test.expected = new int[]{ 3, 4, 4, 4, 3, 2, 1, 2, 3, 4, 3, 2, 1 };
        Assert.assertArrayEquals(_755Test.expected, _755Test.solution1.pourWater(_755Test.heights, 5, 2));
    }
}

