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
package org.apache.kafka.clients.consumer;


import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.record.TimestampType;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import static OffsetResetStrategy.EARLIEST;


public class MockConsumerTest {
    private MockConsumer<String, String> consumer = new MockConsumer(EARLIEST);

    @Test
    public void testSimpleMock() {
        consumer.subscribe(Collections.singleton("test"));
        Assert.assertEquals(0, consumer.poll(Duration.ZERO).count());
        consumer.rebalance(Arrays.asList(new TopicPartition("test", 0), new TopicPartition("test", 1)));
        // Mock consumers need to seek manually since they cannot automatically reset offsets
        HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
        beginningOffsets.put(new TopicPartition("test", 0), 0L);
        beginningOffsets.put(new TopicPartition("test", 1), 0L);
        consumer.updateBeginningOffsets(beginningOffsets);
        consumer.seek(new TopicPartition("test", 0), 0);
        ConsumerRecord<String, String> rec1 = new ConsumerRecord("test", 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, "key1", "value1");
        ConsumerRecord<String, String> rec2 = new ConsumerRecord("test", 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, "key2", "value2");
        consumer.addRecord(rec1);
        consumer.addRecord(rec2);
        ConsumerRecords<String, String> recs = consumer.poll(Duration.ofMillis(1));
        Iterator<ConsumerRecord<String, String>> iter = recs.iterator();
        Assert.assertEquals(rec1, iter.next());
        Assert.assertEquals(rec2, iter.next());
        Assert.assertFalse(iter.hasNext());
        Assert.assertEquals(2L, consumer.position(new TopicPartition("test", 0)));
        consumer.commitSync();
        Assert.assertEquals(2L, consumer.committed(new TopicPartition("test", 0)).offset());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSimpleMockDeprecated() {
        consumer.subscribe(Collections.singleton("test"));
        Assert.assertEquals(0, consumer.poll(1000).count());
        consumer.rebalance(Arrays.asList(new TopicPartition("test", 0), new TopicPartition("test", 1)));
        // Mock consumers need to seek manually since they cannot automatically reset offsets
        HashMap<TopicPartition, Long> beginningOffsets = new HashMap<>();
        beginningOffsets.put(new TopicPartition("test", 0), 0L);
        beginningOffsets.put(new TopicPartition("test", 1), 0L);
        consumer.updateBeginningOffsets(beginningOffsets);
        consumer.seek(new TopicPartition("test", 0), 0);
        ConsumerRecord<String, String> rec1 = new ConsumerRecord("test", 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, "key1", "value1");
        ConsumerRecord<String, String> rec2 = new ConsumerRecord("test", 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, "key2", "value2");
        consumer.addRecord(rec1);
        consumer.addRecord(rec2);
        ConsumerRecords<String, String> recs = consumer.poll(1);
        Iterator<ConsumerRecord<String, String>> iter = recs.iterator();
        Assert.assertEquals(rec1, iter.next());
        Assert.assertEquals(rec2, iter.next());
        Assert.assertFalse(iter.hasNext());
        Assert.assertEquals(2L, consumer.position(new TopicPartition("test", 0)));
        consumer.commitSync();
        Assert.assertEquals(2L, consumer.committed(new TopicPartition("test", 0)).offset());
    }

    @Test
    public void testConsumerRecordsIsEmptyWhenReturningNoRecords() {
        TopicPartition partition = new TopicPartition("test", 0);
        consumer.assign(Collections.singleton(partition));
        consumer.addRecord(new ConsumerRecord<String, String>("test", 0, 0, null, null));
        consumer.updateEndOffsets(Collections.singletonMap(partition, 1L));
        consumer.seekToEnd(Collections.singleton(partition));
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1));
        MatcherAssert.assertThat(records.count(), CoreMatchers.is(0));
        MatcherAssert.assertThat(records.isEmpty(), CoreMatchers.is(true));
    }
}

