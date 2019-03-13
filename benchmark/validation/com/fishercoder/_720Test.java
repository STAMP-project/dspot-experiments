package com.fishercoder;


import _720.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _720Test {
    private static Solution1 solution1;

    private static String[] words;

    @Test
    public void test1() {
        _720Test.words = new String[]{ "w", "wo", "wor", "worl", "world" };
        TestCase.assertEquals("world", _720Test.solution1.longestWord(_720Test.words));
    }

    @Test
    public void test2() {
        _720Test.words = new String[]{ "a", "banana", "app", "appl", "ap", "apply", "apple" };
        TestCase.assertEquals("apple", _720Test.solution1.longestWord(_720Test.words));
    }
}

