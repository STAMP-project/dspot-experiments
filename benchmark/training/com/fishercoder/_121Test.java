package com.fishercoder;


import _121.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _121Test {
    private static Solution1 solution1;

    private static int[] prices;

    @Test
    public void test1() {
        _121Test.prices = new int[]{ 7, 1, 5, 3, 6, 4 };
        Assert.assertEquals(5, _121Test.solution1.maxProfit(_121Test.prices));
    }

    @Test
    public void test2() {
        _121Test.prices = new int[]{ 7, 6, 4, 3, 1 };
        Assert.assertEquals(0, _121Test.solution1.maxProfit(_121Test.prices));
    }

    @Test
    public void test3() {
        _121Test.prices = new int[]{ 2, 4, 1 };
        Assert.assertEquals(2, _121Test.solution1.maxProfit(_121Test.prices));
    }

    @Test
    public void test4() {
        _121Test.prices = new int[]{ 1, 2 };
        Assert.assertEquals(1, _121Test.solution1.maxProfit(_121Test.prices));
    }
}

