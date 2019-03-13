package com.fishercoder;


import _48.Solution1;
import _48.Solution2;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Test;


public class _48Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[][] matrix;

    @Test
    public void test1() {
        _48Test.matrix = new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6 }, new int[]{ 7, 8, 9 } };
        _48Test.solution1.rotate(_48Test.matrix);
        CommonUtils.print2DIntArray(_48Test.matrix);
    }

    @Test
    public void test2() {
        _48Test.matrix = new int[][]{ new int[]{ 1, 2, 3 }, new int[]{ 4, 5, 6 }, new int[]{ 7, 8, 9 } };
        _48Test.solution2.rotate(_48Test.matrix);
        CommonUtils.print2DIntArray(_48Test.matrix);
    }
}

