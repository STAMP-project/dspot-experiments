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


import ConsumerRecord.NO_TIMESTAMP;
import ConsumerRecord.NULL_CHECKSUM;
import ConsumerRecord.NULL_SIZE;
import TimestampType.NO_TIMESTAMP_TYPE;
import java.util.Optional;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.record.DefaultRecord;
import org.apache.kafka.common.record.TimestampType;
import org.junit.Assert;
import org.junit.Test;


public class ConsumerRecordTest {
    @Test
    @SuppressWarnings("deprecation")
    public void testOldConstructor() {
        String topic = "topic";
        int partition = 0;
        long offset = 23;
        String key = "key";
        String value = "value";
        ConsumerRecord<String, String> record = new ConsumerRecord(topic, partition, offset, key, value);
        Assert.assertEquals(topic, record.topic());
        Assert.assertEquals(partition, record.partition());
        Assert.assertEquals(offset, record.offset());
        Assert.assertEquals(key, record.key());
        Assert.assertEquals(value, record.value());
        Assert.assertEquals(NO_TIMESTAMP_TYPE, record.timestampType());
        Assert.assertEquals(NO_TIMESTAMP, record.timestamp());
        Assert.assertEquals(NULL_CHECKSUM, record.checksum());
        Assert.assertEquals(NULL_SIZE, record.serializedKeySize());
        Assert.assertEquals(NULL_SIZE, record.serializedValueSize());
        Assert.assertEquals(Optional.empty(), record.leaderEpoch());
        Assert.assertEquals(new RecordHeaders(), record.headers());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testNullChecksumInConstructor() {
        String key = "key";
        String value = "value";
        long timestamp = 242341324L;
        ConsumerRecord<String, String> record = new ConsumerRecord("topic", 0, 23L, timestamp, TimestampType.CREATE_TIME, null, key.length(), value.length(), key, value, new RecordHeaders());
        Assert.assertEquals(DefaultRecord.computePartialChecksum(timestamp, key.length(), value.length()), record.checksum());
    }
}

