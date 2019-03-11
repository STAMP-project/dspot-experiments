/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.connectors.kinesis;


import SentinelSequenceNumber.SENTINEL_EARLIEST_SEQUENCE_NUM;
import com.amazonaws.services.kinesis.model.SequenceNumberRange;
import com.amazonaws.services.kinesis.model.Shard;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.operators.StreamSource;
import org.apache.flink.streaming.connectors.kinesis.internals.KinesisDataFetcher;
import org.apache.flink.streaming.connectors.kinesis.model.KinesisStreamShardState;
import org.apache.flink.streaming.connectors.kinesis.model.SequenceNumber;
import org.apache.flink.streaming.connectors.kinesis.model.StreamShardHandle;
import org.apache.flink.streaming.connectors.kinesis.model.StreamShardMetadata;
import org.apache.flink.streaming.connectors.kinesis.serialization.KinesisDeserializationSchema;
import org.apache.flink.streaming.connectors.kinesis.testutils.KinesisShardIdGenerator;
import org.apache.flink.streaming.connectors.kinesis.testutils.TestRuntimeContext;
import org.apache.flink.streaming.connectors.kinesis.testutils.TestSourceContext;
import org.apache.flink.streaming.connectors.kinesis.testutils.TestUtils;
import org.apache.flink.streaming.util.AbstractStreamOperatorTestHarness;
import org.apache.flink.streaming.util.OperatorSnapshotUtil;
import org.apache.flink.testutils.migration.MigrationVersion;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for checking whether {@link FlinkKinesisConsumer} can restore from snapshots that were
 * done using an older {@code FlinkKinesisConsumer}.
 *
 * <p>For regenerating the binary snapshot files run {@link #writeSnapshot()} on the corresponding
 * Flink release-* branch.
 */
@RunWith(Parameterized.class)
public class FlinkKinesisConsumerMigrationTest {
    /**
     * TODO change this to the corresponding savepoint version to be written (e.g. {@link MigrationVersion#v1_3} for 1.3)
     * TODO and remove all @Ignore annotations on the writeSnapshot() method to generate savepoints
     * TODO Note: You should generate the savepoint based on the release branch instead of the master.
     */
    private final MigrationVersion flinkGenerateSavepointVersion = null;

    private static final String TEST_STREAM_NAME = "fakeStream1";

    private static final SequenceNumber TEST_SEQUENCE_NUMBER = new SequenceNumber("987654321");

    private static final String TEST_SHARD_ID = KinesisShardIdGenerator.generateFromShardOrder(0);

    private static final HashMap<StreamShardMetadata, SequenceNumber> TEST_STATE = new HashMap<>();

    static {
        StreamShardMetadata shardMetadata = new StreamShardMetadata();
        shardMetadata.setStreamName(FlinkKinesisConsumerMigrationTest.TEST_STREAM_NAME);
        shardMetadata.setShardId(FlinkKinesisConsumerMigrationTest.TEST_SHARD_ID);
        FlinkKinesisConsumerMigrationTest.TEST_STATE.put(shardMetadata, FlinkKinesisConsumerMigrationTest.TEST_SEQUENCE_NUMBER);
    }

    private final MigrationVersion testMigrateVersion;

    public FlinkKinesisConsumerMigrationTest(MigrationVersion testMigrateVersion) {
        this.testMigrateVersion = testMigrateVersion;
    }

    @Test
    public void testRestoreWithEmptyState() throws Exception {
        final List<StreamShardHandle> initialDiscoveryShards = new java.util.ArrayList(FlinkKinesisConsumerMigrationTest.TEST_STATE.size());
        for (StreamShardMetadata shardMetadata : FlinkKinesisConsumerMigrationTest.TEST_STATE.keySet()) {
            Shard shard = new Shard();
            shard.setShardId(shardMetadata.getShardId());
            SequenceNumberRange sequenceNumberRange = new SequenceNumberRange();
            sequenceNumberRange.withStartingSequenceNumber("1");
            shard.setSequenceNumberRange(sequenceNumberRange);
            initialDiscoveryShards.add(new StreamShardHandle(shardMetadata.getStreamName(), shard));
        }
        final FlinkKinesisConsumerMigrationTest.TestFetcher<String> fetcher = new FlinkKinesisConsumerMigrationTest.TestFetcher(Collections.singletonList(FlinkKinesisConsumerMigrationTest.TEST_STREAM_NAME), new TestSourceContext(), new TestRuntimeContext(true, 1, 0), TestUtils.getStandardProperties(), new org.apache.flink.streaming.connectors.kinesis.serialization.KinesisDeserializationSchemaWrapper(new SimpleStringSchema()), null, initialDiscoveryShards);
        final FlinkKinesisConsumerMigrationTest.DummyFlinkKinesisConsumer<String> consumerFunction = new FlinkKinesisConsumerMigrationTest.DummyFlinkKinesisConsumer(fetcher, new org.apache.flink.streaming.connectors.kinesis.serialization.KinesisDeserializationSchemaWrapper(new SimpleStringSchema()));
        StreamSource<String, FlinkKinesisConsumerMigrationTest.DummyFlinkKinesisConsumer<String>> consumerOperator = new StreamSource(consumerFunction);
        final AbstractStreamOperatorTestHarness<String> testHarness = new AbstractStreamOperatorTestHarness(consumerOperator, 1, 1, 0);
        testHarness.setup();
        testHarness.initializeState(OperatorSnapshotUtil.getResourceFilename((("kinesis-consumer-migration-test-flink" + (testMigrateVersion)) + "-empty-snapshot")));
        testHarness.open();
        consumerFunction.run(new TestSourceContext());
        // assert that no state was restored
        Assert.assertTrue(consumerFunction.getRestoredState().isEmpty());
        // although the restore state is empty, the fetcher should still have been registered the initial discovered shard;
        // furthermore, the discovered shard should be considered a newly created shard while the job wasn't running,
        // and therefore should be consumed from the earliest sequence number
        KinesisStreamShardState restoredShardState = fetcher.getSubscribedShardsState().get(0);
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_STREAM_NAME, restoredShardState.getStreamShardHandle().getStreamName());
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_SHARD_ID, restoredShardState.getStreamShardHandle().getShard().getShardId());
        Assert.assertFalse(restoredShardState.getStreamShardHandle().isClosed());
        Assert.assertEquals(SENTINEL_EARLIEST_SEQUENCE_NUM.get(), restoredShardState.getLastProcessedSequenceNum());
        consumerOperator.close();
        consumerOperator.cancel();
    }

    @Test
    public void testRestore() throws Exception {
        final List<StreamShardHandle> initialDiscoveryShards = new java.util.ArrayList(FlinkKinesisConsumerMigrationTest.TEST_STATE.size());
        for (StreamShardMetadata shardMetadata : FlinkKinesisConsumerMigrationTest.TEST_STATE.keySet()) {
            Shard shard = new Shard();
            shard.setShardId(shardMetadata.getShardId());
            SequenceNumberRange sequenceNumberRange = new SequenceNumberRange();
            sequenceNumberRange.withStartingSequenceNumber("1");
            shard.setSequenceNumberRange(sequenceNumberRange);
            initialDiscoveryShards.add(new StreamShardHandle(shardMetadata.getStreamName(), shard));
        }
        final FlinkKinesisConsumerMigrationTest.TestFetcher<String> fetcher = new FlinkKinesisConsumerMigrationTest.TestFetcher(Collections.singletonList(FlinkKinesisConsumerMigrationTest.TEST_STREAM_NAME), new TestSourceContext(), new TestRuntimeContext(true, 1, 0), TestUtils.getStandardProperties(), new org.apache.flink.streaming.connectors.kinesis.serialization.KinesisDeserializationSchemaWrapper(new SimpleStringSchema()), null, initialDiscoveryShards);
        final FlinkKinesisConsumerMigrationTest.DummyFlinkKinesisConsumer<String> consumerFunction = new FlinkKinesisConsumerMigrationTest.DummyFlinkKinesisConsumer(fetcher, new org.apache.flink.streaming.connectors.kinesis.serialization.KinesisDeserializationSchemaWrapper(new SimpleStringSchema()));
        StreamSource<String, FlinkKinesisConsumerMigrationTest.DummyFlinkKinesisConsumer<String>> consumerOperator = new StreamSource(consumerFunction);
        final AbstractStreamOperatorTestHarness<String> testHarness = new AbstractStreamOperatorTestHarness(consumerOperator, 1, 1, 0);
        testHarness.setup();
        testHarness.initializeState(OperatorSnapshotUtil.getResourceFilename((("kinesis-consumer-migration-test-flink" + (testMigrateVersion)) + "-snapshot")));
        testHarness.open();
        consumerFunction.run(new TestSourceContext());
        // assert that state is correctly restored
        Assert.assertNotEquals(null, consumerFunction.getRestoredState());
        Assert.assertEquals(1, consumerFunction.getRestoredState().size());
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_STATE, FlinkKinesisConsumerMigrationTest.removeEquivalenceWrappers(consumerFunction.getRestoredState()));
        Assert.assertEquals(1, fetcher.getSubscribedShardsState().size());
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_SEQUENCE_NUMBER, fetcher.getSubscribedShardsState().get(0).getLastProcessedSequenceNum());
        KinesisStreamShardState restoredShardState = fetcher.getSubscribedShardsState().get(0);
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_STREAM_NAME, restoredShardState.getStreamShardHandle().getStreamName());
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_SHARD_ID, restoredShardState.getStreamShardHandle().getShard().getShardId());
        Assert.assertFalse(restoredShardState.getStreamShardHandle().isClosed());
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_SEQUENCE_NUMBER, restoredShardState.getLastProcessedSequenceNum());
        consumerOperator.close();
        consumerOperator.cancel();
    }

    @Test
    public void testRestoreWithReshardedStream() throws Exception {
        final List<StreamShardHandle> initialDiscoveryShards = new java.util.ArrayList(FlinkKinesisConsumerMigrationTest.TEST_STATE.size());
        for (StreamShardMetadata shardMetadata : FlinkKinesisConsumerMigrationTest.TEST_STATE.keySet()) {
            // setup the closed shard
            Shard closedShard = new Shard();
            closedShard.setShardId(shardMetadata.getShardId());
            SequenceNumberRange closedSequenceNumberRange = new SequenceNumberRange();
            closedSequenceNumberRange.withStartingSequenceNumber("1");
            closedSequenceNumberRange.withEndingSequenceNumber("1087654321");// this represents a closed shard

            closedShard.setSequenceNumberRange(closedSequenceNumberRange);
            initialDiscoveryShards.add(new StreamShardHandle(shardMetadata.getStreamName(), closedShard));
            // setup the new shards
            Shard newSplitShard1 = new Shard();
            newSplitShard1.setShardId(KinesisShardIdGenerator.generateFromShardOrder(1));
            SequenceNumberRange newSequenceNumberRange1 = new SequenceNumberRange();
            newSequenceNumberRange1.withStartingSequenceNumber("1087654322");
            newSplitShard1.setSequenceNumberRange(newSequenceNumberRange1);
            newSplitShard1.setParentShardId(FlinkKinesisConsumerMigrationTest.TEST_SHARD_ID);
            Shard newSplitShard2 = new Shard();
            newSplitShard2.setShardId(KinesisShardIdGenerator.generateFromShardOrder(2));
            SequenceNumberRange newSequenceNumberRange2 = new SequenceNumberRange();
            newSequenceNumberRange2.withStartingSequenceNumber("2087654322");
            newSplitShard2.setSequenceNumberRange(newSequenceNumberRange2);
            newSplitShard2.setParentShardId(FlinkKinesisConsumerMigrationTest.TEST_SHARD_ID);
            initialDiscoveryShards.add(new StreamShardHandle(shardMetadata.getStreamName(), newSplitShard1));
            initialDiscoveryShards.add(new StreamShardHandle(shardMetadata.getStreamName(), newSplitShard2));
        }
        final FlinkKinesisConsumerMigrationTest.TestFetcher<String> fetcher = new FlinkKinesisConsumerMigrationTest.TestFetcher(Collections.singletonList(FlinkKinesisConsumerMigrationTest.TEST_STREAM_NAME), new TestSourceContext(), new TestRuntimeContext(true, 1, 0), TestUtils.getStandardProperties(), new org.apache.flink.streaming.connectors.kinesis.serialization.KinesisDeserializationSchemaWrapper(new SimpleStringSchema()), null, initialDiscoveryShards);
        final FlinkKinesisConsumerMigrationTest.DummyFlinkKinesisConsumer<String> consumerFunction = new FlinkKinesisConsumerMigrationTest.DummyFlinkKinesisConsumer(fetcher, new org.apache.flink.streaming.connectors.kinesis.serialization.KinesisDeserializationSchemaWrapper(new SimpleStringSchema()));
        StreamSource<String, FlinkKinesisConsumerMigrationTest.DummyFlinkKinesisConsumer<String>> consumerOperator = new StreamSource(consumerFunction);
        final AbstractStreamOperatorTestHarness<String> testHarness = new AbstractStreamOperatorTestHarness(consumerOperator, 1, 1, 0);
        testHarness.setup();
        testHarness.initializeState(OperatorSnapshotUtil.getResourceFilename((("kinesis-consumer-migration-test-flink" + (testMigrateVersion)) + "-snapshot")));
        testHarness.open();
        consumerFunction.run(new TestSourceContext());
        // assert that state is correctly restored
        Assert.assertNotEquals(null, consumerFunction.getRestoredState());
        Assert.assertEquals(1, consumerFunction.getRestoredState().size());
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_STATE, FlinkKinesisConsumerMigrationTest.removeEquivalenceWrappers(consumerFunction.getRestoredState()));
        // assert that the fetcher is registered with all shards, including new shards
        Assert.assertEquals(3, fetcher.getSubscribedShardsState().size());
        KinesisStreamShardState restoredClosedShardState = fetcher.getSubscribedShardsState().get(0);
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_STREAM_NAME, restoredClosedShardState.getStreamShardHandle().getStreamName());
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_SHARD_ID, restoredClosedShardState.getStreamShardHandle().getShard().getShardId());
        Assert.assertTrue(restoredClosedShardState.getStreamShardHandle().isClosed());
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_SEQUENCE_NUMBER, restoredClosedShardState.getLastProcessedSequenceNum());
        KinesisStreamShardState restoredNewSplitShard1 = fetcher.getSubscribedShardsState().get(1);
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_STREAM_NAME, restoredNewSplitShard1.getStreamShardHandle().getStreamName());
        Assert.assertEquals(KinesisShardIdGenerator.generateFromShardOrder(1), restoredNewSplitShard1.getStreamShardHandle().getShard().getShardId());
        Assert.assertFalse(restoredNewSplitShard1.getStreamShardHandle().isClosed());
        // new shards should be consumed from the beginning
        Assert.assertEquals(SENTINEL_EARLIEST_SEQUENCE_NUM.get(), restoredNewSplitShard1.getLastProcessedSequenceNum());
        KinesisStreamShardState restoredNewSplitShard2 = fetcher.getSubscribedShardsState().get(2);
        Assert.assertEquals(FlinkKinesisConsumerMigrationTest.TEST_STREAM_NAME, restoredNewSplitShard2.getStreamShardHandle().getStreamName());
        Assert.assertEquals(KinesisShardIdGenerator.generateFromShardOrder(2), restoredNewSplitShard2.getStreamShardHandle().getShard().getShardId());
        Assert.assertFalse(restoredNewSplitShard2.getStreamShardHandle().isClosed());
        // new shards should be consumed from the beginning
        Assert.assertEquals(SENTINEL_EARLIEST_SEQUENCE_NUM.get(), restoredNewSplitShard2.getLastProcessedSequenceNum());
        consumerOperator.close();
        consumerOperator.cancel();
    }

    private static class DummyFlinkKinesisConsumer<T> extends FlinkKinesisConsumer<T> {
        private static final long serialVersionUID = -1573896262106029446L;

        private KinesisDataFetcher<T> mockFetcher;

        private static Properties dummyConfig = TestUtils.getStandardProperties();

        DummyFlinkKinesisConsumer(KinesisDataFetcher<T> mockFetcher, KinesisDeserializationSchema<T> schema) {
            super(FlinkKinesisConsumerMigrationTest.TEST_STREAM_NAME, schema, FlinkKinesisConsumerMigrationTest.DummyFlinkKinesisConsumer.dummyConfig);
            this.mockFetcher = mockFetcher;
        }

        @Override
        protected KinesisDataFetcher<T> createFetcher(List<String> streams, SourceContext<T> sourceContext, RuntimeContext runtimeContext, Properties configProps, KinesisDeserializationSchema<T> deserializer) {
            return mockFetcher;
        }
    }

    private static class TestFetcher<T> extends KinesisDataFetcher<T> {
        final OneShotLatch runLatch = new OneShotLatch();

        final HashMap<StreamShardMetadata, SequenceNumber> testStateSnapshot;

        final List<StreamShardHandle> testInitialDiscoveryShards;

        public TestFetcher(List<String> streams, SourceFunction.SourceContext<T> sourceContext, RuntimeContext runtimeContext, Properties configProps, KinesisDeserializationSchema<T> deserializationSchema, HashMap<StreamShardMetadata, SequenceNumber> testStateSnapshot, List<StreamShardHandle> testInitialDiscoveryShards) {
            super(streams, sourceContext, runtimeContext, configProps, deserializationSchema, DEFAULT_SHARD_ASSIGNER, null);
            this.testStateSnapshot = testStateSnapshot;
            this.testInitialDiscoveryShards = testInitialDiscoveryShards;
        }

        @Override
        public void runFetcher() throws Exception {
            runLatch.trigger();
        }

        @Override
        public HashMap<StreamShardMetadata, SequenceNumber> snapshotState() {
            return testStateSnapshot;
        }

        public void waitUntilRun() throws InterruptedException {
            runLatch.await();
        }

        @Override
        public List<StreamShardHandle> discoverNewShardsToSubscribe() throws InterruptedException {
            return testInitialDiscoveryShards;
        }

        @Override
        public void awaitTermination() throws InterruptedException {
            // do nothing
        }
    }
}

