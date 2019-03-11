package com.fishercoder;


import com.fishercoder.solutions._646;
import org.junit.Assert;
import org.junit.Test;


public class _646Test {
    private static _646 test;

    private static int[][] pairs;

    @Test
    public void test1() {
        _646Test.pairs = new int[][]{ new int[]{ 1, 2 }, new int[]{ 2, 3 }, new int[]{ 5, 6 }, new int[]{ 3, 4 } };
        Assert.assertEquals(3, _646Test.test.findLongestChain(_646Test.pairs));
    }

    @Test
    public void test2() {
        _646Test.pairs = new int[][]{ new int[]{ 9, 10 }, new int[]{ -9, 9 }, new int[]{ -6, 1 }, new int[]{ -4, 1 }, new int[]{ 8, 10 }, new int[]{ 7, 10 }, new int[]{ 9, 10 }, new int[]{ 2, 10 } };
        Assert.assertEquals(2, _646Test.test.findLongestChain(_646Test.pairs));
    }
}

