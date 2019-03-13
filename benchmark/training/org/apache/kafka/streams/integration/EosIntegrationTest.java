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
package org.apache.kafka.streams.integration;


import ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import ConsumerConfig.ISOLATION_LEVEL_CONFIG;
import ConsumerConfig.METADATA_MAX_AGE_CONFIG;
import IsolationLevel.READ_COMMITTED;
import Serdes.LongSerde;
import StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG;
import StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;
import StreamsConfig.EXACTLY_ONCE;
import StreamsConfig.PROCESSING_GUARANTEE_CONFIG;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.StreamsTestUtils;
import org.apache.kafka.test.TestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class EosIntegrationTest {
    private static final int NUM_BROKERS = 3;

    private static final int MAX_POLL_INTERVAL_MS = 5 * 1000;

    private static final int MAX_WAIT_TIME_MS = 60 * 1000;

    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(EosIntegrationTest.NUM_BROKERS, Utils.mkProperties(Collections.singletonMap("auto.create.topics.enable", "false")));

    private static String applicationId;

    private static final int NUM_TOPIC_PARTITIONS = 2;

    private static final String CONSUMER_GROUP_ID = "readCommitted";

    private static final String SINGLE_PARTITION_INPUT_TOPIC = "singlePartitionInputTopic";

    private static final String SINGLE_PARTITION_THROUGH_TOPIC = "singlePartitionThroughTopic";

    private static final String SINGLE_PARTITION_OUTPUT_TOPIC = "singlePartitionOutputTopic";

    private static final String MULTI_PARTITION_INPUT_TOPIC = "multiPartitionInputTopic";

    private static final String MULTI_PARTITION_THROUGH_TOPIC = "multiPartitionThroughTopic";

    private static final String MULTI_PARTITION_OUTPUT_TOPIC = "multiPartitionOutputTopic";

    private final String storeName = "store";

    private AtomicBoolean errorInjected;

    private AtomicBoolean gcInjected;

    private volatile boolean doGC = true;

    private AtomicInteger commitRequested;

    private Throwable uncaughtException;

    private int testNumber = 0;

    @Test
    public void shouldBeAbleToRunWithEosEnabled() throws Exception {
        runSimpleCopyTest(1, EosIntegrationTest.SINGLE_PARTITION_INPUT_TOPIC, null, EosIntegrationTest.SINGLE_PARTITION_OUTPUT_TOPIC);
    }

    @Test
    public void shouldBeAbleToRestartAfterClose() throws Exception {
        runSimpleCopyTest(2, EosIntegrationTest.SINGLE_PARTITION_INPUT_TOPIC, null, EosIntegrationTest.SINGLE_PARTITION_OUTPUT_TOPIC);
    }

    @Test
    public void shouldBeAbleToCommitToMultiplePartitions() throws Exception {
        runSimpleCopyTest(1, EosIntegrationTest.SINGLE_PARTITION_INPUT_TOPIC, null, EosIntegrationTest.MULTI_PARTITION_OUTPUT_TOPIC);
    }

    @Test
    public void shouldBeAbleToCommitMultiplePartitionOffsets() throws Exception {
        runSimpleCopyTest(1, EosIntegrationTest.MULTI_PARTITION_INPUT_TOPIC, null, EosIntegrationTest.SINGLE_PARTITION_OUTPUT_TOPIC);
    }

    @Test
    public void shouldBeAbleToRunWithTwoSubtopologies() throws Exception {
        runSimpleCopyTest(1, EosIntegrationTest.SINGLE_PARTITION_INPUT_TOPIC, EosIntegrationTest.SINGLE_PARTITION_THROUGH_TOPIC, EosIntegrationTest.SINGLE_PARTITION_OUTPUT_TOPIC);
    }

    @Test
    public void shouldBeAbleToRunWithTwoSubtopologiesAndMultiplePartitions() throws Exception {
        runSimpleCopyTest(1, EosIntegrationTest.MULTI_PARTITION_INPUT_TOPIC, EosIntegrationTest.MULTI_PARTITION_THROUGH_TOPIC, EosIntegrationTest.MULTI_PARTITION_OUTPUT_TOPIC);
    }

    @Test
    public void shouldBeAbleToPerformMultipleTransactions() throws Exception {
        final StreamsBuilder builder = new StreamsBuilder();
        builder.stream(EosIntegrationTest.SINGLE_PARTITION_INPUT_TOPIC).to(EosIntegrationTest.SINGLE_PARTITION_OUTPUT_TOPIC);
        final Properties properties = new Properties();
        properties.put(PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE);
        properties.put(CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        properties.put(COMMIT_INTERVAL_MS_CONFIG, 100);
        properties.put(METADATA_MAX_AGE_CONFIG, "1000");
        properties.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        final Properties config = StreamsTestUtils.getStreamsConfig(EosIntegrationTest.applicationId, EosIntegrationTest.CLUSTER.bootstrapServers(), LongSerde.class.getName(), LongSerde.class.getName(), properties);
        try (final KafkaStreams streams = new KafkaStreams(builder.build(), config)) {
            streams.start();
            final List<KeyValue<Long, Long>> firstBurstOfData = prepareData(0L, 5L, 0L);
            final List<KeyValue<Long, Long>> secondBurstOfData = prepareData(5L, 8L, 0L);
            IntegrationTestUtils.produceKeyValuesSynchronously(EosIntegrationTest.SINGLE_PARTITION_INPUT_TOPIC, firstBurstOfData, TestUtils.producerConfig(EosIntegrationTest.CLUSTER.bootstrapServers(), LongSerializer.class, LongSerializer.class), EosIntegrationTest.CLUSTER.time);
            final List<KeyValue<Long, Long>> firstCommittedRecords = IntegrationTestUtils.waitUntilMinKeyValueRecordsReceived(TestUtils.consumerConfig(EosIntegrationTest.CLUSTER.bootstrapServers(), EosIntegrationTest.CONSUMER_GROUP_ID, LongDeserializer.class, LongDeserializer.class, Utils.mkProperties(Collections.singletonMap(ISOLATION_LEVEL_CONFIG, READ_COMMITTED.name().toLowerCase(Locale.ROOT)))), EosIntegrationTest.SINGLE_PARTITION_OUTPUT_TOPIC, firstBurstOfData.size());
            MatcherAssert.assertThat(firstCommittedRecords, CoreMatchers.equalTo(firstBurstOfData));
            IntegrationTestUtils.produceKeyValuesSynchronously(EosIntegrationTest.SINGLE_PARTITION_INPUT_TOPIC, secondBurstOfData, TestUtils.producerConfig(EosIntegrationTest.CLUSTER.bootstrapServers(), LongSerializer.class, LongSerializer.class), EosIntegrationTest.CLUSTER.time);
            final List<KeyValue<Long, Long>> secondCommittedRecords = IntegrationTestUtils.waitUntilMinKeyValueRecordsReceived(TestUtils.consumerConfig(EosIntegrationTest.CLUSTER.bootstrapServers(), EosIntegrationTest.CONSUMER_GROUP_ID, LongDeserializer.class, LongDeserializer.class, Utils.mkProperties(Collections.singletonMap(ISOLATION_LEVEL_CONFIG, READ_COMMITTED.name().toLowerCase(Locale.ROOT)))), EosIntegrationTest.SINGLE_PARTITION_OUTPUT_TOPIC, secondBurstOfData.size());
            MatcherAssert.assertThat(secondCommittedRecords, CoreMatchers.equalTo(secondBurstOfData));
        }
    }

    @Test
    public void shouldNotViolateEosIfOneTaskFails() throws Exception {
        // this test writes 10 + 5 + 5 records per partition (running with 2 partitions)
        // the app is supposed to copy all 40 records into the output topic
        // the app commits after each 10 records per partition, and thus will have 2*5 uncommitted writes
        // 
        // the failure gets inject after 20 committed and 30 uncommitted records got received
        // -> the failure only kills one thread
        // after fail over, we should read 40 committed records (even if 50 record got written)
        try (final KafkaStreams streams = getKafkaStreams(false, "appDir", 2)) {
            streams.start();
            final List<KeyValue<Long, Long>> committedDataBeforeFailure = prepareData(0L, 10L, 0L, 1L);
            final List<KeyValue<Long, Long>> uncommittedDataBeforeFailure = prepareData(10L, 15L, 0L, 1L);
            final List<KeyValue<Long, Long>> dataBeforeFailure = new ArrayList<>();
            dataBeforeFailure.addAll(committedDataBeforeFailure);
            dataBeforeFailure.addAll(uncommittedDataBeforeFailure);
            final List<KeyValue<Long, Long>> dataAfterFailure = prepareData(15L, 20L, 0L, 1L);
            writeInputData(committedDataBeforeFailure);
            TestUtils.waitForCondition(() -> (commitRequested.get()) == 2, EosIntegrationTest.MAX_WAIT_TIME_MS, "SteamsTasks did not request commit.");
            writeInputData(uncommittedDataBeforeFailure);
            final List<KeyValue<Long, Long>> uncommittedRecords = readResult(dataBeforeFailure.size(), null);
            final List<KeyValue<Long, Long>> committedRecords = readResult(committedDataBeforeFailure.size(), EosIntegrationTest.CONSUMER_GROUP_ID);
            checkResultPerKey(committedRecords, committedDataBeforeFailure);
            checkResultPerKey(uncommittedRecords, dataBeforeFailure);
            errorInjected.set(true);
            writeInputData(dataAfterFailure);
            TestUtils.waitForCondition(() -> (uncaughtException) != null, EosIntegrationTest.MAX_WAIT_TIME_MS, "Should receive uncaught exception from one StreamThread.");
            final List<KeyValue<Long, Long>> allCommittedRecords = readResult((((committedDataBeforeFailure.size()) + (uncommittedDataBeforeFailure.size())) + (dataAfterFailure.size())), ((EosIntegrationTest.CONSUMER_GROUP_ID) + "_ALL"));
            final List<KeyValue<Long, Long>> committedRecordsAfterFailure = readResult(((uncommittedDataBeforeFailure.size()) + (dataAfterFailure.size())), EosIntegrationTest.CONSUMER_GROUP_ID);
            final List<KeyValue<Long, Long>> allExpectedCommittedRecordsAfterRecovery = new ArrayList<>();
            allExpectedCommittedRecordsAfterRecovery.addAll(committedDataBeforeFailure);
            allExpectedCommittedRecordsAfterRecovery.addAll(uncommittedDataBeforeFailure);
            allExpectedCommittedRecordsAfterRecovery.addAll(dataAfterFailure);
            final List<KeyValue<Long, Long>> expectedCommittedRecordsAfterRecovery = new ArrayList<>();
            expectedCommittedRecordsAfterRecovery.addAll(uncommittedDataBeforeFailure);
            expectedCommittedRecordsAfterRecovery.addAll(dataAfterFailure);
            checkResultPerKey(allCommittedRecords, allExpectedCommittedRecordsAfterRecovery);
            checkResultPerKey(committedRecordsAfterFailure, expectedCommittedRecordsAfterRecovery);
        }
    }

    @Test
    public void shouldNotViolateEosIfOneTaskFailsWithState() throws Exception {
        // this test updates a store with 10 + 5 + 5 records per partition (running with 2 partitions)
        // the app is supposed to emit all 40 update records into the output topic
        // the app commits after each 10 records per partition, and thus will have 2*5 uncommitted writes
        // and store updates (ie, another 5 uncommitted writes to a changelog topic per partition)
        // in the uncommitted batch sending some data for the new key to validate that upon resuming they will not be shown up in the store
        // 
        // the failure gets inject after 20 committed and 10 uncommitted records got received
        // -> the failure only kills one thread
        // after fail over, we should read 40 committed records and the state stores should contain the correct sums
        // per key (even if some records got processed twice)
        try (final KafkaStreams streams = getKafkaStreams(true, "appDir", 2)) {
            streams.start();
            final List<KeyValue<Long, Long>> committedDataBeforeFailure = prepareData(0L, 10L, 0L, 1L);
            final List<KeyValue<Long, Long>> uncommittedDataBeforeFailure = prepareData(10L, 15L, 0L, 1L, 2L, 3L);
            final List<KeyValue<Long, Long>> dataBeforeFailure = new ArrayList<>();
            dataBeforeFailure.addAll(committedDataBeforeFailure);
            dataBeforeFailure.addAll(uncommittedDataBeforeFailure);
            final List<KeyValue<Long, Long>> dataAfterFailure = prepareData(15L, 20L, 0L, 1L);
            writeInputData(committedDataBeforeFailure);
            TestUtils.waitForCondition(() -> (commitRequested.get()) == 2, EosIntegrationTest.MAX_WAIT_TIME_MS, "SteamsTasks did not request commit.");
            writeInputData(uncommittedDataBeforeFailure);
            final List<KeyValue<Long, Long>> uncommittedRecords = readResult(dataBeforeFailure.size(), null);
            final List<KeyValue<Long, Long>> committedRecords = readResult(committedDataBeforeFailure.size(), EosIntegrationTest.CONSUMER_GROUP_ID);
            final List<KeyValue<Long, Long>> expectedResultBeforeFailure = computeExpectedResult(dataBeforeFailure);
            checkResultPerKey(committedRecords, computeExpectedResult(committedDataBeforeFailure));
            checkResultPerKey(uncommittedRecords, expectedResultBeforeFailure);
            verifyStateStore(streams, getMaxPerKey(expectedResultBeforeFailure));
            errorInjected.set(true);
            writeInputData(dataAfterFailure);
            TestUtils.waitForCondition(() -> (uncaughtException) != null, EosIntegrationTest.MAX_WAIT_TIME_MS, "Should receive uncaught exception from one StreamThread.");
            final List<KeyValue<Long, Long>> allCommittedRecords = readResult((((committedDataBeforeFailure.size()) + (uncommittedDataBeforeFailure.size())) + (dataAfterFailure.size())), ((EosIntegrationTest.CONSUMER_GROUP_ID) + "_ALL"));
            final List<KeyValue<Long, Long>> committedRecordsAfterFailure = readResult(((uncommittedDataBeforeFailure.size()) + (dataAfterFailure.size())), EosIntegrationTest.CONSUMER_GROUP_ID);
            final List<KeyValue<Long, Long>> allExpectedCommittedRecordsAfterRecovery = new ArrayList<>();
            allExpectedCommittedRecordsAfterRecovery.addAll(committedDataBeforeFailure);
            allExpectedCommittedRecordsAfterRecovery.addAll(uncommittedDataBeforeFailure);
            allExpectedCommittedRecordsAfterRecovery.addAll(dataAfterFailure);
            final List<KeyValue<Long, Long>> expectedResult = computeExpectedResult(allExpectedCommittedRecordsAfterRecovery);
            checkResultPerKey(allCommittedRecords, expectedResult);
            checkResultPerKey(committedRecordsAfterFailure, expectedResult.subList(committedDataBeforeFailure.size(), expectedResult.size()));
            verifyStateStore(streams, getMaxPerKey(expectedResult));
        }
    }

    @Test
    public void shouldNotViolateEosIfOneTaskGetsFencedUsingIsolatedAppInstances() throws Exception {
        // this test writes 10 + 5 + 5 + 10 records per partition (running with 2 partitions)
        // the app is supposed to copy all 60 records into the output topic
        // the app commits after each 10 records per partition, and thus will have 2*5 uncommitted writes
        // 
        // a GC pause gets inject after 20 committed and 30 uncommitted records got received
        // -> the GC pause only affects one thread and should trigger a rebalance
        // after rebalancing, we should read 40 committed records (even if 50 record got written)
        // 
        // afterwards, the "stalling" thread resumes, and another rebalance should get triggered
        // we write the remaining 20 records and verify to read 60 result records
        try (final KafkaStreams streams1 = getKafkaStreams(false, "appDir1", 1);final KafkaStreams streams2 = getKafkaStreams(false, "appDir2", 1)) {
            streams1.start();
            streams2.start();
            final List<KeyValue<Long, Long>> committedDataBeforeGC = prepareData(0L, 10L, 0L, 1L);
            final List<KeyValue<Long, Long>> uncommittedDataBeforeGC = prepareData(10L, 15L, 0L, 1L);
            final List<KeyValue<Long, Long>> dataBeforeGC = new ArrayList<>();
            dataBeforeGC.addAll(committedDataBeforeGC);
            dataBeforeGC.addAll(uncommittedDataBeforeGC);
            final List<KeyValue<Long, Long>> dataToTriggerFirstRebalance = prepareData(15L, 20L, 0L, 1L);
            final List<KeyValue<Long, Long>> dataAfterSecondRebalance = prepareData(20L, 30L, 0L, 1L);
            writeInputData(committedDataBeforeGC);
            TestUtils.waitForCondition(() -> (commitRequested.get()) == 2, EosIntegrationTest.MAX_WAIT_TIME_MS, "SteamsTasks did not request commit.");
            writeInputData(uncommittedDataBeforeGC);
            final List<KeyValue<Long, Long>> uncommittedRecords = readResult(dataBeforeGC.size(), null);
            final List<KeyValue<Long, Long>> committedRecords = readResult(committedDataBeforeGC.size(), EosIntegrationTest.CONSUMER_GROUP_ID);
            checkResultPerKey(committedRecords, committedDataBeforeGC);
            checkResultPerKey(uncommittedRecords, dataBeforeGC);
            gcInjected.set(true);
            writeInputData(dataToTriggerFirstRebalance);
            TestUtils.waitForCondition(() -> (((streams1.allMetadata().size()) == 1) && ((streams2.allMetadata().size()) == 1)) && (((streams1.allMetadata().iterator().next().topicPartitions().size()) == 2) || ((streams2.allMetadata().iterator().next().topicPartitions().size()) == 2)), EosIntegrationTest.MAX_WAIT_TIME_MS, "Should have rebalanced.");
            final List<KeyValue<Long, Long>> committedRecordsAfterRebalance = readResult(((uncommittedDataBeforeGC.size()) + (dataToTriggerFirstRebalance.size())), EosIntegrationTest.CONSUMER_GROUP_ID);
            final List<KeyValue<Long, Long>> expectedCommittedRecordsAfterRebalance = new ArrayList<>();
            expectedCommittedRecordsAfterRebalance.addAll(uncommittedDataBeforeGC);
            expectedCommittedRecordsAfterRebalance.addAll(dataToTriggerFirstRebalance);
            checkResultPerKey(committedRecordsAfterRebalance, expectedCommittedRecordsAfterRebalance);
            doGC = false;
            TestUtils.waitForCondition(() -> ((((streams1.allMetadata().size()) == 1) && ((streams2.allMetadata().size()) == 1)) && ((streams1.allMetadata().iterator().next().topicPartitions().size()) == 1)) && ((streams2.allMetadata().iterator().next().topicPartitions().size()) == 1), EosIntegrationTest.MAX_WAIT_TIME_MS, "Should have rebalanced.");
            writeInputData(dataAfterSecondRebalance);
            final List<KeyValue<Long, Long>> allCommittedRecords = readResult(((((committedDataBeforeGC.size()) + (uncommittedDataBeforeGC.size())) + (dataToTriggerFirstRebalance.size())) + (dataAfterSecondRebalance.size())), ((EosIntegrationTest.CONSUMER_GROUP_ID) + "_ALL"));
            final List<KeyValue<Long, Long>> allExpectedCommittedRecordsAfterRecovery = new ArrayList<>();
            allExpectedCommittedRecordsAfterRecovery.addAll(committedDataBeforeGC);
            allExpectedCommittedRecordsAfterRecovery.addAll(uncommittedDataBeforeGC);
            allExpectedCommittedRecordsAfterRecovery.addAll(dataToTriggerFirstRebalance);
            allExpectedCommittedRecordsAfterRecovery.addAll(dataAfterSecondRebalance);
            checkResultPerKey(allCommittedRecords, allExpectedCommittedRecordsAfterRecovery);
        }
    }
}

