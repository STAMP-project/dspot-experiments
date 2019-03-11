package com.fishercoder;


import com.fishercoder.solutions._551;
import junit.framework.Assert;
import org.junit.Test;


public class _551Test {
    private static _551 test;

    private static boolean expected;

    private static boolean actual;

    private static String s;

    @Test
    public void test1() {
        _551Test.s = "ALLAPPL";
        _551Test.expected = false;
        _551Test.actual = _551Test.test.checkRecord(_551Test.s);
        Assert.assertEquals(_551Test.expected, _551Test.actual);
    }
}

