/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.bigquery;


import Attribute.PRIMITIVE;
import Attribute.REPEATED;
import LegacySQLTypeName.BOOLEAN;
import LegacySQLTypeName.BYTES;
import LegacySQLTypeName.FLOAT;
import LegacySQLTypeName.INTEGER;
import LegacySQLTypeName.NUMERIC;
import LegacySQLTypeName.RECORD;
import LegacySQLTypeName.STRING;
import LegacySQLTypeName.TIMESTAMP;
import com.google.api.client.util.Data;
import com.google.api.services.bigquery.model.TableCell;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class FieldValueListTest {
    private static final byte[] BYTES = new byte[]{ 13, 14, 10, 13 };

    private static final String BYTES_BASE64 = BaseEncoding.base64().encode(FieldValueListTest.BYTES);

    private static final TableCell booleanPb = new TableCell().setV("false");

    private final FieldList schema = FieldList.of(Field.of("first", BOOLEAN), Field.of("second", INTEGER), Field.of("third", FLOAT), Field.of("fourth", STRING), Field.of("fifth", TIMESTAMP), Field.of("sixth", LegacySQLTypeName.BYTES), Field.of("seventh", STRING), Field.of("eight", INTEGER), Field.of("ninth", RECORD, Field.of("first", FLOAT), Field.of("second", TIMESTAMP)), Field.of("tenth", NUMERIC));

    private final Map<String, String> integerPb = ImmutableMap.of("v", "1");

    private final Map<String, String> floatPb = ImmutableMap.of("v", "1.5");

    private final Map<String, String> stringPb = ImmutableMap.of("v", "string");

    private final Map<String, String> timestampPb = ImmutableMap.of("v", "42");

    private final Map<String, String> bytesPb = ImmutableMap.of("v", FieldValueListTest.BYTES_BASE64);

    private final Map<String, String> nullPb = ImmutableMap.of("v", Data.nullOf(String.class));

    private final Map<String, Object> repeatedPb = ImmutableMap.<String, Object>of("v", ImmutableList.<Object>of(integerPb, integerPb));

    private final Map<String, Object> recordPb = ImmutableMap.<String, Object>of("f", ImmutableList.<Object>of(floatPb, timestampPb));

    private final Map<String, String> numericPb = ImmutableMap.of("v", "123456789.123456789");

    private final FieldValue booleanFv = FieldValue.of(PRIMITIVE, "false");

    private final FieldValue integerFv = FieldValue.of(PRIMITIVE, "1");

    private final FieldValue floatFv = FieldValue.of(PRIMITIVE, "1.5");

    private final FieldValue stringFv = FieldValue.of(PRIMITIVE, "string");

    private final FieldValue timestampFv = FieldValue.of(PRIMITIVE, "42");

    private final FieldValue bytesFv = FieldValue.of(PRIMITIVE, FieldValueListTest.BYTES_BASE64);

    private final FieldValue nullFv = FieldValue.of(PRIMITIVE, null);

    private final FieldValue repeatedFv = FieldValue.of(REPEATED, FieldValueList.of(ImmutableList.of(integerFv, integerFv)));

    private final FieldValue recordFv = FieldValue.of(Attribute.RECORD, FieldValueList.of(ImmutableList.of(floatFv, timestampFv), schema.get("ninth").getSubFields()));

    private final FieldValue numericFv = FieldValue.of(PRIMITIVE, "123456789.123456789");

    private final List<?> fieldValuesPb = ImmutableList.of(FieldValueListTest.booleanPb, integerPb, floatPb, stringPb, timestampPb, bytesPb, nullPb, repeatedPb, recordPb, numericPb);

    private final FieldValueList fieldValues = FieldValueList.of(ImmutableList.of(booleanFv, integerFv, floatFv, stringFv, timestampFv, bytesFv, nullFv, repeatedFv, recordFv, numericFv), schema);

    @Test
    public void testFromPb() {
        Assert.assertEquals(fieldValues, FieldValueList.fromPb(fieldValuesPb, schema));
        // Schema does not influence values equality
        Assert.assertEquals(fieldValues, FieldValueList.fromPb(fieldValuesPb, null));
    }

    @Test
    public void testGetByIndex() {
        Assert.assertEquals(10, fieldValues.size());
        Assert.assertEquals(booleanFv, fieldValues.get(0));
        Assert.assertEquals(integerFv, fieldValues.get(1));
        Assert.assertEquals(floatFv, fieldValues.get(2));
        Assert.assertEquals(stringFv, fieldValues.get(3));
        Assert.assertEquals(timestampFv, fieldValues.get(4));
        Assert.assertEquals(bytesFv, fieldValues.get(5));
        Assert.assertEquals(nullFv, fieldValues.get(6));
        Assert.assertEquals(repeatedFv, fieldValues.get(7));
        Assert.assertEquals(2, fieldValues.get(7).getRepeatedValue().size());
        Assert.assertEquals(integerFv, fieldValues.get(7).getRepeatedValue().get(0));
        Assert.assertEquals(integerFv, fieldValues.get(7).getRepeatedValue().get(1));
        Assert.assertEquals(recordFv, fieldValues.get(8));
        Assert.assertEquals(2, fieldValues.get(8).getRecordValue().size());
        Assert.assertEquals(floatFv, fieldValues.get(8).getRecordValue().get(0));
        Assert.assertEquals(timestampFv, fieldValues.get(8).getRecordValue().get(1));
        Assert.assertEquals(numericFv, fieldValues.get(9));
    }

    @Test
    public void testGetByName() {
        Assert.assertEquals(10, fieldValues.size());
        Assert.assertEquals(booleanFv, fieldValues.get("first"));
        Assert.assertEquals(integerFv, fieldValues.get("second"));
        Assert.assertEquals(floatFv, fieldValues.get("third"));
        Assert.assertEquals(stringFv, fieldValues.get("fourth"));
        Assert.assertEquals(timestampFv, fieldValues.get("fifth"));
        Assert.assertEquals(bytesFv, fieldValues.get("sixth"));
        Assert.assertEquals(nullFv, fieldValues.get("seventh"));
        Assert.assertEquals(repeatedFv, fieldValues.get("eight"));
        Assert.assertEquals(2, fieldValues.get("eight").getRepeatedValue().size());
        Assert.assertEquals(integerFv, fieldValues.get("eight").getRepeatedValue().get(0));
        Assert.assertEquals(integerFv, fieldValues.get("eight").getRepeatedValue().get(1));
        Assert.assertEquals(recordFv, fieldValues.get("ninth"));
        Assert.assertEquals(2, fieldValues.get("ninth").getRecordValue().size());
        Assert.assertEquals(floatFv, fieldValues.get("ninth").getRecordValue().get("first"));
        Assert.assertEquals(timestampFv, fieldValues.get("ninth").getRecordValue().get("second"));
        Assert.assertEquals(numericFv, fieldValues.get("tenth"));
    }

    @Test
    public void testNullSchema() {
        FieldValueList fieldValuesNoSchema = FieldValueList.of(ImmutableList.of(booleanFv, integerFv, floatFv, stringFv, timestampFv, bytesFv, nullFv, repeatedFv, recordFv, numericFv));
        Assert.assertEquals(fieldValues, fieldValuesNoSchema);
        UnsupportedOperationException exception = null;
        try {
            fieldValuesNoSchema.get("first");
        } catch (UnsupportedOperationException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }

    @Test
    public void testGetNonExistentField() {
        IllegalArgumentException exception = null;
        try {
            fieldValues.get("nonexistent");
        } catch (IllegalArgumentException e) {
            exception = e;
        }
        Assert.assertNotNull(exception);
    }
}

