package com.fishercoder;


import _166.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _166Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals("0.5", _166Test.solution1.fractionToDecimal(1, 2));
    }

    @Test
    public void test2() {
        Assert.assertEquals("2", _166Test.solution1.fractionToDecimal(2, 1));
    }

    @Test
    public void test3() {
        Assert.assertEquals("0.(6)", _166Test.solution1.fractionToDecimal(2, 3));
    }

    @Test
    public void test4() {
        Assert.assertEquals("-6.25", _166Test.solution1.fractionToDecimal((-50), 8));
    }

    @Test
    public void test5() {
        Assert.assertEquals("-0.58(3)", _166Test.solution1.fractionToDecimal(7, (-12)));
    }

    @Test
    public void test6() {
        Assert.assertEquals("0.0000000004656612873077392578125", _166Test.solution1.fractionToDecimal((-1), -2147483648));
    }

    @Test
    public void test7() {
        Assert.assertEquals("0", _166Test.solution1.fractionToDecimal(0, (-5)));
    }

    @Test
    public void test8() {
        Assert.assertEquals("-2147483648", _166Test.solution1.fractionToDecimal(-2147483648, 1));
    }
}

