package com.fishercoder;


import com.fishercoder.solutions._553;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/25/17.
 */
public class _553Test {
    private static _553 test;

    private static int[] nums;

    @Test
    public void test1() {
        _553Test.nums = new int[]{ 1000, 100, 10, 2 };
        Assert.assertEquals("1000/(100/10/2)", _553Test.test.optimalDivision(_553Test.nums));
    }

    @Test
    public void test2() {
        _553Test.nums = new int[]{ 1000, 100, 40, 10, 2 };
        Assert.assertEquals("1000/(100/40/10/2)", _553Test.test.optimalDivision(_553Test.nums));
    }
}

