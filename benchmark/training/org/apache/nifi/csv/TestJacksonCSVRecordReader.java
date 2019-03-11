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
import CSVFormat.EXCEL;
import RecordFieldType.DATE;
import RecordFieldType.DOUBLE;
import RecordFieldType.STRING;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestJacksonCSVRecordReader {
    private final DataType doubleDataType = DOUBLE.getDataType();

    private final CSVFormat format = DEFAULT.withFirstRecordAsHeader().withTrim().withQuote('"');

    @Test
    public void testUTF8() throws IOException, MalformedRecordException {
        final String text = "name\n\u9ec3\u51f1\u63da";
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream bais = new ByteArrayInputStream(text.getBytes());final JacksonCSVRecordReader reader = new JacksonCSVRecordReader(bais, Mockito.mock(ComponentLog.class), schema, format, true, false, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), "UTF-8")) {
            final Record record = reader.nextRecord();
            final String name = ((String) (record.getValue("name")));
            Assert.assertEquals("???", name);
        }
    }

    @Test
    public void testDate() throws IOException, MalformedRecordException {
        final String text = "date\n11/30/1983";
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("date", DATE.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream bais = new ByteArrayInputStream(text.getBytes());final JacksonCSVRecordReader reader = new JacksonCSVRecordReader(bais, Mockito.mock(ComponentLog.class), schema, format, true, false, "MM/dd/yyyy", TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), "UTF-8")) {
            final Record record = reader.nextRecord();
            final Date date = ((Date) (record.getValue("date")));
            final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
            calendar.setTimeInMillis(date.getTime());
            Assert.assertEquals(1983, calendar.get(Calendar.YEAR));
            Assert.assertEquals(10, calendar.get(Calendar.MONTH));
            Assert.assertEquals(30, calendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    @Test
    public void testSimpleParse() throws IOException, MalformedRecordException {
        final List<RecordField> fields = getDefaultFields();
        fields.replaceAll(( f) -> f.getFieldName().equals("balance") ? new RecordField("balance", doubleDataType) : f);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream fis = new FileInputStream(new File("src/test/resources/csv/single-bank-account.csv"));final JacksonCSVRecordReader reader = createReader(fis, schema, format)) {
            final Object[] record = reader.nextRecord().getValues();
            final Object[] expectedValues = new Object[]{ "1", "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" };
            Assert.assertArrayEquals(expectedValues, record);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testExcelFormat() throws IOException, MalformedRecordException {
        final List<RecordField> fields = new ArrayList<RecordField>();
        fields.add(new RecordField("fieldA", STRING.getDataType()));
        fields.add(new RecordField("fieldB", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final String headerLine = "fieldA,fieldB";
        final String inputRecord = "valueA,valueB";
        final String csvData = (headerLine + "\n") + inputRecord;
        final byte[] inputData = csvData.getBytes();
        try (final InputStream bais = new ByteArrayInputStream(inputData);final JacksonCSVRecordReader reader = createReader(bais, schema, EXCEL)) {
            final Object[] record = reader.nextRecord().getValues();
            final Object[] expectedValues = new Object[]{ "valueA", "valueB" };
            Assert.assertArrayEquals(expectedValues, record);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testMultipleRecords() throws IOException, MalformedRecordException {
        final List<RecordField> fields = getDefaultFields();
        fields.replaceAll(( f) -> f.getFieldName().equals("balance") ? new RecordField("balance", doubleDataType) : f);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream fis = new FileInputStream(new File("src/test/resources/csv/multi-bank-account.csv"));final JacksonCSVRecordReader reader = createReader(fis, schema, format)) {
            final Object[] firstRecord = reader.nextRecord().getValues();
            final Object[] firstExpectedValues = new Object[]{ "1", "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" };
            Assert.assertArrayEquals(firstExpectedValues, firstRecord);
            final Object[] secondRecord = reader.nextRecord().getValues();
            final Object[] secondExpectedValues = new Object[]{ "2", "Jane Doe", 4820.09, "321 Your Street", "Your City", "NY", "33333", "USA" };
            Assert.assertArrayEquals(secondExpectedValues, secondRecord);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testExtraWhiteSpace() throws IOException, MalformedRecordException {
        final List<RecordField> fields = getDefaultFields();
        fields.replaceAll(( f) -> f.getFieldName().equals("balance") ? new RecordField("balance", doubleDataType) : f);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream fis = new FileInputStream(new File("src/test/resources/csv/extra-white-space.csv"));final JacksonCSVRecordReader reader = createReader(fis, schema, format)) {
            final Object[] firstRecord = reader.nextRecord().getValues();
            final Object[] firstExpectedValues = new Object[]{ "1", "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" };
            Assert.assertArrayEquals(firstExpectedValues, firstRecord);
            final Object[] secondRecord = reader.nextRecord().getValues();
            final Object[] secondExpectedValues = new Object[]{ "2", "Jane Doe", 4820.09, "321 Your Street", "Your City", "NY", "33333", "USA" };
            Assert.assertArrayEquals(secondExpectedValues, secondRecord);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testMissingField() throws IOException, MalformedRecordException {
        final List<RecordField> fields = getDefaultFields();
        fields.replaceAll(( f) -> f.getFieldName().equals("balance") ? new RecordField("balance", doubleDataType) : f);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final String headerLine = "id, name, balance, address, city, state, zipCode, country";
        final String inputRecord = "1, John, 40.80, 123 My Street, My City, MS, 11111";
        final String csvData = (headerLine + "\n") + inputRecord;
        final byte[] inputData = csvData.getBytes();
        try (final InputStream bais = new ByteArrayInputStream(inputData);final JacksonCSVRecordReader reader = createReader(bais, schema, format)) {
            final Record record = reader.nextRecord();
            Assert.assertNotNull(record);
            Assert.assertEquals("1", record.getValue("id"));
            Assert.assertEquals("John", record.getValue("name"));
            Assert.assertEquals(40.8, record.getValue("balance"));
            Assert.assertEquals("123 My Street", record.getValue("address"));
            Assert.assertEquals("My City", record.getValue("city"));
            Assert.assertEquals("MS", record.getValue("state"));
            Assert.assertEquals("11111", record.getValue("zipCode"));
            Assert.assertNull(record.getValue("country"));
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadRawWithDifferentFieldName() throws IOException, MalformedRecordException {
        final List<RecordField> fields = getDefaultFields();
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final String headerLine = "id, name, balance, address, city, state, zipCode, continent";
        final String inputRecord = "1, John, 40.80, 123 My Street, My City, MS, 11111, North America";
        final String csvData = (headerLine + "\n") + inputRecord;
        final byte[] inputData = csvData.getBytes();
        // test nextRecord does not contain a 'continent' field
        try (final InputStream bais = new ByteArrayInputStream(inputData);final JacksonCSVRecordReader reader = createReader(bais, schema, format)) {
            final Record record = reader.nextRecord(true, true);
            Assert.assertNotNull(record);
            Assert.assertEquals("1", record.getValue("id"));
            Assert.assertEquals("John", record.getValue("name"));
            Assert.assertEquals("40.80", record.getValue("balance"));
            Assert.assertEquals("123 My Street", record.getValue("address"));
            Assert.assertEquals("My City", record.getValue("city"));
            Assert.assertEquals("MS", record.getValue("state"));
            Assert.assertEquals("11111", record.getValue("zipCode"));
            Assert.assertNull(record.getValue("country"));
            Assert.assertNull(record.getValue("continent"));
            Assert.assertNull(reader.nextRecord());
        }
        // test nextRawRecord does contain 'continent' field
        try (final InputStream bais = new ByteArrayInputStream(inputData);final JacksonCSVRecordReader reader = createReader(bais, schema, format)) {
            final Record record = reader.nextRecord(false, false);
            Assert.assertNotNull(record);
            Assert.assertEquals("1", record.getValue("id"));
            Assert.assertEquals("John", record.getValue("name"));
            Assert.assertEquals("40.80", record.getValue("balance"));
            Assert.assertEquals("123 My Street", record.getValue("address"));
            Assert.assertEquals("My City", record.getValue("city"));
            Assert.assertEquals("MS", record.getValue("state"));
            Assert.assertEquals("11111", record.getValue("zipCode"));
            Assert.assertNull(record.getValue("country"));
            Assert.assertEquals("North America", record.getValue("continent"));
            Assert.assertNull(reader.nextRecord(false, false));
        }
    }

    @Test
    public void testFieldInSchemaButNotHeader() throws IOException, MalformedRecordException {
        final List<RecordField> fields = getDefaultFields();
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final String headerLine = "id, name, balance, address, city, state, zipCode";
        final String inputRecord = "1, John, 40.80, 123 My Street, My City, MS, 11111, USA";
        final String csvData = (headerLine + "\n") + inputRecord;
        final byte[] inputData = csvData.getBytes();
        try (final InputStream bais = new ByteArrayInputStream(inputData);final JacksonCSVRecordReader reader = createReader(bais, schema, format)) {
            final Record record = reader.nextRecord();
            Assert.assertNotNull(record);
            Assert.assertEquals("1", record.getValue("id"));
            Assert.assertEquals("John", record.getValue("name"));
            Assert.assertEquals("40.80", record.getValue("balance"));
            Assert.assertEquals("123 My Street", record.getValue("address"));
            Assert.assertEquals("My City", record.getValue("city"));
            Assert.assertEquals("MS", record.getValue("state"));
            Assert.assertEquals("11111", record.getValue("zipCode"));
            // If schema says that there are fields a, b, c
            // and the CSV has a header line that says field names are a, b
            // and then the data has values 1,2,3
            // then a=1, b=2, c=null
            Assert.assertNull(record.getValue("country"));
            Assert.assertNull(reader.nextRecord());
        }
        // Create another Record Reader that indicates that the header line is present but should be ignored. This should cause
        // our schema to be the definitive list of what fields exist.
        try (final InputStream bais = new ByteArrayInputStream(inputData);final JacksonCSVRecordReader reader = new JacksonCSVRecordReader(bais, Mockito.mock(ComponentLog.class), schema, format, true, true, DATE.getDefaultFormat(), TIME.getDefaultFormat(), TIMESTAMP.getDefaultFormat(), "UTF-8")) {
            final Record record = reader.nextRecord();
            Assert.assertNotNull(record);
            Assert.assertEquals("1", record.getValue("id"));
            Assert.assertEquals("John", record.getValue("name"));
            Assert.assertEquals("40.80", record.getValue("balance"));
            Assert.assertEquals("123 My Street", record.getValue("address"));
            Assert.assertEquals("My City", record.getValue("city"));
            Assert.assertEquals("MS", record.getValue("state"));
            Assert.assertEquals("11111", record.getValue("zipCode"));
            // If schema says that there are fields a, b, c
            // and the CSV has a header line that says field names are a, b
            // and then the data has values 1,2,3
            // then a=1, b=2, c=null
            // But if we configure the reader to Ignore the header, then this will not occur!
            Assert.assertEquals("USA", record.getValue("country"));
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testExtraFieldNotInHeader() throws IOException, MalformedRecordException {
        final List<RecordField> fields = getDefaultFields();
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final String headerLine = "id, name, balance, address, city, state, zipCode, country";
        final String inputRecord = "1, John, 40.80, 123 My Street, My City, MS, 11111, USA, North America";
        final String csvData = (headerLine + "\n") + inputRecord;
        final byte[] inputData = csvData.getBytes();
        // test nextRecord does not contain a 'continent' field
        try (final InputStream bais = new ByteArrayInputStream(inputData);final JacksonCSVRecordReader reader = createReader(bais, schema, format)) {
            final Record record = reader.nextRecord(false, false);
            Assert.assertNotNull(record);
            Assert.assertEquals("1", record.getValue("id"));
            Assert.assertEquals("John", record.getValue("name"));
            Assert.assertEquals("40.80", record.getValue("balance"));
            Assert.assertEquals("123 My Street", record.getValue("address"));
            Assert.assertEquals("My City", record.getValue("city"));
            Assert.assertEquals("MS", record.getValue("state"));
            Assert.assertEquals("11111", record.getValue("zipCode"));
            Assert.assertEquals("USA", record.getValue("country"));
            Assert.assertEquals("North America", record.getValue("unknown_field_index_8"));
            Assert.assertNull(reader.nextRecord(false, false));
        }
    }

    @Test
    public void testMultipleRecordsEscapedWithSpecialChar() throws IOException, MalformedRecordException {
        char delimiter = StringEscapeUtils.unescapeJava("\u0001").charAt(0);
        final CSVFormat format = DEFAULT.withFirstRecordAsHeader().withTrim().withQuote('"').withDelimiter(delimiter);
        final List<RecordField> fields = getDefaultFields();
        fields.replaceAll(( f) -> f.getFieldName().equals("balance") ? new RecordField("balance", doubleDataType) : f);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream fis = new FileInputStream(new File("src/test/resources/csv/multi-bank-account_escapedchar.csv"));final JacksonCSVRecordReader reader = createReader(fis, schema, format)) {
            final Object[] firstRecord = reader.nextRecord().getValues();
            final Object[] firstExpectedValues = new Object[]{ "1", "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" };
            Assert.assertArrayEquals(firstExpectedValues, firstRecord);
            final Object[] secondRecord = reader.nextRecord().getValues();
            final Object[] secondExpectedValues = new Object[]{ "2", "Jane Doe", 4820.09, "321 Your Street", "Your City", "NY", "33333", "USA" };
            Assert.assertArrayEquals(secondExpectedValues, secondRecord);
            Assert.assertNull(reader.nextRecord());
        }
    }
}

