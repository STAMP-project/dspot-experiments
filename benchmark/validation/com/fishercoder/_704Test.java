package com.fishercoder;


import _704.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _704Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _704Test.nums = new int[]{ -1, 0, 3, 5, 9, 12 };
        Assert.assertEquals(4, _704Test.solution1.search(_704Test.nums, 9));
    }

    @Test
    public void test2() {
        _704Test.nums = new int[]{ -1, 0, 3, 5, 9, 12 };
        Assert.assertEquals((-1), _704Test.solution1.search(_704Test.nums, 2));
    }

    @Test
    public void test3() {
        _704Test.nums = new int[]{ 5 };
        Assert.assertEquals(0, _704Test.solution1.search(_704Test.nums, 5));
    }

    @Test
    public void test4() {
        _704Test.nums = new int[]{ -1, 0 };
        Assert.assertEquals(1, _704Test.solution1.search(_704Test.nums, 0));
    }

    @Test
    public void test5() {
        _704Test.nums = new int[]{ -1, 0, 3, 5, 9, 12 };
        Assert.assertEquals(1, _704Test.solution1.search(_704Test.nums, 0));
    }
}

