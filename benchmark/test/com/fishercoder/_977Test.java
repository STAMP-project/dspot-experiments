package com.fishercoder;


import _977.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _977Test {
    private static Solution1 solution1;

    private static int[] A;

    @Test
    public void test1() {
        _977Test.A = new int[]{ -4, -1, 0, 3, 10 };
        Assert.assertArrayEquals(new int[]{ 0, 1, 9, 16, 100 }, _977Test.solution1.sortedSquares(_977Test.A));
    }

    @Test
    public void test2() {
        _977Test.A = new int[]{ -7, -3, 2, 3, 11 };
        Assert.assertArrayEquals(new int[]{ 4, 9, 9, 49, 121 }, _977Test.solution1.sortedSquares(_977Test.A));
    }
}

