package com.fishercoder;


import com.fishercoder.solutions._492;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 1/25/17.
 */
public class _492Test {
    private static _492 test;

    private static int[] expected;

    private static int[] actual;

    private static int area;

    @Test
    public void test1() {
        _492Test.area = 4;
        _492Test.expected = new int[]{ 2, 2 };
        _492Test.actual = _492Test.test.constructRectangle(_492Test.area);
        Assert.assertArrayEquals(_492Test.expected, _492Test.actual);
    }

    @Test
    public void test2() {
        _492Test.area = 3;
        _492Test.expected = new int[]{ 3, 1 };
        _492Test.actual = _492Test.test.constructRectangle(_492Test.area);
        Assert.assertArrayEquals(_492Test.expected, _492Test.actual);
    }
}

