package com.fishercoder;


import com.fishercoder.solutions._645;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 7/23/17.
 */
public class _645Test {
    private static _645 test;

    private static int[] nums;

    @Test
    public void test1() {
        _645Test.nums = new int[]{ 1, 2, 2, 4 };
        Assert.assertArrayEquals(new int[]{ 2, 3 }, _645Test.test.findErrorNums(_645Test.nums));
    }
}

