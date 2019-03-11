package com.fishercoder;


import com.fishercoder.solutions._562;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 4/23/17.
 */
public class _562Test {
    private static _562 test;

    private static int expected;

    private static int actual;

    private static int[][] M;

    @Test
    public void test1() {
        _562Test.M = new int[][]{ new int[]{ 0, 1, 1, 0 }, new int[]{ 0, 1, 1, 0 }, new int[]{ 0, 0, 0, 1 } };
        _562Test.expected = 3;
        _562Test.actual = _562Test.test.longestLine(_562Test.M);
        Assert.assertEquals(_562Test.expected, _562Test.actual);
    }
}

