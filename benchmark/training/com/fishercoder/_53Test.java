package com.fishercoder;


import _53.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _53Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _53Test.nums = new int[]{ -2, 1, -3, 4, -1, 2, 1, -5, 4 };
        Assert.assertEquals(6, _53Test.solution1.maxSubArray(_53Test.nums));
    }
}

