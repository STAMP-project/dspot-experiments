package com.fishercoder;


import _683.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _683Test {
    private static Solution1 solution1;

    private static int[] flowers;

    private static int k;

    @Test
    public void test1() {
        _683Test.flowers = new int[]{ 1, 3, 2 };
        _683Test.k = 1;
        Assert.assertEquals(2, _683Test.solution1.kEmptySlots(_683Test.flowers, _683Test.k));
    }

    @Test
    public void test2() {
        _683Test.flowers = new int[]{ 1, 2, 3 };
        _683Test.k = 1;
        Assert.assertEquals((-1), _683Test.solution1.kEmptySlots(_683Test.flowers, _683Test.k));
    }
}

