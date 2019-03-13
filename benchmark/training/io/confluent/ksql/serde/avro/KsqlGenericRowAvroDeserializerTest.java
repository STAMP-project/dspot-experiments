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
package io.confluent.ksql.serde.avro;


import KsqlConfig.SCHEMA_REGISTRY_URL_PROPERTY;
import Schema.BOOLEAN_SCHEMA;
import Schema.INT16_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.INT8_SCHEMA;
import Schema.OPTIONAL_BOOLEAN_SCHEMA;
import Schema.OPTIONAL_FLOAT64_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.STRING_SCHEMA;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.util.KsqlConfig;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema.Parser;
import org.apache.avro.Schema.Type.BOOLEAN;
import org.apache.avro.Schema.Type.DOUBLE;
import org.apache.avro.Schema.Type.FLOAT;
import org.apache.avro.Schema.Type.INT;
import org.apache.avro.Schema.Type.LONG;
import org.apache.avro.Schema.Type.STRING;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.org.apache.avro.Schema;
import org.apache.kafka.connect.data.org.apache.avro.SchemaBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class KsqlGenericRowAvroDeserializerTest {
    private static final String schemaStr = "{" + ((((((((((((("\"namespace\": \"kql\"," + " \"name\": \"orders\",") + " \"type\": \"record\",") + " \"fields\": [") + "     {\"name\": \"orderTime\", \"type\": \"long\"},") + "     {\"name\": \"orderId\",  \"type\": \"long\"},") + "     {\"name\": \"itemId\", \"type\": \"string\"},") + "     {\"name\": \"orderUnits\", \"type\": \"double\"},") + "     {\"name\": \"arrayCol\", \"type\": {\"type\": \"array\", \"items\": ") + "\"double\"}},") + "     {\"name\": \"mapCol\", \"type\": {\"type\": \"map\", \"values\": ") + "\"double\"}}") + " ]") + "}");

    private final Schema schema;

    private final Schema avroSchema;

    private final KsqlConfig ksqlConfig;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    public KsqlGenericRowAvroDeserializerTest() {
        final Parser parser = new Parser();
        avroSchema = parser.parse(KsqlGenericRowAvroDeserializerTest.schemaStr);
        schema = SchemaBuilder.struct().field("ORDERTIME".toUpperCase(), OPTIONAL_INT64_SCHEMA).field("ORDERID".toUpperCase(), OPTIONAL_INT64_SCHEMA).field("ITEMID".toUpperCase(), OPTIONAL_STRING_SCHEMA).field("ORDERUNITS".toUpperCase(), OPTIONAL_FLOAT64_SCHEMA).field("ARRAYCOL".toUpperCase(), SchemaBuilder.array(OPTIONAL_FLOAT64_SCHEMA).optional().build()).field("MAPCOL".toUpperCase(), SchemaBuilder.map(STRING_SCHEMA, OPTIONAL_FLOAT64_SCHEMA).optional().build()).optional().build();
        ksqlConfig = new KsqlConfig(Collections.singletonMap(SCHEMA_REGISTRY_URL_PROPERTY, "fake-schema-registry-url"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldDeserializeCorrectly() {
        final SchemaRegistryClient schemaRegistryClient = new MockSchemaRegistryClient();
        final List<Object> columns = Arrays.asList(1511897796092L, 1L, "item_1", 10.0, Collections.singletonList(100.0), Collections.singletonMap("key1", 100.0));
        final GenericRow genericRow = new GenericRow(columns);
        final GenericRow row = serializeDeserializeRow(schema, "t1", schemaRegistryClient, avroSchema, genericRow);
        Assert.assertNotNull(row);
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().size(), CoreMatchers.equalTo(6));
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().get(0), CoreMatchers.equalTo(1511897796092L));
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().get(1), CoreMatchers.equalTo(1L));
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().get(2), CoreMatchers.equalTo("item_1"));
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().get(3), CoreMatchers.equalTo(10.0));
        MatcherAssert.assertThat("Incorrect deserializarion", ((List<Double>) (row.getColumns().get(4))).size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat("Incorrect deserializarion", ((Map) (row.getColumns().get(5))).size(), CoreMatchers.equalTo(1));
    }

    @Test
    public void shouldDeserializeIfThereAreRedundantFields() {
        final Schema newSchema = SchemaBuilder.struct().field("ordertime".toUpperCase(), OPTIONAL_INT64_SCHEMA).field("orderid".toUpperCase(), OPTIONAL_INT64_SCHEMA).field("itemid".toUpperCase(), OPTIONAL_STRING_SCHEMA).field("orderunits".toUpperCase(), OPTIONAL_FLOAT64_SCHEMA).build();
        final SchemaRegistryClient schemaRegistryClient = new MockSchemaRegistryClient();
        final List<Object> columns = Arrays.asList(1511897796092L, 1L, "item_1", 10.0, Collections.emptyList(), Collections.emptyMap());
        final GenericRow genericRow = new GenericRow(columns);
        final GenericRow row = serializeDeserializeRow(newSchema, "t1", schemaRegistryClient, avroSchema, genericRow);
        Assert.assertNotNull(row);
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().size(), CoreMatchers.equalTo(4));
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().get(0), CoreMatchers.equalTo(1511897796092L));
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().get(1), CoreMatchers.equalTo(1L));
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().get(2), CoreMatchers.equalTo("item_1"));
    }

    @Test
    public void shouldDeserializeWithMissingFields() {
        final String schemaStr1 = "{" + ((((((((("\"namespace\": \"kql\"," + " \"name\": \"orders\",") + " \"type\": \"record\",") + " \"fields\": [") + "     {\"name\": \"orderTime\", \"type\": \"long\"},") + "     {\"name\": \"orderId\",  \"type\": \"long\"},") + "     {\"name\": \"itemId\", \"type\": \"string\"},") + "     {\"name\": \"orderUnits\", \"type\": \"double\"}") + " ]") + "}");
        final Parser parser = new Parser();
        final org.apache.avro.Schema avroSchema1 = parser.parse(schemaStr1);
        final SchemaRegistryClient schemaRegistryClient = new MockSchemaRegistryClient();
        final List<Object> columns = Arrays.asList(1511897796092L, 1L, "item_1", 10.0);
        final GenericRow genericRow = new GenericRow(columns);
        final GenericRow row = serializeDeserializeRow(schema, "t1", schemaRegistryClient, avroSchema1, genericRow);
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().size(), CoreMatchers.equalTo(6));
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().get(0), CoreMatchers.equalTo(1511897796092L));
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().get(1), CoreMatchers.equalTo(1L));
        MatcherAssert.assertThat("Incorrect deserializarion", row.getColumns().get(2), CoreMatchers.equalTo("item_1"));
        Assert.assertNull(row.getColumns().get(4));
        Assert.assertNull(row.getColumns().get(5));
    }

    @Test
    public void shouldDeserializeBooleanToBoolean() {
        shouldDeserializeTypeCorrectly(org.apache.avro.Schema.create(BOOLEAN), false, OPTIONAL_BOOLEAN_SCHEMA);
        shouldDeserializeTypeCorrectly(org.apache.avro.Schema.create(BOOLEAN), true, OPTIONAL_BOOLEAN_SCHEMA);
    }

    @Test
    public void shouldDeserializeIntToInt() {
        shouldDeserializeTypeCorrectly(org.apache.avro.Schema.create(INT), 123, OPTIONAL_INT32_SCHEMA);
    }

    @Test
    public void shouldDeserializeIntToBigint() {
        shouldDeserializeTypeCorrectly(org.apache.avro.Schema.create(INT), 123L, OPTIONAL_INT64_SCHEMA);
    }

    @Test
    public void shouldDeserializeLongToBigint() {
        shouldDeserializeTypeCorrectly(org.apache.avro.Schema.create(LONG), (((long) (Integer.MAX_VALUE)) * 32), OPTIONAL_INT64_SCHEMA);
    }

    @Test
    public void shouldDeserializeFloatToDouble() {
        shouldDeserializeTypeCorrectly(org.apache.avro.Schema.create(FLOAT), ((float) (1.25)), OPTIONAL_FLOAT64_SCHEMA, 1.25);
    }

    @Test
    public void shouldDeserializeDoubleToDouble() {
        shouldDeserializeTypeCorrectly(org.apache.avro.Schema.create(DOUBLE), 1.2345678901234567, OPTIONAL_FLOAT64_SCHEMA);
    }

    @Test
    public void shouldDeserializeStringToString() {
        shouldDeserializeTypeCorrectly(org.apache.avro.Schema.create(STRING), "foobarbizbazboz", OPTIONAL_STRING_SCHEMA);
    }

    @Test
    public void shouldDeserializeEnumToString() {
        final org.apache.avro.Schema enumSchema = org.apache.avro.Schema.createEnum("enum", "doc", "namespace", ImmutableList.of("V0", "V1", "V2"));
        shouldDeserializeTypeCorrectly(enumSchema, new org.apache.avro.generic.GenericData.EnumSymbol(enumSchema, "V1"), OPTIONAL_STRING_SCHEMA, "V1");
    }

    @Test
    public void shouldDeserializeRecordToStruct() {
        final org.apache.avro.Schema recordSchema = org.apache.avro.SchemaBuilder.record("record").fields().name("inner1").type(org.apache.avro.Schema.create(STRING)).noDefault().name("inner2").type(org.apache.avro.Schema.create(INT)).noDefault().endRecord();
        final GenericRecord record = new org.apache.avro.generic.GenericData.Record(recordSchema);
        record.put("inner1", "foobar");
        record.put("inner2", 123456);
        final Schema structSchema = SchemaBuilder.struct().field("inner1", OPTIONAL_STRING_SCHEMA).field("inner2", OPTIONAL_INT32_SCHEMA).optional().build();
        final Struct struct = new Struct(structSchema);
        struct.put("inner1", "foobar");
        struct.put("inner2", 123456);
        shouldDeserializeTypeCorrectly(recordSchema, record, structSchema, struct);
    }

    @Test
    public void shouldDeserializeNullValue() {
        shouldDeserializeTypeCorrectly(org.apache.avro.SchemaBuilder.unionOf().nullType().and().intType().endUnion(), null, OPTIONAL_INT32_SCHEMA);
    }

    @Test
    public void shouldDeserializeArrayToArray() {
        shouldDeserializeTypeCorrectly(org.apache.avro.SchemaBuilder.array().items().intType(), ImmutableList.of(1, 2, 3, 4, 5, 6), SchemaBuilder.array(OPTIONAL_INT32_SCHEMA).optional().build());
    }

    @Test
    public void shouldDeserializeMapToMap() {
        shouldDeserializeTypeCorrectly(org.apache.avro.SchemaBuilder.map().values().intType(), ImmutableMap.of("one", 1, "two", 2, "three", 3), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build());
    }

    @Test
    public void shouldDeserializeDateToInteger() {
        shouldDeserializeTypeCorrectly(LogicalTypes.date().addToSchema(org.apache.avro.SchemaBuilder.builder().intType()), ((int) (ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), LocalDate.now()))), OPTIONAL_INT32_SCHEMA);
    }

    @Test
    public void shouldDeserializeDateToBigint() {
        shouldDeserializeTypeCorrectly(LogicalTypes.date().addToSchema(org.apache.avro.SchemaBuilder.builder().intType()), ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), LocalDate.now()), OPTIONAL_INT64_SCHEMA);
    }

    @Test
    public void shouldDeserializeTimeMicrosToBigint() {
        shouldDeserializeTypeCorrectly(LogicalTypes.timeMicros().addToSchema(org.apache.avro.SchemaBuilder.builder().longType()), ChronoUnit.MICROS.between(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT), LocalDateTime.now()), OPTIONAL_INT64_SCHEMA);
    }

    @Test
    public void shouldDeserializeTimeMillisToBigint() {
        shouldDeserializeTypeCorrectly(LogicalTypes.timeMillis().addToSchema(org.apache.avro.SchemaBuilder.builder().intType()), ChronoUnit.MILLIS.between(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT), LocalDateTime.now()), OPTIONAL_INT64_SCHEMA);
    }

    @Test
    public void shouldDeserializeTimestampToInteger() {
        shouldDeserializeTypeCorrectly(LogicalTypes.timestampMicros().addToSchema(org.apache.avro.SchemaBuilder.builder().longType()), ChronoUnit.MICROS.between(LocalDateTime.of(LocalDate.ofEpochDay(0), LocalTime.MIDNIGHT), LocalDateTime.now()), OPTIONAL_INT64_SCHEMA);
    }

    @Test
    public void shouldDeserializeTimestampToBigint() {
        shouldDeserializeTypeCorrectly(LogicalTypes.timestampMillis().addToSchema(org.apache.avro.SchemaBuilder.builder().longType()), ChronoUnit.MILLIS.between(LocalDateTime.of(LocalDate.ofEpochDay(0), LocalTime.MIDNIGHT), LocalDateTime.now()), OPTIONAL_INT64_SCHEMA);
    }

    @Test
    public void shouldDeserializeUnionToStruct() {
        final org.apache.avro.Schema avroSchema = org.apache.avro.SchemaBuilder.unionOf().intType().and().stringType().endUnion();
        final Schema ksqlSchema = SchemaBuilder.struct().field("int", OPTIONAL_INT32_SCHEMA).field("string", OPTIONAL_STRING_SCHEMA).optional().build();
        final Struct ksqlValue = put("string", "foobar");
        shouldDeserializeTypeCorrectly(avroSchema, "foobar", ksqlSchema, ksqlValue);
    }

    @Test
    public void shouldDeserializeConnectInt8ToInteger() {
        shouldDeserializeConnectTypeCorrectly(INT8_SCHEMA, ((byte) (32)), OPTIONAL_INT32_SCHEMA, 32);
    }

    @Test
    public void shouldDeserializeConnectInt16ToInteger() {
        shouldDeserializeConnectTypeCorrectly(INT16_SCHEMA, ((short) (16384)), OPTIONAL_INT32_SCHEMA, 16384);
    }

    @Test
    public void shouldDeserializeConnectInt8ToBigint() {
        shouldDeserializeConnectTypeCorrectly(INT8_SCHEMA, ((byte) (32)), OPTIONAL_INT64_SCHEMA, 32L);
    }

    @Test
    public void shouldDeserializeConnectInt16ToBigint() {
        shouldDeserializeConnectTypeCorrectly(INT16_SCHEMA, ((short) (16384)), OPTIONAL_INT64_SCHEMA, 16384L);
    }

    @Test
    public void shouldDeserializeConnectMapWithInt8Key() {
        shouldDeserializeConnectTypeCorrectly(SchemaBuilder.map(INT8_SCHEMA, INT32_SCHEMA).optional().build(), ImmutableMap.of(((byte) (1)), 10, ((byte) (2)), 20, ((byte) (3)), 30), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build(), ImmutableMap.of("1", 10, "2", 20, "3", 30));
    }

    @Test
    public void shouldDeserializeConnectMapWithInt16Key() {
        shouldDeserializeConnectTypeCorrectly(SchemaBuilder.map(INT16_SCHEMA, INT32_SCHEMA).optional().build(), ImmutableMap.of(((short) (1)), 10, ((short) (2)), 20, ((short) (3)), 30), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build(), ImmutableMap.of("1", 10, "2", 20, "3", 30));
    }

    @Test
    public void shouldDeserializeConnectMapWithInt32Key() {
        shouldDeserializeConnectTypeCorrectly(SchemaBuilder.map(INT32_SCHEMA, INT32_SCHEMA).optional().build(), ImmutableMap.of(1, 10, 2, 20, 3, 30), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build(), ImmutableMap.of("1", 10, "2", 20, "3", 30));
    }

    @Test
    public void shouldDeserializeConnectMapWithInt64Key() {
        shouldDeserializeConnectTypeCorrectly(SchemaBuilder.map(INT64_SCHEMA, INT32_SCHEMA).optional().build(), ImmutableMap.of(1L, 10, 2L, 20, 3L, 30), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build(), ImmutableMap.of("1", 10, "2", 20, "3", 30));
    }

    @Test
    public void shouldDeserializeConnectMapWithBooleanKey() {
        shouldDeserializeConnectTypeCorrectly(SchemaBuilder.map(BOOLEAN_SCHEMA, INT32_SCHEMA).optional().build(), ImmutableMap.of(true, 10, false, 20), SchemaBuilder.map(OPTIONAL_STRING_SCHEMA, OPTIONAL_INT32_SCHEMA).optional().build(), ImmutableMap.of("true", 10, "false", 20));
    }
}

