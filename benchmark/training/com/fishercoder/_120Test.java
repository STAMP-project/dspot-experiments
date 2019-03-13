package com.fishercoder;


import _120.Solution1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _120Test {
    private static Solution1 solution1;

    private static List<List<Integer>> triangle;

    @Test
    public void test1() {
        _120Test.triangle = new ArrayList();
        _120Test.triangle.add(Arrays.asList(1));
        _120Test.triangle.add(Arrays.asList(2, 3));
        Assert.assertEquals(3, _120Test.solution1.minimumTotal(_120Test.triangle));
    }
}

