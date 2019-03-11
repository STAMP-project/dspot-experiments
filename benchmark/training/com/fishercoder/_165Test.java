package com.fishercoder;


import _165.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _165Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals((-1), _165Test.solution1.compareVersion("1.1", "1.2"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(1, _165Test.solution1.compareVersion("1.0.1", "1"));
    }

    @Test
    public void test3() {
        Assert.assertEquals((-0), _165Test.solution1.compareVersion("1.0", "1"));
    }
}

