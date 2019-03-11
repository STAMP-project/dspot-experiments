package com.fishercoder;


import com.fishercoder.solutions._500;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/15/17.
 */
public class _500Test {
    private static _500 test;

    private static String[] expected;

    private static String[] actual;

    private String[] words;

    @Test
    public void test1() {
        words = new String[]{ "Alaska", "Hello", "Dad", "Peace" };
        _500Test.expected = new String[]{ "Alaska", "Dad" };
        _500Test.actual = _500Test.test.findWords(words);
        Assert.assertArrayEquals(_500Test.expected, _500Test.actual);
    }
}

