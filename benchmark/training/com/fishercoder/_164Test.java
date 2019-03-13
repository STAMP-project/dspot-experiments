package com.fishercoder;


import _164.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _164Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _164Test.nums = new int[]{  };
        Assert.assertEquals(0, _164Test.solution1.maximumGap(_164Test.nums));
    }

    @Test
    public void test2() {
        _164Test.nums = new int[]{ 1, 3, 6, 5 };
        Assert.assertEquals(2, _164Test.solution1.maximumGap(_164Test.nums));
    }

    @Test
    public void test3() {
        _164Test.nums = new int[]{ 1, 100000 };
        Assert.assertEquals(99999, _164Test.solution1.maximumGap(_164Test.nums));
    }
}

