package com.fishercoder;


import _63.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _63Test {
    private static Solution1 solution1;

    private static int[][] obstacleGrid;

    @Test
    public void test1() {
        _63Test.obstacleGrid = new int[][]{ new int[]{ 0, 0 }, new int[]{ 0, 1 } };
        Assert.assertEquals(0, _63Test.solution1.uniquePathsWithObstacles(_63Test.obstacleGrid));
    }

    @Test
    public void test2() {
        _63Test.obstacleGrid = new int[][]{ new int[]{ 0, 0, 0 }, new int[]{ 0, 1, 0 }, new int[]{ 0, 0, 0 } };
        Assert.assertEquals(2, _63Test.solution1.uniquePathsWithObstacles(_63Test.obstacleGrid));
    }

    @Test
    public void test3() {
        int[][] obstacleGrid = new int[][]{ new int[]{ 1, 0 } };
        Assert.assertEquals(0, _63Test.solution1.uniquePathsWithObstacles(obstacleGrid));
    }
}

