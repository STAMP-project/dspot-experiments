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
package org.apache.beam.sdk.util;


import FieldType.BOOLEAN;
import FieldType.BYTE;
import FieldType.DATETIME;
import FieldType.DOUBLE;
import FieldType.FLOAT;
import FieldType.INT16;
import FieldType.INT32;
import FieldType.INT64;
import FieldType.STRING;
import java.math.BigDecimal;
import java.util.Arrays;
import org.apache.beam.sdk.schemas.Schema;
import org.apache.beam.sdk.schemas.Schema.FieldType;
import org.apache.beam.sdk.util.RowJsonDeserializer.UnsupportedRowJsonException;
import org.apache.beam.sdk.values.Row;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit tests for {@link RowJsonDeserializer}.
 */
public class RowJsonDeserializerTest {
    private static final Boolean BOOLEAN_TRUE_VALUE = true;

    private static final String BOOLEAN_TRUE_STRING = "true";

    private static final Byte BYTE_VALUE = 126;

    private static final String BYTE_STRING = "126";

    private static final Short SHORT_VALUE = 32766;

    private static final String SHORT_STRING = "32766";

    private static final Integer INT_VALUE = 2147483646;

    private static final String INT_STRING = "2147483646";

    private static final Long LONG_VALUE = 9223372036854775806L;

    private static final String LONG_STRING = "9223372036854775806";

    private static final Float FLOAT_VALUE = 102000.0F;

    private static final String FLOAT_STRING = "1.02e5";

    private static final Double DOUBLE_VALUE = 1.02;

    private static final String DOUBLE_STRING = "1.02";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testParsesFlatRow() throws Exception {
        Schema schema = Schema.builder().addByteField("f_byte").addInt16Field("f_int16").addInt32Field("f_int32").addInt64Field("f_int64").addFloatField("f_float").addDoubleField("f_double").addBooleanField("f_boolean").addStringField("f_string").addDecimalField("f_decimal").build();
        String rowString = "{\n" + ((((((((("\"f_byte\" : 12,\n" + "\"f_int16\" : 22,\n") + "\"f_int32\" : 32,\n") + "\"f_int64\" : 42,\n") + "\"f_float\" : 1.02E5,\n") + "\"f_double\" : 62.2,\n") + "\"f_boolean\" : true,\n") + "\"f_string\" : \"hello\",\n") + "\"f_decimal\" : 123.12\n") + "}");
        RowJsonDeserializer deserializer = RowJsonDeserializer.forSchema(schema);
        Row parsedRow = newObjectMapperWith(deserializer).readValue(rowString, Row.class);
        Row expectedRow = Row.withSchema(schema).addValues(((byte) (12)), ((short) (22)), 32, ((long) (42)), 102000.0F, 62.2, true, "hello", new BigDecimal("123.12")).build();
        Assert.assertEquals(expectedRow, parsedRow);
    }

    @Test
    public void testParsesArrayField() throws Exception {
        Schema schema = Schema.builder().addInt32Field("f_int32").addArrayField("f_intArray", INT32).build();
        String rowString = "{\n" + (("\"f_int32\" : 32,\n" + "\"f_intArray\" : [ 1, 2, 3, 4, 5]\n") + "}");
        RowJsonDeserializer deserializer = RowJsonDeserializer.forSchema(schema);
        Row parsedRow = newObjectMapperWith(deserializer).readValue(rowString, Row.class);
        Row expectedRow = Row.withSchema(schema).addValues(32, Arrays.asList(1, 2, 3, 4, 5)).build();
        Assert.assertEquals(expectedRow, parsedRow);
    }

    @Test
    public void testParsesArrayOfArrays() throws Exception {
        Schema schema = Schema.builder().addArrayField("f_arrayOfIntArrays", FieldType.array(INT32)).build();
        String rowString = "{\n" + ("\"f_arrayOfIntArrays\" : [ [1, 2], [3, 4], [5]]\n" + "}");
        RowJsonDeserializer deserializer = RowJsonDeserializer.forSchema(schema);
        Row parsedRow = newObjectMapperWith(deserializer).readValue(rowString, Row.class);
        Row expectedRow = Row.withSchema(schema).addArray(Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5)).build();
        Assert.assertEquals(expectedRow, parsedRow);
    }

    @Test
    public void testThrowsForMismatchedArrayField() throws Exception {
        Schema schema = Schema.builder().addArrayField("f_arrayOfIntArrays", FieldType.array(INT32)).build();
        String rowString = "{\n" + ("\"f_arrayOfIntArrays\" : { }\n"// expect array, get object
         + "}");
        RowJsonDeserializer deserializer = RowJsonDeserializer.forSchema(schema);
        thrown.expect(UnsupportedRowJsonException.class);
        thrown.expectMessage("Expected JSON array");
        newObjectMapperWith(deserializer).readValue(rowString, Row.class);
    }

    @Test
    public void testParsesRowField() throws Exception {
        Schema nestedRowSchema = Schema.builder().addInt32Field("f_nestedInt32").addStringField("f_nestedString").build();
        Schema schema = Schema.builder().addInt32Field("f_int32").addRowField("f_row", nestedRowSchema).build();
        String rowString = "{\n" + ((((("\"f_int32\" : 32,\n" + "\"f_row\" : {\n") + "             \"f_nestedInt32\" : 54,\n") + "             \"f_nestedString\" : \"foo\"\n") + "            }\n") + "}");
        RowJsonDeserializer deserializer = RowJsonDeserializer.forSchema(schema);
        Row parsedRow = newObjectMapperWith(deserializer).readValue(rowString, Row.class);
        Row expectedRow = Row.withSchema(schema).addValues(32, Row.withSchema(nestedRowSchema).addValues(54, "foo").build()).build();
        Assert.assertEquals(expectedRow, parsedRow);
    }

    @Test
    public void testThrowsForMismatchedRowField() throws Exception {
        Schema nestedRowSchema = Schema.builder().addInt32Field("f_nestedInt32").addStringField("f_nestedString").build();
        Schema schema = Schema.builder().addInt32Field("f_int32").addRowField("f_row", nestedRowSchema).build();
        String rowString = "{\n" + (("\"f_int32\" : 32,\n" + "\"f_row\" : []\n")// expect object, get array
         + "}");
        RowJsonDeserializer deserializer = RowJsonDeserializer.forSchema(schema);
        thrown.expect(UnsupportedRowJsonException.class);
        thrown.expectMessage("Expected JSON object");
        newObjectMapperWith(deserializer).readValue(rowString, Row.class);
    }

    @Test
    public void testParsesNestedRowField() throws Exception {
        Schema doubleNestedRowSchema = Schema.builder().addStringField("f_doubleNestedString").build();
        Schema nestedRowSchema = Schema.builder().addRowField("f_nestedRow", doubleNestedRowSchema).build();
        Schema schema = Schema.builder().addRowField("f_row", nestedRowSchema).build();
        String rowString = "{\n" + ((((("\"f_row\" : {\n" + "             \"f_nestedRow\" : {\n") + "                                \"f_doubleNestedString\":\"foo\"\n") + "                               }\n") + "            }\n") + "}");
        RowJsonDeserializer deserializer = RowJsonDeserializer.forSchema(schema);
        Row parsedRow = newObjectMapperWith(deserializer).readValue(rowString, Row.class);
        Row expectedRow = Row.withSchema(schema).addValues(Row.withSchema(nestedRowSchema).addValues(Row.withSchema(doubleNestedRowSchema).addValues("foo").build()).build()).build();
        Assert.assertEquals(expectedRow, parsedRow);
    }

    @Test
    public void testThrowsForUnsupportedType() throws Exception {
        Schema schema = Schema.builder().addDateTimeField("f_dateTime").build();
        thrown.expect(UnsupportedRowJsonException.class);
        thrown.expectMessage("DATETIME is not supported");
        RowJsonDeserializer.forSchema(schema);
    }

    @Test
    public void testThrowsForUnsupportedArrayElementType() throws Exception {
        Schema schema = Schema.builder().addArrayField("f_dateTimeArray", DATETIME).build();
        thrown.expect(UnsupportedRowJsonException.class);
        thrown.expectMessage("DATETIME is not supported");
        RowJsonDeserializer.forSchema(schema);
    }

    @Test
    public void testThrowsForUnsupportedNestedFieldType() throws Exception {
        Schema nestedSchema = Schema.builder().addArrayField("f_dateTimeArray", DATETIME).build();
        Schema schema = Schema.builder().addRowField("f_nestedRow", nestedSchema).build();
        thrown.expect(UnsupportedRowJsonException.class);
        thrown.expectMessage("DATETIME is not supported");
        RowJsonDeserializer.forSchema(schema);
    }

    @Test
    public void testParsesNulls() throws Exception {
        Schema schema = Schema.builder().addByteField("f_byte").addNullableField("f_string", STRING).build();
        String rowString = "{\n" + (("\"f_byte\" : 12,\n" + "\"f_string\" : null\n") + "}");
        RowJsonDeserializer deserializer = RowJsonDeserializer.forSchema(schema);
        Row parsedRow = newObjectMapperWith(deserializer).readValue(rowString, Row.class);
        Row expectedRow = Row.withSchema(schema).addValues(((byte) (12)), null).build();
        Assert.assertEquals(expectedRow, parsedRow);
    }

    @Test
    public void testThrowsForMissingNotNullableField() throws Exception {
        Schema schema = Schema.builder().addByteField("f_byte").addStringField("f_string").build();
        String rowString = "{\n" + ("\"f_byte\" : 12\n" + "}");
        RowJsonDeserializer deserializer = RowJsonDeserializer.forSchema(schema);
        thrown.expect(UnsupportedRowJsonException.class);
        thrown.expectMessage("'f_string' is not present");
        newObjectMapperWith(deserializer).readValue(rowString, Row.class);
    }

    @Test
    public void testSupportedBooleanConversions() throws Exception {
        testSupportedConversion(BOOLEAN, RowJsonDeserializerTest.BOOLEAN_TRUE_STRING, RowJsonDeserializerTest.BOOLEAN_TRUE_VALUE);
    }

    @Test
    public void testSupportedStringConversions() throws Exception {
        testSupportedConversion(STRING, quoted(RowJsonDeserializerTest.FLOAT_STRING), RowJsonDeserializerTest.FLOAT_STRING);
    }

    @Test
    public void testSupportedByteConversions() throws Exception {
        testSupportedConversion(BYTE, RowJsonDeserializerTest.BYTE_STRING, RowJsonDeserializerTest.BYTE_VALUE);
    }

    @Test
    public void testSupportedShortConversions() throws Exception {
        testSupportedConversion(INT16, RowJsonDeserializerTest.BYTE_STRING, ((short) (RowJsonDeserializerTest.BYTE_VALUE)));
        testSupportedConversion(INT16, RowJsonDeserializerTest.SHORT_STRING, RowJsonDeserializerTest.SHORT_VALUE);
    }

    @Test
    public void testSupportedIntConversions() throws Exception {
        testSupportedConversion(INT32, RowJsonDeserializerTest.BYTE_STRING, ((int) (RowJsonDeserializerTest.BYTE_VALUE)));
        testSupportedConversion(INT32, RowJsonDeserializerTest.SHORT_STRING, ((int) (RowJsonDeserializerTest.SHORT_VALUE)));
        testSupportedConversion(INT32, RowJsonDeserializerTest.INT_STRING, RowJsonDeserializerTest.INT_VALUE);
    }

    @Test
    public void testSupportedLongConversions() throws Exception {
        testSupportedConversion(INT64, RowJsonDeserializerTest.BYTE_STRING, ((long) (RowJsonDeserializerTest.BYTE_VALUE)));
        testSupportedConversion(INT64, RowJsonDeserializerTest.SHORT_STRING, ((long) (RowJsonDeserializerTest.SHORT_VALUE)));
        testSupportedConversion(INT64, RowJsonDeserializerTest.INT_STRING, ((long) (RowJsonDeserializerTest.INT_VALUE)));
        testSupportedConversion(INT64, RowJsonDeserializerTest.LONG_STRING, RowJsonDeserializerTest.LONG_VALUE);
    }

    @Test
    public void testSupportedFloatConversions() throws Exception {
        testSupportedConversion(FLOAT, RowJsonDeserializerTest.FLOAT_STRING, RowJsonDeserializerTest.FLOAT_VALUE);
        testSupportedConversion(FLOAT, RowJsonDeserializerTest.SHORT_STRING, ((float) (RowJsonDeserializerTest.SHORT_VALUE)));
    }

    @Test
    public void testSupportedDoubleConversions() throws Exception {
        testSupportedConversion(DOUBLE, RowJsonDeserializerTest.DOUBLE_STRING, RowJsonDeserializerTest.DOUBLE_VALUE);
        testSupportedConversion(DOUBLE, RowJsonDeserializerTest.FLOAT_STRING, ((double) (RowJsonDeserializerTest.FLOAT_VALUE)));
        testSupportedConversion(DOUBLE, RowJsonDeserializerTest.INT_STRING, ((double) (RowJsonDeserializerTest.INT_VALUE)));
    }

    @Test
    public void testUnsupportedBooleanConversions() throws Exception {
        testUnsupportedConversion(BOOLEAN, quoted(RowJsonDeserializerTest.BOOLEAN_TRUE_STRING));
        testUnsupportedConversion(BOOLEAN, RowJsonDeserializerTest.BYTE_STRING);
        testUnsupportedConversion(BOOLEAN, RowJsonDeserializerTest.SHORT_STRING);
        testUnsupportedConversion(BOOLEAN, RowJsonDeserializerTest.INT_STRING);
        testUnsupportedConversion(BOOLEAN, RowJsonDeserializerTest.LONG_STRING);
        testUnsupportedConversion(BOOLEAN, RowJsonDeserializerTest.FLOAT_STRING);
        testUnsupportedConversion(BOOLEAN, RowJsonDeserializerTest.DOUBLE_STRING);
    }

    @Test
    public void testUnsupportedStringConversions() throws Exception {
        testUnsupportedConversion(STRING, RowJsonDeserializerTest.BOOLEAN_TRUE_STRING);
        testUnsupportedConversion(STRING, RowJsonDeserializerTest.BYTE_STRING);
        testUnsupportedConversion(STRING, RowJsonDeserializerTest.SHORT_STRING);
        testUnsupportedConversion(STRING, RowJsonDeserializerTest.INT_STRING);
        testUnsupportedConversion(STRING, RowJsonDeserializerTest.LONG_STRING);
        testUnsupportedConversion(STRING, RowJsonDeserializerTest.FLOAT_STRING);
        testUnsupportedConversion(STRING, RowJsonDeserializerTest.DOUBLE_STRING);
    }

    @Test
    public void testUnsupportedByteConversions() throws Exception {
        testUnsupportedConversion(BYTE, RowJsonDeserializerTest.BOOLEAN_TRUE_STRING);
        testUnsupportedConversion(BYTE, quoted(RowJsonDeserializerTest.BYTE_STRING));
        testUnsupportedConversion(BYTE, RowJsonDeserializerTest.SHORT_STRING);
        testUnsupportedConversion(BYTE, RowJsonDeserializerTest.INT_STRING);
        testUnsupportedConversion(BYTE, RowJsonDeserializerTest.LONG_STRING);
        testUnsupportedConversion(BYTE, RowJsonDeserializerTest.FLOAT_STRING);
        testUnsupportedConversion(BYTE, RowJsonDeserializerTest.DOUBLE_STRING);
    }

    @Test
    public void testUnsupportedShortConversions() throws Exception {
        testUnsupportedConversion(INT16, RowJsonDeserializerTest.BOOLEAN_TRUE_STRING);
        testUnsupportedConversion(INT16, quoted(RowJsonDeserializerTest.SHORT_STRING));
        testUnsupportedConversion(INT16, RowJsonDeserializerTest.INT_STRING);
        testUnsupportedConversion(INT16, RowJsonDeserializerTest.LONG_STRING);
        testUnsupportedConversion(INT16, RowJsonDeserializerTest.FLOAT_STRING);
        testUnsupportedConversion(INT16, RowJsonDeserializerTest.DOUBLE_STRING);
    }

    @Test
    public void testUnsupportedIntConversions() throws Exception {
        testUnsupportedConversion(INT32, quoted(RowJsonDeserializerTest.INT_STRING));
        testUnsupportedConversion(INT32, RowJsonDeserializerTest.BOOLEAN_TRUE_STRING);
        testUnsupportedConversion(INT32, RowJsonDeserializerTest.LONG_STRING);
        testUnsupportedConversion(INT32, RowJsonDeserializerTest.FLOAT_STRING);
        testUnsupportedConversion(INT32, RowJsonDeserializerTest.DOUBLE_STRING);
    }

    @Test
    public void testUnsupportedLongConversions() throws Exception {
        testUnsupportedConversion(INT64, quoted(RowJsonDeserializerTest.LONG_STRING));
        testUnsupportedConversion(INT64, RowJsonDeserializerTest.BOOLEAN_TRUE_STRING);
        testUnsupportedConversion(INT64, RowJsonDeserializerTest.FLOAT_STRING);
        testUnsupportedConversion(INT64, RowJsonDeserializerTest.DOUBLE_STRING);
    }

    @Test
    public void testUnsupportedFloatConversions() throws Exception {
        testUnsupportedConversion(FLOAT, quoted(RowJsonDeserializerTest.FLOAT_STRING));
        testUnsupportedConversion(FLOAT, RowJsonDeserializerTest.BOOLEAN_TRUE_STRING);
        testUnsupportedConversion(FLOAT, RowJsonDeserializerTest.DOUBLE_STRING);
        testUnsupportedConversion(FLOAT, RowJsonDeserializerTest.INT_STRING);// too large to fit

    }

    @Test
    public void testUnsupportedDoubleConversions() throws Exception {
        testUnsupportedConversion(DOUBLE, quoted(RowJsonDeserializerTest.DOUBLE_STRING));
        testUnsupportedConversion(DOUBLE, RowJsonDeserializerTest.BOOLEAN_TRUE_STRING);
        testUnsupportedConversion(DOUBLE, RowJsonDeserializerTest.LONG_STRING);// too large to fit

    }
}

