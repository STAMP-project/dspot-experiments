package com.fishercoder;


import _13.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _13Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(12, _13Test.solution1.romanToInt("XII"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(1000, _13Test.solution1.romanToInt("M"));
    }

    @Test
    public void test3() {
        Assert.assertEquals(3999, _13Test.solution1.romanToInt("MMMCMXCIX"));
    }
}

