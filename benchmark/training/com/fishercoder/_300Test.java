package com.fishercoder;


import _300.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _300Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _300Test.nums = new int[]{ 10, 9, 2, 5, 3, 7, 101, 18 };
        Assert.assertEquals(4, _300Test.solution1.lengthOfLIS(_300Test.nums));
    }
}

