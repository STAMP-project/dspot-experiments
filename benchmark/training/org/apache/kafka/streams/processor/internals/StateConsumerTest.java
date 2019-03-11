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


import GlobalStreamThread.StateConsumer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Utils;
import org.junit.Assert;
import org.junit.Test;


public class StateConsumerTest {
    private static final long FLUSH_INTERVAL = 1000L;

    private final TopicPartition topicOne = new TopicPartition("topic-one", 1);

    private final TopicPartition topicTwo = new TopicPartition("topic-two", 1);

    private final MockTime time = new MockTime();

    private final MockConsumer<byte[], byte[]> consumer = new MockConsumer(OffsetResetStrategy.EARLIEST);

    private final Map<TopicPartition, Long> partitionOffsets = new HashMap<>();

    private final LogContext logContext = new LogContext("test ");

    private StateConsumer stateConsumer;

    private StateConsumerTest.StateMaintainerStub stateMaintainer;

    @Test
    public void shouldAssignPartitionsToConsumer() {
        stateConsumer.initialize();
        Assert.assertEquals(Utils.mkSet(topicOne, topicTwo), consumer.assignment());
    }

    @Test
    public void shouldSeekToInitialOffsets() {
        stateConsumer.initialize();
        Assert.assertEquals(20L, consumer.position(topicOne));
        Assert.assertEquals(30L, consumer.position(topicTwo));
    }

    @Test
    public void shouldUpdateStateWithReceivedRecordsForPartition() {
        stateConsumer.initialize();
        consumer.addRecord(new ConsumerRecord("topic-one", 1, 20L, new byte[0], new byte[0]));
        consumer.addRecord(new ConsumerRecord("topic-one", 1, 21L, new byte[0], new byte[0]));
        stateConsumer.pollAndUpdate();
        Assert.assertEquals(2, stateMaintainer.updatedPartitions.get(topicOne).intValue());
    }

    @Test
    public void shouldUpdateStateWithReceivedRecordsForAllTopicPartition() {
        stateConsumer.initialize();
        consumer.addRecord(new ConsumerRecord("topic-one", 1, 20L, new byte[0], new byte[0]));
        consumer.addRecord(new ConsumerRecord("topic-two", 1, 31L, new byte[0], new byte[0]));
        consumer.addRecord(new ConsumerRecord("topic-two", 1, 32L, new byte[0], new byte[0]));
        stateConsumer.pollAndUpdate();
        Assert.assertEquals(1, stateMaintainer.updatedPartitions.get(topicOne).intValue());
        Assert.assertEquals(2, stateMaintainer.updatedPartitions.get(topicTwo).intValue());
    }

    @Test
    public void shouldFlushStoreWhenFlushIntervalHasLapsed() {
        stateConsumer.initialize();
        consumer.addRecord(new ConsumerRecord("topic-one", 1, 20L, new byte[0], new byte[0]));
        time.sleep(StateConsumerTest.FLUSH_INTERVAL);
        stateConsumer.pollAndUpdate();
        Assert.assertTrue(stateMaintainer.flushed);
    }

    @Test
    public void shouldNotFlushOffsetsWhenFlushIntervalHasNotLapsed() {
        stateConsumer.initialize();
        consumer.addRecord(new ConsumerRecord("topic-one", 1, 20L, new byte[0], new byte[0]));
        time.sleep(((StateConsumerTest.FLUSH_INTERVAL) / 2));
        stateConsumer.pollAndUpdate();
        Assert.assertFalse(stateMaintainer.flushed);
    }

    @Test
    public void shouldCloseConsumer() throws IOException {
        stateConsumer.close();
        Assert.assertTrue(consumer.closed());
    }

    @Test
    public void shouldCloseStateMaintainer() throws IOException {
        stateConsumer.close();
        Assert.assertTrue(stateMaintainer.closed);
    }

    private static class StateMaintainerStub implements GlobalStateMaintainer {
        private final Map<TopicPartition, Long> partitionOffsets;

        private final Map<TopicPartition, Integer> updatedPartitions = new HashMap<>();

        private boolean flushed;

        private boolean closed;

        StateMaintainerStub(final Map<TopicPartition, Long> partitionOffsets) {
            this.partitionOffsets = partitionOffsets;
        }

        @Override
        public Map<TopicPartition, Long> initialize() {
            return partitionOffsets;
        }

        public void flushState() {
            flushed = true;
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public void update(final ConsumerRecord<byte[], byte[]> record) {
            final TopicPartition tp = new TopicPartition(record.topic(), record.partition());
            if (!(updatedPartitions.containsKey(tp))) {
                updatedPartitions.put(tp, 0);
            }
            updatedPartitions.put(tp, ((updatedPartitions.get(tp)) + 1));
        }
    }
}

