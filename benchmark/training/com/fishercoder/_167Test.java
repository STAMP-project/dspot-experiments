package com.fishercoder;


import _167.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _167Test {
    private static Solution1 solution1;

    private static int[] numbers;

    private static int[] expected;

    @Test
    public void test1() {
        _167Test.numbers = new int[]{ -3, 3, 4, 90 };
        _167Test.expected = new int[]{ 1, 2 };
        Assert.assertArrayEquals(_167Test.expected, _167Test.solution1.twoSum(_167Test.numbers, 0));
    }
}

