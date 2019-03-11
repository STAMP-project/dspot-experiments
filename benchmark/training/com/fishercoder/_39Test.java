package com.fishercoder;


import _39.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _39Test {
    private static Solution1 solution1;

    private static int[] candidates;

    private static List<List<Integer>> expected;

    @Test
    public void test1() {
        _39Test.candidates = new int[]{ 2, 3, 6, 7 };
        _39Test.expected = new ArrayList<>();
        _39Test.expected.add(Arrays.asList(2, 2, 3));
        _39Test.expected.add(Arrays.asList(7));
        Assert.assertEquals(_39Test.expected, _39Test.solution1.combinationSum(_39Test.candidates, 7));
    }
}

