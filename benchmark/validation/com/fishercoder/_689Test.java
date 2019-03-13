package com.fishercoder;


import _689.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _689Test {
    private static Solution1 solution1;

    private static int[] nums;

    private static int[] expected;

    private static int k;

    @Test
    public void test1() {
        _689Test.nums = new int[]{ 1, 2, 1, 2, 6, 7, 5, 1 };
        _689Test.expected = new int[]{ 0, 3, 5 };
        _689Test.k = 2;
        Assert.assertArrayEquals(_689Test.expected, _689Test.solution1.maxSumOfThreeSubarrays(_689Test.nums, 2));
    }
}

