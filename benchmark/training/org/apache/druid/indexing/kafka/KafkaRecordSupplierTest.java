/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.indexing.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.curator.test.TestingCluster;
import org.apache.druid.indexing.kafka.test.TestBroker;
import org.apache.druid.indexing.seekablestream.common.OrderedPartitionableRecord;
import org.apache.druid.indexing.seekablestream.common.StreamPartition;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.segment.TestHelper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.Test;


public class KafkaRecordSupplierTest {
    private static final Logger log = new Logger(KafkaRecordSupplierTest.class);

    private static String topic = "topic";

    private static long poll_timeout_millis = 1000;

    private static int pollRetry = 5;

    private static int topicPosFix = 0;

    private static final ObjectMapper objectMapper = TestHelper.makeJsonMapper();

    private static TestingCluster zkServer;

    private static TestBroker kafkaServer;

    private List<ProducerRecord<byte[], byte[]>> records;

    @Test
    public void testSupplierSetup() throws InterruptedException, ExecutionException {
        // Insert data
        insertData();
        Set<StreamPartition<Integer>> partitions = ImmutableSet.of(StreamPartition.of(KafkaRecordSupplierTest.topic, 0), StreamPartition.of(KafkaRecordSupplierTest.topic, 1));
        KafkaRecordSupplier recordSupplier = new KafkaRecordSupplier(KafkaRecordSupplierTest.kafkaServer.consumerProperties(), KafkaRecordSupplierTest.objectMapper);
        Assert.assertTrue(recordSupplier.getAssignment().isEmpty());
        recordSupplier.assign(partitions);
        Assert.assertEquals(partitions, recordSupplier.getAssignment());
        Assert.assertEquals(ImmutableSet.of(0, 1), recordSupplier.getPartitionIds(KafkaRecordSupplierTest.topic));
        recordSupplier.close();
    }

    @Test
    public void testPoll() throws InterruptedException, ExecutionException {
        // Insert data
        insertData();
        Set<StreamPartition<Integer>> partitions = ImmutableSet.of(StreamPartition.of(KafkaRecordSupplierTest.topic, 0), StreamPartition.of(KafkaRecordSupplierTest.topic, 1));
        KafkaRecordSupplier recordSupplier = new KafkaRecordSupplier(KafkaRecordSupplierTest.kafkaServer.consumerProperties(), KafkaRecordSupplierTest.objectMapper);
        recordSupplier.assign(partitions);
        recordSupplier.seekToEarliest(partitions);
        List<OrderedPartitionableRecord<Integer, Long>> initialRecords = new java.util.ArrayList(createOrderedPartitionableRecords());
        List<OrderedPartitionableRecord<Integer, Long>> polledRecords = recordSupplier.poll(KafkaRecordSupplierTest.poll_timeout_millis);
        for (int i = 0; ((polledRecords.size()) != (initialRecords.size())) && (i < (KafkaRecordSupplierTest.pollRetry)); i++) {
            polledRecords.addAll(recordSupplier.poll(KafkaRecordSupplierTest.poll_timeout_millis));
            Thread.sleep(200);
        }
        Assert.assertEquals(partitions, recordSupplier.getAssignment());
        Assert.assertEquals(initialRecords.size(), polledRecords.size());
        Assert.assertTrue(initialRecords.containsAll(polledRecords));
        recordSupplier.close();
    }

    @Test
    public void testPollAfterMoreDataAdded() throws InterruptedException, ExecutionException {
        // Insert data
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaRecordSupplierTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (ProducerRecord<byte[], byte[]> record : records.subList(0, 13)) {
                kafkaProducer.send(record).get();
            }
            kafkaProducer.commitTransaction();
        }
        Set<StreamPartition<Integer>> partitions = ImmutableSet.of(StreamPartition.of(KafkaRecordSupplierTest.topic, 0), StreamPartition.of(KafkaRecordSupplierTest.topic, 1));
        KafkaRecordSupplier recordSupplier = new KafkaRecordSupplier(KafkaRecordSupplierTest.kafkaServer.consumerProperties(), KafkaRecordSupplierTest.objectMapper);
        recordSupplier.assign(partitions);
        recordSupplier.seekToEarliest(partitions);
        List<OrderedPartitionableRecord<Integer, Long>> polledRecords = recordSupplier.poll(KafkaRecordSupplierTest.poll_timeout_millis);
        for (int i = 0; ((polledRecords.size()) != 13) && (i < (KafkaRecordSupplierTest.pollRetry)); i++) {
            polledRecords.addAll(recordSupplier.poll(KafkaRecordSupplierTest.poll_timeout_millis));
            Thread.sleep(200);
        }
        // Insert data
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaRecordSupplierTest.kafkaServer.newProducer()) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            for (ProducerRecord<byte[], byte[]> record : records.subList(13, 15)) {
                kafkaProducer.send(record).get();
            }
            kafkaProducer.commitTransaction();
        }
        for (int i = 0; ((polledRecords.size()) != (records.size())) && (i < (KafkaRecordSupplierTest.pollRetry)); i++) {
            polledRecords.addAll(recordSupplier.poll(KafkaRecordSupplierTest.poll_timeout_millis));
            Thread.sleep(200);
        }
        List<OrderedPartitionableRecord<Integer, Long>> initialRecords = createOrderedPartitionableRecords();
        Assert.assertEquals(records.size(), polledRecords.size());
        Assert.assertEquals(partitions, recordSupplier.getAssignment());
        final int initialRecordsPartition0Size = initialRecords.stream().filter(( r) -> r.getPartitionId().equals(0)).collect(Collectors.toSet()).size();
        final int initialRecordsPartition1Size = initialRecords.stream().filter(( r) -> r.getPartitionId().equals(1)).collect(Collectors.toSet()).size();
        final int polledRecordsPartition0Size = polledRecords.stream().filter(( r) -> r.getPartitionId().equals(0)).collect(Collectors.toSet()).size();
        final int polledRecordsPartition1Size = polledRecords.stream().filter(( r) -> r.getPartitionId().equals(1)).collect(Collectors.toSet()).size();
        Assert.assertEquals(initialRecordsPartition0Size, polledRecordsPartition0Size);
        Assert.assertEquals(initialRecordsPartition1Size, polledRecordsPartition1Size);
        recordSupplier.close();
    }

    @Test
    public void testSeek() throws InterruptedException, ExecutionException {
        // Insert data
        insertData();
        StreamPartition<Integer> partition0 = StreamPartition.of(KafkaRecordSupplierTest.topic, 0);
        StreamPartition<Integer> partition1 = StreamPartition.of(KafkaRecordSupplierTest.topic, 1);
        Set<StreamPartition<Integer>> partitions = ImmutableSet.of(StreamPartition.of(KafkaRecordSupplierTest.topic, 0), StreamPartition.of(KafkaRecordSupplierTest.topic, 1));
        KafkaRecordSupplier recordSupplier = new KafkaRecordSupplier(KafkaRecordSupplierTest.kafkaServer.consumerProperties(), KafkaRecordSupplierTest.objectMapper);
        recordSupplier.assign(partitions);
        Assert.assertEquals(0L, ((long) (recordSupplier.getEarliestSequenceNumber(partition0))));
        Assert.assertEquals(0L, ((long) (recordSupplier.getEarliestSequenceNumber(partition1))));
        recordSupplier.seek(partition0, 2L);
        recordSupplier.seek(partition1, 2L);
        List<OrderedPartitionableRecord<Integer, Long>> initialRecords = createOrderedPartitionableRecords();
        List<OrderedPartitionableRecord<Integer, Long>> polledRecords = recordSupplier.poll(KafkaRecordSupplierTest.poll_timeout_millis);
        for (int i = 0; ((polledRecords.size()) != 11) && (i < (KafkaRecordSupplierTest.pollRetry)); i++) {
            polledRecords.addAll(recordSupplier.poll(KafkaRecordSupplierTest.poll_timeout_millis));
            Thread.sleep(200);
        }
        Assert.assertEquals(11, polledRecords.size());
        Assert.assertTrue(initialRecords.containsAll(polledRecords));
        recordSupplier.close();
    }

    @Test
    public void testSeekToLatest() throws InterruptedException, ExecutionException {
        // Insert data
        insertData();
        StreamPartition<Integer> partition0 = StreamPartition.of(KafkaRecordSupplierTest.topic, 0);
        StreamPartition<Integer> partition1 = StreamPartition.of(KafkaRecordSupplierTest.topic, 1);
        Set<StreamPartition<Integer>> partitions = ImmutableSet.of(StreamPartition.of(KafkaRecordSupplierTest.topic, 0), StreamPartition.of(KafkaRecordSupplierTest.topic, 1));
        KafkaRecordSupplier recordSupplier = new KafkaRecordSupplier(KafkaRecordSupplierTest.kafkaServer.consumerProperties(), KafkaRecordSupplierTest.objectMapper);
        recordSupplier.assign(partitions);
        Assert.assertEquals(0L, ((long) (recordSupplier.getEarliestSequenceNumber(partition0))));
        Assert.assertEquals(0L, ((long) (recordSupplier.getEarliestSequenceNumber(partition1))));
        recordSupplier.seekToLatest(partitions);
        List<OrderedPartitionableRecord<Integer, Long>> polledRecords = recordSupplier.poll(KafkaRecordSupplierTest.poll_timeout_millis);
        Assert.assertEquals(Collections.emptyList(), polledRecords);
        recordSupplier.close();
    }

    @Test(expected = IllegalStateException.class)
    public void testSeekUnassigned() throws InterruptedException, ExecutionException {
        // Insert data
        try (final KafkaProducer<byte[], byte[]> kafkaProducer = KafkaRecordSupplierTest.kafkaServer.newProducer()) {
            for (ProducerRecord<byte[], byte[]> record : records) {
                kafkaProducer.send(record).get();
            }
        }
        StreamPartition<Integer> partition0 = StreamPartition.of(KafkaRecordSupplierTest.topic, 0);
        StreamPartition<Integer> partition1 = StreamPartition.of(KafkaRecordSupplierTest.topic, 1);
        Set<StreamPartition<Integer>> partitions = ImmutableSet.of(StreamPartition.of(KafkaRecordSupplierTest.topic, 0));
        KafkaRecordSupplier recordSupplier = new KafkaRecordSupplier(KafkaRecordSupplierTest.kafkaServer.consumerProperties(), KafkaRecordSupplierTest.objectMapper);
        recordSupplier.assign(partitions);
        Assert.assertEquals(0, ((long) (recordSupplier.getEarliestSequenceNumber(partition0))));
        recordSupplier.seekToEarliest(Collections.singleton(partition1));
        recordSupplier.close();
    }

    @Test
    public void testPosition() throws InterruptedException, ExecutionException {
        // Insert data
        insertData();
        StreamPartition<Integer> partition0 = StreamPartition.of(KafkaRecordSupplierTest.topic, 0);
        StreamPartition<Integer> partition1 = StreamPartition.of(KafkaRecordSupplierTest.topic, 1);
        Set<StreamPartition<Integer>> partitions = ImmutableSet.of(StreamPartition.of(KafkaRecordSupplierTest.topic, 0), StreamPartition.of(KafkaRecordSupplierTest.topic, 1));
        KafkaRecordSupplier recordSupplier = new KafkaRecordSupplier(KafkaRecordSupplierTest.kafkaServer.consumerProperties(), KafkaRecordSupplierTest.objectMapper);
        recordSupplier.assign(partitions);
        Assert.assertEquals(0L, ((long) (recordSupplier.getPosition(partition0))));
        Assert.assertEquals(0L, ((long) (recordSupplier.getPosition(partition1))));
        recordSupplier.seek(partition0, 4L);
        recordSupplier.seek(partition1, 5L);
        Assert.assertEquals(4L, ((long) (recordSupplier.getPosition(partition0))));
        Assert.assertEquals(5L, ((long) (recordSupplier.getPosition(partition1))));
        recordSupplier.seekToEarliest(Collections.singleton(partition0));
        Assert.assertEquals(0L, ((long) (recordSupplier.getPosition(partition0))));
        recordSupplier.seekToLatest(Collections.singleton(partition0));
        Assert.assertEquals(12L, ((long) (recordSupplier.getPosition(partition0))));
        long prevPos = recordSupplier.getPosition(partition0);
        recordSupplier.getEarliestSequenceNumber(partition0);
        Assert.assertEquals(prevPos, ((long) (recordSupplier.getPosition(partition0))));
        recordSupplier.getLatestSequenceNumber(partition0);
        Assert.assertEquals(prevPos, ((long) (recordSupplier.getPosition(partition0))));
        recordSupplier.close();
    }
}

