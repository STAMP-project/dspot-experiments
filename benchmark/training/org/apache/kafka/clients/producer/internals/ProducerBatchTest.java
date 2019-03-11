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
package org.apache.kafka.clients.producer.internals;


import CompressionType.NONE;
import Record.EMPTY_HEADERS;
import TimestampType.CREATE_TIME;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Deque;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.record.CompressionType;
import org.apache.kafka.common.record.LegacyRecord;
import org.apache.kafka.common.record.MemoryRecords;
import org.apache.kafka.common.record.MemoryRecordsBuilder;
import org.apache.kafka.common.record.Record;
import org.apache.kafka.common.record.RecordBatch;
import org.junit.Assert;
import org.junit.Test;


public class ProducerBatchTest {
    private final long now = 1488748346917L;

    private final MemoryRecordsBuilder memoryRecordsBuilder = MemoryRecords.builder(ByteBuffer.allocate(128), NONE, CREATE_TIME, 128);

    @Test
    public void testChecksumNullForMagicV2() {
        ProducerBatch batch = new ProducerBatch(new TopicPartition("topic", 1), memoryRecordsBuilder, now);
        FutureRecordMetadata future = batch.tryAppend(now, null, new byte[10], EMPTY_HEADERS, null, now);
        Assert.assertNotNull(future);
        Assert.assertNull(future.checksumOrNull());
    }

    @Test
    public void testBatchAbort() throws Exception {
        ProducerBatch batch = new ProducerBatch(new TopicPartition("topic", 1), memoryRecordsBuilder, now);
        ProducerBatchTest.MockCallback callback = new ProducerBatchTest.MockCallback();
        FutureRecordMetadata future = batch.tryAppend(now, null, new byte[10], EMPTY_HEADERS, callback, now);
        KafkaException exception = new KafkaException();
        batch.abort(exception);
        Assert.assertTrue(future.isDone());
        Assert.assertEquals(1, callback.invocations);
        Assert.assertEquals(exception, callback.exception);
        Assert.assertNull(callback.metadata);
        // subsequent completion should be ignored
        Assert.assertFalse(batch.done(500L, 2342342341L, null));
        Assert.assertFalse(batch.done((-1), (-1), new KafkaException()));
        Assert.assertEquals(1, callback.invocations);
        Assert.assertTrue(future.isDone());
        try {
            future.get();
            Assert.fail("Future should have thrown");
        } catch (ExecutionException e) {
            Assert.assertEquals(exception, e.getCause());
        }
    }

    @Test
    public void testBatchCannotAbortTwice() throws Exception {
        ProducerBatch batch = new ProducerBatch(new TopicPartition("topic", 1), memoryRecordsBuilder, now);
        ProducerBatchTest.MockCallback callback = new ProducerBatchTest.MockCallback();
        FutureRecordMetadata future = batch.tryAppend(now, null, new byte[10], EMPTY_HEADERS, callback, now);
        KafkaException exception = new KafkaException();
        batch.abort(exception);
        Assert.assertEquals(1, callback.invocations);
        Assert.assertEquals(exception, callback.exception);
        Assert.assertNull(callback.metadata);
        try {
            batch.abort(new KafkaException());
            Assert.fail("Expected exception from abort");
        } catch (IllegalStateException e) {
            // expected
        }
        Assert.assertEquals(1, callback.invocations);
        Assert.assertTrue(future.isDone());
        try {
            future.get();
            Assert.fail("Future should have thrown");
        } catch (ExecutionException e) {
            Assert.assertEquals(exception, e.getCause());
        }
    }

    @Test
    public void testBatchCannotCompleteTwice() throws Exception {
        ProducerBatch batch = new ProducerBatch(new TopicPartition("topic", 1), memoryRecordsBuilder, now);
        ProducerBatchTest.MockCallback callback = new ProducerBatchTest.MockCallback();
        FutureRecordMetadata future = batch.tryAppend(now, null, new byte[10], EMPTY_HEADERS, callback, now);
        batch.done(500L, 10L, null);
        Assert.assertEquals(1, callback.invocations);
        Assert.assertNull(callback.exception);
        Assert.assertNotNull(callback.metadata);
        try {
            batch.done(1000L, 20L, null);
            Assert.fail("Expected exception from done");
        } catch (IllegalStateException e) {
            // expected
        }
        RecordMetadata recordMetadata = future.get();
        Assert.assertEquals(500L, recordMetadata.offset());
        Assert.assertEquals(10L, recordMetadata.timestamp());
    }

    @Test
    public void testAppendedChecksumMagicV0AndV1() {
        for (byte magic : Arrays.asList(RecordBatch.MAGIC_VALUE_V0, RecordBatch.MAGIC_VALUE_V1)) {
            MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(128), magic, NONE, CREATE_TIME, 0L);
            ProducerBatch batch = new ProducerBatch(new TopicPartition("topic", 1), builder, now);
            byte[] key = "hi".getBytes();
            byte[] value = "there".getBytes();
            FutureRecordMetadata future = batch.tryAppend(now, key, value, EMPTY_HEADERS, null, now);
            Assert.assertNotNull(future);
            byte attributes = LegacyRecord.computeAttributes(magic, NONE, CREATE_TIME);
            long expectedChecksum = LegacyRecord.computeChecksum(magic, attributes, now, key, value);
            Assert.assertEquals(expectedChecksum, future.checksumOrNull().longValue());
        }
    }

    @Test
    public void testSplitPreservesHeaders() {
        for (CompressionType compressionType : CompressionType.values()) {
            MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), RecordBatch.MAGIC_VALUE_V2, compressionType, CREATE_TIME, 0L);
            ProducerBatch batch = new ProducerBatch(new TopicPartition("topic", 1), builder, now);
            Header header = new RecordHeader("header-key", "header-value".getBytes());
            while (true) {
                FutureRecordMetadata future = batch.tryAppend(now, "hi".getBytes(), "there".getBytes(), new Header[]{ header }, null, now);
                if (future == null) {
                    break;
                }
            } 
            Deque<ProducerBatch> batches = batch.split(200);
            Assert.assertTrue("This batch should be split to multiple small batches.", ((batches.size()) >= 2));
            for (ProducerBatch splitProducerBatch : batches) {
                for (RecordBatch splitBatch : splitProducerBatch.records().batches()) {
                    for (Record record : splitBatch) {
                        Assert.assertTrue("Header size should be 1.", ((record.headers().length) == 1));
                        Assert.assertTrue("Header key should be 'header-key'.", record.headers()[0].key().equals("header-key"));
                        Assert.assertTrue("Header value should be 'header-value'.", new String(record.headers()[0].value()).equals("header-value"));
                    }
                }
            }
        }
    }

    @Test
    public void testSplitPreservesMagicAndCompressionType() {
        for (byte magic : Arrays.asList(RecordBatch.MAGIC_VALUE_V0, RecordBatch.MAGIC_VALUE_V1, RecordBatch.MAGIC_VALUE_V2)) {
            for (CompressionType compressionType : CompressionType.values()) {
                if ((compressionType == (CompressionType.NONE)) && (magic < (RecordBatch.MAGIC_VALUE_V2)))
                    continue;

                if ((compressionType == (CompressionType.ZSTD)) && (magic < (RecordBatch.MAGIC_VALUE_V2)))
                    continue;

                MemoryRecordsBuilder builder = MemoryRecords.builder(ByteBuffer.allocate(1024), magic, compressionType, CREATE_TIME, 0L);
                ProducerBatch batch = new ProducerBatch(new TopicPartition("topic", 1), builder, now);
                while (true) {
                    FutureRecordMetadata future = batch.tryAppend(now, "hi".getBytes(), "there".getBytes(), EMPTY_HEADERS, null, now);
                    if (future == null)
                        break;

                } 
                Deque<ProducerBatch> batches = batch.split(512);
                Assert.assertTrue(((batches.size()) >= 2));
                for (ProducerBatch splitProducerBatch : batches) {
                    Assert.assertEquals(magic, splitProducerBatch.magic());
                    Assert.assertTrue(splitProducerBatch.isSplitBatch());
                    for (RecordBatch splitBatch : splitProducerBatch.records().batches()) {
                        Assert.assertEquals(magic, splitBatch.magic());
                        Assert.assertEquals(0L, splitBatch.baseOffset());
                        Assert.assertEquals(compressionType, splitBatch.compressionType());
                    }
                }
            }
        }
    }

    /**
     * A {@link ProducerBatch} configured using a timestamp preceding its create time is interpreted correctly
     * as not expired by {@link ProducerBatch#hasReachedDeliveryTimeout(long, long)}.
     */
    @Test
    public void testBatchExpiration() {
        long deliveryTimeoutMs = 10240;
        ProducerBatch batch = new ProducerBatch(new TopicPartition("topic", 1), memoryRecordsBuilder, now);
        // Set `now` to 2ms before the create time.
        Assert.assertFalse(batch.hasReachedDeliveryTimeout(deliveryTimeoutMs, ((now) - 2)));
        // Set `now` to deliveryTimeoutMs.
        Assert.assertTrue(batch.hasReachedDeliveryTimeout(deliveryTimeoutMs, ((now) + deliveryTimeoutMs)));
    }

    /**
     * A {@link ProducerBatch} configured using a timestamp preceding its create time is interpreted correctly
     * * as not expired by {@link ProducerBatch#hasReachedDeliveryTimeout(long, long)}.
     */
    @Test
    public void testBatchExpirationAfterReenqueue() {
        ProducerBatch batch = new ProducerBatch(new TopicPartition("topic", 1), memoryRecordsBuilder, now);
        // Set batch.retry = true
        batch.reenqueued(now);
        // Set `now` to 2ms before the create time.
        Assert.assertFalse(batch.hasReachedDeliveryTimeout(10240, ((now) - 2L)));
    }

    @Test
    public void testShouldNotAttemptAppendOnceRecordsBuilderIsClosedForAppends() {
        ProducerBatch batch = new ProducerBatch(new TopicPartition("topic", 1), memoryRecordsBuilder, now);
        FutureRecordMetadata result0 = batch.tryAppend(now, null, new byte[10], EMPTY_HEADERS, null, now);
        Assert.assertNotNull(result0);
        Assert.assertTrue(memoryRecordsBuilder.hasRoomFor(now, null, new byte[10], EMPTY_HEADERS));
        memoryRecordsBuilder.closeForRecordAppends();
        Assert.assertFalse(memoryRecordsBuilder.hasRoomFor(now, null, new byte[10], EMPTY_HEADERS));
        Assert.assertEquals(null, batch.tryAppend(((now) + 1), null, new byte[10], EMPTY_HEADERS, null, ((now) + 1)));
    }

    private static class MockCallback implements Callback {
        private int invocations = 0;

        private RecordMetadata metadata;

        private Exception exception;

        @Override
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            (invocations)++;
            this.metadata = metadata;
            this.exception = exception;
        }
    }
}

