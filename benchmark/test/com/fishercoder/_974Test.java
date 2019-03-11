package com.fishercoder;


import _974.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _974Test {
    private static Solution1 test;

    @Test
    public void test1() {
        Assert.assertEquals(7, _974Test.test.subarraysDivByK(new int[]{ 4, 5, 0, -2, -3, 1 }, 5));
    }
}

