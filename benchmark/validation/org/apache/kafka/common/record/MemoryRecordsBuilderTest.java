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


import MemoryRecordsBuilder.RecordsInfo;
import Record.EMPTY_HEADERS;
import RecordBatch.MAGIC_VALUE_V0;
import RecordBatch.MAGIC_VALUE_V1;
import RecordBatch.MAGIC_VALUE_V2;
import RecordBatch.NO_TIMESTAMP;
import TimestampType.CREATE_TIME;
import TimestampType.LOG_APPEND_TIME;
import TimestampType.NO_TIMESTAMP_TYPE;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import org.apache.kafka.common.errors.UnsupportedCompressionTypeException;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static CompressionType.NONE;
import static CompressionType.ZSTD;
import static ControlRecordType.ABORT;
import static ControlRecordType.COMMIT;
import static LegacyRecord.NO_TIMESTAMP;
import static LegacyRecord.RECORD_OVERHEAD_V0;
import static LegacyRecord.RECORD_OVERHEAD_V1;
import static RecordBatch.CURRENT_MAGIC_VALUE;
import static RecordBatch.MAGIC_VALUE_V0;
import static RecordBatch.MAGIC_VALUE_V1;
import static RecordBatch.MAGIC_VALUE_V2;
import static RecordBatch.NO_PARTITION_LEADER_EPOCH;
import static RecordBatch.NO_PRODUCER_EPOCH;
import static RecordBatch.NO_PRODUCER_ID;
import static RecordBatch.NO_SEQUENCE;
import static Records.LOG_OVERHEAD;
import static TimestampType.CREATE_TIME;
import static TimestampType.LOG_APPEND_TIME;


@RunWith(Parameterized.class)
public class MemoryRecordsBuilderTest {
    private final CompressionType compressionType;

    private final int bufferOffset;

    private final Time time;

    public MemoryRecordsBuilderTest(int bufferOffset, CompressionType compressionType) {
        this.bufferOffset = bufferOffset;
        this.compressionType = compressionType;
        this.time = Time.SYSTEM;
    }

    @Test
    public void testWriteEmptyRecordSet() {
        byte magic = MAGIC_VALUE_V0;
        assumeAtLeastV2OrNotZstd(magic);
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        Supplier<MemoryRecordsBuilder> builderSupplier = () -> new MemoryRecordsBuilder(buffer, magic, compressionType, CREATE_TIME, 0L, 0L, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        if ((compressionType) != (ZSTD)) {
            MemoryRecords records = builderSupplier.get().build();
            Assert.assertEquals(0, records.sizeInBytes());
            Assert.assertEquals(bufferOffset, buffer.position());
        } else {
            Exception e = Assert.assertThrows(IllegalArgumentException.class, () -> builderSupplier.get().build());
            Assert.assertEquals(e.getMessage(), ("ZStandard compression is not supported for magic " + magic));
        }
    }

    @Test
    public void testWriteTransactionalRecordSet() {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        long pid = 9809;
        short epoch = 15;
        int sequence = 2342;
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, CURRENT_MAGIC_VALUE, compressionType, CREATE_TIME, 0L, 0L, pid, epoch, sequence, true, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.append(System.currentTimeMillis(), "foo".getBytes(), "bar".getBytes());
        MemoryRecords records = builder.build();
        List<MutableRecordBatch> batches = Utils.toList(records.batches().iterator());
        Assert.assertEquals(1, batches.size());
        Assert.assertTrue(batches.get(0).isTransactional());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteTransactionalNotAllowedMagicV0() {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        long pid = 9809;
        short epoch = 15;
        int sequence = 2342;
        new MemoryRecordsBuilder(buffer, MAGIC_VALUE_V0, compressionType, CREATE_TIME, 0L, 0L, pid, epoch, sequence, true, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteTransactionalNotAllowedMagicV1() {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        long pid = 9809;
        short epoch = 15;
        int sequence = 2342;
        new MemoryRecordsBuilder(buffer, MAGIC_VALUE_V1, compressionType, CREATE_TIME, 0L, 0L, pid, epoch, sequence, true, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteControlBatchNotAllowedMagicV0() {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        long pid = 9809;
        short epoch = 15;
        int sequence = 2342;
        new MemoryRecordsBuilder(buffer, MAGIC_VALUE_V0, compressionType, CREATE_TIME, 0L, 0L, pid, epoch, sequence, false, true, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteControlBatchNotAllowedMagicV1() {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        long pid = 9809;
        short epoch = 15;
        int sequence = 2342;
        new MemoryRecordsBuilder(buffer, MAGIC_VALUE_V1, compressionType, CREATE_TIME, 0L, 0L, pid, epoch, sequence, false, true, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteTransactionalWithInvalidPID() {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        long pid = NO_PRODUCER_ID;
        short epoch = 15;
        int sequence = 2342;
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, CURRENT_MAGIC_VALUE, compressionType, CREATE_TIME, 0L, 0L, pid, epoch, sequence, true, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteIdempotentWithInvalidEpoch() {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        long pid = 9809;
        short epoch = NO_PRODUCER_EPOCH;
        int sequence = 2342;
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, CURRENT_MAGIC_VALUE, compressionType, CREATE_TIME, 0L, 0L, pid, epoch, sequence, true, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteIdempotentWithInvalidBaseSequence() {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        long pid = 9809;
        short epoch = 15;
        int sequence = NO_SEQUENCE;
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, CURRENT_MAGIC_VALUE, compressionType, CREATE_TIME, 0L, 0L, pid, epoch, sequence, true, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteEndTxnMarkerNonTransactionalBatch() {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        long pid = 9809;
        short epoch = 15;
        int sequence = NO_SEQUENCE;
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, CURRENT_MAGIC_VALUE, compressionType, CREATE_TIME, 0L, 0L, pid, epoch, sequence, false, true, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.appendEndTxnMarker(NO_TIMESTAMP, new EndTransactionMarker(ABORT, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWriteEndTxnMarkerNonControlBatch() {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        long pid = 9809;
        short epoch = 15;
        int sequence = NO_SEQUENCE;
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, CURRENT_MAGIC_VALUE, compressionType, CREATE_TIME, 0L, 0L, pid, epoch, sequence, true, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.appendEndTxnMarker(NO_TIMESTAMP, new EndTransactionMarker(ABORT, 0));
    }

    @Test
    public void testCompressionRateV0() {
        byte magic = MAGIC_VALUE_V0;
        assumeAtLeastV2OrNotZstd(magic);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.position(bufferOffset);
        LegacyRecord[] records = new LegacyRecord[]{ LegacyRecord.create(magic, 0L, "a".getBytes(), "1".getBytes()), LegacyRecord.create(magic, 1L, "b".getBytes(), "2".getBytes()), LegacyRecord.create(magic, 2L, "c".getBytes(), "3".getBytes()) };
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, magic, compressionType, CREATE_TIME, 0L, 0L, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        int uncompressedSize = 0;
        for (LegacyRecord record : records) {
            uncompressedSize += (record.sizeInBytes()) + (LOG_OVERHEAD);
            builder.append(record);
        }
        MemoryRecords built = builder.build();
        if ((compressionType) == (NONE)) {
            Assert.assertEquals(1.0, builder.compressionRatio(), 1.0E-5);
        } else {
            int compressedSize = ((built.sizeInBytes()) - (LOG_OVERHEAD)) - (RECORD_OVERHEAD_V0);
            double computedCompressionRate = ((double) (compressedSize)) / uncompressedSize;
            Assert.assertEquals(computedCompressionRate, builder.compressionRatio(), 1.0E-5);
        }
    }

    @Test
    public void testEstimatedSizeInBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.position(bufferOffset);
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, CURRENT_MAGIC_VALUE, compressionType, CREATE_TIME, 0L, 0L, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        int previousEstimate = 0;
        for (int i = 0; i < 10; i++) {
            builder.append(new SimpleRecord(i, ("" + i).getBytes()));
            int currentEstimate = builder.estimatedSizeInBytes();
            Assert.assertTrue((currentEstimate > previousEstimate));
            previousEstimate = currentEstimate;
        }
        int bytesWrittenBeforeClose = builder.estimatedSizeInBytes();
        MemoryRecords records = builder.build();
        Assert.assertEquals(records.sizeInBytes(), builder.estimatedSizeInBytes());
        if ((compressionType) == (NONE))
            Assert.assertEquals(records.sizeInBytes(), bytesWrittenBeforeClose);

    }

    @Test
    public void testCompressionRateV1() {
        byte magic = MAGIC_VALUE_V1;
        assumeAtLeastV2OrNotZstd(magic);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.position(bufferOffset);
        LegacyRecord[] records = new LegacyRecord[]{ LegacyRecord.create(magic, 0L, "a".getBytes(), "1".getBytes()), LegacyRecord.create(magic, 1L, "b".getBytes(), "2".getBytes()), LegacyRecord.create(magic, 2L, "c".getBytes(), "3".getBytes()) };
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, magic, compressionType, CREATE_TIME, 0L, 0L, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        int uncompressedSize = 0;
        for (LegacyRecord record : records) {
            uncompressedSize += (record.sizeInBytes()) + (LOG_OVERHEAD);
            builder.append(record);
        }
        MemoryRecords built = builder.build();
        if ((compressionType) == (NONE)) {
            Assert.assertEquals(1.0, builder.compressionRatio(), 1.0E-5);
        } else {
            int compressedSize = ((built.sizeInBytes()) - (LOG_OVERHEAD)) - (RECORD_OVERHEAD_V1);
            double computedCompressionRate = ((double) (compressedSize)) / uncompressedSize;
            Assert.assertEquals(computedCompressionRate, builder.compressionRatio(), 1.0E-5);
        }
    }

    @Test
    public void buildUsingLogAppendTime() {
        byte magic = MAGIC_VALUE_V1;
        assumeAtLeastV2OrNotZstd(magic);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.position(bufferOffset);
        long logAppendTime = System.currentTimeMillis();
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, magic, compressionType, LOG_APPEND_TIME, 0L, logAppendTime, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.append(0L, "a".getBytes(), "1".getBytes());
        builder.append(0L, "b".getBytes(), "2".getBytes());
        builder.append(0L, "c".getBytes(), "3".getBytes());
        MemoryRecords records = builder.build();
        MemoryRecordsBuilder.RecordsInfo info = builder.info();
        Assert.assertEquals(logAppendTime, info.maxTimestamp);
        if ((compressionType) != (NONE))
            Assert.assertEquals(2L, info.shallowOffsetOfMaxTimestamp);
        else
            Assert.assertEquals(0L, info.shallowOffsetOfMaxTimestamp);

        for (RecordBatch batch : records.batches()) {
            Assert.assertEquals(LOG_APPEND_TIME, batch.timestampType());
            for (Record record : batch)
                Assert.assertEquals(logAppendTime, record.timestamp());

        }
    }

    @Test
    public void buildUsingCreateTime() {
        byte magic = MAGIC_VALUE_V1;
        assumeAtLeastV2OrNotZstd(magic);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.position(bufferOffset);
        long logAppendTime = System.currentTimeMillis();
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, magic, compressionType, CREATE_TIME, 0L, logAppendTime, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.append(0L, "a".getBytes(), "1".getBytes());
        builder.append(2L, "b".getBytes(), "2".getBytes());
        builder.append(1L, "c".getBytes(), "3".getBytes());
        MemoryRecords records = builder.build();
        MemoryRecordsBuilder.RecordsInfo info = builder.info();
        Assert.assertEquals(2L, info.maxTimestamp);
        if ((compressionType) == (NONE))
            Assert.assertEquals(1L, info.shallowOffsetOfMaxTimestamp);
        else
            Assert.assertEquals(2L, info.shallowOffsetOfMaxTimestamp);

        int i = 0;
        long[] expectedTimestamps = new long[]{ 0L, 2L, 1L };
        for (RecordBatch batch : records.batches()) {
            Assert.assertEquals(CREATE_TIME, batch.timestampType());
            for (Record record : batch)
                Assert.assertEquals(expectedTimestamps[(i++)], record.timestamp());

        }
    }

    @Test
    public void testAppendedChecksumConsistency() {
        assumeAtLeastV2OrNotZstd(MAGIC_VALUE_V0);
        assumeAtLeastV2OrNotZstd(MAGIC_VALUE_V1);
        ByteBuffer buffer = ByteBuffer.allocate(512);
        for (byte magic : Arrays.asList(MAGIC_VALUE_V0, MAGIC_VALUE_V1, MAGIC_VALUE_V2)) {
            MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, magic, compressionType, CREATE_TIME, 0L, NO_TIMESTAMP, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
            Long checksumOrNull = builder.append(1L, "key".getBytes(), "value".getBytes());
            MemoryRecords memoryRecords = builder.build();
            List<Record> records = TestUtils.toList(memoryRecords.records());
            Assert.assertEquals(1, records.size());
            Assert.assertEquals(checksumOrNull, records.get(0).checksumOrNull());
        }
    }

    @Test
    public void testSmallWriteLimit() {
        // with a small write limit, we always allow at least one record to be added
        byte[] key = "foo".getBytes();
        byte[] value = "bar".getBytes();
        int writeLimit = 0;
        ByteBuffer buffer = ByteBuffer.allocate(512);
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, CURRENT_MAGIC_VALUE, compressionType, CREATE_TIME, 0L, NO_TIMESTAMP, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, writeLimit);
        Assert.assertFalse(builder.isFull());
        Assert.assertTrue(builder.hasRoomFor(0L, key, value, EMPTY_HEADERS));
        builder.append(0L, key, value);
        Assert.assertTrue(builder.isFull());
        Assert.assertFalse(builder.hasRoomFor(0L, key, value, EMPTY_HEADERS));
        MemoryRecords memRecords = builder.build();
        List<Record> records = TestUtils.toList(memRecords.records());
        Assert.assertEquals(1, records.size());
        Record record = records.get(0);
        Assert.assertEquals(ByteBuffer.wrap(key), record.key());
        Assert.assertEquals(ByteBuffer.wrap(value), record.value());
    }

    @Test
    public void writePastLimit() {
        byte magic = MAGIC_VALUE_V1;
        assumeAtLeastV2OrNotZstd(magic);
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.position(bufferOffset);
        long logAppendTime = System.currentTimeMillis();
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, magic, compressionType, CREATE_TIME, 0L, logAppendTime, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.setEstimatedCompressionRatio(0.5F);
        builder.append(0L, "a".getBytes(), "1".getBytes());
        builder.append(1L, "b".getBytes(), "2".getBytes());
        Assert.assertFalse(builder.hasRoomFor(2L, "c".getBytes(), "3".getBytes(), EMPTY_HEADERS));
        builder.append(2L, "c".getBytes(), "3".getBytes());
        MemoryRecords records = builder.build();
        MemoryRecordsBuilder.RecordsInfo info = builder.info();
        Assert.assertEquals(2L, info.maxTimestamp);
        Assert.assertEquals(2L, info.shallowOffsetOfMaxTimestamp);
        long i = 0L;
        for (RecordBatch batch : records.batches()) {
            Assert.assertEquals(CREATE_TIME, batch.timestampType());
            for (Record record : batch)
                Assert.assertEquals((i++), record.timestamp());

        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAppendAtInvalidOffset() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.position(bufferOffset);
        long logAppendTime = System.currentTimeMillis();
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, MAGIC_VALUE_V1, compressionType, CREATE_TIME, 0L, logAppendTime, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.appendWithOffset(0L, System.currentTimeMillis(), "a".getBytes(), null);
        // offsets must increase monotonically
        builder.appendWithOffset(0L, System.currentTimeMillis(), "b".getBytes(), null);
    }

    @Test
    public void convertV2ToV1UsingMixedCreateAndLogAppendTime() {
        ByteBuffer buffer = ByteBuffer.allocate(512);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V2, compressionType, LOG_APPEND_TIME, 0L);
        builder.append(10L, "1".getBytes(), "a".getBytes());
        builder.close();
        int sizeExcludingTxnMarkers = buffer.position();
        MemoryRecords.writeEndTransactionalMarker(buffer, 1L, System.currentTimeMillis(), 0, 15L, ((short) (0)), new EndTransactionMarker(ABORT, 0));
        int position = buffer.position();
        builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V2, compressionType, CREATE_TIME, 1L);
        builder.append(12L, "2".getBytes(), "b".getBytes());
        builder.append(13L, "3".getBytes(), "c".getBytes());
        builder.close();
        sizeExcludingTxnMarkers += (buffer.position()) - position;
        MemoryRecords.writeEndTransactionalMarker(buffer, 14L, System.currentTimeMillis(), 0, 1L, ((short) (0)), new EndTransactionMarker(COMMIT, 0));
        buffer.flip();
        Supplier<ConvertedRecords<MemoryRecords>> convertedRecordsSupplier = () -> MemoryRecords.readableRecords(buffer).downConvert(MAGIC_VALUE_V1, 0, time);
        if ((compressionType) != (ZSTD)) {
            ConvertedRecords<MemoryRecords> convertedRecords = convertedRecordsSupplier.get();
            MemoryRecords records = convertedRecords.records();
            // Transactional markers are skipped when down converting to V1, so exclude them from size
            verifyRecordsProcessingStats(convertedRecords.recordConversionStats(), 3, 3, records.sizeInBytes(), sizeExcludingTxnMarkers);
            List<? extends RecordBatch> batches = Utils.toList(records.batches().iterator());
            if ((compressionType) != (NONE)) {
                Assert.assertEquals(2, batches.size());
                Assert.assertEquals(LOG_APPEND_TIME, timestampType());
                Assert.assertEquals(CREATE_TIME, timestampType());
            } else {
                Assert.assertEquals(3, batches.size());
                Assert.assertEquals(LOG_APPEND_TIME, timestampType());
                Assert.assertEquals(CREATE_TIME, timestampType());
                Assert.assertEquals(CREATE_TIME, timestampType());
            }
            List<Record> logRecords = Utils.toList(records.records().iterator());
            Assert.assertEquals(3, logRecords.size());
            Assert.assertEquals(ByteBuffer.wrap("1".getBytes()), logRecords.get(0).key());
            Assert.assertEquals(ByteBuffer.wrap("2".getBytes()), logRecords.get(1).key());
            Assert.assertEquals(ByteBuffer.wrap("3".getBytes()), logRecords.get(2).key());
        } else {
            Exception e = Assert.assertThrows(UnsupportedCompressionTypeException.class, convertedRecordsSupplier::get);
            Assert.assertEquals("Down-conversion of zstandard-compressed batches is not supported", e.getMessage());
        }
    }

    @Test
    public void convertToV1WithMixedV0AndV2Data() {
        assumeAtLeastV2OrNotZstd(MAGIC_VALUE_V0);
        assumeAtLeastV2OrNotZstd(MAGIC_VALUE_V1);
        ByteBuffer buffer = ByteBuffer.allocate(512);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V0, compressionType, NO_TIMESTAMP_TYPE, 0L);
        builder.append(NO_TIMESTAMP, "1".getBytes(), "a".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, MAGIC_VALUE_V2, compressionType, CREATE_TIME, 1L);
        builder.append(11L, "2".getBytes(), "b".getBytes());
        builder.append(12L, "3".getBytes(), "c".getBytes());
        builder.close();
        buffer.flip();
        ConvertedRecords<MemoryRecords> convertedRecords = MemoryRecords.readableRecords(buffer).downConvert(MAGIC_VALUE_V1, 0, time);
        MemoryRecords records = convertedRecords.records();
        verifyRecordsProcessingStats(convertedRecords.recordConversionStats(), 3, 2, records.sizeInBytes(), buffer.limit());
        List<? extends RecordBatch> batches = Utils.toList(records.batches().iterator());
        if ((compressionType) != (NONE)) {
            Assert.assertEquals(2, batches.size());
            Assert.assertEquals(MAGIC_VALUE_V0, magic());
            Assert.assertEquals(0, baseOffset());
            Assert.assertEquals(MAGIC_VALUE_V1, magic());
            Assert.assertEquals(1, baseOffset());
        } else {
            Assert.assertEquals(3, batches.size());
            Assert.assertEquals(MAGIC_VALUE_V0, magic());
            Assert.assertEquals(0, baseOffset());
            Assert.assertEquals(MAGIC_VALUE_V1, magic());
            Assert.assertEquals(1, baseOffset());
            Assert.assertEquals(MAGIC_VALUE_V1, magic());
            Assert.assertEquals(2, baseOffset());
        }
        List<Record> logRecords = Utils.toList(records.records().iterator());
        Assert.assertEquals("1", Utils.utf8(logRecords.get(0).key()));
        Assert.assertEquals("2", Utils.utf8(logRecords.get(1).key()));
        Assert.assertEquals("3", Utils.utf8(logRecords.get(2).key()));
        convertedRecords = MemoryRecords.readableRecords(buffer).downConvert(MAGIC_VALUE_V1, 2L, time);
        records = convertedRecords.records();
        batches = Utils.toList(records.batches().iterator());
        logRecords = Utils.toList(records.records().iterator());
        if ((compressionType) != (NONE)) {
            Assert.assertEquals(2, batches.size());
            Assert.assertEquals(MAGIC_VALUE_V0, magic());
            Assert.assertEquals(0, baseOffset());
            Assert.assertEquals(MAGIC_VALUE_V1, magic());
            Assert.assertEquals(1, baseOffset());
            Assert.assertEquals("1", Utils.utf8(logRecords.get(0).key()));
            Assert.assertEquals("2", Utils.utf8(logRecords.get(1).key()));
            Assert.assertEquals("3", Utils.utf8(logRecords.get(2).key()));
            verifyRecordsProcessingStats(convertedRecords.recordConversionStats(), 3, 2, records.sizeInBytes(), buffer.limit());
        } else {
            Assert.assertEquals(2, batches.size());
            Assert.assertEquals(MAGIC_VALUE_V0, magic());
            Assert.assertEquals(0, baseOffset());
            Assert.assertEquals(MAGIC_VALUE_V1, magic());
            Assert.assertEquals(2, baseOffset());
            Assert.assertEquals("1", Utils.utf8(logRecords.get(0).key()));
            Assert.assertEquals("3", Utils.utf8(logRecords.get(1).key()));
            verifyRecordsProcessingStats(convertedRecords.recordConversionStats(), 3, 1, records.sizeInBytes(), buffer.limit());
        }
    }

    @Test
    public void shouldThrowIllegalStateExceptionOnBuildWhenAborted() {
        byte magic = MAGIC_VALUE_V0;
        assumeAtLeastV2OrNotZstd(magic);
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, magic, compressionType, CREATE_TIME, 0L, 0L, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.abort();
        Assert.assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void shouldResetBufferToInitialPositionOnAbort() {
        byte magic = MAGIC_VALUE_V0;
        assumeAtLeastV2OrNotZstd(magic);
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, magic, compressionType, CREATE_TIME, 0L, 0L, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.append(0L, "a".getBytes(), "1".getBytes());
        builder.abort();
        Assert.assertEquals(bufferOffset, builder.buffer().position());
    }

    @Test
    public void shouldThrowIllegalStateExceptionOnCloseWhenAborted() {
        byte magic = MAGIC_VALUE_V0;
        assumeAtLeastV2OrNotZstd(magic);
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, magic, compressionType, CREATE_TIME, 0L, 0L, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.abort();
        try {
            builder.close();
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // ok
        }
    }

    @Test
    public void shouldThrowIllegalStateExceptionOnAppendWhenAborted() {
        byte magic = MAGIC_VALUE_V0;
        assumeAtLeastV2OrNotZstd(magic);
        ByteBuffer buffer = ByteBuffer.allocate(128);
        buffer.position(bufferOffset);
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, magic, compressionType, CREATE_TIME, 0L, 0L, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, buffer.capacity());
        builder.abort();
        try {
            builder.append(0L, "a".getBytes(), "1".getBytes());
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // ok
        }
    }

    @Test
    public void testBuffersDereferencedOnClose() {
        Runtime runtime = Runtime.getRuntime();
        int payloadLen = 1024 * 1024;
        ByteBuffer buffer = ByteBuffer.allocate((payloadLen * 2));
        byte[] key = new byte[0];
        byte[] value = new byte[payloadLen];
        new Random().nextBytes(value);// Use random payload so that compressed buffer is large

        List<MemoryRecordsBuilder> builders = new ArrayList<>(100);
        long startMem = 0;
        long memUsed = 0;
        int iterations = 0;
        while ((iterations++) < 100) {
            buffer.rewind();
            MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, MAGIC_VALUE_V2, compressionType, CREATE_TIME, 0L, 0L, NO_PRODUCER_ID, NO_PRODUCER_EPOCH, NO_SEQUENCE, false, false, NO_PARTITION_LEADER_EPOCH, 0);
            builder.append(1L, new byte[0], value);
            builder.build();
            builders.add(builder);
            System.gc();
            memUsed = ((runtime.totalMemory()) - (runtime.freeMemory())) - startMem;
            // Ignore memory usage during initialization
            if (iterations == 2)
                startMem = memUsed;
            else
                if ((iterations > 2) && (memUsed < ((iterations - 2) * 1024)))
                    break;


        } 
        Assert.assertTrue(("Memory usage too high: " + memUsed), (iterations < 100));
    }
}

