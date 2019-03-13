package com.fishercoder;


import com.fishercoder.solutions._451;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/15/17.
 */
public class _451Test {
    private static _451 test;

    private static String expected;

    private static String actual;

    private static String input;

    @Test
    public void test1() {
        _451Test.input = "tree";
        _451Test.expected = "eert";
        _451Test.actual = _451Test.test.frequencySort(_451Test.input);
        Assert.assertEquals(_451Test.expected, _451Test.actual);
    }

    @Test
    public void test2() {
        _451Test.input = "cccaaa";
        _451Test.expected = "aaaccc";
        _451Test.actual = _451Test.test.frequencySort(_451Test.input);
        Assert.assertEquals(_451Test.expected, _451Test.actual);
    }

    @Test
    public void test3() {
        _451Test.input = "Aabb";
        _451Test.expected = "bbAa";
        _451Test.actual = _451Test.test.frequencySort(_451Test.input);
        Assert.assertEquals(_451Test.expected, _451Test.actual);
    }
}

