package com.fishercoder;


import _87.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _87Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(true, _87Test.solution1.isScramble("great", "rgeat"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(true, _87Test.solution1.isScramble("great", "rgtae"));
    }

    @Test
    public void test3() {
        Assert.assertEquals(true, _87Test.solution1.isScramble("abc", "bca"));
    }
}

