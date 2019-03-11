package com.fishercoder;


import _161.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _161Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(true, _161Test.solution1.isOneEditDistance("a", "ac"));
    }
}

