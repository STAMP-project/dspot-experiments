/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.monitor.sampling.aggregator;


import Extrapolation.AVG_ADJACENT;
import Extrapolation.AVG_AVAILABLE;
import Extrapolation.NO_VALID_EXTRAPOLATION;
import MetadataClient.ClusterAndGeneration;
import com.linkedin.cruisecontrol.CruiseControlUnitTestUtils;
import com.linkedin.cruisecontrol.exception.NotEnoughValidWindowsException;
import com.linkedin.cruisecontrol.metricdef.MetricDef;
import com.linkedin.cruisecontrol.monitor.sampling.aggregator.Extrapolation;
import com.linkedin.cruisecontrol.monitor.sampling.aggregator.MetricSampleAggregationResult;
import com.linkedin.cruisecontrol.monitor.sampling.aggregator.ValuesAndExtrapolations;
import com.linkedin.kafka.cruisecontrol.async.progress.OperationProgress;
import com.linkedin.kafka.cruisecontrol.common.MetadataClient;
import com.linkedin.kafka.cruisecontrol.common.TestConstants;
import com.linkedin.kafka.cruisecontrol.config.KafkaCruiseControlConfig;
import com.linkedin.kafka.cruisecontrol.monitor.ModelCompletenessRequirements;
import com.linkedin.kafka.cruisecontrol.monitor.metricdefinition.KafkaMetricDef;
import com.linkedin.kafka.cruisecontrol.monitor.sampling.PartitionEntity;
import com.linkedin.kafka.cruisecontrol.monitor.sampling.PartitionMetricSample;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import org.apache.kafka.clients.Metadata;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.TopicPartition;
import org.junit.Assert;
import org.junit.Test;

import static com.linkedin.kafka.cruisecontrol.common.Resource.DISK;


/**
 * Unit test for {@link KafkaPartitionMetricSampleAggregator}.
 */
public class KafkaPartitionMetricSampleAggregatorTest {
    private static final int PARTITION = 0;

    private static final int NUM_WINDOWS = 20;

    private static final long WINDOW_MS = 1000L;

    private static final int MIN_SAMPLES_PER_WINDOW = 4;

    private static final TopicPartition TP = new TopicPartition(TestConstants.TOPIC0, KafkaPartitionMetricSampleAggregatorTest.PARTITION);

    private static final PartitionEntity PE = new PartitionEntity(KafkaPartitionMetricSampleAggregatorTest.TP);

    @Test
    public void testAggregate() throws NotEnoughValidWindowsException {
        KafkaCruiseControlConfig config = new KafkaCruiseControlConfig(getLoadMonitorProperties());
        Metadata metadata = getMetadata(Collections.singleton(KafkaPartitionMetricSampleAggregatorTest.TP));
        KafkaPartitionMetricSampleAggregator metricSampleAggregator = new KafkaPartitionMetricSampleAggregator(config, metadata);
        populateSampleAggregator(((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) + 1), KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator);
        MetricSampleAggregationResult<String, PartitionEntity> result = metricSampleAggregator.aggregate(clusterAndGeneration(metadata.fetch()), Long.MAX_VALUE, new OperationProgress());
        Map<PartitionEntity, ValuesAndExtrapolations> valuesAndExtrapolations = result.valuesAndExtrapolations();
        Assert.assertEquals("The windows should only have one partition", 1, valuesAndExtrapolations.size());
        ValuesAndExtrapolations partitionValuesAndExtrapolations = valuesAndExtrapolations.get(KafkaPartitionMetricSampleAggregatorTest.PE);
        Assert.assertNotNull(partitionValuesAndExtrapolations);
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, partitionValuesAndExtrapolations.metricValues().length());
        for (int i = 0; i < (KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS); i++) {
            Assert.assertEquals((((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - i) * (KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS)), result.valuesAndExtrapolations().get(KafkaPartitionMetricSampleAggregatorTest.PE).window(i));
            for (com.linkedin.kafka.cruisecontrol.common.Resource resource : com.linkedin.kafka.cruisecontrol.common.Resource.cachedValues()) {
                Collection<Integer> metricIds = KafkaMetricDef.resourceToMetricIds(resource);
                double expectedValue = (resource == (DISK) ? (((((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 1) - i) * 10) + (KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW)) - 1 : ((((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 1) - i) * 10) + (((KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW) - 1) / 2.0)) * (metricIds.size());
                Assert.assertEquals(((("The utilization for " + resource) + " should be ") + expectedValue), expectedValue, partitionValuesAndExtrapolations.metricValues().valuesForGroup(resource.name(), KafkaMetricDef.commonMetricDef(), true).get(i), 0);
            }
        }
        // Verify the metric completeness checker state
        MetadataClient.ClusterAndGeneration clusterAndGeneration = new MetadataClient.ClusterAndGeneration(metadata.fetch(), 1);
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, metricSampleAggregator.validWindows(clusterAndGeneration, 1.0).size());
        Map<Long, Float> monitoredPercentages = metricSampleAggregator.validPartitionRatioByWindows(clusterAndGeneration);
        for (double percentage : monitoredPercentages.values()) {
            Assert.assertEquals(1.0, percentage, 0.0);
        }
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, metricSampleAggregator.availableWindows().size());
    }

    @Test
    public void testAggregateWithUpdatedCluster() throws NotEnoughValidWindowsException {
        KafkaCruiseControlConfig config = new KafkaCruiseControlConfig(getLoadMonitorProperties());
        Metadata metadata = getMetadata(Collections.singleton(KafkaPartitionMetricSampleAggregatorTest.TP));
        KafkaPartitionMetricSampleAggregator metricSampleAggregator = new KafkaPartitionMetricSampleAggregator(config, metadata);
        populateSampleAggregator(((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) + 1), KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator);
        TopicPartition tp1 = new TopicPartition(((TestConstants.TOPIC0) + "1"), 0);
        Cluster cluster = getCluster(Arrays.asList(KafkaPartitionMetricSampleAggregatorTest.TP, tp1));
        metadata.update(cluster, Collections.emptySet(), 1);
        Map<PartitionEntity, ValuesAndExtrapolations> aggregateResult = metricSampleAggregator.aggregate(clusterAndGeneration(cluster), Long.MAX_VALUE, new OperationProgress()).valuesAndExtrapolations();
        // Partition "topic-0" should be valid in all NUM_WINDOW windows and Partition "topic1-0" should not since
        // there is no sample for it.
        Assert.assertEquals(1, aggregateResult.size());
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, aggregateResult.get(KafkaPartitionMetricSampleAggregatorTest.PE).windows().size());
        ModelCompletenessRequirements requirements = new ModelCompletenessRequirements(1, 0.0, true);
        MetricSampleAggregationResult<String, PartitionEntity> result = metricSampleAggregator.aggregate(clusterAndGeneration(cluster), (-1), Long.MAX_VALUE, requirements, new OperationProgress());
        aggregateResult = result.valuesAndExtrapolations();
        Assert.assertNotNull("tp1 should be included because includeAllTopics is set to true", aggregateResult.get(new PartitionEntity(tp1)));
        Map<Integer, Extrapolation> extrapolations = aggregateResult.get(new PartitionEntity(tp1)).extrapolations();
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, extrapolations.size());
        for (int i = 0; i < (KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS); i++) {
            Assert.assertEquals(NO_VALID_EXTRAPOLATION, extrapolations.get(i));
        }
    }

    @Test
    public void testAggregateWithPartitionExtrapolations() throws NotEnoughValidWindowsException {
        KafkaCruiseControlConfig config = new KafkaCruiseControlConfig(getLoadMonitorProperties());
        Metadata metadata = getMetadata(Collections.singleton(KafkaPartitionMetricSampleAggregatorTest.TP));
        KafkaPartitionMetricSampleAggregator metricSampleAggregator = new KafkaPartitionMetricSampleAggregator(config, metadata);
        TopicPartition tp1 = new TopicPartition(TestConstants.TOPIC0, 1);
        Cluster cluster = getCluster(Arrays.asList(KafkaPartitionMetricSampleAggregatorTest.TP, tp1));
        PartitionEntity pe1 = new PartitionEntity(tp1);
        metadata.update(cluster, Collections.emptySet(), 1);
        populateSampleAggregator(((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) + 1), KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator);
        // Populate partition 1 but leave 1 hole at NUM_WINDOWS'th window.
        CruiseControlUnitTestUtils.populateSampleAggregator(((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 2), KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator, pe1, 0, KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS, KafkaMetricDef.commonMetricDef());
        CruiseControlUnitTestUtils.populateSampleAggregator(2, KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator, pe1, ((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 1), KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS, KafkaMetricDef.commonMetricDef());
        MetricSampleAggregationResult<String, PartitionEntity> result = metricSampleAggregator.aggregate(clusterAndGeneration(cluster), Long.MAX_VALUE, new OperationProgress());
        Assert.assertEquals(2, result.valuesAndExtrapolations().size());
        Assert.assertTrue(result.valuesAndExtrapolations().get(KafkaPartitionMetricSampleAggregatorTest.PE).extrapolations().isEmpty());
        Assert.assertEquals(1, result.valuesAndExtrapolations().get(pe1).extrapolations().size());
        Assert.assertTrue(result.valuesAndExtrapolations().get(pe1).extrapolations().containsKey(1));
        Assert.assertEquals((((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 1) * (KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS)), result.valuesAndExtrapolations().get(pe1).window(1));
        Assert.assertEquals(AVG_ADJACENT, result.valuesAndExtrapolations().get(pe1).extrapolations().get(1));
    }

    @Test
    public void testFallbackToAvgAvailable() throws NotEnoughValidWindowsException {
        KafkaCruiseControlConfig config = new KafkaCruiseControlConfig(getLoadMonitorProperties());
        Metadata metadata = getMetadata(Collections.singleton(KafkaPartitionMetricSampleAggregatorTest.TP));
        KafkaPartitionMetricSampleAggregator metricSampleAggregator = new KafkaPartitionMetricSampleAggregator(config, metadata);
        // Only give two sample to the aggregator.
        CruiseControlUnitTestUtils.populateSampleAggregator(((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 1), KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator, KafkaPartitionMetricSampleAggregatorTest.PE, 2, KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS, KafkaMetricDef.commonMetricDef());
        MetricSampleAggregationResult<String, PartitionEntity> result = metricSampleAggregator.aggregate(clusterAndGeneration(metadata.fetch()), ((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) * (KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS)), new OperationProgress());
        // Partition "topic-0" is expected to be a valid partition in result with valid sample values for window [3, NUM_WINDOWS].
        Assert.assertEquals(((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 2), result.valuesAndExtrapolations().get(KafkaPartitionMetricSampleAggregatorTest.PE).windows().size());
        populateSampleAggregator(2, ((KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW) - 2), metricSampleAggregator);
        result = metricSampleAggregator.aggregate(clusterAndGeneration(metadata.fetch()), ((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) * (KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS)), new OperationProgress());
        int numWindows = result.valuesAndExtrapolations().get(KafkaPartitionMetricSampleAggregatorTest.PE).metricValues().length();
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, numWindows);
        int numExtrapolationss = 0;
        for (Map.Entry<Integer, Extrapolation> entry : result.valuesAndExtrapolations().get(KafkaPartitionMetricSampleAggregatorTest.PE).extrapolations().entrySet()) {
            Assert.assertEquals(AVG_AVAILABLE, entry.getValue());
            numExtrapolationss++;
        }
        Assert.assertEquals(2, numExtrapolationss);
    }

    @Test
    public void testFallbackToAvgAdjacent() throws NotEnoughValidWindowsException {
        KafkaCruiseControlConfig config = new KafkaCruiseControlConfig(getLoadMonitorProperties());
        TopicPartition anotherTopicPartition = new TopicPartition("AnotherTopic", 1);
        PartitionEntity anotherPartitionEntity = new PartitionEntity(anotherTopicPartition);
        Metadata metadata = getMetadata(Arrays.asList(KafkaPartitionMetricSampleAggregatorTest.TP, anotherTopicPartition));
        KafkaPartitionMetricSampleAggregator metricSampleAggregator = new KafkaPartitionMetricSampleAggregator(config, metadata);
        // Only give one sample to the aggregator for previous period.
        populateSampleAggregator(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator);
        // Create let (NUM_WINDOWS + 1) have enough samples.
        CruiseControlUnitTestUtils.populateSampleAggregator(1, KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator, KafkaPartitionMetricSampleAggregatorTest.PE, KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS, KafkaMetricDef.commonMetricDef());
        // Let a window exist but not containing samples for partition 0
        CruiseControlUnitTestUtils.populateSampleAggregator(1, KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator, anotherPartitionEntity, ((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) + 1), KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS, KafkaMetricDef.commonMetricDef());
        // Let the rest of the window has enough samples.
        CruiseControlUnitTestUtils.populateSampleAggregator(2, KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator, KafkaPartitionMetricSampleAggregatorTest.PE, ((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) + 2), KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS, KafkaMetricDef.commonMetricDef());
        MetricSampleAggregationResult<String, PartitionEntity> result = metricSampleAggregator.aggregate(clusterAndGeneration(metadata.fetch()), (((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) * (KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS)) * 2), new OperationProgress());
        int numWindows = result.valuesAndExtrapolations().get(KafkaPartitionMetricSampleAggregatorTest.PE).metricValues().length();
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, numWindows);
        int numExtrapolations = 0;
        for (Map.Entry<Integer, Extrapolation> entry : result.valuesAndExtrapolations().get(KafkaPartitionMetricSampleAggregatorTest.PE).extrapolations().entrySet()) {
            Assert.assertEquals(AVG_ADJACENT, entry.getValue());
            numExtrapolations++;
        }
        Assert.assertEquals(1, numExtrapolations);
    }

    @Test
    public void testTooManyFlaws() throws NotEnoughValidWindowsException {
        KafkaCruiseControlConfig config = new KafkaCruiseControlConfig(getLoadMonitorProperties());
        Metadata metadata = getMetadata(Collections.singleton(KafkaPartitionMetricSampleAggregatorTest.TP));
        KafkaPartitionMetricSampleAggregator metricSampleAggregator = new KafkaPartitionMetricSampleAggregator(config, metadata);
        // Only give two samples to the aggregator.
        CruiseControlUnitTestUtils.populateSampleAggregator(((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 2), KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator, KafkaPartitionMetricSampleAggregatorTest.PE, 3, KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS, KafkaMetricDef.commonMetricDef());
        MetricSampleAggregationResult<String, PartitionEntity> result = metricSampleAggregator.aggregate(clusterAndGeneration(metadata.fetch()), ((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) * (KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS)), new OperationProgress());
        // Partition "topic-0" is expected to be a valid partition in result, with valid sample values collected for window [1, NUM_WINDOW - 3].
        Assert.assertEquals(((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 3), result.valuesAndExtrapolations().get(KafkaPartitionMetricSampleAggregatorTest.PE).windows().size());
    }

    @Test
    public void testNotEnoughWindows() {
        KafkaCruiseControlConfig config = new KafkaCruiseControlConfig(getLoadMonitorProperties());
        Metadata metadata = getMetadata(Collections.singleton(KafkaPartitionMetricSampleAggregatorTest.TP));
        KafkaPartitionMetricSampleAggregator metricSampleAggregator = new KafkaPartitionMetricSampleAggregator(config, metadata);
        populateSampleAggregator(((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) + 1), KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator);
        try {
            // Only 4 windows have smaller timestamp than the timestamp we passed in.
            ModelCompletenessRequirements requirements = new ModelCompletenessRequirements(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, 0.0, false);
            metricSampleAggregator.aggregate(clusterAndGeneration(metadata.fetch()), (-1L), ((((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 1) * (KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS)) - 1), requirements, new OperationProgress());
            Assert.fail("Should throw NotEnoughValidWindowsException");
        } catch (NotEnoughValidWindowsException nse) {
            // let it go
        }
    }

    @Test
    public void testExcludeInvalidMetricSample() throws NotEnoughValidWindowsException {
        KafkaCruiseControlConfig config = new KafkaCruiseControlConfig(getLoadMonitorProperties());
        Metadata metadata = getMetadata(Collections.singleton(KafkaPartitionMetricSampleAggregatorTest.TP));
        KafkaPartitionMetricSampleAggregator metricSampleAggregator = new KafkaPartitionMetricSampleAggregator(config, metadata);
        MetricDef metricDef = KafkaMetricDef.commonMetricDef();
        populateSampleAggregator(((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) + 1), KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW, metricSampleAggregator);
        // Set the leader to be node 1, which is different from the leader in the metadata.
        PartitionMetricSample sampleWithDifferentLeader = new PartitionMetricSample(1, KafkaPartitionMetricSampleAggregatorTest.TP);
        sampleWithDifferentLeader.record(metricDef.metricInfo(DISK_USAGE.name()), 10000);
        sampleWithDifferentLeader.record(metricDef.metricInfo(CPU_USAGE.name()), 10000);
        sampleWithDifferentLeader.record(metricDef.metricInfo(LEADER_BYTES_IN.name()), 10000);
        sampleWithDifferentLeader.record(metricDef.metricInfo(LEADER_BYTES_OUT.name()), 10000);
        sampleWithDifferentLeader.close(0);
        // Only populate the CPU metric
        PartitionMetricSample incompletePartitionMetricSample = new PartitionMetricSample(0, KafkaPartitionMetricSampleAggregatorTest.TP);
        incompletePartitionMetricSample.record(metricDef.metricInfo(CPU_USAGE.name()), 10000);
        incompletePartitionMetricSample.close(0);
        metricSampleAggregator.addSample(sampleWithDifferentLeader);
        metricSampleAggregator.addSample(incompletePartitionMetricSample);
        // Check the window value and make sure the metric samples above are excluded.
        Map<PartitionEntity, ValuesAndExtrapolations> valuesAndExtrapolations = metricSampleAggregator.aggregate(clusterAndGeneration(metadata.fetch()), ((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) * (KafkaPartitionMetricSampleAggregatorTest.WINDOW_MS)), new OperationProgress()).valuesAndExtrapolations();
        ValuesAndExtrapolations partitionValuesAndExtrapolations = valuesAndExtrapolations.get(KafkaPartitionMetricSampleAggregatorTest.PE);
        for (com.linkedin.kafka.cruisecontrol.common.Resource resource : com.linkedin.kafka.cruisecontrol.common.Resource.cachedValues()) {
            Collection<Integer> metricIds = KafkaMetricDef.resourceToMetricIds(resource);
            double expectedValue = (resource == (DISK) ? (KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW) - 1 : ((KafkaPartitionMetricSampleAggregatorTest.MIN_SAMPLES_PER_WINDOW) - 1) / 2.0) * (metricIds.size());
            Assert.assertEquals(((("The utilization for " + resource) + " should be ") + expectedValue), expectedValue, partitionValuesAndExtrapolations.metricValues().valuesForGroup(resource.name(), KafkaMetricDef.commonMetricDef(), true).get(((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 1)), 0);
        }
    }

    @Test
    public void testValidWindows() {
        KafkaPartitionMetricSampleAggregatorTest.TestContext ctx = setupScenario1();
        KafkaPartitionMetricSampleAggregator aggregator = ctx.aggregator();
        MetadataClient.ClusterAndGeneration clusterAndGeneration = ctx.clusterAndGeneration(0);
        SortedSet<Long> validWindows = aggregator.validWindows(clusterAndGeneration, 1.0);
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, validWindows.size());
        assertValidWindows(validWindows, KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, Collections.emptySet());
    }

    @Test
    public void testValidWindowsWithInvalidPartitions() {
        KafkaPartitionMetricSampleAggregatorTest.TestContext ctx = setupScenario2();
        KafkaPartitionMetricSampleAggregator aggregator = ctx.aggregator();
        MetadataClient.ClusterAndGeneration clusterAndGeneration = ctx.clusterAndGeneration(0);
        SortedSet<Long> validWindows = aggregator.validWindows(clusterAndGeneration, 1.0);
        Assert.assertEquals("Should have three invalid windows.", ((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 3), validWindows.size());
        assertValidWindows(validWindows, ((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 1), Arrays.asList(6, 7));
        // reduced monitored percentage should include every window.
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, aggregator.validWindows(clusterAndGeneration, 0.5).size());
    }

    @Test
    public void testValidWindowWithDifferentInvalidPartitions() {
        KafkaPartitionMetricSampleAggregatorTest.TestContext ctx = setupScenario3();
        KafkaPartitionMetricSampleAggregator aggregator = ctx.aggregator();
        MetadataClient.ClusterAndGeneration clusterAndGeneration = ctx.clusterAndGeneration(0);
        SortedSet<Long> validWindows = aggregator.validWindows(clusterAndGeneration, 0.5);
        Assert.assertEquals("Should have two invalid windows.", ((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 2), validWindows.size());
        assertValidWindows(validWindows, KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, Arrays.asList(6, 7));
    }

    @Test
    public void testValidWindowsWithTooManyExtrapolations() {
        KafkaPartitionMetricSampleAggregatorTest.TestContext ctx = setupScenario4();
        KafkaPartitionMetricSampleAggregator aggregator = ctx.aggregator();
        MetadataClient.ClusterAndGeneration clusterAndGeneration = ctx.clusterAndGeneration(0);
        SortedSet<Long> validWindows = aggregator.validWindows(clusterAndGeneration, 0.5);
        Assert.assertEquals("Should have two invalid windows.", ((KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS) - 2), validWindows.size());
        assertValidWindows(validWindows, KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, Arrays.asList(6, 7));
    }

    @Test
    public void testMonitoredPercentage() {
        KafkaPartitionMetricSampleAggregatorTest.TestContext ctx = setupScenario1();
        KafkaPartitionMetricSampleAggregator aggregator = ctx.aggregator();
        MetadataClient.ClusterAndGeneration clusterAndGeneration = ctx.clusterAndGeneration(0);
        Assert.assertEquals(1.0, aggregator.monitoredPercentage(clusterAndGeneration), 0.01);
        ctx = setupScenario2();
        aggregator = ctx.aggregator();
        clusterAndGeneration = ctx.clusterAndGeneration(0);
        Assert.assertEquals(0.5, aggregator.monitoredPercentage(clusterAndGeneration), 0.01);
        ctx = setupScenario3();
        aggregator = ctx.aggregator();
        clusterAndGeneration = ctx.clusterAndGeneration(0);
        Assert.assertEquals((((double) (2)) / 6), aggregator.monitoredPercentage(clusterAndGeneration), 0.01);
        ctx = setupScenario4();
        aggregator = ctx.aggregator();
        clusterAndGeneration = ctx.clusterAndGeneration(0);
        Assert.assertEquals((((double) (2)) / 6), aggregator.monitoredPercentage(clusterAndGeneration), 0.01);
    }

    @Test
    public void testMonitoredPercentagesByWindows() {
        KafkaPartitionMetricSampleAggregatorTest.TestContext ctx = setupScenario1();
        KafkaPartitionMetricSampleAggregator aggregator = ctx.aggregator();
        MetadataClient.ClusterAndGeneration clusterAndGeneration = ctx.clusterAndGeneration(0);
        Map<Long, Float> percentages = aggregator.validPartitionRatioByWindows(clusterAndGeneration);
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, percentages.size());
        for (Map.Entry<Long, Float> entry : percentages.entrySet()) {
            Assert.assertEquals(1.0, entry.getValue(), 0.01);
        }
        ctx = setupScenario2();
        aggregator = ctx.aggregator();
        clusterAndGeneration = ctx.clusterAndGeneration(0);
        percentages = aggregator.validPartitionRatioByWindows(clusterAndGeneration);
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, percentages.size());
        for (Map.Entry<Long, Float> entry : percentages.entrySet()) {
            long window = entry.getKey();
            if (((window == 6000) || (window == 7000)) || (window == 20000)) {
                Assert.assertEquals(0.5, entry.getValue(), 0.01);
            } else {
                Assert.assertEquals(1.0, entry.getValue(), 0.01);
            }
        }
        ctx = setupScenario3();
        aggregator = ctx.aggregator();
        clusterAndGeneration = ctx.clusterAndGeneration(0);
        percentages = aggregator.validPartitionRatioByWindows(clusterAndGeneration);
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, percentages.size());
        for (Map.Entry<Long, Float> entry : percentages.entrySet()) {
            long window = entry.getKey();
            if ((((window == 6000) || (window == 7000)) || (window == 18000)) || (window == 19000)) {
                Assert.assertEquals((((double) (4)) / 6), entry.getValue(), 0.01);
            } else {
                Assert.assertEquals(1.0, entry.getValue(), 0.01);
            }
        }
        ctx = setupScenario4();
        aggregator = ctx.aggregator();
        clusterAndGeneration = ctx.clusterAndGeneration(0);
        percentages = aggregator.validPartitionRatioByWindows(clusterAndGeneration);
        Assert.assertEquals(KafkaPartitionMetricSampleAggregatorTest.NUM_WINDOWS, percentages.size());
        for (Map.Entry<Long, Float> entry : percentages.entrySet()) {
            long window = entry.getKey();
            if ((window == 6000) || (window == 7000)) {
                Assert.assertEquals((((double) (2)) / 6), entry.getValue(), 0.01);
            } else {
                Assert.assertEquals((((double) (4)) / 6), entry.getValue(), 0.01);
            }
        }
    }

    private static class TestContext {
        private final Metadata _metadata;

        private final KafkaPartitionMetricSampleAggregator _aggregator;

        TestContext(Metadata metadata, KafkaPartitionMetricSampleAggregator aggregator) {
            _metadata = metadata;
            _aggregator = aggregator;
        }

        private ClusterAndGeneration clusterAndGeneration(int generation) {
            return new MetadataClient.ClusterAndGeneration(_metadata.fetch(), generation);
        }

        private KafkaPartitionMetricSampleAggregator aggregator() {
            return _aggregator;
        }
    }
}

