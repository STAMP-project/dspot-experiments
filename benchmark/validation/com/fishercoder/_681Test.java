package com.fishercoder;


import _681.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _681Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals("19:39", _681Test.solution1.nextClosestTime("19:34"));
    }
}

