package com.fishercoder;


import _699.Solution1;
import java.util.Arrays;
import junit.framework.TestCase;
import org.junit.Test;


public class _699Test {
    private static Solution1 solution1;

    private static int[][] positions;

    @Test
    public void test1() {
        _699Test.positions = new int[][]{ new int[]{ 1, 2 }, new int[]{ 2, 3 }, new int[]{ 6, 1 } };
        TestCase.assertEquals(Arrays.asList(2, 5, 5), _699Test.solution1.fallingSquares(_699Test.positions));
    }
}

