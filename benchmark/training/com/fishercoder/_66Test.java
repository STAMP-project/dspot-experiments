package com.fishercoder;


import _66.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _66Test {
    private static Solution1 solution1;

    private static int[] digits;

    @Test
    public void test1() {
        _66Test.digits = new int[]{ 9, 9, 9, 9 };
        Assert.assertArrayEquals(new int[]{ 1, 0, 0, 0, 0 }, _66Test.solution1.plusOne(_66Test.digits));
    }

    @Test
    public void test2() {
        _66Test.digits = new int[]{ 8, 9, 9, 9 };
        Assert.assertArrayEquals(new int[]{ 9, 0, 0, 0 }, _66Test.solution1.plusOne(_66Test.digits));
    }

    @Test
    public void test3() {
        _66Test.digits = new int[]{ 2, 4, 9, 3, 9 };
        Assert.assertArrayEquals(new int[]{ 2, 4, 9, 4, 0 }, _66Test.solution1.plusOne(_66Test.digits));
    }
}

