package com.fishercoder;


import _396.Solution1;
import _396.Solution2;
import org.junit.Assert;
import org.junit.Test;


public class _396Test {
    private static Solution1 solution1;

    private static Solution2 solution2;

    private static int[] A;

    @Test
    public void test1() {
        _396Test.A = new int[]{ 4, 3, 2, 6 };
        Assert.assertEquals(26, _396Test.solution1.maxRotateFunction(_396Test.A));
        Assert.assertEquals(26, _396Test.solution2.maxRotateFunction(_396Test.A));
    }

    @Test
    public void test2() {
        _396Test.A = new int[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        Assert.assertEquals(330, _396Test.solution1.maxRotateFunction(_396Test.A));
        Assert.assertEquals(330, _396Test.solution2.maxRotateFunction(_396Test.A));
    }
}

