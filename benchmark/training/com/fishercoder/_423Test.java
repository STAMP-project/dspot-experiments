package com.fishercoder;


import com.fishercoder.solutions._423;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 4/27/17.
 */
public class _423Test {
    private static _423 test;

    private static String expected;

    private static String actual;

    private static String s;

    @Test
    public void test1() {
        _423Test.s = "fviefuro";
        _423Test.expected = "45";
        _423Test.actual = _423Test.test.originalDigits(_423Test.s);
        Assert.assertEquals(_423Test.expected, _423Test.actual);
    }
}

