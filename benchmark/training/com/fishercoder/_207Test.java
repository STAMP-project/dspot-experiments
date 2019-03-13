package com.fishercoder;


import _207.Solution1;
import junit.framework.Assert;
import org.junit.Test;


public class _207Test {
    private static Solution1 test;

    private static boolean actual;

    private static boolean expected;

    private static int[][] prerequisites;

    private static int numCourses;

    @Test
    public void test1() {
        _207Test.numCourses = 2;
        _207Test.prerequisites = new int[][]{ new int[]{ 0, 1 } };
        _207Test.expected = true;
        _207Test.actual = _207Test.test.canFinish(_207Test.numCourses, _207Test.prerequisites);
        Assert.assertEquals(_207Test.expected, _207Test.actual);
    }
}

