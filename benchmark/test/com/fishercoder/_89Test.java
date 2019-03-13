package com.fishercoder;


import _89.Solution1;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class _89Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(Arrays.asList(0, 1, 3, 2, 6, 7, 5, 4), _89Test.solution1.grayCode(3));
    }
}

