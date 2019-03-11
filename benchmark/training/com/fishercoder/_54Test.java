package com.fishercoder;


import _54.Solution1;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _54Test {
    private static Solution1 solution1;

    private static int[][] matrix;

    @Test
    public void test1() {
        _54Test.matrix = new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6 }, new int[]{ 7, 8, 9 } };
        Assert.assertEquals(Arrays.asList(1, 2, 3, 6, 9, 8, 7, 4, 5), _54Test.solution1.spiralOrder(_54Test.matrix));
    }
}

