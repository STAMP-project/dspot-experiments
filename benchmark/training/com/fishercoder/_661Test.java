package com.fishercoder;


import _661.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _661Test {
    private static Solution1 solution1;

    private static int[][] M;

    private static int[][] expected;

    @Test
    public void test1() {
        _661Test.M = new int[][]{ new int[]{ 1, 1, 1 }, new int[]{ 1, 0, 1 }, new int[]{ 1, 1, 1 } };
        _661Test.expected = _661Test.M = new int[][]{ new int[]{ 0, 0, 0 }, new int[]{ 0, 0, 0 }, new int[]{ 0, 0, 0 } };
        Assert.assertArrayEquals(_661Test.expected, _661Test.solution1.imageSmoother(_661Test.M));
    }
}

