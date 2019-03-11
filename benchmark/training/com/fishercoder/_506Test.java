package com.fishercoder;


import com.fishercoder.solutions._506;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/15/17.
 */
public class _506Test {
    private static _506 test;

    private static String[] expected;

    private static String[] actual;

    private static int[] nums;

    @Test
    public void test1() {
        _506Test.nums = new int[]{ 2, 4, 1 };
        _506Test.expected = new String[]{ "Silver Medal", "Gold Medal", "Bronze Medal" };
        _506Test.actual = _506Test.test.findRelativeRanks(_506Test.nums);
        Assert.assertArrayEquals(_506Test.expected, _506Test.actual);
    }

    @Test
    public void test2() {
        _506Test.nums = new int[]{ 5, 4, 3, 2, 1 };
        _506Test.expected = new String[]{ "Gold Medal", "Silver Medal", "Bronze Medal", "4", "5" };
        _506Test.actual = _506Test.test.findRelativeRanks(_506Test.nums);
        Assert.assertArrayEquals(_506Test.expected, _506Test.actual);
    }
}

