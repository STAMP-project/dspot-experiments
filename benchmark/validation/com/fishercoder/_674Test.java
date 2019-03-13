package com.fishercoder;


import _674.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _674Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _674Test.nums = new int[]{ 1, 3, 5, 4, 7 };
        Assert.assertEquals(3, _674Test.solution1.findLengthOfLCIS(_674Test.nums));
    }

    @Test
    public void test2() {
        _674Test.nums = new int[]{ 2, 2, 2, 2, 2 };
        Assert.assertEquals(1, _674Test.solution1.findLengthOfLCIS(_674Test.nums));
    }
}

