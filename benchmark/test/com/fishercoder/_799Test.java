package com.fishercoder;


import _799.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _799Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(0.125, _799Test.solution1.champagneTower(8, 3, 0), 0);
    }

    @Test
    public void test2() {
        Assert.assertEquals(0.875, _799Test.solution1.champagneTower(8, 3, 1), 0);
    }

    @Test
    public void test3() {
        Assert.assertEquals(0.875, _799Test.solution1.champagneTower(8, 3, 2), 0);
    }

    @Test
    public void test4() {
        Assert.assertEquals(0.125, _799Test.solution1.champagneTower(8, 3, 3), 0);
    }

    @Test
    public void test5() {
        Assert.assertEquals(0.0, _799Test.solution1.champagneTower(1, 1, 1), 0);
    }

    @Test
    public void test6() {
        Assert.assertEquals(0.5, _799Test.solution1.champagneTower(2, 1, 1), 0);
    }

    @Test
    public void test7() {
        Assert.assertEquals(0.0, _799Test.solution1.champagneTower(1000000000, 99, 99), 0);
    }
}

