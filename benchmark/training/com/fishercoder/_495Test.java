package com.fishercoder;


import com.fishercoder.solutions._495;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/8/17.
 */
public class _495Test {
    private static int actual = 0;

    private static int expected = 0;

    private static int[] timeSeries;

    private static int duration = 0;

    @Test
    public void test1() {
        _495 test = new _495();
        _495Test.timeSeries = new int[]{ 1, 4 };
        _495Test.duration = 2;
        _495Test.actual = test.findPoisonedDuration(_495Test.timeSeries, _495Test.duration);
        _495Test.expected = 4;
        Assert.assertEquals(_495Test.expected, _495Test.actual);
    }

    @Test
    public void test2() {
        _495 test = new _495();
        _495Test.timeSeries = new int[]{ 1, 2 };
        _495Test.duration = 2;
        _495Test.actual = test.findPoisonedDuration(_495Test.timeSeries, _495Test.duration);
        _495Test.expected = 3;
        Assert.assertEquals(_495Test.expected, _495Test.actual);
    }

    @Test
    public void test3() {
        _495 test = new _495();
        _495Test.timeSeries = new int[]{  };
        _495Test.duration = 100000;
        _495Test.actual = test.findPoisonedDuration(_495Test.timeSeries, _495Test.duration);
        _495Test.expected = 0;
        Assert.assertEquals(_495Test.expected, _495Test.actual);
    }
}

