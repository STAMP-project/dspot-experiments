package com.fishercoder;


import _806.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _806Test {
    private static Solution1 solution1;

    private static int[] widths;

    @Test
    public void test1() {
        _806Test.widths = new int[]{ 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
        Assert.assertArrayEquals(new int[]{ 3, 60 }, _806Test.solution1.numberOfLines(_806Test.widths, "abcdefghijklmnopqrstuvwxyz"));
    }

    @Test
    public void test2() {
        _806Test.widths = new int[]{ 4, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
        Assert.assertArrayEquals(new int[]{ 2, 4 }, _806Test.solution1.numberOfLines(_806Test.widths, "bbbcccdddaaa"));
    }
}

