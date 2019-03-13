package com.fishercoder;


import com.fishercoder.solutions._533;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/25/17.
 */
public class _533Test {
    private static _533 test;

    private static char[][] picture;

    @Test
    public void test1() {
        _533Test.picture = new char[][]{ new char[]{ 'W', 'B', 'W', 'B', 'B', 'W' }, new char[]{ 'W', 'B', 'W', 'B', 'B', 'W' }, new char[]{ 'W', 'B', 'W', 'B', 'B', 'W' }, new char[]{ 'W', 'W', 'B', 'W', 'B', 'W' } };
        Assert.assertEquals(6, _533Test.test.findBlackPixel(_533Test.picture, 3));
    }

    @Test
    public void test2() {
        _533Test.picture = new char[][]{ new char[]{ 'B' } };
        Assert.assertEquals(1, _533Test.test.findBlackPixel(_533Test.picture, 1));
    }
}

