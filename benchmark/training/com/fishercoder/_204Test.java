package com.fishercoder;


import _204.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _204Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(0, _204Test.solution1.countPrimes(2));
    }

    @Test
    public void test2() {
        Assert.assertEquals(1, _204Test.solution1.countPrimes(3));
    }

    @Test
    public void test3() {
        Assert.assertEquals(2, _204Test.solution1.countPrimes(5));
    }

    @Test
    public void test4() {
        Assert.assertEquals(114155, _204Test.solution1.countPrimes(1500000));
    }

    @Test
    public void test5() {
        Assert.assertEquals(10, _204Test.solution1.countPrimes(30));
    }

    @Test
    public void test6() {
        Assert.assertEquals(4, _204Test.solution1.countPrimes(10));
    }

    @Test
    public void test7() {
        Assert.assertEquals(8, _204Test.solution1.countPrimes(20));
    }

    @Test
    public void test8() {
        Assert.assertEquals(12, _204Test.solution1.countPrimes(40));
    }

    @Test
    public void test9() {
        Assert.assertEquals(15, _204Test.solution1.countPrimes(50));
    }
}

