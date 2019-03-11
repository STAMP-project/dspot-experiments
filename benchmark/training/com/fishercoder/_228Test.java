package com.fishercoder;


import _228.Solution1;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _228Test {
    private static Solution1 solution1;

    private static List<String> expected;

    private static int[] nums;

    @Test
    public void test1() {
        _228Test.nums = new int[]{ 0, 1, 2, 4, 5, 7 };
        _228Test.expected = Arrays.asList("0->2", "4->5", "7");
        Assert.assertEquals(_228Test.expected, _228Test.solution1.summaryRanges(_228Test.nums));
    }
}

