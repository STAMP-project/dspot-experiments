package com.fishercoder;


import _162.Solution1;
import _162.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _162Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] nums;

    @Test
    public void test1() {
        _162Test.nums = new int[]{ 1, 2 };
        Assert.assertEquals(1, _162Test.solution1.findPeakElement(_162Test.nums));
        Assert.assertEquals(1, _162Test.solution2.findPeakElement(_162Test.nums));
    }

    @Test
    public void test2() {
        _162Test.nums = new int[]{ 1 };
        Assert.assertEquals(0, _162Test.solution1.findPeakElement(_162Test.nums));
        Assert.assertEquals(0, _162Test.solution2.findPeakElement(_162Test.nums));
    }

    @Test
    public void test3() {
        _162Test.nums = new int[]{ 1, 2, 3, 1 };
        Assert.assertEquals(2, _162Test.solution1.findPeakElement(_162Test.nums));
        Assert.assertEquals(2, _162Test.solution2.findPeakElement(_162Test.nums));
    }
}

