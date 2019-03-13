package com.fishercoder;


import _347.Solution1;
import _347.Solution2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _347Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] nums;

    private static List<Integer> expected;

    @Test
    public void test1() {
        _347Test.nums = new int[]{ 3, 0, 1, 0 };
        _347Test.expected = new ArrayList<>(Arrays.asList(0, 3));
        /**
         * Comment out until Leetcode addresses this test case:
         * https://discuss.leetcode.com/topic/44237/java-o-n-solution-bucket-sort/75
         * Then I'll update this Solution1 code accordingly.
         *
         * My post is still un-addressed. - 3/12/2018
         */
        // assertEquals(expected, solution1.topKFrequent(nums, 2));
    }

    @Test
    public void test2() {
        _347Test.nums = new int[]{ 3, 0, 1, 0 };
        _347Test.expected = new ArrayList<>(Arrays.asList(0, 3));
        Assert.assertEquals(_347Test.expected, _347Test.solution2.topKFrequent(_347Test.nums, 2));
    }

    @Test
    public void test3() {
        _347Test.nums = new int[]{ 1, 1, 1, 2, 2, 3 };
        _347Test.expected = new ArrayList<>(Arrays.asList(1, 2));
        Assert.assertEquals(_347Test.expected, _347Test.solution1.topKFrequent(_347Test.nums, 2));
    }
}

