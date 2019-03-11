package com.fishercoder;


import _767.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _767Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals("aba", _767Test.solution1.reorganizeString("aab"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("", _767Test.solution1.reorganizeString("aaab"));
    }

    @Test
    public void test3() {
        Assert.assertEquals("bababab", _767Test.solution1.reorganizeString("aaabbbb"));
    }

    @Test
    public void test4() {
        Assert.assertEquals("vovlv", _767Test.solution1.reorganizeString("vvvlo"));
    }
}

