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


import BatchRetention.DELETE;
import BatchRetention.DELETE_EMPTY;
import BufferSupplier.NO_CACHING;
import ControlRecordType.COMMIT;
import DefaultRecordBatch.RECORD_BATCH_OVERHEAD;
import MemoryRecords.FilterResult;
import MemoryRecords.RecordFilter;
import Record.EMPTY_HEADERS;
import RecordBatch.MAGIC_VALUE_V2;
import RecordBatch.NO_PARTITION_LEADER_EPOCH;
import RecordBatch.NO_PRODUCER_EPOCH;
import RecordBatch.NO_PRODUCER_ID;
import RecordBatch.NO_SEQUENCE;
import RecordBatch.NO_TIMESTAMP;
import Records.HEADER_SIZE_UP_TO_MAGIC;
import Records.LOG_OVERHEAD;
import Records.MAGIC_OFFSET;
import TimestampType.CREATE_TIME;
import TimestampType.LOG_APPEND_TIME;
import TimestampType.NO_TIMESTAMP_TYPE;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.CorruptRecordException;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.record.MemoryRecords.RecordFilter.BatchRetention;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static CompressionType.LZ4;
import static CompressionType.NONE;
import static CompressionType.ZSTD;
import static ControlRecordType.COMMIT;
import static RecordBatch.MAGIC_VALUE_V0;
import static RecordBatch.MAGIC_VALUE_V1;
import static RecordBatch.MAGIC_VALUE_V2;
import static RecordBatch.NO_PRODUCER_EPOCH;
import static RecordBatch.NO_PRODUCER_ID;
import static RecordBatch.NO_SEQUENCE;
import static RecordBatch.NO_TIMESTAMP;
import static Records.SIZE_OFFSET;
import static TimestampType.CREATE_TIME;


@RunWith(Parameterized.class)
public class MemoryRecordsTest {
    private CompressionType compression;

    private byte magic;

    private long firstOffset;

    private long pid;

    private short epoch;

    private int firstSequence;

    private long logAppendTime = System.currentTimeMillis();

    private int partitionLeaderEpoch = 998;

    public MemoryRecordsTest(byte magic, long firstOffset, CompressionType compression) {
        this.magic = magic;
        this.compression = compression;
        this.firstOffset = firstOffset;
        if (magic >= (MAGIC_VALUE_V2)) {
            pid = 134234L;
            epoch = 28;
            firstSequence = 777;
        } else {
            pid = NO_PRODUCER_ID;
            epoch = NO_PRODUCER_EPOCH;
            firstSequence = NO_SEQUENCE;
        }
    }

    @Test
    public void testIterator() {
        assumeAtLeastV2OrNotZstd();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MemoryRecordsBuilder builder = new MemoryRecordsBuilder(buffer, magic, compression, CREATE_TIME, firstOffset, logAppendTime, pid, epoch, firstSequence, false, false, partitionLeaderEpoch, buffer.limit());
        SimpleRecord[] records = new SimpleRecord[]{ new SimpleRecord(1L, "a".getBytes(), "1".getBytes()), new SimpleRecord(2L, "b".getBytes(), "2".getBytes()), new SimpleRecord(3L, "c".getBytes(), "3".getBytes()), new SimpleRecord(4L, null, "4".getBytes()), new SimpleRecord(5L, "d".getBytes(), null), new SimpleRecord(6L, ((byte[]) (null)), null) };
        for (SimpleRecord record : records)
            builder.append(record);

        MemoryRecords memoryRecords = builder.build();
        for (int iteration = 0; iteration < 2; iteration++) {
            int total = 0;
            for (RecordBatch batch : memoryRecords.batches()) {
                Assert.assertTrue(batch.isValid());
                Assert.assertEquals(compression, batch.compressionType());
                Assert.assertEquals(((firstOffset) + total), batch.baseOffset());
                if ((magic) >= (MAGIC_VALUE_V2)) {
                    Assert.assertEquals(pid, batch.producerId());
                    Assert.assertEquals(epoch, batch.producerEpoch());
                    Assert.assertEquals(((firstSequence) + total), batch.baseSequence());
                    Assert.assertEquals(partitionLeaderEpoch, batch.partitionLeaderEpoch());
                    Assert.assertEquals(records.length, batch.countOrNull().intValue());
                    Assert.assertEquals(CREATE_TIME, batch.timestampType());
                    Assert.assertEquals(records[((records.length) - 1)].timestamp(), batch.maxTimestamp());
                } else {
                    Assert.assertEquals(NO_PRODUCER_ID, batch.producerId());
                    Assert.assertEquals(NO_PRODUCER_EPOCH, batch.producerEpoch());
                    Assert.assertEquals(NO_SEQUENCE, batch.baseSequence());
                    Assert.assertEquals(NO_PARTITION_LEADER_EPOCH, batch.partitionLeaderEpoch());
                    Assert.assertNull(batch.countOrNull());
                    if ((magic) == (MAGIC_VALUE_V0))
                        Assert.assertEquals(NO_TIMESTAMP_TYPE, batch.timestampType());
                    else
                        Assert.assertEquals(CREATE_TIME, batch.timestampType());

                }
                int recordCount = 0;
                for (Record record : batch) {
                    Assert.assertTrue(record.isValid());
                    Assert.assertTrue(record.hasMagic(batch.magic()));
                    Assert.assertFalse(record.isCompressed());
                    Assert.assertEquals(((firstOffset) + total), record.offset());
                    Assert.assertEquals(records[total].key(), record.key());
                    Assert.assertEquals(records[total].value(), record.value());
                    if ((magic) >= (MAGIC_VALUE_V2))
                        Assert.assertEquals(((firstSequence) + total), record.sequence());

                    Assert.assertFalse(record.hasTimestampType(LOG_APPEND_TIME));
                    if ((magic) == (MAGIC_VALUE_V0)) {
                        Assert.assertEquals(NO_TIMESTAMP, record.timestamp());
                        Assert.assertFalse(record.hasTimestampType(CREATE_TIME));
                        Assert.assertTrue(record.hasTimestampType(NO_TIMESTAMP_TYPE));
                    } else {
                        Assert.assertEquals(records[total].timestamp(), record.timestamp());
                        Assert.assertFalse(record.hasTimestampType(NO_TIMESTAMP_TYPE));
                        if ((magic) < (MAGIC_VALUE_V2))
                            Assert.assertTrue(record.hasTimestampType(CREATE_TIME));
                        else
                            Assert.assertFalse(record.hasTimestampType(CREATE_TIME));

                    }
                    total++;
                    recordCount++;
                }
                Assert.assertEquals((((batch.baseOffset()) + recordCount) - 1), batch.lastOffset());
            }
        }
    }

    @Test
    public void testHasRoomForMethod() {
        assumeAtLeastV2OrNotZstd();
        MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), magic, compression, CREATE_TIME, 0L);
        builder.append(0L, "a".getBytes(), "1".getBytes());
        Assert.assertTrue(builder.hasRoomFor(1L, "b".getBytes(), "2".getBytes(), EMPTY_HEADERS));
        builder.close();
        Assert.assertFalse(builder.hasRoomFor(1L, "b".getBytes(), "2".getBytes(), EMPTY_HEADERS));
    }

    @Test
    public void testHasRoomForMethodWithHeaders() {
        if ((magic) >= (MAGIC_VALUE_V2)) {
            MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(100), magic, compression, CREATE_TIME, 0L);
            RecordHeaders headers = new RecordHeaders();
            headers.add("hello", "world.world".getBytes());
            headers.add("hello", "world.world".getBytes());
            headers.add("hello", "world.world".getBytes());
            headers.add("hello", "world.world".getBytes());
            headers.add("hello", "world.world".getBytes());
            builder.append(logAppendTime, "key".getBytes(), "value".getBytes());
            // Make sure that hasRoomFor accounts for header sizes by letting a record without headers pass, but stopping
            // a record with a large number of headers.
            Assert.assertTrue(builder.hasRoomFor(logAppendTime, "key".getBytes(), "value".getBytes(), EMPTY_HEADERS));
            Assert.assertFalse(builder.hasRoomFor(logAppendTime, "key".getBytes(), "value".getBytes(), headers.toArray()));
        }
    }

    /**
     * This test verifies that the checksum returned for various versions matches hardcoded values to catch unintentional
     * changes to how the checksum is computed.
     */
    @Test
    public void testChecksum() {
        // we get reasonable coverage with uncompressed and one compression type
        if (((compression) != (NONE)) && ((compression) != (LZ4)))
            return;

        SimpleRecord[] records = new SimpleRecord[]{ new SimpleRecord(283843L, "key1".getBytes(), "value1".getBytes()), new SimpleRecord(1234L, "key2".getBytes(), "value2".getBytes()) };
        RecordBatch batch = MemoryRecords.withRecords(magic, compression, records).batches().iterator().next();
        long expectedChecksum;
        if ((magic) == (MAGIC_VALUE_V0)) {
            if ((compression) == (NONE))
                expectedChecksum = 1978725405L;
            else
                expectedChecksum = 66944826L;

        } else
            if ((magic) == (MAGIC_VALUE_V1)) {
                if ((compression) == (NONE))
                    expectedChecksum = 109425508L;
                else
                    expectedChecksum = 1407303399L;

            } else {
                if ((compression) == (NONE))
                    expectedChecksum = 3851219455L;
                else
                    expectedChecksum = 2745969314L;

            }

        Assert.assertEquals(((("Unexpected checksum for magic " + (magic)) + " and compression type ") + (compression)), expectedChecksum, batch.checksum());
    }

    @Test
    public void testFilterToPreservesPartitionLeaderEpoch() {
        if ((magic) >= (MAGIC_VALUE_V2)) {
            int partitionLeaderEpoch = 67;
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 0L, NO_TIMESTAMP, partitionLeaderEpoch);
            builder.append(10L, null, "a".getBytes());
            builder.append(11L, "1".getBytes(), "b".getBytes());
            builder.append(12L, null, "c".getBytes());
            ByteBuffer filtered = ByteBuffer.allocate(2048);
            builder.build().filterTo(new TopicPartition("foo", 0), new MemoryRecordsTest.RetainNonNullKeysFilter(), filtered, Integer.MAX_VALUE, NO_CACHING);
            filtered.flip();
            MemoryRecords filteredRecords = MemoryRecords.readableRecords(filtered);
            List<MutableRecordBatch> batches = TestUtils.toList(filteredRecords.batches());
            Assert.assertEquals(1, batches.size());
            MutableRecordBatch firstBatch = batches.get(0);
            Assert.assertEquals(partitionLeaderEpoch, firstBatch.partitionLeaderEpoch());
        }
    }

    @Test
    public void testFilterToEmptyBatchRetention() {
        if ((magic) >= (MAGIC_VALUE_V2)) {
            for (boolean isTransactional : Arrays.asList(true, false)) {
                ByteBuffer buffer = ByteBuffer.allocate(2048);
                long producerId = 23L;
                short producerEpoch = 5;
                long baseOffset = 3L;
                int baseSequence = 10;
                int partitionLeaderEpoch = 293;
                int numRecords = 2;
                MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, baseOffset, NO_TIMESTAMP, producerId, producerEpoch, baseSequence, isTransactional, partitionLeaderEpoch);
                builder.append(11L, "2".getBytes(), "b".getBytes());
                builder.append(12L, "3".getBytes(), "c".getBytes());
                builder.close();
                MemoryRecords records = builder.build();
                ByteBuffer filtered = ByteBuffer.allocate(2048);
                MemoryRecords.FilterResult filterResult = records.filterTo(new TopicPartition("foo", 0), new MemoryRecords.RecordFilter() {
                    @Override
                    protected BatchRetention checkBatchRetention(RecordBatch batch) {
                        // retain all batches
                        return BatchRetention.RETAIN_EMPTY;
                    }

                    @Override
                    protected boolean shouldRetainRecord(RecordBatch recordBatch, Record record) {
                        // delete the records
                        return false;
                    }
                }, filtered, Integer.MAX_VALUE, NO_CACHING);
                // Verify filter result
                Assert.assertEquals(numRecords, filterResult.messagesRead());
                Assert.assertEquals(records.sizeInBytes(), filterResult.bytesRead());
                Assert.assertEquals((baseOffset + 1), filterResult.maxOffset());
                Assert.assertEquals(0, filterResult.messagesRetained());
                Assert.assertEquals(RECORD_BATCH_OVERHEAD, filterResult.bytesRetained());
                Assert.assertEquals(12, filterResult.maxTimestamp());
                Assert.assertEquals((baseOffset + 1), filterResult.shallowOffsetOfMaxTimestamp());
                // Verify filtered records
                filtered.flip();
                MemoryRecords filteredRecords = MemoryRecords.readableRecords(filtered);
                List<MutableRecordBatch> batches = TestUtils.toList(filteredRecords.batches());
                Assert.assertEquals(1, batches.size());
                MutableRecordBatch batch = batches.get(0);
                Assert.assertEquals(0, batch.countOrNull().intValue());
                Assert.assertEquals(12L, batch.maxTimestamp());
                Assert.assertEquals(CREATE_TIME, batch.timestampType());
                Assert.assertEquals(baseOffset, batch.baseOffset());
                Assert.assertEquals((baseOffset + 1), batch.lastOffset());
                Assert.assertEquals(baseSequence, batch.baseSequence());
                Assert.assertEquals((baseSequence + 1), batch.lastSequence());
                Assert.assertEquals(isTransactional, batch.isTransactional());
            }
        }
    }

    @Test
    public void testEmptyBatchRetention() {
        if ((magic) >= (MAGIC_VALUE_V2)) {
            ByteBuffer buffer = ByteBuffer.allocate(RECORD_BATCH_OVERHEAD);
            long producerId = 23L;
            short producerEpoch = 5;
            long baseOffset = 3L;
            int baseSequence = 10;
            int partitionLeaderEpoch = 293;
            long timestamp = System.currentTimeMillis();
            DefaultRecordBatch.writeEmptyHeader(buffer, MAGIC_VALUE_V2, producerId, producerEpoch, baseSequence, baseOffset, baseOffset, partitionLeaderEpoch, CREATE_TIME, timestamp, false, false);
            buffer.flip();
            ByteBuffer filtered = ByteBuffer.allocate(2048);
            MemoryRecords records = MemoryRecords.readableRecords(buffer);
            MemoryRecords.FilterResult filterResult = records.filterTo(new TopicPartition("foo", 0), new MemoryRecords.RecordFilter() {
                @Override
                protected BatchRetention checkBatchRetention(RecordBatch batch) {
                    // retain all batches
                    return BatchRetention.RETAIN_EMPTY;
                }

                @Override
                protected boolean shouldRetainRecord(RecordBatch recordBatch, Record record) {
                    return false;
                }
            }, filtered, Integer.MAX_VALUE, NO_CACHING);
            // Verify filter result
            Assert.assertEquals(0, filterResult.messagesRead());
            Assert.assertEquals(records.sizeInBytes(), filterResult.bytesRead());
            Assert.assertEquals(baseOffset, filterResult.maxOffset());
            Assert.assertEquals(0, filterResult.messagesRetained());
            Assert.assertEquals(RECORD_BATCH_OVERHEAD, filterResult.bytesRetained());
            Assert.assertEquals(timestamp, filterResult.maxTimestamp());
            Assert.assertEquals(baseOffset, filterResult.shallowOffsetOfMaxTimestamp());
            Assert.assertTrue(((filterResult.outputBuffer().position()) > 0));
            // Verify filtered records
            filtered.flip();
            MemoryRecords filteredRecords = MemoryRecords.readableRecords(filtered);
            Assert.assertEquals(RECORD_BATCH_OVERHEAD, filteredRecords.sizeInBytes());
        }
    }

    @Test
    public void testEmptyBatchDeletion() {
        if ((magic) >= (MAGIC_VALUE_V2)) {
            for (final BatchRetention deleteRetention : Arrays.asList(DELETE, DELETE_EMPTY)) {
                ByteBuffer buffer = ByteBuffer.allocate(RECORD_BATCH_OVERHEAD);
                long producerId = 23L;
                short producerEpoch = 5;
                long baseOffset = 3L;
                int baseSequence = 10;
                int partitionLeaderEpoch = 293;
                long timestamp = System.currentTimeMillis();
                DefaultRecordBatch.writeEmptyHeader(buffer, MAGIC_VALUE_V2, producerId, producerEpoch, baseSequence, baseOffset, baseOffset, partitionLeaderEpoch, CREATE_TIME, timestamp, false, false);
                buffer.flip();
                ByteBuffer filtered = ByteBuffer.allocate(2048);
                MemoryRecords records = MemoryRecords.readableRecords(buffer);
                MemoryRecords.FilterResult filterResult = records.filterTo(new TopicPartition("foo", 0), new MemoryRecords.RecordFilter() {
                    @Override
                    protected BatchRetention checkBatchRetention(RecordBatch batch) {
                        return deleteRetention;
                    }

                    @Override
                    protected boolean shouldRetainRecord(RecordBatch recordBatch, Record record) {
                        return false;
                    }
                }, filtered, Integer.MAX_VALUE, NO_CACHING);
                // Verify filter result
                Assert.assertEquals(0, filterResult.outputBuffer().position());
                // Verify filtered records
                filtered.flip();
                MemoryRecords filteredRecords = MemoryRecords.readableRecords(filtered);
                Assert.assertEquals(0, filteredRecords.sizeInBytes());
            }
        }
    }

    @Test
    public void testBuildEndTxnMarker() {
        if ((magic) >= (MAGIC_VALUE_V2)) {
            long producerId = 73;
            short producerEpoch = 13;
            long initialOffset = 983L;
            int coordinatorEpoch = 347;
            int partitionLeaderEpoch = 29;
            EndTransactionMarker marker = new EndTransactionMarker(COMMIT, coordinatorEpoch);
            MemoryRecords records = MemoryRecords.withEndTransactionMarker(initialOffset, System.currentTimeMillis(), partitionLeaderEpoch, producerId, producerEpoch, marker);
            // verify that buffer allocation was precise
            Assert.assertEquals(records.buffer().remaining(), records.buffer().capacity());
            List<MutableRecordBatch> batches = TestUtils.toList(records.batches());
            Assert.assertEquals(1, batches.size());
            RecordBatch batch = batches.get(0);
            Assert.assertTrue(batch.isControlBatch());
            Assert.assertEquals(producerId, batch.producerId());
            Assert.assertEquals(producerEpoch, batch.producerEpoch());
            Assert.assertEquals(initialOffset, batch.baseOffset());
            Assert.assertEquals(partitionLeaderEpoch, batch.partitionLeaderEpoch());
            Assert.assertTrue(batch.isValid());
            List<Record> createdRecords = TestUtils.toList(batch);
            Assert.assertEquals(1, createdRecords.size());
            Record record = createdRecords.get(0);
            Assert.assertTrue(record.isValid());
            EndTransactionMarker deserializedMarker = EndTransactionMarker.deserialize(record);
            Assert.assertEquals(COMMIT, deserializedMarker.controlType());
            Assert.assertEquals(coordinatorEpoch, deserializedMarker.coordinatorEpoch());
        }
    }

    @Test
    public void testFilterToBatchDiscard() {
        assumeAtLeastV2OrNotZstd();
        Assume.assumeTrue((((compression) != (NONE)) || ((magic) >= (RecordBatch.MAGIC_VALUE_V2))));
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 0L);
        builder.append(10L, "1".getBytes(), "a".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 1L);
        builder.append(11L, "2".getBytes(), "b".getBytes());
        builder.append(12L, "3".getBytes(), "c".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 3L);
        builder.append(13L, "4".getBytes(), "d".getBytes());
        builder.append(20L, "5".getBytes(), "e".getBytes());
        builder.append(15L, "6".getBytes(), "f".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 6L);
        builder.append(16L, "7".getBytes(), "g".getBytes());
        builder.close();
        buffer.flip();
        ByteBuffer filtered = ByteBuffer.allocate(2048);
        MemoryRecords.readableRecords(buffer).filterTo(new TopicPartition("foo", 0), new MemoryRecords.RecordFilter() {
            @Override
            protected BatchRetention checkBatchRetention(RecordBatch batch) {
                // discard the second and fourth batches
                if (((batch.lastOffset()) == 2L) || ((batch.lastOffset()) == 6L))
                    return BatchRetention.DELETE;

                return BatchRetention.DELETE_EMPTY;
            }

            @Override
            protected boolean shouldRetainRecord(RecordBatch recordBatch, Record record) {
                return true;
            }
        }, filtered, Integer.MAX_VALUE, NO_CACHING);
        filtered.flip();
        MemoryRecords filteredRecords = MemoryRecords.readableRecords(filtered);
        List<MutableRecordBatch> batches = TestUtils.toList(filteredRecords.batches());
        Assert.assertEquals(2, batches.size());
        Assert.assertEquals(0L, batches.get(0).lastOffset());
        Assert.assertEquals(5L, batches.get(1).lastOffset());
    }

    @Test
    public void testFilterToAlreadyCompactedLog() {
        assumeAtLeastV2OrNotZstd();
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        // create a batch with some offset gaps to simulate a compacted batch
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 0L);
        builder.appendWithOffset(5L, 10L, null, "a".getBytes());
        builder.appendWithOffset(8L, 11L, "1".getBytes(), "b".getBytes());
        builder.appendWithOffset(10L, 12L, null, "c".getBytes());
        builder.close();
        buffer.flip();
        ByteBuffer filtered = ByteBuffer.allocate(2048);
        MemoryRecords.readableRecords(buffer).filterTo(new TopicPartition("foo", 0), new MemoryRecordsTest.RetainNonNullKeysFilter(), filtered, Integer.MAX_VALUE, NO_CACHING);
        filtered.flip();
        MemoryRecords filteredRecords = MemoryRecords.readableRecords(filtered);
        List<MutableRecordBatch> batches = TestUtils.toList(filteredRecords.batches());
        Assert.assertEquals(1, batches.size());
        MutableRecordBatch batch = batches.get(0);
        List<Record> records = TestUtils.toList(batch);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals(8L, records.get(0).offset());
        if ((magic) >= (MAGIC_VALUE_V1))
            Assert.assertEquals(new SimpleRecord(11L, "1".getBytes(), "b".getBytes()), new SimpleRecord(records.get(0)));
        else
            Assert.assertEquals(new SimpleRecord(NO_TIMESTAMP, "1".getBytes(), "b".getBytes()), new SimpleRecord(records.get(0)));

        if ((magic) >= (MAGIC_VALUE_V2)) {
            // the new format preserves first and last offsets from the original batch
            Assert.assertEquals(0L, batch.baseOffset());
            Assert.assertEquals(10L, batch.lastOffset());
        } else {
            Assert.assertEquals(8L, batch.baseOffset());
            Assert.assertEquals(8L, batch.lastOffset());
        }
    }

    @Test
    public void testFilterToPreservesProducerInfo() {
        if ((magic) >= (MAGIC_VALUE_V2)) {
            ByteBuffer buffer = ByteBuffer.allocate(2048);
            // non-idempotent, non-transactional
            MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 0L);
            builder.append(10L, null, "a".getBytes());
            builder.append(11L, "1".getBytes(), "b".getBytes());
            builder.append(12L, null, "c".getBytes());
            builder.close();
            // idempotent
            long pid1 = 23L;
            short epoch1 = 5;
            int baseSequence1 = 10;
            builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 3L, NO_TIMESTAMP, pid1, epoch1, baseSequence1);
            builder.append(13L, null, "d".getBytes());
            builder.append(14L, "4".getBytes(), "e".getBytes());
            builder.append(15L, "5".getBytes(), "f".getBytes());
            builder.close();
            // transactional
            long pid2 = 99384L;
            short epoch2 = 234;
            int baseSequence2 = 15;
            builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 3L, NO_TIMESTAMP, pid2, epoch2, baseSequence2, true, NO_PARTITION_LEADER_EPOCH);
            builder.append(16L, "6".getBytes(), "g".getBytes());
            builder.append(17L, "7".getBytes(), "h".getBytes());
            builder.append(18L, null, "i".getBytes());
            builder.close();
            buffer.flip();
            ByteBuffer filtered = ByteBuffer.allocate(2048);
            MemoryRecords.readableRecords(buffer).filterTo(new TopicPartition("foo", 0), new MemoryRecordsTest.RetainNonNullKeysFilter(), filtered, Integer.MAX_VALUE, NO_CACHING);
            filtered.flip();
            MemoryRecords filteredRecords = MemoryRecords.readableRecords(filtered);
            List<MutableRecordBatch> batches = TestUtils.toList(filteredRecords.batches());
            Assert.assertEquals(3, batches.size());
            MutableRecordBatch firstBatch = batches.get(0);
            Assert.assertEquals(1, firstBatch.countOrNull().intValue());
            Assert.assertEquals(0L, firstBatch.baseOffset());
            Assert.assertEquals(2L, firstBatch.lastOffset());
            Assert.assertEquals(NO_PRODUCER_ID, firstBatch.producerId());
            Assert.assertEquals(NO_PRODUCER_EPOCH, firstBatch.producerEpoch());
            Assert.assertEquals(NO_SEQUENCE, firstBatch.baseSequence());
            Assert.assertEquals(NO_SEQUENCE, firstBatch.lastSequence());
            Assert.assertFalse(firstBatch.isTransactional());
            List<Record> firstBatchRecords = TestUtils.toList(firstBatch);
            Assert.assertEquals(1, firstBatchRecords.size());
            Assert.assertEquals(NO_SEQUENCE, firstBatchRecords.get(0).sequence());
            Assert.assertEquals(new SimpleRecord(11L, "1".getBytes(), "b".getBytes()), new SimpleRecord(firstBatchRecords.get(0)));
            MutableRecordBatch secondBatch = batches.get(1);
            Assert.assertEquals(2, secondBatch.countOrNull().intValue());
            Assert.assertEquals(3L, secondBatch.baseOffset());
            Assert.assertEquals(5L, secondBatch.lastOffset());
            Assert.assertEquals(pid1, secondBatch.producerId());
            Assert.assertEquals(epoch1, secondBatch.producerEpoch());
            Assert.assertEquals(baseSequence1, secondBatch.baseSequence());
            Assert.assertEquals((baseSequence1 + 2), secondBatch.lastSequence());
            Assert.assertFalse(secondBatch.isTransactional());
            List<Record> secondBatchRecords = TestUtils.toList(secondBatch);
            Assert.assertEquals(2, secondBatchRecords.size());
            Assert.assertEquals((baseSequence1 + 1), secondBatchRecords.get(0).sequence());
            Assert.assertEquals(new SimpleRecord(14L, "4".getBytes(), "e".getBytes()), new SimpleRecord(secondBatchRecords.get(0)));
            Assert.assertEquals((baseSequence1 + 2), secondBatchRecords.get(1).sequence());
            Assert.assertEquals(new SimpleRecord(15L, "5".getBytes(), "f".getBytes()), new SimpleRecord(secondBatchRecords.get(1)));
            MutableRecordBatch thirdBatch = batches.get(2);
            Assert.assertEquals(2, thirdBatch.countOrNull().intValue());
            Assert.assertEquals(3L, thirdBatch.baseOffset());
            Assert.assertEquals(5L, thirdBatch.lastOffset());
            Assert.assertEquals(pid2, thirdBatch.producerId());
            Assert.assertEquals(epoch2, thirdBatch.producerEpoch());
            Assert.assertEquals(baseSequence2, thirdBatch.baseSequence());
            Assert.assertEquals((baseSequence2 + 2), thirdBatch.lastSequence());
            Assert.assertTrue(thirdBatch.isTransactional());
            List<Record> thirdBatchRecords = TestUtils.toList(thirdBatch);
            Assert.assertEquals(2, thirdBatchRecords.size());
            Assert.assertEquals(baseSequence2, thirdBatchRecords.get(0).sequence());
            Assert.assertEquals(new SimpleRecord(16L, "6".getBytes(), "g".getBytes()), new SimpleRecord(thirdBatchRecords.get(0)));
            Assert.assertEquals((baseSequence2 + 1), thirdBatchRecords.get(1).sequence());
            Assert.assertEquals(new SimpleRecord(17L, "7".getBytes(), "h".getBytes()), new SimpleRecord(thirdBatchRecords.get(1)));
        }
    }

    @Test
    public void testFilterToWithUndersizedBuffer() {
        assumeAtLeastV2OrNotZstd();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 0L);
        builder.append(10L, null, "a".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 1L);
        builder.append(11L, "1".getBytes(), new byte[128]);
        builder.append(12L, "2".getBytes(), "c".getBytes());
        builder.append(13L, null, "d".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 4L);
        builder.append(14L, null, "e".getBytes());
        builder.append(15L, "5".getBytes(), "f".getBytes());
        builder.append(16L, "6".getBytes(), "g".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 7L);
        builder.append(17L, "7".getBytes(), new byte[128]);
        builder.close();
        buffer.flip();
        ByteBuffer output = ByteBuffer.allocate(64);
        List<Record> records = new ArrayList<>();
        while (buffer.hasRemaining()) {
            output.rewind();
            MemoryRecords.FilterResult result = MemoryRecords.readableRecords(buffer).filterTo(new TopicPartition("foo", 0), new MemoryRecordsTest.RetainNonNullKeysFilter(), output, Integer.MAX_VALUE, NO_CACHING);
            buffer.position(((buffer.position()) + (result.bytesRead())));
            result.outputBuffer().flip();
            if (output != (result.outputBuffer()))
                Assert.assertEquals(0, output.position());

            MemoryRecords filtered = MemoryRecords.readableRecords(result.outputBuffer());
            records.addAll(TestUtils.toList(filtered.records()));
        } 
        Assert.assertEquals(5, records.size());
        for (Record record : records)
            Assert.assertNotNull(record.key());

    }

    @Test
    public void testToString() {
        assumeAtLeastV2OrNotZstd();
        long timestamp = 1000000;
        MemoryRecords memoryRecords = MemoryRecords.withRecords(magic, compression, new SimpleRecord(timestamp, "key1".getBytes(), "value1".getBytes()), new SimpleRecord((timestamp + 1), "key2".getBytes(), "value2".getBytes()));
        switch (magic) {
            case MAGIC_VALUE_V0 :
                Assert.assertEquals(("[(record=LegacyRecordBatch(offset=0, Record(magic=0, attributes=0, compression=NONE, " + ("crc=1978725405, key=4 bytes, value=6 bytes))), (record=LegacyRecordBatch(offset=1, Record(magic=0, " + "attributes=0, compression=NONE, crc=1964753830, key=4 bytes, value=6 bytes)))]")), memoryRecords.toString());
                break;
            case MAGIC_VALUE_V1 :
                Assert.assertEquals(("[(record=LegacyRecordBatch(offset=0, Record(magic=1, attributes=0, compression=NONE, " + (("crc=97210616, CreateTime=1000000, key=4 bytes, value=6 bytes))), (record=LegacyRecordBatch(offset=1, " + "Record(magic=1, attributes=0, compression=NONE, crc=3535988507, CreateTime=1000001, key=4 bytes, ") + "value=6 bytes)))]")), memoryRecords.toString());
                break;
            case MAGIC_VALUE_V2 :
                Assert.assertEquals(("[(record=DefaultRecord(offset=0, timestamp=1000000, key=4 bytes, value=6 bytes)), " + "(record=DefaultRecord(offset=1, timestamp=1000001, key=4 bytes, value=6 bytes))]"), memoryRecords.toString());
                break;
            default :
                Assert.fail(("Unexpected magic " + (magic)));
        }
    }

    @Test
    public void testFilterTo() {
        assumeAtLeastV2OrNotZstd();
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 0L);
        builder.append(10L, null, "a".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 1L);
        builder.append(11L, "1".getBytes(), "b".getBytes());
        builder.append(12L, null, "c".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 3L);
        builder.append(13L, null, "d".getBytes());
        builder.append(20L, "4".getBytes(), "e".getBytes());
        builder.append(15L, "5".getBytes(), "f".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, magic, compression, CREATE_TIME, 6L);
        builder.append(16L, "6".getBytes(), "g".getBytes());
        builder.close();
        buffer.flip();
        ByteBuffer filtered = ByteBuffer.allocate(2048);
        MemoryRecords.FilterResult result = MemoryRecords.readableRecords(buffer).filterTo(new TopicPartition("foo", 0), new MemoryRecordsTest.RetainNonNullKeysFilter(), filtered, Integer.MAX_VALUE, NO_CACHING);
        filtered.flip();
        Assert.assertEquals(7, result.messagesRead());
        Assert.assertEquals(4, result.messagesRetained());
        Assert.assertEquals(buffer.limit(), result.bytesRead());
        Assert.assertEquals(filtered.limit(), result.bytesRetained());
        if ((magic) > (MAGIC_VALUE_V0)) {
            Assert.assertEquals(20L, result.maxTimestamp());
            if (((compression) == (NONE)) && ((magic) < (MAGIC_VALUE_V2)))
                Assert.assertEquals(4L, result.shallowOffsetOfMaxTimestamp());
            else
                Assert.assertEquals(5L, result.shallowOffsetOfMaxTimestamp());

        }
        MemoryRecords filteredRecords = MemoryRecords.readableRecords(filtered);
        List<MutableRecordBatch> batches = TestUtils.toList(filteredRecords.batches());
        final List<Long> expectedEndOffsets;
        final List<Long> expectedStartOffsets;
        final List<Long> expectedMaxTimestamps;
        if (((magic) < (MAGIC_VALUE_V2)) && ((compression) == (NONE))) {
            expectedEndOffsets = Arrays.asList(1L, 4L, 5L, 6L);
            expectedStartOffsets = Arrays.asList(1L, 4L, 5L, 6L);
            expectedMaxTimestamps = Arrays.asList(11L, 20L, 15L, 16L);
        } else
            if ((magic) < (MAGIC_VALUE_V2)) {
                expectedEndOffsets = Arrays.asList(1L, 5L, 6L);
                expectedStartOffsets = Arrays.asList(1L, 4L, 6L);
                expectedMaxTimestamps = Arrays.asList(11L, 20L, 16L);
            } else {
                expectedEndOffsets = Arrays.asList(2L, 5L, 6L);
                expectedStartOffsets = Arrays.asList(1L, 3L, 6L);
                expectedMaxTimestamps = Arrays.asList(11L, 20L, 16L);
            }

        Assert.assertEquals(expectedEndOffsets.size(), batches.size());
        for (int i = 0; i < (expectedEndOffsets.size()); i++) {
            RecordBatch batch = batches.get(i);
            Assert.assertEquals(expectedStartOffsets.get(i).longValue(), batch.baseOffset());
            Assert.assertEquals(expectedEndOffsets.get(i).longValue(), batch.lastOffset());
            Assert.assertEquals(magic, batch.magic());
            Assert.assertEquals(compression, batch.compressionType());
            if ((magic) >= (MAGIC_VALUE_V1)) {
                Assert.assertEquals(expectedMaxTimestamps.get(i).longValue(), batch.maxTimestamp());
                Assert.assertEquals(CREATE_TIME, batch.timestampType());
            } else {
                Assert.assertEquals(NO_TIMESTAMP, batch.maxTimestamp());
                Assert.assertEquals(NO_TIMESTAMP_TYPE, batch.timestampType());
            }
        }
        List<Record> records = TestUtils.toList(filteredRecords.records());
        Assert.assertEquals(4, records.size());
        Record first = records.get(0);
        Assert.assertEquals(1L, first.offset());
        if ((magic) > (MAGIC_VALUE_V0))
            Assert.assertEquals(11L, first.timestamp());

        Assert.assertEquals("1", Utils.utf8(first.key(), first.keySize()));
        Assert.assertEquals("b", Utils.utf8(first.value(), first.valueSize()));
        Record second = records.get(1);
        Assert.assertEquals(4L, second.offset());
        if ((magic) > (MAGIC_VALUE_V0))
            Assert.assertEquals(20L, second.timestamp());

        Assert.assertEquals("4", Utils.utf8(second.key(), second.keySize()));
        Assert.assertEquals("e", Utils.utf8(second.value(), second.valueSize()));
        Record third = records.get(2);
        Assert.assertEquals(5L, third.offset());
        if ((magic) > (MAGIC_VALUE_V0))
            Assert.assertEquals(15L, third.timestamp());

        Assert.assertEquals("5", Utils.utf8(third.key(), third.keySize()));
        Assert.assertEquals("f", Utils.utf8(third.value(), third.valueSize()));
        Record fourth = records.get(3);
        Assert.assertEquals(6L, fourth.offset());
        if ((magic) > (MAGIC_VALUE_V0))
            Assert.assertEquals(16L, fourth.timestamp());

        Assert.assertEquals("6", Utils.utf8(fourth.key(), fourth.keySize()));
        Assert.assertEquals("g", Utils.utf8(fourth.value(), fourth.valueSize()));
    }

    @Test
    public void testFilterToPreservesLogAppendTime() {
        assumeAtLeastV2OrNotZstd();
        long logAppendTime = System.currentTimeMillis();
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, magic, compression, LOG_APPEND_TIME, 0L, logAppendTime, pid, epoch, firstSequence);
        builder.append(10L, null, "a".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, magic, compression, LOG_APPEND_TIME, 1L, logAppendTime, pid, epoch, firstSequence);
        builder.append(11L, "1".getBytes(), "b".getBytes());
        builder.append(12L, null, "c".getBytes());
        builder.close();
        builder = MemoryRecords.builder(buffer, magic, compression, LOG_APPEND_TIME, 3L, logAppendTime, pid, epoch, firstSequence);
        builder.append(13L, null, "d".getBytes());
        builder.append(14L, "4".getBytes(), "e".getBytes());
        builder.append(15L, "5".getBytes(), "f".getBytes());
        builder.close();
        buffer.flip();
        ByteBuffer filtered = ByteBuffer.allocate(2048);
        MemoryRecords.readableRecords(buffer).filterTo(new TopicPartition("foo", 0), new MemoryRecordsTest.RetainNonNullKeysFilter(), filtered, Integer.MAX_VALUE, NO_CACHING);
        filtered.flip();
        MemoryRecords filteredRecords = MemoryRecords.readableRecords(filtered);
        List<MutableRecordBatch> batches = TestUtils.toList(filteredRecords.batches());
        Assert.assertEquals((((magic) < (MAGIC_VALUE_V2)) && ((compression) == (NONE)) ? 3 : 2), batches.size());
        for (RecordBatch batch : batches) {
            Assert.assertEquals(compression, batch.compressionType());
            if ((magic) > (MAGIC_VALUE_V0)) {
                Assert.assertEquals(LOG_APPEND_TIME, batch.timestampType());
                Assert.assertEquals(logAppendTime, batch.maxTimestamp());
            }
        }
    }

    @Test
    public void testNextBatchSize() {
        assumeAtLeastV2OrNotZstd();
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        MemoryRecordsBuilder builder = MemoryRecords.builder(buffer, magic, compression, LOG_APPEND_TIME, 0L, logAppendTime, pid, epoch, firstSequence);
        builder.append(10L, null, "abc".getBytes());
        builder.close();
        buffer.flip();
        int size = buffer.remaining();
        MemoryRecords records = MemoryRecords.readableRecords(buffer);
        Assert.assertEquals(size, records.firstBatchSize().intValue());
        Assert.assertEquals(0, buffer.position());
        buffer.limit(1);// size not in buffer

        Assert.assertNull(records.firstBatchSize());
        buffer.limit(LOG_OVERHEAD);// magic not in buffer

        Assert.assertNull(records.firstBatchSize());
        buffer.limit(HEADER_SIZE_UP_TO_MAGIC);// payload not in buffer

        Assert.assertEquals(size, records.firstBatchSize().intValue());
        buffer.limit(size);
        byte magic = buffer.get(MAGIC_OFFSET);
        buffer.put(MAGIC_OFFSET, ((byte) (10)));
        Assert.assertThrows(CorruptRecordException.class, records::firstBatchSize);
        buffer.put(MAGIC_OFFSET, magic);
        buffer.put(((SIZE_OFFSET) + 3), ((byte) (0)));
        Assert.assertThrows(CorruptRecordException.class, records::firstBatchSize);
    }

    @Test
    public void testWithRecords() {
        Supplier<MemoryRecords> recordsSupplier = () -> MemoryRecords.withRecords(magic, compression, new SimpleRecord(10L, "key1".getBytes(), "value1".getBytes()));
        if (((compression) != (ZSTD)) || ((magic) >= (RecordBatch.MAGIC_VALUE_V2))) {
            MemoryRecords memoryRecords = recordsSupplier.get();
            String key = Utils.utf8(memoryRecords.batches().iterator().next().iterator().next().key());
            Assert.assertEquals("key1", key);
        } else {
            Assert.assertThrows(IllegalArgumentException.class, recordsSupplier::get);
        }
    }

    private static class RetainNonNullKeysFilter extends MemoryRecords.RecordFilter {
        @Override
        protected BatchRetention checkBatchRetention(RecordBatch batch) {
            return BatchRetention.DELETE_EMPTY;
        }

        @Override
        public boolean shouldRetainRecord(RecordBatch batch, Record record) {
            return record.hasKey();
        }
    }
}

