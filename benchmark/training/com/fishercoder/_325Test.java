package com.fishercoder;


import _325.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _325Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _325Test.nums = new int[]{ -2, -1, 2, 1 };
        Assert.assertEquals(2, _325Test.solution1.maxSubArrayLen(_325Test.nums, 1));
    }

    @Test
    public void test2() {
        _325Test.nums = new int[]{ 1, -1, 5, -2, 3 };
        Assert.assertEquals(4, _325Test.solution1.maxSubArrayLen(_325Test.nums, 3));
    }
}

