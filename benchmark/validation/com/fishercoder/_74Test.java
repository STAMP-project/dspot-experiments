package com.fishercoder;


import _74.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _74Test {
    private static Solution1 solution1;

    private static int target;

    private static int[][] matrix;

    @Test
    public void test1() {
        _74Test.target = 3;
        _74Test.matrix = new int[][]{ new int[]{ 1, 3, 5, 7 }, new int[]{ 10, 11, 16, 20 }, new int[]{ 23, 30, 34, 50 } };
        Assert.assertEquals(true, _74Test.solution1.searchMatrix(_74Test.matrix, _74Test.target));
    }
}

