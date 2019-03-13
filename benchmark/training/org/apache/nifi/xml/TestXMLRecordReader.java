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


import RecordFieldType.CHOICE;
import RecordFieldType.DATE;
import RecordFieldType.INT;
import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.SimpleRecordSchema;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestXMLRecordReader {
    private final String dateFormat = DATE.getDefaultFormat();

    private final String timeFormat = TIME.getDefaultFormat();

    private final String timestampFormat = TIMESTAMP.getDefaultFormat();

    @Test
    public void testSingleRecord() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/person.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), false, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", 42, "USA" }, getValues());
        Assert.assertNull(reader.nextRecord());
    }

    @Test
    public void testMap() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_map.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSchemaForMap(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord();
        Assert.assertEquals("P1", first.getValue("ID"));
        Map firstMap = ((Map) (first.getValue("MAP")));
        Assert.assertEquals("Cleve Butler", firstMap.get("NAME"));
        Assert.assertEquals("42", firstMap.get("AGE"));
        Assert.assertEquals("USA", firstMap.get("COUNTRY"));
        Record second = reader.nextRecord();
        Assert.assertEquals("P2", second.getValue("ID"));
        Map secondMap = ((Map) (second.getValue("MAP")));
        Assert.assertEquals("Ainslie Fletcher", secondMap.get("NAME"));
        Assert.assertEquals("33", secondMap.get("AGE"));
        Assert.assertEquals("UK", secondMap.get("COUNTRY"));
    }

    @Test
    public void testMapWithRecords() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_map2.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSchemaForRecordMap(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord();
        Assert.assertEquals("P1", first.getValue("ID"));
        Map firstMap = ((Map) (first.getValue("MAP")));
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", 42, "USA" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", 33, "UK" }, getValues());
        Record second = reader.nextRecord();
        Assert.assertEquals("P2", second.getValue("ID"));
        Map secondMap = ((Map) (second.getValue("MAP")));
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", 74, "FR" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", 16, "USA" }, getValues());
    }

    @Test
    public void testTagInCharactersSimpleField() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_tag_in_characters.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", 42, null }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", 33, null }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", 74, null }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", 16, null }, getValues());
    }

    @Test
    public void testTagInCharactersRecord() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_tag_in_characters.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSchemaWithNestedRecord3(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(true, true);
        Assert.assertEquals(42, first.getValue("AGE"));
        Record firstNested = ((Record) (first.getValue("NAME")));
        Assert.assertEquals("Cleve Butler", firstNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", firstNested.getValue("ATTR"));
        Assert.assertEquals("inner content", firstNested.getValue("INNER"));
        Record second = reader.nextRecord(true, true);
        Assert.assertEquals(33, second.getValue("AGE"));
        Record secondNested = ((Record) (second.getValue("NAME")));
        Assert.assertEquals("Ainslie Fletcher", secondNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", secondNested.getValue("ATTR"));
        Assert.assertEquals("inner content", secondNested.getValue("INNER"));
        Record third = reader.nextRecord(true, true);
        Assert.assertEquals(74, third.getValue("AGE"));
        Record thirdNested = ((Record) (third.getValue("NAME")));
        Assert.assertEquals("Am?lie Bonfils", thirdNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", thirdNested.getValue("ATTR"));
        Assert.assertEquals("inner content", thirdNested.getValue("INNER"));
        Record fourth = reader.nextRecord(true, true);
        Assert.assertEquals(16, fourth.getValue("AGE"));
        Record fourthNested = ((Record) (fourth.getValue("NAME")));
        Assert.assertEquals("Elenora Scrivens", fourthNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", fourthNested.getValue("ATTR"));
        Assert.assertEquals("inner content", fourthNested.getValue("INNER"));
        Record fifth = reader.nextRecord(true, true);
        Assert.assertNull(fifth.getValue("AGE"));
        Record fifthNested = ((Record) (fifth.getValue("NAME")));
        Assert.assertNull(fifthNested.getValue("CONTENT"));
        Assert.assertNull(fifthNested.getValue("ATTR"));
        Assert.assertEquals("inner content", fifthNested.getValue("INNER"));
    }

    @Test
    public void testTagInCharactersCoerceTrueDropFalse() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_tag_in_characters.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSchemaWithNestedRecord3(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(true, false);
        Assert.assertEquals("P1", first.getValue("ID"));
        Assert.assertEquals(42, first.getValue("AGE"));
        Record firstNested = ((Record) (first.getValue("NAME")));
        Assert.assertEquals("Cleve Butler", firstNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", firstNested.getValue("ATTR"));
        Assert.assertEquals("inner content", firstNested.getValue("INNER"));
        Record second = reader.nextRecord(true, false);
        Assert.assertEquals("P2", second.getValue("ID"));
        Assert.assertEquals(33, second.getValue("AGE"));
        Record secondNested = ((Record) (second.getValue("NAME")));
        Assert.assertEquals("Ainslie Fletcher", secondNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", secondNested.getValue("ATTR"));
        Assert.assertEquals("inner content", secondNested.getValue("INNER"));
        Record third = reader.nextRecord(true, false);
        Assert.assertEquals("P3", third.getValue("ID"));
        Assert.assertEquals(74, third.getValue("AGE"));
        Record thirdNested = ((Record) (third.getValue("NAME")));
        Assert.assertEquals("Am?lie Bonfils", thirdNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", thirdNested.getValue("ATTR"));
        Assert.assertEquals("inner content", thirdNested.getValue("INNER"));
        Record fourth = reader.nextRecord(true, false);
        Assert.assertEquals("P4", fourth.getValue("ID"));
        Assert.assertEquals(16, fourth.getValue("AGE"));
        Record fourthNested = ((Record) (fourth.getValue("NAME")));
        Assert.assertEquals("Elenora Scrivens", fourthNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", fourthNested.getValue("ATTR"));
        Assert.assertEquals("inner content", fourthNested.getValue("INNER"));
        Record fifth = reader.nextRecord(true, false);
        Assert.assertEquals("P5", fifth.getValue("ID"));
        Assert.assertNull(fifth.getValue("AGE"));
        Record fifthNested = ((Record) (fifth.getValue("NAME")));
        Assert.assertNull(fifthNested.getValue("CONTENT"));
        Assert.assertNull(fifthNested.getValue("ATTR"));
        Assert.assertEquals("inner content", fifthNested.getValue("INNER"));
    }

    @Test
    public void testTagInCharactersCoerceFalseDropFalse() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_tag_in_characters.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(false, false);
        Assert.assertEquals("P1", first.getValue("ID"));
        Assert.assertEquals("42", first.getValue("AGE"));
        Record firstNested = ((Record) (first.getValue("NAME")));
        Assert.assertEquals("Cleve Butler", firstNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", firstNested.getValue("ATTR"));
        Assert.assertEquals("inner content", firstNested.getValue("INNER"));
        Record second = reader.nextRecord(false, false);
        Assert.assertEquals("P2", second.getValue("ID"));
        Assert.assertEquals("33", second.getValue("AGE"));
        Record secondNested = ((Record) (second.getValue("NAME")));
        Assert.assertEquals("Ainslie Fletcher", secondNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", secondNested.getValue("ATTR"));
        Assert.assertEquals("inner content", secondNested.getValue("INNER"));
        Record third = reader.nextRecord(false, false);
        Assert.assertEquals("P3", third.getValue("ID"));
        Assert.assertEquals("74", third.getValue("AGE"));
        Record thirdNested = ((Record) (third.getValue("NAME")));
        Assert.assertEquals("Am?lie Bonfils", thirdNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", thirdNested.getValue("ATTR"));
        Assert.assertEquals("inner content", thirdNested.getValue("INNER"));
        Record fourth = reader.nextRecord(false, false);
        Assert.assertEquals("P4", fourth.getValue("ID"));
        Assert.assertEquals("16", fourth.getValue("AGE"));
        Record fourthNested = ((Record) (fourth.getValue("NAME")));
        Assert.assertEquals("Elenora Scrivens", fourthNested.getValue("CONTENT"));
        Assert.assertEquals("attr content", fourthNested.getValue("ATTR"));
        Assert.assertEquals("inner content", fourthNested.getValue("INNER"));
        Record fifth = reader.nextRecord(false, false);
        Assert.assertEquals("P5", fifth.getValue("ID"));
        Assert.assertNull(fifth.getValue("AGE"));
        Record fifthNested = ((Record) (fifth.getValue("NAME")));
        Assert.assertNull(fifthNested.getValue("CONTENT"));
        Assert.assertNull(fifthNested.getValue("ATTR"));
        Assert.assertEquals("inner content", fifthNested.getValue("INNER"));
    }

    @Test
    public void testSimpleRecord() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", 42, "USA" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", 33, "UK" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", 74, "FR" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", 16, "USA" }, getValues());
    }

    @Test
    public void testSimpleRecord2() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema2(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertNull(getValue("AGE"));
        Assert.assertNull(getValue("AGE"));
        Assert.assertNotNull(getValue("AGE"));
        Assert.assertNotNull(getValue("AGE"));
    }

    @Test
    public void testSimpleRecord3() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertEquals(Integer.class, getValue("AGE").getClass());
        Assert.assertEquals(String.class, getValue("AGE").getClass());
    }

    @Test
    public void testSimpleRecord4() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people.xml");
        List<RecordField> fields = getSimpleRecordFields();
        fields.remove(2);
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(fields), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertEquals(Integer.class, getValue("AGE").getClass());
        Assert.assertEquals(String.class, getValue("AGE").getClass());
    }

    @Test
    public void testSimpleRecordCoerceFalseDropFalse() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_no_attributes.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", "42", "USA" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", "33", "UK" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", "74", "FR" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", "16", "USA" }, getValues());
    }

    @Test
    public void testSimpleRecordWithAttribute() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people.xml");
        List<RecordField> fields = getSimpleRecordFields();
        fields.add(new RecordField("ID", STRING.getDataType()));
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(fields), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord();
        Assert.assertTrue(Arrays.asList(first.getValues()).containsAll(Arrays.asList("Cleve Butler", 42, "USA", "P1")));
        Assert.assertEquals("P1", first.getAsString("ID"));
        Record second = reader.nextRecord();
        Assert.assertTrue(Arrays.asList(second.getValues()).containsAll(Arrays.asList("Ainslie Fletcher", 33, "UK", "P2")));
        Assert.assertEquals("P2", second.getAsString("ID"));
        Record third = reader.nextRecord();
        Assert.assertTrue(Arrays.asList(third.getValues()).containsAll(Arrays.asList("Am?lie Bonfils", 74, "FR", "P3")));
        Assert.assertEquals("P3", third.getAsString("ID"));
        Record fourth = reader.nextRecord();
        Assert.assertTrue(Arrays.asList(fourth.getValues()).containsAll(Arrays.asList("Elenora Scrivens", 16, "USA", "P4")));
        Assert.assertEquals("P4", fourth.getAsString("ID"));
    }

    @Test
    public void testSimpleRecordWithAttribute2() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people.xml");
        List<RecordField> fields = getSimpleRecordFields();
        fields.add(new RecordField("ID", STRING.getDataType()));
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(fields), true, "ATTR_", "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord();
        Assert.assertTrue(Arrays.asList(first.getValues()).containsAll(Arrays.asList("Cleve Butler", 42, "USA")));
        Assert.assertEquals("P1", first.getAsString("ATTR_ID"));
        Record second = reader.nextRecord();
        Assert.assertTrue(Arrays.asList(second.getValues()).containsAll(Arrays.asList("Ainslie Fletcher", 33, "UK")));
        Assert.assertEquals("P2", second.getAsString("ATTR_ID"));
        Record third = reader.nextRecord();
        Assert.assertTrue(Arrays.asList(third.getValues()).containsAll(Arrays.asList("Am?lie Bonfils", 74, "FR")));
        Assert.assertEquals("P3", third.getAsString("ATTR_ID"));
        Record fourth = reader.nextRecord();
        Assert.assertTrue(Arrays.asList(fourth.getValues()).containsAll(Arrays.asList("Elenora Scrivens", 16, "USA")));
        Assert.assertEquals("P4", fourth.getAsString("ATTR_ID"));
    }

    @Test
    public void testSimpleRecordWithAttribute3() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people.xml");
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(Collections.emptyList()), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(true, true);
        Assert.assertEquals(null, first.getAsString("ID"));
        Record second = reader.nextRecord(false, false);
        Assert.assertEquals("P2", second.getAsString("ID"));
        Record third = reader.nextRecord(true, false);
        Assert.assertEquals("P3", third.getAsString("ID"));
        Record fourth = reader.nextRecord(false, true);
        Assert.assertEquals(null, fourth.getAsString("ID"));
    }

    @Test
    public void testSimpleRecordWithAttribute4() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people2.xml");
        List<RecordField> fields = getSimpleRecordFields();
        fields.add(new RecordField("ID", INT.getDataType()));
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(fields), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertEquals(Integer.class, getValue("ID").getClass());
        Assert.assertEquals(String.class, getValue("ID").getClass());
    }

    @Test
    public void testSimpleRecordWithAttribute5() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people2.xml");
        List<RecordField> fields = getSimpleRecordFields();
        fields.add(new RecordField("ID", INT.getDataType()));
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(fields), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertEquals(Integer.class, getValue("ID").getClass());
        Assert.assertEquals(String.class, getValue("ID").getClass());
    }

    @Test
    public void testSimpleRecordWithAttributeCoerceFalseDropFalse() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people.xml");
        List<RecordField> fields = getSimpleRecordFields();
        fields.add(new RecordField("ID", STRING.getDataType()));
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(fields), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(false, false);
        Assert.assertTrue(Arrays.asList(new Object[]{ "Cleve Butler", "42", "USA", "P1" }).containsAll(Arrays.asList(first.getValues())));
        Assert.assertEquals("P1", first.getAsString("ID"));
        Record second = reader.nextRecord(false, false);
        Assert.assertTrue(Arrays.asList(new Object[]{ "Ainslie Fletcher", "33", "UK", "P2" }).containsAll(Arrays.asList(second.getValues())));
        Assert.assertEquals("P2", second.getAsString("ID"));
        Record third = reader.nextRecord(false, false);
        Assert.assertTrue(Arrays.asList(new Object[]{ "Am?lie Bonfils", "74", "FR", "P3" }).containsAll(Arrays.asList(third.getValues())));
        Assert.assertEquals("P3", third.getAsString("ID"));
        Record fourth = reader.nextRecord(false, false);
        Assert.assertTrue(Arrays.asList(new Object[]{ "Elenora Scrivens", "16", "USA", "P4" }).containsAll(Arrays.asList(fourth.getValues())));
        Assert.assertEquals("P4", fourth.getAsString("ID"));
    }

    @Test
    public void testSimpleTypeWithAttributeAsRecord() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people3.xml");
        final List<RecordField> fields = new ArrayList<>();
        final List<RecordField> nestedFields1 = new ArrayList<>();
        nestedFields1.add(new RecordField("ID", STRING.getDataType()));
        nestedFields1.add(new RecordField("CONTENT", STRING.getDataType()));
        final DataType recordType1 = RECORD.getRecordDataType(new SimpleRecordSchema(nestedFields1));
        fields.add(new RecordField("NAME", recordType1));
        final List<RecordField> nestedFields2 = new ArrayList<>();
        nestedFields2.add(new RecordField("ID", STRING.getDataType()));
        nestedFields2.add(new RecordField("CONTENT", INT.getDataType()));
        final DataType recordType2 = RECORD.getRecordDataType(new SimpleRecordSchema(nestedFields2));
        fields.add(new RecordField("AGE", recordType2));
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(fields), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(true, true);
        Assert.assertTrue(((first.getValue("NAME")) instanceof Record));
        Record first_nested1 = ((Record) (first.getValue("NAME")));
        Assert.assertTrue(((first.getValue("AGE")) instanceof Record));
        Record first_nested2 = ((Record) (first.getValue("AGE")));
        Assert.assertEquals("name1", first_nested1.getValue("ID"));
        Assert.assertEquals("Cleve Butler", first_nested1.getValue("CONTENT"));
        Assert.assertEquals("age1", first_nested2.getValue("ID"));
        Assert.assertEquals(42, first_nested2.getValue("CONTENT"));
        Record second = reader.nextRecord(true, true);
        Assert.assertTrue(((second.getValue("NAME")) instanceof Record));
        Record second_nested1 = ((Record) (second.getValue("NAME")));
        Assert.assertTrue(((second.getValue("AGE")) instanceof Record));
        Record second_nested2 = ((Record) (second.getValue("AGE")));
        Assert.assertEquals("name2", second_nested1.getValue("ID"));
        Assert.assertEquals("Ainslie Fletcher", second_nested1.getValue("CONTENT"));
        Assert.assertEquals("age2", second_nested2.getValue("ID"));
        Assert.assertEquals(33, second_nested2.getValue("CONTENT"));
    }

    @Test
    public void testSimpleTypeWithAttributeAsRecordCoerceFalseDropFalse() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people3.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(false, false);
        Assert.assertTrue(((first.getValue("NAME")) instanceof Record));
        Record first_nested1 = ((Record) (first.getValue("NAME")));
        Assert.assertTrue(((first.getValue("AGE")) instanceof Record));
        Record first_nested2 = ((Record) (first.getValue("AGE")));
        Assert.assertEquals("name1", first_nested1.getValue("ID"));
        Assert.assertEquals("Cleve Butler", first_nested1.getValue("CONTENT"));
        Assert.assertEquals("age1", first_nested2.getValue("ID"));
        Assert.assertEquals("42", first_nested2.getValue("CONTENT"));
        Assert.assertEquals("USA", first.getValue("COUNTRY"));
        Record second = reader.nextRecord(false, false);
        Assert.assertTrue(((second.getValue("NAME")) instanceof Record));
        Record second_nested1 = ((Record) (second.getValue("NAME")));
        Assert.assertTrue(((second.getValue("AGE")) instanceof Record));
        Record second_nested2 = ((Record) (second.getValue("AGE")));
        Assert.assertEquals("name2", second_nested1.getValue("ID"));
        Assert.assertEquals("Ainslie Fletcher", second_nested1.getValue("CONTENT"));
        Assert.assertEquals("age2", second_nested2.getValue("ID"));
        Assert.assertEquals("33", second_nested2.getValue("CONTENT"));
        Assert.assertEquals("UK", second.getValue("COUNTRY"));
    }

    @Test
    public void testSimpleRecordWithHeader() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_with_header_and_comments.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, null, dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", 42, "USA" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", 33, "UK" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", 74, "FR" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", 16, "USA" }, getValues());
    }

    @Test
    public void testSimpleRecordWithHeaderNoValidation() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_with_header_and_comments.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, null, dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", 42, "USA" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", 33, "UK" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", 74, "FR" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", 16, "USA" }, getValues());
    }

    @Test
    public void testInvalidXml() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_invalid.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        int count = 0;
        /* Due to the missing starting tag <PERSON> for the third entry in people_invalid.xml, the reader assumes <NAME> and
        <AGE> to be records. The tag <COUNTRY> is also assumed to be a record, but the exception is thrown
        before the "record" for <COUNTRY> is returned. Even a tracking of the parsing depth would not help to overcome this problem.
         */
        try {
            while ((reader.nextRecord()) != null) {
                count++;
            } 
        } catch (MalformedRecordException e) {
            Assert.assertEquals("Could not parse XML", e.getMessage());
            Assert.assertEquals(4, count);
        }
    }

    @Test
    public void testChoiceForSimpleField() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people.xml");
        List<RecordField> fields = getSimpleRecordFields2();
        fields.add(new RecordField("AGE", CHOICE.getDataType()));
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(fields), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record record = reader.nextRecord();
        Assert.assertTrue(((record.getValue("AGE")) instanceof String));
        Assert.assertEquals("42", record.getValue("AGE"));
    }

    @Test
    public void testChoiceForRecord() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_nested.xml");
        List<RecordField> fields = getSimpleRecordFields();
        fields.add(new RecordField("ADDRESS", CHOICE.getDataType()));
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(fields), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record record = reader.nextRecord();
        Assert.assertTrue(((record.getValue("ADDRESS")) instanceof Record));
        Record nested = ((Record) (record.getValue("ADDRESS")));
        Assert.assertEquals("292 West Street", nested.getValue("STREET"));
        Assert.assertEquals("Jersey City", nested.getValue("CITY"));
    }

    @Test
    public void testNameSpaces() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_namespace.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", 42, "USA" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", 33, "UK" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", 74, "FR" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", 16, "USA" }, getValues());
    }

    @Test
    public void testCData() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_cdata.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", 42, "USA" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", 33, "UK" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", 74, "FR" }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", 16, "USA" }, getValues());
    }

    @Test
    public void testRecordExpectedSimpleFieldFoundAndNoContentFieldConfigured() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people.xml");
        List<RecordField> fields = getSimpleRecordFields2();
        final DataType recordType = RECORD.getRecordDataType(getNestedSchema());
        fields.add(new RecordField("AGE", recordType));
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(fields), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", "USA", null }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", "UK", null }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", "FR", null }, getValues());
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", "USA", null }, getValues());
    }

    @Test
    public void testSimpleFieldExpectedButRecordFound() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_nested.xml");
        List<RecordField> fields = getSimpleRecordFields();
        fields.add(new RecordField("ADDRESS", STRING.getDataType()));
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(fields), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertNull(getValue("ADDRESS"));
        Assert.assertNull(getValue("ADDRESS"));
        Assert.assertNull(getValue("ADDRESS"));
        Assert.assertNull(getValue("ADDRESS"));
    }

    @Test
    public void testParseEmptyFields() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_empty.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertArrayEquals(new Object[]{ null, null, null }, getValues());
        Assert.assertArrayEquals(new Object[]{ null, null, null }, getValues());
    }

    @Test
    public void testParseEmptyFieldsCoerceFalseDropFalse() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_empty.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertArrayEquals(new Object[]{ null, null, null }, getValues());
        Assert.assertArrayEquals(new Object[]{ null, null, null }, getValues());
    }

    @Test(expected = MalformedRecordException.class)
    public void testEmptyStreamAsSingleRecord() throws IOException, MalformedRecordException {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), false, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        reader.nextRecord(true, true);
    }

    @Test(expected = MalformedRecordException.class)
    public void testEmptyStreamAsArray() throws IOException, MalformedRecordException {
        InputStream is = new ByteArrayInputStream(new byte[0]);
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        reader.nextRecord(true, true);
    }

    @Test(expected = MalformedRecordException.class)
    public void testEmptyStreamWIthXmlHeader() throws IOException, MalformedRecordException {
        InputStream is = new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"utf-8\"?>".getBytes());
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record record = reader.nextRecord(false, false);
        Assert.assertNull(record);
    }

    @Test
    public void testParseEmptyArray() throws IOException, MalformedRecordException {
        InputStream is = new ByteArrayInputStream("<root></root>".getBytes());
        XMLRecordReader reader = new XMLRecordReader(is, getSimpleSchema(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Assert.assertNull(reader.nextRecord());
    }

    @Test
    public void testNestedRecord() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_nested.xml");
        RecordSchema schema = getSchemaWithNestedRecord();
        XMLRecordReader reader = new XMLRecordReader(is, schema, true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Object[] valuesFirstRecord = reader.nextRecord().getValues();
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", 42, "USA" }, Arrays.copyOfRange(valuesFirstRecord, 0, ((valuesFirstRecord.length) - 1)));
        Assert.assertArrayEquals(new Object[]{ "292 West Street", "Jersey City" }, getValues());
        Object[] valuesSecondRecord = reader.nextRecord().getValues();
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", 33, "UK" }, Arrays.copyOfRange(valuesSecondRecord, 0, ((valuesSecondRecord.length) - 1)));
        Assert.assertArrayEquals(new Object[]{ "123 6th St.", "Seattle" }, getValues());
        Object[] valuesThirdRecord = reader.nextRecord().getValues();
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", 74, "FR" }, Arrays.copyOfRange(valuesThirdRecord, 0, ((valuesThirdRecord.length) - 1)));
        Assert.assertArrayEquals(new Object[]{ "44 Shirley Ave.", "Los Angeles" }, getValues());
        Object[] valuesFourthRecord = reader.nextRecord().getValues();
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", 16, "USA" }, Arrays.copyOfRange(valuesFourthRecord, 0, ((valuesFourthRecord.length) - 1)));
        Assert.assertArrayEquals(new Object[]{ "70 Bowman St.", "Columbus" }, getValues());
    }

    @Test
    public void testNestedRecordCoerceFalseDropFalse() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_nested.xml");
        RecordSchema schema = getSchemaWithNestedRecord();
        XMLRecordReader reader = new XMLRecordReader(is, schema, true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(false, false);
        Object[] valuesFirstRecord = first.getValues();
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", "42", "USA" }, Arrays.copyOfRange(valuesFirstRecord, 0, ((valuesFirstRecord.length) - 1)));
        Assert.assertEquals("P1", first.getAsString("ID"));
        Record nestedFirstRecord = ((Record) (first.getValue("ADDRESS")));
        Assert.assertEquals("Jersey City", nestedFirstRecord.getAsString("CITY"));
        Assert.assertEquals("292 West Street", nestedFirstRecord.getAsString("STREET"));
        Record second = reader.nextRecord(false, false);
        Object[] valuesSecondRecord = second.getValues();
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", "33", "UK" }, Arrays.copyOfRange(valuesSecondRecord, 0, ((valuesSecondRecord.length) - 1)));
        Assert.assertEquals("P2", second.getAsString("ID"));
        Record nestedSecondRecord = ((Record) (second.getValue("ADDRESS")));
        Assert.assertEquals("Seattle", nestedSecondRecord.getAsString("CITY"));
        Assert.assertEquals("123 6th St.", nestedSecondRecord.getAsString("STREET"));
        Record third = reader.nextRecord(false, false);
        Object[] valuesThirdRecord = third.getValues();
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", "74", "FR" }, Arrays.copyOfRange(valuesThirdRecord, 0, ((valuesThirdRecord.length) - 1)));
        Assert.assertEquals("P3", third.getAsString("ID"));
        Record nestedThirdRecord = ((Record) (third.getValue("ADDRESS")));
        Assert.assertEquals("Los Angeles", nestedThirdRecord.getAsString("CITY"));
        Assert.assertEquals("44 Shirley Ave.", nestedThirdRecord.getAsString("STREET"));
        Record fourth = reader.nextRecord(false, false);
        Object[] valuesFourthRecord = fourth.getValues();
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", "16", "USA" }, Arrays.copyOfRange(valuesFourthRecord, 0, ((valuesFourthRecord.length) - 1)));
        Assert.assertEquals("P4", fourth.getAsString("ID"));
        Record nestedFourthRecord = ((Record) (fourth.getValue("ADDRESS")));
        Assert.assertEquals("Columbus", nestedFourthRecord.getAsString("CITY"));
        Assert.assertEquals("70 Bowman St.", nestedFourthRecord.getAsString("STREET"));
    }

    @Test
    public void testNestedRecordFieldsToIgnoreCoerceTrueDropTrue() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_nested.xml");
        // Fields "AGE" and "ADDRESS/CITY" are not defined here
        RecordSchema schema = getSchemaWithNestedRecord2();
        XMLRecordReader reader = new XMLRecordReader(is, schema, true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record firstRecord = reader.nextRecord(true, true);
        Object[] valuesFirstRecord = firstRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", "USA" }, Arrays.copyOfRange(valuesFirstRecord, 0, ((valuesFirstRecord.length) - 1)));
        Record firstRecordNested = ((Record) (firstRecord.getValue("ADDRESS")));
        Assert.assertEquals("292 West Street", firstRecordNested.getValue("STREET"));
        Assert.assertNull(firstRecord.getValue("AGE"));
        Assert.assertNull(firstRecordNested.getValue("CITY"));
        Record secondRecord = reader.nextRecord(true, true);
        Object[] valuesSecondRecord = secondRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", "UK" }, Arrays.copyOfRange(valuesSecondRecord, 0, ((valuesSecondRecord.length) - 1)));
        Record secondRecordNested = ((Record) (secondRecord.getValue("ADDRESS")));
        Assert.assertEquals("123 6th St.", secondRecordNested.getValue("STREET"));
        Assert.assertNull(secondRecord.getValue("AGE"));
        Assert.assertNull(secondRecordNested.getValue("CITY"));
        Record thirdRecord = reader.nextRecord(true, true);
        Object[] valuesThirdRecord = thirdRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", "FR" }, Arrays.copyOfRange(valuesThirdRecord, 0, ((valuesThirdRecord.length) - 1)));
        Record thirdRecordNested = ((Record) (thirdRecord.getValue("ADDRESS")));
        Assert.assertEquals("44 Shirley Ave.", thirdRecordNested.getValue("STREET"));
        Assert.assertNull(thirdRecord.getValue("AGE"));
        Assert.assertNull(thirdRecordNested.getValue("CITY"));
        Record fourthRecord = reader.nextRecord(true, true);
        Object[] valuesFourthRecord = fourthRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", "USA" }, Arrays.copyOfRange(valuesFourthRecord, 0, ((valuesFourthRecord.length) - 1)));
        Record fourthRecordNested = ((Record) (fourthRecord.getValue("ADDRESS")));
        Assert.assertEquals("70 Bowman St.", fourthRecordNested.getValue("STREET"));
        Assert.assertNull(fourthRecord.getValue("AGE"));
        Assert.assertNull(fourthRecordNested.getValue("CITY"));
    }

    @Test
    public void testNestedRecordFieldsToIgnoreCoerceFalseDropTrue() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_nested.xml");
        // Fields "AGE" and "ADDRESS/CITY" are not defined here
        RecordSchema schema = getSchemaWithNestedRecord2();
        XMLRecordReader reader = new XMLRecordReader(is, schema, true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record firstRecord = reader.nextRecord(false, true);
        Object[] valuesFirstRecord = firstRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", "USA" }, Arrays.copyOfRange(valuesFirstRecord, 0, ((valuesFirstRecord.length) - 1)));
        Record firstRecordNested = ((Record) (firstRecord.getValue("ADDRESS")));
        Assert.assertEquals("292 West Street", firstRecordNested.getValue("STREET"));
        Assert.assertNull(firstRecord.getValue("AGE"));
        Assert.assertNull(firstRecordNested.getValue("CITY"));
        Record secondRecord = reader.nextRecord(false, true);
        Object[] valuesSecondRecord = secondRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", "UK" }, Arrays.copyOfRange(valuesSecondRecord, 0, ((valuesSecondRecord.length) - 1)));
        Record secondRecordNested = ((Record) (secondRecord.getValue("ADDRESS")));
        Assert.assertEquals("123 6th St.", secondRecordNested.getValue("STREET"));
        Assert.assertNull(secondRecord.getValue("AGE"));
        Assert.assertNull(secondRecordNested.getValue("CITY"));
        Record thirdRecord = reader.nextRecord(false, true);
        Object[] valuesThirdRecord = thirdRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", "FR" }, Arrays.copyOfRange(valuesThirdRecord, 0, ((valuesThirdRecord.length) - 1)));
        Record thirdRecordNested = ((Record) (thirdRecord.getValue("ADDRESS")));
        Assert.assertEquals("44 Shirley Ave.", thirdRecordNested.getValue("STREET"));
        Assert.assertNull(thirdRecord.getValue("AGE"));
        Assert.assertNull(thirdRecordNested.getValue("CITY"));
        Record fourthRecord = reader.nextRecord(false, true);
        Object[] valuesFourthRecord = fourthRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", "USA" }, Arrays.copyOfRange(valuesFourthRecord, 0, ((valuesFourthRecord.length) - 1)));
        Record fourthRecordNested = ((Record) (fourthRecord.getValue("ADDRESS")));
        Assert.assertEquals("70 Bowman St.", fourthRecordNested.getValue("STREET"));
        Assert.assertNull(fourthRecord.getValue("AGE"));
        Assert.assertNull(fourthRecordNested.getValue("CITY"));
    }

    @Test
    public void testNestedRecordFieldsToIgnoreCoerceTrueDropFalse() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_nested.xml");
        // Fields "AGE" and "ADDRESS/CITY" are not defined here
        RecordSchema schema = getSchemaWithNestedRecord2();
        XMLRecordReader reader = new XMLRecordReader(is, schema, true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record firstRecord = reader.nextRecord(true, false);
        Object[] valuesFirstRecord = firstRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", "USA" }, Arrays.copyOfRange(valuesFirstRecord, 0, ((valuesFirstRecord.length) - 1)));
        Record firstRecordNested = ((Record) (firstRecord.getValue("ADDRESS")));
        Assert.assertEquals("292 West Street", firstRecordNested.getValue("STREET"));
        Assert.assertNotNull(firstRecord.getValue("AGE"));
        Assert.assertEquals("42", firstRecord.getValue("AGE"));
        Assert.assertNotNull(firstRecordNested.getValue("CITY"));
        Assert.assertEquals("Jersey City", firstRecordNested.getValue("CITY"));
        Record secondRecord = reader.nextRecord(true, false);
        Object[] valuesSecondRecord = secondRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", "UK" }, Arrays.copyOfRange(valuesSecondRecord, 0, ((valuesSecondRecord.length) - 1)));
        Record secondRecordNested = ((Record) (secondRecord.getValue("ADDRESS")));
        Assert.assertEquals("123 6th St.", secondRecordNested.getValue("STREET"));
        Assert.assertNotNull(secondRecord.getValue("AGE"));
        Assert.assertEquals("33", secondRecord.getValue("AGE"));
        Assert.assertNotNull(secondRecordNested.getValue("CITY"));
        Assert.assertEquals("Seattle", secondRecordNested.getValue("CITY"));
        Record thirdRecord = reader.nextRecord(true, false);
        Object[] valuesThirdRecord = thirdRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", "FR" }, Arrays.copyOfRange(valuesThirdRecord, 0, ((valuesThirdRecord.length) - 1)));
        Record thirdRecordNested = ((Record) (thirdRecord.getValue("ADDRESS")));
        Assert.assertEquals("44 Shirley Ave.", thirdRecordNested.getValue("STREET"));
        Assert.assertNotNull(thirdRecord.getValue("AGE"));
        Assert.assertEquals("74", thirdRecord.getValue("AGE"));
        Assert.assertNotNull(thirdRecordNested.getValue("CITY"));
        Assert.assertEquals("Los Angeles", thirdRecordNested.getValue("CITY"));
        Record fourthRecord = reader.nextRecord(true, false);
        Object[] valuesFourthRecord = fourthRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", "USA" }, Arrays.copyOfRange(valuesFourthRecord, 0, ((valuesFourthRecord.length) - 1)));
        Record fourthRecordNested = ((Record) (fourthRecord.getValue("ADDRESS")));
        Assert.assertEquals("70 Bowman St.", fourthRecordNested.getValue("STREET"));
        Assert.assertNotNull(fourthRecord.getValue("AGE"));
        Assert.assertEquals("16", fourthRecord.getValue("AGE"));
        Assert.assertNotNull(fourthRecordNested.getValue("CITY"));
        Assert.assertEquals("Columbus", fourthRecordNested.getValue("CITY"));
    }

    @Test
    public void testNestedRecordFieldsToIgnoreCoerceFalseDropFalse() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_nested.xml");
        // Fields "AGE" and "ADDRESS/CITY" are not defined here
        RecordSchema schema = getSchemaWithNestedRecord2();
        XMLRecordReader reader = new XMLRecordReader(is, schema, true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record firstRecord = reader.nextRecord(false, false);
        Object[] valuesFirstRecord = firstRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", "USA" }, Arrays.copyOfRange(valuesFirstRecord, 0, ((valuesFirstRecord.length) - 1)));
        Record firstRecordNested = ((Record) (firstRecord.getValue("ADDRESS")));
        Assert.assertEquals("292 West Street", firstRecordNested.getValue("STREET"));
        Assert.assertNotNull(firstRecord.getValue("AGE"));
        Assert.assertEquals("42", firstRecord.getValue("AGE"));
        Assert.assertNotNull(firstRecordNested.getValue("CITY"));
        Assert.assertEquals("Jersey City", firstRecordNested.getValue("CITY"));
        Record secondRecord = reader.nextRecord(false, false);
        Object[] valuesSecondRecord = secondRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", "UK" }, Arrays.copyOfRange(valuesSecondRecord, 0, ((valuesSecondRecord.length) - 1)));
        Record secondRecordNested = ((Record) (secondRecord.getValue("ADDRESS")));
        Assert.assertEquals("123 6th St.", secondRecordNested.getValue("STREET"));
        Assert.assertNotNull(secondRecord.getValue("AGE"));
        Assert.assertEquals("33", secondRecord.getValue("AGE"));
        Assert.assertNotNull(secondRecordNested.getValue("CITY"));
        Assert.assertEquals("Seattle", secondRecordNested.getValue("CITY"));
        Record thirdRecord = reader.nextRecord(false, false);
        Object[] valuesThirdRecord = thirdRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", "FR" }, Arrays.copyOfRange(valuesThirdRecord, 0, ((valuesThirdRecord.length) - 1)));
        Record thirdRecordNested = ((Record) (thirdRecord.getValue("ADDRESS")));
        Assert.assertEquals("44 Shirley Ave.", thirdRecordNested.getValue("STREET"));
        Assert.assertNotNull(thirdRecord.getValue("AGE"));
        Assert.assertEquals("74", thirdRecord.getValue("AGE"));
        Assert.assertNotNull(thirdRecordNested.getValue("CITY"));
        Assert.assertEquals("Los Angeles", thirdRecordNested.getValue("CITY"));
        Record fourthRecord = reader.nextRecord(false, false);
        Object[] valuesFourthRecord = fourthRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", "USA" }, Arrays.copyOfRange(valuesFourthRecord, 0, ((valuesFourthRecord.length) - 1)));
        Record fourthRecordNested = ((Record) (fourthRecord.getValue("ADDRESS")));
        Assert.assertEquals("70 Bowman St.", fourthRecordNested.getValue("STREET"));
        Assert.assertNotNull(fourthRecord.getValue("AGE"));
        Assert.assertEquals("16", fourthRecord.getValue("AGE"));
        Assert.assertNotNull(fourthRecordNested.getValue("CITY"));
        Assert.assertEquals("Columbus", fourthRecordNested.getValue("CITY"));
    }

    @Test
    public void testSimpleArray() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_array_simple.xml");
        RecordSchema schema = getSchemaWithSimpleArray();
        XMLRecordReader reader = new XMLRecordReader(is, schema, true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record firstRecord = reader.nextRecord();
        Object[] valuesFirstRecord = firstRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", 42, "USA" }, Arrays.copyOfRange(valuesFirstRecord, 0, ((valuesFirstRecord.length) - 1)));
        Object[] nestedArrayFirstRecord = ((Object[]) (valuesFirstRecord[((valuesFirstRecord.length) - 1)]));
        Assert.assertEquals(2, nestedArrayFirstRecord.length);
        Assert.assertArrayEquals(new Object[]{ "child1", "child2" }, nestedArrayFirstRecord);
        Assert.assertNotEquals(null, firstRecord.getValue("CHILD"));
        Record secondRecord = reader.nextRecord();
        Object[] valuesSecondRecord = secondRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", 33, "UK" }, Arrays.copyOfRange(valuesSecondRecord, 0, ((valuesSecondRecord.length) - 1)));
        Object[] nestedArraySecondRecord = ((Object[]) (valuesSecondRecord[((valuesSecondRecord.length) - 1)]));
        Assert.assertEquals(1, nestedArraySecondRecord.length);
        Assert.assertArrayEquals(new Object[]{ "child1" }, nestedArraySecondRecord);
        Assert.assertNotEquals(null, secondRecord.getValue("CHILD"));
        Record thirdRecord = reader.nextRecord();
        Object[] valuesThirdRecord = thirdRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", 74, "FR" }, Arrays.copyOfRange(valuesThirdRecord, 0, ((valuesThirdRecord.length) - 1)));
        Object[] nestedArrayThirdRecord = ((Object[]) (valuesThirdRecord[((valuesThirdRecord.length) - 1)]));
        Assert.assertEquals(3, nestedArrayThirdRecord.length);
        Assert.assertArrayEquals(new Object[]{ "child1", "child2", "child3" }, nestedArrayThirdRecord);
        Assert.assertNotEquals(null, thirdRecord.getValue("CHILD"));
        Record valuesFourthRecord = reader.nextRecord();
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", 16, "USA" }, Arrays.copyOfRange(valuesFourthRecord.getValues(), 0, ((valuesFourthRecord.getValues().length) - 1)));
        Assert.assertEquals(null, valuesFourthRecord.getValue("CHILD"));
    }

    @Test
    public void testSimpleArrayCoerceFalseDropFalse() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_array_simple.xml");
        RecordSchema schema = getSchemaWithSimpleArray();
        XMLRecordReader reader = new XMLRecordReader(is, schema, true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(false, false);
        Object[] valuesFirstRecord = first.getValues();
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", "42", "USA" }, Arrays.copyOfRange(valuesFirstRecord, 0, ((valuesFirstRecord.length) - 1)));
        Object[] nestedArrayFirstRecord = ((Object[]) (valuesFirstRecord[((valuesFirstRecord.length) - 1)]));
        Assert.assertEquals(2, nestedArrayFirstRecord.length);
        Assert.assertArrayEquals(new Object[]{ "child1", "child2" }, nestedArrayFirstRecord);
        Assert.assertNotEquals(null, first.getValue("CHILD"));
        Record second = reader.nextRecord(false, false);
        Object[] valuesSecondRecord = second.getValues();
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", "33", "UK" }, Arrays.copyOfRange(valuesSecondRecord, 0, ((valuesSecondRecord.length) - 1)));
        String nestedArraySecondRecord = ((String) (valuesSecondRecord[((valuesSecondRecord.length) - 1)]));
        Assert.assertEquals("child1", nestedArraySecondRecord);
        Assert.assertNotEquals(null, second.getValue("CHILD"));
        Record third = reader.nextRecord(false, false);
        Object[] valuesThirdRecord = third.getValues();
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", "74", "FR" }, Arrays.copyOfRange(valuesThirdRecord, 0, ((valuesThirdRecord.length) - 1)));
        Object[] nestedArrayThirdRecord = ((Object[]) (valuesThirdRecord[((valuesThirdRecord.length) - 1)]));
        Assert.assertEquals(3, nestedArrayThirdRecord.length);
        Assert.assertArrayEquals(new Object[]{ "child1", "child2", "child3" }, nestedArrayThirdRecord);
        Assert.assertNotEquals(null, third.getValue("CHILD"));
        Record fourth = reader.nextRecord(false, false);
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", "16", "USA" }, Arrays.copyOfRange(fourth.getValues(), 0, ((fourth.getValues().length) - 1)));
        Assert.assertEquals(null, fourth.getValue("CHILD"));
    }

    @Test
    public void testNestedArrayInNestedRecord() throws IOException, MalformedRecordException {
        InputStream is = new FileInputStream("src/test/resources/xml/people_array.xml");
        RecordSchema schema = getSchemaWithNestedArray();
        XMLRecordReader reader = new XMLRecordReader(is, schema, true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record firstRecord = reader.nextRecord();
        Object[] valuesFirstRecord = firstRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Cleve Butler", 42, "USA" }, Arrays.copyOfRange(valuesFirstRecord, 0, ((valuesFirstRecord.length) - 1)));
        Record nestedArrayFirstRecord = ((Record) (firstRecord.getValue("CHILDREN")));
        Assert.assertEquals(2, ((Object[]) (nestedArrayFirstRecord.getValue("CHILD"))).length);
        Assert.assertArrayEquals(new Object[]{ "child1", "child2" }, ((Object[]) (nestedArrayFirstRecord.getValue("CHILD"))));
        Record secondRecord = reader.nextRecord();
        Object[] valuesSecondRecord = secondRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Ainslie Fletcher", 33, "UK" }, Arrays.copyOfRange(valuesSecondRecord, 0, ((valuesSecondRecord.length) - 1)));
        Record nestedArraySecondRecord = ((Record) (secondRecord.getValue("CHILDREN")));
        Assert.assertEquals(1, ((Object[]) (nestedArraySecondRecord.getValue("CHILD"))).length);
        Assert.assertArrayEquals(new Object[]{ "child1" }, ((Object[]) (nestedArraySecondRecord.getValue("CHILD"))));
        Record thirdRecord = reader.nextRecord();
        Object[] valuesThirdRecord = thirdRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Am?lie Bonfils", 74, "FR" }, Arrays.copyOfRange(valuesThirdRecord, 0, ((valuesThirdRecord.length) - 1)));
        Record nestedArrayThirdRecord = ((Record) (thirdRecord.getValue("CHILDREN")));
        Assert.assertEquals(3, ((Object[]) (nestedArrayThirdRecord.getValue("CHILD"))).length);
        Assert.assertArrayEquals(new Object[]{ "child1", "child2", "child3" }, ((Object[]) (nestedArrayThirdRecord.getValue("CHILD"))));
        Record fourthRecord = reader.nextRecord();
        Object[] valuesFourthRecord = fourthRecord.getValues();
        Assert.assertArrayEquals(new Object[]{ "Elenora Scrivens", 16, "USA" }, Arrays.copyOfRange(valuesFourthRecord, 0, ((valuesFourthRecord.length) - 1)));
        Assert.assertEquals(null, fourthRecord.getValue("CHILDREN"));
    }

    @Test
    public void testDeeplyNestedArraysAndRecords() throws IOException, MalformedRecordException {
        // test records in nested arrays
        InputStream is = new FileInputStream("src/test/resources/xml/people_complex1.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSchemaForComplexData(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(true, true);
        Object[] grandchildren_arr = ((Object[]) (first.getValue("CHILDREN")));
        Record first_1_1_1 = ((Record) (((Object[]) (getValue("CHILD")))[0]));
        Assert.assertEquals("daughter", first_1_1_1.getValue("ROLE"));
        Assert.assertEquals("1-1-1", first_1_1_1.getValue("ID"));
        Assert.assertEquals("Selina", first_1_1_1.getValue("NAME"));
        Record first_1_1_2 = ((Record) (((Object[]) (getValue("CHILD")))[1]));
        Assert.assertEquals("son", first_1_1_2.getValue("ROLE"));
        Assert.assertEquals("1-1-2", first_1_1_2.getValue("ID"));
        Assert.assertEquals("Hans", first_1_1_2.getValue("NAME"));
        Record first_1_1_3 = ((Record) (((Object[]) (getValue("CHILD")))[0]));
        Assert.assertEquals("daughter", first_1_1_3.getValue("ROLE"));
        Assert.assertEquals("1-2-1", first_1_1_3.getValue("ID"));
        Assert.assertEquals("Selina2", first_1_1_3.getValue("NAME"));
        Record first_1_1_4 = ((Record) (((Object[]) (getValue("CHILD")))[1]));
        Assert.assertEquals("son", first_1_1_4.getValue("ROLE"));
        Assert.assertEquals("1-2-2", first_1_1_4.getValue("ID"));
        Assert.assertEquals("Hans2", first_1_1_4.getValue("NAME"));
        Record second = reader.nextRecord(true, true);
        Object[] grandchildren_arr2 = ((Object[]) (second.getValue("CHILDREN")));
        Record second_2_1_1 = ((Record) (((Object[]) (getValue("CHILD")))[0]));
        Assert.assertEquals("daughter", second_2_1_1.getValue("ROLE"));
        Assert.assertEquals("2-1-1", second_2_1_1.getValue("ID"));
        Assert.assertEquals("Selina3", second_2_1_1.getValue("NAME"));
    }

    @Test
    public void testDeeplyNestedArraysAndRecords2() throws IOException, MalformedRecordException {
        // test multiply nested arrays and records (recursion)
        InputStream is = new FileInputStream("src/test/resources/xml/people_complex2.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSchemaForComplexData2(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord();
        Assert.assertEquals("grandmother", first.getValue("ROLE"));
        Assert.assertEquals("1", first.getValue("ID"));
        Assert.assertEquals("Lisa", first.getValue("NAME"));
        Object[] gm_spouses = ((Object[]) (first.getValue("CHILDREN")));
        Assert.assertEquals(2, gm_spouses.length);
        Object[] gm_spouse1_parents = ((Object[]) (getValue("CHILD")));
        Assert.assertEquals(2, gm_spouse1_parents.length);
        Record first_1_1 = ((Record) (gm_spouse1_parents[0]));
        Assert.assertEquals("mother", first_1_1.getValue("ROLE"));
        Assert.assertEquals("1-1", first_1_1.getValue("ID"));
        Assert.assertEquals("Anna", first_1_1.getValue("NAME"));
        Object[] gm_spouse1_parent1_first_husband = ((Object[]) (first_1_1.getValue("CHILDREN")));
        Assert.assertEquals(1, gm_spouse1_parent1_first_husband.length);
        Object[] gm_spouse1_parent1_children = ((Object[]) (getValue("CHILD")));
        Record first_1_1_1 = ((Record) (gm_spouse1_parent1_children[0]));
        Assert.assertEquals("daughter", first_1_1_1.getValue("ROLE"));
        Assert.assertEquals("1-1-1", first_1_1_1.getValue("ID"));
        Assert.assertEquals("Selina", first_1_1_1.getValue("NAME"));
        Record first_1_1_2 = ((Record) (gm_spouse1_parent1_children[1]));
        Assert.assertEquals("son", first_1_1_2.getValue("ROLE"));
        Assert.assertEquals("1-1-2", first_1_1_2.getValue("ID"));
        Assert.assertEquals("Hans", first_1_1_2.getValue("NAME"));
        Record first_1_2 = ((Record) (gm_spouse1_parents[1]));
        Assert.assertEquals("mother", first_1_2.getValue("ROLE"));
        Assert.assertEquals("1-2", first_1_2.getValue("ID"));
        Assert.assertEquals("Catrina", first_1_2.getValue("NAME"));
        Object[] gm_spouse2_parents = ((Object[]) (getValue("CHILD")));
        Assert.assertEquals(1, gm_spouse2_parents.length);
        Record second = reader.nextRecord();
        Record second_2_1_1 = ((Record) (((Object[]) (getValue("CHILD")))[0]));
        Assert.assertEquals("daughter", second_2_1_1.getValue("ROLE"));
        Assert.assertEquals("2-1-1", second_2_1_1.getValue("ID"));
        Assert.assertEquals("Selina3", second_2_1_1.getValue("NAME"));
    }

    @Test
    public void testDeeplyNestedArraysAndRecordsCoerceFalseDropTrue() throws IOException, MalformedRecordException {
        // test multiply nested arrays and records (recursion)
        InputStream is = new FileInputStream("src/test/resources/xml/people_complex2.xml");
        XMLRecordReader reader = new XMLRecordReader(is, getSchemaForComplexData2(), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(false, true);
        Assert.assertEquals("grandmother", first.getValue("ROLE"));
        Assert.assertEquals("1", first.getValue("ID"));
        Assert.assertEquals("Lisa", first.getValue("NAME"));
        Object[] gm_spouses = ((Object[]) (first.getValue("CHILDREN")));
        Assert.assertEquals(2, gm_spouses.length);
        Object[] gm_spouse1_parents = ((Object[]) (getValue("CHILD")));
        Assert.assertEquals(2, gm_spouse1_parents.length);
        Record first_1_1 = ((Record) (gm_spouse1_parents[0]));
        Assert.assertEquals("mother", first_1_1.getValue("ROLE"));
        Assert.assertEquals("1-1", first_1_1.getValue("ID"));
        Assert.assertEquals("Anna", first_1_1.getValue("NAME"));
        Record gm_spouse1_parent1_first_husband = ((Record) (first_1_1.getValue("CHILDREN")));
        Object[] gm_spouse1_parent1_children = ((Object[]) (gm_spouse1_parent1_first_husband.getValue("CHILD")));
        Record first_1_1_1 = ((Record) (gm_spouse1_parent1_children[0]));
        Assert.assertEquals("daughter", first_1_1_1.getValue("ROLE"));
        Assert.assertEquals("1-1-1", first_1_1_1.getValue("ID"));
        Assert.assertEquals("Selina", first_1_1_1.getValue("NAME"));
        Record first_1_1_2 = ((Record) (gm_spouse1_parent1_children[1]));
        Assert.assertEquals("son", first_1_1_2.getValue("ROLE"));
        Assert.assertEquals("1-1-2", first_1_1_2.getValue("ID"));
        Assert.assertEquals("Hans", first_1_1_2.getValue("NAME"));
        Record first_1_2 = ((Record) (gm_spouse1_parents[1]));
        Assert.assertEquals("mother", first_1_2.getValue("ROLE"));
        Assert.assertEquals("1-2", first_1_2.getValue("ID"));
        Assert.assertEquals("Catrina", first_1_2.getValue("NAME"));
        Record gm_spouse2_parents = ((Record) (getValue("CHILD")));
        Assert.assertEquals("1-3", gm_spouse2_parents.getValue("ID"));
        Record second = reader.nextRecord(false, true);
        Record second_2_1_1 = ((Record) (getValue("CHILD")));
        Assert.assertEquals("daughter", second_2_1_1.getValue("ROLE"));
        Assert.assertEquals("2-1-1", second_2_1_1.getValue("ID"));
        Assert.assertEquals("Selina3", second_2_1_1.getValue("NAME"));
    }

    @Test
    public void testDeeplyNestedArraysAndRecordsCoerceFalseDropFalse() throws IOException, MalformedRecordException {
        // test multiply nested arrays and records (recursion)
        InputStream is = new FileInputStream("src/test/resources/xml/people_complex2.xml");
        XMLRecordReader reader = new XMLRecordReader(is, new SimpleRecordSchema(Collections.emptyList()), true, null, "CONTENT", dateFormat, timeFormat, timestampFormat, Mockito.mock(ComponentLog.class));
        Record first = reader.nextRecord(false, false);
        Assert.assertEquals("1", first.getValue("ID"));
        Assert.assertEquals("Lisa", first.getValue("NAME"));
        Assert.assertEquals("grandmother", first.getValue("ROLE"));
        Object[] gm_arr = ((Object[]) (first.getValue("CHILDREN")));
        Assert.assertEquals(2, gm_arr.length);
        Record gm_hus1_arr_rec = ((Record) (gm_arr[0]));
        Assert.assertEquals("husband1", gm_hus1_arr_rec.getValue("SPOUSE"));
        Object[] gm_hus1_arr_rec_arr = ((Object[]) (gm_hus1_arr_rec.getValue("CHILD")));
        Assert.assertEquals(2, gm_hus1_arr_rec_arr.length);
        Record child1_1 = ((Record) (gm_hus1_arr_rec_arr[0]));
        Assert.assertEquals("1-1", child1_1.getValue("ID"));
        Assert.assertEquals("Anna", child1_1.getValue("NAME"));
        Assert.assertEquals("mother", child1_1.getValue("ROLE"));
        Record child1_1_rec = ((Record) (child1_1.getValue("CHILDREN")));
        Assert.assertEquals("first husband", child1_1_rec.getValue("ID"));
        Object[] child1_1_rec_arr = ((Object[]) (child1_1_rec.getValue("CHILD")));
        Assert.assertEquals(2, child1_1_rec_arr.length);
        Record child1_1_1 = ((Record) (child1_1_rec_arr[0]));
        Assert.assertEquals("1-1-1", child1_1_1.getValue("ID"));
        Assert.assertEquals("Selina", child1_1_1.getValue("NAME"));
        Assert.assertEquals("daughter", child1_1_1.getValue("ROLE"));
        Record child1_1_2 = ((Record) (child1_1_rec_arr[1]));
        Assert.assertEquals("1-1-2", child1_1_2.getValue("ID"));
        Assert.assertEquals("Hans", child1_1_2.getValue("NAME"));
        Assert.assertEquals("son", child1_1_2.getValue("ROLE"));
        Record child1_2 = ((Record) (gm_hus1_arr_rec_arr[1]));
        Assert.assertEquals("1-2", child1_2.getValue("ID"));
        Assert.assertEquals("Catrina", child1_2.getValue("NAME"));
        Assert.assertEquals("mother", child1_2.getValue("ROLE"));
        Record gm_hus2_arr_rec = ((Record) (gm_arr[1]));
        Assert.assertEquals("husband2", gm_hus2_arr_rec.getValue("SPOUSE"));
        Record child1_3 = ((Record) (gm_hus2_arr_rec.getValue("CHILD")));
        Assert.assertEquals("1-3", child1_3.getValue("ID"));
        Assert.assertEquals("Anna2", child1_3.getValue("NAME"));
        Assert.assertEquals("mother", child1_3.getValue("ROLE"));
        Assert.assertEquals(2, ((Object[]) (getValue("CHILD"))).length);
        Record second = reader.nextRecord(false, false);
        Assert.assertEquals("2-1-1", getValue("ID"));
    }
}

