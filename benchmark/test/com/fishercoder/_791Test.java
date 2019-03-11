package com.fishercoder;


import _791.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _791Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals("cbad", _791Test.solution1.customSortString("cba", "abcd"));
    }
}

