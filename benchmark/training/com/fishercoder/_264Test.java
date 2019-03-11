package com.fishercoder;


import _264.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _264Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(12, _264Test.solution1.nthUglyNumber(10));
    }

    @Test
    public void test2() {
        Assert.assertEquals(402653184, _264Test.solution1.nthUglyNumber(1352));
    }
}

