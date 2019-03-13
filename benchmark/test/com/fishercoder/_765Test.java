package com.fishercoder;


import _765.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _765Test {
    private static Solution1 solution1;

    private static int[] row;

    @Test
    public void test1() {
        _765Test.row = new int[]{ 0, 2, 1, 3 };
        Assert.assertEquals(1, _765Test.solution1.minSwapsCouples(_765Test.row));
    }

    @Test
    public void test2() {
        _765Test.row = new int[]{ 3, 2, 0, 1 };
        Assert.assertEquals(0, _765Test.solution1.minSwapsCouples(_765Test.row));
    }

    @Test
    public void test3() {
        _765Test.row = new int[]{ 0, 4, 7, 3, 1, 5, 2, 8, 6, 9 };
        Assert.assertEquals(3, _765Test.solution1.minSwapsCouples(_765Test.row));
    }

    @Test
    public void test4() {
        _765Test.row = new int[]{ 5, 6, 4, 0, 2, 1, 9, 3, 8, 7, 11, 10 };
        Assert.assertEquals(4, _765Test.solution1.minSwapsCouples(_765Test.row));
    }
}

