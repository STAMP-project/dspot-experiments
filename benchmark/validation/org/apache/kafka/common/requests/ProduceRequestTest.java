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
package org.apache.kafka.common.requests;


import ApiKeys.PRODUCE;
import CompressionType.NONE;
import CompressionType.ZSTD;
import MemoryRecords.EMPTY;
import ProduceRequest.Builder;
import RecordBatch.CURRENT_MAGIC_VALUE;
import RecordBatch.MAGIC_VALUE_V0;
import RecordBatch.MAGIC_VALUE_V1;
import RecordBatch.MAGIC_VALUE_V2;
import TimestampType.CREATE_TIME;
import TimestampType.NO_TIMESTAMP_TYPE;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.record.MemoryRecords;
import org.apache.kafka.common.record.MemoryRecordsBuilder;
import org.apache.kafka.common.record.RecordVersion;
import org.apache.kafka.common.record.SimpleRecord;
import org.junit.Assert;
import org.junit.Test;


public class ProduceRequestTest {
    private final SimpleRecord simpleRecord = new SimpleRecord(System.currentTimeMillis(), "key".getBytes(), "value".getBytes());

    @Test
    public void shouldBeFlaggedAsTransactionalWhenTransactionalRecords() throws Exception {
        final MemoryRecords memoryRecords = MemoryRecords.withTransactionalRecords(0, NONE, 1L, ((short) (1)), 1, 1, simpleRecord);
        final ProduceRequest request = Builder.forCurrentMagic(((short) (-1)), 10, Collections.singletonMap(new TopicPartition("topic", 1), memoryRecords)).build();
        Assert.assertTrue(request.hasTransactionalRecords());
    }

    @Test
    public void shouldNotBeFlaggedAsTransactionalWhenNoRecords() throws Exception {
        final ProduceRequest request = createNonIdempotentNonTransactionalRecords();
        Assert.assertFalse(request.hasTransactionalRecords());
    }

    @Test
    public void shouldNotBeFlaggedAsIdempotentWhenRecordsNotIdempotent() throws Exception {
        final ProduceRequest request = createNonIdempotentNonTransactionalRecords();
        Assert.assertFalse(request.hasTransactionalRecords());
    }

    @Test
    public void shouldBeFlaggedAsIdempotentWhenIdempotentRecords() throws Exception {
        final MemoryRecords memoryRecords = MemoryRecords.withIdempotentRecords(1, NONE, 1L, ((short) (1)), 1, 1, simpleRecord);
        final ProduceRequest request = Builder.forCurrentMagic(((short) (-1)), 10, Collections.singletonMap(new TopicPartition("topic", 1), memoryRecords)).build();
        Assert.assertTrue(request.hasIdempotentRecords());
    }

    @Test
    public void testBuildWithOldMessageFormat() {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V1, NONE, CREATE_TIME, 0L);
        builder.append(10L, null, "a".getBytes());
        Map<TopicPartition, MemoryRecords> produceData = new HashMap<>();
        produceData.put(new TopicPartition("test", 0), builder.build());
        ProduceRequest.Builder requestBuilder = Builder.forMagic(MAGIC_VALUE_V1, ((short) (1)), 5000, produceData, null);
        Assert.assertEquals(2, requestBuilder.oldestAllowedVersion());
        Assert.assertEquals(2, requestBuilder.latestAllowedVersion());
    }

    @Test
    public void testBuildWithCurrentMessageFormat() {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, CURRENT_MAGIC_VALUE, NONE, CREATE_TIME, 0L);
        builder.append(10L, null, "a".getBytes());
        Map<TopicPartition, MemoryRecords> produceData = new HashMap<>();
        produceData.put(new TopicPartition("test", 0), builder.build());
        ProduceRequest.Builder requestBuilder = Builder.forMagic(CURRENT_MAGIC_VALUE, ((short) (1)), 5000, produceData, null);
        Assert.assertEquals(3, requestBuilder.oldestAllowedVersion());
        Assert.assertEquals(PRODUCE.latestVersion(), requestBuilder.latestAllowedVersion());
    }

    @Test
    public void testV3AndAboveShouldContainOnlyOneRecordBatch() {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, NONE, CREATE_TIME, 0L);
        builder.append(10L, null, "a".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, NONE, CREATE_TIME, 1L);
        builder.append(11L, "1".getBytes(), "b".getBytes());
        builder.append(12L, null, "c".getBytes());
        builder.close();
        buffer.flip();
        Map<TopicPartition, MemoryRecords> produceData = new HashMap<>();
        produceData.put(new TopicPartition("test", 0), MemoryRecords.readableRecords(buffer));
        ProduceRequest.Builder requestBuilder = Builder.forCurrentMagic(((short) (1)), 5000, produceData);
        assertThrowsInvalidRecordExceptionForAllVersions(requestBuilder);
    }

    @Test
    public void testV3AndAboveCannotHaveNoRecordBatches() {
        Map<TopicPartition, MemoryRecords> produceData = new HashMap<>();
        produceData.put(new TopicPartition("test", 0), EMPTY);
        ProduceRequest.Builder requestBuilder = Builder.forCurrentMagic(((short) (1)), 5000, produceData);
        assertThrowsInvalidRecordExceptionForAllVersions(requestBuilder);
    }

    @Test
    public void testV3AndAboveCannotUseMagicV0() {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V0, NONE, NO_TIMESTAMP_TYPE, 0L);
        builder.append(10L, null, "a".getBytes());
        Map<TopicPartition, MemoryRecords> produceData = new HashMap<>();
        produceData.put(new TopicPartition("test", 0), builder.build());
        ProduceRequest.Builder requestBuilder = Builder.forCurrentMagic(((short) (1)), 5000, produceData);
        assertThrowsInvalidRecordExceptionForAllVersions(requestBuilder);
    }

    @Test
    public void testV3AndAboveCannotUseMagicV1() {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V1, NONE, CREATE_TIME, 0L);
        builder.append(10L, null, "a".getBytes());
        Map<TopicPartition, MemoryRecords> produceData = new HashMap<>();
        produceData.put(new TopicPartition("test", 0), builder.build());
        ProduceRequest.Builder requestBuilder = Builder.forCurrentMagic(((short) (1)), 5000, produceData);
        assertThrowsInvalidRecordExceptionForAllVersions(requestBuilder);
    }

    @Test
    public void testV6AndBelowCannotUseZStdCompression() {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V2, ZSTD, CREATE_TIME, 0L);
        builder.append(10L, null, "a".getBytes());
        Map<TopicPartition, MemoryRecords> produceData = new HashMap<>();
        produceData.put(new TopicPartition("test", 0), builder.build());
        // Can't create ProduceRequest instance with version within [3, 7)
        for (short version = 3; version < 7; version++) {
            ProduceRequest.Builder requestBuilder = new ProduceRequest.Builder(version, version, ((short) (1)), 5000, produceData, null);
            assertThrowsInvalidRecordExceptionForAllVersions(requestBuilder);
        }
        // Works fine with current version (>= 7)
        Builder.forCurrentMagic(((short) (1)), 5000, produceData);
    }

    @Test
    public void testMixedTransactionalData() {
        final long producerId = 15L;
        final short producerEpoch = 5;
        final int sequence = 10;
        final String transactionalId = "txnlId";
        final MemoryRecords nonTxnRecords = MemoryRecords.withRecords(NONE, new SimpleRecord("foo".getBytes()));
        final MemoryRecords txnRecords = MemoryRecords.withTransactionalRecords(NONE, producerId, producerEpoch, sequence, new SimpleRecord("bar".getBytes()));
        final Map<TopicPartition, MemoryRecords> recordsByPartition = new LinkedHashMap<>();
        recordsByPartition.put(new TopicPartition("foo", 0), txnRecords);
        recordsByPartition.put(new TopicPartition("foo", 1), nonTxnRecords);
        final ProduceRequest.Builder builder = Builder.forMagic(RecordVersion.current().value, ((short) (-1)), 5000, recordsByPartition, transactionalId);
        final ProduceRequest request = builder.build();
        Assert.assertTrue(request.hasTransactionalRecords());
        Assert.assertTrue(request.hasIdempotentRecords());
    }

    @Test
    public void testMixedIdempotentData() {
        final long producerId = 15L;
        final short producerEpoch = 5;
        final int sequence = 10;
        final MemoryRecords nonTxnRecords = MemoryRecords.withRecords(NONE, new SimpleRecord("foo".getBytes()));
        final MemoryRecords txnRecords = MemoryRecords.withIdempotentRecords(NONE, producerId, producerEpoch, sequence, new SimpleRecord("bar".getBytes()));
        final Map<TopicPartition, MemoryRecords> recordsByPartition = new LinkedHashMap<>();
        recordsByPartition.put(new TopicPartition("foo", 0), txnRecords);
        recordsByPartition.put(new TopicPartition("foo", 1), nonTxnRecords);
        final ProduceRequest.Builder builder = Builder.forMagic(RecordVersion.current().value, ((short) (-1)), 5000, recordsByPartition, null);
        final ProduceRequest request = builder.build();
        Assert.assertFalse(request.hasTransactionalRecords());
        Assert.assertTrue(request.hasIdempotentRecords());
    }
}

