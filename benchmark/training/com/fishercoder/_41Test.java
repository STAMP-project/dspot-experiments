package com.fishercoder;


import _41.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _41Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _41Test.nums = new int[]{ 1, 2, 0 };
        Assert.assertEquals(3, _41Test.solution1.firstMissingPositive(_41Test.nums));
    }

    @Test
    public void test2() {
        _41Test.nums = new int[]{  };
        Assert.assertEquals(1, _41Test.solution1.firstMissingPositive(_41Test.nums));
    }

    @Test
    public void test3() {
        _41Test.nums = new int[]{ 3, 4, -1, 1 };
        Assert.assertEquals(2, _41Test.solution1.firstMissingPositive(_41Test.nums));
    }

    @Test
    public void test4() {
        _41Test.nums = new int[]{ 2 };
        Assert.assertEquals(1, _41Test.solution1.firstMissingPositive(_41Test.nums));
    }
}

