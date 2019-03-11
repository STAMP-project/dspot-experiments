package com.fishercoder;


import _306.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _306Test {
    private static Solution1 solution1;

    private static String num;

    @Test
    public void test1() {
        _306Test.num = "0235813";
        Assert.assertEquals(false, _306Test.solution1.isAdditiveNumber(_306Test.num));
    }

    @Test
    public void test2() {
        _306Test.num = "000";
        Assert.assertEquals(true, _306Test.solution1.isAdditiveNumber(_306Test.num));
    }

    @Test
    public void test3() {
        _306Test.num = "011235";
        Assert.assertEquals(true, _306Test.solution1.isAdditiveNumber(_306Test.num));
    }
}

