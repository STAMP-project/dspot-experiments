package com.fishercoder;


import _867.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _867Test {
    private static Solution1 solution1;

    private static int[][] A;

    private static int[][] result;

    @Test
    public void test1() {
        _867Test.A = new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6 }, new int[]{ 7, 8, 9 } };
        _867Test.result = new int[][]{ new int[]{ 1, 4, 7 }, new int[]{ 2, 5, 8 }, new int[]{ 3, 6, 9 } };
        Assert.assertArrayEquals(_867Test.result, _867Test.solution1.transpose(_867Test.A));
    }

    @Test
    public void test2() {
        _867Test.A = new int[][]{ new int[]{ 1, 2, 3 } };
        _867Test.result = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 } };
        Assert.assertArrayEquals(_867Test.result, _867Test.solution1.transpose(_867Test.A));
    }

    @Test
    public void test3() {
        _867Test.A = new int[][]{ new int[]{ 1 }, new int[]{ 2 }, new int[]{ 3 } };
        _867Test.result = new int[][]{ new int[]{ 1, 2, 3 } };
        Assert.assertArrayEquals(_867Test.result, _867Test.solution1.transpose(_867Test.A));
    }

    @Test
    public void test4() {
        _867Test.A = new int[][]{ new int[]{ 1, 2, 3, 4 }, new int[]{ 5, 6, 7, 8 }, new int[]{ 9, 10, 11, 12 } };
        _867Test.result = new int[][]{ new int[]{ 1, 5, 9 }, new int[]{ 2, 6, 10 }, new int[]{ 3, 7, 11 }, new int[]{ 4, 8, 12 } };
        Assert.assertArrayEquals(_867Test.result, _867Test.solution1.transpose(_867Test.A));
    }
}

