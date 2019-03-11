package com.fishercoder;


import _989.Solution1;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;


public class _989Test {
    private static Solution1 solution1;

    private static int[] A;

    @Test
    public void test1() {
        _989Test.A = new int[]{ 1, 2, 0, 0 };
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), _989Test.solution1.addToArrayForm(_989Test.A, 34));
    }

    @Test
    public void test2() {
        _989Test.A = new int[]{ 2, 7, 4 };
        Assert.assertEquals(Arrays.asList(4, 5, 5), _989Test.solution1.addToArrayForm(_989Test.A, 181));
    }

    @Test
    public void test3() {
        _989Test.A = new int[]{ 2, 1, 5 };
        Assert.assertEquals(Arrays.asList(1, 0, 2, 1), _989Test.solution1.addToArrayForm(_989Test.A, 806));
    }

    @Test
    public void test4() {
        _989Test.A = new int[]{ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 };
        Assert.assertEquals(Arrays.asList(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), _989Test.solution1.addToArrayForm(_989Test.A, 1));
    }
}

