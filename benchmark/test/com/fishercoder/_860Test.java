package com.fishercoder;


import _860.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _860Test {
    private static Solution1 test;

    private static int[] bills;

    @Test
    public void test1() {
        _860Test.bills = new int[]{ 5, 5, 5, 10, 20 };
        Assert.assertEquals(true, _860Test.test.lemonadeChange(_860Test.bills));
    }

    @Test
    public void test2() {
        _860Test.bills = new int[]{ 5, 5, 10 };
        Assert.assertEquals(true, _860Test.test.lemonadeChange(_860Test.bills));
    }

    @Test
    public void test3() {
        _860Test.bills = new int[]{ 10, 10 };
        Assert.assertEquals(false, _860Test.test.lemonadeChange(_860Test.bills));
    }

    @Test
    public void test4() {
        _860Test.bills = new int[]{ 5, 5, 10, 10, 20 };
        Assert.assertEquals(false, _860Test.test.lemonadeChange(_860Test.bills));
    }

    @Test
    public void test5() {
        _860Test.bills = new int[]{ 5, 5, 5, 20, 5, 5, 5, 10, 20, 5, 10, 20, 5, 20, 5, 10, 5, 5, 5, 5 };
        Assert.assertEquals(false, _860Test.test.lemonadeChange(_860Test.bills));
    }
}

