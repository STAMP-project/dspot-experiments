package com.fishercoder;


import com.fishercoder.solutions._410;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/6/17.
 */
public class _410Test {
    private static _410 test;

    private static int[] nums;

    @Test
    public void test1() {
        _410Test.nums = new int[]{ 7, 2, 5, 10, 8 };
        Assert.assertEquals(18, _410Test.test.splitArray(_410Test.nums, 2));
    }
}

