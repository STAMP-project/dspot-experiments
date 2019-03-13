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


import EmbeddedFormat.AVRO;
import Versions.KAFKA_V1_JSON_AVRO;
import io.confluent.kafkarest.converters.AvroConverter;
import io.confluent.kafkarest.entities.AvroConsumerRecord;
import io.confluent.kafkarest.entities.Partition;
import io.confluent.kafkarest.entities.PartitionReplica;
import io.confluent.kafkarest.entities.Topic;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.ws.rs.core.GenericType;
import org.apache.avro.Schema;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;


public class ConsumerAvroTest extends AbstractConsumerTest {
    private static final String topicName = "topic1";

    private static final List<Partition> partitions = Arrays.asList(new Partition(0, 0, Arrays.asList(new PartitionReplica(0, true, true), new PartitionReplica(1, false, false))));

    private static final Topic topic = new Topic(ConsumerAvroTest.topicName, new Properties(), ConsumerAvroTest.partitions);

    private static final String groupName = "testconsumergroup";

    // Primitive types
    private final List<ProducerRecord<Object, Object>> recordsOnlyValues = Arrays.asList(new ProducerRecord<Object, Object>(ConsumerAvroTest.topicName, 1), new ProducerRecord<Object, Object>(ConsumerAvroTest.topicName, 2), new ProducerRecord<Object, Object>(ConsumerAvroTest.topicName, 3), new ProducerRecord<Object, Object>(ConsumerAvroTest.topicName, 4));

    // And primitive keys w/ record values
    private static final String valueSchemaStr = "{\"type\": \"record\", " + (((("\"name\":\"test\"," + "\"fields\":[{") + "  \"name\":\"field\", ") + "  \"type\": \"int\"") + "}]}");

    private static final Schema valueSchema = new Schema.Parser().parse(ConsumerAvroTest.valueSchemaStr);

    private final List<ProducerRecord<Object, Object>> recordsWithKeys = Arrays.asList(new ProducerRecord<Object, Object>(ConsumerAvroTest.topicName, "key", set("field", 72).build()), new ProducerRecord<Object, Object>(ConsumerAvroTest.topicName, "key", set("field", 73).build()), new ProducerRecord<Object, Object>(ConsumerAvroTest.topicName, "key", set("field", 74).build()), new ProducerRecord<Object, Object>(ConsumerAvroTest.topicName, "key", set("field", 75).build()));

    private static final GenericType<List<AvroConsumerRecord>> avroConsumerRecordType = new GenericType<List<AvroConsumerRecord>>() {};

    private static final AbstractConsumerTest.Converter converter = new AbstractConsumerTest.Converter() {
        @Override
        public Object convert(Object obj) {
            return AvroConverter.toJson(obj).json;
        }
    };

    public ConsumerAvroTest() {
        super(1, true);
    }

    @Test
    public void testConsumeOnlyValues() {
        String instanceUri = startConsumeMessages(ConsumerAvroTest.groupName, ConsumerAvroTest.topicName, AVRO, KAFKA_V1_JSON_AVRO);
        produceAvroMessages(recordsOnlyValues);
        consumeMessages(instanceUri, ConsumerAvroTest.topicName, recordsOnlyValues, KAFKA_V1_JSON_AVRO, KAFKA_V1_JSON_AVRO, ConsumerAvroTest.avroConsumerRecordType, ConsumerAvroTest.converter);
        commitOffsets(instanceUri);
    }

    @Test
    public void testConsumeWithKeys() {
        String instanceUri = startConsumeMessages(ConsumerAvroTest.groupName, ConsumerAvroTest.topicName, AVRO, KAFKA_V1_JSON_AVRO);
        produceAvroMessages(recordsWithKeys);
        consumeMessages(instanceUri, ConsumerAvroTest.topicName, recordsWithKeys, KAFKA_V1_JSON_AVRO, KAFKA_V1_JSON_AVRO, ConsumerAvroTest.avroConsumerRecordType, ConsumerAvroTest.converter);
        commitOffsets(instanceUri);
    }

    @Test
    public void testConsumeInvalidTopic() {
        startConsumeMessages(ConsumerAvroTest.groupName, "nonexistenttopic", AVRO, KAFKA_V1_JSON_AVRO, true);
    }

    @Test
    public void testConsumeTimeout() {
        String instanceUri = startConsumeMessages(ConsumerAvroTest.groupName, ConsumerAvroTest.topicName, AVRO, KAFKA_V1_JSON_AVRO);
        produceAvroMessages(recordsWithKeys);
        consumeMessages(instanceUri, ConsumerAvroTest.topicName, recordsWithKeys, KAFKA_V1_JSON_AVRO, KAFKA_V1_JSON_AVRO, ConsumerAvroTest.avroConsumerRecordType, ConsumerAvroTest.converter);
        consumeForTimeout(instanceUri, ConsumerAvroTest.topicName, KAFKA_V1_JSON_AVRO, KAFKA_V1_JSON_AVRO, ConsumerAvroTest.avroConsumerRecordType);
    }

    @Test
    public void testDeleteConsumer() {
        String instanceUri = startConsumeMessages(ConsumerAvroTest.groupName, ConsumerAvroTest.topicName, AVRO, KAFKA_V1_JSON_AVRO);
        produceAvroMessages(recordsWithKeys);
        consumeMessages(instanceUri, ConsumerAvroTest.topicName, recordsWithKeys, KAFKA_V1_JSON_AVRO, KAFKA_V1_JSON_AVRO, ConsumerAvroTest.avroConsumerRecordType, ConsumerAvroTest.converter);
        deleteConsumer(instanceUri);
    }
}

