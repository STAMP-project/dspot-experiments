package com.fishercoder;


import _941.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _941Test {
    private static Solution1 solution1;

    private static int[] A;

    @Test
    public void test1() {
        _941Test.A = new int[]{ 0, 3, 2, 1 };
        Assert.assertEquals(true, _941Test.solution1.validMountainArray(_941Test.A));
    }

    @Test
    public void test2() {
        _941Test.A = new int[]{ 2, 1 };
        Assert.assertEquals(false, _941Test.solution1.validMountainArray(_941Test.A));
    }

    @Test
    public void test3() {
        _941Test.A = new int[]{ 3, 5, 5 };
        Assert.assertEquals(false, _941Test.solution1.validMountainArray(_941Test.A));
    }

    @Test
    public void test4() {
        _941Test.A = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertEquals(false, _941Test.solution1.validMountainArray(_941Test.A));
    }
}

