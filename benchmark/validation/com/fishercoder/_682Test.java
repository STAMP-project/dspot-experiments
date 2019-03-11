package com.fishercoder;


import _682.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _682Test {
    private static Solution1 solution1;

    private static String[] ops;

    @Test
    public void test1() {
        _682Test.ops = new String[]{ "5", "2", "C", "D", "+" };
        Assert.assertEquals(30, _682Test.solution1.calPoints(_682Test.ops));
    }

    @Test
    public void test2() {
        _682Test.ops = new String[]{ "5", "-2", "4", "C", "D", "9", "+", "+" };
        Assert.assertEquals(27, _682Test.solution1.calPoints(_682Test.ops));
    }
}

