package com.fishercoder;


import _667.Solutoin1;
import _667.Solutoin2;
import org.junit.Assert;
import org.junit.Test;


public class _667Test {
    private static Solutoin1 solution1;

    private static Solutoin2 solution2;

    private static int[] expected;

    @Test
    public void test1() {
        _667Test.expected = new int[]{ 1, 2, 3 };
        Assert.assertArrayEquals(_667Test.expected, _667Test.solution1.constructArray(3, 1));
        Assert.assertArrayEquals(_667Test.expected, _667Test.solution2.constructArray(3, 1));
    }
}

