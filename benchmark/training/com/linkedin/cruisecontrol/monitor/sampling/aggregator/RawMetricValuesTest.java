/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.cruisecontrol.monitor.sampling.aggregator;


import Extrapolation.AVG_ADJACENT;
import Extrapolation.AVG_AVAILABLE;
import Extrapolation.FORCED_INSUFFICIENT;
import Extrapolation.NO_VALID_EXTRAPOLATION;
import com.linkedin.cruisecontrol.IntegerEntity;
import com.linkedin.cruisecontrol.metricdef.MetricDef;
import com.linkedin.cruisecontrol.monitor.sampling.MetricSample;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;


public class RawMetricValuesTest {
    private static final float EPSILON = 0.01F;

    private static final int NUM_WINDOWS = 5;

    private static final int NUM_WINDOWS_TO_KEEP = (RawMetricValuesTest.NUM_WINDOWS) + 1;

    private static final int MIN_SAMPLES_PER_WINDOW = 4;

    private MetricDef _metricDef;

    @Test
    public void testAddSampleToEvictedWindows() {
        RawMetricValues rawValues = new RawMetricValues(2, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
        rawValues.updateOldestWindowIndex(2);
        MetricSample<String, IntegerEntity> m1 = getMetricSample(10, 10, 10);
        rawValues.addSample(m1, 1, _metricDef);
        Assert.assertEquals(0, rawValues.numSamples());
    }

    @Test
    public void testAddSampleUpdateExtrapolation() {
        // Let the minSamplePerWindow to be MIN_SAMPLE_PER_WINDOW + 1 so all the windows needs extrapolation.
        RawMetricValues rawValues = new RawMetricValues(RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, ((RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW) + 1));
        // All the window index should be 2,3,4,5,6,7
        prepareWindowMissingAtIndex(rawValues, Arrays.asList(3, 5), 2);
        // now add sample to window 2 and 6 to make them valid without flaws.
        MetricSample<String, IntegerEntity> m = getMetricSample(10, 10, 10);
        rawValues.addSample(m, 2, _metricDef);
        rawValues.addSample(m, 6, _metricDef);
        Assert.assertTrue(rawValues.isValidAtWindowIndex(2));
        Assert.assertFalse(rawValues.isExtrapolatedAtWindowIndex(2));
        Assert.assertTrue(rawValues.isValidAtWindowIndex(6));
        Assert.assertFalse(rawValues.isExtrapolatedAtWindowIndex(6));
        // At this point window 3 and 5 should still be invalid
        Assert.assertFalse(rawValues.isValidAtWindowIndex(3));
        Assert.assertFalse(rawValues.isExtrapolatedAtWindowIndex(3));
        Assert.assertFalse(rawValues.isValidAtWindowIndex(5));
        Assert.assertFalse(rawValues.isExtrapolatedAtWindowIndex(5));
        // now adding sample to 4 should make 3 and 5 valid with flaw of ADJACENT_AVG
        rawValues.addSample(m, 4, _metricDef);
        Assert.assertTrue(rawValues.isValidAtWindowIndex(4));
        Assert.assertFalse(rawValues.isExtrapolatedAtWindowIndex(4));
        Assert.assertTrue(rawValues.isValidAtWindowIndex(3));
        Assert.assertTrue(rawValues.isExtrapolatedAtWindowIndex(3));
        Assert.assertTrue(rawValues.isValidAtWindowIndex(5));
        Assert.assertTrue(rawValues.isExtrapolatedAtWindowIndex(5));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddToWindowLargerThanCurrentWindow() {
        RawMetricValues rawValues = new RawMetricValues(RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
        rawValues.updateOldestWindowIndex(0);
        rawValues.addSample(getMetricSample(10, 10, 10), RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, _metricDef);
    }

    @Test
    public void testAggregateSingleWindow() {
        RawMetricValues rawValues = new RawMetricValues(2, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
        rawValues.updateOldestWindowIndex(0);
        MetricSample<String, IntegerEntity> m1 = getMetricSample(10, 10, 10);
        MetricSample<String, IntegerEntity> m2 = getMetricSample(6, 6, 6);
        MetricSample<String, IntegerEntity> m3 = getMetricSample(2, 12, 8);
        MetricSample<String, IntegerEntity> m4 = getMetricSample(18, 10, 2);
        // No sample
        ValuesAndExtrapolations valuesAndExtrapolations = aggregate(rawValues, new TreeSet(Collections.singleton(0L)));
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(0).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(1).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(2).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(NO_VALID_EXTRAPOLATION, valuesAndExtrapolations.extrapolations().get(0));
        // Add the first sample
        addSample(rawValues, m1, 0);
        valuesAndExtrapolations = aggregate(rawValues, new TreeSet(Collections.singleton(0L)));
        Assert.assertEquals(10, valuesAndExtrapolations.metricValues().valuesFor(0).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(10, valuesAndExtrapolations.metricValues().valuesFor(1).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(10, valuesAndExtrapolations.metricValues().valuesFor(2).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(FORCED_INSUFFICIENT, valuesAndExtrapolations.extrapolations().get(0));
        // Add the second sample
        addSample(rawValues, m2, 0);
        valuesAndExtrapolations = aggregate(rawValues, new TreeSet(Collections.singleton(0L)));
        Assert.assertEquals(8, valuesAndExtrapolations.metricValues().valuesFor(0).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(10, valuesAndExtrapolations.metricValues().valuesFor(1).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(6, valuesAndExtrapolations.metricValues().valuesFor(2).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(AVG_AVAILABLE, valuesAndExtrapolations.extrapolations().get(0));
        // Add the third sample
        addSample(rawValues, m3, 0);
        valuesAndExtrapolations = aggregate(rawValues, new TreeSet(Collections.singleton(0L)));
        Assert.assertEquals(6, valuesAndExtrapolations.metricValues().valuesFor(0).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(12, valuesAndExtrapolations.metricValues().valuesFor(1).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(8, valuesAndExtrapolations.metricValues().valuesFor(2).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(AVG_AVAILABLE, valuesAndExtrapolations.extrapolations().get(0));
        // Add the fourth sample
        addSample(rawValues, m4, 0);
        valuesAndExtrapolations = aggregate(rawValues, new TreeSet(Collections.singleton(0L)));
        Assert.assertEquals(9, valuesAndExtrapolations.metricValues().valuesFor(0).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(12, valuesAndExtrapolations.metricValues().valuesFor(1).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(2, valuesAndExtrapolations.metricValues().valuesFor(2).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.extrapolations().size());
    }

    @Test
    public void testAggregateMultipleWindows() {
        for (int i = 0; i < ((RawMetricValuesTest.NUM_WINDOWS) * 2); i++) {
            RawMetricValues rawValues = new RawMetricValues(RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
            rawValues.updateOldestWindowIndex(i);
            float[][] expected = populate(rawValues, i);
            ValuesAndExtrapolations valuesAndExtrapolations = aggregate(rawValues, allIndexes(i));
            assertAggregatedValues(valuesAndExtrapolations.metricValues(), expected, i);
        }
    }

    @Test
    public void testExtrapolationAdjacentAvgAtMiddle() {
        RawMetricValues rawValues = new RawMetricValues(RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
        prepareWindowMissingAtIndex(rawValues, 1);
        ValuesAndExtrapolations valuesAndExtrapolations = aggregate(rawValues, allIndexes(0));
        Assert.assertEquals(11.5, valuesAndExtrapolations.metricValues().valuesFor(0).get(1), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(13.0, valuesAndExtrapolations.metricValues().valuesFor(1).get(1), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(13.0, valuesAndExtrapolations.metricValues().valuesFor(2).get(1), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(AVG_ADJACENT, valuesAndExtrapolations.extrapolations().get(1));
    }

    @Test
    public void testExtrapolationAdjacentAvgAtLeftEdge() {
        RawMetricValues rawValues = new RawMetricValues(RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
        prepareWindowMissingAtIndex(rawValues, 0);
        ValuesAndExtrapolations valuesAndExtrapolations = aggregate(rawValues, allIndexes(0));
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(0).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(1).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(2).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(NO_VALID_EXTRAPOLATION, valuesAndExtrapolations.extrapolations().get(0));
    }

    @Test
    public void testExtrapolationAdjacentAvgAtRightEdge() {
        RawMetricValues rawValues = new RawMetricValues(RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
        prepareWindowMissingAtIndex(rawValues, RawMetricValuesTest.NUM_WINDOWS);
        rawValues.updateOldestWindowIndex(1);
        ValuesAndExtrapolations valuesAndExtrapolations = aggregate(rawValues, allIndexes(1));
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(0).get(((RawMetricValuesTest.NUM_WINDOWS) - 1)), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(1).get(((RawMetricValuesTest.NUM_WINDOWS) - 1)), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(2).get(((RawMetricValuesTest.NUM_WINDOWS) - 1)), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(NO_VALID_EXTRAPOLATION, valuesAndExtrapolations.extrapolations().get(((RawMetricValuesTest.NUM_WINDOWS) - 1)));
    }

    @Test
    public void testExtrapolationAdjacentAvgAtLeftEdgeWithWrapAround() {
        RawMetricValues rawValues = new RawMetricValues(RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
        prepareWindowMissingAtIndex(rawValues, 0);
        // When oldest window index is 0, position 0 is the first index and should have no extrapolation.
        ValuesAndExtrapolations valuesAndExtrapolations = rawValues.aggregate(allIndexes(0), _metricDef);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(0).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(1).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(2).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(NO_VALID_EXTRAPOLATION, valuesAndExtrapolations.extrapolations().get(0));
        // When oldest window index is 2, position 0 is the last index and should have no extrapolation.
        rawValues.updateOldestWindowIndex(2);
        valuesAndExtrapolations = rawValues.aggregate(allIndexes(2), _metricDef);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(0).get(((RawMetricValuesTest.NUM_WINDOWS) - 1)), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(1).get(((RawMetricValuesTest.NUM_WINDOWS) - 1)), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(2).get(((RawMetricValuesTest.NUM_WINDOWS) - 1)), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(NO_VALID_EXTRAPOLATION, valuesAndExtrapolations.extrapolations().get(((RawMetricValuesTest.NUM_WINDOWS) - 1)));
        // when the oldest window index is 3, position 0 is the 3rd index. There should be an extrapolation.
        rawValues.updateOldestWindowIndex(3);
        valuesAndExtrapolations = rawValues.aggregate(allIndexes(3), _metricDef);
        Assert.assertEquals(31.5, valuesAndExtrapolations.metricValues().valuesFor(0).get(3), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(33.0, valuesAndExtrapolations.metricValues().valuesFor(1).get(3), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(33.0, valuesAndExtrapolations.metricValues().valuesFor(2).get(3), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(AVG_ADJACENT, valuesAndExtrapolations.extrapolations().get(3));
    }

    @Test
    public void testExtrapolationAdjacentAvgAtRightEdgeWithWrapAround() {
        RawMetricValues rawValues = new RawMetricValues(RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
        prepareWindowMissingAtIndex(rawValues, RawMetricValuesTest.NUM_WINDOWS);
        // When oldest window index is 1, position NUM_WINDOWS is the last index and should have no extrapolation.
        rawValues.updateOldestWindowIndex(1);
        ValuesAndExtrapolations valuesAndExtrapolations = rawValues.aggregate(allIndexes(1), _metricDef);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(0).get(((RawMetricValuesTest.NUM_WINDOWS) - 1)), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(1).get(((RawMetricValuesTest.NUM_WINDOWS) - 1)), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(2).get(((RawMetricValuesTest.NUM_WINDOWS) - 1)), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(NO_VALID_EXTRAPOLATION, valuesAndExtrapolations.extrapolations().get(((RawMetricValuesTest.NUM_WINDOWS) - 1)));
        // When oldest window index is NUM_WINDOWS, position NUM_WINDOWS is the first index, it should have no extrapolation.
        rawValues.updateOldestWindowIndex(RawMetricValuesTest.NUM_WINDOWS);
        valuesAndExtrapolations = rawValues.aggregate(allIndexes(RawMetricValuesTest.NUM_WINDOWS), _metricDef);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(0).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(1).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(0, valuesAndExtrapolations.metricValues().valuesFor(2).get(0), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(NO_VALID_EXTRAPOLATION, valuesAndExtrapolations.extrapolations().get(0));
        // when the oldest window index is 3, position NUM_WINDOWS is the 3rd index. There should be an extrapolation.
        rawValues.updateOldestWindowIndex(3);
        valuesAndExtrapolations = rawValues.aggregate(allIndexes(3), _metricDef);
        Assert.assertEquals(21.5, valuesAndExtrapolations.metricValues().valuesFor(0).get(2), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(23.0, valuesAndExtrapolations.metricValues().valuesFor(1).get(2), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(23.0, valuesAndExtrapolations.metricValues().valuesFor(2).get(2), RawMetricValuesTest.EPSILON);
        Assert.assertEquals(1, valuesAndExtrapolations.extrapolations().size());
        Assert.assertEquals(AVG_ADJACENT, valuesAndExtrapolations.extrapolations().get(2));
    }

    @Test
    public void testAdjacentAvgAtEdgeWhenNewWindowRollsOut() {
        RawMetricValues rawValues = new RawMetricValues(RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
        prepareWindowMissingAtIndex(rawValues, ((RawMetricValuesTest.NUM_WINDOWS) - 1));
        Assert.assertFalse(rawValues.isValidAtWindowIndex(((RawMetricValuesTest.NUM_WINDOWS) - 1)));
        Assert.assertFalse(rawValues.isExtrapolatedAtWindowIndex(((RawMetricValuesTest.NUM_WINDOWS) - 1)));
        rawValues.updateOldestWindowIndex(1);
        Assert.assertTrue(rawValues.isValidAtWindowIndex(((RawMetricValuesTest.NUM_WINDOWS) - 1)));
        Assert.assertTrue(rawValues.isExtrapolatedAtWindowIndex(((RawMetricValuesTest.NUM_WINDOWS) - 1)));
    }

    @Test
    public void testAdjacentAvgAtEdgeWhenNewWindowRollsOutWithLargeLeap() {
        RawMetricValues rawValues = new RawMetricValues(RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
        prepareWindowMissingAtIndex(rawValues, ((RawMetricValuesTest.NUM_WINDOWS) - 1));
        Assert.assertFalse(rawValues.isValidAtWindowIndex(((RawMetricValuesTest.NUM_WINDOWS) - 1)));
        Assert.assertFalse(rawValues.isExtrapolatedAtWindowIndex(((RawMetricValuesTest.NUM_WINDOWS) - 1)));
        rawValues.updateOldestWindowIndex(((RawMetricValuesTest.NUM_WINDOWS) - 1));
        Assert.assertFalse(rawValues.isValidAtWindowIndex(((RawMetricValuesTest.NUM_WINDOWS) - 1)));
        Assert.assertFalse(rawValues.isExtrapolatedAtWindowIndex(((RawMetricValuesTest.NUM_WINDOWS) - 1)));
    }

    @Test
    public void testIsValid() {
        RawMetricValues rawValues = new RawMetricValues(RawMetricValuesTest.NUM_WINDOWS_TO_KEEP, RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW);
        rawValues.updateOldestWindowIndex(0);
        MetricSample<String, IntegerEntity> m = getMetricSample(10, 10, 10);
        for (int i = 0; i < (RawMetricValuesTest.NUM_WINDOWS_TO_KEEP); i++) {
            for (int j = 0; j < ((RawMetricValuesTest.MIN_SAMPLES_PER_WINDOW) - 1); j++) {
                addSample(rawValues, m, i);
            }
        }
        Assert.assertTrue(rawValues.isValid(5));
        Assert.assertFalse(rawValues.isValid(4));
        addSample(rawValues, m, 0);
        Assert.assertTrue(rawValues.isValid(4));
    }
}

