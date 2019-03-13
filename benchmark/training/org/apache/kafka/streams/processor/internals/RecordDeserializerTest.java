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


import TimestampType.CREATE_TIME;
import java.util.Collections;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.common.utils.LogContext;
import org.junit.Assert;
import org.junit.Test;


public class RecordDeserializerTest {
    private final RecordHeaders headers = new RecordHeaders(new Header[]{ new RecordHeader("key", "value".getBytes()) });

    private final ConsumerRecord<byte[], byte[]> rawRecord = new ConsumerRecord("topic", 1, 1, 10, TimestampType.LOG_APPEND_TIME, 5L, 3, 5, new byte[0], new byte[0], headers);

    @SuppressWarnings("deprecation")
    @Test
    public void shouldReturnConsumerRecordWithDeserializedValueWhenNoExceptions() {
        final RecordDeserializer recordDeserializer = new RecordDeserializer(new RecordDeserializerTest.TheSourceNode(false, false, "key", "value"), null, new LogContext(), new Metrics().sensor("skipped-records"));
        final ConsumerRecord<Object, Object> record = recordDeserializer.deserialize(null, rawRecord);
        Assert.assertEquals(rawRecord.topic(), record.topic());
        Assert.assertEquals(rawRecord.partition(), record.partition());
        Assert.assertEquals(rawRecord.offset(), record.offset());
        Assert.assertEquals(rawRecord.checksum(), record.checksum());
        Assert.assertEquals("key", record.key());
        Assert.assertEquals("value", record.value());
        Assert.assertEquals(rawRecord.timestamp(), record.timestamp());
        Assert.assertEquals(CREATE_TIME, record.timestampType());
        Assert.assertEquals(rawRecord.headers(), record.headers());
    }

    static class TheSourceNode extends SourceNode<Object, Object> {
        private final boolean keyThrowsException;

        private final boolean valueThrowsException;

        private final Object key;

        private final Object value;

        TheSourceNode(final boolean keyThrowsException, final boolean valueThrowsException, final Object key, final Object value) {
            super("", Collections.emptyList(), null, null);
            this.keyThrowsException = keyThrowsException;
            this.valueThrowsException = valueThrowsException;
            this.key = key;
            this.value = value;
        }

        @Override
        public Object deserializeKey(final String topic, final Headers headers, final byte[] data) {
            if (keyThrowsException) {
                throw new RuntimeException();
            }
            return key;
        }

        @Override
        public Object deserializeValue(final String topic, final Headers headers, final byte[] data) {
            if (valueThrowsException) {
                throw new RuntimeException();
            }
            return value;
        }
    }
}

