package com.fishercoder;


import _273.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _273Test {
    private static Solution1 solution1;

    private static int num;

    @Test
    public void test1() {
        _273Test.num = 123;
        Assert.assertEquals("One Hundred Twenty Three", _273Test.solution1.numberToWords(_273Test.num));
    }

    @Test
    public void test2() {
        _273Test.num = 12345;
        Assert.assertEquals("Twelve Thousand Three Hundred Forty Five", _273Test.solution1.numberToWords(_273Test.num));
    }

    @Test
    public void test3() {
        _273Test.num = 1234567;
        Assert.assertEquals("One Million Two Hundred Thirty Four Thousand Five Hundred Sixty Seven", _273Test.solution1.numberToWords(_273Test.num));
    }
}

