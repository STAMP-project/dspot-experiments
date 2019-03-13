package com.fishercoder;


import com.fishercoder.solutions._498;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/26/17.
 */
public class _498Test {
    private static _498 test;

    private static int[][] matrix;

    private static int[] expected;

    @Test
    public void test1() {
        _498Test.matrix = new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6 }, new int[]{ 7, 8, 9 } };
        _498Test.expected = new int[]{ 1, 2, 4, 7, 5, 3, 6, 8, 9 };
        Assert.assertArrayEquals(_498Test.expected, _498Test.test.findDiagonalOrder(_498Test.matrix));
    }

    @Test
    public void test2() {
        _498Test.matrix = new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6 }, new int[]{ 7, 8, 9 }, new int[]{ 10, 11, 12 }, new int[]{ 13, 14, 15 } };
        _498Test.expected = new int[]{ 1, 2, 4, 7, 5, 3, 6, 8, 10, 13, 11, 9, 12, 14, 15 };
        Assert.assertArrayEquals(_498Test.expected, _498Test.test.findDiagonalOrder(_498Test.matrix));
    }
}

