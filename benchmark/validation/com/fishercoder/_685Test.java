package com.fishercoder;


import _685.Solution1;
import _685.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _685Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[][] edges;

    private static int[] expected;

    @Test
    public void test1() {
        _685Test.edges = new int[][]{ new int[]{ 2, 1 }, new int[]{ 3, 1 }, new int[]{ 4, 2 }, new int[]{ 1, 4 } };
        _685Test.expected = new int[]{ 2, 1 };
        Assert.assertArrayEquals(_685Test.expected, _685Test.solution1.findRedundantDirectedConnection(_685Test.edges));
        Assert.assertArrayEquals(_685Test.expected, _685Test.solution2.findRedundantDirectedConnection(_685Test.edges));
    }

    @Test
    public void test2() {
        _685Test.edges = new int[][]{ new int[]{ 2, 1 }, new int[]{ 1, 4 }, new int[]{ 4, 3 }, new int[]{ 3, 2 } };
        _685Test.expected = new int[]{ 3, 2 };
        Assert.assertArrayEquals(_685Test.expected, _685Test.solution1.findRedundantDirectedConnection(_685Test.edges));
        Assert.assertArrayEquals(_685Test.expected, _685Test.solution2.findRedundantDirectedConnection(_685Test.edges));
    }

    @Test
    public void test3() {
        _685Test.edges = new int[][]{ new int[]{ 1, 2 }, new int[]{ 1, 3 }, new int[]{ 2, 3 } };
        _685Test.expected = new int[]{ 2, 3 };
        // assertArrayEquals(expected, solution1.findRedundantDirectedConnection(edges));
        Assert.assertArrayEquals(_685Test.expected, _685Test.solution2.findRedundantDirectedConnection(_685Test.edges));
    }
}

