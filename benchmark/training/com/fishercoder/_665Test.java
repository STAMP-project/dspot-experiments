package com.fishercoder;


import _665.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _665Test {
    private static Solution1 solution1;

    private static int[] nums;

    @Test
    public void test1() {
        _665Test.nums = new int[]{ 4, 2, 3 };
        Assert.assertEquals(true, _665Test.solution1.checkPossibility(_665Test.nums));
    }

    @Test
    public void test2() {
        _665Test.nums = new int[]{ 4, 2, 1 };
        Assert.assertEquals(false, _665Test.solution1.checkPossibility(_665Test.nums));
    }

    @Test
    public void test3() {
        _665Test.nums = new int[]{ 3, 4, 2, 3 };
        Assert.assertEquals(false, _665Test.solution1.checkPossibility(_665Test.nums));
    }

    @Test
    public void test4() {
        _665Test.nums = new int[]{ 2, 3, 3, 2, 4 };
        Assert.assertEquals(true, _665Test.solution1.checkPossibility(_665Test.nums));
    }

    @Test
    public void test5() {
        _665Test.nums = new int[]{ 2, 3, 3, 2, 2, 4 };
        Assert.assertEquals(false, _665Test.solution1.checkPossibility(_665Test.nums));
    }

    @Test
    public void test6() {
        _665Test.nums = new int[]{ 2, 3, 3, 2, 2, 2, 4 };
        Assert.assertEquals(false, _665Test.solution1.checkPossibility(_665Test.nums));
    }

    @Test
    public void test7() {
        _665Test.nums = new int[]{ 3, 3, 2, 2 };
        Assert.assertEquals(false, _665Test.solution1.checkPossibility(_665Test.nums));
    }

    @Test
    public void test8() {
        _665Test.nums = new int[]{ -1, 4, 2, 3 };
        Assert.assertEquals(true, _665Test.solution1.checkPossibility(_665Test.nums));
    }

    @Test
    public void test9() {
        _665Test.nums = new int[]{ 1, 2, 4, 5, 3 };
        Assert.assertEquals(true, _665Test.solution1.checkPossibility(_665Test.nums));
    }

    @Test
    public void test10() {
        _665Test.nums = new int[]{ 1, 2, 4, 5, 3, 6 };
        Assert.assertEquals(true, _665Test.solution1.checkPossibility(_665Test.nums));
    }
}

