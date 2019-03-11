package com.fishercoder;


import _740.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _740Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _740Test.nums = new int[]{ 3, 4, 2 };
        Assert.assertEquals(6, _740Test.solution1.deleteAndEarn(_740Test.nums));
    }

    @Test
    public void test2() {
        _740Test.nums = new int[]{ 2, 2, 3, 3, 3, 4 };
        Assert.assertEquals(9, _740Test.solution1.deleteAndEarn(_740Test.nums));
    }
}

