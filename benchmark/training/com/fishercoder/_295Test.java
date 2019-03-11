package com.fishercoder;


import _295.Solution1.MedianFinder;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by fishercoder on 5/27/17.
 */
public class _295Test {
    private static MedianFinder solution1;

    @Test
    public void test1() {
        _295Test.solution1.addNum(1);
        _295Test.solution1.addNum(3);
        _295Test.solution1.addNum((-1));
        Assert.assertEquals(1.0, _295Test.solution1.findMedian(), 0);
    }
}

