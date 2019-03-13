package com.fishercoder;


import _758.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _758Test {
    private static Solution1 solution1;

    private static String[] words;

    @Test
    public void test1() {
        _758Test.words = new String[]{ "ab", "bc" };
        Assert.assertEquals("a<b>abc</b>d", _758Test.solution1.boldWords(_758Test.words, "aabcd"));
    }

    @Test
    public void test2() {
        _758Test.words = new String[]{ "ccb", "b", "d", "cba", "dc" };
        Assert.assertEquals("eeaa<b>d</b>a<b>d</b>a<b>dc</b>", _758Test.solution1.boldWords(_758Test.words, "eeaadadadc"));
    }
}

