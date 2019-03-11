package com.fishercoder;


import _789.Solution;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by varunu28 on 1/01/19.
 */
public class _789Test {
    private static Solution test;

    @Test
    public void test1() {
        Assert.assertEquals(true, _789Test.test.escapeGhosts(new int[][]{ new int[]{ 1, 0 }, new int[]{ 0, 3 } }, new int[]{ 0, 1 }));
    }

    @Test
    public void test2() {
        Assert.assertEquals(false, _789Test.test.escapeGhosts(new int[][]{ new int[]{ 1, 0 } }, new int[]{ 2, 0 }));
    }

    @Test
    public void test3() {
        Assert.assertEquals(false, _789Test.test.escapeGhosts(new int[][]{ new int[]{ 2, 0 } }, new int[]{ 1, 0 }));
    }
}

