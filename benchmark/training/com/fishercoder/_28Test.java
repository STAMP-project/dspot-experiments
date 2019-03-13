package com.fishercoder;


import _28.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _28Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        TestCase.assertEquals(0, _28Test.solution1.strStr("a", ""));
    }

    @Test
    public void test2() {
        TestCase.assertEquals((-1), _28Test.solution1.strStr("mississippi", "a"));
    }

    @Test
    public void test3() {
        TestCase.assertEquals(0, _28Test.solution1.strStr("a", "a"));
    }
}

