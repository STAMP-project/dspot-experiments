package com.fishercoder;


import _14.Solution1;
import _14.Solution2;
import _14.Solution3;
import org.junit.Assert;
import org.junit.Test;


public class _14Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static Solution3 solution3;

    private static String[] strs;

    @Test
    public void test1() {
        _14Test.strs = new String[]{ "a", "b" };
        Assert.assertEquals("", _14Test.solution1.longestCommonPrefix(_14Test.strs));
        Assert.assertEquals("", _14Test.solution2.longestCommonPrefix(_14Test.strs));
        Assert.assertEquals("", _14Test.solution3.longestCommonPrefix(_14Test.strs));
    }

    @Test
    public void test2() {
        _14Test.strs = new String[]{ "leetcode", "lead" };
        Assert.assertEquals("le", _14Test.solution1.longestCommonPrefix(_14Test.strs));
        Assert.assertEquals("le", _14Test.solution2.longestCommonPrefix(_14Test.strs));
        Assert.assertEquals("le", _14Test.solution3.longestCommonPrefix(_14Test.strs));
    }

    @Test
    public void test3() {
        _14Test.strs = new String[]{ "leetcode", "code" };
        Assert.assertEquals("", _14Test.solution1.longestCommonPrefix(_14Test.strs));
        Assert.assertEquals("", _14Test.solution2.longestCommonPrefix(_14Test.strs));
        Assert.assertEquals("", _14Test.solution3.longestCommonPrefix(_14Test.strs));
    }
}

