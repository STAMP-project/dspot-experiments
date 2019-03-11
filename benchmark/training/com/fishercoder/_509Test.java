package com.fishercoder;


import _509.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _509Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(1, _509Test.solution1.fib(2));
    }

    @Test
    public void test2() {
        Assert.assertEquals(2, _509Test.solution1.fib(3));
    }

    @Test
    public void test3() {
        Assert.assertEquals(3, _509Test.solution1.fib(4));
    }

    @Test
    public void test4() {
        Assert.assertEquals(0, _509Test.solution1.fib(0));
    }
}

