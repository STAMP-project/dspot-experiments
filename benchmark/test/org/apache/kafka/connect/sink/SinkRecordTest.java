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
package org.apache.kafka.connect.sink;


import Schema.BOOLEAN_SCHEMA;
import Schema.STRING_SCHEMA;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Values;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.header.Header;
import org.apache.kafka.connect.header.Headers;
import org.junit.Assert;
import org.junit.Test;


public class SinkRecordTest {
    private static final String TOPIC_NAME = "myTopic";

    private static final Integer PARTITION_NUMBER = 0;

    private static final long KAFKA_OFFSET = 0L;

    private static final Long KAFKA_TIMESTAMP = 0L;

    private static final TimestampType TS_TYPE = TimestampType.CREATE_TIME;

    private SinkRecord record;

    @Test
    public void shouldCreateSinkRecordWithHeaders() {
        Headers headers = new ConnectHeaders().addString("h1", "hv1").addBoolean("h2", true);
        record = new SinkRecord(SinkRecordTest.TOPIC_NAME, SinkRecordTest.PARTITION_NUMBER, Schema.STRING_SCHEMA, "key", Schema.BOOLEAN_SCHEMA, false, SinkRecordTest.KAFKA_OFFSET, SinkRecordTest.KAFKA_TIMESTAMP, SinkRecordTest.TS_TYPE, headers);
        Assert.assertNotNull(record.headers());
        Assert.assertSame(headers, record.headers());
        Assert.assertFalse(record.headers().isEmpty());
    }

    @Test
    public void shouldCreateSinkRecordWithEmptyHeaders() {
        Assert.assertEquals(SinkRecordTest.TOPIC_NAME, record.topic());
        Assert.assertEquals(SinkRecordTest.PARTITION_NUMBER, record.kafkaPartition());
        Assert.assertEquals(STRING_SCHEMA, record.keySchema());
        Assert.assertEquals("key", record.key());
        Assert.assertEquals(BOOLEAN_SCHEMA, record.valueSchema());
        Assert.assertEquals(false, record.value());
        Assert.assertEquals(SinkRecordTest.KAFKA_OFFSET, record.kafkaOffset());
        Assert.assertEquals(SinkRecordTest.KAFKA_TIMESTAMP, record.timestamp());
        Assert.assertEquals(SinkRecordTest.TS_TYPE, record.timestampType());
        Assert.assertNotNull(record.headers());
        Assert.assertTrue(record.headers().isEmpty());
    }

    @Test
    public void shouldDuplicateRecordAndCloneHeaders() {
        SinkRecord duplicate = record.newRecord(SinkRecordTest.TOPIC_NAME, SinkRecordTest.PARTITION_NUMBER, STRING_SCHEMA, "key", BOOLEAN_SCHEMA, false, SinkRecordTest.KAFKA_TIMESTAMP);
        Assert.assertEquals(SinkRecordTest.TOPIC_NAME, duplicate.topic());
        Assert.assertEquals(SinkRecordTest.PARTITION_NUMBER, duplicate.kafkaPartition());
        Assert.assertEquals(STRING_SCHEMA, duplicate.keySchema());
        Assert.assertEquals("key", duplicate.key());
        Assert.assertEquals(BOOLEAN_SCHEMA, duplicate.valueSchema());
        Assert.assertEquals(false, duplicate.value());
        Assert.assertEquals(SinkRecordTest.KAFKA_OFFSET, duplicate.kafkaOffset());
        Assert.assertEquals(SinkRecordTest.KAFKA_TIMESTAMP, duplicate.timestamp());
        Assert.assertEquals(SinkRecordTest.TS_TYPE, duplicate.timestampType());
        Assert.assertNotNull(duplicate.headers());
        Assert.assertTrue(duplicate.headers().isEmpty());
        Assert.assertNotSame(record.headers(), duplicate.headers());
        Assert.assertEquals(record.headers(), duplicate.headers());
    }

    @Test
    public void shouldDuplicateRecordUsingNewHeaders() {
        Headers newHeaders = new ConnectHeaders().addString("h3", "hv3");
        SinkRecord duplicate = record.newRecord(SinkRecordTest.TOPIC_NAME, SinkRecordTest.PARTITION_NUMBER, STRING_SCHEMA, "key", BOOLEAN_SCHEMA, false, SinkRecordTest.KAFKA_TIMESTAMP, newHeaders);
        Assert.assertEquals(SinkRecordTest.TOPIC_NAME, duplicate.topic());
        Assert.assertEquals(SinkRecordTest.PARTITION_NUMBER, duplicate.kafkaPartition());
        Assert.assertEquals(STRING_SCHEMA, duplicate.keySchema());
        Assert.assertEquals("key", duplicate.key());
        Assert.assertEquals(BOOLEAN_SCHEMA, duplicate.valueSchema());
        Assert.assertEquals(false, duplicate.value());
        Assert.assertEquals(SinkRecordTest.KAFKA_OFFSET, duplicate.kafkaOffset());
        Assert.assertEquals(SinkRecordTest.KAFKA_TIMESTAMP, duplicate.timestamp());
        Assert.assertEquals(SinkRecordTest.TS_TYPE, duplicate.timestampType());
        Assert.assertNotNull(duplicate.headers());
        Assert.assertEquals(newHeaders, duplicate.headers());
        Assert.assertSame(newHeaders, duplicate.headers());
        Assert.assertNotSame(record.headers(), duplicate.headers());
        Assert.assertNotEquals(record.headers(), duplicate.headers());
    }

    @Test
    public void shouldModifyRecordHeader() {
        Assert.assertTrue(record.headers().isEmpty());
        record.headers().addInt("intHeader", 100);
        Assert.assertEquals(1, record.headers().size());
        Header header = record.headers().lastWithName("intHeader");
        Assert.assertEquals(100, ((int) (Values.convertToInteger(header.schema(), header.value()))));
    }
}

