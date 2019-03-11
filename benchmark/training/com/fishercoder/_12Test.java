package com.fishercoder;


import _12.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _12Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals("XII", _12Test.solution1.intToRoman(12));
    }

    @Test
    public void test2() {
        Assert.assertEquals("M", _12Test.solution1.intToRoman(1000));
    }

    @Test
    public void test3() {
        Assert.assertEquals("MMMCMXCIX", _12Test.solution1.intToRoman(3999));
    }
}

