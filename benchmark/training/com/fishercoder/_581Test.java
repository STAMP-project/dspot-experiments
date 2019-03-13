package com.fishercoder;


import com.fishercoder.solutions._581;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/17/17.
 */
public class _581Test {
    private static _581 test;

    private static int[] nums;

    @Test
    public void test1() {
        _581Test.nums = new int[]{ 1, 2, 3, 4 };
        Assert.assertEquals(0, _581Test.test.findUnsortedSubarray(_581Test.nums));
        Assert.assertEquals(0, _581Test.test.findUnsortedSubarray_sorting(_581Test.nums));
    }

    @Test
    public void test2() {
        _581Test.nums = new int[]{ 2, 6, 4, 8, 10, 9, 15 };
        Assert.assertEquals(5, _581Test.test.findUnsortedSubarray(_581Test.nums));
    }
}

