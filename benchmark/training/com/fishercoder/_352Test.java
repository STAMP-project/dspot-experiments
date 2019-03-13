package com.fishercoder;


import _352.Solution1.SummaryRanges;
import com.fishercoder.common.classes.Interval;
import com.fishercoder.common.utils.CommonUtils;
import java.util.List;
import org.junit.Test;


public class _352Test {
    private static SummaryRanges test;

    private static List<Interval> actual;

    @Test
    public void test1() {
        _352Test.test.addNum(1);
        _352Test.actual = _352Test.test.getIntervals();
        CommonUtils.printIntervals(_352Test.actual);
        _352Test.test.addNum(3);
        _352Test.actual = _352Test.test.getIntervals();
        CommonUtils.printIntervals(_352Test.actual);
        _352Test.test.addNum(7);
        _352Test.actual = _352Test.test.getIntervals();
        CommonUtils.printIntervals(_352Test.actual);
        _352Test.test.addNum(2);
        _352Test.actual = _352Test.test.getIntervals();
        CommonUtils.printIntervals(_352Test.actual);
        _352Test.test.addNum(6);
        _352Test.actual = _352Test.test.getIntervals();
        CommonUtils.printIntervals(_352Test.actual);
    }
}

