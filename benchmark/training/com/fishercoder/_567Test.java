package com.fishercoder;


import _567.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _567Test {
    private static Solution1 solution1;

    private static boolean expected;

    private static boolean actual;

    private static String s1;

    private static String s2;

    @Test
    public void test1() {
        _567Test.s1 = "ab";
        _567Test.s2 = "eidbaooo";
        _567Test.expected = true;
        _567Test.actual = _567Test.solution1.checkInclusion(_567Test.s1, _567Test.s2);
        Assert.assertEquals(_567Test.expected, _567Test.actual);
    }
}

