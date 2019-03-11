package com.fishercoder;


import _976.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _976Test {
    private static Solution1 test;

    @Test
    public void test1() {
        Assert.assertEquals(5, _976Test.test.largestPerimeter(new int[]{ 2, 1, 2 }));
    }

    @Test
    public void test2() {
        Assert.assertEquals(0, _976Test.test.largestPerimeter(new int[]{ 1, 2, 1 }));
    }

    @Test
    public void test3() {
        Assert.assertEquals(10, _976Test.test.largestPerimeter(new int[]{ 3, 2, 3, 4 }));
    }

    @Test
    public void test4() {
        Assert.assertEquals(8, _976Test.test.largestPerimeter(new int[]{ 3, 6, 2, 3 }));
    }
}

