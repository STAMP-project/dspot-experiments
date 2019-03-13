package com.fishercoder;


import _154.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _154Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _154Test.nums = new int[]{ 1, 1, 1 };
        Assert.assertEquals(1, _154Test.solution1.findMin(_154Test.nums));
    }
}

