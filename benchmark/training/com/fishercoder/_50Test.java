package com.fishercoder;


import _50.Solution1;
import _50.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _50Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        Assert.assertEquals(1024.0, _50Test.solution1.myPow(2.0, 10), 1.0E-5);
        Assert.assertEquals(1024.0, _50Test.solution2.myPow(2.0, 10), 1.0E-5);
    }
}

