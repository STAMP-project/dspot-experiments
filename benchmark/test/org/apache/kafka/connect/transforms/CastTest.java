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
package org.apache.kafka.connect.transforms;


import Cast.SPEC_CONFIG;
import Schema.BOOLEAN_SCHEMA;
import Schema.FLOAT32_SCHEMA;
import Schema.FLOAT64_SCHEMA;
import Schema.INT16_SCHEMA;
import Schema.INT32_SCHEMA;
import Schema.INT64_SCHEMA;
import Schema.INT8_SCHEMA;
import Schema.OPTIONAL_FLOAT32_SCHEMA;
import Schema.OPTIONAL_INT16_SCHEMA;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.STRING_SCHEMA;
import Schema.Type.BOOLEAN;
import Schema.Type.FLOAT32;
import Schema.Type.FLOAT64;
import Schema.Type.INT16;
import Schema.Type.INT32;
import Schema.Type.INT64;
import Schema.Type.INT8;
import Schema.Type.STRING;
import Timestamp.SCHEMA;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.data.Decimal;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.data.Values;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.Assert;
import org.junit.Test;


public class CastTest {
    private final Cast<SourceRecord> xformKey = new Cast.Key<>();

    private final Cast<SourceRecord> xformValue = new Cast.Value<>();

    private static final long MILLIS_PER_DAY = ((24 * 60) * 60) * 1000;

    @Test(expected = ConfigException.class)
    public void testConfigEmpty() {
        xformKey.configure(Collections.singletonMap(SPEC_CONFIG, ""));
    }

    @Test(expected = ConfigException.class)
    public void testConfigInvalidSchemaType() {
        xformKey.configure(Collections.singletonMap(SPEC_CONFIG, "foo:faketype"));
    }

    @Test(expected = ConfigException.class)
    public void testConfigInvalidTargetType() {
        xformKey.configure(Collections.singletonMap(SPEC_CONFIG, "foo:array"));
    }

    @Test(expected = ConfigException.class)
    public void testUnsupportedTargetType() {
        xformKey.configure(Collections.singletonMap(SPEC_CONFIG, "foo:bytes"));
    }

    @Test(expected = ConfigException.class)
    public void testConfigInvalidMap() {
        xformKey.configure(Collections.singletonMap(SPEC_CONFIG, "foo:int8:extra"));
    }

    @Test(expected = ConfigException.class)
    public void testConfigMixWholeAndFieldTransformation() {
        xformKey.configure(Collections.singletonMap(SPEC_CONFIG, "foo:int8,int32"));
    }

    @Test
    public void castWholeRecordKeyWithSchema() {
        xformKey.configure(Collections.singletonMap(SPEC_CONFIG, "int8"));
        SourceRecord transformed = xformKey.apply(new SourceRecord(null, null, "topic", 0, Schema.INT32_SCHEMA, 42, Schema.STRING_SCHEMA, "bogus"));
        Assert.assertEquals(INT8, transformed.keySchema().type());
        Assert.assertEquals(((byte) (42)), transformed.key());
    }

    @Test
    public void castWholeRecordValueWithSchemaInt8() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int8"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Schema.INT32_SCHEMA, 42));
        Assert.assertEquals(INT8, transformed.valueSchema().type());
        Assert.assertEquals(((byte) (42)), transformed.value());
    }

    @Test
    public void castWholeRecordValueWithSchemaInt16() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int16"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Schema.INT32_SCHEMA, 42));
        Assert.assertEquals(INT16, transformed.valueSchema().type());
        Assert.assertEquals(((short) (42)), transformed.value());
    }

    @Test
    public void castWholeRecordValueWithSchemaInt32() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int32"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Schema.INT32_SCHEMA, 42));
        Assert.assertEquals(INT32, transformed.valueSchema().type());
        Assert.assertEquals(42, transformed.value());
    }

    @Test
    public void castWholeRecordValueWithSchemaInt64() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int64"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Schema.INT32_SCHEMA, 42));
        Assert.assertEquals(INT64, transformed.valueSchema().type());
        Assert.assertEquals(((long) (42)), transformed.value());
    }

    @Test
    public void castWholeRecordValueWithSchemaFloat32() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "float32"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Schema.INT32_SCHEMA, 42));
        Assert.assertEquals(FLOAT32, transformed.valueSchema().type());
        Assert.assertEquals(42.0F, transformed.value());
    }

    @Test
    public void castWholeRecordValueWithSchemaFloat64() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "float64"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Schema.INT32_SCHEMA, 42));
        Assert.assertEquals(FLOAT64, transformed.valueSchema().type());
        Assert.assertEquals(42.0, transformed.value());
    }

    @Test
    public void castWholeRecordValueWithSchemaBooleanTrue() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "boolean"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Schema.INT32_SCHEMA, 42));
        Assert.assertEquals(BOOLEAN, transformed.valueSchema().type());
        Assert.assertEquals(true, transformed.value());
    }

    @Test
    public void castWholeRecordValueWithSchemaBooleanFalse() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "boolean"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Schema.INT32_SCHEMA, 0));
        Assert.assertEquals(BOOLEAN, transformed.valueSchema().type());
        Assert.assertEquals(false, transformed.value());
    }

    @Test
    public void castWholeRecordValueWithSchemaString() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "string"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Schema.INT32_SCHEMA, 42));
        Assert.assertEquals(STRING, transformed.valueSchema().type());
        Assert.assertEquals("42", transformed.value());
    }

    @Test
    public void castWholeBigDecimalRecordValueWithSchemaString() {
        BigDecimal bigDecimal = new BigDecimal(42);
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "string"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Decimal.schema(bigDecimal.scale()), bigDecimal));
        Assert.assertEquals(STRING, transformed.valueSchema().type());
        Assert.assertEquals("42", transformed.value());
    }

    @Test
    public void castWholeDateRecordValueWithSchemaString() {
        Date timestamp = new Date(((CastTest.MILLIS_PER_DAY) + 1));// day + 1msec to get a timestamp formatting.

        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "string"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, Timestamp.SCHEMA, timestamp));
        Assert.assertEquals(STRING, transformed.valueSchema().type());
        Assert.assertEquals(Values.dateFormatFor(timestamp).format(timestamp), transformed.value());
    }

    @Test
    public void castWholeRecordDefaultValue() {
        // Validate default value in schema is correctly converted
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int32"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, SchemaBuilder.float32().defaultValue((-42.125F)).build(), 42.125F));
        Assert.assertEquals(INT32, transformed.valueSchema().type());
        Assert.assertEquals(42, transformed.value());
        Assert.assertEquals((-42), transformed.valueSchema().defaultValue());
    }

    @Test
    public void castWholeRecordKeySchemaless() {
        xformKey.configure(Collections.singletonMap(SPEC_CONFIG, "int8"));
        SourceRecord transformed = xformKey.apply(new SourceRecord(null, null, "topic", 0, null, 42, Schema.STRING_SCHEMA, "bogus"));
        Assert.assertNull(transformed.keySchema());
        Assert.assertEquals(((byte) (42)), transformed.key());
    }

    @Test
    public void castWholeRecordValueSchemalessInt8() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int8"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, 42));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(((byte) (42)), transformed.value());
    }

    @Test
    public void castWholeRecordValueSchemalessInt16() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int16"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, 42));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(((short) (42)), transformed.value());
    }

    @Test
    public void castWholeRecordValueSchemalessInt32() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int32"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, 42));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(42, transformed.value());
    }

    @Test
    public void castWholeRecordValueSchemalessInt64() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int64"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, 42));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(((long) (42)), transformed.value());
    }

    @Test
    public void castWholeRecordValueSchemalessFloat32() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "float32"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, 42));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(42.0F, transformed.value());
    }

    @Test
    public void castWholeRecordValueSchemalessFloat64() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "float64"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, 42));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(42.0, transformed.value());
    }

    @Test
    public void castWholeRecordValueSchemalessBooleanTrue() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "boolean"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, 42));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(true, transformed.value());
    }

    @Test
    public void castWholeRecordValueSchemalessBooleanFalse() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "boolean"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, 0));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(false, transformed.value());
    }

    @Test
    public void castWholeRecordValueSchemalessString() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "string"));
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, 42));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals("42", transformed.value());
    }

    @Test(expected = DataException.class)
    public void castWholeRecordValueSchemalessUnsupportedType() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int8"));
        xformValue.apply(new SourceRecord(null, null, "topic", 0, null, Collections.singletonList("foo")));
    }

    @Test
    public void castFieldsWithSchema() {
        Date day = new Date(CastTest.MILLIS_PER_DAY);
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int8:int16,int16:int32,int32:int64,int64:boolean,float32:float64,float64:boolean,boolean:int8,string:int32,bigdecimal:string,date:string,optional:int32"));
        // Include an optional fields and fields with defaults to validate their values are passed through properly
        SchemaBuilder builder = SchemaBuilder.struct();
        builder.field("int8", INT8_SCHEMA);
        builder.field("int16", OPTIONAL_INT16_SCHEMA);
        builder.field("int32", SchemaBuilder.int32().defaultValue(2).build());
        builder.field("int64", INT64_SCHEMA);
        builder.field("float32", FLOAT32_SCHEMA);
        // Default value here ensures we correctly convert default values
        builder.field("float64", SchemaBuilder.float64().defaultValue((-1.125)).build());
        builder.field("boolean", BOOLEAN_SCHEMA);
        builder.field("string", STRING_SCHEMA);
        builder.field("bigdecimal", Decimal.schema(new BigDecimal(42).scale()));
        builder.field("date", SCHEMA);
        builder.field("optional", OPTIONAL_FLOAT32_SCHEMA);
        builder.field("timestamp", SCHEMA);
        Schema supportedTypesSchema = builder.build();
        Struct recordValue = new Struct(supportedTypesSchema);
        recordValue.put("int8", ((byte) (8)));
        recordValue.put("int16", ((short) (16)));
        recordValue.put("int32", 32);
        recordValue.put("int64", ((long) (64)));
        recordValue.put("float32", 32.0F);
        recordValue.put("float64", (-64.0));
        recordValue.put("boolean", true);
        recordValue.put("bigdecimal", new BigDecimal(42));
        recordValue.put("date", day);
        recordValue.put("string", "42");
        recordValue.put("timestamp", new Date(0));
        // optional field intentionally omitted
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, supportedTypesSchema, recordValue));
        Assert.assertEquals(((short) (8)), get("int8"));
        Assert.assertTrue(schema().isOptional());
        Assert.assertEquals(16, get("int16"));
        Assert.assertEquals(((long) (32)), get("int32"));
        Assert.assertEquals(2L, schema().defaultValue());
        Assert.assertEquals(true, get("int64"));
        Assert.assertEquals(32.0, get("float32"));
        Assert.assertEquals(true, get("float64"));
        Assert.assertEquals(true, schema().defaultValue());
        Assert.assertEquals(((byte) (1)), get("boolean"));
        Assert.assertEquals(42, get("string"));
        Assert.assertEquals("42", get("bigdecimal"));
        Assert.assertEquals(Values.dateFormatFor(day).format(day), get("date"));
        Assert.assertEquals(new Date(0), get("timestamp"));
        Assert.assertNull(get("optional"));
        Schema transformedSchema = ((Struct) (transformed.value())).schema();
        Assert.assertEquals(INT16_SCHEMA.type(), schema().type());
        Assert.assertEquals(OPTIONAL_INT32_SCHEMA.type(), schema().type());
        Assert.assertEquals(INT64_SCHEMA.type(), schema().type());
        Assert.assertEquals(BOOLEAN_SCHEMA.type(), schema().type());
        Assert.assertEquals(FLOAT64_SCHEMA.type(), schema().type());
        Assert.assertEquals(BOOLEAN_SCHEMA.type(), schema().type());
        Assert.assertEquals(INT8_SCHEMA.type(), schema().type());
        Assert.assertEquals(INT32_SCHEMA.type(), schema().type());
        Assert.assertEquals(STRING_SCHEMA.type(), schema().type());
        Assert.assertEquals(STRING_SCHEMA.type(), schema().type());
        Assert.assertEquals(OPTIONAL_INT32_SCHEMA.type(), schema().type());
        // The following fields are not changed
        Assert.assertEquals(SCHEMA.type(), schema().type());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void castFieldsSchemaless() {
        xformValue.configure(Collections.singletonMap(SPEC_CONFIG, "int8:int16,int16:int32,int32:int64,int64:boolean,float32:float64,float64:boolean,boolean:int8,string:int32"));
        Map<String, Object> recordValue = new HashMap<>();
        recordValue.put("int8", ((byte) (8)));
        recordValue.put("int16", ((short) (16)));
        recordValue.put("int32", 32);
        recordValue.put("int64", ((long) (64)));
        recordValue.put("float32", 32.0F);
        recordValue.put("float64", (-64.0));
        recordValue.put("boolean", true);
        recordValue.put("string", "42");
        SourceRecord transformed = xformValue.apply(new SourceRecord(null, null, "topic", 0, null, recordValue));
        Assert.assertNull(transformed.valueSchema());
        Assert.assertEquals(((short) (8)), ((Map<String, Object>) (transformed.value())).get("int8"));
        Assert.assertEquals(16, ((Map<String, Object>) (transformed.value())).get("int16"));
        Assert.assertEquals(((long) (32)), ((Map<String, Object>) (transformed.value())).get("int32"));
        Assert.assertEquals(true, ((Map<String, Object>) (transformed.value())).get("int64"));
        Assert.assertEquals(32.0, ((Map<String, Object>) (transformed.value())).get("float32"));
        Assert.assertEquals(true, ((Map<String, Object>) (transformed.value())).get("float64"));
        Assert.assertEquals(((byte) (1)), ((Map<String, Object>) (transformed.value())).get("boolean"));
        Assert.assertEquals(42, ((Map<String, Object>) (transformed.value())).get("string"));
    }
}

