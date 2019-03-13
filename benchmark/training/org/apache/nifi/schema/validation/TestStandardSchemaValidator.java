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
package org.apache.nifi.schema.validation;


import RecordFieldType.ARRAY;
import RecordFieldType.DOUBLE;
import RecordFieldType.INT;
import RecordFieldType.LONG;
import RecordFieldType.MAP;
import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.MapRecord;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordFieldType;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.validation.SchemaValidationResult;
import org.apache.nifi.serialization.record.validation.ValidationError;
import org.junit.Assert;
import org.junit.Test;


public class TestStandardSchemaValidator {
    @Test
    public void testValidateCorrectSimpleTypesStrictValidation() throws ParseException {
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
        final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        df.setTimeZone(TimeZone.getTimeZone("gmt"));
        final long time = df.parse("2017/01/01 17:00:00.000").getTime();
        final Map<String, Object> intMap = new LinkedHashMap<>();
        intMap.put("height", 48);
        intMap.put("width", 96);
        List<RecordField> mapRecordFields = new ArrayList<>();
        RecordField mapRecordField = new RecordField("mapRecord", MAP.getMapDataType(INT.getDataType()));
        mapRecordFields.add(mapRecordField);
        fields.add(mapRecordField);
        RecordSchema mapRecordSchema = new org.apache.nifi.serialization.SimpleRecordSchema(mapRecordFields);
        MapRecord mapRecord = new MapRecord(mapRecordSchema, intMap);
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
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
        valueMap.put("map", intMap);
        valueMap.put("mapRecord", mapRecord);
        final Record record = new MapRecord(schema, valueMap);
        final SchemaValidationContext validationContext = new SchemaValidationContext(schema, false, true);
        final StandardSchemaValidator validator = new StandardSchemaValidator(validationContext);
        final SchemaValidationResult result = validator.validate(record);
        Assert.assertTrue(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        Assert.assertTrue(result.getValidationErrors().isEmpty());
    }

    @Test
    public void testValidateWrongButCoerceableType() throws ParseException {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put("id", 1);
        Record record = new MapRecord(schema, valueMap);
        final SchemaValidationContext strictValidationContext = new SchemaValidationContext(schema, false, true);
        final SchemaValidationContext lenientValidationContext = new SchemaValidationContext(schema, false, false);
        // Validate with correct type of int and a strict validation
        StandardSchemaValidator validator = new StandardSchemaValidator(strictValidationContext);
        SchemaValidationResult result = validator.validate(record);
        Assert.assertTrue(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        Assert.assertTrue(result.getValidationErrors().isEmpty());
        // Validate with correct type of int and a lenient validation
        validator = new StandardSchemaValidator(lenientValidationContext);
        result = validator.validate(record);
        Assert.assertTrue(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        Assert.assertTrue(result.getValidationErrors().isEmpty());
        // Update Map to set value to a String that is coerceable to an int
        valueMap.put("id", "1");
        record = new MapRecord(schema, valueMap);
        // Validate with incorrect type of string and a strict validation
        validator = new StandardSchemaValidator(strictValidationContext);
        result = validator.validate(record);
        Assert.assertFalse(result.isValid());
        final Collection<ValidationError> validationErrors = result.getValidationErrors();
        Assert.assertEquals(1, validationErrors.size());
        final ValidationError validationError = validationErrors.iterator().next();
        Assert.assertEquals("/id", validationError.getFieldName().get());
        // Validate with incorrect type of string and a lenient validation
        validator = new StandardSchemaValidator(lenientValidationContext);
        result = validator.validate(record);
        Assert.assertTrue(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        Assert.assertTrue(result.getValidationErrors().isEmpty());
    }

    @Test
    public void testMissingRequiredField() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType(), false));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put("id", 1);
        final Record record = new MapRecord(schema, valueMap, false, false);
        final SchemaValidationContext allowExtraFieldsContext = new SchemaValidationContext(schema, true, true);
        StandardSchemaValidator validator = new StandardSchemaValidator(allowExtraFieldsContext);
        SchemaValidationResult result = validator.validate(record);
        Assert.assertFalse(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        final ValidationError error = result.getValidationErrors().iterator().next();
        Assert.assertEquals("/name", error.getFieldName().get());
    }

    @Test
    public void testMissingNullableField() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("name", STRING.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put("id", 1);
        Record record = new MapRecord(schema, valueMap, false, false);
        final SchemaValidationContext allowExtraFieldsContext = new SchemaValidationContext(schema, true, true);
        StandardSchemaValidator validator = new StandardSchemaValidator(allowExtraFieldsContext);
        SchemaValidationResult result = validator.validate(record);
        Assert.assertTrue(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        Assert.assertTrue(result.getValidationErrors().isEmpty());
    }

    @Test
    public void testExtraFields() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put("id", 1);
        valueMap.put("name", "John Doe");
        Record record = new MapRecord(schema, valueMap, false, false);
        final SchemaValidationContext allowExtraFieldsContext = new SchemaValidationContext(schema, true, true);
        final SchemaValidationContext forbidExtraFieldsContext = new SchemaValidationContext(schema, false, false);
        StandardSchemaValidator validator = new StandardSchemaValidator(allowExtraFieldsContext);
        SchemaValidationResult result = validator.validate(record);
        Assert.assertTrue(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        Assert.assertTrue(result.getValidationErrors().isEmpty());
        validator = new StandardSchemaValidator(forbidExtraFieldsContext);
        result = validator.validate(record);
        Assert.assertFalse(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        final Collection<ValidationError> validationErrors = result.getValidationErrors();
        Assert.assertEquals(1, validationErrors.size());
        final ValidationError validationError = validationErrors.iterator().next();
        Assert.assertEquals("/name", validationError.getFieldName().get());
        System.out.println(validationError);
    }

    @Test
    public void testInvalidEmbeddedField() {
        final List<RecordField> accountFields = new ArrayList<>();
        accountFields.add(new RecordField("name", STRING.getDataType()));
        accountFields.add(new RecordField("balance", DOUBLE.getDataType()));
        final RecordSchema accountSchema = new org.apache.nifi.serialization.SimpleRecordSchema(accountFields);
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("account", RECORD.getRecordDataType(accountSchema)));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> accountValues = new HashMap<>();
        accountValues.put("name", "account-1");
        accountValues.put("balance", "123.45");
        final Record accountRecord = new MapRecord(accountSchema, accountValues);
        final Map<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put("id", 1);
        valueMap.put("account", accountRecord);
        Record record = new MapRecord(schema, valueMap, false, false);
        final SchemaValidationContext strictValidationContext = new SchemaValidationContext(schema, false, true);
        final SchemaValidationContext lenientValidationContext = new SchemaValidationContext(schema, false, false);
        StandardSchemaValidator validator = new StandardSchemaValidator(strictValidationContext);
        SchemaValidationResult result = validator.validate(record);
        Assert.assertFalse(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        Assert.assertEquals(1, result.getValidationErrors().size());
        final ValidationError validationError = result.getValidationErrors().iterator().next();
        Assert.assertEquals("/account/balance", validationError.getFieldName().get());
        validator = new StandardSchemaValidator(lenientValidationContext);
        result = validator.validate(record);
        Assert.assertTrue(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        Assert.assertTrue(result.getValidationErrors().isEmpty());
    }

    @Test
    public void testInvalidArrayValue() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("id", INT.getDataType()));
        fields.add(new RecordField("numbers", ARRAY.getArrayDataType(INT.getDataType())));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Map<String, Object> valueMap = new LinkedHashMap<>();
        valueMap.put("id", 1);
        valueMap.put("numbers", new Object[]{ 1, "2", "3" });
        Record record = new MapRecord(schema, valueMap, false, false);
        final SchemaValidationContext strictValidationContext = new SchemaValidationContext(schema, false, true);
        final SchemaValidationContext lenientValidationContext = new SchemaValidationContext(schema, false, false);
        StandardSchemaValidator validator = new StandardSchemaValidator(strictValidationContext);
        SchemaValidationResult result = validator.validate(record);
        Assert.assertFalse(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        Assert.assertEquals(1, result.getValidationErrors().size());
        final ValidationError validationError = result.getValidationErrors().iterator().next();
        Assert.assertEquals("/numbers", validationError.getFieldName().get());
        validator = new StandardSchemaValidator(lenientValidationContext);
        result = validator.validate(record);
        Assert.assertTrue(result.isValid());
        Assert.assertNotNull(result.getValidationErrors());
        Assert.assertTrue(result.getValidationErrors().isEmpty());
    }
}

