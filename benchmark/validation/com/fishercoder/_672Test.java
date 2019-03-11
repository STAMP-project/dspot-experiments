package com.fishercoder;


import _672.Solution1;
import _672.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _672Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        Assert.assertEquals(2, _672Test.solution1.flipLights(1, 1));
        Assert.assertEquals(2, _672Test.solution2.flipLights(1, 1));
    }

    @Test
    public void test2() {
        Assert.assertEquals(3, _672Test.solution1.flipLights(2, 1));
        Assert.assertEquals(3, _672Test.solution2.flipLights(2, 1));
    }

    @Test
    public void test3() {
        Assert.assertEquals(4, _672Test.solution1.flipLights(3, 1));
        Assert.assertEquals(4, _672Test.solution2.flipLights(3, 1));
    }

    @Test
    public void test4() {
        Assert.assertEquals(4, _672Test.solution1.flipLights(4, 1));
        Assert.assertEquals(4, _672Test.solution2.flipLights(4, 1));
    }

    @Test
    public void test5() {
        Assert.assertEquals(4, _672Test.solution1.flipLights(10, 1));
        Assert.assertEquals(4, _672Test.solution2.flipLights(10, 1));
    }

    @Test
    public void test6() {
        Assert.assertEquals(4, _672Test.solution1.flipLights(2, 2));
        Assert.assertEquals(4, _672Test.solution2.flipLights(2, 2));
    }

    @Test
    public void test7() {
        Assert.assertEquals(2, _672Test.solution1.flipLights(1, 2));
        Assert.assertEquals(2, _672Test.solution2.flipLights(1, 2));
    }

    @Test
    public void test8() {
        Assert.assertEquals(2, _672Test.solution1.flipLights(1, 3));
        Assert.assertEquals(2, _672Test.solution2.flipLights(1, 3));
    }

    @Test
    public void test9() {
        Assert.assertEquals(2, _672Test.solution1.flipLights(1, 4));
        Assert.assertEquals(2, _672Test.solution2.flipLights(1, 4));
    }

    @Test
    public void test10() {
        Assert.assertEquals(2, _672Test.solution1.flipLights(1, 5));
        Assert.assertEquals(2, _672Test.solution2.flipLights(1, 5));
    }

    @Test
    public void test11() {
        Assert.assertEquals(2, _672Test.solution1.flipLights(1, 10));
        Assert.assertEquals(2, _672Test.solution2.flipLights(1, 10));
    }

    @Test
    public void test12() {
        Assert.assertEquals(7, _672Test.solution1.flipLights(3, 2));
        Assert.assertEquals(7, _672Test.solution2.flipLights(3, 2));
    }

    @Test
    public void test13() {
        Assert.assertEquals(8, _672Test.solution1.flipLights(7, 5));
        Assert.assertEquals(8, _672Test.solution2.flipLights(7, 5));
    }

    @Test
    public void test14() {
        Assert.assertEquals(1, _672Test.solution1.flipLights(1, 0));
        Assert.assertEquals(1, _672Test.solution2.flipLights(1, 0));
    }

    @Test
    public void test15() {
        Assert.assertEquals(8, _672Test.solution1.flipLights(7, 5));
        Assert.assertEquals(8, _672Test.solution2.flipLights(7, 5));
    }
}

