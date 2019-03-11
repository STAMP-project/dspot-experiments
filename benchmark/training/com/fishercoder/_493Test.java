package com.fishercoder;


import _493.Solution1;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/10/17.
 */
public class _493Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _493Test.nums = new int[]{ 1, 3, 2, 3, 1 };
        Assert.assertEquals(2, _493Test.solution1.reversePairs(_493Test.nums));
    }

    @Test
    public void test2() {
        _493Test.nums = new int[]{ 2, 4, 3, 5, 1 };
        Assert.assertEquals(3, _493Test.solution1.reversePairs(_493Test.nums));
    }

    @Test
    public void test3() {
        _493Test.nums = new int[]{ 2147483647, 2147483647, 2147483647, 2147483647, 2147483647, 2147483647 };
        Assert.assertEquals(0, _493Test.solution1.reversePairs(_493Test.nums));
    }

    @Test
    public void test4() {
        _493Test.nums = new int[]{ 1, 2147483647, 2147483647, 2147483647, 2147483647, 2147483647 };
        Assert.assertEquals(0, _493Test.solution1.reversePairs(_493Test.nums));
    }

    @Test
    public void test5() {
        _493Test.nums = new int[]{ 2147483647, 2147483646, 2147483647, 2147483647, 2147483647 };
        Assert.assertEquals(0, _493Test.solution1.reversePairs(_493Test.nums));
    }
}

