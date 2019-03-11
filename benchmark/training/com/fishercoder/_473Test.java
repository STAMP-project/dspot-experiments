package com.fishercoder;


import com.fishercoder.solutions._473;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 5/30/17.
 */
public class _473Test {
    private static _473 test;

    private static int[] nums;

    @Test
    public void test1() {
        _473Test.nums = new int[]{ 1, 1, 2, 2, 2 };
        Assert.assertEquals(true, _473Test.test.makesquare(_473Test.nums));
    }
}

