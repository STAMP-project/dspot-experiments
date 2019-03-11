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
package org.apache.kafka.connect.data;


import Schema.INT8_SCHEMA;
import Schema.STRING_SCHEMA;
import Schema.Type.ARRAY;
import Schema.Type.BOOLEAN;
import Schema.Type.BYTES;
import Schema.Type.FLOAT32;
import Schema.Type.FLOAT64;
import Schema.Type.INT16;
import Schema.Type.INT32;
import Schema.Type.INT64;
import Schema.Type.INT8;
import Schema.Type.MAP;
import Schema.Type.STRING;
import Schema.Type.STRUCT;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.connect.errors.SchemaBuilderException;
import org.junit.Assert;
import org.junit.Test;


public class SchemaBuilderTest {
    private static final String NAME = "name";

    private static final Integer VERSION = 2;

    private static final String DOC = "doc";

    private static final Map<String, String> NO_PARAMS = null;

    @Test
    public void testInt8Builder() {
        Schema schema = SchemaBuilder.int8().build();
        assertTypeAndDefault(schema, INT8, false, null);
        assertNoMetadata(schema);
        schema = SchemaBuilder.int8().name(SchemaBuilderTest.NAME).optional().defaultValue(((byte) (12))).version(SchemaBuilderTest.VERSION).doc(SchemaBuilderTest.DOC).build();
        assertTypeAndDefault(schema, INT8, true, ((byte) (12)));
        assertMetadata(schema, SchemaBuilderTest.NAME, SchemaBuilderTest.VERSION, SchemaBuilderTest.DOC, SchemaBuilderTest.NO_PARAMS);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testInt8BuilderInvalidDefault() {
        SchemaBuilder.int8().defaultValue("invalid");
    }

    @Test
    public void testInt16Builder() {
        Schema schema = SchemaBuilder.int16().build();
        assertTypeAndDefault(schema, INT16, false, null);
        assertNoMetadata(schema);
        schema = SchemaBuilder.int16().name(SchemaBuilderTest.NAME).optional().defaultValue(((short) (12))).version(SchemaBuilderTest.VERSION).doc(SchemaBuilderTest.DOC).build();
        assertTypeAndDefault(schema, INT16, true, ((short) (12)));
        assertMetadata(schema, SchemaBuilderTest.NAME, SchemaBuilderTest.VERSION, SchemaBuilderTest.DOC, SchemaBuilderTest.NO_PARAMS);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testInt16BuilderInvalidDefault() {
        SchemaBuilder.int16().defaultValue("invalid");
    }

    @Test
    public void testInt32Builder() {
        Schema schema = SchemaBuilder.int32().build();
        assertTypeAndDefault(schema, INT32, false, null);
        assertNoMetadata(schema);
        schema = SchemaBuilder.int32().name(SchemaBuilderTest.NAME).optional().defaultValue(12).version(SchemaBuilderTest.VERSION).doc(SchemaBuilderTest.DOC).build();
        assertTypeAndDefault(schema, INT32, true, 12);
        assertMetadata(schema, SchemaBuilderTest.NAME, SchemaBuilderTest.VERSION, SchemaBuilderTest.DOC, SchemaBuilderTest.NO_PARAMS);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testInt32BuilderInvalidDefault() {
        SchemaBuilder.int32().defaultValue("invalid");
    }

    @Test
    public void testInt64Builder() {
        Schema schema = SchemaBuilder.int64().build();
        assertTypeAndDefault(schema, INT64, false, null);
        assertNoMetadata(schema);
        schema = SchemaBuilder.int64().name(SchemaBuilderTest.NAME).optional().defaultValue(((long) (12))).version(SchemaBuilderTest.VERSION).doc(SchemaBuilderTest.DOC).build();
        assertTypeAndDefault(schema, INT64, true, ((long) (12)));
        assertMetadata(schema, SchemaBuilderTest.NAME, SchemaBuilderTest.VERSION, SchemaBuilderTest.DOC, SchemaBuilderTest.NO_PARAMS);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testInt64BuilderInvalidDefault() {
        SchemaBuilder.int64().defaultValue("invalid");
    }

    @Test
    public void testFloatBuilder() {
        Schema schema = SchemaBuilder.float32().build();
        assertTypeAndDefault(schema, FLOAT32, false, null);
        assertNoMetadata(schema);
        schema = SchemaBuilder.float32().name(SchemaBuilderTest.NAME).optional().defaultValue(12.0F).version(SchemaBuilderTest.VERSION).doc(SchemaBuilderTest.DOC).build();
        assertTypeAndDefault(schema, FLOAT32, true, 12.0F);
        assertMetadata(schema, SchemaBuilderTest.NAME, SchemaBuilderTest.VERSION, SchemaBuilderTest.DOC, SchemaBuilderTest.NO_PARAMS);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testFloatBuilderInvalidDefault() {
        SchemaBuilder.float32().defaultValue("invalid");
    }

    @Test
    public void testDoubleBuilder() {
        Schema schema = SchemaBuilder.float64().build();
        assertTypeAndDefault(schema, FLOAT64, false, null);
        assertNoMetadata(schema);
        schema = SchemaBuilder.float64().name(SchemaBuilderTest.NAME).optional().defaultValue(12.0).version(SchemaBuilderTest.VERSION).doc(SchemaBuilderTest.DOC).build();
        assertTypeAndDefault(schema, FLOAT64, true, 12.0);
        assertMetadata(schema, SchemaBuilderTest.NAME, SchemaBuilderTest.VERSION, SchemaBuilderTest.DOC, SchemaBuilderTest.NO_PARAMS);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testDoubleBuilderInvalidDefault() {
        SchemaBuilder.float64().defaultValue("invalid");
    }

    @Test
    public void testBooleanBuilder() {
        Schema schema = SchemaBuilder.bool().build();
        assertTypeAndDefault(schema, BOOLEAN, false, null);
        assertNoMetadata(schema);
        schema = SchemaBuilder.bool().name(SchemaBuilderTest.NAME).optional().defaultValue(true).version(SchemaBuilderTest.VERSION).doc(SchemaBuilderTest.DOC).build();
        assertTypeAndDefault(schema, BOOLEAN, true, true);
        assertMetadata(schema, SchemaBuilderTest.NAME, SchemaBuilderTest.VERSION, SchemaBuilderTest.DOC, SchemaBuilderTest.NO_PARAMS);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testBooleanBuilderInvalidDefault() {
        SchemaBuilder.bool().defaultValue("invalid");
    }

    @Test
    public void testStringBuilder() {
        Schema schema = SchemaBuilder.string().build();
        assertTypeAndDefault(schema, STRING, false, null);
        assertNoMetadata(schema);
        schema = SchemaBuilder.string().name(SchemaBuilderTest.NAME).optional().defaultValue("a default string").version(SchemaBuilderTest.VERSION).doc(SchemaBuilderTest.DOC).build();
        assertTypeAndDefault(schema, STRING, true, "a default string");
        assertMetadata(schema, SchemaBuilderTest.NAME, SchemaBuilderTest.VERSION, SchemaBuilderTest.DOC, SchemaBuilderTest.NO_PARAMS);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testStringBuilderInvalidDefault() {
        SchemaBuilder.string().defaultValue(true);
    }

    @Test
    public void testBytesBuilder() {
        Schema schema = SchemaBuilder.bytes().build();
        assertTypeAndDefault(schema, BYTES, false, null);
        assertNoMetadata(schema);
        schema = SchemaBuilder.bytes().name(SchemaBuilderTest.NAME).optional().defaultValue("a default byte array".getBytes()).version(SchemaBuilderTest.VERSION).doc(SchemaBuilderTest.DOC).build();
        assertTypeAndDefault(schema, BYTES, true, "a default byte array".getBytes());
        assertMetadata(schema, SchemaBuilderTest.NAME, SchemaBuilderTest.VERSION, SchemaBuilderTest.DOC, SchemaBuilderTest.NO_PARAMS);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testBytesBuilderInvalidDefault() {
        SchemaBuilder.bytes().defaultValue("a string, not bytes");
    }

    @Test
    public void testParameters() {
        Map<String, String> expectedParameters = new HashMap<>();
        expectedParameters.put("foo", "val");
        expectedParameters.put("bar", "baz");
        Schema schema = SchemaBuilder.string().parameter("foo", "val").parameter("bar", "baz").build();
        assertTypeAndDefault(schema, STRING, false, null);
        assertMetadata(schema, null, null, null, expectedParameters);
        schema = SchemaBuilder.string().parameters(expectedParameters).build();
        assertTypeAndDefault(schema, STRING, false, null);
        assertMetadata(schema, null, null, null, expectedParameters);
    }

    @Test
    public void testStructBuilder() {
        Schema schema = SchemaBuilder.struct().field("field1", INT8_SCHEMA).field("field2", INT8_SCHEMA).build();
        assertTypeAndDefault(schema, STRUCT, false, null);
        Assert.assertEquals(2, schema.fields().size());
        Assert.assertEquals("field1", schema.fields().get(0).name());
        Assert.assertEquals(0, schema.fields().get(0).index());
        Assert.assertEquals(INT8_SCHEMA, schema.fields().get(0).schema());
        Assert.assertEquals("field2", schema.fields().get(1).name());
        Assert.assertEquals(1, schema.fields().get(1).index());
        Assert.assertEquals(INT8_SCHEMA, schema.fields().get(1).schema());
        assertNoMetadata(schema);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testNonStructCantHaveFields() {
        SchemaBuilder.int8().field("field", SchemaBuilder.int8().build());
    }

    @Test
    public void testArrayBuilder() {
        Schema schema = SchemaBuilder.array(INT8_SCHEMA).build();
        assertTypeAndDefault(schema, ARRAY, false, null);
        Assert.assertEquals(schema.valueSchema(), INT8_SCHEMA);
        assertNoMetadata(schema);
        // Default value
        List<Byte> defArray = Arrays.asList(((byte) (1)), ((byte) (2)));
        schema = SchemaBuilder.array(INT8_SCHEMA).defaultValue(defArray).build();
        assertTypeAndDefault(schema, ARRAY, false, defArray);
        Assert.assertEquals(schema.valueSchema(), INT8_SCHEMA);
        assertNoMetadata(schema);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testArrayBuilderInvalidDefault() {
        // Array, but wrong embedded type
        SchemaBuilder.array(INT8_SCHEMA).defaultValue(Arrays.asList("string")).build();
    }

    @Test
    public void testMapBuilder() {
        // SchemaBuilder should also pass the check
        Schema schema = SchemaBuilder.map(INT8_SCHEMA, INT8_SCHEMA);
        assertTypeAndDefault(schema, MAP, false, null);
        Assert.assertEquals(schema.keySchema(), INT8_SCHEMA);
        Assert.assertEquals(schema.valueSchema(), INT8_SCHEMA);
        assertNoMetadata(schema);
        schema = SchemaBuilder.map(INT8_SCHEMA, INT8_SCHEMA).build();
        assertTypeAndDefault(schema, MAP, false, null);
        Assert.assertEquals(schema.keySchema(), INT8_SCHEMA);
        Assert.assertEquals(schema.valueSchema(), INT8_SCHEMA);
        assertNoMetadata(schema);
        // Default value
        Map<Byte, Byte> defMap = Collections.singletonMap(((byte) (5)), ((byte) (10)));
        schema = SchemaBuilder.map(INT8_SCHEMA, INT8_SCHEMA).defaultValue(defMap).build();
        assertTypeAndDefault(schema, MAP, false, defMap);
        Assert.assertEquals(schema.keySchema(), INT8_SCHEMA);
        Assert.assertEquals(schema.valueSchema(), INT8_SCHEMA);
        assertNoMetadata(schema);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testMapBuilderInvalidDefault() {
        // Map, but wrong embedded type
        Map<Byte, String> defMap = Collections.singletonMap(((byte) (5)), "foo");
        SchemaBuilder.map(INT8_SCHEMA, INT8_SCHEMA).defaultValue(defMap).build();
    }

    @Test
    public void testEmptyStruct() {
        final SchemaBuilder emptyStructSchemaBuilder = SchemaBuilder.struct();
        Assert.assertEquals(0, emptyStructSchemaBuilder.fields().size());
        new Struct(emptyStructSchemaBuilder);
        final Schema emptyStructSchema = emptyStructSchemaBuilder.build();
        Assert.assertEquals(0, emptyStructSchema.fields().size());
        new Struct(emptyStructSchema);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testDuplicateFields() {
        final Schema schema = SchemaBuilder.struct().name("testing").field("id", SchemaBuilder.string().doc("").build()).field("id", SchemaBuilder.string().doc("").build()).build();
        final Struct struct = put("id", "testing");
        struct.validate();
    }

    @Test
    public void testDefaultFieldsSameValueOverwriting() {
        final SchemaBuilder schemaBuilder = SchemaBuilder.string().name("testing").version(123);
        schemaBuilder.name("testing");
        schemaBuilder.version(123);
        Assert.assertEquals("testing", schemaBuilder.name());
    }

    @Test(expected = SchemaBuilderException.class)
    public void testDefaultFieldsDifferentValueOverwriting() {
        final SchemaBuilder schemaBuilder = SchemaBuilder.string().name("testing").version(123);
        schemaBuilder.name("testing");
        schemaBuilder.version(456);
    }

    @Test(expected = SchemaBuilderException.class)
    public void testFieldNameNull() {
        Schema schema = SchemaBuilder.struct().field(null, STRING_SCHEMA).build();
    }

    @Test(expected = SchemaBuilderException.class)
    public void testFieldSchemaNull() {
        Schema schema = SchemaBuilder.struct().field("fieldName", null).build();
    }

    @Test(expected = SchemaBuilderException.class)
    public void testArraySchemaNull() {
        Schema schema = SchemaBuilder.array(null).build();
    }

    @Test(expected = SchemaBuilderException.class)
    public void testMapKeySchemaNull() {
        Schema schema = SchemaBuilder.map(null, STRING_SCHEMA).build();
    }

    @Test(expected = SchemaBuilderException.class)
    public void testMapValueSchemaNull() {
        Schema schema = SchemaBuilder.map(STRING_SCHEMA, null).build();
    }

    @Test(expected = SchemaBuilderException.class)
    public void testTypeNotNull() {
        SchemaBuilder.type(null);
    }
}

