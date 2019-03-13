package com.fishercoder;


import _393.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _393Test {
    private static Solution1 solution1;

    private static boolean expected;

    private static boolean actual;

    private static int[] data;

    @Test
    public void test2() {
        _393Test.data = new int[]{ 5 };
        _393Test.expected = true;
        _393Test.actual = _393Test.solution1.validUtf8(_393Test.data);
        Assert.assertEquals(_393Test.expected, _393Test.actual);
    }
}

