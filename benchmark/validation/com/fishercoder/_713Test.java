package com.fishercoder;


import _713.Solution1;
import _713.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _713Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] nums;

    private static int k;

    @Test
    public void test1() {
        _713Test.nums = new int[]{ 1, 2, 3 };
        _713Test.k = 0;
        Assert.assertEquals(0, _713Test.solution2.numSubarrayProductLessThanK(_713Test.nums, _713Test.k));
        Assert.assertEquals(0, _713Test.solution1.numSubarrayProductLessThanK(_713Test.nums, _713Test.k));
    }
}

