package com.fishercoder;


import _56.Solution1;
import com.fishercoder.common.classes.Interval;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _56Test {
    private static Solution1 solution1;

    private static List<Interval> intervals;

    private static List<Interval> expected;

    @Test
    public void test1() {
        _56Test.intervals = new ArrayList();
        _56Test.intervals.add(new Interval(2, 3));
        _56Test.intervals.add(new Interval(5, 5));
        _56Test.intervals.add(new Interval(2, 2));
        _56Test.intervals.add(new Interval(3, 4));
        _56Test.intervals.add(new Interval(3, 4));
        _56Test.expected = new ArrayList();
        _56Test.expected.add(new Interval(2, 4));
        _56Test.expected.add(new Interval(5, 5));
        Assert.assertEquals(_56Test.expected, _56Test.solution1.merge(_56Test.intervals));
    }

    @Test
    public void test2() {
        _56Test.intervals = new ArrayList();
        _56Test.intervals.add(new Interval(1, 3));
        _56Test.intervals.add(new Interval(2, 6));
        _56Test.intervals.add(new Interval(8, 10));
        _56Test.intervals.add(new Interval(15, 18));
        _56Test.expected = new ArrayList();
        _56Test.expected.add(new Interval(1, 6));
        _56Test.expected.add(new Interval(8, 10));
        _56Test.expected.add(new Interval(15, 18));
        Assert.assertEquals(_56Test.expected, _56Test.solution1.merge(_56Test.intervals));
    }
}

