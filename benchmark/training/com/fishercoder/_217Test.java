package com.fishercoder;


import _217.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _217Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _217Test.nums = new int[]{ 1, 2, 3, 4, 3 };
        Assert.assertEquals(true, _217Test.solution1.containsDuplicate(_217Test.nums));
    }
}

