package com.fishercoder;


import _319.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _319Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(1, _319Test.solution1.bulbSwitch(2));
    }

    @Test
    public void test2() {
        Assert.assertEquals(1, _319Test.solution1.bulbSwitch(3));
    }

    @Test
    public void test3() {
        Assert.assertEquals(2, _319Test.solution1.bulbSwitch(4));
    }

    @Test
    public void test4() {
        Assert.assertEquals(2, _319Test.solution1.bulbSwitch(5));
    }

    @Test
    public void test5() {
        Assert.assertEquals(2, _319Test.solution1.bulbSwitch(6));
    }

    @Test
    public void test6() {
        Assert.assertEquals(2, _319Test.solution1.bulbSwitch(7));
    }

    @Test
    public void test7() {
        Assert.assertEquals(2, _319Test.solution1.bulbSwitch(8));
    }

    @Test
    public void test8() {
        Assert.assertEquals(3, _319Test.solution1.bulbSwitch(9));
    }

    @Test
    public void test11() {
        Assert.assertEquals(3, _319Test.solution1.bulbSwitch(15));
    }

    @Test
    public void test9() {
        Assert.assertEquals(4, _319Test.solution1.bulbSwitch(17));
    }

    @Test
    public void test10() {
        Assert.assertEquals(4, _319Test.solution1.bulbSwitch(16));
    }
}

