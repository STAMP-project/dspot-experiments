package com.fishercoder;


import _668.Solution1;
import _668.Solution2;
import junit.framework.TestCase;
import org.junit.Test;


public class _668Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    @Test
    public void test1() {
        TestCase.assertEquals(3, _668Test.solution1.findKthNumber(3, 3, 5));
        TestCase.assertEquals(3, _668Test.solution2.findKthNumber(3, 3, 5));
    }

    @Test
    public void test2() {
        TestCase.assertEquals(6, _668Test.solution1.findKthNumber(2, 3, 6));
        TestCase.assertEquals(6, _668Test.solution2.findKthNumber(2, 3, 6));
    }

    @Test
    public void test3() {
        // assertEquals(31666344, solution1.findKthNumber(9895, 28405, 100787757));//this will run into OOM error, so comment out
        TestCase.assertEquals(31666344, _668Test.solution2.findKthNumber(9895, 28405, 100787757));
    }
}

