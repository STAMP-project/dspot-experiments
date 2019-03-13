package com.fishercoder;


import _961.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _961Test {
    private static Solution1 solution1;

    private static int[] A;

    @Test
    public void test1() {
        _961Test.A = new int[]{ 1, 2, 3, 3 };
        TestCase.assertEquals(3, _961Test.solution1.repeatedNTimes(_961Test.A));
    }

    @Test
    public void test2() {
        _961Test.A = new int[]{ 2, 1, 2, 5, 3, 2 };
        TestCase.assertEquals(2, _961Test.solution1.repeatedNTimes(_961Test.A));
    }

    @Test
    public void test3() {
        _961Test.A = new int[]{ 5, 1, 5, 2, 5, 3, 5, 4 };
        TestCase.assertEquals(5, _961Test.solution1.repeatedNTimes(_961Test.A));
    }
}

