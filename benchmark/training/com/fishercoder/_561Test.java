package com.fishercoder;


import com.fishercoder.solutions._561;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 4/23/17.
 */
public class _561Test {
    private static _561 test;

    private static int expected;

    private static int actual;

    private static int[] nums;

    @Test
    public void test1() {
        _561Test.nums = new int[]{ 1, 4, 3, 2 };
        _561Test.expected = 4;
        _561Test.actual = _561Test.test.arrayPairSum(_561Test.nums);
        Assert.assertEquals(_561Test.expected, _561Test.actual);
    }
}

