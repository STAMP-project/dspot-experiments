package com.fishercoder;


import _356.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _356Test {
    private static Solution1 solution1;

    private static int[][] points;

    @Test
    public void test1() {
        _356Test.points = new int[][]{ new int[]{ 1, 1 }, new int[]{ -1, 1 } };
        Assert.assertEquals(true, _356Test.solution1.isReflected(_356Test.points));
    }

    @Test
    public void test2() {
        _356Test.points = new int[][]{ new int[]{ 1, 1 }, new int[]{ -1, -1 } };
        Assert.assertEquals(false, _356Test.solution1.isReflected(_356Test.points));
    }
}

