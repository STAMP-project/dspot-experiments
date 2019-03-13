package com.fishercoder;


import com.fishercoder.solutions._34;
import org.junit.Assert;
import org.junit.Test;


public class _34Test {
    private static _34 test;

    private static int[] nums;

    @Test
    public void test1() {
        _34Test.nums = new int[]{ 1, 2, 3 };
        Assert.assertArrayEquals(new int[]{ 1, 1 }, _34Test.test.searchRange(_34Test.nums, 2));
    }

    @Test
    public void test2() {
        _34Test.nums = new int[]{  };
        Assert.assertArrayEquals(new int[]{ -1, -1 }, _34Test.test.searchRange(_34Test.nums, 0));
    }
}

