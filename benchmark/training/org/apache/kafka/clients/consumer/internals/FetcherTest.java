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
package org.apache.kafka.clients.consumer.internals;


import ApiKeys.API_VERSIONS;
import ApiKeys.FETCH;
import BufferSupplier.NO_CACHING;
import DefaultRecordBatch.RECORD_BATCH_OVERHEAD;
import Errors.BROKER_NOT_AVAILABLE;
import Errors.FENCED_LEADER_EPOCH;
import Errors.INVALID_REQUEST;
import Errors.INVALID_TOPIC_EXCEPTION;
import Errors.LEADER_NOT_AVAILABLE;
import Errors.NONE;
import Errors.NOT_LEADER_FOR_PARTITION;
import Errors.OFFSET_NOT_AVAILABLE;
import Errors.OFFSET_OUT_OF_RANGE;
import Errors.TOPIC_AUTHORIZATION_FAILED;
import Errors.UNKNOWN_LEADER_EPOCH;
import Errors.UNKNOWN_TOPIC_OR_PARTITION;
import Errors.UNSUPPORTED_FOR_MESSAGE_FORMAT;
import FetchRequest.Builder;
import FetchResponse.AbortedTransaction;
import IsolationLevel.READ_COMMITTED;
import IsolationLevel.READ_UNCOMMITTED;
import ListOffsetRequest.EARLIEST_TIMESTAMP;
import ListOffsetRequest.LATEST_TIMESTAMP;
import ListOffsetResponse.PartitionData;
import MemoryRecords.EMPTY;
import MemoryRecords.FilterResult;
import MetadataResponse.PartitionMetadata;
import MetadataResponse.TopicMetadata;
import OffsetResetStrategy.EARLIEST;
import OffsetResetStrategy.LATEST;
import RecordBatch.CURRENT_MAGIC_VALUE;
import RecordBatch.MAGIC_VALUE_V0;
import RecordBatch.MAGIC_VALUE_V2;
import RecordBatch.NO_PARTITION_LEADER_EPOCH;
import RecordBatch.NO_TIMESTAMP;
import TimestampType.CREATE_TIME;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.kafka.clients.ApiVersions;
import org.apache.kafka.clients.ClientDnsLookup;
import org.apache.kafka.clients.ClientRequest;
import org.apache.kafka.clients.FetchSessionHandler;
import org.apache.kafka.clients.MockClient;
import org.apache.kafka.clients.NetworkClient;
import org.apache.kafka.clients.NodeApiVersions;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.clients.consumer.OffsetOutOfRangeException;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.MetricNameTemplate;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.metrics.KafkaMetric;
import org.apache.kafka.common.metrics.MetricConfig;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.metrics.Sensor;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.record.CompressionType;
import org.apache.kafka.common.record.ControlRecordType;
import org.apache.kafka.common.record.DefaultRecordBatch;
import org.apache.kafka.common.record.LegacyRecord;
import org.apache.kafka.common.record.MemoryRecords;
import org.apache.kafka.common.record.MemoryRecordsBuilder;
import org.apache.kafka.common.record.Record;
import org.apache.kafka.common.record.RecordBatch;
import org.apache.kafka.common.record.SimpleRecord;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.common.requests.AbstractRequest;
import org.apache.kafka.common.requests.ApiVersionsResponse;
import org.apache.kafka.common.requests.FetchMetadata;
import org.apache.kafka.common.requests.FetchRequest;
import org.apache.kafka.common.requests.FetchResponse;
import org.apache.kafka.common.requests.IsolationLevel;
import org.apache.kafka.common.requests.ListOffsetRequest;
import org.apache.kafka.common.requests.ListOffsetResponse;
import org.apache.kafka.common.requests.MetadataRequest;
import org.apache.kafka.common.requests.MetadataResponse;
import org.apache.kafka.common.requests.ResponseHeader;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.utils.ByteBufferOutputStream;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.test.DelayedReceive;
import org.apache.kafka.test.MockSelector;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import static BatchRetention.DELETE_EMPTY;


@SuppressWarnings("deprecation")
public class FetcherTest {
    private static final double EPSILON = 1.0E-4;

    private ConsumerRebalanceListener listener = new NoOpConsumerRebalanceListener();

    private String topicName = "test";

    private String groupId = "test-group";

    private final String metricGroup = ("consumer" + (groupId)) + "-fetch-manager-metrics";

    private TopicPartition tp0 = new TopicPartition(topicName, 0);

    private TopicPartition tp1 = new TopicPartition(topicName, 1);

    private TopicPartition tp2 = new TopicPartition(topicName, 2);

    private TopicPartition tp3 = new TopicPartition(topicName, 3);

    private MetadataResponse initialUpdateResponse = TestUtils.metadataUpdateWith(1, Collections.singletonMap(topicName, 4));

    private int minBytes = 1;

    private int maxBytes = Integer.MAX_VALUE;

    private int maxWaitMs = 0;

    private int fetchSize = 1000;

    private long retryBackoffMs = 100;

    private long requestTimeoutMs = 30000;

    private MockTime time = new MockTime(1);

    private SubscriptionState subscriptions;

    private ConsumerMetadata metadata;

    private FetcherMetricsRegistry metricsRegistry;

    private MockClient client;

    private Metrics metrics;

    private ConsumerNetworkClient consumerClient;

    private Fetcher<?, ?> fetcher;

    private MemoryRecords records;

    private MemoryRecords nextRecords;

    private MemoryRecords emptyRecords;

    private MemoryRecords partialRecords;

    private ExecutorService executorService;

    @Test
    public void testFetchNormal() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, NONE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> partitionRecords = fetchedRecords();
        Assert.assertTrue(partitionRecords.containsKey(tp0));
        List<ConsumerRecord<byte[], byte[]>> records = partitionRecords.get(tp0);
        Assert.assertEquals(3, records.size());
        Assert.assertEquals(4L, subscriptions.position(tp0).longValue());// this is the next fetching position

        long offset = 1;
        for (ConsumerRecord<byte[], byte[]> record : records) {
            Assert.assertEquals(offset, record.offset());
            offset += 1;
        }
    }

    @Test
    public void testMissingLeaderEpochInRecords() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V0, CompressionType.NONE, CREATE_TIME, 0L, System.currentTimeMillis(), NO_PARTITION_LEADER_EPOCH);
        builder.append(0L, "key".getBytes(), "1".getBytes());
        builder.append(0L, "key".getBytes(), "2".getBytes());
        MemoryRecords records = builder.build();
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(fullFetchResponse(tp0, records, NONE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> partitionRecords = fetchedRecords();
        Assert.assertTrue(partitionRecords.containsKey(tp0));
        Assert.assertEquals(2, partitionRecords.get(tp0).size());
        for (ConsumerRecord<byte[], byte[]> record : partitionRecords.get(tp0)) {
            Assert.assertEquals(Optional.empty(), record.leaderEpoch());
        }
    }

    @Test
    public void testLeaderEpochInConsumerRecord() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Integer partitionLeaderEpoch = 1;
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, CURRENT_MAGIC_VALUE, CompressionType.NONE, CREATE_TIME, 0L, System.currentTimeMillis(), partitionLeaderEpoch);
        builder.append(0L, "key".getBytes(), partitionLeaderEpoch.toString().getBytes());
        builder.append(0L, "key".getBytes(), partitionLeaderEpoch.toString().getBytes());
        builder.close();
        partitionLeaderEpoch += 7;
        builder = MemoryRecords.builder(buffer, CURRENT_MAGIC_VALUE, CompressionType.NONE, CREATE_TIME, 2L, System.currentTimeMillis(), partitionLeaderEpoch);
        builder.append(0L, "key".getBytes(), partitionLeaderEpoch.toString().getBytes());
        builder.close();
        partitionLeaderEpoch += 5;
        builder = MemoryRecords.builder(buffer, CURRENT_MAGIC_VALUE, CompressionType.NONE, CREATE_TIME, 3L, System.currentTimeMillis(), partitionLeaderEpoch);
        builder.append(0L, "key".getBytes(), partitionLeaderEpoch.toString().getBytes());
        builder.append(0L, "key".getBytes(), partitionLeaderEpoch.toString().getBytes());
        builder.append(0L, "key".getBytes(), partitionLeaderEpoch.toString().getBytes());
        builder.close();
        buffer.flip();
        MemoryRecords records = MemoryRecords.readableRecords(buffer);
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(fullFetchResponse(tp0, records, NONE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> partitionRecords = fetchedRecords();
        Assert.assertTrue(partitionRecords.containsKey(tp0));
        Assert.assertEquals(6, partitionRecords.get(tp0).size());
        for (ConsumerRecord<byte[], byte[]> record : partitionRecords.get(tp0)) {
            int expectedLeaderEpoch = Integer.parseInt(Utils.utf8(record.value()));
            Assert.assertEquals(Optional.of(expectedLeaderEpoch), record.leaderEpoch());
        }
    }

    @Test
    public void testClearBufferedDataForTopicPartitions() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, NONE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Set<TopicPartition> newAssignedTopicPartitions = new HashSet<>();
        newAssignedTopicPartitions.add(tp1);
        fetcher.clearBufferedDataForUnassignedPartitions(newAssignedTopicPartitions);
        Assert.assertFalse(fetcher.hasCompletedFetches());
    }

    @Test
    public void testFetchSkipsBlackedOutNodes() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        client.updateMetadata(initialUpdateResponse);
        Node node = initialUpdateResponse.brokers().iterator().next();
        client.blackout(node, 500);
        Assert.assertEquals(0, fetcher.sendFetches());
        time.sleep(500);
        Assert.assertEquals(1, fetcher.sendFetches());
    }

    @Test
    public void testFetcherIgnoresControlRecords() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        long producerId = 1;
        short producerEpoch = 0;
        int baseSequence = 0;
        int partitionLeaderEpoch = 0;
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MemoryRecordsBuilder builder = MemoryRecords.idempotentBuilder(buffer, CompressionType.NONE, 0L, producerId, producerEpoch, baseSequence);
        builder.append(0L, "key".getBytes(), null);
        builder.close();
        MemoryRecords.writeEndTransactionalMarker(buffer, 1L, time.milliseconds(), partitionLeaderEpoch, producerId, producerEpoch, new org.apache.kafka.common.record.EndTransactionMarker(ControlRecordType.ABORT, 0));
        buffer.flip();
        client.prepareResponse(fullFetchResponse(tp0, MemoryRecords.readableRecords(buffer), NONE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> partitionRecords = fetchedRecords();
        Assert.assertTrue(partitionRecords.containsKey(tp0));
        List<ConsumerRecord<byte[], byte[]>> records = partitionRecords.get(tp0);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(2L, subscriptions.position(tp0).longValue());
        ConsumerRecord<byte[], byte[]> record = records.get(0);
        Assert.assertArrayEquals("key".getBytes(), record.key());
    }

    @Test
    public void testFetchError() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, NOT_LEADER_FOR_PARTITION, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> partitionRecords = fetchedRecords();
        Assert.assertFalse(partitionRecords.containsKey(tp0));
    }

    @Test
    public void testFetchedRecordsRaisesOnSerializationErrors() {
        // raise an exception from somewhere in the middle of the fetch response
        // so that we can verify that our position does not advance after raising
        ByteArrayDeserializer deserializer = new ByteArrayDeserializer() {
            int i = 0;

            @Override
            public byte[] deserialize(String topic, byte[] data) {
                if ((((i)++) % 2) == 1) {
                    // Should be blocked on the value deserialization of the first record.
                    Assert.assertEquals("value-1", new String(data, StandardCharsets.UTF_8));
                    throw new SerializationException();
                }
                return data;
            }
        };
        buildFetcher(deserializer, deserializer);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 1);
        client.prepareResponse(matchesOffset(tp0, 1), fullFetchResponse(tp0, this.records, NONE, 100L, 0));
        Assert.assertEquals(1, fetcher.sendFetches());
        consumerClient.poll(timer(0));
        // The fetcher should block on Deserialization error
        for (int i = 0; i < 2; i++) {
            try {
                fetcher.fetchedRecords();
                Assert.fail("fetchedRecords should have raised");
            } catch (SerializationException e) {
                // the position should not advance since no data has been returned
                Assert.assertEquals(1, subscriptions.position(tp0).longValue());
            }
        }
    }

    @Test
    public void testParseCorruptedRecord() throws Exception {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        DataOutputStream out = new DataOutputStream(new ByteBufferOutputStream(buffer));
        byte magic = RecordBatch.MAGIC_VALUE_V1;
        byte[] key = "foo".getBytes();
        byte[] value = "baz".getBytes();
        long offset = 0;
        long timestamp = 500L;
        int size = LegacyRecord.recordSize(magic, key.length, value.length);
        byte attributes = LegacyRecord.computeAttributes(magic, CompressionType.NONE, CREATE_TIME);
        long crc = LegacyRecord.computeChecksum(magic, attributes, timestamp, key, value);
        // write one valid record
        out.writeLong(offset);
        out.writeInt(size);
        LegacyRecord.write(out, magic, crc, LegacyRecord.computeAttributes(magic, CompressionType.NONE, CREATE_TIME), timestamp, key, value);
        // and one invalid record (note the crc)
        out.writeLong((offset + 1));
        out.writeInt(size);
        LegacyRecord.write(out, magic, (crc + 1), LegacyRecord.computeAttributes(magic, CompressionType.NONE, CREATE_TIME), timestamp, key, value);
        // write one valid record
        out.writeLong((offset + 2));
        out.writeInt(size);
        LegacyRecord.write(out, magic, crc, LegacyRecord.computeAttributes(magic, CompressionType.NONE, CREATE_TIME), timestamp, key, value);
        // Write a record whose size field is invalid.
        out.writeLong((offset + 3));
        out.writeInt(1);
        // write one valid record
        out.writeLong((offset + 4));
        out.writeInt(size);
        LegacyRecord.write(out, magic, crc, LegacyRecord.computeAttributes(magic, CompressionType.NONE, CREATE_TIME), timestamp, key, value);
        buffer.flip();
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, MemoryRecords.readableRecords(buffer), NONE, 100L, 0));
        consumerClient.poll(timer(0));
        // the first fetchedRecords() should return the first valid message
        Assert.assertEquals(1, fetcher.fetchedRecords().get(tp0).size());
        Assert.assertEquals(1, subscriptions.position(tp0).longValue());
        ensureBlockOnRecord(1L);
        seekAndConsumeRecord(buffer, 2L);
        ensureBlockOnRecord(3L);
        try {
            // For a record that cannot be retrieved from the iterator, we cannot seek over it within the batch.
            seekAndConsumeRecord(buffer, 4L);
            Assert.fail("Should have thrown exception when fail to retrieve a record from iterator.");
        } catch (KafkaException ke) {
            // let it go
        }
        ensureBlockOnRecord(4L);
    }

    @Test
    public void testInvalidDefaultRecordBatch() {
        buildFetcher();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteBufferOutputStream out = new ByteBufferOutputStream(buffer);
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(out, DefaultRecordBatch.CURRENT_MAGIC_VALUE, CompressionType.NONE, TimestampType.CREATE_TIME, 0L, 10L, 0L, ((short) (0)), 0, false, false, 0, 1024);
        builder.append(10L, "key".getBytes(), "value".getBytes());
        builder.close();
        buffer.flip();
        // Garble the CRC
        buffer.position(17);
        buffer.put("beef".getBytes());
        buffer.position(0);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, MemoryRecords.readableRecords(buffer), NONE, 100L, 0));
        consumerClient.poll(timer(0));
        // the fetchedRecords() should always throw exception due to the bad batch.
        for (int i = 0; i < 2; i++) {
            try {
                fetcher.fetchedRecords();
                Assert.fail("fetchedRecords should have raised KafkaException");
            } catch (KafkaException e) {
                Assert.assertEquals(0, subscriptions.position(tp0).longValue());
            }
        }
    }

    @Test
    public void testParseInvalidRecordBatch() {
        buildFetcher();
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V2, 0L, CompressionType.NONE, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        ByteBuffer buffer = records.buffer();
        // flip some bits to fail the crc
        buffer.putInt(32, ((buffer.get(32)) ^ 87238423));
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, MemoryRecords.readableRecords(buffer), NONE, 100L, 0));
        consumerClient.poll(timer(0));
        try {
            fetcher.fetchedRecords();
            Assert.fail("fetchedRecords should have raised");
        } catch (KafkaException e) {
            // the position should not advance since no data has been returned
            Assert.assertEquals(0, subscriptions.position(tp0).longValue());
        }
    }

    @Test
    public void testHeaders() {
        buildFetcher();
        MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), CompressionType.NONE, CREATE_TIME, 1L);
        builder.append(0L, "key".getBytes(), "value-1".getBytes());
        Header[] headersArray = new Header[1];
        headersArray[0] = new RecordHeader("headerKey", "headerValue".getBytes(StandardCharsets.UTF_8));
        builder.append(0L, "key".getBytes(), "value-2".getBytes(), headersArray);
        Header[] headersArray2 = new Header[2];
        headersArray2[0] = new RecordHeader("headerKey", "headerValue".getBytes(StandardCharsets.UTF_8));
        headersArray2[1] = new RecordHeader("headerKey", "headerValue2".getBytes(StandardCharsets.UTF_8));
        builder.append(0L, "key".getBytes(), "value-3".getBytes(), headersArray2);
        MemoryRecords memoryRecords = builder.build();
        List<ConsumerRecord<byte[], byte[]>> records;
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 1);
        client.prepareResponse(matchesOffset(tp0, 1), fullFetchResponse(tp0, memoryRecords, NONE, 100L, 0));
        Assert.assertEquals(1, fetcher.sendFetches());
        consumerClient.poll(timer(0));
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> recordsByPartition = fetchedRecords();
        records = recordsByPartition.get(tp0);
        Assert.assertEquals(3, records.size());
        Iterator<ConsumerRecord<byte[], byte[]>> recordIterator = records.iterator();
        ConsumerRecord<byte[], byte[]> record = recordIterator.next();
        Assert.assertNull(record.headers().lastHeader("headerKey"));
        record = recordIterator.next();
        Assert.assertEquals("headerValue", new String(record.headers().lastHeader("headerKey").value(), StandardCharsets.UTF_8));
        Assert.assertEquals("headerKey", record.headers().lastHeader("headerKey").key());
        record = recordIterator.next();
        Assert.assertEquals("headerValue2", new String(record.headers().lastHeader("headerKey").value(), StandardCharsets.UTF_8));
        Assert.assertEquals("headerKey", record.headers().lastHeader("headerKey").key());
    }

    @Test
    public void testFetchMaxPollRecords() {
        buildFetcher(2);
        List<ConsumerRecord<byte[], byte[]>> records;
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 1);
        client.prepareResponse(matchesOffset(tp0, 1), fullFetchResponse(tp0, this.records, NONE, 100L, 0));
        client.prepareResponse(matchesOffset(tp0, 4), fullFetchResponse(tp0, this.nextRecords, NONE, 100L, 0));
        Assert.assertEquals(1, fetcher.sendFetches());
        consumerClient.poll(timer(0));
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> recordsByPartition = fetchedRecords();
        records = recordsByPartition.get(tp0);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(3L, subscriptions.position(tp0).longValue());
        Assert.assertEquals(1, records.get(0).offset());
        Assert.assertEquals(2, records.get(1).offset());
        Assert.assertEquals(0, fetcher.sendFetches());
        consumerClient.poll(timer(0));
        recordsByPartition = fetchedRecords();
        records = recordsByPartition.get(tp0);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(4L, subscriptions.position(tp0).longValue());
        Assert.assertEquals(3, records.get(0).offset());
        Assert.assertTrue(((fetcher.sendFetches()) > 0));
        consumerClient.poll(timer(0));
        recordsByPartition = fetchedRecords();
        records = recordsByPartition.get(tp0);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(6L, subscriptions.position(tp0).longValue());
        Assert.assertEquals(4, records.get(0).offset());
        Assert.assertEquals(5, records.get(1).offset());
    }

    /**
     * Test the scenario where a partition with fetched but not consumed records (i.e. max.poll.records is
     * less than the number of fetched records) is unassigned and a different partition is assigned. This is a
     * pattern used by Streams state restoration and KAFKA-5097 would have been caught by this test.
     */
    @Test
    public void testFetchAfterPartitionWithFetchedRecordsIsUnassigned() {
        buildFetcher(2);
        List<ConsumerRecord<byte[], byte[]>> records;
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 1);
        // Returns 3 records while `max.poll.records` is configured to 2
        client.prepareResponse(matchesOffset(tp0, 1), fullFetchResponse(tp0, this.records, NONE, 100L, 0));
        Assert.assertEquals(1, fetcher.sendFetches());
        consumerClient.poll(timer(0));
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> recordsByPartition = fetchedRecords();
        records = recordsByPartition.get(tp0);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(3L, subscriptions.position(tp0).longValue());
        Assert.assertEquals(1, records.get(0).offset());
        Assert.assertEquals(2, records.get(1).offset());
        assignFromUser(Collections.singleton(tp1));
        client.prepareResponse(matchesOffset(tp1, 4), fullFetchResponse(tp1, this.nextRecords, NONE, 100L, 0));
        subscriptions.seek(tp1, 4);
        Assert.assertEquals(1, fetcher.sendFetches());
        consumerClient.poll(timer(0));
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> fetchedRecords = fetchedRecords();
        Assert.assertNull(fetchedRecords.get(tp0));
        records = fetchedRecords.get(tp1);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(6L, subscriptions.position(tp1).longValue());
        Assert.assertEquals(4, records.get(0).offset());
        Assert.assertEquals(5, records.get(1).offset());
    }

    @Test
    public void testFetchNonContinuousRecords() {
        // if we are fetching from a compacted topic, there may be gaps in the returned records
        // this test verifies the fetcher updates the current fetched/consumed positions correctly for this case
        buildFetcher();
        MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), CompressionType.NONE, CREATE_TIME, 0L);
        builder.appendWithOffset(15L, 0L, "key".getBytes(), "value-1".getBytes());
        builder.appendWithOffset(20L, 0L, "key".getBytes(), "value-2".getBytes());
        builder.appendWithOffset(30L, 0L, "key".getBytes(), "value-3".getBytes());
        MemoryRecords records = builder.build();
        List<ConsumerRecord<byte[], byte[]>> consumerRecords;
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, records, NONE, 100L, 0));
        consumerClient.poll(timer(0));
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> recordsByPartition = fetchedRecords();
        consumerRecords = recordsByPartition.get(tp0);
        Assert.assertEquals(3, consumerRecords.size());
        Assert.assertEquals(31L, subscriptions.position(tp0).longValue());// this is the next fetching position

        Assert.assertEquals(15L, consumerRecords.get(0).offset());
        Assert.assertEquals(20L, consumerRecords.get(1).offset());
        Assert.assertEquals(30L, consumerRecords.get(2).offset());
    }

    /**
     * Test the case where the client makes a pre-v3 FetchRequest, but the server replies with only a partial
     * request. This happens when a single message is larger than the per-partition limit.
     */
    @Test
    public void testFetchRequestWhenRecordTooLarge() {
        try {
            buildFetcher();
            client.setNodeApiVersions(NodeApiVersions.create(Collections.singletonList(new ApiVersionsResponse.ApiVersion(FETCH.id, ((short) (2)), ((short) (2))))));
            makeFetchRequestWithIncompleteRecord();
            try {
                fetcher.fetchedRecords();
                Assert.fail("RecordTooLargeException should have been raised");
            } catch (RecordTooLargeException e) {
                Assert.assertTrue(e.getMessage().startsWith("There are some messages at [Partition=Offset]: "));
                // the position should not advance since no data has been returned
                Assert.assertEquals(0, subscriptions.position(tp0).longValue());
            }
        } finally {
            client.setNodeApiVersions(NodeApiVersions.create());
        }
    }

    /**
     * Test the case where the client makes a post KIP-74 FetchRequest, but the server replies with only a
     * partial request. For v3 and later FetchRequests, the implementation of KIP-74 changed the behavior
     * so that at least one message is always returned. Therefore, this case should not happen, and it indicates
     * that an internal error has taken place.
     */
    @Test
    public void testFetchRequestInternalError() {
        buildFetcher();
        makeFetchRequestWithIncompleteRecord();
        try {
            fetcher.fetchedRecords();
            Assert.fail("RecordTooLargeException should have been raised");
        } catch (KafkaException e) {
            Assert.assertTrue(e.getMessage().startsWith("Failed to make progress reading messages"));
            // the position should not advance since no data has been returned
            Assert.assertEquals(0, subscriptions.position(tp0).longValue());
        }
    }

    @Test
    public void testUnauthorizedTopic() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // resize the limit of the buffer to pretend it is only fetch-size large
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, TOPIC_AUTHORIZATION_FAILED, 100L, 0));
        consumerClient.poll(timer(0));
        try {
            fetcher.fetchedRecords();
            Assert.fail("fetchedRecords should have thrown");
        } catch (TopicAuthorizationException e) {
            Assert.assertEquals(Collections.singleton(topicName), e.unauthorizedTopics());
        }
    }

    @Test
    public void testFetchDuringRebalance() {
        buildFetcher();
        subscriptions.subscribe(Collections.singleton(topicName), listener);
        subscriptions.assignFromSubscribed(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        client.updateMetadata(initialUpdateResponse);
        Assert.assertEquals(1, fetcher.sendFetches());
        // Now the rebalance happens and fetch positions are cleared
        subscriptions.assignFromSubscribed(Collections.singleton(tp0));
        client.prepareResponse(fullFetchResponse(tp0, this.records, NONE, 100L, 0));
        consumerClient.poll(timer(0));
        // The active fetch should be ignored since its position is no longer valid
        Assert.assertTrue(fetcher.fetchedRecords().isEmpty());
    }

    @Test
    public void testInFlightFetchOnPausedPartition() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        subscriptions.pause(tp0);
        client.prepareResponse(fullFetchResponse(tp0, this.records, NONE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertNull(fetcher.fetchedRecords().get(tp0));
    }

    @Test
    public void testFetchOnPausedPartition() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        subscriptions.pause(tp0);
        Assert.assertFalse(((fetcher.sendFetches()) > 0));
        Assert.assertTrue(client.requests().isEmpty());
    }

    @Test
    public void testFetchNotLeaderForPartition() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, NOT_LEADER_FOR_PARTITION, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertEquals(0, fetcher.fetchedRecords().size());
        Assert.assertEquals(0L, metadata.timeToNextUpdate(time.milliseconds()));
    }

    @Test
    public void testFetchUnknownTopicOrPartition() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, UNKNOWN_TOPIC_OR_PARTITION, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertEquals(0, fetcher.fetchedRecords().size());
        Assert.assertEquals(0L, metadata.timeToNextUpdate(time.milliseconds()));
    }

    @Test
    public void testFetchFencedLeaderEpoch() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, FENCED_LEADER_EPOCH, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertEquals("Should not return any records", 0, fetcher.fetchedRecords().size());
        Assert.assertEquals("Should have requested metadata update", 0L, metadata.timeToNextUpdate(time.milliseconds()));
    }

    @Test
    public void testFetchUnknownLeaderEpoch() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, UNKNOWN_LEADER_EPOCH, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertEquals("Should not return any records", 0, fetcher.fetchedRecords().size());
        Assert.assertNotEquals("Should not have requested metadata update", 0L, metadata.timeToNextUpdate(time.milliseconds()));
    }

    @Test
    public void testEpochSetInFetchRequest() {
        buildFetcher();
        subscriptions.assignFromUser(Collections.singleton(tp0));
        client.updateMetadata(initialUpdateResponse);
        // Metadata update with leader epochs
        MetadataResponse metadataResponse = TestUtils.metadataUpdateWith("dummy", 1, Collections.emptyMap(), Collections.singletonMap(topicName, 4), ( error, partition, leader, leaderEpoch, replicas, isr, offlineReplicas) -> new MetadataResponse.PartitionMetadata(error, partition, leader, Optional.of(99), replicas, Collections.emptyList(), offlineReplicas));
        client.updateMetadata(metadataResponse);
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        // Check for epoch in outgoing request
        MockClient.RequestMatcher matcher = ( body) -> {
            if (body instanceof FetchRequest) {
                FetchRequest fetchRequest = ((FetchRequest) (body));
                fetchRequest.fetchData().values().forEach(( partitionData) -> {
                    assertTrue("Expected Fetcher to set leader epoch in request", partitionData.currentLeaderEpoch.isPresent());
                    assertEquals("Expected leader epoch to match epoch from metadata update", partitionData.currentLeaderEpoch.get().longValue(), 99);
                });
                return true;
            } else {
                Assert.fail("Should have seen FetchRequest");
                return false;
            }
        };
        client.prepareResponse(matcher, fullFetchResponse(tp0, this.records, NONE, 100L, 0));
        consumerClient.pollNoWakeup();
    }

    @Test
    public void testFetchOffsetOutOfRange() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, OFFSET_OUT_OF_RANGE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertEquals(0, fetcher.fetchedRecords().size());
        Assert.assertTrue(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertEquals(null, subscriptions.position(tp0));
    }

    @Test
    public void testStaleOutOfRangeError() {
        // verify that an out of range error which arrives after a seek
        // does not cause us to reset our position or throw an exception
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, OFFSET_OUT_OF_RANGE, 100L, 0));
        subscriptions.seek(tp0, 1);
        consumerClient.poll(timer(0));
        Assert.assertEquals(0, fetcher.fetchedRecords().size());
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertEquals(1, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testFetchedRecordsAfterSeek() {
        buildFetcher(OffsetResetStrategy.NONE, new ByteArrayDeserializer(), new ByteArrayDeserializer(), 2, READ_UNCOMMITTED);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertTrue(((fetcher.sendFetches()) > 0));
        client.prepareResponse(fullFetchResponse(tp0, this.records, OFFSET_OUT_OF_RANGE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        subscriptions.seek(tp0, 2);
        Assert.assertEquals(0, fetcher.fetchedRecords().size());
    }

    @Test
    public void testFetchOffsetOutOfRangeException() {
        buildFetcher(OffsetResetStrategy.NONE, new ByteArrayDeserializer(), new ByteArrayDeserializer(), 2, READ_UNCOMMITTED);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        fetcher.sendFetches();
        client.prepareResponse(fullFetchResponse(tp0, this.records, OFFSET_OUT_OF_RANGE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        for (int i = 0; i < 2; i++) {
            OffsetOutOfRangeException e = Assert.assertThrows(OffsetOutOfRangeException.class, () -> fetcher.fetchedRecords());
            Assert.assertEquals(Collections.singleton(tp0), e.offsetOutOfRangePartitions().keySet());
            Assert.assertEquals(0L, e.offsetOutOfRangePartitions().get(tp0).longValue());
        }
    }

    @Test
    public void testFetchPositionAfterException() {
        // verify the advancement in the next fetch offset equals to the number of fetched records when
        // some fetched partitions cause Exception. This ensures that consumer won't lose record upon exception
        buildFetcher(OffsetResetStrategy.NONE, new ByteArrayDeserializer(), new ByteArrayDeserializer(), Integer.MAX_VALUE, READ_UNCOMMITTED);
        assignFromUser(Utils.mkSet(tp0, tp1));
        subscriptions.seek(tp0, 1);
        subscriptions.seek(tp1, 1);
        Assert.assertEquals(1, fetcher.sendFetches());
        Map<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> partitions = new LinkedHashMap<>();
        partitions.put(tp1, new FetchResponse.PartitionData<>(Errors.NONE, 100, FetchResponse.INVALID_LAST_STABLE_OFFSET, FetchResponse.INVALID_LOG_START_OFFSET, null, records));
        partitions.put(tp0, new FetchResponse.PartitionData<>(Errors.OFFSET_OUT_OF_RANGE, 100, FetchResponse.INVALID_LAST_STABLE_OFFSET, FetchResponse.INVALID_LOG_START_OFFSET, null, MemoryRecords.EMPTY));
        client.prepareResponse(new FetchResponse(Errors.NONE, new LinkedHashMap(partitions), 0, FetchMetadata.INVALID_SESSION_ID));
        consumerClient.poll(timer(0));
        List<ConsumerRecord<byte[], byte[]>> allFetchedRecords = new ArrayList<>();
        fetchRecordsInto(allFetchedRecords);
        Assert.assertEquals(1, subscriptions.position(tp0).longValue());
        Assert.assertEquals(4, subscriptions.position(tp1).longValue());
        Assert.assertEquals(3, allFetchedRecords.size());
        OffsetOutOfRangeException e = Assert.assertThrows(OffsetOutOfRangeException.class, () -> fetchRecordsInto(allFetchedRecords));
        Assert.assertEquals(Collections.singleton(tp0), e.offsetOutOfRangePartitions().keySet());
        Assert.assertEquals(1L, e.offsetOutOfRangePartitions().get(tp0).longValue());
        Assert.assertEquals(1, subscriptions.position(tp0).longValue());
        Assert.assertEquals(4, subscriptions.position(tp1).longValue());
        Assert.assertEquals(3, allFetchedRecords.size());
    }

    @Test
    public void testCompletedFetchRemoval() {
        // Ensure the removal of completed fetches that cause an Exception if and only if they contain empty records.
        buildFetcher(OffsetResetStrategy.NONE, new ByteArrayDeserializer(), new ByteArrayDeserializer(), Integer.MAX_VALUE, READ_UNCOMMITTED);
        assignFromUser(Utils.mkSet(tp0, tp1, tp2, tp3));
        client.updateMetadata(initialUpdateResponse);
        subscriptions.seek(tp0, 1);
        subscriptions.seek(tp1, 1);
        subscriptions.seek(tp2, 1);
        subscriptions.seek(tp3, 1);
        Assert.assertEquals(1, fetcher.sendFetches());
        Map<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> partitions = new LinkedHashMap<>();
        partitions.put(tp1, new FetchResponse.PartitionData<>(Errors.NONE, 100, FetchResponse.INVALID_LAST_STABLE_OFFSET, FetchResponse.INVALID_LOG_START_OFFSET, null, records));
        partitions.put(tp0, new FetchResponse.PartitionData<>(Errors.OFFSET_OUT_OF_RANGE, 100, FetchResponse.INVALID_LAST_STABLE_OFFSET, FetchResponse.INVALID_LOG_START_OFFSET, null, MemoryRecords.EMPTY));
        partitions.put(tp2, new FetchResponse.PartitionData<>(Errors.NONE, 100L, 4, 0L, null, nextRecords));
        partitions.put(tp3, new FetchResponse.PartitionData<>(Errors.NONE, 100L, 4, 0L, null, partialRecords));
        client.prepareResponse(new FetchResponse(Errors.NONE, new LinkedHashMap(partitions), 0, FetchMetadata.INVALID_SESSION_ID));
        consumerClient.poll(timer(0));
        List<ConsumerRecord<byte[], byte[]>> fetchedRecords = new ArrayList<>();
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> recordsByPartition = fetchedRecords();
        for (List<ConsumerRecord<byte[], byte[]>> records : recordsByPartition.values())
            fetchedRecords.addAll(records);

        Assert.assertEquals(fetchedRecords.size(), ((subscriptions.position(tp1)) - 1));
        Assert.assertEquals(4, subscriptions.position(tp1).longValue());
        Assert.assertEquals(3, fetchedRecords.size());
        List<OffsetOutOfRangeException> oorExceptions = new ArrayList<>();
        try {
            recordsByPartition = fetchedRecords();
            for (List<ConsumerRecord<byte[], byte[]>> records : recordsByPartition.values())
                fetchedRecords.addAll(records);

        } catch (OffsetOutOfRangeException oor) {
            oorExceptions.add(oor);
        }
        // Should have received one OffsetOutOfRangeException for partition tp1
        Assert.assertEquals(1, oorExceptions.size());
        OffsetOutOfRangeException oor = oorExceptions.get(0);
        Assert.assertTrue(oor.offsetOutOfRangePartitions().containsKey(tp0));
        Assert.assertEquals(oor.offsetOutOfRangePartitions().size(), 1);
        recordsByPartition = fetchedRecords();
        for (List<ConsumerRecord<byte[], byte[]>> records : recordsByPartition.values())
            fetchedRecords.addAll(records);

        // Should not have received an Exception for tp2.
        Assert.assertEquals(6, subscriptions.position(tp2).longValue());
        Assert.assertEquals(5, fetchedRecords.size());
        int numExceptionsExpected = 3;
        List<KafkaException> kafkaExceptions = new ArrayList<>();
        for (int i = 1; i <= numExceptionsExpected; i++) {
            try {
                recordsByPartition = fetchedRecords();
                for (List<ConsumerRecord<byte[], byte[]>> records : recordsByPartition.values())
                    fetchedRecords.addAll(records);

            } catch (KafkaException e) {
                kafkaExceptions.add(e);
            }
        }
        // Should have received as much as numExceptionsExpected Kafka exceptions for tp3.
        Assert.assertEquals(numExceptionsExpected, kafkaExceptions.size());
    }

    @Test
    public void testSeekBeforeException() {
        buildFetcher(OffsetResetStrategy.NONE, new ByteArrayDeserializer(), new ByteArrayDeserializer(), 2, READ_UNCOMMITTED);
        assignFromUser(Utils.mkSet(tp0));
        subscriptions.seek(tp0, 1);
        Assert.assertEquals(1, fetcher.sendFetches());
        Map<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> partitions = new HashMap<>();
        partitions.put(tp0, new FetchResponse.PartitionData<>(Errors.NONE, 100, FetchResponse.INVALID_LAST_STABLE_OFFSET, FetchResponse.INVALID_LOG_START_OFFSET, null, records));
        client.prepareResponse(fullFetchResponse(tp0, this.records, NONE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertEquals(2, fetcher.fetchedRecords().get(tp0).size());
        subscriptions.assignFromUser(Utils.mkSet(tp0, tp1));
        subscriptions.seek(tp1, 1);
        Assert.assertEquals(1, fetcher.sendFetches());
        partitions = new HashMap();
        partitions.put(tp1, new FetchResponse.PartitionData<>(Errors.OFFSET_OUT_OF_RANGE, 100, FetchResponse.INVALID_LAST_STABLE_OFFSET, FetchResponse.INVALID_LOG_START_OFFSET, null, MemoryRecords.EMPTY));
        client.prepareResponse(new FetchResponse(Errors.NONE, new LinkedHashMap(partitions), 0, FetchMetadata.INVALID_SESSION_ID));
        consumerClient.poll(timer(0));
        Assert.assertEquals(1, fetcher.fetchedRecords().get(tp0).size());
        subscriptions.seek(tp1, 10);
        // Should not throw OffsetOutOfRangeException after the seek
        Assert.assertEquals(0, fetcher.fetchedRecords().size());
    }

    @Test
    public void testFetchDisconnected() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, NONE, 100L, 0), true);
        consumerClient.poll(timer(0));
        Assert.assertEquals(0, fetcher.fetchedRecords().size());
        // disconnects should have no affect on subscription state
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertTrue(subscriptions.isFetchable(tp0));
        Assert.assertEquals(0, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testUpdateFetchPositionNoOpWithPositionSet() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 5L);
        fetcher.resetOffsetsIfNeeded();
        Assert.assertFalse(client.hasInFlightRequests());
        Assert.assertTrue(subscriptions.isFetchable(tp0));
        Assert.assertEquals(5, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testUpdateFetchPositionResetToDefaultOffset() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0);
        client.prepareResponse(listOffsetRequestMatcher(EARLIEST_TIMESTAMP), listOffsetResponse(NONE, 1L, 5L));
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertTrue(subscriptions.isFetchable(tp0));
        Assert.assertEquals(5, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testUpdateFetchPositionResetToLatestOffset() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, LATEST);
        client.updateMetadata(initialUpdateResponse);
        client.prepareResponse(listOffsetRequestMatcher(LATEST_TIMESTAMP), listOffsetResponse(NONE, 1L, 5L));
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertTrue(subscriptions.isFetchable(tp0));
        Assert.assertEquals(5, subscriptions.position(tp0).longValue());
    }

    /**
     * Make sure the client behaves appropriately when receiving an exception for unavailable offsets
     */
    @Test
    public void testFetchOffsetErrors() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, LATEST);
        // Fail with OFFSET_NOT_AVAILABLE
        client.prepareResponse(listOffsetRequestMatcher(LATEST_TIMESTAMP), listOffsetResponse(OFFSET_NOT_AVAILABLE, 1L, 5L), false);
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        Assert.assertTrue(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertFalse(subscriptions.isFetchable(tp0));
        // Fail with LEADER_NOT_AVAILABLE
        time.sleep(retryBackoffMs);
        client.prepareResponse(listOffsetRequestMatcher(LATEST_TIMESTAMP), listOffsetResponse(LEADER_NOT_AVAILABLE, 1L, 5L), false);
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        Assert.assertTrue(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertFalse(subscriptions.isFetchable(tp0));
        // Back to normal
        time.sleep(retryBackoffMs);
        client.prepareResponse(listOffsetRequestMatcher(LATEST_TIMESTAMP), listOffsetResponse(NONE, 1L, 5L), false);
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertTrue(subscriptions.hasValidPosition(tp0));
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertTrue(subscriptions.isFetchable(tp0));
        Assert.assertEquals(subscriptions.position(tp0).longValue(), 5L);
    }

    @Test
    public void testListOffsetSendsReadUncommitted() {
        testListOffsetsSendsIsolationLevel(READ_UNCOMMITTED);
    }

    @Test
    public void testListOffsetSendsReadCommitted() {
        testListOffsetsSendsIsolationLevel(READ_COMMITTED);
    }

    @Test
    public void testResetOffsetsSkipsBlackedOutConnections() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, EARLIEST);
        // Check that we skip sending the ListOffset request when the node is blacked out
        client.updateMetadata(initialUpdateResponse);
        Node node = initialUpdateResponse.brokers().iterator().next();
        client.blackout(node, 500);
        fetcher.resetOffsetsIfNeeded();
        Assert.assertEquals(0, consumerClient.pendingRequestCount());
        consumerClient.pollNoWakeup();
        Assert.assertTrue(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertEquals(EARLIEST, subscriptions.resetStrategy(tp0));
        time.sleep(500);
        client.prepareResponse(listOffsetRequestMatcher(EARLIEST_TIMESTAMP), listOffsetResponse(NONE, 1L, 5L));
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertTrue(subscriptions.isFetchable(tp0));
        Assert.assertEquals(5, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testUpdateFetchPositionResetToEarliestOffset() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, EARLIEST);
        client.prepareResponse(listOffsetRequestMatcher(EARLIEST_TIMESTAMP), listOffsetResponse(NONE, 1L, 5L));
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertTrue(subscriptions.isFetchable(tp0));
        Assert.assertEquals(5, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testResetOffsetsMetadataRefresh() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, LATEST);
        // First fetch fails with stale metadata
        client.prepareResponse(listOffsetRequestMatcher(LATEST_TIMESTAMP), listOffsetResponse(NOT_LEADER_FOR_PARTITION, 1L, 5L), false);
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        // Expect a metadata refresh
        client.prepareMetadataUpdate(initialUpdateResponse);
        consumerClient.pollNoWakeup();
        Assert.assertFalse(client.hasPendingMetadataUpdates());
        // Next fetch succeeds
        time.sleep(retryBackoffMs);
        client.prepareResponse(listOffsetRequestMatcher(LATEST_TIMESTAMP), listOffsetResponse(NONE, 1L, 5L));
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertTrue(subscriptions.isFetchable(tp0));
        Assert.assertEquals(5, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testUpdateFetchPositionDisconnect() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, LATEST);
        // First request gets a disconnect
        client.prepareResponse(listOffsetRequestMatcher(LATEST_TIMESTAMP), listOffsetResponse(NONE, 1L, 5L), true);
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        // Expect a metadata refresh
        client.prepareMetadataUpdate(initialUpdateResponse);
        consumerClient.pollNoWakeup();
        Assert.assertFalse(client.hasPendingMetadataUpdates());
        // No retry until the backoff passes
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(client.hasInFlightRequests());
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        // Next one succeeds
        time.sleep(retryBackoffMs);
        client.prepareResponse(listOffsetRequestMatcher(LATEST_TIMESTAMP), listOffsetResponse(NONE, 1L, 5L));
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertTrue(subscriptions.isFetchable(tp0));
        Assert.assertEquals(5, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testAssignmentChangeWithInFlightReset() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, LATEST);
        // Send the ListOffsets request to reset the position
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        Assert.assertTrue(client.hasInFlightRequests());
        // Now we have an assignment change
        assignFromUser(Collections.singleton(tp1));
        // The response returns and is discarded
        client.respond(listOffsetResponse(NONE, 1L, 5L));
        consumerClient.pollNoWakeup();
        Assert.assertFalse(client.hasPendingResponses());
        Assert.assertFalse(client.hasInFlightRequests());
        Assert.assertFalse(subscriptions.isAssigned(tp0));
    }

    @Test
    public void testSeekWithInFlightReset() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, LATEST);
        // Send the ListOffsets request to reset the position
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        Assert.assertTrue(client.hasInFlightRequests());
        // Now we get a seek from the user
        subscriptions.seek(tp0, 237);
        // The response returns and is discarded
        client.respond(listOffsetResponse(NONE, 1L, 5L));
        consumerClient.pollNoWakeup();
        Assert.assertFalse(client.hasPendingResponses());
        Assert.assertFalse(client.hasInFlightRequests());
        Assert.assertEquals(237L, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testChangeResetWithInFlightReset() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, LATEST);
        // Send the ListOffsets request to reset the position
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        Assert.assertTrue(client.hasInFlightRequests());
        // Now we get a seek from the user
        subscriptions.requestOffsetReset(tp0, EARLIEST);
        // The response returns and is discarded
        client.respond(listOffsetResponse(NONE, 1L, 5L));
        consumerClient.pollNoWakeup();
        Assert.assertFalse(client.hasPendingResponses());
        Assert.assertFalse(client.hasInFlightRequests());
        Assert.assertTrue(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertEquals(EARLIEST, subscriptions.resetStrategy(tp0));
    }

    @Test
    public void testIdempotentResetWithInFlightReset() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, LATEST);
        // Send the ListOffsets request to reset the position
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        Assert.assertTrue(client.hasInFlightRequests());
        // Now we get a seek from the user
        subscriptions.requestOffsetReset(tp0, LATEST);
        client.respond(listOffsetResponse(NONE, 1L, 5L));
        consumerClient.pollNoWakeup();
        Assert.assertFalse(client.hasInFlightRequests());
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertEquals(5L, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testRestOffsetsAuthorizationFailure() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, LATEST);
        // First request gets a disconnect
        client.prepareResponse(listOffsetRequestMatcher(LATEST_TIMESTAMP), listOffsetResponse(TOPIC_AUTHORIZATION_FAILED, (-1), (-1)), false);
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        try {
            fetcher.resetOffsetsIfNeeded();
            Assert.fail("Expected authorization error to be raised");
        } catch (TopicAuthorizationException e) {
            Assert.assertEquals(Collections.singleton(tp0.topic()), e.unauthorizedTopics());
        }
        // The exception should clear after being raised, but no retry until the backoff
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(client.hasInFlightRequests());
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        // Next one succeeds
        time.sleep(retryBackoffMs);
        client.prepareResponse(listOffsetRequestMatcher(LATEST_TIMESTAMP), listOffsetResponse(NONE, 1L, 5L));
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertTrue(subscriptions.isFetchable(tp0));
        Assert.assertEquals(5, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testUpdateFetchPositionOfPausedPartitionsRequiringOffsetReset() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.pause(tp0);// paused partition does not have a valid position

        subscriptions.requestOffsetReset(tp0, LATEST);
        client.prepareResponse(listOffsetRequestMatcher(LATEST_TIMESTAMP), listOffsetResponse(NONE, 1L, 10L));
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertFalse(subscriptions.isFetchable(tp0));// because tp is paused

        Assert.assertTrue(subscriptions.hasValidPosition(tp0));
        Assert.assertEquals(10, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testUpdateFetchPositionOfPausedPartitionsWithoutAValidPosition() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0);
        subscriptions.pause(tp0);// paused partition does not have a valid position

        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertTrue(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertFalse(subscriptions.isFetchable(tp0));// because tp is paused

        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
    }

    @Test
    public void testUpdateFetchPositionOfPausedPartitionsWithAValidPosition() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 10);
        subscriptions.pause(tp0);// paused partition already has a valid position

        fetcher.resetOffsetsIfNeeded();
        Assert.assertFalse(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertFalse(subscriptions.isFetchable(tp0));// because tp is paused

        Assert.assertTrue(subscriptions.hasValidPosition(tp0));
        Assert.assertEquals(10, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testGetAllTopics() {
        // sending response before request, as getTopicMetadata is a blocking call
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        client.prepareResponse(newMetadataResponse(topicName, NONE));
        Map<String, List<PartitionInfo>> allTopics = fetcher.getAllTopicMetadata(time.timer(5000L));
        Assert.assertEquals(initialUpdateResponse.topicMetadata().size(), allTopics.size());
    }

    @Test
    public void testGetAllTopicsDisconnect() {
        // first try gets a disconnect, next succeeds
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        client.prepareResponse(null, true);
        client.prepareResponse(newMetadataResponse(topicName, NONE));
        Map<String, List<PartitionInfo>> allTopics = fetcher.getAllTopicMetadata(time.timer(5000L));
        Assert.assertEquals(initialUpdateResponse.topicMetadata().size(), allTopics.size());
    }

    @Test(expected = TimeoutException.class)
    public void testGetAllTopicsTimeout() {
        // since no response is prepared, the request should timeout
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        fetcher.getAllTopicMetadata(time.timer(50L));
    }

    @Test
    public void testGetAllTopicsUnauthorized() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        client.prepareResponse(newMetadataResponse(topicName, TOPIC_AUTHORIZATION_FAILED));
        try {
            fetcher.getAllTopicMetadata(time.timer(10L));
            Assert.fail();
        } catch (TopicAuthorizationException e) {
            Assert.assertEquals(Collections.singleton(topicName), e.unauthorizedTopics());
        }
    }

    @Test(expected = InvalidTopicException.class)
    public void testGetTopicMetadataInvalidTopic() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        client.prepareResponse(newMetadataResponse(topicName, INVALID_TOPIC_EXCEPTION));
        fetcher.getTopicMetadata(new MetadataRequest.Builder(Collections.singletonList(topicName), true), time.timer(5000L));
    }

    @Test
    public void testGetTopicMetadataUnknownTopic() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        client.prepareResponse(newMetadataResponse(topicName, UNKNOWN_TOPIC_OR_PARTITION));
        Map<String, List<PartitionInfo>> topicMetadata = fetcher.getTopicMetadata(new MetadataRequest.Builder(Collections.singletonList(topicName), true), time.timer(5000L));
        Assert.assertNull(topicMetadata.get(topicName));
    }

    @Test
    public void testGetTopicMetadataLeaderNotAvailable() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        client.prepareResponse(newMetadataResponse(topicName, LEADER_NOT_AVAILABLE));
        client.prepareResponse(newMetadataResponse(topicName, NONE));
        Map<String, List<PartitionInfo>> topicMetadata = fetcher.getTopicMetadata(new MetadataRequest.Builder(Collections.singletonList(topicName), true), time.timer(5000L));
        Assert.assertTrue(topicMetadata.containsKey(topicName));
    }

    @Test
    public void testGetTopicMetadataOfflinePartitions() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        MetadataResponse originalResponse = newMetadataResponse(topicName, NONE);// baseline ok response

        // create a response based on the above one with all partitions being leaderless
        List<MetadataResponse.TopicMetadata> altTopics = new ArrayList<>();
        for (MetadataResponse.TopicMetadata item : originalResponse.topicMetadata()) {
            List<MetadataResponse.PartitionMetadata> partitions = item.partitionMetadata();
            List<MetadataResponse.PartitionMetadata> altPartitions = new ArrayList<>();
            for (MetadataResponse.PartitionMetadata p : partitions) {
                altPartitions.add(// no leader
                new MetadataResponse.PartitionMetadata(p.error(), p.partition(), null, Optional.empty(), p.replicas(), p.isr(), p.offlineReplicas()));
            }
            MetadataResponse.TopicMetadata alteredTopic = new MetadataResponse.TopicMetadata(item.error(), item.topic(), item.isInternal(), altPartitions);
            altTopics.add(alteredTopic);
        }
        Node controller = originalResponse.controller();
        MetadataResponse altered = new MetadataResponse(((List<Node>) (originalResponse.brokers())), originalResponse.clusterId(), (controller != null ? controller.id() : MetadataResponse.NO_CONTROLLER_ID), altTopics);
        client.prepareResponse(altered);
        Map<String, List<PartitionInfo>> topicMetadata = fetcher.getTopicMetadata(new MetadataRequest.Builder(Collections.singletonList(topicName), false), time.timer(5000L));
        Assert.assertNotNull(topicMetadata);
        Assert.assertNotNull(topicMetadata.get(topicName));
        // noinspection ConstantConditions
        Assert.assertEquals(metadata.fetch().partitionCountForTopic(topicName).longValue(), topicMetadata.get(topicName).size());
    }

    /* Send multiple requests. Verify that the client side quota metrics have the right values */
    @Test
    public void testQuotaMetrics() {
        buildFetcher();
        MockSelector selector = new MockSelector(time);
        Sensor throttleTimeSensor = Fetcher.throttleTimeSensor(metrics, metricsRegistry);
        Cluster cluster = TestUtils.singletonCluster("test", 1);
        Node node = cluster.nodes().get(0);
        NetworkClient client = new NetworkClient(selector, metadata, "mock", Integer.MAX_VALUE, 1000, 1000, (64 * 1024), (64 * 1024), 1000, ClientDnsLookup.DEFAULT, time, true, new ApiVersions(), throttleTimeSensor, new LogContext());
        short apiVersionsResponseVersion = API_VERSIONS.latestVersion();
        ByteBuffer buffer = ApiVersionsResponse.createApiVersionsResponse(400, CURRENT_MAGIC_VALUE).serialize(apiVersionsResponseVersion, new ResponseHeader(0));
        selector.delayedReceive(new DelayedReceive(node.idString(), new org.apache.kafka.common.network.NetworkReceive(node.idString(), buffer)));
        while (!(client.ready(node, time.milliseconds()))) {
            client.poll(1, time.milliseconds());
            // If a throttled response is received, advance the time to ensure progress.
            time.sleep(client.throttleDelayMs(node, time.milliseconds()));
        } 
        selector.clear();
        for (int i = 1; i <= 3; i++) {
            int throttleTimeMs = 100 * i;
            FetchRequest.Builder builder = Builder.forConsumer(100, 100, new LinkedHashMap());
            ClientRequest request = client.newClientRequest(node.idString(), builder, time.milliseconds(), true);
            client.send(request, time.milliseconds());
            client.poll(1, time.milliseconds());
            FetchResponse response = fullFetchResponse(tp0, nextRecords, NONE, i, throttleTimeMs);
            buffer = response.serialize(FETCH.latestVersion(), new ResponseHeader(request.correlationId()));
            selector.completeReceive(new org.apache.kafka.common.network.NetworkReceive(node.idString(), buffer));
            client.poll(1, time.milliseconds());
            // If a throttled response is received, advance the time to ensure progress.
            time.sleep(client.throttleDelayMs(node, time.milliseconds()));
            selector.clear();
        }
        Map<MetricName, KafkaMetric> allMetrics = metrics.metrics();
        KafkaMetric avgMetric = allMetrics.get(metrics.metricInstance(metricsRegistry.fetchThrottleTimeAvg));
        KafkaMetric maxMetric = allMetrics.get(metrics.metricInstance(metricsRegistry.fetchThrottleTimeMax));
        // Throttle times are ApiVersions=400, Fetch=(100, 200, 300)
        Assert.assertEquals(250, ((Double) (avgMetric.metricValue())), FetcherTest.EPSILON);
        Assert.assertEquals(400, ((Double) (maxMetric.metricValue())), FetcherTest.EPSILON);
        client.close();
    }

    /* Send multiple requests. Verify that the client side quota metrics have the right values */
    @Test
    public void testFetcherMetrics() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        MetricName maxLagMetric = metrics.metricInstance(metricsRegistry.recordsLagMax);
        Map<String, String> tags = new HashMap<>();
        tags.put("topic", tp0.topic());
        tags.put("partition", String.valueOf(tp0.partition()));
        MetricName partitionLagMetric = metrics.metricName("records-lag", metricGroup, tags);
        Map<MetricName, KafkaMetric> allMetrics = metrics.metrics();
        KafkaMetric recordsFetchLagMax = allMetrics.get(maxLagMetric);
        // recordsFetchLagMax should be initialized to NaN
        Assert.assertEquals(Double.NaN, ((Double) (recordsFetchLagMax.metricValue())), FetcherTest.EPSILON);
        // recordsFetchLagMax should be hw - fetchOffset after receiving an empty FetchResponse
        fetchRecords(tp0, EMPTY, NONE, 100L, 0);
        Assert.assertEquals(100, ((Double) (recordsFetchLagMax.metricValue())), FetcherTest.EPSILON);
        KafkaMetric partitionLag = allMetrics.get(partitionLagMetric);
        Assert.assertEquals(100, ((Double) (partitionLag.metricValue())), FetcherTest.EPSILON);
        // recordsFetchLagMax should be hw - offset of the last message after receiving a non-empty FetchResponse
        MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), CompressionType.NONE, CREATE_TIME, 0L);
        for (int v = 0; v < 3; v++)
            builder.appendWithOffset(v, NO_TIMESTAMP, "key".getBytes(), ("value-" + v).getBytes());

        fetchRecords(tp0, builder.build(), NONE, 200L, 0);
        Assert.assertEquals(197, ((Double) (recordsFetchLagMax.metricValue())), FetcherTest.EPSILON);
        Assert.assertEquals(197, ((Double) (partitionLag.metricValue())), FetcherTest.EPSILON);
        // verify de-registration of partition lag
        subscriptions.unsubscribe();
        Assert.assertFalse(allMetrics.containsKey(partitionLagMetric));
    }

    @Test
    public void testFetcherLeadMetric() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        MetricName minLeadMetric = metrics.metricInstance(metricsRegistry.recordsLeadMin);
        Map<String, String> tags = new HashMap<>(2);
        tags.put("topic", tp0.topic());
        tags.put("partition", String.valueOf(tp0.partition()));
        MetricName partitionLeadMetric = metrics.metricName("records-lead", metricGroup, "", tags);
        Map<MetricName, KafkaMetric> allMetrics = metrics.metrics();
        KafkaMetric recordsFetchLeadMin = allMetrics.get(minLeadMetric);
        // recordsFetchLeadMin should be initialized to NaN
        Assert.assertEquals(Double.NaN, ((Double) (recordsFetchLeadMin.metricValue())), FetcherTest.EPSILON);
        // recordsFetchLeadMin should be position - logStartOffset after receiving an empty FetchResponse
        fetchRecords(tp0, EMPTY, NONE, 100L, (-1L), 0L, 0);
        Assert.assertEquals(0L, ((Double) (recordsFetchLeadMin.metricValue())), FetcherTest.EPSILON);
        KafkaMetric partitionLead = allMetrics.get(partitionLeadMetric);
        Assert.assertEquals(0L, ((Double) (partitionLead.metricValue())), FetcherTest.EPSILON);
        // recordsFetchLeadMin should be position - logStartOffset after receiving a non-empty FetchResponse
        MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), CompressionType.NONE, CREATE_TIME, 0L);
        for (int v = 0; v < 3; v++) {
            builder.appendWithOffset(v, NO_TIMESTAMP, "key".getBytes(), ("value-" + v).getBytes());
        }
        fetchRecords(tp0, builder.build(), NONE, 200L, (-1L), 0L, 0);
        Assert.assertEquals(0L, ((Double) (recordsFetchLeadMin.metricValue())), FetcherTest.EPSILON);
        Assert.assertEquals(3L, ((Double) (partitionLead.metricValue())), FetcherTest.EPSILON);
        // verify de-registration of partition lag
        subscriptions.unsubscribe();
        Assert.assertFalse(allMetrics.containsKey(partitionLeadMetric));
    }

    @Test
    public void testReadCommittedLagMetric() {
        buildFetcher(EARLIEST, new ByteArrayDeserializer(), new ByteArrayDeserializer(), Integer.MAX_VALUE, READ_COMMITTED);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        MetricName maxLagMetric = metrics.metricInstance(metricsRegistry.recordsLagMax);
        Map<String, String> tags = new HashMap<>();
        tags.put("topic", tp0.topic());
        tags.put("partition", String.valueOf(tp0.partition()));
        MetricName partitionLagMetric = metrics.metricName("records-lag", metricGroup, tags);
        Map<MetricName, KafkaMetric> allMetrics = metrics.metrics();
        KafkaMetric recordsFetchLagMax = allMetrics.get(maxLagMetric);
        // recordsFetchLagMax should be initialized to NaN
        Assert.assertEquals(Double.NaN, ((Double) (recordsFetchLagMax.metricValue())), FetcherTest.EPSILON);
        // recordsFetchLagMax should be lso - fetchOffset after receiving an empty FetchResponse
        fetchRecords(tp0, EMPTY, NONE, 100L, 50L, 0);
        Assert.assertEquals(50, ((Double) (recordsFetchLagMax.metricValue())), FetcherTest.EPSILON);
        KafkaMetric partitionLag = allMetrics.get(partitionLagMetric);
        Assert.assertEquals(50, ((Double) (partitionLag.metricValue())), FetcherTest.EPSILON);
        // recordsFetchLagMax should be lso - offset of the last message after receiving a non-empty FetchResponse
        MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), CompressionType.NONE, CREATE_TIME, 0L);
        for (int v = 0; v < 3; v++)
            builder.appendWithOffset(v, NO_TIMESTAMP, "key".getBytes(), ("value-" + v).getBytes());

        fetchRecords(tp0, builder.build(), NONE, 200L, 150L, 0);
        Assert.assertEquals(147, ((Double) (recordsFetchLagMax.metricValue())), FetcherTest.EPSILON);
        Assert.assertEquals(147, ((Double) (partitionLag.metricValue())), FetcherTest.EPSILON);
        // verify de-registration of partition lag
        subscriptions.unsubscribe();
        Assert.assertFalse(allMetrics.containsKey(partitionLagMetric));
    }

    @Test
    public void testFetchResponseMetrics() {
        buildFetcher();
        String topic1 = "foo";
        String topic2 = "bar";
        TopicPartition tp1 = new TopicPartition(topic1, 0);
        TopicPartition tp2 = new TopicPartition(topic2, 0);
        subscriptions.assignFromUser(Utils.mkSet(tp1, tp2));
        Map<String, Integer> partitionCounts = new HashMap<>();
        partitionCounts.put(topic1, 1);
        partitionCounts.put(topic2, 1);
        client.updateMetadata(TestUtils.metadataUpdateWith(1, partitionCounts));
        int expectedBytes = 0;
        LinkedHashMap<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> fetchPartitionData = new LinkedHashMap<>();
        for (TopicPartition tp : Utils.mkSet(tp1, tp2)) {
            subscriptions.seek(tp, 0);
            MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), CompressionType.NONE, CREATE_TIME, 0L);
            for (int v = 0; v < 3; v++)
                builder.appendWithOffset(v, NO_TIMESTAMP, "key".getBytes(), ("value-" + v).getBytes());

            MemoryRecords records = builder.build();
            for (Record record : records.records())
                expectedBytes += record.sizeInBytes();

            fetchPartitionData.put(tp, new FetchResponse.PartitionData<>(Errors.NONE, 15L, FetchResponse.INVALID_LAST_STABLE_OFFSET, 0L, null, records));
        }
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(new FetchResponse(Errors.NONE, fetchPartitionData, 0, FetchMetadata.INVALID_SESSION_ID));
        consumerClient.poll(timer(0));
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> fetchedRecords = fetchedRecords();
        Assert.assertEquals(3, fetchedRecords.get(tp1).size());
        Assert.assertEquals(3, fetchedRecords.get(tp2).size());
        Map<MetricName, KafkaMetric> allMetrics = metrics.metrics();
        KafkaMetric fetchSizeAverage = allMetrics.get(metrics.metricInstance(metricsRegistry.fetchSizeAvg));
        KafkaMetric recordsCountAverage = allMetrics.get(metrics.metricInstance(metricsRegistry.recordsPerRequestAvg));
        Assert.assertEquals(expectedBytes, ((Double) (fetchSizeAverage.metricValue())), FetcherTest.EPSILON);
        Assert.assertEquals(6, ((Double) (recordsCountAverage.metricValue())), FetcherTest.EPSILON);
    }

    @Test
    public void testFetchResponseMetricsPartialResponse() {
        buildFetcher();
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 1);
        Map<MetricName, KafkaMetric> allMetrics = metrics.metrics();
        KafkaMetric fetchSizeAverage = allMetrics.get(metrics.metricInstance(metricsRegistry.fetchSizeAvg));
        KafkaMetric recordsCountAverage = allMetrics.get(metrics.metricInstance(metricsRegistry.recordsPerRequestAvg));
        MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), CompressionType.NONE, CREATE_TIME, 0L);
        for (int v = 0; v < 3; v++)
            builder.appendWithOffset(v, NO_TIMESTAMP, "key".getBytes(), ("value-" + v).getBytes());

        MemoryRecords records = builder.build();
        int expectedBytes = 0;
        for (Record record : records.records()) {
            if ((record.offset()) >= 1)
                expectedBytes += record.sizeInBytes();

        }
        fetchRecords(tp0, records, NONE, 100L, 0);
        Assert.assertEquals(expectedBytes, ((Double) (fetchSizeAverage.metricValue())), FetcherTest.EPSILON);
        Assert.assertEquals(2, ((Double) (recordsCountAverage.metricValue())), FetcherTest.EPSILON);
    }

    @Test
    public void testFetchResponseMetricsWithOnePartitionError() {
        buildFetcher();
        assignFromUser(Utils.mkSet(tp0, tp1));
        subscriptions.seek(tp0, 0);
        subscriptions.seek(tp1, 0);
        Map<MetricName, KafkaMetric> allMetrics = metrics.metrics();
        KafkaMetric fetchSizeAverage = allMetrics.get(metrics.metricInstance(metricsRegistry.fetchSizeAvg));
        KafkaMetric recordsCountAverage = allMetrics.get(metrics.metricInstance(metricsRegistry.recordsPerRequestAvg));
        MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), CompressionType.NONE, CREATE_TIME, 0L);
        for (int v = 0; v < 3; v++)
            builder.appendWithOffset(v, NO_TIMESTAMP, "key".getBytes(), ("value-" + v).getBytes());

        MemoryRecords records = builder.build();
        Map<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> partitions = new HashMap<>();
        partitions.put(tp0, new FetchResponse.PartitionData<>(Errors.NONE, 100, FetchResponse.INVALID_LAST_STABLE_OFFSET, 0L, null, records));
        partitions.put(tp1, new FetchResponse.PartitionData<>(Errors.OFFSET_OUT_OF_RANGE, 100, FetchResponse.INVALID_LAST_STABLE_OFFSET, 0L, null, MemoryRecords.EMPTY));
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(new FetchResponse(Errors.NONE, new LinkedHashMap(partitions), 0, FetchMetadata.INVALID_SESSION_ID));
        consumerClient.poll(timer(0));
        fetcher.fetchedRecords();
        int expectedBytes = 0;
        for (Record record : records.records())
            expectedBytes += record.sizeInBytes();

        Assert.assertEquals(expectedBytes, ((Double) (fetchSizeAverage.metricValue())), FetcherTest.EPSILON);
        Assert.assertEquals(3, ((Double) (recordsCountAverage.metricValue())), FetcherTest.EPSILON);
    }

    @Test
    public void testFetchResponseMetricsWithOnePartitionAtTheWrongOffset() {
        buildFetcher();
        assignFromUser(Utils.mkSet(tp0, tp1));
        subscriptions.seek(tp0, 0);
        subscriptions.seek(tp1, 0);
        Map<MetricName, KafkaMetric> allMetrics = metrics.metrics();
        KafkaMetric fetchSizeAverage = allMetrics.get(metrics.metricInstance(metricsRegistry.fetchSizeAvg));
        KafkaMetric recordsCountAverage = allMetrics.get(metrics.metricInstance(metricsRegistry.recordsPerRequestAvg));
        // send the fetch and then seek to a new offset
        Assert.assertEquals(1, fetcher.sendFetches());
        subscriptions.seek(tp1, 5);
        MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), CompressionType.NONE, CREATE_TIME, 0L);
        for (int v = 0; v < 3; v++)
            builder.appendWithOffset(v, NO_TIMESTAMP, "key".getBytes(), ("value-" + v).getBytes());

        MemoryRecords records = builder.build();
        Map<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> partitions = new HashMap<>();
        partitions.put(tp0, new FetchResponse.PartitionData<>(Errors.NONE, 100, FetchResponse.INVALID_LAST_STABLE_OFFSET, 0L, null, records));
        partitions.put(tp1, new FetchResponse.PartitionData<>(Errors.NONE, 100, FetchResponse.INVALID_LAST_STABLE_OFFSET, 0L, null, MemoryRecords.withRecords(CompressionType.NONE, new SimpleRecord("val".getBytes()))));
        client.prepareResponse(new FetchResponse(Errors.NONE, new LinkedHashMap(partitions), 0, FetchMetadata.INVALID_SESSION_ID));
        consumerClient.poll(timer(0));
        fetcher.fetchedRecords();
        // we should have ignored the record at the wrong offset
        int expectedBytes = 0;
        for (Record record : records.records())
            expectedBytes += record.sizeInBytes();

        Assert.assertEquals(expectedBytes, ((Double) (fetchSizeAverage.metricValue())), FetcherTest.EPSILON);
        Assert.assertEquals(3, ((Double) (recordsCountAverage.metricValue())), FetcherTest.EPSILON);
    }

    @Test
    public void testFetcherMetricsTemplates() {
        Map<String, String> clientTags = Collections.singletonMap("client-id", "clientA");
        buildFetcher(new MetricConfig().tags(clientTags), EARLIEST, new ByteArrayDeserializer(), new ByteArrayDeserializer(), Integer.MAX_VALUE, READ_UNCOMMITTED);
        // Fetch from topic to generate topic metrics
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, this.records, NONE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> partitionRecords = fetchedRecords();
        Assert.assertTrue(partitionRecords.containsKey(tp0));
        // Create throttle metrics
        Fetcher.throttleTimeSensor(metrics, metricsRegistry);
        // Verify that all metrics except metrics-count have registered templates
        Set<MetricNameTemplate> allMetrics = new HashSet<>();
        for (MetricName n : metrics.metrics().keySet()) {
            String name = n.name().replaceAll(tp0.toString(), "{topic}-{partition}");
            if (!(n.group().equals("kafka-metrics-count")))
                allMetrics.add(new MetricNameTemplate(name, n.group(), "", n.tags().keySet()));

        }
        TestUtils.checkEquals(allMetrics, new HashSet(metricsRegistry.getAllTemplates()), "metrics", "templates");
    }

    @Test
    public void testGetOffsetsForTimesTimeout() {
        try {
            buildFetcher();
            fetcher.offsetsForTimes(Collections.singletonMap(new TopicPartition(topicName, 2), 1000L), time.timer(100L));
            Assert.fail("Should throw timeout exception.");
        } catch (TimeoutException e) {
            // let it go.
        }
    }

    @Test
    public void testGetOffsetsForTimes() {
        buildFetcher();
        // Empty map
        Assert.assertTrue(fetcher.offsetsForTimes(new HashMap<TopicPartition, Long>(), time.timer(100L)).isEmpty());
        // Unknown Offset
        testGetOffsetsForTimesWithUnknownOffset();
        // Error code none with unknown offset
        testGetOffsetsForTimesWithError(NONE, NONE, (-1L), 100L, null, 100L);
        // Error code none with known offset
        testGetOffsetsForTimesWithError(NONE, NONE, 10L, 100L, 10L, 100L);
        // Test both of partition has error.
        testGetOffsetsForTimesWithError(NOT_LEADER_FOR_PARTITION, INVALID_REQUEST, 10L, 100L, 10L, 100L);
        // Test the second partition has error.
        testGetOffsetsForTimesWithError(NONE, NOT_LEADER_FOR_PARTITION, 10L, 100L, 10L, 100L);
        // Test different errors.
        testGetOffsetsForTimesWithError(NOT_LEADER_FOR_PARTITION, NONE, 10L, 100L, 10L, 100L);
        testGetOffsetsForTimesWithError(UNKNOWN_TOPIC_OR_PARTITION, NONE, 10L, 100L, 10L, 100L);
        testGetOffsetsForTimesWithError(UNSUPPORTED_FOR_MESSAGE_FORMAT, NONE, 10L, 100L, null, 100L);
        testGetOffsetsForTimesWithError(BROKER_NOT_AVAILABLE, NONE, 10L, 100L, 10L, 100L);
    }

    @Test
    public void testGetOffsetsFencedLeaderEpoch() {
        buildFetcher();
        subscriptions.assignFromUser(Collections.singleton(tp0));
        client.updateMetadata(initialUpdateResponse);
        subscriptions.requestOffsetReset(tp0, LATEST);
        client.prepareResponse(listOffsetResponse(FENCED_LEADER_EPOCH, 1L, 5L));
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertTrue(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertFalse(subscriptions.isFetchable(tp0));
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        Assert.assertEquals(0L, metadata.timeToNextUpdate(time.milliseconds()));
    }

    @Test
    public void testGetOffsetsUnknownLeaderEpoch() {
        buildFetcher();
        subscriptions.assignFromUser(Collections.singleton(tp0));
        subscriptions.requestOffsetReset(tp0, LATEST);
        client.prepareResponse(listOffsetResponse(UNKNOWN_LEADER_EPOCH, 1L, 5L));
        fetcher.resetOffsetsIfNeeded();
        consumerClient.pollNoWakeup();
        Assert.assertTrue(subscriptions.isOffsetResetNeeded(tp0));
        Assert.assertFalse(subscriptions.isFetchable(tp0));
        Assert.assertFalse(subscriptions.hasValidPosition(tp0));
        Assert.assertEquals(0L, metadata.timeToNextUpdate(time.milliseconds()));
    }

    @Test
    public void testGetOffsetsIncludesLeaderEpoch() {
        buildFetcher();
        subscriptions.assignFromUser(Collections.singleton(tp0));
        client.updateMetadata(initialUpdateResponse);
        // Metadata update with leader epochs
        MetadataResponse metadataResponse = TestUtils.metadataUpdateWith("dummy", 1, Collections.emptyMap(), Collections.singletonMap(topicName, 4), ( error, partition, leader, leaderEpoch, replicas, isr, offlineReplicas) -> new MetadataResponse.PartitionMetadata(error, partition, leader, Optional.of(99), replicas, Collections.emptyList(), offlineReplicas));
        client.updateMetadata(metadataResponse);
        // Request latest offset
        subscriptions.requestOffsetReset(tp0);
        fetcher.resetOffsetsIfNeeded();
        // Check for epoch in outgoing request
        MockClient.RequestMatcher matcher = ( body) -> {
            if (body instanceof ListOffsetRequest) {
                ListOffsetRequest offsetRequest = ((ListOffsetRequest) (body));
                Optional<Integer> epoch = offsetRequest.partitionTimestamps().get(tp0).currentLeaderEpoch;
                Assert.assertTrue("Expected Fetcher to set leader epoch in request", epoch.isPresent());
                Assert.assertEquals("Expected leader epoch to match epoch from metadata update", epoch.get().longValue(), 99);
                return true;
            } else {
                Assert.fail("Should have seen ListOffsetRequest");
                return false;
            }
        };
        client.prepareResponse(matcher, listOffsetResponse(NONE, 1L, 5L));
        consumerClient.pollNoWakeup();
    }

    @Test
    public void testGetOffsetsForTimesWhenSomeTopicPartitionLeadersNotKnownInitially() {
        buildFetcher();
        final String anotherTopic = "another-topic";
        final TopicPartition t2p0 = new TopicPartition(anotherTopic, 0);
        client.reset();
        // Metadata initially has one topic
        MetadataResponse initialMetadata = TestUtils.metadataUpdateWith(3, Collections.singletonMap(topicName, 2));
        client.updateMetadata(initialMetadata);
        // The first metadata refresh should contain one topic
        client.prepareMetadataUpdate(initialMetadata);
        client.prepareResponseFrom(listOffsetResponse(tp0, NONE, 1000L, 11L), metadata.fetch().leaderFor(tp0));
        client.prepareResponseFrom(listOffsetResponse(tp1, NONE, 1000L, 32L), metadata.fetch().leaderFor(tp1));
        // Second metadata refresh should contain two topics
        Map<String, Integer> partitionNumByTopic = new HashMap<>();
        partitionNumByTopic.put(topicName, 2);
        partitionNumByTopic.put(anotherTopic, 1);
        MetadataResponse updatedMetadata = TestUtils.metadataUpdateWith(3, partitionNumByTopic);
        client.prepareMetadataUpdate(updatedMetadata);
        client.prepareResponseFrom(listOffsetResponse(t2p0, NONE, 1000L, 54L), metadata.fetch().leaderFor(t2p0));
        Map<TopicPartition, Long> timestampToSearch = new HashMap<>();
        timestampToSearch.put(tp0, LATEST_TIMESTAMP);
        timestampToSearch.put(tp1, LATEST_TIMESTAMP);
        timestampToSearch.put(t2p0, LATEST_TIMESTAMP);
        Map<TopicPartition, OffsetAndTimestamp> offsetAndTimestampMap = fetcher.offsetsForTimes(timestampToSearch, time.timer(Long.MAX_VALUE));
        Assert.assertNotNull(("Expect Fetcher.offsetsForTimes() to return non-null result for " + (tp0)), offsetAndTimestampMap.get(tp0));
        Assert.assertNotNull(("Expect Fetcher.offsetsForTimes() to return non-null result for " + (tp1)), offsetAndTimestampMap.get(tp1));
        Assert.assertNotNull(("Expect Fetcher.offsetsForTimes() to return non-null result for " + t2p0), offsetAndTimestampMap.get(t2p0));
        Assert.assertEquals(11L, offsetAndTimestampMap.get(tp0).offset());
        Assert.assertEquals(32L, offsetAndTimestampMap.get(tp1).offset());
        Assert.assertEquals(54L, offsetAndTimestampMap.get(t2p0).offset());
    }

    @Test(expected = TimeoutException.class)
    public void testBatchedListOffsetsMetadataErrors() {
        buildFetcher();
        Map<TopicPartition, ListOffsetResponse.PartitionData> partitionData = new HashMap<>();
        partitionData.put(tp0, new ListOffsetResponse.PartitionData(Errors.NOT_LEADER_FOR_PARTITION, ListOffsetResponse.UNKNOWN_TIMESTAMP, ListOffsetResponse.UNKNOWN_OFFSET, Optional.empty()));
        partitionData.put(tp1, new ListOffsetResponse.PartitionData(Errors.UNKNOWN_TOPIC_OR_PARTITION, ListOffsetResponse.UNKNOWN_TIMESTAMP, ListOffsetResponse.UNKNOWN_OFFSET, Optional.empty()));
        client.prepareResponse(new ListOffsetResponse(0, partitionData));
        Map<TopicPartition, Long> offsetsToSearch = new HashMap<>();
        offsetsToSearch.put(tp0, EARLIEST_TIMESTAMP);
        offsetsToSearch.put(tp1, EARLIEST_TIMESTAMP);
        fetcher.offsetsForTimes(offsetsToSearch, timer(0));
    }

    @Test
    public void testSkippingAbortedTransactions() {
        buildFetcher(EARLIEST, new ByteArrayDeserializer(), new ByteArrayDeserializer(), Integer.MAX_VALUE, READ_COMMITTED);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int currentOffset = 0;
        currentOffset += appendTransactionalRecords(buffer, 1L, currentOffset, new SimpleRecord(time.milliseconds(), "key".getBytes(), "value".getBytes()), new SimpleRecord(time.milliseconds(), "key".getBytes(), "value".getBytes()));
        abortTransaction(buffer, 1L, currentOffset);
        buffer.flip();
        List<FetchResponse.AbortedTransaction> abortedTransactions = new ArrayList<>();
        abortedTransactions.add(new FetchResponse.AbortedTransaction(1, 0));
        MemoryRecords records = MemoryRecords.readableRecords(buffer);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(fullFetchResponseWithAbortedTransactions(records, abortedTransactions, NONE, 100L, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> fetchedRecords = fetchedRecords();
        Assert.assertFalse(fetchedRecords.containsKey(tp0));
    }

    @Test
    public void testReturnCommittedTransactions() {
        buildFetcher(EARLIEST, new ByteArrayDeserializer(), new ByteArrayDeserializer(), Integer.MAX_VALUE, READ_COMMITTED);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int currentOffset = 0;
        currentOffset += appendTransactionalRecords(buffer, 1L, currentOffset, new SimpleRecord(time.milliseconds(), "key".getBytes(), "value".getBytes()), new SimpleRecord(time.milliseconds(), "key".getBytes(), "value".getBytes()));
        currentOffset += commitTransaction(buffer, 1L, currentOffset);
        buffer.flip();
        List<FetchResponse.AbortedTransaction> abortedTransactions = new ArrayList<>();
        MemoryRecords records = MemoryRecords.readableRecords(buffer);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                FetchRequest request = ((FetchRequest) (body));
                Assert.assertEquals(READ_COMMITTED, request.isolationLevel());
                return true;
            }
        }, fullFetchResponseWithAbortedTransactions(records, abortedTransactions, NONE, 100L, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> fetchedRecords = fetchedRecords();
        Assert.assertTrue(fetchedRecords.containsKey(tp0));
        Assert.assertEquals(fetchedRecords.get(tp0).size(), 2);
    }

    @Test
    public void testReadCommittedWithCommittedAndAbortedTransactions() {
        buildFetcher(EARLIEST, new ByteArrayDeserializer(), new ByteArrayDeserializer(), Integer.MAX_VALUE, READ_COMMITTED);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        List<FetchResponse.AbortedTransaction> abortedTransactions = new ArrayList<>();
        long pid1 = 1L;
        long pid2 = 2L;
        // Appends for producer 1 (eventually committed)
        appendTransactionalRecords(buffer, pid1, 0L, new SimpleRecord("commit1-1".getBytes(), "value".getBytes()), new SimpleRecord("commit1-2".getBytes(), "value".getBytes()));
        // Appends for producer 2 (eventually aborted)
        appendTransactionalRecords(buffer, pid2, 2L, new SimpleRecord("abort2-1".getBytes(), "value".getBytes()));
        // commit producer 1
        commitTransaction(buffer, pid1, 3L);
        // append more for producer 2 (eventually aborted)
        appendTransactionalRecords(buffer, pid2, 4L, new SimpleRecord("abort2-2".getBytes(), "value".getBytes()));
        // abort producer 2
        abortTransaction(buffer, pid2, 5L);
        abortedTransactions.add(new FetchResponse.AbortedTransaction(pid2, 2L));
        // New transaction for producer 1 (eventually aborted)
        appendTransactionalRecords(buffer, pid1, 6L, new SimpleRecord("abort1-1".getBytes(), "value".getBytes()));
        // New transaction for producer 2 (eventually committed)
        appendTransactionalRecords(buffer, pid2, 7L, new SimpleRecord("commit2-1".getBytes(), "value".getBytes()));
        // Add messages for producer 1 (eventually aborted)
        appendTransactionalRecords(buffer, pid1, 8L, new SimpleRecord("abort1-2".getBytes(), "value".getBytes()));
        // abort producer 1
        abortTransaction(buffer, pid1, 9L);
        abortedTransactions.add(new FetchResponse.AbortedTransaction(1, 6));
        // commit producer 2
        commitTransaction(buffer, pid2, 10L);
        buffer.flip();
        MemoryRecords records = MemoryRecords.readableRecords(buffer);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(fullFetchResponseWithAbortedTransactions(records, abortedTransactions, NONE, 100L, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> fetchedRecords = fetchedRecords();
        Assert.assertTrue(fetchedRecords.containsKey(tp0));
        // There are only 3 committed records
        List<ConsumerRecord<byte[], byte[]>> fetchedConsumerRecords = fetchedRecords.get(tp0);
        Set<String> fetchedKeys = new HashSet<>();
        for (ConsumerRecord<byte[], byte[]> consumerRecord : fetchedConsumerRecords) {
            fetchedKeys.add(new String(consumerRecord.key(), StandardCharsets.UTF_8));
        }
        Assert.assertEquals(Utils.mkSet("commit1-1", "commit1-2", "commit2-1"), fetchedKeys);
    }

    @Test
    public void testMultipleAbortMarkers() {
        buildFetcher(EARLIEST, new ByteArrayDeserializer(), new ByteArrayDeserializer(), Integer.MAX_VALUE, READ_COMMITTED);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int currentOffset = 0;
        currentOffset += appendTransactionalRecords(buffer, 1L, currentOffset, new SimpleRecord(time.milliseconds(), "abort1-1".getBytes(), "value".getBytes()), new SimpleRecord(time.milliseconds(), "abort1-2".getBytes(), "value".getBytes()));
        currentOffset += abortTransaction(buffer, 1L, currentOffset);
        // Duplicate abort -- should be ignored.
        currentOffset += abortTransaction(buffer, 1L, currentOffset);
        // Now commit a transaction.
        currentOffset += appendTransactionalRecords(buffer, 1L, currentOffset, new SimpleRecord(time.milliseconds(), "commit1-1".getBytes(), "value".getBytes()), new SimpleRecord(time.milliseconds(), "commit1-2".getBytes(), "value".getBytes()));
        commitTransaction(buffer, 1L, currentOffset);
        buffer.flip();
        List<FetchResponse.AbortedTransaction> abortedTransactions = new ArrayList<>();
        abortedTransactions.add(new FetchResponse.AbortedTransaction(1, 0));
        MemoryRecords records = MemoryRecords.readableRecords(buffer);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(fullFetchResponseWithAbortedTransactions(records, abortedTransactions, NONE, 100L, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> fetchedRecords = fetchedRecords();
        Assert.assertTrue(fetchedRecords.containsKey(tp0));
        Assert.assertEquals(fetchedRecords.get(tp0).size(), 2);
        List<ConsumerRecord<byte[], byte[]>> fetchedConsumerRecords = fetchedRecords.get(tp0);
        Set<String> committedKeys = new HashSet<>(Arrays.asList("commit1-1", "commit1-2"));
        Set<String> actuallyCommittedKeys = new HashSet<>();
        for (ConsumerRecord<byte[], byte[]> consumerRecord : fetchedConsumerRecords) {
            actuallyCommittedKeys.add(new String(consumerRecord.key(), StandardCharsets.UTF_8));
        }
        Assert.assertTrue(actuallyCommittedKeys.equals(committedKeys));
    }

    @Test
    public void testReadCommittedAbortMarkerWithNoData() {
        buildFetcher(EARLIEST, new StringDeserializer(), new StringDeserializer(), Integer.MAX_VALUE, READ_COMMITTED);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        long producerId = 1L;
        abortTransaction(buffer, producerId, 5L);
        appendTransactionalRecords(buffer, producerId, 6L, new SimpleRecord("6".getBytes(), null), new SimpleRecord("7".getBytes(), null), new SimpleRecord("8".getBytes(), null));
        commitTransaction(buffer, producerId, 9L);
        buffer.flip();
        // send the fetch
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        // prepare the response. the aborted transactions begin at offsets which are no longer in the log
        List<FetchResponse.AbortedTransaction> abortedTransactions = new ArrayList<>();
        abortedTransactions.add(new FetchResponse.AbortedTransaction(producerId, 0L));
        client.prepareResponse(fullFetchResponseWithAbortedTransactions(MemoryRecords.readableRecords(buffer), abortedTransactions, NONE, 100L, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<String, String>>> allFetchedRecords = fetchedRecords();
        Assert.assertTrue(allFetchedRecords.containsKey(tp0));
        List<ConsumerRecord<String, String>> fetchedRecords = allFetchedRecords.get(tp0);
        Assert.assertEquals(3, fetchedRecords.size());
        Assert.assertEquals(Arrays.asList(6L, 7L, 8L), collectRecordOffsets(fetchedRecords));
    }

    @Test
    public void testUpdatePositionWithLastRecordMissingFromBatch() {
        buildFetcher();
        MemoryRecords records = MemoryRecords.withRecords(CompressionType.NONE, new SimpleRecord("0".getBytes(), "v".getBytes()), new SimpleRecord("1".getBytes(), "v".getBytes()), new SimpleRecord("2".getBytes(), "v".getBytes()), new SimpleRecord(null, "value".getBytes()));
        // Remove the last record to simulate compaction
        MemoryRecords.FilterResult result = records.filterTo(tp0, new MemoryRecords.RecordFilter() {
            @Override
            protected BatchRetention checkBatchRetention(RecordBatch batch) {
                return DELETE_EMPTY;
            }

            @Override
            protected boolean shouldRetainRecord(RecordBatch recordBatch, Record record) {
                return (record.key()) != null;
            }
        }, ByteBuffer.allocate(1024), Integer.MAX_VALUE, NO_CACHING);
        result.outputBuffer().flip();
        MemoryRecords compactedRecords = MemoryRecords.readableRecords(result.outputBuffer());
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, compactedRecords, NONE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> allFetchedRecords = fetchedRecords();
        Assert.assertTrue(allFetchedRecords.containsKey(tp0));
        List<ConsumerRecord<byte[], byte[]>> fetchedRecords = allFetchedRecords.get(tp0);
        Assert.assertEquals(3, fetchedRecords.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(Integer.toString(i), new String(fetchedRecords.get(i).key()));
        }
        // The next offset should point to the next batch
        Assert.assertEquals(4L, subscriptions.position(tp0).longValue());
    }

    @Test
    public void testUpdatePositionOnEmptyBatch() {
        buildFetcher();
        long producerId = 1;
        short producerEpoch = 0;
        int sequence = 1;
        long baseOffset = 37;
        long lastOffset = 54;
        int partitionLeaderEpoch = 7;
        ByteBuffer buffer = ByteBuffer.allocate(RECORD_BATCH_OVERHEAD);
        DefaultRecordBatch.writeEmptyHeader(buffer, CURRENT_MAGIC_VALUE, producerId, producerEpoch, sequence, baseOffset, lastOffset, partitionLeaderEpoch, CREATE_TIME, System.currentTimeMillis(), false, false);
        buffer.flip();
        MemoryRecords recordsWithEmptyBatch = MemoryRecords.readableRecords(buffer);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        client.prepareResponse(fullFetchResponse(tp0, recordsWithEmptyBatch, NONE, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> allFetchedRecords = fetchedRecords();
        Assert.assertTrue(allFetchedRecords.isEmpty());
        // The next offset should point to the next batch
        Assert.assertEquals((lastOffset + 1), subscriptions.position(tp0).longValue());
    }

    @Test
    public void testReadCommittedWithCompactedTopic() {
        buildFetcher(EARLIEST, new StringDeserializer(), new StringDeserializer(), Integer.MAX_VALUE, READ_COMMITTED);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        long pid1 = 1L;
        long pid2 = 2L;
        long pid3 = 3L;
        appendTransactionalRecords(buffer, pid3, 3L, new SimpleRecord("3".getBytes(), "value".getBytes()), new SimpleRecord("4".getBytes(), "value".getBytes()));
        appendTransactionalRecords(buffer, pid2, 15L, new SimpleRecord("15".getBytes(), "value".getBytes()), new SimpleRecord("16".getBytes(), "value".getBytes()), new SimpleRecord("17".getBytes(), "value".getBytes()));
        appendTransactionalRecords(buffer, pid1, 22L, new SimpleRecord("22".getBytes(), "value".getBytes()), new SimpleRecord("23".getBytes(), "value".getBytes()));
        abortTransaction(buffer, pid2, 28L);
        appendTransactionalRecords(buffer, pid3, 30L, new SimpleRecord("30".getBytes(), "value".getBytes()), new SimpleRecord("31".getBytes(), "value".getBytes()), new SimpleRecord("32".getBytes(), "value".getBytes()));
        commitTransaction(buffer, pid3, 35L);
        appendTransactionalRecords(buffer, pid1, 39L, new SimpleRecord("39".getBytes(), "value".getBytes()), new SimpleRecord("40".getBytes(), "value".getBytes()));
        // transaction from pid1 is aborted, but the marker is not included in the fetch
        buffer.flip();
        // send the fetch
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        Assert.assertEquals(1, fetcher.sendFetches());
        // prepare the response. the aborted transactions begin at offsets which are no longer in the log
        List<FetchResponse.AbortedTransaction> abortedTransactions = new ArrayList<>();
        abortedTransactions.add(new FetchResponse.AbortedTransaction(pid2, 6L));
        abortedTransactions.add(new FetchResponse.AbortedTransaction(pid1, 0L));
        client.prepareResponse(fullFetchResponseWithAbortedTransactions(MemoryRecords.readableRecords(buffer), abortedTransactions, NONE, 100L, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<String, String>>> allFetchedRecords = fetchedRecords();
        Assert.assertTrue(allFetchedRecords.containsKey(tp0));
        List<ConsumerRecord<String, String>> fetchedRecords = allFetchedRecords.get(tp0);
        Assert.assertEquals(5, fetchedRecords.size());
        Assert.assertEquals(Arrays.asList(3L, 4L, 30L, 31L, 32L), collectRecordOffsets(fetchedRecords));
    }

    @Test
    public void testReturnAbortedTransactionsinUncommittedMode() {
        buildFetcher(EARLIEST, new ByteArrayDeserializer(), new ByteArrayDeserializer(), Integer.MAX_VALUE, READ_UNCOMMITTED);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int currentOffset = 0;
        currentOffset += appendTransactionalRecords(buffer, 1L, currentOffset, new SimpleRecord(time.milliseconds(), "key".getBytes(), "value".getBytes()), new SimpleRecord(time.milliseconds(), "key".getBytes(), "value".getBytes()));
        abortTransaction(buffer, 1L, currentOffset);
        buffer.flip();
        List<FetchResponse.AbortedTransaction> abortedTransactions = new ArrayList<>();
        abortedTransactions.add(new FetchResponse.AbortedTransaction(1, 0));
        MemoryRecords records = MemoryRecords.readableRecords(buffer);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(fullFetchResponseWithAbortedTransactions(records, abortedTransactions, NONE, 100L, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> fetchedRecords = fetchedRecords();
        Assert.assertTrue(fetchedRecords.containsKey(tp0));
    }

    @Test
    public void testConsumerPositionUpdatedWhenSkippingAbortedTransactions() {
        buildFetcher(EARLIEST, new ByteArrayDeserializer(), new ByteArrayDeserializer(), Integer.MAX_VALUE, READ_COMMITTED);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        long currentOffset = 0;
        currentOffset += appendTransactionalRecords(buffer, 1L, currentOffset, new SimpleRecord(time.milliseconds(), "abort1-1".getBytes(), "value".getBytes()), new SimpleRecord(time.milliseconds(), "abort1-2".getBytes(), "value".getBytes()));
        currentOffset += abortTransaction(buffer, 1L, currentOffset);
        buffer.flip();
        List<FetchResponse.AbortedTransaction> abortedTransactions = new ArrayList<>();
        abortedTransactions.add(new FetchResponse.AbortedTransaction(1, 0));
        MemoryRecords records = MemoryRecords.readableRecords(buffer);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(fullFetchResponseWithAbortedTransactions(records, abortedTransactions, NONE, 100L, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> fetchedRecords = fetchedRecords();
        // Ensure that we don't return any of the aborted records, but yet advance the consumer position.
        Assert.assertFalse(fetchedRecords.containsKey(tp0));
        Assert.assertEquals(currentOffset, ((long) (subscriptions.position(tp0))));
    }

    @Test
    public void testConsumingViaIncrementalFetchRequests() {
        buildFetcher(2);
        List<ConsumerRecord<byte[], byte[]>> records;
        assignFromUser(new HashSet(Arrays.asList(tp0, tp1)));
        subscriptions.seek(tp0, 0);
        subscriptions.seek(tp1, 1);
        // Fetch some records and establish an incremental fetch session.
        LinkedHashMap<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> partitions1 = new LinkedHashMap<>();
        partitions1.put(tp0, new FetchResponse.PartitionData<>(Errors.NONE, 2L, 2, 0L, null, this.records));
        partitions1.put(tp1, new FetchResponse.PartitionData<>(Errors.NONE, 100L, FetchResponse.INVALID_LAST_STABLE_OFFSET, 0L, null, emptyRecords));
        FetchResponse resp1 = new FetchResponse(Errors.NONE, partitions1, 0, 123);
        client.prepareResponse(resp1);
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> fetchedRecords = fetchedRecords();
        Assert.assertFalse(fetchedRecords.containsKey(tp1));
        records = fetchedRecords.get(tp0);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(3L, subscriptions.position(tp0).longValue());
        Assert.assertEquals(1L, subscriptions.position(tp1).longValue());
        Assert.assertEquals(1, records.get(0).offset());
        Assert.assertEquals(2, records.get(1).offset());
        // There is still a buffered record.
        Assert.assertEquals(0, fetcher.sendFetches());
        fetchedRecords = fetchedRecords();
        Assert.assertFalse(fetchedRecords.containsKey(tp1));
        records = fetchedRecords.get(tp0);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(3, records.get(0).offset());
        Assert.assertEquals(4L, subscriptions.position(tp0).longValue());
        // The second response contains no new records.
        LinkedHashMap<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> partitions2 = new LinkedHashMap<>();
        FetchResponse resp2 = new FetchResponse(Errors.NONE, partitions2, 0, 123);
        client.prepareResponse(resp2);
        Assert.assertEquals(1, fetcher.sendFetches());
        consumerClient.poll(timer(0));
        fetchedRecords = fetchedRecords();
        Assert.assertTrue(fetchedRecords.isEmpty());
        Assert.assertEquals(4L, subscriptions.position(tp0).longValue());
        Assert.assertEquals(1L, subscriptions.position(tp1).longValue());
        // The third response contains some new records for tp0.
        LinkedHashMap<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> partitions3 = new LinkedHashMap<>();
        partitions3.put(tp0, new FetchResponse.PartitionData<>(Errors.NONE, 100L, 4, 0L, null, this.nextRecords));
        FetchResponse resp3 = new FetchResponse(Errors.NONE, partitions3, 0, 123);
        client.prepareResponse(resp3);
        Assert.assertEquals(1, fetcher.sendFetches());
        consumerClient.poll(timer(0));
        fetchedRecords = fetchedRecords();
        Assert.assertFalse(fetchedRecords.containsKey(tp1));
        records = fetchedRecords.get(tp0);
        Assert.assertEquals(2, records.size());
        Assert.assertEquals(6L, subscriptions.position(tp0).longValue());
        Assert.assertEquals(1L, subscriptions.position(tp1).longValue());
        Assert.assertEquals(4, records.get(0).offset());
        Assert.assertEquals(5, records.get(1).offset());
    }

    @Test
    public void testFetcherConcurrency() throws Exception {
        int numPartitions = 20;
        Set<TopicPartition> topicPartitions = new HashSet<>();
        for (int i = 0; i < numPartitions; i++)
            topicPartitions.add(new TopicPartition(topicName, i));

        buildDependencies(new MetricConfig(), EARLIEST);
        fetcher = new Fetcher<byte[], byte[]>(new LogContext(), consumerClient, minBytes, maxBytes, maxWaitMs, fetchSize, (2 * numPartitions), true, new ByteArrayDeserializer(), new ByteArrayDeserializer(), metadata, subscriptions, metrics, metricsRegistry, time, retryBackoffMs, requestTimeoutMs, IsolationLevel.READ_UNCOMMITTED) {
            @Override
            protected FetchSessionHandler sessionHandler(int id) {
                final FetchSessionHandler handler = super.sessionHandler(id);
                if (handler == null)
                    return null;
                else {
                    return new FetchSessionHandler(new LogContext(), id) {
                        @Override
                        public Builder newBuilder() {
                            verifySessionPartitions();
                            return handler.newBuilder();
                        }

                        @Override
                        public boolean handleResponse(FetchResponse response) {
                            verifySessionPartitions();
                            return handler.handleResponse(response);
                        }

                        @Override
                        public void handleError(Throwable t) {
                            verifySessionPartitions();
                            handler.handleError(t);
                        }

                        // Verify that session partitions can be traversed safely.
                        private void verifySessionPartitions() {
                            try {
                                Field field = FetchSessionHandler.class.getDeclaredField("sessionPartitions");
                                field.setAccessible(true);
                                LinkedHashMap<?, ?> sessionPartitions = ((LinkedHashMap<?, ?>) (field.get(handler)));
                                for (Map.Entry<?, ?> entry : sessionPartitions.entrySet()) {
                                    // If `sessionPartitions` are modified on another thread, Thread.yield will increase the
                                    // possibility of ConcurrentModificationException if appropriate synchronization is not used.
                                    Thread.yield();
                                }
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
            }
        };
        MetadataResponse initialMetadataResponse = TestUtils.metadataUpdateWith(1, Collections.singletonMap(topicName, numPartitions));
        client.updateMetadata(initialMetadataResponse);
        fetchSize = 10000;
        assignFromUser(topicPartitions);
        topicPartitions.forEach(( tp) -> subscriptions.seek(tp, 0L));
        AtomicInteger fetchesRemaining = new AtomicInteger(1000);
        executorService = Executors.newSingleThreadExecutor();
        Future<?> future = executorService.submit(() -> {
            while ((fetchesRemaining.get()) > 0) {
                synchronized(consumerClient) {
                    if (!(client.requests().isEmpty())) {
                        ClientRequest request = client.requests().peek();
                        FetchRequest fetchRequest = ((FetchRequest) (request.requestBuilder().build()));
                        LinkedHashMap<TopicPartition, FetchResponse.PartitionData<MemoryRecords>> responseMap = new LinkedHashMap<>();
                        for (Map.Entry<TopicPartition, FetchRequest.PartitionData> entry : fetchRequest.fetchData().entrySet()) {
                            TopicPartition tp = entry.getKey();
                            long offset = entry.getValue().fetchOffset;
                            responseMap.put(tp, new FetchResponse.PartitionData<>(Errors.NONE, (offset + 2L), (offset + 2), 0L, null, buildRecords(offset, 2, offset)));
                        }
                        client.respondToRequest(request, new FetchResponse(Errors.NONE, responseMap, 0, 123));
                        consumerClient.poll(timer(0));
                    }
                }
            } 
            return fetchesRemaining.get();
        });
        Map<TopicPartition, Long> nextFetchOffsets = topicPartitions.stream().collect(Collectors.toMap(Function.identity(), ( t) -> 0L));
        while (((fetchesRemaining.get()) > 0) && (!(future.isDone()))) {
            if ((fetcher.sendFetches()) == 1) {
                synchronized(consumerClient) {
                    consumerClient.poll(timer(0));
                }
            }
            if (fetcher.hasCompletedFetches()) {
                Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> fetchedRecords = fetchedRecords();
                if (!(fetchedRecords.isEmpty())) {
                    fetchesRemaining.decrementAndGet();
                    fetchedRecords.entrySet().forEach(( entry) -> {
                        TopicPartition tp = entry.getKey();
                        List<ConsumerRecord<byte[], byte[]>> records = entry.getValue();
                        assertEquals(2, records.size());
                        long nextOffset = nextFetchOffsets.get(tp);
                        assertEquals(nextOffset, records.get(0).offset());
                        assertEquals((nextOffset + 1), records.get(1).offset());
                        nextFetchOffsets.put(tp, (nextOffset + 2));
                    });
                }
            }
        } 
        Assert.assertEquals(0, future.get());
    }

    @Test
    public void testEmptyControlBatch() {
        buildFetcher(EARLIEST, new ByteArrayDeserializer(), new ByteArrayDeserializer(), Integer.MAX_VALUE, READ_COMMITTED);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int currentOffset = 1;
        // Empty control batch should not cause an exception
        DefaultRecordBatch.writeEmptyHeader(buffer, MAGIC_VALUE_V2, 1L, ((short) (0)), (-1), 0, 0, NO_PARTITION_LEADER_EPOCH, CREATE_TIME, time.milliseconds(), true, true);
        currentOffset += appendTransactionalRecords(buffer, 1L, currentOffset, new SimpleRecord(time.milliseconds(), "key".getBytes(), "value".getBytes()), new SimpleRecord(time.milliseconds(), "key".getBytes(), "value".getBytes()));
        commitTransaction(buffer, 1L, currentOffset);
        buffer.flip();
        List<FetchResponse.AbortedTransaction> abortedTransactions = new ArrayList<>();
        MemoryRecords records = MemoryRecords.readableRecords(buffer);
        assignFromUser(Collections.singleton(tp0));
        subscriptions.seek(tp0, 0);
        // normal fetch
        Assert.assertEquals(1, fetcher.sendFetches());
        Assert.assertFalse(fetcher.hasCompletedFetches());
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                FetchRequest request = ((FetchRequest) (body));
                Assert.assertEquals(READ_COMMITTED, request.isolationLevel());
                return true;
            }
        }, fullFetchResponseWithAbortedTransactions(records, abortedTransactions, NONE, 100L, 100L, 0));
        consumerClient.poll(timer(0));
        Assert.assertTrue(fetcher.hasCompletedFetches());
        Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> fetchedRecords = fetchedRecords();
        Assert.assertTrue(fetchedRecords.containsKey(tp0));
        Assert.assertEquals(fetchedRecords.get(tp0).size(), 2);
    }
}

