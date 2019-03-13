package com.fishercoder;


import _719.Solution1;
import _719.Solution2;
import junit.framework.Assert;
import org.junit.Test;


public class _719Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] nums;

    @Test
    public void test1() {
        _719Test.nums = new int[]{ 1, 3, 1 };
        Assert.assertEquals(0, _719Test.solution1.smallestDistancePair(_719Test.nums, 1));
        Assert.assertEquals(0, _719Test.solution2.smallestDistancePair(_719Test.nums, 1));
    }
}

