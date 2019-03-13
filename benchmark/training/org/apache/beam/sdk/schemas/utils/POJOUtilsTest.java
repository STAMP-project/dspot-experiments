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
package org.apache.beam.sdk.schemas.utils;


import JavaFieldTypeSupplier.INSTANCE;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.beam.sdk.schemas.FieldValueGetter;
import org.apache.beam.sdk.schemas.FieldValueSetter;
import org.apache.beam.sdk.schemas.Schema;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link POJOUtils} class.
 */
public class POJOUtilsTest {
    static final DateTime DATE = DateTime.parse("1979-03-14");

    static final Instant INSTANT = DateTime.parse("1979-03-15").toInstant();

    static final byte[] BYTE_ARRAY = "byteArray".getBytes(Charset.defaultCharset());

    static final ByteBuffer BYTE_BUFFER = ByteBuffer.wrap("byteBuffer".getBytes(Charset.defaultCharset()));

    @Test
    public void testNullables() {
        Schema schema = POJOUtils.schemaFromPojoClass(TestPOJOs.POJOWithNullables.class, INSTANCE);
        Assert.assertTrue(schema.getField("str").getType().getNullable());
        Assert.assertFalse(schema.getField("anInt").getType().getNullable());
    }

    @Test
    public void testSimplePOJO() {
        Schema schema = POJOUtils.schemaFromPojoClass(TestPOJOs.SimplePOJO.class, INSTANCE);
        Assert.assertEquals(TestPOJOs.SIMPLE_POJO_SCHEMA, schema);
    }

    @Test
    public void testNestedPOJO() {
        Schema schema = POJOUtils.schemaFromPojoClass(TestPOJOs.NestedPOJO.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.NESTED_POJO_SCHEMA, schema);
    }

    @Test
    public void testPrimitiveArray() {
        Schema schema = POJOUtils.schemaFromPojoClass(TestPOJOs.PrimitiveArrayPOJO.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.PRIMITIVE_ARRAY_POJO_SCHEMA, schema);
    }

    @Test
    public void testNestedArray() {
        Schema schema = POJOUtils.schemaFromPojoClass(TestPOJOs.NestedArrayPOJO.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.NESTED_ARRAY_POJO_SCHEMA, schema);
    }

    @Test
    public void testNestedCollection() {
        Schema schema = POJOUtils.schemaFromPojoClass(TestPOJOs.NestedCollectionPOJO.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.NESTED_COLLECTION_POJO_SCHEMA, schema);
    }

    @Test
    public void testPrimitiveMap() {
        Schema schema = POJOUtils.schemaFromPojoClass(TestPOJOs.PrimitiveMapPOJO.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.PRIMITIVE_MAP_POJO_SCHEMA, schema);
    }

    @Test
    public void testNestedMap() {
        Schema schema = POJOUtils.schemaFromPojoClass(TestPOJOs.NestedMapPOJO.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestPOJOs.NESTED_MAP_POJO_SCHEMA, schema);
    }

    @Test
    public void testGeneratedSimpleGetters() {
        TestPOJOs.SimplePOJO simplePojo = new TestPOJOs.SimplePOJO("field1", ((byte) (41)), ((short) (42)), 43, 44L, true, POJOUtilsTest.DATE, POJOUtilsTest.INSTANT, POJOUtilsTest.BYTE_ARRAY, POJOUtilsTest.BYTE_BUFFER, new BigDecimal(42), new StringBuilder("stringBuilder"));
        List<FieldValueGetter> getters = POJOUtils.getGetters(TestPOJOs.SimplePOJO.class, TestPOJOs.SIMPLE_POJO_SCHEMA, INSTANCE);
        Assert.assertEquals(12, getters.size());
        Assert.assertEquals("str", getters.get(0).name());
        Assert.assertEquals("field1", getters.get(0).get(simplePojo));
        Assert.assertEquals(((byte) (41)), getters.get(1).get(simplePojo));
        Assert.assertEquals(((short) (42)), getters.get(2).get(simplePojo));
        Assert.assertEquals(((int) (43)), getters.get(3).get(simplePojo));
        Assert.assertEquals(((long) (44)), getters.get(4).get(simplePojo));
        Assert.assertEquals(true, getters.get(5).get(simplePojo));
        Assert.assertEquals(POJOUtilsTest.DATE.toInstant(), getters.get(6).get(simplePojo));
        Assert.assertEquals(POJOUtilsTest.INSTANT, getters.get(7).get(simplePojo));
        Assert.assertArrayEquals("Unexpected bytes", POJOUtilsTest.BYTE_ARRAY, ((byte[]) (getters.get(8).get(simplePojo))));
        Assert.assertArrayEquals("Unexpected bytes", POJOUtilsTest.BYTE_BUFFER.array(), ((byte[]) (getters.get(9).get(simplePojo))));
        Assert.assertEquals(new BigDecimal(42), getters.get(10).get(simplePojo));
        Assert.assertEquals("stringBuilder", getters.get(11).get(simplePojo));
    }

    @Test
    public void testGeneratedSimpleSetters() {
        TestPOJOs.SimplePOJO simplePojo = new TestPOJOs.SimplePOJO();
        List<FieldValueSetter> setters = POJOUtils.getSetters(TestPOJOs.SimplePOJO.class, TestPOJOs.SIMPLE_POJO_SCHEMA, INSTANCE);
        Assert.assertEquals(12, setters.size());
        setters.get(0).set(simplePojo, "field1");
        setters.get(1).set(simplePojo, ((byte) (41)));
        setters.get(2).set(simplePojo, ((short) (42)));
        setters.get(3).set(simplePojo, ((int) (43)));
        setters.get(4).set(simplePojo, ((long) (44)));
        setters.get(5).set(simplePojo, true);
        setters.get(6).set(simplePojo, POJOUtilsTest.DATE.toInstant());
        setters.get(7).set(simplePojo, POJOUtilsTest.INSTANT);
        setters.get(8).set(simplePojo, POJOUtilsTest.BYTE_ARRAY);
        setters.get(9).set(simplePojo, POJOUtilsTest.BYTE_BUFFER.array());
        setters.get(10).set(simplePojo, new BigDecimal(42));
        setters.get(11).set(simplePojo, "stringBuilder");
        Assert.assertEquals("field1", simplePojo.str);
        Assert.assertEquals(((byte) (41)), simplePojo.aByte);
        Assert.assertEquals(((short) (42)), simplePojo.aShort);
        Assert.assertEquals(((int) (43)), simplePojo.anInt);
        Assert.assertEquals(((long) (44)), simplePojo.aLong);
        Assert.assertEquals(true, simplePojo.aBoolean);
        Assert.assertEquals(POJOUtilsTest.DATE, simplePojo.dateTime);
        Assert.assertEquals(POJOUtilsTest.INSTANT, simplePojo.instant);
        Assert.assertArrayEquals("Unexpected bytes", POJOUtilsTest.BYTE_ARRAY, simplePojo.bytes);
        Assert.assertEquals(POJOUtilsTest.BYTE_BUFFER, simplePojo.byteBuffer);
        Assert.assertEquals(new BigDecimal(42), simplePojo.bigDecimal);
        Assert.assertEquals("stringBuilder", simplePojo.stringBuilder.toString());
    }

    @Test
    public void testGeneratedSimpleBoxedGetters() {
        TestPOJOs.POJOWithBoxedFields pojo = new TestPOJOs.POJOWithBoxedFields(((byte) (41)), ((short) (42)), 43, 44L, true);
        List<FieldValueGetter> getters = POJOUtils.getGetters(TestPOJOs.POJOWithBoxedFields.class, TestPOJOs.POJO_WITH_BOXED_FIELDS_SCHEMA, INSTANCE);
        Assert.assertEquals(((byte) (41)), getters.get(0).get(pojo));
        Assert.assertEquals(((short) (42)), getters.get(1).get(pojo));
        Assert.assertEquals(((int) (43)), getters.get(2).get(pojo));
        Assert.assertEquals(((long) (44)), getters.get(3).get(pojo));
        Assert.assertEquals(true, getters.get(4).get(pojo));
    }

    @Test
    public void testGeneratedSimpleBoxedSetters() {
        TestPOJOs.POJOWithBoxedFields pojo = new TestPOJOs.POJOWithBoxedFields();
        List<FieldValueSetter> setters = POJOUtils.getSetters(TestPOJOs.POJOWithBoxedFields.class, TestPOJOs.POJO_WITH_BOXED_FIELDS_SCHEMA, INSTANCE);
        setters.get(0).set(pojo, ((byte) (41)));
        setters.get(1).set(pojo, ((short) (42)));
        setters.get(2).set(pojo, ((int) (43)));
        setters.get(3).set(pojo, ((long) (44)));
        setters.get(4).set(pojo, true);
        Assert.assertEquals(((byte) (41)), pojo.aByte.byteValue());
        Assert.assertEquals(((short) (42)), pojo.aShort.shortValue());
        Assert.assertEquals(((int) (43)), pojo.anInt.intValue());
        Assert.assertEquals(((long) (44)), pojo.aLong.longValue());
        Assert.assertEquals(true, pojo.aBoolean.booleanValue());
    }

    @Test
    public void testGeneratedByteBufferSetters() {
        TestPOJOs.POJOWithByteArray pojo = new TestPOJOs.POJOWithByteArray();
        List<FieldValueSetter> setters = POJOUtils.getSetters(TestPOJOs.POJOWithByteArray.class, TestPOJOs.POJO_WITH_BYTE_ARRAY_SCHEMA, INSTANCE);
        setters.get(0).set(pojo, POJOUtilsTest.BYTE_ARRAY);
        setters.get(1).set(pojo, POJOUtilsTest.BYTE_BUFFER.array());
        Assert.assertArrayEquals("not equal", POJOUtilsTest.BYTE_ARRAY, pojo.bytes1);
        Assert.assertEquals(POJOUtilsTest.BYTE_BUFFER, pojo.bytes2);
    }
}

