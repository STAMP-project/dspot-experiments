/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.json;


import Date.SCHEMA;
import JsonSchema.ENVELOPE_PAYLOAD_FIELD_NAME;
import JsonSchema.ENVELOPE_SCHEMA_FIELD_NAME;
import Schema.BOOLEAN_SCHEMA;
import Schema.BYTES_SCHEMA;
import Schema.FLOAT32_SCHEMA;
import Schema.FLOAT64_SCHEMA;
import Schema.INT16_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.INT8_SCHEMA;
import Schema.OPTIONAL_BOOLEAN_SCHEMA;
import Schema.STRING_SCHEMA;
import SchemaAndValue.NULL;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import org.apache.kafka.common.cache.Cache;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.connect.data.Date;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Time;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.errors.DataException;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;


public class JsonConverterTest {
    private static final String TOPIC = "topic";

    ObjectMapper objectMapper = new ObjectMapper();

    JsonConverter converter = new JsonConverter();

    // Schema metadata
    @Test
    public void testConnectSchemaMetadataTranslation() {
        // this validates the non-type fields are translated and handled properly
        Assert.assertEquals(new SchemaAndValue(Schema.BOOLEAN_SCHEMA, true), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"boolean\" }, \"payload\": true }".getBytes()));
        Assert.assertEquals(new SchemaAndValue(Schema.OPTIONAL_BOOLEAN_SCHEMA, null), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"boolean\", \"optional\": true }, \"payload\": null }".getBytes()));
        Assert.assertEquals(new SchemaAndValue(SchemaBuilder.bool().defaultValue(true).build(), true), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"boolean\", \"default\": true }, \"payload\": null }".getBytes()));
        Assert.assertEquals(new SchemaAndValue(SchemaBuilder.bool().required().name("bool").version(2).doc("the documentation").parameter("foo", "bar").build(), true), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"boolean\", \"optional\": false, \"name\": \"bool\", \"version\": 2, \"doc\": \"the documentation\", \"parameters\": { \"foo\": \"bar\" }}, \"payload\": true }".getBytes()));
    }

    // Schema types
    @Test
    public void booleanToConnect() {
        Assert.assertEquals(new SchemaAndValue(Schema.BOOLEAN_SCHEMA, true), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"boolean\" }, \"payload\": true }".getBytes()));
        Assert.assertEquals(new SchemaAndValue(Schema.BOOLEAN_SCHEMA, false), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"boolean\" }, \"payload\": false }".getBytes()));
    }

    @Test
    public void byteToConnect() {
        Assert.assertEquals(new SchemaAndValue(Schema.INT8_SCHEMA, ((byte) (12))), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"int8\" }, \"payload\": 12 }".getBytes()));
    }

    @Test
    public void shortToConnect() {
        Assert.assertEquals(new SchemaAndValue(Schema.INT16_SCHEMA, ((short) (12))), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"int16\" }, \"payload\": 12 }".getBytes()));
    }

    @Test
    public void intToConnect() {
        Assert.assertEquals(new SchemaAndValue(Schema.INT32_SCHEMA, 12), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"int32\" }, \"payload\": 12 }".getBytes()));
    }

    @Test
    public void longToConnect() {
        Assert.assertEquals(new SchemaAndValue(Schema.INT64_SCHEMA, 12L), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"int64\" }, \"payload\": 12 }".getBytes()));
        Assert.assertEquals(new SchemaAndValue(Schema.INT64_SCHEMA, 4398046511104L), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"int64\" }, \"payload\": 4398046511104 }".getBytes()));
    }

    @Test
    public void floatToConnect() {
        Assert.assertEquals(new SchemaAndValue(Schema.FLOAT32_SCHEMA, 12.34F), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"float\" }, \"payload\": 12.34 }".getBytes()));
    }

    @Test
    public void doubleToConnect() {
        Assert.assertEquals(new SchemaAndValue(Schema.FLOAT64_SCHEMA, 12.34), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"double\" }, \"payload\": 12.34 }".getBytes()));
    }

    @Test
    public void bytesToConnect() throws UnsupportedEncodingException {
        ByteBuffer reference = ByteBuffer.wrap("test-string".getBytes("UTF-8"));
        String msg = "{ \"schema\": { \"type\": \"bytes\" }, \"payload\": \"dGVzdC1zdHJpbmc=\" }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        ByteBuffer converted = ByteBuffer.wrap(((byte[]) (schemaAndValue.value())));
        Assert.assertEquals(reference, converted);
    }

    @Test
    public void stringToConnect() {
        Assert.assertEquals(new SchemaAndValue(Schema.STRING_SCHEMA, "foo-bar-baz"), converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"string\" }, \"payload\": \"foo-bar-baz\" }".getBytes()));
    }

    @Test
    public void arrayToConnect() {
        byte[] arrayJson = "{ \"schema\": { \"type\": \"array\", \"items\": { \"type\" : \"int32\" } }, \"payload\": [1, 2, 3] }".getBytes();
        Assert.assertEquals(new SchemaAndValue(SchemaBuilder.array(INT32_SCHEMA).build(), Arrays.asList(1, 2, 3)), converter.toConnectData(JsonConverterTest.TOPIC, arrayJson));
    }

    @Test
    public void mapToConnectStringKeys() {
        byte[] mapJson = "{ \"schema\": { \"type\": \"map\", \"keys\": { \"type\" : \"string\" }, \"values\": { \"type\" : \"int32\" } }, \"payload\": { \"key1\": 12, \"key2\": 15} }".getBytes();
        Map<String, Integer> expected = new HashMap<>();
        expected.put("key1", 12);
        expected.put("key2", 15);
        Assert.assertEquals(new SchemaAndValue(SchemaBuilder.map(STRING_SCHEMA, INT32_SCHEMA).build(), expected), converter.toConnectData(JsonConverterTest.TOPIC, mapJson));
    }

    @Test
    public void mapToConnectNonStringKeys() {
        byte[] mapJson = "{ \"schema\": { \"type\": \"map\", \"keys\": { \"type\" : \"int32\" }, \"values\": { \"type\" : \"int32\" } }, \"payload\": [ [1, 12], [2, 15] ] }".getBytes();
        Map<Integer, Integer> expected = new HashMap<>();
        expected.put(1, 12);
        expected.put(2, 15);
        Assert.assertEquals(new SchemaAndValue(SchemaBuilder.map(INT32_SCHEMA, INT32_SCHEMA).build(), expected), converter.toConnectData(JsonConverterTest.TOPIC, mapJson));
    }

    @Test
    public void structToConnect() {
        byte[] structJson = "{ \"schema\": { \"type\": \"struct\", \"fields\": [{ \"field\": \"field1\", \"type\": \"boolean\" }, { \"field\": \"field2\", \"type\": \"string\" }] }, \"payload\": { \"field1\": true, \"field2\": \"string\" } }".getBytes();
        Schema expectedSchema = SchemaBuilder.struct().field("field1", BOOLEAN_SCHEMA).field("field2", STRING_SCHEMA).build();
        Struct expected = put("field1", true).put("field2", "string");
        SchemaAndValue converted = converter.toConnectData(JsonConverterTest.TOPIC, structJson);
        Assert.assertEquals(new SchemaAndValue(expectedSchema, expected), converted);
    }

    @Test
    public void nullToConnect() {
        // When schemas are enabled, trying to decode a tombstone should be an empty envelope
        // the behavior is the same as when the json is "{ "schema": null, "payload": null }"
        // to keep compatibility with the record
        SchemaAndValue converted = converter.toConnectData(JsonConverterTest.TOPIC, null);
        Assert.assertEquals(NULL, converted);
    }

    @Test
    public void nullSchemaPrimitiveToConnect() {
        SchemaAndValue converted = converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": null, \"payload\": null }".getBytes());
        Assert.assertEquals(NULL, converted);
        converted = converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": null, \"payload\": true }".getBytes());
        Assert.assertEquals(new SchemaAndValue(null, true), converted);
        // Integers: Connect has more data types, and JSON unfortunately mixes all number types. We try to preserve
        // info as best we can, so we always use the largest integer and floating point numbers we can and have Jackson
        // determine if it's an integer or not
        converted = converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": null, \"payload\": 12 }".getBytes());
        Assert.assertEquals(new SchemaAndValue(null, 12L), converted);
        converted = converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": null, \"payload\": 12.24 }".getBytes());
        Assert.assertEquals(new SchemaAndValue(null, 12.24), converted);
        converted = converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": null, \"payload\": \"a string\" }".getBytes());
        Assert.assertEquals(new SchemaAndValue(null, "a string"), converted);
        converted = converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": null, \"payload\": [1, \"2\", 3] }".getBytes());
        Assert.assertEquals(new SchemaAndValue(null, Arrays.asList(1L, "2", 3L)), converted);
        converted = converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": null, \"payload\": { \"field1\": 1, \"field2\": 2} }".getBytes());
        Map<String, Long> obj = new HashMap<>();
        obj.put("field1", 1L);
        obj.put("field2", 2L);
        Assert.assertEquals(new SchemaAndValue(null, obj), converted);
    }

    @Test
    public void decimalToConnect() {
        Schema schema = Decimal.schema(2);
        BigDecimal reference = new BigDecimal(new BigInteger("156"), 2);
        // Payload is base64 encoded byte[]{0, -100}, which is the two's complement encoding of 156.
        String msg = "{ \"schema\": { \"type\": \"bytes\", \"name\": \"org.apache.kafka.connect.data.Decimal\", \"version\": 1, \"parameters\": { \"scale\": \"2\" } }, \"payload\": \"AJw=\" }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        BigDecimal converted = ((BigDecimal) (schemaAndValue.value()));
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(reference, converted);
    }

    @Test
    public void decimalToConnectOptional() {
        Schema schema = Decimal.builder(2).optional().schema();
        String msg = "{ \"schema\": { \"type\": \"bytes\", \"name\": \"org.apache.kafka.connect.data.Decimal\", \"version\": 1, \"optional\": true, \"parameters\": { \"scale\": \"2\" } }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertNull(schemaAndValue.value());
    }

    @Test
    public void decimalToConnectWithDefaultValue() {
        BigDecimal reference = new BigDecimal(new BigInteger("156"), 2);
        Schema schema = Decimal.builder(2).defaultValue(reference).build();
        String msg = "{ \"schema\": { \"type\": \"bytes\", \"name\": \"org.apache.kafka.connect.data.Decimal\", \"version\": 1, \"default\": \"AJw=\", \"parameters\": { \"scale\": \"2\" } }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(reference, schemaAndValue.value());
    }

    @Test
    public void decimalToConnectOptionalWithDefaultValue() {
        BigDecimal reference = new BigDecimal(new BigInteger("156"), 2);
        Schema schema = Decimal.builder(2).optional().defaultValue(reference).build();
        String msg = "{ \"schema\": { \"type\": \"bytes\", \"name\": \"org.apache.kafka.connect.data.Decimal\", \"version\": 1, \"optional\": true, \"default\": \"AJw=\", \"parameters\": { \"scale\": \"2\" } }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(reference, schemaAndValue.value());
    }

    @Test
    public void dateToConnect() {
        Schema schema = Date.SCHEMA;
        GregorianCalendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.DATE, 10000);
        java.util.Date reference = calendar.getTime();
        String msg = "{ \"schema\": { \"type\": \"int32\", \"name\": \"org.apache.kafka.connect.data.Date\", \"version\": 1 }, \"payload\": 10000 }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        java.util.Date converted = ((java.util.Date) (schemaAndValue.value()));
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(reference, converted);
    }

    @Test
    public void dateToConnectOptional() {
        Schema schema = Date.builder().optional().schema();
        String msg = "{ \"schema\": { \"type\": \"int32\", \"name\": \"org.apache.kafka.connect.data.Date\", \"version\": 1, \"optional\": true }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertNull(schemaAndValue.value());
    }

    @Test
    public void dateToConnectWithDefaultValue() {
        java.util.Date reference = new java.util.Date(0);
        Schema schema = Date.builder().defaultValue(reference).schema();
        String msg = "{ \"schema\": { \"type\": \"int32\", \"name\": \"org.apache.kafka.connect.data.Date\", \"version\": 1, \"default\": 0 }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(reference, schemaAndValue.value());
    }

    @Test
    public void dateToConnectOptionalWithDefaultValue() {
        java.util.Date reference = new java.util.Date(0);
        Schema schema = Date.builder().optional().defaultValue(reference).schema();
        String msg = "{ \"schema\": { \"type\": \"int32\", \"name\": \"org.apache.kafka.connect.data.Date\", \"version\": 1, \"optional\": true, \"default\": 0 }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(reference, schemaAndValue.value());
    }

    @Test
    public void timeToConnect() {
        Schema schema = Time.SCHEMA;
        GregorianCalendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.MILLISECOND, 14400000);
        java.util.Date reference = calendar.getTime();
        String msg = "{ \"schema\": { \"type\": \"int32\", \"name\": \"org.apache.kafka.connect.data.Time\", \"version\": 1 }, \"payload\": 14400000 }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        java.util.Date converted = ((java.util.Date) (schemaAndValue.value()));
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(reference, converted);
    }

    @Test
    public void timeToConnectOptional() {
        Schema schema = Time.builder().optional().schema();
        String msg = "{ \"schema\": { \"type\": \"int32\", \"name\": \"org.apache.kafka.connect.data.Time\", \"version\": 1, \"optional\": true }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertNull(schemaAndValue.value());
    }

    @Test
    public void timeToConnectWithDefaultValue() {
        java.util.Date reference = new java.util.Date(0);
        Schema schema = Time.builder().defaultValue(reference).schema();
        String msg = "{ \"schema\": { \"type\": \"int32\", \"name\": \"org.apache.kafka.connect.data.Time\", \"version\": 1, \"default\": 0 }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(reference, schemaAndValue.value());
    }

    @Test
    public void timeToConnectOptionalWithDefaultValue() {
        java.util.Date reference = new java.util.Date(0);
        Schema schema = Time.builder().optional().defaultValue(reference).schema();
        String msg = "{ \"schema\": { \"type\": \"int32\", \"name\": \"org.apache.kafka.connect.data.Time\", \"version\": 1, \"optional\": true, \"default\": 0 }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(reference, schemaAndValue.value());
    }

    @Test
    public void timestampToConnect() {
        Schema schema = Timestamp.SCHEMA;
        GregorianCalendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.MILLISECOND, 2000000000);
        calendar.add(Calendar.MILLISECOND, 2000000000);
        java.util.Date reference = calendar.getTime();
        String msg = "{ \"schema\": { \"type\": \"int64\", \"name\": \"org.apache.kafka.connect.data.Timestamp\", \"version\": 1 }, \"payload\": 4000000000 }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        java.util.Date converted = ((java.util.Date) (schemaAndValue.value()));
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(reference, converted);
    }

    @Test
    public void timestampToConnectOptional() {
        Schema schema = Timestamp.builder().optional().schema();
        String msg = "{ \"schema\": { \"type\": \"int64\", \"name\": \"org.apache.kafka.connect.data.Timestamp\", \"version\": 1, \"optional\": true }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertNull(schemaAndValue.value());
    }

    @Test
    public void timestampToConnectWithDefaultValue() {
        Schema schema = Timestamp.builder().defaultValue(new java.util.Date(42)).schema();
        String msg = "{ \"schema\": { \"type\": \"int64\", \"name\": \"org.apache.kafka.connect.data.Timestamp\", \"version\": 1, \"default\": 42 }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(new java.util.Date(42), schemaAndValue.value());
    }

    @Test
    public void timestampToConnectOptionalWithDefaultValue() {
        Schema schema = Timestamp.builder().optional().defaultValue(new java.util.Date(42)).schema();
        String msg = "{ \"schema\": { \"type\": \"int64\", \"name\": \"org.apache.kafka.connect.data.Timestamp\", \"version\": 1,  \"optional\": true, \"default\": 42 }, \"payload\": null }";
        SchemaAndValue schemaAndValue = converter.toConnectData(JsonConverterTest.TOPIC, msg.getBytes());
        Assert.assertEquals(schema, schemaAndValue.schema());
        Assert.assertEquals(new java.util.Date(42), schemaAndValue.value());
    }

    // Schema metadata
    @Test
    public void testJsonSchemaMetadataTranslation() {
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, BOOLEAN_SCHEMA, true));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"boolean\", \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(true, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).booleanValue());
        converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, OPTIONAL_BOOLEAN_SCHEMA, null));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"boolean\", \"optional\": true }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertTrue(converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).isNull());
        converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, SchemaBuilder.bool().defaultValue(true).build(), true));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"boolean\", \"optional\": false, \"default\": true }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(true, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).booleanValue());
        converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, SchemaBuilder.bool().required().name("bool").version(3).doc("the documentation").parameter("foo", "bar").build(), true));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"boolean\", \"optional\": false, \"name\": \"bool\", \"version\": 3, \"doc\": \"the documentation\", \"parameters\": { \"foo\": \"bar\" }}"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(true, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).booleanValue());
    }

    @Test
    public void testCacheSchemaToConnectConversion() {
        Cache<JsonNode, Schema> cache = Whitebox.getInternalState(converter, "toConnectSchemaCache");
        Assert.assertEquals(0, cache.size());
        converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"boolean\" }, \"payload\": true }".getBytes());
        Assert.assertEquals(1, cache.size());
        converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"boolean\" }, \"payload\": true }".getBytes());
        Assert.assertEquals(1, cache.size());
        // Different schema should also get cached
        converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"boolean\", \"optional\": true }, \"payload\": true }".getBytes());
        Assert.assertEquals(2, cache.size());
        // Even equivalent, but different JSON encoding of schema, should get different cache entry
        converter.toConnectData(JsonConverterTest.TOPIC, "{ \"schema\": { \"type\": \"boolean\", \"optional\": false }, \"payload\": true }".getBytes());
        Assert.assertEquals(3, cache.size());
    }

    // Schema types
    @Test
    public void booleanToJson() {
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, BOOLEAN_SCHEMA, true));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"boolean\", \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(true, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).booleanValue());
    }

    @Test
    public void byteToJson() {
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, INT8_SCHEMA, ((byte) (12))));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"int8\", \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(12, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).intValue());
    }

    @Test
    public void shortToJson() {
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, INT16_SCHEMA, ((short) (12))));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"int16\", \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(12, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).intValue());
    }

    @Test
    public void intToJson() {
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, INT32_SCHEMA, 12));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"int32\", \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(12, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).intValue());
    }

    @Test
    public void longToJson() {
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, INT64_SCHEMA, 4398046511104L));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"int64\", \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(4398046511104L, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).longValue());
    }

    @Test
    public void floatToJson() {
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, FLOAT32_SCHEMA, 12.34F));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"float\", \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(12.34F, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).floatValue(), 0.001);
    }

    @Test
    public void doubleToJson() {
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, FLOAT64_SCHEMA, 12.34));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"double\", \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(12.34, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).doubleValue(), 0.001);
    }

    @Test
    public void bytesToJson() throws IOException {
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, BYTES_SCHEMA, "test-string".getBytes()));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"bytes\", \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(ByteBuffer.wrap("test-string".getBytes()), ByteBuffer.wrap(converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).binaryValue()));
    }

    @Test
    public void stringToJson() {
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, STRING_SCHEMA, "test-string"));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"string\", \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals("test-string", converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).textValue());
    }

    @Test
    public void arrayToJson() {
        Schema int32Array = SchemaBuilder.array(INT32_SCHEMA).build();
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, int32Array, Arrays.asList(1, 2, 3)));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"array\", \"items\": { \"type\": \"int32\", \"optional\": false }, \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(JsonNodeFactory.instance.arrayNode().add(1).add(2).add(3), converted.get(ENVELOPE_PAYLOAD_FIELD_NAME));
    }

    @Test
    public void mapToJsonStringKeys() {
        Schema stringIntMap = SchemaBuilder.map(STRING_SCHEMA, INT32_SCHEMA).build();
        Map<String, Integer> input = new HashMap<>();
        input.put("key1", 12);
        input.put("key2", 15);
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, stringIntMap, input));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"map\", \"keys\": { \"type\" : \"string\", \"optional\": false }, \"values\": { \"type\" : \"int32\", \"optional\": false }, \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(JsonNodeFactory.instance.objectNode().put("key1", 12).put("key2", 15), converted.get(ENVELOPE_PAYLOAD_FIELD_NAME));
    }

    @Test
    public void mapToJsonNonStringKeys() {
        Schema intIntMap = SchemaBuilder.map(INT32_SCHEMA, INT32_SCHEMA).build();
        Map<Integer, Integer> input = new HashMap<>();
        input.put(1, 12);
        input.put(2, 15);
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, intIntMap, input));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"map\", \"keys\": { \"type\" : \"int32\", \"optional\": false }, \"values\": { \"type\" : \"int32\", \"optional\": false }, \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertTrue(converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).isArray());
        ArrayNode payload = ((ArrayNode) (converted.get(ENVELOPE_PAYLOAD_FIELD_NAME)));
        Assert.assertEquals(2, payload.size());
        Set<JsonNode> payloadEntries = new HashSet<>();
        for (JsonNode elem : payload)
            payloadEntries.add(elem);

        Assert.assertEquals(new HashSet<>(Arrays.asList(JsonNodeFactory.instance.arrayNode().add(1).add(12), JsonNodeFactory.instance.arrayNode().add(2).add(15))), payloadEntries);
    }

    @Test
    public void structToJson() {
        Schema schema = SchemaBuilder.struct().field("field1", BOOLEAN_SCHEMA).field("field2", STRING_SCHEMA).field("field3", STRING_SCHEMA).field("field4", BOOLEAN_SCHEMA).build();
        Struct input = put("field1", true).put("field2", "string2").put("field3", "string3").put("field4", false);
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, schema, input));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"struct\", \"optional\": false, \"fields\": [{ \"field\": \"field1\", \"type\": \"boolean\", \"optional\": false }, { \"field\": \"field2\", \"type\": \"string\", \"optional\": false }, { \"field\": \"field3\", \"type\": \"string\", \"optional\": false }, { \"field\": \"field4\", \"type\": \"boolean\", \"optional\": false }] }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals(JsonNodeFactory.instance.objectNode().put("field1", true).put("field2", "string2").put("field3", "string3").put("field4", false), converted.get(ENVELOPE_PAYLOAD_FIELD_NAME));
    }

    @Test
    public void structSchemaIdentical() {
        Schema schema = SchemaBuilder.struct().field("field1", BOOLEAN_SCHEMA).field("field2", STRING_SCHEMA).field("field3", STRING_SCHEMA).field("field4", BOOLEAN_SCHEMA).build();
        Schema inputSchema = SchemaBuilder.struct().field("field1", BOOLEAN_SCHEMA).field("field2", STRING_SCHEMA).field("field3", STRING_SCHEMA).field("field4", BOOLEAN_SCHEMA).build();
        Struct input = put("field1", true).put("field2", "string2").put("field3", "string3").put("field4", false);
        assertStructSchemaEqual(schema, input);
    }

    @Test
    public void decimalToJson() throws IOException {
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, Decimal.schema(2), new BigDecimal(new BigInteger("156"), 2)));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"bytes\", \"optional\": false, \"name\": \"org.apache.kafka.connect.data.Decimal\", \"version\": 1, \"parameters\": { \"scale\": \"2\" } }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertArrayEquals(new byte[]{ 0, -100 }, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).binaryValue());
    }

    @Test
    public void dateToJson() {
        GregorianCalendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.DATE, 10000);
        java.util.Date date = calendar.getTime();
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, SCHEMA, date));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"int32\", \"optional\": false, \"name\": \"org.apache.kafka.connect.data.Date\", \"version\": 1 }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        JsonNode payload = converted.get(ENVELOPE_PAYLOAD_FIELD_NAME);
        Assert.assertTrue(payload.isInt());
        Assert.assertEquals(10000, payload.intValue());
    }

    @Test
    public void timeToJson() {
        GregorianCalendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.MILLISECOND, 14400000);
        java.util.Date date = calendar.getTime();
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, Time.SCHEMA, date));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"int32\", \"optional\": false, \"name\": \"org.apache.kafka.connect.data.Time\", \"version\": 1 }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        JsonNode payload = converted.get(ENVELOPE_PAYLOAD_FIELD_NAME);
        Assert.assertTrue(payload.isInt());
        Assert.assertEquals(14400000, payload.longValue());
    }

    @Test
    public void timestampToJson() {
        GregorianCalendar calendar = new GregorianCalendar(1970, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.add(Calendar.MILLISECOND, 2000000000);
        calendar.add(Calendar.MILLISECOND, 2000000000);
        java.util.Date date = calendar.getTime();
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, Timestamp.SCHEMA, date));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"int64\", \"optional\": false, \"name\": \"org.apache.kafka.connect.data.Timestamp\", \"version\": 1 }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        JsonNode payload = converted.get(ENVELOPE_PAYLOAD_FIELD_NAME);
        Assert.assertTrue(payload.isLong());
        Assert.assertEquals(4000000000L, payload.longValue());
    }

    @Test
    public void nullSchemaAndPrimitiveToJson() {
        // This still needs to do conversion of data, null schema means "anything goes"
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, null, true));
        validateEnvelopeNullSchema(converted);
        Assert.assertTrue(converted.get(ENVELOPE_SCHEMA_FIELD_NAME).isNull());
        Assert.assertEquals(true, converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).booleanValue());
    }

    @Test
    public void nullSchemaAndArrayToJson() {
        // This still needs to do conversion of data, null schema means "anything goes". Make sure we mix and match
        // types to verify conversion still works.
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, null, Arrays.asList(1, "string", true)));
        validateEnvelopeNullSchema(converted);
        Assert.assertTrue(converted.get(ENVELOPE_SCHEMA_FIELD_NAME).isNull());
        Assert.assertEquals(JsonNodeFactory.instance.arrayNode().add(1).add("string").add(true), converted.get(ENVELOPE_PAYLOAD_FIELD_NAME));
    }

    @Test
    public void nullSchemaAndMapToJson() {
        // This still needs to do conversion of data, null schema means "anything goes". Make sure we mix and match
        // types to verify conversion still works.
        Map<String, Object> input = new HashMap<>();
        input.put("key1", 12);
        input.put("key2", "string");
        input.put("key3", true);
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, null, input));
        validateEnvelopeNullSchema(converted);
        Assert.assertTrue(converted.get(ENVELOPE_SCHEMA_FIELD_NAME).isNull());
        Assert.assertEquals(JsonNodeFactory.instance.objectNode().put("key1", 12).put("key2", "string").put("key3", true), converted.get(ENVELOPE_PAYLOAD_FIELD_NAME));
    }

    @Test
    public void nullSchemaAndMapNonStringKeysToJson() {
        // This still needs to do conversion of data, null schema means "anything goes". Make sure we mix and match
        // types to verify conversion still works.
        Map<Object, Object> input = new HashMap<>();
        input.put("string", 12);
        input.put(52, "string");
        input.put(false, true);
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, null, input));
        validateEnvelopeNullSchema(converted);
        Assert.assertTrue(converted.get(ENVELOPE_SCHEMA_FIELD_NAME).isNull());
        Assert.assertTrue(converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).isArray());
        ArrayNode payload = ((ArrayNode) (converted.get(ENVELOPE_PAYLOAD_FIELD_NAME)));
        Assert.assertEquals(3, payload.size());
        Set<JsonNode> payloadEntries = new HashSet<>();
        for (JsonNode elem : payload)
            payloadEntries.add(elem);

        Assert.assertEquals(new HashSet<>(Arrays.asList(JsonNodeFactory.instance.arrayNode().add("string").add(12), JsonNodeFactory.instance.arrayNode().add(52).add("string"), JsonNodeFactory.instance.arrayNode().add(false).add(true))), payloadEntries);
    }

    @Test
    public void nullSchemaAndNullValueToJson() {
        // This characterizes the production of tombstone messages when Json schemas is enabled
        Map<String, Boolean> props = Collections.singletonMap("schemas.enable", true);
        converter.configure(props, true);
        byte[] converted = converter.fromConnectData(JsonConverterTest.TOPIC, null, null);
        Assert.assertNull(converted);
    }

    @Test
    public void nullValueToJson() {
        // This characterizes the production of tombstone messages when Json schemas is not enabled
        Map<String, Boolean> props = Collections.singletonMap("schemas.enable", false);
        converter.configure(props, true);
        byte[] converted = converter.fromConnectData(JsonConverterTest.TOPIC, null, null);
        Assert.assertNull(converted);
    }

    @Test(expected = DataException.class)
    public void mismatchSchemaJson() {
        // If we have mismatching schema info, we should properly convert to a DataException
        converter.fromConnectData(JsonConverterTest.TOPIC, FLOAT64_SCHEMA, true);
    }

    @Test
    public void noSchemaToConnect() {
        Map<String, Boolean> props = Collections.singletonMap("schemas.enable", false);
        converter.configure(props, true);
        Assert.assertEquals(new SchemaAndValue(null, true), converter.toConnectData(JsonConverterTest.TOPIC, "true".getBytes()));
    }

    @Test
    public void noSchemaToJson() {
        Map<String, Boolean> props = Collections.singletonMap("schemas.enable", false);
        converter.configure(props, true);
        JsonNode converted = parse(converter.fromConnectData(JsonConverterTest.TOPIC, null, true));
        Assert.assertTrue(converted.isBoolean());
        Assert.assertEquals(true, converted.booleanValue());
    }

    @Test
    public void testCacheSchemaToJsonConversion() {
        Cache<Schema, ObjectNode> cache = Whitebox.getInternalState(converter, "fromConnectSchemaCache");
        Assert.assertEquals(0, cache.size());
        // Repeated conversion of the same schema, even if the schema object is different should return the same Java
        // object
        converter.fromConnectData(JsonConverterTest.TOPIC, SchemaBuilder.bool().build(), true);
        Assert.assertEquals(1, cache.size());
        converter.fromConnectData(JsonConverterTest.TOPIC, SchemaBuilder.bool().build(), true);
        Assert.assertEquals(1, cache.size());
        // Validate that a similar, but different schema correctly returns a different schema.
        converter.fromConnectData(JsonConverterTest.TOPIC, SchemaBuilder.bool().optional().build(), true);
        Assert.assertEquals(2, cache.size());
    }

    @Test
    public void testJsonSchemaCacheSizeFromConfigFile() throws IOException, URISyntaxException {
        URL url = getClass().getResource("/connect-test.properties");
        File propFile = new File(url.toURI());
        String workerPropsFile = propFile.getAbsolutePath();
        Map<String, String> workerProps = (!(workerPropsFile.isEmpty())) ? Utils.propsToStringMap(Utils.loadProps(workerPropsFile)) : Collections.<String, String>emptyMap();
        JsonConverter rc = new JsonConverter();
        rc.configure(workerProps, false);
    }

    // Note: the header conversion methods delegates to the data conversion methods, which are tested above.
    // The following simply verify that the delegation works.
    @Test
    public void testStringHeaderToJson() {
        JsonNode converted = parse(converter.fromConnectHeader(JsonConverterTest.TOPIC, "headerName", STRING_SCHEMA, "test-string"));
        validateEnvelope(converted);
        Assert.assertEquals(parse("{ \"type\": \"string\", \"optional\": false }"), converted.get(ENVELOPE_SCHEMA_FIELD_NAME));
        Assert.assertEquals("test-string", converted.get(ENVELOPE_PAYLOAD_FIELD_NAME).textValue());
    }

    @Test
    public void stringHeaderToConnect() {
        Assert.assertEquals(new SchemaAndValue(Schema.STRING_SCHEMA, "foo-bar-baz"), converter.toConnectHeader(JsonConverterTest.TOPIC, "headerName", "{ \"schema\": { \"type\": \"string\" }, \"payload\": \"foo-bar-baz\" }".getBytes()));
    }
}

