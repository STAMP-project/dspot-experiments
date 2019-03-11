package com.fishercoder;


import _57.Solution1;
import com.fishercoder.common.classes.Interval;
import com.fishercoder.common.utils.CommonUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class _57Test {
    private static Solution1 solution1;

    private static List<Interval> intervals;

    private static List<Interval> expected;

    private static List<Interval> actual;

    @Test
    public void test1() {
        _57Test.intervals = new ArrayList(Arrays.asList(new Interval(1, 3), new Interval(6, 9)));
        _57Test.expected = new ArrayList(Arrays.asList(new Interval(1, 5), new Interval(6, 9)));
        CommonUtils.printIntervals(_57Test.intervals);
        _57Test.actual = _57Test.solution1.insert(_57Test.intervals, new Interval(2, 5));
        CommonUtils.printIntervals(_57Test.actual);
        Assert.assertEquals(_57Test.expected, _57Test.actual);
    }

    @Test
    public void test2() {
        _57Test.intervals = new ArrayList(Arrays.asList(new Interval(1, 2), new Interval(3, 5), new Interval(6, 7), new Interval(8, 10), new Interval(12, 16)));
        CommonUtils.printIntervals(_57Test.intervals);
        _57Test.expected = new ArrayList(Arrays.asList(new Interval(1, 2), new Interval(3, 10), new Interval(12, 16)));
        _57Test.actual = _57Test.solution1.insert(_57Test.intervals, new Interval(4, 9));
        CommonUtils.printIntervals(_57Test.actual);
        Assert.assertEquals(_57Test.expected, _57Test.actual);
    }

    @Test
    public void test3() {
        _57Test.intervals = new ArrayList(Arrays.asList(new Interval(1, 5)));
        CommonUtils.printIntervals(_57Test.intervals);
        _57Test.expected = new ArrayList(Arrays.asList(new Interval(1, 5)));
        _57Test.actual = _57Test.solution1.insert(_57Test.intervals, new Interval(2, 3));
        CommonUtils.printIntervals(_57Test.actual);
        Assert.assertEquals(_57Test.expected, _57Test.actual);
    }

    @Test
    public void test4() {
        _57Test.intervals = new ArrayList(Arrays.asList());
        CommonUtils.printIntervals(_57Test.intervals);
        _57Test.expected = new ArrayList(Arrays.asList(new Interval(5, 7)));
        _57Test.actual = _57Test.solution1.insert(_57Test.intervals, new Interval(5, 7));
        CommonUtils.printIntervals(_57Test.actual);
        Assert.assertEquals(_57Test.expected, _57Test.actual);
    }

    @Test
    public void test5() {
        _57Test.intervals = new ArrayList(Arrays.asList(new Interval(1, 5)));
        _57Test.expected = new ArrayList(Arrays.asList(new Interval(1, 5), new Interval(6, 8)));
        CommonUtils.printIntervals(_57Test.intervals);
        _57Test.actual = _57Test.solution1.insert(_57Test.intervals, new Interval(6, 8));
        CommonUtils.printIntervals(_57Test.actual);
        Assert.assertEquals(_57Test.expected, _57Test.actual);
    }

    @Test
    public void test6() {
        _57Test.intervals = new ArrayList(Arrays.asList(new Interval(1, 5)));
        _57Test.expected = new ArrayList(Arrays.asList(new Interval(0, 5)));
        CommonUtils.printIntervals(_57Test.intervals);
        _57Test.actual = _57Test.solution1.insert(_57Test.intervals, new Interval(0, 3));
        CommonUtils.printIntervals(_57Test.actual);
        Assert.assertEquals(_57Test.expected, _57Test.actual);
    }

    @Test
    public void test7() {
        _57Test.intervals = new ArrayList(Arrays.asList(new Interval(1, 5)));
        _57Test.expected = new ArrayList(Arrays.asList(new Interval(0, 0), new Interval(1, 5)));
        CommonUtils.printIntervals(_57Test.intervals);
        _57Test.actual = _57Test.solution1.insert(_57Test.intervals, new Interval(0, 0));
        CommonUtils.printIntervals(_57Test.actual);
        Assert.assertEquals(_57Test.expected, _57Test.actual);
    }

    @Test
    public void test8() {
        _57Test.intervals = new ArrayList(Arrays.asList(new Interval(2, 5), new Interval(6, 7), new Interval(8, 9)));
        _57Test.expected = new ArrayList(Arrays.asList(new Interval(0, 1), new Interval(2, 5), new Interval(6, 7), new Interval(8, 9)));
        CommonUtils.printIntervals(_57Test.intervals);
        _57Test.actual = _57Test.solution1.insert(_57Test.intervals, new Interval(0, 1));
        CommonUtils.printIntervals(_57Test.actual);
        Assert.assertEquals(_57Test.expected, _57Test.actual);
    }

    @Test
    public void test9() {
        _57Test.intervals = new ArrayList(Arrays.asList(new Interval(2, 4), new Interval(5, 7), new Interval(8, 10), new Interval(11, 13)));
        _57Test.expected = new ArrayList(Arrays.asList(new Interval(2, 7), new Interval(8, 10), new Interval(11, 13)));
        CommonUtils.printIntervals(_57Test.intervals);
        _57Test.actual = _57Test.solution1.insert(_57Test.intervals, new Interval(3, 6));
        CommonUtils.printIntervals(_57Test.actual);
        Assert.assertEquals(_57Test.expected, _57Test.actual);
    }
}

