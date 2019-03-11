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
import RecordFieldType.INT;
import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import com.jayway.jsonpath.JsonPath;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordFieldType;
import org.apache.nifi.serialization.record.RecordSchema;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestJsonPathRowRecordReader {
    private final String dateFormat = DATE.getDefaultFormat();

    private final String timeFormat = TIME.getDefaultFormat();

    private final String timestampFormat = TIMESTAMP.getDefaultFormat();

    private final LinkedHashMap<String, JsonPath> allJsonPaths = new LinkedHashMap<>();

    @Test
    public void testReadArray() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-array.json"));final JsonPathRowRecordReader reader = new JsonPathRowRecordReader(allJsonPaths, schema, in, Mockito.mock(ComponentLog.class), dateFormat, timeFormat, timestampFormat)) {
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
    public void testReadOneLine() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-oneline.json"));final JsonPathRowRecordReader reader = new JsonPathRowRecordReader(allJsonPaths, schema, in, Mockito.mock(ComponentLog.class), dateFormat, timeFormat, timestampFormat)) {
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
    public void testSingleJsonElement() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/single-bank-account.json"));final JsonPathRowRecordReader reader = new JsonPathRowRecordReader(allJsonPaths, schema, in, Mockito.mock(ComponentLog.class), dateFormat, timeFormat, timestampFormat)) {
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
    public void testElementWithNestedData() throws IOException, MalformedRecordException {
        final LinkedHashMap<String, JsonPath> jsonPaths = new LinkedHashMap(allJsonPaths);
        jsonPaths.put("account", JsonPath.compile("$.account"));
        final DataType accountType = RECORD.getRecordDataType(getAccountSchema());
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("account", accountType));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/single-element-nested.json"));final JsonPathRowRecordReader reader = new JsonPathRowRecordReader(jsonPaths, schema, in, Mockito.mock(ComponentLog.class), dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country", "account" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.RECORD });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            final Object[] simpleElements = Arrays.copyOfRange(firstRecordValues, 0, ((firstRecordValues.length) - 1));
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", null, "123 My Street", "My City", "MS", "11111", "USA" }, simpleElements);
            final Object lastElement = firstRecordValues[((firstRecordValues.length) - 1)];
            Assert.assertTrue((lastElement instanceof Record));
            final Record record = ((Record) (lastElement));
            Assert.assertEquals(42, record.getValue("id"));
            Assert.assertEquals(4750.89, record.getValue("balance"));
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testElementWithNestedArray() throws IOException, MalformedRecordException {
        final LinkedHashMap<String, JsonPath> jsonPaths = new LinkedHashMap(allJsonPaths);
        jsonPaths.put("accounts", JsonPath.compile("$.accounts"));
        final DataType accountRecordType = RECORD.getRecordDataType(getAccountSchema());
        final DataType accountsType = ARRAY.getArrayDataType(accountRecordType);
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("accounts", accountsType));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/single-element-nested-array.json"));final JsonPathRowRecordReader reader = new JsonPathRowRecordReader(jsonPaths, schema, in, Mockito.mock(ComponentLog.class), dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country", "accounts" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.ARRAY });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            final Object[] nonArrayValues = Arrays.copyOfRange(firstRecordValues, 0, ((firstRecordValues.length) - 1));
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", null, "123 My Street", "My City", "MS", "11111", "USA" }, nonArrayValues);
            final Object lastRecord = firstRecordValues[((firstRecordValues.length) - 1)];
            Assert.assertTrue(Object[].class.isAssignableFrom(lastRecord.getClass()));
            final Object[] array = ((Object[]) (lastRecord));
            Assert.assertEquals(2, array.length);
            final Object firstElement = array[0];
            Assert.assertTrue((firstElement instanceof Record));
            final Record firstRecord = ((Record) (firstElement));
            Assert.assertEquals(42, firstRecord.getValue("id"));
            Assert.assertEquals(4750.89, firstRecord.getValue("balance"));
            final Object secondElement = array[1];
            Assert.assertTrue((secondElement instanceof Record));
            final Record secondRecord = ((Record) (secondElement));
            Assert.assertEquals(43, secondRecord.getValue("id"));
            Assert.assertEquals(48212.38, secondRecord.getValue("balance"));
            Assert.assertNull(reader.nextRecord());
        }
    }

    @Test
    public void testReadArrayDifferentSchemas() throws IOException, MalformedRecordException {
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(getDefaultFields());
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-array-different-schemas.json"));final JsonPathRowRecordReader reader = new JsonPathRowRecordReader(allJsonPaths, schema, in, Mockito.mock(ComponentLog.class), dateFormat, timeFormat, timestampFormat)) {
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
        final LinkedHashMap<String, JsonPath> jsonPaths = new LinkedHashMap(allJsonPaths);
        jsonPaths.put("address2", JsonPath.compile("$.address2"));
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("address2", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/bank-account-array-different-schemas.json"));final JsonPathRowRecordReader reader = new JsonPathRowRecordReader(jsonPaths, schema, in, Mockito.mock(ComponentLog.class), dateFormat, timeFormat, timestampFormat)) {
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
    public void testPrimitiveTypeArrays() throws IOException, MalformedRecordException {
        final LinkedHashMap<String, JsonPath> jsonPaths = new LinkedHashMap(allJsonPaths);
        jsonPaths.put("accountIds", JsonPath.compile("$.accountIds"));
        final List<RecordField> fields = getDefaultFields();
        final DataType idsType = ARRAY.getArrayDataType(INT.getDataType());
        fields.add(new RecordField("accountIds", idsType));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        try (final InputStream in = new FileInputStream(new File("src/test/resources/json/primitive-type-array.json"));final JsonPathRowRecordReader reader = new JsonPathRowRecordReader(jsonPaths, schema, in, Mockito.mock(ComponentLog.class), dateFormat, timeFormat, timestampFormat)) {
            final List<String> fieldNames = schema.getFieldNames();
            final List<String> expectedFieldNames = Arrays.asList(new String[]{ "id", "name", "balance", "address", "city", "state", "zipCode", "country", "accountIds" });
            Assert.assertEquals(expectedFieldNames, fieldNames);
            final List<RecordFieldType> dataTypes = schema.getDataTypes().stream().map(( dt) -> dt.getFieldType()).collect(Collectors.toList());
            final List<RecordFieldType> expectedTypes = Arrays.asList(new RecordFieldType[]{ RecordFieldType.INT, RecordFieldType.STRING, RecordFieldType.DOUBLE, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.STRING, RecordFieldType.ARRAY });
            Assert.assertEquals(expectedTypes, dataTypes);
            final Object[] firstRecordValues = reader.nextRecord().getValues();
            final Object[] nonArrayValues = Arrays.copyOfRange(firstRecordValues, 0, ((firstRecordValues.length) - 1));
            Assert.assertArrayEquals(new Object[]{ 1, "John Doe", 4750.89, "123 My Street", "My City", "MS", "11111", "USA" }, nonArrayValues);
            final Object lastRecord = firstRecordValues[((firstRecordValues.length) - 1)];
            Assert.assertNotNull(lastRecord);
            Assert.assertTrue(Object[].class.isAssignableFrom(lastRecord.getClass()));
            final Object[] array = ((Object[]) (lastRecord));
            Assert.assertArrayEquals(new Object[]{ 1, 2, 3 }, array);
            Assert.assertNull(reader.nextRecord());
            Assert.assertNull(reader.nextRecord());
        }
    }
}

