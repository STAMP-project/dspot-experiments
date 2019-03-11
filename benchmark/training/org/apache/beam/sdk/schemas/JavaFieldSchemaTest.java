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


import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.beam.sdk.schemas.utils.SchemaTestUtils;
import org.apache.beam.sdk.schemas.utils.TestPOJOs;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.beam.vendor.guava.v20_0.com.google.common.primitives.Ints;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link JavaFieldSchema} schema provider.
 */
public class JavaFieldSchemaTest {
    static final DateTime DATE = DateTime.parse("1979-03-14");

    static final Instant INSTANT = DateTime.parse("1979-03-15").toInstant();

    static final byte[] BYTE_ARRAY = "bytearray".getBytes(Charset.defaultCharset());

    static final ByteBuffer BYTE_BUFFER = ByteBuffer.wrap("byteBuffer".getBytes(Charset.defaultCharset()));

    @Test
    public void testSchema() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Schema schema = registry.getSchema(TestPOJOs.SimplePOJO.class);
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.SIMPLE_POJO_SCHEMA, schema);
    }

    @Test
    public void testToRow() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        TestPOJOs.SimplePOJO pojo = createSimple("string");
        Row row = registry.getToRowFunction(TestPOJOs.SimplePOJO.class).apply(pojo);
        Assert.assertEquals(12, row.getFieldCount());
        Assert.assertEquals("string", row.getString("str"));
        Assert.assertEquals(((byte) (1)), ((Object) (row.getByte("aByte"))));
        Assert.assertEquals(((short) (2)), ((Object) (row.getInt16("aShort"))));
        Assert.assertEquals(((int) (3)), ((Object) (row.getInt32("anInt"))));
        Assert.assertEquals(((long) (4)), ((Object) (row.getInt64("aLong"))));
        Assert.assertEquals(true, row.getBoolean("aBoolean"));
        Assert.assertEquals(JavaFieldSchemaTest.DATE.toInstant(), row.getDateTime("dateTime"));
        Assert.assertEquals(JavaFieldSchemaTest.INSTANT, row.getDateTime("instant").toInstant());
        Assert.assertArrayEquals(JavaFieldSchemaTest.BYTE_ARRAY, row.getBytes("bytes"));
        Assert.assertArrayEquals(JavaFieldSchemaTest.BYTE_BUFFER.array(), row.getBytes("byteBuffer"));
        Assert.assertEquals(BigDecimal.ONE, row.getDecimal("bigDecimal"));
        Assert.assertEquals("stringbuilder", row.getString("stringBuilder"));
    }

    @Test
    public void testFromRow() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row = createSimpleRow("string");
        TestPOJOs.SimplePOJO pojo = registry.getFromRowFunction(TestPOJOs.SimplePOJO.class).apply(row);
        Assert.assertEquals("string", pojo.str);
        Assert.assertEquals(((byte) (1)), pojo.aByte);
        Assert.assertEquals(((short) (2)), pojo.aShort);
        Assert.assertEquals(((int) (3)), pojo.anInt);
        Assert.assertEquals(((long) (4)), pojo.aLong);
        Assert.assertEquals(true, pojo.aBoolean);
        Assert.assertEquals(JavaFieldSchemaTest.DATE, pojo.dateTime);
        Assert.assertEquals(JavaFieldSchemaTest.INSTANT, pojo.instant);
        Assert.assertArrayEquals("not equal", JavaFieldSchemaTest.BYTE_ARRAY, pojo.bytes);
        Assert.assertEquals(JavaFieldSchemaTest.BYTE_BUFFER, pojo.byteBuffer);
        Assert.assertEquals(BigDecimal.ONE, pojo.bigDecimal);
        Assert.assertEquals("stringbuilder", pojo.stringBuilder.toString());
    }

    @Test
    public void testFromRowWithGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        TestPOJOs.SimplePOJO pojo = createSimple("string");
        Row row = registry.getToRowFunction(TestPOJOs.SimplePOJO.class).apply(pojo);
        // Test that the fromRowFunction simply returns the original object back.
        TestPOJOs.SimplePOJO extracted = registry.getFromRowFunction(TestPOJOs.SimplePOJO.class).apply(row);
        Assert.assertSame(pojo, extracted);
    }

    @Test
    public void testRecursiveGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.NESTED_POJO_SCHEMA, registry.getSchema(TestPOJOs.NestedPOJO.class));
        TestPOJOs.NestedPOJO pojo = new TestPOJOs.NestedPOJO(createSimple("string"));
        Row row = registry.getToRowFunction(TestPOJOs.NestedPOJO.class).apply(pojo);
        Row nestedRow = row.getRow("nested");
        Assert.assertEquals("string", nestedRow.getString("str"));
        Assert.assertEquals(((byte) (1)), ((Object) (nestedRow.getByte("aByte"))));
        Assert.assertEquals(((short) (2)), ((Object) (nestedRow.getInt16("aShort"))));
        Assert.assertEquals(((int) (3)), ((Object) (nestedRow.getInt32("anInt"))));
        Assert.assertEquals(((long) (4)), ((Object) (nestedRow.getInt64("aLong"))));
        Assert.assertEquals(true, nestedRow.getBoolean("aBoolean"));
        Assert.assertEquals(JavaFieldSchemaTest.DATE.toInstant(), nestedRow.getDateTime("dateTime"));
        Assert.assertEquals(JavaFieldSchemaTest.INSTANT, nestedRow.getDateTime("instant").toInstant());
        Assert.assertArrayEquals("not equal", JavaFieldSchemaTest.BYTE_ARRAY, nestedRow.getBytes("bytes"));
        Assert.assertArrayEquals("not equal", JavaFieldSchemaTest.BYTE_BUFFER.array(), nestedRow.getBytes("byteBuffer"));
        Assert.assertEquals(BigDecimal.ONE, nestedRow.getDecimal("bigDecimal"));
        Assert.assertEquals("stringbuilder", nestedRow.getString("stringBuilder"));
    }

    @Test
    public void testRecursiveSetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row nestedRow = createSimpleRow("string");
        Row row = Row.withSchema(TestPOJOs.NESTED_POJO_SCHEMA).addValue(nestedRow).build();
        TestPOJOs.NestedPOJO pojo = registry.getFromRowFunction(TestPOJOs.NestedPOJO.class).apply(row);
        Assert.assertEquals("string", pojo.nested.str);
        Assert.assertEquals(((byte) (1)), pojo.nested.aByte);
        Assert.assertEquals(((short) (2)), pojo.nested.aShort);
        Assert.assertEquals(((int) (3)), pojo.nested.anInt);
        Assert.assertEquals(((long) (4)), pojo.nested.aLong);
        Assert.assertEquals(true, pojo.nested.aBoolean);
        Assert.assertEquals(JavaFieldSchemaTest.DATE, pojo.nested.dateTime);
        Assert.assertEquals(JavaFieldSchemaTest.INSTANT, pojo.nested.instant);
        Assert.assertArrayEquals("not equal", JavaFieldSchemaTest.BYTE_ARRAY, pojo.nested.bytes);
        Assert.assertEquals(JavaFieldSchemaTest.BYTE_BUFFER, pojo.nested.byteBuffer);
        Assert.assertEquals(BigDecimal.ONE, pojo.nested.bigDecimal);
        Assert.assertEquals("stringbuilder", pojo.nested.stringBuilder.toString());
    }

    @Test
    public void testPrimitiveArrayGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.PRIMITIVE_ARRAY_POJO_SCHEMA, registry.getSchema(TestPOJOs.PrimitiveArrayPOJO.class));
        List<String> strList = ImmutableList.of("a", "b", "c");
        int[] intArray = new int[]{ 1, 2, 3, 4 };
        Long[] longArray = new Long[]{ 42L, 43L, 44L };
        TestPOJOs.PrimitiveArrayPOJO pojo = new TestPOJOs.PrimitiveArrayPOJO(strList, intArray, longArray);
        Row row = registry.getToRowFunction(TestPOJOs.PrimitiveArrayPOJO.class).apply(pojo);
        Assert.assertEquals(strList, row.getArray("strings"));
        Assert.assertEquals(Ints.asList(intArray), row.getArray("integers"));
        Assert.assertEquals(Arrays.asList(longArray), row.getArray("longs"));
        // Ensure that list caching works.
        Assert.assertSame(row.getArray("strings"), row.getArray("strings"));
        Assert.assertSame(row.getArray("integers"), row.getArray("integers"));
        Assert.assertSame(row.getArray("longs"), row.getArray("longs"));
    }

    @Test
    public void testPrimitiveArraySetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row = Row.withSchema(TestPOJOs.PRIMITIVE_ARRAY_POJO_SCHEMA).addArray("a", "b", "c", "d").addArray(1, 2, 3, 4).addArray(42L, 43L, 44L, 45L).build();
        TestPOJOs.PrimitiveArrayPOJO pojo = registry.getFromRowFunction(TestPOJOs.PrimitiveArrayPOJO.class).apply(row);
        Assert.assertEquals(row.getArray("strings"), pojo.strings);
        Assert.assertEquals(row.getArray("integers"), Ints.asList(pojo.integers));
        Assert.assertEquals(row.getArray("longs"), Arrays.asList(pojo.longs));
    }

    @Test
    public void testRecursiveArrayGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.NESTED_ARRAY_POJO_SCHEMA, registry.getSchema(TestPOJOs.NestedArrayPOJO.class));
        TestPOJOs.SimplePOJO simple1 = createSimple("string1");
        TestPOJOs.SimplePOJO simple2 = createSimple("string2");
        TestPOJOs.SimplePOJO simple3 = createSimple("string3");
        TestPOJOs.NestedArrayPOJO pojo = new TestPOJOs.NestedArrayPOJO(simple1, simple2, simple3);
        Row row = registry.getToRowFunction(TestPOJOs.NestedArrayPOJO.class).apply(pojo);
        List<Row> rows = row.getArray("pojos");
        Assert.assertSame(simple1, registry.getFromRowFunction(TestPOJOs.SimplePOJO.class).apply(rows.get(0)));
        Assert.assertSame(simple2, registry.getFromRowFunction(TestPOJOs.SimplePOJO.class).apply(rows.get(1)));
        Assert.assertSame(simple3, registry.getFromRowFunction(TestPOJOs.SimplePOJO.class).apply(rows.get(2)));
    }

    @Test
    public void testRecursiveArraySetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row1 = createSimpleRow("string1");
        Row row2 = createSimpleRow("string2");
        Row row3 = createSimpleRow("string3");
        Row row = Row.withSchema(TestPOJOs.NESTED_ARRAY_POJO_SCHEMA).addArray(row1, row2, row3).build();
        TestPOJOs.NestedArrayPOJO pojo = registry.getFromRowFunction(TestPOJOs.NestedArrayPOJO.class).apply(row);
        Assert.assertEquals(3, pojo.pojos.length);
        Assert.assertEquals("string1", pojo.pojos[0].str);
        Assert.assertEquals("string2", pojo.pojos[1].str);
        Assert.assertEquals("string3", pojo.pojos[2].str);
    }

    @Test
    public void testNestedArraysGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.NESTED_ARRAYS_POJO_SCHEMA, registry.getSchema(TestPOJOs.NestedArraysPOJO.class));
        List<List<String>> listOfLists = Lists.newArrayList(Lists.newArrayList("a", "b", "c"), Lists.newArrayList("d", "e", "f"), Lists.newArrayList("g", "h", "i"));
        TestPOJOs.NestedArraysPOJO pojo = new TestPOJOs.NestedArraysPOJO(listOfLists);
        Row row = registry.getToRowFunction(TestPOJOs.NestedArraysPOJO.class).apply(pojo);
        Assert.assertEquals(listOfLists, row.getArray("lists"));
    }

    @Test
    public void testNestedArraysSetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        List<List<String>> listOfLists = Lists.newArrayList(Lists.newArrayList("a", "b", "c"), Lists.newArrayList("d", "e", "f"), Lists.newArrayList("g", "h", "i"));
        Row row = Row.withSchema(TestPOJOs.NESTED_ARRAYS_POJO_SCHEMA).addArray(listOfLists).build();
        TestPOJOs.NestedArraysPOJO pojo = registry.getFromRowFunction(TestPOJOs.NestedArraysPOJO.class).apply(row);
        Assert.assertEquals(listOfLists, pojo.lists);
    }

    @Test
    public void testMapFieldGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.NESTED_MAP_POJO_SCHEMA, registry.getSchema(TestPOJOs.NestedMapPOJO.class));
        TestPOJOs.SimplePOJO simple1 = createSimple("string1");
        TestPOJOs.SimplePOJO simple2 = createSimple("string2");
        TestPOJOs.SimplePOJO simple3 = createSimple("string3");
        TestPOJOs.NestedMapPOJO pojo = new TestPOJOs.NestedMapPOJO(ImmutableMap.of("simple1", simple1, "simple2", simple2, "simple3", simple3));
        Row row = registry.getToRowFunction(TestPOJOs.NestedMapPOJO.class).apply(pojo);
        Map<String, Row> extractedMap = row.getMap("map");
        Assert.assertEquals(3, extractedMap.size());
        Assert.assertEquals("string1", extractedMap.get("simple1").getString("str"));
        Assert.assertEquals("string2", extractedMap.get("simple2").getString("str"));
        Assert.assertEquals("string3", extractedMap.get("simple3").getString("str"));
    }

    @Test
    public void testMapFieldSetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row1 = createSimpleRow("string1");
        Row row2 = createSimpleRow("string2");
        Row row3 = createSimpleRow("string3");
        Row row = Row.withSchema(TestPOJOs.NESTED_MAP_POJO_SCHEMA).addValue(ImmutableMap.of("simple1", row1, "simple2", row2, "simple3", row3)).build();
        TestPOJOs.NestedMapPOJO pojo = registry.getFromRowFunction(TestPOJOs.NestedMapPOJO.class).apply(row);
        Assert.assertEquals(3, pojo.map.size());
        Assert.assertEquals("string1", pojo.map.get("simple1").str);
        Assert.assertEquals("string2", pojo.map.get("simple2").str);
        Assert.assertEquals("string3", pojo.map.get("simple3").str);
    }

    @Test
    public void testNullValuesGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row = registry.getToRowFunction(TestPOJOs.POJOWithNullables.class).apply(new TestPOJOs.POJOWithNullables(null, 42));
        Assert.assertNull(row.getString("str"));
        Assert.assertEquals(42, ((Object) (row.getInt32("anInt"))));
    }

    @Test
    public void testNullValuesSetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row = Row.withSchema(TestPOJOs.NULLABLES_SCHEMA).addValues(null, 42).build();
        TestPOJOs.POJOWithNullables pojo = registry.getFromRowFunction(TestPOJOs.POJOWithNullables.class).apply(row);
        Assert.assertNull(pojo.str);
        Assert.assertEquals(42, pojo.anInt);
    }

    @Test
    public void testNestedNullValuesGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row = registry.getToRowFunction(TestPOJOs.POJOWithNestedNullable.class).apply(new TestPOJOs.POJOWithNestedNullable(null));
        Assert.assertNull(row.getValue("nested"));
    }

    @Test
    public void testNNestedullValuesSetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row = Row.withSchema(TestPOJOs.NESTED_NULLABLE_SCHEMA).addValue(null).build();
        TestPOJOs.POJOWithNestedNullable pojo = registry.getFromRowFunction(TestPOJOs.POJOWithNestedNullable.class).apply(row);
        Assert.assertNull(pojo.nested);
    }

    @Test
    public void testAnnotations() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Schema schema = registry.getSchema(TestPOJOs.AnnotatedSimplePojo.class);
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.SIMPLE_POJO_SCHEMA, schema);
        Row simpleRow = createSimpleRow("string");
        TestPOJOs.AnnotatedSimplePojo pojo = createAnnotated("string");
        Assert.assertEquals(simpleRow, registry.getToRowFunction(TestPOJOs.AnnotatedSimplePojo.class).apply(pojo));
        TestPOJOs.AnnotatedSimplePojo pojo2 = registry.getFromRowFunction(TestPOJOs.AnnotatedSimplePojo.class).apply(simpleRow);
        Assert.assertEquals(pojo, pojo2);
    }

    @Test
    public void testStaticCreator() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Schema schema = registry.getSchema(TestPOJOs.StaticCreationSimplePojo.class);
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.SIMPLE_POJO_SCHEMA, schema);
        Row simpleRow = createSimpleRow("string");
        TestPOJOs.StaticCreationSimplePojo pojo = createStaticCreation("string");
        Assert.assertEquals(simpleRow, registry.getToRowFunction(TestPOJOs.StaticCreationSimplePojo.class).apply(pojo));
        TestPOJOs.StaticCreationSimplePojo pojo2 = registry.getFromRowFunction(TestPOJOs.StaticCreationSimplePojo.class).apply(simpleRow);
        Assert.assertEquals(pojo, pojo2);
    }

    @Test
    public void testNestedArraysFromRow() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Schema schema = registry.getSchema(TestPOJOs.PojoWithNestedArray.class);
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.POJO_WITH_NESTED_ARRAY_SCHEMA, schema);
        Row simpleRow = createSimpleRow("string");
        List<Row> list = ImmutableList.of(simpleRow, simpleRow);
        List<List<Row>> listOfList = ImmutableList.of(list, list);
        Row nestedRow = Row.withSchema(TestPOJOs.POJO_WITH_NESTED_ARRAY_SCHEMA).addValue(listOfList).build();
        TestPOJOs.SimplePOJO simplePojo = createSimple("string");
        List<TestPOJOs.SimplePOJO> simplePojoList = ImmutableList.of(simplePojo, simplePojo);
        List<List<TestPOJOs.SimplePOJO>> simplePojoListOfList = ImmutableList.of(simplePojoList, simplePojoList);
        TestPOJOs.PojoWithNestedArray converted = registry.getFromRowFunction(TestPOJOs.PojoWithNestedArray.class).apply(nestedRow);
        Assert.assertEquals(simplePojoListOfList, converted.pojos);
    }

    @Test
    public void testNestedArraysToRow() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Schema schema = registry.getSchema(TestPOJOs.PojoWithNestedArray.class);
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.POJO_WITH_NESTED_ARRAY_SCHEMA, schema);
        Row simpleRow = createSimpleRow("string");
        List<Row> list = ImmutableList.of(simpleRow, simpleRow);
        List<List<Row>> listOfList = ImmutableList.of(list, list);
        Row nestedRow = Row.withSchema(TestPOJOs.POJO_WITH_NESTED_ARRAY_SCHEMA).addValue(listOfList).build();
        TestPOJOs.SimplePOJO simplePojo = createSimple("string");
        List<TestPOJOs.SimplePOJO> simplePojoList = ImmutableList.of(simplePojo, simplePojo);
        List<List<TestPOJOs.SimplePOJO>> simplePojoListOfList = ImmutableList.of(simplePojoList, simplePojoList);
        Row converted = registry.getToRowFunction(TestPOJOs.PojoWithNestedArray.class).apply(new TestPOJOs.PojoWithNestedArray(simplePojoListOfList));
        Assert.assertEquals(nestedRow, converted);
    }
}

