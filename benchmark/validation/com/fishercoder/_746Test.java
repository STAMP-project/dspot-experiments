package com.fishercoder;


import _746.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _746Test {
    private static Solution1 solution1;

    private static int[] cost;

    @Test
    public void test1() {
        _746Test.cost = new int[]{ 10, 15, 20 };
        Assert.assertEquals(15, _746Test.solution1.minCostClimbingStairs(_746Test.cost));
    }

    @Test
    public void test2() {
        _746Test.cost = new int[]{ 1, 100, 1, 1, 1, 100, 1, 1, 100, 1 };
        Assert.assertEquals(6, _746Test.solution1.minCostClimbingStairs(_746Test.cost));
    }
}

