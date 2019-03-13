package com.fishercoder;


import com.fishercoder.solutions._555;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 4/29/17.
 */
public class _555Test {
    private static _555 test;

    private static String expected;

    private static String actual;

    private static String[] strs;

    @Test
    public void test1() {
        _555Test.strs = new String[]{ "abc", "xyz" };
        _555Test.expected = "zyxcba";
        _555Test.actual = _555Test.test.splitLoopedString(_555Test.strs);
        Assert.assertEquals(_555Test.expected, _555Test.actual);
    }

    @Test
    public void test2() {
        _555Test.strs = new String[]{ "lc", "evol", "cdy" };
        _555Test.expected = "ylclovecd";
        _555Test.actual = _555Test.test.splitLoopedString(_555Test.strs);
        Assert.assertEquals(_555Test.expected, _555Test.actual);
    }
}

