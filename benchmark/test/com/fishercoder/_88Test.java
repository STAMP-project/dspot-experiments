package com.fishercoder;


import _88.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _88Test {
    private static Solution1 solution1;

    private int[] nums1;

    private int[] nums2;

    private int[] expected;

    @Test
    public void test1() {
        nums1 = new int[]{ 2, 0 };
        nums2 = new int[]{ 1 };
        expected = new int[]{ 1, 2 };
        _88Test.solution1.merge(nums1, 1, nums2, 1);
        Assert.assertArrayEquals(expected, nums1);
    }

    @Test
    public void test2() {
        nums1 = new int[]{ 4, 5, 6, 0, 0, 0 };
        nums2 = new int[]{ 1, 2, 3 };
        expected = new int[]{ 1, 2, 3, 4, 5, 6 };
        _88Test.solution1.merge(nums1, 3, nums2, 3);
        Assert.assertArrayEquals(expected, nums1);
    }
}

