package com.fishercoder;


import _494.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _494Test {
    private static Solution1 solution1;

    private static int expected;

    private static int actual;

    private static int S;

    private static int[] nums;

    @Test
    public void test1() {
        _494Test.S = 3;
        _494Test.nums = new int[]{ 1, 1, 1, 1, 1 };
        _494Test.expected = 5;
        _494Test.actual = _494Test.solution1.findTargetSumWays(_494Test.nums, _494Test.S);
        Assert.assertEquals(_494Test.expected, _494Test.actual);
    }

    @Test
    public void test2() {
        _494Test.S = 3;
        _494Test.nums = new int[]{ 1, 1, 1, 1, 5 };
        _494Test.expected = 4;
        _494Test.actual = _494Test.solution1.findTargetSumWays(_494Test.nums, _494Test.S);
        Assert.assertEquals(_494Test.expected, _494Test.actual);
    }
}

