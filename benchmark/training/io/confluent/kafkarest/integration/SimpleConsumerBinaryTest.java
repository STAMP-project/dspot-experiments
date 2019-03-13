/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.kafkarest.integration;


import Errors.PARTITION_NOT_FOUND_ERROR_CODE;
import Errors.PARTITION_NOT_FOUND_MESSAGE;
import Errors.TOPIC_NOT_FOUND_ERROR_CODE;
import Errors.TOPIC_NOT_FOUND_MESSAGE;
import Response.Status.NOT_FOUND;
import Versions.KAFKA_V1_JSON_BINARY;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.entities.BinaryConsumerRecord;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;


public class SimpleConsumerBinaryTest extends AbstractConsumerTest {
    private static final String topicName = "topic1";

    private final List<ProducerRecord<byte[], byte[]>> recordsOnlyValues = Arrays.asList(new ProducerRecord<byte[], byte[]>(SimpleConsumerBinaryTest.topicName, "value".getBytes()), new ProducerRecord<byte[], byte[]>(SimpleConsumerBinaryTest.topicName, "value2".getBytes()), new ProducerRecord<byte[], byte[]>(SimpleConsumerBinaryTest.topicName, "value3".getBytes()), new ProducerRecord<byte[], byte[]>(SimpleConsumerBinaryTest.topicName, "value4".getBytes()));

    private final List<ProducerRecord<byte[], byte[]>> recordsWithKeys = Arrays.asList(new ProducerRecord<byte[], byte[]>(SimpleConsumerBinaryTest.topicName, "key".getBytes(), "value".getBytes()), new ProducerRecord<byte[], byte[]>(SimpleConsumerBinaryTest.topicName, "key".getBytes(), "value2".getBytes()), new ProducerRecord<byte[], byte[]>(SimpleConsumerBinaryTest.topicName, "key".getBytes(), "value3".getBytes()), new ProducerRecord<byte[], byte[]>(SimpleConsumerBinaryTest.topicName, "key".getBytes(), "value4".getBytes()));

    private static final GenericType<List<BinaryConsumerRecord>> binaryConsumerRecordType = new GenericType<List<BinaryConsumerRecord>>() {};

    @Test
    public void testConsumeOnlyValuesByOffset() {
        produceBinaryMessages(recordsOnlyValues);
        // No "count" parameter in the query
        // We expect only the first record in the response
        simpleConsumeMessages(SimpleConsumerBinaryTest.topicName, 0, null, recordsOnlyValues.subList(0, 1), KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, SimpleConsumerBinaryTest.binaryConsumerRecordType, null);
    }

    @Test
    public void testConsumeWithKeysByOffset() {
        produceBinaryMessages(recordsWithKeys);
        // No "count" parameter in the query
        simpleConsumeMessages(SimpleConsumerBinaryTest.topicName, 0, null, recordsWithKeys.subList(0, 1), KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, SimpleConsumerBinaryTest.binaryConsumerRecordType, null);
    }

    @Test
    public void testConsumeOnlyValuesByOffsetAndCount() {
        produceBinaryMessages(recordsOnlyValues);
        simpleConsumeMessages(SimpleConsumerBinaryTest.topicName, 0, recordsOnlyValues.size(), recordsOnlyValues, KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, SimpleConsumerBinaryTest.binaryConsumerRecordType, null);
    }

    @Test
    public void testConsumeWithKeysByOffsetAndCount() {
        produceBinaryMessages(recordsWithKeys);
        simpleConsumeMessages(SimpleConsumerBinaryTest.topicName, 0, recordsWithKeys.size(), recordsWithKeys, KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, SimpleConsumerBinaryTest.binaryConsumerRecordType, null);
    }

    @Test(timeout = 10000)
    public void testConsumeMoreMessagesThanAvailable() {
        produceBinaryMessages(recordsOnlyValues);
        // Ask for more than there is
        simpleConsumeMessages(SimpleConsumerBinaryTest.topicName, 0, ((recordsOnlyValues.size()) + 1), recordsOnlyValues, KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, SimpleConsumerBinaryTest.binaryConsumerRecordType, null);
    }

    @Test
    public void testConsumeInvalidTopic() {
        Response response = request("/topics/nonexistenttopic/partitions/0/messages", Collections.singletonMap("offset", "0")).accept(KAFKA_V1_JSON_BINARY).get();
        TestUtils.assertErrorResponse(NOT_FOUND, response, TOPIC_NOT_FOUND_ERROR_CODE, TOPIC_NOT_FOUND_MESSAGE, KAFKA_V1_JSON_BINARY);
    }

    @Test
    public void testConsumeInvalidPartition() {
        Response response = request("/topics/topic1/partitions/1/messages", Collections.singletonMap("offset", "0")).accept(KAFKA_V1_JSON_BINARY).get();
        TestUtils.assertErrorResponse(NOT_FOUND, response, PARTITION_NOT_FOUND_ERROR_CODE, PARTITION_NOT_FOUND_MESSAGE, KAFKA_V1_JSON_BINARY);
    }
}

