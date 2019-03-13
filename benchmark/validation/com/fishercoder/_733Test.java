package com.fishercoder;


import _733.Solution1;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Test;


public class _733Test {
    private static Solution1 solution1;

    private static int[][] image;

    private static int[][] result;

    @Test
    public void test1() {
        _733Test.image = new int[][]{ new int[]{ 1, 1, 1 }, new int[]{ 1, 1, 0 }, new int[]{ 1, 0, 1 } };
        _733Test.result = _733Test.solution1.floodFill(_733Test.image, 1, 1, 2);
        CommonUtils.print2DIntArray(_733Test.result);
    }

    @Test
    public void test2() {
        _733Test.image = new int[][]{ new int[]{ 0, 0, 0 }, new int[]{ 0, 0, 0 } };
        _733Test.result = _733Test.solution1.floodFill(_733Test.image, 0, 0, 2);
        CommonUtils.print2DIntArray(_733Test.result);
    }
}

