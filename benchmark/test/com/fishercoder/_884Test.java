package com.fishercoder;


import _884.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _884Test {
    private static Solution1 solution1;

    private static String[] expected;

    @Test
    public void test1() {
        _884Test.expected = new String[]{ "sweet", "sour" };
        Assert.assertArrayEquals(_884Test.expected, _884Test.solution1.uncommonFromSentences("this apple is sweet", "this apple is sour"));
    }

    @Test
    public void test2() {
        _884Test.expected = new String[]{ "banana" };
        Assert.assertArrayEquals(_884Test.expected, _884Test.solution1.uncommonFromSentences("apple apple", "banana"));
    }
}

