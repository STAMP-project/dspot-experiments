package com.fishercoder;


import com.fishercoder.solutions._547;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/9/17.
 */
public class _547Test {
    private static _547 test;

    private static int expected;

    private static int actual;

    private static int[][] M;

    @Test
    public void test1() {
        _547Test.M = new int[][]{ new int[]{ 1, 1, 0 }, new int[]{ 1, 1, 0 }, new int[]{ 0, 0, 1 } };
        _547Test.expected = 2;
        _547Test.actual = _547Test.test.findCircleNum(_547Test.M);
        Assert.assertEquals(_547Test.expected, _547Test.actual);
    }

    @Test
    public void test2() {
        _547Test.M = new int[][]{ new int[]{ 1, 1, 0 }, new int[]{ 1, 1, 1 }, new int[]{ 0, 1, 1 } };
        _547Test.expected = 1;
        _547Test.actual = _547Test.test.findCircleNum(_547Test.M);
        Assert.assertEquals(_547Test.expected, _547Test.actual);
    }

    @Test
    public void test3() {
        _547Test.M = new int[][]{ new int[]{ 1, 0, 0, 1 }, new int[]{ 0, 1, 1, 0 }, new int[]{ 0, 1, 1, 1 }, new int[]{ 1, 0, 1, 1 } };
        _547Test.expected = 1;
        _547Test.actual = _547Test.test.findCircleNum(_547Test.M);
        Assert.assertEquals(_547Test.expected, _547Test.actual);
    }
}

