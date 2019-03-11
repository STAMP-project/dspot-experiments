package com.fishercoder;


import _673.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _673Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test2() {
        _673Test.nums = new int[]{ 2, 2, 2, 2, 2 };
        Assert.assertEquals(5, _673Test.solution1.findNumberOfLIS(_673Test.nums));
    }
}

