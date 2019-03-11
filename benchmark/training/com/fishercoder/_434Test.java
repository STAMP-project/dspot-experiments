package com.fishercoder;


import com.fishercoder.solutions._434;
import junit.framework.Assert;
import org.junit.Test;


public class _434Test {
    private static _434 test;

    private static int expected;

    private static int actual;

    private static String s;

    @Test
    public void test1() {
        _434Test.s = "Hello, my name is John";
        _434Test.expected = 5;
        _434Test.actual = _434Test.test.countSegments(_434Test.s);
        Assert.assertEquals(_434Test.expected, _434Test.actual);
    }

    @Test
    public void test2() {
        _434Test.s = ", , , ,        a, eaefa";
        _434Test.expected = 6;
        _434Test.actual = _434Test.test.countSegments(_434Test.s);
        Assert.assertEquals(_434Test.expected, _434Test.actual);
    }
}

