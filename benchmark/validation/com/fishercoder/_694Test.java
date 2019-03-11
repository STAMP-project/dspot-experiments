package com.fishercoder;


import _694.Solution1;
import _694.Solution2;
import junit.framework.TestCase;
import org.junit.Test;


public class _694Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[][] grid;

    @Test
    public void test1() {
        _694Test.grid = new int[][]{ new int[]{ 1, 1, 0, 1, 1 }, new int[]{ 1, 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 0, 1 }, new int[]{ 1, 1, 0, 1, 1 } };
        // assertEquals(3, solution1.numDistinctIslands(grid));
        TestCase.assertEquals(3, _694Test.solution2.numDistinctIslands(_694Test.grid));
    }

    @Test
    public void test2() {
        _694Test.grid = new int[][]{ new int[]{ 1, 1, 0, 0, 0 }, new int[]{ 1, 1, 0, 0, 0 }, new int[]{ 0, 0, 0, 1, 1 }, new int[]{ 0, 0, 0, 1, 1 } };
        // assertEquals(1, solution1.numDistinctIslands(grid));
        TestCase.assertEquals(1, _694Test.solution2.numDistinctIslands(_694Test.grid));
    }
}

