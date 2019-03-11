package com.fishercoder;


import _190.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _190Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(536870912, _190Test.solution1.reverseBits(4));
    }

    @Test
    public void test2() {
        Assert.assertEquals(964176192, _190Test.solution1.reverseBits(43261596));
    }
}

