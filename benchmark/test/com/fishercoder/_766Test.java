package com.fishercoder;


import _766.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _766Test {
    private static Solution1 solution1;

    private static int[][] matrix;

    @Test
    public void test1() {
        _766Test.matrix = new int[][]{ new int[]{ 1, 2, 3, 4 }, new int[]{ 5, 1, 2, 3 }, new int[]{ 9, 5, 1, 2 } };
        Assert.assertEquals(true, _766Test.solution1.isToeplitzMatrix(_766Test.matrix));
    }

    @Test
    public void test2() {
        _766Test.matrix = new int[][]{ new int[]{ 1, 2 }, new int[]{ 2, 2 } };
        Assert.assertEquals(false, _766Test.solution1.isToeplitzMatrix(_766Test.matrix));
    }

    @Test
    public void test3() {
        _766Test.matrix = new int[][]{ new int[]{ 1, 2, 3, 4, 5, 9 }, new int[]{ 5, 1, 2, 3, 4, 5 }, new int[]{ 9, 5, 1, 2, 3, 4 } };
        Assert.assertEquals(true, _766Test.solution1.isToeplitzMatrix(_766Test.matrix));
    }
}

