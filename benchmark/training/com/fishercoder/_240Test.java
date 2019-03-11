package com.fishercoder;


import _240.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _240Test {
    private static Solution1 solution1;

    private static boolean actual;

    private static boolean expected;

    private static int target;

    private static int[][] matrix;

    @Test
    public void test1() {
        _240Test.target = 5;
        _240Test.matrix = new int[][]{ new int[]{ 1, 4, 7, 11, 15 }, new int[]{ 2, 5, 8, 12, 19 }, new int[]{ 3, 6, 9, 16, 22 }, new int[]{ 10, 13, 14, 17, 24 }, new int[]{ 18, 21, 23, 26, 30 } };
        _240Test.expected = true;
        _240Test.actual = _240Test.solution1.searchMatrix(_240Test.matrix, _240Test.target);
        Assert.assertEquals(_240Test.expected, _240Test.actual);
    }

    @Test
    public void test2() {
        _240Test.target = 0;
        _240Test.matrix = new int[][]{  };
        _240Test.expected = false;
        _240Test.actual = _240Test.solution1.searchMatrix(_240Test.matrix, _240Test.target);
        Assert.assertEquals(_240Test.expected, _240Test.actual);
    }
}

