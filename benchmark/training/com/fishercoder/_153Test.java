package com.fishercoder;


import _153.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _153Test {
    private static Solution1 solution1;

    private static int expected;

    private static int actual;

    private static int[] nums;

    @Test
    public void test1() {
        _153Test.nums = new int[]{ 4, 5, 6, 7, 0, 1, 2 };
        _153Test.expected = 0;
        _153Test.actual = _153Test.solution1.findMin(_153Test.nums);
        Assert.assertEquals(_153Test.expected, _153Test.actual);
    }

    @Test
    public void test2() {
        _153Test.nums = new int[]{ 1 };
        _153Test.expected = 1;
        _153Test.actual = _153Test.solution1.findMin(_153Test.nums);
        Assert.assertEquals(_153Test.expected, _153Test.actual);
    }

    @Test
    public void test3() {
        _153Test.nums = new int[]{ 2, 1 };
        _153Test.expected = 1;
        _153Test.actual = _153Test.solution1.findMin(_153Test.nums);
        Assert.assertEquals(_153Test.expected, _153Test.actual);
    }

    @Test
    public void test4() {
        _153Test.nums = new int[]{ 2, 3, 4, 5, 1 };
        _153Test.expected = 1;
        _153Test.actual = _153Test.solution1.findMin(_153Test.nums);
        Assert.assertEquals(_153Test.expected, _153Test.actual);
    }
}

