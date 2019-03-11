package com.fishercoder;


import _26.Solution1;
import _26.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _26Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] nums;

    @Test
    public void test1() {
        _26Test.nums = new int[]{ 1, 1, 2 };
        Assert.assertEquals(2, _26Test.solution1.removeDuplicates(_26Test.nums));
    }

    @Test
    public void test2() {
        _26Test.nums = new int[]{ 1, 1, 2 };
        Assert.assertEquals(2, _26Test.solution2.removeDuplicates(_26Test.nums));
    }

    @Test
    public void test3() {
        _26Test.nums = new int[]{ 1, 1, 2, 2, 3 };
        Assert.assertEquals(3, _26Test.solution1.removeDuplicates(_26Test.nums));
    }

    @Test
    public void test4() {
        _26Test.nums = new int[]{ 1, 1, 2, 2, 3 };
        Assert.assertEquals(3, _26Test.solution2.removeDuplicates(_26Test.nums));
    }

    @Test
    public void test5() {
        _26Test.nums = new int[]{ 1, 1 };
        Assert.assertEquals(1, _26Test.solution1.removeDuplicates(_26Test.nums));
    }

    @Test
    public void test6() {
        _26Test.nums = new int[]{ 1, 1 };
        Assert.assertEquals(1, _26Test.solution2.removeDuplicates(_26Test.nums));
    }
}

