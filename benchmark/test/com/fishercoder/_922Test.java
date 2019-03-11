package com.fishercoder;


import _922.Solution1;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Test;


public class _922Test {
    private static Solution1 solution1;

    private static int[] A;

    private static int[] result;

    @Test
    public void test1() {
        _922Test.A = new int[]{ 4, 2, 5, 7 };
        _922Test.result = _922Test.solution1.sortArrayByParityII(_922Test.A);
        CommonUtils.printArray(_922Test.result);
    }

    @Test
    public void test2() {
        _922Test.A = new int[]{ 3, 1, 4, 2 };
        _922Test.result = _922Test.solution1.sortArrayByParityII(_922Test.A);
        CommonUtils.printArray(_922Test.result);
    }
}

