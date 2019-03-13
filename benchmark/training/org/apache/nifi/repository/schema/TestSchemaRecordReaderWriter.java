/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.repository.schema;


import FieldType.BOOLEAN;
import FieldType.BYTE_ARRAY;
import FieldType.COMPLEX;
import FieldType.INT;
import FieldType.LONG;
import FieldType.LONG_STRING;
import FieldType.STRING;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import static FieldType.BOOLEAN;
import static FieldType.BYTE_ARRAY;
import static FieldType.INT;
import static FieldType.LONG;
import static FieldType.LONG_STRING;
import static FieldType.STRING;
import static Repetition.EXACTLY_ONE;
import static Repetition.ZERO_OR_MORE;
import static Repetition.ZERO_OR_ONE;


public class TestSchemaRecordReaderWriter {
    private static Character utfCharOneByte = '$';

    private static Character utfCharTwoByte = '?';

    private static Character utfCharThreeByte = '?';

    private static String utfStringOneByte = TestSchemaRecordReaderWriter.utfCharOneByte.toString();

    private static String utfStringTwoByte = TestSchemaRecordReaderWriter.utfCharTwoByte.toString();

    private static String utfStringThreeByte = TestSchemaRecordReaderWriter.utfCharThreeByte.toString();

    @Test
    @SuppressWarnings("unchecked")
    public void testRoundTrip() throws IOException {
        // Create a 'complex' record that contains two different types of fields - a string and an int.
        final List<RecordField> complexFieldList1 = new ArrayList<>();
        complexFieldList1.add(createField("string field", STRING));
        complexFieldList1.add(createField("int field", INT));
        final ComplexRecordField complexField1 = new ComplexRecordField("complex1", EXACTLY_ONE, complexFieldList1);
        final Map<RecordField, Object> complexMap1 = new LinkedHashMap<>();
        final RecordField stringField = createField("string field", STRING);
        final RecordField intField = createField("int field", INT);
        complexMap1.put(stringField, "apples");
        complexMap1.put(intField, 100);
        final FieldMapRecord complexRecord1 = new FieldMapRecord(complexMap1, new RecordSchema(stringField, intField));
        // Create another 'complex' record that contains two other types of fields - a long string and a long.
        final List<RecordField> complexFieldList2 = new ArrayList<>();
        complexFieldList2.add(createField("long string field", LONG_STRING));
        complexFieldList2.add(createField("long field", LONG));
        final ComplexRecordField complexField2 = new ComplexRecordField("complex2", EXACTLY_ONE, complexFieldList2);
        final Map<RecordField, Object> complexMap2 = new LinkedHashMap<>();
        final RecordField longStringField = createField("long string field", LONG_STRING);
        final RecordField longField = createField("long field", LONG);
        complexMap2.put(longStringField, "oranges");
        complexMap2.put(longField, Long.MAX_VALUE);
        final FieldMapRecord complexRecord2 = new FieldMapRecord(complexMap2, new RecordSchema(longStringField, longField));
        // Create a Union Field that indicates that the type could be either 'complex 1' or 'complex 2'
        final UnionRecordField unionRecordField = new UnionRecordField("union", ZERO_OR_MORE, Arrays.asList(new RecordField[]{ complexField1, complexField2 }));
        // Create a Record Schema
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new SimpleRecordField("int", INT, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("int present", INT, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("boolean", BOOLEAN, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("boolean present", BOOLEAN, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("byte array", BYTE_ARRAY, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("byte array present", BYTE_ARRAY, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("long", LONG, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("long present", LONG, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("string", STRING, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("string present", STRING, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("long string", LONG_STRING, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("long string present", LONG_STRING, ZERO_OR_ONE));
        fields.add(new ComplexRecordField("complex present", EXACTLY_ONE, new SimpleRecordField("color", STRING, ZERO_OR_ONE), new SimpleRecordField("fruit", STRING, ZERO_OR_ONE)));
        fields.add(new MapRecordField("map present", new SimpleRecordField("key", STRING, EXACTLY_ONE), new SimpleRecordField("value", INT, EXACTLY_ONE), ZERO_OR_ONE));
        fields.add(unionRecordField);
        final RecordSchema schema = new RecordSchema(fields);
        // Create a 'complex' record that contains two different elements.
        final RecordField colorField = createField("color", STRING);
        final RecordField fruitField = createField("fruit", STRING);
        final Map<RecordField, Object> complexFieldMap = new LinkedHashMap<>();
        complexFieldMap.put(colorField, "red");
        complexFieldMap.put(fruitField, "apple");
        // Create a simple map that can be used for a Map Field
        final Map<String, Integer> simpleMap = new HashMap<>();
        simpleMap.put("apples", 100);
        // Create a Map of record fields to values, so that we can create a Record to write out
        final Map<RecordField, Object> values = new LinkedHashMap<>();
        values.put(createField("int", INT), 42);
        values.put(createField("int present", INT), 42);
        values.put(createField("boolean present", BOOLEAN), true);
        values.put(createField("byte array present", BYTE_ARRAY), "Hello".getBytes());
        values.put(createField("long present", LONG), 42L);
        values.put(createField("string present", STRING), "Hello");
        values.put(createField("long string present", LONG_STRING), "Long Hello");
        values.put(createField("complex present", COMPLEX), new FieldMapRecord(complexFieldMap, new RecordSchema(colorField, fruitField)));
        values.put(new MapRecordField("map present", createField("key", STRING), createField("value", INT), EXACTLY_ONE), simpleMap);
        values.put(unionRecordField, Arrays.asList(new NamedValue[]{ new NamedValue("complex1", complexRecord1), new NamedValue("complex2", complexRecord2) }));
        final FieldMapRecord originalRecord = new FieldMapRecord(values, schema);
        // Write out a record and read it back in.
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Write the schema to the stream
            schema.writeTo(baos);
            // Write the record twice, to make sure that we're able to read/write multiple sequential records
            final SchemaRecordWriter writer = new SchemaRecordWriter();
            writer.writeRecord(originalRecord, baos);
            writer.writeRecord(originalRecord, baos);
            try (final InputStream in = new ByteArrayInputStream(baos.toByteArray())) {
                // Read the Schema from the stream and create a Record Reader for reading records, based on this schema
                final RecordSchema readSchema = RecordSchema.readFrom(in);
                final SchemaRecordReader reader = SchemaRecordReader.fromSchema(readSchema);
                // Read two records and verify the values.
                for (int i = 0; i < 2; i++) {
                    final Record record = reader.readRecord(in);
                    Assert.assertNotNull(record);
                    Assert.assertEquals(42, record.getFieldValue("int"));
                    Assert.assertEquals(42, record.getFieldValue("int present"));
                    Assert.assertEquals(true, record.getFieldValue("boolean present"));
                    Assert.assertTrue(Arrays.equals("Hello".getBytes(), ((byte[]) (record.getFieldValue("byte array present")))));
                    Assert.assertEquals(42L, record.getFieldValue("long present"));
                    Assert.assertEquals("Hello", record.getFieldValue("string present"));
                    Assert.assertEquals("Long Hello", record.getFieldValue("long string present"));
                    final Record complexRecord = ((Record) (record.getFieldValue("complex present")));
                    Assert.assertEquals("red", complexRecord.getFieldValue("color"));
                    Assert.assertEquals("apple", complexRecord.getFieldValue("fruit"));
                    Assert.assertEquals(simpleMap, record.getFieldValue("map present"));
                    final List<Record> unionRecords = ((List<Record>) (record.getFieldValue("union")));
                    Assert.assertNotNull(unionRecords);
                    Assert.assertEquals(2, unionRecords.size());
                    final Record unionRecord1 = unionRecords.get(0);
                    Assert.assertEquals("apples", unionRecord1.getFieldValue("string field"));
                    Assert.assertEquals(100, unionRecord1.getFieldValue("int field"));
                    final Record unionRecord2 = unionRecords.get(1);
                    Assert.assertEquals("oranges", unionRecord2.getFieldValue("long string field"));
                    Assert.assertEquals(Long.MAX_VALUE, unionRecord2.getFieldValue("long field"));
                }
                // Ensure that there is no more data.
                Assert.assertNull(reader.readRecord(in));
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUTFLargerThan64k() throws IOException {
        // Create a Record Schema
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new SimpleRecordField("int present", INT, ZERO_OR_ONE));
        fields.add(new SimpleRecordField("string present", STRING, ZERO_OR_ONE));
        final RecordSchema schema = new RecordSchema(fields);
        // Create a Map of record fields to values, so that we can create a Record to write out
        final Map<RecordField, Object> values = new LinkedHashMap<>();
        values.put(createField("int present", INT), 42);
        final String utfString = ((TestSchemaRecordReaderWriter.utfStringOneByte) + (TestSchemaRecordReaderWriter.utfStringTwoByte)) + (TestSchemaRecordReaderWriter.utfStringThreeByte);// 3 chars and 6 utf8 bytes

        final String seventyK = StringUtils.repeat(utfString, 21845);// 65,535 chars and 131070 utf8 bytes

        Assert.assertTrue(((seventyK.length()) == 65535));
        Assert.assertTrue(((seventyK.getBytes("UTF-8").length) == 131070));
        values.put(createField("string present", STRING), seventyK);
        final FieldMapRecord originalRecord = new FieldMapRecord(values, schema);
        // Write out a record and read it back in.
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Write the schema to the stream
            schema.writeTo(baos);
            // Write the record twice, to make sure that we're able to read/write multiple sequential records
            final SchemaRecordWriter writer = new SchemaRecordWriter();
            writer.writeRecord(originalRecord, baos);
            writer.writeRecord(originalRecord, baos);
            try (final InputStream in = new ByteArrayInputStream(baos.toByteArray())) {
                // Read the Schema from the stream and create a Record Reader for reading records, based on this schema
                final RecordSchema readSchema = RecordSchema.readFrom(in);
                final SchemaRecordReader reader = SchemaRecordReader.fromSchema(readSchema);
                // Read the records and verify the values.
                for (int i = 0; i < 2; i++) {
                    final Record record = reader.readRecord(in);
                    Assert.assertNotNull(record);
                    Assert.assertEquals(42, record.getFieldValue("int present"));
                    Assert.assertTrue((((SchemaRecordWriter.MAX_ALLOWED_UTF_LENGTH) - (((String) (record.getFieldValue("string present"))).getBytes("utf-8").length)) <= 3));
                    Assert.assertEquals(32768, ((String) (record.getFieldValue("string present"))).length());
                }
                // Ensure that there is no more data.
                Assert.assertNull(reader.readRecord(in));
            }
        }
    }

    @Test
    public void testSingleCharUTF8Lengths() {
        // verify handling of single characters mapping to utf8 byte strings
        Assert.assertEquals("test 1 char string truncated to 0 utf bytes should be 0", 0, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringOneByte, 0));
        Assert.assertEquals("test 2 char string truncated to 0 utf bytes should be 0", 0, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringTwoByte, 0));
        Assert.assertEquals("test 3 char string truncated to 0 utf bytes should be 0", 0, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringThreeByte, 0));
        Assert.assertEquals("test 1 char string truncated to 1 utf bytes should be 1", 1, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringOneByte, 1));
        Assert.assertEquals("test 2 char string truncated to 1 utf bytes should be 0", 0, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringTwoByte, 1));
        Assert.assertEquals("test 3 char string truncated to 1 utf bytes should be 0", 0, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringThreeByte, 1));
        Assert.assertEquals("test 1 char string truncated to 2 utf bytes should be 1", 1, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringOneByte, 2));
        Assert.assertEquals("test 2 char string truncated to 2 utf bytes should be 2", 1, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringTwoByte, 2));
        Assert.assertEquals("test 3 char string truncated to 2 utf bytes should be 0", 0, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringThreeByte, 2));
        Assert.assertEquals("test 1 char string truncated to 3 utf bytes should be 1", 1, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringOneByte, 3));
        Assert.assertEquals("test 2 char string truncated to 3 utf bytes should be 2", 1, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringTwoByte, 3));
        Assert.assertEquals("test 3 char string truncated to 3 utf bytes should be 3", 1, SchemaRecordWriter.getCharsInUTF8Limit(TestSchemaRecordReaderWriter.utfStringThreeByte, 3));
    }

    @Test
    public void testMultiCharUTFLengths() {
        // test boundary conditions as 1, 2, and 3 UTF byte chars are included into utf limit                                                  positions used by strings
        final String testString1 = ((TestSchemaRecordReaderWriter.utfStringOneByte) + (TestSchemaRecordReaderWriter.utfStringTwoByte)) + (TestSchemaRecordReaderWriter.utfStringThreeByte);
        // char 'abc' utf 'abbccc'
        Assert.assertEquals("test 6 char string truncated to 0 utf bytes should be 0", 0, SchemaRecordWriter.getCharsInUTF8Limit(testString1, 0));// utf ''

        Assert.assertEquals("test 6 char string truncated to 1 utf bytes should be 1", 1, SchemaRecordWriter.getCharsInUTF8Limit(testString1, 1));// utf 'a'

        Assert.assertEquals("test 6 char string truncated to 2 utf bytes should be 1", 1, SchemaRecordWriter.getCharsInUTF8Limit(testString1, 2));// utf 'a'

        Assert.assertEquals("test 6 char string truncated to 3 utf bytes should be 2", 2, SchemaRecordWriter.getCharsInUTF8Limit(testString1, 3));// utf 'abb'

        Assert.assertEquals("test 6 char string truncated to 4 utf bytes should be 2", 2, SchemaRecordWriter.getCharsInUTF8Limit(testString1, 4));// utf 'abb'

        Assert.assertEquals("test 6 char string truncated to 5 utf bytes should be 2", 2, SchemaRecordWriter.getCharsInUTF8Limit(testString1, 5));// utf 'abb'

        Assert.assertEquals("test 6 char string truncated to 6 utf bytes should be 3", 3, SchemaRecordWriter.getCharsInUTF8Limit(testString1, 6));// utf 'abbccc'

    }

    @Test
    public void testSmallCharUTFLengths() throws UnsupportedEncodingException {
        final String string12b = StringUtils.repeat((((TestSchemaRecordReaderWriter.utfStringOneByte) + (TestSchemaRecordReaderWriter.utfStringTwoByte)) + (TestSchemaRecordReaderWriter.utfStringThreeByte)), 2);
        Assert.assertEquals("test multi-char string truncated to  0 utf bytes should be 0", 0, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 0));
        Assert.assertEquals("test multi-char string truncated to  1 utf bytes should be 0", 1, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 1));
        Assert.assertEquals("test multi-char string truncated to  2 utf bytes should be 0", 1, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 2));
        Assert.assertEquals("test multi-char string truncated to  3 utf bytes should be 0", 2, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 3));
        Assert.assertEquals("test multi-char string truncated to  4 utf bytes should be 0", 2, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 4));
        Assert.assertEquals("test multi-char string truncated to  5 utf bytes should be 0", 2, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 5));
        Assert.assertEquals("test multi-char string truncated to  6 utf bytes should be 0", 3, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 6));
        Assert.assertEquals("test multi-char string truncated to  7 utf bytes should be 0", 4, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 7));
        Assert.assertEquals("test multi-char string truncated to  8 utf bytes should be 0", 4, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 8));
        Assert.assertEquals("test multi-char string truncated to  9 utf bytes should be 0", 5, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 9));
        Assert.assertEquals("test multi-char string truncated to 10 utf bytes should be 0", 5, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 10));
        Assert.assertEquals("test multi-char string truncated to 11 utf bytes should be 0", 5, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 11));
        Assert.assertEquals("test multi-char string truncated to 12 utf bytes should be 0", 6, SchemaRecordWriter.getCharsInUTF8Limit(string12b, 12));
    }

    @Test
    public void testLargeCharUTFLengths() {
        final String string64k = StringUtils.repeat((((TestSchemaRecordReaderWriter.utfStringOneByte) + (TestSchemaRecordReaderWriter.utfStringTwoByte)) + (TestSchemaRecordReaderWriter.utfStringThreeByte)), 21845);
        Assert.assertEquals("test 64k char string should be 64k chars long", 65535, string64k.length());
        // drop half the chars going to utf of 64k bytes -- (1+1+1) * 21845 = 65535 chars which converts to (1+2+3) * 21845 = 131070 utf bytes so 1/2 is truncated
        Assert.assertEquals("test 64k char string truncated to 65,535 utf bytes should be 32768", 32768, SchemaRecordWriter.getCharsInUTF8Limit(string64k, 65535));
        // dropping bytes off the end of utf length
        Assert.assertEquals("test 64k char string truncated to 65,534 utf bytes should be 32767", 32767, SchemaRecordWriter.getCharsInUTF8Limit(string64k, 65534));// lost 2 byte char

        Assert.assertEquals("test 64k char string truncated to 65,533 utf bytes should be 32767", 32767, SchemaRecordWriter.getCharsInUTF8Limit(string64k, 65533));
        Assert.assertEquals("test 64k char string truncated to 65,532 utf bytes should be 32766", 32766, SchemaRecordWriter.getCharsInUTF8Limit(string64k, 65532));// lost 1 byte char

        Assert.assertEquals("test 64k char string truncated to 65,531 utf bytes should be 32765", 32765, SchemaRecordWriter.getCharsInUTF8Limit(string64k, 65531));// lost 3 byte char

        Assert.assertEquals("test 64k char string truncated to 65,530 utf bytes should be 32765", 32765, SchemaRecordWriter.getCharsInUTF8Limit(string64k, 65530));
        Assert.assertEquals("test 64k char string truncated to 65,529 utf bytes should be 32765", 32765, SchemaRecordWriter.getCharsInUTF8Limit(string64k, 65529));
        Assert.assertEquals("test 64k char string truncated to 65,528 utf bytes should be 32764", 32764, SchemaRecordWriter.getCharsInUTF8Limit(string64k, 65528));// lost 2 byte char (again)

    }
}

