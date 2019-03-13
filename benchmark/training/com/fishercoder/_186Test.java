package com.fishercoder;


import _186.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _186Test {
    private static Solution1 solution1;

    private static char[] s;

    private static char[] expected;

    @Test
    public void test1() {
        _186Test.s = new char[]{ 'h', 'i', '!' };
        _186Test.solution1.reverseWords(_186Test.s);
        _186Test.expected = new char[]{ 'h', 'i', '!' };
        Assert.assertArrayEquals(_186Test.expected, _186Test.s);
    }
}

