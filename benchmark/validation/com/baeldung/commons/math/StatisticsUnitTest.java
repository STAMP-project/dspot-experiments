package com.baeldung.commons.math;


import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Assert;
import org.junit.Test;


public class StatisticsUnitTest {
    private double[] values;

    private DescriptiveStatistics descriptiveStatistics;

    @Test
    public void whenDescriptiveStatisticsGetMean_thenCorrect() {
        Assert.assertEquals(2439.8181818181815, descriptiveStatistics.getMean(), 1.0E-7);
    }

    @Test
    public void whenDescriptiveStatisticsGetMedian_thenCorrect() {
        Assert.assertEquals(51, descriptiveStatistics.getPercentile(50), 1.0E-7);
    }

    @Test
    public void whenDescriptiveStatisticsGetStandardDeviation_thenCorrect() {
        Assert.assertEquals(6093.054649651221, descriptiveStatistics.getStandardDeviation(), 1.0E-7);
    }
}

