package com.fishercoder;


import _723.Solution1;
import org.junit.Test;


public class _723Test {
    private static Solution1 solution1;

    private static int[][] board;

    private static int[][] expected;

    @Test
    public void test1() {
        _723Test.board = new int[][]{ new int[]{ 110, 5, 112, 113, 114 }, new int[]{ 210, 211, 5, 213, 214 }, new int[]{ 310, 311, 3, 313, 314 }, new int[]{ 410, 411, 412, 5, 414 }, new int[]{ 5, 1, 512, 3, 3 }, new int[]{ 610, 4, 1, 613, 614 }, new int[]{ 710, 1, 2, 713, 714 }, new int[]{ 810, 1, 2, 1, 1 }, new int[]{ 1, 1, 2, 2, 2 }, new int[]{ 4, 1, 4, 4, 1014 } };
        _723Test.expected = new int[][]{ new int[]{ 0, 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 0, 0 }, new int[]{ 0, 0, 0, 0, 0 }, new int[]{ 110, 0, 0, 0, 114 }, new int[]{ 210, 0, 0, 0, 214 }, new int[]{ 310, 0, 0, 113, 314 }, new int[]{ 410, 0, 0, 213, 414 }, new int[]{ 610, 211, 112, 313, 614 }, new int[]{ 710, 311, 412, 613, 714 }, new int[]{ 810, 411, 512, 713, 1014 } };
        assert2dArrayEquals(_723Test.expected, _723Test.solution1.candyCrush(_723Test.board));
    }
}

