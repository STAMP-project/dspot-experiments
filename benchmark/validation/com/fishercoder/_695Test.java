package com.fishercoder;


import com.fishercoder.solutions._695;
import junit.framework.TestCase;
import org.junit.Test;


public class _695Test {
    private static _695 test;

    private static int[][] grid;

    @Test
    public void test1() {
        _695Test.grid = new int[][]{ new int[]{ 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0 }, new int[]{ 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, new int[]{ 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0 }, new int[]{ 0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0 }, new int[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 }, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0 }, new int[]{ 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0 } };
        TestCase.assertEquals(6, _695Test.test.maxAreaOfIsland(_695Test.grid));
    }
}

