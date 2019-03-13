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
import CompressionType.NONE;
import DefaultRecordBatch.LAST_OFFSET_DELTA_OFFSET;
import DefaultRecordBatch.LENGTH_OFFSET;
import RecordBatch.CURRENT_MAGIC_VALUE;
import RecordBatch.MAGIC_VALUE_V2;
import RecordBatch.NO_PRODUCER_EPOCH;
import RecordBatch.NO_PRODUCER_ID;
import RecordBatch.NO_SEQUENCE;
import RecordBatch.NO_TIMESTAMP;
import TimestampType.CREATE_TIME;
import TimestampType.LOG_APPEND_TIME;
import TimestampType.NO_TIMESTAMP_TYPE;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.utils.CloseableIterator;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;

import static CompressionType.NONE;
import static ControlRecordType.COMMIT;
import static RecordBatch.CURRENT_MAGIC_VALUE;
import static RecordBatch.NO_PARTITION_LEADER_EPOCH;
import static RecordBatch.NO_SEQUENCE;
import static RecordBatch.NO_TIMESTAMP;
import static TimestampType.CREATE_TIME;


public class DefaultRecordBatchTest {
    @Test
    public void testWriteEmptyHeader() {
        long producerId = 23423L;
        short producerEpoch = 145;
        int baseSequence = 983;
        long baseOffset = 15L;
        long lastOffset = 37;
        int partitionLeaderEpoch = 15;
        long timestamp = System.currentTimeMillis();
        for (TimestampType timestampType : Arrays.asList(CREATE_TIME, LOG_APPEND_TIME)) {
            for (boolean isTransactional : Arrays.asList(true, false)) {
                for (boolean isControlBatch : Arrays.asList(true, false)) {
                    ByteBuffer buffer = ByteBuffer.allocate(2048);
                    DefaultRecordBatch.writeEmptyHeader(buffer, CURRENT_MAGIC_VALUE, producerId, producerEpoch, baseSequence, baseOffset, lastOffset, partitionLeaderEpoch, timestampType, timestamp, isTransactional, isControlBatch);
                    buffer.flip();
                    DefaultRecordBatch batch = new DefaultRecordBatch(buffer);
                    Assert.assertEquals(producerId, batch.producerId());
                    Assert.assertEquals(producerEpoch, batch.producerEpoch());
                    Assert.assertEquals(baseSequence, batch.baseSequence());
                    Assert.assertEquals((baseSequence + ((int) (lastOffset - baseOffset))), batch.lastSequence());
                    Assert.assertEquals(baseOffset, batch.baseOffset());
                    Assert.assertEquals(lastOffset, batch.lastOffset());
                    Assert.assertEquals(partitionLeaderEpoch, batch.partitionLeaderEpoch());
                    Assert.assertEquals(isTransactional, batch.isTransactional());
                    Assert.assertEquals(timestampType, batch.timestampType());
                    Assert.assertEquals(timestamp, batch.maxTimestamp());
                    Assert.assertEquals(NO_TIMESTAMP, batch.firstTimestamp());
                    Assert.assertEquals(isControlBatch, batch.isControlBatch());
                }
            }
        }
    }

    @Test
    public void buildDefaultRecordBatch() {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V2, NONE, CREATE_TIME, 1234567L);
        builder.appendWithOffset(1234567, 1L, "a".getBytes(), "v".getBytes());
        builder.appendWithOffset(1234568, 2L, "b".getBytes(), "v".getBytes());
        MemoryRecords records = builder.build();
        for (MutableRecordBatch batch : records.batches()) {
            Assert.assertTrue(batch.isValid());
            Assert.assertEquals(1234567, batch.baseOffset());
            Assert.assertEquals(1234568, batch.lastOffset());
            Assert.assertEquals(2L, batch.maxTimestamp());
            Assert.assertEquals(NO_PRODUCER_ID, batch.producerId());
            Assert.assertEquals(NO_PRODUCER_EPOCH, batch.producerEpoch());
            Assert.assertEquals(NO_SEQUENCE, batch.baseSequence());
            Assert.assertEquals(NO_SEQUENCE, batch.lastSequence());
            for (Record record : batch) {
                Assert.assertTrue(record.isValid());
            }
        }
    }

    @Test
    public void buildDefaultRecordBatchWithProducerId() {
        long pid = 23423L;
        short epoch = 145;
        int baseSequence = 983;
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V2, NONE, CREATE_TIME, 1234567L, NO_TIMESTAMP, pid, epoch, baseSequence);
        builder.appendWithOffset(1234567, 1L, "a".getBytes(), "v".getBytes());
        builder.appendWithOffset(1234568, 2L, "b".getBytes(), "v".getBytes());
        MemoryRecords records = builder.build();
        for (MutableRecordBatch batch : records.batches()) {
            Assert.assertTrue(batch.isValid());
            Assert.assertEquals(1234567, batch.baseOffset());
            Assert.assertEquals(1234568, batch.lastOffset());
            Assert.assertEquals(2L, batch.maxTimestamp());
            Assert.assertEquals(pid, batch.producerId());
            Assert.assertEquals(epoch, batch.producerEpoch());
            Assert.assertEquals(baseSequence, batch.baseSequence());
            Assert.assertEquals((baseSequence + 1), batch.lastSequence());
            for (Record record : batch) {
                Assert.assertTrue(record.isValid());
            }
        }
    }

    @Test
    public void buildDefaultRecordBatchWithSequenceWrapAround() {
        long pid = 23423L;
        short epoch = 145;
        int baseSequence = (Integer.MAX_VALUE) - 1;
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V2, NONE, CREATE_TIME, 1234567L, NO_TIMESTAMP, pid, epoch, baseSequence);
        builder.appendWithOffset(1234567, 1L, "a".getBytes(), "v".getBytes());
        builder.appendWithOffset(1234568, 2L, "b".getBytes(), "v".getBytes());
        builder.appendWithOffset(1234569, 3L, "c".getBytes(), "v".getBytes());
        MemoryRecords records = builder.build();
        List<MutableRecordBatch> batches = TestUtils.toList(records.batches());
        Assert.assertEquals(1, batches.size());
        RecordBatch batch = batches.get(0);
        Assert.assertEquals(pid, batch.producerId());
        Assert.assertEquals(epoch, batch.producerEpoch());
        Assert.assertEquals(baseSequence, batch.baseSequence());
        Assert.assertEquals(0, batch.lastSequence());
        List<Record> allRecords = TestUtils.toList(batch);
        Assert.assertEquals(3, allRecords.size());
        Assert.assertEquals(((Integer.MAX_VALUE) - 1), allRecords.get(0).sequence());
        Assert.assertEquals(Integer.MAX_VALUE, allRecords.get(1).sequence());
        Assert.assertEquals(0, allRecords.get(2).sequence());
    }

    @Test
    public void testSizeInBytes() {
        Header[] headers = new Header[]{ new RecordHeader("foo", "value".getBytes()), new RecordHeader("bar", ((byte[]) (null))) };
        long timestamp = System.currentTimeMillis();
        SimpleRecord[] records = new SimpleRecord[]{ new SimpleRecord(timestamp, "key".getBytes(), "value".getBytes()), new SimpleRecord((timestamp + 30000), null, "value".getBytes()), new SimpleRecord((timestamp + 60000), "key".getBytes(), null), new SimpleRecord((timestamp + 60000), "key".getBytes(), "value".getBytes(), headers) };
        int actualSize = MemoryRecords.withRecords(NONE, records).sizeInBytes();
        Assert.assertEquals(actualSize, DefaultRecordBatch.sizeInBytes(Arrays.asList(records)));
    }

    @Test(expected = InvalidRecordException.class)
    public void testInvalidRecordSize() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V2, 0L, NONE, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        ByteBuffer buffer = records.buffer();
        buffer.putInt(LENGTH_OFFSET, 10);
        DefaultRecordBatch batch = new DefaultRecordBatch(buffer);
        Assert.assertFalse(batch.isValid());
        batch.ensureValid();
    }

    @Test(expected = InvalidRecordException.class)
    public void testInvalidRecordCountTooManyNonCompressedV2() {
        long now = System.currentTimeMillis();
        DefaultRecordBatch batch = DefaultRecordBatchTest.recordsWithInvalidRecordCount(MAGIC_VALUE_V2, now, NONE, 5);
        // force iteration through the batch to execute validation
        // batch validation is a part of normal workflow for LogValidator.validateMessagesAndAssignOffsets
        for (Record record : batch) {
            record.isValid();
        }
    }

    @Test(expected = InvalidRecordException.class)
    public void testInvalidRecordCountTooLittleNonCompressedV2() {
        long now = System.currentTimeMillis();
        DefaultRecordBatch batch = DefaultRecordBatchTest.recordsWithInvalidRecordCount(MAGIC_VALUE_V2, now, NONE, 2);
        // force iteration through the batch to execute validation
        // batch validation is a part of normal workflow for LogValidator.validateMessagesAndAssignOffsets
        for (Record record : batch) {
            record.isValid();
        }
    }

    @Test(expected = InvalidRecordException.class)
    public void testInvalidRecordCountTooManyCompressedV2() {
        long now = System.currentTimeMillis();
        DefaultRecordBatch batch = DefaultRecordBatchTest.recordsWithInvalidRecordCount(MAGIC_VALUE_V2, now, GZIP, 5);
        // force iteration through the batch to execute validation
        // batch validation is a part of normal workflow for LogValidator.validateMessagesAndAssignOffsets
        for (Record record : batch) {
            record.isValid();
        }
    }

    @Test(expected = InvalidRecordException.class)
    public void testInvalidRecordCountTooLittleCompressedV2() {
        long now = System.currentTimeMillis();
        DefaultRecordBatch batch = DefaultRecordBatchTest.recordsWithInvalidRecordCount(MAGIC_VALUE_V2, now, GZIP, 2);
        // force iteration through the batch to execute validation
        // batch validation is a part of normal workflow for LogValidator.validateMessagesAndAssignOffsets
        for (Record record : batch) {
            record.isValid();
        }
    }

    @Test(expected = InvalidRecordException.class)
    public void testInvalidCrc() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V2, 0L, NONE, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        ByteBuffer buffer = records.buffer();
        buffer.putInt(LAST_OFFSET_DELTA_OFFSET, 23);
        DefaultRecordBatch batch = new DefaultRecordBatch(buffer);
        Assert.assertFalse(batch.isValid());
        batch.ensureValid();
    }

    @Test
    public void testSetLastOffset() {
        SimpleRecord[] simpleRecords = new SimpleRecord[]{ new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()) };
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V2, 0L, NONE, CREATE_TIME, simpleRecords);
        long lastOffset = 500L;
        long firstOffset = (lastOffset - (simpleRecords.length)) + 1;
        DefaultRecordBatch batch = new DefaultRecordBatch(records.buffer());
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

    @Test
    public void testSetPartitionLeaderEpoch() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V2, 0L, NONE, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        int leaderEpoch = 500;
        DefaultRecordBatch batch = new DefaultRecordBatch(records.buffer());
        batch.setPartitionLeaderEpoch(leaderEpoch);
        Assert.assertEquals(leaderEpoch, batch.partitionLeaderEpoch());
        Assert.assertTrue(batch.isValid());
        List<MutableRecordBatch> recordBatches = Utils.toList(records.batches().iterator());
        Assert.assertEquals(1, recordBatches.size());
        Assert.assertEquals(leaderEpoch, recordBatches.get(0).partitionLeaderEpoch());
    }

    @Test
    public void testSetLogAppendTime() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V2, 0L, NONE, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        long logAppendTime = 15L;
        DefaultRecordBatch batch = new DefaultRecordBatch(records.buffer());
        batch.setMaxTimestamp(LOG_APPEND_TIME, logAppendTime);
        Assert.assertEquals(LOG_APPEND_TIME, batch.timestampType());
        Assert.assertEquals(logAppendTime, batch.maxTimestamp());
        Assert.assertTrue(batch.isValid());
        List<MutableRecordBatch> recordBatches = Utils.toList(records.batches().iterator());
        Assert.assertEquals(1, recordBatches.size());
        Assert.assertEquals(logAppendTime, recordBatches.get(0).maxTimestamp());
        Assert.assertEquals(LOG_APPEND_TIME, recordBatches.get(0).timestampType());
        for (Record record : records.records())
            Assert.assertEquals(logAppendTime, record.timestamp());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNoTimestampTypeNotAllowed() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V2, 0L, NONE, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        DefaultRecordBatch batch = new DefaultRecordBatch(records.buffer());
        batch.setMaxTimestamp(NO_TIMESTAMP_TYPE, NO_TIMESTAMP);
    }

    @Test
    public void testReadAndWriteControlBatch() {
        long producerId = 1L;
        short producerEpoch = 0;
        int coordinatorEpoch = 15;
        ByteBuffer buffer = ByteBuffer.allocate(128);
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, CURRENT_MAGIC_VALUE, NONE, CREATE_TIME, 0L, NO_TIMESTAMP, producerId, producerEpoch, NO_SEQUENCE, true, true, NO_PARTITION_LEADER_EPOCH, buffer.remaining());
        EndTransactionMarker marker = new EndTransactionMarker(COMMIT, coordinatorEpoch);
        builder.appendEndTxnMarker(System.currentTimeMillis(), marker);
        MemoryRecords records = builder.build();
        List<MutableRecordBatch> batches = TestUtils.toList(records.batches());
        Assert.assertEquals(1, batches.size());
        MutableRecordBatch batch = batches.get(0);
        Assert.assertTrue(batch.isControlBatch());
        List<Record> logRecords = TestUtils.toList(records.records());
        Assert.assertEquals(1, logRecords.size());
        Record commitRecord = logRecords.get(0);
        Assert.assertEquals(marker, EndTransactionMarker.deserialize(commitRecord));
    }

    @Test
    public void testStreamingIteratorConsistency() {
        MemoryRecords records = MemoryRecords.withRecords(MAGIC_VALUE_V2, 0L, GZIP, CREATE_TIME, new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()));
        DefaultRecordBatch batch = new DefaultRecordBatch(records.buffer());
        try (CloseableIterator<Record> streamingIterator = batch.streamingIterator(BufferSupplier.create())) {
            TestUtils.checkEquals(streamingIterator, batch.iterator());
        }
    }

    @Test
    public void testIncrementSequence() {
        Assert.assertEquals(10, DefaultRecordBatch.incrementSequence(5, 5));
        Assert.assertEquals(0, DefaultRecordBatch.incrementSequence(Integer.MAX_VALUE, 1));
        Assert.assertEquals(4, DefaultRecordBatch.incrementSequence(((Integer.MAX_VALUE) - 5), 10));
    }
}

