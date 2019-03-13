package com.fishercoder;


import com.fishercoder.solutions._415;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/8/17.
 */
public class _415Test {
    private static _415 test;

    private static String expected;

    private static String actual;

    private static String num1;

    private static String num2;

    @Test
    public void test1() {
        _415Test.num1 = "123";
        _415Test.num2 = "34567";
        _415Test.expected = "34690";
        _415Test.actual = _415Test.test.addStrings(_415Test.num1, _415Test.num2);
        Assert.assertEquals(_415Test.expected, _415Test.actual);
    }

    @Test
    public void test2() {
        _415Test.num1 = "1";
        _415Test.num2 = "9";
        _415Test.expected = "10";
        _415Test.actual = _415Test.test.addStrings(_415Test.num1, _415Test.num2);
        Assert.assertEquals(_415Test.expected, _415Test.actual);
    }

    @Test
    public void test3() {
        _415Test.num1 = "9";
        _415Test.num2 = "99";
        _415Test.expected = "108";
        _415Test.actual = _415Test.test.addStrings(_415Test.num1, _415Test.num2);
        Assert.assertEquals(_415Test.expected, _415Test.actual);
    }
}

