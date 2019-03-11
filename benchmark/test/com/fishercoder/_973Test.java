package com.fishercoder;


import _973.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _973Test {
    private static Solution1 test;

    @Test
    public void test1() {
        Assert.assertArrayEquals(new int[][]{ new int[]{ -2, 2 } }, _973Test.test.kClosest(new int[][]{ new int[]{ 1, 3 }, new int[]{ -2, 2 } }, 1));
    }

    @Test
    public void test2() {
        Assert.assertArrayEquals(new int[][]{ new int[]{ 3, 3 }, new int[]{ -2, 4 } }, _973Test.test.kClosest(new int[][]{ new int[]{ 3, 3 }, new int[]{ 5, -1 }, new int[]{ -2, 4 } }, 2));
    }
}

