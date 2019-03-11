package com.fishercoder;


import com.fishercoder.solutions._490;
import junit.framework.Assert;
import org.junit.Test;


public class _490Test {
    private static _490 test;

    private static boolean expected;

    private static boolean actual;

    private static int[][] maze;

    private static int[] start;

    private static int[] destination;

    @Test
    public void test1() {
        _490Test.maze = new int[][]{ new int[]{ 0, 0, 0, 0, 0 }, new int[]{ 1, 1, 0, 0, 1 }, new int[]{ 0, 0, 0, 0, 0 }, new int[]{ 0, 1, 0, 0, 1 }, new int[]{ 0, 1, 0, 0, 0 } };
        _490Test.start = new int[]{ 4, 3 };
        _490Test.destination = new int[]{ 0, 1 };
        _490Test.actual = _490Test.test.hasPath(_490Test.maze, _490Test.start, _490Test.destination);
        _490Test.expected = false;
        Assert.assertEquals(_490Test.expected, _490Test.actual);
    }

    @Test
    public void test2() {
        _490Test.maze = new int[][]{ new int[]{ 0, 0, 1, 0, 0 }, new int[]{ 0, 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 1, 0 }, new int[]{ 1, 1, 0, 1, 1 }, new int[]{ 0, 0, 0, 0, 0 } };
        _490Test.start = new int[]{ 0, 4 };
        _490Test.destination = new int[]{ 4, 4 };
        _490Test.actual = _490Test.test.hasPath(_490Test.maze, _490Test.start, _490Test.destination);
        _490Test.expected = true;
        Assert.assertEquals(_490Test.expected, _490Test.actual);
    }
}

