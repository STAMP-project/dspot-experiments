package com.fishercoder;


import _844.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _844Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(true, _844Test.solution1.backspaceCompare("ab#c", "ad#c"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(true, _844Test.solution1.backspaceCompare("ab##", "c#d#"));
    }

    @Test
    public void test3() {
        Assert.assertEquals(true, _844Test.solution1.backspaceCompare("a##c", "#a#c"));
    }

    @Test
    public void test4() {
        Assert.assertEquals(false, _844Test.solution1.backspaceCompare("a#c", "b"));
    }
}

