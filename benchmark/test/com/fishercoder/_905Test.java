package com.fishercoder;


import _905.Solution1;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Test;


public class _905Test {
    private static Solution1 solution1;

    private static int[] A;

    private static int[] actual;

    @Test
    public void test1() {
        _905Test.A = new int[]{ 3, 1, 2, 4 };
        _905Test.actual = _905Test.solution1.sortArrayByParity(_905Test.A);
        CommonUtils.printArray(_905Test.actual);
    }

    @Test
    public void test2() {
        _905Test.A = new int[]{ 1, 3 };
        _905Test.actual = _905Test.solution1.sortArrayByParity(_905Test.A);
        CommonUtils.printArray(_905Test.actual);
    }
}

