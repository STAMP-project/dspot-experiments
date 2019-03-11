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


import RecordFieldType.ARRAY;
import RecordFieldType.CHOICE;
import RecordFieldType.MAP;
import RecordFieldType.RECORD;
import Type.BOOLEAN;
import Type.BYTES;
import Type.DOUBLE;
import Type.FLOAT;
import Type.INT;
import Type.LONG;
import Type.NULL;
import Type.STRING;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.nifi.schema.access.SchemaNotFoundException;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.junit.Assert;
import org.junit.Test;


public class TestAvroReaderWithEmbeddedSchema {
    @Test
    public void testLogicalTypes() throws IOException, ParseException, SchemaNotFoundException, MalformedRecordException {
        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/avro/logical-types.avsc"));
        testLogicalTypes(schema);
    }

    @Test
    public void testNullableLogicalTypes() throws IOException, ParseException, SchemaNotFoundException, MalformedRecordException {
        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/avro/logical-types-nullable.avsc"));
        testLogicalTypes(schema);
    }

    @Test
    public void testDataTypes() throws IOException, SchemaNotFoundException, MalformedRecordException {
        final List<Field> accountFields = new ArrayList<>();
        accountFields.add(new Field("accountId", Schema.create(LONG), null, ((Object) (null))));
        accountFields.add(new Field("accountName", Schema.create(STRING), null, ((Object) (null))));
        final Schema accountSchema = Schema.createRecord("account", null, null, false);
        accountSchema.setFields(accountFields);
        final List<Field> catFields = new ArrayList<>();
        catFields.add(new Field("catTailLength", Schema.create(INT), null, ((Object) (null))));
        catFields.add(new Field("catName", Schema.create(STRING), null, ((Object) (null))));
        final Schema catSchema = Schema.createRecord("cat", null, null, false);
        catSchema.setFields(catFields);
        final List<Field> dogFields = new ArrayList<>();
        dogFields.add(new Field("dogTailLength", Schema.create(INT), null, ((Object) (null))));
        dogFields.add(new Field("dogName", Schema.create(STRING), null, ((Object) (null))));
        final Schema dogSchema = Schema.createRecord("dog", null, null, false);
        dogSchema.setFields(dogFields);
        final List<Field> fields = new ArrayList<>();
        fields.add(new Field("name", Schema.create(STRING), null, ((Object) (null))));
        fields.add(new Field("age", Schema.create(INT), null, ((Object) (null))));
        fields.add(new Field("balance", Schema.create(DOUBLE), null, ((Object) (null))));
        fields.add(new Field("rate", Schema.create(FLOAT), null, ((Object) (null))));
        fields.add(new Field("debt", Schema.create(BOOLEAN), null, ((Object) (null))));
        fields.add(new Field("nickname", Schema.create(NULL), null, ((Object) (null))));
        fields.add(new Field("binary", Schema.create(BYTES), null, ((Object) (null))));
        fields.add(new Field("fixed", Schema.createFixed("fixed", null, null, 5), null, ((Object) (null))));
        fields.add(new Field("map", Schema.createMap(Schema.create(STRING)), null, ((Object) (null))));
        fields.add(new Field("array", Schema.createArray(Schema.create(LONG)), null, ((Object) (null))));
        fields.add(new Field("account", accountSchema, null, ((Object) (null))));
        fields.add(new Field("desiredbalance", // test union of NULL and other type with no value
        Schema.createUnion(Arrays.asList(Schema.create(NULL), Schema.create(DOUBLE))), null, ((Object) (null))));
        fields.add(new Field("dreambalance", // test union of NULL and other type with a value
        Schema.createUnion(Arrays.asList(Schema.create(NULL), Schema.create(DOUBLE))), null, ((Object) (null))));
        fields.add(new Field("favAnimal", Schema.createUnion(Arrays.asList(catSchema, dogSchema)), null, ((Object) (null))));
        fields.add(new Field("otherFavAnimal", Schema.createUnion(Arrays.asList(catSchema, dogSchema)), null, ((Object) (null))));
        final Schema schema = Schema.createRecord("record", null, null, false);
        schema.setFields(fields);
        final byte[] source;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final Map<String, String> map = new HashMap<>();
        map.put("greeting", "hello");
        map.put("salutation", "good-bye");
        final DatumWriter<GenericRecord> datumWriter = new org.apache.avro.generic.GenericDatumWriter(schema);
        try (final DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter(datumWriter);final DataFileWriter<GenericRecord> writer = dataFileWriter.create(schema, baos)) {
            final GenericRecord record = new org.apache.avro.generic.GenericData.Record(schema);
            record.put("name", "John");
            record.put("age", 33);
            record.put("balance", 1234.56);
            record.put("rate", 0.045F);
            record.put("debt", false);
            record.put("binary", ByteBuffer.wrap("binary".getBytes(StandardCharsets.UTF_8)));
            record.put("fixed", new org.apache.avro.generic.GenericData.Fixed(Schema.create(BYTES), "fixed".getBytes(StandardCharsets.UTF_8)));
            record.put("map", map);
            record.put("array", Arrays.asList(1L, 2L));
            record.put("dreambalance", 1.0E7);
            final GenericRecord accountRecord = new org.apache.avro.generic.GenericData.Record(accountSchema);
            accountRecord.put("accountId", 83L);
            accountRecord.put("accountName", "Checking");
            record.put("account", accountRecord);
            final GenericRecord catRecord = new org.apache.avro.generic.GenericData.Record(catSchema);
            catRecord.put("catTailLength", 1);
            catRecord.put("catName", "Meow");
            record.put("otherFavAnimal", catRecord);
            final GenericRecord dogRecord = new org.apache.avro.generic.GenericData.Record(dogSchema);
            dogRecord.put("dogTailLength", 14);
            dogRecord.put("dogName", "Fido");
            record.put("favAnimal", dogRecord);
            writer.append(record);
        }
        source = baos.toByteArray();
        try (final InputStream in = new ByteArrayInputStream(source)) {
            final AvroRecordReader reader = new AvroReaderWithEmbeddedSchema(in);
            final RecordSchema recordSchema = reader.getSchema();
            Assert.assertEquals(15, recordSchema.getFieldCount());
            Assert.assertEquals(RecordFieldType.STRING, recordSchema.getDataType("name").get().getFieldType());
            Assert.assertEquals(RecordFieldType.INT, recordSchema.getDataType("age").get().getFieldType());
            Assert.assertEquals(RecordFieldType.DOUBLE, recordSchema.getDataType("balance").get().getFieldType());
            Assert.assertEquals(RecordFieldType.FLOAT, recordSchema.getDataType("rate").get().getFieldType());
            Assert.assertEquals(RecordFieldType.BOOLEAN, recordSchema.getDataType("debt").get().getFieldType());
            Assert.assertEquals(RecordFieldType.STRING, recordSchema.getDataType("nickname").get().getFieldType());
            Assert.assertEquals(ARRAY, recordSchema.getDataType("binary").get().getFieldType());
            Assert.assertEquals(ARRAY, recordSchema.getDataType("fixed").get().getFieldType());
            Assert.assertEquals(MAP, recordSchema.getDataType("map").get().getFieldType());
            Assert.assertEquals(ARRAY, recordSchema.getDataType("array").get().getFieldType());
            Assert.assertEquals(RECORD, recordSchema.getDataType("account").get().getFieldType());
            Assert.assertEquals(RecordFieldType.DOUBLE, recordSchema.getDataType("desiredbalance").get().getFieldType());
            Assert.assertEquals(RecordFieldType.DOUBLE, recordSchema.getDataType("dreambalance").get().getFieldType());
            Assert.assertEquals(CHOICE, recordSchema.getDataType("favAnimal").get().getFieldType());
            Assert.assertEquals(CHOICE, recordSchema.getDataType("otherFavAnimal").get().getFieldType());
            final Object[] values = reader.nextRecord().getValues();
            Assert.assertEquals(15, values.length);
            Assert.assertEquals("John", values[0]);
            Assert.assertEquals(33, values[1]);
            Assert.assertEquals(1234.56, values[2]);
            Assert.assertEquals(0.045F, values[3]);
            Assert.assertEquals(false, values[4]);
            Assert.assertEquals(null, values[5]);
            Assert.assertArrayEquals(toObjectArray("binary".getBytes(StandardCharsets.UTF_8)), ((Object[]) (values[6])));
            Assert.assertArrayEquals(toObjectArray("fixed".getBytes(StandardCharsets.UTF_8)), ((Object[]) (values[7])));
            Assert.assertEquals(map, values[8]);
            Assert.assertArrayEquals(new Object[]{ 1L, 2L }, ((Object[]) (values[9])));
            final Map<String, Object> accountValues = new HashMap<>();
            accountValues.put("accountName", "Checking");
            accountValues.put("accountId", 83L);
            final List<RecordField> accountRecordFields = new ArrayList<>();
            accountRecordFields.add(new RecordField("accountId", RecordFieldType.LONG.getDataType(), false));
            accountRecordFields.add(new RecordField("accountName", RecordFieldType.STRING.getDataType(), false));
            final RecordSchema accountRecordSchema = new org.apache.nifi.serialization.SimpleRecordSchema(accountRecordFields);
            final Record mapRecord = new org.apache.nifi.serialization.record.MapRecord(accountRecordSchema, accountValues);
            Assert.assertEquals(mapRecord, values[10]);
            Assert.assertNull(values[11]);
            Assert.assertEquals(1.0E7, values[12]);
            final Map<String, Object> dogMap = new HashMap<>();
            dogMap.put("dogName", "Fido");
            dogMap.put("dogTailLength", 14);
            final List<RecordField> dogRecordFields = new ArrayList<>();
            dogRecordFields.add(new RecordField("dogTailLength", RecordFieldType.INT.getDataType(), false));
            dogRecordFields.add(new RecordField("dogName", RecordFieldType.STRING.getDataType(), false));
            final RecordSchema dogRecordSchema = new org.apache.nifi.serialization.SimpleRecordSchema(dogRecordFields);
            final Record dogRecord = new org.apache.nifi.serialization.record.MapRecord(dogRecordSchema, dogMap);
            Assert.assertEquals(dogRecord, values[13]);
            final Map<String, Object> catMap = new HashMap<>();
            catMap.put("catName", "Meow");
            catMap.put("catTailLength", 1);
            final List<RecordField> catRecordFields = new ArrayList<>();
            catRecordFields.add(new RecordField("catTailLength", RecordFieldType.INT.getDataType(), false));
            catRecordFields.add(new RecordField("catName", RecordFieldType.STRING.getDataType(), false));
            final RecordSchema catRecordSchema = new org.apache.nifi.serialization.SimpleRecordSchema(catRecordFields);
            final Record catRecord = new org.apache.nifi.serialization.record.MapRecord(catRecordSchema, catMap);
            Assert.assertEquals(catRecord, values[14]);
        }
    }

    @Test
    public void testMultipleTypes() throws IOException, ParseException, SchemaNotFoundException, MalformedRecordException {
        final Schema schema = new Schema.Parser().parse(new File("src/test/resources/avro/multiple-types.avsc"));
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] serialized;
        final DatumWriter<GenericRecord> datumWriter = new org.apache.avro.generic.GenericDatumWriter(schema);
        try (final DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter(datumWriter);final DataFileWriter<GenericRecord> writer = dataFileWriter.create(schema, baos)) {
            // If a union field has multiple type options, a value should be mapped to the first compatible type.
            final GenericRecord r1 = new org.apache.avro.generic.GenericData.Record(schema);
            r1.put("field", 123);
            final GenericRecord r2 = new org.apache.avro.generic.GenericData.Record(schema);
            r2.put("field", Arrays.asList(1, 2, 3));
            final GenericRecord r3 = new org.apache.avro.generic.GenericData.Record(schema);
            r3.put("field", "not a number");
            writer.append(r1);
            writer.append(r2);
            writer.append(r3);
            writer.flush();
            serialized = baos.toByteArray();
        }
        try (final InputStream in = new ByteArrayInputStream(serialized)) {
            final AvroRecordReader reader = new AvroReaderWithEmbeddedSchema(in);
            final RecordSchema recordSchema = reader.getSchema();
            Assert.assertEquals(CHOICE, recordSchema.getDataType("field").get().getFieldType());
            Record record = reader.nextRecord();
            Assert.assertEquals(123, record.getValue("field"));
            record = reader.nextRecord();
            Assert.assertArrayEquals(new Object[]{ 1, 2, 3 }, ((Object[]) (record.getValue("field"))));
            record = reader.nextRecord();
            Assert.assertEquals("not a number", record.getValue("field"));
        }
    }

    public static enum Status {

        GOOD,
        BAD;}
}

