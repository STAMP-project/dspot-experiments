package com.fishercoder;


import _27.Solution1;
import junit.framework.TestCase;
import org.junit.Test;


public class _27Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _27Test.nums = new int[]{ 3, 2, 2, 3 };
        TestCase.assertEquals(2, _27Test.solution1.removeElement(_27Test.nums, 3));
    }

    @Test
    public void test2() {
        _27Test.nums = new int[]{ 2, 2, 3 };
        TestCase.assertEquals(1, _27Test.solution1.removeElement(_27Test.nums, 2));
    }

    @Test
    public void test3() {
        _27Test.nums = new int[]{ 1 };
        TestCase.assertEquals(0, _27Test.solution1.removeElement(_27Test.nums, 1));
    }
}

