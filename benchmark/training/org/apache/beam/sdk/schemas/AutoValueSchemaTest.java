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


import com.google.auto.value.AutoValue;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.apache.beam.sdk.schemas.annotations.DefaultSchema;
import org.apache.beam.sdk.schemas.annotations.SchemaCreate;
import org.apache.beam.sdk.schemas.utils.SchemaTestUtils;
import org.apache.beam.sdk.values.Row;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.Test;


/**
 * Tests for {@link AutoValueSchema}.
 */
public class AutoValueSchemaTest {
    static final DateTime DATE = DateTime.parse("1979-03-14");

    static final byte[] BYTE_ARRAY = "bytearray".getBytes(Charset.defaultCharset());

    static final StringBuilder STRING_BUILDER = new StringBuilder("stringbuilder");

    static final Schema SIMPLE_SCHEMA = Schema.builder().addStringField("str").addByteField("aByte").addInt16Field("aShort").addInt32Field("anInt").addInt64Field("aLong").addBooleanField("aBoolean").addDateTimeField("dateTime").addDateTimeField("instant").addByteArrayField("bytes").addByteArrayField("byteBuffer").addDecimalField("bigDecimal").addStringField("stringBuilder").build();

    static final Schema OUTER_SCHEMA = Schema.builder().addRowField("inner", AutoValueSchemaTest.SIMPLE_SCHEMA).build();

    // A base interface for the different varieties of our AutoValue schema to ease in testing.
    interface SimpleSchema {
        String getStr();

        byte getaByte();

        short getaShort();

        int getAnInt();

        long getaLong();

        boolean isaBoolean();

        DateTime getDateTime();

        @SuppressWarnings("mutable")
        byte[] getBytes();

        ByteBuffer getByteBuffer();

        Instant getInstant();

        BigDecimal getBigDecimal();

        StringBuilder getStringBuilder();
    }

    @AutoValue
    @DefaultSchema(AutoValueSchema.class)
    abstract static class SimpleAutoValue implements AutoValueSchemaTest.SimpleSchema {
        @Override
        public abstract String getStr();

        @Override
        public abstract byte getaByte();

        @Override
        public abstract short getaShort();

        @Override
        public abstract int getAnInt();

        @Override
        public abstract long getaLong();

        @Override
        public abstract boolean isaBoolean();

        @Override
        public abstract DateTime getDateTime();

        @Override
        @SuppressWarnings("mutable")
        public abstract byte[] getBytes();

        @Override
        public abstract ByteBuffer getByteBuffer();

        @Override
        public abstract Instant getInstant();

        @Override
        public abstract BigDecimal getBigDecimal();

        @Override
        public abstract StringBuilder getStringBuilder();
    }

    @AutoValue
    @DefaultSchema(AutoValueSchema.class)
    abstract static class SimpleAutoValueWithBuilder implements AutoValueSchemaTest.SimpleSchema {
        @Override
        public abstract String getStr();

        @Override
        public abstract byte getaByte();

        @Override
        public abstract short getaShort();

        @Override
        public abstract int getAnInt();

        @Override
        public abstract long getaLong();

        @Override
        public abstract boolean isaBoolean();

        @Override
        public abstract DateTime getDateTime();

        @Override
        @SuppressWarnings("mutable")
        public abstract byte[] getBytes();

        @Override
        public abstract ByteBuffer getByteBuffer();

        @Override
        public abstract Instant getInstant();

        @Override
        public abstract BigDecimal getBigDecimal();

        @Override
        public abstract StringBuilder getStringBuilder();

        @AutoValue.Builder
        abstract static class Builder {
            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setStr(String str);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setaByte(byte aByte);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setaShort(short aShort);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setAnInt(int anInt);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setaLong(long aLong);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setaBoolean(boolean aBoolean);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setDateTime(DateTime dateTime);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setBytes(byte[] bytes);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setByteBuffer(ByteBuffer byteBuffer);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setInstant(Instant instant);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setBigDecimal(BigDecimal bigDecimal);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder.Builder setStringBuilder(StringBuilder stringBuilder);

            abstract AutoValueSchemaTest.SimpleAutoValueWithBuilder build();
        }
    }

    @Test
    public void testSchema() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Schema schema = registry.getSchema(AutoValueSchemaTest.SimpleAutoValue.class);
        SchemaTestUtils.assertSchemaEquivalent(AutoValueSchemaTest.SIMPLE_SCHEMA, schema);
    }

    @Test
    public void testToRowConstructor() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        AutoValueSchemaTest.SimpleAutoValue value = new AutoValue_AutoValueSchemaTest_SimpleAutoValue("string", ((byte) (1)), ((short) (2)), ((int) (3)), ((long) (4)), true, AutoValueSchemaTest.DATE, AutoValueSchemaTest.BYTE_ARRAY, ByteBuffer.wrap(AutoValueSchemaTest.BYTE_ARRAY), AutoValueSchemaTest.DATE.toInstant(), BigDecimal.ONE, AutoValueSchemaTest.STRING_BUILDER);
        Row row = registry.getToRowFunction(AutoValueSchemaTest.SimpleAutoValue.class).apply(value);
        verifyRow(row);
    }

    @Test
    public void testFromRowConstructor() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row = createSimpleRow("string");
        AutoValueSchemaTest.SimpleAutoValue value = registry.getFromRowFunction(AutoValueSchemaTest.SimpleAutoValue.class).apply(row);
        verifyAutoValue(value);
    }

    @Test
    public void testToRowBuilder() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        AutoValueSchemaTest.SimpleAutoValueWithBuilder value = new AutoValue_AutoValueSchemaTest_SimpleAutoValueWithBuilder.Builder().setStr("string").setaByte(((byte) (1))).setaShort(((short) (2))).setAnInt(((int) (3))).setaLong(((long) (4))).setaBoolean(true).setDateTime(AutoValueSchemaTest.DATE).setBytes(AutoValueSchemaTest.BYTE_ARRAY).setByteBuffer(ByteBuffer.wrap(AutoValueSchemaTest.BYTE_ARRAY)).setInstant(AutoValueSchemaTest.DATE.toInstant()).setBigDecimal(BigDecimal.ONE).setStringBuilder(AutoValueSchemaTest.STRING_BUILDER).build();
        Row row = registry.getToRowFunction(AutoValueSchemaTest.SimpleAutoValueWithBuilder.class).apply(value);
        verifyRow(row);
    }

    @Test
    public void testFromRowBuilder() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row = createSimpleRow("string");
        AutoValueSchemaTest.SimpleAutoValueWithBuilder value = registry.getFromRowFunction(AutoValueSchemaTest.SimpleAutoValueWithBuilder.class).apply(row);
        verifyAutoValue(value);
    }

    // Test nested classes.
    @AutoValue
    @DefaultSchema(AutoValueSchema.class)
    abstract static class AutoValueOuter {
        abstract AutoValueSchemaTest.SimpleAutoValue getInner();
    }

    @AutoValue
    @DefaultSchema(AutoValueSchema.class)
    abstract static class AutoValueOuterWithBuilder {
        abstract AutoValueSchemaTest.SimpleAutoValue getInner();

        @AutoValue.Builder
        abstract static class Builder {
            public abstract AutoValueSchemaTest.AutoValueOuterWithBuilder.Builder setInner(AutoValueSchemaTest.SimpleAutoValue inner);

            abstract AutoValueSchemaTest.AutoValueOuterWithBuilder build();
        }
    }

    @Test
    public void testToRowNestedConstructor() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        AutoValueSchemaTest.SimpleAutoValue inner = new AutoValue_AutoValueSchemaTest_SimpleAutoValue("string", ((byte) (1)), ((short) (2)), ((int) (3)), ((long) (4)), true, AutoValueSchemaTest.DATE, AutoValueSchemaTest.BYTE_ARRAY, ByteBuffer.wrap(AutoValueSchemaTest.BYTE_ARRAY), AutoValueSchemaTest.DATE.toInstant(), BigDecimal.ONE, AutoValueSchemaTest.STRING_BUILDER);
        AutoValueSchemaTest.AutoValueOuter outer = new AutoValue_AutoValueSchemaTest_AutoValueOuter(inner);
        Row row = registry.getToRowFunction(AutoValueSchemaTest.AutoValueOuter.class).apply(outer);
        verifyRow(row.getRow("inner"));
    }

    @Test
    public void testToRowNestedBuilder() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        AutoValueSchemaTest.SimpleAutoValue inner = new AutoValue_AutoValueSchemaTest_SimpleAutoValue("string", ((byte) (1)), ((short) (2)), ((int) (3)), ((long) (4)), true, AutoValueSchemaTest.DATE, AutoValueSchemaTest.BYTE_ARRAY, ByteBuffer.wrap(AutoValueSchemaTest.BYTE_ARRAY), AutoValueSchemaTest.DATE.toInstant(), BigDecimal.ONE, AutoValueSchemaTest.STRING_BUILDER);
        AutoValueSchemaTest.AutoValueOuterWithBuilder outer = new AutoValue_AutoValueSchemaTest_AutoValueOuterWithBuilder.Builder().setInner(inner).build();
        Row row = registry.getToRowFunction(AutoValueSchemaTest.AutoValueOuterWithBuilder.class).apply(outer);
        verifyRow(row.getRow("inner"));
    }

    @Test
    public void testFromRowNestedConstructor() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row inner = createSimpleRow("string");
        Row outer = Row.withSchema(AutoValueSchemaTest.OUTER_SCHEMA).addValue(inner).build();
        AutoValueSchemaTest.AutoValueOuter value = registry.getFromRowFunction(AutoValueSchemaTest.AutoValueOuter.class).apply(outer);
        verifyAutoValue(value.getInner());
    }

    @Test
    public void testFromRowNestedBuilder() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row inner = createSimpleRow("string");
        Row outer = Row.withSchema(AutoValueSchemaTest.OUTER_SCHEMA).addValue(inner).build();
        AutoValueSchemaTest.AutoValueOuterWithBuilder value = registry.getFromRowFunction(AutoValueSchemaTest.AutoValueOuterWithBuilder.class).apply(outer);
        verifyAutoValue(value.getInner());
    }

    // Test that Beam annotations can be used to specify a creator method.
    @AutoValue
    @DefaultSchema(AutoValueSchema.class)
    abstract static class SimpleAutoValueWithStaticFactory implements AutoValueSchemaTest.SimpleSchema {
        @Override
        public abstract String getStr();

        @Override
        public abstract byte getaByte();

        @Override
        public abstract short getaShort();

        @Override
        public abstract int getAnInt();

        @Override
        public abstract long getaLong();

        @Override
        public abstract boolean isaBoolean();

        @Override
        public abstract DateTime getDateTime();

        @Override
        @SuppressWarnings("mutable")
        public abstract byte[] getBytes();

        @Override
        public abstract ByteBuffer getByteBuffer();

        @Override
        public abstract Instant getInstant();

        @Override
        public abstract BigDecimal getBigDecimal();

        @Override
        public abstract StringBuilder getStringBuilder();

        @SchemaCreate
        static AutoValueSchemaTest.SimpleAutoValueWithStaticFactory create(String str, byte aByte, short aShort, int anInt, long aLong, boolean aBoolean, DateTime dateTime, byte[] bytes, ByteBuffer byteBuffer, Instant instant, BigDecimal bigDecimal, StringBuilder stringBuilder) {
            return new AutoValue_AutoValueSchemaTest_SimpleAutoValueWithStaticFactory(str, aByte, aShort, anInt, aLong, aBoolean, dateTime, bytes, byteBuffer, instant, bigDecimal, stringBuilder);
        }
    }

    @Test
    public void testToRowStaticFactory() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        AutoValueSchemaTest.SimpleAutoValueWithStaticFactory value = AutoValueSchemaTest.SimpleAutoValueWithStaticFactory.create("string", ((byte) (1)), ((short) (2)), ((int) (3)), ((long) (4)), true, AutoValueSchemaTest.DATE, AutoValueSchemaTest.BYTE_ARRAY, ByteBuffer.wrap(AutoValueSchemaTest.BYTE_ARRAY), AutoValueSchemaTest.DATE.toInstant(), BigDecimal.ONE, AutoValueSchemaTest.STRING_BUILDER);
        Row row = registry.getToRowFunction(AutoValueSchemaTest.SimpleAutoValueWithStaticFactory.class).apply(value);
        verifyRow(row);
    }

    @Test
    public void testFromRowStaticFactory() throws NoSuchSchemaException {
        SchemaRegistry registry = SchemaRegistry.createDefault();
        Row row = createSimpleRow("string");
        AutoValueSchemaTest.SimpleAutoValueWithStaticFactory value = registry.getFromRowFunction(AutoValueSchemaTest.SimpleAutoValueWithStaticFactory.class).apply(row);
        verifyAutoValue(value);
    }
}

