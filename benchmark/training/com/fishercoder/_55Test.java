package com.fishercoder;


import _55.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _55Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _55Test.nums = new int[]{ 0, 2, 3 };
        Assert.assertEquals(false, _55Test.solution1.canJump(_55Test.nums));
    }

    @Test
    public void test2() {
        _55Test.nums = new int[]{ 1, 2 };
        Assert.assertEquals(true, _55Test.solution1.canJump(_55Test.nums));
    }
}

