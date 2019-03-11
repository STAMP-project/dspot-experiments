package com.fishercoder;


import _122.Solution1;
import _122.Solution2;
import junit.framework.Assert;
import org.junit.Test;


public class _122Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] prices;

    @Test
    public void test1() {
        _122Test.prices = new int[]{ 1, 2, 4 };
        Assert.assertEquals(3, _122Test.solution1.maxProfit(_122Test.prices));
        Assert.assertEquals(3, _122Test.solution2.maxProfit(_122Test.prices));
    }
}

