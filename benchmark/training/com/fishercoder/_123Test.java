package com.fishercoder;


import _123.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _123Test {
    private static Solution1 solution1;

    private static int[] prices;

    @Test
    public void test1() {
        _123Test.prices = new int[]{ 1 };
        Assert.assertEquals(0, _123Test.solution1.maxProfit(_123Test.prices));
    }
}

