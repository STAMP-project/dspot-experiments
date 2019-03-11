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
package io.confluent.ksql.rest.server.resources.streaming;


import Format.AVRO;
import Format.JSON;
import Format.STRING;
import GenericData.Record;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.ksql.rest.server.resources.streaming.TopicStream.Format;
import io.confluent.ksql.rest.server.resources.streaming.TopicStream.RecordFormatter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Bytes;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class TopicStreamTest {
    private SchemaRegistryClient schemaRegistryClient;

    @Test
    public void shouldMatchAvroFormatter() throws Exception {
        // Given:
        final Schema schema = TopicStreamTest.parseAvroSchema(("{\n" + ((((("    \"fields\": [\n" + "        { \"name\": \"str1\", \"type\": \"string\" }\n") + "    ],\n") + "    \"name\": \"myrecord\",\n") + "    \"type\": \"record\"\n") + "}")));
        final GenericData.Record avroRecord = new GenericData.Record(schema);
        avroRecord.put("str1", "My first string");
        expect(schemaRegistryClient.register(anyString(), anyObject())).andReturn(1);
        expect(schemaRegistryClient.getById(anyInt())).andReturn(schema).times(2);
        replay(schemaRegistryClient);
        final byte[] avroData = serializeAvroRecord(avroRecord);
        // When:
        final TopicStreamTest.Result result = getFormatter(avroData);
        // Then:
        MatcherAssert.assertThat(result.format, Matchers.is(AVRO));
        MatcherAssert.assertThat(result.formatted, Matchers.endsWith(", key, {\"str1\": \"My first string\"}\n"));
    }

    @Test
    public void shouldNotMatchAvroFormatter() {
        // Given:
        replay(schemaRegistryClient);
        final String notAvro = "test-data";
        // When:
        final TopicStreamTest.Result result = getFormatter(notAvro);
        // Then:
        MatcherAssert.assertThat(result.format, Matchers.is(Matchers.not(AVRO)));
    }

    @Test
    public void shouldFormatJson() {
        // Given:
        replay(schemaRegistryClient);
        final String json = "{    \"name\": \"myrecord\"," + ("    \"type\": \"record\"" + "}");
        // When:
        final TopicStreamTest.Result result = getFormatter(json);
        // Then:
        MatcherAssert.assertThat(result.format, Matchers.is(JSON));
        MatcherAssert.assertThat(result.formatted, Matchers.is("{\"ROWTIME\":-1,\"ROWKEY\":\"key\",\"name\":\"myrecord\",\"type\":\"record\"}\n"));
    }

    @Test
    public void shouldNotMatchJsonFormatter() {
        // Given:
        replay(schemaRegistryClient);
        final String notJson = "{  BAD DATA  \"name\": \"myrecord\"," + ("    \"type\": \"record\"" + "}");
        // When:
        final TopicStreamTest.Result result = getFormatter(notJson);
        // Then:
        MatcherAssert.assertThat(result.format, Matchers.is(Matchers.not(JSON)));
    }

    @Test
    public void shouldFilterNullValues() {
        replay(schemaRegistryClient);
        final ConsumerRecord<String, Bytes> record = new ConsumerRecord("some-topic", 1, 1, "key", null);
        final RecordFormatter formatter = new RecordFormatter(schemaRegistryClient, "some-topic");
        final ConsumerRecords<String, Bytes> records = new ConsumerRecords(ImmutableMap.of(new TopicPartition("some-topic", 1), ImmutableList.of(record)));
        MatcherAssert.assertThat(formatter.format(records), Matchers.empty());
    }

    @Test
    public void shouldHandleNullValuesFromSTRINGPrint() throws IOException {
        final DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(3, 1, Locale.getDefault());
        final ConsumerRecord<String, Bytes> record = new ConsumerRecord("some-topic", 1, 1, "key", null);
        final String formatted = STRING.maybeGetFormatter("some-topic", record, null, dateFormat).get().print(record);
        MatcherAssert.assertThat(formatted, Matchers.endsWith(", key , NULL\n"));
    }

    private static final class Result {
        private final Format format;

        private final String formatted;

        private Result(final Format format, final String formatted) {
            this.format = format;
            this.formatted = formatted;
        }
    }
}

