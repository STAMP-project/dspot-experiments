package com.fishercoder;


import com.fishercoder.solutions._503;
import org.junit.Assert;
import org.junit.Test;


public class _503Test {
    private static _503 test;

    private static int[] nums;

    private static int[] expected;

    private static int[] actual;

    @Test
    public void test1() {
        _503Test.nums = new int[]{ 1, 2, 1 };
        _503Test.expected = new int[]{ 2, -1, 2 };
        _503Test.actual = _503Test.test.nextGreaterElements(_503Test.nums);
        Assert.assertArrayEquals(_503Test.expected, _503Test.actual);
    }
}

