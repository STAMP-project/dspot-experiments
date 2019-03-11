package com.fishercoder;


import _269.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _269Test {
    private static Solution1 solution1;

    private static String[] words;

    @Test
    public void test1() {
        _269Test.words = new String[]{ "wrt", "wrf", "er", "ett", "rftt" };
        Assert.assertEquals("wertf", _269Test.solution1.alienOrder(_269Test.words));
    }
}

