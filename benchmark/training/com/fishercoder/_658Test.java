package com.fishercoder;


import com.fishercoder.solutions._658;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _658Test {
    private static _658 test;

    private static List<Integer> arr;

    private static List<Integer> expected;

    private static int k;

    private static int x;

    @Test
    public void test1() {
        _658Test.k = 4;
        _658Test.x = 3;
        _658Test.expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        _658Test.arr = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        Assert.assertEquals(_658Test.expected, _658Test.test.findClosestElements(_658Test.arr, _658Test.k, _658Test.x));
    }

    @Test
    public void test2() {
        _658Test.k = 4;
        _658Test.x = -1;
        _658Test.expected = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
        _658Test.arr = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
        Assert.assertEquals(_658Test.expected, _658Test.test.findClosestElements(_658Test.arr, _658Test.k, _658Test.x));
    }

    @Test
    public void test3() {
        _658Test.k = 3;
        _658Test.x = 5;
        _658Test.arr = new ArrayList<>(Arrays.asList(0, 0, 1, 2, 3, 3, 4, 7, 7, 8));
        _658Test.expected = new ArrayList<>(Arrays.asList(3, 3, 4));
        Assert.assertEquals(_658Test.expected, _658Test.test.findClosestElements(_658Test.arr, _658Test.k, _658Test.x));
    }
}

