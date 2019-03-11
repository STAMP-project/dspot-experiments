package com.fishercoder;


import _316.Solution1;
import _316.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _316Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        Assert.assertEquals("abc", _316Test.solution1.removeDuplicateLetters("bcabc"));
        Assert.assertEquals("abc", _316Test.solution2.removeDuplicateLetters("bcabc"));
    }

    @Test
    public void test2() {
        Assert.assertEquals("acdb", _316Test.solution1.removeDuplicateLetters("cbacdcbc"));
        Assert.assertEquals("acdb", _316Test.solution2.removeDuplicateLetters("cbacdcbc"));
    }
}

