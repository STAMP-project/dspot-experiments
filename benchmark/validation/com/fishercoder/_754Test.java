package com.fishercoder;


import _754.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _754Test {
    private static Solution1 solution1;

    @Test
    public void test4() {
        Assert.assertEquals(1, _754Test.solution1.reachNumber(1));
    }

    @Test
    public void test2() {
        Assert.assertEquals(3, _754Test.solution1.reachNumber(2));
    }

    @Test
    public void test1() {
        Assert.assertEquals(2, _754Test.solution1.reachNumber(3));
    }

    @Test
    public void test3() {
        Assert.assertEquals(3, _754Test.solution1.reachNumber(4));
    }

    @Test
    public void test5() {
        Assert.assertEquals(5, _754Test.solution1.reachNumber(5));
    }

    @Test
    public void test6() {
        Assert.assertEquals(3, _754Test.solution1.reachNumber(6));
    }

    @Test
    public void test7() {
        Assert.assertEquals(5, _754Test.solution1.reachNumber(7));
    }

    @Test
    public void test8() {
        Assert.assertEquals(4, _754Test.solution1.reachNumber(8));
    }

    @Test
    public void test9() {
        Assert.assertEquals(5, _754Test.solution1.reachNumber(9));
    }

    @Test
    public void test10() {
        Assert.assertEquals(4, _754Test.solution1.reachNumber(10));
    }

    @Test
    public void test11() {
        Assert.assertEquals(15, _754Test.solution1.reachNumber(100));
    }

    @Test
    public void test12() {
        Assert.assertEquals(47, _754Test.solution1.reachNumber(1000));
    }

    @Test
    public void test13() {
        Assert.assertEquals(143, _754Test.solution1.reachNumber(10000));
    }

    @Test
    public void test14() {
        Assert.assertEquals(447, _754Test.solution1.reachNumber(100000));
    }

    @Test
    public void test15() {
        Assert.assertEquals(1415, _754Test.solution1.reachNumber(1000000));
    }

    @Test
    public void test16() {
        Assert.assertEquals(4472, _754Test.solution1.reachNumber(10000000));
    }
}

