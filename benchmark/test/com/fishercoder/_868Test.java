package com.fishercoder;


import _868.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _868Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(2, _868Test.solution1.binaryGap(22));
    }

    @Test
    public void test2() {
        Assert.assertEquals(2, _868Test.solution1.binaryGap(5));
    }

    @Test
    public void test3() {
        Assert.assertEquals(1, _868Test.solution1.binaryGap(6));
    }

    @Test
    public void test4() {
        Assert.assertEquals(0, _868Test.solution1.binaryGap(8));
    }
}

