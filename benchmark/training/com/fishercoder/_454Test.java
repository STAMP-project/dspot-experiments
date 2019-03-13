package com.fishercoder;


import com.fishercoder.solutions._454;
import junit.framework.Assert;
import org.junit.Test;


public class _454Test {
    private static _454 test;

    private static int expected;

    private static int actual;

    private static int[] A;

    private static int[] B;

    private static int[] C;

    private static int[] D;

    @Test
    public void test1() {
        _454Test.A = new int[]{ 1, 2 };
        _454Test.B = new int[]{ -2, -1 };
        _454Test.C = new int[]{ -1, 2 };
        _454Test.D = new int[]{ 0, 2 };
        _454Test.expected = 2;
        _454Test.actual = _454Test.test.fourSumCount(_454Test.A, _454Test.B, _454Test.C, _454Test.D);
        Assert.assertEquals(_454Test.expected, _454Test.actual);
    }
}

