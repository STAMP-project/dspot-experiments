package com.fishercoder;


import com.fishercoder.solutions._575;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/6/17.
 */
public class _575Test {
    private static _575 test;

    private static int expected;

    private static int actual;

    private static int[] candies;

    @Test
    public void test1() {
        _575Test.candies = new int[]{ 1, 1, 2, 3 };
        _575Test.expected = 2;
        _575Test.actual = _575Test.test.distributeCandies(_575Test.candies);
        Assert.assertEquals(_575Test.expected, _575Test.actual);
    }

    @Test
    public void test2() {
        _575Test.candies = new int[]{ 1, 1, 2, 2, 3, 3 };
        _575Test.expected = 3;
        _575Test.actual = _575Test.test.distributeCandies(_575Test.candies);
        Assert.assertEquals(_575Test.expected, _575Test.actual);
    }

    @Test
    public void test3() {
        _575Test.candies = new int[]{ 1000, 1, 1, 1 };
        _575Test.expected = 2;
        _575Test.actual = _575Test.test.distributeCandies(_575Test.candies);
        Assert.assertEquals(_575Test.expected, _575Test.actual);
    }
}

