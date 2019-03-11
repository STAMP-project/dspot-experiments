/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.connectors.kafka;


import KafkaTopicPartitionStateSentinel.EARLIEST_OFFSET;
import TimeCharacteristic.ProcessingTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.operators.StreamSource;
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.connectors.kafka.config.OffsetCommitMode;
import org.apache.flink.streaming.connectors.kafka.internals.AbstractFetcher;
import org.apache.flink.streaming.connectors.kafka.internals.AbstractPartitionDiscoverer;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicsDescriptor;
import org.apache.flink.streaming.util.AbstractStreamOperatorTestHarness;
import org.apache.flink.streaming.util.OperatorSnapshotUtil;
import org.apache.flink.testutils.migration.MigrationVersion;
import org.apache.flink.util.SerializedValue;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static FlinkKafkaConsumerBase.PARTITION_DISCOVERY_DISABLED;


/**
 * Tests for checking whether {@link FlinkKafkaConsumerBase} can restore from snapshots that were
 * done using previous Flink versions' {@link FlinkKafkaConsumerBase}.
 *
 * <p>For regenerating the binary snapshot files run {@link #writeSnapshot()} on the corresponding
 * Flink release-* branch.
 */
@RunWith(Parameterized.class)
public class FlinkKafkaConsumerBaseMigrationTest {
    /**
     * TODO change this to the corresponding savepoint version to be written (e.g. {@link MigrationVersion#v1_3} for 1.3)
     * TODO and remove all @Ignore annotations on write*Snapshot() methods to generate savepoints
     * TODO Note: You should generate the savepoint based on the release branch instead of the master.
     */
    private final MigrationVersion flinkGenerateSavepointVersion = null;

    private static final HashMap<KafkaTopicPartition, Long> PARTITION_STATE = new HashMap<>();

    static {
        FlinkKafkaConsumerBaseMigrationTest.PARTITION_STATE.put(new KafkaTopicPartition("abc", 13), 16768L);
        FlinkKafkaConsumerBaseMigrationTest.PARTITION_STATE.put(new KafkaTopicPartition("def", 7), 987654321L);
    }

    private static final List<String> TOPICS = new java.util.ArrayList(FlinkKafkaConsumerBaseMigrationTest.PARTITION_STATE.keySet()).stream().map(( p) -> p.getTopic()).distinct().collect(Collectors.toList());

    private final MigrationVersion testMigrateVersion;

    public FlinkKafkaConsumerBaseMigrationTest(MigrationVersion testMigrateVersion) {
        this.testMigrateVersion = testMigrateVersion;
    }

    /**
     * Test restoring from an legacy empty state, when no partitions could be found for topics.
     */
    @Test
    public void testRestoreFromEmptyStateNoPartitions() throws Exception {
        final FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer<String> consumerFunction = new FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer(Collections.singletonList("dummy-topic"), Collections.<KafkaTopicPartition>emptyList(), PARTITION_DISCOVERY_DISABLED);
        StreamSource<String, FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer<String>> consumerOperator = new StreamSource(consumerFunction);
        final AbstractStreamOperatorTestHarness<String> testHarness = new AbstractStreamOperatorTestHarness(consumerOperator, 1, 1, 0);
        testHarness.setTimeCharacteristic(ProcessingTime);
        testHarness.setup();
        // restore state from binary snapshot file
        testHarness.initializeState(OperatorSnapshotUtil.getResourceFilename((("kafka-consumer-migration-test-flink" + (testMigrateVersion)) + "-empty-state-snapshot")));
        testHarness.open();
        // assert that no partitions were found and is empty
        Assert.assertTrue(((consumerFunction.getSubscribedPartitionsToStartOffsets()) != null));
        Assert.assertTrue(consumerFunction.getSubscribedPartitionsToStartOffsets().isEmpty());
        // assert that no state was restored
        Assert.assertTrue(consumerFunction.getRestoredState().isEmpty());
        consumerOperator.close();
        consumerOperator.cancel();
    }

    /**
     * Test restoring from an empty state taken using a previous Flink version, when some partitions could be
     * found for topics.
     */
    @Test
    public void testRestoreFromEmptyStateWithPartitions() throws Exception {
        final List<KafkaTopicPartition> partitions = new java.util.ArrayList(FlinkKafkaConsumerBaseMigrationTest.PARTITION_STATE.keySet());
        final FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer<String> consumerFunction = new FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer(FlinkKafkaConsumerBaseMigrationTest.TOPICS, partitions, PARTITION_DISCOVERY_DISABLED);
        StreamSource<String, FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer<String>> consumerOperator = new StreamSource(consumerFunction);
        final AbstractStreamOperatorTestHarness<String> testHarness = new AbstractStreamOperatorTestHarness(consumerOperator, 1, 1, 0);
        testHarness.setTimeCharacteristic(ProcessingTime);
        testHarness.setup();
        // restore state from binary snapshot file
        testHarness.initializeState(OperatorSnapshotUtil.getResourceFilename((("kafka-consumer-migration-test-flink" + (testMigrateVersion)) + "-empty-state-snapshot")));
        testHarness.open();
        // the expected state in "kafka-consumer-migration-test-flink1.2-snapshot-empty-state";
        // all new partitions after the snapshot are considered as partitions that were created while the
        // consumer wasn't running, and should start from the earliest offset.
        final HashMap<KafkaTopicPartition, Long> expectedSubscribedPartitionsWithStartOffsets = new HashMap<>();
        for (KafkaTopicPartition partition : FlinkKafkaConsumerBaseMigrationTest.PARTITION_STATE.keySet()) {
            expectedSubscribedPartitionsWithStartOffsets.put(partition, EARLIEST_OFFSET);
        }
        // assert that there are partitions and is identical to expected list
        Assert.assertTrue(((consumerFunction.getSubscribedPartitionsToStartOffsets()) != null));
        Assert.assertTrue((!(consumerFunction.getSubscribedPartitionsToStartOffsets().isEmpty())));
        Assert.assertEquals(expectedSubscribedPartitionsWithStartOffsets, consumerFunction.getSubscribedPartitionsToStartOffsets());
        // the new partitions should have been considered as restored state
        Assert.assertTrue(((consumerFunction.getRestoredState()) != null));
        Assert.assertTrue((!(consumerFunction.getSubscribedPartitionsToStartOffsets().isEmpty())));
        for (Map.Entry<KafkaTopicPartition, Long> expectedEntry : expectedSubscribedPartitionsWithStartOffsets.entrySet()) {
            Assert.assertEquals(expectedEntry.getValue(), consumerFunction.getRestoredState().get(expectedEntry.getKey()));
        }
        consumerOperator.close();
        consumerOperator.cancel();
    }

    /**
     * Test restoring from a non-empty state taken using a previous Flink version, when some partitions could be
     * found for topics.
     */
    @Test
    public void testRestore() throws Exception {
        final List<KafkaTopicPartition> partitions = new java.util.ArrayList(FlinkKafkaConsumerBaseMigrationTest.PARTITION_STATE.keySet());
        final FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer<String> consumerFunction = new FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer(FlinkKafkaConsumerBaseMigrationTest.TOPICS, partitions, PARTITION_DISCOVERY_DISABLED);
        StreamSource<String, FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer<String>> consumerOperator = new StreamSource(consumerFunction);
        final AbstractStreamOperatorTestHarness<String> testHarness = new AbstractStreamOperatorTestHarness(consumerOperator, 1, 1, 0);
        testHarness.setTimeCharacteristic(ProcessingTime);
        testHarness.setup();
        // restore state from binary snapshot file
        testHarness.initializeState(OperatorSnapshotUtil.getResourceFilename((("kafka-consumer-migration-test-flink" + (testMigrateVersion)) + "-snapshot")));
        testHarness.open();
        // assert that there are partitions and is identical to expected list
        Assert.assertTrue(((consumerFunction.getSubscribedPartitionsToStartOffsets()) != null));
        Assert.assertTrue((!(consumerFunction.getSubscribedPartitionsToStartOffsets().isEmpty())));
        // on restore, subscribedPartitionsToStartOffsets should be identical to the restored state
        Assert.assertEquals(FlinkKafkaConsumerBaseMigrationTest.PARTITION_STATE, consumerFunction.getSubscribedPartitionsToStartOffsets());
        // assert that state is correctly restored from legacy checkpoint
        Assert.assertTrue(((consumerFunction.getRestoredState()) != null));
        Assert.assertEquals(FlinkKafkaConsumerBaseMigrationTest.PARTITION_STATE, consumerFunction.getRestoredState());
        consumerOperator.close();
        consumerOperator.cancel();
    }

    /**
     * Test restoring from savepoints before version Flink 1.3 should fail if discovery is enabled.
     */
    @Test
    public void testRestoreFailsWithNonEmptyPreFlink13StatesIfDiscoveryEnabled() throws Exception {
        Assume.assumeTrue((((testMigrateVersion) == (MigrationVersion.v1_3)) || ((testMigrateVersion) == (MigrationVersion.v1_2))));
        final List<KafkaTopicPartition> partitions = new java.util.ArrayList(FlinkKafkaConsumerBaseMigrationTest.PARTITION_STATE.keySet());
        final FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer<String> consumerFunction = new FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer(FlinkKafkaConsumerBaseMigrationTest.TOPICS, partitions, 1000L);// discovery enabled

        StreamSource<String, FlinkKafkaConsumerBaseMigrationTest.DummyFlinkKafkaConsumer<String>> consumerOperator = new StreamSource(consumerFunction);
        final AbstractStreamOperatorTestHarness<String> testHarness = new AbstractStreamOperatorTestHarness(consumerOperator, 1, 1, 0);
        testHarness.setTimeCharacteristic(ProcessingTime);
        testHarness.setup();
        // restore state from binary snapshot file; should fail since discovery is enabled
        try {
            testHarness.initializeState(OperatorSnapshotUtil.getResourceFilename((("kafka-consumer-migration-test-flink" + (testMigrateVersion)) + "-snapshot")));
            Assert.fail("Restore from savepoints from version before Flink 1.3.x should have failed if discovery is enabled.");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IllegalArgumentException));
        }
    }

    // ------------------------------------------------------------------------
    private static class DummyFlinkKafkaConsumer<T> extends FlinkKafkaConsumerBase<T> {
        private static final long serialVersionUID = 1L;

        private final List<KafkaTopicPartition> partitions;

        private final AbstractFetcher<T, ?> fetcher;

        @SuppressWarnings("unchecked")
        DummyFlinkKafkaConsumer(AbstractFetcher<T, ?> fetcher, List<String> topics, List<KafkaTopicPartition> partitions, long discoveryInterval) {
            super(topics, null, ((KafkaDeserializationSchema<T>) (Mockito.mock(KafkaDeserializationSchema.class))), discoveryInterval, false);
            this.fetcher = fetcher;
            this.partitions = partitions;
        }

        DummyFlinkKafkaConsumer(List<String> topics, List<KafkaTopicPartition> partitions, long discoveryInterval) {
            this(Mockito.mock(AbstractFetcher.class), topics, partitions, discoveryInterval);
        }

        @Override
        protected AbstractFetcher<T, ?> createFetcher(SourceContext<T> sourceContext, Map<KafkaTopicPartition, Long> thisSubtaskPartitionsWithStartOffsets, SerializedValue<AssignerWithPeriodicWatermarks<T>> watermarksPeriodic, SerializedValue<AssignerWithPunctuatedWatermarks<T>> watermarksPunctuated, StreamingRuntimeContext runtimeContext, OffsetCommitMode offsetCommitMode, MetricGroup consumerMetricGroup, boolean useMetrics) throws Exception {
            return fetcher;
        }

        @Override
        protected AbstractPartitionDiscoverer createPartitionDiscoverer(KafkaTopicsDescriptor topicsDescriptor, int indexOfThisSubtask, int numParallelSubtasks) {
            AbstractPartitionDiscoverer mockPartitionDiscoverer = Mockito.mock(AbstractPartitionDiscoverer.class);
            try {
                when(mockPartitionDiscoverer.discoverPartitions()).thenReturn(partitions);
            } catch (Exception e) {
                // ignore
            }
            when(mockPartitionDiscoverer.setAndCheckDiscoveredPartition(ArgumentMatchers.any(KafkaTopicPartition.class))).thenReturn(true);
            return mockPartitionDiscoverer;
        }

        @Override
        protected boolean getIsAutoCommitEnabled() {
            return false;
        }

        @Override
        protected Map<KafkaTopicPartition, Long> fetchOffsetsWithTimestamp(Collection<KafkaTopicPartition> partitions, long timestamp) {
            throw new UnsupportedOperationException();
        }
    }

    private abstract static class DummySourceContext implements SourceFunction.SourceContext<String> {
        private final Object lock = new Object();

        @Override
        public void collectWithTimestamp(String element, long timestamp) {
        }

        @Override
        public void emitWatermark(Watermark mark) {
        }

        @Override
        public Object getCheckpointLock() {
            return lock;
        }

        @Override
        public void close() {
        }

        @Override
        public void markAsTemporarilyIdle() {
        }
    }
}

