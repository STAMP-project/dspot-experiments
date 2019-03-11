package com.fishercoder;


import _679.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _679Test {
    private static Solution1 solution1;

    @Test
    public void test1() {
        Assert.assertEquals(true, _679Test.solution1.judgePoint24(new int[]{ 4, 1, 8, 7 }));
    }

    @Test
    public void test2() {
        Assert.assertEquals(false, _679Test.solution1.judgePoint24(new int[]{ 1, 2, 1, 2 }));
    }

    @Test
    public void test3() {
        // 8 / (1 - 2/3) = 24
        Assert.assertEquals(true, _679Test.solution1.judgePoint24(new int[]{ 1, 2, 3, 8 }));
    }

    @Test
    public void test4() {
        Assert.assertEquals(true, _679Test.solution1.judgePoint24(new int[]{ 1, 3, 4, 6 }));
    }

    @Test
    public void test5() {
        Assert.assertEquals(true, _679Test.solution1.judgePoint24(new int[]{ 1, 9, 1, 2 }));
    }
}

