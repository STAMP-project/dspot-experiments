package com.fishercoder;


import com.fishercoder.solutions._424;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/7/17.
 */
public class _424Test {
    private static _424 test;

    private static String s;

    private static int k;

    private static int actual;

    private static int expected;

    @Test
    public void test1() {
        _424Test.s = "ABAB";
        _424Test.k = 2;
        _424Test.actual = _424Test.test.characterReplacement(_424Test.s, _424Test.k);
        _424Test.expected = 4;
        Assert.assertEquals(_424Test.expected, _424Test.actual);
    }

    @Test
    public void test2() {
        _424Test.s = "AABABBA";
        _424Test.k = 1;
        _424Test.actual = _424Test.test.characterReplacement(_424Test.s, _424Test.k);
        _424Test.expected = 4;
        Assert.assertEquals(_424Test.expected, _424Test.actual);
    }

    @Test
    public void test3() {
        _424Test.s = "AAAA";
        _424Test.k = 2;
        _424Test.actual = _424Test.test.characterReplacement(_424Test.s, _424Test.k);
        _424Test.expected = 4;
        Assert.assertEquals(_424Test.expected, _424Test.actual);
    }

    @Test
    public void test4() {
        _424Test.s = "AAAB";
        _424Test.k = 0;
        _424Test.actual = _424Test.test.characterReplacement(_424Test.s, _424Test.k);
        _424Test.expected = 3;
        Assert.assertEquals(_424Test.expected, _424Test.actual);
    }

    @Test
    public void test5() {
        _424Test.s = "AABA";
        _424Test.k = 0;
        _424Test.actual = _424Test.test.characterReplacement(_424Test.s, _424Test.k);
        _424Test.expected = 2;
        Assert.assertEquals(_424Test.expected, _424Test.actual);
    }

    @Test
    public void test6() {
        _424Test.s = "ABBB";
        _424Test.k = 2;
        _424Test.actual = _424Test.test.characterReplacement(_424Test.s, _424Test.k);
        _424Test.expected = 4;
        Assert.assertEquals(_424Test.expected, _424Test.actual);
    }
}

