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
package org.apache.nifi.avro;


import LogicalTypes.Decimal;
import RecordFieldType.ARRAY;
import RecordFieldType.BYTE;
import RecordFieldType.CHAR;
import RecordFieldType.DATE;
import RecordFieldType.DOUBLE;
import RecordFieldType.FLOAT;
import RecordFieldType.INT;
import RecordFieldType.LONG;
import RecordFieldType.MAP;
import RecordFieldType.RECORD;
import RecordFieldType.SHORT;
import RecordFieldType.STRING;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import Type.BYTES;
import Type.NULL;
import Type.UNION;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.nifi.schema.access.SchemaNotFoundException;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.serialization.record.type.RecordDataType;
import org.junit.Assert;
import org.junit.Test;


public class TestAvroTypeUtil {
    @Test
    public void testCreateAvroSchemaPrimitiveTypes() throws SchemaNotFoundException {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("int", INT.getDataType()));
        fields.add(new RecordField("long", LONG.getDataType()));
        fields.add(new RecordField("string", STRING.getDataType(), "hola", Collections.singleton("greeting")));
        fields.add(new RecordField("byte", BYTE.getDataType()));
        fields.add(new RecordField("char", CHAR.getDataType()));
        fields.add(new RecordField("short", SHORT.getDataType()));
        fields.add(new RecordField("double", DOUBLE.getDataType()));
        fields.add(new RecordField("float", FLOAT.getDataType()));
        fields.add(new RecordField("time", TIME.getDataType()));
        fields.add(new RecordField("date", DATE.getDataType()));
        fields.add(new RecordField("timestamp", TIMESTAMP.getDataType()));
        final DataType arrayType = ARRAY.getArrayDataType(STRING.getDataType());
        fields.add(new RecordField("strings", arrayType));
        final DataType mapType = MAP.getMapDataType(LONG.getDataType());
        fields.add(new RecordField("map", mapType));
        final List<RecordField> personFields = new ArrayList<>();
        personFields.add(new RecordField("name", STRING.getDataType()));
        personFields.add(new RecordField("dob", DATE.getDataType()));
        final RecordSchema personSchema = new org.apache.nifi.serialization.SimpleRecordSchema(personFields);
        final DataType personType = RECORD.getRecordDataType(personSchema);
        fields.add(new RecordField("person", personType));
        final RecordSchema recordSchema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Schema avroSchema = AvroTypeUtil.extractAvroSchema(recordSchema);
        // everything should be a union, since it's nullable.
        for (final Field field : avroSchema.getFields()) {
            final Schema fieldSchema = field.schema();
            Assert.assertEquals(UNION, fieldSchema.getType());
            Assert.assertTrue((("Field " + (field.name())) + " does not contain NULL type"), fieldSchema.getTypes().contains(Schema.create(NULL)));
        }
        final RecordSchema afterConversion = AvroTypeUtil.createSchema(avroSchema);
        Assert.assertEquals(INT.getDataType(), afterConversion.getDataType("int").get());
        Assert.assertEquals(LONG.getDataType(), afterConversion.getDataType("long").get());
        Assert.assertEquals(STRING.getDataType(), afterConversion.getDataType("string").get());
        Assert.assertEquals(INT.getDataType(), afterConversion.getDataType("byte").get());
        Assert.assertEquals(STRING.getDataType(), afterConversion.getDataType("char").get());
        Assert.assertEquals(INT.getDataType(), afterConversion.getDataType("short").get());
        Assert.assertEquals(DOUBLE.getDataType(), afterConversion.getDataType("double").get());
        Assert.assertEquals(FLOAT.getDataType(), afterConversion.getDataType("float").get());
        Assert.assertEquals(TIME.getDataType(), afterConversion.getDataType("time").get());
        Assert.assertEquals(DATE.getDataType(), afterConversion.getDataType("date").get());
        Assert.assertEquals(TIMESTAMP.getDataType(), afterConversion.getDataType("timestamp").get());
        Assert.assertEquals(arrayType, afterConversion.getDataType("strings").get());
        Assert.assertEquals(mapType, afterConversion.getDataType("map").get());
        Assert.assertEquals(personType, afterConversion.getDataType("person").get());
        final RecordField stringField = afterConversion.getField("string").get();
        Assert.assertEquals("hola", stringField.getDefaultValue());
        Assert.assertEquals(Collections.singleton("greeting"), stringField.getAliases());
    }

    /**
     * The issue consists on having an Avro's schema with a default value in an
     * array. See
     * <a href="https://issues.apache.org/jira/browse/NIFI-4893">NIFI-4893</a>.
     *
     * @throws IOException
     * 		schema not found.
     */
    @Test
    public void testDefaultArrayValue1() throws IOException {
        Schema avroSchema = new Schema.Parser().parse(getClass().getResourceAsStream("defaultArrayValue1.json"));
        GenericRecordBuilder builder = new GenericRecordBuilder(avroSchema);
        Record r = builder.build();
        @SuppressWarnings("unchecked")
        GenericData.Array<Integer> values = ((GenericData.Array<Integer>) (r.get("listOfInt")));
        Assert.assertEquals(values.size(), 0);
        RecordSchema record = AvroTypeUtil.createSchema(avroSchema);
        RecordField field = record.getField("listOfInt").get();
        Assert.assertEquals(ARRAY, field.getDataType().getFieldType());
        Assert.assertTrue(((field.getDefaultValue()) instanceof Object[]));
        Assert.assertEquals(0, ((Object[]) (field.getDefaultValue())).length);
    }

    /**
     * The issue consists on having an Avro's schema with a default value in an
     * array. See
     * <a href="https://issues.apache.org/jira/browse/NIFI-4893">NIFI-4893</a>.
     *
     * @throws IOException
     * 		schema not found.
     */
    @Test
    public void testDefaultArrayValue2() throws IOException {
        Schema avroSchema = new Schema.Parser().parse(getClass().getResourceAsStream("defaultArrayValue2.json"));
        GenericRecordBuilder builder = new GenericRecordBuilder(avroSchema);
        Record r = builder.build();
        @SuppressWarnings("unchecked")
        GenericData.Array<Integer> values = ((GenericData.Array<Integer>) (r.get("listOfInt")));
        Assert.assertArrayEquals(new Object[]{ 1, 2 }, values.toArray());
        RecordSchema record = AvroTypeUtil.createSchema(avroSchema);
        RecordField field = record.getField("listOfInt").get();
        Assert.assertEquals(ARRAY, field.getDataType().getFieldType());
        Assert.assertTrue(((field.getDefaultValue()) instanceof Object[]));
        Assert.assertArrayEquals(new Object[]{ 1, 2 }, ((Object[]) (field.getDefaultValue())));
    }

    /**
     * The issue consists on having an Avro's schema with a default value in an
     * array. See
     * <a href="https://issues.apache.org/jira/browse/NIFI-4893">NIFI-4893</a>.
     *
     * @throws IOException
     * 		schema not found.
     */
    @Test
    public void testDefaultArrayValuesInRecordsCase1() throws IOException {
        Schema avroSchema = new Schema.Parser().parse(getClass().getResourceAsStream("defaultArrayInRecords1.json"));
        GenericRecordBuilder builder = new GenericRecordBuilder(avroSchema);
        Record field1Record = build();
        builder.set("field1", field1Record);
        Record r = builder.build();
        @SuppressWarnings("unchecked")
        GenericData.Array<Integer> values = ((GenericData.Array<Integer>) (get("listOfInt")));
        Assert.assertArrayEquals(new Object[]{  }, values.toArray());
        RecordSchema record = AvroTypeUtil.createSchema(avroSchema);
        RecordField field = record.getField("field1").get();
        Assert.assertEquals(RECORD, field.getDataType().getFieldType());
        RecordDataType data = ((RecordDataType) (field.getDataType()));
        RecordSchema childSchema = data.getChildSchema();
        RecordField childField = childSchema.getField("listOfInt").get();
        Assert.assertEquals(ARRAY, childField.getDataType().getFieldType());
        Assert.assertTrue(((childField.getDefaultValue()) instanceof Object[]));
        Assert.assertArrayEquals(new Object[]{  }, ((Object[]) (childField.getDefaultValue())));
    }

    /**
     * The issue consists on having an Avro's schema with a default value in an
     * array. See
     * <a href="https://issues.apache.org/jira/browse/NIFI-4893">NIFI-4893</a>.
     *
     * @throws IOException
     * 		schema not found.
     */
    @Test
    public void testDefaultArrayValuesInRecordsCase2() throws IOException {
        Schema avroSchema = new Schema.Parser().parse(getClass().getResourceAsStream("defaultArrayInRecords2.json"));
        GenericRecordBuilder builder = new GenericRecordBuilder(avroSchema);
        Record field1Record = build();
        builder.set("field1", field1Record);
        Record r = builder.build();
        @SuppressWarnings("unchecked")
        GenericData.Array<Integer> values = ((GenericData.Array<Integer>) (get("listOfInt")));
        Assert.assertArrayEquals(new Object[]{ 1, 2, 3 }, values.toArray());
        RecordSchema record = AvroTypeUtil.createSchema(avroSchema);
        RecordField field = record.getField("field1").get();
        Assert.assertEquals(RECORD, field.getDataType().getFieldType());
        RecordDataType data = ((RecordDataType) (field.getDataType()));
        RecordSchema childSchema = data.getChildSchema();
        RecordField childField = childSchema.getField("listOfInt").get();
        Assert.assertEquals(ARRAY, childField.getDataType().getFieldType());
        Assert.assertTrue(((childField.getDefaultValue()) instanceof Object[]));
        Assert.assertArrayEquals(new Object[]{ 1, 2, 3 }, ((Object[]) (childField.getDefaultValue())));
    }

    // Simple recursion is a record A composing itself (similar to a LinkedList Node
    // referencing 'next')
    @Test
    public void testSimpleRecursiveSchema() {
        Schema recursiveSchema = new Schema.Parser().parse(("{\n" + (((((((((((((((((((("  \"namespace\": \"org.apache.nifi.testing\",\n" + "  \"name\": \"NodeRecord\",\n") + "  \"type\": \"record\",\n") + "  \"fields\": [\n") + "    {\n") + "      \"name\": \"id\",\n") + "      \"type\": \"int\"\n") + "    },\n") + "    {\n") + "      \"name\": \"value\",\n") + "      \"type\": \"string\"\n") + "    },\n") + "    {\n") + "      \"name\": \"parent\",\n") + "      \"type\": [\n") + "        \"null\",\n") + "        \"NodeRecord\"\n") + "      ]\n") + "    }\n") + "  ]\n") + "}\n")));
        // Make sure the following doesn't throw an exception
        RecordSchema result = AvroTypeUtil.createSchema(recursiveSchema);
        // Make sure it parsed correctly
        Assert.assertEquals(3, result.getFieldCount());
        Optional<RecordField> idField = result.getField("id");
        Assert.assertTrue(idField.isPresent());
        Assert.assertEquals(INT, idField.get().getDataType().getFieldType());
        Optional<RecordField> valueField = result.getField("value");
        Assert.assertTrue(valueField.isPresent());
        Assert.assertEquals(STRING, valueField.get().getDataType().getFieldType());
        Optional<RecordField> parentField = result.getField("parent");
        Assert.assertTrue(parentField.isPresent());
        Assert.assertEquals(RECORD, parentField.get().getDataType().getFieldType());
        // The 'parent' field should have a circular schema reference to the top level
        // record schema, similar to how Avro handles this
        Assert.assertEquals(result, getChildSchema());
    }

    // Complicated recursion is a record A composing record B, who composes a record
    // A
    @Test
    public void testComplicatedRecursiveSchema() {
        Schema recursiveSchema = new Schema.Parser().parse(("{\n" + (((((((((((((((((((((((((((((((((((((("  \"namespace\": \"org.apache.nifi.testing\",\n" + "  \"name\": \"Record_A\",\n") + "  \"type\": \"record\",\n") + "  \"fields\": [\n") + "    {\n") + "      \"name\": \"id\",\n") + "      \"type\": \"int\"\n") + "    },\n") + "    {\n") + "      \"name\": \"value\",\n") + "      \"type\": \"string\"\n") + "    },\n") + "    {\n") + "      \"name\": \"child\",\n") + "      \"type\": {\n") + "        \"namespace\": \"org.apache.nifi.testing\",\n") + "        \"name\": \"Record_B\",\n") + "        \"type\": \"record\",\n") + "        \"fields\": [\n") + "          {\n") + "            \"name\": \"id\",\n") + "            \"type\": \"int\"\n") + "          },\n") + "          {\n") + "            \"name\": \"value\",\n") + "            \"type\": \"string\"\n") + "          },\n") + "          {\n") + "            \"name\": \"parent\",\n") + "            \"type\": [\n") + "              \"null\",\n") + "              \"Record_A\"\n") + "            ]\n") + "          }\n") + "        ]\n") + "      }\n") + "    }\n") + "  ]\n") + "}\n")));
        // Make sure the following doesn't throw an exception
        RecordSchema recordASchema = AvroTypeUtil.createSchema(recursiveSchema);
        // Make sure it parsed correctly
        Assert.assertEquals(3, recordASchema.getFieldCount());
        Optional<RecordField> recordAIdField = recordASchema.getField("id");
        Assert.assertTrue(recordAIdField.isPresent());
        Assert.assertEquals(INT, recordAIdField.get().getDataType().getFieldType());
        Optional<RecordField> recordAValueField = recordASchema.getField("value");
        Assert.assertTrue(recordAValueField.isPresent());
        Assert.assertEquals(STRING, recordAValueField.get().getDataType().getFieldType());
        Optional<RecordField> recordAChildField = recordASchema.getField("child");
        Assert.assertTrue(recordAChildField.isPresent());
        Assert.assertEquals(RECORD, recordAChildField.get().getDataType().getFieldType());
        // Get the child schema
        RecordSchema recordBSchema = ((RecordDataType) (recordAChildField.get().getDataType())).getChildSchema();
        // Make sure it parsed correctly
        Assert.assertEquals(3, recordBSchema.getFieldCount());
        Optional<RecordField> recordBIdField = recordBSchema.getField("id");
        Assert.assertTrue(recordBIdField.isPresent());
        Assert.assertEquals(INT, recordBIdField.get().getDataType().getFieldType());
        Optional<RecordField> recordBValueField = recordBSchema.getField("value");
        Assert.assertTrue(recordBValueField.isPresent());
        Assert.assertEquals(STRING, recordBValueField.get().getDataType().getFieldType());
        Optional<RecordField> recordBParentField = recordBSchema.getField("parent");
        Assert.assertTrue(recordBParentField.isPresent());
        Assert.assertEquals(RECORD, recordBParentField.get().getDataType().getFieldType());
        // Make sure the 'parent' field has a schema reference back to the original top
        // level record schema
        Assert.assertEquals(recordASchema, getChildSchema());
    }

    @Test
    public void testMapWithNullSchema() throws IOException {
        Schema recursiveSchema = new Schema.Parser().parse(getClass().getResourceAsStream("schema.json"));
        // Make sure the following doesn't throw an exception
        RecordSchema recordASchema = AvroTypeUtil.createSchema(recursiveSchema.getTypes().get(0));
        // check the fix with the proper file
        try (DataFileStream<GenericRecord> r = new DataFileStream(getClass().getResourceAsStream("data.avro"), new org.apache.avro.generic.GenericDatumReader())) {
            GenericRecord n = r.next();
            AvroTypeUtil.convertAvroRecordToMap(n, recordASchema, StandardCharsets.UTF_8);
        }
    }

    @Test
    public void testToDecimalConversion() {
        final LogicalTypes.Decimal decimalType = LogicalTypes.decimal(18, 8);
        final Schema fieldSchema = Schema.create(BYTES);
        decimalType.addToSchema(fieldSchema);
        final Map<Object, String> expects = new HashMap<>();
        // Double to Decimal
        expects.put(123.0, "123.00000000");
        // Double can not represent exact 1234567890.12345678, so use 1 less digit to
        // test here.
        expects.put(1.2345678901234567E9, "1234567890.12345670");
        expects.put(1.2345678912345678E8, "123456789.12345678");
        expects.put(1.234567890123456E15, "1234567890123456.00000000");
        // ROUND HALF UP.
        expects.put(0.1234567890123456, "0.12345679");
        // BigDecimal to BigDecimal
        expects.put(new BigDecimal("123"), "123.00000000");
        expects.put(new BigDecimal("1234567890.12345678"), "1234567890.12345678");
        expects.put(new BigDecimal("123456789012345678"), "123456789012345678.00000000");
        // ROUND HALF UP.
        expects.put(new BigDecimal("0.123456789012345678"), "0.12345679");
        // String to BigDecimal
        expects.put("123", "123.00000000");
        expects.put("1234567890.12345678", "1234567890.12345678");
        expects.put("123456789012345678", "123456789012345678.00000000");
        expects.put("0.1234567890123456", "0.12345679");
        expects.put("Not a number", "java.lang.NumberFormatException");
        // Integer to BigDecimal
        expects.put(123, "123.00000000");
        expects.put((-1234567), "-1234567.00000000");
        // Long to BigDecimal
        expects.put(123L, "123.00000000");
        expects.put(123456789012345678L, "123456789012345678.00000000");
        expects.forEach(( rawValue, expect) -> {
            final Object convertedValue;
            try {
                convertedValue = AvroTypeUtil.convertToAvroObject(rawValue, fieldSchema, StandardCharsets.UTF_8);
            } catch (Exception e) {
                if (expect.equals(e.getClass().getCanonicalName())) {
                    // Expected behavior.
                    return;
                }
                Assert.fail(String.format("Unexpected exception, %s with %s %s while expecting %s", e, rawValue.getClass().getSimpleName(), rawValue, expect));
                return;
            }
            Assert.assertTrue((convertedValue instanceof ByteBuffer));
            final ByteBuffer serializedBytes = ((ByteBuffer) (convertedValue));
            final BigDecimal bigDecimal = new Conversions.DecimalConversion().fromBytes(serializedBytes, fieldSchema, decimalType);
            Assert.assertEquals(String.format("%s %s should be converted to %s", rawValue.getClass().getSimpleName(), rawValue, expect), expect, bigDecimal.toString());
        });
    }

    @Test
    public void testBytesDecimalConversion() {
        final LogicalTypes.Decimal decimalType = LogicalTypes.decimal(18, 8);
        final Schema fieldSchema = Schema.create(BYTES);
        decimalType.addToSchema(fieldSchema);
        final Object convertedValue = AvroTypeUtil.convertToAvroObject("2.5", fieldSchema, StandardCharsets.UTF_8);
        Assert.assertTrue((convertedValue instanceof ByteBuffer));
        final ByteBuffer serializedBytes = ((ByteBuffer) (convertedValue));
        final BigDecimal bigDecimal = new Conversions.DecimalConversion().fromBytes(serializedBytes, fieldSchema, decimalType);
        Assert.assertEquals(new BigDecimal("2.5").setScale(8), bigDecimal);
    }

    @Test
    public void testFixedDecimalConversion() {
        final LogicalTypes.Decimal decimalType = LogicalTypes.decimal(18, 8);
        final Schema fieldSchema = Schema.createFixed("mydecimal", "no doc", "myspace", 18);
        decimalType.addToSchema(fieldSchema);
        final Object convertedValue = AvroTypeUtil.convertToAvroObject("2.5", fieldSchema, StandardCharsets.UTF_8);
        Assert.assertTrue((convertedValue instanceof GenericFixed));
        final GenericFixed genericFixed = ((GenericFixed) (convertedValue));
        final BigDecimal bigDecimal = new Conversions.DecimalConversion().fromFixed(genericFixed, fieldSchema, decimalType);
        Assert.assertEquals(new BigDecimal("2.5").setScale(8), bigDecimal);
    }

    @Test
    public void testSchemaNameNotEmpty() throws IOException {
        Schema schema = new Schema.Parser().parse(getClass().getResourceAsStream("simpleSchema.json"));
        RecordSchema recordSchema = AvroTypeUtil.createSchema(schema);
        Assert.assertTrue(recordSchema.getIdentifier().getName().isPresent());
        Assert.assertEquals(Optional.of("record_name"), recordSchema.getIdentifier().getName());
    }

    @Test
    public void testStringToBytesConversion() {
        Object o = AvroTypeUtil.convertToAvroObject("Hello", Schema.create(BYTES), StandardCharsets.UTF_16);
        Assert.assertTrue((o instanceof ByteBuffer));
        Assert.assertEquals("Hello", new String(((ByteBuffer) (o)).array(), StandardCharsets.UTF_16));
    }

    @Test
    public void testStringToNullableBytesConversion() {
        Object o = AvroTypeUtil.convertToAvroObject("Hello", Schema.createUnion(Schema.create(NULL), Schema.create(BYTES)), StandardCharsets.UTF_16);
        Assert.assertTrue((o instanceof ByteBuffer));
        Assert.assertEquals("Hello", new String(((ByteBuffer) (o)).array(), StandardCharsets.UTF_16));
    }

    @Test
    public void testBytesToStringConversion() {
        final Charset charset = Charset.forName("UTF_32LE");
        Object o = AvroTypeUtil.convertToAvroObject("Hello".getBytes(charset), Schema.create(Type.STRING), charset);
        Assert.assertTrue((o instanceof String));
        Assert.assertEquals("Hello", o);
    }

    @Test
    public void testAliasCreatedForInvalidField() {
        final List<RecordField> fields = new ArrayList<>();
        fields.add(new RecordField("valid", STRING.getDataType()));
        fields.add(new RecordField("$invalid2", STRING.getDataType()));
        fields.add(new RecordField("3invalid3", STRING.getDataType()));
        fields.add(new RecordField("  __ Another ONE!!", STRING.getDataType()));
        final RecordSchema recordSchema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final Schema avroSchema = AvroTypeUtil.extractAvroSchema(recordSchema);
        Assert.assertNotNull(avroSchema.getField("valid"));
        Assert.assertNull(avroSchema.getField("$invalid"));
        final Field field2 = avroSchema.getField("_invalid2");
        Assert.assertNotNull(field2);
        Assert.assertEquals("_invalid2", field2.name());
        Assert.assertEquals(1, field2.aliases().size());
        Assert.assertTrue(field2.aliases().contains("$invalid2"));
        Assert.assertNull(avroSchema.getField("$invalid3"));
        final Field field3 = avroSchema.getField("_invalid3");
        Assert.assertNotNull(field3);
        Assert.assertEquals("_invalid3", field3.name());
        Assert.assertEquals(1, field3.aliases().size());
        Assert.assertTrue(field3.aliases().contains("3invalid3"));
        Assert.assertNull(avroSchema.getField("  __ Another ONE!!"));
        final Field field4 = avroSchema.getField("_____Another_ONE__");
        Assert.assertNotNull(field4);
        Assert.assertEquals("_____Another_ONE__", field4.name());
        Assert.assertEquals(1, field4.aliases().size());
        Assert.assertTrue(field4.aliases().contains("  __ Another ONE!!"));
    }

    @Test
    public void testMapToRecordConversion() {
        final Charset charset = Charset.forName("UTF-8");
        Object o = AvroTypeUtil.convertToAvroObject(Collections.singletonMap("Hello", "World"), Schema.createRecord(Collections.singletonList(new Field("Hello", Schema.create(Type.STRING), "", ""))), charset);
        Assert.assertTrue((o instanceof Record));
        Assert.assertEquals("World", get("Hello"));
    }

    @Test
    public void testListAndMapConversion() {
        Schema s = Schema.createRecord(Arrays.asList(new Field("List", Schema.createArray(Schema.createRecord(Arrays.asList(new Field("Message", Schema.create(Type.STRING), "", "")))), "", null)));
        Map<String, Object> obj = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int x = 0; x < 10; x++) {
            list.add(new HashMap<String, Object>() {
                {
                    put("Message", UUID.randomUUID().toString());
                }
            });
        }
        obj.put("List", list);
        Object o = AvroTypeUtil.convertToAvroObject(obj, s);
        Assert.assertTrue((o instanceof Record));
        List innerList = ((List) (get("List")));
        Assert.assertNotNull(innerList);
        Assert.assertEquals(10, innerList.size());
        for (Object inner : innerList) {
            Assert.assertTrue((inner instanceof Record));
            Assert.assertNotNull(get("Message"));
        }
    }
}

