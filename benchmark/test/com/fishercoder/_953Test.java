package com.fishercoder;


import _953.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _953Test {
    private static Solution1 solution1;

    private static String[] words;

    private static String order;

    @Test
    public void test1() {
        _953Test.words = new String[]{ "hello", "leetcode" };
        _953Test.order = "hlabcdefgijkmnopqrstuvwxyz";
        Assert.assertEquals(true, _953Test.solution1.isAlienSorted(_953Test.words, _953Test.order));
    }

    @Test
    public void test2() {
        _953Test.words = new String[]{ "word", "world", "row" };
        _953Test.order = "worldabcefghijkmnpqstuvxyz";
        Assert.assertEquals(false, _953Test.solution1.isAlienSorted(_953Test.words, _953Test.order));
    }

    @Test
    public void test3() {
        _953Test.words = new String[]{ "apple", "app" };
        _953Test.order = "abcdefghijklmnopqrstuvwxyz";
        Assert.assertEquals(false, _953Test.solution1.isAlienSorted(_953Test.words, _953Test.order));
    }
}

