package com.fishercoder;


import _457.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _457Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _457Test.nums = new int[]{ 2, -1, 1, 2, 2 };
        Assert.assertEquals(true, _457Test.solution1.circularArrayLoop(_457Test.nums));
    }

    @Test
    public void test2() {
        _457Test.nums = new int[]{ -1, 2 };
        Assert.assertEquals(false, _457Test.solution1.circularArrayLoop(_457Test.nums));
    }

    @Test
    public void test3() {
        _457Test.nums = new int[]{ -1, 2, 3 };
        Assert.assertEquals(false, _457Test.solution1.circularArrayLoop(_457Test.nums));
    }

    @Test
    public void test4() {
        _457Test.nums = new int[]{ 2, 1, 9 };
        Assert.assertEquals(false, _457Test.solution1.circularArrayLoop(_457Test.nums));
    }
}

