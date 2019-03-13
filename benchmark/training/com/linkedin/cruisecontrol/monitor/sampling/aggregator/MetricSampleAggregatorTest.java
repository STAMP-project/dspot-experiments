/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.cruisecontrol.monitor.sampling.aggregator;


import AggregationOptions.Granularity;
import com.linkedin.cruisecontrol.CruiseControlUnitTestUtils;
import com.linkedin.cruisecontrol.IntegerEntity;
import com.linkedin.cruisecontrol.exception.NotEnoughValidWindowsException;
import com.linkedin.cruisecontrol.metricdef.AggregationFunction;
import com.linkedin.cruisecontrol.metricdef.MetricDef;
import com.linkedin.cruisecontrol.metricdef.MetricInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link MetricSampleAggregator}
 */
public class MetricSampleAggregatorTest {
    private static final float EPSILON = 0.01F;

    private static final int NUM_WINDOWS = 20;

    private static final long WINDOW_MS = 1000L;

    private static final int MIN_SAMPLES_PER_WINDOW = 4;

    private static final String ENTITY_GROUP_1 = "g1";

    private static final String ENTITY_GROUP_2 = "g2";

    private static final IntegerEntity ENTITY1 = new IntegerEntity(MetricSampleAggregatorTest.ENTITY_GROUP_1, 1234);

    private static final IntegerEntity ENTITY2 = new IntegerEntity(MetricSampleAggregatorTest.ENTITY_GROUP_1, 5678);

    private static final IntegerEntity ENTITY3 = new IntegerEntity(MetricSampleAggregatorTest.ENTITY_GROUP_2, 1234);

    private final MetricDef _metricDef = CruiseControlUnitTestUtils.getMetricDef();

    @Test
    public void testAddSampleInDifferentWindows() throws NotEnoughValidWindowsException {
        MetricSampleAggregator<String, IntegerEntity> aggregator = new MetricSampleAggregator(MetricSampleAggregatorTest.NUM_WINDOWS, MetricSampleAggregatorTest.WINDOW_MS, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, 0, _metricDef);
        // The remaining windows should NUM_WINDOWS - 2 to 2 * NUM_WINDOWS - 3;
        populateSampleAggregator(((2 * (MetricSampleAggregatorTest.NUM_WINDOWS)) - 1), MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, aggregator);
        AggregationOptions<String, IntegerEntity> options = new AggregationOptions(1, 1, MetricSampleAggregatorTest.NUM_WINDOWS, 5, Collections.emptySet(), Granularity.ENTITY_GROUP, true);
        MetricSampleAggregationResult<String, IntegerEntity> aggResults = aggregator.aggregate((-1), Long.MAX_VALUE, options);
        Assert.assertNotNull(aggResults);
        Assert.assertEquals(1, aggResults.valuesAndExtrapolations().size());
        for (Map.Entry<IntegerEntity, ValuesAndExtrapolations> entry : aggResults.valuesAndExtrapolations().entrySet()) {
            ValuesAndExtrapolations valuesAndExtrapolations = entry.getValue();
            List<Long> windows = valuesAndExtrapolations.windows();
            Assert.assertEquals(MetricSampleAggregatorTest.NUM_WINDOWS, windows.size());
            for (int i = 0; i < (MetricSampleAggregatorTest.NUM_WINDOWS); i++) {
                Assert.assertEquals(((((2 * (MetricSampleAggregatorTest.NUM_WINDOWS)) - 2) - i) * (MetricSampleAggregatorTest.WINDOW_MS)), windows.get(i).longValue());
            }
            for (MetricInfo info : _metricDef.all()) {
                MetricValues valuesForMetric = valuesAndExtrapolations.metricValues().valuesFor(info.id());
                for (int i = 0; i < (MetricSampleAggregatorTest.NUM_WINDOWS); i++) {
                    double expectedValue;
                    if (((info.aggregationFunction()) == (AggregationFunction.LATEST)) || ((info.aggregationFunction()) == (AggregationFunction.MAX))) {
                        expectedValue = (((((2 * (MetricSampleAggregatorTest.NUM_WINDOWS)) - 3) - i) * 10) + (MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW)) - 1;
                    } else {
                        expectedValue = ((((2 * (MetricSampleAggregatorTest.NUM_WINDOWS)) - 3) - i) * 10) + (((MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW) - 1) / 2.0);
                    }
                    Assert.assertEquals(((("The utilization for " + (info.name())) + " should be ") + expectedValue), expectedValue, valuesForMetric.get((i % (MetricSampleAggregatorTest.NUM_WINDOWS))), 0);
                }
            }
        }
        Assert.assertEquals(((MetricSampleAggregatorTest.NUM_WINDOWS) + 1), aggregator.allWindows().size());
        Assert.assertEquals(MetricSampleAggregatorTest.NUM_WINDOWS, aggregator.numAvailableWindows());
    }

    @Test
    public void testGeneration() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = new MetricSampleAggregator(MetricSampleAggregatorTest.NUM_WINDOWS, MetricSampleAggregatorTest.WINDOW_MS, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, 0, _metricDef);
        CruiseControlUnitTestUtils.populateSampleAggregator(((MetricSampleAggregatorTest.NUM_WINDOWS) + 1), MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, aggregator, MetricSampleAggregatorTest.ENTITY1, 0, MetricSampleAggregatorTest.WINDOW_MS, _metricDef);
        Assert.assertEquals(((MetricSampleAggregatorTest.NUM_WINDOWS) + 1), aggregator.generation().intValue());
        AggregationOptions<String, IntegerEntity> options = new AggregationOptions(1, 1, MetricSampleAggregatorTest.NUM_WINDOWS, 5, Collections.emptySet(), Granularity.ENTITY_GROUP, true);
        MetricSampleAggregatorState<String, IntegerEntity> windowState = aggregator.aggregatorState();
        for (int i = 1; i < ((MetricSampleAggregatorTest.NUM_WINDOWS) + 1); i++) {
            Assert.assertEquals(((MetricSampleAggregatorTest.NUM_WINDOWS) + 1), windowState.windowStates().get(((long) (i))).generation().intValue());
        }
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 1, aggregator, MetricSampleAggregatorTest.ENTITY2, 1, MetricSampleAggregatorTest.WINDOW_MS, _metricDef);
        aggregator.completeness((-1), Long.MAX_VALUE, options);
        Assert.assertEquals(((MetricSampleAggregatorTest.NUM_WINDOWS) + 2), windowState.windowStates().get(((long) (2))).generation().intValue());
        long initGeneration = aggregator.generation();
        // Ensure that generation is not bumped up for group retains that remove no elements.
        aggregator.retainEntityGroup(Collections.singleton(MetricSampleAggregatorTest.ENTITY_GROUP_1));
        Assert.assertEquals(initGeneration, aggregator.generation().longValue());
        // Ensure that generation is not bumped up for group removes that remove no elements.
        aggregator.removeEntityGroup(Collections.emptySet());
        Assert.assertEquals(initGeneration, aggregator.generation().longValue());
        // Ensure that generation is not bumped up for entity removes that remove no elements.
        aggregator.removeEntities(Collections.emptySet());
        Assert.assertEquals(initGeneration, aggregator.generation().longValue());
        // Ensure that generation is not bumped up for entity retains that remove no elements.
        aggregator.retainEntities(new HashSet(Arrays.asList(MetricSampleAggregatorTest.ENTITY1, MetricSampleAggregatorTest.ENTITY2)));
        Assert.assertEquals(initGeneration, aggregator.generation().longValue());
        // Ensure that generation is bumped up for retains that remove elements.
        aggregator.retainEntityGroup(Collections.emptySet());
        Assert.assertEquals((initGeneration + 1), aggregator.generation().longValue());
    }

    @Test
    public void testEarliestWindow() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = new MetricSampleAggregator(MetricSampleAggregatorTest.NUM_WINDOWS, MetricSampleAggregatorTest.WINDOW_MS, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, 0, _metricDef);
        Assert.assertNull(aggregator.earliestWindow());
        CruiseControlUnitTestUtils.populateSampleAggregator(MetricSampleAggregatorTest.NUM_WINDOWS, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, aggregator, MetricSampleAggregatorTest.ENTITY1, 0, MetricSampleAggregatorTest.WINDOW_MS, _metricDef);
        Assert.assertEquals(MetricSampleAggregatorTest.WINDOW_MS, aggregator.earliestWindow().longValue());
        CruiseControlUnitTestUtils.populateSampleAggregator(2, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, aggregator, MetricSampleAggregatorTest.ENTITY1, MetricSampleAggregatorTest.NUM_WINDOWS, MetricSampleAggregatorTest.WINDOW_MS, _metricDef);
        Assert.assertEquals((2 * (MetricSampleAggregatorTest.WINDOW_MS)), aggregator.earliestWindow().longValue());
    }

    @Test
    public void testAllWindows() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = new MetricSampleAggregator(MetricSampleAggregatorTest.NUM_WINDOWS, MetricSampleAggregatorTest.WINDOW_MS, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, 0, _metricDef);
        Assert.assertTrue(aggregator.allWindows().isEmpty());
        CruiseControlUnitTestUtils.populateSampleAggregator(((MetricSampleAggregatorTest.NUM_WINDOWS) + 1), MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, aggregator, MetricSampleAggregatorTest.ENTITY1, 0, MetricSampleAggregatorTest.WINDOW_MS, _metricDef);
        List<Long> allStWindows = aggregator.allWindows();
        Assert.assertEquals(((MetricSampleAggregatorTest.NUM_WINDOWS) + 1), allStWindows.size());
        for (int i = 0; i < ((MetricSampleAggregatorTest.NUM_WINDOWS) + 1); i++) {
            Assert.assertEquals(((i + 1) * (MetricSampleAggregatorTest.WINDOW_MS)), allStWindows.get(i).longValue());
        }
    }

    @Test
    public void testAvailableWindows() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = new MetricSampleAggregator(MetricSampleAggregatorTest.NUM_WINDOWS, MetricSampleAggregatorTest.WINDOW_MS, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, 0, _metricDef);
        Assert.assertTrue(aggregator.availableWindows().isEmpty());
        CruiseControlUnitTestUtils.populateSampleAggregator(1, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, aggregator, MetricSampleAggregatorTest.ENTITY1, 0, MetricSampleAggregatorTest.WINDOW_MS, _metricDef);
        Assert.assertTrue(aggregator.availableWindows().isEmpty());
        CruiseControlUnitTestUtils.populateSampleAggregator(((MetricSampleAggregatorTest.NUM_WINDOWS) - 2), MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, aggregator, MetricSampleAggregatorTest.ENTITY1, 1, MetricSampleAggregatorTest.WINDOW_MS, _metricDef);
        List<Long> availableWindows = aggregator.availableWindows();
        Assert.assertEquals(((MetricSampleAggregatorTest.NUM_WINDOWS) - 2), availableWindows.size());
        for (int i = 0; i < ((MetricSampleAggregatorTest.NUM_WINDOWS) - 2); i++) {
            Assert.assertEquals(((i + 1) * (MetricSampleAggregatorTest.WINDOW_MS)), availableWindows.get(i).longValue());
        }
    }

    @Test
    public void testAddSamplesWithLargeInterval() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = new MetricSampleAggregator(MetricSampleAggregatorTest.NUM_WINDOWS, MetricSampleAggregatorTest.WINDOW_MS, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, 0, _metricDef);
        // Populate samples for time window indexed from 0 to NUM_WINDOWS to aggregator.
        CruiseControlUnitTestUtils.populateSampleAggregator(((MetricSampleAggregatorTest.NUM_WINDOWS) + 1), MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, aggregator, MetricSampleAggregatorTest.ENTITY1, 0, MetricSampleAggregatorTest.WINDOW_MS, _metricDef);
        // Populate samples for time window index from 4 * NUM_WINDOWS to 5 * NUM_WINDOWS - 1 to aggregator.
        CruiseControlUnitTestUtils.populateSampleAggregator(MetricSampleAggregatorTest.NUM_WINDOWS, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, aggregator, MetricSampleAggregatorTest.ENTITY1, (4 * (MetricSampleAggregatorTest.NUM_WINDOWS)), MetricSampleAggregatorTest.WINDOW_MS, _metricDef);
        // If aggregator rolls out time window properly, time window indexed from 4 * NUM_WINDOW -1 to 5 * NUM_WINDOW -1 are
        // currently in memory and time window indexed from 4 * NUM_WINDOW -1 to  5 * NUM_WINDOW - 2 should be returned from query.
        List<Long> availableWindows = aggregator.availableWindows();
        Assert.assertEquals(MetricSampleAggregatorTest.NUM_WINDOWS, availableWindows.size());
        for (int i = 0; i < (MetricSampleAggregatorTest.NUM_WINDOWS); i++) {
            Assert.assertEquals(((i + (4 * (MetricSampleAggregatorTest.NUM_WINDOWS))) * (MetricSampleAggregatorTest.WINDOW_MS)), availableWindows.get(i).longValue());
        }
    }

    @Test
    public void testAggregationOption1() throws NotEnoughValidWindowsException {
        MetricSampleAggregator<String, IntegerEntity> aggregator = prepareCompletenessTestEnv();
        // Let the group coverage to be 1
        AggregationOptions<String, IntegerEntity> options = new AggregationOptions(0.5, 1, MetricSampleAggregatorTest.NUM_WINDOWS, 5, new HashSet(Arrays.asList(MetricSampleAggregatorTest.ENTITY1, MetricSampleAggregatorTest.ENTITY2, MetricSampleAggregatorTest.ENTITY3)), Granularity.ENTITY, true);
        MetricSampleCompleteness<String, IntegerEntity> completeness = aggregator.completeness((-1), Long.MAX_VALUE, options);
        Assert.assertTrue(completeness.validWindowIndexes().isEmpty());
        Assert.assertTrue(completeness.validEntities().isEmpty());
        Assert.assertTrue(completeness.validEntityGroups().isEmpty());
        assertCompletenessByWindowIndex(completeness);
    }

    @Test
    public void testAggregationOption2() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = prepareCompletenessTestEnv();
        // Change the group coverage requirement to 0, window 3, 4, 20 will be excluded because minValidEntityRatio is not met.
        AggregationOptions<String, IntegerEntity> options = new AggregationOptions(0.5, 0.0, MetricSampleAggregatorTest.NUM_WINDOWS, 5, new HashSet(Arrays.asList(MetricSampleAggregatorTest.ENTITY1, MetricSampleAggregatorTest.ENTITY2, MetricSampleAggregatorTest.ENTITY3)), Granularity.ENTITY, true);
        MetricSampleCompleteness<String, IntegerEntity> completeness = aggregator.completeness((-1), Long.MAX_VALUE, options);
        Assert.assertEquals(17, completeness.validWindowIndexes().size());
        Assert.assertFalse(completeness.validWindowIndexes().contains(3L));
        Assert.assertFalse(completeness.validWindowIndexes().contains(4L));
        Assert.assertFalse(completeness.validWindowIndexes().contains(20L));
        Assert.assertEquals(2, completeness.validEntities().size());
        Assert.assertTrue(completeness.validEntities().contains(MetricSampleAggregatorTest.ENTITY1));
        Assert.assertTrue(completeness.validEntities().contains(MetricSampleAggregatorTest.ENTITY3));
        Assert.assertEquals(1, completeness.validEntityGroups().size());
        Assert.assertTrue(completeness.validEntityGroups().contains(MetricSampleAggregatorTest.ENTITY3.group()));
        assertCompletenessByWindowIndex(completeness);
    }

    @Test
    public void testAggregationOption3() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = prepareCompletenessTestEnv();
        // Change the option to have 0.5 as minValidEntityGroupRatio. This will exclude window index 3, 4, 20.
        AggregationOptions<String, IntegerEntity> options = new AggregationOptions(0.0, 0.5, MetricSampleAggregatorTest.NUM_WINDOWS, 5, new HashSet(Arrays.asList(MetricSampleAggregatorTest.ENTITY1, MetricSampleAggregatorTest.ENTITY2, MetricSampleAggregatorTest.ENTITY3)), Granularity.ENTITY, true);
        MetricSampleCompleteness<String, IntegerEntity> completeness = aggregator.completeness((-1), Long.MAX_VALUE, options);
        Assert.assertEquals(17, completeness.validWindowIndexes().size());
        Assert.assertFalse(completeness.validWindowIndexes().contains(3L));
        Assert.assertFalse(completeness.validWindowIndexes().contains(4L));
        Assert.assertFalse(completeness.validWindowIndexes().contains(20L));
        Assert.assertEquals(2, completeness.validEntities().size());
        Assert.assertTrue(completeness.validEntities().contains(MetricSampleAggregatorTest.ENTITY1));
        Assert.assertTrue(completeness.validEntities().contains(MetricSampleAggregatorTest.ENTITY3));
        Assert.assertEquals(1, completeness.validEntityGroups().size());
        Assert.assertTrue(completeness.validEntityGroups().contains(MetricSampleAggregatorTest.ENTITY3.group()));
        assertCompletenessByWindowIndex(completeness);
    }

    @Test
    public void testAggregationOption4() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = prepareCompletenessTestEnv();
        // Change the option to have 0.5 as minValidEntityGroupRatio. This will exclude window index 3, 4, 20.
        AggregationOptions<String, IntegerEntity> options = new AggregationOptions(0.0, 0.0, MetricSampleAggregatorTest.NUM_WINDOWS, 5, new HashSet(Arrays.asList(MetricSampleAggregatorTest.ENTITY1, MetricSampleAggregatorTest.ENTITY2, MetricSampleAggregatorTest.ENTITY3)), Granularity.ENTITY, true);
        MetricSampleCompleteness<String, IntegerEntity> completeness = aggregator.completeness((-1), Long.MAX_VALUE, options);
        Assert.assertEquals(17, completeness.validWindowIndexes().size());
        Assert.assertEquals(2, completeness.validEntities().size());
        Assert.assertTrue(completeness.validEntities().contains(MetricSampleAggregatorTest.ENTITY1));
        Assert.assertTrue(completeness.validEntities().contains(MetricSampleAggregatorTest.ENTITY3));
        Assert.assertTrue(completeness.validEntityGroups().contains(MetricSampleAggregatorTest.ENTITY3.group()));
        assertCompletenessByWindowIndex(completeness);
    }

    @Test
    public void testAggregationOption5() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = prepareCompletenessTestEnv();
        // Change the option to use entity group granularity. In this case ENTITY1 will not be considered as valid entity
        // so there will be no valid windows.
        AggregationOptions<String, IntegerEntity> options = new AggregationOptions(0.5, 0.0, MetricSampleAggregatorTest.NUM_WINDOWS, 5, new HashSet(Arrays.asList(MetricSampleAggregatorTest.ENTITY1, MetricSampleAggregatorTest.ENTITY2, MetricSampleAggregatorTest.ENTITY3)), Granularity.ENTITY_GROUP, true);
        MetricSampleCompleteness<String, IntegerEntity> completeness = aggregator.completeness((-1), Long.MAX_VALUE, options);
        Assert.assertTrue(completeness.validWindowIndexes().isEmpty());
        Assert.assertTrue(completeness.validEntities().isEmpty());
        Assert.assertTrue(completeness.validEntityGroups().isEmpty());
        assertCompletenessByWindowIndex(completeness);
    }

    @Test
    public void testAggregationOption6() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = prepareCompletenessTestEnv();
        // Change the option to use entity group granularity and reduce the minValidEntityRatio to 0.3. This will
        // include ENTITY3 except in window 3, 4, 20.
        AggregationOptions<String, IntegerEntity> options = new AggregationOptions(0.3, 0.0, MetricSampleAggregatorTest.NUM_WINDOWS, 5, new HashSet(Arrays.asList(MetricSampleAggregatorTest.ENTITY1, MetricSampleAggregatorTest.ENTITY2, MetricSampleAggregatorTest.ENTITY3)), Granularity.ENTITY_GROUP, true);
        MetricSampleCompleteness<String, IntegerEntity> completeness = aggregator.completeness((-1), Long.MAX_VALUE, options);
        Assert.assertEquals(17, completeness.validWindowIndexes().size());
        Assert.assertFalse(completeness.validWindowIndexes().contains(3L));
        Assert.assertFalse(completeness.validWindowIndexes().contains(4L));
        Assert.assertFalse(completeness.validWindowIndexes().contains(20L));
        Assert.assertEquals(1, completeness.validEntities().size());
        Assert.assertTrue(completeness.validEntities().contains(MetricSampleAggregatorTest.ENTITY3));
        Assert.assertEquals(1, completeness.validEntityGroups().size());
        Assert.assertTrue(completeness.validEntityGroups().contains(MetricSampleAggregatorTest.ENTITY3.group()));
        assertCompletenessByWindowIndex(completeness);
    }

    @Test
    public void testAggregationOption7() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = prepareCompletenessTestEnv();
        // Require 0.3 min valid entity ratio, 0 max allowed extrapolations, entity aggregation granularity.
        // The result should exclude window 3, 4 and 20 due to insufficient valid ratio, and missing window 11 because
        // the ENTITY1 has extrapolations in that window.
        AggregationOptions<String, IntegerEntity> options = new AggregationOptions(0.5, 0.0, MetricSampleAggregatorTest.NUM_WINDOWS, 1, new HashSet(Arrays.asList(MetricSampleAggregatorTest.ENTITY1, MetricSampleAggregatorTest.ENTITY2, MetricSampleAggregatorTest.ENTITY3)), Granularity.ENTITY, true);
        MetricSampleCompleteness<String, IntegerEntity> completeness = aggregator.completeness((-1), Long.MAX_VALUE, options);
        Assert.assertEquals(16, completeness.validWindowIndexes().size());
        Assert.assertFalse(completeness.validWindowIndexes().contains(3L));
        Assert.assertFalse(completeness.validWindowIndexes().contains(4L));
        Assert.assertFalse(completeness.validWindowIndexes().contains(20L));
        Assert.assertFalse(completeness.validWindowIndexes().contains(11L));
        Assert.assertEquals(2, completeness.validEntities().size());
        Assert.assertTrue(completeness.validEntities().contains(MetricSampleAggregatorTest.ENTITY1));
        Assert.assertTrue(completeness.validEntities().contains(MetricSampleAggregatorTest.ENTITY3));
        // Need to skip window 11L because extrapolation is not allowed here.
        assertCompletenessByWindowIndex(completeness, Collections.singleton(11L));
        Assert.assertEquals((1.0F / 3), completeness.extrapolatedEntitiesByWindowIndex().get(11L).doubleValue(), MetricSampleAggregatorTest.EPSILON);
        Assert.assertEquals((1.0F / 3), completeness.validEntityRatioByWindowIndex().get(11L), MetricSampleAggregatorTest.EPSILON);
        Assert.assertEquals((1.0F / 3), completeness.validEntityRatioWithGroupGranularityByWindowIndex().get(11L), MetricSampleAggregatorTest.EPSILON);
        Assert.assertEquals(0.5, completeness.validEntityGroupRatioByWindowIndex().get(11L), MetricSampleAggregatorTest.EPSILON);
    }

    @Test
    public void testPeekCurrentWindow() {
        MetricSampleAggregator<String, IntegerEntity> aggregator = new MetricSampleAggregator(MetricSampleAggregatorTest.NUM_WINDOWS, MetricSampleAggregatorTest.WINDOW_MS, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, 0, _metricDef);
        // Add samples to three entities.
        // Entity1 has 2 windows with insufficient data.
        // Entity2 has 2 windows with sufficient data.
        // Entity3 has 1 window with sufficient data, i.e. the active window does not have data.
        populateSampleAggregator(2, 1, aggregator, MetricSampleAggregatorTest.ENTITY1);
        populateSampleAggregator(2, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, aggregator, MetricSampleAggregatorTest.ENTITY2);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, MetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, aggregator, MetricSampleAggregatorTest.ENTITY3, 0, MetricSampleAggregatorTest.WINDOW_MS, _metricDef);
        Map<IntegerEntity, ValuesAndExtrapolations> currentWindowMetrics = aggregator.peekCurrentWindow();
        Assert.assertEquals(Extrapolation.FORCED_INSUFFICIENT, currentWindowMetrics.get(MetricSampleAggregatorTest.ENTITY1).extrapolations().get(0));
        Assert.assertTrue(currentWindowMetrics.get(MetricSampleAggregatorTest.ENTITY2).extrapolations().isEmpty());
        Assert.assertEquals(Extrapolation.NO_VALID_EXTRAPOLATION, currentWindowMetrics.get(MetricSampleAggregatorTest.ENTITY3).extrapolations().get(0));
    }

    @Test
    public void testConcurrency() throws NotEnoughValidWindowsException {
        final int numThreads = 10;
        final int numEntities = 5;
        final int samplesPerWindow = 100;
        final int numRandomEntities = 10;
        // We set the minimum number of samples per window to be the total number of samples to insert.
        // So when there is a sample got lost we will fail to collect enough window.
        final MetricSampleAggregator<String, IntegerEntity> aggregator = new MetricSampleAggregator(MetricSampleAggregatorTest.NUM_WINDOWS, MetricSampleAggregatorTest.WINDOW_MS, ((samplesPerWindow * numThreads) * (numRandomEntities / numEntities)), 0, _metricDef);
        final Random random = new Random();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    // Add samples for 10 random partitions.
                    int startingEntity = (random.nextInt(5)) % numEntities;
                    for (int i = 0; i < numRandomEntities; i++) {
                        IntegerEntity entity = new IntegerEntity("group", ((startingEntity + i) % numEntities));
                        populateSampleAggregator(((2 * (MetricSampleAggregatorTest.NUM_WINDOWS)) + 1), samplesPerWindow, aggregator, entity);
                    }
                }
            };
            threads.add(t);
        }
        threads.forEach(Thread::start);
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                // let it go.
            }
        }
        Assert.assertEquals((((((MetricSampleAggregatorTest.NUM_WINDOWS) + 1) * samplesPerWindow) * numRandomEntities) * numThreads), aggregator.numSamples());
        AggregationOptions<String, IntegerEntity> options = new AggregationOptions(1, 1, MetricSampleAggregatorTest.NUM_WINDOWS, 5, Collections.emptySet(), Granularity.ENTITY_GROUP, true);
        MetricSampleAggregationResult<String, IntegerEntity> aggResult = aggregator.aggregate((-1), Long.MAX_VALUE, options);
        Assert.assertEquals(numEntities, aggResult.valuesAndExtrapolations().size());
        Assert.assertTrue(aggResult.invalidEntities().isEmpty());
        for (ValuesAndExtrapolations valuesAndExtrapolations : aggResult.valuesAndExtrapolations().values()) {
            Assert.assertEquals(MetricSampleAggregatorTest.NUM_WINDOWS, valuesAndExtrapolations.windows().size());
            Assert.assertTrue(valuesAndExtrapolations.extrapolations().isEmpty());
        }
        MetricSampleCompleteness<String, IntegerEntity> completeness = aggregator.completeness((-1), Long.MAX_VALUE, options);
        for (double validPartitionRatio : completeness.validEntityRatioByWindowIndex().values()) {
            Assert.assertEquals(1.0, validPartitionRatio, MetricSampleAggregatorTest.EPSILON);
        }
    }
}

