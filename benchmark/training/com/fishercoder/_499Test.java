package com.fishercoder;


import com.fishercoder.solutions._505;
import junit.framework.Assert;
import org.junit.Test;


public class _499Test {
    private static _505 test;

    private static int expected;

    private static int actual;

    private static int[][] maze;

    private static int[] start;

    private static int[] destination;

    @Test
    public void test1() {
        _499Test.maze = new int[][]{ new int[]{ 0, 0, 0, 0, 0 }, new int[]{ 1, 1, 0, 0, 1 }, new int[]{ 0, 0, 0, 0, 0 }, new int[]{ 0, 1, 0, 0, 1 }, new int[]{ 0, 1, 0, 0, 0 } };
        _499Test.start = new int[]{ 4, 3 };
        _499Test.destination = new int[]{ 0, 1 };
        _499Test.actual = _499Test.test.shortestDistance(_499Test.maze, _499Test.start, _499Test.destination);
        _499Test.expected = -1;
        Assert.assertEquals(_499Test.expected, _499Test.actual);
    }

    @Test
    public void test2() {
        _499Test.maze = new int[][]{ new int[]{ 0, 0, 1, 0, 0 }, new int[]{ 0, 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 1, 0 }, new int[]{ 1, 1, 0, 1, 1 }, new int[]{ 0, 0, 0, 0, 0 } };
        _499Test.start = new int[]{ 0, 4 };
        _499Test.destination = new int[]{ 4, 4 };
        _499Test.actual = _499Test.test.shortestDistance(_499Test.maze, _499Test.start, _499Test.destination);
        _499Test.expected = 12;
        Assert.assertEquals(_499Test.expected, _499Test.actual);
    }
}

