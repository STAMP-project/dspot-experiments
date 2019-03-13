package com.fishercoder;


import _15.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _15Test {
    private static Solution1 solution1;

    private static int[] nums;

    private static List<List<Integer>> expected;

    @Test
    public void test1() {
        _15Test.nums = new int[]{ -1, 0, 1, 2, -1, -4 };
        _15Test.expected = new ArrayList<>();
        _15Test.expected.add(Arrays.asList((-1), (-1), 2));
        _15Test.expected.add(Arrays.asList((-1), 0, 1));
        Assert.assertEquals(_15Test.expected, _15Test.solution1.threeSum(_15Test.nums));
    }
}

