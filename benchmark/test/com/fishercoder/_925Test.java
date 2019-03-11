package com.fishercoder;


import _925.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _925Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(true, _925Test.solution1.isLongPressedName("alex", "aaleex"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(false, _925Test.solution1.isLongPressedName("saeed", "ssaaedd"));
    }

    @Test
    public void test3() {
        Assert.assertEquals(true, _925Test.solution1.isLongPressedName("leelee", "lleeelee"));
    }

    @Test
    public void test4() {
        Assert.assertEquals(true, _925Test.solution1.isLongPressedName("laiden", "laiden"));
    }

    @Test
    public void test5() {
        Assert.assertEquals(false, _925Test.solution1.isLongPressedName("pyplrz", "ppyypllr"));
    }

    @Test
    public void test6() {
        Assert.assertEquals(true, _925Test.solution1.isLongPressedName("leelee", "lleeelee"));
    }
}

