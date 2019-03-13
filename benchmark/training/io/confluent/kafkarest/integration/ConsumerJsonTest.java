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


import EmbeddedFormat.JSON;
import Versions.KAFKA_V1_JSON_JSON;
import io.confluent.kafkarest.entities.JsonConsumerRecord;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.core.GenericType;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;


public class ConsumerJsonTest extends AbstractConsumerTest {
    private static final String topicName = "topic1";

    private static final String groupName = "testconsumergroup";

    private final List<ProducerRecord<Object, Object>> recordsWithKeys = Arrays.asList(new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, "key", "value"), new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, "key", null), new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, "key", 43.2), new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, "key", 999), new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, "key", exampleMapValue()), new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, "key", exampleListValue()));

    private final List<ProducerRecord<Object, Object>> recordsOnlyValues = Arrays.asList(new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, "value"), new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, null), new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, 43.2), new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, 999), new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, exampleMapValue()), new ProducerRecord<Object, Object>(ConsumerJsonTest.topicName, exampleListValue()));

    private static final GenericType<List<JsonConsumerRecord>> jsonConsumerRecordType = new GenericType<List<JsonConsumerRecord>>() {};

    @Test
    public void testConsumeWithKeys() {
        String instanceUri = startConsumeMessages(ConsumerJsonTest.groupName, ConsumerJsonTest.topicName, JSON, KAFKA_V1_JSON_JSON);
        produceJsonMessages(recordsWithKeys);
        consumeMessages(instanceUri, ConsumerJsonTest.topicName, recordsWithKeys, KAFKA_V1_JSON_JSON, KAFKA_V1_JSON_JSON, ConsumerJsonTest.jsonConsumerRecordType, null);
        commitOffsets(instanceUri);
    }

    @Test
    public void testConsumeOnlyValues() {
        String instanceUri = startConsumeMessages(ConsumerJsonTest.groupName, ConsumerJsonTest.topicName, JSON, KAFKA_V1_JSON_JSON);
        produceJsonMessages(recordsOnlyValues);
        consumeMessages(instanceUri, ConsumerJsonTest.topicName, recordsOnlyValues, KAFKA_V1_JSON_JSON, KAFKA_V1_JSON_JSON, ConsumerJsonTest.jsonConsumerRecordType, null);
        commitOffsets(instanceUri);
    }
}

