package com.fishercoder;


import _330.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _330Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _330Test.nums = new int[]{ 1, 2, 4, 13, 43 };
        List<Integer> expected = new ArrayList(Arrays.asList(8, 29));
        Assert.assertEquals(expected, _330Test.solution1.findPatches(_330Test.nums, 100));
        Assert.assertEquals(2, _330Test.solution1.minPatches(_330Test.nums, 100));
    }
}

