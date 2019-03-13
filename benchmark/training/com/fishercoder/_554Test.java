package com.fishercoder;


import com.fishercoder.solutions._554;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;


public class _554Test {
    private static _554 test;

    private static int expected;

    private static int actual;

    private static List<List<Integer>> wall;

    @Test
    public void test1() {
        _554Test.wall = new ArrayList<>();
        _554Test.wall.add(Arrays.asList(1, 2, 2, 1));
        _554Test.wall.add(Arrays.asList(3, 1, 2));
        _554Test.wall.add(Arrays.asList(1, 3, 2));
        _554Test.wall.add(Arrays.asList(2, 4));
        _554Test.wall.add(Arrays.asList(3, 1, 2));
        _554Test.wall.add(Arrays.asList(1, 3, 1, 1));
        _554Test.expected = 2;
        _554Test.actual = _554Test.test.leastBricks(_554Test.wall);
        Assert.assertEquals(_554Test.expected, _554Test.actual);
    }
}

