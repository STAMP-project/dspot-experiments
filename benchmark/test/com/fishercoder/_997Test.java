package com.fishercoder;


import _997.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _997Test {
    private static Solution1 solution1;

    private static int[][] trust;

    @Test
    public void test1() {
        _997Test.trust = new int[][]{ new int[]{ 1, 2 } };
        Assert.assertEquals(2, _997Test.solution1.findJudge(2, _997Test.trust));
    }

    @Test
    public void test2() {
        _997Test.trust = new int[][]{ new int[]{ 1, 3 }, new int[]{ 2, 3 } };
        Assert.assertEquals(3, _997Test.solution1.findJudge(3, _997Test.trust));
    }

    @Test
    public void test3() {
        _997Test.trust = new int[][]{ new int[]{ 1, 2 }, new int[]{ 2, 3 }, new int[]{ 3, 1 } };
        Assert.assertEquals((-1), _997Test.solution1.findJudge(3, _997Test.trust));
    }

    @Test
    public void test4() {
        _997Test.trust = new int[][]{ new int[]{ 1, 2 }, new int[]{ 2, 3 } };
        Assert.assertEquals((-1), _997Test.solution1.findJudge(3, _997Test.trust));
    }

    @Test
    public void test5() {
        _997Test.trust = new int[][]{ new int[]{ 1, 3 }, new int[]{ 1, 4 }, new int[]{ 2, 3 }, new int[]{ 2, 4 }, new int[]{ 4, 3 } };
        Assert.assertEquals(3, _997Test.solution1.findJudge(4, _997Test.trust));
    }

    @Test
    public void test6() {
        _997Test.trust = new int[][]{ new int[]{ 1, 3 }, new int[]{ 2, 3 }, new int[]{ 3, 1 } };
        Assert.assertEquals((-1), _997Test.solution1.findJudge(3, _997Test.trust));
    }
}

