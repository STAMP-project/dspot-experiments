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
package org.apache.nifi.json;


import RecordFieldType.ARRAY;
import RecordFieldType.DATE;
import RecordFieldType.DOUBLE;
import RecordFieldType.INT;
import RecordFieldType.LONG;
import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.avro.Schema;
import org.apache.nifi.avro.AvroTypeUtil;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordFieldType;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.type.ChoiceDataType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestJsonTreeRowRecordReader {
    private final String dateFormat = DATE.getDefaultFormat();

    private final String timeFormat = TIME.getDefaultFormat();

    private final String timestampFormat = TIMESTAMP.getDefaultFormat();

    @Test
    public void testChoiceOfRecordTypes() throws IOException, MalformedRecordException {
        final Schema avroSchema = new Schema.Parser().parse(new File("src/test/resources/json/record-choice.avsc"));
        final RecordSchema recordSchema = AvroTypeUtil.createSchema(avroSchema);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/elements-for-record-choice.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), recordSchema, dateFormat, timeFormat, timestampFormat)) {
            // evaluate first record
            final Record firstRecord = reader.nextRecord();
            Assert.assertNotNull(firstRecord);
            final RecordSchema firstOuterSchema = firstRecord.getSchema();
            Assert.assertEquals(Arrays.asList("id", "child"), firstOuterSchema.getFieldNames());
            Assert.assertEquals("1234", firstRecord.getValue("id"));
            // record should have a schema that indicates that the 'child' is a CHOICE of 2 different record types
            Assert.assertTrue(((firstOuterSchema.getDataType("child").get().getFieldType()) == (RecordFieldType.CHOICE)));
            final List<DataType> firstSubTypes = getPossibleSubTypes();
            Assert.assertEquals(2, firstSubTypes.size());
            Assert.assertEquals(2L, firstSubTypes.stream().filter(( type) -> (type.getFieldType()) == RecordFieldType.RECORD).count());
            // child record should have a schema with "id" as the only field
            final Object childObject = firstRecord.getValue("child");
            Assert.assertTrue((childObject instanceof Record));
            final Record firstChildRecord = ((Record) (childObject));
            final RecordSchema firstChildSchema = firstChildRecord.getSchema();
            Assert.assertEquals(Arrays.asList("id"), firstChildSchema.getFieldNames());
            // evaluate second record
            final Record secondRecord = reader.nextRecord();
            Assert.assertNotNull(secondRecord);
            final RecordSchema secondOuterSchema = secondRecord.getSchema();
            Assert.assertEquals(Arrays.asList("id", "child"), secondOuterSchema.getFieldNames());
            Assert.assertEquals("1234", secondRecord.getValue("id"));
            // record should have a schema that indicates that the 'child' is a CHOICE of 2 different record types
            Assert.assertTrue(((secondOuterSchema.getDataType("child").get().getFieldType()) == (RecordFieldType.CHOICE)));
            final List<DataType> secondSubTypes = getPossibleSubTypes();
            Assert.assertEquals(2, secondSubTypes.size());
            Assert.assertEquals(2L, secondSubTypes.stream().filter(( type) -> (type.getFieldType()) == RecordFieldType.RECORD).count());
            // child record should have a schema with "name" as the only field
            final Object secondChildObject = secondRecord.getValue("child");
            Assert.assertTrue((secondChildObject instanceof Record));
            final Record secondChildRecord = ((Record) (secondChildObject));
            final RecordSchema secondChildSchema = secondChildRecord.getSchema();
            Assert.assertEquals(Arrays.asList("name"), secondChildSchema.getFieldNames());
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadArray() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-array.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" }, firstRecordValues);
            final Object[] secondRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 2, "Jane Doe", 4820.09, "321 Your Street", "Your City", "NY", "33333", "USA" }, secondRecordValues);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadOneLinePerJSON() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-oneline.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" }, firstRecordValues);
            final Object[] secondRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 2, "Jane Doe", 4820.09, "321 Your Street", "Your City", "NY", "33333", "USA" }, secondRecordValues);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadMultilineJSON() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-multiline.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" }, firstRecordValues);
            final Object[] secondRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 2, "Jane Doe", 4820.09, "321 Your Street", "Your City", "NY", "33333", "USA" }, secondRecordValues);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadMultilineArrays() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-multiarray.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" }, firstRecordValues);
            final Object[] secondRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 2, "Jane Doe", 4820.09, "321 Your Street", "Your City", "NY", "33333", "USA" }, secondRecordValues);
            final Object[] thirdRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 3, "Maria Doe", 4750.89, "123 My Street", "My City", "ME", "11111", "USA" }, thirdRecordValues);
            final Object[] fourthRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 4, "Xi Doe", 4820.09, "321 Your Street", "Your City", "NV", "33333", "USA" }, fourthRecordValues);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadMixedJSON() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-mixed.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" }, firstRecordValues);
            final Object[] secondRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 2, "Jane Doe", 4820.09, "321 Your Street", "Your City", "NY", "33333", "USA" }, secondRecordValues);
            final Object[] thirdRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 3, "Maria Doe", 4750.89, "123 My Street", "My City", "ME", "11111", "USA" }, thirdRecordValues);
            final Object[] fourthRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 4, "Xi Doe", 4820.09, "321 Your Street", "Your City", "NV", "33333", "USA" }, fourthRecordValues);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadRawRecordIncludesFieldsNotInSchema() throws IOException, MalformedRecordException {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-array.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final Record schemaValidatedRecord = reader.nextRecord(true, true);
            Assert.assertEquals(1, schemaValidatedRecord.getValue("id"));
            Assert.assertEquals("John Doe", schemaValidatedRecord.getValue("name"));
            Assert.assertNull(schemaValidatedRecord.getValue("balance"));
        }
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-array.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final Record rawRecord = reader.nextRecord(false, false);
            Assert.assertEquals(1, rawRecord.getValue("id"));
            Assert.assertEquals("John Doe", rawRecord.getValue("name"));
            Assert.assertEquals(4750.89, rawRecord.getValue("balance"));
            Assert.assertEquals("123 My Street", rawRecord.getValue("address"));
            Assert.assertEquals("My City", rawRecord.getValue("city"));
            Assert.assertEquals("MS", rawRecord.getValue("state"));
            Assert.assertEquals("11111", rawRecord.getValue("zipCode"));
            Assert.assertEquals("USA", rawRecord.getValue("country"));
        }
    }

    @Test
    public void testReadRawRecordTypeCoercion() throws IOException, MalformedRecordException {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", STRING.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-array.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final Record schemaValidatedRecord = reader.nextRecord(true, true);
            Assert.assertEquals("1", schemaValidatedRecord.getValue("id"));// will be coerced into a STRING as per the schema

            Assert.assertEquals("John Doe", schemaValidatedRecord.getValue("name"));
            Assert.assertNull(schemaValidatedRecord.getValue("balance"));
            Assert.assertEquals(2, schemaValidatedRecord.getRawFieldNames().size());
        }
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-array.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final Record rawRecord = reader.nextRecord(false, false);
            Assert.assertEquals(1, rawRecord.getValue("id"));// will return raw value of (int) 1

            Assert.assertEquals("John Doe", rawRecord.getValue("name"));
            Assert.assertEquals(4750.89, rawRecord.getValue("balance"));
            Assert.assertEquals("123 My Street", rawRecord.getValue("address"));
            Assert.assertEquals("My City", rawRecord.getValue("city"));
            Assert.assertEquals("MS", rawRecord.getValue("state"));
            Assert.assertEquals("11111", rawRecord.getValue("zipCode"));
            Assert.assertEquals("USA", rawRecord.getValue("country"));
            Assert.assertEquals(8, rawRecord.getRawFieldNames().size());
        }
    }

    @Test
    public void testSingleJsonElement() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/single-bank-account.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" }, firstRecordValues);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testSingleJsonElementWithChoiceFields() throws IOException, MalformedRecordException {
        // Wraps default fields by Choice data type to test mapping to a Choice type.
        final List<RecordField> choiceFields = getDefaultFields().stream().map(( f) -> new RecordField(f.getFieldName(), RecordFieldType.CHOICE.getChoiceDataType(f.getDataType()))).collect(Collectors.toList());
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(choiceFields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/single-bank-account.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING });
            final List<RecordField> fields = schema.getFields();
            for (int i = 0; i < (schema.getFields().size()); i++) {
                Assert.assertTrue(((fields.get(i).getDataType()) instanceof ChoiceDataType));
                final ChoiceDataType choiceDataType = ((ChoiceDataType) (fields.get(i).getDataType()));
                Assert.assertEquals(expectedTypes.get(i), choiceDataType.getPossibleSubTypes().get(0).getFieldType());
            }
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" }, firstRecordValues);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testElementWithNestedData() throws IOException, MalformedRecordException {
        final DataType accountType = RECORD.getRecordDataType(getAccountSchema());
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("account", accountType));
        fields.remove(new RecordField("balance", DOUBLE.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/single-element-nested.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.RECORD });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            final Object[] allButLast = Arrays.copyOfRange(firstRecordValues, 0, ((firstRecordValues.length) - 1));
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", "123 My Street", "My City", "MS", "11111", "USA" }, allButLast);
            final Object last = firstRecordValues[((firstRecordValues.length) - 1)];
            Assert.assertTrue(Record.class.isAssignableFrom(last.getClass()));
            final Record record = ((Record) (last));
            Assert.assertEquals(42, record.getValue("id"));
            Assert.assertEquals(4750.89, record.getValue("balance"));
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testElementWithNestedArray() throws IOException, MalformedRecordException {
        final DataType accountRecordType = RECORD.getRecordDataType(getAccountSchema());
        final DataType accountsType = ARRAY.getArrayDataType(accountRecordType);
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("accounts", accountsType));
        fields.remove(new RecordField("balance", DOUBLE.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/single-element-nested-array.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "address", "city", "state", "zipCode", "country", "accounts" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.ARRAY });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            final Object[] nonArrayValues = Arrays.copyOfRange(firstRecordValues, 0, ((firstRecordValues.length) - 1));
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", "123 My Street", "My City", "MS", "11111", "USA" }, nonArrayValues);
            final Object lastRecord = firstRecordValues[((firstRecordValues.length) - 1)];
            Assert.assertTrue(Object[].class.isAssignableFrom(lastRecord.getClass()));
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadArrayDifferentSchemas() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-array-different-schemas.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" }, firstRecordValues);
            final Object[] secondRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 2, "Jane Doe", 4820.09, "321 Your Street", "Your City", "NY", "33333", null }, secondRecordValues);
            final Object[] thirdRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 3, "Jake Doe", 4751.89, "124 My Street", "My City", "MS", "11111", "USA" }, thirdRecordValues);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadArrayDifferentSchemasWithOverride() throws IOException, MalformedRecordException {
        final Map<String, DataType> overrides = new HashMap<>();
        overrides.put("address2", STRING.getDataType());
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("address2", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-array-different-schemas.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country", "address2" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA", null }, firstRecordValues);
            final Object[] secondRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 2, "Jane Doe", 4820.09, "321 Your Street", "Your City", "NY", "33333", null, null }, secondRecordValues);
            final Object[] thirdRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 3, "Jake Doe", 4751.89, "124 My Street", "My City", "MS", "11111", "USA", "Apt. #12" }, thirdRecordValues);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadArrayDifferentSchemasWithOptionalElementOverridden() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-array-optional-balance.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" }, firstRecordValues);
            final Object[] secondRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 2, "Jane Doe", null, "321 Your Street", "Your City", "NY", "33333", "USA" }, secondRecordValues);
            final Object[] thirdRecordValues = reader.nextRecord().getValues();
            Assert.assertArrayEquals(new Object[]{ 3, "Jimmy Doe", null, "321 Your Street", "Your City", "NY", "33333", "USA" }, thirdRecordValues);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadUnicodeCharacters() throws IOException, MalformedRecordException {
        final List<RecordField> fromFields = new ArrayList<>();
        fromFields.add(new RecordField("id", LONG.getDataType()));
        fromFields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema fromSchema = new org.apache.nifi.serialization.SimpleRecordSchema(fromFields);
        final DataType fromType = RECORD.getRecordDataType(fromSchema);
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("created_at", STRING.getDataType()));
        fields.add(new RecordField("id", LONG.getDataType()));
        fields.add(new RecordField("unicode", STRING.getDataType()));
        fields.add(new RecordField("from", fromType));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/json-with-unicode.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            final Object secondValue = firstRecordValues[1];
            Assert.assertTrue((secondValue instanceof Long));
            Assert.assertEquals(832036744985577473L, secondValue);
            final Object unicodeValue = firstRecordValues[2];
            Assert.assertEquals("\u3061\u3083\u6ce3\u304d\u305d\u3046", unicodeValue);
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testIncorrectSchema() throws IOException, MalformedRecordException {
        final DataType accountType = RECORD.getRecordDataType(getAccountSchema());
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("account", accountType));
        fields.remove(new RecordField("balance", DOUBLE.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/single-bank-account-wrong-field-type.json"));final JsonTreeRowRecordReader reader = new JsonTreeRowRecordReader(in, Mockito.mock(ComponentLog.class), schema, dateFormat, timeFormat, timestampFormat)) {
            reader.nextRecord().getValues();
            Assert.fail("Was able to read record with invalid schema.");
        } catch (final MalformedRecordException mre) {
            final String msg = mre.getCause().getMessage();
            Assert.assertTrue(msg.contains("account.balance"));
            Assert.assertTrue(msg.contains("true"));
            Assert.assertTrue(msg.contains("Double"));
            Assert.assertTrue(msg.contains("Boolean"));
        }
    }
}

