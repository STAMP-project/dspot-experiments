package com.fishercoder;


import _762.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _762Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        TestCase.assertEquals(4, _762Test.solution1.countPrimeSetBits(6, 10));
    }
}

