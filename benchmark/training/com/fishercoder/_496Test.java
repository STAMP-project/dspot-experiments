package com.fishercoder;


import com.fishercoder.solutions._496;
import org.junit.Assert;
import org.junit.Test;


public class _496Test {
    private static _496 test;

    private static int[] findNums;

    private static int[] nums;

    private static int[] expected;

    private static int[] actual;

    @Test
    public void test1() {
        _496Test.findNums = new int[]{ 4, 1, 2 };
        _496Test.nums = new int[]{ 1, 3, 4, 2 };
        _496Test.expected = new int[]{ -1, 3, -1 };
        _496Test.actual = _496Test.test.nextGreaterElement_naive_way(_496Test.findNums, _496Test.nums);
        Assert.assertArrayEquals(_496Test.expected, _496Test.actual);
        _496Test.actual = _496Test.test.nextGreaterElement_clever_way(_496Test.findNums, _496Test.nums);
        Assert.assertArrayEquals(_496Test.expected, _496Test.actual);
    }
}

