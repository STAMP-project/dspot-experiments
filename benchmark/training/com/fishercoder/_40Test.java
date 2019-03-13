package com.fishercoder;


import _40.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _40Test {
    private static Solution1 solution1;

    private static int[] candidates;

    private static List<List<Integer>> expected;

    @Test
    public void test1() {
        _40Test.candidates = new int[]{ 10, 1, 2, 7, 6, 1, 5 };
        _40Test.expected = new ArrayList<>();
        _40Test.expected.add(Arrays.asList(1, 1, 6));
        _40Test.expected.add(Arrays.asList(1, 2, 5));
        _40Test.expected.add(Arrays.asList(1, 7));
        _40Test.expected.add(Arrays.asList(2, 6));
        Assert.assertEquals(_40Test.expected, _40Test.solution1.combinationSum2(_40Test.candidates, 8));
    }
}

