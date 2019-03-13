package com.fishercoder;


import _697.Solution1;
import _697.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _697Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] nums;

    @Test
    public void test1() {
        _697Test.nums = new int[]{ 1 };
        Assert.assertEquals(1, _697Test.solution1.findShortestSubArray(_697Test.nums));
        Assert.assertEquals(1, _697Test.solution2.findShortestSubArray(_697Test.nums));
    }

    @Test
    public void test2() {
        _697Test.nums = new int[]{ 1, 2, 2, 3, 1 };
        Assert.assertEquals(2, _697Test.solution1.findShortestSubArray(_697Test.nums));
        Assert.assertEquals(2, _697Test.solution2.findShortestSubArray(_697Test.nums));
    }

    @Test
    public void test3() {
        _697Test.nums = new int[]{ 1, 2, 2, 3, 1, 1 };
        Assert.assertEquals(6, _697Test.solution1.findShortestSubArray(_697Test.nums));
        Assert.assertEquals(6, _697Test.solution2.findShortestSubArray(_697Test.nums));
    }

    @Test
    public void test4() {
        _697Test.nums = new int[]{ 1, 2, 2, 3, 1, 1, 5 };
        Assert.assertEquals(6, _697Test.solution1.findShortestSubArray(_697Test.nums));
        Assert.assertEquals(6, _697Test.solution2.findShortestSubArray(_697Test.nums));
    }

    @Test
    public void test5() {
        _697Test.nums = new int[]{ 1, 2, 2, 3, 1, 4, 2 };
        Assert.assertEquals(6, _697Test.solution1.findShortestSubArray(_697Test.nums));
        Assert.assertEquals(6, _697Test.solution2.findShortestSubArray(_697Test.nums));
    }
}

