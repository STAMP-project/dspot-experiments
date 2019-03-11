package com.fishercoder;


import _52.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _52Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(1, _52Test.solution1.totalNQueens(1));
    }

    @Test
    public void test2() {
        Assert.assertEquals(92, _52Test.solution1.totalNQueens(8));
    }

    @Test
    public void test3() {
        Assert.assertEquals(0, _52Test.solution1.totalNQueens(2));
    }
}

