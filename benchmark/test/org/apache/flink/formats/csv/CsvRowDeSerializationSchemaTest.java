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
package org.apache.flink.formats.csv;


import CsvRowSerializationSchema.Builder;
import Types.BIG_DEC;
import Types.BIG_INT;
import Types.BOOLEAN;
import Types.BYTE;
import Types.DOUBLE;
import Types.FLOAT;
import Types.INT;
import Types.LONG;
import Types.SHORT;
import Types.SQL_DATE;
import Types.SQL_TIME;
import Types.SQL_TIMESTAMP;
import Types.STRING;
import Types.VOID;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.function.Consumer;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.types.Row;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link CsvRowSerializationSchema} and {@link CsvRowDeserializationSchema}.
 */
public class CsvRowDeSerializationSchemaTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testSerializeDeserialize() throws Exception {
        testNullableField(LONG, "null", null);
        testNullableField(STRING, "null", null);
        testNullableField(VOID, "null", null);
        testNullableField(STRING, "\"This is a test.\"", "This is a test.");
        testNullableField(STRING, "\"This is a test\n\r.\"", "This is a test\n\r.");
        testNullableField(BOOLEAN, "true", true);
        testNullableField(BOOLEAN, "null", null);
        testNullableField(BYTE, "124", ((byte) (124)));
        testNullableField(SHORT, "10000", ((short) (10000)));
        testNullableField(INT, "1234567", 1234567);
        testNullableField(LONG, "12345678910", 12345678910L);
        testNullableField(FLOAT, "0.33333334", 0.33333334F);
        testNullableField(DOUBLE, "0.33333333332", 0.33333333332);
        testNullableField(BIG_DEC, "\"1234.0000000000000000000000001\"", new BigDecimal("1234.0000000000000000000000001"));
        testNullableField(BIG_INT, "\"123400000000000000000000000000\"", new BigInteger("123400000000000000000000000000"));
        testNullableField(SQL_DATE, "2018-10-12", Date.valueOf("2018-10-12"));
        testNullableField(SQL_TIME, "12:12:12", Time.valueOf("12:12:12"));
        testNullableField(SQL_TIMESTAMP, "\"2018-10-12 12:12:12.0\"", Timestamp.valueOf("2018-10-12 12:12:12"));
        testNullableField(Types.ROW(STRING, INT, BOOLEAN), "Hello;42;false", Row.of("Hello", 42, false));
        testNullableField(Types.OBJECT_ARRAY(STRING), "a;b;c", new String[]{ "a", "b", "c" });
        testNullableField(Types.OBJECT_ARRAY(BYTE), "12;4;null", new Byte[]{ 12, 4, null });
        testNullableField(((TypeInformation<byte[]>) (Types.PRIMITIVE_ARRAY(BYTE))), "awML", new byte[]{ 107, 3, 11 });
    }

    @Test
    public void testSerializeDeserializeCustomizedProperties() throws Exception {
        final Consumer<CsvRowSerializationSchema.Builder> serConfig = ( serSchemaBuilder) -> serSchemaBuilder.setEscapeCharacter('*').setQuoteCharacter('\'').setArrayElementDelimiter(":").setFieldDelimiter(';');
        final Consumer<CsvRowDeserializationSchema.Builder> deserConfig = ( deserSchemaBuilder) -> deserSchemaBuilder.setEscapeCharacter('*').setQuoteCharacter('\'').setArrayElementDelimiter(":").setFieldDelimiter(';');
        testField(STRING, "123*'4**", "123'4*", deserConfig, ";");
        testField(STRING, "'123''4**'", "123'4*", serConfig, deserConfig, ";");
        testField(STRING, "'a;b*'c'", "a;b'c", deserConfig, ";");
        testField(STRING, "'a;b''c'", "a;b'c", serConfig, deserConfig, ";");
        testField(INT, "       12          ", 12, deserConfig, ";");
        testField(INT, "12", 12, serConfig, deserConfig, ";");
        testField(Types.ROW(STRING, STRING), "1:hello", Row.of("1", "hello"), deserConfig, ";");
        testField(Types.ROW(STRING, STRING), "'1:hello'", Row.of("1", "hello"), serConfig, deserConfig, ";");
        testField(Types.ROW(STRING, STRING), "'1:hello world'", Row.of("1", "hello world"), serConfig, deserConfig, ";");
        testField(STRING, "null", "null", serConfig, deserConfig, ";");// string because null literal has not been set

    }

    @Test
    public void testDeserializeParseError() throws Exception {
        try {
            testDeserialization(false, false, "Test,null,Test");// null not supported

            Assert.fail("Missing field should cause failure.");
        } catch (IOException e) {
            // valid exception
        }
    }

    @Test
    public void testDeserializeUnsupportedNull() throws Exception {
        // unsupported null for integer
        Assert.assertEquals(Row.of("Test", null, "Test"), testDeserialization(true, false, "Test,null,Test"));
    }

    @Test
    public void testDeserializeIncompleteRow() throws Exception {
        // last two columns are missing
        Assert.assertEquals(Row.of("Test", null, null), testDeserialization(true, false, "Test"));
    }

    @Test
    public void testDeserializeMoreColumnsThanExpected() throws Exception {
        // one additional string column
        Assert.assertNull(testDeserialization(true, false, "Test,12,Test,Test"));
    }

    @Test
    public void testDeserializeIgnoreComment() throws Exception {
        // # is part of the string
        Assert.assertEquals(Row.of("#Test", 12, "Test"), testDeserialization(false, false, "#Test,12,Test"));
    }

    @Test
    public void testDeserializeAllowComment() throws Exception {
        // entire row is ignored
        Assert.assertNull(testDeserialization(true, true, "#Test,12,Test"));
    }

    @Test
    public void testSerializationProperties() throws Exception {
        final TypeInformation<Row> rowInfo = Types.ROW(STRING, INT, STRING);
        final CsvRowSerializationSchema.Builder serSchemaBuilder = setLineDelimiter("\r");
        Assert.assertArrayEquals("Test,12,Hello\r".getBytes(), CsvRowDeSerializationSchemaTest.serialize(serSchemaBuilder, Row.of("Test", 12, "Hello")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidNesting() throws Exception {
        testNullableField(Types.ROW(Types.ROW(STRING)), "FAIL", Row.of(Row.of("FAIL")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidType() throws Exception {
        testNullableField(Types.GENERIC(java.util.Date.class), "FAIL", new java.util.Date());
    }
}

