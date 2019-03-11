package com.fishercoder;


import _368.Solution1;
import java.util.Arrays;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class _368Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _368Test.nums = new int[]{ 1, 2, 4, 8 };
        Assert.assertThat(_368Test.solution1.largestDivisibleSubset(_368Test.nums), Is.is(Arrays.asList(8, 4, 2, 1)));
    }

    @Test
    public void test2() {
        _368Test.nums = new int[]{ 1, 2, 3 };
        Assert.assertThat(_368Test.solution1.largestDivisibleSubset(_368Test.nums), Is.is(Arrays.asList(2, 1)));
    }

    @Test
    public void test3() {
        _368Test.nums = new int[]{ 1 };
        Assert.assertThat(_368Test.solution1.largestDivisibleSubset(_368Test.nums), Is.is(Arrays.asList(1)));
    }

    @Test
    public void test4() {
        _368Test.nums = new int[]{ 546, 669 };
        Assert.assertThat(_368Test.solution1.largestDivisibleSubset(_368Test.nums), Is.is(Arrays.asList(546)));
    }

    @Test
    public void test5() {
        _368Test.nums = new int[]{  };
        Assert.assertThat(_368Test.solution1.largestDivisibleSubset(_368Test.nums), Is.is(Arrays.asList()));
    }

    @Test
    public void test6() {
        _368Test.nums = new int[]{ 4, 8, 10, 240 };
        Assert.assertThat(_368Test.solution1.largestDivisibleSubset(_368Test.nums), Is.is(Arrays.asList(240, 8, 4)));
    }
}

