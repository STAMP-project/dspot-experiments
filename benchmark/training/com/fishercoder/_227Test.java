package com.fishercoder;


import _227.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _227Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(7, _227Test.solution1.calculate("3+2*2"));
    }

    @Test
    public void test2() {
        Assert.assertEquals(1, _227Test.solution1.calculate(" 3/2 "));
    }

    @Test
    public void test3() {
        Assert.assertEquals(5, _227Test.solution1.calculate(" 3+5 / 2 "));
    }

    @Test
    public void test4() {
        Assert.assertEquals(27, _227Test.solution1.calculate("100000000/1/2/3/4/5/6/7/8/9/10"));
    }
}

