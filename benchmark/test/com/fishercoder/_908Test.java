package com.fishercoder;


import _908.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _908Test {
    private static Solution1 solution1;

    private static int[] A;

    @Test
    public void test1() {
        _908Test.A = new int[]{ 1 };
        Assert.assertEquals(0, _908Test.solution1.smallestRangeI(_908Test.A, 0));
    }

    @Test
    public void test2() {
        _908Test.A = new int[]{ 0, 10 };
        Assert.assertEquals(6, _908Test.solution1.smallestRangeI(_908Test.A, 2));
    }

    @Test
    public void test3() {
        _908Test.A = new int[]{ 1, 3, 6 };
        Assert.assertEquals(0, _908Test.solution1.smallestRangeI(_908Test.A, 3));
    }
}

