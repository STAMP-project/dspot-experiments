package com.fishercoder;


import com.fishercoder.solutions._458;
import junit.framework.Assert;
import org.junit.Test;


public class _458Test {
    private static _458 test;

    private static int expected;

    private static int actual;

    private static int buckets;

    private static int minutesToDie;

    private static int minutesToTest;

    @Test
    public void test1() {
        _458Test.buckets = 1000;
        _458Test.minutesToDie = 15;
        _458Test.minutesToTest = 60;
        _458Test.expected = 5;
        _458Test.actual = _458Test.test.poorPigs(_458Test.buckets, _458Test.minutesToDie, _458Test.minutesToTest);
        Assert.assertEquals(_458Test.expected, _458Test.actual);
    }

    @Test
    public void test2() {
        _458Test.buckets = 1;
        _458Test.minutesToDie = 1;
        _458Test.minutesToTest = 1;
        _458Test.expected = 0;
        _458Test.actual = _458Test.test.poorPigs(_458Test.buckets, _458Test.minutesToDie, _458Test.minutesToTest);
        Assert.assertEquals(_458Test.expected, _458Test.actual);
    }

    @Test
    public void test3() {
        _458Test.buckets = 1000;
        _458Test.minutesToDie = 12;
        _458Test.minutesToTest = 60;
        _458Test.expected = 4;
        _458Test.actual = _458Test.test.poorPigs(_458Test.buckets, _458Test.minutesToDie, _458Test.minutesToTest);
        Assert.assertEquals(_458Test.expected, _458Test.actual);
    }
}

