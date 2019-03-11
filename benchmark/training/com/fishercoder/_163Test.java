package com.fishercoder;


import _163.Solution1;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _163Test {
    private static Solution1 solution1;

    private static List<String> expected;

    private static List<String> actual;

    private static int[] nums;

    @Test
    public void test1() {
        // solution1 case 1: should return ["0->2147483646"]
        _163Test.nums = new int[]{ 2147483647 };
        _163Test.expected.add("0->2147483646");
        _163Test.actual = _163Test.solution1.findMissingRanges(_163Test.nums, 0, 2147483647);
        Assert.assertEquals(_163Test.expected, _163Test.actual);
    }

    @Test
    public void test2() {
        // solution1 case 2: should return ["-2147483647->-1","1->2147483646"]
        _163Test.nums = new int[]{ -2147483648, -2147483648, 0, 2147483647, 2147483647 };
        _163Test.expected.add("-2147483647->-1");
        _163Test.expected.add("1->2147483646");
        _163Test.actual = _163Test.solution1.findMissingRanges(_163Test.nums, -2147483648, 2147483647);
        Assert.assertEquals(_163Test.expected, _163Test.actual);
    }

    @Test
    public void test3() {
        // solution1 case 3: should return ["-2147483648->2147483647"]
        _163Test.nums = new int[]{  };
        _163Test.expected.add("-2147483648->2147483647");
        _163Test.actual = _163Test.solution1.findMissingRanges(_163Test.nums, -2147483648, 2147483647);
        Assert.assertEquals(_163Test.expected, _163Test.actual);
    }

    @Test
    public void test4() {
        // solution1 case 4: should return ["-2147483648->2147483646"]
        _163Test.nums = new int[]{ 2147483647 };
        _163Test.expected.add("-2147483648->2147483646");
        _163Test.actual = _163Test.solution1.findMissingRanges(_163Test.nums, -2147483648, 2147483647);
        Assert.assertEquals(_163Test.expected, _163Test.actual);
    }

    @Test
    public void test5() {
        // solution1 case 5: should return ["0->2147483647"]
        _163Test.nums = new int[]{  };
        _163Test.expected.add("0->2147483647");
        _163Test.actual = _163Test.solution1.findMissingRanges(_163Test.nums, 0, 2147483647);
        Assert.assertEquals(_163Test.expected, _163Test.actual);
    }

    @Test
    public void test6() {
        // solution1 case 6: should return ["-2147483647->2147483647"]
        _163Test.nums = new int[]{ -2147483648 };
        _163Test.expected.add("-2147483647->2147483647");
        _163Test.actual = _163Test.solution1.findMissingRanges(_163Test.nums, -2147483648, 2147483647);
        Assert.assertEquals(_163Test.expected, _163Test.actual);
    }
}

