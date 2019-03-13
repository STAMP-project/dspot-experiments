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
package io.confluent.kafkarest.unit;


import Errors.JSON_AVRO_CONVERSION_MESSAGE;
import ProducerPool.ProduceRequestCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafkarest.AvroRestProducer;
import io.confluent.kafkarest.entities.AvroTopicProduceRecord;
import io.confluent.kafkarest.entities.SchemaHolder;
import io.confluent.rest.exceptions.RestConstraintViolationException;
import java.util.Arrays;
import java.util.concurrent.Future;
import javax.validation.ConstraintViolationException;
import org.apache.avro.Schema;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class AvroRestProducerTest {
    private static final ObjectMapper mapper = new ObjectMapper();

    private KafkaAvroSerializer keySerializer;

    private KafkaAvroSerializer valueSerializer;

    private KafkaProducer<Object, Object> producer;

    private AvroRestProducer restProducer;

    private SchemaHolder schemaHolder;

    private ProduceRequestCallback produceCallback;

    @Test(expected = ConstraintViolationException.class)
    public void testInvalidSchema() throws Exception {
        schemaHolder = new SchemaHolder(null, "invalidValueSchema");
        restProducer.produce(new io.confluent.kafkarest.ProduceTask(schemaHolder, 1, produceCallback), "test", null, Arrays.asList(new AvroTopicProduceRecord(AvroRestProducerTest.mapper.readTree("{}"), AvroRestProducerTest.mapper.readTree("{}"), null)));
    }

    @Test
    public void testInvalidData() throws Exception {
        schemaHolder = new SchemaHolder(null, "\"int\"");
        try {
            restProducer.produce(new io.confluent.kafkarest.ProduceTask(schemaHolder, 1, produceCallback), "test", null, Arrays.asList(new AvroTopicProduceRecord(null, AvroRestProducerTest.mapper.readTree("\"string\""), null)));
        } catch (RestConstraintViolationException e) {
            // expected, but should contain additional info
            assert e.getMessage().startsWith(JSON_AVRO_CONVERSION_MESSAGE);
            assert (e.getMessage().length()) > (JSON_AVRO_CONVERSION_MESSAGE.length());
        } catch (Throwable t) {
            Assert.fail("Unexpected exception type");
        }
    }

    @Test
    public void testRepeatedProducer() throws Exception {
        final int schemaId = 1;
        final String valueSchemaStr = "{\"type\": \"record\", \"name\": \"User\", \"fields\": [{\"name\": \"name\", \"type\": \"string\"}]}";
        final Schema valueSchema = new Schema.Parser().parse(valueSchemaStr);
        // This is the key part of the test, we should only call register once with the same schema, and then see the lookup
        // by ID the rest of the times
        EasyMock.expect(valueSerializer.register(EasyMock.isA(String.class), EasyMock.isA(Schema.class))).andReturn(schemaId);
        EasyMock.expect(valueSerializer.getById(schemaId)).andReturn(valueSchema).times(9999);
        EasyMock.replay(valueSerializer);
        Future f = EasyMock.createMock(Future.class);
        EasyMock.expect(producer.send(EasyMock.isA(ProducerRecord.class), EasyMock.isA(Callback.class))).andStubReturn(f);
        EasyMock.replay(producer);
        schemaHolder = new SchemaHolder(null, valueSchemaStr);
        for (int i = 0; i < 10000; ++i) {
            restProducer.produce(new io.confluent.kafkarest.ProduceTask(schemaHolder, 1, produceCallback), "test", null, Arrays.asList(new AvroTopicProduceRecord(null, AvroRestProducerTest.mapper.readTree("{\"name\": \"bob\"}"), null)));
        }
    }
}

