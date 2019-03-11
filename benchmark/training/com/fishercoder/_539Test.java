package com.fishercoder;


import com.fishercoder.solutions._539;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _539Test {
    private static _539 test;

    private static int expected;

    private static int actual;

    private static List<String> timePoints;

    @Test
    public void test1() {
        _539Test.timePoints = new ArrayList<>(Arrays.asList("23:59", "00:00"));
        _539Test.expected = 1;
        _539Test.actual = _539Test.test.findMinDifference(_539Test.timePoints);
        Assert.assertEquals(_539Test.expected, _539Test.actual);
    }

    @Test
    public void test2() {
        _539Test.timePoints = new ArrayList<>(Arrays.asList("23:59", "00:00", "01:20"));
        _539Test.expected = 1;
        _539Test.actual = _539Test.test.findMinDifference(_539Test.timePoints);
        Assert.assertEquals(_539Test.expected, _539Test.actual);
    }
}

