package com.fishercoder;


import com.fishercoder.solutions._525;
import junit.framework.Assert;
import org.junit.Test;


public class _525Test {
    private static _525 test;

    private static int expected;

    private static int actual;

    private static int[] nums;

    @Test
    public void test1() {
        _525Test.nums = new int[]{ 0, 1 };
        _525Test.expected = 2;
        _525Test.actual = _525Test.test.findMaxLength(_525Test.nums);
        Assert.assertEquals(_525Test.expected, _525Test.actual);
    }

    @Test
    public void test2() {
        _525Test.nums = new int[]{ 0, 1, 0 };
        _525Test.expected = 2;
        _525Test.actual = _525Test.test.findMaxLength(_525Test.nums);
        Assert.assertEquals(_525Test.expected, _525Test.actual);
    }

    @Test
    public void test3() {
        _525Test.nums = new int[]{ 0, 0, 1, 0, 0, 0, 1, 1 };
        _525Test.expected = 6;
        _525Test.actual = _525Test.test.findMaxLength(_525Test.nums);
        Assert.assertEquals(_525Test.expected, _525Test.actual);
    }
}

