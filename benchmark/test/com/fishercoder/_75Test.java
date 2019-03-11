package com.fishercoder;


import _75.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _75Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _75Test.nums = new int[]{ 2, 2, 1 };
        _75Test.solution1.sortColors(_75Test.nums);
        Assert.assertArrayEquals(new int[]{ 1, 2, 2 }, _75Test.nums);
    }

    @Test
    public void test2() {
        _75Test.nums = new int[]{ 0, 1, 2, 0, 2, 1 };
        _75Test.solution1.sortColors(_75Test.nums);
        Assert.assertArrayEquals(new int[]{ 0, 0, 1, 1, 2, 2 }, _75Test.nums);
    }

    @Test
    public void test3() {
        _75Test.nums = new int[]{ 0 };
        _75Test.solution1.sortColors(_75Test.nums);
        Assert.assertArrayEquals(new int[]{ 0 }, _75Test.nums);
    }

    @Test
    public void test4() {
        _75Test.nums = new int[]{ 1, 0 };
        _75Test.solution1.sortColors(_75Test.nums);
        Assert.assertArrayEquals(new int[]{ 0, 1 }, _75Test.nums);
    }

    @Test
    public void test5() {
        _75Test.nums = new int[]{ 2 };
        _75Test.solution1.sortColors(_75Test.nums);
        Assert.assertArrayEquals(new int[]{ 2 }, _75Test.nums);
    }
}

