package com.fishercoder;


import _149.Solution1;
import com.fishercoder.common.classes.Point;
import org.junit.Assert;
import org.junit.Test;


public class _149Test {
    private static Solution1 solution1;

    private static Point[] points;

    @Test
    public void test1() {
        _149Test.points = new Point[]{ new Point(0, 0), new Point(1, 65536), new Point(65536, 0) };
        Assert.assertEquals(2, _149Test.solution1.maxPoints(_149Test.points));
    }
}

