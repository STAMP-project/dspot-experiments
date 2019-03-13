package com.fishercoder;


import _621.Solution1;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by stevesun on 6/19/17.
 */
public class _621Test {
    private static Solution1 solution1;

    private static char[] tasks;

    @Test
    public void test1() {
        _621Test.tasks = new char[]{ 'A', 'A', 'A', 'B', 'B', 'B' };
        Assert.assertEquals(8, _621Test.solution1.leastInterval(_621Test.tasks, 2));
    }
}

