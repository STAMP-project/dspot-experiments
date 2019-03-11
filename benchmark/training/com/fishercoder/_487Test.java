package com.fishercoder;


import com.fishercoder.solutions._487;
import junit.framework.Assert;
import org.junit.Test;


public class _487Test {
    private static _487 test;

    private static int[] nums;

    private static int expected;

    private static int actual;

    @Test
    public void test1() {
        _487Test.nums = new int[]{ 1, 1, 0, 1, 1, 1 };
        _487Test.expected = 3;
        _487Test.actual = _487Test.test.findMaxConsecutiveOnes(_487Test.nums);
        Assert.assertEquals(_487Test.expected, _487Test.actual);
    }

    @Test
    public void test2() {
        _487Test.nums = new int[]{ 1, 1, 1, 1, 1, 1 };
        _487Test.expected = 6;
        _487Test.actual = _487Test.test.findMaxConsecutiveOnes(_487Test.nums);
        Assert.assertEquals(_487Test.expected, _487Test.actual);
    }

    @Test
    public void test3() {
        _487Test.nums = new int[]{  };
        _487Test.expected = 0;
        _487Test.actual = _487Test.test.findMaxConsecutiveOnes(_487Test.nums);
        Assert.assertEquals(_487Test.expected, _487Test.actual);
    }
}

