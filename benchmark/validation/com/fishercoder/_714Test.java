package com.fishercoder;


import _714.Solution1;
import _714.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _714Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] prices;

    private static int fee;

    @Test
    public void test1() {
        _714Test.prices = new int[]{ 1, 3, 2, 8, 4, 9 };
        _714Test.fee = 2;
        Assert.assertEquals(8, _714Test.solution1.maxProfit(_714Test.prices, _714Test.fee));
        Assert.assertEquals(8, _714Test.solution2.maxProfit(_714Test.prices, _714Test.fee));
    }

    @Test
    public void test2() {
        _714Test.prices = new int[]{ 1, 3, 7, 5, 10, 3 };
        _714Test.fee = 3;
        Assert.assertEquals(6, _714Test.solution1.maxProfit(_714Test.prices, _714Test.fee));
        Assert.assertEquals(6, _714Test.solution2.maxProfit(_714Test.prices, _714Test.fee));
    }

    @Test
    public void test3() {
        _714Test.prices = new int[]{ 1, 4, 6, 2, 8, 3, 10, 14 };
        _714Test.fee = 3;
        Assert.assertEquals(13, _714Test.solution1.maxProfit(_714Test.prices, _714Test.fee));
        Assert.assertEquals(13, _714Test.solution2.maxProfit(_714Test.prices, _714Test.fee));
    }
}

