package com.fishercoder;


import _764.Solution1;
import _764.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _764Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[][] mines;

    @Test
    public void test1() {
        _764Test.mines = new int[][]{ new int[]{ 0, 1 }, new int[]{ 1, 0 }, new int[]{ 1, 1 } };
        Assert.assertEquals(1, _764Test.solution1.orderOfLargestPlusSign(2, _764Test.mines));
        Assert.assertEquals(1, _764Test.solution2.orderOfLargestPlusSign(2, _764Test.mines));
    }

    @Test
    public void test2() {
        _764Test.mines = new int[][]{ new int[]{ 4, 2 } };
        Assert.assertEquals(2, _764Test.solution1.orderOfLargestPlusSign(5, _764Test.mines));
        Assert.assertEquals(2, _764Test.solution2.orderOfLargestPlusSign(5, _764Test.mines));
    }
}

