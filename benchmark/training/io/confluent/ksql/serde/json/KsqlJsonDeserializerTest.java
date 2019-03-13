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
package io.confluent.ksql.serde.json;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.logging.processing.ProcessingLogConfig;
import io.confluent.ksql.logging.processing.ProcessingLogger;
import io.confluent.ksql.serde.SerdeTestUtils;
import io.confluent.ksql.serde.util.SerdeProcessingLogMessageFactory;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Schema.OPTIONAL_FLOAT64_SCHEMA;
import org.apache.kafka.connect.data.Schema.OPTIONAL_INT64_SCHEMA;
import org.apache.kafka.connect.data.Schema.OPTIONAL_STRING_SCHEMA;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class KsqlJsonDeserializerTest {
    private Schema orderSchema;

    private KsqlJsonDeserializer ksqlJsonDeserializer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ProcessingLogConfig processingLogConfig = new ProcessingLogConfig(Collections.emptyMap());

    @Mock
    ProcessingLogger recordLogger;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void shouldDeserializeJsonCorrectly() throws JsonProcessingException {
        final Map<String, Object> orderRow = new HashMap<>();
        orderRow.put("ordertime", 1511897796092L);
        orderRow.put("@orderid", 1L);
        orderRow.put("itemid", "Item_1");
        orderRow.put("orderunits", 10.0);
        orderRow.put("arraycol", new Double[]{ 10.0, 20.0 });
        orderRow.put("mapcol", Collections.singletonMap("key1", 10.0));
        final byte[] jsonBytes = objectMapper.writeValueAsBytes(orderRow);
        final GenericRow genericRow = ksqlJsonDeserializer.deserialize("", jsonBytes);
        Assert.assertThat(genericRow.getColumns().size(), CoreMatchers.equalTo(6));
        Assert.assertThat(genericRow.getColumns().get(0), CoreMatchers.equalTo(1511897796092L));
        Assert.assertThat(genericRow.getColumns().get(1), CoreMatchers.equalTo(1L));
        Assert.assertThat(genericRow.getColumns().get(2), CoreMatchers.equalTo("Item_1"));
        Assert.assertThat(genericRow.getColumns().get(3), CoreMatchers.equalTo(10.0));
    }

    @Test
    public void shouldDeserializeJsonCorrectlyWithRedundantFields() throws JsonProcessingException {
        final Map<String, Object> orderRow = new HashMap<>();
        orderRow.put("ordertime", 1511897796092L);
        orderRow.put("@orderid", 1L);
        orderRow.put("itemid", "Item_1");
        orderRow.put("orderunits", 10.0);
        orderRow.put("arraycol", new Double[]{ 10.0, 20.0 });
        orderRow.put("mapcol", Collections.singletonMap("key1", 10.0));
        final byte[] jsonBytes = objectMapper.writeValueAsBytes(orderRow);
        final Schema newOrderSchema = SchemaBuilder.struct().field("ordertime".toUpperCase(), OPTIONAL_INT64_SCHEMA).field("orderid".toUpperCase(), OPTIONAL_INT64_SCHEMA).field("itemid".toUpperCase(), OPTIONAL_STRING_SCHEMA).field("orderunits".toUpperCase(), OPTIONAL_FLOAT64_SCHEMA).build();
        final KsqlJsonDeserializer ksqlJsonDeserializer = new KsqlJsonDeserializer(newOrderSchema, false, recordLogger);
        final GenericRow genericRow = ksqlJsonDeserializer.deserialize("", jsonBytes);
        Assert.assertThat(genericRow.getColumns().size(), CoreMatchers.equalTo(4));
        Assert.assertThat(genericRow.getColumns().get(0), CoreMatchers.equalTo(1511897796092L));
        Assert.assertThat(genericRow.getColumns().get(1), CoreMatchers.equalTo(1L));
        Assert.assertThat(genericRow.getColumns().get(2), CoreMatchers.equalTo("Item_1"));
        Assert.assertThat(genericRow.getColumns().get(3), CoreMatchers.equalTo(10.0));
    }

    @Test
    public void shouldDeserializeEvenWithMissingFields() throws JsonProcessingException {
        final Map<String, Object> orderRow = new HashMap<>();
        orderRow.put("ordertime", 1511897796092L);
        orderRow.put("@orderid", 1L);
        orderRow.put("itemid", "Item_1");
        orderRow.put("orderunits", 10.0);
        final byte[] jsonBytes = objectMapper.writeValueAsBytes(orderRow);
        final GenericRow genericRow = ksqlJsonDeserializer.deserialize("", jsonBytes);
        Assert.assertThat(genericRow.getColumns().size(), CoreMatchers.equalTo(6));
        Assert.assertThat(genericRow.getColumns().get(0), CoreMatchers.equalTo(1511897796092L));
        Assert.assertThat(genericRow.getColumns().get(1), CoreMatchers.equalTo(1L));
        Assert.assertThat(genericRow.getColumns().get(2), CoreMatchers.equalTo("Item_1"));
        Assert.assertThat(genericRow.getColumns().get(3), CoreMatchers.equalTo(10.0));
        Assert.assertThat(genericRow.getColumns().get(4), CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertThat(genericRow.getColumns().get(5), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldTreatNullAsNull() throws JsonProcessingException {
        final Map<String, Object> row = new HashMap<>();
        row.put("ordertime", null);
        row.put("@orderid", null);
        row.put("itemid", null);
        row.put("orderunits", null);
        row.put("arrayCol", new Double[]{ 0.0, null });
        row.put("mapCol", null);
        final GenericRow expected = new GenericRow(Arrays.asList(null, null, null, null, new Double[]{ 0.0, null }, null));
        final GenericRow genericRow = ksqlJsonDeserializer.deserialize("", objectMapper.writeValueAsBytes(row));
        Assert.assertThat(genericRow, CoreMatchers.equalTo(expected));
    }

    @Test
    public void shouldCreateJsonStringForStructIfDefinedAsVarchar() throws JsonProcessingException {
        final Schema schema = SchemaBuilder.struct().field("itemid".toUpperCase(), Schema.OPTIONAL_STRING_SCHEMA).build();
        final KsqlJsonDeserializer deserializer = new KsqlJsonDeserializer(schema, false, recordLogger);
        final GenericRow expected = new GenericRow(Collections.singletonList("{\"CATEGORY\":{\"ID\":2,\"NAME\":\"Food\"},\"ITEMID\":6,\"NAME\":\"Item_6\"}"));
        final GenericRow genericRow = deserializer.deserialize("", "{\"itemid\":{\"CATEGORY\":{\"ID\":2,\"NAME\":\"Food\"},\"ITEMID\":6,\"NAME\":\"Item_6\"}}".getBytes(StandardCharsets.UTF_8));
        Assert.assertThat(genericRow, CoreMatchers.equalTo(expected));
    }

    @Test
    public void shouldLogDeserializationErrors() {
        // When:
        Throwable cause = null;
        final byte[] data = "{foo".getBytes(StandardCharsets.UTF_8);
        try {
            ksqlJsonDeserializer.deserialize("", data);
            Assert.fail("deserialize should have thrown");
        } catch (final SerializationException e) {
            cause = e.getCause();
        }
        // Then:
        SerdeTestUtils.shouldLogError(recordLogger, SerdeProcessingLogMessageFactory.deserializationErrorMsg(cause, Optional.ofNullable(data)).apply(processingLogConfig), processingLogConfig);
    }
}

