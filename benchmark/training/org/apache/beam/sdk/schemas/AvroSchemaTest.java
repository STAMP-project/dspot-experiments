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
import FieldType.BYTES;
import FieldType.DATETIME;
import FieldType.DOUBLE;
import FieldType.FLOAT;
import FieldType.INT32;
import FieldType.INT64;
import FieldType.STRING;
import TestAvro.SCHEMA;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.AvroIgnore;
import org.apache.avro.reflect.AvroName;
import org.apache.avro.reflect.AvroSchema;
import org.apache.avro.reflect.Nullable;
import org.apache.avro.util.Utf8;
import org.apache.beam.sdk.schemas.LogicalTypes.FixedBytes;
import org.apache.beam.sdk.schemas.Schema.FieldType;
import org.apache.beam.sdk.schemas.utils.AvroUtils;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for AVRO schema classes.
 */
public class AvroSchemaTest {
    /**
     * A test POJO that corresponds to our AVRO schema.
     */
    public static class AvroSubPojo {
        @AvroName("bool_non_nullable")
        public boolean boolNonNullable;

        @AvroName("int")
        @Nullable
        public Integer anInt;

        public AvroSubPojo(boolean boolNonNullable, Integer anInt) {
            this.boolNonNullable = boolNonNullable;
            this.anInt = anInt;
        }

        public AvroSubPojo() {
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof AvroSchemaTest.AvroSubPojo)) {
                return false;
            }
            AvroSchemaTest.AvroSubPojo that = ((AvroSchemaTest.AvroSubPojo) (o));
            return ((boolNonNullable) == (that.boolNonNullable)) && (Objects.equals(anInt, that.anInt));
        }

        @Override
        public int hashCode() {
            return Objects.hash(boolNonNullable, anInt);
        }

        @Override
        public String toString() {
            return (((("AvroSubPojo{" + "boolNonNullable=") + (boolNonNullable)) + ", anInt=") + (anInt)) + '}';
        }
    }

    /**
     * A test POJO that corresponds to our AVRO schema.
     */
    public static class AvroPojo {
        @AvroName("bool_non_nullable")
        public boolean boolNonNullable;

        @Nullable
        @AvroName("int")
        public Integer anInt;

        @Nullable
        @AvroName("long")
        public Long aLong;

        @AvroName("float")
        @Nullable
        public Float aFloat;

        @AvroName("double")
        @Nullable
        public Double aDouble;

        @Nullable
        public String string;

        @Nullable
        public ByteBuffer bytes;

        @AvroSchema("{\"type\": \"fixed\", \"size\": 4, \"name\": \"fixed4\"}")
        @Nullable
        public byte[] fixed;

        @Nullable
        public AvroSchemaTest.AvroSubPojo row;

        @Nullable
        public List<AvroSchemaTest.AvroSubPojo> array;

        @Nullable
        public Map<String, AvroSchemaTest.AvroSubPojo> map;

        @AvroIgnore
        String extraField;

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof AvroSchemaTest.AvroPojo)) {
                return false;
            }
            AvroSchemaTest.AvroPojo avroPojo = ((AvroSchemaTest.AvroPojo) (o));
            return (((((((((((boolNonNullable) == (avroPojo.boolNonNullable)) && (Objects.equals(anInt, avroPojo.anInt))) && (Objects.equals(aLong, avroPojo.aLong))) && (Objects.equals(aFloat, avroPojo.aFloat))) && (Objects.equals(aDouble, avroPojo.aDouble))) && (Objects.equals(string, avroPojo.string))) && (Objects.equals(bytes, avroPojo.bytes))) && (Arrays.equals(fixed, avroPojo.fixed))) && (Objects.equals(row, avroPojo.row))) && (Objects.equals(array, avroPojo.array))) && (Objects.equals(map, avroPojo.map));
        }

        @Override
        public int hashCode() {
            return Objects.hash(boolNonNullable, anInt, aLong, aFloat, aDouble, string, bytes, Arrays.hashCode(fixed), row, array, map);
        }

        public AvroPojo(boolean boolNonNullable, int anInt, long aLong, float aFloat, double aDouble, String string, ByteBuffer bytes, byte[] fixed, AvroSchemaTest.AvroSubPojo row, List<AvroSchemaTest.AvroSubPojo> array, Map<String, AvroSchemaTest.AvroSubPojo> map) {
            this.boolNonNullable = boolNonNullable;
            this.anInt = anInt;
            this.aLong = aLong;
            this.aFloat = aFloat;
            this.aDouble = aDouble;
            this.string = string;
            this.bytes = bytes;
            this.fixed = fixed;
            this.row = row;
            this.array = array;
            this.map = map;
            this.extraField = "";
        }

        public AvroPojo() {
        }
    }

    private static final Schema SUBSCHEMA = Schema.builder().addField("bool_non_nullable", BOOLEAN).addNullableField("int", INT32).build();

    private static final FieldType SUB_TYPE = FieldType.row(AvroSchemaTest.SUBSCHEMA).withNullable(true);

    private static final Schema SCHEMA = Schema.builder().addField("bool_non_nullable", BOOLEAN).addNullableField("int", INT32).addNullableField("long", INT64).addNullableField("float", FLOAT).addNullableField("double", DOUBLE).addNullableField("string", STRING).addNullableField("bytes", BYTES).addField("fixed", FieldType.logicalType(FixedBytes.of(4))).addNullableField("timestampMillis", DATETIME).addNullableField("row", AvroSchemaTest.SUB_TYPE).addNullableField("array", FieldType.array(AvroSchemaTest.SUB_TYPE)).addNullableField("map", FieldType.map(STRING, AvroSchemaTest.SUB_TYPE)).build();

    private static final Schema POJO_SCHEMA = Schema.builder().addField("bool_non_nullable", BOOLEAN).addNullableField("int", INT32).addNullableField("long", INT64).addNullableField("float", FLOAT).addNullableField("double", DOUBLE).addNullableField("string", STRING).addNullableField("bytes", BYTES).addField("fixed", FieldType.logicalType(FixedBytes.of(4))).addNullableField("row", AvroSchemaTest.SUB_TYPE).addNullableField("array", FieldType.array(AvroSchemaTest.SUB_TYPE.withNullable(false))).addNullableField("map", FieldType.map(STRING, AvroSchemaTest.SUB_TYPE.withNullable(false))).build();

    private static final byte[] BYTE_ARRAY = new byte[]{ 1, 2, 3, 4 };

    private static final DateTime DATE_TIME = new DateTime().withDate(1979, 3, 14).withTime(1, 2, 3, 4);

    private static final TestAvroNested AVRO_NESTED_SPECIFIC_RECORD = new TestAvroNested(true, 42);

    private static final TestAvro AVRO_SPECIFIC_RECORD = new TestAvro(true, 43, 44L, ((float) (44.1)), ((double) (44.2)), "mystring", ByteBuffer.wrap(AvroSchemaTest.BYTE_ARRAY), new fixed4(AvroSchemaTest.BYTE_ARRAY), AvroSchemaTest.DATE_TIME, AvroSchemaTest.AVRO_NESTED_SPECIFIC_RECORD, ImmutableList.of(AvroSchemaTest.AVRO_NESTED_SPECIFIC_RECORD, AvroSchemaTest.AVRO_NESTED_SPECIFIC_RECORD), ImmutableMap.of("k1", AvroSchemaTest.AVRO_NESTED_SPECIFIC_RECORD, "k2", AvroSchemaTest.AVRO_NESTED_SPECIFIC_RECORD));

    private static final GenericRecord AVRO_NESTED_GENERIC_RECORD = set("bool_non_nullable", true).set("int", 42).build();

    private static final GenericRecord AVRO_GENERIC_RECORD = set("bool_non_nullable", true).set("int", 43).set("long", 44L).set("float", ((float) (44.1))).set("double", ((double) (44.2))).set("string", new Utf8("mystring")).set("bytes", ByteBuffer.wrap(AvroSchemaTest.BYTE_ARRAY)).set("fixed", GenericData.get().createFixed(null, AvroSchemaTest.BYTE_ARRAY, org.apache.avro.Schema.createFixed("fixed4", "", "", 4))).set("timestampMillis", AvroSchemaTest.DATE_TIME.getMillis()).set("row", AvroSchemaTest.AVRO_NESTED_GENERIC_RECORD).set("array", ImmutableList.of(AvroSchemaTest.AVRO_NESTED_GENERIC_RECORD, AvroSchemaTest.AVRO_NESTED_GENERIC_RECORD)).set("map", ImmutableMap.of(new Utf8("k1"), AvroSchemaTest.AVRO_NESTED_GENERIC_RECORD, new Utf8("k2"), AvroSchemaTest.AVRO_NESTED_GENERIC_RECORD)).build();

    private static final Row NESTED_ROW = Row.withSchema(AvroSchemaTest.SUBSCHEMA).addValues(true, 42).build();

    private static final Row ROW = Row.withSchema(AvroSchemaTest.SCHEMA).addValues(true, 43, 44L, ((float) (44.1)), ((double) (44.2)), "mystring", ByteBuffer.wrap(AvroSchemaTest.BYTE_ARRAY), AvroSchemaTest.BYTE_ARRAY, AvroSchemaTest.DATE_TIME, AvroSchemaTest.NESTED_ROW, ImmutableList.of(AvroSchemaTest.NESTED_ROW, AvroSchemaTest.NESTED_ROW), ImmutableMap.of("k1", AvroSchemaTest.NESTED_ROW, "k2", AvroSchemaTest.NESTED_ROW)).build();

    @Test
    public void testSpecificRecordSchema() {
        Assert.assertEquals(AvroSchemaTest.SCHEMA, new AvroRecordSchema().schemaFor(TypeDescriptor.of(TestAvro.class)));
    }

    @Test
    public void testPojoSchema() {
        Assert.assertEquals(AvroSchemaTest.POJO_SCHEMA, new AvroRecordSchema().schemaFor(TypeDescriptor.of(AvroSchemaTest.AvroPojo.class)));
    }

    @Test
    public void testSpecificRecordToRow() {
        SerializableFunction<TestAvro, Row> toRow = new AvroRecordSchema().toRowFunction(TypeDescriptor.of(TestAvro.class));
        Assert.assertEquals(AvroSchemaTest.ROW, toRow.apply(AvroSchemaTest.AVRO_SPECIFIC_RECORD));
    }

    @Test
    public void testRowToSpecificRecord() {
        SerializableFunction<Row, TestAvro> fromRow = new AvroRecordSchema().fromRowFunction(TypeDescriptor.of(TestAvro.class));
        Assert.assertEquals(AvroSchemaTest.AVRO_SPECIFIC_RECORD, fromRow.apply(AvroSchemaTest.ROW));
    }

    @Test
    public void testGenericRecordToRow() {
        SerializableFunction<GenericRecord, Row> toRow = AvroUtils.getGenericRecordToRowFunction(AvroSchemaTest.SCHEMA);
        Assert.assertEquals(AvroSchemaTest.ROW, toRow.apply(AvroSchemaTest.AVRO_GENERIC_RECORD));
    }

    @Test
    public void testRowToGenericRecord() {
        SerializableFunction<Row, GenericRecord> fromRow = AvroUtils.getRowToGenericRecordFunction(TestAvro.SCHEMA.);
        Assert.assertEquals(AvroSchemaTest.AVRO_GENERIC_RECORD, fromRow.apply(AvroSchemaTest.ROW));
    }

    private static final AvroSchemaTest.AvroSubPojo SUB_POJO = new AvroSchemaTest.AvroSubPojo(true, 42);

    private static final AvroSchemaTest.AvroPojo AVRO_POJO = new AvroSchemaTest.AvroPojo(true, 43, 44L, ((float) (44.1)), ((double) (44.2)), "mystring", ByteBuffer.wrap(AvroSchemaTest.BYTE_ARRAY), AvroSchemaTest.BYTE_ARRAY, AvroSchemaTest.SUB_POJO, ImmutableList.of(AvroSchemaTest.SUB_POJO, AvroSchemaTest.SUB_POJO), ImmutableMap.of("k1", AvroSchemaTest.SUB_POJO, "k2", AvroSchemaTest.SUB_POJO));

    private static final Row ROW_FOR_POJO = Row.withSchema(AvroSchemaTest.POJO_SCHEMA).addValues(true, 43, 44L, ((float) (44.1)), ((double) (44.2)), "mystring", ByteBuffer.wrap(AvroSchemaTest.BYTE_ARRAY), AvroSchemaTest.BYTE_ARRAY, AvroSchemaTest.NESTED_ROW, ImmutableList.of(AvroSchemaTest.NESTED_ROW, AvroSchemaTest.NESTED_ROW), ImmutableMap.of("k1", AvroSchemaTest.NESTED_ROW, "k2", AvroSchemaTest.NESTED_ROW)).build();

    @Test
    public void testPojoRecordToRow() {
        SerializableFunction<AvroSchemaTest.AvroPojo, Row> toRow = new AvroRecordSchema().toRowFunction(TypeDescriptor.of(AvroSchemaTest.AvroPojo.class));
        Assert.assertEquals(AvroSchemaTest.ROW_FOR_POJO, toRow.apply(AvroSchemaTest.AVRO_POJO));
    }

    @Test
    public void testRowToPojo() {
        SerializableFunction<Row, AvroSchemaTest.AvroPojo> fromRow = new AvroRecordSchema().fromRowFunction(TypeDescriptor.of(AvroSchemaTest.AvroPojo.class));
        Assert.assertEquals(AvroSchemaTest.AVRO_POJO, fromRow.apply(AvroSchemaTest.ROW_FOR_POJO));
    }
}

