package com.fishercoder;


import _985.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _985Test {
    private static Solution1 solution1;

    private static int[] expected;

    private static int[] actual;

    private static int[] A;

    private static int[][] queries;

    @Test
    public void test1() {
        _985Test.A = new int[]{ 1, 2, 3, 4 };
        _985Test.queries = new int[][]{ new int[]{ 1, 0 }, new int[]{ -3, 1 }, new int[]{ -4, 0 }, new int[]{ 2, 3 } };
        _985Test.expected = new int[]{ 8, 6, 2, 4 };
        _985Test.actual = _985Test.solution1.sumEvenAfterQueries(_985Test.A, _985Test.queries);
        Assert.assertArrayEquals(_985Test.expected, _985Test.actual);
    }
}

