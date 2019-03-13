/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.processor.internals;


import StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.ProcessorStateException;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.internals.InternalStreamsBuilder;
import org.apache.kafka.streams.kstream.internals.TimeWindow;
import org.apache.kafka.streams.processor.TaskId;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.internals.OffsetCheckpoint;
import org.apache.kafka.test.MockKeyValueStore;
import org.apache.kafka.test.MockKeyValueStoreBuilder;
import org.apache.kafka.test.MockRestoreConsumer;
import org.apache.kafka.test.MockStateRestoreListener;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import static ProcessorStateManager.CHECKPOINT_FILE_NAME;


public class StandbyTaskTest {
    private final TaskId taskId = new TaskId(0, 1);

    private StandbyTask task;

    private final Serializer<Integer> intSerializer = new IntegerSerializer();

    private final String applicationId = "test-application";

    private final String storeName1 = "store1";

    private final String storeName2 = "store2";

    private final String storeChangelogTopicName1 = ProcessorStateManager.storeChangelogTopic(applicationId, storeName1);

    private final String storeChangelogTopicName2 = ProcessorStateManager.storeChangelogTopic(applicationId, storeName2);

    private final String globalStoreName = "ktable1";

    private final TopicPartition partition1 = new TopicPartition(storeChangelogTopicName1, 1);

    private final TopicPartition partition2 = new TopicPartition(storeChangelogTopicName2, 1);

    private final MockStateRestoreListener stateRestoreListener = new MockStateRestoreListener();

    private final Set<TopicPartition> topicPartitions = Collections.emptySet();

    private final ProcessorTopology topology = ProcessorTopology.withLocalStores(Arrays.asList(new MockKeyValueStoreBuilder(storeName1, false).build(), new MockKeyValueStoreBuilder(storeName2, true).build()), mkMap(mkEntry(storeName1, storeChangelogTopicName1), mkEntry(storeName2, storeChangelogTopicName2)));

    private final TopicPartition globalTopicPartition = new TopicPartition(globalStoreName, 0);

    private final Set<TopicPartition> ktablePartitions = Utils.mkSet(globalTopicPartition);

    private final ProcessorTopology ktableTopology = ProcessorTopology.withLocalStores(Collections.singletonList(withLoggingDisabled().build()), mkMap(mkEntry(globalStoreName, globalTopicPartition.topic())));

    private File baseDir;

    private StateDirectory stateDirectory;

    private final MockConsumer<byte[], byte[]> consumer = new MockConsumer(OffsetResetStrategy.EARLIEST);

    private final MockRestoreConsumer<Integer, Integer> restoreStateConsumer = new MockRestoreConsumer(new IntegerSerializer(), new IntegerSerializer());

    private final StoreChangelogReader changelogReader = new StoreChangelogReader(restoreStateConsumer, Duration.ZERO, stateRestoreListener, new LogContext("standby-task-test "));

    private final byte[] recordValue = intSerializer.serialize(null, 10);

    private final byte[] recordKey = intSerializer.serialize(null, 1);

    @Test
    public void testStorePartitions() throws IOException {
        final StreamsConfig config = createConfig(baseDir);
        task = new StandbyTask(taskId, topicPartitions, topology, consumer, changelogReader, config, null, stateDirectory);
        task.initializeStateStores();
        Assert.assertEquals(Utils.mkSet(partition2, partition1), new java.util.HashSet(task.checkpointedOffsets().keySet()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateNonInitializedStore() throws IOException {
        final StreamsConfig config = createConfig(baseDir);
        task = new StandbyTask(taskId, topicPartitions, topology, consumer, changelogReader, config, null, stateDirectory);
        restoreStateConsumer.assign(new java.util.ArrayList(task.checkpointedOffsets().keySet()));
        try {
            task.update(partition1, Collections.singletonList(new ConsumerRecord(partition1.topic(), partition1.partition(), 10, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue)));
            Assert.fail("expected an exception");
        } catch (final NullPointerException npe) {
            MatcherAssert.assertThat(npe.getMessage(), CoreMatchers.containsString("stateRestoreCallback must not be null"));
        }
    }

    @Test
    public void testUpdate() throws IOException {
        final StreamsConfig config = createConfig(baseDir);
        task = new StandbyTask(taskId, topicPartitions, topology, consumer, changelogReader, config, null, stateDirectory);
        task.initializeStateStores();
        final Set<TopicPartition> partition = Collections.singleton(partition2);
        restoreStateConsumer.assign(partition);
        for (final ConsumerRecord<Integer, Integer> record : Arrays.asList(new ConsumerRecord(partition2.topic(), partition2.partition(), 10, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, 1, 100), new ConsumerRecord(partition2.topic(), partition2.partition(), 20, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, 2, 100), new ConsumerRecord(partition2.topic(), partition2.partition(), 30, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, 3, 100))) {
            restoreStateConsumer.bufferRecord(record);
        }
        restoreStateConsumer.seekToBeginning(partition);
        task.update(partition2, restoreStateConsumer.poll(Duration.ofMillis(100)).records(partition2));
        final StandbyContextImpl context = ((StandbyContextImpl) (task.context()));
        final MockKeyValueStore store1 = ((MockKeyValueStore) (context.getStateMgr().getStore(storeName1)));
        final MockKeyValueStore store2 = ((MockKeyValueStore) (context.getStateMgr().getStore(storeName2)));
        Assert.assertEquals(Collections.emptyList(), store1.keys);
        Assert.assertEquals(Arrays.asList(1, 2, 3), store2.keys);
    }

    @Test
    public void shouldRestoreToWindowedStores() throws IOException {
        final String storeName = "windowed-store";
        final String changelogName = (((applicationId) + "-") + storeName) + "-changelog";
        final TopicPartition topicPartition = new TopicPartition(changelogName, 1);
        final List<TopicPartition> partitions = Collections.singletonList(topicPartition);
        consumer.assign(partitions);
        final InternalTopologyBuilder internalTopologyBuilder = new InternalTopologyBuilder().setApplicationId(applicationId);
        final InternalStreamsBuilder builder = new InternalStreamsBuilder(internalTopologyBuilder);
        builder.stream(Collections.singleton("topic"), new org.apache.kafka.streams.kstream.internals.ConsumedInternal()).groupByKey().windowedBy(TimeWindows.of(Duration.ofMillis(60000)).grace(Duration.ofMillis(0L))).count(Materialized.<Object, Long, WindowStore<Bytes, byte[]>>as(storeName).withRetention(Duration.ofMillis(120000L)));
        builder.buildAndOptimizeTopology();
        task = new StandbyTask(taskId, partitions, internalTopologyBuilder.build(0), consumer, new StoreChangelogReader(restoreStateConsumer, Duration.ZERO, stateRestoreListener, new LogContext("standby-task-test ")), createConfig(baseDir), new MockStreamsMetrics(new Metrics()), stateDirectory);
        task.initializeStateStores();
        consumer.commitSync(mkMap(mkEntry(topicPartition, new OffsetAndMetadata(35L))));
        task.commit();
        final List<ConsumerRecord<byte[], byte[]>> remaining1 = task.update(topicPartition, Arrays.asList(makeWindowedConsumerRecord(changelogName, 10, 1, 0L, 60000L), makeWindowedConsumerRecord(changelogName, 20, 2, 60000L, 120000), makeWindowedConsumerRecord(changelogName, 30, 3, 120000L, 180000), makeWindowedConsumerRecord(changelogName, 40, 4, 180000L, 240000)));
        Assert.assertEquals(Arrays.asList(new org.apache.kafka.streams.KeyValue(new org.apache.kafka.streams.kstream.Windowed(1, new TimeWindow(0, 60000)), 100L), new org.apache.kafka.streams.KeyValue(new org.apache.kafka.streams.kstream.Windowed(2, new TimeWindow(60000, 120000)), 100L), new org.apache.kafka.streams.KeyValue(new org.apache.kafka.streams.kstream.Windowed(3, new TimeWindow(120000, 180000)), 100L)), getWindowedStoreContents(storeName, task));
        consumer.commitSync(mkMap(mkEntry(topicPartition, new OffsetAndMetadata(45L))));
        task.commit();
        final List<ConsumerRecord<byte[], byte[]>> remaining2 = task.update(topicPartition, remaining1);
        Assert.assertEquals(Collections.emptyList(), remaining2);
        // the first record's window should have expired.
        Assert.assertEquals(Arrays.asList(new org.apache.kafka.streams.KeyValue(new org.apache.kafka.streams.kstream.Windowed(2, new TimeWindow(60000, 120000)), 100L), new org.apache.kafka.streams.KeyValue(new org.apache.kafka.streams.kstream.Windowed(3, new TimeWindow(120000, 180000)), 100L), new org.apache.kafka.streams.KeyValue(new org.apache.kafka.streams.kstream.Windowed(4, new TimeWindow(180000, 240000)), 100L)), getWindowedStoreContents(storeName, task));
    }

    @Test
    public void shouldWriteCheckpointFile() throws IOException {
        final String storeName = "checkpoint-file-store";
        final String changelogName = (((applicationId) + "-") + storeName) + "-changelog";
        final TopicPartition topicPartition = new TopicPartition(changelogName, 1);
        final List<TopicPartition> partitions = Collections.singletonList(topicPartition);
        final InternalTopologyBuilder internalTopologyBuilder = new InternalTopologyBuilder().setApplicationId(applicationId);
        final InternalStreamsBuilder builder = new InternalStreamsBuilder(internalTopologyBuilder);
        builder.stream(Collections.singleton("topic"), new org.apache.kafka.streams.kstream.internals.ConsumedInternal()).groupByKey().count(Materialized.as(storeName));
        builder.buildAndOptimizeTopology();
        consumer.assign(partitions);
        task = new StandbyTask(taskId, partitions, internalTopologyBuilder.build(0), consumer, changelogReader, createConfig(baseDir), new MockStreamsMetrics(new Metrics()), stateDirectory);
        task.initializeStateStores();
        consumer.commitSync(mkMap(mkEntry(topicPartition, new OffsetAndMetadata(20L))));
        task.commit();
        task.update(topicPartition, Collections.singletonList(makeWindowedConsumerRecord(changelogName, 10, 1, 0L, 60000L)));
        task.suspend();
        task.closeStateManager(true);
        final File taskDir = stateDirectory.directoryForTask(taskId);
        final OffsetCheckpoint checkpoint = new OffsetCheckpoint(new File(taskDir, CHECKPOINT_FILE_NAME));
        final Map<TopicPartition, Long> offsets = checkpoint.read();
        Assert.assertEquals(1, offsets.size());
        Assert.assertEquals(new Long(11L), offsets.get(topicPartition));
    }

    @Test
    public void shouldRestoreToKTable() throws IOException {
        consumer.assign(Collections.singletonList(globalTopicPartition));
        consumer.commitSync(mkMap(mkEntry(globalTopicPartition, new OffsetAndMetadata(0L))));
        task = new StandbyTask(taskId, ktablePartitions, ktableTopology, consumer, changelogReader, createConfig(baseDir), null, stateDirectory);
        task.initializeStateStores();
        // The commit offset is at 0L. Records should not be processed
        List<ConsumerRecord<byte[], byte[]>> remaining = task.update(globalTopicPartition, Arrays.asList(makeConsumerRecord(globalTopicPartition, 10, 1), makeConsumerRecord(globalTopicPartition, 20, 2), makeConsumerRecord(globalTopicPartition, 30, 3), makeConsumerRecord(globalTopicPartition, 40, 4), makeConsumerRecord(globalTopicPartition, 50, 5)));
        Assert.assertEquals(5, remaining.size());
        consumer.commitSync(mkMap(mkEntry(globalTopicPartition, new OffsetAndMetadata(10L))));
        task.commit();// update offset limits

        // The commit offset has not reached, yet.
        remaining = task.update(globalTopicPartition, remaining);
        Assert.assertEquals(5, remaining.size());
        consumer.commitSync(mkMap(mkEntry(globalTopicPartition, new OffsetAndMetadata(11L))));
        task.commit();// update offset limits

        // one record should be processed.
        remaining = task.update(globalTopicPartition, remaining);
        Assert.assertEquals(4, remaining.size());
        consumer.commitSync(mkMap(mkEntry(globalTopicPartition, new OffsetAndMetadata(45L))));
        task.commit();// update offset limits

        // The commit offset is now 45. All record except for the last one should be processed.
        remaining = task.update(globalTopicPartition, remaining);
        Assert.assertEquals(1, remaining.size());
        consumer.commitSync(mkMap(mkEntry(globalTopicPartition, new OffsetAndMetadata(50L))));
        task.commit();// update offset limits

        // The commit offset is now 50. Still the last record remains.
        remaining = task.update(globalTopicPartition, remaining);
        Assert.assertEquals(1, remaining.size());
        consumer.commitSync(mkMap(mkEntry(globalTopicPartition, new OffsetAndMetadata(60L))));
        task.commit();// update offset limits

        // The commit offset is now 60. No record should be left.
        remaining = task.update(globalTopicPartition, remaining);
        Assert.assertEquals(Collections.emptyList(), remaining);
    }

    @Test
    public void shouldInitializeStateStoreWithoutException() throws IOException {
        final InternalStreamsBuilder builder = new InternalStreamsBuilder(new InternalTopologyBuilder());
        builder.stream(Collections.singleton("topic"), new org.apache.kafka.streams.kstream.internals.ConsumedInternal()).groupByKey().count();
        initializeStandbyStores(builder);
    }

    @Test
    public void shouldInitializeWindowStoreWithoutException() throws IOException {
        final InternalStreamsBuilder builder = new InternalStreamsBuilder(new InternalTopologyBuilder());
        builder.stream(Collections.singleton("topic"), new org.apache.kafka.streams.kstream.internals.ConsumedInternal()).groupByKey().windowedBy(TimeWindows.of(Duration.ofMillis(100))).count();
        initializeStandbyStores(builder);
    }

    @Test
    public void shouldCheckpointStoreOffsetsOnCommit() throws IOException {
        consumer.assign(Collections.singletonList(globalTopicPartition));
        final Map<TopicPartition, OffsetAndMetadata> committedOffsets = new HashMap<>();
        committedOffsets.put(new TopicPartition(globalTopicPartition.topic(), globalTopicPartition.partition()), new OffsetAndMetadata(100L));
        consumer.commitSync(committedOffsets);
        restoreStateConsumer.updatePartitions(globalStoreName, Collections.singletonList(new org.apache.kafka.common.PartitionInfo(globalStoreName, 0, Node.noNode(), new Node[0], new Node[0])));
        final TaskId taskId = new TaskId(0, 0);
        final MockTime time = new MockTime();
        final StreamsConfig config = createConfig(baseDir);
        task = new StandbyTask(taskId, ktablePartitions, ktableTopology, consumer, changelogReader, config, null, stateDirectory);
        task.initializeStateStores();
        restoreStateConsumer.assign(new java.util.ArrayList(task.checkpointedOffsets().keySet()));
        final byte[] serializedValue = Serdes.Integer().serializer().serialize("", 1);
        task.update(globalTopicPartition, Collections.singletonList(new ConsumerRecord(globalTopicPartition.topic(), globalTopicPartition.partition(), 50L, serializedValue, serializedValue)));
        time.sleep(config.getLong(COMMIT_INTERVAL_MS_CONFIG));
        task.commit();
        final Map<TopicPartition, Long> checkpoint = new OffsetCheckpoint(new File(stateDirectory.directoryForTask(taskId), CHECKPOINT_FILE_NAME)).read();
        MatcherAssert.assertThat(checkpoint, CoreMatchers.equalTo(Collections.singletonMap(globalTopicPartition, 51L)));
    }

    @Test
    public void shouldCloseStateMangerOnTaskCloseWhenCommitFailed() throws Exception {
        consumer.assign(Collections.singletonList(globalTopicPartition));
        final Map<TopicPartition, OffsetAndMetadata> committedOffsets = new HashMap<>();
        committedOffsets.put(new TopicPartition(globalTopicPartition.topic(), globalTopicPartition.partition()), new OffsetAndMetadata(100L));
        consumer.commitSync(committedOffsets);
        restoreStateConsumer.updatePartitions(globalStoreName, Collections.singletonList(new org.apache.kafka.common.PartitionInfo(globalStoreName, 0, Node.noNode(), new Node[0], new Node[0])));
        final StreamsConfig config = createConfig(baseDir);
        final AtomicBoolean closedStateManager = new AtomicBoolean(false);
        task = new StandbyTask(taskId, ktablePartitions, ktableTopology, consumer, changelogReader, config, null, stateDirectory) {
            @Override
            public void commit() {
                throw new RuntimeException("KABOOM!");
            }

            @Override
            void closeStateManager(final boolean clean) throws ProcessorStateException {
                closedStateManager.set(true);
            }
        };
        task.initializeStateStores();
        try {
            task.close(true, false);
            Assert.fail("should have thrown exception");
        } catch (final Exception e) {
            // expected
            task = null;
        }
        Assert.assertTrue(closedStateManager.get());
    }
}

