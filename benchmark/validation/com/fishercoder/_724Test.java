package com.fishercoder;


import _724.Solution1;
import _724.Solution2;
import junit.framework.TestCase;
import org.junit.Test;


public class _724Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] nums;

    @Test
    public void test1() {
        _724Test.nums = new int[]{ 1, 7, 3, 6, 5, 6 };
        TestCase.assertEquals(3, _724Test.solution1.pivotIndex(_724Test.nums));
    }

    @Test
    public void test2() {
        _724Test.nums = new int[]{ 1, 2, 3 };
        TestCase.assertEquals((-1), _724Test.solution1.pivotIndex(_724Test.nums));
    }

    @Test
    public void test3() {
        _724Test.nums = new int[]{ -1, -1, -1, 0, 1, 1 };
        TestCase.assertEquals(0, _724Test.solution1.pivotIndex(_724Test.nums));
    }

    @Test
    public void test4() {
        _724Test.nums = new int[]{ -1, -1, 0, 1, 1, 0 };
        TestCase.assertEquals(5, _724Test.solution1.pivotIndex(_724Test.nums));
    }

    @Test
    public void test5() {
        _724Test.nums = new int[]{ 1, 7, 3, 6, 5, 6 };
        TestCase.assertEquals(3, _724Test.solution2.pivotIndex(_724Test.nums));
    }

    @Test
    public void test6() {
        _724Test.nums = new int[]{ 1, 2, 3 };
        TestCase.assertEquals((-1), _724Test.solution2.pivotIndex(_724Test.nums));
    }

    @Test
    public void test7() {
        _724Test.nums = new int[]{ -1, -1, -1, 0, 1, 1 };
        TestCase.assertEquals(0, _724Test.solution2.pivotIndex(_724Test.nums));
    }

    @Test
    public void test8() {
        _724Test.nums = new int[]{ -1, -1, 0, 1, 1, 0 };
        TestCase.assertEquals(5, _724Test.solution2.pivotIndex(_724Test.nums));
    }
}

