package com.fishercoder;


import _134.Solution1;
import org.junit.Assert;
import org.junit.Test;


public class _134Test {
    private static Solution1 solution1;

    private static int[] gas;

    private static int[] cost;

    @Test
    public void test1() {
        _134Test.gas = new int[]{ 4 };
        _134Test.cost = new int[]{ 5 };
        Assert.assertEquals((-1), _134Test.solution1.canCompleteCircuit(_134Test.gas, _134Test.cost));
    }

    @Test
    public void test2() {
        _134Test.gas = new int[]{ 5 };
        _134Test.cost = new int[]{ 4 };
        Assert.assertEquals(0, _134Test.solution1.canCompleteCircuit(_134Test.gas, _134Test.cost));
    }

    @Test
    public void test3() {
        _134Test.gas = new int[]{ 2 };
        _134Test.cost = new int[]{ 2 };
        Assert.assertEquals(0, _134Test.solution1.canCompleteCircuit(_134Test.gas, _134Test.cost));
    }
}

