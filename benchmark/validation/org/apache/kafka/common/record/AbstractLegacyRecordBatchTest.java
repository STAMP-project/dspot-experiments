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
package org.apache.kafka.common.record;


import CompressionType.GZIP;
import CompressionType.ZSTD;
import RecordBatch.MAGIC_VALUE_V0;
import RecordBatch.MAGIC_VALUE_V1;
import RecordBatch.NO_TIMESTAMP;
import TimestampType.CREATE_TIME;
import TimestampType.LOG_APPEND_TIME;
import TimestampType.NO_TIMESTAMP_TYPE;
import java.util.Arrays;
import java.util.List;
import org.apache.kafka.common.record.AbstractLegacyRecordBatch.ByteBufferLegacyRecordBatch;
import org.apache.kafka.common.utils.Utils;
import org.junit.Assert;
import org.junit.Test;


public class AbstractLegacyRecordBatchTest {
    @Test
    public void testSetLastOffsetCompressed() {
        SimpleRecord[] simpleRecords = new SimpleRecord[]{ new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()) };
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V1, 0L, GZIP, CREATE_TIME, simpleRecords);
        long lastOffset = 500L;
        long firstOffset = (lastOffset - (simpleRecords.length)) + 1;
        ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
        batch.setLastOffset(lastOffset);
        Assert.assertEquals(lastOffset, batch.lastOffset());
        Assert.assertEquals(firstOffset, batch.baseOffset());
        Assert.assertTrue(batch.isValid());
        List<MutableRecordBatch> recordBatches = Utils.toList(records.batches().iterator());
        Assert.assertEquals(1, recordBatches.size());
        Assert.assertEquals(lastOffset, recordBatches.get(0).lastOffset());
        long offset = firstOffset;
        for (Record record : records.records())
            Assert.assertEquals((offset++), record.offset());

    }

    /**
     * The wrapper offset should be 0 in v0, but not in v1. However, the latter worked by accident and some versions of
     * librdkafka now depend on it. So we support 0 for compatibility reasons, but the recommendation is to set the
     * wrapper offset to the relative offset of the last record in the batch.
     */
    @Test
    public void testIterateCompressedRecordWithWrapperOffsetZero() {
        for (byte magic : Arrays.asList(MAGIC_VALUE_V0, MAGIC_VALUE_V1)) {
            SimpleRecord[] simpleRecords = new SimpleRecord[]{ new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()) };
            MemoryRecords records = MemoryRecords.withRecords(magic, 0L, GZIP, CREATE_TIME, simpleRecords);
            ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
            batch.setLastOffset(0L);
            long offset = 0L;
            for (Record record : batch)
                Assert.assertEquals((offset++), record.offset());

        }
    }

    @Test(expected = InvalidRecordException.class)
    public void testInvalidWrapperOffsetV1() {
        SimpleRecord[] simpleRecords = new SimpleRecord[]{ new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()) };
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V1, 0L, GZIP, CREATE_TIME, simpleRecords);
        ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
        batch.setLastOffset(1L);
        batch.iterator();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNoTimestampTypeNotAllowed() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V1, 0L, GZIP, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
        batch.setMaxTimestamp(NO_TIMESTAMP_TYPE, NO_TIMESTAMP);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetLogAppendTimeNotAllowedV0() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V0, 0L, GZIP, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        long logAppendTime = 15L;
        ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
        batch.setMaxTimestamp(LOG_APPEND_TIME, logAppendTime);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetCreateTimeNotAllowedV0() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V0, 0L, GZIP, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        long createTime = 15L;
        ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
        batch.setMaxTimestamp(CREATE_TIME, createTime);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetPartitionLeaderEpochNotAllowedV0() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V0, 0L, GZIP, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
        batch.setPartitionLeaderEpoch(15);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSetPartitionLeaderEpochNotAllowedV1() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V1, 0L, GZIP, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
        batch.setPartitionLeaderEpoch(15);
    }

    @Test
    public void testSetLogAppendTimeV1() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V1, 0L, GZIP, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        long logAppendTime = 15L;
        ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
        batch.setMaxTimestamp(LOG_APPEND_TIME, logAppendTime);
        Assert.assertEquals(LOG_APPEND_TIME, batch.timestampType());
        Assert.assertEquals(logAppendTime, batch.maxTimestamp());
        Assert.assertTrue(batch.isValid());
        List<MutableRecordBatch> recordBatches = Utils.toList(records.batches().iterator());
        Assert.assertEquals(1, recordBatches.size());
        Assert.assertEquals(LOG_APPEND_TIME, recordBatches.get(0).timestampType());
        Assert.assertEquals(logAppendTime, recordBatches.get(0).maxTimestamp());
        for (Record record : records.records())
            Assert.assertEquals(logAppendTime, record.timestamp());

    }

    @Test
    public void testSetCreateTimeV1() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V1, 0L, GZIP, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        long createTime = 15L;
        ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
        batch.setMaxTimestamp(CREATE_TIME, createTime);
        Assert.assertEquals(CREATE_TIME, batch.timestampType());
        Assert.assertEquals(createTime, batch.maxTimestamp());
        Assert.assertTrue(batch.isValid());
        List<MutableRecordBatch> recordBatches = Utils.toList(records.batches().iterator());
        Assert.assertEquals(1, recordBatches.size());
        Assert.assertEquals(CREATE_TIME, recordBatches.get(0).timestampType());
        Assert.assertEquals(createTime, recordBatches.get(0).maxTimestamp());
        long expectedTimestamp = 1L;
        for (Record record : records.records())
            Assert.assertEquals((expectedTimestamp++), record.timestamp());

    }

    @Test
    public void testZStdCompressionTypeWithV0OrV1() {
        SimpleRecord[] simpleRecords = new SimpleRecord[]{ new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()) };
        // Check V0
        try {
            MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V0, 0L, ZSTD, CREATE_TIME, simpleRecords);
            ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
            batch.setLastOffset(1L);
            batch.iterator();
            Assert.fail("Can't reach here");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("ZStandard compression is not supported for magic 0", e.getMessage());
        }
        // Check V1
        try {
            MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V1, 0L, ZSTD, CREATE_TIME, simpleRecords);
            ByteBufferLegacyRecordBatch batch = new ByteBufferLegacyRecordBatch(records.buffer());
            batch.setLastOffset(1L);
            batch.iterator();
            Assert.fail("Can't reach here");
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("ZStandard compression is not supported for magic 1", e.getMessage());
        }
    }
}

