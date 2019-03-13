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
package org.apache.nifi.xml;


import RecordFieldType.INT;
import RecordFieldType.LONG;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.nifi.schema.access.SchemaNameAsAttribute;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordFieldType;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.RecordSet;
import org.junit.Assert;
import org.junit.Test;
import org.xmlunit.diff.ElementSelectors;
import org.xmlunit.matchers.CompareMatcher;

import static org.apache.nifi.xml.TestWriteXMLResultUtils.NullValues.EMPTY;
import static org.apache.nifi.xml.TestWriteXMLResultUtils.NullValues.HAS_NULL;
import static org.apache.nifi.xml.TestWriteXMLResultUtils.NullValues.ONLY_NULL;
import static org.apache.nifi.xml.TestWriteXMLResultUtils.NullValues.WITHOUT_NULL;


public class TestWriteXMLResult {
    @Test
    public void testRecordNameIsNullSchemaIdentifierMissing() {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecordsWithoutIdentifierInSchema();
        final String expectedMessage = "The property 'Name of Record Tag' has not been set and the writer does not find a record name in the schema.";
        final StringBuilder actualMessage = new StringBuilder();
        try {
            new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "root", null, "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        } catch (IOException e) {
            actualMessage.append(e.getMessage());
        }
        Assert.assertEquals(expectedMessage, actualMessage.toString());
    }

    @Test
    public void testRecordNameIsNullSchemaIdentifierExists() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecords();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", null, "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isIdenticalTo(out.toString()).ignoreWhitespace());
    }

    @Test
    public void testRootNameIsNull() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecords();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, null, "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        final String expectedMessage = "The writer attempts to write multiple record although property \'Name of Root Tag\' " + "has not been set. If the XMLRecordSetWriter is supposed to write multiple records into one FlowFile, this property is required to be configured.";
        final StringBuilder actualMessage = new StringBuilder();
        try {
            writer.write(recordSet);
        } catch (IOException e) {
            actualMessage.append(e.getMessage());
        }
        Assert.assertEquals(expectedMessage, actualMessage.toString());
    }

    @Test
    public void testSingleRecord() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSingleRecord();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, null, "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>";
        Assert.assertThat(xmlResult, CompareMatcher.isIdenticalTo(out.toString()).ignoreWhitespace());
    }

    @Test
    public void testDataTypes() throws IOException, ParseException {
        OutputStream out = new ByteArrayOutputStream();
        final List<RecordField> fields = new ArrayList<>();
        for (final RecordFieldType fieldType : RecordFieldType.values()) {
            if (fieldType == (RecordFieldType.CHOICE)) {
                final List<DataType> possibleTypes = new ArrayList<>();
                possibleTypes.add(INT.getDataType());
                possibleTypes.add(LONG.getDataType());
                fields.add(new RecordField(fieldType.name().toLowerCase(), fieldType.getChoiceDataType(possibleTypes)));
            } else
                if (fieldType == (RecordFieldType.MAP)) {
                    fields.add(new RecordField(fieldType.name().toLowerCase(), fieldType.getMapDataType(INT.getDataType())));
                } else {
                    fields.add(new RecordField(fieldType.name().toLowerCase(), fieldType.getDataType()));
                }

        }
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields, TestWriteXMLResultUtils.SCHEMA_IDENTIFIER_RECORD);
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        final long time = df.parse("2017/01/01 17:00:00.000").getTime();
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("height", 48);
        map.put("width", 96);
        final Map<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put("string", "string");
        valueMap.put("boolean", true);
        valueMap.put("byte", ((byte) (1)));
        valueMap.put("char", 'c');
        valueMap.put("short", ((short) (8)));
        valueMap.put("int", 9);
        valueMap.put("bigint", BigInteger.valueOf(8L));
        valueMap.put("long", 8L);
        valueMap.put("float", 8.0F);
        valueMap.put("double", 8.0);
        valueMap.put("date", new Date(time));
        valueMap.put("time", new Time(time));
        valueMap.put("timestamp", new Timestamp(time));
        valueMap.put("record", null);
        valueMap.put("array", null);
        valueMap.put("choice", 48L);
        valueMap.put("map", map);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, valueMap);
        final RecordSet rs = RecordSet.of(schema, record);
        WriteXMLResult writer = new WriteXMLResult(rs.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "RECORD", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(rs);
        writer.flush();
        String xmlResult = "<ROOT><RECORD><string>string</string><boolean>true</boolean><byte>1</byte><char>c</char><short>8</short>" + (("<int>9</int><bigint>8</bigint><long>8</long><float>8.0</float><double>8.0</double><date>2017-01-01</date>" + "<time>17:00:00</time><timestamp>2017-01-01 17:00:00</timestamp><record /><choice>48</choice><array />") + "<map><height>48</height><width>96</width></map></RECORD></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleRecord() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecords();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isIdenticalTo(out.toString()).ignoreWhitespace());
    }

    @Test
    public void testSimpleRecordWithNullValuesAlwaysSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecordsWithNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isIdenticalTo(out.toString()).ignoreWhitespace());
    }

    @Test
    public void testSimpleRecordWithNullValuesNeverSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecordsWithNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME></NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME></NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isIdenticalTo(out.toString()).ignoreWhitespace());
    }

    @Test
    public void testSimpleRecordWithNullValuesSuppressMissings() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecordsWithNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, SUPPRESS_MISSING, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME></NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isIdenticalTo(out.toString()).ignoreWhitespace());
    }

    @Test
    public void testEmptyRecordWithEmptySchema() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getEmptyRecordsWithEmptySchema();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isIdenticalTo(out.toString()).ignoreWhitespace());
    }

    @Test
    public void testNestedRecord() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getNestedRecords();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY>" + (("<ADDRESS><STREET>292 West Street</STREET><CITY>Jersey City</CITY></ADDRESS></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY><ADDRESS>") + "<STREET>123 6th St.</STREET><CITY>Seattle</CITY></ADDRESS></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testNestedRecordWithNullValuesAlwaysSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getNestedRecordsWithNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><ADDRESS><CITY>Jersey City</CITY></ADDRESS><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><ADDRESS><CITY>Seattle</CITY></ADDRESS><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testNestedRecordWithNullValuesNeverSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getNestedRecordsWithNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY>" + (("<ADDRESS><STREET></STREET><CITY>Jersey City</CITY></ADDRESS></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY><ADDRESS>") + "<STREET></STREET><CITY>Seattle</CITY></ADDRESS></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testNestedRecordWithNullValuesSuppressMissings() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getNestedRecordsWithNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, SUPPRESS_MISSING, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY>" + (("<ADDRESS><STREET></STREET><CITY>Jersey City</CITY></ADDRESS></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY><ADDRESS>") + "<CITY>Seattle</CITY></ADDRESS></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testNestedRecordWithOnlyNullValuesAlwaysSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getNestedRecordsWithOnlyNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testNestedRecordWithOnlyNullValuesNeverSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getNestedRecordsWithOnlyNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><ADDRESS><STREET></STREET><CITY></CITY></ADDRESS>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><ADDRESS><STREET></STREET><CITY></CITY></ADDRESS>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testEmptyNestedRecordEmptySchemaNeverSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getEmptyNestedRecordEmptyNestedSchema();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><ADDRESS></ADDRESS><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><ADDRESS></ADDRESS><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testEmptyNestedRecordEmptySchemaAlwaysSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getEmptyNestedRecordEmptyNestedSchema();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testNestedEmptyRecordDefinedSchemaSuppressMissing() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getEmptyNestedRecordDefinedSchema();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, SUPPRESS_MISSING, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><ADDRESS></ADDRESS><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><ADDRESS></ADDRESS><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleArray() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(WITHOUT_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><CHILDREN>Tom</CHILDREN><CHILDREN>Anna</CHILDREN><CHILDREN>Ben</CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN>Tom</CHILDREN><CHILDREN>Anna</CHILDREN><CHILDREN>Ben</CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleArrayWithNullValuesAlwaysSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(HAS_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><CHILDREN>Tom</CHILDREN><CHILDREN>Ben</CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN>Tom</CHILDREN><CHILDREN>Ben</CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleArrayWithNullValuesNeverSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(HAS_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><CHILDREN>Tom</CHILDREN><CHILDREN></CHILDREN><CHILDREN>Ben</CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN>Tom</CHILDREN><CHILDREN></CHILDREN><CHILDREN>Ben</CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleArrayWithOnlyNullValuesAlwaysSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(ONLY_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleArrayWithOnlyNullValuesAlwaysSuppressWrapping() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(ONLY_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.USE_PROPERTY_AS_WRAPPER, "ARRAY", "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testEmptyArray() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(EMPTY);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testEmptyArrayNeverSupressPropAsWrapper() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(EMPTY);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.USE_PROPERTY_AS_WRAPPER, "ARRAY", "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><ARRAY></ARRAY><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><ARRAY></ARRAY><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleArrayPropAsWrapper() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(WITHOUT_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.USE_PROPERTY_AS_WRAPPER, "ARRAY", "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><ARRAY><CHILDREN>Tom</CHILDREN><CHILDREN>Anna</CHILDREN><CHILDREN>Ben</CHILDREN></ARRAY>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><ARRAY><CHILDREN>Tom</CHILDREN><CHILDREN>Anna</CHILDREN><CHILDREN>Ben</CHILDREN></ARRAY>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleArrayPropForElem() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(WITHOUT_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.USE_PROPERTY_FOR_ELEMENTS, "ELEM", "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><CHILDREN><ELEM>Tom</ELEM><ELEM>Anna</ELEM><ELEM>Ben</ELEM></CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN><ELEM>Tom</ELEM><ELEM>Anna</ELEM><ELEM>Ben</ELEM></CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleMapAlwaysSuppressWithoutNull() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(WITHOUT_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3><CHILD2>Anna</CHILD2></CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3><CHILD2>Anna</CHILD2></CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleMapAlwaysSuppressHasNull() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(HAS_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3></CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3></CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleMapAlwaysSuppressOnlyNull() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(ONLY_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleMapAlwaysSuppressEmpty() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(EMPTY);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        System.out.println(out.toString());
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleMapNeverSuppressHasNull() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(HAS_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3><CHILD2></CHILD2></CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3><CHILD2></CHILD2></CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testSimpleMapNeverSuppressEmpty() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(EMPTY);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><CHILDREN></CHILDREN><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN></CHILDREN><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testChoice() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecordsWithChoice();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    /* Test writeRawRecord */
    @Test
    public void testWriteWithoutSchemaSimpleRecord() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecords();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleRecordWithNullValuesAlwaysSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecordsWithNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleRecordWithNullValuesNeverSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecordsWithNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME></NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleRecordWithNullValuesSuppressMissings() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getSimpleRecordsWithNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, SUPPRESS_MISSING, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME></NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaNestedRecord() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getNestedRecords();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY>" + (("<ADDRESS><STREET>292 West Street</STREET><CITY>Jersey City</CITY></ADDRESS></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY><ADDRESS>") + "<STREET>123 6th St.</STREET><CITY>Seattle</CITY></ADDRESS></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaNestedRecordWithNullValuesAlwaysSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getNestedRecordsWithNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY>" + (("<ADDRESS><CITY>Jersey City</CITY></ADDRESS></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY><ADDRESS>") + "<CITY>Seattle</CITY></ADDRESS></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaNestedRecordWithNullValuesNeverSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getNestedRecordsWithNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY>" + (("<ADDRESS><STREET></STREET><CITY>Jersey City</CITY></ADDRESS></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY><ADDRESS>") + "<CITY>Seattle</CITY></ADDRESS></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaNestedRecordWithOnlyNullValuesAlwaysSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getNestedRecordsWithOnlyNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaNestedRecordWithOnlyNullValuesNeverSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getNestedRecordsWithOnlyNullValues();
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><ADDRESS><STREET></STREET><CITY></CITY></ADDRESS>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><ADDRESS></ADDRESS>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleArray() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(WITHOUT_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><CHILDREN>Tom</CHILDREN><CHILDREN>Anna</CHILDREN><CHILDREN>Ben</CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN>Tom</CHILDREN><CHILDREN>Anna</CHILDREN><CHILDREN>Ben</CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleArrayWithNullValuesAlwaysSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(HAS_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><CHILDREN>Tom</CHILDREN><CHILDREN>Ben</CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN>Tom</CHILDREN><CHILDREN>Ben</CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleArrayWithNullValuesNeverSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(HAS_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><CHILDREN>Tom</CHILDREN><CHILDREN></CHILDREN><CHILDREN>Ben</CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN>Tom</CHILDREN><CHILDREN></CHILDREN><CHILDREN>Ben</CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleArrayWithOnlyNullValuesAlwaysSuppress() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(ONLY_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleArrayWithOnlyNullValuesAlwaysSuppressWrapping() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(ONLY_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.USE_PROPERTY_AS_WRAPPER, "ARRAY", "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaEmptyArray() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(EMPTY);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaEmptyArrayNeverSupressPropAsWrapper() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(EMPTY);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.USE_PROPERTY_AS_WRAPPER, "ARRAY", "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><ARRAY></ARRAY><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><ARRAY></ARRAY><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleArrayPropAsWrapper() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(WITHOUT_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.USE_PROPERTY_AS_WRAPPER, "ARRAY", "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><ARRAY><CHILDREN>Tom</CHILDREN><CHILDREN>Anna</CHILDREN><CHILDREN>Ben</CHILDREN></ARRAY>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><ARRAY><CHILDREN>Tom</CHILDREN><CHILDREN>Anna</CHILDREN><CHILDREN>Ben</CHILDREN></ARRAY>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleArrayPropForElem() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleArray(WITHOUT_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.USE_PROPERTY_FOR_ELEMENTS, "ELEM", "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><CHILDREN><ELEM>Tom</ELEM><ELEM>Anna</ELEM><ELEM>Ben</ELEM></CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN><ELEM>Tom</ELEM><ELEM>Anna</ELEM><ELEM>Ben</ELEM></CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleMapAlwaysSuppressWithoutNull() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(WITHOUT_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3><CHILD2>Anna</CHILD2></CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3><CHILD2>Anna</CHILD2></CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleMapAlwaysSuppressHasNull() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(HAS_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.write(recordSet);
        writer.flush();
        String xmlResult = "<ROOT><PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3></CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3></CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleMapAlwaysSuppressOnlyNull() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(ONLY_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleMapAlwaysSuppressEmpty() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(EMPTY);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, ALWAYS_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleMapNeverSuppressHasNull() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(HAS_NULL);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3><CHILD2></CHILD2></CHILDREN>" + (("<NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN><CHILD1>Tom</CHILD1><CHILD3>Ben</CHILD3><CHILD2></CHILD2></CHILDREN>") + "<NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>");
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }

    @Test
    public void testWriteWithoutSchemaSimpleMapNeverSuppressEmpty() throws IOException {
        OutputStream out = new ByteArrayOutputStream();
        RecordSet recordSet = TestWriteXMLResultUtils.getRecordWithSimpleMap(EMPTY);
        WriteXMLResult writer = new WriteXMLResult(recordSet.getSchema(), new SchemaNameAsAttribute(), out, true, NEVER_SUPPRESS, ArrayWrapping.NO_WRAPPING, null, "ROOT", "PERSON", "UTF-8", TestWriteXMLResultUtils.DATE_FORMAT, TestWriteXMLResultUtils.TIME_FORMAT, TestWriteXMLResultUtils.TIMESTAMP_FORMAT);
        writer.onBeginRecordSet();
        Record record;
        while ((record = recordSet.next()) != null) {
            writer.writeRawRecord(record);
        } 
        writer.onFinishRecordSet();
        writer.flush();
        writer.close();
        String xmlResult = "<ROOT><PERSON><CHILDREN></CHILDREN><NAME>Cleve Butler</NAME><AGE>42</AGE><COUNTRY>USA</COUNTRY></PERSON>" + "<PERSON><CHILDREN></CHILDREN><NAME>Ainslie Fletcher</NAME><AGE>33</AGE><COUNTRY>UK</COUNTRY></PERSON></ROOT>";
        Assert.assertThat(xmlResult, CompareMatcher.isSimilarTo(out.toString()).ignoreWhitespace().withNodeMatcher(new org.xmlunit.diff.DefaultNodeMatcher(ElementSelectors.byNameAndText)));
    }
}

