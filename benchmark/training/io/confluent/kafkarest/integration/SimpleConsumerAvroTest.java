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
import Versions.KAFKA_V1_JSON_AVRO;
import io.confluent.kafkarest.TestUtils;
import io.confluent.kafkarest.converters.AvroConverter;
import io.confluent.kafkarest.entities.AvroConsumerRecord;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import org.apache.avro.Schema;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;


public class SimpleConsumerAvroTest extends AbstractConsumerTest {
    private static final String topicName = "topic1";

    // Primitive types
    private final List<ProducerRecord<Object, Object>> recordsOnlyValues = Arrays.asList(new ProducerRecord<Object, Object>(SimpleConsumerAvroTest.topicName, 1), new ProducerRecord<Object, Object>(SimpleConsumerAvroTest.topicName, 2), new ProducerRecord<Object, Object>(SimpleConsumerAvroTest.topicName, 3), new ProducerRecord<Object, Object>(SimpleConsumerAvroTest.topicName, 4));

    // And primitive keys w/ record values
    private static final String valueSchemaStr = "{\"type\": \"record\", " + (((("\"name\":\"test\"," + "\"fields\":[{") + "  \"name\":\"field\", ") + "  \"type\": \"int\"") + "}]}");

    private static final Schema valueSchema = new Schema.Parser().parse(SimpleConsumerAvroTest.valueSchemaStr);

    private final List<ProducerRecord<Object, Object>> recordsWithKeys = Arrays.asList(new ProducerRecord<Object, Object>(SimpleConsumerAvroTest.topicName, "key", set("field", 72).build()), new ProducerRecord<Object, Object>(SimpleConsumerAvroTest.topicName, "key", set("field", 73).build()), new ProducerRecord<Object, Object>(SimpleConsumerAvroTest.topicName, "key", set("field", 74).build()), new ProducerRecord<Object, Object>(SimpleConsumerAvroTest.topicName, "key", set("field", 75).build()));

    private static final GenericType<List<AvroConsumerRecord>> avroConsumerRecordType = new GenericType<List<AvroConsumerRecord>>() {};

    private static final AbstractConsumerTest.Converter converter = new AbstractConsumerTest.Converter() {
        @Override
        public Object convert(Object obj) {
            return AvroConverter.toJson(obj).json;
        }
    };

    public SimpleConsumerAvroTest() {
        super(1, true);
    }

    @Test
    public void testConsumeOnlyValuesByOffset() {
        produceAvroMessages(recordsOnlyValues);
        // No "count" parameter in the query
        // We expect only the first record in the response
        simpleConsumeMessages(SimpleConsumerAvroTest.topicName, 0, null, recordsOnlyValues.subList(0, 1), KAFKA_V1_JSON_AVRO, KAFKA_V1_JSON_AVRO, SimpleConsumerAvroTest.avroConsumerRecordType, SimpleConsumerAvroTest.converter);
    }

    @Test
    public void testConsumeWithKeysByOffset() {
        produceAvroMessages(recordsWithKeys);
        // No "count" parameter in the query
        simpleConsumeMessages(SimpleConsumerAvroTest.topicName, 0, null, recordsWithKeys.subList(0, 1), KAFKA_V1_JSON_AVRO, KAFKA_V1_JSON_AVRO, SimpleConsumerAvroTest.avroConsumerRecordType, SimpleConsumerAvroTest.converter);
    }

    @Test
    public void testConsumeOnlyValuesByOffsetAndCount() {
        produceAvroMessages(recordsOnlyValues);
        simpleConsumeMessages(SimpleConsumerAvroTest.topicName, 0, recordsOnlyValues.size(), recordsOnlyValues, KAFKA_V1_JSON_AVRO, KAFKA_V1_JSON_AVRO, SimpleConsumerAvroTest.avroConsumerRecordType, SimpleConsumerAvroTest.converter);
    }

    @Test
    public void testConsumeWithKeysByOffsetAndCount() {
        produceAvroMessages(recordsWithKeys);
        simpleConsumeMessages(SimpleConsumerAvroTest.topicName, 0, recordsWithKeys.size(), recordsWithKeys, KAFKA_V1_JSON_AVRO, KAFKA_V1_JSON_AVRO, SimpleConsumerAvroTest.avroConsumerRecordType, SimpleConsumerAvroTest.converter);
    }

    @Test
    public void testConsumeInvalidTopic() {
        Response response = request("/topics/nonexistenttopic/partitions/0/messages", Collections.singletonMap("offset", "0")).accept(KAFKA_V1_JSON_AVRO).get();
        TestUtils.assertErrorResponse(NOT_FOUND, response, TOPIC_NOT_FOUND_ERROR_CODE, TOPIC_NOT_FOUND_MESSAGE, KAFKA_V1_JSON_AVRO);
    }

    @Test
    public void testConsumeInvalidPartition() {
        Response response = request("/topics/topic1/partitions/1/messages", Collections.singletonMap("offset", "0")).accept(KAFKA_V1_JSON_AVRO).get();
        TestUtils.assertErrorResponse(NOT_FOUND, response, PARTITION_NOT_FOUND_ERROR_CODE, PARTITION_NOT_FOUND_MESSAGE, KAFKA_V1_JSON_AVRO);
    }
}

