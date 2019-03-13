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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.beam.sdk.schemas.utils.SchemaTestUtils;
import org.apache.beam.sdk.schemas.utils.TestJavaBeans;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.beam.vendor.guava.v20_0.com.google.common.primitives.Ints;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for the {@link JavaBeanSchema} schema provider.
 */
public class JavaBeanSchemaTest {
    static final DateTime DATE = DateTime.parse("1979-03-14");

    static final byte[] BYTE_ARRAY = "bytearray".getBytes(Charset.defaultCharset());

    @Test
    public void testSchema() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Schema schema = registry.getSchema(TestJavaBeans.SimpleBean.class);
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.SIMPLE_BEAN_SCHEMA, schema);
    }

    @Test
    public void testToRow() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        TestJavaBeans.SimpleBean bean = createSimple("string");
        Row row = registry.getToRowFunction(TestJavaBeans.SimpleBean.class).apply(bean);
        Assert.assertEquals(12, row.getFieldCount());
        Assert.assertEquals("string", row.getString("str"));
        Assert.assertEquals(((byte) (1)), ((Object) (row.getByte("aByte"))));
        Assert.assertEquals(((short) (2)), ((Object) (row.getInt16("aShort"))));
        Assert.assertEquals(((int) (3)), ((Object) (row.getInt32("anInt"))));
        Assert.assertEquals(((long) (4)), ((Object) (row.getInt64("aLong"))));
        Assert.assertEquals(true, ((Object) (row.getBoolean("aBoolean"))));
        Assert.assertEquals(JavaBeanSchemaTest.DATE.toInstant(), row.getDateTime("dateTime"));
        Assert.assertEquals(JavaBeanSchemaTest.DATE.toInstant(), row.getDateTime("instant"));
        Assert.assertArrayEquals(JavaBeanSchemaTest.BYTE_ARRAY, row.getBytes("bytes"));
        Assert.assertArrayEquals(JavaBeanSchemaTest.BYTE_ARRAY, row.getBytes("byteBuffer"));
        Assert.assertEquals(BigDecimal.ONE, row.getDecimal("bigDecimal"));
        Assert.assertEquals("stringbuilder", row.getString("stringBuilder"));
    }

    @Test
    public void testFromRow() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row = createSimpleRow("string");
        TestJavaBeans.SimpleBean bean = registry.getFromRowFunction(TestJavaBeans.SimpleBean.class).apply(row);
        Assert.assertEquals("string", bean.getStr());
        Assert.assertEquals(((byte) (1)), bean.getaByte());
        Assert.assertEquals(((short) (2)), bean.getaShort());
        Assert.assertEquals(((int) (3)), bean.getAnInt());
        Assert.assertEquals(((long) (4)), bean.getaLong());
        Assert.assertEquals(true, bean.isaBoolean());
        Assert.assertEquals(JavaBeanSchemaTest.DATE, bean.getDateTime());
        Assert.assertEquals(JavaBeanSchemaTest.DATE.toInstant(), bean.getInstant());
        Assert.assertArrayEquals("not equal", JavaBeanSchemaTest.BYTE_ARRAY, bean.getBytes());
        Assert.assertArrayEquals("not equal", JavaBeanSchemaTest.BYTE_ARRAY, bean.getByteBuffer().array());
        Assert.assertEquals(BigDecimal.ONE, bean.getBigDecimal());
        Assert.assertEquals("stringbuilder", bean.getStringBuilder().toString());
    }

    @Test
    public void testFromRowWithGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        TestJavaBeans.SimpleBean bean = createSimple("string");
        Row row = registry.getToRowFunction(TestJavaBeans.SimpleBean.class).apply(bean);
        // Test that the fromRowFunction simply returns the original object back.
        TestJavaBeans.SimpleBean extracted = registry.getFromRowFunction(TestJavaBeans.SimpleBean.class).apply(row);
        Assert.assertSame(bean, extracted);
    }

    @Test
    public void testRecursiveGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.NESTED_BEAN_SCHEMA, registry.getSchema(TestJavaBeans.NestedBean.class));
        TestJavaBeans.NestedBean bean = new TestJavaBeans.NestedBean(createSimple("string"));
        Row row = registry.getToRowFunction(TestJavaBeans.NestedBean.class).apply(bean);
        Row nestedRow = row.getRow("nested");
        Assert.assertEquals("string", nestedRow.getString("str"));
        Assert.assertEquals(((byte) (1)), ((Object) (nestedRow.getByte("aByte"))));
        Assert.assertEquals(((short) (2)), ((Object) (nestedRow.getInt16("aShort"))));
        Assert.assertEquals(((int) (3)), ((Object) (nestedRow.getInt32("anInt"))));
        Assert.assertEquals(((long) (4)), ((Object) (nestedRow.getInt64("aLong"))));
        Assert.assertEquals(true, nestedRow.getBoolean("aBoolean"));
        Assert.assertEquals(JavaBeanSchemaTest.DATE.toInstant(), nestedRow.getDateTime("dateTime"));
        Assert.assertEquals(JavaBeanSchemaTest.DATE.toInstant(), nestedRow.getDateTime("instant"));
        Assert.assertArrayEquals("not equal", JavaBeanSchemaTest.BYTE_ARRAY, nestedRow.getBytes("bytes"));
        Assert.assertArrayEquals("not equal", JavaBeanSchemaTest.BYTE_ARRAY, nestedRow.getBytes("byteBuffer"));
        Assert.assertEquals(BigDecimal.ONE, nestedRow.getDecimal("bigDecimal"));
        Assert.assertEquals("stringbuilder", nestedRow.getString("stringBuilder"));
    }

    @Test
    public void testRecursiveSetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row nestedRow = createSimpleRow("string");
        Row row = Row.withSchema(TestJavaBeans.NESTED_BEAN_SCHEMA).addValue(nestedRow).build();
        TestJavaBeans.NestedBean bean = registry.getFromRowFunction(TestJavaBeans.NestedBean.class).apply(row);
        Assert.assertEquals("string", bean.getNested().getStr());
        Assert.assertEquals(((byte) (1)), bean.getNested().getaByte());
        Assert.assertEquals(((short) (2)), bean.getNested().getaShort());
        Assert.assertEquals(((int) (3)), bean.getNested().getAnInt());
        Assert.assertEquals(((long) (4)), bean.getNested().getaLong());
        Assert.assertEquals(true, bean.getNested().isaBoolean());
        Assert.assertEquals(JavaBeanSchemaTest.DATE, bean.getNested().getDateTime());
        Assert.assertEquals(JavaBeanSchemaTest.DATE.toInstant(), bean.getNested().getInstant());
        Assert.assertArrayEquals("not equal", JavaBeanSchemaTest.BYTE_ARRAY, bean.getNested().getBytes());
        Assert.assertArrayEquals("not equal", JavaBeanSchemaTest.BYTE_ARRAY, bean.getNested().getByteBuffer().array());
        Assert.assertEquals(BigDecimal.ONE, bean.getNested().getBigDecimal());
        Assert.assertEquals("stringbuilder", bean.getNested().getStringBuilder().toString());
    }

    @Test
    public void testPrimitiveArrayGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.PRIMITIVE_ARRAY_BEAN_SCHEMA, registry.getSchema(TestJavaBeans.PrimitiveArrayBean.class));
        List<String> strList = ImmutableList.of("a", "b", "c");
        int[] intArray = new int[]{ 1, 2, 3, 4 };
        Long[] longArray = new Long[]{ 42L, 43L, 44L };
        TestJavaBeans.PrimitiveArrayBean bean = new TestJavaBeans.PrimitiveArrayBean(strList, intArray, longArray);
        Row row = registry.getToRowFunction(TestJavaBeans.PrimitiveArrayBean.class).apply(bean);
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
        Row row = Row.withSchema(TestJavaBeans.PRIMITIVE_ARRAY_BEAN_SCHEMA).addArray("a", "b", "c", "d").addArray(1, 2, 3, 4).addArray(42L, 43L, 44L, 45L).build();
        TestJavaBeans.PrimitiveArrayBean bean = registry.getFromRowFunction(TestJavaBeans.PrimitiveArrayBean.class).apply(row);
        Assert.assertEquals(row.getArray("strings"), bean.getStrings());
        Assert.assertEquals(row.getArray("integers"), Ints.asList(bean.getIntegers()));
        Assert.assertEquals(row.getArray("longs"), Arrays.asList(bean.getLongs()));
    }

    @Test
    public void testRecursiveArrayGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.NESTED_ARRAY_BEAN_SCHEMA, registry.getSchema(TestJavaBeans.NestedArrayBean.class));
        TestJavaBeans.SimpleBean simple1 = createSimple("string1");
        TestJavaBeans.SimpleBean simple2 = createSimple("string2");
        TestJavaBeans.SimpleBean simple3 = createSimple("string3");
        TestJavaBeans.NestedArrayBean bean = new TestJavaBeans.NestedArrayBean(simple1, simple2, simple3);
        Row row = registry.getToRowFunction(TestJavaBeans.NestedArrayBean.class).apply(bean);
        List<Row> rows = row.getArray("beans");
        Assert.assertSame(simple1, registry.getFromRowFunction(TestJavaBeans.SimpleBean.class).apply(rows.get(0)));
        Assert.assertSame(simple2, registry.getFromRowFunction(TestJavaBeans.SimpleBean.class).apply(rows.get(1)));
        Assert.assertSame(simple3, registry.getFromRowFunction(TestJavaBeans.SimpleBean.class).apply(rows.get(2)));
    }

    @Test
    public void testRecursiveArraySetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row1 = createSimpleRow("string1");
        Row row2 = createSimpleRow("string2");
        Row row3 = createSimpleRow("string3");
        Row row = Row.withSchema(TestJavaBeans.NESTED_ARRAY_BEAN_SCHEMA).addArray(row1, row2, row3).build();
        TestJavaBeans.NestedArrayBean bean = registry.getFromRowFunction(TestJavaBeans.NestedArrayBean.class).apply(row);
        Assert.assertEquals(3, bean.getBeans().length);
        Assert.assertEquals("string1", bean.getBeans()[0].getStr());
        Assert.assertEquals("string2", bean.getBeans()[1].getStr());
        Assert.assertEquals("string3", bean.getBeans()[2].getStr());
    }

    @Test
    public void testNestedArraysGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.NESTED_ARRAYS_BEAM_SCHEMA, registry.getSchema(TestJavaBeans.NestedArraysBean.class));
        List<List<String>> listOfLists = Lists.newArrayList(Lists.newArrayList("a", "b", "c"), Lists.newArrayList("d", "e", "f"), Lists.newArrayList("g", "h", "i"));
        TestJavaBeans.NestedArraysBean bean = new TestJavaBeans.NestedArraysBean(listOfLists);
        Row row = registry.getToRowFunction(TestJavaBeans.NestedArraysBean.class).apply(bean);
        Assert.assertEquals(listOfLists, row.getArray("lists"));
    }

    @Test
    public void testNestedArraysSetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        List<List<String>> listOfLists = Lists.newArrayList(Lists.newArrayList("a", "b", "c"), Lists.newArrayList("d", "e", "f"), Lists.newArrayList("g", "h", "i"));
        Row row = Row.withSchema(TestJavaBeans.NESTED_ARRAYS_BEAM_SCHEMA).addArray(listOfLists).build();
        TestJavaBeans.NestedArraysBean bean = registry.getFromRowFunction(TestJavaBeans.NestedArraysBean.class).apply(row);
        Assert.assertEquals(listOfLists, bean.getLists());
    }

    @Test
    public void testMapFieldGetters() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.NESTED_MAP_BEAN_SCHEMA, registry.getSchema(TestJavaBeans.NestedMapBean.class));
        TestJavaBeans.SimpleBean simple1 = createSimple("string1");
        TestJavaBeans.SimpleBean simple2 = createSimple("string2");
        TestJavaBeans.SimpleBean simple3 = createSimple("string3");
        TestJavaBeans.NestedMapBean bean = new TestJavaBeans.NestedMapBean(ImmutableMap.of("simple1", simple1, "simple2", simple2, "simple3", simple3));
        Row row = registry.getToRowFunction(TestJavaBeans.NestedMapBean.class).apply(bean);
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
        Row row = Row.withSchema(TestJavaBeans.NESTED_MAP_BEAN_SCHEMA).addValue(ImmutableMap.of("simple1", row1, "simple2", row2, "simple3", row3)).build();
        TestJavaBeans.NestedMapBean bean = registry.getFromRowFunction(TestJavaBeans.NestedMapBean.class).apply(row);
        Assert.assertEquals(3, bean.getMap().size());
        Assert.assertEquals("string1", bean.getMap().get("simple1").getStr());
        Assert.assertEquals("string2", bean.getMap().get("simple2").getStr());
        Assert.assertEquals("string3", bean.getMap().get("simple3").getStr());
    }

    @Test
    public void testAnnotations() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Schema schema = registry.getSchema(TestJavaBeans.SimpleBeanWithAnnotations.class);
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.SIMPLE_BEAN_SCHEMA, schema);
        TestJavaBeans.SimpleBeanWithAnnotations pojo = createAnnotated("string");
        Row row = registry.getToRowFunction(TestJavaBeans.SimpleBeanWithAnnotations.class).apply(pojo);
        Assert.assertEquals(12, row.getFieldCount());
        Assert.assertEquals("string", row.getString("str"));
        Assert.assertEquals(((byte) (1)), ((Object) (row.getByte("aByte"))));
        Assert.assertEquals(((short) (2)), ((Object) (row.getInt16("aShort"))));
        Assert.assertEquals(((int) (3)), ((Object) (row.getInt32("anInt"))));
        Assert.assertEquals(((long) (4)), ((Object) (row.getInt64("aLong"))));
        Assert.assertEquals(true, ((Object) (row.getBoolean("aBoolean"))));
        Assert.assertEquals(JavaBeanSchemaTest.DATE.toInstant(), row.getDateTime("dateTime"));
        Assert.assertEquals(JavaBeanSchemaTest.DATE.toInstant(), row.getDateTime("instant"));
        Assert.assertArrayEquals(JavaBeanSchemaTest.BYTE_ARRAY, row.getBytes("bytes"));
        Assert.assertArrayEquals(JavaBeanSchemaTest.BYTE_ARRAY, row.getBytes("byteBuffer"));
        Assert.assertEquals(BigDecimal.ONE, row.getDecimal("bigDecimal"));
        Assert.assertEquals("stringbuilder", row.getString("stringBuilder"));
        TestJavaBeans.SimpleBeanWithAnnotations pojo2 = registry.getFromRowFunction(TestJavaBeans.SimpleBeanWithAnnotations.class).apply(createSimpleRow("string"));
        Assert.assertEquals(pojo, pojo2);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testMismatchingNullable() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        thrown.expect(RuntimeException.class);
        Schema schema = registry.getSchema(TestJavaBeans.MismatchingNullableBean.class);
    }
}

