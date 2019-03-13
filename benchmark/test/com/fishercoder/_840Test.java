package com.fishercoder;


import _840.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _840Test {
    private static Solution1 test;

    private static int[][] grid;

    @Test
    public void test1() {
        _840Test.grid = new int[][]{ new int[]{ 4, 3, 8, 4 }, new int[]{ 9, 5, 1, 9 }, new int[]{ 2, 7, 6, 2 } };
        Assert.assertEquals(1, _840Test.test.numMagicSquaresInside(_840Test.grid));
    }

    @Test
    public void test2() {
        _840Test.grid = new int[][]{ new int[]{ 5, 5, 5 }, new int[]{ 5, 5, 5 }, new int[]{ 5, 5, 5 } };
        Assert.assertEquals(0, _840Test.test.numMagicSquaresInside(_840Test.grid));
    }

    @Test
    public void test3() {
        _840Test.grid = new int[][]{ new int[]{ 10, 3, 5 }, new int[]{ 1, 6, 11 }, new int[]{ 7, 9, 2 } };
        Assert.assertEquals(0, _840Test.test.numMagicSquaresInside(_840Test.grid));
    }
}

