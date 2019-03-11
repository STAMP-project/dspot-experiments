package com.fishercoder;


import _7.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _7Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        // its reversed number is greater than Integer.MAX_VALUE, thus return 0
        Assert.assertEquals(0, _7Test.solution1.reverse(1534236469));
    }
}

