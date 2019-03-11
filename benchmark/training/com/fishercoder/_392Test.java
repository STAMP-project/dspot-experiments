package com.fishercoder;


import _392.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _392Test {
    private static Solution1 solution1;

    private static String s;

    private static String t;

    private static boolean expected;

    private static boolean actual;

    @Test
    public void test1() {
        _392Test.s = "abc";
        _392Test.t = "ahbgdc";
        _392Test.expected = true;
        _392Test.actual = _392Test.solution1.isSubsequence(_392Test.s, _392Test.t);
        Assert.assertEquals(_392Test.expected, _392Test.actual);
    }

    @Test
    public void test2() {
        _392Test.s = "axc";
        _392Test.t = "ahbgdc";
        _392Test.expected = false;
        _392Test.actual = _392Test.solution1.isSubsequence(_392Test.s, _392Test.t);
        Assert.assertEquals(_392Test.expected, _392Test.actual);
    }
}

