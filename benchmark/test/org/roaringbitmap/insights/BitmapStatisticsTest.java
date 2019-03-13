package org.roaringbitmap.insights;


import org.junit.Assert;
import org.junit.Test;

import static BitmapStatistics.empty;


public class BitmapStatisticsTest {
    @Test
    public void toStringWorks() {
        BitmapStatistics statistics = new BitmapStatistics(new BitmapStatistics.ArrayContainersStats(10, 50), 2, 1);
        String string = statistics.toString();
        Assert.assertTrue(string.contains(BitmapStatistics.class.getSimpleName()));
    }

    @Test
    public void statsForEmpty() {
        BitmapStatistics statistics = empty;
        double bitmapFraction = statistics.containerFraction(statistics.getBitmapContainerCount());
        Assert.assertTrue(Double.isNaN(bitmapFraction));
        long averageArraysCardinality = statistics.getArrayContainersStats().averageCardinality();
        Assert.assertEquals(Long.MAX_VALUE, averageArraysCardinality);
    }
}

