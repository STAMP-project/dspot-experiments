package com.fishercoder;


import _796.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _796Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(true, _796Test.solution1.rotateString("abcde", "cdeab"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(false, _796Test.solution1.rotateString("abcde", "abced"));
    }
}

