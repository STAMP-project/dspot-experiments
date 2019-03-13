package com.fishercoder;


import com.fishercoder.solutions._504;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/15/17.
 */
public class _504Test {
    private static _504 test;

    private static String expected;

    private static String actual;

    private static int num;

    @Test
    public void test1() {
        _504Test.num = 100;
        _504Test.expected = "202";
        _504Test.actual = _504Test.test.convertToBase7(_504Test.num);
        Assert.assertEquals(_504Test.expected, _504Test.actual);
    }

    @Test
    public void test2() {
        _504Test.num = -7;
        _504Test.expected = "-10";
        _504Test.actual = _504Test.test.convertToBase7(_504Test.num);
        Assert.assertEquals(_504Test.expected, _504Test.actual);
    }
}

