package com.fishercoder;


import _680.Solution1;
import _680.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _680Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        Assert.assertEquals(true, _680Test.solution1.validPalindrome("aba"));
        Assert.assertEquals(true, _680Test.solution2.validPalindrome("aba"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(true, _680Test.solution1.validPalindrome("abcca"));
        Assert.assertEquals(true, _680Test.solution2.validPalindrome("abcca"));
    }

    @Test
    public void test3() {
        Assert.assertEquals(true, _680Test.solution1.validPalindrome("acbca"));
        Assert.assertEquals(true, _680Test.solution2.validPalindrome("acbca"));
    }

    @Test
    public void test4() {
        Assert.assertEquals(false, _680Test.solution1.validPalindrome("accbba"));
        Assert.assertEquals(false, _680Test.solution2.validPalindrome("accbba"));
    }

    @Test
    public void test5() {
        Assert.assertEquals(true, _680Test.solution1.validPalindrome("abdeeda"));
        Assert.assertEquals(true, _680Test.solution2.validPalindrome("abdeeda"));
    }

    @Test
    public void test6() {
        Assert.assertEquals(true, _680Test.solution1.validPalindrome("cbbcc"));
        Assert.assertEquals(true, _680Test.solution2.validPalindrome("cbbcc"));
    }

    @Test
    public void test7() {
        Assert.assertEquals(false, _680Test.solution1.validPalindrome("abc"));
        Assert.assertEquals(false, _680Test.solution2.validPalindrome("abc"));
    }
}

