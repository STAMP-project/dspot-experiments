package com.fishercoder;


import _666.Solution1;
import _666.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _666Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] nums;

    @Test
    public void test1() {
        _666Test.nums = new int[]{ 113, 215, 221 };
        Assert.assertEquals(12, _666Test.solution1.pathSum(_666Test.nums));
        Assert.assertEquals(12, _666Test.solution2.pathSum(_666Test.nums));
    }

    @Test
    public void test2() {
        _666Test.nums = new int[]{ 113, 221 };
        Assert.assertEquals(4, _666Test.solution1.pathSum(_666Test.nums));
        Assert.assertEquals(4, _666Test.solution2.pathSum(_666Test.nums));
    }

    @Test
    public void test3() {
        _666Test.nums = new int[]{ 113, 214, 221, 348, 487 };
        Assert.assertEquals(26, _666Test.solution1.pathSum(_666Test.nums));
        Assert.assertEquals(26, _666Test.solution2.pathSum(_666Test.nums));
    }
}

