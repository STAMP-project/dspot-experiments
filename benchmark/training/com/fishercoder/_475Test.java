package com.fishercoder;


import com.fishercoder.solutions._475;
import junit.framework.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 4/23/17.
 */
public class _475Test {
    private static _475 test;

    private static int expected;

    private static int actual;

    private static int[] houses;

    private static int[] heaters;

    @Test
    public void test1() {
        _475Test.houses = new int[]{ 1, 2, 3 };
        _475Test.heaters = new int[]{ 2 };
        _475Test.expected = 1;
        _475Test.actual = _475Test.test.findRadius(_475Test.houses, _475Test.heaters);
        Assert.assertEquals(_475Test.expected, _475Test.actual);
    }

    @Test
    public void test2() {
        _475Test.houses = new int[]{ 1, 2, 3, 4 };
        _475Test.heaters = new int[]{ 1, 4 };
        _475Test.expected = 1;
        _475Test.actual = _475Test.test.findRadius(_475Test.houses, _475Test.heaters);
        Assert.assertEquals(_475Test.expected, _475Test.actual);
    }

    @Test
    public void test3() {
        _475Test.houses = new int[]{ 1 };
        _475Test.heaters = new int[]{ 1, 2, 3, 4 };
        _475Test.expected = 0;
        _475Test.actual = _475Test.test.findRadius(_475Test.houses, _475Test.heaters);
        Assert.assertEquals(_475Test.expected, _475Test.actual);
    }

    @Test
    public void test4() {
        _475Test.houses = new int[]{ 1, 2, 3, 5, 15 };
        _475Test.heaters = new int[]{ 2, 30 };
        _475Test.expected = 13;
        _475Test.actual = _475Test.test.findRadius(_475Test.houses, _475Test.heaters);
        Assert.assertEquals(_475Test.expected, _475Test.actual);
    }
}

