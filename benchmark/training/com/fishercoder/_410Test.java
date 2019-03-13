package com.fishercoder;


import _410.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _410Test {
    private static Solution1 test;

    private static int[] nums;

    @Test
    public void test1() {
        _410Test.nums = new int[]{ 7, 2, 5, 10, 8 };
        Assert.assertEquals(18, _410Test.test.splitArray(_410Test.nums, 2));
    }
}

