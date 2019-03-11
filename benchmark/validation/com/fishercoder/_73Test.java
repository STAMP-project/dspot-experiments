package com.fishercoder;


import _73.Solution1;
import _73.Solution2;
import _73.Solution3;
import org.junit.Assert;
import org.junit.Test;


public class _73Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static Solution3 solution3;

    private static int[][] matrix;

    private static int[][] expected;

    @Test
    public void test1() {
        _73Test.matrix = new int[][]{ new int[]{ 0, 0, 0, 5 }, new int[]{ 4, 3, 1, 4 }, new int[]{ 0, 1, 1, 4 }, new int[]{ 1, 2, 1, 3 }, new int[]{ 0, 0, 1, 1 } };
        _73Test.solution1.setZeroes(_73Test.matrix);
        _73Test.expected = new int[][]{ new int[]{ 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 4 }, new int[]{ 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 3 }, new int[]{ 0, 0, 0, 0 } };
        Assert.assertArrayEquals(_73Test.expected, _73Test.matrix);
    }

    @Test
    public void test2() {
        _73Test.matrix = new int[][]{ new int[]{ 0, 0, 0, 5 }, new int[]{ 4, 3, 1, 4 }, new int[]{ 0, 1, 1, 4 }, new int[]{ 1, 2, 1, 3 }, new int[]{ 0, 0, 1, 1 } };
        _73Test.solution2.setZeroes(_73Test.matrix);
        _73Test.expected = new int[][]{ new int[]{ 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 4 }, new int[]{ 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 3 }, new int[]{ 0, 0, 0, 0 } };
        Assert.assertArrayEquals(_73Test.expected, _73Test.matrix);
    }

    @Test
    public void test3() {
        _73Test.matrix = new int[][]{ new int[]{ 0, 0, 0, 5 }, new int[]{ 4, 3, 1, 4 }, new int[]{ 0, 1, 1, 4 }, new int[]{ 1, 2, 1, 3 }, new int[]{ 0, 0, 1, 1 } };
        _73Test.solution3.setZeroes(_73Test.matrix);
        _73Test.expected = new int[][]{ new int[]{ 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 4 }, new int[]{ 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 3 }, new int[]{ 0, 0, 0, 0 } };
        Assert.assertArrayEquals(_73Test.expected, _73Test.matrix);
    }
}

