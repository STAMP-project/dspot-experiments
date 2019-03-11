package com.fishercoder;


import com.fishercoder.solutions._594;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/20/17.
 */
public class _594Test {
    private static _594 test;

    private static int[] nums;

    @Test
    public void test1() {
        _594Test.nums = new int[]{ 1, 3, 2, 2, 5, 2, 3, 7 };
        Assert.assertEquals(5, _594Test.test.findLHS(_594Test.nums));
    }
}

