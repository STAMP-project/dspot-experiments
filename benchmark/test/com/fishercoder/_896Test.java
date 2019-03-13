package com.fishercoder;


import _896.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _896Test {
    private static Solution1 solution1;

    private static int[] A;

    @Test
    public void test1() {
        _896Test.A = new int[]{ 1, 3, 2 };
        Assert.assertEquals(false, _896Test.solution1.isMonotonic(_896Test.A));
    }
}

