package com.fishercoder;


import _76.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _76Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        Assert.assertEquals("BANC", _76Test.solution1.minWindow("ADOBECODEBANC", "ABC"));
    }
}

