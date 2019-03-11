package com.fishercoder;


import _370.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _370Test {
    private static Solution1 solution1;

    private static int[][] updates;

    private static int length;

    private static int[] expected;

    @Test
    public void test1() {
        _370Test.updates = new int[][]{ new int[]{ 1, 3, 2 }, new int[]{ 2, 4, 3 }, new int[]{ 0, 2, -2 } };
        _370Test.length = 5;
        _370Test.expected = new int[]{ -2, 0, 3, 5, 3 };
        Assert.assertArrayEquals(_370Test.expected, _370Test.solution1.getModifiedArray(_370Test.length, _370Test.updates));
    }
}

