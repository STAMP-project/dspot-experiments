package com.fishercoder;


import _220.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _220Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _220Test.nums = new int[]{ -1, -1 };
        Assert.assertEquals(true, _220Test.solution1.containsNearbyAlmostDuplicate(_220Test.nums, 1, 0));
    }

    @Test
    public void test2() {
        _220Test.nums = new int[]{ 1, 2 };
        Assert.assertEquals(false, _220Test.solution1.containsNearbyAlmostDuplicate(_220Test.nums, 0, 1));
    }

    @Test
    public void test3() {
        _220Test.nums = new int[]{ 4, 2 };
        Assert.assertEquals(false, _220Test.solution1.containsNearbyAlmostDuplicate(_220Test.nums, 2, 1));
    }

    @Test
    public void test4() {
        _220Test.nums = new int[]{ 2, 2 };
        Assert.assertEquals(true, _220Test.solution1.containsNearbyAlmostDuplicate(_220Test.nums, 3, 0));
    }

    @Test
    public void test5() {
        _220Test.nums = new int[]{ 1 };
        Assert.assertEquals(false, _220Test.solution1.containsNearbyAlmostDuplicate(_220Test.nums, 1, 1));
    }

    @Test
    public void test6() {
        _220Test.nums = new int[]{ 2147483647, -2147483647 };
        Assert.assertEquals(false, _220Test.solution1.containsNearbyAlmostDuplicate(_220Test.nums, 1, 2147483647));
    }
}

