package com.fishercoder;


import _821.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _821Test {
    private static Solution1 solution1;

    private static int[] expected;

    @Test
    public void test1() {
        _821Test.expected = new int[]{ 3, 2, 1, 0, 1, 0, 0, 1, 2, 2, 1, 0 };
        Assert.assertArrayEquals(_821Test.expected, _821Test.solution1.shortestToChar("loveleetcode", 'e'));
    }
}

