package com.fishercoder;


import _406.Solution1;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Test;


public class _406Test {
    private static Solution1 solution1;

    private static int[][] people;

    private static int[][] actual;

    @Test
    public void test1() {
        _406Test.people = new int[][]{ new int[]{ 7, 0 }, new int[]{ 4, 4 }, new int[]{ 7, 1 }, new int[]{ 5, 0 }, new int[]{ 6, 1 }, new int[]{ 5, 2 } };
        _406Test.actual = _406Test.solution1.reconstructQueue(_406Test.people);
        CommonUtils.printArrayArray(_406Test.actual);
    }
}

