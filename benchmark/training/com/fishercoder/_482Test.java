package com.fishercoder;


import com.fishercoder.solutions._482;
import junit.framework.Assert;
import org.junit.Test;


public class _482Test {
    private static _482 test;

    private static String expected;

    private static String actual;

    private static String S;

    private static int k;

    @Test
    public void test1() {
        _482Test.S = "2-4A0r7-4k";
        _482Test.k = 4;
        _482Test.expected = "24A0-R74K";
        _482Test.actual = _482Test.test.licenseKeyFormatting(_482Test.S, _482Test.k);
        Assert.assertEquals(_482Test.expected, _482Test.actual);
    }

    @Test
    public void test2() {
        _482Test.S = "2-4A0r7-4k";
        _482Test.k = 3;
        _482Test.expected = "24-A0R-74K";
        _482Test.actual = _482Test.test.licenseKeyFormatting(_482Test.S, _482Test.k);
        Assert.assertEquals(_482Test.expected, _482Test.actual);
    }

    @Test
    public void test3() {
        _482Test.S = "--a-a-a-a--";
        _482Test.k = 2;
        _482Test.expected = "AA-AA";
        _482Test.actual = _482Test.test.licenseKeyFormatting(_482Test.S, _482Test.k);
        Assert.assertEquals(_482Test.expected, _482Test.actual);
    }

    @Test
    public void test4() {
        _482Test.S = "---";
        _482Test.k = 3;
        _482Test.expected = "";
        _482Test.actual = _482Test.test.licenseKeyFormatting(_482Test.S, _482Test.k);
        Assert.assertEquals(_482Test.expected, _482Test.actual);
    }
}

