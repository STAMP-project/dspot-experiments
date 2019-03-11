package com.fishercoder;


import _540.Solution1;
import _540.Solution2;
import junit.framework.Assert;
import org.junit.Test;


public class _540Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] nums;

    @Test
    public void test1() {
        _540Test.nums = new int[]{ 1, 1, 2, 3, 3, 4, 4, 8, 8 };
        Assert.assertEquals(2, _540Test.solution1.singleNonDuplicate(_540Test.nums));
        Assert.assertEquals(2, _540Test.solution2.singleNonDuplicate(_540Test.nums));
    }

    @Test
    public void test2() {
        _540Test.nums = new int[]{ 3, 3, 7, 7, 10, 11, 11 };
        Assert.assertEquals(10, _540Test.solution1.singleNonDuplicate(_540Test.nums));
        Assert.assertEquals(10, _540Test.solution2.singleNonDuplicate(_540Test.nums));
    }

    @Test
    public void test3() {
        _540Test.nums = new int[]{ 1, 1, 2 };
        Assert.assertEquals(2, _540Test.solution1.singleNonDuplicate(_540Test.nums));
        Assert.assertEquals(2, _540Test.solution2.singleNonDuplicate(_540Test.nums));
    }
}

