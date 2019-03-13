package com.fishercoder;


import _950.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _950Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertArrayEquals(new int[]{ 2, 13, 3, 11, 5, 17, 7 }, _950Test.solution1.deckRevealedIncreasing(new int[]{ 17, 13, 11, 2, 3, 5, 7 }));
    }
}

