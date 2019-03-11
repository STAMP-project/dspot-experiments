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
package org.apache.nifi.record.path;


import RecordFieldType.ARRAY;
import RecordFieldType.BYTE;
import RecordFieldType.CHOICE;
import RecordFieldType.DATE;
import RecordFieldType.INT;
import RecordFieldType.LONG;
import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.nifi.record.path.exception.RecordPathException;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.util.DataTypeUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestRecordPath {
    @Test
    public void testCompile() {
        System.out.println(RecordPath.compile("/person/name/last"));
        System.out.println(RecordPath.compile("/person[2]"));
        System.out.println(RecordPath.compile("//person[2]"));
        System.out.println(RecordPath.compile("/person/child[1]//sibling/name"));
        // contains is a 'filter function' so can be used as the predicate
        RecordPath.compile("/name[contains(., 'hello')]");
        // substring is not a filter function so cannot be used as a predicate
        try {
            RecordPath.compile("/name[substring(., 1, 2)]");
        } catch (final RecordPathException e) {
            // expected
        }
        // substring is not a filter function so can be used as *part* of a predicate but not as the entire predicate
        RecordPath.compile("/name[substring(., 1, 2) = 'e']");
    }

    @Test
    public void testChildField() {
        final Map<String, Object> accountValues = new HashMap<>();
        accountValues.put("id", 1);
        accountValues.put("balance", 123.45);
        final Record accountRecord = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("mainAccount", accountRecord);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals(48, RecordPath.compile("/id").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(record, RecordPath.compile("/id").evaluate(record).getSelectedFields().findFirst().get().getParentRecord().get());
        Assert.assertEquals("John Doe", RecordPath.compile("/name").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(record, RecordPath.compile("/name").evaluate(record).getSelectedFields().findFirst().get().getParentRecord().get());
        Assert.assertEquals(accountRecord, RecordPath.compile("/mainAccount").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(record, RecordPath.compile("/mainAccount").evaluate(record).getSelectedFields().findFirst().get().getParentRecord().get());
        Assert.assertEquals(1, RecordPath.compile("/mainAccount/id").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(accountRecord, RecordPath.compile("/mainAccount/id").evaluate(record).getSelectedFields().findFirst().get().getParentRecord().get());
        Assert.assertEquals(123.45, RecordPath.compile("/mainAccount/balance").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(accountRecord, RecordPath.compile("/mainAccount/id").evaluate(record).getSelectedFields().findFirst().get().getParentRecord().get());
    }

    @Test
    public void testRootRecord() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final FieldValue fieldValue = RecordPath.compile("/").evaluate(record).getSelectedFields().findFirst().get();
        Assert.assertEquals(Optional.empty(), fieldValue.getParent());
        Assert.assertEquals(record, fieldValue.getValue());
    }

    @Test
    public void testWildcardChild() {
        final Map<String, Object> accountValues = new HashMap<>();
        accountValues.put("id", 1);
        accountValues.put("balance", 123.45);
        final Record accountRecord = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("mainAccount", accountRecord);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final List<FieldValue> fieldValues = RecordPath.compile("/mainAccount/*").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(2, fieldValues.size());
        for (final FieldValue fieldValue : fieldValues) {
            Assert.assertEquals(accountRecord, fieldValue.getParentRecord().get());
        }
        Assert.assertEquals("id", fieldValues.get(0).getField().getFieldName());
        Assert.assertEquals(1, fieldValues.get(0).getValue());
        Assert.assertEquals("balance", fieldValues.get(1).getField().getFieldName());
        Assert.assertEquals(123.45, fieldValues.get(1).getValue());
        RecordPath.compile("/mainAccount/*[. > 100]").evaluate(record).getSelectedFields().forEach(( field) -> field.updateValue(122.44));
        Assert.assertEquals(1, accountValues.get("id"));
        Assert.assertEquals(122.44, accountValues.get("balance"));
    }

    @Test
    public void testWildcardWithArray() {
        final Map<String, Object> accountValues = new HashMap<>();
        accountValues.put("id", 1);
        accountValues.put("balance", 123.45);
        final Record accountRecord = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("accounts", new Object[]{ accountRecord });
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final List<FieldValue> fieldValues = RecordPath.compile("/*[0]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(1, fieldValues.size());
        final FieldValue fieldValue = fieldValues.get(0);
        Assert.assertEquals("accounts", fieldValue.getField().getFieldName());
        Assert.assertEquals(record, fieldValue.getParentRecord().get());
        Assert.assertEquals(accountRecord, fieldValue.getValue());
        final Map<String, Object> updatedAccountValues = new HashMap<>(accountValues);
        updatedAccountValues.put("balance", 122.44);
        final Record updatedAccountRecord = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), updatedAccountValues);
        RecordPath.compile("/*[0]").evaluate(record).getSelectedFields().forEach(( field) -> field.updateValue(updatedAccountRecord));
        final Object[] accountRecords = ((Object[]) (record.getValue("accounts")));
        Assert.assertEquals(1, accountRecords.length);
        final Record recordToVerify = ((Record) (accountRecords[0]));
        Assert.assertEquals(122.44, recordToVerify.getValue("balance"));
        Assert.assertEquals(48, record.getValue("id"));
        Assert.assertEquals("John Doe", record.getValue("name"));
    }

    @Test
    public void testDescendantField() {
        final Map<String, Object> accountValues = new HashMap<>();
        accountValues.put("id", 1);
        accountValues.put("balance", 123.45);
        final Record accountRecord = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("mainAccount", accountRecord);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final List<FieldValue> fieldValues = RecordPath.compile("//id").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(2, fieldValues.size());
        final FieldValue first = fieldValues.get(0);
        final FieldValue second = fieldValues.get(1);
        Assert.assertEquals(INT, first.getField().getDataType().getFieldType());
        Assert.assertEquals(INT, second.getField().getDataType().getFieldType());
        Assert.assertEquals(48, first.getValue());
        Assert.assertEquals(1, second.getValue());
    }

    @Test
    public void testParent() {
        final Map<String, Object> accountValues = new HashMap<>();
        accountValues.put("id", 1);
        accountValues.put("balance", 123.45);
        final Record accountRecord = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("mainAccount", accountRecord);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final List<FieldValue> fieldValues = RecordPath.compile("//id/..").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(2, fieldValues.size());
        final FieldValue first = fieldValues.get(0);
        final FieldValue second = fieldValues.get(1);
        Assert.assertEquals(RECORD, first.getField().getDataType().getFieldType());
        Assert.assertEquals(RECORD, second.getField().getDataType().getFieldType());
        Assert.assertEquals(record, first.getValue());
        Assert.assertEquals(accountRecord, second.getValue());
    }

    @Test
    public void testMapKey() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("city", "New York");
        attributes.put("state", "NY");
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("attributes", attributes);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final FieldValue fieldValue = RecordPath.compile("/attributes['city']").evaluate(record).getSelectedFields().findFirst().get();
        Assert.assertTrue(fieldValue.getField().getFieldName().equals("attributes"));
        Assert.assertEquals("New York", fieldValue.getValue());
        Assert.assertEquals(record, fieldValue.getParentRecord().get());
    }

    @Test
    public void testMapKeyReferencedWithCurrentField() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("city", "New York");
        attributes.put("state", "NY");
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("attributes", attributes);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final FieldValue fieldValue = RecordPath.compile("/attributes/.['city']").evaluate(record).getSelectedFields().findFirst().get();
        Assert.assertTrue(fieldValue.getField().getFieldName().equals("attributes"));
        Assert.assertEquals("New York", fieldValue.getValue());
        Assert.assertEquals(record, fieldValue.getParentRecord().get());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUpdateMap() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("city", "New York");
        attributes.put("state", "NY");
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("attributes", attributes);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        RecordPath.compile("/attributes['city']").evaluate(record).getSelectedFields().findFirst().get().updateValue("Boston");
        Assert.assertEquals("Boston", ((Map<String, Object>) (record.getValue("attributes"))).get("city"));
    }

    @Test
    public void testMapWildcard() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("city", "New York");
        attributes.put("state", "NY");
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("attributes", attributes);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final List<FieldValue> fieldValues = RecordPath.compile("/attributes[*]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertEquals("New York", fieldValues.get(0).getValue());
        Assert.assertEquals("NY", fieldValues.get(1).getValue());
        for (final FieldValue fieldValue : fieldValues) {
            Assert.assertEquals("attributes", fieldValue.getField().getFieldName());
            Assert.assertEquals(record, fieldValue.getParentRecord().get());
        }
        RecordPath.compile("/attributes[*]").evaluate(record).getSelectedFields().forEach(( field) -> field.updateValue("Unknown"));
        Assert.assertEquals("Unknown", attributes.get("city"));
        Assert.assertEquals("Unknown", attributes.get("state"));
        RecordPath.compile("/attributes[*][fieldName(.) = 'attributes']").evaluate(record).getSelectedFields().forEach(( field) -> field.updateValue("Unknown"));
        Assert.assertEquals("Unknown", attributes.get("city"));
        Assert.assertEquals("Unknown", attributes.get("state"));
    }

    @Test
    public void testMapMultiKey() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("city", "New York");
        attributes.put("state", "NY");
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("attributes", attributes);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final List<FieldValue> fieldValues = RecordPath.compile("/attributes['city', 'state']").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(2, fieldValues.size());
        Assert.assertEquals("New York", fieldValues.get(0).getValue());
        Assert.assertEquals("NY", fieldValues.get(1).getValue());
        for (final FieldValue fieldValue : fieldValues) {
            Assert.assertEquals("attributes", fieldValue.getField().getFieldName());
            Assert.assertEquals(record, fieldValue.getParentRecord().get());
        }
        RecordPath.compile("/attributes['city', 'state']").evaluate(record).getSelectedFields().forEach(( field) -> field.updateValue("Unknown"));
        Assert.assertEquals("Unknown", attributes.get("city"));
        Assert.assertEquals("Unknown", attributes.get("state"));
    }

    @Test
    public void testEscapedFieldName() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name,date", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name,date", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final FieldValue fieldValue = RecordPath.compile("/'name,date'").evaluate(record).getSelectedFields().findFirst().get();
        Assert.assertEquals("name,date", fieldValue.getField().getFieldName());
        Assert.assertEquals("John Doe", fieldValue.getValue());
        Assert.assertEquals(record, fieldValue.getParentRecord().get());
    }

    @Test
    public void testSingleArrayIndex() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("numbers", new Object[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final FieldValue fieldValue = RecordPath.compile("/numbers[3]").evaluate(record).getSelectedFields().findFirst().get();
        Assert.assertEquals("numbers", fieldValue.getField().getFieldName());
        Assert.assertEquals(3, fieldValue.getValue());
        Assert.assertEquals(record, fieldValue.getParentRecord().get());
    }

    @Test
    public void testSingleArrayRange() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("numbers", new Object[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final List<FieldValue> fieldValues = RecordPath.compile("/numbers[0..1]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        for (final FieldValue fieldValue : fieldValues) {
            Assert.assertEquals("numbers", fieldValue.getField().getFieldName());
            Assert.assertEquals(record, fieldValue.getParentRecord().get());
        }
        Assert.assertEquals(2, fieldValues.size());
        for (int i = 0; i < 1; i++) {
            Assert.assertEquals(i, fieldValues.get(0).getValue());
        }
    }

    @Test
    public void testMultiArrayIndex() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("numbers", new Object[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final List<FieldValue> fieldValues = RecordPath.compile("/numbers[3,6, -1, -2]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        int i = 0;
        final int[] expectedValues = new int[]{ 3, 6, 9, 8 };
        for (final FieldValue fieldValue : fieldValues) {
            Assert.assertEquals("numbers", fieldValue.getField().getFieldName());
            Assert.assertEquals(expectedValues[(i++)], fieldValue.getValue());
            Assert.assertEquals(record, fieldValue.getParentRecord().get());
        }
        RecordPath.compile("/numbers[3,6, -1, -2]").evaluate(record).getSelectedFields().forEach(( field) -> field.updateValue(99));
        Assert.assertArrayEquals(new Object[]{ 0, 1, 2, 99, 4, 5, 99, 7, 99, 99 }, ((Object[]) (values.get("numbers"))));
    }

    @Test
    public void testMultiArrayIndexWithRanges() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("numbers", new Object[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        List<FieldValue> fieldValues = RecordPath.compile("/numbers[0, 2, 4..7, 9]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        for (final FieldValue fieldValue : fieldValues) {
            Assert.assertEquals("numbers", fieldValue.getField().getFieldName());
            Assert.assertEquals(record, fieldValue.getParentRecord().get());
        }
        int[] expectedValues = new int[]{ 0, 2, 4, 5, 6, 7, 9 };
        Assert.assertEquals(expectedValues.length, fieldValues.size());
        for (int i = 0; i < (expectedValues.length); i++) {
            Assert.assertEquals(expectedValues[i], fieldValues.get(i).getValue());
        }
        fieldValues = RecordPath.compile("/numbers[0..-1]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        for (final FieldValue fieldValue : fieldValues) {
            Assert.assertEquals("numbers", fieldValue.getField().getFieldName());
            Assert.assertEquals(record, fieldValue.getParentRecord().get());
        }
        expectedValues = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertEquals(expectedValues.length, fieldValues.size());
        for (int i = 0; i < (expectedValues.length); i++) {
            Assert.assertEquals(expectedValues[i], fieldValues.get(i).getValue());
        }
        fieldValues = RecordPath.compile("/numbers[-1..-1]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        for (final FieldValue fieldValue : fieldValues) {
            Assert.assertEquals("numbers", fieldValue.getField().getFieldName());
            Assert.assertEquals(record, fieldValue.getParentRecord().get());
        }
        expectedValues = new int[]{ 9 };
        Assert.assertEquals(expectedValues.length, fieldValues.size());
        for (int i = 0; i < (expectedValues.length); i++) {
            Assert.assertEquals(expectedValues[i], fieldValues.get(i).getValue());
        }
        fieldValues = RecordPath.compile("/numbers[*]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        for (final FieldValue fieldValue : fieldValues) {
            Assert.assertEquals("numbers", fieldValue.getField().getFieldName());
            Assert.assertEquals(record, fieldValue.getParentRecord().get());
        }
        expectedValues = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        Assert.assertEquals(expectedValues.length, fieldValues.size());
        for (int i = 0; i < (expectedValues.length); i++) {
            Assert.assertEquals(expectedValues[i], fieldValues.get(i).getValue());
        }
        fieldValues = RecordPath.compile("/xx[1,2,3]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(0, fieldValues.size());
    }

    @Test
    public void testEqualsPredicate() {
        final Map<String, Object> accountValues = new HashMap<>();
        accountValues.put("id", 1);
        accountValues.put("balance", 123.45);
        final Record accountRecord = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("mainAccount", accountRecord);
        values.put("numbers", new Object[]{ 1, 2, 3, 4, 4, 4, 5 });
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        List<FieldValue> fieldValues = RecordPath.compile("/numbers[0..-1][. = 4]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(3, fieldValues.size());
        for (final FieldValue fieldValue : fieldValues) {
            final String fieldName = fieldValue.getField().getFieldName();
            Assert.assertEquals("numbers", fieldName);
            Assert.assertEquals(INT, fieldValue.getField().getDataType().getFieldType());
            Assert.assertEquals(4, fieldValue.getValue());
            Assert.assertEquals(record, fieldValue.getParentRecord().get());
        }
        fieldValues = RecordPath.compile("//id[. = 48]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(1, fieldValues.size());
        final FieldValue fieldValue = fieldValues.get(0);
        Assert.assertEquals("id", fieldValue.getField().getFieldName());
        Assert.assertEquals(INT.getDataType(), fieldValue.getField().getDataType());
        Assert.assertEquals(48, fieldValue.getValue());
        Assert.assertEquals(record, fieldValue.getParentRecord().get());
    }

    @Test
    public void testRelativePath() {
        final Map<String, Object> accountValues = new HashMap<>();
        accountValues.put("id", 1);
        accountValues.put("balance", 123.45);
        final Record accountRecord = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("mainAccount", accountRecord);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final List<FieldValue> fieldValues = RecordPath.compile("/mainAccount/././balance/.").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(1, fieldValues.size());
        final FieldValue fieldValue = fieldValues.get(0);
        Assert.assertEquals(accountRecord, fieldValue.getParentRecord().get());
        Assert.assertEquals(123.45, fieldValue.getValue());
        Assert.assertEquals("balance", fieldValue.getField().getFieldName());
        RecordPath.compile("/mainAccount/././balance/.").evaluate(record).getSelectedFields().forEach(( field) -> field.updateValue(123.44));
        Assert.assertEquals(123.44, accountValues.get("balance"));
    }

    @Test
    public void testCompareToLiteral() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("numbers", new Object[]{ 0, 1, 2 });
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        List<FieldValue> fieldValues = RecordPath.compile("/id[. > 42]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(1, fieldValues.size());
        fieldValues = RecordPath.compile("/id[. < 42]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(0, fieldValues.size());
    }

    @Test
    public void testCompareToAbsolute() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("numbers", new Object[]{ 0, 1, 2 });
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        List<FieldValue> fieldValues = RecordPath.compile("/numbers[0..-1][. < /id]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(3, fieldValues.size());
        fieldValues = RecordPath.compile("/id[. > /numbers[-1]]").evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(1, fieldValues.size());
    }

    @Test
    public void testCompareWithEmbeddedPaths() {
        final Map<String, Object> accountValues1 = new HashMap<>();
        accountValues1.put("id", 1);
        accountValues1.put("balance", 10000.0);
        final Record accountRecord1 = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues1);
        final Map<String, Object> accountValues2 = new HashMap<>();
        accountValues2.put("id", 2);
        accountValues2.put("balance", 48.02);
        final Record accountRecord2 = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues2);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("accounts", new Object[]{ accountRecord1, accountRecord2 });
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final RecordPath recordPath = RecordPath.compile("/accounts[0..-1][./balance > 100]");
        List<FieldValue> fieldValues = recordPath.evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(1, fieldValues.size());
        final FieldValue fieldValue = fieldValues.get(0);
        Assert.assertEquals("accounts", fieldValue.getField().getFieldName());
        Assert.assertEquals(0, getArrayIndex());
        Assert.assertEquals(record, fieldValue.getParentRecord().get());
        Assert.assertEquals(accountRecord1, fieldValue.getValue());
    }

    @Test
    public void testPredicateInMiddleOfPath() {
        final Map<String, Object> accountValues1 = new HashMap<>();
        accountValues1.put("id", 1);
        accountValues1.put("balance", 10000.0);
        final Record accountRecord1 = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues1);
        final Map<String, Object> accountValues2 = new HashMap<>();
        accountValues2.put("id", 2);
        accountValues2.put("balance", 48.02);
        final Record accountRecord2 = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues2);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("accounts", new Object[]{ accountRecord1, accountRecord2 });
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final RecordPath recordPath = RecordPath.compile("/accounts[0..-1][./balance > 100]/id");
        List<FieldValue> fieldValues = recordPath.evaluate(record).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(1, fieldValues.size());
        final FieldValue fieldValue = fieldValues.get(0);
        Assert.assertEquals("id", fieldValue.getField().getFieldName());
        Assert.assertEquals(accountRecord1, fieldValue.getParentRecord().get());
        Assert.assertEquals(1, fieldValue.getValue());
    }

    @Test
    public void testUpdateValueOnMatchingFields() {
        final Map<String, Object> accountValues1 = new HashMap<>();
        accountValues1.put("id", 1);
        accountValues1.put("balance", 10000.0);
        final Record accountRecord1 = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues1);
        final Map<String, Object> accountValues2 = new HashMap<>();
        accountValues2.put("id", 2);
        accountValues2.put("balance", 48.02);
        final Record accountRecord2 = new org.apache.nifi.serialization.record.MapRecord(getAccountSchema(), accountValues2);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        values.put("accounts", new Object[]{ accountRecord1, accountRecord2 });
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final RecordPath recordPath = RecordPath.compile("/accounts[0..-1][./balance > 100]/id");
        recordPath.evaluate(record).getSelectedFields().findFirst().get().updateValue(100);
        Assert.assertEquals(48, record.getValue("id"));
        Assert.assertEquals(100, accountRecord1.getValue("id"));
        Assert.assertEquals(2, accountRecord2.getValue("id"));
    }

    @Test
    public void testPredicateDoesNotIncludeFieldsThatDontHaveRelativePath() {
        final List<RecordField> addressFields = new ArrayList<>();
        addressFields.add(new RecordField("city", STRING.getDataType()));
        addressFields.add(new RecordField("state", STRING.getDataType()));
        addressFields.add(new RecordField("zip", STRING.getDataType()));
        final RecordSchema addressSchema = new org.apache.nifi.serialization.SimpleRecordSchema(addressFields);
        final List<RecordField> detailsFields = new ArrayList<>();
        detailsFields.add(new RecordField("position", STRING.getDataType()));
        detailsFields.add(new RecordField("managerName", STRING.getDataType()));
        final RecordSchema detailsSchema = new org.apache.nifi.serialization.SimpleRecordSchema(detailsFields);
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("name", STRING.getDataType()));
        fields.add(new RecordField("address", RECORD.getRecordDataType(addressSchema)));
        fields.add(new RecordField("details", RECORD.getRecordDataType(detailsSchema)));
        final RecordSchema recordSchema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(recordSchema, new HashMap());
        record.setValue("name", "John Doe");
        final Record addressRecord = new org.apache.nifi.serialization.record.MapRecord(addressSchema, new HashMap());
        addressRecord.setValue("city", "San Francisco");
        addressRecord.setValue("state", "CA");
        addressRecord.setValue("zip", "12345");
        record.setValue("address", addressRecord);
        final Record detailsRecord = new org.apache.nifi.serialization.record.MapRecord(detailsSchema, new HashMap());
        detailsRecord.setValue("position", "Developer");
        detailsRecord.setValue("managerName", "Jane Doe");
        record.setValue("details", detailsRecord);
        final RecordPath recordPath = RecordPath.compile("/*[./state != 'NY']");
        final RecordPathResult result = recordPath.evaluate(record);
        final List<FieldValue> fieldValues = result.getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(1, fieldValues.size());
        final FieldValue fieldValue = fieldValues.get(0);
        Assert.assertEquals("address", fieldValue.getField().getFieldName());
        Assert.assertEquals("12345", RecordPath.compile("/*[./state != 'NY']/zip").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testPredicateWithAbsolutePath() {
        final List<RecordField> addressFields = new ArrayList<>();
        addressFields.add(new RecordField("city", STRING.getDataType()));
        addressFields.add(new RecordField("state", STRING.getDataType()));
        addressFields.add(new RecordField("zip", STRING.getDataType()));
        final RecordSchema addressSchema = new org.apache.nifi.serialization.SimpleRecordSchema(addressFields);
        final List<RecordField> detailsFields = new ArrayList<>();
        detailsFields.add(new RecordField("position", STRING.getDataType()));
        detailsFields.add(new RecordField("preferredState", STRING.getDataType()));
        final RecordSchema detailsSchema = new org.apache.nifi.serialization.SimpleRecordSchema(detailsFields);
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("name", STRING.getDataType()));
        fields.add(new RecordField("address1", RECORD.getRecordDataType(addressSchema)));
        fields.add(new RecordField("address2", RECORD.getRecordDataType(addressSchema)));
        fields.add(new RecordField("details", RECORD.getRecordDataType(detailsSchema)));
        final RecordSchema recordSchema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(recordSchema, new HashMap());
        record.setValue("name", "John Doe");
        final Record address1Record = new org.apache.nifi.serialization.record.MapRecord(addressSchema, new HashMap());
        address1Record.setValue("city", "San Francisco");
        address1Record.setValue("state", "CA");
        address1Record.setValue("zip", "12345");
        record.setValue("address1", address1Record);
        final Record address2Record = new org.apache.nifi.serialization.record.MapRecord(addressSchema, new HashMap());
        address2Record.setValue("city", "New York");
        address2Record.setValue("state", "NY");
        address2Record.setValue("zip", "01234");
        record.setValue("address2", address2Record);
        final Record detailsRecord = new org.apache.nifi.serialization.record.MapRecord(detailsSchema, new HashMap());
        detailsRecord.setValue("position", "Developer");
        detailsRecord.setValue("preferredState", "NY");
        record.setValue("details", detailsRecord);
        final RecordPath recordPath = RecordPath.compile("/*[./state = /details/preferredState]");
        final RecordPathResult result = recordPath.evaluate(record);
        final List<FieldValue> fieldValues = result.getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(1, fieldValues.size());
        final FieldValue fieldValue = fieldValues.get(0);
        Assert.assertEquals("address2", fieldValue.getField().getFieldName());
    }

    @Test
    public void testRelativePathOnly() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final FieldValue recordFieldValue = new StandardFieldValue(record, new RecordField("record", RECORD.getDataType()), null);
        final List<FieldValue> fieldValues = RecordPath.compile("./name").evaluate(record, recordFieldValue).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(1, fieldValues.size());
        final FieldValue fieldValue = fieldValues.get(0);
        Assert.assertEquals("John Doe", fieldValue.getValue());
        Assert.assertEquals(record, fieldValue.getParentRecord().get());
        Assert.assertEquals("name", fieldValue.getField().getFieldName());
    }

    @Test
    public void testRelativePathAgainstNonRecordField() {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final FieldValue recordFieldValue = new StandardFieldValue(record, new RecordField("root", RECORD.getRecordDataType(record.getSchema())), null);
        final FieldValue nameFieldValue = new StandardFieldValue("John Doe", new RecordField("name", STRING.getDataType()), recordFieldValue);
        final List<FieldValue> fieldValues = RecordPath.compile(".").evaluate(record, nameFieldValue).getSelectedFields().collect(Collectors.toList());
        Assert.assertEquals(1, fieldValues.size());
        final FieldValue fieldValue = fieldValues.get(0);
        Assert.assertEquals("John Doe", fieldValue.getValue());
        Assert.assertEquals(record, fieldValue.getParentRecord().get());
        Assert.assertEquals("name", fieldValue.getField().getFieldName());
        fieldValue.updateValue("Jane Doe");
        Assert.assertEquals("Jane Doe", record.getValue("name"));
    }

    @Test
    public void testSubstringFunction() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final FieldValue fieldValue = RecordPath.compile("substring(/name, 0, 4)").evaluate(record).getSelectedFields().findFirst().get();
        Assert.assertEquals("John", fieldValue.getValue());
        Assert.assertEquals("John", RecordPath.compile("substring(/name, 0, -5)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("", RecordPath.compile("substring(/name, 1000, 1005)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("", RecordPath.compile("substring(/name, 4, 3)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("substring(/name, 0, 10000)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("", RecordPath.compile("substring(/name, -50, -1)").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testSubstringBeforeFunction() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("John", RecordPath.compile("substringBefore(/name, ' ')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("substringBefore(/name, 'XYZ')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("substringBefore(/name, '')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John D", RecordPath.compile("substringBeforeLast(/name, 'o')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("substringBeforeLast(/name, 'XYZ')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("substringBeforeLast(/name, '')").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testSubstringAfterFunction() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("hn Doe", RecordPath.compile("substringAfter(/name, 'o')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("substringAfter(/name, 'XYZ')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("substringAfter(/name, '')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("n Doe", RecordPath.compile("substringAfter(/name, 'oh')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("e", RecordPath.compile("substringAfterLast(/name, 'o')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("substringAfterLast(/name, 'XYZ')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("substringAfterLast(/name, '')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("n Doe", RecordPath.compile("substringAfterLast(/name, 'oh')").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testContains() {
        final Record record = createSimpleRecord();
        Assert.assertEquals("John Doe", RecordPath.compile("/name[contains(., 'o')]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(0L, RecordPath.compile("/name[contains(., 'x')]").evaluate(record).getSelectedFields().count());
        record.setValue("name", "John Doe 48");
        Assert.assertEquals("John Doe 48", RecordPath.compile("/name[contains(., /id)]").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testStartsWith() {
        final Record record = createSimpleRecord();
        Assert.assertEquals("John Doe", RecordPath.compile("/name[startsWith(., 'J')]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(0L, RecordPath.compile("/name[startsWith(., 'x')]").evaluate(record).getSelectedFields().count());
        Assert.assertEquals("John Doe", RecordPath.compile("/name[startsWith(., '')]").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testEndsWith() {
        final Record record = createSimpleRecord();
        Assert.assertEquals("John Doe", RecordPath.compile("/name[endsWith(., 'e')]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(0L, RecordPath.compile("/name[endsWith(., 'x')]").evaluate(record).getSelectedFields().count());
        Assert.assertEquals("John Doe", RecordPath.compile("/name[endsWith(., '')]").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testIsEmpty() {
        final Record record = createSimpleRecord();
        Assert.assertEquals("John Doe", RecordPath.compile("/name[isEmpty(../missing)]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("/name[isEmpty(/missing)]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(0L, RecordPath.compile("/name[isEmpty(../id)]").evaluate(record).getSelectedFields().count());
        record.setValue("missing", "   ");
        Assert.assertEquals(0L, RecordPath.compile("/name[isEmpty(/missing)]").evaluate(record).getSelectedFields().count());
    }

    @Test
    public void testIsBlank() {
        final Record record = createSimpleRecord();
        Assert.assertEquals("John Doe", RecordPath.compile("/name[isBlank(../missing)]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        record.setValue("missing", "   ");
        Assert.assertEquals("John Doe", RecordPath.compile("/name[isBlank(../missing)]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("/name[isBlank(/missing)]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(0L, RecordPath.compile("/name[isBlank(../id)]").evaluate(record).getSelectedFields().count());
    }

    @Test
    public void testContainsRegex() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("John Doe", RecordPath.compile("/name[containsRegex(., 'o')]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("/name[containsRegex(., '[xo]')]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(0L, RecordPath.compile("/name[containsRegex(., 'x')]").evaluate(record).getSelectedFields().count());
    }

    @Test
    public void testNot() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("John Doe", RecordPath.compile("/name[not(contains(., 'x'))]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(0L, RecordPath.compile("/name[not(. = 'John Doe')]").evaluate(record).getSelectedFields().count());
        Assert.assertEquals("John Doe", RecordPath.compile("/name[not(. = 'Jane Doe')]").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testChainingFunctions() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("John Doe", RecordPath.compile("/name[contains(substringAfter(., 'o'), 'h')]").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testMatchesRegex() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals(0L, RecordPath.compile("/name[matchesRegex(., 'John D')]").evaluate(record).getSelectedFields().count());
        Assert.assertEquals("John Doe", RecordPath.compile("/name[matchesRegex(., '[John Doe]{8}')]").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testReplace() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("John Doe", RecordPath.compile("/name[replace(../id, 48, 18) = 18]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(0L, RecordPath.compile("/name[replace(../id, 48, 18) = 48]").evaluate(record).getSelectedFields().count());
        Assert.assertEquals("Jane Doe", RecordPath.compile("replace(/name, 'ohn', 'ane')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("replace(/name, 'ohnny', 'ane')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John 48", RecordPath.compile("replace(/name, 'Doe', /id)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("23", RecordPath.compile("replace(/id, 48, 23)").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testReplaceRegex() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("ohn oe", RecordPath.compile("replaceRegex(/name, '[JD]', '')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("replaceRegex(/name, 'ohnny', 'ane')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("11", RecordPath.compile("replaceRegex(/id, '[0-9]', 1)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("Jxohn Dxoe", RecordPath.compile("replaceRegex(/name, '([JD])', '$1x')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("Jxohn Dxoe", RecordPath.compile("replaceRegex(/name, '(?<hello>[JD])', '${hello}x')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("48ohn 48oe", RecordPath.compile("replaceRegex(/name, '(?<hello>[JD])', /id)").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testReplaceRegexEscapedCharacters() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        // Special character cases
        values.put("name", "John Doe");
        Assert.assertEquals("Replacing whitespace to new line", "John\nDoe", RecordPath.compile("replaceRegex(/name, \'[\\s]\', \'\\n\')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        values.put("name", "John\nDoe");
        Assert.assertEquals("Replacing new line to whitespace", "John Doe", RecordPath.compile("replaceRegex(/name, \'\\n\', \' \')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        values.put("name", "John Doe");
        Assert.assertEquals("Replacing whitespace to tab", "John\tDoe", RecordPath.compile("replaceRegex(/name, \'[\\s]\', \'\\t\')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        values.put("name", "John\tDoe");
        Assert.assertEquals("Replacing tab to whitespace", "John Doe", RecordPath.compile("replaceRegex(/name, \'\\t\', \' \')").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testReplaceRegexEscapedQuotes() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        // Quotes
        // NOTE: At Java code, a single back-slash needs to be escaped with another-back slash, but needn't to do so at NiFi UI.
        // The test record path is equivalent to replaceRegex(/name, '\'', '"')
        values.put("name", "'John' 'Doe'");
        Assert.assertEquals("Replacing quote to double-quote", "\"John\" \"Doe\"", RecordPath.compile("replaceRegex(/name, \'\\\'\', \'\"\')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        values.put("name", "\"John\" \"Doe\"");
        Assert.assertEquals("Replacing double-quote to single-quote", "'John' 'Doe'", RecordPath.compile("replaceRegex(/name, \'\"\', \'\\\'\')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        values.put("name", "'John' 'Doe'");
        Assert.assertEquals("Replacing quote to double-quote, the function arguments are wrapped by double-quote", "\"John\" \"Doe\"", RecordPath.compile("replaceRegex(/name, \"\'\", \"\\\"\")").evaluate(record).getSelectedFields().findFirst().get().getValue());
        values.put("name", "\"John\" \"Doe\"");
        Assert.assertEquals("Replacing double-quote to single-quote, the function arguments are wrapped by double-quote", "'John' 'Doe'", RecordPath.compile("replaceRegex(/name, \"\\\"\", \"\'\")").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testReplaceRegexEscapedBackSlashes() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        // Back-slash
        // NOTE: At Java code, a single back-slash needs to be escaped with another-back slash, but needn't to do so at NiFi UI.
        // The test record path is equivalent to replaceRegex(/name, '\\', '/')
        values.put("name", "John\\Doe");
        Assert.assertEquals("Replacing a back-slash to forward-slash", "John/Doe", RecordPath.compile("replaceRegex(/name, \'\\\\\', \'/\')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        values.put("name", "John/Doe");
        Assert.assertEquals("Replacing a forward-slash to back-slash", "John\\Doe", RecordPath.compile("replaceRegex(/name, \'/\', \'\\\\\')").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testReplaceRegexEscapedBrackets() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        // Brackets
        values.put("name", "J[o]hn Do[e]");
        Assert.assertEquals("Square brackets can be escaped with back-slash", "J(o)hn Do(e)", RecordPath.compile("replaceRegex(replaceRegex(/name, \'\\[\', \'(\'), \'\\]\', \')\')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        values.put("name", "J(o)hn Do(e)");
        Assert.assertEquals("Brackets can be escaped with back-slash", "J[o]hn Do[e]", RecordPath.compile("replaceRegex(replaceRegex(/name, \'\\(\', \'[\'), \'\\)\', \']\')").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testReplaceNull() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        fields.add(new RecordField("missing", LONG.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals(48, RecordPath.compile("replaceNull(/missing, /id)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(14, RecordPath.compile("replaceNull(/missing, 14)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(48, RecordPath.compile("replaceNull(/id, 14)").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testConcat() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("fullName", INT.getDataType()));
        fields.add(new RecordField("lastName", STRING.getDataType()));
        fields.add(new RecordField("firstName", LONG.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("lastName", "Doe");
        values.put("firstName", "John");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("John Doe: 48", RecordPath.compile("concat(/firstName, ' ', /lastName, ': ', 48)").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testFieldName() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("name", RecordPath.compile("fieldName(/name)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("name", RecordPath.compile("fieldName(/*)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("//*[startsWith(fieldName(.), 'na')]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("name", RecordPath.compile("fieldName(//*[startsWith(fieldName(.), 'na')])").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("John Doe", RecordPath.compile("//name[not(startsWith(fieldName(.), 'xyz'))]").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(0L, RecordPath.compile("//name[not(startsWith(fieldName(.), 'n'))]").evaluate(record).getSelectedFields().count());
    }

    @Test
    public void testToDateFromString() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("date", DATE.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("date", "2017-10-20T11:00:00Z");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertTrue(((RecordPath.compile("toDate(/date, \"yyyy-MM-dd\'T\'HH:mm:ss\'Z\'\")").evaluate(record).getSelectedFields().findFirst().get().getValue()) instanceof Date));
    }

    @Test
    public void testToDateFromLong() throws ParseException {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("date", LONG.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final DateFormat dateFormat = DataTypeUtils.getDateFormat("yyyy-MM-dd");
        final long dateValue = dateFormat.parse("2017-10-20T11:00:00Z").getTime();
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("date", dateValue);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        // since the field is a long it shouldn't do the conversion and should return the value unchanged
        Assert.assertTrue(((RecordPath.compile("toDate(/date, \"yyyy-MM-dd\'T\'HH:mm:ss\'Z\'\")").evaluate(record).getSelectedFields().findFirst().get().getValue()) instanceof Long));
    }

    @Test
    public void testToDateFromNonDateString() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", DATE.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        // since the field is a string it shouldn't do the conversion and should return the value unchanged
        final FieldValue fieldValue = RecordPath.compile("toDate(/name, \"yyyy-MM-dd\'T\'HH:mm:ss\'Z\'\")").evaluate(record).getSelectedFields().findFirst().get();
        Assert.assertEquals("John Doe", fieldValue.getValue());
    }

    @Test
    public void testFormatDateFromString() throws ParseException {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("date", DATE.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("date", "2017-10-20T11:00:00Z");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        final FieldValue fieldValue = RecordPath.compile("format( toDate(/date, \"yyyy-MM-dd\'T\'HH:mm:ss\'Z\'\"), \'yyyy-MM-dd\' )").evaluate(record).getSelectedFields().findFirst().get();
        Assert.assertEquals("2017-10-20", fieldValue.getValue());
        final FieldValue fieldValueUnchanged = RecordPath.compile("format( toDate(/date, \"yyyy-MM-dd\'T\'HH:mm:ss\'Z\'\"), \'INVALID\' )").evaluate(record).getSelectedFields().findFirst().get();
        Assert.assertEquals(DataTypeUtils.getDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse("2017-10-20T11:00:00Z"), fieldValueUnchanged.getValue());
    }

    @Test
    public void testFormatDateFromLong() throws ParseException {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("date", LONG.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final DateFormat dateFormat = DataTypeUtils.getDateFormat("yyyy-MM-dd");
        final long dateValue = dateFormat.parse("2017-10-20").getTime();
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("date", dateValue);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("2017-10-20", RecordPath.compile("format(/date, 'yyyy-MM-dd' )").evaluate(record).getSelectedFields().findFirst().get().getValue());
        final FieldValue fieldValueUnchanged = RecordPath.compile("format(/date, 'INVALID' )").evaluate(record).getSelectedFields().findFirst().get();
        Assert.assertEquals(dateValue, fieldValueUnchanged.getValue());
    }

    @Test
    public void testFormatDateFromDate() throws ParseException {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("date", DATE.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final DateFormat dateFormat = DataTypeUtils.getDateFormat("yyyy-MM-dd");
        final java.util.Date utilDate = dateFormat.parse("2017-10-20");
        final Date dateValue = new Date(utilDate.getTime());
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("date", dateValue);
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("2017-10-20", RecordPath.compile("format(/date, 'yyyy-MM-dd')").evaluate(record).getSelectedFields().findFirst().get().getValue());
        final FieldValue fieldValueUnchanged = RecordPath.compile("format(/date, 'INVALID')").evaluate(record).getSelectedFields().findFirst().get();
        Assert.assertEquals(dateValue, fieldValueUnchanged.getValue());
    }

    @Test
    public void testFormatDateWhenNotDate() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("name", "John Doe");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("John Doe", RecordPath.compile("format(/name, 'yyyy-MM')").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test
    public void testToString() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("bytes", CHOICE.getChoiceDataType(ARRAY.getArrayDataType(BYTE.getDataType()))));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("bytes", "Hello World!".getBytes(StandardCharsets.UTF_16));
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("Hello World!", RecordPath.compile("toString(/bytes, \"UTF-16\")").evaluate(record).getSelectedFields().findFirst().get().getValue());
    }

    @Test(expected = IllegalCharsetNameException.class)
    public void testToStringBadCharset() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("bytes", CHOICE.getChoiceDataType(ARRAY.getArrayDataType(BYTE.getDataType()))));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("bytes", "Hello World!".getBytes(StandardCharsets.UTF_16));
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        RecordPath.compile("toString(/bytes, \"NOT A REAL CHARSET\")").evaluate(record).getSelectedFields().findFirst().get().getValue();
    }

    @Test
    public void testToBytes() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("s", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("s", "Hello World!");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertArrayEquals("Hello World!".getBytes(StandardCharsets.UTF_16LE), ((byte[]) (RecordPath.compile("toBytes(/s, \"UTF-16LE\")").evaluate(record).getSelectedFields().findFirst().get().getValue())));
    }

    @Test(expected = IllegalCharsetNameException.class)
    public void testToBytesBadCharset() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("s", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> values = new HashMap<>();
        values.put("id", 48);
        values.put("s", "Hello World!");
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        RecordPath.compile("toBytes(/s, \"NOT A REAL CHARSET\")").evaluate(record).getSelectedFields().findFirst().get().getValue();
    }

    @Test
    public void testBase64Encode() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("firstName", STRING.getDataType()));
        fields.add(new RecordField("lastName", STRING.getDataType()));
        fields.add(new RecordField("b", ARRAY.getArrayDataType(BYTE.getDataType())));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final List<Object> expectedValues = Arrays.asList(Base64.getEncoder().encodeToString("John".getBytes(StandardCharsets.UTF_8)), Base64.getEncoder().encodeToString("Doe".getBytes(StandardCharsets.UTF_8)), Base64.getEncoder().encode("xyz".getBytes(StandardCharsets.UTF_8)));
        final Map<String, Object> values = new HashMap<>();
        values.put("firstName", "John");
        values.put("lastName", "Doe");
        values.put("b", "xyz".getBytes(StandardCharsets.UTF_8));
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals(Base64.getEncoder().encodeToString("John".getBytes(StandardCharsets.UTF_8)), RecordPath.compile("base64Encode(/firstName)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals(Base64.getEncoder().encodeToString("Doe".getBytes(StandardCharsets.UTF_8)), RecordPath.compile("base64Encode(/lastName)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertTrue(Arrays.equals(Base64.getEncoder().encode("xyz".getBytes(StandardCharsets.UTF_8)), ((byte[]) (RecordPath.compile("base64Encode(/b)").evaluate(record).getSelectedFields().findFirst().get().getValue()))));
        List<Object> actualValues = RecordPath.compile("base64Encode(/*)").evaluate(record).getSelectedFields().map(FieldValue::getValue).collect(Collectors.toList());
        IntStream.range(0, 3).forEach(( i) -> {
            Object expectedObject = expectedValues.get(i);
            Object actualObject = actualValues.get(i);
            if (actualObject instanceof String) {
                Assert.assertEquals(expectedObject, actualObject);
            } else
                if (actualObject instanceof byte[]) {
                    Assert.assertTrue(Arrays.equals(((byte[]) (expectedObject)), ((byte[]) (actualObject))));
                }

        });
    }

    @Test
    public void testBase64Decode() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("firstName", STRING.getDataType()));
        fields.add(new RecordField("lastName", STRING.getDataType()));
        fields.add(new RecordField("b", ARRAY.getArrayDataType(BYTE.getDataType())));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final List<Object> expectedValues = Arrays.asList("John", "Doe", "xyz".getBytes(StandardCharsets.UTF_8));
        final Map<String, Object> values = new HashMap<>();
        values.put("firstName", Base64.getEncoder().encodeToString("John".getBytes(StandardCharsets.UTF_8)));
        values.put("lastName", Base64.getEncoder().encodeToString("Doe".getBytes(StandardCharsets.UTF_8)));
        values.put("b", Base64.getEncoder().encode("xyz".getBytes(StandardCharsets.UTF_8)));
        final Record record = new org.apache.nifi.serialization.record.MapRecord(schema, values);
        Assert.assertEquals("John", RecordPath.compile("base64Decode(/firstName)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertEquals("Doe", RecordPath.compile("base64Decode(/lastName)").evaluate(record).getSelectedFields().findFirst().get().getValue());
        Assert.assertTrue(Arrays.equals("xyz".getBytes(StandardCharsets.UTF_8), ((byte[]) (RecordPath.compile("base64Decode(/b)").evaluate(record).getSelectedFields().findFirst().get().getValue()))));
        List<Object> actualValues = RecordPath.compile("base64Decode(/*)").evaluate(record).getSelectedFields().map(FieldValue::getValue).collect(Collectors.toList());
        IntStream.range(0, 3).forEach(( i) -> {
            Object expectedObject = expectedValues.get(i);
            Object actualObject = actualValues.get(i);
            if (actualObject instanceof String) {
                Assert.assertEquals(expectedObject, actualObject);
            } else
                if (actualObject instanceof byte[]) {
                    Assert.assertTrue(Arrays.equals(((byte[]) (expectedObject)), ((byte[]) (actualObject))));
                }

        });
    }
}

