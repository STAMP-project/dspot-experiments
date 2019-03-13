package com.fishercoder;


import _44.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _44Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        TestCase.assertEquals(false, _44Test.solution1.isMatch("aa", "a"));
    }

    @Test
    public void test2() {
        TestCase.assertEquals(true, _44Test.solution1.isMatch("aa", "aa"));
    }

    @Test
    public void test3() {
        TestCase.assertEquals(false, _44Test.solution1.isMatch("aaa", "aa"));
    }

    @Test
    public void test4() {
        TestCase.assertEquals(true, _44Test.solution1.isMatch("aa", "*"));
    }

    @Test
    public void test5() {
        TestCase.assertEquals(true, _44Test.solution1.isMatch("aa", "a*"));
    }

    @Test
    public void test6() {
        TestCase.assertEquals(true, _44Test.solution1.isMatch("ab", "?*"));
    }

    @Test
    public void test7() {
        TestCase.assertEquals(false, _44Test.solution1.isMatch("aab", "c*a*b"));
    }
}

