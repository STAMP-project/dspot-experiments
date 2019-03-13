package com.fishercoder;


import com.fishercoder.solutions._556;
import org.junit.Assert;
import org.junit.Test;


public class _556Test {
    private static _556 test;

    private static int n;

    private static int expected;

    private static int actual;

    @Test
    public void test1() {
        _556Test.n = 12;
        _556Test.expected = 21;
        _556Test.actual = _556Test.test.nextGreaterElement(_556Test.n);
        Assert.assertEquals(_556Test.expected, _556Test.actual);
    }

    @Test
    public void test2() {
        _556Test.n = 21;
        _556Test.expected = -1;
        _556Test.actual = _556Test.test.nextGreaterElement(_556Test.n);
        Assert.assertEquals(_556Test.expected, _556Test.actual);
        Assert.assertTrue(((Integer.MAX_VALUE) > 1999999999));
    }

    @Test
    public void test3() {
        _556Test.n = 1999999999;
        _556Test.expected = -1;
        _556Test.actual = _556Test.test.nextGreaterElement(_556Test.n);
        Assert.assertEquals(_556Test.expected, _556Test.actual);
    }

    @Test
    public void test4() {
        _556Test.n = 12222333;
        _556Test.expected = 12223233;
        _556Test.actual = _556Test.test.nextGreaterElement(_556Test.n);
        Assert.assertEquals(_556Test.expected, _556Test.actual);
    }

    @Test
    public void test5() {
        _556Test.n = 12443322;
        _556Test.expected = 13222344;
        _556Test.actual = _556Test.test.nextGreaterElement(_556Test.n);
        Assert.assertEquals(_556Test.expected, _556Test.actual);
    }
}

