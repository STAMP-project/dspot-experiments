package com.fishercoder;


import _684.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _684Test {
    private static Solution1 solution1;

    private static int[][] edges;

    private static int[] expected;

    @Test
    public void test1() {
        _684Test.edges = new int[][]{ new int[]{ 1, 2 }, new int[]{ 1, 3 }, new int[]{ 2, 3 } };
        _684Test.expected = new int[]{ 2, 3 };
        Assert.assertArrayEquals(_684Test.expected, _684Test.solution1.findRedundantConnection(_684Test.edges));
    }

    @Test
    public void test2() {
        _684Test.edges = new int[][]{ new int[]{ 1, 2 }, new int[]{ 2, 3 }, new int[]{ 3, 4 }, new int[]{ 1, 4 }, new int[]{ 1, 5 } };
        _684Test.expected = new int[]{ 1, 4 };
        Assert.assertArrayEquals(_684Test.expected, _684Test.solution1.findRedundantConnection(_684Test.edges));
    }

    @Test
    public void test3() {
        _684Test.edges = new int[][]{ new int[]{ 9, 10 }, new int[]{ 5, 8 }, new int[]{ 2, 6 }, new int[]{ 1, 5 }, new int[]{ 3, 8 }, new int[]{ 4, 9 }, new int[]{ 8, 10 }, new int[]{ 4, 10 }, new int[]{ 6, 8 }, new int[]{ 7, 9 } };
        _684Test.expected = new int[]{ 4, 10 };
        Assert.assertArrayEquals(_684Test.expected, _684Test.solution1.findRedundantConnection(_684Test.edges));
    }

    @Test
    public void test4() {
        _684Test.edges = new int[][]{ new int[]{ 1, 2 }, new int[]{ 2, 3 }, new int[]{ 1, 5 }, new int[]{ 3, 4 }, new int[]{ 1, 4 } };
        _684Test.expected = new int[]{ 1, 4 };
        Assert.assertArrayEquals(_684Test.expected, _684Test.solution1.findRedundantConnection(_684Test.edges));
    }
}

