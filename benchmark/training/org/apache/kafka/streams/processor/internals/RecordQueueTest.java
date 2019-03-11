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
package org.apache.kafka.streams.processor.internals;


import RecordQueue.UNKNOWN;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.metrics.Sensor;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.streams.errors.DefaultProductionExceptionHandler;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.errors.LogAndFailExceptionHandler;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.processor.FailOnInvalidTimestamp;
import org.apache.kafka.streams.processor.LogAndSkipOnInvalidTimestamp;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.apache.kafka.streams.state.StateSerdes;
import org.apache.kafka.test.InternalMockProcessorContext;
import org.apache.kafka.test.MockSourceNode;
import org.apache.kafka.test.MockTimestampExtractor;
import org.junit.Assert;
import org.junit.Test;


public class RecordQueueTest {
    private final Serializer<Integer> intSerializer = new IntegerSerializer();

    private final Deserializer<Integer> intDeserializer = new IntegerDeserializer();

    private final TimestampExtractor timestampExtractor = new MockTimestampExtractor();

    private final String[] topics = new String[]{ "topic" };

    private final Sensor skippedRecordsSensor = new Metrics().sensor("skipped-records");

    final InternalMockProcessorContext context = new InternalMockProcessorContext(StateSerdes.withBuiltinTypes("anyName", Bytes.class, Bytes.class), new RecordCollectorImpl(null, new LogContext("record-queue-test "), new DefaultProductionExceptionHandler(), skippedRecordsSensor));

    private final MockSourceNode mockSourceNodeWithMetrics = new MockSourceNode(topics, intDeserializer, intDeserializer);

    private final RecordQueue queue = new RecordQueue(new TopicPartition(topics[0], 1), mockSourceNodeWithMetrics, timestampExtractor, new LogAndFailExceptionHandler(), context, new LogContext());

    private final RecordQueue queueThatSkipsDeserializeErrors = new RecordQueue(new TopicPartition(topics[0], 1), mockSourceNodeWithMetrics, timestampExtractor, new LogAndContinueExceptionHandler(), context, new LogContext());

    private final byte[] recordValue = intSerializer.serialize(null, 10);

    private final byte[] recordKey = intSerializer.serialize(null, 1);

    @Test
    public void testTimeTracking() {
        Assert.assertTrue(queue.isEmpty());
        Assert.assertEquals(0, queue.size());
        Assert.assertEquals(UNKNOWN, queue.timestamp());
        // add three 3 out-of-order records with timestamp 2, 1, 3
        final List<ConsumerRecord<byte[], byte[]>> list1 = Arrays.asList(new ConsumerRecord("topic", 1, 2, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue), new ConsumerRecord("topic", 1, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue), new ConsumerRecord("topic", 1, 3, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue));
        queue.addRawRecords(list1);
        Assert.assertEquals(3, queue.size());
        Assert.assertEquals(2L, queue.timestamp());
        // poll the first record, now with 1, 3
        Assert.assertEquals(2L, queue.poll().timestamp);
        Assert.assertEquals(2, queue.size());
        Assert.assertEquals(2L, queue.timestamp());
        // poll the second record, now with 3
        Assert.assertEquals(1L, queue.poll().timestamp);
        Assert.assertEquals(1, queue.size());
        Assert.assertEquals(3L, queue.timestamp());
        // add three 3 out-of-order records with timestamp 4, 1, 2
        // now with 3, 4, 1, 2
        final List<ConsumerRecord<byte[], byte[]>> list2 = Arrays.asList(new ConsumerRecord("topic", 1, 4, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue), new ConsumerRecord("topic", 1, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue), new ConsumerRecord("topic", 1, 2, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue));
        queue.addRawRecords(list2);
        Assert.assertEquals(4, queue.size());
        Assert.assertEquals(3L, queue.timestamp());
        // poll the third record, now with 4, 1, 2
        Assert.assertEquals(3L, queue.poll().timestamp);
        Assert.assertEquals(3, queue.size());
        Assert.assertEquals(4L, queue.timestamp());
        // poll the rest records
        Assert.assertEquals(4L, queue.poll().timestamp);
        Assert.assertEquals(4L, queue.timestamp());
        Assert.assertEquals(1L, queue.poll().timestamp);
        Assert.assertEquals(4L, queue.timestamp());
        Assert.assertEquals(2L, queue.poll().timestamp);
        Assert.assertTrue(queue.isEmpty());
        Assert.assertEquals(0, queue.size());
        Assert.assertEquals(4L, queue.timestamp());
        // add three more records with 4, 5, 6
        final List<ConsumerRecord<byte[], byte[]>> list3 = Arrays.asList(new ConsumerRecord("topic", 1, 4, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue), new ConsumerRecord("topic", 1, 5, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue), new ConsumerRecord("topic", 1, 6, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue));
        queue.addRawRecords(list3);
        Assert.assertEquals(3, queue.size());
        Assert.assertEquals(4L, queue.timestamp());
        // poll one record again, the timestamp should advance now
        Assert.assertEquals(4L, queue.poll().timestamp);
        Assert.assertEquals(2, queue.size());
        Assert.assertEquals(5L, queue.timestamp());
        // clear the queue
        queue.clear();
        Assert.assertTrue(queue.isEmpty());
        Assert.assertEquals(0, queue.size());
        Assert.assertEquals(UNKNOWN, queue.timestamp());
        // re-insert the three records with 4, 5, 6
        queue.addRawRecords(list3);
        Assert.assertEquals(3, queue.size());
        Assert.assertEquals(4L, queue.timestamp());
    }

    @Test(expected = StreamsException.class)
    public void shouldThrowStreamsExceptionWhenKeyDeserializationFails() {
        final byte[] key = Serdes.Long().serializer().serialize("foo", 1L);
        final List<ConsumerRecord<byte[], byte[]>> records = Collections.singletonList(new ConsumerRecord("topic", 1, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, key, recordValue));
        queue.addRawRecords(records);
    }

    @Test(expected = StreamsException.class)
    public void shouldThrowStreamsExceptionWhenValueDeserializationFails() {
        final byte[] value = Serdes.Long().serializer().serialize("foo", 1L);
        final List<ConsumerRecord<byte[], byte[]>> records = Collections.singletonList(new ConsumerRecord("topic", 1, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, value));
        queue.addRawRecords(records);
    }

    @Test
    public void shouldNotThrowStreamsExceptionWhenKeyDeserializationFailsWithSkipHandler() {
        final byte[] key = Serdes.Long().serializer().serialize("foo", 1L);
        final List<ConsumerRecord<byte[], byte[]>> records = Collections.singletonList(new ConsumerRecord("topic", 1, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, key, recordValue));
        queueThatSkipsDeserializeErrors.addRawRecords(records);
        Assert.assertEquals(0, queueThatSkipsDeserializeErrors.size());
    }

    @Test
    public void shouldNotThrowStreamsExceptionWhenValueDeserializationFailsWithSkipHandler() {
        final byte[] value = Serdes.Long().serializer().serialize("foo", 1L);
        final List<ConsumerRecord<byte[], byte[]>> records = Collections.singletonList(new ConsumerRecord("topic", 1, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, value));
        queueThatSkipsDeserializeErrors.addRawRecords(records);
        Assert.assertEquals(0, queueThatSkipsDeserializeErrors.size());
    }

    @Test(expected = StreamsException.class)
    public void shouldThrowOnNegativeTimestamp() {
        final List<ConsumerRecord<byte[], byte[]>> records = Collections.singletonList(new ConsumerRecord("topic", 1, 1, (-1L), TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue));
        final RecordQueue queue = new RecordQueue(new TopicPartition(topics[0], 1), new MockSourceNode(topics, intDeserializer, intDeserializer), new FailOnInvalidTimestamp(), new LogAndContinueExceptionHandler(), new InternalMockProcessorContext(), new LogContext());
        queue.addRawRecords(records);
    }

    @Test
    public void shouldDropOnNegativeTimestamp() {
        final List<ConsumerRecord<byte[], byte[]>> records = Collections.singletonList(new ConsumerRecord("topic", 1, 1, (-1L), TimestampType.CREATE_TIME, 0L, 0, 0, recordKey, recordValue));
        final RecordQueue queue = new RecordQueue(new TopicPartition(topics[0], 1), new MockSourceNode(topics, intDeserializer, intDeserializer), new LogAndSkipOnInvalidTimestamp(), new LogAndContinueExceptionHandler(), new InternalMockProcessorContext(), new LogContext());
        queue.addRawRecords(records);
        Assert.assertEquals(0, queue.size());
    }
}

