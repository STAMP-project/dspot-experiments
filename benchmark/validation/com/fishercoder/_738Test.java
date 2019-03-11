package com.fishercoder;


import _738.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _738Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        TestCase.assertEquals(9, _738Test.solution1.monotoneIncreasingDigits(10));
    }
}

