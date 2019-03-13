/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.schemas;


import FieldType.BOOLEAN;
import FieldType.BYTE;
import FieldType.DATETIME;
import FieldType.DECIMAL;
import FieldType.DOUBLE;
import FieldType.FLOAT;
import FieldType.INT16;
import FieldType.INT32;
import FieldType.INT64;
import FieldType.STRING;
import java.util.stream.Stream;
import org.apache.beam.sdk.schemas.LogicalTypes.PassThroughLogicalType;
import org.apache.beam.sdk.schemas.Schema.Field;
import org.apache.beam.sdk.schemas.Schema.FieldType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link Schema}.
 */
public class SchemaTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreate() {
        Schema schema = Schema.builder().addByteField("f_byte").addInt16Field("f_int16").addInt32Field("f_int32").addInt64Field("f_int64").addDecimalField("f_decimal").addFloatField("f_float").addDoubleField("f_double").addStringField("f_string").addDateTimeField("f_datetime").addBooleanField("f_boolean").build();
        Assert.assertEquals(10, schema.getFieldCount());
        Assert.assertEquals(0, schema.indexOf("f_byte"));
        Assert.assertEquals("f_byte", schema.getField(0).getName());
        Assert.assertEquals(BYTE, schema.getField(0).getType());
        Assert.assertEquals(1, schema.indexOf("f_int16"));
        Assert.assertEquals("f_int16", schema.getField(1).getName());
        Assert.assertEquals(INT16, schema.getField(1).getType());
        Assert.assertEquals(2, schema.indexOf("f_int32"));
        Assert.assertEquals("f_int32", schema.getField(2).getName());
        Assert.assertEquals(INT32, schema.getField(2).getType());
        Assert.assertEquals(3, schema.indexOf("f_int64"));
        Assert.assertEquals("f_int64", schema.getField(3).getName());
        Assert.assertEquals(INT64, schema.getField(3).getType());
        Assert.assertEquals(4, schema.indexOf("f_decimal"));
        Assert.assertEquals("f_decimal", schema.getField(4).getName());
        Assert.assertEquals(DECIMAL, schema.getField(4).getType());
        Assert.assertEquals(5, schema.indexOf("f_float"));
        Assert.assertEquals("f_float", schema.getField(5).getName());
        Assert.assertEquals(FLOAT, schema.getField(5).getType());
        Assert.assertEquals(6, schema.indexOf("f_double"));
        Assert.assertEquals("f_double", schema.getField(6).getName());
        Assert.assertEquals(DOUBLE, schema.getField(6).getType());
        Assert.assertEquals(7, schema.indexOf("f_string"));
        Assert.assertEquals("f_string", schema.getField(7).getName());
        Assert.assertEquals(STRING, schema.getField(7).getType());
        Assert.assertEquals(8, schema.indexOf("f_datetime"));
        Assert.assertEquals("f_datetime", schema.getField(8).getName());
        Assert.assertEquals(DATETIME, schema.getField(8).getType());
        Assert.assertEquals(9, schema.indexOf("f_boolean"));
        Assert.assertEquals("f_boolean", schema.getField(9).getName());
        Assert.assertEquals(BOOLEAN, schema.getField(9).getType());
    }

    @Test
    public void testNestedSchema() {
        Schema nestedSchema = Schema.of(Field.of("f1_str", STRING));
        Schema schema = Schema.of(Field.of("nested", FieldType.row(nestedSchema)));
        Field inner = schema.getField("nested").getType().getRowSchema().getField("f1_str");
        Assert.assertEquals("f1_str", inner.getName());
        Assert.assertEquals(STRING, inner.getType());
    }

    @Test
    public void testArraySchema() {
        FieldType arrayType = FieldType.array(STRING);
        Schema schema = Schema.of(Field.of("f_array", arrayType));
        Field field = schema.getField("f_array");
        Assert.assertEquals("f_array", field.getName());
        Assert.assertEquals(arrayType, field.getType());
    }

    @Test
    public void testArrayOfRowSchema() {
        Schema nestedSchema = Schema.of(Field.of("f1_str", STRING));
        FieldType arrayType = FieldType.array(FieldType.row(nestedSchema));
        Schema schema = Schema.of(Field.of("f_array", arrayType));
        Field field = schema.getField("f_array");
        Assert.assertEquals("f_array", field.getName());
        Assert.assertEquals(arrayType, field.getType());
    }

    @Test
    public void testNestedArraySchema() {
        FieldType arrayType = FieldType.array(FieldType.array(STRING));
        Schema schema = Schema.of(Field.of("f_array", arrayType));
        Field field = schema.getField("f_array");
        Assert.assertEquals("f_array", field.getName());
        Assert.assertEquals(arrayType, field.getType());
    }

    @Test
    public void testWrongName() {
        Schema schema = Schema.of(Field.of("f_byte", BYTE));
        thrown.expect(IllegalArgumentException.class);
        schema.getField("f_string");
    }

    @Test
    public void testWrongIndex() {
        Schema schema = Schema.of(Field.of("f_byte", BYTE));
        thrown.expect(IndexOutOfBoundsException.class);
        schema.getField(1);
    }

    @Test
    public void testCollector() {
        Schema schema = Stream.of(Schema.Field.of("f_int", INT32), Schema.Field.of("f_string", STRING)).collect(Schema.toSchema());
        Assert.assertEquals(2, schema.getFieldCount());
        Assert.assertEquals("f_int", schema.getField(0).getName());
        Assert.assertEquals(INT32, schema.getField(0).getType());
        Assert.assertEquals("f_string", schema.getField(1).getName());
        Assert.assertEquals(STRING, schema.getField(1).getType());
    }

    @Test
    public void testEquivalent() {
        final Schema expectedNested1 = Schema.builder().addStringField("yard1").addInt64Field("yard2").build();
        final Schema expectedSchema1 = Schema.builder().addStringField("field1").addInt64Field("field2").addRowField("field3", expectedNested1).addArrayField("field4", FieldType.row(expectedNested1)).addMapField("field5", STRING, FieldType.row(expectedNested1)).build();
        final Schema expectedNested2 = Schema.builder().addInt64Field("yard2").addStringField("yard1").build();
        final Schema expectedSchema2 = Schema.builder().addMapField("field5", STRING, FieldType.row(expectedNested2)).addArrayField("field4", FieldType.row(expectedNested2)).addRowField("field3", expectedNested2).addInt64Field("field2").addStringField("field1").build();
        Assert.assertNotEquals(expectedSchema1, expectedSchema2);
        Assert.assertTrue(expectedSchema1.equivalent(expectedSchema2));
    }

    @Test
    public void testPrimitiveNotEquivalent() {
        Schema schema1 = Schema.builder().addInt64Field("foo").build();
        Schema schema2 = Schema.builder().addStringField("foo").build();
        Assert.assertNotEquals(schema1, schema2);
        Assert.assertFalse(schema1.equivalent(schema2));
        schema1 = Schema.builder().addInt64Field("foo").build();
        schema2 = Schema.builder().addInt64Field("bar").build();
        Assert.assertNotEquals(schema1, schema2);
        Assert.assertFalse(schema1.equivalent(schema2));
        schema1 = Schema.builder().addInt64Field("foo").build();
        schema2 = Schema.builder().addNullableField("foo", INT64).build();
        Assert.assertNotEquals(schema1, schema2);
        Assert.assertFalse(schema1.equivalent(schema2));
    }

    @Test
    public void testNestedNotEquivalent() {
        Schema nestedSchema1 = Schema.builder().addInt64Field("foo").build();
        Schema nestedSchema2 = Schema.builder().addStringField("foo").build();
        Schema schema1 = Schema.builder().addRowField("foo", nestedSchema1).build();
        Schema schema2 = Schema.builder().addRowField("foo", nestedSchema2).build();
        Assert.assertNotEquals(schema1, schema2);
        Assert.assertFalse(schema1.equivalent(schema2));
    }

    @Test
    public void testArrayNotEquivalent() {
        Schema schema1 = Schema.builder().addArrayField("foo", BOOLEAN).build();
        Schema schema2 = Schema.builder().addArrayField("foo", DATETIME).build();
        Assert.assertNotEquals(schema1, schema2);
        Assert.assertFalse(schema1.equivalent(schema2));
    }

    @Test
    public void testNestedArraysNotEquivalent() {
        Schema nestedSchema1 = Schema.builder().addInt64Field("foo").build();
        Schema nestedSchema2 = Schema.builder().addStringField("foo").build();
        Schema schema1 = Schema.builder().addArrayField("foo", FieldType.row(nestedSchema1)).build();
        Schema schema2 = Schema.builder().addArrayField("foo", FieldType.row(nestedSchema2)).build();
        Assert.assertNotEquals(schema1, schema2);
        Assert.assertFalse(schema1.equivalent(schema2));
    }

    @Test
    public void testMapNotEquivalent() {
        Schema schema1 = Schema.builder().addMapField("foo", STRING, BOOLEAN).build();
        Schema schema2 = Schema.builder().addMapField("foo", DATETIME, BOOLEAN).build();
        Assert.assertNotEquals(schema1, schema2);
        Assert.assertFalse(schema1.equivalent(schema2));
        schema1 = Schema.builder().addMapField("foo", STRING, BOOLEAN).build();
        schema2 = Schema.builder().addMapField("foo", STRING, STRING).build();
        Assert.assertNotEquals(schema1, schema2);
        Assert.assertFalse(schema1.equivalent(schema2));
    }

    @Test
    public void testNestedMapsNotEquivalent() {
        Schema nestedSchema1 = Schema.builder().addInt64Field("foo").build();
        Schema nestedSchema2 = Schema.builder().addStringField("foo").build();
        Schema schema1 = Schema.builder().addMapField("foo", STRING, FieldType.row(nestedSchema1)).build();
        Schema schema2 = Schema.builder().addMapField("foo", STRING, FieldType.row(nestedSchema2)).build();
        Assert.assertNotEquals(schema1, schema2);
        Assert.assertFalse(schema1.equivalent(schema2));
    }

    static class TestType extends PassThroughLogicalType<Long> {
        TestType(String id, String arg) {
            super(id, arg, INT64);
        }
    }

    @Test
    public void testLogicalType() {
        Schema schema1 = Schema.builder().addLogicalTypeField("logical", new SchemaTest.TestType("id", "arg")).build();
        Schema schema2 = Schema.builder().addLogicalTypeField("logical", new SchemaTest.TestType("id", "arg")).build();
        Assert.assertEquals(schema1, schema2);// Logical types are the same.

        Schema schema3 = Schema.builder().addLogicalTypeField("logical", new SchemaTest.TestType("id2", "arg")).build();
        Assert.assertNotEquals(schema1, schema3);// Logical type id is different.

        Schema schema4 = Schema.builder().addLogicalTypeField("logical", new SchemaTest.TestType("id", "arg2")).build();
        Assert.assertNotEquals(schema1, schema4);// Logical type arg is different.

    }
}

