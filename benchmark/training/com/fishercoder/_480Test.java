package com.fishercoder;


import com.fishercoder.solutions._480;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/27/17.
 */
public class _480Test {
    private static _480 test;

    private static int[] nums;

    private static double[] expected;

    private static int k;

    @Test
    public void test1() {
        _480Test.nums = new int[]{ 1, 3, -1, -3, 5, 3, 6, 7 };
        _480Test.expected = new double[]{ 1, -1, -1, 3, 5, 6 };
        _480Test.k = 3;
        Assert.assertArrayEquals(_480Test.expected, _480Test.test.medianSlidingWindow(_480Test.nums, _480Test.k), 0);
    }
}

