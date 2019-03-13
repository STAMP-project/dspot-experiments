package com.fishercoder;


import _859.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _859Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        TestCase.assertEquals(true, _859Test.solution1.buddyStrings("ab", "ba"));
    }

    @Test
    public void test2() {
        TestCase.assertEquals(false, _859Test.solution1.buddyStrings("ab", "ab"));
    }

    @Test
    public void test3() {
        TestCase.assertEquals(true, _859Test.solution1.buddyStrings("aa", "aa"));
    }

    @Test
    public void test4() {
        TestCase.assertEquals(true, _859Test.solution1.buddyStrings("aaaaaaabc", "aaaaaaacb"));
    }

    @Test
    public void test5() {
        TestCase.assertEquals(false, _859Test.solution1.buddyStrings("", "aa"));
    }

    @Test
    public void test6() {
        TestCase.assertEquals(true, _859Test.solution1.buddyStrings("aaa", "aaa"));
    }
}

