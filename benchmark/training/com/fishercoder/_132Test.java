package com.fishercoder;


import _132.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _132Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(1, _132Test.solution1.minCut("aab"));
    }
}

