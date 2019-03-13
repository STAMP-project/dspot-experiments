package com.fishercoder;


import _670.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _670Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(7236, _670Test.solution1.maximumSwap(2736));
    }

    @Test
    public void test2() {
        Assert.assertEquals(9973, _670Test.solution1.maximumSwap(9973));
    }

    @Test
    public void test3() {
        Assert.assertEquals(73236, _670Test.solution1.maximumSwap(23736));
    }

    @Test
    public void test4() {
        Assert.assertEquals(98213, _670Test.solution1.maximumSwap(91283));
    }
}

