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
package org.apache.nifi.csv;


import CSVFormat.DEFAULT;
import QuoteMode.ALL;
import QuoteMode.NONE;
import RecordFieldType.DATE;
import RecordFieldType.INT;
import RecordFieldType.LONG;
import RecordFieldType.STRING;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.nifi.schema.access.SchemaNameAsAttribute;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordFieldType;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.RecordSet;
import org.junit.Assert;
import org.junit.Test;


public class TestWriteCSVResult {
    @Test
    public void testDataTypes() throws IOException {
        final CSVFormat csvFormat = DEFAULT.withQuoteMode(ALL).withRecordSeparator("\n");
        final StringBuilder headerBuilder = new StringBuilder();
        final List<RecordField> fields = new ArrayList<>();
        for (final RecordFieldType fieldType : RecordFieldType.values()) {
            if (fieldType == (RecordFieldType.CHOICE)) {
                final List<DataType> possibleTypes = new ArrayList<>();
                possibleTypes.add(INT.getDataType());
                possibleTypes.add(LONG.getDataType());
                fields.add(new RecordField(fieldType.name().toLowerCase(), fieldType.getChoiceDataType(possibleTypes)));
            } else {
                fields.add(new RecordField(fieldType.name().toLowerCase(), fieldType.getDataType()));
            }
            headerBuilder.append('"').append(fieldType.name().toLowerCase()).append('"').append(",");
        }
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final long now = System.currentTimeMillis();
        try (final WriteCSVResult result = new WriteCSVResult(csvFormat, schema, new SchemaNameAsAttribute(), baos, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), true, "UTF-8")) {
            final Map<String, Object> valueMap = new HashMap<>();
            valueMap.put("string", "a?bc?12?3");
            valueMap.put("boolean", true);
            valueMap.put("byte", ((byte) (1)));
            valueMap.put("char", 'c');
            valueMap.put("short", ((short) (8)));
            valueMap.put("int", 9);
            valueMap.put("bigint", BigInteger.valueOf(8L));
            valueMap.put("long", 8L);
            valueMap.put("float", 8.0F);
            valueMap.put("double", 8.0);
            valueMap.put("date", new Date(now));
            valueMap.put("time", new Time(now));
            valueMap.put("timestamp", new Timestamp(now));
            valueMap.put("record", null);
            valueMap.put("choice", 48L);
            valueMap.put("array", null);
            final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, valueMap);
            final RecordSet rs = RecordSet.of(schema, record);
            result.write(rs);
        }
        final String output = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        headerBuilder.deleteCharAt(((headerBuilder.length()) - 1));
        final String headerLine = headerBuilder.toString();
        final String[] splits = output.split("\n");
        Assert.assertEquals(2, splits.length);
        Assert.assertEquals(headerLine, splits[0]);
        final String dateValue = getDateFormat(DATE.getDefaultFormat()).format(now);
        final String timeValue = getDateFormat(TIME.getDefaultFormat()).format(now);
        final String timestampValue = getDateFormat(TIMESTAMP.getDefaultFormat()).format(now);
        final String values = splits[1];
        final StringBuilder expectedBuilder = new StringBuilder();
        expectedBuilder.append((((((("\"true\",\"1\",\"8\",\"9\",\"8\",\"8\",\"8.0\",\"8.0\",\"" + dateValue) + "\",\"") + timeValue) + "\",\"") + timestampValue) + "\",\"c\",\"a\u5b5fbc\u674e12\u51123\",,\"48\",,"));
        final String expectedValues = expectedBuilder.toString();
        Assert.assertEquals(expectedValues, values);
    }

    @Test
    public void testExtraFieldInWriteRecord() throws IOException {
        final CSVFormat csvFormat = DEFAULT.withEscape('\\').withQuoteMode(NONE).withRecordSeparator("\n");
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", "1");
        values.put("name", "John");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String output;
        try (final WriteCSVResult writer = new WriteCSVResult(csvFormat, schema, new SchemaNameAsAttribute(), baos, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), true, "ASCII")) {
            writer.beginRecordSet();
            writer.write(record);
            writer.finishRecordSet();
            writer.flush();
            output = baos.toString();
        }
        Assert.assertEquals("id\n1\n", output);
    }

    @Test
    public void testExtraFieldInWriteRawRecord() throws IOException {
        final CSVFormat csvFormat = DEFAULT.withEscape('\\').withQuoteMode(NONE).withRecordSeparator("\n");
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", "1");
        values.put("name", "John");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String output;
        try (final WriteCSVResult writer = new WriteCSVResult(csvFormat, schema, new SchemaNameAsAttribute(), baos, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), true, "ASCII")) {
            writer.beginRecordSet();
            writer.writeRawRecord(record);
            writer.finishRecordSet();
            writer.flush();
            output = baos.toString();
        }
        Assert.assertEquals("id,name\n1,John\n", output);
    }

    @Test
    public void testMissingFieldWriteRecord() throws IOException {
        final CSVFormat csvFormat = DEFAULT.withEscape('\\').withQuoteMode(NONE).withRecordSeparator("\n");
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", STRING.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", "1");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String output;
        try (final WriteCSVResult writer = new WriteCSVResult(csvFormat, schema, new SchemaNameAsAttribute(), baos, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), true, "ASCII")) {
            writer.beginRecordSet();
            writer.writeRecord(record);
            writer.finishRecordSet();
            writer.flush();
            output = baos.toString();
        }
        Assert.assertEquals("id,name\n1,\n", output);
    }

    @Test
    public void testMissingFieldWriteRawRecord() throws IOException {
        final CSVFormat csvFormat = DEFAULT.withEscape('\\').withQuoteMode(NONE).withRecordSeparator("\n");
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", STRING.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", "1");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String output;
        try (final WriteCSVResult writer = new WriteCSVResult(csvFormat, schema, new SchemaNameAsAttribute(), baos, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), true, "ASCII")) {
            writer.beginRecordSet();
            writer.writeRawRecord(record);
            writer.finishRecordSet();
            writer.flush();
            output = baos.toString();
        }
        Assert.assertEquals("id,name\n1,\n", output);
    }

    @Test
    public void testMissingAndExtraFieldWriteRecord() throws IOException {
        final CSVFormat csvFormat = DEFAULT.withEscape('\\').withQuoteMode(NONE).withRecordSeparator("\n");
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", STRING.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", "1");
        values.put("dob", "1/1/1970");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String output;
        try (final WriteCSVResult writer = new WriteCSVResult(csvFormat, schema, new SchemaNameAsAttribute(), baos, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), true, "ASCII")) {
            writer.beginRecordSet();
            writer.writeRecord(record);
            writer.finishRecordSet();
            writer.flush();
            output = baos.toString();
        }
        Assert.assertEquals("id,name\n1,\n", output);
    }

    @Test
    public void testMissingAndExtraFieldWriteRawRecord() throws IOException {
        final CSVFormat csvFormat = DEFAULT.withEscape('\\').withQuoteMode(NONE).withRecordSeparator("\n");
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", STRING.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new LinkedHashMap<>();
        values.put("id", "1");
        values.put("dob", "1/1/1970");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final String output;
        try (final WriteCSVResult writer = new WriteCSVResult(csvFormat, schema, new SchemaNameAsAttribute(), baos, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), true, "ASCII")) {
            writer.beginRecordSet();
            writer.writeRawRecord(record);
            writer.finishRecordSet();
            writer.flush();
            output = baos.toString();
        }
        Assert.assertEquals("id,dob,name\n1,1/1/1970,\n", output);
    }
}

