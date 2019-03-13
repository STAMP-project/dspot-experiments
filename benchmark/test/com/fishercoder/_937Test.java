package com.fishercoder;


import _937.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _937Test {
    private static Solution1 solution1;

    private static String[] logs;

    private static String[] expected;

    @Test
    public void test1() {
        _937Test.logs = new String[]{ "a1 9 2 3 1", "g1 act car", "zo4 4 7", "ab1 off key dog", "a8 act zoo" };
        _937Test.expected = new String[]{ "g1 act car", "a8 act zoo", "ab1 off key dog", "a1 9 2 3 1", "zo4 4 7" };
        Assert.assertArrayEquals(_937Test.expected, _937Test.solution1.reorderLogFiles(_937Test.logs));
    }

    @Test
    public void test2() {
        _937Test.logs = new String[]{ "t kvr", "r 3 1", "i 403", "7 so", "t 54" };
        _937Test.expected = new String[]{ "t kvr", "7 so", "r 3 1", "i 403", "t 54" };
        Assert.assertArrayEquals(_937Test.expected, _937Test.solution1.reorderLogFiles(_937Test.logs));
    }
}

