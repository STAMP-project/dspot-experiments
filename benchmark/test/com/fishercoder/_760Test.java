package com.fishercoder;


import _760.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _760Test {
    private static Solution1 solution1;

    private static int[] A;

    private static int[] B;

    private static int[] expected;

    @Test
    public void test1() {
        _760Test.A = new int[]{ 12, 28, 46, 32, 50 };
        _760Test.B = new int[]{ 50, 12, 32, 46, 28 };
        _760Test.expected = new int[]{ 1, 4, 3, 2, 0 };
        Assert.assertArrayEquals(_760Test.expected, _760Test.solution1.anagramMappings(_760Test.A, _760Test.B));
    }
}

