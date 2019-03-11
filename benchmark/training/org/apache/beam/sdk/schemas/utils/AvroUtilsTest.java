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


import DateTimeZone.UTC;
import FieldType.INT32;
import Type.BYTES;
import Type.INT;
import Type.LONG;
import Type.NULL;
import Type.STRING;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.util.Utf8;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.org.apache.avro.Schema;
import org.apache.beam.sdk.schemas.utils.AvroUtils.TypeWithNullability;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Maps;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for conversion between AVRO records and Beam rows.
 */
@RunWith(JUnitQuickcheck.class)
public class AvroUtilsTest {
    private static final Schema NULL_SCHEMA = org.apache.avro.Schema.create(NULL);

    @Test
    public void testUnwrapNullableSchema() {
        org.apache.avro.Schema avroSchema = org.apache.avro.Schema.createUnion(org.apache.avro.Schema.create(NULL), org.apache.avro.Schema.create(STRING));
        TypeWithNullability typeWithNullability = new TypeWithNullability(avroSchema);
        Assert.assertTrue(typeWithNullability.nullable);
        Assert.assertEquals(org.apache.avro.Schema.create(STRING), typeWithNullability.type);
    }

    @Test
    public void testUnwrapNullableSchemaReordered() {
        org.apache.avro.Schema avroSchema = org.apache.avro.Schema.createUnion(org.apache.avro.Schema.create(STRING), org.apache.avro.Schema.create(NULL));
        TypeWithNullability typeWithNullability = new TypeWithNullability(avroSchema);
        Assert.assertTrue(typeWithNullability.nullable);
        Assert.assertEquals(org.apache.avro.Schema.create(STRING), typeWithNullability.type);
    }

    @Test
    public void testUnwrapNullableSchemaToUnion() {
        org.apache.avro.Schema avroSchema = org.apache.avro.Schema.createUnion(org.apache.avro.Schema.create(STRING), org.apache.avro.Schema.create(LONG), org.apache.avro.Schema.create(NULL));
        TypeWithNullability typeWithNullability = new TypeWithNullability(avroSchema);
        Assert.assertTrue(typeWithNullability.nullable);
        Assert.assertEquals(org.apache.avro.Schema.createUnion(org.apache.avro.Schema.create(STRING), org.apache.avro.Schema.create(LONG)), typeWithNullability.type);
    }

    static final byte[] BYTE_ARRAY = new byte[]{ 1, 2, 3, 4 };

    static final DateTime DATE_TIME = new DateTime().withDate(1979, 3, 14).withTime(1, 2, 3, 4).withZone(UTC);

    static final BigDecimal BIG_DECIMAL = new BigDecimal(3600);

    @Test
    public void testFromAvroSchema() {
        Assert.assertEquals(getBeamSchema(), AvroUtils.toBeamSchema(getAvroSchema()));
    }

    @Test
    public void testFromBeamSchema() {
        Schema beamSchema = getBeamSchema();
        org.apache.avro.Schema avroSchema = AvroUtils.toAvroSchema(beamSchema);
        Assert.assertEquals(getAvroSchema(), avroSchema);
    }

    @Test
    public void testNullableFieldInAvroSchema() {
        List<Field> fields = Lists.newArrayList();
        fields.add(new Field("int", ReflectData.makeNullable(org.apache.avro.Schema.create(INT)), "", null));
        fields.add(new Field("array", org.apache.avro.Schema.createArray(ReflectData.makeNullable(org.apache.avro.Schema.create(BYTES))), "", null));
        fields.add(new Field("map", org.apache.avro.Schema.createMap(ReflectData.makeNullable(org.apache.avro.Schema.create(INT))), "", null));
        org.apache.avro.Schema avroSchema = org.apache.avro.Schema.createRecord(fields);
        Schema expectedSchema = Schema.builder().addNullableField("int", INT32).addArrayField("array", FieldType.BYTES.withNullable(true)).addMapField("map", FieldType.STRING, INT32.withNullable(true)).build();
        Assert.assertEquals(expectedSchema, AvroUtils.toBeamSchema(avroSchema));
        Map<String, Object> nullMap = Maps.newHashMap();
        nullMap.put("k1", null);
        GenericRecord genericRecord = set("int", null).set("array", Lists.newArrayList(((Object) (null)))).set("map", nullMap).build();
        Row expectedRow = Row.withSchema(expectedSchema).addValue(null).addValue(Lists.newArrayList(((Object) (null)))).addValue(nullMap).build();
        Assert.assertEquals(expectedRow, AvroUtils.toBeamRowStrict(genericRecord, expectedSchema));
    }

    @Test
    public void testNullableFieldsInBeamSchema() {
        Schema beamSchema = Schema.builder().addNullableField("int", INT32).addArrayField("array", INT32.withNullable(true)).addMapField("map", FieldType.STRING, INT32.withNullable(true)).build();
        List<Field> fields = Lists.newArrayList();
        fields.add(new Field("int", ReflectData.makeNullable(org.apache.avro.Schema.create(INT)), "", null));
        fields.add(new Field("array", org.apache.avro.Schema.createArray(ReflectData.makeNullable(org.apache.avro.Schema.create(INT))), "", null));
        fields.add(new Field("map", org.apache.avro.Schema.createMap(ReflectData.makeNullable(org.apache.avro.Schema.create(INT))), "", null));
        org.apache.avro.Schema avroSchema = org.apache.avro.Schema.createRecord(fields);
        Assert.assertEquals(avroSchema, AvroUtils.toAvroSchema(beamSchema));
        Map<Utf8, Object> nullMapUtf8 = Maps.newHashMap();
        nullMapUtf8.put(new Utf8("k1"), null);
        Map<String, Object> nullMapString = Maps.newHashMap();
        nullMapString.put("k1", null);
        GenericRecord expectedGenericRecord = set("int", null).set("array", Lists.newArrayList(((Object) (null)))).set("map", nullMapUtf8).build();
        Row row = Row.withSchema(beamSchema).addValue(null).addValue(Lists.newArrayList(((Object) (null)))).addValue(nullMapString).build();
        Assert.assertEquals(expectedGenericRecord, AvroUtils.toGenericRecord(row, avroSchema));
    }

    @Test
    public void testBeamRowToGenericRecord() {
        GenericRecord genericRecord = AvroUtils.toGenericRecord(getBeamRow(), null);
        Assert.assertEquals(getAvroSchema(), genericRecord.getSchema());
        Assert.assertEquals(getGenericRecord(), genericRecord);
    }

    @Test
    public void testGenericRecordToBeamRow() {
        Row row = AvroUtils.toBeamRowStrict(getGenericRecord(), null);
        Assert.assertEquals(getBeamRow(), row);
    }

    static class ContainsField extends BaseMatcher<org.apache.avro.Schema> {
        private final Function<org.apache.avro.Schema, Boolean> predicate;

        ContainsField(final Function<org.apache.avro.Schema, Boolean> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean matches(final Object item0) {
            if (!(item0 instanceof org.apache.avro.Schema)) {
                return false;
            }
            org.apache.avro.Schema item = ((org.apache.avro.Schema) (item0));
            if (predicate.apply(item)) {
                return true;
            }
            switch (item.getType()) {
                case RECORD :
                    return item.getFields().stream().anyMatch(( x) -> matches(x.schema()));
                case UNION :
                    return item.getTypes().stream().anyMatch(this::matches);
                case ARRAY :
                    return matches(item.getElementType());
                case MAP :
                    return matches(item.getValueType());
                default :
                    return false;
            }
        }

        @Override
        public void describeTo(final Description description) {
        }
    }
}

