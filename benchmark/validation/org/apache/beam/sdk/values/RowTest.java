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
package org.apache.beam.sdk.values;


import DateTimeZone.UTC;
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
import Schema.Field;
import Schema.FieldType.BYTES;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.Schema.FieldType;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link Row}.
 */
public class RowTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCreatesNullRecord() {
        Schema type = Stream.of(Field.of("f_byte", BYTE).withNullable(true), Field.of("f_int16", INT16).withNullable(true), Field.of("f_int32", INT32).withNullable(true), Field.of("f_int64", INT64).withNullable(true), Field.of("f_decimal", DECIMAL).withNullable(true), Field.of("f_float", FLOAT).withNullable(true), Field.of("f_double", DOUBLE).withNullable(true), Field.of("f_string", STRING).withNullable(true), Field.of("f_datetime", DATETIME).withNullable(true), Field.of("f_boolean", BOOLEAN).withNullable(true), Field.of("f_array", FieldType.array(DATETIME)).withNullable(true), Field.of("f_map", FieldType.map(INT32, DOUBLE)).withNullable(true)).collect(Schema.toSchema());
        Row row = Row.nullRow(type);
        Assert.assertNull(row.getByte("f_byte"));
        Assert.assertNull(row.getByte(0));
        Assert.assertNull(row.getInt16("f_int16"));
        Assert.assertNull(row.getInt16(1));
        Assert.assertNull(row.getInt32("f_int32"));
        Assert.assertNull(row.getInt32(2));
        Assert.assertNull(row.getInt64("f_int64"));
        Assert.assertNull(row.getInt64(3));
        Assert.assertNull(row.getDecimal("f_decimal"));
        Assert.assertNull(row.getDecimal(4));
        Assert.assertNull(row.getFloat("f_float"));
        Assert.assertNull(row.getFloat(5));
        Assert.assertNull(row.getDouble("f_double"));
        Assert.assertNull(row.getDouble(6));
        Assert.assertNull(row.getString("f_string"));
        Assert.assertNull(row.getString(7));
        Assert.assertNull(row.getDateTime("f_datetime"));
        Assert.assertNull(row.getDateTime(8));
        Assert.assertNull(row.getBoolean("f_boolean"));
        Assert.assertNull(row.getBoolean(9));
        Assert.assertNull(row.getBoolean("f_array"));
        Assert.assertNull(row.getBoolean(10));
        Assert.assertNull(row.getBoolean("f_map"));
        Assert.assertNull(row.getBoolean(11));
    }

    @Test
    public void testRejectsNullRecord() {
        Schema type = Stream.of(Field.of("f_int", Schema.FieldType.INT32)).collect(Schema.toSchema());
        thrown.expect(IllegalArgumentException.class);
        Row.nullRow(type);
    }

    @Test
    public void testCreatesRecord() {
        Schema schema = Schema.builder().addByteField("f_byte").addInt16Field("f_int16").addInt32Field("f_int32").addInt64Field("f_int64").addDecimalField("f_decimal").addFloatField("f_float").addDoubleField("f_double").addStringField("f_string").addDateTimeField("f_datetime").addBooleanField("f_boolean").build();
        DateTime dateTime = new DateTime().withDate(1979, 3, 14).withTime(1, 2, 3, 4).withZone(UTC);
        Row row = Row.withSchema(schema).addValues(((byte) (0)), ((short) (1)), 2, 3L, new BigDecimal(2.3), 1.2F, 3.0, "str", dateTime, false).build();
        Assert.assertEquals(((byte) (0)), ((Object) (row.getByte("f_byte"))));
        Assert.assertEquals(((byte) (0)), ((Object) (row.getByte(0))));
        Assert.assertEquals(((short) (1)), ((Object) (row.getInt16("f_int16"))));
        Assert.assertEquals(((short) (1)), ((Object) (row.getInt16(1))));
        Assert.assertEquals(((int) (2)), ((Object) (row.getInt32("f_int32"))));
        Assert.assertEquals(((int) (2)), ((Object) (row.getInt32(2))));
        Assert.assertEquals(((long) (3)), ((Object) (row.getInt64("f_int64"))));
        Assert.assertEquals(((long) (3)), ((Object) (row.getInt64(3))));
        Assert.assertEquals(new BigDecimal(2.3), row.getDecimal("f_decimal"));
        Assert.assertEquals(new BigDecimal(2.3), row.getDecimal(4));
        Assert.assertEquals(1.2F, row.getFloat("f_float"), 0);
        Assert.assertEquals(1.2F, row.getFloat(5), 0);
        Assert.assertEquals(3.0, row.getDouble("f_double"), 0);
        Assert.assertEquals(3.0, row.getDouble(6), 0);
        Assert.assertEquals("str", row.getString("f_string"));
        Assert.assertEquals("str", row.getString(7));
        Assert.assertEquals(dateTime, row.getDateTime("f_datetime"));
        Assert.assertEquals(dateTime, row.getDateTime(8));
        Assert.assertEquals(false, row.getBoolean("f_boolean"));
        Assert.assertEquals(false, row.getBoolean(9));
    }

    @Test
    public void testCreatesNestedRow() {
        Schema nestedType = Stream.of(Field.of("f1_str", Schema.FieldType.STRING)).collect(Schema.toSchema());
        Schema type = Stream.of(Field.of("f_int", Schema.FieldType.INT32), Field.of("nested", Schema.FieldType.row(nestedType))).collect(Schema.toSchema());
        Row nestedRow = Row.withSchema(nestedType).addValues("foobar").build();
        Row row = Row.withSchema(type).addValues(42, nestedRow).build();
        Assert.assertEquals(((int) (42)), ((Object) (row.getInt32("f_int"))));
        Assert.assertEquals("foobar", row.getRow("nested").getString("f1_str"));
    }

    @Test
    public void testCreatesArray() {
        List<Integer> data = Lists.newArrayList(2, 3, 5, 7);
        Schema type = Stream.of(Field.of("array", Schema.FieldType.array(Schema.FieldType.INT32))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addArray(data).build();
        Assert.assertEquals(data, row.getArray("array"));
    }

    @Test
    public void testCreatesArrayWithNullElement() {
        List<Integer> data = Lists.newArrayList(2, null, 5, null);
        Schema type = Stream.of(Field.of("array", Schema.FieldType.array(Schema.FieldType.INT32, true))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addArray(data).build();
        Assert.assertEquals(data, row.getArray("array"));
    }

    @Test
    public void testCreatesRowArray() {
        Schema nestedType = Stream.of(Field.of("f1_str", STRING)).collect(Schema.toSchema());
        List<Row> data = Lists.newArrayList(Row.withSchema(nestedType).addValues("one").build(), Row.withSchema(nestedType).addValues("two").build(), Row.withSchema(nestedType).addValues("three").build());
        Schema type = Stream.of(Field.of("array", FieldType.array(FieldType.row(nestedType)))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addArray(data).build();
        Assert.assertEquals(data, row.getArray("array"));
    }

    @Test
    public void testCreatesArrayArray() {
        List<List<Integer>> data = Lists.<List<Integer>>newArrayList(Lists.newArrayList(1, 2, 3, 4));
        Schema type = Stream.of(Field.of("array", FieldType.array(FieldType.array(INT32)))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addArray(data).build();
        Assert.assertEquals(data, row.getArray("array"));
    }

    @Test
    public void testCreatesArrayArrayWithNullElement() {
        List<List<Integer>> data = Lists.<List<Integer>>newArrayList(Lists.newArrayList(1, null, 3, null), null);
        Schema type = Stream.of(Field.of("array", FieldType.array(FieldType.array(INT32, true), true))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addArray(data).build();
        Assert.assertEquals(data, row.getArray("array"));
    }

    @Test
    public void testCreatesArrayOfMap() {
        List<Map<Integer, String>> data = ImmutableList.<Map<Integer, String>>builder().add(ImmutableMap.of(1, "value1")).add(ImmutableMap.of(2, "value2")).build();
        Schema type = Stream.of(Field.of("array", FieldType.array(FieldType.map(INT32, STRING)))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addArray(data).build();
        Assert.assertEquals(data, row.getArray("array"));
    }

    @Test
    public void testCreateMapWithPrimitiveValue() {
        Map<Integer, String> data = ImmutableMap.<Integer, String>builder().put(1, "value1").put(2, "value2").put(3, "value3").put(4, "value4").build();
        Schema type = Stream.of(Field.of("map", FieldType.map(INT32, STRING))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addValue(data).build();
        Assert.assertEquals(data, row.getMap("map"));
    }

    @Test
    public void testCreateMapWithNullValue() {
        Map<Integer, String> data = new HashMap();
        data.put(1, "value1");
        data.put(2, "value2");
        data.put(3, null);
        data.put(4, null);
        Schema type = Stream.of(Field.of("map", FieldType.map(INT32, STRING, true))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addValue(data).build();
        Assert.assertEquals(data, row.getMap("map"));
    }

    @Test
    public void testCreateMapWithArrayValue() {
        Map<Integer, List<String>> data = ImmutableMap.<Integer, List<String>>builder().put(1, Arrays.asList("value1")).put(2, Arrays.asList("value2")).build();
        Schema type = Stream.of(Field.of("map", FieldType.map(INT32, FieldType.array(STRING)))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addValue(data).build();
        Assert.assertEquals(data, row.getMap("map"));
    }

    @Test
    public void testCreateMapWithMapValue() {
        Map<Integer, Map<Integer, String>> data = ImmutableMap.<Integer, Map<Integer, String>>builder().put(1, ImmutableMap.of(1, "value1")).put(2, ImmutableMap.of(2, "value2")).build();
        Schema type = Stream.of(Field.of("map", FieldType.map(INT32, FieldType.map(INT32, STRING)))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addValue(data).build();
        Assert.assertEquals(data, row.getMap("map"));
    }

    @Test
    public void testCreateMapWithMapValueWithNull() {
        Map<Integer, Map<Integer, String>> data = new HashMap();
        Map<Integer, String> innerData = new HashMap();
        innerData.put(11, null);
        innerData.put(12, "value3");
        data.put(1, ImmutableMap.of(1, "value1"));
        data.put(2, ImmutableMap.of(2, "value2"));
        data.put(3, null);
        data.put(4, innerData);
        Schema type = Stream.of(Field.of("map", FieldType.map(INT32, FieldType.map(INT32, STRING, true), true))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addValue(data).build();
        Assert.assertEquals(data, row.getMap("map"));
    }

    @Test
    public void testCreateMapWithRowValue() {
        Schema nestedType = Stream.of(Field.of("f1_str", STRING)).collect(Schema.toSchema());
        Map<Integer, Row> data = ImmutableMap.<Integer, Row>builder().put(1, Row.withSchema(nestedType).addValues("one").build()).put(2, Row.withSchema(nestedType).addValues("two").build()).build();
        Schema type = Stream.of(Field.of("map", FieldType.map(INT32, FieldType.row(nestedType)))).collect(Schema.toSchema());
        Row row = Row.withSchema(type).addValue(data).build();
        Assert.assertEquals(data, row.getMap("map"));
    }

    @Test
    public void testCollector() {
        Schema type = Stream.of(Field.of("f_int", INT32), Field.of("f_str", STRING), Field.of("f_double", DOUBLE)).collect(Schema.toSchema());
        Row row = Stream.of(1, "2", 3.0).collect(Row.toRow(type));
        Assert.assertEquals(1, row.<Object>getValue("f_int"));
        Assert.assertEquals("2", row.getValue("f_str"));
        Assert.assertEquals(3.0, row.<Object>getValue("f_double"));
    }

    @Test
    public void testThrowsForIncorrectNumberOfFields() {
        Schema type = Stream.of(Field.of("f_int", INT32), Field.of("f_str", STRING), Field.of("f_double", DOUBLE)).collect(Schema.toSchema());
        thrown.expect(IllegalArgumentException.class);
        Row.withSchema(type).addValues(1, "2").build();
    }

    @Test
    public void testByteArrayEquality() {
        byte[] a0 = new byte[]{ 1, 2, 3, 4 };
        byte[] b0 = new byte[]{ 1, 2, 3, 4 };
        Schema schema = Schema.of(Field.of("bytes", BYTES));
        Row a = Row.withSchema(schema).addValue(a0).build();
        Row b = Row.withSchema(schema).addValue(b0).build();
        Assert.assertEquals(a, b);
    }

    @Test
    public void testByteBufferEquality() {
        byte[] a0 = new byte[]{ 1, 2, 3, 4 };
        byte[] b0 = new byte[]{ 1, 2, 3, 4 };
        Schema schema = Schema.of(Field.of("bytes", BYTES));
        Row a = Row.withSchema(schema).addValue(ByteBuffer.wrap(a0)).build();
        Row b = Row.withSchema(schema).addValue(ByteBuffer.wrap(b0)).build();
        Assert.assertEquals(a, b);
    }
}

