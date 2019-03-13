package com.fishercoder;


import _688.Solution1;
import _688.Solution2;
import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class _688Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        Assert.assertEquals(0.0625, _688Test.solution1.knightProbability(3, 2, 0, 0));
        Assert.assertEquals(0.0625, _688Test.solution2.knightProbability(3, 2, 0, 0));
    }

    @Test
    public void test2() {
        assertTrue(((Math.abs((1.9E-4 - (_688Test.solution2.knightProbability(8, 30, 6, 4))))) < 1.0E-6));
    }
}

