package com.fishercoder;


import _832.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _832Test {
    private static Solution1 solution1;

    private static int[][] expected;

    private static int[][] A;

    @Test
    public void test1() {
        _832Test.A = new int[][]{ new int[]{ 1, 1, 0 }, new int[]{ 1, 0, 1 }, new int[]{ 0, 0, 0 } };
        _832Test.expected = new int[][]{ new int[]{ 1, 0, 0 }, new int[]{ 0, 1, 0 }, new int[]{ 1, 1, 1 } };
        Assert.assertArrayEquals(_832Test.expected, _832Test.solution1.flipAndInvertImage(_832Test.A));
    }

    @Test
    public void test2() {
        _832Test.A = new int[][]{ new int[]{ 1, 1, 0, 0 }, new int[]{ 1, 0, 0, 1 }, new int[]{ 0, 1, 1, 1 }, new int[]{ 1, 0, 1, 0 } };
        _832Test.expected = new int[][]{ new int[]{ 1, 1, 0, 0 }, new int[]{ 0, 1, 1, 0 }, new int[]{ 0, 0, 0, 1 }, new int[]{ 1, 0, 1, 0 } };
        Assert.assertArrayEquals(_832Test.expected, _832Test.solution1.flipAndInvertImage(_832Test.A));
    }
}

