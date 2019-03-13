package com.fishercoder;


import _747.Solution1;
import _747.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _747Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] nums;

    @Test
    public void test1() {
        _747Test.nums = new int[]{ 3, 6, 1, 0 };
        Assert.assertEquals(1, _747Test.solution1.dominantIndex(_747Test.nums));
    }

    @Test
    public void test2() {
        _747Test.nums = new int[]{ 3, 6, 1, 0 };
        Assert.assertEquals(1, _747Test.solution2.dominantIndex(_747Test.nums));
    }

    @Test
    public void test3() {
        _747Test.nums = new int[]{ 1, 2, 3, 4 };
        Assert.assertEquals((-1), _747Test.solution1.dominantIndex(_747Test.nums));
    }

    @Test
    public void test4() {
        _747Test.nums = new int[]{ 1, 2, 3, 4 };
        Assert.assertEquals((-1), _747Test.solution2.dominantIndex(_747Test.nums));
    }
}

