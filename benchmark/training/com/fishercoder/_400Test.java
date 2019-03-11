package com.fishercoder;


import _400.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _400Test {
    private static Solution1 solution1;

    private static int expected;

    private static int actual;

    private static int n;

    @Test
    public void test1() {
        _400Test.n = 11;
        _400Test.expected = 0;
        _400Test.actual = _400Test.solution1.findNthDigit(_400Test.n);
        Assert.assertEquals(_400Test.expected, _400Test.actual);
    }
}

