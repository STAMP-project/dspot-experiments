package com.fishercoder;


import _698.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _698Test {
    private static Solution1 solution1;

    private static int[] nums;

    private static int k;

    @Test
    public void test1() {
        _698Test.nums = new int[]{ 4, 3, 2, 3, 5, 2, 1 };
        _698Test.k = 4;
        Assert.assertEquals(true, _698Test.solution1.canPartitionKSubsets(_698Test.nums, _698Test.k));
    }

    @Test
    public void test2() {
        _698Test.nums = new int[]{ -1, 1, 0, 0 };
        _698Test.k = 4;
        Assert.assertEquals(false, _698Test.solution1.canPartitionKSubsets(_698Test.nums, _698Test.k));
    }
}

