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


import GetterTypeSupplier.INSTANCE;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.beam.sdk.schemas.FieldValueGetter;
import org.apache.beam.sdk.schemas.FieldValueSetter;
import org.apache.beam.sdk.schemas.JavaBeanSchema;
import org.apache.beam.sdk.schemas.JavaBeanSchema.SetterTypeSupplier;
import org.apache.beam.sdk.schemas.Schema;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link JavaBeanUtils} class.
 */
public class JavaBeanUtilsTest {
    @Test
    public void testNullable() {
        Schema schema = JavaBeanUtils.schemaFromJavaBeanClass(TestJavaBeans.NullableBean.class, INSTANCE);
        Assert.assertTrue(schema.getField("str").getType().getNullable());
        Assert.assertFalse(schema.getField("anInt").getType().getNullable());
    }

    @Test
    public void testSimpleBean() {
        Schema schema = JavaBeanUtils.schemaFromJavaBeanClass(TestJavaBeans.SimpleBean.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.SIMPLE_BEAN_SCHEMA, schema);
    }

    @Test
    public void testNestedBean() {
        Schema schema = JavaBeanUtils.schemaFromJavaBeanClass(TestJavaBeans.NestedBean.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.NESTED_BEAN_SCHEMA, schema);
    }

    @Test
    public void testPrimitiveArray() {
        Schema schema = JavaBeanUtils.schemaFromJavaBeanClass(TestJavaBeans.PrimitiveArrayBean.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.PRIMITIVE_ARRAY_BEAN_SCHEMA, schema);
    }

    @Test
    public void testNestedArray() {
        Schema schema = JavaBeanUtils.schemaFromJavaBeanClass(TestJavaBeans.NestedArrayBean.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.NESTED_ARRAY_BEAN_SCHEMA, schema);
    }

    @Test
    public void testNestedCollection() {
        Schema schema = JavaBeanUtils.schemaFromJavaBeanClass(TestJavaBeans.NestedCollectionBean.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.NESTED_COLLECTION_BEAN_SCHEMA, schema);
    }

    @Test
    public void testPrimitiveMap() {
        Schema schema = JavaBeanUtils.schemaFromJavaBeanClass(TestJavaBeans.PrimitiveMapBean.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.PRIMITIVE_MAP_BEAN_SCHEMA, schema);
    }

    @Test
    public void testNestedMap() {
        Schema schema = JavaBeanUtils.schemaFromJavaBeanClass(TestJavaBeans.NestedMapBean.class, INSTANCE);
        SchemaTestUtils.assertSchemaEquivalent(TestJavaBeans.NESTED_MAP_BEAN_SCHEMA, schema);
    }

    @Test
    public void testGeneratedSimpleGetters() {
        TestJavaBeans.SimpleBean simpleBean = new TestJavaBeans.SimpleBean();
        simpleBean.setStr("field1");
        simpleBean.setaByte(((byte) (41)));
        simpleBean.setaShort(((short) (42)));
        simpleBean.setAnInt(43);
        simpleBean.setaLong(44);
        simpleBean.setaBoolean(true);
        simpleBean.setDateTime(DateTime.parse("1979-03-14"));
        simpleBean.setInstant(DateTime.parse("1979-03-15").toInstant());
        simpleBean.setBytes("bytes1".getBytes(Charset.defaultCharset()));
        simpleBean.setByteBuffer(ByteBuffer.wrap("bytes2".getBytes(Charset.defaultCharset())));
        simpleBean.setBigDecimal(new BigDecimal(42));
        simpleBean.setStringBuilder(new StringBuilder("stringBuilder"));
        List<FieldValueGetter> getters = JavaBeanUtils.getGetters(TestJavaBeans.SimpleBean.class, TestJavaBeans.SIMPLE_BEAN_SCHEMA, new JavaBeanSchema.GetterTypeSupplier());
        Assert.assertEquals(12, getters.size());
        Assert.assertEquals("str", getters.get(0).name());
        Assert.assertEquals("field1", getters.get(0).get(simpleBean));
        Assert.assertEquals(((byte) (41)), getters.get(1).get(simpleBean));
        Assert.assertEquals(((short) (42)), getters.get(2).get(simpleBean));
        Assert.assertEquals(((int) (43)), getters.get(3).get(simpleBean));
        Assert.assertEquals(((long) (44)), getters.get(4).get(simpleBean));
        Assert.assertEquals(true, getters.get(5).get(simpleBean));
        Assert.assertEquals(DateTime.parse("1979-03-14").toInstant(), getters.get(6).get(simpleBean));
        Assert.assertEquals(DateTime.parse("1979-03-15").toInstant(), getters.get(7).get(simpleBean));
        Assert.assertArrayEquals("Unexpected bytes", "bytes1".getBytes(Charset.defaultCharset()), ((byte[]) (getters.get(8).get(simpleBean))));
        Assert.assertArrayEquals("Unexpected bytes", "bytes2".getBytes(Charset.defaultCharset()), ((byte[]) (getters.get(9).get(simpleBean))));
        Assert.assertEquals(new BigDecimal(42), getters.get(10).get(simpleBean));
        Assert.assertEquals("stringBuilder", getters.get(11).get(simpleBean).toString());
    }

    @Test
    public void testGeneratedSimpleSetters() {
        TestJavaBeans.SimpleBean simpleBean = new TestJavaBeans.SimpleBean();
        List<FieldValueSetter> setters = JavaBeanUtils.getSetters(TestJavaBeans.SimpleBean.class, TestJavaBeans.SIMPLE_BEAN_SCHEMA, new SetterTypeSupplier());
        Assert.assertEquals(12, setters.size());
        setters.get(0).set(simpleBean, "field1");
        setters.get(1).set(simpleBean, ((byte) (41)));
        setters.get(2).set(simpleBean, ((short) (42)));
        setters.get(3).set(simpleBean, ((int) (43)));
        setters.get(4).set(simpleBean, ((long) (44)));
        setters.get(5).set(simpleBean, true);
        setters.get(6).set(simpleBean, DateTime.parse("1979-03-14").toInstant());
        setters.get(7).set(simpleBean, DateTime.parse("1979-03-15").toInstant());
        setters.get(8).set(simpleBean, "bytes1".getBytes(Charset.defaultCharset()));
        setters.get(9).set(simpleBean, "bytes2".getBytes(Charset.defaultCharset()));
        setters.get(10).set(simpleBean, new BigDecimal(42));
        setters.get(11).set(simpleBean, "stringBuilder");
        Assert.assertEquals("field1", simpleBean.getStr());
        Assert.assertEquals(((byte) (41)), simpleBean.getaByte());
        Assert.assertEquals(((short) (42)), simpleBean.getaShort());
        Assert.assertEquals(((int) (43)), simpleBean.getAnInt());
        Assert.assertEquals(((long) (44)), simpleBean.getaLong());
        Assert.assertEquals(true, simpleBean.isaBoolean());
        Assert.assertEquals(DateTime.parse("1979-03-14"), simpleBean.getDateTime());
        Assert.assertEquals(DateTime.parse("1979-03-15").toInstant(), simpleBean.getInstant());
        Assert.assertArrayEquals("Unexpected bytes", "bytes1".getBytes(Charset.defaultCharset()), simpleBean.getBytes());
        Assert.assertEquals(ByteBuffer.wrap("bytes2".getBytes(Charset.defaultCharset())), simpleBean.getByteBuffer());
        Assert.assertEquals(new BigDecimal(42), simpleBean.getBigDecimal());
        Assert.assertEquals("stringBuilder", simpleBean.getStringBuilder().toString());
    }

    @Test
    public void testGeneratedSimpleBoxedGetters() {
        TestJavaBeans.BeanWithBoxedFields bean = new TestJavaBeans.BeanWithBoxedFields();
        bean.setaByte(((byte) (41)));
        bean.setaShort(((short) (42)));
        bean.setAnInt(43);
        bean.setaLong(44L);
        bean.setaBoolean(true);
        List<FieldValueGetter> getters = JavaBeanUtils.getGetters(TestJavaBeans.BeanWithBoxedFields.class, TestJavaBeans.BEAN_WITH_BOXED_FIELDS_SCHEMA, new JavaBeanSchema.GetterTypeSupplier());
        Assert.assertEquals(((byte) (41)), getters.get(0).get(bean));
        Assert.assertEquals(((short) (42)), getters.get(1).get(bean));
        Assert.assertEquals(((int) (43)), getters.get(2).get(bean));
        Assert.assertEquals(((long) (44)), getters.get(3).get(bean));
        Assert.assertEquals(true, getters.get(4).get(bean));
    }

    @Test
    public void testGeneratedSimpleBoxedSetters() {
        TestJavaBeans.BeanWithBoxedFields bean = new TestJavaBeans.BeanWithBoxedFields();
        List<FieldValueSetter> setters = JavaBeanUtils.getSetters(TestJavaBeans.BeanWithBoxedFields.class, TestJavaBeans.BEAN_WITH_BOXED_FIELDS_SCHEMA, new SetterTypeSupplier());
        setters.get(0).set(bean, ((byte) (41)));
        setters.get(1).set(bean, ((short) (42)));
        setters.get(2).set(bean, ((int) (43)));
        setters.get(3).set(bean, ((long) (44)));
        setters.get(4).set(bean, true);
        Assert.assertEquals(((byte) (41)), bean.getaByte().byteValue());
        Assert.assertEquals(((short) (42)), bean.getaShort().shortValue());
        Assert.assertEquals(((int) (43)), bean.getAnInt().intValue());
        Assert.assertEquals(((long) (44)), bean.getaLong().longValue());
        Assert.assertEquals(true, bean.getaBoolean().booleanValue());
    }

    @Test
    public void testGeneratedByteBufferSetters() {
        TestJavaBeans.BeanWithByteArray bean = new TestJavaBeans.BeanWithByteArray();
        List<FieldValueSetter> setters = JavaBeanUtils.getSetters(TestJavaBeans.BeanWithByteArray.class, TestJavaBeans.BEAN_WITH_BYTE_ARRAY_SCHEMA, new SetterTypeSupplier());
        setters.get(0).set(bean, "field1".getBytes(Charset.defaultCharset()));
        setters.get(1).set(bean, "field2".getBytes(Charset.defaultCharset()));
        Assert.assertArrayEquals("not equal", "field1".getBytes(Charset.defaultCharset()), bean.getBytes1());
        Assert.assertEquals(ByteBuffer.wrap("field2".getBytes(Charset.defaultCharset())), bean.getBytes2());
    }
}

