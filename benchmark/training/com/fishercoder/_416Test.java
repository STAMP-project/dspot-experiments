package com.fishercoder;


import com.fishercoder.solutions._416;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/5/17.
 */
public class _416Test {
    private static _416 test;

    private static int[] nums;

    @Test
    public void test1() {
        _416Test.nums = new int[]{ 1, 5, 11, 5 };
        Assert.assertEquals(true, _416Test.test.canPartition(_416Test.nums));
    }

    @Test
    public void test2() {
        _416Test.nums = new int[]{ 1, 2, 3, 5 };
        Assert.assertEquals(false, _416Test.test.canPartition(_416Test.nums));
    }

    @Test
    public void test3() {
        _416Test.nums = new int[]{ 1, 2, 3, 4, 5, 6, 7 };
        Assert.assertEquals(true, _416Test.test.canPartition(_416Test.nums));
    }
}

