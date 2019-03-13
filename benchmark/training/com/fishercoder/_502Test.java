package com.fishercoder;


import com.fishercoder.solutions._502;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/2/17.
 */
public class _502Test {
    private static _502 test;

    private static int[] Profits;

    private static int[] Capital;

    @Test
    public void test1() {
        _502Test.Profits = new int[]{ 1, 2, 3 };
        _502Test.Capital = new int[]{ 0, 1, 1 };
        Assert.assertEquals(4, _502Test.test.findMaximizedCapital(2, 0, _502Test.Profits, _502Test.Capital));
    }
}

