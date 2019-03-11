package com.fishercoder;


import _397.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _397Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(17, _397Test.solution1.integerReplacement(65535));
    }

    @Test
    public void test2() {
        Assert.assertEquals(14, _397Test.solution1.integerReplacement(1234));
    }

    @Test
    public void test3() {
        Assert.assertEquals(3, _397Test.solution1.integerReplacement(5));
    }
}

