package com.fishercoder;


import _229.Solution2;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _229Test {
    private static Solution2 solution2;

    private static int[] nums;

    @Test
    public void test1() {
        _229Test.nums = new int[]{ 1 };
        Assert.assertEquals(Arrays.asList(1), _229Test.solution2.majorityElement(_229Test.nums));
    }

    @Test
    public void test2() {
        _229Test.nums = new int[]{ 1, 2 };
        Assert.assertEquals(Arrays.asList(2, 1), _229Test.solution2.majorityElement(_229Test.nums));
    }

    @Test
    public void test3() {
        _229Test.nums = new int[]{ 2, 2 };
        Assert.assertEquals(Arrays.asList(2), _229Test.solution2.majorityElement(_229Test.nums));
    }

    @Test
    public void test4() {
        _229Test.nums = new int[]{ 1, 2, 3 };
        Assert.assertEquals(Arrays.asList(), _229Test.solution2.majorityElement(_229Test.nums));
    }

    @Test
    public void test5() {
        _229Test.nums = new int[]{ 3, 2, 3 };
        Assert.assertEquals(Arrays.asList(3), _229Test.solution2.majorityElement(_229Test.nums));
    }

    @Test
    public void test6() {
        _229Test.nums = new int[]{ 3, 3, 4 };
        Assert.assertEquals(Arrays.asList(3), _229Test.solution2.majorityElement(_229Test.nums));
    }

    @Test
    public void test7() {
        _229Test.nums = new int[]{ 2, 2, 1, 3 };
        Assert.assertEquals(Arrays.asList(2), _229Test.solution2.majorityElement(_229Test.nums));
    }
}

