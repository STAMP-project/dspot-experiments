package com.fishercoder;


import _852.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _852Test {
    private static Solution1 solution1;

    private static int[] A;

    @Test
    public void test1() {
        _852Test.A = new int[]{ 0, 1, 0 };
        TestCase.assertEquals(1, _852Test.solution1.peakIndexInMountainArray(_852Test.A));
    }

    @Test
    public void test2() {
        _852Test.A = new int[]{ 0, 2, 1, 0 };
        TestCase.assertEquals(1, _852Test.solution1.peakIndexInMountainArray(_852Test.A));
    }
}

