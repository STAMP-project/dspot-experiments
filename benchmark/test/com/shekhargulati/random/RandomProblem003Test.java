package com.shekhargulati.random;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class RandomProblem003Test {
    @Test
    public void performMatrixMultiplicationOfTwoArrays() throws Exception {
        int[][] a = new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6 } };
        int[][] b = new int[][]{ new int[]{ 7, 8 }, new int[]{ 9, 10 }, new int[]{ 11, 12 } };
        int[][] result = new int[][]{ new int[]{ 58, 64 }, new int[]{ 139, 154 } };
        int[][] c = RandomProblem003.matrixMultiplication(a, b);
        Assert.assertThat(c, CoreMatchers.equalTo(result));
    }
}

