package com.fishercoder;


import _215.Solution2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/9/17.
 */
public class _215Test {
    private static Solution2 solution2;

    private static int k;

    private static int[] nums;

    private static int actual;

    private static int expected;

    @Test
    public void test1() {
        _215Test.k = 2;
        _215Test.nums = new int[]{ 3, 2, 1, 5, 6, 4 };
        _215Test.actual = _215Test.solution2.findKthLargest(_215Test.nums, _215Test.k);
        _215Test.expected = 5;
        Assert.assertEquals(_215Test.expected, _215Test.actual);
    }
}

