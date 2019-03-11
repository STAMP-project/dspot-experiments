package com.fishercoder;


import _69.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _69Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(4, _69Test.solution1.mySqrt(16));
    }

    @Test
    public void test2() {
        Assert.assertEquals(2, _69Test.solution1.mySqrt(8));
    }
}

