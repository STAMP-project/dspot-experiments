package com.fishercoder;


import com.fishercoder.solutions._560;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 4/29/17.
 */
public class _560Test {
    private static _560 test;

    private static int expected;

    private static int actual;

    private static int[] nums;

    private static int k;

    @Test
    public void test1() {
        _560Test.nums = new int[]{ 1, 1, 1 };
        _560Test.k = 2;
        _560Test.expected = 2;
        _560Test.actual = _560Test.test.subarraySum(_560Test.nums, _560Test.k);
        Assert.assertEquals(_560Test.expected, _560Test.actual);
    }
}

