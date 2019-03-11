package com.fishercoder;


import com.fishercoder.solutions._541;
import junit.framework.Assert;
import org.junit.Test;


public class _541Test {
    private static _541 test;

    private static String expected;

    private static String actual;

    private static String s;

    private static int k;

    @Test
    public void test1() {
        _541Test.s = "abcd";
        _541Test.k = 5;
        _541Test.expected = "dcba";
        _541Test.actual = _541Test.test.reverseStr(_541Test.s, _541Test.k);
        Assert.assertEquals(_541Test.expected, _541Test.actual);
    }

    @Test
    public void test2() {
        _541Test.s = "abcdefg";
        _541Test.k = 2;
        _541Test.expected = "bacdfeg";
        _541Test.actual = _541Test.test.reverseStr(_541Test.s, _541Test.k);
        Assert.assertEquals(_541Test.expected, _541Test.actual);
    }

    @Test
    public void test3() {
        _541Test.s = "abcd";
        _541Test.k = 4;
        _541Test.expected = "dcba";
        _541Test.actual = _541Test.test.reverseStr(_541Test.s, _541Test.k);
        Assert.assertEquals(_541Test.expected, _541Test.actual);
    }

    @Test
    public void test4() {
        _541Test.s = "abcdefg";
        _541Test.k = 3;
        _541Test.expected = "cbadefg";
        _541Test.actual = _541Test.test.reverseStr(_541Test.s, _541Test.k);
        Assert.assertEquals(_541Test.expected, _541Test.actual);
    }

    @Test
    public void test5() {
        _541Test.s = "abcd";
        _541Test.k = 3;
        _541Test.expected = "cbad";
        _541Test.actual = _541Test.test.reverseStr(_541Test.s, _541Test.k);
        Assert.assertEquals(_541Test.expected, _541Test.actual);
    }

    @Test
    public void test6() {
        _541Test.s = "hyzqyljrnigxvdtneasepfahmtyhlohwxmkqcdfehybknvdmfrfvtbsovjbdhevlfxpdaovjgunjqlimjkfnqcqnajmebeddqsgl";
        System.out.println(("s.length() = " + (_541Test.s.length())));
        _541Test.k = 39;
        _541Test.expected = "fdcqkmxwholhytmhafpesaentdvxginrjlyqzyhehybknvdmfrfvtbsovjbdhevlfxpdaovjgunjqllgsqddebemjanqcqnfkjmi";
        _541Test.actual = _541Test.test.reverseStr(_541Test.s, _541Test.k);
        Assert.assertEquals(_541Test.expected, _541Test.actual);
    }
}

