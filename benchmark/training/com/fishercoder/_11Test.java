package com.fishercoder;


import _11.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _11Test {
    private static Solution1 solution1;

    private static int[] height;

    @Test
    public void test1() {
        _11Test.height = new int[]{ 1, 1 };
        Assert.assertEquals(1, _11Test.solution1.maxArea(_11Test.height));
    }
}

