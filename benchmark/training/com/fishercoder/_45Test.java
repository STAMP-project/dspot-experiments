package com.fishercoder;


import _45.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _45Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _45Test.nums = new int[]{ 2, 3, 1, 1, 4 };
        Assert.assertEquals(2, _45Test.solution1.jump(_45Test.nums));
    }
}

