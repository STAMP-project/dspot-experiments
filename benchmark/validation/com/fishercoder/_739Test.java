package com.fishercoder;


import _739.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _739Test {
    private static Solution1 solution1;

    private static int[] temperatures;

    private static int[] expected;

    @Test
    public void test1() {
        _739Test.temperatures = new int[]{ 73, 74, 75, 71, 69, 72, 76, 73 };
        _739Test.expected = new int[]{ 1, 1, 4, 2, 1, 1, 0, 0 };
        Assert.assertArrayEquals(_739Test.expected, _739Test.solution1.dailyTemperatures(_739Test.temperatures));
    }
}

