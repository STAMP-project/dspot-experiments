package com.fishercoder;


import _239.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _239Test {
    private static Solution1 solution1;

    private static int[] expected;

    private static int[] actual;

    private static int[] nums;

    private static int k;

    @Test
    public void test1() {
        _239Test.nums = new int[]{ 1, 3, -1, -3, 5, 3, 6, 7 };
        _239Test.k = 3;
        _239Test.expected = new int[]{ 3, 3, 5, 5, 6, 7 };
        _239Test.actual = _239Test.solution1.maxSlidingWindow(_239Test.nums, _239Test.k);
        Assert.assertArrayEquals(_239Test.expected, _239Test.actual);
    }
}

