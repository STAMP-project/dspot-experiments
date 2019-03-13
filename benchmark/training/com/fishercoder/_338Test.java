package com.fishercoder;


import _338.Solution1;
import com.fishercoder.common.utils.CommonUtils;
import org.junit.Assert;
import org.junit.Test;


public class _338Test {
    private static Solution1 test;

    private static int[] expected;

    private static int[] actual;

    @Test
    public void test1() {
        _338Test.expected = new int[]{ 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4 };
        _338Test.actual = _338Test.test.countBits(15);
        CommonUtils.printArray(_338Test.actual);
        Assert.assertArrayEquals(_338Test.expected, _338Test.actual);
    }
}

