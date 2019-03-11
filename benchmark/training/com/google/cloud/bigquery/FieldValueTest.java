/**
 * Copyright 2015 Google LLC
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


import FieldValue.Attribute.PRIMITIVE;
import FieldValue.Attribute.RECORD;
import FieldValue.Attribute.REPEATED;
import com.google.api.client.util.Data;
import com.google.api.services.bigquery.model.TableCell;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class FieldValueTest {
    private static final byte[] BYTES = new byte[]{ 13, 14, 10, 13 };

    private static final String BYTES_BASE64 = BaseEncoding.base64().encode(FieldValueTest.BYTES);

    private static final TableCell BOOLEAN_FIELD = new TableCell().setV("false");

    private static final Map<String, String> INTEGER_FIELD = ImmutableMap.of("v", "1");

    private static final Map<String, String> FLOAT_FIELD = ImmutableMap.of("v", "1.5");

    private static final Map<String, String> GEOGRAPHY_FIELD = ImmutableMap.of("v", "POINT(-122.350220 47.649154)");

    private static final Map<String, String> NUMERIC_FIELD = ImmutableMap.of("v", "123456789.123456789");

    private static final Map<String, String> STRING_FIELD = ImmutableMap.of("v", "string");

    private static final Map<String, String> TIMESTAMP_FIELD = ImmutableMap.of("v", "42");

    private static final Map<String, String> BYTES_FIELD = ImmutableMap.of("v", FieldValueTest.BYTES_BASE64);

    private static final Map<String, String> NULL_FIELD = ImmutableMap.of("v", Data.nullOf(String.class));

    private static final Map<String, Object> REPEATED_FIELD = ImmutableMap.<String, Object>of("v", ImmutableList.<Object>of(FieldValueTest.INTEGER_FIELD, FieldValueTest.INTEGER_FIELD));

    private static final Map<String, Object> RECORD_FIELD = ImmutableMap.<String, Object>of("f", ImmutableList.<Object>of(FieldValueTest.FLOAT_FIELD, FieldValueTest.TIMESTAMP_FIELD));

    @Test
    public void testFromPb() {
        FieldValue value = FieldValue.fromPb(FieldValueTest.BOOLEAN_FIELD);
        Assert.assertEquals(PRIMITIVE, value.getAttribute());
        Assert.assertFalse(value.getBooleanValue());
        value = FieldValue.fromPb(FieldValueTest.INTEGER_FIELD);
        Assert.assertEquals(PRIMITIVE, value.getAttribute());
        Assert.assertEquals(1, value.getLongValue());
        value = FieldValue.fromPb(FieldValueTest.FLOAT_FIELD);
        Assert.assertEquals(PRIMITIVE, value.getAttribute());
        Assert.assertEquals(1.5, value.getDoubleValue(), 0);
        value = FieldValue.fromPb(FieldValueTest.GEOGRAPHY_FIELD);
        Assert.assertEquals(PRIMITIVE, value.getAttribute());
        Assert.assertEquals("POINT(-122.350220 47.649154)", value.getStringValue());
        value = FieldValue.fromPb(FieldValueTest.NUMERIC_FIELD);
        Assert.assertEquals(PRIMITIVE, value.getAttribute());
        Assert.assertEquals(new BigDecimal("123456789.123456789"), value.getNumericValue());
        value = FieldValue.fromPb(FieldValueTest.STRING_FIELD);
        Assert.assertEquals(PRIMITIVE, value.getAttribute());
        Assert.assertEquals("string", value.getStringValue());
        value = FieldValue.fromPb(FieldValueTest.TIMESTAMP_FIELD);
        Assert.assertEquals(PRIMITIVE, value.getAttribute());
        Assert.assertEquals(42000000, value.getTimestampValue());
        value = FieldValue.fromPb(FieldValueTest.BYTES_FIELD);
        Assert.assertEquals(PRIMITIVE, value.getAttribute());
        Assert.assertArrayEquals(FieldValueTest.BYTES, value.getBytesValue());
        value = FieldValue.fromPb(FieldValueTest.NULL_FIELD);
        Assert.assertNull(value.getValue());
        value = FieldValue.fromPb(FieldValueTest.REPEATED_FIELD);
        Assert.assertEquals(REPEATED, value.getAttribute());
        Assert.assertEquals(FieldValue.fromPb(FieldValueTest.INTEGER_FIELD), value.getRepeatedValue().get(0));
        Assert.assertEquals(FieldValue.fromPb(FieldValueTest.INTEGER_FIELD), value.getRepeatedValue().get(1));
        value = FieldValue.fromPb(FieldValueTest.RECORD_FIELD);
        Assert.assertEquals(RECORD, value.getAttribute());
        Assert.assertEquals(FieldValue.fromPb(FieldValueTest.FLOAT_FIELD), value.getRepeatedValue().get(0));
        Assert.assertEquals(FieldValue.fromPb(FieldValueTest.TIMESTAMP_FIELD), value.getRepeatedValue().get(1));
    }

    @Test
    public void testEquals() {
        FieldValue booleanValue = FieldValue.of(PRIMITIVE, "false");
        Assert.assertEquals(booleanValue, FieldValue.fromPb(FieldValueTest.BOOLEAN_FIELD));
        Assert.assertEquals(booleanValue.hashCode(), FieldValue.fromPb(FieldValueTest.BOOLEAN_FIELD).hashCode());
        FieldValue integerValue = FieldValue.of(PRIMITIVE, "1");
        Assert.assertEquals(integerValue, FieldValue.fromPb(FieldValueTest.INTEGER_FIELD));
        Assert.assertEquals(integerValue.hashCode(), FieldValue.fromPb(FieldValueTest.INTEGER_FIELD).hashCode());
        FieldValue floatValue = FieldValue.of(PRIMITIVE, "1.5");
        Assert.assertEquals(floatValue, FieldValue.fromPb(FieldValueTest.FLOAT_FIELD));
        Assert.assertEquals(floatValue.hashCode(), FieldValue.fromPb(FieldValueTest.FLOAT_FIELD).hashCode());
        FieldValue geographyValue = FieldValue.of(PRIMITIVE, "POINT(-122.350220 47.649154)");
        Assert.assertEquals(geographyValue, FieldValue.fromPb(FieldValueTest.GEOGRAPHY_FIELD));
        Assert.assertEquals(geographyValue.hashCode(), FieldValue.fromPb(FieldValueTest.GEOGRAPHY_FIELD).hashCode());
        FieldValue numericValue = FieldValue.of(PRIMITIVE, "123456789.123456789");
        Assert.assertEquals(numericValue, FieldValue.fromPb(FieldValueTest.NUMERIC_FIELD));
        Assert.assertEquals(numericValue.hashCode(), FieldValue.fromPb(FieldValueTest.NUMERIC_FIELD).hashCode());
        FieldValue stringValue = FieldValue.of(PRIMITIVE, "string");
        Assert.assertEquals(stringValue, FieldValue.fromPb(FieldValueTest.STRING_FIELD));
        Assert.assertEquals(stringValue.hashCode(), FieldValue.fromPb(FieldValueTest.STRING_FIELD).hashCode());
        FieldValue timestampValue = FieldValue.of(PRIMITIVE, "42");
        Assert.assertEquals(timestampValue, FieldValue.fromPb(FieldValueTest.TIMESTAMP_FIELD));
        Assert.assertEquals(timestampValue.hashCode(), FieldValue.fromPb(FieldValueTest.TIMESTAMP_FIELD).hashCode());
        FieldValue bytesValue = FieldValue.of(PRIMITIVE, FieldValueTest.BYTES_BASE64);
        Assert.assertEquals(bytesValue, FieldValue.fromPb(FieldValueTest.BYTES_FIELD));
        Assert.assertEquals(bytesValue.hashCode(), FieldValue.fromPb(FieldValueTest.BYTES_FIELD).hashCode());
        FieldValue nullValue = FieldValue.of(PRIMITIVE, null);
        Assert.assertEquals(nullValue, FieldValue.fromPb(FieldValueTest.NULL_FIELD));
        Assert.assertEquals(nullValue.hashCode(), FieldValue.fromPb(FieldValueTest.NULL_FIELD).hashCode());
        FieldValue repeatedValue = FieldValue.of(REPEATED, ImmutableList.of(integerValue, integerValue));
        Assert.assertEquals(repeatedValue, FieldValue.fromPb(FieldValueTest.REPEATED_FIELD));
        Assert.assertEquals(repeatedValue.hashCode(), FieldValue.fromPb(FieldValueTest.REPEATED_FIELD).hashCode());
        FieldValue recordValue = FieldValue.of(RECORD, ImmutableList.of(floatValue, timestampValue));
        Assert.assertEquals(recordValue, FieldValue.fromPb(FieldValueTest.RECORD_FIELD));
        Assert.assertEquals(recordValue.hashCode(), FieldValue.fromPb(FieldValueTest.RECORD_FIELD).hashCode());
    }
}

