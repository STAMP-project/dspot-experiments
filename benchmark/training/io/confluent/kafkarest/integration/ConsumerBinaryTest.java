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


import ConstraintViolationExceptionMapper.UNPROCESSABLE_ENTITY;
import EmbeddedFormat.BINARY;
import Errors.CONSUMER_ALREADY_EXISTS_ERROR_CODE;
import Errors.CONSUMER_ALREADY_EXISTS_MESSAGE;
import Errors.INVALID_CONSUMER_CONFIG_ERROR_CODE;
import Errors.INVALID_CONSUMER_CONFIG_MESSAGE;
import Response.Status.CONFLICT;
import Versions.ANYTHING;
import Versions.KAFKA_MOST_SPECIFIC_DEFAULT;
import Versions.KAFKA_V1_JSON;
import Versions.KAFKA_V1_JSON_BINARY;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.entities.BinaryConsumerRecord;
import io.confluent.kafkarest.entities.ConsumerInstanceConfig;
import io.confluent.kafkarest.entities.Partition;
import io.confluent.kafkarest.entities.PartitionReplica;
import io.confluent.kafkarest.entities.Topic;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;


public class ConsumerBinaryTest extends AbstractConsumerTest {
    private static final String topicName = "topic1";

    private static final List<Partition> partitions = Arrays.asList(new Partition(0, 0, Arrays.asList(new PartitionReplica(0, true, true), new PartitionReplica(1, false, false))));

    private static final Topic topic = new Topic(ConsumerBinaryTest.topicName, new Properties(), ConsumerBinaryTest.partitions);

    private static final String groupName = "testconsumergroup";

    private final List<ProducerRecord<byte[], byte[]>> recordsOnlyValues = Arrays.asList(new ProducerRecord<byte[], byte[]>(ConsumerBinaryTest.topicName, "value".getBytes()), new ProducerRecord<byte[], byte[]>(ConsumerBinaryTest.topicName, "value2".getBytes()), new ProducerRecord<byte[], byte[]>(ConsumerBinaryTest.topicName, "value3".getBytes()), new ProducerRecord<byte[], byte[]>(ConsumerBinaryTest.topicName, "value4".getBytes()));

    private final List<ProducerRecord<byte[], byte[]>> recordsWithKeys = Arrays.asList(new ProducerRecord<byte[], byte[]>(ConsumerBinaryTest.topicName, "key".getBytes(), "value".getBytes()), new ProducerRecord<byte[], byte[]>(ConsumerBinaryTest.topicName, "key".getBytes(), "value2".getBytes()), new ProducerRecord<byte[], byte[]>(ConsumerBinaryTest.topicName, "key".getBytes(), "value3".getBytes()), new ProducerRecord<byte[], byte[]>(ConsumerBinaryTest.topicName, "key".getBytes(), "value4".getBytes()));

    private static final GenericType<List<BinaryConsumerRecord>> binaryConsumerRecordType = new GenericType<List<BinaryConsumerRecord>>() {};

    @Test
    public void testConsumeOnlyValues() {
        // Between these tests we either leave the config null or request the binary embedded format
        // so we can test that both will result in binary consumers. We also us varying accept
        // parameters to test that we default to Binary for various values.
        String instanceUri = startConsumeMessages(ConsumerBinaryTest.groupName, ConsumerBinaryTest.topicName, null, KAFKA_V1_JSON_BINARY);
        produceBinaryMessages(recordsOnlyValues);
        consumeMessages(instanceUri, ConsumerBinaryTest.topicName, recordsOnlyValues, KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, ConsumerBinaryTest.binaryConsumerRecordType, null);
        commitOffsets(instanceUri);
    }

    @Test
    public void testConsumeWithKeys() {
        String instanceUri = startConsumeMessages(ConsumerBinaryTest.groupName, ConsumerBinaryTest.topicName, BINARY, KAFKA_V1_JSON_BINARY);
        produceBinaryMessages(recordsWithKeys);
        consumeMessages(instanceUri, ConsumerBinaryTest.topicName, recordsWithKeys, KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, ConsumerBinaryTest.binaryConsumerRecordType, null);
        commitOffsets(instanceUri);
    }

    @Test
    public void testConsumeWithAcceptAllHeader() {
        // This test ensures that Accept: */* defaults to binary
        String instanceUri = startConsumeMessages(ConsumerBinaryTest.groupName, ConsumerBinaryTest.topicName, BINARY, KAFKA_V1_JSON_BINARY);
        produceBinaryMessages(recordsWithKeys);
        consumeMessages(instanceUri, ConsumerBinaryTest.topicName, recordsWithKeys, ANYTHING, KAFKA_V1_JSON_BINARY, ConsumerBinaryTest.binaryConsumerRecordType, null);
        commitOffsets(instanceUri);
    }

    @Test
    public void testConsumeInvalidTopic() {
        startConsumeMessages(ConsumerBinaryTest.groupName, "nonexistenttopic", null, KAFKA_V1_JSON_BINARY, true);
    }

    @Test
    public void testConsumeTimeout() {
        String instanceUri = startConsumeMessages(ConsumerBinaryTest.groupName, ConsumerBinaryTest.topicName, BINARY, KAFKA_V1_JSON_BINARY);
        produceBinaryMessages(recordsWithKeys);
        consumeMessages(instanceUri, ConsumerBinaryTest.topicName, recordsWithKeys, KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, ConsumerBinaryTest.binaryConsumerRecordType, null);
        consumeForTimeout(instanceUri, ConsumerBinaryTest.topicName, KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, ConsumerBinaryTest.binaryConsumerRecordType);
    }

    @Test
    public void testDeleteConsumer() {
        String instanceUri = startConsumeMessages(ConsumerBinaryTest.groupName, ConsumerBinaryTest.topicName, null, KAFKA_V1_JSON_BINARY);
        produceBinaryMessages(recordsWithKeys);
        consumeMessages(instanceUri, ConsumerBinaryTest.topicName, recordsWithKeys, KAFKA_V1_JSON_BINARY, KAFKA_V1_JSON_BINARY, ConsumerBinaryTest.binaryConsumerRecordType, null);
        deleteConsumer(instanceUri);
    }

    // The following tests are only included in the binary consumer because they test functionality
    // that isn't specific to the type of embedded data, but since they need
    @Test
    public void testInvalidKafkaConsumerConfig() {
        ConsumerInstanceConfig config = new ConsumerInstanceConfig("id", "name", "binary", "bad-config", null, null, null);
        Response response = request(("/consumers/" + (ConsumerBinaryTest.groupName))).post(Entity.entity(config, KAFKA_V1_JSON));
        TestUtils.assertErrorResponse(UNPROCESSABLE_ENTITY, response, INVALID_CONSUMER_CONFIG_ERROR_CODE, INVALID_CONSUMER_CONFIG_MESSAGE, KAFKA_V1_JSON);
    }

    @Test
    public void testDuplicateConsumerID() {
        String instanceUrl = startConsumeMessages(ConsumerBinaryTest.groupName, ConsumerBinaryTest.topicName, null, KAFKA_V1_JSON_BINARY);
        produceBinaryMessages(recordsWithKeys);
        // Duplicate the same instance, which should cause a conflict
        String name = consumerNameFromInstanceUrl(instanceUrl);
        Response createResponse = createConsumerInstance(ConsumerBinaryTest.groupName, null, name, null);
        TestUtils.assertErrorResponse(CONFLICT, createResponse, CONSUMER_ALREADY_EXISTS_ERROR_CODE, CONSUMER_ALREADY_EXISTS_MESSAGE, KAFKA_MOST_SPECIFIC_DEFAULT);
    }
}

