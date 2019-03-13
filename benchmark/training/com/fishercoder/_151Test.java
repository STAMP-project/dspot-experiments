package com.fishercoder;


import _151.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _151Test {
    private static Solution1 solution1;

    private static String s;

    @Test
    public void test1() {
        _151Test.s = " ";
        Assert.assertEquals("", _151Test.solution1.reverseWords(_151Test.s));
    }

    @Test
    public void test2() {
        _151Test.s = " 1";
        Assert.assertEquals("1", _151Test.solution1.reverseWords(_151Test.s));
    }

    @Test
    public void test3() {
        _151Test.s = "   a   b ";
        Assert.assertEquals("b a", _151Test.solution1.reverseWords(_151Test.s));
    }

    @Test
    public void test4() {
        _151Test.s = "a b  c";
        Assert.assertEquals("c b a", _151Test.solution1.reverseWords(_151Test.s));
    }
}

