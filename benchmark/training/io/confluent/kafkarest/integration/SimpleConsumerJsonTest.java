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
import Versions.KAFKA_V1_JSON_JSON;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.entities.JsonConsumerRecord;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;


public class SimpleConsumerJsonTest extends AbstractConsumerTest {
    private static final String topicName = "topic1";

    private final List<ProducerRecord<Object, Object>> recordsWithKeys = Arrays.asList(new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, "key", "value"), new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, "key", null), new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, "key", 43.2), new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, "key", 999), new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, "key", exampleMapValue()), new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, "key", exampleListValue()));

    private final List<ProducerRecord<Object, Object>> recordsOnlyValues = Arrays.asList(new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, "value"), new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, null), new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, 43.2), new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, 999), new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, exampleMapValue()), new ProducerRecord<Object, Object>(SimpleConsumerJsonTest.topicName, exampleListValue()));

    private static final GenericType<List<JsonConsumerRecord>> jsonConsumerRecordType = new GenericType<List<JsonConsumerRecord>>() {};

    @Test
    public void testConsumeOnlyValuesByOffset() {
        produceJsonMessages(recordsOnlyValues);
        // No "count" parameter in the query
        // We expect only the first record in the response
        simpleConsumeMessages(SimpleConsumerJsonTest.topicName, 0, null, recordsOnlyValues.subList(0, 1), KAFKA_V1_JSON_JSON, KAFKA_V1_JSON_JSON, SimpleConsumerJsonTest.jsonConsumerRecordType, null);
    }

    @Test
    public void testConsumeOnlyValuesByOffsetAndCount() {
        produceJsonMessages(recordsOnlyValues);
        simpleConsumeMessages(SimpleConsumerJsonTest.topicName, 0, recordsOnlyValues.size(), recordsOnlyValues, KAFKA_V1_JSON_JSON, KAFKA_V1_JSON_JSON, SimpleConsumerJsonTest.jsonConsumerRecordType, null);
    }

    @Test
    public void testConsumeWithKeysByOffsetAndCount() {
        produceJsonMessages(recordsWithKeys);
        simpleConsumeMessages(SimpleConsumerJsonTest.topicName, 0, recordsWithKeys.size(), recordsWithKeys, KAFKA_V1_JSON_JSON, KAFKA_V1_JSON_JSON, SimpleConsumerJsonTest.jsonConsumerRecordType, null);
    }

    @Test(timeout = 10000)
    public void testConsumeMoreMessagesThanAvailable() {
        produceJsonMessages(recordsOnlyValues);
        // Ask for more than there is
        simpleConsumeMessages(SimpleConsumerJsonTest.topicName, 0, ((recordsOnlyValues.size()) + 1), recordsOnlyValues, KAFKA_V1_JSON_JSON, KAFKA_V1_JSON_JSON, SimpleConsumerJsonTest.jsonConsumerRecordType, null);
    }

    @Test
    public void testConsumeInvalidTopic() {
        Response response = request("/topics/nonexistenttopic/partitions/0/messages", Collections.singletonMap("offset", "0")).accept(KAFKA_V1_JSON_JSON).get();
        TestUtils.assertErrorResponse(NOT_FOUND, response, TOPIC_NOT_FOUND_ERROR_CODE, TOPIC_NOT_FOUND_MESSAGE, KAFKA_V1_JSON_JSON);
    }

    @Test
    public void testConsumeInvalidPartition() {
        Response response = request("/topics/topic1/partitions/1/messages", Collections.singletonMap("offset", "0")).accept(KAFKA_V1_JSON_JSON).get();
        TestUtils.assertErrorResponse(NOT_FOUND, response, PARTITION_NOT_FOUND_ERROR_CODE, PARTITION_NOT_FOUND_MESSAGE, KAFKA_V1_JSON_JSON);
    }
}

