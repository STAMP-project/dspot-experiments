package com.fishercoder;


import _201.Solution1;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/3/17.
 */
public class _201Test {
    private static Solution1 solution1;

    private static int m;

    private static int n;

    private static int actual;

    private static int expected;

    @Test
    public void test1() {
        _201Test.m = 5;
        _201Test.n = 7;
        _201Test.actual = _201Test.solution1.rangeBitwiseAnd(_201Test.m, _201Test.n);
        _201Test.expected = 4;
        Assert.assertEquals(_201Test.expected, _201Test.actual);
        _201Test.actual = _201Test.solution1.rangeBitwiseAnd(_201Test.m, _201Test.n);
        Assert.assertEquals(_201Test.expected, _201Test.actual);
    }

    @Test
    public void test2() {
        _201Test.m = 1;
        _201Test.n = 2;
        _201Test.actual = _201Test.solution1.rangeBitwiseAnd(_201Test.m, _201Test.n);
        _201Test.expected = 0;
        Assert.assertEquals(_201Test.expected, _201Test.actual);
        _201Test.actual = _201Test.solution1.rangeBitwiseAnd(_201Test.m, _201Test.n);
        Assert.assertEquals(_201Test.expected, _201Test.actual);
    }

    @Test
    public void test3() {
        _201Test.m = 0;
        _201Test.n = 2147483647;
        _201Test.actual = _201Test.solution1.rangeBitwiseAnd(_201Test.m, _201Test.n);
        _201Test.expected = 0;
        Assert.assertEquals(_201Test.expected, _201Test.actual);
        _201Test.actual = _201Test.solution1.rangeBitwiseAnd(_201Test.m, _201Test.n);
        Assert.assertEquals(_201Test.expected, _201Test.actual);
    }

    @Test
    public void test4() {
        _201Test.m = 20000;
        _201Test.n = 2147483647;
        _201Test.actual = _201Test.solution1.rangeBitwiseAnd(_201Test.m, _201Test.n);
        _201Test.expected = 0;
        Assert.assertEquals(_201Test.expected, _201Test.actual);
        _201Test.actual = _201Test.solution1.rangeBitwiseAnd(_201Test.m, _201Test.n);
        Assert.assertEquals(_201Test.expected, _201Test.actual);
    }
}

