package com.fishercoder;


import com.fishercoder.solutions._523;
import junit.framework.Assert;
import org.junit.Test;


public class _523Test {
    private static _523 test;

    private static boolean expected;

    private static boolean actual;

    private static int[] nums;

    private static int k;

    @Test
    public void test1() {
        _523Test.nums = new int[]{ 23, 2, 4, 6, 7 };
        _523Test.expected = true;
        _523Test.k = 6;
        _523Test.actual = _523Test.test.checkSubarraySum(_523Test.nums, _523Test.k);
        Assert.assertEquals(_523Test.expected, _523Test.actual);
    }

    @Test
    public void test2() {
        _523Test.nums = new int[]{ 23, 2, 6, 4, 7 };
        _523Test.expected = true;
        _523Test.k = 6;
        _523Test.actual = _523Test.test.checkSubarraySum(_523Test.nums, _523Test.k);
        Assert.assertEquals(_523Test.expected, _523Test.actual);
    }

    @Test
    public void test3() {
        _523Test.nums = new int[]{ 23, 2, 6, 4, 7 };
        _523Test.expected = false;
        _523Test.k = 0;
        _523Test.actual = _523Test.test.checkSubarraySum(_523Test.nums, _523Test.k);
        Assert.assertEquals(_523Test.expected, _523Test.actual);
    }

    @Test
    public void test4() {
        _523Test.nums = new int[]{ 0, 1, 0 };
        _523Test.expected = false;
        _523Test.k = 0;
        _523Test.actual = _523Test.test.checkSubarraySum(_523Test.nums, _523Test.k);
        Assert.assertEquals(_523Test.expected, _523Test.actual);
    }

    @Test
    public void test5() {
        _523Test.nums = new int[]{ 0, 0 };
        _523Test.expected = true;
        _523Test.k = 0;
        _523Test.actual = _523Test.test.checkSubarraySum(_523Test.nums, _523Test.k);
        Assert.assertEquals(_523Test.expected, _523Test.actual);
    }

    @Test
    public void test6() {
        _523Test.nums = new int[]{ 1, 1 };
        _523Test.expected = true;
        _523Test.k = 2;
        _523Test.actual = _523Test.test.checkSubarraySum(_523Test.nums, _523Test.k);
        Assert.assertEquals(_523Test.expected, _523Test.actual);
    }

    @Test
    public void test7() {
        _523Test.nums = new int[]{ 0 };
        _523Test.expected = false;
        _523Test.k = -1;
        _523Test.actual = _523Test.test.checkSubarraySum(_523Test.nums, _523Test.k);
        Assert.assertEquals(_523Test.expected, _523Test.actual);
    }

    @Test
    public void test8() {
        _523Test.nums = new int[]{ 23, 2, 4, 6, 7 };
        _523Test.expected = true;
        _523Test.k = -6;
        _523Test.actual = _523Test.test.checkSubarraySum(_523Test.nums, _523Test.k);
        Assert.assertEquals(_523Test.expected, _523Test.actual);
    }

    @Test
    public void test9() {
        _523Test.nums = new int[]{ 1, 2, 3 };
        _523Test.expected = false;
        _523Test.k = 4;
        _523Test.actual = _523Test.test.checkSubarraySum(_523Test.nums, _523Test.k);
        Assert.assertEquals(_523Test.expected, _523Test.actual);
    }

    @Test
    public void test10() {
        _523Test.nums = new int[]{ 5, 2, 4 };
        _523Test.expected = false;
        _523Test.k = 5;
        _523Test.actual = _523Test.test.checkSubarraySum(_523Test.nums, _523Test.k);
        Assert.assertEquals(_523Test.expected, _523Test.actual);
    }
}

