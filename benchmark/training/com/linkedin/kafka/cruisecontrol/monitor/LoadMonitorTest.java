/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.monitor;


import MetadataClient.ClusterAndGeneration;
import Resource.CPU;
import Resource.DISK;
import Resource.NW_IN;
import Resource.NW_OUT;
import com.linkedin.cruisecontrol.CruiseControlUnitTestUtils;
import com.linkedin.cruisecontrol.exception.NotEnoughValidWindowsException;
import com.linkedin.cruisecontrol.metricdef.MetricDef;
import com.linkedin.kafka.cruisecontrol.async.progress.OperationProgress;
import com.linkedin.kafka.cruisecontrol.common.MetadataClient;
import com.linkedin.kafka.cruisecontrol.common.TestConstants;
import com.linkedin.kafka.cruisecontrol.config.KafkaCruiseControlConfig;
import com.linkedin.kafka.cruisecontrol.model.ClusterModel;
import com.linkedin.kafka.cruisecontrol.monitor.metricdefinition.KafkaMetricDef;
import com.linkedin.kafka.cruisecontrol.monitor.sampling.PartitionEntity;
import com.linkedin.kafka.cruisecontrol.monitor.sampling.aggregator.KafkaPartitionMetricSampleAggregator;
import java.util.Arrays;
import org.apache.kafka.clients.Metadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for LoadMonitor
 */
public class LoadMonitorTest {
    private static final int P0 = 0;

    private static final int P1 = 1;

    private static final TopicPartition T0P0 = new TopicPartition(TestConstants.TOPIC0, LoadMonitorTest.P0);

    private static final TopicPartition T0P1 = new TopicPartition(TestConstants.TOPIC0, LoadMonitorTest.P1);

    private static final TopicPartition T1P0 = new TopicPartition(TestConstants.TOPIC1, LoadMonitorTest.P0);

    private static final TopicPartition T1P1 = new TopicPartition(TestConstants.TOPIC1, LoadMonitorTest.P1);

    private static final PartitionEntity PE_T0P0 = new PartitionEntity(LoadMonitorTest.T0P0);

    private static final PartitionEntity PE_T0P1 = new PartitionEntity(LoadMonitorTest.T0P1);

    private static final PartitionEntity PE_T1P0 = new PartitionEntity(LoadMonitorTest.T1P0);

    private static final PartitionEntity PE_T1P1 = new PartitionEntity(LoadMonitorTest.T1P1);

    private static final MetricDef METRIC_DEF = KafkaMetricDef.commonMetricDef();

    private static final int NUM_WINDOWS = 2;

    private static final int MIN_SAMPLES_PER_WINDOW = 4;

    private static final long WINDOW_MS = 1000;

    private static final String DEFAULT_CLEANUP_POLICY = "delete";

    private final Time _time = new MockTime(0);

    @Test
    public void testStateWithOnlyActiveSnapshotWindow() {
        LoadMonitorTest.TestContext context = prepareContext();
        LoadMonitor loadMonitor = context.loadmonitor();
        KafkaPartitionMetricSampleAggregator aggregator = context.aggregator();
        // populate the metrics aggregator.
        // four samples for each partition
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T0P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T0P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T1P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T1P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        MetadataClient.ClusterAndGeneration clusterAndGeneration = loadMonitor.refreshClusterAndGeneration();
        LoadMonitorState state = loadMonitor.state(new OperationProgress(), clusterAndGeneration);
        // The load monitor only has an active window. There is no stable window.
        Assert.assertEquals(0, state.numValidPartitions());
        Assert.assertEquals(0, state.numValidWindows());
        Assert.assertTrue(state.monitoredWindows().isEmpty());
    }

    @Test
    public void testStateWithoutEnoughSnapshotWindows() {
        LoadMonitorTest.TestContext context = prepareContext();
        LoadMonitor loadMonitor = context.loadmonitor();
        KafkaPartitionMetricSampleAggregator aggregator = context.aggregator();
        // populate the metrics aggregator.
        // four samples for each partition except T1P1. T1P1 has no sample in the first window and one in the second window.
        CruiseControlUnitTestUtils.populateSampleAggregator(2, 4, aggregator, LoadMonitorTest.PE_T0P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(2, 4, aggregator, LoadMonitorTest.PE_T0P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(2, 4, aggregator, LoadMonitorTest.PE_T1P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        MetadataClient.ClusterAndGeneration clusterAndGeneration = loadMonitor.refreshClusterAndGeneration();
        LoadMonitorState state = loadMonitor.state(new OperationProgress(), clusterAndGeneration);
        // The load monitor has 1 stable window with 0.5 of valid partitions ratio.
        Assert.assertEquals(0, state.numValidPartitions());
        Assert.assertEquals(0, state.numValidWindows());
        Assert.assertEquals(1, state.monitoredWindows().size());
        Assert.assertEquals(0.5, state.monitoredWindows().get(LoadMonitorTest.WINDOW_MS), 0.0);
        // Back fill for T1P1
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 1, aggregator, LoadMonitorTest.PE_T1P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        clusterAndGeneration = loadMonitor.refreshClusterAndGeneration();
        state = loadMonitor.state(new OperationProgress(), clusterAndGeneration);
        // The load monitor now has one stable window with 1.0 of valid partitions ratio.
        Assert.assertEquals(0, state.numValidPartitions());
        Assert.assertEquals(1, state.numValidWindows());
        Assert.assertEquals(1, state.monitoredWindows().size());
        Assert.assertEquals(1.0, state.monitoredWindows().get(LoadMonitorTest.WINDOW_MS), 0.0);
    }

    @Test
    public void testStateWithInvalidSnapshotWindows() {
        LoadMonitorTest.TestContext context = prepareContext();
        LoadMonitor loadMonitor = context.loadmonitor();
        KafkaPartitionMetricSampleAggregator aggregator = context.aggregator();
        // populate the metrics aggregator.
        // four samples for each partition except T1P1. T1P1 has 2 samples in the first window, and 2 samples in the
        // active window.
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T0P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T0P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T1P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 2, aggregator, LoadMonitorTest.PE_T1P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 2, aggregator, LoadMonitorTest.PE_T1P1, 2, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        MetadataClient.ClusterAndGeneration clusterAndGeneration = loadMonitor.refreshClusterAndGeneration();
        LoadMonitorState state = loadMonitor.state(new OperationProgress(), clusterAndGeneration);
        // Both partitions for topic 0 should be valid.
        Assert.assertEquals(2, state.numValidPartitions());
        // Both topic should be valid in the first window.
        Assert.assertEquals(1, state.numValidWindows());
        // There should be 2 monitored windows.
        Assert.assertEquals(2, state.monitoredWindows().size());
        // Both topic should be valid in the first window.
        Assert.assertEquals(1.0, state.monitoredWindows().get(LoadMonitorTest.WINDOW_MS), 0.0);
        // Only topic 2 is valid in the second window.
        Assert.assertEquals(0.5, state.monitoredWindows().get(((LoadMonitorTest.WINDOW_MS) * 2)), 0.0);
        // Back fill 3 samples for T1P1 in the second window.
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 3, aggregator, LoadMonitorTest.PE_T1P1, 1, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        clusterAndGeneration = loadMonitor.refreshClusterAndGeneration();
        state = loadMonitor.state(new OperationProgress(), clusterAndGeneration);
        // All the partitions should be valid now.
        Assert.assertEquals(4, state.numValidPartitions());
        // All the windows should be valid now.
        Assert.assertEquals(2, state.numValidWindows());
        // There should be two monitored windows.
        Assert.assertEquals(2, state.monitoredWindows().size());
        // Both monitored windows should have 100% completeness.
        Assert.assertEquals(1.0, state.monitoredWindows().get(LoadMonitorTest.WINDOW_MS), 0.0);
        Assert.assertEquals(1.0, state.monitoredWindows().get(((LoadMonitorTest.WINDOW_MS) * 2)), 0.0);
    }

    @Test
    public void testMeetCompletenessRequirements() {
        LoadMonitorTest.TestContext context = prepareContext();
        LoadMonitor loadMonitor = context.loadmonitor();
        KafkaPartitionMetricSampleAggregator aggregator = context.aggregator();
        // Require at least 1 valid window with 1.0 of valid partitions ratio.
        ModelCompletenessRequirements requirements1 = new ModelCompletenessRequirements(1, 1.0, false);
        // Require at least 1 valid window with 0.5 of valid partitions ratio.
        ModelCompletenessRequirements requirements2 = new ModelCompletenessRequirements(1, 0.5, false);
        // Require at least 2 valid windows with 1.0 of valid partitions ratio.
        ModelCompletenessRequirements requirements3 = new ModelCompletenessRequirements(2, 1.0, false);
        // Require at least 2 valid windows with 0.5 of valid partitions ratio.
        ModelCompletenessRequirements requirements4 = new ModelCompletenessRequirements(2, 0.5, false);
        // populate the metrics aggregator.
        // One stable window + one active window, enough samples for each partition except T1P1.
        CruiseControlUnitTestUtils.populateSampleAggregator(2, 4, aggregator, LoadMonitorTest.PE_T0P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(2, 4, aggregator, LoadMonitorTest.PE_T0P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(2, 4, aggregator, LoadMonitorTest.PE_T1P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        // The load monitor has one window with 0.5 valid partitions ratio.
        MetadataClient.ClusterAndGeneration clusterAndGeneration = loadMonitor.refreshClusterAndGeneration();
        Assert.assertFalse(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements1));
        Assert.assertTrue(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements2));
        Assert.assertFalse(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements3));
        Assert.assertFalse(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements4));
        // Add more samples, two stable windows + one active window. enough samples for each partition except T1P1
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T0P0, 2, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T0P1, 2, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T1P0, 2, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 1, aggregator, LoadMonitorTest.PE_T1P1, 2, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        // The load monitor has two windows, both with 0.5 valid partitions ratio
        clusterAndGeneration = loadMonitor.refreshClusterAndGeneration();
        Assert.assertFalse(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements1));
        Assert.assertTrue(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements2));
        Assert.assertFalse(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements3));
        Assert.assertTrue(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements4));
        // Back fill the first stable window for T1P1
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 1, aggregator, LoadMonitorTest.PE_T1P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        // The load monitor has two windows with 1.0 and 0.5 of completeness respectively.
        clusterAndGeneration = loadMonitor.refreshClusterAndGeneration();
        Assert.assertTrue(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements1));
        Assert.assertTrue(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements2));
        Assert.assertFalse(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements3));
        Assert.assertTrue(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements4));
        // Back fill all stable windows for T1P1
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 3, aggregator, LoadMonitorTest.PE_T1P1, 1, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        // The load monitor has two windows both with 1.0 of completeness.
        clusterAndGeneration = loadMonitor.refreshClusterAndGeneration();
        Assert.assertTrue(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements1));
        Assert.assertTrue(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements2));
        Assert.assertTrue(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements3));
        Assert.assertTrue(loadMonitor.meetCompletenessRequirements(clusterAndGeneration, requirements4));
    }

    // Test the case with enough snapshot windows and valid partitions.
    @Test
    public void testBasicClusterModel() throws NotEnoughValidWindowsException {
        LoadMonitorTest.TestContext context = prepareContext();
        LoadMonitor loadMonitor = context.loadmonitor();
        KafkaPartitionMetricSampleAggregator aggregator = context.aggregator();
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T0P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T0P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T1P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T1P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        ClusterModel clusterModel = loadMonitor.clusterModel((-1), Long.MAX_VALUE, new ModelCompletenessRequirements(2, 1.0, false), new OperationProgress());
        Assert.assertEquals(6.5, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(CPU), 0.0);
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_IN), 0.0);
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_OUT), 0.0);
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(DISK), 0.0);
    }

    // Not enough snapshot windows and some partitions are missing from all snapshot windows.
    @Test
    public void testClusterModelWithInvalidPartitionAndInsufficientSnapshotWindows() throws NotEnoughValidWindowsException {
        LoadMonitorTest.TestContext context = prepareContext();
        LoadMonitor loadMonitor = context.loadmonitor();
        KafkaPartitionMetricSampleAggregator aggregator = context.aggregator();
        ModelCompletenessRequirements requirements1 = new ModelCompletenessRequirements(1, 1.0, false);
        ModelCompletenessRequirements requirements2 = new ModelCompletenessRequirements(1, 0.5, false);
        ModelCompletenessRequirements requirements3 = new ModelCompletenessRequirements(2, 1.0, false);
        ModelCompletenessRequirements requirements4 = new ModelCompletenessRequirements(2, 0.5, false);
        // populate the metrics aggregator.
        // two samples for each partition except T1P1
        CruiseControlUnitTestUtils.populateSampleAggregator(2, 4, aggregator, LoadMonitorTest.PE_T0P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(2, 4, aggregator, LoadMonitorTest.PE_T0P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(2, 4, aggregator, LoadMonitorTest.PE_T1P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        try {
            loadMonitor.clusterModel((-1), Long.MAX_VALUE, requirements1, new OperationProgress());
            Assert.fail("Should have thrown NotEnoughValidWindowsException.");
        } catch (NotEnoughValidWindowsException nevwe) {
            // let it go
        }
        ClusterModel clusterModel = loadMonitor.clusterModel((-1L), Long.MAX_VALUE, requirements2, new OperationProgress());
        Assert.assertNull(clusterModel.partition(LoadMonitorTest.T1P0));
        Assert.assertNull(clusterModel.partition(LoadMonitorTest.T1P1));
        Assert.assertEquals(1, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().numWindows());
        Assert.assertEquals(3, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(DISK), 0.0);
        Assert.assertEquals(1.5, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(CPU), 0.0);
        Assert.assertEquals(3.0, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_IN), 0.0);
        Assert.assertEquals(3.0, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_OUT), 0.0);
        try {
            loadMonitor.clusterModel((-1L), Long.MAX_VALUE, requirements3, new OperationProgress());
            Assert.fail("Should have thrown NotEnoughValidWindowsException.");
        } catch (NotEnoughValidWindowsException nevwe) {
            // let it go
        }
        try {
            loadMonitor.clusterModel((-1L), Long.MAX_VALUE, requirements4, new OperationProgress());
            Assert.fail("Should have thrown NotEnoughValidWindowsException.");
        } catch (NotEnoughValidWindowsException nevwe) {
            // let it go
        }
    }

    // Enough snapshot windows, some partitions are invalid in all snapshot windows.
    @Test
    public void testClusterWithInvalidPartitions() throws NotEnoughValidWindowsException {
        LoadMonitorTest.TestContext context = prepareContext();
        LoadMonitor loadMonitor = context.loadmonitor();
        KafkaPartitionMetricSampleAggregator aggregator = context.aggregator();
        ModelCompletenessRequirements requirements1 = new ModelCompletenessRequirements(1, 1.0, false);
        ModelCompletenessRequirements requirements2 = new ModelCompletenessRequirements(1, 0.5, false);
        ModelCompletenessRequirements requirements3 = new ModelCompletenessRequirements(2, 1.0, false);
        ModelCompletenessRequirements requirements4 = new ModelCompletenessRequirements(2, 0.5, false);
        // populate the metrics aggregator.
        // two samples for each partition except T1P1
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T0P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T0P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T1P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        try {
            loadMonitor.clusterModel((-1), Long.MAX_VALUE, requirements1, new OperationProgress());
            Assert.fail("Should have thrown NotEnoughValidWindowsException.");
        } catch (NotEnoughValidWindowsException nevwe) {
            // let it go
        }
        ClusterModel clusterModel = loadMonitor.clusterModel((-1L), Long.MAX_VALUE, requirements2, new OperationProgress());
        Assert.assertNull(clusterModel.partition(LoadMonitorTest.T1P0));
        Assert.assertNull(clusterModel.partition(LoadMonitorTest.T1P1));
        Assert.assertEquals(2, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().numWindows());
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(DISK), 0.0);
        Assert.assertEquals(6.5, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(CPU), 0.0);
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_IN), 0.0);
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_OUT), 0.0);
        try {
            loadMonitor.clusterModel((-1L), Long.MAX_VALUE, requirements3, new OperationProgress());
            Assert.fail("Should have thrown NotEnoughValidWindowsException.");
        } catch (NotEnoughValidWindowsException nevwe) {
            // let it go
        }
        clusterModel = loadMonitor.clusterModel((-1L), Long.MAX_VALUE, requirements4, new OperationProgress());
        Assert.assertNull(clusterModel.partition(LoadMonitorTest.T1P0));
        Assert.assertNull(clusterModel.partition(LoadMonitorTest.T1P1));
        Assert.assertEquals(2, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().numWindows());
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(DISK), 0.0);
        Assert.assertEquals(6.5, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(CPU), 0.0);
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_IN), 0.0);
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_OUT), 0.0);
    }

    // Enough snapshot windows, some partitions are not available in some snapshot windows.
    @Test
    public void testClusterModelWithPartlyInvalidPartitions() throws NotEnoughValidWindowsException {
        LoadMonitorTest.TestContext context = prepareContext();
        LoadMonitor loadMonitor = context.loadmonitor();
        KafkaPartitionMetricSampleAggregator aggregator = context.aggregator();
        ModelCompletenessRequirements requirements1 = new ModelCompletenessRequirements(1, 1.0, false);
        ModelCompletenessRequirements requirements2 = new ModelCompletenessRequirements(1, 0.5, false);
        ModelCompletenessRequirements requirements3 = new ModelCompletenessRequirements(2, 1.0, false);
        ModelCompletenessRequirements requirements4 = new ModelCompletenessRequirements(2, 0.5, false);
        // populate the metrics aggregator.
        // four samples for each partition except T1P1
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T0P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T0P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(3, 4, aggregator, LoadMonitorTest.PE_T1P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 1, aggregator, LoadMonitorTest.PE_T1P1, 1, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        ClusterModel clusterModel = loadMonitor.clusterModel((-1), Long.MAX_VALUE, requirements1, new OperationProgress());
        for (TopicPartition tp : Arrays.asList(LoadMonitorTest.T0P0, LoadMonitorTest.T0P1, LoadMonitorTest.T1P0, LoadMonitorTest.T1P1)) {
            Assert.assertNotNull(clusterModel.partition(tp));
        }
        Assert.assertEquals(1, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().numWindows());
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(DISK), 0.0);
        Assert.assertEquals(11.5, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(CPU), 0.0);
        Assert.assertEquals(23, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_IN), 0.0);
        Assert.assertEquals(23, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_OUT), 0.0);
        Assert.assertEquals(10, clusterModel.partition(LoadMonitorTest.T1P1).leader().load().expectedUtilizationFor(DISK), 0.0);
        Assert.assertEquals(10, clusterModel.partition(LoadMonitorTest.T1P1).leader().load().expectedUtilizationFor(CPU), 0.0);
        Assert.assertEquals(20, clusterModel.partition(LoadMonitorTest.T1P1).leader().load().expectedUtilizationFor(NW_IN), 0.0);
        Assert.assertEquals(20, clusterModel.partition(LoadMonitorTest.T1P1).leader().load().expectedUtilizationFor(NW_OUT), 0.0);
        clusterModel = loadMonitor.clusterModel((-1L), Long.MAX_VALUE, requirements2, new OperationProgress());
        Assert.assertNull(clusterModel.partition(LoadMonitorTest.T1P0));
        Assert.assertNull(clusterModel.partition(LoadMonitorTest.T1P1));
        Assert.assertEquals(2, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().numWindows());
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(DISK), 0.0);
        Assert.assertEquals(6.5, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(CPU), 0.0);
        Assert.assertEquals(13.0, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_IN), 0.0);
        Assert.assertEquals(13.0, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_OUT), 0.0);
        try {
            loadMonitor.clusterModel((-1L), Long.MAX_VALUE, requirements3, new OperationProgress());
            Assert.fail("Should have thrown NotEnoughValidWindowsException.");
        } catch (NotEnoughValidWindowsException nevwe) {
            // let it go
        }
        clusterModel = loadMonitor.clusterModel((-1L), Long.MAX_VALUE, requirements4, new OperationProgress());
        Assert.assertNull(clusterModel.partition(LoadMonitorTest.T1P0));
        Assert.assertNull(clusterModel.partition(LoadMonitorTest.T1P1));
        Assert.assertEquals(2, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().numWindows());
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(DISK), 0.0);
        Assert.assertEquals(6.5, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(CPU), 0.0);
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_IN), 0.0);
        Assert.assertEquals(13, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(NW_OUT), 0.0);
    }

    @Test
    public void testClusterModelWithInvalidSnapshotWindows() throws NotEnoughValidWindowsException {
        LoadMonitorTest.TestContext context = prepareContext(4);
        LoadMonitor loadMonitor = context.loadmonitor();
        KafkaPartitionMetricSampleAggregator aggregator = context.aggregator();
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T0P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T0P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T1P0, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T1P1, 0, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T0P0, 3, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T0P1, 3, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T1P0, 3, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T1P1, 3, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T0P0, 4, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T0P1, 4, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T1P0, 4, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        CruiseControlUnitTestUtils.populateSampleAggregator(1, 4, aggregator, LoadMonitorTest.PE_T1P1, 4, LoadMonitorTest.WINDOW_MS, LoadMonitorTest.METRIC_DEF);
        ClusterModel clusterModel = loadMonitor.clusterModel((-1), Long.MAX_VALUE, new ModelCompletenessRequirements(2, 0, false), new OperationProgress());
        Assert.assertEquals(2, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().numWindows());
        Assert.assertEquals(16.5, clusterModel.partition(LoadMonitorTest.T0P0).leader().load().expectedUtilizationFor(CPU), 0.0);
        Assert.assertEquals(2, clusterModel.partition(LoadMonitorTest.T0P1).leader().load().numWindows());
        Assert.assertEquals(33, clusterModel.partition(LoadMonitorTest.T0P1).leader().load().expectedUtilizationFor(DISK), 0.0);
        Assert.assertEquals(2, clusterModel.partition(LoadMonitorTest.T1P0).leader().load().numWindows());
        Assert.assertEquals(33, clusterModel.partition(LoadMonitorTest.T1P0).leader().load().expectedUtilizationFor(NW_IN), 0.0);
        Assert.assertEquals(2, clusterModel.partition(LoadMonitorTest.T1P1).leader().load().numWindows());
        Assert.assertEquals(33, clusterModel.partition(LoadMonitorTest.T1P1).leader().load().expectedUtilizationFor(NW_OUT), 0.0);
    }

    private static class TestContext {
        private final LoadMonitor _loadMonitor;

        private final KafkaPartitionMetricSampleAggregator _aggregator;

        private final KafkaCruiseControlConfig _config;

        private final Metadata _metadata;

        private TestContext(LoadMonitor loadMonitor, KafkaPartitionMetricSampleAggregator aggregator, KafkaCruiseControlConfig config, Metadata metadata) {
            _loadMonitor = loadMonitor;
            _aggregator = aggregator;
            _config = config;
            _metadata = metadata;
        }

        private LoadMonitor loadmonitor() {
            return _loadMonitor;
        }

        private KafkaPartitionMetricSampleAggregator aggregator() {
            return _aggregator;
        }

        private KafkaCruiseControlConfig config() {
            return _config;
        }

        private Metadata metadata() {
            return _metadata;
        }
    }
}

