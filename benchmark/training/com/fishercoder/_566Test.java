package com.fishercoder;


import com.fishercoder.solutions._566;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 4/29/17.
 */
public class _566Test {
    private static _566 test;

    private static int[][] expected;

    private static int[][] actual;

    private static int[][] nums;

    private static int r;

    private static int c;

    @Test
    public void test1() {
        _566Test.nums = new int[][]{ new int[]{ 1, 2 }, new int[]{ 3, 4 } };
        _566Test.r = 1;
        _566Test.c = 4;
        _566Test.expected = new int[][]{ new int[]{ 1, 2, 3, 4 } };
        _566Test.actual = _566Test.test.matrixReshape(_566Test.nums, _566Test.r, _566Test.c);
        Assert.assertArrayEquals(_566Test.expected, _566Test.actual);
    }
}

