package com.fishercoder;


import _221.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _221Test {
    private static Solution1 solution1;

    private static char[][] matrix;

    @Test
    public void test1() {
        _221Test.matrix = new char[][]{ new char[]{ '1', '0', '1', '0', '0' }, new char[]{ '1', '0', '1', '1', '1' }, new char[]{ '1', '1', '1', '1', '1' }, new char[]{ '1', '0', '0', '1', '0' } };
        Assert.assertEquals(4, _221Test.solution1.maximalSquare(_221Test.matrix));
    }
}

