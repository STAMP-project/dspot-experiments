package com.fishercoder;


import _10.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _10Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(true, _10Test.solution1.isMatch("", ""));
    }

    @Test
    public void test2() {
        Assert.assertEquals(false, _10Test.solution1.isMatch("aa", "a"));
    }

    @Test
    public void test3() {
        Assert.assertEquals(true, _10Test.solution1.isMatch("aab", "c*a*b"));
    }
}

