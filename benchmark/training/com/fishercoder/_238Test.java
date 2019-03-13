package com.fishercoder;


import _238.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _238Test {
    private static Solution1 solution1;

    private static int[] expected;

    private static int[] actual;

    private static int[] nums;

    @Test
    public void test1() {
        _238Test.nums = new int[]{ 0, 0 };
        _238Test.expected = new int[]{ 0, 0 };
        _238Test.actual = _238Test.solution1.productExceptSelf(_238Test.nums);
        Assert.assertArrayEquals(_238Test.expected, _238Test.actual);
    }

    @Test
    public void test2() {
        _238Test.nums = new int[]{ 1, 0 };
        _238Test.expected = new int[]{ 0, 1 };
        _238Test.actual = _238Test.solution1.productExceptSelf(_238Test.nums);
        Assert.assertArrayEquals(_238Test.expected, _238Test.actual);
    }

    @Test
    public void test3() {
        _238Test.nums = new int[]{ 1, 2, 3, 4 };
        _238Test.expected = new int[]{ 24, 12, 8, 6 };
        _238Test.actual = _238Test.solution1.productExceptSelf(_238Test.nums);
        Assert.assertArrayEquals(_238Test.expected, _238Test.actual);
    }
}

