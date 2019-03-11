package com.fishercoder;


import _526.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _526Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(2, _526Test.solution1.countArrangement(2));
    }

    @Test
    public void test2() {
        Assert.assertEquals(5, _526Test.solution1.countArrangement(3));
    }
}

