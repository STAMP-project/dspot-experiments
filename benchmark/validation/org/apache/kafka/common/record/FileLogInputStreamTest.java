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


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import org.apache.kafka.common.record.FileLogInputStream.FileChannelRecordBatch;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static CompressionType.NONE;
import static CompressionType.ZSTD;


@RunWith(Parameterized.class)
public class FileLogInputStreamTest {
    private final byte magic;

    private final CompressionType compression;

    public FileLogInputStreamTest(byte magic, CompressionType compression) {
        this.magic = magic;
        this.compression = compression;
    }

    @Test
    public void testWriteTo() throws IOException {
        if (((compression) == (ZSTD)) && ((magic) < (RecordBatch.MAGIC_VALUE_V2)))
            return;

        try (FileRecords fileRecords = FileRecords.open(TestUtils.tempFile())) {
            fileRecords.append(MemoryRecords.withRecords(magic, compression, new SimpleRecord("foo".getBytes())));
            fileRecords.flush();
            FileLogInputStream logInputStream = new FileLogInputStream(fileRecords, 0, fileRecords.sizeInBytes());
            FileChannelRecordBatch batch = logInputStream.nextBatch();
            Assert.assertNotNull(batch);
            Assert.assertEquals(magic, batch.magic());
            ByteBuffer buffer = ByteBuffer.allocate(128);
            batch.writeTo(buffer);
            buffer.flip();
            MemoryRecords memRecords = MemoryRecords.readableRecords(buffer);
            List<Record> records = Utils.toList(memRecords.records().iterator());
            Assert.assertEquals(1, records.size());
            Record record0 = records.get(0);
            Assert.assertTrue(record0.hasMagic(magic));
            Assert.assertEquals("foo", Utils.utf8(record0.value(), record0.valueSize()));
        }
    }

    @Test
    public void testSimpleBatchIteration() throws IOException {
        if (((compression) == (ZSTD)) && ((magic) < (RecordBatch.MAGIC_VALUE_V2)))
            return;

        try (FileRecords fileRecords = FileRecords.open(TestUtils.tempFile())) {
            SimpleRecord firstBatchRecord = new SimpleRecord(3241324L, "a".getBytes(), "foo".getBytes());
            SimpleRecord secondBatchRecord = new SimpleRecord(234280L, "b".getBytes(), "bar".getBytes());
            fileRecords.append(MemoryRecords.withRecords(magic, 0L, compression, TimestampType.CREATE_TIME, firstBatchRecord));
            fileRecords.append(MemoryRecords.withRecords(magic, 1L, compression, TimestampType.CREATE_TIME, secondBatchRecord));
            fileRecords.flush();
            FileLogInputStream logInputStream = new FileLogInputStream(fileRecords, 0, fileRecords.sizeInBytes());
            FileChannelRecordBatch firstBatch = logInputStream.nextBatch();
            assertGenericRecordBatchData(firstBatch, 0L, 3241324L, firstBatchRecord);
            assertNoProducerData(firstBatch);
            FileChannelRecordBatch secondBatch = logInputStream.nextBatch();
            assertGenericRecordBatchData(secondBatch, 1L, 234280L, secondBatchRecord);
            assertNoProducerData(secondBatch);
            Assert.assertNull(logInputStream.nextBatch());
        }
    }

    @Test
    public void testBatchIterationWithMultipleRecordsPerBatch() throws IOException {
        if (((magic) < (RecordBatch.MAGIC_VALUE_V2)) && ((compression) == (NONE)))
            return;

        if (((compression) == (ZSTD)) && ((magic) < (RecordBatch.MAGIC_VALUE_V2)))
            return;

        try (FileRecords fileRecords = FileRecords.open(TestUtils.tempFile())) {
            SimpleRecord[] firstBatchRecords = new SimpleRecord[]{ new SimpleRecord(3241324L, "a".getBytes(), "1".getBytes()), new SimpleRecord(234280L, "b".getBytes(), "2".getBytes()) };
            SimpleRecord[] secondBatchRecords = new SimpleRecord[]{ new SimpleRecord(238423489L, "c".getBytes(), "3".getBytes()), new SimpleRecord(897839L, null, "4".getBytes()), new SimpleRecord(8234020L, "e".getBytes(), null) };
            fileRecords.append(MemoryRecords.withRecords(magic, 0L, compression, TimestampType.CREATE_TIME, firstBatchRecords));
            fileRecords.append(MemoryRecords.withRecords(magic, 1L, compression, TimestampType.CREATE_TIME, secondBatchRecords));
            fileRecords.flush();
            FileLogInputStream logInputStream = new FileLogInputStream(fileRecords, 0, fileRecords.sizeInBytes());
            FileChannelRecordBatch firstBatch = logInputStream.nextBatch();
            assertNoProducerData(firstBatch);
            assertGenericRecordBatchData(firstBatch, 0L, 3241324L, firstBatchRecords);
            FileChannelRecordBatch secondBatch = logInputStream.nextBatch();
            assertNoProducerData(secondBatch);
            assertGenericRecordBatchData(secondBatch, 1L, 238423489L, secondBatchRecords);
            Assert.assertNull(logInputStream.nextBatch());
        }
    }

    @Test
    public void testBatchIterationV2() throws IOException {
        if ((magic) != (RecordBatch.MAGIC_VALUE_V2))
            return;

        try (FileRecords fileRecords = FileRecords.open(TestUtils.tempFile())) {
            long producerId = 83843L;
            short producerEpoch = 15;
            int baseSequence = 234;
            int partitionLeaderEpoch = 9832;
            SimpleRecord[] firstBatchRecords = new SimpleRecord[]{ new SimpleRecord(3241324L, "a".getBytes(), "1".getBytes()), new SimpleRecord(234280L, "b".getBytes(), "2".getBytes()) };
            SimpleRecord[] secondBatchRecords = new SimpleRecord[]{ new SimpleRecord(238423489L, "c".getBytes(), "3".getBytes()), new SimpleRecord(897839L, null, "4".getBytes()), new SimpleRecord(8234020L, "e".getBytes(), null) };
            fileRecords.append(MemoryRecords.withIdempotentRecords(magic, 15L, compression, producerId, producerEpoch, baseSequence, partitionLeaderEpoch, firstBatchRecords));
            fileRecords.append(MemoryRecords.withTransactionalRecords(magic, 27L, compression, producerId, producerEpoch, (baseSequence + (firstBatchRecords.length)), partitionLeaderEpoch, secondBatchRecords));
            fileRecords.flush();
            FileLogInputStream logInputStream = new FileLogInputStream(fileRecords, 0, fileRecords.sizeInBytes());
            FileChannelRecordBatch firstBatch = logInputStream.nextBatch();
            assertProducerData(firstBatch, producerId, producerEpoch, baseSequence, false, firstBatchRecords);
            assertGenericRecordBatchData(firstBatch, 15L, 3241324L, firstBatchRecords);
            Assert.assertEquals(partitionLeaderEpoch, firstBatch.partitionLeaderEpoch());
            FileChannelRecordBatch secondBatch = logInputStream.nextBatch();
            assertProducerData(secondBatch, producerId, producerEpoch, (baseSequence + (firstBatchRecords.length)), true, secondBatchRecords);
            assertGenericRecordBatchData(secondBatch, 27L, 238423489L, secondBatchRecords);
            Assert.assertEquals(partitionLeaderEpoch, secondBatch.partitionLeaderEpoch());
            Assert.assertNull(logInputStream.nextBatch());
        }
    }

    @Test
    public void testBatchIterationIncompleteBatch() throws IOException {
        if (((compression) == (ZSTD)) && ((magic) < (RecordBatch.MAGIC_VALUE_V2)))
            return;

        try (FileRecords fileRecords = FileRecords.open(TestUtils.tempFile())) {
            SimpleRecord firstBatchRecord = new SimpleRecord(100L, "foo".getBytes());
            SimpleRecord secondBatchRecord = new SimpleRecord(200L, "bar".getBytes());
            fileRecords.append(MemoryRecords.withRecords(magic, 0L, compression, TimestampType.CREATE_TIME, firstBatchRecord));
            fileRecords.append(MemoryRecords.withRecords(magic, 1L, compression, TimestampType.CREATE_TIME, secondBatchRecord));
            fileRecords.flush();
            fileRecords.truncateTo(((fileRecords.sizeInBytes()) - 13));
            FileLogInputStream logInputStream = new FileLogInputStream(fileRecords, 0, fileRecords.sizeInBytes());
            FileChannelRecordBatch firstBatch = logInputStream.nextBatch();
            assertNoProducerData(firstBatch);
            assertGenericRecordBatchData(firstBatch, 0L, 100L, firstBatchRecord);
            Assert.assertNull(logInputStream.nextBatch());
        }
    }

    @Test
    public void testNextBatchSelectionWithMaxedParams() throws IOException {
        try (FileRecords fileRecords = FileRecords.open(TestUtils.tempFile())) {
            FileLogInputStream logInputStream = new FileLogInputStream(fileRecords, Integer.MAX_VALUE, Integer.MAX_VALUE);
            Assert.assertNull(logInputStream.nextBatch());
        }
    }

    @Test
    public void testNextBatchSelectionWithZeroedParams() throws IOException {
        try (FileRecords fileRecords = FileRecords.open(TestUtils.tempFile())) {
            FileLogInputStream logInputStream = new FileLogInputStream(fileRecords, 0, 0);
            Assert.assertNull(logInputStream.nextBatch());
        }
    }
}

